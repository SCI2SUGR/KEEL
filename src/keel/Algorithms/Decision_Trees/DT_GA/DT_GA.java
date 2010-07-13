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
 * <p>Title: Algorithm</p>
 *
 * <p>Description: It contains the implementation of the algorithm</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Alberto Fernández
 * @version 1.0
 */

import java.io.IOException;
import org.core.*;
import java.util.Arrays;
import keel.Algorithms.Decision_Trees.DT_GA.C45.C45;
import java.util.StringTokenizer;
import java.util.Vector;

public class DT_GA {

  myDataset train, val, test;
  String outputTr, outputTst, ficheroBR, claseMayoritaria;
  int nClasses, nGenerations, popSize, instancesPerLeaf, type, S;
  double crossProb, mutProb;
  float confidence;
  boolean pruned;
  String fichTrain;
  BaseR baseReglasTree, baseReglasGA;
  Clasificador clasif;

  public static int GA_SMALL = 0;
  public static int GA_LARGE_SN = 1;

  private boolean somethingWrong = false; //to check if everything is correct.

  /**
   * Default constructor
   */
  public DT_GA() {
  }

  /**
   * It reads the data from the input files (training, validation and test) and parse all the parameters
   * from the parameters array.
   * @param parameters parseParameters It contains the input files, output files and parameters
   */
  public DT_GA(parseParameters parameters) {

    train = new myDataset();
    val = new myDataset();
    test = new myDataset();
    fichTrain = parameters.getTrainingInputFile();
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
    //somethingWrong = somethingWrong || train.hasRealAttributes();
    somethingWrong = somethingWrong || train.hasMissingAttributes();

    outputTr = parameters.getTrainingOutputFile();
    outputTst = parameters.getTestOutputFile();

    ficheroBR = parameters.getOutputFile(0);

    //Now we parse the parameters
    long semilla = Long.parseLong(parameters.getParameter(0));
    String aux = parameters.getParameter(1);
    pruned = true;
    /*if (aux.compareToIgnoreCase("FALSE") == 0) {
      pruned = false;
         }*/
    confidence = Float.parseFloat(parameters.getParameter(1));
    instancesPerLeaf = Integer.parseInt(parameters.getParameter(2));
    aux = parameters.getParameter(3);
    type = this.GA_SMALL;
    if (aux.compareToIgnoreCase("GA-LARGE-SN") == 0) {
      type = this.GA_LARGE_SN;
    }
    S = Integer.parseInt(parameters.getParameter(4));
    nGenerations = Integer.parseInt(parameters.getParameter(5));
    popSize = Integer.parseInt(parameters.getParameter(6));
    while (popSize % 2 != 0) {
      popSize++;
    }
    crossProb = Double.parseDouble(parameters.getParameter(7));
    mutProb = Double.parseDouble(parameters.getParameter(8));

    Randomize.setSeed(semilla);

  }

  /**
   * It launches the algorithm
   */
  public void execute() {
    if (somethingWrong) { //We do not execute the program
      System.err.println("An error was found, the data-set has missing values.");
      System.err.println("Aborting the program");
      //We should not use the statement: System.exit(-1);
    }
    else {
      //We do here the algorithm's operations

      nClasses = train.getnClasses();
      C45 arbol = new C45(fichTrain, pruned, confidence, instancesPerLeaf);
      try {
        arbol.generateTree();
      }
      catch (Exception e) {
        System.err.println(e.getMessage());
        System.exit( -1);
      }
      //System.err.println("Mira -> \n"+arbol.printString());
      Fichero.escribeFichero("arbol.txt", arbol.printString());
      String cadenaArbol = arbol.printString();
      obtenerReglas(cadenaArbol);
      baseReglasTree.cubrirEjemplos();
      System.out.println(baseReglasTree.printString());
      baseReglasGA = baseReglasTree.genetico(type, S, nGenerations, popSize,
                                             crossProb, mutProb);

      claseMayoritaria = train.claseMasFrecuente();
      clasif = new Clasificador(baseReglasTree,baseReglasGA,type,S,claseMayoritaria);
      //Finally we should fill the training and test output files
      double accTr = doOutput(this.val, this.outputTr);
      double accTst = doOutput(this.test, this.outputTst);

      escribeSalidas(accTr,accTst);
    }
  }

