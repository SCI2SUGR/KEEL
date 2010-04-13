package keel.GraphInterKeel.experiments;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * <p>Title: Keel</p>
 * <p>Description: Keel project environment</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Universidad de Granada</p>
 * @author V�ctor Manuel Gonz�lez Quevedo
 * @version 0.1
 */
public class KeelTreeCellRenderer extends DefaultTreeCellRenderer {

    /**
     * Default builder
     */
    public KeelTreeCellRenderer() {
        super();
        super.setBackgroundNonSelectionColor(this.getBackground());
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the base component of the tree
     * @param tree The tree.
     * @param value Value assigned to the component
     * @param sel True if the node is currently selected
     * @param expanded True if the tree is currently expanded
     * @param leaf True if the node is a leaf
     * @param row Index of the node
     * @param hasFocus True if the node has the focus
     * @return The base component of the tree
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component nodo = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        nodo.setBackground(this.getBackground());
        if (value.toString().charAt(0) != '(') {
        } else {
            nodo.setEnabled(false);
            if (hasFocus) {
                nodo.setBackground(Color.WHITE);
            }
        }

        return nodo;
    }

    private void jbInit() throws Exception {
        this.setFont(new java.awt.Font("Arial", 0, 11));
    }
}
