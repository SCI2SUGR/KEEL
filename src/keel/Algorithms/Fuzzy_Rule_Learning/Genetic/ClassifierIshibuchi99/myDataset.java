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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierIshibuchi99;

/**
 * <p>
 * @author Written by Alberto Fernández (University of Granada) 01/01/2007
 * @author Modified by Francisco José Berlanga (University of Jaén) 09/12/2008 
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */

import java.io.IOException;

import keel.Dataset.*;

public class myDataset {
/**	
 * <p>
 * It contains the methods to read a Classification/Regression Dataset
 * </p>
 */

    public static final int REAL = 0;
    public static final int ENTERO = 1;
    public static final int NOMINAL = 2;

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
    private int nClasses; // Number of outputs

    private InstanceSet IS; //The whole instance set

    private double stdev[], average[]; //standard deviation and average of each attribute
    private int instancesCl[];

    /**
     * <p>    
     * Init a new set of instances
     * </p>     
     */
    public myDataset() {
        IS = new InstanceSet();
    }

    /**
     * <p>        
     * Outputs an array of examples with their corresponding attribute values.
     * </p>          
     * @return double[][] an array of examples with their corresponding attribute values
     */
    public double[][] getX() {
        return X;
    }

    /**
     * <p>        
     * Output a specific example
     * </p>          
     * @param pos int position (id) of the example in the data-set
     * @return double[] the attributes of the given example
     */
    public double[] getExample(int pos) {
        return X[pos];
    }

    /**
     * <p>        
     * Returns the output of the data-set as integer values
     * </p>          
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
     * <p>        
     * Returns the output of the data-set as real values
     * </p>          
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
     * <p>        
     * Returns the output of the data-set as nominal values
     * </p>          
     * @return String[] an array of nominal values corresponding to the output values of the dataset
     */
    public String[] getOutputAsString() {
        String [] output = new String[this.output.length];
        for (int i = 0; i < this.output.length; i++) {
            output[i] = this.output[i];
        }
        return output;
    }

    /**
     * <p>        
     * It returns the output value of the example "pos"
     * </p>          
     * @param pos int the position (id) of the example
     * @return String a string containing the output value
     */
    public String getOutputAsString(int pos) {
        return output[pos];
    }

    /**
     * <p>        
     * It returns the output value of the example "pos"
     * </p>          
     * @param pos int the position (id) of the example
     * @return int an integer containing the output value
     */
    public int getOutputAsInteger(int pos) {
        return outputInteger[pos];
    }

    /**
     * <p>        
     * It returns the output value of the example "pos"
     * </p>          
     * @param pos int the position (id) of the example
     * @return double a real containing the output value
     */
    public double getOutputAsReal(int pos) {
        return outputReal[pos];
    }

    /**
     * <p>        
     * It returns an array with the maximum values of the attributes
     * </p>          
     * @return double[] an array with the maximum values of the attributes
     */
    public double[] getemax() {
        return emax;
    }

    /**
     * <p>        
     * It returns an array with the minimum values of the attributes
     * </p>          
     * @return double[] an array with the minimum values of the attributes
     */
    public double[] getemin() {
        return emin;
    }

    /**
     * <p>    
     * It returns the maximum value of the attribute "variable"
     * </p>        
     * @param variable int Variable id
     * @return int the maximum value of the attribute "variable"
     */  
    public double getMax(int variable){
        return emax[variable];
    }

    /**
     * <p>    
     * It returns the minimum value of the attribute "variable"
     * </p>          
     * @param variable int Variable id     
     * @return int the minimum value of the attribute "variable"
     */        
    public double getMin(int variable){
        return emin[variable];
    }

    /**
     * <p>        
     * It gets the size of the data-set
     * </p>          
     * @return int the number of examples in the data-set
     */
    public int getnData() {
        return nData;
    }

    /**
     * <p>        
     * It gets the number of variables of the data-set (including the output)
     * </p>          
     * @return int the number of variables of the data-set (including the output)
     */
    public int getnVars() {
        return nVars;
    }

    /**
     * <p>        
     * It gets the number of input attributes of the data-set
     * </p>          
     * @return int the number of input attributes of the data-set
     */
    public int getnInputs() {
        return nInputs;
    }

    /**
     * <p>        
     * It gets the number of output attributes of the data-set (for example number of classes in classification)
     * </p>          
     * @return int the number of different output values of the data-set
     */
    public int getnClasses() {
        return nClasses;
    }

    /**
     * <p>        
     * This function checks if the attribute value is missing
     * </p>          
     * @param i int Example id
     * @param j int Variable id
     * @return boolean True is the value is missing, else it returns false
     */
    public boolean isMissing(int i, int j) {
        return missing[i][j];
    }

    /**
     * <p>        
     * It reads the whole input data-set and it stores each example and its associated output value in
     * local arrays to ease their use.
     * </p>          
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
            for (int i = 0; i < nInputs; i++){
              emax[i] = Attributes.getAttribute(i).getMaxAttribute();
              emin[i] = Attributes.getAttribute(i).getMinAttribute();
            }
            // All values are casted into double/integer
            nClasses = 0;
            for (int i = 0; i < nData; i++) {
                Instance inst = IS.getInstance(i);
                for (int j = 0; j < nInputs; j++) {
                    X[i][j] = IS.getInputNumericValue(i, j); //inst.getInputRealValues(j);
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
        computeStatistics();
        this.computeInstancesPerClass();
    }


    /**
     * <p>        
     * It reads the whole input data-set and it stores each example and its associated output value in
     * local arrays to ease their use.
     * </p>          
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
            nClasses = 0;
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
     * <p>        
     * It copies the header of the dataset
     * </p>          
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
     * <p>        
     * It transform the input space into the [0,1] range
     * </p>          
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
     * <p>        
     * It checks if the data-set has any real value
     * </p>          
     * @return boolean True if it has some real values, else false.
     */
    public boolean hasRealAttributes() {
        return Attributes.hasRealAttributes();
    }

