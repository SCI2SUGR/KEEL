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

package keel.Algorithms.Associative_Classification.ClassifierCMAR;

import java.io.IOException;
import org.core.*;

/**
 * It contains the implementation of the CMAR algorithm
 *
 * @author Written by Jesus Alcala (University of Granada) 09/02/2010
 * @author Modified by Sergio Almecija (University of Granada) 23/05/2010
 * @version 1.0
 * @since JDK1.5
 */
public class CMAR {

  myDataset train, val, test;
  String outputTr, outputTst, fileDB, fileRB, fileTime, fileHora, data;
  DataBase dataBase;
  AprioriTFP_CMAR newClassification;
  long startTime, totalTime;

  //We may declare here the algorithm's parameters
  double minConf, minSup;
  int delta;

  private boolean somethingWrong = false; //to check if everything is correct.

  /**
   * It reads the data from the input files (training, validation and test) and parse all the parameters
   * from the parameters array.
   * @param parameters parseParameters It contains the input files, output files and parameters
   */
  public CMAR(parseParameters parameters) {
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
    this.minConf = Double.parseDouble(parameters.getParameter(0)) * 100.0;
    this.minSup = Double.parseDouble(parameters.getParameter(1)) * 100.0;
    this.delta = Integer.parseInt(parameters.getParameter(2));
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
	  
	  // Create instance of class ClassificationPRM	
	  this.newClassification = new AprioriTFP_CMAR(this.minConf, this.minSup, this.delta);

	  // Read data to be mined from file (method in AssocRuleMining class)
	  this.newClassification.inputDataSet(this.train, this.dataBase);
	  
	  // Reorder input data according to frequency of single attributes
	  // excluding classifiers. Proceed as follows: (1) create a conversion
	  // array (with classifiers left at end), (2) reorder the attributes 
	  // according to this array. Do not throw away unsupported attributes 
	  // as when data set is split (if distribution is not exactly even) we 
	  // may have thrown away supported attributes that contribute to the 
	  // generation of CRs. NB Never throw away classifiers even if
	  // unsupported!
	  
//	  newClassification.idInputDataOrdering(this.dataBase);  // ClassificationAprioriT
	  //newClassification.recastInputData();      // AssocRuleMining
	  
	  // Create training data set (method in ClassificationAprioriT class)
	  // assuming a 50:50 split
	  this.newClassification.testDataSet(this.test, this.dataBase);
//	  newClassification.createTrainingAndTestDataSets();
	  
	  // Mine data, produce T-tree and generate CRs
	  newClassification.startCMARclassification();
//	  newClassification.outputDuration(time1, (double) System.currentTimeMillis());

	  // Output
//	  this.newClassification.outputFrequentSets();
//	  this.newClassification.outputNumFreqSets();
//	  this.newClassification.outputNumUpdates();
//	  this.newClassification.outputStorage();
	  //newClassification.outputTtree();
//	  System.out.println("Accuracy = " + accuracy);
//	  newClassification.getCurrentRuleListObject().outputNumCMARrules();
	  
	  // Two methiods for outputting rules, second should only be used 
	  // when input data set has been reordered.
	  //newClassification.getCurrentRuleListObject().outputCMARrulesWithReconversion();
	  
      this.dataBase.saveFile(this.fileDB);
	  this.newClassification.getCurrentRuleListObject().outputCMARrules(this.fileRB);
//      this.ruleBase.saveFile(this.fileRB);


      //Finally we should fill the training and test output files
      doOutputTra(this.outputTr);
      doOutputTst(this.outputTst);
	  
	  totalTime = System.currentTimeMillis() - startTime;
	  this.writeTime();

      System.out.println("Algorithm Finished");
    }
  }

  /**
   * It writes the time the algorithm takes on classify a given dataset.
   */
  public void writeTime() {
	long aux, seg, min, hor;
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
   * @param dataset myDataset input dataset
   * @param filename String the name of the file
   */
  private void doOutputTra(String filename) {
    String output = new String("");
    output = this.train.copyHeader(); //we insert the header in the output file
    //We write the output for each example
    for (int i = 0; i < this.train.getnData(); i++) {
      //for classification:
      output += this.train.getOutputAsString(i) + " " + this.classificationOutput(this.newClassification.dataArray[i]) + "\n";
    }
    Files.writeFile(filename, output);
  }

  /**
   * It generates the output file from a given dataset and stores it in a file
   * @param dataset myDataset input dataset
   * @param filename String the name of the file
   */
  private void doOutputTst(String filename) {
    String output = new String("");
    output = this.test.copyHeader(); //we insert the header in the output file

//	if (this.newClassification.currentRlist.startCMARrulelist != null) {
		//We write the output for each example
		for (int i = 0; i < this.test.getnData(); i++) {
			//for classification:
			output += this.test.getOutputAsString(i) + " " + this.classificationOutput(this.newClassification.testDataArray[i]) + "\n";
		}
//	}	

    Files.writeFile(filename, output);
  }

  /**
   * It returns the algorithm classification output given an input example
   * @param example double[] The input example
   * @return String the output generated by the algorithm
   */
  private String classificationOutput(short[] example) {
    String output = new String("?");
    /**
      Here we should include the algorithm directives to generate the
      classification output from the input example
     */
    int clas = this.newClassification.currentRlist.classifyRecordWCS(example);
    if (clas > 0) {
//      output = "" + clas;
      output = train.getOutputValue(clas-this.dataBase.getOrderClas());
    }
    return output;
  }

}

