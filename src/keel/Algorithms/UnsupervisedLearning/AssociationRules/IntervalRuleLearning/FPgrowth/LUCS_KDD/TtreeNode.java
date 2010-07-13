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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.FPgrowth.LUCS_KDD;

/* ------------------------------------------------------------------------- */
/*                                                                           */
/*                           TOTAL SUPPORT TREE BODE                         *//*                                                                           */
/*                                Frans Coenen                               */
/*                                                                           */
/*                            Wednesday 2 July 2003                          */
/*                                                                           */
/*                       Department of Computer Science                      */
/*                         The University of Liverpool                       */
/*                                                                           */ 
/* ------------------------------------------------------------------------- */

/**
* <p>
* @author Modified by Nicolò Flugy Papè (Politecnico di Milano) 24/03/2009
* @version 1.1
* @since JDK1.5
* </p>
*/

public class TtreeNode {
	/**
	* <p>
	* Methods concerned with Ttree node structure. Arrays of these structures are used
	* to store nodes at the same level in any sub-branch of the T-tree
	* </p>
	*/
    
    /* ------ FIELDS ------ */
    
    /** The support associate with the itemset represented by the node. */
    public int support = 0;
    
    /** A reference variable to the child (if any) of the node. */
    public TtreeNode[] childRef = null;

    // Diagnostics
    /** The number of nodes in the T-tree. */
    public static int numberOfNodes = 0;
    
    /* ------ CONSTRUCTORS ------ */	
    
    /** Default constructor */
	
    public TtreeNode() {
	numberOfNodes++;
	}
	
    /** One argument constructor. 
    @param sup the support value to be included in the structure. */
	
    public TtreeNode(int sup) {
	support = sup;
	numberOfNodes++;
	}
    
    /* ------ METHODS ------ */
    
    /** It returns the number of nodes.
     */
    public static int getNumberOfNodes() {
        return(numberOfNodes);
	}
}

