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
/*                      ASSOCIATION RULE DATA MINING                          */
/*                                                                            */
/*                            Frans Coenen                                    */
/*                                                                            */
/*                        Wednesday 9 January 2003                            */
/*   (revised 21/1/2003, 14/2/2003, 2/5/2003, 2/7/2003, 3/2/2004, 8/5/2004,   */
/*                           1/2/2005, 3/2/2005)                              */
/*                                                                            */
/*                    Department of Computer Science                          */
/*                     The University of Liverpool                            */
/*                                                                            */ 
/* -------------------------------------------------------------------------- */



/* To compile: javac.exe AssocRuleMining.java */

// Java packages
import java.util.ArrayList;

import keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.FPgrowth.AssociationRule;
import keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.FPgrowth.myDataset;

/**
* <p>
* @author Modified by Nicolò Flugy Papè (Politecnico di Milano) 24/03/2009
* @version 1.1
* @since JDK1.5
* </p>
*/

public class AssocRuleMining {
	/**
	* <p>
	* Set of utilities to support various Association Rule Mining (ARM)
	* </p>
	*/

    /* ------ FIELDS ------ */

    // Inner class for storing linked list of ARs or CARs as appropriate.

    protected class RuleNode {
    	/** Antecedent of AR. */
	protected short[] antecedent;
	/** Consequent of AR. */
	protected short[] consequent;
	/** The confidence value associate with the rule represented by this
	node. */
	double confidenceForRule=0.0;
	/** The support value associate with the rule represented by this
	node. */
	double supportForRule=0.0;
	/** The support value associate with the antecedent of this
	rule. */
	double supportForAntecedent=0.0;
	/** Link to next node */
	RuleNode next = null;
	
	/** Three argument constructor
	@param ante the antecedent (LHS) of the AR.
    	@param cons the consequent (RHS) of the AR.
    	@param confValue the associated confidence value. 
    	@param ruleSupValue the associated support value. 
    	@param antSupValue the associated antecedent support value. */
	
	protected RuleNode(short[] ante, short[]cons, double confValue, double ruleSupValue, double antSupValue) {
	    antecedent        = ante;
	    consequent        = cons;
	    confidenceForRule = confValue;
	    supportForRule = ruleSupValue;
	    supportForAntecedent = antSupValue;
	    }
	}
	
    // Data structures
	
    /** The reference to start of the rule list. */
    protected RuleNode startRulelist = null;	
    /** 2-D aray to hold input data from data file. Note that within the data
    array records are numbered from zero, thus rexcord one has index 0 etc. */
    protected short[][] dataArray = null;
    /** 2-D array used to renumber columns for input data in terms of
    frequency of single attributes (reordering will enhance performance
    for some ARM algorithms). */
    protected int[][] conversionArray   = null;
    /** 1-D array used to reconvert input data column numbers to their
    original numbering where the input data has been ordered to enhance
    computational efficiency. */
    protected short[] reconversionArray = null;
	
    // Command line arguments with default values and associated fields.
    
    /** Number of columns. */
    protected int     numCols    = 0;
    /** Number of rows. */
    protected int     numRows    = 0;
    /** % support. */
    protected double  support    = 0.0;
    /** Minimum support value in terms of number of rows. <P>Set when input
    data is read and the number of records is known,   */
    protected double  minSupport = 0.0;
    /** % confidence. */
    protected double  confidence = 0.0;
    /** The number of one itemsets (singletons). */
    protected int numOneItemSets = 0;
    
    protected myDataset dataset;

    // Flags

    /** Flag to indicate whether input data has been sorted or not. */
    protected boolean isOrderedFlag;
    /** Flag to indicate whether input data has been sorted and pruned or
    not. */
    protected boolean isPrunedFlag;

    /* ------ CONSTRUCTORS ------ */

    /** Constructor to process dataset and parameters.
     * @param ds The instance of the dataset for dealing with its records
     * @param sup The user-specified minimum support for the mined association rules
     * @param conf The user-specified minimum confidence for the mined association rules */
    
