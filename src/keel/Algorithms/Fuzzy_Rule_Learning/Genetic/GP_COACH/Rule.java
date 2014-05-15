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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.GP_COACH;

import org.core.Randomize;
import java.util.ArrayList;
import java.util.Collections;

/**
 * <p>Title: Rule </p>
 *
 * <p>Description: Fuzzy Rule in the GP-COACH algorithm </p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Victoria Lopez (University of Granada) 19/11/2011
 * @version 1.5
 * @since JDK1.5
 */
public class Rule implements Comparable {

    private ArrayList <FuzzyAntecedent> antecedent; // Antecedent of the fuzzy rule
    private int clas; // Consequent of the fuzzy rule
    private double weight; // Weight associated to the fuzzy rule
    private double raw_fitness; // Raw fitness associated to this fuzzy rule
    private double penalized_fitness; // Penalized fitness associated to this fuzzy rule (after applying the token competition procedure)
    private int t_norm; // T-norm used to compute the compatibility degree
    private int t_conorm; // T-conorm used to compute the compatibility degree
    private int ruleWeight; // Way of computing the rule weight
    private int level; // Type 1 for general rules, type 2 for specific rules
    private int ideal; // Number of training samples that this rule can seize
    private boolean n_e; // Indicates whether this rule has been evaluated or not
    private boolean [] tokens; // Used in the token competition procedure
    
    /**
     * Default constructor
     */
    public Rule () {
    }
    
    /**
     * Constructor with parameters. It creates a random general rule
     * 
     * @param database	Data Base associated to the general rule base that includes this rule
     * @param n_classes	Number of classes in the training set
     * @param tnorm	T-norm used to compute the compatibility degree
     * @param tconorm	T-conorm used to compute the compatibility degree
     * @param rule_weight	Way of computing the rule weight
     */
    public Rule (DataBase database, int n_classes, int tnorm, int tconorm, int rule_weight) {
    	int length_antecedent;
    	boolean [] selected_antecedent = new boolean [database.numVariables()];
    	
    	antecedent = new ArrayList <FuzzyAntecedent> (database.numVariables());
    	for (int i=0; i<database.numVariables(); i++) {
    		selected_antecedent[i] = false;
    	}
    	do {
	    	// Select randomly some variables of the database and create randomly its antecedent
	    	length_antecedent = Randomize.RandintClosed(1, database.numVariables());
	    	
	    	for (int i=0; i<length_antecedent; i++) {
	    		int var_selected;
	    		FuzzyAntecedent new_antecedent;
	    		
	    		do {
	    			var_selected = Randomize.Randint(0, database.numVariables());
	    		} while (selected_antecedent[var_selected]);
	    		
	    		new_antecedent = new FuzzyAntecedent (database, var_selected);
	    		
	    		if (new_antecedent.isValid()) {
	    			selected_antecedent[var_selected] = true;
	    			antecedent.add((FuzzyAntecedent)new_antecedent);
	    		}
	    	}
	    	
    	} while (antecedent.size() == 0);
    	
    	t_norm = tnorm;
    	t_conorm = tconorm;
    	ruleWeight = rule_weight;
    	level = 1;
    	clas = Randomize.Randint(0, n_classes);
    	n_e = true;    	
    }
    
