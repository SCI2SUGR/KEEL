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
 * <p>Title: Keel</p>
 * <p>Description: experiment type selection</p>
 * @author Victor Manuel Gonzalez Quevedo
 * @author Modified by Juan Carlos Fernandez Caballero and Pedro Antonio Gutierrez (University of Córdoba) 7/07/2009
 * @version 1.0
 */
package keel.GraphInterKeel.experiments;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Rectangle;

import keel.GraphInterKeel.menu.Frame;

public class SelectExp extends JPanel {

    Experiments parent;
    ButtonGroup buttonGroup2 = new ButtonGroup();
    JPanel jPanel1 = new JPanel();
    JButton jRadioButton3 = new JButton();
    JButton jRadioButton1 = new JButton();
    JButton jRadioButton2 = new JButton();
    JLabel jLabel1 = new JLabel();
    JPanel jPanel2 = new JPanel();
    JLabel jLabel2 = new JLabel();
    JRadioButton jRadioButton4 = new JRadioButton();
    JRadioButton jRadioButton5 = new JRadioButton();
    JRadioButton jRadioButton6 = new JRadioButton();
    public boolean correct = false;
    GridLayout gridLayout1 = new GridLayout();
    JSpinner spinnerKFold = new JSpinner(new SpinnerNumberModel(10, 2, 10, 1));

