package keel.GraphInterKeel.datacf.util;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Hashtable;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

/**
 * <p>
 * @author Written by Pedro Antonio Gutiérrez and Juan Carlos Fernández (University of Córdoba) 23/10/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
public class EachRowEditor implements TableCellEditor {

    /**
     * <p>
     * Class for editing rows in a dataset
     * </p>
     */

    /** Editors */
    protected Hashtable editors;

    /** TableCell editors */
    protected TableCellEditor editor, defaultEditor;

    /** JTable */
    JTable table;

    /**
     * <p>
     * Constructs a EachRowEditor. create default editor
     * </p>
     * @param table JTable for editor
     * @see TableCellEditor
     * @see DefaultCellEditor
     */
    public EachRowEditor(JTable table) {
        this.table = table;
        editors = new Hashtable();
        defaultEditor = new DefaultCellEditor(new JTextField());
    }

    /**
     * <p>
     * Set the editor in a row
     * </p>
     * @param row Table row
     * @param editor Table cell editor
     */
    public void setEditorAt(int row, TableCellEditor editor) {
        editors.put(new Integer(row), editor);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        //editor = (TableCellEditor)editors.get(new Integer(row));
        //if (editor == null) {
        //  editor = defaultEditor;
        //}
        return editor.getTableCellEditorComponent(table, value, isSelected,
                row, column);
    }

    /**
     * <p>
     * Returns the editor value
     * </p>
     * @return Object Editor value
     */
    @Override
    public Object getCellEditorValue() {
        return editor.getCellEditorValue();
    }

    /**
     * <p>
     * Stops the cell editor
     * </p>
     * @return boolean Has the editor stopped correctly?
     */
    @Override
    public boolean stopCellEditing() {
        return editor.stopCellEditing();
    }

    /**
     * <p>
     * Cancels the cell editing
     * </p>
     */
    @Override
    public void cancelCellEditing() {
        editor.cancelCellEditing();
    }

    /**
     * <p>
     * Is a cell editable?
     * </p>
     * @param anEvent Mouse event
     * @return boolean Is the cell editable?
     */
    @Override
    public boolean isCellEditable(EventObject anEvent) {
        selectEditor((MouseEvent) anEvent);
        return editor.isCellEditable(anEvent);
    }

    /**
     * <p>
     * Adds a cell editor listener
     * </p>
     * @param l New cell editor listener
     */
    @Override
    public void addCellEditorListener(CellEditorListener l) {
        editor.addCellEditorListener(l);
    }

    /**
     * <p>
     * Removes a cell editor listener
     * </p>
     * @param l Cell editor listener to remove
     */
    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        editor.removeCellEditorListener(l);
    }

    /**
     * <p>
     * Should a cell be selected
     * </p>
     * @param anEvent Cell editor listener to remove
     * @return boolean Should the cell be selected?
     */
    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        selectEditor((MouseEvent) anEvent);
        return editor.shouldSelectCell(anEvent);
    }

    /**
     * <p>
     * Establishes select editor
     * </p>
     * @param e Mouse event
     */
    protected void selectEditor(MouseEvent e) {
        int row;
        if (e == null) {
            row = table.getSelectionModel().getAnchorSelectionIndex();
        } else {
            row = table.rowAtPoint(e.getPoint());
        }
        editor = (TableCellEditor) editors.get(new Integer(row));
        if (editor == null) {
            editor = defaultEditor;
        }
    }
}
