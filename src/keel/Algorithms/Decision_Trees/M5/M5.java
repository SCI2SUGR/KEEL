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

/**
* <p>
* @author Written by Cristobal Romero (Universidad de Córdoba) 10/10/2007
* @version 0.1
* @since JDK 1.5
*</p>
*/

package keel.Algorithms.Decision_Trees.M5;

import java.io.*;
import java.util.*;

public final class M5 {

    /** The root node */
    private M5TreeNode m_root[];

    /** The options */
    private InformationHandler options;

    /** No smoothing? */
    private boolean m_UseUnsmoothed = false;

    /** Pruning factor */
    private double m_PruningFactor = 2;

    /** Type of model */
    private int m_Model = M5TreeNode.MODEL_TREE;

    /** Verbosity */
    private int m_Verbosity = 0;

    /** Filter for replacing missing values. */
    private ReplaceMissingValuesFilter m_ReplaceMissingValuesFilter;

    /** Filter for replacing nominal attributes with numeric binary ones. */
    private NominalToBinaryFilter m_NominalToBinaryFilter;

    public static final int MODEL_LINEAR_REGRESSION = M5TreeNode.
            LINEAR_REGRESSION;
    public static final int MODEL_REGRESSION_TREE = M5TreeNode.REGRESSION_TREE;
    public static final int MODEL_MODEL_TREE = M5TreeNode.MODEL_TREE;
    public static final Association[] TAGS_MODEL_TYPES = {
            new Association(MODEL_LINEAR_REGRESSION, "Simple linear regression"),
            new Association(MODEL_REGRESSION_TREE, "Regression tree"),
            new Association(MODEL_MODEL_TREE, "Model tree")
    };

    static String trainFileName, testFileName, testOutFileName,
            trainOutFileName, outputFileName;
    static String type, unsmoothed, pruningFactor, verbosity;

    static StringBuffer lista = new StringBuffer();

    /**
     * Name: initTokenizer
     * Sets configures the tokenizer that reads the input file in "Keel".
     *
     * @param tokenizer: the tokenizer we want to configure.
     */
    private static void initTokenizer(StreamTokenizer tokenizer) {
        tokenizer.resetSyntax();
        tokenizer.whitespaceChars(0, ' ');
        tokenizer.wordChars(' ' + 1, '\u00FF');
        tokenizer.whitespaceChars(',', ',');
        tokenizer.quoteChar('"');
        tokenizer.quoteChar('\'');
        tokenizer.ordinaryChar('=');
        tokenizer.ordinaryChar('{');
        tokenizer.ordinaryChar('}');
        tokenizer.ordinaryChar('[');
        tokenizer.ordinaryChar(']');
        tokenizer.eolIsSignificant(true);
    }

