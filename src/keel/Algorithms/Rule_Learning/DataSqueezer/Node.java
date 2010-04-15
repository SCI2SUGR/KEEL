package keel.Algorithms.Rule_Learning.DataSqueezer;
import java.util.*;

/**
 *  Class to represent a node in the tree
 */
class Node 
{
	/** The entropy of data points if this node is a leaf node. */
	private double entropy;		
	
	/** The set of data points if this is a leaf node. */
	private Vector data;
	
	/** If this is not a leaf node, the attribute that is used to divide the set of data points. */
	private int attribute;			
	
	/** If this is not a leaf node, the attribute-value that is used to divide the set of data points. */
	private int value;				
	
	/** If this is not a leaf node, references to the children nodes. */
	private Node []children;		
	
	/** The parent to this node.  The root has parent == null. */
	private Node parent;			

	/** Creates a new node.
	 *
	 */
	public Node() 
	{
		data = new Vector();
	}
	
	/** Function to set value of the entropy of the node.
	 * 
	 * @param value 	The value of the entropy.
	 */
	public void setEntropy( double value )
	{
		entropy = value;
	}
	
	/** Returns the value of the entropy of the node.
	 * 
	 * @return		The value of the entropy.
	 */
	public double getEntropy()
	{
		return entropy;
	}
	
	/** Function to set the itemsets that satisifies the condition of the node. 
	 * 
	 * @param newData 	The itemsets.
	 */
	public void setData( Vector newData )
	{
		data = newData;
	}
	
	/** Returns the itemsets that satisfy the condition of the node. 
	 */
	public Vector getData()
	{
		return data;
	}
	
	/** Function to set the node used to decompose the node.
	 * 
	 * @param attIndex	The index of the attribute.
	 */
	public void setDecompositionAttribute( int attIndex )
	{
		attribute = attIndex;
	}
	
	/** Returns the index of the attribute used to decompose the node.
	 * 
	 */
	public int getDecompositionAttribute()
	{
		return attribute;
	}

	/** Function to set the value of the attribute used to decompose the node.
	 * 
	 * @param valIndex	The index of the value.
	 */
	public void setDecompositionValue( int valIndex )
	{
		value = valIndex;
	}
	
	/** Returns the index of the value used to decompose the node.
	 * 
	 */
	public int getDecompositionValue()
	{
		return value;
	}

	/** Function to set the children of the node.
	 * 
	 * @param nodes 	The children of the node.
	 */
	public void setChildren( Node []nodes )
	{
		children = nodes;
	}
	
	/** Function to add a child to the node.
	 * 
	 * @param node 	The new child.
	 */
	public void addChildren( Node node )
	{
		children[numChildren()] = node;
	}
	
	/** Returns the number of children of the node.
	 * 
	 */
	public int numChildren()
	{
		int nChildren = 0;
		
		for ( int i = 0; i < children.length; i++ )
			if ( children[i] != null )
				nChildren++;
			
		return nChildren;
	}
	
	/** Returns the children of the node. 
	 * 
	 */
	public Node[] getChildren()
	{
		return children;
	}

	/** Returns the child with the given index.
	 * 
	 * @param index		The index of the child.
	 */
	public Node getChildren( int index )
	{
		return children[index];
	}
	
	/** Function to set the parent of the node.
	 * 
	 * @param node		The parent of the node.
	 */ 
	public void setParent( Node node )
	{
		parent = node;
	}
	
	/** Returns the parent of the node. 
	 * 
	 */
	public Node getParent()
	{
		return parent;
	}
};