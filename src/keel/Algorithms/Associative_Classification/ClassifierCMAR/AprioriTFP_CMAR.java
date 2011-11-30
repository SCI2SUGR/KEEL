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
/*                             APRIORI-TFP CMAR                               */
/*              (CLASSIFICATION BASED ON MULTIPLE ASSOCIATION RULES)          */
/*                                                                            */
/*                               Frans Coenen                                 */
/*                                                                            */
/*                             Tuesday 2 March 2004                           */
/*                            (Bug fixes: 7/2/2005)			      */
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
			+--AprioriTFPclass
				|
				+-- AprioriTFP_CMAR		*/

// Java packages

package keel.Algorithms.Associative_Classification.ClassifierCMAR;

import java.util.*;
import java.io.*;

/**
 * Methods to produce classification rules using Wenmin Li, Jiawei Han and
Jian Pei's CMAR (Classification based on Multiple associate Rules) algorithm
but founded on Apriori-TFP. Assumes that input dataset is orgnised such that
classifiers are at the end of each record. Note: number of classifiers value is
stored in the <TT>numClasses</TT> field.
 *
 *
 *
 * @author Frans Coenen 2 March 2004
 * @author Modified by Jesus Alcala (University of Granada) 09/02/2010
 * @author Modified by Sergio Almecija (University of Granada) 23/05/2010
 * @version 1.0
 * @since JDK1.5
 */
public class AprioriTFP_CMAR extends AprioriTFPclass {

    /* ------ FIELDS ------ */

    // CONSTANTS
    /** The maximum number of CARs */
    private final int MAX_NUM_CARS = 1000000;

    // OTHER FIELDS
    /** The number of CARs generated so far. */
    private int numCarsSoFar = 0;


    /* ------ CONSTRUCTORS ------ */

    /** 
     * Processes command line arguments.
     * @param minConf Minimum confidence threshold
     * @param minSup Minimum support threshold
     * @param delta Minimum coverage threshold
     */
    public AprioriTFP_CMAR(double minConf, double minSup, int delta) {
		super(minConf, minSup, delta);
	}

    /* ------ METHODS ------ */

    /* START CMAR CLASSIFICATION */

    /** Starts CMAR classifier generation proces. <P> Proceeds as follows:<OL>
    <LI>Generate all CARs using Apriori-TFP and place selected CARs into linked
    list of rules.
    <LI>Prune list according the cover stratgey.
    <LI>Test classification using Chi-Squared Weighting approach.</OL>
    */

    public void startCMARclassification() {
        System.out.println("START APRIORI-TFP CMAR\n" + "--------------------------");
		
		// Generate all CARs using Apriori-TFP and place selected CARs into
        // linked list of rules.
        startCARgeneration();

        // Prune linked list of rules using "cover" principal
        currentRlist.outputNumCMARrules();
		
		System.out.println("prune CARS");
		currentRlist.pruneUsingCover(copyItemSet(dataArray));
		
		// Test classification using the test set.
//		return(testClassification());
    }

    /** Starts CMAR classifier generation proces (version with full output).
    <P> Proceeds as follows:<OL>
    <LI>Generate all CARs using Apriori-TFP and place selected CARs into linked
    list of rules.
    <LI>Prune list according the cover stratgey.
    <LI>Test classification using Chi-Squared Weighting approach.</OL>
    @return The classification accuracy (%).	*/

    public double startCMARclassificationWithOutput() {
        System.out.println("START APRIORI-TFP CMAR\n" + "--------------------------");
		
		// Generate all CARs using Apriori-TFP and place selected CARs into
        // linked list of rules.
        startCARgeneration();

        // Prune linked list of rules using "cover" principal
        currentRlist.outputNumCMARrules();
		
		System.out.println("prune CARS");
		currentRlist.pruneUsingCover(copyItemSet(dataArray));
		
		// Test classification using the test set.
		return(testClassificationWithOutput());
    }

    /** Commences process of genertaing CARS using apriori TFP. <P> For each
    rule generated add to rule list if: (i) Chi-Squared value is
    above a specified critical threshold (5% by default), and (ii) the CR tree
    does not contain a more general rule with a higher ordering. Rule added to
    rule list according to ranking (ordering). */

    private void startCARgeneration() {
        // Calculate minimum support threshold in terms of number of
        // records in the training set.
		outputSuppAndConf();
		minSupport = numRowsInTrainingSet * support / 100.0;
		System.out.println("Num rows in training set = " + numRows + ", reduced minimum support = " + minSupport);
		
		currentRlist.setNumRows(numRows);
		currentRlist.setNumClasses(numClasses);
		currentRlist.setNumOneItemSets(numOneItemSets);
		
		// Set rule list to null. Note that startRuleList is defined in the
		// AssocRuleMining parent class and is also used to store Association
		// Rules (ARS) with respect ARM.
		currentRlist.startRulelist = null;
		numCarsSoFar = 0;
		
		// Create P-tree
		createPtree();
		
		// Generate T-tree and generate CARS
		createTotalSupportTree();
	}

