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

/* -------------------------------------------------------------------------- */
/*                                                                            */
/*                          FP   T R E E   C L A S S                          *//*                                                                            */
/*                                 Frans Coenen                               */
/*                                                                            */
/*                                 25 May 2001                                */
/*                    (Revised 5 February 2003, 3 February 2006)              */ 
/*                                                                            */
/*                        Department of Computer Science                      */
/*                          The University of Liverpool                       */
/*                                                                            */
/* -------------------------------------------------------------------------- */

/* Structure:

AssocRuleMining
      |
      +-- TotalSupportTree	 
                  |
		  +-- FPtree		*/

/* Java packages */
import keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.FPgrowth.myDataset;

/**
* <p>
* @author Modified by Nicolò Flugy Papè (Politecnico di Milano) 24/03/2009
* @version 1.1
* @since JDK1.5
* </p>
*/
                             
public class FPtree extends TotalSupportTree {
	/**
	* <p>
	* Implementation of Han's FP-growth ARM algorithm
	* </p>
	*/
    
    /* ------ FIELDS ------ */
    
    /** FP-tree node structure comprising a <TT>FPgrowthItemPrefixSubtreeNode</TT> in 
    which to store counts and a reference to a child branch. */
    
    protected class FPtreeNode {
        /** The FP tree node. */
        private FPgrowthItemPrefixSubtreeNode node = null;
	/** The reference to the child branch (levels in FP-tree branches are
	stored as a arrays of <TT>FPtreeNode</TT> structures. */ 
        private FPtreeNode[] childRefs = null;
        
	/** Default constructor. */

	protected FPtreeNode() {
	    }  
	    
	/** Single argument constructor. 
	@param newNode the reference to a new node to be included in the
	FP-tree.*/
	
	protected FPtreeNode(FPgrowthItemPrefixSubtreeNode newNode) {
	    node = newNode;
	    }
	}
    
    /** Prefix subtree structure. <P> A set enumeration tree in which to store
    itemsets together with support values. */
    
    private class FPgrowthItemPrefixSubtreeNode {
        /** The attribute identifier. */
        private short itemName;
	/** The support count. */
	private int itemCount;
	/** The backward link to the parent node in FP tree. */
	private FPgrowthItemPrefixSubtreeNode parentRef = null;
	/** The forward link to the next node in a linked list of nodes with
	same attribute identifier starting with an element in the header table
	(array). */
	private FPgrowthItemPrefixSubtreeNode nodeLink = null;
	
	/** Default constructor. */
	
	private FPgrowthItemPrefixSubtreeNode() {
	    }
	
	/** Three argument constructor. 
	@param name the itemset identifier. 
	@param support the support value for the itemset.
	@param backRef the backward link to the parent node. */
	
	private FPgrowthItemPrefixSubtreeNode(short name, int support, 
			FPgrowthItemPrefixSubtreeNode backRef) {
	    itemName = name;
	    itemCount = support;
	    parentRef = backRef;
	    }
	}
    
    /** Header table. <P> Array of these structures used to link into FP-tree.
    All FP-tree nodes with the same identifier are linked together starting
    from a node in a header table (made up of <TT>HeaderTasble</TT> structures).
    It is this "cross" linking that gives the FP-tree its most significant
    advantage. */
    
    protected class FPgrowthHeaderTable {
        /** The 1-itemset (attribute) identifier. */
	protected short itemName;
	/** The forward link to the next node in the link list of nodes. */
        protected FPgrowthItemPrefixSubtreeNode nodeLink = null;
        
	// Constructors

	protected FPgrowthHeaderTable (short columnNum) {
	    itemName = columnNum;
	    }  
        }
	
    /** Structure in which to store ancestor itemSets, i.e. nodes in an FP-tree
    that preceed the nodes identified by following a trail of links from a
    particular item in the header table. */
    
