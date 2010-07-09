/**
 * 
 * File: DataException.java
 * 
 * A new exception type defined to be thrown when a data set is incorrect
 * 
 * @author Written by Joaquín Derrac (University of Granada) 10/08/2008 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Lazy_Learning;

public class DataException  extends Exception{

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of CheckException
	 */
	public DataException() {
		
		super();
		
	}//end-method 


	/**
	 * Creates a new instance of DataException, by using a
	 * message to define it.
	 * 
	 * @param msg The message of the exception
	 */
	public DataException(String msg){
		
		super(msg);
		
	}//end-method 

}//end-class

