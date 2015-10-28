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

package keel.Algorithms.Decision_Trees.C45_Binarization;



import java.io.IOException;

import keel.Dataset.*;

import java.util.Vector;

/**
 * <p>Title: Dataset</p>
 *
 * <p>Description: It contains the methods to read a Classification/Regression Dataset</p>
 * <p>Company: KEEL </p>
 *
 * @author Alberto Fernández (University of Jaén - 27-09-2010)
 * @version 1.2
 */
public class myDataset {

    /**
     * Number to represent type of variable real or double.
     */
    public static final int REAL = Attribute.REAL;

    /**
     * Number to represent type of variable integer.
     */
    public static final int INTEGER = Attribute.INTEGER;

    /**
     * Number to represent type of variable nominal.
     */
    public static final int NOMINAL = Attribute.NOMINAL;

    /**
     * examples array.
     */
    protected double[][] X = null; 
  private double[][] X_normalized = null;
  private boolean[][] missing = null; //possible missing values
  private int[] outputInteger = null; //output of the data-set as integer values
  private double[] outputReal = null; //output of the data-set as double values
  private String[] output = null; //output of the data-set as string values
  private double[] emax; //max value of an attribute
  private double[] emin; //min value of an attribute

  private int nData; // Number of examples
  private int nVars; // Number of variables
  private int nInputs; // Number of inputs
  private int nClasses; // Number of outputs
  private int [] list_of_classes;

  private InstanceSet IS; //The whole instance set
  private Attribute[] inputs_att;
  private Attribute output_att;

  private double stdev[], average[]; //standard deviation and average of each attribute
  private double stdevPerClass[][], averagePerClass[][];
  private int instancesCl[];
  
  private int[] types;
  private String[] classes;
  private String[] variables;
  private String[][] nominals;
  private String[][] nominales_head;

  /**
   * Init a new set of instances
   */
  public myDataset() {
    IS = new InstanceSet();
  }
  
  /**
   * It generates a new binary dataset for the OVO scheme
   * @param copy the original training dataset
   * @param class_1 first class
   * @param class_2 second class
   */
  public myDataset(myDataset copy, int class_1, int class_2) {
	  nVars = copy.getnVars();
	  nInputs = copy.getnInputs();
	  nClasses = 2; //copia.getnClasses();
	  classes = copy.classes.clone();
	  variables = copy.variables.clone();
	  types = copy.types.clone();
	  nominals = new String[nInputs][];
	  for (int i = 0; i < nominals.length; i++) {
		  nominals[i] = copy.nominals[i].clone();
	  }
	  double[][] X_aux = new double[copy.size()][copy.getnInputs()];
	  int[] outputInteger_aux = new int[copy.size()];
	  String[] output_aux = new String[copy.size()];
	  nData = 0;
	  emax = new double[copy.getnInputs()];
	  emin = new double[copy.getnInputs()];
	  for (int i = 0; i < emax.length; i++) {
		  emax[i] = Double.MIN_VALUE;
		  emin[i] = Double.MAX_VALUE;
	  }
	  for (int i = 0; i < copy.size(); i++) {
		  if ( (copy.getOutputAsInteger(i) == class_1) ||
				  (copy.getOutputAsInteger(i) == class_2)) {
			  //X_aux[nData] = copia.getExample(i).clone();
			  double[] auxiliar = copy.getExample(i).clone();
			  for (int j = 0; j < emax.length; j++) {
				  X_aux[nData][j] = auxiliar[j];
				  if (emax[j] < auxiliar[j]) {
					  emax[j] = auxiliar[j];
				  }
				  if (emin[j] > auxiliar[j]) {
					  emin[j] = auxiliar[j];
				  }
			  }
			  outputInteger_aux[nData] = copy.getOutputAsInteger(i);
			  output_aux[nData] = copy.getOutputAsString(i);
			  nData++;
		  }
	  }
	  nominales_head = new String[nominals.length][];
	    for (int i = 0; i < nominals.length; i++) {
	      if (types[i] == this.NOMINAL) {
	        boolean[] auxi = new boolean[nominals[i].length];
	        for (int j = 0; j < auxi.length; j++) {
	          auxi[j] = false;
	        }
	        for (int j = 0; j < nData; j++) {
	          auxi[ (int) X_aux[j][i]] = true;
	        }
	        int contador = 0;
	        for (int j = 0; j < auxi.length; j++) {
	          if (auxi[j]) {
	            contador++;
	          }
	        }
	        nominales_head[i] = new String[contador];
	        contador = 0;
	        for (int j = 0; j < auxi.length; j++) {
	          if (auxi[j]) {
	            nominales_head[i][contador++] = nominals[i][j];
	          }
	        }
	      }
	      else {
	        nominales_head[i] = new String[1];
	        nominales_head[i][0] = "?";
	      }
	    }
	  X = new double[nData][nInputs];
	  outputInteger = new int[nData];
	  output = new String[nData];
	  for (int i = 0; i < nData; i++) {
		  X[i] = X_aux[i].clone();
		  outputInteger[i] = outputInteger_aux[i];
		  output[i] = output_aux[i];
	  }
	  list_of_classes = new int[2];
	  list_of_classes[0] = class_1;
	  list_of_classes[1] = class_2;
	  copy.computeInstancesPerClass(); //para el cost_sensitive learning

	  instancesCl = new int[copy.getnClasses()];
	  for (int i = 0; i < instancesCl.length; i++) {
		  instancesCl[i] = copy.numberInstances(i);
	  }
  }

