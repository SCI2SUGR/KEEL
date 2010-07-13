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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierSLAVE;

/**
 * <p>
 * @author Written by Francisco José Berlanga (University of Jaén) 01/01/2007
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */

import java.io.IOException;
import java.util.*;
import org.core.*;

public class Algorithm {
/**	
 * <p>
 * It contains the implementation of the algorithm
 * </p>
 */
 
  myDataset train, val, test;
  String outputTr, outputTst;
  String fichero_reglas, fichero_inf;
  double classProb[];
  double attrProb[][][]; //atribute value, atribute position, class
  int nClasses, num_etiquetas, tamPoblacion, Iteraciones_sin_cambios;
  double probMut;
  long semilla;
  VectorVar V;
  example_set E, E_Tra, E_Test;
  boolean usarPeso;

  //We may declare here the algorithm's parameters

  private boolean somethingWrong = false; //to check if everything is correct.

    /**
     * <p>
     * Default constructor
     * </p>
     */
  public Algorithm() {
  }

    /**
     * <p>
     * It reads the data from the input files (training, validation and test) and parse all the parameters
     * from the parameters array.
     * </p>
     * @param parameters parseParameters It contains the input files, output files and parameters
     */
  public Algorithm(parseParameters parameters) {

    train = new myDataset();
    val = new myDataset();
    test = new myDataset();
    try {
      System.out.println("\nReading the training set: " +
                         parameters.getTrainingInputFile());
      train.readClassificationSet(parameters.getTrainingInputFile(), true);
      System.out.println("\nReading the validation set: " +
                         parameters.getValidationInputFile());
      val.readClassificationSet(parameters.getValidationInputFile(), false);
      System.out.println("\nReading the test set: " +
                         parameters.getTestInputFile());
      test.readClassificationSet(parameters.getTestInputFile(), false);
    }
    catch (IOException e) {
      System.err.println(
          "There was a problem while reading the input data-sets: " +
          e);
      somethingWrong = true;
    }

    //We may check if there are some numerical attributes, because our algorithm may not handle them:
    //somethingWrong = somethingWrong || train.hasNumericalAttributes();
    //somethingWrong = somethingWrong || train.hasMissingAttributes();

    outputTr = parameters.getTrainingOutputFile();
    outputTst = parameters.getTestOutputFile();
    fichero_reglas = parameters.getOutputFile(0);
    fichero_inf = parameters.getOutputFile(1);

    //Now we parse the parameters, for example:
    semilla = Long.parseLong(parameters.getParameter(0));
    //...
    num_etiquetas = Integer.parseInt(parameters.getParameter(1));
    tamPoblacion = Integer.parseInt(parameters.getParameter(2));
    Iteraciones_sin_cambios = Integer.parseInt(parameters.getParameter(3));
    probMut = Double.parseDouble(parameters.getParameter(4));
    String peso = parameters.getParameter(5);
    usarPeso = false;
    if (peso.equalsIgnoreCase("YES")){
      usarPeso = true;
    }
  }

