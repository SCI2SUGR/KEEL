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

package keel.Algorithms.Discretizers.MODL;

import java.util.ArrayList;


/**
 * This class represents an operation of the post-optimization of the MODL discretization algorithm
 * 
 * <p>
 * @author Written by Julián Luengo Martín 07/05/2008
 * @version 0.
 * @since JDK 1.5
 * </p>
 */
public class Neighbour implements Comparable {
	public double cost = 0; //cost of the operation
	public int type = -1; //type: Split, MergeSplit or MergeMergeSplit
	int index= -1; //index of the split
	ArrayList<Double> interval = null; //reference to the first interval of the operation
	int intervalPosition = -1; //position of the interval in the list of all intervals
	
	final static int Split = 1;
	final static int MergeSplit = 2;
	final static int MergeMergeSplit = 3;
	
	public int compareTo(Object o){
		Neighbour n = (Neighbour) o;
		
		if(this.cost < n.cost)
			return -1;
		if(this.cost > n.cost)
			return 1;
		return 0;
	}
}

