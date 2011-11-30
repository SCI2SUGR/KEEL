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

import java.util.*;

/**
 * This class mines the frecuent fuzzy itemsets and the fuzzy classification associacion rules
 *
 * @author Written by Jesus Alcala (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */
public class Apriori {
  ArrayList<Itemset> L2;
  double minpsup, minpconf;
  int nClasses, nVariables;
  RuleBase ruleBase;
  myDataset train;
  DataBase dataBase;

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
   */
  public Apriori(DataBase dataBase, myDataset train) {
	  this.train = train;
	  this.dataBase = dataBase;

	  this.L2 = new ArrayList<Itemset> ();
  }

  /**
   * <p>
   * Sets the minimum confidence and support thresholds
   * </p>
   * @param minpsup double The minimum support for the problem
   * @param minpconf double The minimum confidence for the problem
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
  public RuleBase generateRB (double minpsup, double minpconf) {
	  int i, j;

	  this.ruleBase = new RuleBase(this.dataBase, this.train);

	  this.minpsup = minpsup; 
	  this.minpconf = minpconf;
	  
	  if ((this.minpsup == 0.0) || (this.minpconf == 0.0))  return ruleBase;


	  this.nClasses = this.train.getnClasses();
	  this.nVariables = this.train.getnInputs();

	  this.generateL2();
	  this.generateLarge (this.L2);
	  System.gc();

	  this.ruleBase.reduceRules();
	  return (this.ruleBase);
  }

  private void generateL2() {
	  int i, j, k;
	  Item item;
	  Itemset itemset;
	  
	  this.L2.clear();
	  itemset = new Itemset(0);

	  for (i=0; i < this.nVariables; i++) {
		  for (j=0; j < this.dataBase.numLabels(i); j++) {
			  item = new Item(i, j);
			  itemset.add(item);
			  for (k=0; k < this.nClasses; k++) {
				  itemset.setClas(k);
				  itemset.calculateSupports(this.train);
				  if (itemset.getSupportClass() > this.minpsup)  this.L2.add(itemset.clone());
			  }
			  itemset.remove(0);
		  }
	  }

	  this.generateRules(this.L2);
  }


  private void generateLarge (ArrayList<Itemset> Lk) {
	  int i, j, size;
	  ArrayList<Itemset> Lnew;
	  Itemset newItemset, itemseti, itemsetj;

	  size = Lk.size();

	  if (size > 1) {
		  if ((Lk.get(0)).size() < this.nVariables) {
			  Lnew = new ArrayList<Itemset> ();

			  for (i = 0; i < size-1; i++) {
				  itemseti = Lk.get(i);
				  for (j = i+1; j < size; j++) {
					  itemsetj = Lk.get(j);
					  if (this.isCombinable(itemseti, itemsetj)) {
						  newItemset = itemseti.clone();
						  newItemset.add((itemsetj.get(itemsetj.size()-1)).clone());
						  newItemset.calculateSupports(this.train);
						  if (newItemset.getSupportClass() > this.minpsup)  Lnew.add(newItemset);
					  }
				  }
				  
				  this.generateLarge(Lnew);
				  this.generateRules(Lnew);
				  Lnew.clear();
			      System.gc();
			  }
		  }
	  }
  }

  private boolean isCombinable(Itemset itemseti, Itemset itemsetj) {
	  int i;
	  Item itemi, itemj;
	  Itemset itemset;

	  if (itemseti.getClas() != itemsetj.getClas())  return (false);

	  itemi = itemseti.get(itemseti.size()-1);
	  itemj = itemsetj.get(itemseti.size()-1);
	  if (itemi.getVariable() >= itemj.getVariable())  return (false);

	  return (true);
  }

  private void generateRules(ArrayList<Itemset> Lk) {
	  int i;
	  Itemset itemset;
	  double confidence;

	  for (i=0; i < Lk.size(); i++) {
		  itemset = Lk.get(i);
		  if (itemset.getSupport() > 0.0)  confidence = itemset.getSupportClass() / itemset.getSupport();
		  else  confidence = 0.0;
		  if (confidence > this.minpconf) {
			  this.ruleBase.add(itemset);
		  }
	  }

	  if (this.ruleBase.size() > 200000) {
		  this.ruleBase.reduceRules();
		  System.out.println("Number of rules: " + this.ruleBase.size());
		  System.gc();
	  }
  }
}