    /**
     * <p>
     * It launches the algorithm
     * </p>
     */
  public void execute() {
    if (somethingWrong) { //We do not execute the program
      System.err.println("An error was found, either the data-set have numerical values or missing values.");
      System.err.println("Aborting the program");
      //We should not use the statement: System.exit(-1);
    }
    else {
      //We do here the algorithm's operations
      Randomize.setSeed(semilla);

//            num_etiquetas = 5;

      V = new VectorVar(train, num_etiquetas);
      E = new example_set(train);
      E.AddPartitionTest(test);

      //number of binary populations
      int NPobBin = 1;
      //number of integer populations
      int NPobEnt = 2;
      //number of real populations
      int NPobRea = 2; //5
      //Elite of the genetic population
      int Elite = 10;
      //number of components of the fitness function
      int NFitness = 3;
      //number of individuals in the population
      int Indiv = tamPoblacion;
      //total number of populations in the genetic algorithm
      int NPobTotal = NPobBin + NPobEnt + NPobRea;
      //Variation rank of each integer population [0,rango[i]]
      int[] rango = new int[NPobEnt];
      //Variation rank of each real population [rangoi[i],rangos[i]]
      double[] rangoi = new double[NPobRea];
      double[] rangos = new double[NPobRea];
      //Genetic algorithm's parameters:
      //  Size of each population, prob. of mut and prob. of crossover.
      int[] tama = new int[NPobTotal];
      double[] mut = new double[NPobTotal];
      double[] cruce = new double[NPobTotal];

      //Genetic algorithm's configuration
      for (int i = 0; i < NPobBin; i++) {
        mut[i] = probMut; // 0.05
        cruce[i] = 0.3; // 0.6
      }

      for (int i = NPobBin; i < NPobBin + NPobEnt; i++) {
        mut[i] = 0.0;
        cruce[i] = 0.0;
      }

      mut[NPobBin] = probMut; // 0.01
      cruce[NPobBin] = 0.2;

      for (int i = NPobBin + NPobEnt; i < NPobBin + NPobEnt + NPobRea;
           i++) {
        mut[i] = 0; // 0.01
        cruce[i] = 0;
      }

      // Values for the variable's level
      mut[NPobBin + NPobEnt] = 1.0 / V.N_Antecedents(); // 0.01
      cruce[NPobBin + NPobEnt] = 0.2;

      int antecedente;
      // Size and rank of the value's level
      ArrayList<Integer> lista = new ArrayList<Integer> (2);

//            System.out.println("Tama[0]: "+tama[0]+", Rango[0]:"+rango[0]);
      Integer aux1 = new Integer(tama[0]);
      Integer aux2 = new Integer(rango[0]);

      lista.add(aux1);
      lista.add(aux2);

      V.Encode(lista);

      aux1 = lista.get(0);
      aux2 = lista.get(1);
      tama[0] = aux1.intValue();
      rango[0] = aux2.intValue();
//            System.out.println("Tama[0]: "+tama[0]+", Rango[0]:"+rango[0]);

      // Rank for the variable's level
      rangoi[0] = 0.0;
      rangos[0] = 1.0;
      // Rank for the set of covered examples
      rangoi[1] = -1.0;
      rangos[1] = 1.0;
      // Size of the consequent's level
      tama[1] = 1;
      // Size of the dominance's level
      tama[2] = 1;
      // Size of the variable's level
      tama[3] = V.N_Antecedents() + 1;
      // Size of the set of example's level
      tama[4] = E.N_Examples() + 1; // The last position keeps the real size of elements

      multiPopulation G = new multiPopulation(NPobBin, NPobEnt, NPobRea,
                                            rango, rangoi, rangos, mut,
                                            cruce, Elite, Indiv, tama,
                                            NFitness);

      ruleset R = new ruleset();
      R.AddDomain(V);

      int n, z;
      double accuracy_old = 0.0;
      double accuracy_new = 0.0;
      double test = 0.0;
      double cardinal = 0.0;
      double variables_por_regla = 0.0;
      double variables_usadas = 0.0;
      int[] frecuencia_variables;
//            double tiempo_eje = new double[num_par];
      int iteraciones = 0;
      int condiciones = 0;
//            clock_t tiempo0, tiempo1;
      int iteraciones_parcial;

      double[] agregado;
      double[] peso_agregado;
      double[] valor = new double[3];

      frecuencia_variables = new int[V.N_Antecedents()];

      //double **Med_Inform;
      double[][] Med_Inform = ReservaParaMedidasDeInformacion(V);

      int[] ejemplos_por_clase = new int[rango[0]];

      iteraciones = 0;
      accuracy_old = -1;
      accuracy_new = 0;
      E_Tra = E.Extract_Training_Set(1);
      E_Tra.UnMarkAll();
      E_Test = E.Extract_Test_Set(1);
      E_Test.UnMarkAll();
      n = E_Tra.Not_Covered_Examples();
      z = E_Test.Not_Covered_Examples();
      int Total_ejemplos = E_Tra.N_Examples();
      agregado = new double[Total_ejemplos];
      peso_agregado = new double[Total_ejemplos];
      for (int i = 0; i < Total_ejemplos; i++) {
        agregado[i] = 0;
        peso_agregado[i] = 0;
      }

//                 tiempo0=clock();
      for (int clase = 0; clase < rango[0]; clase++) {

        do {
          E_Tra.Examples_per_Class(V.Consequent(), 1, rango[0],
                                   ejemplos_por_clase);
          System.out.println("Success: " + accuracy_new);
          System.out.println("Number of examples: " + n);
          for (int i = 0; i < rango[0]; i++) {
            System.out.println("Class " + i + ": " +
                               ejemplos_por_clase[i]);
          }

          // Calculo de las medidas de informacion para el nivel de variable
          System.out.println("Calculating information measures");

          ArrayList<double[][]>
              lista1 = new ArrayList<double[][]> (1);
          lista1.add(Med_Inform);
          MedidasDeInformacion(V, E_Tra, lista1);
          Med_Inform = lista1.get(0);
          System.out.println(
              "The information measures calculation is finished");

          iteraciones_parcial = GA(G, V, E_Tra, R, Med_Inform,
                                   clase,
                                   Total_ejemplos);
          valor[0] = G.FitnessValue(0);
          iteraciones += iteraciones_parcial;
          accuracy_old = accuracy_new;

          ArrayList<double[][]>
              lista2 = new ArrayList<double[][]> (1);
          double[][] list = new double[2][];
          list[0] = new double[Total_ejemplos];
          list[1] = new double[Total_ejemplos];

          for (int i = 0; i < Total_ejemplos; i++) {
            list[0][i] = agregado[i];
            list[1][i] = peso_agregado[i];
          }

          lista2.add(0, list);

          accuracy_new = Aciertos(R, V, E_Tra, lista2);

          list = lista2.get(0);
          for (int i = 0; i < Total_ejemplos; i++) {
            agregado[i] = list[0][i];
            peso_agregado[i] = list[1][i];
          }

          /*         if (accuracy_new[par]>=accuracy_old){
                     //cout << "Aciertos: " << accuracy_new[par]  << endl;
           accuracy_new[par] = Filtrar_Reglas(R[par],V,E_Par);
           cout << "Aciertos despues de filtrar: " << accuracy_new[par]  << endl;
                     accuracy_new[par] = Aciertos(R[par],V,E_Par,agregado, peso_agregado, fich_extension);
           cout << "Aciertos: " << accuracy_new[par]  << endl;
                     n=E_Par.Not_Covered_Examples();
                   }*/
          n = E_Tra.Not_Covered_Examples();
          System.out.println(" The output is: " + valor[0]);

        }
        while (valor[0] > 0 && n > 0);

        if (valor[0] <= 0) {

          R.Remove();
          //accuracy_new[par] = Filtrar_Reglas(R[par],V,E_Par);
          //accuracy_new[par] = Aciertos(R[par],V,E_Par,agregado,peso_agregado, fich_extension);
          //cout << "Aciertos despues de filtrar: " << accuracy_new[par]  << endl;
        }

        /*if (accuracy_new[par]==1){
          accuracy_new[par] = Filtrar_Reglas(R[par],V,E_Par);
          //accuracy_new[par] = Aciertos(R[par],V,E_Par,agregado,fich_extension);
         cout << "Aciertos despues de filtrar: " << accuracy_new[par]  << endl;
                            }*/

      } // End of for loop for each class

      test = Aciertos(R, V, E_Test);
      cardinal = R.N_rule();
      variables_por_regla = R.Variables_per_rule();

      ArrayList<int[]> lista2 = new ArrayList<int[]> (1);
      lista2.add(frecuencia_variables);
      variables_usadas = R.Frecuence_each_Variables(lista2);
      frecuencia_variables = lista2.get(0);

      condiciones = R.Labels_per_RB();
//                 tiempo1=clock();
//                 tiempo_eje[par]=1.0*(tiempo1-tiempo0)/CLOCKS_PER_SEC;
//                 tiempo0=tiempo1;

//                 out.open(fich_extension.c_str(),ios::app);
      System.out.println(
          "----------------------------------------------------");
      System.out.println("Training Success: " + accuracy_new);
      System.out.println("Test Success: " + test);
      System.out.println("Number of rules: " + cardinal);
      System.out.println("Variables per rule: " +
                         variables_por_regla);
      System.out.println("Used variables: " + variables_usadas);
//                 System.out.println("Tiempo: " << tiempo_eje);
      System.out.println("Iterations: " + iteraciones);
      System.out.println("Conditions: " + condiciones);
      System.out.println(
          "----------------------------------------------------");
//               System.out.println("------------------- REGLAS -------------------------");
//                 System.out.println("----------------------------------------------------");
//                 out.close();
//                 R[par].SaveRuleInterpreted(nomfich.c_str());
//                 R[par].SaveRuleInterpreted_append(fich_extension.c_str());
      //R[par].Save(nomfich2);
//              } // Final del for de las particiones





      //nClasses = train.getnOutputs();

      //Finally we should fill the training and test output files
      E_Tra = E.Extract_Training_Set(1);
      E_Tra.UnMarkAll();
      E_Test = E.Extract_Test_Set(1);
      E_Test.UnMarkAll();

      doOutput(R, V, E_Tra, this.val, this.outputTr);
      doOutput(R, V, E_Test, this.test, this.outputTst);

      // We create the file containing the semantic of the variables
      String output = new String("");
      output = V.PrintDefinitionToString();
      Files.writeFile(fichero_inf, output);

      // We create the file containing the learned fuzzy rules
      String output2 = new String("");
      output2 = R.PrintDefinitionToString();
      Files.writeFile(fichero_reglas, output2);

      System.out.println("Algorithm Finished");
    }
  }