    public AssocRuleMining(myDataset ds, double sup, double conf) {
    	int i, j, nTrans, nAttr;
    	int[][] trans = ds.getFakeTransactions();
    	
    	nTrans = ds.getnTrans();
    	nAttr = ds.getnVars();
    	
    	support = sup * 100.0;
    	confidence = conf * 100.0;
    	numRows = nTrans;
    	minSupport = (numRows * support) / 100.0;
    	dataArray = new short[numRows][nAttr];
    	
    	for (i=0; i < numRows; i++)
    		for (j=0; j < nAttr; j++)
    			dataArray[i][j] = (short)trans[i][j];
    	
    	numCols = numOneItemSets = ds.getIDsOfAllAttributeValues().size();
    	dataset = ds;
    }

    /* ------ METHODS ------ */
    
    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*        REORDER DATA SET ACCORDING TO ATTRIBUTE FREQUENCY         */
    /*                                                                  */
    /* ---------------------------------------------------------------- */

    /* REORDER INPUT DATA: */

    /** Reorders input data according to frequency of
    single attributes. <P> Example, given the data set:
    <PRE>
    1 2 5
    1 2 3
    2 4 5
    1 2 5
    2 3 5
    </PRE>
    This would produce a countArray (ignore index 0):
    <PRE>
    +---+---+---+---+---+---+
    |   | 1 | 2 | 3 | 4 | 5 |
    +---+---+---+---+---+---+
    |   | 3 | 5 | 2 | 1 | 4 |
    +---+---+---+---+---+---+
    </PRE>
    Which sorts to:
    <PRE>
    +---+---+---+---+---+---+
    |   | 2 | 5 | 1 | 3 | 4 |
    +---+---+---+---+---+---+
    |   | 5 | 4 | 3 | 2 | 1 |
    +---+---+---+---+---+---+
    </PRE>
    Giving rise to the conversion Array of the form (no index 0):
    <PRE>
    +---+---+---+---+---+---+
    |   | 3 | 1 | 4 | 5 | 2 |
    +---+---+---+---+---+---+
    |   | 3 | 5 | 2 | 1 | 4 |
    +---+---+---+---+---+---+
    </PRE>
    Note that the second row here are the counts which no longer play a role
    in the conversion exercise. Thus to the new column number for column 1 is 
    column 3 (i.e. the first vale at index 1). The reconversion array of the 
    form:
    <PRE>
    +---+---+---+---+---+---+
    |   | 2 | 5 | 1 | 3 | 4 |
    +---+---+---+---+---+---+		
    </PRE> */
    
    public void idInputDataOrdering() {
	
	// Count singles and store in countArray;	     
        int[][] countArray = countSingles();
        
	// Bubble sort count array on support value (second index)	
	orderCountArray(countArray);
       
        // Define conversion and reconversion arrays      
	defConvertArrays(countArray);
	
	// Set sorted flag
	isOrderedFlag = true;
	}
	   
    /* COUNT SINGLES */
    
    /** Counts number of occurrences of each single attribute in the
    input data.
    @return 2-D array where first row represents column numbers
    and second row represents support counts. */
    
    protected int[][] countSingles() {
        
	// Dimension and initialize count array
	
	int[][] countArray = new int[numCols+1][2];
	for (int index=0;index<countArray.length;index++) {
	    countArray[index][0] = index;
	    countArray[index][1] = 0;
	    }
	    	    
	// Step through input data array counting singles and incrementing
	// appropriate element in the count array
	
	for(int rowIndex=0;rowIndex<dataArray.length;rowIndex++) {
	     if (dataArray[rowIndex] != null) {
		for (int colIndex=0;colIndex<dataArray[rowIndex].length;
					colIndex++) 
		    	countArray[dataArray[rowIndex][colIndex]][1]++;
		}
	    }
	
	// Return
	
	return(countArray);
	}
	
    /* ORDER COUNT ARRAY */
    
    /** Bubble sorts count array produced by <TT>countSingles</TT> method
    so that array is ordered according to frequency of single items. 
    @param countArray The 2-D array returned by the <TT>countSingles</TT> 
    method. */
       
