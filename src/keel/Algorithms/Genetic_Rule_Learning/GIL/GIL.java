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

package keel.Algorithms.Genetic_Rule_Learning.GIL;

/**
 * <p>Title: Algorithm</p>
 *
 * <p>Description: It contains the implementation of the algorithm</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Alberto Fernández
 * @version 1.0
 */

import java.io.IOException;
import java.util.Arrays;

import org.core.*;

public class GIL {

    myDataset train, val, test;
    String outputTr, outputTst, outputRule;
    int nClasses;
    
    //Parameters
    long seed;

    //We may declare here the algorithm's parameters
    int popSize;
    double w1;
    double w2;
    double w3;
    double p1a;
    double p1b;
    double p2;
    double p3;
    double p4;
    double p5;
    double p6;
    double p7a;
    double p7b;
    double p7c;
    double p8;
    double p9;
    double p10;
    double p11;
    double p12;
    double p13;
    double p14;
    int numGenerations;
    double pConditionLevel;
    double lowerThreshold;
    double upperThreshold;

    private boolean somethingWrong = false; //to check if everything is correct.

    /**
     * Default constructor
     */
    public GIL () {
    }

    /**
     * It reads the data from the input files (training, validation and test) and parse all the parameters
     * from the parameters array.
     * @param parameters parseParameters It contains the input files, output files and parameters
     */
    public GIL(parseParameters parameters) {

        train = new myDataset();
        val = new myDataset();
        test = new myDataset();
        try {
            System.out.println("\nReading the training set: " +
                               parameters.getTrainingInputFile());
            train.readClassificationSet(parameters.getTrainingInputFile(), true);
            System.out.println("\nReading the validation set: " +
                               parameters.getValidationInputFile());
            val.readClassificationSet(parameters.getValidationInputFile(), false);
            System.out.println("\nReading the test set: " +
                               parameters.getTestInputFile());
            test.readClassificationSet(parameters.getTestInputFile(), false);
        } catch (IOException e) {
            System.err.println(
                    "There was a problem while reading the input data-sets: " +
                    e);
            somethingWrong = true;
        }

        //We may check if there are some numerical attributes, because our algorithm may not handle them:
        somethingWrong = somethingWrong || train.hasRealAttributes();
        somethingWrong = somethingWrong || train.hasMissingAttributes();

        outputTr = parameters.getTrainingOutputFile();
        outputTst = parameters.getTestOutputFile();
        outputRule = parameters.getOutputFile(0);

        //Now we parse the parameters, for example:
        
         seed = Long.parseLong(parameters.getParameter(0));
         
         popSize = Integer.parseInt(parameters.getParameter(1));
         w1 = Double.parseDouble(parameters.getParameter(2));
         w2 = Double.parseDouble(parameters.getParameter(3));
         w3 = Double.parseDouble(parameters.getParameter(4));
         p1a = Double.parseDouble(parameters.getParameter(5));
         p1b = Double.parseDouble(parameters.getParameter(6));
         p2 = Double.parseDouble(parameters.getParameter(7));
         p3 = Double.parseDouble(parameters.getParameter(8));
         p4 = Double.parseDouble(parameters.getParameter(9));
         p5 = Double.parseDouble(parameters.getParameter(10));
         p6 = Double.parseDouble(parameters.getParameter(11));
         p7a = Double.parseDouble(parameters.getParameter(12));
         p7b = Double.parseDouble(parameters.getParameter(13));
         p7c = Double.parseDouble(parameters.getParameter(14));
         p8 = Double.parseDouble(parameters.getParameter(15));
         p9 = Double.parseDouble(parameters.getParameter(16));
         p10 = Double.parseDouble(parameters.getParameter(17));
         p11 = Double.parseDouble(parameters.getParameter(18));
         p12 = Double.parseDouble(parameters.getParameter(19));
         p13 = Double.parseDouble(parameters.getParameter(20));
         p14 = Double.parseDouble(parameters.getParameter(21));
         numGenerations = Integer.parseInt(parameters.getParameter(22));
         pConditionLevel = Double.parseDouble(parameters.getParameter(23));
         lowerThreshold = Double.parseDouble(parameters.getParameter(24));
         upperThreshold = Double.parseDouble(parameters.getParameter(25));
        
        //...
    }

