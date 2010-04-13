package keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet;

/**
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penya, Aaron Ruiz Mora (University of Cordoba) 17/07/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public interface ILayer<N extends INeuron> {
	
	/**
	 * <p>
	 * Represents a layer in the neural net
	 * </p>
	 */
    
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Layer Attributes
	/////////////////////////////////////////////////////////////////
	
    /**
     * <p>
	 * Add a neuron to the layer
	 * </p>
	 * @param neuron New neuron to add to the layer
	 */
    public void addNeuron(N neuron);
    
    /**
     * <p>
	 * Returns the maximum number of neurons of this layer
	 * </p>
	 * @return int Maximum number of neurons
	 */
    public int getMaxnofneurons();
    
    /**
     * <p>
	 * Returns a neuron of the layer, using its index
	 * </p>
	 * @param index Index of the neuron to return
	 * @return INeuron Neuron in the layer
	 */
    public N getNeuron(int index);
    
    /**
     * <p>
	 * Returns the number of neurons of this layer
	 * </p>
	 * @return int Number of neurons
	 */
    public int getNofneurons();
    
    
	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////
    
	/**
	 * <p>
	 * Checks if this layer is equal to another
	 * </p>
	 * @param other Other layer to compare
	 * @return true if both layers are equal
	 */
    public boolean equals(ILayer<N> other);
    
	/**
	 * </p>
	 * Returns an integer number that identifies the layer
	 * <p>
	 * @return int Hashcode
	 */
    public int hashCode();
}
