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

package keel.Algorithms.Genetic_Rule_Learning.OIGA;


import org.core.Randomize;
import keel.Dataset.*;
import java.util.*;


/**
 * <p>
 * This class implements the SEM algorithm of the OIGA method, which evolves 
 * mono-attribute rules.
 * </p>
 * 
 * <p>
 * @author Written by Julián Luengo Martín 08/02/2007
 * @version 0.2
 * @since JDK 1.5
 * </p>
 */
public class SEM {
	int long_poblacion = 100;
	int n_genes;
	int nAtt;
	double prob_mutacion = 0.01;
	double crossoverRate = 1.0;
	int numberRules;
	int Mu_next;
	int stagnationLimit = 15;
	int generationLimit = 30;
	double survivorsPercent = 0.5;
	RuleSet  poblacion[];
	RuleSet previousPob[];
	RuleSet intermediatePob[];
	int attributeSelected = 0;
	InstanceSet IS;
	InstanceSet IStest;
	double bestCR = -1;
	
	static int attributeOrder[] = new int[Attributes.getInputNumAttributes()];
	
	/**
	 * <p>
	 * Default constructor. No memory allocated
	 * </p>
	 */
	public SEM(){
		poblacion = null;
	}
	
	/**
	 * Parametrized constructor
	 * @param pobSize number of chromosomes (rule sets)
	 * @param numberRules number of rules of each chromosome
	 * @param attSel attribute used to evolve in this SEM
	 * @param IS the data set used for train the SEM
	 */
	public SEM(int pobSize,int numberRules,int attSel,InstanceSet IS){
		poblacion = new RuleSet[pobSize];
		long_poblacion = pobSize;
		this.numberRules = numberRules;
		nAtt = 1;
		n_genes = numberRules*(3*nAtt+1);
		attributeSelected = attSel;
		this.IS = IS;

		for(int i=0;i<pobSize;i++){
			poblacion[i] = new RuleSet(numberRules,nAtt);
			poblacion[i].createRules(numberRules, 1);
			poblacion[i].randomizeRules(IS);
		}
	}
	
	
	/**
	 * Sets the reference data set for this SEM
	 * @param dataset new data set for training
	 */
	public void setIS(InstanceSet dataset){
		IS = dataset;
	}
	
	/**
	 * Set the generations limit
	 * @param iters maximum number of iterations of the SEM
	 */
	public void setGenerationLimit(int iters){
		generationLimit = iters;
	}
	
	/**
	 * Set the parameters for this SEM
	 * @param mutationProb the mutation probability
	 * @param crossoverRate the crossoverProbability between 2 parents
	 * @param survivorsPercent the percent of parents that will be maintained from a generation to next one
	 */
	public void setGAparams(double mutationProb, double crossoverRate, double survivorsPercent){
		this.prob_mutacion = mutationProb;
		this.crossoverRate = crossoverRate;
		this.survivorsPercent = survivorsPercent;
	}
	
	/**
	 * Gets the Classification Rate of this SEM
	 * @return retuns the Classification Rate of this SEM
	 */
	public double getCR(){
		return bestCR;
	}
	
	/**
	 * Gets the i-th chromosome
	 * @param i the rule set we want to retrieve
	 * @return the selected rule set 
	 */
	public RuleSet getChromosome(int i){
		return poblacion[i];
	}

	/**
	 * One-point crossover
	 * @param cr1 index of parent 1 in poblation
	 * @param cr2 index of parent 2 in poblation
	 */
	public void onePointCrossover(int cr1,int cr2){
		RuleSet rule1 = poblacion[cr1];
		RuleSet rule2 = poblacion[cr2];

		//there are 3*number of attribute elements, plus class value in each cromosome
		int cutpoint = Randomize.Randint(0, n_genes);
		int cutpoint_rule = cutpoint/(3*nAtt+1);
		int cutpoint_variable = cutpoint%(3*nAtt+1);
		
		//rule1 is replaced from cutpoint (inclusive) to the end of his rule set
		rule1.copyFromPointtoEnd(rule2, cutpoint_rule, cutpoint_variable);
		//rule2 is replaced from the begining of his rule set to cutpoint (not inclusive)
		rule2.copyFromBegintoPoint(rule1, cutpoint_rule, cutpoint_variable);
		//childs must be evaluated
		rule1.setEvaluated(false);
		rule2.setEvaluated(false);
	}
	
	/**
	 * It performs a one point crossover in the new poblation, using adjacent chromosomes as parents
	 */
	public void crossOver(){
		int parentspreserved = (int)(long_poblacion*survivorsPercent);
		for(int i=0;i<long_poblacion;i=i+2){
			if(Randomize.Rand() < this.crossoverRate && i+1 < long_poblacion)
				onePointCrossover(i,i+1);
		}
	}
	
