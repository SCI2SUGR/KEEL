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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierMOGUL;

/**
 * <p>
 * @author Written by Jesus Alcala Fernandez (University of Granada) 01/01/2004
 * @author Modified by Francisco José Berlanga (University of Jaén) 09/12/2008 
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */

import java.io.*;
import java.util.*;

public class MyFile{
/**
 * <p>
 * Functions for dealing with files
 * </p>
 */

	/**
	 * <p>
         * Function for reading a data file in a String Object
	 * </p>
	 * @param nombreMyFile String The file to be read
	 * @return String The contents of the file "nombreMyFile"
	 */        
        public static String ReadMyFile(String nombreMyFile) {
                String cadena = "";

            try {
                        FileInputStream fis = new FileInputStream(nombreMyFile);

                        byte[] leido = new byte[4096];
                        int bytesLeidos = 0;

                        while (bytesLeidos != -1) {
                                bytesLeidos = fis.read(leido);

                                if (bytesLeidos != -1) {
                                        cadena += new String(leido, 0, bytesLeidos);
                                }
                        }

                        fis.close();
                }
                catch (IOException e) {
                        e.printStackTrace();
                        System.exit(-1);
                }

                return cadena;
        }


	/**
	 * <p>
         * Function for writing a String Object in a file
	 * </p>
	 * @param nombreMyFile String The file to be write
	 * @param cadena String The contents to write on the file "nombreMyFile"	 
	 */           
        public static void WriteMyFile (String nombreMyFile, String cadena) {
            try {
                        FileOutputStream f = new FileOutputStream(nombreMyFile);
                        DataOutputStream fis = new DataOutputStream((OutputStream) f);

                        fis.writeBytes(cadena);

                        fis.close();
                }
                catch (IOException e) {
                        e.printStackTrace();
                        System.exit(-1);
                }
        }


	/**
	 * <p>
         * Function for adding a String Object to a file
	 * </p>
	 * @param nombreMyFile String The file to be write
	 * @param cadena String The contents to add on the file "nombreMyFile"	 	 
	 */           
        public static void AddtoMyFile (String nombreMyFile, String cadena) {
            try {
                        RandomAccessFile fis = new RandomAccessFile(nombreMyFile, "rw");
                        fis.seek(fis.length());

                        fis.writeBytes(cadena);

                        fis.close();
                }
                catch (IOException e) {
                        e.printStackTrace();
                        System.exit(-1);
                }
        }

}

