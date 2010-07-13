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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.Eclat;

/**
 * <p>
 * @author Written by Nicolò Flugy Papè (Politecnico di Milano) 24/03/2009
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

import java.util.*;

public class EclatProcess {
  /**
   * <p>
   * It provides the implementation of the algorithm to be run in a process
   * </p>
   */
  
  private double minSupport;
  private double minConfidence;
  private myDataset dataset;
  
  private int nAttr;
  private int nTrans;
  private Item root;
  private int nFrequentItemsets;
  private int nCoveredRecords;
  
  /**
   * <p>
   * It creates a new process for the algorithm by setting up its parameters
   * </p>
   * @param dataset The instance of the dataset for dealing with its records
   * @param minSupport The user-specified minimum support for the mined association rules
   * @param minConfidence The user-specified minimum confidence for the mined association rules
   */
  public EclatProcess(myDataset dataset, double minSupport, double minConfidence) {
	  this.minSupport = minSupport;
	  this.minConfidence = minConfidence;
	  this.dataset = dataset;
	  
	  this.nAttr = dataset.getnVars();
	  this.nTrans = dataset.getnTrans();
	  this.root = new Item(-1);
  }
  
  /**
   * <p>
   * It runs the algorithm for mining association rules
   * </p>
   */
  public void run() {
	  this.generateFirstCandidates();
	  this.nFrequentItemsets = this.generateCandidates(this.root, new ArrayList<Item>(), 1);
  }
  
  /**
   * <p>
   * It constructs a rules set once the algorithm has been carried out
   * </p>
   * @return An array of association rules having both minimum confidence and support
   */
  public ArrayList<AssociationRule> generateRulesSet() {
	  ArrayList<AssociationRule> rules = new ArrayList<AssociationRule>();
	  HashSet<Integer> covered_records = new HashSet<Integer>();
	  
	  this.generateRules(this.root, new ArrayList<Item>(), rules, covered_records);
	  this.nCoveredRecords = covered_records.size();
	  
	  return rules;
  }
  
  /**
   * <p>
   * It prints out on screen relevant information regarding the mined association rules
   * </p>
   * @param rules The array of association rules from which gathering relevant information
   */
  public void printReport(ArrayList<AssociationRule> rules) {
	  int r;
	  double avg_sup = 0.0, avg_conf = 0.0, avg_ant_length = 0.0;	  
	  AssociationRule ar;
	  
	  for (r=0; r < rules.size(); r++) {
		  ar = rules.get(r);
		  
		  avg_sup += ar.getRuleSupport();
		  avg_conf += ar.getConfidence();
		  avg_ant_length += ar.getAntecedent().size();
	  }
	  
	  System.out.println("\nNumber of Frequent Itemsets found: " + this.nFrequentItemsets);
	  System.out.println("Number of Association Rules generated: " + rules.size());
	  
	  if (! rules.isEmpty()) {
		  System.out.println("Average Support: " + ( avg_sup / rules.size() ));
		  System.out.println("Average Confidence: " + ( avg_conf / rules.size() ));
		  System.out.println("Average Antecedents Length: " + ( avg_ant_length / rules.size() ));
		  System.out.println("Number of Covered Records (%): " + ( (100.0 * this.nCoveredRecords) / this.nTrans) );
	  }
  }
  
  private void generateFirstCandidates() {
	Enumeration<Integer> keys;
	
	for (keys = this.dataset.getTIDList().keys(); keys.hasMoreElements(); ) {
		this.root.addChild( new Item( keys.nextElement() ) );
	}
  }
  
  private int generateCandidates(Item item, ArrayList<Item> current, int depth) {
	int i, sup, generated = 0;  
	Item child;
	ArrayList<Item> v = item.getChildren(); 
    ArrayList<Item> v_tmp = new ArrayList<Item>(v);
	
    for (i=0; i < v_tmp.size(); i++) {
    	child = v_tmp.get(i);
    	current.add(child);
    	
	    sup = this.countSupport(current);
	    
	    if ( ( (double)sup / (double)this.nTrans ) >= this.minSupport ) {
	    	
	    	child.setSupport(sup);
	    	
	    	if (depth < this.nAttr) {
	    		this.copySiblings(child, v);
	        	generated += this.generateCandidates(child, current, depth + 1);
	        }
	    	
	    	generated++;
	    }
	    else v.remove(child);
	    
	    current.remove(child);
	}
    
    return generated;
  }
  
  private void copySiblings(Item item, ArrayList<Item> siblings) {
    int i, mod_item, mod_sibling;
	Item sibling;
    
	mod_item = item.getLabel() % this.nAttr;
    
    for (i=0; i < siblings.size(); i++) {
    	sibling = siblings.get(i);
    	mod_sibling = sibling.getLabel() % this.nAttr;
    	
    	if (mod_sibling > mod_item) item.addChild( new Item( sibling.getLabel() ) );
    }
  }
  
  private int countSupport(ArrayList<Item> itemset) {
	  return ( this.countCoveredRecords(itemset).size() );
  }
  
  private void generateRules(Item item, ArrayList<Item> itemset, ArrayList<AssociationRule> rules, HashSet<Integer> cov_recs) {
	int f, i, j;
	double rule_sup, ant_sup, conf;
	AssociationRule ar;
	ArrayList<Item> ant, v = item.getChildren();
    
	for (f=0; f < v.size(); f++) {
    	item = v.get(f);
    	itemset.add(item);
    	
    	if (itemset.size() > 1) {
    		for (i=0; i < itemset.size(); i++) {
    			ant = new ArrayList<Item>();
    			
    			for (j=0; j < itemset.size(); j++) {
        			if (i != j) ant.add( itemset.get(j) );
        		}
        		
    			rule_sup = (double)item.getSupport() / (double)this.nTrans;
    			ant_sup = (double)searchItemsetIntoTrie(this.root, ant, 0) / (double)this.nTrans;
    			
    			conf = rule_sup / ant_sup;
    			
    			if (conf >= this.minConfidence) {
    				ar = new AssociationRule();
    				
    				for (j=0; j < ant.size(); j++) {
    					ar.addAntecedent( ant.get(j).getLabel() );
    				}
    				
        			ar.addConsequent( itemset.get(i).getLabel() );
    				
        			ar.setRuleSupport(rule_sup);
    				ar.setAntecedentSupport(ant_sup);
    				ar.setConfidence(conf);
    				
    				cov_recs.addAll( this.countCoveredRecords(itemset) );
    				
    				rules.add(ar);
        		}
    		}	
    	}
    		
    	if ( item.hasChildren() ) this.generateRules(item, itemset, rules, cov_recs);
    	
    	itemset.remove(item);
    }
  }
  
  private int searchItemsetIntoTrie(Item item, ArrayList<Item> itemset, int index) {
	int i, support = 0;
	ArrayList<Item> v = item.getChildren();
     
	for (i=0; i < v.size(); i++) {
    	item = v.get(i);
    	
    	if ( item.equals( itemset.get(index) ) ) {
    		if (index == (itemset.size()-1)) return ( item.getSupport() );
    		else if ( item.hasChildren() ) support = searchItemsetIntoTrie(item, itemset, index + 1);
    		
    		break;
    	}
    }
      
    return support;
  }
  
  private HashSet<Integer> countCoveredRecords(ArrayList<Item> itemset) {
	int i, k;
	ArrayList<HashSet<Integer>> v_tid_lst = new ArrayList<HashSet<Integer>>();
	
	for (i=0; i < itemset.size(); i++) {
		Item item = itemset.get(i);
		v_tid_lst.add( this.dataset.getTIDList().get( item.getLabel() ) );
	}
	
	HashSet<Integer> toIntersect = new HashSet<Integer>( v_tid_lst.get(0) );
	
	for (k=1; k < v_tid_lst.size(); k++) {
		toIntersect.retainAll( v_tid_lst.get(k) );
		if ( toIntersect.isEmpty() ) break;
	}
	
	return toIntersect;
  }
  
}
