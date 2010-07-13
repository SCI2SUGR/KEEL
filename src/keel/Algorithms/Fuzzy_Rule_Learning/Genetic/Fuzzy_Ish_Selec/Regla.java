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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Fuzzy_Ish_Selec;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

import java.util.*;
import org.core.Randomize;

public class Regla
    implements Comparable {

  int[] antecedente;
  int clase;
  double peso, p_DC;
  int tipoCompatibilidad, cubiertos;
  BaseD baseDatos;
  boolean n_e;

  public Regla(Regla r) {
    this.antecedente = new int[r.antecedente.length];
    for (int k = 0; k < this.antecedente.length; k++) {
      this.antecedente[k] = r.antecedente[k];
    }
    this.baseDatos = r.baseDatos;
    this.tipoCompatibilidad = r.tipoCompatibilidad;
  }

  public Regla(BaseD baseDatos, int tipoCompatibilidad) {
    this.baseDatos = baseDatos;
    antecedente = new int[baseDatos.numVariables()];
    this.tipoCompatibilidad = tipoCompatibilidad;
  }

  public void construyeHeuristica(myDataset train, int ejemplo, double p_DC) {
    int etiquetas[] = new int[14];
    double probabilidades[] = new double[14];
    double suma;
    int n_variables = train.getnInputs();
    for (int j = 0; j < n_variables; j++) { //Calculo la probabilidad asociada a cada posible antecedente
      suma = 0.0;
      int etiq;
      for (int k = 0; (k < 4); k++) { //Para cada nivel de granularidad
        for (int l = 0; l < 2 + k; l++) {
          etiq = (int) ( (1.5 * k) + (0.5 * k * k) + l);
          probabilidades[etiq] = baseDatos.pertenencia(k, j, l,
              train.getExample(ejemplo)[j]);
          suma += probabilidades[etiq];
        }
      }
      for (int k = 0; k < 14; k++) {
        probabilidades[k] /= suma;
        etiquetas[k] = k;
      }
      for (int k = 0; k < 13; k++) {
        for (int l = k + 1; l < 14; l++) {
          if (probabilidades[k] > probabilidades[l]) {
            suma = probabilidades[k];
            probabilidades[k] = probabilidades[l];
            probabilidades[l] = suma;
            etiq = etiquetas[k];
            etiquetas[k] = etiquetas[l];
            etiquetas[l] = etiq;
          }
        }
      }
      for (int k = 1; k < 14; k++) {
        probabilidades[k] += probabilidades[k - 1];
      }
      suma = Randomize.Rand();
      boolean salir = false;
      for (int k = 0; (k < 14) && (!salir); k++) {
        if (probabilidades[k] > suma) {
          antecedente[j] = etiquetas[k];
          salir = true;
        }
      }
    }
    for (int j = 0; j < n_variables; j++) { //Aplico la probabilidad de D.C.
      if (p_DC > Randomize.Rand()) {
        antecedente[j] = 14; //Etiqueta Don't Care
      }
    }
    calcula_consecuente(train);
    n_e = true;

  }

  public void asignaAntecedente(int [] antecedente){
    for (int i = 0; i < antecedente.length; i++){
      this.antecedente[i] = antecedente[i];
    }
  }

  public void calcula_consecuente(myDataset train) {
    int i, mejor_clase;
    int n_clases = train.getnClasses();
    double comp, total, mejor_comp, segun_comp, aux;
    double[] sumaclases = new double[n_clases];

    /* sumaclases acumulara el grado de compatibilidad del antecedente de la
       regla con los ejemplos de cada clase */

    for (i = 0; i < n_clases; i++) {
      sumaclases[i] = 0.0;
    }

    total = 0.0;
    /* Se calcula la suma por clases */
    for (i = 0; i < train.size(); i++) {
      comp = compatibilidad(train.getExample(i));
      if (comp > 0.0) {
        sumaclases[train.getOutputAsInteger(i)] += comp;
        total += comp;
      }
    }

    mejor_clase = 0;
    mejor_comp = sumaclases[0];
    segun_comp = sumaclases[1];
    if (segun_comp > mejor_comp) {
      mejor_clase = 1;
      aux = mejor_comp;
      mejor_comp = segun_comp;
      segun_comp = aux;
    }

    for (i = 2; i < n_clases; i++) {
      comp = sumaclases[i];
      if (comp >= mejor_comp) {
        mejor_clase = i;
        segun_comp = mejor_comp;
        mejor_comp = comp;
      }
    }

    if (mejor_comp == segun_comp) {
      this.clase = mejor_clase;
      peso = -1.0;
    }

    clase = mejor_clase; //Asigno la clase
    consecuente_PCF4(train); //Asigno el peso
  }

  public void setClase(int clase) {
    this.clase = clase;
  }

  public void asignaConsecuente(myDataset train, int pesoRegla) {
    if (pesoRegla == Fuzzy_Ish.CF) {
      consecuente_CF(train);
    }
    else if (pesoRegla == Fuzzy_Ish.PCF_II) {
      consecuente_PCF2(train);
    }
    else if (pesoRegla == Fuzzy_Ish.PCF_IV) {
      consecuente_PCF4(train);
    }
  }

  public double compatibilidad(double[] ejemplo) {
    if (tipoCompatibilidad == Fuzzy_Ish.MINIMO) {
      return compatibilidadMinimo(ejemplo);
    }
    else {
      return compatibilidadProducto(ejemplo);
    }
  }

  private double compatibilidadMinimo(double[] ejemplo) {
    double minimo, grado_pertenencia;
    int etiqueta, k, p, et;
    minimo = 1.0;
    for (int i = 0; i < antecedente.length; i++) {
      etiqueta = antecedente[i];
      boolean salir = false;
      for (k = 0, p = 1, et = 0; !salir; i++) {
        if (etiqueta <= p) {
          etiqueta -= et;
          salir = true;
        }
        et += 2 + i;
        p += 3 + i;
      }
      grado_pertenencia = baseDatos.pertenencia(k, i, etiqueta, ejemplo[i]);

      minimo = Math.min(grado_pertenencia, minimo);
    }
    return (minimo);

  }

  private double compatibilidadProducto(double[] ejemplo) {
    double producto, grado_pertenencia;
    int etiqueta, k, p, et;
    producto = 1.0;
    for (int i = 0; i < antecedente.length; i++) {
      etiqueta = antecedente[i];
      boolean salir = false;
      for (k = 0, p = 1, et = 0; !salir; k++) {
        //System.err.println("k["+k+"], p["+p+"], et["+et+"]");
        if (etiqueta <= p) {
          etiqueta -= et;
          salir = true;
        }
        et += 2 + k;
        p += 3 + k;
      }
      k--; //porque al salir del bucle se incrementa
      //System.err.println("Obtengo-> [k][i][etiqueta]::["+k+"]["+i+"]["+etiqueta+"]");
      grado_pertenencia = baseDatos.pertenencia(k, i, etiqueta, ejemplo[i]);

      producto = producto * grado_pertenencia;
    }
    return (producto);
  }

  private void consecuente_CF(myDataset train) {
    double[] sumaclases = new double[train.getnClasses()];
    for (int i = 0; i < train.getnClasses(); i++) {
      sumaclases[i] = 0.0;
    }

    double total = 0.0;
    double comp;
    /* Se calcula la suma por clases */
    for (int i = 0; i < train.size(); i++) {
      comp = this.compatibilidad(train.getExample(i));
      sumaclases[train.getOutputAsInteger(i)] += comp;
      total += comp;
    }
    peso = sumaclases[clase] / total;
  }

  private void consecuente_PCF2(myDataset train) {
    double[] sumaclases = new double[train.getnClasses()];
    for (int i = 0; i < train.getnClasses(); i++) {
      sumaclases[i] = 0.0;
    }

    double total = 0.0;
    double comp;
    /* Se calcula la suma por clases */
    for (int i = 0; i < train.size(); i++) {
      comp = this.compatibilidad(train.getExample(i));
      sumaclases[train.getOutputAsInteger(i)] += comp;
      total += comp;
    }
    double suma = (total - sumaclases[clase]) / (train.getnClasses() - 1.0);
    peso = (sumaclases[clase] - suma) / total;
  }

  private void consecuente_PCF4(myDataset train) {
    double[] sumaclases = new double[train.getnClasses()];
    for (int i = 0; i < train.getnClasses(); i++) {
      sumaclases[i] = 0.0;
    }

    double total = 0.0;
    double comp;
    /* Se calcula la suma por clases */
    for (int i = 0; i < train.size(); i++) {
      comp = this.compatibilidad(train.getExample(i));
      sumaclases[train.getOutputAsInteger(i)] += comp;
      total += comp;
    }
    double suma = total - sumaclases[clase];
    if (total == 0) {
      peso = -1;
    }
    else {
      peso = (sumaclases[clase] - suma) / total;
    }
  }

  public Regla clone() {
    Regla r = new Regla(baseDatos, tipoCompatibilidad);
    r.antecedente = new int[antecedente.length];
    for (int i = 0; i < this.antecedente.length; i++) {
      r.antecedente[i] = this.antecedente[i];
    }
    r.clase = clase;
    r.peso = peso;
    r.p_DC = p_DC;
    r.n_e = n_e;
    return r;
  }

  public void mutar(myDataset train, double prob_mut) {
    for (int k = 0; k < antecedente.length; k++) {
      if (prob_mut > Randomize.Rand()) {
        antecedente[k] = Randomize.RandintClosed(0, 15);
      }
    }
    calcula_consecuente(train);
  }

  public int compareTo(Object a) {
    if ( ( (Regla) a).cubiertos < this.cubiertos) {
      return -1;
    }
    if ( ( (Regla) a).cubiertos > this.cubiertos) {
      return 1;
    }
    return 0;
  }

}

