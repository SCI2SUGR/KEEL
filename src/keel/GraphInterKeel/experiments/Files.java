package keel.GraphInterKeel.experiments;

/*
 * Created on 16-Jun-2004
 *
 * Class for use data files
 *
 */
/**
 * @author Jes�s Alcal� Fern�ndez
 *
 *
 */
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
     * @param name
     * @param text
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
