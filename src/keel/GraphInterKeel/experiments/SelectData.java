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
 * @authos Ana Palacios Jimenez and Luciano Sanchez Ramons 23-4-2010 (University of Oviedo)
 * @version 2.0
 */
package keel.GraphInterKeel.experiments;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;
import java.io.*;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import keel.GraphInterKeel.datacf.*;

public class SelectData extends JPanel implements Scrollable {

    private int maxUnitIncrement = 10;
    private int pos = 10;
    Vector checks;
    Vector checksC;
    Vector checksC_LQD;
    Vector checksLQD_C;
    Vector datasetList = new Vector();
    Vector datasetXML = new Vector();
    Vector datasetListC = new Vector();
    Vector datasetXMLC = new Vector();
    Vector datasetListC_LQD = new Vector();
    Vector datasetXMLC_LQD = new Vector();
    Vector datasetListLQD_C = new Vector();
    Vector datasetXMLLQD_C = new Vector();
    Vector actualList;
    Vector actualListC;
    Vector actualListC_LQD;
    Vector actualListLQD_C;
    JButton importB = new JButton();
    int componentWidth;
    JButton remove = new JButton();
    int cadParent;
    String cad;
    String cadParent_aux;
    JButton selectAll = new JButton();
    JButton selectAllC = new JButton();
    JButton selectAllC_LQD = new JButton();
    JButton selectAllLQD_C = new JButton();
    JButton invertSelection = new JButton();
    JButton invertSelectionC = new JButton();
    JButton invertSelectionC_LQD = new JButton();
    JButton invertSelectionLQD_C = new JButton();
    int numberOfUserDataset = 0;
    JButton selectAllUser = new JButton();
    JButton invertSelectionUser = new JButton();
    Experiments parent;
    Hashtable<String, Boolean> dataActive = new Hashtable<String, Boolean>();
    int oddWidth, evenWidth, maxWidth;
    JButton crisp_lqd = new JButton();
    JButton lqd_crisp = new JButton();
    JButton crispc = new JButton();
    DatasetXML listData[];
    JPanel crisplqd = new JPanel();
    JPanel lqdcrisp = new JPanel();
    JPanel crisp = new JPanel();
    JPanel lqd = new JPanel();
    JLabel titulo3 = new JLabel("Keel Crisp Dataset");
    int pos_initial = 0;
    int pos_initial_lc = 0;
    int pos_initial_c = 0;
    int posic_lqd;
    int posilqd_c;
    int posic;

    /**
     * Builder
     */
    public SelectData() {
        super();
    }

