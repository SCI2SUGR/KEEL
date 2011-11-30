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
/*                      ASSOCIATION RULE DATA MINING                          */
/*                                                                            */
/*                            Frans Coenen                                    */
/*                                                                            */
/*                        Wednesday 9 January 2003                            */
/*        (revised 21/1/2003, 14/2/2003, 2/5/2003, 2/7/2003, 3/2/2004)        */
/*                                                                            */
/*                    Department of Computer Science                          */
/*                     The University of Liverpool                            */
/*                                                                            */ 
/* -------------------------------------------------------------------------- */

// Java packages
package keel.Algorithms.Associative_Classification.ClassifierCMAR;

import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 * Set of utilities to support various Association Rule Mining (ARM) 
algorithms included in the LUCS-KDD suite of ARM programs.
 *
 * @author Frans Coenen 2 July 2003
 * @author Modified by Jesus Alcala (University of Granada) 09/02/2010
 * @author Modified by Sergio Almecija (University of Granada) 23/05/2010
 * @version 1.0
 * @since JDK1.5
 */
public class AssocRuleMining extends JFrame {

    /* ------ FIELDS ------ */
	
    // Data structures
     
    /** 2-D aray to hold input data from data file */
    protected short[][] dataArray = null;
    /** 2-D array used to renumber coulmns for input data in terms of
    frequency of single attributes (reordering will enhance performance
    for some ARM algorithms). */
    protected int[][] conversionArray   = null;
    /** 1-D array used to reconvert input data column numbers to their
    original numbering where the input data has been ordered to enhance
    computational efficienvy. */
    protected short[] reconversionArray = null;
    
    // Constants
    
    /** Minimum support value */
    private static final double MIN_SUPPORT = 0.0;
    /** Maximum support value */
    private static final double MAX_SUPPORT = 100.0;
    /** Maximum confidence value */
    private static final double MIN_CONFIDENCE = 0.0;
    /** Maximum confidence value */
    private static final double MAX_CONFIDENCE = 100.0;	
    
    // Command line arguments with default values and associated fields
    
    /** Command line argument for data file name. */	
    protected String  fileName   = null;
    /** Command line argument for number of columns. */	
    protected int     numCols    = 0;
    /** Command line argument for number of rows. */
    protected int     numRows    = 0;
    /** Command line argument for % support (default = 1%). */
    protected double  support    = 1.0;
    /** Minimum support value in terms of number of rows. */
    protected double  minSupport = 0;
    /** Command line argument for % confidence (default = 50%). */
    protected double  confidence = 50.0;
    /** The number of one itemsets (singletons). */
    protected int numOneItemSets = 0;	
    /** Number of classes in input data set (input by the user). */
    protected int numClasses = 0;	
    
    // Flags
    
    /** Error flag used when checking command line aruments (default = 
    <TT>true</TT>). */
    protected boolean errorFlag  = true;
    /** Input format OK flag( default = <TT>true</TT>). */
    protected boolean inputFormatOkFlag = true;
    /** Flag to indicate whether system has data or not. */
    private boolean haveDataFlag = false;
    /** Flag to indicate whether input data has been sorted or not. */
    private boolean isOrderedFlag = false;
    /** Flag to indicate whether input data has been sorted and pruned or 
    not. */
    private boolean isPrunedFlag = false;
    
    // Other fields
    
    /** The input stream. */
    protected BufferedReader fileInput;
    /** The file path */
    protected File filePath = null;	
    
    /* ------ CONSTRUCTORS ------ */
    
    /** 
     * Processes command line arguments.
     * @param minConf double Minimum confidence threshold
     * @param minSup double Minimum support threshold
     */
    	 
    public AssocRuleMining(double minConf, double minSup) {
		confidence = minConf;  
        support = minSup;  
    }

    /** Default constructor */

    public AssocRuleMining() {
    }

	
    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*                     READ INPUT DATA FROM FILE                    */
    /*                                                                  */
    /* ---------------------------------------------------------------- */

