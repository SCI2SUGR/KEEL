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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierFuzzySGERD;

/**
 * <p>Title: DataBase</p>
 *
 * <p>Description: Fuzzy Data Base</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Alberto Fernández (University of Granada) 29/10/2007
 * @author Modified by Jesus Alcalá (University of Granada) 21/05/2009
 * @version 1.2
 * @since JDK1.5
 */

import org.core.Files;

public class DataBase {
    int n_variables, partitions, nLabels;
    Fuzzy[][][] dataBase;
    String names[];

    /**
     * Default constructor
     */
    public DataBase() {
    }


    /**
     * Constructor with parameters. It performs a homegeneous partition of the input space for
     * a each number of partitions
     * @param partitions int Number of fuzzy partitions
     * @param n_variables int Number of input variables of the problem
     * @param ranges double[][] ranges of each variable (minimum and maximum values)
     * @param names String[] Labels for the input attributes
     */
    public DataBase(int partitions, int n_variables, double[][] ranges, String[] names) {
        double mark, value, range;

        this.n_variables = n_variables;
        this.names = names.clone();
        this.nLabels = ((partitions * (partitions + 1)) / 2) - 1;
        this.partitions = partitions - 1;
        this.dataBase = new Fuzzy[this.partitions][][];

        for (int j = 0; j < this.partitions; j++) {
            this.dataBase[j] = new Fuzzy[n_variables][];
            for (int i = 0; i < n_variables; i++) {
                this.dataBase[j][i] = new Fuzzy[2 + j];
                range = Math.abs(ranges[i][1] - ranges[i][0]);
                mark = range / ((double) 1 + j);
                for (int label = 0; label < 2 + j; label++) {
                    value = ranges[i][0] + mark * (label - 1);
                    this.dataBase[j][i][label] = new Fuzzy();
                    this.dataBase[j][i][label].x0 = value;
                    value = ranges[i][0] + mark * label;
                    this.dataBase[j][i][label].x1 = value;
                    value = ranges[i][0] + mark * (label + 1);
                    this.dataBase[j][i][label].x3 = value;
                    this.dataBase[j][i][label].y = 1;
                    this.dataBase[j][i][label].name = new String("L_" + label + "(" + (j + 2) + ")");
                    this.dataBase[j][i][label].label = (int) ((1.5 * j) + (0.5 * j * j) + label);

                    if ((this.dataBase[j][i][label].x0 < ranges[i][0]) || (this.dataBase[j][i][label].x3 > ranges[i][1])) {
                        this.dataBase[j][i][label].covering = mark / range;
                    } else {
                        this.dataBase[j][i][label].covering = (2.0 * mark) / range;
                    }
                }
            }
        }
    }

    /**
     * It returns the number of variables of the problem
     * @return int the number of variables of the problem
     */
    public int numVariables() {
        return n_variables;
    }

    /**
     * It returns the number of fuzzy labels
     * @return int the number of fuzzy labels
     */
    public int numLabels() {
        return nLabels;
    }

    /**
     * It computes the membership degree for a input value
     * @param i int the input variable id
     * @param j int the fuzzy label id
     * @param k int the layer of the hierarchical DB
     * @param X double the input value
     * @return double the membership degree
     */
    public double membership(int i, int j, int k, double X) {
        if (i < 0 || i > 13) {
            return (1); // Don't care
        } else {
            return (this.dataBase[i][j][k].Fuzzify(X));
        }
    }

    /**
     * It translates the antecedent id to a valid partition
     * @param ant int the antecedent id
     * @return int the corresponding partition
     */
    public int partition(int ant) {
        if (ant < 0 || ant > 13) {
            return ( -1);
        } 
		else if (ant < 2) {
            return (0);
        } 
		else if (ant < 5) {
            return (1);
        } 
		else if (ant < 9) {
            return (2);
        } 
		else {
            return (3);
        }
    }

    /**
     * It translates the antecedent id to a valid fuzzy label
     * @param ant int the antecedent id
     * @return int the corresponding fuzzy label
     */
    public int label(int ant) {
        if (ant < 0 || ant > 13) {
            return ( -1);
        } else if (ant < 2) {
            return (ant);
        } else if (ant < 5) {
            return (ant - 2);
        } else if (ant < 9) {
            return (ant - 5);
        } else {
            return (ant - 9);
        }
    }

    /**
     * It returns the coverage of an specific fuzzy partition
     * @param partition int the partition
     * @param variable int the variable id
     * @param label int the fuzzy label
     * @return double the coverage of an specific fuzzy partition
     */
    public double covering(int partition, int variable, int label) {
        return (this.dataBase[partition][variable][label].covering);
    }


    /**
     * It makes a copy of a fuzzy label
     * @param i int the input variable id
     * @param j int the fuzzy label id
     * @param k int the layer of the hierarchical DB
     * @return Fuzzy a copy of a fuzzy label
     */
    public Fuzzy copy(int i, int j, int k) {
        return this.dataBase[i][j][k].clone();
    }

    /**
     * It prints the points of a fuzzy label
     * @param var int variable id
     * @param label int label id
     * @return String the points of a fuzzy label
     */
    public String print_triangle(int var, int label) {
        String cadena = new String("");
        int k;
        if (label <= 1) {
            k = 0;
        } else if (label <= 4) {
            k = 1;
            label -= 2;
        } else if (label <= 8) {
            k = 2;
            label -= 5;
        } else {
            k = 3;
            label -= 9;
        }

        Fuzzy d = this.dataBase[k][var][label];

        cadena = d.name + ": \t" + d.x0 + "\t" + d.x1 + "\t" + d.x3 + "\n";
        return cadena;
    }

    /**
     * It prints the name of a fuzzy label
     * @param var int variable id
     * @param label int label id
     * @return String the name of a fuzzy label
     */
    public String print(int var, int label) {
        String cadena = new String("");
        int k;
        if (label <= 1) {
            k = 0;
        } else if (label <= 4) {
            k = 1;
            label -= 2;
        } else if (label <= 8) {
            k = 2;
            label -= 5;
        } else {
            k = 3;
            label -= 9;
        }

        return this.dataBase[k][var][label].name;
    }

    /**
     * It prints the Data Base into an string
     * @return String the data base
     */
    public String printString() {
        String string = new String(
                "@Using Triangular Membership Functions as antecedent fuzzy sets");
        for (int k = 0; k < this.partitions; k++) {
            string += "\n\n@Number of Labels per variable: " + (k + 2) + "\n";
            for (int i = 0; i < this.n_variables; i++) {
                string += "\n" + names[i] + ":\n";
                for (int j = 0; j < 2 + k; j++) {
                    string += dataBase[k][i][j].name + ": (" +
                            dataBase[k][i][j].x0 + "," + dataBase[k][i][j].x1 +
                            "," + dataBase[k][i][j].x3 + ")\n";
                }
            }
        }
        return string;

    }

    /**
     * It writes the Data Base into an output file
     * @param filename String the name of the output file
     */
    public void saveFile(String filename) {
        String stringOut = new String("");
        stringOut = printString();
        Files.writeFile(filename, stringOut);
    }

}