    private class FPgrowthSupportedSets {
        /** The itemSet label. */
        private short[] itemSet = null;
	/** The associated support value for the given itemset. */
        private int support;
	/** The reference to the next node in a linked list. */
	private FPgrowthSupportedSets nodeLink = null;
        
	/** Three argument constructor.
	@param newitemSet the given itemSet label.
	@param newSupport the associated support value for the given itemset. 
	@param newNodeLink the reference to the next node in a linked list. */
	
	private FPgrowthSupportedSets(short[] newitemSet, int newSupport, 
			FPgrowthSupportedSets newNodeLink) {
	    itemSet = newitemSet;
            support = newSupport;
	    nodeLink = newNodeLink;
	    } 
	} 
    
    /** Structure in which to store counts. */
    
    private class FPgrowthColumnCounts {
        /** The column/attribute ID number. */
        private short columnNum;
	/** The associated support value. */
        private int support=0;
        
	/** One argument constructor.
	@param column the column/attribute ID number. */

	private FPgrowthColumnCounts(int column) {
	    columnNum = (short) column;
	    }  
        
	/** Two argument constructor.
	@param column the column/attribute ID number. 
	@param sup the associatec support value. */
	
	private FPgrowthColumnCounts(int column, int sup) {
	    columnNum = (short) column;
	    support = sup;
	    }  
	}   
	
    // Data structures
    
    /** Start reference for FP-tree. */
    protected FPtreeNode rootNode = null;
    /** Start reference for header table. */
    protected FPgrowthHeaderTable[] headerTable; 
    /** Start reference for supportedSets linked list (temporary storage 
    only).*/
    private static FPgrowthSupportedSets startTempSets = null;
    
    // Other fields 
    
    /** Temporary storage for an index into an array of FP-tree nodes. </P>
    Used when reassigning child reference arrays. */
    private int tempIndex  = 0;    	
    
    /* ------ CONSTRUCTORS ------ */
    
    /** Constructor to process dataset and parameters.
     * @param ds The instance of the dataset for dealing with its records
     * @param sup The user-specified minimum support for the mined association rules
     * @param conf The user-specified minimum confidence for the mined association rules */
    
    public FPtree(myDataset ds, double sup, double conf) {
	super(ds, sup, conf);
	
	// Initialise root node
	rootNode = new FPtreeNode();
	
	// Create header table	
	headerTable = new FPgrowthHeaderTable[numOneItemSets+1];
	
	// Populate header table	
	for (int index=1;index<headerTable.length;index++) {
	    headerTable[index] = new FPgrowthHeaderTable((short) index);
	    }
	}	
    
    /* ------ METHODS ------ */
    
    /*-------------------------------------------------------------------*/
    /*                                                                   */
    /*                             GENERATE FP-TREE                      */
    /*                                                                   */
    /*-------------------------------------------------------------------*/  
    
    /* CREATE FP-TREE */   
    /** Top level method to commence the construction of the FP-Tree. */
    
    public void createFPtree() {      
	//System.out.println("GENERATING FP-TREE\n------------------");
	
	// Create header table	
	headerTable = new FPgrowthHeaderTable[numOneItemSets+1];
	
	// Populate header table	
	for (int index=1;index<headerTable.length;index++) {
	    headerTable[index] = new FPgrowthHeaderTable((short) index);
	    }
	    
	// Process datatable, loop through data table (stored in data array)
	// For each entry add the entry to the FP-tree.

	for (int index=0;index<dataArray.length;index++) {
	    // Non null record (if initial data set has been reordered and
	    // pruned some records may be empty
	    if (dataArray[index] != null) 
	    	addToFPtree(rootNode,0,dataArray[index],1,headerTable);
	    }         	                                                  
	}

    /* ADD TO FP-TREE */   
    /** Searches through current list of child refs looking for given item set.
    <P> If reference for current itemset found increments support count and 
    proceed down branch, otherwise adds to current level. 
    @param ref the current location in the FP-tree (<TT>rootNode</TT> at start).
    @param place the current index in the given itemset.
    @param itemSet the given itemset.
    @param support the associated support value for the given itemset.
    @param headerRef the link to the appropriate place in the header table. */
    
