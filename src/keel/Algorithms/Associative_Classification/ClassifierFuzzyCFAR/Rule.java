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
import org.core.Randomize;

/**
 * Codifies a Fuzzy Rule
 *
 * @author Written by Jesus Alcala (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */
public class Rule implements Comparable {

  int[] antecedente;
  int clase, RightN, WrongN, typeOrder;
  long time;
  double DF, Dconf, Dsupp;
  DataBase dataBase;


  /**
   * <p>
   * Copy Constructor
   * </p>
   * @param r Rule Rule to be copied
   */
  public Rule(Rule r) {
    this.antecedente = new int[r.antecedente.length];
    for (int k = 0; k < this.antecedente.length; k++) {
      this.antecedente[k] = r.antecedente[k];
    }
    this.clase = r.clase;
    this.dataBase = r.dataBase;
	this.Dconf = r.Dconf;
	this.Dsupp = r.Dsupp;
	this.time = r.time;
	this.DF = r.DF;
	this.RightN = r.RightN;
	this.WrongN = r.WrongN;
	this.typeOrder = r.typeOrder;
  }


  /**
   * <p>
   * Parameters Constructor
   * </p>
   * @param dataBase Set of training data which is necessary to generate a rule
   */
  public Rule(DataBase dataBase) {
    this.antecedente = new int[dataBase.numVariables()];
    for (int i = 0; i < this.antecedente.length; i++)  this.antecedente[i] = -1;  // Don't care
    this.clase = -1;
    this.dataBase = dataBase;
	this.Dconf = 0;
	this.Dsupp = 0;
	this.time = 0;
	this.DF = 0;
	this.RightN = 0;
	this.WrongN = 0;
	this.typeOrder = 0;
  }


  /**
   * <p>
   * Clone Function
   * </p>
   */
  public Rule clone() {
    Rule r = new Rule(this.dataBase);
    r.antecedente = new int[antecedente.length];
    for (int i = 0; i < this.antecedente.length; i++) {
      r.antecedente[i] = this.antecedente[i];
    }
    r.clase = this.clase;
	r.dataBase = this.dataBase;
	r.Dconf = this.Dconf;
	r.Dsupp = this.Dsupp;
	r.time = this.time;
	r.DF = this.DF;
	r.RightN = this.RightN;
	r.WrongN = this.WrongN;
	r.typeOrder = this.typeOrder;

    return (r);
  }

  /**
   * <p>
   * It sets the antecedent of the rule
   * </p>
   * @param antecedente Antecedent of the rule
   */
  public void asignaAntecedente(int [] antecedente){
    for (int i = 0; i < antecedente.length; i++)  this.antecedente[i] = antecedente[i];
  }

  /**
   * <p>
   * It sets the consequent of the rule
   * </p>
   * @param clas Class of the rule
   */
  public void setConsequent(int clas) {
    this.clase = clas;
  }


    /**
   * <p>
   * Function to check if a given example matchs with the rule (the rule correctly classifies it)
   * </p>
   * @param ejemplo Example to be classified
   * @return 0.0 = doesn't match, >0.0 = does.
   */
  public double matching(double[] ejemplo) {
    return (degreeMinimum(ejemplo));
  }

  private double degreeMinimum(double[] ejemplo) {
    double minimum, degree;

    minimum = 1.0;
    for (int i = 0; i < antecedente.length; i++) {
      degree = dataBase.matching(i, antecedente[i], ejemplo[i]);
	  minimum = Math.min(minimum, degree);
    }

	this.DF = minimum * this.Dconf;
    return (this.DF);
  }


  /**
   * <p>
   * It sets the confidence of the rule
   * </p>
   */
  public void setConfidence(double Dconf) {
    this.Dconf = Dconf;
  }


  /**
   * <p>
   * It sets the support of the rule
   * </p>
   */
  public void setSupport(double Dsupp) {
    this.Dsupp = Dsupp;
  }


  /**
   * <p>
   * It sets the time of the rule
   * </p>
   */
  public void setTime(long time) {
    this.time = time;
  }

  /**
   * <p>
   * It sets to 0 the number of right and wrong of the rule
   * </p>
   */
  public void setIni() {
    this.RightN = 0;
    this.WrongN = 0;
  }

  /**
   * <p>
   * It increases the number of right
   * </p>
   */
  public void incrRightN() {
    this.RightN++;
  }

  /**
   * <p>
   * It increases the number of wrong
   * </p>
   */
  public void incrWrongN() {
    this.WrongN++;
  }

  /**
   * <p>
   * It selects to order by precede
   * </p>
   */
  public void orderPrecede() {
	  this.typeOrder = 0;
  }

  /**
   * <p>
   * It selects to order by DF
   * </p>
   */
  public void orderDF() {
	  this.typeOrder = 1;
  }


  /**
   * <p>
   * It returns the Confidence of the rule
   * </p>
   * @return Confidence of the rule
   */
  public double getConfidence() {
    return (this.Dconf);
  }


  /**
   * <p>
   * It returns the support of the rule
   * </p>
   * @return Support of the rule
   */
  public double getSupport() {
    return (this.Dsupp);
  }


  /**
   * <p>
   * It returns the time of the rule
   * </p>
   * @return Time of the rule
   */
  public long getTime() {
    return (this.time);
  }


  /**
   * <p>
   * It returns the number of right of the rule
   * </p>
   * @return Rights of the rule
   */
  public int getRightN() {
    return (this.RightN);
  }

  /**
   * <p>
   * It returns the number of wrong of the rule
   * </p>
   * @return Wrongs of the rule
   */
  public int getWrongN() {
    return (this.WrongN);
  }

  /**
   * <p>
   * It returns the DF of the rule
   * </p>
   * @return DF of the rule
   */
  public double getDF() {
    return (this.DF);
  }

  /**
   * <p>
   * It returns the output class of the rule
   * </p>
   * @return Output class of the rule
   */
  public int getClas() {
    return (this.clase);
  }

  /**
   * <p>
   * Function to check if a given rule is a subrule of this rule
   * </p>
   * @param a Rule to be examinated
   * @return false = it isn't, true = it is.
   */
  public boolean isSubset(Rule a) {
	  for (int k = 0; k < this.antecedente.length; k++) {
		  if (this.antecedente[k] > -1) {
			  if (this.antecedente[k] != a.antecedente[k])  return (false);
		  }
	  }

	  return (true);
  }

  /**
   * It sets the label for a given position in the antecedent (for a given attribute)
   * @param pos Location of the attribute which we want to set the label
   * @param label New label value to set
   */
  public void setLabel(int pos, int label) {
	this.antecedente[pos] = label;
  }


  /**
   * Function to compare objects of the Rule class
   * Necessary to be able to use "sort" function
   */
  public int compareTo(Object a) {
	  if (this.typeOrder < 1) {
		  if (((Rule) a).Dconf < this.Dconf)  return -1;
		  else if (((Rule) a).Dconf > this.Dconf)  return 1;
		  
		  if (((Rule) a).Dsupp < this.Dsupp)  return -1;
		  else if (((Rule) a).Dsupp > this.Dsupp)  return 1;
		  
		  if (((Rule) a).time < this.time)  return 1;
		  else if (((Rule) a).time > this.time)  return -1;
		  
		  return 0;
	  }
	  else {
		  if (((Rule) a).DF < this.DF)  return -1;
		  if (((Rule) a).DF > this.DF)  return 1;
		  return 0;
	  }
  }

}
