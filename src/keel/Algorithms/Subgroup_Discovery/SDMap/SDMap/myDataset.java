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
 * @author Written by Alberto Fernandez (University of Granada) 01/02/2006
 * @author Modified by Nicolo Flugy Papa (Politecnico di Milano) 24/03/2009
 * @author Modified by Cristobal J. Carmona (University of Jaen) 10/07/2010
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.SDMap.SDMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import keel.Dataset.*;

public class myDataset {
      /**
       * <p>
       * It contains the methods to read a Dataset for the Association Rules Mining problem
       * </p>
       */

      public static final int NOMINAL = 0;
      public static final int INTEGER = 1;
      public static final int REAL = 2;

      private double[][] trueTransactions = null; //true transactions array
      private int[] covered = null; //transactions covered
      private boolean[][] missing = null; //possible missing values
      private double[] emax; //max value of an attribute
      private double[] emin; //min value of an attribute

      private int nTrans; // Number of transactions
      private int nInputs; // Number of inputs
      private int nOutputs; // Number of outputs
      private int numOutputs;
      private int nVars; // Number of variables

      private int[] classes;   //Values of the classes

      private int nPartitionForNumericAttributes = 1; //Number of partition
      private int[][] fakeTransactions = null; //fake transactions array
      private double[] steps = null; //steps of each attribute depending on the number of partitions
      private Hashtable<Integer, HashSet<Integer>> tidList = null; //structure that maps every attribute value with a list of TIDs which contains the latter

      private InstanceSet IS; //The whole instance set

     /**
     * <p>
     * Initialize a new set of instances
     * </p>
     * @param nPartition The number of partition in which numeric attributes are uniformly divided
     */
      public myDataset(int nPartition) {
              IS = new InstanceSet();
              nPartitionForNumericAttributes = (nPartition > 0) ? nPartition : 1;
      }

      public String header(){
            return IS.getHeader();
      }


      /**
       * <p>
       * Outputs an array of transactions with their corresponding attribute values.
       * </p>
       * @return double[][] an array of transactions with their corresponding attribute values
       */
      public double[][] getTrueTransactions() {
        return trueTransactions;
      }

      /**
       * <p>
       * Outputs an array of transactions with their corresponding attribute values.
       * </p>
       * @param i             Position of the example
       * @param j             Positoin of the variable
       * @return The value of the transaction for that position
       */
      public double getValTrueTransactions(int i, int j){
        return trueTransactions[i][j];
      }

