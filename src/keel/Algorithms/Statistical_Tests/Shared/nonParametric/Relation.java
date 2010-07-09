/**
 * File: Relation.java
 * 
 * This class defines a relation between two integers.
 * 
 * @author Written by Joaquín Derrac (University of Granada) 29/04/2010
 * @version 1.1 
 * @since JDK1.5
*/
package keel.Algorithms.Statistical_Tests.Shared.nonParametric;

public class Relation {

  public int i; //first element
  public int j; //second element

  /**
  * Default builder
  */
  public Relation() {

  }//end-method

  /**
  * Builder
  *
  * @param x First integer
  * @param y Second integer
  */
  public Relation(int x, int y) {
    i = x;
    j = y;
  }//end-method
  
  /**
  * To string method
  *
  * @return A string representing the Relation
  */
  public String toString() {
	  return "("+i+","+j+")";	  
  }//end-method

}//end-class