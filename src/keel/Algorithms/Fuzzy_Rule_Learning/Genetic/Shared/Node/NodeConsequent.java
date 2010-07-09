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



public class NodeConsequent extends Node {
/**
 * <p>
 * Class for management the consequent of a node
 * </p>
 */
    // Internal Node
    // It's evaluated to a integer number
    int C;

    /**
     * <p>
     * Constructor. Genetare a new node with a consequent
     * </p>
     * @param c The consequent (int)
     */
    public NodeConsequent(int c)  {
        super(NConsequent);
        C=c;
    }

    /**
     * <p>
     * Constructor. Generate a new node with from another one
     * </p>
     * @param n The node (NodeConsequent)
     */
    public NodeConsequent(NodeConsequent n) {
        super(NConsequent);
        C=n.C;
    }

    /**
     * <p>
     * This method asign to a NodeConsequente the properties of another
     * </p>
     * @param n The NodeConsequent (NodeConsequent)
     */
    public void set(NodeConsequent n) {
        super.set(n);
        C=n.C;
    }
    
    /**
     * <p>
     * This method evaluate if two nodes are compatibles
     * looking for the type and the consequent
     * </p>
     * @param n The node (Node)
     * @return True or false
     */
    protected boolean compatibleData(Node n) {
        if (mytypeid!=n.mytypeid) return false;
        NodeConsequent nc=(NodeConsequent)n;
        if (C!=nc.C) return false;
        return true;
    }
    
    /**
     * <p>
     * This method return the consequent of a node
     * </p>
     * @return The consequent
     */
    public int CrispEval() {
        return C;
    }
    
    /**
     * <p>
     * This method creates a clone from the object of the class
     * </p>
     * @return the node cloned
     */
    public Node clone() {
        return new NodeConsequent(this);
    }

    /**
     * <p>
     * This method is for debug
     * </p>
     */
    public void debug() {
        System.out.print("C="+ C);
    }

}



