/**
 * 
 * File: Referencia.java
 * 
 * An auxiliary class to manage references between two values
 * 
 * @author Written by Salvador García (University of Granada) 20/07/2004 
 * @version 0.1 
 * @since JDK1.5
 * 
 */
package keel.Algorithms.Preprocess.Basic;

public class Referencia implements Comparable {

	//values of the reference
	public int entero;
	public double real;

	/**
	 * Default builder
	 */
	public Referencia () {} //end-method
	
	/**
	 * Builder
	 *
	 * @param a Integer value
	 * @param b Double value
	 */
	public Referencia (int a, double b) {

		entero = a;
		real = b;

	}//end-method
	
	/**
	 * Compare to Method
	 *
	 * @param o1 Reference to compare
	 *
	 * @return Relative order between the references
	 */
	public int compareTo (Object o1) {

		if (this.real > ((Referencia)o1).real)
		  return -1;
		else if (this.real < ((Referencia)o1).real)
		  return 1;
		else return 0;

	}//end-method

	/**
	 * To String Method
	 *
	 * @return String representation of the chromosome
	 */
	public String toString () {

		return new String ("{"+entero+", "+real+"}");

	}//end-method
	
}//end-class

