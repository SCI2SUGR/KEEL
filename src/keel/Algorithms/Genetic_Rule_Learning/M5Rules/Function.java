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

import keel.Dataset.Attributes;

/**
 * Class for handling a linear function.
 */

public final class Function{

    int terms[];
    double coeffs[];

    /**
     * Constructs a function of constant value
     */
    public Function() {
        terms = new int[1];
        terms[0] = 0;
        coeffs = new double[1];
        coeffs[0] = 0.0;
    }

    /**
     * Constucts a function with all attributes except the class in the inst
     * @param inst itemsets
     */
    public Function(MyDataset inst) {

        int i, count = 0;

        terms = new int[inst.numAttributes()];
        for (i = 0; i < inst.numAttributes() - 1; i++) {
            if (i != inst.getClassIndex()) {
                terms[++count] = i;
            }
        }
        terms[0] = count;

        coeffs = new double[count + 1];
    }

    /**
     * Constructs a function with one attribute
     * @param attr an attribute
     */
    public Function(int attr) {
        terms = new int[2];
        terms[0] = 1;
        terms[1] = attr;
        coeffs = new double[2];
        coeffs[0] = 0.0;
        coeffs[1] = 0.0;
    }

    /**
     * Makes a copy of a function
     * @return the copy of the function
     */
    public final Function copy() {

        Function fcopy = new Function();

        fcopy.terms = iVectorCopy(terms, terms[0] + 1);
        fcopy.coeffs = dVectorCopy(coeffs, terms[0] + 1);

        return fcopy;
    }

    public static final double[] dVectorCopy(double a[], int n) {

        int i;
        double b[];

        b = new double[n];
        for (i = 0; i < n; i++) {
            b[i] = a[i];
        }
        return b;
    }

    public final static int[] iVectorCopy(int a[], int n) {
        int i, b[];

        b = new int[n];
        for (i = 0; i < n; i++) {
            b[i] = a[i];
        }
        return b;
    }

    /**
     * Converts a function to a string
     * @param inst itemsets
     * @param startPoint the starting point on the screen; used to feed line before reaching beyond 80 characters
     * @return the converted string
     * @exception Exception if something goes wrong
     */
    public final String toString(MyDataset inst, int startPoint) throws
            Exception {

        int i, j, count1, count, precision = 3;
        String string;
        StringBuffer text = new StringBuffer();

        count1 = count = startPoint + inst.getClassAttribute().name().length() + 3;
        string = M5.doubleToStringG(coeffs[0], 1, precision);
        if (coeffs[0] >= 0.0) {
            count += string.length();
        } else {
            count += 1 + string.length();
        }
        text.append(inst.getClassAttribute().name() + " = " + string);
        for (i = 1; i <= terms[0]; i++) {
            string = M5.doubleToStringG(Math.abs(coeffs[i]), 1, precision);
            count += 3 + string.length() +
                    inst.getAttribute(terms[i]).name().length();
            if (count > 80) {
                text.append("\n");
                for (j = 1; j <= count1 - 1; j++) {
                    text.append(" ");
                }
                count = count1 - 1 + 3 + string.length() +
                        inst.getAttribute(terms[i]).name().length();
            }
            if (coeffs[i] >= 0.0) {
                text.append(" + ");
            } else {
                text.append(" - ");
            }
            text.append(string + inst.getAttribute(terms[i]).name());
        }

        return text.toString();
    }

    public final String toString() {

        int i, j,  precision = 3;
        String string;
        StringBuffer text = new StringBuffer();

        string = M5.doubleToStringG(coeffs[0], 1, precision);

        text.append(Attributes.getOutputAttribute(0).getName() + " = " + string);
        for (i = 1; i <= terms[0]; i++) {
            string = M5.doubleToStringG(Math.abs(coeffs[i]), 1, precision);

            if (coeffs[i] >= 0.0) {
                text.append(" + ");
            } else {
                text.append(" - ");
            }
            text.append(string + Attributes.getInputAttribute(terms[i]).getName());
        }

        return text.toString();
    }