    /* INPUT DATA SET */    
    
    /** 
     * Commences process of getting inout data (GUI version also exists). 
     * @param train myDataset Class where examples are stored to build the classifier
     * @param dataBase DataBase Class to store the examples to work with the algorithm and some other useful information
     */
    
    public void inputDataSet(myDataset train, DataBase dataBase) {    
        // Read the file
		readFile(train, dataBase);
		countNumCols();
		minSupport = (numRows * support) / 100.0;
	}
	 
    /* READ FILE */
    /** Reads input data from file specified in command line argument (GUI 
    version also exists). <P>Proceeds as follows:
    <OL>
    <LI>Gets number of lines in file, checking format of each line (space 
    seperated integers), if incorrectly formatted line found 
    <TT>inputFormatOkFlag</TT> set to <TT>flase</TT>.
    <LI>Diminsions input array.
    <LI>Reads data
    </OL> 
    * @param train myDataset Class where examples are stored to build the classifier
    * @param dataBase DataBase Class to store the examples to work with the algorithm and some other useful information
    */
    
    public void readFile(myDataset train, DataBase dataBase) {
		int i, j, k;
		int [] example;
		short value;

		numClasses = train.getnClasses();
		numRows = train.getnData();
		dataArray = new short[numRows][];	

		for (i=0; i < numRows; i++) {
			example = train.getExample(i);
			dataArray[i] = new short[dataBase.numVariablesUsed() + 1];
			value = 1;
			for (j=0, k=0; j < example.length; j++) {
				if (dataBase.numLabels(j) > 1) {
					dataArray[i][k] = (short) example[j];
					dataArray[i][k] += value;
					value += dataBase.numLabels(j);
//					System.out.print (dataArray[i][k] + " ");
					k++;
				}
			}
			dataArray[i][k] = (short) train.getOutputAsInteger(i);
			dataArray[i][k] += value;
//			System.out.print (dataArray[i][k] + " ");

//			System.out.println ("");
		}
//		System.out.println ("");
//		System.out.println ("");
	}    
    
	
    /* COUNT NUMBER OF COLUMNS */
    /** Counts number of columns represented by input data. */
    	
    private void countNumCols() {
        int maxAttribute=0;
		
		// Loop through data array	
        for(int index=0; index<dataArray.length; index++) {
			int lastIndex = dataArray[index].length-1;
			if (dataArray[index][lastIndex] > maxAttribute)  maxAttribute = dataArray[index][lastIndex];	    
	    }
		
		numCols        = maxAttribute;
		numOneItemSets = numCols; 	// default value only
	}	
	
    /* OPEN FILE NAME */    
    /** Opens file using fileName (instance field). 
    @param nameOfFile the filename of the file to be opened. */
/*    
    protected void openFileName(String nameOfFile) {
		try {
			// Open file
			FileReader file = new FileReader(nameOfFile);
			fileInput = new BufferedReader(file);
	    }	
		catch(IOException ioException) {
			JOptionPane.showMessageDialog(this,"Error Opening File", "Error: ",JOptionPane.ERROR_MESSAGE);
	    }
	}
*/	
    /* OPEN FILE PATH */
    /** Opens file using filePath (instance field). */
/*
    private void openFilePath() {
	try {
	    // Open file
	    FileReader file = new FileReader(filePath);
	    fileInput = new BufferedReader(file);
	    }
	catch(IOException ioException) {
	    JOptionPane.showMessageDialog(this,"Error Opening File",
			 "Error: ",JOptionPane.ERROR_MESSAGE);
	    }
	}
*/	   
    /* CLOSE FILE */    
    /** Close file fileName (instance field). */
/*    
    protected void closeFile() {
        if (fileInput != null) {
	    try {
	    	fileInput.close();
		}
	    catch (IOException ioException) {
		JOptionPane.showMessageDialog(this,"Error Closeing File", 
			 "Error: ",JOptionPane.ERROR_MESSAGE);
		}
	    }
	}
*/	
    /* BINARY CONVERSION. */
    