    private void addToFPtree(FPtreeNode ref, int place, short[] itemSet, 
    				int support, FPgrowthHeaderTable[] headerRef) {  
	if (place < itemSet.length) {
	    if (!addToFPtree1(ref,place,itemSet,support,headerRef)) 
	    		addToFPtree2(ref,place,itemSet,support,headerRef);
	    }
	}
	
    /* ADD TO FP TREE 1 */    
    /** Searches through existing branch and if itemset found updates the 
    support count and returns true, otherwise return false. 
    @param ref the current FP-tree node reference.
    @param place the current index in the given itemset.
    @param itemSet the given itemset.
    @param support the associated support value for the given itemset.
    @param headerRef the link to the appropriate place in the header table. 
    @return true if given itemset exists in FP-tree, and false otherwise. */
    
    private boolean addToFPtree1(FPtreeNode ref, int place, short[] itemSet,
    			int support, FPgrowthHeaderTable[] headerRef) {	
			
	// Loop	
	if (ref.childRefs != null) {
	    for (int index=0;index<ref.childRefs.length;index++) {
	        // If item is already in list of child refs
	        // increment count and proceed down branch.
	        if (itemSet[place] == ref.childRefs[index].node.itemName) {
	            ref.childRefs[index].node.itemCount =
		                 ref.childRefs[index].node.itemCount + support;
		    numUpdates++;
		    addToFPtree(ref.childRefs[index],place+1,itemSet,support,
		    		headerRef);
		    return(true);
		    }
	        // Child refs ordered lexicographically so break when passed
		// point where item should be
		if (itemSet[place] < ref.childRefs[index].node.itemName) 
					return(false);
		}
	    }	
        
	// Default
	
	return(false);
	}

    /* ADD TO FP TREE 2 */
    
    /** Adds new node to FP-tree. <P> Adds first attribute in itemSet and then 
    rest of sequence. 
    @param ref the current FP-tree node reference.
    @param place the current index in the given itemset.
    @param itemSet the given itemset.
    @param support the associated support value for the given itemset.
    @param headerRef the link to the appropriate place in the header table. */
    
    private void addToFPtree2(FPtreeNode ref, int place, short[] itemSet,
    				int support, FPgrowthHeaderTable[] headerRef) {	
	
	// Create new Item Prefix Subtree Node
	FPgrowthItemPrefixSubtreeNode newPrefixNode = new 
	    		FPgrowthItemPrefixSubtreeNode(itemSet[place],support,ref.node);
	// Create new FP tree node incorporating new Item Prefix Subtree Node
	FPtreeNode newFPtreeNode = new FPtreeNode(newPrefixNode);
	// Add link from header table
	addRefToFPgrowthHeaderTable(itemSet[place],newPrefixNode,headerRef);
	// Add into FP tree
	ref.childRefs = reallocFPtreeChildRefs(ref.childRefs,newFPtreeNode);
	// Proceed down branch with rest of itemSet
	addRestOfitemSet(ref.childRefs[tempIndex],newPrefixNode,place+1,itemSet,
				support,headerRef);
	}
    
    /* ADD REST OF ITEMSET */
    
    /** Continues adding attributes in current itemset to FP-tree. 
    @param ref the current FP-tree node reference.
    @param backRef the backwards link to the previous node.
    @param place the current index in the given itemset.
    @param itemSet the given itemset.
    @param support the associated support value for the given itemset.
    @param headerRef the link to the appropriate place in the header table. */
    
