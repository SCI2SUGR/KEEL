package keel.Algorithms.Neural_Networks.NNEP_Common.initiators;

import keel.Algorithms.Neural_Networks.NNEP_Common.algorithm.NeuralNetAlgorithm;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.ILayer;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.INeuron;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.Link;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.LinkedLayer;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.LinkedNeuron;
import net.sf.jclec.IIndividual;

/**
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penya, Aaron Ruiz Mora (University of Cordoba) 17/07/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public class RandomInitiator extends PureLayerInitiator {
	
	/**
	 * <p>
	 * Random initiator both conections and weights
	 * </p>
	 */
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////
	
	/** Minimum absolute value of the new weights */
	
	protected double significativeWeight = 0.0000001;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
	 * Empty constructor
	 * </p>
	 */
	public RandomInitiator() 
	{
		super();
	}
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------- Setting and getting properties
	/////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
	 * Access to significative weight value
	 * </p>
	 * @return double Significative weight value
	 */
	public double getSignificativeWeight() {
		return significativeWeight;
	}
	
	/**
	 * <p>
	 * Sets the significative weight value
	 * </p>
	 * @param significativeWeight New significative weight value
	 */
	public void setSignificativeWeight(double significativeWeight) {
		this.significativeWeight = significativeWeight;
	}
	
	/////////////////////////////////////////////////////////////////
	// -------------------------- Overwriting AbstractMutator methods
	/////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
	 * Prepare initiation process
	 * </p>
	 */
	@Override
	public void prepareInitiation() {
		if (context instanceof NeuralNetAlgorithm){
			this.significativeWeight = ((NeuralNetAlgorithm<? extends IIndividual>)context).getFitDif();
		}
	}
	
	/**
	 * <p>
	 * Create all the links of a neural net
	 * </p>
	 * @param linkedLayer Linked layer where create the links
	 * @param previousLayer LinkedLayer the neurons are going to be connected
	 * @param newNeuron New neuron to create its links
	 * @return Random generator
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
		
		//Init all the links broken
		for(int j=0; j<previousLayer.getMaxnofneurons(); j++){
			links[j] = new Link();
			links[j].setBroken(true);
			links[j].setWeight(0);
		}
		
		int noflinks = randGen.choose(1, previousLayer.getNofneurons()+1);
		
		//For each effective link
		for(int j=0; j<noflinks; j++){
			//Select a new neuron randomly
			int selectedNeuron = randGen.choose(0, previousLayer.getNofneurons());    
			links[selectedNeuron].setOrigin(previousLayer.getNeuron(selectedNeuron));
			links[selectedNeuron].setTarget(newNeuron);
			links[selectedNeuron].setBroken(false);
		}
		
		return links;
	}
	
	/**
	 * <p>
	 * Initiate the weights of all the links of a neural net
	 * </p>
	 * @param linkedLayer Linked layer where initiate the weights
	 */
	@Override
	public void initiateWeights(LinkedLayer linkedLayer) {
		
		// Apply random weights
		for(int i=0; i<linkedLayer.getNofneurons(); i++) {
			for(int j=0; j<linkedLayer.getNeuron(i).getLinks().length; j++) {
				LinkedNeuron neuron = linkedLayer.getNeuron(i);
				if (neuron.getLink(j).isBroken()==false)
					neuron.getLink(j).setWeight(neuron.randomWeight(randGen, significativeWeight));
			}
		}
	}
	
}
