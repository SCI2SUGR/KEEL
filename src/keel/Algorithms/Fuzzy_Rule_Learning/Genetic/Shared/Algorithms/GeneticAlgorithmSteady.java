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


public class GeneticAlgorithmSteady extends GeneticAlgorithm {
/** 
* <p> 
* GeneticAlgorithmSteady is the genetic algorithm (GA) algorithm when
* the steady option is chosen, that is, the Steady parameter of the
* given method is marked.
* This class is an specification of {@link GeneticAlgorithm}.
* </p> 
*/ 

    //The array of the populations for each iteration or generation
    GeneticIndividual [][] population;
    int [] tournament;
	int [] tournament2;
    double[] tournamentFitness;
    //The array of fitness evaluations of each individual in the analysed populations 
    double[][] fitnessCache;
	//The mutation genetic operation probability
    double MUTATIONPROB;
	//The amplitude of the mutation genetic operation
    double MUTATIONAMPL;
	//The migration probability
	double MIGRATIONPROB;
	//The local optimization method probability
	double LOCALOPTPROB;
	//The local optimization method number of iterations
	int LOCALOPTITER;
	//Each element in this array marks the goodness of an individual in the population for each generation
    double []fitnessMark;
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
* @param pTourSize an int with the number of individuals that must be chosen for the tournament
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
    public GeneticAlgorithmSteady(
                                   GeneticIndividual initialIndividual,
                                   int pPopSize,
								   int pGenerations,
                                   int pTourSize,
                                   double PM,
                                   double AMP,
								   double PMG,
								   double pLOptProb,
								   int NOL,
								   int IOL,
                                   Randomize r,
								   int pCrossoverID,
								   int pMutationID) {
        
        rand=r;
        population = new GeneticIndividual[pGenerations][pPopSize];
        tournament = new int[pTourSize];
		tournament2 = new int [pTourSize];
        tournamentFitness = new double[pTourSize];
        fitnessCache = new double[pGenerations][pPopSize];
		fitnessMark = new double[pGenerations];
		bestFitness = new double[pGenerations];
        MUTATIONPROB = PM;
		MIGRATIONPROB = PMG;
		LOCALOPTPROB = pLOptProb;
        MUTATIONAMPL = AMP;
		CROSSOVERID = pCrossoverID;
		MUTATIONID = pMutationID;
		LOCALOPTITER = NOL;
		LOID = IOL;
		
		try {
		for (int p=0;p<pGenerations;p++) {
			fitnessMark[p]=0;
			bestFitness[p]=0;
			for (int i=0;i<pPopSize;i++) {
				System.out.println("Inicialiting the population["+i+"]");
				population[p][i] = initialIndividual.clone();
				population[p][i].Random();
				fitnessCache[p][i]=population[p][i].fitness();
				fitnessMark[p]+=fitnessCache[p][i];
				if (fitnessCache[p][i]<bestFitness[p] || i==0) bestFitness[p]=fitnessCache[p][i];
			}
        }
		} catch (invalidFitness e) {
		  System.out.println("Unsupported fitness function");
		}
    }


/**
* <p>
* This method carries out a sorted insertion of the example x in the array tournament given its 
* fitness value. Also, its fitness value is sorted inserted in {@link tournamentFitness}.
* </p>
* @param x  the example to insert
* @param valueX the fitness value of x
* @param tournament the array of indexes with sorted individuals in the tournament
*/
    private void sortedInsertion(int x, double valueX, int tournament[]) {
        
        int pos=0;
        while (tournament[pos]>=0 && valueX>tournamentFitness[pos] && pos<tournament.length) pos++;

        if (pos==tournament.length) return;
        for (int i=tournament.length-1;i>pos;i--) {
            tournament[i]=tournament[i-1];
            tournamentFitness[i]=tournamentFitness[i-1];
        }
        tournament[pos]=x;
        tournamentFitness[pos]=valueX;

    }

/** 
* <p> 
* this method is intended for evolving the algorithm for a given number of iterations
* with an steady GA algorithm.
* The basic steps for each iteration are: the tournament selection and the genetic 
* operations are carried out, the offsprings are evaluated and sorted with the current
* population.
* </p> 
* @param MAXITER an integer with the number of iterations torun in the evolucion
* @return the best {@link GeneticIndividual} found
* @throws {@link invalidCrossover}, {@link invalidMutation} of {@link invalidOptim}
*         in case of unsupported crossover, mutation or local optimization operations.
*/ 	
    public GeneticIndividual evolve(int MAXITER) throws invalidCrossover, invalidMutation, invalidOptim {

        int pTourSize=tournament.length;

        System.out.println("Calculation "+MAXITER+" generations ");
       
	    try {
        for (int i=0;i<MAXITER;i++) {

		  if (i%100==0) System.out.println("Generation="+i);
          for (int p=0;p<population.length;p++) {
		  
           
            for (int j=0;j<pTourSize;j++) { tournament[j]=-1; }
            for (int j=0;j<pTourSize;j++) {
                //  An individual out of the tourment is loocked for.
                int ind=0; boolean hasBeenFound=false;
                do {
                    hasBeenFound=false;
                    ind=(int)(rand.Rand()*population[p].length);
                    for (int k=0;k<pTourSize;k++) {
                        if (ind==tournament[k]) {
                            hasBeenFound=true; break;
                        }
                    }
                } while (hasBeenFound);
                // The found individual is inserted by order
                sortedInsertion(ind,fitnessCache[p][ind],tournament);
            }
            
            // Tournament Winners are crossed
            population[p][tournament[0]].
                crossover(population[p][tournament[1]], population[p][tournament[pTourSize-2]], population[p][tournament[pTourSize-1]],CROSSOVERID);

            // Children are mutated
            if (rand.Rand()<MUTATIONPROB) population[p][tournament[pTourSize-2]].mutation(MUTATIONAMPL,MUTATIONID);
            if (rand.Rand()<MUTATIONPROB) population[p][tournament[pTourSize-1]].mutation(MUTATIONAMPL,MUTATIONID);
            
            // Local optimization is applied depending on it probability
            if (rand.Rand()<LOCALOPTPROB) population[p][tournament[pTourSize-2]].localOptimization(LOCALOPTITER, LOID);
            if (rand.Rand()<LOCALOPTPROB) population[p][tournament[pTourSize-1]].localOptimization(LOCALOPTITER, LOID);
			
			// Winner from tournamet is copied to any sub-population using certain probability.
			// Now a new tournament is performed over selected sub-population to achieve elitism in the algorithm
			if (rand.Rand()<MIGRATIONPROB) {
                            // Sub-population selected to insert the winner individual
			    int lTheOutput=(int)(rand.Rand()*population.length);
				for (int j=0;j<pTourSize;j++) { tournament2[j]=-1; }
				for (int j=0;j<pTourSize;j++) {
					// An individual out of the tourment is loocked for.
					int ind=0; boolean hasBeenFound=false;
					do {
						hasBeenFound=false;
						ind=(int)(rand.Rand()*population[lTheOutput].length);
						for (int k=0;k<pTourSize;k++) {
							if (ind==tournament2[k]) {
								hasBeenFound=true; break;
							}
						}
					} while (hasBeenFound);
                                         // The found individual is inserted by order
					sortedInsertion(ind,fitnessCache[lTheOutput][ind],tournament2);
				}
				
                                // Tournament Winner is copied over the loser from the remote				
				fitnessMark[lTheOutput]-=fitnessCache[lTheOutput][tournament[pTourSize-1]];
				population[lTheOutput][tournament2[pTourSize-1]]=population[p][tournament[0]].clone();
				fitnessCache[lTheOutput][tournament2[pTourSize-1]]=fitnessCache[p][tournament[0]];
				fitnessMark[lTheOutput]+=fitnessCache[lTheOutput][tournament[pTourSize-1]];
				
			}

            
            // Fitness for new individual is calculated
            fitnessMark[p]-=fitnessCache[p][tournament[pTourSize-2]];
            fitnessMark[p]-=fitnessCache[p][tournament[pTourSize-1]];
            fitnessCache[p][tournament[pTourSize-2]]=population[p][tournament[pTourSize-2]].fitness();
            fitnessCache[p][tournament[pTourSize-1]]=population[p][tournament[pTourSize-1]].fitness();
            fitnessMark[p]+=fitnessCache[p][tournament[pTourSize-2]];
            fitnessMark[p]+=fitnessCache[p][tournament[pTourSize-1]];
            if (fitnessCache[p][tournament[pTourSize-2]]<bestFitness[p]) bestFitness[p]=fitnessCache[p][tournament[pTourSize-2]];
            if (fitnessCache[p][tournament[pTourSize-1]]<bestFitness[p]) bestFitness[p]=fitnessCache[p][tournament[pTourSize-1]];

            if (i%100==0) {
                System.out.println("The best fitness="+bestFitness[p]+" the fitness mean="+fitnessMark[p]/population[p].length);
            }
			}
            
        }
		
		} catch (invalidFitness f) {
		  System.out.println("Unsupported fitnes function");
		}

        int pmin=0; double minfit=0; int imin=0; boolean theFirst=true;
		for (int p=0;p<population.length;p++) {
			// Best individual from Population is looked for
			for (int i=0;i<population[p].length;i++) {
				double fit=fitnessCache[p][i];
				if (fit<minfit || theFirst) {
					theFirst=false;
					minfit=fit; imin=i; pmin=p;
				}
			}
		}
        System.out.println("Minimum Fitness found="+minfit);
        return population[pmin][imin].clone();
    
    }

    
    
}

