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

import java.util.ArrayList;
import java.util.Collections;

import org.core.Randomize;

/**
 * <p>Title: CHC </p>
 *
 * <p>Description: Uses a CHC algorithm to select the rules used in the GP-COACH-H algorithm </p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Victoria Lopez (University of Granada) 26/04/2011
 * @version 1.5
 * @since JDK1.5
 */

public class CHC {
	private ArrayList <Rule> rule_population;
	private myDataset dataset;
	private GP_COACH_H gp_coach_obj;
	private double raw_alpha;
	
	private ArrayList <CHC_Chromosome> population;
	
	private int max_eval;
	private int n_eval;
	
	private int pop_length;
	
	private int bitsgene;
	private int tuning_size;
	
	private boolean has_low_granularity;
	private boolean has_high_granularity;
	private int nLabelsLow;
	private int nLabelsHigh;
	
	private double threshold;
	private double best_fitness;
	private int n_restart_not_improving;
	
	/**
     * Default constructor
     */
    public CHC () {
    }
    
    /**
     * Creates a CHC object with its parameters
     * 
     * @param current_dataset	Training dataset used in this algorithm
     * @param gp_coach_data	GP-COACH-H object that we will use to train an element in the training set with our rules
     * @param alpha	Alpha of the raw_fitness evaluation function
     * @param pop	Population of rules we want to select
     * @param n_low	Number of low granularity rules in the rule base
     * @param n_high	Number of high granularity rules in the rule base
     * @param eval	Maximum number of evaluations in the CHC algorithm
     * @param popLength	Size of the population in the CHC algorithm
     * @param bits_per_gene Bits per gene for the gray code associated to the real part of the CHC algorithm (lateral tuning)
     */
    public CHC (myDataset current_dataset, GP_COACH_H gp_coach_data, double alpha, ArrayList <Rule> pop, int n_low, int n_high, int eval, int popLength, int bits_per_gene) {
    	dataset = current_dataset;
    	gp_coach_obj = gp_coach_data;
    	rule_population = pop;
    	max_eval = eval;
    	pop_length = popLength;
    	bitsgene = bits_per_gene;
    	raw_alpha = alpha;
    	
    	population = new ArrayList <CHC_Chromosome> (pop_length);
    	best_fitness = -1.0;
    	
    	nLabelsLow = n_low;
    	nLabelsHigh = n_high;
    }
    
	/**
     * Run the CHC algorithm for the data in this population
     * 
     */
    public void runCHC () {
    	ArrayList <CHC_Chromosome> C_population;
    	ArrayList <CHC_Chromosome> Cr_population;
    	boolean pop_changes;
    	
    	n_eval = 0;
    	
    	// Compute the number of fuzzy labels we have to tune
    	// First of all, we check the granularity for each rule
    	has_low_granularity = false;
    	has_high_granularity = false;
    	Rule current_rule;
    	int total_labels = 0;
    	int r;
    	
    	for (r=0; ((r<rule_population.size()) && (!has_low_granularity || !has_high_granularity)); r++) {
    		current_rule = (Rule)rule_population.get(r);
    		
    		if (current_rule.getGranularity() == nLabelsLow) {
    			has_low_granularity = true;
    		}
    		
    		if (current_rule.getGranularity() == nLabelsHigh) {
    			has_high_granularity = true;
    		}
    	}
    	
    	if (has_low_granularity) {
    		total_labels += nLabelsLow;
    	}
    	
    	if (has_high_granularity) {
    		total_labels += nLabelsHigh;
    	}
    	tuning_size = total_labels * dataset.getnInputs();
    	
    	threshold = (double)(tuning_size * bitsgene + rule_population.size())/4.0;
    	n_restart_not_improving = 0;
    	
    	initPopulation();
    	evalPopulation();
    	
    	do {
    		// Select for crossover
    		C_population = randomSelection();
    		// Cross selected individuals
    		Cr_population = recombine (C_population);
    		// Evaluate new population
    		evaluate (Cr_population);
    		
    		// Select individuals for new population
    		pop_changes = selectNewPopulation (Cr_population);
    		
    		// Check if we have improved or not
    		if (!pop_changes) {
    			threshold -= bitsgene;
    		}
    		
    		// If we do not improve our current population for several trials, then we should restart the population
    		if (threshold < 0) {
    			//System.out.println("Restart!!");
    			restartPopulation();
    			threshold = (double)(tuning_size * bitsgene + rule_population.size())/4.0;
    	    	best_fitness = -1.0;
    			n_restart_not_improving++;
    			evalPopulation();
    		}
    		
    		//System.out.println("CHC procedure: " + n_eval + " of " + max_eval + " evaluations. Best fitness is " + best_fitness);
    	} while ((n_eval < max_eval) && (best_fitness < 1.0) && (n_restart_not_improving <= 3));

    	// The evaluations have finished now, so we select the individual with best fitness
    	Collections.sort(population);
    }
    
