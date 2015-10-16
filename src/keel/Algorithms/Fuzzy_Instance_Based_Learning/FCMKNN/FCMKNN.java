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
 * File: FCMKNN.java
 * 
 * The FCMKNN algorithm. 
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2011 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Fuzzy_Instance_Based_Learning.FCMKNN;

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

public class FCMKNN extends FuzzyIBLAlgorithm {
	
	private double centroids [][];
	private double membership [][];
	private double M;
	private int K;
	private double epsilon;
	
	private int maxIterations;
	private double delta;
	
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
	    
	    //Getting the M parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    M = Double.parseDouble(tokens.nextToken().substring(1));
	    
	    //Getting the Max Iterations parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    maxIterations = Integer.parseInt(tokens.nextToken().substring(1));
	    
	    //Getting the delta parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    delta = Double.parseDouble(tokens.nextToken().substring(1));


	} //end-method	

	/**
	 * Main builder. Initializes the methods' structures
	 * 
	 * @param script Configuration script
	 */
	public FCMKNN(String script){
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="Fuzzy C-Means K-NN";

		centroids = new double [nClasses][inputAtt];
		membership = new double [trainData.length][nClasses];
		
		referenceMembership = new double [referenceData.length][nClasses];
		testMembership = new double [testData.length][nClasses];
		
		//Initialization of random generator
	    Randomize.setSeed(seed);
	    
	    //Initialization of Reporting tool
	    ReportTool.setOutputFile(outFile[2]);

	} //end-method	
	
	/**
	 * Generates the model of the algorithm
	 */
	public void generateModel (){
		
		double newEpsilon;
		
		//Start of model time
		Timer.resetTime();
				
		double term = 0.7/(double)(nClasses-1);
		
		//Initialization of the membership matrix
		//0.7 is assigned to the labeled instance in the training
		//set. 0.3 is split between the rest of classes
		for(int i=0;i<trainData.length;i++){
			Arrays.fill(membership[i],term);
			membership[i][trainOutput[i]]=0.3;
		}
		
		
		epsilon=Double.MAX_VALUE;
		newEpsilon=Double.MAX_VALUE;
			
		int iterations=0;
		do{
			
			epsilon=newEpsilon;
			computeCentroids();
			
			newEpsilon=computeMembership();
			
			System.out.println("Iteration "+iterations+" Error: "+newEpsilon);
			iterations++;
		}while((Math.abs(epsilon-newEpsilon)>delta)&&(iterations<maxIterations));
		
		
		//End of model time
		Timer.setModelTime();
	
		//Showing results
		System.out.println(name+" "+ relation + " Model " + Timer.getModelTime() + "s");
		
	} //end-method	
	
	private double computeMembership(){
		
		double distances [] = new double [centroids.length];
		double exp = 2.0 / (M-1.0);
		double difference;
		double sum=0.0;
		
		difference=0.0;
		
		for(int i=0;i<trainData.length;i++){
		
			//compute distances to centroids
			for(int c=0;c<centroids.length;c++){
				
				distances[c]=Util.euclideanDistance(trainData[i], centroids[c]);
			}
			
			
			//compute memberships
			for(int c=0;c<centroids.length;c++){	
				sum=0.0;
				for(int k=0;k<centroids.length;k++){
					sum+=Math.pow(distances[c]/distances[k],exp);
				}
				
				membership[i][c]=1.0/sum;
			}
			
		}
		
		//test difference
		
		difference=0.0;
		
		for(int c=0;c<centroids.length;c++){
			for(int i=0;i<trainData.length;i++){
				difference+=Math.pow(membership[i][c],M)*Util.euclideanDistance(trainData[i], centroids[c]);
			}
			
		}

		return difference;
	}
	
	
	
	private void computeCentroids(){
		
		double sumW[];
		double term;
		
		for(int i=0;i<nClasses;i++){
			Arrays.fill(centroids[i],0.0);
		}
		
		sumW= new double [nClasses];
		Arrays.fill(sumW,0.0);
		
		for(int i=0;i<trainData.length;i++){
			for(int j=0;j<nClasses;j++){
				term = Math.pow(membership[i][j],M);    
				for(int k=0;k<inputAtt;k++){
					centroids[j][k]+=(term*trainData[i][k]);
				}
				sumW[j]+=term;
			}	
		}
		
		for(int i=0;i<nClasses;i++){
			for(int k=0;k<inputAtt;k++){
				centroids[i][k]/=sumW[i];
			}
		}
		
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
		
		//compute membership
		for(int i = 0;i<K;i++){
			for(int j=0;j<nClasses;j++){
				referenceMembership [index][j]+= membership[nearestN[i]][j];
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
		
		//compute membership
		for(int i = 0;i<K;i++){
			for(int j=0;j<nClasses;j++){
				testMembership [index][j]+= membership[nearestN[i]][j];;
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
		
		/*
		DecimalFormat nf4;
		
		nf4 = (DecimalFormat) DecimalFormat.getInstance();
		nf4.setMaximumFractionDigits(4);
		nf4.setMinimumFractionDigits(0);

		DecimalFormatSymbols dfs = nf4.getDecimalFormatSymbols();
		
		dfs.setDecimalSeparator('.');
		nf4.setDecimalFormatSymbols(dfs); 
		
		String text="\n\n====================\n";
		
		text+="Prototypes:\n";
		
		for(int i=0;i<centroids.length;i++){
			if(nInstances[i]>0){
				text+=(i+1)+": ";
				for(int j=0;j<inputAtt;j++){
					text+= " "+nf4.format(centroids[i][j]);
				}
				text+="\n";
			}
		}
		
		text+="\n\nReference set membership:\n";
		
		for(int i=0;i<referenceData.length;i++){
			text+=(i+1)+": ";
			for(int j=0;j<nClasses;j++){
				text+="Class "+(j+1)+": "+nf4.format(membership[i][j])+"\t";
			}
			text+="\n";
		}
		
		/*text+="\n\nTest set membership:\n";
		
		for(int i=0;i<testData.length;i++){
			text+=(i+1)+": ";
			for(int j=0;j<nClasses;j++){
				text+="Class "+(j+1)+": "+nf4.format(testMembership[i][j])+"\t";
			}
			text+="\n";
		}
		
		ReportTool.addToReport(text);*/
		
		
	} //end-method	
    
} //end-class 
