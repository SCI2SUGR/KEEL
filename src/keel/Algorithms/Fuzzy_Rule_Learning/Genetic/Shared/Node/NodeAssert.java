/**
 * <p>
 * @author Written by Luciano Sánchez (University of Oviedo) 21/01/2004
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Node;


public abstract class NodeAssert extends Node {
/**
 * Class for constructor and define abstrac methods
 **/

	// Internal Node
    // It's evaluated to a real number
	
	/**
	 * <p>
	 * Constructor. Generate a new node of t type and n children
	 * </p>
	 * @param n The number of children
	 * @param t The type of node
	 */
    public NodeAssert(int n, int t) {  super(n,t); }
    
    /**
     * <p>
     * This abstract method evaluate a node 
     * </p>
     * @return The crips evaluation
     */
    public abstract double CrispEval();
    
    /**
     * <p>
     * This abstract method clone a node
     * </p>
     * @return The cloned node
     */
    public abstract Node clone();
    
    
    
}
