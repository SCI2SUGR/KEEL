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
 * This class stores information to manage the PRM procedure.
 * This algorithm modifies FOIL to achieve higher accuracy and efficiency.
 *
 *
 * @author Written by Jesus Alcala (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */
public class PRM {
	
  double alfa, delta, min_gain;
  int nClasses, nData;
  myDataset train;
  DataBase dataBase;
  RuleBase ruleBase;
  int[] P;
  int[] N;
  PNArray A;
  int[] Pc;
  int[] Nc;
  PNArray Ac;

  /**
   * <p>
   * Default Constructor
   * </p>
   */
  public PRM() {
  }
  
  /**
   * <p>
   * Parameters Constructor
   * </p>
   * @param dataBase DataBase Set of training data which is necessary to compute PRM
   * @param train myDataset Training data set with information to compute PRM (mainly, the training examples)
   * @param ruleBase RuleBase Set of rules to be pruned
   * @param alfa double Weight decay factor
   * @param delta double Number of best rules we will take in account for each example
   * @param min_gain double Minimum gain threshold to be used
   */
  public PRM(DataBase dataBase, myDataset train, RuleBase ruleBase, double alfa, double delta, double min_gain) {
	  this.dataBase = dataBase;
	  this.train = train;
	  this.ruleBase = ruleBase;
	  this.alfa = alfa; 
	  this.delta = delta; 
	  this.min_gain = min_gain; 
	  this.nClasses = this.train.getnClasses();
	  this.nData = this.train.getnData();

	  this.P = new int[this.nData];
	  this.N = new int[this.nData];
	  this.A = new PNArray(train, dataBase);
	  this.Pc = new int[this.nData];
	  this.Nc = new int[this.nData];
	  this.Ac = new PNArray(train, dataBase);
  }

  /**
   * <p>
   * Main function of the method: it prunes the rulebase to obtain the final set of rules.
   * </p>
   */
  public void generatePR () {
	  int i, j;
	  boolean stop, addLiteral;
	  double totalWeight, bestGain;
	  Literal lit;
	  Rule r, ruleAux, ruleAux1;
	  ArrayList<Rule> listRules;
	  ArrayList<Literal> listGain;

	  listRules = new ArrayList<Rule> ();
//	  this.listGain = new ArrayList<Literal> ();

	  for (i=0; i < this.nClasses; i++) {
		  this.train.iniWeight();
		  this.iniPN(i);
		  this.A.ini(i);
		  totalWeight = this.delta * this.train.numberInstances(i);
		  listRules.clear();
//		  this.listGain.clear();
		  stop = false;

		  while ((this.getTotalWeight() > totalWeight) && !stop) {
			  if (listRules.size() > 0) {
				  r = listRules.get(0);
				  listRules.remove(0);
				  this.calculatePNc(r);
				  this.calculateAc(r);
			  }
			  else {
				  r = new Rule (train, dataBase, i);
				  this.copyPN();
				  this.copyA();
			  }

			  addLiteral = true;
			  while ((r.getnAnts() < this.dataBase.numVariablesUsed()) && addLiteral) {
//				  this.listGain.clear();
				  listGain = r.getGain(this.min_gain, this.Ac);
				  
				  if (listGain.size() > 0) {
					  ruleAux = r.clone();
					  if (listGain.size() > 1)  Collections.sort(listGain);

					  lit = listGain.get(0);
					  bestGain = lit.getGain();
					  r.setLabel(lit.getVariable(), lit.getValue());

					  this.changePNAc(lit);

					  for (j=1; j < listGain.size(); j++) {
						  lit = listGain.get(j);
						  if (lit.getGain() >= (bestGain * 0.99)) {
							  ruleAux1 = ruleAux.clone();
							  ruleAux1.setLabel(lit.getVariable(), lit.getValue());
							  listRules.add(ruleAux1);
						  }
						  else  j = listGain.size();
					  }
					  listGain.clear();
				  }
				  else  addLiteral = false;
			  }

			  if (r.getnAnts() > 0) {
				  r.calculateLaplace(this.train);
				  this.ruleBase.add(r);
				  for (j=0; j < this.nData; j++) {
					  if (this.Pc[j] > 0) {
						  this.A.reducePositive(j);
						  this.train.reduceWeight(j, this.alfa);
						  this.A.incrPositive(j);
					  }
				  }
			  }
			  else  stop = true;
		  }
	  }

	  this.ruleBase.sort();
  }

  /**
   * <p>
   * It initializes positive and negative values for each example for a given class
   * </p>
   * @param clas int Class to initialize positive and negative values of each example
   */
  public void iniPN(int clas) {
	  int i;

	  for (i=0; i < this.nData; i++) {
		  if (this.train.getOutputAsInteger(i) == clas) {
			  this.P[i] = 1;
			  this.N[i] = 0;
		  }
		  else {
			  this.P[i] = 0;
			  this.N[i] = 1;
		  }
	  }
  }

  /**
   * <p>
   * Function to copy P and N values to an auxiliar structure
   * </p>
   */
  public void copyPN() {
	  int i;

	  for (i=0; i < this.nData; i++) {
		  this.Pc[i] = this.P[i];
		  this.Nc[i] = this.N[i];
	  }
  }

  /**
   * <p>
   * Function to copy a PNArray to an auxiliar PNArray
   * </p>
   */
  public void copyA() {
	  this.Ac.copy(this.A);
  }

  /**
   * <p>
   * It computes positive and negative values for each example in the training set
   * by means a given rule
   * </p>
   * @param r Rule Rule used to compute P and N
   */
  public void calculatePNc(Rule r) {
	  int i;

	  for (i=0; i < this.nData; i++) {
		  if (r.matching(this.train.getExample(i)) > 0.0) {
			  if (this.train.getOutputAsInteger(i) == r.getClas()) {
				  this.Pc[i] = 1;
				  this.Nc[i] = 0;
			  }
			  else {
				  this.Pc[i] = 0;
				  this.Nc[i] = 1;
			  }
		  }
		  else {
			  this.Pc[i] = 0;
			  this.Nc[i] = 0;
		  }
	  }
  }

  /**
   * <p>
   * It computes the PNArray A' from a rule r
   * </p>
   * @param r Rule Rule used to compute the PNArray
   */
  public void calculateAc(Rule r) {
	  this.Ac.ini(r);
  }

  /**
   * <p>
   * It modifies positive and negative values and the PNArray of each example from a given literal
   * </p>
   * @param lit Literal Given literal to modify P, N and Ac
   */
  public void changePNAc (Literal lit) {
	  int j;
	  int[] example; 

	  for (j=0; j < this.nData; j++) {
		  example = this.train.getExample(j);
		  if (this.Pc[j] > 0) {
			  if (example[lit.getVariable()] != lit.getValue()) {
				  this.Pc[j] = 0;
				  this.Ac.reducePositive(j);
			  }
		  }
		  else if (this.Nc[j] > 0) {
			  if (example[lit.getVariable()] != lit.getValue()) {
				  this.Nc[j] = 0;
				  this.Ac.reduceNegative(j);
			  }
		  }
	  }
  }

  /**
   * <p>
   * It returns the total weight of the positive classification
   * </p>
   * @return double Total weight
   */
  public double getTotalWeight() {
	  int i;
	  double total;

	  total = 0.0;
	  for (i=0; i < this.nData; i++) {
		  if (this.P[i] > 0)  total += this.train.getWeight(i);
	  }

	  return (total);
  }

}

