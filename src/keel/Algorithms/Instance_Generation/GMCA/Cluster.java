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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package keel.Algorithms.Instance_Generation.GMCA;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import java.util.*;

/**
 * Represents a cluster
 * @author diegoj
 */
public class Cluster
{
    /** Statistical/debug counter 1 */
    static int i=0;
    /** Statistical/debug counter 2 */
    static int i2=0;
    
    /** Prototype set of the cluster */
    PrototypeSet set;
    
    /** Prototype representative of the cluster (the center of the cluster) */
    private Prototype representative;
    
    /** Id of the cluster */
    public int id = 0;
    
    /** Radius of the cluster: longest distance between center and any other protototype. */
    private double radius = 0.0;  
    
    /*public static void setClusterSet(ClusterSet c)
    {
        clusterSet = c;
    }*/
    
    /**
     * Update the center value of the cluster using average operation.
     */
    private void updateRepresentative()
    {
        //clusterSet.assignment.remove(representative);
        representative = set.avg();
        //clusterSet.assignment.put(representative, this);
        representative.setIndex((i++));
    }
    /*
    private void updateAssignment(Prototype p)
    {
        clusterSet.assignment.put(p, this);
    }
    
    
    
    private void updateAssignment()
    {
        updateRepresentative();
        for(Prototype p : set)
            updateAssignment(p);
    }
    
    private boolean removeAssignment(Prototype p)
    {
        return (clusterSet.assignment.remove(p) == null);
    }
    */
    
    /**
     * Distance between clusters.
     * @param c Other cluster.
     * @return Eucludian distance between the center of the clusters.
     */
    public double d(Cluster c)
    {
        return Distance.d(representative, c.representative);
    }
    
    /**
     * Distance between clusters.
     * @param c1 One of the clusters.
     * @param c2 Other cluster.
     * @return Eucludian distance between the center of the clusters.
     */
    static double d(Cluster c1, Cluster c2)
    {
        return Distance.d(c1.representative, c2.representative);
    }
    
    /**
     * Update the radius value (longest distance between center and other protototype).
     */
    private void updateRadius()
    {
        Pair<Prototype,Double> rr = set.radius(representative);
        radius = rr.second();
    }
    
    /**
     * Cluster constructor using a prototype set and its center.
     * @param s Prototype set.
     * @param representative Center of s.
     */
    public Cluster(PrototypeSet s, Prototype representative)
    {
        id = i2++;
        set = s;        
        this.representative = representative;
        /*clusterSet.clusters.add(this);
        updateAssignment();*/
        updateRadius();
    }
    
    /**
     * Cluster constructor using a prototype set and its center.
     * @param s Prototype set.
     */
    public Cluster(PrototypeSet s)
    {
        id = i2++;
        set = s;
        //clusterSet.clusters.add(this);
        updateRepresentative();
        //updateAssignment();
        updateRadius();
    }
    
    /**
     * Cluster constructor using a prototype set and its center.
     * @param p Prototype which forms an one-prototype prototype set.
     */
    public Cluster(Prototype p)
    {
        id = i2++;
        set = new PrototypeSet();
        set.add(p);
        representative = p;
        radius = 0.0;
        /*clusterSet.clusters.add(this);
        updateAssignment();*/
    }
    
    /**
     * Removes a prototype of the cluster.
     * @param p Prototype to be removed.
     * @return TRUE if p is removed, FALSE in other chase.
     */
    public boolean remove(Prototype p)
    {
        boolean suc1 = set.remove(p);
        //boolean suc2 = removeAssignment(p);
        return suc1; //&& suc2;
    }
    
    /*protected void delete()
    {
        //Debug.errorln("Borramos cluster " + id + " " + set.size() + " elementos");
        //Debug.errorln("Borramos representative "+ representative.getIndex() +" del cluster " + id);
        //clusterSet.clusters.remove(this);
        //removeAssignment(this.representative);
    }*/
    
    /**
     * Add prototype to the cluster.
     * @param p Prototype to be added.
     */
    public void add(Prototype p)
    {
        set.add(p);
        //updateAssignment(p);
        updateRepresentative();
        updateRadius();
    }

    
    /*public void setRepresentative(Prototype p)
    {
        this.representative = p;
        updateRadius();
        updateRepresentative();
    }*/
    
    /**
     * Informs if the cluster is empty.
     * @return TRUE if there are no prototypes in the cluster, FALSE if so.
     */
    boolean isEmpty(){ return set.isEmpty(); }

    /**
     * Gets the representative of the cluster
     * @return Prototype representative of the cluster.
     */
    public Prototype getRepresentative()
    {
        return representative;
    }
    
    /**
     * Gets the radius of the cluster.
     * @return The farthest prototype to the representative, and the distance between them.
     */    
    public Pair<Prototype,Double> getRadius()
    {
        return new Pair<Prototype,Double>(representative,radius);
    }
    
    /**
     * Gets the radius length  of the cluster.
     * @return The distance between the farthest prototype and the representative.
     */    
    public double getRadiusLength(){ return radius;  }

    /**
     * Gets the prototype set of the cluster.
     * @return Prototype set of the cluster.
     */    
    public PrototypeSet getSet(){ return set; }
    
    /**
     * Gets the prototype set of the cluster.
     * @return Prototype set of the cluster.
     */    
    public PrototypeSet getPrototypeSet(){ return getSet(); }
    
    /**
     * Gets the label of all prototypes of the cluster.
     * @return Label of the representative/all prototypes of the cluster.
     */    
    public double label(){ return representative.label(); }
    
    /**
     * Gets the label of all prototypes of the cluster.
     * @return Label of the representative/all prototypes of the cluster.
     */   
    public double getLabel(){ return representative.label(); }
    
    /**
     * Gets the number of prototypes that forms the cluster.
     * @return Size of the prototype set.
     */   
    public int size(){ return set.size(); } 
    
    /*public Cluster merge(Cluster other)
    {
        PrototypeSet mergedSet = set.union(other.set);        
        Cluster merged = new Cluster(mergedSet);
        return merged;
    }*/
    
    /**
     * Merges two cluster but not including the representatives of the cluster arguments.
     * @param other Cluster to be merged with.
     * @return Cluster that its center is an average prototype of the representatives, and includes as set the union of the prototype sets.
     */
    public Cluster mix(Cluster other)
    {
        //this.delete();
        //other.delete();
        PrototypeSet mergedSet = set.union(other.set);
        //Prototype avg = mergedSet.avg();
        //avg.setIndex(10000+(i++));        
        //mergedSet.remove(representative);//we need to remove early reps
        //mergedSet.remove(other.representative);//we need to remove early reps
        Cluster merged = new Cluster(mergedSet);
        //Debug.errorln("Rep. " + merged.representative.getIndex() + " de cluster " + merged.id);
        //clusterSet.assignment.put(avg, merged);
        //clusterSet.assignment.put(avg, merged);
        return merged;
    }
}

