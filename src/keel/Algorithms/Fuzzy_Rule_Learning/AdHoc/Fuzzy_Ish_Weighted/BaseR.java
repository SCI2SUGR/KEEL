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
 * <p>Title: BaseR</p>
 *
 * <p>Description: Contains the definition of the rule base</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: KEEL</p>
 *
 * @author A. Fernández
 * @version 1.0
 */

import java.util.*;
import org.core.*;

public class BaseR {

  ArrayList<Regla> ruleBase;
  BaseD dataBase;
  myDataset train;
  int n_vars, ruleWeight, infType, compType;

  public BaseR() {

  }

  public BaseR(BaseD dataBase, myDataset train, int ruleWeight,
               int infType, int compType) {
    ruleBase = new ArrayList<Regla> ();
    this.dataBase = dataBase;
    this.train = train;
    this.n_vars = dataBase.numVariables();
    this.ruleWeight = ruleWeight;
    this.compType = compType;
    this.infType = infType;
  }

  public String printString() {
    int i, j;
    String[] nombres = train.nombres();
    String[] clases = train.clases();
    String cadena = new String("");
    cadena += "@Number of rules: " + ruleBase.size() + "\n\n";
    for (i = 0; i < ruleBase.size(); i++) {
      Regla r = ruleBase.get(i);
      cadena += (i + 1) + ": ";
      for (j = 0; j < n_vars - 1; j++) {
        //cadena += r.dataBase.print(j, r.antecedente[j]);
        cadena += nombres[j] + " IS " + r.dataBase.print(j, r.antecedent[j]) +
            " AND ";
      }
      cadena += nombres[j] + " IS " + r.dataBase.print(j, r.antecedent[j]) +
          ": " +
          clases[r.clas] + " with Rule Weight: " + r.weight + "\n";
    }
    return (cadena);
  }

  public void escribeFichero(String filename) {
    String cadenaSalida = new String("");
    cadenaSalida = printString();
    Fichero.escribeFichero(filename, cadenaSalida);
  }

  public int FRM(double[] example) {
    if (this.infType == Fuzzy_Ish.WINNING_RULE) {
      return FRM_WR(example);
    }
    else {
      return FRM_AC(example);
    }
  }

  private int FRM_WR(double[] example) {
    int clase = -1;
    double max = 0.0;
    for (int i = 0; i < ruleBase.size(); i++) {
      Regla r = ruleBase.get(i);
      double produc = r.compatibilidad(example);
      produc *= r.weight;
      if (produc > max) {
        max = produc;
        clase = r.clas;
      }
    }
    return clase;
  }

  private int FRM_AC(double[] example) {
    int clase = -1;
    double[] grado_clases = new double[1];
    for (int i = 0; i < ruleBase.size(); i++) {
      Regla r = ruleBase.get(i);

      double produc = r.compatibilidad(example);
      produc *= r.weight;
      if (r.clas > grado_clases.length - 1) {
        double[] aux = new double[grado_clases.length];
        for (int j = 0; j < aux.length; j++) {
          aux[j] = grado_clases[j];
        }
        grado_clases = new double[r.clas + 1];
        for (int j = 0; j < aux.length; j++) {
          grado_clases[j] = aux[j];
        }
      }
      grado_clases[r.clas] += produc;
    }
    double max = 0.0;
    for (int l = 0; l < grado_clases.length; l++) {
      //System.err.println("Grado_Clase["+l+"]: "+grado_clases[l]);
      if (grado_clases[l] > max) {
        max = grado_clases[l];
        clase = l;
      }
    }

    return clase;
  }

  /**
   * Genero el conjunto de reglas inicial para cada particion del espacio (2...L)
   */
  public void Generacion() {
    int[] regla = new int[n_vars];
    this.RecorreAntecedentes(regla, 0);
  }

  private void RecorreAntecedentes(int[] Regla_act, int pos) {
    if (pos == n_vars) {
      crearRegla(Regla_act);
    }
    else {
      for (Regla_act[pos] = dataBase.dataBase[0][0].label;
           Regla_act[pos] <
           dataBase.dataBase[0][dataBase.dataBase[0].
           length - 1].label + 1;
           Regla_act[pos]++) {
        RecorreAntecedentes(Regla_act, pos + 1);
      }
    }
  }

  private void crearRegla(int[] antecedente) {
    Regla r = new Regla(dataBase, compType);
    r.assignAntecedent(antecedente);
    r.calcula_consecuente(train);
    if (r.weight > 0) {
      ruleBase.add(r);
    }
  }

  /**
   * This function adjust the certainty degree for the rules
   * @param nu double learning rate
   * @param epochs int number of epochs
   */
  public void learnWeights(double nu, int epochs) {
    for (int k = 0; k < epochs; k++) {
      for (int p = 0; p < train.size(); p++) {
        double[] example = train.getExample(p);
        double max = 0.0;
        int clas = -1, rule = -1;
        for (int i = 0; i < ruleBase.size(); i++) {
          Regla r = ruleBase.get(i);
          double produc = r.compatibilidad(example);
          produc *= r.weight;
          if (produc > max) {
            max = produc;
            clas = r.clas;
            rule = i;
          }
        }
        if (rule > 0) {
          double adjust = 0;
          int real_clas = train.getOutputAsInteger(p);
          if (clas == real_clas) { //correctly classified
            adjust = nu * train.getWeight(real_clas) *
                (1.0 - ruleBase.get(rule).weight);
          }
          else { //misclassified
            adjust = -1.0 * nu * train.getWeight(real_clas) *
                ruleBase.get(rule).weight;
          }
          ruleBase.get(rule).adjustWeight(adjust);
        }
      }
    }
    borrar();
  }

  public void borrar() {
    for (int i = 0; i < ruleBase.size(); ) {
      if (ruleBase.get(i).weight < 0.0) {
        ruleBase.remove(i);
      }
      else {
        i++;
      }
    }
  }

  public BaseR clone() {
    BaseR br = new BaseR();
    br.dataBase = dataBase;
    br.ruleBase = new ArrayList<Regla> ();
    for (int i = 0; i < ruleBase.size(); i++) {
      br.ruleBase.add(ruleBase.get(i).clone());
    }
    br.train = train;
    br.n_vars = n_vars;
    br.ruleWeight = ruleWeight;
    br.infType = infType;
    br.compType = compType;
    return br;
  }

  public int size() {
    return ruleBase.size();
  }

  public void eliminaRegla(int pos) {
    ruleBase.remove(pos);
  }

}

