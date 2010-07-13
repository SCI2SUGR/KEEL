/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S·nchez (luciano@uniovi.es)
    J. Alcal·-Fdez (jalcala@decsai.ugr.es)
    S. GarcÌa (sglopez@ujaen.es)
    A. Fern·ndez (alberto.fernandez@ujaen.es)
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

package keel.Algorithms.Instance_Generation.VQ;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import keel.Algorithms.Instance_Generation.LVQ.*;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;
import java.util.*;

/**
 * Cluster of a prototype set.
 * @author diegoj
 */
public class Cluster
{
    /** Centroid/Center of the cluster. */
    protected Prototype centroid;
    /** Set of prototypes (minus the center) that forms the cluster. */
    protected PrototypeSet set;
    /** Class of all the prototypes of the Cluster. */
    protected double label;

    /**
     * Asssigns the center of the cluster.
     * @param centroid New center of the cluster.
     */
    public void setCentroid(Prototype centroid){ this.centroid = centroid; }

    /**
     * Asssigns the class of the cluster.
     * @param label New label of the cluster.
     */
    public void setLabel(double label){ this.label = label; }

    /**
     * Asssigns the set of prototypes of the cluster.
     * @param set New set of the cluster.
     */
    public void setSet(PrototypeSet set){ this.set = set; }

    /**
     * Get the class of the cluster.
     * @return class of the elements of the cluster.
     */
    public double getLabel(){ return label; }

    /**
     * Get the elements of the cluster.
     * @return set of the cluster.
     */
    public PrototypeSet getPrototypeSet(){ return set; }
    
    /**
     * Add one element to the cluster.
     * @param t New prototype to be added to the cluster.
     */
    public void add(Prototype t){ set.add(t); }
    
    /**
     * Get one element of the cluster.
     * @param i Index of the element to be returned.
     * @return ith prototype of the cluster.
     */
    public Prototype get(int i){ return set.get(i); }
    
    /**
     * Construct a new cluster.
     * @param centroid Centroid of the cluster.
     * @param set Set of the cluster.
     */
    public Cluster(Prototype centroid, PrototypeSet set)
    {
        this.centroid = centroid;
        this.set = set;
        this.label = centroid.label();
    }
    
    /**
     * Construct a new cluster.
     * @param set Set of the cluster.
     * @param centroid Centroid of the cluster.    
     */
    public Cluster(PrototypeSet set, Prototype centroid)
    {
        this.centroid = centroid;
        this.set = set;
        this.label = centroid.label();
    }
    
    /**
     * Returns the center of the cluster.
     * @return Center of the cluster.
     */
    public Prototype center(){ return centroid; }
    
    /**
     * Returns the center of the cluster.
     * @return Center of the cluster.
     */
    public Prototype getCentroid(){ return centroid; }
    
    /**
     * Returns the Medium Squared Error of the cluster.
     * @return MSE of the cluster.
     */
    public double fitness()
    {
        double acca = 0.0;
        for(Prototype pa : set)
            acca += Distance.dSquared(pa, centroid);
        return acca;
    }
    
    /**
     * Computes the average prototype of the cluster.
     * @return Average prototype of the cluster.
     */
    public Prototype avg(){ return set.avg(); }
    
    /**
     * Returns the size of the cluster.
     * @return Size of the cluster.
     */
    public int size(){ return set.size(); }
    
    /**
     * Use the nearest neighbor condition to make a partition in two cluster which have got this centers.
     * @param center1 Center of the first set.
     * @param center2 Center of the second set.
     * @return Pair of clusters.
     */
    protected Pair<Cluster, Cluster> partititonWhoseCentersAre(Prototype center1, Prototype center2)
    {
        PrototypeSet one = new PrototypeSet();
        PrototypeSet two = new PrototypeSet();
        
        PrototypeSet centerSet = new PrototypeSet();
        centerSet.add(center1);
        centerSet.add(center2);
        Debug.endsIf(center1 == center2, "Centers are the same, it is not allowed.");
        
        for(Prototype p : set)
        {
            Prototype q = centerSet.nearestTo(p);
            if(q == center1)
            {
                one.add(p);
                //Debug.errorln("ONE");
            }
            else if(q == center2)
            {
                two.add(p);
                //Debug.errorln("TWOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
            }
            else if(q==null)
                Debug.goout("q is null");
            else
                Debug.goout("partitionWhoseCentersAre gone wrong wrong wrong!");
        }
        
        Cluster c1 = new Cluster(one, center1);
        Cluster c2 = new Cluster(two, center2);
        return new Pair<Cluster,Cluster>(c1, c2);
    }
    
