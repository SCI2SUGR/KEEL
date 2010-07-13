/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S·nchez (luciano@uniovi.es)
    J. Alcal·-Fdez (jalcala@decsai.ugr.es)
    S. GarcÌa (sglopez@ujaen.es)
    A. Fern·ndez (alberto.fernandez@ujaen.es)
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
 * <p>
 * @author Administrator
 * @author Modified by Pedro Antonio Guti√©rrez and Juan Carlos Fern√°ndez (University of C√≥rdoba) 23/10/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
package keel.GraphInterKeel.datacf.help;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.Vector;

public class HelpOptions extends JPanel {

    /**
     * <p>
     * Panel for help options
     * </p>
     */

    /** BorderLayout */
    private BorderLayout borderLayout1 = new BorderLayout();

    /** JTabbedPanel */
    private JTabbedPane jTabbedPane1 = new JTabbedPane();

    /** Main content panel */
    private JPanel contents = new JPanel();

    /** Index */
    private JPanel index = new JPanel();

    /** Other border layout */
    private BorderLayout borderLayout2 = new BorderLayout();

    /** Scroll Panel */
    private JScrollPane jScrollPane1 = new JScrollPane();

    /** The list */
    private JList list = new JList();

    /** Main Panel */
    private JPanel jPanel1 = new JPanel();

    /** Other Panel */
    private BorderLayout borderLayout3 = new BorderLayout();

    /** JLabel */
    private JLabel jLabel1 = new JLabel();

    /** Text of the options */
    private JTextField text = new JTextField();

    /** Other scroll panel */
    private JScrollPane jScrollPane2;

    /** Other border layout */
    private BorderLayout borderLayout4 = new BorderLayout();

    /** Help options tree */
    protected JTree tree;

    /** Top of the tree */
    protected DefaultMutableTreeNode top;

    /** Parent frame */
    private HelpFrame parent;

    /** Search button */
    private JButton search = new JButton();

