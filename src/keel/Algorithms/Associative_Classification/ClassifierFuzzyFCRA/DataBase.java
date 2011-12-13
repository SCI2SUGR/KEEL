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

package keel.Algorithms.Associative_Classification.ClassifierFuzzyFCRA;

import org.core.Files;

/**
 * Fuzzy Data Base
 *
 * @author Written by Jesus Alcalá (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */
public class DataBase {
  int n_variables, partitions;
  int[] nLabels;
  Fuzzy[][] dataBase;
  String names[];

  public DataBase() {
  }

  /**
   * <p>
   * Parameters Constructor
   * </p>
   * @param nLabels int It is the number of membership functions for each real/integer variable
   * @param train myDataset It contains the train data set with the whole information to execute the algorithm
   */
  public DataBase(int nLabels, myDataset train) {
    double mark, value, rank, labels;
	double[][] ranks = train.devuelveRangos();

	this.n_variables = train.getnInputs();
	this.names = (train.names()).clone();
	this.nLabels = new int[this.n_variables];
    this.dataBase = new Fuzzy[this.n_variables][];

    for (int i = 0; i < this.n_variables; i++) {
	  rank = Math.abs(ranks[i][1] - ranks[i][0]);

	  if (train.isNominal(i))  this.nLabels[i] = ((int) rank) + 1;
	  else  this.nLabels[i] = nLabels;

	  this.dataBase[i] = new Fuzzy[this.nLabels[i]];

	  mark = rank / (this.nLabels[i] - 1.0);
	  for (int j = 0; j < this.nLabels[i]; j++) {
		  this.dataBase[i][j] = new Fuzzy();
		  value = ranks[i][0] + mark * (j - 1);
		  this.dataBase[i][j].x0 = this.setValue(value, ranks[i][0], ranks[i][1]);
		  value = ranks[i][0] + mark * j;
		  this.dataBase[i][j].x1 = this.setValue(value, ranks[i][0], ranks[i][1]);
		  value = ranks[i][0] + mark * (j + 1);
		  this.dataBase[i][j].x3 = this.setValue(value, ranks[i][0], ranks[i][1]);
		  this.dataBase[i][j].y = 1;
		  this.dataBase[i][j].name = new String("L_" + j + "(" + this.nLabels[i] + ")");
	  }
    }
  }

  private double setValue(double val, double min, double tope) {
    if (val > min -1E-4 && val < min + 1E-4)  return (min);
    if (val > tope - 1E-4 && val < tope + 1E-4)  return (tope);
    return (val);
  }

  /**
   * <p>
   * It returns the number of input attributes in the examples
   * </p>
   * @return int The number of input attributes
   */
  public int numVariables() {
    return (this.n_variables);
  }

  /**
   * <p>
   * It returns the number of different labels that a specific input attribute can hold
   * </p>
   * @param variable int The input attribute which we want to know the number of different labels it can have
   * @return int The number of labels
   */
  public int numLabels(int variable) {
    return (this.nLabels[variable]);
  }

  /**
   * <p>
   * It return the whole array of number of labels for every attribute
   * </p>
   * @return int[] the whole array of number of labels for every attribute
   */
  public int[] getnLabels() {
    return (this.nLabels);
  }

  /**
   * <p>
   * It checks if the value of a specific label in a specific attribute matchs with a given value
   * </p>
   * @param variable int Attribute which we are going to check
   * @param label int Attribute's label we are going to check
   * @param value int Value to be compared
   * @return int 1 = they match, 0 = they don't.
   */
  public double matching(int variable, int label, double value) {
	if ((variable < 0) || (label < 0))  return (1);  // Don't care
    else  return (this.dataBase[variable][label].Fuzzifica(value));
  }

  /**
   * <p>
   * It prints the values of the fuzzy label of the given variable
   * </p>
   * 
   * @param var Attribute which we are going to check
   * @param label Attribute's label we are going to check
   * @return the values of the fuzzy label of the given variable
   */
  public String print_triangle(int var, int label) {
    String cadena = new String("");

	Fuzzy d = this.dataBase[var][label];

    cadena = d.name + ": \t" + d.x0 + "\t" + d.x1 + "\t" + d.x3 + "\n";
    return cadena;
  }

  /**
   * <p>
   * It prints an attribute with its label in a string way
   * </p>
   * @param var int Attribute to be printed
   * @param label int Attribute's label to be printed
   * @return String A string which represents the "string format" of the given input
   */
  public String print(int var, int label) {
	return (this.dataBase[var][label].getName());
  }

  /**
   * <p>
   * It prints the whole database
   * </p>
   * @return String The whole database
   */
  public String printString() {
    String string = new String("@Using Triangular Membership Functions as antecedent fuzzy sets");
    for (int i = 0; i < this.n_variables; i++) {
      string += "\n\n@Number of Labels in Variable " + (i+1) + ": " + this.nLabels[i];
      string += "\n" + this.names[i] + ":\n";
      for (int j = 0; j < this.nLabels[i]; j++) {
        string += this.dataBase[i][j].name + ": (" + this.dataBase[i][j].x0 + "," + this.dataBase[i][j].x1 + "," + this.dataBase[i][j].x3 + ")\n";
      }
    }
    return string;
  }

  /**
   * <p>
   * It stores the data base in a given file
   * </p>
   * @param filename String Name for the database file
   */
  public void saveFile(String filename) {
    String stringOut = new String("");
    stringOut = printString();
    Files.writeFile(filename, stringOut);
  }

}

