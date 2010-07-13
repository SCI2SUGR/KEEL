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

/**
 * 
 * File: TreeNode.java
 * 
 * Data structure that is used in the construction of the decision tree.
 * It stores the information about the relationship between nodes in a tree, and
 * the type of node that it is. This class has a identifier that relates the
 * instances of this class with the instances of the Node class.
 * 
 * @author Written by Victoria Lopez Morales (University of Granada) 29/04/2009 
 * @version 1.0 
 * @since JDK1.5
 */

public class TreeNode {
    /**
     * Identifier of the node
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
     * Creates a node with empty values that we can identify
     */ 
    TreeNode () {
        identifier = 0;
        
        left = null;
        right = null;
        
        isLeaf = false;
        outputClass = -1;
        
        condition = null;
    }
    
    /** 
     * Creates a node with the identifier, the descendants and its condition as a leaf node. It also
     * includes the output class selected for it and the relationship between the nodes
     *
     * @param id  Number identifying the node that is being created
     * @param newleft   Left descendant of the node that is being created
     * @param newright  Right descendant of the node that is being created
     * @param leaf  Whether the new node is a leaf node or not
     * @param oclass    Output class for the node that is being created
     * @param cond  Way to split this node into its descendants
     */     
    TreeNode (int id, TreeNode newleft, TreeNode newright, boolean leaf, int oclass, Split cond) {
        identifier = id;
        
        left = newleft;
        right = newright;
        
        isLeaf = leaf;
        outputClass = oclass;
        
        condition = cond;
    }
    
    /** 
     * Creates a node with the identifier, its condition as a leaf node and the output class selected
     * for it; the rest of the values are initialized with empty values that we can identify. It is used
     * when we create a leaf node
     *
     * @param id  Number identifying the node that is being created
     * @param leaf  Whether the new node is a leaf node or not
     * @param oclass    Output class for the node that is being created
     */     
    TreeNode (int id, boolean leaf, int oclass) {
        identifier = id;
        
        left = null;
        right = null;
        
        isLeaf = leaf;
        outputClass = oclass;
        
        condition = null;
    }
    
    /** 
     * Creates a node from another existing node
     *
     * @param tree  Original node from which we are going to create a copy
     */   
    TreeNode (TreeNode tree) {
        this.identifier = tree.identifier;
        
        this.left = new TreeNode(tree.left);
        this.right = new TreeNode(tree.right);
        
        this.isLeaf = tree.isLeaf;
        this.outputClass = tree.outputClass;
        
        this.condition = new Split (tree.condition);
    }
    
    /** 
     * Checks if a node is the same node as another object
     *
     * @param obj  Object that is checked to see if it is the same node
     * @return true if the nodes are the same, false otherwise
     * @see java.lang.Object#equals(java.lang.Object)
     */ 
    public boolean equals (Object obj) {
        // First we check if the reference is the same
        if (this == obj)
            return true;
        
        // Then we check if the object exists and is from the class TreeNode
        if((obj == null) || (obj.getClass() != this.getClass()))
            return false;
        
        // object must be TreeNode at this point
        TreeNode test = (TreeNode)obj;
        
        // We check the class attributes of the TreeNode class
        return ((identifier == test.identifier) && (isLeaf == test.isLeaf) && (outputClass == test.outputClass) && (left == test.left || (left != null && left.equals(test.left))) && (right == test.right || (right != null && right.equals(test.right))) && (condition == test.condition || (condition != null && condition.equals(test.condition))));
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
        hash = 31 * hash + (null == condition ? 0 : condition.hashCode());
        return hash;
    }
    