  /**
   * It generates a new binary dataset for the OVA scheme
   * @param copia the original training dataset
   * @param positiva primary class (against the rest)
   */  
  public myDataset(myDataset copia, int positiva) {
	  nVars = copia.getnVars();
	  nInputs = copia.getnInputs();
	  nClasses = 2; //copia.getnClasses();
	  classes = copia.classes.clone();
	  variables = copia.variables.clone();
	  types = copia.types.clone();
	  nominals = new String[nInputs][];
	  for (int i = 0; i < nominals.length; i++) {
		  nominals[i] = copia.nominals[i].clone();
	  }
	  nominales_head = nominals.clone();
	  emax = copia.getemax().clone();
	  emin = copia.getemin().clone();
	  nData = copia.size();
	  X = new double[nData][nInputs];
	  X = copia.getX().clone();
	  outputInteger = new int[nData];
	  output = new String[nData];
	  int positivos = 0;
	  for (int i = 0; i < nData; i++){
		  outputInteger[i] = 1;
		  output[i] = "negative";
		  if (copia.getOutputAsInteger(i) == positiva){
			  positivos++;
			  outputInteger[i] = 0;
			  output[i] = "positive";
		  }
	  }
	  list_of_classes = new int[2];
	  list_of_classes[0] = 0;//clase_1;
	  list_of_classes[1] = 1;//clase_2;
	  instancesCl = new int[2];
	  instancesCl[0] = positivos;
	  instancesCl[1] = nData - positivos;
	  if (instancesCl[1] < 0) instancesCl[1] = positivos - nData;
	  classes[0] = "positive";
	  classes[1] = "negative";  
  }
  
