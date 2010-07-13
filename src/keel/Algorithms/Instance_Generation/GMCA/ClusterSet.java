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
 * Pair of clusters class that implements comparator.
 * It is used for sorting the pairs of clusters in inter-cluster ascending order.
 */
class PairOfClusters implements Comparable<PairOfClusters>
{
    /** Pair of clusters. */
    Pair<Cluster,Cluster> pair;
    /** Distance between the pair of clusters. */
    double distance;
    /** Label of the clusters.*/
    double label;

    /**
     * Returns the distance between the clusters.
     * @return Distance beteween the clusters.
     */
    public double getDistance() { return distance;  }

    /**
     * Label of the clusters.
     * @return Label of the clusters.
     */
    public double getLabel() { return label; }

    /**
     * Returns the pair of cluster.
     * @return Pair of clusters.
     */
    public Pair<Cluster, Cluster> getPair() { return pair; }
    
    /**
     * Constructor.
     * @param one One cluster.
     * @param two Other cluster.     
     */
    public PairOfClusters(Cluster one, Cluster two)
    {
        pair = new Pair<Cluster,Cluster>(one,two);
        distance = Cluster.d(one, two);
        label = one.label();
    }
    
    /**
     * Constructor.
     * @param one One cluster.
     * @param two Other cluster.
     * @param dist Distance between one and two.
     */
    public PairOfClusters(Cluster one, Cluster two, double dist)
    {
        pair = new Pair<Cluster,Cluster>(one,two);
        distance = dist;
        label = one.label();
    }
    
    /**
     * Overriding of the compareTo function.
     * @param other Other pair of clusters.
     * @return -1 if distance is smaller, 0 equal or 1 greater than other's clusters distance.
     */
    @Override
    public int compareTo(PairOfClusters other)
    {
        if(distance < other.distance)
            return -1;
        else if(distance == other.distance)
            return 0;
        return 1;
    }
}


/**
 * Set of the clusters.
 * @author diegoj
 */
public class ClusterSet
{
   /** Set of clusters. */
    ArrayList<Cluster> clusters;
   /** Assignment of cluster to each prototype.  */
    HashMap<Prototype,Cluster> assignment;
   // Assignment of cluster to each prototype (used to restore the set).  
    //HashMap<Prototype,Cluster> savedAssignment;
    Cluster mixed = null;
    Cluster a = null;
    Cluster b = null;
    
    /*private void saveAssignment()
    {
        ArrayList<Prototype> ps = new ArrayList<Prototype>(assignment.keySet());
        for(Prototype p : ps)
            savedAssignment.put(p, assignment.get(p));
    }*/
    
    /*private void restoreAssignment()
    {
        ArrayList<Prototype> ps = new ArrayList<Prototype>(savedAssignment.keySet());
        for(Prototype p : ps)
            if(assignment.containsKey(p)  &&  assignment.get(p)!=savedAssignment.get(p))
            {
                assignment.get(p).remove(p);
                assignment.put(p, savedAssignment.get(p));
                savedAssignment.get(p).add(p);
            }
        remove(mixed);
        add(a);
        add(b); 
        mixed = null;
        a = null;
        b = null;
    }*/
    
    //public void save(){ saveAssignment(); }
    
    //public void restore(){ restoreAssignment(); }
    
    /**
     * Clone the cluster set.
     * @return New clusterset that is a hard-copy of present.
     */
    @Override
    public ClusterSet clone()
    {
        ClusterSet copy = new ClusterSet();
        copy.clusters = new ArrayList<Cluster>();
        copy.assignment = new HashMap<Prototype,Cluster>();
        for(Cluster c : clusters)
            copy.clusters.add(c);
        ArrayList<Prototype> ps = new ArrayList<Prototype>(assignment.keySet());
        for(Prototype p : ps)
            copy.assignment.put(p, assignment.get(p));
        return copy;
    }
    
    public ClusterSet()
    {
        clusters = new ArrayList<Cluster>();
        assignment = new HashMap<Prototype,Cluster>();        
        //savedAssignment = new HashMap<Prototype,Cluster>();        
    }
    
    /**
     * Gets cluster in ith position.
     * @param i Position of the cluster.
     * @return Cluster in ith position.
     */
    public Cluster get(int i){ return clusters.get(i); }
    
    /**
     * Test that every prototype of the set has got an assigned cluster .
     * @param s Prototype set to be tested.
     */
    void test(PrototypeSet s)
    {
        for(Prototype p : s)
            if(!assignment.containsKey(p))
                Debug.errorln("OJJJJJJOOOOOOO Prototipo " + p.getIndex() + " no tiene cluster");
    }
    
