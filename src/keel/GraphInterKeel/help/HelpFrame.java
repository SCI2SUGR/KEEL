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
 * File: HelpFrame.java
 *
 * A class for managing the help frame
 *
 * @author Written by Admin 4/8/2010
 * @version 1.0
 * @since JDK1.5
 */
package keel.GraphInterKeel.help;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Vector;
import javax.swing.tree.*;

public class HelpFrame extends JFrame {

    JPanel contentPane;
    BorderLayout borderLayout1 = new BorderLayout();
    JSplitPane jSplitPane1 = new JSplitPane();
    HelpOptions opciones;
    HelpContent contenido;
    Vector temas = new Vector();

    /**
     * Builder
     */
    public HelpFrame() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            initializeHelpFrame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialization
     *
     * @throws java.lang.Exception
     */
    private void initializeHelpFrame() throws Exception {
        contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(borderLayout1);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(
                this.getClass().getResource("/keel/GraphInterKeel/resources/ico/logo/logo.gif")));
        this.setResizable(false);
        this.setSize(new Dimension(800, 600));
        this.setTitle("Help");
        jSplitPane1.setDividerSize(7);
        contentPane.setFont(new java.awt.Font("Arial", 0, 11));
        contentPane.add(jSplitPane1, BorderLayout.CENTER);
        jSplitPane1.setDividerLocation(175);

        // Create tree and help page
        opciones = new HelpOptions(this);
        contenido = new HelpContent();

        // Add elements to panel
        jSplitPane1.setLeftComponent(opciones);
        jSplitPane1.setRightComponent(contenido);

        contenido.muestraURL(this.getClass().getResource("/help/intro.html"));

