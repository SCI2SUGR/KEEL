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


/**
 * Class to store a non-fuzzy rule, together with some necessary information to manage the CBA algorithm
 *
 * @author Written by Jesus Alcala (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */
public class Rule implements Comparable {
	
  int[] antecedent;
  int[] classCasesCovered;
  int clas, nAnts, mark;
  long time;
  ArrayList<Replace> replace;
  double conf, supp;
  DataBase dataBase;
  
  /**
   * <p>
   * Copy Constructor
   * </p>
   * @param r Rule to be copied
   */
  public Rule(Rule r) {
    this.antecedent = new int[r.antecedent.length];
    this.classCasesCovered = new int[r.classCasesCovered.length];
	for (int k = 0; k < this.antecedent.length; k++)  this.antecedent[k] = r.antecedent[k];
	for (int k = 0; k < this.classCasesCovered.length; k++)  this.classCasesCovered[k] = r.classCasesCovered[k];

	this.replace = new ArrayList<Replace> ();
	for (int k = 0; k < r.replace.size(); k++)  this.replace.add(r.replace.get(k).clone());

	this.clas = r.clas;
    this.dataBase = r.dataBase;
	this.conf = r.conf;
	this.supp = r.supp;
	this.nAnts = r.nAnts;
	this.mark = r.mark;
	this.time = r.time;
  }

  /**
   * <p>
   * Parameters Constructor
   * </p>
   * @param dataBase Set of training data which is necessary to generate a rule
   */
  public Rule(DataBase dataBase) {
	this.replace = new ArrayList<Replace> ();
    this.antecedent = new int[dataBase.numVariables()];
    this.classCasesCovered = new int[dataBase.numClasses()];
    for (int i = 0; i < this.antecedent.length; i++)  this.antecedent[i] = -1;  // Don't care
    for (int i = 0; i < this.classCasesCovered.length; i++)  this.classCasesCovered[i] = 0;


    this.clas = -1;
    this.dataBase = dataBase;
	this.conf = 0.0;
	this.supp = 0.0;
	this.nAnts = 0;
	this.mark = 0;
	this.time = 0;
  }

  /**
   * <p>
   * Clone Function
   * </p>
   */
  public Rule clone() {
    Rule r = new Rule(this.dataBase);

    for (int i = 0; i < this.antecedent.length; i++)  r.antecedent[i] = this.antecedent[i];
    for (int i = 0; i < this.classCasesCovered.length; i++)  r.classCasesCovered[i] = this.classCasesCovered[i];
	for (int i = 0; i < this.replace.size(); i++)  r.replace.add(this.replace.get(i).clone());

	r.clas = this.clas;
	r.dataBase = this.dataBase;
	r.conf = this.conf;
	r.supp = this.supp;
	r.nAnts = this.nAnts;
	r.mark = this.mark;
	r.time = this.time;

    return (r);
  }

  /**
   * <p>
   * It sets the rule's antecedent
   * </p>
   * @param antecedent Array of values. Each position in the array represents an attribute.
   */
  public void asignaAntecedente(int [] antecedent){
	this.nAnts = 0;
    for (int i = 0; i < antecedent.length; i++) {
		this.antecedent[i] = antecedent[i];
		if (this.antecedent[i] > -1)  this.nAnts++;
	}
  }

  /**
   * <p>
   * It returns the rule's consequent
   * </p>
   * @param clas Consequent of the rule
   */
  public void setConsequent(int clas) {
    this.clas = clas;
  }

  /**
   * <p>
   * Function to check if a given example matchs with the rule (the rule correctly classifies it)
   * </p>
   * @param example Example to be classified
   * @return double 0.0 = doesn't match, >0.0 = does.
   */
  public double matching(int[] example) {
    return (this.degree(example));
  }

  private double degree(int[] example) {
    double degree;

    degree = 1.0;
    for (int i = 0; i < antecedent.length && degree > 0.0; i++) {
      degree *= (double) dataBase.matching(i, antecedent[i], example[i]);
    }

    return (degree);
  }

  /**
   * <p>
   * Function to add a replace entry in the "Replace" list
   * </p>
   * @param r Element to be inserted
   */
  public void addReplace(Replace r) {
    this.replace.add(r);;
  }

  /**
   * <p>
   * Function to check if a rule is equal to another given
   * </p>
   * @param rule Rule to compare with ours
   * @return boolean true = they are equal, false = they aren't.
   */
  public boolean isEqual (Rule rule) {
	  int i;

	  if (this.clas != rule.getClas())  return (false);

	  for (i=0; i < this.antecedent.length; i++)
		  if (this.antecedent[i] != rule.antecedent[i])  return (false);

	  return (true);
  }

  /**
   * <p>
   * It sets the rule's confidence
   * </p>
   * @param conf double Confidence to be set
   */
  public void setConfidence(double conf) {
    this.conf = conf;
  }

  /**
   * <p>
   * It sets the rule's support
   * </p>
   * @param supp double Support to be set
   */
  public void setSupport(double supp) {
    this.supp = supp;
  }

  /**
   * <p>
   * It sets the rule's mark
   * </p>
   * @param mark Whether the rule is marked (1) or not (0)
   */
  public void setMark (int mark) {
    this.mark = mark;
  }

