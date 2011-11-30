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

/**
* <p>
* @author Written by Manuel Moreno (Universidad de Córdoba) 01/07/2008
* @version 0.1
* @since JDK 1.5
*</p>
*/

package keel.Algorithms.Decision_Trees.CART.tree;

/**
 * 
 * Class that implements a tree used by the CART algorithm
 *
 */
public class TreeNode
{	

	/** 
	 * Link to this node's parent. 
	 * Null when this node is the root of the tree 
	 */
	private TreeNode parent;

	/** Left son */
	private TreeNode leftSon;

	/** Right son */
	private TreeNode rightSon;

	/** Index of patterns from data set included in this node */
	private int[] patterns;

	/** Input variable index */
	private int variable = -1;

	/** Split value */
	private double value = -1;

	/** Output class  associated with this node. Index to data set output class */
	private int outputClass = -1;
	
	/** Output value associated with this node. Only used in regression problems */
	private double outputValue = -1;

	/** Impurities associated to this node. This improve the algorithm performance*/
	private double impurities; 
	
	/////////////////////////////////////////////////////////////////////
	// ------------------------------------------------------ Constructor
	/////////////////////////////////////////////////////////////////////
	/**
	 * Default Constructor
	 */
	public TreeNode(TreeNode parent) {
		super();
		this.parent = parent;
	}

	/**
	 * Constructor
	 * @param patterns Index of patterns in this node
	 */
	public TreeNode(TreeNode parent, int[] patterns) {
		this.parent = parent;
		this.patterns = patterns;
	}

	/////////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Getters and Setters
	/////////////////////////////////////////////////////////////////////

	/**
	 * It gets the parent of the current node
	 * 
	 * @return parent parent of current node
	 */
	public TreeNode getParent() {
		return parent;
	}

	/**
	 * It sets the parent of the current node
	 * 
	 * @param parent to set
	 */
	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	////////////////////////////////////////////////////////////////

	/**
	 * It gets the left son of the current node
	 * 
	 * @return left son of current node. null if it is terminal
	 */
	public TreeNode getLeftSon() {
		return leftSon;
	}

	/**
	 * @param leftSon son to set
	 */	
	public void setLeftSon(TreeNode leftSon) {
		this.leftSon = leftSon;
	}
	////////////////////////////////////////////////////////////////

	/**
	 * @return right son of current node. null if it is terminal
	 */
	public TreeNode getRightSon() {
		return rightSon;
	}

	/**
	 * @param rightSon to set
	 */
	public void setRightSon(TreeNode rightSon) {
		this.rightSon = rightSon;
	}

	////////////////////////////////////////////////////////////////
	/**
	 * @return the patterns
	 */
	public int[] getPatterns() {
		return patterns;
	}

	/**
	 * @param patterns the patterns to set
	 */
	public void setPatterns(int[] patterns) {
		this.patterns = patterns;
	}

	////////////////////////////////////////////////////////////////

	/**
	 * @return the variable
	 */
	public int getVariable() {
		return variable;
	}

	/**
	 * @param variable the variable to set
	 */
	public void setVariable(int variable) {
		this.variable = variable;
	}

	////////////////////////////////////////////////////////////////
	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}

	////////////////////////////////////////////////////////////////

	/**
	 * @return the outputClass
	 */
	public int getOutputClass() {
		return outputClass;
	}

	/**
	 * @param outputClass the outputClass to set
	 */
	public void setOutputClass(int outputClass) {
		this.outputClass = outputClass;
	}
	
	
	/**
	 * @return the outputValue
	 */
	public double getOutputValue() {
		return outputValue;
	}

	/**
	 * @param outputValue the outputValue to set
	 */
	public void setOutputValue(double outputValue) {
		this.outputValue = outputValue;
	}
	
	////////////////////////////////////////////////////////////////
	
	/**
	 * @return the impurities
	 */
	public double getImpurities() {
		return impurities;
	}

	/**
	 * @param impurities the impurities to set
	 */
	public void setImpurities(double impurities) {
		this.impurities = impurities;
	}
	
	
	/////////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////////


	/**
	 * @return depth of current node
	 */
	public int depth() {
		int leftDepth, rightDepth;
		if (leftSon != null)
			leftDepth = leftSon.depth();
		else
			leftDepth = 0;

		if (rightSon != null)
			rightDepth = rightSon.depth();
		else
			rightDepth = 0;

		return 1 + Math.max(leftDepth, rightDepth);
	}

	/**
	 * @return true if node is leaf; false otherwise
	 */
	public boolean isTerminal() {
		if (leftSon == null && rightSon == null)
			return true;
		else
			return false;
	}

	/**
	 * @param pattern pattern to evaluate
	 * @param regression flag to determine whether it is a regression or classification problem
	 */
	public double evaluate(double [] pattern, boolean regression) {

		if (this.isTerminal()) { // if terminal return output class
			if (regression)
				return outputValue;
			else
				return outputClass;
		}
		else {// else, call proper son's evaluation
			if (pattern[variable] <= value)
				return leftSon.evaluate(pattern, regression);
			else
				return rightSon.evaluate(pattern, regression);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		String result = new String();
		if (this.isTerminal())
			if (outputClass != -1)
				result = "Class: "+outputClass+"\n";
			else
				result = "Mean: "+outputValue+"\n";
		else {
			result = "If "+ variable+" <= "+value+";\n "; //Class: "+outputClass+"\n";
			if (leftSon != null)
				result += "Then "+leftSon.toString();
			if (rightSon != null)
				result += "Else\n"+ rightSon.toString();
		}
		return result;
	}
}

