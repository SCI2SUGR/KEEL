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
/*                              RULE LIST                                     */
/*                                                                            */
/*                            Frans Coenen                                    */
/*                                                                            */
/*                         Tuesday 2 March 2004                               */
/*                                                                            */
/*                    Department of Computer Science                          */
/*                     The University of Liverpool                            */
/*                                                                            */ 
/* -------------------------------------------------------------------------- */
/* Class structure

AssocRuleMining
      |
      +-- RuleList			*/

// Java packages
package keel.Algorithms.Associative_Classification.ClassifierCMAR;

import org.core.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 * Set of utilities to support various Association Rule Mining (ARM) 
algorithms included in the LUCS-KDD suite of ARM programs.
 *
 * @author Frans Coenen 2 March 2004
 * @author Modified by Jesus Alcala (University of Granada) 09/02/2010
 * @author Modified by Sergio Almecija (University of Granada) 23/05/2010
 * @version 1.0
 * @since JDK1.5
 */
public class RuleList extends AssocRuleMining {

    /* ------ FIELDS ------ */
	
    // --- Data structures ---
    
    /** Rule node in linked list of rules (either ARs or CRs). */ 
 
    protected class RuleNode { 
		/** Antecedent of AR. */ 
		protected short[] antecedent; 
		/** Consequent of AR. */ 
		protected short[] consequent; 
		/** The confidence value associate with the rule represented by this node. */ 
		double confidenceForRule=0.0; 
		/** Link to next node */ 
		RuleNode next = null; 
		
		/** Three argument constructor 
		@param antecedent the antecedent (LHS) of the AR. 
    	@param consequent the consequent (RHS) of the AR. 
    	@param support the associated confidence value. */
		
		private RuleNode(short[] ante, short[]cons, double confValue) { 
			antecedent        = ante; 
			consequent        = cons; 
			confidenceForRule = confValue; 
		} 
	} 

    /** Rule node in linked list of rules (either ARs or CRs) for CMAR 
    algorithm. */ 
 
    protected class RuleNodeCMAR { 
    	/** Antecedent of AR. */ 
		protected short[] antecedent; 
		/** Consequent of AR. */ 
		protected short[] consequent; 
		/** The confidence value associate with the rule represented by this node. */ 
		double confidenceForRule=0.0; 
		/** The support value associate with the rule represented by this node. */ 
		double supportForRule=0.0; 
		/** The support value associate with the antecedent of the rule represented by this node. */ 
		double suppAntecedent=0.0; 
		/** The support value associate with the consequent of the rule represented by this node. */ 
		double suppConsequent=0.0; 
		/** Link to next node */ 
		RuleNodeCMAR next = null; 
	 	
		/** Six argument constructor 
		@param antecedent the antecedent (LHS) of the AR. 
    	@param consequent the consequent (RHS) of the AR. 
    	@param suppValue the associated support value.  
		@param suppAnte the associated support for the antecedent. 
		@param suppCons the associated support for the consequent. 
		@param comsvalue the associated confidence value.   */ 
	 	
		private RuleNodeCMAR(short[] ante, short[]cons, double suppValue, double suppAnte, double suppCons, double confValue) { 
			antecedent        = ante; 
			consequent        = cons; 
			supportForRule    = suppValue; 
			suppAntecedent    = suppAnte; 
			suppConsequent    = suppCons; 
			confidenceForRule = confValue; 
	    } 
	} 
    
    /** The reference to start of the rule list. */ 
    protected RuleNode startRulelist = null;	
    /** The reference to start of the CMAR rule list. */ 
    protected RuleNodeCMAR startCMARrulelist = null; 
    /** 1-D array for observed values for Chi-Squared Testing. */ 
    private double[] obsValues = new double[4]; 
    /** 1-D array for expected values for Chi-Squared Testing. */ 
    private double[] expValues = new double[4]; 

    // --- Chi-Squared Testing Constants ---     
    /** Critical threshold for 25% "significance" level (assuming "degree of freedom" equivalent to 1). */ 
    private static final double THRESHOLD_25    = 1.3233; 
    /** Critical threshold for 25% "significance" level (assuming "degree of freedom" equivalent to 1). */ 
    private static final double THRESHOLD_20    = 1.6424; 
    /** Critical threshold for 10% "significance" level (assuming "degree of freedom" equivalent to 1). */ 
    private static final double THRESHOLD_10    = 2.7055; 
    /** Critical threshold for 5% "significance" level (assuming "degree of freedom" equivalent to 1). */ 
    private static final double THRESHOLD_5     = 3.8415; 
    /** Critical threshold for 2.5% "significance" level (assuming "degree of freedom" equivalent to 1). */ 
    private static final double THRESHOLD_2HALF = 5.0239; 
    /** Critical threshold for 1% "significance" level (assuming "degree of freedom" equivalent to 1). */ 
    private static final double THRESHOLD_1     = 6.6349; 
    /** Critical threshold for 0.5% "significance" level (assuming "degree of freedom" equivalent to 1). */ 
    private static final double THRESHOLD_HALF  = 7.8794; 
     
