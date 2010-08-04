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
 * File: Algorithm.java
 *
 * A class for managing algorithms
 *
 * @author Written by Admin 4/8/2009
 * @author Modified Joaquin Derrac 20-5-2010
 * @author Modified Amelia Zafra 28-6-2010
 * @version 1.0
 * @since JDK1.5
 */
package keel.GraphInterKeel.experiments;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public final class Algorithm extends Node {

    //Julian - Now the 'par' vector is in nodo
    //so it is inherited by both Test and Algorithm classes
    //public Vector par;
    /**
     * Builder
     */
    public Algorithm() {
        super();
    }

    /**
     * Builder
     * @param dsc Dsc parent
     * @param position Position in the graph
     * @param p Parent graph
     */
    public Algorithm(ExternalObjectDescription dsc, Point position, GraphPanel p) {

        super(dsc, position, p.mainGraph.getId());

        /*System.out.println("Building ALGORITHM with " +
        " name = " + dsc.enumerateNames() +
        " path = " + dsc.getPath() +
        " subtype = " + dsc.getSubtype() +
        " jarname= " + dsc.getJarName()
        );*/

        actInputOutput(dsc, p);

        p.mainGraph.setId(p.mainGraph.getId() + 1);
        type = type_Algorithm;
        //this.subtipo = subtipo;
        // System.out.println ("el subtype es "+dsc.getSubtypelqd());
        if (dsc.getSubtype() == type_Preprocess) {

            if (dsc.getSubtypelqd() != CRISP) {
                image = Toolkit.getDefaultToolkit().getImage(
                        this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/preprocessLQD.gif"));
                if (dsc.getSubtypelqd() == LQD) {
                    type_lqd = LQD;
                } else {
                    type_lqd = CRISP2;
                }
            } else {
                image = Toolkit.getDefaultToolkit().getImage(
                        this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/preprocess.gif"));
            }
        } else if (dsc.getSubtype() == type_Postprocess) {
            if (dsc.getSubtypelqd() != CRISP) {
                image = Toolkit.getDefaultToolkit().getImage(
                        this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/postprocessLQD.gif"));
                if (dsc.getSubtypelqd() == LQD) {
                    type_lqd = LQD;
                } else {
                    type_lqd = CRISP2;
                }
            } else {
                image = Toolkit.getDefaultToolkit().getImage(
                        this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/postprocess.gif"));
            }
        } else if (dsc.getSubtype() == type_Method) {
            if (dsc.getSubtypelqd() != CRISP) {
                image = Toolkit.getDefaultToolkit().getImage(
                        this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/method_nodeLQD.gif"));
                if (dsc.getSubtypelqd() == LQD) {
                    type_lqd = LQD;
                } else {
                    type_lqd = CRISP2;
                }
            } else {
                image = Toolkit.getDefaultToolkit().getImage(
                        this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/method_node.gif"));
            }

        }
        pd = p;

        // search pattern file
        par = new Vector();
        if (dsc.getSubtypelqd() == CRISP) {
            for (int i = 0; i < Layer.numLayers; i++) {
                par.addElement(new Parameters(dsc.getPath(i) + dsc.getName(i) + ".xml", false));
            //System.out.println(dsc.getPath(i) + dsc.getName(i) + ".xml");
            }
        } else {
            par.addElement(new Parameters(dsc.getPath(0) + dsc.getName(0) + ".xml", false));
            System.out.println(dsc.getPath(0) + dsc.getName(0) + ".xml");
        }


    }

    /**
     * Builder
     *
     * @param dsc Dsc parent
     * @param position Position in the graph
     * @param p Parent graph
     * @param vparameters Actual parameters
     * @param id Id of the node
     * @param lqd LQD pertenence
     * @param join Arcs of the node
     */
    public Algorithm(ExternalObjectDescription dsc, Point position, GraphPanel p,
            Vector vparameters, int id, int lqd, Vector<Joint> join) {
        super(dsc, position, id);

        actInputOutput(dsc, p);

        type = type_Algorithm;

        this.dsc.setArg(join);
        for (int ar = 0; ar < dsc.arg.size(); ar++) {
            if (lqd == LQD) {
                dsc.arg.get(ar).type_lqd = "LQD";
            } else if (lqd == CRISP2) {
                dsc.arg.get(ar).type_lqd = "CRISP";
            }

            dsc.arg.get(ar).times.clear();
            dsc.arg.get(ar).tableVector.clear();

            dsc.arg.get(ar).information();
        }

        // this.subtipo = subtipo;
        if (dsc.getSubtype() == type_Preprocess) {
            if (lqd == LQD) {
                image = Toolkit.getDefaultToolkit().getImage(
                        this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/preprocessLQD.gif"));
                type_lqd = LQD;
                this.dsc.setSubtypelqd(LQD);

            } else if (lqd == CRISP2) {
                image = Toolkit.getDefaultToolkit().getImage(
                        this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/preprocessLQD.gif"));
                type_lqd = CRISP2;
                this.dsc.setSubtypelqd(CRISP2);

            } else {
                image = Toolkit.getDefaultToolkit().getImage(
                        this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/preprocess.gif"));
            }
        } else if (dsc.getSubtype() == type_Postprocess) {
            if (lqd == CRISP2) {
                image = Toolkit.getDefaultToolkit().getImage(
                        this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/postprocessLQD.gif"));
                type_lqd = CRISP2;
                dsc.setSubtypelqd(CRISP2);

            } else if (lqd == LQD) {
                image = Toolkit.getDefaultToolkit().getImage(
                        this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/postprocessLQD.gif"));
                type_lqd = LQD;
                dsc.setSubtypelqd(LQD);

            } else {
                image = Toolkit.getDefaultToolkit().getImage(
                        this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/postprocess.gif"));
            }
        } else if (dsc.getSubtype() == type_Method) {

            if (lqd == CRISP2) {
                image = Toolkit.getDefaultToolkit().getImage(
                        this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/method_nodeLQD.gif"));
                type_lqd = CRISP2;
                this.dsc.setSubtypelqd(CRISP2);
            } else if (lqd == LQD) {
                image = Toolkit.getDefaultToolkit().getImage(
                        this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/method_nodeLQD.gif"));
                type_lqd = LQD;
                this.dsc.setSubtypelqd(LQD);
            } else {
                image = Toolkit.getDefaultToolkit().getImage(
                        this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/method_node.gif"));
            }
        }
        pd = p;
        par = new Vector(vparameters.size());
        for (int i = 0; i < vparameters.size(); i++) {
            par.addElement(new Parameters((Parameters) vparameters.elementAt(i)));
        // par = new Parametros(parameters);
        }
    }

    /**
     *  Shows a container dialog
     *
     * @param title Title of the dialog
     * @param show Wheter to show or not
     * @param destino Node related
     * @param parent Parent frame
     */
    public void contain(String title, int show, Node destino, Experiments parent) {

        //dialog = new Container(title,this.dsc.name,this);
        if (show == 1) {
            dialog = new Container_Selected(pd.parent, true, title, destino, parent);
        } else {
            dialog = new Container_Selected(pd.parent, true, this, destino, parent);
        }

        // Center dialog
        //dialog.setSize(257, 250);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = dialog.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        dialog.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        dialog.setResizable(false);
        dialog.setVisible(true);

    }

    /**
     * Check the constraints defined by the node described by 'dsc' contained
     * in the graph
     *
     * @param dsc The object describing the node
     * @param p the graph panel which contains the node
     */
    @Override
    public void actInputOutput(ExternalObjectDescription dsc, GraphPanel p) {

        for (int i = 0; i < p.parent.listAlgor.length; i++) {

            //System.out.println (" algor: "+p.padre.listAlgor[i].name + " - " +  dsc.nombre[0] );
            if (p.parent.listAlgor[i].name.equalsIgnoreCase(dsc.name[0])) {
                //System.out.println (" \n\nALGORISME TROBAT");
                //System.out.println ("  > Continuous: "+p.padre.listAlgor[i].m_bInputContinuous );
                //System.out.println ("  > Integer: "+p.padre.listAlgor[i].m_bInputInteger );
                //System.out.println ("  > NOminal: "+p.padre.listAlgor[i].m_bInputNominal );
                //System.out.println ("  > Missing: "+p.padre.listAlgor[i].m_bInputMissing );


                m_bInputContinuous = p.parent.listAlgor[i].m_bInputContinuous;
                m_bInputInteger = p.parent.listAlgor[i].m_bInputInteger;
                m_bInputNominal = p.parent.listAlgor[i].m_bInputNominal;
                m_bInputMissing = p.parent.listAlgor[i].m_bInputMissing;
                m_bInputImprecise = p.parent.listAlgor[i].m_bInputImprecise;
                m_bInputMultiClass = p.parent.listAlgor[i].m_bInputMultiClass;
                m_bInputMultiOutput = p.parent.listAlgor[i].m_bInputMultiOutput;
                m_bInputMIL = p.parent.listAlgor[i].m_bInputMIL;

                m_bOutputContinuous = p.parent.listAlgor[i].m_bOutputContinuous;
                m_bOutputInteger = p.parent.listAlgor[i].m_bOutputInteger;
                m_bOutputNominal = p.parent.listAlgor[i].m_bOutputNominal;
                m_bOutputMissing = p.parent.listAlgor[i].m_bOutputMissing;
                m_bOutputImprecise = p.parent.listAlgor[i].m_bOutputImprecise;
                m_bOutputMultiClass = p.parent.listAlgor[i].m_bOutputMultiClass;
                m_bOutputMultiOutput = p.parent.listAlgor[i].m_bOutputMultiOutput;

                break;
            }

        }
    }

    /**
     * Get active parameters
     *
     * @return Parameters
     */
    Parameters getActivePair() {

        return (Parameters) par.elementAt(0);
    }

    /**
     * Shows the parameter dialog
     */
    public void showDialog() {

        dialog = new ParametersDialog(pd.parent, "Algorithm Parameters", true, par, dsc);

        // Center dialog
        dialog.setSize(400, 580);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = dialog.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        dialog.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    /**
     * Draws this component
     * @param g2 Graphic component
     * @param select Is selected
     */
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

        g2.setFont(new Font("Courier", Font.BOLD + Font.ITALIC, 12));
        FontMetrics metrics = g2.getFontMetrics();

        int width;
        int height = metrics.getHeight();
        if (dsc.getSubtypelqd() == CRISP2 || type_lqd == CRISP2) {
            width = metrics.stringWidth(id + "." + dsc.getName() + " (Crisp)");
            g2.drawString(id + "." + dsc.getName() + " (Crisp)", centre.x - width / 2, centre.y + 40);
        } else if (dsc.getSubtypelqd() == LQD || type_lqd == LQD) {
            width = metrics.stringWidth(id + "." + dsc.getName() + " (Low Quality)");
            g2.drawString(id + "." + dsc.getName() + " (Low Quality)", centre.x - width / 2, centre.y + 40);
        } else {
            width = metrics.stringWidth(dsc.getName());
            g2.drawString(dsc.getName(), centre.x - width / 2, centre.y + 40);
        }

    }
}
