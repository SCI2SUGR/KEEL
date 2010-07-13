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
 
package keel.Algorithms.Instance_Generation.PNN;
import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;
import org.core.*;
import java.util.*;
import keel.Algorithms.Instance_Generation.utilities.*;

/**
 * Matrix of distances between two sets of prototypes.
 */
public class MatrixOfDistances
{
    /** One prototype set */
    protected PrototypeSet A;
    /** Other prototype set */
    protected PrototypeSet B;
    /** Prototypes added to the set, to be compared both A and B (it is not so much used) */
    protected PrototypeSet orphans;
    /** Warehouse of distance between prototypes in A and prototypes in B */
    protected HashMap<Prototype, HashMap<Prototype, Double> > matrix;
    //Para cada prototipo de B, un hashset de todos los prototipos que lo apuntan
    /** For each B-prototype, there is a hashset of all the others prototypes which point to it. */
    protected HashMap<Prototype,HashSet<Prototype>> invertedBList;

    /**
     * Use d-squared, its faster than euclidean one.
     * @param a One prototype.
     * @param b Other prototype.
     * @return Distance between a and b.
     */
    double d(Prototype a, Prototype b)
    {
        return Distance.dSquared(a,b);
    }
    
    /**
     * Construct a matrix of the distances between elements of the set A and B.
     * @param A One prototype set.
     * @param B Other prototype set.
     */
    public MatrixOfDistances(PrototypeSet A, PrototypeSet B)
    {
        this.A = A;
        this.B = B;
        this.orphans = new PrototypeSet();
        matrix = new HashMap<Prototype,HashMap<Prototype, Double>>();
        invertedBList = new HashMap<Prototype,HashSet<Prototype>>();
        for(Prototype b : B)
            invertedBList.put(b, new HashSet<Prototype>());
        for(Prototype a : A)
        {
            matrix.put(a, new HashMap<Prototype, Double>());
            for(Prototype b : B)
                if(a!=b)
                {
                    invertedBList.get(b).add(a);//lista invertida de b a->b
                    //matrix.get(a).put(b, Distance.d(a, b));
                    matrix.get(a).put(b, d(a, b));                    
                }
        }
    }
    
    /**
     * Get the distance between two prototypes.
     * @param a A prototype.
     * @param b Other prototype.
     * @return Distance between a and b.
     */
    public double get(Prototype a, Prototype b)
    {
        double dist = 0.0;
        //if(!a.equals(b))
        //if(a!=b)
        //{
            if(matrix.containsKey(a))
                dist = matrix.get(a).get(b);
            else if(matrix.containsKey(b))
                dist = matrix.get(b).get(a);    
            else
                dist = -1.0;//used only for ours
        //}
        return dist;
    }
    
     /**
     * Remove a prototype to the matrix of distances.
     * @param a prototype to be erased.
     */
    public boolean removeFromA(Prototype a)
    {
        boolean present = matrix.containsKey(a);
        if(present)
            matrix.remove(a);
        return present;
    }
    
    /**
    * Remove a prototype to the matrix of distances.
    * @param x prototype to be erased.
    * @return TRUE if it was removed, FALSE if not.
    */
    public boolean removeFromB(Prototype x)
    {
        int _size = B.size();
        boolean found = false;
        Prototype Bi = null;
        for (int i = 0; i < _size && !found; ++i)
        {
            Bi = B.get(i);
            found = (x == Bi);
        }
        if (found)
        {
            ArrayList<Prototype> list = new ArrayList<Prototype>(invertedBList.get(Bi));
            for (Prototype p : list)
                matrix.get(p).remove(x);
            
        }
        return found;
    }
    
    /**
     * Remove a prototype to the matrix of distances.
     * @param x prototype to be erased.
     * @return TRUE if it was removed, FALSE if not. 
     */
    public boolean remove(Prototype x)
    {
        if(matrix.containsKey(x))
            return removeFromA(x);
        else
            return removeFromB(x);
        //return false;
    }
       
    /**
     * Add a new prototype to the matrix of distances.
     * @param x New prototype to be added.
     */
    public void add(Prototype x)
    {
        orphans.add(x);
        HashMap<Prototype, Double> xRow = new HashMap<Prototype, Double>();
        matrix.put(x, xRow);        
        for(Prototype a : A)
            if(x!=a)
                xRow.put(a, Distance.d(x, a));
        for(Prototype b : B)
            if(x!=b && !xRow.containsKey(b))
                //xRow.put(b, Distance.d(x, b));
                xRow.put(b, d(x, b));
    }
    
    /**
     * Add a new prototype to the matrix of distances  (subset A).
     * @param x New prototype to be added to A set.
     */
    public void addToA(Prototype x)
    {
        HashMap<Prototype, Double> xRow = new HashMap<Prototype, Double>();
        matrix.put(x, xRow);
        for(Prototype b : B)
            if(x!=b  &&  !xRow.containsKey(b))
                //xRow.put(b, Distance.d(x, b));
                xRow.put(b, d(x, b));
    }
    
    /**
    * Add a new prototype to the matrix of distances (subset B).
    * @param x New prototype to be added to B set.
    */
    public void addToB(Prototype x)
    {
        invertedBList.put(x,new HashSet<Prototype>());
        HashMap<Prototype, Double> xRow = new HashMap<Prototype, Double>();
        matrix.put(x, xRow);
        for(Prototype a : A)
            if(x!=a  &&  !xRow.containsKey(a))
            {
                //xRow.put(a, Distance.d(x, a));
                xRow.put(a, d(x, a));
                invertedBList.get(x).add(a);
            }
    }
}//end-of-class

