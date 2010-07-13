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

package keel.Algorithms.Genetic_Rule_Learning.Corcoran;

import java.util.*;
import java.io.IOException;

import org.core.*;

/**
 * <p>Title: Main class of the algorithm</p>
 * <p>Description: It contains the esential methods for the CN2 algorithm</p>
 * <p>Created: December 11th 2004</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: KEEL</p>
 * @author Alberto Fernández (University of Granada) 11/12/2004
 * @since JDK1.5
 * @version 1.8
 */
public class Corcoran {
  public Individual New[];

  public double max[], min[];
  public int rules[][];
  public int result_class[], fr_classes[], ex_classes[];
  public double offspring1[], offspring2[];
  public boolean nominales[];

  public static int n_generations,
      population_size,
      n_gens,
      rows,
      rows_tst,
      columns,
      nrules;

  public double cross_prob,
      mutation_prob,
      seed;

  public int sample[];
  public long Seed;
  public long OrigSeed;

  public double Best_current_perf;
  public int Best_guy;
  public int Gen;
  public int Trials;
  public double Worst_current_perf1, Worst_current_perf2;
  public int Worst_guy1, Worst_guy2;

  private String trainFile, evalFile, testFile;
  private String outputFileTr, outputFileTst, outputFile;

  public myDataset trainData, evalData, testData;
  private String myOutput;
  private static String classNames[], attributeNames[];
  private int trainClasses[], evalClasses[],testClasses[];
  private int nClasses;

  private int trainResults[], testResults[];
  private double dataT[][], dataTst[][];

  /**
   * Comparison of two real values for maximization
   * @param X First value
   * @param Y Second Value
   * @return True if the first value is higher than the second one (maximization)
   */
  private boolean BETTER(double X, double Y) {
    return ( (X) > (Y));
  };

  /**
   * This function initializes the population randomly
   */
  private void Initialize() {

    int i, j, r, n_var;
    int indice; //manages the current input variable

    Randomize.setSeed(Seed);

    Trials = 0;
    Gen = 0;
    n_var = columns;

    for (i = 0; i < n_var; i++) { // Here it is computed the max and min values of training and test
      min[i] = dataT[0][i];
      max[i] = dataT[0][i];
    }

    for (i = 0; i < n_var; i++) {
      for (j = 1; j < rows; j++) {
        if (dataT[j][i] < min[i]) {
          min[i] = dataT[j][i];
        }
        else if (dataT[j][i] > max[i]) {
          max[i] = dataT[j][i];
        }
      }
    }

    // We initialize the population randomly between the universe of discourse
    for (i = 0; i < population_size; i++) {
      for (j = 0, r = 0; j < n_gens; j++, r++) {
        // atributos
        for (indice = 0; indice < n_var; indice++, j++) {
            if (nominales[indice]){
              New[i].setGene(j,
                             Randomize.RandintClosed((int)min[indice], (int)max[indice]));
              j++;
              New[i].setGene(j,
                             Randomize.RandintClosed((int)min[indice], (int)max[indice]));
            }else{
              New[i].setGene(j,
                             Randomize.RanddoubleClosed(min[indice], max[indice]));
              j++;
              New[i].setGene(j,
                             Randomize.RanddoubleClosed(min[indice], max[indice]));
            }
          }
        // class
        New[i].setGene(j, Randomize.RandintClosed(0, nClasses));

        New[i].setExitos(r, 0);
        New[i].setFracasos(r, 0);
      }
      New[i].setN_e(true);
    }
  };

