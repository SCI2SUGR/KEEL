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
 * File ParametersDialog.java
 *
 * Created on 02-mar-2009, 5:30:48
 * Modified on 12-may-2009
 * @author Ignacio Robles
 * @author Julian Luengo
 * @author Modified by Juan Carlos Fernandez Caballero and Pedro Antonio Gutierrez (University of Córdoba) 7/07/2009
 * @authos Modified Ana Palacios Jimenez and Luciano Sanchez Ramos 23-4-2010 (University of Oviedo)
 */
package keel.GraphInterKeel.experiments;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.table.*;

import keel.GraphInterKeel.menu.Frame;

public class ParametersDialog extends JDialog {

    JPanel panel1 = new JPanel() {

        @Override
        public void paintComponent(Graphics g) {
            //ImageIcon img = new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/experimentos/parametersVector.jpg"));
            //g.drawImage(img.getImage(), 0, 0, null);
            super.paintComponent(g);
        }
    };
    /***************************************************************
     ***************  EDUCATIONAL KEEL  ****************************
     **************************************************************/
    private Experiments experiment;
    /***************************************************************
     ***************  EDUCATIONAL KEEL  ****************************
     **************************************************************/
    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    JLabel jLabel3 = new JLabel();
    JButton jButton1 = new JButton();
    JButton jButton2 = new JButton();
    JScrollPane jScrollPane1 = new JScrollPane();
    JTable jTable1;
    Parameters parameterData;
    JLabel jLabel4 = new JLabel();
    Parameters undoParameters;
    Vector actualSeed;
    ParametersTable paramTable;
    JComboBox opciones = new JComboBox();
    JButton jButton5 = new JButton();
    ImageIcon image1 = new ImageIcon(this.getClass().getResource(
            "/keel/GraphInterKeel/resources/ico/experiments/aceptar.gif"));
    ImageIcon image2 = new ImageIcon(this.getClass().getResource(
            "/keel/GraphInterKeel/resources/ico/experiments/cancelar.gif"));
    ImageIcon image3 = new ImageIcon(this.getClass().getResource(
            "/keel/GraphInterKeel/resources/ico/experiments/deshacer.gif"));
    SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 1000, 1);
    JSpinner jSpinner1 = new JSpinner(model);
    Vector all;
    ExternalObjectDescription dsc;
    JComboBox jComboBox1 = new JComboBox();
    JLabel jLabel5 = new JLabel();
    //JLabel jLabel6 = new JLabel();
    //ExternalObjectDescription dsc1;

    /**
     * Builder
     *
     * @param frame Parent frame
     * @param title Title of the frame
     * @param modal Modal status
     * @param parametersVector Parameters vector
     * @param dsc Parent dsc
     */
    public ParametersDialog(Experiments frame, String title, boolean modal,
            Vector parametersVector, ExternalObjectDescription dsc) {
        super(frame, title, modal);
        
      /*   if(dsc_al.getSubtypelqd()==Node.CRISP2 ||dsc_al.getSubtypelqd()==Node.LQD )
        {
             jComboBox1.setEditable(false);
             jComboBox1.setVisible(false);
              jLabel6.setText("Algorithm without datasets");
              jLabel6.setBounds(new Rectangle(185, 76, 155, 22));
            jLabel6.setForeground(Color.black);
            jLabel6.setBackground(new Color(225, 225, 225));
            jLabel6.setFont(new java.awt.Font("Arial", 1, 11));
            jLabel5.setVisible(false);
            panel1.add(jLabel6, null);
         }*/
        
        try {
            this.dsc = frame.experimentGraph.getExternalObjectDescription();
            all = parametersVector;
            
            parameterData = (Parameters) (parametersVector.elementAt(Layer.layerActivo));
            undoParameters = new Parameters((Parameters) (parametersVector.elementAt(Layer.layerActivo)));
            paramTable = new ParametersTable(undoParameters, this);
            /***************************************************************
             ***************  EDUCATIONAL KEEL  ***************************
             **************************************************************/
            if (Frame.buttonPressed == 1) //Button Teaching pressed
            {
                experiment = frame;
            }
            /***************************************************************
             ***************  EDUCATIONAL KEEL  ****************************
             **************************************************************/
            initParameters();
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Default builder
     */
    public ParametersDialog() {
        this(null, "", false, null, null);
    }

    private void initParameters() throws Exception {

        class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {

            JComponent component = new JTextField();

            // This method is called when a cell value is edited by the user.
            public Component getTableCellEditorComponent(
                    JTable table, Object value,
                    boolean isSelected,
                    int rowIndex, int vColIndex) {
                // 'value' is value contained in the cell located at (rowIndex, vColIndex)

                component = null;

                // colIndex is useful
                int selectedRow = 0;
                int n = parameterData.getNumParameters();
                int suma = -1;
                int pos = 0;
                while (pos < n) {
                    while (parameterData.isHidden(pos)) {
                        pos++;
                    }
                    suma++;
                    if (suma == rowIndex) {
                        break;
                    }
                    pos++;
                }
                selectedRow = pos;

                if (vColIndex == 1) {

                    if (parameterData.getParameterType(selectedRow).equalsIgnoreCase("list")) {

                        String[] valores = new String[parameterData.getDomain(selectedRow).size()];

                        int seleccionado = 0;
                        for (int i = 0; i < parameterData.getDomain(selectedRow).size(); i++) {
                            valores[i] = parameterData.getDomainValue(selectedRow, i);
                            if (value.equals(parameterData.getDomainValue(selectedRow, i))) {
                                seleccionado = i;
                            }
                        }

                        JComboBox tmp = new JComboBox(valores);
                        tmp.setSelectedIndex(seleccionado);
                        component = tmp;
                    } else {
                        component = new JTextField();
                        // Configure the component with the specified value
                        ((JTextField) component).setText((String) value);
                    }
                }

                // Return the configured component
                return component;
            }

            // This method is called when editing is completed.
            // It must return the new value to be stored in the cell.
            public Object getCellEditorValue() {

                if (component instanceof JComboBox) {
                    return ((JComboBox) component).getSelectedItem();
                } else {
                    return ((JTextField) component).getText();
                }
            }
        }

        jTable1 = new JTable(paramTable) {

            @Override
            public boolean isCellEditable(int rowIndex, int vColIndex) {
                if (vColIndex == 1) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        // For OSX
        jTable1.setRowHeight(30);

        // Install new editor in second row
        int vColIndex = 1;
        TableColumn col = jTable1.getColumnModel().getColumn(vColIndex);
        col.setCellEditor(new MyTableCellEditor());

        panel1.setLayout(null);
        jLabel1.setBackground(new Color(225, 225, 225));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 11));
        jLabel1.setForeground(Color.black);
        jLabel1.setText("Algorithm Name:");
        jLabel1.setBounds(new Rectangle(17, 15, 135, 22));
        jLabel2.setBackground(new Color(225, 225, 225));
        jLabel2.setFont(new java.awt.Font("Arial", 1, 11));
        jLabel2.setBorder(null);
        jLabel2.setText("Number of Executions:");
        jLabel2.setBounds(new Rectangle(17, 76, 136, 22));
        jLabel3.setBackground(new Color(225, 225, 225));
        jLabel3.setFont(new java.awt.Font("Arial", 1, 11));
        jLabel3.setText("Parameters:");
        jLabel3.setBounds(new Rectangle(17, 213, 90, 21));
        jButton1.setBackground(new Color(225, 225, 225));
        jButton1.setBounds(new Rectangle(75, 490, 100, 30));
        jButton1.setFont(new java.awt.Font("Arial", 0, 11));
        jButton1.setOpaque(false);
        jButton1.setToolTipText("Apply changes");
        jButton1.setIcon(image1);
        jButton1.setMnemonic('A');
        jButton1.setText("Apply");
        jButton1.addActionListener(new ParametrosDialog_jButton1_actionAdapter(this));
        jButton2.setText("Cancel");
        jButton2.addActionListener(new ParametrosDialog_jButton2_actionAdapter(this));
        jButton2.setBackground(new Color(225, 225, 225));
        jButton2.setBounds(new Rectangle(224, 490, 100, 30));
        jButton2.setFont(new java.awt.Font("Arial", 0, 11));
        jButton2.setOpaque(false);
        jButton2.setToolTipText("Don\'t apply the changes");
        jButton2.setIcon(image2);
        jButton2.setMnemonic('C');
        jScrollPane1.getViewport().setBackground(Color.white);
        jScrollPane1.setFont(new java.awt.Font("Arial", 0, 11));
        jScrollPane1.setBorder(BorderFactory.createEtchedBorder());
        jScrollPane1.setBounds(new Rectangle(17, 233, 359, 203));
        jLabel4.setBackground(new Color(225, 225, 225));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11));
        jLabel4.setBorder(null);
        jLabel4.setText(parameterData.getAlgorithmType());
        jLabel4.setBounds(new Rectangle(17, 37, 349, 22));
        jButton5.setBackground(new Color(225, 225, 225));
        jButton5.setBounds(new Rectangle(133, 449, 130, 30));
        jButton5.setFont(new java.awt.Font("Arial", 0, 11));
        jButton5.setOpaque(false);
        jButton5.setToolTipText("Restore default values");
        jButton5.setIcon(image3);
        jButton5.setMnemonic('V');
        jButton5.setText("Default Values");
        jButton5.addActionListener(new ParametrosDialog_jButton5_actionAdapter(this));
        jTable1.setBackground(SystemColor.white);
        jTable1.setFont(new java.awt.Font("Arial", 0, 11));
        opciones.addActionListener(new ParametrosDialog_opciones_actionAdapter(this));
        opciones.addFocusListener(new ParametrosDialog_opciones_focusAdapter(this));
        jSpinner1.setBounds(new Rectangle(17, 103, 78, 22));
        jSpinner1.setValue(new Integer(parameterData.getExe()));
        jSpinner1.setBackground(new Color(225, 225, 225));
        jSpinner1.setFont(new java.awt.Font("Arial", 0, 11));
        jSpinner1.setForeground(new Color(225, 225, 225));
        if (!parameterData.isProbabilistic()) {
            jSpinner1.setEnabled(false);
            jLabel2.setEnabled(false);
        }
            
            jComboBox1.setBounds(new Rectangle(184, 103, 185, 22));
            jComboBox1.addActionListener(new ParametrosDialog_jComboBox1_actionAdapter(this));
            String valores[] = dsc.getAllNames();
            for (int i = 0; i < valores.length; i++) {
            jComboBox1.addItem(valores[i]);
            }
            jComboBox1.addItem("All Datasets");
            jComboBox1.setFont(new java.awt.Font("Arial", 0, 11));
            jComboBox1.setSelectedIndex(valores.length);
        
        jLabel5.setBounds(new Rectangle(185, 76, 135, 22));
        jLabel5.setText("Applied to Dataset:");
        jLabel5.setForeground(Color.black);
        jLabel5.setBackground(new Color(225, 225, 225));
        jLabel5.setFont(new java.awt.Font("Arial", 1, 11));
        panel1.setBackground(new Color(225, 225, 225));
        panel1.setFont(new java.awt.Font("Arial", 0, 11));
        panel1.setOpaque(false);
        this.getContentPane().setBackground(new Color(225, 225, 225));
        this.setFont(new java.awt.Font("Arial", 0, 11));
        opciones.setBackground(new Color(225, 225, 225));
        opciones.setFont(new java.awt.Font("Arial", 0, 11));
        getContentPane().add(panel1);
        panel1.add(jLabel1, null);
        panel1.add(jButton2, null);
        panel1.add(jScrollPane1, null);
        panel1.add(jLabel4, null);
        panel1.add(jLabel3, null);
        panel1.add(jButton5, null);
        panel1.add(jButton1, null);
        panel1.add(jLabel2, null);
        panel1.add(jSpinner1, null);
        panel1.add(jComboBox1, null);
        panel1.add(jLabel5, null);

        jTable1.getTableHeader().setReorderingAllowed(false);
        TableColumn column = null;
        column = jTable1.getColumnModel().getColumn(0);
        column.setPreferredWidth(200);
        column = jTable1.getColumnModel().getColumn(1);
        column.setPreferredWidth(50);
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel rowSM = jTable1.getSelectionModel();

        /**
        rowSM.addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (!lsm.isSelectionEmpty()) {
        int selectedRow = lsm.getMinSelectionIndex();
        jTable1.changeSelection(selectedRow, 1, true, false);
        if (parameterData.getTipoParametro(selectedRow).equalsIgnoreCase("list")) {
        TableColumn column = jTable1.getColumnModel().getColumn(1);
        opciones.removeAllItems();
        for (int i = 0; i < parameterData.getDominio(selectedRow).size(); i++) {
        opciones.addItem(parameterData.getDominioValor(selectedRow, i));
        }
        column.setCellEditor(new DefaultCellEditor(opciones));
        } else {
        TableColumn column = jTable1.getColumnModel().getColumn(1);
        column.setCellEditor(new DefaultCellEditor(new JTextField()));
        }
        }
        }
        });
         */
        jScrollPane1.getViewport().add(jTable1, null);

    }

    /**
     * Hide button
     * @param e Event
     */
    void jButton2_actionPerformed(ActionEvent e) {
        this.setVisible(false);
    }

    /**
     * Apply parameters
     * @param e Event
     */
    void jButton1_actionPerformed(ActionEvent e) {
        jTable1.getColumnModel().getColumn(1).getCellEditor().stopCellEditing();
        Integer ejecuciones = (Integer) jSpinner1.getValue();
        if (jComboBox1.getSelectedIndex() == Layer.numLayers) {
            for (int i = 0; i < all.size(); i++) {
                ((Parameters) (all.elementAt(i))).copyParameters(undoParameters);
                ((Parameters) (all.elementAt(i))).setExe(ejecuciones.intValue());
            }
        } else {
            //Apply results in specified layer
            jTable1.getColumnModel().getColumn(1).getCellEditor().stopCellEditing();
            ejecuciones = (Integer) jSpinner1.getValue();
            parameterData.copyParameters(undoParameters);
            parameterData.setExe(ejecuciones.intValue());

            parameterData.copyParameters(undoParameters);
            parameterData.setExe(ejecuciones.intValue());

        }
        /***************************************************************
         ***************  EDUCATIONAL KEEL  ****************************
         **************************************************************/
        if (Frame.buttonPressed == 1) //Button Teaching pressed
        {
            //Incorrect parameters
            if (paramTable.getOutOfRange() == false) {
                //window of partitions is opened
                if (experiment.getExecDocentWindowState() == false) {
                    Object[] options = {"OK", "CANCEL"};
                    int n = JOptionPane.showOptionDialog(this, "The actual experiment is configured with others params. \n" +
                            "OK presses to STOP experiment and resume witch new configuration. \n", "Warning!",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                            null, options, options[0]);
                    if (n == JOptionPane.YES_OPTION) {
                        this.setVisible(false);
                        //Open new window of partitions and invoque "generarDirectorios" method
                        experiment.deleteExecDocentWindow();
                        experiment.closedEducationalExec(null);
                    //experiment.ejecutar_actionPerformed(null);
                    } else {
                        //Don�t anything
                    }
                } //Window of partitions closed
                else {
                    this.setVisible(false);
                }
            }
        } else //Button Experiments pressed
        {
            this.setVisible(false);
        }
    }//jButton1_actionPerformed

    /***************************************************************
     ***************  EDUCATIONAL KEEL  ****************************
     **************************************************************/
    class ParametrosDialog_jButton2_actionAdapter
            implements java.awt.event.ActionListener {

        ParametersDialog adaptee;

        ParametrosDialog_jButton2_actionAdapter(ParametersDialog adaptee) {
            this.adaptee = adaptee;
        }

        public void actionPerformed(ActionEvent e) {
            adaptee.jButton2_actionPerformed(e);
        }
    }

    class ParametrosDialog_jButton1_actionAdapter
            implements java.awt.event.ActionListener {

        ParametersDialog adaptee;

        ParametrosDialog_jButton1_actionAdapter(ParametersDialog adaptee) {
            this.adaptee = adaptee;
        }

        public void actionPerformed(ActionEvent e) {
            adaptee.jButton1_actionPerformed(e);
        }
    }

    /**
     * Default values button
     * @param e Event
     */
    void jButton5_actionPerformed(ActionEvent e) {
        int cont = 0;

        jTable1.getColumnModel().getColumn(1).getCellEditor().stopCellEditing(); //esto se hace para que funcione bien el poner valores por defecto

        undoParameters.setValues(parameterData.getDefaultValues());
        for (int i = 0; i < parameterData.getNumParameters(); i++) {
            if (!parameterData.isHidden(i)) {
                jTable1.setValueAt(undoParameters.getValue(i), cont, 1);
                cont++;
            }
        }
        jTable1.repaint();
    }

    /**
     * Options button
     * @param e Event
     */
    void opciones_actionPerformed(ActionEvent e) {
    }

    /**
     * Option focus
     * @param e Event
     */
    void opciones_focusLost(FocusEvent e) {
        jTable1.changeSelection(0, 1, true, false);
    }

    /**
     * Apply changes
     * @param e Event
     */
    void jComboBox1_actionPerformed(ActionEvent e) {
        int cont = 0;

        //Apply results in specified layer
        jTable1.getColumnModel().getColumn(1).getCellEditor().stopCellEditing();
        Integer ejecuciones = (Integer) jSpinner1.getValue();
        parameterData.copyParameters(undoParameters);
        parameterData.setExe(ejecuciones.intValue());

        //change layer
        if (jComboBox1.getSelectedIndex() < Layer.numLayers) {
            parameterData = (Parameters) (all.elementAt(jComboBox1.getSelectedIndex()));
            undoParameters.setValues(parameterData.getValues());
            for (int i = 0; i < parameterData.getNumParameters(); i++) {
                if (!parameterData.isHidden(i)) {
                    jTable1.setValueAt(undoParameters.getValue(i), cont, 1);
                    cont++;
                }
            }
            jTable1.repaint();
            jSpinner1.setValue(new Integer(parameterData.getExe()));
        }
    }
}

class ParametrosDialog_jButton5_actionAdapter
        implements java.awt.event.ActionListener {

    ParametersDialog adaptee;

    ParametrosDialog_jButton5_actionAdapter(ParametersDialog adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton5_actionPerformed(e);
    }
}

class ParametrosDialog_opciones_actionAdapter
        implements java.awt.event.ActionListener {

    ParametersDialog adaptee;

    ParametrosDialog_opciones_actionAdapter(ParametersDialog adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.opciones_actionPerformed(e);
    }
}

class ParametrosDialog_opciones_focusAdapter
        extends java.awt.event.FocusAdapter {

    ParametersDialog adaptee;

    ParametrosDialog_opciones_focusAdapter(ParametersDialog adaptee) {
        this.adaptee = adaptee;
    }
    @Override
    public void focusLost(FocusEvent e) {
        adaptee.opciones_focusLost(e);
    }
}

class ParametrosDialog_jComboBox1_actionAdapter implements java.awt.event.ActionListener {

    ParametersDialog adaptee;

    ParametrosDialog_jComboBox1_actionAdapter(ParametersDialog adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jComboBox1_actionPerformed(e);
    }
}
