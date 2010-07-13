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
 * <p>Title: Dataset</p>
 *
 * <p>Description: It contains the methods to read a Classification/Regression Dataset</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Alberto Fernández (University of Granada) 02/07/2007
 * @version 1.0
 * @since JDK1.5
 */

import java.io.IOException;

import keel.Dataset.*;

public class myDataset {

    private double[][] X = null; //examples array
    private boolean[][] missing = null; //possible missing values
    private int[] outputInteger = null; //output of the data-set as integer values
    private double[] outputReal = null; //output of the data-set as double values
    private String[] output = null; //output of the data-set as string values
    private double[] emax; //max value of an attribute
    private double[] emin; //min value of an attribute

    private int nData; // Number of examples
    private int nVars; // Numer of variables
    private int nInputs; // Number of inputs
    private int nOutputs; // Number of outputs

    private InstanceSet IS; //The whole instance set

    private double stdev[], average[]; //standard deviation and average of each attribute
    private int instancesCl[];

    /**
     * Init a new set of instances
     */
    public myDataset() {
        IS = new InstanceSet();
    }

    /**
     * Outputs an array of examples with their corresponding attribute values.
     * @return double[][] an array of examples with their corresponding attribute values
     */
    public double[][] getX() {
        return X;
    }

    /**
     * Output a specific example
     * @param pos int position (id) of the example in the data-set
     * @return double[] the attributes of the given example
     */
    public double[] getExample(int pos) {
        return X[pos];
    }

    /**
     * Returns the output of the data-set as integer values
     * @return int[] an array of integer values corresponding to the output values of the dataset
     */
    public int[] getOutputAsInteger() {
        int[] output = new int[outputInteger.length];
        for (int i = 0; i < outputInteger.length; i++) {
            output[i] = outputInteger[i];
        }
        return output;
    }

    /**
     * Returns the output of the data-set as real values
     * @return double[] an array of real values corresponding to the output values of the dataset
     */
    public double[] getOutputAsReal() {
        double[] output = new double[outputReal.length];
        for (int i = 0; i < outputReal.length; i++) {
            output[i] = outputInteger[i];
        }
        return output;
    }

    /**
     * Returns the output of the data-set as nominal values
     * @return String[] an array of nomianl values corresponding to the output values of the dataset
     */
    public String[] getOutputAsString() {
        String [] output = new String[this.output.length];
        for (int i = 0; i < this.output.length; i++) {
            output[i] = this.output[i];
        }
        return output;
    }

    /**
     * It returns the output value of the example "pos"
     * @param pos int the position (id) of the example
     * @return String a string containing the output value
     */
    public String getOutputAsString(int pos) {
        return output[pos];
    }

    /**
     * It returns the output value of the example "pos"
     * @param pos int the position (id) of the example
     * @return int an integer containing the output value
     */
    public int getOutputAsInteger(int pos) {
        return outputInteger[pos];
    }

    /**
     * It returns the output value of the example "pos"
     * @param pos int the position (id) of the example
     * @return double a real containing the output value
     */
    public double getOutputAsReal(int pos) {
        return outputReal[pos];
    }

    /**
     * It returns an array with the maximum values of the attributes
     * @return double[] an array with the maximum values of the attributes
     */
    public double[] getemax() {
        return emax;
    }

    /**
     * It returns an array with the minimum values of the attributes
     * @return double[] an array with the minimum values of the attributes
     */
    public double[] getemin() {
        return emin;
    }

    /**
     * It gets the size of the data-set
     * @return int the number of examples in the data-set
     */
    public int getnData() {
        return nData;
    }

    /**
     * It gets the number of variables of the data-set (including the output)
     * @return int the number of variables of the data-set (including the output)
     */
    public int getnVars() {
        return nVars;
    }

    /**
     * It gets the number of input attributes of the data-set
     * @return int the number of input attributes of the data-set
     */
    public int getnInputs() {
        return nInputs;
    }

    /**
     * It gets the number of output attributes of the data-set (for example number of classes in classification)
     * @return int the number of different output values of the data-set
     */
    public int getnOutputs() {
        return nOutputs;
    }

    /**
     * This function checks if the attribute value is missing
     * @param i int Example id
     * @param j int Variable id
     * @return boolean True is the value is missing, else it returns false
     */
    public boolean isMissing(int i, int j) {
        return missing[i][j];
    }

