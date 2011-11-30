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

package keel.Algorithms.Associative_Classification.ClassifierFuzzyCFAR;

import java.util.*;

/**
 * This class contains the representation of a itemset
 *
 * @author Jesus Alcalá (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */
public class Itemset {
  ArrayList<Item> itemset;
  int clas;
  double MS, support, supportRule;


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
   * @param MS Cut threshold
   */
  public Itemset(int clas, double MS) {
      this.itemset = new ArrayList<Item> ();
	  this.clas = clas;
	  this.MS = MS;
	  this.support = 0;
      this.supportRule = 0;
  }


  /**
   * <p>
   * Clone function
   * </p>
   */
  public Itemset clone() {
    Itemset d = new Itemset(this.clas, this.MS);
	for (int i=0; i < this.itemset.size(); i++)  d.add((itemset.get(i)).clone());

    d.clas = this.clas;
	d.MS = this.MS;
	d.support = this.support;
    d.supportRule = this.supportRule;

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
   * @return The requested item of the itemset
   */
  public Item get (int pos) {
	  return (this.itemset.get(pos));
  }


  /**
   * <p>
   * Function to remove the item located in the given position
   * </p>
   * @param pos Position of the requested item into the itemset
   * @return The removed item of the itemset
   */
  public Item remove (int pos) {
	  return (this.itemset.remove(pos));
  }


  /**
   * <p>
   * It returns the size of the itemset (the number of items it has)
   * </p>
   * @return Number of items the itemset stores
   */
  public int size () {
	  return (this.itemset.size());
  }


  /**
   * <p>
   * It returns the support of the antecedent of the itemset
   * </p>
   * @return Support of the antecedent of the itemset
   */
  public double getSupport() {
	return (this.support);
  }


  /**
   * <p>
   * It returns the support of the itemset for its related output class
   * </p>
   * @return Support of the itemset for its related output class
   */
  public double getSupportClass() {
	return (this.supportRule);
  }


  /**
   * <p>
   * It returns the output class of the itemset
   * </p>
   * @return output class of the itemset
   */
  public int getClas() {
	return (this.clas);
  }


  /**
   * <p>
   * It sets the cut threshold
   * </p>
   */
  public void setMS(double value) {
	this.MS = value;
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
   * It computes the support, rule support, hits, misses and PER of our itemset for a given dataset
   * </p>
   * @param train Given training dataset to be able to calculate supports
   */
  public void calculateSupports(myDataset train) {
	int i;
    double degree;

	this.support = 0.0;
    this.supportRule = 0.0;

    for (i = 0; i < train.size(); i++) {
      degree = this.degree(train.getExampleXf(i));
      if (degree > this.MS) {
        this.support += degree;
		if (train.getOutputAsInteger(i) == this.clas)  this.supportRule += degree;
      }
    }

	this.support /= train.getnData();
    this.supportRule /= train.getnData();
  }


  private double degree(double[][] ejemplo) {
    return (degreeMinimum(ejemplo));
  }


  private double degreeMinimum(double[][] example) {
    double minimum;
	Item item;

    minimum = 1.0;
    for (int i = 0; i < itemset.size(); i++) {
		item = itemset.get(i);
		minimum = Math.min(minimum, example[item.getVariable()][item.getValue()]);
    }
    return (minimum);
  }

}