    private void orderCountArray(int[][] countArray) {
        int attribute, quantity;	
        boolean isOrdered;
        int index; 
               
        do {
	    isOrdered = true;
            index     = 1; 
            while (index < (countArray.length-1)) {
                if (countArray[index][1] >= countArray[index+1][1]) index++;
	        else {
	            isOrdered=false;
                    // Swap
		    attribute              = countArray[index][0];
		    quantity               = countArray[index][1];
	            countArray[index][0]   = countArray[index+1][0];
	            countArray[index][1]   = countArray[index+1][1];
                    countArray[index+1][0] = attribute;
	            countArray[index+1][1] = quantity;
	            // Increment index
		    index++;  
	            }
	  	}     
	    } while (isOrdered==false);
    	}
    
    /* ORDER FIRST N ELEMENTS IN COUNT ARRAY */

    /** Bubble sorts first N elements in count array produced by
    <TT>countSingles</TT> method so that array is ordered according to
    frequency of single items. <P> Used when ordering classification input
    data.
    @param countArray The 2-D array returned by the <TT>countSingles</TT>
    method.
    @param endIndex the index of the Nth element. */

    protected void orderFirstNofCountArray(int[][] countArray, int endIndex) {
        int attribute, quantity;
        boolean isOrdered;
        int index;

        do {
	    isOrdered = true;
            index     = 1;
            while (index < endIndex) {
                if (countArray[index][1] >= countArray[index+1][1]) index++;
	        else {
	            isOrdered=false;
                    // Swap
		    attribute              = countArray[index][0];
		    quantity               = countArray[index][1];
	            countArray[index][0]   = countArray[index+1][0];
	            countArray[index][1]   = countArray[index+1][1];
                    countArray[index+1][0] = attribute;
	            countArray[index+1][1] = quantity;
	            // Increment index
		    index++;
	            }
	  	}
	    } while (isOrdered==false);
    	}

    /* DEFINE CONVERSION ARRAYS: */

    /** Defines conversion and reconversion arrays.
    @param countArray The 2-D array sorted by the <TT>orderCcountArray</TT>
    method.*/

    protected void defConvertArrays(int[][] countArray) {

	// Dimension arrays

	conversionArray   = new int[numCols+1][2];
        reconversionArray = new short[numCols+1];

	// Assign values

	for(int index=1;index<countArray.length;index++) {
            conversionArray[countArray[index][0]][0] = index;
            conversionArray[countArray[index][0]][1] = countArray[index][1];
	    reconversionArray[index] = (short) countArray[index][0];
	    }

	// Diagnostic ouput if desired
	//outputConversionArrays();
	}

    /* RECAST INPUT DATA. */

    /** Recasts the contents of the data array so that each record is ordered
    according to conversion array.
    <P>Proceed as follows:

    1) For each record in the data array. Create an empty new itemSet array.
    2) Place into this array attribute/column numbers that correspond to the
       appropriate equivalents contained in the conversion array.
    3) Reorder this itemSet and return into the data array. */

    public void recastInputData() {
        short[] itemSet;
	int attribute;

	// Step through data array using loop construct

        for(int rowIndex=0;rowIndex<dataArray.length;rowIndex++) {
	    itemSet = new short[dataArray[rowIndex].length];
	    // For each element in the itemSet replace with attribute number
	    // from conversion array
	    for(int colIndex=0;colIndex<dataArray[rowIndex].length;colIndex++) {
	        attribute = dataArray[rowIndex][colIndex];
		itemSet[colIndex] = (short) conversionArray[attribute][0];
		}
	    // Sort itemSet and return to data array
	    sortItemSet(itemSet);
	    dataArray[rowIndex] = itemSet;
	    }
	}

    /* RECAST INPUT DATA AND REMOVE UNSUPPORTED SINGLE ATTRIBUTES. */

    /** Recasts the contents of the data array so that each record is
    ordered according to ColumnCounts array and excludes non-supported
    elements. <P> Proceed as follows:

    1) For each record in the data array. Create an empty new itemSet array.
    2) Place into this array any column numbers in record that are
       supported at the index contained in the conversion array.
    3) Assign new itemSet back into to data array */

