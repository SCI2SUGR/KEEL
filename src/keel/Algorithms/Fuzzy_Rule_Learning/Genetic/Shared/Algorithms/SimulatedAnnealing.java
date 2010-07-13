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


// Simulated Annealing in "Combining GP operators with SA search
// 
// 
public class SimulatedAnnealing extends GeneticAlgorithm {
/** 
* <p> 
* SimulatedAnnealing is the simulated annealing evolutionary algorithm and 
* programming (SAP) algorithm as detailed in "Combining GP operators with SA 
* search to evolve fuzzy rule based classifiers", Sanchez, Couso, Corrales
* Information Sciences 136 (2001).
* This class is an specification of {@link GeneticAlgorithm}.
* </p> 
*/ 
    
	//The random number generator to be used
    static Randomize rand;
	//The fitness value of the current individual
    double fitnessC;
	//The fitness value of the best individual found
	double fitnessC1;
	//The value of the best fitness ever found
	double fitnessCBest=fitnessC;
	//The coefficient for the fitness error following the simulate annealing evolution rules
	double delta;
	//The evolution bound following the simulate annealing evolution rules
	double v;
	//The iteration temperature 
	double T;
	//The initial temperature following the simulate annealing evolution rules
	double T0;
	//The final temperature following the simulate annealing evolution rules
	double T1;
	//The initial probability associated to T0, following the simulate annealing evolution rules
	double P0;
	//The final probability associated to T0, following the simulate annealing evolution rules
	double P1;
    //The current individual, the search point 
    GeneticIndividual C;
	//The best individual ever found
	GeneticIndividual CBest;
	//The mutation genetic operation probability
    double GAMUTATIONPROB;
	//The local optimization method probability
	double LOCALOPTPROB;
	//The local optimization method numer of iterations
	int LOCALOPTITER;
	//The genetic algorithm's mutation to be used according to the type of {@link GeneticIndividual}
    int GAMUTATIONID;    // Kind of mutation
	//The genetic programming's mutation to be used according to the type of {@link GeneticIndividual}
    int GPMUTATIONID;
	//The number of searchs for each iteration 
	int NSUB;
	//The temperature coefficient to allow mutation operations
	double K1;
	//The mean temperature coefficient to allow mutation operations
	double Km;
	//The allowable fitness increment in each iteration
	double df;
	//The local optimization method to be used according to the type of {@link GeneticIndividual}
	int LOID;
	
