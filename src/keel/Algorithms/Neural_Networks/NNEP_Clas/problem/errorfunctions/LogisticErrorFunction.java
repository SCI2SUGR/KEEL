package keel.Algorithms.Neural_Networks.NNEP_Clas.problem.errorfunctions;

import keel.Algorithms.Neural_Networks.NNEP_Common.problem.errorfunctions.IErrorFunction;



/**
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penia (University of Cordoba) 16/7/2007
 * @author Modified by Aaron Ruiz Mora (University of Cordoba) 16/7/2007
 * @version 0.1
 * @since JDK1.5
 */

public class LogisticErrorFunction implements IErrorFunction<double[][]> {
	
	/**
	 * <p>
	 * Logistic Error Function.
	 * </p>
	 */
	
	/////////////////////////////////////////////////////////////////
	// -------------------------------------------------- Constructor
	/////////////////////////////////////////////////////////////////
    
    /**
     * <p>
     * Empty constructor
     * </p>
     */
    
    public LogisticErrorFunction() {
        super();
    }
	
	/////////////////////////////////////////////////////////////////
	// ------------------------ Implementing IErrorFunction Interface
	/////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
     * Returns the logistic error of a matrix of obtained values for
     * classification, compared with a matrix of expected values
     *
     * @param obtained Double matrix of obtained values
     * @param expected Double matrix of expected values
     * 
     * @return double Logistic error value
     * </p>
     */
	
	public double calculateError(double[][] obtained, double[][] expected) {
		
		// Resulting logistic error
		double logError = 0;
		
		// Compute value
		for(int i=0; i<obtained[0].length; i++){
			double sum1 = 0;
			double sum2 = 0;
			for(int j=0; j<obtained.length; j++){
				sum1 += expected[j][i] * obtained[j][i];
				sum2 += Math.exp(obtained[j][i]);
			}
			logError += (- sum1 + Math.log(sum2));
				
		}
		
		// Mean Logistic Error
		logError /= obtained[0].length;

		if(Double.isInfinite(logError) || Double.isNaN(logError))
			logError = Double.MAX_VALUE;
		
		return logError;
	}

}
