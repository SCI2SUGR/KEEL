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
 * File: Credits.java
 *
 * Showing the credits of the application
 *
 * @author Written by Admin 4/8/2010
 * @version 1.0
 * @since JDK1.5
 */
package keel.GraphInterKeel.experiments;

import java.awt.*;
import java.awt.event.*;
import java.util.ResourceBundle;
import javax.swing.*;

import keel.GraphInterKeel.menu.*;

public class Credits extends JFrame implements ActionListener {

    protected JLabel titleLabel,  aboutLabel[];
    protected static int labelCount = 5;
    protected static int aboutWidth = 400;
    protected static int aboutHeight = 300;
    protected static int aboutTop = 200;
    protected static int aboutLeft = 350;
    protected Font titleFont,  bodyFont,  miniFont,  subtFont;
    protected ResourceBundle resbundle;
    protected Experiments parent;

    /**
     * Default builder
     *
     * @param itParent Parent frame
     */
    public Credits(Experiments itParent) {
        super("");

        parent = itParent;
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(
                this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/logo.gif")));
        this.setTitle("About ...");
        this.setResizable(false);
        SymWindow aSymWindow = new SymWindow();
        this.addWindowListener(aSymWindow);

        // Initialize useful fonts
        titleFont = new Font("Arial", Font.BOLD, 22);


        if (titleFont == null) {
            titleFont = new Font("Arial", Font.BOLD, 22);
        }
        bodyFont = new Font("Arial", Font.PLAIN, 12);
        if (bodyFont == null) {
            bodyFont = new Font("Arial", Font.PLAIN, 12);
        }
        miniFont = new Font("Arial", Font.PLAIN, 10);
        if (miniFont == null) {
            miniFont = new Font("Arial", Font.PLAIN, 10);
        }
        subtFont = new Font("Arial", Font.BOLD, 14);
        if (subtFont == null) {
            subtFont = new Font("Arial", Font.BOLD, 14);

        }
        this.setBackground(new Color(225, 225, 225));
        this.getContentPane().setLayout(new BorderLayout(10, 10));

        aboutLabel = new JLabel[labelCount];
        aboutLabel[0] = new JLabel("");
        aboutLabel[0].setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/logotipo.png")));
        aboutLabel[0].setBounds(new Rectangle(15, 13, 85, 63));
        aboutLabel[1] = new JLabel("KEEL Software Tool V2.0");
        aboutLabel[1].setFont(titleFont);
        aboutLabel[2] = new JLabel("For more information about the software tool ");
        aboutLabel[2].setFont(miniFont);
        aboutLabel[3] = new JLabel("and the KEEL project, please visit:");
        aboutLabel[3].setFont(miniFont);


        aboutLabel[4] = new JLabel("http://www.keel.es");
        aboutLabel[4].setFont(miniFont);
        aboutLabel[4].addMouseListener(new Frame_logotipo_mouseAdapter(this));


        Panel textPanel2 = new Panel(new GridLayout(labelCount, 1));
        textPanel2.setBackground(new Color(74, 117, 177));
        for (int i = 0; i < labelCount; i++) {
            aboutLabel[i].setHorizontalAlignment(JLabel.CENTER);
            textPanel2.add(aboutLabel[i]);
        }

        this.getContentPane().add(textPanel2, BorderLayout.CENTER);
        this.pack();
        this.setLocation(aboutLeft, aboutTop);
        this.setSize(aboutWidth, aboutHeight);
        try {
            initCredits();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class Frame_logotipo_mouseAdapter extends MouseAdapter {

        private Credits adaptee;

        Frame_logotipo_mouseAdapter(Credits adaptee) {
            this.adaptee = adaptee;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            adaptee.logotipo_mouseEntered(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            adaptee.logotipo_mouseExited(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            adaptee.logotipo_mousePressed(e);
        }
    }

    /**
     * Entering logo
     *
     * @param e Event
     */
    public void logotipo_mouseEntered(MouseEvent e) {
        this.setCursor(Cursor.HAND_CURSOR);
    }

    /**
     * Exiting logo
     *
     * @param e Event
     */
    public void logotipo_mouseExited(MouseEvent e) {
        this.setCursor(Cursor.DEFAULT_CURSOR);
    }

    /**
     * Pressing logo
     *
     * @param e Event
     */
    public void logotipo_mousePressed(MouseEvent e) {
        BrowserControl.displayURL("http://www.keel.es");
    }

    class SymWindow extends java.awt.event.WindowAdapter {

        @Override
        public void windowClosing(java.awt.event.WindowEvent event) {
            parent.setEnabled(true);
            setVisible(false);
        }
    }

    /**
     * Action performed
     * @param newEvent Handler
     */
    public void actionPerformed(ActionEvent newEvent) {
        setVisible(false);
    }

    private void initCredits() throws Exception {

        this.getContentPane().setBackground(Color.white);
        this.setFont(new java.awt.Font("Arial", 0, 11));

    }
}