  private int Clasificacion(ruleset R, VectorVar V, example_set E, int posicion) {
    int clase;
    vectordouble w;
    double grado = 0;
    int regla_disparada = 0;

    w = E.Data(posicion);

    ArrayList<Double> lista1 = new ArrayList<Double> (1);
    Double aux1 = Double.valueOf(grado);
    lista1.add(aux1);

    ArrayList<Integer> lista2 = new ArrayList<Integer> (1);
    Integer aux2 = Integer.valueOf(regla_disparada);
    lista2.add(aux2);

    clase = R.InferenceC(w, lista1, lista2);

    aux1 = lista1.get(0);
    grado = aux1.doubleValue();

    aux2 = lista2.get(0);
    regla_disparada = aux2.intValue();

    return clase;
  }

  private double Aciertos(ruleset R, VectorVar V, example_set E) {
    int n = E.N_Examples();
    int clase;
    vectordouble w;
    int[] marcar = new int[n];
    int tama1 = 0;
    double[] gmarcar = new double[n];
    int[] desmarcar = new int[n];
    int tama2 = 0;
    double[] gdesmarcar = new double[n];
    double bien = 0, mal = 0, nosesabe = 0, grado = 0, old_acierto = 0;
    int conse = V.Consequent();
    int regla_disparada = 0;
    int[] bien_clas = new int[R.N_rule()];
    int[] mal_clas = new int[R.N_rule()];

    for (int i = 0; i < R.N_rule(); i++) {
      bien_clas[i] = 0;
      mal_clas[i] = 0;
    }

    for (int i = 0; i < n; i++) {
      w = E.Data(i);

      ArrayList<Double> lista1 = new ArrayList<Double> (1);
      Double aux1 = Double.valueOf(grado);
      lista1.add(aux1);

      ArrayList<Integer> lista2 = new ArrayList<Integer> (1);
      Integer aux2 = Integer.valueOf(regla_disparada);
      lista2.add(aux2);

      clase = R.InferenceC(w, lista1, lista2);

      aux1 = lista1.get(0);
      grado = aux1.doubleValue();

      aux2 = lista2.get(0);
      regla_disparada = aux2.intValue();

      if (clase == -1) {
        nosesabe++;
        //desmarcar[tama2]=i;
        //gdesmarcar[tama2]=0;
        //tama2++;
      }
      else if (clase == w.At(conse)) {
        //else if (V.Adaptation(w.At(conse),conse,clase)>0){
        bien++;
        marcar[tama1] = i;
        gmarcar[tama1] = grado;
        tama1++;
        bien_clas[regla_disparada]++;
      }
      else {
        mal++;
        desmarcar[tama2] = i;
        gdesmarcar[tama2] = grado;
        tama2++;
        mal_clas[regla_disparada]++;
      }
    }

    E.Mark(marcar, tama1, gmarcar);
    E.UnMark(desmarcar, tama2, gdesmarcar);
//       ofstream out(nomfich.c_str(),ios::app);

    System.out.println("Success: " + bien);
    System.out.println("Mistakes:  " + mal);
    System.out.println("Not classified: " + nosesabe);

    for (int i = 0; i < R.N_rule(); i++) {
      System.out.println("   Rule " + i + ": " + bien_clas[i] + " / " +
                         mal_clas[i]);
    }

//       out.close();

    return bien / n;
  }

