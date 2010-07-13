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
 * File: TestResults.java
 *
 * Class to help in the processing of the execution of a experiment. Graph version
 *
 * @author Joaquin Derrac (University of Granada) 15/6/2009
 * @version 1.0
 * @since JDK1.5
 *
 */
package keel.RunKeelGraph;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;
import java.awt.event.*;

public class TestsResults extends JDialog {

    BorderLayout borderLayout1 = new BorderLayout();
    JScrollPane jScrollPane1 = new JScrollPane();
    JScrollPane jScrollPane2 = new JScrollPane();
    JScrollPane jScrollPane3 = new JScrollPane();
    JTree jTree1;
    JTree jTree2;
    JTextArea jTextArea1 = new JTextArea();
    JTabbedPane jTabbedPane1 = new JTabbedPane();

    /**
     * Builder
     * @param frame Parent frame
     * @param title Title of the window
     * @param modal Test if the window is modal or not
     */
    public TestsResults(Frame frame, String title, boolean modal) {
        super(frame, title, modal);
        try {
            jbInit();
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Default builder
     */
    public TestsResults() {
        this(null, "", false);
    }

    private void jbInit() throws Exception {
        this.getContentPane().setLayout(borderLayout1);
        this.setFont(new java.awt.Font("Arial", 0, 11));
        this.setResizable(false);
        jTextArea1.setBackground(Color.white);
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 11));
        jTextArea1.setEditable(false);
        jScrollPane2.setFont(new java.awt.Font("Arial", 0, 11));
        jScrollPane2.setMinimumSize(new Dimension(430, 400));
        jScrollPane2.setPreferredSize(new Dimension(430, 400));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 11));
        jTabbedPane1.setMinimumSize(new Dimension(170, 400));
        jTabbedPane1.setPreferredSize(new Dimension(170, 400));

        // Algorithms and test directories
        FileInputStream file = new FileInputStream("RunKeel.config");
        ObjectInputStream input = new ObjectInputStream(file);
        Vector tests = (Vector) ((Vector) input.readObject()).elementAt(1);
        input.close();

        File f = new File("../results");
        File[] listado = f.listFiles();
        Vector algoritmos = new Vector();
        for (int i = 0; i < listado.length; i++) {
            if (tests.contains(listado[i].getName()) == false) {
                algoritmos.addElement(listado[i].getName());
            }
        }

        // Create algoritmh tree
        DefaultMutableTreeNode top2 = new DefaultMutableTreeNode("Algorithms");
        createAlgorithmNodes(top2, algoritmos);
        jTree2 = new JTree(top2) {

            public String getToolTipText(MouseEvent evt) {
                try {
                    if (getRowForLocation(evt.getX(), evt.getY()) == -1) {
                        return null;
                    }
                    TreePath curPath = getPathForLocation(evt.getX(), evt.getY());
                    if (((ToolTipTreeNode) curPath.getLastPathComponent()).isValid() == false) {
                        ((ToolTipTreeNode) curPath.getLastPathComponent()).setToolTipText();
                    }
                    return ((ToolTipTreeNode) curPath.getLastPathComponent()).getToolTipText();
                } catch (Exception e) {
                    return null;
                }
            }
        };
        jTree2.setFont(new java.awt.Font("Arial", 0, 11));
        jTree2.setToolTipText("");

        DefaultTreeCellRenderer renderer2 = new DefaultTreeCellRenderer();
        renderer2.setLeafIcon(new ImageIcon(Frame1.class.getResource(
                "/ico/algo.gif")));
        jTree2.setCellRenderer(renderer2);
        jTree2.addTreeSelectionListener(new TestsResults_jTree2_treeSelectionAdapter(this));
        jScrollPane3.setFont(new java.awt.Font("Arial", 0, 11));
        jScrollPane1.setFont(new java.awt.Font("Arial", 0, 11));
        jTree1.setFont(new java.awt.Font("Arial", 0, 11));
        jScrollPane3.getViewport().add(jTree2, null);
        jTabbedPane1.addTab("Algorithms",
                new ImageIcon(Frame1.class.getResource("/ico/algo.gif")),
                jScrollPane3);

        // Create test tree
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Tests");
        createTestNodes(top, tests);
        jTree1 = new JTree(top) {

            public String getToolTipText(MouseEvent evt) {
                try {
                    if (getRowForLocation(evt.getX(), evt.getY()) == -1) {
                        return null;
                    }
                    TreePath curPath = getPathForLocation(evt.getX(), evt.getY());
                    if (((ToolTipTreeNode) curPath.getLastPathComponent()).isValid() == false) {
                        ((ToolTipTreeNode) curPath.getLastPathComponent()).setToolTipText();
                    }
                    return ((ToolTipTreeNode) curPath.getLastPathComponent()).getToolTipText();

                } catch (Exception e) {
                    return null;
                }
            }
        };
        jTree1.setToolTipText("");

        DefaultTreeCellRenderer renderer1 = new DefaultTreeCellRenderer();
        renderer1.setLeafIcon(new ImageIcon(Frame1.class.getResource(
                "/ico/test.gif")));
        jTree1.setCellRenderer(renderer1);
        jTree1.addTreeSelectionListener(new TestsResults_jTree1_treeSelectionAdapter(this));
        jScrollPane1.getViewport().add(jTree1, null);
        jTabbedPane1.addTab("Tests",
                new ImageIcon(Frame1.class.getResource("/ico/test.gif")),
                jScrollPane1);

        this.getContentPane().add(jTabbedPane1, BorderLayout.WEST);
        this.getContentPane().add(jScrollPane2, BorderLayout.CENTER);
        jScrollPane2.getViewport().add(jTextArea1, null);
    }

    private void createTestNodes(DefaultMutableTreeNode top, Vector tests) {
        String raiz = "../results/";
        for (int i = 0; i < tests.size(); i++) {
            File f = new File(raiz + tests.elementAt(i));
            if (f.isDirectory()) {
                insertDirectory(f, raiz + tests.elementAt(i), top, null, true);
            }
        }
    }

    private void createAlgorithmNodes(DefaultMutableTreeNode top, Vector algo) {
        String raiz = "../results/";
        for (int i = 0; i < algo.size(); i++) {
            File f = new File(raiz + algo.elementAt(i));
            if (f.isDirectory()) {
                insertDirectory(f, raiz + algo.elementAt(i), top, null, true);
            }
        }
    }

    private void insertDirectory(File f, String nombre,
            DefaultMutableTreeNode actual,
            FilenameFilter filtro, boolean ins_files) {
        // Add directory to the tree and his content recursively
        // files are added if ins_files is true

        // Add actual directory
        DefaultMutableTreeNode dir = new DefaultMutableTreeNode(f.getName());
        actual.add(dir);

        // Analize directory content and add (first directories, then files) filtering
        String s[] = f.list(filtro);
        Arrays.sort(s);
        for (int i = 0; i < s.length; i++) {
            File f2 = new File(nombre + "/" + s[i]);
            if (f2.isDirectory()) {
                insertDirectory(f2, nombre + "/" + s[i], dir, filtro, ins_files);
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

    private void insertFile(File f, String nombre,DefaultMutableTreeNode actual) {
        // Add file to the tree, above according directory
        ToolTipTreeNode fich = new ToolTipTreeNode(f.getName(), nombre);
        actual.add(fich);
    }

    /**
     * Search the path of a experiment
     * @param name Name of the experiment
     * @return The path found
     */
    protected static String searchPath(String name) {

        String linea, config, ruta, aux, lectura, partes[];
        StringTokenizer tokenizer;
        boolean para = false;
        File f;

        config = new String(name);
        partes = name.split("/");
        ruta = new String(partes[partes.length - 2] + " -> " + partes[partes.length -
                3]);

        do {
            // config file name associated
            config = config.replaceFirst("../results", ".");
            config = config.replaceFirst("result", "config");
            config = config.replaceFirst(".tra", ".txt");
            config = config.replaceFirst(".tst", ".txt");

            // read file to extract inputData
            f = new File(config);
            if (!f.exists()) {
                ruta = null;
                break;
            }
            lectura = readFile(config);
            lectura = lectura + "\n";
            tokenizer = new StringTokenizer(lectura, "\n\r");
            linea = tokenizer.nextToken();
            linea = tokenizer.nextToken();

            tokenizer = new StringTokenizer(lectura, "\"");
            linea = tokenizer.nextToken();
            linea = tokenizer.nextToken();

            if (linea.indexOf("datasets") != -1) {
                config = new String(linea);
                partes = linea.split("/");
                aux = new String(partes[partes.length - 1]);
                aux = aux.substring(0, aux.lastIndexOf("tra"));
                ruta = ruta.substring(0, ruta.indexOf("->")) + "(" + aux + ") " +
                        ruta.substring(ruta.indexOf("->"));
                para = true;
            } else {
                config = new String(linea);
                partes = linea.split("/");
                aux = new String(partes[partes.length - 2]);
                ruta = aux.concat(" -> " + ruta);
            }
        } while (!para);

        return ruta;
    }

    private static String readFile(String nombreFichero) {
        String cadena = "";

        try {
            FileInputStream fis = new FileInputStream(nombreFichero);

            byte[] leido = new byte[4096];
            int bytesLeidos = 0;

            while (bytesLeidos != -1) {
                bytesLeidos = fis.read(leido);

                if (bytesLeidos != -1) {
                    cadena += new String(leido, 0, bytesLeidos);
                }
            }

            fis.close();
        } catch (Exception e) {
        }

        return cadena;
    }

    void jTree1_valueChanged(TreeSelectionEvent e) {
        // Tree selection
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) (e.getPath().
                getLastPathComponent());

        if (jTree1.getSelectionPath() != null) {
            if (node.isLeaf()) {
                jTree2.setSelectionPath(null);
                // Obtain path of selected directory
                String cad = e.getPath().toString().substring(1,
                        e.getPath().toString().length() - 1);
                StringTokenizer partes = new StringTokenizer(cad, ", ");
                partes.nextToken();
                String path_nodo = "../results";
                while (partes.hasMoreTokens()) {
                    path_nodo += "/" + partes.nextToken();
                }
                jTextArea1.setText(readFile(path_nodo));
                jTextArea1.setCaretPosition(0);
            }
        }
    }

    void jTree2_valueChanged(TreeSelectionEvent e) {
        // Tree selection
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) (e.getPath().
                getLastPathComponent());

        if (jTree2.getSelectionPath() != null) {
            if (node.isLeaf()) {
                jTree1.setSelectionPath(null);
                // Obtain path of selected directory
                String cad = e.getPath().toString().substring(1,
                        e.getPath().toString().length() - 1);
                StringTokenizer partes = new StringTokenizer(cad, ", ");
                partes.nextToken();
                String path_nodo = "../results";
                while (partes.hasMoreTokens()) {
                    path_nodo += "/" + partes.nextToken();
                }
                jTextArea1.setText(readFile(path_nodo));
                jTextArea1.setCaretPosition(0);
            }
        }
    }
}

