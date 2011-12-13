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
 * This class contains the representation of a Rule Set
 *
 * @author Written by Jesus Alcala (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */
public class RuleBase {
  ArrayList<Rule> ruleBase;
  DataBase dataBase;
  myDataset train;
  int n_variables;
  double fitness;

  /**
   * Maximization
   * @param a int first number
   * @param b int second number
   * @return boolean true if a is better than b
   */
  public boolean BETTER(int a, int b) {
    if (a > b)  return true;
    return  false;
  }

  /**
   * <p>
   * Default Constructor
   * </p>
   */
  public RuleBase() {
  }


  /**
   * <p>
   * Parameters Constructor
   * </p>
   * @param dataBase DataBase Set of training data which is necessary to generate a rule
   * @param train myDataset Training data set with information to construct the rule base (mainly, the training examples)
   */
  public RuleBase(DataBase dataBase, myDataset train) {
    this.ruleBase = new ArrayList<Rule> ();
    this.dataBase = dataBase;
    this.train = train;
    this.n_variables = dataBase.numVariables();
	this.fitness = 0.0;
  }

  /**
   * <p>
   * Clone Function
   * </p>
   * 
   * @return a copy of the RuleBase
   */
  public RuleBase clone() {
    RuleBase br = new RuleBase();
    br.ruleBase = new ArrayList<Rule> ();
    for (int i = 0; i < this.ruleBase.size(); i++)  br.ruleBase.add((this.ruleBase.get(i)).clone());

    br.dataBase = this.dataBase;
    br.train = this.train;
    br.n_variables = this.n_variables;
	br.fitness = this.fitness;
    return (br);
  }

  /**
   * <p>
   * It adds a rule to the rule base
   * </p>
   * @param rule Rule Rule to be added
   */
  public void add(Rule rule) {
	  this.ruleBase.add(rule);
  }

  /**
   * <p>
   * It adds a rule to the rule base
   * </p>
   * @param itemset Itemset itemset to be added
   */
  public void add(Itemset itemset) {
	  int i;
	  Item item;

	  int[] antecedent = new int[n_variables];
	  for (i=0; i < n_variables; i++)  antecedent[i] = -1;  // Don't care

	  for (i=0; i < itemset.size(); i++) {
		  item = itemset.get(i);
		  antecedent[item.getVariable()] = item.getValue();
	  }
	  
	  Rule r = new Rule(this.dataBase);
      r.asignaAntecedente(antecedent);
	  r.setConsequent(itemset.getClas());
	  r.setConfidence(itemset.getSupportClass() / itemset.getSupport());
	  r.setSupport(itemset.getSupportClass());
      this.ruleBase.add(r);
  }

  /**
   * <p>
   * Function to get a rule from the rule base
   * </p>
   * @param pos int Position in the rule base where the desired rule is stored
   * @return Rule The desired rule
   */
  public Rule get(int pos) {
	  return (this.ruleBase.get(pos));
  }

  /**
   * <p>
   * It returns the number of rules in the rule base
   * </p>
   * @return int Rule base's size
   */
  public int size() {
	  return (this.ruleBase.size());
  }

  /**
   * <p>
   * Function to sort the rule base
   * </p>
   */
  public void sort () {
	  Collections.sort(this.ruleBase);
  }

  /**
   * <p>
   * It removes the rule stored in the given position
   * </p>
   * @param pos int Position where the rule we want to remove is
   * @return Rule Removed rule
   */
  public Rule remove(int pos) {
	  return (this.ruleBase.remove(pos));
  }

  /**
   * <p>
   * Function to evaluate the whole rule base by using the training dataset
   * </p>
   * @return double Fitness of the rule base
   */
  public double evaluate() {
    int nHits, Prediction;
	
	nHits = 0;
    for (int j = 0; j < train.size(); j++) {
      Prediction = this.FRM_WR(train.getExample(j));
      if (train.getOutputAsInteger(j) == Prediction)  nHits++;
    }

	this.fitness = (100.0 * nHits) / (1.0 * this.train.size());
    return  (this.fitness);
  }

  /**
   * <p>
   * It returns the class which better fits to the given example
   * </p>
   * @param example int[] Example to be classified
   * @return int Output class
   */
  public int FRM(double[] example) {
    return FRM_WR(example);
  }

  private int FRM_WR(double[] example) {
    int clas = -1;
    double max = 0.0;

	for (int i = 0; i < this.ruleBase.size(); i++) {
      Rule r = this.ruleBase.get(i);
      double DF = r.matching(example);
      if (DF > max) {
        max = DF;
        clas = r.getClas();
      }
    }

	return clas;
  }

  /**
   * <p>
   * Function to return the fitness of the rule base
   * </p>
   * @return double Fitness of the rule base
   */
  public double getAccuracy() {
    return  (this.fitness);
  }


  /**
   * <p>
   * Function to eliminate the redundant rules
   * </p>
   */
  public void reduceRules() {
	  int i, j;
	  Rule rulei, rulej;
	  
	  this.sort();

	  for (i=0; i < this.ruleBase.size(); i++) {
		  rulei = this.ruleBase.get(i);
		  for (j = i+1; j < this.ruleBase.size(); j++) {
			  rulej = this.ruleBase.get(j);
			  if (rulei.isSubset(rulej)) {
				  this.ruleBase.remove(j);
				  j--;
			  }
		  }
	  }
  }

  /**
   * <p>
   * Function to adjust fuzzy confidences (Nozaki method)
   * </p>
   * @param n1 double learning rate
   * @param n2 double learning rate
   * @param Jmax int number of iterations
   */
  public void adaptiveRules(double n1, double n2, int Jmax) {
	  int i, j, k, bestRule;
	  double max, DF, conf;
	  Rule r;

	  for (i=0; i < Jmax; i++) {
		  for (j = 0; j < this.train.size(); j++) {
			  max = -1;
			  bestRule = -1;
			  for (k = 0; k < this.ruleBase.size(); k++) {
				  r = this.ruleBase.get(k);
                  DF = r.matching(this.train.getExample(j));
				  if (DF > max) {
					  max = DF;
					  bestRule = k;
				  }
			  }
			  if (bestRule > -1) {
				  r = this.ruleBase.get(bestRule);
				  conf = r.getConfidence();
				  if (train.getOutputAsInteger(j) == r.getClas())  r.setConfidence(conf + (n1 * (1 - conf)));
				  else  r.setConfidence(conf - (n2 * conf));
			  }
		  }
      }
  }



  /**
   * <p>
   * It prints the whole rulebase
   * </p>
   * @return String The whole rulebase
   */
  public String printString() {
    int i, j, ant;
    String [] names = train.names();
    String [] clases = train.clases();
    String stringOut = new String("");

	ant = 0;
    for (i = 0; i < this.ruleBase.size(); i++) {
      Rule r = this.ruleBase.get(i);
      stringOut += (i+1)+": ";
      for (j = 0; j < n_variables && r.antecedent[j] < 0; j++);
	  if (j < n_variables && r.antecedent[j] >= 0) {
		  stringOut += names[j]+" IS " + r.dataBase.print(j,r.antecedent[j]);
		  ant++;
	  }
      for (j++; j < n_variables-1; j++) {
		if (r.antecedent[j] >=0) {
			stringOut += " AND " + names[j]+" IS " + r.dataBase.print(j,r.antecedent[j]);
		    ant++;
		}
      }
      if (j < n_variables && r.antecedent[j] >= 0)  {
		  stringOut += " AND " + names[j]+" IS " + r.dataBase.print(j,r.antecedent[j]) + ": " + clases[r.clas];
  		  ant++;
	  }
	  else  stringOut += ": " + clases[r.clas];

	  stringOut += " CF: " + r.getConfidence() + "\n";
    }

	stringOut += "\n\n";
    stringOut += "@supp and CF:\n\n";
    for (i = 0; i < this.ruleBase.size(); i++) {
    	Rule rule = this.ruleBase.get(i);
    	stringOut += (i+1)+": ";
    	stringOut += "supp: " + rule.getSupport() + " AND CF: " + rule.getConfidence() + "\n";
	}

    stringOut = "@Number of rules: " + this.ruleBase.size() + " Number of Antecedents by rule: " + ant * 1.0 / this.ruleBase.size() + "\n\n" + stringOut;
	return (stringOut);
  }

  /**
   * <p>
   * It stores the rule base in a given file
   * </p>
   * @param filename String Name for the rulebase file
   */
  public void saveFile(String filename, double minFS, double minFC) {
    String stringOut = new String("");
    stringOut = printString();
    stringOut += "\n\n@Minimum Support: " + minFS + "  Minimum Confidence: " + minFC + "\n\n";
    Files.writeFile(filename, stringOut);
  }

}

