//
//  SGA.java
//
//  Salvador García López
//
//  Created by Salvador García López 19-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Feature_Selection.evolutionary_algorithms.GGA;

import keel.Algorithms.Preprocess.Basic.*;
import keel.Algorithms.Preprocess.Feature_Selection.Shared.*;

import org.core.*;

import java.util.StringTokenizer;
import java.util.Arrays;

public class GGA extends FSAlgorithm{

	/*Own parameters of the algorithm*/
	private long seed;
	private double crossProb;
	private double mutationProb;
	private int popSize;
	private int evaluations;
	private int maxEvaluations;
	private double beta;
	private int k;
	private boolean elitism;
	private Chromosome ace;

	private Chromosome population [];
	private Chromosome newPop [];

	public GGA (String script) {
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="FS-GGA";

		evaluations=0;

		//Inicialization of auxiliar structures
		
		Chromosome.setData(trainData,trainOutput);
		Chromosome.setK(k);
		Chromosome.setNClasses(nClasses);
		Chromosome.setMutationProb(mutationProb);
		Chromosome.setBeta(beta);
		
	    population = new Chromosome[popSize];
	    newPop = new Chromosome[popSize];
	    for (int i=0; i<popSize; i++){
	    	population[i] = new Chromosome (inputAtt);
	    	population[i].evaluate();
	    }

	    Arrays.sort(population);
	    
	    if(elitism==true){
	    	ace=new Chromosome(population[0].getGenes(),population[0].getFitness());
	    }
	    
		//Initialization of random generator
	    
	    Randomize.setSeed(seed);
	    
		//Initialization stuff ends here. So, we can start time-counting
		
		setInitialTime(); 
	}
  
  
	protected void readParameters(String script){
		
		String file;
		String line;
		StringTokenizer fileLines, tokens;
		
	    file = Files.readFile(script);
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

	    /*Getting the cross probability*/
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    crossProb = Double.parseDouble(tokens.nextToken().substring(1));

	    /*Getting the mutation probability*/
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    mutationProb = Double.parseDouble(tokens.nextToken().substring(1));

	    /*Getting the size of the population*/
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    popSize = Integer.parseInt(tokens.nextToken().substring(1));
	    
	    /*Getting the number of evaluations*/
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    maxEvaluations = Integer.parseInt(tokens.nextToken().substring(1));

	    //Getting the beta parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    beta = Double.parseDouble(tokens.nextToken().substring(1));

	    //Getting the K parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    k = Integer.parseInt(tokens.nextToken().substring(1));
	    
	    //Getting the use of elitism parameter*/
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();;
	    elitism = tokens.nextToken().substring(1).equalsIgnoreCase("Yes")?true:false;   
 	  
	}

	public void execute () {

		
		int candidate1, candidate2;
		int selected1, selected2;
		
		while (evaluations < maxEvaluations) {
			
			for(int i=0;i<popSize;i+=2){
				
		        //Binary tournament selection: First candidate
				
				candidate1 = Randomize.Randint(0,popSize-1);
		        do {
		        	candidate2 = Randomize.Randint(0,popSize-1);
		        } while (candidate2 == candidate1);
		        
		        if (population[candidate1].getFitness() > population[candidate2].getFitness()){
		        	selected1=candidate1;
		        }
		        else{
		        	selected1=candidate2;
		        }
				
		        //Binary tournament selection: Second candidate
				
				candidate1 = Randomize.Randint(0,popSize-1);
		        do {
		        	candidate2 = Randomize.Randint(0,popSize-1);
		        } while (candidate2 == candidate1);
		        
		        if (population[candidate1].getFitness() > population[candidate2].getFitness()){
		        	selected2=candidate1;
		        }
		        else{
		        	selected2=candidate2;
		        }
				
		        //Cross operator

		        if (Randomize.Rand() < crossProb) { 
		        	newPop[i] = new Chromosome (population[selected1].getGenes());
		        	newPop[i+1] = new Chromosome (newPop[i].crossPMX(population[selected2].getGenes()));
		        } 
		        else { //there is not cross
		        	newPop[i] = new Chromosome (population[selected1].getGenes(),population[selected1].getFitness());
		        	newPop[i+1] = new Chromosome (population[selected2].getGenes(),population[selected2].getFitness());
		        }
				
		        //Mutation operator
		        newPop[i].mutation();
		        newPop[i+1].mutation();
		        
		        /*Evaluation of the offspring*/
		        if(newPop[i].getValid()==false){
		        	newPop[i].evaluate();
		        	evaluations++;
		        }
		        if(newPop[i+1].getValid()==false){
		        	newPop[i+1].evaluate();
		        	evaluations++;
		        }
			}  

	        /*Replace the population*/
			
			System.arraycopy(newPop, 0, population, 0, popSize);
	        Arrays.sort(population);
	        
		    if(elitism==true){
		    	if(population[0].getFitness()>ace.getFitness()){	    		
		    		ace=new Chromosome(population[0].getGenes(),population[0].getFitness());
		    	}
		    	else{
		    		population[popSize-1] = new Chromosome (ace.getGenes(),ace.getFitness());
		    	}
		    }
		}
    
	    Arrays.sort(population);
	    
	    //Features selected
	    int featSelected[]=new int[inputAtt];
	
	    featSelected=population[0].getGenes();
	    
	    System.out.println(name+" "+ relation + " Train " + (double)(System.currentTimeMillis()-initialTime)/1000.0 + "s");
	    OutputFS.writeTrainOutput(outFile[0], trainReal, trainNominal, trainNulls, trainOutput,featSelected, inputs, output, inputAtt, relation);
	    OutputFS.writeTestOutput(outFile[1], test, featSelected, inputs, output, inputAtt, relation);
	    OutputIS.escribeSalidaAux(outFile[1]+".txt",((double)(System.currentTimeMillis()-initialTime)/1000.0),1.0-((double)population[0].getNGenes()/(double)trainData[0].length),relation);
	}

}