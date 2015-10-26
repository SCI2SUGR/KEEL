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



package keel.Algorithms.Fuzzy_Instance_Based_Learning.GAFuzzyKNN;

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

/**
 * 
 * File: GAFuzzyKNN.java
 * 
 * The GAFuzzyKNN algorithm. 
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2011 
 * @version 1.0 
 * @since JDK1.5
 * 
 */
public class GAFuzzyKNN extends FuzzyIBLAlgorithm {

	private static final double MAX_NORM = 100000000;
	private int K; //K value for Fuzzy K-NN
	private double M; //M value for Fuzzy K-NN norm
	private int kInit; //k value for K-NN in membership assignment
	
	private double membership [][];
	
	private double referenceMembership [][];
	private double testMembership [][];
	
	private int population [][];
	private int elite [];
	private double eliteFitness; 
	private double fitness [];
	
	private int popSize;
	private int maxGenerations;
	private int generations;
	private double crossProb;
	private double mutProb;
	private double sumFitness;
	
	private double bestFitness;
	private int indexBest;

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
	    
		//Getting the popSize parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    popSize = Integer.parseInt(tokens.nextToken().substring(1));
	    
	    //Getting the maxGenerations parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    maxGenerations = Integer.parseInt(tokens.nextToken().substring(1));
	    
	    //Getting the crossProb parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    crossProb = Double.parseDouble(tokens.nextToken().substring(1));
	    
