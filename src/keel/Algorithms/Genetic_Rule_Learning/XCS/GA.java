/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

/**
 * <p>
 * @author Written by Albert Orriols (La Salle, Ramón Llull University - Barcelona)  28/03/2004
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


public class GA {
/**
 * <p>
 * This is the main class of the genetic algorithm. It has an object of
 * crossover and selection, that can take different selection and crossover
 * models.
 * </p>
 */

    ///////////////////////////////////////
   // associations

/**
 * <p>
 * A reference to a crossover object
 * </p>
 */
    private Crossover cross; 

/**
 * <p>
 * A reference to a selection object
 * </p>
 */
    private Selection sel; 


  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Constructs the GA
 * </p>
 */
  public  GA() {        
    if (Config.typeOfSelection.toUpperCase().equals("RWS")){
        sel = new RouletteSelection();
    }
    else{
        sel = new TournamentSelection();
    }
    if (Config.typeOfCrossover.toUpperCase().equals("2PT")){
        cross = new TwoPointCrossover();
    }
    else{
        cross = new UniformCrossover();
    }
  } // end GA        

  
  
/**
 * <p>
 * It makes an iteration of the genetic algorithm. It selects two parents,
 * performs crossover, mutate the children and introduces them to the
 * population, accordingly with the doGASubsumption parameter.
 * </p>
 * @param actionSet is the action set where the GA has to be applied.
 * @param envState is the environmental state.
 */
  public void runGA(double[] envState, Population actionSet, int tStamp) {        
    //Declaring the two childs
    Classifier child1, child2;
    //Initializing the selection method and selecting the parents.
    sel.init(actionSet);
    Classifier parent1 = sel.makeSelection(actionSet);
    Classifier parent2 = sel.makeSelection(actionSet);

    // Decision of making crossover or not
    if (Config.rand() < Config.pX){
        // It makes the reductions specified in prediction error and fitness, and set the prediction at the
        // average of the parents
        //child1 = new Classifier(parent1,  (parent1.getPrediction() + parent2.getPrediction())/2.0,  Config.predictionErrorReduction * (parent1.getPredError() + parent2.getPredError())/2.0 ,  Config.fitReduction * (parent1.getFitness()/parent1.getNumerosity() + parent2.getFitness()/parent2.getNumerosity()) /2.,  tStamp,  parent1.getASize(),  0,  1);
        //child2 = new Classifier(parent2,  child1.getPrediction(),  child1.getPredError(),  child1.getFitness(), tStamp, parent2.getASize(),  0,  1);
        child1 = new Classifier (parent1, parent2, tStamp);
        child2 = new Classifier (parent2, parent1, tStamp);
        
        //Applies crossover (generates two children).
        cross.makeCrossover(parent1,parent2,child1,child2);
    }
    else{ //Crossover will not be applied
        child1 = new Classifier(parent1, tStamp);
        child2 = new Classifier(parent2, tStamp);
    }


    // The children are mutated.
    boolean actionMutated1 = child1.mutate(envState);
    boolean actionMutated2 = child2.mutate(envState);

    child1.calculateGenerality();
    child2.calculateGenerality();

    // The children have to be introduced in the population
    if (Config.doGASubsumption){
        // We want to get a reference to the initial population, and we are in the action set.
        doGASubsumption(actionSet,parent1,parent2,child1,child2,actionMutated1,actionMutated2,envState);
    }
    else{ // We do not do GA subsumption
        Population parentRef = actionSet.getParentRef().getParentRef();
        
        if ( !actionMutated1 & child1.match(envState) ) //It can be inserted in the action set to
            parentRef.insertInPopulation(child1,actionSet); //The new classifier is inserted in the action set.
        else
            parentRef.insertInPopulation(child1,null);

        if (!actionMutated2 & child2.match(envState))	
            parentRef.insertInPopulation(child2,actionSet); //The second child is inserted in the action set.
        else
            parentRef.insertInPopulation(child2,null);
    }
} // end runGA        




/**
 * <p>
 * It applies the GA subsumption. It has to see if the parents
 * subsume the children. If they do not, it has to search
 * in the population for a subsumer. Otherwise, it has to insert
 * the new classifier in the population.
 * </p>
 * @param actionSet is the action set where the GA subsumption has to be applied. 
 * @param parent1 is the first parent.
 * @param parent2 is the second parent.
 * @param child1 is the first child.
 * @param child2 is the second child.
 * @param actionMutated2 indicates if the action has been mutated
 * @param envState is the environmental state.
 */

  public void doGASubsumption(Population actionSet,Classifier parent1, Classifier parent2, Classifier child1, Classifier child2,boolean actionMutated1, boolean actionMutated2, double[] envState){
   	
    Population parentRef = actionSet.getParentRef().getParentRef();	
    
    //Trying to subsume the first child.
    if (parent1.doesSubsume(child1)){
        parent1.increaseNumerosity(1);
        parentRef.increaseMicroClSum(1);
    
    }else if (parent2.doesSubsume(child1)){
        parent2.increaseNumerosity(1);
        parentRef.increaseMicroClSum(1);
        
    }else   parentRef.insertInPSubsumingCl(child1,null);

    //Making the same process for the second child.
    if (parent1.doesSubsume(child2)){
        parent1.increaseNumerosity(1);
        parentRef.increaseMicroClSum(1);
        
    }else if (parent2.doesSubsume(child2)){
        parent2.increaseNumerosity(1);
        parentRef.increaseMicroClSum(1);
        
    }else   parentRef.insertInPSubsumingCl(child2,null);
    
    //Deleting the extra classifiers in the population.
    while (parentRef.getMicroClSum() > Config.popSize){
        parentRef.deleteClFromPopulation(actionSet);
    }
  } // end doGASubsumption

} // end GA




