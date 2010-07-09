/**
 * File: HelpContent.java
 *
 * This class shows the help panel of the module
 *
 * @author Written by Joaquin Derrac (University of Granada) 29/04/2010
 * @version 1.0
 * @since JDK1.5
*/
package keel.GraphInterKeel.statistical.help;

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
