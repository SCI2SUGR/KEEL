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

package keel.Algorithms.Associative_Classification.ClassifierFuzzyFCRA;

import java.util.*;
import org.core.*;

/**
 * This class contains the population for the genetic algorithm
 *
 * @author Written by Jesus Alcalá (University of Granada) 09/02/2010
 * @version 1.2
 * @since JDK1.5
 */
public class Population {
    ArrayList<Individual> Population;
    ArrayList<Individual> offspring;
    double crossProb, mutProb, wCAR, wV, n1, n2;
    int n_variables, pop_size, lengthSC, Jmax;
    double best_fitness, best_accuracy;
    int[] selected;

	myDataset train;
    DataBase dataBase;
	Apriori apriori;

    /**
     * Maximization
     * @param a int first number
     * @param b int second number
     * @return boolean true if a is better than b
     */
    public boolean BETTER(double a, double b) {
        if (a > b) {
            return true;
        }
        return false;
    }


    /**
     * Default Constructor
     */
	public Population() {
    }


    /**
     * Constructor with parameters
     * @param train myDataset training set
     * @param dataBase DataBase dataBase
     * @param size int population size
     * @param crossProb double crossover probability
     * @param mutProb double mutation probability
     * @param wCAR double relative weight of the classification accuracy rate
     * @param wV double relative weight of the number of fuzzy rules
     * @param n1 double learning rate (Nozaki method)
     * @param n2 double learning rate (Nozaki method)
     * @param Jmax int number of iterations (Nozaki method)
     * @param apriori Apriori an Apriori objects
     */
	public Population(myDataset train, DataBase dataBase, int size, double crossProb, double mutProb, int lengthSC, double wCAR, double wV, double n1, double n2, int Jmax, Apriori apriori) {
        this.dataBase = dataBase;
		this.train = train;
		this.apriori = apriori;
		this.n_variables = dataBase.numVariables();

        this.pop_size = size;
        this.mutProb = mutProb;
        this.crossProb = crossProb;
		this.lengthSC = lengthSC;
		this.wCAR = wCAR;
		this.wV = wV;
		this.n1 = n1;
		this.n2 = n2;
		this.Jmax = Jmax;

        this.selected = new int[this.pop_size];
		Population = new ArrayList<Individual>();
        offspring = new ArrayList<Individual>();
    }

    private void init() {
		for (int i = 0; i < this.pop_size; i++) {
            Individual ind = new Individual(this.wCAR, this.wV, this.lengthSC);
			ind.randomValues();
            Population.add(ind);
        }

		this.best_fitness = 0.0;
    }

    /**
     * Main procedure of the GA
     * @param n_Generations int number of generations
     */
    public void Generation(int n_Generations) {
        init();
        evaluate(Population);
        for (int i = 0; i < n_Generations; i++) {
            selection();
            crossover();
            mutation();
            evaluate(offspring); 
            elitist(i);
        }
    }

    private void selection() {
		int i, j;
		double random, f_min, sum;
        double[] probabilities = new double[Population.size()];
		ArrayList<Selected> vector = new ArrayList<Selected>();

        offspring.clear();
        Collections.sort(Population);
        
		f_min = Population.get(Population.size() - 1).fitness;
        sum = 0;
        for (i = 0; i < Population.size(); i++) {
            probabilities[i] = Population.get(i).fitness - f_min;
            sum += (probabilities[i] - f_min);
        }

        for (i = 0; i < Population.size(); i++) {
            probabilities[i] /= sum;
            Selected s = new Selected(probabilities[i], i);
            vector.add(s);
        }

        Collections.sort(vector);

        for (i = 0; i < Population.size(); i++) {
            random = Randomize.Rand();

            for (j = 0; random < vector.get(j).probability; j++);
            selected[i] = vector.get(j).post;
        }
    }

    private void crossover() {
		double random;
		int pointCross1, pointCross2;

		for (int i = 0; i < Population.size(); i+=2) {
			Individual dad = Population.get(selected[i]).clone();
			Individual mom = Population.get(selected[i + 1]).clone();

			random = Randomize.Rand();
			if (random < this.crossProb) {
				pointCross1 = Randomize.Randint(0, this.lengthSC);
				pointCross2 = Randomize.Randint(this.lengthSC, (this.lengthSC * 2));

				dad.interchangeValues(mom, pointCross1, pointCross2);
			}

			offspring.add(dad);
			offspring.add(mom);
        }
    }

    private void mutation() {
        for (int i = 0; i < offspring.size(); i++) {
            offspring.get(i).mutation(this.mutProb);
        }
    }

    private void elitist(int generation) {
        Collections.sort(Population);
        Individual best = Population.get(0).clone();
        Population.clear();
        Population.add(best);

		Collections.sort(offspring);
        offspring.remove(offspring.size() - 1);
        
		for (int i = 0; i < offspring.size(); i++) {
            Individual indiv = offspring.get(i).clone();
            Population.add(indiv);
        }

        if (BETTER(best.getFitness(), this.best_fitness)) {
			this.best_fitness = best.getFitness();
			this.best_accuracy = best.getAccuracy();
        }

        System.out.println("Best Fitness obtained in generation[" + generation + "]: " + this.best_fitness + "  Accuracy: " + this.best_accuracy);
    }

    private void evaluate (ArrayList<Individual> Individuals) {
		double fit, acc;

		this.best_fitness = Double.NEGATIVE_INFINITY;
		best_accuracy = 0.0;

        for (int i = 0; i < Individuals.size(); i++) {
			if (Individuals.get(i).isNew())  Individuals.get(i).evaluate(this.apriori, this.n1, this.n2, this.Jmax);
			fit = Individuals.get(i).getFitness();

            if (BETTER(fit, this.best_fitness)) {
                this.best_fitness = fit;
				this.best_accuracy = Individuals.get(i).getAccuracy();
            }
        }
    }

    /**
     * It returns the best RB found so far
     * @return RuleBase the best RB found so far
     */
    public RuleBase getBestRB() {
		RuleBase ruleBase;

        Collections.sort(Population);
		ruleBase = Population.get(0).generateRB(this.apriori, this.n1, this.n2, this.Jmax);

		return ruleBase;
	}

    /**
     * It returns the best minimum support
     * @return double the best minimum support found so far
     */
    public double getBestMinFS() {
        Collections.sort(Population);
		return Population.get(0).getMinFS();
	}

    /**
     * It returns the best minimum confidence
     * @return double the best minimum confidence found so far
     */
    public double getBestMinFC() {
        Collections.sort(Population);
		return Population.get(0).getMinFC();
	}

}