    /*protected void makeClusterSetByClasses(PrototypeSet training)
    {
        clusters = new ArrayList<Cluster>();
        assignment = new HashMap<Prototype,Cluster>();
        ArrayList<Double> classes = training.nonVoidClasses();
        for(double c : classes)
        {
            PrototypeSet trainingC = training.getFromClass(c);
            Prototype avgC = trainingC.avg();
            Cluster clusterC = new Cluster(trainingC,avgC);
            clusters.add(clusterC);
            for(Prototype p : trainingC)    
                assignment.put(p, clusterC);
        }
    }
    
    protected void makeInitialClusterSet(PrototypeSet training)
    {
        clusters = new ArrayList<Cluster>();
        assignment = new HashMap<Prototype,Cluster>();
        for(Prototype p : training)
        {
            Cluster clusterP = new Cluster(p);
            clusters.add(clusterP);
            assignment.put(p, clusterP);
        }
    }*/
    
    /**
     * Gets the cluster of a prototype.
     * @param p Prototype.
     * @return Cluster of p.
     */
    public Cluster get(Prototype p){ return assignment.get(p); }
    
    /*public void removeWhoseRepresentativeIs(Prototype p)
    {
        remove(assignment.get(p));
    }*/
    
    /**
     * Merge two clusters.
     * @param Ca One cluster.
     * @param Cb Other cluster.
     * @return Merged cluster.
     */
    public Cluster merge(Cluster Ca, Cluster Cb)
    {
        //Debug.force(assignment.containsKey(a), "prototype (a) "+ a.getIndex()  +" is NOT a representative");
        //Debug.force(assignment.containsKey(b), "prototype (b) "+ b.getIndex()  +" is NOT a representative");
        //Debug.endsIfNull(assignment.get(a), "Cluster de a es NULL");
        //Debug.endsIfNull(assignment.get(b), "Cluster de b es NULL");
        //Cluster cA = assignment.get(a);
        //Cluster cB = assignment.get(b);
        //Debug.errorln("MERGE de CLUSTERS " + Ca.id + " y " + Cb.id);
        Cluster CmixAB = Ca.mix(Cb);
        assignment.put(CmixAB.getRepresentative(), CmixAB);
        //Debug.errorln("HHHHHHHHHHHHHHHHHHAntes " + size());
        add(CmixAB);
        remove(Ca);
        remove(Cb);
        //Debug.errorln("Borra cluster " + Ca.id);
        //Debug.errorln("Borra cluster " + Cb.id);        
        //Debug.errorln("HHHHHHHHHHHHHHHHHHDESPUES " + size());
        mixed = CmixAB;
        a = Ca;
        b = Cb;
        return CmixAB;
    }
    
    /**
     * Remove a cluster of the set.
     * @param c Cluster to be removed.
     * @return TRUE if it has been removed, FALSE in other chase.
     */
    public boolean remove(Cluster c)
    {
        for(Prototype p : c.set)
            if(assignment.get(p)==c)
                assignment.remove(p);
        return clusters.remove(c);
    }
    
    /**
     * Adds a cluster of the set.
     * @param c Cluster to be added.
     */
    public void add(Cluster c)
    {
        clusters.add(c);        
        for(Prototype p : c.set)
            assignment.put(p, c);
        assignment.put(c.getRepresentative(), c);
    }
    
    
    /*public Pair<Cluster,Cluster> pairOfClustersWithSameClass()
    {
        Pair<Cluster,Cluster> pair = null;
        double dMin = Double.POSITIVE_INFINITY;
        int _size = clusters.size();
        for(int i=0; i<_size; ++i)
            for(int j=i+1; j<_size; ++j)
            {
                Cluster ci = clusters.get(i);
                Cluster cj = clusters.get(j);
                if(ci.label() == cj.label() && ci!=cj)
                {
                    double dij = ci.d(cj);
                    if(dij < dMin)
                    {
                        dMin = dij;
                        pair = new Pair<Cluster,Cluster>(ci,cj);
                    }
                }
            }
        return pair;
    }*/
    
    /*public Pair<Cluster,Double> nearestTo(Cluster c)
    {
        Pair<Cluster,Double> pair = null;
        double dMin = Double.POSITIVE_INFINITY;
        for(Cluster k : clusters)
            if(k != c)
            {
                double d = k.d(c);    
                if(d < dMin)
                {
                    dMin = d;
                    pair = new Pair<Cluster,Double>(k,dMin);
                }
            }
        return pair;
    }*/
    
