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

package keel.Algorithms.Rule_Learning.ART;
import java.util.*;

/**
 *  Class to represent a node in the tree
 */
class Node{
	
	/** El conjunto de datos si es nodo hoja */
	private Vector data;
	
        /** Atributos por los que se expande el nodo*/
        private Vector<Integer> attributes;
        
        /** Valores por los que se ha expandido el nodo padre*/
        private Vector<Integer> values;

        /**Indice a la clase*/
        private int clas;

	/** Si no es nodo hoja, referencia a sus nodos hijos */
	private Vector<Node> children;
	
	/** El padre del nodo. El padre de la raiz es null */
	private Node parent;

        /** Numero de ejemplos de training que satisfacen la condicion de este nodo*/
        private int support;

	/**
         * Default constructor. Creates an empty node.
	 */
	public Node(){
		data = new Vector();
                attributes = new Vector<Integer>();
                values = new Vector<Integer>();
                clas = -1;
                children = new Vector<Node>();
                support = 0;
	}
	
	
	/**
         * Sets the examples that satisfy the node condition.
	 * @param newData given examples to be set.
	 */
	public void setData( Vector newData ){
		data = newData;
	}
	
	/**
         * Returns the examples that satisfy the node condition.
	 */
	public Vector getData(){
		return data;
	}

        /**
         * Adds an example to the dataset.
	 * @param item 	example to be added.
	 */
        public void addData(Itemset item){
            data.add(item);
        }

	/** Function to set the children of the node.
	 * 
	 * @param nodes 	The children of the node.
	 */
	public void setChildren( Vector<Node> nodes ){
		children = nodes;
	}
	
	/** Function to add a child to the node.
	 * 
	 * @param node 	The new child.
	 */
	public void addChildren( Node node ){
		children.add(node);
	}
	
	/** Returns the number of children of the node.
	 * 
	 */
	public int numChildren(){
			
		return children.size();
	}
	
	/** Returns the children of the node. 
	 * 
	 */
	public Vector<Node> getChildren(){
		return children;
	}

	/** Returns the child with the given index.
	 * 
	 * @param index		The index of the child.
	 */
	public Node getChildren( int index ){
		return children.get(index);
	}
	
	/** Function to set the parent of the node.
	 * 
	 * @param node		The parent of the node.
	 */ 
	public void setParent( Node node ){
		parent = node;
	}
	
	/** Returns the parent of the node. 
	 * 
	 */
	public Node getParent(){
		return parent;
	}

        /**
         * Returns the attributes of the node.
         * @return  the attributes of the node.
         */
        public Vector<Integer> getAttributes() {
            return attributes;
        }

        /**
         * Sets the attributes of the node with the given ones.
         * @param attributes given attributes to be set.
         */
        public void setAttributes(Vector<Integer> attributes) {
            this.attributes = attributes;
        }

        /**
         * Returns the support.
         * @return the support.
         */
        public int getSupport() {
            return support;
        }

        /**
         * Sets the support with the value given.
         * @param support given value to set.
         */
        public void setSupport(int support) {
            this.support = support;
        }

        /**
         * Returns the node values.
         * @return  the node values.
         */
        public Vector<Integer> getValues() {
            return values;
        }
        
        /**
         * Sets the node values with the given ones.
         * @param values given values to set.
         */
        public void setValues(Vector<Integer> values) {
            this.values = values;
        }

        /**
         * Returns the class value of the node.
         * @return  the class value of the node.
         */
        public int getClas() {
            return clas;
        }

        /**
         * Sets the class value of the node.
         * @param clas  given class to set. 
         */
        public void setClas(int clas) {
            this.clas = clas;
        }

};

