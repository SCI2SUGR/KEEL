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
 * File: Application1.java
 *
 * Frame of the application to process the execution of a experiment. Graph version
 *
 * @author Joaquin Derrac (University of Granada) 15/6/2009
 * @version 1.0
 * @since JDK1.5
 *
 */

package keel.RunKeelGraph;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.Vector;

public class Frame1 extends JFrame {

    JPanel contentPane;
    JButton jButton1 = new JButton();
    JScrollPane jScrollPane1 = new JScrollPane();
    JTextArea jTextArea1 = new JTextArea();
    JProgressBar jProgressBar1 = new JProgressBar();
    JButton jButton2 = new JButton();
    int paso = 0;
    Execute exe;
    JButton jButton3 = new JButton();
    JScrollPane jScrollPane2 = new JScrollPane();
    JTextArea jTextArea2 = new JTextArea();
    JLabel jLabel1 = new JLabel();

    /**
     * Default builder
     */
    public Frame1() {

        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Component initialization
     * @throws java.lang.Exception
     */
    private void jbInit() throws Exception {

        this.setFont(new java.awt.Font("Arial", 0, 11));
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(Frame1.class.getResource("/ico/logo.gif")));
        contentPane = (JPanel) this.getContentPane();
        jButton1.setBounds(new Rectangle(259, 16, 132, 30));
        jButton1.setFont(new java.awt.Font("Arial", 0, 11));
        jButton1.setIcon(new ImageIcon(Frame1.class.getResource("/ico/play.gif")));
        jButton1.setText("Run Experiment");
        jButton1.addActionListener(new Frame1_jButton1_actionAdapter(this));
        contentPane.setLayout(null);
        this.setSize(new Dimension(800, 600));
        this.setTitle("Run Experiment");
        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jScrollPane1.setFont(new java.awt.Font("Arial", 0, 11));
        jScrollPane1.setBounds(new Rectangle(17, 68, 764, 200));
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 11));
        jTextArea1.setEditable(false);
        jProgressBar1.setFont(new java.awt.Font("Arial", 0, 11));
        jProgressBar1.setBounds(new Rectangle(17, 538, 764, 14));
        jButton2.setBounds(new Rectangle(408, 16, 132, 30));
        jButton2.setEnabled(false);
        jButton2.setFont(new java.awt.Font("Arial", 0, 11));
        jButton2.setIcon(new ImageIcon(Frame1.class.getResource("/ico/stop.gif")));
        jButton2.setText("Stop");
        jButton2.addActionListener(new Frame1_jButton2_actionAdapter(this));
        jButton3.setBounds(new Rectangle(750, 16, 30, 30));
        jButton3.setFont(new java.awt.Font("Arial", 0, 1));
        jButton3.setToolTipText("Results");
        jButton3.setIcon(new ImageIcon(Frame1.class.getResource("/ico/test.gif")));
        jButton3.setText("");
        jButton3.addActionListener(new Frame1_jButton3_actionAdapter(this));
        jScrollPane2.setFont(new java.awt.Font("Arial", 0, 11));
        jScrollPane2.setBounds(new Rectangle(17, 312, 764, 200));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11));
        jLabel1.setText("Standard output");
        jLabel1.setBounds(new Rectangle(17, 289, 118, 21));
        contentPane.setFont(new java.awt.Font("Arial", 0, 11));
        jTextArea2.setFont(new java.awt.Font("Arial", 0, 11));
        contentPane.add(jScrollPane1, null);
        jScrollPane1.getViewport().add(jTextArea1, null);
        contentPane.add(jButton3, null);
        contentPane.add(jButton2, null);
        contentPane.add(jButton1, null);
        contentPane.add(jLabel1, null);
        contentPane.add(jScrollPane2, null);
        contentPane.add(jProgressBar1, null);
        jScrollPane2.getViewport().add(jTextArea2, null);
        this.setResizable(false);

        Vector listado = new Vector();
        listDirectory("../results", listado);
        if (listado.size() == 0) {
            jButton3.setEnabled(false);
        }
    }

    /**
     * Overridden so we can exit when window is closed
     * @param e Event
     */
    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            System.exit(0);
        }
    }

    void jButton1_actionPerformed(ActionEvent e) {
        jButton1.setEnabled(false);
        jButton2.setEnabled(true);

        // Thread that executes commands
        exe = new Execute("Executable", this);
        exe.start();
    }

    void jButton2_actionPerformed(ActionEvent e) {
        paso = 0;
        jProgressBar1.setValue(0);
        jButton1.setEnabled(true);
        jButton2.setEnabled(false);
        jButton3.setEnabled(true);
        exe.pr.destroy();
        exe.stop();
        jTextArea1.append("\n");
    }

    class Execute extends Thread {

        private Frame1 parent;
        Process pr;

        /**
         * Buider
         * @param name
         * @param frame
         */
        public Execute(String name, Frame1 frame) {
            super(name);
            parent = frame;
        }

        /**
         * Run method of the thread
         */
        @Override
        public void run() {
            try {
                FileInputStream file = new FileInputStream("RunKeel.config");
                ObjectInputStream input = new ObjectInputStream(file);
                Vector sentencias = (Vector) ((Vector) input.readObject()).elementAt(0);
                input.close();
                jProgressBar1.setMinimum(0);
                jProgressBar1.setMaximum(sentencias.size());
                jProgressBar1.setValue(parent.paso);

                boolean para = false;
                for (int i = parent.paso; i < sentencias.size() && !para; i++) {
                    parent.paso = i;
                    String comando = (String) sentencias.elementAt(i);
                    jTextArea1.append("\nExecuting: " + comando);
                    pr = Runtime.getRuntime().exec(comando);
                    BufferedReader error = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
                    BufferedReader salida = new BufferedReader(new InputStreamReader(pr.getInputStream()));

                    pr.waitFor();

                    // Error messages
                    String line = null;
                    if (pr.exitValue() != 0) {
                        while ((line = error.readLine()) != null) {
                            jTextArea1.append("\n" + line);
                        }

                        jTextArea1.append("\nError: exit value not 0");
                        para = true;
                        JOptionPane.showMessageDialog(parent, "Error running experiment",
                                "Run Keel",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        jProgressBar1.setValue(i + 1);
                    }

                    // Standard output
                    line = salida.readLine();
                    if (line != null) {
                        jTextArea2.append("\n\n--> Output: " + comando);
                        jTextArea2.append("\n\n" + line);
                        while ((line = salida.readLine()) != null) {
                            jTextArea2.append("\n" + line);
                        }
                    }
                }
                if (!para) {
                    parent.paso = 0;
                    JOptionPane.showMessageDialog(parent,
                            "Experiment completed successfully",
                            "Run Keel",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                jTextArea1.append("\n" + ex.toString());
                JOptionPane.showMessageDialog(parent, "Error running experiment",
                        "Run Keel",
                        JOptionPane.ERROR_MESSAGE);
            }
            jTextArea1.append("\n");
            jButton1.setEnabled(true);
            jButton2.setEnabled(false);
            jButton3.setEnabled(true);
        }
    }

    void jButton3_actionPerformed(ActionEvent e) {
        TestsResults dialogo = new TestsResults(this, "Results", true);

        // Center dialog
        dialogo.setSize(800, 600);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = dialogo.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        dialogo.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        dialogo.setResizable(false);
        dialogo.setVisible(true);
    }

    /**
     * List the content of a directory (Not used, only for future use)
     * @param directory
     * @param result
     */
    private void listDirectory(String directory, Vector result) {

        File file = new File(directory);
        File listado[] = file.listFiles();

        if (listado == null) {
            return;
        }

        for (int i = 0; i < listado.length; i++) {

            if (listado[i].isFile()) {
                result.add(new String(directory + "/" + listado[i].getName()));
            } else {
                listDirectory(directory + "/" + listado[i].getName(), result);
            }
        }
    }
}

class Frame1_jButton1_actionAdapter
        implements java.awt.event.ActionListener {

    Frame1 adaptee;

    Frame1_jButton1_actionAdapter(Frame1 adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton1_actionPerformed(e);
    }
}

class Frame1_jButton2_actionAdapter
        implements java.awt.event.ActionListener {

    Frame1 adaptee;

    Frame1_jButton2_actionAdapter(Frame1 adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton2_actionPerformed(e);
    }
}

class Frame1_jButton3_actionAdapter
        implements java.awt.event.ActionListener {

    Frame1 adaptee;

    Frame1_jButton3_actionAdapter(Frame1 adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton3_actionPerformed(e);
    }
}

