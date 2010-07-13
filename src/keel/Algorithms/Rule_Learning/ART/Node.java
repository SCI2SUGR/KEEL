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
         * Crea un nuevo nodo.
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
         * Funcion que establece los itemsets que satisfacen la condicion del nodo
	 * @param newData 	Los itemsets.
	 */
	public void setData( Vector newData ){
		data = newData;
	}
	
	/**
         * Devuelve los itemsets que satisfacen la condicion del nodo
	 */
	public Vector getData(){
		return data;
	}

        /**
         * Funcion que aniade un itemset al conjunto de datos
	 * @param item 	Itemset a aniadir.
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

        public Vector<Integer> getAttributes() {
            return attributes;
        }

        public void setAttributes(Vector<Integer> attributes) {
            this.attributes = attributes;
        }

        public int getSupport() {
            return support;
        }

        public void setSupport(int support) {
            this.support = support;
        }

        public Vector<Integer> getValues() {
            return values;
        }

        public void setValues(Vector<Integer> values) {
            this.values = values;
        }

        public int getClas() {
            return clas;
        }

        public void setClas(int clas) {
            this.clas = clas;
        }

};

