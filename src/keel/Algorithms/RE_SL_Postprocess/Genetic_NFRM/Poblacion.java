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

package keel.Algorithms.RE_SL_Postprocess.Genetic_NFRM;

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
    MatrizR m = new MatrizR(baseReglas);
    double [] ajuste = new double[2*n_etiquetas*n_variables];
    soporte = new double[n_variables];
    for (int i = 0; i < soporte.length; i++){
      soporte[i] = baseReglas.baseDatos.baseDatos[i][0].x3 - baseReglas.baseDatos.baseDatos[i][0].x0;
    }
    for (int j = 0; j < ajuste.length; j++){
      ajuste[j] = 0.0;
    }
    Individuo ind = new Individuo(m, ajuste, soporte);
    cromosomas.add(ind);
    for (int i = 1; i < tamPoblacion; i++) {
      MatrizR m2 = new MatrizR(baseReglas.size(), baseReglas.numEtiquetas());
      int h = 0;
      for (int j = 0; j < n_variables; j++){
        double sop = soporte[j];
        sop *= 3;
        sop /= 8;
        for (int k = 0; k < n_etiquetas; k++){
          ajuste[h] = Randomize.RanddoubleClosed(-1.0*sop, sop);
          ajuste[h+1] = Randomize.RanddoubleClosed(-1.0*sop, sop);
          h+=2;
        }
      }
      Individuo ind2 = new Individuo(m2, ajuste,soporte);
      cromosomas.add(ind2);
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
      baseReglas.baseDatos.ajusta(cromosoma.cromosoma2);
      ecm = 0;
      for (int j = 0; j < train.size(); j++) {
        double salida = baseReglas.FRM(train.getExample(j), cromosoma.cromosoma1);//, cromosoma);
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
    System.err.println("Initial ECM: " + mejor_ECM);
    for (int i = 1; i < cromosomas.size(); i++) {
      cromosoma = cromosomas.get(i);
      if (cromosoma.n_e) {
        baseReglas.baseDatos.ajusta(cromosoma.cromosoma2);
        ecm = 0;
        for (int j = 0; j < train.size(); j++) {
          double salida = baseReglas.FRM(train.getExample(j), cromosoma.cromosoma1); //, cromosoma);
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
      //cromosoma.print();
      if (cromosoma.n_e) {
        ecm = 0;
        baseReglas.baseDatos.ajusta(cromosoma.cromosoma2);
        for (int j = 0; j < train.size(); j++) {
          double salida = baseReglas.FRM(train.getExample(j), cromosoma.cromosoma1);
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
      aleatorio1 = Randomize.RandintClosed(0, cromosomas.size()); //Elijo uno aleatoriamente
      do {
        aleatorio2 = Randomize.RandintClosed(0, cromosomas.size()); //Elijo otro aleatoriamente
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
        int puntoCorte = Randomize.Randint(1, padre.cromosoma1.size() - 1);
        MatrizR m1 = new MatrizR(padre.cromosoma1, madre.cromosoma1, puntoCorte);
        MatrizR m2 = new MatrizR(madre.cromosoma1, padre.cromosoma1, puntoCorte);
        double[] ajuste1 = new double[padre.cromosoma2.length];
        double[] ajuste2 = new double[padre.cromosoma2.length];
        puntoCorte = Randomize.Randint(1, padre.cromosoma2.length - 1);
        for (int j = 0; j < puntoCorte; j++) {
          ajuste1[j] = padre.cromosoma2[j];
          ajuste2[j] = madre.cromosoma2[j];
        }
        for (int j = puntoCorte; j < ajuste1.length; j++) {
          ajuste1[j] = madre.cromosoma2[j];
          ajuste2[j] = padre.cromosoma2[j];
        }
        //revisar extremos!!

        hijo1 = new Individuo(m1, ajuste1, soporte);
        hijo2 = new Individuo(m2, ajuste2, soporte);
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
    int posicion = Randomize.RandintClosed(0, hijos.size());
    hijos.remove(posicion);
    for (int i = 0; i < hijos.size(); i++) {
      Individuo nuevo = hijos.get(i).clone();
      cromosomas.add(nuevo);
    }
  }

}