        // Fill help information
        createNodes(opciones.top);
        opciones.arbol.expandRow(0);
    }

    @Override
    /**
     *  Closing the application
     */
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            this.setVisible(false);
        }
    }

    /**
     * Create nodes
     * @param top Root tree
     */
    public void createNodes(DefaultMutableTreeNode top) {
        DefaultMutableTreeNode categ = null;
        DefaultMutableTreeNode categ2 = null;
        DefaultMutableTreeNode categ3 = null;
        DefaultMutableTreeNode hoja = null;

        hoja = new DefaultMutableTreeNode(new HelpSheet("Introduction",
                this.getClass().getResource("/help/intro.html")));
        top.add(hoja);
        temas.add(new HelpSheet("Introduction",
                this.getClass().getResource("/help/intro.html")));

        //Data preparation
        categ = new DefaultMutableTreeNode("Data preparation");
        top.add(categ);
        hoja = new DefaultMutableTreeNode(new HelpSheet("New",
                this.getClass().getResource("/help/data_new.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("New",
                this.getClass().getResource("/help/data_new.html")));
        hoja = new DefaultMutableTreeNode(new HelpSheet("View",
                this.getClass().getResource("/help/data_view.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("View",
                this.getClass().getResource("/help/data_view.html")));
        hoja = new DefaultMutableTreeNode(new HelpSheet("Data Import",
                this.getClass().getResource("/help/data_import.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("Data Import",
                this.getClass().getResource("/help/data_import.html")));

        hoja = new DefaultMutableTreeNode(new HelpSheet("Data Export",
                this.getClass().getResource("/help/data_export.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("Data Export",
                this.getClass().getResource("/help/data_export.html")));

        hoja = new DefaultMutableTreeNode(new HelpSheet("Partition",
                this.getClass().getResource("/help/data_partition.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("Partition",
                this.getClass().getResource("/help/data_partition.html")));
        hoja = new DefaultMutableTreeNode(new HelpSheet("Edit",
                this.getClass().getResource("/help/data_edit.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("Edit",
                this.getClass().getResource("/help/data_edit.html")));
        /*hoja = new DefaultMutableTreeNode(new HelpSheet("Preparation",
                this.getClass().getResource("/help/data_trans.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("Preparation",
                this.getClass().getResource("/help/data_trans.html")));*/

        // Experiment design
        //Introduction
        categ = new DefaultMutableTreeNode("Experiments design");
        top.add(categ);
        hoja = new DefaultMutableTreeNode(new HelpSheet("Introduction",
                this.getClass().getResource("/help/exp_intro.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("Introduction",
                this.getClass().getResource("/help/exp_intro.html")));

        //Configuration of experiments
        hoja = new DefaultMutableTreeNode(new HelpSheet("Configuration of experiments",
                this.getClass().getResource("/help/exp_config.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("Configuration of experiments",
                this.getClass().getResource("/help/exp_config.html")));

        //Selection of data sets
        hoja = new DefaultMutableTreeNode(new HelpSheet("Selection of data sets",
                this.getClass().getResource("/help/exp_selection.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("Selection of data sets",
                this.getClass().getResource("/help/exp_selection.html")));

        //Experiment graph
        hoja = new DefaultMutableTreeNode(new HelpSheet("Experiment graph",
                this.getClass().getResource("/help/exp_graph.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("Experiment graph",
                this.getClass().getResource("/help/exp_graph.html")));

        //Subcategory
        categ2 = new DefaultMutableTreeNode("Experiment graph elements");
        categ.add(categ2);

        hoja = new DefaultMutableTreeNode(new HelpSheet("Data sets",
                this.getClass().getResource("/help/exp_graph_datasets.html")));
        categ2.add(hoja);
        temas.add(new HelpSheet("Data sets",
                this.getClass().getResource("/help/exp_graph_datasets.html")));

        hoja = new DefaultMutableTreeNode(new HelpSheet("Preprocessing methods",
                this.getClass().getResource("/help/exp_graph_preprocess.html")));
        categ2.add(hoja);
        temas.add(new HelpSheet("Preprocessing methods",
                this.getClass().getResource("/help/exp_graph_preprocess.html")));

        hoja = new DefaultMutableTreeNode(new HelpSheet("Standard methods",
                this.getClass().getResource("/help/exp_graph_standard.html")));
        categ2.add(hoja);
        temas.add(new HelpSheet("Standard methods",
                this.getClass().getResource("/help/exp_graph_standard.html")));

        hoja = new DefaultMutableTreeNode(new HelpSheet("Postprocessing methods",
                this.getClass().getResource("/help/exp_graph_postprocess.html")));
        categ2.add(hoja);
        temas.add(new HelpSheet("Postprocessing methods",
                this.getClass().getResource("/help/exp_graph_postprocess.html")));

        hoja = new DefaultMutableTreeNode(new HelpSheet("Statistical tests",
                this.getClass().getResource("/help/exp_graph_statistical.html")));
        categ2.add(hoja);
        temas.add(new HelpSheet("Statistical tests",
                this.getClass().getResource("/help/exp_graph_statistical.html")));

        hoja = new DefaultMutableTreeNode(new HelpSheet("Visualization modules",
                this.getClass().getResource("/help/exp_graph_visualization.html")));
        categ2.add(hoja);
        temas.add(new HelpSheet("Visualization modules",
                this.getClass().getResource("/help/exp_graph_visualization.html")));

        hoja = new DefaultMutableTreeNode(new HelpSheet("Connections",
                this.getClass().getResource("/help/exp_graph_connections.html")));
        categ2.add(hoja);
        temas.add(new HelpSheet("Connections",
                this.getClass().getResource("/help/exp_graph_connections.html")));

        //Graph Management
        hoja = new DefaultMutableTreeNode(new HelpSheet("Graph Management",
                this.getClass().getResource("/help/exp_management.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("Graph Management",
                this.getClass().getResource("/help/exp_management.html")));

        //Algorithm parameters configuration
        hoja = new DefaultMutableTreeNode(new HelpSheet("Algorithm parameters configuration",
                this.getClass().getResource("/help/exp_parameters.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("Algorithm parameters configuration",
                this.getClass().getResource("/help/exp_parameters.html")));

        //Generation of experiments
        hoja = new DefaultMutableTreeNode(new HelpSheet("Generation of experiments",
                this.getClass().getResource("/help/exp_generation.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("Generation of experiments",
                this.getClass().getResource("/help/exp_generation.html")));

        //Menu bar
        hoja = new DefaultMutableTreeNode(new HelpSheet("Menu bar",
                this.getClass().getResource("/help/exp_menu.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("Menu bar",
                this.getClass().getResource("/help/exp_menu.html")));

        //Tool bar
        hoja = new DefaultMutableTreeNode(new HelpSheet("Tool bar",
                this.getClass().getResource("/help/exp_tool.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("Tool bar",
                this.getClass().getResource("/help/exp_tool.html")));

        //Status bar
        hoja = new DefaultMutableTreeNode(new HelpSheet("Status bar",
                this.getClass().getResource("/help/exp_status.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("Status bar",
                this.getClass().getResource("/help/exp_status.html")));

        //Run KEEL
        categ = new DefaultMutableTreeNode("Run KEEL");
        top.add(categ);

        //Launching RunKeel
        hoja = new DefaultMutableTreeNode(new HelpSheet("Launching RunKeel",
                this.getClass().getResource("/help/run_launch.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("Launching RunKeel",
                this.getClass().getResource("/help/run_launch.html")));
        //View results
        hoja = new DefaultMutableTreeNode(new HelpSheet("View results",
                this.getClass().getResource("/help/run_results.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("View results",
                this.getClass().getResource("/help/run_results.html")));

        //Keel docente
        categ = new DefaultMutableTreeNode("Teaching");
        top.add(categ);
        hoja = new DefaultMutableTreeNode(new HelpSheet("Introduction",
                this.getClass().getResource("/help/teach/exp_intro.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("Introduction",
                this.getClass().getResource("/help/teach/exp_intro.html")));
        hoja = new DefaultMutableTreeNode(new HelpSheet("Menu Bar",
                this.getClass().getResource("/help/teach/exp_menu.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("Menu Bar",
                this.getClass().getResource("/help/teach/exp_menu.html")));
        hoja = new DefaultMutableTreeNode(new HelpSheet("Tools bar",
                this.getClass().getResource("/help/teach/exp_tool.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("Tools bar",
                this.getClass().getResource("/help/teach/exp_tool.html")));
        hoja = new DefaultMutableTreeNode(new HelpSheet("Status bar",
                this.getClass().getResource("/help/teach/exp_status.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("Status bar",
                this.getClass().getResource("/help/teach/exp_status.html")));

        // --> Experiment graph
        categ2 = new DefaultMutableTreeNode("Experiment Graph");
        categ.add(categ2);
        hoja = new DefaultMutableTreeNode(new HelpSheet("Datasets",
                this.getClass().getResource("/help/teach/exp_datasets.html")));
        categ2.add(hoja);
        temas.add(new HelpSheet("Datasets",
                this.getClass().getResource("/help/teach/exp_datasets.html")));

        // --> Algorithms
        categ3 = new DefaultMutableTreeNode("Algorithms");
        categ2.add(categ3);
        hoja = new DefaultMutableTreeNode(new HelpSheet("Types",
                this.getClass().getResource("/help/teach/exp_algotypes.html")));
        categ3.add(hoja);
        temas.add(new HelpSheet("Types",
                this.getClass().getResource("/help/teach/exp_algotypes.html")));
        hoja = new DefaultMutableTreeNode(new HelpSheet("Insert algorithm",
                this.getClass().getResource("/help/teach/exp_algoins.html")));
        categ3.add(hoja);
        temas.add(new HelpSheet("Insert algorithm",
                this.getClass().getResource("/help/teach/exp_algoins.html")));
        hoja = new DefaultMutableTreeNode(new HelpSheet("Parameters configuration",
                this.getClass().getResource("/help/teach/exp_algopar.html")));
        categ3.add(hoja);
        temas.add(new HelpSheet("Parameters configuration",
                this.getClass().getResource("/help/teach/exp_algopar.html")));

        hoja = new DefaultMutableTreeNode(new HelpSheet("Connections",
                this.getClass().getResource("/help/teach/exp_conn.html")));
        categ2.add(hoja);
        temas.add(new HelpSheet("Connections",
                this.getClass().getResource("/help/teach/exp_conn.html")));

        hoja = new DefaultMutableTreeNode(new HelpSheet("Interface management",
                this.getClass().getResource("/help/teach/exp_inter.html")));
        categ.add(hoja);
        temas.add(new HelpSheet("Interface management",
                this.getClass().getResource("/help/teach/exp_inter.html")));

    }
}