/**
 * <p>
 * @author Written by Luciano Sánchez (University of Oviedo) 26/01/2004
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Node;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;

public class NodeSquareRoot extends NodeExprArit {
/**
 * <p>
 * Class for management of square root node
 * </p>
 */
	
    //It's evaluated to a alpha-cuts family
	/**
	 * <p>
	 * Constructor. Generates a new square root node with only one children
	 * </p>
	 * @param assert1 The arithmetic expression 
	 */
    public NodeSquareRoot(NodeExprArit assert1) {
        super(1,NSquareRoot);
        children[0]=assert1.clone();
    }

    /**
	 * <p>
	 * Constructor. Generates a new square root node from another square root node
	 * </p>
	 * @param n The square root node
	 */
    public NodeSquareRoot(NodeSquareRoot n) {
        super(n.children.length,NSquareRoot);
        for (int i=0;i<children.length;i++) children[i]=n.children[i].clone();
    }

    /**
     * <p>
     * This method sets a square root node to another one
     * </p>
     * @param n The square root node
     */
    public void set(NodeSquareRoot n) {
        super.set(n);
        for (int i=0;i<children.length;i++) children[i]=n.children[i].clone();
    }

    /**
     * <p>
     * This method clones a node from another square root node 
     * </p>
     * @return The node cloned
     */
    public Node clone() {
        return new NodeSquareRoot(this);
    }

    /**
     * <p>
     * This method evaluates the square root node
     * </p>
     * @return The fuzzy alpha cut
     */
    public FuzzyAlphaCut Beval(){
        NodeExprArit tmp1=(NodeExprArit)children[0];
        FuzzyAlphaCut t1=(FuzzyAlphaCut)tmp1.Beval();
        return t1.sqrt();
    }

    /**
     * <p>
     * This method is for debug
     * </p>
     */
    public void debug() {
        System.out.print("SQRT(");
        children[0].debug();
        System.out.print(")");
    }

}