    /**
     * It reads the whole input data-set and it stores each example and its associated output value in
     * local arrays to ease their use.
     * @param datasetFile String name of the file containing the dataset
     * @param train boolean It must have the value "true" if we are reading the training data-set
     * @throws IOException If there ocurs any problem with the reading of the data-set
     */
    public void readClassificationSet(String datasetFile, boolean train) throws
            IOException {
        try {
            // Load in memory a dataset that contains a classification problem
            IS.readSet(datasetFile, train);
            nData = IS.getNumInstances();
            nInputs = Attributes.getInputNumAttributes();
            nVars = nInputs + Attributes.getOutputNumAttributes();

            // outputIntegerheck that there is only one output variable
            if (Attributes.getOutputNumAttributes() > 1) {
                System.out.println(
                        "This algorithm can not process MIMO datasets");
                System.out.println(
                        "All outputs but the first one will be removed");
                System.exit(1);
            }
            boolean noOutputs = false;
            if (Attributes.getOutputNumAttributes() < 1) {
                System.out.println(
                        "This algorithm can not process datasets without outputs");
                System.out.println("Zero-valued output generated");
                noOutputs = true;
                System.exit(1);
            }

            // Initialice and fill our own tables
            X = new double[nData][nInputs];
            missing = new boolean[nData][nInputs];
            outputInteger = new int[nData];
            outputReal = new double[nData];
            output = new String[nData];

            // Maximum and minimum of inputs
            emax = new double[nInputs];
            emin = new double[nInputs];

            // All values are casted into double/integer
            nOutputs = 0;
            for (int i = 0; i < nData; i++) {
                Instance inst = IS.getInstance(i);
                for (int j = 0; j < nInputs; j++) {
                    X[i][j] = IS.getInputNumericValue(i, j); //inst.getInputRealValues(j);
                    missing[i][j] = inst.getInputMissingValues(j);
                    if (X[i][j] > emax[j] || i == 0) {
                        emax[j] = X[i][j];
                    }
                    if (X[i][j] < emin[j] || i == 0) {
                        emin[j] = X[i][j];
                    }
                }

                if (noOutputs) {
                    outputInteger[i] = 0;
                    output[i] = "";
                } else {
                    outputInteger[i] = (int) IS.getOutputNumericValue(i, 0);
                    output[i] = IS.getOutputNominalValue(i,0);
                }
                if (outputInteger[i] > nOutputs) {
                    nOutputs = outputInteger[i];
                }
            }
            nOutputs++;

        } catch (Exception e) {
            System.out.println("DBG: Exception in readSet");
            e.printStackTrace();
        }
        computeStatistics();
    }

    /**
     * It reads the whole input data-set and it stores each example and its associated output value in
     * local arrays to ease their use.
     * @param datasetFile String name of the file containing the dataset
     * @param train boolean It must have the value "true" if we are reading the training data-set
     * @throws IOException If there ocurs any problem with the reading of the data-set
     */
    public void readRegressionSet(String datasetFile, boolean train) throws
            IOException {
        try {
            // Load in memory a dataset that contains a regression problem
            IS.readSet(datasetFile, train);
            nData = IS.getNumInstances();
            nInputs = Attributes.getInputNumAttributes();
            nVars = nInputs + Attributes.getOutputNumAttributes();

            // outputIntegerheck that there is only one output variable
            if (Attributes.getOutputNumAttributes() > 1) {
                System.out.println(
                        "This algorithm can not process MIMO datasets");
                System.out.println(
                        "All outputs but the first one will be removed");
                System.exit(1);
            }
            boolean noOutputs = false;
            if (Attributes.getOutputNumAttributes() < 1) {
                System.out.println(
                        "This algorithm can not process datasets without outputs");
                System.out.println("Zero-valued output generated");
                noOutputs = true;
                System.exit(1);
            }

            // Initialice and fill our own tables
            X = new double[nData][nInputs];
            missing = new boolean[nData][nInputs];
            outputInteger = new int[nData];

            // Maximum and minimum of inputs
            emax = new double[nInputs];
            emin = new double[nInputs];

            // All values are casted into double/integer
            nOutputs = 0;
            for (int i = 0; i < nData; i++) {
                Instance inst = IS.getInstance(i);
                for (int j = 0; j < nInputs; j++) {
                    X[i][j] = IS.getInputNumericValue(i, j);
                    missing[i][j] = inst.getInputMissingValues(j);
                    if (X[i][j] > emax[j] || i == 0) {
                        emax[j] = X[i][j];
                    }
                    if (X[i][j] < emin[j] || i == 0) {
                        emin[j] = X[i][j];
                    }
                }

                if (noOutputs) {
                    outputReal[i] = outputInteger[i] = 0;
                } else {
                    outputReal[i] = IS.getOutputNumericValue(i, 0);
                    outputInteger[i] = (int) outputReal[i];
                }
            }
        } catch (Exception e) {
            System.out.println("DBG: Exception in readSet");
            e.printStackTrace();
        }
        computeStatistics();
    }