    public void recastInputDataAndPruneUnsupportedAtts() {
        short[] itemSet;
	int attribute;

	// Step through data array using loop construct

        for(int rowIndex=0;rowIndex<dataArray.length;rowIndex++) {
	    // Check for empty row
	    if (dataArray[rowIndex]!= null) {
	        itemSet = null;
	        // For each element in the current record find if supported with
	        // reference to the conversion array. If so add to "itemSet".
	    	for(int colIndex=0;colIndex<dataArray[rowIndex].length;colIndex++) {
	            attribute = dataArray[rowIndex][colIndex];
		    // Check support
		    if (conversionArray[attribute][1] >= minSupport) {
		        itemSet = reallocInsert(itemSet,
		    			(short) conversionArray[attribute][0]);
		        }
		    }
	        // Return new item set to data array
	        dataArray[rowIndex] = itemSet;
	 	}
	    }

	// Set isPrunedFlag (used with GUI interface)
	isPrunedFlag=true;
	// Reset number of one item sets field
	numOneItemSets = getNumSupOneItemSets();
	}

    /* GET NUM OF SUPPORTE ONE ITEM SETS */
    /** Gets number of supported single item sets (note this is not necessarily
    the same as the number of columns/attributes in the input set).
    @return Number of supported 1-item sets */

    protected int getNumSupOneItemSets() {
        int counter = 0;

	// Step through conversion array incrementing counter for each
	// supported element found

	for (int index=1;index < conversionArray.length;index++) {
	    if (conversionArray[index][1] >= minSupport) counter++;
	    }

	// Return

	return(counter);
	}

    /** Reconverts given item set according to contents of reconversion array.
    @param itemSet the fgiven itemset.
    @return the reconverted itemset. */	
    
    protected short[] reconvertItemSet(short[] itemSet) {
        // If no conversion return orginal item set
	if (reconversionArray==null) return(itemSet); 
	
	// If item set null return null
	if (itemSet==null) return(null);
	
	// Define new item set
	short[] newItemSet = new short[itemSet.length];
	
	// Copy
	for(int index=0;index<newItemSet.length;index++) {
	    newItemSet[index] = reconversionArray[itemSet[index]];
	    }
	
	// Return
	return(newItemSet);    
        }

    /** Reconvert single item if appropriate. 
    @param item the given item (attribute).
    @return the reconvered item. */
    
    protected short reconvertItem(short item) {
        // If no conversion return orginal item
	if (reconversionArray==null) return(item); 
	
	// Otherwise rerturn reconvert item
	return(reconversionArray[item]);
	}
	
    /* -------------------------------------------------------------- */
    /*                                                                */
    /*        RULE LINKED LIST ORDERED ACCORDING TO CONFIDENCE        */
    /*                                                                */
    /* -------------------------------------------------------------- */

    /* Methods for inserting rules into a linked list of rules ordered
    according to confidence (most confident first). Each rule described in
    terms of 3 fields: 1) Antecedent (an item set), 2) a consequent (an item
    set), 3) a confidence value (double). <P> The support field is not used. */

    /* INSERT (ASSOCIATION/CLASSIFICATION) RULE INTO RULE LINKED LIST (ORDERED
    ACCORDING CONFIDENCE). */

    /** Inserts an (association/classification) rule into the linkedlist of
    rules pointed at by <TT>startRulelist</TT>. <P> The list is ordered so that
    rules with highest confidence are listed first. If two rules have the same
    confidence the new rule will be placed after the existing rule. Thus, if
    using an Apriori approach to generating rules, more general rules will
    appear first in the list with more specific rules (i.e. rules with a larger
    antecedent) appearing later as the more general rules will be generated
    first.
    @param antecedent the antecedent (LHS) of the rule.
    @param consequent the consequent (RHS) of the rule.
    @param confidenceForRule the associated confidence value.
    @param supportForRule the associated support value.  */
    
