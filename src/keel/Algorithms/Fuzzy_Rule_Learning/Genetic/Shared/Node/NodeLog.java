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
 * @author Written by Luciano Sánchez (University of Oviedo) 28/01/2004
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Node;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;

public class NodeLog extends NodeExprArit {
/**
 * <p>
 * Class for management log nodes 
 * </p>
 */
    
    //It's evaluated to alfa-cuts family
	/**
	 * <p>
	 * Constructor. Generate a new log node con an only one child
	 * </p>
	 * @param assert1 The aritmetic expresion
	 */
    public NodeLog(NodeExprArit assert1) {
        super(1,NLog);
        children[0]=assert1.clone();
    }
    
    /**
     * <p>
     * Constructor. Generate a new log node from another one
     * </p>
     * @param n The log node
     */
    public NodeLog(NodeLog n) {
        super(n.children.length,NLog);
        for (int i=0;i<children.length;i++) children[i]=n.children[i].clone();
    }
    
    /**
     * <p>
     * This method assign a log node to another one
     * </p>
     * @param n The log node to be assigned
     */
    public void set(NodeLog n) {
        super.set(n);
        for (int i=0;i<children.length;i++) children[i]=n.children[i].clone();
    }
    
    /**
     * <p>
     * This method clone a log node
     * </p>
     * @return The node cloned
     */
    public Node clone() {
        return new NodeLog(this);
    }
    
    /**
     * <p>
     * This method evaluate two nodes with the fuzzy alpha cut log
     * </p>
     * @return The fuzzy alpha cut
     */
    public FuzzyAlphaCut Beval(){
        NodeExprArit tmp1=(NodeExprArit)children[0];
        FuzzyAlphaCut t1=(FuzzyAlphaCut)tmp1.Beval();
        return t1.log();
    }
    
    /**
     * <p>
     * This method is for debug
     * </p>
     */
    public void debug() {
        System.out.print("SQRT(");
        children[0].debug();
        System.out.print(")");
    }
    
}


