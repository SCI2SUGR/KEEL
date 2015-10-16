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
 * File: CIW_NN.java
 * 
 * The CIW-NN Algorithm.
 * It makes use of three different preprocessing techniques in order to 
 * improve the KNN classification. Instance Selection, feature weighting
 * and instance weighting are considered whitin the evolutionary framework
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/1/2010 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Coevolution.CIW_NN;

import java.util.Arrays;
import java.util.StringTokenizer;

import keel.Algorithms.Coevolution.CoevolutionAlgorithm;
import keel.Dataset.Attribute;

import org.core.Files;
import org.core.Randomize;

public class CIW_NN extends CoevolutionAlgorithm{

	private int sizePop;
	private ChromosomeIS ISPopulation[];
	private ChromosomeFW FWPopulation[];
	private ChromosomeIW IWPopulation[];
	private int MAX_EVALUATIONS;
	private int evaluations;
	private int K;
	double fitA[];
	double fitB[];
	double fitC[];
	int evs[];
	int counter;
	
	private ChromosomeIS bestIS;
	private ChromosomeFW bestFW;
	private ChromosomeIW bestIW;
	private double bestFitnessIS;
	private double bestFitnessFW;
	private double bestFitnessIW;
	
	private double alpha;
	private double prob0to1;
	private double prob1;
	private double mutProb;
	private int epochFW;
	private int epochIW;

	private int trainRealClass[][];
	private int trainPrediction[][];
	private int testRealClass[][];
	private int testPrediction[][];	
	private int testUnclassified;	
	private int trainUnclassified;	
	private int testConfMatrix[][];
	private int trainConfMatrix[][];
	
	/** 
	 * The main method of the class
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */
	public CIW_NN (String script) {
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="CIW_NN";
		
		//Initialization of random generator
	    
	    Randomize.setSeed(seed);

		evaluations=0;

		//create populations
		ChromosomeIS.setSize(trainData.length);
		ChromosomeFW.setSize(inputAtt);
		ChromosomeIW.setSize(nClasses);
		ChromosomeIS.setProb(prob0to1);
		ChromosomeIS.setprob1(prob1);
		
		ISPopulation= new ChromosomeIS [sizePop];
		FWPopulation= new ChromosomeFW [sizePop];
		IWPopulation= new ChromosomeIW [sizePop];
		
		//initialize populations
		for(int i=0;i<sizePop;i++){
			ISPopulation[i]= new ChromosomeIS();
			FWPopulation[i]= new ChromosomeFW();
			IWPopulation[i]= new ChromosomeIW();
		}
		
		//prepare weighted KNN classifier
		
		WKNN.setData(trainData);
		WKNN.setOutput(trainOutput);
		WKNN.setK(K);
		WKNN.setNClasses(nClasses);
		
		//prepare algorithms
		
		BinaryCHC.setThreshold((trainData.length/4));
		BinaryCHC.setAlpha(alpha);
		BinaryCHC.setprob0to1R(prob0to1);

		RealCHC.setMAX_EVALS(epochFW);
		RealCHC.setMutation(mutProb);
		
		RealIWCHC.setMAX_EVALS(epochIW);
		RealIWCHC.setMutation(mutProb);

	    
	    //Initialization stuff ends here. So, we can start time-counting
		
		setInitialTime();

	} //end-method 
	
	/** 
	 * Reads configuration script, to extract the parameter's values.
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */	
	protected void readParameters (String script) {
		
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
	    
	    //Getting the MAX EVALUATIONS parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    K = Integer.parseInt(tokens.nextToken().substring(1));
	    
	    //Getting the MAX EVALUATIONS parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    MAX_EVALUATIONS = Integer.parseInt(tokens.nextToken().substring(1));
    
	    //Getting the sizePop parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    sizePop = Integer.parseInt(tokens.nextToken().substring(1));
	    
	    //Getting the alpha parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    alpha = Double.parseDouble(tokens.nextToken().substring(1));
	    
	    //Getting the r parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    prob0to1 = Double.parseDouble(tokens.nextToken().substring(1));
	    
	    //Getting the prob1
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    prob1 = Double.parseDouble(tokens.nextToken().substring(1));
	    
		//Getting the mutation probability
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    mutProb = Double.parseDouble(tokens.nextToken().substring(1));
	    
		//Getting the epoch length (FW)
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    epochFW = Integer.parseInt(tokens.nextToken().substring(1));

	  //Getting the epoch length (FW)
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    epochIW = Integer.parseInt(tokens.nextToken().substring(1));
	    
	}//end-method

