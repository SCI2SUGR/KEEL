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
 * TODO Must be checked
 * 
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
	 * @return
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
