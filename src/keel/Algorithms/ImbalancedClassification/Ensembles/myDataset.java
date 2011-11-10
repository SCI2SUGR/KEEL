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

package keel.Algorithms.ImbalancedClassification.Ensembles;

/**
 * <p>Title: Dataset</p>
 *
 * <p>Description: It contains the methods to read a Classification/Regression Dataset</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Alberto Fern�ndez
 * @version 1.0
 */

import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import keel.Dataset.*;
import java.util.Vector;
import org.core.Randomize;

public class myDataset {

  public static final int REAL = 0;
  public static final int INTEGER = 1;
  public static final int NOMINAL = 2;

  private double[][] X = null; //examples array
//  private double[][] X_normalized = null;
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
  private int [] list_of_classes;

  private InstanceSet IS; //The whole instance set

  private double stdev[], average[]; //standard deviation and average of each attribute
  private double stdevPerClass[][], averagePerClass[][];
  private int instancesCl[];

  /**
   * Init a new set of instances
   */
  public myDataset() {
    IS = new InstanceSet();
  }

  public myDataset(myDataset copia, double[] distribution){
    nVars = copia.getnVars();
    nInputs = copia.getnInputs();
    nClasses = copia.getnClasses();
    double [][] X_aux = new double[copia.size()][copia.getnInputs()];
    int [] outputInteger_aux = new int [copia.size()];
    String [] output_aux = new String [copia.size()];
    nData = 0;
    double r;
    for (int i = 0; i < copia.size(); i++){
       r = Randomize.Rand();
       int j = -1;
       double acumSum = 0;
       do {
          j++;
          acumSum += distribution[j];
       }
       while (acumSum < r);
       X_aux[nData] = copia.getExample(j).clone();
       outputInteger_aux[nData] = copia.getOutputAsInteger(j);
       output_aux[nData] = copia.getOutputAsString(j);
       nData++;
    }
    X = new double[nData][nInputs];
    outputInteger = new int[nData];
    output = new String[nData];
    for (int i = 0; i < nData; i++){
      X[i] = X_aux[i].clone();
      outputInteger[i] = outputInteger_aux[i];
      output[i] = output_aux[i];
    }

    list_of_classes = copia.list_of_classes.clone();
    computeInstancesPerClass();
    //this.computeStatisticsPerClass();
  }

  public myDataset(myDataset copia){
    nVars = copia.getnVars();
    nInputs = copia.getnInputs();
    nClasses = copia.getnClasses();
    nData = 0;
    list_of_classes = copia.list_of_classes.clone();
    copia.IS.setAttributesAsNonStatic();
    this.IS = new InstanceSet(copia.IS);
  }

