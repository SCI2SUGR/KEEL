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

/**
 * <p>
 * @author Written by Alberto Fernández (University of Granada)  01/07/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.3
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Rule_Learning.Ripper;

 /**
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

public class MyDataset {
/**
 * DataSet class
 */ 
  
  public static final int REAL = 0;
  public static final int INTEGER = 1;
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
  private double totalCond; //Number of possible conditions

  private InstanceSet IS; //The whole instance set

  private double stdev[], average[]; //standard deviation and average of each attribute
  private int instancesCl[]; // number of instances of each class

  /**
   * Init a new set of instances
   */
  public MyDataset() {
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
   * Returns the number of possible conditions.
   * @return the number of possible conditions.
   */
  public double getNumCond(){
    return totalCond;
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
   * Output a specific example
   * @param mask Mask with the position (id) of the example in the data-set
   * @return double[] the attributes of the given example
   */
  public double[] getExample(Mask mask) {
    return X[mask.getIndex()];
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

  public double getMax(int variable) {
    return emax[variable];
  }

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
   * This function checks if the attribute value is missing
   * @param mask Mask
   * @param j int Variable id
   * @return boolean True is the value is missing, else it returns false
   */
  public boolean isMissing(Mask mask, int j) {
    return missing[mask.getIndex()][j];
  }

  /**
   * It reads the whole input data-set and it stores each example and its associated output value in
   * local arrays to ease their use.
   * @param datasetFile String name of the file containing the dataset
   * @param train boolean It must have the value "true" if we are reading the training data-set
   * @throws IOException If there ocurs any problem with the reading of the data-set
   */
  public void readClassificationSet(String datasetFile, boolean train) throws IOException {
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
      X = new double[nData][nInputs];
      missing = new boolean[nData][nInputs];
      outputInteger = new int[nData];
      outputReal = new double[nData];
      output = new String[nData];

      // Maximum and minimum of inputs
      emax = new double[nInputs];
      emin = new double[nInputs];

      // All values are casted into double/integer
      nClasses = 0;
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

    totalCond=this.numAllConditions();
  }

  /**
   * It reads the whole input data-set and it stores each example and its associated output value in
   * local arrays to ease their use.
   * @param datasetFile String name of the file containing the dataset
   * @param train boolean It must have the value "true" if we are reading the training data-set
   * @throws IOException If there ocurs any problem with the reading of the data-set
   */
  public void readRegressionSet(String datasetFile, boolean train) throws IOException {
    try {
      // Load in memory a dataset that contains a regression problem
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

  public boolean hasNumericalAttributes() {
    return (Attributes.hasIntegerAttributes() || Attributes.hasRealAttributes());
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

  /**
   * It computes the average and standard deviation of the input attributes
   */
  private void computeStatistics() {
    stdev = new double[this.getnVars()];
    average = new double[this.getnVars()];
    for (int i = 0; i < this.getnInputs(); i++) {
      average[i] = 0;
      for (int j = 0; j < this.getnData(); j++) {//Julian - fix
        average[i] += X[j][i];//Julian - fix
      }
      average[i] /= (double)this.getnData();//Julian - fix
    }

    average[average.length-1] = 0;
    for (int j = 0; j < outputReal.length; j++) {
      average[average.length-1] += outputReal[j];
    }
    average[average.length-1] /= outputReal.length;


    for (int i = 0; i < this.getnInputs(); i++) {
      double sum = 0;
      for (int j = 0; j < this.getnData(); j++) { //Julian - fix
        sum += (X[j][i] - average[i]) * (X[j][i] - average[i]);//Julian - fix
      }
      sum /= (double)this.getnData(); //Julian - fix
      stdev[i] = Math.sqrt(sum);
    }
    double sum = 0;
    for (int j = 0; j < outputReal.length; j++) {
      sum += (outputReal[j] - average[average.length-1]) * (outputReal[j] - average[average.length-1]);
    }
    sum /= outputReal.length;
    stdev[stdev.length-1] = Math.sqrt(sum);

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

  public void computeInstancesPerClass() {
    instancesCl = new int[nClasses];
    for (int i = 0; i < this.getnData(); i++) {
      instancesCl[this.outputInteger[i]]++;
    }
  }

  public int numberInstances(int clas) {
    return instancesCl[clas];
  }

  public int numberValues(int attribute) {
    return Attributes.getInputAttribute(attribute).getNumNominalValues();
  }

  public String getOutputValue(int intValue) {
    return Attributes.getOutputAttribute(0).getNominalValue(intValue);
  }

  public int getType(int variable) {
    if (Attributes.getInputAttribute(variable).getType() == Attributes.getInputAttribute(0).INTEGER) {
      return this.INTEGER;
    }
    if (Attributes.getInputAttribute(variable).getType() == Attributes.getInputAttribute(0).REAL) {
      return this.REAL;
    }
    if (Attributes.getInputAttribute(variable).getType() == Attributes.getInputAttribute(0).NOMINAL) {
      return this.NOMINAL;
    }
    return 0;
  }

  /**
   * Devuelve el universo de discuros de las variables de entrada y salida
   * @return double[][] El rango minimo y maximo de cada variable
   */
  public double [][] devuelveRangos(){
    double [][] rangos = new double[this.getnVars()][2];
    for (int i = 0; i < this.getnInputs(); i++){
      if (Attributes.getInputAttribute(i).getNumNominalValues() > 0){
        rangos[i][0] = 0;
        rangos[i][1] = Attributes.getInputAttribute(i).getNumNominalValues()-1;
      }else{
        rangos[i][0] = Attributes.getInputAttribute(i).getMinAttribute();
        rangos[i][1] = Attributes.getInputAttribute(i).getMaxAttribute();
      }
    }
    rangos[this.getnVars()-1][0] = Attributes.getOutputAttribute(0).getMinAttribute();
    rangos[this.getnVars()-1][1] = Attributes.getOutputAttribute(0).getMaxAttribute();
    return rangos;
  }

  /*******************NEW METHODS*************************************/

  /**
   * It filters the instances covered by a simple rule from this dataset;
   * i.e., it deactivates the instances not covered by that rule.
   * @param mask Mask the mask with the active entries of the dataset
   * @param A int attribute's id
   * @param V double attribute's value
   * @param operator int rule operator: >,<= or = (Rule.GREATER,Rule.LOWER,Rule.EQUAL)
   */
  public void filter(Mask mask,int A,double V,int operator){
    mask.resetIndex();
    while(mask.next()){
      if (this.isMissing(mask,A)){
        mask.reset();
      }
      else{
        if (operator == Rule.EQUAL && X[mask.getIndex()][A] != V)
          mask.reset();
        if (operator == Rule.GREATER && X[mask.getIndex()][A] <= V)
          mask.reset();
        if (operator == Rule.LOWER && X[mask.getIndex()][A] > V)
          mask.reset();
      }
    }
  }

  /**
   * It filters the instances covered by a simple rule from this dataset;
   * i.e., it deactivates the instances not covered by that rule.
   * @param mask Mask the mask with the actives entries of the dataset
   * @param sr SimpleRule the rule
   */
  public void filter(Mask mask,SimpleRule sr){
    filter(mask,sr.getAttribute(),sr.getValue(),sr.getOperator());
  }

  /**
   * It filters the instances covered by a rule from this dataset;
   * i.e., it deactivates the instances not covered by that rule.
   * @param mask Mask the mask with the active entries of the dataset
   * @param rule Rule the rule
   */
  public void filter(Mask mask,Rule rule){
    for (int i=0;i<rule.size();i++)
      this.filter(mask, rule.getSimpleRule(i));
  }

  /**
   * It filters the instances covered by a set of rule from this dataset;
   * i.e., it deactivates the instances not covered by that ruleset.
   * @param mask Mask the mask with the active entries of the dataset
   * @param rules Ruleset the ruleset
   */
  public void filter(Mask mask,Ruleset rules){
    Mask previous=new Mask(this.getnData(),false);
    for (int i=0;i<rules.size();i++){
      Mask current=mask.copy();
      filter(current,rules.getRule(i));
      previous=previous.or(current);
    }
    previous.copyTo(mask);
  }

  /**
   * It filters the instances of a given class from this dataset;
   * i.e., it deactivates the instances from the other class.
   * @param mask Mask the mask whit the active entries of the dataset
   * @param value String the name of the class
   */
  public void filterByClass(Mask mask,String value){
    mask.resetIndex();
    while (mask.next()) {
      if (!output[mask.getIndex()].equals(value))
        mask.reset();
    }
  }

  /**
   * It substracts the instances covered by a simple rule from this dataset;
   * i.e., it deactivates the instances covered by that rule.
   * @param mask Mask the mask with the active entries of the dataset
   * @param A int attribute's id
   * @param V double attribute's value
   * @param operator int rule operator: >,<= or = (Rule.GREATER,Rule.LOWER,Rule.EQUAL)
   */
  public void substract(Mask mask,int A,double V,int operator){
    mask.resetIndex();
    while(mask.next()){
      if (!isMissing(mask,A)){
        if (operator == Rule.EQUAL && X[mask.getIndex()][A] == V)
          mask.reset();
        if (operator == Rule.GREATER && X[mask.getIndex()][A] > V)
          mask.reset();
        if (operator == Rule.LOWER && X[mask.getIndex()][A] <= V)
          mask.reset();
      }
    }
  }


  /**
   * It substracts the instances covered by a simple rule from this dataset;
   * i.e., it deactivates the instances covered by that rule.
   * @param mask Mask the mask with the active entries of the dataset
   * @param sr SimpleRule the rule
   */
  public void substract(Mask mask,SimpleRule sr){
    substract(mask,sr.getAttribute(),sr.getValue(),sr.getOperator());
  }

  /**
   * It substracts the instances covered by a rule from this dataset;
   * i.e., it deactivates the instances covered by that rule.
   * @param mask Mask the mask with the active entries of the dataset
   * @param rule Rule the rule
   */
  public void substract(Mask mask,Rule rule){
    mask.resetIndex();
    while(mask.next()){
      boolean seguir=true;
      for (int i = 0; i < rule.size() && seguir; i++){
        int A=rule.getSimpleRule(i).getAttribute();
        double V=rule.getSimpleRule(i).getValue();
        int operator=rule.getSimpleRule(i).getOperator();
        if (isMissing(mask,A))
          seguir=false;
        else{
          if (operator == Rule.EQUAL)
            seguir=(X[mask.getIndex()][A] == V);
          if (rule.getSimpleRule(i).getOperator() == Rule.GREATER)
            seguir=(X[mask.getIndex()][A] > V);
          if (rule.getSimpleRule(i).getOperator() == Rule.LOWER)
            seguir=(X[mask.getIndex()][A] <= V);
        }
      }
      if (seguir)
        mask.reset();
    }
  }

  /**
   * It substracts the instances covered by a set of rule from this dataset;
   * i.e., it deactivates the instances covered by that ruleset.
   * @param mask Mask the mask with the active entries of the dataset
   * @param rules Ruleset the set of rules
   */
  public void substract(Mask mask,Ruleset rules){
    for (int i=0;i<rules.size();i++){
      substract(mask,rules.getRule(i));
    }
  }

  /**
   * It substracts the instances covered by a set of rules from this dataset;
   * i.e., it deactivates the instances covered by that ruleset.
   * This method allows to ignore a rule of the set.
   * @param mask Mask the mask with the active entries of the dataset
   * @param rules Ruleset the set of rules
   * @param ignore int number of the rule to ignore
   */
  public void substract(Mask mask,Ruleset rules,int ignore){
    for (int i=0;i<rules.size();i++){
      if (i!=ignore)
        substract(mask,rules.getRule(i));
    }
  }

  /**
   * Compute the number of all possible conditions that could
   * appear in a rule of a given data.  For nominal attributes,
   * it's the number of values that could appear; for numeric
   * attributes, it's the number of values * 2, i.e. <= and >=
   * are counted as different possible conditions.
   *
   * @return number of all conditions of the data
   */
  public double numAllConditions(){
    double total = 0;

    for(int i=0;i<Attributes.getInputNumAttributes();i++){
      Attribute att= Attributes.getInputAttribute(i);
      if(att.getType()==Attribute.NOMINAL)
        total += (double)att.getNumNominalValues();
      else
        total += 2.0 * (double) numDistinctValues(i);
    }
    return total;
  }

  /**
   * Returns the number of distinct values of a given attribute.
   * Returns the number of instances if the attribute is a
   * string attribute. The value 'missing' is not counted.
   *
   * @param attIndex the attribute (index starts with 0)
   * @return the number of distinct values of a given attribute
   */
  public int numDistinctValues(int attIndex) {

    Attribute att= Attributes.getAttribute(attIndex);
    if (att.getType()==Attribute.REAL || att.getType()==Attribute.INTEGER) {
      double [] attVals = attributeToDoubleArray(attIndex);
      Utilities.mergeSort(attVals,nData);
      double prev = 0;
      int counter = 0;
      for (int i = 0; i < nData; i++) {

        if (attVals[i]==Double.NaN) {
          break;
        }
        if ((i == 0) || (attVals[i] > prev)) {
          prev = attVals[i];
          counter++;
        }
      }
      return counter;
    } else {
      return att.getNumNominalValues();
    }
  }

  /**
   * Gets the value of all instances in this dataset for a particular
   * attribute. Useful in conjunction with Utils.sort to allow iterating
   * through the dataset in sorted order for some attribute.
   *
   * @param index the index of the attribute.
   * @return an array containing the value of the desired attribute for
   * each instance in the dataset.
   */
  public double [] attributeToDoubleArray(int index) {

    double [] result = new double[this.nData];
    for (int i = 0; i < result.length; i++) {
      result[i] = X[i][index];
    }
    return result;
  }


  /**
   * Classifies the entries' classes according to several sets of rules.
   * @param actives Mask active entries of the dataset
   * @param rulesets Ruleset[] the rulesets
   * @param length int the number of rulesets
   * @return a vector of the length of this dataset with the class name for each entry.
   */
  public String[] classify(Mask actives,Ruleset[] rulesets,int length){
    String[] classification=new String[this.getnData()];
    for(int i=0;i<classification.length;i++)
      if ( actives.isActive(i) )
        classification[i]=rulesets[length-1].getType();
    for (int i=0;i<length-1;i++){
      Mask filtered=actives.copy();
      this.filter(filtered,rulesets[i]);
      filtered.resetIndex();
      while(filtered.next()){
        int ind=filtered.getIndex();
        classification[ind]=rulesets[i].getType();
      }
      substract(actives,rulesets[i]);
    }
    return classification;
  }

  /**
   * Classifies the entries' classes according to several sets of rules.
   * @param rulesets Ruleset[] the rulesets
   * @param length int the number of rulesets
   * @return a vector of the length of this dataset with the class name for each entry.
   */
  public String[] classify(Ruleset[] rulesets, int length){
    return classify(new Mask(size()),rulesets,length);
  }

  /**
   * Returns a string representation of the entries of this MyDataset.
   * @return a string representation of the entries of this MyDataset.
   */
  public String toString(){
    String salida="";
    for (int i=0;i<this.nData;i++){
      if (Attributes.getInputAttribute(0).getType()==Attribute.NOMINAL)
        salida+=i+".- ("+Attributes.getInputAttribute(0).getNominalValue((int)X[i][0]);
      else
        salida+=i+".- ("+X[i][0];
      for (int j=1;j<this.nInputs+1;j++){
        if (Attributes.getInputAttribute(j).getType()==Attribute.NOMINAL)
          salida+=","+Attributes.getInputAttribute(j).getNominalValue((int)X[i][j]);
        else
          salida+=","+X[i][j];
      }
      salida+=") ->"+output[i]+"\n";
    }
    return salida;
  }

  /**
   * Returns a string representation of the active entries of this MyDataset.
   * @param mask Mask active entries
   * @return a string representation of the active entries of this MyDataset.
   */
  public String toString(Mask mask){
    String salida="";
    mask.resetIndex();
    while (mask.next()){
      int i=mask.getIndex();
      if (Attributes.getInputAttribute(0).getType()==Attribute.NOMINAL)
        salida+=i+".- ("+Attributes.getInputAttribute(0).getNominalValue((int)X[i][0]);
      else
        salida+=i+".- ("+X[i][0];
      for (int j=1;j<this.nInputs;j++){
        if (Attributes.getInputAttribute(j).getType()==Attribute.NOMINAL)
          salida+=","+Attributes.getInputAttribute(j).getNominalValue((int)X[i][j]);
        else
          salida+=","+X[i][j];
      }
      salida+=")->"+output[i]+"\n";
    }

    return salida;
  }

}

