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

/**
 * <p>Title: GP-COACH</p>
 *
 * <p>Description: It contains the implementation of the GP-COACH algorithm</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Victoria Lopez (University of Granada) 11/01/2011
 * @version 1.0
 */

import java.io.IOException;
import java.util.*;

import org.core.*;

public class GP_COACH {

    myDataset train, val, test;
    String outputTr, outputTst, fileDB, fileRB;
    
    DataBase dataBase;
    
    // Algorithm's parameters
    long seed;
    int ruleWeight; // Way of computing the rule weight
    int nLabels; // Number of labels for the database
    int t_norm; // T-norm used
    int t_conorm; // T-conorm used
    int inferenceType; // Fuzzy Reasoning Method
    int nGenerations; // Number of evaluations
    int populationSize; // Initial number of rules of the Rule Base (initial population size)
    double alpha; // Alpha for computation of Raw Fitness
    double probCross, probMut, probIns, probDrop; // Crossover probability, mutation probability, insertion probability, dropping condition probability
    int tournamentSize; // Tournament Selection Size
    double w1, w2, w3, w4; // Global Fitness Weights
    
    public static final int CF = 0;
    public static final int PCF_IV = 1;
    public static final int PCF_II = 2;
    public static final int NO_RW = 3;
    public static final int MINIMUM = 0;
    public static final int PRODUCT = 1;
    public static final int MAXIMUM = 0;
    public static final int PROBABILISTIC_SUM = 1;
    public static final int WINNING_RULE = 0;
    public static final int NORMALIZED_SUM = 1; // also known as additive combination
    public static final int ARITHMETIC_MEAN = 2;
    
    // Population of the genetic algorithm
    ArrayList <Rule> population;
    double populationFitness;
    ArrayList <Rule> bestPopulation;
    double bestPopulationFitness;
    
    boolean [] tokensGlobal; // Used in the token competition procedure

    private boolean somethingWrong = false; //to check if everything is correct.

    /**
     * Default constructor
     */
    public GP_COACH () {
    }

