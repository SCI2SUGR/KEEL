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
 * File: Arc.java
 *
 * A class representing arcs of the graph
 *
 * @author Written by Admin 4/8/2010
 * @version 1.0
 * @since JDK1.5
 */
package keel.GraphInterKeel.experiments;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;

public class Arc implements Serializable {

    private int sourceNode;
    private int destinationNode;
    private transient Shape line;
    private transient Image myImage;
    private transient GraphPanel pd;

    /**
     * Builder
     */
    public Arc() {
    }

    /**
     * Builder
     * @param p Parent graph
     */
    public Arc(GraphPanel p) {
        myImage = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/flecha.gif"));
        pd = p;
    }

    /**
     * Builder
     * @param source Source node
     * @param destination Destination node
     * @param p Parent graph
     */
    public Arc(int source, int destination, GraphPanel p) {
        sourceNode = source;
        destinationNode = destination;
        pd = p;
        myImage = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/flecha.gif"));
    }

    /**
     * Gets the source node of this arc
     * @return The source node
     */
    public int getSource() {
        return sourceNode;
    }

    /**
     * Gets the destination node
     * @return the destination node
     */
    public int getDestination() {
        return destinationNode;
    }

    /**
     * Sets the node source
     * @param source The new node source
     */
    public void setSource(int source) {
        sourceNode = source;
        pd.mainGraph.setModified(true);
    }

    /**
     * Sets the destination node
     * @param destination the new destination node
     */
    public void setDestination(int destination) {
        destinationNode = destination;
        pd.mainGraph.setModified(true);
    }

    /**
     * Gets the source node of this arc
     * @return The source node
     */
    public int getSource2() {
        return sourceNode;
    }

    /**
     * Gets the destination node
     * @return the destination node
     */
    public int getDestination2() {
        return destinationNode;
    }

    /**
     * Sets the node source
     * @param source The new node source
     */
    public void setSource2(int source) {
        sourceNode = source;
    }

    /**
     * Sets the destination node
     * @param destination the new destination node
     */
    public void setDestination2(int destination) {
        destinationNode = destination;
    }

    /**
     * Draws this component
     * @param g2 The graphic element
     * @param source The  point of origin
     * @param destination The destination point
     * @param select if this element is selected
     */
    public void draw(Graphics2D g2, Point source, Point destination,
            boolean select) {
        Point intersect = intersection(source, destination);
        destination = intersect;
        line = new Line2D.Float(source, destination);
        if (select) {
            Stroke s = g2.getStroke();
            g2.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_BEVEL, 0, new float[]{1, 1}, 0));
            g2.draw(line);
            g2.setStroke(s);
        } else {
            g2.draw(line);

        }
        AffineTransform af = g2.getTransform();
        double angulo = Math.atan2(destination.y - source.y, destination.x - source.x) -
                (5 * Math.PI / 4);
        g2.rotate(angulo, intersect.x, intersect.y);
        g2.drawImage(myImage, intersect.x, intersect.y, 15, 15, pd);
        g2.setTransform(af);
    }

    /**
     * Test if the provided point is inside
     * @param point Point
     * @param source Source node
     * @param destination Destination node
     * @return If the provided point is inside
     */
    public boolean isInside(Point point, Point source, Point destination) {
        double dist = ((Line2D) line).ptSegDist(point);
        if (dist < 10) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Computes the intersection of two nodes
     * @param source Source node
     * @param destination Destination node
     * @return Intersection of two nodes as a point
     */
    private Point intersection(Point source, Point destination) {
        Point punto = new Point();
        Point p0, p1, p2, p3;
        double t, d;

        double angulo = Math.atan2(destination.y - source.y, destination.x - source.x) *
                180.0 / Math.PI;
        if (angulo < 0) {
            angulo += 360;
        }
        if (angulo >= 45.0 && angulo < 135) {
            p0 = new Point(destination.x - 25, destination.y - 25);
            p1 = new Point(destination.x + 25, destination.y - 25);
        } else if (angulo >= 135.0 && angulo < 225) {
            p0 = new Point(destination.x + 25, destination.y - 25);
            p1 = new Point(destination.x + 25, destination.y + 25);
        } else if (angulo >= 225.0 && angulo < 315) {
            p0 = new Point(destination.x + 25, destination.y + 25);
            p1 = new Point(destination.x - 25, destination.y + 25);
        } else {
            p0 = new Point(destination.x - 25, destination.y + 25);
            p1 = new Point(destination.x - 25, destination.y - 25);
        }

        p2 = source;
        p3 = destination;

        d = p0.x * (p3.y - p2.y) + p1.x * (p2.y - p3.y) + p3.x * (p1.y - p0.y) +
                p2.x * (p0.y - p1.y);
        t = p0.x * (p3.y - p2.y) + p2.x * (p0.y - p3.y) + p3.x * (p2.y - p0.y);
        if (d != 0) {
            t = t / d;

        }
        punto.x = (int) (p0.x + (p1.x - p0.x) * t);
        punto.y = (int) (p0.y + (p1.y - p0.y) * t);

        return punto;
    }
}
