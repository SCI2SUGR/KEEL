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

package keel.Algorithms.Decision_Trees.Target;

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

import java.util.ArrayList;
import java.util.Collections;

import org.core.*;

public class Poblacion {

  ArrayList<Tree> bosque, hijos;
  double pSplit;
  int nClasses, nGenerations;
  int nCross, nMut, nClone, nImmigration, nTrees;
  myDataset train;
  double mejor_fitness, prob1_var, prob2_var;

  /**
   * Minimizacion
   * @param a double primer valor
   * @param b double segundo valor
   * @return boolean true si el primer valor es menor que el segundo
   */
  public boolean BETTER(double a, double b) {
    return (a < b);
  }

  public Poblacion() {
    bosque = new ArrayList<Tree> ();
    hijos = new ArrayList<Tree> ();
  }

  public Poblacion(myDataset train, double pSplit, int nGenerations, int nCross,
                   int nMut, int nClone, int nImmigration) {
    this.train = train;
    this.pSplit = pSplit;
    this.nGenerations = nGenerations;
    this.nCross = nCross;
    this.nMut = nMut;
    this.nClone = nClone;
    this.nImmigration = nImmigration;
    nTrees = nCross + nMut + nClone + nImmigration;
    bosque = new ArrayList<Tree> ();
    hijos = new ArrayList<Tree> ();
    mejor_fitness = Double.MAX_VALUE;
  }

  public void hacerGenetico() {
    init();
    evaluate(bosque, 0);
    //System.exit(0);
    for (int i = 1; i <= nGenerations; i++) {
      //System.err.println("Generation[" + i + "]");
      evolution();
      evaluate(hijos, i);
      replace();
    }
  }

  private void init() {
    prob1_var = prob2_var = 0.5;
    if (train.numCuantitativos() == 1){
      prob1_var = 1.0;
    }else if (train.numCuantitativos() == 2){
      prob2_var = 1.0;
    }
    for (int i = 0; i < nTrees; i++) {
      Tree t = new Tree(null, train, pSplit, true,prob1_var, prob2_var);
      bosque.add(t);
      //System.err.println("Arbol["+i+"]: "+t.printString());
    }
    //System.exit(0);
  }

  private void evaluate(ArrayList<Tree> individuos, int generation) {
    boolean entrar = false;
    for (int i = 0; i < individuos.size(); i++) {
      if (individuos.get(i).n_e) {
        double fitness = individuos.get(i).evaluar();
        /*if (generation == 0){
          System.err.println("Generacion[" + generation + "], Individuo(" + i +
                             ") -> " + fitness);
        }*/
        if (BETTER(fitness, mejor_fitness)) {
          mejor_fitness = fitness;
          entrar = true;
        }
      }
    }
    if (entrar) {
      System.out.println("Best Fitness obtained in generation[" + generation +
                         "]: " + mejor_fitness);
      //System.out.println(individuos.get(mejor).printString());

    }
  }

  private void evolution() {
    //System.err.println("cruce");
    cross();
    //System.err.println("mutacion");
    mutate();
    //System.err.println("clonacion");
    clonation();
    //System.err.println("inmigracion");
    immigration();
  }

  private int seleccion(int posicionNo) {
    int i = 0;
    int posicion = 0;
    double rank_min = 0.75;
    double rank_max = 2.0 - rank_min;
    int tamPoblacion = bosque.size();
    double[] Ruleta = new double[tamPoblacion];

    /* Ordenamos la poblacion de mayor a menor fitness */
    Collections.sort(bosque);

    /* Calculamos la probabilidad de seleccion de cada individuo mediante
     el ranking lineal en funcion de su posicion en el orden y construimos
       la ruleta */
    for (i = 0; i < tamPoblacion; i++) {
      if (i != 0) {
        Ruleta[i] = Ruleta[i - 1] +
            (rank_max -
             (rank_max - rank_min) * i /
             (double) (tamPoblacion - 1)) /
            (double) tamPoblacion;
      }
      else {
        Ruleta[i] = (rank_max -
                     (rank_max - rank_min) * i /
                     (double) (tamPoblacion - 1)) /
            (float) tamPoblacion;
      }
    }

    boolean salir = false;

    while (!salir) {
      double u = Randomize.Rand();
      posicion = 0;
      for (i = 0; i < tamPoblacion; i++) {
        for (; Ruleta[posicion] < u; posicion++) {
          ;
        }
      }
      if (posicion != posicionNo) {
        salir = true;
      }
    }

    return posicion;
  }

