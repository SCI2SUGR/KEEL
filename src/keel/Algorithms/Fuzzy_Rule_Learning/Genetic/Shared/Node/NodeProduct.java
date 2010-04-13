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

public class NodeProduct extends NodeExprArit {
/**
 * <p>
 * Class for management of product nodes
 * </p>
 */
    //It's evaluated to a alpha-cuts family
	/**
	 * <p>
	 * Constructor. Generates a new product node
	 * </p>
	 * @param assert1 The arithmetic expression
	 * @param assert2 The arithmetic expression
	 */
    public NodeProduct(NodeExprArit assert1, NodeExprArit assert2) {
        super(2,NProduct);
        children[0]=assert1.clone();
        children[1]=assert2.clone();
    }
    
    /**
     * <p>
     * Constructor. Generates a new product node from another one
     * </p>
     * @param n The product node
     */
    public NodeProduct(NodeProduct n) {
        super(n.children.length,NProduct);
        for (int i=0;i<children.length;i++) children[i]=n.children[i].clone();
    }
    
    /**
     * <p>
     * This method sets a product node to another one
     * </p>
     * @param n The product node to be assigned
     */
    public void set(NodeProduct n) {
        super.set(n);
        for (int i=0;i<children.length;i++) children[i]=n.children[i].clone();
    }
    
    /**
     * <p>
     * This method clones a product node
     * </p>
     * @return The node cloned
     */
    public Node clone() {
        return new NodeProduct(this);
    }
    
    /**
     * <p>
     * This method evaluates two nodes with the fuzzy alpha cut product
     * </p>
     * @return The fuzzy alpha cut
     */
    public FuzzyAlphaCut Beval(){
        NodeExprArit tmp1=(NodeExprArit)children[0];
        NodeExprArit tmp2=(NodeExprArit)children[1];
        FuzzyAlphaCut t1=(FuzzyAlphaCut)tmp1.Beval();
        FuzzyAlphaCut t2=(FuzzyAlphaCut)tmp2.Beval();
        return t1.multiply(t2);
    }
    
    /**
     * <p>
     * This method is for debug
     * </p>
     */
    public void debug() {
        System.out.print("(");
        children[0].debug();
        System.out.print(") * (");
        children[1].debug();
        System.out.print(")");
    }
    
}

