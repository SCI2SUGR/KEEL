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
import java.util.Collections;

/**
 * <p>Title: Fuzzy Antecedent </p>
 *
 * <p>Description: Fuzzy Antecedent for a variable in the GP-COACH algorithm </p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Victoria Lopez (University of Granada) 19/11/2011
 * @version 1.5
 * @since JDK1.5
 */

public class FuzzyAntecedent implements Comparable {
	private ArrayList <Fuzzy> labels;
	private int max_label;
	private int var;
	
    /**
     * Default constructor
     */
    public FuzzyAntecedent () {
    }
	
    /**
     * Constructor with parameters. It creates a random fuzzy antecedent
     * for a specified variable
     * 
     * @param database	Data Base associated to the rule that uses this antecedent
     * @param var_selected	Specified variable that uses this antecedent
     */
	public FuzzyAntecedent (DataBase database, int var_selected) {
		Fuzzy label;
		
		max_label = database.numLabels();
		labels = new ArrayList <Fuzzy> (max_label);
		var = var_selected;
		
		// Select randomly a set of labels and create the fuzzy labels for each selected value
		for (int i=0; i<max_label; i++) {
			if (Randomize.Rand() < 0.5) {
				label = database.clone(var_selected, i);
				labels.add(label);
			}
		}		
	}
	
    /**
     * Constructor with parameters. It creates a fuzzy antecedent using a given fuzzy label
     * for a specified variable
     * 
     * @param label	Fuzzy label given to create this fuzzy antecedent
     * @param var_selected	Specified variable that uses this antecedent
     * @param max	Maximum number of labels this antecedent can tolerate
     */
	public FuzzyAntecedent (Fuzzy label, int var_selected, int max) {
		max_label = max;
		labels = new ArrayList <Fuzzy> ();
		var = var_selected;
		
		labels.add(label);		
	}
	
	/**
	 * Copy constructor for a FuzzyAntecedent from another FuzzyAntecedent
	 * 
	 * @param original	FuzzyAntecedent which will be used as base to create another FuzzyAntecedent
	 */
	public FuzzyAntecedent (FuzzyAntecedent original) {
		max_label = original.max_label;
		var = original.var;
		
		labels = new ArrayList <Fuzzy> (original.labels.size());
		for (int i=0; i<original.labels.size(); i++) {
			Fuzzy copy_label = ((Fuzzy)original.labels.get(i)).clone();
			labels.add(copy_label);
		}
	}
	
	/**
	 * Constructor for a FuzzyAntecedent that builds it from another FuzzyAntecedent applying a lateral tuning
	 * 
	 * @param original	FuzzyAntecedent which will be used as base to create another FuzzyAntecedent
	 * @param granularity_tuning	Matrix codifying the lateral tuning used to create the new FuzzyAntecedent
	 */
	public FuzzyAntecedent (FuzzyAntecedent original, double[][] granularity_tuning) {
		max_label = original.max_label;
		var = original.var;
		
		labels = new ArrayList <Fuzzy> (original.labels.size());
		for (int i=0; i<original.labels.size(); i++) {
			Fuzzy new_label = ((Fuzzy)original.labels.get(i)).clone();
			new_label.lateralDisplace(granularity_tuning[var][new_label.label]);
			
			labels.add(new_label);
		}
	}

	/**
	 * Checks if the fuzzy antecedent is a valid and representative antecedent for a rule
	 * or not. If there are no labels in the antecedent or if all the labels are in the
	 * antecedent, it is no use to use this antecedent.
	 * 
	 * @return true if the labels selected form an antecedent, false if the labels selected
	 * do not form an antecedent (there are no labels, or all labels are selected)
	 */
	public boolean isValid() {
		if ((labels.size() == 0) || (labels.size() == max_label))
			return false;
		else
			return true;
	}
	
	/**
	 * Checks if the fuzzy antecedent represents an any condition. It happens when all the 
	 * labels are in the antecedent, so it is no use to use this antecedent.
	 * 
	 * @return true if all labels are selected to form an antecedent, thus creating an any condition
	 */
	public boolean isAny() {
		if (labels.size() == max_label)
			return true;
		else
			return false;
	}
	
	/**
	 * Obtains the variable related to the fuzzy antecedent
	 * 
	 * @return	the variable that uses this antecedent
	 */
	public int getAttribute () {
		return var;
	}
	
    /**
     * Obtains the number of labels used in this fuzzy antecedent as condition
     * 
     * @return number of labels used in this fuzzy antecedent as condition
     */
    public int getnLabels() {
    	return labels.size();
    }
	
