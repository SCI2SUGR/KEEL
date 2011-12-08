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

package keel.Algorithms.Associative_Classification.ClassifierCBA2;

import java.util.*;


/**
 * This class stores an itemset representation for classification by association algorithms.
 * Also, it stores some useful information to manage the itemset
 *
 * @author Jesus Alcalá (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */
public class Itemset implements Comparable {

  ArrayList<Item> itemset;
  int clas;
  double support, supportRule, per;
  int hits, misses;

  
  /**
   * <p>
   * Default Constructor
   * </p>
   */
  public Itemset() {
  }

  /**
   * <p>
   * Parameters Constructor
   * </p>
   * @param clas Associated output of the Itemset
   */
  public Itemset(int clas) {
      this.itemset = new ArrayList<Item> ();
	  this.clas = clas;
	  this.support = 0;
      this.supportRule = 0;
	  this.per = 0;
	  this.hits = 0;
	  this.misses = 0;
  }

  /**
   * <p>
   * Clone function
   * </p>
   */
  public Itemset clone() {
    Itemset d = new Itemset(this.clas);
	for (int i=0; i < this.itemset.size(); i++)  d.add((itemset.get(i)).clone());

    d.clas = this.clas;
	d.support = this.support;
    d.supportRule = this.supportRule;
    d.per = this.per;
	d.hits = this.hits;
	d.misses = this.misses;

	return (d);
  }

  /**
   * <p>
   * Function to add an item to our itemset
   * </p>
   * @param item Element to be added
   */
  public void add (Item item) {
	  this.itemset.add(item);
  }

  /**
   * <p>
   * It returns the item located in the given position of the itemset
   * </p>
   * @param pos Position of the requested item into the itemset
   * @return Item The requested item of the itemset
   */
  public Item get (int pos) {
	  return (this.itemset.get(pos));
  }

  /**
   * <p>
   * Function to remove the item located in the given position
   * </p>
   * @param pos Position of the requested item into the itemset
   * @return Item The removed item of the itemset
   */
  public Item remove (int pos) {
	  return (this.itemset.remove(pos));
  }

  /**
   * <p>
   * It returns the size of the itemset (the number of items it has)
   * </p>
   * @return int Number of items the itemset stores
   */
  public int size () {
	  return (this.itemset.size());
  }

  /**
   * <p>
   * It returns the support of the antecedent of the itemset
   * </p>
   * @return double Support of the antecedent of the itemset
   */
  public double getSupport() {
	return (this.support);
  }

  /**
   * <p>
   * It returns the support of the itemset for its related output class
   * </p>
   * @return double Support of the itemset for its related output class
   */
  public double getSupportClass() {
	return (this.supportRule);
  }

  /**
   * <p>
   * It returns the number of hits of the itemset against the training set
   * </p>
   * @return int Number of hits of the itemset against the training set
   */
  public int getHits() {
	return (this.hits);
  }

  /**
   * <p>
   * It returns the number of misses of the itemset against the training set
   * </p>
   * @return int Number of misses of the itemset against the training set
   */
  public int getMisses() {
	return (this.misses);
  }

  /**
   * <p>
   * It returns the Pessimistic Error Rate of the itemset
   * </p>
   * @return double Pessimistic Error Rate of the itemset
   */
  public double getPer() {
	return (this.per);
  }

  /**
   * <p>
   * It returns the output class of the itemset
   * </p>
   * @return int output class of the itemset
   */
  public int getClas() {
	return (this.clas);
  }

  /**
   * <p>
   * Function which sets the itemset's output class
   * </p>
   * @param clas Itemset's output class
   */
  public void setClas(int clas) {
	this.clas = clas;
  }

  /**
   * <p>
   * Function to check if an itemset is equal to another given
   * </p>
   * @param a Itemset to compare with ours
   * @return boolean true = they are equal, false = they aren't.
   */
  public boolean isEqual(Itemset a) {
	  int i;
	  Item item;

	  if (this.itemset.size() != a.size())  return (false);

	  for (i=0; i < this.itemset.size(); i++) {
		  item = this.itemset.get(i);
		  if (!item.isEqual(a.get(i)))  return (false);
	  }

	  if (this.clas != a.getClas())  return (false);

	  return (true);
  }

  /**
   * <p>
   * Function to check if the antecedent of our itemset is equal to another given
   * </p>
   * @param a Itemset which antecedents we are going to compare with ours
   * @return boolean true = they are equal, false = they aren't.
   */
  public boolean isEqualAnt(Itemset a) {
	  int i;
	  Item item;

	  if (this.itemset.size() != a.size())  return (false);

	  for (i=0; i < this.itemset.size(); i++) {
		  item = this.itemset.get(i);
		  if (!item.isEqual(a.get(i)))  return (false);
	  }

	  return (true);
  }

