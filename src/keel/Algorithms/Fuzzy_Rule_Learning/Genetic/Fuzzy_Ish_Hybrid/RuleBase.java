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
 * <p>Title: RuleBase </p>
 *
 * <p>Description: Fuzzy Rule Base </p>
 *
 * <p>Copyright: KEEL Copyright (c) 2008</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Alberto Fernández (University of Granada) 29/10/2007
 * @author Modified by Alberto Fernández (University of Granada) 03/09/2009
 * @version 1.5
 * @since JDK1.5
 */

import java.util.*;
import org.core.*;

public class RuleBase implements Comparable {

    ArrayList<Rule> ruleBase;
    ArrayList<Rule> offspring;
    DataBase dataBase;
    myDataset train;
    int n_variables, ruleWeight, infType, compType, fitness, n_replace_mich,
            n_offsprings;
    boolean n_e;
    int nonCovered, examplesUncovered[];
    double crossProb, mutProb, p_DC;
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
     * Default builder
     */
    public RuleBase() {

    }

    /**
     * Builds an object for the Rule Base
     * @param dataBase DataBase Data Base
     * @param train myDataset Training set
     * @param ruleWeight int Code for the rule weight
     * @param infType int Code for the inference system
     * @param compType int Code for the compatibility degree computation
     * @param crossProb double Crossover probability
     * @param p_DC double Don't Care probability
     */
    public RuleBase(DataBase dataBase, myDataset train, int ruleWeight,
                    int infType, int compType, double crossProb, double p_DC) {
        ruleBase = new ArrayList<Rule>();
        this.dataBase = dataBase;
        this.train = train;
        this.n_variables = dataBase.numVariables();
        this.ruleWeight = ruleWeight;
        this.compType = compType;
        this.infType = infType;
        this.n_e = true;
        examplesUncovered = new int[train.size()];
        this.p_DC = p_DC;
        this.crossProb = crossProb;
        mutProb = 1.0 / n_variables;
    }

    /**
     * Builds an object for the Rule Base with an heuristic
     * @param examples int[] Position of the training instances for the heuristic
     * @param n_Rules int Number of rules in the Rule Base
     * @param dataBase DataBase Data Base
     * @param train myDataset Training set
     * @param ruleWeight int Code for the rule weight
     * @param infType int Code for the inference system
     * @param compType int Code for the compatibility degree computation
     * @param crossProb double Crossover probability
     * @param p_DC double Don't Care probability
     */
    public RuleBase(int[] examples, int n_Rules, DataBase dataBase,
                    myDataset train,
                    int ruleWeight, int infType, int compType, double crossProb,
                    double p_DC) {
        ruleBase = new ArrayList<Rule>();
        this.dataBase = dataBase;
        this.train = train;
        this.n_variables = dataBase.numVariables();
        this.ruleWeight = ruleWeight;
        this.compType = compType;
        this.infType = infType;
        for (int i = 0; i < n_Rules; i++) {
            Rule r = new Rule(this.dataBase, compType, train.getNominals());
            r.buildHeuristic(train, examples[i], p_DC);
            ruleBase.add(r);
        }
        n_e = true;
        examplesUncovered = new int[train.size()];
        this.p_DC = p_DC;
        this.crossProb = crossProb;
        mutProb = 1.0 / n_variables;
    }

    /**
     * Prints the Rule Base into a String object
     * @return String the RB
     */
    public String printString() {
        int i, j;
        String[] varNames, classNames;
        varNames = train.varNames();
        classNames = train.classNames();
        String cadena = new String("");
        cadena += "@Number of rules: " + ruleBase.size() + "\n\n";
        for (i = 0; i < ruleBase.size(); i++) {
            Rule r = ruleBase.get(i);
            cadena += (i + 1) + ": ";
            boolean dontCare = false;
            for (j = 0; j < n_variables; j++) {
                //cadena += r.dataBase.print(j, r.antecedente[j]);
                if ((r.antecedent[j] != 14)||(r.antecedent[j] != -1)) {
                    if (dontCare) {
                        cadena += " AND ";
                        dontCare = false;
                    }
                    cadena += varNames[j] + " IS " +
                            r.dataBase.print(j, r.antecedent[j]);
                    dontCare = true;
                }
            }
            cadena += ": " + classNames[r.clas] + " with Rule Weight: " +
                    r.weight +
                    "\n";
        }

        return (cadena);
    }

