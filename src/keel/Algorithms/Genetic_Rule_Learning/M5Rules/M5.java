/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

package keel.Algorithms.Genetic_Rule_Learning.M5Rules;

public final class M5 {

    /** The root node */
    private M5TreeNode m_root[];

    /** No smoothing? */
    private boolean m_UseUnsmoothed = false;

    /** Pruning factor */
    private double m_PruningFactor = 2;

    /** Type of model */
    private int m_Model = M5TreeNode.MODEL_TREE;

    /** Verbosity */
    private int m_Verbosity = 0;

    public static final int MODEL_LINEAR_REGRESSION = M5TreeNode.LINEAR_REGRESSION;
    public static final int MODEL_REGRESSION_TREE = M5TreeNode.REGRESSION_TREE;
    public static final int MODEL_MODEL_TREE = M5TreeNode.MODEL_TREE;


    MyDataset trainDataset, valDataset, testDataset;

    /**
     * Constructor by parameters file.
     * @param paramFile the parsed parameters file
     * @throws java.lang.Exception if the class for the dataset is not numeric.
     */
    public M5(parseParameters paramFile) throws Exception{

      //File Names
      String trainFileName=paramFile.getTrainingInputFile();
      String valFileName=paramFile.getValidationInputFile();
      String testFileName=paramFile.getTestInputFile();

      //Options
      m_Model=MODEL_MODEL_TREE;
      m_PruningFactor=Double.parseDouble(paramFile.getParameter(0)); //pruning factor (a in (n+a)/(n-k))
      m_UseUnsmoothed=Boolean.valueOf(paramFile.getParameter(1)).booleanValue(); //whether the tree must be smoothed or not
      m_Verbosity = Integer.parseInt(paramFile.getParameter(2)); //verbosity level
      if (m_PruningFactor < 0 || m_PruningFactor > 10) {
        m_PruningFactor = 2;
        System.err.println("Error: Pruning Factor must be in the interval [0,10]");
        System.err.println("Using default value: 2");
      }
      if (m_Verbosity < 0 || m_Verbosity > 2) {
        m_Verbosity = 0;
        System.err.println("Error: Verbosity must be 0, 1 or 2");
        System.err.println("Using default value: 0");
      }

      /* Initializes the dataset. */
      trainDataset = new MyDataset( trainFileName, true  );
      valDataset = new MyDataset( valFileName, false  );
      testDataset = new MyDataset( testFileName, false  );

      if (trainDataset.getClassAttribute().isDiscret()) {
        throw new Exception("Class has to be numeric.");
      }

      // generate the tree
      buildClassifier( trainDataset );

    }

    public M5(MyDataset data,double prune_factor,boolean unsmoothed,int verbosity) throws Exception{

      //Options
      m_Model=MODEL_MODEL_TREE;
      m_PruningFactor=prune_factor; //pruning factor (a in (n+a)/(n-k))
      m_UseUnsmoothed=unsmoothed; //whether the tree must be smoothed or not
      m_Verbosity = verbosity; //verbosity level
      if (m_PruningFactor < 0 || m_PruningFactor > 10) {
        m_PruningFactor = 2;
        System.err.println("Error: Pruning Factor must be in the interval [0,10]");
        System.err.println("Using default value: 2");
      }
      if (m_Verbosity < 0 || m_Verbosity > 2) {
        m_Verbosity = 0;
        System.err.println("Error: Verbosity must be 0, 1 or 2");
        System.err.println("Using default value: 0");
      }

      if (data.getClassAttribute().isDiscret()) {
        throw new Exception("Class has to be numeric.");
      }

      // generate the tree
      buildClassifier( data );

    }

    /**
     * Construct a model tree by training itemsets
     *
     * @param inst training itemsets
     * @exception Exception if the classifier can't be built
     */
    public final void buildClassifier(MyDataset inst) throws Exception {

        inst=inst.discretToBinary();
        m_root = new M5TreeNode[2];
        double deviation = stdDev(inst.getClassIndex(), inst);

        m_root[0] = new M5TreeNode(inst, null,m_Model,m_PruningFactor,deviation); // build an empty tree
        m_root[0].split(inst); // build the unpruned initial tree
        m_root[0].numLeaves(0); // set tree leaves' number of the unpruned treee

        m_root[1] = m_root[0].copy(null); // make a copy of the unpruned tree
        m_root[1].prune(); // prune the tree

        if (!m_UseUnsmoothed) {
            m_root[1].smoothen(); // compute the smoothed linear models at the leaves
            m_root[1].numLeaves(0); // set tree leaves' number of the pruned tree
        }

    }

