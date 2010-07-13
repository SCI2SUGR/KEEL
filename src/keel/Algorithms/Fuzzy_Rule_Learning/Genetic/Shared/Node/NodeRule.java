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


public class NodeRule extends Node {
/**
 * <p>
 * Class for management a rule node
 * </p>
 */
    // Internal Node
    // It's evaluated to a pair integer/weight
    double weight; 

    /**
     * <p>
     * Constructor. Generates a new rule node
     * </p>
     * @param assert1 The rule antecedent
     * @param consequent The rule consequent
     * @param w The weight
     */
    public NodeRule(NodeAssert assert1, NodeConsequent consequent, double w) {
        super(2,NRule);
        weight=w;
        children[0]=assert1.clone();
        children[1]=consequent.clone();
    }
    
    /**
     * <p>
     * Constructor. Generates a new rule node from another one
     * </p>
     * @param n The rule node
     */
    public NodeRule(NodeRule n) {
        super(n.children.length,NRule);
        weight=n.weight;
        for (int i=0;i<children.length;i++)
            children[i]=n.children[i].clone();
    }
    
    /**
     * <p>
     * This method sets a rule node from another one
     * </p>
     * @param n The rule node
     */
    public void set(NodeRule n) {
        super.set(n);
        weight=n.weight;
        for (int i=0;i<children.length;i++)
            children[i]=n.children[i].clone();
    }
    
    /**
     * <p>
     * This method evaluates if two nodes are the same type
     * </p>
     * @return True or false
     */
    protected boolean compatibleData(Node n) {
        if (mytypeid!=n.mytypeid) return false;
        return true;
    }
    
    /**
     * <p>
     * This method clones a node rule
     * </p>
     * @return The node cloned
     */
    public Node clone() {
        return new NodeRule(this);
    }
    
    /**
     * <p>
     * This method evaluates the weight and the consequent of a rule
     * </p>
     * @return An IntDouble with the weight and the consequent
     */
    public IntDouble CrispEval() {
        IntDouble result = new IntDouble();
        NodeAssert tmp1=(NodeAssert)children[0];
        NodeConsequent tmp2=(NodeConsequent)children[1];
        result.weight=tmp1.CrispEval()*weight;
        result.consequent=tmp2.CrispEval();
        return result;  
    }

    /**
     * <p>
     * This method is for debug
     * </p>
     */
    public void debug() {
        System.out.print("SI ");
        children[0].debug();
        System.out.print(" ENTONCES ");
        children[1].debug();
        if (weight!=1) System.out.print(" CON PESO "+weight);
        System.out.println();
    }
    
}

