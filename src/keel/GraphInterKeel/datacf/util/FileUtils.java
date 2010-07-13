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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <p>
 * @author Written by Pedro Antonio Guti√©rrez and Juan Carlos Fern√°ndez (University of C√≥rdoba) 23/10/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
public class FileUtils {

    /**
     * <p>
     * Utilities for Files. Copy, delete,...
     * </p>
     */

    /**
     * <p>
     * Utilities for Files. Deletes recursively all the files and directories
     * contained in a given path
     * </p>
     * @param dirPath Path to delete all the files contained in it
     */
    public static void recursiveDelete(File dirPath) {
        String[] ls = dirPath.list();

        for (int idx = 0; idx < ls.length; idx++) {
            File file = new File(dirPath, ls[idx]);
            if (file.isDirectory()) {
                recursiveDelete(file);
            }
            file.delete();
        }
    }

    /**
     * <p>
     * Utilities for Files. Deletes recursively all the files and directories
     * contained in a given path and also the path
     * </p>
     * @param dirPath Path to delete all the files contained in it
     */
    public static void deletePath(File dirPath) {
        recursiveDelete(dirPath);
        dirPath.delete();
    }

    /**
     * <p>
     * Copy files
     * </p>
     * @param fromFileName From file name
     * @param toFileName Target file name
     * @throws java.io.IOException
     */
    @SuppressWarnings("empty-statement")
    public static void copy(String fromFileName, String toFileName)
            throws IOException {
        File fromFile = new File(fromFileName);
        File toFile = new File(toFileName);

        if (!fromFile.exists()) {
            throw new IOException("FileCopy: " + "no such source file: " + fromFileName);
        }
        if (!fromFile.isFile()) {
            throw new IOException("FileCopy: " + "can't copy directory: " + fromFileName);
        }
        if (!fromFile.canRead()) {
            throw new IOException("FileCopy: " + "source file is unreadable: " + fromFileName);
        }
        if (toFile.isDirectory()) {
            toFile = new File(toFile, fromFile.getName());
        }
        if (toFile.exists()) {
            if (!toFile.canWrite()) {
                throw new IOException("FileCopy: " + "destination file is unwriteable: " + toFileName);
            }
        } else {
            String parent = toFile.getParent();
            if (parent == null) {
                parent = System.getProperty("user.dir");
            }
            File dir = new File(parent);
            if (!dir.exists()) {
                throw new IOException("FileCopy: " + "destination directory doesn't exist: " + parent);
            }
            if (dir.isFile()) {
                throw new IOException("FileCopy: " + "destination is not a directory: " + parent);
            }
            if (!dir.canWrite()) {
                throw new IOException("FileCopy: " + "destination directory is unwriteable: " + parent);
            }
        }

        FileInputStream from = null;
        FileOutputStream to = null;
        try {
            from = new FileInputStream(fromFile);
            to = new FileOutputStream(toFile);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = from.read(buffer)) != -1) {
                to.write(buffer, 0, bytesRead); // write
            }
        } finally {
            if (from != null) {
                try {
                    from.close();
                } catch (IOException e) {
                    ;
                }
            }
            if (to != null) {
                try {
                    to.close();
                } catch (IOException e) {
                    ;
                }
            }
        }
    }
}

