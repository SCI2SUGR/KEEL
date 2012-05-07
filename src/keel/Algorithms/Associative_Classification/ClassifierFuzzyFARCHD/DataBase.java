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

package keel.Algorithms.Associative_Classification.ClassifierFuzzyFARCHD;

/**
 * <p>Title: DataBase</p>
 * <p>Description: Fuzzy Data Base</p>
 * <p>Copyright: Copyright KEEL (c) 2008</p>
 * <p>Company: KEEL </p>
 * @author Written by Jesus Alcalá (University of Granada) 09/02/2011
 * @version 1.0
 * @since JDK1.6
 */

import org.core.Files;

public class DataBase {
  int n_variables, partitions;
  int[] nLabels;
  boolean[] varReal;
  Fuzzy[][] dataBase;
  Fuzzy[][] dataBaseIni;
  String names[];

  public DataBase() {
  }

/**
* <p>
* This method builds the database, creating the initial linguistic partitions
* </p>
* @param nLabels Number of Linguistic Values
* @param train Training dataset
* @return The databse
*/

  public DataBase(int nLabels, myDataset train) {
    double mark, value, rank, labels;
	double[][] ranks = train.returnRanks();

	this.n_variables = train.getnInputs();
	this.names = (train.names()).clone();
	this.nLabels = new int[this.n_variables];
	this.varReal = new boolean[this.n_variables];
    this.dataBase = new Fuzzy[this.n_variables][];
    this.dataBaseIni = new Fuzzy[this.n_variables][];

    for (int i = 0; i < this.n_variables; i++) {
	  rank = Math.abs(ranks[i][1] - ranks[i][0]);

	  this.varReal[i] = false;

	  if (train.isNominal(i))  this.nLabels[i] = ((int) rank) + 1;
	  else if (train.isInteger(i) && ((rank + 1) <= nLabels))  this.nLabels[i] = ((int) rank) + 1;
	  else {
		  this.nLabels[i] = nLabels;
		  this.varReal[i] = true;
	  }

	  this.dataBase[i] = new Fuzzy[this.nLabels[i]];
	  this.dataBaseIni[i] = new Fuzzy[this.nLabels[i]];

	  mark = rank / (this.nLabels[i] - 1.0);
	  for (int j = 0; j < this.nLabels[i]; j++) {
		  this.dataBase[i][j] = new Fuzzy();
		  this.dataBaseIni[i][j] = new Fuzzy();
		  value = ranks[i][0] + mark * (j - 1);
		  this.dataBaseIni[i][j].x0 = this.dataBase[i][j].x0 = this.setValue(value, ranks[i][0], ranks[i][1]);
		  value = ranks[i][0] + mark * j;
		  this.dataBaseIni[i][j].x1 = this.dataBase[i][j].x1 = this.setValue(value, ranks[i][0], ranks[i][1]);
		  value = ranks[i][0] + mark * (j + 1);
		  this.dataBaseIni[i][j].x3 = this.dataBase[i][j].x3 = this.setValue(value, ranks[i][0], ranks[i][1]);
		  this.dataBaseIni[i][j].y = this.dataBase[i][j].y = 1.0;
		  this.dataBase[i][j].name = new String("L_" + j + "(" + this.nLabels[i] + ")");
		  this.dataBaseIni[i][j].name = new String("L_" + j + "(" + this.nLabels[i] + ")");
	  }
    }
  }

  private double setValue(double val, double min, double tope) {
    if (val > min - 1E-4 && val < min + 1E-4)  return (min);
    if (val > tope - 1E-4 && val < tope + 1E-4)  return (tope);
    return (val);
  }

  public void decode(double[] gene) {
	  int i, j, pos;
	  double displacement;

	  pos = 0;

	  for (i=0; i < n_variables; i++) {
		  if (varReal[i]) {
			  for (j=0; j < this.nLabels[i]; j++, pos++) {
				  if (j == 0)  displacement = (gene[pos] - 0.5) * (this.dataBaseIni[i][j+1].x1 - this.dataBaseIni[i][j].x1);
				  else if (j == (this.nLabels[i]-1))  displacement = (gene[pos] - 0.5) * (this.dataBaseIni[i][j].x1 - this.dataBaseIni[i][j-1].x1);
				  else {
					  if ((gene[pos] - 0.5) < 0.0)  displacement = (gene[pos] - 0.5) * (this.dataBaseIni[i][j].x1 - this.dataBaseIni[i][j-1].x1);
					  else  displacement = (gene[pos] - 0.5) * (this.dataBaseIni[i][j+1].x1 - this.dataBaseIni[i][j].x1);
				  }
				  
				  this.dataBase[i][j].x0 = this.dataBaseIni[i][j].x0 + displacement;
				  this.dataBase[i][j].x1 = this.dataBaseIni[i][j].x1 + displacement;
				  this.dataBase[i][j].x3 = this.dataBaseIni[i][j].x3 + displacement;
			  }
		  }
	  }
  }

  public int numVariables() {
    return (this.n_variables);
  }

  public int getnLabelsReal() {
	  int i, count;

	  count = 0;

	  for (i=0; i < n_variables; i++) {
		  if (varReal[i])  count += this.nLabels[i];
	  }

	  return (count);
  }

  public int numLabels(int variable) {
    return (this.nLabels[variable]);
  }

  public int[] getnLabels() {
    return (this.nLabels);
  }

  public double matching(int variable, int label, double value) {
	if ((variable < 0) || (label < 0))  return (1);  // Don't care
    else  return (this.dataBase[variable][label].Fuzzifica(value));
  }

  public String print_triangle(int var, int label) {
    String cadena = new String("");

	Fuzzy d = this.dataBase[var][label];

    cadena = d.name + ": \t" + d.x0 + "\t" + d.x1 + "\t" + d.x3 + "\n";
    return cadena;
  }

  public String print(int var, int label) {
	return (this.dataBase[var][label].getName());
  }

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

  public void saveFile(String filename) {
    String stringOut = new String("");
    stringOut = printString();
    Files.writeFile(filename, stringOut);
  }

}
