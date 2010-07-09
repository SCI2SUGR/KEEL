package keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet;

/**
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penya, Aaron Ruiz Mora (University of Cordoba) 17/07/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public interface INeuron{
	
	/**
	 * <p>
	 * Represents a neuron in the neural net
	 * </p>
	 */
    
	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////
    
	/**
	 * <p>
	 * Checks if this neuron is equal to another
	 * </p>
	 * @param other Other neuron to compare
	 * @return true if both neurons are equal
	 */
    public boolean equals(INeuron other);
    
	/**
	 * <p>
	 * Returns an integer number that identifies the neuron
	 * </p>
	 * @return int Hashcode
	 */
    public int hashCode();
    
	/**
	 * <p>
	 * Operates this neuron using an array of inputs for the inputs neurons
	 * </p>
	 * @param inputs Array to be used for the inputs observations
	 * @return double Output of the neuron for the array specified
	 */
    public double operate(double []inputs);
    
	/**
	 * <p>
	 * Operates this neuron using an input matrix for the inputs neurons
	 * </p>
	 * @param inputs Matrix to be used for the inputs observations
	 * @return double [] Outputs of the neuron for the matrix specified
	 */
    public double [] operate(double[][] inputs);
    
}