    /**
     * It writes the rule base into an ouput file
     * @param filename String the name of the output file
     */
    public void writeFile(String filename) {
        String outputString = new String("");
        outputString = printString();
        Files.writeFile(filename, outputString);
    }

    /**
     * Fuzzy Reasoning Method
     * @param example double[] the input example
     * @return int the predicted class label (id)
     */
    public int FRM(double[] example) {
        if (this.infType == Fuzzy_Ish.WINNING_RULE) {
            return FRM_WR(example);
        } else {
            return FRM_AC(example);
        }
    }

    /**
     * Winning Rule FRM
     * @param example double[] the input example
     * @return int the class label for the rule with highest membership degree to the example
     */
    private int FRM_WR(double[] example) {
        int clas = -1;
        double max = 0.0;
        for (int i = 0; i < ruleBase.size(); i++) {
            Rule r = ruleBase.get(i);
            double produc = r.compatibility(example);
            produc *= r.weight;
            if (produc > max) {
                max = produc;
                clas = r.clas;
            }
        }
        return clas;
    }

    /**
     * Additive Combination FRM
     * @param example double[] the input example
     * @return int the class label for the set of rules with the highest sum of membership degree per class
     */
    private int FRM_AC(double[] example) {
        int clas = -1;
        double[] class_degrees = new double[1];
        for (int i = 0; i < ruleBase.size(); i++) {
            Rule r = ruleBase.get(i);

            double produc = r.compatibility(example);
            produc *= r.weight;
            if (r.clas > class_degrees.length - 1) {
                double[] aux = new double[class_degrees.length];
                for (int j = 0; j < aux.length; j++) {
                    aux[j] = class_degrees[j];
                }
                class_degrees = new double[r.clas + 1];
                for (int j = 0; j < aux.length; j++) {
                    class_degrees[j] = aux[j];
                }
            }
            class_degrees[r.clas] += produc;
        }
        double max = 0.0;
        for (int l = 0; l < class_degrees.length; l++) {
            if (class_degrees[l] > max) {
                max = class_degrees[l];
                clas = l;
            }
        }
        return clas;
    }

    /**
     * Evaluation funtion
     *
     * It counts the number of examples correctly classified
     */
    public void evaluate() {
        int n_classified = 0;
        int nonCovered = 0;
        for (int j = 0; j < train.size(); j++) {
            int clase = this.FRM_WR(train.getExample(j));
            if (train.getOutputAsInteger(j) == clase) {
                n_classified++;
            } else {
                examplesUncovered[nonCovered++] = j;
            }
        }
        fitness = n_classified;
        n_e = false;
    }

    /**
     * It deletes from the RB those rules with a negative weight
     */
    public void delete() {
        for (int i = 0; i < ruleBase.size(); ) {
            if (ruleBase.get(i).weight < 0.0) {
                ruleBase.remove(i);
            } else {
                i++;
            }
        }
    }

    /**
     * It returns the accuracy rate of the Rule Base
     * @return double the accuracy rate of the Rule Base
     */
    public double getAccuracy() {
        return (double) fitness / train.size();
    }

