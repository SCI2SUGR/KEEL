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
 * File: CenterNN.java
 * 
 * The CenterNN Algorithm.
 * A enhanced 1-NN classifier,which uses the distance between train instances
 * and centers of their class,as a reference of how far is the train instance
 * from the query object
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2008 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Lazy_Learning.CenterNN;

import keel.Algorithms.Lazy_Learning.LazyAlgorithm;


public class CenterNN extends LazyAlgorithm{
	
	//Adictional structures
	
	double centers[][];
	double diferences[][];
	double quads[];
	
	double classDistance[];
	double projection[];
	double separation[];

	/** 
	 * The main method of the class
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */
	public CenterNN (String script) {
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="Center NN";

		//Inicialization of auxiliar structures
		
	    classDistance=new double [nClasses];
	    
		diferences=new double[trainData.length][inputAtt];
		
		quads=new double[trainData.length];
		
		projection=new double[inputAtt];
		
		separation=new double[inputAtt];
		
		centers=new double[nClasses][inputAtt];
		
		for(int i=0;i<nClasses;i++){
			for(int j=0;j<inputAtt;j++){
				centers[i][j]=0.0;
			}	
		}
		
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
	} //end-method 
	
	/** 
	 * Calculates centers of each class, and vectors from each
	 * train instance to its class center
	 * 
	 */
	public void precalculateParameters(){
		
		calculateCenters();
		calculateDiferences();
		
	} //end-method 

	/** 
	 * Calculates centers of each class.
	 * 
	 */
	private void calculateCenters(){
		
		int insClass;
						
		for(int i=0;i<trainData.length;i++){
			
			insClass=trainOutput[i];
			
			for(int j=0;j<inputAtt;j++){
				centers[insClass][j]+=trainData[i][j];
			}	
		}
		
		for(int i=0;i<nClasses;i++){
			for(int j=0;j<inputAtt;j++){
				centers[i][j]/=(double)nInstances[i];
			}			
		}			
	} //end-method 
	
	/** 
	 * Calculates diference vectors from each train instance
	 * to its class center, and get its quadratic distance. 
	 * 
	 */
	private void calculateDiferences(){
		
		int insClass;
		double sum;
				
		for(int i=0;i<trainData.length;i++){
			
			sum=0.0;
			
			insClass=trainOutput[i];
			
			for(int j=0;j<inputAtt;j++){
				diferences[i][j]=centers[insClass][j]-trainData[i][j];
				sum+=(diferences[i][j]*diferences[i][j]);
			}	
			quads[i]=sum;
		}		
	} //end-method 
		
	/** 
	 * Evaluates a instance to predict its class.
	 * 
	 * @param example Instance evaluated 
	 * @return Class predicted 
	 * 
	 */
	protected int evaluate (double example[]) {
		
		int output=-1;
		int insClass;
		double distance;
		double MU;
		double min;

		for(int i=0;i<nClasses;i++){
			classDistance[i]=Double.MAX_VALUE;
		}

		//Get the minimun distance to each class
		for(int i=0;i<trainData.length;i++){
			
			//leave-one-out
			if(euclideanDistance(example,trainData[i])>0.0){
				
				insClass=trainOutput[i];
				
				MU=calculateMu(example,i);
				
				//project test instance in the center vector
				for(int j=0;j<inputAtt;j++){
					projection[j]=trainData[i][j]+ (MU * diferences[i][j]);
				}
					
				distance=euclideanDistance(example,projection);
				
				if(classDistance[insClass]>distance){
					classDistance[insClass]=distance;
				}
			}
			
		}

		//Get the nearest class
		min=Double.MAX_VALUE;
		for(int i=0;i<nClasses;i++){
			if(min>classDistance[i]){
				min=classDistance[i];
				output=i;
			}
		}
			
		return output;
		
	} //end-method 

	/** 
	 * Calculates the position parameter (MU) for a given pair train/test instance
	 * 
	 * @param example The test instance
	 * @param instance Index of the train instance
	 * @return Position parameter calculated 
	 */
	private double calculateMu(double example[],int instance){
		
		double mu;
		double sum=0.0;
		
		for(int i=0;i<inputAtt;i++){	
			separation[i]=example[i]-trainData[instance][i];
			sum+=(separation[i]*diferences[instance][i]);
		}
		
		mu=sum/quads[instance];
		
		return mu;
		
	} //end-method 

} //end-class 

