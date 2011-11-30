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

import java.util.*;

/**
 * Class to store a non-fuzzy rule, together with some necessary information to manage the CBA algorithm
 * 
 * @author Written by Jesus Alcala (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */
public class Rule implements Comparable {
	
  int[] antecedent;
  int clas, nAnts;
  double laplaceAccuracy;
  DataBase dataBase;

  /**
   * <p>
   * Copy Constructor
   * </p>
   */
  public Rule() {
  }

  public Rule(DataBase dataBase) {
    this.antecedent = new int[dataBase.numVariables()];
    for (int i = 0; i < this.antecedent.length; i++)  this.antecedent[i] = -1;  // Don't care

    this.dataBase = dataBase;
	this.clas = -1;
	this.nAnts = 0;
	this.laplaceAccuracy = 0.0;
  }

  /**
   * <p>
   * Parameters Constructor
   * </p>
   * @param dataBase DataBase Set of training data which is necessary to generate a rule
   */
  public Rule(myDataset train, DataBase dataBase, int clas) {
    this.antecedent = new int[dataBase.numVariables()];
    for (int i = 0; i < this.antecedent.length; i++)  this.antecedent[i] = -1;  // Don't care

    this.dataBase = dataBase;
	this.clas = clas;
	this.nAnts = 0;
	this.laplaceAccuracy = 0.0;
  }

  /**
   * <p>
   * Clone Function
   * </p>
   */
  public Rule clone() {
    Rule r = new Rule(this.dataBase);
    for (int i = 0; i < this.antecedent.length; i++)  r.antecedent[i] = this.antecedent[i];

	r.clas = this.clas;
	r.dataBase = this.dataBase;
	r.nAnts = this.nAnts;
	r.laplaceAccuracy = this.laplaceAccuracy;

    return (r);
  }

  /**
   * It sets the label for a given position in the antecedent (for a given attribute)
   * @param pos int Location of the attribute which we want to set the label
   * @param label int New label value to set
   */
  public void setLabel(int pos, int label) {
	if ((antecedent[pos] < 0) && (label > -1))  this.nAnts++;
	if ((antecedent[pos] > -1) && (label < 0))  this.nAnts--;
	this.antecedent[pos] = label;
  }

  /**
   * <p>
   * Function to check if a given example matchs with the rule (the rule correctly classifies it)
   * </p>
   * @param example int[] Example to be classified
   * @return double 0.0 = doesn't match, >0.0 = does.
   */
  public double matching(int[] example) {
    return (this.degree(example));
  }

  private double degree(int[] example) {
    double degree;

    degree = 1.0;
    for (int i = 0; i < antecedent.length && degree > 0.0; i++) {
      degree *= (double) dataBase.matching(i, antecedent[i], example[i]);
    }

    return (degree);
  }

  /**
   * <p>
   * It returns the output class of the rule
   * </p>
   * @return int Output class of the rule
   */
  public int getClas() {
    return (this.clas);
  }

  /**
   * <p>
   * It returns the number of antecedents of the rule
   * </p>
   * @return int Number of antecedents of the rule
   */
  public int getnAnts() {
    return (this.nAnts);
  }

  /**
   * <p>
   * It returns the Laplace accuracy of the rule
   * </p>
   * @return double Laplace accuracy of the rule
   */
  public double getLaplace() {
    return (this.laplaceAccuracy);
  }

  /**
   * <p>
   * It returns an array of literals whose gain is higher than a minimum threshold
   * </p>
   * @param min_gain double Minimum gain threshold
   * @param A PNArray Training dataset splitted in Possitive and Negative examples
   * @return ArrayList<Literal> Literals whose gain is higher than a minimum threshold
   */
  public ArrayList<Literal> getGain (double min_gain, PNArray A) {
	  int i, j;
	  Literal lit;
	  double gain;

	  ArrayList<Literal> listLiterals = new ArrayList<Literal> ();

	  for (i=0; i < this.antecedent.length; i++) {
		  if ((this.antecedent[i] < 0) && (this.dataBase.numLabels(i) > 1)) {
			  for (j=0; j < this.dataBase.numLabels(i); j++) {
				  if (A.getP(i, j) > 0.0) {
					  gain = A.getP(i, j) * (Math.log(A.getP(i, j) * 1.0 / (A.getP(i, j) + A.getN(i, j))) - Math.log(A.getP() / (A.getP() + A.getN())));
				  }
				  else  gain = -1.0;
				  if (gain >= min_gain) {
					  lit = new Literal(i, j);
					  lit.setGain(gain);
					  listLiterals.add(lit);
				  }
			  }
		  }
	  }

	  return (listLiterals);
  }

  /**
   * <p>
   * Function to calculate the Laplace accuracy to our rule from a train dataset
   * </p>
   * @param train myDataset Dataset used to calculate Laplace
   */
  public void calculateLaplace(myDataset train) {
	  int i, nc, nTotal;

	  nc = nTotal = 0;
	  
	  for (i=0; i < train.getnData(); i++) {
		  if (this.matching(train.getExample(i)) > 0.0) {
			  if (train.getOutputAsInteger(i) == clas)  nc++;
			  nTotal++;
		  }
	  }

	  this.laplaceAccuracy = (nc + 1.0) / (1.0 * nTotal + train.getnClasses());
  }

  /**
   * Function to compare objects of the Rule class
   * Necessary to be able to use "sort" function
   * It sorts in an decreasing order of laplace accuracy
   */
  public int compareTo(Object a) {
    if ( ( (Rule) a).laplaceAccuracy < this.laplaceAccuracy) {
      return -1;
    }
    if ( ( (Rule) a).laplaceAccuracy > this.laplaceAccuracy) {
      return 1;
    }
    return 0;
  }


}

