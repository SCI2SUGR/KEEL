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

package keel.Algorithms.Decision_Trees.PUBLIC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Random;

/**
 * 
 * File: Node.java
 * 
 * Data structure that is used in the construction of the decision tree.
 * It stores the information about the attributes with a list of Registers for each
 * attribute. The node also has a histogram that resumes this information.
 * 
 * @author Written by Victoria Lopez Morales (University of Granada) 15/03/2009 
 * @version 1.0 
 * @since JDK1.5
 */

public class Node {
    /**
     * Identifier of the node
     */
    int identifier;
    
    /**
     * Name of each attribute of the node
     */
    ArrayList <myAttribute> the_attributes;
	
    /**
     * Separate list for each attribute
     */
	ArrayList <ArrayList <Register>> attributes;
	
	// Histogram for the attributes
	/**
	 * Number of rows for the histograms of the attributes
	 */
	int [] rows;
	/**
	 * Number of columns for the histograms
	 */
	int [] columns;
	/**
	 * Histogram for categorical attributes
	 */
	ArrayList <int [][]> count_matrix;
	/**
	 * Histogram for continuous attributes c_below
	 */
	ArrayList <int [][]> c_below;
	/**
	 * Histogram for continuous attributes c_above
	 */
	ArrayList <int [][]> c_above;
	
	/**
	 * Generator for pseudorandom numbers
	 */
	Random generator;
	
    /** 
     * Creates a node with a base constructor that doesn't initialize the node structures
     */     
    public Node () {
    }
	
    /** 
     * Creates a node from a complete dataset and its corresponding id. It is the most common use for
     * constructing a node
     *
     * @param data  Dataset that has the data that is going to be stored in the node
     * @param id   Number identifying the node that is being created
     */     
	public Node (myDataset data, int id) {
	    myAttribute att;
	    Register reg;
	    ArrayList <Register> reglist;
	    
	    identifier = id;
	    the_attributes = new ArrayList <myAttribute> ();
	    attributes = new ArrayList <ArrayList <Register>> ();
	    
	    // (1) Constructing attribute lists
	    for (int j=0; j<data.getNumAtr(); j++) {
	        // Allocate an array list for the attribute
	        reglist = new ArrayList <Register> ();
	        att = data.getAttributeI (j);
	        
	        // Add the array list to the_attributes list
	        the_attributes.add(new myAttribute (att));
	        
	        // Add all the information into the attribute list with registers
	        for (int i=0; i<data.getNumIns(); i++) {
	            reg = new Register (i, data.getDataI(i,j), data.getOutputI(i));
	            reglist.add(reg);
	        }
	        
	        attributes.add(reglist);
	    }
	    
	    // Printing attribute lists to check them 
	    /*for (int i=0; i<attributes.size(); i++) {
	        System.out.println(attributesNames.get(i));
	        for (int j=0; j<attributes.get(i).size(); j++) {
	            System.out.println(attributes.get(i).get(j));
	        }
	    }*/
	    
	    // (2) Sorting the attribute lists for numeric attributes
	    for (int i=0; i<data.getNumAtr(); i++) {
			if (!data.getAttributeI(i).isNominal()) {
			    for (int j=0; j<data.getNumIns(); j++) {
	           		Collections.sort(attributes.get(i));
				}
			}
	    }
	    
	    // Printing attribute lists to check them 
        /*for (int i=0; i<attributes.size(); i++) {
            System.out.println(attributesNames.get(i));
            for (int j=0; j<attributes.get(i).size(); j++) {
                System.out.println(attributes.get(i).get(j));
            }
        }*/
        
	    // (3) Constructing the histogram for the class distribution
	    rows = new int[data.getNumAtr()];
	    columns = new int[data.getNumAtr()];
	    
	    // First, we create the information about the data stored in the node
        for (int i=0; i<data.getNumAtr(); i++) {
            if (!data.getAttributeI(i).isNominal()) {
                // Numeric attributes
                int aux_columns;
                aux_columns = data.getOutputAttribute().getValues().size();
                
                rows[i] = 1;
                columns[i] = aux_columns;
            }
            else {
                // Categorical attributes
                int aux_rows, aux_columns;
                aux_rows = data.getAttributeI(i).getValues().size();
                aux_columns = data.getOutputAttribute().getValues().size();
                
                rows[i] = aux_rows;
                columns[i] = aux_columns;
            }
        }
        
        // Then, we calculate all the histograms 
        calculateHistograms ();
        
        // We initialize the generator of random numbers
        generator = new Random(12345678);
        
        // Printing the class histograms to check them 
        /*for (int i=0; i<data.getNumAtr(); i++) {
            System.out.println(the_attributes.get(i).getName());
            
            if (!data.getAttributeI(i).isNominal()) {
                // Numeric attributes
                System.out.print("C_below:");
                for (int j=0; j<columns[i]; j++) {
                    System.out.print(" " + c_below.get(i)[0][j]);
                }
                System.out.println();
                System.out.print("C_above:");
                for (int j=0; j<columns[i]; j++) {
                    System.out.print(" " + c_above.get(i)[0][j]);
                }
                System.out.println();
            }
            else {
                // Categorical attributes
                System.out.println("Count matrix:");
                for (int j=0; j<rows[i]; j++) {
                    for (int k=0; k<columns[i]; k++) {
                        System.out.print(count_matrix.get(i)[j][k] + " ");
                    }
                    System.out.println();
                }
            }
        }*/	
	}
	
