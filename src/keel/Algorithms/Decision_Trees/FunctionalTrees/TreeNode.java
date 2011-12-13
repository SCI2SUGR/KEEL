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

package keel.Algorithms.Decision_Trees.FunctionalTrees;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Data structure that is used in the construction of the decision tree.
 * It stores the information about the relationship between nodes in a tree, and
 * the type of node that it is along some other necessary information such as the
 * attributes values, the attributes themselves or the output class for the item.
 * 
 * @author Written by Victoria Lopez Morales (University of Granada) 29/05/2009 
 * @version 1.0 
 * @since JDK1.5
 */
public class TreeNode {
    /**
     * Identifier of the tree node
     */
    int identifier;
    
    /**
     * Left descendant of this node, if it is not a leaf node
     */
    TreeNode left;
    /**
     * Right descendant of this node, if it is not a leaf node
     */
    TreeNode right;
    
    /**
     * Whether this node is a leaf node or not. A node that isn't a leaf node can be 
     * changed to a leaf node anytime, but a leaf node can't become a no leaf node 
     */
    boolean isLeaf;
    /**
     * The class asigned to this node if it is a leaf node, otherwise this class has
     * no meaning, but it is usually -1
     */
    int outputClass;
    
    /**
     * Relationship between this node and its descendants
     */
    Split condition;
    
    /**
     * Values of the attributes for each instance in the node
     */
    ArrayList <ArrayList <Double>> values;
    /**
     * Class of each instance in the node
     */
    ArrayList <Double> oclass;
    
    /**
     * All the information about the attributes that the node uses
     */
    ArrayList <myAttribute> the_attributes;
    /**
     * All the information about the output attribute of the node (class of the node)
     */
    myAttribute output_attribute;
    
    /**
     * Generator for pseudorandom numbers
     */
    Random generator;
    
    /**
     * Classifier that we are going to use in the pruned leaves, which are the leaf nodes
     */
    int prunedClassifier;
    /**
     * Parameter for some of the classifier on leaves, number of neares neighbours considered
     */
    int K;
    
    /**
     * Auxiliar structure for computing the Naive Bayes classifier
     */
    ArrayList <int [][]> count_matrix;
    /**
     * Auxiliar structure for computing the K-methods
     */
    double [][] normalized_values;
    /**
     * Auxiliar structure for computing KSNN
     */
    double further[];
    /**
     * Auxiliar structure for computing KSNN
     */
    boolean selected[];
    /**
     * Auxiliar structure for computing KNNAdaptive
     */
    double radius[]; 
    /**
     * Auxiliar structure for computing Nearest Means
     */
    double means[][];
    /**
     * Auxiliar structure for computing Nearest Means
     */
    private int[] meanClass;
    
    /** 
     * Creates a tree node with empty values that we can identify
     */ 
    TreeNode () {
        identifier = 0;
        
        left = null;
        right = null;
        
        isLeaf = false;
        outputClass = -1;
        
        condition = null;
        
        values = null;
        oclass = null;
        the_attributes = null;
        output_attribute = null;
        
        generator = new Random(12345678);
        
        prunedClassifier = -1;
        K = -1;
    }
    
    /** 
     * Creates a node with the identifier, the descendants and its condition as a leaf node. It also
     * includes the output class selected for it and the relationship between the nodes plus some
     * other information like a whole dataset, the kind of classifier used at leaves or the K parameter
     * for those classifiers at leaves
     *
     * @param id  Number identifying the node that is being created
     * @param newleft   Left descendant of the node that is being created
     * @param newright  Right descendant of the node that is being created
     * @param leaf  Whether the new node is a leaf node or not
     * @param oclass    Output class for the node that is being created
     * @param cond  Way to split this node into its descendants
     * @param data  Dataset that has the data that is going to be stored in the node
     * @param prunedLeavesClassifier    Kind of classifier used at the leaves
     * @param K_classifier  Number of nearest neighboors used in some of the classifiers at the leaves
     */
    TreeNode (int id, TreeNode newleft, TreeNode newright, boolean leaf, int oclass, Split cond, myDataset data, int prunedLeavesClassifier, int K_classifier) {
        ArrayList <Double> att_values;
        
        identifier = id;
        
        left = newleft;
        right = newright;
        
        isLeaf = leaf;
        outputClass = oclass;
        
        condition = cond;

        the_attributes = data.getAttributes();
        output_attribute = data.getOutputAttribute();
        
        // Initialize values from the original dataset
        values = new ArrayList <ArrayList <Double>>();
        for (int i=0; i<the_attributes.size(); i++) {
            att_values = new ArrayList <Double> ();
            for (int j=0; j<data.getNumIns(); j++) {
                att_values.add(data.getDataI(j, i));
            }
            values.add(att_values);
        }
        
        // Initialize output class associate with each instances from the original dataset
        this.oclass = new ArrayList <Double> ();
        for (int i=0; i<data.getNumIns(); i++) {
            this.oclass.add(new Double(data.getOutputI(i)));
        }
        
        generator = new Random(12345678);
        
        prunedClassifier = prunedLeavesClassifier;
        K = K_classifier;
    }
    
    /**
     * Creates a tree node from another existing tree node
     * 
     * @param tree  Original tree node from which we are going to create a copy
     */   
    TreeNode (TreeNode tree) {
        ArrayList <Double> att_values;
        
        this.identifier = tree.identifier;
        
        this.left = new TreeNode(tree.left);
        this.right = new TreeNode(tree.right);
        
        this.isLeaf = tree.isLeaf;
        this.outputClass = tree.outputClass;
        
        this.condition = new Split (tree.condition);
        
        // Copy values that are in the node
        this.values = new ArrayList <ArrayList <Double>>();
        for (int i=0; i<the_attributes.size(); i++) {
            att_values = new ArrayList <Double> ();
            for (int j=0; j<tree.values.get(i).size(); j++) {
                att_values.add(tree.values.get(i).get(j));
            }
            this.values.add(att_values);
        }
        
        // Copy output class associate with each instances
        this.oclass = new ArrayList <Double> ();
        for (int i=0; i<tree.oclass.size(); i++) {
            this.oclass.add(tree.oclass.get(i));
        }
        
        // Copy the attributes
        this.the_attributes = new ArrayList <myAttribute> ();
        for (int i=0; i<tree.the_attributes.size(); i++) {
            this.the_attributes.add(new myAttribute(tree.the_attributes.get(i)));
        } 
        
        this.output_attribute = new myAttribute(tree.output_attribute);
        
        this.generator = tree.generator;
        this.prunedClassifier = tree.prunedClassifier;
        this.K = tree.K;
    }
    
