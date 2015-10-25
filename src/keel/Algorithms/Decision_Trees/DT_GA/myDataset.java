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

package keel.Algorithms.Decision_Trees.DT_GA;

/**
 * <p>Title: Dataset</p>
 *
 * <p>Description: It contains the methods to read a Classification/Regression Dataset</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Alberto Fernández
 * @version 1.0
 */

import java.io.IOException;

import keel.Dataset.*;
import java.util.Vector;

/**
 * <p>Title: Dataset</p>
 *
 * <p>Description: It contains the methods to read a Classification/Regression Dataset</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Alberto Fernández
 * @version 1.0
 */
public class myDataset {

    /**
     * Number to represent type of variable real or double.
     */
    public static final int REAL = 0;

    /**
     * Number to represent type of variable integer.
     */
    public static final int INTEGER = 1;

    /**
     * Number to represent type of variable nominal.
     */
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
     * Gets the maximum value of the variable as argument.
     * @param variable index of the attribute/variable is being asked for
     * @return the maximun value of the attribute as argument.
     */
    public double getMax(int variable) {
    return emax[variable];
  }

    /**
     * Gets the minimum value of the variable as argument.
     * @param variable index of the attribute/variable is being asked for
     * @return the minimum value of the attribute as argument.
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
      for (int i = 0; i < nInputs; i++) {
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
          if (missing[i][j]) {
            X[i][j] = emin[j] - 1;
          }
        }

        if (noOutputs) {
          outputInteger[i] = 0;
          output[i] = "";
        }
        else {
          outputInteger[i] = (int) IS.getOutputNumericValue(i, 0);
          output[i] = IS.getOutputNominalValue(i, 0);
        }
        if (outputInteger[i] > nClasses) {
          nClasses = outputInteger[i];
        }
      }
      nClasses++;
      System.out.println("Number of classes=" + nClasses);

    }
    catch (Exception e) {
      System.out.println("DBG: Exception in readSet");
      e.printStackTrace();
    }
    computeStatistics();
    this.computeInstancesPerClass();
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
      for (int i = 0; i < nInputs; i++) {
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
          if (missing[i][j]) {
            X[i][j] = emin[j] - 1;
          }
        }

        if (noOutputs) {
          outputReal[i] = outputInteger[i] = 0;
        }
        else {
          outputReal[i] = IS.getOutputNumericValue(i, 0);
          outputInteger[i] = (int) outputReal[i];
        }
      }
    }
    catch (Exception e) {
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
        }
        else {
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

    /**
     * Checks if the data-set has any numeric value.
     * @return boolean True if it has some numeric value, else false.
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
     * Returns the total number of instances in the data-set.
     * @return Number of instances in the data-set.
     */
    public int size() {
    return nData;
  }

  /**
   * It computes the average and standard deviation of the input attributes
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
     * Counts and stores the number of instances that belong to each class.
     */
    public void computeInstancesPerClass() {
    instancesCl = new int[nClasses];
    for (int i = 0; i < this.getnData(); i++) {
      instancesCl[this.outputInteger[i]]++;
    }
  }

    /**
     * Returns the number of instances of the class with index passed as argument ("clas").
     * @param clas Index of the class being asked.
     * @return Number of instances of the class "clas"
     */
    public int numberInstances(int clas) {
    return instancesCl[clas];
  }

    /**
     * Returns the number of different values of the attribute with index passed as argument.
     * @param attribute Index of the attribute being asked.
     * @return Number of different values of the attribute passed as argument.
     */
    public int numberValues(int attribute) {
    return Attributes.getInputAttribute(attribute).getNumNominalValues();
  }

    /**
     * Returns the nominal output value which corresponds to the integer passed as argument.
     * @param intValue Integer value which determinate the nominal output.
     * @return String value which corresponds to the integer passed as argument ("intValue"). 
     */
    public String getOutputValue(int intValue) {
    return Attributes.getOutputAttribute(0).getNominalValue(intValue);
  }

