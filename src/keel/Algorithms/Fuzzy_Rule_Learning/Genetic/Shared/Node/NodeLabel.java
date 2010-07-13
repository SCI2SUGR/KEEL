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
 * <p>
 * @author Written by Luciano Sánchez (University of Oviedo) 21/01/2004
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

//Generic Fuzzy Set
package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Node;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;

public class NodeLabel extends Node {
/**
 * <p>
 * Class for management terminal nodes witch are evaluated as linguistic fuzzy labels
 * </p>
 */
    // Terminal Node
    // It's evaluated to linguistic fuzzy label
    Fuzzy B;

    /**
     * <p>
     * Constructor. Generate a label terminal node 
     * </p>
     * @param b The label (Fuzzy)
     */
    public NodeLabel(Fuzzy b) {
        super(NValue);
        B=b.clone();
    }

    /**
     * <p>
     * Constructor. Generate a label terminal node from another one
     * </p>
     * @param n The node (NodeLabel)
     */
    public NodeLabel(NodeLabel n) {
        super(NValue);
        B=n.B.clone();
    }

    /**
     * <p>
     * This method asing to a NodeLabel the properties from another
     * </p>
     * @param n The node (NodeLabel)
     */
    public void set(NodeLabel n) {
        super.set(n);
        B=n.B.clone();
    }
    
    /**
     * <p>
     * This method evaluate if a Node is teh same type as another one
     * </p>
     * @param n The node to be evaluated (Node)
     */
    protected boolean compatibleData(Node n) {
        if (mytypeid!=n.mytypeid) return false;
        return true;
    }
    
    /**
     * <p>
     * This method clone a node
     * </p>
     * @return The node to be cloned
     */
    public Node clone() {
        return new NodeLabel(this);
    }
    
    /**
     * <p>
     * This method return the membership level
     * </p>
     * @return The membership level 
     */
    public Fuzzy CrispEval() {
        return B;
    }
   
    /**
     * <p>
     * This method is for debug
     * </p>
     */
    public void debug() {
        System.out.print(B.aString());
    }

}