    /**
     * Name: getNextToken
     * Lets the tokenizer to take the next toke at the start of a new line.
     * If it is not possible, returns false. Else, return true.
     *
     * @param tokenizer: the tokenizer.
     *
     * Returns a boolean that is true if there is a next token to read
     */
    private static boolean getNextToken(StreamTokenizer tokenizer) {
        try {
            //if next token is end of file, returns false
            if (tokenizer.nextToken() == StreamTokenizer.TT_EOF) {
                return false;
            } else {
                tokenizer.pushBack();
                //looking for the end of line
                while (tokenizer.nextToken()
                       != StreamTokenizer.TT_EOL) {
                    ;
                }
                //looking for a new token
                while (tokenizer.nextToken()
                       == StreamTokenizer.TT_EOL) {
                    ;
                }
                //if there is no a new token, return false
                if (tokenizer.sval == null) {
                    return false;
                } else {
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());

            return false;
        }
    }

    /**
     * Name: setOptions
     * Sets the option of the execution of the algorithm, reading them from an input file.
     *
     * @param option: the tokenizer used to tokenize the input file.
     */
    protected static void setOptions(StreamTokenizer options) throws Exception {
        //read the next token
        options.nextToken();

        //the word read must be "algorithm"
        if (options.sval.equalsIgnoreCase("algorithm")) {
            options.nextToken();
            options.nextToken();

            //the name of the algorithm must be "gsp"
            if (!options.sval.equalsIgnoreCase("M5")) {
                throw new Exception("The name of the " +
                                    "algorithm is not correct.");
            }

            options.nextToken();
            options.nextToken();

            //read the data file name
            if (options.sval.equalsIgnoreCase("inputData")) {
                options.nextToken();
                options.nextToken();

                trainFileName = options.sval;
                options.nextToken();
                options.nextToken();
                testFileName = options.sval;
                getNextToken(options);
            } else {
                throw new Exception("The file must " +
                                    "start with the word inputData.");
            }

            //reads the output file name
            if (options.sval.equalsIgnoreCase("outputData")) {
                options.nextToken();
                options.nextToken();
                trainOutFileName = options.sval;
                options.nextToken();
                testOutFileName = options.sval;
                options.nextToken();
                outputFileName = options.sval;
                getNextToken(options);
            } else {
                throw new Exception("The file must start " +
                                    "with the word outputData.");
            }

            if (options.ttype == StreamTokenizer.TT_EOF) {
                return;
            }

            //read the parameter values
            for (int k = 0; k < 4; k++) {
                //reading the minimum support
                if (options.sval.equalsIgnoreCase(
                        "type")) {
                    options.nextToken();
                    options.nextToken();

                    type = options.sval;

                    if (!getNextToken(options)) {
                        return;
                    } else {
                        continue;
                    }
                }

                //reading the maximum number of gaps
                if (options.sval.equalsIgnoreCase("pruningFactor")) {
                    options.nextToken();
                    options.nextToken();

                    pruningFactor = options.sval;

                    if (!getNextToken(options)) {
                        return;
                    } else {
                        continue;
                    }
                }

                if (options.sval.equalsIgnoreCase("unsmoothed")) {
                    options.nextToken();
                    options.nextToken();

                    unsmoothed = options.sval;

                    if (!getNextToken(options)) {
                        return;
                    } else {
                        continue;
                    }
                }

                if (options.sval.equalsIgnoreCase("verbosity")) {
                    options.nextToken();
                    options.nextToken();

                    verbosity = options.sval;

                    if (!getNextToken(options)) {
                        return;
                    } else {
                        continue;
                    }
                }
            }
        } else {
            throw new Exception("The file must start with " +
                                "the word \"algorithm=\" followed by the " +
                                "name of the algorithm.");
        }
    }


    public static String getHeader(String fileName) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new
                    FileInputStream(fileName)));
            StringBuffer sb = new StringBuffer();
            String line = "", aux = "";

            line = br.readLine();
            if (line.length() >= 5) {
                aux = line.substring(0, 5);
            }

            while (!aux.equalsIgnoreCase("@data")) {
                if (!line.startsWith("%") && (line.length() > 1)) {
                    sb.append(line + "\n");
                }
                line = br.readLine();
                if (line.length() >= 5) {
                    aux = line.substring(0, 5);
                }
            }

            sb.append("@data\n");
            br.close();

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getHeaderNoData(String fileName) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new
                    FileInputStream(fileName)));
            StringBuffer sb = new StringBuffer();
            String line = "", aux = "";

            line = br.readLine();
            if (line.length() >= 5) {
                aux = line.substring(0, 5);
            }

            while (!aux.equalsIgnoreCase("@data")) {
                sb.append(line + "\n");
                line = br.readLine();
                if (line.length() >= 5) {
                    aux = line.substring(0, 5);
                }
            }

            br.close();

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Construct a model tree by training instances
     *
     * @param inst training instances
     * @exception Exception if the classifier can't be built
     */
    public final void buildClassifier(M5Instances inst) throws Exception {

        if (inst.checkForStringAttributes()) {
            throw new Exception("Can't handle string attributes!");
        }
        options = new InformationHandler(inst);

        options.model = m_Model;
        options.smooth = !m_UseUnsmoothed;
        options.pruningFactor = m_PruningFactor;
        options.verbosity = m_Verbosity;

        if (!inst.classAttribute().isNumeric()) {
            throw new Exception("Class has to be numeric.");
        }

        inst = new M5Instances(inst);
        inst.deleteWithMissingClass();
        m_ReplaceMissingValuesFilter = new ReplaceMissingValuesFilter();
        m_ReplaceMissingValuesFilter.setInputFormat(inst);
        inst = ReplaceMissingValuesFilter.useFilter(inst,
                m_ReplaceMissingValuesFilter);
        m_NominalToBinaryFilter = new NominalToBinaryFilter();
        m_NominalToBinaryFilter.setInputFormat(inst);
        inst = NominalToBinaryFilter.useFilter(inst, m_NominalToBinaryFilter);

        m_root = new M5TreeNode[2];
        options.deviation = stdDev(inst.classIndex(), inst);

        m_root[0] = new M5TreeNode(inst, null, options); // build an empty tree
        m_root[0].split(inst); // build the unpruned initial tree
        m_root[0].numLeaves(0); // set tree leaves' number of the unpruned treee

        m_root[1] = m_root[0].copy(null); // make a copy of the unpruned tree
        m_root[1].prune(); // prune the tree

        if (options.model != M5TreeNode.LINEAR_REGRESSION) {
            m_root[1].smoothen(); // compute the smoothed linear models at the leaves
            m_root[1].numLeaves(0); // set tree leaves' number of the pruned tree
        }
    }

    /**
     * Classifies the given test instance.
     *
     * @param ins the instance to be classified
     * @return the predicted class for the instance
     * @exception Exception if the instance can't be classified
     */
    public double classifyInstance(M5Instance ins) throws Exception {

        m_ReplaceMissingValuesFilter.input(ins);
        m_ReplaceMissingValuesFilter.batchFinished();
        ins = m_ReplaceMissingValuesFilter.output();

        m_NominalToBinaryFilter.input(ins);
        m_NominalToBinaryFilter.batchFinished();
        ins = m_NominalToBinaryFilter.output();

        double prueba = m_root[1].predict(ins, !m_UseUnsmoothed);
        //System.out.println(prueba);
        //lista.append(""+prueba+"\n");
        return prueba;
    }

    /**
     * @return an enumeration of all the available options
     */
    public Enumeration listOptions() {

        Vector newVector = new Vector(4);

        newVector.addElement(new Information("\tType of model to be used.\n" +
                                             "\tl: linear regression\n" +
                                             "\tr: regression tree\n" +
                                             "\tm: model tree\n" +
                                             "\t(default: m)",
                                             "-O", 1, "-O <l|r|m>"));
        newVector.addElement(new Information("\tUse unsmoothed tree.", "C", 0,
                                             "-U"));
        newVector.addElement(new Information("\tPruning factor (default: 2).",
                                             "-F", 1, "-F <double>"));
        newVector.addElement(new Information("\tVerbosity (default: 0).",
                                             "-V", 1, "-V <0|1|2>"));

        return newVector.elements();
    }

    /**
     * Parses a given list of options.
     *
     * @param options the list of options as an array of strings
     * @exception Exception if an option is not supported
     */
    public void setOptions(String[] options) throws Exception {

        String modelString = M5StaticUtils.getOption('O', options);
        if (modelString.length() != 0) {
            if (modelString.equals("l")) {
                setModelType(new SelectedAssociation(MODEL_LINEAR_REGRESSION,
                        TAGS_MODEL_TYPES));
            } else if (modelString.equals("r")) {
                setModelType(new SelectedAssociation(MODEL_REGRESSION_TREE,
                        TAGS_MODEL_TYPES));
            } else if (modelString.equals("m")) {
                setModelType(new SelectedAssociation(MODEL_MODEL_TREE,
                        TAGS_MODEL_TYPES));
            } else {
                throw new Exception("Don't know model type " + modelString);
            }
        } else {
            setModelType(new SelectedAssociation(MODEL_MODEL_TREE,
                                                 TAGS_MODEL_TYPES));
        }

        setUseUnsmoothed(M5StaticUtils.getFlag('U', options));
        if (m_Model != M5TreeNode.MODEL_TREE) {
            setUseUnsmoothed(true);
        }

        String pruningString = M5StaticUtils.getOption('F', options);
        if (pruningString.length() != 0) {
            setPruningFactor((new Double(pruningString)).doubleValue());
        } else {
            setPruningFactor(2);
        }

        String verbosityString = M5StaticUtils.getOption('V', options);
        if (verbosityString.length() != 0) {
            setVerbosity(Integer.parseInt(verbosityString));
        } else {
            setVerbosity(0);
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
            double absDev = absDev(m_root[0].instances.classIndex(),
                                   m_root[0].instances);

            if (options.verbosity >= 1 &&
                options.model != M5TreeNode.LINEAR_REGRESSION) {
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
                if ((m_root[0].model != M5TreeNode.LINEAR_REGRESSION) &&
                    (m_UseUnsmoothed)) {
                    text.append(
                            "@Unsmoothed linear models at the leaves of the pruned tree (simple):\n");

                    //     the unsmoothed linear models at the leaves of the pruned tree
                    text.append(m_root[1].formulaeToString(false) + "\n");
                }
                if ((m_root[0].model == M5TreeNode.MODEL_TREE) &&
                    (!m_UseUnsmoothed)) {
                    text.append(
                            "@Smoothed linear models at the leaves of the pruned tree (complex):\n");
                    text.append(m_root[1].formulaeToString(true) + "\n");
                    //   the smoothed linear models at the leaves of the pruned tree
                }
            } else {
                text.append("@Training linear regression model:\n");
                text.append(m_root[1].unsmoothed.toString(m_root[1].instances,
                        0) + "\n\n");
                //       print the linear regression model
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
     * Returns an enumeration of the additional measure names
     * @return an enumeration of the measure names
     */
    public Enumeration enumerateMeasures() {
        Vector newVector = new Vector(3);
        newVector.addElement("measureNumLinearModels");
        newVector.addElement("measureNumLeaves");
        newVector.addElement("measureNumRules");
        return newVector.elements();
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

    /**
     * Get the value of Model.
     *
     * @return Value of Model.
     */
    public SelectedAssociation getModelType() {

        return new SelectedAssociation(m_Model, TAGS_MODEL_TYPES);
    }

    /**
     * Set the value of Model.
     *
     * @param newMethod  Value to assign to Model.
     */
    public void setModelType(SelectedAssociation newMethod) {

        if (newMethod.getTags() == TAGS_MODEL_TYPES) {
            m_Model = newMethod.getSelectedTag().getID();
        }
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

    public static M5 forName(String classifierName, String[] options) throws
            Exception {

        return (M5) M5StaticUtils.forName(M5.class,
                                          classifierName,
                                          options);
    }


    /**
     * Tests if enumerated attribute(s) exists in the instances
     * @param inst instances
     * @return true if there is at least one; false if none
     */
    public final static boolean hasEnumAttr(M5Instances inst) {
        int j;
        boolean b = false;

        for (j = 0; j < inst.numAttributes(); j++) {
            if (inst.attribute(j).isNominal() == true) {
                b = true;
            }
        }

        return b;
    }

    /**
     * Tests if missing value(s) exists in the instances
     * @param inst instances
     * @return true if there is missing value(s); false if none
     */
    public final static boolean hasMissing(M5Instances inst) {
        int i, j;
        boolean b = false;

        for (i = 0; i < inst.numInstances(); i++) {
            for (j = 0; j < inst.numAttributes(); j++) {
                if (inst.instance(i).isMissing(j) == true) {
                    b = true;
                }
            }
        }

        return b;
    }

    /**
     * Returns the sum of the instances values of an attribute
     * @param attr an attribute
     * @param inst instances
     * @return the sum value
     */
    public final static double sum(int attr, M5Instances inst) {
        int i;
        double sum = 0.0;

        for (i = 0; i <= inst.numInstances() - 1; i++) {
            sum += inst.instance(i).value(attr);
        }

        return sum;
    }

    /**
     * Returns the squared sum of the instances values of an attribute
     * @param attr an attribute
     * @param inst instances
     * @return the squared sum value
     */
    public final static double sqrSum(int attr, M5Instances inst) {
        int i;
        double sqrSum = 0.0, value;

        for (i = 0; i <= inst.numInstances() - 1; i++) {
            value = inst.instance(i).value(attr);
            sqrSum += value * value;
        }

        return sqrSum;
    }

    /**
     * Returns the standard deviation value of the instances values of an attribute
     * @param attr an attribute
     * @param inst instances
     * @return the standard deviation value
     */
    public final static double stdDev(int attr, M5Instances inst) {
        int i, count = 0;
        double sd, va, sum = 0.0, sqrSum = 0.0, value;

        for (i = 0; i <= inst.numInstances() - 1; i++) {
            count++;
            value = inst.instance(i).value(attr);
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
     * Returns the absolute deviation value of the instances values of an attribute
     * @param attr an attribute
     * @param inst instances
     * @return the absolute deviation value
     */
    public final static double absDev(int attr, M5Instances inst) {

        int i;
        double average = 0.0, absdiff = 0.0, absDev;

        for (i = 0; i <= inst.numInstances() - 1; i++) {
            average += inst.instance(i).value(attr);
        }
        if (inst.numInstances() > 1) {
            average /= (double) inst.numInstances();
            for (i = 0; i <= inst.numInstances() - 1; i++) {
                absdiff += Math.abs(inst.instance(i).value(attr) - average);
            }
            absDev = absdiff / (double) inst.numInstances();
        } else {
            absDev = 0.0;
        }

        return absDev;
    }

    /**
     * Returns the variance value of the instances values of an attribute
     * @param attr an attribute
     * @param inst instances
     * @return the variance value
     */
    public final static double variance(int attr, M5Instances inst) {
        int i, count = 0;
        double value, sum = 0.0, sqrSum = 0.0, va;

        for (i = 0; i <= inst.numInstances() - 1; i++) {
            value = inst.instance(i).value(attr);
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
     * return string buffer with deleted trailing zeros and decimal point
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
     * @param n the number of instances at the up node
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

    /**
     * Prints sepearating line
     */
    public final static String separatorToString() {
        return "--------------------------------------------------------------------------------\n";
    }

    /**
     * Prints the head lines of the output
     */
    public final static String headToString() {

        StringBuffer text = new StringBuffer();

        text.append("M5Java version " + InformationHandler.VERSION + "\n");
        return text.toString();
    }


    /**
     * Main method for M5' algorithm
     *
     * @param argv command line arguments
     */
    public static void main(String[] argv) {

        try {
            StreamTokenizer tokenizer =
                    new StreamTokenizer(
                            new BufferedReader(new FileReader(argv[0])));
            initTokenizer(tokenizer);
            setOptions(tokenizer);

            String[] opt;
            String strOpt = "-t " + trainFileName + " -T " + testFileName +
                            " -O " + type + " -F " + pruningFactor + " -V " +
                            verbosity;

            //copyHeaders();

            if (unsmoothed.equalsIgnoreCase("true")) {
                strOpt += " -U";
            }

            opt = strOpt.split(" ");

            String strOut = EvaluateModel.evaluateModel(new M5(), opt);
            //System.out.println(strOut);

            PrintWriter pw = new PrintWriter(new FileOutputStream(
                    outputFileName));

            pw.print(strOut);

            pw.flush();
            pw.close();

            /*PrintWriter listando = new PrintWriter(new FileOutputStream("probando.txt"));
                  listando.print(lista.toString());
                  listando.close();*/

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}


