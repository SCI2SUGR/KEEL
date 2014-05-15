/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    S. García (sglopez@ujaen.es)
    F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
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

/**
 * 
 * File: EIS_RFS.java
 * 
 * The EIS_RFS Algorithm.
 * EIS = Evolutionary Instance Selection (using a SSGA)
 * RFS = Rough Set based Feature Selection
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/04/2010 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.RST_Learning.EIS_RFS;

import java.util.Arrays;
import java.util.StringTokenizer;

import org.core.Files;
import org.core.Randomize;

import keel.Algorithms.RST_Learning.KNNClassifier;
import keel.Algorithms.RST_Learning.RSTAlgorithm;
import keel.Algorithms.RST_Learning.RSTData;
import keel.Algorithms.RST_Learning.ReportTool;
import keel.Algorithms.RST_Learning.Timer;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;


public class EIS_RFS extends RSTAlgorithm{

	private int K; //K value for K-NN
	private int sizePop; //size of the population
	private double initProb; //probability of 1 in initialization
	private double alpha; // alpha value in fitness function
	private double mutProb; //probability of mutation
	private int maxEvaluations; //maximum evaluations allowed
	private double maxGamma; // alpha value in gamma measure
	private double RSTperformance;
	private double newAcc;
	
	private int evaluations;	
	private Chromosome population [];
	
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
	    
	    //Getting the sizePop parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    sizePop = Integer.parseInt(tokens.nextToken().substring(1));
	    
	    //Getting the maxEvaluations parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    maxEvaluations = Integer.parseInt(tokens.nextToken().substring(1));
	    
	    //Getting the initProb parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    initProb = Double.parseDouble(tokens.nextToken().substring(1));
	    
	    //Getting the alpha parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    alpha = Double.parseDouble(tokens.nextToken().substring(1));
	    
	    //Getting the mutProb parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    mutProb = Double.parseDouble(tokens.nextToken().substring(1));
	    
	    //Getting the maxGamma parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    maxGamma = Double.parseDouble(tokens.nextToken().substring(1));
	}

	/**
	 * Main builder. Initializes the methods' structures
	 * 
	 * @param script Configuration script
	 */
	public EIS_RFS(String script){
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="EIS_RFS";

		//Initialization of random generator
	    
	    Randomize.setSeed(seed);
	    
	    //initialization of the RST Utility
	    RSTData.setData(trainData);
		RSTData.setOutput(trainOutput);
		RSTData.setClasses(nClasses);
		
		RSTData.setNumValues();
		
		boolean nominal []= new boolean [inputAtt];
		
		for(int i=0;i<inputAtt;i++){
			if(Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL){
				nominal[i]=true;
			}
			else{
				nominal[i]=false;
			}
		}
		
		for(int i=0;i<inputAtt;i++){
			if(nominal[i]){
				RSTData.setNumValue(Attributes.getInputAttribute(i).getNumNominalValues(),i);
			}
		}
		
		RSTData.setNominal(nominal);
		
		RSTData.setAlpha(maxGamma);
		RSTData.setNormalization(RSTData.computeGamma());
	    
	    //Initialization of the classifier
	    
	    KNNClassifier.setData(trainData);
	    KNNClassifier.setOutput(trainOutput);
	    KNNClassifier.setClasses(nClasses);
	    KNNClassifier.setK(K);
	    
	    //Initialization of the RST FS
	    
	    KNNClassifier.setAllInstances();
	    RSTperformance=KNNClassifier.accuracy();
	    

	    RSTData.setAllInstances();
	    RSTData.computeBestFeatures();
	    KNNClassifier.setFeatures(RSTData.getAttributes());
	    KNNClassifier.setAllInstances();
	    
	    newAcc=KNNClassifier.accuracy();
	    
	    if(newAcc>RSTperformance){
	    	RSTperformance=newAcc; 	
	    }
	    else{
	    	KNNClassifier.setAllFeatures();
	    }
	    
		//Initialization of auxiliary structures
			
	    Chromosome.setSize(trainSize);
	    Chromosome.setInitProb(initProb);
	    Chromosome.setAlpha(alpha);
	    Chromosome.setMutationProbability(mutProb);
	    
	    population= new Chromosome [sizePop];
	    
	    for(int i=0;i<sizePop;i++){
	    	population[i]=new Chromosome();
	    	population[i].evaluate();
	    }

	    Arrays.sort(population);

	    //Initialization of Reporting tool
	    ReportTool.setOutputFile(outFile[2]);

	    evaluations=0;
	}

	/**
	 * Performs the evolution process
	 */
	public void evolution (){
		
		//Start of model time
		Timer.resetTime();	
		
		Chromosome one;
		Chromosome two;
		int candidate1, candidate2;
		int selected1, selected2;
		
		int oldFeatures [];
		
		boolean enterRST=true;
		
		oldFeatures= new int [inputAtt];
		
		while (evaluations < maxEvaluations) {
			
			if((evaluations%100==0)&&(enterRST)){
				System.out.println(evaluations+" "+RSTperformance+" "+population[0].getFitness());
				
				//update of the RST FS
				oldFeatures=KNNClassifier.getFS();
				RSTData.setInstances(population[0].getAll());
			    RSTData.computeBestFeatures();
			    
			    KNNClassifier.setFeatures(RSTData.getAttributes());
			    KNNClassifier.setInstances(population[0].getAll());
			    
			    newAcc=KNNClassifier.accuracy();
			    System.out.println(evaluations+"*********"+newAcc+" "+RSTData.getnFeatures());
			    if(newAcc>RSTperformance){
			    	RSTperformance=newAcc;
			    }
			    else{
			    	KNNClassifier.setFeatures(oldFeatures);
			    }
			    
			    if((evaluations> 0.75*maxEvaluations)&&(enterRST)){
					enterRST=false;
					
					//update of the RST FS
					oldFeatures=KNNClassifier.getFS();

				    
				    KNNClassifier.setAllFeatures();
				    KNNClassifier.setInstances(population[0].getAll());
				    
				    newAcc=KNNClassifier.accuracy();
				    System.out.println("Final: RST: "+RSTperformance+" Basic:"+ newAcc);

				    if(newAcc>RSTperformance){
				    	System.out.println("Disabling feature selection");
				    }
				    else{
				    	KNNClassifier.setFeatures(oldFeatures);
				    }
				}
			}

			
	        //Binary tournament selection: First candidate
			
			candidate1 = Randomize.Randint(0,sizePop-1);
	        do {
	        	candidate2 = Randomize.Randint(0,sizePop-1);
	        } while (candidate2 == candidate1);
	        
	        if (population[candidate1].getFitness() > population[candidate2].getFitness()){
	        	selected1=candidate1;
	        }
	        else{
	        	selected1=candidate2;
	        }

	        //Binary tournament selection: First candidate
			
			candidate1 = Randomize.Randint(0,sizePop-1);
	        do {
	        	candidate2 = Randomize.Randint(0,sizePop-1);
	        } while (candidate2 == candidate1);
	        
	        if (population[candidate1].getFitness() > population[candidate2].getFitness()){
	        	selected2=candidate1;
	        }
	        else{
	        	selected2=candidate2;
	        }
	        
	        //Cross operator
	        
	        one = new Chromosome (population[selected1].getAll());
	        two = new Chromosome (one.crossPMX(population[selected2].getAll()));

	        //Mutation operator
	        one.mutation();
	        two.mutation();
	        
	        one.evaluate();
	        evaluations++;

	        two.evaluate();
	        evaluations++;
	        	        
	        /*Replace the two worst*/
	        Arrays.sort(population);
	        
	        population[sizePop-2] = new Chromosome (one.getAll(),one.getFitness());
	        population[sizePop-1] = new Chromosome (two.getAll(),two.getFitness());
	        
	    }
    
		//Get bets chromosome
		Arrays.sort(population);
		KNNClassifier.setInstances(population[0].getAll());
		
		//End of model time
		Timer.setModelTime();
	
		//Showing results
		System.out.println(name+" "+ relation + " Model " + Timer.getModelTime() + "s");
		
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
		
	} 
	
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
	} 
	
	/**
	 * Classifies the training set
	 */
	public void classifyTrainSet(){
				
		for(int i=0;i<trainData.length;i++){
			
			trainPrediction[i]=KNNClassifier.classifyTrainingInstance(i);
			
		}

	}
	
	/**
	 * Classifies the test set
	 */
	public void classifyTestSet(){

		for(int i=0;i<testData.length;i++){
			
			testPrediction[i]=KNNClassifier.classifyNewInstance(testData[i]);
			
		}

	}
	
	/**
	 * Reports the results obtained
	 */
	public void printReport(){
		
		writeOutput(outFile[0], trainOutput, trainPrediction);
		writeOutput(outFile[1], testOutput, testPrediction);
		
		ReportTool.setResults(trainOutput,trainPrediction,testOutput,testPrediction,nClasses);
		
		ReportTool.printReport();
	}
    
} //end-class 
