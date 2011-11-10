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
 * File: UserMethod.java
 *
 * A class for managing user methods
 *
 * @author Written by Ana Palacios Jimenez and Luciano Sanchez Ramos 23-4-2010 (University of Oviedo)
 * @version 1.0
 * @since JDK1.5
 */
package keel.GraphInterKeel.experiments;

import java.awt.*;
import java.awt.geom.*;

public final class UserMethod extends Node {

    protected Parameters parametersUser;
    protected String patternFile;
    protected String command;

    /**
     * Builder
     * @param dsc Parent dsc
     * @param posicion Initial position
     * @param p Graph
     */
    public UserMethod(ExternalObjectDescription dsc, Point posicion,
            GraphPanel p) {
        super(dsc, posicion, p.mainGraph.getId());
        p.mainGraph.setId(p.mainGraph.getId() + 1);
        type = type_userMethod;
        patternFile = new String("");
        command = new String("");
        image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/usuario.gif"));
        pd = p;
        parametersUser = null;
    }

    /**
     * Builder
     * @param dsc Parent dsc
     * @param position Initial position
     * @param p Graph
     * @param parameters Parameters of the method
     * @param id Node id
     */
    public UserMethod(ExternalObjectDescription dsc, Point position,
            GraphPanel p, Parameters parameters, int id) {
        super(dsc, position, id);
        type = type_userMethod;
        patternFile = new String("");
        command = new String("");
        image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/usuario.gif"));
        pd = p;
        parametersUser = null;
        if (parameters != null) {
            parametersUser = new Parameters(parameters);
        }
    }

    /**
     * Contain method
     *
     * @param title Title of the node
     * @param show Wheter to show or not
     * @param n Id node
     * @param exp Paret frame
     */
    public void contain(String title, int show, Node n, Experiments exp) {
    }

    /**
     * Show associated dialog
     */
    public void showDialog() {

        dialog = new DialogUser(pd.parent, "Algorithm Files", true, this);
        dialog.setSize(400, 269);
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
     * Drawing component
     * @param g2 Graphich element
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
        int width = metrics.stringWidth(dsc.getName());
        int height = metrics.getHeight();
        g2.drawString(dsc.getName(), centre.x - width / 2, centre.y + 40);
    }
}
