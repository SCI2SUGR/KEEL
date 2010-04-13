/** 
* <p> 
* @author Written by Luciano Sánchez (University of Oviedo) 27/02/2004
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Shared.ClassicalOptim;

abstract public class FUN {
	/**
	 * <p>
	 * Abstract class that represent a error function for a neural network.
	 * 
	 * </p>
	 */
	
	/**
	 * Returns the training mean square error for a perceptron with weights x
	 * 
	 * @param x the weights of a perceptron.
	 * @return the training mean square error of a perceptron with weights x.
	 */
    abstract public double evaluate(double x[][][]);
}