    protected void insertRuleintoRulelist(short[] antecedent,
    				short[] consequent, double confidenceForRule, double supportForRule, double supportForAntecedent) {

	// Create new node
	RuleNode newNode = new RuleNode(antecedent,consequent,
							confidenceForRule, supportForRule, supportForAntecedent);

	// Empty list situation
	if (startRulelist == null) {
	    startRulelist = newNode;
	    return;
	    }

	// Add new node to start
	if (confidenceForRule > startRulelist.confidenceForRule) {
	    newNode.next = startRulelist;
	    startRulelist  = newNode;
	    return;
	    }

	// Add new node to middle
	RuleNode markerNode = startRulelist;
	RuleNode linkRuleNode = startRulelist.next;
	while (linkRuleNode != null) {
	    if (confidenceForRule > linkRuleNode.confidenceForRule) {
	        markerNode.next = newNode;
		newNode.next    = linkRuleNode;
		return;
		}
	    markerNode = linkRuleNode;
	    linkRuleNode = linkRuleNode.next;
	    }

	// Add new node to end
	markerNode.next = newNode;
	}

    /* ----------------------------------------------- */
    /*                                                 */
    /*        ITEM SET INSERT AND ADD METHODS          */
    /*                                                 */
    /* ----------------------------------------------- */

    /* REALLOC INSERT */

    /** Resizes given item set so that its length is increased by one
    and new element inserted.
    @param oldItemSet the original item set
    @param newElement the new element/attribute to be inserted
    @return the combined item set */

    protected short[] reallocInsert(short[] oldItemSet, short newElement) {

	// No old item set

	if (oldItemSet == null) {
	    short[] newItemSet = {newElement};
	    return(newItemSet);
	    }

	// Otherwise create new item set with length one greater than old
	// item set

	int oldItemSetLength = oldItemSet.length;
	short[] newItemSet = new short[oldItemSetLength+1];

	// Loop

	int index1;
	for (index1=0;index1 < oldItemSetLength;index1++) {
	    if (newElement < oldItemSet[index1]) {
		newItemSet[index1] = newElement;
		// Add rest
		for(int index2 = index1+1;index2<newItemSet.length;index2++)
				newItemSet[index2] = oldItemSet[index2-1];
		return(newItemSet);
		}
	    else newItemSet[index1] = oldItemSet[index1];
	    }

	// Add to end

	newItemSet[newItemSet.length-1] = newElement;

	// Return new item set

	return(newItemSet);
	}

    /* REALLOC 1 */

    /** Resizes given item set so that its length is increased by one
    and appends new element (identical to append method)
    @param oldItemSet the original item set
    @param newElement the new element/attribute to be appended
    @return the combined item set */

    protected short[] realloc1(short[] oldItemSet, short newElement) {

	// No old item set

	if (oldItemSet == null) {
	    short[] newItemSet = {newElement};
	    return(newItemSet);
	    }

	// Otherwise create new item set with length one greater than old
	// item set

	int oldItemSetLength = oldItemSet.length;
	short[] newItemSet = new short[oldItemSetLength+1];

	// Loop

	int index;
	for (index=0;index < oldItemSetLength;index++)
		newItemSet[index] = oldItemSet[index];
	newItemSet[index] = newElement;

	// Return new item set

	return(newItemSet);
	}

    /* REALLOC 2 */

    /** Resizes given array so that its length is increased by one element
    and new element added to front
    @param oldItemSet the original item set
    @param newElement the new element/attribute to be appended
    @return the combined item set */

    protected short[] realloc2(short[] oldItemSet, short newElement) {

	// No old array

	if (oldItemSet == null) {
	    short[] newItemSet = {newElement};
	    return(newItemSet);
	    }

	// Otherwise create new array with length one greater than old array

	int oldItemSetLength = oldItemSet.length;
	short[] newItemSet = new short[oldItemSetLength+1];

	// Loop

	newItemSet[0] = newElement;
	for (int index=0;index < oldItemSetLength;index++)
		newItemSet[index+1] = oldItemSet[index];

	// Return new array

	return(newItemSet);
	}
	
    /* --------------------------------------------- */
    /*                                               */
    /*            ITEM SET DELETE METHODS            */
    /*                                               */
    /* --------------------------------------------- */

    /* REMOVE ELEMENT N */
    
    /** Removes the nth element/attribute from the given item set.
    @param oldItemSet the given item set.
    @param n the index of the element to be removed (first index is 0). 
    @return Revised item set with nth element removed. */
    
