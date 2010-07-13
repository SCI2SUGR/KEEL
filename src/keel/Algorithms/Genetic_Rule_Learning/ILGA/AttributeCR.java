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
 * <p>
 * @author Written by Julián Luengo Martín 08/02/2007
 * @version 0.1
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Genetic_Rule_Learning.ILGA;

/**
 * <p>
 * This class implements the relation between an attribute and its classification rate
 * for sorting purposes
 * </p>
 */
public class AttributeCR implements Comparable{
	
	public int attribute;
	public double CR;
	
	/**
	 * <p>
	 * Assigns the attribute number and the CR to this object
	 * </p>
	 * @param att the reference attribute
	 * @param CR the CR to be asigned
	 */
	public AttributeCR(int att,double CR){
		attribute = att;
		this.CR = CR;
	}
	
	/**
	 * Implementation of the method compareTo for sorting (by CR)
	 */
	public int compareTo(Object o){
		AttributeCR acr = (AttributeCR) o;
		
		if(this.CR < acr.CR)
			return -1;
		if(this.CR > acr.CR)
			return 1;
		return 0;
	}

}