  /**
   * It generates a new binary dataset for the OVO scheme (NESTING)
   * @param copy the original training dataset
   * @param class_1 first class
   * @param class_2 second class
   * @param ties number of ties
   */
  public myDataset(myDataset copy, int class_1, int class_2, int[] ties){
    nVars = copy.getnVars();
    nInputs = copy.getnInputs();
    nClasses = copy.getnClasses();
    double [][] X_aux = new double[copy.size()][copy.getnInputs()];
    int [] outputInteger_aux = new int [copy.size()];
    String [] output_aux = new String [copy.size()];
    nData = 0;
    for (int i = 0; i < copy.size(); i++){
      if (((copy.getOutputAsInteger(i) == class_1)||(copy.getOutputAsInteger(i) == class_2)) && ties[i] == 1){
        X_aux[nData] = copy.getExample(i).clone();
        outputInteger_aux[nData] = copy.getOutputAsInteger(i);
        output_aux[nData] = copy.getOutputAsString(i);
        nData++;
      }
    }
    X = new double[nData][nInputs];
    outputInteger = new int[nData];
    output = new String[nData];
    for (int i = 0; i < nData; i++){
      X[i] = X_aux[i].clone();
      outputInteger[i] = outputInteger_aux[i];
      output[i] = output_aux[i];
    }
    list_of_classes = new int[2];
    list_of_classes[0] = class_1;
    list_of_classes[1] = class_2;
    copy.computeInstancesPerClass(); //para el cost_sensitive learning
    instancesCl = new int[copy.getnClasses()];
    for (int i = 0; i < instancesCl.length; i++){
      instancesCl[i] = copy.numberInstances(i);
    }
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
    String[] output = new String[this.output.length];
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
     * It returns the maximum value of the given attribute
     * 
     * @param variable the index of the attribute
     * @return the maximum value of the given attribute
     */
    public double getMax(int variable) {
    return emax[variable];
  }

    /**
     * It returns the minimum value of the given attribute
     * 
     * @param variable the index of the attribute
     * @return the minimum value of the given attribute
     */
    public double getMin(int variable) {
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
      IS = new InstanceSet();
      IS.readSet(datasetFile, train);
      IS.setAttributesAsNonStatic();
      inputs_att = IS.getAttributeDefinitions().getInputAttributes();
      output_att = IS.getAttributeDefinitions().getOutputAttribute(0);
      
      nData = IS.getNumInstances();
      nInputs = IS.getAttributeDefinitions().getInputNumAttributes();
      nVars = nInputs + IS.getAttributeDefinitions().getOutputNumAttributes();

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
      for (int i = 0; i < nInputs; i++) {
        emax[i] = Attributes.getAttribute(i).getMaxAttribute();
        emin[i] = Attributes.getAttribute(i).getMinAttribute();
      }
      // All values are casted into double/integer
      nClasses = 0;
      int datosMal, datos, aux;
      datosMal = datos = aux = 0;
      for (int i = 0; i < nData; i++) {
        Instance inst = IS.getInstance(i);
        for (int j = 0; j < nInputs; j++) {
          X[datos][j] = IS.getInputNumericValue(i, j); //inst.getInputRealValues(j);
          missing[i][j] = inst.getInputMissingValues(j);
          if (missing[i][j]) {
            j = nInputs; //Anulo el ejemplo (ignore missing) //X[i][j] = emin[j] - 1;
            datosMal++;
          }
        }
        if (noOutputs) {
          outputInteger[datos] = 0;
          output[datos] = "";
        }
        else {
          outputInteger[datos] = (int) IS.getOutputNumericValue(i, 0);
          output[datos] = IS.getOutputNominalValue(i, 0);
        }
        if (outputInteger[datos] > nClasses) {
          nClasses = outputInteger[datos];
        }
        datos++;
        if (datosMal > aux){
          datos--; //Anulo el ejemplo (ignore missing)
          aux = datosMal;
        }
      }
      nData -= datosMal;
      nClasses++;
      System.out.println("Number of classes=" + nClasses);

    }
    catch (Exception e) {
      System.out.println("DBG: Exception in readSet");
      e.printStackTrace();
    }
    list_of_classes = new int[nClasses];
    for (int i = 0; i < nClasses; i++){
      list_of_classes[i] = i;
    }
    computeStatistics();
    computeInstancesPerClass();
    variables = new String[nVars];
    classes = new String[nClasses];
    types = new int[nInputs];
    nominals = new String[nInputs][];
    nominales_head = new String[nInputs][];
    for (int i = 0; i < nInputs; i++) {
      variables[i] = inputs_att[i].getName();
      types[i] = inputs_att[i].getType();
      //System.err.print("Atributo["+i+"]: ");
      if (inputs_att[i].getNumNominalValues() > 0) {
        nominals[i] = new String[inputs_att[i].getNumNominalValues()];
        nominales_head[i] = new String[inputs_att[i].getNumNominalValues()];
        for (int j = 0; j < nominals[i].length; j++) {
          nominals[i][j] = inputs_att[i].getNominalValue(j);
          //System.err.print(", "+nominales[i][j]);
          nominales_head[i][j] = inputs_att[i].getNominalValue(j);
        }
      }
      else {
        nominals[i] = new String[1];
        nominals[i][0] = "?";
        nominales_head[i] = new String[1];
        nominales_head[i][0] = "?";
      }
      //System.err.println("");
    }
    variables[nInputs] = output_att.getName();
    for (int i = 0; i < nClasses; i++) {
      classes[i] = output_att.getNominalValue(i);
    }
  }