    /**
     * Gets the current settings of the Classifier.
     *
     * @return an array of strings suitable for passing to setOptions
     */
    public String[] getOptions() {

        String[] options = new String[7];
        int current = 0;

        switch (m_Model) {
        case MODEL_MODEL_TREE:
            options[current++] = "-O";
            options[current++] = "m";
            if (m_UseUnsmoothed) {
                options[current++] = "-U";
            }
            break;
        case MODEL_REGRESSION_TREE:
            options[current++] = "-O";
            options[current++] = "r";
            break;
        case MODEL_LINEAR_REGRESSION:
            options[current++] = "-O";
            options[current++] = "l";
            break;
        }
        options[current++] = "-F";
        options[current++] = "" + m_PruningFactor;
        options[current++] = "-V";
        options[current++] = "" + m_Verbosity;

        while (current < options.length) {
            options[current++] = "";
        }
        return options;
    }

    /**
     * Converts the output of the training process into a string
     *
     * @return the converted string
     */
    public final String toString() {

        try {
            StringBuffer text = new StringBuffer();
            double absDev = absDev(m_root[0].itemsets.getClassIndex(),m_root[0].itemsets);

            if (m_Verbosity >= 1 && m_Model != M5TreeNode.LINEAR_REGRESSION) {
                switch (m_root[0].model) {
                case M5TreeNode.LINEAR_REGRESSION:
                    break;
                case M5TreeNode.REGRESSION_TREE:
                    text.append("@Unpruned training regression tree:\n");
                    break;
                case M5TreeNode.MODEL_TREE:
                    text.append("@Unpruned training model tree:\n");
                    break;
                }
                if (m_root[0].type == false) {
                    text.append("\n");
                }

                text.append(m_root[0].treeToString(0, absDev) + "\n");
                text.append("@Models at the leaves:\n\n");

                //    the linear models at the leaves of the unpruned tree
                text.append(m_root[0].formulaeToString(false) + "\n"); ;
            }

            if (m_root[0].model != M5TreeNode.LINEAR_REGRESSION) {
                switch (m_root[0].model) {
                case M5TreeNode.LINEAR_REGRESSION:
                    break;
                case M5TreeNode.REGRESSION_TREE:
                    text.append("@Pruned training regression tree:\n");
                    break;
                case M5TreeNode.MODEL_TREE:
                    text.append("@Pruned training model tree:\n");
                    break;
                }
                if (m_root[1].type == false) {
                    text.append("\n");
                }
                text.append(m_root[1].treeToString(0, absDev) + "\n"); //the pruned tree
                text.append("@Models at the leaves:\n");
                if ((m_root[0].model != M5TreeNode.LINEAR_REGRESSION) && (m_UseUnsmoothed)) {
                    text.append("@Unsmoothed linear models at the leaves of the pruned tree (simple):\n");

                    //the unsmoothed linear models at the leaves of the pruned tree
                    text.append(m_root[1].formulaeToString(false) + "\n");
                }
                if ((m_root[0].model == M5TreeNode.MODEL_TREE) && (!m_UseUnsmoothed)) {
                    text.append("@Smoothed linear models at the leaves of the pruned tree (complex):\n");
                    text.append(m_root[1].formulaeToString(true) + "\n");
                    //the smoothed linear models at the leaves of the pruned tree
                }
            } else {
                text.append("@Training linear regression model:\n");
                text.append(m_root[1].unsmoothed.toString(m_root[1].itemsets,0) + "\n\n");
                // print the linear regression model
            }

            text.append("@Number of Rules: " + m_root[1].numberOfLinearModels());

            return text.toString();
        } catch (Exception e) {
            return "can't print m5' tree";
        }
    }

    /**
     * return the number of linear models
     * @return the number of linear models
     */
    public double measureNumLinearModels() {
        return m_root[1].numberOfLinearModels();
    }

    /**
     * return the number of leaves in the tree
     * @return the number leaves in the tree (same as # linear models &
     * # rules)
     */
    public double measureNumLeaves() {
        return measureNumLinearModels();
    }

    /**
     * return the number of rules
     * @return the number of rules (same as # linear models &
     * # leaves in the tree)
     */
    public double measureNumRules() {
        return measureNumLinearModels();
    }

    /**
     * Returns the value of the named measure
     * @param additionalMeasureName the name of the measure to query for its value
     * @return the value of the named measure
     * @exception IllegalArgumentException if the named measure is not supported
     */
    public double getMeasure(String additionalMeasureName) {
        if (additionalMeasureName.compareTo("measureNumRules") == 0) {
            return measureNumRules();
        } else if (additionalMeasureName.compareTo("measureNumLinearModels") ==
                   0) {
            return measureNumLinearModels();
        } else if (additionalMeasureName.compareTo("measureNumLeaves") == 0) {
            return measureNumLeaves();
        } else {
            throw new IllegalArgumentException(additionalMeasureName
                                               + " not supported (M5)");
        }
    }

