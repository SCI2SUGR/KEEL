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
 * <p>Title: RuleBase</p>
 *
 * <p>Description: This class contains the representation of a Rule Set</p>
 *
 * <p>Copyright: Copyright KEEL (c) 2007</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Alberto Fernández (University of Granada) 29/10/2007
 * @author Modifed by Jesus Alcala (University of Granada) 23/05/2009
 * @version 1.1
 * @since JDK1.5
 */

import java.util.*;
import org.core.*;

public class RuleBase {
  ArrayList<Rule> ruleBase;
  DataBase dataBase;
  myDataset train;
  int n_variables, fitness, totalPartitions, totalLabels, typeEvaluation;

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
   * Default constructor
   */
  public RuleBase() {
  }

  /**
   * Builds an object for the Rule Base
   * @param dataBase DataBase Data Base
   * @param train myDataset Training set
   * @param typeEvaluation int Code for the evaluation system
   */
  public RuleBase(DataBase dataBase, myDataset train, int typeEvaluation) {
    this.ruleBase = new ArrayList<Rule> ();
    this.dataBase = dataBase;
    this.train = train;
    this.typeEvaluation = typeEvaluation;
    this.n_variables = dataBase.numVariables();
    this.totalPartitions = dataBase.partitions;
    this.totalLabels = dataBase.nLabels;
  }

  /**
   * It generates the initial rule set for each partition of the input space (2...L)
   */
  public void initialization() {
    int i, j, k;

    int[] rule = new int[n_variables];
    for (i = 0; i < this.n_variables; i++) {
      rule[i] = -1; // don't care
    }

    for (i = 0; i < this.n_variables; i++) {
      for (j = 0; j < this.totalLabels; j++) {
        rule[i] = j;
        this.createRule(rule);
      }
      rule[i] = -1; // don't care
    }
  }

  /**
   * It returns the size of the Rule Base
   * @return int the number of rules
   */
  public int size() {
    return (this.ruleBase.size());
  }

  /**
   * It adds a new rule to the rule set
   * @param rule Rule the new rule
   */
  public void add(Rule rule) {
    this.ruleBase.add(rule);
  }

  /**
   * It returns a given rule from the RB
   * @param pos int the rule id
   * @return Rule a rule of the RB
   */
  public Rule get(int pos) {
    return (this.ruleBase.get(pos));
  }

  /**
   * It removes a given rule from the RB
   * @param pos int the rule id
   * @return Rule the removed rule
   */
  public Rule remove(int pos) {
    return (this.ruleBase.remove(pos));
  }

  /**
   * It sorts the rule base according to the fitness of the rules
   */
  public void sort() {
    Collections.sort(this.ruleBase);
  }

  /**
   * It creates a new rule and add it to the RB
   * @param antecedent int[] the antecedent conditions for the rule
   */
  private void createRule(int[] antecedent) {
    Rule r = new Rule(this.dataBase, this.typeEvaluation);
    r.asignAntecedent(antecedent);
    r.setConsequent(this.train);
    r.evaluation(this.train);
    r.onNew();
    this.ruleBase.add(r);
  }

  /**
   *
   * @return RuleBase
   */
  public RuleBase clone() {
    RuleBase br = new RuleBase();
    br.ruleBase = new ArrayList<Rule> ();
    for (int i = 0; i < this.ruleBase.size(); i++) {
      br.ruleBase.add(this.ruleBase.get(i).clone());
    }
    br.dataBase = this.dataBase;
    br.train = this.train;
    br.typeEvaluation = this.typeEvaluation;
    br.n_variables = this.n_variables;
    br.totalPartitions = this.totalPartitions;
    br.totalLabels = this.totalLabels;
    br.fitness = this.fitness;
    return (br);
  }

  /**
   * It performs a copy of the current RB
   * @return RuleBase a copy of the current RB
   */
  public RuleBase cloneEmpty() {
    RuleBase br = new RuleBase();
    br.ruleBase = new ArrayList<Rule> ();
    br.dataBase = this.dataBase;
    br.train = this.train;
    br.typeEvaluation = this.typeEvaluation;
    br.n_variables = this.n_variables;
    br.totalPartitions = this.totalPartitions;
    br.totalLabels = this.totalLabels;
    br.fitness = this.fitness;
    return (br);
  }