    /*----------------------------------------------------------------------- */
    /*                                                                        */
    /*             APRIORI-TFP CMAR WITH TEN CROSS VALIDATION (TCV)           */
    /*                                                                        */
    /*----------------------------------------------------------------------- */

    /* COMMEMCE TEN CROSS VALIDATION WITH OUTPUT */

    /** Start Ten Cross Validation (TCV) process with output of individual
    accuracies. */
/*
    public void commenceTCVwithFullOutput() {
        double[][] parameters = new double[10][4];

	System.out.println("START TCV APRIORI-TFP CMAR CLASSIFICATION\n" +
	                    	"------------------------------------");

	// Loop through tenths data sets
	for (int index=0;index<10;index++) {
	    System.out.println("[--- " + index + " ---]");
	    // Create training and test sets
	    createTrainingAndTestDataSets(index);
	    // Set global rule list reference to null
	    currentRlist.setStartCMARrulelistToNull();
	    // Mine data, produce T-tree and generate CRs
	    parameters[index][0] = startCMARclassificationWithOutput();
	    // Output and storage
	    currentRlist.outputNumCMARrules();
	    currentRlist.outputCMARrules();
	    parameters[index][1] = countNumFreqSets();
	    parameters[index][2] = numUpdates;
	    parameters[index][3] = currentRlist.getNumCMAR_CRs();
	    }

	// Determine totals
	double totalAccu        = 0;
	double totalNumFreqSets = 0;
	double totalNumUpdates  = 0;
	double totalNumCRs      = 0;
	System.out.println("---------------------------------------");
	for (int index=0;index<parameters.length;index++) {
	    System.out.println("(" + (index+1) + ") Accuracy = " +
	    	twoDecPlaces(parameters[index][0]) + ", Num. Freq. Sets = " +
	    	twoDecPlaces(parameters[index][1]) + ", Num Updates = " +
		twoDecPlaces(parameters[index][2]) + ", Num CRs = " +
		twoDecPlaces(parameters[index][3]));
	    // Totals
	    totalAccu        = totalAccu+parameters[index][0];
	    totalNumFreqSets = totalNumFreqSets+parameters[index][1];
	    totalNumUpdates  = totalNumUpdates+parameters[index][2];
	    totalNumCRs      = totalNumCRs+parameters[index][3];
	    }
	// Calculate averages
	averageAccuracy    = totalAccu/10;
        averageNumFreqSets = totalNumFreqSets/10;
    	averageNumUpdates  = totalNumUpdates/10;
    	averageNumCRs      = totalNumCRs/10;
	// Output avergaes
	System.out.println("---------------------------------------");
	System.out.println("Average Accuracy = " +
	    twoDecPlaces(averageAccuracy) + ", Num. Freq. Sets = " +
	    twoDecPlaces(averageNumFreqSets) + ", Average Num Updates = " +
	    twoDecPlaces(averageNumUpdates) + ", Average Num CRs = " +
	    twoDecPlaces(averageNumCRs));
	System.out.println("========================================");
	}
*/	          /* COMMEMCE TEN CROSS VALIDATION WITH OUTPUT */

