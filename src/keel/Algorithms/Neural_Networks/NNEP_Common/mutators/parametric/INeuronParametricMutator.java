package keel.Algorithms.Neural_Networks.NNEP_Common.mutators.parametric;



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

public interface INeuronParametricMutator<N extends LinkedNeuron> {
	
	/**
	 * <p>
	 * Parametric Mutator of a specific neuron
	 * </p>
	 */
	
	/**
	 * <p>
	 * Do the parametric mutation over the links of a specific neuron
	 * in a specific layer
	 * </p>
	 * @param neuron Neuron in the layer to mutate
	 * @param layer Layer that contains the neuron
	 * @param nextLayer Next layer
	 * @param indexNeuron Index of neuron in the layer
	 * @param alphaInput Alpha coeficient for the input weigths
	 * @param alphaOutput Alpha coeficient for the output weigths
	 * @param temper Temperature of the individual that is being mutated
	 */
	
	public void parametricMutation(N neuron, LinkedLayer layer, LinkedLayer nextLayer, 
			int indexNeuron, double alphaInput, double alphaOutput, double temper);
	
}
