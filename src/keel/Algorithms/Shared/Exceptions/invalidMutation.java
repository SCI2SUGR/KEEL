/** 
* <p> 
* @author Written by Luciano Sánchez (University of Oviedo) 26/02/2004
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Shared.Exceptions;

public class invalidMutation extends Exception {
	/**
	  * <p>
	  * This exception is to report an invalid mutation operator.
	  * </p>
	  */
	 /**
	  * <p>
	  * Creates an new invalidMutation object calling the super() method();
	  * 
	  * </p>	
	  */
	
	public  invalidMutation() { super(); }
	/**
	  * <p>
	  * 
	  * Creates an new invalidMutation object calling the super(s) method() with the report string s.
	  * </p>	
	  *
	  * @param s the report String.
	  */
 public  invalidMutation(String s) { super(s); }
}
