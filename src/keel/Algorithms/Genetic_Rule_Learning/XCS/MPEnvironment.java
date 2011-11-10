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


public class MPEnvironment extends SSEnvironment {
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
 * It is the number of bits that indicate the position. In the MUX problem, 
 * some bits may not be valid. For example, in a MUX of 12 
 * bits, 3 bits are for adressing, and 8 bits represent the condition (position bits)
 * So, 1 bit is not valid (it does not influence the output).
 * </p>
 * 
 */
    private int positionBits; 


/**
 * <p>
 * It indicates if the classification has been performed. In mux problem it's true
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
    public MPEnvironment() {        
        //super();
        condLength = Config.clLength;
      
     	double i = 0;
        for(i=1.0; (int) i+Math.pow(2.,i)<=condLength; i++);//calculates the position bits in this problem
		positionBits=(int)(i-1);
	
	
	classExecuted = false;
	isCorrect = false;
	currentState = new double[Config.clLength];
	maxPayOff = 1000;
	minPayOff = 0;
	System.out.println ("Number of bits position: "+positionBits);
	initRepresentationParameters();
    
    } // end MPEnvironment        

/**
 * <p>
 * Determines if the classification was good
 * </p>
 * <p>
 * 
 * @return a boolean that indicates if the last classification was good.
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
 * <p>
 * 
 * @return a double with the reward
 * </p>
 * <p>
 * @param action is the action chosen to do.
 * </p>
 */
    public double makeAction(int action) {        
        int position = 0;
        for (int i=0; i<positionBits; i++){
        	if (currentState[i] == (double)'1')
        		position += (int)Math.pow(2.0, positionBits -i -1);
	}

	if ( (int)(currentState[position+positionBits] - (double)'0') == action){
		isCorrect = true;
		classExecuted = true;
		return maxPayOff;
	}
		isCorrect = false;
		classExecuted = true;
		return minPayOff;
    
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
 * Creates a new state of the problem. XCS has to decide the action to do.
 * </p>
 * <p>
 * 
 * @return a float[] with the new state.
 * </p>
 */
    public double[] newState() {        
        for (int i=0; i<condLength; i++){
        	if (Config.rand() < 0.5)
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
 * Returns if the action has been performed. It is used
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
 * Return the class of the environmental state. It's used by UCS
 * (in supervised learning). 
 * </p>
 * <p>
 * @return a int with the class associated with the current environmental
 * state.
 * </p>
 */ 
    public int getEnvironmentClass(){
    	int position = 0;
        for (int i=0; i<positionBits; i++){
        	if (currentState[i] == (double)'1')
        		position += (int)Math.pow(2.0, positionBits -i -1);
	}
	
	
	return ((int)currentState[position + positionBits] - (int)'0');
    } //end getEnvironmentClass
 
 
 
 
 
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
 * It returns the number of examples of the database. It's only
 * used in the file environments. 
 * </p>
 */    
    public int getNumberOfExamples(){return 0;}
    
/**
 * <p>
 * It deletes the examples of the database that match with de 
 * classifier given as a parameter. It's only used in the file environment. 
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
	Config.numberOfActions = 2;
	
	Config.charVector = new char[3];
	Config.charVector[0] = '0';
	Config.charVector[1] = '1';
	Config.charVector[2] = '#';
	Config.numberOfCharacters = 3;
   }

 
} // end MPEnvironment




