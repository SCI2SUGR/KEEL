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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.GFS_RB_MF;

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

public class Poblacion {

  ArrayList<Individuo> cromosomas;
  ArrayList<Individuo> hijos;
  BaseR baseReglas;
  myDataset train;
  double mejor_ECM, sumatoria, crossProb, mutProb;
  int[] selectos;
  double [] soporte;

  public boolean BETTER(double a, double b) {
    if (a > b) {
      return true;
    }
    return false;
  }

  public Poblacion(int tamPoblacion, BaseR baseReglas, myDataset train) {
    cromosomas = new ArrayList<Individuo> ();
    hijos = new ArrayList<Individuo> ();
    int n_etiquetas = baseReglas.baseDatos.n_etiquetas;
    int n_variables = baseReglas.baseDatos.n_variables;
    this.baseReglas = baseReglas;
    this.train = train;

    int n_reglas = 1;
    for (int i = 0; i < n_variables-1; i++){ //menos la salida
      n_reglas *= n_etiquetas;
    }
    Individuo ind = new Individuo(n_variables,n_etiquetas,n_reglas,baseReglas.baseDatos);
    double [] max = ind.dameIntervalosMax();
    double [] min = ind.dameIntervalosMin();
    for (int i = 1; i < tamPoblacion; i++) {
      ind = new Individuo(n_variables,n_etiquetas,n_reglas,min,max);
      cromosomas.add(ind);
    }
    mejor_ECM = Double.MAX_VALUE;
    sumatoria = train.sumatoria();
    selectos = new int[cromosomas.size()];
  }

  public Individuo getMejor() {
    Collections.sort(cromosomas);
    return cromosomas.get(0);
  }

  public void procesoGenetico(int nGeneraciones, double crossProb,
                              double mutProb) {
    this.crossProb = crossProb;
    this.mutProb = mutProb;
    evaluaP();
    for (int i = 0; i < nGeneraciones; i++) {
      select();
      cross();
      mutate();
      evalua(i);
      replace();
    }
  }

  private void evaluaP() {
    double ecm = 0;
    Individuo cromosoma = cromosomas.get(0);
    if (cromosoma.n_e) {
      ecm = 0;
      baseReglas.ajusta(cromosoma);
      for (int j = 0; j < train.size(); j++) {
        double salida = baseReglas.FLC(train.getExample(j));
        ecm += (salida - train.getOutputAsReal(j)) *
            (salida - train.getOutputAsReal(j));
      }
      ecm /= sumatoria;
      cromosoma.setFitness(ecm);
      if (ecm < mejor_ECM) {
        mejor_ECM = ecm;
      }
      cromosoma.n_e = false;
    }
    //System.err.println("Initial ECM: " + mejor_ECM);
    for (int i = 1; i < cromosomas.size(); i++) {
      cromosoma = cromosomas.get(i);
      baseReglas.ajusta(cromosoma);
      if (cromosoma.n_e) {
        ecm = 0;
        for (int j = 0; j < train.size(); j++) {
          double salida = baseReglas.FLC(train.getExample(j));
          ecm += (salida - train.getOutputAsReal(j)) *
              (salida - train.getOutputAsReal(j));
          cromosoma.setFitness(salida);
        }
        ecm /= sumatoria;
        if (ecm < mejor_ECM) {
          mejor_ECM = ecm;
        }
        cromosoma.n_e = false;
      }
    }
    System.err.println("Best ECM in the initial Population: " + mejor_ECM);
  }

  private void evalua(int generation) {
    double ecm = 0;
    for (int i = 0; i < hijos.size(); i++) {
      Individuo cromosoma = hijos.get(i);
      baseReglas.ajusta(cromosoma);
      if (cromosoma.n_e) {
        ecm = 0;
        for (int j = 0; j < train.size(); j++) {
          double salida = baseReglas.FLC(train.getExample(j));
          ecm += (salida - train.getOutputAsReal(j)) *
              (salida - train.getOutputAsReal(j));
        }
        ecm /= sumatoria;
        cromosoma.setFitness(ecm);
        if (ecm < mejor_ECM) {
          mejor_ECM = ecm;
        }
        cromosoma.n_e = false;
      }
    }
    System.err.println("Generation[" + generation + "], ECM: " + mejor_ECM);
  }

  /**
     @brief Torneo binario entre cromosomas
     @param indice Indice del hijo
     @param cromosoma1 Es el indice del individuo1 dentro de la poblacion
     @param cromosoma2 Es el indice del individuo2 dentro de la poblacion

     Comparo las fitness de los dos individuos, el que tenga mayor valor pasa
     a formar parte del nuevo conjunto de seleccion.
   */
  void torneo(int indice, int cromosoma1, int cromosoma2) {
    if (BETTER(cromosomas.get(cromosoma1).fitness,
               cromosomas.get(cromosoma2).fitness)) {
      //hijos.add(poblacion.get(cromosoma1).clone());
      selectos[indice] = cromosoma1;
    }
    else {
      //hijos.add(poblacion.get(cromosoma2).clone());
      selectos[indice] = cromosoma2;
    }
    ;
  }

  private void select() {
    hijos.clear();
    int aleatorio1, aleatorio2, i, inicio;
    inicio = 0;
    for (i = inicio; i < cromosomas.size(); i++) { //Generamos la otra mitad por los operadores geneticos, Cojo numIndividuos nuevos individuos...
      aleatorio1 = Randomize.RandintClosed(0, cromosomas.size()-1); //Elijo uno aleatoriamente
      do {
        aleatorio2 = Randomize.RandintClosed(0, cromosomas.size()-1); //Elijo otro aleatoriamente
      }
      while (aleatorio1 == aleatorio2); //pero que no coincida con el anterior
      torneo(i, aleatorio1, aleatorio2); //Inserto en la posicion 'i' el mejor de los 2
    }

  }

  private void cross() {
    for (int i = 0; i < cromosomas.size() / 2; i++) {
      Individuo padre = cromosomas.get(selectos[i]);
      Individuo madre = cromosomas.get(selectos[i + 1]);
      Individuo hijo1, hijo2;
      if (Randomize.Rand() < crossProb) {
        int puntoCorte = Randomize.Randint(1, padre.cromosoma1.length-1);
        hijo1 = new Individuo(padre,madre,puntoCorte);
        hijo2 = new Individuo(madre,padre,puntoCorte);
      }
      else {
        hijo1 = padre.clone();
        hijo2 = madre.clone();
      }
      hijos.add(hijo1);
      hijos.add(hijo2);
    }
  }

  private void mutate() {
    for (int i = 0; i < hijos.size(); i++) {
      hijos.get(i).mutar(mutProb);
    }
  }

  private void replace() {
    Collections.sort(cromosomas);
    Individuo mejor = cromosomas.get(0).clone();
    cromosomas.clear();
    cromosomas.add(mejor);
    int posicion = Randomize.RandintClosed(0, hijos.size()-1);
    hijos.remove(posicion);
    for (int i = 0; i < hijos.size(); i++) {
      Individuo nuevo = hijos.get(i).clone();
      cromosomas.add(nuevo);
    }
  }

}
