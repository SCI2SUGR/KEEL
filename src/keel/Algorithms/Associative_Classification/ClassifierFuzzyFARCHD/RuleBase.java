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
 * <p>Title: RuleBase</p>
 * <p>Description: This class contains the representation of a Rule Set</p>
 * <p>Copyright: Copyright KEEL (c) 2007</p>
 * <p>Company: KEEL </p>
 * @author Written by Jesus Alcala (University of Granada) 09/02/2011
 * @version 1.0
 * @since JDK1.5
 */

import java.util.*;
import org.core.*;

public class RuleBase {
  ArrayList<Rule> ruleBase;
  DataBase dataBase;
  myDataset train;
  int n_variables, K, nUncover, typeInference, defaultRule;
  int[] nUncoverClas;
  double fitness;

  public boolean BETTER(int a, int b) {
    if (a > b)  return true;
    return  false;
  }

  public RuleBase() {
  }

/**
* <p>
* Builder
* </p>
* @param dataBase Data Base
* @param train Training dataset
* @param K Covered patterns in the second stage are completely eliminated when they have been covered more than K times.
* @param typeInference Two option: 0) the class of the rule with the best matching; 1) the class with the best matching
* @return Rule Base
*/
public RuleBase(DataBase dataBase, myDataset train, int K, int typeInference) {
    this.ruleBase = new ArrayList<Rule> ();
    this.dataBase = dataBase;
    this.train = train;
    this.n_variables = dataBase.numVariables();
	this.fitness = 0.0;
	this.K = K;
	this.typeInference = typeInference;
	this.defaultRule = -1;
	this.nUncover = 0;
	this.nUncoverClas = new int[this.train.getnClasses()];
  }

/**
* <p>
* Clone
* </p>
* @return A copy of the Rule Base
*/
  public RuleBase clone() {
    RuleBase br = new RuleBase();
    br.ruleBase = new ArrayList<Rule> ();
    for (int i = 0; i < this.ruleBase.size(); i++)  br.ruleBase.add((this.ruleBase.get(i)).clone());

    br.dataBase = this.dataBase;
    br.train = this.train;
    br.n_variables = this.n_variables;
	br.fitness = this.fitness;
	br.K = this.K;
	br.typeInference = this.typeInference;
	br.defaultRule = this.defaultRule;
	br.nUncover = this.nUncover;
	br.nUncoverClas = new int[this.train.getnClasses()];
	for (int i = 0; i < this.train.getnClasses(); i++)  br.nUncoverClas[i] = this.nUncoverClas[i];

	return (br);
  }


  public void add(Rule rule) {
	  this.ruleBase.add(rule);
  }

  public void add(RuleBase ruleBase) {
	  int i;

	  for (i=0; i<ruleBase.size(); i++) {
		  this.ruleBase.add(ruleBase.get(i).clone());
	  }
  }


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

  public Rule get(int pos) {
	  return (this.ruleBase.get(pos));
  }

  public int size() {
	  return (this.ruleBase.size());
  }

  public void sort () {
	  Collections.sort(this.ruleBase);
  }

  public Rule remove(int pos) {
	  return (this.ruleBase.remove(pos));
  }

  public void clear() {
	  this.ruleBase.clear();
	  this.fitness = 0.0;
  }

  public int getTypeInference() {
    return  (this.typeInference);
  }

  public double getAccuracy() {
    return  (this.fitness);
  }

  public void setDefaultRule() {
	  int i, bestRule;

	  bestRule = 0;
	  for (i=1; i < this.train.getnClasses(); i++) {
		  if (this.train.numberInstances(bestRule) < this.train.numberInstances(i))  bestRule = i;
	  }

	  this.defaultRule = bestRule;
  }


  public boolean hasUncover() {
    return  (this.nUncover > 0);
  }

  public int getUncover() {
    return  (this.nUncover);
  }

  public int getK() {
    return  (this.K);
  }

  public void evaluate() {
    int nHits, prediction;
	
	nHits = 0;
	this.nUncover = 0;
	for (int j = 0; j < this.train.getnClasses(); j++)  this.nUncoverClas[j] = 0;

	for (int j = 0; j < train.size(); j++) {
      prediction = this.FRM(train.getExample(j));
      if (this.train.getOutputAsInteger(j) == prediction)  nHits++;
	  if (prediction < 0) {
		  this.nUncover++;
		  this.nUncoverClas[this.train.getOutputAsInteger(j)]++;
	  }
    }

	this.fitness = (100.0 * nHits) / (1.0 * this.train.size());
  }



  public void evaluate(double[] gene, int[] selected) {
    int nHits, prediction;

	this.dataBase.decode(gene);
	
	nHits = 0;
	this.nUncover = 0;
	for (int j = 0; j < this.train.getnClasses(); j++)  this.nUncoverClas[j] = 0;

	for (int j = 0; j < train.size(); j++) {
      prediction = this.FRM(train.getExample(j), selected);
      if (this.train.getOutputAsInteger(j) == prediction)  nHits++;
	  if (prediction < 0) {
		  this.nUncover++;
		  this.nUncoverClas[this.train.getOutputAsInteger(j)]++;
	  }
    }

	this.fitness = (100.0 * nHits) / (1.0 * this.train.size());
  }