	/** 
     * Creates a node from another existing node
     *
     * @param nod  Original node from which we are going to create a copy
     */   
    public Node (Node nod) {
	    ArrayList <Register> auxregister;
        int [][] aux_count_matrix;
        int [][] aux_c_below;
        int [][] aux_c_above;
        
        // Copy each data field to the new attribute
        this.identifier = nod.identifier;
        
        // Copy the_attributes list
	    this.the_attributes = new ArrayList <myAttribute>();
        for (int i=0; i<nod.attributes.size(); i++) {
            this.the_attributes.add(new myAttribute(nod.the_attributes.get(i)));
        }

        this.attributes = new ArrayList <ArrayList<Register>>();
        for (int i=0; i<nod.attributes.size(); i++) {
            auxregister = new ArrayList <Register> ();
            for (int j=0; j<nod.attributes.get(i).size(); j++) {
                auxregister.add(new Register(nod.attributes.get(i).get(j)));
            }
            this.attributes.add(auxregister);
        }
        
        // Copy the histograms
        this.rows = new int [nod.attributes.size()];
        System.arraycopy(nod.rows, 0, rows, 0, nod.rows.length);
        this.columns = new int [nod.attributes.size()];
        System.arraycopy(nod.columns, 0, columns, 0, nod.columns.length);
        
        this.count_matrix = new ArrayList <int [][]> (); // Histogram for categorical
        this.c_below = new ArrayList <int [][]> (); // Histogram for continuous
        this.c_above = new ArrayList <int [][]> (); // Histogram for continuous
        
        for (int i=0; i<nod.attributes.size(); i++) {
            if (!the_attributes.get(i).isNominal()) {
                aux_count_matrix = new int[1][1];
                aux_c_below = new int[1][columns[i]];
                aux_c_above = new int[1][columns[i]];
                
                for (int j=0; j<columns[i]; j++) {
                    aux_c_below[0][j] = nod.c_below.get(i)[0][j];
                    aux_c_above[0][j] = nod.c_above.get(i)[0][j];
                }
                
                count_matrix.add(aux_count_matrix);
                c_below.add(aux_c_below);
                c_above.add(aux_c_above);
            }
            else {
                aux_count_matrix = new int[rows[i]][columns[i]];
                aux_c_below = new int[1][1];
                aux_c_above = new int[1][1];
                
                for (int j=0; j<rows[i]; j++) {
                    for (int k=0; k<columns[i]; k++) {
                        aux_count_matrix[j][k] = nod.count_matrix.get(i)[j][k];
                    }
                }
                
                count_matrix.add(aux_count_matrix);
                c_below.add(aux_c_below);
                c_above.add(aux_c_above);
            }
        }
        
        this.generator = nod.generator;
	}
	
    /** 
     * Checks if a node is the same node as another object
     *
     * @param obj  Object that is checked to see if it is the same node
     * @return true if the nodes are the same, false otherwise
     * @see java.lang.Object#equals(java.lang.Object)
     */ 
    public boolean equals (Object obj) {
        boolean eq;
        
        // First we check if the reference is the same
        if (this == obj)
            return true;
        
        // Then we check if the object exists and is from the class Node
        if((obj == null) || (obj.getClass() != this.getClass()))
            return false;
        
        // object must be Node at this point
        Node test = (Node)obj;
        
        // We check the class attributes of the Node class
        eq = (identifier == test.identifier); 
        eq = eq && ((the_attributes == test.the_attributes) || (the_attributes != null && the_attributes.equals(test.the_attributes)));
        eq = eq && ((attributes == test.attributes) || (attributes != null && attributes.equals(test.attributes)));

        if (eq) {
            for (int i=0; i<attributes.size(); i++) {
                if (rows[i] != test.rows[i])
                    return false;
                else if (columns[i] != test.columns[i])
                    return false;
                else {
                    if (rows[i] > 1) {
                        // Categorical attribute
                        for (int j=0; j<rows[i]; j++) {
                            for (int k=0; k<columns[i]; k++) {
                                if (count_matrix.get(i)[j][k] != test.count_matrix.get(i)[j][k]) { 
                                    return false;
                                }
                            }
                        }
                    }
                    else {
                        // Numerical attribute
                        for (int j=0; j<columns[j]; j++) {
                            if (c_above.get(i)[0][j] != test.c_above.get(i)[0][j])
                                return false;
                            else if (c_below.get(i)[0][j] != test.c_below.get(i)[0][j])
                                return false;
                        }
                    }
                }
            }
        }

        return eq;
    }

