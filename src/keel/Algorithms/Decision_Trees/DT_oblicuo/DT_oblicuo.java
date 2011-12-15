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

import java.io.IOException;
import org.core.*;


/**
 * <p>It contains the implementation of the algorithm</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Alberto Fernández
 * @version 1.0
 */
public class DT_oblicuo {

  myDataset train, val, test;
  String outputTr, outputTst, ficheroTree, claseMayoritaria;
  int nClasses, nGenerations;
  String fichTrain;
  Tree arbol;

  private boolean somethingWrong = false; //to check if everything is correct.

  /**
   * Default constructor
   */
  public DT_oblicuo() {
  }

  /**
   * It reads the data from the input files (training, validation and test) and parse all the parameters
   * from the parameters array.
   * @param parameters parseParameters It contains the input files, output files and parameters
   */
  public DT_oblicuo(parseParameters parameters) {

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
    //somethingWrong = somethingWrong || train.hasNominalAttributes();
    somethingWrong = somethingWrong || train.hasMissingAttributes();

    outputTr = parameters.getTrainingOutputFile();
    outputTst = parameters.getTestOutputFile();

    ficheroTree = parameters.getOutputFile(0);

    //Now we parse the parameters
    long semilla = Long.parseLong(parameters.getParameter(0));
    nGenerations = Integer.parseInt(parameters.getParameter(1));
    /*
         nCross = Integer.parseInt(parameters.getParameter(3));
         nMut = Integer.parseInt(parameters.getParameter(4));
         nClone = Integer.parseInt(parameters.getParameter(5));
         nImmigration = Integer.parseInt(parameters.getParameter(6));
     */

    Randomize.setSeed(semilla);

  }

  /**
   * It launches the algorithm
   */
  public void execute() {
    if (somethingWrong) { //We do not execute the program
      System.err.println("An error was found: the data-set has missing values.");
      System.err.println("Please remove the examples with MV or apply a preprocessing step");
      System.err.println("Aborting the program");
      //We should not use the statement: System.exit(-1);
    }
    else {
      //We do here the algorithm's operations

      //nClasses = train.getnClasses();
      int[] ejemplos = new int[train.size()];
      for (int i = 0; i < ejemplos.length; i++) {
        ejemplos[i] = i;
      }
      arbol = new Tree(null, train, train.size(), ejemplos, nGenerations);

      //Finally we should fill the training and test output files
      double accTr = doOutput(this.val, this.outputTr);
      double accTst = doOutput(this.test, this.outputTst);

      escribeResultados(accTr,accTst);
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
    //return train.claseMasFrecuente();
    return arbol.clasificar(example);
  }

  private void escribeResultados(double accTr, double accTst){
    //System.out.println(arbol.printString());
    System.out.println("Accuracy in training: " + accTr);
    System.out.println("Accuracy in test: " + accTst);
    System.out.println("Algorithm Finished");
    Fichero.escribeFichero(ficheroTree,arbol.printString());
    Fichero.AnadirtoFichero(ficheroTree,"\n\nAccuracy in training: " + accTr);
    Fichero.AnadirtoFichero(ficheroTree,"\nAccuracy in test: " + accTst);

  }

}