  /**
   * It removes a prespecified number of rules from the rule set
   * @param nRules int the number of rules to remove
   */
  public void removeRules(int nRules) {
    while (this.ruleBase.size() > nRules) {
      this.ruleBase.remove(nRules);
    }
  }

  /**
   * It performs the classification process with the current rule set
   * @return double the accuracy rate
   */
  public double classify() {
    evaluate();
    return this.getAccuracy();
  }

  /**
   * It evaluates the rule set
   */
  public void evaluate() {
    int n_clasificados, Prediction;

    n_clasificados = 0;
    for (int j = 0; j < train.size(); j++) {
      Prediction = this.FRM_WR(train.getExample(j));
      if (train.getOutputAsInteger(j) == Prediction) {
        n_clasificados++;
      }
    }

    this.fitness = n_clasificados;
  }

  /**
   * Fuzzy Reasoning Method
   * @param example double[] the input example
   * @return int the predicted class label (id)
   */
  public int FRM(double[] example) {
    return FRM_WR(example);
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
      if (produc > max) {
        max = produc;
        clas = r.clas;
      }
    }

	return  clas;
  }
/*
  private int FRM_WR(double[] example) {
	boolean twoClasses = false;
    int clas = -1;
    double max = 0.0;

    for (int i = 0; i < ruleBase.size(); i++) {
      Rule r = ruleBase.get(i);
      double produc = r.compatibility(example);
      if (produc > max) {
        max = produc;
        clas = r.clas;
		twoClasses = false;
      }
	  else if ((produc == max) && (r.clas != clas))  twoClasses = true;
    }

	if (twoClasses == true)  return (-1);
	else  return clas;
  }
*/
  /**
   * It returns the accuracy rate for the rule set
   * @return double the accuracy rate for the rule set
   */
  public double getAccuracy() {
    return (double) fitness / train.size();
  }

  /**
   * It adds the best rules to a new rule base
   * @param ruleBase RuleBase a given rule base to insert rules
   * @param nRules int the number of rules to insert
   */
  public void selection(RuleBase ruleBase, int nRules) {
    while ( (this.ruleBase.size() > nRules) && (ruleBase.size() < nRules)) {
      ruleBase.add(this.ruleBase.remove(nRules));
    }

    while (this.ruleBase.size() > nRules) {
      this.ruleBase.remove(nRules);
    }
  }

  /**
   * It prints the rule base into an string
   * @return String an string containing the rule set
   */
  public String printString() {
    int i, j, ant;
    String[] nombres = train.varNames();
    String[] clases = train.classNames();
    String cadena = new String("");

	ant = 0;
    for (i = 0; i < ruleBase.size(); i++) {
      Rule r = ruleBase.get(i);
      cadena += (i + 1) + ": ";
      for (j = 0; j < n_variables && r.antecedent[j] < 0; j++) {
        ;
      }
      if (j < n_variables && r.antecedent[j] >= 0) {
        cadena += nombres[j] + " IS " + r.dataBase.print(j, r.antecedent[j]);
		ant++;
      }
      for (j++; j < n_variables - 1; j++) {
        if (r.antecedent[j] >= 0) {
          cadena += " AND " + nombres[j] + " IS " + r.dataBase.print(j, r.antecedent[j]);
		  ant++;
        }
      }
      if (j < n_variables && r.antecedent[j] >= 0) {
        cadena += " AND " + nombres[j] + " IS " + r.dataBase.print(j, r.antecedent[j]) + ": " + clases[r.clas] + "\n";
		ant++;
      }
      else {
        cadena += ": " + clases[r.clas] + "\n";
      }
    }
    cadena = "@Number of rules: " + ruleBase.size() + " Number of Antecedents by rule: " + ant * 1.0 / ruleBase.size() + "\n\n" + cadena;

	return (cadena);
  }

  /**
   * It writes the rule set into a file
   * @param filename String the name of the file
   */
  public void saveFile(String filename) {
    String stringOut = new String("");
    stringOut = printString();
    Files.writeFile(filename, stringOut);
  }
}