  /**
   * It transforms the input space into the [0,1] range
   */
  public void normalize() {
    int atts = this.getnInputs();
    double maxs[] = new double[atts];
    for (int j = 0; j < atts; j++) {
        if (Attributes.getAttribute(j).getType() == Attribute.NOMINAL)
        {
            maxs[j] = 1.0 /
                    ((Attributes.getAttribute(j).getNumNominalValues()-1) - 0);
        }
        else
            maxs[j] = 1.0 / (emax[j] - emin[j]);
    }
    for (int i = 0; i < this.getnData(); i++) {
      for (int j = 0; j < atts; j++) {
        if (isMissing(i, j)) {
          ; //this process ignores missing values
        }
        else {
            if (Attributes.getAttribute(j).getType() == Attribute.NOMINAL)
            {
                X[i][j] = (X[i][j] - 0) * maxs[j];
            }
            else
                X[i][j] = (X[i][j] - emin[j]) * maxs[j];
        }
      }
    }
  }

   /**
   * It transforms the input space into the [0,1] range, but it is stored in X_normalized.
   */
    public void normalize_statistics() {
    int atts = this.getnInputs();
    double maxs[] = new double[atts];
    X_normalized =  new double[nData][nInputs];
    for (int j = 0; j < atts; j++) {
        if (Attributes.getAttribute(j).getType() == Attribute.NOMINAL)
        {
            maxs[j] = 1.0 /
                    ((Attributes.getAttribute(j).getNumNominalValues()-1) - 0);
        }
        else
            maxs[j] = 1.0 / (emax[j] - emin[j]);
    }
    for (int i = 0; i < this.getnData(); i++) {
      for (int j = 0; j < atts; j++) {
        if (isMissing(i, j)) {
          ; //this process ignores missing values
        }
        else {
            if (Attributes.getAttribute(j).getType() == Attribute.NOMINAL)
            {
                X_normalized[i][j] = (X[i][j] - 0) * maxs[j];
            }
            else
                X_normalized[i][j] = (X[i][j] - emin[j]) * maxs[j];
        }
      }
    }
  }

    /**
   * It computes the average and standard deviation of the input attributes
   */
  public void computeStatisticsPerClass() {
    this.normalize_statistics();
    stdevPerClass = new double[nClasses][this.getnInputs()];
    averagePerClass = new double[nClasses][this.getnInputs()];
    int c;

    for (int i = 0; i < this.getnInputs(); i++) {
      for (int j = 0; j < this.getnClasses(); j++)
          averagePerClass[j][i] = 0;
      for (int j = 0; j < this.getnData(); j++) {
        c = this.outputInteger[j];
        if (!this.isMissing(j, i)) {
          averagePerClass[c][i] += X_normalized[j][i];
        }
      }
      for (int j = 0; j < this.getnClasses(); j++)
      {
        averagePerClass[j][i] /= this.numberOfExamples(j);
        if (Double.isNaN(averagePerClass[j][i]))
            averagePerClass[j][i] = this.average(i);
      }
    }

    for (int i = 0; i < this.getnInputs(); i++) {
      double[] sum = new double[nClasses];
      for (int j = 0; j < this.getnClasses(); j++)
          sum[j] = 0;
      for (int j = 0; j < this.getnData(); j++) {
        c = this.outputInteger[j];
        if (!this.isMissing(j, i)) {
          sum[c] += (X_normalized[j][i] - averagePerClass[c][i]) * (X_normalized[j][i] - averagePerClass[c][i]);
        }
      }
      for (int j = 0; j < this.getnClasses(); j++)
      {
        sum[j] /= this.numberOfExamples(j);
        stdevPerClass[j][i] = Math.sqrt(sum[j]);
      }
    }
  }

    /**
     * Returns the average values per class.
     * @return the average values per class.
     */
    public double[][] getAveragePerClass()
  {
      return this.averagePerClass;
  }

    /**
     * Returns the standard deviation per class.
     * @return the standard deviation per class.
     */
    public double[][] getStdPerClass()
  {
      return this.stdevPerClass;
  }

  /**
   * It checks if the data-set has any real value
   * @return boolean True if it has some real values, else false.
   */
  public boolean hasRealAttributes() {
    return Attributes.hasRealAttributes();
  }

    /**
     * It checks if the data-set has any numerical value
     * @return boolean True if it has some numerical values, else false.
     */
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

    /**
     * It return the size of the data-set
     * 
     * @return the size of the data-set
     */
    public int size() {
    return nData;
  }

  /**
   * It computes the average and standard deviation of the input attributes.
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
     * It computes the number of examples per class.
     * 
     */
    public void computeInstancesPerClass() {
    instancesCl = new int[nClasses];
    for (int i = 0; i < this.getnData(); i++) {
      instancesCl[this.outputInteger[i]]++;
    }
  }

