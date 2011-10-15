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

/*
 * File: Experiments.java
 *
 * Main class of the experiments module
 *
 * Created on 02-mar-2009, 5:30:48
 * Modified on 12-may-2009
 * @author Ignacio Robles
 * @author Julian Luengo
 * @author Modified by Juan Carlos Fernandez Caballero and Pedro Antonio Gutierrez (University of Córdoba) 7/07/2009
 * @author Modified by Ana Palacios Jimenez and Luciano Sanchez Ramos 23-4-2010 (University of Oviedo)
 * @author Modified by Joaquín Derrac 4-7-2010 (University of Granada)
 */
package keel.GraphInterKeel.experiments;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.io.*;
import javax.swing.event.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.Random;
import java.util.Arrays;
import keel.GraphInterKeel.help.*;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.util.StringTokenizer;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.Marshaller;
import org.xml.sax.InputSource;
import keel.GraphInterKeel.menu.Frame;
import keel.GraphInterKeel.menu.FrameModules;
import javax.imageio.ImageIO;

public class Experiments extends javax.swing.JFrame implements ItemListener, IEducationalRunListener {
    //----------------------------------
    // VARIABLE DECLARATIONS
    //---------------------------------

    Graph experimentGraph = new Graph();
    Vector vector_undo = new Vector();
    Vector vector_redo = new Vector();
    Cursor cursorDraw = Cursor.getDefaultCursor();
    AlgorithmXML listAlgor[] = new AlgorithmXML[1000];
    GraphPanel graphDiagram = new GraphPanel(this, experimentGraph);
    int nListAlgor = 0; // Construct the frame
    int heightHelpPanelSplit = 0;
    SelectExp experimentPartitionsTypeSelection;
    DefaultMutableTreeNode top, top2, top4, top5, top6, top7;
    String lastDirectory;
    int cursorAction;
    ExternalObjectDescription dsc;
    ExternalObjectDescription dscLQD;
    ExternalObjectDescription dscCRISP;
    ExternalObjectDescription dscLQD_C;
    ExternalObjectDescription dscC_LQD;
    ExternalObjectDescription dscC;
    Random rnd;
    public DatasetXML listData[];
    public DatasetXML listDataLQD_C[];
    public DatasetXML listDataC_LQD[];
    public DatasetXML listDataC[];
    DefaultMutableTreeNode node;
    JTree tree;
    //FATHER FRAME
    keel.GraphInterKeel.menu.Frame father;
    //STATIC VARIABLES
    public static final int INVESTIGATION = 0;
    public static final int LQD = 2;
    public static final int TEACHING = 1;
    public static final int IMBALANCED = 3;
    public static final int MULTIINSTANCE = 4;
    public static final int SUBGROUPDISCOVERY = 5;
    static final int CLASSIFICATION = 0;
    static final int REGRESSION = 1;
    static final int UNSUPERVISED = 2;
    static final int PK = 0;
    static final int P5X2 = 1;
    static final int PnoVal = 2;
    //END OF STATIC VARIABLES
    public int heapSize = 512; //- Java performance option variables
    public int numberKFoldCross = 10;
    public boolean notSelectedDataset = true;
    public String root;
    public int objType;
    public int RamaLqd;
    public boolean summary = false;
    protected int expType = 0;
    protected int cvType = 0;
    private String lastPathChosen;
    ;

    //Absolute names array
    private String fullName[];
    private boolean duplicates[];
    public boolean question = true;
    /***************************************************************
     ***************  EDUCATIONAL KEEL  ***************************
     **************************************************************/
    public boolean closedEducationalExecWindow = true;
    EducationalRun ejd = null;

