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

package keel.Algorithms.Neural_Networks.NNEP_Common.initiators;

import keel.Algorithms.Neural_Networks.NNEP_Common.INeuralNetSpecies;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.ILayer;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.INeuron;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.InputLayer;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.Link;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.LinkedLayer;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.LinkedNeuron;
import net.sf.jclec.IIndividual;
import net.sf.jclec.IPopulation;
import net.sf.jclec.ISpecies;
import net.sf.jclec.ISystem;
import net.sf.jclec.util.random.IRandGen;

/**
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penna, Aaron Ruiz Mora (University of Cordoba) 17/07/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public abstract class PureLayerInitiator implements IInitiator {
	
	/**
	 * <p>
	 * Abstract implementation for IInitiator
	 * </p>
	 */
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////
	
	/** Execution context */
	
	protected IPopulation<? extends IIndividual> context;
	
	/** Associated species */
	
	protected INeuralNetSpecies<? extends IIndividual> species;
	
	/** Random generator used in creation */
	
	protected IRandGen randGen;

	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
	 * Empty constructor
	 * </p>
	 */
	public PureLayerInitiator() 
	{
		super();
	}
		
	/////////////////////////////////////////////////////////////////
	// ---------------------------- Implementing IInitiator interface
	/////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
	 * Set the system context
	 * </p>
	 * @param context Execution context
	 */
	public void contextualize(ISystem<? extends IIndividual> context){
		// Context and Randgen
		if(context instanceof IPopulation){
			this.context = (IPopulation<? extends IIndividual>) context;
			this.randGen = this.context.createRandGen();
		}
		else
			throw new IllegalArgumentException("This object uses a population as execution context");
		
		// Species
		ISpecies<? extends IIndividual> spc = this.context.getSpecies();
		if (spc instanceof INeuralNetSpecies)
			this.species = (INeuralNetSpecies<? extends IIndividual>) spc;
		else
			throw new IllegalStateException("Illegal species in context");
	}
	
	/**
	 * <p>
	 * Sets the random generator of the initiator
	 * </p>
	 * @param randGen Random generator
	 */
	public void setRandGen(IRandGen randGen){
		this.randGen = randGen;
	}
	
	/**
	 * <p>
	 * Initiation method of a linked layer
	 * </p>
	 * @param linkedLayer Linked layer to initiate
	 * @param previousLayer Previous layer
	 * @param indexLayer Index of layer into the neural net
	 * @param indexWeightRange Index of weight range into the layer (useful for initiating hibrid layers)
	 */	
	public void initiate(LinkedLayer linkedLayer,
			ILayer<? extends INeuron> previousLayer, int indexLayer, int indexWeightRange) {
		
		// Initiate the layer properties
		if(linkedLayer.getType()==LinkedLayer.HIDDEN_LAYER) {
			//Setup hidden layers weights and numbers of neurons
			linkedLayer.setMaxnofneurons(species.getHiddenLayerMaxNofneurons(indexLayer));
			linkedLayer.setMinnofneurons(species.getHiddenLayerMinNofneurons(indexLayer));
			linkedLayer.setInitialmaxnofneurons(species.getHiddenLayerInitialMaxNofneurons(indexLayer));
			linkedLayer.setBiased(species.isHiddenLayerBiased(indexLayer));
		}
		else {
			//Setup output layer weights and number of neurons
			linkedLayer.setMaxnofneurons(species.getNOfOutputs());
			linkedLayer.setBiased(species.isOutputLayerBiased());
		}
		
		//Prepare initiation process
		prepareInitiation();
		
		// Remove the neurons
        /*if(linkedLayer.neurons!=null)
        	linkedLayer.neurons.clear();*/
        
        //Number of effective neurons
        int nofneurons;
        
        if(linkedLayer.getType()==LinkedLayer.OUTPUT_LAYER)
            nofneurons = linkedLayer.getMaxnofneurons();
        else
            nofneurons = randGen.choose(linkedLayer.getMinnofneurons(), linkedLayer.getInitialmaxnofneurons()+1);
        
        //Generate the number of neurons
        for(int i=0; i<nofneurons; i++){
        	
            //Obtain a new neuron (depends on the specific kind of layer)
            LinkedNeuron newNeuron = linkedLayer.obtainNewNeuron();
            
            //Set the weight range to the neuron
            if(linkedLayer.getType()==LinkedLayer.OUTPUT_LAYER)
            	newNeuron.setWeightRange(species.getOutputLayerWeightRange(indexWeightRange));
            else
            	newNeuron.setWeightRange(species.getHiddenLayerWeightRange(indexLayer, indexWeightRange));
            
    		//Create the links
            Link [] links = createLinks(linkedLayer, previousLayer, newNeuron);
            
            //Set the links
            newNeuron.setLinks(links);
            
            //Add the neuron
            linkedLayer.addNeuron(newNeuron);
        }
		
        // Avoid hidden neurons without output link
        if(! (previousLayer instanceof InputLayer)){
      	  
          	for(int i=0; i<previousLayer.getNofneurons(); i++){
          		
  				int nOfLayerLinks=0;
  				for(int j=0; j<linkedLayer.getNofneurons(); j++){
  					LinkedNeuron layerNeuron = linkedLayer.getNeuron(j);
  					if(!layerNeuron.getLinks()[i].isBroken())
  						nOfLayerLinks++;
  				}
  				
  				if(nOfLayerLinks==0){
  				    //Select a node of the layer
  				    int selectedNeuron = randGen.choose(0, linkedLayer.getNofneurons());
  				    
  				    //Obtain the links array
  				    Link [] links = linkedLayer.getNeuron(selectedNeuron).getLinks();
  				    links[i].setOrigin(previousLayer.getNeuron(i));
  				    links[i].setTarget(linkedLayer.getNeuron(selectedNeuron));
  				    links[i].setBroken(false);
  				}					
        	}
        }
        
        initiateWeights(linkedLayer);
	}

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Prepare initiation process
	 * </p>
	 */
	public abstract void prepareInitiation();
	
	/**
	 * <p>
	 * Create all the links of a neural net
	 * </p>
	 * @param linkedLayer Linked layer where create the links
	 * @param previousLayer LinkedLayer the neurons are going to be connected
	 * @param newNeuron New neuron to create its links
	 * @return Random generator
	 */
	public abstract Link [] createLinks(LinkedLayer linkedLayer,
			ILayer<? extends INeuron> previousLayer, LinkedNeuron newNeuron);
	
	/**
	 * <p>
	 * Initiate the weights of all the links of a neural net
	 * </p>
	 * @param linkedLayer Linked layer where initiate the weights
	 */
	public abstract void initiateWeights(LinkedLayer linkedLayer);
}

