/**
 * <p>
 * @author Written by Albert Orriols (La Salle University Ramón Lull, Barcelona)  28/03/2004
 * @author Modified by Xavi Solé (La Salle University Ramón Lull, Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Genetic_Rule_Learning.UCS;

import java.util.*;
import java.lang.*;
import java.io.*;


public class TFreeMutation implements TernaryMutation {
/**
 * <p>
 * This class implements the free mutation. So, a gene can be changed for
 * any valid value in the representation.
 * </p>
 */
	
  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Mutates the character.
 * </p>
 * <p>
 * 
 * @param pos is the value of the classifier allele.
 * </p>
 * <p>
 * @param currentState is the value of that classifier position in the
 * environment. It in needed for the niched mutation.
 * </p>
 * <p>
 * @return a char with the mutated character
 * </p>
 */
    public char mutate(char pos, char currentState) {        
        char newPos = ' ';
        if (Config.rand() < Config.pM){
        	do{
        		newPos = Config.charVector[((int)(Config.numberOfCharacters * Config.rand())) % Config.numberOfCharacters];
        	}while (pos == newPos);
		return newPos;
	}
        return pos;
    } // end mutate        

} // end TFreeMutation



