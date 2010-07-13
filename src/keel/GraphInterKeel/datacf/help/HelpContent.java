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

import java.awt.*;
import javax.swing.*;
import java.net.URL;

public class HelpContent extends JPanel {

    /**
     * <p>
     * Class for including help of KEEL
     * </p>
     */

    // Layout
    private BorderLayout borderLayout1 = new BorderLayout();

    // JScrollPanel
    private JScrollPane jScrollPane1 = new JScrollPane();

    // JEditorPanel
    private JEditorPane contenido = new JEditorPane();

    /**
     * <p>
     * Constructor that initializes the panel
     * </p>
     */
    public HelpContent() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * <p>
     * Init the components
     * </p>
     * @throws java.lang.Exception Exception in the component initialization
     */
    void jbInit() throws Exception {
        this.setLayout(borderLayout1);
        contenido.setFont(new java.awt.Font("Arial", 0, 11));
        contenido.setEditable(false);
        this.setFont(new java.awt.Font("Arial", 0, 11));
        jScrollPane1.setFont(new java.awt.Font("Arial", 0, 11));
        this.add(jScrollPane1, BorderLayout.CENTER);
        jScrollPane1.getViewport().add(contenido, null);
    }

    /**
     * <p>
     * Set the URL to be shown
     * </p>
     * @param url URL help
     */
    public void muestraURL(URL url) {
        try {
            contenido.setPage(url);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}

