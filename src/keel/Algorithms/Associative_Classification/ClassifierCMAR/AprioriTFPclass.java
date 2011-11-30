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
/*                            Apriori-TFP CLASSIFIER                          */
/*                                                                            */
/*                                Frans Coenen                                */
/*                                                                            */
/*                           Monday 2 February 2004                           */
/*                                                                            */
/*                       Department of Computer Science                       */
/*                        The University of Liverpool                         */
/*                                                                            */
/* -------------------------------------------------------------------------- */

/* Class structure

AssocRuleMining
      |
      +-- TotalSupportTree
                |
		+-- PartialSupportTree
			  |
			  +-- AprioriTFPclass		*/

// Java packages

package keel.Algorithms.Associative_Classification.ClassifierCMAR;

import java.util.*;			      
import java.io.*;

/**
 * Methods to produce classification rules using a Apriori-T appraoch. Assumes 
that input dataset is orgnised such that classifiers are at the end of each 
record. Note: number of classifiers value is stored in the <TT>numClasses</TT> 
field.
 *
 * @author Frans Coenen 29 April 2003
 * @author Modified by Jesus Alcala (University of Granada) 09/02/2010
 * @author Modified by Sergio Almecija (University of Granada) 23/05/2010
 * @version 1.0
 * @since JDK1.5
 */
public class AprioriTFPclass extends PartialSupportTree {

    /* ------ FIELDS ------ */
    
    // Data structures
    
    /** 2-D array to hold the test data <P> Note that classifiaction
    involves producing a set of Classification Rules (CRs) from a training
    set and then testing the effectiveness of the CRs on a test set. */
    protected short[][] testDataArray = null;
    /** 3-data array to hold 10th sets of input data. <P> Used in
    conjunction with "10 Cross Validation" where the input data is divided
    into 10 sunsets and CRs are produced using each subset in turn and validated
    against the remaininmg 9 sets. The oveerall average accuracy is then the
    total accuracy divided by 10. */
    protected short[][][] tenthDataSets = new short[10][][];
    
    // Other fields
    /** Number of rows in input data set, not the same as the number of rows
    in the classification training set. <P> Used for temporery storage of total
    number of rows when using Ten Cross Validation (TCV) approach only. <P> The 
    <TT>numRows</TT> field inherited from the super class records is used 
    throughout the CR generation process. Set to number of rows using
    <TT>setNumRowsInInputSet</TT> method called by application class. */
    protected int numRowsInInputSet;
    /** Number of rows in test set, again not the same as the number of rows
    in the classification training set. */
    protected int numRowsInTestSet;
    /** Number of rows in training set, also not the same as the number of rows
    in the classification training set. */
    protected int numRowsInTrainingSet;
    /** Percentage describing classification accuarcy. */
    protected double accuracy;
    
    // Diagnostic fields
    /** Average accuracy as the result of TCV. */
    protected double averageAccuracy; 
    /** Average number of frequent sets as the result of TCV. */
    protected double averageNumFreqSets;
    /** Average number of updates as the result of TCV. */
    protected double averageNumUpdates; 
    /** Average accuracy number of callsification rules as the result of TCV. */
    protected double averageNumCRs; 
    
    /* ------ CONSTRUCTORS ------ */

    /** 
     * Processes command line arguments.
     * @param minConf double Minimum confidence threshold
     * @param minSup double Minimum support threshold
     * @param delta int Minimum coverage threshold
     */
    public AprioriTFPclass(double minConf, double minSup, int delta) {
		super(minConf, minSup, delta);
	}
	
    /* ------ METHODS ------ */
    	
    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*                        COMMAND LINE ARGUMENTS                    */
    /*                                                                  */
    /* ---------------------------------------------------------------- */
    
	
    /* SET SUPPORT AND CONFIDENCE */	
    
    /** Sets new values for the support and confidence fields. 
    @param newSupport the new support value.
    @param newConfidence the new confidence value. */
    
    public void setSupportAndConfidence(double newSupport, double newConfidence) {
        support    = newSupport;
		confidence = newConfidence;
	}
	
    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*                      DATA SET UTILITIES                          */
    /*                                                                  */
    /* ---------------------------------------------------------------- */
    
    /* REORDER INPUT DATA: */
    
    /** Reorders input data according to frequency of single attributes but
    excluding classifiers which are left unordered at the end of the attribute
    list. <P> Overides method in <TT>AssocRuleMining</TT> class. Note
    reordering makes for more efficient executuion of the T-tree (and P-tree)
    algorithms.
    * @param dataBase DataBase Class to store the examples to work with the algorithm and some other useful information 
    */
    
