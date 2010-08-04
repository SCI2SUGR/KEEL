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
 * File: DialogDataset2.java
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

public class DialogDataset2 extends JDialog {

    JPanel panel1 = new JPanel() {

        @Override
        public void paintComponent(Graphics g) {
            //ImageIcon img = new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/experimentos/datasets.jpg"));
            //g.drawImage(img.getImage(), 0, 0, null);
            super.paintComponent(g);
        }
    };
    JScrollPane jScrollPane2 = new JScrollPane();
    JButton accept = new JButton();
    JButton cancel = new JButton();
    JButton add = new JButton();
    JButton drop = new JButton();
    JButton dropAll = new JButton();
    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    JList jList2 = new JList();
    JLabel jLabel3 = new JLabel();
    JLabel jLabel4 = new JLabel();
    JTextField training = new JTextField();
    JTextField testing = new JTextField();
    JButton searchTra = new JButton();
    JButton searchTst = new JButton();
    SpinnerNumberModel model = new SpinnerNumberModel(10, 1, 1000, 10);
    ImageIcon image1 = new ImageIcon(this.getClass().getResource(
            "/keel/GraphInterKeel/resources/ico/experiments/anadir.gif"));
    ImageIcon image2 = new ImageIcon(this.getClass().getResource(
            "/keel/GraphInterKeel/resources/ico/experiments/browser.gif"));
    ImageIcon image3 = new ImageIcon(this.getClass().getResource(
            "/keel/GraphInterKeel/resources/ico/experiments/anadir2.gif"));
    Vector aList;
    DataSet data;
    Vector someLists;
    JButton add5 = new JButton();
    JButton add12 = new JButton();
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
    public DialogDataset2(Experiments frame, String title, boolean modal,
            DataSet data, int layer) {
        super(frame, title, modal);
        this.data = data;
        this.layer = layer;
        aList = new Vector();
        aList = (Vector) ((Vector) data.tableVector.elementAt(layer)).clone();
        someLists = new Vector();
        for (int i = 0; i < Layer.numLayers; i++) {
            someLists.addElement((Vector) ((Vector) data.tableVector.elementAt(i)).clone());
        }
        try {
            initialize2();
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Default builder
     */
    public DialogDataset2() {
        this(null, "", false, null, 0);
    }

    /**
     * Initialization
     * @throws java.lang.Exception
     */
    private void initialize2() throws Exception {
        panel1.setLayout(null);
        jScrollPane2.getViewport().setBackground(new Color(225, 225, 225));
        jScrollPane2.setFont(new java.awt.Font("Arial", 0, 11));
        jScrollPane2.setBounds(new Rectangle(30, 241, 401, 170));
        accept.setBackground(new Color(225, 225, 225));
        accept.setBounds(new Rectangle(91, 481, 100, 30));
        accept.setFont(new java.awt.Font("Arial", 0, 11));
        accept.setOpaque(false);
        accept.setToolTipText("Apply changes");
        accept.setFocusPainted(true);
//    accept.setIcon(image5);
        accept.setMnemonic('A');
        accept.setText("Apply");
        accept.addActionListener(new DialogDataset2_aceptar_actionAdapter(this));
        cancel.setBackground(new Color(225, 225, 225));
        cancel.setBounds(new Rectangle(255, 481, 100, 30));
        cancel.setFont(new java.awt.Font("Arial", 0, 11));
        cancel.setOpaque(false);
        cancel.setToolTipText("Don\'t apply the changes");
//    cancel.setIcon(image6);
        cancel.setMnemonic('C');
        cancel.setText("Cancel");
        cancel.addActionListener(new DialogDataset2_cancelar_actionAdapter(this));
        add.setBackground(new Color(225, 225, 225));
        add.setBounds(new Rectangle(138, 142, 118, 30));
        add.setFont(new java.awt.Font("Arial", 0, 11));
        add.setOpaque(false);
        add.setToolTipText("Add selected files");
        add.setIcon(image1);
        add.setText("Add Selection");
        add.addActionListener(new DialogDataset2_anadir_actionAdapter(this));
        drop.setBackground(new Color(225, 225, 225));
        drop.setBounds(new Rectangle(91, 439, 100, 30));
        drop.setFont(new java.awt.Font("Arial", 0, 11));
        drop.setOpaque(false);
        drop.setToolTipText("Remove selected items");
        drop.setText("Remove");
        drop.addActionListener(new DialogDataset2_quitar_actionAdapter(this));
        dropAll.setBackground(new Color(225, 225, 225));
        dropAll.setBounds(new Rectangle(255, 439, 100, 30));
        dropAll.setFont(new java.awt.Font("Arial", 0, 11));
        dropAll.setOpaque(false);
        dropAll.setToolTipText("Remove all items");
        dropAll.setText("Remove All");
        dropAll.addActionListener(new DialogDataset2_quitar_todo_actionAdapter(this));
        jLabel1.setBackground(new Color(225, 225, 225));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 11));
        jLabel1.setText("Selected DataSets:");
        jLabel1.setBounds(new Rectangle(30, 216, 116, 22));
        jLabel2.setBackground(new Color(225, 225, 225));
        jLabel2.setFont(new java.awt.Font("Arial", 1, 11));
        jLabel2.setText("DataSets Selection:");
        jLabel2.setBounds(new Rectangle(30, 24, 119, 20));
        this.addWindowListener(new DialogDataset2_this_windowAdapter(this));
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
        searchTra.addActionListener(new DialogDataset2_busca_tra_actionAdapter(this));
        searchTst.setBackground(new Color(225, 225, 225));
        searchTst.setBounds(new Rectangle(388, 111, 33, 20));
        searchTst.setFont(new java.awt.Font("Arial", 0, 11));
        searchTst.setOpaque(false);
        searchTst.setToolTipText("Browse for file");
        searchTst.setIcon(image2);
        searchTst.setText("");
        searchTst.addActionListener(new DialogDataset2_busca_tst_actionAdapter(this));
        add5.setBackground(new Color(225, 225, 225));
        add5.setBounds(new Rectangle(60, 186, 123, 30));
        add5.setFont(new java.awt.Font("Arial", 0, 11));
        add5.setOpaque(false);
        add5.setToolTipText("Add all 5x2 cv files");
        add5.setVerifyInputWhenFocusTarget(true);
        add5.setIcon(image3);
        add5.setText("Add 5x2 cv");
        add5.addActionListener(new DialogDataset2_anadir5_actionAdapter(this));
        add12.setText("Add 5x2 cv All Layers");
        add12.addActionListener(new DialogDataset2_anadir12_actionAdapter(this));
        add12.setIcon(image3);
        add12.setToolTipText("Add all 10-fold cv files");
        add12.setBackground(new Color(225, 225, 225));
        add12.setBounds(new Rectangle(207, 186, 192, 30));
        add12.setFont(new java.awt.Font("Arial", 0, 11));
        add12.setOpaque(false);
        jLabel5.setBackground(new Color(225, 225, 225));
        jLabel5.setEnabled(false);
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11));
        jLabel5.setForeground(Color.red);
        jLabel5.setText("All Layers");
        jLabel5.setBounds(new Rectangle(143, 219, 107, 18));

        panel1.setBackground(new Color(225, 225, 225));
        panel1.setFont(new java.awt.Font("Arial", 0, 11));
        panel1.setOpaque(false);
        this.getContentPane().setBackground(new Color(225, 225, 225));
        this.setFont(new java.awt.Font("Arial", 0, 11));
        jList2.setFont(new java.awt.Font("Arial", 0, 11));
        getContentPane().add(panel1);
        panel1.add(jLabel2, null);
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
        panel1.add(jLabel1, null);
        panel1.add(jLabel5, null);
        panel1.add(add5, null);