  /**
   * Evaluates a whole population. It obtains the best and worst chromosomes
   */
  private void Evaluate() {

    double performance;
    int i;

    for (i = 0; i < population_size; i++) {
      if (New[i].getN_e()) { // If not evaluated
        New[i].setPerf(evalua(New[i]));
        performance = New[i].getPerf();
        New[i].setN_e(false);
        Trials++;
      }
      else {
        performance = New[i].getPerf();

      }
      if (i == 0) {
        Best_current_perf = performance; //Best current individual
        Best_guy = 0;

        // Worst individuals
        Worst_current_perf1 = performance;
        Worst_guy1 = 0;

        Worst_current_perf2 = 1;
        Worst_guy2 = 0;
      }
      else {
        if (BETTER(performance, Best_current_perf)) {
          Best_current_perf = performance;
          Best_guy = i;
        }

        // if the current chromosome is worst than the worst, it is removed
        else if (BETTER(Worst_current_perf2, performance)) {
          Worst_current_perf1 = Worst_current_perf2;
          Worst_guy1 = Worst_guy2;
          Worst_current_perf2 = performance;
          Worst_guy2 = i;
        }
        else if (BETTER(Worst_current_perf1, performance)) {
          Worst_current_perf1 = performance;
          Worst_guy1 = i;
        }
      }
    }

  };

  /**
   * It performs the evaluation of an individul (set of rules) for a given data-set
   * @param ind Individual The chromosome or rule set
   * @param datos myDataset The Data-set
   * @return int [] The predicted class for the rule set
   */
  private int [] evaluacion(Individual ind,myDataset datos){
    int i, j, ii, k, ok; // ok checks that it is always between max and min
    int indice;
    double aleat, mejor_fracasos;
    int mejor_clase, mejor_exitos, divisor;
    double[] cromosoma = ind.getTodo();
    int [] resultados;
    int[] vexitos = ind.getExitos();
    int[] vfracasos = ind.getFracasos();
    double elAtributo;

    int columns = datos.getData(0).getNattributes();
    int rows = datos.size();

    int [] clases = new int[rows];
    double [][] misDatos = new double[rows][columns];
    for (i = 0; i < rows; i++) {
      clases[i] = datos.getData(i).getClas();
      for (j = 0; j < columns; j++) {
        misDatos[i][j] = datos.getData(i).getAttribute(j);
      }
    }
    int rules[][] = new int[rows][nrules];
    resultados = new int[rows];
    divisor = (2 * (columns + 1)) - 1;

    for (i = 0; i < rows; i++) {
      for (ii = 0; ii < nrules; ii++) {
        rules[i][ii] = 0;
        vexitos[ii] = 0;
        vfracasos[ii] = 0;

        indice = ii * divisor; // with index we manage the position of the attribute inside the chromosome
        ok = 1; // ok = 1 -> the rule satisfies the example

        for (j = 0; (j < columns) && (ok == 1); j++) {
          elAtributo = misDatos[i][j];
          if ( ( (cromosoma[indice] > elAtributo) ||
                (cromosoma[indice + 1] < elAtributo)) &&
              (cromosoma[indice] < cromosoma[indice + 1])) {
            ok = 0;
          }
          indice = indice + 2;
        }

        if (ok == 1) {
          rules[i][ii] = 1;

          indice = (ii * divisor) + divisor - 1;
          //System.err.println("Clase -> "+cromosoma[indice]+", NDatos: "+rows+", NAtributos: "+columns+", divisor: "+divisor);
          if (cromosoma[indice] == clases[i]) {
            vexitos[ii]++;
          }
          else {
            vfracasos[ii]++;
          }
        }
      } // end rules

    }

    for (i = 0; i < rows; i++) {
      for (k = 0; k < nClasses; k++) {
        result_class[k] = -1;
        ex_classes[k] = 0;
        fr_classes[k] = 0;
      }

      // Now we compute the accuracy for each class
      for (k = 0; k < nrules; k++) {
        if (rules[i][k] != 0) {
          for (j = 0; j < nClasses; j++) {
            indice = k * divisor + divisor - 1;
            if ( (int) cromosoma[indice] == j) {
              ex_classes[j] += vexitos[k];
              fr_classes[j] += vfracasos[k];
              result_class[j] = 1;
            }
          }
        }
      }

      mejor_clase = -1;
      mejor_exitos = -1;
      mejor_fracasos = Double.MAX_VALUE;

      // We search for the class with higher accuracy
      for (k = 0; k < nClasses; k++) {
        if (result_class[k] != -1) {
          if (fr_classes[k] < mejor_fracasos) {
            mejor_clase = k;
            mejor_exitos = ex_classes[k];
            mejor_fracasos = fr_classes[k];
          }

          // same number of misclassifications
          else if (fr_classes[k] == mejor_fracasos) {
            // we check the correct classifications
            if (ex_classes[k] > mejor_exitos) {
              mejor_clase = k;
              mejor_exitos = vexitos[k];
              mejor_fracasos = vfracasos[k];
            }

            // if they are the same, we choose randomly
            else if (ex_classes[k] == mejor_exitos) {
              aleat = Randomize.RandClosed();
              if (aleat > 0.5) {
                mejor_clase = k;
                mejor_exitos = vexitos[k];
                mejor_fracasos = vfracasos[k];
              }
            }
          }
        }
      }

      resultados[i] = mejor_clase;
    }

    return resultados;

  }