    /**
     * It reads the data from the input files (training, validation and test) and parse all the parameters
     * from the parameters array.
     * 
     * @param parameters parseParameters It contains the input files, output files and parameters
     */
    public GP_COACH (parseParameters parameters) {

        train = new myDataset();
        val = new myDataset();
        test = new myDataset();
        try {
            System.out.println("\nReading the training set: " +
                               parameters.getTrainingInputFile());
            train.readClassificationSet(parameters.getTrainingInputFile(), true);
            System.out.println("\nReading the validation set: " +
                               parameters.getValidationInputFile());
            val.readClassificationSet(parameters.getValidationInputFile(), false);
            System.out.println("\nReading the test set: " +
                               parameters.getTestInputFile());
            test.readClassificationSet(parameters.getTestInputFile(), false);
        } catch (IOException e) {
            System.err.println(
                    "There was a problem while reading the input data-sets: " +
                    e);
            somethingWrong = true;
        }

        //We may check if there are some numerical attributes, because our algorithm may not handle them:
        //somethingWrong = somethingWrong || train.hasNumericalAttributes();
        //somethingWrong = somethingWrong || train.hasMissingAttributes();

        outputTr = parameters.getTrainingOutputFile();
        outputTst = parameters.getTestOutputFile();

        fileDB = parameters.getOutputFile(0);
        fileRB = parameters.getOutputFile(1);
        
        // Now we parse the parameters, for example:
        seed = Long.parseLong(parameters.getParameter(0));
        
        // Labels for the data base
        nLabels = Integer.parseInt(parameters.getParameter(1));
        
        // Computation of the compatibility degree (t-norm and t-conorm)
        String aux = parameters.getParameter(2); 
        t_norm = PRODUCT;
        t_conorm = PROBABILISTIC_SUM;
        if (aux.compareToIgnoreCase("Minimum/Maximum") == 0) {
          t_norm = MINIMUM;
          t_conorm = MAXIMUM;
        }
        
        // Rule Weight
        aux = parameters.getParameter(3);
        ruleWeight = PCF_IV;
        if (aux.compareToIgnoreCase("Certainty_Factor") == 0) {
          ruleWeight = CF;
        }
        else if (aux.compareToIgnoreCase("Average_Penalized_Certainty_Factor") == 0) {
          ruleWeight = PCF_II;
        }
        else if (aux.compareToIgnoreCase("No_Weights") == 0){
          ruleWeight = NO_RW;
        }
        
        // Fuzzy Reasoning Method
        aux = parameters.getParameter(4);
        inferenceType = WINNING_RULE;
        if (aux.compareToIgnoreCase("Normalized_Sum") == 0) { // (also known as additive combination)
          inferenceType = NORMALIZED_SUM; 
        }
        else if (aux.compareToIgnoreCase("Arithmetic_Mean") == 0) {
          inferenceType = ARITHMETIC_MEAN; 
        }
        
        // Number of evaluations
        nGenerations = Integer.parseInt(parameters.getParameter(5));
        
        // Initial size of the population (initial size of the rule base)
        populationSize = Integer.parseInt(parameters.getParameter(6));
        if (populationSize == 0){
          if (train.getnInputs() < 10){
        	  populationSize = 5 * train.getnInputs(); //heuristic
          }else{
        	  populationSize = 50;
          }
        }
        
        // Alpha for computation of Raw Fitness
        alpha = Double.parseDouble(parameters.getParameter(7));
        
        // Crossover probability, mutation probability, insertion probability, dropping condition probability
        probCross = Double.parseDouble(parameters.getParameter(8));
        probMut = Double.parseDouble(parameters.getParameter(9));
        probIns = Double.parseDouble(parameters.getParameter(10));
        probDrop = Double.parseDouble(parameters.getParameter(11));
        
        // Tournament Selection Size
        tournamentSize = Integer.parseInt(parameters.getParameter(12));
        
        // Global Fitness Weights
        w1 = Double.parseDouble(parameters.getParameter(13));
        w2 = Double.parseDouble(parameters.getParameter(14));
        w3 = Double.parseDouble(parameters.getParameter(15));
        w4 = Double.parseDouble(parameters.getParameter(16));

        // Normalize the genetic operators probabilities
        double sum_prob = probCross + probMut + probIns + probDrop;
        if (sum_prob != 1) {
        	probCross /= sum_prob;
        	probMut /= sum_prob;
        	probIns /= sum_prob;
        	probDrop /= sum_prob;
        }
        	
        // Allocate memory for the token competition procedure
        tokensGlobal = new boolean[train.getnData()];
    }

