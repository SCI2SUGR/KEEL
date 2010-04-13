package keel.GraphInterKeel.datacf.util;

import javax.swing.table.*;
import javax.swing.*;

/**
 * <p>
 * @author Written Administrator
 * @author Modified by Pedro Antonio Gutiérrez and Juan Carlos Fernández (University of Córdoba) 23/10/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
public class DatasetTable extends AbstractTableModel {

    /**
     * Class for store a dataset in a table
     */
    Dataset datos;
    String[] columnNames;
    Object[][] data;
    JPanel contenedor;

    /**
     * <p>
     * Constructor
     * </p>
     * @param data Dataset
     * @param padre Jpanel parent
     */
    public DatasetTable(Dataset data, JPanel padre) {
        int i, j;

        contenedor = padre;
        datos = data;
        columnNames = new String[datos.getNVariables()];
        for (i = 0; i < columnNames.length; i++) {
            columnNames[i] = new String(datos.getAttributeIndex(i));
        }
        this.data = new Object[datos.getNData()][datos.getNVariables()];
        for (i = 0; i < this.data.length; i++) {
            for (j = 0; j < this.data[i].length; j++) {
                try {
                    if (datos.getAttributeTypeIndex(j).equalsIgnoreCase("nominal")) {
                        this.data[i][j] = new String(datos.getDataIndex(i, j));
                    } else if (datos.getAttributeTypeIndex(j).equalsIgnoreCase("integer")) {
                        this.data[i][j] = new String(datos.getDataIndex(i, j));
                    } else {
                        this.data[i][j] = new String(datos.getDataIndex(i, j));
                    }
                } catch (NumberFormatException ex) {
                    this.data[i][j] = null;
                } catch (NullPointerException ex) {
                    this.data[i][j] = "<null>";
                }
            }
        }
    }

    /**
     * <p>
     * Get Data
     * </p>
     * @return Data
     */
    public Object[][] getData() {
        return this.data;
    }

    /**
     * <p>
     * Sets Data
     * </p>
     * @param data Data
     */
    public void setData(Object[][] data) {
        this.data = data;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    /**
     * <p>
     *  Return columnNames
     * </p>
     * @return String[] columnNames
     */
    public String[] getColumnNames() {
        return columnNames;
    }

    /**
     * <p>
     * Get datos
     * </p>
     * @return datos Dataset
     */
    public Dataset getDatos() {
        return this.datos;
    }

    /**
     * <p>
     * Set datos
     * </p>
     * @param datos Dataset
     */
    public void setDatos(Dataset datos) {
        this.datos = datos;
    }

    /**
     * <p>
     *  Set Column Names
     * </p>
     * @param columnNames Column names
     */
    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    public Class getColumnClass(int c) {
        boolean parar = false;
        int i = 0;
        for (i = 0; i < data.length && !parar; i++) {
            if (data[i][c] != null) {
                parar = true;
            }
        }
        i--;
        return getValueAt(i, c).getClass();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return true;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        try {
            if (datos.getAttributeTypeIndex(col).equalsIgnoreCase("nominal")) {
                data[row][col] = new String((String) value);
            } else if (datos.getAttributeTypeIndex(col).equalsIgnoreCase("integer")) {
                try {
                    if (Integer.parseInt((String) value) >= datos.getRangesInt(col, 0).intValue() && Integer.parseInt((String) value) <= datos.getRangesInt(col, 1).intValue()) {
                        data[row][col] = new String((String) value);
                    } else {
                        JOptionPane.showMessageDialog(contenedor,
                                "Please, insert a value in Domain [" +
                                datos.getRangesInt(col, 0).toString() + ", " +
                                datos.getRangesInt(col, 1).toString() + "]",
                                "Error", 1);
                    }
                } catch (NumberFormatException ex) {
                    data[row][col] = "<null>";
                }
            } else {
                try {
                    if (Double.parseDouble((String) value) >= datos.getRangesReal(col, 0).doubleValue() && Double.parseDouble((String) value) <= datos.getRangesReal(col, 1).doubleValue()) {
                        data[row][col] = new String((String) value);
                    } else {
                        JOptionPane.showMessageDialog(contenedor,
                                "Please, insert a value in Domain [" +
                                datos.getRangesReal(col, 0).toString() + ", " +
                                datos.getRangesReal(col, 1).toString() + "]",
                                "Error", 1);
                    }
                } catch (NumberFormatException ex) {
                    data[row][col] = "<null>";
                }
            }
        } catch (NullPointerException ex) {
            data[row][col] = null;
        }
    }
}
