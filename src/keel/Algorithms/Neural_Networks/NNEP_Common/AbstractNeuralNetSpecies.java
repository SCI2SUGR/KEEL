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

package keel.Algorithms.Neural_Networks.NNEP_Common;

import net.sf.jclec.util.range.Interval;

/**
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penia (University of Cordoba) 16/7/2007
 * @param <I> Type of represented individuals
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public abstract class AbstractNeuralNetSpecies<I extends NeuralNetIndividual> implements INeuralNetSpecies<I> 
{	
	/**
	 * <p>
	 * Abstract implementation for INeuralNetSpecies.
	 * </p>
	 */
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////
	
	/** Type of neuralnets  */
	
	protected String neuralNetType;
	
	/** Number of inputs of the neural nets  */
	
	protected int nOfInputs;
	
	/** Number of hidden layers of the neural nets  */
	
	protected int nOfHiddenLayers;
	
	/** Number of outputs  */
	
	protected int nOfOutputs;
	
	/** Weight ranges of each LinkedLayer of the neural nets  */
	
	protected Interval[][] weightRanges;

	/** Maximum number of neurons of each LinkedLayer of the neural nets  */
	
	protected int[] maxNofneurons;

	/** Minimum number of neurons of each LinkedLayer of the neural nets  */
	
	protected int[] minNofneurons;

	/** Initial number of neurons of each LinkedLayer of the neural nets  */
	
	protected int[] initialMaxNofneurons;

	/** Type of each LinkedLayer of the neural nets  */
	
	protected String[] type;
	
	/** Initiator of each LinkedLayer of the neural nets  */
	
	protected String[] initiator;
	
	/** Boolean indicating if each linked layer of neural nets are biased  */
	
	protected boolean[] biased;
	
	/** Types of each neuron for hibrid layers */
	
	protected String[][] neuronTypes;
	
	/** Percentages of each neuron type for hibrid layers */
	
	protected double[][] percentages;
	
	/** Initiator of neurons of each HibridLayer of the neural nets  */
	
	protected String[][] initiatorNeuronTypes;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
	 * Empty constructor
	 * </p>
	 */
	
	public AbstractNeuralNetSpecies() 
	{
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ------------ Implementing INeuralNetSpecies interface
	/////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
	 * Returns a neural net type
	 * 
	 * @return int Number of hidden layers
	 * </p>
	 */
	
	public String getNeuralNetType() {
		return neuralNetType;
	}
	
	/**
	 * <p>
	 * Returns number of hidden layers of the neural nets
	 * 
	 * @return int Number of hidden layers
	 * </p>
	 */
	
	public int getNOfHiddenLayers() {
		return nOfHiddenLayers;
	}
	
	/**
	 * <p>
	 * Returns number of inputs of the neural nets
	 * 
	 * @return int Number of inputs
	 * </p>
	 */
	
	public int getNOfInputs() {
		return nOfInputs;
	}

	/**
	 * <p>
	 * Returns number of outputs of the neural nets
	 * 
	 * @return int Number of outputs
	 * </p>
	 */
	
	public int getNOfOutputs() {
		return nOfOutputs;
	}
	
	/**
	 * <p>
	 * Returns weight range of a hidden layer
	 * 
	 * @param index Index of the desired hidden layer
	 * @param indexRange Index of the desired range into the layer (useful for hibrid layer) 
	 * 
	 * @return Interval Weight range
	 * </p>
	 */
	
	public Interval getHiddenLayerWeightRange(int index, int indexRange) {
		return weightRanges[index][indexRange];
	}
	
	/**
	 * <p>
	 * Returns weight range of the output layer
	 * 
	 * @param indexRange Index of the desired range into the layer (useful for hibrid layer) 
	 * 
	 * @return Interval Weight range
	 * </p>
	 */
	
	public Interval getOutputLayerWeightRange(int indexRange) {
		return weightRanges[weightRanges.length-1][indexRange];
	}
	
	/**
	 * <p>
	 * Returns minimum number of neurons of a hidden layer
	 * 
	 * @param index Index of the desired hidden layer
	 * 
	 * @return int Minimum number of neurons
	 * </p>
	 */
	
	public int getHiddenLayerMinNofneurons(int index) {
		return minNofneurons[index];
	}
	
	/**
	 * <p>
	 * Returns maximum number of neurons of a hidden layer
	 * 
	 * @param index Index of the desired hidden layer
	 * 
	 * @return int Maximum number of neurons
	 * </p>
	 */
	
	public int getHiddenLayerMaxNofneurons(int index) {
		return maxNofneurons[index];
	}
	
	/**
	 * <p>
	 * Returns initial maximum number of neurons of a hidden layer
	 * 
	 * @param index Index of the desired hidden layer
	 * 
	 * @return int Initial maximum number of neurons
	 * </p>
	 */
	
	public int getHiddenLayerInitialMaxNofneurons(int index) {
		return initialMaxNofneurons[index];
	}
	
	/**
	 * <p>
	 * Returns type of neurons of a hidden layer
	 * 
	 * @param index Index of the desired hidden layer
	 * 
	 * @return String Type of neurons
	 * </p>
	 */
	
	public String getHiddenLayerType(int index) {
		return type[index];
	}
	
	/**
	 * <p>
	 * Returns type of neurons of the output layer
	 * 
	 * @return String Type of neurons
	 * </p>
	 */
	
	public String getOutputLayerType() {
		return type[type.length-1];
	}
	
	/**
	 * <p>
	 * Returns initiator of neurons of a hidden layer
	 * 
	 * @param index Index of the desired hidden layer
	 * 
	 * @return String Initiator of neurons
	 * </p>
	 */
	
	public String getHiddenLayerInitiator(int index) {
		return initiator[index];
	}
	
	/**
	 * <p>
	 * Returns initiator of neurons of the output layer
	 * 
	 * @return String Initiator of neurons
	 * </p>
	 */
	
	public String getOutputLayerInitiator() {
		return initiator[initiator.length-1];
	}
	
	/**
	 * <p>
	 * Returns a boolean indicating if a hidden layer is biased
	 * 
	 * @param index Index of the desired hidden layer
	 * 
	 * @return boolean Is hidden layer biased?
	 * </p>
	 */
	
	public boolean isHiddenLayerBiased(int index){
		return biased[index];
	}
	
	/**
	 * <p>
	 * Returns a boolean indicating if output layer is biased
	 * 
	 * @return boolean Is output layer biased?
	 * </p>
	 */
	
	public boolean isOutputLayerBiased(){
		return biased[biased.length-1];
	}
	
	/**
	 * <p>
	 * Returns an array of neuron types of a concrete layer
	 * (this is an hibrid layer)
	 * 
	 * @param index Index of the desired hidden layer
	 * 
	 * @return String[] Array of neurons types
	 * </p>
	 */

	public String[] getNeuronTypes(int index) {
		return neuronTypes[index];
	}
	
	/**
	 * <p>
	 * Returns an array of percentages of a concrete layer
	 * (this is an hibrid layer)
	 * 
	 * @param index Index of the desired hidden layer
	 * 
	 * @return double[] Array of percentages
	 * </p>
	 */

	public double[] getPercentages(int index) {
		return percentages[index];
	}
	
	/**
	 * <p>
	 * Returns an array of initiators of neurons of hibrid layers
	 * 
	 * @param index Index of the desired hidden layer
	 * 
	 * @return String[] Array of percentages
	 * </p>
	 */

	public String[] getInitiatorNeuronTypes(int index) {
		return initiatorNeuronTypes[index];
	}
}