  /**
   * It computes the accuracy measure for a given output and a data-set
   * @param ejemplos myDataset The data-set
   * @param resultados int[] The predicted classes
   * @return double The accuracy
   */
  private double porcentajeAcierto(myDataset ejemplos,int [] resultados){
    int aciertos = 0;
    if (ejemplos.size() != resultados.length){
      System.err.println("Error! -> the data-set has a different size with respect to the results one");
      System.exit(-1);
    }
    int rows = ejemplos.size();

    for (int i = 0; i < rows; i++) {
      if (resultados[i] == ejemplos.getData(i).getClas()){
        aciertos++;
      }
    }

    return ((1.0*aciertos)/rows);
  }

  /**
   * Evaluates an individual
   *
   * @param chrom The chromosome to evaluate
   * @return The fitness value for chrom
   */
  private double evalua(Individual chrom) {

    int i, j, ii, k, ok;
    int indice;
    double exito, aleat, mejor_fracasos;
    int mejor_clase, mejor_exitos, divisor;
    double[] cromosoma = chrom.getTodo();
    int[] vexitos = chrom.getExitos();
    int[] vfracasos = chrom.getFracasos();
    double elAtributo;

    exito = 0;
    divisor = (2 * (columns + 1))-1;

    for (i = 0; i < rows; i++) {
      for (ii = 0; ii < nrules; ii++) {
        rules[i][ii] = 0;
        vexitos[ii] = 0;
        vfracasos[ii] = 0;

        indice = ii * divisor;
        ok = 1;

        for (j = 0; (j < columns) && (ok == 1); j++) {
          elAtributo = dataT[i][j];
          if ( ( (cromosoma[indice] > elAtributo) ||
                (cromosoma[indice + 1] < elAtributo)) &&
              (cromosoma[indice] < cromosoma[indice + 1])) {
            ok = 0;
          }
          indice = indice + 2;
        }

        if (ok == 1) {
          rules[i][ii] = 1;

          indice = (ii * divisor) + divisor - 1;
          //System.err.println("Clase -> "+cromosoma[indice]+", NAtributos: "+columns+", divisor: "+divisor);
          if (cromosoma[indice] == trainClasses[i]) {
            vexitos[ii]++;
          }
          else {
            vfracasos[ii]++;
          }
        }
      }

    }

    for (i = 0; i < rows; i++) {
      for (k = 0; k < nClasses; k++) {
        result_class[k] = -1;
        ex_classes[k] = 0;
        fr_classes[k] = 0;
      }

      for (k = 0; k < nrules; k++) {
        if (rules[i][k] != 0) {
          for (j = 0; j < nClasses; j++) {
            indice = k * divisor + divisor - 1;
            if ( (int) cromosoma[indice] == j) {
              ex_classes[j] += vexitos[k];
              fr_classes[j] += vfracasos[k];
              result_class[j] = 1;
            }
          }
        }
      }

      mejor_clase = -1;
      mejor_exitos = -1;
      mejor_fracasos = Double.MAX_VALUE;

      for (k = 0; k < nClasses; k++) {
        if (result_class[k] != -1) {
          if (fr_classes[k] < mejor_fracasos) {
            mejor_clase = k;
            mejor_exitos = ex_classes[k];
            mejor_fracasos = fr_classes[k];
          }

          else if (fr_classes[k] == mejor_fracasos) {
            if (ex_classes[k] > mejor_exitos) {
              mejor_clase = k;
              mejor_exitos = vexitos[k];
              mejor_fracasos = vfracasos[k];
            }

            else if (ex_classes[k] == mejor_exitos) {
              aleat = Randomize.RandClosed();
              if (aleat > 0.5) {
                mejor_clase = k;
                mejor_exitos = vexitos[k];
                mejor_fracasos = vfracasos[k];
              }
            }
          }
        }
      }

      if (mejor_clase != -1) {
        if (mejor_clase == trainClasses[i]) {
          exito++;
        }
      }
      trainResults[i] = mejor_clase;
    }

    return (exito / rows);
  };