	/**
	 * Performs the coevolutionary search
	 */
	public void coevolution(){

		fitA= new double [10000];
		fitB= new double [10000];
		fitC= new double [10000];
		evs= new int [10000];
		counter=0;
		
		Arrays.fill(fitA, -1.0);
		Arrays.fill(fitB, -1.0);
		Arrays.fill(fitC, -1.0);
		Arrays.fill(evs, -1);
		
		//initial evaluations
		initialEvaluation();
		
		bestIS=ISPopulation[0].clone();
		bestFW=FWPopulation[0].clone();
		bestIW=IWPopulation[0].clone();
		
		evaluations=0;
		bestFitnessIS=Double.MIN_VALUE;
		bestFitnessFW=Double.MIN_VALUE;
		bestFitnessIW=Double.MIN_VALUE;
		
		while(evaluations<MAX_EVALUATIONS){
			
			//IS generation
			WKNN.setFeatureWeights(bestFW.getAll());
			WKNN.setInstanceWeights(bestIW.getAll());
			
			evaluations+=BinaryCHC.generation(ISPopulation);
			
			//FW generation
			WKNN.setInstances(bestIS.getAll());
			WKNN.setInstanceWeights(bestIW.getAll());
			
			evaluations+=RealCHC.generation(FWPopulation);
			
			//IW generation
			WKNN.setInstances(bestIS.getAll());
			WKNN.setFeatureWeights(bestFW.getAll());
			
			evaluations+=RealIWCHC.generation(IWPopulation);			
			
			//Update best individuals
			if(ISPopulation[0].getFitness()>bestFitnessIS){
				bestIS=ISPopulation[0].clone();
				bestFitnessIS=ISPopulation[0].getFitness();
				System.out.println("-----"+bestFitnessIS);
			}
			
			if(FWPopulation[0].getFitness()>bestFitnessFW){
				bestFW=FWPopulation[0].clone();
				bestFitnessFW=FWPopulation[0].getFitness();
				System.out.println("*****"+bestFitnessFW);
			}
			
			if(IWPopulation[0].getFitness()>bestFitnessIW){
				bestIW=IWPopulation[0].clone();
				bestFitnessIW=IWPopulation[0].getFitness();
				System.out.println();
				System.out.println("+++"+bestFitnessIW);
			}
			
			fitA[counter]=ISPopulation[0].getFitness();
			fitB[counter]=FWPopulation[0].getFitness();
			fitC[counter]=IWPopulation[0].getFitness();
			
			evs[counter]=evaluations;
			counter++;
		}
		
		System.out.println(bestIS);
		System.out.println(bestFW);
		System.out.println(bestIW);
	}
	
	/**
	 * Performs an initial evaluation of the populations
	 */
	private void initialEvaluation(){
		
		double acc,red;
		int fullIS [];
		double fullFW [];
		double fullIW [];
		double fitness;
		
		fullIS=new int [trainData.length];
		fullFW=new double [inputAtt];
		fullIW=new double [nClasses];
		
		Arrays.fill(fullIS, 1);
		Arrays.fill(fullFW, 1.0);
		Arrays.fill(fullIW, 0.5);
		
		//evaluateISPopulation
		
		WKNN.setFeatureWeights(fullFW);
		WKNN.setInstanceWeights(fullIW);
		
		for(int i=0;i<sizePop;i++){
			
			WKNN.setInstances(ISPopulation[i].getAll());
			acc=evaluate();	
			red=ISPopulation[i].computeRed();
			
			fitness=ISFitness(acc,red);
			
			ISPopulation[i].setFitness(fitness);
			
		}
		
		//evaluateFWPopulation
		
		WKNN.setInstances(fullIS);
		WKNN.setInstanceWeights(fullIW);
		
		for(int i=0;i<sizePop;i++){
			
			WKNN.setFeatureWeights(FWPopulation[i].getAll());
			acc=evaluate();		
			
			fitness=FWFitness(acc);
			
			FWPopulation[i].setFitness(fitness);
			
		}
		
		//evaluateIWPopulation
		
		WKNN.setInstances(fullIS);
		WKNN.setFeatureWeights(fullFW);
		
		for(int i=0;i<sizePop;i++){
			
			WKNN.setInstanceWeights(IWPopulation[i].getAll());
			acc=evaluate();		
			
			fitness=IWFitness(acc);
			
			IWPopulation[i].setFitness(fitness);
			
		}		
		
		//sort populations
		
		Arrays.sort(ISPopulation);
		Arrays.sort(FWPopulation);
		Arrays.sort(IWPopulation);
	}
	