    /**
     * Returns the type of the attribute with index passed as argument.
     * Possible returned values: INTEGER(1), REAL(0), NOMINAL(2) and UNDEFINED(0)
     * @param variable Index of the attribute.
     * @return the type of the attribute as a integer value.
     */
    public int getTipo(int variable) {
    if (Attributes.getInputAttribute(variable).getType() ==
        Attributes.getInputAttribute(0).INTEGER) {
      return this.INTEGER;
    }
    if (Attributes.getInputAttribute(variable).getType() ==
        Attributes.getInputAttribute(0).REAL) {
      return this.REAL;
    }
    if (Attributes.getInputAttribute(variable).getType() ==
        Attributes.getInputAttribute(0).NOMINAL) {
      return this.NOMINAL;
    }
    return 0;
  }

  /**
     * Returns the minimum and maximum values of every attributes as a matrix.
     * The matrix has a size of number_of_attributes x 2 ([nAttributes][2]).
     * The minimum value is located at the first position of each array and the maximum, at the second.
     * @return Matrix which stores the minimum and maximum values of every attributes.
     */
  public double[][] devuelveRangos() {
    double[][] rangos = new double[this.getnVars()][2];
    for (int i = 0; i < this.getnInputs(); i++) {
      if (Attributes.getInputAttribute(i).getNumNominalValues() > 0) {
        rangos[i][0] = 0;
        rangos[i][1] = Attributes.getInputAttribute(i).getNumNominalValues() -
            1;
      }
      else {
        rangos[i][0] = Attributes.getInputAttribute(i).getMinAttribute();
        rangos[i][1] = Attributes.getInputAttribute(i).getMaxAttribute();
      }
    }
    rangos[this.getnVars() -
        1][0] = Attributes.getOutputAttribute(0).getMinAttribute();
    rangos[this.getnVars() -
        1][1] = Attributes.getOutputAttribute(0).getMaxAttribute();
    return rangos;
  }

    /**
   * It returns the attribute name of a given variable
   * @param pos variable id.
   * @return attribute name of a given variable
   */
    public String nombreVar(int pos) {
    return Attributes.getInputAttribute(pos).getName();
  }

  /**
   * Returns the nominal value for a class represented by the integer given.
   * @param clase integer representation of the class.
   * @return String nominal value for the class
   */
  public String nombreClase(int clase) {
    return Attributes.getOutputAttribute(0).getNominalValue(clase);
  }

  /**
   * Discretizacion en anchura uniforme
   * @param intervalos int Numero de intervalos a discretizar
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
    public String [] nombres(){
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
    public static double valorReal(int atributo, String valorNominal){
    Vector nominales = Attributes.getInputAttribute(atributo).getNominalValuesList();
    int aux = nominales.indexOf(valorNominal);
    return 1.0*aux;
  }

    /**
     * Returns a nominal representation of a attribute's real value given as argument.
     * @param atributo Attribute given.
     * @param valorReal Real value of the attribute given.
     * @return Returns a nominal representation of a attribute's real value.
     */
    public static String valorNominal(int atributo, double valorReal){
    Vector nominales = Attributes.getInputAttribute(atributo).getNominalValuesList();
    return (String)nominales.get((int)valorReal);
  }

    /**
   * It returns the number of nominal values for a given variable
   * @param atributo variable id
   * @return the number of nominal values for a given variable
   */
    public int totalNominales(int atributo){
    return Attributes.getInputAttribute(atributo).getNumNominalValues();
  }

    /**
   * It returns the most frequent class in the dataset
   * @return the most frequent class in the dataset
   */
    public String claseMasFrecuente(){
    int [] clases = new int[nClasses];
    for (int i = 0; i < this.outputInteger.length; i++){
      clases[outputInteger[i]]++;
    }
    int claseMayoritaria = 0;
    for (int i = 0; i < clases.length; i++){
      if (clases[claseMayoritaria] < clases[i]){
        claseMayoritaria = i;
      }
    }
    return this.getOutputValue(claseMayoritaria);
  }

  /**
     * It returns the number of instances in the data set for a given class.
     * @param clase int Given class.
     * @return int Number of instances for the given class.
     */
  public int numEjemplos(int clase){
    int ejemplos = 0;
    for (int i = 0; i < outputInteger.length; i++){
      if (clase == outputInteger[i])
        ejemplos++;
    }
    return ejemplos;
  }

}

