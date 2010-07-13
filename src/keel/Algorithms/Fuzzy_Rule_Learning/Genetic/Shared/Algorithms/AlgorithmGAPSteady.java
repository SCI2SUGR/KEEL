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


public class AlgorithmGAPSteady extends GeneticAlgorithm {
/** 
* <p> 
* AlgorithmGAPSteady is the genetic algorithm and programming (GAP) algorithm when
* the Steady option is chosen, that is, the Steady parameter of the
* given method is marked but the Niches is not.
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
	//The probability of the crossover to be a genetic algorithm crossover
	double GACROSSOVERPROB;
	//The probability of the mutation to be a genetic algorithm mutation
	double GAMUTATIONPROB;
	//The local optimization method probability
	double LOCALOPTPROB;
	//The local optimization method numer of iterations
	int LOCALOPTITER;
	//Each element in this array marks the goodness of an individual in the population for each generation
    double []fitnessMark;
	//The best fitness value founf for each iteration
    double []bestFitness;
	//The random number generator to be used
    static Randomize rand;
	//The genetic algorithm's crossover to be used according to the type of {@link GeneticIndividual}
	int GACROSSOVERID;
	//The genetic programming's crossover to be used according to the type of {@link GeneticIndividual}
	int GPCROSSOVERID;
	//The genetic algorithm's mutation to be used according to the type of {@link GeneticIndividual}
	int GAMUTATIONID;
	//The genetic programming's mutation to be used according to the type of {@link GeneticIndividual}
	int GPMUTATIONID;
	//The population number of individuals
	int POPULATIONSIZE;
	//The local optimization method to be used according to the type of {@link GeneticIndividual}
	int LOID;
	
/**
* <p>
* Class constructor with the following parameters:
* </p>
* @param initialIndividual a {@link GeneticIndividual} to start the search process with the desired type of individual
* @param pPopSize an int with the population size
* @param pTourSize an int with the number of individuals that must be chosen for the tournament
* @param PM a double with the mutation probability
* @param AMP a double with the mutation amplitude
* @param PMG a double with the migration probability
* @param pGACrossoverProb a double with the genetic algorithm crossover operation probability
* @param pGAMutationProb a double with the genetic algorithm mutation operation probability
* @param pGenerations an int with the number of iterations to be carried out
* @param pLOptProb a double with the local optimization method probability
* @param NOL an int with the number of iterations in the local optimization method
* @param IOL an int with the local identification method identidication
* @param r the {@link Randomize} object
* @param pGACrossoverID the genetic algorithm crossover operation used attending the the current {@link Genotype}
* @param pGPCrossoverID the genetic algorithm crossover operation used attending the the current {@link Genotype}
* @param pGAMutationID the genetic algorithm crossover operation used attending the the current {@link Genotype}
* @param pGPMutationID the genetic algorithm crossover operation used attending the the current {@link Genotype}
*/
    public AlgorithmGAPSteady(
							  GeneticIndividual initialIndividual,
							  int pPopSize,
							  int pTourSize,
							  double PM,
							  double AMP,
							  double PMG,
							  double pGACrossoverProb,
							  double pGAMutationProb,
							  int pGenerations,
							  double pLOptProb,
							  int NOL,
							  int IOL,
							  Randomize r,
							  int pGACrossoverID,
							  int pGPCrossoverID,
							  int pGAMutationID,
							  int pGPMutationID) {

	System.out.println("POP="+pPopSize+" tournament="+pTourSize+
                           " PM="+PM+" AMP="+AMP+" PMG="+PMG+
                           " pGACrossoverProb="+pGACrossoverProb+" pGAMutationProb="+pGAMutationProb+" PNPOP="+pGenerations+
                           " pLOptProb="+pLOptProb+" NOL="+NOL+" IOL="+IOL);
	
		GACROSSOVERID = pGACrossoverID;
		GAMUTATIONID = pGAMutationID;
		GPCROSSOVERID = pGPCrossoverID;
		GPMUTATIONID = pGPMutationID;
        MUTATIONPROB = PM;
		MIGRATIONPROB = PMG;
		GACROSSOVERPROB = pGACrossoverProb;
		GAMUTATIONPROB = pGAMutationProb;
		POPULATIONSIZE = pGenerations;
        MUTATIONAMPL = AMP;
		LOCALOPTPROB = pLOptProb;
		LOCALOPTITER = NOL;
		LOID = IOL;

        rand=r;
        population = new GeneticIndividual[POPULATIONSIZE][pPopSize];
        tournament = new int[pTourSize];
		tournament2 = new int [pTourSize];
        tournamentFitness = new double[pTourSize];
        fitnessCache = new double[POPULATIONSIZE][pPopSize];
		fitnessMark = new double[POPULATIONSIZE];
		bestFitness = new double[POPULATIONSIZE];
				
		fitnessMark = new double[POPULATIONSIZE];
		bestFitness = new double[POPULATIONSIZE];
		
		try {
			for (int p=0;p<POPULATIONSIZE;p++) {
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
		} catch (invalidFitness f) {
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
* with an steady with niches GAP algorithm.
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
		
        System.out.println("Calculating "+MAXITER+" generations ");
		
		try {
        for (int i=0;i<MAXITER;i++) {
			
			if (i%100==0) System.out.println("Generation="+i);
			for (int p=0;p<population.length;p++) {
				
				
				for (int j=0;j<pTourSize;j++) { tournament[j]=-1; }
				for (int j=0;j<pTourSize;j++) {
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
					sortedInsertion(ind,fitnessCache[p][ind],tournament);
				}
				
				int tc,tm;
				if (rand.Rand()<GACROSSOVERPROB) tc=GACROSSOVERID; else tc=GPCROSSOVERID;
				if (rand.Rand()<GAMUTATIONPROB) tm=GAMUTATIONID; else tm=GPMUTATIONID;
				
				population[p][tournament[0]].
					crossover(population[p][tournament[1]], population[p][tournament[pTourSize-2]], population[p][tournament[pTourSize-1]],tc);
				
				if (rand.Rand()<MUTATIONPROB) population[p][tournament[pTourSize-2]].mutation(MUTATIONAMPL,tm);
				if (rand.Rand()<MUTATIONPROB) population[p][tournament[pTourSize-1]].mutation(MUTATIONAMPL,tm);
				
				if (rand.Rand()<LOCALOPTPROB) {
					population[p][tournament[pTourSize-2]].localOptimization(LOCALOPTITER, LOID);
				}
				if (rand.Rand()<LOCALOPTPROB) {
					population[p][tournament[pTourSize-1]].localOptimization(LOCALOPTITER, LOID);
				}

				if (rand.Rand()<MIGRATIONPROB) {
					int lTheOutput=(int)(rand.Rand()*population.length);
					for (int j=0;j<pTourSize;j++) { tournament2[j]=-1; }
					for (int j=0;j<pTourSize;j++) {
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
						sortedInsertion(ind,fitnessCache[lTheOutput][ind],tournament2);
					}
					
					population[lTheOutput][tournament2[pTourSize-1]]=population[p][tournament[0]].clone(); 
					fitnessCache[lTheOutput][tournament2[pTourSize-1]]=fitnessCache[p][tournament[0]];
					
					
				}
				
				
				// fitnessMark[p]-=fitnessCache[p][tournament[pTourSize-2]];
				// fitnessMark[p]-=fitnessCache[p][tournament[pTourSize-1]];
				fitnessCache[p][tournament[pTourSize-2]]=population[p][tournament[pTourSize-2]].fitness();
				fitnessCache[p][tournament[pTourSize-1]]=population[p][tournament[pTourSize-1]].fitness();
				// fitnessMark[p]+=fitnessCache[p][tournament[pTourSize-2]];
				// fitnessMark[p]+=fitnessCache[p][tournament[pTourSize-1]];
				if (fitnessCache[p][tournament[pTourSize-2]]<bestFitness[p]) bestFitness[p]=fitnessCache[p][tournament[pTourSize-2]];
				if (fitnessCache[p][tournament[pTourSize-1]]<bestFitness[p]) bestFitness[p]=fitnessCache[p][tournament[pTourSize-1]];
				
				if (i%100==0) {
					fitnessMark[p]=0;
					for (int k=0;k<fitnessCache[p].length;k++) fitnessMark[p]+=fitnessCache[p][k];
					System.out.print("Best="+bestFitness[p]+" Mean="+fitnessMark[p]/population[p].length);
					
					int[] whichIsMyFamily=new int[population[p].length];
					int numfamilias=0;
					for (int j=0;j<population[p].length;j++) {
						if (whichIsMyFamily[j]>0) continue;
						numfamilias++;
						whichIsMyFamily[j]=numfamilias;
						for (int k=j;k<population[p].length;k++)
							if (population[p][j].g.isRelated(population[p][k].g)) {
								whichIsMyFamily[k]=numfamilias;
							}
					}
					System.out.println(" Number of families="+numfamilias);
				}
			}
				
        }
			
		} catch (invalidFitness f) {
		  System.out.println("Unsupported fitness function");
		}

		int pmin=0; double minfit=0; int imin=0; boolean theFirst=true;
		for (int p=0;p<population.length;p++) { 
			for (int i=0;i<population[p].length;i++) {
				double fit=fitnessCache[p][i];
				if (fit<minfit || theFirst) {
					theFirst=false;
					minfit=fit; imin=i; pmin=p;
				}
			}
		}
		System.out.println("Minimum Fitness="+minfit);
		return population[pmin][imin].clone();
			
    }
		
		
		
		
}


