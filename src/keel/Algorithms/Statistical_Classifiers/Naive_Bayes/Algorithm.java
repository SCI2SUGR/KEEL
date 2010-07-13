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

package keel.Algorithms.Statistical_Classifiers.Naive_Bayes;

/**
 * <p>Title: Algorithm</p>
 *
 * <p>Description: It contains the implementation of the algorithm Naive-Bayes</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Alberto Fernández (University of Granada) 02/07/2007
 * @version 1.0
 * @since JDK1.5
 */

import java.io.IOException;
import org.core.*;

public class Algorithm {

    myDataset train, val, test;
    String outputTr, outputTst, output;
    double classProb[];
    double attrProb[][][]; //atribute value, atribute position, class
    int counts[][][]; //atribute value, atribute position, class
    int nClasses;

    //We may declare here the algorithm's parameters

    private boolean somethingWrong = false; //to check if everything is correct.

    /**
     * Default constructor
     */
    public Algorithm() {
    }

    /**
     * It reads the data from the input files (training, validation and test) and parse all the parameters
     * from the parameters array.
     * @param parameters parseParameters It contains the input files, output files and parameters
     */
    public Algorithm(parseParameters parameters) {

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
        somethingWrong = somethingWrong || train.hasNumericalAttributes();
        //somethingWrong = somethingWrong || train.hasMissingAttributes();

        outputTr = parameters.getTrainingOutputFile();
        outputTst = parameters.getTestOutputFile();
        output = parameters.getOutputFile(0);

        //Now we parse the parameters, for example:
        /*
         seed = Long.parseLong(parameters.getParameter(0));
         iterations = Integer.parseInt(parameters.getParameter(1));
         crossOverProb = Double.parseDouble(parameters.getParameter(2));
         */
        //...

    }

    /**
     * It launches the algorithm
     */
    public void execute() {
        if (somethingWrong) { //We do not execute the program
            System.err.println("An error was found, the data-set have numerical attributes. Please use a discretizer.");
            System.err.println("Aborting the program");
            //We should not use the statement: System.exit(-1);
        } else {
            //We do here the algorithm's operations

            nClasses = train.getnOutputs();
            computeProbabilites();

            //Finally we should fill the training and test output files
            doOutput(this.val, this.outputTr);
            doOutput(this.test, this.outputTst);
            generateOutputInfo();

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
                    this.classificationOutput(dataset.getExample(i),dataset.getMissing(i)) + "\n";
        }
        Fichero.escribeFichero(filename, output);
    }

    /**
     * It returns the algorithm classification output given an input example
     * @param example double[] The input example
     * @param missing boolean [] A vector that stores the possible missing attributes of the examples
     * @return String the output generated by the algorithm
     */
    private String classificationOutput(double[] example, boolean [] missing) {
        String output = new String("?");
        /**
          Here we should include the algorithm directives to generate the
          classification output from the input example
         */

        //We compute P(C_i | X_j)
        double probClasses[] = new double[nClasses];
        double probExampleClass[] = new double[nClasses];
        double probExample = 0.0;

        for (int i = 0; i < nClasses; i++) {
            probExampleClass[i] = computeProbExampleClass(example, missing, i);
            probExample += probExampleClass[i] * this.classProb[i];
        }

        for (int i = 0; i < nClasses; i++) {
            probClasses[i] = (probExampleClass[i] * this.classProb[i]) /
                             probExample;
        }

        double max = 0.0;
        int finalClass = -1;
        for (int i = 0; i < nClasses; i++) {
            if (max < probClasses[i]) {
                max = probClasses[i];
                finalClass = i;
            }
        }
        if (finalClass != -1) {
            output = train.getOutputValue(finalClass);
        }

        return output;
    }

    /**
     * It computes the prior probabilities of the different classes and attribute values corresponding to a class
     */
    private void computeProbabilites() {
        computeClassProb(); //First the class probabilities
        computeAttrProb(); //Then the probability of the attributes to be in a certain class
    }

    /**
     * Here we compute the prior class probabilities
     */
    private void computeClassProb() {
        classProb = new double[nClasses];
        train.computeInstancesPerClass();
        for (int i = 0; i < nClasses; i++) {
            classProb[i] = 1.0 * train.numberInstances(i) / train.getnData();
        }
    }

    /**
     * Here we compute the probability of an attribute value to be in a certain class
     */
    private void computeAttrProb() {
        double example[];
        int clas;
        attrProb = new double[nClasses][train.getnInputs()][1];
        counts = new int[nClasses][train.getnInputs()][1];
        for (int i = 0; i < nClasses; i++) {
            for (int j = 0; j < train.getnInputs(); j++) {
                attrProb[i][j] = new double[train.numberValues(j)];
                counts[i][j] = new int[train.numberValues(j)];
            }
        }
        for (int i = 0; i < train.getnData(); i++) {
            example = train.getExample(i);
            clas = train.getOutputAsInteger(i);
            for (int j = 0; j < train.getnInputs(); j++) {
                if (! train.isMissing(i,j)){
                    attrProb[clas][j][(int) example[j]]++;
                }
            }
        }
        int contador[][] = new int[nClasses][train.getnInputs()];

        for (int i = 0; i < attrProb.length; i++) {
            for (int j = 0; j < attrProb[i].length; j++) {
                for (int k = 0; k < attrProb[i][j].length; k++) {
                    counts[i][j][k] = (int)attrProb[i][j][k]; //for output
                    attrProb[i][j][k]++; //Laplace
                    contador[i][j] += attrProb[i][j][k];
                }
            }
        }
        for (int i = 0; i < attrProb.length; i++) {
            for (int j = 0; j < attrProb[i].length; j++) {
                for (int k = 0; k < attrProb[i][j].length; k++) {
                    attrProb[i][j][k] /= contador[i][j];
                }
            }
        }
    }

    /**
     * This function computes the probability of an example to be in a certain class
     * @param example double[] The attribute values of the example
     * @param missing boolean [] A vector that stores the possible missing attributes of the examples
     * @param clas int The class to check
     * @return double The computed probability
     */
    private double computeProbExampleClass(double[] example, boolean [] missing, int clas) {
        double prob = 1.0;
        for (int i = 0; i < example.length; i++) {
            if (!missing[i]){
                prob *= attrProb[clas][i][(int) example[i]];
            }
        }
        return prob;
    }

    /**
     * Here we generate some info about the counts of each value for the pair attribute-class
     */
    private void generateOutputInfo(){
        String string = new String("");
        for (int i = 0; i < nClasses; i++){
            string += "\nClass " + train.getOutputValue(i) +
                    ": Prior probability = " + classProb[i] + "\n";
            for (int j = 0; j < counts[i].length; j++){
                string += train.varName(j)+": Discrete Estimator. Counts = ";
                int contador = 0;
                for (int k = 0; k < counts[i][j].length; k++){
                    string += counts[i][j][k] + " ";
                    contador += counts[i][j][k];
                }
                string += "(Total = "+contador+")\n";
            }
            string += "\n\n";
        }
        Fichero.escribeFichero(output,string);
    }
}

