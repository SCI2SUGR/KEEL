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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.GeneticFuzzyAprioriMS;

/**
 * <p>
 * @author Written by Alvaro Lopez
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

import java.util.ArrayList;

public class FuzzyDataset {
	/**
	 * <p>
	 * It represents a fuzzy dataset which is based on the original dataset and handles fuzzy transactions
	 * </p>
	 */
	
	private myDataset dataset;
	private ArrayList<FuzzyAttribute> fuzzyAttributes;
	private double[][][] fuzzyTransactions;
	private int[] numFuzzyRegions;
	
	/**
	 * It creates a new fuzzy dataset by setting up its properties
	 * @param dataset The instance of the dataset for dealing with its records
	 * @param fuzzyAttributes The fuzzy attributes that are previously built
	 */
	public FuzzyDataset(myDataset dataset, ArrayList<FuzzyAttribute> fuzzyAttributes) {
		this.dataset = dataset;
		this.fuzzyAttributes = fuzzyAttributes;
		this.createFuzzyTransactions();
		this.setNumFuzzyRegions();
	}
		
	private void createFuzzyTransactions() {
		int trans, attr, id_attr;
		double[][] true_transactions;
		
		this.fuzzyTransactions = new double[ this.dataset.getnTrans() ][ this.fuzzyAttributes.size() ][];
		true_transactions = this.dataset.getTrueTransactions();
		
		
		for (trans=0; trans < this.fuzzyTransactions.length; trans++) {
			for (attr=0; attr < this.fuzzyTransactions[trans].length; attr++) {
				id_attr = this.fuzzyAttributes.get(attr).getIdAttr();
				this.transformIntoFuzzySet(trans, attr, true_transactions[trans][id_attr]);
			}
		}
	}
	
	private void transformIntoFuzzySet(int trans, int attr, double true_value) {
		  int region;
		  FuzzyRegion[] fuzzy_regions;
		  
		  fuzzy_regions = this.fuzzyAttributes.get(attr).getFuzzyRegions();
		  this.fuzzyTransactions[trans][attr] = new double[ fuzzy_regions.length ];
		  
		  for (region=0; region < fuzzy_regions.length; region++) {
			  this.fuzzyTransactions[trans][attr][region] = fuzzy_regions[region].getFuzzyValue(true_value);
		  }
	}
	
	private void setNumFuzzyRegions() {
		this.numFuzzyRegions = new int[ this.fuzzyAttributes.size() ];
		
		for (int i=0; i < this.numFuzzyRegions.length; i++)
			this.numFuzzyRegions[i] = this.fuzzyAttributes.get(i).getNumberOfFuzzyRegions();
	}
	
	/**
	 * It returns the membership degrees associated with each fuzzy attribute and for all the transactions
	 * @return A 3-D array containing the membership degrees associated with each fuzzy attribute and for all the transactions
	 */
	public double[][][] getFuzzyTransactions() {
		return this.fuzzyTransactions;
	}
	
	/**
	 * It returns the number of fuzzy attributes composing a fuzzy dataset
	 * @return A value representing the number of fuzzy attributes composing a fuzzy dataset
	 */
	public int getNumberOfFuzzyAttributes() {
		return ( this.fuzzyAttributes.size() );
	}
	
	/**
	 * It returns the number of fuzzy regions of each involved fuzzy attributes
	 * @return An array containing the number of fuzzy regions of each involved fuzzy attributes
	 */
	public int[] getNumberOfFuzzyRegions() {
		return this.numFuzzyRegions;
	}
	
}