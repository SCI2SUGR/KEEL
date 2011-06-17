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
 * <p>Description: DataSets selection</p>
 * @author Victor Manuel Gonzalez Quevedo
 * @author Ana Palacios Jimenez and Luciano Sanchez Ramos 23-4-2010 (University of Oviedo)
 * @version 2.0
 */
package keel.GraphInterKeel.experiments;

import java.awt.*;
import javax.swing.*;
import java.io.File;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import keel.GraphInterKeel.datacf.DataCFFrame;

public class DinamicDataset extends JPanel implements Scrollable {

    private int maxUnitIncrement = 10;
    private int pos = 10;
    Vector checks = new Vector();
    ;
    Vector checksLQD_C = new Vector();
    ;
    Vector checksC_LQD = new Vector();
    ;
    Vector checksC = new Vector();
    ;
    Vector datasetList = new Vector();
    Vector datasetXML = new Vector();
    Vector datasetListLQD_C = new Vector();
    Vector datasetXMLLQD_C = new Vector();
    Vector datasetListC_LQD = new Vector();
    Vector datasetXMLC_LQD = new Vector();
    Vector datasetListC = new Vector();
    Vector datasetXMLC = new Vector();
    Vector actualList;
    Vector actualListLQD_C;
    Vector actualListC_LQD;
    Vector actualListC;
    public Vector edits;
    Experiments parent;
    boolean activos[];
    boolean activosLQD_C[];
    boolean activosC_LQD[];
    boolean activosC[];
    JDialog dialogDinamic;
    JButton importB = new JButton();
    Hashtable<String, Boolean> dataActive = new Hashtable<String, Boolean>();
    JButton selectAll = new JButton();
    JButton selectAllLQD_C = new JButton();
    JButton selectAllC_LQD = new JButton();
    JButton selectAllC = new JButton();
    JButton invertSelection = new JButton();
    JButton invertSelectionLQD_C = new JButton();
    JButton invertSelectionC_LQD = new JButton();
    JButton invertSelectionC = new JButton();
    JButton selectAllUser = new JButton();
    JButton invertSelectionUser = new JButton();
    int datasetsNoUser;
    int datasetsUser;
    JButton crisp_lqd = new JButton();
    JButton lqd_crisp = new JButton();
    JButton crispb = new JButton();
    JPanel lqdcrisp = new JPanel();
    JPanel crisplqd = new JPanel();
    JPanel crisp = new JPanel();
    JPanel lqd = new JPanel();
    JLabel titulo3 = new JLabel("Keel Crisp Dataset");
    String cad;

    /**
     * Builder
     */
    public DinamicDataset() {
        super();
    }

