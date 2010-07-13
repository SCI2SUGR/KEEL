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
 * File: CamNN.java
 * 
 * The Cam NN Algorithm.
 * It makes use of Cam distance to improve the KNN classification. 
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2008 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Lazy_Learning.CamNN;

import keel.Algorithms.Lazy_Learning.LazyAlgorithm;

import org.core.*;
import java.util.StringTokenizer;

public class CamNN extends LazyAlgorithm{

	//Parameters
	
	int K;
	
	//Adictional structures
	
	double V[][];
	double W[][];
	double G[];
	double L;
	
	double c1;
	double c2;
	double A[];
	double B[];
	double TAU[][];
	
	int N;
	double gammaValue;
	double gammaNextValue;
	
	/** 
	 * The main method of the class
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */
	public CamNN (String script) {

		readDataFiles(script);
		
		//Naming the algorithm
		name="Cam NN";

		//Inicialization of auxiliar structures
	
	    V= new double[inputAtt][K];
	    W= new double[inputAtt][K];

	    G= new double[inputAtt];
	    
	    A= new double[trainData.length];
	    B= new double[trainData.length];
	    TAU= new double[trainData.length][inputAtt];
		
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
		
		String file;
		String line;
		StringTokenizer fileLines, tokens;
		
	    file = Files.readFile (script);
	    fileLines = new StringTokenizer (file,"\n\r");
	    
	    //Discard in/out files definition
	    fileLines.nextToken();
	    fileLines.nextToken();
	    fileLines.nextToken();

	    //Getting the number of neighbors
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    K = Integer.parseInt(tokens.nextToken().substring(1));

	}//end-method
	
	/** 
	 * Calculates A, B and TAU Values for each training instance.
	 * 
	 */
	public void precalculateParameters(){

		double minDist[];
		int nearestN[];
		double dist;
		boolean stop;
		double module;
		int dimension;
		
		if(inputAtt>16){
			dimension=16;
		}else{
			dimension=inputAtt;
		}
		
	    //Calculate gamma values
	    
	    if(dimension%2==0){
	    	N=dimension/2;
	    	gammaValue=fact(N-1);
	    	gammaNextValue=Math.sqrt(Math.PI)/Math.pow(2.0,(double)N);
	    	gammaNextValue*=doubleFact((2*N)-1);
	    }
	    else{
	    	N=dimension/2;
	    	gammaValue=Math.sqrt(Math.PI)/Math.pow(2.0,(double)N);
	    	gammaValue*=doubleFact((2*N)-1);
	    	gammaNextValue=fact(N+1);
	    }
	    
	    
		nearestN = new int[K];
		minDist = new double[K];

		//We need to find K Nearest Neighbors to estimate V and W
		
		for(int instance=0;instance<trainData.length;instance++){

			 //KNN Method starts here
		    
		    for (int i=0; i<K; i++) {
				nearestN[i] = -1;
				minDist[i] = Double.MAX_VALUE;
			}
		    
			for (int i=0; i<trainData.length; i++) {
			
			    dist = euclideanDistance(trainData[instance],trainData[i]);

				if (dist > 0.0){ //leave-one-out
				
					//see if it's nearer than our previous selected neigbours
					stop=false;
					
					for(int j=0;j<K && !stop;j++){
					
						if (dist < minDist[j]) {
						    
							for (int l = K - 1; l >= j+1; l--) {
								minDist[l] = minDist[l - 1];
								nearestN[l] = nearestN[l - 1];
							}	
							
							minDist[j] = dist;
							nearestN[j] = i;
							stop=true;
						}
					}
				}
			}
			
			//neigbours have been found
			
			//calc V vector
			for(int i=0;i<inputAtt;i++){
				for(int j=0;j<K;j++){
					V[i][j]=trainData[nearestN[j]][i]-trainData[instance][i];	
				}
			}
			
			//calc W vector
			for(int i=0;i<inputAtt;i++){
				for(int j=0;j<K;j++){
					if(trainOutput[instance]==trainOutput[nearestN[j]]){
						W[i][j]=V[i][j];
					}
					else{
						W[i][j]=V[i][j]*(-0.5);
					}
				}
			}	
			
			//calc G vector
			for(int i=0;i<inputAtt;i++){
				G[i]=0.0;
				for(int j=0;j<K;j++){
					G[i]+=W[i][j];
				}
				G[i]/=K;
			}		
			
			//calc L value
			L=0.0;
			
			for(int j=0;j<K;j++){
				module=0.0;
				for(int i=0;i<inputAtt;i++){
					module+=(W[i][j]*W[i][j]);
				}
				L+=Math.sqrt(module);
			}	
			L/=K;
			
			//calc c1 and c2 value
			
			c2=Math.sqrt(2.0)*(gammaNextValue)/gammaValue;
			c1=c2/(double)inputAtt;

			//calc A and B values

			A[instance]=L/c2;
			
			module=0.0;
			for(int i=0;i<inputAtt;i++){
				module+=(G[i]*G[i]);
			}
			module=Math.sqrt(module);
			
			B[instance]=module/c1;
			
			//calc TAU vector
			
			for(int i=0;i<inputAtt;i++){
				TAU[instance][i]=G[i]/module;
			}
			
		}
	}//end-method	
	