    /** 
     * Checks if a tree node is the same tree node as another object
     *
     * @param obj  Object that is checked to see if it is the same tree node
     * @return true if the tree nodes are the same, false otherwise
     * @see java.lang.Object#equals(java.lang.Object)
     */ 
    public boolean equals (Object obj) {
        boolean result;
        
        // First we check if the reference is the same
        if (this == obj)
            return true;
     
        // Then we check if the object exists and is from the class TreeNode
        if((obj == null) || (obj.getClass() != this.getClass()))
            return false;
        
        // object must be TreeNode at this point
        TreeNode test = (TreeNode)obj;
        result = ((identifier == test.identifier) && (K == test.K) && (prunedClassifier == test.prunedClassifier) && (isLeaf == test.isLeaf) && (outputClass == test.outputClass) && (left == test.left || (left != null && left.equals(test.left))) && (right == test.right || (right != null && right.equals(test.right))) && (condition == test.condition || (condition != null && condition.equals(test.condition))) && (output_attribute == test.output_attribute || (output_attribute != null && output_attribute.equals(test.output_attribute))));
     
        // We check the class attributes of the TreeNode class
        if (result) {
            if ((values.size() == test.values.size()) && (oclass.size() == test.oclass.size()) && (the_attributes.size() == test.the_attributes.size())) {
                // Check if values size is the same
                for (int i=0; i<values.size() && result; i++) {
                    if (values.get(i).size() != test.values.get(i).size())
                        result = false;
                }
                
                // Check the_attributes elements
                for (int i=0; i<the_attributes.size() && result; i++) {
                    if (!((the_attributes.get(i) == test.the_attributes.get(i)) || (the_attributes.get(i) != null && the_attributes.get(i).equals(test.the_attributes.get(i)))))
                        result = false;
                }
                
                // Check oclass elements
                for (int i=0; i<oclass.size() && result; i++) {
                    if (!((oclass.get(i) == test.oclass.get(i)) || (oclass.get(i) != null && oclass.get(i).equals(test.oclass.get(i)))))
                        result = false;
                }
                
                // Check values elements
                for (int i=0; i<values.size() && result; i++) {
                    for (int j=0; j<values.get(i).size() && result; j++) {
                        if (!((values.get(i).get(j) == test.values.get(i).get(j)) || (values.get(i).get(j) != null && values.get(i).get(j).equals(test.values.get(i).get(j)))))
                            result = false;
                    }
                }
                
                return result;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
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
        hash = 31 * hash + (null == left ? 0 : left.hashCode());
        hash = 31 * hash + (null == right ? 0 : right.hashCode());
        hash = 31 * hash + (isLeaf ? 1 : 0);
        hash = 31 * hash + outputClass;
        hash = 31 * hash + prunedClassifier;
        hash = 31 * hash + K;
        hash = 31 * hash + (null == condition ? 0 : condition.hashCode());
        hash = 31 * hash + (null == oclass ? 0 : oclass.hashCode());
        return hash;
    }
    
    /** 
     * Overriden function that converts the class to a string
     *
     * @return the string representation of the class
     * @see java.lang.Object#toString()
     */ 
    public String toString() { 
        String aux = "";

        aux = aux + "Tree Node " + identifier + "\n";
        
        if (isLeaf) {
            if (prunedClassifier == -1) {
                // This leaf wasn't pruned
                aux = aux + "Leaf Node, class " + outputClass + "\n";
            }
            else {
                int [] distribution = getOutputClassDistribution();
                switch (prunedClassifier) { // Print the classifier at leaves used
                    case 0:
                        aux = aux + "Leaf Node, Naive Bayes: "; 
                        break;
                    case 1:
                        aux = aux + "Leaf Node, " + K + "NN: ";
                        break;
                    case 2:
                        aux = aux + "Leaf Node, NM: "; 
                        break;
                    case 3:
                        aux = aux + "Leaf Node, " + K + "SNN: "; 
                        break;
                    case 4:
                        aux = aux + "Leaf Node, " + K + "NNAdaptive: "; 
                        break;
                    default:
                        System.err.println("The classifier at leaves is not valid");
                        System.exit(-1);
                        break;
                }
                ArrayList <Double> classes = getClasses();
                for (int i=0; i<distribution.length; i++) {
                    double classvalue = classes.get(i);
                    aux = aux + output_attribute.getValues().get((int)classvalue) + " " + (double)distribution[i]/(double)oclass.size() + "% ";
                }
                aux += "\n";
            }
        }
        else {
            aux += "Internal Node\n";
            aux = aux + "Split: " + condition + "\n";
        }
        
        for (int i=0; i<the_attributes.size(); i++) {
            aux = aux + the_attributes.get(i).getName() + "\n";
        }
        aux = aux + output_attribute.getName() + "\n";
        
        for (int i=0; i<values.get(0).size(); i++) {
            for (int j=0; j<values.size(); j++) {
                aux = aux + values.get(j).get(i) + " ";
            }
            aux = aux + "Output class: " + oclass.get(i) + "\n";
        }
                
        return aux;
    }
    
    /**
     * Prints the tree in a String with all the information that makes it human readable
     *
     * @return a String with the tree in it
     */
    public String printTree () {
        try {
            StringBuffer text = new StringBuffer();

            if (!isLeaf) {
                printTree(0, text);
            }

            return text.toString();
        } catch (Exception e) {
            return "Can not print the tree";
        }
    }

    /** 
     * Prints the tree in a StringBuffer with a depth given according to the relationship
     * of the nodes in the whole tree with all the information that makes it human readable
     *
     * @param depth Position in the tree of the node that is reflected in the string in a
     * major number of space in it
     * @param text Output where the tree is exposed
     * @throws Exception    If the tree cannot be printed.
     */ 
    private void printTree (int depth, StringBuffer text) throws Exception {
        String aux = "";
        String aux2 = "";

        for (int k = 0; k < depth; k++) {
          aux += "\t";
        }

        for (int k = 1; k < depth; k++) {
            aux2 += "\t";
        }

        text.append(aux);
        if (isLeaf) {
            if ((isPure()) || (outputClass != -1)) {
                // This node wasn't pruned or is pure
                if (output_attribute.isNominal()) {
                    text.append(output_attribute.getValues().get(outputClass) + " \n");
                }
                else {
                    text.append(outputClass + " \n");
                }
            }
            else {
                // This node was pruned
                int [] distribution = getOutputClassDistribution();
                switch (prunedClassifier) { // Print the classifier at leaves used
                    case 0:
                        text.append("Naive Bayes: "); 
                        break;
                    case 1:
                        text.append(K + "NN: ");
                        break;
                    case 2:
                        text.append("NM: "); 
                        break;
                    case 3:
                        text.append(K + "SNN: "); 
                        break;
                    case 4:
                        text.append(K + "NNAdaptive: "); 
                        break;
                    default:
                        System.err.println("The classifier at leaves is not valid");
                        System.exit(-1);
                        break;
                }
                ArrayList <Double> classes = getClasses();
                if (output_attribute.isNominal()) {
                    for (int i=0; i<distribution.length; i++) {
                        double classvalue = classes.get(i);
                        text.append(output_attribute.getValues().get((int)classvalue) + " " + (double)distribution[i]/(double)oclass.size() + "% ");
                    }
                }
                else {
                    for (int i=0; i<distribution.length; i++) {
                        double classvalue = classes.get(i);
                        text.append(classvalue + " " + (double)distribution[i]/(double)oclass.size() + "% ");
                    }
                }
                text.append("\n");
            }
        }
        else {
            if (the_attributes.get(condition.getAttribute()).isNominal()) {
                text.append("if (" + the_attributes.get(condition.getAttribute()).getName() + " in " + the_attributes.get(condition.getAttribute()).getValues().get((int)condition.getValue()) + ") then {\n");
            }
            else {
                text.append("if (" + the_attributes.get(condition.getAttribute()).getName() + " < " + condition.getValue() + ") then {\n");
            }
            left.printTree(depth + 1, text);
            text.append(aux + "else { \n");
            right.printTree(depth + 1, text);
        }
        text.append(aux2 + "}\n");
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
     * Gets the left descendant of the node, if it is not a leaf node
     *
     * @return the left descendant of the node
     */
    public TreeNode getLeft() {
        return left;
    }

    /**
     * Replaces the left descendant of the node with another new left descendant
     * 
     * @param left  New node that is going to be kept as left descendant of this node 
     */
    public void setLeft(TreeNode left) {
        this.left = left;
    }

    /** 
     * Gets the right descendant of the node, if it is not a leaf node
     *
     * @return the left descendant of the node
     */
    public TreeNode getRight() {
        return right;
    }

    /**
     * Replaces the right descendant of the node with another new right descendant
     * 
     * @param right  New node that is going to be kept as right descendant of this node 
     */
    public void setRight(TreeNode right) {
        this.right = right;
    }

    /** 
     * Answers if the node is a leaf node or not
     *
     * @return true if the node is a leaf node, false otherwise
     */
    public boolean isLeaf() {
        return isLeaf;
    }

    /**
     * Changes the logical attribute stating if a node is leaf or not
     * 
     * @param isLeaf    Logical value stating if a node is leaf or not
     */
    public void setLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    /** 
     * Gets the output class stored for the node. It should be considered only when the node is a leaf node
     *
     * @return the output class of the node
     */
    public int getOutputClass() {
        return outputClass;
    }

    /**
     * Replaces the output class of the node with another new output class
     * 
     * @param outputClass   New output class for the node 
     */
    public void setOutputClass(int outputClass) {
        this.outputClass = outputClass;
    }

    /**
     * Gets the relationship between this node and its descendats, that's the way to split this node in two
     * 
     * @return the split for this node
     */
    public Split getCondition() {
        return condition;
    }

    /**
     * Replaces the relationship between this node and its descendants, this means, changing the condition how
     * the two descendants are created. When using this function is highly recommended using the setLeft() and
     * setRight() functions too
     * 
     * @param condition New relationship between this node and its descendants
     */
    public void setCondition(Split condition) {
        this.condition = condition;
    }
    
    /**
     * Searches a descendant node from this node with a specific id
     * 
     * @param id    Identifier of the node we are searching for
     * @return the TreeNode with the specified id
     */
    public TreeNode getNode (int id) {
        TreeNode aux = null;
        
        if (this == null) {
            return null;
        }
        
        if (identifier == id) {
            return this;
        }
        
        if (left != null)
            aux = left.getNode(id);
        
        if ((aux == null) && (right != null)) {
            aux = right.getNode(id);
        }
        
        return aux;
    }
    
    /**
     * Gets the number of internal nodes from this node and its descendants. If this is a leaf node the number
     * of internal nodes is 0.
     * 
     * @return the number of internal nodes from this node and its descendants
     */
    public int getNumNodes () {
        int nodes = 0;
        
        if (isLeaf) {
            nodes = 0;
        }
        else {
            nodes++;
            if (left != null)
                nodes += left.getNumNodes();
            if (right != null)
                nodes += right.getNumNodes();
        }
        
        return nodes;
    }
    
    /**
     * Gets the number of leaf nodes from this node and its descendants
     * 
     * @return the number of leaf nodes from this node and its descendants
     */
    public int getLeafs () {
        int leafs = 0;
        
        if (isLeaf) {
            leafs = 1;
        }
        else {
            if (left != null)
                leafs += left.getLeafs();
            if (right != null)
                leafs += right.getLeafs();
        }
        
        return leafs;
    }
    
    /**
     * Classifies a given item with the information stored in the node and its descendants, making a call
     * to the specific classifiers at the leaves
     * 
     * @param ind  Data attribute values for the item we are classifying
     * @return the class asigned to the item given
     */
    public int evaluate (double [] ind) {
        // If we are at a leaf node we can obtain the class directly, otherwise we have to descend
        // to a leaf node
        if (isLeaf) {
            // If it is a pure node or it is a pruned node with a specified class, we've got the result
            if (isPure() || (outputClass != -1)) {
                return outputClass;
            }
            else {
                // Here, we call the specified classifier
                switch (prunedClassifier) {
                    case 0:
                        return evaluateNaiveBayes (ind);
                    case 1:
                        return evaluateKNN (ind);
                    case 2:
                        return evaluateNM (ind);
                    case 3:
                        return evaluateKSNN (ind);
                    case 4:
                        return evaluateKNNAdaptive (ind);
                    default:
                        System.err.println("The classifier used at the leaves isn't valid");
                        System.exit(-1);
                        break;
                }
                return -1;
            }
        }
        else {
            if (condition != null) {
                // Descend to the corresponding leaf node
                if (the_attributes.get(condition.getAttribute()).isNominal()) {
                    if (ind[condition.getAttribute()] == condition.getValue()) {
                        return left.evaluate(ind);
                    }
                    else {
                        return right.evaluate(ind);
                    }   
                }
                else {
                    if (ind[condition.getAttribute()] < condition.getValue()) {
                        return left.evaluate(ind);
                    }
                    else {
                        return right.evaluate(ind);
                    }   
                }
            }
            else {
                System.err.println("Tree not fully built");
                System.exit(-1);
                return -1;
            }
        }
    }
    
    /**
     * Removes all the descendants of this node
     */
    private void deleteDescendants () {
        // Delete descendants of descendants
        if (left != null) {
            left.deleteDescendants();
        }
        
        if (right != null) {
            right.deleteDescendants();
        }
        
        left = null;
        right = null;
        condition = null;
    }

    /**
     * Decides if a node is partitionable or not. A node is partitionable when the node is not pure or when
     * the parent node at least has num_min_instances instances in it
     * 
     * @param num_min_instances The minimum number of data instances that the tree node must contain to
     * be considered a partitionable node
     * @return true, if it is a partitionable node, false otherwise
     */
    public boolean isPartitionable (int num_min_instances) {
        // First check that at least has X instanes in it
        if (values.get(0).size() < num_min_instances) {
            return false;
        }
        
        // Then check if the node is pure
        double unique_class = oclass.get(0);
        
        // Check that the node is not pure
        for (int i=0; i<oclass.size(); i++) {
            if (oclass.get(i) != unique_class)
                return true;
        }
        
        return false;
    }
    
    /**
     * Sets this node as a leaf node. To set a node as leaf, we have to delete its descendants, mark the
     * node as leaf, and assign a class for classification
     */
    public void setAsLeaf () {
        isLeaf = true;
        
        deleteDescendants(); // The node doesn't have any descendants
        
        assignOutputClass(); // The outputclass of the node is decided from the data stored in the node
    }
    
    /**
     * Sets this node as a classifier leaf node. To set a node as classifier leaf, we have to delete its
     * descendants, mark the node as leaf, and assign an invalid class for classification
     */
    public void setAsClassifierLeaf () {
        isLeaf = true;
        
        deleteDescendants(); // The node doesn't have any descendants
        
        if (!isPure()) {
            outputClass = -1; // If the node isn't pure, the other classifier is used
        }
        else {
            assignOutputClass();
        }
        
        condition = null;
        
        // When we make the leaf a classifier, we pre-process the data in it for the later classification
        switch (prunedClassifier) {
            case 0:
                // Naive Bayes needs categorical data and the count matrix
                for (int i=0; i<the_attributes.size(); i++) {
                    if (!the_attributes.get(i).isNominal()) {
                        System.err.println("The attribute " + the_attributes.get(i).getName() + " is not a nominal attribute");
                        System.exit(-1);
                    }
                }
                calculateCountMatrix();
                break;
            case 1:
                // KNN needs normalized values
                normalizeValues();
                break;
            case 2:
                // NM needs normalized values and the centroids of the classes
                normalizeValues();
                calculateMeans();
                break;
            case 3:
                // KSNN needs normalized values and the further neighbour matrix
                normalizeValues();
                getFurtherNeighbor();
                break;
            case 4:
                // KNNAdaptive needs normalized values and the radius matrix
                normalizeValues();
                calculateRadius();
                break;
            default:
                System.err.println("The classifier used at the leaves isn't valid");
                System.exit(-1);
                break;
        }
    }
    
    /**
     * Initializes the output class stored in the node with a valid output class, in a standard way, 
     * with the class stored in the node if it is pure or with the class of the majority of the instances
     * where if there are two majority classes, it selects randomly one of them
     */
    public void assignOutputClass() {
        // If the node is pure, the output class is the class of one of its instances; if it is not,
        // the output class is the major class in the node
        if (isPure()) {
            double aux = oclass.get(0);
            outputClass = (int) aux;
        }
        else {
            outputClass = getMajorOutputClass();
        }
    }
    
    /**
     * Check is a node is pure or a node isn't pure
     * 
     * @return true, if all the data that is in the node is from the same class; false, otherwise
     */
    private boolean isPure() {
        // If there aren't any instances in the node, then the node is pure
        if (oclass.isEmpty())
            return true;
        
        // Get the class of the first instance
        double unique_value = oclass.get(0);
        
        // Compare the class to all the other instances
        for (int i=0; i<oclass.size(); i++) {
            // If one of the instances has a different class, return false
            if (oclass.get(i) != unique_value) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Gets the output class of the majority of the instances. If there are two majority classes, it
     * selects randomly one of them
     *
     * @return the majority class of the node
     */
    private int getMajorOutputClass () {
        int num_classes = getNumClasses();
        int [] repetitions = new int [num_classes];
        int max, posmax;
        ArrayList <Double> which_classes = getClasses();
        
        for (int i=0; i<num_classes; i++) {
            repetitions[i] = 0;
        }
     
        // Count the frecuence of each output class
        for (int j=0; j<oclass.size(); j++) {
            int position = which_classes.indexOf (oclass.get(j));
            repetitions[position]++;
        }
        
        max = repetitions[0];
        posmax = 0;
     
        // Find the maximum output class
        for (int i=1; i<num_classes; i++) {
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
        ArrayList <Integer> diff_values;
        
        diff_values = new ArrayList <Integer> ();
        
        // Create a list with all the different possible values for the output class
        for (int j=0; j<oclass.size(); j++) {
            double aux = oclass.get(j);
            if (!diff_values.contains(new Integer((int)aux))) {
                diff_values.add(new Integer((int)aux));                   
            }
        }
        
        return diff_values.size();
    }
    
    /**
     * Gets an array list with the different classes in the node
     * 
     * @return an array list with the different classes in the node
     */
    public ArrayList <Double> getClasses () {
        ArrayList <Double> diff_values;
        
        diff_values = new ArrayList <Double> ();
        
        // Create a list with all the different possible values for the output class
        for (int j=0; j<oclass.size(); j++) {
            double aux = oclass.get(j);
            if (!diff_values.contains(new Double(aux))) {
                diff_values.add(new Double(aux));                   
            }
        }
        
        // Return all the values of the list
        return diff_values;
    }
    
    /**
     * Evaluates all splits and returns the best split found. For each attribute, it evaluates all 
     * possible splits and select the split with the best split criteria
     * 
     * @param splitCriteria Information measure used to compute the best split. 0 is the value for
     * Entropy, 1 is for InformationGain, 2 is for GiniIndex and 3 is for GainRatio
     * @return the best split for this node after evaluating all splits
     */
    public Split evaluateAllSplits (int splitCriteria) {
        Split best_split = null;
        
        double [] best_split_criteria;
        double split_criteria_value;
        
        ArrayList <Split> best_split_attribute;
        Split a_split;
        
        double best_split_value;
        
        best_split_criteria = new double [values.size()];
        best_split_attribute = new ArrayList <Split> (values.size());
        
        // For each split
        // 0 is Entropy, the best split is the minimum Entropy
        // 1 is InformationGain, the best split is the maximum InformationGain
        // 2 is GiniIndex, the best split is the minimum GiniIndex
        // 3 is GainRatio, the best split is the maximum GainRatio
        for (int i=0; i<values.size(); i++) {
            System.out.println("Evaluating split points for: " + the_attributes.get(i).getName());
            
            if (the_attributes.get(i).isNominal()) {
                
                // The attribute is nominal
                a_split = new Split (-1, 0.0);
                best_split_attribute.add(a_split);
                if ((splitCriteria == 0) || (splitCriteria == 2)) {
                    best_split_criteria[i] = Double.MAX_VALUE;
                }
                else {
                    best_split_criteria[i] = -Double.MAX_VALUE;
                }
                
                for (int j=0; j<the_attributes.get(i).getValues().size(); j++) {
                    a_split = new Split (i, 1.0*j);
                    
                    if (valid_split(a_split)) {
                        split_criteria_value = evaluateSplit (a_split, splitCriteria);
                        
                        if (split_criteria_value < best_split_criteria[i]) {
                            // The new split has a minor value of the criteria, we change the split if the criteria is Entropy or GiniIndex
                            if ((splitCriteria == 0) || (splitCriteria == 2)) {
                                best_split_attribute.set(i, a_split);
                                best_split_criteria[i] = split_criteria_value;
                            }
                        }
                        else if (split_criteria_value > best_split_criteria[i]) {
                            // The new split has a major value of the criteria, we change the split if the criteria is InformationGain or GainRatio
                            if ((splitCriteria == 1) || (splitCriteria == 3)) {
                                best_split_attribute.set(i, a_split);
                                best_split_criteria[i] = split_criteria_value;
                            }
                        }
                    }
                }
                System.out.println("Best split for " + the_attributes.get(i).getName() + " found: " + i + " " + the_attributes.get(i).getValues().get((int)best_split_attribute.get(i).getValue()) + " Split criteria = " + best_split_criteria[i]);
            }
            else {
                // The attribute is numerical
                a_split = new Split (-1, 0);
                best_split_attribute.add(a_split);
                if ((splitCriteria == 0) || (splitCriteria == 2)) {
                    best_split_criteria[i] = Double.MAX_VALUE;
                }
                else {
                    best_split_criteria[i] = -Double.MAX_VALUE;
                }
                
                for (int j=0; j<values.get(i).size(); j++) {
                    a_split = new Split (i, values.get(i).get(j));
                    if (valid_split(a_split)) {
                        split_criteria_value = evaluateSplit (a_split, splitCriteria);
                        
                        if (split_criteria_value < best_split_criteria[i]) {
                            // The new split has a minor value of the criteria, we change the split if the criteria is Entropy or GiniIndex
                            if ((splitCriteria == 0) || (splitCriteria == 2)) {
                                best_split_attribute.set(i, a_split);
                                best_split_criteria[i] = split_criteria_value;
                            }
                        }
                        else if (split_criteria_value > best_split_criteria[i]) {
                            // The new split has a major value of the criteria, we change the split if the criteria is InformationGain or GainRatio
                            if ((splitCriteria == 1) || (splitCriteria == 3)) {
                                best_split_attribute.set(i, a_split);
                                best_split_criteria[i] = split_criteria_value;
                            }
                        }
                    }
                }
                System.out.println("Best split for " + the_attributes.get(i).getName() + " found: " + i + " " + best_split_attribute.get(i).getValue() + " Split criteria = " + best_split_criteria[i]);
            }
        }
        
        best_split = best_split_attribute.get(0);
        best_split_value = best_split_criteria[0];
        
        for (int i=0; i<values.size(); i++) {
            if ((splitCriteria == 1) || (splitCriteria == 3)) {
                if (best_split_criteria[i] > best_split_value) {
                    best_split = best_split_attribute.get(i);
                    best_split_value = best_split_criteria[i];
                }
            }
            else { //splitCriteria = 0,2
                if (best_split_criteria[i] < best_split_value) {
                    best_split = best_split_attribute.get(i);
                    best_split_value = best_split_criteria[i];
                }
            }
        }

        System.out.println("\nBEST SPLIT FOUND: " + best_split);

        return best_split;
    }
    
    /**
     * Evaluates a split and returns the value of the information measure used
     * 
     * @param sp    Split that is going to be evaluated
     * @param splitCriteria Information measure used to compute the split. 0 is the value for
     * Entropy, 1 is for InformationGain, 2 is for GiniIndex and 3 is for GainRatio
     * @return value of the information measure for the split 
     */
    private double evaluateSplit (Split sp, int splitCriteria) {
        double result, result1, result2, pj, pj1, pj2;
        ArrayList <ArrayList <Integer>> split_oclass_distribution;
        int [] oclass_distribution;
        int num_classes = getNumClasses();
        int total;
        
        split_oclass_distribution = getOutputClassDistribution (sp);
        oclass_distribution = getOutputClassDistribution ();
        
        result1 = 0;
        result2 = 0;
        result = 0;
        total = split_oclass_distribution.get(0).get(num_classes) + split_oclass_distribution.get(1).get(num_classes);
        
        if (splitCriteria == 0) { // The splitCriteria is entropy
            for (int i=0; i<num_classes; i++) {
                if (split_oclass_distribution.get(0).get(num_classes) != 0) {
                    pj1 = (double)split_oclass_distribution.get(0).get(i)/(double)split_oclass_distribution.get(0).get(num_classes);
                    if (pj1 != 0)
                        result1 = result1 + pj1 * Math.log(pj1)/Math.log(2.0);
                }
                if (split_oclass_distribution.get(1).get(num_classes) != 0) {
                    pj2 = (double)split_oclass_distribution.get(1).get(i)/(double)split_oclass_distribution.get(1).get(num_classes);
                    if (pj2 != 0)
                        result2 = result2 + pj2 * Math.log(pj2)/Math.log(2.0);
                }
            }
            
            result1 = -result1;
            result2 = -result2;
            
            if (total != 0) {
                result = (double)split_oclass_distribution.get(0).get(num_classes)/(double)total * result1 + (double)split_oclass_distribution.get(1).get(num_classes)/(double)total * result2;
            }
        }
        else if (splitCriteria == 1) { // The splitCriteria is InformationGain
            // First, we calculate the entropy of the whole subset
            for (int i=0; i<num_classes; i++) {
                if (oclass.size() != 0) {
                    pj = (double)oclass_distribution[i]/(double)oclass.size();
                    if (pj != 0) 
                        result = result + pj * Math.log(pj)/Math.log(2.0);
                }
            }
            result = -result;
            
            // Then, we calculate the entropy for the subsets
            for (int i=0; i<num_classes; i++) {
                if (split_oclass_distribution.get(0).get(num_classes) != 0) {
                    pj1 = (double)split_oclass_distribution.get(0).get(i)/(double)split_oclass_distribution.get(0).get(num_classes);
                    if (pj1 != 0)
                        result1 = result1 + pj1 * Math.log(pj1)/Math.log(2.0);
                }
                if (split_oclass_distribution.get(1).get(num_classes) != 0) {
                    pj2 = (double)split_oclass_distribution.get(1).get(i)/(double)split_oclass_distribution.get(1).get(num_classes);
                    if (pj2 != 0)
                        result2 = result2 + pj2 * Math.log(pj2)/Math.log(2.0);
                }
            }
            
            result1 = -result1;
            result2 = -result2;
            
            // We calculate the informationGain
            result = result - (double)split_oclass_distribution.get(0).get(num_classes)/(double)oclass.size() * result1 - (double)split_oclass_distribution.get(1).get(num_classes)/(double)oclass.size() * result2;
        }
        else if (splitCriteria == 2) { // The splitCriteria is GiniIndex
            // We calculate the gini index for the subsets
            for (int i=0; i<num_classes; i++) {
                if (split_oclass_distribution.get(0).get(num_classes) != 0) {
                    pj1 = (double)split_oclass_distribution.get(0).get(i)/(double)split_oclass_distribution.get(0).get(num_classes);
                    if (pj1 != 0)
                        result1 = result1 + Math.pow (pj1,2.0);
                }
                if (split_oclass_distribution.get(1).get(num_classes) != 0) {
                    pj2 = (double)split_oclass_distribution.get(1).get(i)/(double)split_oclass_distribution.get(1).get(num_classes);
                    if (pj2 != 0)
                        result2 = result2 + Math.pow (pj2,2.0);
                }
            }

            result1 = 1 - result1;
            result2 = 1 - result2;
            
            result = (double)split_oclass_distribution.get(0).get(num_classes)/(double)oclass.size() * result1 + (double)split_oclass_distribution.get(1).get(num_classes)/(double)oclass.size() * result2;
        }
        else if (splitCriteria == 3) { // The splitCriteria is GainRatio
            double informationGain;
            
            // First, we calculate the entropy of the whole subset
            for (int i=0; i<num_classes; i++) {
                if (oclass.size() != 0) {
                    pj = (double)oclass_distribution[i]/(double)oclass.size();
                    if (pj != 0) 
                        result = result + pj * Math.log(pj)/Math.log(2.0);
                }
            }
            result = -result;
            
            // Then, we calculate the entropy for the subsets
            for (int i=0; i<num_classes; i++) {
                if (split_oclass_distribution.get(0).get(num_classes) != 0) {
                    pj1 = (double)split_oclass_distribution.get(0).get(i)/(double)split_oclass_distribution.get(0).get(num_classes);
                    if (pj1 != 0)
                        result1 = result1 + pj1 * Math.log(pj1)/Math.log(2.0);
                }
                if (split_oclass_distribution.get(1).get(num_classes) != 0) {
                    pj2 = (double)split_oclass_distribution.get(1).get(i)/(double)split_oclass_distribution.get(1).get(num_classes);
                    if (pj2 != 0)
                        result2 = result2 + pj2 * Math.log(pj2)/Math.log(2.0);
                }
            }
            
            result1 = -result1;
            result2 = -result2;
            
            // We calculate the informationGain
            informationGain = result - (double)split_oclass_distribution.get(0).get(num_classes)/(double)oclass.size() * result1 - (double)split_oclass_distribution.get(1).get(num_classes)/(double)oclass.size() * result2;
        
            result = informationGain/result;
        }
        
        return result;
    }
    
    /**
     * Gets an array with the frecuencies of the classes in the node
     * 
     * @return an array with the frecuencies of the classes in the node
     */
    private int [] getOutputClassDistribution () {
        int [] output;
        int num_classes = getNumClasses();
        ArrayList <Double> which_classes = getClasses();
        
        output = new int [num_classes];
        
        // Before starting, the number of elements in each class is 0
        for (int i=0; i<num_classes; i++) {
            output[i] = 0;
        }
        
        // We count every instance in the node for the class distribution
        for (int i=0; i<oclass.size(); i++) {
            int position = which_classes.indexOf (oclass.get(i));
            output[position]++;
        }
        
        return output;
    }
    
    /**
     * Gets a list of lists with the frecuencies of the classes in the descendants node given a split
     * 
     * @param sp    Split that is going to be used to compute the frecuencies of the classes for the
     * supposed descendants
     * @return a list of lists with the frecuencies of classes depending on a split
     */
    private ArrayList <ArrayList <Integer>> getOutputClassDistribution (Split sp) {
        int [] output1;
        int [] output2;
        int num_classes = getNumClasses();
        ArrayList <Double> which_classes = getClasses();
        int total1, total2;
        ArrayList <Integer> o1;
        ArrayList <Integer> o2;
        ArrayList <ArrayList <Integer>> o12;
        
        output1 = new int [num_classes];
        output2 = new int [num_classes];
        
        // Before starting, the number of elements in each class is 0
        for (int i=0; i<num_classes; i++) {
            output1[i] = 0;
            output2[i] = 0;
        }
        
        if (the_attributes.get(sp.getAttribute()).isNominal()) {
            // The attribute for the split is nominal
            // We count every instance in the node for the class distribution
            for (int i=0; i<oclass.size(); i++) {
                int position = which_classes.indexOf (oclass.get(i));
                
                if (sp.getValue() == values.get(sp.getAttribute()).get(i)) {
                    output1[position]++;
                }
                else {
                    output2[position]++;
                }
            }
        }
        else {
            // The attribute for the split is numerical
            // We count every instance in the node for the class distribution
            for (int i=0; i<oclass.size(); i++) {
                int position = which_classes.indexOf (oclass.get(i));
                
                if (values.get(sp.getAttribute()).get(i) < sp.getValue()) {
                    output1[position]++;
                }
                else {
                    output2[position]++;
                }
            }
        }
        
        // Add the results to the arraylist output
        o1 = new ArrayList <Integer> (num_classes);
        o2 = new ArrayList <Integer> (num_classes);
        
        total1 = 0;
        total2 = 0;
        
        for (int i=0; i<num_classes; i++) {
            total1 += output1[i];
            total2 += output2[i];
            
            o1.add(output1[i]);
            o2.add(output2[i]);
        }
        
        o1.add(total1);
        o2.add(total2);
       
        o12 = new ArrayList <ArrayList <Integer>> (2);
        o12.add(o1);
        o12.add(o2);
        
        return o12;
    }
    
    /**
     * Checks if a split is valid for a TreeNode, this means, that it will generate valid child nodes
     * 
     * @param sp    Split that is going to be checked if it generates valid child nodes
     * @return true, if the split generates valid child nodes, false otherwise
     */
    public boolean valid_split (Split sp) {
        boolean found = false;
        
        if (the_attributes.get(sp.getAttribute()).isNominal()) {
            // The split is meant for a nominal attribute
            // A split for a nominal attribute is valid when there are two different values for the attribute and one of them is the split_value
            for (int i=0; i<values.get(sp.getAttribute()).size() && !found; i++) {
                if (values.get(sp.getAttribute()).get(i) == sp.getValue())
                    found = true;
            }
            if (found) {
                found = false;
                for (int i=0; i<values.get(sp.getAttribute()).size() && !found; i++) {
                    if (values.get(sp.getAttribute()).get(i) != sp.getValue())
                        found = true;
                }
                return found;
            }
            else {
                return false;
            }
        }
        else {
            // The split is meant for a numerical attribute
            // A split for a numerical attribute is valid when there is a value below it and a value equal or above it
            for (int i=0; i<values.get(sp.getAttribute()).size() && !found; i++) {
                if (values.get(sp.getAttribute()).get(i) < sp.getValue())
                    found = true;
            }
            if (found) {
                found = false;
                for (int i=0; i<values.get(sp.getAttribute()).size() && !found; i++) {
                    if (values.get(sp.getAttribute()).get(i) >= sp.getValue())
                        found = true;
                }
                return found;
            }
            else {
                return false;
            }
        }
    }
    
    /**
     * Splits a node into two nodes from a split following the identifier of a given number into an arraylist
     * of nodes.
     * 
     * @param sp    Split used in this node to divide the node into two new nodes left and right
     * @param newidentifier Identifier of the last node created in the algorithm
     * @return an arraylist with two nodes, node left and node right obtained from the original node with the split
     */
    public ArrayList <TreeNode> split (Split sp, int newidentifier) {
        ArrayList <TreeNode> result;
        Double value;
        
        result = new ArrayList <TreeNode> (2);
        
        left = new TreeNode ();
        right = new TreeNode ();
        
        // Before doing anything, we have to check is the split is valid
        if (sp.getAttribute() == -1) {
            // We weren't able to find a valid split, so we mark this node as leaf
            setAsLeaf();
            return null;
        }
        else if (!valid_split(sp)) {
            System.err.println("This split isn't valid, and cannot be used to split");
            System.exit(-1);
        }
        
        // First we have to copy the attributes into the new nodes
        left.the_attributes = new ArrayList<myAttribute>();
        right.the_attributes = new ArrayList<myAttribute>();
        for (int i=0; i<the_attributes.size(); i++) {
            left.the_attributes.add(new myAttribute(the_attributes.get(i)));
            right.the_attributes.add(new myAttribute(the_attributes.get(i)));
        }
        
        left.identifier = newidentifier + 1;
        right.identifier = newidentifier + 2;
        
        // Then, we split the separate list for each attribute
        left.values = new ArrayList <ArrayList <Double>> (values.size());
        right.values = new ArrayList <ArrayList<Double>> (values.size());
        
        // Initialize attributes list to contain empty lists
        for (int i=0; i<values.size(); i++) {
            left.values.add(new ArrayList <Double> ());
            right.values.add(new ArrayList <Double> ());
        }
        left.oclass = new ArrayList <Double> ();
        right.oclass = new ArrayList <Double> ();
        
        if (the_attributes.get(sp.getAttribute()).isNominal()) {
            // Attribute is categorical
            for (int j=0; j<values.get(sp.getAttribute()).size(); j++) {
                value = values.get(sp.getAttribute()).get(j);
                
                if (value == sp.getValue()) {
                    // This instance will belong to the left son
                    for (int k=0; k<values.size(); k++) {
                        left.values.get(k).add(values.get(k).get(j));
                    }
                    left.oclass.add(oclass.get(j));
                }
                else {
                    // This instance will belong to the right son
                    for (int k=0; k<values.size(); k++) {
                        right.values.get(k).add(values.get(k).get(j));
                    }
                    right.oclass.add(oclass.get(j));
                }
            }
        }
        else {
            // Attribute is numerical
            for (int j=0; j<values.get(sp.getAttribute()).size(); j++) {
                value = values.get(sp.getAttribute()).get(j);
                
                if (value < sp.getValue()) {
                    // This instance will belong to the left son
                    for (int k=0; k<values.size(); k++) {
                        left.values.get(k).add(values.get(k).get(j));
                    }
                    left.oclass.add(oclass.get(j));
                }
                else {
                    // This instance will belong to the right son
                    for (int k=0; k<values.size(); k++) {
                        right.values.get(k).add(values.get(k).get(j));
                    }
                    right.oclass.add(oclass.get(j));
                }
            }
        }
        
        if ((left.values.get(0).size() == 0) || (right.values.get(0).size() == 0)) {
            return null;
        }
        
        left.generator = generator;
        right.generator = generator;
        
        left.isLeaf = false;
        right.isLeaf = false;
        
        left.outputClass = -1;
        right.outputClass = -1;
        
        left.condition = null;
        right.condition = null;
        
        left.output_attribute = new myAttribute (output_attribute);
        right.output_attribute = new myAttribute (output_attribute);
        
        left.prunedClassifier = prunedClassifier;
        right.prunedClassifier = prunedClassifier;
        
        left.K = K;
        right.K = K;
        
        isLeaf = false;
        outputClass = -1;
        condition = sp;
        
        result.add(left);
        result.add(right);
        
        return result;
    }
    
    /**
     * Prunes all the leaves in the tree, this means, that all the leaves disappear and the ascendants
     * of those leaves become the new leaves of the tree with the classifier at leaves
     */
    public void pruneAllLeaves () {
        if ((!isLeaf) && (left != null) && (right != null)) {
            if (left.isLeaf && right.isLeaf) {
                // This node will be a leaf node and its children disappear
                System.out.println("Nodes " + left.identifier + " and " + right.identifier + " are pruned, and node " + identifier + " is set as a leaf node");
                setAsClassifierLeaf ();
            }
            else {
                left.pruneAllLeaves();
                right.pruneAllLeaves();
            }
        }
    }
    
    /**
     * Prunes the leaves in the tree that have greater error than the general error of the tree, making
     * them leaves with classifiers on them
     */
    public void pruneWithError () {
        int [] total_error;
        double general_error;
        
        total_error = getTreeError();
        general_error = (double)total_error[0]/(double)total_error[1];
        
        if (isLeaf) {
            prune(general_error);
        }
        else {
            left.prune(general_error);
            right.prune(general_error);
        }
    }
    
    /**
     * Prunes this leave or its descendants according to the error given, making it a leave with a
     * classifier on it
     * 
     * @param error General error of the tree that has to be surpassed in order to prune the leave
     */
    public void prune (double error) {
        if (isLeaf) {
            int [] local_error = getNodeError();
            double node_error = (double)local_error[0]/(double)local_error[1];
            
            if (node_error > error) {
                System.out.println("Node " + identifier + " is set as a leaf node with a classifier");
                setAsClassifierLeaf ();
            }
        }
        else {
            left.prune(error);
            right.prune(error);
        }
    }
    
    /**
     * Obtains the general error of the tree from all its leaves in a two number array
     * 
     * @return an array with two numbers that summarizes the general error of the tree
     */
    public int [] getTreeError () {
        int [] error, auxerror;
        
        error = new int [2];
        error[0] = 0;
        error[1] = 1;
        
        if (isLeaf) {
            error = getNodeError();
        }
        else {
            if (left != null) {
                auxerror = left.getTreeError();
                error[0] += auxerror[0];
                error[1] += auxerror[1];
            }
            if (right != null) {
                auxerror = right.getTreeError();
                error[0] += auxerror[0];
                error[1] += auxerror[1];
            }
        }
        
        return error;
    }
    
    /**
     * Obtains the error of a leave node in a the tree from all the instances in the leaf in 
     * a two number array
     * 
     * @return an array with two numbers that summarizes the leaf error in the tree
     */
    public int [] getNodeError () {
        int [] error = new int [2];
    
        if (isLeaf) {
            error[0] = 0;
            error[1] = oclass.size();
            for (int i=0; i<oclass.size(); i++) {
                if (oclass.get(i).intValue() != outputClass) {
                    error[0]++;
                }
            }
        }
        else {
            System.err.println("This node isn't a leaf, so the error cannot be computed");
            System.exit(-1);
        }
        
        return error;
    }
    
    /**
     * This function builds the normalized values matrix for the node's data which means that it
     * normalizes the values and stores them in the matrix
     */
    private void normalizeValues() {
        double minimum [];
        double range [];
        
        normalized_values = new double [values.get(0).size()][values.size()];
        
        minimum = new double[values.size()];
        range = new double[values.size()];     

        for (int i=0; i<values.size(); i++) {
            if (!the_attributes.get(i).isNominal()) {
                minimum[i] = the_attributes.get(i).getMin();
                range[i] = the_attributes.get(i).getMax() - minimum[i];
            }
        }
        
        // Both real and nominal data are normalized in [0,1]
        for (int i=0; i<values.get(0).size(); i++) {
            for (int j = 0; j < values.size(); j++) {
                if (the_attributes.get(j).isNominal()) {
                    if (the_attributes.get(j).getValues().size() > 1) {
                        normalized_values[i][j] = values.get(j).get(i)/the_attributes.get(j).getValues().size()-1;
                    }
                }
                else {
                    normalized_values[i][j] = values.get(j).get(i) - minimum[j];
                    normalized_values[i][j] = normalized_values[i][j] / range[j];
                }
            }
        }
    }
    
    /**
     * This function normalizes the values for a given example looking at the values of the attributes
     * in this node
     * 
     * @param example   An array with the values that have to be normalized
     * @return an array with the values normalized accordingly to the values of the whole node
     */
    private double [] normalize (double example[]) {
        double minimum [];
        double range [];
        double result [] = new double [values.size()];
        
        minimum = new double[values.size()];
        range = new double[values.size()];     

        for (int i=0; i<values.size(); i++) {
            if (!the_attributes.get(i).isNominal()) {
                minimum[i] = the_attributes.get(i).getMin();
                range[i] = the_attributes.get(i).getMax() - minimum[i];
            }
        }
        
        // Both real and nominal data are normalized in [0,1]
        for (int j = 0; j < values.size(); j++) {
            if (the_attributes.get(j).isNominal()) {
                if (the_attributes.get(j).getValues().size() > 1) {
                    result[j] = example[j]/the_attributes.get(j).getValues().size()-1;
                }
            }
            else {
                result[j] = example[j] - minimum[j];
                result[j] = result[j] / range[j];
            }
        }
        return result;
    }
    
    /**
     * Classifies a given item with the information stored in the node with the KNN classifier
     * 
     * @param item  Data attribute values for the item we are classifying
     * @return the class asigned to the item given
     */
    private int evaluateKNN (double item[]) {
        double minDist[];
        double element[];
        int nearestN[];
        int selectedClasses[];
        double dist;
        int prediction;
        int predictionValue;
        boolean stop;
        int num_classes = output_attribute.getValues().size();
        
        element = normalize(item);
        
        nearestN = new int[K];
        minDist = new double[K];
    
        for (int i=0; i<K; i++) {
            nearestN[i] = -1;
            minDist[i] = Double.MAX_VALUE;
        }
        
        //KNN Method starts here
        for (int i=0; i<normalized_values.length; i++) {
            dist = distance(normalized_values[i], element);
            
            // See if it's nearer than our previous selected neighbors
            stop = false;
                
            for(int j=0; j<K && !stop;j++){
                
                if (dist < minDist[j]) {
                        
                    for (int l = K - 1; l >= j+1; l--) {
                        minDist[l] = minDist[l - 1];
                        nearestN[l] = nearestN[l - 1];
                    }   
                        
                    minDist[j] = dist;
                    nearestN[j] = i;
                    stop=true;
                }
            }
            
        }
        
        // We have check all the instances... see what is the most present class
        selectedClasses = new int[num_classes];
    
        for (int i=0; i<num_classes; i++) {
            selectedClasses[i] = 0;
        }   
        
        for (int i=0; i<K; i++) {
            if (nearestN[i] != -1) {
                selectedClasses[oclass.get(nearestN[i]).intValue()] += 1;
            }
        }
        
        prediction=0;
        predictionValue=selectedClasses[0];
        
        for (int i=1; i<num_classes; i++) {
            if (predictionValue < selectedClasses[i]) {
                predictionValue = selectedClasses[i];
                prediction = i;
            }
        }
        
        if (predictionValue == 0) {
            prediction = getMajorOutputClass();
        }
        
        return prediction;
    }
    
    /** 
     * Calculates the euclidean distance between two instances
     * 
     * @param instance1 First instance to calculate the distance
     * @param instance2 Second instance to calculate the distance
     * @return the euclidean distance between them
     * 
     */
    private double distance (double instance1[], double instance2[]) {
        double length = 0.0;

        for (int i=0; i<instance1.length; i++) {
            length += (instance1[i]-instance2[i])*(instance1[i]-instance2[i]);
        }
            
        length = Math.sqrt(length); 
                
        return length;
    }
    
    /** 
     * Precalculates the radius of each train instance, used in the KNNAdaptive classifier
     */ 
    private void calculateRadius(){
        
        int ownClass;
        double minDist;
        double dist;

        radius = new double[normalized_values.length];
        
        for(int i=0;i<normalized_values.length;i++){
            
            ownClass = oclass.get(i).intValue();

            minDist=Double.MAX_VALUE;
            
            //Search the nearest enemy (instance from another class)
            for(int j=0; j<normalized_values.length;j++){
            
                if(ownClass != oclass.get(j).intValue()){
                    
                    dist = distance(normalized_values[i], normalized_values[j]);
                    
                    if (dist < minDist){
                        minDist=dist;
                    }
                
                }
            }
            
            radius[i] = minDist;
        }
        
    } //end-method  
    
    /**
     * Classifies a given item with the information stored in the node with the KNNAdaptive classifier
     * 
     * @param item  Data attribute values for the item we are classifying
     * @return the class asigned to the item given
     */
    private int evaluateKNNAdaptive (double item[]) {
        double example[];
        double minDist[];
        int nearestN[];
        int selectedClasses[];
        double dist;
        int prediction;
        int predictionValue;
        boolean stop;
        int num_classes = output_attribute.getValues().size();

        example = normalize(item);
        
        nearestN = new int[K];
        minDist = new double[K];
    
        for (int i=0; i<K; i++) {
            nearestN[i] = -1;
            minDist[i] = Double.MAX_VALUE;
        }
        
        //KNN Method starts here
        
        for (int i=0; i<normalized_values.length; i++) {
        
            dist = adaptiveDistance(normalized_values[i], example, i);

            // See if it's nearer than our previous selected neighbors
            stop=false;
                
            for(int j=0;j<K && !stop;j++){
                
                if (dist < minDist[j]) {
                        
                    for (int l = K - 1; l >= j+1; l--) {
                        minDist[l] = minDist[l - 1];
                        nearestN[l] = nearestN[l - 1];
                    }   
                        
                    minDist[j] = dist;
                    nearestN[j] = i;
                    stop=true;
                }
            }
        }
        
        //we have check all the instances... see what is the most present class
        selectedClasses= new int[num_classes];
    
        for (int i=0; i<num_classes; i++) {
            selectedClasses[i] = 0;
        }   
        
        for (int i=0; i<K; i++) {
            if (nearestN[i] != -1) {
                selectedClasses[oclass.get(nearestN[i]).intValue()]+=1;
            }
        }
        
        prediction=0;
        predictionValue=selectedClasses[0];
        
        for (int i=1; i<num_classes; i++) {
            if (predictionValue < selectedClasses[i]) {
                predictionValue = selectedClasses[i];
                prediction = i;
            }
        }
        
        if (predictionValue == 0) {
            prediction = getMajorOutputClass();
        }
        
        return prediction;
    }
    
    /** 
     * Calculates the adaptive distance between two instances
     * 
     * @param instance1 First instance to calculate the distance
     * @param instance2 Second instance to calculate the distance
     * @param index Index of train instance in radius structure 
     * @return the adaptive distance between them
     */ 
    private double adaptiveDistance (double instance1[], double instance2[], int index) {
        double dist;
        
        dist = distance(instance1, instance2);
        
        // Apply the radius conversion               
        dist = dist/radius[index];

        return dist;
    } //end-method  
    
    /** 
     * Calculates, for each train instance, the distance to its further K neighbour
     */
    private void getFurtherNeighbor(){
        double minDist[];
        int nearestN[];
        double dist;
        boolean stop;
        
        further = new double [normalized_values.length];
        selected = new boolean [normalized_values.length];    
        
        nearestN = new int[K];
        minDist = new double[K];
        
        for(int instance=0;instance<normalized_values.length;instance++){

            Arrays.fill(nearestN,-1);
            Arrays.fill(minDist,Double.MAX_VALUE);

            //find its K nearest neighbors
            for (int i=0; i<normalized_values.length; i++) {
                dist = distance(normalized_values[instance], normalized_values[i]);

                // see if it's nearer than our previous selected neighbors
                stop=false;
                    
                for(int j=0;j<K && !stop;j++){
                    if (dist < minDist[j]) {
                        for (int l = K - 1; l >= j+1; l--) {
                            minDist[l] = minDist[l - 1];
                            nearestN[l] = nearestN[l - 1];
                        }   
                            
                        minDist[j] = dist;
                        nearestN[j] = i;
                        stop=true;
                    }
                }
            }
            
            // Get the maximum distance
            further[instance]=minDist[K-1];
        }
    }
    
    /**
     * Classifies a given item with the information stored in the node with the KSNN classifier
     * 
     * @param item  Data attribute values for the item we are classifying
     * @return the class asigned to the item given
     */
    private int evaluateKSNN (double item[]) {
        int output;
        int votes[];
        double minDist[];
        double example[];
        int nearestN[];
        double dist;
        boolean stop;
        int maxVotes;
        int num_classes = output_attribute.getValues().size();
        
        example = normalize(item);
        
        votes = new int[num_classes];
        nearestN = new int[K];
        minDist = new double[K];
        
        for (int i=0; i<normalized_values.length; i++) {
            selected[i]=false;
        }
        
        //find its K nearest neighbors
        
        for (int i=0; i<K; i++) {
            nearestN[i] = -1;
            minDist[i] = Double.POSITIVE_INFINITY;
        }
        
        for (int i=0; i<normalized_values.length; i++) {
        
            dist = distance(example,normalized_values[i]);

            //see if it's nearer than our previous selected neighbors
            stop=false;
                
            for (int j=0;j<K && !stop;j++){
                if (dist < minDist[j]) {
                    for (int l = K - 1; l >= j+1; l--) {
                        minDist[l] = minDist[l - 1];
                        nearestN[l] = nearestN[l - 1];
                    }   
                        
                    minDist[j] = dist;
                    nearestN[j] = i;
                    stop=true;
                }
            }
            
            //Select if the example would be a nearest neighbor
            if (dist < further[i]) {
                selected[i]=true;
            }
        }
        
        //Select the neighbors     
        for (int i=0; i<K; i++) {
            if (nearestN[i] != -1) {
                selected[nearestN[i]]=true;
            }
        }

        // Voting process
        for (int i=0; i<num_classes; i++) {
            votes[i]=0;
        }

        for (int i=0; i<normalized_values.length; i++) {
            if(selected[i]==true){
                votes[oclass.get(i).intValue()]++;
            }
        }
        
        //Select the final output
        output=-1;
        maxVotes=0;
        
        for(int i=0;i<num_classes;i++){
            
            if(maxVotes<votes[i]){          
                maxVotes=votes[i];
                output=i;
            }
        }
        
        if (maxVotes == 0) {
            output = getMajorOutputClass();
        }
        
        return output;
    }
    
    /** 
     * Calculates the mean (centroid) of each class
     */ 
    private void calculateMeans () {
        int num_classes = output_attribute.getValues().size();
        int isClass;
        int nInstances [] = new int [num_classes];
        
        for(int i=0; i<num_classes; i++){
            nInstances[i]=0;
        }
        for(int i=0;i<oclass.size();i++){
            nInstances[oclass.get(i).intValue()]++;
        }
        
        means = new double[num_classes][values.size()];
        meanClass = new int[num_classes];
        
        //Initialize the mean's structure
        for(int i=0;i<num_classes;i++){
            for(int j=0;j<values.size();j++){
                means[i][j]=0.0;
            }   
            meanClass[i]=i;
        }
        
        // Calculate the sum of every instance for each class
        
        for (int i=0;i<normalized_values.length;i++){
            isClass=oclass.get(i).intValue();
            
            for(int j=0;j<values.size();j++){
                means[isClass][j]+=normalized_values[i][j];
            }   
        }
        
        // Get the means
        for(int i=0;i<num_classes;i++){
            for(int j=0;j<values.size();j++){
                if(nInstances[i]>0){
                    means[i][j]/=(double)nInstances[i];
                }
            }           
        }       
    }
    
    /**
     * Classifies a given item with the information stored in the node with the NM classifier
     * 
     * @param item  Data attribute values for the item we are classifying
     * @return the class asigned to the item given
     */
    private int evaluateNM (double item[]) {
        int output;
        double aux;
        double min;
        double [] example;
        
        example = normalize(item);
        
        min=Double.MAX_VALUE;
        output=-1;
        
        //get the nearest mean
        for(int i=0;i<means.length;i++){
            
            aux=distance(example,means[i]);
            
            if(aux<min){
                min=aux;
                output=i;
            }
        }
        
        //use their class
        output=meanClass[output];
        
        return output;
    }
    
    /**
     * Calculates the count matrix of the data in this node, which is used in the evaluation process
     * in the Naive Bayes classifier. The count matrix is a matrix that stores the number of items in
     * each class depending on the values of certain attributes.
     */
    private void calculateCountMatrix () {
        int [][] aux_count_matrix;
        
        count_matrix = new ArrayList <int [][]> (); // Histogram for Naive-Bayes
        
        for (int i=0; i<the_attributes.size(); i++) {
            aux_count_matrix = new int[the_attributes.get(i).getValues().size()][output_attribute.getValues().size()+1];
                
            for (int j=0; j<the_attributes.get(i).getValues().size(); j++) {
                for (int k=0; k<output_attribute.getValues().size(); k++) {
                    aux_count_matrix[j][k] = 0;
                }
                aux_count_matrix[j][output_attribute.getValues().size()] = 0;
            }
                
            for (int j=0; j<values.get(i).size(); j++) {
                aux_count_matrix[values.get(i).get(j).intValue()][oclass.get(j).intValue()]++;
                aux_count_matrix[values.get(i).get(j).intValue()][output_attribute.getValues().size()]++;
            }
                
            count_matrix.add(aux_count_matrix);
        }
    }

    /**
     * Classifies a given item with the information stored in the node with the Naive Bayes classifier
     * 
     * @param item  Data attribute values for the item we are classifying
     * @return the class asigned to the item given
     */
    private int evaluateNaiveBayes (double item[]) {
        int selected;
        double max;
        double [] points;
        int num_classes = output_attribute.getValues().size();
        
        points = new double[num_classes];
        
        // Initialize points
        for(int j=0;j<num_classes;j++){
            points[j]=1.0;
        }
        
        // Accumulate points
        for(int j=0;j<item.length;j++){
            for(int k=0;k<num_classes;k++){
                // Here we do the Laplace correction
                points[k] = points[k] * ((double)(count_matrix.get(j)[(int)item[j]][k]+1)/(double)(count_matrix.get(j)[(int)item[j]][num_classes]+1));
            }
        }
        
        // Find the maximum
        selected=-1;
        max=0;
        
        for (int j=0;j<num_classes;j++){
            if (max<=points[j]) {
                max=points[j];
                selected=j;
            }
        }
        
        return selected;
    }

}


