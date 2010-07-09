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


public interface IntegerMutation {

   ///////////////////////////////////////
  // associations



  ///////////////////////////////////////
  // operations

/**
 * <p>
 * Mutates the lower real value of the interval.
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
    public int mutateLower(int lowerValue, int upperValue, int currentState, int scale);


/**
 * <p>
 * Mutates the upper real value.
 * 
 * @return a double with the mutated value
 * </p>
 */
    public int mutateUpper(int lowerValue, int upperValue, int currentState, int scale);

} // end IntegerMutation