  /**
   * We select two different individuals randomly from all the population
   */
  private void Select() {
    int i, j2, j;
    j = Randomize.RandintClosed(0, population_size);
    for (i = 0; i < n_gens; i++) {
      offspring1[i] = New[j].getGene(i);

    }
    do {
      j2 = Randomize.RandintClosed(0, population_size);
    }
    while (j2 == j);

    for (i = 0; i < n_gens; i++) {
      offspring2[i] = New[j2].getGene(i);

    }
  };

  /**
   * Simple crossover<br/><br/>
   *
   * We select a rule as "cut point". Half of the rules of a chromosome goes to the other parent and all the way
   * round, generating two new offspring.
   */
  private void Cruce_simple() {
    int i, regla;
    double tmp;

    regla = Randomize.RandintClosed(0, nrules);

    for (i = regla * (2 * columns); i < n_gens; i++) {
      tmp = offspring1[i];
      offspring1[i] = offspring2[i];
      offspring2[i] = tmp;
    }
  };

  /**
   * Mutation Operator<br/><br/>
   *
   * Given a mutation probability, two offspring changes the range of their genes
   */
  private void Mutacion() {

    int i, divisor, pos;
    double aleat, ratio;

    divisor = 2 * columns + 1;

    // First, we apply the creepy mutation
    aleat = Randomize.RandClosed();

    if (aleat > mutation_prob) {
      for (i = 0; i < n_gens - 1; i = i + 2) {
        if ( (i % divisor) == (divisor - 1)) {
          i--;
        }
        else {
          aleat = Randomize.RandClosed();
          if (aleat > mutation_prob) {
            pos = i % divisor;
            pos = pos / 2;

            aleat = Randomize.RandClosed();

            if (aleat > 0.5) {
              offspring1[i] += ( (max[pos] - min[pos]) / 100);

              // if goes outside the range, we assign the maximum value
              if (offspring1[i] > max[pos]) {
                offspring1[i] = max[pos];
              }
            }
            else {
              offspring1[i] -= ( (max[pos] - min[pos]) / 100);

              // if goes outside the range, we assign the minimum value
              if (offspring1[i] < min[pos]) {
                offspring1[i] = min[pos];
              }
            }

            aleat = Randomize.RandClosed();
            if (aleat > 0.5) {
              offspring1[i + 1] += ( (max[pos] - min[pos]) / 100);
              if (offspring1[i + 1] > max[pos]) {
                offspring1[i + 1] = max[pos];
              }
            }
            else {
              offspring1[i + 1] -= ( (max[pos] - min[pos]) / 100);
              if (offspring1[i + 1] < min[pos]) {
                offspring1[i + 1] = min[pos];
              }
            }
          }
        }
      }
    }
    else {
      for (i = 0; i < n_gens - 1; i = i + 2) {
        if ( (i % divisor) == (divisor - 1)) {
          i--;
        }
        else {
          aleat = Randomize.RandClosed();
          if (aleat > mutation_prob) {
            pos = i % divisor;
            pos = pos / 2;

            aleat = Randomize.RandClosed();
            if (aleat > 0.5) {
              offspring2[i] += ( (max[pos] - min[pos]) / 100);
              if (offspring2[i] > max[pos]) {
                offspring2[i] = max[pos];
              }
            }
            else {
              offspring2[i] -= ( (max[pos] - min[pos]) / 100);
              if (offspring2[i] < min[pos]) {
                offspring2[i] = min[pos];
              }
            }
            aleat = Randomize.RandClosed();
            if (aleat > 0.5) {
              offspring2[i + 1] += ( (max[pos] - min[pos]) / 100);
              if (offspring2[i + 1] > max[pos]) {
                offspring2[i + 1] = max[pos];
              }
            }
            else {
              offspring2[i + 1] -= ( (max[pos] - min[pos]) / 100);
              if (offspring2[i + 1] < min[pos]) {
                offspring2[i + 1] = min[pos];
              }
            }
          }
        }
      }
    }

    // Second, we apply the simple random mutation
    ratio = (double) 1 / (2 * (columns - 1) + 2);
    for (i = 0; i < n_gens; i++) {
      aleat = Randomize.RandClosed();
      if (aleat <= ratio) {
        if ( (i % divisor) == (divisor - 1)) { // it is a class value
          offspring1[i] = Randomize.RandintClosed(0, nClasses);
        }
        else {
          pos = i % divisor;
          pos = pos / 2;
          offspring1[i] = Randomize.RanddoubleClosed(min[pos], max[pos]+1);
        }
      }
      aleat = Randomize.RandClosed();
      if (aleat <= ratio) {
        if ( (i % divisor) == (divisor - 1)) {
          offspring2[i] = Randomize.RandintClosed(0, nClasses);
        }
        else {
          pos = i % divisor;
          pos = pos / 2;
          offspring2[i] = Randomize.RanddoubleClosed(min[pos], max[pos]);
        }
      }
    }

  };

