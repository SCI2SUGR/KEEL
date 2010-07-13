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
 * @author Written by Luciano Sánchez (University of Oviedo) 26/01/2004
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */


package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Node;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;

public class NodeMinus extends NodeExprArit {
/**
 * <p>
 * Class for management minus node
 * </p>
 */
    
     //It's evaluated to an alpha-cuts family

	/**
	 * <p>
	 * Constructor. Generates a new minus node with two children
	 * </p>
	 * @param assert1 The arithmetic expression for the fist child
	 * @param assert2 The arithmetic expression for the second child
	 */
    public NodeMinus(NodeExprArit assert1, NodeExprArit assert2) {
        super(2,NMinus);
        children[0]=assert1.clone();
        children[1]=assert2.clone();
    }
    
    /**
     * <p>
     * Constructor. Generates a new minus node from another one
     * </p>
     * @param n The minus node
     */
    public NodeMinus(NodeMinus n) {
        super(n.children.length,NMinus);
        for (int i=0;i<children.length;i++) children[i]=n.children[i].clone();
    }
    
    /**
     * <p>
     * This method sets a minus node to another one
     * </p>
     * @param n the minus node to be assigned
     */
    public void set(NodeMinus n) {
        super.set(n);
        for (int i=0;i<children.length;i++) children[i]=n.children[i].clone();
    }
    
    /**
     * <p>
     * This method clone a minud node
     * </p>
     * @return The node cloned 
     */
    public Node clone() {
        return new NodeMinus(this);
    }
    
    /**
     * <p>
     * This method evaluates two nodes with the fuzzy alpha cut minus
     * </p>
     * @return The fuzzy alpha cut
     */
    public FuzzyAlphaCut Beval(){
        NodeExprArit tmp1=(NodeExprArit)children[0];
        NodeExprArit tmp2=(NodeExprArit)children[1];
        FuzzyAlphaCut t1=(FuzzyAlphaCut)tmp1.Beval();
        FuzzyAlphaCut t2=(FuzzyAlphaCut)tmp2.Beval();
        return t1.subtract(t2);
    }
    
    /**
     * <p>
     * This method is for debug
     * </p>
     */
    public void debug() {
        System.out.print("(");
        children[0].debug();
        System.out.print(") - (");
        children[1].debug();
        System.out.print(")");
    }
    
}

