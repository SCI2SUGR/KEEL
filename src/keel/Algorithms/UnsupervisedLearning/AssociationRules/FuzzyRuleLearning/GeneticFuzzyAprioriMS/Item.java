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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.GeneticFuzzyAprioriMS;

/**
 * <p>
 * @author Written by Alvaro Lopez
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

public class Item implements Comparable {
  /**
   * <p>
   * It represents an item throughout the execution of the algorithm
   * </p>
   */
  
  private int idAttr;
  private int idLabel;
  
  /**
   * <p>
   * Default constructor
   * </p>
   */
  public Item() {
  }
  
  /**
   * <p>
   * It creates a new item by setting up its parameters
   * </p>
   * @param idAttr The ID of the attribute which the item represents
   * @param idLabel The ID of the label representing the involved fuzzy region
   */
  public Item(int idAttr, int idLabel) {
	  this.idAttr = idAttr;
	  this.idLabel = idLabel;
  }
  
  /**
   * <p>
   * It returns the ID of the attribute involved in the item
   * </p>
   * @return A value representing the ID of the attribute involved in the item
   */
  public int getIDAttribute() {
	  return this.idAttr;
  }

  /**
   * <p>
   * It returns the ID of the label involved in the item
   * </p>
   * @return A value representing the ID of the label involved in the item
   */
  public int getIDLabel() {
	  return this.idLabel;
  }

  /**
   * <p>
   * It allows to clone correctly an item
   * </p>
   * @return A copy of the item
   */
  public Item clone() {
    Item i = new Item();
    
    i.idAttr = this.idAttr;
    i.idLabel = this.idLabel;

	return i;
  }
  
  /**
   * <p>
   * It indicates whether some other item is "equal to" this one
   * </p>
   * @param obj The reference object with which to compare
   * @return True if this item is the same as the argument; False otherwise
   */
  public boolean equals(Object obj) {
	  Item item = (Item)obj;
	  
	  if ( (this.idAttr == item.idAttr) && (this.idLabel == item.idLabel) ) return true;
	  else return false;
  }
  
  /**
   * <p>
   * It compares an item with another one in order to accomplish ordering later.
   * </p>
   * @param item The object to be compared
   * @return A negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object
   */
  public int compareTo(Object item) {
    if (((Item) item).idAttr > this.idAttr) {
      return -1;
    }
    else if (((Item) item).idAttr < this.idAttr) {
      return 1;
    }
    else if (((Item) item).idLabel > this.idLabel) {
      return -1;
    }
    else if (((Item) item).idLabel < this.idLabel) {
      return 1;
    }

    return 0;
  }
  
  /**
   * <p>
   * It returns a raw string representation of an item
   * </p>
   * @return A raw string representation of the item
   */
  public String toString() {
	return ( "(" + this.idAttr + ", " + this.idLabel + ")" );
  }

}