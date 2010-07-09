package keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet;

/**
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penya, Aaron Ruiz Mora (University of Cordoba) 17/07/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public interface INeuralNet{
	
	/**
	 * <p>
	 * Represents a neural net
	 * </p>
	 */
    
    /////////////////////////////////////////////////////////////////
    // ----------------------------------------------- Net attributes
    /////////////////////////////////////////////////////////////////
    
    /**
     * <p>
     * Returns the current number of hidden layers of the neural net
     * </p>
     * @return int Number of hidden layers
     */
    public int getNofhlayers();
    
    /**
     * <p>
     * Returns the input layer of this neural net
     * </p>
     * @return InputLayer Input layer of the net
     */
    public InputLayer getInputLayer();
    
    /**
     * <p>
     * Returns a specific hidden layer of the neural net
     * </p>
     * @param index Number of layer to return
     * @return LinkedLayer Hidden layer
     */
    public LinkedLayer getHlayer(int index);
    
    /**
     * <p>
     * Returns the output layer of this neural net
     * </p>
     * @return LinkedLayer Output layer of the net
     */
    public LinkedLayer getOutputLayer();
    
    /**
     * <p>
	 * Sets the input layer of this neural net
	 * </p>
	 * @param inputLayer New input layer of the net
	 */
    public void setInputLayer(InputLayer inputLayer);
    
    /**
     * <p>
     * Adds a new layer to the neural net
     * </p>
     * @param layer New hidden layer
     */
    public void addHlayer(LinkedLayer layer);
    
    /**
     * <p>
	 * Sets the output layer of this neural net
	 * </p>
	 * @param outputLayer New output layer of the net
	 */
    public void setOutputLayer(LinkedLayer outputLayer);
    
    /////////////////////////////////////////////////////////////////
    // ----------------------------------------------- Public methods
    /////////////////////////////////////////////////////////////////    
    
    /**
     * <p>
     * Returns a copy of the neural net
     * </p>
     * @return INeuralNet Copy of the neural net
     */
    public INeuralNet copy();
    
    /**
     * <p>
     * Checks if this neural net is equal to another
     * </p>
     * @param other Other neural net to compare
     * @return true if both neural nets are equals
     */
    public boolean equals(INeuralNet other);
    
    /**
     * <p>
	 * Returns an integer number that identifies the neural net
	 * </p>
	 * @return int Hashcode
	 */
    public int hashCode();
    
    /**
     * <p>
     * Checks if this neural net is full of neurons
     * </p>
     * @return true if the neural net is full of neurons
     */
    public boolean neuronsFull();
    
    /**
     * <p>
     * Checks if this neural net is empty of neurons
     * </p>
     * @return true if the neural net is empty of neurons
     */
    public boolean neuronsEmpty();
    
    /**
     * <p>
     * Checks if this neural net is full of links
     * </p>
     * @return true if the neural net is full of links
     */
    public boolean linksFull();
    
    /**
     * <p>
     * Checks if this neural net is empty of links
     * </p>
     * @return true if the neural net is empty of links
     */
    public boolean linksEmpty();
    
    /**
     * <p>
	 * Returns the number of hidden neurons of this neural net
	 * </p>
	 * @return int Number of hidden neurons
	 */
    public int getNofhneurons();
    
    /**
     * <p>
	 * Returns the number of effective links of this neural net
	 * </p>
	 * @return int Number of effective links
	 */
    public int getNoflinks();
    
    /**
     * <p>
	 * Keep relevant links, that is, those links whose weight is higher
	 * than certain number
	 * </p>
     * @param significativeWeight Significative weight
	 */
    public void keepRelevantLinks(double significativeWeight);
}
