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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierFuzzySGERD;

/**
 * <p>Title: SGERD</p>
 *
 * <p>Description: It contains the implementation of the SGERD algorithm</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Alberto Fernández (University of Granada) 03/09/2007
 * @author Modified by Jesus Alcalá (University of Granada) 19/05/2009
 * @version 1.4
 * @since JDK1.5
 */

import java.io.IOException;
import org.core.*;

public class SGERD {

  myDataset train, val, test;
  String outputTr, outputTst, fileDB, fileRB;
  DataBase dataBase;
  RuleBase ruleBase;

  //We may declare here the algorithm's parameters
  int typeEvaluation, Q, K;

  private boolean somethingWrong = false; //to check if everything is correct.

  /**
   * Default constructor
   */
  public SGERD() {
  }

  /**
   * It reads the data from the input files (training, validation and test) and parse all the parameters
   * from the parameters array.
   * @param parameters parseParameters It contains the input files, output files and parameters
   */
  public SGERD(parseParameters parameters) {

    this.train = new myDataset();
    this.val = new myDataset();
    this.test = new myDataset();
    try {
      System.out.println("\nReading the training set: " + parameters.getTrainingInputFile());
      this.train.readClassificationSet(parameters.getTrainingInputFile(), true);
	  this.train.computeOverlapping();
	  this.train.normalize();
      this.train.computeStatistics();
      this.train.computeInstancesPerClass();
      System.out.println("\nReading the validation set: " + parameters.getValidationInputFile());
      this.val.readClassificationSet(parameters.getValidationInputFile(), false);
	  this.val.normalize();
      System.out.println("\nReading the test set: " + parameters.getTestInputFile());
      this.test.readClassificationSet(parameters.getTestInputFile(), false);
	  this.test.normalize();
    }
    catch (IOException e) {
      System.err.println("There was a problem while reading the input data-sets: " + e);
      this.somethingWrong = true;
    }

    //We may check if there are some numerical attributes, because our algorithm may not handle them:
    //somethingWrong = somethingWrong || train.hasNumericalAttributes();
    this.somethingWrong = this.somethingWrong || this.train.hasMissingAttributes();

    this.outputTr = parameters.getTrainingOutputFile();
    this.outputTst = parameters.getTestOutputFile();

    this.fileDB = parameters.getOutputFile(0);
    this.fileRB = parameters.getOutputFile(1);

    //Now we parse the parameters
    long seed = Long.parseLong(parameters.getParameter(0));

    this.Q = Integer.parseInt(parameters.getParameter(1));
    if ((this.Q < 1) || (this.Q > (14*this.train.getnInputs())))  this.Q = Math.min((14*this.train.getnInputs()) / (2*this.train.getnClasses()), 20);
	if (this.Q < 1)  this.Q = 1;

	this.typeEvaluation = Integer.parseInt(parameters.getParameter(2));
	this.K = 5;

    Randomize.setSeed(seed);
  }

  /**
   * It launches the algorithm
   */
  public void execute() {
    if (this.somethingWrong) { //We do not execute the program
        System.err.println("An error was found, either the data-set has missing values.");
        System.err.println("Please remove the examples with missing data or apply a MV preprocessing.");
        System.err.println("Aborting the program");
      //We should not use the statement: System.exit(-1);
    }
    else {
      //We do here the algorithm's operations
      this.dataBase = new DataBase(this.K, this.train.getnInputs(), this.train.getRanges(), this.train.varNames());
      this.ruleBase = new RuleBase(dataBase, train, typeEvaluation);
      this.ruleBase.initialization();

      Population pobl = new Population(this.ruleBase, this.Q, this.train, this.dataBase.numLabels());
      pobl.Generation();

      dataBase.saveFile(this.fileDB);
      ruleBase = pobl.bestRB();
      ruleBase.saveFile(this.fileRB);

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
      output += dataset.getOutputAsString(i) + " " + this.classificationOutput(dataset.getExample(i)) + "\n";
    }
    Files.writeFile(filename, output);
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
    int clas = this.ruleBase.FRM(example);
    if (clas >= 0) {
      output = train.getOutputValue(clas);
    }
    return output;
  }

}

