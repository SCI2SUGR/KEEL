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

package keel.Algorithms.Decision_Trees.DT_oblicuo;

import java.util.*;
import org.core.Randomize;


/**
 * 
 *
 * @author Anonymous - 2011
 * @version 1.0
 * @since JDK1.6
 */
public class Poblacion {

  ArrayList<Individuo> cromosomas;
  ArrayList<Individuo> hijos;
  int nGenerations, n_clases, selectos[], clases[];
  myDataset train;
  double mejor_fitness, ejemplos[][];

  public boolean BETTER(double a, double b) {
    if (a > b) {
      return true;
    }
    return false;
  }

  public Poblacion() {
    cromosomas = new ArrayList<Individuo> ();
    hijos = new ArrayList<Individuo> ();
  }

  public Poblacion(myDataset train, int n_ejemplos, int ejemplos[],
                   int nGenerations, double[] pesos, double valor) {
    this.train = train;
    //this.ejemplos = ejemplos.clone();
    //this.n_ejemplos = n_ejemplos;
    this.ejemplos = new double[n_ejemplos][train.getnInputs()];
    n_clases = train.getnClasses();
    this.clases = new int[n_ejemplos];
    for (int i = 0; i < n_ejemplos; i++) {
      this.ejemplos[i] = train.getExample(ejemplos[i]).clone();
      this.clases[i] = train.getOutputAsInteger(ejemplos[i]);
    }

    int tamPoblacion = (int) Math.round(20.0 * Math.sqrt(train.getnInputs())); //heuristica
    while ( (tamPoblacion % 2) != 0) {
      tamPoblacion++;
    }
    cromosomas = new ArrayList<Individuo> ();
    hijos = new ArrayList<Individuo> ();
    int porcentaje = (int) Math.round(tamPoblacion * 0.1); //heuristica para copiar
    for (int i = 0; i < porcentaje; i++) {
      Individuo ind = new Individuo(pesos, valor);
      cromosomas.add(ind);
    }
    //copiar el axis paralel al 10% de la poblacion inicial
    for (int i = porcentaje; i < tamPoblacion; i++) {
      Individuo ind = new Individuo(pesos.length + 1);
      cromosomas.add(ind);
    }
    selectos = new int[tamPoblacion];
    this.nGenerations = nGenerations;
    mejor_fitness = 0.0;
  }

  public void genetico() {
    evaluate(cromosomas, 0); //evaluacion
    for (int i = 0; i < nGenerations; i++) { //generaciones
      selection(); //seleccion
      crossover(); //cruce
      evaluate(hijos, i); //evaluacion
      elitist();
    }
    //Collections.sort(cromosomas);
  }

  private void evaluate(ArrayList<Individuo> individuos, int generation) {
    boolean entrar = false;
    for (int i = 0; i < individuos.size(); i++) {
      double fitness = individuos.get(i).impureza(ejemplos, clases, n_clases);
      //System.err.println("Generacion["+generation+"], Individuo("+i+") -> "+fitness);
      if (fitness > mejor_fitness) {
        mejor_fitness = fitness;
        entrar = true;
      }
    }
    /*if (entrar) {
      System.out.println("Best Fitness obtained in generation[" + generation +
                         "]: " + mejor_fitness);
    }*/
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
      selectos[indice] = cromosoma1;
    }
    else {
      selectos[indice] = cromosoma2;
    }
    ;
  }

  private void selection() {
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

  private void crossoverPoint() {
    for (int i = 0; i < selectos.length / 2; i++) {
      Individuo padre = cromosomas.get(selectos[i]);
      Individuo madre = cromosomas.get(selectos[i + 1]);
      int puntoCorte = Randomize.RandintClosed(1, padre.size() - 2);
      Individuo hijo1 = new Individuo(padre, madre, puntoCorte);
      Individuo hijo2 = new Individuo(madre, padre, puntoCorte);
      hijos.add(hijo1);
      hijos.add(hijo2);
    }
  }

  private void crossover() {
    boolean [] mascara = new boolean[train.getnInputs()+1];
    for (int i = 0; i < selectos.length / 2; i++) {
      for (int j = 0; j < mascara.length; j++){
        mascara[j] = (Randomize.Rand() > 0.5);
      }
      Individuo padre = cromosomas.get(selectos[i]);
      Individuo madre = cromosomas.get(selectos[i + 1]);
      Individuo hijo1 = new Individuo(padre, madre, mascara);
      Individuo hijo2 = new Individuo(madre, padre, mascara);
      hijos.add(hijo1);
      hijos.add(hijo2);
    }
  }


  private void elitist() {
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

  public double[] mejorSolucion() {
    Collections.sort(cromosomas);
    return cromosomas.get(0).cromosoma.clone();
  }

}

