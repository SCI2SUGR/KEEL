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



import java.util.ArrayList;
import java.util.Collections;
import org.core.*;

/**
 * <p>Title: Poblacion (Population). </p>
 *
 * <p>Description: This class implements the population of chromosomes used to perform the genetic algorithm</p>
 *
 * @author not attributable
 * @version 1.0
 */
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

    /**
     * Checks if the double a is greater than b.
     * @param a first given number.
     * @param b second given number.
     * @return True if the double a is greater than b. 
     */
    public boolean BETTER(double a, double b) {
    if (a > b) {
      return true;
    }
    return false;
  }

    /**
     * Default Constructor. Basic structures will be initialized.
     */
    public Poblacion() {
    cromosomas = new ArrayList<Individuo> ();
    mejor_fitness = 0;
  }

        /**
     * Paramenter constructor. The population structures will be initialized with the parameters given.
     * @param codigoRegla rule's code.
     * @param r rule used to initialize the different chromosomes.
     * @param nGenerations maximum number for generations.
     * @param popSize population size.
     * @param crossProb crossover probability.
     * @param mutProb mutation probability.
     * @param clase class for the population.
     * @param train training dataset. 
     */
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

     /**
     * Paramenter constructor. The population structures will be initialized with the parameters given.
     * @param ejemplosTr examples considered to be used as training.
     * @param nGenerations maximum number for generations.
     * @param popSize population size.
     * @param crossProb crossover probability.
     * @param mutProb mutation probability.
     * @param train training dataset. 
     * @param norm_acc initial accuracies.
     */
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
   * Initializes the population for a small Genetic Algorithm.
   * @param atributos boolean[]  Selected antecedents (attributes) for the chromosomes.
   * @param clase String consequent class of all the chromosomes.
   * @param codigoRegla int rule code.
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
   * Initializes randomly the population for a large Genetic Algorithm.
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

    /**
     * Returns a String representation of the population.
     * @return a String representation of the population.
     */
    public String printString() {
    String cadena = new String("");
    for (int i = 0; i < popSize; i++) {
      cadena += "Chromosome[" + (i + 1) + "]: " + cromosomas.get(i).printString();
    }
    return cadena;
  }

    /**
     * Performs the small GA to generate the different rules for the decision tree.
     */
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

  /** Selects one of the given two chromosomes to form the new generation (population). 
   * Compares the two fitness values and selects the greater one.
     @param indice Son's index for the new population.
     @param cromosoma1 First individual's index of the actual population.
     @param cromosoma2 Second individual's index of the actual population.
     * 
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
   * Obtains the number of examples for the i-th class.
   * @param clase int class position.
   * @return int the number of examples for the i-th class.
   */
  public int numEjemplos(int clase){
    int n_ejemplos = 0;
    for (int i = 0; i < nEjemplos; i++){
      if (clase == train.getOutputAsInteger(ejemplos[i]))
        n_ejemplos++;
    }
    return n_ejemplos;
  }


  /** Computes the information gain.
   * Info(G) = - sum_{j=1}^c {|Gj|/|T|*log_2{|Gj|/|T|}}
   * c = number of classes. 
   * |Gj| = number of examples of the class j.
   * |T| = number of total examples.
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

    /**
     * Chooses the examples to be used as training whoses boolean value is true.
     * @param ejemplos given boolean vector containing the information of the selected examples.
     */
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

     /**
     * Performs the large GA to generate the different rules for the decision tree.
     */
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

    /**
     * Returns the rules generated by the GA and stored on the chromosomes of the population.
     * @return Array of rules that is used to build the tree.
     */
    public ArrayList<Regla> dameReglas() {
    return reglas;
  }

}

