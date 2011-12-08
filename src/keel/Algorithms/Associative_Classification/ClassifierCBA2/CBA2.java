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

package keel.Algorithms.Associative_Classification.ClassifierCBA2;

import java.io.IOException;
import org.core.*;


/**
 * It contains the implementation of the CBA2 (Classification Based on Association 2) algorithm
 *
 * @author Written by Jesus Alcala (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */
public class CBA2 {

  myDataset train, val, test;
  String outputTr, outputTst, fileDB, fileRB, fileTime, fileHora, data;
  DataBase dataBase;
  RuleBase ruleBase;
  RuleBase CAR;
  Apriori apriori;
  long startTime, totalTime;

  //We may declare here the algorithm's parameters
  double minsup, minconf;
  int isPrune, limitRules;


  private boolean somethingWrong = false; //to check if everything is correct.

  /**
   * Default constructor
   */
  public CBA2() {
  }

  /**
   * It reads the data from the input files (training, validation and test) and parse all the parameters
   * from the parameters array.
   * @param parameters It contains the input files, output files and parameters
   */
  public CBA2(parseParameters parameters) {
	this.startTime = System.currentTimeMillis();

    this.train = new myDataset();
    this.val = new myDataset();
    this.test = new myDataset();
    try {
      System.out.println("\nReading the training set: " + parameters.getTrainingInputFile());
      this.train.readClassificationSet(parameters.getTrainingInputFile(), true);
      System.out.println("\nReading the validation set: " + parameters.getValidationInputFile());
      this.val.readClassificationSet(parameters.getValidationInputFile(), false);
      System.out.println("\nReading the test set: " + parameters.getTestInputFile());
      this.test.readClassificationSet(parameters.getTestInputFile(), false);
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
	this.data = parameters.getTrainingInputFile();
    this.fileTime = (parameters.getOutputFile(1)).substring(0,(parameters.getOutputFile(1)).lastIndexOf('/')) + "/time.txt";
    this.fileHora = (parameters.getOutputFile(1)).substring(0,(parameters.getOutputFile(1)).lastIndexOf('/')) + "/hora.txt";

    //Now we parse the parameters
    this.minsup = Double.parseDouble(parameters.getParameter(0));
    this.minconf = Double.parseDouble(parameters.getParameter(1));
	this.isPrune = Integer.parseInt(parameters.getParameter(2));
	this.limitRules = Integer.parseInt(parameters.getParameter(3));
	if (this.limitRules < 1)  this.limitRules = Integer.MAX_VALUE;

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
      this.dataBase = new DataBase(this.train);
	  this.apriori = new Apriori(this.dataBase, this.train, this.minsup, this.minconf, this.isPrune, this.limitRules);
	  this.CAR = this.apriori.generateCAR();
      System.out.println("\nNumber of rule generated in Apriori: " + this.CAR.size());
	  this.CAR.CBACBM2();
	  this.ruleBase = this.CAR.getClassifier();

      this.dataBase.saveFile(this.fileDB);
      this.ruleBase.saveFile(this.fileRB);

      //Finally we should fill the training and test output files
      doOutput(this.val, this.outputTr);
      doOutput(this.test, this.outputTst);
	  
	  totalTime = System.currentTimeMillis() - startTime;
	  this.writeTime();

      System.out.println("Algorithm Finished");
    }
  }

  /**
   * It prints the running time of the algorithm
   */
  public void writeTime() {
	long seg, min, hor;
    String stringOut = new String("");

    stringOut = "" + totalTime / 1000 + "  " + data + "\n";
    Files.addToFile(this.fileTime, stringOut);
	totalTime /= 1000;
	seg = totalTime % 60;
	totalTime /= 60;
	min = totalTime % 60;
	hor = totalTime / 60;
    stringOut = "";
	
	if (hor < 10)  stringOut = stringOut + "0"+ hor + ":";
	else   stringOut = stringOut + hor + ":";

	if (min < 10)  stringOut = stringOut + "0"+ min + ":";
	else   stringOut = stringOut + min + ":";

	if (seg < 10)  stringOut = stringOut + "0"+ seg;
	else   stringOut = stringOut + seg;

	stringOut = stringOut + "  " + data + "\n";
    Files.addToFile(this.fileHora, stringOut);
  }




  /**
   * It generates the output file from a given dataset and stores it in a file
   * @param dataset input dataset
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
   * @param example The input example
   * @return String the output generated by the algorithm
   */
  private String classificationOutput(int[] example) {
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