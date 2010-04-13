package keel.Algorithms.Neural_Networks.NNEP_Regr.neuralnet;



import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.AbstractNeuralNet;
import keel.Algorithms.Neural_Networks.NNEP_Regr.problem.regression.IRegressor;

/**  
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penia (University of Cordoba) 16/7/2007
 * @author Written by Aaron Ruiz Mora (University of Cordoba) 16/7/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public class NeuralNetRegressor extends AbstractNeuralNet implements IRegressor {

	/**
	 * <p>
	 * Neural net used as a regressor
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
    
    public NeuralNetRegressor() {
        super();
    }
	
	/////////////////////////////////////////////////////////////////
	// ---------------------------- Implementing IRegressor interface
	/////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
	 * Estimates output value of a observation, through
	 * its inputs values 
	 * </p>
	 * @param inputs Double array with all inputs of the observation
	 * 
	 * @return double Output of the regressor for these inputs
	 */
    
    public double operate(double []inputs){
    	return outputLayer.getNeuron(0).operate(inputs);
    }
    
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
    
    public double[] operate(double [][]inputs){
    	return outputLayer.getNeuron(0).operate(inputs);
    }
}
