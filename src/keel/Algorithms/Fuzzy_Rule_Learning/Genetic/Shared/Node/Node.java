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
 * @author Written by Luciano Sánchez (University of Oviedo) 20/01/2004
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

// Node de un arbol sintactico de un
// interprete del siguiente lenguaje:
//
//
// Basereglas := Regla | Basereglas Regla
// Regla := SI Aserto ENTONCES Consecuente
// Aserto := Variable ES Valor | Aserto AND Aserto | Aserto OR Aserto
// Variable := X0 | X1 | ... | XN
// Valor := V0 | V1 | ... | VN
//
// Every variable has the same linguistic labels and does not use weights

// Terminal nodes have the same own attributes and  haven't got pointer to children.
// Internal nodes only have got pointer to children.

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Node;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;


public abstract class Node {
/**
 * <p>
 * The class defines the characteristics of a node 
 * </p>
 */
    // Process of induction of fuzzy rules bank
    public final static int NVariable=0;
    public final static int NLabel=1;
    public final static int NConsequent=2;
    public final static int NAnd=3;
    public final static int NOr=4;
    public final static int NEs=5;
    public final static int NRule=6;
    public final static int NRuleBase=7;

    // Symbolic regression, GA-P with intervals
    public final static int NExprHold=8;
    public final static int NExprArit=9;
    public final static int NValue=10;
    public final static int NSum=11;
    public final static int NMinus=12;
    public final static int NProduct=13;
    public final static int NSquareRoot=14;
    public final static int NExp=15;
    public final static int NLog=16;
    
    protected int mytypeid;
    protected Node[] children;
    
    /**
     * <p>
     * The public method return the list of children of the node
     * </p>
     * @return The list of children of the node
     */
    public Node[] children() { return children; }

    /**
     * <p>
     * Constructor. Generate a new void node with cero chindren
     * </p>
     * @param t The type of the node (int) 
     */
    public Node(int t) {
        mytypeid=t;
        children = new Node[0];
    }

    /**
     * <p>
     * Constructor. Generate a new void node with n chindren
     * </p>
     * @param n The number of chindren of the node (int) 
     * @param t The type of the node (int)
     */
    public Node(int n, int t) {
        mytypeid=t;
        children = new Node[n];
    }

    /**
     * <p>
     * Constructor. Generate a new node from another
     * </p>
     * @param n The node to be copied (Node)
     */
    public Node(Node n) {
        mytypeid=n.mytypeid;
        children = new Node[n.children.length];
    }

    /**
     * <p>
     * This abstract method clone a Node
     * </p>
     * @return A node cloned
     */
    public abstract Node clone();

    /**
     * <p>
     * This method asign a node the porperties from another
     * </p>
     * @param n The node to be asigned (Node)
     */
    public void set(Node n) {
        mytypeid=n.mytypeid;
        children = new Node[n.children.length];
    }

    /**
     * <p>
     * This method replace the terminal from fuzzy alpha cuts
     * </p>
     * @param x List of fuzzy alpha cuts (FuzzylphaCut[])
     */
    
    public void replaceTerminals(FuzzyAlphaCut[] x) {
        for (int i=0;i<children.length;i++)
            children[i].replaceTerminals(x);
    }

    /**
     * <p>
     * This abstrac method is for debug
     * </p>
     */
    public abstract void debug();

    /**
     * <p>
     * This method return the type of node
     * </p>
     * @return The type of node
     */
    public int type() {
        return mytypeid;
    }

    /**
     * <p>
     * This method return the number of children of the node
     * </p>
     * @return The number of children
     */
    public int nChildren()  {
        return children.length;
    }

    /**
     * <p>
     * This method return the children of the i position
     * </p>
     * @param i Position of the children (int)
     * @return The children in the i position
     */
    public Node child(int i)  {
        return children[i];
    }

    /**
     * <p>
     * This method modify the children node in the i position, assigning the node that is
     * passed as parameter
     * </p>
     * @param n The node (Node)
     * @param i The node position to be changed (int)
     */
    public void changeChild(Node n, int i) {
        children[i]=n;
    }
    
    /**
     * <p>
     * This method find out if two nodes are the same type 
     * </p>
     * @param n The node to be compared (Node)
     * @return True or false
     */
    protected boolean compatibleData(Node n)
    {
        return (mytypeid==n.mytypeid);
    }
    
    /**
     * <p>
     * This method find out if two nodes are the same type, the same number of children
     * or the children are compatible. 
     * </p>
     * @param n The node to be compared (Node)
     * @return True or false
     */
    public boolean compatible(Node n) {
        if (!compatibleData(n)) return false;
        if (children.length!=n.children.length) return false;
        for (int i=0;i<children.length;i++) 
            if (!children[i].compatible(n.children[i])) return false;
        return true;
    }

}