    /**
     * It launches the algorithm
     */
    public void execute() {
        if (somethingWrong) { //We do not execute the program
            System.err.println("An error was found, either the data-set have numerical values or missing values.");
            System.err.println("Aborting the program");
            //We should not use the statement: System.exit(-1);
        } else {
            //We do here the algorithm's operations
            Randomize.setSeed(seed);

            // First, we create the Data Base according to the number of labels indicated in the parameter file
            dataBase = new DataBase(train.getnInputs(), nLabels, train.getRanges(), train.varNames());
            
            // The algorithm starts initializing the population and evaluating it, then it copies the initial population to the best population
            initPopulation();
            
            // Evaluates all the population individuals
            evaluatePopulationIndividuals (population);
            
            // Evaluates the general population
            populationFitness = evaluatePopulation(population);
            
            bestPopulation = new ArrayList <Rule> (populationSize);
            for (int i=0; i<populationSize; i++) {
            	Rule aux_i = (Rule)population.get(i);
            	Rule new_aux_i = new Rule(aux_i);
            	bestPopulation.add(new_aux_i);
            }
            bestPopulationFitness = populationFitness;
            System.out.println("Global fitness obtained in generation [0]: " + bestPopulationFitness);
            
            for (int gens = 1; gens < nGenerations; gens++) {
            	int offspring_size = population.size();
            	ArrayList <Rule> offspring_population = new ArrayList <Rule> (offspring_size);
            	
            	for (int a=0; a<offspring_size; a++) {	
            		Rule new_rule = new Rule();
            		boolean insertion_possible = true;
            		boolean dropping_possible = true;
            		boolean generated_rule = false;
            		
            		// Select the parent for the genetic operation
            		int first_parent = tournamentSelection();
            		
            		do {
	            		// Select the genetic operation applied to the parent
	            		double random_value = Randomize.Rand();
	                    if ((random_value < probCross) && (population.size() > 1)) {
	                    	// Apply crossover operator
	                    	int second_parent = randomSelection(first_parent); // Select a new parent different from the first parent
	                    	new_rule = crossover (first_parent, second_parent);
	                    	generated_rule = true;
	                    } else if (random_value < (probCross + probMut)) {
	                    	// Apply mutation operator
	                    	new_rule = mutation (first_parent);
	                    	generated_rule = true;
	                    } else if ((random_value < (probCross + probMut + probIns)) && insertion_possible) {
	                    	// Apply insertion operator
	                    	new_rule = insertion (first_parent); 
	                    	if (new_rule.getnVar() == 0) {
	                    		insertion_possible = false;
	                    		generated_rule = false;
	                    	}
	                    	else {
	                    		generated_rule = true;
	                    	}
	                    } else if (dropping_possible) {
	                    	// Apply dropping condition operator
	                    	new_rule = dropping (first_parent); 
	                    	if (new_rule.getnVar() == 0) {
	                    		insertion_possible = false;
	                    		generated_rule = false;
	                    	}
	                    	else {
	                    		generated_rule = true;
	                    	}
	                    }
            		} while (!generated_rule);
            		
            		// The generated rule is added to the offspring population
            		offspring_population.add(new_rule);
            	}
            	
            	// We evaluate the rules generated
            	evaluatePopulationIndividuals(offspring_population);
            	
            	// Now, we update the current population with the generated rules
            	for (int rules=0; rules<offspring_population.size(); rules++) {
            		Rule selected_rule = (Rule)offspring_population.get(rules);
            		Rule new_selected_rule = new Rule(selected_rule);
            		population.add(new_selected_rule);
            	}
            	
            	// Then, we begin the token competition procedure
            	tokenCompetition();
            	
            	// After the token competition has been applied, we create type 2 rules for the
            	// samples that are not seized by any rule. This new rules are added to the current
            	// population
            	createRules();
            	
            	// Compute now the global fitness score for the population
            	populationFitness = evaluatePopulation(population);
            	
            	// Then, check if the new population is better than the original population
            	if (populationFitness > bestPopulationFitness) {
            		// Move the current population to best population
            		bestPopulation = new ArrayList <Rule> (population.size());
                    for (int i=0; i<population.size(); i++) {
                    	Rule aux_i = (Rule)population.get(i);
                    	Rule new_aux_i = new Rule(aux_i);
                    	bestPopulation.add(new_aux_i);
                    }
                    bestPopulationFitness = populationFitness;
                    System.out.println("Global fitness obtained in generation [" + gens + "]: " + bestPopulationFitness);
                }
            }
            
            // Prints the database used and the best rule base into the output files
            dataBase.writeFile(this.fileDB);
            writeFile(this.fileRB);

            // Finally we should fill the training and test output files
            double accTra = doOutput(this.val, this.outputTr);
            double accTst = doOutput(this.test, this.outputTst);

            System.out.println("Accuracy obtained in training: "+accTra);
            System.out.println("Accuracy obtained in test: "+accTst);
            System.out.println("Algorithm Finished");
        }
    }



    /**
     * Inits the population with a group of random rules
     */
    private void initPopulation() {
        population = new ArrayList <Rule> (populationSize);

        for (int i = 0; i < populationSize; i++) {
            Rule indi = new Rule(dataBase, train.getnClasses(), t_norm, t_conorm, ruleWeight);
            population.add(indi);
        }
    }

    /**
     * Evaluates the population individuals. If a rule was previously evaluated we do not evaluate it again
     * 
     * @param pop	Population whose individuals are going to be evaluated
     */
    private void evaluatePopulationIndividuals (ArrayList <Rule> pop) {
        for (int i = 0; i < pop.size(); i++) {
            if (pop.get(i).not_eval()) {
                pop.get(i).evaluate(train, alpha);
            }
        }
    }
    
