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



package keel.Algorithms.Fuzzy_Instance_Based_Learning.FuzzyNPC;

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
 * File: FuzzyNPC.java
 * 
 * The FuzzyNPC algorithm. 
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2011 
 * @version 1.0 
 * @since JDK1.5
 * 
 */
public class FuzzyNPC extends FuzzyIBLAlgorithm {

	private static final double MAX_NORM = 100000000;
	
	private double M; //M value for Fuzzy K-NN norm
	
	private double referenceMembership [][];
	private double testMembership [][];
	private double prototypes [][];
	
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
	    
	    //Getting the M parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    M = Double.parseDouble(tokens.nextToken().substring(1));

	} //end-method	

	/**
	 * Main builder. Initializes the methods' structures
	 * 
	 * @param script Configuration script
	 */
	public FuzzyNPC(String script){
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="Fuzzy Nearest Prototype Classifier";

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
		
		//compute prototypes
		prototypes = new double [nClasses][inputAtt];
		
		for(int i=0;i<nClasses;i++){
			Arrays.fill(prototypes[i],0.0);
		}
		
		for(int i=0;i<trainData.length;i++){
			for(int j=0;j<trainData[0].length;j++){
				prototypes[trainOutput[i]][j]+=trainData[i][j];
			}
		}
		
		for(int i=0;i<nClasses;i++){
			for(int j=0;j<trainData[0].length;j++){
				if(nInstances[i]>0){
					prototypes[i][j]/=(double)nInstances[i];
				}
			}
		}

		//End of model time
		Timer.setModelTime();
	
		//Showing results
		System.out.println(name+" "+ relation + " Model " + Timer.getModelTime() + "s");
		
	} //end-method	
	
	
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
			
			computeTrainMembership(i,referenceData[i]);
			trainPrediction[i]=computeClass(referenceMembership[i]);
			
		}

	} //end-method	
	
	/**
	 * Classifies the test set
	 */
	public void classifyTestSet(){

		for(int i=0;i<testData.length;i++){
			
			computeTestMembership(i,testData[i]);
			testPrediction[i]=computeClass(testMembership[i]);
			
		}

	} //end-method	
	
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
	 * Evaluates a instance to predict its class membership
	 * 
	 * @param index Index of the instance in the test set
	 * @param example Instance evaluated 
	 * 
	 */
	private void computeTrainMembership(int index, double example[]) {
	
		double [] norms;
		double sumNorm;

		norms = new double [nClasses];
		sumNorm=0.0;
		
		for (int i=0; i<nClasses; i++) {
			if(nInstances[i]>0){
				
				norms[i] = Util.euclideanDistance(prototypes[i],example);
				norms[i] = 1.0/ Math.pow(norms[i],(2.0/(M-1.0)));
				
				norms[i]=Math.min(norms[i],MAX_NORM);
				
				sumNorm+=norms[i];

			}
		}	
		 
		
		for (int i=0; i<nClasses; i++) {
			if(nInstances[i]>0){
				referenceMembership [index][i] = norms[i]/sumNorm;
			}
			else{
				referenceMembership [index][i]= 0.0;
			}
		}

	} //end-method	
	
	/** 
	 * Evaluates a instance to predict its class membership
	 * 
	 * @param index Index of the instance in the test set
	 * @param example Instance evaluated 
	 * 
	 */
	private void computeTestMembership(int index, double example[]) {
	
		double [] norms;
		double sumNorm;

		norms = new double [nClasses];
		sumNorm=0.0;
		
		for (int i=0; i<nClasses; i++) {
			if(nInstances[i]>0){
				norms[i] = Util.euclideanDistance(prototypes[i],example);
				norms[i] = 1.0/ Math.pow(norms[i],(2.0/(M-1.0)));
				
				norms[i]=Math.min(norms[i],MAX_NORM);
				
				sumNorm+=norms[i];

			}
		}	
		 
		
		for (int i=0; i<nClasses; i++) {
			if(nInstances[i]>0){
				testMembership [index][i] = norms[i]/sumNorm;
			}
			else{
				testMembership [index][i]= 0.0;
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
