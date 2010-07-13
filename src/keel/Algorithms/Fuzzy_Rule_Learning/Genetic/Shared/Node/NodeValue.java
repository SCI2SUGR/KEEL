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

public class NodeValue extends NodeExprArit {
/**
 * <p>
 * Class for management nodes with the number of the variable and the value of
 * the variable as a string
 * </p>
 */
	
    //It's evaluated to a fuzzy constant
    // GAP implementation requires, this node must contains
    // a pointer to a fuzzy set instead a copy. 
    // You must remove every calling to "Clona"     
    
    private int index;
    private FuzzyAlphaCut[] string;
    
    /**
     * <p>
     * Constructor. Generates a new node value
     * </p>
     * @param i The index
     * @param c The fuzzy alpha cuts
     */
    public NodeValue(int i, FuzzyAlphaCut[] c) {
        super(0,NValue);
        index=i;
        string=c;
    }
    /**
     * <p>
     * Constructor. Generates a new node from another one
     * </p>
     * @param n The node
     */
    public NodeValue(NodeValue n) {
        super(0,NValue);
        index=n.index;
        string=n.string;
    }
    
    /**
     * <p>
     * This method sets a node to another
     * </p> 
     * @param n The node to be assigned 
     */
    public void set(NodeValue n) {
        super.set(n);
        index=n.index;
        string=n.string;
    }
    
    /**
     * <p>
     * This method evaluate if two nodes are the same type
     * </p>
     * @param n The node to be compared
     * @return True or false
     */
    protected boolean compatibleData(Node n) {
        if (mytypeid!=n.mytypeid) return false;
        return true;
    }
    /**
     * <p>
     * This method replace replace the fuzzy alpha cuts 
     * </p>
     * @param c The fuzzy alpha cuts
     */
    public void setString(FuzzyAlphaCut[] c) {
        string=c;
    }
    
    /**
     * <p>
     * This method generate a new node from a NodeValue
     * </p>
     * @return the node
     */
    public Node clone() {
        return new NodeValue(this);
    }
    
    /**
     * <p>
     * this method return the fuzzy alpha cuts of a node
     * </p>
     * @return The fuzzy alpha cuts
     */
    public FuzzyAlphaCut Beval(){
        return string[index];
    }
    
    /**
     * <p>
     * This method return the index of the node
     * </p>
     * @return The index
     */
    public int getIndex() {
        return index;
    }
    
    /**
     * <p>
     * This method is for debug
     * </p>
     */
    public void debug() {
        System.out.print("VALOR (");
        System.out.print(string[index].aString());
        System.out.print(")");
    }
    
}