 /**
* <p>
* Class constructor with the following parameters:
* </p>
* @param initialIndividual a {@link GeneticIndividual} to start the search process with the desired type of individual
* @param pPopSize an int with the population size
* @param pGenerations an int with the number of iterations to be carried out
* @param deltaFit a double with the coefficient for the fitness error following the simulate annealing evolution rules
* @param pPROBMUTAGA a double with the mutation probability
* @param p0 the desired initial temperature
* @param p1 the desired final temperature
* @param Km a double with the mutation amplitude
* @param pNSUB an int with the number of individuals to be analized in each iteration
* @param r the {@link Randomize} object
* @param pGAMutationID the genetic algorithm mutation operation used attending the the current {@link Genotype}
* @param pGPMutationID the genetic programming mutation operation used attending the the current {@link Genotype}
* @param pNUMOL an int with the number of iterations in the local optimization method
* @param pIDOL an int with the local identification method identidication
* @param pLOptProb a double with the local optimization method probability
*/
   public SimulatedAnnealing(
                            GeneticIndividual initialIndividual,	
							double pPROBMUTAGA,          // Mutation probability for GA
							double deltaFit,             // Fitness increment for a waited crossover
							double p0,                   // Probability for accepting -deltafit in iteration 0
                            double p1,                                               // Probability for accepting -deltafit in final iteration
							double KM,                   // Mutation amplitude
							int pNSUB,                   // Number of iterations for each temperature
                            Randomize r,
                            int pGAMutationID,
                            int pGPMutationID,
							int pNUMOL,
							int pIDOL,
							double pLOptProb) {
        
        rand = r; P0=p0; P1=p1; GAMUTATIONPROB=pPROBMUTAGA; NSUB=pNSUB;
        T0 = -deltaFit/Math.log(p0);          // Initial temperature
		T1 = -deltaFit/Math.log(p1);  // Final temperature
		System.out.println("Initial Temeperature="+T0);
		System.out.println("Final Temperature="+T1);
		K1 = 2.0*(T0+T1)/KM; Km=KM;
        C = initialIndividual.clone(); 
        C.Random();
		CBest = C.clone();
        
        GAMUTATIONID=pGAMutationID;
        GPMUTATIONID=pGPMutationID;
		df=deltaFit;
		LOCALOPTITER=pNUMOL;
		LOID=pIDOL;
		LOCALOPTPROB=pLOptProb;
		try {
        fitnessC = C.fitness();
		fitnessCBest=fitnessC;
		} catch (invalidFitness e) {
		  System.out.println("Unsupported fitness function");
		}

    }
    
/** 
* <p> 
* this method is intended for evolving the algorithm for a given number of iterations
* with an SAP algorithm.
* </p> 
* @param MAXITER an integer with the number of iterations torun in the evolucion
* @return the best {@link GeneticIndividual} found
* @throws {@link invalidCrossover}, {@link invalidMutation} of {@link invalidOptim}
*         in case of unsupported crossover, mutation or local optimization operations.
*/ 	
    public GeneticIndividual evolve(int MAXITER) throws invalidMutation, invalidOptim {
        
        final boolean adaptative=false;
        
        System.out.println("Local Optimization method: "+LOCALOPTPROB+" "+LOCALOPTITER);
		System.out.println("Number of iterations="+MAXITER+" * "+NSUB);
		double CoolingFactor = Math.exp(1.0/MAXITER*Math.log(T1/T0));
		System.out.println("Cooling Factor="+CoolingFactor);
		
		T=T0; double allowedMean=0, deviationMean=0;
		
		try {
		double allowed=0, deltaMean=0;
        for (int it=0;it<MAXITER;it++) {
			
			allowed=0; deltaMean=0;
			for (int sub=0;sub<NSUB;sub++) {
				
				GeneticIndividual C1=C.clone();
				
				
				if (rand.Rand()<GAMUTATIONPROB) {
					C1.mutation(T/K1,GAMUTATIONID);
				} else {
					C1.mutation(0,GPMUTATIONID);
				}
				
				fitnessC1 = C1.fitness();
				delta = fitnessC1 - fitnessC;
				
                                if (LOCALOPTPROB>0)
                                  if (delta<0 || rand.Rand()<LOCALOPTPROB) C1.localOptimization(LOCALOPTITER,LOID);
				
				v = rand.Rand();
				double edt = Math.exp(-delta/T);

				if (delta>0 && v<edt) allowed++;
				if (delta>0) deltaMean+=delta;
				
				if (delta<0 || v<edt) {
					C=C1.clone(); fitnessC=fitnessC1;
					if (fitnessC1 < fitnessCBest) {
						CBest = C1.clone();
						fitnessCBest = fitnessC1;
					}
				}
			}
			
			allowed/=NSUB; deltaMean/=NSUB; double temperatureDeviation=P0+it*(P1-P0)/MAXITER;
			allowedMean+=allowed; deviationMean+=temperatureDeviation;
			
			// Automatic control for temperature
			double errorintegral = deviationMean-allowedMean;
			double Kregul = 1;
			double correccion=Math.exp(Kregul*errorintegral);
			
			if (adaptative) {
                        if (Math.abs(correccion-1)>0.1) {
			  System.out.println("Temperature profile tunning: "+correccion);
			  T*=correccion;
			  K1 = T/Km;
			  // Recover the history
			  deviationMean=0;
			  allowedMean=0;
			}
                        }
			
			System.out.println(
							   "Iteration="+it+
							   ", the best fitness="+fitnessCBest+
							   ", the current fitness="+fitnessC+
							   ", allowable fr.="+allowed+
							   ", fitness mean increasing="+deltaMean+
							   ", current probability factor="+temperatureDeviation+
							   ", integral error="+errorintegral+
							   ", current Temperature="+T
							   );
			

			T=T*CoolingFactor;
        }
        System.out.println("Local Optimizacion of final result: ");
        if (LOCALOPTPROB>0) CBest.localOptimization(LOCALOPTITER,LOID);
        System.out.println("Final Fitness value="+CBest.fitness());
		
		} catch (invalidFitness e) {
		  System.out.println("Unsupported fitness function");
		}
        return CBest.clone();        
    }
}

