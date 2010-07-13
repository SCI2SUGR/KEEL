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
 * File: NM.java
 * 
 * The Nearest Mean Algorithm.
 * It makes use of class means to classificate new instances,
 * calculating their distances and selecting the nearest class mean. 
 * 
 * @author Written by Joaquín Derrac (University of Granada) 12/11/2008 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Lazy_Learning.NM;

import keel.Algorithms.Lazy_Learning.LazyAlgorithm;

public class NM extends LazyAlgorithm{
	
	//Adictional structures
	
	double means[][];
	private int[] meanClass;
	
	
	/** 
	 * The main method of the class
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */
	public NM (String script) {
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="Nearest Mean";
	    
	    //Inicialization of auxiliar structures
	    
		means=new double[nClasses][inputAtt];
		meanClass=new int[nClasses];
		
		//Initialization stuff ends here. So, we can start time-counting
		
		setInitialTime();

	} //end-method 
	
	/** 
	 * Reads configuration script, to extract the parameter's values.
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */	
	protected void readParameters (String script) {
		//No parameters to read in this algorithm
	}

	/** 
	 * Evaluates a instance to predict its class.
	 * 
	 * @param example Instance evaluated 
	 * @return Class predicted
	 */
	protected int evaluate (double example[]) {
		
		int output;
		double aux;
		double min;
		
		min=Double.MAX_VALUE;
		output=-1;
		
		//get the nearest mean
		for(int i=0;i<means.length;i++){
			
			aux=euclideanDistance(example,means[i]);
			
			if(aux<min){
				min=aux;
				output=i;
			}
		}
		
		//use their class
		output=meanClass[output];
		
		return output;
		
	}//end-method
	
	/** 
	 * Calculate the mean (centroid) of each class
	 * 
	 */	
	public void calculateMeans(){
		
		int isClass;
		
		//Initialice the mean's structure
		
		for(int i=0;i<nClasses;i++){
			for(int j=0;j<inputAtt;j++){
				means[i][j]=0.0;
			}	
			meanClass[i]=i;
		}
		
		//calculate the sum of every instance for each class
		
		for(int i=0;i<trainData.length;i++){
			
			isClass=trainOutput[i];
			
			for(int j=0;j<inputAtt;j++){
				means[isClass][j]+=trainData[i][j];
			}	
		}
		
		//get the means
		
		for(int i=0;i<nClasses;i++){
			for(int j=0;j<inputAtt;j++){
				if(nInstances[i]>0){
					means[i][j]/=(double)nInstances[i];
				}
			}			
		}		
		
	}//end-method 
	
} //end-class 

