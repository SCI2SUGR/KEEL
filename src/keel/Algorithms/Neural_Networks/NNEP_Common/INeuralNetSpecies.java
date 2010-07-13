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

import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.INeuralNet;
import net.sf.jclec.ISpecies;
import net.sf.jclec.util.range.Interval;



/**
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penia (University of Cordoba) 16/7/2007
 * @param <I> Type of represented individuals
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public interface INeuralNetSpecies<I extends NeuralNetIndividual> extends ISpecies<I>
{
	/**
	 * <p>
	 * Species for Individuals that contains a NeuralNet as genotype.
	 * </p>
	 */
	
	// Factory methods
	
	/**
	 * <p>
	 * Factory method
	 * 
	 * @param genotype Individual genotype
	 * 
	 * @return I A new instance of represented class
	 * </p>
	 */
	
	public I createIndividual(INeuralNet genotype);
	
	/**
	 * <p>
	 * Factory method
	 * 
	 * @return I A new instance of individual genotype
	 * </p>
	 */
	
	public INeuralNet createGenotype();	

	// Genotype information

	/**
	 * <p>
	 * Returns number of hidden layers of the neural nets
	 * 
	 * @return int Number of hidden layers
	 * </p>
	 */
	
	public int getNOfHiddenLayers();
	
	/**
	 * <p>
	 * Returns number of inputs of the neural nets
	 * 
	 * @return int Number of inputs
	 * </p>
	 */
	
	public int getNOfInputs();
	
	/**
	 * <p>
	 * Returns number of outputs of the neural nets
	 * 
	 * @return int Number of outputs
	 * </p>
	 */
	
	public int getNOfOutputs();
	
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
	
	public Interval getHiddenLayerWeightRange(int index, int indexRange);
	
	/**
	 * <p>
	 * Returns weight range of the output layer
	 * 
	 * @param indexRange Index of the desired range into the layer (useful for hibrid layer) 
	 * 
	 * @return Interval Weight range
	 * </p>
	 */
	
	public Interval getOutputLayerWeightRange(int indexRange);
	
	/**
	 * <p>
	 * Returns maximum number of neurons of a hidden layer
	 * 
	 * @param index Index of the desired hidden layer
	 * 
	 * @return int Maximum number of neurons
	 * </p>
	 */
	
	public int getHiddenLayerMaxNofneurons(int index);
	
	/**
	 * <p>
	 * Returns minimum number of neurons of a hidden layer
	 * 
	 * @param index Index of the desired hidden layer
	 * 
	 * @return int Minimum number of neurons
	 * </p>
	 */
	
	public int getHiddenLayerMinNofneurons(int index);
	
	/**
	 * <p>
	 * Returns initial maximum number of neurons of a hidden layer
	 * 
	 * @param index Index of the desired hidden layer
	 * 
	 * @return int Initial maximum number of neurons
	 * </p>
	 */
	
	public int getHiddenLayerInitialMaxNofneurons(int index);
	
	/**
	 * <p>
	 * Returns type of neurons of a hidden layer
	 * 
	 * @param index Index of the desired hidden layer
	 * 
	 * @return String Type of neurons
	 * </p>
	 */
	
	public String getHiddenLayerType(int index);
	
	/**
	 * <p>
	 * Returns type of neurons of the output layer
	 * 
	 * @return String Type of neurons
	 * </p>
	 */
	
	public String getOutputLayerType();
	
	/**
	 * <p>
	 * Returns initiator of neurons of a hidden layer
	 * 
	 * @param index Index of the desired hidden layer
	 * 
	 * @return String Initiator of neurons
	 * </p>
	 */
	
	public String getHiddenLayerInitiator(int index);
	
	/**
	 * <p>
	 * Returns initiator of neurons of the output layer
	 * 
	 * @return String Initiator of neurons
	 * </p>
	 */
	
	public String getOutputLayerInitiator();
	
	/**
	 * <p>
	 * Returns a boolean indicating if a hidden layer is biased
	 * 
	 * @param index Index of the desired hidden layer
	 * 
	 * @return boolean Is hidden layer biased?
	 * </p>
	 */
	
	public boolean isHiddenLayerBiased(int index);
	
	/**
	 * <p>
	 * Returns a boolean indicating if output layer is biased
	 * 
	 * @return boolean Is output layer biased?
	 * </p>
	 */
	
	public boolean isOutputLayerBiased();
	
	/**
	 * <p>
	 * Returns an array of neuron types of a concrete layer
	 * 
	 * @param index Index of the desired hidden layer
	 * 
	 * @return String[] Array of neurons types
	 * </p>
	 */

	public String[] getNeuronTypes(int index);
	
	/**
	 * <p>
	 * Returns an array of percentages of a concrete layer
	 * for an hibrid layer
	 * 
	 * @param index Index of the desired hidden layer
	 * 
	 * @return double[] Array of percentages
	 * </p>
	 */

	public double[] getPercentages(int index);
	
	/**
	 * <p>
	 * Returns an array of initiators of neurons of hibrid layers
	 * 
	 * @param index Index of the desired hidden layer
	 * 
	 * @return String[] Array of percentages
	 * </p>
	 */

	public String[] getInitiatorNeuronTypes(int index);
}

