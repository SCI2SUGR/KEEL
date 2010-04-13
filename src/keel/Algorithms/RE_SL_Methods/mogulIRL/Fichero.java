package keel.Algorithms.RE_SL_Methods.mogulIRL;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
import java.io.*;
import java.util.*;

public class Fichero{


        /* Function for reading a data file in a String Object */
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
                        System.exit(-1);
                }

                return cadena;
        }


        /* Function for writing a String Object in a file */
        public static void escribeFichero (String nombreFichero, String cadena) {
            try {
                        FileOutputStream f = new FileOutputStream(nombreFichero);
                        DataOutputStream fis = new DataOutputStream((OutputStream) f);

                        fis.writeBytes(cadena);

                        fis.close();
                }
                catch (IOException e) {
                        e.printStackTrace();
                        System.exit(-1);
                }
        }


        /* Function for adding a String Object to a file */
        public static void AnadirtoFichero (String nombreFichero, String cadena) {
            try {
                        RandomAccessFile fis = new RandomAccessFile(nombreFichero, "rw");
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
