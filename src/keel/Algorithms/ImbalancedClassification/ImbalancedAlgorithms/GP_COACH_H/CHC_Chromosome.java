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

package keel.Algorithms.ImbalancedClassification.ImbalancedAlgorithms.GP_COACH_H;

import org.core.Randomize;

import java.util.ArrayList;

/**
 * <p>Title: CHC_RuleBase </p>
 *
 * <p>Description: Chromosome that represents a modified data base and rule base used in the CHC algorithm </p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Victoria Lopez (University of Granada) 14/06/2011
 * @version 1.5
 * @since JDK1.5
 */

public class CHC_Chromosome implements Comparable {
	private boolean [] rule_selection; // Boolean array selecting rules from a rule base
	private double [] lateral_tuning;
	private boolean n_e; // Indicates whether this chromosome has been evaluated or not
	private double fitness;// Fitness associated to the rule base represented by the boolean array
	private int n_rules; // Fitness associated to the rule base, it indicates the number of rules that it contains
	
	public static final double MIN_LATERAL_TUNING = 0.0;
	public static final double MAX_LATERAL_TUNING = 1.0;
	
    /**
     * Default constructor
     */
    public CHC_Chromosome () {
    }
    
    /**
     * Creates a CHC chromosome from another chromosome (copies a chromosome)
     * 
     * @param orig	Original chromosome that is going to be copied
     */
    public CHC_Chromosome (CHC_Chromosome orig) {
    	rule_selection = new boolean [orig.rule_selection.length];
    	lateral_tuning = new double [orig.lateral_tuning.length];
    	
    	for (int i=0; i<orig.rule_selection.length; i++) {
    		rule_selection[i] = orig.rule_selection[i];
    	}
    	
    	for (int i=0; i<orig.lateral_tuning.length; i++) {
    		lateral_tuning[i] = orig.lateral_tuning[i];
    	}
    	
    	n_e = orig.n_e;
    	fitness = orig.fitness;
    	n_rules = orig.n_rules;
    }
    
    /**
     * Creates a random CHC_Chromosome of specified size
     * 
     * @param size_rule	Size of the new chromosome considering the number of rules in the rule base
     * @param size_rule	Size of the new chromosome considering the number fuzzy labels in the data base
     */
    public CHC_Chromosome (int size_rule, int size_tuning) {
    	double u;
    	
    	rule_selection = new boolean [size_rule];
    	
    	for (int i=0; i<size_rule; i++) {
    		u = Randomize.Rand();
			if (u < 0.5) {
				rule_selection[i] = false;
			}
			else {
				rule_selection[i] = true;
			}
    	}
    	
    	lateral_tuning = new double [size_tuning];
    	
    	for (int i=0; i<size_tuning; i++) {
    		u = Randomize.Rand();
			lateral_tuning[i] = CHC_Chromosome.MIN_LATERAL_TUNING + (CHC_Chromosome.MAX_LATERAL_TUNING - CHC_Chromosome.MIN_LATERAL_TUNING) * u;
    	}
    	
    	n_e = true;
    	fitness = 0.0;
    	n_rules = size_rule;
    }

    /**
     * Creates a CHC_Chromosome of specified size with all its elements set to the specified value
     * 
     * @param size_rules	Size of the rule part of the new chromosome
     * @param value_rules	Value that all elements of the rule part of the chromosome are going to have 
     * @param size_tuning	Size of the tuning part of the new chromosome
     * @param value_tuning	Value that all elements of the tuning part of the chromosome are going to have 
     */
    public CHC_Chromosome (int size_rules, boolean value_rules, int size_tuning, double value_tuning) {
    	rule_selection = new boolean [size_rules];
    	lateral_tuning = new double [size_tuning];
    	
    	for (int i=0; i<size_rules; i++) {
    		rule_selection[i] = value_rules;
    	}
    	
    	for (int i=0; i<size_tuning; i++) {
    		lateral_tuning[i] = value_tuning;
    	}
    	
    	n_e = true;
    	fitness = 0.0;
    	n_rules = size_rules;
    }
    
