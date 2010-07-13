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
* @author Written by Luciano Sanchez (University of Oviedo) 20/01/2004 
* @author Modified by J.R. Villar (University of Oviedo) 19/12/2008
* @version 1.0 
* @since JDK1.4 
* </p> 
*/ 

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Algorithms;
import org.core.*;
import keel.Algorithms.Shared.Exceptions.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Individual.*;


public class GeneticAlgorithmGenerational extends GeneticAlgorithm {
/** 
* <p> 
* GeneticAlgorithmGenerational is the genetic algorithm (GA) algorithm when
* the generational option is chosen, that is, the Steady parameter of the
* given method is not marked.
* This class is an specification of {@link GeneticAlgorithm}.
* </p> 
*/ 

    //The array of the populations for each iteration or generation
    GeneticIndividual [][] population;
	//The array of populations just after the genetic operations for each generation
    GeneticIndividual [][] intermediatePopulation;
	//The array of the elite population: the best individuals ever found
    GeneticIndividual []elite;
	
    //The array of fitness evaluations of each individual in the analysed populations 
    double [][] fitnessCache;
	//Each element in this array marks the goodness of an individual in the population for each generation
    double [][] fitnessMark;
	//The crossover genetic operation probability
    double CROSSOVERPROB;
	//The mutation genetic operation probability
    double MUTATIONPROB;
	//The amplitude of the mutation genetic operation
    double MUTATIONAMPL;
	//The migration probability
	double MIGRATIONPROB;
	//The local optimization method probability
	double LOCALOPTPROB;
	//The local optimization method numer of iterations
	int LOCALOPTITER;
	//The mean fitness value for each iteration
    double []fitnessMean;
	//The best fitness value founf for each iteration
    double []bestFitness;
	//The random number generator to be used
    static Randomize rand;
	//The genetic algorithm's crossover to be used according to the type of {@link GeneticIndividual}
	int CROSSOVERID;
	//The genetic algorithm's mutation to be used according to the type of {@link GeneticIndividual}
	int MUTATIONID;
	//The local optimization method to be used according to the type of {@link GeneticIndividual}
	int LOID;
	
/**
* <p>
* Class constructor with the following parameters:
* </p>
* @param initialIndividual a {@link GeneticIndividual} to start the search process with the desired type of individual
* @param pPopSize an int with the population size
* @param pGenerations an int with the number of iterations to be carried out
* @param PM a double with the mutation probability
* @param AMP a double with the mutation amplitude
* @param PMG a double with the migration probability
* @param pLOptProb a double with the local optimization method probability
* @param NOL an int with the number of iterations in the local optimization method
* @param IOL an int with the local identification method identidication
* @param r the {@link Randomize} object
* @param pCrossoverID the genetic algorithm crossover operation used attending the the current {@link Genotype}
* @param pMutationID the genetic algorithm crossover operation used attending the the current {@link Genotype}
*/
    public GeneticAlgorithmGenerational(
		GeneticIndividual initialIndividual,
		int pPopSize, 
		int nGenerations, 
		double PM, double AMP, double PMIG, 
		double pLOptProb,
		int NOL, int IOL, Randomize r, 
		int pCrossoverID, int pMutationID) {
		
        rand = r;
        population = new GeneticIndividual[nGenerations][pPopSize];
        intermediatePopulation = new GeneticIndividual[nGenerations][pPopSize];
        fitnessCache = new double[nGenerations][pPopSize];
        fitnessMark = new double[nGenerations][pPopSize];
		fitnessMean = new double[nGenerations];
		bestFitness = new double[nGenerations];
		elite = new GeneticIndividual[nGenerations];
        MUTATIONPROB = PM;
        MUTATIONAMPL = AMP;
		MIGRATIONPROB = PMIG;
		CROSSOVERID = pCrossoverID;
		MUTATIONID = pMutationID;
		LOCALOPTPROB = pLOptProb;
		LOCALOPTITER = NOL;
		LOID = IOL;
		
        
        
        try {
		for (int p=0;p<nGenerations;p++) {
			elite[p]=initialIndividual.clone();
			for (int i=0;i<pPopSize;i++) {
				System.out.println("Inicialiting the population[" + i + "]" );
				population[p][i] = initialIndividual.clone();
				intermediatePopulation[p][i] = initialIndividual.clone();
				population[p][i].Random();
				fitnessCache[p][i]=population[p][i].fitness();
				fitnessMean[p]+=fitnessCache[p][i];
				if (fitnessCache[p][i]<bestFitness[p] || i==0) bestFitness[p]=fitnessCache[p][i];
			}
			
		}
		} catch (invalidFitness e) {
		  System.out.println("Unsupported fitness function");
		}
        
    }
	
/** 
* <p> 
* this method is intended for evolving the algorithm for a given number of iterations
* with an generational GA algorithm.
* The basic steps for each iteration are: the fitness normalization, the generation of the 
* intermeadiate population with Stocastic Universal Sampling, the genetic operations to carry
* out and finally the evaluation of the fitness of each individual.
* </p> 
* @param MAXITER an integer with the number of iterations torun in the evolucion
* @return the best {@link GeneticIndividual} found
* @throws {@link invalidCrossover}, {@link invalidMutation} of {@link invalidOptim}
*         in case of unsupported crossover, mutation or local optimization operations.
*/ 	
    public GeneticIndividual evolve(int MAXITER) throws invalidCrossover, invalidMutation, invalidOptim {
        System.out.println("Calculating "+MAXITER+" generations");
		
		int nGenerations=population.length;
        int pPopSize=population[0].length;
        int []lBestIndividual=new int[nGenerations]; double []fitnesselite=new double[nGenerations];
		
		double []bestFitness = new double[nGenerations];
		double []lTheWorstFitness = new double[nGenerations];
		
        for (int i=0;i<MAXITER;i++) {
			System.out.println("Generation="+i);

			for (int p=0;p<nGenerations;p++) {
				// Fitness is normalized
				bestFitness[p]=fitnessCache[p][0];
				lTheWorstFitness[p]=fitnessCache[p][0];
				for (int j=1;j<pPopSize;j++) {
					if (fitnessCache[p][j]<bestFitness[p]) bestFitness[p]=fitnessCache[p][j];
					if (fitnessCache[p][j]>lTheWorstFitness[p]) lTheWorstFitness[p]=fitnessCache[p][j];
				}
				
				for (int j=0;j<pPopSize;j++) {
					fitnessMark[p][j]=1-(fitnessCache[p][j]-bestFitness[p])/(lTheWorstFitness[p]-bestFitness[p]);
				}
				
				// Accumulative Normalized Fitness Table is calculated
				for (int j=1;j<pPopSize;j++) fitnessMark[p][j]+=fitnessMark[p][j-1];
				
			}
			
			for (int p=0;p<nGenerations;p++) {	
				// Temporal population is generated with
                                // Stocastic Universal Sampling
				for (int j=0;j<pPopSize;j++) {
				    // Migrations are executed depending on its probability
				    int origen=p;
					if (rand.Rand()<MIGRATIONPROB) {
					 origen=(int)(rand.Rand()*population.length);
					}
					
					double fit=rand.Rand()*fitnessMark[origen][pPopSize-1];
					int k=0;
					while (fitnessMark[origen][k]<fit) k++;
					
					intermediatePopulation[p][j]=population[origen][k].clone();
				}
				
				// Each individual is crossed(sampling without Replacement)
				for (int j=0;j<pPopSize;j+=2) {
					intermediatePopulation[p][j] .crossover(intermediatePopulation[p][j+1], population[p][j], population[p][j+1],CROSSOVERID);
					
					// Children are mutated
					if (rand.Rand()<MUTATIONPROB) population[p][j].mutation(MUTATIONAMPL, MUTATIONID);
					if (rand.Rand()<MUTATIONPROB) population[p][j+1].mutation(MUTATIONAMPL, MUTATIONID);
					
                                        // Local optimization is applied depending on it probability
					if (rand.Rand()<LOCALOPTPROB) population[p][j].localOptimization(LOCALOPTITER, LOID);
					if (rand.Rand()<LOCALOPTPROB) population[p][j+1].localOptimization(LOCALOPTITER, LOID);
				}
				
				try {
				 // Fitnesses are recalculated for Individuals 
				fitnessMean[p]=0; bestFitness[p]=0; lBestIndividual[p]=0;
				for (int j=0;j<pPopSize;j++) {
					fitnessCache[p][j]=population[p][j].fitness();
					fitnessMean[p]+=fitnessCache[p][j];
					if (fitnessCache[p][j]<bestFitness[p] || j==0) {
						bestFitness[p]=fitnessCache[p][j];
						lBestIndividual[p]=j;
					}
				}
				fitnessMean[p]/=pPopSize;
				} catch(invalidFitness e) {
				  System.out.println("Unsupported fitness function");
				  return population[0][0].clone();
				}
				if (i==0 || bestFitness[p]<fitnesselite[p]) {
					elite[p]=population[p][lBestIndividual[p]].clone(); 
					fitnesselite[p]=bestFitness[p];
				}
				System.out.println("The best fitness in the Elite pop="+fitnesselite[p]+" the best fitness in the pop="+bestFitness[p]+" the mean fitness value="+fitnessMean[p]);
				
			}
		}
		
		int theBestInElite=0;
		for (int p=1;p<nGenerations;p++) if (fitnesselite[p]<fitnesselite[theBestInElite]) theBestInElite=p;
		
        return elite[theBestInElite].clone();
        
        
    }
	
}


