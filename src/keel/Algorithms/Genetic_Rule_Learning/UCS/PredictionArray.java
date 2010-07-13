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
 * @author Written by Albert Orriols (La Salle University Ramón Lull, Barcelona)  28/03/2004
 * @author Modified by Xavi Solé (La Salle University Ramón Lull, Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Genetic_Rule_Learning.UCS;

import java.lang.*;
import java.io.*;
import java.util.*;


 
public class PredictionArray {
/**
 * <p>
 * Prediction array
 * This class builds the prediction array from the match set
 * </p>
 */
	
  ///////////////////////////////////////
  // attributes


/**
 * At first, it represents the sum of all the predictions of classifiers. When 
 * the prediction array has been constructed, it has the prediction for each
 * action. The size of this array is the number of actions that can take a
 * classifier.
 */
    private double [] predArray; 

/**
 * Represents the sum of all the numerosities.
 */
    private double [] numSum; 




/**
 * <p>
 * This function builds the prediction arry
 * </p>
 * @param pop is the population for which the prediction array has to be created.
 */
  public  PredictionArray(Population pop) {        
	int i=0;
	predArray = new double [Config.numberOfActions];
	numSum 	  = new double [Config.numberOfActions];
	
	for (i=0; i<Config.numberOfActions; i++){
		predArray[i] = 0;
		numSum[i] = 0;
	}
	
	// For each macroclassifier in the population.
	for (i = 0; i<pop.getMacroClSum(); i++){
		Classifier cl = pop.getClassifier(i);
		if (cl != null){
			predArray [ cl.getAction() ] +=  cl.getAccuracy() * cl.getMacroClFitness();
			numSum[ cl.getAction() ] += cl.getNumerosity();
		}
	}        
  } // end PredictionArray        



/**
 * <p>
 * Returns the best action in the prediction array. If there
 * are more than one "best action" it selects one randomly.
 * </p>
 * @return a int with the best action.
 */
    public int getBestAction() {        
        double maxPA = predArray[0];
        int pos = 0;
		Vector bestActions = new Vector();
        
        for (int i=1; i<predArray.length; i++){
        	if (predArray[i] > maxPA){
        		bestActions.removeAllElements();
        		maxPA = predArray[i];
        		pos = i;
        		bestActions.add(new Integer(i));
        	}else if (predArray[i] == maxPA){
        		bestActions.add(new Integer(i));
        	}
        }
        
        if (bestActions.size() >1){
        	return ((Integer)bestActions.elementAt( (int)(Config.rand()*(double)bestActions.size()) ) ).intValue();
        }
        else  return pos;
    } // end getBestAction        


/**
 * <p>
 * Returns the best action in the prediction array.
 * </p>
 * @return an integer with the best action.
 */
    public double getBestValue() {        
        double maxPA = predArray[0];
        for (int i=1; i<predArray.length; i++){
        	if (predArray[i] > maxPA){
        		maxPA = predArray[i];
        	}
        }
        return maxPA;
    } // end getBestValue     



/**
 * <p>
 * Returns the number of "best actions" in the prediction
 * array. 
 * If there are more than one "best action",it implies that it cannot be classified (the
 * system cannot decide, without a stochastic method ,the best action).
 * </p>
 * @return a int with the number of best actions in the prediction array.
 */
    public int howManyBestActions() {        
        double maxPA = predArray[0];
        int numBestActions = 1;
        for (int i=1; i<predArray.length; i++){
        	if (predArray[i] > maxPA){
        		maxPA = predArray[i];
        		numBestActions = 1;
        	}
        	else if (predArray[i] == maxPA){
        		numBestActions ++;	
        	}
        }
        return numBestActions;
    } // end howManyBestActions

	
/**
 * <p>
 * Returns the value of that position in the prediction array
 * </p>
 * @param action is the action  we want to know the value.
 * @return a double with the value in that position.
 */
	
    public double get (int action){
    	if (action >=0 && action <predArray.length){
    		return predArray[action];	
    	}
    	return 0.;
    }


/**
 * <p>
 * Prints the prediction array to the standard output.
 * </p>
 */

    public void print(){
    	for (int i =0; i<Config.numberOfActions; i++){
    		System.out.println ("Action "+i+": "+predArray[i]);
    	}
 	
 	System.out.println ("And the prediction error maximum is: "+getBestAction());
    }

} // end PredictionArray




