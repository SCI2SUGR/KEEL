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

/* ------------------------------------------------------------------------- */
/*                                                                           */
/*                             TOTAL SUPPORT TREE                            */
/*                                                                           */
/*                                Frans Coenen                               */
/*                                                                           */
/*                               10 January 2003                             */
/*        (Revised 23/1/3003, 8/2/2003, 18/3/2003, 3/3/2003. 7/4/2004)       */
/*                                                                           */
/*                       Department of Computer Science                      */
/*                         The University of Liverpool                       */
/*                                                                           */ 
/* ------------------------------------------------------------------------- */

/* Structure:

AssocRuleMining
      |
      +-- TotalSupportTree	

To compile: javac AprioriTsortedPrunedApp.java
To run */

/* Java packages */      
package keel.Algorithms.Associative_Classification.ClassifierCMAR;

import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 * Methods concerned with the generation, processing and manipulation of 
T-tree data storage structures used to hold the total support counts for large 
itemsets.
 *
 * @author Frans Coenen 3 July 2003
 * @author Modified by Jesus Alcala (University of Granada) 09/02/2010
 * @author Modified by Sergio Almecija (University of Granada) 23/05/2010
 * @version 1.0
 * @since JDK1.5
 */
public class TotalSupportTree extends AssocRuleMining {

    /* ------ FIELDS ------ */
	
    // Data structures    
    /** The reference to start of t-tree. */
    protected TtreeNode[] startTtreeRef;	 
    /** The serialisation array (used for distributed ARM applications
    when sending T-trees from one processor to another via a JavaSpace). */
    //protected int[] serializationArray;
    /** The marker to the "current" location in the serialisation array. <P>
    initialised to zero. */
    //protected int serializationRef = 0;
    
    // Constants
    /** The maximum number of frequent sets that may be generated. */
    protected final int MAX_NUM_FREQUENT_SETS = 1000000;
    
    // Other fields     
    /** The next level indicator flag: set to <TT>true</TT> if new level 
    generated and by default. */
    protected boolean nextLevelExists = true ; 
    /** Instance of the class RuleList */
    protected RuleList currentRlist = null;

    // Diagnostics 
    /** The number of frequent sets (nodes in t-tree with above minimum 
    support) generated so far. */
    protected int numFrequentsets =0;
    /** The number of updates required to generate the T-tree. */
    protected long numUpdates   = 0l;
    /** Time to generate P-tree. */
    protected String duration = null;
    
    /* ------ CONSTRUCTORS ------ */

    /** 
     * Processes command line arguments.
     * @param minConf double Minimum confidence threshold
     * @param minSup double Minimum support threshold
     * @param delta int Minimum coverage threshold
     */
     public TotalSupportTree(double minConf, double minSup, int delta) {
		super(minConf, minSup);
		// Create RuleList object for later usage
		currentRlist = new RuleList(delta);
	}
	
    /* ------ METHODS ------ */
    
    /*----------------------------------------------------------------------- */
    /*                                                                        */
    /*                       T-TREE BUILDING METHODS                          */
    /*                                                                        */
    /*----------------------------------------------------------------------- */
	
    /* CREATE T-TREE TOP LEVEL */    
    /** Generates level 1 (top) of the T-tree. */
    		
    protected void createTtreeTopLevel() {
        
	// Dimension and initialise top level of T-tree	
	startTtreeRef = new TtreeNode[numOneItemSets+1];
	for (int index=1;index<=numOneItemSets;index++) 
	    			startTtreeRef[index] = new TtreeNode();
	    
        // Add support for each 1 itemset        
	createTtreeTopLevel2();

	// Prune top level, setting any unsupport 1 itemsets to null 
	pruneLevelN(startTtreeRef,1); 
	}
    
    /* CREATE T-TREE 2nd LEVEL */	
    /** Adds supports to level 1 (top) of the T-tree. */
    	
    protected void createTtreeTopLevel2() {  	
	/* STUBB */
	}
	
    /*---------------------------------------------------------------------- */
    /*                                                                       */
    /*                                 PRUNING                               */
    /*                                                                       */
    /*---------------------------------------------------------------------- */ 
    
    /* PRUNE LEVEL N */
    
    /** Prunes the given level in the T-tree. <P> Operates in a recursive 
    manner to first find the appropriate level in the T-tree before processing 
    the required level (when found). Pruning carried out according to value of
    <TT>minSupport</TT> field.
    @param linkRef The reference to the current sub-branch of T-tree (start at 
    top of tree)
    @param level the level marker, set to the required level at the start and 
    then decremented by 1 on each recursion. 
    @return true if all nodes at a given level in the given branch of the 
    T-tree have been prined, false otherwise. */
    
    protected boolean pruneLevelN(TtreeNode [] linkRef, int level) {
        int size = linkRef.length;
	
	// At right leve;	
	if (level == 1) {
	    boolean allUnsupported = true;
	    // Step through level and set to null where below min support
	    for (int index1=1;index1<size;index1++) {
	        if (linkRef[index1] != null) {
	            if (linkRef[index1].support < minSupport) 
		    		linkRef[index1] = null;
	            else {
		        numFrequentsets++;
			allUnsupported = false;
			}
		    }
		}
	    return(allUnsupported);
	    }
	    	
	// Wrong level, Step through row
	for (int index1=level;index1<size;index1++) {
	    if (linkRef[index1] != null) {		
		// If child branch step down branch
		if (linkRef[index1].childRef != null) {
		    if (pruneLevelN(linkRef[index1].childRef,level-1)) 
			    		linkRef[index1].childRef=null;
		    }
		}
	    }	
	return(false);
	}
					
    /*---------------------------------------------------------------------- */
    /*                                                                       */
    /*                            LEVEL GENERATION                           */
    /*                                                                       */
    /*---------------------------------------------------------------------- */ 
    
    /* GENERATE LEVEL 2 */
    
