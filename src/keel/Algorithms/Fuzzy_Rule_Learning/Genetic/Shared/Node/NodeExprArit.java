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
 * @author Written by Luciano Sánchez (University of Oviedo) 23/01/2004
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Node;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;

public abstract class NodeExprArit extends Node {
/**
 * Class for constructor and define abstract methods
 */
	//Arithmetic expressions
    //This node is evaluated to fuzzy number
	/**
	 * <p>
	 * Constructor. Generate a new node of t type and n children
	 * </p>
	 * @param n The number of children
	 * @param t The type of node
	 */
    public NodeExprArit(int n, int t) {  super(n,t); }
    
    /**
     * <p>
     * This abstract method evaluate the alphacut of two nodes
     *</p>
     *@return The fuzzy alpha cuts evaluated
     */
    public abstract FuzzyAlphaCut Beval();
  
    /**
     * <p>
     * This abstract method clone a node
     * </p>
     * @return The cloned node
     */
    public abstract Node clone();
}

