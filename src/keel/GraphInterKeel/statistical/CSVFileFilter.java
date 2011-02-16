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
 * File: CSVFileFilter.java
 *
 * This class filter CSV methods for file dialogs in the module
 *
 * @author Written by Joaquin Derrac (University of Granada) 29/04/2010
 * @version 1.1
 * @since JDK1.5
*/

package keel.GraphInterKeel.statistical;

import java.io.File;
import java.util.Vector;
import javax.swing.filechooser.*;

public final class CSVFileFilter extends FileFilter {

    private Vector<String> extensions = new Vector<String>();
    private String filterName = null;

    /**
     * Set the filter name
     * @param fn New name of the filter
     */
    public void setFilterName(String fn) {
        filterName = new String(fn);
    }

    /**
     * Adds an extension to the filter
     * @param ex Nex extension to add
     */
    public void addExtension(String ex) {
        extensions.add(new String(ex));
    }

    /**
     * Test if the file is accepted
     * @param f File to be tested
     * @return True if the file is accepted, false if not
     */
    public boolean accept(File f) {
        String filename = f.getName();

        for (int i = 0; i < extensions.size(); i++) {
            if (filename.endsWith(extensions.elementAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the filter name
     * @return The filter name
     */
    public String getDescription() {
        return filterName;
    }
}
