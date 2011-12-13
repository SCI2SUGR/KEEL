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

package keel.Algorithms.Discretizers.CACC;

import java.util.*;
import keel.Algorithms.Discretizers.Basic.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.Parameters;

/**
 * 
 * <p>
 * This class implements the CACC discretizer
 * </p>
 * 
 * <p>
 * @author Written by Jose A. Saez Munoz (SCI2S research group, DECSAI in ETSIIT, University of Granada), 21/12/2009
 * @version 1.0
 * @since JDK1.6
 * </p>
 */
public class CACC extends Discretizer {

	private int numInstances;			// total number of instances
	private double[] cutpoints;			// possible cutpoints
	private int[] selected;				// selected cutpoints
	private int numcp;					// number of selected cutpoints
	private int[][] matrix;				// quanta matrix

	
//******************************************************************************************************

	/**
	 * <p>
	 * Constructor of the class
	 * </p>
	 */
	public CACC(){
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
		cutpoints = new double[size+1];			// midpoints + minimun + maximun
		selected = new int[cutpoints.length];
		
		cutpoints[0] = realValues[attribute][values[begin]];
		selected[0] = 1;
		
		cutpoints[cutpoints.length-1] = realValues[attribute][values[end]];
		selected[cutpoints.length-1] = 1;
		
		for(i = 1 ; i < cutpoints.length-1 ; ++i){
			cutpoints[i] = (valuesNoRepeated[i-1]+valuesNoRepeated[i])/2;
			selected[i] = 0;
		}

	
		// 3) Set the initial discretization scheme as D: {[d0,dn]} and Globalcacc = 0
		numcp = 2;
		double Globalcacc = 0, maxCACC = -1, auxCACC = 0;
		int posCACC = 0;
		
		int k = 1;
		boolean finish = false;
		
		do{
			
			numcp++;
			
			// for each  inner boundary which is not already in  scheme D
			for(i = 1 ; i < cutpoints.length-1 ; i++){
				
				// Add it into D;
				if(selected[i] == 0){
					
					selected[i] = 1;
					
					// Calculate the corresponding cacc value;
					auxCACC = caccValue(attribute);
					
					if(auxCACC > maxCACC){
						maxCACC = auxCACC;
						posCACC = i;	
					}
					
					selected[i] = 0;
				}
			}
			
			numcp--;
			

			// see if add the cutpoint with maximum cacc value
			if( (maxCACC > Globalcacc) || (k < Parameters.numClasses) ){

				selected[posCACC] = 1;
				numcp++;
				Globalcacc = maxCACC;
				k = k+1;
			}
		
			// otherwise, finish the algorithm
			else{
				finish = true;
			}
			
		}while(!finish);


		// return the selected cutpoints
		Vector cp = new Vector();
		selected[0] = selected[cutpoints.length-1] = 0;
		for(i = 0 ; i < cutpoints.length ; ++i)
			if(selected[i] == 1)
				cp.add(cutpoints[i]);
		
		return cp;
	}
	
//******************************************************************************************************
		
	/**
	 * <p>
	 * Computes the cacc value of a discretization
	 * </p>
 	 * @param attribute index of the attribute to discretize
	 * @return the cacc value
	 */
	private double caccValue(int attribute){
				
		int i, r;
		double y = 0;
		
		CreateQuantaMatrix(attribute);
		
		for(i = 0 ; i < Parameters.numClasses ; ++i){
			for(r = 0 ; r < numcp-1 ; ++r){
				y += Math.pow(matrix[i][r],2)/(matrix[i][numcp-1]*matrix[Parameters.numClasses][r]);
			}
		}
		
		y--;
		y *= (numInstances/(Math.log(numcp)));
		
		return Math.sqrt(y/(y+numInstances));
	}
	
//******************************************************************************************************
			
	/**
	 * <p>
	 * Creates the quanta matrix basing on the selected cutpoints
	 * </p>
	 * @param attribute index of the attribute
	 */
	private void CreateQuantaMatrix(int attribute){
		
		int i, j, point, clase;
		int suma;
		
		// matrix creation
		matrix = new int[Parameters.numClasses+1][];
		
		for(i = 0 ; i < Parameters.numClasses+1 ; ++i){
			matrix[i] = new int[numcp];
			for(j = 0 ; j < numcp ; ++j)
				matrix[i][j] = 0;
		}
		
		
		// create the quanta matrix
		boolean continuar = true;
		int intervalo = 0;
		
		for(i = 0 ; i < numInstances ; ++i){
			
			continuar = true;
			intervalo = 0;
			
			for(point = 1 ; point < cutpoints.length && continuar ; ++point){
				
				if(realValues[attribute][i] <= cutpoints[point] && selected[point] == 1){
					matrix[classOfInstances[i]][intervalo]++;
					continuar = false;
				}
				
				if(selected[point] == 1)
					intervalo++;
			}
			
		}
		
		
		// sumatory per classes
		for(clase = 0 ; clase < Parameters.numClasses ; ++clase){
			
			suma = 0;
			for(j = 0 ; j < numcp-1 ; ++j)
				suma += matrix[clase][j];
			
			matrix[clase][numcp-1] = suma;
		}
		
		
		// sumatory per intervals
		for(j = 0 ; j < numcp-1 ; ++j){
			
			suma = 0;
			for(clase = 0 ; clase < Parameters.numClasses ; ++clase)
				suma += matrix[clase][j];
		
			matrix[Parameters.numClasses][j] = suma;
		}
		
	}
	
}
