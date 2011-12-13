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

package keel.Algorithms.Discretizers.OneR;

import java.util.Arrays;


/**
 * <p>
 * This class represents the optimum class for a given explanatory value
 * </p>
 * 
 * <p>
 * @author Written by Julián Luengo Martín 28/10/2008
 * @version 0.1
 * @since JDK 1.5
 * </p>
 */
public class Opt {
	double _value;
	int count[];
	int prior[];
	boolean clean;
	int max;
	
	public Opt(){
		_value = Double.NaN;
		count = null;
		clean = false;
		max = -1;
	}
	
	/**
	 * Creates a new object with the given elements
	 * @param value the explanatory value
	 * @param numClasses the total number of different classes
	 */
	public Opt(double value,int numClasses){
		_value = value;
		count = new int[numClasses];
		clean = false;
		prior = new int[numClasses];
	}
	
	/**
	 * Sets the priority of the classes associated to this explanatory value (the less, the more priority)
	 * @param p array with the priority associated to each class
	 */
	public void setPrior(int p[]){
		prior = Arrays.copyOf(p, p.length);
		
	}
	
	/**
	 * Increases the count for the class indicated
	 * @param index the class for which the count will be incremented by one
	 */
	public void countClass(int index){
		count[index]++;
		clean = false;
	}

	/**
	 * Computes the optimum class for the explanatory value
	 * @return the optimum class found
	 */
	public int getOptClass(){
		if(!clean){
			max = 0;
			for(int i=1;i<count.length;i++){
				if(count[i]>max)
					max = i;
				else if(prior[i]<prior[max])
					max = i;

			}
			clean = true;
		}
		return max;
	}
	
	/**
	 * Gets the explanatory value associated
	 * @return the explanatory value associated
	 */
	public double getValue(){
		return _value;
	}
	
}

