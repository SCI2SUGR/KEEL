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
 * File: IF_KNN.java
 * 
 * The IF-KNN algorithm. 
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2011 
 * @version 1.0 
 * @since JDK1.5
 * 
 */


package keel.Algorithms.Fuzzy_Instance_Based_Learning.IF_KNN;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.core.Files;

import keel.Algorithms.Fuzzy_Instance_Based_Learning.FuzzyIBLAlgorithm;
import keel.Algorithms.Fuzzy_Instance_Based_Learning.ReportTool;
import keel.Algorithms.Fuzzy_Instance_Based_Learning.Timer;
import keel.Algorithms.Fuzzy_Instance_Based_Learning.Util;

public class IF_KNN extends FuzzyIBLAlgorithm {

	private int K;
	private double mA;
	private double vA;
	private double mR;
	private double vR;
	private int k;
	
	private double membership [][];
	
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
	    
	    //Getting the K parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    K = Integer.parseInt(tokens.nextToken().substring(1));
	    
	    //Getting the mA parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    mA = Double.parseDouble(tokens.nextToken().substring(1));
	    
	    //Getting the vA parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    vA = Double.parseDouble(tokens.nextToken().substring(1));
	    
	    //Getting the mR parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    mR = Double.parseDouble(tokens.nextToken().substring(1));
	    
	    //Getting the vR parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    vR = Double.parseDouble(tokens.nextToken().substring(1));
	    
	    //Getting the k parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    k = Integer.parseInt(tokens.nextToken().substring(1));
	    
	} //end-method	

	/**
	 * Main builder. Initializes the methods' structures
	 * 
	 * @param script Configuration script
	 */
	public IF_KNN(String script){
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="IF_KNN";
		
		membership = new double [trainData.length][nClasses];
		
		for(int i=0;i<trainData.length;i++){
			
			Arrays.fill(membership[i], -1.0);
		}
		
		referenceMembership = new double [referenceData.length][nClasses];
		testMembership = new double [testData.length][nClasses];

	    //Initialization of Reporting tool
	    ReportTool.setOutputFile(outFile[2]);
	    
	} //end-method	

	/**
	 * Generates the model of the algorithm
	 */
	public void generateModel (){
		
		//Start of model time
		Timer.resetTime();	

		
		computeMembership();
	

		//End of model time
		Timer.setModelTime();
	
		//Showing results
		System.out.println(name+" "+ relation + " Model " + Timer.getModelTime() + "s");
		
	}

	private void computeMembership(){
		
		for(int instance=0;instance<trainData.length;instance++){
			
			double minDist[];
			int nearestN[];
			int selectedClasses[];
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
			
			    dist = Util.euclideanDistance(trainData[i],trainData[instance]);

				if (i != instance){ //leave-one-out
				
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
			}
			
			//we have check all the instances... see what is the most present class
			selectedClasses= new int[nClasses];
		
			Arrays.fill(selectedClasses, 0);
			
			for (int i=0; i<k; i++) {
				selectedClasses[trainOutput[nearestN[i]]]++;
			}
			
			
			Arrays.fill(membership[instance], 0.0);
			
			double term;
			for (int i=0; i<nClasses; i++) {
				term = ((double)selectedClasses[i]/(double)k);
				if(trainOutput[instance]==i){
					membership[instance][i]=0.51+0.49*term;
				}else{
					membership[instance][i]=0.49*term;
				}
			}

		}
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
			
			trainPrediction[i]=classifyTrainInstance(i,trainData[i]);
			
		}

	} //end-method	
	
	/**
	 * Classifies the test set
	 */
	public void classifyTestSet(){

		for(int i=0;i<testData.length;i++){
			
			testPrediction[i]=classifyTestInstance(i,testData[i]);
			
		}

	} //end-method	
	
	/** 
	 * Classifies an instance of the training set
	 * 
	 * @param index Index of the instance in the test set
	 * @param example Instance evaluated 
	 * @return class computed
	 */
	private int classifyTrainInstance(int index, double example[]) {
	
		double minDist[];
		int nearestN[];
		double dist;
		boolean stop;
		int result;

		nearestN = new int[K];
		minDist = new double[K];
	
	    for (int i=0; i<K; i++) {
			nearestN[i] = 0;
			minDist[i] = Double.MAX_VALUE;
		}
		
	    //KNN Method starts here
	    
		for (int i=0; i<trainData.length; i++) {
		
			if(i!=index){ //leave-one-out
				
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
		
		//voting process
		
		Arrays.fill(referenceMembership[index], 0.0);
		
		for(int i=0;i<K;i++){
			for(int c=0;c<nClasses;c++){
				referenceMembership[index][c]+=computeVote(membership[nearestN[i]][c]);
			}
		}
		
		for(int c=0;c<nClasses;c++){
			referenceMembership[index][c]=0.5*(1.0+(referenceMembership[index][c]/(double)K));
		}
		
		result=computeClass(referenceMembership[index]);
		
		return result;
		
	} //end-method	
	
	/** 
	 * Classifies an instance of the test set
	 * 
	 * @param index Index of the instance in the test set
	 * @param example Instance evaluated 
	 * @return class computed
	 */
	private int classifyTestInstance(int index, double example[]) {
	
		double minDist[];
		int nearestN[];
		double dist;
		boolean stop;
		int result;

		nearestN = new int[K];
		minDist = new double[K];
	
	    for (int i=0; i<K; i++) {
			nearestN[i] = 0;
			minDist[i] = Double.MAX_VALUE;
		}
		
	    //KNN Method starts here
	    
		for (int i=0; i<trainData.length; i++) {
				
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
		
		//voting process
		
		Arrays.fill(testMembership[index], 0.0);
		
		for(int i=0;i<K;i++){
			for(int c=0;c<nClasses;c++){
				testMembership[index][c]+=computeVote(membership[nearestN[i]][c]);
			}
		}
		
		for(int c=0;c<nClasses;c++){
			testMembership[index][c]=0.5*(1.0+(testMembership[index][c]/(double)K));
		}
		
		result=computeClass(testMembership[index]);
		
		return result;
		
	} //end-method	

	private double computeVote(double value){
		
		double mem, nonMem;
		
		mem = value;
		nonMem = 1.0-value;
		
		if((mem>=mA)&&(nonMem<=vA)){
			return mem;
		}
		
		if((mem<mR)&&(nonMem>vR)){
			return (-1.0*nonMem);
		}
		
		return 0.0;
	}
	
	private int computeClass(double member[]){
		
		int out=0;
		double max=member[0];
		
		for(int i=1; i< member.length;i++){
			if(max<member[i]){
				max=member[i];
				out=i;
			}
		}
		
		return out;
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