    private void addRestOfitemSet(FPtreeNode ref, FPgrowthItemPrefixSubtreeNode backRef, 
    				int place, short[] itemSet, int support, 
						FPgrowthHeaderTable[] headerRef) {
        
	// Process if more items in item set.
	if (place<itemSet.length) {
	    // Create new Item Prefix Subtree Node
	    FPgrowthItemPrefixSubtreeNode newPrefixNode = new
	    		FPgrowthItemPrefixSubtreeNode(itemSet[place],support,backRef);
	    // Create new FP tree node incorporating new Item Prefix Subtree 
	    // Node
	    FPtreeNode newFPtreeNode = new FPtreeNode(newPrefixNode);
	    // Add link from header table
	    addRefToFPgrowthHeaderTable(itemSet[place],newPrefixNode,headerRef);
	    ref.childRefs = reallocFPtreeChildRefs(ref.childRefs,newFPtreeNode);
	    // Add into FP tree
	    addRestOfitemSet(ref.childRefs[tempIndex],newPrefixNode,place+1,
	    					itemSet,support,headerRef);
	    }
	}
	    
    /* ADD REF TO HEADER TABLE */
    
    /** Adds reference to new FP-tree node to header table moving old reference 
    so that it becomes a link from the new FP-tree node.
    @param columnNumber the given attribute.
    @param newNode the newly created FP-tree node.
    @param headerRef the reference to the header table (array). */
    
    private void addRefToFPgrowthHeaderTable(short columnNumber, 
    		     FPgrowthItemPrefixSubtreeNode newNode, 
		     FPgrowthHeaderTable[] headerRef) {
        FPgrowthItemPrefixSubtreeNode tempRef;

	// Loop through header table
	for (int index=1;index<headerRef.length;index++) {
	    // Found right attribute in table?
	    if (columnNumber == headerRef[index].itemName) {
	        tempRef = headerRef[index].nodeLink;
		headerRef[index].nodeLink = newNode;
		newNode.nodeLink = tempRef;
		break;
		}
	    }   
        }

    /* ---------------------------------------------------------- */
    /*                                                            */
    /*                       FP-TREE MINING                       */
    /*                                                            */
    /* ---------------------------------------------------------- */
    
    /* Methodology:

    1) Step through header table from end to start (least common single 
    attribute to most common single attribute). For each item.
    a) Count support by following node links and add to linked list of 
       supported sets.
    b) Determine the "ancestor trails" connected to the nodes linked to the
       current item in the header table.
    c) Treat the list of ancestor itemSets as a new set of input data and 
       create a new header table based on the accumulated supported counts of 
       the single items in the ancestor itemSets 
    d) Prune the ancestor itemSets so as to remove unsupported items.
    e) Repeat (1) with local header table and list of pruned ancestor itemSets 
       as input */
       
    /* START MINING */
    
    /** Top level "FP-growth method" to mine the FP tree. */
    
    public void startMining() {
        
	//System.out.println("Mining FP-tree");
	 
    	startMining(headerTable,null);
	
	// Generate ARs
	generateARs();
	}
    
    /* START MINING */
    
    /** Commences process of mining the FP tree. <P> Commence with the bottom 
    of the header table and work upwards. Working upwards from the bottom of 
    the header table if there is a link to an FP tree node :
    <OL>
    <LI> Count the support.
    <LI> Build up itemSet sofar.
    <LI> Add to supported sets.
    <LI> Build a new FP tree: (i) create a new local root, (ii) create a 
	 new local header table and (iii) populate with ancestors.
    <LI> If new local FP tree is not empty repeat mining operation.
    </OL>
    Otherwise end. 
    @param tableRef the reference to the current location in the header table
    (commencing with the last item).
    @param itemSetSofar the label fot the current item sets as generated to
    date (null at start). */	
    
