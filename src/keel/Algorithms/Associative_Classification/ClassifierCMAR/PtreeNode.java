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

/* -------------------------------------------------------------------------- */
/*                                                                            */
/*             P A R T I A L   S U P P O R T   T R E E  N O D E               */
/*                                                                            */
/*                               Frans Coenen                                 */
/*                                                                            */
/*                          Wednesday 9 January 2003                          */
/*                             (Revised 5/7/2003)                             */
/*                                                                            */
/*                       Department of Computer Science                       */
/*                        The University of Liverpool                         */
/*                                                                            */
/* -------------------------------------------------------------------------- */

/** Tree structure to store Ptree nodes. <P> Same as top level structure but 
with the addition of a sibling branch. 
@author Frans Coenen
@version 5 July 2003 */

package keel.Algorithms.Associative_Classification.ClassifierCMAR;

/**
 * Class to store the node of a P-tree
 *
 * @author Frans Coenen
 * @author Modified by Jesus Alcala (University of Granada) 09/02/2010
 * @author Modified by Sergio Almecija (University of Granada) 23/05/2010
 * @version 1.0
 * @since JDK1.5
 */
public class PtreeNode {
    
    /*------------------------------------------------------------------------*/
    /*                                                                        */
    /*                                   FIELDS                               */
    /*                                                                        */
    /*------------------------------------------------------------------------*/
    
    /** Partial support for the rows. */
    public int support = 1;

    /** Array of short (16 bit) integers describing the row. */
    public short[] itemSet = null;
	
    /** Pointer to child structure. */
    public PtreeNode childRef = null;
    
    /** Pointer to sibling structurte.  */
    public PtreeNode siblingRef = null;
	   
    /*---------------------------------------------------------------------*/
    /*                                                                     */
    /*                           CONSTRUCTORS                              */
    /*                                                                     */
    /*---------------------------------------------------------------------*/
    
    /** Create P-tree node (other than top-level node)
    @param newItemSet the itemset to be stored at the node.   */ 

    public PtreeNode(short[] newItemSet) {
        itemSet = newItemSet;
	}  
    }