	/**
	 * Performs an evaluation
	 * 
	 * @return Accuracy obtained
	 */
	private double evaluate(){
		
		double value;
		
		value=WKNN.accuracy();
		evaluations++;
		
		return value;
	}
	
	/**
	 * Sets the fitness for a IS individual
	 * 
	 * @param acc Accuracy computed
	 * @param red Reduction rate computed
	 * 
	 * @return Fitness computed
	 */
	private double ISFitness(double acc,double red){
		
		double result;
		
		result= (alpha*acc)+((1.0-alpha)*red);
		
		return result;
		
	}
	
	/**
	 * Sets the fitness for a FW individual
	 * 
	 * @param acc Accuracy computed
	 * 
	 * @return Fitness computed
	 */
	private double FWFitness(double acc){
		
		double result;
		
		result=acc;
		
		return result;
		
	}
	
	/**
	 * Sets the fitness for a IW individual
	 * 
	 * @param acc Accuracy computed
	 * 
	 * @return Fitness computed
	 */
	private double IWFitness(double acc){
		
		double result;
		
		result=acc;
		
		return result;
		
	}
	
	/**
	 * Classifies the training set
	 * 
	 * @return Output computed
	 */
	public int [] classifyTraining(){
		
		int result []= new int [trainData.length];
		
		WKNN.setInstances(bestIS.getAll());
		WKNN.setFeatureWeights(bestFW.getAll());
		WKNN.setInstanceWeights(bestIW.getAll());
		
		for(int i=0;i<trainData.length;i++){
			
			result[i]=WKNN.classifyTrainInstance(i);
			
		}
		
		return result;
	}
	
	/**
	 * Classifies the test set
	 * 
	 * @return Output computed
	 */
	public int [] classifyTestSet(){
		
		int result []= new int [testData.length];
		
		WKNN.setInstances(bestIS.getAll());
		WKNN.setFeatureWeights(bestFW.getAll());
		WKNN.setInstanceWeights(bestIW.getAll());
		
		for(int i=0;i<testData.length;i++){
			
			result[i]=WKNN.classifyNewInstance(testData[i]);
			
		}
		
		return result;
	}
	
	/** 
	 * Executes the classification of train dataset
	 * 
	 */	
	public void classifyTrain(){
		
		modelTime=((double)System.currentTimeMillis()-initialTime)/1000.0;
		System.out.println(name+" "+ relation + " Model " + modelTime + "s");
		
		//Check  time		
		setInitialTime();
		
		int [] clasResult;
		
		trainRealClass = new int[trainData.length][1];
		trainPrediction = new int[trainData.length][1];			
		    
		clasResult=classifyTraining();
		
		for (int i=0; i<trainRealClass.length; i++) {
			trainRealClass[i][0]= trainOutput[i];
			trainPrediction[i][0]= clasResult[i];
		}
			
		trainingTime=((double)System.currentTimeMillis()-initialTime)/1000.0;
		
		//Writing results
		writeOutput(outFile[0], trainRealClass, trainPrediction);
		System.out.println(name+" "+ relation + " Training " + trainingTime + "s");
		
	}//end-method 
	
	/** 
	 * Executes the classification of test dataset
	 * 
	 */	
	public void classifyTest(){
		
		//Check  time		
		setInitialTime();
		
		int [] clasResult;
		
		testRealClass = new int[testData.length][1];
		testPrediction = new int[testData.length][1];			
		    
		clasResult=classifyTestSet();
		
		for (int i=0; i<testRealClass.length; i++) {
			testRealClass[i][0]= testOutput[i];
			testPrediction[i][0]= clasResult[i];
		}
			
		testTime=((double)System.currentTimeMillis()-initialTime)/1000.0;
		
		//Writing results
		writeOutput(outFile[1], testRealClass, testPrediction);
		System.out.println(name+" "+ relation + " Test " + testTime + "s");
		
	}//end-method 


