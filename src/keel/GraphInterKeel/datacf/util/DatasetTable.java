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

import javax.swing.table.*;
import javax.swing.*;

/**
 * <p>
 * @author Written Administrator
 * @author Modified by Pedro Antonio Guti√©rrez and Juan Carlos Fern√°ndez (University of C√≥rdoba) 23/10/2008
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

