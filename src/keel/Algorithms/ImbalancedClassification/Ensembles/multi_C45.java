/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. SÃ¡nchez (luciano@uniovi.es)
    J. AlcalÃ¡-Fdez (jalcala@decsai.ugr.es)
    S. GarcÃ­a (sglopez@ujaen.es)
    A. FernÃ¡ndez (alberto.fernandez@ujaen.es)
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

import java.io.IOException;
import org.core.*;
import keel.Algorithms.ImbalancedClassification.Ensembles.C45.C45;
import java.util.StringTokenizer;
import java.util.Vector;
import keel.Algorithms.ImbalancedClassification.Auxiliar.AUC.CalculateAUC;
import keel.Algorithms.ImbalancedClassification.Auxiliar.AUC.PosProb;
import keel.Algorithms.ImbalancedClassification.Auxiliar.AUC.AccAUC;
import keel.Algorithms.ImbalancedClassification.Auxiliar.AUC.PredPair;

/**
 * <p>Title: multi_C45</p>
 * <p>Description: Main class to compute the algorithm procedure
 * <p>Company: KEEL </p>
 *
 * @author Mikel Galar Idoate (UPNA)
 * @author Modified by Alberto Fernandez (University of Jaen) 15/10/2012
 * @author Modified by Sarah Vluymans (University of Ghent) 29/01/2014
 * @author Modified by Alberto Fernandez (University of Jaen) 08/05/2014
 * @version 1.2
 * @since JDK1.6
 */
 
public class multi_C45 {

  parseParameters parameters;
  myDataset train, val, test;

    /**
     *Training output filename.
     */
    public static String outputTr;
	String outputTst, ruleBaseFile;
	int instancesPerLeaf, n_classifiers, lambda;
  float confidence;
  boolean pruned, valid[];
	String trainFile,cabecera;
	RuleBase[] treeRuleSet;           // Trees of the ensemble
  myDataset actua_train_set;        // train data-set for the actual ensemble
  Ensemble ensemble;                
  String ensembleType;
	String evMeas;


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
    trainFile = parameters.getTrainingInputFile();
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

    ruleBaseFile = parameters.getOutputFile(0);

    //Now we parse the parameters
    pruned = parameters.getParameter(1).equalsIgnoreCase("TRUE");
    confidence = Float.parseFloat(parameters.getParameter(2));
    instancesPerLeaf = Integer.parseInt(parameters.getParameter(3));
    n_classifiers = Integer.parseInt(parameters.getParameter(4));
    ensembleType = parameters.getParameter(5);
    
    if (ensembleType.equalsIgnoreCase("ADABOOST.NC"))
    	lambda = Integer.parseInt(parameters.getParameter(7));
    else
    	lambda = 1;

		cabecera = parameters.getTrainingInputFile();

		String[] aux = null;
		aux = cabecera.split("\\.");
		cabecera = aux[aux.length - 2]; //aux.length-1 es la extension
		aux = cabecera.split("/");
		cabecera = aux[aux.length - 1];  
    
    /* Create the ensemble! */
    ensemble = new Ensemble(ensembleType, train, n_classifiers, lambda, this);
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
      treeRuleSet = new RuleBase[n_classifiers];