	/**
	 * Prints the additional output file
	 */
	public void printExitValues(){
		
		double redIS;

		String text="";		
		
		computeConfussionMatrixes();
		
		//Accuracy
		text+="Accuracy: "+getAccuracy()+"\n";
		text+="Accuracy (Training): "+getTrainAccuracy()+"\n";
		
		//Kappa
		text+="Kappa: "+getKappa()+"\n";
		text+="Kappa (Training): "+getTrainKappa()+"\n";
		
		//Unclassified
		text+="Unclassified instances: "+testUnclassified+"\n";
		text+="Unclassified instances (Training): "+trainUnclassified+"\n";	
		
		//Reduction
		
		redIS=bestIS.computeRed();

		//Reduction IS	
		text+= "Reduction (IS): " +redIS+ "\n";
		
		//Model time
		text+= "Model time: "+modelTime+" s\n";
		
		//Training time
		text+= "Training time: "+trainingTime+" s\n";
		
		//Test time
		text+= "Test time: "+testTime+" s\n";
		
		//Print final chromosomes
		text+="Final solution:\n";
		text+=bestIS+"\n";
		text+=bestFW+"\n";
		text+=bestIW+"\n";
		text+="\n";
		
		//Confusion matrix
		text+="Confussion Matrix:\n";
		for(int i=0;i<nClasses;i++){
			
			for(int j=0;j<nClasses;j++){
				text+=testConfMatrix[i][j]+"\t";
			}
			text+="\n";
		}
		text+="\n";
		
		text+="Training Confussion Matrix:\n";
		for(int i=0;i<nClasses;i++){
			
			for(int j=0;j<nClasses;j++){
				text+=trainConfMatrix[i][j]+"\t";
			}
			text+="\n";
		}
		text+="\n";

		text+="Convergence\n\n";
		for(int i=0;i<counter;i++){
			text+=evs[i]+" "+fitA[i]+" "+fitB[i]+" "+fitC[i]+"\n";
		}
		
		//Finish additional output file
		Files.writeFile (outFile[2], text);
		
	}//end-method 
	
	/**
	 * Computes the confusion matrixes
	 * 
	 */
	private void computeConfussionMatrixes(){
		
		testConfMatrix= new int [nClasses][nClasses];
		trainConfMatrix= new int [nClasses][nClasses];
		
		testUnclassified=0;
		
		for(int i=0;i<nClasses;i++){
			Arrays.fill(testConfMatrix[i], 0);
		}
		
		for(int i=0;i<testPrediction.length;i++){
			if(testPrediction[i][0]==-1){
				testUnclassified++;
			}else{
				testConfMatrix[testPrediction[i][0]][testRealClass[i][0]]++;
			}
		}
		
		trainUnclassified=0;
		
		for(int i=0;i<nClasses;i++){
			Arrays.fill(trainConfMatrix[i], 0);
		}
		
		for(int i=0;i<trainPrediction.length;i++){
			if(trainPrediction[i][0]==-1){
				trainUnclassified++;
			}else{
				trainConfMatrix[trainPrediction[i][0]][trainRealClass[i][0]]++;
			}
		}
		
	}//end-method 
	
	/**
	 * Computes the accuracy obtained on test set
	 * 
	 * @return Accuracy on test set
	 */
	private double getAccuracy(){
		
		double acc;
		int count=0;
		
		for(int i=0;i<nClasses;i++){			
			count+=testConfMatrix[i][i];
		}
		
		acc=((double)count/(double)test.getNumInstances());
		
		return acc;
		
	}//end-method 
	
	/**
	 * Computes the accuracy obtained on the training set
	 * 
	 * @return Accuracy on test set
	 */
	private double getTrainAccuracy(){
		
		double acc;
		int count=0;
		
		for(int i=0;i<nClasses;i++){			
			count+=trainConfMatrix[i][i];
		}
		
		acc=((double)count/(double)train.getNumInstances());
		
		return acc;
		
	}//end-method 
	
	/**
	 * Computes the Kappa obtained on test set
	 * 
	 * @return Kappa on test set
	 */	
	private double getKappa(){
		
		double kappa;
		double agreement,expected;
		int count,count2;
		double prob1,prob2;
		
		count=0;
		for(int i=0;i<nClasses;i++){			
			count+=testConfMatrix[i][i];
		}
		
		agreement=((double)count/(double)test.getNumInstances());
		
		expected=0.0;
		
		for(int i=0;i<nClasses;i++){			
			
			count=0;
			count2=0;
			
			for(int j=0;j<nClasses;j++){
				count+=testConfMatrix[i][j];
				count2+=testConfMatrix[j][i];
			}
			
			prob1=((double)count/(double)test.getNumInstances());
			prob2=((double)count2/(double)test.getNumInstances());
			
			expected+=(prob1*prob2);
		}

		kappa=(agreement-expected)/(1.0-expected);
		
		return kappa;
		
	}//end-method 