    /**
     * It returns the number of instances in the data set for a given class.
     * @param clas int Given class.
     * @return int Number of instances for the given class.
     */
    public int numberInstances(int clas) {
    return instancesCl[clas];
  }

    /**
     * Function to get the number of different feasible values for a given attribute 
     * @param attribute int Given attribute
     * @return int Number of different feasible values for a given attribute
     */
    public int numberValues(int attribute) {
    return Attributes.getInputAttribute(attribute).getNumNominalValues();
  }

    /**
     * It returns the output value (string) which matchs with a given integer value 
     * @param intValue int Given value
     * @return String Output value in an understanding way
     */
    public String getOutputValue(int intValue) {
	  return classes[intValue];
  }

    /**
     * It returns the type of an attribute
     * @param variable Given attribute
     * @return int Type of the attribute, it is an integer which corresponds to an enummerate field
     */
    public int getTipo(int variable) {
	  return types[variable];
  }

  /**
     * Returns the minimum and maximum values of every attributes as a matrix.
     * The matrix has a size of number_of_attributes x 2 ([nAttributes][2]).
     * The minimum value is located at the first position of each array and the maximum, at the second.
     * @return Matrix which stores the minimum and maximum values of every attributes.
     */
  public double[][] getRanges() {
	  double[][] rangos = new double[this.getnVars()][2];
	  for (int i = 0; i < this.getnInputs(); i++) {
		  rangos[i][0] = emin[i];
		  rangos[i][1] = emax[i];
	  }
	  rangos[this.getnVars() - 1][0] = 0;
	  rangos[this.getnVars() - 1][1] = nClasses - 1;
	  return rangos;
  }

  /**
   * It returns the attribute name of a given variable
   * @param pos variable id.
   * @return attribute name of a given variable
   */
  public String varName(int pos) {
    return Attributes.getInputAttribute(pos).getName();
  }

  /**
   * Returns the nominal value for a class represented by the integer given.
   * @param clase integer representation of the class.
   * @return String nominal value for the class
   */
  public String className(int clase) {
	return classes[clase];
    //return Attributes.getOutputAttribute(0).getNominalValue(clase);
  }

  /**
   * Uniform width discretization
   * @param intervalos int Number of intervals
   */
  public void discretize(int intervalos){
    for (int i = 0; i < nInputs; i++){
      if (this.getTipo(i) == this.REAL) { //si es real
        double corte = (emax[i] - emin[i])/intervalos;
        for (int j = 0; j < this.size(); j++){
          double acum = emin[i] + corte;
          boolean salir = false;
          for (int k = 0; (k < intervalos)&&(!salir); k++){
            if (X[j][i] < acum){
              X[j][i] = k;
              salir = true;
            }
            acum += corte;
          }
        }
      }
    }
  }

  /**
   * It returns the names for all input variables
   * @return names for all input variables
   */
  public String [] names(){
    String nombres[] = new String[nInputs];
    for (int i = 0; i < nInputs; i++){
      nombres[i] = Attributes.getInputAttribute(i).getName();
    }
    return nombres;
  }

    /**
     * Returns a real representation of a attribute's nominal value given as argument.
     * @param atributo Attribute given.
     * @param valorNominal Nominal value of the attribute given.
     * @return Returns a real representation of a attribute's nominal value.
     */
    public static double realValue(int atributo, String valorNominal){
    Vector nominales = Attributes.getInputAttribute(atributo).getNominalValuesList();
    int aux = nominales.indexOf(valorNominal);
    return 1.0*aux;
  }

    /**
     * Returns a numeric representation of a class nominal value given as argument.
     * @param valorNominal class nominal value.
     * @return Numeric representation of a class nominal value.
     */
    public int numericClass(String valorNominal){
    Vector nominales = Attributes.getOutputAttribute(0).getNominalValuesList();
    int aux = nominales.indexOf(valorNominal);
    return aux;
  }

    /**
     * Returns a nominal representation of a attribute's real value given as argument.
     * @param atributo Attribute given.
     * @param valorReal Real value of the attribute given.
     * @return Returns a nominal representation of a attribute's real value.
     */
    public static String nominalValue(int atributo, double valorReal){
    Vector nominales = Attributes.getInputAttribute(atributo).getNominalValuesList();
    return (String)nominales.get((int)valorReal);
  }

  /**
   * It returns the number of nominal values for a given variable
   * @param attribute variable id
   * @return the number of nominal values for a given variable
   */
  public int totalNominals(int attribute){
    return Attributes.getInputAttribute(attribute).getNumNominalValues();
  }

