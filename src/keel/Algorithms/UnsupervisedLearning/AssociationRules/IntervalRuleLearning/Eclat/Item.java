package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.Eclat;

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
   * It sets the support of an item
   * </p>
   * @param support The support of the item
   */ 
  public void setSupport(int support) {
    this.support = support;
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
