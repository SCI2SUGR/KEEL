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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Fuzzy_Ish_Hybrid;

/**
 * <p>Title: Populatoin</p>
 *
 * <p>Description: This class contains the population for the genetic algorithm</p>
 *
 * <p>Copyright: KEEL Copyright (c) 2008</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Alberto Fernández (University of Granada) 29/10/2007
 * @author Modified by Alberto Fernández (University of Granada) 18/12/2008
 * @version 1.2
 * @since JDK1.5
 */
import java.util.*;
import org.core.*;

public class Population {
    ArrayList<RuleBase> population;
    ArrayList<RuleBase> offspring;
    myDataset train;
    DataBase dataBase;
    double crossProb, mutProb, p_DC, michProb;
    int n_variables, n_rules, n_replace, pop_size;
    int ruleWeight, compType, infType;
    double best_accuracy;
    int[] selected;

    /**
     * Maximization
     * @param a int first number
     * @param b int second number
     * @return boolean true if a is better than b
     */
    public boolean BETTER(int a, int b) {
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
     * @param nRules int number of rules
     * @param crossProb double crossover probability
     * @param ruleWeight int rule weight id
     * @param compType int compatibility degree id
     * @param infType int inference type id
     * @param p_DC double don't care probability
     * @param michProb double probability of performing a michigan iteration
     */
    public Population(myDataset train, DataBase dataBase, int size, int nRules,
                      double crossProb, int ruleWeight, int compType,
                      int infType,
                      double p_DC, double michProb) {
        this.dataBase = dataBase;
        this.crossProb = crossProb;
        this.n_rules = nRules;
        n_replace = size - 1;
        population = new ArrayList<RuleBase>();
        offspring = new ArrayList<RuleBase>();
        this.train = train;
        n_variables = dataBase.numVariables();
        this.ruleWeight = ruleWeight;
        this.compType = compType;
        this.infType = infType;
        this.p_DC = p_DC;
        best_accuracy = 0.0;
        selected = new int[n_replace];
        this.pop_size = size;
        mutProb = 1.0 / n_variables;
        this.michProb = michProb;
    }

    /**
     * Main procedure of the GA
     * @param n_generations int number of generations
     */
    public void Generation(int n_generations) {
        init();
        evaluate(population);
        classify(0);
        for (int gens = 0; (gens < n_generations) && (best_accuracy < 1.0);
                        gens++) {
            selection();
            cross();
            mutation();
            michiganIteration(); //for approximately half of the chromosomes
            evaluate(offspring);
            replace();
            classify(gens);
        }
        delete();
    }

    /**
     * Initialization step
     *
     * Build the rule-set / population by means of a heuristic funtion
     */
    private void init() {
        int examples[] = new int[n_rules];
        int long_tabla_tra = train.size();
        int selected[] = new int[long_tabla_tra];
        int i, j, total;

        for (i = 0; i < long_tabla_tra; i++) {
            selected[i] = i;
        }
        for (i = 0; i < pop_size; i++) {
            total = long_tabla_tra;
            for (j = 0; j < n_rules &&(j<total); j++) {
                int selection = Randomize.RandintClosed(0, total - 1);
                examples[j] = selected[selection];
                total--;
                selected[selection] = selected[long_tabla_tra - j - 1];
            }
            RuleBase br = new RuleBase(examples, n_rules, dataBase, train,
                                       ruleWeight,
                                       infType, compType, crossProb, p_DC);
            population.add(br);
        }
    }

    /**
     * Evaluation function
     * @param chromosomes ArrayList the population
     */
    private void evaluate(ArrayList<RuleBase> chromosomes) {
        for (int i = 0; i < chromosomes.size(); i++) {
            RuleBase chromosome = chromosomes.get(i);
            if (chromosome.n_e) {
                chromosome.evaluate();
            }
        }

    }

    /**
     * Classification procedure
     * @param generation int Number of the current generation
     */
    private void classify(int generation) {
        Collections.sort(population);
        if (population.get(0).getAccuracy() > best_accuracy) {
            best_accuracy = population.get(0).getAccuracy();
            System.out.println("Best Accuracy obtained in generation[" +
                               generation +
                               "]: " + best_accuracy);
        }
    }

    /**
     * Binary tournament between chromosomes
     * @param index Is the index of the set in which the best chromosome will be inserted
     * @param crom1 Is the index of the first individual inside the offspring
     * @param crom2 Is the index of the second individual inside the offspring
     */
    void tournament(int index, int crom1, int crom2) {
        if (BETTER(population.get(crom1).fitness, population.get(crom2).fitness)) {
            selected[index] = crom1;
        } else {
            selected[index] = crom2;
        }
    }

    /**
     * Selection step
     */
    private void selection() {
        int random1, random2, i, init;
        init = 0;
        for (i = init; i < n_replace; i++) {
            random1 = Randomize.RandintClosed(0, population.size() - 1); //One is selected randomly
            do {
                random2 = Randomize.RandintClosed(0, population.size() - 1); //I select other randomly
            } while (random1 == random2);
            tournament(i, random1, random2); //the best of the two individuals is inserted at the 'i'-th position
        }

    }

    /**
     * Crossover operator
     */
    private void cross() {
        for (int i = 0; i < n_replace - 1; i += 2) { //n_replace is even
            if (this.crossProb > Randomize.Rand()) {
                RuleBase offspring1 = new RuleBase(dataBase, train, ruleWeight,
                                              infType,
                                              compType, crossProb, p_DC);
                RuleBase offspring2 = new RuleBase(dataBase, train, ruleWeight,
                                              infType,
                                              compType, crossProb, p_DC);
                RuleBase dad = population.get(selected[i]);
                RuleBase mom = population.get(selected[i + 1]);
                for (int j = 0; j < n_rules; j++) {
                    if (Randomize.Rand() > 0.5) { //substring-wise uniform crossover
                        offspring1.ruleBase.add(new Rule(dad.ruleBase.get(j)));
                        offspring2.ruleBase.add(new Rule(mom.ruleBase.get(j)));
                    } else {
                        offspring2.ruleBase.add(new Rule(dad.ruleBase.get(j)));
                        offspring1.ruleBase.add(new Rule(mom.ruleBase.get(j)));
                    }
                }
                offspring.add(offspring1);
                offspring.add(offspring2);
            } else {
                offspring.add(population.get(i).clone());
                offspring.add(population.get(i + 1).clone());
            }
        }
        offspring.add(population.get(n_replace - 1).clone());
    }

    /**
     * Mutation operator
     */
    private void mutation() {
        for (int i = 0; i < n_replace; i++) {
            offspring.get(i).mutate();
        }
    }

    /**
     * Michigan iteration step. It is applied according to the "michProb" parameter
     */
    private void michiganIteration() {
        for (int i = 0; i < n_replace; i++) { //For all offspring generated during the Pittsburgh process
            if (Randomize.Rand() < michProb) { //if it is selected...
                offspring.get(i).michigan();
            }
        }
    }

    /**
     * Replacement step. Elitism
     */
    private void replace() {
        Collections.sort(population);
        RuleBase best = population.get(0).clone();
        population.clear();
        population.add(best);
        for (int i = 0; i < n_replace; i++) {
            population.add(offspring.get(i).clone());
        }
        offspring.clear();
    }

    /**
     * We delete duplicated rules and with a 0 value in the rule weight
     */
    private void delete() {
        for (int i = 0; i < population.size(); i++) {
            population.get(i).delete();
        }
    }

    /**
     * Writes the best population / RB into file
     * @param filename String the name of the file
     */
    public void writeFile(String filename) {
        Collections.sort(population);
        population.get(0).writeFile(filename);
    }

    /**
     * It returns the best RB found so far
     * @return RuleBase the best RB found so far
     */
    public RuleBase bestRB() {
        Collections.sort(population);
        return population.get(0).clone();
    }

}