	/**
	 * Computes the matching degree of a specified value with a set of fuzzy labels, using
	 * a specified t_conorm as OR operator
	 * 
	 * @param value	Real value that is going to be matched with the fuzzy labels in this fuzzy antecedent
	 * @param t_conorm T-conorm used as OR operator
	 * @return	matching degree of the double value with the set of labels that represent an attribute
	 */
	public double matchingDegree(double value, int t_conorm) {
        if (t_conorm == GP_COACH_H.MAXIMUM) {
			return maximumMatchingDegree (value);
		}
		else if (t_conorm == GP_COACH_H.PROBABILISTIC_SUM) {
			return probabilisticSumMatchingDegree (value);
		}
		else {
			System.err.println("Unknown t-conorm for the computation of the compatibility degree");
			System.exit(-1);
			return -1.0;
		}
	}
	
	/**
	 * Computes the matching degree of a specified value with a set of fuzzy labels, using
	 * MAXIMUM as OR operator
	 * 
	 * @param value	Real value that is going to be matched with the fuzzy labels in this fuzzy antecedent
	 * @return	matching degree of the double value with the set of labels that represent an attribute
	 * using MAXIMUM as OR operator
	 */
	private double maximumMatchingDegree (double value) {
    	double maximum, membershipDegree;
    	
    	maximum = 0.0;
    	for (int i = 0; i < labels.size() ; i++) {
    		membershipDegree = ((Fuzzy)labels.get(i)).Fuzzify(value);
    		maximum = Math.max(membershipDegree, maximum);
    	}
    	
    	return (maximum);
	}
	
	/**
	 * Computes the matching degree of a specified value with a set of fuzzy labels, using
	 * PROBABILISTIC_SUM as OR operator
	 * 
	 * @param value	Real value that is going to be matched with the fuzzy labels in this fuzzy antecedent
	 * @return	matching degree of the double value with the set of labels that represent an attribute
	 * using PROBABILISTIC_SUM as OR operator
	 */
	private double probabilisticSumMatchingDegree (double value) {
    	double probabilistic_sum, membershipDegree;
    	
    	probabilistic_sum = 0.0;
    	for (int i = 0; i < labels.size() ; i++) {
    		membershipDegree = ((Fuzzy)labels.get(i)).Fuzzify(value);
            probabilistic_sum = probabilistic_sum + membershipDegree - (probabilistic_sum * membershipDegree);
    	}
    	
    	return (probabilistic_sum);
	}
	
	/**
	 * Change the labels associated to this fuzzy antecedent with the labels of the given
	 * antecedent
	 * 
	 * @param var_fuzzy	antecedent whose labels are going to be assigned to this antecedent
	 * @param data	data base containing all the information about the labels used in this
	 * program
	 */
	public void changeLabels (FuzzyAntecedent var_fuzzy, DataBase data) {
		Fuzzy label;
		
		// First, we copy the maximum number of labels
		max_label = data.numLabels();
		
		// Then we copy the labels according to the variable that this antecedent uses
		labels = new ArrayList <Fuzzy>(var_fuzzy.labels.size());
		for (int i=0; i<var_fuzzy.labels.size(); i++) {
			label = data.clone(var, ((Fuzzy)var_fuzzy.labels.get(i)).label);
			labels.add(label);
		}
	}
	
	/**
	 * Mix the labels associated to this FuzzyAntecedent with the labels associated to 
	 * another FuzzyAntecedent
	 * 
	 * @param var	antecedent whose labels are going to be mixed to this antecedent
	 */
	public void mixLabels (FuzzyAntecedent var_fuzzy) {
		Fuzzy label;
		
		// First, we check if both fuzzy antecedents are compatible
		if ((max_label != var_fuzzy.max_label) || (var != var_fuzzy.var)) {
			System.err.println("We cannot mix incompatible fuzzy antecedents");
			System.exit(-1);
		}
    	
    	// Mix the labels from the new fuzzy antecedent
		for (int i=0; i<var_fuzzy.labels.size(); i++) {
			boolean found = false;
			
			for (int j=0; j<labels.size() && !found; j++) {
				if (((Fuzzy)var_fuzzy.labels.get(i)).label == ((Fuzzy)labels.get(j)).label) {
					found = true;
				}
			}
			
			if (!found) {
				label = (Fuzzy)var_fuzzy.labels.get(i);
				labels.add(label);
			}
		}
	}
	
