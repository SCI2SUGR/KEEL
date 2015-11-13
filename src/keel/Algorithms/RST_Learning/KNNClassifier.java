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


package keel.Algorithms.RST_Learning;

import java.util.Arrays;

/**
 * 
 * File: KNNClassifier.java
 * 
 * A KNN classifier with the capabilities of selecting instances and features.
 * For efficiency, employs unsquared euclidean distance.
 * 
 * @author Written by Joaquín Derrac (University of Granada) 20/04/2010 
 * @version 1.0 
 * @since JDK1.5
 * 
 */
public class KNNClassifier{
	
	private static int K;
	private static double data[][];
	private static int output[];
	
	private static int instances;
	private static int features;
	private static int nClasses;
	
	private static int IS[];
	private static int FS[];
	
	private static int nearestN [];
	private static double minDist [];
	
	/**
	 * Sets the number of classes in the data
	 * 
	 * @param value Number of classes
	 */
	public static void setClasses(int value){
		
		nClasses=value;
		
	}//end-method 
	
	/**
	 * Sets the K value
	 * 
	 * @param value K value
	 */
	public static void setK(int value){
		
		K=value;
		
		nearestN = new int[K];
		minDist = new double[K];
		
	}//end-method 
	
	/**
	 * Loads the training data into the classifier
	 * 
	 * @param newData Data represented with continuous values
	 */
	public static void setData(double newData[][]){	
		
		instances = newData.length;
		features = newData[0].length;
		
		data = new double [instances][features];
		
		for(int i=0;i<instances;i++){		
			for(int j=0;j<features;j++){		
				data[i][j]=newData[i][j];
			}
		}

		IS = new int [instances];
		FS = new int [features];
		
		Arrays.fill(IS, 1);
		Arrays.fill(FS, 1);
		
	}//end-method 
	
	/**
	 * Loads the training output into the classifier
	 * 
	 * @param newOutput Output attribute of the training data
	 */
	public static void setOutput(int newOutput[]){	
		
		output=new int [data.length];
		
		System.arraycopy(newOutput,0,output, 0, data.length);
		
	}//end-method 
	
	/**
	 * Sets the vector of instances selected
	 * 
	 * @param selected Vector of instances selected
	 */
	public static void setInstances(int selected []){
		
		for(int i=0; i< instances;i++){
			IS[i]=selected[i];
		}
		
	}//end-method 
	
	/**
	 * Sets the vector of features selected
	 * 
	 * @param selected Vector of features selected
	 */
	public static void setFeatures(int selected []){
		
		
		for(int i=0; i< features ;i++){
			FS[i]=selected[i];
		}
		
	}//end-method 
	
    /**
     * On the instance selector vector, sets the all the instances to 1 (selected)
     */
    public static void setAllInstances(){
		
		Arrays.fill(IS,1);
		
	}//end-method 
	
    /**
     *  On the features selector vector, sets the all the features to 1 (selected)
     */
    public static void setAllFeatures(){
		
		Arrays.fill(FS,1);
		
	}//end-method 
	
	/**
	 * Estimates the LVO (Leave-one-out) accuracy of the classifier
	 * over the training data.
	 *  
	 * @return Accuracy estimated
	 */
	public static double accuracy(){
		
		int hits;
		int test;
		double acc;
		
		hits=0;
		
		for (int i=0; i<data.length; i++) {
			
			test=classifyTrainingInstance(i);
			
			if(test==output[i]){
				hits++;
			}
		}
		
		acc=(double)((double)hits/(double)data.length);
		
		return acc;
		
	}//end-method 
	
	/**
	 * Classifies a new example
	 * 
	 * @param example Example to classify
	 * @return Class of the example
	 */
	public static int classifyNewInstance(double example[]){
		
		int value;
	
		if(K==1){
			value=classifyInstance(example);
			
		}
		else{
			value=classifyInstanceK(example);
		}
	    
		return value;
		
	}//end-method 
	
	/**
	 * Classifies an instance by means of a 1-NN classifier
	 * 
	 * @param example Example to classify
	 * @return Class of the example
	 */
	private static int classifyInstance(double example[]){
		
		double dist;
	
	    int near=-1;
	    double minD=Double.MAX_VALUE;
	    
	    //1-NN Method starts here

		for (int i=0; i<data.length; i++) {
		
			if(IS[i]==1){
				dist = newEuclideanDistance(example,i);
				
				//see if it's nearer than our previous selected neigbours
				if (dist < minD) {
					minD = dist;
					near = i;		
				}
			}
		}

		if(near==-1){
			return -1;
		}
		
		return output[near];
		
	}//end-method 
	
