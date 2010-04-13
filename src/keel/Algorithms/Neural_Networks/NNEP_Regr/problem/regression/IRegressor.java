package keel.Algorithms.Neural_Networks.NNEP_Regr.problem.regression;

/**  
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penia (University of Cordoba) 16/7/2007
 * @author Written by Aaron Ruiz Mora (University of Cordoba) 16/7/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public interface IRegressor {
	
	/**
	 * <p>
	 * Generic regressor.
	 * </p>
	 */
	
	/**
	 * <p>
	 * Estimates output value of a observation, through
	 * its inputs values 
	 * </p>
	 * @param inputs Double array with all inputs of the observation
	 * 
	 * @return double Output of the regressor for these inputs
	 */
    
    public double operate(double []inputs);
    
	/**
	 * <p>
	 * Estimates output values of a set of observations, through
	 * their inputs values
	 * </p>
	 * @param inputs Double matrix with all inputs of all observations
	 * 
	 * @return double[] Output values of the regressor for all 
	 *                  observation inputs
	 */
    
    public double[] operate(double [][]inputs);
}
