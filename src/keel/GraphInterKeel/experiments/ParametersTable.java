package keel.GraphInterKeel.experiments;
/*
 * <p>
 * ParametersTable.java
 *
 * @author Administrator
 * @author Modified by Juan Carlos Fernandez Caballero and Pedro Antonio Gutierrez (University of Córdoba) 7/07/2009
 * </p>
 */

import javax.swing.table.*;
import javax.swing.*;
import keel.GraphInterKeel.menu.Frame;

public class ParametersTable extends AbstractTableModel {

    /** parameters of the table */
    Parameters parameterData;

    /** header of the table */
    final String[] columnNames = {
        "Parameter descriptor", "Value"};

    /** data contained in the table*/
    Object[][] data;

    /** dialog of the table */
    JDialog container;
    /***************************************************************
     ***************  EDUCATIONAL KEEL  ****************************
     **************************************************************/
    private boolean outOfRange = false;

    /***************************************************************
     ***************  EDUCATIONAL KEEL  ****************************
     **************************************************************/
    public ParametersTable(Parameters params, JDialog parentDialog) {
        container = parentDialog;
        parameterData = params;
        data = new Object[parameterData.getNumParameters()][2];
        for (int i = 0; i < parameterData.getNumParameters(); i++) {
            data[i][0] = new String(parameterData.getDescriptions(i));
            data[i][1] = new String(parameterData.getValue(i));
        }
    }

    /***************************************************************
     ***************  EDUCATIONAL KEEL  ****************************
     **************************************************************/
    public boolean getOutOfRange() {
        return outOfRange;
    }

    /***************************************************************
     ***************  EDUCATIONAL KEEL  ****************************
     **************************************************************/

    /**
     * Gets the number of rows (i.e. the number of visible parameters)
     * @return the number of visible paramters
     */
    public int getRowCount() {
        // return parameterData.getNParametros();
        return parameterData.getNVisibleParams();
    }

    /**
     * Gets the number of columns
     * @return The number of columns is always 2
     */
    public int getColumnCount() {
        return 2;
    }

    /**
     * Get the specified column's name
     * @param col the index of the column
     * @return the name of the column
     */
    public String getColumnName(int col) {
        return columnNames[col];
    }

    /**
     * Returns the value of the parameter especified
     * @param rowIndex The row (i.e. the number of the paramter)
     * @param columnIndex The column we want to retrieve
     * @return the value of the parameter or its name
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        // return data[rowIndex][columnIndex];

        // colIndex is useful
        // rowIndex no:
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


        return data[pos][columnIndex];



    }

    /**
     * Check if the cell is editable
     * @param row The row of the cell
     * @param col The column index of the cell
     * @return true if the used can edit the cell, false otherwise
     */
    public boolean isCellEditable(int row, int col) {
        if (col == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Sets the value of the cell
     * @param value the new value
     * @param rowIndex the index of the row
     * @param col the index of the column
     */
    public void setValueAt(Object value, int rowIndex, int col) {
        Integer entero;
        ;
        Double real;
        boolean correcto = false;

        // Calculate the position of rowIndex, col in the table

        int row = -1;
        for (int i = 0; i <= rowIndex; i++) {
            row++;
            while (parameterData.isHidden(row)) {
                row++;
            }
        }




        if (parameterData.getParameterType(row).equalsIgnoreCase("integer")) {
            try {
                entero = new Integer((String) value);
                if (!parameterData.getDomain(row).isEmpty() &&
                        (entero.compareTo(new Integer(parameterData.getDomainValue(row, 0))) < 0 ||
                        entero.compareTo(new Integer(parameterData.getDomainValue(row, 1))) > 0)) {
                    JOptionPane.showMessageDialog(container,
                            "Please, insert a value in Domain [" +
                            parameterData.getDomainValue(row, 0) + ", " +
                            parameterData.getDomainValue(row, 1) + "]",
                            "Error", 1);
                    /***************************************************************
                     ***************  EDUCATIONAL KEEL  ****************************
                     **************************************************************/
                    if (Frame.buttonPressed == 1) //Button Teachig pressed
                    {
                        outOfRange = true;
                    }
                   /***************************************************************
                   ***************  EDUCATIONAL KEEL  ****************************
                   **************************************************************/
                    } else {
                    correcto = true;
                    /***************************************************************
                     ***************  EDUCATIONAL KEEL  ****************************
                     **************************************************************/
                    if (Frame.buttonPressed == 1) //Button Teaching pressed
                    {
                        outOfRange = false;
                    }
                   /***************************************************************
                   ***************  EDUCATIONAL KEEL  ****************************
                   **************************************************************/
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(container,
                        "Please, insert an Integer number",
                        "Error", 2);
                /***************************************************************
                 ***************  EDUCATIONAL KEEL  ****************************
                 **************************************************************/
                if (Frame.buttonPressed == 1) //Button Teaching pressed
                {
                    outOfRange = true;
                }
            /***************************************************************
             ***************  EDUCATIONAL KEEL  ****************************
             **************************************************************/
            }
        } else if (parameterData.getParameterType(row).equalsIgnoreCase("real")) {
            try {
                real = new Double((String) value);
                if (!parameterData.getDomain(row).isEmpty() &&
                        (real.compareTo(new Double(parameterData.getDomainValue(row, 0))) < 0 ||
                        real.compareTo(new Double(parameterData.getDomainValue(row, 1))) > 0)) {
                    JOptionPane.showMessageDialog(container,
                            "Please, insert a value in Domain [" +
                            parameterData.getDomainValue(row, 0) + ", " +
                            parameterData.getDomainValue(row, 1) + "]",
                            "Error", 1);
                    /***************************************************************
                     ***************  EDUCATIONAL KEEL  ****************************
                     **************************************************************/
                    if (Frame.buttonPressed == 1) //Button Teaching pressed
                    {
                        outOfRange = true;
                    }
                /***************************************************************
                 ***************  EDUCATIONAL KEEL  ****************************
                 **************************************************************/
                } else {
                    correcto = true;
                    /***************************************************************
                     ***************  EDUCATIONAL KEEL  ****************************
                     **************************************************************/
                    if (Frame.buttonPressed == 1) //Button Teaching pressed
                    {
                        outOfRange = false;
                    }
                /***************************************************************
                 ***************  EDUCATIONAL KEEL  ****************************
                 **************************************************************/
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(container,
                        "Please, insert an Floating point number",
                        "Error", 2);
                /***************************************************************
                 ***************  EDUCATIONAL KEEL  ****************************
                 **************************************************************/
                if (Frame.buttonPressed == 1) //Button Teaching pressed
                {
                    outOfRange = true;
                }
            /***************************************************************
             ***************  EDUCATIONAL KEEL  ****************************
             **************************************************************/
            }
        } else {
            correcto = true;
            /***************************************************************
             ***************  EDUCATIONAL KEEL  ****************************
             **************************************************************/
            if (Frame.buttonPressed == 1) //Button Teaching pressed
            {
                outOfRange = false;
            }
        /***************************************************************
         ***************  EDUCATIONAL KEEL  ****************************
         **************************************************************/
        }
        if (correcto) {
            data[row][col] = value;
            parameterData.setValue(row, (String) value);
        }
    }
}