    /**
     * Builder
     * @param newParent Parent frame
     */
    public DinamicDataset(Experiments newParent) {
        try {
            this.parent = newParent;
            initDinamicDataset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tranform data sets
     */
    private void transform() {

        lqdcrisp.removeAll();
        crisplqd.removeAll();
        crisp.removeAll();
        lqd.removeAll();
        lqd_crisp.setBackground(new Color(225, 225, 225));
        crisp_lqd.setBackground(new Color(225, 225, 225));
        crispb.setBackground(new Color(225, 225, 225));


    }

    private void initDinamicDataset() throws Exception {

        //importB.setBackground(new Color(0, 0, 0));
        importB.setText("Import");
        importB.addActionListener(new DinamicDataset_importar_actionAdapter(this));

        crisp_lqd.setBackground(new Color(225, 225, 225));
        crisp_lqd.setText("Crisp to Low Quality");
        crisp_lqd.addActionListener(new DinamicDataset_crisp_lqd_actionAdapter(this));
        crisp_lqd.setVisible(false);


        lqd_crisp.setBackground(new Color(225, 225, 225));
        lqd_crisp.setText("Low Quality to Crisp");
        lqd_crisp.addActionListener(new DinamicDataset_lqd_crisp_actionAdapter(this));
        lqd_crisp.setVisible(false);
        lqd_crisp.setEnabled(false);

        crispb.setBackground(new Color(225, 225, 225));
        crispb.setText("Keel Crisp Classification ");
        crispb.addActionListener(new DinamicDataset_crisp_actionAdapter(this));
        crispb.setVisible(true);
        crispb.setEnabled(false);

        selectAll.setBackground(new Color(225, 225, 225));
        selectAll.setText("Select All");
        selectAll.addActionListener(new DinamicDataset_selectAll_actionAdapter(this));

        invertSelection.setBackground(new Color(225, 225, 225));
        invertSelection.setText("Invert");
        invertSelection.addActionListener(new DinamicDataset_invertSelection_actionAdapter(this));

        selectAllUser.setBackground(new Color(225, 225, 225));
        selectAllUser.setText("Select All");
        selectAllUser.addActionListener(new DinamicDataset_selectAllUser_actionAdapter(this));

        invertSelectionUser.setBackground(new Color(225, 225, 225));
        invertSelectionUser.setText("Invert");
        invertSelectionUser.addActionListener(new DinamicDataset_invertSelectionUser_actionAdapter(this));


        selectAllLQD_C.setBackground(new Color(225, 225, 225));
        selectAllLQD_C.setText("Select All LQD_C");
        selectAllLQD_C.addActionListener(new DinamicDataset_selectAllLQD_C_actionAdapter(this));

        selectAllC_LQD.setBackground(new Color(225, 225, 225));
        selectAllC_LQD.setText("Select All C_LQD");
        selectAllC_LQD.addActionListener(new DinamicDataset_selectAllC_LQD_actionAdapter(this));

        selectAllC.setBackground(new Color(225, 225, 225));
        selectAllC.setText("Select All Crisp");
        selectAllC.addActionListener(new DinamicDataset_selectAllC_actionAdapter(this));


        invertSelectionLQD_C.setBackground(new Color(225, 225, 225));
        invertSelectionLQD_C.setText("Invert LQD_C");
        invertSelectionLQD_C.addActionListener(new DinamicDataset_invertSelectionLQD_C_actionAdapter(this));

        invertSelectionC_LQD.setBackground(new Color(225, 225, 225));
        invertSelectionC_LQD.setText("Invert C_LQD");
        invertSelectionC_LQD.addActionListener(new DinamicDataset_invertSelectionC_LQD_actionAdapter(this));

        invertSelectionC.setBackground(new Color(225, 225, 225));
        invertSelectionC.setText("Invert Crisp");
        invertSelectionC.addActionListener(new DinamicDataset_invertSelectionC_actionAdapter(this));

    }

    /**
     * Insert a new data set in the list, from an External Object Description
     * @param ds The external object description which contains the data set(s)
     * @param path the path to the  file(s) of this data set
     */
    public void insert(DatasetXML ds, String path) {
        // Inserts a dataset info
        datasetList.add(new ExternalObjectDescription(ds.nameAbr, path, 0));
        datasetXML.add(ds);
    }

    /**
     * Insert a new LQD data set in the list, from an External Object Description
     * @param ds The external object description which contains the data set(s)
     * @param path the path to the  file(s) of this data set
     */
    public void insertLQD_C(DatasetXML ds, String path) {
        // Inserts a dataset info
        datasetListLQD_C.add(new ExternalObjectDescription(ds.nameAbr, path, 0));
        datasetXMLLQD_C.add(ds);
    }

    /**
     * Insert a new LQD data set in the list, from an External Object Description
     * @param ds The external object description which contains the data set(s)
     * @param path the path to the  file(s) of this data set
     */
    public void insertC_LQD(DatasetXML ds, String path) {
        // Inserts a dataset info
        datasetListC_LQD.add(new ExternalObjectDescription(ds.nameAbr, path, 0));
        datasetXMLC_LQD.add(ds);
    }

    /**
     * Insert a new C data set in the list, from an External Object Description
     * @param ds The external object description which contains the data set(s)
     * @param path the path to the  file(s) of this data set
     */
    public void insertC(DatasetXML ds, String path) {
        // Inserts a dataset info
        datasetListC.add(new ExternalObjectDescription(ds.nameAbr, path, 0));
        datasetXMLC.add(ds);
    }

    /**
     * Clear the vectors which stores the list of data sets
     */
    public void removeAllData() {
        datasetList.removeAllElements();
        datasetXML.removeAllElements();
        datasetListLQD_C.removeAllElements();
        datasetXMLLQD_C.removeAllElements();
        datasetListC_LQD.removeAllElements();
        datasetXMLC_LQD.removeAllElements();
        datasetListC.removeAllElements();
        datasetXMLC.removeAllElements();

        transform();

    }

    /**
     * Checks if any of the data sets are selected in the list
     * @return If any of the data sets are selected in the list
     */
    public boolean isAnySelected() {
        // Check that one or more datasets are selected
        for (int i = 0; i < checks.size(); i++) {
            if (((JButton) checks.elementAt(i)).getText() == "Del") {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if any of the data sets are selected in the list
     * @return If any of the data sets are selected in the list
     */
    public boolean isAnySelectedLQD_C() {
        // Check that one or more datasets are selected
        for (int i = 0; i < checksLQD_C.size(); i++) {
            if (((JButton) checksLQD_C.elementAt(i)).getText() == "Del") {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if any of the data sets are selected in the list
     * @return If any of the data sets are selected in the list
     */
    public boolean isAnySelectedC_LQD() {
        // Check that one or more datasets are selected
        for (int i = 0; i < checksC_LQD.size(); i++) {
            if (((JButton) checksC_LQD.elementAt(i)).getText() == "Del") {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if any of the data sets are selected in the list
     * @return If any of the data sets are selected in the list
     */
    public boolean isAnySelectedC() {
        // Check that one or more datasets are selected
        for (int i = 0; i < checksC.size(); i++) {
            if (((JButton) checksC.elementAt(i)).getText() == "Del") {
                return true;
            }
        }
        return false;
    }

    /**
     * Method for saving the selected data sets so we can restore them later
     */
    public void saveSelected() {
        dataActive = new Hashtable<String, Boolean>();

        for (int i = 0; i < checks.size(); i++) {
            if (((JButton) checks.elementAt(i)).getText() == "Del") {
                dataActive.put(((DatasetXML) datasetXML.elementAt(i)).nameComplete, new Boolean(true));
            }
        }
    }

    /**
     * Clear all data estructures, and allocates new memory for them
     */
    public void clear() {

        checks = new Vector();
        datasetList = new Vector();
        actualList = new Vector();
        checksLQD_C = new Vector();
        datasetListLQD_C = new Vector();
        actualListLQD_C = new Vector();
        checksC_LQD = new Vector();
        datasetListC_LQD = new Vector();
        actualListC_LQD = new Vector();
        checksC = new Vector();
        datasetListC = new Vector();
        actualListC = new Vector();
        this.removeAll();
        transform();
    }

    /**
     * Reload the data set list, given a type of experiment
     * @param type The type of experiment
     */
    public void reload(int type) {
        pos = 20;
        this.removeAll();
        checks = new Vector();
        edits = new Vector();

        cad = "classification";
        switch (type) {
            case Experiments.CLASSIFICATION:
                cad = "classification";
                break;
            case Experiments.REGRESSION:
                cad = "regression";
                break;
            case Experiments.UNSUPERVISED:
                cad = "unsupervised";
                break;
        }

        actualList = new Vector();

        JLabel titulo1;
        if (parent.objType == parent.LQD) {
            titulo1 = new JLabel("KEEL Low Quality Datasets");
            lqd.setBackground(new Color(255, 198, 140));
            this.add(lqd);
            org.jdesktop.layout.GroupLayout lqdLayout = new org.jdesktop.layout.GroupLayout(lqd);
            lqd.setLayout(lqdLayout);
        } else {
            titulo1 = new JLabel("KEEL Datasets");
        }

        titulo1.setFont(new Font("Arial", Font.BOLD, 14));
        titulo1.setBounds(new Rectangle(10, 0, 200, 16));
        this.add(titulo1);

        datasetsNoUser = 0;
        for (int i = 0; i < datasetList.size(); i++) {
            File data = new File(((ExternalObjectDescription) datasetList.elementAt(i)).getPath(0));
            try {
                java.net.URL recursoInterno = data.toURL();
                File f = new File(recursoInterno.getFile());

                if (((DatasetXML) datasetXML.elementAt(i)).problemType.toLowerCase().compareTo(cad) == 0 && ((DatasetXML) datasetXML.elementAt(i)).user == false) {
                    JButton chk = new JButton();
                    chk.setBounds(new Rectangle(115, pos, 60, 20));
                    chk.setOpaque(true);
                    chk.setText("Add");
                    chk.setFont(new Font("Arial", Font.PLAIN, 8));
                    //if(parent.objType==parent.LQD)
                    //  chk.setForeground(Color.red);
                    chk.addActionListener(new DinamicDataset_Checks_actionAdapter(this));
                    if (parent.objType == parent.LQD) {
                        lqd.add(chk);
                    } else {
                        this.add(chk);
                    }
                    checks.add(chk);
                    if (parent.objType != parent.LQD) {
                        JButton edt = new JButton();
                        edt.setBounds(new Rectangle(180, pos, 60, 20));
                        edt.setOpaque(true);
                        edt.setText("Edit");
                        edt.setFont(new Font("Arial", Font.PLAIN, 8));
                        edt.addActionListener(new DinamicDataset_Edits_actionAdapter(this));
                        edits.add(edt);
                    }
                    String nameAbrv = "   " + ((DatasetXML) datasetXML.elementAt(i)).nameComplete;
                    if (nameAbrv.length() > 20) {
                        nameAbrv = nameAbrv.substring(0, 20) + "...";
                    }
                    JLabel txt = new JLabel(nameAbrv);
                    txt.setBounds(new Rectangle(0, pos, 200, 20));
                    if (parent.objType == parent.LQD) {
                        lqd.add(txt);
                    } else {
                        this.add(txt);
                    }
                    pos += 25;
                    actualList.add(datasetList.elementAt(i));
                    datasetsNoUser++;
                }
            } catch (java.net.MalformedURLException ex) {
            }
        }

        //Buttons to select all datasets and invert selection

        pos += 10;

        selectAll.setBounds(new Rectangle(15, pos, 110, 20));
        invertSelection.setBounds(new Rectangle(130, pos, 110, 20));
        if (parent.objType == parent.LQD) {
            lqd.add(selectAll);
            lqd.add(invertSelection);
        } else {
            this.add(selectAll);
            this.add(invertSelection);
        }

        pos += 30;

        /*
         * USER DATASETS
         */

        JLabel titulo2;
        titulo3.setVisible(false);

        if (parent.objType != parent.LQD) {
            titulo2 = new JLabel("User Datasets");
        } else {
            titulo2 = new JLabel("Transforms of Datasets");
            titulo3.setFont(new Font("Arial", Font.BOLD, 14));
            this.add(titulo3);
            titulo3.setVisible(true);
            lqd.setBounds(10, 25, 280, pos);
            pos = pos + 35;
        }

        titulo2.setFont(new Font("Arial", Font.BOLD, 14));
        titulo2.setBounds(new Rectangle(10, pos + 5, 200, 16));
        this.add(titulo2);

        pos += 25;

        datasetsUser = 0;
        for (int i = 0; i < datasetList.size(); i++) {
            File data = new File(((ExternalObjectDescription) datasetList.elementAt(i)).getPath(0));
            try {
                java.net.URL recursoInterno = data.toURL();
                File f = new File(recursoInterno.getFile());

                if (((DatasetXML) datasetXML.elementAt(i)).problemType.toLowerCase().compareTo(cad) == 0 && ((DatasetXML) datasetXML.elementAt(i)).user == true) {
                    JButton chk = new JButton();
                    chk.setBounds(new Rectangle(115, pos, 60, 20));
                    chk.setOpaque(true);
                    chk.setText("Add");
                    chk.setFont(new Font("Arial", Font.PLAIN, 8));
                    chk.addActionListener(new DinamicDataset_Checks_actionAdapter(this));
                    this.add(chk);
                    checks.add(chk);
                    JButton edt = new JButton();
                    edt.setBounds(new Rectangle(180, pos, 60, 20));
                    edt.setOpaque(true);
                    edt.setText("Edit");
                    edt.setFont(new Font("Arial", Font.PLAIN, 8));
                    edt.addActionListener(new DinamicDataset_Edits_actionAdapter(this));
                    edits.add(edt);
                    String nameAbrv = "   " + ((DatasetXML) datasetXML.elementAt(i)).nameComplete;
                    if (nameAbrv.length() > 20) {
                        nameAbrv = nameAbrv.substring(0, 20) + "...";
                    }
                    JLabel txt = new JLabel(nameAbrv);
                    txt.setBounds(new Rectangle(0, pos, 200, 20));
                    this.add(txt);
                    pos += 25;
                    actualList.add(datasetList.elementAt(i));

                    datasetsUser++;
                }

            } catch (java.net.MalformedURLException ex) {
            }
        }
        activos = new boolean[checks.size()];

        //Buttons to select all datasets and invert selection

        pos += 10;

        selectAllUser.setBounds(new Rectangle(15, pos, 110, 20));
        this.add(selectAllUser);

        invertSelectionUser.setBounds(new Rectangle(130, pos, 110, 20));
        this.add(invertSelectionUser);

        pos += 30;

        importB.setBounds(new Rectangle(85, pos, 90, 20));

        crisp_lqd.setBounds(new Rectangle(10, (pos - 30), 128, 20));
        lqd_crisp.setBounds(new Rectangle(10 + 139, (pos - 30), 128, 20));


        this.add(importB);
        if (parent.objType == parent.LQD) {
            this.add(crisp_lqd);
            this.add(lqd_crisp);
            this.add(crispb);
        }

        pos += 20;

        parent.dinDatasetsScrollPane.getViewport().setBackground(this.getBackground());

        this.setPreferredSize(new Dimension(280, pos+20));

        this.repaint();

    }

    /**
     * Reload LQD data sets
     */
    public void reload_lqd_crisp() {
        //  JOptionPane.showMessageDialog(this, "Nentraaaaaaaaaaaaa 1",
        //            "Invalid number of folds", JOptionPane.ERROR_MESSAGE);
        //We obtain the lqd datasets 
        try {
            String raiz = "." + File.separatorChar + "data" + File.separatorChar + "DatasetsLQD.xml";
            File dataset = new File(raiz);
            java.net.URL recursoInternocrisp = dataset.toURL();
            //System.out.println(recursoInterno);

            if (recursoInternocrisp == null) {
                System.err.println("Datasets.xml file not found at resources directory");
            } else {
                // SGL - Loading of an internal resource list method file in XML Format
                Document doc = new Document();
                try {
                    SAXBuilder builder = new SAXBuilder();
                    doc = builder.build(recursoInternocrisp);
                } catch (JDOMException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Dataset specification XML file not found");
                }

                List datasets = doc.getRootElement().getChildren();
                parent.listDataLQD_C = new DatasetXML[datasets.size()];
                for (int i = 0; i < datasets.size(); i++) {
                    parent.listDataLQD_C[i] = new DatasetXML((Element) datasets.get(i));
                    insertLQD_C(parent.listDataLQD_C[i], "/data/");
                }

                sortDatasetLQD_C();
            }
        } catch (java.net.MalformedURLException ex) {
            ex.printStackTrace();
        }


        lqdcrisp.setBackground(new Color(255, 253, 202));
        lqdcrisp.removeAll();
        lqdcrisp.setVisible(false);

        org.jdesktop.layout.GroupLayout lqdcrispLayout = new org.jdesktop.layout.GroupLayout(lqdcrisp);
        lqdcrisp.setLayout(lqdcrispLayout);

        //lqd_crisp.setBackground(Color.yellow);

        int situation = 10;
        actualListLQD_C = new Vector();
        checksLQD_C = new Vector();


        for (int i = 0; i < datasetListLQD_C.size(); i++) {
            File data = new File(((ExternalObjectDescription) datasetListLQD_C.elementAt(i)).getPath(0));
            try {
                java.net.URL recursoInterno = data.toURL();
                File f = new File(recursoInterno.getFile());

                if (((DatasetXML) datasetXMLLQD_C.elementAt(i)).problemType.toLowerCase().compareTo(cad) == 0 && ((DatasetXML) datasetXMLLQD_C.elementAt(i)).user == false) {
                    JButton chk = new JButton();
                    chk.setBounds(new Rectangle(115, situation, 60, 20));
                    chk.setOpaque(true);
                    chk.setText("Add");
                    chk.setFont(new Font("Arial", Font.PLAIN, 8));
                    //chk.setForeground(Color.red);
                    chk.addActionListener(new DinamicDataset_ChecksLQD_C_actionAdapter(this));
                    lqdcrisp.add(chk);
                    checksLQD_C.add(chk);

                    String nameAbrv = "   " + ((DatasetXML) datasetXMLLQD_C.elementAt(i)).nameComplete;
                    if (nameAbrv.length() > 20) {
                        nameAbrv = nameAbrv.substring(0, 20) + "...";
                    }
                    JLabel txt = new JLabel(nameAbrv);
                    txt.setBounds(new Rectangle(0, situation, 200, 20));
                    lqdcrisp.add(txt);
                    situation += 25;
                    actualListLQD_C.add(datasetListLQD_C.elementAt(i));

                }
            } catch (java.net.MalformedURLException ex) {
            }
        }

        //Buttons to select all datasets and invert selection

        situation += 15;

        activosLQD_C = new boolean[checksLQD_C.size()];
        selectAllLQD_C.setBounds(new Rectangle(10, situation, 115, 20));
        //        selectAll.setFont(new Font("Arial", Font.PLAIN, 10));
        lqdcrisp.add(selectAllLQD_C);
        invertSelectionLQD_C.setBounds(new Rectangle(135, situation, 110, 20));
        //        invertSelection.setFont(new Font("Arial", Font.PLAIN, 10));
        lqdcrisp.add(invertSelectionLQD_C);
        situation += 30;

        this.add(lqdcrisp);
        lqdcrisp.setBounds(15, pos - 5, 330, situation);

    }

    /**
     * Reload LQD data sets
     */
    public int reload_crisp_lqd() {
        //  JOptionPane.showMessageDialog(this, "Nentraaaaaaaaaaaaa 1",
        //            "Invalid number of folds", JOptionPane.ERROR_MESSAGE);
        //We obtain the lqd datasets 
        try {
            String raiz = "." + File.separatorChar + "data" + File.separatorChar + "C_LQD" + File.separatorChar + "DatasetsC_LQD.xml";
            File dataset = new File(raiz);
            java.net.URL recursoInternocrisp = dataset.toURL();
            //System.out.println(recursoInterno);

            if (recursoInternocrisp == null) {
                System.err.println("Datasets.xml file not found at resources directory");
            } else {
                // SGL - Loading of an internal resource list method file in XML Format
                Document doc = new Document();
                try {
                    SAXBuilder builder = new SAXBuilder();
                    doc = builder.build(recursoInternocrisp);
                } catch (JDOMException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Dataset specification XML file not found");
                }

                List datasets = doc.getRootElement().getChildren();
                parent.listDataC_LQD = new DatasetXML[datasets.size()];
                for (int i = 0; i < datasets.size(); i++) {
                    parent.listDataC_LQD[i] = new DatasetXML((Element) datasets.get(i));
                    insertC_LQD(parent.listDataC_LQD[i], "/data/");
                }

                sortDatasetC_LQD();
            }
        } catch (java.net.MalformedURLException ex) {
            ex.printStackTrace();
        }

        crisplqd.setBackground(new Color(201, 216, 237));
        crisplqd.removeAll();
        crisplqd.setVisible(false);

        org.jdesktop.layout.GroupLayout crisplqdLayout = new org.jdesktop.layout.GroupLayout(crisplqd);
        crisplqd.setLayout(crisplqdLayout);

        // crisp_lqd.setBackground(Color.blue);

        int situation = 10;
        actualListC_LQD = new Vector();
        checksC_LQD = new Vector();


        for (int i = 0; i < datasetListC_LQD.size(); i++) {
            File data = new File(((ExternalObjectDescription) datasetListC_LQD.elementAt(i)).getPath(0));
            try {
                java.net.URL recursoInterno = data.toURL();
                File f = new File(recursoInterno.getFile());

                if (((DatasetXML) datasetXMLC_LQD.elementAt(i)).problemType.toLowerCase().compareTo(cad) == 0 && ((DatasetXML) datasetXMLC_LQD.elementAt(i)).user == false) {
                    JButton chk = new JButton();
                    chk.setBounds(new Rectangle(155, situation, 60, 20));
                    chk.setOpaque(true);
                    chk.setText("Add");
                    chk.setFont(new Font("Arial", Font.PLAIN, 8));
                    //chk.setForeground(Color.red);
                    chk.addActionListener(new DinamicDataset_ChecksC_LQD_actionAdapter(this));
                    crisplqd.add(chk);
                    checksC_LQD.add(chk);

                    String nameAbrv = "   " + ((DatasetXML) datasetXMLC_LQD.elementAt(i)).nameComplete;
                    if (nameAbrv.length() > 20) {
                        nameAbrv = nameAbrv.substring(0, 20) + "...";
                    }
                    JLabel txt = new JLabel(nameAbrv);
                    txt.setBounds(new Rectangle(0, situation, 200, 20));
                    crisplqd.add(txt);
                    situation += 25;
                    actualListC_LQD.add(datasetListC_LQD.elementAt(i));

                }
            } catch (java.net.MalformedURLException ex) {
            }
        }

        //Buttons to select all datasets and invert selection

        situation += 15;

        activosC_LQD = new boolean[checksC_LQD.size()];
        selectAllC_LQD.setBounds(new Rectangle(10, situation, 115, 20));
        //        selectAll.setFont(new Font("Arial", Font.PLAIN, 10));
        crisplqd.add(selectAllC_LQD);
        invertSelectionC_LQD.setBounds(new Rectangle(135, situation, 110, 20));
        //        invertSelection.setFont(new Font("Arial", Font.PLAIN, 10));
        crisplqd.add(invertSelectionC_LQD);
        situation += 30;


        this.add(crisplqd);
        crisplqd.setBounds(15, pos - 5, 330, situation);

        return situation + pos;
    }

    /**
     * Reload LQD data sets
     *
     * @param position Position in the list
     */
    public void reload_crisp(int position) {
        //  JOptionPane.showMessageDialog(this, "Nentraaaaaaaaaaaaa 1",
        //            "Invalid number of folds", JOptionPane.ERROR_MESSAGE);
        //We obtain the lqd datasets 
        try {
            String raiz = "." + File.separatorChar + "data" + File.separatorChar + "Datasets.xml";
            File dataset = new File(raiz);
            java.net.URL recursoInternocrisp = dataset.toURL();
            //System.out.println(recursoInterno);

            if (recursoInternocrisp == null) {
                System.err.println("Datasets.xml file not found at resources directory");
            } else {
                // SGL - Loading of an internal resource list method file in XML Format
                Document doc = new Document();
                try {
                    SAXBuilder builder = new SAXBuilder();
                    doc = builder.build(recursoInternocrisp);
                } catch (JDOMException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Dataset specification XML file not found");
                }

                List datasets = doc.getRootElement().getChildren();
                parent.listDataC = new DatasetXML[datasets.size()];
                for (int i = 0; i < datasets.size(); i++) {
                    parent.listDataC[i] = new DatasetXML((Element) datasets.get(i));
                    insertC(parent.listDataC[i], "/data/");
                }

                sortDatasetC();
            }
        } catch (java.net.MalformedURLException ex) {
            ex.printStackTrace();
        }

        crisp.setBackground(new Color(204, 204, 204));
        crisp.removeAll();
        crisp.setVisible(false);

        org.jdesktop.layout.GroupLayout crispLayout = new org.jdesktop.layout.GroupLayout(crisp);
        crisp.setLayout(crispLayout);

        //crispb.setBackground(Color.darkGray);

        //int situation=position+10;
        int situation = crisp_lqd.getY() + 35;
        actualListC = new Vector();
        checksC = new Vector();
        pos = 10;

        titulo3.setBounds(new Rectangle(10, situation, 200, 16));
        crispb.setBounds(new Rectangle(10, situation + 35, 150, 20));

        for (int i = 0; i < datasetListC.size(); i++) {
            File data = new File(((ExternalObjectDescription) datasetListC.elementAt(i)).getPath(0));
            try {
                java.net.URL recursoInterno = data.toURL();
                File f = new File(recursoInterno.getFile());

                if (((DatasetXML) datasetXMLC.elementAt(i)).problemType.toLowerCase().compareTo(cad) == 0 && ((DatasetXML) datasetXMLC.elementAt(i)).user == false) {
                    JButton chk = new JButton();
                    chk.setBounds(new Rectangle(115, pos, 60, 20));
                    chk.setOpaque(true);
                    chk.setText("Add");
                    chk.setFont(new Font("Arial", Font.PLAIN, 8));
                    //chk.setForeground(Color.red);
                    chk.addActionListener(new DinamicDataset_ChecksC_actionAdapter(this));
                    crisp.add(chk);
                    checksC.add(chk);

                    String nameAbrv = "   " + ((DatasetXML) datasetXMLC.elementAt(i)).nameComplete;
                    if (nameAbrv.length() > 20) {
                        nameAbrv = nameAbrv.substring(0, 20) + "...";
                    }
                    JLabel txt = new JLabel(nameAbrv);
                    txt.setBounds(new Rectangle(0, pos, 200, 20));
                    crisp.add(txt);
                    pos = pos + 25;
                    actualListC.add(datasetListC.elementAt(i));

                }
            } catch (java.net.MalformedURLException ex) {
            }
        }

        //Buttons to select all datasets and invert selection

        pos = pos + 15;

        activosC = new boolean[checksC.size()];
        selectAllC.setBounds(new Rectangle(10, pos, 115, 20));
        //        selectAll.setFont(new Font("Arial", Font.PLAIN, 10));
        crisp.add(selectAllC);
        invertSelectionC.setBounds(new Rectangle(135, pos, 110, 20));
        //        invertSelection.setFont(new Font("Arial", Font.PLAIN, 10));
        crisp.add(invertSelectionC);

        this.add(crisp);
        crisp.setBounds(15, crispb.getY() + 35, 330, pos + 25);

    }

    /**
     * Once the Buttons has been loaded again, we must set their state
     * as it was previously set by the user <- we take the state saved from saveSelected()
     */
    public void reloadPreviousActiveDataSets() {
        Boolean isActive;
        int i;

        //System.out.println("  > Inici reloadPreviousActiveDatasets");

        //Initialize the state of the Dataset
        //((Nodo)parent.grafo.getNodos().elementAt(0)).updateState();

        for (int id = 0; id < checks.size(); id++) {
            isActive = dataActive.get(((DatasetXML) datasetXML.elementAt(id)).nameComplete);
            if (isActive == null) { //new data set
                ((JButton) (checks.elementAt(id))).setText("Add");
                ((JButton) (edits.elementAt(id))).setVisible(false);
                this.remove(((JButton) (edits.elementAt(id))));
            } else {
                if (!isActive) { //not selected before
                    ((JButton) (checks.elementAt(id))).setText("Add");
                    ((JButton) (edits.elementAt(id))).setVisible(false);
                    this.remove(((JButton) (edits.elementAt(id))));
                } else { //selected before
                    ((JButton) (checks.elementAt(id))).setText("Del");
                    ((JButton) (edits.elementAt(id))).setVisible(true);
                    this.add((JButton) (edits.elementAt(id)));
                }
            }
        }

        if (Layer.numLayers == 1) {
            for (i = 0; i < checks.size(); i++) {
                if (((JButton) checks.elementAt(i)).getText() == "Del") {
                    ((JButton) checks.elementAt(i)).setEnabled(false);
                }
            }
        } else {
            for (i = 0; i < checks.size(); i++) {
                ((JButton) checks.elementAt(i)).setEnabled(true);
            }
        }
    //System.out.println("  > End reloadPreviousActiveDatasets");
    }


    // scroll control
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation,
            int direction) {
        int currentPosition = 0;
        if (orientation == SwingConstants.HORIZONTAL) {
            currentPosition = visibleRect.x;
        } else {
            currentPosition = visibleRect.y;

        }
        if (direction < 0) {
            int newPosition = currentPosition -
                    (currentPosition / maxUnitIncrement) * maxUnitIncrement;
            return (newPosition == 0) ? maxUnitIncrement : newPosition;
        } else {
            return ((currentPosition / maxUnitIncrement) + 1) * maxUnitIncrement -
                    currentPosition;
        }
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation,
            int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            return visibleRect.width - maxUnitIncrement;
        } else {
            return visibleRect.height - maxUnitIncrement;
        }
    }

    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public void setMaxUnitIncrement(int pixels) {
        maxUnitIncrement = pixels;
    }

    /**
     * When the "Edit" button is pressed, this function is executed
     * @param e The event associated
     */
    void edits_actionPerformed(ActionEvent e) {
        Object s = e.getSource();
        int i, id = 0;
        DataSet dataset = new DataSet();
        int cont = -1;

        //search the editbutton clicked
        for (i = 0; i < edits.size(); i++) {
            if (s == edits.elementAt(i)) {
                id = i;
            }
        }
        //search the correct layer
        for (i = 0; i <= id; i++) {
            if (((JButton) checks.elementAt(i)).getText() == "Del") {
                cont++;
            }
        }

        for (i = 0; i < parent.experimentGraph.numNodes(); i++) {
            if (parent.experimentGraph.getNodeAt(i).getType() == Node.type_Dataset) {
                dataset = (DataSet) parent.experimentGraph.getNodeAt(i);
            }
        }

        if (parent.cvType == Experiments.P5X2) {
            dialogDinamic = new DialogDataset2(parent, "DataSet", true, dataset, cont);
        } else {
            dialogDinamic = new DialogDataset(parent, "DataSet", true, dataset, cont);
        //dialogDinamic.getContentPane().setBackground(new Color(225, 225, 225));
        }
        // Center dialog
        dialogDinamic.setSize(464, 580);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = dialogDinamic.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        dialogDinamic.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        dialogDinamic.setResizable(false);
        dialogDinamic.setVisible(true);

    }

    /**
     * Checking paths
     * @param origen
     */
    private void check_way(Node origen) {

        for (int i = 0; i < parent.graphDiagramINNER.mainGraph.numNodes(); i++) {
            if (parent.graphDiagramINNER.mainGraph.getNodeAt(i).type != Node.type_Dataset) {
                //check if contain the node origen
                for (int r = 0; r < parent.graphDiagramINNER.mainGraph.getNodeAt(i).dsc.arg.size(); r++) {
                    if (parent.graphDiagramINNER.mainGraph.getNodeAt(i).dsc.arg.get(r).before.id == origen.id) {
                        parent.graphDiagramINNER.mainGraph.getNodeAt(i).image = Toolkit.getDefaultToolkit().getImage(
                                this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/preprocessLQD_ast.gif"));
                        parent.graphDiagramINNER.mainGraph.getNodeAt(i).dsc.arg.get(r).before = origen;

                        parent.graphDiagramINNER.repaint();

                        //Check that all the datasets contained in the node also are contained in the origen
                        // (maybe origen have deleted someone)
                        for (int d_dest = 0; d_dest < parent.graphDiagramINNER.mainGraph.getNodeAt(i).dsc.arg.get(r).data_selected.size(); d_dest++) {
                            boolean contained = false;
                            for (int d = 0; d < origen.dsc.getNamesLength(); d++) {
                                if (parent.graphDiagramINNER.mainGraph.getNodeAt(i).dsc.arg.get(r).data_selected.get(d_dest).compareTo(origen.dsc.getName(d)) == 0) {
                                    contained = true;
                                    break;
                                }
                            }
                            if (contained == false)//we have to erase this datasets and its parameters
                            {
                                parent.graphDiagramINNER.mainGraph.getNodeAt(i).dsc.arg.get(r).data_selected.remove(d_dest);
                                parent.graphDiagramINNER.mainGraph.getNodeAt(i).dsc.arg.get(r).parameters.remove(d_dest);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Update data sets
     */
    public void update() {

        if (parent.objType == parent.LQD) {
            loadDatasetInfo(checks, actualList, activos, parent.dsc, Node.LQD);
        } else {
            loadDatasetInfo(checks, actualList, activos, parent.dsc, Node.CRISP);
        }
    }

    /**
     * Loaind information of data sets
     *
     * @param check Checks list
     * @param List List of data sets
     * @param actives Array of active data sets
     * @param exobj Parent experiment
     * @param node_type Type of node
     */
    private void loadDatasetInfo(Vector check, Vector List, boolean actives[], ExternalObjectDescription exobj,
            int node_type) {

        File dir;
        String[] ficheros;
        int i, c;
        ExternalObjectDescription dsc, dscal;
        String valores[];
        boolean actTemp[];
        int posAction = 0;
        boolean action = false;
        boolean missing = false;
        boolean found;
        Vector listas = new Vector();

        actTemp = new boolean[check.size()];

        for (i = 0, c = 0; i < check.size(); i++) {
            if (((JButton) check.elementAt(i)).getText() == "Del") {
                actTemp[i] = true;
                if (c == 0) {
                    // if(parent.objType==parent.LQD)
                    exobj = new ExternalObjectDescription((ExternalObjectDescription) List.elementAt(i), true);
                //else
                //    exobj = new ExternalObjectDescription((ExternalObjectDescription) List.elementAt(i));
                } else {
                    //if(parent.objType==parent.LQD)
                    exobj.insert(new ExternalObjectDescription((ExternalObjectDescription) List.elementAt(i), true));
                //else
                //   exobj.insert(new ExternalObjectDescription((ExternalObjectDescription) List.elementAt(i)));
                }
                c++;
            } else {
                actTemp[i] = false;
            }
        }

        dsc = new ExternalObjectDescription(exobj);

        valores = dsc.getAllNames();

        Layer.numLayers = valores.length;
        if (Layer.numLayers == 1) //This can not be erased
        {
            for (i = 0; i < check.size(); i++) {
                if (((JButton) check.elementAt(i)).getText() == "Del") {
                    ((JButton) check.elementAt(i)).setEnabled(false);
                }
            }
        } else {
            for (i = 0; i < check.size(); i++) {
                ((JButton) check.elementAt(i)).setEnabled(true);
            }
        }

        Layer.layerActivo = 0;
        //System.out.println("New data sets number = " + Layer.numLayers);

        for (i = 0; i < Layer.numLayers; i++) {
            listas.addElement(new Vector());
        }

        Vector save;
        //update the extenal object descriptions
        //for the data set added
        for (i = 0; i < parent.graphDiagramINNER.mainGraph.numNodes(); i++) {
            /*System.out.println("numero de nodos insertados en el grafico "+
            parent.graphDiagramINNER.mainGraph.numNodes());
            System.out.println("el type-lqd del nodo es "+
            parent.graphDiagramINNER.mainGraph.getNodeAt(i).type_lqd + " tipo de dataset 0 o algorimot pre 4 "+
            parent.graphDiagramINNER.mainGraph.getNodeAt(i).type+" y pasa el tipo "+node_type);*/
            if (parent.graphDiagramINNER.mainGraph.getNodeAt(i).type == Node.type_Dataset) {

                if (parent.graphDiagramINNER.mainGraph.getNodeAt(i).type_lqd == node_type) {
                    if (parent.objType == parent.LQD) {
                        parent.graphDiagramINNER.mainGraph.getNodeAt(i).dsc = new ExternalObjectDescription(dsc);
                        check_way(parent.graphDiagramINNER.mainGraph.getNodeAt(i));//check if this node is connect with other node
                    } else {
                        parent.graphDiagramINNER.mainGraph.getNodeAt(i).dsc = new ExternalObjectDescription(dsc);
                        save = (Vector) ((DataSet) parent.graphDiagramINNER.mainGraph.getNodeAt(i)).tableVector.clone();
                        ((DataSet) parent.graphDiagramINNER.mainGraph.getNodeAt(i)).tableVector = new Vector();

                        for (int j = 0; j < dsc.getNamesLength(); j++) {
                            ((DataSet) parent.graphDiagramINNER.mainGraph.getNodeAt(i)).tableVector.addElement(new Vector());
                        }


                        if (parent.cvType == Experiments.PK) {
                            //add 10 fold cross validation files for each layer  
                            for (int k = 0; k < Layer.numLayers; k++) {
                                try {
                                    dir = new File("." + dsc.getPath(k) + dsc.getName(k));
                                    ficheros = dir.list();

                                    for (int l = 1; l <= parent.numberKFoldCross; l++) {

                                        String pareja = "";

                                        found = false;
                                        for (int j = 0; j < ficheros.length && !found; j++) {
                                            if (ficheros[j].indexOf(parent.numberKFoldCross + "-" + l + "tra.dat") != -1) {
                                                pareja = ficheros[j] + ",";
                                                found = true;
                                            }
                                        }

                                        if (!found) {
                                            missing = true;
                                        }

                                        found = false;
                                        for (int j = 0; j < ficheros.length && !found; j++) {
                                            if (ficheros[j].indexOf(parent.numberKFoldCross + "-" + l + "tst.dat") != -1) {
                                                pareja += ficheros[j];
                                                found = true;
                                            }
                                        }

                                        if (!found || missing) {
                                            missing = true;
                                            ((DataSet) parent.graphDiagramINNER.mainGraph.getNodeAt(i)).addMissingPartition(pareja, k);
                                        } else {
                                            ((Vector) (listas.elementAt(k))).add(pareja);
                                        }
                                    }

                                } catch (Exception ex) {
                                }
                            }
                        } // if PK
                        //  if(parent.objType!=parent.LQD) //ESTO LUEGO LO TENGO QUE MIRAR PARA CONTROLAR
                        // {
                        else if (parent.cvType == Experiments.P5X2) {
                            // add 5x2 cross validation files for each layer
                            for (int k = 0; k < Layer.numLayers; k++) {
                                try {
                                    dir = new File("." + dsc.getPath(k) + dsc.getName(k));
                                    ficheros = dir.list();

                                    for (int l = 1; l <= 5; l++) {
                                        String pareja = "";
                                        String pareja2 = "";

                                        found = false;
                                        for (int j = 0; j < ficheros.length && !found; j++) {
                                            if (ficheros[j].indexOf("5x2-" + l + "tra.dat") != -1) {
                                                pareja = ficheros[j];
                                                found = true;
                                            }
                                        }

                                        if (!found) {
                                            missing = true;
                                        }

                                        found = false;
                                        for (int j = 0; j < ficheros.length && !found; j++) {
                                            if (ficheros[j].indexOf("5x2-" + l + "tst.dat") != -1) {
                                                pareja2 = ficheros[j] + ",";
                                                pareja2 = pareja;
                                                pareja += "," + ficheros[j];
                                                found = true;
                                            }
                                        }

                                        if (!found || missing) {
                                            missing = true;
                                            ((DataSet) parent.graphDiagramINNER.mainGraph.getNodeAt(i)).addMissingPartition(pareja, k);
                                        } else {
                                            ((Vector) (listas.elementAt(k))).add(pareja);
                                            ((Vector) (listas.elementAt(k))).add(pareja2);
                                        }
                                    }
                                } catch (Exception ex) {
                                }
                            }
                        } else {
                            boolean stop;
                            for (int k = 0; k < Layer.numLayers; k++) {
                                try {
                                    dir = new File("." + dsc.getPath(k) + dsc.getName(k));
                                    ficheros = dir.list();
                                    stop = false;
                                    for (int j = 0; j < ficheros.length && !stop; j++) {
                                        if (ficheros[j].compareTo(dsc.getName(k) + "-10-1tra.dat") == 0) {
                                            ((Vector) (listas.elementAt(k))).add(dsc.getName(k) + "-10-1tra.dat" + "," + dsc.getName(k) + "-10-1tst.dat");
                                            stop = true;
                                        } else {
                                            if (ficheros[j].compareTo(dsc.getName(k) + "-5-1tra.dat") == 0) {
                                                ((Vector) (listas.elementAt(k))).add(dsc.getName(k) + "-5-1tra.dat" + "," + dsc.getName(k) + "-5-1tst.dat");
                                                stop = true;
                                            } else {
                                                if (ficheros[j].compareTo(dsc.getName(k) + ".dat") == 0) {
                                                    ((Vector) (listas.elementAt(k))).add(dsc.getName(k) + ".dat" + "," + dsc.getName(k) + ".dat");
                                                    stop = true;
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                }
                            }

                        }
                        // }

                        if (missing == true) {
                            parent.status.setText("There are missing partitions. They will be regenerated when the experiment is generated.");
                        } else {
                            parent.status.setText("All the partitions of the data sets selected have been found.");
                        }
                        for (int l = 0; l < Layer.numLayers; l++) {
                            ((DataSet) parent.graphDiagramINNER.mainGraph.getNodeAt(i)).tableVector.setElementAt((Vector) (((Vector) listas.elementAt(l)).clone()), l);
                        }

                        //restore previous edited Layers
                        DataSet aux;
                        Vector main;
                        String name;
                        String name2;
                        Boolean find;

                        aux = (DataSet) parent.graphDiagramINNER.mainGraph.getNodeAt(i);
                        main = (Vector) aux.tableVector.clone();

                        for (int l = 0; l < Layer.numLayers; l++) {
                            name = dsc.getName(l);
                            find = false;
                            for (int index = 0; index < save.size() && !find; index++) {
                                name2 = "XXXXX";
                                if (((Vector) (save.get(index))).size() > 0) {
                                    name2 = (String) ((Vector) (save.get(index))).get(0);
                                }
                                if (name2.startsWith(name)) {
                                    main.setElementAt(save.get(index), l);
                                    find = true;
                                }
                            }
                        }

                        aux.tableVector = (Vector) main.clone();
                    }
                }//type_lqd

            } // end-typedataset
            else {
                if (parent.objType != parent.LQD) {
                    dscal = new ExternalObjectDescription(parent.graphDiagramINNER.mainGraph.getNodeAt(i).dsc);
                    parent.graphDiagramINNER.mainGraph.getNodeAt(i).dsc = new ExternalObjectDescription(dscal.getName(), dscal.getPath(), dscal.getSubtype(), dscal.getJarName());
                    for (int k = 0, conta = 0; k < check.size(); k++) {
                        if (actives[k] == true && actTemp[k] == false) {//one dataset has been removed
                            parent.graphDiagramINNER.mainGraph.getNodeAt(i).par.removeElementAt(conta);
                            action = false;
                            posAction = k;
                        } else if (actives[k] == false && actTemp[k] == true) {//one dataset has been added
                            //System.out.println (k+"-"+conta+"*****************************************"+((Parameters)(parent.graphDiagramINNER.grafo.getNodeAt(i).par.elementAt(0))).getValues());
                            Parameters temporal = new Parameters((Parameters) (parent.graphDiagramINNER.mainGraph.getNodeAt(i).par.elementAt(0)));
                            //temporal.setValues(temporal.getDefaultValues());

                            parent.graphDiagramINNER.mainGraph.getNodeAt(i).par.insertElementAt(new Parameters(temporal), conta);
                            action = true;
                            posAction = k;
                        //System.out.println ("#"+k+"-"+conta+"*****************************************"+((Parameters)parent.graphDiagramINNER.grafo.getNodeAt(i).par.elementAt(0)).getValues());
                        }
                        if (actives[k] == true) {
                            conta++;
                        }
                    }
                }
            }//else dscal
        }//for numbers of numNodes

        if (parent.objType != parent.LQD) {
            actives[posAction] = action;
            parent.reload_algorithms();
        }

    }

    /**
     * Update input and output variables
     *
     * @param node_type Type of node
     */
    void actDatasetIO(int node_type) {

        //exobj contains the datasets that are in the Node 
        if (parent.objType != parent.LQD) {
            int iDataSetIndex;
            iDataSetIndex = 0;
            while (((Node) parent.experimentGraph.getNodes().elementAt(iDataSetIndex)).type != Node.type_Dataset) {
                iDataSetIndex++;
            }
            //System.out.println("tenemos tantos nodos distintos de dataset "+
            //        iDataSetIndex);
            if (iDataSetIndex < parent.experimentGraph.getNodes().size()) {
                //System.out.println("index es menor que el total que tenemos lo que implica que hay un dataset y es el "+
                //   iDataSetIndex);
                DataSet dataSet = (DataSet) parent.experimentGraph.getNodes().elementAt(iDataSetIndex);
                // System.out.println("el tipo de dataset es " + dataSet.type+ " y si es lqd "+dataSet.type_lqd);
                dataSet.updateState();
                dataSet.actInputOutput(parent.graphDiagramINNER.mainGraph.getNodeAt(iDataSetIndex).dsc, parent.graphDiagramINNER);
            }
        } else //we have low quality data and we must to control the different datasets
        {
            System.out.println("The numbers of nodes in the graph are " +
                    parent.graphDiagramINNER.mainGraph.numNodes());

            for (int i = 0; i < parent.graphDiagramINNER.mainGraph.numNodes(); i++) {
                System.out.println("The type_lqd is  " +
                        parent.graphDiagramINNER.mainGraph.getNodeAt(i).type_lqd);
                if (parent.graphDiagramINNER.mainGraph.getNodeAt(i).type == Node.type_Dataset) {
                    if (parent.graphDiagramINNER.mainGraph.getNodeAt(i).type_lqd == node_type) {

                        System.out.println("we are in the correct node ");
                        DataSet dataSet = (DataSet) parent.graphDiagramINNER.mainGraph.getNodeAt(i);
                        System.out.println("Type of dataset " + dataSet.type + " type of lqd " + dataSet.type_lqd);
                        if (dataSet.type_lqd == Node.LQD) {
                            dataSet.actInputOutputLQD(parent.graphDiagramINNER.mainGraph.getNodeAt(i).dsc, parent.graphDiagramINNER, parent.listData);
                        } else if (dataSet.type_lqd == Node.LQD_C) {
                            dataSet.actInputOutputLQD(parent.graphDiagramINNER.mainGraph.getNodeAt(i).dsc, parent.graphDiagramINNER, parent.listDataLQD_C);
                        } else if (dataSet.type_lqd == Node.C_LQD) {
                            dataSet.actInputOutputLQD(parent.graphDiagramINNER.mainGraph.getNodeAt(i).dsc, parent.graphDiagramINNER, parent.listDataC_LQD);
                        } else if (dataSet.type_lqd == Node.CRISP2) {
                            dataSet.actInputOutputLQD(parent.graphDiagramINNER.mainGraph.getNodeAt(i).dsc, parent.graphDiagramINNER, parent.listDataC);
                        }
                    }
                }
            }
        }

    }

    /**
     * Import button
     * @param e Event
     */
    public void import_actionPerformed(ActionEvent e) {

        this.saveSelected();

        //new DataCF dialog
        DataCFFrame frame = new DataCFFrame();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        frame.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);

        frame.setParent(this.parent);
        frame.getSelectorToolbar().setVisible(false);
        frame.getSelectorTabbedPane().removeAll();
        if (parent.cvType == Experiments.P5X2) {
            frame.addImportTab(false, false);
        } else if (parent.cvType == Experiments.PK) {
            frame.addImportTab(false, true);
        }else{
            frame.addImportTab(false, false);
        }
        
        this.parent.setVisible(false);
        frame.setVisible(true);

    }

    /**
     * Sort the data sets list loaded from disc, so they
     * appear in alphabetic order
     */
    public void sortDatasets() {
        //sort the data set list
        Collections.sort(datasetXML);
        Collections.sort(datasetList);
    }
    /**
     * Sort the data sets list loaded from disc, so they
     * appear in alphabetic order
     */
    public void sortDatasetLQD_C() {
        //sort the data set list
        Collections.sort(datasetXMLLQD_C);
        Collections.sort(datasetListLQD_C);
    }
    /**
     * Sort the data sets list loaded from disc, so they
     * appear in alphabetic order
     */
    public void sortDatasetC_LQD() {
        //sort the data set list
        Collections.sort(datasetXMLC_LQD);
        Collections.sort(datasetListC_LQD);
    }

    /**
     * Sort the data sets list loaded from disc, so they
     * appear in alphabetic order
     */
    public void sortDatasetC() {
        //sort the data set list
        Collections.sort(datasetXMLC);
        Collections.sort(datasetListC);
    }

    /**
     * Tracks the button pressed, and implements the changes which derive
     * from the action
     * @param e actionevent associated
     */
    void checks_actionPerformed(ActionEvent e) {

        int i;
        int id = 0;
        Object s = e.getSource();
        int some_active = 0;

        for (i = 0; i < checks.size(); i++) {
            if (((JButton) checks.elementAt(i)).getText() == "Del") {
                activos[i] = true;
                some_active = 1;
            } else {
                activos[i] = false;
            }
        }

        //search the checkbutton clicked
        for (i = 0; i < checks.size(); i++) {
            if (s == checks.elementAt(i)) {
                id = i;
            }
        }
        //    activos[id] = !activos[id];

        if (((JButton) (checks.elementAt(id))).getText() == "Del") {

            if (id < datasetsNoUser) {
                invertSelection.setEnabled(true);
            } else {
                invertSelectionUser.setEnabled(true);
            }
            ((JButton) (checks.elementAt(id))).setText("Add");
            if (parent.objType != parent.LQD) {
                ((JButton) (edits.elementAt(id))).setVisible(false);
                this.remove(((JButton) (edits.elementAt(id))));
            }
        } else {
            ((JButton) (checks.elementAt(id))).setText("Del");
            if (parent.objType != parent.LQD) {
                ((JButton) (edits.elementAt(id))).setVisible(true);
                this.add((JButton) (edits.elementAt(id)));
            }
        }

        //at least there must be a dataset selected
        if (Layer.numLayers == 1 && ((JButton) (checks.elementAt(id))).getText() == "Add") {
            ((JButton) (checks.elementAt(id))).setText("Del");
            if (parent.objType != parent.LQD) {
                ((JButton) (edits.elementAt(id))).setVisible(true);
                this.add((JButton) (edits.elementAt(id)));
            }
        }

        if (parent.objType == parent.LQD) {
            if (some_active == 0) {
                parent.dsc = new ExternalObjectDescription((ExternalObjectDescription) actualList.elementAt(id), true);
                ((JButton) checks.elementAt(id)).setEnabled(false);
                Point punto = new Point(125, 125);
                parent.graphDiagramINNER.new_dataset(punto, parent.dsc, GraphPanel.NODELQD);
                parent.cursorAction = GraphPanel.SELECTING;
                ((CardLayout) parent.selectionPanel1.getLayout()).show(parent.selectionPanel1, "dinDatasetsCard");
                parent.graphDiagramINNER.repaint();
            }
            loadDatasetInfo(checks, actualList, activos, parent.dsc, Node.LQD);
        } else {
            loadDatasetInfo(checks, actualList, activos, parent.dsc, Node.CRISP);
        }

        this.repaint();
        //System.out.println("  > Final checks_actionPerformed");

        if (parent.objType == parent.LQD) {
            actDatasetIO(Node.LQD);
        } else {
            actDatasetIO(Node.CRISP);
        }

    }

    /**
     * Adds or remove a given data set
     * @param n identifier of the data set
     */
    void checks_actionPerformedNumber(int n) {

        int i;
        int id = 0;
        int some_active = 0;

        for (i = 0; i < checks.size(); i++) {
            if (((JButton) checks.elementAt(i)).getText() == "Del") {
                activos[i] = true;
                some_active = 1;
            } else {
                activos[i] = false;
            }
        }

        //search the checkbutton clicked
        id = n;

        //    activos[id] = !activos[id];

        if (((JButton) (checks.elementAt(id))).getText() == "Del") {
            ((JButton) (checks.elementAt(id))).setText("Add");
            if (parent.objType != parent.LQD) {
                ((JButton) (edits.elementAt(id))).setVisible(false);
                this.remove(((JButton) (edits.elementAt(id))));
            }
        } else {
            ((JButton) (checks.elementAt(id))).setText("Del");
            if (parent.objType != parent.LQD) {
                ((JButton) (edits.elementAt(id))).setVisible(true);
                this.add((JButton) (edits.elementAt(id)));
            }
        }

        //at least there must be a dataset selected
        if (Layer.numLayers == 1 && ((JButton) (checks.elementAt(id))).getText() == "Add") {
            ((JButton) (checks.elementAt(id))).setText("Del");
            if (parent.objType != parent.LQD) {
                ((JButton) (edits.elementAt(id))).setVisible(true);
                this.add((JButton) (edits.elementAt(id)));
            }
        }

        if (parent.objType == parent.LQD) {
            if (some_active == 0) {
                parent.dsc = new ExternalObjectDescription((ExternalObjectDescription) actualList.elementAt(id), true);
                ((JButton) checks.elementAt(id)).setEnabled(false);
                Point punto = new Point(125, 125);
                parent.graphDiagramINNER.new_dataset(punto, parent.dsc, GraphPanel.NODELQD);
                parent.cursorAction = GraphPanel.SELECTING;
                ((CardLayout) parent.selectionPanel1.getLayout()).show(parent.selectionPanel1, "dinDatasetsCard");
                parent.graphDiagramINNER.repaint();
            }
            loadDatasetInfo(checks, actualList, activos, parent.dsc, Node.LQD);
        } else {
            parent.dsc = new ExternalObjectDescription((ExternalObjectDescription) actualList.elementAt(id), true);
            loadDatasetInfo(checks, actualList, activos, parent.dsc, Node.CRISP);

        }

        this.repaint();
        //System.out.println("  > Final checks_actionPerformed");

        if (parent.objType == parent.LQD) {
            actDatasetIO(Node.LQD);
        } else {
            actDatasetIO(Node.CRISP);
        }


    }

    /**
     * Adds or remove a given data set
     * @param e Event
     */
    void checksLQD_C_actionPerformed(ActionEvent e) {


        //  JOptionPane.showMessageDialog(this, "Nentraaaaaaaaaaaaa checkslqd_d",
        //        "Invalid number of folds", JOptionPane.ERROR_MESSAGE);
        int i;
        int id = 0;
        Object s = e.getSource();

        int some_active = 0;
        for (i = 0; i < checksLQD_C.size(); i++) {
            if (((JButton) checksLQD_C.elementAt(i)).getText() == "Del") {
                activosLQD_C[i] = true;
                some_active = 1;
            } else {
                activosLQD_C[i] = false;
            }
        }

        //search the checkbutton clicked
        for (i = 0; i < checksLQD_C.size(); i++) {
            if (s == checksLQD_C.elementAt(i)) {
                id = i;
            }
        }


        if (((JButton) (checksLQD_C.elementAt(id))).getText() == "Del") {
            ((JButton) (checksLQD_C.elementAt(id))).setText("Add");
        } else {
            ((JButton) (checksLQD_C.elementAt(id))).setText("Del");
        }

        //at least there must be a dataset selected
        if (Layer.numLayers == 1 && ((JButton) (checksLQD_C.elementAt(id))).getText() == "Add") {
            ((JButton) (checksLQD_C.elementAt(id))).setText("Del");
        }

        //If the first dataset that we insert the this type. Therefore we need to draw the node
        //in the panel of the right
        if (some_active == 0) {
            parent.dscLQD_C = new ExternalObjectDescription((ExternalObjectDescription) actualListLQD_C.elementAt(id), true);
            ((JButton) checksLQD_C.elementAt(id)).setEnabled(false);
            Point punto = new Point(125 + 205, 125);
            parent.graphDiagramINNER.new_dataset(punto, parent.dscLQD_C, GraphPanel.NODELQD_c);
            parent.cursorAction = GraphPanel.SELECTING;
            ((CardLayout) parent.selectionPanel1.getLayout()).show(parent.selectionPanel1, "dinDatasetsCard");
            parent.graphDiagramINNER.repaint();
        }

        loadDatasetInfo(checksLQD_C, actualListLQD_C, activosLQD_C, parent.dscLQD_C, Node.LQD_C);

        this.repaint();
        //System.out.println("  > Final checks_actionPerformed");

        actDatasetIO(Node.LQD_C);

    }

    /**
     * Adds or remove a given data set
     * @param e Event
     */
    void checksC_LQD_actionPerformed(ActionEvent e) {


        //  JOptionPane.showMessageDialog(this, "Nentraaaaaaaaaaaaa checkslqd_d",
        //        "Invalid number of folds", JOptionPane.ERROR_MESSAGE);
        int i;
        int id = 0;
        Object s = e.getSource();
        int some_active = 0;
        for (i = 0; i < checksC_LQD.size(); i++) {
            if (((JButton) checksC_LQD.elementAt(i)).getText() == "Del") {
                activosC_LQD[i] = true;
                some_active = 1;
            } else {
                activosC_LQD[i] = false;
            }
        }

        //search the checkbutton clicked
        for (i = 0; i < checksC_LQD.size(); i++) {
            if (s == checksC_LQD.elementAt(i)) {
                id = i;
            }
        }


        if (((JButton) (checksC_LQD.elementAt(id))).getText() == "Del") {
            ((JButton) (checksC_LQD.elementAt(id))).setText("Add");
        } else {
            ((JButton) (checksC_LQD.elementAt(id))).setText("Del");
        }

        //at least there must be a dataset selected
        if (Layer.numLayers == 1 && ((JButton) (checksC_LQD.elementAt(id))).getText() == "Add") {
            ((JButton) (checksC_LQD.elementAt(id))).setText("Del");
        }

        if (some_active == 0) {
            parent.dscC_LQD = new ExternalObjectDescription((ExternalObjectDescription) actualListC_LQD.elementAt(id), true);
            ((JButton) checksC_LQD.elementAt(id)).setEnabled(false);
            Point punto = new Point(125, 125 + 205);
            parent.graphDiagramINNER.new_dataset(punto, parent.dscC_LQD, GraphPanel.NODEC_LQD);
            parent.cursorAction = GraphPanel.SELECTING;
            ((CardLayout) parent.selectionPanel1.getLayout()).show(parent.selectionPanel1, "dinDatasetsCard");
            parent.graphDiagramINNER.repaint();
        }

        loadDatasetInfo(checksC_LQD, actualListC_LQD, activosC_LQD, parent.dscC_LQD, Node.C_LQD);

        this.repaint();
        //System.out.println("  > Final checks_actionPerformed");

        actDatasetIO(Node.C_LQD);

    }

    /**
     * Adds or remove a given data set
     * @param e Event
     */
    void checksC_actionPerformed(ActionEvent e) {


        //  JOptionPane.showMessageDialog(this, "Nentraaaaaaaaaaaaa checkslqd_d",
        //        "Invalid number of folds", JOptionPane.ERROR_MESSAGE);
        int i;
        int id = 0;
        Object s = e.getSource();
        int some_active = 0;

        for (i = 0; i < checksC.size(); i++) {
            if (((JButton) checksC.elementAt(i)).getText() == "Del") {
                activosC[i] = true;
                some_active = 1;
            } else {
                activosC[i] = false;
            }
        }

        //search the checkbutton clicked
        for (i = 0; i < checksC.size(); i++) {
            if (s == checksC.elementAt(i)) {
                id = i;
            }
        }


        if (((JButton) (checksC.elementAt(id))).getText() == "Del") {
            ((JButton) (checksC.elementAt(id))).setText("Add");
        } else {
            ((JButton) (checksC.elementAt(id))).setText("Del");
        }

        //at least there must be a dataset selected
        if (Layer.numLayers == 1 && ((JButton) (checksC.elementAt(id))).getText() == "Add") {
            ((JButton) (checksC.elementAt(id))).setText("Del");
        }

        if (some_active == 0) {
            parent.dscC = new ExternalObjectDescription((ExternalObjectDescription) actualListC.elementAt(id), true);
            ((JButton) checksC.elementAt(id)).setEnabled(false);
            Point punto = new Point(125 + 205, 125 + 205);
            parent.graphDiagramINNER.new_dataset(punto, parent.dscC, GraphPanel.NODEC);
            parent.cursorAction = GraphPanel.SELECTING;
            ((CardLayout) parent.selectionPanel1.getLayout()).show(parent.selectionPanel1, "dinDatasetsCard");
            parent.graphDiagramINNER.repaint();
        }
        loadDatasetInfo(checksC, actualListC, activosC, parent.dscC, Node.CRISP2);

        this.repaint();
        //System.out.println("  > Final checks_actionPerformed");

        actDatasetIO(Node.CRISP2);

    }

    /**
     * Updates status of active data sets
     * @param check Check list
     * @param dsc Parent dsc
     * @param List List of data sets
     * @param type_node Type of node
     * @param punto Parent point
     */
    public void some_active(Vector check, ExternalObjectDescription dsc, Vector List, int type_node, Point punto) {
        int some_active = 0;
        for (int i = 0; i < check.size(); i++) {
            if (((JButton) check.elementAt(i)).getText() == "Del") {
                some_active = 1;
            }
        }

        if (some_active == 0) {

            for (int i = 0; i < check.size(); i++) {
                if (i == 0) {
                    dsc = new ExternalObjectDescription((ExternalObjectDescription) List.elementAt(i), true);
                } else {
                    dsc.insert(new ExternalObjectDescription((ExternalObjectDescription) List.elementAt(i)), true);
                }
            }

            parent.graphDiagramINNER.new_dataset(punto, dsc, type_node);
            parent.cursorAction = GraphPanel.SELECTING;
            ((CardLayout) parent.selectionPanel1.getLayout()).show(parent.selectionPanel1, "dinDatasetsCard");
            parent.graphDiagramINNER.repaint();
        }
    }

    /**
     * Select all button
     * @param e Event
     */
    public void selectAll_actionPerformed(ActionEvent e) {

        int i;

        if (parent.objType != parent.LQD) {
            for (i = 0; i < checks.size() && i < datasetsNoUser; i++) {
                if (((JButton) (checks.elementAt(i))).getText() == "Add") {
                    checks_actionPerformedNumber(i);
                }
            }

            invertSelection.setEnabled(false);
        } else {
            Point punto = new Point(125, 125);
            some_active(checks, parent.dsc, actualList, GraphPanel.NODELQD, punto);


            for (i = 0; i < checks.size() && i < datasetsNoUser; i++) {
                activos[i] = true;
            }


            for (i = 0; i < checks.size() && i < datasetsNoUser; i++) {
                if ((activos[i]) && ((JButton) (checks.elementAt(i))).getText() == "Add") {
                    ((JButton) checks.elementAt(i)).setText("Del");
                    if (parent.objType != parent.LQD) {
                        ((JButton) edits.elementAt(i)).setVisible(true);
                        this.add((JButton) (edits.elementAt(i)));
                    }
                }
            }

            loadDatasetInfo(checks, actualList, activos, parent.dsc, Node.LQD);

            this.repaint();
            //System.out.println("  > Final selectAll_actionPerformed");
            actDatasetIO(Node.LQD);

        }

    }

    /**
     * Invert button
     * @param e Event
     */
    public void invertSelection_actionPerformed(ActionEvent e) {

        int i;

        if (parent.objType == parent.LQD) {

            Point punto = new Point(125, 125);
            some_active(checks, parent.dsc, actualList, GraphPanel.NODELQD, punto);
        }

        boolean oneActive = false;

        for (i = 0; i < checks.size() && i < datasetsNoUser; i++) {
            if (((JButton) checks.elementAt(i)).getText() == "Del") {
                activos[i] = false;
            } else {
                activos[i] = true;
            }
        }

        if (parent.objType != parent.LQD) {
            for (i = datasetsNoUser; i < checks.size(); i++) {
                if (((JButton) checks.elementAt(i)).getText() == "Del") {
                    activos[i] = true;
                    oneActive = true;
                } else {
                    activos[i] = false;
                }
            }
        }

        for (i = 0; i < checks.size(); i++) {
            if (activos[i]) {
                oneActive = true;
            }
        }
        //Ensure at least one dataset will remain selected
        if (oneActive) {

            for (i = 0; i < checks.size() && i < datasetsNoUser; i++) {
                if (((JButton) (checks.elementAt(i))).getText() == "Add") {
                    ((JButton) checks.elementAt(i)).setText("Del");
                    if (parent.objType != parent.LQD) {
                        ((JButton) edits.elementAt(i)).setVisible(true);
                        this.add((JButton) (edits.elementAt(i)));
                    }
                } else {
                    ((JButton) (checks.elementAt(i))).setText("Add");
                    if (parent.objType != parent.LQD) {
                        ((JButton) (edits.elementAt(i))).setVisible(false);
                        this.remove(((JButton) (edits.elementAt(i))));
                    }
                }
            }


            if (parent.objType == parent.LQD) {
                loadDatasetInfo(checks, actualList, activos, parent.dsc, Node.LQD);
            } else {
                loadDatasetInfo(checks, actualList, activos, parent.dsc, Node.CRISP);
            }

            this.repaint();

            if (parent.objType == parent.LQD) {
                actDatasetIO(Node.LQD);
            } else {
                actDatasetIO(Node.CRISP);
            }

        }

    }

    /**
     * Select all button
     * @param e Event
     */
    public void selectAllUser_actionPerformed(ActionEvent e) {


        for (int i = datasetsNoUser; i < checks.size(); i++) {
            if (((JButton) (checks.elementAt(i))).getText() == "Add") {
                checks_actionPerformedNumber(i);
            }
        }
        invertSelectionUser.setEnabled(false);

    /*
    for (int i = datasetsNoUser; i < checks.size(); i++) {
    activos[i] = true;
    }

    for (int i = datasetsNoUser; i < checks.size(); i++) {
    if (((JButton) (checks.elementAt(i))).getText() == "Add") {
    ((JButton) checks.elementAt(i)).setText("Del");
    ((JButton) edits.elementAt(i)).setVisible(true);
    this.add((JButton) (edits.elementAt(i)));
    }
    }

    loadDatasetInfo(checks,actualList,activos,parent.dsc,Node.CRISP);

    this.repaint();
    //System.out.println("  > Final selectAll_actionPerformed");

    actDatasetIO(Node.CRISP);
     */
    }

    /**
     * Select all button
     * @param e Event
     */
    public void selectAllLQD_C_actionPerformed(ActionEvent e) {

        int i;



        Point punto = new Point(125 + 205, 125);
        some_active(checksLQD_C, parent.dscLQD_C, actualListLQD_C, GraphPanel.NODELQD_c, punto);


        //System.out.println("Tamanio "+ checks.size());

        for (i = 0; i < checksLQD_C.size(); i++) {
            activosLQD_C[i] = true;
        }


        for (i = 0; i < checksLQD_C.size(); i++) {
            if ((activosLQD_C[i]) && ((JButton) (checksLQD_C.elementAt(i))).getText() == "Add") {
                ((JButton) checksLQD_C.elementAt(i)).setText("Del");
            // ((JButton) checksLQD_C.elementAt(i)).setForeground(Color.black);

            }
        }
        loadDatasetInfo(checksLQD_C, actualListLQD_C, activosLQD_C, parent.dscLQD_C, Node.LQD_C);

        this.repaint();
        //System.out.println("  > Final selectAll_actionPerformed");

        actDatasetIO(Node.LQD_C);
    }

    /**
     * Select all button
     * @param e Event
     */
    public void selectAllC_LQD_actionPerformed(ActionEvent e) {

        int i;

        //System.out.println("Tamanio "+ checks.size());

        Point punto = new Point(125, 125 + 205);
        some_active(checksC_LQD, parent.dscC_LQD, actualListC_LQD, GraphPanel.NODEC_LQD, punto);


        for (i = 0; i < checksC_LQD.size(); i++) {
            activosC_LQD[i] = true;
        }


        for (i = 0; i < checksC_LQD.size(); i++) {
            if ((activosC_LQD[i]) && ((JButton) (checksC_LQD.elementAt(i))).getText() == "Add") {
                ((JButton) checksC_LQD.elementAt(i)).setText("Del");

            }
        }
        loadDatasetInfo(checksC_LQD, actualListC_LQD, activosC_LQD, parent.dscC_LQD, Node.C_LQD);

        this.repaint();
        //System.out.println("  > Final selectAll_actionPerformed");

        actDatasetIO(Node.C_LQD);
    }

    /**
     * Select all button
     * @param e Event
     */
    public void selectAllC_actionPerformed(ActionEvent e) {

        int i;

        //System.out.println("Tamanio "+ checks.size());
        Point punto = new Point(125 + 205, 125 + 205);
        some_active(checksC, parent.dscC, actualListC, GraphPanel.NODEC, punto);

        for (i = 0; i < checksC.size(); i++) {
            activosC[i] = true;
        }


        for (i = 0; i < checksC.size(); i++) {
            if ((activosC[i]) && ((JButton) (checksC.elementAt(i))).getText() == "Add") {
                ((JButton) checksC.elementAt(i)).setText("Del");

            }
        }
        loadDatasetInfo(checksC, actualListC, activosC, parent.dscC, Node.CRISP2);

        this.repaint();
        //System.out.println("  > Final selectAll_actionPerformed");

        actDatasetIO(Node.CRISP2);
    }

    /**
     * Invert button
     * @param e Event
     */
    public void invertSelectionLQD_C_actionPerformed(ActionEvent e) {

        int i;
        boolean oneActive = false;

        Point punto = new Point(125 + 205, 125);
        some_active(checksLQD_C, parent.dscLQD_C, actualListLQD_C, GraphPanel.NODELQD_c, punto);

        for (i = 0; i < checksLQD_C.size(); i++) {
            if (((JButton) checksLQD_C.elementAt(i)).getText() == "Del") {
                activosLQD_C[i] = false;
            } else {
                activosLQD_C[i] = true;
            }
        }



        for (i = 0; i < checksLQD_C.size(); i++) {
            if (activosLQD_C[i]) {
                oneActive = true;
            }
        }
        //Ensure at least one dataset will remain selected
        if (oneActive) {

            for (i = 0; i < checksLQD_C.size(); i++) {
                if (((JButton) (checksLQD_C.elementAt(i))).getText() == "Add") {
                    ((JButton) checksLQD_C.elementAt(i)).setText("Del");
                //  ((JButton) checksLQD_C.elementAt(i)).setForeground(Color.black);

                } else {
                    ((JButton) (checksLQD_C.elementAt(i))).setText("Add");
                // ((JButton) (checksLQD_C.elementAt(i))).setForeground(Color.red);

                }
            }

            loadDatasetInfo(checksLQD_C, actualListLQD_C, activosLQD_C, parent.dscLQD_C, Node.LQD_C);

            this.repaint();

            actDatasetIO(Node.LQD_C);
        }
    }

    /**
     * Invert button
     * @param e Event
     */
    public void invertSelectionC_LQD_actionPerformed(ActionEvent e) {

        int i;
        boolean oneActive = false;

        Point punto = new Point(125, 125 + 205);
        some_active(checksC_LQD, parent.dscC_LQD, actualListC_LQD, GraphPanel.NODEC_LQD, punto);


        for (i = 0; i < checksC_LQD.size(); i++) {
            if (((JButton) checksC_LQD.elementAt(i)).getText() == "Del") {
                activosC_LQD[i] = false;
            } else {
                activosC_LQD[i] = true;
            }
        }


        for (i = 0; i < checksC_LQD.size(); i++) {
            if (activosC_LQD[i]) {
                oneActive = true;
            }
        }
        //Ensure at least one dataset will remain selected
        if (oneActive) {

            for (i = 0; i < checksC_LQD.size(); i++) {
                if (((JButton) (checksC_LQD.elementAt(i))).getText() == "Add") {
                    ((JButton) checksC_LQD.elementAt(i)).setText("Del");

                } else {
                    ((JButton) (checksC_LQD.elementAt(i))).setText("Add");

                }
            }

            loadDatasetInfo(checksC_LQD, actualListC_LQD, activosC_LQD, parent.dscC_LQD, Node.C_LQD);

            this.repaint();

            actDatasetIO(Node.C_LQD);
        }
    }

    /**
     * Invert button
     * @param e Event
     */
    public void invertSelectionC_actionPerformed(ActionEvent e) {

        int i;
        boolean oneActive = false;

        Point punto = new Point(125 + 205, 125 + 205);
        some_active(checksC, parent.dscC, actualListC, GraphPanel.NODEC, punto);


        for (i = 0; i < checksC.size(); i++) {
            if (((JButton) checksC.elementAt(i)).getText() == "Del") {
                activosC[i] = false;
            } else {
                activosC[i] = true;
            }
        }


        for (i = 0; i < checksC.size(); i++) {
            if (activosC[i]) {
                oneActive = true;
            }
        }
        //Ensure at least one dataset will remain selected
        if (oneActive) {

            for (i = 0; i < checksC.size(); i++) {
                if (((JButton) (checksC.elementAt(i))).getText() == "Add") {
                    ((JButton) checksC.elementAt(i)).setText("Del");

                } else {
                    ((JButton) (checksC.elementAt(i))).setText("Add");

                }
            }

            loadDatasetInfo(checksC, actualListC, activosC, parent.dscC, Node.CRISP2);

            this.repaint();

            actDatasetIO(Node.CRISP2);
        }
    }

    /**
     * LQD to Crisp button
     * @param e Event
     */
    public void lqd_crisp_actionPerformed(ActionEvent e) {


        titulo3.setBounds(new Rectangle(10, lqd_crisp.getY() + 30, 200, 16));
        crisp.setBounds(new Rectangle(15, titulo3.getY() + 20 + 35, 330, crisp.getHeight()));
        crispb.setBounds(new Rectangle(10, titulo3.getY() + 25, 150, 20));
        if (lqd_crisp.getBackground() == Color.yellow) {
            lqdcrisp.setVisible(false);
            lqd_crisp.setBackground(new Color(225, 225, 225));


        } else {
            lqdcrisp.setVisible(true);
            lqd_crisp.setBackground(Color.yellow);

            crisplqd.setVisible(false);
            crisp_lqd.setBackground(new Color(225, 225, 225));

            titulo3.setBounds(new Rectangle(10, lqdcrisp.getY() + 25 + lqdcrisp.getHeight(), 200, 16));
            crisp.setBounds(new Rectangle(15, titulo3.getY() + 20 + 35, 330, crisp.getHeight()));
            crispb.setBounds(new Rectangle(10, titulo3.getY() + 25, 150, 20));
        }

    }

    /**
     * Crisp button
     * @param e Event
     */
    public void crisp_actionPerformed(ActionEvent e) {


        if (crispb.getBackground() == Color.darkGray) {
            crisp.setVisible(false);
            crispb.setBackground(new Color(225, 225, 225));
        } else {
            crisp.setVisible(true);
            crispb.setBackground(Color.darkGray);

        }

    }

    /**
     * Crisp to LQD button
     * @param e Event
     */
    public void crisp_lqd_actionPerformed(ActionEvent e) {

        titulo3.setBounds(new Rectangle(10, crisp_lqd.getY() + 30, 200, 16));
        crisp.setBounds(new Rectangle(15, titulo3.getY() + 20 + 35, 330, crisp.getHeight()));
        crispb.setBounds(new Rectangle(10, titulo3.getY() + 25, 150, 20));

        if (crisp_lqd.getBackground() == Color.blue) {
            crisplqd.setVisible(false);
            crisp_lqd.setBackground(new Color(225, 225, 225));
        } else {
            crisplqd.setVisible(true);
            crisp_lqd.setBackground(Color.blue);

            lqdcrisp.setVisible(false);
            lqd_crisp.setBackground(new Color(225, 225, 225));
            titulo3.setBounds(new Rectangle(10, crisplqd.getY() + 25 + crisplqd.getHeight(), 200, 16));
            crisp.setBounds(new Rectangle(15, titulo3.getY() + 20 + 35, 330, crisp.getHeight()));
            crispb.setBounds(new Rectangle(10, titulo3.getY() + 25, 150, 20));

        }

    }

    /**
     * Invert button
     * @param e Event
     */
    public void invertSelectionUser_actionPerformed(ActionEvent e) {

        int i;
        boolean oneActive = false;

        for (i = datasetsNoUser; i < checks.size(); i++) {
            if (((JButton) checks.elementAt(i)).getText() == "Del") {
                activos[i] = false;
            } else {
                activos[i] = true;
            }
        }

        for (i = 0; i < checks.size() && i < datasetsNoUser; i++) {
            if (((JButton) checks.elementAt(i)).getText() == "Del") {
                activos[i] = true;
                oneActive = true;
            } else {
                activos[i] = false;
            }
        }

        for (i = 0; i < checks.size(); i++) {
            if (activos[i]) {
                oneActive = true;
            }
        }
        //Ensure at least one dataset will remain selected
        if (oneActive) {

            for (i = datasetsNoUser; i < checks.size(); i++) {
                if (((JButton) (checks.elementAt(i))).getText() == "Add") {
                    ((JButton) checks.elementAt(i)).setText("Del");
                    ((JButton) edits.elementAt(i)).setVisible(true);
                    this.add((JButton) (edits.elementAt(i)));
                } else {
                    ((JButton) (checks.elementAt(i))).setText("Add");
                    ((JButton) (edits.elementAt(i))).setVisible(false);
                    this.remove(((JButton) (edits.elementAt(i))));
                }
            }

            loadDatasetInfo(checks, actualList, activos, parent.dsc, Node.CRISP);

            this.repaint();

            actDatasetIO(Node.CRISP);

        }

    }

    /**
     * Hides import button
     */
    public void hideImportButton() {
        importB.setVisible(false);
    }
}

class DinamicDataset_importar_actionAdapter implements ActionListener {

    private DinamicDataset adaptee;

    DinamicDataset_importar_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.import_actionPerformed(e);
    }
}

class DinamicDataset_Checks_actionAdapter implements java.awt.event.ActionListener {

    DinamicDataset adaptee;

    DinamicDataset_Checks_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.checks_actionPerformed(e);
    }
}

class DinamicDataset_ChecksLQD_C_actionAdapter implements java.awt.event.ActionListener {

    DinamicDataset adaptee;

    DinamicDataset_ChecksLQD_C_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.checksLQD_C_actionPerformed(e);
    }
}

class DinamicDataset_ChecksC_LQD_actionAdapter implements java.awt.event.ActionListener {

    DinamicDataset adaptee;

    DinamicDataset_ChecksC_LQD_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.checksC_LQD_actionPerformed(e);
    }
}

class DinamicDataset_ChecksC_actionAdapter implements java.awt.event.ActionListener {

    DinamicDataset adaptee;

    DinamicDataset_ChecksC_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.checksC_actionPerformed(e);
    }
}

class DinamicDataset_Edits_actionAdapter implements java.awt.event.ActionListener {

    DinamicDataset adaptee;

    DinamicDataset_Edits_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.edits_actionPerformed(e);
    }
}

class DinamicDataset_remove_actionAdapter implements ActionListener {

    private SelectData adaptee;

    DinamicDataset_remove_actionAdapter(SelectData adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.remove_actionPerformed(e);
    }
}

class DinamicDataset_selectAll_actionAdapter implements ActionListener {

    private DinamicDataset adaptee;

    DinamicDataset_selectAll_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.selectAll_actionPerformed(e);
    }
}

class DinamicDataset_invertSelection_actionAdapter implements ActionListener {

    private DinamicDataset adaptee;

    DinamicDataset_invertSelection_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.invertSelection_actionPerformed(e);
    }
}

class DinamicDataset_selectAllUser_actionAdapter implements ActionListener {

    private DinamicDataset adaptee;

    DinamicDataset_selectAllUser_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.selectAllUser_actionPerformed(e);
    }
}

class DinamicDataset_invertSelectionUser_actionAdapter implements ActionListener {

    private DinamicDataset adaptee;

    DinamicDataset_invertSelectionUser_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.invertSelectionUser_actionPerformed(e);
    }
}

class DinamicDataset_invertSelectionLQD_C_actionAdapter implements ActionListener {

    private DinamicDataset adaptee;

    DinamicDataset_invertSelectionLQD_C_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.invertSelectionLQD_C_actionPerformed(e);
    }
}

class DinamicDataset_invertSelectionC_LQD_actionAdapter implements ActionListener {

    private DinamicDataset adaptee;

    DinamicDataset_invertSelectionC_LQD_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.invertSelectionC_LQD_actionPerformed(e);
    }
}

class DinamicDataset_invertSelectionC_actionAdapter implements ActionListener {

    private DinamicDataset adaptee;

    DinamicDataset_invertSelectionC_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.invertSelectionC_actionPerformed(e);
    }
}

class DinamicDataset_selectAllLQD_C_actionAdapter implements ActionListener {

    private DinamicDataset adaptee;

    DinamicDataset_selectAllLQD_C_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.selectAllLQD_C_actionPerformed(e);
    }
}

class DinamicDataset_selectAllC_LQD_actionAdapter implements ActionListener {

    private DinamicDataset adaptee;

    DinamicDataset_selectAllC_LQD_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.selectAllC_LQD_actionPerformed(e);
    }
}

class DinamicDataset_selectAllC_actionAdapter implements ActionListener {

    private DinamicDataset adaptee;

    DinamicDataset_selectAllC_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.selectAllC_actionPerformed(e);
    }
}

class DinamicDataset_lqd_crisp_actionAdapter implements ActionListener {

    private DinamicDataset adaptee;

    DinamicDataset_lqd_crisp_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.lqd_crisp_actionPerformed(e);
    }
}

class DinamicDataset_crisp_lqd_actionAdapter implements ActionListener {

    private DinamicDataset adaptee;

    DinamicDataset_crisp_lqd_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.crisp_lqd_actionPerformed(e);
    }
}

class DinamicDataset_crisp_actionAdapter implements ActionListener {

    private DinamicDataset adaptee;

    DinamicDataset_crisp_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.crisp_actionPerformed(e);
    }
}

