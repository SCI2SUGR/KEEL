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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.Alatasetal;

/**
 * <p>
 * @author Written by Alberto Fernández (University of Granada)
 * @author Modified by Nicolò Flugy Papè (Politecnico di Milano) 24/03/2009
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

  private InstanceSet IS; //The whole instance set


  /**
   * Initialize a new set of instances
   */
  public myDataset() {
	  IS = new InstanceSet();
  }

  /**
   * Outputs an array of transactions with their corresponding attribute values.
   * @return double[][] an array of transactions with their corresponding attribute values
   */
  public double[][] getTrueTransactions() {
    return trueTransactions;
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
  * @param variable Id otf the attribute
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
      trueTransactions = new double[nTrans][nVars];
      missing = new boolean[nTrans][nVars];

      // Maximum and minimum of inputs
      emax = new double[nVars];
      emin = new double[nVars];
      for (i = 0; i < nVars; i++) {
    	if ( Attributes.getAttribute(i).getType() != myDataset.NOMINAL ) {
			emax[i] = Attributes.getAttribute(i).getMaxAttribute();
			emin[i] = Attributes.getAttribute(i).getMinAttribute();
    	}
    	else {
			emin[i] = 0;
			emax[i] = Attributes.getAttribute(i).getNumNominalValues() - 1;
    	}
      }
      
      // All values are casted into double/integer
      for (i=0; i < nTrans; i++) {
        Instance inst = IS.getInstance(i);
        
        for (j=0; j < nInputs; j++) {
          trueTransactions[i][j] = IS.getInputNumericValue(i, j);
          
          missing[i][j] = inst.getInputMissingValues(j);
          if (missing[i][j]) {
            trueTransactions[i][j] = emin[j] - 1;
          }
        }
		
		if (this.nOutputs > 0) {
			for (k=0; k < this.nOutputs; k++, j++) {
				trueTransactions[i][j] = IS.getOutputNumericValue(i, k);
			}
		}
	  }
    }
    catch (Exception e) {
      System.out.println("DBG: Exception in readSet");
      e.printStackTrace();
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
  * It returns the number of different values in the case of a nominal variable
  * @param attribute Id of the variable
  * @return the number of different values in the case of a nominal variable
  */
  public int numberValues(int attribute) {
    return Attributes.getInputAttribute(attribute).getNumNominalValues();
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
   * It returns the names of the variables
   * @return String[] an array with the names of the variables
   */
  public String[] names() {
    String nombres[] = new String[nVars];
    for (int i = 0; i < nVars; i++) {
      nombres[i] = Attributes.getInputAttribute(i).getName();
    }
    return nombres;
  }

  /**
   * It returns the name of the variable in "pos"
   * @param pos int Id of the variable
   * @return String the name of the attribute
   */
  public String getNameVar(int pos) {
    return Attributes.getInputAttribute(pos).getName();
  }

  
  /**
   * It returns the type of the attribute in "n_attr"
   * @param n_attr int Id of the attribute
   * @return int the type of the attribute
   */
  public int getAttributeType(int n_attr) {
    return Attributes.getInputAttribute(n_attr).getType();
  }

}