    /**
     * <p>    
     * It checks if the data-set has any numerical (real or integer) value
     * </p>          
     * @return boolean True if it has some numerical values, else false.     
     */  
    public boolean hasNumericalAttributes(){
        return (Attributes.hasIntegerAttributes() || Attributes.hasRealAttributes());
    }

    /**
     * <p>        
     * It checks if the data-set has any missing value
     * </p>          
     * @return boolean True if it has some missing values, else false.
     */
    public boolean hasMissingAttributes(){
        return (this.sizeWithoutMissing() < this.getnData());
    }

    /**
     * <p>        
     * It return the size of the data-set without having account the missing values
     * </p>          
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
     * <p>        
     * It computes the average and standard deviation of the input attributes
     * </p>          
     */
    private void computeStatistics() {
      stdev = new double[this.getnVars()];
      average = new double[this.getnVars()];

      for (int i = 0; i < this.getnInputs(); i++) {
        average[i] = 0;
        for (int j = 0; j < this.getnData(); j++) {
          if (!this.isMissing(j, i)) {
            average[i] += X[j][i];
          }
        }
        average[i] /= this.getnData();
      }
      average[average.length - 1] = 0;
      for (int j = 0; j < outputReal.length; j++) {
        average[average.length - 1] += outputReal[j];
      }
      average[average.length - 1] /= outputReal.length;

      for (int i = 0; i < this.getnInputs(); i++) {
        double sum = 0;
        for (int j = 0; j < this.getnData(); j++) {
          if (!this.isMissing(j, i)) {
            sum += (X[j][i] - average[i]) * (X[j][i] - average[i]);
          }
        }
        sum /= this.getnData();
        stdev[i] = Math.sqrt(sum);
      }

      double sum = 0;
      for (int j = 0; j < outputReal.length; j++) {
        sum += (outputReal[j] - average[average.length - 1]) *
            (outputReal[j] - average[average.length - 1]);
      }
      sum /= outputReal.length;
      stdev[stdev.length - 1] = Math.sqrt(sum);

    }


    /**
     * <p>        
     * It return the standard deviation of an specific attribute
     * </p>          
     * @param position int attribute id (position of the attribute)
     * @return double the standard deviation  of the attribute
     */
    public double stdDev(int position) {
        return stdev[position];
    }

    /**
     * <p>        
     * It return the average of an specific attribute
     * </p>          
     * @param position int attribute id (position of the attribute)
     * @return double the average of the attribute
     */
    public double average(int position) {
        return average[position];
    }

    /**
     * <p>    
     * It computes the number of intances per class
     * </p>          
     */     
    public void computeInstancesPerClass(){
        instancesCl = new int[nClasses];
        for (int i = 0; i < this.getnData(); i++){
            instancesCl[this.outputInteger[i]]++;
        }
    }

    /**
     * <p>    
     * It computes the number of intances for the class "clas"
     * </p>
     * @param clas String Name of the class             
     * @return int the number of intances for the class "clas"     
     */     
    public int numberInstances(int clas){
        return instancesCl[clas];
    }

    /**
     * <p>    
     * It returns the number of nominal values for the atributte "attribute"
     * </p>     
     * @param attribute int attribute id (position of the attribute)     
     * @return int the number of nominal values for the atributte "attribute"          
     */     
    public int numberValues(int attribute){
        return Attributes.getInputAttribute(attribute).getNumNominalValues();
    }

    /**
     * <p>    
     * It returns the nominal value for the class in the position "intValue"
     * </p>   
     * @param intValue int class id (position of the class)          
     * Return String the nominal value for the class in the position "intValue"            
     */     
    public String getOutputValue(int intValue){
        return Attributes.getOutputAttribute(0).getNominalValue(intValue);
    }

    /**
     * <p>    
     * It returns the type for the attribute "variable"
     * </p>    
     * @param variable int attribute id (position of the attribute)           
     * Return int 1 if "variable" is an integer attribute; 0 if "variable" is a real attribute;
     * 2 if "variable" is a nominal attribute; 0 (real) by default; 
     */      
    public int getType(int variable){
        if (Attributes.getAttribute(variable).getType() == Attributes.getAttribute(0).INTEGER)
            return this.ENTERO;
        if (Attributes.getAttribute(variable).getType() == Attributes.getAttribute(0).REAL)
            return this.REAL;
        if (Attributes.getAttribute(variable).getType() == Attributes.getAttribute(0).NOMINAL)
            return this.NOMINAL;
        return 0;
    }

    /**
     * <p>    
     * It returns an array showing if the value of each attribute for the instance "pos" is missing (TRUE) or not (FALSE)
     * </p>        
     * @param pos int Instance id         
     * Return bolean[] an array showing if the value of each attribute for the instance "pos" is missing (TRUE) or not (FALSE)     
     */   
    public boolean [] getMissing(int pos){
        return this.missing[pos];
    }

}

