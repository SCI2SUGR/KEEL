/* ------------------------------------------------------------------------- */
/*                                                                           */
/*                           TOTAL SUPPORT TREE BODE                         */
/*                                                                           */
/*                                Frans Coenen                               */
/*                                                                           */
/*                            Wednesday 2 July 2003                          */
/*                                                                           */
/*                       Department of Computer Science                      */
/*                         The University of Liverpool                       */
/*                                                                           */ 
/* ------------------------------------------------------------------------- */

package keel.Algorithms.Associative_Classification.ClassifierCMAR;
import java.io.*;
import java.util.*;

/**
 * <p>Title: TtreeNode</p>
 *
 * <p>Description: Methods concerned with Ttree node structure. Arrays of these structures 
are used to store nodes at the same level in any sub-branch of the T-tree.</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Frans Coenen 2 July 2003
 * @author Modified by Jesus Alcala (University of Granada) 09/02/2010
 * @author Modified by Sergio Almecija (University of Granada) 23/05/2010
 * @version 1.0
 * @since JDK1.5
 */

public class  TtreeNode {
    
    /* ------ FIELDS ------ */
    
    /** The support associate wuth the itemset represented by the node. */
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
    
    /**
     * It returns the number of nodes in a T-tree
     * @return static int Number of nodes in the T-tree
     */
    public static int getNumberOfNodes() {
        return(numberOfNodes);
	}
    }
