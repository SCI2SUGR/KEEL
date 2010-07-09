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
