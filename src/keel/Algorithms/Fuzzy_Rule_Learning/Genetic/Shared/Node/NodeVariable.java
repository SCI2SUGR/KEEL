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

public class NodeVariable extends NodeExprArit {
/**
 * <p>
 * Class for management nodes with the number of the variable and the value of
 * the variable
 * </p>  
 */
    // Internal Node
    // It's evaluated to a real number    
    int N;      // Number of variable
    FuzzyAlphaCut X;   // Value of the variable 

    /**
     * <p>
     * Constructor. Generates a new variable node
     * </p>
     * @param n The number (index) for the new variable
     */
    public NodeVariable(int n) {
        super(0,NVariable);
        N=n; X=new FuzzyAlphaCut(new FuzzyNumberTRIANG(0,0,0));
    }
    
    /**
     * <p>
     * Constructor. Generates a new variable node from another one
     * </p>
     * @param n The variable node
     */
    public NodeVariable(NodeVariable n) {
        super(0,NVariable);
        N=n.N; X=n.X;
    }

    /**
     * <p>
     * This method sets a variable node to another one
     * </p>
     * @param n The variable node to be assigned
     */
    public void set(NodeVariable n) {
        super.set(n);
        N=n.N; X=n.X;
    }
    
    /**
     * <p>
     * This method evaluates if two nodes are the same type
     * </p>
     * @param n The node
     * @return True or false
     */
    protected boolean compatibleData(Node n) {
        if (mytypeid!=n.mytypeid) return false;
        NodeVariable nl=(NodeVariable)n;
        if (N!=nl.N) return false;
        return true;
    }
    
    
    /**
     * <p>
     * This method clones a variable node 
     * </p>
     * @return The node cloned
     */
    public Node clone() {
        return new NodeVariable(this);
    }

    /**
     * <p>
     * This method calculates the center of mass of the fuzzy alpha cuts
     * </p>
     * @return The center of mass
     */
    public double CrispEval() {
        return X.massCentre();
    }
    
    /**
     * <p>
     * This method returns the fuzzy alpha cut
     * </p>
     * @return The fuzzy alpha cut
     */
    public FuzzyAlphaCut Beval() {
        return X;
	}

    /**
     * <p>
     * This method replaces the fuzzy alpla cuts in a especified node
     * </p>
     * @param x The new fuzzy alpha cuts 
     */
    public void replaceTerminals(FuzzyAlphaCut [] x) {
        X=x[N];
    }

    /**
     * <p>
     * This method is for debug
     * </p>
     */
    public void debug() {
        System.out.print("X["+N+"]");
    }
    
    /**
     * <p>
     * This method returns the variable number
     * </p>
     * @return The variable number
     */
    public int getN()  {
        return N;
    }
}