    /**
     * Constructor with parameters. It creates a specific rule from a given sample
     * 
     * @param database	Data Base associated to the general rule base that includes this rule
     * @param n_classes	Number of classes in the training set
     * @param tnorm	T-norm used to compute the compatibility degree
     * @param tconorm	T-conorm used to compute the compatibility degree
     * @param rule_weight	Way of computing the rule weight
     * @param sample	Data sample used to generate this rule
     * @param sample_class	Output class associated to the data sample used to generate this rule
     */
    public Rule (DataBase database, int n_classes, int tnorm, int tconorm, int rule_weight, double [] sample, int sample_class) {
    	antecedent = new ArrayList <FuzzyAntecedent> (database.numVariables());
    	clas = sample_class;
    	t_norm = tnorm;
    	t_conorm = tconorm;
    	ruleWeight = rule_weight;
    	level = 2;
    	n_e = true;    	

        for (int i = 0; i < database.numVariables(); i++) {
            double max = 0.0;
            int etq = -1;
            double per;
            for (int j = 0; j < database.numLabels(); j++) {
                per = database.membershipFunction(i, j, sample[i]);
                if (per > max) {
                    max = per;
                    etq = j;
                }
            }
            if (max == 0.0) {
                System.err.println("There was an error while searching for the antecedent of the rule");
                System.err.println("Example: ");
                for (int j = 0; j < database.numVariables(); j++) {
                    System.err.print(sample[j] + "\t");
                }
                System.err.println("Variable " + i);
                System.exit(1);
            }
            
            FuzzyAntecedent new_antecedent = new FuzzyAntecedent (database.clone(i, etq), i, database.numLabels());
            antecedent.add((FuzzyAntecedent)new_antecedent);
        }
    }

	/**
	 * Copy constructor for a Fuzzy rule from another Fuzzy Rule
	 * 
	 * @param original	Rule which will be used as base to create another Rule
	 */
	public Rule (Rule original) {
		antecedent = new ArrayList <FuzzyAntecedent> (original.antecedent.size());
		for (int i=0; i<original.antecedent.size(); i++) {
			FuzzyAntecedent original_fuzzy_antecedent = (FuzzyAntecedent)original.antecedent.get(i);
			FuzzyAntecedent new_fuzzy_antecedent = new FuzzyAntecedent(original_fuzzy_antecedent);
			antecedent.add(new_fuzzy_antecedent);
		}
		
	    clas = original.clas; 
	    weight = original.weight; 
	    raw_fitness = original.raw_fitness; 
	    penalized_fitness = original.penalized_fitness;
	    t_norm = original.t_norm;
	    t_conorm = original.t_conorm; 
	    ruleWeight = original.ruleWeight;
	    level = original.level;
	    n_e = original.n_e;
	    ideal = original.ideal;
		
		tokens = new boolean [original.tokens.length];
		for (int i=0; i<original.tokens.length; i++) {
			tokens[i] = original.tokens[i];
		}
	}
    
    /**
     * Checks if the current rule has been evaluated or not.
     * 
     * @return true, if this rule was evaluated previously;
     * false, if it has never been evaluated
     */
    public boolean not_eval() {
        return n_e;
    }
    
    /**
     * Evaluates this rule, computing the raw_fitness of the rule
     * and the weight. It also initializes the token structure for
     * the token competition computation
     * 
     * @param dataset	Training dataset used in this algorithm
     * @param alpha	Alpha of the raw_fitness evaluation function
     */
    public void evaluate (myDataset dataset, double alpha) {
    	double compatibility, support, confidence;
    	double compatibility_matching_examples = 0;
        double compatibility_correctly_matching_examples = 0;
        int class_examples = dataset.numberInstances(clas);
        ideal = 0;
        
        tokens = new boolean [dataset.getnData()];
        for (int i = 0; i < dataset.getnData(); i++) {
            tokens[i] = false;
            compatibility = compatibility(dataset.getExample(i), dataset.getMissing(i));
            if (compatibility > 0) {
            	compatibility_matching_examples += compatibility;
            	if (clas == dataset.getOutputAsInteger(i)) {
                	compatibility_correctly_matching_examples += compatibility;
                	tokens[i] = true;
                    ideal++;
                }
            }
        }
        
        if (class_examples > 0)
        	support = compatibility_correctly_matching_examples / class_examples;
        else
        	support = 0.0;
        
        if (compatibility_matching_examples > 0)
        	confidence = compatibility_correctly_matching_examples / compatibility_matching_examples;
        else
        	confidence = 0.0;
        
        raw_fitness = alpha * confidence + (1 - alpha) * support;
        penalized_fitness = -1.0;
        assingConsequent(dataset);
        n_e = false;
    }
    
