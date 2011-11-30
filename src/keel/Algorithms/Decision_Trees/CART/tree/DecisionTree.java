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
 * This class represents a binary decision tree 
 *
 */
public class DecisionTree {

	/** Root Node */
	private TreeNode root;
	
	/////////////////////////////////////////////////////////////////////
	// ----------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Default Constructor
	 */
	public DecisionTree() {

	}
	
	/////////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Getters and Setters
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * It returns the root of the tree
	 * 
	 * @return the root of the tree
	 */
	public TreeNode getRoot() {
		return root;
	}
	
	/**
	 * It set the root of the tree
	 * 
	 * @param root the root to set
	 */
	public void setRoot(TreeNode root) 
	{
		this.root = root;
	}

	/////////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 * It returns the depth of the tree
	 * 
	 * @return depth of the tree
	 */
	public int depth() {
		if (root == null)
			return 0;
		else
			return root.depth();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Returns tree as a String in preorder format.
	 */
	@Override
	public String toString() {
		String result = "Tree detph: "+depth()+"\n";
		result += root.toString();
		return result;
	}
	
}