  /**
   * It generates the output file from a given dataset and stores it in a file
   * @param dataset myDataset input dataset
   * @param filename String the name of the file
   * @return the Accuracy of the classifier
   */
  private double doOutput(myDataset dataset, String filename) {
    String output = new String("");
    output = dataset.copyHeader(); //we insert the header in the output file
    int aciertos = 0;
    //We write the output for each example
    for (int i = 0; i < dataset.getnData(); i++) {
      //for classification:
      String claseReal = dataset.getOutputAsString(i);
      String prediccion = this.classificationOutput(dataset.getExample(i));
      output += claseReal + " " + prediccion + "\n";
      if (claseReal.equalsIgnoreCase(prediccion)) {
        aciertos++;
      }
    }
    Fichero.escribeFichero(filename, output);
    return (1.0 * aciertos / dataset.size());
  }

  /**
   * It returns the algorithm classification output given an input example
   * @param example double[] The input example
   * @return String the output generated by the algorithm
   */
  private String classificationOutput(double[] example) {
    /*StringBuffer clase = new StringBuffer("");
    boolean smallDisjunct = baseReglasTree.clasifica(true, example, clase); //en clase guardo la clase y devuelvo si es SD
    if (smallDisjunct) {
      StringBuffer claseGA = new StringBuffer("");
      baseReglasGA.clasifica(false, example, claseGA);
      if (!(claseGA.toString().equalsIgnoreCase("<unclassified>"))) {
        clase = claseGA;
      }
      //System.err.println("Mira -> "+claseGA);
    }
    return clase.toString();*/
    return clasif.clasifica(example);
  }

  private void obtenerReglas(String cadenaArbol) {
    String reglas = new String("");
    StringTokenizer lineas = new StringTokenizer(cadenaArbol, "\n"); //este lee lineas
    String linea = lineas.nextToken(); //Primera linea @TotalNumberOfNodes X
    linea = lineas.nextToken(); //Segunda linea @NumberOfLeafs Y
    //Empieza el arbol
    Vector variables = new Vector();
    Vector valores = new Vector();
    Vector operadores = new Vector();
    int contador = 0;
    while (lineas.hasMoreTokens()) {
      linea = lineas.nextToken();
      StringTokenizer campo = new StringTokenizer(linea, " \t");
      String cosa = campo.nextToken(); //Posibilidades: "if", "elseif", "class"
      if (cosa.compareToIgnoreCase("if") == 0) {
        campo.nextToken(); //(
        variables.add(campo.nextToken()); //nombre de la variable (AttX, X == posicion)
        operadores.add(campo.nextToken()); //Una de tres: "=", "<=", ">"
        valores.add(campo.nextToken()); //Valor
      }
      else if (cosa.compareToIgnoreCase("elseif") == 0) {
        int dejar = Integer.parseInt(campo.nextToken());
        for (int i = variables.size() - 1; i >= dejar; i--) {
          variables.remove(variables.size() - 1);
          operadores.remove(operadores.size() - 1);
          valores.remove(valores.size() - 1);
        }
        campo.nextToken(); //(
        variables.add(campo.nextToken()); //nombre de la variable (AttX, X == posicion)
        operadores.add(campo.nextToken()); //Una de tres: "=", "<=", ">"
        valores.add(campo.nextToken()); //Valor
      }
      else { //Clase --> genero la regla
        campo.nextToken(); // =
        contador++; //tengo una nueva regla
        reglas += "\nRULE-" + contador + ": IF ";
        int i;
        for (i = 0; i < variables.size() - 1; i++) {
          reglas += (String) variables.get(i) + " " + (String) operadores.get(i) +
              " " + (String) valores.get(i) + " AND ";
        }
        reglas += (String) variables.get(i) + " " + (String) operadores.get(i) +
            " " + (String) valores.get(i);
        reglas += " THEN class = " + campo.nextToken();
        variables.remove(variables.size() - 1);
        operadores.remove(operadores.size() - 1);
        valores.remove(valores.size() - 1);
      }
      //System.err.println(reglas);
    }
    //Fichero.escribeFichero("reglas.txt", reglas);
    baseReglasTree = new BaseR(train, reglas);
  }

  public void escribeSalidas(double accTr, double accTst){
    System.out.println("Number of Rules (Tree): " + baseReglasTree.size());
    System.out.println(""+baseReglasTree.printString());
    System.out.println("Number of Rules (GA): " + baseReglasGA.size());
    System.out.println(""+baseReglasGA.printString());
    System.out.println("Accuracy in training: " + accTr);
    System.out.println("Accuracy in test: " + accTst);
    System.out.println("Algorithm Finished");
    Fichero.escribeFichero(ficheroBR,baseReglasTree.printString()+ "\n");
    Fichero.AnadirtoFichero(ficheroBR,baseReglasGA.printString()+ "\n");
    Fichero.AnadirtoFichero(ficheroBR,"Accuracy in training: " + accTr+ "\n");
    Fichero.AnadirtoFichero(ficheroBR,"Accuracy in test: " + accTst+ "\n");
  }
}

