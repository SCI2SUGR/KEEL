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

package keel.Algorithms.Neural_Networks.NNEP_Common.data;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * @author Written by Amelia Zafra, Sebastian Ventura (University of Cordoba) 17/07/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public class Metadata implements IMetadata 
{
	/**
	 * <p>
	 * Implementation of IMetadata interface.
	 * </p>
	 */
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------- Internal variables
	/////////////////////////////////////////////////////////////////
	
    /** Array list containing all attributes of this meta data. */
	
    protected ArrayList<IAttribute> attributesList = 
    	new ArrayList<IAttribute>();

    /** Mapping of attribute names to attributes. */
    
    protected HashMap<String, IAttribute> attributesMap = 
    	new HashMap<String, IAttribute>();

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////

    /**
     * <p>
     * Empty constructor
     * </p>
     */
    
	public Metadata() 
	{
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////

	// IMetadata interface
	
    /**
     * <p>
     * Returns number of mining attributes in mining data specification
     * </p>
     * @return number of mining attributes
     */
	public int numberOfAttributes() 
	{
		return attributesList.size();
	}

    /**
     * <p>
     * Get mining attribute by name
     * </p>
     * @param attributeName name of attribute required
     * @return specified mining attribute, null if not found
     */
	public IAttribute getAttribute(String attributeName) 
	{		
		return attributesMap.get(attributeName);
	}

    /**
     * <p>
     * Get mining attribute by index of the array of attributes of
     * mining data specification
     * </p>
     * @param attributeIndex index of attribute required
     * @return specified mining attribute, null if not found
     */
	public IAttribute getAttribute(int attributeIndex) 
	{
		return attributesList.get(attributeIndex);
	}

    /**
     * <p>
     * Get index of given attribute in this specification
     * </p>
     * @param attribute Attribute ...
     * @return index of attribute, -1 if attribute is not found
     */
	public int getIndex(IAttribute attribute) 
	{
		return attributesList.indexOf(attribute);
	}

    /**
     * <p>
     * Get index of given attribute in this specification
     * </p>
     * @param attributeName Attribute name 
     * @return index of attribute, -1 if attribute is not found
     */
	public int getIndex(String attributeName) 
	{
		IAttribute attribute = attributesMap.get(attributeName);
		if (attribute == null) {
			return -1;
		}
		else {
			return attributesList.indexOf(attribute);
		}
	}

	// Metadata update methods
	
    /**
     * <p>
     * Adds an attribute to this metadata
     * </p>
     * <p>
     * If the name of the new attribute is empty or there already 
     * exists an attribute with the same name, it is not added to 
     * the name hashtable.
     * </p>
     * <p>
     * This means that it could not be retrieved via its name. It is
     * highly recommended only to use attributes with unique names.
     * </p>
     * @param attribute mining attribute to add
     * @return true attribute also added to name hashtable, false if attribute
     * name is null or there already exists an attribute with the same name
     */
    public boolean addAttribute( IAttribute attribute )
    {
   		String attributeName = attribute.getName();
   		if ( attributeName == null || attributesMap.get(attributeName) != null ) {
   			return false;    			
   		}
   		else {
       		attributesList.add( attribute );
       		attributesMap.put(attributeName, attribute);
       		return true;
    	}
    }
	
    /**
     * <p>
     * Sets array of all attributes of this specification
     * </p>
     * @param attributesArray array of all attributes to set
     */
    public void setAttributesArray(IAttribute[] attributesArray)
    {
    	// Clear attributes list and map
        attributesList.clear(); attributesMap.clear();
        // Add all attributes
        for (IAttribute attribute : attributesArray) {
            addAttribute(attribute);
        }
    }

    
    // Podriamos annadir algun otro metodo...
}