    /** Generates level 2 of the T-tree. <P> The general 
    <TT>generateLevelN</TT> method assumes we have to first find the right 
    level in the T-tree, that is not necessary in this case of level 2. */
    
    protected void generateLevel2() {	    
	
	// Set next level flag	
	nextLevelExists=false;
	
	// loop through top level	
	for (int index=2;index<startTtreeRef.length;index++) {
	    // If supported T-tree node (i.e. it exists)
	    if (startTtreeRef[index] != null) generateNextLevel(startTtreeRef, index,realloc2(null,(short) index));	
	    }
	}
	
    /* GENERATE LEVEL N */
    
    /** Commences process of generating remaining levels in the T-tree (other 
    than top and 2nd levels). <P> Proceeds in a recursive manner level by level
    untill the required level is reached. Example, if we have a T-tree of the form:
    
    <PRE>
    (A) ----- (B) ----- (C)
               |         |
	       |         |
	      (A)       (A) ----- (B)
    </PRE><P>	                           
    Where all nodes are supported and we wish to add the third level we would
    walk the tree and attempt to add new nodes to every level 2 node found.
    Having found the correct level we step through starting from B (we cannot
    add a node to A), so in this case there is only one node from which a level
    3 node may be attached. 
    @param linkRef the reference to the current sub-branch of T-tree (start at 
    top of tree).
    @param level the level marker, set to the required level at the start and
    then decremented by 1 on each recursion.
    @param itemSet the current itemset under consideration. */
    
    protected void generateLevelN(TtreeNode[] linkRef, int level, 
    							short[] itemSet) {
	int index1;
	int localSize = linkRef.length;
	
	// Correct level
	
	if (level == 1) {
	    for (index1=2;index1<localSize;index1++) {
	        // If supported T-tree node
	    	if (linkRef[index1] != null) generateNextLevel(linkRef,index1,
					realloc2(itemSet,(short) index1));		
	        }
	    }
	
	// Wrong level
	
	else {
		
	    for (index1=level;index1<localSize;index1++) {
	        // If supported T-tree node
	        if (linkRef[index1]!=null && linkRef[index1].childRef!=null) {
		    generateLevelN(linkRef[index1].childRef,level-1,
		    			realloc2(itemSet,(short) index1));
		    }	
		}
	    }
	}

    /* GENERATE NEXT LEVEL */
    
    /** Generates a new level in the T-tree from a given "parent" node. <P> 
    Example 1, given the following:
    
    <PRE>
    (A) ----- (B) ----- (C)
               |         |
	       |         |
	      (A)       (A) ----- (B) 
    </PRE><P>	      
    where we wish to add a level 3 node to node (B), i.e. the node {A}, we 
    would proceed as follows:
    <OL>
    <LI> Generate a new level in the T-tree attached to node (B) of length 
    one less than the numeric equivalent of B i.e. 2-1=1.
    <LI> Loop through parent level from (A) to node immediately before (B). 
    <LI> For each supported parent node create an itemset label by combing the
    index of the parent node (e.g. A) with the complete itemset label for B --- 
    {C,B} (note reverse order), thus for parent node (B) we would get a new
    level in the T-tree with one node in it --- {C,B,A} represented as A.
    <LI> For this node to be a candidate large item set its size-1 subsets must 
    be supported, there are three of these in this example {C,A}, {C,B} and
    {B,A}. We know that the first two are supported because they are in the
    current branch, but {B,A} is in another branch. So we must generate this
    set and test it. More generally we must test all cardinality-1 subsets
    which do not include the first element. This is done using the method 
    <TT>testCombinations</TT>. 
    </OL>
    <P>Example 2, given:
    <PRE>
    (A) ----- (D)
               |         
	       |         
	      (A) ----- (B) ----- (C)
	                           |
				   |
				  (A) ----- (B) 
    </PRE><P>	 
    where we wish to add a level 4 node (A) to (B) this would represent the
    complete label {D,C,B,A}, the N-1 subsets will then be {{D,C,B},{D,C,A},
    {D,B,A} and {C,B,A}}. We know the first two are supported becuase they are
    contained in the current sub-branch of the T-tree, {D,B,A} and {C,B,A} are
    not.
    </OL> 
    @param parentRef the reference to the level in the sub-branch of the T-tree
    under consideration.
    @param endIndex the index of the current node under consideration.
    @param itemSet the complete label represented by the current node (required
    to generate further itemsets to be X-checked). */
    
    protected void generateNextLevel(TtreeNode[] parentRef, int endIndex, 
    			short[] itemSet) {
	parentRef[endIndex].childRef = new TtreeNode[endIndex];	// New level
        short[] newItemSet;	
	// Generate a level in Ttree
	
	TtreeNode currentNode = parentRef[endIndex];
	
	// Loop through parent sub-level of siblings upto current node
	for (int index=1;index<endIndex;index++) {	
	    // Check if "uncle" element is supported (i.e. it exists) 
	    if (parentRef[index] != null) {	
		// Create an appropriate itemSet label to test
	        newItemSet = realloc2(itemSet,(short) index);
		if (testCombinations(newItemSet)) {
		    currentNode.childRef[index] = new TtreeNode();
		    nextLevelExists=true;
		    }
	        else currentNode.childRef[index] = null;
	        }
	    }
	}  
	
    /* TEST COMBINATIONS */
    
