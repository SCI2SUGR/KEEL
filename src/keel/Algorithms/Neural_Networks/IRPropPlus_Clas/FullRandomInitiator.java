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

package keel.Algorithms.Neural_Networks.IRPropPlus_Clas;

import keel.Algorithms.Neural_Networks.NNEP_Common.initiators.RandomInitiator;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.ILayer;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.INeuron;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.Link;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.LinkedLayer;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.LinkedNeuron;

/**
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penia (University of Cordoba)(5/11/2007)
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public class FullRandomInitiator extends RandomInitiator {
	/**
	 * <p>
	 * Random initiator generating a Full model
	 * </p>
	 */
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
	 * Empty constructor
	 * </p>
	 */
	
	public FullRandomInitiator() {
		super();
	}


	/////////////////////////////////////////////////////////////////
	// -------------------------- Overwriting RandomInitiator methods
	/////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
	 * Create all the links of a neural net.
	 * 
	 * @param linkedLayer Linked layer where create the links
	 * @param previousLayer LinkedLayer the neurons are going to be connected
	 * @param newNeuron New neuron to create its links	 * 
	 * @return Random generator
	 * </p>
	 */
	
	@Override
	public Link [] createLinks(LinkedLayer linkedLayer,
			ILayer<? extends INeuron> previousLayer, LinkedNeuron newNeuron) {
		
		//Array of links (Enough space for the maximum of neurons)
		Link links[];
		
		if(linkedLayer.isBiased()){
			links = new Link[previousLayer.getMaxnofneurons()+1];
			links[previousLayer.getMaxnofneurons()] = new Link();
			links[previousLayer.getMaxnofneurons()].setBroken(false);
			newNeuron.setBiased(true);
		}
		else
			links = new Link[previousLayer.getMaxnofneurons()];
		
		//For each effective link
		for(int j=0; j<previousLayer.getMaxnofneurons(); j++){  
			links[j] = new Link();
			links[j].setOrigin(previousLayer.getNeuron(j));
			links[j].setTarget(newNeuron);
			links[j].setBroken(false);
		}
		
		return links;
	}

}

