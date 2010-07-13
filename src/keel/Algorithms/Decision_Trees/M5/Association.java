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
* @author Written by Cristobal Romero (Universidad de Córdoba) 10/10/2007
* @version 0.1
* @since JDK 1.5
*</p>
*/

package keel.Algorithms.Decision_Trees.M5;

/**
 * An <code>Association</code> simply associates a numeric ID with a String description.
 */
public class Association {

    /** The ID */
    protected int m_ID;

    /** The descriptive text */
    protected String m_Readable;

    /**
     * Creates a new <code>Association</code> instance.
     *
     * @param ident the ID for the new Association.
     * @param readable the description for the new Association.
     */
    public Association(int ident, String readable) {
        m_ID = ident;
        m_Readable = readable;
    }

    /**
     * Gets the numeric ID of the Association.
     *
     * @return the ID of the Association.
     */
    public int getID() {
        return m_ID;
    }

    /**
     * Gets the string description of the Association.
     *
     * @return the description of the Association.
     */
    public String getReadable() {
        return m_Readable;
    }
}