class ToolTipTreeNode extends DefaultMutableTreeNode {

    private String toolTipText;
    private String name;
    private boolean valid;

    /**
     * Builder
     * @param str Title of the tree
     * @param name Name of the tree
     */
    public ToolTipTreeNode(String str, String name) {
        super(str);
        this.name = name;
        valid = false;
    }

    /**
     * Get the tool tip text
     * @return The tool tip text
     */
    public String getToolTipText() {
        if (!valid) {
            return null;
        }
        return toolTipText;
    }

    /**
     * Set the tool tip
     */
    public void setToolTipText() {
        toolTipText = TestsResults.searchPath(name);
        valid = true;
    }

    /**
     * Test if the tool tip tree is valid
     * @return
     */
    public boolean isValid() {
        return valid;
    }
}

class TestsResults_jTree1_treeSelectionAdapter
        implements javax.swing.event.TreeSelectionListener {

    TestsResults adaptee;

    TestsResults_jTree1_treeSelectionAdapter(TestsResults adaptee) {
        this.adaptee = adaptee;
    }

    public void valueChanged(TreeSelectionEvent e) {
        adaptee.jTree1_valueChanged(e);
    }
}

class TestsResults_jTree2_treeSelectionAdapter
        implements javax.swing.event.TreeSelectionListener {

    TestsResults adaptee;

    TestsResults_jTree2_treeSelectionAdapter(TestsResults adaptee) {
        this.adaptee = adaptee;
    }

    public void valueChanged(TreeSelectionEvent e) {
        adaptee.jTree2_valueChanged(e);
    }
}

