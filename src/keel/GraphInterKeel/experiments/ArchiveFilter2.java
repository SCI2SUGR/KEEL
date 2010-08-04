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
 * <p>Title: Keel</p>
 * <p>Description: File filter </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Universidad de Granada</p>
 * @author V�ctor Manuel Gonz�lez Quevedo
 * @version 0.1
 */
package keel.GraphInterKeel.experiments;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class ArchiveFilter2 extends FileFilter {

    String[] files; // filtered files extensions
    String description; // filter description

    /**
     * Builder
     * @param _files Files accepted
     * @param descr Description of the set
     */
    public ArchiveFilter2(String[] _files, String descr) {
        this.files = _files;
        this.description = descr;
    }

    /**
     * Tests if a file is accepted
     * @param f File to be tested
     * @return True if the file is accepted, false if not
     */
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String nombre = f.getName();
        int i = nombre.lastIndexOf('.');
        if (i > 0 && i < nombre.length() - 1) {
            String extension = nombre.substring(i + 1).toLowerCase();

            for (i = 0; i < files.length; i++) {
                // check that extension is valid
                if (files[i].equals(extension)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get the description of the archive
     * @return The description of the archive
     */
    public String getDescription() {
        return description;
    }
}
