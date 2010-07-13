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
 * Represents a selected value from a finite set of values, where each
 * value is a Tag (i.e. has some string associated with it). Primarily
 * used in schemes to select between alternative behaviours,
 * associating names with the alternative behaviours.
 */
public class SelectedAssociation {

    /** The index of the selected tag */
    protected int m_Selected;

    /** The set of tags to choose from */
    protected Association[] m_Tags;

    /**
     * Creates a new <code>SelectedAssociation</code> instance.
     *
     * @param tagID the id of the selected tag.
     * @param tags an array containing the possible valid Tags.
     * @exception IllegalArgumentException if the selected tag isn't in the array
     * of valid values.
     */
    public SelectedAssociation(int tagID, Association[] tags) {
        for (int i = 0; i < tags.length; i++) {
            if (tags[i].getID() == tagID) {
                m_Selected = i;
                m_Tags = tags;
                return;
            }
        }
        throw new IllegalArgumentException("Selected tag is not valid");
    }

    /** Returns true if this SelectedAssociation equals another object */
    public boolean equals(Object o) {
        if ((o == null) || !(o.getClass().equals(this.getClass()))) {
            return false;
        }
        SelectedAssociation s = (SelectedAssociation) o;
        if ((s.getTags() == m_Tags)
            && (s.getSelectedTag() == m_Tags[m_Selected])) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Gets the selected Tag.
     *
     * @return the selected Tag.
     */
    public Association getSelectedTag() {
        return m_Tags[m_Selected];
    }

    /**
     * Gets the set of all valid Tags.
     *
     * @return an array containing the valid Tags.
     */
    public Association[] getTags() {
        return m_Tags;
    }
}

