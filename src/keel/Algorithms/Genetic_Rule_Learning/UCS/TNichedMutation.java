
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


public class TNichedMutation implements TernaryMutation{
/**
 * <p>
 * This class applies the niched mutation. So, the changes made in the
 * classifier cannot result in another classifier that does not match with the
 * environment (we can change a don't care for the value of the parent, or
 * a value for a don't care value, but, for example, we would never changer
 * a '0' for a '1').
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
 * @return a char with the character mutated
 * </p>
 */
    public char mutate(char pos, char currentState) {        
	
        if (Config.rand() < Config.pM){
        	if (pos == Config.dontCareSymbol){
        		if (currentState == (char)-1)	pos = Config.charVector[(int) (Config.rand() * (double)Config.charVector.length)];
        		else pos = currentState;
        	}
        	else{
        		pos = Config.dontCareSymbol;
        	}
	}
        return pos;
    } // end mutate        

} // end TNichedMutation



