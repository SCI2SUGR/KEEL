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
import java.lang.*;
import java.io.*;
import java.util.*;


public class TournamentSelection implements Selection {
/**
 * <p>
 * This class implements the tournament selection method.
 * </p>
 */
	
   ///////////////////////////////////////
  // attributes


/**
 * <p>
 * It is the first set of the population chosen by tournament.
 * </p>
 * 
 */
    private Classifier [] set1; 

/**
 * <p>
 * It is the second set of the population chosen by tournament.
 * </p>
 * 
 */
    private Classifier [] set2; 

/**
 * <p>
 * It is the set from which the selection has to be made.
 * </p>
 * 
 */
    private int activeSet = 1; 

/**
 * <p>
 * It is the size of the tournament.
 *
 */
   private int tournamentSize;

  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Creates a TournamentSelection object
 * </p>
 * 
 */
    public  TournamentSelection() {        
    	activeSet = 1;
    	tournamentSize = 0;
    	set1 = null;
    	set2 = null;
    } // end TournamentSelection        



/**
 * <p>
 * Initializes the tournament selection. It initializes two sets of 
 * classifiers to make the tournament selection with them. Their size 
 * is a fraction of the action set size.
 * </p>
 * <p>
 * 
 * @param pop is the population.
 * </p>
 */
    public void init(Population pop) {        
        int aleat = 0;
        int aSetSize = pop.getMacroClSum();
        tournamentSize = (int)Config.tournamentSize * aSetSize;
        set1 = new Classifier [tournamentSize];
        
        for (int i = 0; i<tournamentSize; i++){
        	aleat = (int) (Config.rand() * (double)aSetSize);
        	if (aleat == aSetSize) aleat --;
        	set1[i] = pop.getClassifier(aleat);
        }
        
        set2 = new Classifier[tournamentSize];
        for (int i = 0; i<tournamentSize; i++){
        	aleat = (int) (Config.rand() * (double)aSetSize);
        	if (aleat == aSetSize) aleat --;
        	set2[i] = pop.getClassifier(aleat);
        }
	activeSet = 1;
    } // end init



/**
 * <p>
 * Applies the tournament selection.
 * </p>
 * <p>
 * @param pop is the population.
 * </p>
 * <p>
 * @return a Classifier with the selected classifier
 * </p>
 */
    public Classifier makeSelection(Population pop) {        
        double maxFitness = -1000;
        int pos = -1;
        for (int i=0; i<tournamentSize; i++){
        	if (activeSet == 1){
        		if (set1[i].getFitness() > maxFitness){
        			pos = i;
        			maxFitness = set1[i].getFitness();
        		}
        	}
        	else{
        		if (set2[i].getFitness() > maxFitness){
        			pos = i;
        			maxFitness = set2[i].getFitness();
        		}
        	}
        }
        
        if (pos >= 0){
        	if (activeSet == 1){
        		activeSet = 2;
        		return set1[pos];
        	}
        	else{
        		activeSet = 1;
        		return set2[pos];
        	}
	}
        else{
        	activeSet = activeSet%2 +1;
        	return null;
        }
    } // end makeSelection        




} // end TournamentSelection




