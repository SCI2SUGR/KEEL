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
 * File: DataException.java
 * 
 * A new exception type defined to be thrown when a data set is incorrect
 * 
 * @author Written by Joaquín Derrac (University of Granada) 10/08/2008 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Hyperrectangles.Basic;

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