    /**
     * Creates a CHC chromosome from a boolean array representing a chromosome
     * 
     * @param data_rule	boolean array representing the rule part of a chromosome
     * @param data_rule	boolean array representing the tuning part of a chromosome
     */
    public CHC_Chromosome (boolean data_rules[], double data_tuning[]) {
    	rule_selection = new boolean [data_rules.length];
    	lateral_tuning = new double [data_tuning.length];
    	
    	for (int i=0; i<data_rules.length; i++) {
    		rule_selection[i] = data_rules[i];
    	}
    	for (int i=0; i<data_tuning.length; i++) {
    		lateral_tuning[i] = data_tuning[i];
    	}
    	
    	n_e = true;
    	fitness = 0.0;
    	n_rules = data_rules.length;
    }

    /**
     * Checks if the current chromosome has been evaluated or not.
     * 
     * @return true, if this chromosome was evaluated previously;
     * false, if it has never been evaluated
     */
    public boolean not_eval() {
        return n_e;
    }
    
    /**
     * Evaluates this chromosome, computing the fitness of the rule
     * and the weight. It also initializes the token structure for
     * the token competition computation
     * 
     * @param dataset	Training dataset used in this algorithm
     * @param rule_population	Rule population that is selected by the CHC chromosome
     * @param gp_coach_obj	GP-COACH-H object that we will use to train an element in the training set with our rules
     * @param has_low	true, if the associated rule base contains low granularity rules; false otherwise
     * @param has_high	true, if the associated rule base contains high granularity rules; false otherwise
     * @param labels_low	Number of fuzzy labels in low granularity rules
     * @param labels_high	Number of fuzzy labels in high granularity rules
     * @param alpha	Alpha of the raw_fitness evaluation function
     */
    public void evaluate (myDataset dataset, ArrayList <Rule> rule_population, GP_COACH_H gp_coach_obj, boolean has_low, boolean has_high, int labels_low, int labels_high, double alpha) {
    	ArrayList <Rule> pop_type1 = new ArrayList <Rule> ();
    	ArrayList <Rule> pop_type2 = new ArrayList <Rule> ();
    	int hits = 0;
    	int selected_rules = 0;
    	int class_estimation;
    	double [][] low_granularity_tuning;
    	double [][] high_granularity_tuning;
    	int total_labels = labels_low + labels_high;
        
    	if (rule_population.size() != rule_selection.length) {
    		System.err.println("The CHC individual does not match the rule population given");
    		System.exit(-1);
    	}
    	
    	// Create the low granularity tuning matrix
    	if (has_low) {
    		low_granularity_tuning = new double [dataset.getnInputs()][labels_low];
    	
    		if (has_high) {
    			// We also have high granularity labels
    			for (int i=0; i<dataset.getnInputs(); i++) {
    				for (int j=0; j<labels_low; j++) {
    					low_granularity_tuning[i][j] = lateral_tuning[i*total_labels+j];
    				}
    			}
    		}
    		else {
    			// We only have low granularity labels
    			for (int i=0; i<dataset.getnInputs(); i++) {
    				for (int j=0; j<labels_low; j++) {
    					low_granularity_tuning[i][j] = lateral_tuning[i*labels_low+j];
    				}
    			}
    		}
    	}
    	else {
    		low_granularity_tuning = null;
    	}
    	
    	// Create the high granularity tuning matrix
    	if (has_high) {
    		high_granularity_tuning = new double [dataset.getnInputs()][labels_high];
    		
    		if (has_low) {
    			// We also have low granularity labels
    			for (int i=0; i<dataset.getnInputs(); i++) {
    				for (int j=0; j<labels_high; j++) {
    					high_granularity_tuning[i][j] = lateral_tuning[i*total_labels+labels_low+j];
    				}
    			}
    		}
    		else {
    			// We only have high granularity labels
    			for (int i=0; i<dataset.getnInputs(); i++) {
    				for (int j=0; j<labels_high; j++) {
    					high_granularity_tuning[i][j] = lateral_tuning[i*labels_high+j];
    				}
    			}
    		}
    	}
    	else {
    		high_granularity_tuning = null;
    	}
    	
    	for (int i=0; i<rule_population.size(); i++) {
    		if (rule_selection[i]) {
    			selected_rules++;
	        	Rule aux_i = (Rule)rule_population.get(i);
	        	Rule new_aux_i = new Rule(aux_i);
	        	
	        	// Update the rule according to the chromosome selected
	        	if (new_aux_i.getGranularity() == labels_low) {
	        		if (!has_low) {
	        			System.err.println("We are selecting a low granularity rule when this rule base hasn't got any low granularity rules");
	        			System.err.println("Rule{" + new_aux_i.getLevel() + "}: " + new_aux_i.printString(dataset.varNames(), dataset.classNames()));
	        			System.exit(-1);
	        		}
	        		new_aux_i.updateFuzzyLabels(low_granularity_tuning, dataset, alpha);
	        	}
	        	else if (new_aux_i.getGranularity() == labels_high) {
	        		if (!has_high) {
	        			System.err.println("We are selecting a high granularity rule when this rule base hasn't got any granularity rules");
	        			System.err.println("Rule{" + new_aux_i.getLevel() + "}: " + new_aux_i.printString(dataset.varNames(), dataset.classNames()));
	        			System.exit(-1);
	        		}
	        		new_aux_i.updateFuzzyLabels(high_granularity_tuning, dataset, alpha);
	        	}
	        	else {
	        		System.err.println("This rule has an unknown granularity not considered in this algorithm");
	        		System.exit(-1);
	        	}
	        	
	        	if (new_aux_i.getLevel() == 1) {
	        		pop_type1.add(new_aux_i);
	        	}
	        	else {
	        		pop_type2.add(new_aux_i);
	        	}
    		}
        }
    	
        for (int i = 0; i < dataset.getnData(); i++) {
        	class_estimation = -1;
        	
        	if (gp_coach_obj.inferenceType == GP_COACH_H.WINNING_RULE) { 
        		class_estimation = gp_coach_obj.FRM_WR(pop_type1, dataset.getExample(i), dataset.getMissing(i), new ArrayList <Integer> ());
            	if (class_estimation == -1) {
            		class_estimation = gp_coach_obj.FRM_WR(pop_type2, dataset.getExample(i), dataset.getMissing(i), new ArrayList <Integer> ());
            	}
        	}
        	else if (gp_coach_obj.inferenceType == GP_COACH_H.NORMALIZED_SUM) { 
        		class_estimation = gp_coach_obj.FRM_NS(pop_type1, dataset.getExample(i), dataset.getMissing(i), new ArrayList <Integer> ());
            	if (class_estimation == -1) {
            		class_estimation = gp_coach_obj.FRM_NS(pop_type2, dataset.getExample(i), dataset.getMissing(i), new ArrayList <Integer> ());
            	}
        	}
            else {
                System.err.println("Undefined Fuzzy Reasoning Method");
                System.exit(-1);
            }
        	
        	if (dataset.getOutputAsInteger(i) == class_estimation)
        		hits++;
        }
        
        fitness = (double)hits/(double)dataset.getnData();
        
        n_e = false;
        n_rules = selected_rules;
    }
    
