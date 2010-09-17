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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.FuzzyApriori;

/**
 * <p>
 * @author Written by Alvaro Lopez
 * @version 1.1
 * @since JDK1.6
 * </p>
 */

import java.io.IOException;
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
  private boolean[][] missing = null; //possible missing values
  private double[] emax; //max value of an attribute
  private double[] emin; //min value of an attribute

  private int nTrans; // Number of transactions
  private int nInputs; // Number of inputs
  private int nOutputs; // Number of outputs
  private int nVars; // Number of variables
  
  private int nFuzzyRegionsForNumericAttributes; //Number of fuzzy regions for numeric attributes
  private double[][][] fuzzyTransactions = null; //fuzzy transactions array
  private FuzzyRegion[][] fuzzyAttributes = null; //fuzzy attributes array
  private int nLabels[] = null; //number of labels for each attribute
  
  private InstanceSet IS; //The whole instance set

  
  /**
	 * <p>
	 * Initialize a new set of instances
	 * </p>
	 * @param nFuzzyRegionsForNumericAttributes The number of fuzzy regions with which numeric attributes are evaluated
	 */
  public myDataset(int nFuzzyRegionsForNumericAttributes) {
	  IS = new InstanceSet();
	  this.nFuzzyRegionsForNumericAttributes = (nFuzzyRegionsForNumericAttributes > 0) ? nFuzzyRegionsForNumericAttributes : 1;
  }

  /**
   * Outputs an array of transactions with their corresponding attribute values.
   * @return double[][] an array of transactions with their corresponding attribute values
   */
  public double[][] getTrueTransactions() {
    return trueTransactions;
  }
  
  /**
   * Outputs the fuzzy attribute values for the specified transaction.
   * @param idTrans The ID of the specified transaction
   * @return double[][] an array of fuzzy attribute values also depending on the number of fuzzy regions
   */
  public double[][] getFuzzyTransaction(int idTrans) {
    return fuzzyTransactions[idTrans];
  }
  
  /**
   * Outputs the fuzzy regions of the specified attribute.
   * @param idAttr The ID of the specified attribute
   * @return FuzzyRegion[] an array of fuzzy regions
   */
  public FuzzyRegion[] getFuzzyAttribute(int idAttr) {
    return fuzzyAttributes[idAttr];
  }
  
  /**
   * Outputs the number of labels for all attributes in the dataset.
   * @return int[] an array of values representing the number of labels for each attribute
   */
  public int[] getNLabelsOfAttributes() {
    return nLabels;
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
  * It returns the upper bound of the variable
  * @param variable Id of the attribute
  * @return double the upper bound of the variable
  */
  public double getMax(int variable) {
    return emax[variable];
  }

  /**
  * It returns the lower bound of the variable
  * @param variable Id of the attribute
  * @return double the lower bound of the variable
  */
  public double getMin(int variable) {
    return emin[variable];
  }

  /**
   * It gets the size of the data-set
   * @return int the number of transactions in the data-set
  */
  public int getnTrans() {
    return nTrans;
  }

  /**
   * It gets the number of variables of the data-set
   * @return int the number of variables of the data-set
   */
  public int getnVars() {
    return nVars;
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
   * It reads the whole input data-set and it stores each transaction in
   * local array
   * @param datasetFile String name of the file containing the data-set
   * @throws IOException If there occurs any problem with the reading of the data-set
   */
  public void readDataSet(String datasetFile) throws
      IOException {
	  int i, j, k;
	  
	  try {
      // Load in memory a data-set that contains a Frequent Items Mining problem
      IS.readSet(datasetFile, true);
      this.nTrans = IS.getNumInstances();
	  this.nInputs = Attributes.getInputNumAttributes();
	  this.nOutputs = Attributes.getOutputNumAttributes();
	  this.nVars = this.nInputs + this.nOutputs;

      // Initialize and fill our own tables
      this.trueTransactions = new double[nTrans][nVars];
      this.fuzzyTransactions = new double[nTrans][nVars][];
      this.fuzzyAttributes = new FuzzyRegion[nVars][];
      this.nLabels = new int[nVars];
      
      missing = new boolean[nTrans][nVars];

      // Maximum and minimum of attributes
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
      
      this.buildFuzzyRegions();
            
      // All values are casted into double/integer
      for (i=0; i < nTrans; i++) {
        Instance inst = IS.getInstance(i);
                
        for (j=0; j < nInputs; j++) {
        	trueTransactions[i][j] = IS.getInputNumericValue(i, j);
        	this.transformIntoFuzzySet(i, j);
        	
        	missing[i][j] = inst.getInputMissingValues(j);
        	if (missing[i][j]) {
        		trueTransactions[i][j] = emin[j] - 1;
        	}
        }
		
		for (k=0; k < nOutputs; k++, j++) {
			trueTransactions[i][j] = IS.getOutputNumericValue(i, k);
			this.transformIntoFuzzySet(i, j);
	    }
	  }
    }
    catch (Exception e) {
      System.out.println("DBG: Exception in readSet");
      e.printStackTrace();
    }
  }
  
  private void buildFuzzyRegions() {
	  int id_attr, id_label;
	  double rank, mark, value;
	  String str_label;
	  
	  for (id_attr=0; id_attr < this.nVars; id_attr++) {
		  rank = Math.abs(emax[id_attr] - emin[id_attr]);
		  this.nLabels[id_attr] = (getAttributeType(id_attr) == myDataset.NOMINAL) ? ((int) rank) + 1 : this.nFuzzyRegionsForNumericAttributes; 
		  this.fuzzyAttributes[id_attr] = new FuzzyRegion[ this.nLabels[id_attr] ];
		  mark = rank / (this.nLabels[id_attr] - 1.0);
		  
		  for (id_label=0; id_label < this.nLabels[id_attr]; id_label++) {
			  this.fuzzyAttributes[id_attr][id_label] = new FuzzyRegion();
			  
			  value = emin[id_attr] + mark * (id_label - 1);
			  this.fuzzyAttributes[id_attr][id_label].setX0( this.setValue(value, emax[id_attr]) );
			  
			  value = emin[id_attr] + mark * id_label;
			  this.fuzzyAttributes[id_attr][id_label].setX1( this.setValue(value, emax[id_attr]) );
			  
			  value = emin[id_attr] + mark * (id_label + 1);
			  this.fuzzyAttributes[id_attr][id_label].setX3( this.setValue(value, emax[id_attr]) );
			  
			  this.fuzzyAttributes[id_attr][id_label].setY(1.0);
			  
			  str_label = (getAttributeType(id_attr) == myDataset.NOMINAL) ? new String( getNominalValue(id_attr, id_label) ) : new String("LABEL_" + id_label);
			  this.fuzzyAttributes[id_attr][id_label].setLabel(str_label);
		  }
	  }
  }
  
  private double setValue(double val, double tope) {
	  if (val > -1E-4 && val < 1E-4) return 0.0;
	  
	  if (val > tope - 1E-4 && val < tope + 1E-4) return tope;
	  
	  return val;
  }
  
  private void transformIntoFuzzySet(int id_trans, int id_attr) {
	  int id_label;
	  
	  this.fuzzyTransactions[id_trans][id_attr] = new double[ this.nLabels[id_attr] ];
	  
	  for (id_label=0; id_label < this.nLabels[id_attr]; id_label++) {
		  this.fuzzyTransactions[id_trans][id_attr][id_label] = this.fuzzyAttributes[id_attr][id_label].getFuzzyValue(this.trueTransactions[id_trans][id_attr]);
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
   * It checks if the data-set has any numerical value (real or integer)
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
    return (this.sizeWithoutMissing() < this.getnTrans());
  }

  /**
   * It return the size of the data-set without having account the missing values
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
   * It returns an array indicating the position of the missing values on a specific example
   * @param pos int Id of the example
   * @return boolean[] an array indicating the position of the missing values on the example
   */
  public boolean [] getMissing(int pos){
      return this.missing[pos];
  }
  
  /**
   * It returns the name of the attribute in "id_attr"
   * @param id_attr int Id of the attribute
   * @return String the name of the attribute
   */
  public String getAttributeName(int id_attr) {
	if (id_attr < this.nInputs) return ( Attributes.getInputAttribute(id_attr).getName() );
	else return ( Attributes.getOutputAttribute(id_attr - this.nInputs).getName() );
  }
  
  /**
   * It returns the type of the attribute in "id_attr"
   * @param id_attr int Id of the attribute
   * @return int the type of the attribute
   */
  public int getAttributeType(int id_attr) {
	if (id_attr < this.nInputs) return ( Attributes.getInputAttribute(id_attr).getType() );
	else return ( Attributes.getOutputAttribute(id_attr - this.nInputs).getType() );
  }
  
  /**
   * It returns the nominal value "id_val" within the attribute "id_attr"
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