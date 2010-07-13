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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Fuzzy_Ish_Hybrid;

/**
 * <p>Title: Fuzzy_Ish</p>
 *
 * <p>Description: It contains the implementation of the FH-GBML algorithm</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Alberto Fernández (University of Granada) 29/10/2007
 * @author Modified by Alberto Fernández (University of Granada) 03/09/2009
 * @version 1.4
 * @since JDK1.5
 */

import java.io.IOException;
import org.core.*;

public class Fuzzy_Ish {

  myDataset train, val, test;
  String outputTr, outputTst, fileDB, fileBR;
  int nClasses, combinationType, inferenceType, ruleWeight, nRules,
      populationSize, nGenerations;
  double crossProb, p_DC, michProb;
  DataBase dataBase;
  RuleBase ruleBase;

  public static final int MINIMUM = 0;
  public static final int PRODUCT = 1;
  public static final int CF = 0;
  public static final int PCF_IV = 1;
  public static final int MCF = 2;
  public static final int PCF_II = 3;
  public static final int WINNING_RULE = 0;
  public static final int ADDITIVE_COMBINATION = 1;

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
    //somethingWrong = somethingWrong || train.hasMissingAttributes();

    outputTr = parameters.getTrainingOutputFile();
    outputTst = parameters.getTestOutputFile();

    fileDB = parameters.getOutputFile(0);
    fileBR = parameters.getOutputFile(1);

    //Now we parse the parameters
    long seed = Long.parseLong(parameters.getParameter(0));
    //String aux = parameters.getParameter(1); //Computation of the compatibility degree
    combinationType = PRODUCT;
    /*if (aux.compareToIgnoreCase("minimum") == 0) {
      combinationType = MINIMUM;
    }
    aux = parameters.getParameter(2);*/
    ruleWeight = PCF_IV;
    /*if (aux.compareToIgnoreCase("Certainty_Factor") == 0) {
      ruleWeight = CF;
    }
    else if (aux.compareToIgnoreCase("Mansoory_Rule_Weight_System") == 0) {
      ruleWeight = MCF;
    }
    else if (aux.compareToIgnoreCase("Average_Penalized_Certainty_Factor") == 0) {
      ruleWeight = PCF_II;
    }
    aux = parameters.getParameter(3);*/
    inferenceType = WINNING_RULE;
    /*if (aux.compareToIgnoreCase("Additive_Combination") == 0) {
      inferenceType = ADDITIVE_COMBINATION;
    }*/

    nRules = Integer.parseInt(parameters.getParameter(1));
    if (nRules == 0){
      if (train.getnInputs() < 10){
        nRules = 5 * train.getnInputs(); //heuristic
      }else{
        nRules = 50;
      }
    }
    while (nRules % 10 != 0) {
      nRules++;
    } //In order to have no problems with "n_replace"
    if(nRules > train.getnData()){
        nRules = train.getnInputs()/10;
        nRules *=10;
   }

    populationSize = Integer.parseInt(parameters.getParameter(2));
    while (populationSize % 10 != 0) {
      populationSize++;
    }

    crossProb = Double.parseDouble(parameters.getParameter(3));
    this.nGenerations = Integer.parseInt(parameters.getParameter(4));
    p_DC = Double.parseDouble(parameters.getParameter(5));
    michProb = Double.parseDouble(parameters.getParameter(6));

    Randomize.setSeed(seed);

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

      dataBase = new DataBase(train.getnInputs(), train.getRanges(),train.varNames(),train.getNominals());
      Population pobl = new Population(train, dataBase, populationSize, nRules,
                                     crossProb, ruleWeight, combinationType,
                                     inferenceType, p_DC, michProb);
      pobl.Generation(this.nGenerations);

      dataBase.writeFile(this.fileDB);
      ruleBase = pobl.bestRB();
      ruleBase.writeFile(this.fileBR);

      //Finally we should fill the training and test output files
      double accTra = doOutput(this.val, this.outputTr);
      double accTst = doOutput(this.test, this.outputTst);

      System.out.println("Accuracy obtained in training: "+accTra);
      System.out.println("Accuracy obtained in test: "+accTst);
      System.out.println("Algorithm Finished");
    }
  }

  /**
   * It generates the output file from a given dataset and stores it in a file
   * @param dataset myDataset input dataset
   * @param filename String the name of the file
   *
   * @return The classification accuracy
   */
  private double doOutput(myDataset dataset, String filename) {
    String output = new String("");
    int hits = 0;
    output = dataset.copyHeader(); //we insert the header in the output file
    //We write the output for each example
    for (int i = 0; i < dataset.getnData(); i++) {
      //for classification:
      String classOut = this.classificationOutput(dataset.getExample(i));
      output += dataset.getOutputAsString(i) + " " + classOut + "\n";
      if (dataset.getOutputAsString(i).equalsIgnoreCase(classOut)){
        hits++;
      }
    }
    Files.writeFile(filename, output);
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
    int clas = ruleBase.FRM(example);
    if (clas >= 0) {
      output = train.getOutputValue(clas);
    }
    return output;
  }

}

