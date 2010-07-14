package keel.Algorithms.Associative_Classification.ClassifierFuzzyCFAR;

/**
 * <p>Title: Apriori</p>
 *
 * <p>Description: This class mines the frecuent fuzzy itemsets and the fuzzy classification associacion rules</p>
 *
 * <p>Copyright: Copyright KEEL (c) 2007</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Jesus Alcala (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */

import java.util.*;


public class Apriori {
  ArrayList<Itemset> L1;
  double minpsup, minpconf, MS;
  int nClasses, nVariables;
  long time;
  RuleBase ruleBase;

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
   * @param minpsup double The minimum support for the problem
   * @param minpconf double The minimum confidence for the problem
   * @param MS double Cut Threshold (MS)
   * @param ruleBase RuleBase Rule Base
   */
  public Apriori(double minpsup, double minpconf, double MS, RuleBase ruleBase) {
	  this.L1 = new ArrayList<Itemset> ();
	  this.minpsup = minpsup; 
	  this.minpconf = minpconf; 
	  this.MS = MS;
	  this.ruleBase = ruleBase;
  }


  /**
   * <p>
   * Generates the Rule Base with the whole Classification Association Rules set
   * by using the Apriori Method
   * </p>
   */
  public void generate (myDataset train, int[] nLabels) {
	  int i, j;

	  this.time = 0;
	  this.nClasses = train.getnClasses();
	  this.nVariables = train.getnInputs();

	  for (i=0; i < this.nClasses; i++) {
		  System.out.println("Class: " + (i+1));
		  this.generateL1 (train, nLabels, i);
		  this.generateLarge (this.L1, train);
		  System.gc();
	  }
  }

  private void generateL1 (myDataset train, int[] nLabels, int clas) {
	  int i, j;
	  Item item;
	  Itemset itemset;
	  
	  itemset = new Itemset (clas, this.MS);
	  this.L1.clear();

	  for (i=0; i < this.nVariables; i++) {
		  for (j=0; j < nLabels[i]; j++) {
			  item = new Item(i, j);
			  itemset.add(item);
			  itemset.calculateSupports(train);
			  if (itemset.getSupportClass() >= this.minpsup)  this.L1.add(itemset.clone());
			  itemset.remove(0);
		  }
	  }

	  this.generateRules(this.L1);
  }


  private void generateLarge (ArrayList<Itemset> Lk, myDataset train) {
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
						  newItemset.calculateSupports(train);
						  if (newItemset.getSupportClass() >= this.minpsup)  Lnew.add(newItemset);
					  }
				  }
				  
				  this.generateLarge(Lnew, train);
				  this.generateRules(Lnew);
				  Lnew.clear();
			  }
			  
			  System.gc();
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

  private void generateRules(ArrayList<Itemset> Lk) {
	  int i;
	  Itemset itemset;
	  double confidence;

	  for (i=0; i < Lk.size(); i++) {
		  itemset = Lk.get(i);
		  confidence = itemset.getSupportClass() / itemset.getSupport();
		  if (confidence >= this.minpconf) {
			  this.ruleBase.add(itemset, this.time);
			  this.time++;
		  }
	  }

	  if (this.ruleBase.size() > 200000) {
		  this.ruleBase.selection();
		  itemset = Lk.get(0);
		  System.out.println("Number of rules in Apriori (" + itemset.getClas() + "/" + this.nClasses + "): " + this.ruleBase.size());
		  System.gc();
	  }
  }
}
