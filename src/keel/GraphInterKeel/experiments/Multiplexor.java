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

/**
 *
 * File: Multiplexor.java
 *
 * Multiplexor nodes
 *
 * @author Written by Admin 4/8/2009
 * @author Modified Ana Palacios Jimenez and Luciano Sanchez Ramons 23-4-2010 (University of Oviedo)
 * @version 1.0
 * @since JDK1.5
 */

package keel.GraphInterKeel.experiments;

import java.awt.*;
import java.awt.geom.*;
import java.util.Vector;


public final class Multiplexor extends Node {

    protected transient Vector inputs;

    /**
     * Builder
     * @param position Position in the graph
     * @param p Graph
     */
    public Multiplexor(Point position, GraphPanel p) {
        super(new ExternalObjectDescription("Multiplexor", null, Node.type_Undefined), position, p.mainGraph.getId());
        p.mainGraph.setId(p.mainGraph.getId() + 1);
        type = type_Multiplexor;
        image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/multiplex.gif"));
        pd = p;
        inputs = new Vector();
    }

    /**
     * Builder
     * @param subtipo Subtype of the node
     * @param position Position in the graph
     * @param p Graph
     * @param id Node id
     */
    public Multiplexor(int subtipo, Point position, GraphPanel p, int id) {
        super(new ExternalObjectDescription("Multiplexor", null, subtipo), position, id);
        type = type_Multiplexor;
        image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/multiplex.gif"));
        pd = p;
        inputs = new Vector();
    }

    /**
     * Show dialog
     */
    public void showDialog() {
    }

    /**
     * Contain method
     * @param title Frame title
     * @param show Whether to show or not
     * @param n Id of the node
     * @param exp Parent frame
     */
    public void contain(String title,int show,Node n,Experiments exp) {
    }
    public void draw(Graphics2D g2, boolean select) {
        Point pinit = new Point(centre.x - 25, centre.y - 25);
        Point pfin = new Point(centre.x + 25, centre.y + 25);
        figure = new RoundRectangle2D.Float(pinit.x, pinit.y,
                Math.abs(pfin.x - pinit.x),
                Math.abs(pfin.y - pinit.y), 20, 20);

        g2.setColor(Color.black);
        if (select) {
            Stroke s = g2.getStroke();
            g2.setStroke(new BasicStroke(5, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_BEVEL, 0, new float[]{1, 1}, 0));
            g2.draw(figure);
            g2.setStroke(s);
        } else {
            g2.draw(figure);

        }
        g2.drawImage(image, centre.x - 25, centre.y - 25, 50, 50, pd);
    }

    /**
     * Add input to the multiplexor
     * @param node Id of the node
     */
    public void addInput(int node) {
        // insert id node as an input
        if (dsc.getSubtype() == type_Undefined) {
            dsc.setSubtype(pd.mainGraph.getNodeAt(node).dsc.getSubtype());
        }
        inputs.addElement(new Integer(pd.mainGraph.getNodeAt(node).id));
    }

    /**
     * Remove input from the multiplexor
     * @param node Id of the node
     * @param multi Id of the multiplexor
     */
    public void removeInput(int node, int multi) {
        // remove id node as an input
        inputs.removeElement(new Integer(pd.mainGraph.getNodeAt(node).id));
        if (inputs.isEmpty()) {
            dsc.setSubtype(type_Undefined);
            // remove output connections
            for (int i = pd.mainGraph.numArcs() - 1; i >= 0; i--) {
                if (pd.mainGraph.getArcAt(i).getSource() == multi) {
                    pd.mainGraph.dropArc(i);
                }
            }
        }
    }

    /**
     * Gets input
     * @return Vector with the inputs
     */
    public Vector getInputs() {
        // return input nodes
        Vector e = new Vector();
        for (int i = 0; i < inputs.size(); i++) {
            Integer n = (Integer) inputs.elementAt(i);
            boolean para = false;
            for (int j = 0; j < pd.mainGraph.numNodes() && !para; j++) {
                if (pd.mainGraph.getNodeAt(j).id == n.intValue()) {
                    if (pd.mainGraph.getNodeAt(j).type != type_Multiplexor) {
                        e.addElement(new Integer(j));
                    } else {
                        // chain multiplexors
                        Vector tmp = new Vector();
                        tmp = (Vector) ((Multiplexor) pd.mainGraph.getNodeAt(j)).getInputs().clone();
                        for (int k = 0; k < tmp.size(); k++) {
                            e.addElement(tmp.elementAt(k));
                        }
                    }
                    para = true;
                }
            }
        }
        return e;
    }
}
