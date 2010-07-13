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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Fuzzy_Ish_Selec;

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

public class Fuzzy_Ish {

  myDataset train, val, test;
  String outputTr, outputTst, ficheroBD, ficheroBR;
  int nClasses, tipoCombinacion, tipoInferencia, pesoRegla, nReglas,
      tamPoblacion, nGenerations, L;
  double mutProb, p_include, w_acc, w_size;
  BaseD baseDatos;
  BaseR baseReglas;

  public static final int MINIMO = 0;
  public static final int PRODUCTO = 1;
  public static final int CF = 0;
  public static final int PCF_IV = 1;
  public static final int MCF = 2;
  public static final int PCF_II = 3;
  public static final int CLASICO = 0;
  public static final int SUMA = 1;

  //We may declare here the algorithm's parameters

  private boolean somethingWrong = false; //to check if everything is correct.

  /**
   * Default constructor
   */
  public Fuzzy_Ish() {
  }

  /**
   * It reads the data from the input files (training, validation and test) and parse all the parameters
   * from the parameters array.
   * @param parameters parseParameters It contains the input files, output files and parameters
   */
  public Fuzzy_Ish(parseParameters parameters) {

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
    somethingWrong = somethingWrong || train.hasMissingAttributes();

    outputTr = parameters.getTrainingOutputFile();
    outputTst = parameters.getTestOutputFile();

    ficheroBD = parameters.getOutputFile(0);
    ficheroBR = parameters.getOutputFile(1);

    //Now we parse the parameters
    long semilla = Long.parseLong(parameters.getParameter(0));
    tipoCombinacion = PRODUCTO;
    pesoRegla = PCF_II;
    tipoInferencia = CLASICO;

    L = Integer.parseInt(parameters.getParameter(1));

    w_acc = Double.parseDouble(parameters.getParameter(2));
    w_size = Double.parseDouble(parameters.getParameter(3));
    tamPoblacion = Integer.parseInt(parameters.getParameter(4));
    while ((tamPoblacion % 2) != 0) tamPoblacion++;
    p_include = Double.parseDouble(parameters.getParameter(5));
    mutProb = Double.parseDouble(parameters.getParameter(6));
    nGenerations = Integer.parseInt(parameters.getParameter(7));

    Randomize.setSeed(semilla);

  }

  /**
   * It launches the algorithm
   */
  public void execute() {
    if (somethingWrong) { //We do not execute the program
        System.err.println("An error was found, either the data-set has missing values.");
        System.err.println("Please remove the examples with missing data or apply a MV preprocessing.");
        System.err.println("Aborting the program");
      //We should not use the statement: System.exit(-1);
    }
    else {
      //We do here the algorithm's operations

      nClasses = train.getnClasses();

      baseDatos = new BaseD(L, train.getnInputs(), train.devuelveRangos(), train.nombres());
      baseReglas = new BaseR(baseDatos, train, tipoInferencia, tipoCombinacion, pesoRegla);
      baseReglas.Generacion();

      Poblacion pobl = new Poblacion(baseReglas,w_acc,w_size,tamPoblacion,p_include,mutProb);
      pobl.Generacion(this.nGenerations);

      baseDatos.escribeFichero(this.ficheroBD);
      baseReglas = pobl.mejorBR();
      baseReglas.escribeFichero(this.ficheroBR);

      //Finally we should fill the training and test output files
      doOutput(this.val, this.outputTr);
      doOutput(this.test, this.outputTst);

      System.out.println("Algorithm Finished");
    }
  }

  /**
   * It generates the output file from a given dataset and stores it in a file
   * @param dataset myDataset input dataset
   * @param filename String the name of the file
   */
  private void doOutput(myDataset dataset, String filename) {
    String output = new String("");
    output = dataset.copyHeader(); //we insert the header in the output file
    //We write the output for each example
    for (int i = 0; i < dataset.getnData(); i++) {
      //for classification:
      output += dataset.getOutputAsString(i) + " " +
          this.classificationOutput(dataset.getExample(i)) + "\n";
    }
    Fichero.escribeFichero(filename, output);
  }

  /**
   * It returns the algorithm classification output given an input example
   * @param example double[] The input example
   * @return String the output generated by the algorithm
   */
  private String classificationOutput(double[] example) {
    String output = new String("?");
    /**
      Here we should include the algorithm directives to generate the
      classification output from the input example
     */
    int clase = baseReglas.FRM(example);
    if (clase >= 0) {
      output = train.getOutputValue(clase);
    }
    return output;
  }

}

