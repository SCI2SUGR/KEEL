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
 *
 * File: Graph.java
 *
 * The graph of the experiment
 *
 * @author Juli�n Luengo Mart�n (modifications 19/04/2009)
 * @author Ana Palacios Jimenez and Luciano Sanchez Ramons 23-4-2010 (University of Oviedo)
 * @version 1.0
 * @since JDK1.5
 */
package keel.GraphInterKeel.experiments;

import java.util.*;
import java.io.*;

public class Graph implements Serializable {

    private Vector nodes;
    private Vector arcs;
    private String name;
    private long seed;
    private transient boolean modified;
    protected transient boolean autoSeed;
    private int id;
    private DataSet backupDataSet;
    private int type;
    public int objective;

    /**
     * Builder
     */
    public Graph() {
        nodes = new Vector();
        arcs = new Vector();
        name = null;
        modified = false;
        //Old seed mode, generated from the system uptime
        /*seed = System.currentTimeMillis();
        if (seed % 2 == 0) {
        seed += 1;
        }*/
        //new seed mode: generated statically
        seed = 12345678;
        autoSeed = true;
        id = 0;
        backupDataSet = null;
    }

    /**
     * Gets all the nodes from this graph
     * @return a vector with all the nodes
     */
    public Vector getNodes() {
        return nodes;
    }

    /**
     * Sets a new set of nodes
     * @param _nodes the new set of nodes
     */
    public void setNodes(Vector _nodes) {
        nodes = _nodes;
    }

    /**
     * Gets all the arcs in the graph
     * @return the vector with all the arcs
     */
    public Vector getArcs() {
        return arcs;
    }

    /**
     * Puts a new set of arcs
     * @param _arcs the new arcs in the graph (should be consistent!)
     */
    public void setArcs(Vector _arcs) {
        arcs = _arcs;
    }

    /**
     * Sets the type of the graph (experiment type)
     * @param type the type of the experiment represented by this graph
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Gets the type of this graph
     * @return the type of the graph
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the seed of the graph (i.e. the seed of the experiment)
     * @param _seed the new seed
     */
    public void setSeed(long _seed) {
        seed = _seed;
        modified = true;
    }

    /**
     * Gets the seed of this graph
     * @return the current seed
     */
    public long getSeed() {
        return seed;
    }

    /**
     * Insert a new node in the graph
     * @param n the new node
     */
    public void insertNode(Node n) {
        nodes.addElement(n);
        modified = true;
    }

    /**
     * Insert a node at the specified position
     * @param n the node to be inserted
     * @param i the position of the insertion
     */
    public void insertNode(Node n, int i) {
        nodes.insertElementAt(n, i);
        modified = true;
    }

    /**
     * Inserts a new arc in the graph
     * @param a the new arc
     */
    public void insertArc(Arc a) {
        arcs.addElement(a);
        modified = true;
    }

    /**
     * Inserts a new arc at the specified position
     * @param a the new arc
     * @param i the position of the insertion
     */
    public void insertArc(Arc a, int i) {
        arcs.insertElementAt(a, i);
        modified = true;
    }

    /**
     * Deletes a node from the graph
     * @param i the index of the node to be deleted
     */
    public void dropNode(int i) {

        // dataset nodes can't be removed
        if (nodes.elementAt(i) instanceof DataSet) {
            // save a copy
            backupDataSet = (DataSet) nodes.elementAt(i);
        }

        nodes.removeElementAt(i);
        modified = true;
    }

    /*   public void dropNodeLQD(Vector<Integer> N) {
    System.out.println("entramos en borrar un nodo");

    System.out.println("el tamaño de nodos es "+this.numNodes());
    for(int i=0;i<N.size();i++)
    {
    nodes.removeElementAt(N.get(i));
    System.out.println("el tamaño de nodos es "+this.numNodes());
    }



    }*/
    /**
     * Drop node
     * @param N Id of the node
     */
    public void dropNodeLQD_move(int N) {

        nodes.removeElementAt(N);
    }

