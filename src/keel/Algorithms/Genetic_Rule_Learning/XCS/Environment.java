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



public interface Environment {
/**
 * <p>
 * This interface is the environment interface. It has to be implemented for
 * all the environment problems. At first, it is extended in three classes, the
 * single step environment problems (SSEnvirontment), the single step
 * environment problems that have to be read from a file
 * (SSFileEnvironment), an the multiple step environment problems
 * (MSEnvironment). To implement a new problem environment it's necessary
 * to extend one of these three classes, defining all their functions.
 * </p>
 */
   ///////////////////////////////////////
  // associations



  ///////////////////////////////////////
  // operations

/**
 * <p>
 * Determines if the classification was good
 * </p>
 * @return a boolean that indicates if the last classification was good.
 */
    public boolean wasCorrect();

/**
 * <p>
 * This function returns the reward given when applying the action in the
 * environment.
 * </p>
 * @return a double with the reward
 * @param action is the action chosen to do.
 */
    public double makeAction(int action);

/**
 * <p>
 * The function returns the current state.
 * </p>
 * @return a double[] with the current state.
 */
    public double[] getCurrentState();

/**
 * <p>
 * Creates a new state of the problem. XCS has to decide the
 * action to do.
 * </p>
 * @return a double[] with the new situation.
 */
    public double[] newState();

/**
 * <p>
 * Returns the environment maximum payoff
 * </p>
 * @return a double with the environment maximum payoff.
 */
    public double getMaxPayoff();

/**
 * <p>
 * Returns the environment minimum payoff
 * </p>
 * @return a double with the environment minimum payoff.
 */
    public double getMinPayoff();
    
/**
 * <p>
 * Returns if the class has been performed. It is used
 * in the multiple step problems. 
 * </p>
 * @return a boolean indicating if the problem has finished.
 */ 
    public boolean isPerformed();
	

 /**
 * <p>
 * Returns the class of the environmental state. It is
 * used by UCS (supervised learning). 
 * </p>
 * @return an integer with the class associated to the current environmental state.
 */ 
    public int getEnvironmentClass();

/**
 * <p>
 * It initializes the first example. It is used in the file 
 * environment to get the examples sequentially.
 * </p>
 */
    public void beginSequentialExamples();
    
/**
 * <p>
 * It returns the new Example of a single step file environment.
 * </p>
 */ 
    public double[] getSequentialState();
    
/**
 * <p>
 * It returns the number of examples of the database. It is only
 * used in the file environments. 
 * </p>
 */    
    public int getNumberOfExamples();
    
/**
 * <p>
 * It deletes the examples of the database that match with the 
 * given classifier. It is only used in the file enviornment. 
 * </p>
 */    
    public void deleteMatchedExamples(Classifier cl);
	
} // end Environment






