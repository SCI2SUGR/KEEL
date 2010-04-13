/** 
* <p> 
* @author Written by Luciano Sánchez (University of Oviedo) 26/02/2004
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Shared.Exceptions;



public class invalidCrossover extends Exception {
 /**
  * <p>
  * This exception is to report an invalid crossover operator.
  * </p>
  */
 /**
  * <p>
  * Creates an new invalidCrossover object calling the super() method();
  * 
  * </p>	
  */
 public  invalidCrossover() { super(); }
 /**
  * <p>
  * 
  * Creates an new invalidCrossover object calling the super(s) method() with the report string s.
  * </p>	
  *
  * @param s the report String.
  */
 public  invalidCrossover(String s) { super(s); }
}