    /** 
     * Hash-code function for the class that is used when object is inserted in a structure like a hashtable
     *
     * @return the hash code obtained
     * @see java.lang.Object#hashCode()
     */ 
    public int hashCode() {
        int hash = 7;
                
        hash = 31 * hash + identifier;
        hash = 31 * hash + (null == the_attributes ? 0 : the_attributes.hashCode());
        hash = 31 * hash + (null == attributes ? 0 : attributes.hashCode());
        return hash;
    }
    
    /** 
     * Overriden function that converts the class to a string
     *
     * @return the string representation of the class
     * @see java.lang.Object#toString()
     */ 
    public String toString() { 
        String aux;
        
        aux = new String("Node " + identifier + "\n");
        
        for (int i=0; i<attributes.size(); i++) {
            aux = aux + the_attributes.get(i).getName() + "\nAttribute list:\n";
            
            // Print attribute lists
            for (int j=0; j<attributes.get(i).size(); j++) {
                aux = aux + attributes.get(i).get(j) + "\n";
            }
            
            // Print histograms
            aux = aux + "Histogram\n";
            if (!the_attributes.get(i).isNominal()) {
                // Numeric attributes
                aux += "C_below:";
                for (int j=0; j<columns[i]; j++) {
                    aux = aux + " " + c_below.get(i)[0][j];
                }
                aux += "\nC_above:";
                for (int j=0; j<columns[i]; j++) {
                    aux = aux + " " + c_above.get(i)[0][j];
                }
                aux += "\n";
            }
            else {
                // Categorical attributes
                aux += "Count matrix:\n";
                for (int j=0; j<rows[i]; j++) {
                    for (int k=0; k<columns[i]; k++) {
                        aux = aux + count_matrix.get(i)[j][k] + " ";
                    }
                    aux += "\n";
                }
            }
        }
        
        return aux;
    }
    
    /**
     * Calculate the histograms for all the attributes of the node taking in account if they are categorical
     * attributes or numerical attributes
     */
    private void calculateHistograms () {
        int [][] aux_count_matrix;
        int [][] aux_c_below;
        int [][] aux_c_above;
        Register reg;
        
        count_matrix = new ArrayList <int [][]> (); // Histogram for categorical
        c_below = new ArrayList <int [][]> (); // Histogram for continuous
        c_above = new ArrayList <int [][]> (); // Histogram for continuous
        
        for (int i=0; i<attributes.size(); i++) {
            if (!the_attributes.get(i).isNominal()) {
                // Numeric attributes
                aux_count_matrix = new int[1][1];
                aux_c_below = new int[1][columns[i]];
                aux_c_above = new int[1][columns[i]];
                
                // Initialize structures
                for (int j=0; j<columns[i]; j++) {
                    aux_c_below[0][j] = 0;
                    aux_c_above[0][j] = 0;
                }
                
                // Compute all the instances in the register list
                for (int j=0; j<attributes.get(i).size(); j++) {
                    reg = (Register)attributes.get(i).get(j);
                    aux_c_above[0][reg.getOutputClass()]++;
                }
                
                // Add information to the histogram lists
                count_matrix.add(aux_count_matrix);
                c_below.add(aux_c_below);
                c_above.add(aux_c_above);
            }
            else {
                // Categorical attributes
                aux_count_matrix = new int[rows[i]][columns[i]];
                aux_c_below = new int[1][1];
                aux_c_above = new int[1][1];
                
                // Initialize structure
                for (int j=0; j<rows[i]; j++) {
                    for (int k=0; k<columns[i]; k++) {
                        aux_count_matrix[j][k] = 0;
                    }
                }
                
                // Compute all the instances in the register list
                for (int j=0; j<attributes.get(i).size(); j++) {
                    reg = (Register)attributes.get(i).get(j);
                    aux_count_matrix[(int)reg.getAttributeValue()][reg.getOutputClass()]++;
                }
                
                // Add information to the histogram lists
                count_matrix.add(aux_count_matrix);
                c_below.add(aux_c_below);
                c_above.add(aux_c_above);
            }
        }
    }
    
