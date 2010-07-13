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

/**
 * <p>
 * @author Written by Albert Orriols (La Salle, Ramón Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Genetic_Rule_Learning.XCS;
import  keel.Algorithms.Genetic_Rule_Learning.XCS.KeelParser.Config;
import java.util.*;
import java.lang.*;
import java.io.*;


public class WilsonReduction implements Reduction {
/**
 * <p>
 * This class implements the reduction Interface. It codifies an strict 
 * version of the wilson reduction proposed in Wilson 2002. 
 * </p>
 */	
	
  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Creates one WilsonReduction Object. 
 * </p>
 */
    public  WilsonReduction() {        
       
    } // end WilsonReduction        


/**
 * <p>
 * Compacts the ruleSet of the population. Before doing that, it writes
 * all the classifiers in a file. Then it compacts the classifiers
 * following the method described in Wilson's 2002 article. After all, it
 * writes  the new ruleset into a file, with the same name and .cmp
 * (compacted) extension.
 * It does not destroy the environment.
 * It does modify the population (it returns the population sorted). 
 * </p>
 * <p>
 * 
 * @return a Population with the reduced population. So, the initial
 * population is not modified.
 * </p>
 * <p>
 * @param pop is the population that has to be reduced.
 * </p>
 * <p>
 * @param reductionEnv is the environment that will be used to get the performance
 * of classifiers.
 * </p>
 */
    public Population makeReduction(Population pop, Environment reductionEnv) {        
       int i=0,j=0;

	
	//First we sort the population.
    	if (Config.typeOfReduction.toUpperCase().equals("ER"))
    		// The last parameter: 0 --> Numerosity, 1 --> Experience
    		pop.sortPopulation(0,pop.getMacroClSum()-1,0);
	else
		pop.sortPopulation(0,pop.getMacroClSum()-1,1);
	
	//////////////////////////////////////////////////////////////////
	//System.out.println ("After sorting the population, it keeps as: ");
	//pop.print();
	/////////////////////////////////////////////////////////////////
	
	// Now, we look for the performance of the sets of classifiers until we get a 100% performance.
	double [] performance = new double [pop.getMacroClSum()];
	for (i=0; i<performance.length; i++) performance[i] = 0.;
	i=0;
	boolean exit = false;
	
	double maxPerf = 0.;
	int posMaxPerf = 0;
	
	//System.out.println ("We start to compute the performance of the 'i' first classifiers"); 
	while (i<pop.getMacroClSum() && !exit){
		performance[i] = getPerformance(pop,reductionEnv,i);
		if (performance[i] > maxPerf){
			maxPerf = performance[i]; 
			posMaxPerf = i;
		}
		System.out.println ("Performance["+i+"] = "+performance[i]);
		if (performance[i] == 1.) exit = true;
        //We add 1 to i if we have found the 100% performance to get the number of classifiers needed of the position.
	i++; 
	}
	
	if (!exit){ 
		System.out.println ("THE POPULATION HAS NOT REACHED THE 100% PERFORMANCE. THE RULESSET REDUCTION WILL BE MADE WITH "+maxPerf+" PERFORMANCE");
		i = posMaxPerf+1;
	}
	double currentPerf = 0.;
	Population BPop = new Population(i); //We create a new population with the maximum size of 'i', the number of classifiers in the set with 100% performance
	System.out.println ("The population B of size "+i+" is builded. It is: ");
	for (j=0; j<i; j++){
		if (currentPerf < performance[j]){
			currentPerf = performance[j];
			BPop.addClassifier(pop.getClassifier(j));
		}
	}
	BPop.print();
	
	System.out.println ("\n\n The population Mcomp is created");
	return BPop.createMCompPopulation(reductionEnv);

    } // end makeReduction        




/**
 * <p>
 * Gets the performance of the first classifiers in the population
 * applied in the 'n' first classifiers in the population. 
 * A file environment is needed.
 * </p>
 * <p>
 * @param env is the file environment that contains the input examples.
 * </p>
 * <p>
 * @param n is the number of classifiers in the population that has to 
 * be considered. 
 * </p>
 * <p>
 * @return double with the percentage of correct classifications.
 * </p>
 */

    private double getPerformance(Population pop,Environment env,int n){
    	int i=0, j=0;
    	double []example= null;
    	int numberOfNoClassified=0, numberOfClassified=0,numberOfCorrectClassifications=0,numberOfWrongClassifications=0;
    	
    	env.beginSequentialExamples();
    	
    	for (i=0; i<=n; i++){
    		pop.getClassifier(i).setNumberMatches(0);
    	}
    	
    	for (i=0; i<env.getNumberOfExamples(); i++){
    		Population matchSet = new Population(pop.getMacroClSum());
    		if (i==0) example = env.getCurrentState();
    		else example = env.getSequentialState();
    		
    		// We add the classifiers of the population that match with this input example
    		for (j=0; j<=n; j++){ 
    			if (pop.getClassifier(j).match (example)){
				//set[j].print();
    				pop.getClassifier(j).increaseNumberMatches(1);
    				matchSet.addClassifier(pop.getClassifier(j));
    			}
    		}	
    		
    		//If there are no classifiers that match with the examples, we increase the numberOfNoClassified.
    		if (matchSet.getMacroClSum() == 0)	numberOfNoClassified ++;
    		else{ //Else we create the prediction array and look if the prediction is ok. 
    			
    			PredictionArray predArray = new PredictionArray(matchSet);
    			if (predArray.getBestValue()==0 || predArray.howManyBestActions() > 1){
    				 numberOfNoClassified ++;
    			}
    			else{
    				numberOfClassified ++;
    				if (predArray.getBestAction() == env.getEnvironmentClass())
    					numberOfCorrectClassifications++;	
    				else
    					numberOfWrongClassifications++;
    			}
    		}
    	}	
    	
    	    	
	return (double)numberOfCorrectClassifications / (double)env.getNumberOfExamples();
    } // end getPerformance






} // end WilsonReduction




