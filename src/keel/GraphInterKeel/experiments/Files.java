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
 * File: Files.java
 *
 * Class for use data files
 *
 * @author Jesus Alcala Fernandez
 * @version 1.0
 * @since JDK1.5
 */

package keel.GraphInterKeel.experiments;

import java.io.*;

public class Files {

    /**
     * Read of a file
     * @param name Name of the file
     * @return A string containing the text of the file
     */
    public static String readFile(String name) {
        String cadena = "";

        try {

            BufferedReader fis;
            if (name.substring(0, 4).equals("jar:") || name.substring(0, 5).equals("file:")) {
                java.net.URL miurl = new java.net.URL(name);
                InputStreamReader isr = new InputStreamReader(miurl.openStream());
                fis = new BufferedReader(isr);
            } else {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(
                        name));
                fis = new BufferedReader(isr);
            }

            String linea;
            while (true) {
                linea = fis.readLine();
                if (linea == null) {
                    break;
                }
                cadena += (linea + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return cadena;
    }

    /**
     * Read from a file
     * @param name Name of the file
     * @return A string containing the text of the file
     */
    public static String leeFicheroLinea(String name) {
        String cadena = "";

        try {

            BufferedReader fis;
            if (name.substring(0, 4).equals("jar:")) {
                java.net.URL miurl = new java.net.URL(name);
                InputStreamReader isr = new InputStreamReader(miurl.openStream());
                fis = new BufferedReader(isr);
            } else {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(
                        name));
                fis = new BufferedReader(isr);
            }

            String linea;
            linea = fis.readLine();
            cadena += (linea + "\n");

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return cadena;
    }

    /**
     * Writes a text in a output file
     * @param name Name of the file
     * @param text Text to write
     */
    public static void writeFile(String name, String text) {
        try {
            FileOutputStream f = new FileOutputStream(name);
            DataOutputStream fis = new DataOutputStream((OutputStream) f);

            fis.writeBytes(text);

            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Appends a text to a file
     *
     * @param name Name of the file
     * @param text Text to add
     */
    public static void addToFile(String name, String text) {
        try {
            RandomAccessFile fis = new RandomAccessFile(name, "rw");
            fis.seek(fis.length());

            fis.writeBytes(text);

            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
