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