      /**
       * <p>
       * Outputs an array of transactions with their recasted attribute values.
       * </p>
       * @return double[][] an array of transactions with their recasted attribute values
       */
      public int[][] getFakeTransactions() {
        return fakeTransactions;
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
       * It returns the upper bound of the variable
       * <(p>
       * @param variable Id otf the attribute
       * @return double the upper bound of the variable
       */
      public double getMax(int variable) {
        return emax[variable];
      }

      /**
       * <p>
       * It returns the lower bound of the variable
       * </p>
       * @param variable Id of the attribute
       * @return double the lower bound of the variable
       */
      public double getMin(int variable) {
        return emin[variable];
      }

      /**
       * <p>
       * It gets the size of the data-set
       * </p>
       * @return int the number of transactions in the data-set
       */
      public int getnTrans() {
        return nTrans;
      }

      /**
       * <p>
       * It gets the number of variables of the data-set
       * </p>
       * @return int the number of variables of the data-set
       */
      public int getnVars() {
        return nVars;
      }

      /**
       * <p>
       * It gets the number of values for the target variable
       * </p>
       * @return int the number of values fot the target variable
       */
      public int getNValOutput(){
        return numOutputs;
      }

      /**
       * <p>
       * It gets the number of output variables
       * </p>
       * @return int the number of output variables
       */
      public int getnOutput(){
        return nOutputs;
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
       * It reads the whole input data-set and it stores each transaction in
       * local array
       * </p>
       * @param datasetFile String name of the file containing the data-set
       * @throws IOException If there occurs any problem with the reading of the data-set
       */
      public void readDataSet(String datasetFile, boolean _isTrain) throws
          IOException {
              int i, j, k, cnt_index;

              try {
          // Load in memory a data-set that contains a Frequent Items Mining problem
          IS.readSet(datasetFile, _isTrain);
          this.nTrans = IS.getNumInstances();
              this.nInputs = Attributes.getInputNumAttributes();
              this.nOutputs = Attributes.getOutputNumAttributes();
              this.nVars = this.nInputs + this.nOutputs;

          numOutputs = Attributes.getOutputAttribute(0).getNumNominalValues();
          classes = new int[Attributes.getOutputAttribute(0).getNumNominalValues()];
          for(i = 0; i<Attributes.getOutputAttribute(0).getNumNominalValues(); i++){
            classes[i] = i;
          }

          // Initialize and fill our own tables
          this.trueTransactions = new double[nTrans][nVars];
          this.covered = new int[nTrans];
          this.fakeTransactions = new int[nTrans][nVars];
          this.steps = new double[nVars];
          this.tidList = new Hashtable<Integer, HashSet<Integer>>();

          missing = new boolean[nTrans][nVars];

          // Maximum and minimum of inputs
          emax = new double[nVars];
          emin = new double[nVars];
          for (i = 0; i < nVars; i++) {
            if ( getAttributeType(i) != myDataset.NOMINAL ) {
                            emax[i] = getMaxValue(i);
                            emin[i] = getMinValue(i);
            }
            else {
                            emin[i] = 0;
                            emax[i] = getNumNominalValues(i) - 1;
            }
          }

          for (i=0; i < nVars; i++)
              steps[i] = (emax[i] - emin[i]) / nPartitionForNumericAttributes;

          // All values are casted into double/integer
          for (i=0; i < nTrans; i++) {
            Instance inst = IS.getInstance(i);

            cnt_index = 1;
            covered[i] = 0;

            for (j=0; j < nInputs; j++) {
                    trueTransactions[i][j] = IS.getInputNumericValue(i, j);

                    fakeTransactions[i][j] = recastTrueValue(trueTransactions[i][j], j, cnt_index);
                    addTIDToValueList(fakeTransactions[i][j], i);
                    cnt_index += (getAttributeType(j) != myDataset.NOMINAL) ? nPartitionForNumericAttributes : getNumNominalValues(j);

                    missing[i][j] = inst.getInputMissingValues(j);
                    if (missing[i][j]) {
                            trueTransactions[i][j] = emin[j] - 1;
                            fakeTransactions[i][j] = 0;
                    }
            }

            for (k=0; k < nOutputs; k++, j++) {
                trueTransactions[i][j] = IS.getOutputNumericValue(i, k);
                fakeTransactions[i][j] = recastTrueValue(trueTransactions[i][j], j, cnt_index);
                addTIDToValueList(fakeTransactions[i][j], i);
                cnt_index += (getAttributeType(j) != myDataset.NOMINAL) ? nPartitionForNumericAttributes : getNumNominalValues(j);
            }
          }
        }
        catch (Exception e) {
          System.out.println("DBG: Exception in readSet");
          e.printStackTrace();
        }
      }

      private int recastTrueValue(double true_value, int id_attr, int cnt_index) {
            int p, fake_value = (int)true_value;

            if (getAttributeType(id_attr) != myDataset.NOMINAL) {
                    boolean stop = false;

                    for (p=0; p < nPartitionForNumericAttributes-1 && (! stop); p++) {
                            if ( (true_value >= (emin[id_attr] + steps[id_attr] * p)) && (true_value <= (emin[id_attr] + steps[id_attr] * (p + 1))) ) {
                                    fake_value = p;
                                    stop = true;
                            }
                    }

                    if (! stop) fake_value = p;
            }

        return (fake_value + cnt_index);
      }

      private void addTIDToValueList(int value, int tid) {
              HashSet<Integer> lst = (HashSet<Integer>) tidList.get(value);

              if (lst == null) {
                      lst = new HashSet<Integer>();
              tidList.put(value, lst);
          }

              lst.add(tid);
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
       * It checks if the data-set has any numerical value (real or integer)
       * </p>
       * @return boolean True if it has some numerical values, else false.
       */
      public boolean hasNumericalAttributes() {
        return (Attributes.hasIntegerAttributes() ||
                Attributes.hasRealAttributes());
      }

      /**
       * <p>
       * It checks if the data-set has any missing value
       * </p>
       * @return boolean True if it has some missing values, else false.
       */
      public boolean hasMissingAttributes() {
        return (this.sizeWithoutMissing() < this.getnTrans());
      }

      /**
       * <p>
       * It return the size of the data-set without having account the missing values
       * </p>
       * @return int the size of the data-set without having account the missing values
       */
      public int sizeWithoutMissing() {
        int tam = 0;
        for (int i = 0; i < nTrans; i++) {
          int j;
          for (j = 1; (j < nVars) && (!isMissing(i, j)); j++) {
            ;
          }
          if (j == nVars) {
            tam++;
          }
        }
        return tam;
      }

      /**
       * <p>
       * It returns an array indicating the position of the missing values on a specific example
       * </p>
       * @param pos int Id of the example
       * @return boolean[] an array indicating the position of the missing values on the example
       */
      public boolean [] getMissing(int pos){
          return this.missing[pos];
      }

      /**
       * <p>
       * It returns suitable recasted IDs to recognize later each value belonging to an attribute
       * </p>
       * @return ArrayList<Integer> an array with the IDs of each attribute value
       */
      public ArrayList<Integer> getIDsOfAllAttributeValues() {
              int a, v, num_values;
              ArrayList<Integer> ids = new ArrayList<Integer>();

              for (a=0; a < nVars; a++) {
                      num_values = (getAttributeType(a) != myDataset.NOMINAL) ? nPartitionForNumericAttributes : getNumNominalValues(a);

                      for (v=0; v < num_values; v++)
                              ids.add(v * nVars + a);
              }

              return ids;
      }

      /**
       * <p>
       * It returns an array with the step values of each attribute depending on the chosen number of partitions
       * </p>
       * @return double[] an array with the steps values of each attribute
       */
      public double[] getSteps() {
              return steps;
      }

      /**
       * <p>
       * It outputs an array of attribute values with their corresponding TIDs
       * </p>
       * @return an Hashtable of attribute values with their corresponding TIDs stored in an HashSet
       */
      public Hashtable<Integer, HashSet<Integer>> getTIDList() {
              return tidList;
      }

      /**
       * <p>
       * It returns the name of the attribute in "id_attr"
       * </p>
       * @param id_attr int Id of the attribute
       * @return String the name of the attribute
       */
      public String getAttributeName(int id_attr) {
            if (id_attr < this.nInputs) return ( Attributes.getInputAttribute(id_attr).getName() );
            else return ( Attributes.getOutputAttribute(id_attr - this.nInputs).getName() );
      }

      /**
       * <p>
       * It returns the type of the attribute in "id_attr"
       * </p>
       * @param id_attr int Id of the attribute
       * @return int the type of the attribute
       */
      public int getAttributeType(int id_attr) {
            if (id_attr < this.nInputs) return ( Attributes.getInputAttribute(id_attr).getType() );
            else return ( Attributes.getOutputAttribute(id_attr - this.nInputs).getType() );
      }

      /**
       * <p>
       * It returns the nominal value "id_val" within the attribute "id_attr"
       * </p>
       * @param id_attr int Id of the attribute
       * @param id_val int Id of the nominal value within the attribute
       * @return String the nominal value
       */
      public String getNominalValue(int id_attr, int id_val) {
            if (id_attr < this.nInputs) return ( Attributes.getInputAttribute(id_attr).getNominalValue(id_val) );
            else return ( Attributes.getOutputAttribute(id_attr - this.nInputs).getNominalValue(id_val) );
      }

      private double getMaxValue(int id_attr) {
            if (id_attr < this.nInputs) return ( Attributes.getInputAttribute(id_attr).getMaxAttribute() );
            else return ( Attributes.getOutputAttribute(id_attr - this.nInputs).getMaxAttribute() );
      }

      private double getMinValue(int id_attr) {
            if (id_attr < this.nInputs) return ( Attributes.getInputAttribute(id_attr).getMinAttribute() );
            else return ( Attributes.getOutputAttribute(id_attr - this.nInputs).getMinAttribute() );
      }

      private int getNumNominalValues(int id_attr) {
            if (id_attr < this.nInputs) return ( Attributes.getInputAttribute(id_attr).getNumNominalValues() );
            else return ( Attributes.getOutputAttribute(id_attr - this.nInputs).getNumNominalValues() );
      }
  
}