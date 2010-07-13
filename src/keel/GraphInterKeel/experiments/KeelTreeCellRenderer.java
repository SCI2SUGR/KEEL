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

package keel.GraphInterKeel.experiments;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * <p>Title: Keel</p>
 * <p>Description: Keel project environment</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Universidad de Granada</p>
 * @author Vï¿½ctor Manuel Gonzï¿½lez Quevedo
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