    /**
     * Get the value of UseUnsmoothed.
     *
     * @return Value of UseUnsmoothed.
     */
    public boolean getUseUnsmoothed() {

        return m_UseUnsmoothed;
    }

    /**
     * Set the value of UseUnsmoothed.
     *
     * @param v  Value to assign to UseUnsmoothed.
     */
    public void setUseUnsmoothed(boolean v) {

        m_UseUnsmoothed = v;
    }

    /**
     * Get the value of PruningFactor.
     *
     * @return Value of PruningFactor.
     */
    public double getPruningFactor() {

        return m_PruningFactor;
    }

    /**
     * Set the value of PruningFactor.
     *
     * @param v  Value to assign to PruningFactor.
     */
    public void setPruningFactor(double v) {

        m_PruningFactor = v;
    }


    public M5TreeNode getTree(){
      //if (m_UseUnsmoothed)
      //  return m_root[0];
      //else
        return m_root[1];
    }

    /**
     * Get the value of Verbosity.
     *
     * @return Value of Verbosity.
     */
    public int getVerbosity() {

        return m_Verbosity;
    }

    /**
     * Set the value of Verbosity.
     *
     * @param v  Value to assign to Verbosity.
     */
    public void setVerbosity(int v) {

        m_Verbosity = v;
    }


    /**
     * Tests if enumerated attribute(s) exists in the itemsets
     * @param inst itemsets
     * @return true if there is at least one; false if none
     */
    public final static boolean hasEnumAttr(MyDataset inst) {
        int j;
        boolean b = false;

        for (j = 0; j < inst.numAttributes(); j++) {
            if (inst.getAttribute(j).isDiscret() == true) {
                b = true;
            }
        }

        return b;
    }

    /**
     * Tests if missing value(s) exists in the itemsets
     * @param inst itemsets
     * @return true if there is missing value(s); false if none
     */
    public final static boolean hasMissing(MyDataset inst) {
        int i, j;
        boolean b = false;

        for (i = 0; i < inst.numItemsets(); i++) {
            for (j = 0; j < inst.numAttributes(); j++) {
                if (inst.itemset(i).isMissing(j) == true) {
                    b = true;
                }
            }
        }

        return b;
    }

    /**
     * Returns the sum of the itemsets values of an attribute
     * @param attr an attribute
     * @param inst itemsets
     * @return the sum value
     */
    public final static double sum(int attr, MyDataset inst) {
        int i;
        double sum = 0.0;

        for (i = 0; i <= inst.numItemsets() - 1; i++) {
            sum += inst.itemset(i).getValue(attr);
        }

        return sum;
    }

    /**
     * Returns the squared sum of the itemsets values of an attribute
     * @param attr an attribute
     * @param inst itemsets
     * @return the squared sum value
     */
    public final static double sqrSum(int attr, MyDataset inst) {
        int i;
        double sqrSum = 0.0, value;

        for (i = 0; i <= inst.numItemsets() - 1; i++) {
            value = inst.itemset(i).getValue(attr);
            sqrSum += value * value;
        }

        return sqrSum;
    }

    /**
     * Returns the standard deviation value of the itemsets values of an attribute
     * @param attr an attribute
     * @param inst itemsets
     * @return the standard deviation value
     */
    public final static double stdDev(int attr, MyDataset inst) {
        int i, count = 0;
        double sd, va, sum = 0.0, sqrSum = 0.0, value;

        for (i = 0; i <= inst.numItemsets() - 1; i++) {
            count++;
            value = inst.itemset(i).getValue(attr);
            sum += value;
            sqrSum += value * value;
        }

        if (count > 1) {
            va = (sqrSum - sum * sum / count) / count;
            va = Math.abs(va);
            sd = Math.sqrt(va);
        } else {
            sd = 0.0;
        }

        return sd;
    }

    /**
     * Returns the absolute deviation value of the itemsets values of an attribute
     * @param attr an attribute
     * @param inst itemsets
     * @return the absolute deviation value
     */
    public final static double absDev(int attr, MyDataset inst) {

        int i;
        double average = 0.0, absdiff = 0.0, absDev;

        for (i = 0; i <= inst.numItemsets() - 1; i++) {
            average += inst.itemset(i).getValue(attr);
        }
        if (inst.numItemsets() > 1) {
            average /= (double) inst.numItemsets();
            for (i = 0; i <= inst.numItemsets() - 1; i++) {
                absdiff += Math.abs(inst.itemset(i).getValue(attr) - average);
            }
            absDev = absdiff / (double) inst.numItemsets();
        } else {
            absDev = 0.0;
        }

        return absDev;
    }

