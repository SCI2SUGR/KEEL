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

package keel.Algorithms.Discretizers.HellingerBD;

import java.util.*;
import keel.Algorithms.Discretizers.Basic.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.Parameters;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;

/**
 * <p>
 * This class implements the HellingerBD discretizer
 * </p>
 * 
 * @author Written by Jose A. Saez (University of Granada), 12/21/2009
 * @version 1.0
 * @since JDK1.6
 */
public class HellingerBD extends Discretizer {
	
	private double[] cutpoints;			// possible cutpoints
	private double[] classProb;			// probability of occurrence of each distinct class
	private int[] selected;				// selected cutpoints
	private int numInstances;			// total number of instances
	private double[] entropyIntrvl;		// entropy of each interval
	private int[] numInterAtt;

	
//******************************************************************************************************

	/**
	 * <p>
	 * Constructor of the class
	 * </p>
	 */
	public HellingerBD(){
		
		int i;
		
		numInterAtt = new int[Parameters.numAttributes];
		
		
		if(Parameters.setConfig){

			for(i = 0 ; i < Parameters.numAttributes ; ++i){
				
				Attribute att = Attributes.getAttribute(i);
				
				if(att.getType() == Attribute.REAL || att.getType() == Attribute.INTEGER)
					if (Parameters.numIntervals > 0)
						numInterAtt[i] = Parameters.numIntervals;
					else
						numInterAtt[i] = (Parameters.numInstances / (100)) > Parameters.numClasses?Parameters.numInstances / (100):Parameters.numClasses;
				
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
	 * It returns a vector with the discretized values
	 * </p>
	 * @param attribute index of the attribute to discretize
	 * @param values vector of the indexes of the instances sorted from the lowest to the highest value of attribute
	 * @param begin index of the instance with the lowest value of attribute
	 * @param end index of the instance with the highest value of attribute
	 * @return vector with the discretized values
	 */
	protected Vector discretizeAttribute(int attribute, int []values, int begin, int end){
		
		int i;
		numInstances = realValues[attribute].length; //number of instances

		
		// 1) Form a set of all distinct values in ascending order
		double[] valuesNoRepeated = new double[end+1];
		int size = 0;
		
		double value = realValues[attribute][values[begin]];
		valuesNoRepeated[size++] = value;
		
		for(i = begin+1 ; i <= end ; ++i){
			if(value != realValues[attribute][values[i]]){
				valuesNoRepeated[size++] = realValues[attribute][values[i]];
				value = realValues[attribute][values[i]];
			}
		}
		
		
		// 2) Calculate the midpoints of all the adjacent pairs in the set
		int numcp = size+1;				// midpoints + minimun + maximun
		cutpoints = new double[numcp];			
		
		cutpoints[0] = realValues[attribute][values[begin]];		
		cutpoints[numcp-1] = realValues[attribute][values[end]];
		
		for(i = 1 ; i < numcp-1 ; ++i)
			cutpoints[i] = (valuesNoRepeated[i-1]+valuesNoRepeated[i])/2;
		
		
		// 3) Compute the probability of each class
		classProb = new double[Parameters.numClasses];
		for(i = 0 ; i < Parameters.numClasses ; ++i)
			classProb[i] = 0;
		
		for(i = 0 ; i < numInstances ; ++i)
			classProb[classOfInstances[i]]++;
		
		for(i = 0 ; i < Parameters.numClasses  ; ++i)
			classProb[i] /= numInstances;

		
		// 4) Compute the entropy of each interval
		int numIntervals = numcp-1;
		entropyIntrvl = new double[numIntervals];
		for(i = 0 ; i < numIntervals ; ++i)
			entropyIntrvl[i] = intervalEntropy(i, attribute);
		
		
		// 5) Compute the entropy for each cutpoint
		double[] entropyCutp = new double[numcp];
		entropyCutp[0] = entropyCutp[numcp-1] = (-1)*Double.MIN_VALUE;
		for(i = 1 ; i < numcp-1 ; ++i)
			entropyCutp[i] = cutpointEntropy(i);

		// ... and sort these values
		int[] positions = Quicksort.sort(entropyCutp, numcp, Quicksort.LOWEST_FIRST);

		
		// 6) Lastly, repeat and quit the first size-maxIntervals cutpoints
		selected = new int[numcp];

		for(i = 0 ; i < numcp ; ++i)
			selected[i] = 1;
		
		for(i = 0 ; i < numcp-(numInterAtt[attribute]-1) ; ++i)
			selected[positions[i]] = 0;

		
		// 7) return the selected cutpoints
		Vector cp = new Vector();
		for(i = 0 ; i < numcp ; ++i)
			if(selected[i] == 1)
				cp.add(cutpoints[i]);
		
		return cp;
	}
	
//******************************************************************************************************
	
	/**
	 * <p>
	 * It computes the interval entropy
	 * </p>
 	 * @param interval index of the interval
 	 * @param attribute index of the attribute to discretize
	 * @return the entropy of the interval
	 */
	public double intervalEntropy(int interval, int attribute){

		int i, numInst = 0;
		double total = 0;
		double bottomBound = cutpoints[interval];
		double topBound = cutpoints[interval+1];
		
		
		// compute the probability of each distinct class into the interval
		double[] probInterval = new double[Parameters.numClasses];
		for(i = 0 ; i < Parameters.numClasses ; ++i)
			probInterval[i] = 0;
		
		for(i = 0 ; i < numInstances ; ++i){
			
			// if it is the last interval
			if(interval == entropyIntrvl.length-1){
				if(realValues[attribute][i] >= bottomBound){
					probInterval[classOfInstances[i]]++;
					numInst++;
				}
			}
			
			else if(realValues[attribute][i] >= bottomBound && realValues[attribute][i] < topBound){
				probInterval[classOfInstances[i]]++;
				numInst++;
			}
		}
				
		for(i = 0 ; i < Parameters.numClasses ; ++i)
			probInterval[i] /= numInst;
		
		
		// compute the entropy of the interval
		for(i = 0 ; i < Parameters.numClasses ; ++i)
			total += Math.pow((Math.sqrt(classProb[i]) - Math.sqrt(probInterval[i])), 2);

		
		return Math.sqrt(total);
	}
	
//******************************************************************************************************
	
	/**
	 * <p>
	 * It computes the cutpoint entropy
	 * </p>
 	 * @param cutp index of the cutpoint
	 * @return the entropy of the cutpoint
	 */
	public double cutpointEntropy(int cutp){
		
		return 	Math.abs(entropyIntrvl[cutp-1] - entropyIntrvl[cutp]);
	}	
	
}