    /** Produce an item set (array of elements) from input line.
    @param dataLine row from the input data file
    @param numberOfTokens number of items in row
    @return 1-D array of short integers representing attributes in input
    row */
    
    protected short[] binConversion(StringTokenizer dataLine, int numberOfTokens) {
        short number;
		short[] newItemSet = null;
		
		// Load array
		for (int tokenCounter=0; tokenCounter < numberOfTokens; tokenCounter++) {
			number = new Short(dataLine.nextToken()).shortValue();
			newItemSet = realloc1(newItemSet, number);
	    }
	
	// Return itemSet	
	
	return(newItemSet);
	}
		
    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*        REORDER DATA SET ACCORDING TO ATTRIBUTE FREQUENCY         */
    /*                                                                  */
    /* ---------------------------------------------------------------- */
    
    /* REORDER INPUT DATA: */
    
    /** Reorders input data according to frequency of 
    single attributes. <P> Example, giben the data set:
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
    </PRE> 
    * @param dataBase DataBase Class to store the examples to work with the algorithm and some other useful information
    */
    
    public void idInputDataOrdering(DataBase dataBase) {
		// Count singles and store in countArray;	     
        int[][] countArray = countSingles(dataBase);
		
		// Bubble sort count array on support value (second index)	
		orderCountArray(countArray);
		
		// Define conversion and reconversion arrays      
		defConvertArrays(countArray);
		
		// Set sorted flag
		isOrderedFlag = true;
	}
	   
    /* COUNT SINGLES */
    
    /** Counts number of occurances of each single attribute in the
    input data.
    @return 2-D array where first row represents column numbers
    and second row represents support counts. */
    
    protected int[][] countSingles(DataBase dataBase) {
		// Dimension and initialize count array
		
		int[][] countArray = new int[numCols+1][2];
		
		for (int index=0;index<countArray.length;index++) {
			countArray[index][0] = index;
			countArray[index][1] = 0;
	    }

		// Step through input data array counting singles and incrementing
		// appropriate elment in the count array
		
		for(int rowIndex=0;rowIndex<dataArray.length;rowIndex++) {
			if (dataArray[rowIndex] != null) {
				for (int colIndex=0; colIndex<dataArray[rowIndex].length; colIndex++) {
					if (dataBase.numLabels(colIndex) > 1)  countArray[dataArray[rowIndex][colIndex]][1]++;
				}
			}
	    }
		
		// Return
		return(countArray);
	}
	
    /* SORT COUNT ARRAY */
    
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
    
