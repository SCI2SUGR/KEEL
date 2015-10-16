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
 * File: JFKNN.java
 * 
 * The JFKNN algorithm. 
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2011 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Fuzzy_Instance_Based_Learning.JFKNN;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.core.Files;

import keel.Algorithms.Fuzzy_Instance_Based_Learning.FuzzyIBLAlgorithm;
import keel.Algorithms.Fuzzy_Instance_Based_Learning.ReportTool;
import keel.Algorithms.Fuzzy_Instance_Based_Learning.Timer;
import keel.Algorithms.Fuzzy_Instance_Based_Learning.Util;

public class JFKNN extends FuzzyIBLAlgorithm {

	private double distances[][];
	private int trainInstances;
	private int testInstances;
	private int totalInstances;
	
	private int H;
	private double bestError;
	private double globalError;
	
	private Triplet tripletArray [];
	private Triplet bestTriplet;
	private Triplet baseTriplet;
	private Triplet bestTripletPhase1;
	private double phase1Error;
	private Triplet bestTripletPhase2;
	private double phase2Error;
	
	private double Vbase[][];
	
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


	} //end-method	

	/**
	 * Main builder. Initializes the methods' structures
	 * 
	 * @param script Configuration script
	 */
	public JFKNN(String script){
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="Jozwik Fuzzy K-NN";

		trainInstances=trainData.length;
		testInstances=testData.length;
		totalInstances=trainInstances+testInstances;
		
		distances= new double [totalInstances][totalInstances];
		
	    //Initialization of Reporting tool
	    ReportTool.setOutputFile(outFile[2]);

	} //end-method	
	
	/**
	 * Generates the model of the algorithm
	 */
	public void generateModel (){
		
		int index1,index2;
		
		//Start of model time
		Timer.resetTime();	

		//compute all possible distances (for optimizing)
		for(int i=0;i<totalInstances;i++){
			distances[i][i]=0.0;	
			if(i<trainInstances){
				index1=i;
				for(int j=i+1;j<totalInstances;j++){		
					if(j<trainInstances){
						index2=j;
						distances[i][j]=Util.euclideanDistance(trainData[index1], trainData[index2]);
						distances[j][i]=distances[i][j];
					}else{
						index2=j-trainInstances;
						distances[i][j]=Util.euclideanDistance(trainData[index1], testData[index2]);
						distances[j][i]=distances[i][j];
					}
				}	
			}else{
				index1=i-trainInstances;
				for(int j=index1+1;j<testInstances;j++){
					index2=j;
					distances[i][j]=Util.euclideanDistance(testData[index1], testData[index2]);	
					distances[j][i]=distances[i][j];
				}	
			}
		}

		bestTriplet=new Triplet(trainInstances,nClasses);
		
		bestTriplet.error=1.0;
		bestTriplet.k=0;
		
		
		for(int i=0;i<trainInstances;i++){
			bestTriplet.w[i][trainOutput[i]]=1.0;
		}
		
		baseTriplet= new Triplet(bestTriplet);
		
		tripletArray= new Triplet[trainInstances-1];
		
		//First Stage
		
		bestError=1.0;
		H=0;
		
		double partialBestError=1.0;;
		int partialIndex=-1;
		do{
			globalError=bestError;
			H++;
			
			for(int i=0;i<trainInstances-1;i++){
				tripletArray[i]=new Triplet(baseTriplet.w);
				tripletArray[i].k=i+1;
			}

			for(int i=0;i<tripletArray.length;i++){
				leaveOneOutTriplet(tripletArray[i]);
				if(partialBestError>tripletArray[i].error){
					partialBestError=tripletArray[i].error;
					partialIndex=i;
				}
			}
			
			if(bestError>partialBestError){
				bestError=partialBestError;
				generateNewTriplets(tripletArray[partialIndex]);	
				
			}
			
			
			System.out.println("Iteration number "+H+" Current Error: "+globalError+" Best Error: "+bestError);

		}while(globalError>bestError);
		
		phase1Error=globalError;
		
		//end-first-stage
		
		//obtaining of the V array
		Vbase = new double [testInstances][nClasses];
		
		VClassification(bestTriplet.w,bestTriplet.k);
		
		//generating new base and best triplets
		
		bestTripletPhase1=new Triplet(bestTriplet);
		
		bestTriplet=new Triplet(totalInstances,nClasses);
		
		bestTriplet.error=1.0;
		bestTriplet.k=0;
		
		for(int i=0;i<trainInstances;i++){
			bestTriplet.w[i][trainOutput[i]]=1.0;
		}
		
		for(int i=0;i<testInstances;i++){
			for(int j=0;j<nClasses;j++){
				bestTriplet.w[i+trainInstances][j]=Vbase[i][j];
			}
			
		}
		
		baseTriplet= new Triplet(bestTriplet);
		
		tripletArray= new Triplet[totalInstances-1];
		
		//Second Stage
		
		bestError=1.0;
		H=0;
		
		partialBestError=1.0;;
		partialIndex=-1;
		
		do{
			globalError=bestError;
			H++;
			
			for(int i=0;i<totalInstances-1;i++){
				tripletArray[i]=new Triplet(baseTriplet.w);
				tripletArray[i].k=i+1;
			}

			for(int i=0;i<tripletArray.length;i++){
				leaveOneOutTripletSecond(tripletArray[i]);
				if(partialBestError>tripletArray[i].error){
					partialBestError=tripletArray[i].error;
					partialIndex=i;
				}
			}
			
			if(bestError>partialBestError){
				bestError=partialBestError;
				generateNewTripletsSecond(tripletArray[partialIndex]);				
			}
			
			
			System.out.println("Iteration number "+H+" Current Error: "+globalError+" Best Error: "+bestError);

		}while(globalError>bestError);
		
		phase2Error=globalError;
		bestTripletPhase2=new Triplet(bestTriplet);
		
		//End of model time
		Timer.setModelTime();
	
		//Showing results
		System.out.println(name+" "+ relation + " Model " + Timer.getModelTime() + "s");
		
	} //end-method	
	
	/**
	 * Performs the preliminary classification of the test set using the
	 * instances obtained at the first stage
	 * 
	 * @param memberships matrix of membership of the training set
	 * @param k best k value found
	 */
	private void VClassification(double memberships [][],int k){
	
		for(int instance=0;instance<testInstances;instance++){
				
			double minDist[];
			int nearestN[];
			
			double dist;
			boolean stop;

			nearestN = new int[k];
			minDist = new double[k];
		
			for (int i=0; i<k; i++) {
				nearestN[i] = 0;
				minDist[i] = Double.MAX_VALUE;
			}
				
			//KNN Method starts here
			    
			for (int i=0; i<trainData.length; i++) {
				
				dist = distances[i][trainInstances+instance];
					
				//see if it's nearer than our previous selected neighbors
				stop=false;
						
				for(int j=0;j<k && !stop;j++){
						
					if (dist < minDist[j]) {
							    
						for (int l = k - 1; l >= j+1; l--) {
							minDist[l] = minDist[l - 1];
							nearestN[l] = nearestN[l - 1];
						}	
								
						minDist[j] = dist;
						nearestN[j] = i;
						stop=true;
					}
				}
				
			}
			
			Arrays.fill(Vbase[instance],0.0);
			
			for(int i=0;i<nearestN.length;i++){
				for(int j=0;j<nClasses;j++){
					Vbase[instance][j]+=memberships[nearestN[i]][j];
				}
			}
			
			for(int j=0;j<nClasses;j++){
				Vbase[instance][j]/=k;
			}

		}
		
	}//end-method
	
	/**
	 * Generates a new sequence of triplets for a new step of the iterative process
	 * 
	 * @param best current best triplet found
	 */
	private void generateNewTriplets(Triplet best){
		
		bestTriplet=new Triplet(best);	
		
		for(int i=0;i<trainInstances;i++){
			for(int j=0;j<nClasses;j++){
				baseTriplet.w[i][j]=((best.w[i][j]*(double)best.k)+baseTriplet.w[i][j])/((double)best.k+1.0);
			}	
		}
		
	}//end-method
	
	/**
	 * Generates a new sequence of triplets for a new step of the iterative process
	 * This time, both training and test instances are considered
	 * 
	 * @param best current best triplet found
	 */
	private void generateNewTripletsSecond(Triplet best){
		
		bestTriplet=new Triplet(best);	
		
		for(int i=0;i<totalInstances;i++){
			for(int j=0;j<nClasses;j++){
				baseTriplet.w[i][j]=((best.w[i][j]*(double)best.k)+baseTriplet.w[i][j])/((double)best.k+1.0);
			}	
		}
	}//end-method
	
	/**
	 * Performs a LOO process on a triplet, to estimate its error
	 * 
	 * @param set triplet to analyze
	 */
	private void leaveOneOutTriplet(Triplet set){
		
		double selectedClasses[];
		double oldW[][];
		double term;
		double bestMembership;
		int trueOutput;
		int expectedOutput;
		int misses;
		
		misses=0;
		
		selectedClasses= new double[nClasses];
		
		oldW= new double [trainInstances][nClasses];
		
		for(int i=0;i<trainInstances;i++){
			System.arraycopy(set.w[i], 0, oldW[i], 0, oldW[i].length);
		}
		
		for(int instance=0;instance<trainInstances;instance++){
				
			double minDist[];
			int nearestN[];
			
			double dist;
			boolean stop;

			nearestN = new int[set.k];
			minDist = new double[set.k];
		
			for (int i=0; i<set.k; i++) {
				nearestN[i] = 0;
				minDist[i] = Double.MAX_VALUE;
			}
				
			//KNN Method starts here
			    
			for (int i=0; i<trainData.length; i++) {
				
				dist = distances[i][instance];
				
				if (i != instance){ //leave-one-out
					
					//see if it's nearer than our previous selected neighbors
					stop=false;
						
					for(int j=0;j<set.k && !stop;j++){
						
						if (dist < minDist[j]) {
							    
							for (int l = set.k - 1; l >= j+1; l--) {
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
			
		
			Arrays.fill(selectedClasses, 0.0);
			
			for (int i=0; i<set.k; i++) {
				for(int j=0;j<nClasses;j++){
					selectedClasses[j]+=oldW[nearestN[i]][j];

				}

			}
			
			bestMembership=0.0;
			expectedOutput=-1;
			for (int i=0; i<nClasses; i++) {
				term = ((double)selectedClasses[i]/(double)set.k);
				set.w[instance][i]=term;

				if(term>bestMembership){
					bestMembership=term;
					expectedOutput=i;
				}
			}

			trueOutput=trainOutput[instance];

			if(trueOutput!=expectedOutput){
				
				misses++;
			}
		}
		
		//compute LOO error
		set.error=(double)((double)misses/(double)trainInstances);
		
	}//end-method
	
	/**
	 * Performs a LOO process on a triplet, to estimate its error
	 * This time, both training and test instances are considered
	 * 
	 * @param set triplet to analyze
	 */
	private void leaveOneOutTripletSecond(Triplet set){
		
		double selectedClasses[];
		double oldW[][];
		double term;
		double bestMembership;
		int trueOutput;
		int expectedOutput;
		int misses;
		
		misses=0;
		
		selectedClasses= new double[nClasses];
		
		oldW= new double [totalInstances][nClasses];
		
		for(int i=0;i<totalInstances;i++){
			System.arraycopy(set.w[i], 0, oldW[i], 0, oldW[i].length);
		}
		
		for(int instance=0;instance<totalInstances;instance++){
				
			double minDist[];
			int nearestN[];
			
			double dist;
			boolean stop;

			nearestN = new int[set.k];
			minDist = new double[set.k];
		
			for (int i=0; i<set.k; i++) {
				nearestN[i] = 0;
				minDist[i] = Double.MAX_VALUE;
			}
				
			//KNN Method starts here
			    
			for (int i=0; i<totalInstances; i++) {
				
				dist = distances[i][instance];
				
				if (i != instance){ //leave-one-out
					
					//see if it's nearer than our previous selected neighbors
					stop=false;
						
					for(int j=0;j<set.k && !stop;j++){
						
						if (dist < minDist[j]) {
							    
							for (int l = set.k - 1; l >= j+1; l--) {
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
			
		
			Arrays.fill(selectedClasses, 0.0);
			
			for (int i=0; i<set.k; i++) {
				for(int j=0;j<nClasses;j++){
					selectedClasses[j]+=oldW[nearestN[i]][j];

				}

			}
			
			bestMembership=0.0;
			expectedOutput=-1;
			for (int i=0; i<nClasses; i++) {
				term = ((double)selectedClasses[i]/(double)set.k);
				set.w[instance][i]=term;

				if(term>bestMembership){
					bestMembership=term;
					expectedOutput=i;
				}
			}

			if(instance<trainInstances){
				trueOutput=trainOutput[instance];
				
				if(trueOutput!=expectedOutput){
					
					misses++;
				}
			}
			
		}
		
		//compute LOO error
		set.error=(double)((double)misses/(double)trainInstances);
		
	}//end-method
	
	/**
	 * Predict the class of an instance given its class membership array
	 * 
	 * @param classArray class membership array
	 * @return class of the instance
	 */
	private int predictClass(double classArray[]){
		
		int output =-1;
		double membership = 0.0;
		
		for(int j=0;j<nClasses;j++){
			if(membership<classArray[j]){
				membership=classArray[j];
				output=j;
			}
		}
		
		return output;
		
	}//end-method
	
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
		
		if(phase1Error<=phase2Error){
			for(int i=0;i<trainData.length;i++){
				
				trainPrediction[i]=predictClass(bestTripletPhase1.w[i]);
				
			}
			
		}else{
			for(int i=0;i<trainData.length;i++){
				
				trainPrediction[i]=predictClass(bestTripletPhase2.w[i]);
				
			}
			
		}

	} //end-method	
	
	/**
	 * Classifies the test set
	 */
	public void classifyTestSet(){

		if(phase1Error<=phase2Error){
			for(int i=0;i<testData.length;i++){
				
				testPrediction[i]=predictClass(Vbase[i]);
				
			}
			
		}else{
			for(int i=0;i<testData.length;i++){
				
				testPrediction[i]=predictClass(bestTripletPhase2.w[trainInstances+i]);
				
			}
			
		}

	} //end-method	
		
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
