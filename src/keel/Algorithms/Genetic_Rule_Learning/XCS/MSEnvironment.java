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
 * @author Modified by Xavi Solé ((La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Genetic_Rule_Learning.XCS;
import  keel.Algorithms.Genetic_Rule_Learning.XCS.KeelParser.Config;
import java.lang.*;
import java.io.*;
import java.util.*;



public class MSEnvironment implements Environment{
/**
 * <p>
 * It is the base class for all the multiple step problems.
 * </p>
 */
  ///////////////////////////////////////
  // attributes


/**
 * <p>
 * Represents the number of actions that the problem can take.
 * </p>
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
    private double[] currentSate; 



/**
 * <p>
 * It indicates if the classification has been performed. In the multiplexer problem 
 * (and single step problems) it is true in every step.
 * 
 */
    private boolean classExecuted; 

  ///////////////////////////////////////
  // operations


/**
 * <p>
 * It is the constructor of the class.
 * </p>
 * 
 */
    public  MSEnvironment() {        
        // your code here
    } // end MSEnvironment        

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
        // your code here
        return false;
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
        // your code here
        return 0.0;
    } // end makeAction        

/**
 * <p>
 * The function returns the current state.
 * </p>
 * <p>
 * 
 * @return a double[] with the current state.
 * </p>
 */
    public double[] getCurrentState() {        
        // your code here
        return null;
    } // end getCurrentState        

/**
 * <p>
 * Create a new state of the problem. XCS has to decide the
 * action to do.
 * </p>
 * <p>
 * 
 * @return a double[] with the new state.
 * </p>
 */
    public double[] newState() {        
        // your code here
        return null;
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
    	return 0.;
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
    	return 0.;	
    }
    
/**
 * <p>
 * Returns if the class has been performed. It is used
 * in multiple step problems. 
 * </p>
 * <p>
 * @return a boolean indicating if the problem has finished.
 * </p>
 */ 
    public boolean isPerformed(){
    	return false;	
    }


/**
 * <p>
 * Return the class of the environmental state. It is
 * used by the UCS (supervised learning). 
 * </p>
 * <p>
 * @return an integer with the class associated with the current environmental
 * state.
 * </p>
 */ 
    public int getEnvironmentClass(){
    	return 0;	
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




} // end MSEnvironment




