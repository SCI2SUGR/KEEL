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

package keel.Algorithms.Fuzzy_Rule_Learning.AdHoc.Fuzzy_Ish_Weighted;

/**
 * <p>Title: Regla</p>
 *
 * <p>Description: Contains the definition of a fuzzy rule</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: KEEL</p>
 *
 * @author A. Fernández
 * @version 1.0
 */

public class Regla{

  int[] antecedent;
  int clas;
  double weight;
  int compatibilityType, covered;
  BaseD dataBase;

  /**
   * Copy constructor
   * @param r Regla the rule to be copied
   */
  public Regla(Regla r) {
    this.antecedent = new int[r.antecedent.length];
    for (int k = 0; k < this.antecedent.length; k++) {
      this.antecedent[k] = r.antecedent[k];
    }
    this.dataBase = r.dataBase;
    this.compatibilityType = r.compatibilityType;
  }

  /**
   * Constructor with parameters
   * @param n_variables int
   * @param compatibilityType int
   */
  public Regla(BaseD dataBase, int compatibilityType) {
    this.dataBase = dataBase;
    antecedent = new int[dataBase.numVariables()];
    this.compatibilityType = compatibilityType;
  }

  public void assignAntecedent(int [] antecedent){
    for (int i = 0; i < antecedent.length; i++){
      this.antecedent[i] = antecedent[i];
    }
  }

  /**
   * It computes the best consequent (class and rule weight) for the given rule
   * @param train the training data-set (set of examples)
   */
  public void calcula_consecuente(myDataset train) {
    int i, mejor_clas;
    int n_class = train.getnClasses();
    double comp, total, mejor_comp, segun_comp, aux;
    double[] sumaclass = new double[n_class];

    /* sumaclass acumulara el grado de compatibilidad del antecedent de la
       regla con los ejemplos de cada clas */

    for (i = 0; i < n_class; i++) {
      sumaclass[i] = 0.0;
    }

    total = 0.0;
    /* Se calcula la suma por class */
    for (i = 0; i < train.size(); i++) {
      comp = compatibilidad(train.getExample(i));
      if (comp > 0.0) {
        //System.err.print("comp -> "+comp);
        comp *= train.getWeight(train.getOutputAsInteger(i));
        //System.err.println(", comp2 -> "+comp);
        sumaclass[train.getOutputAsInteger(i)] += comp;
        total += comp;
      }
    }

    mejor_clas = 0;
    mejor_comp = sumaclass[0];
    segun_comp = sumaclass[1];
    if (segun_comp > mejor_comp) {
      mejor_clas = 1;
      aux = mejor_comp;
      mejor_comp = segun_comp;
      segun_comp = aux;
    }

    for (i = 2; i < n_class; i++) {
      comp = sumaclass[i];
      if (comp >= mejor_comp) {
        mejor_clas = i;
        segun_comp = mejor_comp;
        mejor_comp = comp;
      }
    }

    if (mejor_comp == segun_comp) {
      this.clas = mejor_clas;
      weight = -1.0;
    }

    clas = mejor_clas; //Asigno la clas
    consecuente_PCF2(train); //Asigno el weight
  }

  /**
   * It assigns the class of the rule
   * @param clas int
   */
  public void setclas(int clas) {
    this.clas = clas;
  }

  /**
   * It assigns the rule weight to the rule
   * @param train myDataset the training set
   * @param ruleWeight int the type of rule weight
   */
  public void asignaConsecuente(myDataset train, int ruleWeight) {
    if (ruleWeight == Fuzzy_Ish.CF) {
      consecuente_CF(train);
    }
    else if (ruleWeight == Fuzzy_Ish.PCF_II) {
      consecuente_PCF2(train);
    }
    else if (ruleWeight == Fuzzy_Ish.PCF_IV) {
      consecuente_PCF4(train);
    }
  }

  /**
   * It computes the compatibility of the rule with an input example
   * @param example double[] The input example
   * @return double the degree of compatibility
   */
  public double compatibilidad(double[] example) {
    if (compatibilityType == Fuzzy_Ish.MINIMUM) {
      return compatibilityMinimum(example);
    }
    else {
      return compatibilityProduct(example);
    }
  }

