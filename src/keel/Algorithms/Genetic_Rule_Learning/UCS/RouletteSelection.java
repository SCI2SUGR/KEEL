/**
 * <p>
 * @author Written by Albert Orriols (La Salle University Ramón Lull, Barcelona)  28/03/2004
 * @author Modified by Xavi Solé (La Salle University Ramón Lull, Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Genetic_Rule_Learning.UCS;

import java.lang.*;
import java.io.*;
import java.util.*;


public class RouletteSelection implements Selection{
/**
 * <p>
 * This class implements Roulette Selection. 
 * </p>
 */

/**
 * It's a roulette object.
 */
    private Roulette roul; 
    



/**
 * Creates a RouletteSelection object.
 */
  public  RouletteSelection() {        
	roul = null;
  } // end RouletteSelection        


/**
 * It creates and initializes the roulette with the fitness of all
 * classifiers in the population.
 * 
 * @param pop is the action set where the selection has to be applied.
 * @see Roulette
 */ 
  public void init(Population pop) {        
	int i = 0;
	double lowerFitness=0;
	roul = new Roulette (pop.getMacroClSum());      
	for (i=0; i<pop.getMacroClSum(); i++){
		roul.add( pop.getClassifier(i).getMacroClFitness() );
	}
  } // end init        



/**
 * Performs the roulette wheel selection
 * 
 * @param pop is the population.
 * @return a Classifier with the selected classifier
 */
  public Classifier makeSelection(Population pop) {        
	int i = roul.selectRoulette();
	return pop.getClassifier(i);
  } // end makeSelection        


} // end RouletteSelection



