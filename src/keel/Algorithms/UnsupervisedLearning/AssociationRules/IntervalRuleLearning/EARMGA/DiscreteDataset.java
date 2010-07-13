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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.EARMGA;

/**
 * <p>
 * @author Written by Nicolò Flugy Papè (Politecnico di Milano) 15/06/2009
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

public class DiscreteDataset {
  /**
   * <p>
   * It handles a discrete dataset used for solving the Association Rules Mining problem
   * </p>
   */
  
  private int nVars;
  private int[] nPartitions;
  private Interval[][] discreteDataset;
  
  /**
   * <p>
   * Default constructor
   * </p>
   */
  public DiscreteDataset() {
  }
  
  /**
   * <p>
   * It sets a discrete dataset by setting up its properties
   * </p>
   * @param nPartitions The number of partition in which numeric attributes are uniformly divided
   * @param dataset The instance of the dataset for dealing with its records
   */
  public DiscreteDataset(int nPartitions, myDataset dataset) {
    int i, j;
	double mark, value, rank;

	this.nVars = dataset.getnVars();
	this.nPartitions = new int[ this.nVars ];
    this.discreteDataset = new Interval[ this.nVars ][];

    for (i=0; i < this.nVars; i++) {
	  rank = Math.abs(dataset.getMax(i) - dataset.getMin(i));

	  if (dataset.getAttributeType(i) == myDataset.NOMINAL) this.nPartitions[i] = (int)rank + 1;
	  else this.nPartitions[i] = nPartitions;

	  this.discreteDataset[i] = new Interval[ this.nPartitions[i] ];

	  if (dataset.getAttributeType(i) == myDataset.NOMINAL) {
		  for (j=0; j < this.nPartitions[i]; j++) {
			  this.discreteDataset[i][j] = new Interval();
			  value = dataset.getMin(i) + j;
			  this.discreteDataset[i][j].setLeft(value);
			  this.discreteDataset[i][j].setRight(value);
		  }
	  }
	  else {
		  mark = rank / this.nPartitions[i];
		  
		  for (j=0; j < this.nPartitions[i]; j++) {
			  this.discreteDataset[i][j] = new Interval();
			  value = dataset.getMin(i) + mark * j;
			  this.discreteDataset[i][j].setLeft( this.setValue(value, dataset.getMin(i)) );
			  value = dataset.getMin(i) + mark * (j + 1);
			  this.discreteDataset[i][j].setRight( this.setValue(value, dataset.getMax(i)) );
		  }
	  }
    }
  }
  
  /**
   * <p>
   * It returns the number of intervals in which the domain of an attribute has been decomposed
   * </p>
   * @param variable The ID of the attribute
   * @return A value representing the number of intervals in which the domain of the attribute has been decomposed
   */
  public int numIntervals(int variable) {
    return this.nPartitions[variable];
  }
  
  /**
   * <p>
   * It returns the interval of an attribute
   * </p>
   * @param variable The ID of the attribute
   * @param interval The ID of the interval
   * @return An object representing the interval of the attribute
   */
  public Interval getInterval(int variable, int interval) {
    return this.discreteDataset[variable][interval];
  }
  
  /**
   * <p>
   * It checks whether a value is covered by an interval for an attribute
   * </p>
   * @param variable The ID of the attribute
   * @param interval The ID of the interval
   * @param value The value to check
   * @return True if the value is covered by the interval for that attribute; False otherwise
   */
  public boolean isCovered(int variable, int interval, double value) {
    return this.discreteDataset[variable][interval].isCovered(value);
  }
  
  private double setValue(double val, double tope) {
    if ((val > -1E-4) && (val < 1E-4)) return 0;
    if ((val > tope - 1E-4) && (val < tope + 1E-4)) return tope;
    
    return val;
  }
  
}

