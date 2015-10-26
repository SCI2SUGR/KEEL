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


package keel.Algorithms.Fuzzy_Instance_Based_Learning.IFSKNN;

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
 * File: IFSKNN.java
 * 
 * The IFSKNN algorithm. 
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2011 
 * @version 1.0 
 * @since JDK1.5
 * 
 */
public class IFSKNN extends FuzzyIBLAlgorithm {

	private int K;
	private double meanInstances [][];
	
	private double membership [];
	private double nonmembership [];

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
	public IFSKNN(String script){
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="IFSKNN";

	    //Initialization of Reporting tool
	    ReportTool.setOutputFile(outFile[2]);
	    
	} //end-method	

	/**
	 * Generates the model of the algorithm
	 */
	public void generateModel (){
		
		//Start of model time
		Timer.resetTime();	
		
		computeMeanInstances();
		
		computeMembership();
	

		//End of model time
		Timer.setModelTime();
	
		//Showing results
		System.out.println(name+" "+ relation + " Model " + Timer.getModelTime() + "s");
		
	}

	private void computeMembership(){
		
		double dist;
		double minDist;
		
		membership = new double [trainData.length]; 
		nonmembership = new double [trainData.length]; 
		
		for(int i=0; i<trainData.length;i++){
			
			dist=Util.euclideanDistance(trainData[i], meanInstances[trainOutput[i]]);
			membership[i]= Math.pow(Math.E, -1.0*dist);
			
			minDist=Double.MAX_VALUE;
			for(int c=0; c<nClasses; c++){
				
				if(c!=trainOutput[i]){
					dist=Util.euclideanDistance(trainData[i], meanInstances[c]);
					if(minDist>dist){
						minDist=dist;
					}
				}
				
			}
			
			nonmembership[i]= Math.pow(Math.E, -1.0*minDist);
			nonmembership[i]=Math.min(1.0-membership[i], nonmembership[i]);
			
		}

	}
	
