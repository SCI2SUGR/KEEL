/**
 * File: Files.java.
 *
 * Implements methods to manage data files
 *
 * @author Written by Joaquin Derrac (University of Granada) 29/04/2010
 * @version 1.0
 * @since JDK1.5
*/
package keel.GraphInterKeel.statistical;

import java.io.*;

public class Files{

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