  /**
   * We replace the two worst genes by the new two offspring.<br/><br/>
   */
  private void Sustituir() {
    int i;

    for (i = 0; i < n_gens; i++) {
      New[Worst_guy1].setGene(i, offspring1[i]);
      New[Worst_guy2].setGene(i, offspring2[i]);
    }

    for (i = 0; i < nrules; i++) {
      New[Worst_guy1].setExitos(i, 0);
      New[Worst_guy1].setFracasos(i, 0);
      New[Worst_guy2].setExitos(i, 0);
      New[Worst_guy2].setFracasos(i, 0);
    }

    New[Worst_guy1].setN_e(true);
    New[Worst_guy2].setN_e(true);

  };

  /**
   * It read the input data from file
   * @param ficheroParam Name of the parameters file
   */
  private void Input(String ficheroParam) {

    StringTokenizer linea, datos;
    String fichero = Files.readFile(ficheroParam);
    String una_linea;
    String valores[] = new String[6];
    int i, j;

    linea = new StringTokenizer(fichero, "\n\r");
    linea.nextToken(); //Algorithm name
    una_linea = linea.nextToken();
    datos = new StringTokenizer(una_linea, " = \" ");
    datos.nextToken(); //inputData
    trainFile = datos.nextToken();
    evalFile = datos.nextToken();
    testFile = datos.nextToken();
    una_linea = linea.nextToken();
    datos = new StringTokenizer(una_linea, " = \" ");
    datos.nextToken(); //outputData
    outputFileTr = datos.nextToken();
    outputFileTst = datos.nextToken();
    outputFile = datos.nextToken();

    for (i = 0; i < 6; i++) {
      una_linea = linea.nextToken();
      datos = new StringTokenizer(una_linea, " = \" ");
      datos.nextToken();
      valores[i] = datos.nextToken();
    }

    Seed = Long.parseLong(valores[0]);
    n_generations = Integer.parseInt(valores[1]);
    population_size = Integer.parseInt(valores[2]);
    cross_prob = Double.parseDouble(valores[3]);
    mutation_prob = Double.parseDouble(valores[4]);
    nrules = Integer.parseInt(valores[5]);

    // Data-sets
    Dataset train = new Dataset(); //trainFile);
    Dataset eval = new Dataset();
    Dataset test = new Dataset(); //testFile);  }
    try {
      train.readSet(trainFile, true);
      eval.readSet(evalFile, false);
      test.readSet(testFile, false);
    }
    catch (IOException e) {
      System.err.println("There was a problem while reading the data-set files");
      System.err.println("-> " + e);
      System.exit(0);
    }

    myOutput = new String("");
    myOutput = test.copiaCabeceraTest();

    System.out.println("\nGenerating data-sets");
    trainData = creaConjunto(train);
    evalData = creaConjunto(eval);
    testData = creaConjunto(test);
    columns = trainData.getData(0).getNattributes();
    rows = trainData.size();
    rows_tst = testData.size();

    trainClasses = new int[rows];
    dataT = new double[rows][columns];
    for (i = 0; i < rows; i++) {
      trainClasses[i] = trainData.getData(i).getClas();
      for (j = 0; j < columns; j++) {
        dataT[i][j] = trainData.getData(i).getAttribute(j);
      }
    }

    evalClasses = new int[evalData.size()];
    for (i = 0; i < evalData.size(); i++) {
      evalClasses[i] = evalData.getData(i).getClas();
    }

    testClasses = new int[rows_tst];
    dataTst = new double[rows_tst][columns];
    for (i = 0; i < testData.size(); i++) {
      testClasses[i] = testData.getData(i).getClas();
      for (j = 0; j < columns; j++) {
        dataTst[i][j] = testData.getData(i).getAttribute(j);
      }
    }

    nClasses = 0;
    for (i = 0; i < trainClasses.length; i++) {
      if (trainClasses[i] > nClasses) {
        nClasses = trainClasses[i];
      }
    }
    nClasses++;

    // number of genes is equal to the number of attributes per number of rules
    n_gens = (columns * 2 + 1) * nrules;
    New = new Individual[population_size];
    for (i = 0; i < population_size; i++) {
      New[i] = new Individual(n_gens, nrules);

    }
    sample = new int[population_size];

    max = new double[columns];
    min = new double[columns];
    rules = new int[rows][nrules];

    offspring1 = new double[n_gens];
    offspring2 = new double[n_gens];

    trainResults = new int[rows];
    testResults = new int[rows_tst];
    classNames = train.dameClases();
    String [] classNamesAux = test.dameClases();
    attributeNames = train.dameNombres();
    if (classNames.length < classNamesAux.length){
      nClasses = classNamesAux.length -1;
      classNames = new String[nClasses+1];
      for (i = 0; i < nClasses+1; i++){
        classNames[i] = classNamesAux[i];
      }
    }

    result_class = new int[nClasses];
    ex_classes = new int[nClasses];
    fr_classes = new int[nClasses];
    nominales = train.getNominales();

  };

