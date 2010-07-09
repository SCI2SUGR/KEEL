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


public class RFreeMutation implements RealMutation {
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
    public double mutateLower(double lowerValue, double upperValue, double currentState) {        
        double des = 0.0;
        if (Config.rand() < Config.pM){
        	des = (Config.rand() - 0.5)*2 * Config.m_0;  //We obtain a number between -1 and 1.
        	lowerValue += des;
        }
    	return lowerValue;  
    } // end mutateLower        

/**
 * <p>
 * Mutates the upper real value.
 * </p>
 * <p>
 * 
 * </p>
 * <p>
 * 
 * @return a double with the mutated value.
 * </p>
 */
    public double mutateUpper(double lowerValue, double upperValue, double currentState) {        
        double des = 0.0;
        if (Config.rand() < Config.pM){
        	des = (Config.rand() - 0.5)*2 * Config.m_0;  //We obtain a number between -1 and 1.
        	upperValue += des;
        }
    	return upperValue;  
    } // end mutateUpper        

} // end RFreeMutation



