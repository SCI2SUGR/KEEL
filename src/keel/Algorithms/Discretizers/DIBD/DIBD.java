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

package keel.Algorithms.Discretizers.DIBD;

import java.util.*;
import keel.Algorithms.Discretizers.Basic.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;


/**
 * <p>
 * This class implements the DIBD
 * </p>
 * 
 * @author Written by Jose A. Saez (University of Granada), 21/12/2009
 * @version 1.0
 * @since JDK1.6
 */
public class DIBD extends Discretizer {

	// tags definition
	static int LEFT = 0;
	static int RIGHT = 1;
	static int ALL_CLASSES = -1;
	
	// instance variables
	private double[] cutpoints;			// possible cutpoints
	private int size;					// number of possible cutpoints
	
	private int[] numInstValue;			// number of instances per value
	private int[][] numInstClass;		// number of instances per class and per value
	
	private int numInstances;			//total number of instances
	private int[] selected;				//cutpoints selected
	
	private int[] numInterAtt;			// number of intervals of each attribute


//******************************************************************************************************

	/**
	 * <p>
	 * Constructor of the class
	 * </p>
	 */
	public DIBD(){
		
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
	 * Returns a vector with the discretized values
	 * </p>
	 * @param attribute number of the attribute
	 * @param values vector of indexes of the instances sorted from lowest to highest
	 * @param begin index of the instance with the lowest value of attribute
	 * @param end index of the instance with the lowest value of attribute
	 * @return vector with the discretized values
	 */
	protected Vector discretizeAttribute(int attribute, int []values, int begin, int end){
		
		int i, j;
		numInstances = realValues[attribute].length; // number of instances
		
		
		// structures initialization
		cutpoints = new double[end+1];
		numInstValue = new int[end+1];
		numInstClass = new int[Parameters.numClasses][end+1];
		size = 0;
		
		for(i = 0 ; i < end+1 ; ++i)
			numInstValue[i] = 0;
		
		for(i = 0 ; i< Parameters.numClasses ; ++i)
			for(j = 0 ; j < end+1 ; ++j)
				numInstClass[i][j] = 0;
		
		
		// 1) calculate the distribution numbers
		double value = realValues[attribute][values[begin]];
		cutpoints[size++] = value;
		numInstValue[size-1]++;
		numInstClass[classOfInstances[values[begin]]][size-1]++;
		
		for(i = begin+1 ; i <= end ; ++i){
			if(value!=realValues[attribute][values[i]]){
				cutpoints[size++] = realValues[attribute][values[i]];
				numInstValue[size-1]++;
				numInstClass[classOfInstances[values[i]]][size-1]++;
				value = realValues[attribute][values[i]];
			}
			else{
				numInstValue[size-1]++;
				numInstClass[classOfInstances[values[i]]][size-1]++;	
			}
		}		
		
		
		// 2) calculate dichotomic entropy for each cutpoint and determine the splitting point
		
		// set initial values
		int icn = 1;						// interval control number
		int vstart = 0, vend = 0; 			// index of start cutpoint and end cutpoint (interval)
		selected = new int[size];			// splitting point sequence list
		
		int pos;							// index of minimal entropy
		double min = 0;						// minimal entropy
		int vstartMin = 0, vendMin = 0;		// index of start and end cutpoints of minimal entropy (interval)
		
		double ni; 							// number of instances of the selected interval
		double ecom, ecomL, ecomR, max, decom;

		// selected cutpoints initialization
		for(i = 0 ; i < size ; ++i)
			selected[i] = 0;
		
		selected[0] = selected[size-1] = 1;
		
		
		boolean stopCondition = false;
		boolean continueLoop = true;
		
		
		do{

			pos = -1;	// this indicates that the value is the first
			
			// determine the splitting point
			for(i = 0 ; i < size ; ++i){
				
				// if the cutpoint is begin of a interval, keep begin and end of this interval [vstart, vend]
				if(selected[i] == 1){
					
					continueLoop = true;
					vstart = i;
					
					for(j = i+1 ; j < size && continueLoop ; ++j){
						if(selected[j] == 1){
							vend = j;
							continueLoop = false;
						}
					}
					
				}
				
				// otherwise, compute the entropy for this cutpoint
				else{
					
					ni = computeIntervalNI(ALL_CLASSES, vstart, vend);
					if(ni == 0) ni = 0.0001;
					value = (double) (Eleft(i) + Eright(i)) / ni;
					
					// if value is lower than min or it is the first value (pos == -1), then update min entropy
					if(value < min || pos == -1){
						min = value;
						pos = i;
						vstartMin = vstart;
						vendMin = vend;
					}
				}
				
			}
			

			// 3) see if the cutpoint enters into the splitting point sequence list
			if(pos == -1)
				stopCondition = true;
			
			else{
				// calculate compound distributional index and compound decrement for the cutpoint of minimal entropy
				ecom = Ecom(vstartMin, vendMin);
				ecomL = Ecom(vstartMin, pos);
				ecomR = Ecom(pos+1, vendMin);
				max = ecomL>ecomR?ecomL:ecomR;
				decom = ecom*(ecom-max);
				

				// adaptative rule control
				if( (decom < 0.001) || (icn >= numInterAtt[attribute]) ){
					stopCondition = true;
				}
				
				else{
					icn++;
					selected[pos] = 1; // add the splitting point into the splitting point sequence list
				}
				
			}
				
		}while(!stopCondition);
		
		
		Vector cp = new Vector();
		selected[0] = selected[size-1] = 0;
		for(i = 0 ; i < size ; ++i)
			if(selected[i] == 1)
				cp.add(cutpoints[i]);
		
		return cp;
	}	
	
//******************************************************************************************************
	
	/**
	 * <p>
	 * It computes the number of instances of class class_ (or all classes) and attribute value <= or > than
	 * value, according to option
	 * </p>
	 * @param class_ class of instances computed
	 * @param value value to compare
	 * @param option is equal to one tag: LEFT (for <=) or RIGHT (for >)
	 * @return number of instances
	 */
	public int computeNI(int class_, int value, int option){
		
		int res = 0;
		
		if(option == LEFT){
			
			if(class_ == ALL_CLASSES){
				for(int i = 0 ; i <= value ; ++i)
					res += numInstValue[i];
			}
			
			else{
				for(int i = 0 ; i <= value ; ++i)
					res += numInstClass[class_][i];
			}
		}
		
		if(option == RIGHT){
			
			if(class_ == ALL_CLASSES){
				for(int i = value+1 ; i < size ; ++i)
					res += numInstValue[i];
			}
			
			else{
				for(int i = value+1 ; i < size ; ++i)
					res += numInstClass[class_][i];
			}
		}
		
		return res;	
	}

//******************************************************************************************************	
	
	/**
	 * <p>
	 * It computes the number of instances with attribute value in the interval [start, end] and class class_
	 * </p>
	 * @param class_ class of instances computed
	 * @param start begin of the interval
	 * @param end end of the interval
	 * @return number of instances
	 */
	public int computeIntervalNI(int class_, int start, int end){
		
		int res = 0;
		
		if(class_ == ALL_CLASSES){
			for(int i = start ; i <= end ; ++i)
				res += numInstValue[i];
		}
		
		else{
			for(int i = start ; i <= end ; ++i)
				res += numInstClass[class_][i];
		}
		
		return res;
	}
	
//******************************************************************************************************	
	
	/**
	 * <p>
	 * It computes the Left decision distributional index needed to compute the entropy of a cutpoint
	 * </p>
	 * @param value index of the cutpoint studied
	 * @return the value of the index
	 */
	public double Eleft(int value){
		
		double res = 0, aux;
		
		for(int dk = 0 ;  dk < Parameters.numClasses ; ++dk){
			
			aux = computeNI(dk, value, LEFT);
			
			if(aux!=0){
				double aux2 = (Math.log(aux/computeNI(ALL_CLASSES, value, LEFT)))/ (Math.log(2));
				res += (-1)*aux*aux2;
			}
		}
		
		return res;
	}
	
//******************************************************************************************************
	
	/**
	 * <p>
	 * It computes the Right decision distributional index needed to compute the entropy of a cutpoint
	 * </p>
	 * @param value index of the cutpoint studied
	 * @return the value of the index
	 */
	public double Eright(int value){
		
		double res = 0, aux;
		
		for(int dk = 0 ;  dk < Parameters.numClasses ; ++dk){
			
			aux = computeNI(dk, value, RIGHT);
			
			if(aux!=0){
				double aux2 = (Math.log(aux/computeNI(ALL_CLASSES, value, RIGHT)))/ (Math.log(2));
				res += (-1)*aux*aux2;
			}
		}
		
		return res;		
	}
	
//******************************************************************************************************
	
	/**
	 * <p>
	 * It computes the compound distributional index needed to compute the compound decrement of a cutpoint
	 * </p>
	 * @param start begin of the interval
	 * @param end end of the interval
	 * @return the value of the index
	 */
	public double Ecom(int start, int end){
		
		double ed = Ed(start, end);
		double ev = Ev(start, end);
		double res = (ed - ev) / numInstances;
		
		return res;
	}

//******************************************************************************************************
	
	/**
	 * <p>
	 * It computes the decision distributional index needed to compute the compound distributional index (Ecom)
	 * </p>
	 * @param start begin of the interval
	 * @param end end of the interval
	 * @return the value of the index
	 */
	public double Ed(int start, int end){
		
		double res = 0, aux, aux2;
		
		for(int dk = 0 ;  dk < Parameters.numClasses ; ++dk){
			aux = computeIntervalNI(dk, start, end);
			
			if(aux != 0){
				aux2 = ( (Math.log(aux/computeIntervalNI(ALL_CLASSES, start, end))) / (Math.log(2)) );
				res += (-1)*aux*aux2;
			}
		}
		
		return res;
	}

//******************************************************************************************************	
	
	/**
	 * <p>
	 * It computes the value distributional index needed to compute the compound distributional index (Ecom)
	 * </p>
	 * @param start begin of the interval
	 * @param end end of the interval
	 * @return the value of the index
	 */
	public double Ev(int start, int end){
		
		double res = 0, aux, resto;
		
		for(int i = start ; i < end ; ++i){
			
				for(int dk = 0 ;  dk < Parameters.numClasses ; ++dk){
					aux = numInstClass[dk][i];
					if(aux != 0){
						resto = ( (Math.log(aux/numInstValue[i])) / (Math.log(2)) );
						res += (-1)*aux*resto;
					}
				}
		}

		return res;		
	}
	
}