  /**
   * It creates a dataset (attributes/class) according to those obtained from a data-file
   * @param myData It must be a dataset read from file
   * @return The new dataset created, that is, a linked-list of objects "muestras"
   */
  private myDataset creaConjunto(Dataset myData) {
      myDataset datos = new myDataset();
      int tam = myData.getnentradas();
      double[] vars = new double[tam];
      double[][] X;
      int[] C;
      int clase = 0;
      X = myData.getX();
      C = myData.getC();
      for (int i = 0; i < myData.getndatos(); i++) {
          boolean salir = false;
          for (int j = 0; (j < tam) && (!salir); j++) {
              if (myData.isMissing(i, j)) {
                  salir = true;
              } else {
                  vars[j] = X[i][j];
              }
          }
          if (!salir) {
              clase = C[i]; //Integer.parseInt(mis_datos.getDatosIndex(i, tam));
              Instance m = new Instance(vars, clase, tam);
              m.setPosFile(i);
              datos.addData(m);
          }
      }
      return datos;
    }

  /**
   * It writes the output data:
   * <ul>
   * <li> General Data</li>
   * <li> Training file</li>
   * <li> Test File</li>
   * </ul>
   *
   * @param ptrain Training accuracy
   * @param peval Validation accuracy
   * @param ptest Test accuracy
   * @param res Predicted classes in the validation set
   * @param resTest Predicted classes in the test set
   * @param tiempo Execution time in miliseconds
   * @param BR String it is an string that stores the best rule base found during the search process
   */
  private void escribeSalida(double ptrain, double peval, double ptest, int [] res, int [] resTest, long tiempo, String BR) {
    Files f = new Files();
    String cad = "";
    String result = "";

    //cad += "Fichero de datos=" + trainFile;
    cad += "@Seed=" + Seed;
    cad += "\n@Training Accuracy=" + ptrain;
    cad += "\n@Validation Accuracy=" + peval;
    cad += "\n@Test Accuracy=" + ptest;
    cad += "\n@Number of Rules=" + nrules;
    //cad += "\nTiempo transcurrido (sec.)= " + tiempo;
    cad += "\n@Rule Base: \n" + BR;

    f.writeFile(outputFile, cad);
    for (int i = 0; i < res.length; i++) {
      result += new String(classNames[evalClasses[i]+1] + " " + classNames[res[i]+1] + "\n");

    }
    f.writeFile(outputFileTr, myOutput + result);

    result = "";
    for (int i = 0; i < resTest.length; i++) {
      result += new String(classNames[testClasses[i]+1] + " " + classNames[resTest[i]+1] + "\n");

    }
    f.writeFile(outputFileTst, myOutput + result);
  };

