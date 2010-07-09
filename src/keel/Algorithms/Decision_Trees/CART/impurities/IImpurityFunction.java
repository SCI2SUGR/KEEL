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
 * This interface must be followed by any impurity function 
 * 
 */
public interface IImpurityFunction {
	
	/** 
	 * @param dataset Complete data set of patterns
	 */
	public void setDataset(DoubleTransposedDataSet dataset);
	
	/**
	 * 
	 * @param patterns index of patterns from dataset associated to node to evaluate
	 * @param inputvar Input variable to consider
	 * @param splitValue Value used to split the patterns using (inputvar <= splitValue)
	 * @param cost Associated cost
	 * @return Impurity value associated
	 * @throws Exception 
	 */
	public double impurities (int [] patterns,  double cost) throws Exception;
	
	
	

}
