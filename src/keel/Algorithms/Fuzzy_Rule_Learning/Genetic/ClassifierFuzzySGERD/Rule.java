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
 * <p>Title: Rule</p>
 *
 * <p>Description: Codifies a Fuzzy Rule</p>
 *
 * <p>Copyright: KEEL Copyright (c) 2008</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Alberto Fernández (University of Granada) 29/10/2007
 * @author Modified by Jesus Alcala Fernandez (University of Granada) 20/05/2009
 * @version 1.5
 * @since JDK1.5
 */

import java.util.*;
import org.core.Randomize;

public class Rule
    implements Comparable {

  int[] antecedent;
  int clas;
  double threshold, subspaceSize, fitness;
  int active, kj, firstActive, typeEvaluation, newRule;
  DataBase dataBase;

  /**
   * Copy Constructor
   * @param r Rule the rule to be copied
   */
  public Rule(Rule r) {
    this.antecedent = r.antecedent.clone();
    this.dataBase = r.dataBase;
    this.clas = r.clas;
    this.active = 0;
    this.dataBase = r.dataBase;
    this.typeEvaluation = r.typeEvaluation;
    this.newRule = r.newRule;
    this.threshold = r.threshold;
    this.subspaceSize = r.subspaceSize;
    this.fitness = r.fitness;
    this.active = r.active;
    this.kj = r.kj;
    this.firstActive = r.firstActive;
  }

  /**
   * Constructor with parameters
   * @param dataBase DataBase the DB
   * @param typeEvaluation int a code for the compatibility degre computation
   */
  public Rule(DataBase dataBase, int typeEvaluation) {
    this.antecedent = new int[dataBase.numVariables()];
    for (int i = 0; i < this.antecedent.length; i++) {
      this.antecedent[i] = -1; // Don't care
    }
    this.clas = -1;
    this.dataBase = dataBase;
    this.typeEvaluation = typeEvaluation;
    this.active = 0;
    this.firstActive = -1;
    this.newRule = 0;
    this.kj = 0;
  }

  /**
   * It assings the ancedent of the Rule
   * @param antecedent int[] An array containing the fuzzy labels
   */
  public void asignAntecedent(int[] antecedent) {
    boolean first;
    first = true;
    this.active = 0;
    this.firstActive = -1;
    for (int i = 0; i < antecedent.length; i++) {
      this.antecedent[i] = antecedent[i];
      if (this.antecedent[i] >= 0) {
        this.active++;
        if (first) {
          this.firstActive = i;
          first = false;
        }
      }
    }
  }

  /**
   * It carries out a copy of the current rule
   * @return Rule an exact copy of the rule
   */
  public Rule clone() {
    Rule r = new Rule(this.dataBase, this.typeEvaluation);
    r.antecedent = new int[antecedent.length];
    for (int i = 0; i < this.antecedent.length; i++) {
      r.antecedent[i] = this.antecedent[i];
    }
    r.clas = this.clas;
    r.dataBase = this.dataBase;
    r.typeEvaluation = this.typeEvaluation;
    r.newRule = this.newRule;
    r.threshold = this.threshold;
    r.subspaceSize = this.subspaceSize;
    r.fitness = this.fitness;
    r.active = this.active;
    r.kj = this.kj;
    r.firstActive = this.firstActive;

    return (r);
  }

  /**
   * Procedure to compute the best consequent for a given rule
   * @param train myDataset the tranning set
   */
  public void computeConsequent(myDataset train) {
    int i, best_class;
    int n_classes = train.getnClasses();
    double comp, total, best_comp, second_comp, aux;
    double[] sum_classes = new double[n_classes];

    /* sum_classes accumulates the compatibility degree of the antecedent of the rule
       with the examples of each class */

    for (i = 0; i < n_classes; i++) {
      sum_classes[i] = 0.0;
    }

    total = 0.0;
    /* We computed the sum per classes */
    for (i = 0; i < train.size(); i++) {
      comp = compatibility(train.getExample(i));
      if (comp > 0.0) {
        sum_classes[train.getOutputAsInteger(i)] += comp;
        total += comp;
      }
    }

    for (i = 0; i < n_classes; i++) {
      sum_classes[i] /= total;
    }

    best_class = 0;
    best_comp = sum_classes[0];

    for (i = 1; i < n_classes; i++) {
      comp = sum_classes[i];
      if (comp >= best_comp) {
        best_class = i;
        best_comp = comp;
      }
    }

    this.clas = best_class; //I assign the class
  }

  /**
   * Evaluates the rule for computing its fitness
   * @param train myDataset the trianing set
   * @return double the fitness of the rule
   */
  public double evaluation(myDataset train) {
    int i, numberPositiveExamplesDS, label, partition;
    double comp, total;
    double negativeExamples, positiveExamples;

    this.kj = this.lengthRule();
    this.threshold = Math.pow (0.5, this.kj);

    total = 0.0;
    positiveExamples = negativeExamples = 0.0;
    numberPositiveExamplesDS = 0;
    /* Se calcula la suma por clases */
    for (i = 0; i < train.size(); i++) {
      comp = compatibility(train.getExample(i));
      if (comp > 0.0) {
        if (train.getOutputAsInteger(i) == this.clas) {
          positiveExamples += comp;
          if (comp > this.threshold) {
            numberPositiveExamplesDS++;
          }
        }
        else {
          negativeExamples += comp;
        }

        total += comp;
      }
    }

    if (this.typeEvaluation == 0) {
      this.fitness = Math.pow(positiveExamples, 2.0) / total; // fCS (8)
    }
    else if (this.typeEvaluation == 1) {
      this.fitness = positiveExamples - negativeExamples; // fF (10)
    }
    else {
      this.fitness = (positiveExamples - negativeExamples) + (numberPositiveExamplesDS * (1.0 - this.threshold)); // fFModified (13)
    }

    this.subspaceSize = 1;
    for (i = 0; i < this.antecedent.length; i++) {
      if (this.isActive(i)) {
        partition = this.dataBase.partition(this.antecedent[i]);
        label = this.dataBase.label(this.antecedent[i]);
        this.subspaceSize *= this.dataBase.covering(partition, i, label);
      }
    }

    if (this.kj <= (train.getnInputs() / 2.0)) {
      return (this.fitness);
    }
    else {
      this.fitness = (this.subspaceSize / Math.pow(train.getOverlapping(this.clas), this.kj)) * this.fitness;
      return (this.fitness);
    }
  }

  /**
   * Sets the class for the rule
   * @param clas int the class id
   */
  public void setClass(int clas) {
    this.clas = clas;
  }

  /**
   * It sets the class for this rule
   * @param train myDataset the training set
   */
  public void setConsequent(myDataset train) {
    computeConsequent(train);
  }

  /**
   * It computes the compatibility degree of an example for this rule
   * @param example double[] the example
   * @return double the compatibility degree
   */
  public double compatibility(double[] example) {
    return product_t_norm(example);
  }

  /**
   * Product T-norm computation
   * @param example double[] Example
   * @return double the product T-norm
   */
  private double product_t_norm(double[] example) {
    double product, membership_degree;
    int label, k;
    product = 1.0;
    for (int i = 0; (i < antecedent.length) && (product > 0); i++) {
      label = antecedent[i];
      membership_degree = dataBase.membership(dataBase.partition(label), i, dataBase.label(label), example[i]);
      product = product * membership_degree;
    }
    return (product);
  }

  /**
   * Sets this rule as new
   */
  public void onNew() {
    this.newRule = 1;
  }

  /**
   * Sets this rule as "non-new"
   */
  public void offNew() {
    this.newRule = 0;
  }

  /**
   * It returns if the rule is new or not
   * @return boolean true if the rule is new, false otherwise
   */
  public boolean isNew() {
    if (this.newRule > 0) {
      return (true);
    }
    else {
      return (false);
    }
  }

  /**
   * It returns the class of the rule
   * @return int the class of the rule
   */
  public int getClas() {
    return (this.clas);
  }

  /**
   * It returns the activation of this rule
   * @return int the activation of this rule
   */
  public int getActive() {
    return (this.active);
  }

  /**
   * It returns the number of valid labels for the rule until a given position
   * @param value int the position of the rule
   * @return int the number of valid labels for the rule until a given position
   */
  public int getPosActive(int value) {
    int pos, nActive;

    for (pos = 0, nActive = 0; nActive < value; pos++) {
      if (this.antecedent[pos] >= 0) {
        nActive++;
      }
    }
    pos--;

    return (pos);
  }

  /**
   * It returns the length of the rule
   * @return int the length of the rule
   */
  public int lengthRule() {
    return (this.active);
  }

  /**
   * It returns if a given condition is active or not
   * @param pos int the position
   * @return boolean true if the condition has a valid label, false otherwise
   */
  public boolean isActive(int pos) {
    if (this.antecedent[pos] >= 0) {
      return (true);
    }
    else {
      return (false);
    }
  }

  /**
   * Classical Certainty Factor Computation (confidence of the rule)
   * @param train myDataset trainning set
   */
  private void consequent_CF(myDataset train) {
    double[] sum_classes = new double[train.getnClasses()];
    for (int i = 0; i < train.getnClasses(); i++) {
      sum_classes[i] = 0.0;
    }

    double total = 0.0;
    double comp;
    /* We compute the sum per classes */
    for (int i = 0; i < train.size(); i++) {
      comp = this.compatibility(train.getExample(i));
      sum_classes[train.getOutputAsInteger(i)] += comp;
      total += comp;
    }
    //weight = sum_classes[clas] / total;
  }

  /**
   * Heuristic Certainty Factor Computation by Ishibuchi. Rule Weight Heuristic II
   * @param train myDataset trainning set
   */
  private void consequent_PCF2(myDataset train) {
    double[] sum_classes = new double[train.getnClasses()];
    for (int i = 0; i < train.getnClasses(); i++) {
      sum_classes[i] = 0.0;
    }

    double total = 0.0;
    double comp;
    /* We compute the sum per classes */
    for (int i = 0; i < train.size(); i++) {
      comp = this.compatibility(train.getExample(i));
      sum_classes[train.getOutputAsInteger(i)] += comp;
      total += comp;
    }
    double sum = (total - sum_classes[clas]) / (train.getnClasses() - 1.0);
  }

  /**
   * Heuristic Certainty Factor Computation by Ishibuchi. Rule Weight Heuristic IV (penalised certainty factor)
   * @param train myDataset trainning set
   */
  private void consequent_PCF4(myDataset train) {
    double[] sum_classes = new double[train.getnClasses()];
    for (int i = 0; i < train.getnClasses(); i++) {
      sum_classes[i] = 0.0;
    }

    double total = 0.0;
    double comp;
    /* We compute the sum per classes */
    for (int i = 0; i < train.size(); i++) {
      comp = this.compatibility(train.getExample(i));
      sum_classes[train.getOutputAsInteger(i)] += comp;
      total += comp;
    }
    double sum = total - sum_classes[clas];
  }

  /**
   * It sets a new label for the rule
   * @param pos int the position in the antecedent
   * @param label int the label id
   */
  public void setLabel(int pos, int label) {
        if ((this.antecedent[pos] < 0) && (label >= 0))  this.active++;
        if ((this.antecedent[pos] >= 0) && (label < 0))  this.active--;
        this.antecedent[pos] = label;
  }

  /**
   * Compares the fitness of two rules for the ordering procedure
   * @param a Object a Rule
   * @return int -1 if the current rule is worst than the one that is compared, 1 for the contrary case and 0
   * if they are equal.
   */
  public int compareTo(Object a) {
    if ( ( (Rule) a).fitness < this.fitness) {
      return -1;
    }
    if ( ( (Rule) a).fitness > this.fitness) {
      return 1;
    }
    return 0;
  }

}