    /**
     * Constructs a new function of which the variable list is a combination of those of two functions
     * @param f1 function 1
     * @param f2 function 2
     * @return the newly constructed function
     */
    public final static Function combine(Function f1, Function f2) {
        Function f = new Function();

        f.terms = iVectorCombine(f1.terms, f2.terms);
        f.coeffs = new double[f.terms[0] + 1];

        return f;
    }

    public final static int[] iVectorCombine(int[] list1, int[] list2) {
        int i, j, k, count;
        int[] list;

        list = new int[list1[0] + list2[0] + 1];
        count = 0;
        i = 1;
        j = 1;
        while (i <= list1[0] && j <= list2[0]) {
            if (list1[i] < list2[j]) {
                list[count + 1] = list1[i];
                count++;
                i++;
            } else if (list1[i] > list2[j]) {
                list[count + 1] = list2[j];
                count++;
                j++;
            } else {
                list[count + 1] = list1[i];
                count++;
                i++;
                j++;
            }
        }
        if (i > list1[0]) {
            for (k = j; k <= list2[0]; k++) {
                list[count + 1] = list2[k];
                count++;
            }
        }
        if (j > list2[0]) {
            for (k = i; k <= list1[0]; k++) {
                list[count + 1] = list1[k];
                count++;
            }
        }
        list[0] = count;

        return list;
    }

    /**
     * Evaluates a function
     * @param inst itemsets
     * @return the evaluation results
     * @exception Exception if something goes wrong
     */
    public final Results errors(MyDataset inst) throws Exception {
        int i;
        double tmp;
        Results e = new Results(0, inst.numItemsets() - 1);

        for (i = 0; i <= inst.numItemsets() - 1; i++) {
            tmp = this.predict(inst.itemset(i)) - inst.itemset(i).getClassValue();
            e.sumErr += tmp;
            e.sumAbsErr += Math.abs(tmp);
            e.sumSqrErr += tmp * tmp;
        }

        e.meanAbsErr = e.sumAbsErr / e.numItemsets;
        e.meanSqrErr = (e.sumSqrErr - e.sumErr * e.sumErr / e.numItemsets) /
                       e.numItemsets;
        e.meanSqrErr = Math.abs(e.meanSqrErr);
        e.rootMeanSqrErr = Math.sqrt(e.meanSqrErr);

        return e;
    }

    /**
     * Returns the predicted value of itemset i by a function
     * @param itemset itemset i
     * @return the predicted value
     */
    public final double predict(Itemset itemset) {
        int j;
        double y;

        y = coeffs[0];
        for (j = 1; j <= terms[0]; j++) {
            y += coeffs[j] * itemset.getValue(terms[j]);
        }
        return y;
    }

    /**
     * Detects the most insignificant variable in the funcion
     * @param sdy the standard deviation of the class variable
     * @param inst itemsets
     * @return the index of the most insignificant variable in the function
     */
    public final int insignificant(double sdy, MyDataset inst) {
        int j, jmin = -1, jmax = -1;
        double min = 2.0, max = 2.5, sdx, contribution;

        for (j = 1; j <= terms[0]; j++) {
            sdx = M5.stdDev(terms[j], inst);
            if (sdy == 0.0) {
                contribution = 0.0;
            } else {
                contribution = Math.abs(coeffs[j] * sdx / sdy);
            }
            if (contribution < min) {
                min = contribution;
                jmin = j;
            }
            if (contribution > max) {
                max = contribution;
                jmax = j;
            }
        }
        if (max > 2.5) {
            jmin = jmax;
        }

        return jmin;
    }

    /**
     * Removes a term from the function
     * @param j the j-th index in the variable list in the function
     * @return the new function with the term removed
     */
    public final Function remove(int j) {
        int i;
        Function f = new Function();

        f.terms = new int[terms[0]];
        f.terms[0] = terms[0] - 1;
        for (i = 1; i < j; i++) {
            f.terms[i] = terms[i];
        }
        for (i = j; i <= terms[0] - 1; i++) {
            f.terms[i] = terms[i + 1];
        }
        f.coeffs = new double[f.terms[0] + 1];

        return f;
    }


}