    /**
     * Computes the compatibility degree of the rule with an input example.
     * If it has missing values, the only matching possible for the rule happens
     * when the missing value has no condition
     * 
     * @param sample	The input example
     * @param missing	An array with the missing values for this input example
     * @return	the degree of compatibility
     */
    public double compatibility (double [] sample, boolean [] missing) {
        if (t_norm == GP_COACH.MINIMUM) {
			return minimumCompatibility (sample, missing);
		}
		else if (t_norm == GP_COACH.PRODUCT) {
			return productCompatibility (sample, missing);
		}
		else {
			System.err.println("Unknown t-norm for the computation of the compatibility degree");
			System.exit(-1);
			return -1.0;
		}
    }
    
    /**
     * Computes the compatibility degree of the rule with an input example using 
     * minimum as the t-norm for this computation. If it has missing values, the
     * only matching possible for the rule happens when the missing value has no condition
     * 
     * @param sample	The input example
     * @param missing	An array with the missing values for this input example
     * @return	the degree of compatibility using the minimum as t-norm operator
     */
    private double minimumCompatibility (double [] sample, boolean [] missing) {
    	double minimum, membershipDegree;
    	boolean is_comp = true;
    	boolean compatibility_computed = false;
    	int pos;
    	
    	minimum = 1.0;
    	for (int i = 0; i < sample.length && is_comp; i++) {
    		pos = isAttributeInAntecedent(i);
    		if (pos != -1) {
    			compatibility_computed = true;
    			if (missing[i]) {
            		// If we have a missing value, the only way for a matching happens when there is no condition
    				is_comp = false;
        			minimum = 0.0;
                }
    			else {
    				membershipDegree = ((FuzzyAntecedent)antecedent.get(pos)).matchingDegree(sample[i], t_conorm);
    				minimum = Math.min(membershipDegree, minimum);
    			}
    		}
        }
    	
    	if (compatibility_computed)
    		return (minimum);
    	else
    		return 0.0;
    }
    
    /**
     * Computes the compatibility degree of the rule with an input example using 
     * product as the t-norm for this computation. If it has missing values, the
     * only matching possible for the rule happens when the missing value has no condition
     * 
     * @param sample	The input example
     * @param missing	An array with the missing values for this input example
     * @return	the degree of compatibility using the product as t-norm operator
     */
    private double productCompatibility (double [] sample, boolean [] missing) {
    	double product, membershipDegree;
    	boolean is_comp = true;
    	boolean compatibility_computed = false;
    	int pos;
    	
    	product = 1.0;
    	for (int i = 0; i < sample.length && is_comp; i++) {
    		pos = isAttributeInAntecedent(i);
    		if (pos != -1) {
    			compatibility_computed = true;
    			if (missing[i]) {
            		// If we have a missing value, the only way for a matching happens when there is no condition
    				is_comp = false;
    				product = 0.0;
                }
    			else {
    				membershipDegree = ((FuzzyAntecedent)antecedent.get(pos)).matchingDegree(sample[i], t_conorm);
            		product = product * membershipDegree;
    			}
    		}
        }
    	
    	if (compatibility_computed)
    		return (product);
    	else
    		return 0.0;
    }    
    
    /**
     * Checks if a given attribute has a condition in the antecedent, and returns
     * the position of the found attribute
     * 
     * @param var	Given attribute that is going to be searched in the antecedent
     * @return	position, if the attribute is found in the antecedent, -1 otherwise
     */
    private int isAttributeInAntecedent (int var) {
    	int pos = -1;
    	
    	for (int i=0; (i<antecedent.size()) && (pos == -1); i++) {
    		if (((FuzzyAntecedent)antecedent.get(i)).getAttribute() == var) {
    			pos = i;
    		}
    	}

    	return pos;
    }
    
    /**
     * It assigns the rule weight to the rule
     * 
     * @param train myDataset the training set used to compute the weight of the rule
     */
    private void assingConsequent (myDataset train) {
      if (ruleWeight == GP_COACH.CF) {
        consequent_CF(train);
      }
      else if (ruleWeight == GP_COACH.PCF_II) {
        consequent_PCF2(train);
      }
      else if (ruleWeight == GP_COACH.PCF_IV) {
        consequent_PCF4(train);
      }
      else if (ruleWeight == GP_COACH.NO_RW) {
        weight = 1.0;
      }
    }
    
