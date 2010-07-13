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

package keel.Algorithms.Instance_Generation.utilities.KNN;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import java.util.*;
import keel.Dataset.*;
import org.core.*;

/**
 * Implements the KNN algorithm.
 * @author diegoj
 */
public class KNN
{
    /** Number of neighbor-prototypes to be searched in the KNN. */
    protected static int K = 1;
    
    //! Index of the neighbors of an element.
    //protected static HashSet<Integer> neighborsIndex = null;
    
    /**
     * Sets the number of prototypes to be used in the knn algorithm. Must be integer greater than 0.
     * @param n Number of prototypes
     */
    public static void setK(int n)
    {
        K = n;
    }
    
    /**
     * Returns the current value of K.
     * @return Number of neighbors used with each prototype in the KNN.
     */
    public static int k()
    {
        return K;
    }
    
    /**
     * Returns the current value of K.
     * @return Number of neighbors used with each prototype in the KNN.
     */
    public static int getK()
    {
        return k();
    }
    
    /**
     * Implements the 1NN algorithm
     * @param current Prototype which the algorithm will find its nearest-neighbor.
     * @param dataSet Prototype set in which the algorithm will search.
     * @return Nearest prototype to current in the prototype set dataset.
     */
    public static Prototype _1nn(Prototype current, PrototypeSet dataSet)
    {
        Prototype nearestNeighbor = dataSet.get(0);
        int indexNN = 0;
        //double minDist = Distance.dSquared(current, nearestNeighbor);
        //double minDist = Distance.euclideanDistance(current, nearestNeighbor);
        double minDist =Double.POSITIVE_INFINITY;
        double currDist;
        int _size = dataSet.size();
      //  System.out.println("****************");
       // current.print();
        for (int i=0; i<_size; i++)
        {
            Prototype pi = dataSet.get(i);
            //if(!current.equals(pi))
            //{
               // double currDist = Distance.dSquared(current, pi);
             currDist = Distance.euclideanDistance(pi,current);
            // System.out.println(currDist);
            
             if(currDist >0){
                if (currDist < minDist)
                {
                    minDist = currDist;
                   // nearestNeighbor = pi;
                    indexNN =i;
                }
            }
            //}
        }
        
       // System.out.println("Min dist =" + minDist + " Vecino Cercano = "+ indexNN);
        
        return dataSet.get(indexNN);
    }
    
    /**
     * Implements the KNN algorithm
     * @param current Prototype which the algorithm will find its nearest-neighbors.
     * @param dataSet Prototype set in which the algorithm will search.
     * @param k The size neighborhood to be returned.
     * @return Nearest prototypes to current in the prototype set dataset.
     */
    public static PrototypeSet knn(Prototype current, PrototypeSet dataSet, int k)
    {
        PrototypeSet nneighbors = new PrototypeSet(k);        
        PrototypeSet sorted = dataSet.sort(current);
        for(int i=0; i<k; ++i)
            nneighbors.add(sorted.get(i));
        
        return nneighbors;
    }
    
     /**
     * Implements the KNN algorithm. Use static parameter of the class 
     * @param current Prototype which the algorithm will find its nearest-neighbors.
     * @param dataSet Prototype set in which the algorithm will search.
     * @return Nearest prototypes to current in the prototype set dataset.
     */
    public static PrototypeSet knn(Prototype current, PrototypeSet dataSet)
    {
        return KNN.knn(current,dataSet,K);
    }
    
    /**
     * Informs of the number of prototypes with correct class. Uses 1NN to perform nearest prototype.
     * @param training Training data prototype set
     * @param test Test data prototype set
     * @return Number of prototypes well classificated
     */
    public static int classficationAccuracy1NN(PrototypeSet training, PrototypeSet test)
    {
	int wellClassificated = 0;
        for(Prototype p : test)
        {
            Prototype nearestNeighbor = _1nn(p, training);          
            
            if(p.getOutput(0) == nearestNeighbor.getOutput(0))
                ++wellClassificated;
        }
        return wellClassificated;
    }
    
        /**
     * Informs of the number of prototypes with correct class. Uses 1NN to perform nearest prototype.
     * @param training Training data prototype set
     * @param test Test data prototype set
     * @return Number of prototypes well classificated
     */
    public static Pair<Integer,Integer> classficationAccuracyAndError1NN(PrototypeSet training, PrototypeSet test)
    {
	int wellClassificated = 0;
        int notWellClassif = 0;
        for(Prototype p : test)
        {
            Prototype nearestNeighbor = _1nn(p, training);            
            ++notWellClassif;
            if(p.label() == nearestNeighbor.label())
            {
                ++wellClassificated;
                --notWellClassif;
            }
        }
        return new Pair<Integer,Integer>(wellClassificated, notWellClassif);
    }
    