	/**
	 * Classifies an instance by means of a K-NN classifier
	 * 
	 * @param example Example to classify
	 * @return Class of the example
	 */
	private static int classifyInstanceK(double example[]){
		
		int selectedClasses[];
		double dist;
		int prediction;
		int predictionValue;
		boolean stop;

		Arrays.fill(nearestN, -1);
		Arrays.fill(minDist, Double.MAX_VALUE);
		
	    //KNN Method starts here
	    
		for (int i=0; i<instances; i++) {
		
			if(IS[i]==1){
				dist = newEuclideanDistance(example,i);

				//see if it's nearer than our previous selected neighbors
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
			selectedClasses[output[nearestN[i]]]+=1;
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
	 * Classifies a training example
	 * 
	 * @param index Training example to classify
	 * @return Class of the example
	 */
	public static int classifyTrainingInstance(int index){
		
		int value;
		int aux;
	
		//leave-one-out
		aux=IS[index];
		IS[index]=0;
		
		if(K==1){
			value=classifyTrainInstance(index);
		}
		else{
			value=classifyTrainInstanceK(index);
		}
	    
		IS[index]=aux;
		
		return value;
		
	}//end-method 

	/**
	 * Classifies a training instance by means of a 1-NN classifier
	 * 
	 * @param index Example to classify
	 * @return Class of the example
	 */
	private static int classifyTrainInstance(int index){
		
		double dist;
	
	    int near=-1;
	    double minD=Double.MAX_VALUE;
	    
	    //1-NN Method starts here
	    
		for (int i=0; i<data.length; i++) {
		
			if(IS[i]==1){
				dist = euclideanDistance(index,i);
				
				//see if it's nearer than our previous selected neigbours
				if (dist < minD) {
					minD = dist;
					near = i;		
				}
			}
		}
		
		if(near==-1){
			return -1;
		}
		
		return output[near];
		
	}//end-method 
	
	/**
	 * Classifies an instance by means of a K-NN classifier
	 * 
	 * @param instance Example to classify
	 * @return Class of the example
	 */
	private static int classifyTrainInstanceK(int index){
		
		int selectedClasses[];
		double dist;
		int prediction;
		int predictionValue;
		boolean stop;

		Arrays.fill(nearestN, -1);
		Arrays.fill(minDist, Double.MAX_VALUE);
		
	    //KNN Method starts here
	    
		for (int i=0; i<instances; i++) {
		
			if(IS[i]==1){
				dist = euclideanDistance(index,i);

				//see if it's nearer than our previous selected neighbors
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
			selectedClasses[output[nearestN[i]]]+=1;
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
	 * Euclidean instance between two training instances
	 * 
	 * @param a First instance
	 * @param b Second instance
	 * 
	 * @return Unsquared euclidean distance
	 */
	private static double euclideanDistance(int a,int b){
		
		double length=0.0;
		double value;
		
		for (int i=0; i<data[b].length; i++) {
			
			if(FS[i]==1){
				value = data[a][i]-data[b][i];
				length += value*value;
			}
		}
		
		return length;
		
	}//end-method 

	/**
	 * Euclidean instance between a training instance and a new example
	 * 
	 * @param example New example
	 * @param b Training instance
	 * 
	 * @return Unsquared euclidean distance
	 */
	private static double newEuclideanDistance(double example [],int b){
		
		double length=0.0;
		double value;
		
		for (int i=0; i<data[b].length; i++) {
			
			if(FS[i]==1){
				value = example[i]-data[b][i];
				length += value*value;
			}
		}
		
		return length;
		
	}//end-method 
	
	/**
	 * Computes reduction rates over instances
	 * 
	 * @return Reduction rate
	 */
	public static double computeISReduction(){
		
		double count=0;
		double result;
		
		for(int i=0;i<instances;i++){
			if(IS[i]==1){
				count+=1.0;
			}
		}
		
		result=(double)(count/(double)instances);
		result=1.0-result;
		
		return result;
		
	}//end-method 
	
	/**
	 * Computes reduction rates over features
	 * 
	 * @return Reduction rate
	 */
	public static double computeFSReduction(){
		
		double count=0;
		double result;
		
		for(int i=0;i<features;i++){
			if(FS[i]==1){
				count+=1.0;
			}
		}
		
		result=(double)(count/(double)features);
		result=1.0-result;
		
		return result;
		
	}//end-method 
	
	/**
	 * Get a vector with the instances currently selected
	 * 
	 * @return A vector with the instances currently selected
	 */
	public static int [] getIS(){
		
		return IS;
		
	}//end-method 
	
	/**
	 * Get a vector with the features currently selected
	 * 
	 * @return A vector with the features currently selected
	 */
	public static int [] getFS(){
		
		int newFS [];
		
		newFS= new int [FS.length];
		
		for(int i=0;i<FS.length;i++){
			newFS[i]=FS[i];
		}
		
		return newFS;
		
	}//end-method 
	
    /**
     * Returns a string representation of the features selection vector
     * @return a string representation of the features selection vector
     */
    public static String printFS(){
		
		String aux="";
		
		for(int i=0;i<FS.length;i++){
			
			aux+=FS[i];
		}
		
		return aux;
		
	}//end-method 
	

}//end-class
