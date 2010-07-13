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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Fuzzy_Ish_Hybrid;

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
 * @author Modified by Alberto Fernández (University of Granada) 03/09/2009
 * @version 1.2
 * @since JDK1.5
 */

import org.core.Files;

public class DataBase {
  int n_variables;
  Fuzzy[][][] dataBase;
  String varNames[];
  boolean nominals[];

  /**
   * Default constructor
   */
  public DataBase() {
  }

  /**
   * Constructor with parameters. It performs a homegeneous partition of the input space for
   * a each number of fuzzy labels: 2, 3, 4 and 5 labels per variable.
   * @param n_variables int Number of input variables of the problem
   * @param ranges double[][] Range of each variable (minimum and maximum values)
   * @param varNames String[] Labels for the input attributes
   * @param nominals boolean[] a boolean string to know if each attribute is nominal or not
   */
  public DataBase(int n_variables, double[][] ranges, String[] varNames,
                  boolean[] nominals) {
    this.n_variables = n_variables;
    dataBase = new Fuzzy[5][][];
    this.varNames = varNames.clone();
    this.nominals = nominals.clone();

    double marca, valor;
    for (int j = 0; j < 4; j++) {
      dataBase[j] = new Fuzzy[n_variables][];
      for (int i = 0; i < n_variables; i++) {
        //Nuevo para nominales:
        if (nominals[i]) {
          dataBase[j][i] = new Fuzzy[ (int) ranges[i][1] + 2]; //tantos como valores distintos + D.C.
          for (int etq = 0; etq < dataBase[j][i].length; etq++) {
            dataBase[j][i][etq] = new Fuzzy();
            dataBase[j][i][etq].x0 = ranges[i][0] + etq - 0.1;
            dataBase[j][i][etq].x1 = ranges[i][0] + etq;
            dataBase[j][i][etq].x3 = ranges[i][0] + etq + 0.1;
            dataBase[j][i][etq].y = 1.0;
            dataBase[j][i][etq].name = new String("L_" + etq + "(" +
                                                  (j + 2) + ")");
            dataBase[j][i][etq].label = (int) ( (1.5 * j) + (0.5 * j * j) + etq);
          }
          int etq = (int) ranges[i][1] + 1; //D.C.
          dataBase[j][i][etq] = new Fuzzy();
          dataBase[j][i][etq].x0 = Double.MIN_VALUE;
          dataBase[j][i][etq].x1 = ranges[i][1] + ranges[i][0] / 2;
          dataBase[j][i][etq].x3 = Double.MAX_VALUE;
          dataBase[j][i][etq].y = 1.0;
          dataBase[j][i][etq].name = new String("D.C.");
          dataBase[j][i][etq].label = -1;
        }
        else {
          dataBase[j][i] = new Fuzzy[2 + j];
          marca = (ranges[i][1] - ranges[i][0]) / ( (double) 1 + j);
          for (int etq = 0; etq < 2 + j; etq++) {
            valor = ranges[i][0] + marca * (etq - 1);
            dataBase[j][i][etq] = new Fuzzy();
            dataBase[j][i][etq].x0 = valor;
            valor = ranges[i][0] + marca * etq;
            dataBase[j][i][etq].x1 = valor;
            valor = ranges[i][0] + marca * (etq + 1);
            dataBase[j][i][etq].x3 = valor;
            dataBase[j][i][etq].y = 1;
            dataBase[j][i][etq].name = new String("L_" + etq + "(" +
                                                  (j + 2) + ")");
            dataBase[j][i][etq].label = (int) ( (1.5 * j) + (0.5 * j * j) +
                                               etq);
          }
        }
      }
    }
    //Now the D.C. value
    dataBase[4] = new Fuzzy[n_variables][];
    for (int i = 0; i < n_variables; i++) {
      dataBase[4][i] = new Fuzzy[1];
      dataBase[4][i][0] = new Fuzzy();
      dataBase[4][i][0].x0 = ranges[i][0];
      dataBase[4][i][0].x1 = ranges[i][0];
      dataBase[4][i][0].x3 = ranges[i][1];
      dataBase[4][i][0].y = 1.0;
      dataBase[4][i][0].name = new String("D.C.");
      dataBase[4][i][0].label = 14;
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
   * It computes the membership degree for a input value
   * @param i int the input variable id
   * @param j int the fuzzy label id
   * @param k int the layer of the hierarchical DB
   * @param X double the input value
   * @return double the membership degree
   */
  public double membership(int i, int j, int k, double X) {
    return dataBase[i][j][k].Fuzzify(X);
  }

  /**
   * It makes a copy of a fuzzy label
   * @param i int the input variable id
   * @param j int the fuzzy label id
   * @param k int the layer of the hierarchical DB
   * @return Fuzzy a copy of a fuzzy label
   */
  public Fuzzy copy(int i, int j, int k) {
    return dataBase[i][j][k].clone();
  }

  /**
   * It prints the points of a fuzzy label
   * @param var int variable id
   * @param label int label id
   * @return String the points of a fuzzy label
   */
  public String print_triangle(int var, int label) {
    String cadena = new String("");
    int k = 0;
    Fuzzy d;
    if (!nominals[var]) {
      if (label <= 1) {
        k = 0;
      }
      else if (label <= 4) {
        k = 1;
        label -= 2;
      }
      else if (label <= 8) {
        k = 2;
        label -= 5;
      }
      else if (label <= 13) {
        k = 3;
        label -= 9;
      }
      else {
        k = 4;
        label = 0;
      }
    }else {
      if (label == -1) {
        label = dataBase[k][var].length - 1;
      }
    }
    d = dataBase[k][var][label];
    cadena = d.name + ": \t" + d.x0 +
        "\t"
        + d.x1 +
        "\t" + d.x3 + "\n";
    return cadena;
  }

  /**
   * It prints the name of a fuzzy label
   * @param var int variable id
   * @param label int label id
   * @return String the name of a fuzzy label
   */
  public String print(int var, int label) {
    int k = 0;
    if (!nominals[var]) {
      if (label <= 1) {
        k = 0;
      }
      else if (label <= 4) {
        k = 1;
        label -= 2;
      }
      else if (label <= 8) {
        k = 2;
        label -= 5;
      }
      else if (label <= 13) {
        k = 3;
        label -= 9;
      }
      else {
        k = 4;
        label = 0;
      }
    }
    else {
      if (label == -1) {
        label = dataBase[k][var].length - 1;
      }
    }
    return dataBase[k][var][label].name;
  }

  /**
   * It prints the Data Base into an string
   * @return String the data base
   */
  public String printString() {
    String cadena = new String(
        "@Using Triangular Membership Functions as antecedent fuzzy sets");
    for (int k = 0; k < 4; k++) {
      cadena += "\n\n@Number of Labels per variable: " + (k + 2) + "\n";
      for (int i = 0; i < n_variables; i++) {
        cadena += "\n" + varNames[i] + ":\n";
        for (int j = 0; j < dataBase[k][i].length; j++) {
          cadena += dataBase[k][i][j].name + ": (" +
              dataBase[k][i][j].x0 +
              "," + dataBase[k][i][j].x1 + "," +
              dataBase[k][i][j].x3 +
              ")\n";
        }
      }
    }
    cadena += "\nPlus \"Don't Care\" Partition [f(x)=1 for all x]\n";
    return cadena;

  }

  /**
   * It writes the Data Base into an output file
   * @param filename String the name of the output file
   */
  public void writeFile(String filename) {
    String cadenaSalida = new String("");
    cadenaSalida = printString();
    Files.writeFile(filename, cadenaSalida);
  }

  /**
  * It returns the number of possible labels for a variable. It is useful in the case
  * of nominal variables
  * @param variable int the attribute position
  * @return int The number of different fuzzy/crisp labels of the variable
  */
  public int nLabels(int variable) {
    return dataBase[0][variable].length - 1;
  }

}

