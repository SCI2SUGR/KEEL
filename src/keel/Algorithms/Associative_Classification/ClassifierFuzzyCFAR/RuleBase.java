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

package keel.Algorithms.Associative_Classification.ClassifierFuzzyCFAR;

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
  int WrongNDefault;
  int n_variables, fitness;
  int[] totalLabels;



  /**
   * Maximization
   * @param a first number
   * @param b second number
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
   * @param dataBase Set of training data which is necessary to generate a rule
   * @param train Training data set with information to construct the rule base (mainly, the training examples)
   */
  public RuleBase(DataBase dataBase, myDataset train) {
    this.ruleBase = new ArrayList<Rule> ();
    this.dataBase = dataBase;
    this.train = train;
    this.n_variables = dataBase.numVariables();
	this.fitness = 0;
    this.totalLabels = new int[this.n_variables];
	for (int i=0; i < this.n_variables; i++)  this.totalLabels[i] = dataBase.numLabels(i);
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
	br.totalLabels = new int[this.n_variables];
	for (int i=0; i < this.n_variables; i++)  br.totalLabels[i] = this.totalLabels[i];
    return (br);
  }


  /**
   * <p>
   * It adds a rule to the rule base
   * </p>
   * @param rule Rule to be added
   */
  public void add(Rule rule) {
	  this.ruleBase.add(rule);
  }


  /**
   * <p>
   * It adds a rule to the rule base
   * </p>
   * @param itemset itemset to be added
   * @param time Time of the rule
   */
  public void add(Itemset itemset, long time) {
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
	  r.setTime(time);
      this.ruleBase.add(r);
  }


  /**
   * <p>
   * Function to get a rule from the rule base
   * </p>
   * @param pos Position in the rule base where the desired rule is stored
   * @return The desired rule
   */
  public Rule get(int pos) {
	  return (this.ruleBase.get(pos));
  }


  /**
   * <p>
   * It returns the number of rules in the rule base
   * </p>
   * @return Rule base's size
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
   * @param pos Position where the rule we want to remove is
   * @return Removed rule
   */
  public Rule remove(int pos) {
	  return (this.ruleBase.remove(pos));
  }


  public void removeRules() {
	  int i, pos;
	  double minRate, rate;
	  Rule rule;

	  minRate = 1.0;
	  pos = -1;
	  for (i=0; i < this.ruleBase.size(); i++) {
		  rule = this.ruleBase.get(i);
		  if (rule.getRightN() < 1) {
			  this.ruleBase.remove(i);
			  i--;
		  }
		  else {
			  rate = (1.0 * rule.getRightN()) / (1.0 * rule.getWrongN() + rule.getRightN());
			  if (rate < minRate) {
				  minRate = rate;
				  pos = i;
			  }
		  }
	  }

	  if (ruleBase.size() > 0 && pos > -1)  this.ruleBase.remove(pos);
  }


  /**
   * <p>
   * Function to evaluate the whole rule base by using the training dataset
   * </p>
   */
  public void evalua() {
    int n_clasificados, Prediction;
	
	n_clasificados = 0;
    for (int j = 0; j < train.size(); j++) {
      Prediction = this.FRM_WR(train.getExample(j));
      if (train.getOutputAsInteger(j) == Prediction)  n_clasificados++;
    }

	this.fitness = n_clasificados;
  }


  /**
   * <p>
   * It returns the class which better fits to the given example
   * </p>
   * @param example Example to be classified
   * @return Output class
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

	if (clas < 0)  return (0);	// 0 is the default class
	else  return clas;
  }


  /**
   * <p>
   * Function to return the fitness of the rule base
   * </p>
   * @return Fitness of the rule base
   */
  public double getAccuracy() {
    return (double) fitness / train.size();
  }


  /**
   * <p>
   * Function to eliminate the redundant rules
   * </p>
   */
  public void selection() {
	  int i, j;
	  Rule rulei, rulej;
	  
	  for (i=0; i < this.ruleBase.size(); i++) {
		  rulei = this.ruleBase.get(i);
		  rulei.orderPrecede();
	  }
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
   * This function calculates the number of errors
   * </p>
   */
  private void rateError() {
    int n_errores, Prediction;
	
	n_errores = 0;
    for (int j = 0; j < train.size(); j++) {
      Prediction = this.FRM_WR(train.getExample(j));
      if (train.getOutputAsInteger(j) != Prediction)  n_errores++;
    }

	this.fitness = n_errores;
  }



  /**
   * <p>
   * This function generates a classifier from the CompSet of the generated fuzzy rules
   * </p>
   * @return Classifier obtained
   */
  public RuleBase classifier() {
	  int i, j;
	  boolean stop;
	  Rule rule;
	  RuleBase bestRuleBase;

	  for (i = 0; i < this.ruleBase.size(); i++) {
		  rule = this.ruleBase.get(i);
		  rule.orderDF();
	  }

	  this.fitness = this.train.size();
	  bestRuleBase = this.clone();
	  System.out.println("Fitness Inicial: " + this.fitness);

	  if (this.ruleBase.size() > 1) {
		  do {
			  this.calculateRightNWrongN();
			  this.removeRules();
			  
			  this.rateError();
	          System.out.println("Fitness: " + this.fitness + " NRules: " + this.ruleBase.size());
			  
			  stop = true;
			  if (this.fitness <= bestRuleBase.fitness) {
				  bestRuleBase = this.clone();
				  if (this.ruleBase.size() > 1 && this.fitness > 0)  stop = false;			  
			  }
		  }while (!stop);
	  }

	  bestRuleBase.evalua();

	  return (bestRuleBase);
  }


  private void calculateRightNWrongN() {
	  int i, j, nData, evaClass;
	  boolean stop;
	  Rule rule;

	  nData = this.train.size();

	  for (j = 0; j < this.ruleBase.size(); j++) {
		  rule = this.ruleBase.get(j);
		  rule.setIni();
	  }
	  this.WrongNDefault = 0;

	  for (i=0; i < nData; i++) {
		  for (j = 0; j < this.ruleBase.size(); j++) {
			  rule = this.ruleBase.get(j);
			  rule.matching(train.getExample(i));
		  }
		  
		  this.sort();

		  stop = false;
		  for (j=0; j < this.ruleBase.size() && !stop; j++) {
			  rule = this.ruleBase.get(j);
			  if (train.getOutputAsInteger(i) == rule.getClas()) {
				  rule.incrRightN();
				  stop = true;
			  }
			  else  rule.incrWrongN();
		  }
	  }
  }


  /**
   * <p>
   * It prints the whole rulebase
   * </p>
   * @return The whole rulebase
   */
  public String printString() {
    int i, j, ant;
    String [] names = train.names();
    String [] clases = train.clases();
    String cadena = new String("");

	ant = 0;

    for (i = 0; i < this.ruleBase.size(); i++) {
      Rule r = this.ruleBase.get(i);
      cadena += (i+1)+": ";
      for (j = 0; j < n_variables && r.antecedente[j] < 0; j++);
	  if (j < n_variables && r.antecedente[j] >= 0) {
		  cadena += names[j]+" IS " + r.dataBase.print(j,r.antecedente[j]);
		  ant++;
	  }
      for (j++; j < n_variables-1; j++) {
		if (r.antecedente[j] >=0) {
			cadena += " AND " + names[j]+" IS " + r.dataBase.print(j,r.antecedente[j]);
			ant++;
		}
      }
      if (j < n_variables && r.antecedente[j] >= 0) {
		  cadena += " AND " + names[j]+" IS " + r.dataBase.print(j,r.antecedente[j]) + ": " + clases[r.clase] + "\n";
		  ant++;
	  }
	  else  cadena += ": " + clases[r.clase] + "\n";
    }
    
    cadena += (i+1) + ": Default IS " + clases[0] + "\n";  // Default class

	cadena += "\n\n";
    cadena += "@Dsupp and Dconf:\n\n";
    for (i = 0; i < this.ruleBase.size(); i++) {
      Rule rule = this.ruleBase.get(i);
      cadena += (i+1)+": ";
	  cadena += "Dsupp: " + rule.getSupport() + " AND Dconf: " + rule.getConfidence() + "\n";
	}

    cadena = "@Number of rules: " + (this.ruleBase.size() + 1) + " Number of Antecedents by rule: " + ant * 1.0 / (this.ruleBase.size() + 1.0) + "\n\n" + cadena;

	return (cadena);
  }

  /**
   * <p>
   * It stores the rule base in a given file
   * </p>
   * @param filename Name for the rulebase file
   */
  public void saveFile(String filename) {
    String stringOut = new String("");
    stringOut = printString();
    Fichero.escribeFichero(filename, stringOut);
  }
}