    /**
     * Builder
     * @param f Parent frame
     */
    public SelectExp(Experiments f) {
        try {
            parent = f;
            initSelectExperiment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializing
     * @throws java.lang.Exception
     */
    private void initSelectExperiment() throws Exception {
        jPanel1.setBackground(new Color(225, 225, 225));
        jPanel1.setFont(new java.awt.Font("Arial", 0, 11));
        jPanel1.setBorder(BorderFactory.createEtchedBorder());
        jPanel1.setLayout(null);
        jRadioButton3.setOpaque(false);
        jRadioButton3.setText("Unsupervised learning");
        jRadioButton3.addActionListener(new SelectExp_jRadioButton3_actionAdapter(this));
        jRadioButton3.setBackground(new Color(225, 225, 225));
        jRadioButton3.setBounds(new Rectangle(11, 91, 156, 31));
        jRadioButton3.setFont(new java.awt.Font("Arial", 0, 11));
        /***************************************************************
         *********************  EDUCATIONAL KEEL  **********************
         **************************************************************/
        if (Frame.buttonPressed == 1) //Button Teaching pressed
        {
            jRadioButton3.setVisible(false);
        }
        /***************************************************************
         *********************  EDUCATIONAL KEEL  **********************
         **************************************************************/
        jRadioButton1.setOpaque(false);
        jRadioButton1.setText("Classification");
        jRadioButton1.addActionListener(new SelectExp_jRadioButton1_actionAdapter(this));
        jRadioButton1.setBackground(new Color(225, 225, 225));
        jRadioButton1.setBounds(new Rectangle(11, 31, 156, 31));
        jRadioButton1.setFont(new java.awt.Font("Arial", 0, 11));
        jRadioButton2.setOpaque(false);
        jRadioButton2.setText("Regression");
        jRadioButton2.addActionListener(new SelectExp_jRadioButton2_actionAdapter(this));
        jRadioButton2.setBackground(new Color(225, 225, 225));
        jRadioButton2.setBounds(new Rectangle(11, 61, 156, 31));
        jRadioButton2.setFont(new java.awt.Font("Arial", 0, 11));
        jLabel1.setBackground(Color.gray);
        jLabel1.setFont(new java.awt.Font("Arial", 1, 13));
        jLabel1.setForeground(Color.white);
        jLabel1.setOpaque(true);
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setText("Type of the experiment");
        jLabel1.setBounds(new Rectangle(0, 0, 180, 27));
        jPanel2.setBackground(new Color(225, 225, 225));
        jPanel2.setFont(new java.awt.Font("Arial", 0, 11));
        jPanel2.setBorder(BorderFactory.createEtchedBorder());
        jPanel2.setLayout(null);
        jLabel2.setBounds(new Rectangle(-1, 0, 181, 27));
        jLabel2.setText("Type of partitions");
        jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel2.setBackground(Color.gray);
        jLabel2.setFont(new java.awt.Font("Arial", 1, 13));
        jLabel2.setForeground(Color.white);
        jLabel2.setOpaque(true);
        jRadioButton4.setBounds(new Rectangle(14, 33, 140, 23));
        jRadioButton4.setText("k-fold cross validation");
        jRadioButton4.setBackground(new Color(225, 225, 225));
        jRadioButton4.setFont(new java.awt.Font("Arial", 0, 11));
        jRadioButton4.setOpaque(false);
        jRadioButton4.setSelected(true);
        //Nuevas funcionalidades
        jRadioButton4.addActionListener(new SelectExp_jRadioButton4_actionAdapter(this));
        //Nuevas funcionalidades
        jRadioButton5.setBounds(new Rectangle(14, 56, 156, 23));
        jRadioButton5.setBackground(new Color(225, 225, 225));
        jRadioButton5.setFont(new java.awt.Font("Arial", 0, 11));
        jRadioButton5.setOpaque(false);
        //Nuevas funcionalidades
        jRadioButton5.addActionListener(new SelectExp_jRadioButton5_actionAdapter(this));
        //Nuevas funcionalidades

        //Nuevas funcionalidades
        spinnerKFold.setBounds(143, 33, 34, 20);
        //Nuevas funcionalidades
        jRadioButton5.setText("5x2 cross validation");
        jRadioButton6.setBounds(new Rectangle(14, 79, 156, 23));
        jRadioButton6.setBackground(new Color(225, 225, 225));
        jRadioButton6.setFont(new java.awt.Font("Arial", 0, 11));
        jRadioButton6.setOpaque(false);
        jRadioButton6.setText("without validation");
        //Nuevas funcionalidades
        jRadioButton6.addActionListener(new SelectExp_jRadioButton6_actionAdapter(this));
        //Nuevas funcionalidades

        this.setLayout(gridLayout1);
        gridLayout1.setColumns(1);
        gridLayout1.setRows(3);
        this.setBackground(new Color(225, 225, 225));
        this.setFont(new java.awt.Font("Arial", 0, 11));
        jPanel1.add(jRadioButton1, null);
        jPanel1.add(jLabel1, null);
        jPanel1.add(jRadioButton2, null);
        jPanel1.add(jRadioButton3, null);
        this.add(jPanel2, null);
        jPanel2.add(jRadioButton4, null);
        jPanel2.add(jRadioButton5, null);
        //Nuevas funcionalidades
        jPanel2.add(spinnerKFold, null);
        //Nuevas funcionalidades
        jPanel2.add(jRadioButton6, null);
        jPanel2.add(jLabel2, null);
        this.add(jPanel1, null);
        buttonGroup2.add(jRadioButton4);
        buttonGroup2.add(jRadioButton5);
        buttonGroup2.add(jRadioButton6);
    }

    /**
     * Classification button
     * @param e Event
     */
    public void jRadioButton1_actionPerformed(ActionEvent e) {
        parent.expType = Experiments.CLASSIFICATION;
        /***************************************************************
         *********************  EDUCATIONAL KEEL  **********************
         **************************************************************/
        if (Frame.buttonPressed == 0) {
            parent.helpContent.muestraURL(this.getClass().getResource("/contextualHelp/data_set_exp.html"));
        } else {
            parent.helpContent.muestraURL(this.getClass().getResource("/contextualHelpDocente/data_set_exp.html"));
        }
        /***************************************************************
         *********************  EDUCATIONAL KEEL  **********************
         **************************************************************/
        if (jRadioButton4.isSelected()) {
            parent.cvType = Experiments.PK;
        } else if (jRadioButton5.isSelected()) {
            parent.cvType = Experiments.P5X2;
        } else {
            parent.cvType = Experiments.PnoVal;
        }

        //parent.continuarExperimento();
        //Nuevas funcionalidades
        parent.numberKFoldCross = this.getValueKFoldCross();
    //Nuevas funcionalidades
    }

    /**
     * Regression button
     * @param e Event
     */
    public void jRadioButton2_actionPerformed(ActionEvent e) {
        parent.expType = Experiments.REGRESSION;

        if (Frame.buttonPressed == 0) {
            parent.helpContent.muestraURL(this.getClass().getResource("/contextualHelp/data_set_exp.html"));
        } else {
            parent.helpContent.muestraURL(this.getClass().getResource("/contextualHelpDocente/data_set_exp.html"));
        }
        if (jRadioButton4.isSelected()) {
            parent.cvType = Experiments.PK;
        } else if (jRadioButton5.isSelected()) {
            parent.cvType = Experiments.P5X2;
        } else {
            parent.cvType = Experiments.PnoVal;
        }

        //parent.continuarExperimento();
        //Nuevas funcionalidades
        parent.numberKFoldCross = this.getValueKFoldCross();
    //Nuevas funcionalidades
    }

    /**
     * Unsupervised button
     * @param e Event
     */
    public void jRadioButton3_actionPerformed(ActionEvent e) {
        parent.expType = Experiments.UNSUPERVISED;

        if (jRadioButton4.isSelected()) {
            parent.cvType = Experiments.PK;
        } else if (jRadioButton5.isSelected()) {
            parent.cvType = Experiments.P5X2;
        } else {
            parent.cvType = Experiments.PnoVal;
        }

        //parent.continuarExperimento();
        //Nuevas funcionalidades
        parent.numberKFoldCross = this.getValueKFoldCross();
    //Nuevas funcionalidades
    }

    /**
     * Select k-folds
     * @param e Event
     */
    public void jRadioButton4_actionPerformed(ActionEvent e) {
        if (jRadioButton4.isSelected()) {
            //spinnerKFold.setOpaque(false);
            spinnerKFold.setEnabled(true);
        }
    }

    /**
     * Enable k-fold spinner
     * @param e Event
     */
    public void jRadioButton5_actionPerformed(ActionEvent e) {
        if (jRadioButton5.isSelected()) {
            spinnerKFold.setEnabled(false);
        }
    }

    /**
     * Enable k-fold spinner
     * @param e Event
     */
    public void jRadioButton6_actionPerformed(ActionEvent e) {
        if (jRadioButton6.isSelected()) {
            spinnerKFold.setEnabled(false);
        }
    }

    /**
     * Gets number of folds
     * @return Number of folds
     */
    public int getValueKFoldCross() {
        return (Integer) spinnerKFold.getValue();
    }

}

class SelectExp_jRadioButton3_actionAdapter
        implements ActionListener {

    private SelectExp adaptee;

    SelectExp_jRadioButton3_actionAdapter(SelectExp adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jRadioButton3_actionPerformed(e);
    }
}

class SelectExp_jRadioButton2_actionAdapter
        implements ActionListener {

    private SelectExp adaptee;

    SelectExp_jRadioButton2_actionAdapter(SelectExp adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jRadioButton2_actionPerformed(e);
    }
}

class SelectExp_jRadioButton1_actionAdapter
        implements ActionListener {

    private SelectExp adaptee;

    SelectExp_jRadioButton1_actionAdapter(SelectExp adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jRadioButton1_actionPerformed(e);
    }
}

//Nuevas funcionalidades
class SelectExp_jRadioButton4_actionAdapter implements ActionListener {

    private SelectExp adaptee;

    SelectExp_jRadioButton4_actionAdapter(SelectExp adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jRadioButton4_actionPerformed(e);
    }
}

class SelectExp_jRadioButton5_actionAdapter implements ActionListener {

    private SelectExp adaptee;

    SelectExp_jRadioButton5_actionAdapter(SelectExp adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jRadioButton5_actionPerformed(e);
    }
}

class SelectExp_jRadioButton6_actionAdapter implements ActionListener {

    private SelectExp adaptee;

    SelectExp_jRadioButton6_actionAdapter(SelectExp adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jRadioButton6_actionPerformed(e);
    }
}
//Nuevas funcionalidades
