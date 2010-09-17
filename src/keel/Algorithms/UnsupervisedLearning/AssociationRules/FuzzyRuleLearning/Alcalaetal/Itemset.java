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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.Alcalaetal;

/**
 * <p>
 * @author Written by Alvaro Lopez
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

import java.util.*;

public class Itemset {
  /**
   * <p>
   * It represents an itemset throughout the execution of the algorithm
   * </p>
   */
	
  private ArrayList<Item> itemset;
  private double support;

  /**
   * <p>
   * Default constructor
   * </p>
   */
  public Itemset() {
    this.itemset = new ArrayList<Item>();
  }
  
  /**
   * <p>
   * It allows to clone correctly an itemset
   * </p>
   * @return A copy of the itemset
   */
  public Itemset clone() {
    Itemset item = new Itemset();
	
    for (int i=0; i < this.itemset.size(); i++)
		item.add( (itemset.get(i)).clone() );
    
    item.support = this.support;
    
	return item;
  }

  /**
   * <p>
   * It allows to add an item into an itemset
   * </p>
   * @param item An item to be added into the itemset
   */
  public void add(Item item) {
	this.itemset.add(item);
  }
  
  /**
   * <p>
   * It returns the item stored at the index "pos" within an itemset
   * </p>
   * @param pos The index of the item to be returned
   * @return The item which is stored at the index "pos" of the itemset
   */
  public Item get(int pos) {
	return ( this.itemset.get(pos) );
  }

  /**
   * <p>
   * It allows to remove the item stored at the index "pos" within an itemset
   * </p>
   * @param pos The index of the item to be removed
   * @return The item which was previously removed from the itemset
   */
  public Item remove(int pos) {
	return ( this.itemset.remove(pos) );
  }

  /**
   * <p>
   * It returns the number of items contained into an itemset
   * </p>
   * @return A value representing the number of items contained into the itemset
   */
  public int size() {
	return ( this.itemset.size() );
  }
  
  /**
   * <p>
   * It returns the support of an itemset
   * </p>
   * @return A value representing the support of the itemset
   */
  public double getSupport() {
	return this.support;
  }
  
  /**
   * <p>
   * It computes the support of an itemset
   * </p>
   * @param fuzzyDataset The instance of the fuzzy dataset for dealing with its fuzzy transactions
   * @return An array of integer representing the TIDs covered by the itemset
   */
  public ArrayList<Integer> calculateSupport(FuzzyDataset fuzzyDataset) {
	int i;
    double degree;
    double[][][] fuzzyTransactions;
    ArrayList<Integer> covered_tids;
    
    fuzzyTransactions = fuzzyDataset.getFuzzyTransactions();
    covered_tids = new ArrayList<Integer>();
    this.support = 0.0;    
    
    for (i=0; i < fuzzyTransactions.length; i++) {
      degree = this.doIntersection( fuzzyTransactions[i] );
      
      if (degree > 0) {
    	  this.support += degree;
    	  covered_tids.add(i);
      }
    }
    
	this.support /= fuzzyTransactions.length;
	
	return covered_tids;
  }

  private double doIntersection(double[][] fuzzy_trans) {
    return ( this.computeMinimum(fuzzy_trans) );
  }

  private double computeMinimum(double[][] fuzzy_trans) {
    int i;
	double min;
	Item item;
	
    min = 1.0;
    
    for (i=0; i < this.itemset.size(); i++) {
		item = this.itemset.get(i);
		min = Math.min(min, fuzzy_trans[ item.getIDAttribute() ][ item.getIDLabel() ]);
    }
    
    return min;
  }
  
  /**
   * <p>
   * It indicates whether some other itemset is "equal to" this one
   * </p>
   * @param obj The reference object with which to compare
   * @return True if this itemset is the same as the argument; False otherwise
   */
  public boolean equals(Object obj) {
	Itemset its = (Itemset)obj;
	Item it;
	
	if (this.itemset.size() != its.size()) return false;
	
	for (int i=0; i < this.itemset.size(); i++) {
		it = this.itemset.get(i);
		if (! it.equals( its.get(i) ) ) return false;
	}
	
	return true;
  }
  
  /**
   * <p>
   * It returns a raw string representation of an itemset
   * </p>
   * @return A raw string representation of the itemset
   */
  public String toString() {
	String str = "{";
	int i;
	
	for (i=0; i < this.itemset.size() - 1; i++)
		str += this.itemset.get(i) + ", ";
	
	str += this.itemset.get(i) + "}";
	
	return str;
  }
  
}