    /** Start Ten Cross Validation (TCV) process with output of individual
    accuracies. */
/*
    public void commenceTCVwithOutput() {
        double[][] parameters = new double[10][4];

	System.out.println("START TCV APRIORI-TFP CMAR CLASSIFICATION\n" +
	                    	"------------------------------------");

	// Loop through tenths data sets
	for (int index=0;index<10;index++) {
	    System.out.println("[--- " + index + " ---]");
	    // Create training and test sets
	    createTrainingAndTestDataSets(index);
	    // Set global rule list reference to null
	    currentRlist.setStartCMARrulelistToNull();
	    // Mine data, produce T-tree and generate CRs
	    parameters[index][0] = startCMARclassification();
	    // Output and storage
	    currentRlist.outputNumCMARrules();
	    parameters[index][1] = countNumFreqSets();
	    parameters[index][2] = numUpdates;
	    parameters[index][3] = currentRlist.getNumCMAR_CRs();
	    }

	// Determine totals
	double totalAccu        = 0;
	double totalNumFreqSets = 0;
	double totalNumUpdates  = 0;
	double totalNumCRs      = 0;
	for (int index=0;index<parameters.length;index++) {
	    System.out.println("(" + (index+1) + ") Accuracy = " +
	    	twoDecPlaces(parameters[index][0]) + ", Num. Freq. Sets = " +
	    	twoDecPlaces(parameters[index][1]) + ", Num Updates = " +
		twoDecPlaces(parameters[index][2]) + ", Num CRs = " +
		twoDecPlaces(parameters[index][3]));
	    // Totals
	    totalAccu        = totalAccu+parameters[index][0];
	    totalNumFreqSets = totalNumFreqSets+parameters[index][1];
	    totalNumUpdates  = totalNumUpdates+parameters[index][2];
	    totalNumCRs      = totalNumCRs+parameters[index][3];
	    }
	// Calculate averages
	averageAccuracy    = totalAccu/10;
        averageNumFreqSets = totalNumFreqSets/10;
    	averageNumUpdates  = totalNumUpdates/10;
    	averageNumCRs      = totalNumCRs/10;
	// Output avergaes
	System.out.println("---------------------------------------");
	System.out.println("Average Accuracy = " +
	    twoDecPlaces(averageAccuracy) + ", Num. Freq. Sets = " +
	    twoDecPlaces(averageNumFreqSets) + ", Average Num Updates = " +
	    twoDecPlaces(averageNumUpdates) + ", Average Num CRs = " +
	    twoDecPlaces(averageNumCRs));
	}
*/
    /*----------------------------------------------------------------------- */
    /*                                                                        */
    /*            CLASSIFICATION ASSOCIATION RULE (CAR) GENERATION            */
    /*                                                                        */
    /*----------------------------------------------------------------------- */

    /* GENERATE CLASSIFICATION ASSOCIATION RULES */

    /** Initiates process of generating Classification Association Rules (CARS),
    Loops through top level of T-tree as part of the CAR generation process.
    <P>CARs differ from ARs in that they have only a single consequent and that
    the number of admissable consequents is limited. Note that classifiers are
    assumed to be listed at the end of the attribute list.
    @param start the identification number of the first classifier to be
    considered. */

    private void generateCARs(int level) {
		// Loop
		for (int index=numOneItemSets-numClasses+1; index<=numOneItemSets; index++) {
			if (startTtreeRef[index]!=null && startTtreeRef[index].childRef!=null) {
				if (startTtreeRef[index].support >= minSupport) {
					short[] consequent = new short[1];
					consequent[0] = (short) index;
					generateCARs(null,index,level-1,consequent, startTtreeRef[index].childRef);
				}
			}
	    }
	}

    /* GENERATE CLASSIFICATION ASSOCIATION RULES */

    /** Continues process of generating classificationh association rules from
    a T-tree by recursively looping through T-tree level by level.
    @param itemSetSofar the label for a T-treenode as generated sofar.
    @param size the length/size of the current array lavel in the T-tree.
    @param level the current level in the T-tree
    @param consequent the current consequent (classifier) for the CAR.
    @param linkRef the reference to the current array lavel in the T-tree. */

    protected void generateCARs(short[] itemSetSofar, int size, int level, short[] consequent, TtreeNode[] linkRef) {
		// If no more nodes return
		if (linkRef == null) return;
		
		// Check number of CARS generated so far
/*		if (numCarsSoFar>MAX_NUM_CARS) {
			System.out.println("Number of CARs (" + numCarsSoFar + ") generted so far exceeds limit of " + MAX_NUM_CARS + ", generation process stopped!");
			return;
		}
*/
		// At right level
		if (level==1) {
			for (int index=1; index < size; index++) {
				// Check if node exists
				if (linkRef[index] != null) {
					// Generate Antecedent
					short[] tempItemSet = realloc2(itemSetSofar,(short) index);
					// Determine confidence
					double suppForAntecedent = (double) getSupportForItemSetInTtree(tempItemSet);
					double confidenceForCAR = getConfidence(suppForAntecedent, linkRef[index].support);
					// Add CAR to linked list structure if confidence greater
					// than minimum confidence threshold.
					if (confidenceForCAR >= confidence) {
						numCarsSoFar++;
						double suppForConcequent = (double) getSupportForItemSetInTtree(consequent);
						currentRlist.insertRinRlistCMARranking(tempItemSet, consequent,suppForAntecedent,suppForConcequent, linkRef[index].support,confidenceForCAR);
	                }
	            }	   
			}
			return;
	    }
		
		// Wrong level, Otherwise process
		for (int index=1; index < size; index++) {
			// Check if node exists
			if (linkRef[index] != null && linkRef[index].childRef!=null) {
				short[] tempItemSet = realloc2(itemSetSofar,(short) index);
				// Proceed down child branch
				generateCARs(tempItemSet,index,level-1,consequent, linkRef[index].childRef);
			}
	    }
	}