    /**
     * It performs a copy of the current RB
     * @return RuleBase a copy of the current RB
     */
    public RuleBase clone() {
        RuleBase br = new RuleBase();
        br.dataBase = dataBase;
        br.ruleBase = new ArrayList<Rule>();
        for (int i = 0; i < ruleBase.size(); i++) {
            br.ruleBase.add(ruleBase.get(i).clone());
        }
        br.train = train;
        br.n_variables = n_variables;
        br.ruleWeight = ruleWeight;
        br.infType = infType;
        br.compType = compType;
        br.fitness = fitness;
        br.n_e = n_e;
        br.nonCovered = nonCovered;
        br.examplesUncovered = new int[train.size()];
        for (int i = 0; i < examplesUncovered.length; i++) {
            br.examplesUncovered[i] = examplesUncovered[i];
        }
        return br;
    }

    /**
     * Mutation operator
     */
    public void mutate() {
        if (n_e) { //it has been crossed
            for (int j = 0; j < ruleBase.size(); j++) {
                ruleBase.get(j).mutate(train, this.mutProb);
            }
        }
    }

    /**
     * Compares the fitness of two RB for the ordering procedure
     * @param a Object an RB
     * @return int -1 if the current RB is worst than the one that is compared, 1 for the contrary case and 0
     * if they are equal.
     */
    public int compareTo(Object a) {
        if (((RuleBase) a).fitness < this.fitness) {
            return -1;
        }
        if (((RuleBase) a).fitness > this.fitness) {
            return 1;
        }
        return 0;
    }

    /**
     * Genetic Cooperative-Competetive procedure
     */
    public void michigan() {
        n_replace_mich = ruleBase.size() / 5;
        while (n_replace_mich % 2 != 0) {
            n_replace_mich++;
        }
        selected = new int[n_replace_mich];
        offspring = new ArrayList<Rule>();
        evaluateMich();
        selectionMich();
        crossMich();
        mutationMich();
        replaceMich();
    }

    /**
     * Evaluation of the GCCL procedure.
     *
     * It counts the number of examples correctly classified by each rule in the RB
     */
    void evaluateMich() {
        int i, j, l, n_positivesRule, examplesRule;
        boolean[] examples = new boolean[train.size()];
        double membership_degree, membership_aux;
        nonCovered = train.size();
        for (i = 0; i < nonCovered; i++) {
            examples[i] = false;
        }
        for (i = 0; i < ruleBase.size(); i++) {
            n_positivesRule = 0;
            examplesRule = 0;
            Rule r = ruleBase.get(i);
            membership_degree = 0;
            for (j = 0; j < train.size(); j++) {
                membership_degree = r.compatibility(train.getExample(j));
                membership_degree *= r.weight;
                if (membership_degree > 0) { //the antecedent of the rule is compatible with the example
                    //now I check if this rule classifies the example (grado_pertene == MAX of all rules)
                    for (l = 0; l < ruleBase.size(); l++) {
                        Rule r2 = ruleBase.get(l);
                        membership_aux = r2.compatibility(train.getExample(j));
                        membership_aux *= r2.weight;
                        if (membership_aux > membership_degree) {
                            break;
                        }
                    }
                    if (l == ruleBase.size()) { //I have iterated all rules and there is no one better
                        examplesRule++;
                        if (train.getOutputAsInteger(j) == r.clas) {
                            n_positivesRule++;
                            examples[j] = true; //Covered
                        }
                    }
                }
            }
            r.covered = n_positivesRule;
        }
        for (i = 0, j = 0; i < train.size(); i++) {
            if (!examples[i]) {
                examplesUncovered[j++] = i;
                nonCovered--;
            }
        }
    }

