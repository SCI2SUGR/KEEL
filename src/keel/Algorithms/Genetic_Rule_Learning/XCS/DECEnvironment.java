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


public class DECEnvironment extends SSEnvironment {
/**
 * <p>
 * This is the typical example for a single step problem, the multiplexer.
 * </p>
 */
  ///////////////////////////////////////
  // attributes


/**
 * <p>
 * Represents the number of actions that can take the condition.
 * </p>
 * 
 */
    private int numActions; 

/**
 * <p>
 * Represents the length of the condition.
 * </p>
 * 
 */
    private int condLength; 

/**
 * <p>
 * Indicates if the classification has been correct.
 * </p>
 * 
 */
    private boolean isCorrect; 

/**
 * <p>
 * Represents the maximum payoff that a classifier can get.
 * </p>
 * 
 */
    private double maxPayOff; 

/**
 * <p>
 * Represents the minimum payoff that a classifier can get.
 * </p>
 * 
 */
    private double minPayOff; 

/**
 * <p>
 * Represents the current state of the environment.
 * </p>
 * 
 */
    private double[] currentState; 


/**
 * <p>
 * It indicates if the classification has been executed. In the multiplexer problem it's true
 * in every step.
 * 
 */
    private boolean classExecuted; 


  ///////////////////////////////////////
  // operations


/**
 * <p>
 * It is the constructor of the class. It initializes the environment.
 * </p>
 */
    public DECEnvironment() {        
        //super();
        condLength = Config.clLength;
        	
	classExecuted = false;
	isCorrect = false;
	currentState = new double[Config.clLength];
	maxPayOff = 1000;
	minPayOff = 0;
	initRepresentationParameters();
    } // end MPEnvironment        

/**
 * <p>
 * Determines if the classification performed was good
 * </p>
 * <p>
 * 
 * @return a boolean that indicates if the last classifications is good.
 * </p>
 */
    public boolean wasCorrect() {        
        return isCorrect;
    } // end wasCorrect        

/**
 * <p>
 * This function returns the reward given when applying the action in the
 * environment.
 * </p>
 * @return a double with the reward
 * @param action is the action chosen to do.
 */
    public double makeAction(int action) {        
        int value = 0;
        for(int i=0; i<condLength; i++){
	    if((char)currentState[i]=='1'){
		value += (int)Math.pow(2., (double)(condLength-1-i));
	    }
	}
	
	
	if (action == value){ 
		isCorrect = true;
		classExecuted = true;
		return maxPayOff;
	}
	else{
		isCorrect = false;
		classExecuted = true;
		return minPayOff;
	
	}
	
    } // end makeAction        

/**
 * <p>
 * The function returns the current state.
 * </p>
 * <p>
 * 
 * @return a float[] with the current state.
 * </p>
 */
    public double[] getCurrentState() {        
        return currentState;
    } // end getCurrentSate        


/**
 * <p>
 * Creates a new state of the problem. The XCS have to decide the
 * action to do.
 * </p>
 * <p>
 * 
 * @return a float[] with the new situation.
 * </p>
 */
    public double[] newState() {        
        for (int i=0; i<condLength; i++){
        	if (Config.rand() <= 0.5)
        		currentState[i] = (double) '0';
        	else
        		currentState[i] = (double) '1';
        }
        classExecuted = false;
        return currentState;
    } // end newState     


/**
 * <p>
 * Returns the environment maximum payoff
 * </p>
 * <p>
 * @return a double with the environment maximum payoff.
 * </p>
 */
    public double getMaxPayoff(){
    	return maxPayOff;
    }
    
/**
 * <p>
 * Returns the environment minimum payoff
 * </p>
 * <p>
 * @return a double with the environment minimum payoff.
 * </p>
 */
    public double getMinPayoff(){
    	return minPayOff;	
    }
    
/**
 * <p>
 * Returns if the class has been performed. It is used
 * in the multiple step problems. 
 * </p>
 * <p>
 * @return a boolean indicating if the problem has finished.
 * </p>
 */ 
    public boolean isPerformed(){
    	return classExecuted;	
    } 
 


 /**
 * <p>
 * Returns the class of the environmental state. It is
 * used by UCS (supervised learning). 
 * </p>
 * <p>
 * @return an integer with the class associated to the current environmental
 * state.
 * </p>
 */ 
    public int getEnvironmentClass(){
    	int value = 0;
        for(int i=0; i<condLength; i++){
	    if((char)currentState[i]=='1'){
		value += (int)Math.pow(2., (double)(condLength-1-i));
	    }
	}
	return value;
    } //end getClass

 


/**
 * <p>
 * It initializes the first example. It is used in the file 
 * environment to get the examples sequentially.
 * </p>
 */
    public void beginSequentialExamples(){}
    
/**
 * <p>
 * It returns the new Example of a single step file environment.
 * </p>
 */ 
    public double[] getSequentialState(){return null;}
    
/**
 * <p>
 * It returns the number of examples of the database. It is only
 * used in the file environments. 
 * </p>
 */    
    public int getNumberOfExamples(){return 0;}
    
/**
 * <p>
 * It deletes the examples of the database that match with the 
 * given classifier. It is only used in the file environment. 
 * </p>
 */    
    public void deleteMatchedExamples(Classifier cl){}

/**
 * <p>
 * Initializes the representation parameters of the enviornment
 * </p>
 */
    private void initRepresentationParameters(){
	// When reading the descriptor it will be modified if there is a real or integer attribute
	Config.ternaryRep = true;
	// The  number of actions will be updated while reading the file.
	Config.numberOfActions = (int) Math.pow(2., (double) Config.clLength);
	
	Config.charVector = new char[3];
	Config.charVector[0] = '0';
	Config.charVector[1] = '1';
	Config.charVector[2] = '#';
	Config.numberOfCharacters = 3;
   }



} // end DECEnvironment




