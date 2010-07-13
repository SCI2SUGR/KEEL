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
 * File: Pair.java
 *
 * Auxiliary class to repressent pairs of rules and its distance
 *
 * @author Written by Joaquin Derrac (University of Granada) 17/10/2009
 * @version 1.1
 * @since JDK1.5
 *
 */
package keel.Algorithms.Hyperrectangles.INNER;

public class Pair implements Comparable{
	
	private int ruleA;       //first rule
	private int ruleB;       //second rule
	private double distance; //distance between them
	
	/**
	 * Builder.
	 *
	 * @param a Identifier of the first rule
	 * @param b Identifier of the second rule
	 * @param dist Distance between rules
	 */
	public Pair (int a, int b, double dist){
		
		ruleA=a;
		ruleB=b;
		distance=dist;
	}//end-method
	
	/**
	* Returns the distance between rules
	*
	* @return Distance
	*/
	public double dist(){
		return distance;
	}//end-method
	
	/**
	* Returns the first rule of the pair
	*
	* @return Identifier of the first rule
	*/	
	public int A(){
		return ruleA;
	}//end-method
	
	/**
	* Returns the second rule of the pair
	*
	* @return Identifier of the second rule
	*/	
	public int B(){
		return ruleB;
	}//end-method	
	
	/**
	* Compare to method: Compare two pairs of rules regarding its distance 
	*
	* @return Order of the two pairs
	*/	
	public int compareTo(Object o) {
        Pair dir = (Pair)o;
        if(this.distance < dir.distance)
            return -1;
        else if(this.distance == dir.distance)
            return 0;
        else
            return 1;
	}//end-method
	
}//end-class

