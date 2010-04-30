/**
 * @author Juli�n Luengo Mart�n (modifications 19/04/2009)
 * @author Ana Palacios Jimenez and Luciano Sanchez Ramons 23-4-2010 (University of Oviedo)
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
     public void dropNodeLQD(int i) {
        nodes.removeElementAt(i);
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
    
    public void dropArcLQD(int ar) 
    {
        Arc a = (Arc) arcs.elementAt(ar);
        //We have to erase the connection between the source node 
        //and destination node
        int index_origen = a.getSource();
        int iden_origen=-1;
        int index_destino = a.getDestination();
        
         for (int i = this.numNodes() - 1; (i >= 0); i--) 
         {
             if(index_origen==i)
             {
                iden_origen = this.getNodeAt(i).id;
                System.out.println("The source node is "+ iden_origen);
                break;
             }
         }
        
         for (int i = this.numNodes() - 1; (i >= 0); i--) 
         {
            if(index_destino==i)
            {
                Node destination = this.getNodeAt(i);
                System.out.println("The destination node is "+ destination.dsc.getName(0));
                //we have to remove the information of the source node in this node.
                for(int n=0;n<destination.dsc.arg.size();n++)
                {
                    if(destination.dsc.arg.get(n).before.id == iden_origen)
                    {
                        destination.dsc.arg.remove(n);
                        break;
                    }
                }
                
                break;
            }           
         }
        
        arcs.removeElementAt(ar);
        
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

    public void replaceNode(int position, Node n) {
        nodes.setElementAt(n, position);
    }
}