    /*public Pair<Cluster,Double> nearestWithSameClassAs(Cluster c)
    {
        Pair<Cluster,Double> pair = null;
        double label = c.label();
        double dMin = Double.POSITIVE_INFINITY;
        for(Cluster k : clusters)
            if(k != c  && k.label()==label)
            {
                double d = k.d(c);    
                if(d < dMin)
                {
                    dMin = d;
                    pair = new Pair<Cluster,Double>(k,dMin);
                }
            }
        return pair;
    }*/
    
    /**
     * Returns a list of pairs of clusters in inter-cluster ascending order.
     * @return List of pair of clusters closest to nearest clusters.
     */
    public ArrayList<Pair<Cluster,Cluster>> nearestClustersWithSameClass()
    {
        ArrayList<PairOfClusters> unsorted = new ArrayList<PairOfClusters>(clusters.size());
        //HashSet<Cluster> chosen = new HashSet<Cluster>();
        int _size = clusters.size();
        for(int i=0; i<_size; ++i)
            for(int j=i+1; j<_size; ++j)
            {
                Cluster ci = get(i);
                Cluster cj = get(j);
                double dij = ci.d(cj);
                unsorted.add(new PairOfClusters(ci, cj, dij));
            }
        Collections.sort(unsorted);
        ArrayList<Pair<Cluster,Cluster>> sorted = new ArrayList<Pair<Cluster,Cluster>>(unsorted.size());
        for(PairOfClusters c : unsorted)
            sorted.add(c.getPair());

        ArrayList<Pair<Cluster,Cluster>> editedSorted = new ArrayList<Pair<Cluster,Cluster>>(sorted.size());
        int sortedSize = sorted.size();
        HashSet<Cluster> chosen = new HashSet<Cluster>();
        for(int i=0; i<sortedSize; ++i)
        {
            Pair<Cluster,Cluster> ci = sorted.get(i);
            if(!chosen.contains(ci.first()) && !chosen.contains(ci.second()))
            {
                chosen.add(ci.first());
                chosen.add(ci.second());
                editedSorted.add(ci);    
            }
        }
        //Debug.errorln("SORTED: " + sorted.size());
        //Debug.errorln("EDITED SORTED: " + editedSorted.size());
        return editedSorted;
    }
    
    /**
     * Returns the set of clusters.
     * @return Set of clusters.
     */
    public ArrayList<Cluster> getClusters()
    {
        return clusters;
    }
    
    /**
     * Returns the maximum radius length of set of clusters.
     * @return Maximum radius length of the set of clusters.
     */
    public double maxRadiusLength()
    {
        double rMax = Double.NEGATIVE_INFINITY;
        //ArrayList<Cluster> clusters = getClusters();
        for(Cluster c : clusters)
        {
            double cR = c.getRadiusLength();
            if(rMax < cR)
                rMax = cR;
        }
        return rMax;
    }
    
     /**
     * Returns the maximum radius length of set of clusters of one class.
     * @param k Class that must have the clusters.
     * @return Maximum radius length of the set of clusters with k-class.
     */
    public double maxRadiusLengthOfClass(double k)
    {
        double rMax = Double.NEGATIVE_INFINITY;
        //ArrayList<Cluster> clusters = getClusters();
        for(Cluster c : clusters)
            if(c.getRepresentative().label() == k)
            {
                double cR = c.getRadiusLength();
                if(rMax < cR)
                    rMax = cR;
            }
        return rMax;
    }
    
    /**
     * Move one prototype to a cluster.
     * @param p Prototype to be moved.
     * @param c Destination of p.
     */
    public void moveTo(Prototype p, Cluster c)
    {
        Cluster clusterOfP = assignment.get(p);
        clusterOfP.remove(p);
        if(clusterOfP.isEmpty())
            remove(clusterOfP);
        c.add(p);//add prototype to cluster
        assignment.put(p, c);//add prototype to cluster      
    }
    
    /**
     * Returns the cluster of a prototype.
     * @param p Prototype to be its cluster returned.
     * @return Cluster that contains the prototype p.
     */
    public Cluster getClusterOf(Prototype p)
    {
        return assignment.get(p);
    }
    
    /**
     * Gets the number of clusters of the set.
     * @return Number of present clusters.
     */
    public int size(){ return getClusters().size(); }
    
}