    // Cover constant 
    /** Minimum times a record mist be covered */ 
    private static int MIN_COVER = 4; 	// At least 4 rules 
     
    // --- Chi-Squared Fields --- 
    /** Support value for the antecedent of the rule. */ 
    private double supAntecedent; 
    /** Support value for NOT the antecedent of the rule. */ 
    private double supNotAntecedent; 
    /** Support value for the concequent of the rule. */ 
    private double supConsequent; 
    /** Support value for NOT the concequent of the rule. */ 
    private double supNotConsequent; 
    /** Support for the rule. */ 
    private double supRule;	 
    /** Number of records in the input (training) sets. */ 
    private double numRecords; 
    /** Current critical threshold value. */ 
    private double threshold = THRESHOLD_20;		// Default 

    /* ------ CONSTRUCTORS ------ */

    /** Default constructor to create an instance of the class RuleList  */
    	
    public RuleList(int delta) {
		MIN_COVER = delta;
    }
	
    /* ------ METHODS ------ */	
	
    /* ---------------------------------------------------------------- */ 
    /*                                                                  */ 
    /*        RULE LINKED LIST ORDERED ACCORDING TO CMAR RANKING        */ 
    /*                                                                  */ 
    /* ---------------------------------------------------------------- */ 
     
    /* Methods for inserting rules into a linked list of rules ordered  
    according to CMAR ranking. Each rule described in terms of 4 fields: 1)  
    Antecedent (an item set), 2) a consequent (an item set), 3) a total support  
    value and 4) a confidence value (double). */ 
     
    /* INSERT (ASSOCIATION/CLASSIFICATION) RULE INTO RULE LINKED LIST (ORDERED 
    ACCORDING CONFIDENCE). */ 
     
    /** Inserts an (association/classification) rule into the linkedlist of 
    rules pointed at by <TT>startRulelist</TT>. <P> List is ordered according  
    to "CMAR" ranking.  
    @param antecedent the antecedent (LHS) of the rule. 
    @param consequent the consequent (RHS) of the rule. 
    @param supportForAntecedent the associated support for the antecedent. 
    @param supportForConsequent the associated support for the consequent. 
    @param supportForRule the associated support value.  
    @param confidenceForRule the associated confidence value. */ 
     
