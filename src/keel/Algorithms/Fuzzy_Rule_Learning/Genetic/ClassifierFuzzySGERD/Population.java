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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierFuzzySGERD;

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
 * @author Modified by Jesus Alcalá (University of Granada) 24/05/2009
 * @version 1.2
 * @since JDK1.5
 */
import java.util.*;
import org.core.*;

public class Population {
    ArrayList<RuleBase> population;
    ArrayList<RuleBase> populationAux;
    RuleBase finalRuleBase;
    int Q, nClasses, totalLabels;
    myDataset train;
    double best_clasif, bestAccuracy;

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
     * @param ruleBase RuleBase a given rule base
     * @param Q int a given factor
     * @param train myDataset the training set
     * @param totalLabels int the total number of fuzzy labels
     */
    public Population(RuleBase ruleBase, int Q, myDataset train, int totalLabels) {
        this.Q = Q;
        this.train = train;
        this.nClasses = train.getnClasses();
        this.totalLabels = totalLabels;
        this.finalRuleBase = ruleBase.cloneEmpty();

        this.initialization(ruleBase);
    }

    /**
     * It initilises the population
     * @param ruleBase RuleBase a given Rule base
     */
    private void initialization(RuleBase ruleBase) {
        int i, j;
        Rule rule;
        RuleBase base, baseAux;

        this.population = new ArrayList<RuleBase>();
        this.populationAux = new ArrayList<RuleBase>();

        for (i = 0; i < this.nClasses; i++) {
            this.population.add(ruleBase.cloneEmpty());
            this.populationAux.add(ruleBase.cloneEmpty());
        }

        for (i = 0; i < ruleBase.size(); i++) {
            rule = (ruleBase.get(i)).clone();
            base = this.population.get(rule.getClas());
            base.add(rule);
        }

        for (i = 0; i < this.nClasses; i++) {
            base = this.population.get(i);
            baseAux = this.populationAux.get(i);
            base.sort();
            base.selection(baseAux, this.Q);
        }
    }

    /**
     * It performs the generation process
     */
    public void Generation() {
        int j;

        int[] nRules = new int[nClasses];
        do {
            for (j = 0; j < this.nClasses; j++) {
                nRules[j] = (this.population.get(j)).size();
            }
            for (j = 0; j < this.nClasses; j++) {
                this.reproduction(j, nRules[j]);
            }
            this.elitist();
        } while (this.stop());
    }

    /**
     * It computes the stopping criterion
     * @return boolean true if no more generations must be carried out, false otherwise
     */
    private boolean stop() {
        int i, j;
        boolean stop;
        RuleBase ruleBase;
        Rule rule;

        stop = true;

        for (i = 0; i < this.nClasses; i++) {
            ruleBase = population.get(i);
            for (j = 0; j < ruleBase.size(); j++) {
                rule = ruleBase.get(j);
                if (rule.isNew()) {
                    stop = false;
                    rule.offNew();
                }
            }
        }

        return (stop);
    }


    /**
     * Reproduction step
     * @param iClass int class id
     * @param nRules int the number of rules to create
     */
    private void reproduction(int iClass, int nRules) {
        RuleBase ruleBase, ruleBaseAux;
        Rule rulej, rulep;
        int j, p;

        ruleBase = this.population.get(iClass);
        ruleBaseAux = this.populationAux.get(iClass);

        for (j = 0; j < nRules; j++) {
            rulej = ruleBase.get(j);
            p = Randomize.Randint(0, ruleBase.size()-1);
            if (j != p) {
                rulep = ruleBase.get(p);
                crossover(rulej, rulep);
            } 
			else if (ruleBaseAux.size() > 0) {
                p = Randomize.Randint(0, ruleBaseAux.size()-1);
                rulep = ruleBaseAux.get(p);
                mutation(rulej, rulep);
            }
        }
    }