    public void idInputDataOrdering(DataBase dataBase) {	
	// Count singles and store in countArray;
	int[][] countArray = countSingles(dataBase);
	
	// Bubble sort count array on support value (second index)
	orderFirstNofCountArray(countArray,numCols-numClasses);
	
        // Define conversion and reconversion arrays
        defConvertArrays(countArray);
	currentRlist.setReconversionArrayRefs(conversionArray,reconversionArray);
	}
    
    /* PRUNE UNSUPPORTED ATTRIBUTES */
    
    /** Removes single attributes (not classifiers) from input data set which 
    do not meet the minimum support requirement. */
    
    public void pruneUnsupportedAtts() {
        short[] itemSet;
	int attribute;	
	
	// Step through data array using loop construct
	
        for(int rowIndex=0;rowIndex<dataArray.length;rowIndex++) {
	    // Check for empty row
	    if (dataArray[rowIndex]!= null) {	    
	        itemSet = null;
	        // For each attribute in the current record (not the classifier)
		// find if supported with reference to the conversion array. If 
		// so add to "itemSet".
		int maxLength = dataArray[rowIndex].length-1;
	    	for(int colIndex=0;colIndex<maxLength;colIndex++) {
	            attribute = dataArray[rowIndex][colIndex];
		    // Check support
		    if (conversionArray[attribute][1] >= minSupport) {
		        itemSet = reallocInsert(itemSet,
		    			(short) conversionArray[attribute][0]);
		        }
		    }
		// Add classifier
		itemSet = reallocInsert(itemSet,
					dataArray[rowIndex][maxLength]);
	        // Return new item set to data array	  
	        dataArray[rowIndex] = itemSet;
	 	}
	    }
	
	// Adjust classifiers
	recastClassifiers(); 	
	// Reset number of one item sets field
	numOneItemSets = getNumSupOneItemSets();
	}
	
    /* RECAST CLASSIFIERS */
    
    /** Adjusts classifier IDs in data array where attributes have been pruned
    using <TT>pruneUnsupportedAtts</TT> method. <P> Proceeds by looping
    through data table and subtracting a value equal to the number of removed
    attributes from the value of the last element (the classifier) in each 
    record. */
    
    private void recastClassifiers() {
        short difference = (short) (numCols-getNumSupOneItemSets());	
        // Step through data array using loop construct
	int lastIndex;
        for(int rowIndex=0;rowIndex<dataArray.length;rowIndex++) {
	    lastIndex = dataArray[rowIndex].length-1;
	    dataArray[rowIndex][lastIndex] = 
	    		  (short) (dataArray[rowIndex][lastIndex]-difference); 
	    }
	}
	
    /* GET NUM OF SUPPORTE ONE ITEM SETS */    
    /** Gets number of supported attributess (note this is not necessarily
    the same as the number of columns/attributes in the input set) plus the
    number of classifiers. <P> Overides parent method which returns the number
    of support 1 itemsets. This would exclude any classifiers whose support
    value was below the minimum support threshold.
    @return Number of supported 1-item stes */
    
    protected int getNumSupOneItemSets() {
        int counter = 0;
	
	// Step through conversion array incrementing counter for each 
	// supported element found
	
	int length = conversionArray.length-numClasses;
	for (int index=1;index < length;index++) {
	    if (conversionArray[index][1] >= minSupport) counter++;
	    }
	
	// Return
	
	return(counter+numClasses);
	}	
		
    /* CREATE TRAINING AND TEST DATA SETS. */

    /** Populates test and training datasets. <P> Note: (1) assumes a 50:50 
    split, (2) training data set is stored in the dataArray structure in which 
    the input data is stored, (3) method called from application class as same 
    training and test sets may be required if using (say) "hill climbing" 
    approach to maximise accuracy, (4) method is not called from constructor
    partly for same reason as 3 but also because the input data set may (given 
    a particular application) first require ordering and possibly also pruning
    and recasting (see recastClassifiers method). 
    * @param test myDataset Class where examples are stored to build the classifier
    * @param dataBase DataBase Class to store the examples to work with the algorithm and some other useful information
    */
    	
    public void testDataSet(myDataset test, DataBase dataBase) {
		int i, j, k;
		int [] example;
		short value;

		// Determine size of training and test sets.
		setNumRowsInTrainingSet(); 
		numRowsInTestSet = test.getnData();
		
		// Dimension and populate test set
		testDataArray = new short[numRowsInTestSet][];
		for (i=0; i < numRowsInTestSet; i++) {
			example = test.getExample(i);
			testDataArray[i] = new short[dataBase.numVariablesUsed() + 1];
			value = 1;
			for (j=0, k=0; j < example.length; j++) {
				if (dataBase.numLabels(j) > 1) {
					testDataArray[i][k] = (short) example[j];
					testDataArray[i][k] += value;
					value += dataBase.numLabels(j);
//					System.out.print (testDataArray[i][k] + " ");
					k++;
				}
			}
			testDataArray[i][k] = (short) test.getOutputAsInteger(i);
			testDataArray[i][k] += value;
//			System.out.print (testDataArray[i][k] + " ");
//			System.out.println ("");
		}
	}

