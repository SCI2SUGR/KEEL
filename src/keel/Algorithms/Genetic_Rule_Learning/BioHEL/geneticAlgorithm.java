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

package keel.Algorithms.Genetic_Rule_Learning.BioHEL;

import java.util.Vector;
import org.core.Randomize;

public class geneticAlgorithm {
	
	int currentIteration;
	int popSize;
	classifierFactory cf;
	classifier[] population, offspringPopulation;
	rank[] populationRank;
	int flagResetBest;
	int numVersions;
	classifier[] best;
	int tournamentSize;
	double maxFitness;
	double minFitness;
	
	
	public geneticAlgorithm(classifierFactory pCF){
		cf = pCF;
		tournamentSize = Parameters.tournamentSize;
		numVersions = Parameters.is.numVersions();
		best = new classifier [numVersions+1];
		for(int i=0;i<numVersions;i++) best[i] = null;

		initializePopulation();
		doFitnessComputations();
		createPopulationRank();
		checkBestIndividual();
	}
	
//*******************************************************************************************************************************

	public void initializePopulation(){
		int i;

		popSize = Parameters.popSize;

		population = new classifier[popSize];
		offspringPopulation = new classifier[popSize];


		for ( i = 0; i < popSize; i++ )
			population[i] = cf.createClassifier();

		populationRank = new rank[popSize];
		for( i = 0 ; i < popSize ; ++i )
			populationRank[i] = new rank();
		
		flagResetBest = 0;
		currentIteration = 0;
	}

//*******************************************************************************************************************************

	public void doIterations(int n){
		for (; n > 0; n--) {

			selectionAlgorithm();
			crossover();
			mutation();
			replacementAlgorithm();
			createPopulationRank();
			checkBestIndividual();
			
			currentIteration++;
		}
	}
	
//*******************************************************************************************************************************

	// --------------------- SELECTION
	public void selectionAlgorithm(){
		
		scalingAlgorithm();

		switch (Parameters.selectionAlg) {
		
			case Parameters.TOURNAMENT_WOR:
				TournamentSelectionWOR();
				break;
		
			case Parameters.TOURNAMENT:
			default:
				TournamentSelection();
				break;	
		}

		classifier[] tempPop = new classifier[popSize];
		System.arraycopy(population, 0, tempPop, 0, popSize);
		System.arraycopy(offspringPopulation, 0, population, 0, popSize);
		System.arraycopy(tempPop, 0, offspringPopulation, 0, popSize);
	}

	public void TournamentSelectionWOR(){
		int i, j, winner, candidate;

		Sampling samp = new Sampling(popSize);
		for (i = 0; i < popSize; i++) {
			//There can be only one
			winner=samp.getSample();
			for (j = 1; j < tournamentSize; j++) {
				candidate=samp.getSample();
				if(population[candidate].compareToIndividual(population[winner],Parameters.optimizationMethod)>0) {
					winner = candidate;
				}
			}
			offspringPopulation[i]=cf.cloneClassifier(population[winner]);
		}
	}

	public void TournamentSelection(){
		
		int i, j, winner, candidate;

		for ( i = 0; i < popSize; i++ ){
			//There can be only one
			winner = Randomize.Randint(0,popSize);
			
			for ( j = 1 ; j < tournamentSize; j++){
				candidate = Randomize.Randint(0,popSize);
				if(population[candidate].compareToIndividual(population[winner],Parameters.optimizationMethod)>0){
					winner = candidate;
				}
			}
			
			offspringPopulation[i] = cf.cloneClassifier(population[winner]);
		}
	}
	
//*******************************************************************************************************************************

	// ----------------- SCALING
	void scalingAlgorithm(){
		int i;

		for( i = 0 ; i < popSize ; i++ ){
			double value = population[i].getFitness();
			value = identityScaling(value);
			population[i].setScaledFitness(value);
		}
	}

	double identityScaling(double value){
		double res;

		if(Parameters.optimizationMethod==Parameters.MAXIMIZE) res=value;
		else res=maxFitness+minFitness-value;
			
		if(minFitness<0) res-=minFitness;
		
		return res;
	}

//*******************************************************************************************************************************
	

	//--------------------------------- CROSSOVER
	
	public void crossover(){
				
		int j, countCross = 0;

		Sampling samp = new Sampling(popSize);
		int p1 = -1;
		
		for ( j = 0 ; j < popSize; j++ ){
			
			if( Randomize.Rand() < Parameters.crossoverProbability){
				
				if (p1 == -1){
					p1 = samp.getSample();
				}
				
				else{
					int p2 = samp.getSample();
					crossTwoParents(p1, p2, countCross, countCross+1);
					countCross += 2;
					p1 = -1;
				}
			}
			
			else{
				crossOneParent(samp.getSample(), countCross++);
			}
		}
		
		if (p1 != -1){
			crossOneParent(p1, countCross++);
		}
	
	}
	