  private double Aciertos(ruleset R, VectorVar V, example_set E,
                  ArrayList<double[][]> milista) {
    int n = E.N_Examples();
    double[] agregado = new double[n];
    double[] peso_agregado = new double[n];

    double list[][] = milista.get(0);

    for (int i = 0; i < n; i++) {
      agregado[i] = list[0][i];
      peso_agregado[i] = list[1][i];
    }

    int clase;
    vectordouble w;
    int[] marcar = new int[n];
    int tama1 = 0;
    double[] gmarcar = new double[n];
    int[] desmarcar = new int[n];
    int tama2 = 0;
    double[] gdesmarcar = new double[n];
    double bien = 0, mal = 0, nosesabe = 0, grado = 0;
    int conse = V.Consequent();
    int regla_disparada = 0;
    int[] bien_clas = new int[R.N_rule()];
    int[] mal_clas = new int[R.N_rule()];

    for (int i = 0; i < R.N_rule(); i++) {
      bien_clas[i] = 0;
      mal_clas[i] = 0;
    }

    for (int i = 0; i < n; i++) {
      w = E.Data(i);

      ArrayList<Double> lista1 = new ArrayList<Double> (1);
      Double aux1 = Double.valueOf(grado);
      lista1.add(aux1);

      ArrayList<Integer> lista2 = new ArrayList<Integer> (1);
      Integer aux2 = Integer.valueOf(regla_disparada);
      lista2.add(aux2);

      clase = R.InferenceC(w, lista1, lista2);

      aux1 = lista1.get(0);
      grado = aux1.doubleValue();

      aux2 = lista2.get(0);
      regla_disparada = aux2.intValue();

      if (clase == -1) {
        nosesabe++;
        agregado[i] = 0;
        peso_agregado[i] = 0;
        //desmarcar[tama2]=i;
        //gdesmarcar[tama2]=0;
        //tama2++;
      }
      else if (clase == w.At(conse)) {
        //else if (V.Adaptation(w.At(conse),conse,clase)>0){
        bien++;
        marcar[tama1] = i;
        gmarcar[tama1] = grado;
        agregado[i] = grado;
        peso_agregado[i] = R.Get_Weight(regla_disparada);
        tama1++;
        bien_clas[regla_disparada]++;
      }
      else {
        mal++;
        desmarcar[tama2] = i;
        agregado[i] = -grado;
        peso_agregado[i] = R.Get_Weight(regla_disparada);
        gdesmarcar[tama2] = grado;
        tama2++;
        mal_clas[regla_disparada]++;
      }
    }

    E.Mark(marcar, tama1, gmarcar);
    E.UnMark(desmarcar, tama2, gdesmarcar);
//       ofstream out(nomfich.c_str(),ios::app);

    System.out.println("Success: " + bien);
    System.out.println("Mistakes:  " + mal);
    System.out.println("Not classified: " + nosesabe);

    for (int i = 0; i < R.N_rule(); i++) {
      System.out.println("   Rule " + i + ": " + bien_clas[i] + " / " +
                         mal_clas[i]);
    }

//       out.close();


    for (int i = 0; i < n; i++) {
      list[0][i] = agregado[i];
      list[1][i] = peso_agregado[i];
    }

    milista.add(0, list);

    return bien / n;

  }

