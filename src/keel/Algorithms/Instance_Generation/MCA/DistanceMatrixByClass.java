/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Instance_Generation.MCA;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.PNN.*;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import java.util.*;
//import keel.Dataset.Instance;

/**
 Prototype that implements comparator. It is used for sorting.
 */
class PairOfPrototypes implements Comparable<PairOfPrototypes>
{
    Pair<Prototype,Prototype> pair;
    double distance;
    double label;

    public double getDistance() { return distance;  }

    public double getLabel() { return label; }

    public Pair<Prototype, Prototype> getPair() { return pair; }
    
    public PairOfPrototypes(Prototype one, Prototype two)
    {
        pair = new Pair<Prototype,Prototype>(one,two);
        distance = Distance.d(one, two);
        label = one.label();
    }
    
    @Override
    public int compareTo(PairOfPrototypes other)
    {
        if(distance < other.distance)
            return -1;
        else if(distance == other.distance)
            return 0;
        return 1;
    }
}

class SymmetricDistanceMatrix
{
    ArrayList<ArrayList<Double>> matrix;
    PrototypeSet original;    
    //! Position of each prototype in the superset (not in the partition)
    //HashMap<Prototype,Integer> positionInSuperset;
    
    public SymmetricDistanceMatrix()
    {
        matrix = new ArrayList<ArrayList<Double>>();                
    }
    
    protected void updateDistances()
    {
        int _size = original.size();
        matrix = new ArrayList<ArrayList<Double>>(_size);        
        for(int i=0; i<_size; ++i)
        {
            matrix.add(new ArrayList<Double>(_size-i));
            for(int j=i+1; j<_size; ++j)
                matrix.get(i).add(Distance.dSquared(original.get(i), original.get(j)));
        }
    }
    
    public SymmetricDistanceMatrix(PrototypeSet V)
    {
        original = V;
        updateDistances();
    }
    
    public double get(int i, int j)
    {
        double value = 0.0; 
        if(j > i)
            value = matrix.get(i).get(j);
        else if(j == i)
            value = 0.0;
        else if(j < i+1)
        {
            int tmp = j;
            j = i;
            i = tmp;
            value = matrix.get(i).get(j);
        }
        return value;
    }
    
    // No debes hacer todos con todos, piensa algo
    ArrayList<PairOfPrototypes> sortByNearness()
    {
        ArrayList<PairOfPrototypes> pairs = new ArrayList<PairOfPrototypes>();
        int _size = original.size();
        for(int i=0; i<_size; ++i)
            for(int j=0; j<_size; ++j)
                if(i!=j)
                    pairs.add(new PairOfPrototypes(original.get(i),original.get(j)));
         Collections.sort(pairs);
         //ArrayList<Pair<Prototype,Prototype>> sortedPairs = new ArrayList<Pair<Prototype,Prototype>>();      
         //for(PairOfPrototypes p : pairs)
         //   sortedPairs.add(p.getPair());
         return pairs;
    }
    
    boolean remove(Prototype p)
    {
         boolean removed = false;
        int index = original.indexOf(p);
        int _size = original.size();        
        if(index > -1  && index < _size)
        {
            original.remove(index);            
            removed = true;
        }
        return removed;
    }
    
    boolean remove(Prototype p, boolean makeUpdate)
    {
        boolean removed = false;
        int index = original.indexOf(p);
        int _size = original.size();        
        if(index > -1  && index < _size)
        {
            original.remove(index);
            if(makeUpdate)
                updateDistances();
            removed = true;
        }
         /*   for (int i = 0; i < _size; ++i)
                for (int j = i + 1; j < _size; ++j)
                {
                    if(i == index)
                    {
                        matrix.remove(i);
                        removed = true;
                        //++counter;
                    }
                    else if(j == index)
                    {
                        matrix.get(i).remove(j);
                        removed = true;
                        //++counter;
                    }
                }*/
         return removed;
    }
}
/**
 *
 * @author diegoj
 */
public class DistanceMatrixByClass
{
    //Para cada clase hay una matriz de distancias
    HashMap<Double,SymmetricDistanceMatrix> matrix = new HashMap<Double,SymmetricDistanceMatrix>();
    
    public DistanceMatrixByClass(PrototypeSet V)
    {
        ArrayList<Double> labels = Prototype.possibleValuesOfOutput();
        for(double label : labels)
        {
            PrototypeSet pLabelSet = V.getFromClass(label);
            if(!pLabelSet.isEmpty())
                matrix.put(label, new SymmetricDistanceMatrix(pLabelSet));
        }
    }
    
    boolean containsLabel(double label)
    {
        return matrix.containsKey(label);
    }
    
    public ArrayList<Double> labels()
    {
        ArrayList<Double> presLabels = new ArrayList<Double>();
        ArrayList<Double> labels = new ArrayList<Double>(matrix.keySet());
        for(double label : labels)
            if(containsLabel(label))
                presLabels.add(label);
        return presLabels;
    }
    
    private ArrayList<PairOfPrototypes> sortByNearness(double label)
    {
        SymmetricDistanceMatrix pSet = matrix.get(label);
        ArrayList<PairOfPrototypes> sorted = pSet.sortByNearness();
        return sorted;
    }
    

    /**
     * Returns the nearest prototype for each class in the set.
     * @return Hash with associations class, list with pairs of prototypes of each class sorted by distance between them.
     */
    //Hashmap de clases -> lista de pares para esa clase, ordenados de más cercanía a menor cercanía
    public HashMap<Double,ArrayList<Pair<Prototype,Prototype>>> nearnestPrototypesForEachClass()
    {
        ArrayList<Double> labels = labels();
        HashMap<Double,ArrayList<Pair<Prototype,Prototype>>> n = new  HashMap<Double,ArrayList<Pair<Prototype,Prototype>>>();
        
        for(double label : labels)
        {
            ArrayList<PairOfPrototypes> pLabel = sortByNearness(label);
            ArrayList<Pair<Prototype,Prototype>> goodPLabel = new ArrayList<Pair<Prototype,Prototype>>(pLabel.size());
            n.put(label, goodPLabel);
            for(PairOfPrototypes p : pLabel)
                goodPLabel.add(p.getPair());
        }
        return n;
    }
    
    public boolean remove(Prototype p, boolean makeUpdate)
    {
        double pLabel = p.label();
        SymmetricDistanceMatrix m = matrix.get(pLabel);
        return m.remove(p, makeUpdate);
    }
    
    public boolean remove(Prototype p)
    {
        double pLabel = p.label();
        SymmetricDistanceMatrix m = matrix.get(pLabel);
        return m.remove(p);
    }
}
