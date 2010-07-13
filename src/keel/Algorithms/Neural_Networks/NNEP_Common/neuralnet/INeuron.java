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

