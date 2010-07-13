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


public class Roulette {
/**
 * <p>
 * This class implements a generic roulette. 
 * </p>
 */
  ///////////////////////////////////////
  // attributes


/**
 * <p>
 * Represents the roulette. In each position it has a probability
 * </p>
 * 
 */
    private double[] roul; 

/**
 * <p>
 * Represents the number of positions completed.
 * </p>
 * 
 */
    private int pos; 

  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Constructs a roulette. It inicializes all its values to 0, and the
 * pointer to the first position. To use it, you have to update all the
 * relative probabilities (not the sum).
 * </p>
 * @param num is the number of the elements in the roulette.
 */
    public  Roulette(int num) {        
        roul = new double[num];
        pos = 0;
        for (int i=0; i<num; i++){
        	roul[i] = 0;
        }
    } // end Roulette        

/**
 * <p>
 * Enter a new probability in the roulette. It has to be a relative
 * probability and not a sum, because it performs the sum internally.
 * </p>
 * @param num is the new probability.
 */
    public void add(double num) {        
        if (pos == roul.length){ 
        	System.err.println ("--> ROULETTE ERROR!! The roulette is full");
        	return;
        }
        if (pos == 0) roul[pos] = num;
        else roul[pos] = roul[pos-1] + num;
        pos++;
    } // end add        

/**
 * <p>
 * Resets the roulette (puts all the probabilities to 0).
 * </p>
 */
    public void reset() {        
        for (int i =0; i<roul.length; i++){
        	roul[i] = 0;
        }
    } // end reset        

/**
 * <p>
 * Select on position of the roulette and returns it.
 * </p>
 * <p>
 * 
 * @return a int with the position selected
 * </p>
 */
    public int selectRoulette() {        
        if (pos == 0) return -1;
        double aleat = Config.rand() * roul[pos-1];
        boolean finish = false;
        int i=0;
       
        while (i<roul.length && !finish){
        	if (aleat <= roul[i]) finish = true;
        	else i++;	
        }
        if (i== roul.length) i--;
        return i;
    } // end selectRoulette        


/**
 * <p>
 * Prints the roulette
 * </p>
 * 
 */
    public void print() {        
        System.out.println ("\nThe probabilities of the roulette are: ");
        for (int i=0; i<pos; i++){
        	System.out.println ("Position "+i+":"+ roul[i]);
        }
    } // end print        



} // end Roulette




