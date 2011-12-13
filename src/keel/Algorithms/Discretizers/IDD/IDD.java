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

package keel.Algorithms.Discretizers.IDD;

import java.util.*;
import keel.Algorithms.Discretizers.Basic.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.Parameters;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;


/**
 * <p>
 * This class implements the IDD
 * </p>
 * 
 * <p>
 * @author Written by Jose A. Saez Munoz (SCI2S research group, DECSAI in ETSIIT, University of Granada), 21/12/2009
 * @version 1.0
 * @since JDK1.6
 * </p>
 */
public class IDD extends Discretizer {

	// rank class is used to sort values
	private class rank {
		int pos;
		double value;
		public rank(int i, double v){pos=i;value=v;}
	}
	
	// tags
	private static int HIGHER_FIRST = 0;
	private static int LOWER_FIRST = 1;

	// instance variables
	private double[] cutpoints;				// possible cutpoints
	private int numcp;						// number of possible cutpoints

	private int delta;						// neigboorhood size
	private int windowsSize;				// windows size to vote later
	private String distanceFunction;		// kind of distance function
	
	private int[][] quantaMatrixOfClasses;	// number of examples per class with value = cutpoint
	private int[] numInterAtt;				// number of intervals of each attribute
	

//******************************************************************************************************

	/**
	 * <p>
	 * Constructor of the class
	 * </p>
	 */
	public IDD(){
		
		int i;
		
		numInterAtt = new int[Parameters.numAttributes];
		
		
		if(Parameters.setConfig){

			for(i = 0 ; i < Parameters.numAttributes ; ++i){
				
				Attribute att = Attributes.getAttribute(i);
				
				if(att.getType() == Attribute.REAL || att.getType() == Attribute.INTEGER)
						numInterAtt[i] = Parameters.numIntervals;
				// default case
				else
					numInterAtt[i] = 0;
			}
		}
		
		else{
			String[] inter = Parameters.numIntrvls.split("_");
			
			int cont = 0;
			for(i = 0 ; i < Parameters.numAttributes ; ++i){
				
				Attribute att = Attributes.getAttribute(i);
				
				if(att.getType() == Attribute.REAL || att.getType() == Attribute.INTEGER)
					numInterAtt[i] = Integer.parseInt(inter[cont++]);
				
				// default case
				else
					numInterAtt[i] = 0;
			}
		}
	}

//******************************************************************************************************	

	/**
	 * <p>
	 * Returns a vector with the discretized values
	 * </p>
	 * @param attribute index of the attribute to discretize
	 * @param values vector of indexes of the instances sorted from lowest to highest value of attribute
	 * @param begin index of the instance with the lowest value of attribute
	 * @param end index of the instance with the highest value of attribute
	 * @return vector with the discretized values
	 */
	protected Vector discretizeAttribute(int attribute, int []values, int begin, int end){
		
		int i, j;	// loop indexes
		windowsSize = Parameters.WindowsSize;
		delta = Parameters.Neighborhood;
		distanceFunction = Parameters.DistanceFunction;
		
		// inicialize parameters
		cutpoints = new double[end+1];
		numcp = 0;
				
		quantaMatrixOfClasses = new int[end+1][Parameters.numClasses];
		for(i = 0 ; i < end+1 ; ++i)
			for(j = 0 ; j < Parameters.numClasses ; ++j)
				quantaMatrixOfClasses[i][j] = 0;
		
		
		//get the differents values of attribute and his classes
		quantaMatrixOfClasses[numcp][classOfInstances[values[begin]]]++;
		double value = realValues[attribute][values[begin]];
		cutpoints[numcp++] = value;
		
		for(i = begin+1 ; i <= end ; ++i){
			
			if(value != realValues[attribute][values[i]]){
				quantaMatrixOfClasses[numcp][classOfInstances[values[i]]]++;
				cutpoints[numcp++] = realValues[attribute][values[i]];
				value = realValues[attribute][values[i]];
			}
			
			else
				quantaMatrixOfClasses[numcp-1][classOfInstances[values[i]]]++;
		}

		
		double max;
		int index;
		double[] fitness = new double[numcp];
		int[] votes = new int[numcp];
		
		for(i = 0 ; i < numcp ; ++i)
			fitness[i] = votes[i] = 0;


		// compute fitness for all the cutpoints
		for(i = delta ; i < numcp-delta ; ++i)
			fitness[i] = distance(i, attribute);
		
		// compute votes for each cutpoint
		for(i = 1 ; i < numcp-delta ; ++i){
			
			max = fitness[i];
			index = i;
			
			for(j = i+1 ; j < i+windowsSize ; ++j){

				if(fitness[j] > max){
					max = fitness[j];
					index = j;
				}
			}
			
			votes[index]++;
		}
		
		//return cutpoints
		Vector cp = new Vector();
		
		if(numInterAtt[attribute] == 0){
			for(i = 0 ; i < numcp ; ++i){
				if(votes[i] == windowsSize)
					cp.add(cutpoints[i]);
			}
		}
		
		// return the cutpoints with more votes
		else{
			
			// get the numIntervals highest cutpoints basis on votes
			rank[] r = new rank[numcp];
			for(i = 0 ; i < r.length ; ++i)
				r[i] = new rank(i,votes[i]*fitness[i]);
			
			sortValues(r,0,numcp-1,HIGHER_FIRST);

			// sort these cutpoints
			int numfinalcp = numInterAtt[attribute]-1;
			rank[] r1 = new rank[numfinalcp];
			for(i = 0 ; i < numfinalcp ; ++i)
				r1[i] = new rank(i,cutpoints[r[i].pos]);
			
			sortValues(r1,0,numfinalcp-1,LOWER_FIRST);
			
			for(i = 0 ; i < numfinalcp ; ++i)
				cp.add(r1[i].value);
		}
		
		return cp;
	}
	
//******************************************************************************************************
	
