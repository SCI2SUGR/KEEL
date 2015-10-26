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



package keel.Algorithms.Fuzzy_Instance_Based_Learning.D_SKNN;

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
 * File: D_SKNN.java
 * 
 * The D-SKNN algorithm. 
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2011 
 * @version 1.0 
 * @since JDK1.5
 * 
 */
public class D_SKNN extends FuzzyIBLAlgorithm {

	private int K;
	
	private double inner [];
	private double outter [];
	private double mr [];
	private double ms [];
	private boolean exists [];
	private double gamma [];
	private double ALPHA;
	private double BETA;

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
		String type;
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
	    
	    //Getting the beta parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    ALPHA = Double.parseDouble(tokens.nextToken().substring(1));
	    
	    //Getting the beta parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    BETA = Double.parseDouble(tokens.nextToken().substring(1));
	    
	} //end-method	

	/**
	 * Main builder. Initializes the methods' structures
	 * 
	 * @param script Configuration script
	 */
	public D_SKNN(String script){
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="D_SKNN";
		
		gamma = new double [nClasses];
		
		inner = new double [nClasses];
		
		outter = new double [nClasses];
		mr = new double [nClasses];
		ms = new double [nClasses];
		exists = new boolean [nClasses];


	    //Initialization of Reporting tool
	    ReportTool.setOutputFile(outFile[2]);
	    
	} //end-method	
	
	/**
	 * Generates the model of the algorithm
	 */
	public void generateModel (){
		
		//Start of model time
		Timer.resetTime();	
		
		computeGamma();

		//End of model time
		Timer.setModelTime();
	
		//Showing results
		System.out.println(name+" "+ relation + " Model " + Timer.getModelTime() + "s");
		
	}

	private void computeGamma(){
		
		double meanDist, dist;
		int count;
	
		for(int c=0;c<nClasses;c++){
			
			if(nInstances[c]<2){
				gamma[c]=1.0;
			}
			else{
				
				meanDist=0.0;
				count=0;
				
				for(int i=0;i<trainData.length-1;i++){
					if(trainOutput[i]==c){
						for(int j=i+1;j<trainData.length;j++){
							if(trainOutput[j]==c){
						
								dist=Util.euclideanDistance(trainData[i], trainData[j]);
								
								meanDist+=dist;
								count++;
							}
						}
					}
				}

				gamma[c]= 1.0 / Math.pow(meanDist,BETA);
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
		double alpha;
		int out;
		double KNorm;
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
		
		//compute inner m_s^q({Cq}) and outter m_s^q(C) arrays
		
		Arrays.fill(inner, 1.0);
		Arrays.fill(outter, 1.0);
		Arrays.fill(exists, false);
		
		for(int i=0;i<nearestN.length;i++){
			
			out=trainOutput[nearestN[i]];
			dist=minDist[i];
			exists[out]=true;
			
			alpha=1.0-alphaFunction(dist,out);
			
			inner[out]*=alpha;
			outter[out]*=alpha;
			
		}
		
		Arrays.fill(mr, 1.0);
		
		for(int c=0;c<nClasses;c++){
			
			if(exists[c]){
				
				inner[c]=1.0-inner[c];
			}
			else{
				inner[c]=0;
				outter[c]=1.0;
				
			}
			
			for(int r=0;r<nClasses;r++){
				if(r!=c){
					mr[r]*=outter[c];
				}
			}
			
		}
		
		KNorm=0;
		for(int c=0;c<nClasses;c++){
		
			ms[c]=inner[c]*mr[c];
			KNorm+=ms[c];
		}
		
		KNorm+=mr[0]*outter[0];
		
		for(int c=0;c<nClasses;c++){
			
			ms[c]/=KNorm;
		}
		
		result=getMaximum(ms);
		
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
		double alpha;
		int out;
		double KNorm;
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
		
		//compute inner m_s^q({Cq}) and outter m_s^q(C) arrays
		
		Arrays.fill(inner, 1.0);
		Arrays.fill(outter, 1.0);
		Arrays.fill(exists, false);
		
		for(int i=0;i<nearestN.length;i++){
			
			out=trainOutput[nearestN[i]];
			dist=minDist[i];
			exists[out]=true;
			
			alpha=1.0-alphaFunction(dist,out);
			
			inner[out]*=alpha;
			outter[out]*=alpha;
			
		}
		
		Arrays.fill(mr, 1.0);
		
		for(int c=0;c<nClasses;c++){
			
			if(exists[c]){
				
				inner[c]=1.0-inner[c];
			}
			else{
				inner[c]=0;
				outter[c]=1.0;
				
			}
			
			for(int r=0;r<nClasses;r++){
				if(r!=c){
					mr[r]*=outter[c];
				}
			}
			
		}
		
		KNorm=0;
		for(int c=0;c<nClasses;c++){
		
			ms[c]=inner[c]*mr[c];
			KNorm+=ms[c];
		}
		
		KNorm+=mr[0]*outter[0];
		
		for(int c=0;c<nClasses;c++){
			
			ms[c]/=KNorm;
		}
		
		result=getMaximum(ms);
		
		return result;
		
	} //end-method	
	
	private int getMaximum(double array[]){
		
		double max = Double.MIN_VALUE;
		int pos= -1;
		
		for(int i=0;i<array.length;i++){
			if(max<array[i]){
				max=array[i];
				pos=i;
			}
		}
		
		return pos;
	}
	
	private double alphaFunction(double distance, int output){
		
		double result;
		
		result=ALPHA*phiFunction(distance,output);
		
		return result;
	}
	
	private double phiFunction(double distance, int output){
		
		double result;
		
		result=Math.pow(Math.E, -gamma[output]*Math.pow(distance, BETA));
		
		return result;
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