    protected void insertRinRlistCMARranking(short[] antecedent, short[] consequent, double supportForAntecedent, double supportForConsequent, double supportForRule, double confidenceForRule) {
        // Test rule using Chi-Squared testing 
        if (!testRuleUsingChiSquaredTesting(supportForAntecedent, supportForConsequent,supportForRule,numRows)) return; 

		// Create new node 
		RuleNodeCMAR newNode = new RuleNodeCMAR(antecedent,consequent, supportForRule,supportForAntecedent, supportForConsequent,confidenceForRule); 
		
	    // Empty rule list situation 
		if (startCMARrulelist == null) { 
			startCMARrulelist = newNode; 
			return; 
	    } 	
		
		// Check if more general rule with higher ranking exists.  
		if (moreGeneralRuleExists(newNode)) return; 
		
		// Add new node to start	 
		if (ruleIsCMARgreater(newNode,startCMARrulelist)) { 
			newNode.next = startCMARrulelist; 
			startCMARrulelist  = newNode; 
			return; 
	    } 	
		
		// Add new node to middle 
		RuleNodeCMAR markerNode = startCMARrulelist; 
		RuleNodeCMAR linkRuleNode = startCMARrulelist.next; 
		while (linkRuleNode != null) { 
			if (ruleIsCMARgreater(newNode,linkRuleNode)) { 
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
	 
    /* MORE GENERAL EXISTS */ 
     
    /** Tests whether a more general rule, with higher ranking, already exists  
    in the rule list. 
    @param rule the rule under consideration. 
    @return true if more general rule with higher ranking exists, and false 
    otherwise. */ 
     
    private boolean moreGeneralRuleExists(RuleNodeCMAR rule) { 
        RuleNodeCMAR linkRef = startCMARrulelist; 	
		
		// Loop through list 
		while (linkRef!=null) { 
			if (ruleIsMoreGeneral(rule,linkRef) &&  ruleIsCMARgreater2(rule,linkRef)) return(true); 
			linkRef=linkRef.next; 
	    } 	
		
		// Default return 
		return(false); 
	} 
    	 
    /* RULE IS MORE GENERAL */ 
     
    /** Compares two rules and returns true if the first is a more general rule 
    than the second (has fewer antecedent attributes). 
    @param rule1 the given rule to be compared to the second. 
    @param rule2 the rule which the given rule1 is to be compared to. 
    @return true id rule1 is greater then rule2, and false otherwise. */ 
     
    private boolean ruleIsMoreGeneral(RuleNodeCMAR rule1, RuleNodeCMAR rule2) { 
        if (rule1.antecedent.length < rule2.antecedent.length) return(true); 
		
		// Otherwise return false    
		return(false); 
	}  

	 
    /* RULE IS CMAR GREATER */ 
     
    /** Compares two rules and returns true if the first is "CMAR greater" (has  
    a higher ranking) than the second. <P> CMAR ordering is as follows: 
    <OL> 
    <LI>Confidence, a rule <TT>r1</TT> has priority over a rule <TT>r2</TT> if  
    <TT>confidence(r1) &gt; confidence(r2)</TT>.  
    <LI>Support, a rule <TT>r1</TT> has priority over a rule <TT>r2</TT> if  
    <TT>confidence(r1)==confidence(r2) &amp;&amp; support(r1)&gt;support(r2) 
    </TT>.  
    <LI>Size of antecedent, a rule <TT>r1</TT> has priority over a rule  
    <TT>r2</TT> if <TT>confidence(r1)==confidence(r2) &amp;&amp; 
    support(r1)==spoort(r2) &amp;&amp;|A<SUB>r1</SUB>|&lt;|A<SUB>r2</SUB>| 
    </TT>.  
    </OL> 
    @param rule1 the given rule to be compared to the second. 
    @param rule2 the rule which the given rule1 is to be compared to. 
    @return true id rule1 is greater then rule2, and false otherwise. */ 
     
    private boolean ruleIsCMARgreater(RuleNodeCMAR rule1, RuleNodeCMAR rule2) { 
        // Compare confidences 
		if (rule1.confidenceForRule > rule2.confidenceForRule) return(true); 
		
		// If confidences are the same compare support values 
		if (similar2dec(rule1.confidenceForRule,rule2.confidenceForRule)) { 
			if (rule1.supportForRule > rule2.supportForRule) return(true); 
			// If confidences and supports are the same compare antecedents 
			if (similar2dec(rule1.supportForRule,rule2.supportForRule)) { 
				if (rule1.antecedent.length < rule2.antecedent.length)   return(true); 
			} 
	   } 	
	   
	   // Otherwise return false    
	   return(false); 
	}  
	 
    /* RULE IS CMAR GREATER 2 */ 
    	 
    /** Compares two rules, such that the first id more general than the second, 
    and returns true if the first is "CMAR greater" (has a higher ranking) than  
    the second. <P> Method similar to ruleIsCMARgreater method but with the 
    "more general rule" prerequisite. CMAR ordering (founded on confidence and  
    support only) is as follows: 
    <OL> 
    <LI>Confidence, a rule <TT>r1</TT> has priority over a rule <TT>r2</TT> if  
    <TT>confidence(r1) &gt; confidence(r2)</TT>.  
    <LI>Support, a rule <TT>r1</TT> has priority over a rule <TT>r2</TT> if  
    <TT>confidence(r1)==confidence(r2) &amp;&amp; support(r1)&gt;support(r2) 
    </TT>.
    </OL> 
    @param rule1 the given rule to be compared to the second. 
    @param rule2 the rule which the given rule1 is to be compared to. 
    @return true id rule1 is greater then rule2, and false otherwise. */ 
     
    private boolean ruleIsCMARgreater2(RuleNodeCMAR rule1, RuleNodeCMAR rule2) { 
        // Compare confidences 
		if (rule1.confidenceForRule > rule2.confidenceForRule) return(true); 
		
		// If confidences are the same compare support values 
		if (similar2dec(rule1.confidenceForRule,rule2.confidenceForRule)) { 
			if (rule1.supportForRule > rule2.supportForRule) return(true); 
		} 
		
		// Otherwise return false    
		return(false); 
	}  
	 
    /* -------------------------------------------- */ 
    /*                                              */ 
    /*              CHI SQUARED TESTING             */ 
    /*                                              */ 
    /* -------------------------------------------- */ 
	     
    /* TEST RULE USING CHI SQUARED TESTING */ 
     
    /** Tests a classification rule with the given parameters to determine  
    the interestingness/surprisingness of the rule. 
    @param supA the support value for the antecedent of the rule.  
    @param supC the support value for the consequent of the rule.  
    @param supAC the support for the rule.  
    @param numR the number of records in the input (training) sets.  
    @return true if Chi squared value is above critical threshold and false 
    otherwise.  */ 
     
    public boolean testRuleUsingChiSquaredTesting(double supA, double supC, double supAC, double numR) { 
		// Calculate Chi squared value 
		double chiSquaredValue = getChiSquaredValue(supA,supC,supAC,numR);
		
		// Test Chi Squared value. 
		if (chiSquaredValue>threshold) return(true); 
		else return(false); 
	} 
	 
    /* GET CHI-SQUARED VALUE */ 
         
    /** Calculates and returns the Chi-Squared value for a rule.  
    @param supA the support value for the antecedent of the rule.  
    @param supC the support value for the consequent of the rule.  
    @param supAC the support for the rule.  
    @param numR the number of records in the input (training) sets.  
    @return the Chi squared value.  */ 
     
    private double getChiSquaredValue(double supA, double supC, double supAC, double numR) { 
		// Set values 
		supAntecedent = supA; 
    	supConsequent = supC; 
    	supRule       = supAC;	 
    	numRecords    = numR; 
		
		// Calculate observed and expected values 
		calculateObsValues(); 
		calculateExpValues(); 

		// Calculate and return Chi squared value 
		return(calcChiSquaredValue()); 
	} 
				 
    /* CALCULATE OBSERVED VALUES */ 
     
    /** Calculates observed values for Chi squared testing calculation. */ 
      
    private void calculateObsValues() { 
        obsValues[0] = supRule; 
		obsValues[1] = supAntecedent - supRule; 
		obsValues[2] = supConsequent - supRule; 
		obsValues[3] = numRecords - supAntecedent - supConsequent + supRule; 
		
		// Calculate additional support values 
		supNotAntecedent = numRecords - supAntecedent; 
    	supNotConsequent = numRecords - supConsequent;
	} 
	 
    /* CALIULASTE EXPECTED VALUES */ 
     
    /** Calculates expected values for Chi squared testing calculation. */ 
      
    private void calculateExpValues() { 
        expValues[0] = (supConsequent * supAntecedent) / numRecords; 
		expValues[1] = (supNotConsequent * supAntecedent) / numRecords; 
		expValues[2] = (supConsequent * supNotAntecedent) / numRecords; 
		expValues[3] = (supNotConsequent * supNotAntecedent) / numRecords;
	 } 
	 
    /* CALCULATE CHI SQUARED VALUE */ 
     
    /** Calculates the Chi squared values and returns their sum. 
    @return the sum of the Chi Squared values. */  
     
    private double calcChiSquaredValue() { 
        double sumChiSquaredValues = 0.0; 
		
		for (int index=0;index<obsValues.length;index++) { 
			double chiValue = Math.pow((obsValues[index] - expValues[index]),2.0)/expValues[index]; 
			sumChiSquaredValues = sumChiSquaredValues+chiValue; 
	    } 
		
		// Return
		return(sumChiSquaredValues); 
	} 
		 
    /* ------------------------------------------------------------- */ 
    /*                                                               */ 
    /*                     RULE PRUNING (CMAR)                       */ 
    /*                                                               */ 
    /* ------------------------------------------------------------- */ 
     
    /* PRUNE USING COVER */ 
     
    /** Prunes the current CMAR list of rules according to the "cover"  
    principle. 
    @param trainingSet the input data set.	*/ 
     
    protected void pruneUsingCover(short[][] trainingSet) { 
        // Initialise cover array 
		int[] cover = new int[trainingSet.length]; 
		
		// Define rule list references 
		RuleNodeCMAR newStartRef = null; 
		RuleNodeCMAR markerRef   = null; 
		RuleNodeCMAR linkRef     = startCMARrulelist; 
		
		// Loop through rule list 
		while (linkRef!=null) {
			// If no more training records end 
			if (emptyDataSet(trainingSet)) break; 
			// Set cover flag to false, will be set to true of a rule matches  
			// a record. 
			boolean coverFlag=false; 
			// Loop through training set
			for (int index=0;index<trainingSet.length;index++) { 
				// If record satisfies a rule increment cover element for  
				// record and set cover flag to true to indicate that rule 
				// is required by at least one record 
				if (isSubset(linkRef.antecedent,trainingSet[index])) { 
					cover[index]++; 
					coverFlag=true;  
				} 
			}
			
			// If current rule is required by at least one record (cover flag 
			// set to true) add to new rule list 
			if (coverFlag) { 
				if (newStartRef==null) newStartRef=linkRef; 
				else markerRef.next=linkRef; 
				
				markerRef=linkRef; 
				linkRef=linkRef.next; 
				markerRef.next=null; 
			}	    
			else linkRef=linkRef.next; 
			
			// Remove records from training set if adequately covered 
			for (int index=0;index<cover.length;index++) { 
				if (cover[index]>MIN_COVER) trainingSet[index]=null; 
	        } 
	    }
		
		// Set rule list  
		startCMARrulelist = newStartRef; 
	} 
     
    /* EMPTY DATA SET */ 
     
    /** Tests whether a data set is empty or not (all records set to null).
    @param dataSet the input data set.
    @return true if empty, false othjerwise. */

    private boolean emptyDataSet(short[][] dataSet) {
        // Loop through given data set
        for (int index=0;index<dataSet.length;index++)
			if (dataSet[index]!=null)  return(false);
		
		// Default
		return(true);
	}

    /* ------------------------------------------------------------- */
    /*                                                               */
    /*                         CLASSIFIER                            */
    /*                                                               */
    /* ------------------------------------------------------------- */

    /* CLASSIFY RECORD (USING WEIGHTED CHI SQUARED) */
    /** Selects the best rule in a rule list according to the Weighted Chi-
    Squared (WCS) Value. <P> Proceed as follows: <OL>
    <LI>Collect rules that satisfy the record. if
    <OL type="i">
    <LI>If consequents of all rules are all identical, or
    only one rule, classify record.
    <LI>Else group rules according to classifier and determine the combined
    effect of the rules in each group, the classifier associated with
    the "strongest group" is then selected.
    </OL>
    @param classification the possible classes.
    @param itemset the record to be classified.
    @return the class label (or 0 if no class found).	*/

    protected short classifyRecordWCS(short[] itemSet) {
		RuleNodeCMAR linkRef = startCMARrulelist;
		RuleNodeCMAR tempRulelist = startCMARrulelist;
		startCMARrulelist=null;
		
		// Obtain rules that satisfy record (iremSet)
		obtainallRulesForRecord(linkRef,itemSet);

        // If no rules satisfy record return 0
		if (startCMARrulelist==null) {
			startCMARrulelist = tempRulelist;
			return(0);
	    }
		
		// If only one rule return class
		if (startCMARrulelist.next== null) {
			short answer = startCMARrulelist.consequent[0];
			startCMARrulelist=tempRulelist;
			return(answer);
	    }
		
		// If more than one rule but all have the same class return calss
		if (onlyOneClass()) {
			short answer = startCMARrulelist.consequent[0];
			startCMARrulelist=tempRulelist;
			return(answer);
	    }

		// Group rules
		RuleNodeCMAR[] ruleGroups = groupRules();

		// Determine Weighted Chi-Squared (WCS) Values for each group
		double[] wcsValues = calcWCSvalues(ruleGroups);
		// Select group with best WCS value and return associated label
		short consequent = selectBestWCS(wcsValues);
		// Reset global rule list reference
		startCMARrulelist=tempRulelist;
		
		// Return class
		return(consequent);
	}

    /* GROUP RULES */

    /** Groups rules contained in a linked list of rules pointed at by
    <TT>startCMARrulelist</TT> according to their consequent.
    @return an array of rule groups. */

    private RuleNodeCMAR[] groupRules() {
        // Initialise rule groups data structure
	RuleNodeCMAR[] ruleGroups = new RuleNodeCMAR[numClasses];
	for (int index=0;index<ruleGroups.length;index++)
							ruleGroups[index]=null;
	// Loop through rule list
	RuleNodeCMAR linkRef = startCMARrulelist;
	while (linkRef!=null) {
	   // Identify index for consequent
	   int index = numOneItemSets-linkRef.consequent[0];
           // Add to rule group
	   RuleNodeCMAR ruleCopy = new RuleNodeCMAR(linkRef.antecedent,
	   			     linkRef.consequent,linkRef.supportForRule,
				 linkRef.suppAntecedent,linkRef.suppConsequent,
						    linkRef.confidenceForRule);
	   ruleCopy.next=ruleGroups[index];
	   ruleGroups[index]=ruleCopy;
	   // Increment link reference
	   linkRef=linkRef.next;
	   }

	// Return
	return(ruleGroups);
	}

   /* ONLY ONE CLASS */

   /** Checks whether given rule list consequents all refer to the same class
   or not.
   @return true if identical consequent in all rules, false otherwise. */

   private boolean onlyOneClass() {
       RuleNodeCMAR linkRef = startCMARrulelist;

       // Class for first rule.
       short firstClass = linkRef.consequent[0];

       // loop through rest of list.
       linkRef = linkRef.next;
       while (linkRef!=null) {
           if (linkRef.consequent[0]!=firstClass) return(false);
	   linkRef=linkRef.next;
	   }

       // Default return
       return(true);
       }

    /* CALCULATE WEIGHTED CHI SQUARED VALUE FOR RULE GROUPS */

    /** Determines and returns the weighted Chi Squared values for the groups of
    rules.
    @param ruleGroups the given groups of rule.
    @return array of weighted Chi-Squared value for a set of rule groups */

    private double[] calcWCSvalues(RuleNodeCMAR[] ruleGroups) {
        // Dimension array
	double[] wcsArray = new double[ruleGroups.length];

	for (int index=0;index<ruleGroups.length;index++) {
	    RuleNodeCMAR linkRuleNode = ruleGroups[index];
	    double wcsValue = 0.0;
	    while (linkRuleNode != null) {
		double chiSquaredValue =
				 getChiSquaredValue(linkRuleNode.suppAntecedent,
					linkRuleNode.suppConsequent,
					linkRuleNode.supportForRule,numRecords);
		double chiSquaredUB =
			   calcChiSquaredUpperBound(linkRuleNode.suppAntecedent,
						   linkRuleNode.suppConsequent);
		wcsValue =
		      wcsValue + (chiSquaredValue*chiSquaredValue)/chiSquaredUB;
		linkRuleNode = linkRuleNode.next;
		}
	    wcsArray[index]=wcsValue;
	    }

	// Return
	return(wcsArray);
	}

    /* BEST WCS VALUE */

    /** Determines the best of the given WCS values and returns the consequent
    associated with this bet value.
    @param wcsArray the given array of weighted Chi-Squared value for a set
    of rule groups.
    @return the selected consequent. */

    private short selectBestWCS(double[] wcsValues) {
        double bestValue = wcsValues[0];
	int bestIndex    = 0;

	for (int index=1;index<wcsValues.length;index++) {
	    if (wcsValues[index]>bestValue) {
	        bestValue=wcsValues[index];
		bestIndex=index;
		}
	    }

	// Return
	return((short) (numOneItemSets-bestIndex));
	}

    /* CALCULATE CHI SQUARED VALUE UPPER BOUND */

    /** Claculates the upper bound for the Chi-Squared value of a rule.
    @param suppAnte the support for the antecedent of a rule.
    @param suppCons the support for the consequent of a rule.
    @return the Chi-Squared upper bound. */

    private double calcChiSquaredUpperBound(double suppAnte, double suppCons) {
        double term;

	// Test support for antecedent and confidence and choose minimum
	if (suppAnte<suppCons) term =
		      Math.pow(suppAnte-((suppAnte*suppCons)/numRecords),2.0);

	else term = Math.pow(suppCons-((suppAnte*suppCons)/numRecords),2.0);

	// Determine e
	double eVlaue = calcWCSeValue(suppAnte,suppCons);

	// Rerturn upper bound
	return(term*eVlaue*numRecords);
	}

    /* CALCULATE WCS e VALUE. */

    /** Calculates and returns the e value for calculating Weighted Chi-Squared
    (WCS) values.
    @param suppAnte the support for the antecedent of a rule.
    @param suppCons the support for the consequent of a rule.
    @return the ECS e value. */

    private double calcWCSeValue(double suppAnte, double suppCons) {
        double term1 = 1/(suppAnte*suppCons);
	double term2 = 1/(suppAnte*(numRecords-suppCons));
        double term3 = 1/(suppCons*(numRecords-suppAnte));
	double term4 = 1/((numRecords-suppAnte)*(numRecords-suppCons));

	// Return sum
	return(term1+term2+term3+term4);
	}

    /* ------------------------------------------------------------- */
    /*                 CLASSIFIER  (UTILITY METHODS)                 */
    /* ------------------------------------------------------------- */

    /** Places all rules that satisfy the given record in a CMAR rule linked
    list pointed at by startCMARrulelist field, in the order that rules are
    presented. <P> Used in Weighted Chi-Squared classification (CMAR) algorithm.
    @param linkref The reference to the start of the existing list of rules.
    @param itemset the record to be classified.	*/

    private void obtainallRulesForRecord(RuleNodeCMAR linkRef, short[] itemSet) {
	RuleNodeCMAR newStartRef = null;
	RuleNodeCMAR markerRef   = null;

	// Loop through linked list of existing rules
	while (linkRef!=null) {
	    // If rule satisfies record add to new rule list
	    if (isSubset(linkRef.antecedent,itemSet)) {
	        RuleNodeCMAR newNode = new RuleNodeCMAR(linkRef.antecedent, linkRef.consequent,linkRef.supportForRule, linkRef.suppAntecedent,linkRef.suppConsequent, linkRef.confidenceForRule);
			if (newStartRef==null) newStartRef=newNode;
			else markerRef.next=newNode;
			
			markerRef=newNode;
			/*if (newStartRef==null) newStartRef=linkRef;
			else markerRef.next=linkRef;
			markerRef=linkRef; */
		}
		linkRef=linkRef.next;
	}

	// Set rule list
	startCMARrulelist = newStartRef;
	}


    /* ----------------------------------- */
    /*                                     */
    /*              GET METHODS            */
    /*                                     */
    /* ----------------------------------- */

    /* GET NUMBER OF RULES */

    /**  Returns the number of generated rules (usually used in
    conjunction with classification rule mining algorithms rather than ARM
    algorithms).
    @return the number of CRs. */

    public int getNumCRs() {
        int number = 0;
        RuleNode linkRuleNode = startRulelist;

	// Loop through linked list
	while (linkRuleNode != null) {
	    number++;
	    linkRuleNode = linkRuleNode.next;
	    }

	// Return
	return(number);
	}

    /* GET NUMBER OF CMAR CLASSIFICATION RULES */

    /**  Returns the number of generated CMAR classification rules.
    @return the number of CRs. */

    public int getNumCMAR_CRs() {
        int number = 0;
        RuleNodeCMAR linkRuleNode = startCMARrulelist;
		
		// Loop through linked list
		while (linkRuleNode != null) {
			number++;
			linkRuleNode = linkRuleNode.next;
	    }
		
		// Return
		return(number);
	}

    /* ----------------------------------- */
    /*                                     */
    /*              SET METHODS            */
    /*                                     */
    /* ----------------------------------- */


    /* SET NUMBER OF ROWS */

    /** Sets number of rows field. */

    protected void setNumRows(int numR) {
        numRows=numR;
	}

    /* SET NUMBER OF CLASSES */

    /** Sets number of rows field. */

    protected void setNumClasses(int numC) {
        numClasses=numC;
	}

    /* SET NUMBER OF ONE ITEM SETS */

    /** Sets number of one item sets field. */

    protected void setNumOneItemSets(int nois) {
        numOneItemSets=nois;
	}

    /* SET START CMAR RULE LIST TO NULL. */

    protected void  setStartCMARrulelistToNull() {
        startCMARrulelist = null;
	}

    /* SET DATA ARRAT */

    /** Set 2-D "short" data array reference. */

    /*protected void setDataArray(short[][] dArray) {
        dataArray=dArray;
	}    */

    /* SET RECONVERSION ARRAYS */

    /** Sets the reconversion array reference values.
    @param conversionArrayRef the reference to the 2-D array used to renumber
    coulmns for input data in terms of frequency of single attributes
    (reordering will enhance performance for some ARM and CARM algorithms).
    @param reconversionArrayRef the reference to the 1-D array used to reconvert
    input data column numbers to their original numbering where the input data
    has been ordered to enhance computational efficienvy. */

    protected void setReconversionArrayRefs(int[][] conversionArrayRef,
    					short[] reconversionArrayRef) {
        conversionArray   = conversionArrayRef;
        reconversionArray = reconversionArrayRef;
        }

    /* ------------------------------ */
    /*                                */
    /*              OUTPUT            */
    /*                                */
    /* ------------------------------ */

    /* OUTPUT CMAR RULE LINKED LIST */ 
    /** Outputs contents of CMAR rule linked list (if any) */ 
     
    public void outputCMARrules(String filename) {
		String stringOut = new String("");
		stringOut = outputRules(startCMARrulelist);
		Files.writeFile(filename, stringOut);
	} 
	
    /* OUTPUT CMAR RULE LINKED LIST WITH RECONVERSION */ 
    /** Outputs contents of CMAR rule linked list (if any) */ 
     
    public void outputCMARrulesWithReconversion() { 
        outputRulesWithReconversion(startCMARrulelist); 
	} 
 
    /** Outputs given CMAR rule list. 
    @param ruleList the given rule list. */ 
     
    public String outputRules(RuleNodeCMAR ruleList) { 
		String stringOut = new String("");
		int number, nAnt;

		// Check for empty rule list 
		if (ruleList==null) return ("No rules generated!"); 
		
		// Loop through rule list 
		number = 1;
		nAnt = 0;
		RuleNodeCMAR linkRuleNode = ruleList; 
		while (linkRuleNode != null) {
			nAnt += linkRuleNode.antecedent.length;
			stringOut += ("(" + number + ") "); 
			stringOut += outputRule(linkRuleNode); 
			stringOut += "\n"; 
			stringOut += (" Conf: " +  twoDecPlaces(linkRuleNode.confidenceForRule) + "%, (SuppRule: " + linkRuleNode.supportForRule + ", SuppAnt: " +  linkRuleNode.suppAntecedent + ", SuppCons: " +  linkRuleNode.suppConsequent + ")"); 
			stringOut += "\n\n"; 
			number++; 
			linkRuleNode = linkRuleNode.next; 
	    }

		stringOut += "\n\n";
		stringOut = "@Number of rules: " + getNumCMAR_CRs() + " Number of Antecedents by rule: " + nAnt * 1.0 / getNumCMAR_CRs() + "\n\n" + stringOut;

		return (stringOut);
	} 
	 
    /** Outputs a CMAR rule. 
    @param rule the rule to be output. */ 
     
    private String outputRule(RuleNodeCMAR rule) { 
		String stringOut = new String("");

		stringOut += outputItemSet(rule.antecedent); 
		stringOut += " -> "; 
        stringOut += outputItemSet(rule.consequent); 

		return (stringOut);
	} 
    
    /* OUTPUT RULE LINKED LIST WITH RECONVERSION */   
    /** Outputs contents of rule linked list (if any) with reconversion. */
    
    public void outputRulesWithReconversion(RuleNodeCMAR ruleList) {
		// Check for empty rule list
		if (ruleList==null) System.out.println("No rules generated!");
		outputConversionArrays();	
		// Loop through rule list
        int number = 1;
        RuleNodeCMAR linkRuleNode = ruleList; 
		while (linkRuleNode != null) {
			System.out.print("(" + number + ") ");
			outputItemSetWithReconversion(linkRuleNode.antecedent);
			System.out.print(" -> ");
            outputItemSet(linkRuleNode.consequent);
            System.out.println(" " + linkRuleNode.confidenceForRule + "%");
			number++;
			linkRuleNode = linkRuleNode.next;
	    }
	}
		
    /* OUTPUT RULE LINKED LIST WITH DEFAULT */   
    /** Outputs contents of rule linked list (if any), with reconversion, such 
    that last rule is the defualt rule. */
    
    /*public void outputRulesWithDefault() {
        int number = 1;
        RuleNode linkRuleNode = startRulelist;
	
	while (linkRuleNode != null) {
	    // Output rule number
	    System.out.print("(" + number + ") ");
	    // Output antecedent
	    if (linkRuleNode.next==null) System.out.print("Default -> ");
	    else {
	        outputItemSet(linkRuleNode.antecedent);
	        System.out.print(" -> ");
		}
	    // Output concequent
            outputItemSet(linkRuleNode.consequent);
            System.out.println(" " + linkRuleNode.confidenceForRule + "%");
	    // Increment parameters
	    number++;
	    linkRuleNode = linkRuleNode.next;
	    }
	}   */
		
    /* OUTPUT RULE LINKED LIST WITH DEFAULT AND RECONVERSION */   
    /** Outputs contents of rule linked list (if any), with reconversion, such 
    that last rule is the defualt rule. */
    
    /*public void outputRulesWithDefaultRecon() {
        int number = 1;
        RuleNode linkRuleNode = startRulelist;
	
	while (true) {
	    // Output rule number
	    System.out.print("(" + number + ") ");
	    // Output antecedent
	    if (linkRuleNode.next==null) {
	        System.out.print("Default -> ");
		break;
		}
	    else {
	        outputItemSetWithReconversion(linkRuleNode.antecedent);
	        System.out.print(" -> ");
		}
	    // Output concequent
            outputItemSetWithReconversion(linkRuleNode.consequent);
            System.out.println(" " + linkRuleNode.confidenceForRule + "%");
	    // Increment parameters
	    number++;
	    linkRuleNode = linkRuleNode.next;
	    }
	}     */

    /* OUTPUT NUMBER OF RULES */ 
     
    /** Outputs number of generated rules (ARs or CARS). */ 
     
    public void outputNumRules() { 
        System.out.println("Number of rules         = " + getNumCRs()); 
	} 
 
    /* OUTPUT NUMBER OF CMAR RULES */ 
     
    /** Outputs number of generated rules (ARs or CARS). */ 
     
    public void outputNumCMARrules() { 
        System.out.println("Number of CMAR rules    = " + getNumCMAR_CRs()); 
	} 
    }

