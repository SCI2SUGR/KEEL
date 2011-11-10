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

import java.sql.*;
import java.util.*;
import javax.swing.table.*;

/**
 * <p>
 * @author Written by Administrator
 * @author Modified by Pedro Antonio Guti√©rrez and Juan Carlos Fern√°ndez (University of C√≥rdoba) 23/10/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
public class AttributeTable extends AbstractTableModel {

    /**
     * <p>
     * Class representing the table of attributes
     * </p>
     */

    /** Data of the table */
    private Vector m_data;

    /** Names of the columns of the table */
    private String[] m_columnNames;

    /** Attributes of the table */
    private Vector attributes;

    /**
     * <p>
     * Constructor
     * </p>
     * @param p_columns Columns names vector
     * @param p_defaultv Desfault object values for each column
     * @param p_rows Row number (initial)
     */
    public AttributeTable(String p_columns[], Object p_defaultv[], int p_rows) {

        m_columnNames = new String[p_columns.length];
        for (int i = 0; i < p_columns.length; i++) {
            m_columnNames[i] = new String(p_columns[i]);
        }
        m_data = new Vector();
        for (int i = 0; i < p_rows; i++) {
            Vector l_cols = new Vector();
            for (int j = 0; j < p_columns.length; j++) {
                l_cols.addElement(p_defaultv[j]);
            }
            m_data.addElement(l_cols);
        }

        attributes = new Vector();
    }

    /**
     * <p>
     * Fills data in the table with a ResultSet
     * </p>
     * 
     * @param p_rset ResultSet
     */
    public void populateFromResultSet(ResultSet p_rset) {
        // Create a new instance for data vector
        m_data = new Vector();

        try {
            // Navigate along ResultSet
            while (p_rset.next()) { // obtiene la siguiente fila del ResultSet
                Vector l_cols = new Vector();
                // Examine each column and store values in a row vector
                for (int i = 0; i < m_columnNames.length; i++) {
                    l_cols.addElement(p_rset.getObject(i + 1)); // Column value (i+1)
                }
                m_data.addElement(l_cols); // Add row vector
            }
            p_rset.close(); // Close ResultSet
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        super.fireTableDataChanged();
    }

    /**
     * <p>
     * Returns the number of columns
     * </p>
     * @return int Number of columns
     */
    @Override
    public int getColumnCount() {
        return m_columnNames.length;
    }


    /**
     * <p>
     * Returns the number of rows
     * </p>
     * @return int Number of rows
     */
    @Override
    public int getRowCount() {
        return m_data.size();
    }

    /**
     * <p>
     * Returns the name of a given column
     * </p>
     * @param p_col Index of the row
     * @return String Name of row
     */
    @Override
    public String getColumnName(int p_col) {
        return m_columnNames[p_col];
    }

    /**
     * <p>
     * Returns the value of an element of the table
     * </p>
     * @param p_row Index of the row of the element
     * @param p_col Index of the column of the element
     * @return Object Value of the element
     */
    @Override
    public Object getValueAt(int p_row, int p_col) {
        Vector l_colvector = (Vector) m_data.elementAt(p_row);
        return l_colvector.elementAt(p_col);
    }

    /**
     * <p>
     * Returns the class of the elements of a column
     * </p>
     * @param p_col Index of the column
     * @return Class of the elements of the column
     */
    @Override
    public Class getColumnClass(int p_col) {
        return getValueAt(0, p_col).getClass();
    }

    /**
     * <p>
     * Sets the value of an element of the table
     * </p>
     * @param p_obj New value
     * @param p_row Index of the row of the element
     * @param p_col Index of the column of the element
     */
    @Override
    public void setValueAt(Object p_obj, int p_row, int p_col) {
        Vector l_colvector = (Vector) m_data.elementAt(p_row);
        l_colvector.setElementAt(p_obj, p_col);
    }

    /**
     * <p>
     * Inserts a row in the table
     * </p>
     * @param p_newrow Vector containing the values of the new row
     * @param a Attribute corresponding to the new row
     */
    public void insertRow(Vector p_newrow, Attribute a) {
        m_data.addElement(p_newrow);
        attributes.addElement(a);
        super.fireTableDataChanged();
    }

    /**
     * <p>
     * Deletes a row in the table
     * </p>
     * @param p_row Index of the row to delete
     */
    public void deleteRow(int p_row) {
        m_data.removeElementAt(p_row);
        attributes.removeElementAt(p_row);
        super.fireTableDataChanged();
    }

    /**
     * <p>
     * Returns a row of the table as a vector
     * </p>
     * @param p_row Index of the row
     * @return Vector Row of the table as a vector
     */
    public Vector getRow(int p_row) {
        return (Vector) m_data.elementAt(p_row);
    }

    /**
     * <p>
     * Updates a row in the table using a vector with its new values
     * </p>
     * @param p_updatedRow Vector containing the new values of the row
     * @param p_row Index of the row
     * @param a Attribute corresponding to the row
     */
    public void updateRow(Vector p_updatedRow, int p_row, Attribute a) {
        m_data.setElementAt(p_updatedRow, p_row);
        attributes.setElementAt(a, p_row);
        super.fireTableDataChanged();
    }

    /**
     * <p>
     * Clears the table
     * </p>
     */
    public void clearTable() {
        m_data = new Vector();
        attributes = new Vector();
        super.fireTableDataChanged();
    }

    /**
     * <p>
     * Returns the attribute of a row
     * </p>
     * @param p_row Index of the row
     * @return Attribute Attribute of the row
     */
    public Attribute getAtributo(int p_row) {
        return (Attribute) attributes.elementAt(p_row);
    }
}