    /* SORT FIRST N ELEMENTS IN COUNT ARRAY */
    
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
    2) Place into this array attribute/column numbers that coorospond to the
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
	        // For each elment in the current record find if supported with 
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
    @return Number of supported 1-item stes */
    
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
	
    /* RESIZE INPUT DATA */
    
    /** Recasts the input data sets so that only N percent is used. 
    @param percentage the percentage of the current input data that is to form
    the new input data set (number between 0 and 100). */	
    
    public void resizeInputData(double percentage) { 
	// Redefine number of rows
	numRows = (int) ((double) numRows*(percentage/100.0));
        System.out.println("Recast input data, new num rows = " + numRows);
				  
	// Dimension and populate training set. 
	short[][] trainingSet = new short[numRows][];
	for (int index=0;index<numRows;index++) 
				trainingSet[index] = dataArray[index];
	
	// Assign training set label to input data set label.
	dataArray = trainingSet;   
	
	// Determine new minimum support threshold value
	
	minSupport = (numRows * support)/100.0;
	}
		
    /* ----------------------------------------------- */
    /*                                                 */
    /*        ITEM SET INSERT AND ADD METHODS          */
    /*                                                 */
    /* ----------------------------------------------- */

    /* APPEND */

    /** Concatinates two itemSets --- resizes given array so that its
    length is increased by size of second array and second array added.
    @param itemSet1 The first item set.
    @param itemSet2 The item set to be appended.
    @return the combined item set */

    protected short[] append(short[] itemSet1, short[] itemSet2) {

	// Test for emty sets, if found return other
	
	if (itemSet1 == null) return(copyItemSet(itemSet2));
	else if (itemSet2 == null) return(copyItemSet(itemSet1));

	// Create new array
	
	short[] newItemSet = new short[itemSet1.length+itemSet2.length];
	
	// Loop through itemSet 1
	
	int index1;
	for(index1=0;index1<itemSet1.length;index1++) {
	    newItemSet[index1]=itemSet1[index1];
	    }
	
	// Loop through itemSet 2
	
	for(int index2=0;index2<itemSet2.length;index2++) {
	    newItemSet[index1+index2]=itemSet2[index2];
	    }

	// Return
	
	return(newItemSet);	
        }
	
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
		int index;
		
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
		for (index=0; index < oldItemSetLength; index++)	newItemSet[index] = oldItemSet[index];		
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
	
    /* REALLOC 3 */

    /** Resizes given array so that its length is decreased by one element
    and first element removed
    @param oldItemSet the original item set
    @return the shortened item set */

    protected short[] realloc3(short[] oldItemSet) {
        	
	// If old item set comprises one element return null
	
	if (oldItemSet.length == 1) return null;
	
	// Create new array with length one greater than old array
	
	int newItemSetLength = oldItemSet.length-1;
	short[] newItemSet = new short[newItemSetLength];
	
	// Loop
	
	for (int index=0;index < newItemSetLength;index++)
		newItemSet[index] = oldItemSet[index+1];
	
	// Return new array
	
	return(newItemSet);
	}	

    /* REALLOC 4 */

    /** Resize given array so that its length is decreased by size of
    second array (which is expected to be a leading subset of the first)
    and remove second array.
    @param oldItemSet The first item set.
    @param array2 The leading subset of the <TT>oldItemSet</TT>.
    @return Revised item set with leading subset removed. */

    protected short[] realloc4(short[] oldItemSet, short[] array2) {
        int array2length   = array2.length;
		int newItemSetLength = oldItemSet.length-array2length;
		
		// Create new array
		short[] newItemSet = new short[newItemSetLength];
		
		// Loop
		for (int index=0; index < newItemSetLength; index++)
			newItemSet[index] = oldItemSet[index+array2length];
		
		// Return new array
		return(newItemSet);
	}
	
    /* --------------------------------------------- */
    /*                                               */
    /*            ITEM SET DELETE METHODS            */
    /*                                               */
    /* --------------------------------------------- */

    /* REMOVE FIRST N ELEMENTS */

    /** Removes the first n elements/attributes from the given item set.
    @param oldItemSet the given item set.
    @param n the number of leading elements to be removed.
    @return Revised item set with first n elements removed. */

    protected short[] removeFirstNelements(short[] oldItemSet, int n) {
        if (oldItemSet.length == n) return(null);
    	else {
	    short[] newItemSet = new short[oldItemSet.length-n];
	    for (int index=0;index<newItemSet.length;index++) {
	        newItemSet[index] = oldItemSet[index+n];
	        }
	    return(newItemSet);
	    }
	}
	
    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*              METHODS TO RETURN SUBSETS OF ITEMSETS               */
    /*                                                                  */
    /* ---------------------------------------------------------------- */

    /* GET LAST ELEMENT */ 
     
    /** Gets the last element in the given item set, or '0' if the itemset is 
    empty. 
    @param itemSet the given item set.  
    @return the last element. */  
     
    protected short getLastElement(short[] itemSet) { 
        // Check for empty item set 
	if (itemSet == null) return(0); 
	// Otherwise return last element 
        return(itemSet[itemSet.length-1]); 
	} 
	 
    /* COMPLEMENT */

    /** Returns complement of first itemset with respect to second itemset.
    @param itemSet1 the first given item set.
    @param itemSet1 the second given item set.
    @return complement if <TT>itemSet1</TT> in <TT>itemSet2</TT>. */

    protected short[] complement(short[] itemSet1, short[] itemSet2) {
        int lengthOfComp = itemSet2.length-itemSet1.length;
	
	// Return null if no complement
	if (lengthOfComp<1) return(null);
	
	// Otherwsiose define combination array and determine complement
	short[] complement  = new short[lengthOfComp];
	int complementIndex = 0;
	for(int index=0;index<itemSet2.length;index++) {
	    // Add to comination if not in first itemset
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
    		  	
    /* CHECK ITEM SET: */

    /** Determines relationship between two item sets (same, parent,
    before, child or after).
    @param itemSet1 the first item set.
    @param itemSet2 the second item set to be compared with first.
    @return 1 = same, 2 = itemSet2 is parent of itemSet1, 3 = itemSet2
    lexicographically before itemSet1, 4 = itemSet2 is child of itemSet1,
    and 5 = itemSet2 lexicographically after itemSet1. */

    protected int checkItemSets(short[] itemSet1, short[] itemSet2) {

        // Check if the same

	if (isEqual(itemSet1,itemSet2)) return(1);

	// Check whether before or after and subset/superset.

	if (isBefore(itemSet1,itemSet2)) {
	    if (isSubset(itemSet1,itemSet2)) return(2);
	    else return(3);
	    }
        if (isSubset(itemSet2,itemSet1)) return(4);
        return(5);
        }	

    /* EQUALITY CHECK */

    /** Checks whether two item sets are the same.
    @param itemSet1 the first item set.
    @param itemSet2 the second item set to be compared with first.
    @return true if itemSet1 is equal to itemSet2, and false otherwise. */

    protected boolean isEqual(short[] itemSet1, short[] itemSet2) {
	
	// If no itemSet2 (i.e. itemSet2 is null return false)
	
	if (itemSet2 == null) return(false);
	
	// Compare sizes, if not same length they cannot be equal.
	
	int length1 = itemSet1.length;
	int length2 = itemSet2.length;
	if (length1 != length2) return(false);

        // Same size compare elements

        for (int index=0;index < length1;index++) {
	    if (itemSet1[index] != itemSet2[index]) return(false);
	    }

        // itemSet the same.

        return(true);
        }	

    /* BEFORE CHECK */

    /** Checks whether one item set is lexicographically before a second
    item set.
    @param itemSet1 the first item set.
    @param itemSet2 the second item set to be compared with first.
    @return true if itemSet1 is less than or equal (before) itemSet2 and
    false otherwise. Note that before here is not numerical but lexical,
    i.e. {1,2} is before {2} */

    public static boolean isBefore(short[] itemSet1, short[] itemSet2) {
        int length2 = itemSet2.length;
	
	// Compare elements
	for(int index1=0;index1<itemSet1.length;index1++) {
	    if (index1 == length2) return(false); // itemSet2 is a proper subset of itemSet1	
    	    if (itemSet1[index1] < itemSet2[index1]) return(true);
	    if (itemSet1[index1] > itemSet2[index1]) return(false);
	    }
	
	// Return true
	return(true);
	}
		
    /* SUBSET CHECK */

    /** Checks whether one item set is subset of a second item set.
    @param itemSet1 the first item set.
    @param itemSet2 the second item set to be compared with first.
    @return true if itemSet1 is a subset of itemSet2, and false otherwise.
    */

    protected boolean isSubset(short[] itemSet1, short[] itemSet2) {
	// Check for empty itemsets
	if (itemSet1==null) return(true);
	if (itemSet2==null) return(false);
	
	// Loop through itemSet1
	for(int index1=0;index1<itemSet1.length;index1++) {
	    if (notMemberOf(itemSet1[index1],itemSet2)) return(false);
	    }
	
	// itemSet1 is a subset of itemSet2
	return(true);
	}	
	
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
	
	// Got to the end of itemSet and found nothing, return false
	
	return(true);
	}
	
    /* CHECK FOR LEADING SUB STRING */

    /** Checks whether two itemSets share a leading substring.
    @param itemSet1 the first item set.
    @param itemSet2 the second item set to be compared with first.
    @return the substring if a shared leading substring exists, and null
    otherwise. */	
	
    protected short[] checkForLeadingSubString(short[] itemSet1,
    							short[] itemSet2) {
        //int index3=0;
	short[] itemSet3 = null;
	
	// Loop through itemSets
	
	for(int index=0;index<itemSet1.length;index++) {
	    if (index == itemSet2.length) break;
	    if (itemSet1[index] == itemSet2[index])
	    			itemSet3 = realloc1(itemSet3,itemSet1[index]);
	    else break;
	    }	 	
	
	// Return
	
	return(itemSet3);
	}
		
    /* -------------------------------------------------- */
    /*                                                    */
    /*                ITEM SET COMBINATIONS               */
    /*                                                    */
    /* -------------------------------------------------- */

    /* COMBINATIONS */

    /** Invokes <TT>combinations</TT> method to calculate all possible
    combinations of a given item set. <P>
    For example given the item set [1,2,3] this will result in the
    combinations[[1],[2],[3],[1,2],[1,3],[2,3],[1,2,3]].
    @param inputSet the given item set.
    @return array of arrays representing all possible combinations (may be null
    if bo combinations). */

    protected short[][] combinations(short[] inputSet) {
	if (inputSet == null) return(null);
	else {
	    short[][] outputSet = new short[getCombinations(inputSet)][];
	    combinations(inputSet,0,null,outputSet,0);
	    return(outputSet);
	    }
	}
	
    /** Recursively calculates all possible combinations of a given item
    set.
    @param inputSet the given item set.
    @param inputIndex the index within the input set marking current
    element under consideration (0 at start).
    @param sofar the part of a combination determined sofar during the
    recursion (null at start).
    @param outputSet the combinations collected so far, will hold all
    combinations when recusion ends.
    @param outputIndex the current location in the output set.
    @return revised output index. */

    private int combinations(short[] inputSet, int inputIndex,
    		short[] sofar, short[][] outputSet, int outputIndex) {
    	short[] tempSet;
	int index=inputIndex;
	
    	// Loop through input array
	
	while(index < inputSet.length) {
            tempSet = realloc1(sofar,inputSet[index]);
            outputSet[outputIndex] = tempSet;
	    outputIndex = combinations(inputSet,index+1,
	    		copyItemSet(tempSet),outputSet,outputIndex+1);	
    	    index++;
	    }

    	// Return

    	return(outputIndex);
    	}

    /* GET COMBINATTIONS */

    /** Gets the number of possible combinations of a given item set.
    @param set the given item set.
    @return number of possible combinations. */

    private int getCombinations(short[] set) {
    	int counter=0, numComb;	
	
	numComb = (int) Math.pow(2.0,set.length)-1;
	
    	// Return

        return(numComb);
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
     
    /** Makes a copy of a given set of itemSets.  
    @param itemSets the given set of item sets. 
    @return copy of given set of item sets. */ 
     
    protected short[][] copyItemSet(short[][] itemSets) { 
	 
	// Check whether there is a itemSet to copy 
	if (itemSets == null) return(null); 
	 
	// Do copy and return 
	short[][] newItemSets = new short[itemSets.length][]; 
	for(int index1=0;index1<itemSets.length;index1++) { 
	    if (itemSets[index1]==null) newItemSets[index1]=null; 
	    else { 
	        newItemSets[index1] = new short[itemSets[index1].length]; 
		for(int index2=0;index2<itemSets[index1].length;index2++) { 
	            newItemSets[index1][index2] = itemSets[index1][index2]; 
	            } 
		} 
	    } 
	     
        // Return 
	return(newItemSets); 
	} 
	
    /* ------------------------------------------------- */
    /*                                                   */
    /*                    GET METHODS                    */
    /*                                                   */
    /* ------------------------------------------------- */

    /* GET SUPPORT */
    /** Gets the current support setting.
    @return the support value. */

    /*public double getSupport() {
        return(support);
	}      */

    /* GET CONFIDENCE */
    /** Gets the current confidence setting.
    @return the confidence value. */

    public double getConfidence() {
        return(confidence);
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
    may be reirdered or pruned if desired by a particular application. */

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

    /** Outputs the given array of array of short integers. <P> Used for
    diagnostic purposes.
    @param dataSet the five array of arrays. */

    protected void outputDataArray(short[][] dataSet) {
        if (dataSet==null) {
	    System.out.println("null");
	    return;
	    }
	
	// Loop through data array
        for(int index=0;index<dataSet.length;index++) {
	    outputItemSet(dataSet[index]);
	    System.out.println();
	    }
	}
	
    /* -------------- */
    /* OUTPUT ITEMSET */
    /* -------------- */
    /** Outputs a given item set.
    @param itemSet the given item set. */

    protected String outputItemSet(short[] itemSet) {
		String stringOut = new String("");

		// Loop through item set elements
		if (itemSet == null) stringOut = " null ";
		else {
			int counter = 0;
			for (int index=0;index<itemSet.length;index++) {
				if (counter == 0) {
					counter++;
					stringOut += " {";
				}
				else stringOut += " ";
				stringOut += ("" + itemSet[index]);
			}
			stringOut += "} ";
	    }

		return stringOut;
	}	

    /* ---------------------------------*/
    /* OUTPUT ITEMSET WITH RECONVERSION */
    /* ---------------------------------*/
    /** Outputs a given item set reconverting it to its orginal column number
    labels (used where input dataset has been reordered and possible pruned).
    @param itemSet the given item set. */

    protected void outputItemSetWithReconversion(short[] itemSet) {
		// Loop through item set elements
		if (itemSet == null) System.out.print(" null ");
		else {
			int counter = 0;
			for (int index=0;index<itemSet.length;index++) {
				if (counter == 0) {
					counter++;
					System.out.print(" [");
				}
				else System.out.print(" ");
				//System.out.print(itemSet[index]);
				System.out.print(reconversionArray[itemSet[index]]);
			}
			System.out.print("] ");
		}
	}
	
    /* ---------------------- */		
    /* OUTPUT DATA ARRAY SIZE */
    /* ---------------------- */
    /** Ouputs size (number of records and number of elements) of stored
    input data set read from input data file. */

    public void outputDataArraySize() {
    	int numRecords = 0;
		int numElements = 0;
		
		// Loop through data array
		for (int index=0;index<dataArray.length;index++) {
			if (dataArray[index] != null) {
				numRecords++;
				numElements = numElements+dataArray[index].length;
	        }
	    }
		
		// Output
		System.out.println("Number of records        = " + numRecords);
		System.out.println("Number of elements       = " + numElements);
	}
	
    /* ------------------------ */
    /* OUTPUT CONVERSION ARRAYS */
    /* ------------------------ */
    /** Outputs conversion array (used to renumber coulmns for input data
    in terms of frequency of single attributes --- reordering will enhance
    performance for some ARM algorithms). */

    public void outputConversionArrays() {		
		// Conversion array
		System.out.println("Conversion Array = ");
		for(int index=1;index<conversionArray.length;index++) {
			System.out.println("(" + index + ") " + conversionArray[index][0] + " = " + conversionArray[index][1]);
	    }

        // Reconversion array
        System.out.println("Reonversion Array = ");
		for(int index=1;index<reconversionArray.length;index++) {
			System.out.println("(" + index + ") " + reconversionArray[index]);
	    }
	}

    /* ----------- */
    /* OUTPUT MENU */
    /* ----------- */
    /** Outputs menu for command line arguments. */

    protected void outputMenu() {
        System.out.println();
		System.out.println("-C  = Confidence (default 80%)");
		System.out.println("-F  = File name");	
		System.out.println("-N  = Number of classes (Optional)");
		System.out.println("-S  = Support (default 20%)");
		System.out.println();
		
		// Exit
		System.exit(1);
	}

    /* --------------- */
    /* OUTPUT SETTINGS */
    /* --------------- */
    /** Outputs command line values provided by user. */

    protected void outputSettings() {
        System.out.println("SETTINGS\n--------");
		System.out.println("File name                = " + fileName);
		System.out.println("Support (default 20%)    = " + support);
		System.out.println("Confidence (default 80%) = " + confidence);
		System.out.println("Num. classes (Optional)  = " + numClasses);	
		System.out.println();
    }
	
    /* OUTPUT SETTINGS */
    /** Outputs instance field values. */

    protected void outputSettings2() {
		System.out.println("SETTINGS\n--------");
        System.out.println("Number of records        = " + numRows);
		System.out.println("Number of columns        = " + numCols);
		System.out.println("Support (default 20%)    = " + support);
		System.out.println("Confidence (default 80%) = " + confidence);
		System.out.println("Min support              = " + minSupport + " (records)");
		System.out.println("Num one itemsets         = " + numOneItemSets);
		System.out.println("Num. classes (Optional)  = " + numClasses);	
	}
	
    /* -------------------------------------- */
    /* OUTPUT SUPPORT AND CONFIDENCE SETTINGS */
    /* -------------------------------------- */
    /** Outputs current support and confidence settings. */

    public void outputSuppAndConf() {
		System.out.println("Support = " + twoDecPlaces(support) + ", Confidence = " + twoDecPlaces(confidence));
    }

    /* --------------------------------- */
    /*                                   */
    /*        DIAGNOSTIC OUTPUT          */
    /*                                   */
    /* --------------------------------- */

    /* OUTPUT DURATION */
    /** Outputs difference between two given times.
    @param time1 the first time.
    @param time2 the second time.
    @return duration. */

    public double outputDuration(double time1, double time2) {
        double duration = (time2-time1) / 1000;
		System.out.println("Generation time = " + duration + " seconds (" + (duration/60) + " mins)");
		
		// Return
		return(duration);
	}

    /* GET DURATION */
    /** Returns the difference between two given times as a string.
    @param time1 the first time.
    @param time2 the second time.
    @return the difference between the given times as a string. */

    protected String getDuration(double time1, double time2) {
        double duration = (time2-time1) / 1000;
    	return("Generation time = " + duration + " seconds (" + (duration / 60) + " mins)");
	}
    
    /* -------------------------------- */ 
    /*                                  */ 
    /*        SIMILARITY UTILITIES      */ 
    /*                                  */ 
    /* -------------------------------- */ 
 
    /* SIMILAR 2 DFECIMAL PLACES */ 
 
    /* Compares two real numbers and returns true if the two numbers are 
    the same within two decimal places. 
    @param the first given real number. 
    @param the second given number. 
    @return true if similar within two decimal places ad false otherwise. */ 
 
    protected boolean similar2dec(double number1, double number2) { 
        // Convert to integers 
        int numInt1 = (int) ((number1+0.005)*100.0); 
        int numInt2 = (int) ((number2+0.005)*100.0); 
 
        // Compare and return 
        if (numInt1 == numInt2) return(true); 
        else return(false); 
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

    /* THREE DECIMAL PLACES */

    /** Converts given real number to real number rounded up to three decimal
    places.
    @param number the given number.
    @return the number to two decimal places. */

    protected double threeDecPlaces(double number) {
		int numInt = (int) ((number+0.0005)*1000.0);
		number = ((double) numInt)/1000.0;
		return(number);
	}	
}