  /**
   * <p>
   * Function to check if our itemset is Subitemset (can be contained) of a given itemset
   * </p>
   * @param a Itemset to check if can contain ours
   * @return boolean true = our itemset is subitemset of a, false = it isn't.
   */
  public boolean isSubItemset(Itemset a) {
	  int i, j;
	  Item itemi, itemj;
	  boolean stop;

	  if (this.clas != a.getClas())  return (false);

	  for (i=0; i < this.itemset.size(); i++) {
		  itemi = this.itemset.get(i);

		  stop = false;
		  for (j=0; j < a.itemset.size() && !stop; j++) {
			  itemj = a.itemset.get(j);
			  if (itemi.isEqual(itemj))  stop = true;
			  else if (itemj.getVariable() >= itemi.getVariable())  return (false);
		  }

		  if (!stop)  return (false);
	  }

	  return (true);
  }

  /**
   * <p>
   * It computes the support, rule support, hits, misses and PER of our itemset for a given dataset
   * </p>
   * @param train Given training dataset to be able to calculate supports
   */
  public void calculateSupports(myDataset train) {
	int i;
    double degree;

	this.support = 0.0;
    this.supportRule = 0.0;
	this.hits = 0;
	this.misses = 0;

    for (i = 0; i < train.size(); i++) {
      degree = this.degree(train.getExample(i));
	  if (degree > 0.0) {
		  this.support += degree;
		  if (train.getOutputAsInteger(i) == this.clas) {
			  this.supportRule += degree;
			  this.hits++;
		  }
		  else  this.misses++;
	  }
    }

	this.support /= train.getnData();
    this.supportRule /= train.getnData();
	this.per = (1.0 * this.misses + this.errors (this.hits + this.misses * 1.0, this.misses * 1.0, 0.25)) / (misses + hits);
  }


  private double degree(int[] example) {
    double degree;
	Item item;

    degree = 1.0;
    for (int i = 0; i < itemset.size() && degree > 0.0; i++) {
		item = itemset.get(i);
		if (item.getValue() != example[item.getVariable()])  degree = 0.0;
    }
    return (degree);
  }

  private double errors (double N, double e, double CF) {
       // Some constants for the interpolation.
       double Val[] = {0, 0.000000001, 0.00000001, 0.0000001, 0.000001, 0.00001, 0.00005, 0.0001, 0.0005, 0.001, 0.005, 0.01, 0.05, 0.10, 0.20, 0.40, 1.00};
       double Dev[] = {100, 6.0, 5.61, 5.2, 4.75, 4.26, 3.89, 3.72, 3.29, 3.09, 2.58, 2.33, 1.65, 1.28, 0.84, 0.25, 0.00};
       double Val0, Pr, Coeff;
       int i;

	   Coeff = 0;
       i = 0;

       while (CF > Val[i])  i++;

       Coeff = Dev[i-1] + (Dev[i] - Dev[i-1]) * (CF - Val[i-1]) / (Val[i] - Val[i-1]);
       Coeff = Coeff * Coeff;

       if (e < 1E-6)  return N * (1.0 - Math.exp(Math.log(CF) / N));
       else {
		   if (e < 0.9999) {
			   Val0 = N * (1 - Math.exp(Math.log(CF) / N));
               return (Val0 + e * (errors(N, 1.0, CF) - Val0));
           } 
		   else {
               if (e + 0.5 >= N)  return (0.67 * (N - e));
               else {
                   Pr = (e + 0.5 + Coeff / 2 + Math.sqrt(Coeff * ((e + 0.5) * (1 - (e + 0.5) / N) + Coeff / 4))) / (N + Coeff);
                   return (N * Pr - e);
               }
           }
       }
   } 

  /**
   * Function to compare objects of the Itemset class
   * Necessary to be able to use "sort" function
   * It sorts in an decreasing order of attribute
   * If equals, in an decreasing order of value of the attribute
   * If equals, in an decreasing order of class
   */
  public int compareTo(Object a) {
	int i;
	Item itemi, itemj;

	for (i=0; i < this.size(); i++) {
		itemi = this.itemset.get(i);
		itemj = ((Itemset) a).get(i);

		if (itemj.variable > itemi.variable)  return -1;
		else if (itemj.variable < itemi.variable)  return 1;
		else if (itemj.value > itemi.value)  return -1;
		else if (itemj.value < itemi.value)  return 1;
	}

    if (((Itemset) a).getClas() > this.getClas())  return -1;
    else if (((Itemset) a).getClas() < this.getClas())  return 1;
	else  return 0;
  }

}