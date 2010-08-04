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
 * File: FileUtils.java
 *
 * A class for managing files
 *
 * @author Written by Admin 4/8/2010
 * @version 1.0
 * @since JDK1.5
 */
package keel.GraphInterKeel.experiments;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class FileUtils {

    /**
     * Copy a file
     * @param source_name the source file
     * @param dest_name the destination file
     */
    public static void copy(String source_name, String dest_name) {
        // File copy
        System.out.println("Copy from " + source_name + " to " + dest_name);

        File source_file = new File(source_name);
        File destination_file = new File(dest_name);
        FileInputStream source = null;
        FileOutputStream destination = null;
        byte[] buffer;
        int bytes_read;

        try {
            if (source_file.exists() && source_file.isFile() &&
                    !destination_file.exists()) {
                source = new FileInputStream(source_file);
                destination = new FileOutputStream(destination_file);
                buffer = new byte[1024];
                while (true) {
                    bytes_read = source.read(buffer);
                    if (bytes_read == -1) {
                        break;
                    }
                    destination.write(buffer, 0, bytes_read);
                }
            }
        } catch (Exception ex) {
        } finally {
            if (source != null) {
                try {
                    source.close();
                } catch (IOException e) {
                }
            }
            if (destination != null) {
                try {
                    destination.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Copy a file
     * @param source_url url with the source file
     * @param dest_name destination path
     */
    public static void copy(java.net.URL source_url, String dest_name) {
        // File copy

        File destination_file = new File(dest_name);
        FileOutputStream destination = null;
        InputStream source = null;
        byte[] buffer;
        int bytes_read;

        try {
            if (!destination_file.exists()) {
                source = source_url.openStream();
                destination = new FileOutputStream(destination_file);
                buffer = new byte[1024];
                while (true) {
                    bytes_read = source.read(buffer);
                    if (bytes_read == -1) {
                        break;
                    }
                    destination.write(buffer, 0, bytes_read);
                }
            }
        } catch (Exception ex) {
        } finally {
            if (source != null) {
                try {
                    source.close();
                } catch (IOException e) {
                }
            }
            if (destination != null) {
                try {
                    destination.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Gets a directory listing and stores it
     * @param directory The path to the directory
     * @param result vector in which the listing will be stored
     */
    public static void listDir(String directory, Vector result) {
        // List content of a directory

        File file = new File(directory);
        File listado[] = file.listFiles();
        if (listado.length == 0) {
            result.addElement(new String(directory + "/"));
        }
        for (int i = 0; i < listado.length; i++) {
            if (listado[i].isFile()) {
                result.add(new String(directory + "/" + listado[i].getName()));
            } else {
                listDir(directory + "/" + listado[i].getName(), result);
            }
        }
    }

    /**
     * Compress a list of files in a .zip file
     * @param destination the path to the destination ZIP file
     * @param files list of string with the paths to the files to be compressed
     */
    public static void ZipFiles(String destination, Vector files) {

        // compress in .zip file
        byte[] buf = new byte[1024];
        try {

            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(destination));

            // compress files
            for (int i = 0; i < files.size(); i++) {

                String fichero = (String) files.elementAt(i);
                File file = new File(fichero);

                if (file.isDirectory()) {
                    out.putNextEntry(new ZipEntry(fichero));
                    out.closeEntry();
                } else {
                    FileInputStream in = new FileInputStream(fichero);
                    out.putNextEntry(new ZipEntry(fichero));

                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }

                    out.closeEntry();
                    in.close();
                }
            }

            // close zip file
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the necessary directory to build a KEEL experiment
     * @param destination the destination directory (non-existant)
     * @param entryName the base-name of the directory
     */
    public static void prepareFileDirectories(String destination, String entryName) {
        File dir;

        dir = new File(destination + "/" + entryName.substring(0, (entryName.lastIndexOf("/")) < 0 ? 0 : (entryName.lastIndexOf("/"))));
        dir.mkdirs();
    }

    /**
     * Unzip a .zip file
     * @param destination destination of the unzziped file(s)
     * @param filename the path to the ZIP file
     */
    public static void UnZipFiles(String destination, String filename) {

        int BUFFER_SIZE = 8 * 1024;

        try {
            // Create a ZipInputStream to read the zip file
            BufferedOutputStream dest = null;
            FileInputStream fis = new FileInputStream(filename);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));

            // Loop over all of the entries in the zip file
            int count;
            byte data[] = new byte[BUFFER_SIZE];
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String entryName = entry.getName();
                    prepareFileDirectories(destination, entryName);
                    String destFN = destination + File.separator + entry.getName();

                    // Write the file to the file system
                    FileOutputStream fos = new FileOutputStream(destFN);
                    dest = new BufferedOutputStream(fos, BUFFER_SIZE);
                    while ((count = zis.read(data, 0, BUFFER_SIZE)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                }
            }
            zis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new directory if needed (no exists previously)
     * @param directory the new directory path
     */
    public static void mkdir(String directory) {
        File f = new File(directory);
        if (!f.exists()) {
            f.mkdir();
        }
    }

    /**
     * Creates a new directory tree if needed (no exists previously)
     * @param directory the new directory path
     */
    public static void mkdirs(String directory) {
        File f = new File(directory);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    /**
     * Remove all the files in a directory, and then deletes the latter
     * @param directory the directory to be deleted (path)
     */
    public static void rmdir(String directory) {
        File file = new File(directory);
        File listado[] = file.listFiles();
        for (int i = 0; i < listado.length; i++) {
            if (listado[i].isFile()) {
                listado[i].delete();
            } else {
                rmdir(listado[i].getAbsolutePath());
            }
        }
        file.delete();
    }
}
