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
 * @author Written by Luciano Sánchez (University of Oviedo) 25/01/2004
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */


package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Node;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;

public class NodeExprHold extends Node {
/**
 * <p>
 * Class for management of nodes that are evaluated to a vector of fuzzy numbers	
 * </p>
 */
	
    // Internal Node (usually root node)
    // It's evaluated to vector of fuzzy numbers
    
	/**
	 * <p>
	 * Constructor. Generates a new node witch children are the vector of of fuzzy numbers
	 * </p>
	 * @param br A vector of fuzzy numbers (NodeExprArit)
	 */
    public NodeExprHold(NodeExprArit []br) {
        super(br.length,NExprHold);
        for (int i=0;i<br.length;i++) children[i]=br[i].clone();
    }
    
    /**
     * <p>
     * Constructor. Generate a new node from another one
     * </p>
     * @param br The node
     */
    public NodeExprHold(NodeExprHold br) {
        super(br.children.length,NExprHold);
        for (int i=0;i<br.children.length;i++) children[i]=br.children[i].clone();
    }
    
    /**
     * <p>
     * This method sets the node properties to another one
     * </p>
     * @param br The node to be assigned
     */
    public void set(NodeExprHold br) {
        super.set(br);
        for (int i=0;i<br.children.length;i++) children[i]=br.children[i].clone();
    }
    
    /**
     * <p>
     * This method evaluate if two nodes are the same type
     * </p>
     * @return True or false
     */
    protected boolean compatibleData(Node n) {
        if (mytypeid!=n.mytypeid) return false;
        return true;
    }
    
    /**
     * <p>
     * This method clone a node
     * </p>
     * @return The node cloned
     */
    public Node clone() {
        return new NodeExprHold(this);
    }
    
    /**
     *<p>
     * This method evaluate the alphacut of the nodes
     *</p>
     *@return The fuzzy alpha cuts evaluated
     */
    public FuzzyAlphaCut[] Beval() {
        if (children.length==0) return new FuzzyAlphaCut[0];
        else {
            FuzzyAlphaCut[] result=new FuzzyAlphaCut[children.length];
            for (int i=0;i<result.length;i++) {
                NodeExprArit tmp=(NodeExprArit)children[i];
                result[i]=tmp.Beval();
            }
            return result;
        }
        
    }
    
    /**
     * <p>
     * This method is for debug
     * </p>
     */
    public void debug() {
        for (int i=0;i<children.length;i++) {
            children[i].debug();
        }
    }
}

