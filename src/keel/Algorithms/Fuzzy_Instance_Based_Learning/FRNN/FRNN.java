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
 * File: FRNN.java
 * 
 * The FRNN algorithm. 
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2011 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Fuzzy_Instance_Based_Learning.FRNN;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.core.Files;

import keel.Algorithms.Fuzzy_Instance_Based_Learning.FuzzyIBLAlgorithm;
import keel.Algorithms.Fuzzy_Instance_Based_Learning.ReportTool;
import keel.Algorithms.Fuzzy_Instance_Based_Learning.Timer;
import keel.Algorithms.Fuzzy_Instance_Based_Learning.Util;

public class FRNN extends FuzzyIBLAlgorithm {

	private double instance [];
	private double k [];
	private double classPosibility [];
	private final double Q = 2.0;
	
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
	public FRNN(String script){
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="Fuzzy Rough nearest neighbor";

		instance= new double [inputAtt];
		k= new double [inputAtt];
		classPosibility= new double [nClasses];
		
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
	
		
		int output=-1;
		double distance;
		
		if(train){
			System.arraycopy(trainData[index], 0, instance, 0, inputAtt);
			//compute k array
			computeKTrain(index);
		}else{
			System.arraycopy(testData[index], 0, instance, 0, inputAtt);
			//compute k array
			computeKTest(index);
		}
		
		Arrays.fill(classPosibility,0.0);
		
		
		for(int i=0; i<trainData.length;i++){
			
			if((!train)||(index!=i)){
				//compute squared weighted distance
				distance=0.0;
				for(int j=0;j<inputAtt;j++){
					distance+= k[j]*(instance[j]-trainData[i][j])*(instance[j]-trainData[i][j]);
				}

				//crisp classification is assumed for training instances
				if(!train){
					classPosibility[trainOutput[i]]+= Math.exp(Math.pow(-1.0*distance,1.0/(Q-1.0)))/(double)(trainData.length);
				}else{
					classPosibility[trainOutput[i]]+=  Math.exp(Math.pow(-1.0*distance,1.0/(Q-1.0)))/(double)(trainData.length-1.0);
				}
			}

		}
		
		//compute class of maximum posibility
		double max=Double.MIN_VALUE;
		output=-1;
		
		for(int c=0;c<nClasses;c++){
			
			if(max<classPosibility[c]){
				max=classPosibility[c];
				output=c;
			}
		}
		
		return output;
	}	
	
	private void computeKTrain(int index){
		
		double dist;
		
		double sum;
		
		double exp= 2.0/(Q-1.0);
		for(int j=0;j<inputAtt;j++){
			sum=0.0;
			for(int i=0;i<trainData.length;i++){
				
				if(i!=index){
					dist=trainData[index][j]-trainData[i][j];
					dist=Math.pow(dist,exp);
					sum+=dist;
				}
			}
			
			if(sum!=0){
				k[j]=(double)(trainData.length-1.0)/(2.0*sum);
			}
			else{
				k[j]=0;
			}

		}
		
		
	}
	
	private void computeKTest(int index){
		
		double dist;
		
		double sum;
		
		double exp= 2.0/(Q-1.0);
		for(int j=0;j<inputAtt;j++){
			sum=0.0;
			for(int i=0;i<trainData.length;i++){
				dist=testData[index][j]-trainData[i][j];
				dist=Math.pow(dist,exp);
				sum+=dist;
			}
			
			if(sum!=0){
				k[j]=(double)(trainData.length-1.0)/(2.0*sum);
			}
			else{
				k[j]=0;
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
			trainPrediction[i]=classifyInstance(i,true);	
		}
		
	} //end-method	
	
	/**
	 * Classifies the test set
	 */
	public void classifyTestSet(){

		for(int i=0;i<testData.length;i++){
			testPrediction[i]=classifyInstance(i,false);	
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
