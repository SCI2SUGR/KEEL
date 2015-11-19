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



package keel.Algorithms.RST_Learning.EFS_RPS;

import java.util.Arrays;
import java.util.StringTokenizer;

import org.core.Files;
import org.core.Randomize;

import keel.Algorithms.RST_Learning.KNNClassifier;
import keel.Algorithms.RST_Learning.RSTAlgorithm;
import keel.Algorithms.RST_Learning.ReportTool;
import keel.Algorithms.RST_Learning.Timer;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;

/**
 * 
 * File: EFS_RPS.java
 * 
 * The EFS_RPS Algorithm.
 * EFS = Evolutionary Feature Selection (using a SSGA)
 * RPS = Rough Set based Prototype Selection
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2011 
 * @version 1.0 
 * @since JDK1.5
 * 
 */
public class EFS_RPS extends RSTAlgorithm{

	private int sizePop; //size of the population
	private double beta; // alpha value in fitness function
	private double mutProb; //probability of mutation
	private int maxEvaluations; //maximum evaluations allowed
	private int K;
	
	private int cycle;
	
	private double newAcc;
	private double ISWPerformance;
	
	private int implicator;
	private int tnorm;
	
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
	    
	    //Getting the beta parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    beta = Double.parseDouble(tokens.nextToken().substring(1));
	    
	    //Getting the mutProb parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    mutProb = Double.parseDouble(tokens.nextToken().substring(1));
	    
	    //Getting the cycle parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    cycle = Integer.parseInt(tokens.nextToken().substring(1));
	    
	    //Getting the type of implicator
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    type = tokens.nextToken().substring(1); 
	    
	    implicator=ISW.LUKASIEWICZ;
	    
	    if(type.equalsIgnoreCase("KLEENE_DIENES")){
	    	implicator=ISW.KLEENE_DIENES;
	    }
	    
	    //Getting the type of tnorm
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    type = tokens.nextToken().substring(1); 
	    
	    tnorm=ISW.LUKASIEWICZ;
	    
	    if(type.equalsIgnoreCase("MIN")){
	    	tnorm=ISW.MIN;
	    }
	    
	    if(type.equalsIgnoreCase("PRODUCT")){
	    	tnorm=ISW.PRODUCT;
	    }

	}

	/**
	 * Main builder. Initializes the methods' structures
	 * 
	 * @param script Configuration script
	 */
	public EFS_RPS(String script){
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="EFS_RPS";

		//Initialization of random generator
	    
	    Randomize.setSeed(seed);
	    
	    //initialization of the RST Utility
		
		boolean nominal []= new boolean [inputAtt];
		
		for(int i=0;i<inputAtt;i++){
			if(Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL){
				nominal[i]=true;
			}
			else{
				nominal[i]=false;
			}
		}
	    
	    //Initialization of the classifier
	    
	    KNNClassifier.setData(trainData);
	    KNNClassifier.setOutput(trainOutput);
	    KNNClassifier.setClasses(nClasses);
	    KNNClassifier.setK(K);
	    
	    //Initialization of the FSD method
	    ISW.setData(trainData, nominal);
	    ISW.setImplicator(implicator);
	    ISW.setTNorm(tnorm);
	    ISW.setOutput(trainOutput);
	    
	    ISW.setAllInstances();
	    ISW.setAllAttributes();
	    ISW.computeISW();
	    
	    KNNClassifier.setAllFeatures();	    
	    KNNClassifier.setInstances(ISW.getInstances());
	    
	    ISWPerformance=KNNClassifier.accuracy();
	    
		//Initialization of auxiliary structures
			
	    Chromosome.setSize(inputAtt);
	    Chromosome.setInitProb(0.5);
	    Chromosome.setBeta(beta);
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
		double cost;
		int cycleLength;
		double GAMMA = 0.75;
		
		int oldInstances [];
		
		oldInstances = new int [trainData.length];
		
		cycleLength=0;
		while (evaluations < maxEvaluations) {
			
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
	        
	        cycleLength+=2;
	        	        
	        /*Replace the two worst*/
	        Arrays.sort(population);
	        
	        population[sizePop-2] = new Chromosome (one.getAll(),one.getFitness());
	        population[sizePop-1] = new Chromosome (two.getAll(),two.getFitness());
	        
	        if((cycleLength%cycle==0)&&(evaluations<(GAMMA*maxEvaluations))){

				//update of the RST IS
				oldInstances=KNNClassifier.getIS();
		    
				ISW.setAttributes(population[0].getAll());
			    ISW.setAllInstances();
			    cost=ISW.computeISW();
			    
			    cost=Math.ceil(cost);
			    evaluations+=(int)cost;

			    KNNClassifier.setInstances(ISW.getInstances());	    
			    KNNClassifier.setFeatures(population[0].getAll());
			    
			    newAcc=KNNClassifier.accuracy();
			    evaluations++;
			    
			    System.out.println(evaluations+" "+ISWPerformance+" * "+newAcc+" "+population[0].getFitness()); 
			    if(newAcc>ISWPerformance){
			    	ISWPerformance=newAcc;
			    }
			    else{
			    	KNNClassifier.setInstances(oldInstances);
			    }
			    
			    cycleLength=0;
			}
	        
	    }
    
		//Get best chromosome
		Arrays.sort(population);
		KNNClassifier.setFeatures(population[0].getAll());
		
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
