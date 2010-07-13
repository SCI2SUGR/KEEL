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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.Apriori;

/**
 * <p>
 * @author Written by Nicolò Flugy Papè (Politecnico di Milano) 24/03/2009
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

import java.util.*;

public class Item {
  /**
   * <p>
   * It represents an item throughout the execution of the algorithm
   * </p>
   */
	
  private int label;
  private int support;
  private ArrayList<Item> children;
  
  /**
   * <p>
   * It creates a new item by setting up its label
   * </p>
   * @param label The label of the item
   */
  public Item(int label) {
    this.label = label;
    this.support = 0;
    this.children = new ArrayList<Item>();
  }
  
  /**
   * <p>
   * It sets the label associated with an item
   * </p>
   * @param label The label of the item
   */ 
  public void setLabel(int label) {
    this.label = label;
  }
  
  /**
   * <p>
   * It returns the label associated with an item
   * </p>
   * @return A value representing the label of the item
   */
  public int getLabel() {
    return this.label;
  }
  
  /**
   * <p>
   * It increments the support of an item
   * </p>
   */  
  public void incSupport() {
    this.support++;
  }
  
  /**
   * <p>
   * It returns the support of an item
   * </p>
   * @return A value representing the support of the item
   */ 
  public int getSupport() {
    return this.support;
  }
  
  /**
   * <p>
   * It adds a child item to a parent item
   * </p>
   * @param child The item to add to this item
   */
  public void addChild(Item child) {
    this.children.add(child);
  }
  
  /**
   * <p>
   * It returns whether an item has children items
   * </p>
   * @return True if this item has children items; False otherwise
   */
  public boolean hasChildren() { 
   	 return (! this.children.isEmpty());
  }
  
  /**
   * <p>
   * It returns the children of an item
   * </p>
   * @return An array of items representing the children of this item
   */
  public ArrayList<Item> getChildren() {
    return this.children;
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
    
    if (item.label == this.label) return true; 
    else return false;
  }
  
}

