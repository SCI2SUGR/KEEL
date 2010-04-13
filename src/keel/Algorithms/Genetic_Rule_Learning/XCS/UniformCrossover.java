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

public class UniformCrossover implements Crossover{
/**
 * <p>
 * Introduce a new crossover model, implements Crossover.
 * </p>
 */
	
  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Creates an object of the class. It does not have to initialize anything.
 * </p>
 */
  public  UniformCrossover() {        
  } // end UniformCrossover        

/**
 * <p>
 * Applies crossover according to the uniform crossover operator. It obtains two children.
 * </p>
 * @param parent1 is the first parent.
 * @param parent2 is the second parent
 * @param child1 is the first child
 * @param child2 is the second child.
 */
  public void makeCrossover(Classifier parent1, Classifier parent2, Classifier child1, Classifier child2) {        
    double aleat = 0;
    
    for (int i=0; i<Config.clLength; i++){
        aleat = Config.rand();
        if (aleat > 0.5){
            if (Config.ternaryRep && Config.permitWithinCrossover){
                child1.crossAllele(i,parent1,parent2);
                child2.crossAllele(i,parent2,parent1);
            }
            else{
                child1.setAllele(i,parent2);
                child2.setAllele(i,parent1);
            }
        }
    }	
  } // end makeCrossover        

} // end UniformCrossover