    /** Commences the process of testing whether the N-1 sized sub-sets of a 
    newly created T-tree node are supported elsewhere in the Ttree --- (a 
    process refered to as "X-Checking"). <P> Thus given a candidate large 
    itemsets whose size-1 subsets are contained (supported) in the current 
    branch of the T-tree, tests whether size-1 subsets contained in other 
    branches are supported. Proceed as follows:   
    <OL>
    <LI> Using current item set split this into two subsets:
    <P>itemSet1 = first two items in current item set
    <P>itemSet2 = remainder of items in current item set
    <LI> Calculate size-1 combinations in itemSet2
    <LI> For each combination from (2) append to itemSet1 
    </OL>
    <P>Example 1: 
    <PRE>
    currentItemSet = {A,B,C} 
    itemSet1 = {B,A} (change of ordering)
    size = {A,B,C}-2 = 1
    itemSet2 = {C} (currentItemSet with first two elements removed)
    calculate combinations between {B,A} and {C}
    </PRE>
    <P>Example 2: 
    <PRE>
    currentItemSet = {A,B,C,D} 
    itemSet1 = {B,A} (change of ordering)
    itemSet2 = {C,D} (currentItemSet with first two elements removed)
    calculate combinations between {B,A} and {C,D}
    </PRE>
    @param currentItemSet the given itemset.		*/
    
    protected boolean testCombinations(short[] currentItemSet) {  
	// No need to test 1- and 2-itemsets
        if (currentItemSet.length < 3) return(true);
	   
	// Creat itemSet1 (note ordering)
	
	short[] itemSet1 = new short[2];
	itemSet1[0] = currentItemSet[1];
	itemSet1[1] = currentItemSet[0];
	
	// Creat itemSet2
	
	int size = currentItemSet.length-2;
	short[] itemSet2 = removeFirstNelements(currentItemSet,2);
	
	// Calculate combinations

	return(combinations(null,0,2,itemSet1,itemSet2));
	}
	
    /* COMBINATIONS */
    
    /** Determines the cardinality N combinations of a given itemset and then
    checks whether those combinations are supported in the T-tree. <P> 
    Operates in a recursive manner.
    <P>Example 1: Given --- sofarSet=null, 
    startIndex=0, endIndex=2, itemSet1 = {B,A} and itemSet2 = {C}
    <PRE>
    itemSet2.length = 1
    endIndex = 2 greater than itemSet2.length if condition succeeds
    tesSet = null+{B,A} = {B,A}
    retutn true if {B,A} supported and null otherwise
    </PRE>
    <P>Example 2: Given --- sofarSet=null, 
    startIndex=0, endIndex=2, itemSet1 = {B,A} and itemSet2 = {C,D}
    <PRE>
    endindex not greater than length {C,D}
    go into loop
    tempSet = {} + {C} = {C}
    	combinations with --- sofarSet={C}, startIndex=1, 
			endIndex=3, itemSet1 = {B,A} and itemSet2 = {C}
	endIndex greater than length {C,D}
	testSet = {C} + {B,A} = {C,B,A}
    tempSet = {} + {D} = {D}
    	combinations with --- sofarSet={D}, startIndex=1, 
			endIndex=3, itemSet1 = {B,A} and itemSet2 = {C}
	endIndex greater than length {C,D}
	testSet = {D} + {B,A} = {D,B,A}
    </PRE>
    @param sofarSet The combination itemset generated so far (set to null at
    start)
    @param startIndex the current index in the given itemSet2 (set to 0 at 
    start).
    @param endIndex The current index of the given itemset (set to 2 at start)
    and incremented on each recursion until it is greater than the length of
    itemset2.
    @param itemSet1 The first two elements (reversed) of the totla label for the
    current item set.
    @param itemSet2 The remainder of the current item set.
    */	
	
    private boolean combinations(short[] sofarSet, int startIndex,
    		    int endIndex, short[] itemSet1, short[] itemSet2) {
	// At level
	
	if (endIndex > itemSet2.length) {
	    short[] testSet = append(sofarSet,itemSet1);
	    // If testSet exists in the T-tree sofar then it is supported
	    return(findItemSetInTtree(testSet));
	    }
	
	// Otherwise
	else {
	    short[] tempSet;
	    for (int index=startIndex;index<endIndex;index++) {
	        tempSet = realloc2(sofarSet,itemSet2[index]);
	        if (!combinations(tempSet,index+1,endIndex+1,itemSet1,
				itemSet2)) return(false);
	        }
	    }						
        
	// Return
	
	return(true);
	}
		
    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*                           ADD TO T-TREE                          */
    /*                                                                  */
    /* ---------------------------------------------------------------- */
	
    /* ADD TO T-TREE */
    
    /** Commences process of adding an itemset (with its support value) to a
    T-tree when using a T-tree either as a storage mechanism, or when adding to 
    an existing T-tree. 
    @param itemSet The given itemset. Listed in numeric order (not reverse
    numeric order!).
    @param support The support value associated with the given itemset. */
    
    /*public void addToTtree(short[] itemSet, int support) {
        // Determine index of last elemnt in itemSet.
	int endIndex = itemSet.length-1;
	
	// Add itemSet to T-tree.
        startTtreeRef = addToTtree(startTtreeRef,numOneItemSets+1,
			endIndex,itemSet,support);
	}   */
		
    /* ADD TO T-TREE */
    
    /** Inserts a node into a T-tree. <P> Recursive procedure.
    @param linkRef the reference to the current array in Ttree.
    @param size the size of the current array in T-tree.
    @param endIndex the index of the last elemnt/attribute in the itemset, 
    which is also used as a level counter.	
    @param itemSet the given itemset.
    @param support the support value associated with the given itemset. 
    @return the reference to the revised sub-branch of t-tree. */
    
    /*private TtreeNode[] addToTtree(TtreeNode[] linkRef, int size, int endIndex,
    				short[] itemSet, int support) {
	// If no array describing current level in the T-tree or T-tree
	// sub-branch create one with "null" nodes.	
	if (linkRef == null) {
	    linkRef = new TtreeNode[size];
	    for(int index=1;index<linkRef.length;index++) 
			linkRef[index] = null;
	    }
	
	// If null node at index of array describing curent level in T-tree 
	// (T-tree sub-branch) create a T-tree node describing the current 
	// itemset sofar.
	int currentAttribute = itemSet[endIndex]; 
	if (linkRef[currentAttribute] == null)
	    		linkRef[currentAttribute] = new TtreeNode();
	
	// If at right level add support 	
	if (endIndex == 0) {
	    linkRef[currentAttribute].support =
	    			linkRef[currentAttribute].support + support;
	    return(linkRef);
	    }
	    
	// Otherwise proceed down branch and return	
	linkRef[currentAttribute].childRef = 
			addToTtree(linkRef[currentAttribute].childRef,
				currentAttribute,endIndex-1,itemSet,support);
	// Return
	return(linkRef);
	}    */
    	