    /**
     * Restore the data set node from a previous backup
     */
    public void restoreDataSet() {
        if (backupDataSet == null) {
            return;
        }
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.elementAt(i) instanceof DataSet) {
                backupDataSet = null;
                return;
            }
        }
        nodes.addElement(backupDataSet);
        backupDataSet = null;
    }

    /**
     * Deletes an arc from the graph
     * @param i the index of the arc to be dropped
     */
    public void dropArc(int i) {
        Arc a = (Arc) arcs.elementAt(i);
        arcs.removeElementAt(i);
        modified = true;
    }

    /**
     * Search for the next arc
     * @param id_node Id of the initial node
     * @return Id of the arc
     */
    public int next_arc(int id_node) {


        for (int i = numArcs() - 1; i >= 0; i--) {

            Arc a = getArcAt(i);
            if (a.getSource() == id_node) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Drop arc
     * @param ar Arc to remove
     */
    public void dropArcLQD(int ar) {
        //System.out.println("entramos en borrar un arco");
        Arc a = (Arc) arcs.elementAt(ar);
        //We have to erase the connection between the source node 
        //and destination node
        //int index_origen = a.getSource();
        boolean more = false;
        //int index_destino = a.getDestination();
        System.out.println("el numero de arcos es empieze " + numArcs());
        System.out.println("el numero de nods es " + numNodes());


        System.out.println("el nodo origen es " + a.getSource());
        System.out.println("el nodo destino  es " + a.getDestination());

        //We found the arc in the way
        System.out.println("el arco a borrar es " + ar);
        //remove_arc.addElement(ar);
        for (int i = this.numNodes() - 1; (i >= 0); i--) {
            //if(i== a.getDestination())
            if (this.getNodeAt(i).id == a.getDestination()) {
                System.out.println("el nodo destino del arco es " + this.getNodeAt(i).id);
                //Remove the data of the node due to the conection is removed
                Node destination = this.getNodeAt(i);
                System.out.println("The destination node is " + destination.dsc.getName(0) + " y tamaño de enalces " + destination.dsc.arg.size());
                //we have to remove the information of the source node in this node.
                int eliminated = -1;
                for (int n = 0; n < destination.dsc.arg.size(); n++) {
                    System.out.println("realacion con " + destination.dsc.arg.get(n).before.id + " POSCIION " + n);
                    //destination.dsc.arg.get(n).information();
                    System.out.println("el id del nodo " + a.getSource());
                    //if(destination.dsc.arg.get(n).before.id == this.getNodeAt(a.getSource()).id)
                    if (destination.dsc.arg.get(n).before.id == a.getSource()) {
                        eliminated = n;
                        System.out.println("entra en esto de false");
                    //break;
                    } else {
                        System.out.println("entra en esto de true");
                        more = true;
                    }

                }
                if (eliminated != -1) {
                    destination.dsc.arg.remove(eliminated);
                }


                //the next arc from the destination node

                arcs.removeElementAt(ar);
                System.out.println("el numero de arcos es final " + numArcs());
                //int n_ar=next_arc(i);
                int n_ar = next_arc(this.getNodeAt(i).id);
                if (n_ar != -1) {
                    dropArcLQD(n_ar);
                }


                if (more == false) {
                    nodes.removeElementAt(i);
                }

                System.out.println("el numero de nods finalll es " + numNodes());

                break;
            }
        }


    /*  for (int i = this.numNodes() - 1; (i >= 0); i--)
    {
    if(this.getNodeAt(i).id== index_destino)
    {
    Node destination = this.getNodeAt(i);
    System.out.println("The destination node is "+ destination.dsc.getName(0));
    //we have to remove the information of the source node in this node.
    for(int n=0;n<destination.dsc.arg.size();n++)
    {
    destination.dsc.arg.get(n).information();
    System.out.println("realacion con "+destination.dsc.arg.get(n).before.id);
    if(destination.dsc.arg.get(n).before.id == index_origen)
    {
    destination.dsc.arg.remove(n);
    //break;
    }
    else
    {
    System.out.println("si estuviera realacionado con otro si que entra");
    more=true;
    }
    }

    break;
    }
    }

    arcs.removeElementAt(ar);
    if(more==false)
    {
    System.out.println("The node is not connected with other nodes, so we have to erase this node");
    remove_node.addElement(index_destino);
    next_arc(index_destino);

    }

    dropNodeLQD(remove_node);
     */

    }

    /**
     * Deletes the indexed arc, then insert a new one
     * @param i the index of the arc to be deleted
     * @param a the arc to be inserted
     */
    public void dropAndInsertArc(int i, Arc a) {
        arcs.removeElementAt(i);
        arcs.addElement(a);
        modified = true;
    }

    /**
     * Returns the node at position indicated
     * @param i the index of the node
     * @return the node at position i
     */
    public Node getNodeAt(int i) {
        return (Node) nodes.elementAt(i);
    }

    /**
     * Gets the arc at the indicated position
     * @param i the index of the arc
     * @return the arc at position i
     */
    public Arc getArcAt(int i) {
        return (Arc) arcs.elementAt(i);
    }

    /**
     * Gets the number of nodes of this graph
     * @return the current number of present nodes
     */
    public int numNodes() {
        return nodes.size();
    }

    /**
     * Gets the current number of arcs
     * @return the number of arcs in the graph
     */
    public int numArcs() {
        return arcs.size();
    }

    /**
     * Gets the maximum X value of the graph
     * @return the maximum X (horizontal) value of the graph
     */
    public int getMaxX() {

        if (nodes.size() == 0) {
            return 1280;
        }

        int max = 0;

        for (int i = 0; i < nodes.size(); i++) {
            if (((Node) nodes.elementAt(i)).centre.x > max) {
                max = ((Node) nodes.elementAt(i)).centre.x;
            }
        }

        return max + 100;
    }

    /**
     * Gets the maximum Y value of the graph
     * @return the maximum Y (vertical) value of the graph
     */
    public int getMaxY() {

        if (nodes.size() == 0) {
            return 1024;
        }

        int max = 0;

        for (int i = 0; i < nodes.size(); i++) {
            if (((Node) nodes.elementAt(i)).centre.y > max) {
                max = ((Node) nodes.elementAt(i)).centre.y;
            }
        }

        return max + 50;
    }

    /**
     * Sets the name of this graph
     * @param n the new name
     */
    public void setName(String n) {
        name = n;
    //modificado = true;
    }

    /**
     * Gets the name of this graph
     * @return the current name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the modified status of this graph
     * @param valor the new modified status
     */
    public void setModified(boolean valor) {
        modified = valor;
    }

    /**
     * Gets the current modified status
     * @return the modified status
     */
    public boolean getModified() {
        return modified;
    }

    /**
     * Gets the id of this graph
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets a new id for this graph
     * @param id the new ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Prints in the standard output the graph
     */
    public void write() {
        System.out.println("Nodes:");
        for (int i = 0; i < nodes.size(); i++) {
            System.out.println("Type node " +
                    ((Node) (nodes.elementAt(i))).getType());
        }
        System.out.println("Connections:");
        for (int i = 0; i < numArcs(); i++) {
            System.out.println(((Arc) arcs.elementAt(i)).getSource() + "," +
                    ((Arc) arcs.elementAt(i)).getDestination());
        }
    }

    /**
     * Gets the External Object Description which describes this graph
     * @return the ExternalObjectDescription which represents the graph
     */
    public ExternalObjectDescription getExternalObjectDescription() {
        ExternalObjectDescription dsc = null;
        //System.out.println(" in getDSC() " + nodes.size());
        for (int i = 0; i < nodes.size(); i++) {
            /*System.out.println("getDSC: Type node " +
            ( (Node) (nodes.elementAt(i))).getType());*/
            if (nodes.elementAt(i) instanceof DataSet) {
                // if (((Nodo)(nodos.elementAt(i))).getTipo() == Nodo.tipo_DataSet) {
                dsc = ((DataSet) (nodes.elementAt(i))).dsc;
                break;
            }
        }
        return dsc;
    }

    /**
     * Replace a node
     * @param position Position in the graph
     * @param n New node
     */
    public void replaceNode(int position, Node n) {
        nodes.setElementAt(n, position);
    }
}