    /**
     * Creates several population individuals randomly. The first individual has all its values set to true
     */
    private void initPopulation () {
    	CHC_Chromosome current_chromosome = new CHC_Chromosome (rule_population.size(), true, tuning_size, CHC_Chromosome.MIN_LATERAL_TUNING + ((double)(CHC_Chromosome.MAX_LATERAL_TUNING - CHC_Chromosome.MIN_LATERAL_TUNING)/2.0));
    	population.add(current_chromosome);
    	
    	for (int i=1; i<pop_length; i++) {
    		current_chromosome = new CHC_Chromosome (rule_population.size(), tuning_size);
    		population.add(current_chromosome);
    	}
    }
    
    /**
     * Evaluates the population individuals. If a chromosome was previously evaluated we do not evaluate it again
     */
    private void evalPopulation () {
    	double ind_fitness;
    	
        for (int i = 0; i < pop_length; i++) {
            if (population.get(i).not_eval()) {
            	population.get(i).evaluate(dataset, rule_population, gp_coach_obj, has_low_granularity, has_high_granularity, nLabelsLow, nLabelsHigh, raw_alpha);
            	n_eval++;
            }
        	
        	ind_fitness = population.get(i).getFitness();
        	if (ind_fitness > best_fitness) {
        		best_fitness = ind_fitness;            		
        	}
        }
    }
    
    /**
     * Selects all the members of the current population to a new population ArrayList in random order
     * 
     * @return	the current population in random order
     */
    private ArrayList <CHC_Chromosome> randomSelection() {
    	ArrayList <CHC_Chromosome> C_population;
    	int [] order;
    	int pos, tmp;
    	
    	C_population = new ArrayList <CHC_Chromosome> (pop_length);
    	order = new int[pop_length];
    	
    	for (int i=0; i<pop_length; i++) {
    		order[i] = i;
    	}
    	
    	for (int i=0; i<pop_length; i++) {
    		pos = Randomize.Randint(i, pop_length-1);
    		tmp = order[i];
    		order[i] = order[pos];
    		order[pos] = tmp;
    	}
    	
    	for (int i=0; i<pop_length; i++) {
    		C_population.add(new CHC_Chromosome(((CHC_Chromosome)population.get(order[i]))));
    	}
    	
    	return C_population;
    }
    
    /**
     * Obtains the descendants of the given population by creating the most different descendant from parents which are different enough
     * 
     * @param original_population	Original parents used to create the descendants population
     * @return	Population of descendants of the given population
     */
    private ArrayList <CHC_Chromosome> recombine (ArrayList <CHC_Chromosome> original_population) {
    	ArrayList <CHC_Chromosome> Cr_population;
    	int distHamming, n_descendants;
    	CHC_Chromosome main_parent, second_parent;
    	ArrayList <CHC_Chromosome> descendants;
    	
    	n_descendants = pop_length;
    	if ((n_descendants%2)!=0)
    		n_descendants--;
    	Cr_population = new ArrayList <CHC_Chromosome> (n_descendants);
    	
    	for (int i=0; i<n_descendants; i+=2) {
    		main_parent = (CHC_Chromosome)original_population.get(i);
    		second_parent = (CHC_Chromosome)original_population.get(i+1);
    		
    		distHamming = main_parent.hammingDistance (second_parent, bitsgene);
    		
    		if ((distHamming/2.0) > threshold) {
    			descendants = main_parent.createDescendants(second_parent);
    			Cr_population.add((CHC_Chromosome)descendants.get(0));
    			Cr_population.add((CHC_Chromosome)descendants.get(1));
    		}
    	}
    	
    	return Cr_population;
    }
    
