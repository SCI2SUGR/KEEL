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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.QAR_CIP_NSGAII;


/**
 * <p>Title: Class List</p>
 *
 * <p>Description: In this class implements the structure and methods of a list</p>
 *
 * <p>Company: KEEL</p>
 *
 * @author Alvaro Enciso Ruiz (UGR) 10/10/2008
 * @version 1.0
 * @since JDK 1.5
 */
public class Lists {
    public int index;
    public Lists parent;
    public Lists child;
    
    public Lists(){
    	index = -1;
    	parent = null;
    	child = null;
    }
    
    /**
	 * Insert an element X into the list at location specified by NODE 
	 * @param node list in which we want introduce an element 
	 * @param x element to introduce
	 */
    public void insert (Lists node, int x) {
        Lists temp;
        
		if (node == null) {
            System.out.println("Error!! asked to enter after a NULL pointer, hence exiting ");
            System.exit(1);
        }

        temp = new Lists();
        temp.index = x;
        temp.child = node.child;
        temp.parent = node;

		if (node.child != null)  node.child.parent = temp;

        node.child = temp;
    }

    
    /**
	 * Delete the node NODE from the list  
	 * @param node node which we want to delete
	 */
    public Lists del (Lists node) {
        Lists temp;
        
		if (node==null) {
        	System.out.println(" Error!! asked to delete a NULL pointer, hence exiting");
        	System.exit(1);
        }
        
		temp = node.parent;
        temp.child = node.child;

		if (temp.child!=null)  temp.child.parent = temp;

        return (temp);
    } 
}