    /**
     * Classic Certainty Factor weight
     * 
     * @param train myDataset the training set used to compute the weight of the rule
     */
    private void consequent_CF (myDataset train) {
      double[] classes_sum = new double[train.getnClasses()];
      for (int i = 0; i < train.getnClasses(); i++) {
        classes_sum[i] = 0.0;
      }

      double total = 0.0;
      double comp;
      
      /* Computation of the sum by classes */
      for (int i = 0; i < train.size(); i++) {
        comp = compatibility(train.getExample(i), train.getMissing(i));
        classes_sum[train.getOutputAsInteger(i)] += comp;
        total += comp;
      }
      
      if (total != 0.0) // Check if there was any example matching the rule
    	  weight = classes_sum[clas] / total;
      else
    	  weight = 0.0;
    }
    
    /**
     * Penalized Certainty Factor weight II (by Ishibuchi)
     * 
     * @param train myDataset the training set used to compute the weight of the rule
     */
    private void consequent_PCF2(myDataset train) {
      double[] classes_sum = new double[train.getnClasses()];
      for (int i = 0; i < train.getnClasses(); i++) {
        classes_sum[i] = 0.0;
      }

      double total = 0.0;
      double comp;
      
      /* Computation of the sum by classes */
      for (int i = 0; i < train.size(); i++) {
        comp = compatibility(train.getExample(i), train.getMissing(i));
        classes_sum[train.getOutputAsInteger(i)] += comp;
        total += comp;
      }
      double sum = (total - classes_sum[clas]) / (train.getnClasses() - 1.0);
      if (total != 0.0) // Check if there was any example matching the rule
    	  weight = (classes_sum[clas] - sum) / total;
      else
    	  weight = 0.0;
    }
    
    /**
     * Penalized Certainty Factor weight IV (by Ishibuchi)
     * 
     * @param train myDataset the training set used to compute the weight of the rule
     */
    private void consequent_PCF4(myDataset train) {
      double[] classes_sum = new double[train.getnClasses()];
      for (int i = 0; i < train.getnClasses(); i++) {
        classes_sum[i] = 0.0;
      }

      double total = 0.0;
      double comp;
      
      /* Computation of the sum by classes */
      for (int i = 0; i < train.size(); i++) {
        comp = compatibility(train.getExample(i), train.getMissing(i));
        classes_sum[train.getOutputAsInteger(i)] += comp;
        total += comp;
      }
      double sum = total - classes_sum[clas];
      if (total != 0.0) // Check if there was any example matching the rule
    	  weight = (classes_sum[clas] - sum) / total;
      else
    	  weight = 0.0;
    }
    
    /**
     * Obtains the weight associated to this rule 
     * 
     * @return the weight associated to the fuzzy rule
     */
    public double getWeight() {
    	if (n_e) {
    		System.err.println("This rule has note been evaluated, so we cannot obtain its weight");
    		System.exit(-1);
    	}
    	
    	return weight;
    }
    
    /**
     * Obtains the class associated to this rule, consequent of the fuzzy rule
     * 
     * @return class associated to this rule, consequent of the fuzzy rule
     */
    public int getClas() {
    	return clas;
    }
    
    /**
     * Obtains the level of this rule; 1 for general rules, 2 for specific rules
     * 
     * @return class associated to this rule, consequent of the fuzzy rule
     */
    public int getLevel() {
    	return level;
    }

    /**
     * Obtains the number of variables used in this rule antecedent
     * 
     * @return number of variables used in this rule antecedent
     */
    public int getnVar() {
    	return antecedent.size();
    }
    
    /**
     * Obtains the number of different conditions used in this rule antecedent
     * 
     * @return number of different conditions used in this rule antecedent
     */
    public int getnCond() {
    	int cond = 0;
    	
    	for (int i=0; i<antecedent.size(); i++) {
    		cond += ((FuzzyAntecedent)antecedent.get(i)).getnLabels();
    	}
    	
    	return cond;
    }
    