	    //Getting the mutProb parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    mutProb = Double.parseDouble(tokens.nextToken().substring(1));
	    
	} //end-method	

	/**
	 * Main builder. Initializes the methods' structures
	 * 
	 * @param script Configuration script
	 */
	public GAFuzzyKNN(String script){
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="FuzzyKNN";
		
		membership = new double [trainData.length][nClasses];
		
		for(int i=0;i<trainData.length;i++){
			
			Arrays.fill(membership[i], -1.0);
		}
		
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
		
		//Start of model time
		Timer.resetTime();	

		evolve();

		//End of model time
		Timer.setModelTime();
	
		//Showing results
		System.out.println(name+" "+ relation + " Model " + Timer.getModelTime() + "s");
		
	}
	
	/**
	 * Obtains k and m parameters using a binary GA
	 */
	private void evolve(){
		
		int newPopulation [][];
		
		//16 bits for M, 5 bits for k
		population = new int [popSize][21];
		elite = new int [21];
		fitness = new double [popSize];
		
		//Step 1: Initialization
		
		sumFitness=0.0;
		bestFitness=0.0;
		indexBest=0;
		for(int i=0;i<popSize;i++){
			for(int j=0;j<population[0].length;j++){
				if(Randomize.Rand()<0.5){
					population[i][j]=0;
				}
				else{
					population[i][j]=1;
				}
			}
			
			evaluate(i);
			sumFitness+=fitness[i];
			
			if(fitness[i]>bestFitness){
				bestFitness=fitness[i];
				indexBest=i;
			}
		}
		
		for(int j=0;j<population[0].length;j++){
			elite[j]=population[indexBest][j];
		}
		eliteFitness=bestFitness;
		generations=0;
		
		int first, second, cross;
		double value;
		while(generations<maxGenerations){
			
			newPopulation= new int [popSize][21];
			for(int i=0;i<popSize;i+=2){
				
				//Step 2: First parent
				value=Randomize.Rand()*sumFitness;
				
				for(first=0;(first<popSize)&&(value<fitness[first]);first++);
				first--;
				
				//Step 2: Second parent
				do{
					value=Randomize.Rand()*sumFitness;
				
					for(second=0;(second<popSize)&&(value<fitness[second]);second++);
					second--;
				}while(first==second);
				
				//Step 3: Crossover
				
				cross=Randomize.RandintClosed(1,population[0].length-2);
				
				for(int j=0;j<cross;j++){
					newPopulation[i][j]=population[i][j];
					newPopulation[i+1][j]=population[i+1][j];
				}
				
				if(Randomize.Rand()<crossProb){
					for(int j=cross;j<population[0].length;j++){
						newPopulation[i][j]=population[i+1][j];
						newPopulation[i+1][j]=population[i][j];
					}
				}
				else{
					for(int j=cross;j<population[0].length;j++){
						newPopulation[i][j]=population[i][j];
						newPopulation[i+1][j]=population[i+1][j];
					}	
				}
				
				//Step 4: Mutation
				for(int j=0;j<population[0].length;j++){
					
					if(Randomize.Rand()<mutProb){
						newPopulation[i][j]=(newPopulation[i][j]+1)%2;
					}
					
					if(Randomize.Rand()<mutProb){
						newPopulation[i+1][j]=(newPopulation[i+1][j]+1)%2;
					}
				}
			}
			
			//Step 5: Elitist Strategy
			int replace=Randomize.RandintClosed(0, popSize-1);
			
			for(int j=0;j<population[0].length;j++){
				newPopulation[replace][j]=elite[j];
			}
			
			//Evaluation of new Population
			for(int i=0;i<popSize;i++){
				for(int j=0;j<population[0].length;j++){
					population[i][j]=newPopulation[i][j];
				}
			}
			
			sumFitness=0.0;
			bestFitness=0.0;
			indexBest=0;
			for(int i=0;i<popSize;i++){
				
				evaluate(i);
				sumFitness+=fitness[i];
				
				if(fitness[i]>bestFitness){
					
					bestFitness=fitness[i];
					indexBest=i;
				}
				
			}
			
			if(bestFitness>eliteFitness){
				for(int j=0;j<population[0].length;j++){
					elite[j]=population[indexBest][j];
				}
				eliteFitness=bestFitness;
			}

			generations+=1;
		}
		
		//get M and K values
		
		//get M value
		M=1;
		
		double increment=2.0;
		for(int i=0;i<16;i++){
			if(elite[i]==1){
				M+=increment;
			}
			increment/=2.0;
		}
		
		//get K value
		kInit=1;
		int inc=1;
		for(int i=16;i<21;i++){
			if(elite[i]==1){
				kInit+=inc;
			}
			inc*=2;
		}

		assignMembership();
	}
	
	private void evaluate(int index){
		
		//get M value
		M=1;
		
		double increment=2.0;
		for(int i=0;i<16;i++){
			if(population[index][i]==1){
				M+=increment;
			}
			increment/=2.0;
		}
		
		//get K value
		kInit=1;
		int inc=1;
		for(int i=16;i<21;i++){
			if(population[index][i]==1){
				kInit+=inc;
			}
			inc*=2;
		}
		
		assignMembership();
		
		double acc;
		int hits=0;
		for(int i=0;i<trainData.length;i++){
			
			computeTrainMembership(i,referenceData[i]);
			if(computeClass(referenceMembership[i])==trainOutput[i]){
				hits++;
			}
			
		}
		
		acc=(double)hits/(double)trainData.length;
		
		fitness[index]=acc;
		
		referenceMembership = new double [referenceData.length][nClasses];
	}
	
	
	/**
	 * Assign class membership to each instance of the training set
	 */
	private void assignMembership(){

		for(int instance=0;instance<trainData.length;instance++){
					
			double minDist[];
			int nearestN[];
			int selectedClasses[];
			double dist;
			boolean stop;

			nearestN = new int[kInit];
			minDist = new double[kInit];
				
			for (int i=0; i<kInit; i++) {
				nearestN[i] = 0;
				minDist[i] = Double.MAX_VALUE;
			}
					
			//KNN Method starts here
				    
			for (int i=0; i<trainData.length; i++) {
					
				dist = Util.euclideanDistance(trainData[i],trainData[instance]);

				if (i != instance){ //leave-one-out
						
					//see if it's nearer than our previous selected neighbors
					stop=false;
							
					for(int j=0;j<kInit && !stop;j++){
							
						if (dist < minDist[j]) {
								    
							for (int l = kInit - 1; l >= j+1; l--) {
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
					
			for (int i=0; i<kInit; i++) {
				selectedClasses[trainOutput[nearestN[i]]]++;
			}
					
					
			Arrays.fill(membership[instance], 0.0);
					
			double term;
			for (int i=0; i<nClasses; i++) {
				term = ((double)selectedClasses[i]/(double)kInit);
				if(trainOutput[instance]==i){
					membership[instance][i]=0.51+0.49*term;
				}else{
					membership[instance][i]=0.49*term;
				}
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
		double norm[];
		double sum;
		
		norm = new double [K];
		sum = 0.0;
		
		for(int i = 0;i<K;i++){
			
			if(minDist[i]==0.0){
				norm[i]=MAX_NORM;
			}
			
			norm[i] = 1.0/ Math.pow(minDist[i],(2.0/(M-1.0))); 
			
			norm[i]=Math.min(norm[i],MAX_NORM);
			
			sum+=norm[i];
		}
		
		for(int i = 0;i<K;i++){
			for(int c=0;c<nClasses;c++){
				referenceMembership [index][c]+= membership[nearestN[i]][c]*(norm[i]/sum);
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
		double norm[];
		double sum;
		
		norm = new double [K];
		sum = 0.0;
		
		for(int i = 0;i<K;i++){
			
			if(minDist[i]==0.0){
				norm[i]=MAX_NORM;
			}
			
			norm[i] = 1.0/ Math.pow(minDist[i],(2.0/(M-1.0))); 
			
			norm[i]=Math.min(norm[i],MAX_NORM);
			
			sum+=norm[i];
		}
		
		for(int i = 0;i<K;i++){
			for(int c=0;c<nClasses;c++){
				testMembership [index][c]+= membership[nearestN[i]][c]*(norm[i]/sum);
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
		
		String text="\n";
		
		text+= "K value = "+kInit;
		text+= "M value = "+M;
		
		ReportTool.addToReport(text);
		
	} //end-method	
    
} //end-class 