    /**
     * Returns the variance value of the itemsets values of an attribute
     * @param attr an attribute
     * @param inst itemsets
     * @return the variance value
     */
    public final static double variance(int attr, MyDataset inst) {
        int i, count = 0;
        double value, sum = 0.0, sqrSum = 0.0, va;

        for (i = 0; i <= inst.numItemsets() - 1; i++) {
            value = inst.itemset(i).getValue(attr);
            sum += value;
            sqrSum += value * value;
            count++;
        }
        if (count > 0) {
            va = (sqrSum - sum * sum / count) / count;
        } else {
            va = 0.0;
        }
        return va;
    }

    /**
     * Rounds a double
     * @param value the double value
     * @return the double rounded
     */
    public final static long roundDouble(double value) {

        long roundedValue;

        roundedValue = value > 0 ? (long) (value + 0.5) :
                       -(long) (Math.abs(value) + 0.5);

        return roundedValue;
    }

    /**
     * Returns the largest (closest to positive infinity) long integer value that is not greater than the argument.
     * @param value the double value
     * @return the floor integer
     */
    public final static long floorDouble(double value) {

        long floorValue;

        floorValue = value > 0 ? (long) value : -(long) (Math.abs(value) + 1);

        return floorValue;
    }

    /**
     * Rounds a double and converts it into a formatted right-justified String.
     * It is like %f format in C language.
     * @param value the double value
     * @param width the width of the string
     * @param afterDecimalPoint the number of digits after the decimal point
     * @return the double as a formatted string
     */

    public final static String doubleToStringF(double value, int width,
                                               int afterDecimalPoint) {

        StringBuffer stringBuffer;
        String resultString;
        double temp;
        int i, dotPosition;
        long precisionValue;

        if (afterDecimalPoint < 0) {
            afterDecimalPoint = 0;
        }
        precisionValue = 0;
        temp = value * Math.pow(10.0, afterDecimalPoint);
        if (Math.abs(temp) < Long.MAX_VALUE) {
            precisionValue = roundDouble(temp);

            if (precisionValue == 0) {
                resultString = String.valueOf(0);
                stringBuffer = new StringBuffer(resultString);
                stringBuffer.append(".");
                for (i = 1; i <= afterDecimalPoint; i++) {
                    stringBuffer.append("0");
                }
                resultString = stringBuffer.toString();
            } else {
                resultString = String.valueOf(precisionValue);
                stringBuffer = new StringBuffer(resultString);
                dotPosition = stringBuffer.length() - afterDecimalPoint;
                while (dotPosition < 0) {
                    stringBuffer.insert(0, 0);
                    dotPosition++;
                }
                stringBuffer.insert(dotPosition, ".");
                if (stringBuffer.charAt(0) == '.') {
                    stringBuffer.insert(0, 0);
                }
                resultString = stringBuffer.toString();
            }
        } else {
            resultString = new String("NaN"); ;
        }

        // Fill in space characters.
        stringBuffer = new StringBuffer(Math.max(width, resultString.length()));
        for (i = 0; i < stringBuffer.capacity() - resultString.length(); i++) {
            stringBuffer.append(' ');
        }
        stringBuffer.append(resultString);

        return stringBuffer.toString();
    }

