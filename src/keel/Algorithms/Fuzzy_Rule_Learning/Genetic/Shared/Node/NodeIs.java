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


package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Node;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;

public class NodeIs extends NodeAssert {
/**
 *<p>
 *Class for management a node witch is evaluated to a real number
 *</p> 
 */
    // Internal Node
    // It's evaluated to a real number

	/**
	 * <p>
	 * Generate a new node witch two children
	 * </p>
	 * @param variable Node of variable type (NodeVariable)
 	 * @param value Node of label type (Node Label)
	 */
    public NodeIs(NodeVariable variable, NodeLabel value) {
        super(2,NEs);
        children[0]=variable.clone();
        children[1]=value.clone();
    }
    
    /**
     * <p>
     * Generate a new node from another one
     * </p>
     * @param n The node (NodeIs)
     */
    public NodeIs(NodeIs n) {
        super(n.children.length,NEs);
        for (int i=0;i<children.length;i++)
            children[i]=n.children[i].clone();
    }

    /**
     * <p>
     * This method sets to a NodeIs the properties from another 
     * </p>
     * @param n The Node (NodeIs)
     */
    public void set(NodeIs n) {
        super.set(n);
        for (int i=0;i<children.length;i++)
            children[i]=n.children[i].clone();
    }
    
    /**
     * <p>
     * This method  clone a node
     * </p>
     * @return The node cloned
     */
    public Node clone() {
        return new NodeIs(this);
    }

    /**
     * <p>
     * This method calculates the level of membership of a value to a linguistic label
     * </p>
     * @return The level of membership
     */
    public double CrispEval() {
        NodeVariable tmp1=(NodeVariable)children[0];
        NodeLabel tmp2=(NodeLabel)children[1];
        double x=tmp1.CrispEval();
        Fuzzy b=tmp2.CrispEval();
        return b.evaluateMembership(x);
    }

    /**
     *<p>
     *This method is for debug
     *</p> 
     */
    public void debug() {
        System.out.print("(");
        children[0].debug();
        System.out.print(") ES (");
        children[1].debug();
        System.out.print(")");

    }

}



