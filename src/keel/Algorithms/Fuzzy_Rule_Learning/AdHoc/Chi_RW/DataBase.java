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

import org.core.Files;

/**
 * <p>Title: DataBase</p>
 *
 * <p>Description: This class contains the representation of a Fuzzy Data Base</p>
 *
 * <p>Copyright: Copyright KEEL (c) 2007</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Alberto Fernández (University of Granada) 28/10/2007
 * @author Modified by Alberto Fernández (University of Granada) 12/11/2008
 * @version 1.1
 * @since JDK1.5
 */
public class DataBase {
    int n_variables;
    int n_labels;
    Fuzzy[][] dataBase;
    String[] names;

    /**
     * Default constructor
     */
    public DataBase() {
    }

    /**
     * Constructor with parameters. It performs a homegeneous partition of the input space for
     * a given number of fuzzy labels.
     * @param n_variables int Number of input variables of the problem
     * @param n_labels int Number of fuzzy labels
     * @param rangos double[][] Range of each variable (minimum and maximum values)
     * @param names String[] Labels for the input attributes
     */
    public DataBase(int n_variables, int n_labels, double[][] rangos, String[] names) {
        this.n_variables = n_variables;
        this.n_labels = n_labels;
        dataBase = new Fuzzy[n_variables][n_labels];
        this.names = names.clone();

        double marca;
        for (int i = 0; i < n_variables; i++) {
            marca = (rangos[i][1] - rangos[i][0]) / ((double) n_labels - 1);
            if (marca == 0) { //there are no ranges (an unique valor)
                for (int etq = 0; etq < n_labels; etq++) {
                    dataBase[i][etq] = new Fuzzy();
                    dataBase[i][etq].x0 = rangos[i][1] - 0.00000000000001;
                    dataBase[i][etq].x1 = rangos[i][1];
                    dataBase[i][etq].x3 = rangos[i][1] + 0.00000000000001;
                    dataBase[i][etq].y = 1;
                    dataBase[i][etq].name = new String("L_" + etq);
                    dataBase[i][etq].label = etq;
                }
            } else {
                for (int etq = 0; etq < n_labels; etq++) {
                    dataBase[i][etq] = new Fuzzy();
                    dataBase[i][etq].x0 = rangos[i][0] + marca * (etq - 1);
                    dataBase[i][etq].x1 = rangos[i][0] + marca * etq;
                    dataBase[i][etq].x3 = rangos[i][0] + marca * (etq + 1);
                    dataBase[i][etq].y = 1;
                    dataBase[i][etq].name = new String("L_" + etq);
                    dataBase[i][etq].label = etq;
                }
            }
        }
    }

    /**
     * It returns the number of input variables
     * @return int the number of input variables
     */
    public int numVariables() {
        return n_variables;
    }

    /**
     * It returns the number of fuzzy labels
     * @return int the number of fuzzy labels
     */
    public int numLabels() {
        return n_labels;
    }

    /**
     * It computes the membership degree for a input value
     * @param i int the input variable id
     * @param j int the fuzzy label id
     * @param X double the input value
     * @return double the membership degree
     */
    public double membershipFunction(int i, int j, double X) {
        return dataBase[i][j].Fuzzify(X);
    }

    /**
     * It makes a copy of a fuzzy label
     * @param i int the input variable id
     * @param j int the fuzzy label id
     * @return Fuzzy a copy of a fuzzy label
     */
    public Fuzzy clone(int i, int j) {
        return dataBase[i][j].clone();
    }

    /**
     * It prints the Data Base into an string
     * @return String the data base
     */
    public String printString() {
        String cadena = new String(
                "@Using Triangular Membership Functions as antecedent fuzzy sets\n");
        cadena += "@Number of Labels per variable: " + n_labels + "\n";
        for (int i = 0; i < n_variables; i++) {
            //cadena += "\nVariable " + (i + 1) + ":\n";
            cadena += "\n" + names[i] + ":\n";
            for (int j = 0; j < n_labels; j++) {
                cadena += " L_" + (j + 1) + ": (" + dataBase[i][j].x0 +
                        "," + dataBase[i][j].x1 + "," + dataBase[i][j].x3 +
                        ")\n";
            }
        }
        return cadena;
    }

    /**
     * It writes the Data Base into an output file
     * @param filename String the name of the output file
     */
    public void writeFile(String filename) {
        String outputString = new String("");
        outputString = printString();
        Files.writeFile(filename, outputString);
    }

}