    /**
     * Obtains the fitness associated to this rule, its raw_fitness measure
     * 
     * @return	the fitness associated to this rule
     */
    public double getFitness() {
    	return raw_fitness;
    }
    
    /**
     * Changes the class of the rule to a new specified class. It also changes
     * the internal values that correspond to the new class value
     * 
     * @param new_class class that is going to be assigned to this rule
     */
    public void setClass (int new_class) {
        clas = new_class;
        weight = 0.0;
        raw_fitness = 0.0;
        penalized_fitness = -1.0;
        n_e = true;
        ideal = 0;
        level = 1;
    }
    
    /**
     * Obtains the ith variable used in this rule antecedent
     * 
     * @param i	variable position we want to extract from the antecedent
     * @return ith variable in this rule antecedent
     */
    public FuzzyAntecedent getVar (int i) {
    	FuzzyAntecedent antecedent_var;
    	
    	if (i >= antecedent.size()) {
    		System.err.println("We cannot obtain a variable in a position outside the antecedent");
    		System.exit(-1);
    		
    		antecedent_var = new FuzzyAntecedent();
    	}
    	else {
    		antecedent_var = (FuzzyAntecedent)antecedent.get(i);
    	}
    	
    	return antecedent_var;
    }
    
    /**
     * Changes the antecedent labels for one condition in the antecedent to
     * another labels in the antecedent
     * 
     * @param pos_att_to_change	position in this rule antecedent of the attribute that 
     * is going to change labels
     * @param var	fuzzy antecedent containing the new label for the selected attribute
     * @param data	Data Base associated to the general rule base that includes this rule
     */
    public void exchangeAntecedentLabel (int pos_att_to_change, FuzzyAntecedent var, DataBase data) {
    	FuzzyAntecedent new_antecedent;
    	
    	if (pos_att_to_change >= antecedent.size()) {
    		System.err.println("We cannot obtain a variable in a position outside the antecedent");
    		System.exit(-1);
    	}
    	
    	new_antecedent = (FuzzyAntecedent)antecedent.get(pos_att_to_change);
    	
    	new_antecedent.changeLabels(var, data);
    	
    	antecedent.set(pos_att_to_change, (FuzzyAntecedent)new_antecedent); 
    	
        weight = 0.0;
        raw_fitness = 0.0;
        penalized_fitness = -1.0;
        n_e = true;
        ideal = 0;
        level = 1;
    }
    
    /**
     * Changes the antecedent labels for one condition in the antecedent to
     * another labels in the antecedent
     * 
     * @param current_variables	boolean array with the selected variables from this rule
     * @param new_variables	ArrayList of FuzzyAntecedent that contains the new variables for this rule
     */
    public void exchangeVariables (boolean [] current_variables, ArrayList <FuzzyAntecedent> new_variables) {
    	ArrayList <FuzzyAntecedent> new_antecedent;
    	FuzzyAntecedent new_variable;
    	
    	if (current_variables.length != antecedent.size()) {
    		System.err.println("We cannot obtain a variable in a position outside the antecedent");
    		System.exit(-1);
    	}
    	
    	// Add all selected variables to a new antecedent
    	new_antecedent = new ArrayList <FuzzyAntecedent> ();
    	for (int i=0; i<antecedent.size(); i++) {
    		if (current_variables[i]) {
    			new_variable = (FuzzyAntecedent)antecedent.get(i);
    			new_antecedent.add(new_variable);
    		}
    	}
    	
    	// For the new antecedent, we add the variables from the second parent to it
    	// In case the variable was also in the first parent, we mix both variables
    	for (int i=0; i<new_variables.size(); i++) {
    		boolean found = false;
    		for (int j=0; j<new_antecedent.size() && !found; j++) {
    			if (((FuzzyAntecedent)new_antecedent.get(j)).getAttribute() ==((FuzzyAntecedent)new_variables.get(i)).getAttribute()) {
    				found = true;
    				((FuzzyAntecedent)new_antecedent.get(j)).mixLabels((FuzzyAntecedent)new_variables.get(i));
    			}
    		}
    		if (!found) {
	    		new_variable = (FuzzyAntecedent)new_variables.get(i);
				new_antecedent.add(new_variable);
    		}
    	}
    	
    	// After mixing the variables, we have to check that we haven't created variables
    	// with all the labels on the fuzzy antecedent
    	antecedent = new ArrayList <FuzzyAntecedent> ();
    	for (int i=0; i<new_antecedent.size(); i++) {
    		new_variable = (FuzzyAntecedent)new_antecedent.get(i);
			if (!new_variable.isAny()) {
    			antecedent.add(new_variable);
    		}
    	}
    	
        weight = 0.0;
        raw_fitness = 0.0;
        penalized_fitness = -1.0;
        n_e = true;
        ideal = 0;
        level = 1;
    }
    
