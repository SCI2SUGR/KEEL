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

package keel.GraphInterKeel.datacf.util;

import java.util.Collections;
import keel.GraphInterKeel.datacf.editData.EditVariablePanel;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;

/**
 * <p>
 * @author Written by Pedro Antonio Guti√©rrez and Juan Carlos Fern√°ndez (University of C√≥rdoba) 23/10/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
public class VariableTable extends AbstractTableModel {

    /**
     * <p>
     * Implements a table for storing variables for a dataset
     * </p>
     */

    /** Data */
    private Dataset dataDataset;

    //String[] variableNames;

    /** Names of the columns */
    private String[] columnNames = {"Name", "Type", "Range", "Input/Output"};

    /** JPanel container */
    private JPanel container;

    /** dataObject array */
    private Object[][] dataObject;

    /**
     * <p>
     * Constructor
     * </p>
     * @param data Dataset
     * @param parent Parent frame
     */
    public VariableTable(Dataset data, JPanel parent) {
        this.container = parent;
        this.dataDataset = data;
        this.dataObject = new Object[dataDataset.getNVariables()][4];

        //Fills cells for jTable
        // Name, Ej: mcg
        for (int i = 0; i < this.dataDataset.getNVariables(); i++) {
            this.dataObject[i][0] = this.dataDataset.getAttributeIndex(i);
        }
        // Type, Ej: real
        for (int i = 0; i < this.dataDataset.getNVariables(); i++) {
            this.dataObject[i][1] = this.dataDataset.getAttributeTypeIndex(i);
        }
        // Range, Ej: [0.0][0.89]
        for (int i = 0; i < this.dataDataset.getNVariables(); i++) {
            if (this.dataDataset.getAttributeTypeIndex(i).equalsIgnoreCase("nominal")) {
                if (this.dataDataset.getRange(i).size() == 0) {
                    this.dataObject[i][2] = new String("");
                } else {
                    this.dataObject[i][2] = this.dataDataset.getRange(i).elementAt(0);
                }
            } else {
                this.dataObject[i][2] = this.dataDataset.getRange(i).toString();
            }
        }
        // Input/Output, Ej:Input
        for (int i = 0; i < this.dataDataset.getNVariables(); i++) {
            if (this.dataDataset.getInputs().contains(new String(data.getAttributeIndex(i)))) {
                this.dataObject[i][3] = new String("Input");
            }
            if (this.dataDataset.getOutputs().contains(new String(data.getAttributeIndex(i)))) {
                this.dataObject[i][3] = new String("Output");
            }
        }

    }

    /**
     * <p>
     * Gets the number of rows
     * </p>
     * @return int Number of rows
     */
    @Override
    public int getRowCount() {
        return this.dataObject.length;
    }

    /**
     * <p>
     * Gets the number of columns
     * </p>
     * @return int Number of rows
     */
    @Override
    public int getColumnCount() {
        return 4;
    }

    /**
     * <p>
     * Gets the name of a given column
     * </p>
     * @param col Index of the column
     * @return String Name of the column
     */
    @Override
    public String getColumnName(int col) {
        return this.columnNames[col];
    }

    /**
     * <p>
     * Gets the class of a given column
     * </p>
     * @param c Index of the column
     * @return Class of the column
     */
    @Override
    public Class getColumnClass(int c) {
        boolean parar = false;
        int i = 0;
        for (i = 0; i < dataObject.length && !parar; i++) {
            if (dataObject[i][c] != null) {
                parar = true;
            }
        }
        i--;
        if (parar == false) {
            return Object.class;
        }
        return getValueAt(i, c).getClass();
    }

    /**
     * <p>
     * Is a cell editable?
     * </p>
     * @param row Row of the cell
     * @param col Column of the cell
     * @return boolean Is the cell editable?
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        return true;
    }

    /**
     * <p>
     * Returns the value of a cell
     * </p>
     * @param rowIndex Row of the cell
     * @param columnIndex Column of the cell
     * @return Object Value of the cell
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return this.dataObject[rowIndex][columnIndex];
    }

    /**
     * <p>
     * Returns the data array
     * </p>
     * @return Object[][] Data array
     */
    public Object[][] getData() {
        return this.dataObject;
    }

    /**
     * <p>
     * Sets the data array
     * </p>
     * @param data Object[][] New data array
     */
    public void setData(Object[][] data) {
        this.dataObject = data;
    }

    /**
     * <p>
     * Sets the value of a cell
     * </p>
     * @param row Row of the cell
     * @param col Column of the cell
     *
     */
    @Override
    @SuppressWarnings("static-access")
    public void setValueAt(Object value, int row, int col) {
        if (value == null) {
            return;
        }
        Dataset dataset = (Dataset) ((EditVariablePanel) this.container).getEditDataPanel().getData();
        if (col == 0 && dataset.getAttributes().contains(value)) {
            JOptionPane.showMessageDialog(this.container, "There is a variable with the same name ", "Error", 2);
            return;
        }

        this.dataObject[row][col] = new String((String) value);
        //Modificacion de la tabla de dataDataset en el caso del Nombre, typo y Entrada/Salida
        //Los demas se modifican desde otro lugar.
        try {
            //Name
            if (col == 0) {
                String antigua = ((EditVariablePanel) this.container).getEditDataPanel().getTablaDataset().columnNames[row];

                Dataset aux = (Dataset) ((EditVariablePanel) this.container).getEditDataPanel().getData();
                if (aux.getOutputs().contains(antigua)) {
                    aux.getOutputs().setElementAt(value, aux.getOutputs().indexOf(antigua));
                    aux.getAttributes().setElementAt(value, aux.getAttributes().indexOf(antigua));
                }

                if (aux.getInputs().contains(antigua)) {
                    aux.getInputs().setElementAt(value, aux.getInputs().indexOf(antigua));
                    aux.getAttributes().setElementAt(value, aux.getAttributes().indexOf(antigua));
                }

                ((EditVariablePanel) this.container).refreshVariablePanel(aux);
                ((EditVariablePanel) this.container).getEditDataPanel().refreshDataPanel(aux);

            }
            //Type
            if (col == 1) {
                //Type change
                Dataset auxData = (Dataset) ((EditVariablePanel) this.container).getEditDataPanel().getData();

                if (!((String) value).equals(auxData.getAttributeTypeIndex(row))) {
                    //Unabled Buttons in EditVariablePanel
                    ((EditVariablePanel) this.container).enabledRanges(false);
                    //Real or nominal to integer
                    if (((String) value).equals("integer")) {
                        Vector aux;
                        Vector<String> aux2;
                        for (int i = 0; i < auxData.getDataVector().size(); i++) {
                            aux = auxData.getDataVector();
                            aux2 = ((Vector<String>) aux.get(i));
                            if (auxData.getAttributeTypeIndex(row).equals("real")) {
                                if (aux2.get(row) != null) {
                                    String cadena = aux2.get(row);
                                    int entero = (int) Math.round(Double.parseDouble(cadena));
                                    String myString = String.valueOf(entero);
                                    aux2.setElementAt(myString, row);
                                }
                            } else { //It was nominal, put 0
                                aux2.setElementAt("0", row);
                            }
                        }

                        Vector rangos = new Vector();
                        if (auxData.getAttributeTypeIndex(row).equals("real")) { //If was real, round
                            rangos.add(new Integer((int) ((Double) ((Vector) auxData.getRanges().get(row)).get(0)).doubleValue()));
                            rangos.add(new Integer((int) ((Double) ((Vector) auxData.getRanges().get(row)).get(1)).doubleValue()));
                        } else { //It was nominal, put range to [0,0]
                            rangos.add(new Integer(0));
                            rangos.add(new Integer(0));
                        }

                        auxData.getRanges().setElementAt(rangos, row);
                        auxData.getTypes().setElementAt(new String("integer"), row);
                        //Integer or nominal to real
                    } else if (((String) value).equals("real")) {
                        Vector aux;
                        Vector<String> aux2;
                        for (int i = 0; i < auxData.getDataVector().size(); i++) {
                            aux = auxData.getDataVector();
                            aux2 = ((Vector<String>) aux.get(i));
                            if (auxData.getAttributeTypeIndex(row).equals("integer")) {
                                String cadena = aux2.get(row);
                                double doble = Double.parseDouble(cadena);
                                String myString = Double.toString(doble);
                                aux2.setElementAt(myString, row);
                            } else { //It was nominal
                                aux2.setElementAt("0.0", row);
                            }
                        }

                        Vector rangos = new Vector();
                        if (auxData.getAttributeTypeIndex(row).equals("integer")) {
                            rangos.add(new Double((double) ((Integer) ((Vector) auxData.getRanges().get(row)).get(0)).intValue()));
                            rangos.add(new Double((double) ((Integer) ((Vector) auxData.getRanges().get(row)).get(1)).intValue()));
                        } else { //It was nominal
                            rangos.add(new Double(0.0));
                            rangos.add(new Double(0.0));
                        }

                        auxData.getRanges().setElementAt(rangos, row);
                        auxData.getTypes().setElementAt(new String("real"), row);
                    } else //Nominal. Interger or real to nominal
                    {
                        Vector aux;
                        Vector<String> aux2;
                        //Ranges of Integer to Nominal or Real to Nominal
                        Vector rangos = new Vector();
                        Vector rangosAux = new Vector();
                        //Procees the ranges and deleted the repeates. It is added one to one
                        boolean equal = false;
                        int contador = 0; //When all values are equal, we add a nominal
                        for (int i = 0; i < (auxData.getNData()) - 1; i++) {
                            equal = false;
                            for (int j = i + 1; j < auxData.getNData(); j++) {
                                if (auxData.getDataIndex(i, row).equals(auxData.getDataIndex(j, row))) {
                                    equal = true;
                                }
                            }
                            if (equal == false) {
                                rangosAux.add(auxData.getDataIndex(i, row));
                                contador = 1;
                            }
                        }
                        if (contador == 0) {
                            rangosAux.add(auxData.getDataIndex(0, row));
                        }

                        //Ordenamos los valores de rango
                        for (int i = 0; i < rangosAux.size(); i++) {
                            if (auxData.getAttributeTypeIndex(row).equals("integer")) {
                                rangosAux.set(i, Integer.parseInt((String) rangosAux.get(i)));
                            } else {
                                rangosAux.set(i, Double.parseDouble((String) rangosAux.get(i)));
                            }
                        }
                        Collections.sort(rangosAux);
                        for (int i = 0; i < rangosAux.size(); i++) {
                            rangosAux.set(i, String.valueOf(rangosAux.get(i)));
                        }

                        rangos.clear();
                        rangos.addAll(rangosAux);

                        auxData.getRanges().setElementAt(rangos, row);
                        auxData.getTypes().setElementAt(new String("nominal"), row);
                    }
                    ((EditVariablePanel) this.container).refreshVariablePanel(auxData);
                    ((EditVariablePanel) this.container).getEditDataPanel().refreshDataPanel(auxData);
                }
            }
            //Range
            if (col == 2) {
            }
            //Input/Output
            if (col == 3) {
                if (((String) value).equals("Input")) {
                    // Put to input
                    this.dataDataset.getOutputs().remove(this.dataDataset.getAttributeIndex(row));
                    this.dataDataset.getInputs().add(new String(this.dataDataset.getAttributeIndex(row)));
                    this.dataDataset.nOutputs--;
                    this.dataDataset.nInputs++;
                } else {
                    //Put to output
                    this.dataDataset.getInputs().remove(this.dataDataset.getAttributeIndex(row));
                    this.dataDataset.getOutputs().add(new String(this.dataDataset.getAttributeIndex(row)));
                    this.dataDataset.nOutputs++;
                    this.dataDataset.nInputs--;
                }
            }
        } catch (NumberFormatException ex) {
        }
    }
}

