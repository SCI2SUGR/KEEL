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


public class Covering {
/**
 * <p>
 * This class implements the covering operator. It creates new classifiers that match with the environmental
 * state when an input is not covered.
 * </p>
 */
  ///////////////////////////////////////
  // attributes



  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Initializes the actionCovered vector (all its positions to false).
 * </p>
 * 
 */
    public  Covering() {        
    } // end Covering        



/**
 * <p>
 * Covers the actions while, at least, theta_mna actions are covered.
 * </p>
 * @param pop is the population where the new classifiers have to be introduced.
 * @param matchSet is the match set where the new classifiers have to be introduced to.
 * @param envState is the environmental state.
 * @param tStamp is the current time stamp.
 */
 
      public void coverActions(Population pop, Population matchSet,double[] envState,int tStamp,boolean [] actionCovered) {        
        int pos=0,actionToCover=0;
   	
	
   	while (numOfActionsCovered(actionCovered) < Config.theta_mna){
   		// We have to cover some positions...
   		
   		actionToCover = selectActionToCover(actionCovered);
   		if (!actionCovered[actionToCover]){ //Actions of covering.
   		   	
   		   	Classifier cl = new Classifier(envState, actionToCover, matchSet.getMicroClSum()+1,tStamp);			
   			
      			matchSet.addClassifier(cl); // The classifier is added to the match set.
   			pop.addClassifier(cl); //And it is added to the population.
   			actionCovered[actionToCover] = true;
   		}
   		
   			
   			
   			if (pop.getMicroClSum() > Config.popSize){
   				
   				Classifier clDeleted = pop.deleteClassifier(); //It's deleted from population
   				pos = matchSet.isThereClassifier(clDeleted); // It's seek in the match set. 
   				if (pos >=0){ // It has to be deleted from the match set to.
   					matchSet.increaseMicroClSum(-1);  // Decrements the number of microclassifiers
   					if (clDeleted.getNumerosity() == 0){ //It has to be completely deleted from match set.
	   					matchSet.deleteClassifier(pos);
	   				
	   					//If there isn't any other classifier that covers this action.
	   					actionCovered[clDeleted.getAction()] = isCovered(matchSet,clDeleted.getAction()) ;
	   				}
	   						
   					
   				}	
   				
   			}
     
   	}
    } // end coverAction        





/**
 * <p>
 * Returns the number of actions that are covered.
 * </p>
 * @return an integer with the number of covered actions.
 */
    private int numOfActionsCovered(boolean [] actionCovered) {       
    int num = 0;
        for (int i =0; i<Config.numberOfActions; i++){
        	if (actionCovered[i]) num++;
        }
	return num;
    } // end numOfActionCovered

/**
 * <p>
 * Indicates if the action is covered in the population
 * </p>
 * @param act is the action.
 * @param matchSet is the match Set.
 * @return a boolean indicating if the action is covered
 */
    private boolean isCovered(Population matchSet,int act) {        
        for (int i=0; i<matchSet.getMacroClSum(); i++){
        	if (matchSet.getClassifier(i).getAction() == act) return true;	
        }
        return false;
    } // end isCovered      


/**
 * <p>
 * Decides the action that has to be covered (randomly).
 * </p>
 * @return an integer with the action chosen to be covered.
 */

    private int selectActionToCover(boolean [] actionCovered){
    	int i = (int) (Config.rand() * (double)actionCovered.length);
    	while (actionCovered[i]) i = (i+1)%actionCovered.length;
    	return i;
    }




} // end Covering




