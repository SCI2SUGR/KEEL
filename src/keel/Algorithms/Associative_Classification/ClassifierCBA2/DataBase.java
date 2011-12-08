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

package keel.Algorithms.Associative_Classification.ClassifierCBA2;

import org.core.Files;


/**
 * Class to store the examples to work with the algorithm and some other useful information
 *
 * @author Written by Jesus Alcalá (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */
public class DataBase {

  int n_variables, nVariablesUsed, partitions;
  int[] nLabels;
  int[][] dataBase;
  myDataset train;
  String names[];

  /**
   * <p>
   * Default Constructor
   * </p>
   */
  public DataBase() {
  }

  /**
   * <p>
   * Parameters Constructor
   * </p>
   * @param train It contains the train data set with the whole information to execute the algorithm
   */
  public DataBase(myDataset train) {
    int rank;
	int[][] ranks = train.returnRanks();

	this.n_variables = train.getnInputs();
	this.nVariablesUsed = 0;
	this.names = (train.names()).clone();
	this.nLabels = new int[this.n_variables];
    this.dataBase = new int[this.n_variables][];
	this.train = train;

    for (int i = 0; i < this.n_variables; i++) {
	  rank = Math.abs(ranks[i][1] - ranks[i][0]);

	  this.nLabels[i] = ((int) rank) + 1;
	  this.dataBase[i] = new int[this.nLabels[i]];
	  if (this.nLabels[i] > 1)  this.nVariablesUsed++;
	  for (int j = 0; j < this.nLabels[i]; j++)  this.dataBase[i][j] = j;
    }
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
   * It returns the number of input attributes which has been used
   * </p>
   * @return int The number of input attributes which has been used
   */
  public int numVariablesUsed() {
    return (this.nVariablesUsed);
  }

  /**
   * <p>
   * It returns the number of different possible outputs (classes) of the examples
   * </p>
   * @return int The number of different possible outputs (classes) of the examples
   */
  public int numClasses() {
    return (this.train.getnClasses());
  }
  
  /**
   * <p>
   * It returns the number of different labels that a specific input attribute can hold
   * </p>
   * @param variable The input attribute which we want to know the number of different labels it can have
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
   * @param variable Attribute which we are going to check
   * @param label Attribute's label we are going to check
   * @param value Value to be compared
   * @return int 1 = they match, 0 = they don't.
   */
  public int matching(int variable, int label, int value) {
	if ((variable < 0) || (label < 0))  return (1);  // Don't care
    else if (this.dataBase[variable][label] == value)  return (1);
	else  return (0);
  }

  /**
   * <p>
   * It prints an attribute with its label in a string way
   * </p>
   * @param var Attribute to be printed
   * @param label Attribute's label to be printed
   * @return String A string which represents the "string format" of the given input
   */
  public String print(int var, int label) {
	return (this.train.getInputAsString(var, label));
  }

  /**
   * <p>
   * It prints the whole database
   * </p>
   * @return String The whole database
   */
  public String printString() {
    String string = new String("@Data Base:");
    for (int i = 0; i < this.n_variables; i++) {
      string += "\n\n@Number of Values in Variable " + (i+1) + ": " + this.nLabels[i];
      string += "\n" + this.names[i] + ":\n";
      for (int j = 0; j < this.nLabels[i]; j++)  string += this.print(i, j) + "\n";
    }

	return string;
  }

  /**
   * <p>
   * It stores the data base in a given file
   * </p>
   * @param filename Name for the database file
   */
  public void saveFile(String filename) {
    String stringOut = new String("");
    stringOut = printString();
    Files.writeFile(filename, stringOut);
  }

}