    /**
     * Informs of the classification accuracy. The number of prototypes must be at least 1.
     * @param training Training data prototype set
     * @param test Test data prototype set
     * @param k Number of prototype-neighbors to be compared with each prototype of test data prototypes.
     * @return Number of prototypes well classificated
     */
    public static int classficationAccuracy(PrototypeSet training, PrototypeSet test, int k)
    {
        int wellClassificated = 0;        
        //int index = 0;
        for(Prototype p: test)
        {
            //System.out.println("PROTOTIPO " + (index++) + " DE TEST ");
            PrototypeSet neighbors = knn(p, training, k);
            //TreeMap<Class, Ocurrences>
            //TreeMap<Double, Integer> classes = new TreeMap<Double, Integer>();
            HashMap<Double, Integer> classes = new HashMap<Double, Integer>();
            double maximumKey = -1;            
            int maximumTimes = -1;            
            int i=0;
            for(Prototype n: neighbors)
            {
                //double d = Distance.d(p, n);
                double class_n = n.firstOutput();
                //System.out.println("Vecino " + i + " a distancia " + d + " clase " + class_n);
                i++;                
                if(!classes.containsKey(class_n))
                {
                    if(maximumKey==-1)//Para el caso de la asignaciÃ³n inicial
                        maximumKey = class_n;
                    classes.put(class_n, 1);
                
                }
                else
                {
                    int num_n = classes.get(class_n) + 1;
                    classes.put(class_n, num_n);
                    if(num_n > maximumTimes)
                    {
                        maximumTimes = num_n;
                        maximumKey = class_n;
                        //System.out.println("maximumKey: " + maximumKey);
                    }            
                    //evitamos que se coja siempre el mismo
                    else if(num_n == maximumTimes) 
                    {
                        if(RandomGenerator.RanddoubleClosed(0.0, 1.0) > 0.5)
                            maximumKey = class_n;
                    }
                }
            }
            
            /*double m = _1nn(p,training).firstOutput();
            if(maximumKey == m)
                System.out.println("EXITO");
            else
                System.out.println("FAIL " + maximumKey + "( Correcta es "+ m +")");*/
            
            //System.out.println(classes.toString());
            //System.out.println(maximumKey);
            //int chosen_class = classes.get(maximumKey);
            if(maximumKey == p.firstOutput())
                ++wellClassificated;//*/
            
        }
        return wellClassificated;
    }
    
    /**
     * Informs of the classification accuracy
     * @param training Training data prototype set
     * @param test Test data prototype set
     * @return Number of prototypes well classificated
     */
    public static int classficationAccuracy(PrototypeSet training, PrototypeSet test)
    {
        return classficationAccuracy(training, test, K);
    }
    
    /** 
     * Return the nearest prototype to another with the same of different class
     * @param current Prototype which the algorithm will find its nearest-neighbors.
     * @param dataSet Prototype set in which the algorithm will search.
     * @param isSameClass Must return a prototype with the same class as current?
     * @return Nearest prototype to current with the same of different class (resp. isSameClass)
    */
    public static Prototype getNearest(Prototype current, PrototypeSet dataSet, boolean isSameClass)
    {
        if(isSameClass)
            return getNearestWithSameClassAs(current,dataSet);
        return getNearestWithDifferentClassAs(current,dataSet);
    }
    
    /** 
     * Return the nearest prototype to another in a set.
     * @param current Prototype which the algorithm will find its nearest-neighbors.
     * @param dataSet Prototype set in which the algorithm will search.
     * @return Nearest prototype to current in dataSet.
     */
    public static Prototype getNearest(Prototype current, PrototypeSet dataSet)
    {
        return dataSet.nearestTo(current);
    }
    
    /** 
     * Return the nearest prototype to another with the same class.
     * @param current Prototype which the algorithm will find its nearest-neighbor.
     * @param dataSet Prototype set in which the algorithm will search.
     * @return Nearest prototype to current with the same class.
    */        
    public static Prototype getNearestWithSameClassAs(Prototype current, PrototypeSet dataSet)
    {
        double label = current.label();
        PrototypeSet dataSetOfLabel = dataSet.getFromClass(label);
        if(dataSetOfLabel.isEmpty())
        {
            Debug.errorln("There are no prototypes of class " + label);
            return null;
        }
        //Debug.errorln("Size of dataSetOfLabel " + dataSetOfLabel.size());
        double dMin = Double.POSITIVE_INFINITY;
        Prototype nearest = null;
        for(Prototype p : dataSetOfLabel)
        {
            double d = Distance.d(current, p);
            if(d < dMin  &&  current != p)
            {
                dMin = d;
                nearest = p;                
            }
        }
        return nearest;
    }
    