    /**
     * Obtains the fitness associated to this CHC_Chromosome, its fitness measure
     * 
     * @return	the fitness associated to this CHC_Chromosome
     */
    public double getFitness() {
    	return fitness;
    }
    
    /**
     * Obtains the Hamming distance between this and another chromosome
     * 
     * @param ch_b	Other chromosome that we want to compute the Hamming distance to
     * @return	the Hamming distance between this and another chromosome
     */
    public int hammingDistance (CHC_Chromosome ch_b, int bitsgen) {
    	int i;
    	int dist = 0;
    	boolean [] this_boolean_tuning;
    	boolean [] ch_b_boolean_tuning;
    	
    	if ((rule_selection.length != ch_b.rule_selection.length) || (lateral_tuning.length != ch_b.lateral_tuning.length)) {
    		System.err.println("The CHC Chromosomes have different size so we cannot compute their Hamming distance");
    		System.exit(-1);
    	}
    	
    	this_boolean_tuning = convertToGrayCode (lateral_tuning, lateral_tuning.length, bitsgen);
    	ch_b_boolean_tuning = convertToGrayCode (ch_b.lateral_tuning, ch_b.lateral_tuning.length, bitsgen);
    	
    	for (i=0; i<rule_selection.length; i++){
    		if (rule_selection[i] != ch_b.rule_selection[i]) {
    			dist++;
    		}
    	}
    	
    	for (i=0; i<this_boolean_tuning.length; i++){
    		if (this_boolean_tuning[i] != ch_b_boolean_tuning[i]) {
    			dist++;
    		}
    	}

    	return dist;
    }
    
    
    /**
     * Obtains a new pair of CHC_chromosome from this chromosome and another chromosome, swapping half the differing bits at random
     * 
     * @param ch_b	Other chromosome that we want to use to create another chromosome
     * @return	a new pair of CHC_chromosome from this chromosome and the given chromosome
     */
    public ArrayList <CHC_Chromosome> createDescendants (CHC_Chromosome ch_b) {
    	int i, pos;
    	int different_values, n_swaps;
    	int [] different_position;
    	CHC_Chromosome descendant1 = new CHC_Chromosome();
    	CHC_Chromosome descendant2 = new CHC_Chromosome();
    	ArrayList <CHC_Chromosome> descendants;
    	
    	if ((rule_selection.length != ch_b.rule_selection.length) || (lateral_tuning.length != ch_b.lateral_tuning.length)) {
    		System.err.println("The CHC Chromosomes have different size so we cannot combine them");
    		System.exit(-1);
    	}
    	
    	// HUX crossover for the rule selection part
    	different_position = new int [rule_selection.length];
    	
    	descendant1.rule_selection = new boolean[rule_selection.length];
    	descendant2.rule_selection = new boolean[rule_selection.length];
    	
    	different_values = 0;
    	for (i=0; i<rule_selection.length; i++){
    		descendant1.rule_selection[i] = rule_selection[i];
    		descendant2.rule_selection[i] = ch_b.rule_selection[i];
    		
    		if (rule_selection[i] != ch_b.rule_selection[i]) {
    			different_position[different_values] = i;
    			different_values++;
    		}
    	}
    	
    	n_swaps = different_values/2;
    	
    	if ((different_values > 0) && (n_swaps == 0))
    		n_swaps = 1;
    	
    	for (int j=0; j<n_swaps; j++) {
    		different_values--;
    		pos = Randomize.Randint(0, different_values);
    		
    		boolean tmp = descendant1.rule_selection[different_position[pos]];
    		descendant1.rule_selection[different_position[pos]] = descendant2.rule_selection[different_position[pos]];
    		descendant2.rule_selection[different_position[pos]] = tmp;
    		
    		different_position[pos] = different_position[different_values];
    	}
    	
    	// PCBLX crossover for the lateral tuning part
    	double interval, a1, c1, u;
    	
    	descendant1.lateral_tuning = new double [lateral_tuning.length];
    	descendant2.lateral_tuning = new double [lateral_tuning.length];
    	
    	for (i=0; i<lateral_tuning.length; i++) {
    		interval = Math.abs(lateral_tuning[i]- ch_b.lateral_tuning[i]);
    		
    		// Create first descendant
    		a1 = lateral_tuning[i] - interval; 
    		if (a1 < CHC_Chromosome.MIN_LATERAL_TUNING) 
    			a1 = CHC_Chromosome.MIN_LATERAL_TUNING;
    		
    		c1 = lateral_tuning[i] + interval;
    		if (c1 > CHC_Chromosome.MAX_LATERAL_TUNING) 
    			c1 = CHC_Chromosome.MAX_LATERAL_TUNING;
    		
    		u = Randomize.Rand();
    		
    		descendant1.lateral_tuning[i] = a1 + u * (c1 - a1);
    		
    		// Create second descendant
    		a1 = ch_b.lateral_tuning[i] - interval; 
    		if (a1 < CHC_Chromosome.MIN_LATERAL_TUNING) 
    			a1 = CHC_Chromosome.MIN_LATERAL_TUNING;
    		
    		c1 = ch_b.lateral_tuning[i] + interval;
    		if (c1 > CHC_Chromosome.MAX_LATERAL_TUNING) 
    			c1 = CHC_Chromosome.MAX_LATERAL_TUNING;
    		
    		u = Randomize.Rand();
    		
    		descendant2.lateral_tuning[i] = a1 + u * (c1 - a1);
    	}
    	
    	descendant1.n_e = true;
    	descendant2.n_e = true;
    	descendant1.fitness = 0.0;
    	descendant2.fitness = 0.0;
    	descendant1.n_rules = rule_selection.length;
    	descendant2.n_rules = rule_selection.length;
    	
    	descendants = new ArrayList <CHC_Chromosome> (2);
    	descendants.add(descendant1);
    	descendants.add(descendant2);

    	return descendants;
    }    
    