    /* CREATE TRAINING AND TEST DATA SETS. */

    /** Populates test and training datasets. <P> Note: (1) assumes a 50:50 
    split, (2) training data set is stored in the dataArray structure in which 
    the input data is stored, (3) method called from application class as same 
    training and test sets may be required if using (say) "hill climbing" 
    approach to maximise accuracy, (4) method is not called from constructor
    partly for same reason as 3 but also because the input data set may (given 
    a particular application) first require ordering and possibly also pruning
    and recasting (see recastClassifiers method). */
    	
    public void createTrainingAndTestDataSets() {
		int index1, index2;
		
		// Determine size of training and test sets.
		final double PERCENTAGE_SIZE_OF_TEST_SET = 50.0;
		numRowsInTestSet = (int) ((double) numRows * PERCENTAGE_SIZE_OF_TEST_SET / 100.0);
		numRowsInTrainingSet = numRows-numRowsInTestSet;
		numRows = numRowsInTrainingSet;
		
		// Dimension and populate training set. 
		short[][] trainingSet = new short[numRowsInTrainingSet][];
		for (index1=0; index1 < numRowsInTrainingSet; index1++)  trainingSet[index1] = dataArray[index1];
		
		// Dimension and populate test set
		testDataArray = new short[numRowsInTestSet][];
		for (index2=0; index1 < dataArray.length; index1++, index2++)  testDataArray[index2] = dataArray[index1];
		
		// Assign training set label to input data set label.
		dataArray = trainingSet;   
	}


    /** Populates test and training datasets. <P> Note: (1) works on a 9:1
    split with nine of the tenths data sets forming the training set and
    the remaining one tenth the test set, (2) training data set is stored in 
    the same dataArray structure in which the initial input data is stored,
    (3) this method is not called from the constructor as the input data set may
    (given a particular application) first require ordering and possibly also 
    pruning. 
    @param testSetIndex the index of the tenths data sets to be used as the 
    test set. */
    	
    public void createTrainingAndTestDataSets(int testSetIndex) {
        // Dimension and populate test set.
		numRowsInTestSet = tenthDataSets[testSetIndex].length;
		testDataArray    = tenthDataSets[testSetIndex];
		// Dimension of and populate training set.
		numRowsInTrainingSet = numRowsInInputSet-numRowsInTestSet;
		numRows              = numRowsInTrainingSet;
		short[][] trainingSet = new short[numRows][];
		int trainingSetIndex=0;	
		// Before test set
		for(int index=0;index<testSetIndex;index++) {
			for (int tenthsIndex=0;tenthsIndex<tenthDataSets[index].length; tenthsIndex++,trainingSetIndex++) {
				trainingSet[trainingSetIndex] = tenthDataSets[index][tenthsIndex];
	        }
	    }    	
	// After test set
	for(int index=testSetIndex+1;index<tenthDataSets.length;index++) {
	    for (int tenthsIndex=0;tenthsIndex<tenthDataSets[index].length; tenthsIndex++,trainingSetIndex++) {
			trainingSet[trainingSetIndex] =  tenthDataSets[index][tenthsIndex];
	        }
	    }
	
	// Assign training set label to input data set label.
	dataArray = trainingSet;   
	}
	
    /* CREATE TENTHS DATA SETS. */

    /** Populates ten tenths data sets for use when doing Ten Cross Validation
    (TCV) --- test and training datasets. <P> Note: this method is not called 
    from the constructor as the input data set may (given a particular 
    application) first require ordering (and possibly also pruning!). */
    	
    public void createTenthsDataSets() {
		int index;

		// If number of rows is less than 10 cannot create appropriate data 
		// sets
		if (numRows<10) {
			System.out.println("ERROR: only " + numRows + ", therefore cannot create tenths data sets!");
			System.exit(1);
	    }
		
		// Determine size of first nine tenths data sets.
		int tenthSize = numRows / 10;

		// Dimension first nine tenths data sets.
		for(index=0; index < tenthDataSets.length-1; index++)  tenthDataSets[index] = new short[tenthSize][];
		
		// Dimension last tenths data set
		tenthDataSets[index] = new short[numRows-(tenthSize*9)][];
		
		// Populate tenth data sets
		int inputDataIndex=0;
		for(index=0; index < tenthDataSets.length; index++) {
			for(int tenthIndex=0; tenthIndex < tenthDataSets[index].length; tenthIndex++, inputDataIndex++) {
				tenthDataSets[index][tenthIndex] = dataArray[inputDataIndex];
			}
	    }
	}