  private int GA(multiPopulation G, VectorVar V, example_set E, ruleset R,
         double[][] I, int clase, int Total_ejemplos) {

    double peso;
    int NFitness = G.N_Valoracion();
    double[] valor = new double[NFitness];
    double[] valor_anterior = new double[NFitness];
//      ofstream f;
    int n = G.N_individuals();

    int sin_cambios = 0;
    int z;
    double aciertos;

    G.RandomInitialPopulation(I, V.SizeDomain(V.Consequent()),
                                Total_ejemplos, clase);
    fitness(G, V, E);
    G.Sort();

    for (int i = 0; i < 3; i++) {
      valor_anterior[i] = G.FitnessValue(0, i);
    }

    int t = 0;
    //f.open("slave.log",ios::out);
//        System.out.println("ITERACION " + t);
    //f.close();
//        G.PrintDefinition();

    while ( /*t<=1000*/sin_cambios <= Iteraciones_sin_cambios) {
      //G.Selection();
      //G.SteadyState_TwoPointsCrossover();
      G.SteadyState_LogicalBasedCrossover(); //Introducido 12/04/07
      G.SteadyState_UniformMutation();
      fitness(G, V, E);
      G.Sort();
      for (int i = 0; i < 3; i++) {
        valor[i] = G.FitnessValue(0, i);
      }

//            G.Print_SteadyState_Fitness(0);
      /*
          if (sin_cambios%100==0){
              System.out.println("------------------------------------");
              for (int i=0; i<n; i++)
                   G.Print_SteadyState_Fitness(i);
          System.out.println("------------------------------------");
          }
       */
      if (sin_cambios == 0) {
        System.out.println(t + ": ");
        G.Print_SteadyState_Fitness(0);
      }
      t++;

      //f.open("slave.log",ios::app);
//            System.out.println("ITERACION " + t);
      //f.close();
//            G.PrintDefinition();

      if (!Cambios(valor, valor_anterior, NFitness)) {
        sin_cambios++;
      }
      else {
        sin_cambios = 0;
        for (int i = 0; i < 3; i++) {
          valor_anterior[i] = valor[i];
        }

      }

    }

    // To obtain the best rule of each class

    genetcode code = new genetcode();
    G.Code(0, code);

    ArrayList<double[]> lista1 = new ArrayList<double[]> (1);
    lista1.add(valor);
    peso = Bondad(G, V, E, lista1, NFitness, 0);
    valor = lista1.get(0);
    R.Add(code, peso);

    return t;
  }

  private boolean Cambios(double[] m_act, double[] m_old, int cols) {
    cols = 3;
    int i = 0;
    while (i < cols && m_act[i] == m_old[i]) {
      i++;
    }

    return (i != cols);
  }

  private void fitness(multiPopulation G, VectorVar V, example_set E) {

    int n = G.N_individuals();
    int n_fitnes_componentes = G.N_Valoracion();
    double[] valor = new double[n_fitnes_componentes];

    for (int i = 0; i < n; i++) {
      if (G.Modified(i)) {
        ArrayList<double[]> lista1 = new ArrayList<double[]> (1);
        lista1.add(valor);
        Bondad(G, V, E, lista1, n_fitnes_componentes, i);
        valor = lista1.get(0);

        G.Valoration(i, valor);
      }
    }
  }

