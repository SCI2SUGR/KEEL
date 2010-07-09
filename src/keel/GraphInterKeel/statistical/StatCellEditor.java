/**
 * File: statCellEditor.java.
 *
 * Specific cell editor for the data table
 *
 * @author Written by Joaquin Derrac (University of Granada) 29/04/2010
 * @version 1.0
 * @since JDK1.5
*/

package keel.GraphInterKeel.statistical;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;


public class StatCellEditor extends AbstractCellEditor implements TableCellEditor{

   private JTextField field;

   /**
    * Get the value of a cell
    *
    * @return Value stored
    */
    public Object getCellEditorValue() {
        return field.getText();

    }

    /**
     * Gets the cell editor component used
     *
     * @param table Table to modify
     * @param value Value to represent
     * @param isSelected Tests if the cell is currently selected
     * @param row Row selected
     * @param col Column selected
     *
     * @return Cell editor component
     */
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        field.setText(value + "");
        return field;
    }

}