    protected short[] removeElementN(short [] oldItemSet, int n) {
        if (oldItemSet.length <= n) return(oldItemSet);
	else {
	    short[] newItemSet = new short[oldItemSet.length-1];
	    for (int index=0;index<n;index++) newItemSet[index] = 
	    				oldItemSet[index];
	    for (int index=n+1;index<oldItemSet.length;index++) 
	        			newItemSet[index-1] = oldItemSet[index];
	    return(newItemSet);
	    }
	}
	
    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*              METHODS TO RETURN SUBSETS OF ITEMSETS               */
    /*                                                                  */
    /* ---------------------------------------------------------------- */

    /* COMPLEMENT */
    
    /** Returns complement of first itemset with respect to second itemset.
    @param itemSet1 the first given item set.
    @param itemSet2 the second given item set.
    @return complement if <TT>itemSet1</TT> in <TT>itemSet2</TT>. */
    
    protected short[] complement(short[] itemSet1, short[] itemSet2) {
        int lengthOfComp = itemSet2.length-itemSet1.length;
	
	// Return null if no complement
	if (lengthOfComp<1) return(null);
	
	// Otherwsise define combination array and determine complement
	short[] complement  = new short[lengthOfComp];
	int complementIndex = 0;
	for(int index=0;index<itemSet2.length;index++) {
	    // Add to combination if not in first itemset
	    if (notMemberOf(itemSet2[index],itemSet1)) {
	    	complement[complementIndex] = itemSet2[index];
		complementIndex++;
		}	
	    }
	
	// Return
	return(complement);
	}

    /* --------------------------------------- */
    /*                                         */
    /*             SORT ITEM SET               */
    /*                                         */
    /* --------------------------------------- */

    /* SORT ITEM SET: Given an unordered itemSet, sort the set */

    /** Sorts an unordered item set.
    @param itemSet the given item set. */

    protected void sortItemSet(short[] itemSet) {
        short temp;
        boolean isOrdered;
        int index;

        do {
	    isOrdered = true;
            index     = 0;
            while (index < (itemSet.length-1)) {
                if (itemSet[index] <= itemSet[index+1]) index++;
	        else {
	            isOrdered=false;
                    // Swap
		    temp = itemSet[index];
	            itemSet[index] = itemSet[index+1];
                    itemSet[index+1] = temp;
	            // Increment index
		    index++;
	            }
	  	}
	    } while (isOrdered==false);
    	}  

    /* ----------------------------------------------------- */
    /*                                                       */
    /*             BOOLEAN ITEM SET METHODS ETC.             */
    /*                                                       */
    /* ----------------------------------------------------- */    
	
    /* NOT MEMBER OF */
    
    /** Checks whether a particular element/attribute identified by a 
    column number is not a member of the given item set.
    @param number the attribute identifier (column number).
    @param itemSet the given item set.
    @return true if first argument is not a member of itemSet, and false 
    otherwise */
    
    protected boolean notMemberOf(short number, short[] itemSet) {
        
	// Loop through itemSet
	
	for(int index=0;index<itemSet.length;index++) {
	    if (number < itemSet[index]) return(true);
	    if (number == itemSet[index]) return(false);
	    }
	
	// Got to the end of itemSet and found nothing, return true
	
	return(true);
	}

    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*                            MISCELANEOUS                          */
    /*                                                                  */
    /* ---------------------------------------------------------------- */
    
    /* COPY ITEM SET */
    
    /** Makes a copy of a given itemSet. 
    @param itemSet the given item set.
    @return copy of given item set. */
    
    protected short[] copyItemSet(short[] itemSet) {
	
	// Check whether there is a itemSet to copy
	if (itemSet == null) return(null);
	
	// Do copy and return
	short[] newItemSet = new short[itemSet.length];
	for(int index=0;index<itemSet.length;index++) {
	    newItemSet[index] = itemSet[index];
	    }
        
	// Return
	return(newItemSet);
	}

    /* ------------------------------------------------- */
    /*                                                   */
    /*                   OUTPUT METHODS                  */
    /*                                                   */
    /* ------------------------------------------------- */
    
    /* ----------------- */	
    /* OUTPUT DATA TABLE */  
    /* ----------------- */
    /** Outputs stored input data set; initially read from input data file, but
    may be reordered or pruned if desired by a particular application. */
     