    /*---------------------------------------------------------------------- */
    /*                                                                       */
    /*                        T-TREE SEARCH METHODS                          */
    /*                                                                       */
    /*---------------------------------------------------------------------- */  
    
    /* FIND ITEM SET IN T-TREE*/
    
    /** Commences process of determining if an itemset exists in a T-tree. <P> 
    Used to X-check existance of Ttree nodes when generating new levels of the 
    Tree. Note that T-tree node labels are stored in "reverse", e.g. {3,2,1}. 
    @param itemSet the given itemset (IN REVERSE ORDER). 
    @return returns true if itemset found and false otherwise. */
    
    protected boolean findItemSetInTtree(short[] itemSet) {

    	// first element of itemset in Ttree (Note: Ttree itemsets stored in 
	// reverse)
  	if (startTtreeRef[itemSet[0]] != null) {
    	    int lastIndex = itemSet.length-1;
	    // If single item set return true
	    if (lastIndex == 0) return(true);
	    // Otherwise continue down branch
	    else if (startTtreeRef[itemSet[0]].childRef!=null) {
	        return(findItemSetInTtree2(itemSet,1,lastIndex,
			startTtreeRef[itemSet[0]].childRef));
	        }
	    else return(false);
	    }	
	// Item set not in Ttree
    	else return(false);
	}
    
    /** Returns true if the given itemset is found in the T-tree and false 
    otherwise. <P> Operates recursively. 
    @param itemSet the given itemset. 
    @param index the current index in the given T-tree level (set to 1 at
    start).
    @param lastIndex the end index of the current T-tree level.
    @param linRef the reference to the current T-tree level. 
    @return returns true if itemset found and false otherwise. */
     
    private boolean findItemSetInTtree2(short[] itemSet, int index, 
    			int lastIndex, TtreeNode[] linkRef) {  

        // Attribute at "index" in item set exists in Ttree
  	if (linkRef[itemSet[index]] != null) {
  	    // If attribute at "index" is last element of item set then item set
	    // found
	    if (index == lastIndex) return(true);
	    // Otherwise continue
	    else if (linkRef[itemSet[index]].childRef!=null) {
	        return(findItemSetInTtree2(itemSet,index+1,lastIndex,
	    		linkRef[itemSet[index]].childRef));
	        }
	    else return(false); 
	    }	
	// Item set not in Ttree
	else return(false);    
    	}

    /* GET SUPPORT FOT ITEM SET IN T-TREE */
    
    /** Commences process for finding the support value for the given item set
    in the T-tree. <P> Used when generating Associoation Rules (ARs). Note that
    itemsets are stored in reverse order in the T-tree therefore the given
    itemset must be processed in reverse. 
    @param itemSet the given itemset. 
    @return returns the support value (0 if not found). */
    
    protected int getSupportForItemSetInTtree(short[] itemSet) {
	int lastIndex = itemSet.length-1;
	
    	// Last element of itemset in Ttree (Note: Ttree itemsets stored in 
	// reverse)
  	if (startTtreeRef[itemSet[lastIndex]] != null) {
	    // If single item set return support
	    if (lastIndex == 0) return(startTtreeRef[itemSet[0]].support);
	    // Otherwise continue down branch
	    else return(getSupportForItemSetInTtree2(itemSet,lastIndex-1,
			startTtreeRef[itemSet[lastIndex]].childRef));
	    }	
	// Item set not in Ttree thererfore return 0
    	else return(0);
	}
    
    /** Returns the support value for the given itemset if found in the T-tree 
    and 0 otherwise. <P> Operates recursively. 
    @param itemSet the given itemset. 
    @param index the current index in the given itemset.
    @param linRef the reference to the current T-tree level. 
    @return returns the support value (0 if not found). */
     
    private int getSupportForItemSetInTtree2(short[] itemSet, int index, 
    			TtreeNode[] linkRef) {  

        // Element at "index" in item set exists in Ttree
  	if (linkRef[itemSet[index]] != null) {
  	    // If element at "index" is last element of item set then item set
	    // found
	    if (index == 0) return(linkRef[itemSet[0]].support);
	    // Otherwise continue
	    else return(getSupportForItemSetInTtree2(itemSet,index-1,
	    		linkRef[itemSet[index]].childRef));
	    }	
	// Item set not in Ttree thererfore return 0
	else return(0);    
    	}
		
    /*----------------------------------------------------------------------- */
    /*                                                                        */
    /*                    ASSOCIATION RULE (AR) GENERATION                    */
    /*                                                                        */
    /*----------------------------------------------------------------------- */	
    
    /* GENERATE ASSOCIATION RULES */
    
    /** Initiates process of generating Association Rules (ARs) from a 
    T-tree. */
    
    /*public void generateARs() {
	// Command line interface output
	System.out.println("GENERATE ARs:\n-------------");
	
	// Set rule data structure to null
	currentRlist.startRulelist = null;
	
	// Generate
	generateARs2();
	}*/
	
    /** Loops through top level of T-tree as part of the AR generation 
    process. */
    
    /* private void generateARs2() {	
	// Loop	
	for (int index=1;index <= numOneItemSets;index++) {
	    if (startTtreeRef[index] !=null) {
	        if (startTtreeRef[index].support >= minSupport) {
	            short[] itemSetSoFar = new short[1];
		    itemSetSoFar[0] = (short) index;
		    generateARs(itemSetSoFar,index,
		    			startTtreeRef[index].childRef);
		    }
		}
	    } 
	} */
		
