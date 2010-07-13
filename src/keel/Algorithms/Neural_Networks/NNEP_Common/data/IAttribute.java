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

package keel.Algorithms.Neural_Networks.NNEP_Common.data;

/**
 * <p>
 * @author Written by Amelia Zafra, Sebastian Ventura (University of Cordoba) 17/07/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public interface IAttribute
{

/**
 * <p>
 * Dataset Attributes
 * </p>
 */
	
	// Attribute information
	
	/**
	 * <p>
	 * Access to the attribute name
	 * </p> 
	 * @return attribute name
	 */
	public String getName();
	
	/**
	 * <p>
	 * Access to the attribute type
	 * </p>
	 * @return attribute name
	 */
	public AttributeType getType();
	
	// Checking values
	
	/**
	 * <p>
	 * Check if this internal attribute value is valid
	 * </p>
	 * @return true|false
	 */	
	public boolean isValid(double internalValue);

	/**
	 * <p>
	 * Check if this external attribute value is valid
	 * </p>
	 * @param externalValue Value to check
	 * @return true|false
	 */	
	public boolean isValid(Object externalValue);

	// Parsing and showing values
	
	/**
	 * <p>
	 * Show an String which represents a given real value
	 * </p>
	 * @return The real value of the attribute
	 */	
	public String show(double internalValue);
	
	/**
	 * <p>
	 * Parse an  external value to obtain  the internal value of the 
	 * Attribute
	 * </p>
	 * @return The external value of the attribute
	 */
	public double parse(String externalValue);
	
	/**
	 * Return the interval of correct values
	 * 
	 * @param value New value
	 */
	//public Interval intervalValues();
}