    /**
     * It launches the algorithm
     */
    public void execute() {
    	
    	int i, j, k, l;
    	int nperClass[];
    	boolean flag[];
    	int pos, min, max;
    	int classAct;
    	RuleSet population[];
    	double costs[];
    	double minCost, maxCost;
    	double f=0.0;
    	double prob[];
    	double aux;
        double NUmax = 1.5; //used for lineal ranking
        double NUmin = 0.5; //used for lineal ranking
        RuleSet newPopulation[];
        double pos1, pos2;
        int sel1, sel2;
        double comp, cons;
        boolean act1, act2;
        int contAct;
        RuleSet solution[];
        int classSolution[];
        int maxClass;
    	
   	
        if (somethingWrong) { //We do not execute the program
            System.err.println("An error was found, either the data-set have numerical values or missing values.");
            System.err.println("Aborting the program");
            //We should not use the statement: System.exit(-1);
        } else {
            Randomize.setSeed (seed);
           
            nClasses = train.getnClasses();
            nperClass = new int[nClasses];
            flag = new boolean[nClasses];
            Arrays.fill(flag, true);
            for (i=0; i<train.getnData(); i++) {
            	nperClass[train.getOutputAsInteger(i)]++;
            }
            	
            solution = new RuleSet[nClasses-1];
            classSolution = new int[nClasses-1];
    		//Search the class with higher number of instances
    		for (j=0; j<nClasses && !flag[j]; j++);
    		pos = j;
    		max = nperClass[j];
    		for (j=pos+1; j <nClasses; j++) {
    			if (flag[j] && nperClass[j] >= max) {
    				pos = j;
    				max = nperClass[j];
    			}
    		}
    		maxClass = pos;

            
            //For each concept to be learned
        	for (i=0; i<nClasses-1; i++) {
        		//Search the class not chosen yet with lower number of instances
        		for (j=0; j<nClasses && !flag[j]; j++);
        		pos = j;
        		min = nperClass[j];
        		for (j=pos+1; j <nClasses; j++) {
        			if (flag[j] && nperClass[j] < min) {
        				pos = j;
        				min = nperClass[j];
        			}
        		}
        		flag[pos] = false;
        		
        		classAct = pos;
        		classSolution[i]=classAct;
        		System.out.println("Learning concept '" + train.getOutputValue(classAct) + "'");
        		if (nperClass[classAct] == 0) { //there is no examples of this class
        			System.out.println("There is no example of this concept.");
        			solution[i] = new RuleSet();
        		} else {
        		
        			//Initialize population
        			population = new RuleSet[popSize];
        			for (j=0; j<population.length; j++) {
        				population[j] = new RuleSet(train,classAct);
        			}
        			
        			//Compute cost for all chromosomes
        			costs = new double[popSize];
        			for (j=0; j<population.length; j++) {
        				costs[j] = population[j].computeCost();
        			}
        		
        			//Obtain max and min costs in the population
        			minCost = maxCost = costs[0];
        			for (j=1; j<costs.length; j++) {
        				if (costs[j] < minCost)
        					minCost = costs[j];
        				else if (costs[j] > maxCost)
        					maxCost = costs[j];
        			}
        		
        			//Evaluate the population
        			for (j=0; j<population.length; j++) {
        				population[j].computeFitness(train, classAct, minCost, maxCost, f, w1, w2, w3);
        			}
        		
        			//Construct the Baker selection roulette
        			prob = new double[popSize];
        			for (j=0; j<popSize; j++) {
        				aux = (double)( NUmax-NUmin)*((double)j/(popSize-1));
        				prob[j]=(double)(1.0/(popSize)) * (NUmax-aux);
        			}
        			for (j=1; j<popSize; j++)
        				prob[j] = prob[j] + prob[j-1];

					f = 0.0;
        		
        			//Evolutionary cycle
        			for (j=0; j<numGenerations; j++) {
        			
        				System.out.println("Start Generation: " + j);

        				/*Sort the population by fitness*/
        				Arrays.sort(population);
        	        
        				System.out.println("Fitness of the best chromosome: " + population[0].fitness);
        				System.out.println("Completeness of the best chromosome: " + population[0].completeness);
        				System.out.println("Consistency of the best chromosome: " + population[0].consistency);
        				System.out.println("Cost of the best chromosome: " + population[0].cost);
        				System.out.println("Aplication Probabilities: " + p1a + " - " + p2 + " - "+ p3 + " - "+ p4 + " - "+ p5 + " - "+ p6 + " - "+ p7a + " - "+ p8 + " - "+ p9 + " - "+ p10 + " - "+ p11 + " - "+ p12 + " - "+ p13 + " - "+ p14 + " - ");
        	        
        				newPopulation = new RuleSet[popSize];
        	        
        				/*Baker's selection*/
        				act1 = act2 = false;
        				contAct = 0;
        				for (k=0; k<((popSize)/2); k++) {
        					pos1 = Randomize.Rand();
        					pos2 = Randomize.Rand();
        					for (l=0; l<popSize && prob[l]<pos1; l++);
        					sel1 = l;
        					for (l=0; l<popSize && prob[l]<pos2; l++);
        					sel2 = l;
        	            
        					newPopulation[k*2] = new RuleSet(population[sel1]);
        					newPopulation[k*2+1] = new RuleSet(population[sel2]);
      	            
        					/*Application of the operators*/
        					if (Randomize.Rand() < p1a) {
        						newPopulation[k*2].rulesExchange(newPopulation[k*2+1], p1b);
        						act1 = act2 = true;
        					}
 	            
        					comp = newPopulation[k*2].computeCompleteness(train, classAct);
        					cons = newPopulation[k*2].computeConsistency(train, classAct);        	            
        					if (Randomize.Rand() < (p2*(1.5-comp)*(0.5+cons))) {
        						newPopulation[k*2].rulesCopy(newPopulation[k*2+1]);
        						act1 = true;
        					}
        					if (Randomize.Rand() < (p3*(1.5-comp)*(0.5+cons))) {
        						newPopulation[k*2].newEvent(train, classAct);
        						act1 = true;
        					}
        					if (Randomize.Rand() < (p4*(1.5-comp)*(0.5+cons))) {
        						newPopulation[k*2].rulesGeneralization(train);
        						act1 = true;
        					}
        					if (Randomize.Rand() < (p5*(0.5+comp)*(1.5-cons))) {
        						newPopulation[k*2].rulesDrop();
        						act1 = true;
        					}
        					if (Randomize.Rand() < (p6*(0.5+comp)*(1.5-cons))) {
        						newPopulation[k*2].rulesSpecialization(train);
        						act1 = true;
        					}        	            
        					act1 |= newPopulation[k*2].applyOperators(p7a, p7b, p7c, p8, p9, p10, p11, p12, p13, p14, pConditionLevel, train, classAct);

        					comp = newPopulation[k*2+1].computeCompleteness(train, classAct);
        					cons = newPopulation[k*2+1].computeConsistency(train, classAct);        	            
        					if (Randomize.Rand() < (p2*(1.5-comp)*(0.5+cons))) {
        						newPopulation[k*2+1].rulesCopy(newPopulation[k*2+1]);
        						act2 = true;
        					}
        					if (Randomize.Rand() < (p3*(1.5-comp)*(0.5+cons))) {
        						newPopulation[k*2+1].newEvent(train, classAct);
        						act2 = true;
        					}
        					if (Randomize.Rand() < (p4*(1.5-comp)*(0.5+cons))) {
        						newPopulation[k*2+1].rulesGeneralization(train);
        						act2 = true;
        					}
        					if (Randomize.Rand() < (p5*(0.5+comp)*(1.5-cons))) {
        						newPopulation[k*2+1].rulesDrop();
        						act2 = true;
        					}
        					if (Randomize.Rand() < (p6*(0.5+comp)*(1.5-cons))) {
        						newPopulation[k*2+1].rulesSpecialization(train);
        						act2 = true;
        					}
        					act2 |= newPopulation[k*2+1].applyOperators(p7a, p7b, p7c, p8, p9, p10, p11, p12, p13, p14, pConditionLevel, train, classAct);
        					
        					if (act1)
        						contAct++;
        					if (act2)
        						contAct++;
        				}
        	        
        				//Compute cost for all chromosomes
        				for (k=0; k<newPopulation.length; k++) {
        					costs[k] = newPopulation[k].computeCost();
        				}
            		
        				//Obtain max and min costs in the population
        				minCost = maxCost = costs[0];
        				for (k=1; k<costs.length; k++) {
        					if (costs[k] < minCost)
        						minCost = costs[k];
        					else if (costs[k] > maxCost)
        						maxCost = costs[k];
        				}
        				
        				//Evaluate the population
        				for (k=0; k<newPopulation.length; k++) {
        					newPopulation[k].computeFitness(train, classAct, minCost, maxCost, f, w1, w2, w3);
        				}
        				
        				if (((double)contAct/(double)popSize) >= upperThreshold) {
        					p1a *= 0.99;
        					p2 *= 0.99;
        					p3 *= 0.99;
        					p4 *= 0.99;
        					p5 *= 0.99;
        					p6 *= 0.99;
        					p7a *= 0.99;
        					p8 *= 0.99;
        					p9 *= 0.99;
        					p10 *= 0.99;
        					p11 *= 0.99;
        					p12 *= 0.99;
        					p13 *= 0.99;
        					p14 *= 0.99;
        				} else if (((double)contAct/(double)popSize) <= lowerThreshold) {
        					p1a *= 1.01;
        					p2 *= 1.01;
        					p3 *= 1.01;
        					p4 *= 1.01;
        					p5 *= 1.01;
        					p6 *= 1.01;
        					p7a *= 1.01;
        					p8 *= 1.01;
        					p9 *= 1.01;
        					p10 *= 1.01;
        					p11 *= 1.01;
        					p12 *= 1.01;
        					p13 *= 1.01;
        					p14 *= 1.01;            			
        				}
            		
        				f += (double)1 / (double)numGenerations;
        				population = newPopulation;
        			}
        		
        			/*Sort the population by fitness*/
        			Arrays.sort(population);
    	        
        			solution[i] = new RuleSet(population[0]);
        		}
        	}
            
            //Finally we should fill the training and test output files
            doOutput(this.val, this.outputTr, solution, classSolution, maxClass);
            doOutput(this.test, this.outputTst, solution, classSolution, maxClass);
            
            /*Print the rule obtained*/
            for (i=0; i<nClasses-1; i++) {
            	Fichero.AnadirtoFichero(outputRule, "Concept '" + train.getOutputValue(classSolution[i]) + "': \n");
            	Fichero.AnadirtoFichero(outputRule, solution[i].toString(train));
            }
        	Fichero.AnadirtoFichero(outputRule, "Concept' " + train.getOutputValue(maxClass) + "': Default.\n");

            System.out.println("Algorithm Finished");
        }
    }