    /**
     * Evaluates the population with the global measure
     * 
     * @param pop	Population that is going to be evaluated
     */
    private double evaluatePopulation (ArrayList <Rule> pop) {
    	int hits = 0; // Number of examples correctly classified, used to compute accuracy
    	int num_var = 0; // Number of variables used in the rules
    	int num_cond = 0; // Number of conditions used in the rules
    	int num_rules; // Number of rules
        double accuracy; // Accuracy used in the global fitness function computation
        double ave_var; // Average number of variables in the rules
        double ave_cond; // Average number of conditions in the rules
        double norm_var, norm_cond, norm_rul;
        double global_fitness = 0.0;
        
    	// Store the number of rules
        num_rules = pop.size();
        
        // Classify all examples
        for (int i = 0; i < train.getnData(); i++) {
          //for classification:
          int classOut = FRM(pop, train.getExample(i), train.getMissing(i));
          if (train.getOutputAsInteger(i) == classOut) {
            hits++;
          }
        }
        accuracy = 1.0*hits/train.size(); // Computation of the accuracy
        
        // Compute the average number of variables and conditions
        for (int i=0; i < num_rules; i++) {
        	num_var += ((Rule)pop.get(i)).getnVar();
        	num_cond += ((Rule)pop.get(i)).getnCond();
        }
        ave_var = ((double)num_var)/num_rules;
        ave_cond = ((double)num_cond)/num_rules;
        
        // Normalize the values 
        norm_var = ((double)(ave_var - 1))/((double)(train.getnInputs() - 1)); 
        norm_cond = ((double)(ave_cond - 1))/((double)((train.getnInputs()*(nLabels-1)) - 1)); 
        norm_rul = ((double)(num_rules - train.getnClasses()))/((double)(train.size() - train.getnClasses())); 
        
        // Compute global fitness
        global_fitness = w1 * accuracy + w2 * (1.0-norm_var) + w3 * (1.0-norm_cond) + w4 * (1.0-norm_rul);
        
        return global_fitness;
    }
    
    /**
     * Fuzzy Reasoning Method
     * 
     * @param pop	Population that is used to predict the class
     * @param example double[] the input example
     * @param missing boolean[] containing the missing values of the sample
     * @return int the predicted class label (id)
     */
    private int FRM (ArrayList <Rule> pop, double[] example, boolean [] missing) {
    	int clas;
    	ArrayList <Rule> pop_type1 = new ArrayList <Rule> ();
        ArrayList <Rule> pop_type2 = new ArrayList <Rule> ();
    	
    	for (int i=0; i<pop.size(); i++) {
        	Rule aux_i = (Rule)pop.get(i);
        	Rule new_aux_i = new Rule(aux_i);
        	
        	if (new_aux_i.getLevel() == 1)
        		pop_type1.add(new_aux_i);
        	else
        		pop_type2.add(new_aux_i);
        }
        
    	if (inferenceType == WINNING_RULE) {
        	clas = FRM_WR(pop_type1, example, missing);
        	if (clas == -1)
        		return FRM_WR(pop_type2, example, missing);
        	else
        		return clas;
        } 
        else if (inferenceType == NORMALIZED_SUM) {
        	clas = FRM_NS(pop_type1, example, missing);
        	if (clas == -1)
        		return FRM_NS(pop_type2, example, missing);
        	else
        		return clas;
        }
        else if (inferenceType == ARITHMETIC_MEAN) {
        	clas = FRM_AM(pop_type1, example, missing);
        	if (clas == -1)
        		return FRM_AM(pop_type2, example, missing);
        	else
        		return clas;
        }
        else {
            System.err.println("Undefined Fuzzy Reasoning Method");
            System.exit(-1);
            return -1;
        }
    }
    
    /**
     * Winning Rule FRM
     * 
     * @param pop	PPopulation that is used to predict the class
     * @param example double[] the input example
     * @param missing boolean[] containing the missing values of the sample
     * @return int the class label for the rule with highest membership degree to the example
     */
    private int FRM_WR (ArrayList <Rule> pop, double[] example, boolean [] missing) {
        int clas = -1;
        double max = 0.0;
        for (int i = 0; i < pop.size(); i++) {
            Rule r = (Rule)pop.get(i);
            if (r.not_eval()) {
            	System.err.println("Rule " + i + "th has not been evaluated. Therefore, we cannot obtain an output for a specified sample");
            	System.exit(-1);
            }
            
            double produc = r.compatibility(example, missing);
            produc *= r.getWeight();
            if (produc > max) {
                max = produc;
                clas = r.getClas();
            }
        }
        
        return clas;
    }
    
