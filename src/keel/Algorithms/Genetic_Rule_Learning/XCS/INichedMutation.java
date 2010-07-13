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


public class INichedMutation implements IntegerMutation {
/**
 * <p>
 * This class applies the niched mutation. So, the changes made in the
 * classifier cannot result in another classifier that does not match with the
 * environment (we can change a don't care for the value of the parent, or
 * a value for a don't care value, but, for example, we would never change
 * a '0' for a '1').
 * </p>
 */
  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Mutates the lower real value.
 * </p>
 * @param lowerValue is the current lower value of this classifier position
 * @param upperValue is the current upper value of this classifier position.
 * @param currentState is the current State of the environment for this
 * allele of the classifier.
 * @return a double with the mutated value
 */
 public int mutateLower(int lowerValue, int upperValue, int currentState, int scale) {        
        int des = 0;
        if (Config.rand() < Config.pM){
        	des = (int) ((Config.rand() - 0.5)*2 * Config.m_0* (double)scale);  //We obtain a number between -1 and 1.
        	lowerValue += des;
        }
    	if (lowerValue <= currentState || currentState == -1.){
    		return lowerValue;  
    	}
    	return currentState; 
    } // end mutateLower             

/**
 * <p>
 * Mutates the upper real value.
 * </p>
 * <p>
 * @return a double with the mutated value.
 * </p>
 */
    public int mutateUpper(int lowerValue, int upperValue, int currentState, int scale) {        
        int des = 0;
        if (Config.rand() < Config.pM){
        	des = (int) ((Config.rand() - 0.5)*2 * Config.m_0 * (double)scale);  //We obtain a number between -1 and 1.
        	upperValue += des;
        }
	if (upperValue >= currentState || currentState == -1.){
    		return upperValue;  
    	}
    	else{
    		return currentState;
    	}
    } // end mutateUpper             

} // end INichedMutation