	/**
	 * <p>
	 * Computes the distance between two intervals
	 * </p>
	 * @param i index of the cutpoint
	 * @param attribute index of the attribute
	 * @return the distance value
	 */
	private double distance(int i, int attribute){
		
		if(distanceFunction.equals("nominalOutput1"))
			return NominalClassesDistance_1(i, attribute);
		
		if(distanceFunction.equals("nominalOutput2"))
			return NominalClassesDistance_2(i, attribute);
		
		return 0;
	}
	
//******************************************************************************************************

	/**
	 * <p>
	 * Computes the distance between two intervals as follows: first, finds the majority class at left
	 * interval and counts the number of instances at right interval that are not of this class. Later,
	 * finds the majority class at right interval and counts the number of instances at left interval
	 * that are not of this class. At last, returns the minimum of these two values.
	 * </p>
	 * @param i the index of the cutpoint
	 * @param attribute index of the attribute
	 * @return the distance between the two intervals
	 */
	private double NominalClassesDistance_1(int i, int attribute){
		
		int j, fi, si, s;								// loop indexes
		int beginFirst = i-delta, beginSecond = i+1;	// begin of first and second intervals
		
		int[][] numClassesAtInterval = new int[2][Parameters.numClasses];
		for(j = 0 ; j < Parameters.numClasses ; ++j){
			numClassesAtInterval[0][j] = 0;
			numClassesAtInterval[1][j] = 0;
		}
		
		// compute the majority class in first interval and second interval
		for(fi = beginFirst, si = beginSecond ; fi < beginFirst+delta ; ++fi, ++si){
			
			for(s = 0 ; s < Parameters.numClasses ; ++s){
				
				numClassesAtInterval[0][s] += quantaMatrixOfClasses[fi][s];
				numClassesAtInterval[1][s] += quantaMatrixOfClasses[si][s];
			}
		}
		
		// compute the number of instances for majority class
		int numFirst = numClassesAtInterval[0][0], numSecond = numClassesAtInterval[1][0];
		int totalFirst = numClassesAtInterval[0][0] , totalSecond = numClassesAtInterval[1][0];
		int pos1 = 0 , pos2 = 0;
		
		for(j = 1 ; j < Parameters.numClasses ; ++j){
			
			totalFirst += numClassesAtInterval[0][j];
			totalSecond += numClassesAtInterval[1][j];
			
			if(numClassesAtInterval[0][j] > numFirst){
				numFirst = numClassesAtInterval[0][j];
				pos1 = j;
			}
			
			if(numClassesAtInterval[1][j] > numSecond){
				numSecond = numClassesAtInterval[1][j];
				pos2 = j;
			}
		}
		
		// compute the distance
		double distance1 = totalSecond-numClassesAtInterval[1][pos1];
		double distance2 = totalFirst-numClassesAtInterval[0][pos2];
		
		double distance = distance1<distance2?distance1:distance2;
	
		return distance;
	}
	
//******************************************************************************************************
	
	/**
	 * <p>
	 * Computes the distance between two intervals as follows: first, it computes the number of instances
	 * of each class into each interval. Then, computes the euclidean distance between these arrays of number
	 * of instances
	 * </p>
	 * @param i the index of the cutpoint
	 * @param attribute index of the attribute
	 * @return the distance between the two intervals
	 */
	private double NominalClassesDistance_2(int i, int attribute){
		
		int fi, si, s;									// loop indexes
		int beginFirst = i-delta, beginSecond = i+1;	// begin of first and second intervals
		
		int[][] numClassesAtInterval = new int[2][Parameters.numClasses];
		for(int j = 0 ; j < Parameters.numClasses ; ++j){
			numClassesAtInterval[0][j] = 0;
			numClassesAtInterval[1][j] = 0;
		}
		
		// compute the majority class in first interval and second interval
		for(fi = beginFirst, si = beginSecond ; fi < beginFirst+delta ; ++fi, ++si){
			
			for(s = 0 ; s < Parameters.numClasses ; ++s){
				
				numClassesAtInterval[0][s] += quantaMatrixOfClasses[fi][s];
				numClassesAtInterval[1][s] += quantaMatrixOfClasses[si][s];
			}
		}
		
		// compute the distance
		double distance = 0;
		for(s = 0 ; s < Parameters.numClasses ; ++s)
			distance += Math.pow(numClassesAtInterval[0][s]-numClassesAtInterval[1][s],2);
	
		return Math.sqrt(distance);
	}
	
//******************************************************************************************************

	  
	/**
	 * <p>
	 * Sorts an array
 	 * </p>
 	 * @param values array to sort
	 * @param begin start position to sort
	 * @param end end position to sort
	 * @param type = HIGHER_FIRST (it sorts from highest to lowest), type = LOWER_FIRST (it sorts from lowest
	 * to higest)
	 */
	protected void sortValues(rank[] values, int begin, int end, int type){
	
		double pivot;
		rank temp;
		int i, j;

		i = begin;
		j = end;
		pivot = values[(i+j)/2].value;
			

		do {
			
			if(type == HIGHER_FIRST){
				while(values[i].value > pivot) i++;
				while(values[j].value < pivot) j--;
			}
			
			if(type == LOWER_FIRST){
				while(values[i].value < pivot) i++;
				while(values[j].value > pivot) j--;
			}
			
			
			if(i <= j){
				if(i < j){
					temp = values[i];
					values[i] = values[j];
					values[j] = temp;
				}
					
				i++;
				j--;	
			}
		}while(i <= j);
			
		if(begin < j) sortValues(values,begin,j,type);
		if(i < end) sortValues(values,i,end,type);	
	}
	
}
