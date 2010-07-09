/** 
* <p> 
* @author Written by Luciano Sanchez (University of Oviedo) 20/01/2004 
* @author Modified by J.R. Villar (University of Oviedo) 19/12/2008
* @version 1.0 
* @since JDK1.4 
* </p> 
*/ 
package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Algorithms;
import keel.Algorithms.Shared.Exceptions.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Individual.*;


public abstract class GeneticAlgorithm {
/** 
* <p> 
* GeneticAlgorithm is the base clase for all genetic algorithm and programming
* algorithms, including the simulate annealing based one. This class
* is inherit by the following classes: {@link AlgorithmGAPGen}, 
* {@link AlgorithmGAPNiches}, {@link AlgorithmGAPSteady}, 
* {@link GeneticAlgorithmGenerational}, {@link GeneticAlgorithmSteady} and
* {@link SimulatedAnnealing}.
* </p> 
*/ 

/** 
* <p> 
* abstract method for evolving the algorithm for a given number of iterations.
* </p> 
* @param MAXITER an integer with the number of iterations torun in the evolucion
* @return the best {@link GeneticIndividual} found
* @throws {@link invalidCrossover}, {@link invalidMutation} of {@link invalidOptim}
*         in case of unsupported crossover, mutation or local optimization operations.
*/ 	
    abstract public GeneticIndividual evolve(int MAXITER) throws invalidCrossover, invalidMutation, invalidOptim;
}

