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

//
//  ReferenciaMNV.java
//
//  Salvador García López
//
//  Created by Salvador García López 25-2-2008.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.MNV;

/**
 * 
 * File: ReferenciaMNV.java
 * 
 * Simple structure used on MNV algorithm.
 * 
 * @author Written by Salvador García (University of Granada) 20/07/2004 
 * @version 0.1 
 * @since JDK1.5
 * 
 */
public class ReferenciaMNV implements Comparable {
	
    /**
     * Integer value.
     */
    public int entero;

    /**
     * Real value.
     */
    public double real;

    /**
     * Distance. 
     */
    public double dist;

    /**
     * Default constructor.
     */
  public ReferenciaMNV () {}

  /**
     * Parameters constructor.
     * @param a integer value to be set.
     * @param b real value to be set.
     * @param c distance to be set.
     */
  public ReferenciaMNV (int a, double b, double c) {
    entero = a;
    real = b;
    dist = c;
  }

  public int compareTo (Object o1) {
    if (this.real > ((ReferenciaMNV)o1).real)
      return 1;
    else if (this.real < ((ReferenciaMNV)o1).real)
      return -1;
    else if (this.dist > ((ReferenciaMNV)o1).dist)
    	return 1;
    else if (this.dist < ((ReferenciaMNV)o1).dist)
    	return -1;
    else return 0;
  }

  public String toString () {
    return new String ("{"+entero+", "+real+", "+dist+"}");
  }
}

