/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    S. García (sglopez@ujaen.es)
    F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
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

package keel.GraphInterKeel.experiments;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class DialogSeed extends JDialog {

    JPanel panel1 = new JPanel() {

        public void paintComponent(Graphics g) {
            //  ImageIcon img = new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/experimentos/seed.jpg"));
            //g.drawImage(img.getImage(), 0, 0, null);
            super.paintComponent(g);
        }
    };
    JCheckBox jCheckBox1 = new JCheckBox();
    JTextField jTextField1 = new JTextField();
    JButton jButton1 = new JButton();
    JButton jButton2 = new JButton();
    Experiments parent;
    //ImageIcon image1 = new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/ico/aceptar.gif"));
    //ImageIcon image2 = new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/ico/cancelar.gif"));

    public DialogSeed(Experiments frame, String title, boolean modal) {
        super(frame, title, modal);
        parent = frame;
        try {
            jbInit();
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public DialogSeed() {
        this(null, "", false);
    }

    private void jbInit() throws Exception {
        panel1.setLayout(null);
        this.setSize(new Dimension(350, 200));
        panel1.setBackground(new Color(225, 225, 225));
        panel1.setFont(new java.awt.Font("Arial", 0, 11));
        panel1.setOpaque(false);
        panel1.setPreferredSize(new Dimension(350, 200));
        jCheckBox1.setBackground(new Color(225, 225, 225));
        jCheckBox1.setFont(new java.awt.Font("Arial", 0, 11));
        jCheckBox1.setOpaque(false);
        jCheckBox1.setHorizontalAlignment(SwingConstants.TRAILING);
        jCheckBox1.setHorizontalTextPosition(SwingConstants.TRAILING);
        jCheckBox1.setSelected(true);
        jCheckBox1.setText("Automatic (Random Seed)");
        jCheckBox1.setBounds(new Rectangle(90, 49, 155, 23));
        jCheckBox1.addActionListener(new DialogSemilla_jCheckBox1_actionAdapter(this));
        //Seed should not be automatic by default
        /*if (parent.experimentGraph.autoSeed) {
            jTextField1.setEnabled(false);
            jCheckBox1.setSelected(true);
        } else {
            jTextField1.setEnabled(true);
            jCheckBox1.setSelected(false);
        }*/
        jTextField1.setEnabled(true);
        jCheckBox1.setSelected(false);
        //
        jTextField1.setFont(new java.awt.Font("Arial", 0, 11));
        jTextField1.setText(Long.toString(parent.experimentGraph.getSeed()));
        jTextField1.setBounds(new Rectangle(97, 84, 143, 24));
        jButton1.setBackground(new Color(225, 225, 225));
        jButton1.setBounds(new Rectangle(43, 144, 100, 30));
        jButton1.setFont(new java.awt.Font("Arial", 0, 11));
        jButton1.setOpaque(false);
        jButton1.setToolTipText("Apply changes");
        //jButton1.setIcon(image1);
        jButton1.setMnemonic('A');
        jButton1.setText("Apply");
        jButton1.addActionListener(new DialogSemilla_jButton1_actionAdapter(this));
        jButton2.setBackground(new Color(225, 225, 225));
        jButton2.setBounds(new Rectangle(202, 144, 100, 30));
        jButton2.setFont(new java.awt.Font("Arial", 0, 11));
        jButton2.setOpaque(false);
        jButton2.setToolTipText("Don\'t apply the changes");
        //jButton2.setIcon(image2);
        jButton2.setMnemonic('C');
        jButton2.setText("Cancel");
        jButton2.addActionListener(new DialogSemilla_jButton2_actionAdapter(this));
        this.getContentPane().setBackground(new Color(225, 225, 225));
        this.setFont(new java.awt.Font("Arial", 0, 11));
        panel1.add(jTextField1, null);
        panel1.add(jCheckBox1, null);
        panel1.add(jButton1, null);
        panel1.add(jButton2, null);
        this.getContentPane().add(panel1, BorderLayout.SOUTH);
    }

    void jCheckBox1_actionPerformed(ActionEvent e) {
        if (jCheckBox1.isSelected()) {
            parent.experimentGraph.autoSeed = true;
            jTextField1.setEnabled(false);
        } else {
            parent.experimentGraph.autoSeed = false;
            jTextField1.setEnabled(true);
        }
    }

    void jButton1_actionPerformed(ActionEvent e) {
        try {
            parent.experimentGraph.setSeed(Long.parseLong(jTextField1.getText()));
            this.setVisible(false);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Seed must be a number", "Error", 2);
        }

    }

    void jButton2_actionPerformed(ActionEvent e) {
        this.dispose();
    }
}

class DialogSemilla_jCheckBox1_actionAdapter
        implements java.awt.event.ActionListener {

    DialogSeed adaptee;

    DialogSemilla_jCheckBox1_actionAdapter(DialogSeed adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jCheckBox1_actionPerformed(e);
    }
}

class DialogSemilla_jButton1_actionAdapter
        implements java.awt.event.ActionListener {

    DialogSeed adaptee;

    DialogSemilla_jButton1_actionAdapter(DialogSeed adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton1_actionPerformed(e);
    }
}

class DialogSemilla_jButton2_actionAdapter
        implements java.awt.event.ActionListener {

    DialogSeed adaptee;

    DialogSemilla_jButton2_actionAdapter(DialogSeed adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton2_actionPerformed(e);
    }
}

