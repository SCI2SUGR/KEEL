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
 * <p>Title: Itemset</p>
 * <p>Description: This class contains the representation of a itemset</p>
 * <p>Copyright: Copyright KEEL (c) 2007</p>
 * <p>Company: KEEL </p>
 * @author Jesus Alcalá (University of Granada) 09/02/2011
 * @version 1.0
 * @since JDK1.6
 */

import java.util.*;


public class Itemset {
  ArrayList<Item> itemset;
  int clas;
  double support, supportRule;

  public Itemset() {
  }

/**
* <p>
* Builder
* </p>
* @param clas Class
* @return Return a itemset for the class clas
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

  public void add (Item item) {
	  this.itemset.add(item);
  }

  public Item get (int pos) {
	  return (this.itemset.get(pos));
  }

  public Item remove (int pos) {
	  return (this.itemset.remove(pos));
  }

  public int size () {
	  return (this.itemset.size());
  }

  public double getSupport() {
	return (this.support);
  }

  public double getSupportClass() {
	return (this.supportRule);
  }

  public int getClas() {
	return (this.clas);
  }

  public void setClas(int clas) {
	this.clas = clas;
  }


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