    /**
     * It generates new rules in the GCCL process by means of an heuristic procedure
     */
    void generateRuleHeuristic() {
        int[] selected = new int[nonCovered];
        int i, total, new_rules, selection;
        int[] examples = new int[n_replace_mich / 2];
        new_rules = n_replace_mich / 2;
        //We first select training examples that enable use to create the new rules
        if (nonCovered > new_rules) {
            for (i = 0; i < nonCovered; i++) {
                selected[i] = examplesUncovered[i];
            }
            total = nonCovered;
            for (i = 0; i < new_rules; i++) {
                selection = Randomize.RandintClosed(0, total - 1);
                examples[i] = selected[selection];
                total--;
                selected[selection] = selected[nonCovered - i - 1];
            }
        } else {
            new_rules = nonCovered;
            for (i = 0; i < nonCovered; i++) {
                examples[i] = examplesUncovered[i];
            }
        }
        for (i = 0; i < new_rules; i++) {
            Rule r = new Rule(this.dataBase, compType, train.getNominals());
            r.buildHeuristic(train, examples[i], p_DC);
            offspring.add(r);
        }
        n_offsprings = offspring.size();
    }

    /**
     * Binary tournament between chromosomes
     * @param index Is the index of the set in which the best chromosome will be inserted
     * @param crom1 Is the index of the first individual inside the offspring
     * @param crom2 Is the index of the second individual inside the offspring
     */
    void michigan_tournament(int index, int crom1, int crom2) {
        if (BETTER(ruleBase.get(crom1).covered, ruleBase.get(crom2).covered)) {
            selected[index] = crom1;
        } else {
            selected[index] = crom2;
        }
    }

    /**
     * Selection step of the GCCL procedure
     */
    void selectionMich() {
        int random1, random2, i, init;

        n_offsprings = 0;

        generateRuleHeuristic();

        init = nonCovered;
        if (nonCovered > n_replace_mich / 2) {
            init = n_replace_mich / 2;
        }

        for (i = init; i < n_replace_mich; i++) { //We generate the other half by means of genetic operators. I get numIndividuos new individuals
            random1 = Randomize.RandintClosed(0, ruleBase.size() - 1); //One is selected randomly
            do {
                random2 = Randomize.RandintClosed(0, ruleBase.size() - 1); //I select other randomly
            } while (random1 == random2);
            michigan_tournament(i, random1, random2); //the best of the two individuals is inserted at the 'i'-th position
        }
    }

    /**
     * Crossover operator of the GCCL procedure
     */
    void crossMich() {
        int i, j;
        for (i = (n_replace_mich / 2); i < n_replace_mich - 1; i += 2) {
            Rule offspring1 = new Rule(dataBase, compType, train.getNominals());
            Rule offspring2 = new Rule(dataBase, compType, train.getNominals());
            Rule dad = ruleBase.get(selected[i]);
            Rule mom = ruleBase.get(selected[i + 1]);
            if (this.crossProb > Randomize.Rand()) {
                for (j = 0; j < n_variables; j++) {
                    if (Randomize.Rand() > 0.5) {
                        offspring1.antecedent[j] = dad.antecedent[j];
                        offspring2.antecedent[j] = mom.antecedent[j];
                    } else {
                        offspring2.antecedent[j] = dad.antecedent[j];
                        offspring1.antecedent[j] = mom.antecedent[j];
                    }
                }
                offspring1.n_e = true;
                offspring2.n_e = true;
            } else {
                offspring1 = dad.clone();
                offspring2 = mom.clone();
            }
            offspring.add(offspring1);
            offspring.add(offspring2);
        }
    }

    /**
     * Mutation operator of the GCCL procedure
     *
     * Each symbol of the generated strings by the crossover operation is randomly replaced
     */
    void mutationMich() {
        int i;
        for (i = n_offsprings; i < offspring.size(); i++) {
            if (offspring.get(i).n_e) { //it has been crossed
                offspring.get(i).mutate(train, mutProb);
            }
        }
    }

    /**
     * Replacement setp of the GCCL procedure: Elitism.
     */
    void replaceMich() {
        Collections.sort(ruleBase);
        int total = ruleBase.size() - 1 - offspring.size();
        for (int i = ruleBase.size() - 1; i > total; i--) {
            ruleBase.remove(i);
        }
        for (int i = 0; i < offspring.size(); i++) {
            ruleBase.add(offspring.get(i).clone());
        }
        offspring.clear();
    }
}