    /***************************************************************
     ***************  EDUCATIONAL KEEL  ***************************
     **************************************************************/
    //---------------------------------
    // END OF VARIABLE DECLARATIONS
    //---------------------------------
    /**
     * Builder
     */
    public Experiments() {
        initComponents();
        //set frame icon
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(Experiments.class.getResource("/keel/GraphInterKeel/resources/ico/logo/logo.gif")));
        //at start, upper and left menus are disabled
        deactivateUpperMenu();
        deactivateLeftMenu();
        startHelpPanel();
    }

    /**
     * Creates a new form, assigning a parent frame (so the experiment windows
     * can be disposed, and the father set visible again on closing)
     * @param parent the frame that will be shown when the Experiments frame is closed
     * @param type Type of experiment
     */
    public Experiments(keel.GraphInterKeel.menu.Frame parent, int type) {
        initComponents();
        subgroupDiscoveryButton.setVisible(false);
        this.father = parent;
        this.root = parent.raiz;
        experimentGraph.objective = type;
        objType = type;
        //set frame icon
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(Experiments.class.getResource("/keel/GraphInterKeel/resources/ico/logo/logo.gif")));
        //at start, upper and left menus are disabled
        deactivateUpperMenu();
        deactivateLeftMenu();
        startHelpPanel();
        //System.out.println("Empieza");
        this.setVisible(true);
        if (objType == LQD) {
            lqd();
        }
        if ((objType == INVESTIGATION) || (objType == SUBGROUPDISCOVERY)) {
            showHelpButton.setEnabled(true);
            selectItem.setEnabled(false);
            insertDataflowItem.setEnabled(false);
            importItem.setEnabled(false);
            snapshotItem.setEnabled(false);
            runExpItem.setEnabled(false);
            seedItem.setEnabled(false);
            executionOptItem.setEnabled(false);
            subgroupDiscoveryButton.setVisible(true);
        }

        if (objType == IMBALANCED) {
            showHelpButton.setEnabled(true);
            selectItem.setEnabled(false);
            insertDataflowItem.setEnabled(false);
            importItem.setEnabled(false);
            snapshotItem.setEnabled(false);
            runExpItem.setEnabled(false);
            seedItem.setEnabled(false);
            executionOptItem.setEnabled(false);
            setTitle("Imbalanced Experiments Design: Off-Line Module");
            loadImbalancedExperiment();
        }

        if (objType == MULTIINSTANCE) {

            showHelpButton.setEnabled(true);
            selectItem.setEnabled(false);
            insertDataflowItem.setEnabled(false);
            importItem.setEnabled(false);
            snapshotItem.setEnabled(false);
            runExpItem.setEnabled(false);
            seedItem.setEnabled(false);
            executionOptItem.setEnabled(false);
            loadMultiInstanceExperiment();
        }
    }

    /**
     * Load a new imbalanced experiment
     */
    private void loadImbalancedExperiment() {
        numberKFoldCross = 5;
        dinDatasets.hideImportButton();
        panelDatasets.hideImportButton();
        ((CardLayout) selectionPanel1.getLayout()).show(selectionPanel1, "datasetsChecksCard");
        status.setText("Select an initial set of dataset and then click on the drawing panel");
        selectButton.setEnabled(true);
        enableMainToolBar(true);

        quicktools.getComponent(quicktools.getComponentCount() - 1).setEnabled(true);

        helpContent.muestraURL(this.getClass().getResource("/contextualHelp/data_set_exp.html"));

        this.expType = Experiments.CLASSIFICATION;
        cvType = Experiments.PK;

        //we want to prevent that this panel will ever show again
        initialPanel1.setVisible(false);
        //now, we load the datasets and the different methods
        undoButton.setEnabled(false);
        redoButton.setEnabled(false);
        continueExperimentGeneration();
        ((CardLayout) selectionPanel1.getLayout()).show(selectionPanel1, "datasetsChecksPanel");
        deleteItem.setEnabled(false);

    }

    /**
     * Load a new multiinstance experiment
     */
    private void loadMultiInstanceExperiment() {
        numberKFoldCross = 10;
        ((CardLayout) selectionPanel1.getLayout()).show(selectionPanel1, "datasetsChecksCard");
        status.setText("Select an initial set of dataset and then click on the drawing panel");
        selectButton.setEnabled(true);
        enableMainToolBar(true);

        quicktools.getComponent(quicktools.getComponentCount() - 1).setEnabled(true);

        helpContent.muestraURL(this.getClass().getResource("/contextualHelp/data_set_exp.html"));

        this.expType = Experiments.CLASSIFICATION;
        cvType = Experiments.PK;

        //we want to prevent that this panel will ever show again
        initialPanel1.setVisible(false);
        //now, we load the datasets and the different methods
        undoButton.setEnabled(false);
        redoButton.setEnabled(false);
        continueExperimentGeneration();
        ((CardLayout) selectionPanel1.getLayout()).show(selectionPanel1, "datasetsChecksPanel");
        deleteItem.setEnabled(false);

        selectPreprocessMethods.setVisible(false);
        selectPostprocessMethods.setVisible(false);

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        partitionGroup = new javax.swing.ButtonGroup();
        mainPanel = new javax.swing.JPanel();

        quicktools = new javax.swing.JToolBar();
        newButton = new javax.swing.JButton();
        openButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        separator1 = new javax.swing.JToolBar.Separator();
        runButton = new javax.swing.JButton();
        separator2 = new javax.swing.JToolBar.Separator();
        undoButton = new javax.swing.JButton();
        redoButton = new javax.swing.JButton();
        separator3 = new javax.swing.JToolBar.Separator();
        deleteItem = new javax.swing.JButton();
        selectButton = new javax.swing.JButton();
        separator4 = new javax.swing.JToolBar.Separator();
        showHelpButton = new javax.swing.JButton();
        showAlgButton = new javax.swing.JButton();
        separator5 = new javax.swing.JToolBar.Separator();
        snapshotButton = new javax.swing.JButton();
        divider1 = new javax.swing.JSplitPane();
        mainSplitPane1 = new javax.swing.JSplitPane();
        leftPanel1 = new javax.swing.JPanel();
        mainToolBar1 = new javax.swing.JToolBar();
        selecDatasets = new javax.swing.JButton();
        selectPreprocessMethods = new javax.swing.JButton();
        selectMethods = new javax.swing.JButton();
        selectPostprocessMethods = new javax.swing.JButton();
        selectTestMethods = new javax.swing.JButton();
        selectVisualizeMethods = new javax.swing.JButton();
        cursorFlux = new javax.swing.JButton();
        selectionPanel1 = new javax.swing.JPanel();
        initialPanel1 = new javax.swing.JPanel();
        partitionPanel1 = new javax.swing.JPanel();
        withoutButton = new javax.swing.JRadioButton();
        fivetwoButton = new javax.swing.JRadioButton();
        kfoldButton = new javax.swing.JRadioButton();
        kValueField = new javax.swing.JTextField();
        partitionsLabel = new javax.swing.JLabel();
        experimentPanel = new javax.swing.JPanel();
        classificationButton = new javax.swing.JButton();
        regressionButton = new javax.swing.JButton();
        unsupervisedButton = new javax.swing.JButton();
        experimentLabel = new javax.swing.JLabel();
        subgroupDiscoveryButton = new javax.swing.JButton();
        methodsPanel = new javax.swing.JPanel();
        methodsScrollPanel = new javax.swing.JScrollPane();
        methodsSelectionTree = new javax.swing.JTree();
        dinDatasetsPanel = new javax.swing.JPanel();
        dinDatasetsScrollPane = new javax.swing.JScrollPane();
        dinDatasets = new keel.GraphInterKeel.experiments.DinamicDataset(this);
        datasetsChecksPanel = new javax.swing.JPanel();
        checksDatasetsScrollPane = new javax.swing.JScrollPane();
        panelDatasets = new keel.GraphInterKeel.experiments.SelectData(this);
        regressionPanel = new javax.swing.JPanel();
        keelLabel2 = new javax.swing.JLabel();
        keelLabel3 = new javax.swing.JLabel();
        datasetsLabel1 = new javax.swing.JLabel();
        keelLabel6 = new javax.swing.JLabel();
        dailyElectricCheck = new javax.swing.JCheckBox();
        ele1Check = new javax.swing.JCheckBox();
        friedmanCheck = new javax.swing.JCheckBox();
        machineCpuCheck = new javax.swing.JCheckBox();
        selectAll2Button = new javax.swing.JButton();
        invert2Button = new javax.swing.JButton();
        keelLabel7 = new javax.swing.JLabel();
        importButton1 = new javax.swing.JButton();
        unsupervisedPanel = new javax.swing.JPanel();
        datasetsLabel2 = new javax.swing.JLabel();
        keelLabel4 = new javax.swing.JLabel();
        weatherCheck = new javax.swing.JCheckBox();
        selectAll2Button1 = new javax.swing.JButton();
        invert2Button1 = new javax.swing.JButton();
        keelLabel5 = new javax.swing.JLabel();
        importButton2 = new javax.swing.JButton();
        postprocessPanel = new javax.swing.JPanel();
        postprocessScroll = new javax.swing.JScrollPane();
        postprocessSelectionTree = new javax.swing.JTree();
        testPanel = new javax.swing.JPanel();
        testScroll = new javax.swing.JScrollPane();
        testSelectionTree = new javax.swing.JTree();
        visualizePanel = new javax.swing.JPanel();
        visualizeScroll = new javax.swing.JScrollPane();
        visualizeSelectionTree = new javax.swing.JTree();
        preprocessPanel = new javax.swing.JPanel();
        preprocessScroll = new javax.swing.JScrollPane();
        preprocessTree = new javax.swing.JTree();
        graphDiagramINNER = new keel.GraphInterKeel.experiments.GraphPanel(this,experimentGraph);
        helpUseCaseTabbedPanel = new javax.swing.JTabbedPane();
        useCaseScrollPane = new javax.swing.JScrollPane();
        useCaseTextArea = new javax.swing.JTextArea();
        helpContent = new keel.GraphInterKeel.help.HelpContent();
        status = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        newExpItem = new javax.swing.JMenuItem();
        loadExpItem = new javax.swing.JMenuItem();
        fileTopSeparator = new javax.swing.JSeparator();
        saveExpItem = new javax.swing.JMenuItem();
        saveAsExpItem = new javax.swing.JMenuItem();
        fileBottomSeparator = new javax.swing.JSeparator();
        viewMenu = new javax.swing.JMenu();
        statusBarItem = new javax.swing.JCheckBoxMenuItem();
        gridItem = new javax.swing.JCheckBoxMenuItem();
        helpPanelItem = new javax.swing.JCheckBoxMenuItem();
        datasetsItem = new javax.swing.JCheckBoxMenuItem();
        editMenu = new javax.swing.JMenu();
        undoItem = new javax.swing.JMenuItem();
        redoItem = new javax.swing.JMenuItem();
        selectItem = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        insertDataflowItem = new javax.swing.JMenuItem();
        toolsTopSeparator = new javax.swing.JSeparator();
        importItem = new javax.swing.JMenuItem();
        toolsBottomSeparator = new javax.swing.JSeparator();
        snapshotItem = new javax.swing.JMenuItem();
        runExpItem = new javax.swing.JMenuItem();
        seedItem = new javax.swing.JMenuItem();
        executionOptItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentItem = new javax.swing.JMenuItem();
        aboutItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Experiments Design: Off-Line Module");
        setForeground(new java.awt.Color(255, 255, 255));

        mainPanel.setName("mainPanel"); // NOI18N

        quicktools.setFloatable(false);
        quicktools.setRollover(true);
        quicktools.setName("quicktools"); // NOI18N

        newButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/new.png"))); // NOI18N
        newButton.setToolTipText("Create a new experiment");
        newButton.setBorderPainted(false);
        newButton.setFocusable(false);
        newButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newButton.setName("newButton"); // NOI18N
        newButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });
        quicktools.add(newButton);

        openButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/open.png"))); // NOI18N
        openButton.setToolTipText("Open an existing experiment");
        openButton.setBorderPainted(false);
        openButton.setFocusable(false);
        openButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openButton.setName("openButton"); // NOI18N
        openButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });
        quicktools.add(openButton);

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/save.png"))); // NOI18N
        saveButton.setToolTipText("Save the current experiment");
        saveButton.setBorderPainted(false);
        saveButton.setEnabled(false);
        saveButton.setFocusable(false);
        saveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveButton.setName("saveButton"); // NOI18N
        saveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        quicktools.add(saveButton);

        separator1.setName("separator1"); // NOI18N
        quicktools.add(separator1);

        runButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/run.png"))); // NOI18N
        runButton.setToolTipText("Run Experiment");
        runButton.setBorderPainted(false);
        runButton.setFocusable(false);
        runButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        runButton.setName("runButton"); // NOI18N
        runButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        quicktools.add(runButton);

        separator2.setName("separator2"); // NOI18N
        quicktools.add(separator2);

        undoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/undo.png"))); // NOI18N
        undoButton.setToolTipText("Undo");
        undoButton.setBorderPainted(false);
        undoButton.setEnabled(false);
        undoButton.setFocusable(false);
        undoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        undoButton.setName("undoButton"); // NOI18N
        undoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        undoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoButtonActionPerformed(evt);
            }
        });
        quicktools.add(undoButton);

        redoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/redo.png"))); // NOI18N
        redoButton.setToolTipText("Redo");
        redoButton.setBorderPainted(false);
        redoButton.setEnabled(false);
        redoButton.setFocusable(false);
        redoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        redoButton.setName("redoButton"); // NOI18N
        redoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        redoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoButtonActionPerformed(evt);
            }
        });
        quicktools.add(redoButton);

        separator3.setName("separator3"); // NOI18N
        quicktools.add(separator3);

        deleteItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/editdelete.png"))); // NOI18N
        deleteItem.setToolTipText("Delete selected item");
        deleteItem.setBorderPainted(false);
        deleteItem.setEnabled(false);
        deleteItem.setFocusable(false);
        deleteItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteItem.setName("deleteItem"); // NOI18N
        deleteItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        deleteItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteItemActionPerformed(evt);
            }
        });
        quicktools.add(deleteItem);

        selectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/select.png"))); // NOI18N
        selectButton.setToolTipText("Select");
        selectButton.setBorderPainted(false);
        selectButton.setFocusable(false);
        selectButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectButton.setName("selectButton"); // NOI18N
        selectButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        selectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButtonActionPerformed(evt);
            }
        });
        quicktools.add(selectButton);

        separator4.setName("separator4"); // NOI18N
        quicktools.add(separator4);

        showHelpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/viewhelp.png"))); // NOI18N
        showHelpButton.setToolTipText("Show help");
        showHelpButton.setBorderPainted(false);
        showHelpButton.setFocusable(false);
        showHelpButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        showHelpButton.setName("showHelpButton"); // NOI18N
        showHelpButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        showHelpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showHelpButtonActionPerformed(evt);
            }
        });
        quicktools.add(showHelpButton);

        showAlgButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/showalg.png"))); // NOI18N
        showAlgButton.setToolTipText("Show/Hide Datasets and Algorithms panel");
        showAlgButton.setBorderPainted(false);
        showAlgButton.setFocusable(false);
        showAlgButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        showAlgButton.setName("showAlgButton"); // NOI18N
        showAlgButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        showAlgButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAlgButtonActionPerformed(evt);
            }
        });
        quicktools.add(showAlgButton);

        separator5.setName("separator5"); // NOI18N
        quicktools.add(separator5);

        snapshotButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/snapshot.png"))); // NOI18N
        snapshotButton.setToolTipText("Take a photo from the current experiment");
        snapshotButton.setBorderPainted(false);
        snapshotButton.setFocusable(false);
        snapshotButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        snapshotButton.setName("snapshotButton"); // NOI18N
        snapshotButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        snapshotButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                snapshotButtonActionPerformed(evt);
            }
        });
        quicktools.add(snapshotButton);

        divider1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        divider1.setName("divider1"); // NOI18N

        mainSplitPane1.setDividerLocation(360);
        mainSplitPane1.setName("mainSplitPane1"); // NOI18N

        leftPanel1.setName("leftPanel1"); // NOI18N

        mainToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        mainToolBar1.setFloatable(false);
        mainToolBar1.setOrientation(1);
        mainToolBar1.setRollover(true);
        mainToolBar1.setName("mainToolBar1"); // NOI18N

        selecDatasets.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/selectdataset.png"))); // NOI18N
        selecDatasets.setBorderPainted(false);
        selecDatasets.setEnabled(false);
        selecDatasets.setFocusable(false);
        selecDatasets.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selecDatasets.setName("selecDatasets"); // NOI18N
        selecDatasets.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        selecDatasets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selecDatasetsActionPerformed(evt);
            }
        });
        mainToolBar1.add(selecDatasets);

        selectPreprocessMethods.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/preproc.gif"))); // NOI18N
        selectPreprocessMethods.setBorderPainted(false);
        selectPreprocessMethods.setFocusable(false);
        selectPreprocessMethods.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectPreprocessMethods.setName("selectPreprocessMethods"); // NOI18N
        selectPreprocessMethods.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        selectPreprocessMethods.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectPreprocessMethodsActionPerformed(evt);
            }
        });
        mainToolBar1.add(selectPreprocessMethods);

        selectMethods.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/method.gif"))); // NOI18N
        selectMethods.setBorderPainted(false);
        selectMethods.setFocusable(false);
        selectMethods.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectMethods.setName("selectMethods"); // NOI18N
        selectMethods.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        selectMethods.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectMet_actionPerformed(evt);
            }
        });
        mainToolBar1.add(selectMethods);

        selectPostprocessMethods.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/postproc.gif"))); // NOI18N
        selectPostprocessMethods.setBorderPainted(false);
        selectPostprocessMethods.setFocusable(false);
        selectPostprocessMethods.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectPostprocessMethods.setName("selectPostprocessMethods"); // NOI18N
        selectPostprocessMethods.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        selectPostprocessMethods.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectPostprocessMethodsActionPerformed(evt);
            }
        });
        mainToolBar1.add(selectPostprocessMethods);

        selectTestMethods.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/formula.png"))); // NOI18N
        selectTestMethods.setBorderPainted(false);
        selectTestMethods.setFocusable(false);
        selectTestMethods.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectTestMethods.setName("selectTestMethods"); // NOI18N
        selectTestMethods.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        selectTestMethods.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectTestMethodsActionPerformed(evt);
            }
        });
        mainToolBar1.add(selectTestMethods);

        selectVisualizeMethods.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/table.png"))); // NOI18N
        selectVisualizeMethods.setBorderPainted(false);
        selectVisualizeMethods.setFocusable(false);
        selectVisualizeMethods.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectVisualizeMethods.setName("selectVisualizeMethods"); // NOI18N
        selectVisualizeMethods.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        selectVisualizeMethods.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectVisualizeMethodsActionPerformed(evt);
            }
        });
        mainToolBar1.add(selectVisualizeMethods);

        cursorFlux.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/next.png"))); // NOI18N
        cursorFlux.setBorderPainted(false);
        cursorFlux.setFocusable(false);
        cursorFlux.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cursorFlux.setName("cursorFlux"); // NOI18N
        cursorFlux.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cursorFlux.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cursorFluxActionPerformed(evt);
            }
        });
        mainToolBar1.add(cursorFlux);

        selectionPanel1.setName("selectionPanel1"); // NOI18N
        selectionPanel1.setLayout(new java.awt.CardLayout());

        initialPanel1.setName("initialPanel1"); // NOI18N

        partitionPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        partitionPanel1.setName("partitionPanel1"); // NOI18N

        partitionGroup.add(withoutButton);
        withoutButton.setText("without validation");
        withoutButton.setName("withoutButton"); // NOI18N

        partitionGroup.add(fivetwoButton);
        fivetwoButton.setText("5x2 cross validation");
        fivetwoButton.setName("fivetwoButton"); // NOI18N

        partitionGroup.add(kfoldButton);
        kfoldButton.setSelected(true);
        kfoldButton.setText("k-fold cross validation");
        kfoldButton.setName("kfoldButton"); // NOI18N
        kfoldButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                kfoldButtonItemStateChanged(evt);
            }
        });

        kValueField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        kValueField.setText("10");
        kValueField.setName("kValueField"); // NOI18N

        partitionsLabel.setBackground(new java.awt.Color(102, 102, 102));
        partitionsLabel.setForeground(new java.awt.Color(255, 255, 255));
        partitionsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        partitionsLabel.setText("Type of partitions");
        partitionsLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        partitionsLabel.setName("partitionsLabel"); // NOI18N
        partitionsLabel.setOpaque(true);

        org.jdesktop.layout.GroupLayout partitionPanel1Layout = new org.jdesktop.layout.GroupLayout(partitionPanel1);
        partitionPanel1.setLayout(partitionPanel1Layout);
        partitionPanel1Layout.setHorizontalGroup(
            partitionPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, partitionsLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
            .add(partitionPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(kfoldButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(kValueField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(124, Short.MAX_VALUE))
            .add(partitionPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(fivetwoButton)
                .addContainerGap(179, Short.MAX_VALUE))
            .add(partitionPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(withoutButton)
                .addContainerGap(189, Short.MAX_VALUE))
        );
        partitionPanel1Layout.setVerticalGroup(
            partitionPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(partitionPanel1Layout.createSequentialGroup()
                .add(partitionsLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(partitionPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(kfoldButton)
                    .add(kValueField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fivetwoButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(withoutButton)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        experimentPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        experimentPanel.setName("experimentPanel"); // NOI18N

        classificationButton.setText("Classification");
        classificationButton.setName("classificationButton"); // NOI18N
        classificationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                classificationButtonActionPerformed(evt);
            }
        });

        regressionButton.setText("Regression");
        regressionButton.setName("regressionButton"); // NOI18N
        regressionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regressionButtonActionPerformed(evt);
            }
        });

        unsupervisedButton.setText("Unsupervised Learning");
        unsupervisedButton.setName("unsupervisedButton"); // NOI18N
        unsupervisedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unsupervisedButtonActionPerformed(evt);
            }
        });

        experimentLabel.setBackground(new java.awt.Color(102, 102, 102));
        experimentLabel.setForeground(new java.awt.Color(255, 255, 255));
        experimentLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        experimentLabel.setText("Type of the experiment");
        experimentLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        experimentLabel.setName("experimentLabel"); // NOI18N
        experimentLabel.setOpaque(true);

        subgroupDiscoveryButton.setText("Subgroup Discovery");
        subgroupDiscoveryButton.setName("subgroupDiscoveryButton"); // NOI18N
        subgroupDiscoveryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subgroupDiscoveryButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout experimentPanelLayout = new org.jdesktop.layout.GroupLayout(experimentPanel);
        experimentPanel.setLayout(experimentPanelLayout);
        experimentPanelLayout.setHorizontalGroup(
            experimentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(experimentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(classificationButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, experimentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(regressionButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, experimentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(unsupervisedButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addContainerGap())
            .add(experimentLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, experimentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(subgroupDiscoveryButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addContainerGap())
        );
        experimentPanelLayout.setVerticalGroup(
            experimentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(experimentPanelLayout.createSequentialGroup()
                .add(experimentLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(classificationButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(regressionButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(unsupervisedButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(subgroupDiscoveryButton)
                .addContainerGap(79, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout initialPanel1Layout = new org.jdesktop.layout.GroupLayout(initialPanel1);
        initialPanel1.setLayout(initialPanel1Layout);
        initialPanel1Layout.setHorizontalGroup(
            initialPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(partitionPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(experimentPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        initialPanel1Layout.setVerticalGroup(
            initialPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(initialPanel1Layout.createSequentialGroup()
                .add(partitionPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(experimentPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        selectionPanel1.add(initialPanel1, "initialCard");

        methodsPanel.setName("methodsPanel"); // NOI18N

        methodsScrollPanel.setName("methodsScrollPanel"); // NOI18N

        methodsSelectionTree.setName("methodsSelectionTree"); // NOI18N
        methodsSelectionTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                methodSelectionTree_valueChanged(evt);
            }
        });
        methodsScrollPanel.setViewportView(methodsSelectionTree);

        org.jdesktop.layout.GroupLayout methodsPanelLayout = new org.jdesktop.layout.GroupLayout(methodsPanel);
        methodsPanel.setLayout(methodsPanelLayout);
        methodsPanelLayout.setHorizontalGroup(
            methodsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(methodsScrollPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
        );
        methodsPanelLayout.setVerticalGroup(
            methodsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(methodsScrollPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
        );

        selectionPanel1.add(methodsPanel, "methodsCard");

        dinDatasetsPanel.setAutoscrolls(true);
        dinDatasetsPanel.setName("dinDatasetsPanel"); // NOI18N
        dinDatasetsPanel.setLayout(new java.awt.BorderLayout());

        dinDatasetsScrollPane.setName("dinDatasetsScrollPane"); // NOI18N

        dinDatasets.setName("dinDatasets"); // NOI18N

        org.jdesktop.layout.GroupLayout dinDatasetsLayout = new org.jdesktop.layout.GroupLayout(dinDatasets);
        dinDatasets.setLayout(dinDatasetsLayout);
        dinDatasetsLayout.setHorizontalGroup(
            dinDatasetsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );
        dinDatasetsLayout.setVerticalGroup(
            dinDatasetsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );

        dinDatasetsScrollPane.setViewportView(dinDatasets);

        dinDatasetsPanel.add(dinDatasetsScrollPane, java.awt.BorderLayout.CENTER);

        selectionPanel1.add(dinDatasetsPanel, "dinDatasetsCard");

        datasetsChecksPanel.setAutoscrolls(true);
        datasetsChecksPanel.setName("datasetsChecksPanel"); // NOI18N
        datasetsChecksPanel.setLayout(new java.awt.BorderLayout());

        checksDatasetsScrollPane.setName("checksDatasetsScrollPane"); // NOI18N
        checksDatasetsScrollPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                checksDatasetsScrollPaneComponentResized(evt);
            }
        });

        panelDatasets.setName("panelDatasets"); // NOI18N

        org.jdesktop.layout.GroupLayout panelDatasetsLayout = new org.jdesktop.layout.GroupLayout(panelDatasets);
        panelDatasets.setLayout(panelDatasetsLayout);
        panelDatasetsLayout.setHorizontalGroup(
            panelDatasetsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );
        panelDatasetsLayout.setVerticalGroup(
            panelDatasetsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );

        checksDatasetsScrollPane.setViewportView(panelDatasets);

        datasetsChecksPanel.add(checksDatasetsScrollPane, java.awt.BorderLayout.CENTER);

        selectionPanel1.add(datasetsChecksPanel, "datasetsChecksCard");

        regressionPanel.setName("regressionPanel"); // NOI18N

        keelLabel2.setName("keelLabel2"); // NOI18N

        keelLabel3.setName("keelLabel3"); // NOI18N

        datasetsLabel1.setBackground(new java.awt.Color(102, 102, 102));
        datasetsLabel1.setForeground(new java.awt.Color(255, 255, 255));
        datasetsLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        datasetsLabel1.setText("Datasets");
        datasetsLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        datasetsLabel1.setName("datasetsLabel1"); // NOI18N
        datasetsLabel1.setOpaque(true);

        keelLabel6.setFont(new java.awt.Font("Lucida Grande 13 12 12", 1, 12));
        keelLabel6.setText("KEEL Datasets");
        keelLabel6.setName("keelLabel6"); // NOI18N

        dailyElectricCheck.setText("Daily Electric Energy");
        dailyElectricCheck.setName("dailyElectricCheck"); // NOI18N

        ele1Check.setText("Ele1");
        ele1Check.setName("ele1Check"); // NOI18N

        friedmanCheck.setText("Friedman");
        friedmanCheck.setName("friedmanCheck"); // NOI18N

        machineCpuCheck.setText("Machine CPU");
        machineCpuCheck.setName("machineCpuCheck"); // NOI18N

        selectAll2Button.setText("Select All");
        selectAll2Button.setName("selectAll2Button"); // NOI18N

        invert2Button.setText("Invert Selection");
        invert2Button.setName("invert2Button"); // NOI18N

        keelLabel7.setFont(new java.awt.Font("Lucida Grande 13 12 12 12", 1, 12));
        keelLabel7.setText("User Datasets");
        keelLabel7.setName("keelLabel7"); // NOI18N

        importButton1.setText("Import");
        importButton1.setName("importButton1"); // NOI18N

        org.jdesktop.layout.GroupLayout regressionPanelLayout = new org.jdesktop.layout.GroupLayout(regressionPanel);
        regressionPanel.setLayout(regressionPanelLayout);
        regressionPanelLayout.setHorizontalGroup(
            regressionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(regressionPanelLayout.createSequentialGroup()
                .add(regressionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(regressionPanelLayout.createSequentialGroup()
                        .add(24, 24, 24)
                        .add(keelLabel2))
                    .add(regressionPanelLayout.createSequentialGroup()
                        .add(29, 29, 29)
                        .add(keelLabel3))
                    .add(regressionPanelLayout.createSequentialGroup()
                        .add(24, 24, 24)
                        .add(regressionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(keelLabel6)
                            .add(regressionPanelLayout.createSequentialGroup()
                                .add(17, 17, 17)
                                .add(regressionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(ele1Check)
                                    .add(dailyElectricCheck)
                                    .add(friedmanCheck)
                                    .add(machineCpuCheck)))))
                    .add(regressionPanelLayout.createSequentialGroup()
                        .add(29, 29, 29)
                        .add(regressionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(keelLabel7)
                            .add(regressionPanelLayout.createSequentialGroup()
                                .add(73, 73, 73)
                                .add(importButton1))
                            .add(regressionPanelLayout.createSequentialGroup()
                                .add(selectAll2Button)
                                .add(18, 18, 18)
                                .add(invert2Button)))))
                .addContainerGap())
            .add(datasetsLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
        );
        regressionPanelLayout.setVerticalGroup(
            regressionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(regressionPanelLayout.createSequentialGroup()
                .add(datasetsLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(keelLabel6)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dailyElectricCheck)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ele1Check)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(friedmanCheck)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(machineCpuCheck)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(regressionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(selectAll2Button)
                    .add(invert2Button))
                .add(18, 18, 18)
                .add(keelLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 17, Short.MAX_VALUE)
                .add(importButton1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(keelLabel2)
                .add(149, 149, 149)
                .add(keelLabel3))
        );

        selectionPanel1.add(regressionPanel, "regressionCard");

        unsupervisedPanel.setName("unsupervisedPanel"); // NOI18N

        datasetsLabel2.setBackground(new java.awt.Color(102, 102, 102));
        datasetsLabel2.setForeground(new java.awt.Color(255, 255, 255));
        datasetsLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        datasetsLabel2.setText("Datasets");
        datasetsLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        datasetsLabel2.setName("datasetsLabel2"); // NOI18N
        datasetsLabel2.setOpaque(true);

        keelLabel4.setFont(new java.awt.Font("Lucida Grande 13 12 12 12", 1, 12));
        keelLabel4.setText("KEEL Datasets");
        keelLabel4.setName("keelLabel4"); // NOI18N

        weatherCheck.setText("Weather");
        weatherCheck.setName("weatherCheck"); // NOI18N

        selectAll2Button1.setText("Select All");
        selectAll2Button1.setName("selectAll2Button1"); // NOI18N

        invert2Button1.setText("Invert Selection");
        invert2Button1.setName("invert2Button1"); // NOI18N

        keelLabel5.setFont(new java.awt.Font("Lucida Grande 13 12 12 12 12", 1, 12));
        keelLabel5.setText("User Datasets");
        keelLabel5.setName("keelLabel5"); // NOI18N

        importButton2.setText("Import");
        importButton2.setName("importButton2"); // NOI18N

        org.jdesktop.layout.GroupLayout unsupervisedPanelLayout = new org.jdesktop.layout.GroupLayout(unsupervisedPanel);
        unsupervisedPanel.setLayout(unsupervisedPanelLayout);
        unsupervisedPanelLayout.setHorizontalGroup(
            unsupervisedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(unsupervisedPanelLayout.createSequentialGroup()
                .add(unsupervisedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(unsupervisedPanelLayout.createSequentialGroup()
                        .add(24, 24, 24)
                        .add(unsupervisedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(keelLabel4)
                            .add(unsupervisedPanelLayout.createSequentialGroup()
                                .add(17, 17, 17)
                                .add(weatherCheck))))
                    .add(unsupervisedPanelLayout.createSequentialGroup()
                        .add(29, 29, 29)
                        .add(unsupervisedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(keelLabel5)
                            .add(unsupervisedPanelLayout.createSequentialGroup()
                                .add(selectAll2Button1)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(invert2Button1)))))
                .addContainerGap(91, Short.MAX_VALUE))
            .add(datasetsLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
            .add(unsupervisedPanelLayout.createSequentialGroup()
                .add(97, 97, 97)
                .add(importButton2)
                .addContainerGap(148, Short.MAX_VALUE))
        );
        unsupervisedPanelLayout.setVerticalGroup(
            unsupervisedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(unsupervisedPanelLayout.createSequentialGroup()
                .add(datasetsLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(keelLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(weatherCheck)
                .add(18, 18, 18)
                .add(unsupervisedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(selectAll2Button1)
                    .add(invert2Button1))
                .add(64, 64, 64)
                .add(keelLabel5)
                .add(37, 37, 37)
                .add(importButton2)
                .addContainerGap(147, Short.MAX_VALUE))
        );

        selectionPanel1.add(unsupervisedPanel, "unsupervisedCard");

        postprocessPanel.setName("postprocessPanel"); // NOI18N

        postprocessScroll.setName("postprocessScroll"); // NOI18N

        postprocessSelectionTree.setName("postprocessSelectionTree"); // NOI18N
        postprocessSelectionTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                postprocessSelectionTree_valueChanged(evt);
            }
        });
        postprocessScroll.setViewportView(postprocessSelectionTree);

        org.jdesktop.layout.GroupLayout postprocessPanelLayout = new org.jdesktop.layout.GroupLayout(postprocessPanel);
        postprocessPanel.setLayout(postprocessPanelLayout);
        postprocessPanelLayout.setHorizontalGroup(
            postprocessPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(postprocessScroll, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
        );
        postprocessPanelLayout.setVerticalGroup(
            postprocessPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(postprocessScroll, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
        );

        selectionPanel1.add(postprocessPanel, "postprocessCard");

        testPanel.setName("testPanel"); // NOI18N

        testScroll.setName("testScroll"); // NOI18N

        testSelectionTree.setName("testSelectionTree"); // NOI18N
        testSelectionTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                testSelection_valueChanged(evt);
            }
        });
        testScroll.setViewportView(testSelectionTree);

        org.jdesktop.layout.GroupLayout testPanelLayout = new org.jdesktop.layout.GroupLayout(testPanel);
        testPanel.setLayout(testPanelLayout);
        testPanelLayout.setHorizontalGroup(
            testPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(testScroll, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
        );
        testPanelLayout.setVerticalGroup(
            testPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(testScroll, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
        );

        selectionPanel1.add(testPanel, "testCard");

        visualizePanel.setName("visualizePanel"); // NOI18N

        visualizeScroll.setName("visualizeScroll"); // NOI18N

        visualizeSelectionTree.setName("visualizeSelectionTree"); // NOI18N
        visualizeSelectionTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                visualizeSelectionTreeValueChanged(evt);
            }
        });
        visualizeScroll.setViewportView(visualizeSelectionTree);

        org.jdesktop.layout.GroupLayout visualizePanelLayout = new org.jdesktop.layout.GroupLayout(visualizePanel);
        visualizePanel.setLayout(visualizePanelLayout);
        visualizePanelLayout.setHorizontalGroup(
            visualizePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(visualizeScroll, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
        );
        visualizePanelLayout.setVerticalGroup(
            visualizePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(visualizeScroll, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
        );

        selectionPanel1.add(visualizePanel, "visualizeCard");

        preprocessPanel.setName("preprocessPanel"); // NOI18N

        preprocessScroll.setName("preprocessScroll"); // NOI18N

        preprocessTree.setBackground(new java.awt.Color(236, 233, 216));
        preprocessTree.setName("preprocessTree"); // NOI18N
        preprocessTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                preprocessTree_valueChanged(evt);
            }
        });
        preprocessScroll.setViewportView(preprocessTree);

        org.jdesktop.layout.GroupLayout preprocessPanelLayout = new org.jdesktop.layout.GroupLayout(preprocessPanel);
        preprocessPanel.setLayout(preprocessPanelLayout);
        preprocessPanelLayout.setHorizontalGroup(
            preprocessPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(preprocessScroll, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
        );
        preprocessPanelLayout.setVerticalGroup(
            preprocessPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(preprocessScroll, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
        );

        selectionPanel1.add(preprocessPanel, "preprocessCard");

        org.jdesktop.layout.GroupLayout leftPanel1Layout = new org.jdesktop.layout.GroupLayout(leftPanel1);
        leftPanel1.setLayout(leftPanel1Layout);
        leftPanel1Layout.setHorizontalGroup(
            leftPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(leftPanel1Layout.createSequentialGroup()
                .add(mainToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(selectionPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE))
        );
        leftPanel1Layout.setVerticalGroup(
            leftPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(selectionPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
            .add(mainToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
        );

        mainSplitPane1.setLeftComponent(leftPanel1);

        graphDiagramINNER.setName("graphDiagramINNER"); // NOI18N
        graphDiagramINNER.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                graphDiagramINNERMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                graphDiagramINNERMouseExited(evt);
            }
        });

        org.jdesktop.layout.GroupLayout graphDiagramINNERLayout = new org.jdesktop.layout.GroupLayout(graphDiagramINNER);
        graphDiagramINNER.setLayout(graphDiagramINNERLayout);
        graphDiagramINNERLayout.setHorizontalGroup(
            graphDiagramINNERLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 789, Short.MAX_VALUE)
        );
        graphDiagramINNERLayout.setVerticalGroup(
            graphDiagramINNERLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 410, Short.MAX_VALUE)
        );

        mainSplitPane1.setRightComponent(graphDiagramINNER);

        divider1.setLeftComponent(mainSplitPane1);

        helpUseCaseTabbedPanel.setName("helpUseCaseTabbedPanel"); // NOI18N

        useCaseScrollPane.setName("useCaseScrollPane"); // NOI18N

        useCaseTextArea.setColumns(20);
        useCaseTextArea.setEditable(false);
        useCaseTextArea.setRows(5);
        useCaseTextArea.setName("useCaseTextArea"); // NOI18N
        useCaseScrollPane.setViewportView(useCaseTextArea);

        helpUseCaseTabbedPanel.addTab("Data set / Algorithms Use Case", useCaseScrollPane);

        helpContent.setName("helpContent"); // NOI18N
        helpUseCaseTabbedPanel.addTab("User Manual", helpContent);

        helpUseCaseTabbedPanel.setSelectedIndex(1);

        divider1.setRightComponent(helpUseCaseTabbedPanel);

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, divider1)
            .add(mainPanelLayout.createSequentialGroup()
                .add(quicktools, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1147, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .add(quicktools, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(divider1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE))
        );

        status.setText("Select a type of partition and then the type of the experiment");
        status.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        status.setName("status"); // NOI18N

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText("File");
        fileMenu.setName("fileMenu"); // NOI18N

        newExpItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/new.png"))); // NOI18N
        newExpItem.setText("New Experiment");
        newExpItem.setName("newExpItem"); // NOI18N
        newExpItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newExpItemActionPerformed(evt);
            }
        });
        fileMenu.add(newExpItem);

        loadExpItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/open.png"))); // NOI18N
        loadExpItem.setText("Load Experiment");
        loadExpItem.setName("loadExpItem"); // NOI18N
        loadExpItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadExpItemActionPerformed(evt);
            }
        });
        fileMenu.add(loadExpItem);

        fileTopSeparator.setName("fileTopSeparator"); // NOI18N
        fileMenu.add(fileTopSeparator);

        saveExpItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/save.png"))); // NOI18N
        saveExpItem.setText("Save Experiment");
        saveExpItem.setEnabled(false);
        saveExpItem.setName("saveExpItem"); // NOI18N
        saveExpItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveExpItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveExpItem);

        saveAsExpItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/saveas.png"))); // NOI18N
        saveAsExpItem.setText("Save Experiment As ...");
        saveAsExpItem.setEnabled(false);
        saveAsExpItem.setName("saveAsExpItem"); // NOI18N
        saveAsExpItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsExpItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveAsExpItem);

        fileBottomSeparator.setName("fileBottomSeparator"); // NOI18N
        fileMenu.add(fileBottomSeparator);

        menuBar.add(fileMenu);

        viewMenu.setText("View");
        viewMenu.setName("viewMenu"); // NOI18N

        statusBarItem.setSelected(true);
        statusBarItem.setText("Status Bar");
        statusBarItem.setName("statusBarItem"); // NOI18N
        statusBarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusBarItemActionPerformed(evt);
            }
        });
        viewMenu.add(statusBarItem);

        gridItem.setText("Grid");
        gridItem.setName("gridItem"); // NOI18N
        gridItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gridItemActionPerformed(evt);
            }
        });
        viewMenu.add(gridItem);

        helpPanelItem.setSelected(true);
        helpPanelItem.setText("Help Panel");
        helpPanelItem.setName("helpPanelItem"); // NOI18N
        helpPanelItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpPanelItemActionPerformed(evt);
            }
        });
        viewMenu.add(helpPanelItem);

        datasetsItem.setSelected(true);
        datasetsItem.setText("Datasets/Algorithm");
        datasetsItem.setName("datasetsItem"); // NOI18N
        datasetsItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                datasetsItemActionPerformed(evt);
            }
        });
        viewMenu.add(datasetsItem);

        menuBar.add(viewMenu);

        editMenu.setText("Edit");
        editMenu.setName("editMenu"); // NOI18N

        undoItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        undoItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/undo.png"))); // NOI18N
        undoItem.setText("Undo");
        undoItem.setEnabled(false);
        undoItem.setName("undoItem"); // NOI18N
        undoItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoItemActionPerformed(evt);
            }
        });
        editMenu.add(undoItem);

        redoItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/redo.png"))); // NOI18N
        redoItem.setText("Redo");
        redoItem.setEnabled(false);
        redoItem.setName("redoItem"); // NOI18N
        redoItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoItemActionPerformed(evt);
            }
        });
        editMenu.add(redoItem);

        selectItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/select.png"))); // NOI18N
        selectItem.setText("Select");
        selectItem.setName("selectItem"); // NOI18N
        selectItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectItemActionPerformed(evt);
            }
        });
        editMenu.add(selectItem);

        menuBar.add(editMenu);

        toolsMenu.setText("Tools");
        toolsMenu.setName("toolsMenu"); // NOI18N

        insertDataflowItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/next.png"))); // NOI18N
        insertDataflowItem.setText("Insert Dataflow");
        insertDataflowItem.setName("insertDataflowItem"); // NOI18N
        insertDataflowItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertDataflowItemActionPerformed(evt);
            }
        });
        toolsMenu.add(insertDataflowItem);

        toolsTopSeparator.setName("toolsTopSeparator"); // NOI18N
        toolsMenu.add(toolsTopSeparator);

        importItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/importpaq.png"))); // NOI18N
        importItem.setText("Import Algorithm KEEL Packet");
        importItem.setName("importItem"); // NOI18N
        importItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importItemActionPerformed(evt);
            }
        });
        toolsMenu.add(importItem);

        toolsBottomSeparator.setName("toolsBottomSeparator"); // NOI18N
        toolsMenu.add(toolsBottomSeparator);

        snapshotItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/snapshot.png"))); // NOI18N
        snapshotItem.setText("Snapshot");
        snapshotItem.setName("snapshotItem"); // NOI18N
        snapshotItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                snapshotItemActionPerformed(evt);
            }
        });
        toolsMenu.add(snapshotItem);

        runExpItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/run.png"))); // NOI18N
        runExpItem.setText("Run Experiment");
        runExpItem.setName("runExpItem"); // NOI18N
        runExpItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runExpItemActionPerformed(evt);
            }
        });
        toolsMenu.add(runExpItem);

        seedItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/random.png"))); // NOI18N
        seedItem.setText("Seed ...");
        seedItem.setName("seedItem"); // NOI18N
        seedItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seedItemActionPerformed(evt);
            }
        });
        toolsMenu.add(seedItem);

        executionOptItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/options.png"))); // NOI18N
        executionOptItem.setText("Execution Options");
        executionOptItem.setName("executionOptItem"); // NOI18N
        executionOptItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                executionOptItemActionPerformed(evt);
            }
        });
        toolsMenu.add(executionOptItem);

        menuBar.add(toolsMenu);

        helpMenu.setText("Help");
        helpMenu.setName("helpMenu"); // NOI18N

        contentItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        contentItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/help.png"))); // NOI18N
        contentItem.setText("Content");
        contentItem.setName("contentItem"); // NOI18N
        contentItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contentItemActionPerformed(evt);
            }
        });
        helpMenu.add(contentItem);

        aboutItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/about.png"))); // NOI18N
        aboutItem.setText("About");
        aboutItem.setName("aboutItem"); // NOI18N
        aboutItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(0, 0, 0)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, status, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1157, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(0, 0, 0)
                .add(mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(5, 5, 5)
                .add(status))
        );

        /***************************************************************
        ***************  EDUCATIONAL KEEL  ***************************
        **************************************************************/

        if(Frame.buttonPressed == 0)	//Button Experiments pressed
        {
            this.setTitle("Experiments Design: Off-Line Module");

            unsupervisedButton.setEnabled(true);
            unsupervisedButton.setVisible(true);
            mainToolBar1.removeAll();
            mainToolBar1.add(selecDatasets);
            mainToolBar1.add(selectPreprocessMethods);
            mainToolBar1.add(selectMethods);
            mainToolBar1.add(selectPostprocessMethods);
            mainToolBar1.add(selectTestMethods);
            mainToolBar1.add(selectVisualizeMethods);
            mainToolBar1.add(cursorFlux);

        }
        else	//Button Teaching pressed
        {
            this.setTitle("Educational Experiments Design: On-Line Module");

            unsupervisedButton.setEnabled(false);
            unsupervisedButton.setVisible(false);

            runButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/educationalrun.gif")));
            runExpItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/educationalrun.gif")));

            mainToolBar1.removeAll();
            mainToolBar1.add(selecDatasets);
            mainToolBar1.add(selectPreprocessMethods);
            mainToolBar1.add(selectMethods);
            mainToolBar1.add(cursorFlux);

        }

        /***************************************************************
        ***************  EDUCATIONAL KEEL  ***************************
        **************************************************************/

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * New experiment button
     * @param evt Event
     */
    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        int salvar = newExperiment();

        if (objType == LQD && salvar != JOptionPane.CANCEL_OPTION) {
            deactivateUpperMenu();
            summary = false;
            panelDatasets.clear();
            dinDatasets.clear();
            mainSplitPane1.setDividerLocation(-1);
            statusBarItem.setSelected(true);

        }
        if (objType == LQD) {
            deactivateUpperMenu();
            openButton.setEnabled(true);
            newButton.setEnabled(true);
        }

        selectPostprocessMethods.setVisible(true);
        selectTestMethods.setVisible(true);
        selectVisualizeMethods.setVisible(true);

    }//GEN-LAST:event_newButtonActionPerformed

    /**
     * Open button
     * @param evt Event
     */
    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
        /***************************************************************
         *********************  EDUCATIONAL KEEL  **********************
         **************************************************************/
        if (Frame.buttonPressed == 0) //Button Experiments pressed
        {
            loadExperiment();
        } else //Button Teaching pressed
        {
            //warning message if window of partitons is opened and the user
            //presses "load experiment"
            //experiment opened
            if (getExecDocentWindowState() == false) {
                Object[] options = {"OK", "CANCEL"};
                int n = JOptionPane.showOptionDialog(this, "The actual experiment is opened!. \n" +
                        "OK presses to STOP experiment.Results will be losed. \n", "Warning!",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                        null, options, options[0]);
                if (n == JOptionPane.YES_OPTION) {
                    deleteExecDocentWindow();
                    closedEducationalExec(null);
                    loadExperiment();
                }
            } else {
                loadExperiment();
            }
        }
    /***************************************************************
     *********************  EDUCATIONAL KEEL  **********************
     **************************************************************/
    }//GEN-LAST:event_openButtonActionPerformed

    /**
     * Save experiment button
     * @param evt Event
     */
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        saveExpItemActionPerformed(evt);
}//GEN-LAST:event_saveButtonActionPerformed

    /**
     * Run button
     * @param evt Event
     */
    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        /***************************************************************
         *********************  EDUCATIONAL KEEL  **********************
         **************************************************************/
        if (Frame.buttonPressed == 0) //Button Experiments pressed
        {
            if (objType != LQD) {
                regeneratePartitions();
                generateExperimentDirectories();
            } else {
                generateExperimentDirectoriesLQD();
            }

        } else //Button Teaching pressed
        {
            /*warning message if window of partitons is opened and the user
            presses "run experiment" experiment opened*/
            if (getExecDocentWindowState() == false) {
                Object[] options = {"OK", "CANCEL"};
                int n = JOptionPane.showOptionDialog(this, "The actual experiment is opened!. \n" +
                        "OK presses to STOP experiment.Results will be losed. \n", "Warning!",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                        null, options, options[0]);
                if (n == JOptionPane.YES_OPTION) {
                    deleteExecDocentWindow();
                    closedEducationalExec(null);
                    regeneratePartitions();
                    generateExperimentDirectories();
                }
            } else {
                regeneratePartitions();
                generateExperimentDirectories();
            }
        }
    /***************************************************************
     *********************  EDUCATIONAL KEEL  **********************
     **************************************************************/
}//GEN-LAST:event_runButtonActionPerformed

    /**
     * Show help button
     * @param evt Event
     */
    private void showHelpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showHelpButtonActionPerformed

        if (helpPanelItem.isSelected()) {
            divider1.setDividerLocation(divider1.getHeight());
            helpPanelItem.setSelected(false);
        } else {
            divider1.setDividerLocation(-1);
            helpPanelItem.setSelected(true);
        }
}//GEN-LAST:event_showHelpButtonActionPerformed

    /**
     * Show algorithm button
     * @param evt Event
     */
    private void showAlgButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAlgButtonActionPerformed

        if (datasetsItem.isSelected()) {
            datasetsItem.setSelected(false);
        } else {
            datasetsItem.setSelected(true);
        }

        panData_actionPerformed(evt);
}//GEN-LAST:event_showAlgButtonActionPerformed

    /**
     * Snapshot button
     * @param evt Event
     */
    private void snapshotButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_snapshotButtonActionPerformed
        snapshotItemActionPerformed(evt);
}//GEN-LAST:event_snapshotButtonActionPerformed
    /**
     * Select data sets button
     * @param evt Event
     */
    private void selecDatasetsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selecDatasetsActionPerformed

        mainSplitPane1.setDividerLocation(-1);
        datasetsItem.setSelected(true);

        ((CardLayout) selectionPanel1.getLayout()).show(selectionPanel1, "dinDatasetsCard");
        //cursorAction = GraphPanel.SELECTING;
        status.setText("Data Set selection");
        //cursorDraw = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        graphDiagramINNER.setToolTipText("Click twice to see node properties");
        statusBarItem.setSelected(true);
        showAlgButton.setEnabled(true);


    //selectionPanel1.setVisible(true);
}//GEN-LAST:event_selecDatasetsActionPerformed
    /**
     * Select preprocess button
     * @param evt Event
     */
    private void selectPreprocessMethodsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectPreprocessMethodsActionPerformed

        mainSplitPane1.setDividerLocation(-1);
        datasetsItem.setSelected(true);

        ((CardLayout) selectionPanel1.getLayout()).show(selectionPanel1, "preprocessCard");
        //cursorAction = GraphPanel.SELECTING;
        status.setText("Preprocess selection");
        //cursorDraw = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        graphDiagramINNER.setToolTipText("Click twice to see node properties");
        statusBarItem.setSelected(true);
        showAlgButton.setEnabled(true);
    //selectionPanel1.setVisible(true);
}//GEN-LAST:event_selectPreprocessMethodsActionPerformed

    /**
     * Activate menu items
     */
    private void activateMenuItems() {

        if ((objType == INVESTIGATION) || (objType == SUBGROUPDISCOVERY)) {
            selectItem.setEnabled(true);
            insertDataflowItem.setEnabled(true);
            importItem.setEnabled(true);
            snapshotItem.setEnabled(true);
            runExpItem.setEnabled(true);
            seedItem.setEnabled(true);
            executionOptItem.setEnabled(true);
        }
    }

    /**
     * Disable some menu items
     */
    private void deactivateOther() {

        if (objType == INVESTIGATION) {
            selectItem.setEnabled(false);
            insertDataflowItem.setEnabled(false);
            importItem.setEnabled(false);
            snapshotItem.setEnabled(false);
            runExpItem.setEnabled(false);
            seedItem.setEnabled(false);
            executionOptItem.setEnabled(false);
            saveExpItem.setEnabled(false);
            saveAsExpItem.setEnabled(false);
        }

    }

    /**
     * Classification experiments
     * @param evt Event
     */
    private void classificationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_classificationButtonActionPerformed

        numberKFoldCross = Integer.valueOf(kValueField.getText());

        //Control of this variables because of they could be
        //modified in experiments
        if (objType == LQD) {
            //numberKFoldCross=10;
            // cvType = Experiments.PK;
        } else {
            objType = INVESTIGATION;
        }
        if (kfoldButton.isSelected() && numberKFoldCross < 1) {
            JOptionPane.showMessageDialog(this, "Number of folds must be at least 1",
                    "Invalid number of folds", JOptionPane.ERROR_MESSAGE);
        } else {
            ((CardLayout) selectionPanel1.getLayout()).show(selectionPanel1, "datasetsChecksCard");
            status.setText("Select an initial set of dataset and then click on the drawing panel");
            selectButton.setEnabled(true);
            enableMainToolBar(true);

            if (objType != LQD) {
                activateUpperMenu();
            } else {
                quicktools.getComponent(quicktools.getComponentCount() - 1).setEnabled(true);
            }

            if (objType == LQD) {
                helpContent.muestraURL(this.getClass().getResource("/contextualHelp/data_set_exp_lqd.html"));
            } else {
                helpContent.muestraURL(this.getClass().getResource("/contextualHelp/data_set_exp.html"));
            }

            this.expType = Experiments.CLASSIFICATION;
            if (kfoldButton.isSelected()) {
                cvType = Experiments.PK;
            } else if (fivetwoButton.isSelected()) {
                cvType = Experiments.P5X2;
            } else {
                cvType = Experiments.PnoVal;
            }
            //we want to prevent that this panel will ever show again
            initialPanel1.setVisible(false);
            //now, we load the datasets and the different methods
            undoButton.setEnabled(false);
            redoButton.setEnabled(false);
            continueExperimentGeneration();
            ((CardLayout) selectionPanel1.getLayout()).show(selectionPanel1, "datasetsChecksPanel");
        }
        deleteItem.setEnabled(false);
        if (objType != LQD) {
            activateMenuItems();
            runExpItem.setEnabled(false);
            saveButton.setEnabled(false);
            insertDataflowItem.setEnabled(false);
        }
}//GEN-LAST:event_classificationButtonActionPerformed
    /**
     * Regression experiments
     * @param evt Event
     */
    private void regressionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regressionButtonActionPerformed

        if (objType == LQD) {
            JOptionPane.showMessageDialog(this, "No exist regression with Low Quality Data",
                    "Regression LQD", JOptionPane.INFORMATION_MESSAGE);
        } else {
            objType = INVESTIGATION;
            activateUpperMenu();
            selectButton.setEnabled(true);
            enableMainToolBar(true);


            numberKFoldCross = Integer.valueOf(kValueField.getText());
            if (kfoldButton.isSelected() && numberKFoldCross < 1) {
                JOptionPane.showMessageDialog(this, "Number of folds must be at least 1",
                        "Invalid number of folds", JOptionPane.ERROR_MESSAGE);
            } else {
                ((CardLayout) selectionPanel1.getLayout()).show(selectionPanel1, "datasetsChecksCard");
                status.setText("Select an initial set of dataset and then click on the drawing panel");
                selectButton.setEnabled(true);
                enableMainToolBar(true);
                activateUpperMenu();
                helpContent.muestraURL(this.getClass().getResource("/contextualHelp/data_set_exp.html"));

                this.expType = Experiments.REGRESSION;
                if (kfoldButton.isSelected()) {
                    cvType = Experiments.PK;
                } else if (fivetwoButton.isSelected()) {
                    cvType = Experiments.P5X2;
                } else {
                    cvType = Experiments.PnoVal;
                }
                //we want to prevent that this panel will ever show again
                initialPanel1.setVisible(false);
                //now, we load the datasets and the different methods
                undoButton.setEnabled(false);
                redoButton.setEnabled(false);
                continueExperimentGeneration();
                ((CardLayout) selectionPanel1.getLayout()).show(selectionPanel1, "datasetsChecksPanel");
            }
            deleteItem.setEnabled(false);
            if (objType != LQD) {
                activateMenuItems();
                runExpItem.setEnabled(false);
                saveButton.setEnabled(false);
                insertDataflowItem.setEnabled(false);
            }
        }


}//GEN-LAST:event_regressionButtonActionPerformed
    /**
     * Unsupervised experiments
     * @param evt Event
     */
    private void unsupervisedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unsupervisedButtonActionPerformed

        if (objType != LQD) {
            objType = INVESTIGATION;
        }
        activateUpperMenu();
        selectButton.setEnabled(true);
        enableMainToolBar(true);

        numberKFoldCross = Integer.valueOf(kValueField.getText());
        if (kfoldButton.isSelected() && numberKFoldCross < 1) {
            JOptionPane.showMessageDialog(this, "Number of folds must be at least 1",
                    "Invalid number of folds", JOptionPane.ERROR_MESSAGE);
        } else {
            ((CardLayout) selectionPanel1.getLayout()).show(selectionPanel1, "datasetsChecksCard");
            status.setText("Select an initial set of dataset and then click on the drawing panel");
            selectButton.setEnabled(true);
            enableMainToolBar(true);
            activateUpperMenu();
            helpContent.muestraURL(this.getClass().getResource("/contextualHelp/data_set_exp.html"));

            this.expType = Experiments.UNSUPERVISED;
            if (kfoldButton.isSelected()) {
                cvType = Experiments.PK;
            } else if (fivetwoButton.isSelected()) {
                cvType = Experiments.P5X2;
            } else {
                cvType = Experiments.PnoVal;
            }
            //we want to prevent that this panel will ever show again
            initialPanel1.setVisible(false);
            //now, we load the datasets and the different methods
            undoButton.setEnabled(false);
            redoButton.setEnabled(false);
            continueExperimentGeneration();
            ((CardLayout) selectionPanel1.getLayout()).show(selectionPanel1, "datasetsChecksPanel");
        }
        deleteItem.setEnabled(false);
        if (objType != LQD) {
            activateMenuItems();
            runExpItem.setEnabled(false);
            saveButton.setEnabled(false);
            insertDataflowItem.setEnabled(false);
        }

}//GEN-LAST:event_unsupervisedButtonActionPerformed

    /**
     * Select button
     * @param evt Event
     */
    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButtonActionPerformed

        seleccionar_actionPerformed(evt);
    }//GEN-LAST:event_selectButtonActionPerformed

    /**
     * Cursor flux button
     * @param evt Event
     */
    private void cursorFluxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cursorFluxActionPerformed
        if (objType == LQD) {
            if (statusBarItem.isSelected()) {
                mainSplitPane1.setDividerLocation(mainToolBar1.getWidth());
                statusBarItem.setSelected(false);
            }
        } else {
            if (datasetsItem.isSelected()) {
                mainSplitPane1.setDividerLocation(mainToolBar1.getWidth());
                datasetsItem.setSelected(false);
            }

        }

        flujo_actionPerformed(evt);
}//GEN-LAST:event_cursorFluxActionPerformed
    /**
     * Delete button
     * @param evt Event
     */
    private void deleteItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteItemActionPerformed
        /***************************************************************
         ***************  EDUCATIONAL KEEL  ****************************
         **************************************************************/
        // remove an element
        if (Frame.buttonPressed == 0) //Button Experiments pressed
        {

            if (objType == LQD) {
                deletelqd();
            } else {
                delete();
            }
        } else //Button Teaching pressed
        {
            //experiment opened
            if (getExecDocentWindowState() == false) {
                Object[] options = {"OK", "CANCEL"};
                int n = JOptionPane.showOptionDialog(this, "The actual experiment is opened!. \n" +
                        "OK presses to STOP experiment.Results will be losed. \n", "Warning!",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                        null, options, options[0]);
                if (n == JOptionPane.YES_OPTION) {
                    deleteExecDocentWindow();
                    closedEducationalExec(null);
                    delete();
                }
            } else //Button Teaching pressed
            {
                delete();
            }
        }
    /***************************************************************
     ***************  EDUCATIONAL KEEL  ****************************
     **************************************************************/
}//GEN-LAST:event_deleteItemActionPerformed
    /**
     * Mouse entered
     * @param evt Event
     */
    private void graphDiagramINNERMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_graphDiagramINNERMouseEntered
        this.setCursor(cursorDraw);
}//GEN-LAST:event_graphDiagramINNERMouseEntered
    /**
     * Mouse exit
     * @param evt Event
     */
    private void graphDiagramINNERMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_graphDiagramINNERMouseExited
        // save cursor and change cursor to arrow
        cursorDraw = this.getCursor();
        this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_graphDiagramINNERMouseExited
    /**
     * K folds button
     * @param evt Event
     */
    private void kfoldButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_kfoldButtonItemStateChanged
        if (kfoldButton.isSelected()) {
            kValueField.setEnabled(true);
        } else {
            kValueField.setEnabled(false);
        }
}//GEN-LAST:event_kfoldButtonItemStateChanged
    /**
     * Mouse entered
     * @param evt Event
     */
    private void newExpItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newExpItemActionPerformed
        newButtonActionPerformed(evt);
}//GEN-LAST:event_newExpItemActionPerformed
    /**
     * Load button
     * @param evt Event
     */
    private void loadExpItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadExpItemActionPerformed

        openButtonActionPerformed(evt);

}//GEN-LAST:event_loadExpItemActionPerformed
    /**
     * Save button
     * @param evt Event
     */
    private void saveExpItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveExpItemActionPerformed
        saveExperiment(0);
}//GEN-LAST:event_saveExpItemActionPerformed
    /**
     * Save as button
     * @param evt Event
     */
    private void saveAsExpItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsExpItemActionPerformed
        saveExperiment(1);
}//GEN-LAST:event_saveAsExpItemActionPerformed
    /**
     * Help panel button
     * @param evt Event
     */
    private void helpPanelItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpPanelItemActionPerformed

        if (helpPanelItem.isSelected()) {
            divider1.setDividerLocation(-1);
            helpPanelItem.setSelected(true);
        } else {
            divider1.setDividerLocation(divider1.getHeight());
            helpPanelItem.setSelected(false);
        }

}//GEN-LAST:event_helpPanelItemActionPerformed

    /***************************************************************
     ***************  EDUCATIONAL KEEL *****************************
     **************************************************************/
    /**
     * Import button
     * @param evt Event
     */
    private void importItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importItemActionPerformed
        if (Frame.buttonPressed == 0) //Button Experiments pressed
        {
            importItemActionPerformedAux(null);
        } else //Button Teaching Pressed
        {
            /*Warning message if the window of partitions is opened and the
            user presses "import algorithm keel packet" with experiment opened*/
            if (getFocusableWindowState() == false) {
                Object[] options = {"OK", "CANCEL"};
                int n = JOptionPane.showOptionDialog(this, "The actual experiment is opened!. \n" +
                        "OK presses to STOP experiment.Results will be losed. \n", "Warning!",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                        null, options, options[0]);
                if (n == JOptionPane.YES_OPTION) {
                    deleteExecDocentWindow();
                    closedEducationalExec(null);
                    importItemActionPerformedAux(null);
                }
            } else {
                importItemActionPerformedAux(null);
            }
        }
}//GEN-LAST:event_importItemActionPerformed
    /***************************************************************
     ***************  EDUCATIONAL KEEL *****************************
     **************************************************************/
    /**
     * Import packages button
     * @param evt Event
     */
    private void importItemActionPerformedAux(java.awt.event.ActionEvent evt) {
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser(lastPathChosen);
        KeelFileFilter ff = new KeelFileFilter();
        ff.addExtension("zip");
        ff.setFilterName("Zip files (.zip)");
        fc.setFileFilter(ff);
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File file = fc.getSelectedFile();
            lastPathChosen = file.getAbsolutePath();
        }
    }

    /**
     * Snapshot button
     * @param evt Event
     */
    private void snapshotItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_snapshotItemActionPerformed
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser(lastPathChosen);
        KeelFileFilter ff = new KeelFileFilter();
        ff.addExtension("jpg");
        ff.addExtension("jpeg");
        ff.setFilterName("JPEG images (.jpg, .jpeg)");
        fc.setFileFilter(ff);
        int returnVal = fc.showSaveDialog(null);
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fc.getSelectedFile();
                lastPathChosen = file.getAbsolutePath().replace(file.getName(), "");

                String fileName = fc.getSelectedFile().getAbsolutePath();
                if (!fileName.toLowerCase().endsWith(".jpg") && !fileName.toLowerCase().endsWith(".jpeg")) {
                    fileName += ".jpg";
                }
                File tmp = new File(fileName);
                if (!tmp.exists() || JOptionPane.showConfirmDialog(
                        this,
                        "File " + fileName + " already exists. Do you want to replace it?",
                        "Confirm", JOptionPane.YES_NO_OPTION, 3) == JOptionPane.YES_OPTION) {

                    BufferedImage bi = new BufferedImage(graphDiagramINNER.mainGraph.getMaxX(), graphDiagramINNER.mainGraph.getMaxY(),
                            BufferedImage.TYPE_INT_RGB);
                    
					Graphics2D g2 = bi.createGraphics();
					graphDiagramINNER.paint(g2);
					g2.dispose();
					
					ImageIO.write(bi, "jpeg",tmp);
                    /*	
						OutputStream out = new FileOutputStream(fileName);
					JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
						encoder.encode(bi);
						out.close();
						*/                
                }
            } catch (Exception exc) {
            }
        }
}//GEN-LAST:event_snapshotItemActionPerformed

    /**
     * Seed button
     * @param evt Event
     */
    private void seedItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seedItemActionPerformed
       DialogSeed dseed = new DialogSeed(this, "Seed", true);//GEN-LAST:event_seedItemActionPerformed
        dseed.setSize(350, 200);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = dseed.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        dseed.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        dseed.setResizable(false);
        dseed.setVisible(true);

    }

    /**
     * Methods button
     * @param evt Event
     */
    private void selectMet_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectMet_actionPerformed

        mainSplitPane1.setDividerLocation(-1);
        datasetsItem.setSelected(true);

        ((CardLayout) selectionPanel1.getLayout()).show(selectionPanel1, "methodsCard");
        //cursorAction = GraphPanel.SELECTING;
        status.setText("Method selection");
        //cursorDraw = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        graphDiagramINNER.setToolTipText("Click twice to see node properties");
        statusBarItem.setSelected(true);
        showAlgButton.setEnabled(true);
    //selectionPanel1.setVisible(true);
    }//GEN-LAST:event_selectMet_actionPerformed

    /**
     * Postprocess button
     * @param evt Event
     */
    private void selectPostprocessMethodsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectPostprocessMethodsActionPerformed

        mainSplitPane1.setDividerLocation(-1);
        datasetsItem.setSelected(true);

        ((CardLayout) selectionPanel1.getLayout()).show(selectionPanel1, "postprocessCard");
        //cursorAction = GraphPanel.SELECTING;
        status.setText("Postprocess selection");
        //cursorDraw = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        graphDiagramINNER.setToolTipText("Click twice to see node properties");
        statusBarItem.setSelected(true);
        showAlgButton.setEnabled(true);
    //selectionPanel1.setVisible(true);
}//GEN-LAST:event_selectPostprocessMethodsActionPerformed

    /**
     * Test button
     * @param evt Event
     */
    private void selectTestMethodsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectTestMethodsActionPerformed

        mainSplitPane1.setDividerLocation(-1);
        datasetsItem.setSelected(true);

        ((CardLayout) selectionPanel1.getLayout()).show(selectionPanel1, "testCard");
        //cursorAction = GraphPanel.SELECTING;
        status.setText("Test selection");
        //cursorDraw = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        graphDiagramINNER.setToolTipText("Click twice to see node properties");
        statusBarItem.setSelected(true);
        showAlgButton.setEnabled(true);
    //selectionPanel1.setVisible(true);
}//GEN-LAST:event_selectTestMethodsActionPerformed
    /**
     * Visualize button
     * @param evt Event
     */
    private void selectVisualizeMethodsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectVisualizeMethodsActionPerformed

        mainSplitPane1.setDividerLocation(-1);
        datasetsItem.setSelected(true);

        ((CardLayout) selectionPanel1.getLayout()).show(selectionPanel1, "visualizeCard");
        //cursorAction = GraphPanel.SELECTING;
        status.setText("Visor selection");
        //cursorDraw = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        graphDiagramINNER.setToolTipText("Click twice to see node properties");
        statusBarItem.setSelected(true);
        showAlgButton.setEnabled(true);
    //selectionPanel1.setVisible(true);
}//GEN-LAST:event_selectVisualizeMethodsActionPerformed

    /**
     * Manages changes in tree
     * @param evt Event
     */
    private void preprocessTree_valueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_preprocessTree_valueChanged

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) (evt.getPath().getLastPathComponent());

        //System.out.println (evt.getPath().getLastPathComponent()+" GSTRG "+preprocessTree.getSelectionPath());
        if (objType != LQD) {
            if (preprocessTree.getSelectionPath() != null) {
                if (node.isLeaf() && ((ExternalObjectDescription) node.getUserObject()).getName().charAt(0) != '(') {
                    cursorAction = GraphPanel.PAINT_ALGORITHM;

                    dsc = new ExternalObjectDescription((ExternalObjectDescription) (node.getUserObject()));
                    dsc.setSubtype(Node.type_Preprocess);
                    dsc.setSubtypelqd(Node.CRISP);


                    UseCase casoUso = readXMLUseCase("./help/" + dsc.getName());
                    if (casoUso == null) {
                        this.useCaseTextArea.setText("No use case available");
                    } else {
                        this.useCaseTextArea.setText(casoUso.toString());
                        this.useCaseTextArea.setCaretPosition(0);
                    }

                    status.setText("Click on the draw area to insert a new node");
                    cursorDraw = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
                    graphDiagramINNER.setToolTipText("Click on the draw area to insert an algorithm node");

                    methodsSelectionTree.setSelectionPath(null);
                    postprocessSelectionTree.setSelectionPath(null);
                    testSelectionTree.setSelectionPath(null);
                    visualizeSelectionTree.setSelectionPath(null);
                    invisible.setSelected(true);

                } else {
                    cursorAction = GraphPanel.SELECTING;

                    dsc = new ExternalObjectDescription((ExternalObjectDescription) (node.getUserObject()));
                    dsc.setSubtype(Node.type_Method);
                    dsc.setSubtypelqd(Node.CRISP);

                    UseCase casoUso = readXMLUseCase("./help/" + dsc.getName());
                    if (casoUso == null) {
                        this.useCaseTextArea.setText("No use case available");
                    } else {
                        this.useCaseTextArea.setText(casoUso.toString());
                        this.useCaseTextArea.setCaretPosition(0);
                    }

                    cursorDraw = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

                    graphDiagramINNER.setToolTipText("Click twice into a node to view its properties");
                    methodsSelectionTree.setSelectionPath(null);
                    preprocessTree.setSelectionPath(null);
                    postprocessSelectionTree.setSelectionPath(null);
                    testSelectionTree.setSelectionPath(null);
                    visualizeSelectionTree.setSelectionPath(null);
                    //selectButton.setSelected(true);
                    status.setText("Click in a node to select it");
                }
            }
        } else {
            if (preprocessTree.getSelectionPath() != null) {
                if (node.isLeaf()) {
                    cursorAction = GraphPanel.PAINT_ALGORITHM;


                    if (preprocessTree.getSelectionPath().toString().contains("LQD") == true) {
                        dscLQD = new ExternalObjectDescription((ExternalObjectDescription) (node.getUserObject()));
                        dscLQD.setSubtype(Node.type_Preprocess);
                        dscLQD.setSubtypelqd(Node.LQD);
                        RamaLqd = 1;
                    } else {
                        dscCRISP = new ExternalObjectDescription((ExternalObjectDescription) (node.getUserObject()));
                        dscCRISP.setSubtype(Node.type_Preprocess);
                        dscCRISP.setSubtypelqd(Node.CRISP2);
                        RamaLqd = 0;
                    }


                    status.setText("Click on the draw area to insert a new node");
                    cursorDraw = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
                    graphDiagramINNER.setToolTipText("Click on the draw area to insert an algorithm node");

                    methodsSelectionTree.setSelectionPath(null);
                    postprocessSelectionTree.setSelectionPath(null);
                    testSelectionTree.setSelectionPath(null);
                    visualizeSelectionTree.setSelectionPath(null);
                    invisible.setSelected(true);

                }


            }
        }
    }//GEN-LAST:event_preprocessTree_valueChanged

    /**
     * Manages selection in tree
     * @param evt Event
     */
    private void methodSelectionTree_valueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_methodSelectionTree_valueChanged
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) (evt.getPath().getLastPathComponent());

        if (objType != LQD) {
            if (methodsSelectionTree.getSelectionPath() != null) {
                if (node.isLeaf() && ((ExternalObjectDescription) node.getUserObject()).getName().charAt(0) != '(') {
                    cursorAction = GraphPanel.PAINT_ALGORITHM;


                    dsc = new ExternalObjectDescription((ExternalObjectDescription) (node.getUserObject()));

                    dsc.setSubtype(Node.type_Method);
                    dsc.setSubtypelqd(Node.CRISP);

                    UseCase casoUso = readXMLUseCase("./help/" + dsc.getName());
                    if (casoUso == null) {
                        this.useCaseTextArea.setText("No use case available");
                    } else {
                        this.useCaseTextArea.setText(casoUso.toString());
                        this.useCaseTextArea.setCaretPosition(0);
                    }

                    status.setText("Click on the draw area to insert a new node");
                    cursorDraw = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
                    graphDiagramINNER.setToolTipText("Click on the draw area to insert an algorithm node");

                    preprocessTree.setSelectionPath(null);
                    postprocessSelectionTree.setSelectionPath(null);
                    testSelectionTree.setSelectionPath(null);
                    visualizeSelectionTree.setSelectionPath(null);
                    invisible.setSelected(true);
                } else {
                    cursorAction = GraphPanel.SELECTING;

                    dsc = new ExternalObjectDescription((ExternalObjectDescription) (node.getUserObject()));
                    dsc.setSubtype(Node.type_Method);
                    dsc.setSubtypelqd(Node.CRISP);

                    UseCase casoUso = readXMLUseCase("./help/" + dsc.getName());
                    if (casoUso == null) {
                        this.useCaseTextArea.setText("No use case available");
                    } else {
                        this.useCaseTextArea.setText(casoUso.toString());
                        this.useCaseTextArea.setCaretPosition(0);
                    }

                    cursorDraw = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

                    graphDiagramINNER.setToolTipText("Click twice into a node to view its properties");
                    methodsSelectionTree.setSelectionPath(null);
                    preprocessTree.setSelectionPath(null);
                    postprocessSelectionTree.setSelectionPath(null);
                    testSelectionTree.setSelectionPath(null);
                    visualizeSelectionTree.setSelectionPath(null);
                    //selectButton.setSelected(true);
                    status.setText("Click in a node to select it");
                }
            }
        } else {
            if (methodsSelectionTree.getSelectionPath() != null) {
                if (node.isLeaf()) {
                    cursorAction = GraphPanel.PAINT_ALGORITHM;
                    if (methodsSelectionTree.getSelectionPath().toString().contains("LQD") == true) {
                        dscLQD = new ExternalObjectDescription((ExternalObjectDescription) (node.getUserObject()));
                        dscLQD.setSubtype(Node.type_Method);
                        dscLQD.setSubtypelqd(Node.LQD);
                        RamaLqd = 1;
                    } else {
                        dscCRISP = new ExternalObjectDescription((ExternalObjectDescription) (node.getUserObject()));
                        dscCRISP.setSubtype(Node.type_Method);
                        dscCRISP.setSubtypelqd(Node.CRISP2);
                        RamaLqd = 0;
                    }

                    status.setText("Click on the draw area to insert a new node");
                    cursorDraw = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
                    graphDiagramINNER.setToolTipText("Click on the draw area to insert an algorithm node");

                    preprocessTree.setSelectionPath(null);
                    postprocessSelectionTree.setSelectionPath(null);
                    testSelectionTree.setSelectionPath(null);
                    visualizeSelectionTree.setSelectionPath(null);
                    invisible.setSelected(true);

                }
            }

        }
    }//GEN-LAST:event_methodSelectionTree_valueChanged

    /**
     * Manages selection of postprocess in tree
     * @param evt Event
     */
    private void postprocessSelectionTree_valueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_postprocessSelectionTree_valueChanged
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) (evt.getPath().getLastPathComponent());

        if (postprocessSelectionTree.getSelectionPath() != null) {
            if (node.isLeaf() && ((ExternalObjectDescription) node.getUserObject()).getName().charAt(0) != '(') {
                cursorAction = GraphPanel.PAINT_ALGORITHM;

                dsc = new ExternalObjectDescription((ExternalObjectDescription) (node.getUserObject()));
                dsc.setSubtype(Node.type_Postprocess);
                dsc.setSubtypelqd(Node.CRISP);

                UseCase casoUso = readXMLUseCase("./help/" + dsc.getName());
                if (casoUso == null) {
                    this.useCaseTextArea.setText("No use case available");
                } else {
                    this.useCaseTextArea.setText(casoUso.toString());
                    this.useCaseTextArea.setCaretPosition(0);
                }

                status.setText("Click on the draw area to insert a new node");
                cursorDraw = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
                graphDiagramINNER.setToolTipText("Click on the draw area to insert an algorithm node");

                methodsSelectionTree.setSelectionPath(null);
                preprocessTree.setSelectionPath(null);
                testSelectionTree.setSelectionPath(null);
                visualizeSelectionTree.setSelectionPath(null);
                invisible.setSelected(true);

            } else {
                cursorAction = GraphPanel.SELECTING;

                dsc = new ExternalObjectDescription((ExternalObjectDescription) (node.getUserObject()));
                dsc.setSubtype(Node.type_Method);
                dsc.setSubtypelqd(Node.CRISP);

                UseCase casoUso = readXMLUseCase("./help/" + dsc.getName());
                if (casoUso == null) {
                    this.useCaseTextArea.setText("No use case available");
                } else {
                    this.useCaseTextArea.setText(casoUso.toString());
                    this.useCaseTextArea.setCaretPosition(0);
                }

                cursorDraw = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

                graphDiagramINNER.setToolTipText("Click twice into a node to view its properties");
                methodsSelectionTree.setSelectionPath(null);
                preprocessTree.setSelectionPath(null);
                postprocessSelectionTree.setSelectionPath(null);
                visualizeSelectionTree.setSelectionPath(null);
                testSelectionTree.setSelectionPath(null);
                //selectButton.setSelected(true);
                status.setText("Click in a node to select it");
            }
        }
    }//GEN-LAST:event_postprocessSelectionTree_valueChanged

    /**
     * Manages selection of test in tree
     * @param evt Event
     */
    private void testSelection_valueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_testSelection_valueChanged
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) (evt.getPath().getLastPathComponent());

        if (testSelectionTree.getSelectionPath() != null) {
            if (node.isLeaf() && ((ExternalObjectDescription) node.getUserObject()).getName().charAt(0) != '(') {
                cursorAction = GraphPanel.PAINT_TEST;

                dsc = new ExternalObjectDescription((ExternalObjectDescription) (node.getUserObject()));
                dsc.setSubtype(Node.type_Test);

                if (objType == LQD) {
                    dsc.setSubtypelqd(Node.LQD);
                } else {
                    dsc.setSubtypelqd(Node.CRISP);
                }

                UseCase casoUso = readXMLUseCase("./help/" + dsc.getName());
                if (casoUso == null) {
                    this.useCaseTextArea.setText("No use case available");
                } else {
                    this.useCaseTextArea.setText(casoUso.toString());
                    this.useCaseTextArea.setCaretPosition(0);
                }

                status.setText("Click on the draw area to insert a new node");
                cursorDraw = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
                graphDiagramINNER.setToolTipText("Click on the draw area to insert a test node");

                methodsSelectionTree.setSelectionPath(null);
                preprocessTree.setSelectionPath(null);
                postprocessSelectionTree.setSelectionPath(null);
                visualizeSelectionTree.setSelectionPath(null);
                invisible.setSelected(true);

            } else {
                cursorAction = GraphPanel.SELECTING;

                dsc = new ExternalObjectDescription((ExternalObjectDescription) (node.getUserObject()));
                dsc.setSubtype(Node.type_Method);

                if (objType == LQD) {
                    dsc.setSubtypelqd(Node.LQD);
                } else {
                    dsc.setSubtypelqd(Node.CRISP);
                }

                UseCase casoUso = readXMLUseCase("./help/" + dsc.getName());
                if (casoUso == null) {
                    this.useCaseTextArea.setText("No use case available");
                } else {
                    this.useCaseTextArea.setText(casoUso.toString());
                    this.useCaseTextArea.setCaretPosition(0);
                }

                cursorDraw = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

                graphDiagramINNER.setToolTipText("Click twice into a node to view its properties");
                methodsSelectionTree.setSelectionPath(null);
                preprocessTree.setSelectionPath(null);
                postprocessSelectionTree.setSelectionPath(null);
                testSelectionTree.setSelectionPath(null);
                visualizeSelectionTree.setSelectionPath(null);
                //selectButton.setSelected(true);
                status.setText("Click in a node to select it");
            }
        }

    }//GEN-LAST:event_testSelection_valueChanged

    /**
     * Show contents of item
     * @param evt Event
     */
    private void contentItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contentItemActionPerformed
        // show help
        HelpFrame ayuda = new HelpFrame();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = ayuda.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        ayuda.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        ayuda.setVisible(true);
    }//GEN-LAST:event_contentItemActionPerformed

    /**
     * Manages selection of visualization in tree
     * @param evt Event
     */
    private void visualizeSelectionTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_visualizeSelectionTreeValueChanged
        // test selection
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) (evt.getPath().getLastPathComponent());

        if (visualizeSelectionTree.getSelectionPath() != null) {
            if (node.isLeaf() && ((ExternalObjectDescription) node.getUserObject()).getName().charAt(0) != '(') {
                cursorAction = GraphPanel.PAINT_TEST;

                dsc = new ExternalObjectDescription((ExternalObjectDescription) (node.getUserObject()));
                dsc.setSubtype(Node.type_Visor);
                if (objType == LQD) {
                    dsc.setSubtypelqd(Node.LQD);
                } else {
                    dsc.setSubtypelqd(Node.CRISP);
                }

                UseCase casoUso = readXMLUseCase("./help/" + dsc.getName());
                if (casoUso == null) {
                    this.useCaseTextArea.setText("No use case available");
                } else {
                    this.useCaseTextArea.setText(casoUso.toString());
                    this.useCaseTextArea.setCaretPosition(0);
                }

                status.setText("Click on the draw area to insert a new node");
                cursorDraw = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
                graphDiagram.setToolTipText("Click on the draw area to insert a visor of results node");

                methodsSelectionTree.setSelectionPath(null);
                preprocessTree.setSelectionPath(null);
                postprocessSelectionTree.setSelectionPath(null);
                testSelectionTree.setSelectionPath(null);
                invisible.setSelected(true);

            } else {
                cursorAction = GraphPanel.SELECTING;

                dsc = new ExternalObjectDescription((ExternalObjectDescription) (node.getUserObject()));
                dsc.setSubtype(Node.type_Method);
                if (objType == LQD) {
                    dsc.setSubtypelqd(Node.LQD);
                } else {
                    dsc.setSubtypelqd(Node.CRISP);
                }

                UseCase casoUso = readXMLUseCase("./help/" + dsc.getName());
                if (casoUso == null) {
                    this.useCaseTextArea.setText("No use case available");
                } else {
                    this.useCaseTextArea.setText(casoUso.toString());
                    this.useCaseTextArea.setCaretPosition(0);
                }

                cursorDraw = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

                graphDiagram.setToolTipText("Click twice into a node to view its properties");
                methodsSelectionTree.setSelectionPath(null);
                preprocessTree.setSelectionPath(null);
                postprocessSelectionTree.setSelectionPath(null);
                testSelectionTree.setSelectionPath(null);
                testSelectionTree.setSelectionPath(null);
                //selectButton.setSelected(true);
                status.setText("Click in a node to select it");
            }
        }
    }//GEN-LAST:event_visualizeSelectionTreeValueChanged

    /**
     * Undo button
     * @param evt Event
     */
    private void undoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoButtonActionPerformed
        if (vector_undo.size() > 0) {
            /***************************************************************
             *********************  EDUCATIONAL KEEL  **********************
             **************************************************************/
            if (Frame.buttonPressed == 0) //Button Experiments pressed
            {
                vector_redo.insertElementAt(copyGraph(experimentGraph), 0);
                experimentGraph = copyGraph((Graph) vector_undo.elementAt(0));
                vector_undo.removeElementAt(0);
                redoButton.setEnabled(true);
                redoItem.setEnabled(true);
                if (vector_undo.size() == 0) {
                    undoButton.setEnabled(false);
                    undoItem.setEnabled(false);
                }
                experimentGraph.setModified(true);
                graphDiagramINNER.mainGraph = experimentGraph;
                graphDiagramINNER.repaint();
            } else //Button Teaching pressed
            {
                //Warning message if the window of partitions is opened and the
                //user presses "undo"
                if (getExecDocentWindowState() == false) {
                    Object[] options = {"OK", "CANCEL"};
                    int n = JOptionPane.showOptionDialog(this, "The actual experiment is opened!. \n" +
                            "OK presses to STOP experiment.Results will be losed. \n", "Warning!",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                            null, options, options[0]);
                    if (n == JOptionPane.YES_OPTION) {
                        deleteExecDocentWindow();
                        closedEducationalExec(null);

                        vector_redo.insertElementAt(copyGraph(experimentGraph), 0);
                        experimentGraph = copyGraph((Graph) vector_undo.elementAt(0));
                        vector_undo.removeElementAt(0);
                        redoButton.setEnabled(true);
                        redoItem.setEnabled(true);
                        if (vector_undo.size() == 0) {
                            undoButton.setEnabled(false);
                            undoItem.setEnabled(false);
                        }
                        experimentGraph.setModified(true);
                        graphDiagramINNER.mainGraph = experimentGraph;
                        graphDiagramINNER.repaint();
                    }
                } else {
                    vector_redo.insertElementAt(copyGraph(experimentGraph), 0);
                    experimentGraph = copyGraph((Graph) vector_undo.elementAt(0));
                    vector_undo.removeElementAt(0);
                    redoButton.setEnabled(true);
                    redoItem.setEnabled(true);
                    if (vector_undo.size() == 0) {
                        undoButton.setEnabled(false);
                        undoItem.setEnabled(false);
                    }
                    experimentGraph.setModified(true);
                    graphDiagramINNER.mainGraph = experimentGraph;
                    graphDiagramINNER.repaint();
                }
            }
        /***************************************************************
         *********************  EDUCATIONAL KEEL  **********************
         **************************************************************/
        }
    }//GEN-LAST:event_undoButtonActionPerformed
    /**
     * Redo button
     * @param evt Event
     */
    private void redoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoButtonActionPerformed
        /***************************************************************
         *********************  EDUCATIONAL KEEL  **********************
         **************************************************************/
        if (Frame.buttonPressed == 0) //Button Experiments pressed
        {
            vector_undo.insertElementAt(copyGraph(experimentGraph), 0);
            experimentGraph = copyGraph((Graph) vector_redo.elementAt(0));
            vector_redo.removeElementAt(0);
            undoButton.setEnabled(true);
            undoItem.setEnabled(true);
            if (vector_redo.size() == 0) {
                redoButton.setEnabled(false);
                redoItem.setEnabled(false);
            }
            experimentGraph.setModified(true);
            graphDiagramINNER.mainGraph = experimentGraph;
            graphDiagramINNER.repaint();
        } else //Buttton Teaching pressed
        {
            /*Warning message if the window of partitions is opened and the
            user presses "redo" experiment opened*/
            if (getExecDocentWindowState() == false) {
                Object[] options = {"OK", "CANCEL"};
                int n = JOptionPane.showOptionDialog(this, "The actual experiment is opened!. \n" +
                        "OK presses to STOP experiment.Results will be losed. \n", "Warning!",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                        null, options, options[0]);
                if (n == JOptionPane.YES_OPTION) {
                    deleteExecDocentWindow();
                    closedEducationalExec(null);

                    vector_undo.insertElementAt(copyGraph(experimentGraph), 0);
                    experimentGraph = copyGraph((Graph) vector_redo.elementAt(0));
                    vector_redo.removeElementAt(0);
                    undoButton.setEnabled(true);
                    undoItem.setEnabled(true);
                    if (vector_redo.size() == 0) {
                        redoButton.setEnabled(false);
                        redoItem.setEnabled(false);
                    }
                    experimentGraph.setModified(true);
                    graphDiagramINNER.mainGraph = experimentGraph;
                    graphDiagramINNER.repaint();
                }
            } else {
                vector_undo.insertElementAt(copyGraph(experimentGraph), 0);
                experimentGraph = copyGraph((Graph) vector_redo.elementAt(0));
                vector_redo.removeElementAt(0);
                undoButton.setEnabled(true);
                undoItem.setEnabled(true);
                if (vector_redo.size() == 0) {
                    redoButton.setEnabled(false);
                    redoItem.setEnabled(false);
                }
                experimentGraph.setModified(true);
                graphDiagramINNER.mainGraph = experimentGraph;
                graphDiagramINNER.repaint();
            }

        }
    /***************************************************************
     *********************  EDUCATIONAL KEEL  **********************
     **************************************************************/
    }//GEN-LAST:event_redoButtonActionPerformed

    /**
     * Grid painting
     * @param evt Event
     */
