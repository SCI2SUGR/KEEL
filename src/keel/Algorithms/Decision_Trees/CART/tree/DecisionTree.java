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
	 * {@inheritDoc}
	 */
	public TreeNode getRoot() {
		return root;
	}
	
	/**
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