    /**
     * Normalized Sum FRM (also known as Additive Combination)
     * 
     * @param pop	PPopulation that is used to predict the class
     * @param example double[] the input example
     * @param missing boolean[] containing the missing values of the sample
     * @return int the class label for the set of rules with the highest sum of membership degree per class
     */
    private int FRM_NS (ArrayList <Rule> pop, double[] example, boolean [] missing) {
        int clas = -1;
        double[] class_degrees = new double[1];
        for (int i = 0; i < pop.size(); i++) {
        	Rule r = (Rule)pop.get(i);
            if (r.not_eval()) {
            	System.err.println("Rule " + i + "th has not been evaluated. Therefore, we cannot obtain an output for a specified sample");
            	System.exit(-1);
            }

            double produc = r.compatibility(example, missing);
            produc *= r.getWeight();
            if (r.getClas() > class_degrees.length - 1) {
                double[] aux = new double[class_degrees.length];
                for (int j = 0; j < aux.length; j++) {
                    aux[j] = class_degrees[j];
                }
                class_degrees = new double[r.getClas() + 1];
                for (int j = 0; j < aux.length; j++) {
                    class_degrees[j] = aux[j];
                }
            }
            class_degrees[r.getClas()] += produc;
        }
        double max = 0.0;
        for (int l = 0; l < class_degrees.length; l++) {
            if (class_degrees[l] > max) {
                max = class_degrees[l];
                clas = l;
            }
        }
        return clas;
    }
    
    /**
     * Arithmetic Mean FRM
     * 
     * @param pop	PPopulation that is used to predict the class
     * @param example double[] the input example
     * @param missing boolean[] containing the missing values of the sample
     * @return int the class label for the set of rules with the highest sum of membership degree per class
     */
    private int FRM_AM (ArrayList <Rule> pop, double[] example, boolean [] missing) {
        int clas = -1;
        double max = 0.0;
        int [] num_class = new int[1];
        double[] class_degrees = new double[1];
        
        for (int i = 0; i < pop.size(); i++) {
        	Rule r = (Rule)pop.get(i);
            if (r.not_eval()) {
            	System.err.println("Rule " + i + "th has not been evaluated. Therefore, we cannot obtain an output for a specified sample");
            	System.exit(-1);
            }

            double produc = r.compatibility(example, missing);
            produc *= r.getWeight();
            if (r.getClas() > class_degrees.length - 1) {
                double[] aux = new double[class_degrees.length];
                int[] aux2 = new int[class_degrees.length];
                
                for (int j = 0; j < aux.length; j++) {
                    aux[j] = class_degrees[j];
                    aux2[j] = num_class[j];
                }
                
                class_degrees = new double[r.getClas() + 1];
                num_class = new int[r.getClas() + 1];
                
                for (int j = 0; j < aux.length; j++) {
                    class_degrees[j] = aux[j];
                    num_class[j] = aux2[j];
                }
            }
            
            class_degrees[r.getClas()] += produc;
            num_class[r.getClas()]++;
        }
        
        for (int l = 0; l < class_degrees.length; l++) {
        	if (num_class[l] != 0){
        		class_degrees[l] /= num_class[l];
    		}
            if (class_degrees[l] > max) {
                max = class_degrees[l];
                clas = l;
            }
        }
        
        return clas;
    }   
    
    /**
     * Tournament selection procedure. It selects tournamentSize individuals from the population
     * and returns the best individual among them
     * 
     * @return the best individual among a set of individuals selected randomly from the population
     */
    private int tournamentSelection () {
    	int size_for_tournament;
    	int selected_individual;
    	int new_selection;
    	double selected_fitness;
    	ArrayList <Integer> selected_for_tournament;
    	
    	// Check the tournament size
    	if (tournamentSize > population.size())
    		size_for_tournament = population.size();
    	else
    		size_for_tournament = tournamentSize;
    	selected_for_tournament = new ArrayList <Integer> ();
    	
    	// Select the first individual in the tournament randomly
    	selected_individual = Randomize.Randint(0, population.size());
    	selected_fitness = ((Rule)population.get(selected_individual)).getFitness();
    	selected_for_tournament.add(new Integer(selected_individual));
    	
    	for (int i=1; i<size_for_tournament; i++) {
    		// Select a new individual for the tournament
    		do {
    			new_selection = Randomize.Randint(0, population.size());
    		} while (selected_for_tournament.contains(new Integer(new_selection)));
    		
    		selected_for_tournament.add(new Integer(new_selection));
    		double new_fitness = ((Rule)population.get(new_selection)).getFitness();
    		if (new_fitness > selected_fitness) {
    			selected_individual = new_selection;
    	    	selected_fitness = new_fitness;
    		}
    	}
    	
    	// Return the position of the best individual
    	return selected_individual;
    }

