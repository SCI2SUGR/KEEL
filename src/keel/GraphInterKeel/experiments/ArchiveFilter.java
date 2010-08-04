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
 * <p>Description: File filter</p>
 * @author Victor Manuel Gonzolez Quevedo
 * @version 1.0
 */
package keel.GraphInterKeel.experiments;

import java.io.*;

public class ArchiveFilter implements FilenameFilter {

    String[] files; // filtered file extensions

    /**
     * Builder
     * @param _files Files currently selected
     */
    public ArchiveFilter(String[] _files) {
        this.files = _files;
    }

    /**
     * Test if the file shoudl be accepted
     * @param dir Directory containing the file
     * @param name Name of the file
     * @return True if the file is accepted. False, if not
     */
    public boolean accept(File dir, String name) {
        // Accept only the correct files

        File f = new File(dir, name);
        if (f.isDirectory()) {
            return true;
        }

        int i = name.lastIndexOf('.');
        if (i > 0 && i < name.length() - 1) {
            String extension = name.substring(i + 1).toLowerCase();

            for (i = 0; i < files.length; i++) {
                // Check that extension is valid
                if (files[i].equals(extension)) {
                    return true;
                }
            }
        }
        return false;
    }
}
