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
 * <p>Title: Rule</p>
 *
 * <p>Description: Codifies a Fuzzy Rule</p>
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
import org.core.Randomize;

public class Rule
    implements Comparable {

  int[] antecedent;
  int clas;
  double weight, p_DC;
  int compatibilityType, covered;
  DataBase dataBase;
  boolean n_e, nominals[];

  /**
   * Copy Constructor
   * @param r Rule the rule to be copied
   */
  public Rule(Rule r) {
    this.antecedent = r.antecedent.clone();
    this.dataBase = r.dataBase;
    this.compatibilityType = r.compatibilityType;
    this.nominals = r.nominals.clone();
  }

  /**
   * Constructor with parameters
   * @param dataBase DataBase the DB
   * @param compatibilityType int a code for the compatibility degre computation
   * @param nominals array that indicates the possible nominal attributes of the data-set
   */
  public Rule(DataBase dataBase, int compatibilityType, boolean [] nominals) {
    this.dataBase = dataBase;
    antecedent = new int[dataBase.numVariables()];
    this.compatibilityType = compatibilityType;
    this.nominals = nominals.clone();
  }

  /**
   * Builds a new rule from a heuristic function
   * @param train myDataset the tranning set
   * @param example int an example for building the rule
   * @param p_DC double the don't care probability
   */
  public void buildHeuristic(myDataset train, int example, double p_DC) {
    int labels[] = new int[14];
    double probabilities[] = new double[14];
    double sum;
    int n_variables = train.getnInputs();
    for (int j = 0; j < n_variables; j++) { //Calculo la probabilidad asociada a cada posible antecedent
      if (nominals[j]) {
        antecedent[j] = (int) train.getExample(example)[j];
      }
      else {
        sum = 0.0;
        int etiq;
        for (int k = 0; (k < 4); k++) { //Para cada nivel de granularidad
          for (int l = 0; l < 2 + k; l++) {
            etiq = (int) ( (1.5 * k) + (0.5 * k * k) + l);
            probabilities[etiq] = dataBase.membership(k, j, l,
                train.getExample(example)[j]);
            sum += probabilities[etiq];
          }
        }
        for (int k = 0; k < 14; k++) {
          probabilities[k] /= sum;
          labels[k] = k;
        }
        for (int k = 0; k < 13; k++) {
          for (int l = k + 1; l < 14; l++) {
            if (probabilities[k] > probabilities[l]) {
              sum = probabilities[k];
              probabilities[k] = probabilities[l];
              probabilities[l] = sum;
              etiq = labels[k];
              labels[k] = labels[l];
              labels[l] = etiq;
            }
          }
        }
        for (int k = 1; k < 14; k++) {
          probabilities[k] += probabilities[k - 1];
        }
        sum = Randomize.Rand();
        boolean salir = false;
        for (int k = 0; (k < 14) && (!salir); k++) {
          if (probabilities[k] > sum) {
            antecedent[j] = labels[k];
            salir = true;
          }
        }
      }
    }
    for (int j = 0; j < n_variables; j++) { //Aplico la probabilidad de D.C.
      if (p_DC > Randomize.Rand()) {
        if (nominals[j]) {
          antecedent[j] = -1; //Etiqueta Don't Care
        }
        else {
          antecedent[j] = 14; //Etiqueta Don't Care
        }
      }
    }
    computeConsequent(train);
    n_e = true;

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

    best_class = 0;
    best_comp = sum_classes[0];
    second_comp = sum_classes[1];
    if (second_comp > best_comp) {
      best_class = 1;
      aux = best_comp;
      best_comp = second_comp;
      second_comp = aux;
    }

    for (i = 2; i < n_classes; i++) {
      comp = sum_classes[i];
      if (comp >= best_comp) {
        best_class = i;
        second_comp = best_comp;
        best_comp = comp;
      }
    }

    if (best_comp == second_comp) {
      this.clas = best_class;
      weight = -1.0;
    }

    clas = best_class; //I assign the class
    consequent_PCF4(train); //I assign the rule weight
  }

  /**
   * Sets the class for the rule
   * @param clas int the class id
   */
  public void setClass(int clas) {
    this.clas = clas;
  }

  /**
   * It assigns the rule Weigth
   * @param train myDataset trainnig set
   * @param ruleWeight int code for selecting the rule weight heuristic computation
   */
  public void assignConsequent(myDataset train, int ruleWeight) {
    if (ruleWeight == Fuzzy_Ish.CF) {
      consequent_CF(train);
    }
    else if (ruleWeight == Fuzzy_Ish.PCF_II) {
      consequent_PCF2(train);
    }
    else if (ruleWeight == Fuzzy_Ish.PCF_IV) {
      consequent_PCF4(train);
    }
  }

  public double compatibility(double[] example) {
    if (compatibilityType == Fuzzy_Ish.MINIMUM) {
      return minimum_t_norm(example);
    }
    else {
      return product_t_norm(example);
    }
  }

  /**
   * Minimum T-norm computation
   * @param example double[] Example
   * @return double the minimum T-norm
   */
  private double minimum_t_norm(double[] example) {
    double minimum, membership_degree;
    int label, k;
    minimum = 1.0;
    for (int i = 0; (i < antecedent.length)&&(minimum > 0); i++) {
      label = antecedent[i];
      if (nominals[i]){
        if (label == -1){ //D.C.
          membership_degree = 1.0;
        }else{
          membership_degree = dataBase.membership(0, i, label, example[i]);
        }
      }else{
        if (label <= 1) {
          k = 0;
          membership_degree = dataBase.membership(k, i, label, example[i]);
        }
        else if (label <= 4) {
          k = 1;
          label -= 2;
          membership_degree = dataBase.membership(k, i, label, example[i]);
        }
        else if (label <= 8) {
          k = 2;
          label -= 5;
          membership_degree = dataBase.membership(k, i, label, example[i]);
        }
        else if (label <= 13) {
          k = 3;
          label -= 9;
          membership_degree = dataBase.membership(k, i, label, example[i]);
        }
        else {
          k = 4;
          label = 0;
          membership_degree = 1.0;
        }
      }
      minimum = Math.min(membership_degree, minimum);
    }
    return (minimum);

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
    for (int i = 0; (i < antecedent.length)&&(product > 0); i++) {
      label = antecedent[i];
      if (nominals[i]){
        if (label == -1){ //D.C.
          membership_degree = 1.0;
        }else{
          membership_degree = dataBase.membership(0, i, label, example[i]);
        }
      }else{
        if (label <= 1) {
          k = 0;
          membership_degree = dataBase.membership(k, i, label, example[i]);
        }
        else if (label <= 4) {
          k = 1;
          label -= 2;
          membership_degree = dataBase.membership(k, i, label, example[i]);
        }
        else if (label <= 8) {
          k = 2;
          label -= 5;
          membership_degree = dataBase.membership(k, i, label, example[i]);
        }
        else if (label <= 13) {
          k = 3;
          label -= 9;
          membership_degree = dataBase.membership(k, i, label, example[i]);
        }
        else {
          k = 4;
          label = 0;
          membership_degree = 1.0;
        }
      }
      product = product * membership_degree;
    }
    return (product);
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
    weight = sum_classes[clas] / total;
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
    weight = (sum_classes[clas] - sum) / total;
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
    if (total == 0) {
      weight = -1;
    }
    else {
      weight = (sum_classes[clas] - sum) / total;
    }
  }

  /**
   * It carries out a copy of the current rule
   * @return Rule an exact copy of the rule
   */
  public Rule clone() {
    Rule r = new Rule(dataBase, compatibilityType,nominals);
    r.antecedent = antecedent.clone();
    r.clas = clas;
    r.weight = weight;
    r.p_DC = p_DC;
    r.n_e = n_e;
    return r;
  }

  /**
   * Mutatation Operator. It assigns a random new label for each condition according to the mutation probability.
   * @param train myDataset trainnig set
   * @param prob_mut double mutation probability
   */
  public void mutate(myDataset train, double prob_mut) {
    for (int k = 0; k < antecedent.length; k++) {
      if (prob_mut > Randomize.Rand()) {
        if (nominals[k]){
          antecedent[k] = Randomize.RandintClosed(-1, dataBase.nLabels(k));
        }else{
          antecedent[k] = Randomize.RandintClosed(0, 14);
        }
      }
    }
    computeConsequent(train);
  }

  /**
   * Compares the fitness of two rules for the ordering procedure
   * @param a Object a Rule
   * @return int -1 if the current rule is worst than the one that is compared, 1 for the contrary case and 0
   * if they are equal.
   */
  public int compareTo(Object a) {
    if ( ( (Rule) a).covered < this.covered) {
      return -1;
    }
    if ( ( (Rule) a).covered > this.covered) {
      return 1;
    }
    return 0;
  }

}