private void gridItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gridItemActionPerformed
    graphDiagramINNER.paintGrid = gridItem.isSelected();
    graphDiagramINNER.repaint();

}//GEN-LAST:event_gridItemActionPerformed
    /**
     * Datasets button
     * @param evt Event
     */
private void datasetsItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_datasetsItemActionPerformed
    panData_actionPerformed(evt);
}//GEN-LAST:event_datasetsItemActionPerformed
    /**
     * Undo button
     * @param evt Event
     */
private void undoItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoItemActionPerformed
    undoButtonActionPerformed(evt);
}//GEN-LAST:event_undoItemActionPerformed
    /**
     * Redo button
     * @param evt Event
     */
private void redoItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoItemActionPerformed
    redoButtonActionPerformed(evt);
}//GEN-LAST:event_redoItemActionPerformed
    /**
     * Select button
     * @param evt Event
     */
private void selectItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectItemActionPerformed
    seleccionar_actionPerformed(evt);
}//GEN-LAST:event_selectItemActionPerformed
    /**
     * Run button
     * @param evt Event
     */
private void runExpItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runExpItemActionPerformed
    if (objType != LQD) {
        generateExperimentDirectories();
    } else {
        generateExperimentDirectoriesLQD();
    }
}//GEN-LAST:event_runExpItemActionPerformed
    /**
     * Options button
     * @param evt Event
     */
private void executionOptItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_executionOptItemActionPerformed
    ExecutionOptions eop = new ExecutionOptions(this, "Execution Options", true);
    eop.setVisible(true);
}//GEN-LAST:event_executionOptItemActionPerformed
    /**
     * Flow button
     * @param evt Event
     */
private void insertDataflowItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertDataflowItemActionPerformed

    flujo_actionPerformed(evt);
}//GEN-LAST:event_insertDataflowItemActionPerformed
    /**
     * About button
     * @param evt Event
     */
private void aboutItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutItemActionPerformed
    Credits creditos = new Credits(this);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = creditos.getSize();
    if (frameSize.height > screenSize.height) {
        frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
        frameSize.width = screenSize.width;
    }
    creditos.setLocation((screenSize.width - frameSize.width) / 2,
            (screenSize.height - frameSize.height) / 2);
    creditos.setVisible(true);
    this.setEnabled(false);
}//GEN-LAST:event_aboutItemActionPerformed
    /**
     * Checks panel resize
     * @param evt Event
     */
private void checksDatasetsScrollPaneComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_checksDatasetsScrollPaneComponentResized
    panelDatasets.reload(this.expType);
}//GEN-LAST:event_checksDatasetsScrollPaneComponentResized
    /**
     * Status bar event
     * @param evt Event
     */
private void statusBarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusBarItemActionPerformed
    status.setVisible(statusBarItem.isSelected());
}//GEN-LAST:event_statusBarItemActionPerformed

    /**
     * Subgroup discovery experiment
     * @param evt Event
     */