	/**
	 * Adds randomly a label to the label set of this fuzzy antecedent
	 * 
	 * @param data	data base containing all the information about the labels used
	 */
	public void addLabel (DataBase data) {
		boolean found_label = false;
		int selected_label;
		
		// Find a label that we do not have in the label set yet
		do {
			selected_label = Randomize.Randint(0, max_label);
			found_label = false;
			
			for (int i=0; i<labels.size() && !found_label; i++) {
				if (((Fuzzy)labels.get(i)).label == selected_label) {
					found_label = true;
				}
			}
		} while (found_label);
		
		// Add this label to the Fuzzy set
		Fuzzy new_label;
		new_label = data.clone(var, selected_label);
		labels.add(new_label);
	}
	
	/**
	 * Deletes randomly a label from the label set of this fuzzy antecedent
	 */
	public void deleteLabel () {
		int selected_label;
		
		// Select a label from the fuzzy antecedent
		selected_label = Randomize.Randint(0, labels.size());
		
		// Delete this label from the Fuzzy set
		labels.remove(selected_label);
	}
	
	/**
	 * Changes randomly a label from the label set of this fuzzy antecedent to a non-existing label
	 * in this fuzzy antecedent
	 * 
	 * @param data	data base containing all the information about the labels used
	 */
	public void changeLabel (DataBase data) {
		boolean found_label = false;
		int new_label, old_label;
		
		// Find a label that we do not have in the label set yet
		do {
			new_label = Randomize.Randint(0, max_label);
			found_label = false;
			
			for (int i=0; i<labels.size() && !found_label; i++) {
				if (((Fuzzy)labels.get(i)).label == new_label) {
					found_label = true;
				}
			}
		} while (found_label);
		
		// Select a label from the fuzzy antecedent
		old_label = Randomize.Randint(0, labels.size());
		// Delete this label from the Fuzzy set
		labels.remove(old_label);
		
		// Add the selected label to the Fuzzy set
		Fuzzy new_label_in_antecedent;
		new_label_in_antecedent = data.clone(var, new_label);
		labels.add(new_label_in_antecedent);
	}
	
	/**
	 * Obtains the maximum number of labels this fuzzy antecedent can handle
	 * 
	 * @return	the maximum number of labels this fuzzy antecedent can handle
	 */
	public int getMaxLabels () {
		return max_label;
	}
	
    /**
     * String representation of a Fuzzy Antecedent in the GP-COACH algorithm.
     *
     * @return String a string containing the Fuzzy Antecedent
     */
	public String printString() {
		String fuzzy_string = "";
		
		if (labels.size() == 1) {
			fuzzy_string += (labels.get(0)).name;
		}
		else {
			Collections.sort(labels);
	    	
			fuzzy_string += "(";
			for (int i=0; i<labels.size()-1; i++) {
				fuzzy_string = fuzzy_string + labels.get(i).name + " OR ";
			}
			fuzzy_string = fuzzy_string + labels.get(labels.size()-1).name + ")";
		}
		
		return fuzzy_string;
	}
	
    /**
     * Compares this FuzzyAntecedent with another FuzzyAntecedent to check if both FuzzyAntecedents are equal
     * 
     * @param o	FuzzyAntecedent that is going to be compared with the current FuzzyAntecedent
     * @return true, if the FuzzyAntecedents are the same; false, otherwise
     */
	public boolean equals (Object o) {
		  if (o == null)
			  return false;
		  if (o == this)
			  return true;
		  if (!(o instanceof FuzzyAntecedent))
			  return false;
		  
		  FuzzyAntecedent fa = (FuzzyAntecedent) o;
		  
		  if (max_label != fa.max_label)
			  return false;
		  
		  if (var != fa.var)
			  return false;
		  
		  if (labels.size() != fa.labels.size())
			  return false;
		  
		  for (int i=0; i<labels.size(); i++) {
			  Fuzzy a = (Fuzzy)labels.get(i);
			  Fuzzy b = (Fuzzy)fa.labels.get(i);
			  
			  if (!a.equals(b))
				  return false;
		  }
		  		  
		  return true;
	  }
	  
	  /**
	   * Computes the hash code associated to the current FuzzyAntecedent
	   * 
	   * @return the hash code associated to the current FuzzyAntecedent
	   */
	  public int hashCode() {
		  int result = 17;
		  
		  result = 31 * result + max_label;
		  result = 31 * result + var;
		  
		  for (int i=0; i<labels.size(); i++) {
			  result = 31 * result + ((Fuzzy)labels.get(i)).hashCode();
		  }
		  
		  return result;
	  }
	
    /**
     * Compares this object with the specified object for order, according to the number of variable measure 
     * 
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object
     */
    public int compareTo(Object a) {
        if (((FuzzyAntecedent) a).var > this.var) {
            return -1;
        }
        if (((FuzzyAntecedent) a).var < this.var) {
            return 1;
        }
        return 0;
    }
}
