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

package keel.Algorithms.Fuzzy_Rule_Learning.AdHoc.Chi_RW;

import java.util.ArrayList;
import org.core.Files;

/**
 * <p>Title: RuleBase</p>
 *
 * <p>Description: This class contains the representation of a Rule Set</p>
 *
 * <p>Copyright: Copyright KEEL (c) 2007</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Alberto Fernández (University of Granada) 29/10/2007
 * @version 1.0
 * @since JDK1.5
 */
public class RuleBase {

    ArrayList<Rule> ruleBase;
    DataBase dataBase;
    int n_variables, n_labels, ruleWeight, inferenceType, compatibilityType;
    String[] names, classes;

    /**
     * Rule Base Constructor
     * @param dataBase DataBase the Data Base containing the fuzzy partitions
     * @param inferenceType int the inference type for the FRM
     * @param compatibilityType int the compatibility type for the t-norm
     * @param ruleWeight int the rule weight heuristic
     * @param names String[] the names for the features of the problem
     * @param classes String[] the labels for the class attributes
     */
    public RuleBase(DataBase dataBase, int inferenceType, int compatibilityType,
                    int ruleWeight, String[] names, String[] classes) {
        ruleBase = new ArrayList<Rule>();
        this.dataBase = dataBase;
        n_variables = dataBase.numVariables();
        n_labels = dataBase.numLabels();
        this.inferenceType = inferenceType;
        this.compatibilityType = compatibilityType;
        this.ruleWeight = ruleWeight;
        this.names = names.clone();
        this.classes = classes.clone();

    }

    /**
     * It checks if a specific rule is already in the rule base
     * @param r Rule the rule for comparison
     * @return boolean true if the rule is already in the rule base, false in other case
     */
    private boolean duplicated(Rule r) {
        int i = 0;
        boolean found = false;
        while ((i < ruleBase.size()) && (!found)) {
            found = ruleBase.get(i).comparison(r);
            i++;
        }
        return found;
    }

    /**
     * Rule Learning Mechanism for the Chi et al.'s method
     * @param train myDataset the training data-set
     */
    public void Generation(myDataset train) {
        for (int i = 0; i < train.size(); i++) {
            Rule r = searchForBestAntecedent(train.getExample(i),
                                            train.getOutputAsInteger(i));
            r.assingConsequent(train, ruleWeight);
            if ((!duplicated(r)) &&
                (r.weight > 0)) {
                ruleBase.add(r);
            }
        }
    }

    /**
     * This function obtains the best fuzzy label for each variable of the example and assigns
     * it to the rule
     * @param example double[] the input example
     * @param clas int the class of the input example
     * @return Rule the fuzzy rule with the highest membership degree with the example
     */
    private Rule searchForBestAntecedent(double[] example, int clas) {
        Rule r = new Rule(n_variables, this.compatibilityType);
        r.setClass(clas);
        for (int i = 0; i < n_variables; i++) {
            double max = 0.0;
            int etq = -1;
            double per;
            for (int j = 0; j < n_labels; j++) {
                per = dataBase.membershipFunction(i, j, example[i]);
                if (per > max) {
                    max = per;
                    etq = j;
                }
            }
            if (max == 0.0) {
                System.err.println(
                        "There was an Error while searching for the antecedent of the rule");
                System.err.println("Example: ");
                for (int j = 0; j < n_variables; j++) {
                    System.err.print(example[j] + "\t");
                }
                System.err.println("Variable " + i);
                System.exit(1);
            }
            r.antecedent[i] = dataBase.clone(i, etq);
        }
        return r;
    }

    /**
     * It prints the rule base into an string
     * @return String an string containing the rule base
     */
    public String printString() {
        int i, j;
        String cadena = "";

        cadena += "@Number of rules: " + ruleBase.size() + "\n\n";
        for (i = 0; i < ruleBase.size(); i++) {
            Rule r = ruleBase.get(i);
            cadena += (i + 1) + ": ";
            for (j = 0; j < n_variables - 1; j++) {
                cadena += names[j] + " IS " + r.antecedent[j].name + " AND ";
            }
            cadena += names[j] + " IS " + r.antecedent[j].name + ": " +
                    classes[r.clas] + " with Rule Weight: " + r.weight + "\n";
        }

        return (cadena);
    }

    /**
     * It writes the rule base into an ouput file
     * @param filename String the name of the output file
     */
    public void writeFile(String filename) {
        String outputString = new String("");
        outputString = printString();
        Files.writeFile(filename, outputString);
    }

    /**
     * Fuzzy Reasoning Method
     * @param example double[] the input example
     * @return int the predicted class label (id)
     */
    public int FRM(double[] example) {
        if (this.inferenceType == Fuzzy_Chi.WINNING_RULE) {
            return FRM_WR(example);
        } else {
            return FRM_AC(example);
        }
    }

    /**
     * Winning Rule FRM
     * @param example double[] the input example
     * @return int the class label for the rule with highest membership degree to the example
     */
    private int FRM_WR(double[] example) {
        int clas = -1;
        double max = 0.0;
        for (int i = 0; i < ruleBase.size(); i++) {
            Rule r = ruleBase.get(i);
            double produc = r.compatibility(example);
            produc *= r.weight;
            if (produc > max) {
                max = produc;
                clas = r.clas;
            }
        }
        return clas;
    }

    /**
     * Additive Combination FRM
     * @param example double[] the input example
     * @return int the class label for the set of rules with the highest sum of membership degree per class
     */
    private int FRM_AC(double[] example) {
        int clas = -1;
        double[] class_degrees = new double[1];
        for (int i = 0; i < ruleBase.size(); i++) {
            Rule r = ruleBase.get(i);

            double produc = r.compatibility(example);
            produc *= r.weight;
            if (r.clas > class_degrees.length - 1) {
                double[] aux = new double[class_degrees.length];
                for (int j = 0; j < aux.length; j++) {
                    aux[j] = class_degrees[j];
                }
                class_degrees = new double[r.clas + 1];
                for (int j = 0; j < aux.length; j++) {
                    class_degrees[j] = aux[j];
                }
            }
            class_degrees[r.clas] += produc;
        }
        double max = 0.0;
        for (int l = 0; l < class_degrees.length; l++) {
            if (class_degrees[l] > max) {
                max = class_degrees[l];
                clas = l;
            }
        }
        return clas;
    }

}