private void subgroupDiscoveryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subgroupDiscoveryButtonActionPerformed
    objType = SUBGROUPDISCOVERY;
    selectPostprocessMethods.setVisible(false);
    numberKFoldCross = Integer.valueOf(kValueField.getText());

    //Control of this variables because of they could be
    //modified in experiments
    if (objType == LQD) {
        //numberKFoldCross=10;
        // cvType = Experiments.PK;
        }
    if (kfoldButton.isSelected() && numberKFoldCross < 1) {
        JOptionPane.showMessageDialog(this, "Number of folds must be at least 1",
                "Invalid number of folds", JOptionPane.ERROR_MESSAGE);
    } else {
        ((CardLayout) selectionPanel1.getLayout()).show(selectionPanel1, "datasetsChecksCard");
        status.setText("Select an initial set of dataset and then click on the drawing panel");
        selectButton.setEnabled(true);
        enableMainToolBar(true);

        if (objType != LQD) {
            activateUpperMenu();
        } else {
            quicktools.getComponent(quicktools.getComponentCount() - 1).setEnabled(true);
        }

        if (objType == LQD) {
            helpContent.muestraURL(this.getClass().getResource("/contextualHelp/data_set_exp_lqd.html"));
        } else {
            helpContent.muestraURL(this.getClass().getResource("/contextualHelp/data_set_exp.html"));
        }

        this.expType = Experiments.CLASSIFICATION;
        if (kfoldButton.isSelected()) {
            cvType = Experiments.PK;
        } else if (fivetwoButton.isSelected()) {
            cvType = Experiments.P5X2;
        } else {
            cvType = Experiments.PnoVal;
        }
        //we want to prevent that this panel will ever show again
        initialPanel1.setVisible(false);
        //now, we load the datasets and the different methods
        undoButton.setEnabled(false);
        redoButton.setEnabled(false);
        continueExperimentGeneration();
        ((CardLayout) selectionPanel1.getLayout()).show(selectionPanel1, "datasetsChecksPanel");
    }
    deleteItem.setEnabled(false);
    if (objType != LQD) {
        activateMenuItems();
        runExpItem.setEnabled(false);
        saveButton.setEnabled(false);
        insertDataflowItem.setEnabled(false);
    }
}//GEN-LAST:event_subgroupDiscoveryButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new Experiments().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutItem;
    public javax.swing.JScrollPane checksDatasetsScrollPane;
    private javax.swing.JButton classificationButton;
    private javax.swing.JMenuItem contentItem;
    public javax.swing.JButton cursorFlux;
    private javax.swing.JCheckBox dailyElectricCheck;
    public javax.swing.JPanel datasetsChecksPanel;
    private javax.swing.JCheckBoxMenuItem datasetsItem;
    private javax.swing.JLabel datasetsLabel1;
    private javax.swing.JLabel datasetsLabel2;
    public javax.swing.JButton deleteItem;
    public keel.GraphInterKeel.experiments.DinamicDataset dinDatasets;
    public javax.swing.JPanel dinDatasetsPanel;
    public javax.swing.JScrollPane dinDatasetsScrollPane;
    private javax.swing.JSplitPane divider1;
    private javax.swing.JMenu editMenu;
    private javax.swing.JCheckBox ele1Check;
    private javax.swing.JMenuItem executionOptItem;
    private javax.swing.JLabel experimentLabel;
    private javax.swing.JPanel experimentPanel;
    private javax.swing.JSeparator fileBottomSeparator;
    private javax.swing.JSeparator fileTopSeparator;
    private javax.swing.JRadioButton fivetwoButton;
    private javax.swing.JCheckBox friedmanCheck;
    public keel.GraphInterKeel.experiments.GraphPanel graphDiagramINNER;
    private javax.swing.JCheckBoxMenuItem gridItem;
    public keel.GraphInterKeel.help.HelpContent helpContent;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JCheckBoxMenuItem helpPanelItem;
    private javax.swing.JTabbedPane helpUseCaseTabbedPanel;
    private javax.swing.JButton importButton1;
    private javax.swing.JButton importButton2;
    private javax.swing.JMenuItem importItem;
    private javax.swing.JPanel initialPanel1;
    public javax.swing.JMenuItem insertDataflowItem;
    private javax.swing.JButton invert2Button;
    private javax.swing.JButton invert2Button1;
    private javax.swing.JTextField kValueField;
    private javax.swing.JLabel keelLabel2;
    private javax.swing.JLabel keelLabel3;
    private javax.swing.JLabel keelLabel4;
    private javax.swing.JLabel keelLabel5;
    private javax.swing.JLabel keelLabel6;
    private javax.swing.JLabel keelLabel7;
    private javax.swing.JRadioButton kfoldButton;
    private javax.swing.JPanel leftPanel1;
    private javax.swing.JMenuItem loadExpItem;
    private javax.swing.JCheckBox machineCpuCheck;
    private javax.swing.JPanel mainPanel;
    public javax.swing.JSplitPane mainSplitPane1;
    private javax.swing.JToolBar mainToolBar1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JPanel methodsPanel;
    private javax.swing.JScrollPane methodsScrollPanel;
    public javax.swing.JTree methodsSelectionTree;
    private javax.swing.JButton newButton;
    private javax.swing.JMenuItem newExpItem;
    private javax.swing.JButton openButton;
    public keel.GraphInterKeel.experiments.SelectData panelDatasets;
    private javax.swing.ButtonGroup partitionGroup;
    private javax.swing.JPanel partitionPanel1;
    private javax.swing.JLabel partitionsLabel;
    private javax.swing.JPanel postprocessPanel;
    private javax.swing.JScrollPane postprocessScroll;
    public javax.swing.JTree postprocessSelectionTree;
    private javax.swing.JPanel preprocessPanel;
    private javax.swing.JScrollPane preprocessScroll;
    public javax.swing.JTree preprocessTree;
    private javax.swing.JToolBar quicktools;
    private javax.swing.JButton redoButton;
    private javax.swing.JMenuItem redoItem;
    private javax.swing.JButton regressionButton;
    private javax.swing.JPanel regressionPanel;
    public javax.swing.JButton runButton;
    public javax.swing.JMenuItem runExpItem;
    public javax.swing.JMenuItem saveAsExpItem;
    public javax.swing.JButton saveButton;
    public javax.swing.JMenuItem saveExpItem;
    private javax.swing.JMenuItem seedItem;
    public javax.swing.JButton selecDatasets;
    private javax.swing.JButton selectAll2Button;
    private javax.swing.JButton selectAll2Button1;
    private javax.swing.JButton selectButton;
    private javax.swing.JMenuItem selectItem;
    public javax.swing.JButton selectMethods;
    public javax.swing.JButton selectPostprocessMethods;
    public javax.swing.JButton selectPreprocessMethods;
    public javax.swing.JButton selectTestMethods;
    public javax.swing.JButton selectVisualizeMethods;
    public javax.swing.JPanel selectionPanel1;
    private javax.swing.JToolBar.Separator separator1;
    private javax.swing.JToolBar.Separator separator2;
    private javax.swing.JToolBar.Separator separator3;
    private javax.swing.JToolBar.Separator separator4;
    private javax.swing.JToolBar.Separator separator5;
    public javax.swing.JButton showAlgButton;
    private javax.swing.JButton showHelpButton;
    private javax.swing.JButton snapshotButton;
    private javax.swing.JMenuItem snapshotItem;
    public javax.swing.JLabel status;
    public javax.swing.JCheckBoxMenuItem statusBarItem;
    private javax.swing.JButton subgroupDiscoveryButton;
    private javax.swing.JPanel testPanel;
    private javax.swing.JScrollPane testScroll;
    public javax.swing.JTree testSelectionTree;
    private javax.swing.JSeparator toolsBottomSeparator;
    private javax.swing.JMenu toolsMenu;
    private javax.swing.JSeparator toolsTopSeparator;
    private javax.swing.JButton undoButton;
    private javax.swing.JMenuItem undoItem;
    private javax.swing.JButton unsupervisedButton;
    private javax.swing.JPanel unsupervisedPanel;
    private javax.swing.JScrollPane useCaseScrollPane;
    public javax.swing.JTextArea useCaseTextArea;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JPanel visualizePanel;
    private javax.swing.JScrollPane visualizeScroll;
    public javax.swing.JTree visualizeSelectionTree;
    private javax.swing.JCheckBox weatherCheck;
    private javax.swing.JRadioButton withoutButton;
    // End of variables declaration//GEN-END:variables
    //variables used outside
    public JCheckBox invisible = new JCheckBox();


    //*****************************************************************
    //*****************************************************************
    //                        BEGIN OF METHODS
    //*****************************************************************
    //*****************************************************************
    /**
     * Built a LQD experiment
     */
    private void lqd() {
        //Rename the type of experiments
        summary = false;
        experimentLabel.setText("Type of experiments with Low Quality Datasets");
        unsupervisedButton.setText("Unsupervised Learning LQD");
        regressionButton.setText("Regression LQD");
        classificationButton.setText("Classification LQD");

        experimentGraph.objective = objType;
        partitionPanel1.setVisible(false);
        setTitle("Experiments Design of Low Quality Data");
        activateUpperMenu_principals();
        status.setText("Select the type of experiment able to use Low Quality Data");
        helpContent.muestraURL(this.getClass().getResource("/contextualHelp/exp_intro_lqd.html"));
        unsupervisedButton.setEnabled(false);
        regressionButton.setEnabled(false);


        //We don't want the help
        helpUseCaseTabbedPanel.setVisible(false);
    }

    /**
     * Activates upper menu
     */
    private void activateUpperMenu() {
        for (int i = 0; i < quicktools.getComponentCount(); i++) {
            quicktools.getComponent(i).setEnabled(true);
        }
    }
    /**
     * Activates main elements of upper menu
     */
    public void activateUpperMenu_principals() {

        quicktools.getComponent(0).setEnabled(true);
        quicktools.getComponent(1).setEnabled(true);
    //showHelpButton.setEnabled(true);

    }
    /**
     * Disables upper menu
     */
    private void deactivateUpperMenu() {
        for (int i = 0; i < quicktools.getComponentCount(); i++) {
            quicktools.getComponent(i).setEnabled(false);
        }


    }

    /**
     * Disables left menu
     */
    private void deactivateLeftMenu() {
        for (int i = 0; i < mainToolBar1.getComponentCount(); i++) {
            mainToolBar1.getComponent(i).setEnabled(false);
        }
    }

    /**
     * Change state
     * @param event Event
     */
    public void itemStateChanged(ItemEvent event) {
    }

    /**
     * Enables main tool bar
     */
    private void enableMainToolBar(boolean state) {
        for (int i = 0; i < mainToolBar1.getComponentCount(); i++) {
            mainToolBar1.getComponent(i).setEnabled(state);
        }
        if (objType == LQD) {
            selectPostprocessMethods.setEnabled(false);
        }
    }

    /**
     * Stores the experiment to disk
     * @return the user's option (if accepted to save or declined to save) 
     */
    public int saveExperiment(int option) {


        experimentGraph.objective = this.objType;
        int opcion = 0;
        Mapping mapping = new Mapping();
        try {
            if ((experimentGraph.getName() == null) || (option == 1)) {
                JFileChooser f;
                if (lastDirectory == null) {
                    f = new JFileChooser();
                } else {
                    f = new JFileChooser(lastDirectory);
                }
                f.setDialogTitle("Save experiment");
                String exten[] = {"xml"};
                f.setFileFilter(new ArchiveFilter2(exten, "Experiments (.xml)"));
                opcion = f.showSaveDialog(this);
                if (opcion == JFileChooser.APPROVE_OPTION) {
                    lastDirectory = f.getCurrentDirectory().getAbsolutePath();
                    String nombre = f.getSelectedFile().getAbsolutePath();
                    if (!nombre.toLowerCase().endsWith(".xml")) {
                        // Add correct extension
                        nombre += ".xml";
                    }
                    File tmp = new File(nombre);
                    if (!tmp.exists() || JOptionPane.showConfirmDialog(this, "File " + nombre + " already exists. Do you want to replace it?", "Confirm", JOptionPane.YES_NO_OPTION, 3) == JOptionPane.YES_OPTION) {
                        experimentGraph.setName(nombre);
                    }
                }
            }
            if (experimentGraph.getName() != null) {
                try {
                    File f = new File(experimentGraph.getName());
                    if (f.exists()) {
                        f.delete();
                    }

                    if (objType == LQD) {
                        mapping.loadMapping(this.getClass().getResource(
                                "/mapping/mapeoExperimentoLQD.xml"));
                    } else {
                        mapping.loadMapping(this.getClass().getResource(
                                "/mapping/mapeoExperimento.xml"));
                    }

                    FileOutputStream file = new FileOutputStream(f);
                    Marshaller marshaller = new Marshaller(new OutputStreamWriter(file));
                    marshaller.setMapping(mapping);
                    marshaller.marshal(experimentGraph);
                    experimentGraph.setModified(false);
                    status.setText("Experiment saved successfully");
                    if (objType == LQD) {
                        JOptionPane.showMessageDialog(this, "Experiment saved successfully",
                                "Saved", JOptionPane.INFORMATION_MESSAGE);
                    }

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error saving experiment",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    System.err.println(e);
                }
            }

        } catch (Exception e) {
            System.err.println(e);
            return 1;
        }
        return opcion;

    }

    /**
     * Search from next node
     * @param visitados Visited nodes
     * @return Next node
     */
    private int nextNode(Vector visitados) {
        // check node dependencies
        boolean valido = false;
        int valor = 0;
        for (int i = 0; i < experimentGraph.numNodes() && !valido; i++) {
            valor = i;
            valido = true;
            // it hasn't been processed
            if (visitados.contains(new Integer(valor)) == false) //therefore is a node != type_dataset
            {
                // Check dependencies
                for (int j = 0; j < experimentGraph.numArcs() && valido; j++) {
                    Arc arco = experimentGraph.getArcAt(j);
                    if (arco.getDestination() == i) {
                        if (visitados.contains(new Integer(arco.getSource())) == false) {
                            valido = false;
                        }
                    }
                }
            } else {
                valido = false;
            }
        }

        if (valido) {
            visitados.addElement(new Integer(valor));
            return valor;
        } else {
            return -1;
        }
    }

     /**
     * Search from next node
     * @param visitados Visited nodes
     * @return Next node
     */
    private int nextNodeLQD(Vector visitados) {
        // check node dependencies
        boolean valido = false;
        int valor = 0;
        for (int i = 0; i < experimentGraph.numNodes() && !valido; i++) {
            valor = i;
            valido = true;
            // it hasn't been processed
            if (visitados.contains(new Integer(valor)) == false) //therefore is a node != type_dataset
            {
                // Check dependencies
                for (int j = 0; j < experimentGraph.numArcs() && valido; j++) {
                    Arc arco = experimentGraph.getArcAt(j);
                    if (arco.getDestination() == experimentGraph.getNodeAt(i).id) {
                        int position = 0;
                        for (int n = 0; n < experimentGraph.numNodes(); n++) {
                            if (arco.getSource() == experimentGraph.getNodeAt(n).id) {
                                position = n;
                                break;
                            }

                        }
                        if (visitados.contains(position) == false) {
                            valido = false;
                        }
                    }
                }
            } else {
                valido = false;
            }
        }

        if (valido) {
            visitados.addElement(new Integer(valor));
            return valor;
        } else {
            return -1;
        }
    }

    /**
     * Read XML files
     * @param listing File
     * @param rootInsert Root node
     */
    public void insertDirectoryData(java.net.URL listing, String rootInsert) {
        // SGL - Loading of an internal resource list method file in XML Format
        Document doc = new Document();
        try {
            SAXBuilder builder = new SAXBuilder();
            doc = builder.build(listing);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Dataset specification XML file not found");
            return;
        }

        List datasets = doc.getRootElement().getChildren();
        listData = new DatasetXML[datasets.size()];
        for (int i = 0; i < datasets.size(); i++) {
            listData[i] = new DatasetXML((Element) datasets.get(i));
            panelDatasets.insert(listData[i], "/data/");
            dinDatasets.insert(listData[i], "/data/");
        }

        if (objType != IMBALANCED) {
            panelDatasets.sortDatasets();
            dinDatasets.sortDatasets();
        }
    }

    /**
     * Creates the trees (deprecated)
     * @param top root of the tree
     */
    public void createNodes(DefaultMutableTreeNode top) {
        // DefaultMutableTreeNode categ = null;
        DefaultMutableTreeNode categ2 = null;
        DefaultMutableTreeNode categ3 = null;
        DefaultMutableTreeNode hoja = null;

        hoja = new DefaultMutableTreeNode(new HelpSheet("Introduction", this.getClass().getResource("/help/exp_intro.html")));
        top.add(hoja);

        hoja = new DefaultMutableTreeNode(new HelpSheet("Menu Bar", this.getClass().getResource("/help/exp_menu.html")));
        top.add(hoja);

        hoja = new DefaultMutableTreeNode(new HelpSheet("Tools bar", this.getClass().getResource("/help/exp_tool.html")));
        top.add(hoja);

        hoja = new DefaultMutableTreeNode(new HelpSheet("Status bar", this.getClass().getResource("/help/exp_status.html")));
        top.add(hoja);

        // --> Experiment graph
        categ2 = new DefaultMutableTreeNode("Experiment Graph");
        top.add(categ2);
        hoja = new DefaultMutableTreeNode(new HelpSheet("Datasets", this.getClass().getResource("/help/exp_datasets.html")));
        categ2.add(hoja);

        // --> Algorithms
        categ3 = new DefaultMutableTreeNode("Algorithms");
        categ2.add(categ3);
        hoja = new DefaultMutableTreeNode(new HelpSheet("Types", this.getClass().getResource("/help/exp_algotypes.html")));
        categ3.add(hoja);

        hoja = new DefaultMutableTreeNode(new HelpSheet("Insert algorithm",
                this.getClass().getResource("/help/exp_algoins.html")));
        categ3.add(hoja);

        hoja = new DefaultMutableTreeNode(new HelpSheet(
                "Parameters configuration", this.getClass().getResource(
                "/help/exp_algopar.html")));
        categ3.add(hoja);

        hoja = new DefaultMutableTreeNode(new HelpSheet("Connections", this.getClass().getResource("/help/exp_conn.html")));
        categ2.add(hoja);

        hoja = new DefaultMutableTreeNode(new HelpSheet("Generate experiment",
                this.getClass().getResource("/help/exp_gen.html")));
        top.add(hoja);

        hoja = new DefaultMutableTreeNode(new HelpSheet("Interface management",
                this.getClass().getResource("/help/exp_inter.html")));
        top.add(hoja);

    }

    /**
     * Create the data sets' node, and load the lists of 
     * data sets from the XML
     * @param rootInsert path to the data sets
     */
    private void createDatasetNodes(String raiz) {
        try {
            File data = new File(raiz);
            java.net.URL recursoInterno = data.toURL();
            //System.out.println(recursoInterno);

            if (recursoInterno == null) {
                System.err.println("Datasets.xml file not found at resources directory");
            } else {
                insertDirectoryData(recursoInterno, raiz);
            }

            // Add datasets tree structure
            experimentGraph.setType(expType);
            panelDatasets.reload(expType);
            dinDatasets.reload(expType);
            if (objType == LQD) {
                panelDatasets.lqd_crisp.setVisible(true);
                panelDatasets.crisp_lqd.setVisible(true);
                panelDatasets.selectAll.setText("Select All LQD");
                panelDatasets.invertSelection.setText("Invert LQD");
                panelDatasets.importB.setVisible(false);
                panelDatasets.selectAllUser.setVisible(false);
                panelDatasets.invertSelectionUser.setVisible(false);

                dinDatasets.lqd_crisp.setVisible(true);
                dinDatasets.crisp_lqd.setVisible(true);
                dinDatasets.selectAll.setText("Select All LQD");
                dinDatasets.invertSelection.setText("Invert LQD");
                dinDatasets.importB.setVisible(false);
                dinDatasets.selectAllUser.setVisible(false);
                dinDatasets.invertSelectionUser.setVisible(false);

                dinDatasets.reload_lqd_crisp();
                int position = dinDatasets.reload_crisp_lqd();
                dinDatasets.reload_crisp(position);

            }

        } catch (java.net.MalformedURLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Creates the tree for the standard methods from the XML in disk 
     */
    private void createAlgorithmNodes(DefaultMutableTreeNode top, String raiz) {
        // add .jar like original specification, and a list of .jar
        // contents in a .dir file with a list of algorithms
        // included as resources in keel's jar

        try {
            File data = new File(raiz);
            java.net.URL recursoInterno = data.toURL();
            //System.out.println(recursoInterno);

            if (recursoInterno == null) {
                System.err.println("XML description of algorithms file not found at resources directory");
            } else {
                insertInnerListing(top, recursoInterno, raiz);
            }
        } catch (java.net.MalformedURLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Creates the tree for the test methods from the XML in disk 
     */
    private void createTestAlgorithmNodes(DefaultMutableTreeNode top, String raiz) {

        try {
            File data = new File(raiz);
            java.net.URL recursoInterno = data.toURL();
            //System.out.println(recursoInterno);

            if (recursoInterno == null) {
                System.err.println("XML description of tests/visors file not found at resources directory");
            } else {
                insertaListadoInterno_test(top, recursoInterno, raiz);
            }
        } catch (java.net.MalformedURLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Restore the experiment's graph from the provided one
     * @param aux the reference graph
     * @return the new restored graph 
     */
    private Graph restoreGraph(Graph aux) {
        // Restore graph from file
        Graph g = new Graph();
        graphDiagramINNER.mainGraph = g;
        g.setName(aux.getName());
        g.setSeed(aux.getSeed());
        g.setId(aux.getId());
        g.setType(aux.getType());

        String dirInterno = new String("/listAlgorithms");

        java.net.URL raizJar = getClass().getResource(dirInterno);

        for (int i = 0; i < aux.numNodes(); i++) {

            Node a = aux.getNodeAt(i);


            for (int ly = 0; ly < a.dsc.getNamesLength(); ly++) {
                String pathCorregido = a.dsc.getPath(ly);
                if (pathCorregido != null) {
                    if (pathCorregido.startsWith("jar:") && pathCorregido.indexOf(dirInterno) >= 0) {
                        int ptoCorte = pathCorregido.indexOf(dirInterno) + dirInterno.length();
                        pathCorregido = raizJar.toString() + pathCorregido.substring(ptoCorte,
                                pathCorregido.length());

                        a.dsc.setPath(pathCorregido, ly);
                    }
                }
            }


            if (aux.objective == LQD) {
                continueExperimentGeneration();

            }

            if (a.type == Node.type_Algorithm) {
                if (objType == LQD) {
                    Parameters compose = (new Parameters(a.dsc.getPath(0) + a.dsc.getName(0) + ".xml", false));
                    ((Parameters) (a.par.elementAt(0))).setcost_instance(compose.getCost_instances());
                    ((Parameters) (a.par.elementAt(0))).setcrisp(compose.getCrisp());
                    ((Parameters) (a.par.elementAt(0))).setfuzzy(compose.getFuzzy());
                    g.insertNode(new Algorithm(a.dsc, a.getPosicion(), graphDiagramINNER,
                            a.par, a.id, a.getTypelqd(), a.dsc.arg));
                } else {
                    g.insertNode(new Algorithm(a.dsc, a.getPosicion(), graphDiagramINNER,
                            ((Algorithm) a).par, a.id, a.getTypelqd(), a.dsc.arg));
                }




            } else if (a.type == Node.type_Dataset) {
                g.insertNode(new DataSet(a.dsc, a.getPosicion(), graphDiagramINNER,
                        ((DataSet) a).tableVector, ((DataSet) a).modified, a.id, a.getTypelqd()));
            } else if (a.type == Node.type_Jclec) {
                g.insertNode(new Jclec(a.dsc, a.getPosicion(), graphDiagramINNER,
                        ((Jclec) a).param, a.id));
            } else if (a.type == Node.type_Test) {
                g.insertNode(new Test(a.dsc, a.getPosicion(), graphDiagramINNER,
                        ((Test) a).par, a.id, a.getTypelqd()));
            } else if (a.type == Node.type_userMethod) {
                UserMethod mu = new UserMethod(a.dsc, a.getPosicion(),
                        graphDiagramINNER, ((UserMethod) a).parametersUser, a.id);
                mu.command = ((UserMethod) a).command;
                mu.patternFile = ((UserMethod) a).patternFile;
                g.insertNode(mu);
            }
        }
        for (int i = 0; i < aux.numArcs(); i++) {
            Arc a = aux.getArcAt(i);
            Arc b = new Arc(a.getSource(), a.getDestination(), graphDiagramINNER);
            g.insertArc(b);
        }
        g.setModified(false);
        return g;
    }

    /**
     * Creates a copy of the graph
     * @param aux the reference graph
     * @return the new graph
     */
    private Graph copyGraph(Graph aux) {
        // Copy graph
        Graph vieo = graphDiagramINNER.mainGraph;
        Graph g = new Graph();
        graphDiagramINNER.mainGraph = g;
        g.setName(aux.getName());
        g.setSeed(aux.getSeed());
        g.setId(aux.getId());
        g.setType(aux.getType());
        g.autoSeed = aux.autoSeed;
        g.setModified(aux.getModified());

        for (int i = 0; i < aux.numNodes(); i++) {
            Node a = aux.getNodeAt(i);
            if (a.type == Node.type_Algorithm) {
                g.insertNode(new Algorithm(a.dsc, a.getPosicion(), graphDiagramINNER,
                        ((Algorithm) a).par, a.id, a.type_lqd, a.dsc.arg));
            } else if (a.type == Node.type_Dataset) {
                g.insertNode(new DataSet(a.dsc, a.getPosicion(), graphDiagramINNER,
                        ((DataSet) a).tableVector, ((DataSet) a).modified, a.id, a.getTypelqd()));
            } else if (a.type == Node.type_Jclec) {
                g.insertNode(new Jclec(a.dsc, a.getPosicion(), graphDiagramINNER,
                        ((Jclec) a).param, a.id));
            } else if (a.type == Node.type_Test) {
                g.insertNode(new Test(a.dsc, a.getPosicion(), graphDiagramINNER,
                        ((Test) a).par, a.id, a.type_lqd));
            } else if (a.type == Node.type_userMethod) {
                UserMethod mu = new UserMethod(a.dsc, a.getPosicion(),
                        graphDiagramINNER, ((UserMethod) a).parametersUser, a.id);
                mu.command = ((UserMethod) a).command;
                mu.patternFile = ((UserMethod) a).patternFile;
                g.insertNode(mu);
            }
        }
        for (int i = 0; i < aux.numArcs(); i++) {
            Arc a = aux.getArcAt(i);
            Arc b = new Arc(a.getSource(), a.getDestination(), graphDiagramINNER);
            g.insertArc(b);
        }
        graphDiagramINNER.mainGraph = vieo;
        return g;
    }

    /**
     * Adds a file to the tree
     * @param f the file
     * @param nombre the name of the file
     * @param actual the node of the tree in which the file is added
     */
    private void insertFile(File f, String nombre, DefaultMutableTreeNode actual) {
        // Add a file above his tree
        String id = f.getName().substring(0, f.getName().lastIndexOf('.'));
        String path = nombre.substring(0, nombre.lastIndexOf('/') + 1);
        DefaultMutableTreeNode fich = new DefaultMutableTreeNode(
                new ExternalObjectDescription(id, path, 0));
        actual.add(fich);
    }

    /**
     * Adds a directory and their content (recursively)
     * @param f the directory
     * @param nombre the name of the directory
     * @param actual the node of the tree
     * @param filtro filter of the extensions in the directory
     * @param ins_files if true, the content files are added
     */
    private void insertDirectory(File f, String nombre, DefaultMutableTreeNode actual, FilenameFilter filtro, boolean ins_files) {
        // Add directory and their content (recursively)
        // files are added if ins_files is true

        String s[] = f.list(filtro);
        String path = nombre.substring(0, nombre.lastIndexOf('/') + 1);
        if (s.length != 0) {
            // add actual directory
            DefaultMutableTreeNode dir = new DefaultMutableTreeNode(
                    new ExternalObjectDescription(f.getName(), path, 0));
            actual.add(dir);

            // Analize their content and add (first directories, then files)
            // filtering
            Arrays.sort(s);
            for (int i = 0; i < s.length; i++) {
                File f2 = new File(nombre + "/" + s[i]);
                // ignore CVS directories
                if (f2.isDirectory() && !s[i].toLowerCase().equals("cvs")) {
                    insertDirectory(f2, nombre + "/" + s[i], dir, filtro,
                            ins_files);
                }
            }

            if (ins_files) {
                for (int i = 0; i < s.length; i++) {
                    File f2 = new File(nombre + "/" + s[i]);
                    if (!f2.isDirectory()) {
                        insertFile(f2, nombre + "/" + s[i], dir);
                    }
                }
            }
        }
    }

    /**
     * Reads a XML use case
     * @param fileName the path to the XML use case file
     * @return the read use case
     */
    public UseCase readXMLUseCase(String fileName) {

        UseCase casoUso = new UseCase();

        Document doc = new Document();
        try {
            SAXBuilder builder = new SAXBuilder();
            doc = builder.build(new File(fileName + ".xml"));

        //System.out.println("El fichero es: " + fileName + "\n");

        } catch (Exception e) {
            //e.printStackTrace();
            //System.err.println("Use Case XML file not found");
            return null;
        }

        // obtenemos todos los elementos del xml
        List methods = doc.getRootElement().getChildren();
        for (int i = 0; i < methods.size(); i++) {

            // Comprobamos si el elemento es generalDescription, ya que en caso
            // afirmativo tiene subhijos
            if (((Element) methods.get(i)).getName().equals(
                    "generalDescription")) {
                List genDesc = ((Element) methods.get(i)).getChildren();

                for (int j = 0; j < genDesc.size(); j++) {

                    // Obtenemos los parametersUser?metros
                    if (((Element) genDesc.get(j)).getName().equals(
                            "parameterSpec")) {
                        List param = ((Element) genDesc.get(j)).getChildren();
                        for (int k = 0; k < param.size(); k++) {
                            casoUso.addParameter(((Element) param.get(k)).getText());
                        }
                    } // Obtenemos las propiedades
                    else if (((Element) genDesc.get(j)).getName().equals(
                            "properties")) {
                        List properties = ((Element) genDesc.get(j)).getChildren();
                        for (int k = 0; k < properties.size(); k++) {
                            // Mostramos los values de cada tipo de propiedad
                            switch (k) {
                                case 0:
                                    if ((((Element) properties.get(k)).getText()).equals("Yes")) {
                                        casoUso.setContinuous(true);
                                    } else {
                                        casoUso.setContinuous(false);
                                    }
                                    break;
                                case 1:
                                    if ((((Element) properties.get(k)).getText()).equals("Yes")) {
                                        casoUso.setDiscretized(true);
                                    } else {
                                        casoUso.setDiscretized(false);
                                    }
                                    break;
                                case 2:
                                    if ((((Element) properties.get(k)).getText()).equals("Yes")) {
                                        casoUso.setInteger(true);
                                    } else {
                                        casoUso.setInteger(false);
                                    }
                                    break;
                                case 3:
                                    if ((((Element) properties.get(k)).getText()).equals("Yes")) {
                                        casoUso.setNominal(true);
                                    } else {
                                        casoUso.setNominal(false);
                                    }
                                    break;
                                case 4:
                                    if ((((Element) properties.get(k)).getText()).equals("Yes")) {
                                        casoUso.setWhitoutValues(true);
                                    } else {
                                        casoUso.setWhitoutValues(false);
                                    }
                                    break;
                                case 5:
                                    if ((((Element) properties.get(k)).getText()).equals("Yes")) {
                                        casoUso.setWithImprecise(true);
                                    } else {
                                        casoUso.setWithImprecise(false);
                                    }
                                    break;

                            }

                        }
                    } else { // Este es el caso de un hijo de
                        // generalDescription que no tiene subhijos
                        if (((Element) genDesc.get(j)).getName().equals(
                                "howWork")) {
                            casoUso.setHowWork(((Element) genDesc.get(j)).getText());
                        } else if (((Element) genDesc.get(j)).getName().equals(
                                "type")) {
                            casoUso.setType(((Element) genDesc.get(j)).getText());
                        } else // es el objetivo
                        {
                            casoUso.setObjective(((Element) genDesc.get(j)).getText());
                        }
                    }
                }
            } // Obtenemos las referencias
            else if (((Element) methods.get(i)).getName().equals("reference")) {
                List ref = ((Element) methods.get(i)).getChildren();
                for (int k = 0; k < ref.size(); k++) {
                    casoUso.addReference(((Element) ref.get(k)).getText());
                }
            } else if (((Element) methods.get(i)).getName().equals("name")) {
                casoUso.setName(((Element) methods.get(i)).getText());
            } else {// Es el ejemplo
                casoUso.setExample(((Element) methods.get(i)).getText());
            }
        }

        return casoUso;
    }

    /**
     * Tree changed method
     * @param e Event
     */
    public void arbol_valueChanged(TreeSelectionEvent e) {
        // Show help file associated to the item
        DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (nodo != null) {
            Object o = nodo.getUserObject();
            if (nodo.isLeaf()) {
                HelpSheet h = (HelpSheet) o;
                this.helpContent.muestraURL(h.adress);
            }
        }
    }

    /**
     * Resizes the ExternalObjectDescription of all nodes. If the number of data sets
     * has chenged, this function must be called.is different to 1, this function
     * must be called for the method's nodes.
     * @param node the node of the tree from which the search begins
     */
    public void redimAllNodes(DefaultMutableTreeNode node) {
        // node is visited exactly once
        ((ExternalObjectDescription) (node.getUserObject())).redim(Layer.numLayers);

        if (node.getChildCount() >= 0) {
            for (java.util.Enumeration e = node.children(); e.hasMoreElements();) {
                DefaultMutableTreeNode n = (DefaultMutableTreeNode) e.nextElement();
                redimAllNodes(n);
            }
        }
    }

    /**
     * Sets the size of the extenal description objects, and the
     * number of data sets
     * @param values An array with the names of all the data sets (one in each position of the array)
     */
    public void setNumDatasets(String[] values) {

        Layer.numLayers = values.length;
        //System.out.println("New data sets number = " + Layer.numLayers);

        //panelDatasets.checks = new Vector();
        // redimAllNodes(top);
        redimAllNodes(top4);
        redimAllNodes(top2);
        redimAllNodes(top5);
        redimAllNodes(top6);
        redimAllNodes(top7);

    }

    /**
     * Gets the partition type of the experiment
     * @return the partition type
     */
    public int partitionType() {
        return this.cvType;
    }

    /**
     * Find node in the tree
     * @param root Root of the tree
     * @param info Dsc of the node
     * @return Node
     */
    private DefaultMutableTreeNode findNode(DefaultMutableTreeNode root, ExternalObjectDescription info) {
        // LSR search a node to insert it in the tree
        DefaultMutableTreeNode node = null;
        if (root != null) {
            for (java.util.Enumeration e = root.breadthFirstEnumeration(); e.hasMoreElements();) {
                DefaultMutableTreeNode current = (DefaultMutableTreeNode) e.nextElement();
                if (info.equals((ExternalObjectDescription) (current.getUserObject()))) {
                    return current;
                }
            }
        }
        return null;
    }

    /**
     * Insert XML file
     * @param actual Actual tree
     * @param listado New file
     * @param raiz Root node
     */
    private void insertInnerListing(DefaultMutableTreeNode actual, java.net.URL listado, String raiz) {

        // SGL - Loading of an internal resource list method file in XML Format
        Document doc = new Document();
        try {
            SAXBuilder builder = new SAXBuilder();
            doc = builder.build(listado);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Method specification XML file not found");
            return;
        }

        List metodos = doc.getRootElement().getChildren();
        //System.out.println("el valor de nListAlgor "+nListAlgor+" y el numero de metodos "+metodos.size());
        for (int i = nListAlgor; i < nListAlgor + metodos.size(); i++) {
            listAlgor[i] = new AlgorithmXML((Element) metodos.get(i - nListAlgor));
        }
        if (objType != IMBALANCED) {
            Arrays.sort(listAlgor, nListAlgor, nListAlgor + metodos.size());
        }
        for (int i = nListAlgor; i < nListAlgor + metodos.size(); i++) {

            String nombre = ((AlgorithmXML) listAlgor[i]).name;
            String directorio = ((AlgorithmXML) listAlgor[i]).family;
            String nombrejar = ((AlgorithmXML) listAlgor[i]).jarFile;
            String type = ((AlgorithmXML) listAlgor[i]).problemType;

            String cad = "classification";
            switch (expType) {
                case CLASSIFICATION:
                    cad = "classification";
                    break;
                case REGRESSION:
                    cad = "regression";
                    break;
                case UNSUPERVISED:
                    cad = "unsupervised";
                    break;
            }

            DefaultMutableTreeNode sep = new DefaultMutableTreeNode();
            DefaultMutableTreeNode sepC = new DefaultMutableTreeNode();
            if (objType == LQD) {
                sep = findNode(
                        (DefaultMutableTreeNode) actual.getRoot(),
                        new ExternalObjectDescription("LQD", null, 0));
                if (sep == null) {
                    // Doesn't exist: insert
                    sep = new DefaultMutableTreeNode(new ExternalObjectDescription(
                            "LQD", null, 0));
                    actual.add(sep);
                }
                sepC = findNode(
                        (DefaultMutableTreeNode) actual.getRoot(),
                        new ExternalObjectDescription("CRISP", null, 0));
            /* if (sepC == null) {
            // Doesn't exist: insert
            sepC = new DefaultMutableTreeNode(new ExternalObjectDescription(
            "CRISP", null, 0));
            actual.add(sepC);

            }*/
            }
            if ((type.compareTo(cad) == 0) || (type.compareTo("unspecified") == 0) || ((expType != UNSUPERVISED) && (type.compareTo("supervised") == 0))) {
                // Check if directory exists
                DefaultMutableTreeNode dir = new DefaultMutableTreeNode();

                if (objType != LQD) {
                    String bold_directorio = "";
                    if (objType == IMBALANCED) {
                        if ((directorio.equals("Under-Sampling Methods")) || (directorio.equals("Over-Sampling Methods")) || (directorio.equals("Cost-Sensitive Classification")) || (directorio.equals("Ensembles for Class Imbalance"))) {
                            bold_directorio = "<html><b>" + directorio + "</b></html>";
                            dir = findNode(
                                    (DefaultMutableTreeNode) actual.getRoot(),
                                    new ExternalObjectDescription(bold_directorio, null, 0));
                        } else {
                            dir = findNode(
                                    (DefaultMutableTreeNode) actual.getRoot(),
                                    new ExternalObjectDescription(directorio, null, 0));
                        }
                    } else {
                        dir = findNode(
                                (DefaultMutableTreeNode) actual.getRoot(),
                                new ExternalObjectDescription(directorio, null, 0));
                    }
                    if (dir == null) {
                        // Doesn't exist: insert
                        if ((objType == IMBALANCED) && ((directorio.equals("Under-Sampling Methods")) || (directorio.equals("Over-Sampling Methods")) || (directorio.equals("Cost-Sensitive Classification")) || (directorio.equals("Ensembles for Class Imbalance")))) {
                            dir = new DefaultMutableTreeNode(new ExternalObjectDescription(
                                    bold_directorio, null, 0));
                            actual.add(dir);
                        } else {
                            dir = new DefaultMutableTreeNode(new ExternalObjectDescription(
                                    directorio, null, 0));
                            actual.add(dir);
                        }
                    }
                } else {
                    if (RamaLqd == 1) {
                        dir = findNode(
                                (DefaultMutableTreeNode) sep.getRoot(),
                                new ExternalObjectDescription(directorio, null, 0));
                        if (dir == null) {
                            // Doesn't exist: insert
                            dir = new DefaultMutableTreeNode(new ExternalObjectDescription(
                                    directorio, null, 0));
                            sep.add(dir);

                        }
                    } else if (RamaLqd == 0) {
                        dir = findNode(
                                (DefaultMutableTreeNode) sepC.getRoot(),
                                new ExternalObjectDescription(directorio, null, 0));
                        if (dir == null) {
                            // Doesn't exist: insert
                            dir = new DefaultMutableTreeNode(new ExternalObjectDescription(
                                    directorio, null, 0));
                            sepC.add(dir);

                        }
                    }
                }

                String mipath = listado.toString();
                String mitipo;
                mitipo = mipath.substring(mipath.lastIndexOf('/') + 1, mipath.length() - 4);

                /***************************************************************
                 *********************  EDUCATIONAL KEEL  **********************
                 **************************************************************/
                if (Frame.buttonPressed == 0) //Button Experiments Pressed
                {
                    switch (objType) {

                        case INVESTIGATION:

                            mitipo = mitipo.toLowerCase();
                            mipath = mipath.substring(0, mipath.lastIndexOf('/') + 1);
                            mipath = mipath + mitipo + "/";


                            break;

                        case SUBGROUPDISCOVERY:

                            if (mitipo.equals("SubgroupDiscovery") == true) {
                                mitipo = "methods";
                                mipath = mipath.substring(0, mipath.lastIndexOf('/') + 1);
                                mipath = mipath + mitipo + "/";
                            } else {
                                mitipo = mitipo.toLowerCase();
                                mipath = mipath.substring(0, mipath.lastIndexOf('/') + 1);
                                mipath = mipath + mitipo + "/";
                            }
                            break;

                        case MULTIINSTANCE:
                            mipath = mipath.substring(0, mipath.lastIndexOf('/') + 1);

                            if (mitipo.equals("MethodsMultiInstance") == true) {
                                mipath = mipath + "methods" + "/";
                            } else {
                                mitipo = mitipo.toLowerCase();
                                mipath = mipath.substring(0, mipath.lastIndexOf('/') + 1);
                                mipath = mipath + mitipo + "/";
                            }
                            break;

                        case IMBALANCED:

                            mipath = mipath.substring(0, mipath.lastIndexOf('/') + 1);
                            if (mitipo.equals("PreProcessImbalanced") == true) {
                                mipath = mipath + "preprocess" + "/";
                            } else if (mitipo.equals("MethodsImbalanced") == true) {
                                mipath = mipath + "methods" + "/";
                            } else if (mitipo.equals("TestsImbalanced") == true) {
                                mipath = mipath + "tests" + "/";
                            } else if (mitipo.equals("VisualizeImbalanced") == true) {
                                mipath = mipath + "visualize" + "/";
                            } else {
                                mitipo = mitipo.toLowerCase();
                                mipath = mipath + mitipo + "/";
                            }
                            break;

                        default:

                            mitipo = mitipo.toLowerCase();
                            mipath = mipath.substring(0, mipath.lastIndexOf('/') + 1);
                            mipath = mipath + mitipo + "/";

                            break;
                    }
                    ;





                } else //Button Teaching Pressed
                {
                    mipath = mipath.substring(0, mipath.lastIndexOf('/') + 1);
                    if (mitipo.equals("EducationalMethods") == true) {
                        mipath = mipath + "methods" + "/";
                    } else if (mitipo.equals("EducationalPreProcess") == true) {
                        mipath = mipath + "preprocess" + "/";
                    } else {
                        mitipo = mitipo.toLowerCase();
                        mipath = mipath + mitipo + "/";
                    }
                }
                /***************************************************************
                 *********************  EDUCATIONAL KEEL  **********************
                 **************************************************************/
                DefaultMutableTreeNode fich = new DefaultMutableTreeNode(
                        new ExternalObjectDescription(nombre, mipath, 0, nombrejar));
                dir.add(fich);
            } else {
                // Check if directory exists
                DefaultMutableTreeNode dir = new DefaultMutableTreeNode();
                if (objType != LQD) {
                    String bold_directorio = "";
                    if (objType == IMBALANCED) {
                        if ((directorio.equals("Under-Sampling Methods")) || (directorio.equals("Over-Sampling Methods")) || (directorio.equals("Cost-Sensitive Classification")) || (directorio.equals("Ensembles for Class Imbalance"))) {
                            bold_directorio = "<html><b>" + directorio + "</b></html>";
                            dir = findNode(
                                    (DefaultMutableTreeNode) actual.getRoot(),
                                    new ExternalObjectDescription(bold_directorio, null, 0));
                        } else {
                            dir = findNode(
                                    (DefaultMutableTreeNode) actual.getRoot(),
                                    new ExternalObjectDescription(directorio, null, 0));
                        }
                    } else {
                        dir = findNode(
                                (DefaultMutableTreeNode) actual.getRoot(),
                                new ExternalObjectDescription(directorio, null, 0));
                    }
                    if (dir == null) {
                        // Doesn't exist: insert
                        if ((objType == IMBALANCED) && ((directorio.equals("Rebalancing Methods")) || (directorio.equals("Cost-Sensitive Methods")))) {
                            dir = new DefaultMutableTreeNode(new ExternalObjectDescription(
                                    bold_directorio, null, 0));
                            actual.add(dir);
                        } else {
                            dir = new DefaultMutableTreeNode(new ExternalObjectDescription(
                                    directorio, null, 0));
                            actual.add(dir);
                        }
                    }
                } else {
                    if (RamaLqd == 1) {
                        dir = findNode(
                                (DefaultMutableTreeNode) sep.getRoot(),
                                new ExternalObjectDescription(directorio, null, 0));
                        if (dir == null) {
                            // Doesn't exist: insert
                            dir = new DefaultMutableTreeNode(new ExternalObjectDescription(
                                    directorio, null, 0));
                            sep.add(dir);

                        }
                    } else if (RamaLqd == 0) {
                        dir = findNode(
                                (DefaultMutableTreeNode) sepC.getRoot(),
                                new ExternalObjectDescription(directorio, null, 0));
                        if (dir == null) {
                            // Doesn't exist: insert
                            dir = new DefaultMutableTreeNode(new ExternalObjectDescription(
                                    directorio, null, 0));
                            sepC.add(dir);

                        }
                    }
                }

                String mipath = listado.toString();
                String mitipo;
                mitipo = mipath.substring(mipath.lastIndexOf('/') + 1, mipath.length() - 4);

                /***************************************************************
                 *********************  EDUCATIONAL KEEL  **********************
                 **************************************************************/
                if (Frame.buttonPressed == 0) //Button Experiments Pressed
                {

                    if (objType == IMBALANCED) {
                        mipath = mipath.substring(0, mipath.lastIndexOf('/') + 1);

                        if (mitipo.equals("PreProcessImbalanced") == true) {
                            mipath = mipath + "preprocess" + "/";
                        } else if (mitipo.equals("MethodsImbalanced") == true) {
                            mipath = mipath + "methods" + "/";
                        } else if (mitipo.equals("TestsImbalanced") == true) {
                            mipath = mipath + "tests" + "/";
                        } else if (mitipo.equals("VisualizeImbalanced") == true) {
                            mipath = mipath + "visualize" + "/";
                        } else {
                            mitipo = mitipo.toLowerCase();
                            mipath = mipath + mitipo + "/";
                        }
                    } else {
                        mitipo = mitipo.toLowerCase();
                        mipath = mipath.substring(0, mipath.lastIndexOf('/') + 1);
                        mipath = mipath + mitipo + "/";

                    }
                } else {
                    mipath = mipath.substring(0, mipath.lastIndexOf('/') + 1);
                    if (mitipo.equals("EducationalMethods") == true) {
                        mipath = mipath + "methods" + "/";
                    } else if (mitipo.equals("EducationalPreProcess") == true) {
                        mipath = mipath + "preprocess" + "/";
                    } else {
                        mitipo = mitipo.toLowerCase();
                        mipath = mipath + mitipo + "/";
                    }
                }
                /***************************************************************
                 *********************  EDUCATIONAL KEEL  **********************
                 **************************************************************/
                DefaultMutableTreeNode fich = new DefaultMutableTreeNode(
                        new ExternalObjectDescription("(" + nombre + ")", mipath, 0,
                        nombrejar));
                dir.add(fich);
            }
        }
        nListAlgor += metodos.size();
    }

    /**
     * Insert XML file of a test
     * @param actual Actual tree
     * @param listado New file
     * @param raiz Root node
     */
    private void insertaListadoInterno_test(DefaultMutableTreeNode actual, java.net.URL listado, String raiz) {
        // SGL - Loading of an internal resource list method file in XML Format
        Document doc = new Document();
        try {
            SAXBuilder builder = new SAXBuilder();
            doc = builder.build(listado);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Test/Visors specification XML file not found");
            return;
        }

        List metodos = doc.getRootElement().getChildren();
        for (int i = nListAlgor; i < nListAlgor + metodos.size(); i++) {
            listAlgor[i] = new AlgorithmXML((Element) metodos.get(i - nListAlgor));
            String nombre = ((AlgorithmXML) listAlgor[i]).name;
            String directorio = ((AlgorithmXML) listAlgor[i]).family;
            String nombrejar = ((AlgorithmXML) listAlgor[i]).jarFile;
            String type = ((AlgorithmXML) listAlgor[i]).problemType;
            String cad = "classification";
            switch (expType) {
                case CLASSIFICATION:
                    cad = "classification";
                    break;
                case REGRESSION:
                    cad = "regression";
                    break;
                case UNSUPERVISED:
                    cad = "unsupervised";
                    break;
            }



            if ((type.compareTo(cad) == 0) || (type.compareTo("unspecified") == 0) || ((expType != UNSUPERVISED) && (type.compareTo("supervised") == 0))) {
                // Check if directory exists
                DefaultMutableTreeNode dir = findNode(
                        (DefaultMutableTreeNode) actual.getRoot(),
                        new ExternalObjectDescription(directorio, null, 0));
                if (dir == null) {
                    // Doesn't exist: insert
                    dir = new DefaultMutableTreeNode(new ExternalObjectDescription(
                            directorio, null, 0));
                    actual.add(dir);
                }

                String mipath = listado.toString();
                mipath = mipath.substring(0, mipath.lastIndexOf('/') + 1);
                mipath += "tests/";
                DefaultMutableTreeNode fich = new DefaultMutableTreeNode(
                        new ExternalObjectDescription(nombre, mipath, 0, nombrejar));
                dir.add(fich);
            } else {
                // Check if directory exists
                DefaultMutableTreeNode dir = findNode(
                        (DefaultMutableTreeNode) actual.getRoot(),
                        new ExternalObjectDescription(directorio, null, 0));
                if (dir == null) {
                    // Doesn't exist: insert
                    dir = new DefaultMutableTreeNode(new ExternalObjectDescription(
                            directorio, null, 0));
                    actual.add(dir);
                }

                String mipath = listado.toString();
                mipath = mipath.substring(0, mipath.lastIndexOf('/') + 1);
                mipath += "tests/";
                DefaultMutableTreeNode fich = new DefaultMutableTreeNode(
                        new ExternalObjectDescription("(" + nombre + ")", mipath, 0,
                        nombrejar));
                dir.add(fich);
            }
        }
        nListAlgor += metodos.size();
    }

    /**
     * Reload the algorithms trees (all kinds)
     */
    public void reload_algorithms() {
        nListAlgor = 0;
        ((DefaultMutableTreeNode) preprocessTree.getModel().getRoot()).removeAllChildren();

        /***************************************************************
         ***************  EDUCATIONAL KEEL  ****************************
         **************************************************************/
        if (Frame.buttonPressed == 0) //Button Experiments pressed
        {

            if (objType == IMBALANCED) {
                createAlgorithmNodes((DefaultMutableTreeNode) preprocessTree.getModel().getRoot(), "." + File.separatorChar + "algorithm" + File.separatorChar + "PreProcessImbalanced.xml");
            } else {
                createAlgorithmNodes((DefaultMutableTreeNode) preprocessTree.getModel().getRoot(), "." + File.separatorChar + "algorithm" + File.separatorChar + "PreProcess.xml");
            }
        } else //Button Teaching pressed
        {

            createAlgorithmNodes((DefaultMutableTreeNode) preprocessTree.getModel().getRoot(), "." + File.separatorChar + "algorithm" + File.separatorChar + "EducationalPreProcess.xml");
        }
        /***************************************************************
         ***************  EDUCATIONAL KEEL  ****************************
         **************************************************************/
        Color bg = new Color(236, 233, 216);
        preprocessTree.setBackground(bg);
        preprocessTree.updateUI();



        ((DefaultMutableTreeNode) methodsSelectionTree.getModel().getRoot()).removeAllChildren();
        /***************************************************************
         ***************  EDUCATIONAL KEEL  ***************************
         **************************************************************/
        if (Frame.buttonPressed == 0) //Button Experiments Pressed
        {
            if (objType == IMBALANCED) {
                createAlgorithmNodes((DefaultMutableTreeNode) methodsSelectionTree.getModel().getRoot(), "." + File.separatorChar + "algorithm" + File.separatorChar + "MethodsImbalanced.xml");
            } else {
                if (objType == MULTIINSTANCE) {
                    createAlgorithmNodes((DefaultMutableTreeNode) methodsSelectionTree.getModel().getRoot(), "." + File.separatorChar + "algorithm" + File.separatorChar + "MethodsMultiInstance.xml");
                } else {

                    if (objType == SUBGROUPDISCOVERY) {
                        createAlgorithmNodes((DefaultMutableTreeNode) methodsSelectionTree.getModel().getRoot(), "." + File.separatorChar + "algorithm" + File.separatorChar + "SubgroupDiscovery.xml");
                    } else {
                        createAlgorithmNodes((DefaultMutableTreeNode) methodsSelectionTree.getModel().getRoot(), "." + File.separatorChar + "algorithm" + File.separatorChar + "Methods.xml");
                    }
                }
            }
        } else //Button Teaching Pressed
        {
            createAlgorithmNodes((DefaultMutableTreeNode) methodsSelectionTree.getModel().getRoot(), "." + File.separatorChar + "algorithm" + File.separatorChar + "EducationalMethods.xml");
        }
        /***************************************************************
         ***************  EDUCATIONAL KEEL  ***************************
         **************************************************************/
        methodsSelectionTree.setBackground(bg);
        methodsSelectionTree.updateUI();
        ((DefaultMutableTreeNode) postprocessSelectionTree.getModel().getRoot()).removeAllChildren();
        createAlgorithmNodes((DefaultMutableTreeNode) postprocessSelectionTree.getModel().getRoot(), "." + File.separatorChar + "algorithm" + File.separatorChar + "PostProcess.xml");
        postprocessSelectionTree.setBackground(bg);
        postprocessSelectionTree.updateUI();
        ((DefaultMutableTreeNode) testSelectionTree.getModel().getRoot()).removeAllChildren();
        if (objType == IMBALANCED) {
            createTestAlgorithmNodes((DefaultMutableTreeNode) testSelectionTree.getModel().getRoot(), "." + File.separatorChar + "algorithm" + File.separatorChar + "TestsImbalanced.xml");
        } else {
            createTestAlgorithmNodes((DefaultMutableTreeNode) testSelectionTree.getModel().getRoot(), "." + File.separatorChar + "algorithm" + File.separatorChar + "Tests.xml");
        }
        testSelectionTree.setBackground(bg);
        testSelectionTree.updateUI();
        ((DefaultMutableTreeNode) visualizeSelectionTree.getModel().getRoot()).removeAllChildren();
        if (objType == IMBALANCED) {
            createTestAlgorithmNodes((DefaultMutableTreeNode) visualizeSelectionTree.getModel().getRoot(), "." + File.separatorChar + "algorithm" + File.separatorChar + "VisualizeImbalanced.xml");
        } else {
            createTestAlgorithmNodes((DefaultMutableTreeNode) visualizeSelectionTree.getModel().getRoot(), "." + File.separatorChar + "algorithm" + File.separatorChar + "Visualize.xml");
        }
        visualizeSelectionTree.setBackground(bg);
        visualizeSelectionTree.updateUI();
    }

    /**
     * Checks graph
     * @return True if it is correct
     */
    boolean check() {
        // Last validation
        for (int i = 0; i < experimentGraph.numNodes(); i++) {
            // Check that each dataset have selected a training-test pair
            // minimum
            if (experimentGraph.getNodeAt(i).type == Node.type_Dataset) {

                for (int k = 0; k < ((DataSet) experimentGraph.getNodeAt(i)).tableVector.size(); k++) {
                    if (((Vector) ((DataSet) experimentGraph.getNodeAt(i)).tableVector.elementAt(k)).size() == 0) {
                        String mensaje = "Dataset " + experimentGraph.getNodeAt(i).dsc.getName(k) + " has no training and test files selected";
                        JOptionPane.showMessageDialog(this, mensaje, "Error", 2);
                        return false;
                    }
                }
            } // check user's method
            else if (experimentGraph.getNodeAt(i).type == Node.type_userMethod) {
                UserMethod mu = (UserMethod) experimentGraph.getNodeAt(i);
                File f = new File(mu.dsc.getPath() + mu.dsc.getName());
                if (mu.parametersUser == null || f.exists() == false) {
                    String mensaje = "User's Method " + "\"" + mu.dsc.getName() + "\"" + " incorrect";
                    JOptionPane.showMessageDialog(this, mensaje, "Error", 2);
                    return false;
                }
            } else if (experimentGraph.getNodeAt(i).type == Node.type_Test) {
                Test t = (Test) experimentGraph.getNodeAt(i);

                // count test's inputs
                int numEntradas = 0;
                for (int j = 0; j < experimentGraph.numArcs(); j++) {
                    if (experimentGraph.getArcAt(j).getDestination() == i) {
                        numEntradas++;
                    }
                }

                if (!t.chkNumEntradas(numEntradas)) {
                    String mensaje = "Number of inputs in test " + "\"" + t.dsc.getName() + "\"" + " incorrect";
                    JOptionPane.showMessageDialog(this, mensaje, "Error", 2);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Obtains the tipe of data files
     * @param type Type of partition
     * @param ficheros Files selected
     * @param dataset Data set selected
     * @param before Node related
     * @return Type of files
     */
    Vector<String> type_file(String type, String[] ficheros, String dataset, Node before) {
        boolean found;
        Vector<String> files = new Vector<String>();
        int contador = 0;
        int initial = 1;
        if (type.compareTo("10") == 0) {
            contador = 10;
        } else if (type.compareTo("100") == 0 && before.type_lqd == Node.C_LQD) {
            int position = 0;
            DatasetXML[] filesc_lqd = listDataC_LQD;
            for (int f = 0; f < filesc_lqd.length; f++) {
                if (filesc_lqd[f].nameAbr.equalsIgnoreCase(dataset)) {
                    position = f;
                    break;
                }
            }
            contador = filesc_lqd[position].files - 1;
            initial = 0;
        } else if (type.compareTo("100") == 0) {
            contador = 99;
            initial = 0;
        } else if (type.compareTo("O") == 0) {

            contador = 0;
            for (int j = 0; j < ficheros.length; j++) {
                if (ficheros[j].compareTo(dataset + ".dat") == 0) {
                    files.addElement(ficheros[j]);
                    break;
                }
            }

        }
        for (int l = initial; l <= contador; l++) {

            found = false;
            for (int j = 0; j < ficheros.length && !found; j++) {
                if (ficheros[j].indexOf(type + "-" + l + "tra.dat") != -1) {
                    files.addElement(ficheros[j]);
                    found = true;
                }
            }
            if (!found) { //One file is missing
                JOptionPane.showMessageDialog(this, "The dataset " + dataset + " is missing one file (" + dataset + type + "-" + l + "-tra.dat",
                        "Dataset invalid", JOptionPane.ERROR_MESSAGE);
                files.clear();
                files.addElement("-1");
                return files;
            }


            found = false;
            for (int j = 0; j < ficheros.length && !found; j++) {
                if (ficheros[j].indexOf(type + "-" + l + "tst.dat") != -1) {
                    files.addElement(ficheros[j]);
                    found = true;
                }
            }

            if (!found) { //One file is missing
                JOptionPane.showMessageDialog(this, "The dataset " + dataset + " is missing one file (" + type + "-" + l + "-tst.dat",
                        "Dataset invalid", JOptionPane.ERROR_MESSAGE);
                files.clear();
                files.addElement("-1");
                return files;
            }

        }
        return files;
    }

    /**
     * Obtains the data files
     * @param type Type of partition
     * @param dataset Data set selected
     * @param before Node related
     * @return Files
     */
    Vector<String> obtain_files(String type, String dataset, Node before) {
        File dir;
        String[] ficheros;
        Vector<String> files = new Vector<String>();

        for (int i = 0; i < before.dsc.getNamesLength(); i++) {
            if (before.dsc.getName(i).compareTo(dataset) == 0) {
                if (dataset.contains("C_LQD") == true) {
                    dir = new File("." + before.dsc.getPath(i) + "C_LQD/" + before.dsc.getName(i));
                } else {
                    dir = new File("." + before.dsc.getPath(i) + before.dsc.getName(i));
                }

                ficheros = dir.list();
                if (type.compareTo("10cv") == 0) {
                    files = type_file("10", ficheros, dataset, before);
                } else if (type.compareTo("100boost") == 0) {
                    files = type_file("100", ficheros, dataset, before);
                } else if (type.compareTo("O-100boost") == 0 || type.compareTo("O-10cv") == 0) {
                    files = type_file("O", ficheros, dataset, before);
                }

                break;
            }
        }

        System.out.println("Files are " + files);

        return files;

    }

    /**
     * Checks graph
     * @return True if it is correct
     */
    boolean checkLQD() {


        // Last validation
        for (int i = 0; i < experimentGraph.numNodes(); i++) {
            // Check that each dataset have selected a training-test pair
            // minimum         
            //if (experimentGraph.getNodeAt(i).type == Node.type_Dataset)          
            //{
            for (int j = 0; j < experimentGraph.numNodes(); j++) {
                if (experimentGraph.getNodeAt(j).type == Node.type_Algorithm) {
                    for (int a = 0; a < experimentGraph.getNodeAt(j).dsc.arg.size(); a++) {
                        if (experimentGraph.getNodeAt(j).dsc.arg.get(a).before.id == experimentGraph.getNodeAt(i).id) {
                            experimentGraph.getNodeAt(j).dsc.arg.get(a).tableVector.clear();
                            experimentGraph.getNodeAt(j).dsc.arg.get(a).times.clear();
                            for (int data = 0; data < experimentGraph.getNodeAt(j).dsc.arg.get(a).data_selected.size(); data++) {
                                if (experimentGraph.getNodeAt(j).dsc.arg.get(a).parameters.get(data).parameter_data.get(experimentGraph.getNodeAt(j).dsc.arg.get(a).parameters.get(data).parameter_data.size() - 1).get(0).compareTo("10cv") == 0) {
                                    Vector<String> files = obtain_files("10cv", experimentGraph.getNodeAt(j).dsc.arg.get(a).data_selected.get(data), experimentGraph.getNodeAt(j).dsc.arg.get(a).before);
                                    experimentGraph.getNodeAt(j).dsc.arg.get(a).tableVector.addElement(files);
                                    experimentGraph.getNodeAt(j).dsc.arg.get(a).times.addElement(10);
                                } else if (experimentGraph.getNodeAt(j).dsc.arg.get(a).parameters.get(data).parameter_data.get(experimentGraph.getNodeAt(j).dsc.arg.get(a).parameters.get(data).parameter_data.size() - 1).get(0).compareTo("100boost") == 0) {
                                    Vector<String> files = obtain_files("100boost", experimentGraph.getNodeAt(j).dsc.arg.get(a).data_selected.get(data), experimentGraph.getNodeAt(j).dsc.arg.get(a).before);
                                    experimentGraph.getNodeAt(j).dsc.arg.get(a).tableVector.addElement(files);
                                    experimentGraph.getNodeAt(j).dsc.arg.get(a).times.addElement(2);
                                } else if (experimentGraph.getNodeAt(j).dsc.arg.get(a).parameters.get(data).parameter_data.get(experimentGraph.getNodeAt(j).dsc.arg.get(a).parameters.get(data).parameter_data.size() - 1).get(0).compareTo("O-100boost") == 0) {
                                    Vector<String> files = obtain_files("O-100boost", experimentGraph.getNodeAt(j).dsc.arg.get(a).data_selected.get(data), experimentGraph.getNodeAt(j).dsc.arg.get(a).before);
                                    experimentGraph.getNodeAt(j).dsc.arg.get(a).tableVector.addElement(files);
                                    experimentGraph.getNodeAt(j).dsc.arg.get(a).times.addElement(1);
                                } else if (experimentGraph.getNodeAt(j).dsc.arg.get(a).parameters.get(data).parameter_data.get(experimentGraph.getNodeAt(j).dsc.arg.get(a).parameters.get(data).parameter_data.size() - 1).get(0).compareTo("O-10cv") == 0) {
                                    Vector<String> files = obtain_files("0-10cv", experimentGraph.getNodeAt(j).dsc.arg.get(a).data_selected.get(data), experimentGraph.getNodeAt(j).dsc.arg.get(a).before);
                                    experimentGraph.getNodeAt(j).dsc.arg.get(a).tableVector.addElement(files);
                                    experimentGraph.getNodeAt(j).dsc.arg.get(a).times.addElement(1);
                                }
                            }
                        }
                    }
                }
            }
        //}

        // check user's method
           /* else if (experimentGraph.getNodeAt(i).type == Node.type_userMethod) {
        UserMethod mu = (UserMethod) experimentGraph.getNodeAt(i);
        File f = new File(mu.dsc.getPath() + mu.dsc.getName());
        if (mu.parametersUser == null || f.exists() == false) {
        String mensaje = "User's Method " + "\"" + mu.dsc.getName() + "\"" + " incorrect";
        JOptionPane.showMessageDialog(this, mensaje, "Error", 2);
        return false;
        }
        }*/
        /* else if (experimentGraph.getNodeAt(i).type == Node.type_Test) {
        Test t = (Test) experimentGraph.getNodeAt(i);

        // count test's inputs
        int numEntradas = 0;
        for (int j = 0; j < experimentGraph.numArcs(); j++) {
        if (experimentGraph.getArcAt(j).getDestination() == i) {
        numEntradas++;
        }
        }

        if (!t.chkNumEntradas(numEntradas)) {
        String mensaje = "Number of inputs in test " + "\"" + t.dsc.getName() + "\"" + " incorrect";
        JOptionPane.showMessageDialog(this, mensaje, "Error", 2);
        return false;
        }
        }*/
        }
        return true;
    }

    /**
     * Test if the flow is correct
     * @return True
     */
    boolean isFlowCorrect() {
        boolean bResState;
        bResState = true;
        //Creo State i progresso per totes les etapes fent & (tela marinera!!)
        //Comprobo que algun state és true i guardo a bResState
        return bResState;
    }

    /**
     * checks if are nodes not connected to a dataset
     * @return true if there exist nodes which are not connected to a data set
     */
    public Vector isolatedNodes() {
        // check if are nodes not connected to a dataset
        boolean v[] = new boolean[experimentGraph.numNodes()];
        Arrays.fill(v, false);
        Vector visitados = new Vector();
        int i = -1;
        while ((i = nextNode(visitados)) != -1) {
            if (experimentGraph.getNodeAt(i).type == Node.type_Dataset) {
                boolean para = false;
                for (int j = 0; j < experimentGraph.numArcs() && !para; j++) {
                    if (experimentGraph.getArcAt(j).getSource() == i) {
                        v[i] = true;
                        para = true;
                    }
                }
            } else {
                boolean para = false;
                for (int j = 0; j < experimentGraph.numArcs() && !para; j++) {
                    if (experimentGraph.getArcAt(j).getDestination() == i) {
                        if (v[experimentGraph.getArcAt(j).getSource()]) {
                            v[i] = true;
                            para = true;
                        }
                    }
                }
            }
        }

        // store these nodes
        Vector sueltos = new Vector();
        for (i = 0; i < experimentGraph.numNodes(); i++) {
            if (v[i] == false) {
                sueltos.addElement(new Integer(i));
            }
        }

        return sueltos;
    }

    /**
     * checks if are nodes LQD not connected to a dataset
     * @return true if there exist nodes which are not connected to a data set
     */
    public Vector isolatedNodesLQD() {
        // check if are nodes not connected to a dataset
        boolean v[] = new boolean[experimentGraph.numNodes()];
        Arrays.fill(v, false);
        Vector visitados = new Vector();
        int i = -1;
        while ((i = nextNodeLQD(visitados)) != -1) {
            if (experimentGraph.getNodeAt(i).type == Node.type_Dataset) {
                boolean para = false;
                for (int j = 0; j < experimentGraph.numArcs() && !para; j++) {
                    if (experimentGraph.getArcAt(j).getSource() == experimentGraph.getNodeAt(i).id) {
                        v[i] = true;
                        para = true;
                    }
                }
            } else {
                boolean para = false;
                for (int j = 0; j < experimentGraph.numArcs() && !para; j++) {
                    if (experimentGraph.getArcAt(j).getDestination() == experimentGraph.getNodeAt(i).id) {
                        int position = 0;
                        for (int n = 0; n < experimentGraph.numNodes(); n++) {
                            if (experimentGraph.getArcAt(j).getSource() == experimentGraph.getNodeAt(n).id) {
                                position = n;
                                break;
                            }

                        }

                        if (v[position]) {
                            v[i] = true;
                            para = true;
                        }
                    }
                }
            }
        }

        // store these nodes

        Vector sueltos = new Vector();
        for (i = 0; i < experimentGraph.numNodes(); i++) {

            if (v[i] == false) {
                if (experimentGraph.getNodeAt(i).type != Node.type_Visor && experimentGraph.getNodeAt(i).type != Node.type_Test) {
                    sueltos.addElement(new Integer(i));
                } else {
                    summary = true;
                }
            }

        }


        return sueltos;
    }

    /**
     * creates directories for dataset and copy training-test files selected
     * @param ds the data set node with the selected data sets
     * @param path the destination path 
     */
    public void createDatasetDirs(DataSet ds, String path) {
        // creates directories for dataset and copy training-test files selected

        if (ds.modified == false) {
            String path_destino = path + "datasets/" + ds.dsc.getName();
            FileUtils.mkdir(path_destino);

            for (int i = 0; i < ((Vector) (ds.tableVector.elementAt(Layer.layerActivo))).size(); i++) {
                String fichero = ds.getTrainingAt(i);
                String origen = ds.dsc.getPath() + ds.dsc.getName() + "/" + fichero;
                String destino = path_destino + "/" + fichero;
                FileUtils.copy("." + origen, destino);

                fichero = ds.getTestAt(i);
                origen = ds.dsc.getPath() + ds.dsc.getName() + "/" + fichero;
                destino = path_destino + "/" + fichero;

                FileUtils.copy("." + origen, destino);
            }
        }
    }

    /**
     * creates directories for dataset and copy training-test files selected
     * @param ds the data set node with the selected data sets
     * @param path the destination path
     */
    public void createDatasetDirsLQD(Joint ds, String path) {
        // creates directories for dataset and copy training-test files selected

        for (int data = 0; data < ds.data_selected.size(); data++) {
            String path_destino = ""; //path + "datasets/";
            if (ds.type_lqd.compareTo("LQD") == 0) {
                FileUtils.mkdir(path.concat("datasets/LQD"));
                path_destino = path + "datasets/LQD/" + ds.data_selected.get(data) + "-" + ds.parameters.get(data).parameter_data.get(ds.parameters.get(data).parameter_data.size() - 1).get(0);
            } /* else if(ds.before.type_lqd==Node.C_LQD)
            {
            FileUtils.mkdir(path.concat("datasets/C_LQD"));
            path_destino =path + "datasets/C_LQD/"+ ds.data_selected.get(data)+"-"+ds.parameters.get(data).parameter_data.get(ds.parameters.get(data).parameter_data.size()-1).get(0);
            }
            else if(ds.before.type_lqd==Node.LQD_C)
            {
            FileUtils.mkdir(path.concat("datasets/LQD_C"));
            path_destino =path + "datasets/LQD_C/"+ ds.data_selected.get(data)+"-"+ds.parameters.get(data).parameter_data.get(ds.parameters.get(data).parameter_data.size()-1).get(0);
            }*/ else if (ds.type_lqd.compareTo("CRISP") == 0) {
                FileUtils.mkdir(path.concat("datasets/CRISP"));
                path_destino = path + "datasets/CRISP/" + ds.data_selected.get(data) + "-" + ds.parameters.get(data).parameter_data.get(ds.parameters.get(data).parameter_data.size() - 1).get(0);

            }

            File f = new File(path_destino);
            if (!f.exists()) {
                FileUtils.mkdir(path_destino);
                for (int i = 0; i < ds.tableVector.get(data).size(); i++) {
                    String fichero = ds.tableVector.get(data).get(i);
                    String origen = "";
                    for (int or = 0; or < ds.before.dsc.getNamesLength(); or++) {
                        if (ds.before.dsc.getName(or).compareTo(ds.data_selected.get(data)) == 0) {
                            if (ds.before.dsc.getName(or).contains("C_LQD") == true) {
                                origen = ds.before.dsc.getPath(or) + "C_LQD/" + ds.before.dsc.getName(or) + "/" + fichero;
                            } else {
                                origen = ds.before.dsc.getPath(or) + ds.before.dsc.getName(or) + "/" + fichero;
                            }


                            break;
                        }
                    }
                    String destino = path_destino + "/" + fichero;
                    FileUtils.copy("." + origen, destino);
                }
            }

        }

    }

    /**
     * Generates algorithm directories
     * @param al Algorithm
     * @param origen Origin
     * @param path_scripts Path for scripts
     * @param path_results Path for results
     * @param numNode Id of node
     * @param problema Name of data set
     */
    private void dirAlgorithmAlgorithm(Algorithm al, int origen, String path_scripts, String path_results, int numNode, String problema) {

        Algorithm algo = (Algorithm) experimentGraph.getNodeAt(origen);

        // Scripts

        Vector conj = new Vector();
        Vector tra = new Vector();
        Vector tst = new Vector();

        if (algo.dsc.getSubtype() == Node.type_Preprocess) {
            tra = (Vector) algo.getActivePair().getTrainingOutputFiles().clone();
            tst = (Vector) algo.getActivePair().getTestOutputFiles().clone();
        } else {
            tra = (Vector) algo.getActivePair().getTrainingValidationFiles().clone();
            tst = (Vector) algo.getActivePair().getTestFiles().clone();
        }
        conj.add(tra);
        Vector tra2 = new Vector();
        tra2 = (Vector) algo.getActivePair().getTrainingValidationFiles().clone();
        conj.add(tra2);
        conj.add(tst);
        Vector tst2 = new Vector();
        tst2 = (Vector) algo.getActivePair().getTestFiles().clone();
        conj.add(tst2);
        Vector salidas = new Vector();
        salidas = (Vector) algo.getActivePair().getAdditionalOutputFiles().clone();
        if (salidas.size() != 0) {
            conj.add(salidas);

        }
        /***************************************************************
         ***************  EDUCATIONAL KEEL   ***************************
         **************************************************************/
        //When "grabarScripts" is invoqued the config.txt files are created
        //In parameters, there are information about result and dataset
        //paths and the name of the problem and method. Ej: ALLKNN
        /***************************************************************
         ***************  EDUCATIONAL KEEL   ***************************
         **************************************************************/
        if (al.dsc.getSubtype() == Node.type_Preprocess) {
            al.getActivePair().writeScripts(path_scripts, "config", fullName[numNode], problema, conj, "result", true, cvType, numberKFoldCross, expType);
        } else {
            al.getActivePair().writeScripts(path_scripts, "config", fullName[numNode], problema, conj, "result", false, cvType, numberKFoldCross, expType);
        }
    }

    /**
     *
     * @param al Algorithm
     * @param destin
     * @param path_scripts
     * @param path_summary
     * @param path_summary_crisp
     * @param numNode
     * @param position
     * @param type_lqd
     * @param type_algorithm
     * @param sentencias
     * @param problema
     * @param crisp_version
     * @param r_jar
     */
    private void dirAlgorithmDatasetLQD(Algorithm al, Joint destin, String path_scripts, String path_summary, String path_summary_crisp, int numNode, int position,
            String type_lqd, int type_algorithm, Vector sentencias, String problema, boolean crisp_version, String r_jar) {

        if (al.dsc.getSubtype() == Node.type_Preprocess) {
            writeConfig(al, path_scripts, path_summary, path_summary_crisp, "config", (al.id + "." + al.dsc.getName()), true, destin, position, type_lqd, type_algorithm, sentencias, problema, crisp_version, r_jar);
        } else //deja en results
        {
            writeConfig(al, path_scripts, path_summary, path_summary_crisp, "config", (al.id + "." + al.dsc.getName()), false, destin, position, type_lqd, type_algorithm, sentencias, problema, crisp_version, r_jar);
        }
    }

    /**
     * Write a configuration script for the method, employing its parameters
     * @param al
     * @param path
     * @param path_summary
     * @param path_summary_crisp
     * @param baseName
     * @param methodName
     * @param pre
     * @param destin
     * @param position
     * @param type_lqd
     * @param type_algorithm
     * @param sentencias
     * @param problema
     * @param crisp_version
     * @param r_jar
     */
    public void writeConfig(Algorithm al, String path, String path_summary, String path_summary_crisp, String baseName, String methodName,
            boolean pre, Joint destin, int position, String type_lqd, int type_algorithm,
            Vector sentencias, String problema, boolean crisp_version, String r_jar) {

        String fichero, nombre, aux, result = "", results_crisp = "";
        int cont = 0;



        // Check that script doesn't exist ()
        String cadRutaParcial = "";
        if (type_algorithm == 0) {
            cadRutaParcial = "../datasets/" + type_lqd + "/" + destin.data_selected.get(position) +
                    "-" + destin.parameters.get(position).parameter_data.get(destin.parameters.get(position).parameter_data.size() - 1).get(0) + "/";
        } else {
            // cadRutaParcial = "../datasets/"+type_lqd+"/"+fullName[destin.before.id]+
            //   "."+destin.data_selected.get(position)+"-"+destin.parameters.get(position).parameter_data.get(destin.parameters.get(position).parameter_data.size()-1).get(0)+"/";
            int pos = problema.indexOf('-');
            String direction = problema.substring(0, pos) + "/" + problema.substring(pos + 1);
            cadRutaParcial = "../datasets/" + type_lqd + "/" + direction + "/";
        }

        //System.out.println(" la ruta es "+cadRutaParcial+" y estamos con el nodo "+methodName);
        //destin.information();

        for (int i = 0; i < destin.times.get(position); i++) {
            nombre = (new File(path)) + File.separator + baseName + i + ".txt";

            if (summary == true) {
                result = "";
                result = "algorithm = " + al.dsc.getName() + "\n";
                result += "inputData = ";

                results_crisp = "";
                results_crisp = "algorithm = Crisp" + al.dsc.getName() + "\n";
                results_crisp += "inputData = ";

            }
            fichero = "";
            fichero = "algorithm = " + al.dsc.getName() + "\n";
            fichero += "inputData = ";


            if (destin.times.get(position) == 10 && destin.before.type == Node.type_Dataset) {
                fichero += "\"" + cadRutaParcial + destin.tableVector.get(position).get(cont) + "\" ";
                cont++;
                fichero += "\"" + cadRutaParcial + destin.tableVector.get(position).get(cont) + "\" ";
                cont--;
            } else if (destin.times.get(position) == 10) {
                fichero += "\"" + cadRutaParcial + destin.data_selected.get(position) + "-10-" + (i + 10) + "tra.dat" + "\" ";
                fichero += "\"" + cadRutaParcial + destin.data_selected.get(position) + "-10-" + (i + 10) + "tst.dat" + "\" ";
            } else if (destin.times.get(position) == 2) {
                fichero += "\"" + cadRutaParcial + destin.data_selected.get(position) + "-100-" + "\" ";
            } else {
                fichero += "\"" + cadRutaParcial + destin.data_selected.get(position) + "\" ";
            }


            fichero += "\n";
            boolean is_pre = true;
            if (pre) {
                if (destin.times.get(position) == 10 && destin.before.type == Node.type_Dataset) {

                    aux = "../datasets/" + type_lqd + "/" + methodName + "/" + problema + "/" + destin.tableVector.get(position).get(cont) + "\" ";
                    cont++;
                    aux += "\"" + "../datasets/" + type_lqd + "/" + methodName + "/" + problema + "/" + destin.tableVector.get(position).get(cont);
                    cont--;


                } else if (destin.times.get(position) == 10) {
                    aux = "../datasets/" + type_lqd + "/" + methodName + "/" + problema + "/" + destin.data_selected.get(position) + "-10-" + (i + 10) + "tra.dat" + "\" ";
                    aux += "\"" + "../datasets/" + type_lqd + "/" + methodName + "/" + problema + "/" + destin.data_selected.get(position) + "-10-" + (i + 10) + "tst.dat";
                } else {
                    aux = "../datasets/" + type_lqd + "/" + methodName + "/" + problema + "/" + destin.data_selected.get(position) + "-100-";
                }
                fichero += "outputData = \"" + aux + "\" ";

            } else //result
            {
                is_pre = false;
                if (destin.times.get(position) == 10 && destin.before.type == Node.type_Dataset) {
                    aux = "../results/" + type_lqd + "/" + methodName + "/" + problema + "/R" + destin.tableVector.get(position).get(cont) + "\" ";
                    if (summary == true) {

                        String aux_re = "../results/" + type_lqd + "/" + methodName + "/" + problema + "/SUMMARY/";
                        int last = destin.tableVector.get(position).get(cont).lastIndexOf('-');
                        result += "../results/" + type_lqd + "/" + methodName + "/" + problema + "/R" + destin.tableVector.get(position).get(cont).substring(0, last) + "\" ";
                        result += "\n" + "outputData = \"" + aux_re + "\" ";

                    }
                //cont++;
                //aux += "\""+"../results/" +type_lqd+"/"+ methodName + "/" +problema + "/R"+destin.tableVector.get(position).get(cont);
                //cont--;
                } else if (destin.times.get(position) == 10) {
                    aux = "../results/" + type_lqd + "/" + methodName + "/" + problema + "/R" + destin.data_selected.get(position) + "-10-" + (i + 10) + "tra.dat" + "\" ";
                    if (summary == true && (i == destin.times.get(position) - 1)) {
                        String aux_re = "../results/" + type_lqd + "/" + methodName + "/" + problema + "/SUMMARY/";
                        result += "../results/" + type_lqd + "/" + methodName + "/" + problema + "/R" + destin.data_selected.get(position) + "-10" + "\" ";
                        result += "\n" + "outputData = \"" + aux_re + "\" ";
                    }
                //cont++;
                //aux += "\""+"../results/" +type_lqd+"/"+ methodName + "/" +problema + "/R"+destin.data_selected.get(position)+"-10-"+(i+10)+"tst.dat";
                //cont--;
                } else {
                    aux = "../results/" + type_lqd + "/" + methodName + "/" + problema + "/";
                    if (summary == true) {
                        String aux_re = "../results/" + type_lqd + "/" + methodName + "/" + problema + "/SUMMARY/";
                        String aux_re_crisp = "../results/" + type_lqd + "/" + methodName + "/" + problema + "_Crisp/SUMMARY/";

                        results_crisp = results_crisp + "../results/" + type_lqd + "/" + methodName + "/" + problema + "_Crisp/";
                        result += "../results/" + type_lqd + "/" + methodName + "/" + problema + "/";
                        result += "\n" + "outputData = \"" + aux_re + "\" ";
                        results_crisp += "\n" + "outputData = \"" + aux_re_crisp + "\" ";
                    }

                }

                fichero += "outputData = \"" + aux + "\" ";
            }//end else results

            String Icrisp_parameters = "\n\n";
            fichero += "\n\n";
            if (summary == true) {
                result += "\n\n";
                results_crisp += "\n\n";
            //result +="xls = "+path_excel +"\n";
            //results_crisp += "xls = "+path_excel+"\n";
            }


            Parameters parameterData = (Parameters) (al.par.elementAt(0));

            //Now the parameters

            for (int p = 0; p < 4; p++) {
                for (int c = 0; c < destin.parameters.get(position).get(p).size(); c++) {
                    if (p == 0) {
                        fichero += "Instances = ";
                        Icrisp_parameters += "Instances = ";

                    } else if (p == 1) {
                        fichero += "Nclases = ";
                        Icrisp_parameters += "Nclases = ";
                    } else if (p == 2) {
                        fichero += "attributes = ";
                        Icrisp_parameters += "attributes = ";
                    } else if (p == 3) {
                        fichero += "Class = ";
                        Icrisp_parameters += "Class = ";
                    }


                    /*else if (p==destin.parameters.get(position).size()-1)
                    fichero +="Partitions_Data = ";*/

                    fichero += destin.parameters.get(position).get(p).get(c);
                    Icrisp_parameters += destin.parameters.get(position).get(p).get(c) + "\n";
                    fichero += "\n";

                }
            }
            fichero += "Partitions_Data = ";
            Icrisp_parameters += "Partitions_Data = ";
            fichero += destin.parameters.get(position).get(destin.parameters.get(position).size() - 1).get(0);
            Icrisp_parameters += destin.parameters.get(position).get(destin.parameters.get(position).size() - 1).get(0) + "\n";
            fichero += "\n";


            for (int pa = 0 + 4; pa < parameterData.getNumParameters() + 4; pa++) {
                for (int c = 0; c < destin.parameters.get(position).get(pa).size(); c++) {
                    fichero += parameterData.descriptions.get(pa - 4) + " = ";
                    Icrisp_parameters += parameterData.descriptions.get(pa - 4) + " = ";
                    fichero += destin.parameters.get(position).get(pa).get(c);
                    Icrisp_parameters += destin.parameters.get(position).get(pa).get(c) + "\n";
                    fichero += "\n";
                    if (summary == true && destin.times.get(position) != 10 &&
                            (parameterData.descriptions.get(pa - 4).toString().compareTo("Type_risk") == 0 ||
                            parameterData.descriptions.get(pa - 4).toString().compareTo("Minimum_risk") == 0) || parameterData.descriptions.get(pa - 4).toString().compareTo("Type_rule") == 0 || parameterData.descriptions.get(pa - 4).toString().compareTo("Partitions") == 0) {
                        result += parameterData.descriptions.get(pa - 4).toString() + " = ";
                        result += destin.parameters.get(position).get(pa).get(c);
                        result += "\n";

                        results_crisp += parameterData.descriptions.get(pa - 4).toString() + " = ";
                        results_crisp += destin.parameters.get(position).get(pa).get(c);
                        results_crisp += "\n";
                    } else if (summary == true && pa == parameterData.getNumParameters() + 3 && destin.times.get(position) != 10) {
                        result += "Files = ";
                        result += destin.parameters.get(position).get(pa).get(c);
                        result += "\n";

                        results_crisp += "Files = ";
                        results_crisp += destin.parameters.get(position).get(pa).get(c);
                        results_crisp += "\n";
                    }
                }

            }

            if (destin.times.get(position) == 10) {
                result += "Files = 10";
                result += "\n";
            }




            Files.writeFile(nombre, fichero);
            cont = cont + 2;
            if (summary == true && is_pre == false) {
                String nombre_summary = nombre = (new File(path_summary)) + File.separator + "summary.txt";
                Files.writeFile(nombre_summary, result);
            }

            //Insert the node results


            boolean emitir = false;

            try {
                if (al.dsc.getPath().substring(0, 4).equals("jar:") || al.dsc.getPath().substring(0, 5).equals("file:")) {
                    //System.out.println("Debug ERR 1915 " + al.dsc.getPath() + al.dsc.getJarName());
                    java.net.URL miurl = new java.net.URL(al.dsc.getPath() + al.dsc.getJarName());
                    if (miurl.openStream() != null) {
                        emitir = true;
                    }
                //System.out.println("resource emits=" + emitir);
                } else {
                    File f = new File(al.dsc.getPath() + al.dsc.getJarName());
                    if (f.isFile()) {
                        emitir = true;
                    }
                //System.out.println("file emits=" + emitir);
                }



                if (emitir) {
                    String linea = "";
                    linea = new String("java -Xmx" + this.heapSize + "000000 " + " -jar" + " ../exe/" + al.dsc.getJarName() + " ./" + methodName + "/" + problema + "/" + baseName + i + ".txt");
                    sentencias.addElement(linea);

                    //File with 0-100boost must create new file of datasets (100 boost and the 1000 test)
                    if (destin.times.get(position) == 1 && destin.parameters.get(position).parameter_data.get(destin.parameters.get(position).parameter_data.size() - 1).get(0).compareTo("O-100boost") == 0)//crear el boostrap y los 1000 test
                    {

                        String nombre1 = (new File(path)) + File.separator + baseName + "_boost" + i + ".txt";
                        String fichero1 = "inputData = " + "\"" + "../datasets/" + type_lqd + "/" + methodName + "/" + problema + "/" + destin.data_selected.get(position) + "-100-\"";
                        //fichero1=fichero1+"../datasets/"+type_lqd+"/" + methodName + "/" + problema+"/"+destin.data_selected.get(position)+"\n";
                        Files.writeFile(nombre1, fichero1);
                        linea = new String("java -Xmx" + this.heapSize + "000000 " + " -jar" + " ../exe/" + "100boost.jar" + " ./" + methodName + "/" + problema + "/" + baseName + "_boost" + i + ".txt");
                        sentencias.addElement(linea);

                        nombre1 = (new File(path)) + File.separator + baseName + "_test" + i + ".txt";
                        fichero1 = "inputData = " + "\"" + "../datasets/" + type_lqd + "/" + methodName + "/" + problema + "/" + destin.data_selected.get(position) + "-100-\"";
                        //fichero1=fichero1+"../datasets/"+type_lqd+"/" + methodName + "/" + problema+"/"+destin.data_selected.get(position)+"\n";
                        Files.writeFile(nombre1, fichero1);
                        linea = new String("java -Xmx" + this.heapSize + "000000 " + " -jar" + " ../exe/" + "results.jar" + " ./" + methodName + "/" + problema + "/" + baseName + "_test" + i + ".txt");
                        sentencias.addElement(linea);


                    }

                    //These files contains a crisp approach with the same estruct that the LQD. (same main, only
                    //we need the word "Crisp" in the name of the file of results
                    if (crisp_version == true) {
                        String nombre1 = (new File(path)) + File.separator + baseName + "_Crisp" + i + ".txt";
                        String fichero1 = "algorithm = " + al.dsc.getName() + "\n";
                        fichero1 += "inputData = " + "\"" + cadRutaParcial + destin.data_selected.get(position) + "-100-" + "\" ";
                        fichero1 += "\n";
                        aux = "../results/" + type_lqd + "/" + methodName + "/" + problema + "_Crisp" + "/Crisp";
                        fichero1 += "outputData = \"" + aux + "\" ";

                        fichero1 += Icrisp_parameters;

                        Files.writeFile(nombre1, fichero1);
                        linea = new String("java -Xmx" + this.heapSize + "000000 " + " -jar" + " ../exe/" + al.dsc.getJarName() + " ./" + methodName + "/" + problema + "/" + baseName + "_Crisp" + i + ".txt");
                        sentencias.addElement(linea);
                        if (summary == true && is_pre == false) {
                            String nombre_summary_crisp = nombre = (new File(path_summary_crisp)) + File.separator + "summary.txt";
                            Files.writeFile(nombre_summary_crisp, results_crisp);

                            linea = new String("java -Xmx" + this.heapSize + "000000 " + " -jar" + " ../exe/" + r_jar + " ./SUMMARY/" + methodName + "/Crisp_" + problema + "/summary.txt");
                            sentencias.addElement(linea);
                        }

                    }


                //System.out.println("add line " + linea);
                //if (sentencias.contains(linea) == false) {
                //sentencias.addElement(linea);
                //}
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (destin.times.get(position) == 2) {
                i = 2;
            }

            if (pre == false && (i == destin.times.get(position) - 1) || (i >= destin.times.get(position))) {
                if (summary == true) {
                    String linea = new String("java -Xmx" + this.heapSize + "000000 " + " -jar" + " ../exe/" + r_jar + " ./SUMMARY/" + methodName + "/" + problema + "/summary.txt");
                    sentencias.addElement(linea);
                }
            }
        }

    }

    /**
     * Generates algorithm directories
     * @param al Algorithm
     * @param origen Origin
     * @param path_scripts Path for scripts
     * @param path_results Path for results
     * @param numNode Id of node
     * @param problema Name of data set
     */
    private void dirAlgorithmDataset(Algorithm al, int origen, String path_scripts, String path_results, int numNode, String problema) {

        DataSet ds = (DataSet) experimentGraph.getNodeAt(origen);

        // Scripts
        Vector conj = new Vector();
        Vector tra = new Vector();
        Vector tra2 = new Vector();
        Vector tst = new Vector();
        Vector tst2 = new Vector();

        /***************************************************************
         ***************  EDUCATIONAL KEEL   ****************************
         **************************************************************/
        String cadRutaParcial = "";

        if (Frame.buttonPressed == 0) //Button Experiments pressed
        {
            cadRutaParcial = "../datasets/";
        } else //Button Teaching pressed
        {
            cadRutaParcial = "./experiment/datasets/";
        }

        for (int j = 0; j < ((Vector) ds.tableVector.elementAt(Layer.layerActivo)).size(); j++) {
            tra.add(new String(cadRutaParcial + problema + "/" + ds.getTrainingAt(j)));
            tra2.add(new String(cadRutaParcial + problema + "/" + ds.getTrainingAt(j)));
            tst.add(new String(cadRutaParcial + problema + "/" + ds.getTestAt(j)));
            tst2.add(new String(cadRutaParcial + problema + "/" + ds.getTestAt(j)));
        }
        /***************************************************************
         ***************  EDUCATIONAL KEEL   ****************************
         **************************************************************/
        conj.add(tra);
        conj.add(tra2);
        conj.add(tst);
        conj.add(tst2);

        if (al.dsc.getSubtype() == Node.type_Preprocess) {
            al.getActivePair().writeScripts(path_scripts, "config",
                    fullName[numNode], problema, conj, "result", true, cvType, numberKFoldCross, expType);
        } else {
            al.getActivePair().writeScripts(path_scripts, "config",
                    fullName[numNode], problema, conj, "result", false, cvType, numberKFoldCross, expType);
        }
    }

    /**
     * @param al Algorithm
     * @param origen Origin
     * @param path_scripts Path for scripts
     * @param path_results Path for results
     */
    private void dirAlgorithmUsermethod(Algorithm al, int origen, String path_scripts, String path_results) {

        UserMethod mu = (UserMethod) experimentGraph.getNodeAt(origen);
        String problema = mu.dsc.getName();
        String path_tmp = "/" + problema;
        FileUtils.mkdir(path_scripts + path_tmp);
        FileUtils.mkdir(path_results + path_tmp);

        // Scripts
        path_tmp = path_scripts + "/" + problema;
        Vector conj = new Vector();
        Vector tra = new Vector();
        Vector tst = new Vector();
        if (mu.dsc.getSubtype() == Node.type_Preprocess) {
            tra = (Vector) mu.parametersUser.getTrainingOutputFiles().clone();
            tst = (Vector) mu.parametersUser.getTestOutputFiles().clone();
        } else {
            tra = (Vector) mu.parametersUser.getTrainingValidationFiles().clone();
            tst = (Vector) mu.parametersUser.getTestFiles().clone();
        }
        conj.add(tra);
        Vector tra2 = new Vector();
        tra2 = (Vector) mu.parametersUser.getTrainingValidationFiles().clone();
        conj.add(tra2);
        conj.add(tst);
        Vector tst2 = new Vector();
        tst2 = (Vector) mu.parametersUser.getTestFiles().clone();
        conj.add(tst2);
        Vector salidas = new Vector();
        salidas = (Vector) mu.parametersUser.getAdditionalOutputFiles().clone();
        if (salidas.size() != 0) {
            conj.add(salidas);

        }
        if (al.dsc.getSubtype() == Node.type_Preprocess) {
            al.getActivePair().writeScripts(path_tmp, "config",
                    al.dsc.getName(), problema, conj, "result", true, cvType, numberKFoldCross, expType);
        } else {
            al.getActivePair().writeScripts(path_tmp, "config",
                    al.dsc.getName(), problema, conj, "result", false, cvType, numberKFoldCross, expType);
        }
    }

    /**
     * Copy a jar file
     * @param origen1 Origin
     * @param destino1 Destination
     */
    private void copy_jar(String origen1, String destino1) {
        if (origen1.substring(0, 4).equals("jar:") || origen1.substring(0, 5).equals("file:")) {
            try {
                java.net.URL recurso = new java.net.URL(origen1);
                FileUtils.copy(recurso, destino1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // search in external directories
            //System.out.println("Copiando fichero " + origen + " a " + destino);
            FileUtils.copy(origen1, destino1);
        }
    }

    /**
     *
     * @param al
     * @param nodo
     * @param path
     * @param sentencias
     * @param r_jar
     */
    private void dirAlgorithmLQD(Algorithm al, int nodo, String path, Vector sentencias, String r_jar) {

        // creates directories and files for an algorithm
        // Copy algorithm executable
        String destino = path.concat("exe/" + al.dsc.getJarName());
        String origen = al.dsc.getPath() + al.dsc.getJarName();
        /*System.out.println("origen: " + origen);
        System.out.println("destino: " + destino);*/

        if (origen.substring(0, 4).equals("jar:") || origen.substring(0, 5).equals("file:")) {
            try {
                java.net.URL recurso = new java.net.URL(origen);
                FileUtils.copy(recurso, destino);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // search in external directories
            //System.out.println("Copiando fichero " + origen + " a " + destino);
            FileUtils.copy(origen, destino);
        }


        if (al.dsc.getSubtype() == Node.type_Preprocess && (al.dsc.getName().contains("Prelabelling") || al.dsc.getName().contains("Expert"))) {
            String destino1 = path.concat("exe/" + "100boost.jar");
            String origen1 = al.dsc.getPath() + "100boost.jar";

            copy_jar(origen1, destino1);

            destino1 = path.concat("exe/" + "results.jar");
            origen1 = al.dsc.getPath() + "results.jar";

            copy_jar(origen1, destino1);

        }

        // creates scripts and results directories
        Node way = experimentGraph.getNodeAt(nodo);
        String problema = "";
        String path_scripts = "";
        String path_summary = "";
        String path_summary_crisp = "";
        String path_results = "";
        String crisp = "";
        String type_lqd = "";
        boolean crisp_version = false;

        for (int i = 0; i < way.dsc.arg.size(); i++) {
            for (int data = 0; data < way.dsc.arg.get(i).data_selected.size(); data++) {
                /*if(way.dsc.arg.get(i).before.type==Node.type_Dataset)
                problema = way.dsc.arg.get(i).data_selected.get(data)+"-"+way.dsc.arg.get(i).parameters.get(data).parameter_data.get(way.dsc.arg.get(i).parameters.get(data).parameter_data.size()-1).get(0);
                else if(way.dsc.arg.get(i).before.type==Node.type_Algorithm)
                {
                problema =fullName[way.dsc.arg.get(i).before.id]+"-"+way.dsc.arg.get(i).data_selected.get(data)+"-"+way.dsc.arg.get(i).parameters.get(data).parameter_data.get(way.dsc.arg.get(i).parameters.get(data).parameter_data.size()-1).get(0);
                }*/
                problema = way.dsc.arg.get(i).problem.get(data);

                //path_scripts = path + "scripts/" + fullName[nodo] + "/" + problema;
                path_scripts = path + "scripts/" + way.id + "." + way.dsc.getName() + "/" + problema;
                FileUtils.mkdirs(path_scripts);
                if (summary == true) {

                    FileWriter fs1 = null;

                    // path_excel=path+"scripts/results.xls";
                    //fs1 = new FileWriter(path + "scripts/results.xls");

                    path_summary = path + "scripts/SUMMARY/";
                    FileUtils.mkdirs(path_summary);
                    path_summary_crisp = path_summary + way.id + "." + way.dsc.getName() + "/Crisp_" + problema;
                    path_summary = path_summary + way.id + "." + way.dsc.getName() + "/" + problema;

                    FileUtils.mkdirs(path_summary);


                }

                //preprocess methods drops its results on data directory
                if (al.dsc.getSubtype() == Node.type_Preprocess) {
                    //aqui es donde varios con el mismo
                    if (way.dsc.arg.get(i).type_lqd.compareTo("LQD") == 0) {
                        path_results = path + "datasets/LQD/" + way.id + "." + way.dsc.getName();
                        FileUtils.mkdirs(path_results);
                        path_results = path_results + "/" + problema;
                        type_lqd = "LQD";

                    } /*else if(way.dsc.arg.get(i).type_lqd.compareTo("C_LQD")==0)
                    {
                    path_results =path + "datasets/C_LQD/"+ fullName[nodo] + "." + problema;
                    type_lqd="C_LQD";
                    }
                    else if(way.dsc.arg.get(i).type_lqd.compareTo("LQD_C")==0)
                    {
                    path_results =path + "datasets/LQD_C/"+ fullName[nodo] + "." + problema;
                    type_lqd="LQD_C";
                    }*/ else if (way.dsc.arg.get(i).type_lqd.compareTo("CRISP") == 0) {
                        path_results = path + "datasets/CRISP/" + way.id + "." + way.dsc.getName();
                        FileUtils.mkdirs(path_results);
                        path_results = path_results + "/" + problema;
                        type_lqd = "CRISP";
                    }

                    FileUtils.mkdir(path_results);
                } else {
                    if (way.dsc.arg.get(i).type_lqd.compareTo("LQD") == 0) {
                        FileUtils.mkdir(path.concat("results/LQD"));
                        path_results = path + "results/LQD/" + way.id + "." + way.dsc.getName();
                        FileUtils.mkdirs(path_results);


                        if (((Parameters) way.par.elementAt(0)).crisp == 1 && problema.contains("C_LQD") == false) {
                            if (question == true && JOptionPane.showConfirmDialog(this,
                                    way.id + "." + way.dsc.getName() + " contains a crisp version. Do you want to include it in the results?",
                                    "Insert in the results", JOptionPane.YES_NO_OPTION, 3) == JOptionPane.YES_OPTION) {
                                crisp = path_results + "/" + problema + "_Crisp";
                                FileUtils.mkdir(crisp);
                                crisp_version = true;
                                question = false;
                                if (summary == true) {
                                    crisp = crisp + "/SUMMARY";
                                    FileUtils.mkdir(crisp);
                                    FileUtils.mkdirs(path_summary_crisp);
                                }

                            }//IF question
                            else {
                                // System.out.println("dice quenoooooooooooooooooo");
                                //summary=false;
                                question = false;
                            }
                        }

                        path_results = path_results + "/" + problema;
                        type_lqd = "LQD";
                    } /* else if(way.dsc.arg.get(i).type_lqd.compareTo("C_LQD")==0)
                    {
                    FileUtils.mkdir(path.concat("results/C_LQD"));
                    path_results = path + "results/C_LQD" + fullName[nodo] + "." + problema;
                    type_lqd="C_LQD";
                    }
                    else if(way.dsc.arg.get(i).type_lqd.compareTo("LQD_C")==0)
                    {
                    FileUtils.mkdir(path.concat("results/LQD_C"));
                    path_results = path + "results/LQD_C" + fullName[nodo] + "." + problema;
                    type_lqd="LQD_C";
                    }*/ else if (way.dsc.arg.get(i).type_lqd.compareTo("CRISP") == 0) {
                        FileUtils.mkdir(path.concat("results/CRISP"));
                        path_results = path + "results/CRISP/" + way.id + "." + way.dsc.getName();
                        FileUtils.mkdirs(path_results);
                        path_results = path_results + "/" + problema;
                        type_lqd = "CRISP";
                    }
                    FileUtils.mkdir(path_results);
                    if (summary == true) {
                        path_results = path_results + "/SUMMARY";
                        FileUtils.mkdir(path_results);
                    }

                }

                if (way.dsc.arg.get(i).before.type == Node.type_Dataset) {
                    dirAlgorithmDatasetLQD(al, way.dsc.arg.get(i), path_scripts, path_summary, path_summary_crisp, nodo, data, type_lqd, 0, sentencias, problema, crisp_version, r_jar);
                } else if (way.dsc.arg.get(i).before.type == Node.type_Algorithm) {
                    dirAlgorithmDatasetLQD(al, way.dsc.arg.get(i), path_scripts, path_summary, path_summary_crisp, nodo, data, type_lqd, 1, sentencias, problema, crisp_version, r_jar);
                }


            }

        }


    }

    /**
     * Generates algorithm sentences
     * @param al Algorithm
     * @param nodo Node
     * @param path Path
     * @param sentencias Sentences
     * @param root Root node
     */
    private void dirAlgorithm(Algorithm al, int nodo, String path, Vector sentencias, int root) {

        /***************************************************************
         ***************  EDUCATIONAL KEEL  ****************************
         **************************************************************/
        int contSeed = 0;
        int contSeedAux = 0;
        /***************************************************************
         ***************  EDUCATIONAL KEEL  ****************************
         **************************************************************/
        // creates directories and files for an algorithm
        if (al.getActivePair().isProbabilistic()) {
            // add seeds
            for (int i = 0; i < al.getActivePair().getExe(); i++) {
                al.getActivePair().addSeed(Integer.toString(Math.abs(rnd.nextInt())));
                /***************************************************************
                 ***************  EDUCATIONAL KEEL  ****************************
                 **************************************************************/
                contSeed++;
            /***************************************************************
             ***************  EDUCATIONAL KEEL  ****************************
             **************************************************************/
            }
        }

        // Copy algorithm executable
        String destino = path.concat("exe/" + al.dsc.getJarName());
        String origen = al.dsc.getPath() + al.dsc.getJarName();
        //System.out.println("origen: " + origen);
        //System.out.println("destino: " + destino);

        if (origen.substring(0, 4).equals("jar:") || origen.substring(0, 5).equals("file:")) {
            try {
                java.net.URL recurso = new java.net.URL(origen);
                FileUtils.copy(recurso, destino);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // search in external directories
            //System.out.println("Copiando fichero " + origen + " a " + destino);
            FileUtils.copy(origen, destino);
        }


        // creates scripts and results directories
        DataSet ds = (DataSet) experimentGraph.getNodeAt(root);
        String problema = ds.dsc.getName();

        String path_results = "";
        String path_scripts = "";

        path_scripts = path + "scripts/" + fullName[nodo] + "/" + problema;
        FileUtils.mkdirs(path_scripts);

        //preprocess methods drops its results on data directory
        /***************************************************************
         ***************  EDUCATIONAL KEEL  ***************************
         **************************************************************/
        if (Frame.buttonPressed == 0) //Button Experiments pressed
        {
            //preprocess methods drops its results on data directory
            if (al.dsc.getSubtype() == Node.type_Preprocess) {
                path_results = path + "datasets/" + fullName[nodo] + "." + problema;
                FileUtils.mkdir(path_results);
            } else {
                path_results = path + "results/" + fullName[nodo] + "." + problema;
                FileUtils.mkdir(path_results);
            }
        } else //Button Teaching pressed
        {
            //if (al.dsc.getSubtype() == Node.type_Preprocess) {
            //path_results = path + "datasets/" + fullName[nodo] + "." + problema;
            //path_results = path + "datasets/" + fullName[nodo];
            //FileUtils.mkdir(path_results);
            //path_results = path_results + "/" + problema;
            //FileUtils.mkdir(path_results);
            //} else {
            //path_results = path + "results/" + fullName[nodo] + "." + problema;
            path_results = path + "results/" + fullName[nodo];
            FileUtils.mkdir(path_results);
            path_results = path_results + "/" + problema;
            FileUtils.mkdir(path_results);
        //}
        }
        /***************************************************************
         ***************  EDUCATIONAL KEEL  ***************************
         **************************************************************/
        // clear vectors
        al.getActivePair().outputs_tra.removeAllElements();
        al.getActivePair().outputs_tst.removeAllElements();
        al.getActivePair().tra_val.removeAllElements();
        al.getActivePair().tst_val.removeAllElements();
        al.getActivePair().configs.removeAllElements();
        al.getActivePair().additional_outputs.removeAllElements();

        // generate subdirectories and scripts
        for (int i = 0; i < experimentGraph.numArcs(); i++) {
            Arc a = experimentGraph.getArcAt(i);
            if (a.getDestination() == nodo) {
                if (experimentGraph.getNodeAt(a.getSource()).type == Node.type_Dataset) {
                    // data from dataset
                    dirAlgorithmDataset(al, a.getSource(), path_scripts, path_results, nodo, problema);
                } else if (experimentGraph.getNodeAt(a.getSource()).type == Node.type_Algorithm) {
                    dirAlgorithmAlgorithm(al, a.getSource(), path_scripts, path_results, nodo, problema);
                } else if (experimentGraph.getNodeAt(a.getSource()).type == Node.type_userMethod) {
                    dirAlgorithmUsermethod(al, a.getSource(), path_scripts, path_results);
                }
            }
        }

        // algorithm commands
        Vector scripts = new Vector();
        scripts = (Vector) al.getActivePair().getConfigs().clone();
        //scripts.size has the number of partitions of a experiment
        for (int j = 0; j < scripts.size(); j++) {
            //System.out.println("Processing " + al.dsc.getPath() + al.dsc.getJarName());
            boolean emitir = false;

            try {
                if (al.dsc.getPath().substring(0, 4).equals("jar:") || al.dsc.getPath().substring(0, 5).equals("file:")) {
                    //System.out.println("Debug ERR 1915 " + al.dsc.getPath() + al.dsc.getJarName());
                    java.net.URL miurl = new java.net.URL(al.dsc.getPath() + al.dsc.getJarName());
                    if (miurl.openStream() != null) {
                        emitir = true;
                    }
                //System.out.println("resource emits=" + emitir);
                } else {
                    File f = new File(al.dsc.getPath() + al.dsc.getJarName());
                    if (f.isFile()) {
                        emitir = true;
                    }
                //System.out.println("file emits=" + emitir);

                }

                if (emitir) {
                    /***************************************************************
                     ***************  EDUCATIONAL KEEL  ****************************
                     **************************************************************/
                    String linea = "";
                    if (Frame.buttonPressed == 0) //Button Experiments Pressed
                    {
                        linea = new String("java -Xmx" + this.heapSize + "000000 " + " -jar" + " ../exe/" + al.dsc.getJarName() + " ." + scripts.elementAt(j));
                    } else //Button Teaching pressed
                    {
                        int tipo = al.dsc.getSubtype();
                        String tipoS = "";
                        switch (tipo) {
                            case 0:
                                tipoS = "DataSet";
                                break;
                            case 1:
                                tipoS = "Algorithm";
                                break;
                            case 2:
                                tipoS = "userMethod";
                                break;
                            case 3:
                                tipoS = "Jclec";
                                break;
                            case 4:
                                tipoS = "Preprocess";
                                StringTokenizer st = new StringTokenizer(al.dsc.getName(), "-");
                                st.nextToken();
                                String aux = st.nextToken();
                                tipoS = tipoS + "-" + aux;
                                break;
                            case 5:
                                tipoS = "Method";
                                break;
                            case 6:
                                tipoS = "Postprocess";
                                break;
                            case 7:
                                tipoS = "Test";
                                break;
                            case 8:
                                tipoS = "Multiplexor";
                                break;
                            case 9:
                                tipoS = "Undefined";
                                break;
                            case 10:
                                tipoS = "Visor";
                                break;
                        }
                        if (al.getActivePair().getIfSeed() == true) {
                            linea = new String("java -Xmx" + this.heapSize + "000000 " + " -jar" + " ./experiment/exe/" +
                                    al.dsc.getJarName() +
                                    " ./experiment/scripts" +
                                    scripts.elementAt(j) + " " + tipoS + " " +
                                    al.getActivePair().getSeed(contSeedAux));
                        } else {
                            linea = new String("java -Xmx" + this.heapSize + "000000 " + " -jar" + " ./experiment/exe/" +
                                    al.dsc.getJarName() +
                                    " ./experiment/scripts" +
                                    scripts.elementAt(j) + " " + tipoS + " " +
                                    "null");
                        }

                        if (contSeedAux == contSeed - 1) {
                            contSeedAux = -1;
                        }
                        contSeedAux++;
                    }

                    /***************************************************************
                     ***************  EDUCATIONAL KEEL  ****************************
                     **************************************************************/
                    //System.out.println("add line " + linea);
                    if (sentencias.contains(linea) == false) {
                        sentencias.addElement(linea);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Generates test sentences
     * @param t Test
     * @param nodo Node
     * @param path Path
     * @param sentencias Sentences
     */
    private void dirTest(Test t, int nodo, String path, Vector sentencias) {
        // Generates directories for a test

        if (t.getActivePair().isProbabilistic()) {
            // add seeds
            for (int i = 0; i < t.getActivePair().getExe(); i++) {
                t.getActivePair().addSeed(Integer.toString(Math.abs(rnd.nextInt())));
            }
        }

        // Copy test executable
        String origen = t.dsc.getPath() + t.dsc.getJarName();
        String destino = path.concat("exe/" + t.dsc.getJarName());

        if (origen.substring(0, 4).equals("jar:") || origen.substring(0, 5).equals("file:")) {
            try {
                java.net.URL recurso = new java.net.URL(origen);
                /*System.out.println("Copying test resource: " + recurso.toString() +
                " a " + destino);*/
                // duplicates out of .jar
                FileUtils.copy(recurso, destino);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // search in external directory
            //System.out.println("Copying test file:" + origen + " a " + destino);
            FileUtils.copy(origen, destino);
        }

        // creates scripts and results directories
        String path_scripts = path + "scripts/" + t.dsc.getName();
        FileUtils.mkdir(path_scripts);
        String path_results = path + "results/" + t.dsc.getName();
        FileUtils.mkdir(path_results);

        // clear vectors
        t.getActivePair().outputs_tra.removeAllElements();
        t.getActivePair().outputs_tst.removeAllElements();
        t.getActivePair().tra_val.removeAllElements();
        t.getActivePair().tst_val.removeAllElements();
        t.getActivePair().configs.removeAllElements();
        t.getActivePair().additional_outputs.removeAllElements();

        // generate subdirectories and scripts
        // store information anda creates an unique file
        // problem name is a concatenation of various names that are included in the test
        String problema = "TST";
        Vector conj = new Vector();

        for (int i = 0; i < experimentGraph.numArcs(); i++) {
            Arc a = experimentGraph.getArcAt(i);

            if (a.getDestination() == nodo) {

                // input

                if (experimentGraph.getNodeAt(a.getSource()).type == Node.type_Algorithm) {
                    // add to name
                    Algorithm algo = (Algorithm) experimentGraph.getNodeAt(a.getSource());
                    if (problema.equals("TST")) {
                        problema += algo.dsc.getName();
                    } else {
                        problema += ("vs" + algo.dsc.getName());

                    }
                    Vector tst = new Vector();
                    tst = (Vector) algo.getActivePair().getTestOutputFiles().clone();
                    conj.add(tst);
                    //System.out.println("Anhadiendo TST " + catVString(tst));

                    Vector tra = new Vector();
                    tra = (Vector) algo.getActivePair().getTrainingOutputFiles().clone();
                    conj.add(tra);
                //System.out.println("Anhadiendo TRA " + catVString(tra));

                } else if (experimentGraph.getNodeAt(a.getSource()).type == Node.type_userMethod) {

                    UserMethod algo = (UserMethod) experimentGraph.getNodeAt(a.getSource());
                    if (problema.equals("TST")) {
                        problema += algo.dsc.getName();
                    } else {
                        problema += ("vs" + algo.dsc.getName());

                    }
                    Vector tst = new Vector();
                    tst = (Vector) algo.parametersUser.getTestOutputFiles().clone();
                    conj.add(tst);
                    //System.out.println("add own method " + catVString(tst));

                    Vector tra = new Vector();
                    tra = (Vector) algo.parametersUser.getTrainingOutputFiles().clone();
                    conj.add(tra);
                //System.out.println("Anhadiendo TRA " + catVString(tra));


                }
            }
        }

        //System.out.println("Problem name: " + problema);

//        System.out.println("'conj' vector contains :");
//        for (int i = 0; i < conj.size(); i++) {
//            System.out.println("Cell " + i + catVString((Vector) (conj.elementAt(i))));
//        }

        String fullName = new String(problema);

        // If the path is too long, we limit it

        if (path_scripts.length() + problema.length() > 250) {
            //System.out.println("Limiting path length");
            int i = 0;
            String pshort = problema.substring(0, 50);
            boolean exists = true;
            while (exists) {
                exists = (new File(path_scripts + "/" + pshort + i)).exists();
                if (exists) {
                    // File or directory exists
                    i++;
                } else {
                    problema = pshort + i;
                }
            }

        }

        String path_tmp = "/" + problema;
        FileUtils.mkdir(path_scripts + path_tmp);
        FileUtils.mkdir(path_results + path_tmp);
        path_tmp = path_scripts + "/" + problema;

        /*System.out.println("Before save scripts " +
        t.getActivePair().getConfigs().size());*/

        //Search for the DataSet node
        int i = 0;
        while (i < experimentGraph.numNodes() && experimentGraph.getNodeAt(i).type != Node.type_Dataset) {
            i++;
        }
        DataSet ds = (DataSet) experimentGraph.getNodeAt(i);
        String relationBBDD = ds.dsc.getName();

        t.getActivePair().writeTestScripts(path_tmp, "config", t.dsc.getName(),
                problema, conj, "result", true, fullName, relationBBDD);


        /*System.out.println("After save scripts " +
        t.getActivePair().getConfigs().size());*/


        Vector scripts = new Vector();
        scripts = (Vector) t.getActivePair().getConfigs().clone();
        for (int j = 0; j < scripts.size(); j++) {
            //System.out.println("Processing " + t.dsc.getPath() + t.dsc.getJarName());
            boolean emitir = false;

            try {
                if (t.dsc.getPath().substring(0, 4).equals("jar:") || t.dsc.getPath().substring(0, 5).equals("file:")) {
                    java.net.URL miurl = new java.net.URL(t.dsc.getPath() +
                            t.dsc.getJarName());
                    if (miurl.openStream() != null) {
                        emitir = true;
                    }
                //System.out.println("resource emits=" + emitir);
                } else {
                    File f = new File(t.dsc.getPath() + t.dsc.getJarName());
                    if (f.isFile()) {
                        emitir = true;
                    }
                //System.out.println("file emits=" + emitir);

                }

                if (emitir) {
                    /***************************************************************
                     ***************  EDUCATIONAL KEEL *****************************
                     **************************************************************/
                    String linea = "";
                    if (Frame.buttonPressed == 0) //Button Experiments pressed
                    {
                        linea = new String("java -Xmx" + this.heapSize + "000000 " + " -jar" + " ../exe/" +
                                t.dsc.getJarName() + " ." + scripts.elementAt(j));
                    } else //Button Teaching pressed
                    {
                        int tipo = t.dsc.getSubtype();
                        String tipoS = "";

                        switch (tipo) {
                            case 0:
                                tipoS = "Dataset";
                                break;
                            case 1:
                                tipoS = "Algorithm";
                                break;
                            case 2:
                                tipoS = "userMethod";
                                break;
                            case 3:
                                tipoS = "Jclec";
                                break;
                            case 4:
                                tipoS = "Preprocess";
                                StringTokenizer st = new StringTokenizer(t.dsc.getName(), "-");
                                String aux = st.nextToken();
                                tipoS = tipoS + "-" + aux;
                                break;
                            case 5:
                                tipoS = "Method";
                                break;
                            case 6:
                                tipoS = "Postprocess";
                                break;
                            case 7:
                                tipoS = "Test";
                                break;
                            case 8:
                                tipoS = "Multiplexor";
                                break;
                            case 9:
                                tipoS = "Undefined";
                                break;
                            case 10:
                                tipoS = "Visor";
                                break;
                        }
                        linea = new String("java -Xmx" + this.heapSize + "000000 " + " -jar" + " ./experiment/exe/" +
                                t.dsc.getJarName() +
                                " ./experiment/scripts" +
                                scripts.elementAt(j) + " " + tipoS);
                    /***************************************************************
                     ***************  EDUCATIONAL KEEL *****************************
                     **************************************************************/
                    }
                    //System.out.println("Add line " + linea);
                    if (sentencias.contains(linea) == false) {
                        sentencias.addElement(linea);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Generates test sentences
     * @param t Test
     * @param origen Source
     * @param path_scripts Path for scripts
     * @param path_results Path for results
     */
    private void dirTestAlgoritmoOriginal(Test t, int origen, String path_scripts, String path_results) {
        Algorithm algo = (Algorithm) experimentGraph.getNodeAt(origen);
        String problema = algo.dsc.getName();
        String path_tmp = "/" + problema;
        FileUtils.mkdir(path_scripts + path_tmp);
        FileUtils.mkdir(path_results + path_tmp);
        // Scripts
        path_tmp = path_scripts + "/" + problema;
        Vector conj = new Vector();
        Vector tra = new Vector();
        tra = (Vector) algo.getActivePair().getTrainingOutputFiles().clone();
        conj.add(tra);
        Vector tra2 = new Vector();
        tra2 = (Vector) algo.getActivePair().getTrainingValidationFiles().clone();
        conj.add(tra2);
        Vector tst = new Vector();
        tst = (Vector) algo.getActivePair().getTestOutputFiles().clone();
        conj.add(tst);
        Vector tst2 = new Vector();
        tst2 = (Vector) algo.getActivePair().getTestFiles().clone();
        conj.add(tst2);

        t.getActivePair().writeScripts(path_tmp, "config", t.dsc.getName(),
                problema, conj, "result", true, cvType, numberKFoldCross, expType);
    }

    /**
     * Generates test sentences
     * @param t Test
     * @param origen Source
     * @param path_scripts Path for scripts
     * @param path_results Path for results
     */
    private void dirTestUsuario(Test t, int origen, String path_scripts, String path_results) {
        UserMethod mu = (UserMethod) experimentGraph.getNodeAt(origen);
        String problema = mu.dsc.getName();
        String path_tmp = "/" + problema;
        FileUtils.mkdir(path_scripts + path_tmp);
        FileUtils.mkdir(path_results + path_tmp);
        // Scripts
        path_tmp = path_scripts + "/" + problema;
        Vector conj = new Vector();
        Vector tra = new Vector();
        tra = (Vector) mu.parametersUser.getTrainingOutputFiles().clone();
        conj.add(tra);
        Vector tra2 = new Vector();
        tra2 = (Vector) mu.parametersUser.getTrainingValidationFiles().clone();
        conj.add(tra2);
        Vector tst = new Vector();
        tst = (Vector) mu.parametersUser.getTestOutputFiles().clone();
        conj.add(tst);
        Vector tst2 = new Vector();
        tst2 = (Vector) mu.parametersUser.getTestFiles().clone();
        conj.add(tst2);

        t.getActivePair().writeScripts(path_tmp, "config", t.dsc.getName(),
                problema, conj, "result", true, cvType, numberKFoldCross, expType);
    }

    /**
     * Generates user sentences
     * @param mu User methods
     * @param origen Source
     * @param path_scripts Path for scripts
     * @param path_results Path for results
     */
    private void dirUsermethodDataset(UserMethod mu, int origen, String path_scripts, String path_results) {
        DataSet ds = (DataSet) experimentGraph.getNodeAt(origen);
        String problema = ds.dsc.getName();
        String path_tmp = "/" + problema;
        FileUtils.mkdir(path_scripts + path_tmp);
        FileUtils.mkdir(path_results + path_tmp);
        // Scripts
        path_tmp = path_scripts + "/" + problema;
        Vector conj = new Vector();
        Vector tra = new Vector();
        Vector tra2 = new Vector();
        Vector tst = new Vector();
        Vector tst2 = new Vector();

        /***************************************************************
         ***************  EDUCATIONAL KEEL  ****************************
         **************************************************************/
        String cadRutaParcial = "";
        if (Frame.buttonPressed == 0) //Button Experiments pressed
        {
            cadRutaParcial = "../datasets/";
        } else //Button Teaching pressed
        {
            cadRutaParcial = "./experiment/datasets/";
        }

        for (int j = 0; j < ((Vector) ds.tableVector.elementAt(Layer.layerActivo)).size(); j++) {
            tra.add(new String(cadRutaParcial + problema + "/" + ds.getTrainingAt(j)));
            tra2.add(new String(cadRutaParcial + problema + "/" + ds.getTrainingAt(j)));
            tst.add(new String(cadRutaParcial + problema + "/" + ds.getTestAt(j)));
            tst2.add(new String(cadRutaParcial + problema + "/" + ds.getTestAt(j)));
        }
        /***************************************************************
         ***************  EDUCATIONAL KEEL  ****************************
         **************************************************************/
        conj.add(tra);
        conj.add(tra2);
        conj.add(tst);
        conj.add(tst2);

        if (mu.dsc.getSubtype() == Node.type_Preprocess) {
            mu.parametersUser.writeScripts(path_tmp, "config", mu.dsc.getName(),
                    problema, conj, "result", true, cvType, numberKFoldCross, expType);
        } else {
            mu.parametersUser.writeScripts(path_tmp, "config", mu.dsc.getName(),
                    problema, conj, "result", false, cvType, numberKFoldCross, expType);
        }
    }

    /**
     * Generates user sentences
     * @param mu User methods
     * @param origen Source
     * @param path_scripts Path for scripts
     * @param path_results Path for results
     */
    private void dirUsermethodAlgorithm(UserMethod mu, int origen, String path_scripts, String path_results) {
        Algorithm algo = (Algorithm) experimentGraph.getNodeAt(origen);
        String problema = algo.dsc.getName();
        String path_tmp = "/" + problema;
        FileUtils.mkdir(path_scripts + path_tmp);
        FileUtils.mkdir(path_results + path_tmp);
        // Scripts
        path_tmp = path_scripts + "/" + problema;
        Vector conj = new Vector();
        Vector tra = new Vector();
        Vector tst = new Vector();
        if (algo.dsc.getSubtype() == Node.type_Preprocess || mu.dsc.getSubtype() == Node.type_Test || mu.dsc.getSubtype() == Node.type_Visor) {
            tra = (Vector) algo.getActivePair().getTrainingOutputFiles().clone();
            tst = (Vector) algo.getActivePair().getTestOutputFiles().clone();
        } else {
            tra = (Vector) algo.getActivePair().getTrainingValidationFiles().clone();
            tst = (Vector) algo.getActivePair().getTestFiles().clone();
        }
        conj.add(tra);
        Vector tra2 = new Vector();
        tra2 = (Vector) algo.getActivePair().getTrainingValidationFiles().clone();
        conj.add(tra2);
        conj.add(tst);
        Vector tst2 = new Vector();
        tst2 = (Vector) algo.getActivePair().getTestFiles().clone();
        conj.add(tst2);
        Vector salidas = new Vector();
        salidas = (Vector) algo.getActivePair().getAdditionalOutputFiles().clone();
        if (salidas.size() != 0) {
            conj.add(salidas);

        }
        if ((mu.dsc.getSubtype() == Node.type_Preprocess) || (mu.dsc.getSubtype() == Node.type_Test || mu.dsc.getSubtype() == Node.type_Visor)) {
            // only needs 2 inputs
            mu.parametersUser.writeScripts(path_tmp, "config", mu.dsc.getName(),
                    problema, conj, "result", true, cvType, numberKFoldCross, expType);
        } else {
            mu.parametersUser.writeScripts(path_tmp, "config", mu.dsc.getName(),
                    problema, conj, "result", false, cvType, numberKFoldCross, expType);
        }
    }

    /**
     * Generates user sentences
     * @param mu User methods
     * @param origen Source
     * @param path_scripts Path for scripts
     * @param path_results Path for results
     */
    private void dirUsermethodUsermethod(UserMethod mu, int origen, String path_scripts, String path_results) {
        UserMethod met = (UserMethod) experimentGraph.getNodeAt(origen);
        String problema = met.dsc.getName();
        String path_tmp = "/" + problema;
        FileUtils.mkdir(path_scripts + path_tmp);
        FileUtils.mkdir(path_results + path_tmp);
        // Scripts
        path_tmp = path_scripts + "/" + problema;
        Vector conj = new Vector();
        Vector tra = new Vector();
        Vector tst = new Vector();
        if (met.dsc.getSubtype() == Node.type_Preprocess || mu.dsc.getSubtype() == Node.type_Test || mu.dsc.getSubtype() == Node.type_Visor) {
            tra = (Vector) met.parametersUser.getTrainingOutputFiles().clone();
            tst = (Vector) met.parametersUser.getTestOutputFiles().clone();
        } else {
            tra = (Vector) met.parametersUser.getTrainingValidationFiles().clone();
            tst = (Vector) met.parametersUser.getTestFiles().clone();
        }
        conj.add(tra);
        Vector tra2 = new Vector();
        tra2 = (Vector) met.parametersUser.getTrainingValidationFiles().clone();
        conj.add(tra2);
        conj.add(tst);
        Vector tst2 = new Vector();
        tst2 = (Vector) met.parametersUser.getTestFiles().clone();
        conj.add(tst2);
        Vector salidas = new Vector();
        salidas = (Vector) met.parametersUser.getAdditionalOutputFiles().clone();
        if (salidas.size() != 0) {
            conj.add(salidas);

        }
        if ((mu.dsc.getSubtype() == Node.type_Preprocess) || (mu.dsc.getSubtype() == Node.type_Test || mu.dsc.getSubtype() == Node.type_Visor)) {
            // only needs 2 inputs
            mu.parametersUser.writeScripts(path_tmp, "config", mu.dsc.getName(),
                    problema, conj, "result", true, cvType, numberKFoldCross, expType);
        } else {
            mu.parametersUser.writeScripts(path_tmp, "config", mu.dsc.getName(),
                    problema, conj, "result", false, cvType, numberKFoldCross, expType);
        }
    }

    /**
     * Generates user sentences
     * @param mu User methods
     * @param nodo Node
     * @param path Path
     * @param sentencias Sentences
     */
    private void dirUsermethod(UserMethod mu, int nodo, String path, Vector sentencias) {
        // generates directories and files for an user's method
        if (mu.parametersUser.isProbabilistic()) {
            // add seeds
            for (int i = 0; i < mu.parametersUser.getExe(); i++) {
                // mu.parametersUser.addSemilla(Long.toString(Math.abs(aleatorio.nextLong())));
                mu.parametersUser.addSeed(Integer.toString(Math.abs(rnd.nextInt())));
            }
        }

        // Copy executable
        File exe = new File(mu.dsc.getPath() + mu.dsc.getName());
        String destino = path.concat("exe/" + exe.getName());
        FileUtils.copy(mu.dsc.getPath() + mu.dsc.getName(), destino);

        // creates scripts and results directories
        String path_scripts = path + "scripts/" + mu.dsc.getName();
        FileUtils.mkdir(path_scripts);
        String path_results = path + "results/" + mu.dsc.getName();
        FileUtils.mkdir(path_results);

        // clear vectors
        mu.parametersUser.outputs_tra.removeAllElements();
        mu.parametersUser.outputs_tst.removeAllElements();
        mu.parametersUser.tra_val.removeAllElements();
        mu.parametersUser.tst_val.removeAllElements();
        mu.parametersUser.configs.removeAllElements();
        mu.parametersUser.additional_outputs.removeAllElements();

        // creates subdirectories and scripts like an algorithm
        for (int i = 0; i < experimentGraph.numArcs(); i++) {
            Arc a = experimentGraph.getArcAt(i);
            if (a.getDestination() == nodo) {
                if (experimentGraph.getNodeAt(a.getSource()).type == Node.type_Dataset) {
                    // data from dataset
                    dirUsermethodDataset(mu, a.getSource(), path_scripts,
                            path_results);
                } else if (experimentGraph.getNodeAt(a.getSource()).type == Node.type_Algorithm) {
                    // data from algorithm
                    dirUsermethodAlgorithm(mu, a.getSource(), path_scripts,
                            path_results);
                } else if (experimentGraph.getNodeAt(a.getSource()).type == Node.type_userMethod) {
                    // data from another user's method
                    dirUsermethodUsermethod(mu, a.getSource(), path_scripts,
                            path_results);
                }

            }
        }

        // algorithm comands
        Vector scripts = new Vector();
        scripts = (Vector) mu.parametersUser.getConfigs().clone();
        for (int j = 0; j < scripts.size(); j++) {
            File f = new File(mu.dsc.getPath() + mu.dsc.getName());
            if (f.isFile()) {
                /***************************************************************
                 ***************  EDUCATIONAL KEEL  ****************************
                 **************************************************************/
                String linea = "";
                if (Frame.buttonPressed == 0) //Button Experiments Pressed
                {
                    linea = new String(mu.command + " ../exe/" + f.getName() + " ." + scripts.elementAt(j));
                } else {
                    linea = new String(mu.command + " ./experiment/exe/" + f.getName() + " ./experiment/scripts" + scripts.elementAt(j));
                }
                /***************************************************************
                 ***************  EDUCATIONAL KEEL  ****************************
                 **************************************************************/
                if (sentencias.contains(linea) == false) {
                    sentencias.addElement(linea);
                }
            }
        }
    }

    /**
     * Recursive function to obtain absolute names of every element of the graph
     * @param node Identifier of the node which will be named
     * @param prefix Prefix string of the name, inherited from its parents
     */
    private void applyAbsoluteName(int node, String prefix) {

        String name;

        Node actual = experimentGraph.getNodeAt(node);
        name = actual.dsc.getName();
        if (name.length() > 40) {
            name = name.substring(0, 39);
        }

        if (actual.getType() == Node.type_Test) {
            fullName[node] = name;
        }
        if (actual.getType() == Node.type_Algorithm) {

            if (prefix.equals("")) {
                fullName[node] = name;
            } else {

                fullName[node] = prefix + "." + name;
            }

            //scan arcs
            for (int i = 0; i < experimentGraph.numArcs(); i++) {
                if (objType != LQD) {
                    if (experimentGraph.getArcAt(i).getSource() == node) {
                        applyAbsoluteName(experimentGraph.getArcAt(i).getDestination(), fullName[node]);
                    }
                } else {
                    if (experimentGraph.getArcAt(i).getSource() == experimentGraph.getNodeAt(node).id) {
                        for (int n = 0; n < experimentGraph.numNodes(); n++) {

                            if (experimentGraph.getArcAt(i).getDestination() == experimentGraph.getNodeAt(n).id) {
                                applyAbsoluteName(n, fullName[node]);
                                break;
                            }
                        }

                    }

                }

            }
        }
    }

    /**
     * Checks its a given node is duplicated on the experiment
     * @param numNode Identifier of the node to be checked
     *
     */
    private boolean duplicated(int numNode) {

        return duplicates[numNode];

    }

    /**
     * Compute absolute names of every element of the graph
     */
    private void absoluteNames() {

        int roots = 0;
        boolean dup;



        //Find root node (datasets)
        for (int i = 0; i < experimentGraph.numNodes(); i++) {
            if (experimentGraph.getNodeAt(i).type == Node.type_Dataset) {
                roots = i;
            }
        }

        //Initialize array of full names
        fullName = new String[experimentGraph.numNodes()];

        for (int i = 0; i < experimentGraph.numNodes(); i++) {
            fullName[i] = "";
        }
        fullName[roots] = "Datasets";

        //Get the names
        for (int i = 0; i < experimentGraph.numArcs(); i++) {
            if (experimentGraph.getArcAt(i).getSource() == roots) {
                applyAbsoluteName(experimentGraph.getArcAt(i).getDestination(), "");
            }
        }

        //Check for duplicates
        boolean dupli = false;
        int cont = 1;
        for (int i = 1; i < experimentGraph.numNodes(); i++) {
            dupli = false;
            for (int j = i - 1; j > -1; j--) {
                if (fullName[i].equals(fullName[j])) {
                    dupli = true;
                    fullName[j] = cont + "-" + fullName[j];
                    cont++;
                }
            }
            if (dupli == true) {
                fullName[i] = "0-" + fullName[i];
            }
        }

    }

    /**
     * Compute absolute names of every element of the graph
     */
    private void absoluteNamesLQD() {

        Vector<Integer> rooters = new Vector<Integer>();

        /*  boolean dup;

        duplicates = new boolean[experimentGraph.numNodes()];*/


        //Initialize array of full names
        fullName = new String[experimentGraph.numNodes()];

        for (int i = 0; i < experimentGraph.numNodes(); i++) {
            fullName[i] = "";
        }

        //Find root nodeS (datasets)
        for (int i = 0; i < experimentGraph.numNodes(); i++) {
            if (experimentGraph.getNodeAt(i).type == Node.type_Dataset) {
                rooters.addElement(i);
                if (experimentGraph.getNodeAt(i).type_lqd == Node.LQD) {
                    fullName[i] = "DatasetsLQD";
                } else if (experimentGraph.getNodeAt(i).type_lqd == Node.LQD_C) {
                    fullName[i] = "DatasetsLQD_C";
                } else if (experimentGraph.getNodeAt(i).type_lqd == Node.C_LQD) {
                    fullName[i] = "DatasetsC_LQD";
                } else if (experimentGraph.getNodeAt(i).type_lqd == Node.CRISP2) {
                    fullName[i] = "DatasetsCRISP";
                }
            }
        }

        //Get the names
        for (int i = 0; i < experimentGraph.numArcs(); i++) {
            for (int r = 0; r < rooters.size(); r++) {

                if (experimentGraph.getArcAt(i).getSource() == experimentGraph.getNodeAt(rooters.get(r)).id) {

                    for (int n = 0; n < experimentGraph.numNodes(); n++) {

                        if (experimentGraph.getArcAt(i).getDestination() == experimentGraph.getNodeAt(n).id) {
                            applyAbsoluteName(n, "");
                            break;
                        }
                    }
                }
            }
        }

        //Check for duplicates
        boolean dupli = false;
        int cont = 1;
        for (int i = 1; i < experimentGraph.numNodes(); i++) {
            dupli = false;
            for (int j = i - 1; j > -1; j--) {
                if (fullName[i].equals(fullName[j])) {
                    dupli = true;
                    fullName[j] = cont + "-" + fullName[j];
                    cont++;
                }
            }
            if (dupli == true) {
                fullName[i] = "0-" + fullName[i];
            }
        }


    }

    /**
     * Generates all the experiment directories, and fills them
     * with the data sets and scripts.
     * Once completed, the experiment is compressed.
     */

    /*search*/
    private void generateExperimentDirectories() {
        String nameExp = "";
        String comprimio = "";
        int opcion0 = 0; //All nodes connected in Teaching
        int opcion1 = 0; //JFileChooser.APPROVE_OPTION
        int opcion2 = 0; //JOptionPane.YES_OPTION

        /***************************************************************
         *********************  EDUCATIONAL KEEL  **********************
         **************************************************************/
        if (Frame.buttonPressed == 1) //Button Teaching pressed
        {
            nameExp = "experiment";
        }
        /***************************************************************
         *********************  EDUCATIONAL KEEL  **********************
         **************************************************************/

        //List with number of partitons in each experiment
        List<Integer> partitionList = new ArrayList<Integer>();
        int countJobs = 0;
        int indexRoot;

        if (check()) {
            // start random number generator
            rnd = new Random(experimentGraph.getSeed());

            // warning: some nodes are not conected
            Vector sueltos = isolatedNodes();
            if (sueltos.size() != 0) {
                /***************************************************************
                 *********************  EDUCATIONAL KEEL  **********************
                 **************************************************************/
                if (Frame.buttonPressed == 1) //Button Teaching pressed
                {
                    JOptionPane.showMessageDialog(this,
                            "Some nodes are not connected. Please, connect it correctly.",
                            "Warning", JOptionPane.WARNING_MESSAGE);

                    return;
                } else //Button Experiments pressed
                {
                    JOptionPane.showMessageDialog(
                            this,
                            "Some nodes are not connected. Please, connect it correctly to generate the experiment.",
                            "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            /***************************************************************
             *********************  EDUCATIONAL KEEL  **********************
             **************************************************************/
            }//if nodes not conected

            if (isFlowCorrect()) {
                /***************************************************************
                 *********************  EDUCATIONAL KEEL  **********************
                 **************************************************************/
                if (Frame.buttonPressed == 0) //Button Experiments pressed
                {
                    JFileChooser fc;
                    if (lastDirectory == null) {
                        fc = new JFileChooser();
                    } else {
                        fc = new JFileChooser(lastDirectory);
                    }
                    fc.setDialogTitle("Create experiment as ...");
                    String exten[] = {"zip"};
                    fc.setFileFilter(new ArchiveFilter2(exten, "Experiments (.zip)"));
                    opcion1 = fc.showSaveDialog(this);
                    if (opcion1 == JFileChooser.APPROVE_OPTION) //Eleccion de fichero(fileName) y aceptar. Valor == 0
                    {
                        lastDirectory = fc.getCurrentDirectory().getAbsolutePath();
                        comprimio = fc.getSelectedFile().getAbsolutePath();
                        nameExp = fc.getSelectedFile().getName();
                        if (!comprimio.toLowerCase().endsWith(".zip")) {
                            // Add correct extension
                            comprimio += ".zip";
                        } else {
                            nameExp = nameExp.substring(0, nameExp.length() - 4);
                        }
                        File tmp = new File(comprimio);
                        if (tmp.exists()) {
                            opcion2 = JOptionPane.showConfirmDialog(this, "File " + comprimio +
                                    " already exists. Do you want to replace it?",
                                    "Confirm", JOptionPane.YES_NO_OPTION, 3);
                            if (opcion2 == JOptionPane.YES_OPTION) //El archivo escogido existe y se acepta
                            {
                                // if file exists, we replace it
                                if (fc.getSelectedFile().exists()) {
                                    fc.getSelectedFile().delete();
                                }

                                if (new File("./" + nameExp).exists()) {
                                    FileUtils.rmdir("./" + nameExp);
                                }
                            }
                        }
                    }
                } //experiments
                /***************************************************************
                 *********************  EDUCATIONAL KEEL  **********************
                 **************************************************************/
                if ((opcion0 == 0) && (opcion1 == JFileChooser.APPROVE_OPTION) && (opcion2 == JOptionPane.YES_OPTION)) {
                    //below is executed if "Experiments button" is pressed or "Teaching button" is pressed
                    //Always is executed
                    String path = "./" + nameExp + "/";
                    FileUtils.mkdir(path);
                    FileUtils.mkdir(path.concat("datasets"));
                    FileUtils.mkdir(path.concat("exe"));
                    FileUtils.mkdir(path.concat("scripts"));
                    FileUtils.mkdir(path.concat("results"));

                    Vector sentencias = new Vector();
                    Vector tests = new Vector();

                    //Compute absolute names
                    absoluteNames();

                    //Review
                    /*System.out.println("***********ABSOLUTENAMES************");
                    for(int i = 0; i < experimentGraph.numNodes(); i++) {
                    System.out.println(fullName[i]);
                    }
                    System.out.println("***********ENDABSOLUTENAMES************");*/

                    // for each layer
                    int saveLayer = Layer.layerActivo;

                    for (int nl = 0; nl < Layer.numLayers; nl++) {
                        Layer.layerActivo = nl;
                        Vector visitados = new Vector();

                        // STEP 1: dataset directories
                        indexRoot = 0;
                        for (int i = 0; i < experimentGraph.numNodes(); i++) {
                            if (experimentGraph.getNodeAt(i).type == Node.type_Dataset) {
                                if (sueltos.contains(new Integer(i)) == false) {

                                    DataSet ds = (DataSet) experimentGraph.getNodeAt(i);
                                    createDatasetDirs(ds, path);
                                    visitados.addElement(new Integer(i));
                                    indexRoot = i;
                                }
                            }
                        }

                        // STEP 2: directories for algorithms
                        int i = -1;
                        while ((i = nextNode(visitados)) != -1) {
                            if (sueltos.contains(new Integer(i)) == false) {
                                if (experimentGraph.getNodeAt(i).type == Node.type_Algorithm) {
                                    Algorithm al = (Algorithm) experimentGraph.getNodeAt(i);
                                    dirAlgorithm(al, i, path, sentencias, indexRoot);
                                    /***************************************************************
                                     *********************  EDUCATIONAL KEEL  **********************
                                     **************************************************************/
                                    if (Frame.buttonPressed == 1) //Button Teaching pressed
                                    {
                                        partitionList.add(sentencias.size());
                                    }
                                /***************************************************************
                                 *********************  EDUCATIONAL KEEL  **********************
                                 **************************************************************/
                                } else if (experimentGraph.getNodeAt(i).type == Node.type_Test) {
                                    Test t = (Test) experimentGraph.getNodeAt(i);
                                    dirTest(t, i, path, sentencias);
                                    tests.addElement(t.dsc.getName());
                                } else if (experimentGraph.getNodeAt(i).type == Node.type_userMethod) {
                                    UserMethod mu = (UserMethod) experimentGraph.getNodeAt(i);
                                    dirUsermethod(mu, i, path, sentencias);
                                }
                            }
                        }
                    }
                    Layer.layerActivo = saveLayer;

                    // generating script
                    try {
                        Document doc = new Document(new Element("execution"));
                        Element root = doc.getRootElement();
                        Element job = new Element("job");
                        String tokens[];
                        int i, j;
                        for (i = 0; i < sentencias.size(); i++) {
                            Element comando = new Element("command");
                            Element opciones[] = new Element[10];
                            for (j = 0; j < 5; j++) {
                                opciones[j] = new Element("option");
                            }
                            Element exe = new Element("executableFile");
                            Element script = new Element("scriptFile");
                            /***************************************************************
                             *********************  EDUCATIONAL KEEL  **********************
                             **************************************************************/
                            if (Frame.buttonPressed == 0) //Button Experiments pressed
                            {
                                tokens = ((String) sentencias.elementAt(i)).split(" ");
                                comando.setText(tokens[0]);
                                for (j = 1; j < tokens.length - 2; j++) {
                                    opciones[j - 1].setText(tokens[j]);
                                }
                                exe.setText(tokens[j]);
                                script.setText(tokens[j + 1]);
                                Element sentencia = new Element("sentence");
                                sentencia.addContent(comando).addContent(
                                        opciones[0]).addContent(opciones[1]).addContent(opciones[2]).addContent(opciones[3]).addContent(opciones[4]).addContent(exe).addContent(script);
                                //Insert elements "sentence" in file RunKeel.xml
                                root.addContent(sentencia);
                            } else //Button Teaching pressed
                            {
                                Element tipoAlgoritmo = new Element("algorithmType");
                                Element semilla = new Element("seed"); //new line
                                tokens = ((String) sentencias.elementAt(i)).split(" ");
                                comando.setText(tokens[0]);

                                /*for(j=1;j<tokens.length;j++){
                                System.out.println("TOKENNNNNNNNNNNNNNNNNNNN->" + tokens[j]);
                                System.out.println("Tipo ALGORITMO -> " + tokens[j + 2]);
                                System.out.println("SEMILLA -> " + tokens[j + 3]);*/

                                for (j = 1; j < tokens.length - 4; j++) {
                                    opciones[j - 1].setText(tokens[j]);
                                }
                                exe.setText(tokens[j]);
                                script.setText(tokens[j + 1]);
                                tipoAlgoritmo.setText(tokens[j + 2]);
                                semilla.setText(tokens[j + 3]);
                                Element sentencia = new Element("sentence");

                                sentencia.addContent(comando).addContent(opciones[0]).addContent(opciones[1]).
                                        addContent(opciones[2]).addContent(opciones[3]).addContent(opciones[4]).
                                        addContent(exe).addContent(script).addContent(tipoAlgoritmo).addContent(semilla); 	//modified line
                                job.addContent(sentencia);
                                if (i == (int) partitionList.get(countJobs) - 1) {
                                    countJobs++;
                                    //Subelement JOB
                                    root.addContent(job);
                                    job = new Element("job");
                                }
                            }
                        /***************************************************************
                         *********************  EDUCATIONAL KEEL  **********************
                         **************************************************************/
                        }//for each sentence
                        //below is executed if "Experiments button" is pressed or "Teaching button" is pressed
                        //always is executed
                        File f = new File("./" + nameExp + "/scripts/RunKeel.xml");
                        FileOutputStream file = new FileOutputStream(f);
                        XMLOutputter fmt = new XMLOutputter();
                        fmt.setFormat(Format.getPrettyFormat());
                        fmt.output(doc, file);
                        file.close();
                    } catch (Exception exc) {
                        // Remove temporaly folder
                        FileUtils.rmdir("./" + nameExp);
                        exc.printStackTrace();
                    }                        // crate .JAR

                    /***************************************************************
                     *********************  EDUCATIONAL KEEL  **********************
                     **************************************************************/
                    if (Frame.buttonPressed == 0) //Button Experiments pressed
                    {
                        // LSR search runkeel graph into KEEL .jar
                        // FileUtils.copy("./config/RunKeel.jar",
                        // "./experiment/scripts/RunKeel.jar");
                        FileUtils.copy(this.getClass().getResource(
                                "/runkeel/runkeel.jar"), "./" + nameExp + "/scripts/RunKeel.jar");

                        // Compress folder into a .zip file
                        Vector ficheros = new Vector();
                        FileUtils.listDir("./" + nameExp, ficheros);
                        FileUtils.ZipFiles(comprimio, ficheros);

                        // Remove temporaly folder
                        FileUtils.rmdir("./" + nameExp);

                        // Info for executing script
                        JOptionPane.showMessageDialog(
                                this,
                                "Experiment created.\n\nUnzip generated file,\ngo to '" + nameExp + "/scripts/' directory\nand execute 'java -jar RunKeel.jar'",
                                "Experiment created",
                                JOptionPane.INFORMATION_MESSAGE);

                    } else //Button Teaching Pressed
                    {
                        ejd = new EducationalRun(this);
                        closedEducationalExecWindow = false;
                        ejd.setVisible(true);
                    }
                /***************************************************************
                 *********************  EDUCATIONAL KEEL  **********************
                 **************************************************************/
                } //if(opcion==0)
            } else {
                JOptionPane.showMessageDialog(this, "The flow chart is not correct!\n Verify or try another flow", "Alert", JOptionPane.ERROR_MESSAGE);
            }
        }//Comprobar
    }//generateExperimentDirectories

    /**
     * Generates the experiment
     */
    private void generateExperimentDirectoriesLQD() {
        String nameExp = "";
        String comprimio = "";
        int opcion1 = 0; //JFileChooser.APPROVE_OPTION
        int opcion2 = 0; //JOptionPane.YES_OPTION

        /***************************************************************
         *********************  EDUCATIONAL KEEL  **********************
         **************************************************************/
        if (Frame.buttonPressed == 1) //Button Teaching pressed
        {
            nameExp = "experiment";
        }
        /***************************************************************
         *********************  EDUCATIONAL KEEL  **********************
         **************************************************************/

        //List with number of partitons in each experiment
        List<Integer> partitionList = new ArrayList<Integer>();
        int countJobs = 0;
        Vector<Integer> indexRoot = new Vector<Integer>();

        if (checkLQD()) {
            // start random number generator
            rnd = new Random(experimentGraph.getSeed());

            // warning: some nodes are not conected
            Vector<Integer> sueltos = isolatedNodesLQD();
            if (sueltos.size() != 0) {
                JOptionPane.showMessageDialog(this, "Some nodes are not connected. You must to connect them",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            /*System.out.println("el numero de sueltos es "+sueltos.size());
            for(int n=0;n<sueltos.size();n++)
            {
            System.out.println("elimino el  "+sueltos.get(n));

            experimentGraph.dropNodeLQD_move(sueltos.get(n));
            }*/
            }



            JFileChooser fc;
            if (lastDirectory == null) {
                fc = new JFileChooser();
            } else {
                fc = new JFileChooser(lastDirectory);
            }

            question = true;
            fc.setDialogTitle("Create experiment as ...");
            String exten[] = {"zip"};
            fc.setFileFilter(new ArchiveFilter2(exten, "Experiments (.zip)"));
            opcion1 = fc.showSaveDialog(this);
            if (opcion1 == JFileChooser.APPROVE_OPTION) //Eleccion de fichero(fileName) y aceptar. Valor == 0
            {
                lastDirectory = fc.getCurrentDirectory().getAbsolutePath();
                comprimio = fc.getSelectedFile().getAbsolutePath();
                nameExp = fc.getSelectedFile().getName();
                if (!comprimio.toLowerCase().endsWith(".zip")) {
                    // Add correct extension
                    comprimio += ".zip";
                } else {
                    nameExp = nameExp.substring(0, nameExp.length() - 4);
                }
                File tmp = new File(comprimio);
                if (tmp.exists()) {
                    opcion2 = JOptionPane.showConfirmDialog(this, "File " + comprimio +
                            " already exists. Do you want to replace it?",
                            "Confirm", JOptionPane.YES_NO_OPTION, 3);
                    if (opcion2 == JOptionPane.YES_OPTION) //El archivo escogido existe y se acepta
                    {
                        // if file exists, we replace it
                        if (fc.getSelectedFile().exists()) {
                            fc.getSelectedFile().delete();
                        }

                        if (new File("./" + nameExp).exists()) {
                            FileUtils.rmdir("./" + nameExp);
                        }
                    }
                }
            }



            if ((opcion1 == JFileChooser.APPROVE_OPTION) && (opcion2 == JOptionPane.YES_OPTION)) {
                String path = "./" + nameExp + "/";
                FileUtils.mkdir(path);
                FileUtils.mkdir(path.concat("datasets"));
                FileUtils.mkdir(path.concat("exe"));
                FileUtils.mkdir(path.concat("scripts"));
                FileUtils.mkdir(path.concat("results"));

                Vector sentencias = new Vector();



                //Compute absolute names
                absoluteNamesLQD();



                //tenemso que recorres todos los nodos que estan conecteados
                //con un nodo dataset.
                //Dentro de cada nodo vemos sus datasets y apuntamos en vistado
                //este dataset que puede ser usado por otro

                // STEP 1: dataset directories
                String r_jar = "";
                Vector visitados = new Vector();
                for (int i = 0; i < experimentGraph.numNodes(); i++) {
                    if (experimentGraph.getNodeAt(i).type == Node.type_Dataset) {
                        for (int j = 0; j < experimentGraph.numNodes(); j++) {
                            if (experimentGraph.getNodeAt(j).type == Node.type_Algorithm) {
                                for (int a = 0; a < experimentGraph.getNodeAt(j).dsc.arg.size(); a++) {
                                    if (experimentGraph.getNodeAt(j).dsc.arg.get(a).before.id == experimentGraph.getNodeAt(i).id) {
                                        createDatasetDirsLQD(experimentGraph.getNodeAt(j).dsc.arg.get(a), path);
                                        visitados.addElement(new Integer(i));
                                        indexRoot.addElement(i);
                                    }
                                }
                            }
                        }
                    } else if (experimentGraph.getNodeAt(i).type == Node.type_Test ||
                            experimentGraph.getNodeAt(i).type == Node.type_Visor) {

                        Test t = (Test) experimentGraph.getNodeAt(i);
                        // Copy test executable
                        String origen1 = t.dsc.getPath() + t.dsc.getJarName();
                        String destino1 = path.concat("exe/" + t.dsc.getJarName());
                        r_jar = t.dsc.getJarName();
                        copy_jar(origen1, destino1);

                    }
                } //for 1 setp

                //insert the node result
               /* if(r_jar.compareTo("")==0 && summary==true)
                {
                String origen1 = dsc.getPath(0) + dsc.getJarName(0);
                String destino1 = path.concat("exe/results.jar");
                r_jar=dsc.getJarName();
                copy_jar(origen1, destino1);
                }*/

                // STEP 2: directories for algorithms
                int i = -1;
                while ((i = nextNodeLQD(visitados)) != -1) {
                    if (experimentGraph.getNodeAt(i).type == Node.type_Algorithm) {
                        Algorithm al = (Algorithm) experimentGraph.getNodeAt(i);
                        dirAlgorithmLQD(al, i, path, sentencias, r_jar);
                    }

                }



                // generating script

                try {
                    Document doc = new Document(new Element("execution"));
                    Element root = doc.getRootElement();
                    Element job = new Element("job");
                    String tokens[];
                    int j;
                    for (i = 0; i < sentencias.size(); i++) {
                        Element comando = new Element("command");
                        Element opciones[] = new Element[10];
                        for (j = 0; j < 5; j++) {
                            opciones[j] = new Element("option");
                        }
                        Element exe = new Element("executableFile");
                        Element script = new Element("scriptFile");

                        tokens = ((String) sentencias.elementAt(i)).split(" ");
                        comando.setText(tokens[0]);
                        for (j = 1; j < tokens.length - 2; j++) {
                            opciones[j - 1].setText(tokens[j]);
                        }
                        exe.setText(tokens[j]);
                        script.setText(tokens[j + 1]);
                        Element sentencia = new Element("sentence");
                        sentencia.addContent(comando).addContent(
                                opciones[0]).addContent(opciones[1]).addContent(opciones[2]).addContent(opciones[3]).addContent(opciones[4]).addContent(exe).addContent(script);
                        //Insert elements "sentence" in file RunKeel.xm
                        root.addContent(sentencia);

                    }

                    //for each sentence
                    //below is executed if "Experiments button" is pressed or "Teaching button" is pressed
                    //always is executed
                    File f = new File("./" + nameExp + "/scripts/RunKeel.xml");
                    FileOutputStream file = new FileOutputStream(f);
                    XMLOutputter fmt = new XMLOutputter();
                    fmt.setFormat(Format.getPrettyFormat());
                    fmt.output(doc, file);
                    file.close();
                } catch (Exception exc) {
                    // Remove temporaly folder
                    FileUtils.rmdir("./" + nameExp);
                    exc.printStackTrace();
                }  // crate .JAR



                // LSR search runkeel graph into KEEL .jar
                FileUtils.copy(this.getClass().getResource(
                        "/runkeel/runkeel.jar"), "./" + nameExp + "/scripts/RunKeel.jar");


                // Compress folder into a .zip file
                Vector ficheros = new Vector();
                FileUtils.listDir("./" + nameExp, ficheros);
                FileUtils.ZipFiles(comprimio, ficheros);

                // Remove temporaly folder
                FileUtils.rmdir("./" + nameExp);

                // Info for executing script
                JOptionPane.showMessageDialog(this, "Experiment created.\n\nUnzip generated file,\ngo to '" + nameExp + "/scripts/' directory\nand execute 'java -jar RunKeel.jar'",
                        "Experiment created", JOptionPane.INFORMATION_MESSAGE);


            } //if

        } //Check
    }//generateExperimentDirectories

    /***************************************************************
     *********************  EDUCATIONAL KEEL  **********************
     **************************************************************/
    public void closedEducationalExec(EducationalRunEvent event) {
        closedEducationalExecWindow = true;

    }

    /***************************************************************
     *********************  EDUCATIONAL KEEL  **********************
     **************************************************************/
    public int experimentType() {
        return this.expType;
    }

    /**
     * Forces the undo action
     */
    public void forceUndo() {
        vector_redo.insertElementAt(copyGraph(experimentGraph), 0);
        experimentGraph = copyGraph((Graph) vector_undo.elementAt(0));
        vector_undo.removeElementAt(0);
        redoButton.setEnabled(true);

        redoItem.setEnabled(true);
        if (vector_undo.size() == 0) {
            undoButton.setEnabled(false);
            undoItem.setEnabled(false);
        }
        experimentGraph.setModified(true);
        graphDiagramINNER.mainGraph = experimentGraph;
        graphDiagramINNER.repaint();
    }

    /***************************************************************
     *********************  EDUCATIONAL KEEL  **********************
     **************************************************************/
    public boolean getExecDocentWindowState() {
        return closedEducationalExecWindow;
    }

    /***************************************************************
     *********************  EDUCATIONAL KEEL  **********************
     **************************************************************/
    /***************************************************************
     *********************  EDUCATIONAL KEEL  **********************
     **************************************************************/
    public void deleteExecDocentWindow() {
        this.ejd.setVisible(false);
        this.ejd.windowClosing(null);
        this.ejd = null;
    }

    /***************************************************************
     *********************  EDUCATIONAL KEEL  **********************
     **************************************************************/
    //END OF OWN METHODS
    //*****************************************************************
    //*****************************************************************
    /**
     * Help panel
     * @param e Event
     */
    void panAyuda_actionPerformed(ActionEvent e) {
        // show / hide help panel
        if (helpPanelItem.isSelected()) {
            helpUseCaseTabbedPanel.setVisible(false);
            //helpSplit.setDividerLocation(this.getSize().height - 110);
            //showHelpButton.setIcon(showHelpPanelIcon);
            helpPanelItem.setSelected(false);
        } else {
            helpUseCaseTabbedPanel.setVisible(true);
            //helpSplit.setDividerLocation(this.getSize().height - heightHelpPanelSplit);
            //showHelpButton.setIcon(hideHelpPanelIcon);
            helpPanelItem.setSelected(true);
        }
    }

    /**
     * Data panel
     * @param e Event
     */
    void panData_actionPerformed(ActionEvent e) {
        // show / hide help panel
        if (datasetsItem.isSelected()) {
            mainSplitPane1.setDividerLocation(-1);
            datasetsItem.setSelected(true);
        } else {
            mainSplitPane1.setDividerLocation(mainToolBar1.getWidth());
            datasetsItem.setSelected(false);
        }
    }

    /**
     * Show status bar
     * @param e Event
     */
    void jCheckBoxMenuItem1_actionPerformed(ActionEvent e) {
        // show / hide status bar
        if (statusBarItem.isSelected()) {
            status.setVisible(true);
        } else {
            status.setVisible(false);
        }
    }

    /**
     * Show help panel
     * @param e Event
     */
    void jCheckBoxMenuItem4_actionPerformed(ActionEvent e) {
        // show / hide help panel
        if (statusBarItem.isSelected()) {
            datasetsChecksPanel.setVisible(true);
        //datasetsAlgorithmsSplit.setDividerLocation(280);
        //showAlgButton.setIcon(hideDatasetsAlgorithmPanelIcon);
        } else {
            datasetsChecksPanel.setVisible(false);
        //datasetsAlgorithmsSplit.setDividerLocation(52);
        //showAlgButton.setIcon(showDatasetsAlgorithmPanelIcon);
        }
    }

    /**
     * Show grid
     * @param e Event
     */
    void jCheckBoxMenuItem2_actionPerformed(ActionEvent e) {
        // show / hide grid
        graphDiagramINNER.paintGrid = gridItem.isSelected();
        graphDiagramINNER.repaint();
    }

    /**
     * Show help panel
     * @param e
     */
    void jCheckBoxMenuItem3_actionPerformed(ActionEvent e) {
        // show / hide help panel
        if (helpPanelItem.isSelected()) {
            helpUseCaseTabbedPanel.setVisible(true);
        //helpSplit.setDividerLocation(this.getSize().height - heightHelpPanelSplit);
        //showHelpButton.setIcon(hideHelpPanelIcon);
        } else {
            helpUseCaseTabbedPanel.setVisible(false);
        //helpSplit.setDividerLocation(this.getSize().height - 110);
        //showHelpButton.setIcon(showHelpPanelIcon);
        }
    }

    /**
     * This function loads a experiment mapped to a XML file, and
     * restores it to the GUI.
     */
    private void loadExperiment() {
        int i, j, k;
        int salvar = JOptionPane.YES_OPTION;
        if (experimentGraph.getModified()) {
            salvar = JOptionPane.showConfirmDialog(this,
                    "Save Modified Experiment?", "Save Changes",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (salvar == JOptionPane.YES_OPTION) {
                saveExperiment(0);
            }
        }

        if (salvar != JOptionPane.CANCEL_OPTION) {
            JFileChooser f;
            if (lastDirectory == null) {
                f = new JFileChooser();
            } else {
                f = new JFileChooser(lastDirectory);
            }
            f.setDialogTitle("Load experiment");
            String exten[] = {"xml"};
            f.setFileFilter(new ArchiveFilter2(exten, "Experiments (.xml)"));
            int opcion = f.showOpenDialog(this);
            if (opcion == JFileChooser.APPROVE_OPTION) {
                try {
                    lastDirectory = f.getCurrentDirectory().getAbsolutePath();
                    FileInputStream file = new FileInputStream(f.getSelectedFile().getAbsolutePath());

                    Mapping mapping = new Mapping();

                    if (objType == LQD) {
                        mapping.loadMapping(this.getClass().getResource("/mapping/mapeoExperimentoLQD.xml"));
                    } else {
                        mapping.loadMapping(this.getClass().getResource("/mapping/mapeoExperimento.xml"));
                    }
                    Unmarshaller unmar = new Unmarshaller(mapping);
                    Graph aux = (Graph) unmar.unmarshal(new InputSource(file));
                    if (aux.objective == this.objType) {
                        continueExperimentGeneration();
                        experimentGraph = restoreGraph(aux);
                        this.expType = experimentGraph.getType();
                        dinDatasets.removeAllData();
                        panelDatasets.removeAllData();
                        continueExperimentGeneration();
                        experimentGraph.setName(f.getSelectedFile().getAbsolutePath());
                        graphDiagramINNER.setToolTipText("Click twice into a node to view its properties");
                        //selectButton.setSelected(true);
                        status.setText("Click in a node to select it");
                        deleteItem.setEnabled(false);
                        setCursor(Cursor.getDefaultCursor());
                        cursorDraw = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
                        selectButton.setEnabled(true);

                        selectItem.setEnabled(true);

                        showAlgButton.setEnabled(true);
                        selecDatasets.setEnabled(true);
                        selectMethods.setEnabled(true);
                        selectPreprocessMethods.setEnabled(true);
                        saveButton.setEnabled(true);
                        selectVisualizeMethods.setEnabled(true);
                        if (objType != LQD) {
                            selectPostprocessMethods.setEnabled(true);
                            selectTestMethods.setEnabled(true);
                            selectVisualizeMethods.setEnabled(true);

                            showHelpButton.setEnabled(true);
                            newButton.setEnabled(true);
                            openButton.setEnabled(true);
                            snapshotButton.setEnabled(true);
                        } else {
                            selectPostprocessMethods.setEnabled(false);
                            selectTestMethods.setEnabled(false);
                        }
                        /* System.out.println("antes de llama a la repaint");
                        for (i = 0; i < experimentGraph.numNodes(); i++) {
                        System.out.println("EL NODO ES "+experimentGraph.getNodeAt(i).dsc.getName()+ " el tipo es "+experimentGraph.getNodeAt(i).type_lqd);
                        }*/
                        graphDiagramINNER.repaint();
                        vector_undo.clear();
                        vector_redo.clear();
                        undoButton.setEnabled(false);
                        undoItem.setEnabled(false);
                        redoButton.setEnabled(false);
                        redoItem.setEnabled(false);
                        selectButton.setEnabled(true);
                        runButton.setEnabled(true);
                        cursorFlux.setEnabled(true);

                        if (objType == IMBALANCED) {
                            selectPostprocessMethods.setEnabled(false);
                        }
                        if (objType == MULTIINSTANCE) {
                            selectPostprocessMethods.setVisible(false);
                            selectPreprocessMethods.setVisible(false);
                        }
                        if (objType != LQD) {
                            reload_algorithms();
                            saveAsExpItem.setEnabled(true);
                            saveExpItem.setEnabled(true);
                            activateMenuItems();
                        }
                        notSelectedDataset = false;
                        ExternalObjectDescription dsc = experimentGraph.getExternalObjectDescription();

                        //resize the exteneral object descriptions and the number of
                        //data sets
                        setNumDatasets(dsc.getAllNames());
                        //the layer used is always zero
                        Layer.layerActivo = 0;
                        cursorAction = GraphPanel.SELECTING;

                        /*Code that updates the panel of data set selection*/
                        for (i = 0; i < experimentGraph.numNodes(); i++) {
                            if (experimentGraph.getNodeAt(i).getType() == Node.type_Dataset) {
                                if (objType != LQD) {
                                    ExternalObjectDescription dsctmp = experimentGraph.getNodeAt(i).dsc;

                                    int c = 0;
                                    for (j = 0; j < dinDatasets.checks.size(); j++) {
                                        ExternalObjectDescription dataTemp = (ExternalObjectDescription) (dinDatasets.actualList.elementAt(j));
                                        for (k = 0; k < Layer.numLayers; k++) {
                                            if (dsctmp.getName(k).compareToIgnoreCase(dataTemp.getName(0)) == 0) {
                                                ((JButton) (dinDatasets.checks.elementAt(j))).setText("Del");
                                                ((JButton) (dinDatasets.edits.elementAt(j))).setVisible(true);
                                                dinDatasets.add((JButton) (dinDatasets.edits.elementAt(j)));
                                                c++;
                                            }
                                        }
                                    }
                                    if (c == 1) {
                                        for (j = 0; j < dinDatasets.checks.size(); j++) {
                                            if (((JButton) dinDatasets.checks.elementAt(j)).getText() == "Del") {
                                                ((JButton) dinDatasets.checks.elementAt(j)).setEnabled(false);
                                            }
                                        }
                                    }
                                    experimentGraph.setModified(true);
                                } else // is LQD
                                {
                                    ExternalObjectDescription dsctmp = experimentGraph.getNodeAt(i).dsc;
                                    if (experimentGraph.getNodeAt(i).getTypelqd() == Node.LQD) {
                                        load_data(dsctmp, dinDatasets.checks, dinDatasets.actualList);
                                    } else if (experimentGraph.getNodeAt(i).getTypelqd() == Node.LQD_C) {
                                        load_data(dsctmp, dinDatasets.checksLQD_C, dinDatasets.actualListLQD_C);
                                    } else if (experimentGraph.getNodeAt(i).getTypelqd() == Node.C_LQD) {
                                        load_data(dsctmp, dinDatasets.checksC_LQD, dinDatasets.actualListC_LQD);
                                    } else if (experimentGraph.getNodeAt(i).getTypelqd() == Node.CRISP2) {
                                        load_data(dsctmp, dinDatasets.checksC, dinDatasets.actualListC);
                                    }
                                }
                            }
                        }

                        //now we show the dinamic data sets panel
                        //show the dinamic data set panel
                        ((CardLayout) selectionPanel1.getLayout()).show(selectionPanel1, "dinDatasetsCard");
                        dinDatasetsScrollPane.setVisible(true);
                        status.setText("Experiment loaded successfully");
                    } else {
                        if (objType == INVESTIGATION) {
                            JOptionPane.showMessageDialog(this,
                                    "The experiment loaded doesn't correspond with this option (Experiments)", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        } else if (objType == LQD) {
                            JOptionPane.showMessageDialog(this,
                                    "The experiment loaded doesn't correspond with this option (Experiments LQD)", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        } else if (objType == TEACHING) {
                            JOptionPane.showMessageDialog(this,
                                    "The experiment loaded doesn't correspond with this option (Educational)", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        } else if (objType == IMBALANCED) {
                            JOptionPane.showMessageDialog(this,
                                    "The experiment loaded doesn't correspond with this option (Imbalanced)", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        } else if (objType == MULTIINSTANCE) {
                            JOptionPane.showMessageDialog(this,
                                    "The experiment loaded doesn't correspond with this option (Multi Instance)", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        } else if (objType == SUBGROUPDISCOVERY) {
                            JOptionPane.showMessageDialog(this,
                                    "The experiment loaded doesn't correspond with this option (Subgroup Discovery)", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception ex) {
                    //volver a nuevo experimento
                    newExperimentNoPrompt();
                    JOptionPane.showMessageDialog(this,
                            "Error loading experiment", "Error",
                            JOptionPane.ERROR_MESSAGE);
                //ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Load dat sets
     * @param dsctmp New dsc
     * @param check Check list
     * @param List List of data sets
     */
    public void load_data(ExternalObjectDescription dsctmp, Vector check, Vector List) {
        int c = 0;
        for (int j = 0; j < check.size(); j++) {
            ExternalObjectDescription dataTemp = (ExternalObjectDescription) (List.elementAt(j));
            for (int k = 0; k < dsctmp.getNamesLength(); k++) {
                if (dsctmp.getName(k).compareToIgnoreCase(dataTemp.getName(0)) == 0) {
                    ((JButton) (check.elementAt(j))).setText("Del");
                    c++;
                }
            }
        }
        if (c == 1) {
            for (int j = 0; j < check.size(); j++) {
                if (((JButton) check.elementAt(j)).getText() == "Del") {
                    ((JButton) check.elementAt(j)).setEnabled(false);
                }
            }
        }


    }

    /**
     * This function creates a new experiment.
     * If the user wants to, the previous experiment is saved.
     */
    private int newExperiment() {


        int salvar = JOptionPane.YES_OPTION;


        if (initialPanel1.isVisible()) {
            return salvar;
        }


        /***************************************************************
         *********************  EDUCATIONAL KEEL  **********************
         **************************************************************/
        if (Frame.buttonPressed == 0) //Button Experiments pressed
        {
            if (objType == LQD) {

                salvar = JOptionPane.showConfirmDialog(this,
                        "Save Modified Experiment?", "Save Changes",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                if (salvar == JOptionPane.YES_OPTION) {
                    saveExperiment(0);
                }
            } else {
                if (experimentGraph.getModified()) {
                    salvar = JOptionPane.showConfirmDialog(this,
                            "Save Modified Experiment?", "Save Changes",
                            JOptionPane.YES_NO_CANCEL_OPTION);
                    if (salvar == JOptionPane.YES_OPTION) {
                        saveExperiment(0);
                    }
                }
            }
        } else //Button Teaching pressed
        {
            if (experimentGraph.getModified()) {
                salvar = JOptionPane.showConfirmDialog(this,
                        "Save Modified Experiment?", "Save Changes",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                if (salvar == JOptionPane.YES_OPTION) {
                    //window of partitons is opened
                    if (getExecDocentWindowState() == false) {

                        deleteExecDocentWindow();
                        closedEducationalExec(null);
                    }
                    saveExperiment(0);
                } else if (salvar == JOptionPane.NO_OPTION) {
                    //window of partitons is opened
                    if (getExecDocentWindowState() == false) {
                        deleteExecDocentWindow();
                        closedEducationalExec(null);
                    }
                }
            }
        }
        /***************************************************************
         *********************  EDUCATIONAL KEEL  **********************
         **************************************************************/
        if (salvar != JOptionPane.CANCEL_OPTION) {
            this.selectButton.setEnabled(false);

            this.cursorFlux.setEnabled(false);
            this.runButton.setEnabled(false);
            saveButton.setEnabled(false);
            runExpItem.setEnabled(false);
            insertDataflowItem.setEnabled(false);
            selectItem.setEnabled(false);

            this.setCursor(Cursor.getDefaultCursor());
            this.cursorAction = 0;
            experimentGraph = new Graph();
            graphDiagramINNER.mainGraph = experimentGraph;
            graphDiagramINNER.repaint();
            vector_undo.clear();
            vector_redo.clear();

            undoButton.setEnabled(false);
            undoItem.setEnabled(false);
            redoButton.setEnabled(false);
            redoItem.setEnabled(false);
            deleteItem.setEnabled(false);
            reload_algorithms();

            notSelectedDataset = true;
            Layer.layerActivo = 0;
            status.setText("Click on the draw area to insert a new node");
            cursorDraw = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
            cursorAction = GraphPanel.PAINT_DATASET;
            graphDiagramINNER.setToolTipText("Click on the draw area to insert a dataset node");

            /***************************************************************
             *********************  EDUCATIONAL KEEL  **********************
             **************************************************************/
            if (Frame.buttonPressed == 0) {
                this.helpContent.muestraURL(this.getClass().getResource("/contextualHelp/exp_intro.html"));
            } else {
                this.helpContent.muestraURL(this.getClass().getResource("/contextualHelpDocente/exp_intro.html"));
            }
            /***************************************************************
             *********************  EDUCATIONAL KEEL  **********************
             **************************************************************/
            graphDiagramINNER.setBackground(Color.gray);
            cursorFlux.setEnabled(false);
            selecDatasets.setEnabled(false);
            selectPreprocessMethods.setEnabled(false);
            selectMethods.setEnabled(false);
            selectPostprocessMethods.setEnabled(false);
            selectTestMethods.setEnabled(false);
            selectVisualizeMethods.setEnabled(false);
            panelDatasets.removeAllData();
            dinDatasets.removeAllData();
            panelDatasets.deselectAll();
            panelDatasets.repaint();
            ((CardLayout) selectionPanel1.getLayout()).show(selectionPanel1, "initialCard");
            mainSplitPane1.setDividerLocation(-1);
            datasetsItem.setSelected(true);

            if (objType == IMBALANCED) {
                seedItem.setEnabled(false);
                loadImbalancedExperiment();
                //now, we load the datasets and the different methods
                dinDatasets.removeAllData();
                panelDatasets.removeAllData();
                continueExperimentGeneration();
                ((CardLayout) selectionPanel1.getLayout()).show(selectionPanel1, "datasetsChecksPanel");
                deleteItem.setEnabled(false);
            }

            if (objType == MULTIINSTANCE) {
                seedItem.setEnabled(false);
                loadMultiInstanceExperiment();
                //now, we load the datasets and the different methods
                dinDatasets.removeAllData();
                panelDatasets.removeAllData();
                continueExperimentGeneration();
                ((CardLayout) selectionPanel1.getLayout()).show(selectionPanel1, "datasetsChecksPanel");
                deleteItem.setEnabled(false);
            }
        }
        return salvar;
    }

    /**
     * This function creates a new experiment.
     * If the user wants to, the previous experiment is saved.
     */
    private void newExperimentNoPrompt() {

        this.selectButton.setEnabled(false);

        this.cursorFlux.setEnabled(false);
        this.runButton.setEnabled(false);
        runExpItem.setEnabled(false);
        insertDataflowItem.setEnabled(false);
        selectItem.setEnabled(false);

        this.setCursor(Cursor.getDefaultCursor());
        this.cursorAction = 0;
        experimentGraph = new Graph();
        graphDiagramINNER.mainGraph = experimentGraph;
        graphDiagramINNER.repaint();
        vector_undo.clear();
        vector_redo.clear();

        undoButton.setEnabled(false);
        undoItem.setEnabled(false);
        redoButton.setEnabled(false);
        redoItem.setEnabled(false);
        deleteItem.setEnabled(false);
        reload_algorithms();

        notSelectedDataset = true;
        Layer.layerActivo = 0;
        status.setText("Click on the draw area to insert a new node");
        cursorDraw = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        cursorAction = GraphPanel.PAINT_DATASET;
        graphDiagramINNER.setToolTipText("Click on the draw area to insert a dataset node");

        /***************************************************************
         *********************  EDUCATIONAL KEEL  **********************
         **************************************************************/
        if (Frame.buttonPressed == 0) {
            this.helpContent.muestraURL(this.getClass().getResource("/contextualHelp/exp_intro.html"));
        } else {
            this.helpContent.muestraURL(this.getClass().getResource("/contextualHelpDocente/exp_intro.html"));
        }
        /***************************************************************
         *********************  EDUCATIONAL KEEL  **********************
         **************************************************************/
        graphDiagramINNER.setBackground(Color.gray);
        cursorFlux.setEnabled(false);
        selecDatasets.setEnabled(false);
        selectPreprocessMethods.setEnabled(false);
        selectMethods.setEnabled(false);
        selectPostprocessMethods.setEnabled(false);
        selectTestMethods.setEnabled(false);
        selectVisualizeMethods.setEnabled(false);
        panelDatasets.removeAllData();
        dinDatasets.removeAllData();
        panelDatasets.deselectAll();
        panelDatasets.repaint();
        ((CardLayout) selectionPanel1.getLayout()).show(selectionPanel1, "initialCard");


    }

    /**
     * Add a new arc
     * @param e Event
     */
    void flujo_actionPerformed(ActionEvent e) {
        // Add connections

        graphDiagramINNER.setToolTipText("Drag the mouse to draw a data flow between two nodes");
        methodsSelectionTree.setSelectionPath(null);
        preprocessTree.setSelectionPath(null);
        postprocessSelectionTree.setSelectionPath(null);
        testSelectionTree.setSelectionPath(null);
        visualizeSelectionTree.setSelectionPath(null);
        //cursorFlux.setSelected(true);
        cursorAction = GraphPanel.PAINT_ARC;
        status.setText("Click in a node and drag to draw dataflow");
        cursorDraw = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
        showAlgButton.setEnabled(false);

    }

    /**
     * Select an element
     * @param e Event
     */
    void seleccionar_actionPerformed(ActionEvent e) {
        graphDiagramINNER.setToolTipText("Click twice into a node to view its properties");
        methodsSelectionTree.setSelectionPath(null);
        preprocessTree.setSelectionPath(null);
        postprocessSelectionTree.setSelectionPath(null);
        testSelectionTree.setSelectionPath(null);
        visualizeSelectionTree.setSelectionPath(null);
        //selectButton.setSelected(true);
        //cursorFlux.setSelected(false);
        cursorAction = GraphPanel.SELECTING;
        status.setText("Click in a node to select it");
        cursorDraw = Cursor.getDefaultCursor();
    }

    /**
     * Select an user method
     * @param e Event
     */
    void usuario_actionPerformed(ActionEvent e) {
        graphDiagramINNER.setToolTipText("Click to put an user?s method node");
        methodsSelectionTree.setSelectionPath(null);
        preprocessTree.setSelectionPath(null);
        postprocessSelectionTree.setSelectionPath(null);
        testSelectionTree.setSelectionPath(null);
        visualizeSelectionTree.setSelectionPath(null);

        cursorAction = GraphPanel.PAINT_USER;
        dsc = new ExternalObjectDescription("", "", 0);

        dsc.setSubtype(Node.type_Undefined);
        status.setText("Click on the draw area to insert a new node");
        cursorDraw = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    }

    /**
     * Inser something in undo queue
     */
    void insertUndo() {
        if (vector_undo.size() == 10) {
            vector_undo.removeElementAt(9);
        }
        vector_undo.insertElementAt(copyGraph(experimentGraph), 0);
        undoButton.setEnabled(true);
        undoItem.setEnabled(true);
    }

    /**
     * Deletes a selected element in the graph
     */
    private void deletelqd() {
        if (!graphDiagramINNER.multipleSelection) {
            if (graphDiagramINNER.elementSelected) {
                // remove connection
                if (graphDiagramINNER.typeSelected == 1) {
                    if (JOptionPane.showConfirmDialog(this, "Do you want to remove this arc and all the arc contained in the way?",
                            "Remove node", JOptionPane.YES_NO_OPTION, 3) == JOptionPane.YES_OPTION) {
                        experimentGraph.dropArcLQD(graphDiagramINNER.arc_selected);
                    }
                // remove node
                }
                if (graphDiagramINNER.typeSelected == 0) // is a node
                {
                    if (experimentGraph.getNodeAt(graphDiagramINNER.node_selected).dsc.getName(0).compareTo("Results") == 0) {
                        JOptionPane.showMessageDialog(this, "This node can not be erased", "Error", 2);
                    } else {
                        if (JOptionPane.showConfirmDialog(this, "Do you want to remove this node and all its way?",
                                "Remove node", JOptionPane.YES_NO_OPTION, 3) == JOptionPane.YES_OPTION) {

                            boolean found = false;
                            for (int i = experimentGraph.numArcs() - 1; i >= 0; i--) {
                                Arc a = experimentGraph.getArcAt(i);
                                int nodes = experimentGraph.numNodes();
                                if (a.getDestination() == (experimentGraph.getNodeAt(graphDiagramINNER.node_selected).id)) {
                                    found = true;
                                    experimentGraph.dropArcLQD(i);
                                    if (nodes == experimentGraph.numNodes()) {
                                        i = experimentGraph.numArcs();
                                    } else {
                                        break;
                                    }
                                }


                            }
                            if (found == false) {
                                experimentGraph.dropNodeLQD_move(graphDiagramINNER.node_selected);
                            }
                        }
                    }
                }

                graphDiagramINNER.elementSelected = false;
                deleteItem.setEnabled(false);
                graphDiagramINNER.repaint();

            }

            // remove tree selection
            methodsSelectionTree.setSelectionPath(null);
            preprocessTree.setSelectionPath(null);
            postprocessSelectionTree.setSelectionPath(null);
            testSelectionTree.setSelectionPath(null);
            visualizeSelectionTree.setSelectionPath(null);
        } else {
            JOptionPane.showMessageDialog(this, "Several elements are selected, select the element that you want to remove",
                    "Select only one element", JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * Deletes a selected element in the graph
     */
    private void delete() {
        insertUndo();
        if (!graphDiagramINNER.multipleSelection) {
            if (graphDiagramINNER.elementSelected) {
                // remove connection
                if (graphDiagramINNER.typeSelected == 1) {
                    experimentGraph.dropArc(experimentGraph.numArcs() - 1);
                // remove node
                }
                if (graphDiagramINNER.typeSelected == 0) {
                    for (int i = experimentGraph.numArcs() - 1; i >= 0; i--) {
                        Arc a = experimentGraph.getArcAt(i);
                        if (a.getSource() == (experimentGraph.numNodes() - 1)) {
                            experimentGraph.dropArc(i);
                        } else if (a.getDestination() == (experimentGraph.numNodes() - 1)) {
                            experimentGraph.dropArc(i);
                        }
                    }
                    experimentGraph.dropNode(experimentGraph.numNodes() - 1);
                }
                graphDiagramINNER.elementSelected = false;
                deleteItem.setEnabled(false);

                // restore dataset
                experimentGraph.restoreDataSet();

                graphDiagramINNER.repaint();
            }
        } else { // multiple selection
            for (int j = 0; j < graphDiagramINNER.selectedN.size(); j++) {
                int el = ((Integer) (graphDiagramINNER.selectedN.elementAt(j))).intValue();
                Node n = experimentGraph.getNodeAt(el - j);
                experimentGraph.dropNode(el - j);
                experimentGraph.insertNode(n);
                for (int k = 0; k < experimentGraph.numArcs(); k++) {
                    Arc a = experimentGraph.getArcAt(k);
                    int index_origen = a.getSource();
                    int index_destino = a.getDestination();
                    if (index_origen == el - j) {
                        a.setSource(experimentGraph.numNodes() - 1);
                    } else if (index_origen > el - j) {
                        a.setSource(index_origen - 1);
                    }
                    if (index_destino == el - j) {
                        a.setDestination(experimentGraph.numNodes() - 1);
                    } else if (index_destino > el - j) {
                        a.setDestination(index_destino - 1);
                    }
                }

                for (int i = experimentGraph.numArcs() - 1; i >= 0; i--) {
                    Arc a = experimentGraph.getArcAt(i);
                    if (a.getSource() == (experimentGraph.numNodes() - 1)) {
                        experimentGraph.dropArc(i);
                    } else if (a.getDestination() == (experimentGraph.numNodes() - 1)) {
                        experimentGraph.dropArc(i);
                    }
                }
                experimentGraph.dropNode(experimentGraph.numNodes() - 1);
            }
            graphDiagramINNER.multipleSelection = false;
            deleteItem.setEnabled(false);

            // restore dataset
            experimentGraph.restoreDataSet();

            graphDiagramINNER.repaint();
        }

        // remove tree selection
        methodsSelectionTree.setSelectionPath(null);
        preprocessTree.setSelectionPath(null);
        postprocessSelectionTree.setSelectionPath(null);
        testSelectionTree.setSelectionPath(null);
        visualizeSelectionTree.setSelectionPath(null);
    }

    /**
     * Initialize help panel
     */
    private void startHelpPanel() {
        /***************************************************************
         ***************  EDUCATIONAL KEEL  ****************************
         **************************************************************/
        if (Frame.buttonPressed == 0) {
            this.helpContent.muestraURL(this.getClass().getResource("/contextualHelp/exp_intro.html"));
        } else {
            this.helpContent.muestraURL(this.getClass().getResource("/contextualHelpDocente/exp_intro.html"));
        }
    /***************************************************************
     ***************  EDUCATIONAL KEEL  ****************************
     **************************************************************/
    }

    /**
     * Continues the experiment generation, once the used has selected 
     * the initial data sets and clicked on the panel, by doing the following tasks:
     * - Create the data set node
     * - Loads all the trees with the methods
     * - Sets visible the dinamicDataset panel
     */
    public void continueExperimentGeneration() {

        listAlgor = new AlgorithmXML[1000];
        nListAlgor = 0;

        statusBarItem.setEnabled(true);
        showAlgButton.setEnabled(false);
        runButton.setEnabled(false);
        graphDiagramINNER.setBackground(Color.white);
        cursorFlux.setEnabled(false);
        selecDatasets.setEnabled(false);
        //selecDatasets.setSelected(true);
        selectPreprocessMethods.setEnabled(false);
        selectMethods.setEnabled(false);
        selectPostprocessMethods.setEnabled(false);
        selectTestMethods.setEnabled(false);
        selectVisualizeMethods.setEnabled(false);
        if (objType == LQD) {
            createDatasetNodes("." + File.separatorChar + "data" + File.separatorChar + "DatasetsLQD.xml");
        } else {
            if (objType == IMBALANCED) {
                createDatasetNodes("." + File.separatorChar + "data" + File.separatorChar + "DatasetsImbalanced.xml");
            } else {
                if (objType == MULTIINSTANCE) {
                    createDatasetNodes("." + File.separatorChar + "data" + File.separatorChar + "DatasetsMultiInstance.xml");
                } else {
                    createDatasetNodes("." + File.separatorChar + "data" + File.separatorChar + "Datasets.xml");
                }
            }

        }

        status.setText("Select an initial set of dataset and then click on the drawing panel");
        cursorDraw = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        cursorAction = GraphPanel.PAINT_DATASET;
        graphDiagramINNER.setToolTipText("Select an initial set of dataset and then click on the drawing panel");


        /***************************************************************
         ***************  EDUCATIONAL KEEL  ****************************
         **************************************************************/
        if (Frame.buttonPressed == 0) //Button Experiments pressed
        {
            if (objType == LQD) {
                top4 = new DefaultMutableTreeNode(new ExternalObjectDescription("Algorithms the preprocessing", null, 0));
                RamaLqd = 1;
                createAlgorithmNodes(top4, "." + File.separatorChar + "algorithm" + File.separatorChar + "LQD" + File.separatorChar + "PreProcessLQD.xml");
            // RamaLqd=0;
            //createAlgorithmNodes(top4, "." + File.separatorChar + "algorithm" + File.separatorChar + "PreProcess.xml");
            } else {
                if (objType == IMBALANCED) {
                    top4 = new DefaultMutableTreeNode(new ExternalObjectDescription("Algorithms", null, 0));
                    createAlgorithmNodes(top4, "." + File.separatorChar + "algorithm" + File.separatorChar + "PreProcessImbalanced.xml");
                } else {
                    top4 = new DefaultMutableTreeNode(new ExternalObjectDescription("Algorithms", null, 0));
                    createAlgorithmNodes(top4, "." + File.separatorChar + "algorithm" + File.separatorChar + "PreProcess.xml");
                }
            }
        } else //Button Teaching pressed
        {
            top4 = new DefaultMutableTreeNode(new ExternalObjectDescription("Algorithms", null, 0));
            createAlgorithmNodes(top4, "." + File.separatorChar + "algorithm" + File.separatorChar + "EducationalPreProcess.xml");
        }
        /***************************************************************
         ***************  EDUCATIONAL KEEL  ****************************
         **************************************************************/
        preprocessTree = new JTree(top4);
        preprocessTree.setFont(new java.awt.Font("Arial", 0, 11));
        preprocessTree.setBackground(this.getBackground());
        if (top4.getDepth() > 1) {
            DefaultTreeSelectionModel sm4 = new DefaultTreeSelectionModel();
            sm4.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            preprocessTree.setSelectionModel(sm4);

            KeelTreeCellRenderer renderer4 = new KeelTreeCellRenderer();
            renderer4.setLeafIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/algoPR.gif")));
            preprocessTree.setCellRenderer(renderer4);

            //since we have created the Tree again, we need to bind the event listener as well
            preprocessTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {

                public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                    preprocessTree_valueChanged(evt);
                }
            });
        }
        preprocessScroll.setViewportView(preprocessTree);


        //methods Tree generation
        top2 = new DefaultMutableTreeNode(new ExternalObjectDescription("Algorithms", null, 0));



        /***************************************************************
         ***************  EDUCATIONAL KEEL  ****************************
         **************************************************************/
        if (Frame.buttonPressed == 0) //Button Experiments pressed
        {
            if (objType == LQD) {
                top2 = new DefaultMutableTreeNode(new ExternalObjectDescription("Algorithms of classification", null, 0));
                RamaLqd = 1;
                createAlgorithmNodes(top2, "." + File.separatorChar + "algorithm" + File.separatorChar + "LQD" + File.separatorChar + "MethodsLQD.xml");
            // RamaLqd=0;
            // createAlgorithmNodes(top2, "." + File.separatorChar + "algorithm" + File.separatorChar + "Methods.xml");
            } else {
                if (objType == SUBGROUPDISCOVERY) {
                    createAlgorithmNodes(top2, "." + File.separatorChar + "algorithm" + File.separatorChar + "SubgroupDiscovery.xml");
                } else {
                    if (objType == IMBALANCED) {
                        createAlgorithmNodes(top2, "." + File.separatorChar + "algorithm" + File.separatorChar + "MethodsImbalanced.xml");
                    } else {
                        if (objType == MULTIINSTANCE) {
                            createAlgorithmNodes(top2, "." + File.separatorChar + "algorithm" + File.separatorChar + "MethodsMultiInstance.xml");

                        } else {
                            createAlgorithmNodes(top2, "." + File.separatorChar + "algorithm" + File.separatorChar + "Methods.xml");
                        }
                    }

                }
            }


        } else //Button Teaching pressed
        {
            createAlgorithmNodes(top2, "." + File.separatorChar + "algorithm" + File.separatorChar + "EducationalMethods.xml");
        }

        /***************************************************************
         ***************  EDUCATIONAL KEEL  ****************************
         **************************************************************/
        methodsSelectionTree = new JTree(top2);
        methodsSelectionTree.setFont(new java.awt.Font("Arial", 0, 11));
        methodsSelectionTree.setBackground(this.getBackground());
        if (top2.getDepth() > 1) {
            DefaultTreeSelectionModel sm2 = new DefaultTreeSelectionModel();
            sm2.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            methodsSelectionTree.setSelectionModel(sm2);
            KeelTreeCellRenderer renderer2 = new KeelTreeCellRenderer();
            renderer2.setLeafIcon(new ImageIcon(this.getClass().getResource(
                    "/keel/GraphInterKeel/resources/ico/experiments/algo.gif")));
            methodsSelectionTree.setCellRenderer(renderer2);

            //since we have created the Tree again, we need to bind the event listener as well
            methodsSelectionTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {

                public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                    methodSelectionTree_valueChanged(evt);
                }
            });
        }
        methodsScrollPanel.setViewportView(methodsSelectionTree);


        // LSR - internal algorithm's list
      /*  if(objType==LQD)
        {         
        top5 = new DefaultMutableTreeNode(new ExternalObjectDescription("Algorithms the postprocessing", null, 0));
        RamaLqd=1;
        createAlgorithmNodes(top5, "." + File.separatorChar + "algorithm" + File.separatorChar + "PostProcess.xml");
        RamaLqd=0;
        createAlgorithmNodes(top5, "." + File.separatorChar + "algorithm" + File.separatorChar + "PostProcess.xml");
        }

        else
        {*/
        top5 = new DefaultMutableTreeNode(new ExternalObjectDescription("Algorithms", null, 0));
        createAlgorithmNodes(top5, "." + File.separatorChar + "algorithm" + File.separatorChar + "PostProcess.xml");
        //}


        postprocessSelectionTree = new JTree(top5);
        postprocessSelectionTree.setFont(new java.awt.Font("Arial", 0, 11));
        postprocessSelectionTree.setBackground(this.getBackground());
        if (top5.getDepth() > 1) {
            DefaultTreeSelectionModel sm5 = new DefaultTreeSelectionModel();
            sm5.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            postprocessSelectionTree.setSelectionModel(sm5);

            KeelTreeCellRenderer renderer5 = new KeelTreeCellRenderer();
            renderer5.setLeafIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/algoPS.gif")));
            postprocessSelectionTree.setCellRenderer(renderer5);

            //since we have created the Tree again, we need to bind the event listener as well
            postprocessSelectionTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {

                public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                    postprocessSelectionTree_valueChanged(evt);
                }
            });
        }
        postprocessScroll.setViewportView(postprocessSelectionTree);

        // Tests tree generation
        top6 = new DefaultMutableTreeNode(new ExternalObjectDescription("Algorithms", null, 0));
        if (objType == IMBALANCED) {
            createTestAlgorithmNodes(top6, "." + File.separatorChar + "algorithm" + File.separatorChar + "TestsImbalanced.xml");
        } else {
            createTestAlgorithmNodes(top6, "." + File.separatorChar + "algorithm" + File.separatorChar + "Tests.xml");
        }
        testSelectionTree = new JTree(top6);
        testSelectionTree.setFont(new java.awt.Font("Arial", 0, 11));
        testSelectionTree.setBackground(this.getBackground());
        testScroll.getViewport().setBackground(this.getBackground());

        if (top6.getDepth() > 1) {
            DefaultTreeSelectionModel sm6 = new DefaultTreeSelectionModel();
            sm6.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            testSelectionTree.setSelectionModel(sm6);

            testScroll.setAlignmentY((float) 0.5);
            KeelTreeCellRenderer renderer6 = new KeelTreeCellRenderer();
            renderer6.setLeafIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/algoTS.gif")));
            testSelectionTree.setCellRenderer(renderer6);

            //since we have created the Tree again, we need to bind the event listener as well
            testSelectionTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {

                public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                    testSelection_valueChanged(evt);
                }
            });
        }
        testScroll.setViewportView(testSelectionTree);

        // Tests tree generation
        top7 = new DefaultMutableTreeNode(new ExternalObjectDescription("Algorithms", null, 0));
        if (objType == LQD) {
            top7 = new DefaultMutableTreeNode(new ExternalObjectDescription("Results of the algorithms", null, 0));
            createTestAlgorithmNodes(top7, "." + File.separatorChar + "algorithm" + File.separatorChar + "LQD" + File.separatorChar + "ResultsLQD.xml");
        } else {
            if (objType == IMBALANCED) {
                createTestAlgorithmNodes(top7, "." + File.separatorChar + "algorithm" + File.separatorChar + "VisualizeImbalanced.xml");
            } else {
                createTestAlgorithmNodes(top7, "." + File.separatorChar + "algorithm" + File.separatorChar + "Visualize.xml");
            }
        }
        visualizeSelectionTree = new JTree(top7);
        visualizeSelectionTree.setFont(new java.awt.Font("Arial", 0, 11));
        visualizeSelectionTree.setBackground(this.getBackground());
        if (top7.getDepth() > 1) {
            DefaultTreeSelectionModel sm7 = new DefaultTreeSelectionModel();
            sm7.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            visualizeSelectionTree.setSelectionModel(sm7);

            visualizeScroll.setAlignmentY((float) 0.5);
            KeelTreeCellRenderer renderer7 = new KeelTreeCellRenderer();
            renderer7.setLeafIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/algoVS.gif")));
            visualizeSelectionTree.setCellRenderer(renderer7);

            //since we have created the Tree again, we need to bind the event listener as well
            visualizeSelectionTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {

                public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                    visualizeSelectionTreeValueChanged(evt);
                }
            });
        }
        visualizeScroll.setViewportView(visualizeSelectionTree);

    //this.setVisible(true);
    }

    /**
     * Regenerate the partitions in the DataSet node of the graph.
     * This ensures that all the required partitions will be present.
     */
    private void regeneratePartitions() {
        boolean stop = false;
        DataSet ds;

        //Look for the dataset node
        for (int i = 0; i < experimentGraph.numNodes() && !stop; i++) {
            if (experimentGraph.getNodeAt(i).type == Node.type_Dataset) {
                //fill the missing vector
                ds = new DataSet(((DataSet) experimentGraph.getNodeAt(i)).dsc, ((DataSet) experimentGraph.getNodeAt(i)).getPosition(), graphDiagramINNER, null, 0);
                //experimentGraph.replaceNode(i, ds);
                //regenerate partitions
                this.graphDiagramINNER.regenerateDatasetPartitions(ds);
                stop = true;
                //fill the data sets tables with the regenerated partitions
                ds = new DataSet(((DataSet) experimentGraph.getNodeAt(i)).dsc, ((DataSet) experimentGraph.getNodeAt(i)).getPosition(), graphDiagramINNER, null, 0);
                //replace the old node
                //fill the structures from the values of the previous dataset node
                ds.figure = ((DataSet) experimentGraph.getNodeAt(i)).figure;
                ds.dialog = ((DataSet) experimentGraph.getNodeAt(i)).dialog;
                ds.centre = ((DataSet) experimentGraph.getNodeAt(i)).centre;

                ds.tableVector = (Vector) ((DataSet) experimentGraph.getNodeAt(i)).tableVector.clone();

                //rplace the old node
                experimentGraph.replaceNode(i, ds);
            }
        }

    //checks if the partitions are correct

    }

    /** Overridden so we can exit when window is closed, or cancel the process */
    @Override
    protected void processWindowEvent(WindowEvent evt) {
        /***************************************************************
         *********************  EDUCATIONAL KEEL  **********************
         **************************************************************/
        int opcionInterna = JOptionPane.YES_OPTION;
        if (Frame.buttonPressed == 0) //Button Experiments pressed
        {
            if (evt.getID() == WindowEvent.WINDOW_CLOSING) {
                int salvar = JOptionPane.YES_OPTION;
                if (experimentGraph.getModified()) {
                    salvar = JOptionPane.showConfirmDialog(this,
                            "Save Modified Experiment?", "Save Changes",
                            JOptionPane.YES_NO_CANCEL_OPTION);
                    if (salvar == JOptionPane.YES_OPTION) {
                        opcionInterna = saveExperiment(0);
                    }
                }

                if (salvar != JOptionPane.CANCEL_OPTION && opcionInterna != JFileChooser.CANCEL_OPTION) {
                    super.processWindowEvent(evt);
                    this.dispose();
                    this.father.setEnabled(true);
                    this.father.setVisible(true);
                    Layer.layerActivo = 0;

                    if (objType == IMBALANCED) {
                        this.father.setVisible(false);
                        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                        FrameModules frame = new FrameModules();

                        Dimension frameSize = frame.getSize();
                        if (frameSize.height > screenSize.height) {
                            frameSize.height = screenSize.height;
                        }
                        if (frameSize.width > screenSize.width) {
                            frameSize.width = screenSize.width;
                        }
                        frame.setLocation((screenSize.width - frameSize.width) / 2,
                                (screenSize.height - frameSize.height) / 2);

                        frame.setParent(father);
                        this.setVisible(false);
                        frame.setVisible(true);
                    }
                }
            }
        } else //Button Teaching Pressed
        {
            if (evt.getID() == WindowEvent.WINDOW_CLOSING) {
                //Experiment runing, window opened
                if (getExecDocentWindowState() == false) {
                    int salvar = JOptionPane.YES_OPTION;
                    if (experimentGraph.getModified()) {
                        salvar = JOptionPane.showConfirmDialog(this,
                                "Save Modified Experiment?", "Save Changes",
                                JOptionPane.YES_NO_CANCEL_OPTION);
                        if (salvar == JOptionPane.YES_OPTION) {
                            opcionInterna = saveExperiment(0);
                        }
                    }
                    if (salvar != JOptionPane.CANCEL_OPTION && opcionInterna != JFileChooser.CANCEL_OPTION) {
                        super.processWindowEvent(evt);
                        this.dispose();
                        this.father.setEnabled(true);
                        this.father.setVisible(true);
                        Layer.layerActivo = 0;
                        deleteExecDocentWindow();
                        closedEducationalExec(null);
                    }
                } //Experiment closed. window closed
                else {
                    int salvar = JOptionPane.YES_OPTION;
                    if (experimentGraph.getModified()) {
                        salvar = JOptionPane.showConfirmDialog(this,
                                "Save Modified Experiment?", "Save Changes",
                                JOptionPane.YES_NO_CANCEL_OPTION);
                        if (salvar == JOptionPane.YES_OPTION) {
                            opcionInterna = saveExperiment(0);
                        }
                    }

                    if (salvar != JOptionPane.CANCEL_OPTION && opcionInterna != JFileChooser.CANCEL_OPTION) {
                        super.processWindowEvent(evt);
                        this.dispose();
                        this.father.setEnabled(true);
                        this.father.setVisible(true);
                        Layer.layerActivo = 0;
                    }
                }
            }
        }

    /***************************************************************
     *********************  EDUCATIONAL KEEL  **********************
     **************************************************************/
    }
} //END OF CLASS
