package keel.Algorithms.Neural_Networks.NNEP_Regr.problem.errorfunctions;

import keel.Algorithms.Neural_Networks.NNEP_Common.problem.errorfunctions.IErrorFunction;

/**  
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penia (University of Cordoba) 16/7/2007
 * @author Written by Aaron Ruiz Mora (University of Cordoba) 16/7/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public class MSEErrorFunction implements IErrorFunction<double[]> {
	
	/**
	 * <p>
	 * MSE Error Function.
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
    
    public MSEErrorFunction() {
        super();
    }
    
	/////////////////////////////////////////////////////////////////
	// ------------------------ Implementing IErrorFunction Interface
	/////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
     * Returns the MSE of an array of obtained values, compared with
     * an array of expected values
     * </p>
     *
     * @param obtained Double array of obtained values
     * @param expected Double array of expected values
     * 
     * @return double MSE value
     */
	
	public double calculateError(double[] obtained, double[] expected) {
		
		// Resulting MSE
	    double MSE = 0;
	    
        //Squared Error
        for(int i=0; i<obtained.length; i++)
            MSE += Math.pow((obtained[i] - expected[i]), 2);
        
        //Mean Squared Error
        MSE /= obtained.length;

		if(Double.isInfinite(MSE) || Double.isNaN(MSE))
			MSE = Double.MAX_VALUE;
			
        return MSE;
	}

}