    /**
     * It generates the output file from a given dataset and stores it in a file
     * @param dataset myDataset input dataset
     * @param filename String the name of the file
     * @param solution rule set for inducing the classification
     */
    private void doOutput(myDataset dataset, String filename, RuleSet solution[], int [] sol, int max) {
        String output = new String("");
        output = dataset.copyHeader(); //we insert the header in the output file
        //We write the output for each example
        for (int i = 0; i < dataset.getnData(); i++) {
            //for classification:
            output += dataset.getOutputAsString(i) + " " +
                    this.classificationOutput(i, solution, dataset, sol, max) + "\n";
                    
        }
        Fichero.escribeFichero(filename, output);
    }

    private String classificationOutput(int ej, RuleSet solution[], myDataset dataset, int [] sol, int max) {

    	int i, j;
    	boolean example[];
    	boolean rule[];
    	
    	for (i=0; i<nClasses-1; i++) {
    		for (j=0; j<solution[i].getRuleSet().size(); j++) {
    			example = Rule.toBitString(dataset, ej);
    			rule = solution[i].getRule(j).toBitString();
    			if (Rule.match(rule, example)) {
    				return dataset.getOutputValue(sol[i]);
    			}
    		}
    	}
    	
    	return dataset.getOutputValue(max);    	
    }

}

