package keel.Algorithms.Fuzzy_Rule_Learning.AdHoc.Chi_RW;

import java.io.IOException;
import org.core.*;

/**
 * <p>Title: Fuzzy_Chi</p>
 *
 * <p>Description: It contains the implementation of the Chi algorithm</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Alberto Fernández (University of Granada) 02/11/2007
 * @version 1.0
 * @since JDK1.5
 */
public class Fuzzy_Chi {

  myDataset train, val, test;
  String outputTr, outputTst, fileDB, fileRB;
  int nClasses, nLabels, combinationType, inferenceType, ruleWeight;
  DataBase dataBase;
  RuleBase ruleBase;

  public static final int MINIMUM = 0;
  public static final int PRODUCT = 1;
  public static final int CF = 0;
  public static final int PCF_IV = 1;
  public static final int MCF = 2;
  public static final int NO_RW = 3;
  public static final int PCF_II = 3;
  public static final int WINNING_RULE = 0;
  public static final int ADDITIVE_COMBINATION = 1;

  //We may declare here the algorithm's parameters

  private boolean somethingWrong = false; //to check if everything is correct.

  /**
   * Default constructor
   */
  public Fuzzy_Chi() {
  }

  /**
   * It reads the data from the input files (training, validation and test) and parse all the parameters
   * from the parameters array.
   * @param parameters parseParameters It contains the input files, output files and parameters
   */
  public Fuzzy_Chi(parseParameters parameters) {

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
    } catch (IOException e) {
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

    fileDB = parameters.getOutputFile(0);
    fileRB = parameters.getOutputFile(1);

    //Now we parse the parameters
    nLabels = Integer.parseInt(parameters.getParameter(0));
    String aux = parameters.getParameter(1); //Computation of the compatibility degree
    combinationType = PRODUCT;
    if (aux.compareToIgnoreCase("minimum") == 0) {
      combinationType = MINIMUM;
    }
    aux = parameters.getParameter(2);
    ruleWeight = PCF_IV;
    if (aux.compareToIgnoreCase("Certainty_Factor") == 0) {
      ruleWeight = CF;
    }
    else if (aux.compareToIgnoreCase("Average_Penalized_Certainty_Factor") == 0) {
      ruleWeight = PCF_II;
    }
    else if (aux.compareToIgnoreCase("No_Weights") == 0){
      ruleWeight = NO_RW;
    }
    aux = parameters.getParameter(3);
    inferenceType = WINNING_RULE;
    if (aux.compareToIgnoreCase("Additive_Combination") == 0) {
      inferenceType = ADDITIVE_COMBINATION;
    }
  }

  /**
   * It launches the algorithm
   */
  public void execute() {
    if (somethingWrong) { //We do not execute the program
      System.err.println("An error was found, the data-set have missing values");
      System.err.println("Please remove those values before the execution");
      System.err.println("Aborting the program");
      //We should not use the statement: System.exit(-1);
    }
    else {
      //We do here the algorithm's operations

      nClasses = train.getnClasses();

      dataBase = new DataBase(train.getnInputs(), nLabels,
                            train.getRanges(),train.getNames());
      ruleBase = new RuleBase(dataBase, inferenceType, combinationType,
                             ruleWeight, train.getNames(), train.getClasses());

      System.out.println("Data Base:\n"+dataBase.printString());
      ruleBase.Generation(train);

      dataBase.writeFile(this.fileDB);
      ruleBase.writeFile(this.fileRB);

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
    int classOut = ruleBase.FRM(example);
    if (classOut >= 0) {
      output = train.getOutputValue(classOut);
    }
    return output;
  }

}