	public void crossTwoParents(int parent1, int parent2, int son1,int son2){
		
		offspringPopulation[son1] = cf.cloneClassifier(population[parent1]);
		offspringPopulation[son2] = cf.cloneClassifier(population[parent2]);

		population[parent1].crossover(population[parent2], offspringPopulation[son1], offspringPopulation[son2]);
	}


	public void crossOneParent(int parent, int son){
	
		offspringPopulation[son] = cf.cloneClassifier(population[parent]);
	}

//*******************************************************************************************************************************
	
	// --------------- MUTATION

	public void mutation(){

		individualMutation();
		specialStages();		
	}

	
	void specialStages(){
		
		int i,j;
		int numStages=offspringPopulation[0].numSpecialStages();
		for(i=0;i<numStages;i++) {
			for(j=0;j<popSize; j++) {
				offspringPopulation[j].doSpecialStage(i);
			}
		}
	}

	
	public void individualMutation(){
		int i;
		for (i = 0; i < popSize; i++) {
			if(Randomize.Rand()<Parameters.mutationProbability) {
				offspringPopulation[i].mutation();
			}
		}
		
	}

//*******************************************************************************************************************************


//-------------------- REMPLAZAMENT
	
	
	public void replacementAlgorithm(){
	
		totalReplacement();
		doFitnessComputations();
		createPopulationRank();
		if(Parameters.elitismEnabled) doElitism();
	}

	
	public void doElitism(){
		int i,j;

		for(i=0;i<numVersions;i++) {
			if(best[i]!=null) {
				best[i].fitnessComputation();
			}
		}

		int numV=numVersions;
		Vector<Integer> priorities = new Vector<Integer>(popSize+numV);
		for(i=0;i<popSize;i++) priorities.addElement(populationRank[i].pos);
		for(i=0;i<numV;i++) {
			if(best[i]!=null) {
				int size=priorities.size();
				for(j=0;j<size;j++) {
					classifier ind;
					int pos=priorities.get(j);
					if(pos>=popSize) {
						ind=best[pos-popSize];
					} else {
						ind=population[pos];
					}

					if(best[i].compareToIndividual(ind,Parameters.optimizationMethod)>0) {
						priorities.insertElementAt(popSize+i,j);
						break;
					}
				}
				if(j==size) {
					priorities.addElement(popSize+i);
				}
			}
		}

		Vector<Integer> elite = new Vector<Integer>();
		for(i=0;i<popSize;i++) {
			if(priorities.get(i)>=popSize) {
				elite.addElement(priorities.get(i)-popSize);
			}
		}
		int index=0;
		int size=priorities.size();
		for(i=popSize;i<size;i++) {
			if(priorities.get(i)<popSize) {
				int pos=priorities.get(i);
				population[pos]= cf.cloneClassifier(best[elite.get(index++)]);
			}
		}

		flagResetBest=0;
	}


	
	public void totalReplacement(){

		classifier[] tempPop = new classifier[popSize];
		System.arraycopy(population, 0, tempPop, 0, popSize);
		System.arraycopy(offspringPopulation, 0, population, 0, popSize);
		System.arraycopy(tempPop, 0, offspringPopulation, 0, popSize);	
	}


//*******************************************************************************************************************************
	
	classifier[] getPopulation() { return population; }
	classifier getBest() { return best[Parameters.is.getCurrentVersion()]; }
	classifier getWorst() {return population[populationRank[popSize - 1].pos];}
	rank[] getPopulationRank() { return populationRank; }

//*******************************************************************************************************************************

	public void createPopulationRank(){
		
		int i;
		
		double[] vectorQS = new double[popSize];
		for( i = 0 ; i < popSize ; ++i )
			vectorQS[i] = population[i].fitness;
		
		int optionQS = (Parameters.optimizationMethod == Parameters.MINIMIZE) ? Quicksort.LOWEST_FIRST: Quicksort.HIGHEST_FIRST;
		int[] posQS = Quicksort.sort(vectorQS, popSize, optionQS);
		
		for( i = 0; i < popSize; i++ ){
			populationRank[i].pos = posQS[i];
			populationRank[i].ind = cf.cloneClassifier(population[posQS[i]]);
		}
	}
	
//*******************************************************************************************************************************

	public void doFitnessComputations(){
		int i;

		for(i=0;i<popSize;i++) {
			population[i].fitnessComputation();
		}

		maxFitness=minFitness=population[0].getFitness();
		for(i=1;i<popSize;i++) {
			double fitness=population[i].getFitness();
			if(fitness>maxFitness) {
				maxFitness=fitness;
			}
			if(fitness<minFitness) {
				minFitness=fitness;
			}
		}
	}

	public void resetBest(){
		flagResetBest=1;
	}

	public void checkBestIndividual(){
		
		int currVer = Parameters.is.getCurrentVersion();

		if (best[currVer]==null) {
			best[currVer] = cf.cloneClassifier(populationRank[0].ind);
		} else {
			best[currVer].fitnessComputation();
			if (best[currVer].compareToIndividual(populationRank[0].ind, Parameters.optimizationMethod) < 0) {
				best[currVer] = cf.cloneClassifier(populationRank[0].ind);
			}
		}
	}

}