	/** 
	 * Evaluates a instance to predict its class.
	 * 
	 * @param example Instance evaluated 
	 * @return Class predicted 
	 * 
	 */
	protected int evaluate (double example[]) {
		
		double minDist[];
		int nearestN[];
		int selectedClasses[];
		double dist;
		int prediction;
		int predictionValue;
		boolean stop;

		nearestN = new int[K];
		minDist = new double[K];
	
	    for (int i=0; i<K; i++) {
			nearestN[i] = -1;
			minDist[i] = Double.MAX_VALUE;
		}

	    //KNN Method starts here
	    
		for (int i=0; i<trainData.length; i++) {
		
		    dist = camDistance(example,i);
		    
			if (dist > 0.0){ //leave-one-out
			
				//see if it's nearer than our previous selected neigbours
				stop=false;
				
				for(int j=0;j<K && !stop;j++){
				
					if (dist < minDist[j]) {
					    
						for (int l = K - 1; l >= j+1; l--) {
							minDist[l] = minDist[l - 1];
							nearestN[l] = nearestN[l - 1];
						}	
						
						minDist[j] = dist;
						nearestN[j] = i;
						stop=true;
					}
				}
			}
		}
		
		//we have check all the instances... see what is the most present class
		selectedClasses= new int[nClasses];
	
		for (int i=0; i<nClasses; i++) {
			selectedClasses[i] = 0;
		}	
		
		for (int i=0; i<K; i++) {
			selectedClasses[trainOutput[nearestN[i]]]+=1;
		}
		
		prediction=0;
		predictionValue=selectedClasses[0];
		
		for (int i=1; i<nClasses; i++) {
		    if (predictionValue < selectedClasses[i]) {
		        predictionValue = selectedClasses[i];
		        prediction = i;
		    }
		}
		
		return prediction;
		
	}//end-method	
	
	/** 
	 * Calculates Cam Weigthed distance
	 * 
	 * @param example New instance 
	 * @param instance Index of train instance 
	 * @return Distance calculated
	 * 
	 */
	private double camDistance(double example[],int instance){
		
		double length=0.0;
		double factor;
		double cosine;
		double angleSum;
		
		//calculate their euclidean distance
		length=euclideanDistance(example,trainData[instance]);
		
		//calculate the Cam Weight
		angleSum=0.0;
		
		for (int i=0; i<example.length; i++) {
			angleSum += (example[i]-trainData[instance][i])*TAU[instance][i];
		}	
		
		cosine= angleSum/length;

		factor=A[instance];
		
		factor+=B[instance]*cosine;
		
		//apply the Cam Weight
		length/=factor;
		
		return length;
		
	} //end-method
	
	/** 
	 * Factorial function
	 * 
	 * @param n  Number to get its factorial
	 * @return Value of factorial
	 * 
	 */
	private double fact(int n){
		
		int value;
		
		if(n<2){
			return 1.0;
		}
		else{
			value=1;
			
			for(int i=2;i<=n;i++){
				value*=i;
			}
			
			return (double)value;
		}
	}

	/** 
	 * Double factorial function ( n*(n-2)*(n-4)* .... )
	 * 
	 * @param n  Number to get its double factorial
	 * @return Value of double factorial
	 * 
	 */
	private double doubleFact(int n){
		
		int value;
		
		if(n<2){
			return 1.0;
		}
		else{
			value=1;
			
			for(int i=n;i>1;i-=2){
				value*=i;
			}
			
			return (double)value;
		}
			
	} //end-method	
	
} //end-class 

