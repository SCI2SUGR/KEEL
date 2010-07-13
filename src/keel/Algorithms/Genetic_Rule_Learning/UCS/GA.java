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
 * @author Written by Albert Orriols (La Salle University Ramón Lull, Barcelona)  28/03/2004
 * @author Modified by Xavi Solé (La Salle University Ramón Lull, Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Genetic_Rule_Learning.UCS;

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

/**
 * A reference to a crossover object
 */
    private Crossover cross; 

/**
 * A reference to a selection object
 */
    private Selection sel; 




/**
 * Constructs the GA
 */
  public  GA() {        
    if (Config.typeOfSelection.toUpperCase().equals("RWS"))
        sel = new RouletteSelection();
    else
        sel = new TournamentSelection();

    if (Config.typeOfCrossover.toUpperCase().equals("2PT"))
        cross = new TwoPointCrossover();
    else
        cross = new UniformCrossover();
  
  } // end GA        

  
  
/**
 * It makes an iteration of the genetic algorithm. It selects two parents,
 * performs crossover, mutate the children and introduces them to the
 * population, accordingly with the doGASubsumption parameter.
 * 
 * @param correctSet is the action set where the GA has to be applied.
 * @param envState is the environmental state.
 * @param classOfExample is the class of the input example
 * @param tStamp is the time stamp
 */
  public void runGA( Population correctSet, double[] envState, int classOfExample, int tStamp ) {        
    //Declaring the two childs
    Classifier child1, child2;

    //Initializing the selection method and selecting the parents.
    sel.init( correctSet );
    Classifier parent1 = sel.makeSelection( correctSet );
    Classifier parent2 = sel.makeSelection( correctSet );

    // Decision of making crossover or not
    if (Config.rand() < Config.pX){
        child1 = new Classifier (parent1, parent2, tStamp);
        child2 = new Classifier (parent2, parent1, tStamp);
        
        //Applies crossover (generates two children).
        cross.makeCrossover(parent1,parent2,child1,child2);
    }
    else{ //Crossover will not be applied. Offspring are copies of parents
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
        doGASubsumption( correctSet, parent1, parent2, child1, child2, actionMutated1, actionMutated2, envState );
    }
    else{ // We do not do GA subsumption
        Population parentRef = correctSet.getParentRef().getParentRef();
        
        if ( !actionMutated1 & child1.match(envState) ) //It can be inserted in the action set to
            parentRef.insertInPopulation(child1,correctSet); //The new classifier is inserted in the action set.
        else
            parentRef.insertInPopulation(child1,null);

        if (!actionMutated2 & child2.match(envState))	
            parentRef.insertInPopulation(child2,correctSet); //The second child is inserted in the action set.
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
 * @param correctSet is the action set where the GA subsumption has to be applied. 
 * @param parent1 is the first parent.
 * @param parent2 is the second parent.
 * @param child1 is the first child.
 * @param child2 is the second child.
 * @param actionMutated2 indicates if the action has been mutated
 * @param envState is the environmental state.
 */

  public void doGASubsumption( Population correctSet, Classifier parent1, Classifier parent2, 
									Classifier child1, Classifier child2,boolean actionMutated1, 
										boolean actionMutated2, double[] envState){
   	
    Population parentRef = correctSet.getParentRef().getParentRef();	
    
    //Trying to subsume the first child.
    if (parent1.doesSubsume(child1)){
        parent1.increaseNumerosity(1);
        parentRef.increaseMicroClSum(1);
    
    }else if (parent2.doesSubsume(child1)){
        parent2.increaseNumerosity(1);
        parentRef.increaseMicroClSum(1);
        
    }else{   
		parentRef.insertInPSubsumingCl( child1 );
		//parentRef.insertInPopulation( child1, null );
	}

    //Making the same process for the second child.
    if (parent2.doesSubsume(child2)){
        parent2.increaseNumerosity(1);
        parentRef.increaseMicroClSum(1);
        
    }else if (parent1.doesSubsume(child2)){
        parent1.increaseNumerosity(1);
        parentRef.increaseMicroClSum(1);
        
    }else{   
		parentRef.insertInPSubsumingCl( child2 );
		//parentRef.insertInPopulation( child2, null );
	}
    
    //Deleting the extra classifiers in the population.
    while ( parentRef.getMicroClSum() > Config.popSize ){
        parentRef.deleteClFromPopulation( correctSet );
    }
  } // end doGASubsumption

} // end GA




