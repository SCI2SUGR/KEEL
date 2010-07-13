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


public class RMPEnvironment extends SSEnvironment {
/**
 * <p>
 * RMPEnvironment. It's the environment for Real Multiplexer Environment
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
 * The number of bits that indicate the position. 
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
 * It's the constructor of the class. It initializes the environment.
 * </p>
 * <p>
 */
    public RMPEnvironment() {        
        //super();
        condLength = Config.clLength;
        numActions = 2;
     	double i = 0;
        for(i=1.0; (int) i+Math.pow(2.,i)<=condLength; i++);//calculates the position bits in this problem
		positionBits=(int)(i-1);
	
	classExecuted = false;
	isCorrect = false;
	currentState = new double[condLength];
	maxPayOff = 1000;
	minPayOff = 0;
	System.out.println ("The bits number of the position is: "+positionBits);
    
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
 * This function returns the reward given when applying the action to the
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
        double value;
        for (int i=0; i<positionBits; i++){
        	if (currentState[positionBits-i-1] >= 0.5) 
        		position += (int)Math.pow(2.0, positionBits -1 -i);
	}
	
	int act = 0;
	if (currentState[position] >= 0.5) act = 1;
		
	if ( act == action){
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
 * Creates a new state of the problem. XCS has to decide the
 * action to do.
 * </p>
 * <p>
 * 
 * @return a float[] with the new situation.
 * </p>
 */
    public double[] newState() {        
        for (int i=0; i<condLength; i++){
        	currentState[i] = Config.rand();;
        	
        }
        classExecuted = false;
        return currentState;
    } // end newState     



 /**
 * <p>
 * Returns the class of the environmental state. It is
 * used by UCS (supervised learning). 
 * </p>
 * <p>
 * @return an integer with the class associated with the current environmental
 * state.
 * </p>
 */ 
    public int getEnvironmentClass(){
    	int position = 0;
        double value;
        for (int i=0; i<positionBits; i++){
        	if (currentState[positionBits-i-1] >= 0.5) 
        		position += (int)Math.pow(2.0, positionBits -1 -i);
	}
	
	int act = 0;
	if (currentState[position] >= 0.5) act = 1;
		
	return act;
    } //end getClass


} // end RMPEnvironment