    /*------------------------------------- */
    /*                                      */
    /*            T-TREE METHODS            */
    /*                                      */
    /*------------------------------------- */

    /* CREATE T-TREE LEVEL N */
    /** Commences the process of determining the remaining levels in the T-tree
    (other than the top level), level by level in an "Apriori" manner. <P>
    Follows an add support, prune, generate loop until there are no more levels
    to generate. */

    protected void createTtreeLevelN() {
        int nextLevel=2;
		
		// Loop while a further level exists
		while (nextLevelExists) {
			// Add support
			addSupportToTtreeLevelN(nextLevel);
			// Prune unsupported candidate sets
			pruneLevelN(startTtreeRef,nextLevel);
			// Generate CARs
			generateCARs(nextLevel);
			// Check number of frequent sets generated so far
			if (numFrequentsets>MAX_NUM_FREQUENT_SETS) {
				System.out.println("Number of frequent sets (" + numFrequentsets + ") generted so far " + "exceeds limit of " + MAX_NUM_FREQUENT_SETS + ", generation process stopped!");
				break;
			}
			
			// Attempt to generate next level
			nextLevelExists=false;
			generateLevelN(startTtreeRef,nextLevel,null);
			nextLevel++;
	    }	
		
		//End
		System.out.println("Levels in T-tree = " + nextLevel);
	}

    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*                        TEST CLASSIFICATION                       */
    /*                                                                  */
    /* ---------------------------------------------------------------- */

    /* TEST CLASSIFICATION */

    /** Tests the generated classification rules using test sets and return
    percentage accuracy.
    @param the perecentage accuarcy. */

    private double testClassification() {
		int correctClassCounter = 0;
		int wrongClassCounter   = 0;
		int unclassifiedCounter = 0;
		
		// Check if test data exists, if not return' 0'
		if (testDataArray==null) {
			System.out.println("WARNING: No test data");
			return(0);
	    }

		// Check if any classification rules have been generated, if not
		// return'0'.
		if (currentRlist.startCMARrulelist==null) {
			System.out.println("No classification rules generated!");
			return(0);
	    }	
		
		// Loop through test set
    	for(int index=0; index < testDataArray.length; index++) {
			// Note: classifyRecord methods are contained in the
			// AssocRuleMining class. To calssify without default use
			// classifyRecord, with defualt use classifyRecordDefault.
            short classResult = currentRlist.classifyRecordWCS(testDataArray[index]);
			
			if (classResult==0) unclassifiedCounter++;
			else {
				short classActual = getLastElement(testDataArray[index]);
				if (classResult == classActual) correctClassCounter++;
				else wrongClassCounter++;
			}
	    }
		
		// Calculate abd return classification accuracy
		double accuracy = ((double) correctClassCounter * 100.0 / (double) testDataArray.length);
		System.out.println("Accuracy = " + twoDecPlaces(accuracy) + "%");

		
		// Return
		return(accuracy);
	}

    /** Tests the generated classification rules using test sets and return
    percentage accuracy (version with full output).
    @param the perecentage accuarcy. */

    private double testClassificationWithOutput() {
		int correctClassCounter = 0;
		int wrongClassCounter   = 0;
		int unclassifiedCounter = 0;
		
		// Check if test data exists, if not return' 0'
		if (testDataArray==null) {
			System.out.println("WARNING: No test data");
			return(0);
		}
		
		// Check if any classification rules have been generated, if not
		// return'0'.		
		if (currentRlist.startCMARrulelist==null) {
			System.out.println("No classification rules generated!");
			return(0);
	    }	
		
		// Loop through test set
		for(int index=0; index < testDataArray.length; index++) {
			// Note: classifyRecord methods are contained in the
			// AssocRuleMining class. To calssify without default use
			// classifyRecord, with defualt use classifyRecordDefault.			
			short classResult = currentRlist.classifyRecordWCS(testDataArray[index]);

			if (classResult==0) unclassifiedCounter++;			
			else {
				short classActual = getLastElement(testDataArray[index]);
				if (classResult == classActual) correctClassCounter++;
				else wrongClassCounter++;
			}
	    }	
		
		// Calculate abd return classification accuracy
		double accuracy = ((double) correctClassCounter*100.0/(double) testDataArray.length);
        System.out.println("Correct classifications = " + correctClassCounter);
        System.out.println("unclassified            = " + unclassifiedCounter);
        System.out.println("Wrong classifications   = " + wrongClassCounter);
        System.out.println("Number of test cases    = " + testDataArray.length);
        System.out.println("Accuracy                = " + twoDecPlaces(accuracy) + "%");
		
		// Return
		return(accuracy);
	}
}


