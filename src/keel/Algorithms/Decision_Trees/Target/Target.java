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

public class Target {

  myDataset train, val, test;
  String outputTr, outputTst, ficheroTree, claseMayoritaria;
  int nClasses, nGenerations;
  int nCross,nMut,nClone,nImmigration,nTrees;
  String fichTrain;
  double pSplit;
  Tree mejorArbol;

  private boolean somethingWrong = false; //to check if everything is correct.

  /**
   * Default constructor
   */
  public Target() {
  }

  /**
   * It reads the data from the input files (training, validation and test) and parse all the parameters
   * from the parameters array.
   * @param parameters parseParameters It contains the input files, output files and parameters
   */
  public Target(parseParameters parameters) {

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

    ficheroTree = parameters.getOutputFile(0);

    //Now we parse the parameters
    long semilla = Long.parseLong(parameters.getParameter(0));
    pSplit = Double.parseDouble(parameters.getParameter(1));
    nGenerations = Integer.parseInt(parameters.getParameter(2));
    nCross = Integer.parseInt(parameters.getParameter(3));
    /*while (nCross % 2 != 0) {
      nCross++;
    }*/
    nMut = Integer.parseInt(parameters.getParameter(4));
    /*while (nMut % 2 != 0) {
      nMut++;
    }*/
    nClone = Integer.parseInt(parameters.getParameter(5));
    /*while (nClone % 2 != 0) {
      nClone++;
    }*/
    nImmigration = Integer.parseInt(parameters.getParameter(6));
    /*while (nImmigration % 2 != 0) {
      nImmigration++;
    }*/
    nTrees = nCross+nMut+nClone+nImmigration;

    Randomize.setSeed(semilla);

  }

  /**
   * It launches the algorithm
   */
  public void execute() {
    if (somethingWrong) { //We do not execute the program
      System.err.println("An error was found, either the data-set have numerical values or missing values.");
      System.err.println("Aborting the program");
      //We should not use the statement: System.exit(-1);
    }
    else {
      //We do here the algorithm's operations

      nClasses = train.getnClasses();
      Poblacion p = new Poblacion(train,pSplit,nGenerations,nCross,nMut,nClone,nImmigration);
      p.hacerGenetico();
      mejorArbol = p.mejorSolucion();

      //Finally we should fill the training and test output files
      double accTr = doOutput(this.val, this.outputTr);
      double accTst = doOutput(this.test, this.outputTst);

      Fichero.escribeFichero(ficheroTree,mejorArbol.printString());
      System.out.println("Tree obtained: "+mejorArbol.printString());
      System.out.println("Accuracy in training: " + accTr);
      System.out.println("Accuracy in test: " + accTst);
      System.out.println("Algorithm Finished");
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
    return mejorArbol.clasificar(example);
    //return train.claseMasFrecuente();
  }

}

