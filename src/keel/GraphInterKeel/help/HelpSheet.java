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
 * File: HelpSheet.java
 *
 * A class for managing help sheets
 *
 * @author Written by Admin 4/8/2010
 * @version 1.0
 * @since JDK1.5
 */
package keel.GraphInterKeel.help;

import java.net.URL;

public class HelpSheet {

    public String name;
    public URL adress;

    /**
     * Builder
     *
     * @param name Name of the sheet
     * @param file Associated file
     */
    public HelpSheet(String name, String file) {
        this.name = name;
        String prefix = "file:" + System.getProperty("user.dir") + System.getProperty("file.separator");
        try {
            adress = new URL(prefix + file);
        } catch (java.net.MalformedURLException exc) {
            adress = null;
        }
    }

    /**
     * Builder
     *
     * @param name Name of the sheet
     * @param file Associated file
     */
    public HelpSheet(String name, URL file) {
        this.name = name;
        adress = file;
    }

    /**
     * To string method
     *
     * @return String representation
     */
    @Override
    public String toString() {
        return name;
    }
}