    /**
     * Random selection procedure. It selects a random individual from the population
     * different from the given parent
     * 
     * @param i	Previous selected individual that must be different from the random individual
     * @return a random individual from the population different from the given individual
     */
    private int randomSelection (int i) {
    	int selected_individual;
    	
    	// Select the individual randomly
    	do {
    		selected_individual = Randomize.Randint(0, population.size());
    	} while (selected_individual == i);
    	
    	return selected_individual;
    }    
    
    /**
     * Crates a new rule from two parents using the crossover operator defined in the GP-COACH algorithm
     * 
     * @param pos1	Position in the population of the first parent of the new rule
     * @param pos2	Position in the population of the second parent of the new rule
     * @return Rule	new rule created from two parents with the crossover operator
     */
    private Rule crossover (int pos1, int pos2) {
    	Rule new_rule = new Rule();
    	Rule main_parent, second_parent;
    	int cutpoint_selection;
    	
    	// First, we select the main parent
    	if (Randomize.Rand() < 0.5) {
			main_parent = (Rule)population.get(pos1);
			second_parent = (Rule)population.get(pos2);
		}
    	else {
    		main_parent = (Rule)population.get(pos2);
			second_parent = (Rule)population.get(pos1);
    	}
    	
    	// As the grammar rules are very strict, we select cut points in three situations
    	// a) a cut point can be a variable (or conjunction of variables) and the associated fuzzy labels
    	// b) a cut point can be the sets of labels of a variable
    	// c) a cut point can be the class of the rules
    	do {
    		new_rule = new Rule (main_parent);
	    	cutpoint_selection = Randomize.Randint(0, train.getnVars());
	    	
	    	if (cutpoint_selection >= train.getnInputs()) {
	    		// The cut point is the class
	    		new_rule.setClass(second_parent.getClas());
	    	}
	    	else {
	    		// The cut point is in the variables
	    		// Select a variable or a label set randomly
	    		if (Randomize.Rand() < 0.5) {
	    			// Cut point with variables
	    			// Select a set of variables from each parent
	    			boolean [] var_main_parent = new boolean [main_parent.getnVar()];
	    			ArrayList <FuzzyAntecedent> second_parent_variables;
	    			boolean selected, not_selected;
	    			
	    			if (var_main_parent.length > 1) {
		    			do {
		    				Arrays.fill (var_main_parent, false);
			    			selected = false;
			    			not_selected = false;
			    			
			    			for (int i=0; i<var_main_parent.length; i++) {
		    					if (Randomize.Rand() < 0.5) {
		    						var_main_parent[i] = true;
		    						selected = true;
		    					}
		    					else {
		    						not_selected = true;
		    					}
		    				}
			    		} while ((!selected) || (!not_selected));
	    			}
	    			else {
	    				var_main_parent[0] = true;
	    			}
	    			
	    			if (second_parent.getnVar() > 1) {
		    			do {
		    				second_parent_variables = new ArrayList <FuzzyAntecedent> ();
			    			selected = false;
			    			not_selected = false;
			    			
		    				for (int i=0; i<second_parent.getnVar(); i++) {
		    					if (Randomize.Rand() < 0.5) {
		    						second_parent_variables.add(second_parent.getVar(i));
		    						selected = true;
		    					}
		    					else {
		    						not_selected = true;
		    					}
		    				}
		    			} while ((!selected) || (!not_selected));
	    			}
	    			else {
	    				second_parent_variables = new ArrayList <FuzzyAntecedent> ();
		    			second_parent_variables.add(second_parent.getVar(0));
	    			}
	    			
	    			// Create a new rule from the main parent, only with the selected
	    			// variables from it and adding the selected variables from the second parent
	    			new_rule.exchangeVariables(var_main_parent, second_parent_variables);
	    		}
	    		else {
	    			// Cut point with labels
	    			// We exchange the fuzzy labels from a value to a set of fuzzy labels from the another parent
	    			int labels_selected_main = Randomize.Randint(0, main_parent.getnVar());
	    			int labels_selected_second = Randomize.Randint(0, second_parent.getnVar());
	    			
	    			new_rule.exchangeAntecedentLabel(labels_selected_main, second_parent.getVar(labels_selected_second), dataBase);
	    		}
	    	}
    	} while (new_rule.getnVar() == 0);
    	
    	return new_rule;
    }
    
