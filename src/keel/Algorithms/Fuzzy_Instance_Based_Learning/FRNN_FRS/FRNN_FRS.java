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



package keel.Algorithms.Fuzzy_Instance_Based_Learning.FRNN_FRS;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.core.Files;

import keel.Algorithms.Fuzzy_Instance_Based_Learning.FuzzyIBLAlgorithm;
import keel.Algorithms.Fuzzy_Instance_Based_Learning.ReportTool;
import keel.Algorithms.Fuzzy_Instance_Based_Learning.Timer;
import keel.Algorithms.Fuzzy_Instance_Based_Learning.Util;

/**
 * 
 * File: FRNN_FRS.java
 * 
 * The FRNN_FRS algorithm. 
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2011 
 * @version 1.0 
 * @since JDK1.5
 * 
 */
public class FRNN_FRS extends FuzzyIBLAlgorithm {

	private int K;
	
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


	} //end-method	

	/**
	 * Main builder. Initializes the methods' structures
	 * 
	 * @param script Configuration script
	 */
	public FRNN_FRS(String script){
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="Fuzzy Rough nearest neighbor";

		
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

		
		//End of model time
		Timer.setModelTime();
	
		//Showing results
		System.out.println(name+" "+ relation + " Model " + Timer.getModelTime() + "s");
		
	} //end-method	
	
	private int classifyInstance(int index, boolean train){
	
	
		double minDist[];
		int nearestN[];
		double dist;
		boolean stop;
		double min;
		
		double quality;
		double R[];
		
		double lower[];
		double upper[];
		int outputClass;

		nearestN = new int[K];
		minDist = new double[K];
		R = new double[K];
		lower = new double[nClasses];
		upper = new double[nClasses];
		
	
	    for (int i=0; i<K; i++) {
			nearestN[i] = 0;
			minDist[i] = Double.MAX_VALUE;
		}
		
	    //KNN Method starts here
	    
		for (int i=0; i<trainData.length; i++) {
		
			if(train){
				if(i==index){
					dist = Double.MAX_VALUE;
				}else{
					dist = Util.euclideanDistance(trainData[i],trainData[index]);
				}
			}
			else{
				dist = Util.euclideanDistance(trainData[i],testData[index]);
			}
		    

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
		
		quality=0;
		
		Arrays.fill(R, 0.0);
		Arrays.fill(lower, 1.0);
		Arrays.fill(upper, 0.0);
		min=Double.MAX_VALUE;

		for (int l = 0; l< K; l++){	
			for(int j=0;j<inputAtt;j++){
				
				if(train){
					dist=1.0-Math.abs(trainData[nearestN[l]][j]-trainData[index][j]);
				}
				else{
					dist=1.0-Math.abs(trainData[nearestN[l]][j]-testData[index][j]);
				}
				
				if(min>dist){
					min=dist;
				}
	
			}
		
			R[l]=min;
			
			//A(x) is 1 for the training output class... 0 for the rest
			for(int c=0; c<nClasses; c++){

				if(c==trainOutput[nearestN[l]]){
					//lower approximation
					lower[c]=Math.min(lower[c], Math.max(1.0-R[l],1.0));
					//upper approximation
					upper[c]=Math.max(upper[c], Math.min(R[l],1.0));
				}
				else{
					lower[c]=Math.min(lower[c], Math.max(1.0-R[l],0.0));
					//upper approximation
					upper[c]=Math.max(upper[c], Math.min(R[l],0.0));
				}
			}

		}
		
		outputClass=-1;
		
		for(int c=0; c<nClasses; c++){
			
			if(quality<(lower[c]+upper[c])/2.0){
				quality=(lower[c]+upper[c])/2.0;
				outputClass=c;
			}

		}

		return outputClass;
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
			trainPrediction[i]=classifyInstance(i, true);
		}
		
	} //end-method	
	
	/**
	 * Classifies the test set
	 */
	public void classifyTestSet(){

		for(int i=0;i<testData.length;i++){
			testPrediction[i]=classifyInstance(i, false);
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
