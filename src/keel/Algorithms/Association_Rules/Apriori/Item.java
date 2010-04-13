/**
* <p>
* @author Written by Cristobal Romero (Universidad de Córdoba) 27/02/2007
* @version 0.1
* @since JDK 1.5
*</p>
*/

package keel.Algorithms.Association_Rules.Apriori;
import java.util.*;
	
/**
 * Class to implement an Item.
 *
 */
public class Item 
{
	/** The label of the item. */
	private int label;			
	
	/** The support of the items. */
	private int support;
	
	/** The children of the items. */
	private Vector children;		
	
	/** Constructor for creating a Item object. 
	 *
	 */
	public Item() 
	{
		label = 0;
    support = 0;
    children = new Vector();
	}
  
	/** Constructor for creating an Item object and assign the label.
	 * 
	 * @param itemLabel		The label of the item.
	 */
	public Item( int itemLabel )
	{
		label = itemLabel;
    support = 0;
    children = new Vector();
	}

	/** Returns the label of the Item. 
	 * 
	 */
	public int getLabel() 
	{
		return label;
	}

	/** Function to increase the support of an item by one. 
	 *
	 */
	public void incSupport() 
	{
		support++;
	}

	/** Returns the support of the Item. 
	 * 
	 */
	public int getSupport() 
	{
		return support;
	}

	/** Function to add a child to the Item. 
	 * 
	 * @param child		The new child of the item.
	 */
	public void addChild( Item child )
	{
		children.addElement( child );
	}

	/** Function to add a child to the Item at the given position. 
	 * 
	 * @param child		The new child of the item.
	 * @param index		The index of the child.
	 */
	public void addChild( Item child, int index )
	{
		children.insertElementAt( child, index );
	}

	/** Returns if the item has children or not. 
	 * 
	 */
	public boolean hasChildren() 
	{ 
		try 
		{
 			if ( children.firstElement() != null )
 				return true;
 			else
 				return false;
 		}
		catch ( Exception e )
		{
			return false;
		}
	}

	/** Returns the children of the item. 
	 * 
	 */
	public Vector getChildren() 
	{
		return children;
	}
}