    /** 
     * Return the nearest prototypes to another with the same class
     * @param current Prototype which the algorithm will find its nearest-neighbor.
     * @param dataSet Prototype set in which the algorithm will search.
     * @return PrototypeSet containing all the prototypes of the same class in distance increasing order.
    */  
    public static PrototypeSet getNearestNeighborsWithSameClassAs(Prototype current, PrototypeSet dataSet)
    {
        PrototypeSet neighborsWithSameClass = new PrototypeSet();
        PrototypeSet sorted = dataSet.sort(current);
        double class_current = current.label();        
        for(Prototype p : sorted)
            if(p.label() == class_current)
                neighborsWithSameClass.add(p);
        return neighborsWithSameClass;
    }
    
    /** 
     * Return some nearest prototypes to another with the same class
     * @param current Prototype which the algorithm will find its nearest-neighbor.
     * @param dataSet Prototype set in which the algorithm will search.
     * @param numberOfNeighbors Number of neighbors to be returned.
     * @return PrototypeSet containing all the prototypes of the same class in distance increasing order.
    */  
    public static PrototypeSet getNearestNeighborsWithSameClassAs(Prototype current, PrototypeSet dataSet, int numberOfNeighbors)
    {
        PrototypeSet neighborsWithSameClass = new PrototypeSet();
        PrototypeSet sorted = dataSet.sort(current);
        double class_current = current.label();        
        int _size = sorted.size();
        boolean full = false;
        int counter = 0;
        for(int i=0; i<_size && !full; i++)
            if(sorted.get(i).label() == class_current)
            {
                neighborsWithSameClass.add(sorted.get(i));
                full = (counter == numberOfNeighbors);
                ++counter;
            }
        return neighborsWithSameClass;
    }
    
    /** 
     * Return some nearest prototypes to another with different class
     * @param current Prototype which the algorithm will find its nearest-neighbor.
     * @param dataSet Prototype set in which the algorithm will search.
     * @param numberOfNeighbors Number of neighbors to be returned.
     * @return PrototypeSet containing all the prototypes of the same class in distance increasing order.
    */  
    public static PrototypeSet getNearestNeighborsWithDifferentClassAs(Prototype current, PrototypeSet dataSet, int numberOfNeighbors)
    {
        PrototypeSet nn = new PrototypeSet();
        PrototypeSet sorted = dataSet.sort(current);
        double class_current = current.label();
        int _size = sorted.size();
        boolean full = false;
        int counter = 0;
        for(int i=0; i<_size  &&  !full; i++)
            if(sorted.get(i).label() != class_current)
            {
                nn.add(sorted.get(i));
                full = (counter == numberOfNeighbors);
                ++counter;
            }
        return nn;
    }
    
/** 
     * Return the nearest prototype to another with the same class
     * @param current Prototype which the algorithm will find its nearest-neighbor.
     * @param dataSet Prototype set in which the algorithm will search.
     * @return Nearest prototype to current with the same class.
    */        
    public static Prototype getNearestWithDifferentClassAs(Prototype current, PrototypeSet dataSet)
    {
        double label = current.label();        
        double dMin = Double.POSITIVE_INFINITY;
        Prototype nearest = null;        
        for(Prototype p : dataSet)
        {
            double d = Distance.d(current, p);
            if(d < dMin  &&  p.label()!=label  &&  current != p  &&  !current.equals(p))
            {
                dMin = d;
                nearest = p;                
            }
        }
        return nearest;
    }
    
    
        /** 
     * Return some nearest prototypes to another with different class
     * @param current Prototype which the algorithm will find its nearest-neighbor.
     * @param dataSet Prototype set in which the algorithm will search.
     * @param numberOfNeighbors Number of neighbors to be returned.
     * @return PrototypeSet containing all the prototypes of the same class in distance increasing order.
    */  
    public static PrototypeSet getNearestNeighbors(Prototype current, PrototypeSet dataSet, int numberOfNeighbors)
    {
        PrototypeSet nn = new PrototypeSet();
        PrototypeSet sorted = dataSet.sort(current);        
        int _size = sorted.size();
        boolean full = false;
        int counter = 0;
        for(int i=0; i<_size  &&  !full; i++)
        {
            nn.add(sorted.get(i));
            full = (counter == numberOfNeighbors);
            ++counter;
        }
        return nn;
    }
    
}//end KNN.java

