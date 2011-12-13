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

package keel.Algorithms.Discretizers.MVD;

import java.util.ArrayList;
import java.util.Collections;
import keel.Dataset.Instance;


/**
 * Simple class to codify an interval for a numerical attribute
 * 
 * <p>
 * @author Written by Julian Luengo Martin (SCI2S research group, DECSAI in ETSIIT, University of Granada), 19/04/2011
 * @version 1.0
 * @since JDK1.6
 * </p>
 */
public class Interval {

	public double lowerbound;
	public double upperbound;
	
	int attribute; //the attribute to which this interval is related to
	
	ArrayList<Integer> coveredInstances; //indices of the instances in the data set covered by this interval
	
	/**
	 * Parametrized constructor
	 * @param low lower bound of the interval
	 * @param up upper bound of the interval
	 * @param _attribute the attribute in which this interval is located
	 */
	public Interval(double low,double up, int _attribute){
		lowerbound = low;
		upperbound = up;
		
		attribute = _attribute;
		
		coveredInstances = new ArrayList<Integer>();
	}
	
	/**
	 * Checks if the provided instance is covered by this interval in the specified attribute
	 * @param inst
	 */
	public boolean covers(Instance inst){
		double num = inst.getAllInputValues()[attribute]; 
		
		if(num < upperbound && num >= lowerbound)
			return true;
		else
			return false;
	}
	
	/**
	 * Checks if the provided instance is covered by this interval in the specified attribute
	 * @param index
	 */
	public boolean covers(int index){
		
		int i = Collections.binarySearch(coveredInstances, index);
		
		if(i >= 0)
			return true;
		else
			return false;
	}
	
	/**
	 * Adds the index of the instance to the list of covered instances.
	 * BEWARE! it does not check for repeated indices!
	 * @param index the index of the instance to be stored
	 */
	public void addToCoveredInstances(int index){
		coveredInstances.add(index);
	}
	
	/**
	 * Merges the interval with the provided one
	 * @param _int the interval to be merged with
	 */
	public void mergeIntervals(Interval _int){
		
		lowerbound = Math.min(this.lowerbound, _int.lowerbound);
		upperbound = Math.max(this.upperbound, _int.upperbound);
		
		coveredInstances.addAll(_int.coveredInstances);
		Collections.sort(coveredInstances);
	}
	
	/**
	 * Provides the number of instances covered by this interval
	 * @return the number of covered instances (NOT the percentage)
	 */
	public int support(){
		return coveredInstances.size();
	}
	
	/**
	 * Provides the indexes of the instances in the data set covered by this interval
	 * 
	 * @return the indexes of the instances in the data set covered by this interval
	 */
	public ArrayList<Integer> getCoveredInstances(){
		return coveredInstances;
	}
	
	/**
	 * Returns the object in form of string
	 */
	public String toString(){
		return "["+this.lowerbound+","+this.upperbound+"]";
	}
	
}