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