    /**
     * Rounds a double and converts it into a formatted right-justified String. If the double is not equal to zero and not in the range [10e-3,10e7] it is returned in scientific format.
     * It is like %g format in C language.
     * @param value the double value
     * @param width the width of the string
     * @param precision the number of valid digits
     * @return the double as a formatted string
     */
    public final static String doubleToStringG(double value, int width,
                                               int precision) {

        StringBuffer stringBuffer;
        String resultString;
        double temp;
        int i, dotPosition, exponent = 0;
        long precisionValue;

        if (precision <= 0) {
            precision = 1;
        }
        precisionValue = 0;
        exponent = 0;
        if (value != 0.0) {
            exponent = (int) floorDouble(Math.log(Math.abs(value)) /
                                         Math.log(10));
            temp = value * Math.pow(10.0, precision - exponent - 1);
            precisionValue = roundDouble(temp); // then output value =  precisionValue * pow(10,exponent+1-precision)
            if (precision - 1 !=
                (int) (Math.log(Math.abs(precisionValue) + 0.5) / Math.log(10))) {
                exponent++;
                precisionValue = roundDouble(precisionValue / 10.0);
            }
        }

        if (precisionValue == 0) { // value = 0.0
            resultString = String.valueOf("0");
        } else {
            if (precisionValue >= 0) {
                dotPosition = 1;
            } else {
                dotPosition = 2;
            }
            if (exponent < -3 || precision - 1 + exponent > 7) { // Scientific format.
                resultString = String.valueOf(precisionValue);
                stringBuffer = new StringBuffer(resultString);
                stringBuffer.insert(dotPosition, ".");
                stringBuffer = deleteTrailingZerosAndDot(stringBuffer);
                stringBuffer.append("e").append(String.valueOf(exponent));
                resultString = stringBuffer.toString();
            } else { //
                resultString = String.valueOf(precisionValue);
                stringBuffer = new StringBuffer(resultString);
                for (i = 1; i <= -exponent; i++) {
                    stringBuffer.insert(dotPosition - 1, "0");
                }
                if (exponent <= -1) {
                    stringBuffer.insert(dotPosition, ".");
                } else if (exponent <= precision - 1) {
                    stringBuffer.insert(dotPosition + exponent, ".");
                } else {
                    for (i = 1; i <= exponent - (precision - 1); i++) {
                        stringBuffer.append("0");
                    }
                    stringBuffer.append(".");
                }

                // deleting trailing zeros and dot
                stringBuffer = deleteTrailingZerosAndDot(stringBuffer);
                resultString = stringBuffer.toString();
            }
        }
        // Fill in space characters.

        stringBuffer = new StringBuffer(Math.max(width, resultString.length()));
        for (i = 0; i < stringBuffer.capacity() - resultString.length(); i++) {
            stringBuffer.append(' ');
        }
        stringBuffer.append(resultString);

        return stringBuffer.toString();
    }

    /**
     * Deletes the trailing zeros and decimal point in a stringBuffer
     * @param stringBuffer string buffer
     * @return string buffer with deleted trailing zeros and decimal point
     */
    public final static StringBuffer deleteTrailingZerosAndDot(StringBuffer
            stringBuffer) {

        while (stringBuffer.charAt(stringBuffer.length() - 1) == '0' ||
               stringBuffer.charAt(stringBuffer.length() - 1) == '.') {
            if (stringBuffer.charAt(stringBuffer.length() - 1) == '0') {
                stringBuffer.setLength(stringBuffer.length() - 1);
            } else {
                stringBuffer.setLength(stringBuffer.length() - 1);
                break;
            }
        }
        return stringBuffer;
    }

    /**
     * Returns the smoothed values according to the smoothing formula (np+kq)/(n+k)
     * @param p a double, normally is the prediction of the model at the current node
     * @param q a double, normally is the prediction of the model at the up node
     * @param n the number of itemsets at the up node
     * @param k the smoothing constance, default =15
     * @return the smoothed value
     */
    public final static double smoothenValue(double p, double q, int n, int k) {
        return (n * p + k * q) / (double) (n + k);
    }

    /**
     * Returns the correlation coefficient of two double vectors
     * @param y1 double vector 1
     * @param y2 double vector 2
     * @param n the length of two double vectors
     * @return the correlation coefficient
     */
    public final static double correlation(double y1[], double y2[], int n) {

        int i;
        double av1 = 0.0, av2 = 0.0, y11 = 0.0, y22 = 0.0, y12 = 0.0, c;

        if (n <= 1) {
            return 1.0;
        }
        for (i = 0; i < n; i++) {
            av1 += y1[i];
            av2 += y2[i];
        }
        av1 /= (double) n;
        av2 /= (double) n;
        for (i = 0; i < n; i++) {
            y11 += (y1[i] - av1) * (y1[i] - av1);
            y22 += (y2[i] - av2) * (y2[i] - av2);
            y12 += (y1[i] - av1) * (y2[i] - av2);
        }
        if (y11 * y22 == 0.0) {
            c = 1.0;
        } else {
            c = y12 / Math.sqrt(Math.abs(y11 * y22));
        }

        return c;
    }

    /**
     * Tests if two double values are equal to each other
     * @param a double 1
     * @param b double 2
     * @return true if equal; false if not equal
     */
    public final static boolean eqDouble(double a, double b) {

        if (Math.abs(a) < 1e-10 && Math.abs(b) < 1e-10) {
            return true;
        }
        double c = Math.abs(a) + Math.abs(b);
        if (Math.abs(a - b) < c * 1e-10) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Prints error message and exits
     * @param err error message
     */
    public final static void errorMsg(String err) {
        System.out.print("Error: ");
        System.out.println(err);
        System.exit(1);
    }

}


