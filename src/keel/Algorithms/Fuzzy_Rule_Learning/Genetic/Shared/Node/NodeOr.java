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



public class NodeOr extends NodeAssert {
/**
 * <p>
 * Class for management of or nodes
 * </p>
 */
    // Internal Node
    // It's evaluated to a real number
	/**
	 * <p>
	 * Constructor. Generates a new or node with two childrens
	 * </p>
	 * @param assert1 The assert node
	 * @param assert2 The assert node
	 */
    public NodeOr(NodeAssert assert1, NodeAssert assert2) {
        super(2,NOr);
        children[0]=assert1.clone();
        children[1]=assert2.clone();
    }
    
    /**
     * <p>
     * Constructor. Generate a new node from anothe one
     * </p>
     * @param n The or node
     */
    public NodeOr(NodeOr n) {
        super(n.children.length,NOr);
        for (int i=0;i<children.length;i++) children[i]=n.children[i].clone();
    }
    
    /**
     * <p>
     * This method assign an or node to another one
     * </p>
     * @param n The or node to be assigned
     */
    public void set(NodeOr n) {
        super.set(n);
        for (int i=0;i<children.length;i++) children[i]=n.children[i].clone();
    }
    
    /**
     * <p>
     * This method clone an or node
     * </p>
     * @return The node cloned
     */
    public Node clone() {
        return new NodeOr(this);
    }

    /**
     * <p>
     * This method evaluates two nodes and return the maximun
     * </p>
     * @return The crips eval of two nodes
     */
    public double CrispEval(){
        NodeAssert tmp1=(NodeAssert)children[0];
        NodeAssert tmp2=(NodeAssert)children[1];
        double t1=tmp1.CrispEval();
        double t2=tmp2.CrispEval();
        if (t1>t2) return t1; else return t2;
    }

    /**
     * <p>
     * This method is for debug
     * </p>
     */
    public void debug() {
        System.out.print("(");
        children[0].debug();
        System.out.print(") OR (");
        children[1].debug();
        System.out.print(")");
    }
}

