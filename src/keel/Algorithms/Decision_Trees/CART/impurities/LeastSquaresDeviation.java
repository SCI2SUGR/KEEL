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
 * Implementation of Least Square Deviation impurity Function
 *
 */
public class LeastSquaresDeviation implements IImpurityFunction {

	/** Complete Data set of patterns */
	private DoubleTransposedDataSet dataset;


	/**
	 * {@inheritDoc}}
	 */
	public void setDataset(DoubleTransposedDataSet dataset) {
		this.dataset = dataset;
	}
	
	/**
	 * 
	 * {@inheritDoc}
	 * @throws Exception 
	 */
	public double impurities(int [] patterns, double cost) throws Exception {
		int nofpatterns = patterns.length;
		
		if (dataset.getNofoutputs() > 1)
			throw new Exception("Illegal number of outputs for a regression method");
		
		double [] outputs = dataset.getOutput(0);
		double mean = computeMean(patterns);
		
		double impurities = 0f;	

		// For each pattern SUM( (y-mean)^2 )
		for (int i = 0; i < patterns.length; i ++){
			int patternIndex = patterns[i];
			impurities += Math.pow((outputs[patternIndex] - mean),2);
		}
		
		// Return impurities
		return impurities;
	}


	/**
	 * Compute mean of pattern output values
	 * @param patterns
	 * @return the mean of pattern output values
	 */
	private double computeMean(int [] patterns) {
		double mean=0f;
		double [] outputs = dataset.getOutput(0);
		
		for (int i=0; i<patterns.length; i++) {
			int patternIndex = patterns[i];
			mean += outputs[patternIndex];
		}
		mean = mean/patterns.length;
		
		return mean;
	}
}