    public void outputDataArray() {
        if (isPrunedFlag) System.out.println("DATA SET (Ordered and Pruned)\n" +
					"-----------------------------");
	else {
	    if (isOrderedFlag) System.out.println("DATA SET (Ordered)\n" +
					"------------------");
	    else System.out.println("DATA SET\n" + "--------");
	    }

	// Loop through data array
        for(int index=0;index<dataArray.length;index++) {
	    outputItemSet(dataArray[index]);
	    System.out.println();
	    }
	}

    /* -------------- */
    /* OUTPUT ITEMSET */
    /* -------------- */
    /** Outputs a given item set.
    @param itemSet the given item set. */

    protected void outputItemSet(short[] itemSet) {
	// Check for empty set
	if (itemSet == null) System.out.print(" null ");
	// Process
	else {
	    // Reconvert where input dataset has been reordered and possible 
	    // pruned.
	    short[] tempItemSet = reconvertItemSet(itemSet);
	    // Loop through item set elements
            int counter = 0;
	    for (int index=0;index<tempItemSet.length;index++) {
	        if (counter == 0) {
	    	    counter++;
		    System.out.print(" {");
		    }
	        else System.out.print(" ");
	        System.out.print(tempItemSet[index]);
		}
	    System.out.print("} ");
	    }
	}
    
    /* ------------------------ */
    /* OUTPUT RULE LINKED LISTS */
    /* ------------------------ */	

    /** Outputs contents of rule linked list (if any) assuming that the list
    represents a set of ARs.	*/

    public void outputRules() {
        System.out.println("\nASSOCIATION RULES\n=================");
        outputRules(startRulelist);
	}
	
    /** Outputs given rule list.
    @param ruleList the given rule list. */

    public void outputRules(RuleNode ruleList) {
	// Check for empty rule list
	if (ruleList==null) System.out.println("No rules generated!");
	
	// Loop through rule list
	int number = 1;
        RuleNode linkRuleNode = ruleList;
	while (linkRuleNode != null) {
	    System.out.print("(" + number + ") ");
	    outputRule(linkRuleNode);
            System.out.println(" " +
	    		twoDecPlaces(linkRuleNode.confidenceForRule) + "%");
	    number++;
	    linkRuleNode = linkRuleNode.next;
	    }
	}

    /** Outputs a rule assuming that the rule represents an ARs.
    @param rule the rule to be output. */

    private void outputRule(RuleNode rule) {
        outputItemSet(rule.antecedent);
	System.out.print(" -> ");
        outputItemSet(rule.consequent);
	}

    /* -------------------------------- */
    /*                                  */
    /*        OUTPUT UTILITIES          */
    /*                                  */
    /* -------------------------------- */
    
    /* TWO DECIMAL PLACES */
    
    /** Converts given real number to real number rounded up to two decimal 
    places. 
    @param number the given number.
    @return the number to two decimal places. */ 
    
    protected double twoDecPlaces(double number) {
    	int numInt = (int) ((number+0.005)*100.0);
	number = ((double) numInt)/100.0;
	return(number);
    }
        
    /* -------------------------------- */
    /*                                  */
    /*           NEW FEATURES           */
    /*                                  */
    /* -------------------------------- */
    
    /**
     * It constructs a rules set once the algorithm has been carried out.
     * @return An array of association rules having both minimum confidence and support */
    
    public ArrayList<AssociationRule> getRulesSet() {
    	RuleNode linkRuleNode = startRulelist;
    	ArrayList<AssociationRule> rules = new ArrayList<AssociationRule>();
    	
    	// Loop through rule list
    	while (linkRuleNode != null) {
    		rules.add( new AssociationRule( reconvertItemSet(linkRuleNode.antecedent), reconvertItemSet(linkRuleNode.consequent), (double)linkRuleNode.supportForRule / numRows, (double)linkRuleNode.supportForAntecedent / numRows, linkRuleNode.confidenceForRule / 100.0 ) );
    		linkRuleNode = linkRuleNode.next;
    	}
    	
    	return rules;
    }
    
}

