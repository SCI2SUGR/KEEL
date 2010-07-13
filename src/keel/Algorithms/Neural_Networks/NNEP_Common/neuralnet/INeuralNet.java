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

