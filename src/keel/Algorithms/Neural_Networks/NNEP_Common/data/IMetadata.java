package keel.Algorithms.Neural_Networks.NNEP_Common.data;

/**
 * <p>
 * @author Written by Amelia Zafra, Sebastian Ventura (University of Cordoba) 17/07/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public interface IMetadata 
{
	/**
	 * <p>
	 * Dataset spectification
	 * </p>
	 */
	
    /**
     * <p>
     * Returns number of mining attributes in mining data specification
     * </p>
     * @return number of mining attributes
     */
    public int numberOfAttributes();

    /**
     * <p>
     * Get index of given attribute in this specification
     * </p>
     * @param attribute Attribute ...
     * @return index of attribute, -1 if attribute is not found
     */
    public int getIndex( IAttribute attribute );
    
    /**
     * <p>
     * Get index of given attribute in this specification
     * </p>
     * @param attributeName Attribute name 
     * @return index of attribute, -1 if attribute is not found
     */
    public int getIndex(String attributeName );

    /**
     * <p>
     * Get mining attribute by name
     * </p>
     * @param attributeName name of attribute required
     * @return specified mining attribute, null if not found
     */
    public IAttribute getAttribute( String attributeName );

    /**
     * <p>
     * Get mining attribute by index of the array of attributes of
     * mining data specification
     * </p>
     * @param attributeIndex index of attribute required
     * @return specified mining attribute, null if not found
     */
    public IAttribute getAttribute( int attributeIndex);
}
