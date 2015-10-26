/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    S. García (sglopez@ujaen.es)
    F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
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


package keel.Algorithms.Coevolution.CIW_NN;

import java.util.Arrays;


/**
 * 
 * File: WKNN.java
 * 
 * An implementation of a KNN classifier able to manage weights for instances and features
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/1/2010 
 * @version 1.1 
 * @since JDK1.5
 * 
 */
class WKNN{

	private static double data[][];
	private static int output[];
	
	private static int nearestN;
	private static double minDist;
	
	private static double FW[];
	private static int IS[];
	private static double IW[];
	
	private static int K;
	private static int nClasses;
	
	/**
	 * Sets the K parameter of the KNN
	 * 
	 * @param value
	 */
	public static void setK(int value){
	
		if((value>0)&&(value<100)){
			K=value;
		}
	}
	
	/**
	 * Sets the number of classes of data
	 * 
	 * @param value
	 */
	public static void setNClasses(int value){
	
		nClasses=value;
	}

	/**
	 * Sets training data for the KNN
	 * 
	 * @param newData New training data
	 */
	public static void setData(double newData[][]){	
		
		data=new double [newData.length][newData[0].length];
		
		for(int i=0;i<newData.length;i++){		
			System.arraycopy(newData[i],0,data[i], 0, newData[0].length);
		}
	}
	/**
	 * Sets training output for the KNN
	 * 
	 * @param newOutput New training output
	 */
	public static void setOutput(int newOutput[]){	
		
		output=new int [data.length];
		
		System.arraycopy(newOutput,0,output, 0, data.length);
		
	}
	
	/**
	 * Sets feature weights
	 * 
	 * @param weights Feature weights
	 */
	public static void setFeatureWeights(double weigths []){
		
		FW= new double [weigths.length];
		
		System.arraycopy(weigths,0,FW, 0, weigths.length);
	}
	
	/**
	 * Sets instance weights
	 * 
	 * @param weights Instance weights
	 */
	public static void setInstanceWeights(double weigths []){
		
		IW= new double [weigths.length];
		
		System.arraycopy(weigths,0,IW, 0, weigths.length);
	}
	
	/**
	 * Sets selected instances 
	 * 
	 * @param selected Selected instances 
	 */
	public static void setInstances(int selected []){
		
		IS= new int [selected.length];
		
		System.arraycopy(selected,0,IS, 0, selected.length);
	}
	
	/**
	 * Computes accuracy
	 * 
	 * @return Training accuray
	 */
	public static double accuracy(){
		
		int hits;
		int test;
		double acc;
		
		hits=0;
		
		for (int i=0; i<data.length; i++) {
			
			test=knnClassify(i);
			
			if(test==output[i]){
				hits++;
			}
		}
		
		acc=(double)((double)hits/(double)data.length);
		
		return acc;
	}
    
	/**
	 * Classify an instance from the training set
	 * 
	 * @param index Index of the instance
	 * 
	 * @return Class expected
	 */
	public static int classifyTrainInstance(int index){
		
		return knnClassify(index);
	}
	
	/**
	 * Classify a new example
	 * 
	 * @param example New example
	 * 
	 * @return Class expected
	 */
	public static int classifyNewInstance(double example[]){
		
		double dist;
		int newClass;
		
		if(K==1){
			nearestN=0;
			minDist=Double.MAX_VALUE;
	    
			//1NN Method starts here
	    
			for (int i=0; i<data.length; i++) {
		
				dist = newEuclideanDistance(example,i);

				//see if it's nearer than our previous selected neigbours
		
				
				if (dist < minDist) {
			
					minDist = dist;
					nearestN = i;
					
				}
			}
			
			newClass=output[nearestN];
			
		}else{
			
			double minDistArray[];
			int nearestNArray[];
			int selectedClasses[];
			int prediction;
			int predictionValue;
			boolean stop;

			nearestNArray = new int[K];
			minDistArray = new double[K];
		
			Arrays.fill(nearestNArray, 0);
			Arrays.fill(minDistArray, Double.MAX_VALUE);
		    
		    //KNN Method starts here
		    
			for (int i=0; i<data.length; i++) {
			
				dist = newEuclideanDistance(example,i);

				//see if it's nearer than our previous selected neighbors
				stop=false;
					
				for(int j=0;j<K && !stop;j++){
					
					if (dist < minDistArray[j]) {
						    
						for (int l = K - 1; l >= j+1; l--) {
							minDistArray[l] = minDistArray[l - 1];
							nearestNArray[l] = nearestNArray[l - 1];
						}	
							
						minDistArray[j] = dist;
						nearestNArray[j] = i;
						stop=true;
					}
				}
				
			}
			
			//we have check all the instances... see what is the most present class
			selectedClasses= new int[nClasses];
		
			for (int i=0; i<nClasses; i++) {
				selectedClasses[i] = 0;
			}	
			
			for (int i=0; i<K; i++) {
				selectedClasses[output[nearestNArray[i]]]+=1;
			}
			
			prediction=0;
			predictionValue=selectedClasses[0];
			
			for (int i=1; i<nClasses; i++) {
			    if (predictionValue < selectedClasses[i]) {
			        predictionValue = selectedClasses[i];
			        prediction = i;
			    }
			}
			
			newClass=prediction;
			
		}
		
		return newClass;
	}
	
