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
 * File: DialogUser.java
 *
 * A class for managing user methods
 *
 * @author Written by Admin 4/8/2010
 * @version 1.0
 * @since JDK1.5
 */
package keel.GraphInterKeel.experiments;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.io.*;

public class DialogUser extends JDialog {

    JPanel panel1 = new JPanel() {

        @Override
        public void paintComponent(Graphics g) {
            //ImageIcon img = new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/experimentos/usuario.jpg"));
            //g.drawImage(img.getImage(), 0, 0, null);
            super.paintComponent(g);
        }
    };
    JButton jButton1 = new JButton();
    ImageIcon image1 = new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/aceptar.gif"));
    ImageIcon image2 = new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/browser.gif"));
    JPanel jPanel1 = new JPanel();
    TitledBorder titledBorder1;
    JLabel jLabel5 = new JLabel();
    JTextField jTextField1 = new JTextField();
    JButton jButton3 = new JButton();
    JTextField jTextField2 = new JTextField();
    JButton jButton4 = new JButton();
    JLabel jLabel6 = new JLabel();
    JLabel jLabel7 = new JLabel();
    JTextField jTextField3 = new JTextField();
    JLabel jLabel8 = new JLabel();
    UserMethod theMethod;

    /**
     * Buider
     *
     * @param frame Parent frame
     * @param title Title of the frame
     * @param modal Modal status
     * @param metodo Name of the method
     */
    public DialogUser(Frame frame, String title, boolean modal,
            UserMethod metodo) {
        super(frame, title, modal);
        this.theMethod = metodo;
        try {
            initUser();
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Default builder
     */
    public DialogUser() {
        this(null, "", false, null);
    }

    /**
     * Initialize
     * @throws java.lang.Exception
     */
    private void initUser() throws Exception {

        panel1.setLayout(null);
        jButton1.setBackground(new Color(225, 225, 225));
        jButton1.setBounds(new Rectangle(133, 186, 110, 30));
        jButton1.setFont(new java.awt.Font("Arial", 0, 11));
        jButton1.setOpaque(false);
        jButton1.setIcon(image1);
        jButton1.setMnemonic('A');
        jButton1.setText("Continue");
        jButton1.addActionListener(new DialogUsuario_jButton1_actionAdapter(this));
        jPanel1.setBackground(new Color(225, 225, 225));
        jPanel1.setFont(new java.awt.Font("Arial", 0, 11));
        jPanel1.setBorder(BorderFactory.createEtchedBorder());
        jPanel1.setOpaque(false);
        jPanel1.setBounds(new Rectangle(9, 27, 382, 129));
        jPanel1.setLayout(null);
        jLabel5.setBackground(new Color(225, 225, 225));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11));
        jLabel5.setText("Executable File");
        jLabel5.setBounds(new Rectangle(16, 15, 81, 20));
        jTextField1.setFont(new java.awt.Font("Arial", 0, 11));
        jTextField1.setText(theMethod.dsc.getPath() + theMethod.dsc.getName());
        jTextField1.setBounds(new Rectangle(96, 15, 203, 20));
        jButton3.setBackground(new Color(225, 225, 225));
        jButton3.setBounds(new Rectangle(329, 15, 33, 20));
        jButton3.setFont(new java.awt.Font("Arial", 0, 11));
        jButton3.setOpaque(false);
        jButton3.setIcon(image2);
        jButton3.setText("");
        jButton3.addActionListener(new DialogUsuario_jButton3_actionAdapter(this));
        jTextField2.setBounds(new Rectangle(96, 47, 203, 20));
        jTextField2.setFont(new java.awt.Font("Arial", 0, 11));
        jTextField2.setText(theMethod.patternFile);
        jButton4.setText("");
        jButton4.addActionListener(new DialogUsuario_jButton4_actionAdapter(this));
        jButton4.setBackground(new Color(225, 225, 225));
        jButton4.setBounds(new Rectangle(329, 47, 33, 20));
        jButton4.setFont(new java.awt.Font("Arial", 0, 11));
        jButton4.setOpaque(false);
        jButton4.setIcon(image2);
        jLabel6.setBounds(new Rectangle(16, 47, 81, 20));
        jLabel6.setBackground(new Color(225, 225, 225));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11));
        jLabel6.setText("Pattern File");
        jLabel7.setBackground(new Color(225, 225, 225));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11));
        jLabel7.setText("Command");
        jLabel7.setBounds(new Rectangle(16, 80, 57, 20));
        jTextField3.setFont(new java.awt.Font("Arial", 0, 11));
        jTextField3.setText(theMethod.command);
        jTextField3.setBounds(new Rectangle(96, 80, 112, 20));
        jLabel8.setBounds(new Rectangle(226, 80, 145, 20));
        jLabel8.setBackground(new Color(225, 225, 225));
        jLabel8.setEnabled(true);
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11));
        jLabel8.setForeground(Color.darkGray);
        jLabel8.setText("Example (java, perl, ...)");
        panel1.setBackground(new Color(225, 225, 225));
        panel1.setFont(new java.awt.Font("Arial", 0, 11));
        panel1.setOpaque(false);
        this.getContentPane().setBackground(new Color(225, 225, 225));
        this.setFont(new java.awt.Font("Arial", 0, 11));
        getContentPane().add(panel1);
        jPanel1.add(jLabel5, null);
        jPanel1.add(jLabel6, null);
        jPanel1.add(jLabel7, null);
        jPanel1.add(jTextField3, null);
        jPanel1.add(jTextField2, null);
        jPanel1.add(jTextField1, null);
        jPanel1.add(jButton3, null);
        jPanel1.add(jButton4, null);
        jPanel1.add(jLabel8, null);
        panel1.add(jButton1, null);
        panel1.add(jPanel1, null);
    }

    /**
     * Find executable file
     * @param e Event
     */
    void jButton3_actionPerformed(ActionEvent e) {
        JFileChooser f = new JFileChooser();
        f.setDialogTitle("Executable file");
        int opcion = f.showOpenDialog(this);
        if (opcion == JFileChooser.APPROVE_OPTION) {
            jTextField1.setText(f.getSelectedFile().getAbsolutePath());
        }

    }

    /**
     * Find pattern file
     *
     * @param e Event
     */
    void jButton4_actionPerformed(ActionEvent e) {
        JFileChooser f = new JFileChooser();
        f.setDialogTitle("Pattern file");
        int opcion = f.showOpenDialog(this);
        if (opcion == JFileChooser.APPROVE_OPTION) {
            jTextField2.setText(f.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * Inserting new user method
     * @param e Event
     */
    void jButton1_actionPerformed(ActionEvent e) {
        String aux = new String(theMethod.patternFile);
        File f = new File(jTextField1.getText());
        if (f.exists()) {
            for (int i = 0; i < Layer.numLayers; i++) {
                theMethod.dsc.setPath(f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(File.separatorChar) + 1), i);
                theMethod.dsc.setName(new String(f.getName()), i);
            }
            theMethod.patternFile = new String(jTextField2.getText());
            theMethod.command = new String(jTextField3.getText());
            f = new File(theMethod.patternFile);
            if (f.exists() && !aux.equalsIgnoreCase(theMethod.patternFile)) {
                String fichero = Files.leeFicheroLinea(theMethod.patternFile);
                if (fichero.substring(0, 9 > fichero.length() ? fichero.length() : 9).equalsIgnoreCase("algorithm")) {
                    theMethod.parametersUser = new Parameters(theMethod.patternFile, false);
                } else {
                    JOptionPane.showMessageDialog(this, "The file " + theMethod.patternFile + " is not a pattern file", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (!f.exists()) {
                theMethod.parametersUser = null;
            }

            if (theMethod.parametersUser != null) {
                this.setVisible(false);
                theMethod.dialog = new ParametersDialog2(theMethod.pd.parent, "Algorithm Parameters", false,
                        theMethod);
                theMethod.dialog.setSize(400, 580);
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                Dimension frameSize = theMethod.dialog.getSize();
                if (frameSize.height > screenSize.height) {
                    frameSize.height = screenSize.height;
                }
                if (frameSize.width > screenSize.width) {
                    frameSize.width = screenSize.width;
                }
                theMethod.dialog.setLocation((screenSize.width - frameSize.width) / 2,
                        (screenSize.height - frameSize.height) / 2);
                theMethod.dialog.setResizable(false);
                theMethod.dialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(theMethod.pd.parent,
                        "Pattern file incorrect. Can't continue",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(theMethod.pd.parent,
                    "Executable file incorrect. Can't continue",
                    "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    class DialogUsuario_jButton3_actionAdapter
            implements java.awt.event.ActionListener {

        DialogUser adaptee;

        DialogUsuario_jButton3_actionAdapter(DialogUser adaptee) {
            this.adaptee = adaptee;
        }

        public void actionPerformed(ActionEvent e) {
            adaptee.jButton3_actionPerformed(e);
        }
    }

    class DialogUsuario_jButton4_actionAdapter
            implements java.awt.event.ActionListener {

        DialogUser adaptee;

        DialogUsuario_jButton4_actionAdapter(DialogUser adaptee) {
            this.adaptee = adaptee;
        }

        public void actionPerformed(ActionEvent e) {
            adaptee.jButton4_actionPerformed(e);
        }
    }

    class DialogUsuario_jButton1_actionAdapter
            implements java.awt.event.ActionListener {

        DialogUser adaptee;

        DialogUsuario_jButton1_actionAdapter(DialogUser adaptee) {
            this.adaptee = adaptee;
        }

        public void actionPerformed(ActionEvent e) {
            adaptee.jButton1_actionPerformed(e);
        }
    }
}


