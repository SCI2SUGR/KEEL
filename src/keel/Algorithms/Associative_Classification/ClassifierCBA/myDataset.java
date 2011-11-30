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

package keel.Algorithms.Associative_Classification.ClassifierCBA;

import java.io.IOException;
import java.lang.String;
import keel.Dataset.*;

/**
 * It contains the methods to read a Classification/Regression Dataset.
 *
 * @author Alberto Fernández
 * @version 1.0
 */

public class myDataset {

    public static final int REAL = 0;
    public static final int INTEGER = 1;
    public static final int NOMINAL = 2;

    private int[][] X = null; //examples array
    private boolean[][] missing = null; //possible missing values
    private boolean[] nominal = null; //possible missing values
    private int[] outputInteger = null; //output of the data-set as integer values
    private double[] outputReal = null; //output of the data-set as double values
    private String[] output = null; //output of the data-set as string values
    private int[] emax; //max value of an attribute
    private int[] emin; //min value of an attribute

    private int nData; // Number of examples
    private int nVars; // Numer of variables
    private int nInputs; // Number of inputs
    private int nClasses; // Number of outputs

    private InstanceSet IS; //The whole instance set

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
    public int[][] getX() {
        return X;
    }

    /**
     * Output a specific example
     * @param pos position (id) of the example in the data-set
     * @return double[] the attributes of the given example
     */
    public int[] getExample(int pos) {
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
        String[] output = new String[this.output.length];
        for (int i = 0; i < this.output.length; i++) {
            output[i] = this.output[i];
        }
        return output;
    }

    /**
     * It returns the output value of the example "pos"
     * @param pos the position (id) of the example
     * @return String a string containing the output value
     */
    public String getOutputAsString(int pos) {
        return output[pos];
    }

    /**
     * It returns the input value of the example "pos" as string
     * @param var the variable (id) of the example
     * @param pos the position (id) of the example
     * @return String a string containing the input value
     */
    public String getInputAsString(int var, int pos) {
        return Attributes.getInputAttribute(var).getNominalValue(pos);
    }


    /**
     * It returns the output value of the example "pos"
     * @param pos the position (id) of the example
     * @return an integer containing the output value
     */
    public int getOutputAsInteger(int pos) {
        return outputInteger[pos];
    }

    /**
     * It returns the output value of the example "pos"
     * @param pos the position (id) of the example
     * @return a real containing the output value
     */
    public double getOutputAsReal(int pos) {
        return outputReal[pos];
    }

    /**
     * It returns an array with the maximum values of the attributes
     * @return double[] an array with the maximum values of the attributes
     */
    public int[] getemax() {
        return emax;
    }

    /**
     * It returns an array with the minimum values of the attributes
     * @return double[] an array with the minimum values of the attributes
     */
    public int[] getemin() {
        return emin;
    }

    public int getMax(int variable) {
        return emax[variable];
    }

