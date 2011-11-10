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


public class MPEnvironment implements Environment {
/**
 * <p>
 * This is the typical example for a single step problem, the multiplexer.
 * </p>
 */

/**
 * Length of the rule's condition.
 */
    private int condLength; 

/**
 * Current example (the last sampled to the system)
 */
    private double[] currentState; 

/**
 * Class of the current example
 */
	private int classOfCurrentExample;

/**
 * It is the number of bits that indicate the position. In the MUX problem, 
 * some bits may not be valid. For example, in a MUX of 12 
 * bits, 3 bits are for adressing, and 8 bits represent the condition (position bits)
 * So, 1 bit is not valid (it does not influence the output).
 */
    private int positionBits; 




/**
 * It is the constructor of the class. 
 * It initializes the environment.
 *
 */
  public MPEnvironment() {        
	condLength = Config.clLength;
      
	double i = 0;
	for(i=1.0; (int) i+Math.pow(2.,i)<=condLength; i++);//calculates the position bits in this problem
	positionBits=(int)(i-1);
	
	currentState = new double[Config.clLength];
	initRepresentationParameters();
  } // end MPEnvironment        



/**
 * The function returns the current state.
 * 
 * @return a double[] with the current state.
 */
    public double[] getCurrentState() {        
        return currentState;
    } // end getCurrentSate        


/**
 * Creates a new example of the problem. 
 * 
 * @return a double[] with the new example.
 */
  public double[] newState() {        
	for (int i=0; i<condLength; i++){
		if (Config.rand() < 0.5)
			currentState[i] = (double) '0';
		else
			currentState[i] = (double) '1';
	}

	classOfCurrentExample = getCurrentClass();	
	return currentState;
  } // end newState     


 /**
 * Return the class of the environmental state. 
 *
 * @return a int with the class associated with the current environmental
 * state.
 */ 
  public int getCurrentClass(){
	int position = 0;
	for (int i=0; i<positionBits; i++){
		if (currentState[i] == (double)'1')
			position += (int)Math.pow(2.0, positionBits -i -1);
	}

	return ((int)currentState[position + positionBits] - (int)'0');
  } //end getEnvironmentClass
 
 
/**
 * It initializes the first example. It is used in the file 
 * environment to get the examples sequentially.
 */
    public void beginSequentialExamples(){}
    
/**
 * It returns the new Example of a single step file environment.
 */ 
    public double[] getSequentialState(){return newState();}
    
/**
 * It returns the number of examples of the database. 
 */    
    public int getNumberOfExamples(){return (int) Math.pow(2.0, condLength );}

 
/**
 * <p>
 * Initializes the representation parameters of the enviornment
 * </p>
 */
  private void initRepresentationParameters(){
	// When reading the descriptor it will be modified if there is a real or integer attribute
	Config.ternaryRep = true;

	// Type of representation
	Config.typeOfAttributes = new String [ condLength ];	
	for ( int i=0; i<condLength; i++ )
		Config.typeOfAttributes[i]  = "ternary";

	// The  number of actions will be updated while reading the file.
	Config.numberOfActions = 2;
	Config.charVector = new char[3];
	Config.charVector[0] = '0';
	Config.charVector[1] = '1';
	Config.charVector[2] = '#';
	Config.numberOfCharacters = 3;
  }//end initRepresentationParameters

} // END OF CLASS MPEnvironment




