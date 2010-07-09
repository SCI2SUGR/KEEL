package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.FPgrowth.LUCS_KDD;

/* ------------------------------------------------------------------------- */
/*                                                                           */
/*                           TOTAL SUPPORT TREE BODE                         *//*                                                                           */
/*                                Frans Coenen                               */
/*                                                                           */
/*                            Wednesday 2 July 2003                          */
/*                                                                           */
/*                       Department of Computer Science                      */
/*                         The University of Liverpool                       */
/*                                                                           */ 
/* ------------------------------------------------------------------------- */

/**
* <p>
* @author Modified by Nicolò Flugy Papè (Politecnico di Milano) 24/03/2009
* @version 1.1
* @since JDK1.5
* </p>
*/

public class TtreeNode {
	/**
	* <p>
	* Methods concerned with Ttree node structure. Arrays of these structures are used
	* to store nodes at the same level in any sub-branch of the T-tree
	* </p>
	*/
    
    /* ------ FIELDS ------ */
    
    /** The support associate with the itemset represented by the node. */
    public int support = 0;
    
    /** A reference variable to the child (if any) of the node. */
    public TtreeNode[] childRef = null;

    // Diagnostics
    /** The number of nodes in the T-tree. */
    public static int numberOfNodes = 0;
    
    /* ------ CONSTRUCTORS ------ */	
    
    /** Default constructor */
	
    public TtreeNode() {
	numberOfNodes++;
	}
	
    /** One argument constructor. 
    @param sup the support value to be included in the structure. */
	
    public TtreeNode(int sup) {
	support = sup;
	numberOfNodes++;
	}
    
    /* ------ METHODS ------ */
    
    /** It returns the number of nodes.
     */
    public static int getNumberOfNodes() {
        return(numberOfNodes);
	}
}
