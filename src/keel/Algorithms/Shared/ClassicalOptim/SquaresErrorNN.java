/** 
* <p> 
* @author Written by Luciano Sánchez (University of Oviedo) 27/02/2004
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Shared.ClassicalOptim;

public class SquaresErrorNN extends FUN {
    /**
     * <p>
     * Derived class from FUN that implements the error for a perceptron trained with conjugated gradient.
     * 
     * </p>
     * 
     */
	
	//Neural network container. 
    public ConjGradNN Net;
    /**
     * <p>
     * Constructor of an error calculator for neural network based on the conjugated gradient.
     * 
     * </p>
     * @param vNet container with a perceptron.
     */
    public SquaresErrorNN(ConjGradNN vNet) { Net=vNet; }
    
    /**
	 * Returns the training mean square error for a perceptron with weights x
	 * 
	 * @param x the weights of a perceptron.
	 * @return the training mean square error of a perceptron with weights x.
	 */
    public double evaluate(double x[][][]) { return Net.f(x); }
    
}
