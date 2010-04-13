package keel.Algorithms.Neural_Networks.NNEP_Common.data;

/**
 * <p>
 * @author Written by Amelia Zafra, Sebastian Ventura (University of Cordoba) 17/07/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public abstract class AbstractAttribute implements IAttribute 
{

/**
 * <p>
 * IAttribute abstract implementation
 * </p>
*/
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Attribute name */
	
	protected String name;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Empty constructor
	 * </p>
	 */	
	public AbstractAttribute() 
	{
		super();
	}

	/**
	 * <p>
	 * Constructor that sets attribute name
	 * </p>
	 * @param name Attribute name
	 */	
	public AbstractAttribute(String name) 
	{
		super();
		setName(name);
	}

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////

	// Setting properties
	
	/**
	 * <p>
	 * Set attribute name
	 * </p> 
	 * @param name New attribute name
	 */	
	public final void setName(String name) 
	{
		this.name = name;
	}

	// IAttribute interface
	
	/**
	 * <p>
	 * Access to the attribute name
	 * </p>	 * 
	 * @return attribute name
	 */
	public String getName() 
	{
		return name;
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Abstract Methods
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Access to the attribute type
	 * </p>
	 * @return attribute name
	 */
	public abstract AttributeType getType();
	
	/**
	 * <p>
	 * Check if this internal attribute value is valid
	 * </p>
	 * @return true|false
	 */	
	public abstract boolean isValid(double internalValue);
	
	/**
	 * <p>
	 * Check if this external attribute value is valid
	 * </p>
	 * @param externalValue Value to check
	 * @return true|false
	 */	
	public abstract boolean isValid(Object externalValue);

	/**
	 * <p>
	 * Parse an  external value to obtain  the internal value of the 
	 * Attribute
	 * </p>
	 * @return The external value of the attribute
	 */
	public abstract double parse(String externalValue);
	
	/**
	 * <p>
	 * Show an String which represents a given real value
	 * </p>
	 * @return The real value of the attribute
	 */	
	public abstract String show(double internalValue);
	
	
}
