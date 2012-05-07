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

package keel.Algorithms.Associative_Classification.ClassifierFuzzyFARCHD;

/**
 * <p>Title: Population</p>
 * <p>Description: Class for the CHC algorithm</p>
 * <p>Copyright: KEEL Copyright (c) 2010</p>
 * <p>Company: KEEL </p>
 * @author Written by Jesus Alcalá (University of Granada) 09/02/2011
 * @version 1.2
 * @since JDK1.6
 */

import java.util.*;
import org.core.*;

public class Population {
    ArrayList<Individual> Population;
    double alpha, w1, L, Lini;
    int n_variables, pop_size, maxTrials, nTrials, BITS_GEN;
    double best_fitness, best_accuracy;
	int [] selected;

	myDataset train;
    DataBase dataBase;
	RuleBase ruleBase;

    public boolean BETTER(double a, double b) {
        if (a > b) {
            return true;
        }
        return false;
    }

    public Population() {
    }

/**
* <p>
* Builder
* </p>
* @param train Training dataset
* @param dataBase Data Base
* @param RuleBase Rule set
* @param size Population size
* @param BITS_GEN Bits per gen
* @param maxTrials Maximum number of evaluacions
* @param alpha Parameter alpha
* @return A population object
*/
    public Population(myDataset train, DataBase dataBase, RuleBase ruleBase, int size, int BITS_GEN, int maxTrials, double alpha) {
        this.dataBase = dataBase;
		this.train = train;
		this.ruleBase = ruleBase;
		this.BITS_GEN = BITS_GEN;

		this.n_variables = dataBase.numVariables();
        this.pop_size = size;
		this.alpha = alpha;
		this.maxTrials = maxTrials;
		this.Lini = ((dataBase.getnLabelsReal() * BITS_GEN) + ruleBase.size()) / 4.0;
		this.L = this.Lini;
		this.w1 = this.alpha * (double) ruleBase.size();

		Population = new ArrayList<Individual>();
		selected = new int[this.pop_size];
    }

/**
* <p>
* Run the CHC algorithm (Stage 3) 
* </p>
* @return void
*/
    public void Generation() {
        init();
        this.evaluate(0);

		do {
            this.selection();
            this.crossover();
			this.evaluate(this.pop_size);
            this.elitist();
			if (!this.hasNew()) {
				this.L--;
				if (this.L < 0.0) {
					this.restart();
				}
			}
		} while (this.nTrials < this.maxTrials);
    }


    private void init() {
		Individual ind;

        ind = new Individual(this.ruleBase, this.dataBase, this.w1);
		ind.reset();
        Population.add(ind);
		for (int i = 1; i < this.pop_size; i++) {
            ind = new Individual(this.ruleBase, this.dataBase, this.w1);
			ind.randomValues();
            Population.add(ind);
        }

		this.best_fitness = 0.0;
		this.nTrials = 0;
    }


	private void evaluate (int pos) {
        for (int i = pos; i < this.Population.size(); i++)  this.Population.get(i).evaluate();
		this.nTrials += (this.Population.size() - pos);
    }


    private void selection() {
		int i, aux, random;

		for (i=0; i < this.pop_size; i++)  this.selected[i] = i;

		for (i=0; i < this.pop_size; i++) {
			random = Randomize.Randint(0, this.pop_size);
			aux = this.selected[random];
			this.selected[random] = selected[i];
			this.selected[i] = aux;
		}
    }

    private void xPC_BLX(double d, Individual son1, Individual son2) {
		son1.xPC_BLX(son2, d);
	}

    private void Hux(Individual son1, Individual son2) {
		son1.Hux(son2);
	}

    private void crossover() {
		int i;
		double dist;
		Individual dad, mom, son1, son2;

		for (i = 0; i < this.pop_size; i+=2) {
			dad = this.Population.get(this.selected[i]);
			mom = this.Population.get(this.selected[i + 1]);

			dist = (double) dad.distHamming(mom, BITS_GEN);
			dist /= 2.0;

			if (dist > this.L) {
				son1 = dad.clone();
				son2 = mom.clone();
				
				this.xPC_BLX(1.0, son1, son2);
				this.Hux(son1, son2);
				
				son1.onNew();
				son2.onNew();
				
				this.Population.add(son1);
				this.Population.add(son2);
			}
        }
    }


    private void elitist() {
        Collections.sort(this.Population);
		while (this.Population.size() > this.pop_size)  this.Population.remove(this.pop_size);

		this.best_fitness = this.Population.get(0).getFitness();
    }

    private boolean hasNew() {
		int i;
		boolean state;
		Individual ind;

		state = false;
		
		for (i=0; i < this.pop_size; i++) {
			ind = this.Population.get(i);
			if (ind.isNew()) {
				ind.offNew();
				state = true;
			}
		}

		return (state);
	}


    private void restart() {
		int i, dist;
		Individual ind;

		this.w1 = 0.0;

        Collections.sort(this.Population);
		ind = this.Population.get(0).clone();
		ind.setw1(this.w1);

		this.Population.clear();
		this.Population.add(ind);

		for (i = 1; i < this.pop_size; i++) {
            ind = new Individual(this.ruleBase, this.dataBase, this.w1);
			ind.randomValues();
            Population.add(ind);
        }

        this.evaluate(0);
		this.L = this.Lini;
    }

/**
* <p>
* Return the best individual in the population 
* </p>
* @return Return the rule set of the best individual in the population
*/
    public RuleBase getBestRB() {
		RuleBase ruleBase;

        Collections.sort(this.Population);
		ruleBase = Population.get(0).generateRB();

		return ruleBase;
	}
}
