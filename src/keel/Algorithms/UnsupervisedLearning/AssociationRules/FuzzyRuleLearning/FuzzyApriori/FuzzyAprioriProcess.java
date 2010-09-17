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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.FuzzyApriori;

/**
 * <p>
 * @author Written by Alvaro López
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

import java.util.*;

public class FuzzyAprioriProcess {
  /**
   * <p>
   * It provides the implementation of the algorithm to be run in a process
   * </p>
   */
  
  private double minSupport;
  private double minConfidence;
  private boolean useMaxForOneFrequentItemsets;
  private myDataset dataset;

  private int countOneFrequentItemsets;
  private int countFrequentItemsets;
  private ArrayList<AssociationRule> associationRulesSet;
  private boolean[] coveredRecords;
  
  /**
   * <p>
   * It creates a new process for the algorithm by setting up its parameters
   * </p>
   * @param dataset The instance of the dataset for dealing with its records
   * @param useMaxForOneFrequentItemsets It indicates whether the max operator must be used while discovering 1-Frequent Itemsets
   * @param minSupport The user-specified minimum support for the mined association rules
   * @param minConfidence The user-specified minimum confidence for the mined association rules
   */
  public FuzzyAprioriProcess(myDataset dataset, boolean useMaxForOneFrequentItemsets, double minSupport, double minConfidence) {
	  this.useMaxForOneFrequentItemsets = useMaxForOneFrequentItemsets;
	  this.minSupport = minSupport;
	  this.minConfidence = minConfidence;
	  this.dataset = dataset;

      this.countOneFrequentItemsets = 0;
      this.countFrequentItemsets = 0;
      this.associationRulesSet = new ArrayList<AssociationRule>();
	  
	  this.coveredRecords = new boolean[ dataset.getnTrans() ];
	  for (int i=0; i < this.coveredRecords.length; i++)
		  this.coveredRecords[i] = false;
  }
  
  /**
   * <p>
   * It runs the algorithm for mining association rules
   * </p>
   */
  public void run() {
	  int pass = 0;
	  ArrayList<Itemset> current_frequent_itemsets;
	  
	  current_frequent_itemsets = this.generateOneFrequentItemsets(this.useMaxForOneFrequentItemsets);
	  this.countOneFrequentItemsets = current_frequent_itemsets.size();
	  this.countFrequentItemsets = this.countOneFrequentItemsets;
	  
	  System.out.println("\nPass: " + (pass + 1) + "; Total Frequent Itemsets: " + this.countFrequentItemsets);
	  
	  for (pass=1; (pass < this.dataset.getnVars()) && (current_frequent_itemsets.size() > 1); pass++) {
		  current_frequent_itemsets = this.generateCandidateItemsetsAndRules(current_frequent_itemsets);
		  this.countFrequentItemsets += current_frequent_itemsets.size();
		  
		  System.out.println("Pass: " + (pass + 1) + "; Total Frequent Itemsets: " + this.countFrequentItemsets + "; Total Association Rules: " + this.associationRulesSet.size());
	  }
  }
  
  /**
   * <p>
   * It returns a rules set once the algorithm has been carried out
   * </p>
   * @return An array of association rules having both minimum confidence and support
   */
  public ArrayList<AssociationRule> getRulesSet() {
	  return this.associationRulesSet;
  }
  
  /**
   * <p>
   * It prints out on screen relevant information regarding the mined association rules
   * </p>
   * @param rules The array of association rules from which gathering relevant information
   */
  public void printReport(ArrayList<AssociationRule> rules) {
	  int r;
	  double avg_sup = 0.0, avg_conf = 0.0, avg_ant_length = 0.0, avg_interest = 0.0;
	  AssociationRule ar;
	  
	  for (r=0; r < rules.size(); r++) {
		  ar = rules.get(r);
		  
		  avg_sup += ar.getRuleSupport();
		  avg_conf += ar.getConfidence();
		  avg_ant_length += ar.getAntecedent().size();
                  avg_interest += ar.getInterestingness();
	  }
	  
	  System.out.println("\nNumber of Frequent Itemsets found: " + this.countFrequentItemsets);
	  System.out.println("Number of Association Rules generated: " + rules.size());
	  
	  if (! rules.isEmpty()) {
		  System.out.println("Average Support: " + ( avg_sup / rules.size() ));
		  System.out.println("Average Confidence: " + ( avg_conf / rules.size() ));
		  System.out.println("Average Antecedents Length: " + ( avg_ant_length / rules.size() ));
		  System.out.println("Number of Covered Records (%): " + ( (100.0 * this.countCoveredRecords()) / this.dataset.getnTrans()));
                  System.out.println("Average Interestingness: " + ( avg_interest / rules.size() ));
	  }
  }
  
  /**
   * <p>
   * It returns the number of 1-Frequent Itemsets
   * </p>
   * @return A value representing the number of 1-Frequent Itemsets
   */
  public int getNumberOfOneFrequentItemsets() {
	  return this.countOneFrequentItemsets;
  }
  
  private ArrayList<Itemset> generateOneFrequentItemsets(boolean use_max_for_one_frequent_itemsets) {
	  int id_attr, id_label;
	  double max_support;
	  int[] nLabels;
	  Itemset itemset, best_itemset;
	  ArrayList<Itemset> one_frequent_itemsets;
	  
	  nLabels = this.dataset.getNLabelsOfAttributes();
	  one_frequent_itemsets = new ArrayList<Itemset>();
	  
	  if (use_max_for_one_frequent_itemsets) {
		  for (id_attr=0; id_attr < this.dataset.getnVars(); id_attr++) {
			  best_itemset = new Itemset();
			  best_itemset.add( new Item(id_attr, 0) );
			  best_itemset.calculateSupport(this.dataset);
			  max_support = best_itemset.getSupport();
			  
			  for (id_label=1; id_label < nLabels[id_attr]; id_label++) {
				  itemset = new Itemset();  
				  itemset.add( new Item(id_attr, id_label) );
				  itemset.calculateSupport(this.dataset);
				  
				  if (itemset.getSupport() > max_support) {
					  max_support = itemset.getSupport();
					  best_itemset = itemset;
				  }
			  }
			  
			  if (max_support >= this.minSupport) one_frequent_itemsets.add(best_itemset);
		  }
	  }
	  else {
		  for (id_attr=0; id_attr < this.dataset.getnVars(); id_attr++) {
			  for (id_label=0; id_label < nLabels[id_attr]; id_label++) {
				  itemset = new Itemset();  
				  itemset.add( new Item(id_attr, id_label) );
				  itemset.calculateSupport(this.dataset);
				  
				  if (itemset.getSupport() >= this.minSupport) one_frequent_itemsets.add(itemset);
			  }
		  }
	  }
	  
	  return one_frequent_itemsets;
  }
  
  private ArrayList<Itemset> generateCandidateItemsetsAndRules(ArrayList<Itemset> curr_freq_itemsets) {
	  int i, j, size;
	  boolean generated_rules;
	  Itemset i_itemset, j_itemset, new_itemset;
	  ArrayList<Integer> covered_tids;
	  ArrayList<Itemset> next_freq_itemsets;
	  
	  size = curr_freq_itemsets.size();
	  next_freq_itemsets = new ArrayList<Itemset>();
	  
	  for (i=0; i < size-1; i++) {
		  i_itemset = curr_freq_itemsets.get(i);
		  
		  for (j=i+1; j < size; j++) {
			  j_itemset = curr_freq_itemsets.get(j);
			  
			  if ( this.isCombinable(i_itemset, j_itemset, curr_freq_itemsets) ) {
				  new_itemset = i_itemset.clone();
				  new_itemset.add( ( j_itemset.get(j_itemset.size() - 1) ).clone() );
				  covered_tids = new_itemset.calculateSupport(this.dataset);
				  
				  if (new_itemset.getSupport() >= this.minSupport) {
					  generated_rules = this.generateRulesFromItemset(new_itemset);
					  if (generated_rules) this.markCoveredRecords(covered_tids);
					  
					  next_freq_itemsets.add(new_itemset);
				  }
			  }
		  }
	  }
	  
	  return next_freq_itemsets;
  }
  
  private boolean generateRulesFromItemset(Itemset curr_itemset) {
	  int i;
	  double rule_sup, ant_sup, rule_conf,cons_sup,interest;
	  boolean generated_rules = false;
	  Item i_item;
	  Itemset antecedent, consequent;
	  
	  for (i=0; i < curr_itemset.size(); i++) {
		  antecedent = curr_itemset.clone();
		  i_item = antecedent.remove(i);
		  antecedent.calculateSupport(this.dataset);
		  
		  rule_sup = curr_itemset.getSupport();
		  ant_sup = antecedent.getSupport();
		  rule_conf = rule_sup / ant_sup;
		  
		  if (rule_conf >= this.minConfidence) {
			  consequent = new Itemset();
			  consequent.add(i_item);
                          consequent.calculateSupport(this.dataset);
                          cons_sup = consequent.getSupport();
                          interest = rule_conf * (rule_sup/cons_sup) * (1 - (rule_sup/this.dataset.getnTrans()));
			  this.associationRulesSet.add( new AssociationRule(antecedent, consequent, rule_sup, ant_sup, rule_conf,cons_sup,interest) );
			  
			  if (! generated_rules) generated_rules = true;
		  }
	  }
	  
	  return generated_rules;
  }
  
  private boolean isCombinable(Itemset i_itemset, Itemset j_itemset, ArrayList<Itemset> curr_freq_itemsets) {
	  int i;
	  Item i_item, j_item;
	  Itemset itemset;

	  if (i_itemset.size() != j_itemset.size()) return false;

	  i_item = i_itemset.get(i_itemset.size() - 1);
	  j_item = j_itemset.get(i_itemset.size() - 1);
	  if (i_item.getIDAttribute() >= j_item.getIDAttribute()) return false;

	  for (i=0; i < (i_itemset.size() - 1); i++) {
		  i_item = i_itemset.get(i);
		  j_item = j_itemset.get(i);
		  
		  if (! i_item.equals(j_item)) return false;
	  }
	  
	  itemset = i_itemset.clone();
	  itemset.add( ( j_itemset.get(i_itemset.size() - 1) ).clone() );
	  if ( this.pruning(itemset, curr_freq_itemsets) ) return false;

	  return true;
  }
  
  private boolean pruning(Itemset itemset, ArrayList<Itemset> curr_freq_itemsets) {
	  int i;
	  Itemset sub;

	  for (i=0; i < itemset.size() - 2; i++) {
		  sub = itemset.clone();
		  sub.remove(i);
		  if (! this.existingIntoFrequentItemsets(sub, curr_freq_itemsets)) return true;
	  }
	  
	  return false;
  }
  
  private boolean existingIntoFrequentItemsets(Itemset itemset, ArrayList<Itemset> curr_freq_itemsets) {
	  int i;
	  Itemset its;

	  for (i=0; i < curr_freq_itemsets.size(); i++) {
		  its = curr_freq_itemsets.get(i);
		  if ( its.equals(itemset) ) return true;
	  }
	  
	  return false;
  }
  
  private void markCoveredRecords(ArrayList<Integer> covered_tids) {
	  int i, t;
	  
	  for (i=0; i < covered_tids.size(); i++) {
		  t = covered_tids.get(i);
		  if (! this.coveredRecords[t]) this.coveredRecords[t] = true;
	  }
  }
  
  private int countCoveredRecords() {
	  int i, cnt_covered_records = 0;
	  
	  for (i=0; i < this.coveredRecords.length; i++) {
		  if (this.coveredRecords[i]) cnt_covered_records++;
	  }
	  
	  return cnt_covered_records;
  }
  
}