  private double Bondad(multiPopulation G, VectorVar V, example_set E,
                ArrayList<double[]> milista, int N_valor, int elemento_i) {
    double[] valor = milista.get(0);
    int b = 0, n1 = 0, n2 = 0, r1 = 0, r2 = 0, r;
    char[] nb = new char[G.Pb[0].SizeOfIndividual(elemento_i)];
    int[] nn1 = new int[G.Pe[0].SizeOfIndividual(elemento_i)];
    int[] nn2 = new int[G.Pe[1].SizeOfIndividual(elemento_i)];
    double[] nr1 = new double[G.Pr[0].SizeOfIndividual(elemento_i)];
    double[] nr2 = new double[G.Pr[1].SizeOfIndividual(elemento_i)];
    String regla;
    int var1, var2;
    int ne = E.N_Examples();
    vectordouble w = new vectordouble(V.N_Antecedents());
    double positivos = 0, negativos = 0, rango;
    int j;
    double aciertos = 0, fallos = 0;

    ArrayList<Integer> lista1 = new ArrayList<Integer> (1);
    Integer aux1 = new Integer(b);
    lista1.add(aux1);
    nb = G.BinarySubpopulation(0, elemento_i, lista1);
    aux1 = lista1.get(0);
    b = aux1.intValue();

    char[] s = new char[b + 1];
    valor[2] = 0; //numbers of ones in the chromosome
    for (j = 0; j < b; j++) {
      s[j] = nb[j];
      if (nb[j] == '1') {
        valor[2]++;
      }
      s[b] = '\0';
    }

    regla = String.valueOf(s);

    ArrayList<Integer> lista2 = new ArrayList<Integer> (1);
    Integer aux2 = new Integer(n1);
    lista2.add(aux1);
    nn1 = G.IntSubpopulation(0, elemento_i, lista2);
    aux2 = lista2.get(0);
    n1 = aux2.intValue();

    ArrayList<Integer> lista3 = new ArrayList<Integer> (1);
    Integer aux3 = new Integer(n2);
    lista3.add(aux3);
    nn2 = G.IntSubpopulation(1, elemento_i, lista3);
    aux3 = lista3.get(0);
    n2 = aux3.intValue();

    ArrayList<Integer> lista4 = new ArrayList<Integer> (1);
    Integer aux4 = new Integer(r1);
    lista4.add(aux4);
    nr1 = G.RealSubpopulation(0, elemento_i, lista4);
    aux4 = lista4.get(0);
    r1 = aux4.intValue();

    ArrayList<Integer> lista5 = new ArrayList<Integer> (1);
    Integer aux5 = new Integer(r2);
    lista5.add(aux5);
    nr2 = G.RealSubpopulation(1, elemento_i, lista5);
    aux5 = lista5.get(0);
    r2 = aux5.intValue();

    valor[1] = 0; //number of irrelevant variables
    for (j = 0; j < r1 - 1; j++) {
      if (nr1[j] < nr1[r1 - 1]) {
        valor[1]++;
      }
    }

    //double *aux = new double[ne];
    double[] aux_p = new double[ne];
    double[] aux_n = new double[ne];
    double peso, kk = 0;
    // We check if it is a valid rule
    boolean esta_cubierto;

    ArrayList<Double> lista6 = new ArrayList<Double> (1);
    Double aux6 = new Double(kk);
    lista6.add(aux6);

    boolean regla_valida = V.Is_Valid(regla, nr1, nr1[r1 - 1], lista6);

    aux6 = lista6.get(0);
    kk = aux6.doubleValue();

    // Obtaining the weight of the rule
    if (regla_valida) {
      for (j = 0; j < ne; j++) {
        w = E.Data(j);

        ArrayList<double[]> lista7 = new ArrayList<double[]> (1);
        double[] list = new double[2];
        list[0] = aux_p[j];
        list[1] = aux_n[j];
        lista7.add(list);

        V.AdaptationC(w, nn1[0], lista7);

        list = lista7.get(0);
        aux_p[j] = list[0];
        aux_n[j] = list[1];

        esta_cubierto = E.Is_Covered(j);
        //esta_cubierto = false;
        if (!esta_cubierto || (esta_cubierto && aux_n[j] > 0)) {
          nr2[j] = V.Adaptation(w, regla, nr1, nr1[r1 - 1]);
          //nr2[j]=V.Adaptation(w,regla,nr1,nr1[r1-1],E.CoverageDegree(j));  //Adaptation con umbral2
          positivos = positivos + (nr2[j] * aux_p[j]);
          negativos = negativos + (nr2[j] * aux_n[j]);
          if (aux_p[j] < aux_n[j]) {
            nr2[j] = -nr2[j];
          }
        }
        else {
          nr2[j] = V.Adaptation(w, regla, nr1, nr1[r1 - 1],
                                E.CoverageDegree(j));
        }
      }
    }
    if (usarPeso){
      peso = (positivos + 1) / (positivos + negativos + 1);
    }else{
      peso = 1.0;
    }

    // Obtaining the goodness of the rule.
    if (regla_valida) {
      positivos = 0;
      negativos = 0;
      for (j = 0; j < ne; j++) {
        esta_cubierto = E.Is_Covered(j);
        if (!esta_cubierto) {
          if (nr2[j] > 0) {
            if (nr2[j] * peso > -E.CoverageDegree(j)) {
              positivos = positivos + (nr2[j] * aux_p[j]);
              //positivos=positivos+(1.0*aux_p[j]);
              aciertos++;
            }
          }
          else if (nr2[j] < 0 &&
                   ( -nr2[j]) * peso > -E.CoverageDegree(j)) {
            negativos = negativos + ( -nr2[j] * aux_n[j]);
            //if (aux[j]*aux_n[j]>0)
            //negativos=negativos+(1.0*aux_n[j]);
            fallos++;
          }
        }
        else {
          if (aux_n[j] > 0 &&
              ( -nr2[j]) * peso > E.CoverageDegree(j)) {
            negativos = negativos + ( -nr2[j] * aux_n[j]);
            //negativos=negativos+(1.0*aux_n[j]);
            fallos++;
          }
          //else
          //nr2[j]=E.Grade_is_Covered(j)+100;
        }

      }
    }
    // The rule is invalid
    else {
      for (j = 0; j < ne; j++) {
        nr2[j] = 0;
      }

    }

    valor[0] = (positivos - negativos);
    //valor[0]=(aciertos-fallos);    //Lo he modificado
    if (valor[0] == 0 && negativos == 0) {
      valor[0] -= ne;
    }

// if (N_valor>=5){
    //valor[3]=valor[2];
    //valor[4]=aciertos;
//   valor[2]=peso;
    //valor[4]=-fallos;
// }
// No hay que liberar los valores int *nb, *nn, **nnn, *nr, char **mb, double **mr

// Calculo de la dominancia
// nn2[0]=CalculoDominancia(elemento_i, G, ind_clase);

// cout << "Ind " <<elemento_i << ":" <<regla << "["<< valor[0]<<","<< valor[1]<<"]" << endl;


//  return (positivos+1) / (positivos+negativos+1);
    //cout << regla << "  "<< valor[0] << " "<<valor[1]<< "  "<<valor[2]<<endl;

    milista.add(0, valor);
    return peso;
  }

