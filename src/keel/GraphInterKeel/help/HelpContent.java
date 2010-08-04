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
 * File: HelpFrame.java
 *
 * A class for managing the contents of the help
 *
 * @author Written by Admin 4/8/2010
 * @version 1.0
 * @since JDK1.5
 */
package keel.GraphInterKeel.help;

import java.awt.*;
import javax.swing.*;
import java.net.URL;
import java.io.IOException;

public class HelpContent extends JPanel {

    BorderLayout borderLayout1 = new BorderLayout();
    JScrollPane jScrollPane1 = new JScrollPane();
    JEditorPane contenido = new JEditorPane();

    /**
     * Builder
     */
    public HelpContent() {
        try {
            initializeHelpContent();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Initialization
     *
     * @throws java.lang.Exception
     */
    void initializeHelpContent() throws Exception {
        this.setLayout(borderLayout1);
        contenido.setFont(new java.awt.Font("Arial", 0, 11));
        contenido.setEditable(false);
        this.setFont(new java.awt.Font("Arial", 0, 11));
        jScrollPane1.setFont(new java.awt.Font("Arial", 0, 11));
        this.add(jScrollPane1, BorderLayout.CENTER);
        jScrollPane1.getViewport().add(contenido, null);
    }

    /**
     * Shows a URL
     * @param url
     */
    public void muestraURL(URL url) {
        try {
            contenido.setPage(url);
        } catch (IOException e) {
        }
    }
}