    /**
     * Builder
     * @param frame Parent frame
     */
    public SelectData(Experiments frame) {
        try {
            parent = frame;
            initSelector();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Transform method
     */
    private void transform() {
        pos_initial = 0;
        pos_initial_lc = 0;
        pos_initial_c = 0;
        lqdcrisp.setVisible(false);
        lqd_crisp.setBackground(new Color(225, 225, 225));
        crisplqd.setVisible(false);
        crisp_lqd.setBackground(new Color(225, 225, 225));
        lqdcrisp.removeAll();
        crisplqd.removeAll();
        crisp.removeAll();
        lqd.removeAll();
        crispc.setVisible(true);
        crispc.setBackground(new Color(225, 225, 225));

    }

    /**
     * Initialize
     * @throws java.lang.Exception
     */
    private void initSelector() throws Exception {

        checksLQD_C = new Vector();
        checks = new Vector();
        checksC = new Vector();
        checksC_LQD = new Vector();

        importB.setBackground(new Color(225, 225, 225));
        importB.setText("Import");
        importB.addActionListener(new SelectData_importar_actionAdapter(this));

        crisp_lqd.setBackground(new Color(225, 225, 225));
        crisp_lqd.setText("Crisp to Low Quality");
        crisp_lqd.addActionListener(new crisp_lqd_actionAdapter(this));
        crisp_lqd.setVisible(false);

        crispc.setBackground(new Color(225, 225, 225));
        crispc.setText("Keel Crisp Classification ");
        crispc.addActionListener(new crisp_actionAdapter(this));
        crispc.setVisible(true);
        crispc.setEnabled(false);

        crisp.setVisible(false);

        lqd_crisp.setBackground(new Color(225, 225, 225));
        lqd_crisp.setText("Low Quality to Crisp");
        lqd_crisp.addActionListener(new lqd_crisp_actionAdapter(this));
        lqd_crisp.setVisible(false);
        lqd_crisp.setEnabled(false);


        remove.setBackground(new Color(225, 225, 225));
        remove.setText("Remove");
        remove.addActionListener(new SelectData_remove_actionAdapter(this));

        selectAll.setBackground(new Color(225, 225, 225));
        selectAll.setText("Select All");
        selectAll.addActionListener(new SelectData_selectAll_actionAdapter(this));

        selectAllC.setBackground(new Color(225, 225, 225));
        selectAllC.setText("Select All Crisp");
        selectAllC.addActionListener(new SelectData_selectAllC_actionAdapter(this));

        selectAllC_LQD.setBackground(new Color(225, 225, 225));
        selectAllC_LQD.setText("Select All C_LQD");
        selectAllC_LQD.addActionListener(new SelectData_selectAllC_LQD_actionAdapter(this));

        selectAllLQD_C.setBackground(new Color(225, 225, 225));
        selectAllLQD_C.setText("Select All LQD_C");
        selectAllLQD_C.addActionListener(new SelectData_selectAllLQD_C_actionAdapter(this));



        invertSelection.setBackground(new Color(225, 225, 225));
        invertSelection.setText("Invert");
        invertSelection.addActionListener(new SelectData_invertSelection_actionAdapter(this));


        invertSelectionC.setBackground(new Color(225, 225, 225));
        invertSelectionC.setText("Invert Crisp");
        invertSelectionC.addActionListener(new SelectData_invertSelectionC_actionAdapter(this));

        invertSelectionC_LQD.setBackground(new Color(225, 225, 225));
        invertSelectionC_LQD.setText("Invert C_LQD");
        invertSelectionC_LQD.addActionListener(new SelectData_invertSelectionC_LQD_actionAdapter(this));

        invertSelectionLQD_C.setBackground(new Color(225, 225, 225));
        invertSelectionLQD_C.setText("Invert LQD_C");
        invertSelectionLQD_C.addActionListener(new SelectData_invertSelectionLQD_C_actionAdapter(this));

        selectAllUser.setBackground(new Color(225, 225, 225));
        selectAllUser.setText("Select All");
        selectAllUser.addActionListener(new SelectData_selectAllUser_actionAdapter(this));
        invertSelectionUser.setBackground(new Color(225, 225, 225));
        invertSelectionUser.setText("Invert");
        invertSelectionUser.addActionListener(new SelectData_invertSelectionUser_actionAdapter(this));

    }

    /**
     * Insert a new External Object Description (of a data set) in the list
     * @param ds the new data sets�
     * @param path the path to the data set(s) file(s)
     */
    public void insert(DatasetXML ds, String path) {
        // Inserts a dataset info
        datasetList.add(new ExternalObjectDescription(ds.nameAbr, path, 0));
        datasetXML.add(ds);
    }

    /**
     * Insert a new External Object Description (of a data set) in the list
     * @param ds the new data sets�
     * @param path the path to the data set(s) file(s)
     */
    public void insertC_LQD(DatasetXML ds, String path) {
        // Inserts a dataset info
        datasetListC_LQD.add(new ExternalObjectDescription(ds.nameAbr, path, 0));
        datasetXMLC_LQD.add(ds);
    }

    /**
     * Insert a new External Object Description (of a data set) in the list
     * @param ds the new data sets�
     * @param path the path to the data set(s) file(s)
     */
    public void insertLQD_C(DatasetXML ds, String path) {
        // Inserts a dataset info
        datasetListLQD_C.add(new ExternalObjectDescription(ds.nameAbr, path, 0));
        datasetXMLLQD_C.add(ds);
    }

    /**
     * Insert a new External Object Description (of a data set) in the list
     * @param ds the new data sets�
     * @param path the path to the data set(s) file(s)
     */
    public void insertC(DatasetXML ds, String path) {
        // Inserts a dataset info
        datasetListC.add(new ExternalObjectDescription(ds.nameAbr, path, 0));
        datasetXMLC.add(ds);
    }

    /**
     * Method for saving the selected data sets so we can restore them later
     */
    public void saveSelected() {
        dataActive = new Hashtable<String, Boolean>();

        for (int i = 0; i < checks.size(); i++) {
            dataActive.put(((DatasetXML) datasetXML.elementAt(i)).nameComplete, new Boolean(((JCheckBox) checks.elementAt(i)).isSelected()));
        }

    }

    /**
     * Removes all the data sets from the list
     */
    public void removeAllData() {
        datasetList.removeAllElements();
        datasetXML.removeAllElements();
        datasetListC_LQD.removeAllElements();
        datasetXMLC_LQD.removeAllElements();
        datasetListLQD_C.removeAllElements();
        datasetXMLLQD_C.removeAllElements();
        datasetListC.removeAllElements();
        datasetXMLC.removeAllElements();
        transform();
    }

    /**
     * Test if any of the data sets in the list are selected by their
     * correspondent check button
     * @return True if at least one is selected, false otherwise
     */
    public boolean isAnySelected() {
        // Check that one or more datasets are selected
        for (int i = 0; i < checks.size(); i++) {
            if (((JCheckBox) checks.elementAt(i)).isSelected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Test if any of the data sets in the list are selected by their
     * correspondent check button
     * @return True if at least one is selected, false otherwise
     */
    public boolean isAnySelectedLQD_C() {
        // Check that one or more datasets are selected
        for (int i = 0; i < checksLQD_C.size(); i++) {
            if (((JCheckBox) checksLQD_C.elementAt(i)).isSelected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Test if any of the data sets in the list are selected by their
     * correspondent check button
     * @return True if at least one is selected, false otherwise
     */
    public boolean isAnySelectedC_LQD() {
        // Check that one or more datasets are selected
        for (int i = 0; i < checksC_LQD.size(); i++) {
            if (((JCheckBox) checksC_LQD.elementAt(i)).isSelected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Test if any of the data sets in the list are selected by their
     * correspondent check button
     * @return True if at least one is selected, false otherwise
     */
    public boolean isAnySelectedC() {
        // Check that one or more datasets are selected
        for (int i = 0; i < checksC.size(); i++) {
            if (((JCheckBox) checksC.elementAt(i)).isSelected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clear all the data estructures of this object, and allocates new
     * memory for them
     */
    public void clear() {
        checks = new Vector();
        checksC_LQD = new Vector();
        checksLQD_C = new Vector();
        checksC = new Vector();
        datasetList = new Vector();
        datasetListC = new Vector();
        datasetListC_LQD = new Vector();
        datasetListLQD_C = new Vector();
        actualList = new Vector();
        actualListC = new Vector();
        actualListC_LQD = new Vector();
        actualListLQD_C = new Vector();

        this.removeAll();
        transform();

    }

    /**
     * Reload the data set list, given the experiment type
     * @param type The current experiment type, which determines the data sets to be loaded
     */
    public void reload(int type) {
        componentWidth = parent.datasetsChecksPanel.getWidth() -
                parent.mainSplitPane1.getDividerSize() - 30;
        boolean leftColumn;

        int leftPos, rightPos, align;
        pos = 10;
        int width;
        this.removeAll();
        checks = new Vector();
        this.numberOfUserDataset = 0;

        //        String cad = "classification";
        switch (type) {

            case Experiments.CLASSIFICATION:
                 {
                    cad = "classification";

                    this.cadParent = 0;

                }
                break;
            case Experiments.REGRESSION:
                 {
                    cad = "regression";

                    this.cadParent = 1;

                }
                break;
            case Experiments.UNSUPERVISED: {
                cad = "unsupervised";

                this.cadParent = 2;

                break;
            }

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
        titulo1.setBounds(new Rectangle(10, 2, 200, 16));
        this.add(titulo1);

        //compute the maximum length of the differents data sets names
        computeDatasetsLabelWidth(datasetXML);
        maxWidth *= 1.5;
        //we begin putting the check buttons in the left side always
        leftColumn = true;
        //here we set the alignment from the left side of the panel for our
        //buttons
        leftPos = 15;
        //rightPos = leftPos + check Width + oddWidth + 10 + little offset
        rightPos = 15 + maxWidth + 10;


        for (int i = 0; i < datasetList.size(); i++) {
            File data = new File(((ExternalObjectDescription) datasetList.elementAt(i)).getPath(0));
            try {
                java.net.URL recursoInterno = data.toURI().toURL();
                File f = new File(recursoInterno.getFile());

                //File f = new File( ( (DescObjetoExterno) datasetList.elementAt(i)).getPath(0));

                if (((DatasetXML) datasetXML.elementAt(i)).problemType.toLowerCase().compareTo(cad) == 0 && ((DatasetXML) datasetXML.elementAt(i)).user == false) {
                    //set the align for this data set
                    if (leftColumn) {
                        align = leftPos;
                        width = maxWidth;
                        pos += 20;
                    } else {
                        align = rightPos;
                        width = maxWidth;
                    }
                    //                    leftColumn = !leftColumn;

                    JCheckBox chk = new JCheckBox();
                    chk.setBounds(new Rectangle(align, pos, 25, 16));
                    chk.setOpaque(false);
                    if (parent.objType == parent.LQD) {
                        lqd.add(chk);
                    } else {
                        this.add(chk);
                    }
                    checks.add(chk);
                    JLabel txt = new JLabel(((DatasetXML) datasetXML.elementAt(i)).nameComplete);

                    txt.setBounds(new Rectangle(align + 30, pos, componentWidth - 45, 16));

                    if (parent.objType == parent.LQD) {
                        lqd.add(txt);
                    } else {
                        this.add(txt);
                    }

                    actualList.add(datasetList.elementAt(i));
                }
            } catch (java.net.MalformedURLException ex) {
                ex.printStackTrace();
            }
        }
        pos += 30;


        selectAll.setBounds(new Rectangle(15, pos, 110, 20));
        //        selectAll.setFont(new Font("Arial", Font.PLAIN, 10));
        if (parent.objType == parent.LQD) {
            lqd.add(selectAll);
        } else {
            this.add(selectAll);
        }
        invertSelection.setBounds(new Rectangle(130, pos, 110, 20));
        //        invertSelection.setFont(new Font("Arial", Font.PLAIN, 10));
        if (parent.objType == parent.LQD) {
            lqd.add(invertSelection);
        } else {
            this.add(invertSelection);
        }

        pos += 30;

        JLabel titulo2;
        titulo3.setVisible(false);

        if (parent.objType != parent.LQD) {
            titulo2 = new JLabel("User Datasets");
        } else {
            titulo2 = new JLabel("Transforms of Datasets");
            titulo3.setFont(new Font("Arial", Font.BOLD, 14));
            this.add(titulo3);
            titulo3.setVisible(true);
            lqd.setBounds(10, 25, componentWidth, pos);
            pos = pos + 35;
        }

        titulo2.setFont(new Font("Arial", Font.BOLD, 14));
        titulo2.setBounds(new Rectangle(10, pos, 200, 16));


        this.add(titulo2);
        //user data sets now follows
        leftColumn = true;
        for (int i = 0; i < datasetList.size(); i++) {

            File data = new File(((ExternalObjectDescription) datasetList.elementAt(i)).getPath(0));
            try {
                java.net.URL recursoInterno = data.toURL();
                File f = new File(recursoInterno.getFile());

                //File f = new File( ( (DescObjetoExterno) datasetList.elementAt(i)).getPath(0));

                if (((DatasetXML) datasetXML.elementAt(i)).problemType.toLowerCase().compareTo(cad) == 0 && ((DatasetXML) datasetXML.elementAt(i)).user == true) {
                    //set the align for this data set
                    if (leftColumn) {
                        align = leftPos;
                        width = maxWidth;
                        pos += 20;
                    } else {
                        align = rightPos;
                        width = maxWidth;
                    }
                    //                    leftColumn = !leftColumn;
                    this.numberOfUserDataset++;
                    JCheckBox chk = new JCheckBox();
                    chk.setBounds(new Rectangle(align, pos, 25, 16));
                    chk.setOpaque(false);
                    this.add(chk);
                    checks.add(chk);
                    JLabel txt = new JLabel(((DatasetXML) datasetXML.elementAt(i)).nameComplete);
                    txt.setBounds(new Rectangle(align + 30, pos, componentWidth - 45, 16));
                    this.add(txt);
                    actualList.add(datasetList.elementAt(i));
                }
            } catch (java.net.MalformedURLException ex) {
            }
        }

        pos += 30;

        if (numberOfUserDataset != 0) {
            remove.setBounds(new Rectangle(15, pos, 110, 20));
            this.add(remove);
            importB.setBounds(new Rectangle(130, pos, 110, 20));

            crisp_lqd.setBounds(new Rectangle(15, pos, 150, 20));
            lqd_crisp.setBounds(new Rectangle(15, (pos + 25), 150, 20));
            lqd_crisp.setBounds(new Rectangle(15, (pos + 25), 150, 20));
            titulo3.setBounds(new Rectangle(10, pos + 45, 200, 16));
            crispc.setBounds(new Rectangle(15, pos + 45 + 25, 150, 20));

            this.add(importB);
            if (parent.objType == parent.LQD) {
                this.add(crisp_lqd);
                this.add(lqd_crisp);
                this.add(crispc);
            }
            pos += 25;
            selectAllUser.setBounds(new Rectangle(15, pos, 110, 20));
            this.add(selectAllUser);
            invertSelectionUser.setBounds(new Rectangle(130, pos, 110, 20));
            this.add(invertSelectionUser);
            pos += 30;
        } else {
            importB.setBounds(new Rectangle(15, pos, 90, 20));
            this.add(importB);
            if (parent.objType == parent.LQD) {
                this.add(crisp_lqd);
                this.add(lqd_crisp);
                this.add(crispc);
            }
            crisp_lqd.setBounds(new Rectangle(8, pos, 128, 20));
            lqd_crisp.setBounds(new Rectangle(8 + 139, pos, 128, 20));
            titulo3.setBounds(new Rectangle(10, pos + 45, 200, 16));
            crispc.setBounds(new Rectangle(8, pos + 45 + 25, 150, 20));
            pos += 30;

        }

        parent.checksDatasetsScrollPane.getViewport().setBackground(this.getBackground());
        //this.setPreferredSize(new Dimension(rightPos+(int)1.5*maxWidth+30, pos + 10));
        this.setPreferredSize(new Dimension(componentWidth, pos+20));

        this.repaint();
    }

    /**
     * Once the Buttons has been loaded again, we must set their state
     * as it was previously set by the user <- we take the state saved from saveSelected()
     */
    public void reloadPreviousActiveDataSets() {
        Boolean isActive;
        int i;
        for (int id = 0; id < checks.size(); id++) {
            isActive = dataActive.get(((DatasetXML) datasetXML.elementAt(id)).nameComplete);
            if (isActive != null) {
                ((JCheckBox) checks.elementAt(id)).setSelected(isActive.booleanValue());
            }
        }
    }

    /**
     * scroll control
     */
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
     * Import user data sets button control
     * @param e The action event related to this code
     */
    public void importar_actionPerformed(ActionEvent e) {

        this.saveSelected();

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


        this.reload(this.cadParent);

    }
    /**
     * Reload the data set list, given the experiment type
     * @param contain Panel of the container
     */
    public int reload_crisp_lqd(JPanel contain) {
        //  JOptionPane.showMessageDialog(this, "Nentraaaaaaaaaaaaa 1",
        //            "Invalid number of folds", JOptionPane.ERROR_MESSAGE);
        //We obtain the crisp datasets 
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
                listData = new DatasetXML[datasets.size()];
                for (int i = 0; i < datasets.size(); i++) {
                    listData[i] = new DatasetXML((Element) datasets.get(i));
                    insertC_LQD(listData[i], "/data/");
                }

                sortDatasetC_LQD();
            }
        } catch (java.net.MalformedURLException ex) {
            ex.printStackTrace();
        }


        boolean leftColumn;
        int leftPos, rightPos, align;
        int width;
        actualListC_LQD = new Vector();
        checksC_LQD = new Vector();

        //compute the maximum length of the differents data sets names
        computeDatasetsLabelWidth(datasetXMLC_LQD);
        maxWidth *= 1.5;
        //we begin putting the check buttons in the left side always
        leftColumn = true;
        //here we set the alignment from the left side of the panel for our
        //buttons
        leftPos = 15;
        //rightPos = leftPos + check Width + oddWidth + 10 + little offset
        rightPos = 15 + maxWidth + 10;
        int situation = 0;

        for (int i = 0; i < datasetListC_LQD.size(); i++) {
            File data = new File(((ExternalObjectDescription) datasetListC_LQD.elementAt(i)).getPath(0));
            try {
                java.net.URL recursoInterno = data.toURI().toURL();
                File f = new File(recursoInterno.getFile());

                if (((DatasetXML) datasetXMLC_LQD.elementAt(i)).problemType.toLowerCase().compareTo(cad) == 0 && ((DatasetXML) datasetXMLC_LQD.elementAt(i)).user == false) {
                    //set the align for this data set
                    if (leftColumn) {
                        align = leftPos;
                        width = maxWidth;
                        situation += 20;
                    } else {
                        align = rightPos;
                        width = maxWidth;
                    }
                    //                    leftColumn = !leftColumn;

                    JCheckBox chk = new JCheckBox();
                    chk.setBounds(new Rectangle(align, situation, 25, 16));
                    chk.setOpaque(false);
                    contain.add(chk);
                    checksC_LQD.add(chk);
                    JLabel txt = new JLabel(((DatasetXML) datasetXMLC_LQD.elementAt(i)).nameComplete);

                    txt.setBounds(new Rectangle(align + 30, situation, componentWidth - 45, 16));
                    contain.add(txt);
                    actualListC_LQD.add(datasetListC_LQD.elementAt(i));
                }
            } catch (java.net.MalformedURLException ex) {
                ex.printStackTrace();
            }
        }
        situation += 30;


        selectAllC_LQD.setBounds(new Rectangle(10, situation, 115, 20));
        //        selectAll.setFont(new Font("Arial", Font.PLAIN, 10));
        contain.add(selectAllC_LQD);
        invertSelectionC_LQD.setBounds(new Rectangle(135, situation, 110, 20));
        //        invertSelection.setFont(new Font("Arial", Font.PLAIN, 10));
        contain.add(invertSelectionC_LQD);
        situation += 30;

        return situation;

    }

    /**
     * Reload the data set list
     */
    public int reload_crisp() {

        //  JOptionPane.showMessageDialog(this, "Nentraaaaaaaaaaaaa 1",
        //            "Invalid number of folds", JOptionPane.ERROR_MESSAGE);
        //We obtain the crisp datasets 
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
                listData = new DatasetXML[datasets.size()];
                for (int i = 0; i < datasets.size(); i++) {
                    listData[i] = new DatasetXML((Element) datasets.get(i));
                    insertC(listData[i], "/data/");
                }

                sortDatasetC();
            }
        } catch (java.net.MalformedURLException ex) {
            ex.printStackTrace();
        }


        boolean leftColumn;
        int leftPos, rightPos, align;
        int width;
        actualListC = new Vector();
        checksC = new Vector();

        //compute the maximum length of the differents data sets names
        computeDatasetsLabelWidth(datasetXMLC);
        maxWidth *= 1.5;
        //we begin putting the check buttons in the left side always
        leftColumn = true;
        //here we set the alignment from the left side of the panel for our
        //buttons
        leftPos = 15;
        //rightPos = leftPos + check Width + oddWidth + 10 + little offset
        rightPos = 15 + maxWidth + 10;
        int situation = 0;

        for (int i = 0; i < datasetListC.size(); i++) {
            File data = new File(((ExternalObjectDescription) datasetListC.elementAt(i)).getPath(0));
            try {
                java.net.URL recursoInterno = data.toURI().toURL();
                File f = new File(recursoInterno.getFile());

                if (((DatasetXML) datasetXMLC.elementAt(i)).problemType.toLowerCase().compareTo(cad) == 0 && ((DatasetXML) datasetXMLC.elementAt(i)).user == false) {
                    //set the align for this data set
                    if (leftColumn) {
                        align = leftPos;
                        width = maxWidth;
                        situation += 20;
                    } else {
                        align = rightPos;
                        width = maxWidth;
                    }
                    //                    leftColumn = !leftColumn;

                    JCheckBox chk = new JCheckBox();
                    chk.setBounds(new Rectangle(align, situation, 25, 16));
                    chk.setOpaque(false);
                    crisp.add(chk);
                    checksC.add(chk);
                    JLabel txt = new JLabel(((DatasetXML) datasetXMLC.elementAt(i)).nameComplete);

                    txt.setBounds(new Rectangle(align + 30, situation, componentWidth - 45, 16));
                    crisp.add(txt);
                    actualListC.add(datasetListC.elementAt(i));
                }
            } catch (java.net.MalformedURLException ex) {
                ex.printStackTrace();
            }
        }
        situation += 30;

        selectAllC.setBounds(new Rectangle(10, situation, 115, 20));
        //        selectAll.setFont(new Font("Arial", Font.PLAIN, 10));
        crisp.add(selectAllC);
        invertSelectionC.setBounds(new Rectangle(135, situation, 110, 20));
        //        invertSelection.setFont(new Font("Arial", Font.PLAIN, 10));
        crisp.add(invertSelectionC);
        situation += 30;

        return situation;

    }

    /**
     * Reload the data set list, given the experiment type
     * @param contain The current experiment type, which determines the data sets to be loaded
     */
    public int reload_lqd_crisp(JPanel contain) {
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
                listData = new DatasetXML[datasets.size()];
                for (int i = 0; i < datasets.size(); i++) {
                    listData[i] = new DatasetXML((Element) datasets.get(i));
                    insertLQD_C(listData[i], "/data/");
                }

                sortDatasetLQD_C();
            }
        } catch (java.net.MalformedURLException ex) {
            ex.printStackTrace();
        }


        boolean leftColumn;
        int leftPos, rightPos, align;
        int width;
        actualListLQD_C = new Vector();
        checksLQD_C = new Vector();

        //compute the maximum length of the differents data sets names
        computeDatasetsLabelWidth(datasetXMLC_LQD);
        maxWidth *= 1.5;
        //we begin putting the check buttons in the left side always
        leftColumn = true;
        //here we set the alignment from the left side of the panel for our
        //buttons
        leftPos = 15;
        //rightPos = leftPos + check Width + oddWidth + 10 + little offset
        rightPos = 15 + maxWidth + 10;
        int situation = 0;

        for (int i = 0; i < datasetListLQD_C.size(); i++) {
            File data = new File(((ExternalObjectDescription) datasetListLQD_C.elementAt(i)).getPath(0));
            try {
                java.net.URL recursoInterno = data.toURI().toURL();
                File f = new File(recursoInterno.getFile());

                if (((DatasetXML) datasetXMLLQD_C.elementAt(i)).problemType.toLowerCase().compareTo(cad) == 0 && ((DatasetXML) datasetXMLLQD_C.elementAt(i)).user == false) {
                    //set the align for this data set
                    if (leftColumn) {
                        align = leftPos;
                        width = maxWidth;
                        situation += 20;
                    } else {
                        align = rightPos;
                        width = maxWidth;
                    }
                    //                    leftColumn = !leftColumn;

                    JCheckBox chk = new JCheckBox();
                    chk.setBounds(new Rectangle(align, situation, 25, 16));
                    chk.setOpaque(false);
                    contain.add(chk);
                    checksLQD_C.add(chk);
                    JLabel txt = new JLabel(((DatasetXML) datasetXMLLQD_C.elementAt(i)).nameComplete);

                    txt.setBounds(new Rectangle(align + 30, situation, componentWidth - 45, 16));
                    contain.add(txt);
                    actualListLQD_C.add(datasetListLQD_C.elementAt(i));
                }
            } catch (java.net.MalformedURLException ex) {
                ex.printStackTrace();
            }
        }
        situation += 30;


        selectAllLQD_C.setBounds(new Rectangle(10, situation, 115, 20));
        //        selectAll.setFont(new Font("Arial", Font.PLAIN, 10));
        contain.add(selectAllLQD_C);
        invertSelectionLQD_C.setBounds(new Rectangle(135, situation, 110, 20));
        //        invertSelection.setFont(new Font("Arial", Font.PLAIN, 10));
        contain.add(invertSelectionLQD_C);
        situation += 30;

        return situation;

    }

    /**
     * Crisp to LQD button
     * @param e Event
     */
    public void crisp_lqd_actionPerformed(ActionEvent e) {
        lqdcrisp.setVisible(false);
        lqd_crisp.setBackground(new Color(225, 225, 225));
        titulo3.setBounds(new Rectangle(10, crisp_lqd.getY() + 45, 200, 16));
        crisp.setBounds(new Rectangle(15, titulo3.getY() + 20 + 25, componentWidth, crisp.getHeight()));
        crispc.setBounds(new Rectangle(15, titulo3.getY() + 20, 150, 20));
        if (crisp_lqd.getBackground() == Color.blue) {
            // JOptionPane.showMessageDialog(this, "dice que es azul",
            //     "Invalid number of folds", JOptionPane.ERROR_MESSAGE);
            crisp_lqd.setBackground(new Color(225, 225, 225));
            crisplqd.setVisible(false);
        // lqd_crisp.setBounds(new Rectangle(15+25, crisp_lqd.getY(), 150, 20));
        } else {
            //JOptionPane.showMessageDialog(this, "Nentraaaaaaaaaaaaa no es azul",
            //       "Invalid number of folds", JOptionPane.ERROR_MESSAGE);
            if (pos_initial == 0) {
                this.add(crisplqd);
                pos_initial = pos;
                //crisplqd.setBackground(new Color(112, 154, 209));
                crisplqd.setBackground(new Color(201, 216, 237));
                crisplqd.removeAll();

                org.jdesktop.layout.GroupLayout crisplqdLayout = new org.jdesktop.layout.GroupLayout(crisplqd);
                crisplqd.setLayout(crisplqdLayout);
                datasetListC_LQD = new Vector();
                actualListC_LQD = new Vector();
                datasetXMLC_LQD = new Vector();
                checksC_LQD = new Vector();
                posic_lqd = reload_crisp_lqd(crisplqd);
                crisplqd.setBounds(15, pos_initial, componentWidth, posic_lqd);
            }
            crisplqd.setVisible(true);
            crisp_lqd.setBackground(Color.blue);
            titulo3.setBounds(new Rectangle(10, pos_initial + posic_lqd + 35, 200, 16));
            crisp.setBounds(new Rectangle(15, titulo3.getY() + 20 + 25, componentWidth, crisp.getHeight()));
            crispc.setBounds(new Rectangle(15, titulo3.getY() + 20, 150, 20));

        }
    }

    /**
     * Crisp button
     * @param e Event
     */
    public void crisp_actionPerformed(ActionEvent e) {
        if (crispc.getBackground() == Color.darkGray) {
            crispc.setBackground(new Color(225, 225, 225));
            crisp.setVisible(false);
        } else {
            if (pos_initial_c == 0) {
                this.add(crisp);
                pos_initial_c = pos;
                //crisplqd.setBackground(new Color(112, 154, 209));
                crisp.setBackground(new Color(204, 204, 204));
                crisp.removeAll();

                org.jdesktop.layout.GroupLayout crispl = new org.jdesktop.layout.GroupLayout(crisp);
                crisp.setLayout(crispl);
                datasetListC = new Vector();
                actualListC = new Vector();
                datasetXMLC = new Vector();
                checksC = new Vector();
                posic = reload_crisp();
                crisp.setBounds(15, titulo3.getY() + 25 + 25, componentWidth, posic);
            }
            crisp.setVisible(true);
            crispc.setBackground(Color.darkGray);
        }
    }

    /**
     * LQD to Crisp button
     * @param e Event
     */
    public void lqd_crisp_actionPerformed(ActionEvent e) {
        crisplqd.setVisible(false);
        crisp_lqd.setBackground(new Color(225, 225, 225));
        titulo3.setBounds(new Rectangle(10, crisp_lqd.getY() + 45, 200, 16));
        crisp.setBounds(new Rectangle(15, titulo3.getY() + 20 + 25, componentWidth, crisp.getHeight()));
        crispc.setBounds(new Rectangle(15, titulo3.getY() + 20, 150, 20));
        if (lqd_crisp.getBackground() == Color.yellow) {
            lqd_crisp.setBackground(new Color(225, 225, 225));
            lqdcrisp.setVisible(false);
        // lqd_crisp.setBounds(new Rectangle(15+25, crisp_lqd.getY(), 150, 20));
        } else {

            if (pos_initial_lc == 0) {
                this.add(lqdcrisp);
                pos_initial_lc = pos;
                lqdcrisp.setBackground(new Color(255, 253, 202));
                lqdcrisp.removeAll();

                org.jdesktop.layout.GroupLayout lqdcrispLayout = new org.jdesktop.layout.GroupLayout(lqdcrisp);
                lqdcrisp.setLayout(lqdcrispLayout);
                datasetListLQD_C = new Vector();
                actualListLQD_C = new Vector();
                datasetXMLLQD_C = new Vector();
                checksLQD_C = new Vector();
                posilqd_c = reload_lqd_crisp(lqdcrisp);
                lqdcrisp.setBounds(15, pos_initial_lc, componentWidth, posilqd_c);
            }
            lqdcrisp.setVisible(true);
            lqd_crisp.setBackground(Color.yellow);
            titulo3.setBounds(new Rectangle(10, pos_initial_lc + posilqd_c + 35, 200, 16));
            crisp.setBounds(new Rectangle(15, titulo3.getY() + 20 + 25, componentWidth, crisp.getHeight()));
            crispc.setBounds(new Rectangle(15, titulo3.getY() + 20, 150, 20));

        }
    }

    /**
     * Rmove button
     * @param e Event
     */
    public void remove_actionPerformed(ActionEvent e) {
        boolean anySelected = false;

        for (int i = 0; i < this.actualList.size(); i++) {
            if (((JCheckBox) checks.elementAt(i)).isSelected()) {
                for (int j = 0; j < datasetList.size(); j++) {
                    if (datasetList.elementAt(j).equals(actualList.elementAt(i)) && ((DatasetXML) datasetXML.elementAt(j)).user == true) {
                        //Borrar el dataset de usuario
                        this.deleteFromXML(j);
                        actualList.remove(i);
                        checks.remove(i);
                        String nameData = ((DatasetXML) datasetXML.elementAt(j)).nameAbr;
                        FileUtils.rmdir("./data/" + nameData);
                        datasetList.remove(j);
                        datasetXML.remove(j);
                        this.reload(this.cadParent);
                        this.reloadPreviousActiveDataSets();
                        anySelected = true;
                        i = -1;
                        break;
                    }
                }
            }
        }
        if (anySelected == false) {
            JOptionPane.showMessageDialog(null, "Check one or more user datasets", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Select all button
     * @param e Event
     */
    public void selectAll_actionPerformed(ActionEvent e) {

        for (int i = 0; i < this.actualList.size(); i++) {
            for (int j = 0; j < datasetList.size(); j++) {
                if (datasetList.elementAt(j).equals(actualList.elementAt(i)) && ((DatasetXML) datasetXML.elementAt(j)).user == false) {
                    ((JCheckBox) checks.elementAt(i)).setSelected(true);
                }
            }
        }
    }

    /**
     * Select all button
     * @param e Event
     */
    public void selectAllC_actionPerformed(ActionEvent e) {

        for (int i = 0; i < this.actualListC.size(); i++) {
            for (int j = 0; j < datasetListC.size(); j++) {
                if (datasetListC.elementAt(j).equals(actualListC.elementAt(i)) && ((DatasetXML) datasetXMLC.elementAt(j)).user == false) {
                    ((JCheckBox) checksC.elementAt(i)).setSelected(true);
                }
            }
        }
    }

    /**
     * Select all button
     * @param e Event
     */
    public void selectAllC_LQD_actionPerformed(ActionEvent e) {

        for (int i = 0; i < this.actualListC_LQD.size(); i++) {
            for (int j = 0; j < datasetListC_LQD.size(); j++) {
                if (datasetListC_LQD.elementAt(j).equals(actualListC_LQD.elementAt(i)) && ((DatasetXML) datasetXMLC_LQD.elementAt(j)).user == false) {
                    ((JCheckBox) checksC_LQD.elementAt(i)).setSelected(true);
                }
            }
        }
    }

    /**
     * Select all button
     * @param e Event
     */
    public void selectAllLQD_C_actionPerformed(ActionEvent e) {

        for (int i = 0; i < this.actualListLQD_C.size(); i++) {
            for (int j = 0; j < datasetListLQD_C.size(); j++) {
                if (datasetListLQD_C.elementAt(j).equals(actualListLQD_C.elementAt(i)) && ((DatasetXML) datasetXMLLQD_C.elementAt(j)).user == false) {
                    ((JCheckBox) checksLQD_C.elementAt(i)).setSelected(true);
                }
            }
        }
    }

    /**
     * Invert button
     * @param e Event
     */
    public void invertSelection_actionPerformed(ActionEvent e) {

        for (int i = 0; i < this.actualList.size(); i++) {
            for (int j = 0; j < datasetList.size(); j++) {
                if (datasetList.elementAt(j).equals(actualList.elementAt(i)) && ((DatasetXML) datasetXML.elementAt(j)).user == false) {
                    ((JCheckBox) checks.elementAt(i)).setSelected(!((JCheckBox) checks.elementAt(i)).isSelected());
                }
            }
        }
    }

    /**
     * Invert button
     * @param e Event
     */
    public void invertSelectionC_actionPerformed(ActionEvent e) {

        for (int i = 0; i < this.actualListC.size(); i++) {
            for (int j = 0; j < datasetListC.size(); j++) {
                if (datasetListC.elementAt(j).equals(actualListC.elementAt(i)) && ((DatasetXML) datasetXMLC.elementAt(j)).user == false) {
                    ((JCheckBox) checksC.elementAt(i)).setSelected(!((JCheckBox) checksC.elementAt(i)).isSelected());
                }
            }
        }
    }

    /**
     * Invert button
     * @param e Event
     */
    public void invertSelectionC_LQD_actionPerformed(ActionEvent e) {

        for (int i = 0; i < this.actualListC_LQD.size(); i++) {
            for (int j = 0; j < datasetListC_LQD.size(); j++) {
                if (datasetListC_LQD.elementAt(j).equals(actualListC_LQD.elementAt(i)) && ((DatasetXML) datasetXMLC_LQD.elementAt(j)).user == false) {
                    ((JCheckBox) checksC_LQD.elementAt(i)).setSelected(!((JCheckBox) checksC_LQD.elementAt(i)).isSelected());
                }
            }
        }
    }

    /**
     * Invert button
     * @param e Event
     */
    public void invertSelectionLQD_C_actionPerformed(ActionEvent e) {

        for (int i = 0; i < this.actualListLQD_C.size(); i++) {
            for (int j = 0; j < datasetListLQD_C.size(); j++) {
                if (datasetListLQD_C.elementAt(j).equals(actualListLQD_C.elementAt(i)) && ((DatasetXML) datasetXMLLQD_C.elementAt(j)).user == false) {
                    ((JCheckBox) checksLQD_C.elementAt(i)).setSelected(!((JCheckBox) checksLQD_C.elementAt(i)).isSelected());
                }
            }
        }
    }

    /**
     * Select all button
     * @param e Event
     */
    public void selectAllUser_actionPerformed(ActionEvent e) {
        for (int i = 0; i < this.actualList.size(); i++) {
            for (int j = 0; j < datasetList.size(); j++) {
                if (datasetList.elementAt(j).equals(actualList.elementAt(i)) && ((DatasetXML) datasetXML.elementAt(j)).user == true) {
                    ((JCheckBox) checks.elementAt(i)).setSelected(true);
                }
            }
        }
    }

    /**
     * UnSelect all
     */
    public void deselectAll() {
        for (int i = 0; i < checks.size(); i++) {
            ((JCheckBox) checks.elementAt(i)).setSelected(false);
        }

    }

    /**
     * Invert button
     * @param e Event
     */
    public void invertSelectionUser_actionPerformed(ActionEvent e) {
        for (int i = 0; i < this.actualList.size(); i++) {
            for (int j = 0; j < datasetList.size(); j++) {
                if (datasetList.elementAt(j).equals(actualList.elementAt(i)) && ((DatasetXML) datasetXML.elementAt(j)).user == true) {
                    ((JCheckBox) checks.elementAt(i)).setSelected(!((JCheckBox) checks.elementAt(i)).isSelected());
                }
            }
        }
    }

    /**
     * Delete a data set from the XML file
     * @param index the index of the data set to be deleted
     */
    protected void deleteFromXML(int index) {
        /*Load the previuos datasets.xml*/
        Document data = new Document();
        try {
            SAXBuilder builder = new SAXBuilder();
            data = builder.build("./data/Datasets.xml");
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Dataset specification XML file not found");
            return;
        }

        /* Delete user datasets */
        String datasetName = ((DatasetXML) datasetXML.elementAt(index)).nameAbr;
        java.util.List allChildren = (java.util.List) data.getRootElement().getChildren("dataset");
        // Remove the children
        java.util.List toRemove = new ArrayList();
        for (Object element : allChildren) {
            Element el = (Element) element;
            if (el.getChildText("nameAbr").equals(datasetName)) {
                toRemove.add(element);
            }
        }
        allChildren.removeAll(toRemove);

        try {
            File f = new File("./data/Datasets.xml");
            FileOutputStream file = new FileOutputStream(f);
            XMLOutputter fmt = new XMLOutputter();
            fmt.setFormat(Format.getPrettyFormat());
            fmt.output(data, file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This function sorts the inserted data set lists,
     * so they will appear sorted in the GUI
     */
    public void sortDatasets() {
        //sort the data set list
        Collections.sort(datasetXML);
        Collections.sort(datasetList);
    }

    /**
     * This function sorts the inserted data set lists,
     * so they will appear sorted in the GUI
     */
    public void sortDatasetC_LQD() {
        //sort the data set list
        Collections.sort(datasetXMLC_LQD);
        Collections.sort(datasetListC_LQD);
    }

    /**
     * This function sorts the inserted data set lists,
     * so they will appear sorted in the GUI
     */
    public void sortDatasetLQD_C() {
        //sort the data set list
        Collections.sort(datasetXMLLQD_C);
        Collections.sort(datasetListLQD_C);
    }

    /**
     * This function sorts the inserted data set lists,
     * so they will appear sorted in the GUI
     */
    public void sortDatasetC() {
        //sort the data set list
        Collections.sort(datasetXMLC);
        Collections.sort(datasetListC);
    }

    /**
     * We try to compute the JLabels width in pixels, from the length of
     * the label text in characters...  (not finished)
     */
    protected void computeDatasetsLabelWidth(Vector dataset) {
        int width;

        maxWidth = evenWidth = oddWidth = 0;
        for (int i = 0; i < dataset.size(); i++) {
            if (((DatasetXML) dataset.elementAt(i)).problemType.toLowerCase().compareTo(this.cad) == 0) {
                JLabel txt = new JLabel(((DatasetXML) dataset.elementAt(i)).nameComplete);
                //compute the width of this label from the assigned text
                Dimension d = txt.getPreferredSize();
                width = d.width;
                if (maxWidth < width) {
                    maxWidth = width;
                }
                if (i % 2 == 0 && oddWidth < width) {
                    oddWidth = width;
                }
                if (i % 2 != 0 && evenWidth < width) {
                    evenWidth = width;
                }

            }
        }

    }

    /**
     * Hide import button
     */
    public void hideImportButton() {
        importB.setVisible(false);
    }
}

class SelectData_importar_actionAdapter implements ActionListener {

    private SelectData adaptee;

    SelectData_importar_actionAdapter(SelectData adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.importar_actionPerformed(e);
    }
}

class crisp_lqd_actionAdapter implements ActionListener {

    private SelectData adaptee;

    crisp_lqd_actionAdapter(SelectData adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.crisp_lqd_actionPerformed(e);
    }
}

class lqd_crisp_actionAdapter implements ActionListener {

    private SelectData adaptee;

    lqd_crisp_actionAdapter(SelectData adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.lqd_crisp_actionPerformed(e);
    }
}

class crisp_actionAdapter implements ActionListener {

    private SelectData adaptee;

    crisp_actionAdapter(SelectData adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.crisp_actionPerformed(e);
    }
}

class SelectData_remove_actionAdapter implements ActionListener {

    private SelectData adaptee;

    SelectData_remove_actionAdapter(SelectData adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.remove_actionPerformed(e);
    }
}

class SelectData_selectAll_actionAdapter implements ActionListener {

    private SelectData adaptee;

    SelectData_selectAll_actionAdapter(SelectData adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.selectAll_actionPerformed(e);
    }
}

class SelectData_selectAllC_actionAdapter implements ActionListener {

    private SelectData adaptee;

    SelectData_selectAllC_actionAdapter(SelectData adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.selectAllC_actionPerformed(e);
    }
}

class SelectData_selectAllC_LQD_actionAdapter implements ActionListener {

    private SelectData adaptee;

    SelectData_selectAllC_LQD_actionAdapter(SelectData adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.selectAllC_LQD_actionPerformed(e);
    }
}

class SelectData_selectAllLQD_C_actionAdapter implements ActionListener {

    private SelectData adaptee;

    SelectData_selectAllLQD_C_actionAdapter(SelectData adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.selectAllLQD_C_actionPerformed(e);
    }
}

class SelectData_invertSelection_actionAdapter implements ActionListener {

    private SelectData adaptee;

    SelectData_invertSelection_actionAdapter(SelectData adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.invertSelection_actionPerformed(e);
    }
}

class SelectData_invertSelectionC_actionAdapter implements ActionListener {

    private SelectData adaptee;

    SelectData_invertSelectionC_actionAdapter(SelectData adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.invertSelectionC_actionPerformed(e);
    }
}

class SelectData_invertSelectionC_LQD_actionAdapter implements ActionListener {

    private SelectData adaptee;

    SelectData_invertSelectionC_LQD_actionAdapter(SelectData adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.invertSelectionC_LQD_actionPerformed(e);
    }
}

class SelectData_invertSelectionLQD_C_actionAdapter implements ActionListener {

    private SelectData adaptee;

    SelectData_invertSelectionLQD_C_actionAdapter(SelectData adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.invertSelectionLQD_C_actionPerformed(e);
    }
}

class SelectData_selectAllUser_actionAdapter implements ActionListener {

    private SelectData adaptee;

    SelectData_selectAllUser_actionAdapter(SelectData adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.selectAllUser_actionPerformed(e);
    }
}

class SelectData_invertSelectionUser_actionAdapter implements ActionListener {

    private SelectData adaptee;

    SelectData_invertSelectionUser_actionAdapter(SelectData adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.invertSelectionUser_actionPerformed(e);
    }
}