    /**
     * Adds a label to the fuzzy antecedent of the given variable
     * 
     * @param variable_mutated	position of the variable that is going to have a label added
     * @param data	Data Base associated to the general rule base that includes this rule
     */
    public void addLabel (int variable_mutated, DataBase data) {
  		if (variable_mutated >= antecedent.size()) {
    		System.err.println("We cannot select a variable outside the antecedent");
    		System.exit(-1);
    	}
  		
  		if ((((FuzzyAntecedent)antecedent.get(variable_mutated)).getnLabels() + 1) == data.numLabels()) {
  			System.err.println("We cannot add a label to create an any condition");
  			System.exit(-1);
  		}
    	
  		// Add label to the selected fuzzy antecedent
  		((FuzzyAntecedent)antecedent.get(variable_mutated)).addLabel(data);
    		
  		weight = 0.0;
        raw_fitness = 0.0;
        penalized_fitness = -1.0;
        n_e = true;
        ideal = 0;
        level = 1;
    }
    
    /**
     * Deletes a label to the fuzzy antecedent of the given variable
     * 
     * @param variable_mutated	position of the variable that is going to have a label deleted
     */
    public void deleteLabel (int variable_mutated) {
  		if (variable_mutated >= antecedent.size()) {
    		System.err.println("We cannot select a variable outside the antecedent");
    		System.exit(-1);
    	}
  		
  		if (((FuzzyAntecedent)antecedent.get(variable_mutated)).getnLabels() == 1) {
  			System.err.println("We cannot delete a label because the antecedent will be matched to no condition");
  			System.exit(-1);
  		}
    	
  		// Delete label from the selected fuzzy antecedent
  		((FuzzyAntecedent)antecedent.get(variable_mutated)).deleteLabel();
    		
  		weight = 0.0;
        raw_fitness = 0.0;
        penalized_fitness = -1.0;
        n_e = true;
        ideal = 0;
        level = 1;
    }
    
    /**
     * Changes a label in the fuzzy antecedent of the given variable from an existing value to a 
     * non-existing value
     * 
     * @param variable_mutated	position of the variable that is going to have a label deleted
     * @param data	Data Base associated to the general rule base that includes this rule
     */
    public void changeLabel (int variable_mutated, DataBase data) {
  		if (variable_mutated >= antecedent.size()) {
    		System.err.println("We cannot select a variable outside the antecedent");
    		System.exit(-1);
    	}
    	
  		// Change label value in the selected fuzzy antecedent
  		((FuzzyAntecedent)antecedent.get(variable_mutated)).changeLabel(data);
    		
  		weight = 0.0;
        raw_fitness = 0.0;
        penalized_fitness = -1.0;
        n_e = true;
        ideal = 0;
        level = 1;
    }
    
    /**
     * Deletes all the data stored in this rule antecedent
     */
    public void clearAntecedent () {
    	antecedent.clear();
    	
  		weight = 0.0;
        raw_fitness = 0.0;
        penalized_fitness = -1.0;
        n_e = true;
        ideal = 0;
        level = 1;
    }
    