  /**
   * Operator T-min
   * @param example double[] The input example
   * @return double the computation the the minimum T-norm
   */
  private double compatibilityMinimum(double[] ejemplo) {
    double minimo, grado_pertenencia;
    int etiqueta;
    minimo = 1.0;
    for (int i = 0; (i < antecedent.length)&&(minimo > 0); i++) {
      etiqueta = antecedent[i];
      grado_pertenencia = dataBase.membership(i, etiqueta, ejemplo[i]);
      minimo = Math.min(grado_pertenencia, minimo);
    }
    return (minimo);

  }

  /**
   * Operator T-product
   * @param example double[] The input example
   * @return double the computation the the product T-norm
   */
  private double compatibilityProduct(double[] ejemplo) {
    double producto, grado_pertenencia;
    int etiqueta;
    producto = 1.0;
    for (int i = 0; i < (antecedent.length)&&(producto > 0); i++) {
      etiqueta = antecedent[i];
      grado_pertenencia = dataBase.membership(i, etiqueta, ejemplo[i]);
      producto = producto * grado_pertenencia;
    }
    return (producto);
  }

  /**
   * Classic Certainty Factor weight
   * @param train myDataset training dataset
   */
  private void consecuente_CF(myDataset train) {
    double[] sumaclass = new double[train.getnClasses()];
    for (int i = 0; i < train.getnClasses(); i++) {
      sumaclass[i] = 0.0;
    }

    double total = 0.0;
    double comp;
    /* Se calcula la suma por class */
    for (int i = 0; i < train.size(); i++) {
      comp = this.compatibilidad(train.getExample(i));
      comp *= train.getWeight(train.getOutputAsInteger(i));
      sumaclass[train.getOutputAsInteger(i)] += comp;
      total += comp;
    }
    weight = sumaclass[clas] / total;
  }

  /**
   * Penalized Certainty Factor weight II (by Ishibuchi)
   * @param train myDataset training dataset
   */
  private void consecuente_PCF2(myDataset train) {
    double[] sumaclass = new double[train.getnClasses()];
    for (int i = 0; i < train.getnClasses(); i++) {
      sumaclass[i] = 0.0;
    }

    double total = 0.0;
    double comp;
    /* Se calcula la suma por class */
    for (int i = 0; i < train.size(); i++) {
      comp = this.compatibilidad(train.getExample(i));
      comp *= train.getWeight(train.getOutputAsInteger(i));
      sumaclass[train.getOutputAsInteger(i)] += comp;
      total += comp;
    }
    double suma = (total - sumaclass[clas]) / (train.getnClasses() - 1.0);
    weight = (sumaclass[clas] - suma) / total;
  }

  /**
   * Penalized Certainty Factor weight IV (by Ishibuchi)
   * @param train myDataset training dataset
   */  
  private void consecuente_PCF4(myDataset train) {
    double[] sumaclass = new double[train.getnClasses()];
    for (int i = 0; i < train.getnClasses(); i++) {
      sumaclass[i] = 0.0;
    }

    double total = 0.0;
    double comp;
    /* Se calcula la suma por class */
    for (int i = 0; i < train.size(); i++) {
      comp = this.compatibilidad(train.getExample(i));
      comp *= train.getWeight(train.getOutputAsInteger(i));
      sumaclass[train.getOutputAsInteger(i)] += comp;
      total += comp;
    }
    double suma = total - sumaclass[clas];
    if (total == 0) {
      weight = -1;
    }
    else {
      weight = (sumaclass[clas] - suma) / total;
    }
  }

  /**
   * It modifies the rule weight adding a given quantity
   * @param adjust double the value to be added to the rule weight
   */
  public void adjustWeight(double adjust){
    this.weight += adjust;
  }

  /**
   * It creates a copy of the rule
   * @return a rule with the same features than the former
   */
  public Regla clone() {
    Regla r = new Regla(dataBase, compatibilityType);
    r.antecedent = new int[antecedent.length];
    for (int i = 0; i < this.antecedent.length; i++) {
      r.antecedent[i] = this.antecedent[i];
    }
    r.clas = clas;
    r.weight = weight;
    return r;
  }

}
