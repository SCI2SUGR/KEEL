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


import java.util.*;

/**
 * <p>Title: Itemset</p>
 * <p>Description: This class contains the representation of a itemset</p>
 * <p>Copyright: Copyright KEEL (c) 2007</p>
 * <p>Company: KEEL </p>
 * @author Jesus Alcalá (University of Granada) 09/02/2011
 * @version 1.0
 * @since JDK1.6
 */
public class Itemset {
  ArrayList<Item> itemset;
  int clas;
  double support, supportRule;

    /**
     * Default constructor. 
     * None attribute will be initialized.
     */
    public Itemset() {
  }

/**
* <p>
* Builder
* </p>
* @param clas Class
*/
  public Itemset(int clas) {
      this.itemset = new ArrayList<Item> ();
	  this.clas = clas;
	  this.support = 0;
      this.supportRule = 0;
  }

/**
* <p>
* Clone
* </p>
* @return Return a copy of the itemset
*/
  public Itemset clone() {
    Itemset d = new Itemset(this.clas);
	for (int i=0; i < this.itemset.size(); i++)  d.add((itemset.get(i)).clone());

    d.clas = this.clas;
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
     * Set the class with the value given as argument.
     * @param clas class given.
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
	  if (this.clas != a.getClas())  return (false);

	  for (i=0; i < this.itemset.size(); i++) {
		  item = this.itemset.get(i);
		  if (!item.isEqual(a.get(i)))  return (false);
	  }

	  return (true);
  }

    /**
   * <p>
   * It computes the support, rule support, hits, misses and PER of our itemset for a given dataset
   * </p>
   * @param dataBase Given training dataset useful information to calculate supports.
   * @param train Given training dataset to be able to calculate supports.
   */
    public void calculateSupports(DataBase dataBase, myDataset train) {
	int i;
    double degree;

	this.support = 0.0;
    this.supportRule = 0.0;

    for (i = 0; i < train.size(); i++) {
      degree = this.degree(dataBase, train.getExample(i));
      this.support += degree;
      if (train.getOutputAsInteger(i) == this.clas)  this.supportRule += degree;
    }

	this.support /= train.getnData();
    this.supportRule /= train.getnData();
  }

    /**
     * Calculate the degree of the given example inside the given data-set.
     * @param dataBase Given training dataset useful information to calculate the degree.
     * @param ejemplo Given example to calculate its degree.
     * @return
     */
    public double degree(DataBase dataBase, double[] ejemplo) {
    return (degreeProduct(dataBase, ejemplo));
  }


  private double degreeProduct(DataBase dataBase, double[] example) {
    double degree;
	Item item;

    degree = 1.0;
    for (int i = 0; i < itemset.size() && degree > 0.0; i++) {
		item = itemset.get(i);
		degree *= dataBase.matching(item.getVariable(), item.getValue(), example[item.getVariable()]);
    }
    return (degree);
  }

}
