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



package org.core;

import java.io.*;
import java.util.*;


/**
 * <p> Implements methods to manage data files
 * @author Written by Jesus Alcala (University of Granada) 16/06/2004
 * @author Modified by Pedro Gonzalez (University of Jaen) 22/12/2008
 * @version 2
 * @since JDK1.5
 * </p>
 */
public class Files{
/**
 * <p>
 * Implements methods to manage data files
 * </p>
 */


    /**
     * <p>
     * Read a file and returns the content
     * </p>
     * @param fileName Name of the file to read
     * @return A string with the content of the file
     */
    public static String readFile(String fileName) {
        String content = "";
        try {
                FileInputStream fis = new FileInputStream(fileName);
                byte[] piece = new byte[4096];
                int readBytes = 0;
                while (readBytes != -1) {
				readBytes = fis.read(piece);
				if (readBytes != -1) {
					content += new String(piece, 0, readBytes);
				}
		}
		fis.close();
	    }
	catch (IOException e) {
	        e.printStackTrace();
	        System.exit(-1);
	    }

        return content;
    }


    /**
     * <p>
     * Writes data in the file, overwriting previous content 
     * </p>
     * @param fileName Name of the file to read
     * @param content The content to be written
     */
    public static void writeFile (String fileName, String content) {
        try {
                FileOutputStream f = new FileOutputStream(fileName);
                DataOutputStream fis = new DataOutputStream((OutputStream) f);
                fis.writeBytes(content);
                fis.close();
	    }
        catch (IOException e) {
	        e.printStackTrace();
	        System.exit(-1);
	    }
    }


    /**
     * <p>
     * Adds data in the file, avoiding overwrite previous content 
     * </p>
     * @param fileName Name of the file to read
     * @param content The content to be written
     */
    public static void addToFile (String fileName, String content) {
        try {
                RandomAccessFile fis = new RandomAccessFile(fileName, "rw");
                fis.seek(fis.length());
                fis.writeBytes(content);
                fis.close();
            }
        catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
    }


}