    /** 
     * Overriden function that converts the class to a string
     *
     * @return the string representation of the class
     * @see java.lang.Object#toString()
     */ 
    public String toString() { 
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
     * of the nodes in the whole tree
     *
     * @param depth Position in the tree of the node that is reflected in the string in a
     * major number of space in it
     * @param text Output where the tree is exposed
     * @throws Exception    If the tree cannot be printed.
     */ 
    private void printTree (int depth, StringBuffer text) throws Exception {
        String aux = "";

        for (int k = 0; k < depth; k++) {
          aux += "\t";
        }

        text.append(aux);
        if (isLeaf) {
            text.append(outputClass + " \n");
        }
        else {
            text.append("if (" + condition + ") then {\n");
            left.printTree(depth + 1, text);
            text.append(aux + "else { \n");
            right.printTree(depth + 1, text);
        }
        text.append(aux + "}\n");
    }
    
    /**
     * Prints the tree in a String with all the information that makes it human readable
     *
     * @param atts  Attributes used in the dataset for building the tree that are needed to change
     * the numberical information stored in the tree to words information
     * @param outputAttribute   Attribute used for the class in the dataset for building the tree
     * that is needed in a similar way to the attributes
     * @return a String with the tree in it
     */
    public String printTree (ArrayList <myAttribute> atts, myAttribute outputAttribute) {
        try {
            StringBuffer text = new StringBuffer();

            if (!isLeaf) {
                printTree(0, text, atts, outputAttribute);
            }

            return text.toString();
        } catch (Exception e) {
            return "Can not print the tree.";
        }
    }

    /** 
     * Prints the tree in a StringBuffer with a depth given according to the relationship
     * of the nodes in the whole tree with all the information that makes it human readable
     *
     * @param depth Position in the tree of the node that is reflected in the string in a
     * major number of space in it
     * @param text Output where the tree is exposed
     * @param atts  Attributes used in the dataset for building the tree that are needed to change
     * the numberical information stored in the tree to words information
     * @param outputAttribute   Attribute used for the class in the dataset for building the tree
     * that is needed in a similar way to the attributes
     * @throws Exception    If the tree cannot be printed.
     */ 
    private void printTree (int depth, StringBuffer text, ArrayList <myAttribute> atts, myAttribute outputAttribute) throws Exception {
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
            if (outputAttribute.isNominal()) {
                text.append(outputAttribute.getValues().get(outputClass) + " \n");
            }
            else {
                text.append(outputClass + " \n");
            }
        }
        else {
            if (atts.get(condition.getAttribute()).isNominal()) {
                text.append("if (" + atts.get(condition.getAttribute()).getName() + " in " + atts.get(condition.getAttribute()).getValues().get((int)condition.getValue()) + ") then {\n");
            }
            else {
                text.append("if (" + atts.get(condition.getAttribute()).getName() + " < " + condition.getValue() + ") then {\n");
            }
            left.printTree(depth + 1, text, atts, outputAttribute);
            text.append(aux + "else { \n");
            right.printTree(depth + 1, text, atts, outputAttribute);
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
     * Classifies a given item with the information stored in the node and its descendants
     * 
     * @param ind  Data attribute values for the item we are classifying
     * @param atts  Attributes in the data set that are used for building the tree and describing the 
     * instance given
     * @return the class asigned to the item given
     */
    public int evaluate (double [] ind, ArrayList <myAttribute> atts) {
        if (isLeaf) {
            return outputClass;
        }
        else {
            if (condition != null) {
                if (atts.get(condition.getAttribute()).isNominal()) {
                    if (ind[condition.getAttribute()] == condition.getValue()) {
                        return left.evaluate(ind, atts);
                    }
                    else {
                        return right.evaluate(ind, atts);
                    }   
                }
                else {
                    if (ind[condition.getAttribute()] < condition.getValue()) {
                        return left.evaluate(ind, atts);
                    }
                    else {
                        return right.evaluate(ind, atts);
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
     * Removes the descendants from a identifier given of a node. The nodes whose descendants are being
     * removed become leaf nodes
     * 
     * @param original_identifier   Identifier of the node that is becoming a leaf node and whose descendants
     * are being removed
     * @return ArrayList with all the identifiers of the nodes that are being removed
     */
    public ArrayList <Integer> deleteDescendants (int original_identifier) {
        ArrayList <Integer> nodes_deleted;
        
        nodes_deleted = new ArrayList <Integer> ();
        
        // Obtain descendants of descendants
        if (left != null) {
            nodes_deleted.addAll(left.deleteDescendants(original_identifier));
        }
        
        if (right != null) {
            nodes_deleted.addAll(right.deleteDescendants(original_identifier));
        }
        
        left = null;
        right = null;
        condition = null;
        
        if (identifier != original_identifier) {
            nodes_deleted.add(identifier);
        }
        
        return nodes_deleted;
    }
}