  /**
   * Main function of the program<br/>
   * The scheme of the steady-state Pitts-GIRLA method is the following one:
   * <ol>
   * <li> Selection </li>
   * <li> Crossover</li>
   * <li> Mutation</li>
   * <li> Replacement</li>
   * <li> Evaluation</li>
   * <li> Until X generations have been carried out
   * </ol>
   *
   * @param args Arguments
   */
  public static void main(String[] args) {

    Corcoran programa = new Corcoran();
    boolean seguir = true;

    // Input data reading and initialization of the variables and structures
    programa.Input(args[0]);
    if (programa.nClasses > programa.nrules){
      System.err.println("There are more classes than rules, please modify the parameters");
      System.err.println("Aborting program...");
      seguir = false;
    }
    if (seguir){

        System.out.println("\nExecuting program ...\n\n");
        long tiempo = System.currentTimeMillis();
        programa.Best_guy = 0;

        // Initalize the population
        programa.Initialize();

        // Evaluation of the individuals from the initial population
        programa.Evaluate();
        programa.Gen++;

        // General cicle of the genetic algorithm
        double mejorPorc = 0;
        do {
            programa.Select(); // Two parents are selected randomly
            programa.Cruce_simple(); // Crossover
            programa.Mutacion(); // Mutation
            programa.Sustituir(); // Replacement of the two worst chromosomes for the offpring
            programa.Evaluate(); // Evaluacion de los Individuals de la poblacion actual

            programa.Gen++;
            if (programa.New[programa.Best_guy].getPerf() > mejorPorc) {
                mejorPorc = programa.New[programa.Best_guy].getPerf();
                System.out.println("Generation =" + (programa.Gen - 1) +
                                   ". Accuracy -> " +
                                   mejorPorc);
            }
        } while ((programa.Gen <= programa.n_generations) &&
                 (programa.New[programa.Best_guy].getPerf() < 1.0));
        tiempo = ((System.currentTimeMillis() - tiempo) / 60000);

        int[] res = programa.evaluacion(programa.New[programa.Best_guy],
                                        programa.evalData);
        double peval = programa.porcentajeAcierto(programa.evalData, res);
        int[] resTest = programa.evaluacion(programa.New[programa.Best_guy],
                                            programa.testData);
        double ptest = programa.porcentajeAcierto(programa.testData, resTest);
        double ptrain = programa.New[programa.Best_guy].getPerf();

        System.out.println("\n\nTraining accuracy =" + ptrain);
        System.out.println("Evaluation accuracy =" + peval);
        System.out.println("Test accuracy =" + ptest);

        tiempo = System.currentTimeMillis() - tiempo;
        String BR = programa.New[programa.Best_guy].decodificaBR(nrules,
                columns, attributeNames, classNames);
        programa.escribeSalida(ptrain, peval, ptest, res, resTest, tiempo, BR);

    }
    System.out.println("\n*****  END OF THE PROGRAM *******");

  }

};