    /**
     * Crossover operator
     * @param rulej Rule First rule to cross
     * @param rulep Rule Second rule to cross
     */
    private void crossover(Rule rulej, Rule rulep) {
        RuleBase ruleBase;
        Rule rule;
        int antecedent;

        antecedent = rulep.getPosActive(Randomize.RandintClosed(1, rulep.getActive()-1));

        if (!rulej.isActive(antecedent)) {
            for (int i = 0; i < this.totalLabels; i++) {
                rule = rulej.clone();
                rule.setLabel(antecedent, i);
                rule.setConsequent(this.train);
                rule.evaluation(this.train);
                rule.onNew();
                ruleBase = population.get(rule.getClas());
                ruleBase.add(rule);
            }
        }
    }

    /**
     * Mutation operator
     * @param rulej Rule First rule to mutate
     * @param rulep Rule Second rule to mutate
     */
    private void mutation(Rule rulej, Rule rulep) {
        RuleBase ruleBase;
        Rule rule;

        if (!rulej.isActive(rulep.firstActive)) {
            for (int i = 0; i < this.totalLabels; i++) {
                rule = rulej.clone();
                rule.setLabel(rulep.firstActive, i);
                rule.setConsequent(this.train);
                rule.evaluation(this.train);
                rule.onNew();
                ruleBase = population.get(rule.getClas());
                ruleBase.add(rule);
            }
        }
    }

    /**
     * Elitism: only the best rules remain in the popluation
     */
    private void elitist() {
        RuleBase ruleBase;
        for (int i = 0; i < this.nClasses; i++) {
            ruleBase = population.get(i);
            ruleBase.sort();
            ruleBase.removeRules(this.Q);
        }
    }

    /*
     private void clasifica(ArrayList<Individuo> individuos, int generation) {
            boolean entrar = false;
            for (int i = 0; i < individuos.size(); i++) {
                double acc = individuos.get(i).clasifica();
                if (acc > best_clasif) {
                    best_clasif = acc;
                    entrar = true;
                }
            }
            if (entrar) {
                System.out.println("Best Accuracy obtained in generation[" + generation + "]: " + best_clasif);
            }
        }
     */

    /**
     * It writes the best rule base obtained into file
     * @param filename String the name of the file
     */
    public void writeFile(String filename) {
        this.finalRuleBase.saveFile(filename);
    }


    /**
     * It returns the best rule base obtained
     * @return RuleBase the best rule base obtained
     */
    public RuleBase bestRB() {
        this.selectRules();
        return this.finalRuleBase.clone();
    }

    /**
     * It select the best rules in the population
     */
    private void selectRules() {
        int i, j, nActive;
        int[] classActive;
        double accuracy;
        RuleBase ruleBase;
        Rule rule;

        while (this.finalRuleBase.size() > 0) {
            this.finalRuleBase.remove(0);
        }
        for (i = 0; i < this.nClasses; i++) {
            ruleBase = population.get(i);
            if (ruleBase.size() > 0) {
                this.finalRuleBase.add((ruleBase.get(0)).clone());
            }
        }
        this.bestAccuracy = this.finalRuleBase.classify();

        nActive = 0;
        classActive = new int[this.nClasses];
        for (i = 0; i < nClasses; i++) {
            ruleBase = population.get(i);
            if (ruleBase.size() > 1) {
                classActive[i] = 1;
                nActive++;
            } else {
                classActive[i] = 0;
            }
        }

        for (i = 1; nActive > 0; i++) {
            for (j = 0; j < this.nClasses; j++) {
                if (classActive[j] > 0) {
                    ruleBase = population.get(j);
                    rule = (ruleBase.get(i)).clone();
                    this.finalRuleBase.add(rule);
                    accuracy = this.finalRuleBase.classify();
                    if (accuracy > this.bestAccuracy) {
                        this.bestAccuracy = accuracy;
                    } else {
                        this.finalRuleBase.remove(this.finalRuleBase.size() - 1);
                        classActive[j] = 0;
                        nActive--;
                    }
                    if (ruleBase.size() == (i + 1)) {
                        classActive[j] = 0;
                        nActive--;
                    }
                }
            }
        }

        this.finalRuleBase.evaluate();
    }

}