    /**
     * It copies the header of the dataset
     * @return String A string containing all the data-set information
     */
    public String copyHeader() {
        String p = new String("");
        p = "@relation " + Attributes.getRelationName() + "\n";
        p += Attributes.getInputAttributesHeader();
        p += Attributes.getOutputAttributesHeader();
        p += Attributes.getInputHeader() + "\n";
        p += Attributes.getOutputHeader() + "\n";
        p += "@data\n";
        return p;
    }

    /**
     * It transform the input space into the [0,1] range
     */
    public void normalize() {
        int atts = this.getnInputs();
        double maxs[] = new double[atts];
        for (int j = 0; j < atts; j++) {
            maxs[j] = 1.0 / (emax[j] - emin[j]);
        }
        for (int i = 0; i < this.getnData(); i++) {
            for (int j = 0; j < atts; j++) {
                if (isMissing(i, j)) {
                    ; //this process ignores missing values
                } else {
                    X[i][j] = (X[i][j] - emin[j]) * maxs[j];
                }
            }
        }
    }

    /**
     * It checks if the data-set has any real value
     * @return boolean True if it has some real values, else false.
     */
    public boolean hasRealAttributes() {
        return Attributes.hasRealAttributes();
    }

    public boolean hasNumericalAttributes(){
        return (Attributes.hasIntegerAttributes() || Attributes.hasRealAttributes());
    }

    /**
     * It checks if the data-set has any missing value
     * @return boolean True if it has some missing values, else false.
     */
    public boolean hasMissingAttributes(){
        return (this.sizeWithoutMissing() < this.getnData());
    }

    /**
     * It return the size of the data-set without having account the missing values
     * @return int the size of the data-set without having account the missing values
     */
    public int sizeWithoutMissing() {
        int tam = 0;
        for (int i = 0; i < nData; i++) {
            int j;
            for (j = 1; (j < nInputs) && (!isMissing(i, j)); j++) {
                ;
            }
            if (j == nInputs) {
                tam++;
            }
        }
        return tam;
    }

    /**
     * It computes the average and standard deviation of the input attributes
     */
    private void computeStatistics() {
        stdev = new double[this.getnInputs()];
        average = new double[this.getnInputs()];
        for (int i = 0; i < this.getnInputs(); i++) {
            average[i] = 0;
            for (int j = 0; j < this.getnData(); j++) {
                if (!this.isMissing(j,i)){
                    average[i] += X[j][i];
                }
            }
            average[i] /= this.getnData();
        }

        for (int i = 0; i < this.getnInputs(); i++) {
            double sum = 0;
            for (int j = 0; j < this.getnData(); j++) {
                if (!this.isMissing(j,i)){
                    sum += (X[j][i] - average[i]) * (X[j][i] - average[i]);
                }
            }
            sum /= this.getnData();
            stdev[i] = Math.sqrt(sum);
        }
    }

    /**
     * It return the standard deviation of an specific attribute
     * @param position int attribute id (position of the attribute)
     * @return double the standard deviation  of the attribute
     */
    public double stdDev(int position) {
        return stdev[position];
    }

    /**
     * It return the average of an specific attribute
     * @param position int attribute id (position of the attribute)
     * @return double the average of the attribute
     */
    public double average(int position) {
        return average[position];
    }

    /**
     * Computes the number of examples in each class
     */
    public void computeInstancesPerClass(){
        instancesCl = new int[nOutputs];
        for (int i = 0; i < this.getnData(); i++){
            instancesCl[this.outputInteger[i]]++;
        }
    }

    /**
     * It gets the number of examples of one class
     * @param clas int The referred class
     * @return int the number of examples of class "clas"
     */
    public int numberInstances(int clas){
        return instancesCl[clas];
    }

    /**
     * It gets the number of different values of an specific attribute
     * @param attribute int The referred attribute
     * @return int the number of values that the attribute "attribute" has
     */
    public int numberValues(int attribute){
        return Attributes.getInputAttribute(attribute).getNumNominalValues();
    }

    /**
     * It gets the string value for the class number specified in the parameter
     * @param intValue int The class number
     * @return String the class label corresponding to the class number
     */
    public String getOutputValue(int intValue){
        return Attributes.getOutputAttribute(0).getNominalValue(intValue);
    }

    /**
     * It gets an array that indicates the possible missing values for an example
     * @param pos int index of the example
     * @return boolean[] it contains "true" if the value is missing at position "i"
     */
    public boolean [] getMissing(int pos){
        return this.missing[pos];
    }

    /**
     * It gets the name of the variable
     * @param pos int Attribute position
     * @return String name of this attribute
     */
    public String varName(int pos) {
      return Attributes.getInputAttribute(pos).getName();
  }

}