    /**
     * Check is a node is pure or a node isn't pure
     * 
     * @return true, if all the data that is in the node is from the same class; false, otherwise
     */
    public boolean isPure() {
        // If there aren't any instances in the node, then the node is pure
        if (attributes.isEmpty())
            return true;
        
        // Get the class of the first instance
        int oclass = attributes.get(0).get(0).getOutputClass();
        ArrayList <Register> aux;
        
        // Compare the class to all the other instances
        for (int i=0; i<attributes.size(); i++) {
            aux = attributes.get(i);
            for (int j=0; j<aux.size(); j++) {
                // If one of the instances has a different class, return false
                if (aux.get(j).getOutputClass() != oclass)
                    return false;
            }
        }
        
        return true;
    }
    
    /**
     * Evaluates all splits and returns the best split found. For each attribute, it evaluates all possible
     * splits and select the split with the minimum entropy.
     * 
     * @return the best split for this node after evaluating all splits
     */
    public Split evaluateAllSplits () {
        Split aux;
        int cursor, class_acc, total_acc, attribute_split;
        double entropy, entropyS1, entropyS2, pj, value_split;
        double attribute_best_entropy[];
        int attribute_position_entropy[];
        int matrix[][];
        Register reg;
        
        // Allocate memory for the best splits in each attribute
        attribute_best_entropy = new double [attributes.size()];
        attribute_position_entropy = new int [attributes.size()];
        
        // For each attribute
        for (int i=0; i<attributes.size(); i++) {
            System.out.println("Evaluating split points for: " + the_attributes.get(i).getName());
            
            if (the_attributes.get(i).isNominal()) {
                // Attribute is categorical 
                // Construct the histogram
                matrix = new int [rows[i]][columns[i]+1];
                
                for (int j=0; j<rows[i]; j++) {
                    for (int k=0; k<columns[i]+1; k++) {
                        matrix[j][k] = 0;
                    }
                }
                
                for (int j=0; j<attributes.get(i).size(); j++) {
                    reg = (Register)attributes.get(i).get(j);
                    matrix[(int)reg.getAttributeValue()][reg.getOutputClass()]++;
                    matrix[(int)reg.getAttributeValue()][columns[i]]++;
                }
                
                attribute_position_entropy[i] = -1;
                total_acc = 1;
                
                // Start at the first possible value
                for (cursor = 0; cursor < rows[i]; cursor++) {
                    entropyS1 = 0;
                    entropyS2 = 0;
                    
                    // Calculate the entropy for this possible split
                    for (int j=0; j<columns[i]; j++) {
                        if (matrix[cursor][columns[i]] != 0) {
                            pj = (double)matrix[cursor][j]/(double)matrix[cursor][columns[i]];
                        }
                        else {
                            pj = 0;
                        }
                        if (pj != 0)
                            entropyS1 += pj * (Math.log(pj)/Math.log(2.0));
                        
                        class_acc = 0;
                        total_acc = 0;
                        for (int k=0; k<rows[i]; k++) {
                            if (k != cursor) {
                                class_acc += matrix[k][j];
                                total_acc += matrix[k][columns[i]];
                            }
                        }
                        if (total_acc != 0)
                            pj = (double)class_acc/(double)total_acc;
                        else
                            pj = 0;
                        if (pj != 0)
                            entropyS2 += pj * Math.log(pj)/Math.log(2.0);
                    }
                    
                    entropyS1 = -entropyS1;
                    entropyS2 = -entropyS2;
                    
                    entropy = (double)matrix[cursor][columns[i]]/(double)attributes.get(i).size() * entropyS1 + total_acc/(double)attributes.get(i).size() * entropyS2;
                    
                    // Check if this split generates descendants with items
                    int elem_exits = 0;
                    for (int w=0; w<columns[i]; w++) {
                        elem_exits += matrix[cursor][w];
                    }
                    // Store as the best split if it the first possible value, the split generates correct descendants or it is lower
                    // than the current best split
                    if (((entropy < attribute_best_entropy[i]) || (attribute_position_entropy[i] == -1)) && (elem_exits != 0)) {
                        attribute_best_entropy[i] = entropy;
                        attribute_position_entropy[i] = cursor;
                    }
                }
                
                // The best split for this attribute is calculated
                count_matrix.set(i, matrix);

                System.out.println("Best split for " + the_attributes.get(i).getName() + " found: " + i + " " + the_attributes.get(i).getValues().get(attribute_position_entropy[i]) + " Entropy = " + attribute_best_entropy[i]);
            }
            else {
                // Attribute is numeric
                // Best, a priori is with cursor 0
                entropy = 0;
                attribute_best_entropy[i] = attributes.get(i).size();
                attribute_position_entropy[i] = 0;
                
                // Update histograms
                reg = (Register)attributes.get(i).get(0);
                matrix = c_below.get(i);
                matrix[0][reg.getOutputClass()]++;
                c_below.set(i, matrix);
                matrix = c_above.get(i);
                matrix[0][reg.getOutputClass()]--;
                c_above.set(i, matrix);
                
                // Compute the entropy in the middle values
                for (cursor = 1; cursor < attributes.get(i).size(); cursor++) {
                    // As the numeric values are ordered, we only compute possible splits when a new value is found
                    if (attributes.get(i).get(cursor).getAttributeValue() != attributes.get(i).get(cursor-1).getAttributeValue()) {
                        // Compute the entropy for the considered split
                        entropyS1 = 0;
                        entropyS2 = 0;
                            
                        for (int j=0; j<columns[i]; j++) {
                            pj = (double)c_below.get(i)[0][j]/(double)cursor;
                            if (pj != 0)
                                entropyS1 += pj * Math.log(pj)/Math.log(2.0); 
                            pj = (double)c_above.get(i)[0][j]/(double)(attributes.get(i).size() - cursor);
                            if (pj != 0)
                                entropyS2 += pj * Math.log(pj)/Math.log(2.0);
                        }
                            
                        entropyS1 = -entropyS1;
                        entropyS2 = -entropyS2;
                        
                        if (attributes.get(i).size() != 0) {
                            entropy = ((double)cursor/(double)attributes.get(i).size()) * entropyS1 + ((double)(attributes.get(i).size() - cursor)/(double)attributes.get(i).size()) * entropyS2;
                        }
                        else {
                            System.err.println("There aren't any registers in the attribute list");
                            System.exit(-1);
                        }
                        
                        // If the entropy is lower than the best entropy found, we store it
                        if (entropy < attribute_best_entropy[i]) {
                            attribute_best_entropy[i] = entropy;
                            attribute_position_entropy[i] = cursor;
                        }
                    }
                    
                    // We update the histograms
                    reg = (Register)attributes.get(i).get(cursor);
                    matrix = c_below.get(i);
                    matrix[0][reg.getOutputClass()]++;
                    c_below.set(i, matrix);
                    matrix = c_above.get(i);
                    matrix[0][reg.getOutputClass()]--;
                    c_above.set(i, matrix);
                }
                
                // The best split for this attribute is calculated
                System.out.println("Best split for " + the_attributes.get(i).getName() + " found: " + i + " " + attributes.get(i).get(attribute_position_entropy[i]).getAttributeValue() + " Entropy = " + attribute_best_entropy[i]);
            }
        }
        
        // Calculate the best split between the attributes
        value_split = attribute_best_entropy[0];
        attribute_split = 0;
        for (int i=1; i<attributes.size(); i++) {
            if (attribute_best_entropy[i] < value_split) {
                value_split = attribute_best_entropy[i];
                attribute_split = i;
            }
        }
        
        // Get the value of the split depending on whether the attribute is nominal or numerical
        if (the_attributes.get(attribute_split).isNominal())
            // The attribute is nominal, we get the value directly
            value_split = attribute_position_entropy[attribute_split];
        else {
            // The attribute is numerical, we have to search for the value
            boolean found = false;

            value_split = attributes.get(attribute_split).get(attribute_position_entropy[attribute_split]).getAttributeValue(); 
            
            for (int i=0; i<attributes.get(attribute_split).size() && !found; i++) {
                if (attributes.get(attribute_split).get(i).getAttributeValue() > value_split) {
                    value_split = attributes.get(attribute_split).get(i).getAttributeValue();
                    found = true;
                }
            }
        }
        
        aux = new Split(attribute_split, value_split);
        System.out.println("\nBEST SPLIT FOUND: " + aux);
        return aux;
    }
    
