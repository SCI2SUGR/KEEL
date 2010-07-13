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

//
//  Cromosoma.java
//
//  Salvador García López
//
//  Created by Salvador García López 19-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Feature_Selection.Shared;

import org.core.*;

public class Chromosome implements Comparable<Object> {

	//Cromosome data structure
	private int genes[];

	private double fitnessValue;
	private boolean valid;
	
	private static double data[][];
	private static int dataOutput[];
	private static int nInstances;
	private static int nFeatures;
	private static int nClasses;
	private static double beta;
	private static double mutationProb;
	private static int K;

	//Construct a random cromosome of specified size
  
	public Chromosome (int size) {

		double u;

		genes = new int[size];
    
		for (int i=0; i<size; i++) {
			u = Randomize.Rand();
			if (u < 0.5) {
				genes[i] = 0;
			}
			else {
				genes[i] = 1;
			}
		}
		
		valid=false;
	}

	public Chromosome (int info []) {

		genes = new int[info.length];
    
		for (int i=0; i<info.length; i++) {

			genes[i]=info[i];
		}
		
		valid=false;
	}
	
	public Chromosome (int info [],double fitness) {

		genes = new int[info.length];
    
		for (int i=0; i<info.length; i++) {

			genes[i]=info[i];
		}
		
		fitnessValue=fitness;
		valid=true;
	}
	
	public static void setData(double trainData[][],int trainOutput []){
		
		nInstances=trainData.length;
		nFeatures=trainData[0].length;
		data=new double [nInstances][nFeatures];
		dataOutput=new int [nInstances];
		
		for(int i=0;i<nInstances;i++){
			System.arraycopy(trainData, 0, data, 0, nInstances);
		}
		
		System.arraycopy(trainOutput, 0, dataOutput, 0, nInstances);
		
	}
	
	public static void setBeta(double value){
		
		beta=value;
	}

	public static void setMutationProb(double value){
		
		mutationProb=value;
	}
	
	public static void setK(int value){
		
		K=value;
	}

	public static void setNClasses(int value){
		
		nClasses=value;
	}
	
	public int [] getGenes(){
		
		return genes;
	}
	
	public int getNGenes(){
		
		int count = 0;
		
		for(int i=0;i<genes.length;i++){
			if(genes[i]==1){
				count++;
			}
		}
		
		return count;
	}

	public double getFitness(){
		
		return fitnessValue;
	}
	
	public boolean getValid(){
		
		return valid;
	}
	
	private double prunedEuclideanDistance(int indexA,int indexB){
		
		double length=0.0;
		double value;

		for (int i=0; i<nFeatures; i++) {		
			value = data[indexA][i]-data[indexB][i];
			length += (double)genes[i]*value*value;
		}
			
		length = Math.sqrt(length); 
				
		return length;
	}

	//Function that evaluates a chromosome
	public void evaluate () {

		double acc;
		double red;
		  
		acc=classifyData();
		red=getReductionRate();
		  
		fitnessValue=(beta*acc)+((1.0-beta)*red);
		  
		valid=true;
	}
	  
	private double getReductionRate(){
		  
		double rate=0.0;
		int count=0;
		  
		for(int i=0;i<genes.length;i++){
			count+=genes[i];
		}
		  
		rate= 1.0-((double)count/(double)genes.length);
		  
		return rate;
		  
	}
	  
	private double classifyData(){
		
		double acc=0.0;
		int result;
		int errors=0;
					
		for(int i=0;i<data.length;i++){
			result=knnClassifier(data[i],i);
			if(result!=dataOutput[i]){
				errors++;
			}
		}
		  
		acc= 1.0-((double)errors/(double)data.length);

		return acc;
	}

	private int knnClassifier(double [] example, int index){
			
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
			nearestN[i] = 0;
			minDist[i] = Double.MAX_VALUE;
		}

		//KNN Method starts here
		    
		for (int i=0; i<data.length; i++) {
			
			if(i!=index){
					
				dist = prunedEuclideanDistance(index,i);

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
			
		for (int i=0; i<K && nearestN[i]!=-1; i++) {
			selectedClasses[dataOutput[nearestN[i]]]+=1;
		}
		
		prediction=-1;
		predictionValue=0;
			
		for (int i=0; i<nClasses; i++) {
			if (predictionValue < selectedClasses[i]) {
				predictionValue = selectedClasses[i];
			    prediction = i;
			}
		}
		
		
			
		return prediction;

	}
	
	//PMX cross operator
	public int [] crossPMX (int [] parent) {
	
		int point1,point2;
		int down,up;
		int [] offspring;
		
		point1 = Randomize.Randint (0, parent.length-1);
		point2 = Randomize.Randint (0, parent.length-1);
		
	    if (point1 > point2) {
	    	up = point1;
	    	down = point2;
	    } 
	    else {
	    	up = point2;
	    	down = point1;
	    }
	    
	    //crossing first offspring (self)
	    
	    for(int i=down; i<up; i++){
	    	genes[i]=parent[i];
	    }
	    
	    //crossing second offspring (outter)
	    
	    offspring= new int [parent.length];
	    
	    for(int i=0; i<down; i++){
	    	offspring[i]=parent[i];
	    }
	    
	    for(int i=down; i<up; i++){
	    	offspring[i]=genes[i];
	    }
	    
	    for(int i=up; i<parent.length; i++){
	    	offspring[i]=parent[i];
	    }
	    
	    valid=false;
	    
	    return offspring;
	}

	//Mutation Operator
	public void mutation() {

		int i;
		boolean change=false;

	    for (i=0; i<genes.length; i++) {
	    	if (Randomize.Rand() < mutationProb) {
	    		genes[i]=(genes[i]+1)%2;
	    		change=true;
	    	}
	    }
	    
	    if(change){
		    valid=false;	    	
	    }
	}	
	

	//Function that lets compare cromosomes to sort easily
	public int compareTo (Object o1) {
		if (this.fitnessValue > ((Chromosome)o1).fitnessValue)
			return -1;
	    else if (this.fitnessValue < ((Chromosome)o1).fitnessValue)
	    	return 1;
	    else return 0;
	}	
	

	public String toString() {
	  
		int i;

		String temp = "[";

		for (i=0; i<genes.length; i++){
			temp += genes[i];
		}

		temp += ", " + String.valueOf(fitnessValue);

		return temp;
	}
}
