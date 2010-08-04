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
 *
 * File: Path.java
 *
 * A class for managing paths of files
 *
 * @author Written by Admin 4/8/2010
 * @version 1.0
 * @since JDK1.5
 */
package keel.GraphInterKeel.util;

import java.io.File;

/**
 *
 * @author Administrador
 */
public class Path {

    protected static String path = ".";

    /**
     * Get the path
     *
     * @return Path
     */
    public static String getPath() {
        return path;
    }

    /**
     * Set the path
     *
     * @param path Path to set
     */
    public static void setPath(String path) {
        Path.path = path;
    }

    /**
     * Generate a file with the path stored
     *
     * @return New file
     */
    public static File getFilePath() {
        return new File(path);
    }

    /**
     * Sets the path from a given file
     *
     * @param filePath Reference file
     */
    public static void setFilePath(File filePath) {
        Path.path = filePath.getPath();
    }
}