    /**
     * Converts to gray code the real part of a chromosome
     * 
     * @param real_coding	Real part of a chromosome
     * @param length	Length of the real part of a chromosome
     * @param bitsgen	Number of bits per gen in the gray codification
     * @return	boolean array of samples that represents the gray code of the real part of a chromosome
     */
    private boolean [] convertToGrayCode (double [] real_coding, int length, int bitsgen) {
    	boolean [] result;
    	boolean [] tmp;
    	boolean last, comparison;
    	double increment, n;
    	
    	result = new boolean [length*bitsgen];
    	
    	for (int i=0; i < length; i++) {
    		increment = (CHC_Chromosome.MAX_LATERAL_TUNING - CHC_Chromosome.MIN_LATERAL_TUNING) / (Math.pow(2.0, (double)bitsgen) - 1.0);
    		
    		n = ((real_coding[i] - CHC_Chromosome.MIN_LATERAL_TUNING) / increment) + 0.5;
    		tmp = itob ((long)n, bitsgen);
    		
    		last = false;
    		for (int j=0; j<bitsgen; j++) {
    			if (tmp[j] != last) {
    				comparison = true;
    			}
    			else {
    				comparison = false;    				
    			}
    			
    			result [i*bitsgen+j] = comparison;
    			last = tmp[j];
    		}
    	}
    	
    	return result;
    }
    
