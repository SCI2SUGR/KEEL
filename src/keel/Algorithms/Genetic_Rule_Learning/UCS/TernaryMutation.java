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

public interface TernaryMutation {

   ///////////////////////////////////////
  // associations



  ///////////////////////////////////////
  // operations

/**
 * <p>
 * Mutates the character.
 * </p>
 * <p>
 * 
 * </p>
 * <p>
 * 
 * @return a char with the mutated character.
 * </p>
 */
    public char mutate(char pos, char currentState);

} // end TernaryMutation





