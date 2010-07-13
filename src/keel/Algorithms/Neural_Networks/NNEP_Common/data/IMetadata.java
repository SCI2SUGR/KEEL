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