	private void computeMeanInstances(){
		
		meanInstances = new double [nClasses][inputAtt];
		
		for(int i=0;i<nClasses;i++){
			Arrays.fill(meanInstances[i],0.0);
		}
		
		for(int i=0;i<trainData.length;i++){
			for(int j=0;j<trainData[0].length;j++){
				meanInstances[trainOutput[i]][j]+=trainData[i][j];
			}
		}
		
		for(int i=0;i<nClasses;i++){
			for(int j=0;j<trainData[0].length;j++){
				if(nInstances[i]>0){
					meanInstances[i][j]/=(double)nInstances[i];
				}
				else{
					//very far
					meanInstances[i][j]=-100000;
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
	
		int maxVotes;
		int result = -1;
		int selectedClasses[];
		int selectedClasses2[];
		int prediction, prediction2;
		int predictionValue;
		
		double minDist[];
		int nearestN[];
		double dist;
		boolean stop;
		
		double minDist2[];
		int nearestN2[];

		nearestN = new int[K];
		minDist = new double[K];
	
	    for (int i=0; i<K; i++) {
			nearestN[i] = 0;
			minDist[i] = Double.MAX_VALUE;
		}
		
	    //KNN Method starts here
	    //membership
		for (int i=0; i<trainData.length; i++) {
		
			if(i!=index){ //leave-one-out
				
			    dist = Util.euclideanDistance(trainData[i],example);
	
			    dist/= membership[i];
			    
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
		
		//search for agreement on maximum
		
		selectedClasses= new int[nClasses];
	
		Arrays.fill(selectedClasses,0);	
		
		for (int i=0; i<K; i++) {
			selectedClasses[trainOutput[nearestN[i]]]+=1;
		}
		
		prediction=0;
		predictionValue=selectedClasses[0];
		
		for (int i=1; i<nClasses; i++) {
		    if (predictionValue < selectedClasses[i]) {
		        predictionValue = selectedClasses[i];
		        prediction = i;
		    }
		}
		
		//*************************************************
		
		
		nearestN2 = new int[K];
		minDist2 = new double[K];
	
	    for (int i=0; i<K; i++) {
			nearestN2[i] = 0;
			minDist2[i] = Double.MAX_VALUE;
		}
		
	    //KNN Method starts here
	    //non-membership
		for (int i=0; i<trainData.length; i++) {
		
			if(i!=index){ //leave-one-out
				
			    dist = Util.euclideanDistance(trainData[i],example);
	
			    dist*= nonmembership[i];
			    
				//see if it's nearer than our previous selected neighbors
				stop=false;
					
				for(int j=0;j<K && !stop;j++){
					
					if (dist < minDist2[j]) {
						    
						for (int l = K - 1; l >= j+1; l--) {
							minDist2[l] = minDist2[l - 1];
							nearestN2[l] = nearestN2[l - 1];
						}	
							
						minDist2[j] = dist;
						nearestN2[j] = i;
						stop=true;
					}
				}
			}
		}
		
		//search for agreement on minimum
		
		selectedClasses2= new int[nClasses];
		Arrays.fill(selectedClasses2,0);	
		
		for (int i=0; i<K; i++) {
			selectedClasses2[trainOutput[nearestN[i]]]+=1;
		}
		
		prediction2=0;
		predictionValue=selectedClasses2[0];
		
		for (int i=1; i<nClasses; i++) {
		    if (predictionValue < selectedClasses2[i]) {
		        predictionValue = selectedClasses2[i];
		        prediction2 = i;
		    }
		}

		if(prediction==prediction2){
			result=prediction;
		}
		else{
			//there's no agreement. Output is computed as the class with highest K
			maxVotes=-1;
			
			for (int i=0; i<nClasses; i++) {
				if(selectedClasses[i]+selectedClasses2[i]==maxVotes){
					if(selectedClasses[i]>selectedClasses[prediction]){
						prediction=i;
					}
				}
				
				if(selectedClasses[i]+selectedClasses2[i]>maxVotes){
					maxVotes=(selectedClasses[i]+selectedClasses2[i]);
					prediction=i;
				}

			}
			result=prediction;
		}
		
		
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
	
		int maxVotes;
		int result = -1;
		int selectedClasses[];
		int selectedClasses2[];
		int prediction, prediction2;
		int predictionValue;
		
		double minDist[];
		int nearestN[];
		double dist;
		boolean stop;
		
		double minDist2[];
		int nearestN2[];

		nearestN = new int[K];
		minDist = new double[K];
	
	    for (int i=0; i<K; i++) {
			nearestN[i] = 0;
			minDist[i] = Double.MAX_VALUE;
		}
		
	    //KNN Method starts here
	    //membership
		for (int i=0; i<trainData.length; i++) {
				
			dist = Util.euclideanDistance(trainData[i],example);
	
			dist/= membership[i];
			    
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
		
		//search for agreement on maximum
		
		selectedClasses= new int[nClasses];
	
		Arrays.fill(selectedClasses,0);	
		
		for (int i=0; i<K; i++) {
			selectedClasses[trainOutput[nearestN[i]]]+=1;
		}
		
		prediction=0;
		predictionValue=selectedClasses[0];
		
		for (int i=1; i<nClasses; i++) {
		    if (predictionValue < selectedClasses[i]) {
		        predictionValue = selectedClasses[i];
		        prediction = i;
		    }
		}
		
		//*************************************************
		
		
		nearestN2 = new int[K];
		minDist2 = new double[K];
	
	    for (int i=0; i<K; i++) {
			nearestN2[i] = 0;
			minDist2[i] = Double.MAX_VALUE;
		}
		
	    //KNN Method starts here
	    //non-membership
		for (int i=0; i<trainData.length; i++) {
		

				
			dist = Util.euclideanDistance(trainData[i],example);
	
			dist*= nonmembership[i];
			    
			//see if it's nearer than our previous selected neighbors
			stop=false;
					
			for(int j=0;j<K && !stop;j++){
					
				if (dist < minDist2[j]) {
						    
					for (int l = K - 1; l >= j+1; l--) {
						minDist2[l] = minDist2[l - 1];
						nearestN2[l] = nearestN2[l - 1];
					}	
							
					minDist2[j] = dist;
					nearestN2[j] = i;
					stop=true;
				}
			}
			
		}
		
		//search for agreement on minimum
		
		selectedClasses2= new int[nClasses];
		Arrays.fill(selectedClasses2,0);	
		
		for (int i=0; i<K; i++) {
			selectedClasses2[trainOutput[nearestN[i]]]+=1;
		}
		
		prediction2=0;
		predictionValue=selectedClasses2[0];
		
		for (int i=1; i<nClasses; i++) {
		    if (predictionValue < selectedClasses2[i]) {
		        predictionValue = selectedClasses2[i];
		        prediction2 = i;
		    }
		}

		if(prediction==prediction2){
			result=prediction;
		}
		else{
			//there's no agreement. Output is computed as the class with highest K
			maxVotes=-1;
			
			for (int i=0; i<nClasses; i++) {
				if(selectedClasses[i]+selectedClasses2[i]==maxVotes){
					if(selectedClasses[i]>selectedClasses[prediction]){
						prediction=i;
					}
				}
				
				if(selectedClasses[i]+selectedClasses2[i]>maxVotes){
					maxVotes=(selectedClasses[i]+selectedClasses2[i]);
					prediction=i;
				}

			}
			result=prediction;
		}
		
		
		return result;
		
	} //end-method	

	
	/**
	 * Reports the results obtained
	 */
	public void printReport(){
		
		writeOutput(outFile[0], trainOutput, trainPrediction);
		writeOutput(outFile[1], testOutput, testPrediction);
		
		ReportTool.setResults(trainOutput,trainPrediction,testOutput,testPrediction,nClasses);
		
		ReportTool.printReport();
		/*
		DecimalFormat nf4;
		
		nf4 = (DecimalFormat) DecimalFormat.getInstance();
		nf4.setMaximumFractionDigits(4);
		nf4.setMinimumFractionDigits(0);

		DecimalFormatSymbols dfs = nf4.getDecimalFormatSymbols();
		
		dfs.setDecimalSeparator('.');
		nf4.setDecimalFormatSymbols(dfs); 
		
		String text="\n\n====================\n";
		switch(initialization){
		
			case CLASS_MEAN: text+="\nUsing class mean initialization.\n";
				break;
				
			case KNN: text+="\nUsing KNN initialization ( "+k+" neighbors ).\n\n";
				break;
				
			case CRISP: text+="\nUsing crisp initialization.\n";
				default:
					
					break;
		}
		text+="Training set membership:\n";
		
		for(int i=0;i<referenceData.length;i++){
			text+=(i+1)+": ";
			for(int j=0;j<nClasses;j++){
				text+="Class "+(j+1)+": "+nf4.format(membership[i][j])+"\t";
			}
			text+="\n";
		}
		
		text+="\n\nReference set membership:\n";
		
		for(int i=0;i<referenceData.length;i++){
			text+=(i+1)+": ";
			for(int j=0;j<nClasses;j++){
				text+="Class "+(j+1)+": "+nf4.format(referenceMembership[i][j])+"\t";
			}
			text+="\n";
		}
		
		text+="\n\nTest set membership:\n";
		
		for(int i=0;i<testData.length;i++){
			text+=(i+1)+": ";
			for(int j=0;j<nClasses;j++){
				text+="Class "+(j+1)+": "+nf4.format(testMembership[i][j])+"\t";
			}
			text+="\n";
		}
		
		ReportTool.addToReport(text);
		*/
	} //end-method	
    
} //end-class 