    /**
     * Distorsion of the cluster (sum of distances of the prototypes to the center).
     * @return Sumatory of the distances of the set to the center.
     */
    protected double distorsion(Prototype center)
    {
        double acc = 0;
        for(Prototype p : set)
            acc += Distance.d(p, center);
        return acc/(double)set.size();
    }
    
    /**
     * Distorsion of two sets, given two centers.
     * @param centers Centers of the pair of clusters.
     * @param part Clusters.
     * @return Sum of the distorison(part1,center1)+distorison(part2,center2)
     */
    protected static double distorsion(Pair<Prototype,Prototype> centers, Pair<Cluster,Cluster> part)
    {
        double d1 = part.first().distorsion(centers.first());
        double d2 = part.second().distorsion(centers.second());
        return d1 + d2;        
    }
    
    /*public boolean isNearestPrototype(Prototype pseudoCenter)
    {
        int count = 0;
        for(Prototype p : set)
            if(pseudoCenter != p)
            {
                Prototype q = set.nearestTo(p);
                if(Distance.d(p, pseudoCenter) <= Distance.d(p, q))
                    ++count;
            }         
        return count > 0;
    }*/
    
    /**
     * Part the cluster by the LBG method.
     * @param epsilon Maximum error tolerated.
     * @return A partition of the cluster in two new clusters.
     */
    public Pair<Cluster,Cluster> _2LBGPartition(double epsilon)
    {
        int size = set.size();
        int last = size-1;
        double D1 = Double.POSITIVE_INFINITY;
        ArrayList<Integer> r = null;
        //boolean nearestOfSomebody = false;
        //do
        //{
            r = RandomGenerator.generateDifferentRandomIntegers(0, last, 2);
            Prototype p1 = set.get(r.get(0));
            Prototype p2 = set.get(r.get(1));
            //Debug.errorln("Elegimos " + r.get(0) + " last es " + last);
            //Debug.errorln("Elegimos " + r.get(1) + " last es " + last);
        //    nearestOfSomebody = isNearestPrototype(p1) && isNearestPrototype(p2);
        //}
        //while(!nearestOfSomebody);
        
        //old centers
        Pair<Prototype,Prototype> Y = new Pair<Prototype,Prototype>(p1, p2);
        
        //empieza el juego
        boolean termination = false;
        Pair<Cluster,Cluster> partition = null;
        int it=0;
        do
        {
            //Debug.errorln("Iteraci√≥n " + (it++));
            //Partici√≥n por la nearest-neighbor condition
            Pair<Cluster, Cluster> P = partititonWhoseCentersAre(Y.first(), Y.second());

            double Dm = distorsion(Y, P);
            //Debug.errorln("Distorsion: " + Dm);
            double value = Math.abs(D1 - Dm) / Dm;
            //Debug.errorln("Cociente = " + value + " epsilon es " + epsilon);
            if (value <= epsilon)
            {
                //return P;
                termination = true;
                partition = P;
            }
            else
            {
                D1 = Dm;
                Prototype newCenter1 = P.first().avg();//CC
                Prototype newCenter2 = P.second().avg();//CC
                Y = new Pair<Prototype, Prototype>(newCenter1, newCenter2);
            }
        } while (!termination);
        return partition;
    }
    
    /**
     * Returns the centers of the cluster obtained by LBG method.
     * @param epsilon Maximum error tolerated.
     * @return Centers of the LBG clusters.
     */
    public Pair<Prototype,Prototype> centersOfLBGCLuster(double epsilon)
    {
        Pair<Cluster,Cluster> c = _2LBGPartition(epsilon);
        return new Pair<Prototype,Prototype>(c.first().center(), c.second().center());
    }
    
    /**
     * Informs if the centroid of the cluster is its nearest prototype.
     * @param p Prototype to be tested.
     * @return TRUE if p nearest prototype is the centroid of the cluster, FALSE in other chase.
     */
    public boolean isCentroidItsNearestPrototoype(Prototype p)
    {
        return centroid == set.nearestTo(p);
    }
}