    /* GENERATE ASSOCIATION RULES */
    
    /** Continues process of generating association rules from a T-tree by 
    recursively looping through T-tree level by level. 
    @param itemSetSofar the label for a T-treenode as generated sofar.
    @param size the length/size of the current array lavel in the T-tree.
    @param linkRef the reference to the current array lavel in the T-tree. */
    
    /* protected void generateARs(short[] itemSetSofar, int size,
    							TtreeNode[] linkRef) {
	
	// If no more nodes return	
	if (linkRef == null) return;
	
	// Otherwise process
	for (int index=1; index < size; index++) {
	    if (linkRef[index] != null) {
	        if (linkRef[index].support >= minSupport) {
		    // Temp itemset
		    short[] tempItemSet = realloc2(itemSetSofar,(short) index);
		    // Generate ARs for current large itemset
		    generateARsFromItemset(tempItemSet,linkRef[index].support);
	            // Continue generation process
		    generateARs(tempItemSet,index,linkRef[index].childRef); 
	            }
		}
	    }
	}*/
    
    /* GENERATE ASSOCIATION RULES */
    
    /** Generates all association rules for a given large item set found in a
    T-tree structure. <P> Called from <TT>generateARs</TT> method.
    @param itemSet the given large itemset.
    @param support the associated support value for the given large itemset. */
    
    /*private void generateARsFromItemset(short[] itemSet, double support) {
    	// Determine combinations
	short[][] combinations = combinations(itemSet);
	
	// Loop through combinations
	for(int index=0;index<combinations.length;index++) {
            // Find complement of combimation in given itemSet
	    short[] complement = complement(combinations[index],itemSet);
	    // If complement is not empty generate rule
	    if (complement != null) {
	        double confidenceForAR = getConfidence(combinations[index],
		    						support);
		if (confidenceForAR >= confidence) {
		       currentRlist.insertRuleintoRulelist(combinations[index],
		     				   complement,confidenceForAR);
		    }
		} 
	    }
	}*/
	
    /*----------------------------------------------------------------------- */
    /*                                                                        */
    /*                                GET METHODS                             */
    /*                                                                        */
    /*----------------------------------------------------------------------- */
    
    /* GET CONFIDENCE */
    
    /** Calculates and returns the conidence for an AR given the antecedent
    item set and the support for the total item set.
    @param antecedent the antecedent (LHS) of the AR.
    @param support the support for the large itemset from which the AR is
    generated.
    @return the associated confidence value. */
    
    protected double getConfidence(short[] antecedent, double support) {
        // Get support forantecedent
        double supportForAntecedent = (double)
				getSupportForItemSetInTtree(antecedent);
				
	// Return confidence
	double confidenceForAR = ((double) support/supportForAntecedent)*10000;
	int tempConf = (int) confidenceForAR;
	confidenceForAR = (double) tempConf/100;
	return(confidenceForAR); 
	}

    /* GET CONFIDENCE */
    
    /** Calculates and returns the conidence for an AR given the support for 
    both the antecedent and the entire item set.
    @param antecedentSupp the support for antecedent (LHS) of the AR.
    @param totalSupp the support for the large itemset from which the AR is
    generated.
    @return the associated confidence value. */
    
    protected double getConfidence(double antecedentSupp, double totalSupp) {
        // Return confidence
	double confidenceForAR = ((double) totalSupp/antecedentSupp)*10000;
	int tempConf = (int) confidenceForAR;
	confidenceForAR = (double) tempConf/100;
	return(confidenceForAR); 
	}    
	
    /* GET START OF T-TRRE */    
    /** Returns the reference to the start of the T-tree.
    @return The start of the T-tree. */
    
    /*public TtreeNode[] getStartOfTtree() {
    	return(startTtreeRef);
	}  */
	
    /* GET NUMBER OF FREQUENT SETS */
    /** Returns number of frequent/large (supported) sets in T-tree.
    @return the number of supporte sets. */
    
    public int getNumFreqSets() {
        // If emtpy tree (i.e. no supported sets) do nothing
	if (startTtreeRef ==  null) return(0);
		
	// Loop
	int num=0;
	for (int index=1; index <= numOneItemSets; index++) {
	    // Check for null valued top level Ttree node.
	    if (startTtreeRef[index] !=null) {
	        if (startTtreeRef[index].support >= minSupport) 
			num = countNumFreqSets(index,
	    				startTtreeRef[index].childRef,num+1);
		}
	    }
	
	// Output
	return(num);
	}

    /* GET NUMBER OF FREQUENT ONE ITEM SETS */
    /** Returns number of frequent/large (supported) one itemsets in T-tree.
    @return the number of supporte ine itemsets. */
    
    /*public int getNnumFreqOneItemSets() {
	return(numOneItemSets);
	}*/	

    /* GET MINIMUM SUPPORT VALUE */
    /** Returns the minimum support threshold value in terms of a number 
    records.
    @return the minimum support value. */
    
    public double getMinSupport() {
	return(minSupport);
	}

    /* GET CURRENT RULE LIST OBJECT */	   	

    /** Gets the current instance of the RuleList class.
    @return the current RuleList object. */

    public RuleList getCurrentRuleListObject() {
        return(currentRlist);
	}	
			
    /*----------------------------------------------------------------------- */
    /*                                                                        */
    /*                              UTILITY METHODS                           */
    /*                                                                        */
    /*----------------------------------------------------------------------- */
    
    /* SET NUMBER ONE ITEM SETS */
    
    /** Sets the number of one item sets field (<TT>numOneItemSets</TT> to 
    the number of supported one item sets. */
    
    public void setNumOneItemSets() {
        numOneItemSets=getNumSupOneItemSets();
	}
	
    /* COUNT T-TREE NODES */
    
    /** Commences process of counting the number of nodes in the T-tree. <P> 
    Not the same as counting the number of nodes created as some of these may 
    have been pruned. 
    @return the number of nodes. */
    
