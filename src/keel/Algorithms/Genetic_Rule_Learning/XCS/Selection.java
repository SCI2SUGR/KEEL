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


public interface Selection {
/**
 * <p>
 * It is the interface for Selection method. To implement a new selection model,
 * you only have to implement this interface.
 * </p>
 */
   ///////////////////////////////////////
  // associations



  ///////////////////////////////////////
  // operations

/**
 * <p>
 * Does make the random selection of a classifier.
 * </p>
 * <p>
 * @param pop is the population.
 * </p>
 * <p>
 * @return a Classifier with the selected classifier
 * </p>
 */
    public Classifier makeSelection(Population pop);

/**
 * <p>
 * Initializes the system for selection.
 * </p>
 * <p>
 * 
 * @param pop is the population
 * </p>
 */
    public void init(Population pop);

} // end Selection





