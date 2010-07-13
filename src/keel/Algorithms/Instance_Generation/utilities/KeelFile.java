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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Instance_Generation.utilities;
import java.io.*;

/**
 * Keel File function
 * @author diegoj
 */
public class KeelFile
{
    /**
     * Read a Keel-style file.
     * @param nombreFichero Name of the file.
     * @return String text of the readed file.
     */
    public static String read(String nombreFichero) {
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

    /**
     * Write in Keel-style a file.
     * @param nombreFichero Name of the file to be writed.
     * @param cadena Text to be written.
     */    
    public static void write(String nombreFichero, String cadena) {
        try {
            FileOutputStream f = new FileOutputStream(nombreFichero);
            DataOutputStream fis = new DataOutputStream((OutputStream) f);
          //  System.out.println("Cadena="+cadena);
            fis.writeBytes(cadena);
            fis.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit( -1);
        }
    }

    /**
     * Append text to a Keel-style file.
     * @param nombreFichero Name of the file.
     * @param cadena text of the appended file.
     */
    public static void append(String nombreFichero, String cadena) {
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
    
    /**
     * Copy a Keel-style file to another.
     * @param fromFileName Name of the original file.
     * @param toFileName Name of the destination file.
     */
    public static void copy(String fromFileName, String toFileName)
      throws IOException {
    File fromFile = new File(fromFileName);
    File toFile = new File(toFileName);

    if (!fromFile.exists())
      throw new IOException("FileCopy: " + "no such source file: "
          + fromFileName);
    if (!fromFile.isFile())
      throw new IOException("FileCopy: " + "can't copy directory: "
          + fromFileName);
    if (!fromFile.canRead())
      throw new IOException("FileCopy: " + "source file is unreadable: "
          + fromFileName);

    if (toFile.isDirectory())
      toFile = new File(toFile, fromFile.getName());

    if (false && toFile.exists()) {
      if (!toFile.canWrite())
        throw new IOException("FileCopy: "
            + "destination file is unwriteable: " + toFileName);
      System.out.print("Overwrite existing file " + toFile.getName()
          + "? (Y/N): ");
      System.out.flush();
      BufferedReader in = new BufferedReader(new InputStreamReader(
          System.in));
      String response = in.readLine();
      if (!response.equals("Y") && !response.equals("y"))
        throw new IOException("FileCopy: "
            + "existing file was not overwritten.");
    } else {
      String parent = toFile.getParent();
      if (parent == null)
        parent = System.getProperty("user.dir");
      File dir = new File(parent);
      if (!dir.exists())
        throw new IOException("FileCopy: "
            + "destination directory doesn't exist: " + parent);
      if (dir.isFile())
        throw new IOException("FileCopy: "
            + "destination is not a directory: " + parent);
      if (!dir.canWrite())
        throw new IOException("FileCopy: "
            + "destination directory is unwriteable: " + parent);
    }

    FileInputStream from = null;
    FileOutputStream to = null;
    try {
      from = new FileInputStream(fromFile);
      to = new FileOutputStream(toFile);
      byte[] buffer = new byte[4096];
      int bytesRead;

      while ((bytesRead = from.read(buffer)) != -1)
        to.write(buffer, 0, bytesRead); // write
    } finally {
      if (from != null)
        try {
          from.close();
        } catch (IOException e) {
          ;
        }
      if (to != null)
        try {
          to.close();
        } catch (IOException e) {
          ;
        }
    }
  }
}

