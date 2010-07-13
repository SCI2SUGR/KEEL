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

package keel.Algorithms.Fuzzy_Rule_Learning.AdHoc.Fuzzy_Ish_Weighted;

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
  String outputTr, outputTst, dbFile, brFile;
  int nClasses, combinationType, inferenceType, ruleWeight, nLabels, cost, learn, epochs;
  double nu;
  BaseD dataBase;
  BaseR ruleBase;

  public static final int MINIMUM = 0;
  public static final int PRODUCT = 1;
  public static final int CF = 0;
  public static final int PCF_IV = 1;
  public static final int MCF = 2;
  public static final int PCF_II = 3;
  public static final int WINNING_RULE = 0;
  public static final int ADDITIVE = 1;
  public static final int NONE = 0;
  public static final int PROPORTIONAL = 1;
  public static final int HALF = 2;
  public static final int YES = 0;
  public static final int NO = 1;

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

    dbFile = parameters.getOutputFile(0);
    brFile = parameters.getOutputFile(1);

    //Now we parse the parameters
    combinationType = PRODUCT;
    ruleWeight = PCF_II;
    inferenceType = WINNING_RULE;

    nLabels = Integer.parseInt(parameters.getParameter(0));

    String cost_aux = parameters.getParameter(1);
    cost = PROPORTIONAL;
    if (cost_aux.equalsIgnoreCase("NONE")){
      cost = NONE;
    }else if (cost_aux.equalsIgnoreCase("HALF_MINORITY")){
      cost = HALF;
    }
    learn = YES;
    String learn_aux = parameters.getParameter(2);
    if (learn_aux.equalsIgnoreCase("NO")){
      learn = NO;
    }
    nu = Double.parseDouble(parameters.getParameter(3));
    epochs = Integer.parseInt(parameters.getParameter(4));

    train.computeWeights(cost);

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

      dataBase = new BaseD(train.getnInputs(), nLabels, train.devuelveRangos(), train.nombres());
      ruleBase = new BaseR(dataBase, train, inferenceType, combinationType, ruleWeight);
      ruleBase.Generacion();
      if (this.learn == this.YES){
        ruleBase.learnWeights(nu,epochs);
      }

      dataBase.escribeFichero(this.dbFile);
      ruleBase.escribeFichero(this.brFile);

      //Finally we should fill the training and test output files
      double acc_train = doOutput(this.val, this.outputTr);
      double acc_test = doOutput(this.test, this.outputTst);

      System.out.println("Accuracy in training: "+acc_train);
      System.out.println("Accuracy in test: "+acc_test);
      System.out.println("Algorithm Finished");
    }
  }

  /**
   * It generates the output file from a given dataset and stores it in a file
   * @param dataset myDataset input dataset
   * @param filename String the name of the file
   * @return the classifier accuracy
   */
  private double doOutput(myDataset dataset, String filename) {
    String output = new String("");
    output = dataset.copyHeader(); //we insert the header in the output file
    int hits = 0;
    //We write the output for each example
    for (int i = 0; i < dataset.getnData(); i++) {
      //for classification:
      String clas = this.classificationOutput(dataset.getExample(i));
      output += dataset.getOutputAsString(i) + " " + clas + "\n";
      if (dataset.getOutputAsString(i).equalsIgnoreCase(clas)){
        hits++;
      }
    }
    Fichero.escribeFichero(filename, output);
    return (1.0*hits/dataset.size());
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
    int clase = ruleBase.FRM(example);
    if (clase >= 0) {
      output = train.getOutputValue(clase);
    }
    return output;
  }

}

