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


public class AlgorithmGAPNiches extends GeneticAlgorithm {
/** 
* <p> 
* AlgorithmGAPNiches is the genetic algorithm and programming (GAP) algorithm when
* the steady with niches options are chosen, that is, the Steady and the Niches 
* parameters of the given method are marked.
* This class is an specification of {@link GeneticAlgorithm}.
* </p> 
*/ 
    
    final boolean debug=false;     //This Flag indicates if trace execution is printed on the standard out or not.
    
    //The array of the populations for each iteration or generation
    GeneticIndividual [][] population;   //This variable contains individuals of population
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
    //The worst individual found for each generation
    int popWorst[];
	//The second worst individual found for each generation
    int popSecondWorst[];
    //The intra-niche crossover probablity
    double INTRANICHEPROB = 0.75;  
	//The maximum allowable number of niches
    int NMAX = 10; 
    //Auxiliary {@link GeneticIndividual} used in the learning process
    private GeneticIndividual tmp1;
    //Auxiliary {@link GeneticIndividual} used in the learning process
	private GeneticIndividual tmp2;

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
	public AlgorithmGAPNiches(
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
        popWorst = new int[POPULATIONSIZE];
        popSecondWorst = new int [POPULATIONSIZE];
        
		try {
        for (int p=0;p<POPULATIONSIZE;p++) {
            fitnessMark[p]=0;
            bestFitness[p]=0;
            for (int i=0;i<pPopSize;i++) {
                System.out.println("Inicialiting the population["+i+"]");
                population[p][i] = initialIndividual.clone();
                population[p][i].Random();
                fitnessCache[p][i]=population[p][i].fitness();
			}
        }
	    } catch (invalidFitness f) {
			  System.out.println("Unsupported fitness function");
		}

        tmp1 = population[0][0].clone();
        tmp2 = population[0][0].clone();
        
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
* The basic steps for each iteration are: firstly computes the number of different 
* families in the island, then the tournament and the genetic operations are carried
* out, the offsprings are evaluated and assigned to a niche.
* </p> 
* @param MAXITER an integer with the number of iterations torun in the evolucion
* @return the best {@link GeneticIndividual} found
* @throws {@link invalidCrossover}, {@link invalidMutation} of {@link invalidOptim}
*         in case of unsupported crossover, mutation or local optimization operations.
*/ 	
    public GeneticIndividual evolve(int MAXITER) throws invalidCrossover, invalidMutation, invalidOptim {
        
        int pTourSize=tournament.length;
        boolean bIntraNiche=false;
        int thisIsOut1=0;
        int thisIsOut2=0;
        
        System.out.println("Calculating "+MAXITER+" generations ");
        int canBeChosen[] = new int [population[0].length];
        int bestFromNiche[] = new int [population[0].length];
        int worstFromNiche[] = new int [population[0].length];
        int nichesNumber[] = new int [population[0].length];
        
        try {
        for (int i=0;i<MAXITER;i++) {
            
            if (i%100==0) System.out.println("Generation="+i);
            
            // Select island number 'p'
            for (int p=0;p<population.length;p++) {
                
                // Compute the number of different families in the island
                int[] whichIsMyFamily=new int[population[p].length];
                int numberOfFamilies=0;
				fitnessMark[p]=0;
				popWorst[p]=0;
				popSecondWorst[p]=0;
                for (int j=0;j<population[p].length;j++) {
                    if (whichIsMyFamily[j]>0) continue;
                    whichIsMyFamily[j]=numberOfFamilies;

                    for (int k=j;k<population[p].length;k++) {
                        if (population[p][j].g.isRelated(population[p][k].g)) {
                            whichIsMyFamily[k]=numberOfFamilies;
                        }
						if (fitnessCache[p][k]<bestFitness[p] || k==0) bestFitness[p]=fitnessCache[p][k];
						
						if (fitnessCache[p][k]>=fitnessCache[p][popWorst[p]]) {
							popWorst[p]=k;
						}
						
						if ((fitnessCache[p][k]>=fitnessCache[p][popSecondWorst[p]] && 
						     fitnessCache[p][k]<=fitnessCache[p][popWorst[p]] &&
							 popWorst[p]!=k)) {
							popSecondWorst[p]=k;
						}
						
						fitnessMark[p]+=fitnessCache[p][k];
                        
                    }
                    numberOfFamilies++;
                                        
                }

				// Trace the results
				if (i%100==0) System.out.println("Best="+bestFitness[p]
												 +" mean="+fitnessMark[p]/population[p].length
												 +" number of families="+numberOfFamilies);
				
                if (debug) {
                 for (int j=0;j<population[p].length;j++) System.out.print(whichIsMyFamily[j]+" ");
                 System.out.println();
                 System.out.println(" Number of Families="+numberOfFamilies);
                
                 System.out.println("Worst individual: "+popWorst[p]+", its fitness="+fitnessCache[p][popWorst[p]]);
                 System.out.println("Second worst individual "+popSecondWorst[p]+", its fitness="+fitnessCache[p][popSecondWorst[p]]);
                }
                
				
				// Tournament
                for (int j=0;j<pTourSize;j++) { tournament[j]=-1; }
                
                // First element in the tournament can be anywhere in the population
                int ind=(int)(rand.Rand()*population[p].length);
                sortedInsertion(ind,fitnessCache[p][ind],tournament);
                if (debug) {
                System.out.println("The first indiviual in the tournament is "+ind+", with type "+whichIsMyFamily[ind]);
                }
                
                // Compute the number of individuals in its same family
                int accumulateThis=0;
                for (int j=0;j<population[p].length;j++) {
                    if (whichIsMyFamily[j]==whichIsMyFamily[ind]) {
                        canBeChosen[accumulateThis]=j; 
                        accumulateThis++;
                    }
                }
                if (debug) {
                  System.out.println("Niche number of individuals="+accumulateThis);
                }
				
				// Decide whether we want (and can) make an inter-niche crossover
                if (rand.Rand()<INTRANICHEPROB && accumulateThis>=pTourSize) {
                    if (debug) System.out.println("Intra-niche crossover");
                    bIntraNiche=true;
                } else {
                    if (debug) System.out.println("Inter-niche crossover");
                    for (int j=0;j<population[p].length;j++) canBeChosen[j]=j;
                    accumulateThis=population[p].length;
                    bIntraNiche=false;
                }
                
                            
                // Select the remaining elements in the tournament
				// according to the preceding selection
                for (int j=0;j<pTourSize-1;j++) {
					
					// Select at random an individual in "canBeChosen"
					// without replacement
                    ind=0; boolean hasBeenFound=false;
                    do {
                        hasBeenFound=false;
                        ind=canBeChosen[(int)(rand.Rand()*accumulateThis)];
                        for (int k=0;k<pTourSize;k++) {
                            if (ind==tournament[k]) {
                                hasBeenFound=true; break;
                            }
                        }
                    } while (hasBeenFound);
					
                    // Insert this individual into the table
                    if (debug) System.out.println("Individual "+ind+" introduced in the tournament, with type="+whichIsMyFamily[ind]);
                    sortedInsertion(ind,fitnessCache[p][ind],tournament);
                }                    
                
				// Crossover: the parents are the two best individuals in the tournament
                int tc,tm;
                if (rand.Rand()<GACROSSOVERPROB || bIntraNiche) tc=GACROSSOVERID; else tc=GPCROSSOVERID;
                if (rand.Rand()<GAMUTATIONPROB || bIntraNiche) tm=GAMUTATIONID; else tm=GPMUTATIONID;
                
                population[p][tournament[0]].
                    crossover(population[p][tournament[1]], tmp1, tmp2 ,tc);
                
                // The offspring is mutated
                if (rand.Rand()<MUTATIONPROB) tmp1.mutation(MUTATIONAMPL,tm);
                if (rand.Rand()<MUTATIONPROB) tmp2.mutation(MUTATIONAMPL,tm);
                
                // Local optimization
                if (rand.Rand()<LOCALOPTPROB) {
                    tmp1.localOptimization(LOCALOPTITER, LOID);
                }
                if (rand.Rand()<LOCALOPTPROB) {
                    tmp2.localOptimization(LOCALOPTITER, LOID);
                }
   
                // Evaluation of the fitness
                double f1=tmp1.fitness();
                double f2=tmp2.fitness();
                
				// NICHING:
				// We must decide whether the offspring will be inserted
				// in the new population

				// Find the best/worst individual of every family
                for (int pos=0;pos<numberOfFamilies;pos++) {
                    bestFromNiche[pos]=-1;
                    worstFromNiche[pos]=-1;
                    nichesNumber[pos]=0;
                }
                
                for (int j=0;j<population[p].length;j++) {
                    int pos=whichIsMyFamily[j];
                    if (bestFromNiche[pos]==-1) bestFromNiche[pos]=j;
                    if (worstFromNiche[pos]==-1) worstFromNiche[pos]=j;
                    
                    if (fitnessCache[p][j]<fitnessCache[p][bestFromNiche[pos]]) bestFromNiche[pos]=j;
                    if (fitnessCache[p][j]>fitnessCache[p][worstFromNiche[pos]]) worstFromNiche[pos]=j;
                    nichesNumber[pos]++;
                }
                
                
                int niche1=0, niche2=0;
                if (bIntraNiche) {
                   // tmp1 and tmp2 will be in the same niche as their ancestors
                    niche1 = whichIsMyFamily[tournament[0]];
                    niche2 = niche1;
                } else {
                   // Check whether tmp1 and tmp2 will be in an existent family
                    niche1=-1; niche2=-1; 
                    for (int j=0;j<population[p].length;j++) {
                        if (tmp1.g.isRelated(population[p][j].g)) { niche1=whichIsMyFamily[j]; break; }
                    }
                    for (int j=0;j<population[p].length;j++) {
                        if (tmp2.g.isRelated(population[p][j].g)) { niche2=whichIsMyFamily[j]; break; }
                    }

                }
                
                if (debug) System.out.println("The tmp1 individual's niche="+niche1+" and the tmp2's niche="+niche2);
                
				
				// Determine the number of elements that there should be in the families
				// of tmp1 and tmp2 (the offspring) and the two worst individuals in the
				// tournament

                double fbest1=0;
                double fbest2=0;
                double fbest3=fitnessCache[p][bestFromNiche[whichIsMyFamily[tournament[tournament.length-2]]]];
                double fbest4=fitnessCache[p][bestFromNiche[whichIsMyFamily[tournament[tournament.length-1]]]];
               
				// The number of elements is given by the best individual of the family
                if (niche1==-1) fbest1=f1; else fbest1=fitnessCache[p][bestFromNiche[niche1]];
                if (niche2==-1) fbest2=f2; else fbest2=fitnessCache[p][bestFromNiche[niche2]];
                
                if (debug) {    
                  System.out.println("Descendent fitness="+f1+" "+f2);
                  System.out.println("Descendent representatives fitness="+fbest1+" "+fbest2);
                  System.out.println("Loosers Fitness="+
                                   fitnessCache[p][tournament[tournament.length-2]]+
                                   " "+
                                   fitnessCache[p][tournament[tournament.length-1]]);
                  System.out.println("Loosers representatives Fitness="+fbest3+" "+fbest4);
                }
                
                // Determine the ordinal in the population that the offspring and
				// the losers of the tournament would have/have. The population is
				// not ordered
                int obest1=0;
                int obest2=0;
                int obest3=0;
                int obest4=0;
                for (int j=0;j<population[p].length;j++) {
                    if (fitnessCache[p][j]<fbest1) obest1++;
                    if (fitnessCache[p][j]<fbest2) obest2++;
                    if (fitnessCache[p][j]<fbest3) obest3++;
                    if (fitnessCache[p][j]<fbest4) obest4++;
                }
              
				// Evaluate the number of elements we want for the families
				// of the offspring 
                int nCompanion1=(int)((NMAX-1.0)*(1.0*population[p].length-obest1))/(population[p].length-1) + 1;
                int nCompanion2=(int)((NMAX-1.0)*(1.0*population[p].length-obest2))/(population[p].length-1) + 1;
                int nCompanion3=(int)((NMAX-1.0)*(1.0*population[p].length-obest3))/(population[p].length-1) + 1;
                int nCompanion4=(int)((NMAX-1.0)*(1.0*population[p].length-obest4))/(population[p].length-1) + 1;
				
				// Evaluate the number of elements that exist for the families
				// of the losers
                int ncreal1=0;
                int ncreal2=0;
                int ncreal3=nichesNumber[whichIsMyFamily[tournament[tournament.length-2]]];
                int ncreal4=nichesNumber[whichIsMyFamily[tournament[tournament.length-1]]];
                if (niche1!=-1) ncreal1=nichesNumber[niche1];
                if (niche2!=-1) ncreal2=nichesNumber[niche2];
                
                if (debug) {
                  System.out.println("Better than tmp1 individuals="+obest1);
                  System.out.println("Better than tmp2 individuals="+obest2);
                
                  System.out.println("tmp1's desired companion="+nCompanion1);
                  System.out.println("tmp2's desired companion="+nCompanion2);
                  System.out.println("tmp1's final companion="+ncreal1);
                  System.out.println("tmp2's final companion="+ncreal2);
                
                  System.out.println("looser1's desired companion="+nCompanion3);
                  System.out.println("looser2's desired companion="+nCompanion4);
                  System.out.println("looser1's final companion="+ncreal3);
                  System.out.println("looser2's final companion="+ncreal4);
                }
               
			   				    
				// Decide who is inserted and who is removed
				
				
				// 1. Who should be removed
				ind=0;
				int []erased = new int[2]; erased[0]=-1; erased[1]=-1;
				if (ncreal3>nCompanion3) {
				   //  population[tournament.length-2] should be eliminated
				   erased[ind]=tournament[tournament.length-2];
				   ind++;
				}
				if (ncreal4>nCompanion4) {
				   // population[tournament.length-1] should eliminated
				   erased[ind]=tournament[tournament.length-1];
				   ind++;
				}
				   
				// 2. Who should be inserted. 'ind' counts how
				// many individuals of the tournament we may remove.
				// if 'ind' is 0, we remove individuals at the end
				// of the population
				
				if (ncreal1<nCompanion1) {
				   // tmp1 should be inserted in population
				   if (ind>0) {
					  // We have room
				      population[p][erased[ind-1]]=tmp1.clone(); 
					  fitnessCache[p][erased[ind-1]]=f1;
					  ind--; 
				   } else {
				      // Remove the last element in the population
				      if (f1<fitnessCache[p][popWorst[p]]) {
					    population[p][popWorst[p]]=tmp1.clone(); 
					    fitnessCache[p][popWorst[p]]=f1;
					  }
				   } 
				}  
				
				if (ncreal2<nCompanion2) {
				   // tmp2 should inserted in population
                   if (ind>0) {
				      // We have room
				      population[p][erased[ind-1]]=tmp2.clone(); 
					  fitnessCache[p][erased[ind-1]]=f2;
					  ind--; 
				   } else {
				      // If population is full worst fitness individual is replaced
				      if (f2<fitnessCache[p][popSecondWorst[p]]) {
					    population[p][popSecondWorst[p]]=tmp2.clone(); 
					    fitnessCache[p][popSecondWorst[p]]=f2;
					  } else if (f2<fitnessCache[p][popWorst[p]]) {
					    population[p][popWorst[p]]=tmp2.clone(); 
					    fitnessCache[p][popWorst[p]]=f2;
					  }
				   } 
				}
				
                              
                // Migration: the winner of the tournament is cloned in a different
				// population, replacing the worst individual in a new tournament
				
                if (rand.Rand()<MIGRATIONPROB) {
                    // Sub-population selected to insert the winner individual
                    int lTheOutput=(int)(rand.Rand()*population.length);
                    for (int j=0;j<pTourSize;j++) { tournament2[j]=-1; }
                    for (int j=0;j<pTourSize;j++) {
						ind=0; boolean hasBeenFound=false;
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
                    
                    // Update the receiving population 
                    population[lTheOutput][tournament2[pTourSize-1]]=population[p][tournament[0]].clone();
                    fitnessCache[lTheOutput][tournament2[pTourSize-1]]=fitnessCache[p][tournament[0]];
                    
                    
                }
                
            }
			
			            
    }
	
	} catch (invalidFitness f) {
			  System.out.println("Unsupported fitness function");
	}

        
    int pmin=0; double minfit=0; int imin=0; boolean theFirst=true;
    for (int p=0;p<population.length;p++) {
        // Best individual
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


