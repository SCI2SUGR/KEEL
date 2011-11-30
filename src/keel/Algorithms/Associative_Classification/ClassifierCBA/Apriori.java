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

package keel.Algorithms.Associative_Classification.ClassifierCBA;

import java.util.*;

/**
 * This class mines the frecuent non-fuzzy itemsets and the non-fuzzy classification association rules.
 *
 * @author Written by Jesus Alcala (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */
public class Apriori {
	
  ArrayList<Itemset> L2;
  double minpsup, minpconf;
  int nClasses, nVariables, prune;
  long time;
  RuleBase ruleBase;
  myDataset train;
  DataBase dataBase;
  int limitRules;
  int numberRules;

  /**
   * <p>
   * Default Constructor
   * </p>
   */
  public Apriori() {
  }

  /**
   * <p>
   * Parameters Constructor: Generates an Apriori objects from a list of parameters
   * </p>
   * @param dataBase Raw training information useful in the class
   * @param train Contains the train data set with the whole information to execute the algorithm
   * @param minpsup The minimum support for the problem
   * @param minpconf The minimum confidence for the problem
   * @param prune Value which decides whether prune or not the rule set
   * @param limitRules Maximum number of rules to generate (0 = no_limit)
   */
  public Apriori(DataBase dataBase, myDataset train, double minpsup, double minpconf, int prune, int limitRules) {
	  this.train = train;
	  this.dataBase = dataBase;
	  this.minpsup = minpsup; 
	  this.minpconf = minpconf; 
	  this.prune = prune;
	  this.nClasses = this.train.getnClasses();
	  this.nVariables = this.train.getnInputs();
	  this.limitRules = limitRules;

	  this.L2 = new ArrayList<Itemset> ();
  }

  /**
   * <p>
   * Sets the minimum confidence and support thresholds
   * </p>
   * @param minpsup The minimum support for the problem
   * @param minpconf The minimum confidence for the problem
   */
  public void setSupportConfidence (double minpsup, double minpconf) {
	  this.minpsup = minpsup; 
	  this.minpconf = minpconf; 
  }

  /**
   * <p>
   * Generates the Rule Base with the whole Classification Association Rules set
   * by using the Apriori Method
   * </p>
   * @return RuleBase The whole CAR rule set
   */
  public RuleBase generateCAR () {
	  this.ruleBase = new RuleBase(this.dataBase, this.train);
	  this.numberRules = 0;
	  this.time = 0;

	  this.generateL2();
	  this.generateLarge (this.L2);
	  System.gc();

	  return (this.ruleBase);
  }

  private void generateL2() {
	  int i, j, k;
	  Item item;
	  Itemset itemset;
	  
	  this.L2.clear();
	  itemset = new Itemset(0);

	  for (i=0; i < this.nVariables && this.numberRules < this.limitRules; i++) {
		  if (this.dataBase.numLabels(i) > 1) {
			  for (j=0; j < this.dataBase.numLabels(i) && this.numberRules < this.limitRules; j++) {
				  item = new Item(i, j);
				  itemset.add(item);
				  for (k=0; k < this.nClasses && this.numberRules < this.limitRules; k++) {
					  itemset.setClas(k);
					  itemset.calculateSupports(this.train);
					  if (itemset.getSupportClass() > this.minpsup) {
						  this.L2.add(itemset.clone());
						  this.numberRules++;
					  }
				  }
				  itemset.remove(0);
			  }
		  }
	  }

	  this.generateRules(this.L2, this.L2);
  }


  private void generateLarge (ArrayList<Itemset> Lk) {
	  int i, j, size;
	  ArrayList<Itemset> Lnew;
	  Itemset newItemset, itemseti, itemsetj;

	  size = Lk.size();

	  if (size > 1 && this.numberRules < this.limitRules) {
		  if ((Lk.get(0)).size() < this.dataBase.numVariablesUsed()) {
			  Lnew = new ArrayList<Itemset> ();

			  for (i = 0; i < size-1 && this.numberRules < this.limitRules; i++) {
				  itemseti = Lk.get(i);
				  for (j = i+1; j < size && this.numberRules < this.limitRules; j++) {
					  itemsetj = Lk.get(j);
					  if (this.isCombinable(itemseti, itemsetj)) {
						  newItemset = itemseti.clone();
						  newItemset.add((itemsetj.get(itemsetj.size()-1)).clone());
						  newItemset.calculateSupports(this.train);
						  if (newItemset.getSupportClass() >= this.minpsup) {
							  Lnew.add(newItemset);
							  this.numberRules++;
						  }
					  }
				  }
			  }
			  
			  this.generateRules(Lnew, Lk);
			  
			  if (this.numberRules < this.limitRules) {
				  this.numberRules -= Lk.size();
				  Lk.clear();
				  System.gc();
				  this.generateLarge(Lnew);
			  }
		  }
	  }
  }

		
  private boolean isCombinable(Itemset itemseti, Itemset itemsetj) {
	  int i;
	  Item itemi, itemj;

	  if (itemseti.getClas() != itemsetj.getClas())  return (false);
	  if (itemseti.size() != itemsetj.size())  return (false);

	  for (i=0; i < itemseti.size()-1; i++) {
		  itemi = itemseti.get(i);
		  itemj = itemsetj.get(i);
		  if ((itemi.getVariable() != itemj.getVariable()) || (itemi.getValue() != itemj.getValue()))  return (false);
	  }

	  itemi = itemseti.get(itemseti.size()-1);
	  itemj = itemsetj.get(itemsetj.size()-1);
	  if (itemi.getVariable() >= itemj.getVariable())  return (false);

	  return (true);
  }


  private boolean isPrune(Itemset itemset, ArrayList<Itemset> Lf) {
	  int i;
	  Itemset itemseti;

	  for (i=0; i < Lf.size() && Lf.size() > 1; i++) {
		  itemseti = Lf.get(i);

		  if ((itemseti.isSubItemset(itemset)) && (itemseti.getPer() < itemset.getPer()))  return (true);
	  }

	  return (false);
  }


  private void generateRules(ArrayList<Itemset> Lk, ArrayList<Itemset> Lf) {
	  int i, j;
	  Itemset itemseti, itemsetj;
	  double confidence, bestConfidence;
	  boolean stop;

	  Collections.sort(Lk);

	  for (i=0; i < Lk.size() && this.numberRules < this.limitRules;) {
		  itemseti = Lk.get(i);

		  if (itemseti.getSupport() > 0.0)  bestConfidence = itemseti.getSupportClass() / itemseti.getSupport();
		  else  bestConfidence = 0.0;

		  stop = false;
		  for (j=i+1; j < Lk.size() && !stop;) {
			  itemsetj = Lk.get(j);
			  if (itemseti.isEqualAnt(itemsetj)) {
				  if (itemsetj.getSupport() > 0.0)  confidence = itemsetj.getSupportClass() / itemsetj.getSupport();
				  else confidence = 0.0;

				  if (confidence > bestConfidence) {
					  bestConfidence = confidence;
					  itemseti = itemsetj;
				  }
				  j++;
			  }
			  else  stop = true;
		  }

		  if (bestConfidence >= this.minpconf) {
			  if (this.prune > 0 && itemseti.size() > 1) {
				  if (!this.isPrune(itemseti, Lf)) {
					  this.ruleBase.add(itemseti, this.time);
					  this.time++;
					  this.numberRules++;
				  }
			  }
			  else {
				  this.ruleBase.add(itemseti, this.time);
				  this.time++;
				  this.numberRules++;
			  }
		  }
		  i = j;
	  }
  }
}

