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
import java.util.*;


public class Specify {
/**
 * <p>
 * The class implement the specify operator proposed by Lanzi
 * </p>
 */

  ///////////////////////////////////////
  // operations


/**
  * <p> It's the constructor of the class
  *
  */
  
  Specify(){}


/**
 * <p>
 * It applies the specify operator to the population. If the average
 * prediction error of the action set is twice larger than the average
 * prediction error of the population and the classifiers in the action set
 * have been updated Nsp times (Config), then, a classifier is
 * selected randomly from the action set (with probability proportional to
 * its prediction error), and its don't care caracters are replaced with a
 * probability Psp (Config) with the corresponding digit in the
 * system input.
 * </p>
 * <p>
 * 
 * @param pop is the Population
 * </p>
 * <p>
 * @param actionSet is the action set of that iteration.
 * </p>
 * <p>
 * @param envState is the environmental state (the input).
 * </p>
 */
  public void makeSpecify(Population pop, Population actionSet, double[] envState, int tStamp) {        
    int i=0;
    
	if ( actionSet.getPredErrorAverage() >= 2. * pop.getPredErrorAverage() && actionSet.getExperienceAverage() > Config.Nspecify){
		Roulette rul = new Roulette(actionSet.getMacroClSum());
		
		for (i=0; i<actionSet.getMacroClSum(); i++){
			rul.add (actionSet.getClassifier(i).getPredError() * actionSet.getClassifier(i).getNumerosity());
		}
		
		i = rul.selectRoulette();
		
		Classifier cl = actionSet.getClassifier(i); // The classifier with the bigest prediction error is get.
		cl.setPredError (cl.getPredError() * Config.predictionErrorReduction);
		Classifier clOffspring = new Classifier(cl, tStamp); //Creates a copy of the classifier.
		clOffspring.makeSpecify (envState); // It changes all don't care symbols with Psp probability.	
	
		clOffspring.calculateGenerality();
	
		if (clOffspring.match(envState))  //If the new classifier matches with the environment.
			pop.insertInPopulation(clOffspring,actionSet);  //It inserts the new classifier in the population and deletes one if there isn't space enough.
		else
			pop.insertInPopulation(clOffspring,null);

	} //else the specify operator has not to be applied    
  } // end doSpecify        

} // end Specify



