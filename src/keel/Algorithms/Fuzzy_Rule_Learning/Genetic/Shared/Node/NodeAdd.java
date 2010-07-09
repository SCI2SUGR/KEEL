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

public class NodeAdd extends NodeExprArit {
/**
 *<p>
 *The class define a node with two children 
 *</p>
 */
         //It's evaluated to an alfa-cuts family
	
	    /**
	     * <p>
	     * Constructor. Generate a new NodeAdd with two children: asert1 and asert2 
	     * </p>
	     * @param aserto1 Children for the NodeAdd (NodeExprArit)
	     * @param aserto2 Children for the NodeAdd (NodeExprArit)
	     */
	    public NodeAdd(NodeExprArit aserto1, NodeExprArit aserto2) {
            super(2,NSum);
            children[0]=aserto1.clone();
            children[1]=aserto2.clone();
        }
        /**
         * <p>
         * Constructor: Generate a new NodeAdd from a given one (NodeAdd)
         * </p>
         * @param n The NodeAdd
         */
        public NodeAdd(NodeAdd n) {
            super(n.children.length,NSum);
            for (int i=0;i<children.length;i++) children[i]=n.children[i].clone();
        }

        /**
         * <p>
         * This method sets to a NodeAdd the properties from another
         * </p>
         * @param n The NodeAdd
         */
        public void set(NodeAdd n) {
            super.set(n);
            for (int i=0;i<children.length;i++) children[i]=n.children[i].clone();
        }
        
        /**
         * <p>
         * This method generates a new NodeAdd
         * </p>
         * @return The NodeAdd cloned
         */
        public Node clone() {
            return new NodeAdd(this);
        }

        /**
         *<p>
         * This method evaluate the alphacut of two nodes with the sum
         *</p>
         *@return The fuzzy alpha cuts evaluated
         */
        public FuzzyAlphaCut Beval(){
            NodeExprArit tmp1=(NodeExprArit)children[0];
            NodeExprArit tmp2=(NodeExprArit)children[1];
            FuzzyAlphaCut t1=(FuzzyAlphaCut)tmp1.Beval();
            FuzzyAlphaCut t2=(FuzzyAlphaCut)tmp2.Beval();
            return t1.sum(t2);
        }

        /**
         * <p>
         *This method is for debug
         * </p>
         */
        public void debug() {
            System.out.print("(");
            children[0].debug();
            System.out.print(") + (");
            children[1].debug();
            System.out.print(")");
        }

}