  public myDataset(myDataset copia, int clase_1, int clase_2){
    nVars = copia.getnVars();
    nInputs = copia.getnInputs();
    nClasses = copia.getnClasses();
    double [][] X_aux = new double[copia.size()][copia.getnInputs()];
    int [] outputInteger_aux = new int [copia.size()];
    String [] output_aux = new String [copia.size()];
    nData = 0;
    for (int i = 0; i < copia.size(); i++){
      if ((copia.getOutputAsInteger(i) == clase_1)||(copia.getOutputAsInteger(i) == clase_2)){
        X_aux[nData] = copia.getExample(i).clone();
        outputInteger_aux[nData] = copia.getOutputAsInteger(i);
        output_aux[nData] = copia.getOutputAsString(i);
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
    list_of_classes[0] = clase_1;
    list_of_classes[1] = clase_2;
    copia.computeInstancesPerClass(); //para el cost_sensitive learning
    instancesCl = new int[copia.getnClasses()];
    for (int i = 0; i < instancesCl.length; i++){
      instancesCl[i] = copia.numberInstances(i);
    }
  }

    public myDataset(myDataset copia, String bagType){
    nVars = copia.getnVars();
    nInputs = copia.getnInputs();
    nClasses = copia.getnClasses();
    double [][] X_aux = new double[copia.size()][copia.getnInputs()];
    int [] outputInteger_aux = new int [copia.size()];
    String [] output_aux = new String [copia.size()];
    nData = 0;
    int r = 0;
    int maj = copia.claseNumerica(copia.claseMasFrecuente());
    int min = maj == 1 ? 0 : 1;
    int bagT = 0;
    int tam = 0;
    if (bagType.equals("OVERBAGGING"))
    {
       bagT = maj == 1 ? 0 : 1;
       X_aux = new double[copia.instancesCl[maj] * 2][copia.getnInputs()];
       outputInteger_aux = new int [copia.instancesCl[maj] * 2];
       output_aux = new String [copia.instancesCl[maj] * 2];
       tam = maj;
    }
    else if (bagType.equals("UNDERBAGGING"))
    {
       X_aux = new double[copia.instancesCl[min] * 2][copia.getnInputs()];
       outputInteger_aux = new int [copia.instancesCl[min] * 2];
       output_aux = new String [copia.instancesCl[min] * 2];
       bagT = maj;
       tam = min;
    }
    for (int i = 0; i < copia.size(); i++){
       if (copia.getOutputAsInteger(i) == min && bagType.equals("UNDERBAGGING"))
       {
           X_aux[nData] = copia.getExample(i).clone();
           outputInteger_aux[nData] = copia.getOutputAsInteger(i);
           output_aux[nData] = copia.getOutputAsString(i);
           nData++;
       }
       else if (copia.getOutputAsInteger(i) == maj && bagType.equals("OVERBAGGING"))
       {
          X_aux[nData] = copia.getExample(i).clone();
           outputInteger_aux[nData] = copia.getOutputAsInteger(i);
           output_aux[nData] = copia.getOutputAsString(i);
           nData++;
       }
    }
    for (int i = nData; i < copia.instancesCl[tam] * 2; i++){
       do {
         r = Randomize.RandintClosed(0, copia.size() - 1);
       } while (copia.getOutputAsInteger(r) != bagT);

        X_aux[nData] = copia.getExample(r).clone();
        outputInteger_aux[nData] = copia.getOutputAsInteger(r);
        output_aux[nData] = copia.getOutputAsString(r);
        nData++;
    }
    X = new double[nData][nInputs];
    outputInteger = new int[nData];
    output = new String[nData];
    for (int i = 0; i < nData; i++){
      X[i] = X_aux[i].clone();
      outputInteger[i] = outputInteger_aux[i];
      output[i] = output_aux[i];
    }
    list_of_classes = copia.list_of_classes;
    computeInstancesPerClass();
  }

  public myDataset(myDataset copia, int clase_1, int clase_2, int[] empate){
    nVars = copia.getnVars();
    nInputs = copia.getnInputs();
    nClasses = copia.getnClasses();
    double [][] X_aux = new double[copia.size()][copia.getnInputs()];
    int [] outputInteger_aux = new int [copia.size()];
    String [] output_aux = new String [copia.size()];
    nData = 0;
    for (int i = 0; i < copia.size(); i++){
      if (((copia.getOutputAsInteger(i) == clase_1)||(copia.getOutputAsInteger(i) == clase_2)) && empate[i] == 1){
        X_aux[nData] = copia.getExample(i).clone();
        outputInteger_aux[nData] = copia.getOutputAsInteger(i);
        output_aux[nData] = copia.getOutputAsString(i);
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
    list_of_classes[0] = clase_1;
    list_of_classes[1] = clase_2;
    copia.computeInstancesPerClass(); //para el cost_sensitive learning
    instancesCl = new int[copia.getnClasses()];
    for (int i = 0; i < instancesCl.length; i++){
      instancesCl[i] = copia.numberInstances(i);
    }
  }

   public myDataset(myDataset copia, int majC, double[][] Xmaj, int minC, double[][] Xmin) {
    nVars = copia.getnVars();
    nInputs = copia.getnInputs();
    nClasses = copia.getnClasses();
    int newNData = copia.size() + Xmaj.length + Xmin.length;
    double [][] X_aux = new double[newNData][copia.getnInputs()];
    int [] outputInteger_aux = new int [newNData];
    String [] output_aux = new String [newNData];
    nData = 0;
    for (int i = 0; i < copia.size(); i++){
        X_aux[nData] = copia.getExample(i).clone();
        outputInteger_aux[nData] = copia.getOutputAsInteger(i);
        output_aux[nData] = copia.getOutputAsString(i);
        nData++;
    }
    for (int i = 0; i < Xmaj.length; i++){
        X_aux[nData] = Xmaj[i].clone();
        outputInteger_aux[nData] = majC;
        output_aux[nData] = copia.getOutputValue(majC);
        nData++;
    }
    for (int i = 0; i < Xmin.length; i++){
        X_aux[nData] = Xmin[i].clone();
        outputInteger_aux[nData] = minC;
        output_aux[nData] = copia.getOutputValue(minC);
        nData++;
    }
    X = new double[nData][nInputs];
    outputInteger = new int[nData];
    output = new String[nData];
    for (int i = 0; i < nData; i++){
      X[i] = X_aux[i].clone();
      outputInteger[i] = outputInteger_aux[i];
      output[i] = output_aux[i];
    }
    list_of_classes = copia.list_of_classes.clone();
    computeInstancesPerClass();
   }

  /* Original dataset to take examples from and
   * the % of majority class in the new data set */
  public int[] randomUnderSampling(myDataset copia, int majC, int N)
  {
     
    int[] majExamples = new int[copia.size()];
    int majCount = 0;
     // First, we copy the examples from the minority class and save the indexes of the majority
    // the new data-set contains samples_min + samples_min * N / 100
    int size = copia.numberInstances(majC == 0 ? 1 : 0) * (100 + 2 * N) / 100;
    int[] selected = new int[size]; // we store the selected examples indexes


    double [][] X_aux = new double[size][copia.getnInputs()];
    int [] outputInteger_aux = new int [size];
    String [] output_aux = new String [size];
    nData = 0;
    for (int i = 0; i < copia.size(); i++){
       if (copia.getOutputAsInteger(i) == majC)
       {
          // save index
          majExamples[majCount] = i;
          majCount++;
       }
       else
       {
          selected[nData] = i;
          // minority class, save instance
          X_aux[nData] = copia.getExample(i).clone();
          outputInteger_aux[nData] = copia.getOutputAsInteger(i);
          output_aux[nData] = copia.getOutputAsString(i);
          nData++;
       }
    }
    /* random undersampling of the majority */
    int r;
    for (int i = nData; i < size; i++){
       r = Randomize.Randint(0, majCount - 1);

       selected[nData] = majExamples[r];
       X_aux[nData] = copia.getExample(majExamples[r]).clone();
       outputInteger_aux[nData] = copia.getOutputAsInteger(majExamples[r]);
       output_aux[nData] = copia.getOutputAsString(majExamples[r]);
       nData++;
    }
    X = new double[nData][nInputs];
    outputInteger = new int[nData];
    output = new String[nData];
    for (int i = 0; i < nData; i++){
      X[i] = X_aux[i].clone();
      outputInteger[i] = outputInteger_aux[i];
      output[i] = output_aux[i];
    }

    computeInstancesPerClass();
    return selected;
  }

  public int[] randomSampling(myDataset copia, int majC, int minC, int a)
  {

    int[] majExamples = new int[copia.size()];
    int[] minExamples = new int[copia.size()];
    int majCount = 0, minCount = 0;
     // First, we copy the examples from the minority class and save the indexes of the majority
    // the new data-set contains samples_min + samples_min * N / 100
    int size = copia.numberInstances(majC) * a / 100 * 2;
    int[] selected = new int[size]; // we store the selected examples indexes


    double [][] X_aux = new double[size][copia.getnInputs()];
    int [] outputInteger_aux = new int [size];
    String [] output_aux = new String [size];
    nData = 0;
    for (int i = 0; i < copia.size(); i++){
       if (copia.getOutputAsInteger(i) == majC)
       {
          // save index
          majExamples[majCount] = i;
          majCount++;
       }
       else
       {
          minExamples[minCount] = i;
          minCount++;
       }
    }
    /* random undersampling of the majority */
    int r;
    for (int i = 0; i < size / 2; i++){
       r = Randomize.Randint(0, majCount - 1);

       selected[nData] = majExamples[r];
       X_aux[nData] = copia.getExample(majExamples[r]).clone();
       outputInteger_aux[nData] = copia.getOutputAsInteger(majExamples[r]);
       output_aux[nData] = copia.getOutputAsString(majExamples[r]);
       nData++;

       r = Randomize.Randint(0, minCount - 1);

       selected[nData] = minExamples[r];
       X_aux[nData] = copia.getExample(minExamples[r]).clone();
       outputInteger_aux[nData] = copia.getOutputAsInteger(minExamples[r]);
       output_aux[nData] = copia.getOutputAsString(minExamples[r]);
       nData++;
    }
    X = new double[nData][nInputs];
    outputInteger = new int[nData];
    output = new String[nData];
    for (int i = 0; i < nData; i++){
      X[i] = X_aux[i].clone();
      outputInteger[i] = outputInteger_aux[i];
      output[i] = output_aux[i];
    }

    computeInstancesPerClass();
    return selected;
  }

  public int[] randomSampling(myDataset copia, int majC, int minC, int nMaj, int nMin)
  {

    int[] majExamples = new int[copia.size()];
    int[] minExamples = new int[copia.size()];
    int majCount = 0, minCount = 0;
     // First, we copy the examples from the minority class and save the indexes of the majority
    // the new data-set contains samples_min + samples_min * N / 100
    int size = nMaj + nMin;
    int[] selected = new int[size]; // we store the selected examples indexes


    double [][] X_aux = new double[size][copia.getnInputs()];
    int [] outputInteger_aux = new int [size];
    String [] output_aux = new String [size];
    nData = 0;
    for (int i = 0; i < copia.size(); i++){
       if (copia.getOutputAsInteger(i) == majC)
       {
          // save index
          majExamples[majCount] = i;
          majCount++;
       }
       else
       {
          minExamples[minCount] = i;
          minCount++;
       }
    }
    /* random undersampling of the majority */
    boolean[] taken = new boolean[copia.size()];
    int r;
    for (int i = 0; i < nMaj; i++){
       r = Randomize.Randint(0, majCount - 1);

       selected[nData] = majExamples[r]; taken[majExamples[r]] = true;
       X_aux[nData] = copia.getExample(majExamples[r]).clone();
       outputInteger_aux[nData] = copia.getOutputAsInteger(majExamples[r]);
       output_aux[nData] = copia.getOutputAsString(majExamples[r]);
       nData++;
    }
    for (int i = 0; i < nMin; i++){
       r = Randomize.Randint(0, minCount - 1);

       selected[nData] = minExamples[r]; taken[minExamples[r]] = true;
       X_aux[nData] = copia.getExample(minExamples[r]).clone();
       outputInteger_aux[nData] = copia.getOutputAsInteger(minExamples[r]);
       output_aux[nData] = copia.getOutputAsString(minExamples[r]);
       nData++;
    }

    int deleted = 0;
    for (int i = 0; i < copia.size(); i++)
       if (!taken[i])
       {
          this.IS.removeInstance(i - deleted);
          deleted++;
       }


    X = new double[nData][nInputs];
    outputInteger = new int[nData];
    output = new String[nData];
    for (int i = 0; i < nData; i++){
      X[i] = X_aux[i].clone();
      outputInteger[i] = outputInteger_aux[i];
      output[i] = output_aux[i];
    }

    computeInstancesPerClass();
    return selected;
  }


  public boolean[] importanceSampling(myDataset copia, int size, boolean[] oob, double oobErr)
  {

    boolean[] selected = new boolean[copia.getnData()]; // we store the selected examples indexes

    this.IS.clearInstances();
    X = new double[size][copia.getnInputs()];
    outputInteger = new int [size];
    output = new String [size];
    nData = 0;
     int r;
    while (nData < size)
    {
       r = Randomize.Randint(0, copia.getnData());

       if (oob[r] == false || (Randomize.RandClosed() <= oobErr / (1 - oobErr)))
       {
          selected[r] = true;

          this.IS.addInstance(copia.IS.getInstance(r));
          X[nData] = copia.getExample(r).clone();
          outputInteger[nData] = copia.getOutputAsInteger(r);
          output[nData] = copia.getOutputAsString(r);

          nData++;
       }
    }



    /*int deleted = 0;
    for (int i = 0; i < copia.size(); i++)
       if (!taken[i])
       {
          this.IS.removeInstance(i - deleted);
          deleted++;
       }*/


   /* X = new double[nData][nInputs];
    outputInteger = new int[nData];
    output = new String[nData];
    for (int i = 0; i < nData; i++){
      X[i] = X_aux[i].clone();
      outputInteger[i] = outputInteger_aux[i];
      output[i] = output_aux[i];
    }*/

    computeInstancesPerClass();
    return selected;
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
  }

    /**
   * It reads the whole input data-set and it stores each example and its associated output value in
   * local arrays to ease their use.
   * @throws IOException If there ocurs any problem with the reading of the data-set
   */
  public void readInstanceSet(InstanceSet IS) throws
      IOException {
    try {
      this.IS = IS;
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

/*  public void normalize_statistics() {
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
  }*/

    /**
   * It computes the average and standard deviation of the input attributes
   */
  public void computeStatisticsPerClass() {
    //this.normalize_statistics();
    stdevPerClass = new double[nClasses][this.getnInputs()];
    averagePerClass = new double[nClasses][this.getnInputs()];
    int c;

    for (int i = 0; i < this.getnInputs(); i++) {
      for (int j = 0; j < this.getnClasses(); j++)
          averagePerClass[j][i] = 0;
      for (int j = 0; j < this.getnData(); j++) {
        c = this.outputInteger[j];
        if (!this.isMissing(j, i)) {
          averagePerClass[c][i] += X[j][i];
        }
      }
      for (int j = 0; j < this.getnClasses(); j++)
      {
        averagePerClass[j][i] /= this.numEjemplos(j);
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
          sum[c] += (X[j][i] - averagePerClass[c][i]) * (X[j][i] - averagePerClass[c][i]);
        }
      }
      for (int j = 0; j < this.getnClasses(); j++)
      {
        sum[j] /= this.numEjemplos(j);
        stdevPerClass[j][i] = Math.sqrt(sum[j]);
      }
    }
  }

  public double[][] getAveragePerClass()
  {
      return this.averagePerClass;
  }
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
   * Devuelve el universo de discuros de las variables de entrada y salida
   * @return double[][] El rango minimo y maximo de cada variable
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

  public String nombreVar(int pos) {
    return Attributes.getInputAttribute(pos).getName();
  }

  /**
   * Devuelve el valor nominal correspondiente a la clase con valor numerico "clase"
   * @param clase int
   * @return String
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

  public String [] nombres(){
    String nombres[] = new String[nInputs];
    for (int i = 0; i < nInputs; i++){
      nombres[i] = Attributes.getInputAttribute(i).getName();
    }
    return nombres;
  }

  public static double valorReal(int atributo, String valorNominal){
    Vector nominales = Attributes.getInputAttribute(atributo).getNominalValuesList();
    int aux = nominales.indexOf(valorNominal);
    return 1.0*aux;
  }

  public int claseNumerica(String valorNominal){
    Vector nominales = Attributes.getOutputAttribute(0).getNominalValuesList();
    int aux = nominales.indexOf(valorNominal);
    return aux;
  }

  public static String valorNominal(int atributo, double valorReal){
    Vector nominales = Attributes.getInputAttribute(atributo).getNominalValuesList();
    return (String)nominales.get((int)valorReal);
  }

  public int totalNominales(int atributo){
    return Attributes.getInputAttribute(atributo).getNumNominalValues();
  }

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
   * Obtiene el numero de ejemplos para la clase i-esima
   * @param clase int codigo de clase
   * @return int numero de ejemplos pertenencientes a dicha clase
   */
  public int numEjemplos(int clase){
    int ejemplos = 0;
    for (int i = 0; i < outputInteger.length; i++){
      if (clase == outputInteger[i])
        ejemplos++;
    }
    return ejemplos;
  }

  public boolean vacio(){
    if ((this.numberInstances(list_of_classes[0]) == 0)||(this.numberInstances(list_of_classes[1]) == 0)){
      return true;
    }
    return false;
  }

  public String printDataSet(){
     System.out.println("Printing data-set...");
    String cadena = new String("");
    cadena += this.copyHeader();

    String cadenaAux = "";
    for (int i = 0; i < size(); i++){
      double [] ejemplo = this.getExample(i);
      String cadenaAux2 = "";
      for (int j = 0; j < this.getnInputs(); j++){
        if (this.getTipo(j) == this.NOMINAL){
         cadenaAux2 += Attributes.getInputAttribute(j).getNominalValue((int)ejemplo[j])+", ";
        }else{
          cadenaAux2 += ejemplo[j] + ", ";
        }
      }
      cadenaAux += cadenaAux2 + this.getOutputAsString(i)+"\n";
      if (i % 1000 == 0 || i == size() - 1)
      {
         cadena += cadenaAux;
         cadenaAux = "";
      }
    }
    System.out.println("Data-set printed!");
    return cadena;
  }

  public List<Integer> asList(final int[] is)
 {
         return new AbstractList<Integer>() {
                 public Integer get(int i) { return is[i]; }
                 public int size() { return is.length; }
         };
 }

   void deleteExamples(boolean[] correct, int[] selected, int minC) {//, int maxDel) {
      ArrayList delete = new ArrayList<Integer>();
      for (int i = 0; i < selected.length; i++)
         if (correct[selected[i]] && (outputInteger[selected[i]] != minC))// && delete.size() < maxDel)
         {
            if (!delete.contains(selected[i]))
               delete.add(selected[i]);
         }
    double [][] X_aux = new double[size() - delete.size()][getnInputs()];
    int [] outputInteger_aux = new int [size() - delete.size()];
    String [] output_aux = new String [size() - delete.size()];
    int nDataAux = 0;
    //List al = asList(selected);
    for (int i = 0; i < size(); i++){
       if ( !delete.contains(i) )
       {
          X_aux[nDataAux] = getExample(i).clone();
          outputInteger_aux[nDataAux] = getOutputAsInteger(i);
          output_aux[nDataAux] = getOutputAsString(i);
          nDataAux++;
       }
    }
    nData = nDataAux;
    X = new double[nData][nInputs];
    outputInteger = new int[nData];
    output = new String[nData];
    for (int i = 0; i < nData; i++){
      X[i] = X_aux[i].clone();
      outputInteger[i] = outputInteger_aux[i];
      output[i] = output_aux[i];
    }

    computeInstancesPerClass();

   }

  public InstanceSet getIS()
  {
     return this.IS;
  }
}