    /**
     * Splits a node into two nodes from a split following the identifier of a given number into an arraylist
     * of nodes.
     * 
     * @param best_split    Split used in this node to divide the node into two new nodes left and right
     * @param last_new_node Identifier of the last node created in the algorithm
     * @return an arraylist with two nodes, node left and node right obtained from the original node with the split
     */
    public ArrayList <Node> split (Split best_split, int last_new_node) {
        ArrayList <Node> result;
        ArrayList <Register> aux_left, aux_right;
        Node left, right;
        Register regaux;
        
        result = new ArrayList <Node> ();
        
        Hashtable <Integer, Integer> registers = new Hashtable <Integer, Integer> ();
        
        left = new Node ();
        right = new Node ();
        
        // First we have to copy the attributes into the new nodes
        left.the_attributes = new ArrayList<myAttribute>();
        right.the_attributes = new ArrayList<myAttribute>();
        for (int i=0; i<the_attributes.size(); i++) {
            left.the_attributes.add(new myAttribute(the_attributes.get(i)));
            right.the_attributes.add(new myAttribute(the_attributes.get(i)));
        }
        
        // Then we get new identifiers for the node
        left.identifier = last_new_node + 1;
        right.identifier = last_new_node + 2;
        
        // Then, we split the separate list for each attribute
        left.attributes = new ArrayList <ArrayList<Register>>(attributes.size());
        right.attributes = new ArrayList <ArrayList<Register>>(attributes.size());
        
        // Initialize attributes list to contain empty lists
        for (int i=0; i<attributes.size(); i++) {
            left.attributes.add(new ArrayList <Register> ());
            right.attributes.add(new ArrayList <Register> ());
        }
        
        aux_left = new ArrayList <Register> ();
        aux_right = new ArrayList <Register> ();
        
        // We split according to the best_split given
        if (the_attributes.get(best_split.getAttribute()).isNominal()) {
            // Attribute is categorical
            for (int j=0; j<attributes.get(best_split.getAttribute()).size(); j++) {
                regaux = attributes.get(best_split.getAttribute()).get(j);
                
                // Check whether the instance belongs to the left or the right descendant
                // Add this information to a hash
                if (regaux.getAttributeValue() == best_split.getValue()) {
                    aux_left.add(new Register (regaux));
                    registers.put(regaux.getIdentifier(), new Integer(0));
                }
                else {
                    aux_right.add(new Register (regaux));
                    registers.put(regaux.getIdentifier(), new Integer(1));
                }
            }
        }
        else {
            // Attribute is numerical
            for (int j=0; j<attributes.get(best_split.getAttribute()).size(); j++) {
                regaux = attributes.get(best_split.getAttribute()).get(j);
                
             // Check whether the instance belongs to the left or the right descendant
                // Add this information to a hash
                if (regaux.getAttributeValue() < best_split.getValue()) {
                    aux_left.add(new Register (regaux));
                    registers.put(regaux.getIdentifier(), new Integer(0));
                }
                else {
                    aux_right.add(new Register (regaux));
                    registers.put(regaux.getIdentifier(), new Integer(1));
                }
            }
        }
        
        // Check that the descendants have at least one instance on them
        if ((aux_left.size() == 0) || (aux_right.size() == 0)) {
            return null;
        }
        
        left.attributes.set(best_split.getAttribute(), aux_left);
        right.attributes.set(best_split.getAttribute(), aux_right);
        
        // For the attributes that aren't the best split
        for (int i=0; i<attributes.size(); i++) {
            if (i != best_split.getAttribute()) {
                aux_left = new ArrayList <Register> ();
                aux_right = new ArrayList <Register> ();
                
                // Add all their instances to the descendants, using for that the information stored in the
                // hash before
                for (int j=0; j<attributes.get(i).size(); j++) {
                    regaux = attributes.get(i).get(j);
                    Integer leaf = (Integer)registers.get(regaux.getIdentifier());
                    if (leaf != null) {
                        if (leaf.equals(0)){
                            aux_left.add(new Register(regaux));
                        }
                        else {
                            aux_right.add(new Register(regaux));
                        }
                    }
                    else {
                        System.out.println(regaux);
                        System.err.println("Register not found in hash table");
                        System.exit(-1);
                    }
                }
                left.attributes.set(i, aux_left);
                right.attributes.set(i, aux_right);
            }
        }
                
        // Calculate rows and columns arrays for both nodes
        // A priori there isn't any problem if any solution class dissapear or if there is a categorical value which disappear in the split
        left.rows = new int [attributes.size()];
        right.rows = new int [attributes.size()];
        System.arraycopy(rows, 0, left.rows, 0, rows.length);
        System.arraycopy(rows, 0, right.rows, 0, rows.length);
        left.columns = new int [attributes.size()];
        right.columns = new int [attributes.size()];
        System.arraycopy(columns, 0, left.columns, 0, columns.length);
        System.arraycopy(columns, 0, right.columns, 0, columns.length);
        
        // Then, calculate histograms for them
        left.calculateHistograms();
        right.calculateHistograms();

        // Copy the random generator to the descendants
        left.generator = generator;
        right.generator = generator;
        
        // Return the result
        result.add(left);
        result.add(right);
        
        return result;
    }