//    panel1.add(add12, null);//Julian**- There is no more layers in KEEL
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

    /**
     * Accept button
     * @param e
     */
    void aceptar_actionPerformed(ActionEvent e) {
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
        // select training file
        JFileChooser f = new JFileChooser();
        f.setDialogTitle("Training file");
        String exten[] = {
            "dat"};
        f.setFileFilter(new ArchiveFilter2(exten, "Data files (.dat)"));
        f.setCurrentDirectory(new File(data.dsc.getPath() + data.dsc.getName()));
        int opcion = f.showOpenDialog(this);
        if (opcion == JFileChooser.APPROVE_OPTION) {
            training.setText(f.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * Find test files
     * @param e Event
     */
    void busca_tst_actionPerformed(ActionEvent e) {
        // select test file
        JFileChooser f = new JFileChooser();
        f.setDialogTitle("Test file");
        String exten[] = {
            "dat"};
        f.setFileFilter(new ArchiveFilter2(exten, "Data files (.dat)"));
        f.setCurrentDirectory(new File(data.dsc.getPath() + data.dsc.getName()));
        int opcion = f.showOpenDialog(this);
        if (opcion == JFileChooser.APPROVE_OPTION) {
            testing.setText(f.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * Adding 10-folds file
     * @param e Event
     */
    void anadir10_actionPerformed(ActionEvent e) {
        // add 10 fold cross validation files
        File dir = new File(data.dsc.getPath() + data.dsc.getName());
        String[] ficheros = dir.list();
        boolean cont = true, metido;

        for (int i = 1; i <= 10; i++) {
            String pareja = "";
            metido = false;
            for (int j = 0; j < ficheros.length; j++) {
                if (ficheros[j].indexOf("10-" + i + "tra.dat") != -1) {
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
                if (ficheros[j].indexOf("10-" + i + "tst.dat") != -1) {
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
    }

    /**
     * Adding 5-folds file
     * @param e Event
     */
    void anadir5_actionPerformed(ActionEvent e) {
        // add 5x2 cross validation files
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
    }

    /**
     * Adding 5X2-folds file
     * @param e Event
     */
    void anadir12_actionPerformed(ActionEvent e) {
        // add 5x2 cross validation files
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
    }
}

class DialogDataset2_anadir_actionAdapter
        implements java.awt.event.ActionListener {

    DialogDataset2 adaptee;

    DialogDataset2_anadir_actionAdapter(DialogDataset2 adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.anadir_actionPerformed(e);
    }
}

class DialogDataset2_quitar_actionAdapter
        implements java.awt.event.ActionListener {

    DialogDataset2 adaptee;

    DialogDataset2_quitar_actionAdapter(DialogDataset2 adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.quitar_actionPerformed(e);
    }
}

class DialogDataset2_quitar_todo_actionAdapter
        implements java.awt.event.ActionListener {

    DialogDataset2 adaptee;

    DialogDataset2_quitar_todo_actionAdapter(DialogDataset2 adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.quitar_todo_actionPerformed(e);
    }
}

class DialogDataset2_cancelar_actionAdapter
        implements java.awt.event.ActionListener {

    DialogDataset2 adaptee;

    DialogDataset2_cancelar_actionAdapter(DialogDataset2 adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.cancelar_actionPerformed(e);
    }
}

class DialogDataset2_aceptar_actionAdapter
        implements java.awt.event.ActionListener {

    DialogDataset2 adaptee;

    DialogDataset2_aceptar_actionAdapter(DialogDataset2 adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.aceptar_actionPerformed(e);
    }
}

class DialogDataset2_this_windowAdapter
        extends java.awt.event.WindowAdapter {

    DialogDataset2 adaptee;

    DialogDataset2_this_windowAdapter(DialogDataset2 adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        adaptee.this_windowClosing(e);
    }
}

class DialogDataset2_busca_tra_actionAdapter
        implements java.awt.event.ActionListener {

    DialogDataset2 adaptee;

    DialogDataset2_busca_tra_actionAdapter(DialogDataset2 adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.busca_tra_actionPerformed(e);
    }
}

class DialogDataset2_busca_tst_actionAdapter
        implements java.awt.event.ActionListener {

    DialogDataset2 adaptee;

    DialogDataset2_busca_tst_actionAdapter(DialogDataset2 adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.busca_tst_actionPerformed(e);
    }
}

class DialogDataset2_anadir5_actionAdapter
        implements java.awt.event.ActionListener {

    DialogDataset2 adaptee;

    DialogDataset2_anadir5_actionAdapter(DialogDataset2 adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.anadir5_actionPerformed(e);
    }
}

class DialogDataset2_anadir12_actionAdapter implements java.awt.event.ActionListener {

    DialogDataset2 adaptee;

    DialogDataset2_anadir12_actionAdapter(DialogDataset2 adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.anadir12_actionPerformed(e);
    }
}
