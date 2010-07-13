/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S·nchez (luciano@uniovi.es)
    J. Alcal·-Fdez (jalcala@decsai.ugr.es)
    S. GarcÌa (sglopez@ujaen.es)
    A. Fern·ndez (alberto.fernandez@ujaen.es)
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

package keel.GraphInterKeel.datacf.util;

import java.io.File;
import java.util.Vector;
import javax.swing.filechooser.*;

/**
 * <p>
 * @author Written by Ignacio Robles
 * @author Modified by Pedro Antonio Guti√©rrez and Juan Carlos Fern√°ndez (University of C√≥rdoba) 23/10/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
public final class KeelFileFilter extends FileFilter {

    /**
     * <p>
     * Filter for files in a FileBrowser
     * </p>
     */

    /** Extensions of the file filter */
    private Vector<String> extensions = new Vector<String>();

    /** Name of the filter */
    private String filterName = null;

    /**
     * <p>
     * Sets Name of the Filer
     * </p>
     * @param fn Name of filter
     */
    public void setFilterName(String fn) {
        filterName = new String(fn);
    }

    /**
     * <p>
     * Adds extendion to the filter
     * </p>
     * @param ex Extension for the filter
     */
    public void addExtension(String ex) {
        extensions.add(new String(ex));
    }

    /**
     * Overriding the accept method for accepting
     * directory names
     * @param f File to evaluate
     * @return boolean Is the file accepted?
     */
    @Override
    public boolean accept(File f) {
        String filename = f.getName();

        if (f.isDirectory()) {
            return true;
        }
        for (int i = 0; i < extensions.size(); i++) {
            if (filename.endsWith(extensions.elementAt(i))) {
                return true;
            }
        }
        return false;
    }



    /**
     * Returns the description of the file filter
     * @return String Description of the file filter
     */
    @Override
    public String getDescription() {
        return filterName;
    }
}