    /** 
     * Gets the identifier of the node
     *
     * @return the identifier of the node
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     * Replaces the identifier of the node with another new node
     * 
     * @param identifier  New identifier for the node 
     */
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }
    
    /** 
     * Gets the output class of the first register of the node. If the node is pure, the output class
     * is general for the node
     *
     * @return the output class of the node
     */
    public int getOutputClass () {
        return attributes.get(0).get(0).getOutputClass();
    }
    
    /**
     * Gets the number of different registers of data that there are in the node
     * 
     * @return the number of different registers in the node
     */
    public int getNumRegisters () {
        return attributes.get(0).size();
    }
    
    /**
     * Gets the number of items that belongs to class i
     * 
     * @param i Class to which the items should belong to be considered
     * @return the number of items that belongs to class i
     */
    public int getNumItemsClassI (int i) {
        myAttribute att;
        
        att = (myAttribute)the_attributes.get(0);
        
        if (att.isNominal()) {
            // The attribute is nominal, count with the count_matrix
            int numitems = 0;
            
            for (int j=0; j<rows[0]; j++) {
                numitems += count_matrix.get(0)[j][i];
            }
            
            return numitems;
        }
        else {
            // The attribute is numeral, count with the c_above and c_below
            return (c_above.get(0)[0][i] + c_below.get(0)[0][i]);
        }
    }
    
    /**
     * Gets the number of different values for the ith attribute
     * 
     * @param i Position of the attribute which differents values we are counting
     * @return the number of different values for the ith attribute
     */
    public int getDifferentValuesAttributeI (int i) {
        ArrayList <Double> values;
        
        values = new ArrayList <Double> ();
        
        // Create a list with all the different possible values
        for (int j=0; j<attributes.get(i).size(); j++) {
            if (!values.contains(new Double(attributes.get(i).get(j).getAttributeValue()))) {
                values.add(new Double(attributes.get(i).get(j).getAttributeValue()));                   
            }
        }
        
        return values.size();
    }
    
    /**
     * Gets the output class of the majority of the instances. If there are two majority classes, it
     * selects randomly one of them
     *
     * @return the majority class of the node
     */
    public int getMajorOutputClass () {
        int [] repetitions = new int [columns[0]];
        int max, posmax;
        
        for (int i=0; i<columns[0]; i++) {
            repetitions[i] = 0;
        }
        
        // Count the frecuence of each output class
        for (int j=0; j<attributes.get(0).size(); j++) {
            repetitions[attributes.get(0).get(j).getOutputClass()]++;
        }
        
        max = repetitions[0];
        posmax = 0;
        
        // Find the maximum output class
        for (int i=1; i<columns[0]; i++) {
            if (repetitions[i] > max) {
                max = repetitions[i];
                posmax = i;
            }
            else if (repetitions[i] == max) {
                // If the maximum is equal, then decide a maximum randomly
                System.out.println("Can't decide better outputClass between " + posmax + " y " + i);
                int selection = generator.nextInt(2);
                if (selection == 1) {
                    max = repetitions[i];
                    posmax = i;
                }
                System.out.println("Finally selected " + posmax);
            }
        }
        
        return posmax;
    }
    
    /**
     * Gets the number of different classes in the node
     * 
     * @return the number of different classes in the node
     */
    public int getNumClasses() {
        ArrayList <Integer> values;
        
        values = new ArrayList <Integer> ();
        
        // Create a list with all the different possible values for the output class
        for (int j=0; j<attributes.get(0).size(); j++) {
            if (!values.contains(new Integer(attributes.get(0).get(j).getOutputClass()))) {
                values.add(new Integer(attributes.get(0).get(j).getOutputClass()));                   
            }
        }
        
        return values.size();
    }
    
    /**
     * Obtains a list of lists with n1,...,nk in decreasing order. The first list includes the classes
     * 1,...,k ordered in decreasing order of appearance while the second list includes the absolute
     * frecuence of these classes, which is ordered decrementally.
     * 
     * @return a list of lists with n1,...,nk in decreasing order
     */
    public ArrayList<ArrayList<Integer>> getDecreasedNI() {
        ArrayList <ArrayList <Integer>> result;
        ArrayList <Integer> classes;
        ArrayList <Integer> frecuencies;
        myAttribute att;
        
        result = new ArrayList <ArrayList <Integer>> ();
        classes = new ArrayList <Integer> ();
        frecuencies = new ArrayList <Integer> ();
        int [] numitems = new int [columns[0]];
        
        att = (myAttribute)the_attributes.get(0);
        
        // Get the ni for all classes
        for (int i=0; i<columns[0]; i++) {
            if (att.isNominal()) {
                // The attribute is nominal, count with the count_matrix
                numitems[i] = 0;
                
                for (int j=0; j<rows[0]; j++) {
                    numitems[i] += count_matrix.get(0)[j][i];
                }
            }
            else {
                // The attribute is numeral, count with the c_above and c_ below
                numitems[i] = (c_above.get(0)[0][i] + c_below.get(0)[0][i]);
            }
            classes.add(i);
            frecuencies.add(numitems[i]);
        }
        
        // Sort in decreased order the ni
        for(int i=0; i<columns[0] -1; i++){
            int current = frecuencies.get(i);
            int current_position = classes.get(i);
            int k=i;
            
            for(int j=i+1; j<frecuencies.size();j++){
               if(current < frecuencies.get(j)){
                 k = j;
                 current = frecuencies.get(j);
                 current_position = classes.get(j);
               }
            }
            
            frecuencies.set(k,frecuencies.get(i));
            frecuencies.set(i,current);
            classes.set(k,classes.get(i));
            classes.set(i,current_position);
        }
        
        // Delete the elements with ni = 0
        int delete_point;
        delete_point = frecuencies.indexOf(0);
        
        while (delete_point != -1) {
            frecuencies.remove(delete_point);
            classes.remove(delete_point);
            delete_point = frecuencies.indexOf(0);
        }
        
        // Return the result
        result.add(classes);
        result.add(frecuencies);
        
        return result;
    }

    /**
     * Obtains a list of lists with the k classes in decreasing order of ni - V(Si). The first list includes the classes
     * 1,...,k ordered accordingly with the second list which includes the absolute
     * frecuence minus the V measure of these classes, which is ordered decrementally.
     * 
     * @return a list of lists with the k classes in decreasing order of ni - V(Si)
     */
    public ArrayList<ArrayList<Integer>> getDecreasedNIV() {
        ArrayList <ArrayList <Integer>> result;
        ArrayList <Integer> classes;
        ArrayList <Integer> frecuencies;
        myAttribute att;
        
        result = new ArrayList <ArrayList <Integer>> ();
        classes = new ArrayList <Integer> ();
        frecuencies = new ArrayList <Integer> ();
        int [] numitems = new int [columns[0]];
        double [] Vni = new double [columns[0]];
                
        att = (myAttribute)the_attributes.get(0);
        
        // Get the ni for all classes
        for (int i=0; i<columns[0]; i++) {
            if (att.isNominal()) {
                // The attribute is nominal, count with the count_matrix
                numitems[i] = 0;
                
                for (int j=0; j<rows[0]; j++) {
                    numitems[i] += count_matrix.get(0)[j][i];
                }
            }
            else {
                // The attribute is numeral, count with the c_above and c_below
                numitems[i] = (c_above.get(0)[0][i] + c_below.get(0)[0][i]);
            }
            classes.add(i);
            frecuencies.add(numitems[i]);
            Vni[i] = numitems[i] - V(i);
        }
        
        // Sort in decreased order the ni - V
        for(int i=0; i<columns[0] -1; i++){
            int current = frecuencies.get(i);
            int current_position = classes.get(i);
            double current_vi = Vni[i];
            int k=i;
            
            for(int j=i+1; j<Vni.length;j++){
               if(current_vi < Vni[j]){
                 k = j;
                 current = frecuencies.get(j);
                 current_position = classes.get(j);
                 current_vi = Vni[j];
               }
            }
            
            Vni[k] = Vni[i];
            Vni[i] = current_vi;
            frecuencies.set(k,frecuencies.get(i));
            frecuencies.set(i,current);
            classes.set(k,classes.get(i));
            classes.set(i,current_position);
        }
        
        // Delete the elements with ni = 0
        int delete_point;
        delete_point = frecuencies.indexOf(0);
        
        while (delete_point != -1) {
            frecuencies.remove(delete_point);
            classes.remove(delete_point);
            delete_point = frecuencies.indexOf(0);
        }
            
        result.add(classes);
        result.add(frecuencies);
        
        return result;
    }
    
    /**
     * Calculates V for the data in this node with k class, in other words, this calculates the minimum 
     * cost of specifying the split value at the parent of a node containing the data in this node
     * 
     * @param k class for calculating the V measure
     * @return the V measure for this node with k class
     */
    public double V (int k) {
        ArrayList <ArrayList <Double>> all_values;
        ArrayList <Double> values;
        double min, possible_min;
        
        all_values = new ArrayList <ArrayList <Double>> ();
        
        // Calculate vA for every attribute
        for (int i=0; i<attributes.size(); i++) {
            values = new ArrayList <Double> ();
            
            for (int j=0; j<attributes.get(i).size(); j++) {
                if (attributes.get(i).get(j).getOutputClass() == k) {
                    if (!values.contains(new Double(attributes.get(i).get(j).getAttributeValue()))) {
                        values.add(new Double(attributes.get(i).get(j).getAttributeValue()));                   
                    }
                }
            }
            
            all_values.add(values);
        }
        
        // Calculate the minimum value
        min = attributes.get(0).size() + 1;
        for (int i=0; i<attributes.size(); i++) {
            if (all_values.get(i).size() != 0) {
                if (the_attributes.get(i).isNominal()) {
                    possible_min = all_values.get(i).size();
                }
                else {
                    possible_min = Math.log((double)all_values.get(i).size())/Math.log(2.0);
                }
                if (possible_min < min) {
                    min = possible_min;
                }
            }
        }

        if (min == attributes.get(0).size() + 1) {
            return 0;
        }
        else {
            return min;
        }
    }
}