  public int FRM(double[] example) {
    if (this.typeInference == 0)  return FRM_WR(example);
    else  return FRM_AC(example);
  }

  public int FRM(double[] example, int[] selected) {
    if (this.typeInference == 0)  return FRM_WR(example, selected);
    else  return FRM_AC(example, selected);
  }						  


  private int FRM_WR(double[] example, int[] selected) {
    int clas;
    double max, degree;
		
	max = 0.0;
	clas = defaultRule;

	for (int i = 0; i < this.ruleBase.size(); i++) {
		if (selected[i] > 0) {
			Rule r = this.ruleBase.get(i);
			degree = r.matching(example);
			
			if (degree > max) {
				max = degree;
				clas = r.getClas();
			}
		}
	}

    return clas;
  }


  private int FRM_WR(double[] example) {
    int clas;
    double max, degree;
		
	max = 0.0;
	clas = defaultRule;

	for (int i = 0; i < this.ruleBase.size(); i++) {
		Rule r = this.ruleBase.get(i);
		degree = r.matching(example);

		if (degree > max) {
			max = degree;
			clas = r.getClas();
		}
    }

    return clas;
  }


  private int FRM_AC(double[] example, int[] selected) {
    int i, clas;
	double degree, maxDegree;
	double[] degreeClass;

	clas = defaultRule;

    degreeClass = new double[this.train.getnClasses()];
	for (i=0; i < this.train.getnClasses(); i++)  degreeClass[i] = 0.0;

	for (i = 0; i < this.ruleBase.size(); i++) {
		if (selected[i] > 0) {
			Rule r = this.ruleBase.get(i);
			
			degree = r.matching(example);
			degreeClass[r.getClas()] += degree;
		}
    }

    maxDegree = 0.0;
    for (i = 0; i < this.train.getnClasses(); i++) {
      if (degreeClass[i] > maxDegree) {
        maxDegree = degreeClass[i];
        clas = i;
      }
    }

    return clas;
  }


  private int FRM_AC(double[] example) {
    int i, clas;
	double degree, maxDegree;
	double[] degreeClass;

	clas = defaultRule;

    degreeClass = new double[this.train.getnClasses()];
	for (i=0; i < this.train.getnClasses(); i++)  degreeClass[i] = 0.0;

	for (i = 0; i < this.ruleBase.size(); i++) {
		Rule r = this.ruleBase.get(i);
			
		degree = r.matching(example);
		degreeClass[r.getClas()] += degree;
    }

    maxDegree = 0.0;
    for (i = 0; i < this.train.getnClasses(); i++) {
      if (degreeClass[i] > maxDegree) {
        maxDegree = degreeClass[i];
        clas = i;
      }
    }

    return clas;
  }

  public int hasClassUncovered (int[] selected) {
	  int i, count;
	  int[] cover;
	  
	  cover = new int[this.train.getnClasses()];
	  for (i=0; i < cover.length; i++) {
		  if (this.train.numberInstances(i) > 0)  cover[i] = 0;
		  else  cover[i] = 1;
	  }
	  
	  for (i = 0; i < this.ruleBase.size(); i++) {
		  if (selected[i] > 0) {
			  cover[this.ruleBase.get(i).getClas()]++;
		  }
	  }

	  count = 0;
	  for (i=0; i < cover.length; i++) {
		  if (cover[i] == 0)  count++;
	  }

	  return count;
  }


  public void reduceRules(int clas) {
	  ArrayList<ExampleWeight> exampleWeight;
	  int i, posBestWracc, nExamples, nRuleSelect; 
	  double bestWracc;
	  int[] selected;
	  Rule rule;

	  exampleWeight = new ArrayList<ExampleWeight> ();
	  for (i=0; i < this.train.size(); i++)  exampleWeight.add(new ExampleWeight(this.K));  

	  selected = new int[this.ruleBase.size()];
	  for (i=0; i < this.ruleBase.size(); i++)  selected[i] = 0;

	  nExamples = this.train.numberInstances(clas);
	  nRuleSelect = 0;

	  do {
		  bestWracc = -1.0;
		  posBestWracc = -1;
		  
		  for (i=0; i < this.ruleBase.size(); i++) {
			  if (selected[i] == 0) {
				  rule = this.ruleBase.get(i);
				  rule.calculateWracc(this.train, exampleWeight);

				  if (rule.getWracc() > bestWracc) {
					  bestWracc = rule.getWracc();
					  posBestWracc = i;
				  }
			  }
		  }

		  if (posBestWracc > -1) {
			  selected[posBestWracc] = 1;
			  nRuleSelect++;

			  rule = this.ruleBase.get(posBestWracc);
			  nExamples -= rule.reduceWeight(this.train, exampleWeight);
		  }
	  } while ((nExamples > 0) && (nRuleSelect < this.ruleBase.size()) && (posBestWracc > -1));

	  for (i=this.ruleBase.size() - 1; i >= 0; i--) {
		  if (selected[i] == 0)  this.ruleBase.remove(i);
	  }

	  exampleWeight.clear();
	  System.gc();
  }


  public String printString() {
    int i, j, ant;
    String [] names = this.train.names();
    String [] clases = this.train.clases();
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

  public void saveFile(String filename) {
    String stringOut = new String("");
    stringOut = printString();
    Files.writeFile(filename, stringOut);
  }

}
