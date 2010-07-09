/**
 * File: statTableRenderer.java.
 *
 * Specific renderer for the data table
 *
 * @author Written by Joaquin Derrac (University of Granada) 29/04/2010
 * @version 1.0
 * @since JDK1.5
*/
package keel.GraphInterKeel.statistical;

import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

class statTableRenderer extends DefaultTableCellRenderer {

    DecimalFormat formatter;

    /**
     * Builder
     */
    public statTableRenderer() { 
        
        super();

        formatter = (DecimalFormat)DecimalFormat.getNumberInstance();

        formatter.setMinimumFractionDigits(1);
        formatter.setMaximumFractionDigits(10);

        formatter.setGroupingUsed(false);

    }

    /**
     * Formats a value on the cell
     *
     * @param value to set
     */
    public void setValue(Object value) {

        setText(formatter.format(value));
    }

    /**
     * Gets the cell renderer component used
     *
     * @param table Table to modify
     * @param value Value to represent
     * @param isSelected Tests if the cell is currently selected
     * @param hasFocus Tests if the cell has the focus
     * @param row Row selected
     * @param col Column selected
     *
     * @return Cell renderer component
     */
    public Component getTableCellRendererComponent(JTable table,
        Object value, boolean isSelected, boolean hasFocus,int row, int col) {

        String _formattedValue;

        if(col>0){

            Double _value = (Double)value;
            if (value == null) {
                _formattedValue = "0.0";
            } else {
                _formattedValue = formatter.format(_value);
            }
        }
        else {

            _formattedValue=(String)value;

        }

        JLabel testLabel = new JLabel(_formattedValue, SwingConstants.RIGHT);

        if (isSelected) {
            testLabel.setBackground(table.getSelectionBackground());
            testLabel.setOpaque(true);
            testLabel.setForeground(table.getSelectionForeground());
        }
        if (hasFocus) {
            testLabel.setForeground(table.getSelectionBackground());
            testLabel.setBackground(table.getSelectionForeground());
            testLabel.setOpaque(true);
        }

        return testLabel;

  }

}

