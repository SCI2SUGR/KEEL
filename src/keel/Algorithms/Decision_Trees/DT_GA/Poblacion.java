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

package keel.Algorithms.Decision_Trees.DT_GA;

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

  ArrayList<Individuo> cromosomas;
  ArrayList<Individuo> hijos;
  ArrayList<Regla> reglas;
  myDataset train;
  int nGenerations, popSize, nEjemplos;
  double crossProb, mutProb, mejor_fitness;
  int ejemplos[], selectos[];
  double infoG;
  double [] norm_acc;

  public boolean BETTER(double a, double b) {
    if (a > b) {
      return true;
    }
    return false;
  }

  public Poblacion() {
    cromosomas = new ArrayList<Individuo> ();
    mejor_fitness = 0;
  }

  public Poblacion(int codigoRegla, Regla r, int nGenerations, int popSize,
                   double crossProb,
                   double mutProb, myDataset train, String clase) {
    cromosomas = new ArrayList<Individuo> ();
    hijos = new ArrayList<Individuo> ();
    reglas = new ArrayList<Regla> ();
    this.train = train;
    boolean[] atributos = new boolean[train.getnInputs()];
    for (int i = 0; i < atributos.length; i++) {
      atributos[i] = true;
    }
    for (int i = 0; i < r.size(); i++) {
      atributos[r.antecedente.get(i).getAtributo()] = false;
    }
    this.nGenerations = nGenerations;
    this.popSize = popSize;
    this.crossProb = crossProb;
    this.mutProb = mutProb;
    this.nEjemplos = r.cubiertos();
    this.ejemplos = r.ejemplosCubiertos.clone(); //el conjunto de train corresponde solo a los cubiertos
    mejor_fitness = 0;
    this.selectos = new int[popSize];
    inicializaPoblacion(atributos, clase, codigoRegla);
    calculaInfoG();
  }

  public Poblacion(boolean[] ejemplosTr, int nGenerations, int popSize,
                   double crossProb, double mutProb, myDataset train, double [] norm_acc) {
    cromosomas = new ArrayList<Individuo> ();
    hijos = new ArrayList<Individuo> ();
    reglas = new ArrayList<Regla> ();
    this.train = train;
    //Tengo que formar los ejemplos de entrenamiento
    escogerEjemplos(ejemplosTr);
    this.nGenerations = nGenerations;
    this.popSize = popSize;
    this.crossProb = crossProb;
    this.mutProb = mutProb;
    mejor_fitness = 0;
    this.selectos = new int[popSize];
    this.norm_acc = norm_acc.clone();
    //inicializaPoblacion();
    //calculaInfoG();

  }

  /**
   * Inicializa la poblacion para el caso de GA_Small
   * @param atributos boolean[] los atributos que se pueden (true) y no se pueden (false) colocar en el cromosoma
   * @param clase String es la clase consecuente para todos los cromosomas
   * @param codigoRegla int es el codigo de la regla de la que se generan los individuos
   */
  private void inicializaPoblacion(boolean[] atributos, String clase,
                                   int codigoRegla) {
    for (int i = 0; i < popSize; i++) {
      //se generan aleatorios, en funcion del tipo de cada atributo
      Individuo ind = new Individuo(atributos, clase, train, codigoRegla);
      cromosomas.add(ind);
    }
    //System.err.println("Poblacion: \n" + this.printString());
  }

  /**
   * Inicializa la poblacion para el caso de GA_Large
   */
  private void inicializaPoblacion() {
    for (int i = 0; i < popSize; i++) {
      //se generan aleatorios, en funcion del tipo de cada atributo
      int semilla = ejemplos[Randomize.RandintClosed(0,nEjemplos-1)];
      Individuo ind = new Individuo(train, semilla);
      cromosomas.add(ind);
    }
    //System.err.println("Poblacion: \n" + this.printString());
  }


  public String printString() {
    String cadena = new String("");
    for (int i = 0; i < popSize; i++) {
      cadena += "Chromosome[" + (i + 1) + "]: " + cromosomas.get(i).printString();
    }
    return cadena;
  }

  public void GA_Small() {
    clasifica(cromosomas, 0);
    for (int i = 0; i < nGenerations; i++) {
      selection();
      crossover();
      mutation();
      rule_pruning();
      clasifica(hijos, i); //calcula la BR de cada cromosoma y obtiene su fitness
      elitist();
    }
    Collections.sort(cromosomas);
    reglas.add(cromosomas.get(0).convertir());
  }

  private void clasifica(ArrayList<Individuo> individuos, int generation) {
    boolean entrar = false;
    for (int i = 0; i < individuos.size(); i++) {
      if (individuos.get(i).n_e) {
        double fitness = individuos.get(i).clasifica(ejemplos, nEjemplos);
        //System.err.println("Generacion["+generation+"], Individuo("+i+") -> "+fitness);
        if (fitness > mejor_fitness) {
          mejor_fitness = fitness;
          entrar = true;
        }
      }
    }
    if (entrar) {
      System.out.println("Best Fitness obtained in generation[" + generation +
                         "]: " + mejor_fitness);
    }
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

  /**
   * Standard one-point crossover
   */
  private void crossover() {
    for (int i = 0; i < selectos.length / 2; i++) {
      Individuo padre = cromosomas.get(selectos[i]);
      Individuo madre = cromosomas.get(selectos[i + 1]);
      if (Randomize.Rand() < crossProb) {
        int puntoCorte = Randomize.RandintClosed(1, padre.size() - 2);
        Individuo hijo1 = new Individuo(padre, madre, puntoCorte);
        Individuo hijo2 = new Individuo(madre, padre, puntoCorte);
        hijos.add(hijo1);
        hijos.add(hijo2);
      }
      else {
        hijos.add(padre.clone());
        hijos.add(madre.clone());
      }
    }
  }

  /**
   * Random mutation of each chromosome
   */
  private void mutation() {
    for (int i = 0; i < hijos.size(); i++) {
      if (hijos.get(i).n_e) { //es un hijo generado nuevo
        hijos.get(i).mutar(mutProb);
      }
    }
  }

  /**
   * Obtiene el numero de ejemplos para la clase i-esima
   * @param clase int codigo de clase
   * @return int numero de ejemplos pertenencientes a dicha clase
   */
  public int numEjemplos(int clase){
    int n_ejemplos = 0;
    for (int i = 0; i < nEjemplos; i++){
      if (clase == train.getOutputAsInteger(ejemplos[i]))
        n_ejemplos++;
    }
    return n_ejemplos;
  }


  /**
   * Info(G) = - sum_{j=1}^c {|Gj|/|T|*log_2{|Gj|/|T|}}
   * c = numero de clases
   * |Gj| = numero de ejemplos de la clase G
   * |T| = numero total de ejemplos
   */
  private void calculaInfoG(){
    int c = train.getnClasses();
    double sum = 0.0;
    for (int i = 0; i < c; i++){
      double aux = 1.0*this.numEjemplos(i)/nEjemplos;
      sum += (aux)*(Math.log(aux)/Math.log(2));
    }
    infoG = -sum;
  }

  private void rule_pruning(){
    for (int i = 0; i < hijos.size(); i++){
      hijos.get(i).pruning(infoG,nEjemplos,ejemplos);
    }
  }

  public void escogerEjemplos(boolean [] ejemplos){
    nEjemplos = 0;
    this.ejemplos = new int [ejemplos.length];
    for (int i = 0; i < ejemplos.length; i++){
      if (ejemplos[i]){
        this.ejemplos[nEjemplos] = i;
        nEjemplos++;
      }
    }
  }

  public void GA_Large() {
    System.out.println("#Examples Remaining in Training-Set-2 -> "+nEjemplos);
    while (nEjemplos > 5){
      mejor_fitness = 0;
      cromosomas.clear();
      hijos.clear();
      inicializaPoblacion();
      clasificaLarge(cromosomas, 0);
      for (int i = 0; i < nGenerations; i++) {
        selection();
        crossover();
        mutation();
        rule_pruning_large();
        clasificaLarge(hijos, i); //calcula la BR de cada cromosoma y obtiene su fitness
        elitist();
      }
      Collections.sort(cromosomas);
      reglas.add(cromosomas.get(0).convertir());
      niching(); //eliminar del conjunto de entrenamiento los ejemplos cubiertos
      //System.out.println("#Examples Remaining in Training-Set-2 -> "+nEjemplos);
    }
  }

  private void clasificaLarge(ArrayList<Individuo> individuos, int generation) {
    boolean entrar = false;
    for (int i = 0; i < individuos.size(); i++) {
      if (individuos.get(i).n_e) {
        double fitness = individuos.get(i).clasificaLarge(ejemplos, nEjemplos); //elijo dinamicamente la clase
        //System.err.println("Generacion["+generation+"], Individuo("+i+") -> "+fitness);
        if (fitness > mejor_fitness) {
          mejor_fitness = fitness;
          entrar = true;
        }
      }
    }
    /*if (entrar) {
      System.out.println("Best Fitness obtained in generation[" + generation +
                         "]: " + mejor_fitness);
    }*/
  }


  private void rule_pruning_large(){
    for (int i = 0; i < hijos.size(); i++){
      hijos.get(i).pruning(norm_acc);
    }
  }

  private void niching(){
    Regla r = reglas.get(reglas.size()-1); //cojo la ultima generada
    //System.err.print("Regla -> "+r.printString());
    r.cubrirEjemplos();
    int [] ejsCubiertosOK = new int[train.size()];
    ejsCubiertosOK = r.ejemplosBienCubiertos.clone();
    int ejemplosOK = r.cubiertosOK();
    for (int i = 0; i < nEjemplos; i++){
      boolean salir = false;
      for (int j = 0; j < ejemplosOK && (!salir); j++){
        salir = (ejsCubiertosOK[j] == ejemplos[i]);
      }
      if (salir){
        ejemplos[i] = ejemplos[nEjemplos-1]; //sustituyo por el último
        nEjemplos--; //me cargo uno
        i--; //para que no me afecte al contador
      }
    }
  }

  public ArrayList<Regla> dameReglas() {
    return reglas;
  }

}