    /**
     * Converts an integer to a boolean array of a specified length
     * 
     * @param n	Integer value to be converted to a boolean array
     * @param length	Desired length of the new boolean array
     * @return	boolean array associated to the integer value
     */
	private boolean [] itob (long n, int length) {
		boolean [] output = new boolean [length];
		
		for (int i=length-1; i>=0; i--) {
			output[i] = (n & 0x1) != 0;
			n = n >> 1;
    	}
		
		return output;
	}
    
    /**
     * Obtains the selected rules of the rule base associated to the current chromosome
     * 
     * @return	boolean array codifying the selected rules of the rule base associated to the current chromosome
     */
	public boolean [] obtainSelectedRules () {
		return rule_selection;
	}
	
    /**
     * Obtains the lateral tuning of the data base associated to the current chromosome
     * 
     * @return	real array codifying the lateral tuning of the data base associated to the current chromosome
     */
	public double [] obtainLateralTuning () {
		return lateral_tuning;
	}
	
    /**
     * Compares this object with the specified object for order, according to the fitness measure 
     * 
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object
     */
    public int compareTo (Object aThat) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        
    	if (this == aThat) return EQUAL;
    	
    	final CHC_Chromosome that = (CHC_Chromosome)aThat;
    	
    	if (this.fitness > that.fitness) return BEFORE;
        if (this.fitness < that.fitness) return AFTER;
        
        if (this.n_rules > that.n_rules) return AFTER;
        if (this.n_rules < that.n_rules) return BEFORE;
        return EQUAL;
    }
    
}

