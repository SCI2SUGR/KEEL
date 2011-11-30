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

package keel.Algorithms.Associative_Classification.ClassifierCPAR;

/**
 * This class contains the 
 * representation of a structure that stores
 * some information corresponding to rule r.
 * Such as,
 * P and N as the number of positive and negative
 * examples satisfying rule's body
 * P[p] and N[p] as the numbers of positive and negative examples for each literal p
 * satisfying the body of rule r', the rule constructed by appending p to r.
 * 
 *
 * @author Written by Jesus Alcalá (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */
public class PNArray {
	
  int nVariables, nData;
  int[] nLabels;
  double P, N;
  double [][] PLiteral;
  double [][] NLiteral;
  myDataset train;
  DataBase dataBase;

  /**
   * <p>
   * Default Constructor
   * </p>
   */
  public PNArray() {
  }

  /**
   * <p>
   * Parameters Constructor
   * </p>
   * @param dataBase DataBase Set of training data which is necessary to generate the PNArray
   * @param train myDataset Training data set with information to construct the PNArray (mainly, the training examples)
   */
  public PNArray(myDataset train, DataBase dataBase) {
	int i;

	this.nData = train.getnData();
	this.nVariables = train.getnInputs();
	this.nLabels = dataBase.getnLabels();
	this.train = train;
	this.dataBase = dataBase;

	PLiteral = new double[this.nVariables][];
	NLiteral = new double[this.nVariables][];

	for (i=0; i < this.nVariables; i++) {
		this.PLiteral[i] = new double[this.nLabels[i]];
		this.NLiteral[i] = new double[this.nLabels[i]];
	}
  }

  /**
   * <p>
   * Function to copy from a PNArray to ours. 
   * </p>
   * @param a PNArray Object to be copied
   */
  public void copy (PNArray a) {
	  int i, j;

	  this.nData = a.nData;
	  this.nVariables = a.nVariables;
	  this.nLabels = a.nLabels;
	  this.train = a.train;
	  this.dataBase = a.dataBase;

	  this.P = a.P;
	  this.N = a.N;
	  this.PLiteral = new double[a.nVariables][];
	  this.NLiteral = new double[a.nVariables][];

	  for (i=0; i < a.nVariables; i++) {
		  this.PLiteral[i] = new double[a.nLabels[i]];
		  this.NLiteral[i] = new double[a.nLabels[i]];
		  
		  for (j=0; j < a.nLabels[i]; j++) {
			  this.PLiteral[i][j] = a.PLiteral[i][j];
			  this.NLiteral[i][j] = a.NLiteral[i][j];
		  }
	  }
  }

  /**
   * <p>
   * It initializes a PNArray with a given class
   * </p>
   * @param clas int Class which we create a PNArray from.
   */
  public void ini (int clas) {
	int i, j;
	int[] example;

	this.P = (double) this.train.numberInstances(clas);
    this.N = (double) this.nData - this.P;

	for (i=0; i < this.nVariables; i++) {
		for (j=0; j < this.nLabels[i]; j++) {
			this.PLiteral[i][j] = 0.0;
			this.NLiteral[i][j] = 0.0;
		}
	}

	for (i=0; i < this.nData; i++) {
		example = this.train.getExample(i);
		if (this.train.getOutputAsInteger(i) == clas) {
			for (j=0; j < this.nVariables; j++)  this.PLiteral[j][example[j]] += 1.0;
		}
		else {
			for (j=0; j < this.nVariables; j++)  this.NLiteral[j][example[j]] += 1.0;
	    }
	}
  }

  /**
   * <p>
   * It initializes a PNArray from a given rule
   * </p>
   * @param r Rule Given rule to fill a PNArray
   */
  public void ini (Rule r) {
	int i, j;
	int[] example;

	this.P = 0.0;
    this.N = 0.0;

	for (i=0; i < this.nVariables; i++) {
		for (j=0; j < this.nLabels[i]; j++) {
			this.PLiteral[i][j] = 0.0;
			this.NLiteral[i][j] = 0.0;
		}
	}

	for (i=0; i < this.nData; i++) {
		example = this.train.getExample(i);
		if (r.matching(example) > 0.0) {
			if (this.train.getOutputAsInteger(i) == r.getClas()) {
				this.P += this.train.getWeight(i);
				for (j=0; j < this.nVariables; j++)  this.PLiteral[j][example[j]] += this.train.getWeight(i);
			}
			else {
				this.N += this.train.getWeight(i);
				for (j=0; j < this.nVariables; j++)  this.NLiteral[j][example[j]] += this.train.getWeight(i);
			}
		}
	}
  }

  /**
   * <p>
   * It reduces the positive value P of the PNArray by extracting the weight of a training example given by its position in the training dataset
   * </p>
   * @param pos int Position of the example in the dataset 
   */
  public void reducePositive (int pos) {
	int i;
	int[] example = this.train.getExample(pos);

	for (i=0; i < this.nVariables; i++)  this.PLiteral[i][example[i]] -= this.train.getWeight(pos);
	this.P -= this.train.getWeight(pos);
  }

  /**
   * <p>
   * It reduces the negative value P of the PNArray by extracting the weight of a training example given by its position in the training dataset
   * </p>
   * @param pos int Position of the example in the dataset 
   */
  public void reduceNegative (int pos) {
	int i;
	int[] example = this.train.getExample(pos);

	for (i=0; i < this.nVariables; i++)  this.NLiteral[i][example[i]] -= this.train.getWeight(pos);
	this.N -= this.train.getWeight(pos);
  }

  /**
   * <p>
   * It increases the positive value P of the PNArray by adding the weight of a training example given by its position in the training dataset
   * </p>
   * @param pos int Position of the example in the dataset 
   */
  public void incrPositive (int pos) {
	int i;
	int[] example = this.train.getExample(pos);

	for (i=0; i < this.nVariables; i++)  this.PLiteral[i][example[i]] += this.train.getWeight(pos);
	this.P += this.train.getWeight(pos);
  }

  /**
   * <p>
   * It returns the value of the positive weight of the PNArray
   * </p>
   * @return double positive weight of the PNArray
   */
  public double getP() {
	  return (this.P);
  }

  /**
   * <p>
   * It returns the value of the negative weight of the PNArray
   * </p>
   * @return double Negative weight of the PNArray
   */
  public double getN() {
	  return (this.N);
  }
 
  /**
   * <p>
   * It returns the positive weight of a given literal
   * </p>
   * @param var int attribute of the literal
   * @param value int attribute's value of the literal
   * @return double The positive weight of a given literal
   */
  public double getP(int var, int value) {
	  return (this.PLiteral[var][value]);
  }

  /**
   * <p>
   * It returns the negative weight of a given literal
   * </p>
   * @param var int attribute of the literal
   * @param value int attribute's value of the literal
   * @return double The negative weight of a given literal
   */
  public double getN(int var, int value) {
	  return (this.NLiteral[var][value]);
  }
}

