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
* @author Written by Manuel Moreno (Universidad de Córdoba) 01/07/2008
* @version 0.1
* @since JDK 1.5
*</p>
*/

package keel.Algorithms.Decision_Trees.CART.impurities;

import keel.Algorithms.Neural_Networks.NNEP_Common.data.DoubleTransposedDataSet;

/**
 * Implementation of GINI impurity Function
 * 
 *
 */
public class Gini implements IImpurityFunction {

	/** Complete Data set of patterns */
	private DoubleTransposedDataSet dataset;


	/** 
	 * 
	 * It sets the datasets of patters
	 * 
	 * @param dataset Complete data set of patterns
	 */
	public void setDataset(DoubleTransposedDataSet dataset) {
		this.dataset = dataset;
	}
	
	/**
	 * 
	 * It compute the impurity value associated
	 * 
	 * @param patterns index of patterns from dataset associated to node to evaluate
	 * @param cost Associated cost
	 * @return Impurity value associated
	 * @throws Exception 
	 * 
	 */
	public double impurities(int [] patterns, double cost) {
		
		int nofoutputs = dataset.getNofoutputs();
		int nofpatterns = patterns.length;
		// Probabilities for each class in current data set portion
		double []prob_j = new double[nofoutputs];
		for (int i=0; i<patterns.length; i++) {
			int patternIndex = patterns[i]; // Current pattern
			int patternClass=-1; // Initialize variable
		
			// Find which class owns current pattern
			for (int j=0; j<nofoutputs; j++) {
				if ( dataset.getAllOutputs()[j][patternIndex] == 1.0) {
					patternClass = j;
					break;
				}
			}
			// Increment number of patterns in that class
			prob_j[patternClass]++;
		}
		
		// calculate real probabilities
		for (int i=0; i<nofoutputs; i++) 
			prob_j[i] = prob_j[i]/nofpatterns;
		
		// Calculate impurities as 2*SUM(SUM( Cost*p(j|node)*p(k|node))) for each class
		// This can be replaced using 2*SUM(SUM(Cost*p(j|node)*p(k|node))) for j>k
		double impurities = 0f;
		for (int j=0; j<nofoutputs -1; j++) {
			for (int k=j+1; k<nofoutputs; k++) {
				impurities += cost*prob_j[j]*prob_j[k];
			}
		}
		impurities = 2*impurities;
		
		/* TODO Alternative way of compute impurities
		double info = 1.0;
		for (int j=0; j<nofoutputs;j++)
			info += -Math.pow(prob_j[j],2.0d);
		*/
		
		// Return impurities
		 return impurities;
		//return info;
	}

}