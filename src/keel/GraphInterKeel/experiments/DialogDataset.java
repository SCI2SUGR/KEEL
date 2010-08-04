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
 * File: DialogDataset.java
 *
 * This Class manages dialog for modifiying data sets
 *
 * @author Written by Admin 4/8/2008
 * @author Modified by Juan Carlos Fernandez Caballero and Pedro Antonio Gutierrez (University of Córdoba) 7/07/2009
 * @version 1.0
 * @since JDK1.5
 */
package keel.GraphInterKeel.experiments;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Vector;
import java.io.File;
import java.util.Arrays;

import keel.GraphInterKeel.menu.Frame;

public class DialogDataset extends JDialog {

    private String currentPath = "";
    JPanel panel1 = new JPanel() {

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
        }
    };
    /***************************************************************
     ***************  EDUCATIONAL KEEL  ****************************
     **************************************************************/
    private Experiments experiment;
    /***************************************************************
     ***************  EDUCATIONAL KEEL  ***********+****************
     **************************************************************/
    JScrollPane jScrollPane2 = new JScrollPane();
    JButton accept = new JButton();
    JButton cancel = new JButton();
    JButton add = new JButton();
    JButton drop = new JButton();
    JButton dropAll = new JButton();
    JLabel label1 = new JLabel();
    JLabel label2 = new JLabel();
    JList jList2 = new JList();
    JLabel jLabel3 = new JLabel();
    JLabel jLabel4 = new JLabel();
    JTextField training = new JTextField();
    JTextField testing = new JTextField();
    JButton searchTra = new JButton();
    JButton searchTst = new JButton();
    SpinnerNumberModel model = new SpinnerNumberModel(10, 1, 1000, 1);
    JSpinner jSpinner1 = new JSpinner(model);
    ImageIcon image1 = new ImageIcon(this.getClass().getResource(
            "/keel/GraphInterKeel/resources/ico/experiments/anadir.gif"));
    ImageIcon image2 = new ImageIcon(this.getClass().getResource(
            "/keel/GraphInterKeel/resources/ico/experiments/browser.gif"));
    ImageIcon image3 = new ImageIcon(this.getClass().getResource(
            "/keel/GraphInterKeel/resources/ico/experiments/anadir2.gif"));
    Vector aList;
    DataSet data;
    Vector someLists;
    JButton add10 = new JButton();
    JButton add11 = new JButton();
    JLabel jLabel5 = new JLabel();
    int layer;

    /**
     * Builder
     *
     * @param frame Parent frame
     * @param title Title
     * @param modal Modal status
     * @param data Data set selected
     * @param layer Active layer
     */
    public DialogDataset(Experiments frame, String title, boolean modal,
            DataSet data, int layer) {
        super(frame, title, modal);
        experiment = frame;
        this.data = data;
        this.layer = layer;
        aList = new Vector();
        aList = (Vector) ((Vector) data.tableVector.elementAt(layer)).clone();
        someLists = new Vector();
        for (int i = 0; i < Layer.numLayers; i++) {
            someLists.addElement((Vector) ((Vector) data.tableVector.elementAt(i)).clone());
        }

        /***************************************************************
         ***************  EDUCATIONAL KEEL  *****************************
         **************************************************************/
        if (Frame.buttonPressed == 1) //Button Experiments pressed
        {
            experiment = frame;
        }
        /*************************************************************
         ***************  EDUCATIONAL KEEL  ****************************
         **************************************************************/
        try {
            initDialog();
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Default builder
     */
    public DialogDataset() {
        this(null, "", false, null, 0);
    }

    /**
     * Initialize
     * @throws java.lang.Exception
     */
    private void initDialog() throws Exception {
        panel1.setLayout(null);
        jScrollPane2.getViewport().setBackground(new Color(225, 225, 225));
        jScrollPane2.setFont(new java.awt.Font("Arial", 0, 11));
        jScrollPane2.setBounds(new Rectangle(30, 241, 401, 170));
        accept.setBackground(new Color(225, 225, 225));
        accept.setBounds(new Rectangle(91, 481, 100, 30));
        accept.setFont(new java.awt.Font("Arial", 0, 11));
        accept.setOpaque(false);
        accept.setToolTipText("Apply changes");
        accept.setMnemonic('A');
        accept.setText("Apply");
        accept.addActionListener(new DialogDataset_aceptar_actionAdapter(this));
        cancel.setBackground(new Color(225, 225, 225));
        cancel.setBounds(new Rectangle(255, 481, 100, 30));
        cancel.setFont(new java.awt.Font("Arial", 0, 11));
        cancel.setOpaque(false);
        cancel.setToolTipText("Don\'t apply the changes");
        cancel.setMnemonic('C');
        cancel.setText("Cancel");
        cancel.addActionListener(new DialogDataset_cancelar_actionAdapter(this));
        add.setBackground(new Color(225, 225, 225));
        add.setBounds(new Rectangle(178, 142, 118, 30));
        add.setFont(new java.awt.Font("Arial", 0, 11));
        add.setOpaque(false);
        add.setToolTipText("Add selected files");
        add.setIcon(image1);
        add.setText("Add Selection");
        add.addActionListener(new DialogDataset_anadir_actionAdapter(this));
        drop.setBackground(new Color(225, 225, 225));
        drop.setBounds(new Rectangle(91, 439, 100, 30));
        drop.setFont(new java.awt.Font("Arial", 0, 11));
        drop.setOpaque(false);
        drop.setToolTipText("Remove selected items");
        drop.setText("Remove");
        drop.addActionListener(new DialogDataset_quitar_actionAdapter(this));
        dropAll.setBackground(new Color(225, 225, 225));
        dropAll.setBounds(new Rectangle(255, 439, 100, 30));
        dropAll.setFont(new java.awt.Font("Arial", 0, 11));
        dropAll.setOpaque(false);
        dropAll.setToolTipText("Remove all items");
        dropAll.setText("Remove All");
        dropAll.addActionListener(new DialogDataset_quitar_todo_actionAdapter(this));
        label1.setBackground(new Color(225, 225, 225));
        label1.setFont(new java.awt.Font("Arial", 1, 11));
        label1.setText("Selected DataSets:");
        label1.setBounds(new Rectangle(30, 216, 116, 22));
        label2.setBackground(new Color(225, 225, 225));
        label2.setFont(new java.awt.Font("Arial", 1, 11));
        label2.setText("DataSets Selection:");
        label2.setBounds(new Rectangle(30, 24, 119, 20));
        this.addWindowListener(new DialogDataset_this_windowAdapter(this));
        jLabel3.setBackground(new Color(225, 225, 225));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11));
        jLabel3.setText("Training file :");
        jLabel3.setBounds(new Rectangle(30, 75, 68, 23));
        jLabel4.setBackground(new Color(225, 225, 225));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11));
        jLabel4.setText("Test file :");
        jLabel4.setBounds(new Rectangle(30, 113, 52, 15));
        training.setFont(new java.awt.Font("Arial", 0, 11));
        training.setText("");
        training.setBounds(new Rectangle(99, 72, 248, 21));
        testing.setFont(new java.awt.Font("Arial", 0, 11));
        testing.setText("");
        testing.setBounds(new Rectangle(99, 110, 248, 21));
        searchTra.setBackground(new Color(225, 225, 225));
        searchTra.setBounds(new Rectangle(388, 72, 33, 20));
        searchTra.setFont(new java.awt.Font("Arial", 0, 11));
        searchTra.setOpaque(false);
        searchTra.setToolTipText("Browse for file");
        searchTra.setIcon(image2);
        searchTra.setText("");
        searchTra.addActionListener(new DialogDataset_busca_tra_actionAdapter(this));
        searchTst.setBackground(new Color(225, 225, 225));
        searchTst.setBounds(new Rectangle(388, 111, 33, 20));
        searchTst.setFont(new java.awt.Font("Arial", 0, 11));
        searchTst.setOpaque(false);
        searchTst.setToolTipText("Browse for file");
        searchTst.setIcon(image2);
        searchTst.setText("");
        searchTst.addActionListener(new DialogDataset_busca_tst_actionAdapter(this));
        add10.setBackground(new Color(225, 225, 225));
        add10.setBounds(new Rectangle(92, 188, 136, 30));
        add10.setFont(new java.awt.Font("Arial", 0, 11));
        add10.setOpaque(false);
        add10.setToolTipText("Add all k-fold cv files");
        add10.setVerifyInputWhenFocusTarget(true);
        add10.setIcon(image3);
        add10.setText("Add k-fold cv");
        add10.addActionListener(new DialogDataset_anadir10_actionAdapter(this));
        add11.setText("Add K-fold cv All Layers");
        add11.addActionListener(new DialogDataset_anadir11_actionAdapter(this));
        add11.setIcon(image3);
        add11.setToolTipText("Add all k-fold cv files");
        add11.setBackground(new Color(225, 225, 225));
        add11.setBounds(new Rectangle(245, 188, 170, 30));
        add11.setFont(new java.awt.Font("Arial", 0, 11));
        add11.setOpaque(false);
        jSpinner1.setBackground(new Color(225, 225, 225));
        jSpinner1.setFont(new java.awt.Font("Arial", 0, 11));
        jSpinner1.setForeground(new Color(225, 225, 225));
        jSpinner1.setBounds(new Rectangle(30, 188, 50, 30));
        jLabel5.setBackground(new Color(225, 225, 225));
        jLabel5.setEnabled(false);
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11));
        jLabel5.setForeground(Color.red);
        jLabel5.setText("All Layers");
        jLabel5.setBounds(new Rectangle(143, 219, 107, 18));
        panel1.setBackground(new Color(225, 225, 225));
        panel1.setFont(new java.awt.Font("Arial", 0, 11));
        panel1.setAlignmentY((float) 0.5);
        panel1.setOpaque(false);
        this.getContentPane().setBackground(new Color(225, 225, 225));
        this.setFont(new java.awt.Font("Arial", 0, 11));
        jList2.setFont(new java.awt.Font("Arial", 0, 11));
        getContentPane().add(panel1);
        panel1.add(label2, null);
        panel1.add(jLabel3, null);
        panel1.add(jLabel4, null);
        panel1.add(training, null);
        panel1.add(testing, null);
        panel1.add(searchTra, null);
        panel1.add(searchTst, null);
        panel1.add(jScrollPane2, null);
        panel1.add(drop, null);
        panel1.add(dropAll, null);
        panel1.add(accept, null);
        panel1.add(cancel, null);
        panel1.add(label1, null);
        panel1.add(jLabel5, null);
        panel1.add(jSpinner1, null);
        //panel1.add(add11, null); //Julian **- Term Layers is not used any more in KEEL
        panel1.add(add10, null);
        panel1.add(add, null);
        jScrollPane2.getViewport().add(jList2, null);

        // load selected files
        jList2.setListData(aList.toArray());
    }

    /**
     * Add button
     * @param e Event
     */
    void anadir_actionPerformed(ActionEvent e) {
        // add pair of files to the list
        if ((training.getText().length() != 0) && (testing.getText().length() != 0)) {
            File f1 = new File(training.getText());
            File f2 = new File(testing.getText());
            String pareja = f1.getName() + "," + f2.getName();

            if (!aList.contains(pareja)) {
                aList.add(pareja);
                Object[] l = aList.toArray();
                Arrays.sort(l);
                jList2.setListData(l);
                jLabel5.setEnabled(false);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Files must be added in pairs",
                    "Error", 2);
        }
    }

    /**
     * Drop button
     * @param e Event
     */
    void quitar_actionPerformed(ActionEvent e) {
        // remove selected items
        Object[] sel = jList2.getSelectedValues();
        for (int i = 0; i < sel.length; i++) {
            aList.remove(sel[i]);
        }
        if (sel.length > 0) {
            Object[] l = aList.toArray();
            Arrays.sort(l);
            jList2.setListData(l);
        }
    }

    /**
     * Drop all button
     * @param e Event
     */
    void quitar_todo_actionPerformed(ActionEvent e) {
        // remove all items
        jList2.setSelectionInterval(0, aList.size() - 1);
        Object[] sel = jList2.getSelectedValues();
        for (int i = 0; i < sel.length; i++) {
            aList.remove(sel[i]);
        }
        if (sel.length > 0) {
            Object[] l = aList.toArray();
            Arrays.sort(l);
            jList2.setListData(l);
        }
        jLabel5.setEnabled(false);
    }

    /**
     * Cancel button
     * @param e Event
     */
    void cancelar_actionPerformed(ActionEvent e) {
        // don't save changes
        this.setVisible(false);
    }

    /***************************************************************
     ***************  EDUCATIONAL KEEL  ****************************
     **************************************************************/
    /**
     * Accept button
     * @param e
     */
    void aceptar_actionPerformed(ActionEvent e) {
        if (Frame.buttonPressed == 0) //Button Experiments pressed
        {
            // save selected files
            if (jLabel5.isEnabled()) {
                for (int i = 0; i < Layer.numLayers; i++) {
                    data.tableVector.setElementAt((Vector) (((Vector) someLists.elementAt(i)).clone()), i);
                }
            } else {
                data.tableVector.setElementAt((Vector) aList.clone(), layer);
            }
            //rplace the old node
            this.setVisible(false);

        } else //Button Teaching pressed
        {
            //Window of partitions is opened
            if (experiment.getExecDocentWindowState() == false) {
                Object[] options = {"OK", "CANCEL"};
                int n = JOptionPane.showOptionDialog(this, "The actual experiment is configured with others dataset. \n" +
                        "OK presses to STOP experiment and resume witch new configuration. \n", "Warning!",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                        null, options, options[0]);
                if (n == JOptionPane.YES_OPTION) {
                    //save selected files
                    if (jLabel5.isEnabled()) {
                        for (int i = 0; i < Layer.numLayers; i++) {
                            data.tableVector.setElementAt((Vector) (((Vector) someLists.elementAt(i)).clone()), i);
                        }
                    } else {
                        data.tableVector.setElementAt((Vector) aList.clone(), layer);
                    }

                    this.setVisible(false);
                    //To close window of partitions and method "generarDirectorios" is invoqued
                    experiment.deleteExecDocentWindow();
                    experiment.closedEducationalExec(null);
                //experiment.ejecutar_actionPerformed(null);
                } else //cancel
                {
                    cancelar_actionPerformed(null);
                }
            } else //Window of partitions is opened
            {
                // save selected files
                if (jLabel5.isEnabled()) {
                    for (int i = 0; i < Layer.numLayers; i++) {
                        data.tableVector.setElementAt((Vector) (((Vector) someLists.elementAt(i)).clone()), i);
                    }
                } else {
                    data.tableVector.setElementAt((Vector) aList.clone(), layer);
                }

                this.setVisible(false);
            }
        }
    }

    /***************************************************************
     ***************  EDUCATIONAL KEEL  ****************************
     **************************************************************/
    /**
     * Closing window
     * @param e Event
     */
    void this_windowClosing(WindowEvent e) {
        // don't save changes
    }

    /**
     * Find training files
     * @param e Event
     */
    void busca_tra_actionPerformed(ActionEvent e) {

        JFileChooser f;

        // select training file
        f = new JFileChooser(currentPath);

        f.setDialogTitle("Training file");
        String exten[] = {
            "dat"};
        f.setFileFilter(new ArchiveFilter2(exten, "Data files (.dat)"));
        f.setCurrentDirectory(new File(data.dsc.getPath() + data.dsc.getName()));
        int opcion = f.showOpenDialog(this);
        if (opcion == JFileChooser.APPROVE_OPTION) {
            training.setText(f.getSelectedFile().getAbsolutePath());
            currentPath = f.getSelectedFile().getAbsolutePath();
        }
    }

    /**
     * Find test files
     * @param e Event
     */
    void busca_tst_actionPerformed(ActionEvent e) {
        // select test file
        JFileChooser f;

        f = new JFileChooser(currentPath);

        f.setDialogTitle("Test file");
        String exten[] = {
            "dat"};
        f.setFileFilter(new ArchiveFilter2(exten, "Data files (.dat)"));
        f.setCurrentDirectory(new File(data.dsc.getPath() + data.dsc.getName()));
        int opcion = f.showOpenDialog(this);
        if (opcion == JFileChooser.APPROVE_OPTION) {
            testing.setText(f.getSelectedFile().getAbsolutePath());
            currentPath = f.getSelectedFile().getAbsolutePath();
        }
    }

    /**
     * Adding 10-folds file
     * @param e Event
     */
    void anadir10_actionPerformed(ActionEvent e) {
        // add 10 fold cross validation files
        int prevLayer = Layer.layerActivo; //Julian** - store the current layer to restore it later
        //File dir = new File(data.dsc.getPath() + data.dsc.getNombre());
        int K = ((Integer) (jSpinner1.getValue())).intValue();
        try {
            Layer.layerActivo = this.layer; //so getNombre points to the actual dataset
            File dir = new File("." + data.dsc.getPath() + data.dsc.getName());
            //System.out.println(dir);
            String[] ficheros = dir.list();
            boolean cont = true, metido;

            for (int i = 1; i <= K; i++) {
                String pareja = "";
                metido = false;
                for (int j = 0; j < ficheros.length; j++) {
                    if (ficheros[j].indexOf(K + "-" + i + "tra.dat") != -1) {
                        pareja = ficheros[j] + ",";
                        metido = true;
                        break;
                    }
                }
                if (!metido) {
                    cont = false;
                }
                metido = false;
                for (int j = 0; j < ficheros.length; j++) {
                    if (ficheros[j].indexOf(K + "-" + i + "tst.dat") != -1) {
                        pareja += ficheros[j];
                        metido = true;
                        break;
                    }
                }
                if (!metido) {
                    cont = false;
                }
                aList.add(pareja);
            }
            if (!cont) {
                aList.clear();
                JOptionPane.showMessageDialog(this,
                        "Some files not found. Cancelling operation.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            jList2.setListData(aList.toArray());
            jLabel5.setEnabled(false);
        } catch (Exception ex) {
        }
        Layer.layerActivo = prevLayer; //restore the previous layer
    }

    /**
     * Adding 5-folds file
     * @param e Event
     */
    void anadir5_actionPerformed(ActionEvent e) {
        // add 5x2 cross validation files
        try {
            File dir = new File("." + data.dsc.getPath() + data.dsc.getName());
            String[] ficheros = dir.list();
            boolean cont = true, metido;

            for (int i = 1; i <= 5; i++) {
                String pareja = "";
                metido = false;
                for (int j = 0; j < ficheros.length; j++) {
                    if (ficheros[j].indexOf("5x2-" + i + "tra.dat") != -1) {
                        pareja = ficheros[j] + ",";
                        metido = true;
                        break;
                    }
                }
                if (!metido) {
                    cont = false;
                }
                metido = false;
                for (int j = 0; j < ficheros.length; j++) {
                    if (ficheros[j].indexOf("5x2-" + i + "tst.dat") != -1) {
                        pareja += ficheros[j];
                        metido = true;
                        break;
                    }
                }
                if (!metido) {
                    cont = false;
                }
                aList.add(pareja);


            }
            if (!cont) {
                aList.clear();
                JOptionPane.showMessageDialog(this,
                        "Some files not found. Cancelling operation.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            jList2.setListData(aList.toArray());
            jLabel5.setEnabled(false);
        } catch (Exception ex) {
        }
    }

    /**
     * Adding k-folds file
     * @param e Event
     */
    void anadir11_actionPerformed(ActionEvent e) {
        // add K fold cross validation files

        int K = ((Integer) (jSpinner1.getValue())).intValue();

        try {
            File dir = new File("." + data.dsc.getPath() + data.dsc.getName());
            String[] ficheros = dir.list();
            boolean cont = true, metido;

            for (int i = 1; i <= K; i++) {
                String pareja = "";
                for (int j = 0; j < ficheros.length; j++) {
                    if (ficheros[j].indexOf(K + "-" + i + "tra.dat") != -1) {
                        pareja = ficheros[j] + ",";
                        break;
                    }
                }
                for (int j = 0; j < ficheros.length; j++) {
                    if (ficheros[j].indexOf(K + "-" + i + "tst.dat") != -1) {
                        pareja += ficheros[j];
                        break;
                    }
                }
                aList.add(pareja);
            }

            // 	add 10 fold cross validation files for each layer
            for (int k = 0; k < Layer.numLayers; k++) {
//      dir = new File(data.dsc.getPath(k) + data.dsc.getNombre(k));

                dir = new File("." + data.dsc.getPath(k) + data.dsc.getName(k));
                ficheros = dir.list();

                for (int i = 1; i <= K; i++) {
                    String pareja = "";
                    metido = false;
                    for (int j = 0; j < ficheros.length; j++) {
                        if (ficheros[j].indexOf(K + "-" + i + "tra.dat") != -1) {
                            pareja = ficheros[j] + ",";
                            metido = true;
                            break;
                        }
                    }
                    if (!metido) {
                        cont = false;
                    }
                    metido = false;
                    for (int j = 0; j < ficheros.length; j++) {
                        if (ficheros[j].indexOf(K + "-" + i + "tst.dat") != -1) {
                            pareja += ficheros[j];
                            metido = true;
                            break;
                        }
                    }
                    if (!metido) {
                        cont = false;
                    }
                    ((Vector) (someLists.elementAt(k))).add(pareja);
                }
            }

            if (!cont) {
                aList.clear();
                for (int i = 0; i < someLists.size(); i++) {
                    ((Vector) (someLists.elementAt(i))).clear();
                }
                JOptionPane.showMessageDialog(this,
                        "Some files not found. Cancelling operation.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                jLabel5.setEnabled(false);
            } else {
                jLabel5.setEnabled(true);
            }
            jList2.setListData(aList.toArray());
        } catch (Exception ex) {
        }
    }

    /**
     * Adding 5X2-folds file
     * @param e Event
     */
    void anadir12_actionPerformed(ActionEvent e) {
        // add 5x2 cross validation files
        try {
            File dir = new File("." + data.dsc.getPath() + data.dsc.getName());
            String[] ficheros = dir.list();
            boolean cont = true, metido;

            for (int i = 1; i <= 5; i++) {
                String pareja = "";
                for (int j = 0; j < ficheros.length; j++) {
                    if (ficheros[j].indexOf("5x2-" + i + "tra.dat") != -1) {
                        pareja = ficheros[j] + ",";
                        break;
                    }
                }
                for (int j = 0; j < ficheros.length; j++) {
                    if (ficheros[j].indexOf("5x2-" + i + "tst.dat") != -1) {
                        pareja += ficheros[j];
                        break;
                    }
                }
                aList.add(pareja);


            }

            // add 5x2 cross validation files for each layer
            for (int k = 0; k < Layer.numLayers; k++) {
                dir = new File("." + data.dsc.getPath(k) + data.dsc.getName(k));
                ficheros = dir.list();

                for (int i = 1; i <= 5; i++) {
                    String pareja = "";
                    metido = false;
                    for (int j = 0; j < ficheros.length; j++) {
                        if (ficheros[j].indexOf("5x2-" + i + "tra.dat") != -1) {
                            pareja = ficheros[j] + ",";
                            metido = true;
                            break;
                        }
                    }
                    if (!metido) {
                        cont = false;
                    }
                    metido = false;
                    for (int j = 0; j < ficheros.length; j++) {
                        if (ficheros[j].indexOf("5x2-" + i + "tst.dat") != -1) {
                            pareja += ficheros[j];
                            metido = true;
                            break;
                        }
                    }
                    if (!metido) {
                        cont = false;
                    }
                    ((Vector) (someLists.elementAt(k))).add(pareja);

                }
            }
            if (!cont) {
                aList.clear();
                for (int i = 0; i < someLists.size(); i++) {
                    ((Vector) (someLists.elementAt(i))).clear();
                }
                JOptionPane.showMessageDialog(this,
                        "Some files not found. Cancelling operation.", "Error",
                        JOptionPane.ERROR_MESSAGE);

                jLabel5.setEnabled(false);
            } else {
                jLabel5.setEnabled(true);
            }
            jList2.setListData(aList.toArray());
        } catch (Exception ex) {
        }
    }
}

class DialogDataset_anadir_actionAdapter
        implements java.awt.event.ActionListener {

    DialogDataset adaptee;

    DialogDataset_anadir_actionAdapter(DialogDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.anadir_actionPerformed(e);
    }
}

class DialogDataset_quitar_actionAdapter
        implements java.awt.event.ActionListener {

    DialogDataset adaptee;

    DialogDataset_quitar_actionAdapter(DialogDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.quitar_actionPerformed(e);
    }
}

class DialogDataset_quitar_todo_actionAdapter
        implements java.awt.event.ActionListener {

    DialogDataset adaptee;

    DialogDataset_quitar_todo_actionAdapter(DialogDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.quitar_todo_actionPerformed(e);
    }
}

class DialogDataset_cancelar_actionAdapter
        implements java.awt.event.ActionListener {

    DialogDataset adaptee;

    DialogDataset_cancelar_actionAdapter(DialogDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.cancelar_actionPerformed(e);
    }
}

class DialogDataset_aceptar_actionAdapter
        implements java.awt.event.ActionListener {

    DialogDataset adaptee;

    DialogDataset_aceptar_actionAdapter(DialogDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.aceptar_actionPerformed(e);
    }
}

class DialogDataset_this_windowAdapter
        extends java.awt.event.WindowAdapter {

    DialogDataset adaptee;

    DialogDataset_this_windowAdapter(DialogDataset adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        adaptee.this_windowClosing(e);
    }
}

class DialogDataset_busca_tra_actionAdapter
        implements java.awt.event.ActionListener {

    DialogDataset adaptee;

    DialogDataset_busca_tra_actionAdapter(DialogDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.busca_tra_actionPerformed(e);
    }
}

class DialogDataset_busca_tst_actionAdapter
        implements java.awt.event.ActionListener {

    DialogDataset adaptee;

    DialogDataset_busca_tst_actionAdapter(DialogDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.busca_tst_actionPerformed(e);
    }
}

class DialogDataset_anadir10_actionAdapter
        implements java.awt.event.ActionListener {

    DialogDataset adaptee;

    DialogDataset_anadir10_actionAdapter(DialogDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.anadir10_actionPerformed(e);
    }
}

class DialogDataset_anadir11_actionAdapter implements java.awt.event.ActionListener {

    DialogDataset adaptee;

    DialogDataset_anadir11_actionAdapter(DialogDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.anadir11_actionPerformed(e);
    }
}
