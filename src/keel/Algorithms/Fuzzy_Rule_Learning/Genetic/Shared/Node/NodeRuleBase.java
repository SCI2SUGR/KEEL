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
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;


public class NodeRuleBase extends Node {
/**
 * <p>
 * Class for management of a node rule base
 * </p>
 */
    // Internal Node (root node, in general)
    // A vector of pairs integer/weight is evaluated
	/**
	 * <p>
	 * Constructor. Generate a new node rule base from list of rule node
	 * </p>
	 * @param br The list of rule node
	 */
    public NodeRuleBase(NodeRule []br) {
        super(br.length,NRuleBase);
        for (int i=0;i<br.length;i++) children[i]=br[i].clone();
    }

    /**
     * <p>
     * Constructor. Generate a new node rule base from another one
     * </p>
     * @param br The node rule base
     */
    public NodeRuleBase(NodeRuleBase br) {
        super(br.children.length,NRuleBase);
        for (int i=0;i<br.children.length;i++) children[i]=br.children[i].clone();
    }

    /**
     * <p>
     * This method sets a node rule base to another one
     * </p>
     * @param br The node rule base
     */
    public void set(NodeRuleBase br) {
        super.set(br);
        for (int i=0;i<br.children.length;i++) children[i]=br.children[i].clone();
    }
    
    /**
     * <p>
     * This method clones a node from a node rule base
     * </p>
     * @return The node cloned
     */
    public Node clone() {
        return new NodeRuleBase(this);
    }
    
    /**
     * <p>
     * This method evaluates if two nodes are the same type
     * </p>
     * @return True or false
     */
    protected boolean compatibleData(Node n) {
        if (mytypeid!=n.mytypeid) return false;
        return true;
    }
    
    /**
     * <p>
     * This method evaluates the node rule base
     * </p>
     * @return The set of pairs consequent-weight
     */
    public IntDouble[] CrispEval() {
        if (children.length==0) return new IntDouble[0];
        else {
            IntDouble[] result=new IntDouble[children.length];
            for (int i=0;i<result.length;i++) {
                NodeRule tmp=(NodeRule)children[i];
                result[i]=tmp.CrispEval();
            }
            return result;
        }

    }

    /**
     * <p>
     * This method is for debug
     * </p>
     */
    public void debug() {
        for (int i=0;i<children.length;i++) {
            children[i].debug();
        }
    }
}
