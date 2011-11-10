/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S·nchez (luciano@uniovi.es)
    J. Alcal·-Fdez (jalcala@decsai.ugr.es)
    S. GarcÌa (sglopez@ujaen.es)
    A. Fern·ndez (alberto.fernandez@ujaen.es)
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
 * <p>
 * @author Administrator
 * @author Modified by Pedro Antonio Guti√©rrez and Juan Carlos Fern√°ndez (University of C√≥rdoba) 23/10/2008
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
     * @param nombre Name of the sheet
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