  private void MedidasDeInformacion(VectorVar V, example_set E,
                            ArrayList<double[][]> milista) {
    int n = V.TotalVariables(), conse = V.Consequent();
    int ne;

    double[] px;
    double[] py;
    double[][] pxy;
    double Inf, H;

    int nv = V.N_Antecedents();

    double[][] I = milista.get(0);

    //py = new double[conse];
    py = new double[V.SizeDomain(conse)];
    ArrayList<double[]> lista1 = new ArrayList<double[]> (1);
    lista1.add(py);

    CalcularProbabilidadSimpleVariable(V, E, conse, V.SizeDomain(conse),
                                       lista1);

    py = lista1.get(0);

    int i = 0, j = 0;
    double I1 = 0, H1 = 0, I2 = 0, H2 = 0, aux;
    while (i < nv) {
      if (V.IsActive(j) && V.IsAntecedent(j)) {
        px = new double[V.SizeDomain(j)];
        pxy = new double[V.SizeDomain(j)][];
        for (int k = 0; k < V.SizeDomain(j); k++) {
          pxy[k] = new double[V.SizeDomain(conse)];
        }

        ArrayList<double[]> lista2 = new ArrayList<double[]> (1);
        lista2.add(px);

        CalcularProbabilidadSimpleVariable(V, E, j, V.SizeDomain(j),
                                           lista2);

        px = lista2.get(0);

        ArrayList<double[][]> lista3 = new ArrayList<double[][]> (1);
        lista3.add(pxy);

        CalcularProbabilidadConjunta2Variables(V, E, j, conse,
                                               V.SizeDomain(j),
                                               V.SizeDomain(conse), lista3);

        pxy = lista3.get(0);

        I1 = 0;
        H1 = 0;
        for (int k = 0; k < V.SizeDomain(conse); k++) {
          I2 = 0;
          H2 = 0;
          for (int q = 0; q < V.SizeDomain(j); q++) {
            if (pxy[q][k] == 0) {
              aux = 0;
            }
            else {
              //aux=(pxy[q][k] * log(pxy[q][k]/(px[q]*py[k])));
              aux = pxy[q][k] *
                  Math.log( (px[q] * py[k]) / pxy[q][k]);
            }

            I2 = I2 - aux;
            I1 = I1 - aux;

            if (pxy[q][k] == 0) {
              aux = 0;
            }
            else {
              aux = pxy[q][k] * Math.log(pxy[q][k]);
            }

            H2 = H2 - aux;
            H1 = H1 - aux;
          }

          if (H2 == 0) {
            I[i][k + 1] = 0;
          }
          else {
            I[i][k + 1] = (I2 / H2);
          }
        }
        if (H1 == 0) {
          I[i][0] = 0;
        }
        else {
          I[i][0] = (I1 / H1);
        }

        /*V.PrintVar(j);
         cout << ": " << I1 << "/" << H1 << ": " << I[i][0] << endl;
                   for (int k=0; k<V.SizeDomain(conse);k++){
          cout << "\tEtiqueta "<< k <<": " << I[i][k+1] << endl;
                   }*/

        i++;
      }
      j++;
    }

    milista.add(0, I);
  }