    /**
     * Adds a new variable to the fuzzy antecedent set of this rule
     * 
     * @param data	Data Base associated to the general rule base that includes this rule
     */
    public void addVar (DataBase data) {
    	boolean found_var = false;
		int selected_var;
		
    	if (antecedent.size() >= data.numVariables()) {
    		System.err.println("We cannot add a new var to this rule since it has all the possible vars in it");
    		System.exit(-1);
    	}
  		
    	// Find a label that we do not have in the label set yet
		do {
			selected_var = Randomize.Randint(0, data.numVariables());
			found_var = false;
			
			for (int i=0; i<antecedent.size() && !found_var; i++) {
				if (((FuzzyAntecedent)antecedent.get(i)).getAttribute() == selected_var) {
					found_var = true;
				}
			}
		} while (found_var);
		
		// Add this variable to the Fuzzy set
		FuzzyAntecedent new_antecedent;
		do {
			new_antecedent = new FuzzyAntecedent (data, selected_var);
		} while (!new_antecedent.isValid());
		antecedent.add(new_antecedent);
		
  		weight = 0.0;
        raw_fitness = 0.0;
        penalized_fitness = -1.0;
        n_e = true;
        ideal = 0;
        level = 1;
    }
    
    /**
     * Deletes a variable from the fuzzy antecedent set of this rule
     */
    public void deleteVar () {
		int selected_var;
		
		// Select a variable in the fuzzy antecedent
		selected_var = Randomize.Randint(0, antecedent.size());
		
		// Delete this label from the Fuzzy set
		antecedent.remove(selected_var);
    		
  		weight = 0.0;
        raw_fitness = 0.0;
        penalized_fitness = -1.0;
        n_e = true;
        ideal = 0;
        level = 1;
    }
    
    /** 
     * Computes the number of training samples that match this rule. The rule must have been
     * previously evaluated
     * 
     * @return	the number of training samples this rule can seize
     */
    public int ideal() {
    	if (n_e) {
    		System.err.println("The number of training samples this rule can seize cannot be computed before the rule is evaluated");
    		System.exit(-1);
    	}
    	
    	return ideal;
    }
    
    /**
     * Sets the penalized fitness field to a specified given value, corresponding to this rule penalized
     * fitness according to the data set and the other rules considered
     * 
     * @param fitness	penalized fitness value associated to this rule
     */
    public void setPenalizedFitness (double fitness) {
    	penalized_fitness = fitness;
    }
    
    /**
     * Checks if a determined value of the training set is seized by this rule or not
     * 
     * @param idSample	Position in the training set of the value that we want to seize with this rule
     * @return true, if the sample is seized by this rule; false, if it is not seized
     */
    public boolean isSeized (int idSample) {
        return tokens[idSample];
    }
    
    /**
     * Obtains the penalized fitness associated to this rule, computed with the token competition procedure
     * 
     * @return	the penalized fitness associated to this rule
     */
    public double getPenalizedFitness() {
    	if (penalized_fitness < 0.0) {
    		System.err.println("We cannot obtain the penalized fitness measure if we haven't evaluated the individual and we haven't applied the token competition procedure");
    		System.exit(-1);
    	}
    	return penalized_fitness;
    }
    
    /**
     * Compares this object with the specified object for order, according to the raw_fitness measure 
     * 
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object
     */
    public int compareTo(Object a) {
        if (((Rule) a).raw_fitness < this.raw_fitness) {
            return -1;
        }
        if (((Rule) a).raw_fitness > this.raw_fitness) {
            return 1;
        }
        return 0;
    }
    
    /**
     * String representation of a Fuzzy Rule in the GP-COACH algorithm.
     *
     * @return String an string containing the Fuzzy Rule
     */
    public String printString (String [] names, String [] classes) {
    	String rule_string = "IF ";
    	
    	Collections.sort(antecedent);
    	
    	for (int a=0; a<antecedent.size() - 1; a++) {
    		rule_string += names[antecedent.get(a).getAttribute()] + " IS " + (antecedent.get(a)).printString() + " AND ";
    	}
    	rule_string += names[antecedent.get(antecedent.size() - 1).getAttribute()] + " IS " + (antecedent.get(antecedent.size() - 1)).printString() + " THEN " + classes[clas] + " with Rule Weight: " + weight + "\n";

    	return rule_string;
     }

    
}