  /**
   * <p>
   * It returns the confidence of the rule
   * </p>
   * @return double Confidence of the rule
   */
  public double getConfidence() {
    return (this.conf);
  }

  /**
   * <p>
   * It returns the Support of the rule
   * </p>
   * @return double Support of the rule
   */
  public double getSupport() {
    return (this.supp);
  }

  /**
   * <p>
   * It returns the output class of the rule
   * </p>
   * @return int Output class of the rule
   */
  public int getClas() {
    return (this.clas);
  }

  /**
   * <p>
   * It returns the time of the rule
   * </p>
   * @return long Time the rule has been added to the rule set
   */
  public long getTime() {
    return (this.time);
  }

  /**
   * <p>
   * It sets the time the rule was added to the rule set
   * </p>
   * @param time Time to be set
   */
  public void setTime(long time) {
    this.time = time;
  }

  /**
   * <p>
   * It returns the mark of the rule
   * </p>
   * @return int 1 = rule is marked, 0 = rule isn't.
   */
  public int getMark() {
    return (this.mark);
  }

  /**
   * <p>
   * Function to mark the rule
   * </p>
   */
  public void onMark() {
    this.mark = 1;
  }

  /**
   * Function to unmark the rule
   */
  public void offMark() {
    this.mark = 0;
  }

  /**
   * <p>
   * It returns the size of the Replace list in the rule
   * </p>
   * @return int Size of the Replace list in the rule
   */
  public int getnReplace() {
    return (this.replace.size());
  }

  /**
   * <p>
   * It returns the Replace element of the rule in the position "pos"
   * </p>
   * @param pos Position of the "replace element" we are looking for
   * @return Replace Replace element of the rule in the position "pos"
   */
  public Replace getReplace(int pos) {
    return (this.replace.get(pos));
  }

  /**
   * <p>
   * It returns if the rule is marked
   * </p>
   * @return boolean true = Rule is marked, false = rule is not.
   */
  public boolean isMark() {
    return (this.mark == 1);
  }

  /**
   * <p>
   * Function to know whether our rule has more precedence than another given or not.
   * </p>
   * @param r Given rule to compare
   * @return boolean true = our rule has more precedence, false = it hasn't.
   */
  public boolean isPrecedence(Rule r) {
	if (this.conf > r.conf)  return (true);
	else if (this.conf < r.conf)  return (false);
	if (this.supp > r.supp)  return (true);
	else if (this.supp < r.supp)  return (false);
	if (this.time < r.time)  return (true);
	else if (this.time > r.time)  return (false);

	return (true);
  }

  /**
   * <p>
   * Function to increase in 1 the number of examples whose output class is the given class "clas" and are covered by this rule
   * </p>
   * @param clas Output class of the covered example
   */
  public void incrCovered (int clas) {
	  this.classCasesCovered[clas]++;
  }

  /**
   * <p>
   * Function to decrease in 1 the number of examples whose output class is the given class "clas" and are covered by this rule
   * </p>
   * @param clas Output class of the covered example
   */
  public void decrCovered (int clas) {
	  this.classCasesCovered[clas]--;
  }

  /**
   * <p>
   * It returns the number of examples covered by the rule for the class "class"
   * </p>
   * @param clas Class to know how many examples with this class are covered by our rule
   * @return int Number of examples covered by our rule for the given class
   */
  public int getclassCasesCovered (int clas) {
	  return (this.classCasesCovered[clas]);
  }

  /**
   * <p>
   * Function to check whether our rule is subset of a given rule "a"
   * </p>
   * @param a Rule Given rule to compare
   * @return boolean true = our rule is subset of a, false = it isn't.
   */
  public boolean isSubset(Rule a) {
	  if ((this.clas != a.clas) || (this.nAnts > a.nAnts))  return (false);
	  else {
		  for (int k = 0; k < this.antecedent.length; k++) {
			  if (this.antecedent[k] > -1) {
				  if (this.antecedent[k] != a.antecedent[k])  return (false);
			  }
		  }
	      return (true);
	  }
  }

  /**
   * It sets the label for a given position in the antecedent (for a given attribute)
   * @param pos Location of the attribute which we want to set the label
   * @param label New label value to set
   */
  public void setLabel(int pos, int label) {
	if ((antecedent[pos] < 0) && (label > -1))  this.nAnts++;
	if ((antecedent[pos] > -1) && (label < 0))  this.nAnts--;
	this.antecedent[pos] = label;
  }

  /**
   * Function to compare objects of the Rule class
   * Necessary to be able to use "sort" function
   * It sorts in an decreasing order of confidence
   * If equals, in an decreasing order of support
   * If equals, in an decreasing order of time
   */
  public int compareTo(Object a) {
	  if (((Rule) a).conf < this.conf)  return -1;
	  else if (((Rule) a).conf > this.conf)  return 1;
		  
	  if (((Rule) a).supp < this.supp)  return -1;
	  else if (((Rule) a).supp > this.supp)  return 1;
		  
	  if (((Rule) a).time < this.time)  return 1;
	  else if (((Rule) a).time > this.time)  return -1;
		  
	  return 0;
  }

}

