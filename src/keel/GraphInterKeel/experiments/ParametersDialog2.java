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
 * File ParametersDialog2.java
 *
 * Created on 02-mar-2009, 5:30:48
 * Modified on 12-may-2009
 * @author Ignacio Robles
 * @author Julian Luengo
 * @author Modified by Juan Carlos Fernandez Caballero and Pedro Antonio Gutierrez (University of Córdoba) 7/07/2009
 * @authos Modified Ana Palacios Jimenez and Luciano Sanchez Ramos 23-4-2010 (University of Oviedo)
 */
package keel.GraphInterKeel.experiments;

// Called from DialogUsuario
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.table.*;

public class ParametersDialog2 extends JDialog {

    JPanel panel1 = new JPanel() {

        @Override
        public void paintComponent(Graphics g) {
            //ImageIcon img = new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/experimentos/parametros.jpg"));
            //g.drawImage(img.getImage(), 0, 0, null);
            super.paintComponent(g);
        }
    };
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
    ParametersTable parametersTable;
    JComboBox options = new JComboBox();
    JButton jButton5 = new JButton();
    SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 1000, 1);
    JSpinner jSpinner1 = new JSpinner(model);
    JLabel jLabel5 = new JLabel();
    JComboBox function = new JComboBox();
    ImageIcon image1 = new ImageIcon(this.getClass().getResource(
            "/keel/GraphInterKeel/resources/ico/experiments/aceptar.gif"));
    ImageIcon image2 = new ImageIcon(this.getClass().getResource(
            "/keel/GraphInterKeel/resources/ico/experiments/cancelar.gif"));
    ImageIcon image3 = new ImageIcon(this.getClass().getResource(
            "/keel/GraphInterKeel/resources/ico/experiments/deshacer.gif"));
    UserMethod mu;

    /**
     * Builder
     * @param frame Parent frame
     * @param title Title of the frame
     * @param modal Modal status
     * @param mu User method
     */
    public ParametersDialog2(Frame frame, String title, boolean modal, UserMethod mu) {
        super(frame, title, modal);
        try {
            this.mu = mu;
            parameterData = mu.parametersUser;
            undoParameters = new Parameters(mu.parametersUser);
            parametersTable = new ParametersTable(undoParameters, this);
            initParams();
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Default builder
     */
    public ParametersDialog2() {
        this(null, "", false, null);
    }

    /**
     * Initialize
     * @throws java.lang.Exception
     */
    private void initParams() throws Exception {

        class MyTableCellEditor
                extends AbstractCellEditor
                implements TableCellEditor {

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

        jTable1 = new JTable(parametersTable) {

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
        jLabel2.setText("Number of executions:");
        jLabel2.setBounds(new Rectangle(17, 137, 136, 22));
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
        jButton1.addActionListener(new ParametrosDialog2_jButton1_actionAdapter(this));
        jButton1.addActionListener(new ParametrosDialog2_jButton1_actionAdapter(this));
        jButton2.setText("Cancel");
        jButton2.addActionListener(new ParametrosDialog2_jButton2_actionAdapter(this));
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
        jButton5.addActionListener(new ParametrosDialog2_jButton5_actionAdapter(this));
        jTable1.setBackground(SystemColor.white);
        jTable1.setFont(new java.awt.Font("Arial", 0, 11));
        options.addActionListener(new ParametrosDialog2_opciones_actionAdapter(this));
        options.addFocusListener(new ParametrosDialog2_opciones_focusAdapter(this));
        jSpinner1.setBounds(new Rectangle(17, 164, 78, 22));
        jSpinner1.setValue(new Integer(parameterData.getExe()));
        jSpinner1.setBackground(new Color(225, 225, 225));
        jSpinner1.setFont(new java.awt.Font("Arial", 0, 11));
        jSpinner1.setForeground(new Color(225, 225, 225));
        if (!parameterData.isProbabilistic()) {
            jSpinner1.setEnabled(false);
            jLabel2.setEnabled(false);
        }
        jLabel5.setBounds(new Rectangle(17, 68, 135, 22));
        jLabel5.setText("Algorithm Function:");
        jLabel5.setForeground(Color.black);
        jLabel5.setBackground(new Color(225, 225, 225));
        jLabel5.setFont(new java.awt.Font("Arial", 1, 11));
        function.setBounds(new Rectangle(17, 99, 155, 22));
        function.addItem("Pre-Process");
        function.addItem("Method");
        function.addItem("Post-Process");
        function.addItem("Test");
        function.setFont(new java.awt.Font("Arial", 0, 11));
        function.setEditable(false);
        switch (mu.dsc.getSubtype()) {
            case Node.type_Preprocess:
                function.setSelectedIndex(0);
                break;
            case Node.type_Method:
                function.setSelectedIndex(1);
                break;
            case Node.type_Postprocess:
                function.setSelectedIndex(2);
                break;
            case Node.type_Test:
                function.setSelectedIndex(3);
                break;
        }
        panel1.setBackground(new Color(225, 225, 225));
        panel1.setEnabled(true);
        panel1.setFont(new java.awt.Font("Arial", 0, 11));
        panel1.setOpaque(false);
        this.getContentPane().setBackground(new Color(225, 225, 225));
        this.setFont(new java.awt.Font("Arial", 0, 11));
        options.setFont(new java.awt.Font("Arial", 0, 11));
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
        panel1.add(jLabel5, null);
        panel1.add(function, null);

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
        options.removeAllItems();
        // If data domain only contains a value,
        // user can't modify it
        if (parameterData.getDominio(selectedRow).size() == 1) {
        System.out.println("Data not modifiable");
        }

        for (int i = 0; i < parameterData.getDominio(selectedRow).size(); i++) {
        options.addItem(parameterData.getDominioValor(selectedRow, i));
        }

        column.setCellEditor(new DefaultCellEditor(options));
        }
        else {
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
        parameterData.copyParameters(undoParameters);
        Integer ejecuciones = (Integer) jSpinner1.getValue();
        parameterData.setExe(ejecuciones.intValue());
        int subtipo_anterior = mu.dsc.getSubtype();
        switch (function.getSelectedIndex()) {
            case 0:
                mu.dsc.setSubtype(Node.type_Preprocess);
                break;
            case 1:
                mu.dsc.setSubtype(Node.type_Method);
                break;
            case 2:
                mu.dsc.setSubtype(Node.type_Postprocess);
                break;
            case 3:
                mu.dsc.setSubtype(Node.type_Test);
                break;
        }

        // If data type has changed, remove some connections in graph
        if (subtipo_anterior != mu.dsc.getSubtype()) {
            // Search node
            int yo = 0;
            boolean enc = false;
            for (int i = 0; i < mu.pd.mainGraph.numNodes() && !enc; i++) {
                if (mu.pd.mainGraph.getNodeAt(i).type == Node.type_userMethod) {
                    if (mu.equals((UserMethod) mu.pd.mainGraph.getNodeAt(i))) {
                        yo = i;
                        enc = true;
                    }
                }
            }

            if (mu.dsc.getSubtype() == Node.type_Preprocess) {
                // Remove some connection inputs
                for (int i = mu.pd.mainGraph.numArcs() - 1; i >= 0; i--) {
                    Arc a = mu.pd.mainGraph.getArcAt(i);
                    if (a.getDestination() == yo) {
                        if ((mu.pd.mainGraph.getNodeAt(a.getSource()).dsc.getSubtype() !=
                                Node.type_Dataset) &&
                                (mu.pd.mainGraph.getNodeAt(a.getSource()).dsc.getSubtype() !=
                                Node.type_Preprocess)) {
                            mu.pd.mainGraph.dropArc(i);
                        }
                    }
                }
            } else if (mu.dsc.getSubtype() == Node.type_Test || mu.dsc.getSubtype() == Node.type_Visor) {
                // Remove some connection inputs and outputs
                for (int i = mu.pd.mainGraph.numArcs() - 1; i >= 0; i--) {
                    Arc a = mu.pd.mainGraph.getArcAt(i);
                    if (a.getSource() == yo) {
                        mu.pd.mainGraph.dropArc(i);
                    } else if (a.getDestination() == yo) {
                        if ((mu.pd.mainGraph.getNodeAt(a.getSource()).dsc.getSubtype() !=
                                Node.type_Method) &&
                                (mu.pd.mainGraph.getNodeAt(a.getSource()).dsc.getSubtype() !=
                                Node.type_Postprocess)) {
                            mu.pd.mainGraph.dropArc(i);
                        }
                    }
                }
            } else {
                // Remove some connection outputs
                for (int i = mu.pd.mainGraph.numArcs() - 1; i >= 0; i--) {
                    Arc a = mu.pd.mainGraph.getArcAt(i);
                    if (a.getSource() == yo) {
                        if ((mu.pd.mainGraph.getNodeAt(a.getDestination()).dsc.getSubtype() ==
                                Node.type_Dataset) ||
                                (mu.pd.mainGraph.getNodeAt(a.getDestination()).dsc.getSubtype() ==
                                Node.type_Preprocess)) {
                            mu.pd.mainGraph.dropArc(i);
                        }
                    }
                }
            }
        }

        this.setVisible(false);
    }

    class ParametrosDialog2_jButton2_actionAdapter
            implements java.awt.event.ActionListener {

        ParametersDialog2 adaptee;

        ParametrosDialog2_jButton2_actionAdapter(ParametersDialog2 adaptee) {
            this.adaptee = adaptee;
        }

        public void actionPerformed(ActionEvent e) {
            adaptee.jButton2_actionPerformed(e);
        }
    }

    class ParametrosDialog2_jButton1_actionAdapter
            implements java.awt.event.ActionListener {

        ParametersDialog2 adaptee;

        ParametrosDialog2_jButton1_actionAdapter(ParametersDialog2 adaptee) {
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
        undoParameters.setValues(parameterData.getDefaultValues());
        for (int i = 0; i < parameterData.getNumParameters(); i++) {
            jTable1.setValueAt(undoParameters.getValue(i), i, 1);
        }
        jTable1.repaint();
    }

    void opciones_actionPerformed(ActionEvent e) {
    }

    void opciones_focusLost(FocusEvent e) {
        jTable1.changeSelection(0, 1, true, false);
    }
}

class ParametrosDialog2_jButton5_actionAdapter
        implements java.awt.event.ActionListener {

    ParametersDialog2 adaptee;

    ParametrosDialog2_jButton5_actionAdapter(ParametersDialog2 adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton5_actionPerformed(e);
    }
}

class ParametrosDialog2_opciones_actionAdapter
        implements java.awt.event.ActionListener {

    ParametersDialog2 adaptee;

    ParametrosDialog2_opciones_actionAdapter(ParametersDialog2 adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.opciones_actionPerformed(e);
    }
}

class ParametrosDialog2_opciones_focusAdapter
        extends java.awt.event.FocusAdapter {

    ParametersDialog2 adaptee;

    ParametrosDialog2_opciones_focusAdapter(ParametersDialog2 adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void focusLost(FocusEvent e) {
        adaptee.opciones_focusLost(e);
    }
}

class ParametrosDialog2_jButton1_actionAdapter
        implements java.awt.event.ActionListener {

    ParametersDialog2 adaptee;

    ParametrosDialog2_jButton1_actionAdapter(ParametersDialog2 adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton1_actionPerformed(e);
    }
}
