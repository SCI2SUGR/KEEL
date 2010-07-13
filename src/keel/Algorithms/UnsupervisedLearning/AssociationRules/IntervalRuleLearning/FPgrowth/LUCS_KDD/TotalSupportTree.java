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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.FPgrowth.LUCS_KDD;

/* ------------------------------------------------------------------------- */
/*                                                                           */
/*                             TOTAL SUPPORT TREE                            *//*                                                                           */
/*                                Frans Coenen                               */
/*                                                                           */
/*                               10 January 2003                             */
/*  (Revised 23/1/3003, 8/2/2003, 18/3/2003, 3/3/2003, 7/4/2004, 19/1/2005,  */
/*				  3/2/2006)                                  */
/*                                                                           */
/*                       Department of Computer Science                      */
/*                         The University of Liverpool                       */
/*                                                                           */ 
/* ------------------------------------------------------------------------- */

/* Structure:

AssocRuleMining
      |
      +-- TotalSupportTree	 */ 

/* Java packages */
import java.util.HashSet;
import java.util.Hashtable;

import keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.FPgrowth.myDataset;

/**
* <p>
* @author Modified by Nicolò Flugy Papè (Politecnico di Milano) 24/03/2009
* @version 1.1
* @since JDK1.5
* </p>
*/

public class TotalSupportTree extends AssocRuleMining {
	/**
	* <p>
	* Methods concerned with the generation, processing and manipulation of T-tree data
	* storage structures used to hold the total support counts for large itemsets
	* </p>
	*/

    /* ------ FIELDS ------ */
	
    // Data structures    
    /** The reference to start of t-tree. */
    protected TtreeNode[] startTtreeRef;
    
    private HashSet<Integer> covTIDs;

    // Diagnostics 
    /** The number of updates required to generate the T-tree. */
    protected long numUpdates   = 0l;
    
    /* ------ CONSTRUCTORS ------ */

    /** Constructor to process dataset and parameters.
     * @param ds The instance of the dataset for dealing with its records
     * @param sup The user-specified minimum support for the mined association rules
     * @param conf The user-specified minimum confidence for the mined association rules */
    
    public TotalSupportTree(myDataset ds, double sup, double conf) {
	super(ds, sup, conf);
	covTIDs = new HashSet<Integer>();
	}

    /* ------ METHODS ------ */

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
    
    public void addToTtree(short[] itemSet, int support) {
        // Determine index of last elemnt in itemSet.
	int endIndex = itemSet.length-1;
	
	// Add itemSet to T-tree.
        startTtreeRef = addToTtree(startTtreeRef,numOneItemSets+1,
			endIndex,itemSet,support);
	}
		
    /* ADD TO T-TREE */
    
    /** Inserts a node into a T-tree. <P> Recursive procedure.
    @param linkRef the reference to the current array in Ttree.
    @param size the size of the current array in T-tree.
    @param endIndex the index of the last element/attribute in the itemset, 
    which is also used as a level counter.	
    @param itemSet the given itemset.
    @param support the support value associated with the given itemset. 
    @return the reference to the revised sub-branch of t-tree. */
    
    protected TtreeNode[] addToTtree(TtreeNode[] linkRef, int size, int endIndex,
    				short[] itemSet, int support) {
	// If no array describing current level in the T-tree or T-tree
	// sub-branch create one with "null" nodes.	
	if (linkRef == null) {
	    linkRef = new TtreeNode[size];
	    for(int index=1;index<linkRef.length;index++) 
			linkRef[index] = null;
	    }
	
	// If null node at index of array describing current level in T-tree 
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
	}

    /*---------------------------------------------------------------------- */
    /*                                                                       */
    /*                        T-TREE SEARCH METHODS                          */
    /*                                                                       */
    /*---------------------------------------------------------------------- */

    /* GET SUPPORT FOT ITEM SET IN T-TREE */