	/**
	 * Classify an instance using the KNN classifier
	 * 
	 * @param index Index of instance to avoid
	 * 
	 * @return Class expected
	 */
	private static int knnClassify(int index){
		
		int save;
		double dist;
		int newClass;
		
		//leave one out
	    save=IS[index];
	    IS[index]=0;
	    
		if(K==1){
			nearestN=0;
		    minDist=Double.MAX_VALUE;
		    
		    //1NN Method starts here
		        
			for (int i=0; i<data.length; i++) {
			
				if(IS[i]==1){
					dist = euclideanDistance(index,i);
		
					//see if it's nearer than our previous selected neigbours
				
						
					if (dist < minDist) {
					
						minDist = dist;
						nearestN = i;
							
					}
				}
			}
			
			newClass=output[nearestN];
		
		}else{
			
			double minDistArray[];
			int nearestNArray[];
			int selectedClasses[];
			int prediction;
			int predictionValue;
			boolean stop;
	
			nearestNArray = new int[K];
			minDistArray = new double[K];
		
			Arrays.fill(nearestNArray, 0);
			Arrays.fill(minDistArray, Double.MAX_VALUE);
		    
		    //KNN Method starts here
		    
			for (int i=0; i<data.length; i++) {
			
				if(IS[i]==1){
					dist = euclideanDistance(index,i);
		
					//see if it's nearer than our previous selected neighbors
					stop=false;
						
					for(int j=0;j<K && !stop;j++){
						
						if (dist < minDistArray[j]) {
							    
							for (int l = K - 1; l >= j+1; l--) {
								minDistArray[l] = minDistArray[l - 1];
								nearestNArray[l] = nearestNArray[l - 1];
							}	
								
							minDistArray[j] = dist;
							nearestNArray[j] = i;
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
				selectedClasses[output[nearestNArray[i]]]+=1;
			}
			
			prediction=0;
			predictionValue=selectedClasses[0];
			
			for (int i=1; i<nClasses; i++) {
			    if (predictionValue < selectedClasses[i]) {
			        predictionValue = selectedClasses[i];
			        prediction = i;
			    }
			}
			
			newClass=prediction;
			
		}
	
		//undo leave-one-out
		IS[index]=save;
		 
		return newClass;

	}
	
	/**
	 * Computes the euclidean distance between two training instances
	 * 
	 * @param a First instance
	 * @param b Second instance
	 * 
	 * @return Euclidean distance 
	 */
	private static double euclideanDistance(int a,int b){
		
		double length=0.0;
		double value;
		double iWeight;
		
		iWeight=IW[output[b]];

		for (int i=0; i<data[a].length; i++) {
			
			value = data[a][i]-data[b][i];
			length += FW[i]*value*value;
		}
			
		length = (0.2+(1-iWeight)*0.8)*Math.sqrt(length);
				
		return length;
	}
	
	/**
	 * Computes the euclidean distance between atraining instance and a new example
	 * 
	 * @param example New example
	 * @param b Training instance
	 * 
	 * @return Euclidean distance 
	 */
	private static double newEuclideanDistance(double example [],int b){
		
		double length=0.0;
		double value;
		double iWeight;
		
		iWeight=IW[output[b]];
		
		for (int i=0; i<data[b].length; i++) {
			
			value = example[i]-data[b][i];
			length += FW[i]*value*value;
		}
				
		length = (0.2+(1-iWeight)*0.8)*Math.sqrt(length);
		
		return length;
	}

    
} //end-class 