    /**
     * Evaluates the given individuals. If a chromosome was previously evaluated we do not evaluate it again
     * 
     * @param pop	Population of individuals we want to evaluate
     */
    private void evaluate (ArrayList <CHC_Chromosome> pop) {
    	for (int i = 0; i < pop.size(); i++) {
            if (pop.get(i).not_eval()) {
            	pop.get(i).evaluate(dataset, rule_population, gp_coach_obj, has_low_granularity, has_high_granularity, nLabelsLow, nLabelsHigh, raw_alpha);
            	n_eval++;
            }
        }
    }
    
    /**
     * Replaces the current population with the best individuals of the given population and the current population
     * 
     * @param pop	Population of new individuals we want to introduce in the current population
     * @return true, if any element of the current population is changed with other element of the new population; false, otherwise
     */
    private boolean selectNewPopulation (ArrayList <CHC_Chromosome> pop) {
    	double worst_old_population, best_new_population;
    	
    	// First, we sort the old and the new population
    	Collections.sort(population);
    	Collections.sort(pop);
    	
    	worst_old_population = ((CHC_Chromosome)population.get(population.size()-1)).getFitness();
    	if (pop.size() > 0) {
    		best_new_population = ((CHC_Chromosome)pop.get(0)).getFitness();
    	}
    	else {
    		best_new_population = 0.0;
    	}	
    	
    	if ((worst_old_population >= best_new_population) || (pop.size() <= 0)) {
    		return false;
    	}
    	else {
    		ArrayList <CHC_Chromosome> new_pop;
    		CHC_Chromosome current_chromosome;
    		int i = 0;
    		int i_pop = 0;
    		boolean copy_old_population = true;
    		double current_fitness;
    		boolean small_new_pop = false;
    		
    		new_pop = new ArrayList <CHC_Chromosome> (pop_length);
    		
    		// Copy the members of the old population better than the members of the new population
    		do {
    			current_chromosome = (CHC_Chromosome)population.get(i);
    			current_fitness = current_chromosome.getFitness();
    			
    			if (current_fitness < best_new_population) {
    				// Check if we have enough members in the new population to create the final population
    				if ((pop_length-i) > pop.size()) {
    					new_pop.add(current_chromosome);
        				i++;
        				small_new_pop = true;
    				}
    				else {
    					copy_old_population = false;
    				}
    			}
    			else {
    				new_pop.add(current_chromosome);
    				i++;
    			}
    		} while ((i < pop_length) && (copy_old_population));
    		
    		while (i < pop_length) {
    			current_chromosome = (CHC_Chromosome)pop.get(i_pop);
    			new_pop.add(current_chromosome);
    			i++;
    			i_pop++;
    		}
    		
    		if (small_new_pop) {
    			Collections.sort(new_pop);
    		}
    		
    		current_fitness = ((CHC_Chromosome)new_pop.get(0)).getFitness();
    		
    		if (best_fitness < current_fitness) {
    			best_fitness = current_fitness;
    			n_restart_not_improving = 0;
    		}
    		
    		population = new_pop;	
        	return true;
    	}
    }
    
    /**
     * Creates a new population using the CHC diverge procedure
     */
    private void restartPopulation () {
    	ArrayList <CHC_Chromosome> new_pop;
    	CHC_Chromosome current_chromosome;
    	
    	new_pop = new ArrayList <CHC_Chromosome> (pop_length);
    	
    	Collections.sort(population);
    	current_chromosome = (CHC_Chromosome)population.get(0);
    	new_pop.add(current_chromosome);
    	
    	for (int i=1; i<pop_length; i++) {
    		current_chromosome = new CHC_Chromosome (rule_population.size(), tuning_size);
    		new_pop.add(current_chromosome);
    	}
    	
    	population = new_pop;
    }

