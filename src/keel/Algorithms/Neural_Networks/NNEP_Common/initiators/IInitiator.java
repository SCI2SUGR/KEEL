package keel.Algorithms.Neural_Networks.NNEP_Common.initiators;


import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.ILayer;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.INeuron;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.LinkedLayer;
import net.sf.jclec.IIndividual;
import net.sf.jclec.ISystem;

/**
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penna, Aaron Ruiz Mora (University of Cordoba) 17/07/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public interface IInitiator {
	
	/**
	 * <p>
	 * Initiate links of a linked layer
	 * </p>
	 */
	
	/**
	 * <p>
	 * Set the system context
	 * </p>
	 * @param context Execution context
	 */
	public void contextualize(ISystem<? extends IIndividual> context);
	
	/**
	 * <p>
	 * Initiation method of a linked layer
	 * </p>
	 * @param linkedLayer Linked layer to initiate
	 * @param previousLayer Previous layer
	 * @param indexLayer Index of layer into the neural net
	 * @param indexWeightRange Index of weight range into the layer (useful for initiating hibrid layers)
	 */	
	public void initiate(LinkedLayer linkedLayer, ILayer<? extends INeuron> previousLayer, int indexLayer, int indexWeightRange);
	
}