	/**
	 * Computes the Kappa obtained on test set
	 * 
	 * @return Kappa on test set
	 */	
	private double getTrainKappa(){
		
		double kappa;
		double agreement,expected;
		int count,count2;
		double prob1,prob2;
		
		count=0;
		for(int i=0;i<nClasses;i++){			
			count+=trainConfMatrix[i][i];
		}
		
		agreement=((double)count/(double)train.getNumInstances());
		
		expected=0.0;
		
		for(int i=0;i<nClasses;i++){			
			
			count=0;
			count2=0;
			
			for(int j=0;j<nClasses;j++){
				count+=trainConfMatrix[i][j];
				count2+=trainConfMatrix[j][i];
			}
			
			prob1=((double)count/(double)train.getNumInstances());
			prob2=((double)count2/(double)train.getNumInstances());
			
			expected+=(prob1*prob2);
		}

		kappa=(agreement-expected)/(1.0-expected);
		
		return kappa;
		
	}//end-method 
	
	/**
	 * Prints output files.
	 * 
	 * @param filename Name of output file
	 * @param realClass Real output of instances
	 * @param prediction Predicted output for instances
	 */
	private void writeOutput(String filename, int [][] realClass, int [][] prediction) {
	
		String text = "";
		
		/*Printing input attributes*/
		text += "@relation "+ relation +"\n";

		for (int i=0; i<inputs.length; i++) {
			
			text += "@attribute "+ inputs[i].getName()+" ";
			
		    if (inputs[i].getType() == Attribute.NOMINAL) {
		    	text += "{";
		        for (int j=0; j<inputs[i].getNominalValuesList().size(); j++) {
		        	text += (String)inputs[i].getNominalValuesList().elementAt(j);
		        	if (j < inputs[i].getNominalValuesList().size() -1) {
		        		text += ", ";
		        	}
		        }
		        text += "}\n";
		    } else {
		    	if (inputs[i].getType() == Attribute.INTEGER) {
		    		text += "integer";
		        } else {
		        	text += "real";
		        }
		        text += " ["+String.valueOf(inputs[i].getMinAttribute()) + ", " +  String.valueOf(inputs[i].getMaxAttribute())+"]\n";
		    }
		}

		/*Printing output attribute*/
		text += "@attribute "+ output.getName()+" ";

		if (output.getType() == Attribute.NOMINAL) {
			text += "{";
			
			for (int j=0; j<output.getNominalValuesList().size(); j++) {
				text += (String)output.getNominalValuesList().elementAt(j);
		        if (j < output.getNominalValuesList().size() -1) {
		        	text += ", ";
		        }
			}		
			text += "}\n";	    
		} else {
		    text += "integer ["+String.valueOf(output.getMinAttribute()) + ", " + String.valueOf(output.getMaxAttribute())+"]\n";
		}

		/*Printing data*/
		text += "@data\n";

		Files.writeFile(filename, text);
		
		if (output.getType() == Attribute.INTEGER) {
			
			text = "";
			
			for (int i=0; i<realClass.length; i++) {
			      
			      for (int j=0; j<realClass[0].length; j++){
			    	  text += "" + realClass[i][j] + " ";
			      }
			      for (int j=0; j<realClass[0].length; j++){
			    	  text += "" + prediction[i][j] + " ";
			      }
			      text += "\n";			      
			      if((i%10)==9){
			    	  Files.addToFile(filename, text);
			    	  text = "";
			      }     
			}			
			
			if((realClass.length%10)!=0){
				Files.addToFile(filename, text);
			}
		}
		else{
			
			text = "";
			
			for (int i=0; i<realClass.length; i++) {
			      
			      for (int j=0; j<realClass[0].length; j++){
			    	  text += "" + (String)output.getNominalValuesList().elementAt(realClass[i][j]) + " ";
			      }
			      for (int j=0; j<realClass[0].length; j++){
			    	  if(prediction[i][j]>-1){
			    		  text += "" + (String)output.getNominalValuesList().elementAt(prediction[i][j]) + " ";
			    	  }
			    	  else{
			    		  text += "" + "Unclassified" + " ";
			    	  }
			      }
			      text += "\n";
			      
			      if((i%10)==9){
			    	  Files.addToFile(filename, text);
			    	  text = "";
			      } 
			}			
			
			if((realClass.length%10)!=0){
				Files.addToFile(filename, text);
			}		
		}
		
	}//end-method 
    
    
} //end-class 