	/**
	 * Copy the survivorsPercent proportion of the old poblation into the bottom half of 
	 * the new one
	 */
	public void elitism(){
		int parentspreserved = (int)(long_poblacion*survivorsPercent);
//		we keep the best parents and sons
//		Arrays.sort(poblacion,Collections.reverseOrder());
		for(int i=parentspreserved,j=0;i<long_poblacion;i++,j++){
			poblacion[i] = previousPob[j];
		}
	}
	
	/**
	 * Applies mutation in the new poblation
	 */
	public void mutate(){
		int posiciones, i, j;
		double m;

		posiciones=n_genes*long_poblacion;

		if (prob_mutacion>0)
			while (Mu_next<posiciones){
				/* Se determina el cromosoma y el gen que corresponden a la posicion que
			se va a mutar */
				i=Mu_next/n_genes;
				j=Mu_next%n_genes;

				/* Se efectua la mutacion sobre ese gen */
				poblacion[i].mutate(j);

				/* Se marca el cromosoma mutado para su posterior evaluacion */
				poblacion[i].setEvaluated(false);

				/* Se calcula la siguiente posicion a mutar */
				if (prob_mutacion<1)
				{
					m = Randomize.Rand();
					Mu_next += Math.ceil (Math.log(m) / Math.log(1.0 - prob_mutacion));
				}
				else
					Mu_next += 1;
			}

		Mu_next -= posiciones;
	}
	
	/**
	 * Applies a roulette wheel selection
	 */
	public void selection(){
		RuleSet  temp[];
		double probability[] = new double [long_poblacion];
		double total;
		double prob;
		int sel;
		
		temp = new RuleSet[long_poblacion];
		//sort the poblation in order of fitness
		Arrays.sort(poblacion, Collections.reverseOrder());
		
		probability[0] = poblacion[0].getFitness();
		for(int i=1;i<long_poblacion;i++){
			probability[i] = probability[i-1]+poblacion[i].getFitness();
		}
		total = probability[long_poblacion-1];
		for(int i=0;i<long_poblacion;i++){
			probability[i] /= total;
		}

		for(int i=0;i<long_poblacion;i++){
			prob = Randomize.Rand();
			sel = -1;
			for(int j=0;j<long_poblacion && sel==-1;j++){
				if(probability[j]>prob)
					sel = j;
			}
			temp[i] = new RuleSet(poblacion[sel]);
		}
		
		previousPob = poblacion;
		poblacion = temp;
		
	}
	
	/**
	 * Applies a tournament selection, with tournament size of 2
	 */
	public void tournament_selection(){
		int i, j, k, mejor_torneo;

		int tam_torneo = 2;
		int Torneo[] = new int[tam_torneo];
		boolean repetido;
		RuleSet sample[] = new RuleSet[long_poblacion];
		
		for (i=0;i<long_poblacion;i++){
			
			Torneo[0] = Randomize.Randint(0,long_poblacion);
			mejor_torneo=Torneo[0];
	   
			for (j=1;j<tam_torneo;j++)
			{
				do
				{
					Torneo[j] = Randomize.Randint(0,long_poblacion);
					repetido=false; k=0;
					while ((k<j) && (!repetido)){
						if (Torneo[j]==Torneo[k])
							repetido=true;
						else
							k++;
					}
				}
				while (repetido);
	     
				if (poblacion[Torneo[j]].fitness > poblacion[mejor_torneo].fitness)
					mejor_torneo=Torneo[j];
			}
	       
			sample[i] = new RuleSet(poblacion[mejor_torneo]);
		}
		previousPob = poblacion;
		poblacion = sample;
	}
	
	/**
	 * Its evaluate the NEW poblation, using a metric which summarizes the train CR and
	 * test CR
	 */
	public void evaluate(){
		double fitness_train,fitness_test;
		for(int j=0;j<long_poblacion;j++){
			fitness_train = poblacion[j].classify(IS);
			poblacion[j].setEvaluated(false);
			fitness_test = poblacion[j].classify(IStest);
			poblacion[j].fitness = (fitness_train+fitness_test)/2.0;
		}
	}
	
	/**
	 * It runs the Single-attribute Evolution Module (SEM) algorithm to obtain a rule set of ONE attribute
	 */
	public void run(){
		boolean endCondition = false;
		int gen = 0;
		int stagnation = 0;
		
		evaluate();
		while(!endCondition){
			tournament_selection();

			crossOver();

			mutate();
			elitism();
			evaluate();
			
			Arrays.sort(poblacion,Collections.reverseOrder());
			gen++;
			if(bestCR!=poblacion[0].getFitness())
				stagnation = 0;
			else
				stagnation++;
			if(gen>generationLimit || stagnation > stagnationLimit || poblacion[0].getFitness()==1.0)
				endCondition = true;
			bestCR = poblacion[0].getFitness();
		}
	}

	
}

