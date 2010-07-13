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



public class DixonReduction implements Reduction {
/**
 * <p>
 * This class implements the reduction Interface. It codifies  
 * Dixon et al's reduction algorithm proposed in [Dixon et al, 2003]. 
 * Some extra decisions taken, not clearly explained in the paper,
 * are detailed in the algorithmic description by Albert Orriols.
 * </p>
 */
  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Constructs an object of the class.
 * </p>
 */
    public  DixonReduction() {        
    } // end DixonReduction        

/**
 * <p>
 * Compacts the ruleSet of the population using the Dixon method
 * described in the article as the &quot;alternative reduction&quot;.
 * It destructs neither the environment nor the population.
 * </p>
 * <p>
 * @return a Population with the reducted population. The initial
 * population is not modified.
 * </p>
 * <p>
 * @param pop is the population that has to be reduced.
 *
 * @param reductionEnv  is the environment that will be used to get the performance
 * of classifiers.
 * </p>
 */
    public Population makeReduction(Population pop, Environment reductionEnv) {        
        
        // We create a new population with only the experienced classifiers.
        System.out.println ("\tInitial Population Size: "+pop.getMacroClSum());
        Population pExp = pop.deleteNotExpClassifiers(reductionEnv.getMaxPayoff());
        System.out.println ("\tQualified Population Size: "+pExp.getMacroClSum());
        pExp.printPopulationToFile(Config.reductedRulesFile+".experienced.plt");
        
        // All classifiers are set to useless.
        pExp.setUseful(false);
        
        // Enable if you want to print the qualified population
        //pExp.print();
        
        reductionEnv.beginSequentialExamples();
        int numberOfExamples = reductionEnv.getNumberOfExamples();
        
        //System.out.println ("The examples number of the environment is: "+numberOfExamples);
        
        for (int i=0; i<numberOfExamples; i++){
        	double [] example = null;
        	if (i==0) 	example = reductionEnv.getCurrentState();
        	else 		example = reductionEnv.getSequentialState();
        	
       	
        	Population matchSet = new Population (pExp.getMacroClSum());
        	
        	for (int j=0; j<pExp.getMacroClSum(); j++){
        		if (pExp.getClassifier(j).match(example)){
        			matchSet.addClassifier(pExp.getClassifier(j));	
        		}	
        	}
        	  	
        	if (matchSet.getMacroClSum() > 0){
        		PredictionArray predArray = new PredictionArray(matchSet);
        		if (predArray.howManyBestActions() == 1){
        			int actionChosen = predArray.getBestAction();
        			Population actionSet = new Population (matchSet, actionChosen);
        			if (Config.typeOfReduction.toUpperCase().equals("WD")){ //Weak Reduction
        				actionSet.setUseful(true);
        			}
        			else{ //Strong Reduction
        				actionSet.setUsefulAccurateClassifier(true);
        			
        			}
        		}
        	}
        }
        
        //Now, the useless classifiers are removed from population
        System.out.println ("\tThe non useful Classifiers of "+pExp.getMacroClSum()+" qualified are: "+pExp.numberOfNotUseful());
        pExp.removeNonUsefulClassifiers();
        System.out.println ("\tReducted Population Size: "+pExp.getMacroClSum()+"\n\n");
        return pExp;
    } // end makeReduction        

 } // end DixonReduction




