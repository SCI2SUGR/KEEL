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

/***********************************************************************

	This file is part of the Fuzzy Instance Based Learning package, a
	Java package implementing Fuzzy Nearest Neighbor Classifiers as 
	complementary material for the paper:
	
	Fuzzy Nearest Neighbor Algorithms: Taxonomy, Experimental analysis and Prospects

	Copyright (C) 2012
	
	J. Derrac (jderrac@decsai.ugr.es)
	S. García (sglopez@ujaen.es)
	F. Herrera (herrera@decsai.ugr.es)

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
 * File: PFKNN.java
 * 
 * The PFKNN algorithm. 
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2011 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Fuzzy_Instance_Based_Learning.PFKNN;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.core.Files;
import org.core.Randomize;

import keel.Algorithms.Fuzzy_Instance_Based_Learning.FuzzyIBLAlgorithm;
import keel.Algorithms.Fuzzy_Instance_Based_Learning.ReportTool;
import keel.Algorithms.Fuzzy_Instance_Based_Learning.Timer;
import keel.Algorithms.Fuzzy_Instance_Based_Learning.Util;

public class PFKNN extends FuzzyIBLAlgorithm {

	private int K;
	private int selected[];
	
	private double membership[][];
	private double referenceMembership [][];
	private double testMembership [][];
	
	/** 
	 * Reads the parameters of the algorithm. 
	 * 
	 * @param script Configuration script
	 * 
	 */
	@Override
	protected void readParameters(String script) {
		
		String file;
		String line;
		StringTokenizer fileLines, tokens;
		
	    file = Files.readFile (script);
	    fileLines = new StringTokenizer (file,"\n\r");
	    
	    //Discard in/out files definition
	    fileLines.nextToken();
	    fileLines.nextToken();
	    fileLines.nextToken();
	    
	    //Getting the seed
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    seed = Long.parseLong(tokens.nextToken().substring(1));
	    
	    //Getting the K parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    K = Integer.parseInt(tokens.nextToken().substring(1));

	    
	} //end-method	
	
	/**
	 * Main builder. Initializes the methods' structures
	 * 
	 * @param script Configuration script
	 */
	public PFKNN(String script){
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="PFKNN";

		selected = new int [trainData.length];
		membership = new double [trainData.length][nClasses];
		
		referenceMembership = new double [referenceData.length][nClasses];
		testMembership = new double [testData.length][nClasses];

	    //Initialization of Reporting tool
	    ReportTool.setOutputFile(outFile[2]);
	    
	} //end-method	
	
	private void selectBoundaryPoints(){
		
		Arrays.fill(selected, 0);
		
		//for each training instances find its K-nearest enemies
		//and select them
		double minDist[];
		int nearestN[];
		double dist;
		boolean stop;
		nearestN = new int[K];
		minDist = new double[K];
		
		for(int index = 0; index<trainData.length;index++){

		    for (int i=0; i<K; i++) {
				nearestN[i] = 0;
				minDist[i] = Double.MAX_VALUE;
			}
			
		    //KNN Method starts here
		    
			for (int i=0; i<trainData.length; i++) {
			
				if(trainOutput[i]!=trainOutput[index]){
					
				    dist = Util.euclideanDistance(trainData[i],trainData[index]);
		
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
			
			for(int j=0;j<K;j++){
				selected[nearestN[j]]=1;
			}			
		}

	}
	
	private void shuffleVector(int vector []){
		
		int pos,tmp;
		
	    for (int i=0; i<vector.length; i++) {
	    	
	    	pos = Randomize.Randint (0, vector.length);
	    	tmp = vector[i];
	    	vector[i] = vector[pos];
	    	vector[pos] = tmp;
	    }
	}

	private void accomodateClusters(){
		
		evaluateMembership();
		
		//set a random order for the instances
		int order [] = new int [trainData.length];
		
		for (int i=0; i<trainData.length; i++) {
			order[i]=i;
		}
		
		//test if all instances are correctly classified
		int index;
		
		double minDist[];
		int nearestN[];
		double dist;
		boolean stop;
		double classMembership[];
		
		classMembership=new double[nClasses];
		
		for (int i=0; i<trainData.length; i++) {
			
			index=order[i];
			Arrays.fill(classMembership, 0.0);
	
			//find its K nearest neighbors
			nearestN = new int[K];
			minDist = new double[K];
			
			for (int i2=0; i2<K; i2++) {
				nearestN[i2] = -1;
				minDist[i2] = Double.MAX_VALUE;
			}
				
			//KNN Method starts here
			    
			for (int i2=0; i2<trainData.length; i2++) {
				
				dist = Util.euclideanDistance(trainData[i2],trainData[index]);

				if (i2 != index){ //leave-one-out
					
					//see if it's nearer than our previous selected neighbors
					stop=false;
						
					for(int j=0;j<K && !stop;j++){
						
						if (dist < minDist[j]) {
							    
							for (int l = K - 1; l >= j+1; l--) {
								minDist[l] = minDist[l - 1];
								nearestN[l] = nearestN[l - 1];
							}	
								
							minDist[j] = dist;
							nearestN[j] = i2;
							stop=true;
						}
					}
				}
			}
				
			//compute its class membership
			double norm[];
			double sum;
			double MAX_NORM = 100000000;
				
			norm = new double [K];
			sum = 0.0;
				
			for(int n = 0;n<K;n++){
					
				if(nearestN[n]!=-1){
					if(minDist[n]==0.0){
						norm[n]=MAX_NORM;
					}
						
					norm[n] = 1.0/ Math.pow(minDist[n],(2.0/(2.0-1.0))); 
						
					norm[n]=Math.min(norm[n],MAX_NORM);
						
					sum+=norm[n];
				}
			}
				
			for(int n = 0;n<K;n++){
				if(nearestN[n]!=-1){
					for(int c=0;c<nClasses;c++){
						classMembership[c]+= membership[nearestN[n]][c]*(norm[n]/sum);
					}
				}
			}
			
			double max= Double.MIN_VALUE;
			int pred=-1;
			
			for(int c=0;c<nClasses;c++){
			
				if(max<classMembership[c]){
					max=classMembership[c];
					pred=c;
				}
				
			}
			
			if(pred!=trainOutput[index]){
				selected[index]=1;
				evaluateMembership();
			}
		}
		
	}
	
	private void evaluateMembership(){
				
		for(int instance=0;instance<trainData.length;instance++){
			
			if(selected[instance]==1){
				double minDist[];
				int nearestN[];
				int selectedClasses[];
				double dist;
				boolean stop;
	
				nearestN = new int[K];
				minDist = new double[K];
			
			    for (int i=0; i<K; i++) {
					nearestN[i] = 0;
					minDist[i] = Double.MAX_VALUE;
				}
				
			    //KNN Method starts here
			    
				for (int i=0; i<trainData.length; i++) {
				
				    dist = Util.euclideanDistance(trainData[i],trainData[instance]);
	
					if (selected[i]==1 && i != instance){ //leave-one-out
					
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
			
				Arrays.fill(selectedClasses, 0);
				
				for (int i=0; i<K; i++) {
					selectedClasses[trainOutput[nearestN[i]]]++;
				}
				
				
				Arrays.fill(membership[instance], 0.0);
				
				double term;
				for (int i=0; i<nClasses; i++) {
					term = ((double)selectedClasses[i]/(double)K);
					if(trainOutput[instance]==i){
						membership[instance][i]=0.51+0.49*term;
					}else{
						membership[instance][i]=0.49*term;
					}
				}
			}
			else{
				for (int i=0; i<nClasses; i++) {
					if(trainOutput[instance]==i){
						membership[instance][i]=1.0;
					}else{
						membership[instance][i]=0;
					}
				}
			}
		}
	}
	
	private void editTrainingSet(){
		
		int remove [] = new int [trainData.length];
		
		Arrays.fill(remove, 1);
		
		int winner;
		double minDist,dist;
		for(int instance=0;instance<trainData.length;instance++){
			
			winner=-1;
			minDist=Double.MAX_VALUE;
			for(int i=0;i<trainData.length;i++){
			
				if(selected[i]==1 && trainOutput[i]==trainOutput[instance]){
					
					dist=Util.euclideanDistance(trainData[instance], trainData[i]);
					
					if(minDist>dist){
						minDist=dist;
						winner=i;
					}
					
				}
			}
			
			if(winner!=-1){
				remove[winner]=0;
			}
			else{
				remove[instance]=0;
			}
		}
		
		for(int instance=0;instance<trainData.length;instance++){
			
			if(remove[instance]==1){
				selected[instance]=0;
			}
		}
		
		evaluateMembership();
	}


	/**
	 * Classifies the training set (leave-one-out)
	 */
	public void classifyTrain(){
	    
		//Start of training time
		Timer.resetTime();
		
		classifyTrainSet();
		
		//End of training time
		Timer.setTrainingTime();
		
		//Showing results
		System.out.println(name+" "+ relation + " Training " + Timer.getTrainingTime() + "s");
		
	} //end-method	
	
	/**
	 * Classifies the test set
	 */
	public void classifyTest(){
		    
		//Start of training time
		Timer.resetTime();
		
		classifyTestSet();
		
		//End of test time
		Timer.setTestTime();
		
		//Showing results
		System.out.println(name+" "+ relation + " Test " + Timer.getTestTime() + "s");	
		
	} //end-method	
	
	/**
	 * Classifies the training set
	 */
	public void classifyTrainSet(){
				
		for(int i=0;i<trainData.length;i++){
			
			trainPrediction[i]=classifyTrain(i,trainData[i]);
			
		}

	} //end-method	
	
	/**
	 * Classifies the test set
	 */
	public void classifyTestSet(){

		for(int i=0;i<testData.length;i++){

			testPrediction[i]=classifyTest(i,testData[i]);
			
		}

	} //end-method	
	
	/** 
	 * Evaluates a instance to predict its class membership
	 * 
	 * @param index Index of the instance in the test set
	 * @param example Instance evaluated 
	 * 
	 */
	private int classifyTrain(int index, double example[]) {
		
		double minDist[];
		int nearestN[];
		double dist;
		boolean stop;

		nearestN = new int[K];
		minDist = new double[K];
	
	    for (int i=0; i<K; i++) {
			nearestN[i] = 0;
			minDist[i] = Double.MAX_VALUE;
		}
		
	    //KNN Method starts here
	    
		for (int i=0; i<trainData.length; i++) {
		
			if(selected[i]==1 && index!=i){
			    dist = Util.euclideanDistance(trainData[i],example);
	
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
		
		//compute membership
		double norm[];
		double sum;
		
		norm = new double [K];
		sum = 0.0;
		double MAX_NORM = 100000000;
		
		for(int i = 0;i<K;i++){
			
			if(minDist[i]==0.0){
				norm[i]=MAX_NORM;
			}
			
			norm[i] = 1.0/ Math.pow(minDist[i],(2.0/(2.0-1.0))); 
			
			norm[i]=Math.min(norm[i],MAX_NORM);
			
			sum+=norm[i];
		}
		
		for(int i = 0;i<K;i++){
			for(int c=0;c<nClasses;c++){
				referenceMembership [index][c]+= membership[nearestN[i]][c]*(norm[i]/sum);
			}
		}
		
		return computeClass(referenceMembership [index]);
	}
	
	/** 
	 * Evaluates a instance to predict its class membership
	 * 
	 * @param index Index of the instance in the test set
	 * @param example Instance evaluated 
	 * 
	 */
	private int classifyTest(int index, double example[]) {
		
		double minDist[];
		int nearestN[];
		double dist;
		boolean stop;

		nearestN = new int[K];
		minDist = new double[K];
	
	    for (int i=0; i<K; i++) {
			nearestN[i] = 0;
			minDist[i] = Double.MAX_VALUE;
		}
		
	    //KNN Method starts here
	    
		for (int i=0; i<trainData.length; i++) {
		
			if(selected[i]==1){
			    dist = Util.euclideanDistance(trainData[i],example);
	
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
		
		//compute membership
		double norm[];
		double sum;
		
		norm = new double [K];
		sum = 0.0;
		double MAX_NORM = 100000000;
		
		for(int i = 0;i<K;i++){
			
			if(minDist[i]==0.0){
				norm[i]=MAX_NORM;
			}
			
			norm[i] = 1.0/ Math.pow(minDist[i],(2.0/(2.0-1.0))); 
			
			norm[i]=Math.min(norm[i],MAX_NORM);
			
			sum+=norm[i];
		}
		
		for(int i = 0;i<K;i++){
			for(int c=0;c<nClasses;c++){
				testMembership [index][c]+= membership[nearestN[i]][c]*(norm[i]/sum);
			}
		}
		
		return computeClass(testMembership [index]);
		
	}
	
	/**
	 * Computes the class of a instance given its membership array
	 * @param pertenence Membership array
	 * 
	 * @return Class assigned (crisp)
	 */
	private int computeClass(double pertenence[]){
		
		double max = Double.MIN_VALUE;
		
		int output=-1;
		
		for(int i=0; i< pertenence.length;i++){
			if(max<pertenence[i]){
				max=pertenence[i];
				output=i;
			}
		}
		
		return output;
		
	} //end-method	
	
	/**
	 * Generates the model of the algorithm
	 */
	public void generateModel(){
		
		//Start of model time
		Timer.resetTime();	
		
		selectBoundaryPoints();
		
		accomodateClusters();
		
		editTrainingSet();
		
		//End of model time
		Timer.setModelTime();
	
		//Showing results
		System.out.println(name+" "+ relation + " Model " + Timer.getModelTime() + "s");
		
	}
	
	
	/**
	 * Reports the results obtained
	 */
	public void printReport(){
		
		writeOutput(outFile[0], trainOutput, trainPrediction);
		writeOutput(outFile[1], testOutput, testPrediction);
		
		ReportTool.setResults(trainOutput,trainPrediction,testOutput,testPrediction,nClasses);
		
		ReportTool.printReport();
		
	} //end-method	
	
} //end-class 