    /**
     * <p>
     * Constructor that initializes the frame
     * </p>
     * @param v Help frame for initializing the options
     */
    public HelpOptions(HelpFrame v) {
        try {
            parent = v;
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * <p>
     * Init the components
     * </p>
     * @throws java.lang.Exception Exception in the component initialization
     */
    void jbInit() throws Exception {
        this.setLayout(borderLayout1);
        index.setLayout(borderLayout2);
        jPanel1.setLayout(borderLayout3);
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11));
        jLabel1.setText("Search for:");
        text.setFont(new java.awt.Font("Arial", 0, 11));
        text.setText("");
        text.addActionListener(new HelpOptions_text_actionAdapter(this));
        contents.setLayout(borderLayout4);
        borderLayout2.setHgap(0);
        borderLayout2.setVgap(10);
        borderLayout3.setHgap(3);
        borderLayout3.setVgap(10);
        top = new DefaultMutableTreeNode("Help");
        tree = new JTree(top);
        tree.addTreeSelectionListener(new HelpOptions_tree_treeSelectionAdapter(this));
        search.setText("");
        search.setFont(new java.awt.Font("Arial", 0, 11));
        search.setMaximumSize(new Dimension(24, 24));
        search.setMinimumSize(new Dimension(24, 24));
        search.setPreferredSize(new Dimension(24, 24));
        search.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/ico/help/busca.gif")));
        search.addActionListener(new HelpOptions_search_actionAdapter(this));
        list.addListSelectionListener(new HelpOptions_list_listSelectionAdapter(this));
        list.setFont(new java.awt.Font("Arial", 0, 11));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tree.setFont(new java.awt.Font("Arial", 0, 11));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 11));
        this.setFont(new java.awt.Font("Arial", 0, 11));
        contents.setFont(new java.awt.Font("Arial", 0, 11));

        index.setFont(new java.awt.Font("Arial", 0, 11));
        jScrollPane1.setFont(new java.awt.Font("Arial", 0, 11));
        jPanel1.setFont(new java.awt.Font("Arial", 0, 11));
        this.add(jTabbedPane1, BorderLayout.CENTER);
        jTabbedPane1.add(contents, "Content");
        jScrollPane2 = new JScrollPane(tree);
        jScrollPane2.setFont(new java.awt.Font("Arial", 0, 11));
        contents.add(jScrollPane2, BorderLayout.CENTER);
        jScrollPane2.getViewport().add(tree, null);
        jTabbedPane1.add(index, "Index");
        index.add(jScrollPane1, BorderLayout.CENTER);
        jScrollPane1.getViewport().add(list, null);
        index.add(jPanel1, BorderLayout.NORTH);
        jPanel1.add(jLabel1, BorderLayout.NORTH);
        jPanel1.add(text, BorderLayout.CENTER);
        jPanel1.add(search, BorderLayout.EAST);

        // Tree options
        DefaultTreeCellRenderer renderer1 = new DefaultTreeCellRenderer();
        renderer1.setClosedIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/ico/help/libro1.gif")));
        renderer1.setOpenIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/ico/help/libro2.gif")));
        renderer1.setLeafIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/ico/help/fich_ayuda.gif")));
        tree.setCellRenderer(renderer1);
        tree.expandRow(0);
    }

    /**
     * <p>
     * The node tree has changed
     * </p>
     * @param e Event of changing the tree
     */
    void arbol_valueChanged(TreeSelectionEvent e) {
        // Show help file associated to the item
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node != null) {
            Object o = node.getUserObject();
            if (node.isLeaf()) {
                HelpSheet h = (HelpSheet) o;
                parent.content.muestraURL(h.urlAdress);
                // Clear selection
                list.clearSelection();
            }
        }
    }

    /**
     * <p>
     * Action of search button
     * </p>
     * @param e Action Event
     */
    void searchActionPerformed(ActionEvent e) {
        // Show help themes
        if (text.getText().length() == 0) {
            JOptionPane.showMessageDialog(parent, "There are nothing to search", "Info", 1);
        } else {
            Vector v = new Vector();
            for (int i = 0; i < parent.themes.size(); i++) {
                HelpSheet a = (HelpSheet) parent.themes.elementAt(i);
                if (a.toString().toLowerCase().indexOf(text.getText().toLowerCase()) != -1) {
                    v.addElement(parent.themes.elementAt(i));
                }
            }
            list.setListData(v);
        }
    }

    /**
     * <p>
     * The list has changed
     * </p>
     * @param e Event of change in the list
     */
    void listValueChanged(ListSelectionEvent e) {
        // Show help file
        if (list.getSelectedIndex() != -1) {
            HelpSheet h = (HelpSheet) list.getSelectedValue();
            parent.content.muestraURL(h.urlAdress);
            // Clear tree selection
            tree.setSelectionPath(null);
        }
    }

    /**
     * <p>
     * Change in the text
     * </p>
     * @param e Action Event
     */
    void textActionPerformed(ActionEvent e) {
        // Show help themes
        if (text.getText().length() == 0) {
            JOptionPane.showMessageDialog(parent, "There are nothing to search", "Info", 1);
        } else {
            Vector v = new Vector();
            for (int i = 0; i < parent.themes.size(); i++) {
                HelpSheet a = (HelpSheet) parent.themes.elementAt(i);
                if (a.toString().toLowerCase().indexOf(text.getText().toLowerCase()) != -1) {
                    v.addElement(parent.themes.elementAt(i));
                }
            }
            list.setListData(v);
        }
    }

    /** ************************* */
    /** Auxiliary private classes */
    /** ************************* */
    
    private class HelpOptions_tree_treeSelectionAdapter implements javax.swing.event.TreeSelectionListener {

        HelpOptions adaptee;

        HelpOptions_tree_treeSelectionAdapter(HelpOptions adaptee) {
            this.adaptee = adaptee;
        }

        public void valueChanged(TreeSelectionEvent e) {
            adaptee.arbol_valueChanged(e);
        }
    }

    private class HelpOptions_search_actionAdapter implements java.awt.event.ActionListener {

        HelpOptions adaptee;

        HelpOptions_search_actionAdapter(HelpOptions adaptee) {
            this.adaptee = adaptee;
        }

        public void actionPerformed(ActionEvent e) {
            adaptee.searchActionPerformed(e);
        }
    }

    private class HelpOptions_list_listSelectionAdapter implements javax.swing.event.ListSelectionListener {

        HelpOptions adaptee;

        HelpOptions_list_listSelectionAdapter(HelpOptions adaptee) {
            this.adaptee = adaptee;
        }

        public void valueChanged(ListSelectionEvent e) {
            adaptee.listValueChanged(e);
        }
    }

    private class HelpOptions_text_actionAdapter implements java.awt.event.ActionListener {

        HelpOptions adaptee;

        HelpOptions_text_actionAdapter(HelpOptions adaptee) {
            this.adaptee = adaptee;
        }

        public void actionPerformed(ActionEvent e) {
            adaptee.textActionPerformed(e);
        }
    }

}