  private void CalcularProbabilidadSimpleVariable(VectorVar V, example_set E,
                                          int variable, int n_casos,
                                          ArrayList<double[]> milista) {
    double[] p; // = new double[n_casos];
    p = milista.get(0);

    /*for (int i = 0; i < p.length; i++){
      System.err.println("Mira -> "+p[i]);
             }*/
    //n_casos = variable; //para que no pete...

    int ne = 0;
    double suma = 0;
    double[] aux = new double[n_casos];

    for (int j = 0; j < n_casos; j++) {
      p[j] = 0;
    }

    for (int i = 0; i < E.N_Examples(); i++) {
      if (!E.Is_Covered(i)) {
        suma = 0;
        for (int j = 0; j < n_casos; j++) {
          aux[j] = V.Domain(variable).Adaptation(E.Data(i, variable),
                                                 j);
          suma += aux[j];
        }
        // Add the values to the sum
        if (suma > 0) {
          for (int j = 0; j < n_casos; j++) {
            p[j] = p[j] + (aux[j] / suma);
          }

          ne++;
        }
      }
    }

    if (ne > 0) {
      for (int j = 0; j < n_casos; j++) {
        p[j] = p[j] / ne;
      }
    }

    milista.add(0, p);
  }

  private void CalcularProbabilidadConjunta2Variables(VectorVar V, example_set E,
                                              int var1, int var2,
                                              int n_casos1, int n_casos2,
                                              ArrayList<double[][]>
                                              milista) {
    double[][] m = new double[n_casos1][];
    for (int j = 0; j < n_casos1; j++) {
      m[j] = new double[n_casos2];
    }
    m = milista.get(0);

    int ne = 0;
    double suma;

    double[][] aux = new double[n_casos1][];
    for (int j = 0; j < n_casos1; j++) {
      aux[j] = new double[n_casos2];
    }

    for (int j = 0; j < n_casos1; j++) {
      for (int k = 0; k < n_casos2; k++) {
        m[j][k] = 0;
      }
    }

    for (int i = 0; i < E.N_Examples(); i++) {
      if (!E.Is_Covered(i)) {
        suma = 0;
        for (int j = 0; j < n_casos1; j++) {
          for (int k = 0; k < n_casos2; k++) {
            aux[j][k] = V.Domain(var1).Adaptation(E.Data(i, var1),
                                                  j) *
                V.Domain(var2).Adaptation(E.Data(i, var2), k);
            suma = suma + aux[j][k];
          }
        }

        if (suma > 0) {
          for (int j = 0; j < n_casos1; j++) {
            for (int k = 0; k < n_casos2; k++) {
              m[j][k] = m[j][k] + (aux[j][k] / suma);
            }
          }

          ne++;
        }
      }

    }

    if (ne > 0) {
      for (int j = 0; j < n_casos1; j++) {
        for (int k = 0; k < n_casos2; k++) {
          m[j][k] = m[j][k] / ne;
        }
      }
    }

    milista.add(m);
  }

  // Retuns a matrix with nv rows (n. of antecedent variables) and
// m columns (number of clases of the consequent +1). Position 0
// keeps the general measure about all the classes.
  private double[][] ReservaParaMedidasDeInformacion(VectorVar V) {
    int n = V.TotalVariables(), conse = V.Consequent();
    int tam_dom_conse = V.SizeDomain(conse);
    int nv, m = tam_dom_conse + 1;

    nv = V.N_Antecedents();

    double[][] matriz = new double[nv][];
    for (int i = 0; i < nv; i++) {
      matriz[i] = new double[m];
    }

    return matriz;
  }

  /**
   * It generates the output file from a given dataset and stores it in a file
   */
  private void doOutput(ruleset R, VectorVar V, example_set E,
                        myDataset dataset, String filename) {
    String output = new String("");
    output = dataset.copyHeader(); //we insert the header in the output file
    //We write the output for each example
    for (int i = 0; i < dataset.getnData(); i++) {
      //for classification:
      output += dataset.getOutputAsString(i) + " " +
          this.classificationOutput(R, V, E, i, dataset) + "\n";
    }
    Files.writeFile(filename, output);
  }

  /**
   * It returns the algorithm classification output given an input example
   * @return String the output generated by the algorithm
   */
  private String classificationOutput(ruleset R, VectorVar V, example_set E,
                                      int ejemplo, myDataset dataset) {
    int clase = -2;
    String output = new String("");

    clase = Clasificacion(R, V, E, ejemplo);
    if (clase == -1) {
      output = "?";
    }
    else {
      output = dataset.getOutputValue(clase);
    }

    return output;
  }

}

