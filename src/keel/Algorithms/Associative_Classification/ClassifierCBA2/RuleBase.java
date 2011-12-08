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

package keel.Algorithms.Associative_Classification.ClassifierCBA2;

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
  ArrayList<Rule> U;
  ArrayList<Rule> Q;
  ArrayList<Selected> C;
  ArrayList<Structure> A;
  DataBase dataBase;
  myDataset train;
  int n_variables, defaultClass;
  double fitness;

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
	this.fitness = 0.0;
	this.defaultClass = -1;
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
   * It adds a rule to the rule base from an itemset and a time
   * </p>
   * @param itemset Itemset to be translated to an array to insert it in the rule base
   * @param time Position in time when the rule has been added
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
   * @return Rule The desired rule
   */
  public Rule get(int pos) {
	  return (this.ruleBase.get(pos));
  }

  /**
   * <p>
   * It sets the default class for the rule base
   * </p>
   * @param defaultClass Default class to set
   */
  public void setDefaultClass (int defaultClass) {
	  this.defaultClass = defaultClass;
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
   * @param pos Position where the rule we want to remove is
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
   * @param example Example to be classified
   * @return int Output class
   */
  public int FRM(int[] example) {
    return FRM_WR(example);
  }

  private int FRM_WR(int[] example) {
	Rule r;

	for (int i = 0; i < this.ruleBase.size(); i++) {
      r = this.ruleBase.get(i);
      if (r.matching(example) > 0.0)  return (r.getClas());
    }

	return this.defaultClass;
  }

  /**
   * <p>
   * It returns the accuracy of the rule base
   * </p>
   * @return double Accuracy of the rule base
   */
  public double getAccuracy() {
    return  (this.fitness);
  }

  /**
   * Classifier Builder (Method 2)
   */
  public void CBACBM2() {
	  this.U = new ArrayList<Rule> ();
	  this.Q = new ArrayList<Rule> ();
	  this.A = new ArrayList<Structure> ();

	  this.sort();

	  this.stage1();
	  this.stage2();
	  this.stage3();
  }

  private boolean isNew (ArrayList<Rule> rb, Rule rule) {
	  int i;
	  Rule r;

	  for (i=0; i < rb.size(); i++) {
		  r = rb.get(i);
		  if (rule.isEqual(r))  return (false);
	  }

	  return (true);
  }

  private void stage1() {
	  int i, j, cRule, wRule, y;
	  int [] example;
	  Rule rule;

	  this.U.clear();
	  this.Q.clear();
	  this.A.clear();

	  for (i=0; i < this.train.size(); i++) {
		  example = this.train.getExample(i);
		  y = train.getOutputAsInteger(i);

		  cRule = -1;
		  wRule = -1;

		  for (j=0; j < this.size() && (cRule < 0 || wRule < 0); j++) {
			  rule = this.ruleBase.get(j);
			  if (rule.matching(example) > 0.0) {
				  if ((cRule < 0) && (y == rule.getClas()))  cRule = j;
				  if ((wRule < 0) && (y != rule.getClas()))  wRule = j;
			  }
		  }

		  if (cRule > -1) {
			  rule = this.ruleBase.get(cRule);
			  if (this.isNew (this.U, rule))  this.U.add(rule);
			  rule.incrCovered(y);
			  if ((cRule < wRule) || (wRule < 0)) {
				  rule.onMark();
				  if (this.isNew (this.Q, rule))  this.Q.add(rule);
			  }
			  else {
				  Structure str = new Structure (i, y, cRule, wRule);
				  this.A.add(str);
			  }
		  }
		  else if (wRule > -1) {
			  Structure str = new Structure (i, y, cRule, wRule);
			  this.A.add(str);
		  }
	  }
  }


  private void stage2() {
	  int i, j, poscRule, poswRule;
	  Structure str;
	  Rule cRule, wRule, rule;

	  for (i=0; i < this.A.size(); i++) {
		  str = this.A.get(i);
		  poscRule = str.getcRule();
		  poswRule = str.getwRule();

		  wRule = this.ruleBase.get(poswRule);
		  if (wRule.isMark()) {
			  if (poscRule > -1)  this.ruleBase.get(poscRule).decrCovered(str.gety());
			  wRule.incrCovered(str.gety());
		  }
		  else {
			  for (j=0; j < this.U.size(); j++) {
				  rule = this.U.get(j);
				  
				  if (rule.matching(this.train.getExample(str.getdID())) > 0.0) {
					  if (rule.getClas() != str.gety()) {
						  if (poscRule > -1) {
							  cRule = this.ruleBase.get(poscRule);
							  if (rule.isPrecedence(cRule)) {
								  rule.addReplace(new Replace(poscRule,str.getdID(),str.gety()));
								  rule.incrCovered(str.gety());
								  if (this.isNew (this.Q, rule))  this.Q.add(rule);
							  }
						  }
						  else {
							  rule.addReplace(new Replace(poscRule,str.getdID(),str.gety()));
							  rule.incrCovered(str.gety());
							  if (this.isNew (this.Q, rule))  this.Q.add(rule);
						  }
					  }
				  }
			  }
		  }
	  }
  }


  private void stage3() {
	  int i, j, ruleErrors, totalErrors, defaultErrors, errorsOfRule, defaultClass, posLowest, lowestTotalErrors;
	  int [] compClassDistr;
	  int [] exampleCovered;
	  int [] example;
	  Rule rule;
	  Replace rep;
	  Selected sel;

      this.C = new ArrayList<Selected> ();

	  compClassDistr = new int[this.train.getnClasses()];
	  for (i=0; i < this.train.getnClasses(); i++)  compClassDistr[i] = this.train.numberInstances(i);
	  ruleErrors = 0;

	  exampleCovered = new int[this.train.size()];
	  for (i=0; i < this.train.size(); i++)	 exampleCovered[i] = 0;

	  Collections.sort(this.Q);


	  for (i=0; i < this.Q.size(); i++) {
		  rule = this.Q.get(i);
		  if (rule.getclassCasesCovered(rule.getClas()) > 0) {
			  for (j=0; j < rule.getnReplace(); j++) {
				  rep = rule.getReplace(j);
				  if (exampleCovered[rep.getdID()] > 0)  rule.decrCovered(rep.gety());
				  else {
					  if (rep.getcRule() > -1) {
						  this.ruleBase.get(rep.getcRule()).decrCovered(rep.gety());
					  }
				  }
			  }

			  errorsOfRule = 0;
			  for (j = 0; j < this.train.size(); j++) {
				  if (exampleCovered[j] < 1) {
					  example = this.train.getExample(j);
					  if (rule.matching(example) > 0.0) {
						  exampleCovered[j] = 1;
						  compClassDistr[train.getOutputAsInteger(j)]--;
						  if (rule.getClas() != train.getOutputAsInteger(j))  errorsOfRule++;
					  }
				  }
			  }
			  ruleErrors += errorsOfRule;

			  defaultClass = 0;
			  for (j=1; j < this.train.getnClasses(); j++) {
				  if (compClassDistr[defaultClass] < compClassDistr[j])  defaultClass = j;
			  }

			  defaultErrors = 0;
			  for (j=0; j < this.train.getnClasses(); j++) {
				  if (j != defaultClass)  defaultErrors += compClassDistr[j];
			  }

			  totalErrors = ruleErrors + defaultErrors;
			  this.C.add (new Selected (rule, defaultClass, totalErrors));
		  }
	  }
	  if (this.C.size() > 0) {
		  lowestTotalErrors = this.C.get(0).getTotalErrors();
		  posLowest = 0;
		  for (i = 1; i < this.C.size(); i++) {
			  sel = this.C.get(i);
			  if (sel.getTotalErrors() < lowestTotalErrors) {
				  lowestTotalErrors = sel.getTotalErrors();
				  posLowest = i;
			  }
		  }
		  while (this.C.size() > (posLowest+1))  this.C.remove(posLowest+1);
	  }
  }

  /**
   * <p>
   * Function to get stored classifier
   * </p>
   * @return RuleBase The whole classifier
   */
  public RuleBase getClassifier() {
	  int i, defaultClass;
	  RuleBase rb = new RuleBase(this.dataBase, this.train);
	  Selected sel;

	  if (this.C.size() > 0) {
		  for (i=0; i < this.C.size(); i++) {
			  sel = this.C.get(i);
			  rb.add (sel.getRule().clone());
		  }
		  
		  sel = this.C.get(this.C.size() - 1);
		  rb.setDefaultClass (sel.getDefaultClass());
	  }
	  else {
		  defaultClass = 0;
		  for (i=1; i < this.train.getnClasses(); i++) {
			  if (this.train.numberInstances(i) > this.train.numberInstances(defaultClass))  defaultClass = i;
		  }
		  rb.setDefaultClass (defaultClass);
	  }

	  return (rb);
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
		  stringOut += " AND " + names[j]+" IS " + r.dataBase.print(j,r.antecedent[j]) + ": " + clases[r.clas] + "\n";
  		  ant++;
	  }
	  else  stringOut += ": " + clases[r.clas] + "\n";
    }
	stringOut += "Default: " + clases[this.defaultClass] + "\n";

	stringOut += "\n\n";
    stringOut += "@supp and conf:\n\n";
    for (i = 0; i < this.ruleBase.size(); i++) {
    	Rule rule = this.ruleBase.get(i);
    	stringOut += (i+1)+": ";
    	stringOut += "supp: " + rule.getSupport() + " AND conf: " + rule.getConfidence() + "\n";
	}

	if (this.ruleBase.size() > 0) stringOut = "@Number of rules: " + (this.ruleBase.size() + 1) + " Number of Antecedents by rule: " + ant * 1.0 / this.ruleBase.size() + "\n\n" + stringOut;
	else  stringOut = "@Number of rules: 1 Number of Antecedents by rule: 0\n\n" + stringOut;
	return (stringOut);
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

