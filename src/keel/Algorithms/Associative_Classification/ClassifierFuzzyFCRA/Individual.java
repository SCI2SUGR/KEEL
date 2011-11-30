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

import org.core.Randomize;
import java.lang.*;

/**
 * This class contains the representation of the individuals of the population
 *
 * @author Written by Jesus Alcalá (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */
public class Individual implements Comparable{
  int[] chromosome;
  double fitness, accuracy;
  int lengthSC, n_e;
  double wCAR, wV;

    /**
     * Default Constructor
     */
  public Individual() {
  }

    /**
     * Constructor with parameters
     * @param chromosome int[] Chromosome to build the individual
     * @param wCAR double relative weight of the classification accuracy rate
     * @param wV double relative weight of the number of fuzzy rules
     * @param lengthSC int total length of the individual
     */
  public Individual(int [] chromosome, double wCAR, double wV, int lengthSC) {
    this.wCAR = wCAR;
    this.wV = wV;
	this.lengthSC = lengthSC;
	this.n_e = 1;

    this.chromosome = new int[this.lengthSC * 2];
    for (int i = 0; i < this.chromosome.length; i++)   this.chromosome[i] = chromosome[i];
  }

  public Individual(double wCAR, double wV, int lengthSC) {
    this.wCAR = wCAR;
    this.wV = wV;
	this.lengthSC = lengthSC;
	this.n_e = 1;

    this.chromosome = new int[this.lengthSC * 2];
  }

  /**
   * <p>
   * Initialization of the individual with random values
   * </p>
   */
  public void randomValues () {
    for (int i = 0; i < this.chromosome.length; i++){
      if (Randomize.Rand() < 0.5)  chromosome[i] = 0;
	  else  chromosome[i] = 1;
    }

	this.n_e = 1;
  }

  /**
   * <p>
   * It interchanges the values between the position pointCross1 and pointCross2
   * </p>
   * @param indiv Inidividual an individual
   * @param pointCross1 int left posotion
   * @param pointCross2 int right posotion
   */
  public void interchangeValues(Individual indiv, int pointCross1, int pointCross2){
	 int i, aux;

	 for (i = pointCross1; i <= pointCross2; i++) {
		 aux = this.chromosome[i];
		 this.chromosome[i] = indiv.chromosome[i];
		 indiv.chromosome[i] = aux;
	 }

	this.n_e = 1;
	indiv.n_e = 1;
  }

  /**
   * <p>
   * It returns the number of rules in the rule base
   * </p>
   * @return int Rule base's size
   */
  public int size(){
    return this.chromosome.length;
  }

  /**
   * <p>
   * It applies the mutation operator
   * </p>
   */
  public void mutation(double prob){
    for (int i = 0; i < this.chromosome.length; i++){
      if (Randomize.Rand() < prob) {
		  if (this.chromosome[i] == 0)  this.chromosome[i] = 1;
		  else  this.chromosome[i] = 0;
	      
		  this.n_e = 1;
      }
    }
  }

  /**
   * <p>
   * Clone Function
   * </p>
   */
  public Individual clone(){
    Individual i = new Individual();
    
    i.lengthSC = this.lengthSC;
	i.fitness = this.fitness;
	i.accuracy = this.accuracy;
    i.wCAR = this.wCAR;
	i.wV = this.wV;
	i.n_e = this.n_e;

	i.chromosome = new int[this.chromosome.length];
    for (int j = 0; j < this.chromosome.length; j++)  i.chromosome[j] = this.chromosome[j];
    
	return i;
  }

  /**
   * <p>
   * Function to return if this individual is new in the population
   * </p>
   * @return boolean true = it is-, false = it isn't
   */
  public boolean isNew () {
	  if (this.n_e == 1)  return (true);
	  else  return (false);
  }

  /**
   * <p>
   * Function to return the accuracy of the individual
   * </p>
   * @return double The accuracy of the individual
   */
  public double getAccuracy() {
	  return  this.accuracy;
  }

  /**
   * <p>
   * Function to return the fitness of the individual
   * </p>
   * @return double The fitness of the individual
   */
  public double getFitness() {
	  return  this.fitness;
  }

  /**
   * <p>
   * Function to return the minimum support of the individual
   * </p>
   * @return double The minimum support of the individual
   */
  public double getMinFS () {
	double minFS = 0.0;
	for (int i = 0, j = this.lengthSC - 1; j >= 0; i++, j--) {
		if (this.chromosome[i] > 0)  minFS += Math.pow(2.0, j);
	}
	minFS /= Math.pow(2.0, this.lengthSC);

	return (minFS);
  }

  /**
   * <p>
   * Function to return the minimum confidence of the individual
   * </p>
   * @return double The minimum confidence of the individual
   */
  public double getMinFC () {
	double minFC = 0.0;

	for (int i = this.lengthSC, j = this.lengthSC - 1; j >= 0; i++, j--) {
		if (this.chromosome[i] > 0)  minFC += Math.pow(2.0, j);
	}
	minFC /= Math.pow(2.0, this.lengthSC);

	return (minFC);
  }

  /**
   * <p>
   * Generates the Rule Base with adjusted fuzzy confidences
   * </p>
   * @param apriori Apriori Apriori object 
   * @param n1 double learning rate (Nozaki method)
   * @param n2 double learning rate (Nozaki method)
   * @param Jmax int number of iterations (Nozaki method)
   * @return RuleBase The whole CAR rule set
   */
  public RuleBase generateRB(Apriori apriori, double n1, double n2, int Jmax) {
	double minFS, minFC;
	RuleBase ruleBase;

	minFS = this.getMinFS();
	minFC = this.getMinFC();

    ruleBase = apriori.generateRB(minFS, minFC);
	ruleBase.adaptiveRules(n1, n2, Jmax);

	return ruleBase;
  }

  /**
   * <p>
   * Evaluates this individual
   * </p>
   * @param n1 double learning rate (Nozaki method)
   * @param n2 double learning rate (Nozaki method)
   * @param Jmax int number of iterations (Nozaki method)
   */
  public void evaluate(Apriori apriori, double n1, double n2, int Jmax) {
	RuleBase ruleBase;

    ruleBase = this.generateRB(apriori, n1, n2, Jmax);
	if (ruleBase.size() < 1) {
		this.fitness = Double.NEGATIVE_INFINITY;
	    this.accuracy = 0.0;
	}
	else {
		this.accuracy = ruleBase.evaluate();
		this.fitness = (this.wCAR * ruleBase.getAccuracy()) - (this.wV * ruleBase.size());
	}
	this.n_e = 0;
  }

  /**
   * Function to compare objects of the Individual class
   * Necessary to be able to use "sort" function
   * It sorts in an increasing order of fitness
   */
  public int compareTo(Object a) {
    if ( ( (Individual) a).fitness < this.fitness) {
      return -1;
    }
    if ( ( (Individual) a).fitness > this.fitness) {
      return 1;
    }
    return 0;
  }

}

