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
 * <p>Title: Apriori</p>
 * <p>Description: This class mines the frecuent fuzzy itemsets and the fuzzy classification associacion rules</p>
 * <p>Copyright: Copyright KEEL (c) 2007</p>
 * <p>Company: KEEL </p>
 * @author Written by Jesus Alcala (University of Granada) 09/02/2011
 * @version 1.0
 * @since JDK1.6
 */

import java.util.*;
				 

public class Apriori {
  ArrayList<Itemset> L2;
  double minsup, minconf;
  double[] minSupps;
  int nClasses, nVariables, depth;
  long ruleStage1;
  RuleBase ruleBase;
  RuleBase ruleBaseClase;
  myDataset train;
  DataBase dataBase;

  public Apriori() {
  }

/**
* <p>
* Builder
* </p>
* @param ruleBase Rule base
* @param dataBase Data Base
* @param train Training dataset
* @param minsup Minimum support.
* @param minconf Maximum Confidence.
* @param depth Depth of the trees (Depthmax)
* @return A object Apriori
*/
  public Apriori(RuleBase ruleBase, DataBase dataBase, myDataset train, double minsup, double minconf, int depth) {
	  this.train = train;
	  this.dataBase = dataBase;
	  this.ruleBase = ruleBase;
	  this.minconf = minconf;
	  this.depth = depth;
	  this.nClasses = this.train.getnClasses();
	  this.nVariables = this.train.getnInputs();

	  this.L2 = new ArrayList<Itemset> ();
	  minSupps = new double[this.nClasses];
	  for (int i=0; i < this.nClasses; i++)  minSupps[i] = this.train.frecuentClass(i) * minsup;
  }


/**
* <p>
* Generate the rule set (Stage 1 and 2)
* </p>
* @return void
*/
  public void generateRB () {
	  int i, j, uncover;

	  ruleStage1 = 0;

	  this.ruleBaseClase = new RuleBase(this.dataBase, this.train, this.ruleBase.getK(), this.ruleBase.getTypeInference());

	  for (i=0; i < this.nClasses; i++) {
		  this.minsup = minSupps[i]; 
		  this.generateL2(i);
		  this.generateLarge (this.L2, i);
		  
		  this.ruleBaseClase.reduceRules(i);

		  this.ruleBase.add(this.ruleBaseClase);
		  this.ruleBaseClase.clear();
		  System.gc();
	  } 
  }

  private void generateL2(int clas) {
	  int i, j, k, uncover;
	  Item item;
	  Itemset itemset;
	  
	  this.L2.clear();
	  itemset = new Itemset(clas);

	  for (i=0; i < this.nVariables; i++) {
		  if (this.dataBase.numLabels(i) > 1) {
			  for (j=0; j < this.dataBase.numLabels(i); j++) {
				  item = new Item(i, j);
				  itemset.add(item);
				  itemset.calculateSupports(this.dataBase, this.train);
				  if (itemset.getSupportClass() >= this.minsup)  this.L2.add(itemset.clone());
				  itemset.remove(0);
			  }
		  }
	  }

	  this.generateRules(this.L2, clas);
  }


  public int hasUncoverClass(int clas) {
    int uncover;
	double degree;
	Itemset itemset;
	boolean stop;
	
	uncover = 0;
    for (int j = 0; j < train.size(); j++) {
		if (this.train.getOutputAsInteger(j) == clas) {
			stop = false;
			for (int i=0; i < L2.size() && !stop; i++) {
				itemset = L2.get(i);
				degree = itemset.degree(this.dataBase, this.train.getExample(j));
				if (degree > 0.0)  stop = true;
			}

			if (!stop)  uncover++;
		}
    }

	return uncover;
  }


  private void generateLarge (ArrayList<Itemset> Lk, int clas) {
	  int i, j, size;
	  ArrayList<Itemset> Lnew;
	  Itemset newItemset, itemseti, itemsetj;

	  size = Lk.size();

	  if (size > 1) {
		  if (((Lk.get(0)).size() < this.nVariables) && ((Lk.get(0)).size() < this.depth)) {
			  Lnew = new ArrayList<Itemset> ();

			  for (i = 0; i < size-1; i++) {
				  itemseti = Lk.get(i);
				  for (j = i+1; j < size; j++) {
					  itemsetj = Lk.get(j);
					  if (this.isCombinable(itemseti, itemsetj)) {
						  newItemset = itemseti.clone();
						  newItemset.add((itemsetj.get(itemsetj.size()-1)).clone());
						  newItemset.calculateSupports(this.dataBase, this.train);
						  if (newItemset.getSupportClass() >= this.minsup)  Lnew.add(newItemset);
					  }
				  }
				  
				  this.generateRules(Lnew, clas);
				  this.generateLarge(Lnew, clas);
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

	  itemi = itemseti.get(itemseti.size()-1);
	  itemj = itemsetj.get(itemseti.size()-1);
	  if (itemi.getVariable() >= itemj.getVariable())  return (false);

	  return (true);
  }

  public long getRulesStage1() {
	  return (ruleStage1);
  }

  private void generateRules(ArrayList<Itemset> Lk, int clas) {
	  int i, uncover;
	  Itemset itemset;
	  double confidence;

	  for (i=Lk.size() - 1; i >= 0; i--) {
		  itemset = Lk.get(i);
		  if (itemset.getSupport() > 0.0)  confidence = itemset.getSupportClass() / itemset.getSupport();
		  else  confidence = 0.0;
		  if (confidence > 0.4) {
			  this.ruleBaseClase.add(itemset);
			  ruleStage1++;
		  }
		  if (confidence > this.minconf)  Lk.remove(i);
	  }

	  if (this.ruleBaseClase.size() > 500000) {
		  this.ruleBaseClase.reduceRules(clas);
		  System.gc();
	  }
  }
}