      /** While the algorithm has not end, and the number of classifier constructed is not reached... 
       * we construct a new classifier for the ensemble
       */
      boolean fin = false;
      for (int i = 0; i < n_classifiers && !fin; i++) {

          // we get the actual training data-set
        actua_train_set = ensemble.getDS();

        /* Databoost-IM has problems generating instances in Highly imbalanced data-sets */
        if (actua_train_set.getnData() > 53000)
        {
           System.out.println("Databoost overflow!, nData = " + actua_train_set.getnData());
           fin = true;
           break;
        }
        boolean mal = false;
        if (!actua_train_set.vacio())
        {
            // write the data-set which will be readed by C4.5 decision tree learning algorithm
             Fichero.escribeFichero(ensembleType + cabecera  + ".txt", actua_train_set.printDataSet());
             valid[i] = true;
             System.out.println("Training classifier[" + i + "]");
             // Construct the tree using the weights (they can be unirformly distributed)
             C45 tree = new C45(ensembleType + cabecera + ".txt", pruned, confidence, instancesPerLeaf, ensemble.getWeights().clone());
             
             try {
               tree.generateTree();
             }
             catch (Exception e) {
               System.err.println("Error!!");
               System.err.println(e.getMessage());
               System.exit( -1);
             }
             /* The tree is stored in a set of rules */
             String cadenaTree = tree.printString();
             obtainRules(cadenaTree, i);
             if (treeRuleSet[i].size() == 0)
             {
             	  mal = true;
                int clase = tree.getPriorProbabilities()[0] > tree.getPriorProbabilities()[1] ? 0 : 1;
                // The a priori rule is introduced which predict the class with the greatest prior probability
                treeRuleSet[i].ruleBase.add(new Rule(train.getOutputValue(clase), actua_train_set));
             }

             treeRuleSet[i].coverExamples();
             treeRuleSet[i].coverExamples(ensemble.getWeights().clone());    //Step 2                    
           }
           else {
             valid[i] = false;
           }
            // Go to the next iteration of the ensemble!
				if (mal) {
					if ((!ensembleType.contains("EUNDERBAGGING")) && (ensemble.weightsBackup != null)) {
						ensemble.weights = ensemble.weightsBackup.clone();
					}
					else
						fin = ensemble.nextIteration();
				} else
					fin = ensemble.nextIteration();
				if (ensembleType.equalsIgnoreCase("EASYENSEMBLE") 
						|| ensembleType.equalsIgnoreCase("BALANCECASCADE"))
					i = ensemble.t - 1;
        }
      //Finally we should fill the training and test output files
      AccAUC pairTra = doOutput(this.val, this.outputTr);
      AccAUC pairTst = doOutput(this.test, this.outputTst);
      writeOutput(pairTra, pairTst, this.ruleBaseFile);
			ensemble.writeAUCError(this.outputTst);
    }
  }


  /**
   * It generates the output file from a given dataset and stores it in a file
   * @param dataset myDataset input dataset
   * @param filename String the name of the file
   * @return the Accuracy of the classifier
   */
  private AccAUC doOutput(myDataset dataset, String filename) {
     double TP = 0, FP = 0, FN = 0, TN = 0;
    /*String output = new String("");
    output = dataset.copyHeader(); //we insert the header in the output file
    int aciertos = 0;*/
    
    String outputTotal = dataset.copyHeader();
		String claseReal = "";
		String prediccion = "";
		String output2 = "";
		StringBuilder sb = new StringBuilder(dataset.getnData() * 5);
		int aciertos = 0;
    
    /*
     * For AUC: when the weighted sum is positive, the instance is classified
     * as originalDS.getOutputValue(0). When it is negative, as 
     * originalDS.getOutputValue(1).
     * To be able to compute the AUC, we need, for each instance, the probability
     * that the classifier will classify it as belonging to the positive 
     * (minority) class. When this class is the same as 
     * originalDS.getOutputValue(0), we can just the value of the weighted sum
     * as 'probability'. In the other case, we will use (-1) * sum.
     * Note that these 'probabilities' do not necessarily belong to [0,1]. 
     * This is no problem, only their relative differences matter.
     */
    boolean takeOpposite = 
            ensemble.originalDS.getOutputValue(0).equals(train.claseMasFrecuente());
    PosProb[] valsForAUC = new PosProb[dataset.getnData()];
    
 
    //We write the output for each example
    for (int i = 0; i < dataset.getnData(); i++) {
        claseReal = dataset.getOutputAsString(i);
        PredPair predAndVoteValue = this.classificationOutput(dataset.getExample(i));
        prediccion = predAndVoteValue.getPrediction();
        output2 = claseReal.concat(" ").concat(prediccion).concat("\n");
        
        // Calculations for accuracy
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
        
        // Calculations for AUC
        double voted = predAndVoteValue.getVotingValue();
        boolean isPositive = !claseReal.equals(train.claseMasFrecuente());
        double prob = voted;
        if(takeOpposite){
            prob *= -1.0;
        }
        valsForAUC[i] = new PosProb(isPositive, prob);
        
        sb.append(output2);
     }
		outputTotal += sb.toString();

    double TPrate = TP / (TP + FN);
    double TNrate = TN / (TN + FP);
    double gmean = Math.sqrt(TPrate * TNrate);
    double precision = TP / (TP + FP);
    double recall = TP / (TP + FN);
    double fmean = 2 * recall * precision / (1 * recall + precision);

		System.out.println("G-mean: " + gmean);
		System.out.println("F-mean: " + fmean);
		System.out.println("TPrate: " + TPrate);
		System.out.println("TNrate: " + TNrate);
		double FPrate = FP / (FP + TN);
		System.out.println("AUC: " + (1 + TPrate - FPrate) / 2);
		Files.writeFile(filename, outputTotal);
    
    double acc = 1.0 * aciertos / dataset.size();
    double auc = getAUC(valsForAUC);

    return new AccAUC(acc, auc);
  }

	/**
	 * It carries out the classification of a given dataset throughout the learning stage of the ensemble
	 * @param dataset the instance set
	 * @return accuracy for the current ensemble
	 */
    public double classify (myDataset dataset) {

       //double TP = 0, FP = 0, FN = 0, TN = 0;

    //String output = new String("");
    //output = dataset.copyHeader(); //we insert the header in the output file
    int aciertos = 0;
    //We write the output for each example
    for (int i = 0; i < dataset.getnData(); i++) {
        String claseReal = dataset.getOutputAsString(i);
        PredPair predAndVoteValue = this.classificationOutput(dataset.getExample(i));
        String prediccion = predAndVoteValue.getPrediction();
        //output += claseReal + " " + prediccion + "\n";
        if (claseReal.equalsIgnoreCase(prediccion)) {
          aciertos++;
        }

        /*if (claseReal.equalsIgnoreCase(prediccion) && claseReal.equalsIgnoreCase(train.claseMasFrecuente()))
           TN++;
        else if (claseReal.equalsIgnoreCase(prediccion) && !claseReal.equalsIgnoreCase(train.claseMasFrecuente()))
           TP++;
        else if (!claseReal.equalsIgnoreCase(prediccion) && claseReal.equalsIgnoreCase(train.claseMasFrecuente()))
           FP++;
        else
           FN++;*/
     }

    /*double TPrate = TP / (TP + FN);
    double TNrate = TN / (TN + FP);
    double gmean = Math.sqrt(TPrate * TNrate);
    double precision = TP / (TP + FP);
    double recall = TP / (TP + FN);
    double fmean = 2 * recall * precision / (1 * recall + precision);

    System.out.println("G-mean: " + gmean);
    System.out.println("F-mean: " + fmean);
    System.out.println("TPrate: " + TPrate);*/

    return (1.0 * aciertos / dataset.size());
  }


  /**
   * It returns the algorithm classification output given an input example
   * @param example double[] The input example
   * @return String the output generated by the algorithm
   */
  private PredPair classificationOutput(double[] example) {
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
        for (int j = 0; (j < treeRuleSet[i].size()) && (clase.equals("?"));
             j++) {
          if (treeRuleSet[i].ruleBase.get(j).covers(example)) {
            clase = treeRuleSet[i].ruleBase.get(j).clase;
          }
        }
        int clase_num = train.claseNumerica(clase);
        if (clase_num == -1)
        {
            clase_num = train.claseNumerica(train.claseMasFrecuente());
        }
        return clase_num;
      }
      else {
      	System.err.println("This should not be accessed: "+i+"/"+valid[i]);
          return -1;
        }
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
        for (int j = 0; (j < treeRuleSet[i].size()) && (clase.equals("?"));
             j++) {
          if (treeRuleSet[i].ruleBase.get(j).covers(example)) {
						clase = treeRuleSet[i].ruleBase.get(j).clase;
						double nCubiertosOK = treeRuleSet[i].ruleBase.get(j).fCubiertosOK; 
						double nCubiertos = treeRuleSet[i].ruleBase.get(j).fCubiertos;
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

	/**
	 * It extracts the rule set from a given file exported by the C4.5 classifier
	 * @param treeString the contain of the file (rule set)
	 * @param classifier classifier id of the ensemble
	 */
	private void obtainRules(String treeString, int classifier) {
		String rules = new String("");
		StringTokenizer lines = new StringTokenizer(treeString, "\n"); //read lines
		String line = lines.nextToken(); //First line @TotalNumberOfNodes X
		line = lines.nextToken(); //Second line @NumberOfLeafs Y
		//The tree starts
		Vector <String>variables = new Vector<String>();
		Vector <String>values = new Vector<String>();
		Vector <String>operators = new Vector<String>();
		int contador = 0;
		while (lines.hasMoreTokens()) {
			line = lines.nextToken();
			StringTokenizer field = new StringTokenizer(line, " \t");
			String cosa = field.nextToken(); //Possibilities: "if", "elseif", "class"
			if (cosa.compareToIgnoreCase("if") == 0) {
				field.nextToken(); //(
				variables.add(field.nextToken()); //variable name (AttX, X == position)
				operators.add(field.nextToken()); //One of three: "=", "<=", ">"
				values.add(field.nextToken()); //Value
			}
			else if (cosa.compareToIgnoreCase("elseif") == 0) {
				int dejar = Integer.parseInt(field.nextToken());
				for (int i = variables.size() - 1; i >= dejar; i--) {
					variables.remove(variables.size() - 1);
					operators.remove(operators.size() - 1);
					values.remove(values.size() - 1);
				}
				field.nextToken(); //(
				variables.add(field.nextToken()); //variable name (AttX, X == position)
				operators.add(field.nextToken()); //One of three: "=", "<=", ">"
				values.add(field.nextToken()); //Value
			}
			else { //Class --> rule generation
				field.nextToken(); // =
				contador++; //I have a new rule
				rules += "\nRULE-" + contador + ": IF ";
				int i;
				for (i = 0; i < variables.size() - 1; i++) {
					rules += (String) variables.get(i) + " " + (String) operators.get(i) +
							" " + (String) values.get(i) + " AND ";
				}
				rules += (String) variables.get(i) + " " + (String) operators.get(i) +
						" " + (String) values.get(i);
				rules += " THEN class = " + field.nextToken();
				variables.remove(variables.size() - 1);
				operators.remove(operators.size() - 1);
				values.remove(values.size() - 1);
			}
		}
		treeRuleSet[classifier] = new RuleBase(actua_train_set, rules);
	}

	/**
	 * It writes on a file the full ensemble (C4.5 rule sets)
	 * @param pairTra Training accuracy (AUC)
	 * @param pairTst Test accuracy (AUC)
        * @param ruleBaseFile RuleBase filename.
	 */
  public void writeOutput (AccAUC pairTra, AccAUC pairTst, String ruleBaseFile) {    
    Files.writeFile(ruleBaseFile,"");
    for (int i = 0; i < ensemble.nClassifier; i++) {
      if (valid[i]) {
        Files.addToFile(ruleBaseFile, "@Classifier number " + i + ": \n");
				Files.addToFile(ruleBaseFile,treeRuleSet[i].printStringF() + "\n");                               
      }
      else {
        // System.out.println("Not valid!");
      }
    }

		Files.addToFile(ruleBaseFile, "Accuracy in training: " + pairTra.getAcc() + "\n");
		Files.addToFile(ruleBaseFile, "Accuracy in test: " + pairTst.getAcc() + "\n");
		Files.addToFile(ruleBaseFile, "AUC in training: " + pairTra.getAUC() + "\n");
		Files.addToFile(ruleBaseFile, "AUC in test: " + pairTst.getAUC() + "\n");
 
    System.out.println("Accuracy in training: " + pairTra.getAcc());
    System.out.println("Accuracy in test: " + pairTst.getAcc());
    System.out.println("AUC in training: " + pairTra.getAUC());
    System.out.println("AUC in test: " + pairTst.getAUC());
    System.out.println("Algorithm Finished");
    
  }
  
    /*
     * Calculates the AUC for the associated set of values
     *
     * @param	valsForAUC	Array containing the predicted classes and sum obtained by the ensemble
     *
     * @return			The AUC value associated to the given set of values
     */
    private double getAUC(PosProb[] valsForAUC){
        return CalculateAUC.calculate(valsForAUC);
    }


}
