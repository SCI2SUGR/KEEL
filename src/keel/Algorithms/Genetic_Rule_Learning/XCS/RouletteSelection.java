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


public class RouletteSelection implements Selection{
/**
 * <p>
 * This class implements Selection roulette. 
 * </p>
 */ 
	
  ///////////////////////////////////////
  // attributes


/**
 * <p>
 * It's a roulette object.
 * </p>
 * 
 */
    private Roulette roul; 
    
  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Creates a RouletteSelection object.
 * </p>
 * 
 */
    public  RouletteSelection() {        
    	roul = null;
    } // end RouletteSelection        


/**
 * <p>
 * It creates and initializes the roulette with the fitness of all
 * classifiers in the population.
 * </p>
 * <p>
 * 
 * @param pop is the action set where the selection has to be applied.
 * </p>
 * <p>
 * @see Roulette
 * </p>
 */
    public void init(Population pop) {        
      	int i = 0;
  	double lowerFitness=0;
  	roul = new Roulette (pop.getMacroClSum());      
        for (i=0; i<pop.getMacroClSum(); i++){
        	roul.add(pop.getClassifier(i).getFitness() /*- lowerFitness*/);
	}
    } // end init        



/**
 * <p>
 * Performs the roulette wheel selection
 * </p>
 * <p>
 * @param pop is the population.
 * </p>
 * <p>
 * @return a Classifier with the selected classifier
 * </p>
 */
    public Classifier makeSelection(Population pop) {        
        
        int i = roul.selectRoulette();
        
        return pop.getClassifier(i);
    } // end makeSelection        



} // end RouletteSelection