  /**
   * It returns the most frequent class in the dataset
   * @return the most frequent class in the dataset
   */
  public String mostFrequentClass(){
	  int claseMayoritaria = 0;
	  for (int i = 1; i < nClasses; i++){
		  if (instancesCl[claseMayoritaria] < instancesCl[i]){
			  claseMayoritaria = i;
		  }
	  }
	  return this.getOutputValue(claseMayoritaria);
  }

  /**
   * It returns the number of minority class examples
   * @return number of minority class examples
   */
  public int n_minoritaria() {
	  if (this.numberInstances(list_of_classes[0]) >
	  this.numberInstances(list_of_classes[1])) {
		  return this.numberInstances(list_of_classes[1]);
	  }
	  else {
		  return this.numberInstances(list_of_classes[0]);
	  }
  }    

  /**
   * It computes the Imbalance Ratio
   * @return the ratio between negative and positive examples (max/min)
   */
  public double ir(){
	  if (this.numberInstances(list_of_classes[0]) >
	  this.numberInstances(list_of_classes[1])) {
		  return ( (double)this.numberInstances(list_of_classes[0]) /
				  this.numberInstances(list_of_classes[1]));
	  }
	  else {
		  return ( (double)this.numberInstances(list_of_classes[1]) /
				  this.numberInstances(list_of_classes[0]));
	  }
  }  
  
  /**
   * It obtains the number of examples for the i-th class
   * @param clase int class id
   * @return int number of examples belonging to that class
   */
  public int numberOfExamples(int clase){
    int ejemplos = 0;
    for (int i = 0; i < outputInteger.length; i++){
      if (clase == outputInteger[i])
        ejemplos++;
    }
    return ejemplos;
  }

  /**
   * To compute whether the dataset is empty
   * @return True if is empty, false otherwise
   */
  public boolean empty(){
    if ((this.numberInstances(list_of_classes[0]) == 0)||(this.numberInstances(list_of_classes[1]) == 0)){
      return true;
    }
    return false;
  }

  /**
   * It prints the header of a dataset into KEEL format
   * @return the header of a dataset into KEEL format
   */
  public String doHeader() {
	  String cadena = new String("");
	  cadena += "@relation unknown\n";
	  for (int i = 0; i < this.nInputs; i++) {
		  //Attribute a = inputs_att[i];
		  cadena += "@attribute " + variables[i];
		  if (types[i] == this.INTEGER) {
			  cadena += " integer [" + (int) emin[i] + "," + (int) emax[i] + "]\n";
		  }
		  else if (types[i] == this.REAL) {
			  cadena += " real [" + emin[i] + "," + emax[i] + "]\n";
		  }
		  else {
			  cadena += " {";
			  int j;
			  for (j = 0; j < nominales_head[i].length - 1; j++) {
				  cadena += nominales_head[i][j] + ",";
			  }
			  cadena += nominales_head[i][j] + "}\n";
		  }
	  }
	  //Attribute a = output_att;
	  cadena += "@attribute " + variables[nInputs];
	  cadena += " {";
	  int i = 0;
	  for (; i < nClasses-1; i++){
		  cadena += classes[list_of_classes[i]] + ", ";
	  }
	  cadena += classes[list_of_classes[i]] + "}\n";

	  cadena += "@data\n";
	  return cadena;
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
   * It prints the dataset into an string
   * @param ova a boolean variable for the OVO or OVA case. In the latter the attributes will be removed from static (new classes!)
   * @return a string containing the dataset into KEEL format
   */
  public String printDataSet(boolean ova) {
	  String cadena = new String("");
	  cadena += doHeader();
	  if (ova){
		  Attributes.clearAll();
	  }
	  for (int i = 0; i < size(); i++) {
		  double[] ejemplo = this.getExample(i);
		  for (int j = 0; j < this.getnInputs(); j++) {
			  if (this.getTipo(j) == this.NOMINAL) {
				  cadena += nominals[j][ (int) ejemplo[j]] + ", ";
			  }
			  else if (this.getTipo(j) == this.INTEGER) {
				  cadena += (int) ejemplo[j] + ", ";
			  }
			  else {
				  cadena += ejemplo[j] + ", ";
			  }
		  }
		  cadena += this.getOutputAsString(i) + "\n";
	  }
	  return cadena;
  }

  
}