    /*protected int countNumberOfTtreeNodes() {
        int counter = 0;
	
	// Loop
	
	for (int index=1; index < startTtreeRef.length; index++) {
	    if (startTtreeRef[index] !=null) {
	        counter++;
	        counter = countNumberOfTtreeNodes(counter,
						startTtreeRef[index].childRef);
		}
	    }    
        
	// Return
	return(counter);
	}   */
    
    /* COUNT T-TREE NODES IN N SUB-BRANCHES*/
    
    /** Commences process of counting the number of nodes in a sequence of
    T-tree sub-branches.  
    @return the number of nodes. */
    
    /*private int countNumTtreeNodesInNbranches(int startIndex, int endIndex) {
        int counter = 0;
	
	// Loop
	
	for (int index=startIndex; index <= endIndex; index++) {
	    if (startTtreeRef[index] !=null) {
	        counter++;
	        counter = countNumberOfTtreeNodes(counter,
						startTtreeRef[index].childRef);
		}
	    }    
        
	// Return
	return(counter);
	}    */
	
    /** Continues process of counting number of nodes in a T-tree.
    @param counter the count sofar.
    @param linkref the reference to the current location in the T-tree.
    @return the updated count. */
    
    /*private int countNumberOfTtreeNodes(int counter,TtreeNode[] linkRef) {
        // Check for empty branch/sub-branch.
	if (linkRef == null) return(counter);
	
	// Loop through current level of branch/sub-branch.
	for (int index=1;index<linkRef.length;index++) {
	    if (linkRef[index] != null) {
	        counter++;
	        counter = countNumberOfTtreeNodes(counter,
						linkRef[index].childRef); 
		}
	    }    
        
	// Return
	return(counter);    
	}      */

    /** Counts number of nodes in T-tree at level N.
    @param counter the count sofar.
    @param level the required level.
    @param linkref the reference to the current location in the T-tree.
    @return the updated count. */
    
    /*private int countNumTtreeNodesLevelN(int counter, int level,
    		TtreeNode[] linkRef) {
        // No nodes in this sub-branch level?
	if (linkRef == null) return(counter);
	
	// If at right level count nodes with support of 1 or more
	if (level == 1) {
	    for(int index=1;index<linkRef.length;index++) {
	        if ((linkRef[index] != null) && (linkRef[index].support>0)) 
								counter++;
	        }
	    }
	    
	// If at wrong level proceed down child branches
	else {
	    for (int index=1;index<linkRef.length;index++) {
	        if (linkRef[index] != null) counter = 
	    			countNumTtreeNodesLevelN(counter,level-1,
						linkRef[index].childRef); 
		}
	    }    
        
	// Return
	return(counter);    
	}            */
		 	
    /*----------------------------------------------------------------------- */
    /*                                                                        */
    /*                              OUTPUT METHODS                            */
    /*                                                                        */
    /*----------------------------------------------------------------------- */
  
    /* Nine output options:
    
    (1)  Output T-tree
    (2)  Output T-tree branch
    (3)  Output frequent sets (also GUI version)
    (4)  Output number of frequent sets
    (5)  Output number of frequent sets per T-tree branch
    (6)  T-tree statistics
    (7)  Output number of updates and nodes created
    (8)  Output T-tree storage 
    (9)  Output serialization array 
    (10) Output serialized T-tree array (with support vlaues)
    (11) Output serialized T-tree array (no support vlaues) 
    (12) Output serialized T-tree for level N */
    
    /* ---------------- */
    /* 1. OUTPUT T-TRRE */
    /* ---------------- */
    /** Commences process of outputting T-tree structure contents to screen. */	
    
    public void outputTtree() {
	int number = 1;
	
	// Loop
	
	for (int index=1; index < startTtreeRef.length; index++) {
	    if (startTtreeRef[index] !=null) {
	        System.out.print("[" + number + "] {" + index);
	        System.out.println("} = " + startTtreeRef[index].support);
	        outputTtree(new Integer(number).toString(),
			new Integer(index).toString(),
			startTtreeRef[index].childRef);
		number++;
		}
	    }    
	}
	
    /** Continue process of outputting T-tree. <P> Operates in a recursive 
    manner.
    @param number the ID number of a particular node.
    @param itemSetSofar the label for a T-treenode as generated sofar.
    @param linkRef the reference to the current array lavel in the T-tree. */
    
    private void outputTtree(String number, String itemSetSofar,
    				TtreeNode[] linkRef) {
	// Set output local variables.
	int num=1;
	number = number + ".";
	itemSetSofar = itemSetSofar + " ";
	
	// Check for empty branch/sub-branch.
	if (linkRef == null) return;
	
	// Loop through current level of branch/sub-branch.
	for (int index=1;index<linkRef.length;index++) {
	    if (linkRef[index] != null) {
	        System.out.print("[" + number + num + "] {" + itemSetSofar +
	    		index);
	        System.out.println("} = " + linkRef[index].support);
	        String newitemSet = itemSetSofar + 
						new Integer(index).toString();
	        outputTtree(number + num,newitemSet,linkRef[index].childRef); 
	        num++;
		}
	    }    
	}	

    /* ----------------------- */
    /* 2. OUTPUT T-TREE BRANCH */
    /* ----------------------- */
    /** Commences process of outputting contents of a given T-tree branch to
    screen.
    @param linkRef the reference to the start of the branch*/	
    
    public void outputTtreeBranch(TtreeNode[] linkRef) {
	int number = 1;
	
	// Check for empty tree
	
	if (linkRef == null) return;
	
	// Loop
	
	for (int index=1; index<linkRef.length; index++) {
	    if (linkRef[index] !=null) {
	        System.out.print("[" + number + "] {" + index);
	        System.out.println("} = " + linkRef[index].support);
	        outputTtree(new Integer(number).toString(),
			new Integer(index).toString(),linkRef[index].childRef);
		number++;
		}
	    }    
	}
    
