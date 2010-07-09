/** 
* <p> 
* @author Written by Luciano Sánchez (University of Oviedo) 08/03/2004
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Shared.Exceptions;


public class invalidFitness extends Exception{
	/**
	  * <p>
	  * This exception is to report an invalid fitness function.
	  * </p>
	  */
	 /**
	  * <p>
	  * Creates an new invalidFitness object calling the super() method();
	  * 
	  * </p>	
	  */
 public  invalidFitness() { super(); }
 /**
  * <p>
  * 
  * Creates an new invalidFitness object calling the super(s) method() with the report string s.
  * </p>	
  *
  * @param s the report String.
  */
 public  invalidFitness(String s) { super(s); }
}
