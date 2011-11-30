/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

/* -------------------------------------------------------------------------- */
/*                                                                            */
/*                   P A R T I A L   S U P P O R T   T R E E                  */
/*                                                                            */
/*                               Frans Coenen                                 */
/*                                                                            */
/*                          Wednesday 9 January 2003                          */
/*                             (Revised 5/7/2003)                             */
/*                                                                            */
/*                       Department of Computer Science                       */
/*                        The University of Liverpool                         */
/*                                                                            */
/* -------------------------------------------------------------------------- */


/* Structure:

AssocRuleMining
      |
      +-- TotalSupportTree
      		  |
		  +-- PartialSupportTree	 */ 

/* Java packages */ 
package keel.Algorithms.Associative_Classification.ClassifierCMAR;

import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 * Methods to implement the "Apriori-TFP" (Total From Partial) ARM algorithm 
using both the T-tree (Total support tree) and P-tree (Partial support tree 
data structures.
 *
 * @author Frans Coenen 5 July 2003
 * @author Modified by Jesus Alcala (University of Granada) 09/02/2010
 * @author Modified by Sergio Almecija (University of Granada) 23/05/2010
 * @version 1.0
 * @since JDK1.5
 */
public class PartialSupportTree extends TotalSupportTree {

    /*------------------------------------------------------------------------*/
    /*                                                                        */
    /*                                   FIELDS                               */
    /*                                                                        */
    /*------------------------------------------------------------------------*/

    /* ------ NESTED CLASSES ------ */
    
    /** Structurte to contain P-tree data in tabular form for improved
    computational efficiency when creating T-tree. <P> A 2-D array of these 
    structures is created in which to store the Ptree. */
        	
    protected class PtreeRecord {
    	/** Label for P-tree node. */
        private short[] pTreeNodeLabel = null;
	/** Uunion of a pTree node label (<TT>pTreeNodeLabel</TT>) and all its
    	ancestor node labels. */
        private short[] pTreeItemSet = null;
        /** Partial support count. */
	private int support = 0;
	
	/** Creaste P-tree record for inclusion in table.
	@param nodeLabel the label for P-tree node. 
	@param itemSet the uunion of a pTree node label (<TT>nodeLabel</TT>) 
	and all its ancestor node labels. 
	@param sup the partial support count. */
	private PtreeRecord(short[] nodeLabel, short[] itemSet, int sup) {
	    pTreeNodeLabel = nodeLabel;
	    pTreeItemSet   = itemSet;
	    support        = sup;
	    }  
        }		

    /* Array data structures for P tree */
    
    /** Null array describing an "emty" itemset. */	
    private short[]          zeroitemSet   = null;
    /** Reference variable pointing to start of P-tree. */
    private PtreeNodeTop[]   startPtreeRef = null;  	
    
    /* ------ P-TREE DATA TABLE ----- */
    
    /** Array of arrays data structures for P-tree table (used as a
    computational efficiency measure). */
    protected PtreeRecord[][] startPtreeTable = null;
    /** Array for holding the number of P-tree nodes for each possible
    cardinality given the number of attributes, maximum is equal to number of
    columns. */
    private int[] pTreeNodesOfCardinalityN  = null;
    /** Array of "markers" used during the generation of the P-tree, and
    contains "current index" values for each level of cardinality. */
    private int[] pTreeTableMarker          = null; 
    
    /* Other fields */
    
    /** Number of node updates (used for diagnostic purposes). */    
    private int numberOfNodeUpdates = 0;
    
    /*---------------------------------------------------------------------*/
    /*                                                                     */
    /*                           CONSTRUCTORS                              */
    /*                                                                     */
    /*---------------------------------------------------------------------*/

    /** Processes command line arguments. <P> The <TT>numOneItemSets</TT> is
     * incremented by 1 so that indexes match the column numbers 
     * @param minConf double Minimum confidence threshold
     * @param minSup double Minimum support threshold
     * @param delta int Minimum coverage threshold
     */
    public PartialSupportTree(double minConf, double minSup, int delta) {
        super(minConf, minSup, delta);
	}
	
    /*-------------------------------------------------------------------*/
    /*                                                                   */
    /*                    TREE BUILDING METHODS                          */
    /*                                                                   */
    /*-------------------------------------------------------------------*/  
    
    /* CREATE P-TREE */

    /** Processes data set causing each row to be added to P-Tree. */
    
    public void createPtree() {        
		System.out.println("GENERATING P-TREE\n------------------");
		
		// Dimension top line of P-tree
		startPtreeRef     = new PtreeNodeTop[numOneItemSets+1];
		
		// Dimension P-tree table
		startPtreeTable          = new PtreeRecord[numOneItemSets+1][];
		pTreeNodesOfCardinalityN = new int[numOneItemSets+1];
		pTreeTableMarker         = new int[numOneItemSets+1];
		
		// Initilalise top level of Ptree with nulls
		for(int index=0; index<startPtreeRef.length; index++)  startPtreeRef[index] = null;
		
		// Process input data, loop through input (stored in data array)
		// For each entry add the entry to the P-tree.	
		for (int index=0; index<dataArray.length; index++) {
			if (dataArray[index] != null) addToPtreeTopLevel(dataArray[index]);
	    }   
		
		// Create P-tree table
		System.out.println("Creating P-tree table");
		createPtreeTable();
	}
	
    /* ADD TO P-TREE TOP LEVEL */

    /** Commences process to add an itemset to the P-tree starting with the top
    level of the tree. <P> Note that the top level is an array.
    @param itemset the given item set. */    
    private void addToPtreeTopLevel(short[] itemSet) {
		int index = itemSet[0]; // Calculate index
		int itemSetLength = itemSet.length;
		
		// If single attibute itemSet create or update element, otherwise 
		// create or update element and proceed down child branch (flag = 1)
		
		if (itemSetLength == 1) { // Top level
			if (startPtreeRef[index] == null) {
				startPtreeRef[index] = new PtreeNodeTop();
				pTreeNodesOfCardinalityN[1]++;
			}
			else startPtreeRef[index].support = startPtreeRef[index].support+1;
			
			numberOfNodeUpdates++; 
	    }	
		
		// itemSet length greater than 1 therefore proceed down rest of tree
		
		else {			
			// If no top level node create one.
			if (startPtreeRef[index] == null) {
				startPtreeRef[index] = new PtreeNodeTop();
				pTreeNodesOfCardinalityN[1]++;
			}
			else startPtreeRef[index].support = startPtreeRef[index].support+1;
			
			numberOfNodeUpdates++; 

			// Descend from top level node 
			addToPtree(0,2,itemSetLength,startPtreeRef[index].childRef,	realloc3(itemSet),index,null);
	    }	
	}
	
    /* ADD TO P-TREE */

    /** Inserts given itemset into P-tree. <P> Operates as follows:
    If found leaf node create new node and stop. Otherwise:
    <UL>
    <LI> code = 1, Increment support
    <LI> code = 2, Itemset before and subset of current node (parent)
    <LI> code = 3, Itemset before and not subset of current node (elder sibling)
    <LI> code = 4, Itemset after and superset of current node (child)
    <LI> code = 5, Itemset after and not superset of current node (younger sibling)
    </UL>
    <P>Codes generated by call the checkitemSet function. Arguments as follows:
    @param flag the type of branch currently under consideration:
    code 0 = root, 1 = child, 2 = sibling.
    @param parentLength the number of elements represented by the parent node
    of the current node. Used only when adding new "dummy" nodes to maintain
    count of number of nodes of a given size for when the Ptree table is
    generated.
    @param itemSetLength the number of elements in the current itemSet. Used
    only when adding new nodes to maintain count of number of nodes of a given
    size for when the Ptree table is generated.
    @param linkRef the reference (pointer) to current location in the P-tree.
    @param itemSet the row itemSet in the input file currently under
    consideration.
    @param topIndex the index of the element in the array marking the top level
    of the P-tree, used only when inserting new nodes hanging from this top
    level otherwise ignored.
    @param oldRef the reference (pointer) to the previous location in the P-tree,
    used when inserting new nodes. */
    
    public void addToPtree(int flag, int parentLength, int itemSetLength, 
    			PtreeNode linkRef, short[] itemSet, int topIndex, 
							PtreeNode oldRef) {
       
	// No child node hanging of previous level array therefore add new 
	// node here.
	
	if (linkRef == null) {
	    PtreeNode newRef = createPtreeNode(itemSet,itemSetLength);  
	    addSupport2(flag,newRef,topIndex,oldRef);
	    } 
	
	// Otherwise process tree

	else {
	    switch (checkItemSets(itemSet,linkRef.itemSet)) {
	        case 1:  	/* Rule 1: Same */
		    numberOfNodeUpdates++;
		    linkRef.support++;
		    break;
	        case 2:	  /* Rule 2: Before and subset (parent) */
		    beforeAndSubset(flag,itemSetLength,linkRef,itemSet,topIndex,
		    		oldRef);
		    break;
	        case 3:	  /* Rule 3: Before and not subset (elder sibling) */
	            beforeAndNotSubset(flag,parentLength,itemSetLength,linkRef,
		    		itemSet,topIndex,oldRef);
		    break;
	        case 4:   /* Rule 4: After and superset (child) */
	            afterAndSuperset(parentLength,itemSetLength,linkRef,
		    		itemSet);
		    break;
	        case 5:	  /* Rule 5: After and not superset (younger sibling) */
	            afterAndNotSuperset(flag,parentLength,itemSetLength,linkRef,
		    		itemSet,topIndex,oldRef);
		    break;
	        default:	/* Default: Error */
	        }		
	    }
	} 

    /* BEFORE AND SUBSET */

    /** Adds new node into the P-tree on a parent/child link so that the new
    node is the parent of the existing child branch and the child of the
    previous "parent"; also checks if any siblings need to be "moved up". <P>
    Possibilities:
    <OL>
    <LI>Connect to top level node with no siblings moved up ({1 2 3} {1 2})
    <LI>Connect to top level node with siblings moved up ({1 2 3} {1 4} {1 2})
    <LI>Connect to child ref with no siblings moved up ({1 2 3 4} {1 2} {1 2 3})
    <LI>Connect to child ref with siblings moved up ({1 2 3 4} {1 2 4 5} {1 2}
    			{1 2 3})
    <LI>connect to sibling ref with no siblings moved up ({1 2} {1 3 4} {1 3})
    <LI>connect to sibling ref with siblings moved up ({1 2} {1 3 4} (1 4)
    {1 3})
    </OL>
    @param flag the type of branch currently under consideration:
    code 0 = root, 1 = child, 2 = sibling.
    @param itemSetLength the number of elements in the current itemSet. Used
    only when adding new nodes to maintain count of number of nodes of a given
    size for when the Ptree table is generated.
    @param linkRef the reference (pointer) to current location in the P-tree.
    @param currentItemSet the row itemSet in the input file currently under
    consideration.
    @param topIndex the index of the element in the array marking the top level
    of the P-tree, used only when inserting new nodes hanging from this top
    level otherwise ignored.
    @param oldRef the reference (pointer) to the previous location in the P-tree,
    used when inserting new nodes.*/
    
    private void beforeAndSubset(int flag, int itemSetLength, PtreeNode linkRef, 
    			short[] currentitemSet, int topIndex, PtreeNode oldRef) {

	// Create new node with support of current node added in;
	
	PtreeNode newRef = createPtreeNode(currentitemSet,itemSetLength);	
	newRef.support = newRef.support+linkRef.support;
	numberOfNodeUpdates++;	
	
	// Link in existing branch
	
	newRef.childRef = linkRef;
	
	// Connect new node into tree structure and adjust existing current
	// node itemSet so that it does not include the new parent node itemSet
	
	addSupport2(flag,newRef,topIndex,oldRef);
	linkRef.itemSet = realloc4(linkRef.itemSet,currentitemSet);
	
	// Check whether any siblings of the existing node need to be 
	// "moved up" to become a sibling of the new node
	
	checkSiblingBranch(linkRef,currentitemSet,newRef);
	}
		
    /* BEFORE AND NOT SUBSET */

    /** Insets node into P-tree where new itemset is an elder sibling of the
    "current" node. <P> First checks for leading substring with existing node;
    if found and leading substring is not same as current parent creates a new
    P-tree node for this substring and then adds in the new node. Possibilities:
    <OL>
    <LI>Connect to top level node with no common leading substring with current
    		node ({1 3} {1 2}).
    <LI>Connect to top level node with common leading substring with current
    		node and no siblings moved up ({1 2 4} {1 2 3}).	
    <LI>Connect to top level node with common leading substring with current
    		node and siblings moved up ({1 2 4} (1 3} {1 2 3}).		
    <LI>Connect to child ref with no common leading substring with current node
    		({1 2} {1 2 4} {1 2 3}).
    <LI>Connect to child ref with common leading substring with current node
    		and no siblings moved up ({1 2} {1 2 4 5 7} {1 2 4 5 6}).
    <LI>Connect to child ref with common leading substring with current node
    		and siblings moved up ({1 2} {1 2 4 5 7} {1 3} {1 2 4 5 6}).	
    <LI>Connect to sibling ref with no common leading substring with current
    		node ({1 2} {1 4} {1 3}).
    <LI>Connect to sibling ref with common leading substring with current node
    		and no siblings moved up ({1 2} {1 4 5 7} {1 4 5 6}).
    <LI>Connect to sibling ref with common leading substring with current node
    		and siblings moved up ({1 2} {1 4 5 7} {1 6} {1 4 5 6}).
    </OL>
    @param flag the type of branch currently under consideration:
    code 0 = root, 1 = child, 2 = sibling.
    @param parentLength the number of elements represented by the parent node
    of the current node. Used only when adding new "dummy" nodes to maintain
    count of number of nodes of a given size for when the Ptree table is
    generated.
    @param itemSetLength the number of elements in the current itemSet. Used
    only when adding new nodes to maintain count of number of nodes of a given
    size for when the Ptree table is generated.
    @param linkRef the reference (pointer) to current location in the P-tree.
    @param currentItemSet the row itemSet in the input file currently under
    consideration.
    @param topIndex the index of the element in the array marking the top level
    of the P-tree, used only when inserting new nodes hanging from this top
    level otherwise ignored.
    @param oldRef the reference (pointer) to the previous location in the P-tree,
    used when inserting new nodes. */
    
    private void beforeAndNotSubset(int flag, int parentLength, 
    		int itemSetLength, PtreeNode linkRef, short[] currentItemSet,
					    int topIndex, PtreeNode oldRef) {
			
	// Find leading common ellements in row and sibling itemSets if any
	
	short[] subsetItemSet = checkForLeadingSubString(currentItemSet,
			linkRef.itemSet);
	
	// If leading common ellements exists create new node representing 
	// common elements and add current itemSet as child of this new node. 
	// Otherwise add new itemSet as elder sibling.

	if (subsetItemSet != null) {	// Leading substring exists 
	    // Create new parent representing subset
	    PtreeNode newParentRef = createPtreeNode(subsetItemSet,
	    				subsetItemSet.length+parentLength-1);
	    // Add support made up of existing support + 1 for current row
	    newParentRef.support = linkRef.support+1;
	    // Insert new parent node into tree
	    addSupport2(flag,newParentRef,topIndex,oldRef);
	    // Remove leading substring from row itemSet
	    currentItemSet = realloc4(currentItemSet,subsetItemSet);
	    // Attach as child and add on sibling,
	    newParentRef.childRef = createPtreeNode(currentItemSet,
	    							itemSetLength);
	    newParentRef.childRef.siblingRef = linkRef;
	    // Check whether any siblings need to be "moved up"
	    checkSiblingBranch(newParentRef.childRef,subsetItemSet,newParentRef); 
	    }
	else {
	    // Create new node
	    PtreeNode newSiblingRef = 
	    			createPtreeNode(currentItemSet,itemSetLength);
	    // Attach existing node as younger sibling
	    newSiblingRef.siblingRef = linkRef;
	    // Insert into tree
	    addSupport2(flag,newSiblingRef,topIndex,oldRef);
	    }
	}

    /* AFTER AND SUPERSET */

    /** Insets node into P-tree where new itemset is an child of the "current"
    node. <P> If no more child nodes add new node to "current" node as child,
    else carry on down the tree with flag set to 1 to indicate we are following
    a child branch. Possibilities:
    <OL>
    <LI> Add to top level node ({1 2}).
    <LI> Add to child ref ({1 2} {1 2 3}).
    </OL>
    @param parentLength the number of elements represented by the parent node
    of the current node. Used only when adding new "dummy" nodes to maintain
    count of number of nodes of a given size for when the Ptree table is
    generated.
    @param itemSetLength the number of elements in the current itemSet. Used
    only when adding new nodes to maintain count of number of nodes of a given
    size for when the Ptree table is generated.
    @param linkRef the reference (pointer) to current location in the P-tree.
    @param currentItemSet the row itemSet in the input file currently under
    consideration. */

    private void afterAndSuperset(int parentLength, int itemSetLength, 
    				PtreeNode linkRef, short[] currentItemSet) {    
    	
	numberOfNodeUpdates++;
	linkRef.support = linkRef.support+1;	// Increment support
	
	// End of child branch
	 
	if (linkRef.childRef == null) {
	    // Remove existing parent itemSet from currentItemSet

	    PtreeNode newRef = createPtreeNode(realloc4(currentItemSet,
	    				linkRef.itemSet),itemSetLength);
	    // Add to existing node as child
	    linkRef.childRef = newRef;
	    }
	
	// More children, remove existing current itemSet from row itemSet and 
	// continue down child branch
	
	else addToPtree(1,parentLength+linkRef.itemSet.length,itemSetLength,
			linkRef.childRef,realloc4(currentItemSet,
					linkRef.itemSet),0,linkRef);
	}
	
    /* AFTER AND NOT SUPERSET */

    /** Commeences process of inserting node into P-tree where new itemset is
    a younger sibling of the "current" node. <P> Possible actions:
    <OL>
    <LI> There are NO more sibling nodes (call <TT>afterAndNotSuperset1</TT>).
    <LI> There are more sibling nodes (call <TT>afterAndNotSuperset2</TT>).
    </OL>
    @param flag the type of branch currently under consideration:
    code 0 = root, 1 = child, 2 = sibling.
    @param parentLength the number of elements represented by the parent node
    of the current node. Used only when adding new "dummy" nodes to maintain
    count of number of nodes of a given size for when the Ptree table is
    generated.
    @param itemSetLength the number of elements in the current itemSet. Used
    only when adding new nodes to maintain count of number of nodes of a given
    size for when the Ptree table is generated.
    @param linkRef the reference (pointer) to current location in the P-tree.
    @param currentItemSet the row itemSet in the input file currently under
    consideration.
    @param topIndex the index of the element in the array marking the top level
    of the P-tree, used only when inserting new nodes hanging from this top
    level otherwise ignored.
    @param oldRef the reference (pointer) to the previous location in the P-tree,
    used when inserting new nodes. */

    private void afterAndNotSuperset(int flag, int parentLength, 
    		int itemSetLength, PtreeNode linkRef, short[] currentItemSet, 
					    int topIndex, PtreeNode oldRef) { 

	// Test if end of sibling branch, if not continue  
	 
    	if (linkRef.siblingRef == null) 
			afterAndNotSuperset1(flag,parentLength,itemSetLength,
				linkRef,currentItemSet,topIndex,oldRef);
	    
	// Not end of sibling branch
	
	else afterAndNotSuperset2(flag,parentLength,itemSetLength,linkRef,
				currentItemSet,topIndex,oldRef);	    
	}
	
    /* AFTER AND NOT SUPERSET 1 */

    /** Inserts node into P-tree where new itemset is a younger sibling of the
    "current" node and there are no more younger siblings on current existing
    node therefore new node added as sibling. <P> Also tests for leading
    substring. If found and this is not equal to an existing parent itemSet
    method causes a dummy node to represent the substring to be inserted (call
    to <TT>addSupport2</TT>). Possibilities:
    <OL>
    <LI>Add to siblingRef with no common leading substring ({1 2} {1 3}).
    <LI>Add to siblingRef with common leading substring
       	({1 2} {1 3 5} {1 3 4}).
    </OL>   	
    @param flag the type of branch currently under consideration:
    code 0 = root, 1 = child, 2 = sibling.
    @param parentLength the number of elements represented by the parent node
    of the current node. Used only when adding new "dummy" nodes to maintain
    count of number of nodes of a given size for when the Ptree table is
    generated.
    @param itemSetLength the number of elements in the current itemSet. Used
    only when adding new nodes to maintain count of number of nodes of a given
    size for when the Ptree table is generated.
    @param linkRef the reference (pointer) to current location in the P-tree.
    @param currentItemSet the row itemSet in the input file currently under
    consideration.
    @param topIndex the index of the element in the array marking the top level
    of the P-tree, used only when inserting new nodes hanging from this top
    level otherwise ignored.
    @param oldRef the reference (pointer) to the previous location in the P-tree,
    used when inserting new nodes.   	*/
    
    private void afterAndNotSuperset1(int flag, int parentLength, 
    		int itemSetLength, PtreeNode linkRef, short[] currentItemSet, 
					int topIndex, PtreeNode oldRef) { 

	// Find leading common ellements in row and sibling itemSets if any
	
	short[] subsetItemSet = checkForLeadingSubString(currentItemSet,
							linkRef.itemSet);
       
	// If leading common ellements exists create new node representing 
	// common elements and add current itemSet as child of this new node. 
	// Otherwise add new itemSet as elder sibling.
	 
	if (subsetItemSet != null) { 
	    // Create new parent representing subset
	    PtreeNode newParent = createPtreeNode(subsetItemSet,
	    				subsetItemSet.length+parentLength-1);
	    // Add support made up of existing support + 1 for current row
	    newParent.support = linkRef.support+1;
	    // Insert new parent node into tree
            addSupport2(flag,newParent,topIndex,oldRef);
	    // Remove leading substring from current existing node
	    linkRef.itemSet = realloc4(linkRef.itemSet,subsetItemSet);
	    // Attach existing branch as child of new parent and new node
            // as sibling
	    newParent.childRef = linkRef;
	    linkRef.siblingRef = createPtreeNode(realloc4(currentItemSet,
		   				subsetItemSet),itemSetLength);
	    }
	 
	// No leading substring	
	
	else linkRef.siblingRef = createPtreeNode(currentItemSet,
								itemSetLength);
	}

    /* AFTER AND NOT SUPERSET 2 */

    /** Inserts node into P-tree where new itemset is a younger sibling of the
    "current" node and there are more younger sibling on current existing
    node. <P> Possible actions:
    <OL>
    <LI> If there are more sibling nodes and the current row itemSet shares a
    leading substring with the current P-tree node and this is not equal to
    an existing parent itemSet then add a dummy node to represent the
    substring (call <TT>addSupport2</TT>). Then add new node.
    <LI> If more sibling nodes and no shared leading substring continue down
    sibling branch.
    </OL>
    Possibility:
    <OL>
    <LI>Add in dummy node ({1 2 3} {1 3 5} {1 6} {1 3 6}).
    </OL>
    @param flag the type of branch currently under consideration:
    code 0 = root, 1 = child, 2 = sibling.
    @param parentLength the number of elements represented by the parent node
    of the current node. Used only when adding new "dummy" nodes to maintain
    count of number of nodes of a given size for when the Ptree table is
    generated.
    @param itemSetLength the number of elements in the current itemSet. Used
    only when adding new nodes to maintain count of number of nodes of a given
    size for when the Ptree table is generated.
    @param linkRef the reference (pointer) to current location in the P-tree.
    @param currentItemSet the row itemSet in the input file currently under
    consideration.
    @param topIndex the index of the element in the array marking the top level
    of the P-tree, used only when inserting new nodes hanging from this top
    level otherwise ignored.
    @param oldRef the reference (pointer) to the previous location in the P-tree,
    used when inserting new nodes. */
    
    private void afterAndNotSuperset2(int flag, int parentLength, 
    			int itemSetLength,  PtreeNode linkRef, 
			short[] currentItemSet, int topIndex, PtreeNode oldRef) { 
        
	// Find leading common ellements in row and sibling itemSets if any
	
	short[] subsetItemSet = checkForLeadingSubString(currentItemSet,
			linkRef.itemSet);
       
	// If leading common ellements exists create new node representing 
	// common elements and add current itemSet as child of this new node. 
	// Otherwise add new itemSet as elder sibling.
	
	if (subsetItemSet != null) { 
	    // Create new parent representing leading common elements
	    PtreeNode newParentRef = createPtreeNode(subsetItemSet,
	    			     subsetItemSet.length+parentLength-1);
	    // Add support made up of existing support + 1 for current row
	    newParentRef.support = linkRef.support+1;
	    // Insert new parent node into tree
            addSupport2(flag,newParentRef,topIndex,oldRef); 
	    // Remove leading substring from current existing node and add
	    // as child of new parent node
	    linkRef.itemSet = realloc4(linkRef.itemSet,subsetItemSet);
	    newParentRef.childRef = linkRef;
	    // Store reference to existing branch of current existing node
	    // in temporary variable
	    PtreeNode tempRef = linkRef.siblingRef; 
	    // Create new node representing row and add as sibling of 
	    // current existing ref (NOTE: leading sub string will be 
	    // removed when checking siblings (checkSiblingBranch)
	    linkRef.siblingRef = createPtreeNode(currentItemSet,itemSetLength);
	    // Now add in previous siblings
	    linkRef.siblingRef.siblingRef = tempRef;
	    // Check whether any siblings need to be "moved up"
	    checkSiblingBranch(newParentRef.childRef,subsetItemSet,newParentRef); 
	    }	
	
	// Otherwise carry on along sibling branch
	
	else addToPtree(2,parentLength,itemSetLength,linkRef.siblingRef,
						currentItemSet,0,linkRef);
	}
				
    /* ------ ADD SUPPORT 2 ------ */

    /** Adds new node where "before and subset" or "after and not superset". <P>
    The flag argument indicates which type of branch is currently under 
    consideration: 0 = root, 1 = child, 2 = sibling.
    @param flag the type of branch currently under consideration:
    code 0 = root, 1 = child, 2 = sibling.
    @param newRef the reference (pointer) to newly created parent indicating
    current laction in the P-tree.
    @param topIndex the index of the element in the array marking the top level
    of the P-tree, used only when inserting new nodes hanging from this top
    level otherwise ignored.
    @param oldRef the reference (pointer) to the previous location in the P-tree,
    used when inserting new nodes. */

    private void addSupport2(int flag, PtreeNode newRef, int topIndex, 
    						PtreeNode oldRef) {
	
	// Add node
	
	switch (flag) {		
	    case 0:
		startPtreeRef[topIndex].childRef = newRef;
		break;
	    case 1:
		oldRef.childRef = newRef;
		break;
	    case 2:
		oldRef.siblingRef = newRef;
		break;
	    default:
		System.out.println("ERROR: Unidentified flag in addSupport\n"); 
	    }
	}
	
    /* ------ CHECK SIBLING BRANCH ------ */

    /** Checks sibling branch to determine whether the siblings are all
    supersets of the parent and readjusts P-tree accordingly. <P> Possibilities:
    <OL>
    <LI>Sibling branch is empty (do nothing).
    <LI>No nodes in sibling branch are supersets of parent there for move
    them all up to be siblings of the parent
    <LI>All nodes in sibling branch are supersets of parent, thus do
    nothing.
    <LI>Some nodes in sibling branch are supersets of parent, others are
    not; therefore move those that are not up to be siblings of the parent.
    </OL>
    Note: when a node is found that is not a superset of the parent we do not
    need to keep on checking.
    @param linkRef the reference (pointer) to the current node.
    @param parentItemSet the itemset label represented by the parent.
    @param newRef tghe reference (pointer) to the newly created parent node.*/

    private void checkSiblingBranch(PtreeNode linkRef, short[] parentItemSet, 
    				PtreeNode newRef) {
	// Check if first node in sibling branch is a superset of parent 
	// itemSet. If not move the entire branch up. 
	
	if (linkRef.siblingRef != null) {
	    if (! isSubset(parentItemSet,linkRef.siblingRef.itemSet)) {
	        newRef.siblingRef = linkRef.siblingRef;
	        linkRef.siblingRef = null;
	        }
	   
	// Check rest. Branch starts of with supersets of parent itemSet 
	// (which are OK where they are), must find the point where this is 
	// no longer the case, i.e. the  part of the branch that needs to be 
	// moved up (if any). 

	    else {
	        // Remove leading substring
	        linkRef.siblingRef.itemSet = 
				realloc4(linkRef.siblingRef.itemSet,
								parentItemSet);
	        // Set reference varibales
		PtreeNode markerRef = linkRef.siblingRef;
	        PtreeNode localLinkRef = linkRef.siblingRef.siblingRef;
	        while (localLinkRef != null) {
	            if (! isSubset(parentItemSet,localLinkRef.itemSet)) {
		        newRef.siblingRef = localLinkRef;
		        markerRef.siblingRef = null;
			break;
		        }
		    else {
		        localLinkRef.siblingRef.itemSet = 
			     realloc4(localLinkRef.siblingRef.itemSet,
						parentItemSet);	
	                markerRef = localLinkRef;
	                localLinkRef = localLinkRef.siblingRef;
			}
	            }
	        }
	    }
	}        

    /* CREAT P-TREE NODE */
    /** Creates a P-tree node (other than a top level node). 
    @param newItemSet the itemset to be stored at the node.  
    @param level the cardinality (length) of the item set to be stored in
    the node. */
    
    private PtreeNode createPtreeNode(short[] itemSet, int level) {
        pTreeNodesOfCardinalityN[level]++;
        return(new PtreeNode(itemSet));
	}

    /* GET START OF P-TREE */
    /** Gets reference to start of P-tree. 
     * @return PtreeNodeTop[] Reference to start of P-tree. 
     */
    
    public PtreeNodeTop[] getStartOfPtree() {
        return(startPtreeRef);
	}

    /* GET NUMBER P-TREE NODES */
    /** Gets number of nodes in P-tree. 
     * @return int Number of nodes in P-tree. 
     */
    
    public int getNumPtreeNodes() {
        return(calculateNumNodes(startPtreeRef));
	}
	
    /*----------------------------------------------------------------------- */
    /*                                                                        */
    /*                                 P-TREE TABLE                           */
    /*                                                                        */
    /*----------------------------------------------------------------------- */
   
    /* CREATE P-TREE TABLE */
    /** Creates P-tree table starting with top level in P-tree. <P> Proceed as
    follows.
    <OL>
    <LI>Create an array of arrays.
    <LI>Add top level of Ptree.
    <LI>Add remaining nodes in sub-branches.
    </OL> */
    
    public void createPtreeTable() {
 	// Set up array of arrays 	
	for (int index=1;index<pTreeNodesOfCardinalityN.length;index++) {
	    // There may be no itemSets in the Ptree of a particular size
	    if (pTreeNodesOfCardinalityN[index] == 0) startPtreeTable[index] = null;
	    else startPtreeTable[index] = 
	    			new PtreeRecord[pTreeNodesOfCardinalityN[index]];
	    }
	    	
	// Process Ptree	
	for (int index=0;index < startPtreeRef.length;index++) {
	    // Check if valid node (non-null)
	    if (startPtreeRef[index] != null) {
		// Create a label	        
		short[] itemSet = new short[1];
		itemSet[0] = (short) index;
		// Add to P-tree table 
		addToPtreeArray(null,itemSet,startPtreeRef[index].support,1);
		// Process child branch
	    	createPtreeTable2(startPtreeRef[index].childRef,itemSet,1);
		startPtreeRef[index] = null;
		}
	    } 
	}
	
    /* CREATE P-TREE TABLE 2 */

    /** Process child branch hanging from a top level P-tree node.
    @param linkPtreeRef the reference/pointer to the current location in the
    P-tree.
    @param totalpTreeItemSet the union of all the parent labels sofar.
    @param currentLevel the current levl in the P-tree, initially set to 1. */
    	
    private void createPtreeTable2(PtreeNode linkPtreeRef, 
    			short[] totalpTreeItemSet, int currentLevel) {
	if (linkPtreeRef != null) {		// Not referencing null node
	    
	    // Calculate level represented by node
	    
	    int lastElementIndex = linkPtreeRef.itemSet.length-1;
	    int level = currentLevel+lastElementIndex+1; 
		
	    // OAdd to Ptree table 
	    
	    addToPtreeArray(linkPtreeRef.itemSet,totalpTreeItemSet,
	    					linkPtreeRef.support,level);
		
	    // Search through child branch
	
	    createPtreeTable2(linkPtreeRef.childRef,
	    			append(totalpTreeItemSet,
				linkPtreeRef.itemSet),level);
            linkPtreeRef.childRef = null;
		 
	    // Search through sibling branch
	    
	    createPtreeTable2(linkPtreeRef.siblingRef,totalpTreeItemSet,
	    		currentLevel);
	    linkPtreeRef.siblingRef= null;
	    }	         
	}

    /* ADD TO P-TREE ARRAY */

    /** Adds data associated with a P-tree node to the P-tree 2-D table.
    @param pTreeNodeLabel the union of all the parent labels.
    @param pTreeItemSet the node label.
    @param support the support associated with the node
    @param level the current levl in the P-tree. */
    
    private void addToPtreeArray(short[] pTreeNodeLabel, short[] pTreeItemSet, 
    			int support, int level) {
 	
	if (pTreeNodeLabel == null) {
	    startPtreeTable[level][pTreeTableMarker[level]] = 
			new PtreeRecord(pTreeItemSet,pTreeItemSet,support);
	    }
	else startPtreeTable[level][pTreeTableMarker[level]] = 
		new PtreeRecord(pTreeNodeLabel,append(pTreeItemSet,
							pTreeNodeLabel),support);
							
	pTreeTableMarker[level]++;
	}

    /*----------------------------------------------------------------------- */
    /*                                                                        */
    /*                       T-TREE BUILDING METHODS                          */
    /*                                                                        */
    /*----------------------------------------------------------------------- */
    
    /* CREATE TOTAL SUPPORT TREE */   
    /** Commences process of generating a total support tree (T-tree) from
    a P-tree. */
                
    public void createTotalSupportTree() {	
		System.out.println("APRIORI-TFP WITH X-CHECKING\n" + "---------------------------");
		System.out.println("Minimum support threshold = " + twoDecPlaces(support) + "% " + "(" + twoDecPlaces(minSupport) + " (records)");
		
		// If no data (possibly as a result of an order and pruning operation)
		// return
		if (numOneItemSets==0) return;
		
		// Create Top level of T-tree (First pass of dataset)	
		startTtreeRef=null;
		numFrequentsets = 0;	
		createTtreeTopLevel();
		
		// Generate level 2
		generateLevel2();
		
		// Further passes of the dataset	
		createTtreeLevelN();
	}
	
    /* Set of methods for creating T-tree from P-tree which overide methods
    of smae name in parent class. */
    
    /*  GENERATE T-TREE TOP LEVEL 2 */
    /** Commences process to generate top level (singletons) of Ttree by
    looping through table level by level (row by row). */
    
    protected void createTtreeTopLevel2() {
		// Step through Ptree table	
		for(int index=1;index<startPtreeTable.length;index++) {
			// Check if level exists
			if (startPtreeTable[index] != null) {
				createTtreeTopLevel3(startPtreeTable[index]);
			}
	    }	
		
		// Destroy top level of P-tree table
		startPtreeTable[1] = null;
	}

    /** Processes level (row) in P-tree table to generate top level of T-tree.
    @param pTreeTableLevel the given level (row) of P-tree table records. */
    	
    protected void createTtreeTopLevel3(PtreeRecord[] pTreeTableLevel) {
		// Loop through level in P-tree table level
		for(int index=0;index<pTreeTableLevel.length;index++) {
			createTtreeTopLevel4(pTreeTableLevel[index].pTreeNodeLabel, pTreeTableLevel[index].support);
	    }	
	}

    /** Increments support counts in T-tree top level given a P-tree table
    label and an associated support value.
    @param pTreeNodeLabel the label associated with a P-tree node (not the union
    of its parent labels).
    &param pTreeNodeSupport the associated support value.*/
    		
    private void createTtreeTopLevel4(short[] pTreeNodeLabel, int pTreeNodeSupport) {
		// Loop through node label
		for (int index=0;index<pTreeNodeLabel.length;index++) {
			// Increment support for T-tree singleton node    
			startTtreeRef[pTreeNodeLabel[index]].support = startTtreeRef[pTreeNodeLabel[index]].support+pTreeNodeSupport;
			numUpdates++;
	    }
	}
	
    /* CREATE T-TREE LEVEL N */

    /** Commences process of adding support values to further levels of the
    T-tree (not the top level). */

    protected void createTtreeLevelN() {
        int nextLevel=2;
		
		// Loop while a further level exists
		while (nextLevelExists) {  	    
			// Add support   
			addSupportToTtreeLevelN(nextLevel);
			// Destroy current level of P-tree table
			startPtreeTable[nextLevel] = null;
			// Prune unsupported candidate sets
			pruneLevelN(startTtreeRef,nextLevel); 
			// Attempt to generate next level 
			nextLevelExists=false;
			generateLevelN(startTtreeRef,nextLevel,null); 
			nextLevel++; 
	    }
		
		//End
		System.out.println("Levels in T-tree = " + nextLevel);  
	}
		
    /* ADD SUPPORT VALUES TO T-TREE LEVEL N */

    /** Continues process of adding support alues to further levels of the
    T-tree (not the top level) by stepping through the Ptree table from the
    current required level upto the maximum level that may be contained in the
    table.
    @param level the (start) current level. */
     	
    protected void addSupportToTtreeLevelN(int level) {
		// Nested loop to step through P-tree table
		for (int index1=level;index1<startPtreeTable.length;index1++) {
			// Check that there are records in the table at current level
			if (startPtreeTable[index1] != null) {
				// step through records at currentd level in loop
				for(int index2=0;index2<pTreeNodesOfCardinalityN[index1]; index2++) {
					addSupportToTtreeLevelN(startTtreeRef,level, startPtreeTable[index1][index2].pTreeNodeLabel, startPtreeTable[index1][index2].pTreeItemSet, startPtreeTable[index1][index2].support);
				}
	        }
	    }
    }
	   						
    /* ADD SUPPORT VALUES TO T-TREE LEVEL N */

    /** Adds support to to appropriate nodes in T-tree at a given level and
    given a record from the P-tree table.
    @param linkRef the reference (pointer) to the current branch in the T-tree
    (top at start)
    @param level the desired level in T-tree
    @param pTreeNodeLabel the actual P-tree node itemSet label (not the union
    of its parent labels).
    @param pTreeItemSet the Uunion of the pTreeNodeLabel and all parent node
    itemSets of the current node.
    @param support the upport count			*/
    
    private void addSupportToTtreeLevelN(TtreeNode[] linkRef, int level, short[] pTreeNodeLabel, short[] pTreeItemSet, int support) {
		int index;
		int tTreeLength = linkRef.length;
		
		// At right leve;
		if (level == 1) {
			// Step through P-tree table itemSet
			for (index=0;index < pTreeItemSet.length;index++) {
				// Check that index is within Ttree array
				if (pTreeItemSet[index] >= tTreeLength) break;		
				// If valid node, i.e. index is within Ttree array therfore
				// node subsets are supported elsewhere, update
				if (linkRef[pTreeItemSet[index]] != null) {
					linkRef[pTreeItemSet[index]].support = linkRef[pTreeItemSet[index]].support + support; 
					numUpdates++;
				}
			}
	    }	
		
		// At wrong level    
		else {
			// Step through search itemSet
			int scLength = pTreeNodeLabel.length;
			for (index=0;index < scLength;index++) {	
				//for (index=0;index < scLength;index++) {		
				// Check that index is within Ttree array
				if (pTreeNodeLabel[index] >= tTreeLength) break;		
				// If there is a node with a child reference follow that 
				// reference
				if (linkRef[pTreeNodeLabel[index]] != null)  {
					if (linkRef[pTreeNodeLabel[index]].childRef != null) 
						addSupportToTtreeLevelN(linkRef[pTreeNodeLabel[index]].childRef, level-1,pTreeItemSet,pTreeItemSet,support);
					}
				}
			}	
		}
	  
    /*----------------------------------------------------------------------- */
    /*                                                                        */
    /*                           PUBLIC OUTPUT METHODS                        */
    /*                                                                        */
    /*----------------------------------------------------------------------- */
    
    /* Fout types of output:
    
    1) Output P-tree
    2) Output P-tree Statistics
    3) Output P-tree Table
    4) Output P-tree Table statistics */

    /* ---------------- */
    /* 1. OUTPUT P TREE */
    /* ---------------- */
    /** Commences process to output P-tree. */
      
    public void outputPtree() {
    	System.out.println();
	outputPtree1(startPtreeRef); 
	}

    /** Continues process to output P-tree.
    @param linkPtreeRef the reference to the start of the P-tree. */
    				
    public void outputPtree1(PtreeNodeTop[] linkPtreeRef) {
		String newNode;
		int counter = 1;
		
		// Start by processing top level
		for (int index=0;index<linkPtreeRef.length;index++) {
			if (linkPtreeRef[index] != null) { 
				outputPtree2(index,linkPtreeRef[index],counter);
				counter++;
	        }
	    }
	}

    /** Outputs top-level node of P-tree.
    @param index the current index in the top-level (array) of tghe P-tree.
    @param linkRef the reference to the P-tree top level node in question.
    @param counter the node counter (not necesserily the same as the index
    if some nodes are absent).    */

    private void outputPtree2(int index, PtreeNodeTop linkRef, int counter) {
		String newNode = Integer.toString(counter);

	// Outputnode number and support
	
	System.out.print("(" + newNode + ")");
	short[] itemSet = new short[1];
	itemSet[0] = (short) index;
	outputItemSet(itemSet);
	System.out.println("support = " + linkRef.support);
	
	// Continue
	outputPtree3(linkRef.childRef,newNode,1);
	}

    /** Outputs remainder of P-tree (not the top level).
    @param linkRef the reference to the current location in the P-tree.
    @param node the identifier for the current node (for output purposes only).
    @param counter the node counter (used to generate a new node identifier). */
	
    private void outputPtree3(PtreeNode linkRef, String node, int counter) {
	String newNode;

	if (linkRef != null) {
	    // Outputnode number
	    if (node == "start") newNode = Integer.toString(counter);
	    else {
	        newNode = node.concat(".");
	        newNode = newNode.concat(Integer.toString(counter));
		}
	    System.out.print("(" + newNode + ")");
	    outputItemSet(linkRef.itemSet);
	    System.out.println("support = " + linkRef.support);
	    // Continue
	    outputPtree3(linkRef.childRef,newNode,1);
	    counter++;
	    outputPtree3(linkRef.siblingRef,node,counter);
	    }
	}	
	
    /* ---------------------- */	
    /* 2. OUTPUT P TREE STATS */
    /* ---------------------- */
    /** Commences the process of outputting P-tree statistics (for diagnostic
    purposes): (a) Storage, (b) Number of nodes on P-tree, (c) number of
    partial support increments (updates) and (d) generation time. */

    public void outputPtreeStats() {
        System.out.println("P-TREE STATISTICS\n-----------------");
	System.out.println(calculateStorage(startPtreeRef) + 
							" (Bytes) storage");
	System.out.println(calculateNumNodes(startPtreeRef) + " nodess");
	System.out.println(numberOfNodeUpdates + " support value increments");
	}
    
    /* OUTPUT P TREE STORAGE: */

    /** Outputs P-tree storgae requirements in Bytes. */

    public void outputPtreeStorage() {
        int storage;
	
	storage = calculateStorage(startPtreeRef);
	
	System.out.println("P-tree storage = " + storage + " (Bytes)");
	}

    /** Commences process to calculate P-tree storage requirements.
    @param linkPtreeRef the reference to the current portion of the P-tree.
    @return total required storage in bytes. */
    				
    private int calculateStorage(PtreeNodeTop[] linkPtreeRef) {
	int storage = 4;	// For start reference
	
	// Start by processing top level
	
	for (int index=1;index<linkPtreeRef.length;index++) {
	    if (linkPtreeRef[index] != null) storage = calculateStorage(storage,
	    			linkPtreeRef[index]);
	    storage = storage+4;
	    }
	// Return
	
	return(storage);
	}

    /** Commences process to calculates storage requirements for a branch of
    the P-tree eminating from the top level.
    @param storage the required storage sofar.
    @param linkref the reference to the start of the branch.
    @return total required storage in bytes for branch of P-tree. */

    private int calculateStorage(int storage, PtreeNodeTop linkRef) {		
		storage = storage+8; // For top level node
		// Continue
		return(calculateStorage(storage,linkRef.childRef));
	}

    /** Calculates recursivly the storage requirements for a sub-branch of the
    P-tree.
    @param  storage the required storage sofar.
    @param linkref the reference to current location in the P-tree branch.
    @return total required storage in bytes for sub-branch of P-tree. */

    private int calculateStorage(int storage, PtreeNode linkRef) {					
		if (linkRef != null) {
			// 4 each foe childRef, siblingRef and support count
			storage = storage+12+(linkRef.itemSet.length*2);
			// Continue
			storage = calculateStorage(storage,linkRef.childRef);
			storage = calculateStorage(storage,linkRef.siblingRef);
	    }
		
		// Return
		return(storage);    
	}		
        
    /* OUTPUT NUMBER OF P TREE NODES: */

    /** Outputs total number of P-tree nodes (and the number of support 
    value increments). */

    public void outputNumNodes() {
        int num = 0;
		
		num = calculateNumNodes(startPtreeRef);
		
		System.out.println("Number of P-tree nodes = " + num);
		System.out.println("Number of P-tree support value increments = " + numberOfNodeUpdates);
	}

    /** Commence process of determining total number of nodes in P-tree.
    @param linkPtreeRef the reference to the start of the P-tree.
    @return total number of nodes. */
    				
    private int calculateNumNodes(PtreeNodeTop[] linkPtreeRef) {
		int num = 0;	// For start reference
		
		// Start by processing top level
		for (int index=1;index<linkPtreeRef.length;index++) {
			if (linkPtreeRef[index] != null) num = 1 + calculateNumNodes(num, linkPtreeRef[index].childRef);
	    }
		
		// Return
		return(num);
	}

    /** Commence process of determining total number of nodes in (sub-) branch
    of P-tree.
    @param linkPtreeRef the reference to the current location in the P-tree.
    @param the node count so far
    @return total number of nodes. */	

    private int calculateNumNodes(int num, PtreeNode linkRef) {
		if (linkRef != null) {
			num++;
			// Continue
			num = calculateNumNodes(num,linkRef.childRef);
			num = calculateNumNodes(num,linkRef.siblingRef);
	    }	
		
		// Return
		return(num);    
	}	
	
    /* ---------------------- */
    /* 3. OUTPUT P-TREE TABLE */
    /* ---------------------- */

    /** Outputs P-tree table. */

    public void outputPtreeTable() {
		int index1,index2;
		
		System.out.println("P-ree Nodes of cardinality [N]: ");
		for (index1=1;index1<pTreeNodesOfCardinalityN.length;index1++) 
			System.out.println("[" + index1 + "] " + pTreeNodesOfCardinalityN[index1]);

		System.out.println("Marker values on completion of P-tree table " + "generation: ");
        for (index1=1;index1<pTreeTableMarker.length;index1++) 
			System.out.println("[" + index1 + "] " + pTreeTableMarker[index1]);

		// Step through Ptree table
		for(index1=1;index1<startPtreeTable.length;index1++) {
			System.out.println("LEVEL = " + index1);
			if (startPtreeTable[index1] == null) System.out.print("null");
			else {
				for(index2=0;index2<pTreeTableMarker[index1];index2++) {
					System.out.print("Node label = ");
					outputItemSet(startPtreeTable[index1][index2].pTreeNodeLabel);
					System.out.print(" Itemset = ");
					outputItemSet(startPtreeTable[index1][index2].pTreeItemSet);
					System.out.println(" sup = " + startPtreeTable[index1][index2].support);
				} 
			}
			System.out.println();
	    }	
	}

    /* ---------------------------- */
    /* 4. OUTPUT P-TREE TABLE STATS */
    /* ---------------------------- */
    /** Outputs storage requirements for P-tree table. */

    public void outputPtreeTableStats() {
		int index1,index2;
		int storage=0, nodeCounter, nodeTotal=0;
		
		// For each level in the P-tree table
		for(index1=1;index1<startPtreeTable.length;index1++) {
			nodeCounter=0;
			// If null we still need a pointer so 4 bytes
			if (startPtreeTable[index1] == null) storage = storage+4;
			// Else step through records at this level
			else {
				for(index2=0;index2<pTreeTableMarker[index1];index2++) {
					nodeCounter++;
					// 4 for support and 2 for each item in search itemSet
					storage = storage + 4 + (startPtreeTable[index1][index2].pTreeNodeLabel.length*2);
					// 2 for each item in pTree itemSet
					storage = storage + (startPtreeTable[index1][index2].pTreeItemSet.length*2);
				} 
			} 
			
			nodeTotal = nodeTotal+nodeCounter;
	}
	
	System.out.println("P-tree Table Storage = " + storage + " (" + nodeTotal + " nodes)");
	}		
}


