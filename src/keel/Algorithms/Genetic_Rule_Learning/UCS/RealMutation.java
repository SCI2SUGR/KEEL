/**
 * <p>
 * @author Written by Albert Orriols (La Salle University Ramón Lull, Barcelona)  28/03/2004
 * @author Modified by Xavi Solé (La Salle University Ramón Lull) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Genetic_Rule_Learning.UCS;

import java.util.*;
import java.lang.*;
import java.io.*;


public interface RealMutation {

   ///////////////////////////////////////
  // associations



  ///////////////////////////////////////
  // operations

/**
 * <p>
 * Mutates the lower real value.
 * </p>
 * <p>
 * 
 * @param lowerValue is the current lower value of this classifier position
 * </p>
 * <p>
 * @param upperValue is the current upper value of this classifier position.
 * </p>
 * <p>
 * @param currentState is the current State of the environment for this
 * allele of the classifier.
 * </p>
 * <p>
 * @return a double with the mutated value
 * </p>
 */
    public double mutateLower(double lowerValue, double upperValue, double currentState);
/**
 * <p>
 * Mutates the upper real value.
 * </p>
 * <p>
 * 
 * </p>
 * <p>
 * 
 * @return a double with ...
 * </p>
 */
    public double mutateUpper(double lowerValue, double upperValue, double currentState);

} // end RealMutation