    private void startMining(FPgrowthHeaderTable[] tableRef, 
    						       short[] itemSetSofar) {
        int headerTableEnd = tableRef.length-1;
	FPgrowthColumnCounts[] countArray = null;
	FPgrowthHeaderTable[] localHeaderTable = null;
	FPtreeNode localRoot;
	int support;
	short[] newCodeSofar;
	
	// Loop through header table from end to start, item by item
	
        for (int index=headerTableEnd;index>=1;index--) {
	    // Check for null link
	    if (tableRef[index].nodeLink != null) {
	        // process trail of links from header table element
	        startMining(tableRef[index].nodeLink,tableRef[index].itemName,
						itemSetSofar);
		}
	    }
	}
	
    /** Commence process of mining FP tree with respect to a single element in
    the header table.
    @param nodeLink the firsty link from the header table pointing to an FP-tree
    node.
    @param itemName the label associated with the element of interest in the 
    header table.
    @param itemSetSofar the item set represented by the current FP-tree. */
    	
    protected void startMining(FPgrowthItemPrefixSubtreeNode nodeLink,	
     				short itemName, short[] itemSetSofar) {
	
    	// Count support for current item in header table and store a
	// T-tree data structure
	int support = genSupHeadTabItem(nodeLink); 
	short[] newCodeSofar = realloc2(itemSetSofar,itemName);
	addToTtree(newCodeSofar,support); 
	        
	// Collect ancestor itemSets and store in linked list structure 
	startTempSets=null;
	generateAncestorCodes(nodeLink); 
	
	// Process Ancestor itemSets
	if (startTempSets != null) {
	    // Count singles in linked list
	    FPgrowthColumnCounts[] countArray = countFPgrowthSingles(); 
	    // Create and populate local header table
	    FPgrowthHeaderTable[] localHeaderTable = 
	    				createLocalHeaderTable(countArray); 
	    if (localHeaderTable != null) {
		// Prune ancestor itemSets
		pruneAncestorCodes(countArray); 
	        // Create new local root for local FP tree
	        FPtreeNode localRoot = generateLocalFPtree(localHeaderTable);
		// Mine new FP tree
		startMining(localHeaderTable,newCodeSofar);
		}
	    }
	}
			
    /* ---------------------------------------------------------------------- */
    /*                                                                        */
    /*                     PROCESS CURRENT HEADER TABLE                       */
    /*                                                                        */
    /* ---------------------------------------------------------------------- */  
    
    /* GENERATE SUPPORT FOR HEADER TABLE SINGLE ITEM: */
    
    /** Counts support for single attributes in header table by following node 
    links. 
    @param nodeLink the start link from the header table. 
    @return the support valye for the item set indicated by the header table. */
    
    private int genSupHeadTabItem(FPgrowthItemPrefixSubtreeNode nodeLink) {
        int counter = 0;
	
	// Loop
	
        while(nodeLink != null) {
	    counter = counter+nodeLink.itemCount;
	    numUpdates++;
	    nodeLink = nodeLink.nodeLink;
	    }	
	
	// Return
	
	return(counter);
	}
	
    /* ---------------------------------------------------------------------- */
    /*                                                                        */
    /*                              ANCESTOR CODES                            */
    /*                                                                        */
    /* ---------------------------------------------------------------------- */  
    
    /* GENERATE ANCESTOR CODES */
    
    /** Generates ancestor itemSets are made up of the parent nodes of a given 
    node. This method collects such itemSets and stores them in a linked list 
    pointed at by startTempSets. 
    @param ref the reference to the current node in the prefix tree containing
    itemsets together with support values.*/
                 
    private void generateAncestorCodes(FPgrowthItemPrefixSubtreeNode ref) {
        short[] ancestorCode = null;
	int support;
	
	// Loop
	
        while(ref != null) {
	    support = ref.itemCount;
	    ancestorCode = getAncestorCode(ref.parentRef);
	    // Add to linked list with current support
	    if (ancestorCode != null) startTempSets = 
	    			new FPgrowthSupportedSets(ancestorCode,support,
								startTempSets);
	    // Next ref	
	    ref = ref.nodeLink;
	    }	
	}

    /* GET ANCESTOR CODE */
    