    /* ----------------------- */ 	
    /* 3. OUTPUT FREQUENT SETS */
    /* ----------------------- */
    /** Commences the process of outputting the frequent sets contained in 
    the T-tree. */	
    
    public void outputFrequentSets() {
	int number = 1;
	
	System.out.println("FREQUENT (LARGE) ITEM SETS:\n" +
	                    	"---------------------------");
	System.out.println("Format: [N] {I} = S, where N is a sequential " +
		"number, I is the item set and N the support.");
	
	// Loop
	
	for (int index=1; index <= numOneItemSets; index++) {
	    if (startTtreeRef[index] !=null) {
	        if (startTtreeRef[index].support >= minSupport) {
	            System.out.println("[" + number + "] {" + index + "} = " + 
		    				startTtreeRef[index].support);
	            number = outputFrequentSets(number+1,
		    			new Integer(index).toString(),
		    			index,startTtreeRef[index].childRef);
		    }
		}
	    }    
	
	// End
	
	System.out.println("\n");
	}

    /** Outputs T-tree frequent sets. <P> Operates in a recursive manner.
    @param number the number of frequent sets so far.
    @param itemSetSofar the label for a T-treenode as generated sofar.
    @param size the length/size of the current array lavel in the T-tree.
    @param linkRef the reference to the current array lavel in the T-tree. 
    @return the incremented (possibly) number the number of frequent sets so 
    far. */
    
    private int outputFrequentSets(int number, String itemSetSofar, int size,
    							TtreeNode[] linkRef) {
	
	// No more nodes
	
	if (linkRef == null) return(number);
	
	// Otherwise process
	
	itemSetSofar = itemSetSofar + " ";
	for (int index=1; index < size; index++) {
	    if (linkRef[index] != null) {
	        if (linkRef[index].support >= minSupport) {
	            System.out.println("[" + number + "] {" + itemSetSofar + 
		    		index +"} = " + linkRef[index].support);
	            String newitemSet = itemSetSofar + 
		    			new Integer(index).toString();
	            number = outputFrequentSets(number + 1,newitemSet,index,
		    			linkRef[index].childRef); 
	            }
		}
	    }    
	
	// Return
	
	return(number);
	}
    
    /* OUTPUT FREQUENT SETS (GUI VERSION) */
    /** Commences the process of outputting the frequent sets contained in 
    the T-tree to a rext area. 
    @param textArea the text area. */
                
    public void outputFrequentSets(JTextArea textArea) {
	int number = 1;
	
	textArea.append("FREQUENT (LARGE) ITEM SETS:\n" +
	                	"---------------------------\n");
	
	// Loop
	
	for (int index=1; index <= numOneItemSets; index++) {
	    if (startTtreeRef[index] !=null) {
	        if (startTtreeRef[index].support >= minSupport) {
	            textArea.append("[" + number + "] {" + index + "} = " + 
		    			startTtreeRef[index].support + "\n");
	            number = outputFrequentSets(textArea,number+1,
		    			new Integer(index).toString(),
		    			index,startTtreeRef[index].childRef);
		    }
		}
	    }    
	
	// End
	
	textArea.append("\n");
	}

    /** Outputs T-tree frequent sets. <P> Operates in a recursive manner.
    @param the text area.
    @param number the number of frequent sets so far.
    @param itemSetSofar the label for a T-treenode as generated sofar.
    @param size the length/size of the current array lavel in the T-tree.
    @param linkRef the reference to the current array lavel in the T-tree. 
    @return the incremented (possibly) number the number of frequent sets so 
    far. */    
    private int outputFrequentSets(JTextArea textArea, int number, 
    			String itemSetSofar, int size, TtreeNode[] linkRef) {
	
	// No more nodes
	
	if (linkRef == null) return(number);
	
	// Otherwise process
	
	itemSetSofar = itemSetSofar + " ";
	for (int index=1; index < size; index++) {
	    if (linkRef[index] != null) {
	        if (linkRef[index].support >= minSupport) {
	            textArea.append("[" + number + "] {" + itemSetSofar + 
		    		index +"} = " + linkRef[index].support + "\n");
	            String newitemSet = itemSetSofar + 
		    			new Integer(index).toString();
	            number = outputFrequentSets(textArea,number + 1,newitemSet,
		    			index,linkRef[index].childRef); 
	            }
		}
	    }    
	
	// Return
	
	return(number);
	}
	
    /* ------------------------------ */
    /* 4. OUTPUT NUMBER FREQUENT SETS */
    /* ------------------------------ */
    /** Commences the process of counting and outputing number of supported 
    nodes in the T-tree.<P> A supported set is assumed to be a non null node in
    the T-tree. */

    public void outputNumFreqSets() {
	
	// If emtpy tree (i.e. no supported sets) do nothing
	if (startTtreeRef== null) System.out.println("Number of frequent " +
					"sets = 0");
	// Otherwise count and output
	else System.out.println("Number of frequent sets = " + 
					countNumFreqSets());
	}
    
    /* COUNT NUMBER OF FRQUENT SETS */
    /** Commences process of counting the number of frequent (large/supported
    sets conayoned in the T-tree. */
    
    protected int countNumFreqSets() {
        // If emtpy tree return 0
	if (startTtreeRef ==  null) return(0);
	
	// Otherwise loop through T-tree starting with top level
	int num=0;
	for (int index=1; index <= numOneItemSets; index++) {
	    // Check for null valued top level Ttree node.
	    if (startTtreeRef[index] !=null) {
	        if (startTtreeRef[index].support >= minSupport) 
			num = countNumFreqSets(index,
	    				startTtreeRef[index].childRef,num+1);
		}
	    }    
	
	// Return
	return(num);
	}
	