    public int getMin(int variable) {
        return emin[variable];
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
    public int getnClasses() {
        return nClasses;
    }

    /**
     * This function checks if the attribute value is missing
     * @param i Example id
     * @param j Variable id
     * @return boolean True is the value is missing, else it returns false
     */
    public boolean isMissing(int i, int j) {
        return missing[i][j];
    }

    /**
     * This function checks if the attribute value is nominal
     * @param i attribute id
     * @return boolean True is the value is nominal, else it returns false
     */
    public boolean isNominal(int i) {
        return nominal[i];
    }

    /**
     * It reads the whole input data-set and it stores each example and its associated output value in
     * local arrays to ease their use.
     * @param datasetFile name of the file containing the dataset
     * @param train It must have the value "true" if we are reading the training data-set
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
                System.out.println("This algorithm can not process MIMO datasets");
                System.out.println("All outputs but the first one will be removed");
                System.exit(1);
            }
            boolean noOutputs = false;
            if (Attributes.getOutputNumAttributes() < 1) {
                System.out.println("This algorithm can not process datasets without outputs");
                System.out.println("Zero-valued output generated");
                noOutputs = true;
                System.exit(1);
            }

            // Initialice and fill our own tables
            X = new int[nData][nInputs];
            missing = new boolean[nData][nInputs];
            nominal = new boolean[nInputs];
            outputInteger = new int[nData];
            outputReal = new double[nData];
            output = new String[nData];

            // Maximum and minimum of inputs
			emax = new int[nInputs];      
			emin = new int[nInputs];
			for (int i = 0; i < nInputs; i++) {
				if (Attributes.getInputAttribute(i).getNumNominalValues() > 0) {
					emin[i] = 0;
					emax[i] = Attributes.getInputAttribute(i).getNumNominalValues() - 1;
				}
				else {
					System.out.println("This algorithm can not process datasets without outputs");
					System.out.println("Zero-valued output generated");
					noOutputs = true;
					System.exit(1);
				}

				nominal[i] = true;
			}

            // All values are casted into double/integer
            nClasses = 0;
            for (int i = 0; i < nData; i++) {
                Instance inst = IS.getInstance(i);
                for (int j = 0; j < nInputs; j++) {
                    X[i][j] = (int) IS.getInputNumericValue(i, j); 
                    missing[i][j] = inst.getInputMissingValues(j);
                    if (missing[i][j]){
                      X[i][j] = emin[j]-1;
                    }
                }

                if (noOutputs) {
                    outputInteger[i] = 0;
                    output[i] = "";
                } else {
                    outputInteger[i] = (int) IS.getOutputNumericValue(i, 0);
                    output[i] = IS.getOutputNominalValue(i, 0);
                }
                if (outputInteger[i] > nClasses) {
                    nClasses = outputInteger[i];
                }
            }
            nClasses++;
            System.out.println("Number of classes=" + nClasses);

        } catch (Exception e) {
            System.out.println("DBG: Exception in readSet");
            e.printStackTrace();
        }

        this.computeInstancesPerClass();
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
     * It checks if the data-set has any real value
     * @return boolean True if it has some real values, else false.
     */
    public boolean hasRealAttributes() {
        return Attributes.hasRealAttributes();
    }

    public boolean hasNumericalAttributes() {
        return (Attributes.hasIntegerAttributes() ||
                Attributes.hasRealAttributes());
    }

    /**
     * It checks if the data-set has any missing value
     * @return boolean True if it has some missing values, else false.
     */
    public boolean hasMissingAttributes() {
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

    public int size() {
        return nData;
    }



    public void computeInstancesPerClass() {
		int i;
        this.instancesCl = new int[this.nClasses];

		for (i = 0; i < this.nClasses; i++)  this.instancesCl[i] = 0;
        for (i = 0; i < this.getnData(); i++)  this.instancesCl[this.outputInteger[i]]++;
    }

    public int numberInstances(int clas) {
        return instancesCl[clas];
    }

    public int [] returnNumberInstances() {
        return instancesCl;
    }

    public int numberValues(int attribute) {
        return Attributes.getInputAttribute(attribute).getNumNominalValues();
    }

    public String getOutputValue(int intValue) {
        return Attributes.getOutputAttribute(0).getNominalValue(intValue);
    }

    public int getTipo(int variable) {
        if (Attributes.getAttribute(variable).getType() ==
            Attributes.getAttribute(0).INTEGER) {
            return this.INTEGER;
        }
        if (Attributes.getAttribute(variable).getType() ==
            Attributes.getAttribute(0).REAL) {
            return this.REAL;
        }
        if (Attributes.getAttribute(variable).getType() ==
            Attributes.getAttribute(0).NOMINAL) {
            return this.NOMINAL;
        }
        return 0;
    }

    /**
     * Devuelve el universo de discuros de las variables de entrada y salida
     * @return double[][] El rango minimo y maximo de cada variable
     */
    public int [][] returnRanks(){
      int [][] rangos = new int[this.getnVars()][2];
      for (int i = 0; i < this.getnInputs(); i++) {
		  rangos[i][0] = 0;
		  rangos[i][1] = Attributes.getInputAttribute(i).getNumNominalValues()-1;
      }

	  rangos[this.getnVars()-1][0] = 0;
      rangos[this.getnVars()-1][1] = Attributes.getOutputAttribute(0).getNumNominalValues()-1;
      
	  return rangos;
    }


    public String [] names(){
      String names[] = new String[nInputs];
      for (int i = 0; i < nInputs; i++){
        names[i] = Attributes.getInputAttribute(i).getName();
      }
      return names;
    }

    public String [] clases(){
      String clases[] = new String[nClasses];
      for (int i = 0; i < nClasses; i++){
        clases[i] = Attributes.getOutputAttribute(0).getNominalValue(i);
      }
      return clases;

  }

}