    /** Generate the ancestor itemSet from a given node. 
    @param ref the reference to the current node in the prefix tree containing
    itemsets together with support values. */
    	
    private short[] getAncestorCode(FPgrowthItemPrefixSubtreeNode ref) {
        short[] itemSet = null;
	
	if (ref == null) return(null);
	
	// Else process
	
	while (ref != null) {
	    itemSet = realloc2(itemSet,ref.itemName);
	    ref = ref.parentRef;
	    }
	
	// Return
	
	return(itemSet);
	}

    /* PRUNE ANCESTOR CODES */
    
    /** Removes elements in ancestor itemSets (pointed at by 
    <TT>startTempSets</TT>) which are not supported by referring to count 
    array (which contains all the current supported 1 itemsets). 
    @param countArray the array of <TT>FPgrowthColumnCounts</TT> structures 
    describing the single item sets (in terms of labels and associated 
    support), contained in a linked list of <TT>FPgrowthSupportedSets</TT>
    which in turn describe the ancestor nodes in an FP-tree that preceed the 
    nodes identified by following a trail of links from a particular item in 
    the header table.    */
    
    private void pruneAncestorCodes(FPgrowthColumnCounts[] countArray) {
	FPgrowthSupportedSets ref = startTempSets;
	
	// Loop through linked list of ancestor paths
	
	while(ref != null) { 
	    for(int index=0;index<ref.itemSet.length;index++) {
	        if (countArray[ref.itemSet[index]].support < minSupport)
		      ref.itemSet = removeElementN(ref.itemSet,index);
		}
	    ref = ref.nodeLink;
	    }
	}
				
    /* ---------------------------------------------------------------------- */
    /*                                                                        */
    /*      CREATE NEW HEADER TABLE FROM SINGLE ITEMS IN ANCESTOR CODES       */
    /*                                                                        */
    /* ---------------------------------------------------------------------- */  
    
    /* COUNT SINGLES */
    
    /** Counts frequent 1 item sets in ancestor itemSets linked list and place 
    into an array. 
    @return array of <TT>FPgrowthColumnCounts</TT> structures describing the
    single item sets (in terms of labels and associated support), contained in 
    a linked list of <TT>FPgrowthSupportedSets</TT> which in turn describe the 
    ancestor nodes in an FP-tree that preceed the nodes identified by following 
    a trail of links from a particular item in the header table. */
    
    private FPgrowthColumnCounts[] countFPgrowthSingles() {
        int index, place=0;
	FPgrowthSupportedSets nodeLink = startTempSets; // Start of linked list
        
	// Dimension array, assume all attributes present, then it will
	// be possible to index in to the array.
	
	FPgrowthColumnCounts[] countArray = new 
					FPgrowthColumnCounts[numOneItemSets+1];	
	
	// Initialise array
	
	for (index=1;index<numOneItemSets+1;index++) countArray[index] = 
				new FPgrowthColumnCounts(index);
	    
	// Loop through linked list of ancestor itemSets
	
	while (nodeLink != null) {
	    // Loop through itemSet 
	    for (index=0;index<nodeLink.itemSet.length;index++) {
		place = nodeLink.itemSet[index];
		countArray[place].support = countArray[place].support +
			nodeLink.support;
		numUpdates++;
		}
	    nodeLink = nodeLink.nodeLink;
	    }    
	               	                                                  
	// Return
	
	return(countArray);
	}

    /* CREATE LOCAL HEADER TABLE */
    
    /** Creates a local header table comprising those item that are supported 
    in the count array. 
    @param countArray the support for the 1 item sets. 
    @return a FPgrowth header table. */
    
    private FPgrowthHeaderTable[] 
    		createLocalHeaderTable(FPgrowthColumnCounts[] countArray) {
        int index;
	FPgrowthHeaderTable[] localHeaderTable;
	
	localHeaderTable = localHeadTabUnordered(countArray);
	
	// Order according single item support
	
        //orderLocalHeaderTable(localHeaderTable,countArray);
	                	                                                  
	// Return
	
	return(localHeaderTable);
	}		
    