  /**
   * Para todos los posibles arboles a cruzar:
   * - Selecciono dos padres (la probabilidad de seleccion depende del fitness)
   * - Aplico el cruce (uno de los dos posibles)
   */
  private void cross() {
    for (int i = 0; i < this.nCross; i++) {
      //Selecciono dos padres
      int uno = seleccion( -1);
      int dos = seleccion(uno);
      //System.out.println("Applying Crossover");
      cruce(uno, dos);
    }
  }

  /**
   * Dos tipos:
   * - Node Swapping
   * - Subtree Swapping
   * @param posPadre int posicion del padre en la poblacion
   * @param posMadre int posicion de la madre en la poblacion
   */
  private void cruce(int posPadre, int posMadre) {
    Tree padre = bosque.get(posPadre);
    Tree madre = bosque.get(posMadre);
    Tree hijo1, hijo2;
    hijo1 = padre.copia(null);
    hijo2 = madre.copia(null);
    hijo1.n_e = true;
    hijo2.n_e = true;
    int nodo1, nodo2;
    nodo1 = padre.elijeNodo();
    nodo2 = madre.elijeNodo();
    if (Randomize.Rand() < 0.5) { //node swapping
      hijo1.nodeSwap(nodo1, nodo2, madre);
      hijo2.nodeSwap(nodo2, nodo1, padre);
    }
    else { //tree swapping
      hijo1.treeSwap(nodo1, nodo2, madre);
      hijo2.treeSwap(nodo2, nodo1, padre);
    }
    //Ahora elijo el mejor entre los dos padres y los dos hijos y lo meto en "hijos"
    int elegidoH, elegidoP;
    double fitnessP, fitnessH;
    double fitness1 = hijo1.evaluar();
    double fitness2 = hijo2.evaluar();
    if (BETTER(fitness1, fitness2)) {
      fitnessH = fitness1;
      elegidoH = 0;
    }
    else {
      fitnessH = fitness2;
      elegidoH = 1;
    }
    fitness1 = padre.fitness;
    fitness2 = madre.fitness;
    if (BETTER(fitness1, fitness2)) {
      fitnessP = fitness1;
      elegidoP = 0;
    }
    else {
      fitnessP = fitness2;
      elegidoP = 1;
    }
    if (BETTER(fitnessP, fitnessH)) {
      if (elegidoP == 0) { //padre
        hijos.add(padre);
      }
      else { //madre
        hijos.add(madre);
      }
    }
    else {
      if (elegidoH == 0) { //hijo1
        hijos.add(hijo1);
      }
      else { //hijo2
        hijos.add(hijo2);
      }
    }

  }

  private void mutate() {
    for (int i = 0; i < this.nMut; i++) {
      //Selecciono un arbol
      int tipo = Randomize.RandintClosed(0, 3);
      Tree hijo = bosque.get(seleccion( -1)).copia(null);
      hijo.n_e = true;
      if (tipo == 0) { //Split set mutation
        int nodo1;
        nodo1 = hijo.elijeNodo();
        hijo.splitSet(nodo1);
      }
      else if (tipo == 1) { //Split rule mutation
        int nodo1;
        nodo1 = hijo.elijeNodo();
        hijo.splitRule(nodo1);
      }
      else if (tipo == 2) { //Node swap mutation
        int nodo1, nodo2;
        nodo1 = hijo.elijeNodo();
        nodo2 = hijo.elijeNodo();
        hijo.nodeSwap(nodo1, nodo2, hijo);
      }
      else { //Subtree swap mutation
        int nodo1, nodo2;
        nodo1 = hijo.elijeNodo();
        nodo2 = hijo.elijeNodo();
        hijo.treeSwap(nodo1, nodo2, hijo);
      }
      hijos.add(hijo);
    }
  }

  private void clonation() {
    /*for (int i = 0; i < this.nClone; i++) {
      Tree hijo = bosque.get(seleccion( -1)).copia(null);
      hijos.add(hijo);
         }*/
    Collections.sort(bosque);
    for (int i = 0; i < this.nClone; i++) {
      Tree hijo = bosque.get(i).copia(null);
      hijos.add(hijo);
    }
  }

  private void immigration() {
    for (int i = 0; i < this.nImmigration; i++) {
      Tree hijo = new Tree(null, train, pSplit, true, prob1_var, prob2_var);
      hijos.add(hijo);
    }
  }

  private void replace() {
    bosque.clear();
    bosque.addAll(hijos);
    //
  }

  public Tree mejorSolucion() {
    Collections.sort(bosque);
    return bosque.get(0).copia(null);
  }

}

