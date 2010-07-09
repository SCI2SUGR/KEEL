package keel.Algorithms.Fuzzy_Rule_Learning.AdHoc.Chi_RW;

/**
 * <p>Title: Rule</p>
 *
 * <p>Description: This class contains the structure of a Fuzzy Rule</p>
 *
 * <p>Copyright: Copyright KEEL (c) 2007</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Alberto Fernández (University of Granada) 29/10/2007
 * @version 1.0
 * @since JDK1.5
 */

public class Rule {

  Fuzzy[] antecedent;
  int clas;
  double weight;
  int compatibilityType;

  /**
   * Default constructor
   */
  public Rule() {
  }

  /**
   * Constructor with parameters
   * @param n_variables int
   * @param compatibilityType int
   */
  public Rule(int n_variables, int compatibilityType) {
    antecedent = new Fuzzy[n_variables];
    this.compatibilityType = compatibilityType;
  }

  /**
   * It assigns the class of the rule
   * @param clas int
   */
  public void setClass(int clas) {
      this.clas = clas;
  }

  /**
   * It assigns the rule weight to the rule
   * @param train myDataset the training set
   * @param ruleWeight int the type of rule weight
   */
  public void assingConsequent(myDataset train, int ruleWeight) {
    if (ruleWeight == Fuzzy_Chi.CF) {
      consequent_CF(train);
    }
    else if (ruleWeight == Fuzzy_Chi.PCF_II) {
      consequent_PCF2(train);
    }
    else if (ruleWeight == Fuzzy_Chi.PCF_IV) {
      consequent_PCF4(train);
    }
    else if (ruleWeight == Fuzzy_Chi.NO_RW) {
      weight = 1.0;
    }
  }

  /**
   * It computes the compatibility of the rule with an input example
   * @param example double[] The input example
   * @return double the degree of compatibility
   */
  public double compatibility(double[] example) {
    if (compatibilityType == Fuzzy_Chi.MINIMUM) {
      return minimumCompatibility(example);
    }
    else {
      return productCompatibility(example);
    }
  }

  /**
   * Operator T-min
   * @param example double[] The input example
   * @return double the computation the the minimum T-norm
   */
  private double minimumCompatibility(double[] example) {
    double minimum, membershipDegree;
    minimum = 1.0;
    for (int i = 0; i < antecedent.length; i++) {
      membershipDegree = antecedent[i].Fuzzify(example[i]);
      minimum = Math.min(membershipDegree, minimum);
    }
    return (minimum);

  }

  /**
   * Operator T-product
   * @param example double[] The input example
   * @return double the computation the the product T-norm
   */
  private double productCompatibility(double[] example) {
    double product, membershipDegree;
    product = 1.0;
    for (int i = 0; i < antecedent.length; i++) {
      membershipDegree = antecedent[i].Fuzzify(example[i]);
      product = product * membershipDegree;
    }
    return (product);
  }

  /**
   * Classic Certainty Factor weight
   * @param train myDataset training dataset
   */
  private void consequent_CF(myDataset train) {
    double[] classes_sum = new double[train.getnClasses()];
    for (int i = 0; i < train.getnClasses(); i++) {
      classes_sum[i] = 0.0;
    }

    double total = 0.0;
    double comp;
    /* Computation of the sum by classes */
    for (int i = 0; i < train.size(); i++) {
      comp = this.compatibility(train.getExample(i));
      classes_sum[train.getOutputAsInteger(i)] += comp;
      total += comp;
    }
    weight = classes_sum[clas] / total;
  }

  /**
   * Penalized Certainty Factor weight II (by Ishibuchi)
   * @param train myDataset training dataset
   */
  private void consequent_PCF2(myDataset train) {
    double[] classes_sum = new double[train.getnClasses()];
    for (int i = 0; i < train.getnClasses(); i++) {
      classes_sum[i] = 0.0;
    }

    double total = 0.0;
    double comp;
    /* Computation of the sum by classes */
    for (int i = 0; i < train.size(); i++) {
      comp = this.compatibility(train.getExample(i));
      classes_sum[train.getOutputAsInteger(i)] += comp;
      total += comp;
    }
    double sum = (total - classes_sum[clas]) / (train.getnClasses() - 1.0);
    weight = (classes_sum[clas] - sum) / total;
  }

  /**
   * Penalized Certainty Factor weight IV (by Ishibuchi)
   * @param train myDataset training dataset
   */
  private void consequent_PCF4(myDataset train) {
    double[] classes_sum = new double[train.getnClasses()];
    for (int i = 0; i < train.getnClasses(); i++) {
      classes_sum[i] = 0.0;
    }

    double total = 0.0;
    double comp;
    /* Computation of the sum by classes */
    for (int i = 0; i < train.size(); i++) {
      comp = this.compatibility(train.getExample(i));
      classes_sum[train.getOutputAsInteger(i)] += comp;
      total += comp;
    }
    double sum = total - classes_sum[clas];
    weight = (classes_sum[clas] - sum) / total;
  }

  /**
   * This function detects if one rule is already included in the Rule Set
   * @param r Rule Rule to compare
   * @return boolean true if the rule already exists, else false
   */
  public boolean comparison(Rule r) {
    int contador = 0;
    for (int j = 0; j < antecedent.length; j++) {
      if (this.antecedent[j].label == r.antecedent[j].label) {
        contador++;
      }
    }
    if (contador == antecedent.length) {
      if (this.clas != r.clas) { //Comparison of the rule weights
        if (this.weight < r.weight) {
          //Rule Update
          this.clas = r.clas;
          this.weight = r.weight;
        }
      }
      return true;
    }
    return false;
  }

}
