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

