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

package keel.Algorithms.Neural_Networks.NNEP_Common.mutators.structural;



import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.ILayer;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.INeuron;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.LinkedLayer;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.LinkedNeuron;

/**  
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penia (University of Cordoba) 16/7/2007
 * @author Written by Aaron Ruiz Mora (University of Cordoba) 16/7/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public interface INeuronStructuralMutator<N extends LinkedNeuron> {
	
	
	/**
	 * <p>
	 * Structural Mutator of a specific neuron
	 * </p>
	 */
	
	/**
	 * <p>
	 * Adds a neuron into a specific layer
	 * </p>
	 * @param neuron Neuron to add into the layer
	 * @param layer Hidden layer to add the neuron
	 * @param previousLayer Previous layer to the selected hidden layer
	 * @param nextLayer Next layer to the selected hidden layer
	 */
	
	public void addNeuron(N neuron, LinkedLayer layer, ILayer<? extends INeuron> previousLayer,
			LinkedLayer nextLayer);
	
	/**
	 * <p>
	 * Removes a neuron of a specific layer
	 * </p>
	 * @param layer Hidden layer to remove the neuron
	 * @param nextLayer Next layer to the selected hidden layer
	 * @param indexNeuron Index of neuron to remove
	 */
	
	public void removeNeuron(LinkedLayer layer, LinkedLayer nextLayer, int indexNeuron);
	
	/**
	 * <p>
	 * Adds a link to a neuron of an specific layer from
	 * a specific origin neuron
	 * </p>
	 * @param neuron Neuron to add the link
	 * @param layer Hidden layer to add the link
	 * @param previousLayer Previous layer to the selected hidden layer
	 * @param indexNeuron Index of neuron to add the link
	 * @param indexOrigin Index of neuron that its link come from in the previous layer
	 */
	
	public boolean addLink(N neuron, LinkedLayer layer, ILayer<? extends INeuron> previousLayer,
			int indexNeuron, int indexOrigin);
	
	/**
	 * <p>
	 * Removes a link of a neuron of an specific layer from
	 * a specific origin neuron
	 * </p>
	 * @param neuron Neuron to remove the link
	 * @param indexOrigin Index of neuron that its link come from in the previous layer
	 */
	
	public boolean removeLink(N neuron, int indexOrigin);
	
	/**
	 * <p>
	 * Units the weights of two specific neurons, and stores
	 * the result in the first neuron
	 * </p>
	 * @param firstNeuron First Neuron to unit its weights
	 * @param secondNeuron Second Neuron to unit its weights
	 * @param layer Hidden layer to unit the weights of two selected neurons
	 * @param nextLayer Next layer to the selected hidden layer
	 * @param indexFirstNeuron Index of the first neuron to unit
	 * @param indexSecondNeuron Index of the second neuron to unit
	 */
	
	public void unitNeuronsWeights(N firstNeuron, N secondNeuron, LinkedLayer layer, LinkedLayer nextLayer,
			int indexFirstNeuron, int indexSecondNeuron);
	
}