    /**
     * Crates a new rule from a parent using the mutation operator defined in the GP-COACH algorithm
     * 
     * @param pos	Position in the population of the parent of the new rule
     * @return Rule	new rule created from the parent with the mutation operator
     */
    private Rule mutation (int pos) {
    	Rule new_rule = new Rule();
    	Rule parent;
    	int variable_mutated;
    	boolean generated_rule = false;
    	double prob_mut_a = 1.0/3.0;
    	double prob_mut_b = 2.0/3.0;
    	
    	// Obtain the parent
    	parent = (Rule)population.get(pos);
    	new_rule = new Rule (parent);
    	
    	// First, we select the variable in the rule that is going to be mutated
    	variable_mutated = Randomize.Randint(0, parent.getnVar());
    	
    	boolean label_addition = true;
    	boolean label_deletion = true;
    	
    	do {
    		double mut_selection = Randomize.Rand();
    		
    		if ((mut_selection < prob_mut_a) && (label_addition)) {
            	// Apply label addition
    			if ((((FuzzyAntecedent)new_rule.getVar(variable_mutated)).getnLabels() + 1) == nLabels) {
    				label_addition = false;
    			}
    			else {
    				new_rule.addLabel(variable_mutated, dataBase);
    				generated_rule = true;
    			}
    		} else if ((mut_selection < prob_mut_b) && (label_deletion)) {
                // Apply label deletion
            	if (((FuzzyAntecedent)new_rule.getVar(variable_mutated)).getnLabels() == 1) {
    				label_deletion = false;
    			}
    			else {
    				new_rule.deleteLabel(variable_mutated);
    				generated_rule = true;
    			}
            } else  {
            	// Apply label change
            	new_rule.changeLabel(variable_mutated, dataBase);
            	generated_rule = true;
            }
    	} while (!generated_rule);
    	
    	return new_rule;
    }
    
    /**
     * Crates a new rule from a parent using the insertion operator defined in the GP-COACH algorithm
     * 
     * @param pos	Position in the population of the parent of the new rule
     * @return Rule	new rule created from the parent with the insertion operator
     */
    private Rule insertion (int pos) {
    	Rule new_rule = new Rule();
    	Rule parent;
    	
    	// Obtain the parent
    	parent = (Rule)population.get(pos);
    	new_rule = new Rule (parent);
    	
    	// Check if we can insert a variable
    	if (parent.getnVar() == train.getnInputs()) {
    		// We cannot add any more variables, so we return a rule with an empty antecedent
    		new_rule.clearAntecedent();
    	}
    	else {
    		// We can still add a variable, so we randomly create one for the rule
    		new_rule.addVar(dataBase);
    	}
    	
    	return new_rule;
    }
    
    /**
     * Crates a new rule from a parent using the dropping condition operator defined in the GP-COACH algorithm
     * 
     * @param pos	Position in the population of the parent of the new rule
     * @return Rule	new rule created from the parent with the dropping condition operator
     */
    private Rule dropping (int pos) {
    	Rule new_rule = new Rule();
    	Rule parent;
    	
    	// Obtain the parent
    	parent = (Rule)population.get(pos);
    	new_rule = new Rule (parent);
    	
    	// Check if we can delete a variable
    	if (parent.getnVar() == 1) {
    		// We cannot delete any variables, so we return a rule with an empty antecedent
    		new_rule.clearAntecedent();
    	}
    	else {
    		// We can delete a variable, so we randomly delete one in the rule
    		new_rule.deleteVar();
    	}
    	
    	return new_rule;
    }

