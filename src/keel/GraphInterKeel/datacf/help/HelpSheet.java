/**
 * <p>
 * @author Administrator
 * @author Modified by Pedro Antonio Gutiérrez and Juan Carlos Fernández (University of Córdoba) 23/10/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
package keel.GraphInterKeel.datacf.help;

import java.net.URL;

public class HelpSheet {

    /**
     * <p>
     * A sheet of the KEEL help
     * </p>
     */

    /** Current name */
    private String name;

    /** Current URL Adress */
    protected URL urlAdress;

    /**
     * <p>
     * Constructor that initializes the sheet using a file
     * </p>
     * @param name Name of the sheet
     * @param file Name of the file to use
     */
    public HelpSheet(String name, String file) {
        this.name = name;
        String prefix = "file:" + System.getProperty("user.dir") + System.getProperty("file.separator");
        try {
            urlAdress = new URL(prefix + file);
        } catch (java.net.MalformedURLException exc) {
            urlAdress = null;
        }
    }

    /**
     * <p>
     * Constructor that initializes the sheet using an URL address
     * </p>
     * @param name Name of the sheet
     * @param adress URL adress
     */
    public HelpSheet(String nombre, URL adress) {
        this.name = nombre;
//    String[] fields = fichero.getFile().split("/");
//    this.name = fields[fields.length - 1];
        urlAdress = adress;
    }

    /**
     * <p>
     * Overriding toString method to obtain a description of the class
     * </p>
     * @return String Description of the class
     */
    public String toString() {
        return name;
    }
}
