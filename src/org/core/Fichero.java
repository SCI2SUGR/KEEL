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

/*
 * Created on 16-Jun-2004
 *
 * Clase implementada funciones para el manejo de ficheros de datos
 *
 */

/**
 * @author Jesús Alcalá Fernández
 *
 *
 */

package org.core;

import java.io.*;
import java.util.*;

public class Fichero {
    public static String leeFichero(String nombreFichero) {
        String cadena = "";
        try {
            FileInputStream fis = new FileInputStream(nombreFichero);
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
            System.exit( -1);
        }
        return cadena;
    }

    public static void escribeFichero(String nombreFichero, String cadena) {
        try {
            FileOutputStream f = new FileOutputStream(nombreFichero);
            DataOutputStream fis = new DataOutputStream((OutputStream) f);
            fis.writeBytes(cadena);
            fis.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit( -1);
        }
    }

    public static void AnadirtoFichero(String nombreFichero, String cadena) {
        try {
            RandomAccessFile fis = new RandomAccessFile(nombreFichero, "rw");
            fis.seek(fis.length());
            fis.writeBytes(cadena);
            fis.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit( -1);
        }
    }
}