    /**
     * Runs the token competition procedure in order to maintain the diversity of the population.
     * It also deletes the rules that are not competitive according to this token competition procedure.
     */
    private void tokenCompetition() {
        Collections.sort(population);

        // Empty the general token count
        for (int i = 0; i < train.getnData(); i++) {
            tokensGlobal[i] = false;
        }
        
        // Update the penalized fitness value for all rules in the population
        for (int i = 0; i < population.size(); i++) {
            int count = 0;
            Rule selected_rule = (Rule)population.get(i);
        	
            if (selected_rule.ideal() == 0) {
                selected_rule.setPenalizedFitness(0.0);
            } else {
                for (int j = 0; j < train.getnData(); j++) {
                    if ((selected_rule.isSeized(j)) && (!tokensGlobal[j])) {
                        tokensGlobal[j] = true;
                        count++;
                    }
                }
                selected_rule.setPenalizedFitness(selected_rule.getFitness() * (1.0 * count / selected_rule.ideal()));
            }
        }
        
        // Delete the rules that have a penalized fitness equal to 0.0
        ArrayList <Rule> aux_population = new ArrayList <Rule> ();
        for (int i = 0; i < population.size(); i++) {
        	Rule selected_rule = (Rule)population.get(i);
        	Rule new_selected_rule = new Rule(selected_rule);
        	if (selected_rule.getPenalizedFitness() > 0.0) {
        		aux_population.add(new_selected_rule);
        	}
        }
        population = aux_population;
    }
    
    /**
     * Create type 2 rules according to the examples that were not covered with the other rules
     */
    private void createRules () {
        // Obtain the tokens which weren't seized by any rule
        ArrayList <Integer> free_tokens = new ArrayList <Integer> ();
        for (int j = 0; j < tokensGlobal.length; j++) {
            if (!tokensGlobal[j]) {
            	free_tokens.add(new Integer(j));
            }
        }
        
        // Create type 2 rules
        while (free_tokens.size() > 0) {
        	// First, we select randomly a sample that was not covered by a rule and create a rule from that sample
	        int selected_token = ((Integer)(free_tokens.get(Randomize.Randint(0, free_tokens.size())))).intValue();
	        Rule new_rule = new Rule(dataBase, train.getnClasses(), t_norm, t_conorm, ruleWeight, train.getExample(selected_token), train.getOutputAsInteger(selected_token));
	        new_rule.evaluate(train, alpha);
	        population.add(new_rule);
	        
	        // Check which rules are still free after the usage of this rule
	        ArrayList <Integer> new_free_tokens = new ArrayList <Integer> ();
	        for (int i=0; i<free_tokens.size(); i++) {
	        	int current_token = ((Integer)(free_tokens.get(i))).intValue();
	        	if (!new_rule.isSeized(current_token)) {
	                new_free_tokens.add(free_tokens.get(i));
	            }
	        }
	        free_tokens = new_free_tokens;
        }
    }

    /**
     * It prints the rule base into an string
     * 
     * @return String an string containing the rule base
     */
    private String printString() {
        int i;
        String cadena = "";

        cadena += "@Number of rules: " + bestPopulation.size() + "\n\n";
        for (i = 0; i < bestPopulation.size(); i++) {
            cadena = cadena + (i + 1) + ": " + ((Rule)bestPopulation.get(i)).printString(train.varNames(), train.classNames());
        }

        return (cadena);
    }
    
    /**
     * It writes the rule base into an ouput file
     * 
     * @param filename String the name of the output file
     */
    private void writeFile (String filename) {
        String outputString = new String("");
        outputString = printString();
        Files.writeFile(filename, outputString);
    }
    
    /**
     * It generates the output file from a given dataset and stores it in a file
     * @param dataset myDataset input dataset
     * @param filename String the name of the file
     *
     * @return The classification accuracy
     */
    private double doOutput(myDataset dataset, String filename) {
      String output = new String("");
      int hits = 0;
      output = dataset.copyHeader(); // We insert the header in the output file
      
      // We write the output for each example
      for (int i = 0; i < dataset.getnData(); i++) {
          // for classification:
    	  int class_estimation = FRM (bestPopulation, dataset.getExample(i), dataset.getMissing(i));
    	  
    	  String output_estimation = new String("?");
    	  if (class_estimation >= 0) {
    		  output_estimation = dataset.getOutputValue(class_estimation);
    	  }
    	  
    	  output += dataset.getOutputAsString(i) + " " + output_estimation + "\n";
    	  if (dataset.getOutputAsString(i).equalsIgnoreCase(output_estimation)){
    		  hits++;
    	  }
      }
      Files.writeFile(filename, output);
      
      return (1.0*hits/dataset.size());
    }    
}
