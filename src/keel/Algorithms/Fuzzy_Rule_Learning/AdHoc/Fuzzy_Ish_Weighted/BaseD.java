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

package keel.Algorithms.Fuzzy_Rule_Learning.AdHoc.Fuzzy_Ish_Weighted;

/**
 * <p>Title: BaseD</p>
 *
 * <p>Description: Contains the definition of the data base</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: KEEL</p>
 *
 * @author Written by Alberto Fernández (University of Granada) 28/10/2009
 * @version 1.0
 * @since JDK1.5
 */

import org.core.Fichero;

public class BaseD {
  int n_vars;
  int n_labels;
  Difuso[][] dataBase;
  String[] names;

  /**
   * Default constructor
   */
  public BaseD() {
  }

  /**
   * Constructor with parameters. It performs a homegeneous partition of the input space for
   * a given number of fuzzy labels.
   * @param n_variables int Number of input variables of the problem
   * @param n_labels int Number of fuzzy labels
   * @param ranges double[][] Range of each variable (minimum and maximum values)
   * @param names String[] Labels for the input attributes
   */
  public BaseD(int n_vars, int n_labels, double[][] ranges,
               String[] names) {
    this.n_vars = n_vars;
    this.n_labels = n_labels;
    dataBase = new Difuso[n_vars][n_labels];
    this.names = names.clone();

    double marca, valor;
    for (int i = 0; i < n_vars; i++) {
      marca = (ranges[i][1] - ranges[i][0]) / ( (float) n_labels - 1);
      for (int etq = 0; etq < n_labels; etq++) {
        valor = ranges[i][0] + marca * (etq - 1);
        dataBase[i][etq] = new Difuso();
        dataBase[i][etq].x0 = valor;
        valor = ranges[i][0] + marca * etq;
        dataBase[i][etq].x1 = valor;
        valor = ranges[i][0] + marca * (etq + 1);
        dataBase[i][etq].x3 = valor;
        dataBase[i][etq].y = 1;
        dataBase[i][etq].name = new String("L_" + etq);
        dataBase[i][etq].label = etq;
      }
    }
  }

  /**
   * It returns the number of input variables
   * @return int the number of input variables
   */
  public int numVariables() {
    return n_vars;
  }

  /**
   * It returns the number of fuzzy labels
   * @return int the number of fuzzy labels
   */
  public int numEtiquetas() {
    return n_labels;
  }

  /**
   * It computes the membership degree for a input value
   * @param i int the input variable id
   * @param j int the fuzzy label id
   * @param X double the input value
   * @return double the membership degree
   */
  public double membership(int i, int j, double X) {
    return dataBase[i][j].Fuzzifica(X);
  }

  /**
   * It makes a copy of a fuzzy label
   * @param i int the input variable id
   * @param j int the fuzzy label id
   * @return Fuzzy a copy of a fuzzy label
   */
  public Difuso copia(int i, int j) {
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
    for (int i = 0; i < n_vars; i++) {
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
   * It prints a single label of the Data Base into an string
   * @param var int the variable id
   * @param label int the label id
   * @return String the fuzzy label
   */
  public String print(int var, int label){
     return dataBase[var][label].name;
  }

  /**
   * It writes the Data Base into an output file
   * @param filename String the name of the output file
   */
  public void escribeFichero(String filename) {
    String cadenaSalida = new String("");
    cadenaSalida = printString();
    Fichero.escribeFichero(filename, cadenaSalida);
  }

}
