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
 * @author Written by Luciano Sánchez <University of Oviedo) 28/01/2004
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Node;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;


public class NodeExp extends NodeExprArit {
/**
 * <p>
 * Class for management of nodes that are evaluated to alfa-cuts family
 * </p>
 */
    // This node is evaluated to alfa-cuts family
	
	/**
	 * <p>
	 * Constructor. Generate a NodeExp with an only one children
	 * </p>
	 * @param asert1 The node for generate the children (NodeExprArit)
	 */
    public NodeExp(NodeExprArit assert1) {
        super(1,NExp);
        children[0]=assert1.clone();
    }
    
    /**
     * <p>
     * Constructor. Geneate a NodeExpr from another one
     * </p>
     * @param n The NodeExp (NodeExp)
     */
    public NodeExp(NodeExp n) {
        super(n.children.length,NExp);
        for (int i=0;i<children.length;i++) children[i]=n.children[i].clone();
    }
    
    /**
     * <p>
     * This method asign to a NodeExp the properties from another 
     * </p>
     * @param n The Node Exp (NodeExp)
     */
    public void set(NodeExp n) {
        super.set(n);
        for (int i=0;i<children.length;i++) children[i]=n.children[i].clone();
    }
    
    /**
     * <p>
     * This method creates a clone from the object of the class
     * </p>
     * @return The NodeExp cloned
     */
    public Node clone() {
        return new NodeExp(this);
    }
    
    /**
     *<p>
     * This method evaluate the alphacut of a node
     *</p>
     *@return The fuzzy alpha cuts evaluated
     */
    public FuzzyAlphaCut Beval(){
        NodeExprArit tmp1=(NodeExprArit)children[0];
        FuzzyAlphaCut t1=(FuzzyAlphaCut)tmp1.Beval();
        return t1.exp();
    }
    
    /**
     * <p>
     * This method is for debug
     * </p>
     */
    public void debug() {
        System.out.print("EXP(");
        children[0].debug();
        System.out.print(")");
    }
    
}