    /* CREATE NEW LOCAL HEADER TABLE (UNORDERED) */
    
    /** Creatwx a new local header table, but unorderd. 
    @param countArray the csupport for the 1 item sets. 
    @return a FPgrpwth header table. */
    
    private FPgrowthHeaderTable[] 
    		localHeadTabUnordered(FPgrowthColumnCounts[] countArray) {
        int counter = 1;
	
	// Loop through array and count supported one item sets	 
	for (int index=1;index<countArray.length;index++) {
	    if (countArray[index].support >= minSupport) counter++;
	    }
	    
	// Build new Header Table array containing only supported items
	
	if (counter == 1) return(null);
	FPgrowthHeaderTable[] localHeaderTable = 
					new FPgrowthHeaderTable[counter];
	    
	// Populate header table
	
	int place=1;
	for (int index=1;index<countArray.length;index++) {
	    if (countArray[index].support >= minSupport) {
	        localHeaderTable[place] = new 
		    FPgrowthHeaderTable((short) countArray[index].columnNum);    
	        place++;
	        }
	    }    
        
	// Return
	
	return(localHeaderTable);
	}
    
    /* ---------------------------------------------------------------------- */
    /*                                                                        */
    /*                         GENERATE NEW FP-TREE                           */
    /*                                                                        */
    /* ---------------------------------------------------------------------- */  
    	
    /* GENERATE LOCAL FP-tree */
    
    /** Generates a local FP tree 
    @param tableRef reference to start of header table containing links to
    an FP-tree produced during the FP-tree generation process.
    @rerurn reference to the start of the generated FP-tree*/
    
    private FPtreeNode generateLocalFPtree(FPgrowthHeaderTable[] tableRef) {
         FPgrowthSupportedSets ref = startTempSets;
	 FPtreeNode localRoot = new FPtreeNode(); 
	 
	 // Loop

        while(ref != null) { 	 
	    // Add to conditional FP tree   
	    if (ref.itemSet != null) addToFPtree(localRoot,0,ref.itemSet,
	    			ref.support,tableRef);  
       	    ref = ref.nodeLink;
	    }
	
	// Return

	return(localRoot);
	} 
	
    /* ---------------------------------------------------------- */
    /*                                                            */
    /*                     FP-TREE UTILITIES                      */
    /*                                                            */
    /* ---------------------------------------------------------- */
    	
    /* REALLOC 1 FP-TREE */
    
    /** Resizes the given array of FP-tree nodes so that its length is 
    increased by one element and new element inserted.
    @param oldArray the given array of FP-tree nodes.
    @param newNode the given node to be added to the FP-tree
    @return The revised array of FP-tree nodes. */
    
    private FPtreeNode[] reallocFPtreeChildRefs(FPtreeNode[] oldArray, 
    			FPtreeNode newNode) {
            
	// No old array
	
	if (oldArray == null) {
	    FPtreeNode[] newArray = {newNode};
	    tempIndex = 0;
	    return(newArray);
	    }
	
	// Otherwise create new array with length one greater than old array
	
	int oldArrayLength = oldArray.length;
	FPtreeNode[] newArray = new FPtreeNode[oldArrayLength+1];
	
	// Insert new node in correct lexicographic order.
		
	for (int index1=0;index1 < oldArrayLength;index1++) {
	    if (newNode.node.itemName < oldArray[index1].node.itemName) {
		newArray[index1] = newNode;
		for (int index2=index1;index2<oldArrayLength;index2++)
		    newArray[index2+1] = oldArray[index2];
		tempIndex = index1;
		return(newArray);
		}
	    newArray[index1] = oldArray[index1];
	    }
	
	// Default
	
	newArray[oldArrayLength] = newNode;
	tempIndex = oldArrayLength;
	return(newArray);
	}

}

