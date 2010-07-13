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
 * @author Writen by Luciano Sánchez (University of Oviedo) 21/01/2004
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Node;

public class NodeAnd extends NodeAssert {
/**
 * <p>
 * Class for management and nodes 
 * </p>
 */
    // Internal Node
    // It's evaluated to a real number
	
	/**
	 * <p>
	 * Constructor. Generate a new NodeAnd with two children: asert1 and asert2
	 * </p>
	 * @param asert1 Children for the NodeAnd (NodeAssert) 
	 * @param asert2 Children for the NodeAnd (NodeAssert)
	 */
    public NodeAnd(NodeAssert aserto1, NodeAssert aserto2)  {
        super(2,NAnd);
        children[0]=aserto1.clone();
        children[1]=aserto2.clone();
    }

    /**
     * <p>
     * Constructor: Generate a new NodeAnd from a given one (NodeAnd)
     * </p>
     * @param n The NodeAnd
     */
    public NodeAnd(NodeAnd n) {
        super(n.children.length,NAnd);
        for (int i=0;i<children.length;i++) children[i]=n.children[i].clone();
    }

    /**
     * <p>
     * This method asign to to a NodeAnd the properties of another
     * </p>
     * @param n The NodeAdd (NodeAnd) 
     */
    public void set(NodeAnd n) {
        super.set(n);
        for (int i=0;i<children.length;i++) children[i]=n.children[i].clone();
    }

    /**
     * <p>
     * This method clone a NodeAnd
     * </p>
     * @return The NodeAnd cloned 
     */
    public Node clone() {
        return new NodeAnd(this);
    }

    /**
     * <p>  
     * This method evaluate two nodes.
     * </p>
     * @return The minimum crips eval.
     */
    public double CrispEval() {
        NodeAssert tmp1=(NodeAssert)children[0];
        NodeAssert tmp2=(NodeAssert)children[1];
        double t1=tmp1.CrispEval();
        double t2=tmp2.CrispEval();
        if (t1<t2) return t1; else return t2;
    }

    /**
     * <p>
     * This method is for debug
     * </p>
     */
    public void debug() {
        System.out.print("(");
        children[0].debug();
        System.out.print(") AND (");
        children[1].debug();
        System.out.print(")");
    }

}