    /**
     * Obtains the best set of rules from the genetic rule selection process
     * 
     * @return	best set of rules
     */
	public ArrayList <Rule> obtainNewRuleBase() {
		boolean [] selected_rules;
		double [] lateral_tuning;
		CHC_Chromosome best_solution;
		ArrayList <Rule> new_population;
    	double [][] low_granularity_tuning;
    	double [][] high_granularity_tuning;
    	int total_labels;
		
		best_solution = (CHC_Chromosome)population.get(0);
		selected_rules = best_solution.obtainSelectedRules();
		lateral_tuning = best_solution.obtainLateralTuning();
		
    	if (selected_rules.length != rule_population.size()) {
    		System.err.println("The CHC procedure obtained a different rule base size than the original one");
    		System.exit(-1);
    	}
    	
    	// Create the low granularity tuning matrix
    	total_labels = nLabelsLow + nLabelsHigh;
    	if (has_low_granularity) {
    		low_granularity_tuning = new double [dataset.getnInputs()][nLabelsLow];
    	
    		if (has_high_granularity) {
    			// We also have high granularity labels
    			for (int i=0; i<dataset.getnInputs(); i++) {
    				for (int j=0; j<nLabelsLow; j++) {
    					low_granularity_tuning[i][j] = lateral_tuning[i*total_labels+j];
    				}
    			}
    		}
    		else {
    			// We only have low granularity labels
    			for (int i=0; i<dataset.getnInputs(); i++) {
    				for (int j=0; j<nLabelsLow; j++) {
    					low_granularity_tuning[i][j] = lateral_tuning[i*nLabelsLow+j];
    				}
    			}
    		}
    	}
    	else {
    		low_granularity_tuning = null;
    	}
    	
    	// Create the high granularity tuning matrix
    	if (has_high_granularity) {
    		high_granularity_tuning = new double [dataset.getnInputs()][nLabelsHigh];
    		
    		if (has_low_granularity) {
    			// We also have low granularity labels
    			for (int i=0; i<dataset.getnInputs(); i++) {
    				for (int j=0; j<nLabelsHigh; j++) {
    					high_granularity_tuning[i][j] = lateral_tuning[i*total_labels+nLabelsLow+j];
    				}
    			}
    		}
    		else {
    			// We only have high granularity labels
    			for (int i=0; i<dataset.getnInputs(); i++) {
    				for (int j=0; j<nLabelsHigh; j++) {
    					high_granularity_tuning[i][j] = lateral_tuning[i*nLabelsHigh+j];
    				}
    			}
    		}
    	}
    	else {
    		high_granularity_tuning = null;
    	}    	
    	
		new_population = new ArrayList <Rule> (rule_population.size());
		
    	for (int i=0; i<selected_rules.length; i++) {
    		if (selected_rules[i]) {
    			Rule aux_i = (Rule)rule_population.get(i);
    			Rule new_aux_i = new Rule(aux_i);
    			
	        	// Update the rule according to the chromosome selected
	        	if (new_aux_i.getGranularity() == nLabelsLow) {
	        		if (!has_low_granularity) {
	        			System.err.println("We are selecting a low granularity rule when this rule base hasn't got any low granularity rules");
	        			System.err.println("Rule{" + new_aux_i.getLevel() + "}: " + new_aux_i.printString(dataset.varNames(), dataset.classNames()));
	        			System.exit(-1);
	        		}
	        		new_aux_i.updateFuzzyLabels(low_granularity_tuning, dataset, raw_alpha);
	        	}
	        	else if (new_aux_i.getGranularity() == nLabelsHigh) {
	        		if (!has_high_granularity) {
	        			System.err.println("We are selecting a high granularity rule when this rule base hasn't got any granularity rules");
	        			System.err.println("Rule{" + new_aux_i.getLevel() + "}: " + new_aux_i.printString(dataset.varNames(), dataset.classNames()));
	        			System.exit(-1);
	        		}
	        		new_aux_i.updateFuzzyLabels(high_granularity_tuning, dataset, raw_alpha);
	        	}
	        	else {
	        		System.err.println("This rule has an unknown granularity not considered in this algorithm");
	        		System.exit(-1);
	        	}
    			
    			new_population.add(new_aux_i);
    		}
        }
		
		return new_population;
	}

    /**
     * Obtains the best lateral tuning from the genetic tuning process
     * 
     * @return	real matrix representing the best lateral tuning that needs to be applied to the data base 
     */
	public double [] obtainLateralTuning () {
		double [] lateral_tuning;
		CHC_Chromosome best_solution;
		
		best_solution = (CHC_Chromosome)population.get(0);
		lateral_tuning = best_solution.obtainLateralTuning();
		
		return lateral_tuning;
	}
}

