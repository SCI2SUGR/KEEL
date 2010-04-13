/**
 * <p>
 * @author Written by Luciano Sánchez (University of Oviedo) 23/01/2004
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Node;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;

public abstract class NodeExprArit extends Node {
/**
 * Class for constructor and define abstract methods
 */
	//Arithmetic expressions
    //This node is evaluated to fuzzy number
	/**
	 * <p>
	 * Constructor. Generate a new node of t type and n children
	 * </p>
	 * @param n The number of children
	 * @param t The type of node
	 */
    public NodeExprArit(int n, int t) {  super(n,t); }
    
    /**
     * <p>
     * This abstract method evaluate the alphacut of two nodes
     *</p>
     *@return The fuzzy alpha cuts evaluated
     */
    public abstract FuzzyAlphaCut Beval();
  
    /**
     * <p>
     * This abstract method clone a node
     * </p>
     * @return The cloned node
     */
    public abstract Node clone();
}
