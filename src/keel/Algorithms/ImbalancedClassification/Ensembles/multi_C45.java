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

package keel.Algorithms.ImbalancedClassification.Ensembles;

/**
 * <p>Title: Algorithm</p>
 *
 * <p>Description: It contains the implementation of the algorithm</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Alberto Fern�ndez
 * @version 1.0
 */

import java.io.IOException;
import org.core.*;
import keel.Algorithms.ImbalancedClassification.Ensembles.C45.C45;
import java.util.StringTokenizer;
import java.util.Vector;

public class multi_C45 {

  parseParameters parameters;
  myDataset train, val, test;
  String outputTr, outputTst, ficheroBR;
  int instancesPerLeaf, n_classifiers;
  float confidence;
  boolean pruned, valid[];
  String fichTrain;
  BaseR[] baseReglasTree;           // Trees of the ensemble
  myDataset actua_train_set;        // train data-set for the actual ensemble
  Ensemble ensemble;                
  String ensembleType;


  private boolean somethingWrong = false; //to check if everything is correct.

  /**
   * Default constructor
   */
  public multi_C45() {
  }

  /**
   * It reads the data from the input files (training, validation and test) and parse all the parameters
   * from the parameters array.
   * @param parameters parseParameters It contains the input files, output files and parameters
   */
  public multi_C45(parseParameters parameters) {

    this.parameters = parameters;
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

    outputTr = parameters.getTrainingOutputFile();
    outputTst = parameters.getTestOutputFile();

    ficheroBR = parameters.getOutputFile(0);

    //Now we parse the parameters
    pruned = parameters.getParameter(1).equalsIgnoreCase("TRUE");
    confidence = Float.parseFloat(parameters.getParameter(2));
    instancesPerLeaf = Integer.parseInt(parameters.getParameter(3));
    n_classifiers = Integer.parseInt(parameters.getParameter(4));
    ensembleType = parameters.getParameter(5);
    
    System.out.println("go");
    /* Create the ensemble! */
    ensemble = new Ensemble(ensembleType, train, n_classifiers, this);
    System.out.println("gone");
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

      n_classifiers = ensemble.nClassifier;
      valid = new boolean[n_classifiers];
      baseReglasTree = new BaseR[n_classifiers];

      /** While the algorithm has not end, and the number of classifier constructed is not reached... 
       * we construct a new classifier for the ensemble
       */
      boolean fin = false;
      for (int i = 0; i < n_classifiers && !fin; i++) {

          // we get the actual training data-set
        actua_train_set = ensemble.getDS();

        /* Databoost-IM has problems generating instances in Highly imbalanced data-sets */
        if (actua_train_set.getnData() > 10000)
        {
           System.out.println("Databoost overflow!, nData = " + actua_train_set.getnData());
           fin = true;
           break;
        }
        if (!actua_train_set.vacio())
        {
            // write the data-set which will be readed by C4.5 decision tree learning algorithm
             Fichero.escribeFichero(ensembleType + "training.txt", actua_train_set.printDataSet());
             valid[i] = true;
             System.out.println("Training classifier[" + i + "]");
             // Construct the tree using the weights (they can be unirformly distributed)
             C45 arbol = new C45(ensembleType + "training.txt", pruned, confidence, instancesPerLeaf, ensemble.getWeights().clone());
             try {
               arbol.generateTree();
             }
             catch (Exception e) {
               System.err.println("Error!!");
               System.err.println(e.getMessage());
               System.exit( -1);
             }
             /* The tree is stored in a set of rules */
             Fichero.escribeFichero("arbol.txt", arbol.printString());
             String cadenaArbol = arbol.printString();
             obtenerReglas(cadenaArbol, i);
             if (baseReglasTree[i].size() == 0)
             {
                int clase = arbol.getPriorProbabilities()[0] > arbol.getPriorProbabilities()[1] ? 0 : 1;
                // The a priori rule is introduced which predict the class with the greatest prior probability
                baseReglasTree[i].baseReglas.add(new Regla(train.getOutputValue(clase), actua_train_set));
             }

             baseReglasTree[i].cubrirEjemplos();
             baseReglasTree[i].cubrirEjemplos(ensemble.getWeights().clone());            
           }
           else {
             valid[i] = false;
           }
            // Go to the next iteration of the ensemble!
            fin = ensemble.nextIteration();
            if (ensembleType.equalsIgnoreCase("EASYENSEMBLE") 
                    || ensembleType.equalsIgnoreCase("BALANCECASCADE"))
            i = ensemble.t - 1;
        }
      //Finally we should fill the training and test output files
      double accTr = doOutput(this.val, this.outputTr);
      double accTst = doOutput(this.test, this.outputTst);
      escribeSalidas(accTr, accTst);
    }
  }


  /**
   * It generates the output file from a given dataset and stores it in a file
   * @param dataset myDataset input dataset
   * @param filename String the name of the file
   * @return the Accuracy of the classifier
   */
  private double doOutput(myDataset dataset, String filename) {
     double TP = 0, FP = 0, FN = 0, TN = 0;
    String output = new String("");
    output = dataset.copyHeader(); //we insert the header in the output file
    int aciertos = 0;
    //We write the output for each example
    for (int i = 0; i < dataset.getnData(); i++) {
        String claseReal = dataset.getOutputAsString(i);
        String prediccion = this.classificationOutput(dataset.getExample(i));
        output += claseReal + " " + prediccion + "\n";
        if (claseReal.equalsIgnoreCase(prediccion)) {
          aciertos++;
        }

        if (claseReal.equalsIgnoreCase(prediccion) && claseReal.equalsIgnoreCase(train.claseMasFrecuente()))
           TN++;
        else if (claseReal.equalsIgnoreCase(prediccion) && !claseReal.equalsIgnoreCase(train.claseMasFrecuente()))
           TP++;
        else if (!claseReal.equalsIgnoreCase(prediccion) && claseReal.equalsIgnoreCase(train.claseMasFrecuente()))
           FP++;
        else
           FN++;
     }

    double TPrate = TP / (TP + FN);
    double TNrate = TN / (TN + FP);
    double gmean = Math.sqrt(TPrate * TNrate);
    double precision = TP / (TP + FP);
    double recall = TP / (TP + FN);
    double fmean = 2 * recall * precision / (1 * recall + precision);

    System.out.println("G-mean: " + gmean);
    System.out.println("F-mean: " + fmean);
    System.out.println("TPrate: " + TPrate);
    Fichero.escribeFichero(filename, output);
    return (1.0 * aciertos / dataset.size());
  }

    public double prueba(myDataset dataset) {

       double TP = 0, FP = 0, FN = 0, TN = 0;

    //String output = new String("");
    //output = dataset.copyHeader(); //we insert the header in the output file
    int aciertos = 0;
    //We write the output for each example
    for (int i = 0; i < dataset.getnData(); i++) {
        String claseReal = dataset.getOutputAsString(i);
        String prediccion = this.classificationOutput(dataset.getExample(i));
        //output += claseReal + " " + prediccion + "\n";
        if (claseReal.equalsIgnoreCase(prediccion)) {
          aciertos++;
        }

        if (claseReal.equalsIgnoreCase(prediccion) && claseReal.equalsIgnoreCase(train.claseMasFrecuente()))
           TN++;
        else if (claseReal.equalsIgnoreCase(prediccion) && !claseReal.equalsIgnoreCase(train.claseMasFrecuente()))
           TP++;
        else if (!claseReal.equalsIgnoreCase(prediccion) && claseReal.equalsIgnoreCase(train.claseMasFrecuente()))
           FP++;
        else
           FN++;
     }

    double TPrate = TP / (TP + FN);
    double TNrate = TN / (TN + FP);
    double gmean = Math.sqrt(TPrate * TNrate);
    double precision = TP / (TP + FP);
    double recall = TP / (TP + FN);
    double fmean = 2 * recall * precision / (1 * recall + precision);

    System.out.println("G-mean: " + gmean);
    System.out.println("F-mean: " + fmean);
    System.out.println("TPrate: " + TPrate);

    return (1.0 * aciertos / dataset.size());
  }


  /**
   * It returns the algorithm classification output given an input example
   * @param example double[] The input example
   * @return String the output generated by the algorithm
   */
  private String classificationOutput(double[] example) {
    /**
      Here we should include the algorithm directives to generate the
      classification output from the input example
     */
    return ensemble.computeClassScores(example);
  }

  /** It returns the class index of the prediction of an example in the i^{th} classifier
   * 
   * @param i the classifier to be used
   * @param example the example to be classified
   * @return the predicted class index
   */
  protected int obtainClass(int i, double[] example)
  {
      if (valid[i]) {
        String clase = "?";
        for (int j = 0; (j < baseReglasTree[i].size()) && (clase.equals("?"));
             j++) {
          if (baseReglasTree[i].baseReglas.get(j).cubre(example)) {
            clase = baseReglasTree[i].baseReglas.get(j).clase;
          }
        }
        int clase_num = train.claseNumerica(clase);
        if (clase_num == -1)
        {
            // System.out.println("No da la clase!!!!!");
            clase_num = train.claseNumerica(train.claseMasFrecuente());
        }
        return clase_num;
      }
      else
          return -1;
  }

  /** It obtains the confidence on the prediction of the example in the i^{th} classifier
   * 
   * @param i the classifier to be used
   * @param example the example to be classified
   * @return the confidence on the prediction
   */
  protected double obtainConfidence(int i, double[] example)
  {
      double confianza = 0;
      
      if (valid[i]) {
        String clase = "?";
        for (int j = 0; (j < baseReglasTree[i].size()) && (clase.equals("?"));
             j++) {
          if (baseReglasTree[i].baseReglas.get(j).cubre(example)) {
            clase = baseReglasTree[i].baseReglas.get(j).clase;
            double nCubiertosOK = baseReglasTree[i].baseReglas.get(j).fCubiertosOK; //.cubiertosOK();
            double nCubiertos = baseReglasTree[i].baseReglas.get(j).fCubiertos;//.cubiertos();
            if (nCubiertos == 0)
                confianza = 0;
            else
                confianza = (ensemble.nData * nCubiertosOK + 1) / (ensemble.nData * nCubiertos + 2);
          }
        }
        int clase_num = train.claseNumerica(clase);
    
        if (clase_num == -1)
            confianza = 0.5;
        return confianza;
      }
      else
      {
          return 0.5;
      }
  }




  private void obtenerReglas(String cadenaArbol, int classifier) {
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
    baseReglasTree[classifier] = new BaseR(actua_train_set, reglas);
  }

  public void escribeSalidas(double accTr, double accTst) {
    for (int i = 0; i < baseReglasTree.length; i++) {
      //System.out.println("Classifier number " + i + ": ");
      /*if (valid[i]) {
        System.out.println("Number of Rules (Tree): " + baseReglasTree[i].size());
        System.out.println("" + baseReglasTree[i].printString());
      }
      else {
        System.out.println("Not valid!");
      }*/
    }
    
    Fichero.escribeFichero(ficheroBR,"");
    for (int i = 0; i < ensemble.nClassifier; i++) {
      if (valid[i]) {
        Fichero.AnadirtoFichero(ficheroBR, "@Classifier number " + i + ": \n");
        Fichero.AnadirtoFichero(ficheroBR,
                               baseReglasTree[i].printStringF() + "\n");
      }
      else {
        // System.out.println("Not valid!");
      }
    }
    Fichero.AnadirtoFichero(ficheroBR, "Accuracy in training: " + accTr + "\n");
    Fichero.AnadirtoFichero(ficheroBR, "Accuracy in test: " + accTst + "\n");

    System.out.println("Accuracy in training: " + accTr);
    System.out.println("Accuracy in test: " + accTst);
    System.out.println("Algorithm Finished");
  }


}