    /* RECONSTRUCT INPUT DATA */

    /** Reconstructs the input data set by appending the test set to the
    training sets. <P> Note that the training set is stored in the dataArray
    2-D short array. */

    public void reconstructInputData() {
        // Dimension new data sets
        short[][] newDataArray = new short[dataArray.length+
        					testDataArray.length][];

        // Start populating newDataSet with training (data array) set
        int newIndex=0;
        for (int index=0;index<dataArray.length;index++) {
            newDataArray[newIndex] = dataArray[index];
            newIndex++;
            }

        // Complete populating bewData set with test set
        for (int index=0;index<testDataArray.length;index++) {
            newDataArray[newIndex] = testDataArray[index];
            newIndex++;
            }
        // Assign local reference to global reference
        dataArray=newDataArray;
        }
        		
    /*------------------------------------------------------------------- */
    /*                                                                    */
    /*                             SET METHODS                            */
    /*                                                                    */
    /*------------------------------------------------------------------- */
    
    /* SET NUM ROWS IN INPUT SET */
    /** Assigns value to the <TT>numRowsInInputSet</TT> field. <P> used in
    conjunction with TCV to "remember" the overall number of rows in the 
    input data set. <P> Usually called from application classes. */
    
    public void setNumRowsInInputSet() {
        numRowsInInputSet = numRows;
	}

    /* SET BUM ROWS IN TRAINING SET */
    /** Assigns a value equavalent to the number of rows to the number of
    rows in training set field. <P> used when the entire data set is
    considered as the training set. */

    public void setNumRowsInTrainingSet() {
        numRowsInTrainingSet = numRows;
	}
	
    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*                             GET METHODS                          */
    /*                                                                  */
    /* ---------------------------------------------------------------- */
    
    /* GET AVERAGE ACCURACY */
    /** Gets value for average accuracy field.
    @return average accuracy. */
   
    public double getAverageAccuracy() {
        return(averageAccuracy);
	}
	
    /* GET ACCURACY */
    /** Gets the value of the <TT>accuracyt</TT> field. 
    @return the accuracy value (%). */
    
    public double getAccuracy() {
        return(accuracy);
	}	
    
    /* GET AVERAGE NUMBER OF FREQUENT SETS */
    /** Gets value for average umber of frequent sets field.
    @return average number of frequent sets. */
   
    public double getAverageNumFreqSets() {
        return(averageNumFreqSets);
	}
    
    /* GET AVERAGE NUMBER OF UPDATES */
    /** Gets value for average number of updates field.
    @return average number of updates. */
   
    public double getAvergaeNumUpdates() {
        return(averageNumUpdates);
	}
    
    /* GET AVERAGE NUMBER OF CLASSIFICATION RULES */
    /** Gets value for average number of generated classification rules field.
    @return average number of classification rules. */
   
    public double getAverageNumCRs() {
        return(averageNumCRs);
	}
	
    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*                               OUTPUT                             */
    /*                                                                  */
    /* ---------------------------------------------------------------- */
        
    /* OUTPUT MENU */
    
    /** Outputs menu for command line arguments. (Overides higher level method)
    */
    
    protected void outputMenu() {
        System.out.println();
	System.out.println("-A  = Number of attribute");
	System.out.println("-C  = Confidence (default 80%)");
	System.out.println("-F  = File name");	
	System.out.println("-R  = Number of records");
	System.out.println("-S  = Support (default 20%)"); 
	System.out.println("-N  = Number of classes"); 
	System.out.println();
	
	// Exit
	
	System.exit(1);
	}
    
    /* OUTPUT SETTINGS */
    
    /** Outputs command line values provided by user. (Overides higher level 
    method.) */
    
    protected void outputSettings() {
        System.out.println("SETTINGS\n--------");
	System.out.println("File name                     = " + fileName);	
	System.out.println("Support (default 20%)         = " + support); 
	System.out.println("Confidence (default 80%)      = " + confidence);
	System.out.println("Number of classes             = " + numClasses);
	System.out.println();
        }
    
    /* OUTPUT NUMBER OF CLASSES */
    
    /** Outputs number of classes. */
    
    public void outputNumClasses() {
        System.out.println("Number of classes = " + numClasses);
	}
    
    /* OUTPUT ACCURACY */
    
    /** Outputs classification accuracy. */
    
    public void outputAccuracy() {
        System.out.println("Accuracy = " + twoDecPlaces(accuracy));
	}
    
    /* OUTPUT TEST DATA TABLE */
    
    /** Outputs stored input data set read from input data file. */
     
    public void outputTestDataArray() {
        for(int index=0;index<testDataArray.length;index++) {
	    outputItemSet(testDataArray[index]);
	    System.out.println();
	    }
	}
    }
    