    /** Counts the  number of supported nodes in a sub branch of the T-tree.
    @param size the length/size of the current array lavel in the T-tree.
    @param linkRef the reference to the current array lavel in the T-tree.
    @param num the number of frequent serts sofar. */

    protected int countNumFreqSets(int size, TtreeNode[] linkRef, int num) {
	
	if (linkRef == null) return(num);
	
	for (int index=1; index < size; index++) {
	    if (linkRef[index] != null) {
	        if (linkRef[index].support >= minSupport) 
	            			num = countNumFreqSets(index,
					linkRef[index].childRef,num+1);
		}
	    }
	
	// Return
	
	return(num);
	}
    
    /* --------------------------------------------------- */
    /* 5. OUTPUT NUMBER OF FREQUENT SETS PER T-TREE BRANCH */
    /* --------------------------------------------------- */
    /** Outputs the number of supported sets per T-tree branch descending from 
    the top-level of the tree. <P> Used for diagnostic purposes. */
    
    public void outputNumFreqSetsPerBranch() { 
	
	System.out.println("Number of frequent sets per branch");

	
	for (int index=1; index <= numOneItemSets; index++) {
	    if (startTtreeRef[index] !=null) {
	        System.out.println("(" + index + ")" + countNumFreqSets(index,
					startTtreeRef[index].childRef,1));
		}	   
	    }
	}
	
    /* --------------------------- */
    /* 6. OUTPUT T-TREE STATISTICS */
    /* --------------------------- */
    /** Commences the process of outputting T-tree statistics (for diagnostic
    purposes): (a) Storage, (b) Number of nodes on P-tree, (c) number of
    partial support increments (updates) and (d) generation time. */
    
    public void outputTtreeStats() {
        System.out.println("T-TREE STATISTICS\n-----------------");	
	System.out.println(calculateStorage() + " (Bytes) storage");
	System.out.println(TtreeNode.getNumberOfNodes() + " nodess");
	System.out.println(countNumFreqSets() + " frequent sets");
	System.out.println(numUpdates + " support value increments");
	System.out.println(duration);
	}
	
    /** Commences the process of outputting T-tree statistics:GUI version.
    @param textArea the text area. */
    
    public void outputTtreeStats(JTextArea textArea) {
        textArea.append("T-TREE STATISTICS\n-----------------\n");	
	textArea.append(calculateStorage() + " (Bytes) storage\n");
	textArea.append(TtreeNode.getNumberOfNodes() + " nodess\n");
	textArea.append(countNumFreqSets() + " frequent sets\n");
	textArea.append(numUpdates + " support value increments\n");
	textArea.append(duration + "\n");
	}
	
    /* --------------------------- */
    /* 7. OUTPUT NUMBER OF UPDATES */
    /* --------------------------- */
    /** Commences the process of determining and outputting the storage
    requirements (in bytes) for the T-tree */
    /** Outputs the number of update and number of nodes created during the
    generation of the T-tree (the later is not the same as the number of 
    supported nodes). */
    
    public void outputNumUpdates() {
	System.out.println("Number of Nodes created = " + 
			TtreeNode.getNumberOfNodes());
	System.out.println("Number of Updates       = " + numUpdates);
	} 
		
    /* ----------------- */
    /* 8. OUTPUT STORAGE */
    /* ----------------- */
    /** Commences the process of determining and outputting the storage
    requirements (in bytes) for the T-tree. <P> Example: Given ---
    <PRE>
        	{1,2,3} 
    		{1,2,3}
		{1,2,3}
    		{1,2,3}
		{1,2,3}
    </PRE>
    This will produce a T-tree as shown below:	
    <PRE>
    +---+---+---+---+ 		
    | 0 | 1 | 2 | 3 |	
    +---+---+---+---+
          |   |   |
	  |   |   +-----------+
	  |   |               |
	  |   +---+         +---+---+---+    
          |       |         | 0 | 1 | 2 |
	( 5 )   +---+---+   +---+---+---+
	(nul)   | 0 | 1 |         |   |
	        +---+---+         |   +----+
		      |           |        |
		      |           |      +---+---+
		    ( 5 )         |      | 0 + 1 |
		    (nul)       ( 5 )    +---+---+
	                        (nul)          |
				               |
					     ( 5 )
					     (nul)    
    </PRE>					     
    0 elements require 4 bytes of storage, null nodes (not shown above) 4 bytes 
    of storage, others 12 bytes of storage.			*/
    
    public void outputStorage() {
	
	// If emtpy tree (i.e. no supported serts) do nothing
	if (startTtreeRef ==  null) return;
		
	/* Otherwise calculate stoarge */
	System.out.println("T-tree Storage          = " + calculateStorage() + 
			" (Bytes)");
	} 	
   
    /* CALCULATE STORAGE */
    /** Commences process of claculating storage requirements for  T-tree. */
    
    protected int calculateStorage() {
        // If emtpy tree (i.e. no supported serts) return 0
	if (startTtreeRef ==  null) return(0);
		
	/* Step through top level */	
	int storage = 4;	// For element 0
	for (int index=1; index <= numOneItemSets; index++) {
	    if (startTtreeRef[index] !=null) storage = storage + 12 + 
	    		calculateStorage(0,startTtreeRef[index].childRef);
	    else storage = storage+4;
	    }
	// Return
	return(storage);
	}
        
    /** Calculate storage requirements for a sub-branch of the T-tree.
    @param localStorage the storage as calculated sofar (set to 0 at start).
    @param linkRef the reference to the current sub-branch of the T-tree. */
    
    private int calculateStorage(int localStorage, TtreeNode[] linkRef) {
	
	if (linkRef == null) return(0);
	
	for (int index=1; index < linkRef.length; index++) {
	    if (linkRef[index] !=null) localStorage = localStorage + 12 + 
	    		calculateStorage(0,linkRef[index].childRef);
	    else localStorage = localStorage + 4;
	    }   
         
	 /* Return */
	 
	 return(localStorage+4);	// For element 0
	 }
    }
    
    

