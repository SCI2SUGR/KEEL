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


public interface Reduction {
/**
 * <p>
 * This is the interface for all the reduction methods classes in the XCS.
 * By now, the Wilson Reduction and the Dixon Reduction (in two versions,
 * the strict one and the weak one) classes implement this interface. To
 * make other reduction methods, you only have to implement this class.
 * </p>
 */
  ///////////////////////////////////////
  // operations

/**
 * <p>
 * Makes a reduction of the population applying the chosen method by
 * the user (with the configuration file). You can implement new reduction
 * strategies implenting this interface.
 * </p>
 * <p>
 * 
 * @return a Population with the reducted population. So, the inital
 * population is not modified.
 * </p>
 * <p>
 * @param pop is the population that has to be reduced.
 * </p>
 * <p>
 * @param env is the environment that will be used to get the performance
 * of classifiers.
 * </p>
 */
    public Population makeReduction(Population pop, Environment env);

} // end Reduction