    /** Commences process for finding the support value for the given item set
    in the T-tree (which is know to exist in the T-tree). <P> Used when
    generating Association Rules (ARs). Note that itemsets are stored in
    reverse order in the T-tree therefore the given itemset must be processed
    in reverse.
    @param itemSet the given itemset.
    @return returns the support value (0 if not found). */

    protected int getSupportForItemSetInTtree(short[] itemSet) {
	int endInd = itemSet.length-1;

    	// Last element of itemset in Ttree (Note: Ttree itemsets stored in
	// reverse)
  	if (startTtreeRef[itemSet[endInd]] != null) {
	    // If "current index" is 0, then this is the last element (i.e the
	    // input is a 1 itemset)  and therefore item set found
	    if (endInd == 0) return(startTtreeRef[itemSet[0]].support);
	    // Otherwise continue down branch
	    else {
	    	TtreeNode[] tempRef = startTtreeRef[itemSet[endInd]].childRef;
	        if (tempRef != null) return(getSupForIsetInTtree2(itemSet,
							   endInd-1,tempRef));
	    	// No further branch therefore rerurn 0
		else return(0);
		}
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

    private int getSupForIsetInTtree2(short[] itemSet, int index,
    							TtreeNode[] linkRef) {
        // Element at "index" in item set exists in Ttree
  	if (linkRef[itemSet[index]] != null) {
  	    // If "current index" is 0, then this is the last element of the
	    // item set and therefore item set found
	    if (index == 0) return(linkRef[itemSet[0]].support);
	    // Otherwise continue provided there is a child branch to follow
	    else if (linkRef[itemSet[index]].childRef != null)
	    		          return(getSupForIsetInTtree2(itemSet,index-1,
	    		                    linkRef[itemSet[index]].childRef));
	    else return(0);
	    }	
	// Item set not in Ttree therefore return 0
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

    public void generateARs() {
	// Command line interface output
	//System.out.println("GENERATE ARs:\n-------------");

	// Set rule data structure to null
	startRulelist = null;

	// Generate
	generateARs2();
	}

    /** Loops through top level of T-tree as part of the AR generation
    process. */

    protected void generateARs2() {
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
	}

    /* GENERATE ASSOCIATION RULES */

    /** Continues process of generating association rules from a T-tree by
    recursively looping through T-tree level by level.
    @param itemSetSofar the label for a T-tree node as generated sofar.
    @param size the length/size of the current array lavel in the T-tree.
    @param linkRef the reference to the current array level in the T-tree. */

    protected void generateARs(short[] itemSetSofar, int size,
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
	}

    /* GENERATE ASSOCIATION RULES */

    /** Generates all association rules for a given large item set found in a
    T-tree structure. <P> Called from <TT>generateARs</TT> method.
    @param itemSet the given large itemset.
    @param support the associated support value for the given large itemset. */

    private void generateARsFromItemset(short[] itemSet, double support) {
    	double confidenceForAR, supportForAntecedent;
    	short[] antecedent, consequent;
    	boolean atLeastOneGeneratedRule = false;
    	
    	for(int i=0; i < itemSet.length; i++) {
    		consequent = new short[1];
    		consequent[0] = itemSet[i];
    		
    		antecedent = complement(consequent, itemSet);
    		
    		// If complement is not empty generate rule
    	    if (antecedent != null) {
    	    	supportForAntecedent = (double)getSupportForItemSetInTtree(antecedent);
    	    	confidenceForAR = ((double) support/supportForAntecedent) * 100.0;
    	    	
    	    	if (confidenceForAR >= confidence) {
    	    		insertRuleintoRulelist(antecedent, consequent, confidenceForAR, support, supportForAntecedent);
    	    		if (! atLeastOneGeneratedRule) atLeastOneGeneratedRule = true;
    	    	}
    	    }
    	}
    	
    	if (atLeastOneGeneratedRule) covTIDs.addAll( getCoveredRecords( reconvertItemSet(itemSet) ) );
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

    /*----------------------------------------------------------------------- */
    /*                                                                        */
    /*                              OUTPUT METHODS                            */
    /*                                                                        */
    /*----------------------------------------------------------------------- */

    /* ----------------------- */
    /*   OUTPUT FREQUENT SETS  */
    /* ----------------------- */
    /** Commences the process of outputting the frequent sets contained in
    the T-tree. */

    public void outputFrequentSets() {
	int number = 1;

	System.out.println("FREQUENT (LARGE) ITEM SETS:\n" +
	                    	"---------------------------");
	System.out.println("Format: [N] {I} = S, where N is a sequential " +
		"number, I is the item set and S the support.");

	// Loop

	for (short index=1; index <= numOneItemSets; index++) {
	    if (startTtreeRef[index] !=null) {
	        if (startTtreeRef[index].support >= minSupport) {
	            String itemSetSofar = 
		                   new Short(reconvertItem(index)).toString();
	            System.out.println("[" + number + "] {" + itemSetSofar + 
		    		       "} = " + startTtreeRef[index].support);
	            number = outputFrequentSets(number+1,itemSetSofar,
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
    @param size the length/size of the current array level in the T-tree.
    @param linkRef the reference to the current array level in the T-tree.
    @return the incremented (possibly) number the number of frequent sets so
    far. */

    private int outputFrequentSets(int number, String itemSetSofar, int size,
    							TtreeNode[] linkRef) {

	// No more nodes

	if (linkRef == null) return(number);

	// Otherwise process

	itemSetSofar = itemSetSofar + " ";
	for (short index=1; index < size; index++) {
	    if (linkRef[index] != null) {
	        if (linkRef[index].support >= minSupport) {
	            String newItemSet = itemSetSofar + (reconvertItem(index));
		    System.out.println("[" + number + "] {" + newItemSet +
		                             "} = " + linkRef[index].support);
	            number = outputFrequentSets(number + 1,newItemSet,index,
		    			             linkRef[index].childRef);
	            }
		}
	    }

	// Return

	return(number);
	}
	    
    /* COUNT NUMBER OF FREQUENT SETS */
    /** Commences process of counting the number of frequent (large/supported
    sets contained in the T-tree. */
    
    protected int countNumFreqSets() {
        // If empty tree return 0
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
	
    /** Counts the number of supported nodes in a sub branch of the T-tree.
    @param size the length/size of the current array level in the T-tree.
    @param linkRef the reference to the current array level in the T-tree.
    @param num the number of frequent sets sofar. */

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
    
    /* ------------------------------ */
    /*   GET NUMBER OF FREQUENT SETS  */
    /* ------------------------------ */
    /** Commences the process of counting and returning number of supported
    nodes in the T-tree.<P> A supported set is assumed to be a non null node in
    the T-tree.
    @return the number of supported nodes in the T-tree. */

    public int getNumFreqSets() {
    	// If empty tree (i.e. no supported sets) do nothing
    	if (startTtreeRef == null) return 0;
    	else
    		// Otherwise count and return
    		return ( countNumFreqSets() );
	}
    
    protected HashSet<Integer> getCoveredRecords(short[] itemset) {
  	  Hashtable<Integer, HashSet<Integer>> tid_list = this.dataset.getTIDList();	  
  	  HashSet<Integer> toIntersect = new HashSet<Integer>( tid_list.get((int)itemset[0]) );
  	  
  	  for (int i=1; i < itemset.length; i++) {
  		  toIntersect.retainAll( tid_list.get((int)itemset[i]) );
  		  if ( toIntersect.isEmpty() ) break;
  	  }
  	  
  	  return toIntersect;	  
    }
    
    /** Retrieves all the records which are covered by the association rules
     @return a set of TIDs representing the covered records. */
    public HashSet<Integer> getCoveredRecords() {
    	return covTIDs;
    }
    
}

