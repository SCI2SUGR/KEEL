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
