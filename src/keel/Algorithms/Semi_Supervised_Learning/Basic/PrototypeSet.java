/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010

	F. Herrera (herrera@decsai.ugr.es)
    L. SÃ¡nchez (luciano@uniovi.es)
    J. AlcalÃ¡-Fdez (jalcala@decsai.ugr.es)
    S. GarcÃ­a (sglopez@ujaen.es)
    A. FernÃ¡ndez (alberto.fernandez@ujaen.es)
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

package keel.Algorithms.Semi_Supervised_Learning.Basic;

import keel.Algorithms.Semi_Supervised_Learning.utilities.*;
import keel.Algorithms.Preprocess.Instance_Selection.SSMA.Cromosoma;
import keel.Algorithms.Semi_Supervised_Learning.Basic.*;

import keel.Dataset.*;

import java.util.*;

import org.core.*;

//import Jama.Matrix;
//import Jama.Matrix.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Arrays;
/**
 * Represents a prototype set.
 * @author diegoj and Isaac
 */
public class PrototypeSet extends ArrayList<Prototype> implements Comparable
{
    /** Associated instance set to the prototype set. */
    public static InstanceSet associatedInstanceSet = null;

    /**
     * Features set 1.
     */
    protected static ArrayList<Integer> FeaturesSet1;
    /**
     * Features set 2.
     */
    protected static ArrayList<Integer> FeaturesSet2;  // If you use CO-training
    
    public int compareTo (Object o1) {
        double valor1 = this.size();
        double valor2 = ((PrototypeSet) o1).size();
        if (valor1 < valor2)
          return -1;
        else if (valor1 > valor2)
          return 1;
        else return 0;
      }

    
    
 
    /**
     * Empty constructor
     */
    public PrototypeSet()
    {
        super();
    }
    
    /**
     * Constructs a void set with a number of elements.
     * @param numberOfElements Maximum inicial capacity.
     */
    public PrototypeSet(int numberOfElements)
    {
        super(numberOfElements);
    }
    
    /**
     * Constructs the set based on a instance set
     * @param s InstanceSet used to build the implicit parameter.
     */
    public PrototypeSet(InstanceSet s)
    {
        super(new ArrayList<Prototype>(s.getNumInstances()));
        associatedInstanceSet = new InstanceSet(s);
       
        int num = s.getNumInstances();
        Instance[] instances = s.getInstances();
        for(int i=0; i<num; ++i)
        {
        	
            add(new Prototype(instances[i]));
        }
    }
    
    /**
     * Creates a new set of instances by copying a 
     * subset of another set.
     *
     * @param source the set of instances from which a subset 
     * is to be created
     * @param first the index of the first instance to be copied
     * @param toCopy the number of instances to be copied
     */
    
    public PrototypeSet(PrototypeSet source, int first, int toCopy)
    {
        super(source.size());
        associatedInstanceSet = source.associatedInstanceSet;
         for(int i=first; i<toCopy; i++)
             this.add(source.get(i));
    }
    
    /**
     * Transform the prototype set to a instance set object.
     * @return the instance set object.
     */
    public InstanceSet toInstanceSet(){

    	
    	InstanceSet s = new InstanceSet();
    	
    	s.setAttributesAsNonStatic();

        s.setHeader(this.associatedInstanceSet.getHeader());
        s.setAttHeader(this.associatedInstanceSet.getAttHeader());
    	
    	for(int i=0; i< this.size(); ++i)
        {
    		/*
    		Instance anadir = new  Instance(this.get(i).inputs, this.associatedInstanceSet.getAttributeDefinitions());
    		//anadir.setAllOutputValues(this.get(i).outputs);
    		
    		anadir.setOutputNumericValue(0, this.get(i).outputs[0]);
    		//System.out.println(this.get(i).outputs[0]);
            s.addInstance(anadir);
            
            */
    		Instance anadir = new  Instance(this.get(i).inputs, this.associatedInstanceSet.getAttributeDefinitions());
    	
    		
            //Getting all the attributes
            Attribute[] attrs_input = Attributes.getInputAttributes();
                                                                                                                                     
            //Gettin all the outputs attributes
            Attribute[] attrs_output = Attributes.getOutputAttributes();

            //Check which is the data type of the inputs
            HashMap<Integer,Boolean> nominalInput = new HashMap<Integer, Boolean>();
            for (int j=0; j<attrs_input.length; j++)
                nominalInput.put(j, (Attributes.getInputAttribute(j).getType()==Attribute.NOMINAL));

            //Check which is the data type of the outputs
            boolean nominal_output = (Attributes.getOutputAttribute(0).getType()==Attribute.NOMINAL);
            
            
            
           Prototype q = this.get(i).denormalize(); //TOKADO PARA NO NORMALIZAR
               
             for(int j=0; j<this.get(i).numberOfInputs(); ++j)
                {
                    if(nominalInput.get(j))
                    {
                        anadir.setInputNominalValue(j, q.getInputAsNominal(j));
                        anadir.setInputNumericValue(j, q.getInput(j));
                    }
                    else
                    {
                        double q_i = q.getInput(j); //
                        // p.print();
                        //System.out.println("q_i" + q_i);
                        if(Prototype.getTypeOfAttribute(j) == Prototype.INTEGER)
                        	anadir.setInputNumericValue(j, Math.round(q_i));
                        else if(Prototype.getTypeOfAttribute(j) == Prototype.DOUBLE)
                        	anadir.setInputNumericValue(j, q_i);
                    }
                }
             
               if(nominal_output){
                 anadir.setOutputNominalValue(0, q.getOutputAsNominal(0));
                
                    
               }else{
                    anadir.setOutputNumericValue(0, q.label());
               }
            
            s.addInstance(anadir);
            
            
        }
        
        return s;
    }
    
    
    
    
 
    /**
     * Build a set using a partition of other.
     * @param parts Partition of other prototype set.
     */
    public PrototypeSet(ArrayList<PrototypeSet> parts)
    {
        super(parts.size());
        for(PrototypeSet ps : parts)
            for(Prototype p : ps)
                add(p);
    }
    
    /**
     * Return all the prototype in (this) that has other like the nearest neighbor
     * @return
     */
    public PrototypeSet isTheNearPrototype(Prototype other){
    	PrototypeSet result = new PrototypeSet();
    	
    	for(Prototype p: this){
    		
    		Prototype near = this.nearestTo(p);
    		
    		if(near.equals(other)){
    			result.add(p);
    		}
    	}
    	return result;
    	
    }
  
    /**
     * Return all the prototype in (this) that has other like the nearest neighbor with the class given.
     * @return
     */
    public PrototypeSet isTheNearPrototypeWithClass(Prototype other, double clase){
    	PrototypeSet result = new PrototypeSet();
    	
    	for(Prototype p: this){
    		
    		Prototype near = this.nearestTo(p);
    		
    		if(near.equals(other) && (near.getOutput(0) == clase) ){
    			result.add(p);
    		}
    	}
    	return result;
    	
    }
    

    
    /**
     * Return the nearest prototype to all the prototypes of a set.
     * @param current
     * @return
     */
    public Prototype nearestTo(PrototypeSet current)
    {
        double dMin = Double.POSITIVE_INFINITY;
        Prototype nearest = null;
        
        if(current== null)
        	return null;
        //Debug.errorln("Hay " + this.size());
        
        
        for(Prototype p : this)
        {
        	double d =0;
        	for(Prototype q : current){
        		d+= Distance.d(p, q);
        	}
        	
        	            
            if(d < dMin)
            {
                dMin = d;
                nearest = p;
            }
        }
        return nearest;
    }
    
    
    /** 
     * Return the nearest prototype to another in the set.
     * @param current Prototype which the algorithm will find its nearest-neighbor.
     * @return Nearest prototype to current in dataSet.
     */
    public Prototype nearestTo(Prototype current)
    {
        double dMin = Double.POSITIVE_INFINITY;
        Prototype nearest = null;
        
        if(current== null)
        	return null;
        //Debug.errorln("Hay " + this.size());
        for(Prototype p : this)
        {
            double d = Distance.d(current, p);
            if(d < dMin  ) // &&  !current.equalsInputs(p)
            {
                dMin = d;
                nearest = p;
            }
        }
        return nearest;
    }
    
    
    /** 
     * Return the  prototype  containing N from this set.
     * @param current.
     * @return Return the  prototype  containing N from this set.
     */
    public Prototype containing(Prototype current)
    {
        double dMin = Double.POSITIVE_INFINITY;
        Prototype nearest = null;
        //Debug.errorln("Hay " + this.size());
        for(Prototype p : this)
        {
            double d = Distance.d(current, p);
            if(d < dMin)
            {
                dMin = d;
                nearest = p;
            }
        }
        return nearest;
    }
    
    
    /** 
     * Return the INDEX off nearest prototype to another in the set.
     * @param current Prototype which the algorithm will find its nearest-neighbor.
     * @return Index of Nearest prototype to current in dataSet.
     */
    public int IndexNearestTo(Prototype current)
    {
        double dMin = Double.POSITIVE_INFINITY;
        Prototype nearest = null;
        int index = 0;
        //Debug.errorln("Hay " + this.size());
        int i=0;
        for(Prototype p : this)
        {
            double d = Distance.d(current, p);
            if(d < dMin  &&  current!=p)
            {
                dMin = d;
                nearest = p;
                index = i;
            }
            i++;
        }
        //System.out.println("Index = " + index);
        return index;
    }
  
    
    /** 
     * Return the INDEX of the Second closest prototype to another in the set.
     * @param current Prototype which the algorithm will find its nearest-neighbor.
     * @return Index of Nearest prototype to current in dataSet.
     */
    public int IndexSecondNearestTo(Prototype current)
    {
        double dMin = Double.POSITIVE_INFINITY;
        Prototype nearest = null;
        int index = 0;
        //Debug.errorln("Hay " + this.size());
        int i=0;
        for(Prototype p : this)
        {
            double d = Distance.d(current, p);
            if(d < dMin  &&  current!=p)
            {
                dMin = d;
                nearest = p;
                index = i;
            }
            i++;
        }
        
        // Now we "eliminate" the nearest neighbor and look for the second.
        dMin = Double.POSITIVE_INFINITY;
        i =0;
        int index2 = 0;
        for(Prototype p : this)
        {
            double d = Distance.d(current, p);
            if(d < dMin  &&  current!=p && i!=index)
            {
                dMin = d;
                nearest = p;
                index2 = i;
            }
            i++;
        }
        //System.out.println("Index = " + index);
        return index2;
    }
    
    
    /** 
     * Return the nearest prototype to another in the set.
     * @param current Prototype which the algorithm will find its nearest-neighbor.
     * @return Nearest prototype to current in dataSet and length.
     */
    public Pair<Prototype,Double> minimumLengthAndNearestTo(Prototype current)
    {
        double dMin = Double.POSITIVE_INFINITY;
        Prototype nearest = null;
        //Debug.errorln("Hay " + this.size());
        for(Prototype p : this)
        {
            double d = Distance.d(current, p);
            if(d < dMin  &&  current!=p)
            {
                dMin = d;
                nearest = p;
            }
        }
        return new Pair<Prototype,Double>(nearest,dMin);
    }
    
    /** 
     * Return the nearest prototype to another in the set.
     * @param current Prototype which the algorithm will find its nearest-neighbor.
     * @return Nearest prototype to current in dataSet.
     */
    public Pair<Prototype,Double> minimumLengthAndNearestWithSameClassAs(Prototype current)
    {
        double currentLabel = current.label();
        double dMin = Double.POSITIVE_INFINITY;
        Prototype nearest = null;
        //Debug.errorln("Hay " + this.size());
        for(Prototype p : this)
        {
            double d = Distance.d(current, p);
            if(p.label() == currentLabel && d < dMin  &&  current!=p)
            {
                dMin = d;
                nearest = p;
            }
        }
        return new Pair<Prototype,Double>(nearest,dMin);
    }
    
    /**
     * Returns the nearest pair of prototypes of the set.
     * @return Pair wich have the nearest pairs of the set.
     */
    public Pair<Prototype,Prototype> nearestPair()
    {
        double dMin = Double.POSITIVE_INFINITY;
        Pair<Prototype,Prototype> nearest = null;
        for(Prototype p : this)
        {
            Pair<Prototype,Double> min = minimumLengthAndNearestWithSameClassAs(p);
            if(min.second()<dMin)
            {
                dMin = min.second();
                nearest = new Pair<Prototype,Prototype>(p, min.first());
            }
            
        }
        return nearest;
    }
    
    /** 
     * Return the nearest prototype to another in the set with a specified class.
     * @param current Prototype which the algorithm will find its nearest-neighbor.
     * @param label Class that must have this nearest to current.
     * @return Nearest prototype to current in dataSet.
     */
    public Prototype nearestToWithClass(Prototype current, double label)
    {
        PrototypeSet set = this.getFromClass(label);
        double dMin = Double.POSITIVE_INFINITY;
        Prototype nearest = null;
        for(Prototype p : set)
        {
            double d = Distance.d(current, p);
            if(d < dMin)
            {
                dMin = d;
                nearest = p;
            }
        }
        return nearest;
    }
    
    
    /** 
     * Return the nearest prototype to another in the set with a specified class.
     * @param current Prototype which the algorithm will find its nearest-neighbor.
     * @param label Class that must have this nearest to current.
     * @return Nearest prototype to current in dataSet.
     */
    public Prototype nearestToWithDifferentClass(Prototype current, double label)
    {
        PrototypeSet set = this.getAllDifferentFromClass(label);
       
        //System.out.println("Set size ="+ set.size());
        double dMin = Double.POSITIVE_INFINITY;
        Prototype nearest = null;
        for(Prototype p : set)
        {
            double d = Distance.d(current, p);
            if(d < dMin)
            {
                dMin = d;
                nearest = p;
            }
        }
        return nearest;
    }
    
    /**
     * Adds prototype only if it is not already in the set.
     * @param newProt New prototype to be added to the set.
     * @return TRUE if it has been added, FALSE in other chase.
     */
    public boolean uniqueAdd(Prototype newProt)
    {
        boolean found = false;
        int _size = size();
        for(int i=0; !found && i<_size; ++i)
        {
            Prototype p = get(i);
            found = (p == newProt) || (p.equals(newProt)) ;
        }
        if(!found)
            add(newProt);
        return found;
    }
    
    /** 
     * Return the farthest prototype to another in the set.
     * @param current Prototype which the algorithm will find its farthest-neighbor.
     * @return Farthest prototype to current in dataSet.
     */
    public Prototype farthestTo(Prototype current)
    {
        double dMax = Double.NEGATIVE_INFINITY;
        Prototype farthest = null;
        for(Prototype p : this)
        {
            double d = Distance.d(current, p);
            if(d > dMax)
            {
                dMax = d;
                farthest = p;
            }
        }
        return farthest;
    }
    
    /**
     * Copy constructor. NOTE: soft-copy.
     * @param original Original set to be copied.
     */
    public PrototypeSet(PrototypeSet original)
    {
        super(original.size());
       associatedInstanceSet = original.associatedInstanceSet;
        /*int _size = original.size();
        for(int i=0; i<_size; ++i)
            add(new Prototype(original.get(i)));*/
        for(Prototype p : original)
            add(p);
    }
    
    /**
     * Get a random prototype
     * @return Random prototype of the set
     */
    public Prototype getRandom()
    {
        return get(RandomGenerator.RandintClosed(0, size()-1));
    }
    
    /**
     * Remove a random prototype (and returns it)
     * @return Random removed prototype of the set
     */
    public Prototype removeRandom()
    {
        int i = RandomGenerator.RandintClosed(0, size()-1);
        return remove(i);//returns the removed element
    }
    
    /**
     * Select all the prototypes of a specific class. The class must be a valid!.
     * @param _class Choosen class .
     * @return A prototype set which contains all the prototypes of the original set that are of the choosen class.
     */
    public PrototypeSet getFromClass(double _class)
    {
        PrototypeSet selected = new PrototypeSet(size()/2);
        for(Prototype p : this)
            if(p.label()==_class)
                selected.add(p);
        return selected;
    }
    
    /**
     * Select all the patterns of different classs. The class must be a valid!.
     * @param _class Choosen class .
     * @return A prototype set which contains all the prototypes of the original set that are not of the choosen class.
     */
    public PrototypeSet getAllDifferentFromClass(double _class)
    {
        PrototypeSet selected = new PrototypeSet(size()/2);
        for(Prototype p : this)
            if(p.label()!=_class)
                selected.add(p);
        return selected;
    }
    
    /**
     * Informs if the set contains prototypes with several different classes.
     * @return True if there are two or more prototypes with different classes (labels), False in other case.
     */
    public boolean containsSeveralClasses()
    {
        int _size = size();
        double label = get(0).label();
        boolean foundDifferent = false;
        for(int i=1; i<_size && !foundDifferent; ++i)
           foundDifferent = (label != get(i).label());
        return foundDifferent;
    }
    
    /**
     * Count the number of prototypes of each class.
     * @return HashMap of (Class, Number of protoypes with that class assigned)
     */
    public HashMap<Double, Integer> countPrototypesOfEachOutput()
    {
        HashMap<Double,Integer> count = new HashMap<Double,Integer>();
        ArrayList<Double> values = Prototype.possibleValuesOfOutput();
        for(double d: values)
            count.put(d, 0);
        
        for(Prototype p : this)
            count.put(p.firstOutput(), count.get(p.label())+1);
        
       return count;
    }
    
    /**
     * Inform the frequency of each class of the set
     * @return A hash that informs of the absolute ocurrences of each class
     */
    public HashMap<Double,Integer> getFrequencyOfClasses()
    {
        return countPrototypesOfEachOutput();
    }
    
    /**
     * Return classes with at least one element.
     * @return Classes that have got at least one prototype.
     */
    public ArrayList<Double> nonVoidClasses()
    {
        ArrayList<Double> nonVoidClasses = new ArrayList<Double>();
        ArrayList<Double> values = Prototype.possibleValuesOfOutput();
        HashMap<Double,Integer> freq = getFrequencyOfClasses();
        for(double c : values)
            if(freq.get(c)>0)
                nonVoidClasses.add(c);
        return nonVoidClasses;        
    }
    
    /**
     * Returns classes which there are prototypes with them.
     * @return ArrayList with the classes that has got at least one prototype in the set.
     */
    public ArrayList<Double> classesWithPrototypes(){ return nonVoidClasses(); }
    
    /**
     * Returns the most frequent class
     * @return The class with more ocurrences in the set.
     */
    public double mostFrequentClass()
    {
        HashMap<Double,Integer> classes = getFrequencyOfClasses();
        int maxFreq = -1;
        double maxClass = -1.0;
        ArrayList<Double> array = new ArrayList<Double>(classes.keySet());
        for(double c : array)
        {
            int ocurr_c = classes.get(c);
            if(ocurr_c > maxFreq)
            {
                maxFreq = ocurr_c;
                maxClass = c;                
            }
            else if(ocurr_c == maxFreq)
            {
                maxClass = RandomGenerator.randomSelector(c,maxClass);
            }
        }
        return maxClass;
    }
    
    /**
     * Converts data set into a String.
     * @return String with a canonical representation of the data set
     */
    @Override
    public String toString()
    {
        String result = "";
        
        int n = size();
        for(int i=0; i<n; ++i)
            result += get(i).toString() + "\n";
        return result;
    }
    
    
    /**
     * Prints the prototype in the terminal
     */
    public void print()
    {
        System.out.println("\n"+toString());
    }
    
    /**
     * Override of the clone function
     * @return A new object hard-new-copy of the caller.
     */
    @Override
    public PrototypeSet clone()
    {
        //return new PrototypeSet(this);
        PrototypeSet copy = new PrototypeSet(this.size());
        copy.associatedInstanceSet = this.associatedInstanceSet;
        for(Prototype p : this)
            copy.add(new Prototype(p));
        return copy;
    }
    
    /**
     * Hard-copy of the prototype set.
     * @return A new object hard-new-copy of the caller.
     */
    public PrototypeSet copy()
    {
        return this.clone();
    }
    
    

    
    
    /**
     * Converts the dataset into a hashset Â¿Para quÃ© cojones se usa?
     * @return Hashset of the dataset
     */
    public HashSet<Prototype> toHashSet()
    {
        HashSet<Prototype> h = new HashSet<Prototype>();
        for(Prototype p : this)
            h.add(p);
        return h;
    }
    
    /**
     * Converts the dataset into a hashmap (Prototype p, index of p in the set)
     * @return Hashmap of the dataset where keys are prototypes and values are index in the data set.
     */
    public HashMap<Prototype,Integer> toHashMap()
    {
        HashMap<Prototype,Integer> h = new HashMap<Prototype,Integer>();
        int _size = size();
        for(int i=0; i<_size; ++i)
            h.put(get(i), i);
        return h;
    }
    
       
    /**
     * Sort the prototype set in ascending distance to current prototype.
     * @param current Prototype base to be compared to every element of the set.
     * @return Sorted set in ascending distance order to current prototype.
     */
    public PrototypeSet sort(Prototype current)
    {
        int _size = size();
        PrototypeSet sorted = new PrototypeSet(_size);
        sorted.add(this);
        Collections.sort(sorted, new Distance(current));        
        return sorted;
    }

    /**
     * Returns the two nearest to each other prototypes
     * @return A pair with the two fartest prototypes in the set.
     */
    public Pair<Prototype, Prototype> farthestPrototypes()
    {
        int _size = size();        
        double maximumDistance = Double.NEGATIVE_INFINITY;
        Prototype ex1 = null;
        Prototype ex2 = null;
        for(int i=0; i<_size; ++i)
            for(int j=i+1; j<_size; ++j)
                {
                    Prototype p1 = get(i);
                    Prototype p2 = get(j);
                    double cur_dist = Distance.d(p1, p2);
                    if(cur_dist > maximumDistance)
                    {
                        maximumDistance = cur_dist;
                        ex1 = p1;
                        ex2 = p2;
                    }
                }
        return new Pair<Prototype, Prototype>(ex1, ex2); 
    }
    

    /**
     * Generate two subsets of the set t.
     * @param p1 Prototype whose closer prototypes will be in first set.
     * @param p2 Prototype whose closer prototypes will be in second set.
     * @return Two sets of prototypes. Prototype p will be in Pi if pi is nearest to p than pj, for i!=j.
     */
    public Pair<PrototypeSet,PrototypeSet> partIntoSubsetsWhichSeedPointsAre(Prototype p1, Prototype p2)
    {
        return partIntoSubsetsWhichSeedPointsAre(new Pair<Prototype,Prototype>(p1,p2));
    }
   
    /**
     * Generate two subsets of the set t.
     * @param pair Pair of prototypes which will determine the partition.
     * @return Two sets of prototypes. Prototype p will be in Pi if pi is nearest to p than pj, for i!=j.
     */
    public Pair<PrototypeSet,PrototypeSet> partIntoSubsetsWhichSeedPointsAre(Pair<Prototype,Prototype> pair)
    {
        Prototype p1 = pair.first();//seed of the P1 set
        Prototype p2 = pair.second();//seed of the P2 set
        PrototypeSet P1 = new PrototypeSet();
        PrototypeSet P2 = new PrototypeSet();
        P1.add(p1);
        P2.add(p2);
        for(Prototype p : this)
            if(p != p1 && p != p2)
            {
                if(Distance.d(p, p2) < Distance.d(p, p1))
                    P2.add(p);
                else
                    P1.add(p);
            }
        Pair<PrototypeSet, PrototypeSet> part = new Pair<PrototypeSet,PrototypeSet>(P1,P2);
        return part;
    }
 
    
    /**
     * This method return the ratio of the average distance between instances blongin to different classes of i
     * and the average distance between instances that are from the same class i.
     * @return
     * @note This method is obtain by the exprexion (4) in the paper October 1, 2008 18:43WSPC/INSTRUCTIONFILECano-Garcia-Herrera-Bernado-IJPRAI 
 		Because it was imposible to understand in the original PAPER
     */
    public double Overlapping (){
    	double lapping = 0.0;
    	double D1=0.0, D2 =0.0;
    	Prototype m = new Prototype(this.get(0));
    	Prototype mi =new Prototype(this.get(0));
     // Inicialize the prototype.
    	
    	int numberOfClass = this.getPosibleValuesOfOutput().size();
    	
    	//Numerator
    	for (int i= 0; i< numberOfClass; i++){
	    	PrototypeSet aux = this.getFromClass(i);
	    	int ni = aux.size();
	    	
	    	if( ni != 0){
		    	 m = this.avg(); // overall mean.
		    	 mi = aux.avg();  // mean of class i
		    }
	    	D1 += Distance.d(m.formatear(), mi.formatear()) * ni;
	    	
	  	}
    	
    	for (int i=0; i< numberOfClass; i++){
    		PrototypeSet aux = this.getFromClass(i);
	    	int ni = aux.size();
	    	
	    	if( ni != 0){
	    		 mi = aux.avg();  // mean of class i
	    	}
	    	
	     	for( int j=0; j< ni; j++){
	     		Prototype xij = aux.get(j);
	    	     D2+=Distance.d(xij.formatear(), mi.formatear());
	    	}
	    	    	
    	}
    	
    	lapping = D2 / D1;
    	
	     return lapping;
    	
    }
    
    /**
     * 
     * @return If the set contains a mixture of instance return false.
     */
    public boolean homogeneity (){
    	
    	if(this.containsSeveralClasses())
    		return false;
    	return true;
    }
    
    /**
     * Generate two subsets of the set t.
     * @param p1 Prototype whose closer prototypes will be in first set.
     * @param p2 Prototype whose closer prototypes will be in second set.
     * @return Two sets of prototypes. Prototype p will be in Pi if pi is nearest to p than pj, for i!=j.
     */
    public Pair<PrototypeSet,PrototypeSet> partIntoSubsetsOverlappingDegree(Prototype p1, Prototype p2)
    {
        return partIntoSubsetsWhichSeedPointsAre(new Pair<Prototype,Prototype>(p1,p2));
    }
    
    /**
     * Generate two subsets of the set t.
     * @param pair Pair of prototypes which will determine the partition.
     * @return Two sets of prototypes. Prototype p will be in Pi if pi is nearest to p than pj, for i!=j.
     */
    public Pair<PrototypeSet,PrototypeSet> partIntoSubsetsOverlappingDegree(Pair<Prototype,Prototype> pair)
    {
        Prototype p1 = pair.first();//seed of the P1 set
        Prototype p2 = pair.second();//seed of the P2 set
        PrototypeSet P1 = new PrototypeSet();
        PrototypeSet P2 = new PrototypeSet();
        P1.add(p1);
        P2.add(p2);
        for(Prototype p : this)
            if(p != p1 && p != p2)
            {
                if(Distance.d(p, p2) < Distance.d(p, p1))
                    P2.add(p);
                else
                    P1.add(p);
            }
        Pair<PrototypeSet, PrototypeSet> part = new Pair<PrototypeSet,PrototypeSet>(P1,P2);
        return part;
    }
    
    
    /**
     * Makes a partition of the set by class. One part by one class present in the set.
     * @return Class-partition of the current set. That is, one partition for each subset of prototypes with same class.
     */
    public ArrayList<PrototypeSet> classPartition()
    {
        ArrayList<Double> classes = nonVoidClasses();
        //Debug.errorln("Tenemos classes");
        ArrayList<PrototypeSet> part = new ArrayList<PrototypeSet>(classes.size());
        for(double c : classes)
        {
            PrototypeSet setC = getFromClass(c);
            if(!setC.isEmpty())
                part.add(setC);
        }
        return part;
    }
    
    /**
     * Swaps two elements of the prototype set.
     * @param i1 First element.
     * @param i2 Second element.
     */
    public void swap(int i1, int i2)
    {
        Collections.swap(this, i1, i2);
    }
    
    /**
     * Shuffle the set. Uses PrototypeGenerator random seed.
     * @see PrototypeGenerator
     */
    public void randomize()
    {
        Random r = new Random();
        r.setSeed(PrototypeGenerator.getSeed());
        Collections.shuffle(this, r);
    }
    
    /**
     * Shuffle the set.
     * @param shuffleSeed Seed of the random generator.
     */
    public void randomize(long shuffleSeed)
    {
        Random r = new Random();
        r.setSeed(shuffleSeed);
        Collections.shuffle(this, r);
    }
    
    /**
     * Makes a partition of the set. Keeping the % of prototypes selected for each class.
     * @param percentInFirst Percentile of the prototyepes that willbe in the first set.
     * @return Pair of prototype sets. First element contains percentInFirst prototypes of the original, and second the others.
     */
    public Pair<PrototypeSet,PrototypeSet> makePartitionPerClass(double percentInFirst)
    {
        PrototypeSet first = new PrototypeSet();
        PrototypeSet second = new PrototypeSet();
        PrototypeSet copy = this.copy();
        ArrayList<Double> classes = copy.nonVoidClasses();
        for(double c : classes)
        {
            PrototypeSet pc = copy.getFromClass(c);
            int pc_size = pc.size();
            int numInFirst = (int)Math.floor(pc_size * percentInFirst/100.0);
            for(int i=0; i<numInFirst; ++i)
                first.add(pc.get(i));
            for(int i=numInFirst; i<pc_size; ++i)
                second.add(pc.get(i));
        }
        return new Pair<PrototypeSet,PrototypeSet>(first,second);
    }
   
    /**
     * Makes a partition of the set. Keeping the % of prototypes selected for each class.
     * @param percentInFirst Percentile of the prototyepes that willbe in the first set.
     * @return Pair of prototype sets. First element contains percentInFirst prototypes of the original, and second the others.
     */
    public Pair<PrototypeSet,PrototypeSet> makePartition(double percentInFirst)
    {
        PrototypeSet first = new PrototypeSet();
        PrototypeSet second = new PrototypeSet();
        PrototypeSet copy = this.copy();
        copy.randomize();
        int _size = size();
        int numInFirst = (int)Math.floor(_size * percentInFirst/100.0);
        for(int i=0; i<numInFirst; ++i)
            first.add(copy.get(i));
        for(int i=numInFirst; i<_size; ++i)
            second.add(copy.get(i));   
        return new Pair<PrototypeSet,PrototypeSet>(first,second);
    }
    
    /**
     * Make a partition of a set. numberOfSets sets with the same number of prototypes will be generated.
     * @param numberOfSets Number of subsets of the prototype set to be generated.
     * @return Set of prototype set which is a partition of it.
     */
    public ArrayList<PrototypeSet> partIn(int numberOfSets)
    {
        int _size = size();
        Debug.force(_size > numberOfSets, "Too much partitions");
        int _size_1 = _size-1;
        int protsBySet = _size/numberOfSets;
        ArrayList<Integer> shuffle = RandomGenerator.generateDifferentRandomIntegers(0, _size_1, _size);
        ArrayList<PrototypeSet> partitions = new ArrayList<PrototypeSet>(numberOfSets);
        int offset=0;
        for(int i=0; i<numberOfSets; ++i)
        {
            PrototypeSet partition_i = new PrototypeSet();
            int end = offset+protsBySet;
            for(int index=offset; index<end; ++index)
            {
                int k = shuffle.get(index);
                partition_i.add(get(k));                
            }
            partitions.add(partition_i);
            offset = end;
        }
        return partitions;    
    }
    
    /**
     * Size of the greatest diameter in the set.
     * @return Largest distance between two prototypes in the set.
     */
    public double largestDiameter()
    {
        
        Pair<Prototype, Prototype> p = farthestPrototypes();
        return Distance.d(p.first(), p.second()); 
    }
    
    /**
     * Converts data set into a Keelish-String.
     * @param title Title of the data set (required for all keel data files)
     * @return String with the Keel data file representation of the prototype set
     */
    public String asKeelDataFileString(String title)
    {
        String line = "@relation "+title+"\n";
                                                                                                                             
        //Getting all the attributes
        Attribute[] attrs_input = Attributes.getInputAttributes();

        for (int i=0; i<attrs_input.length; i++)
            line += attrs_input[i].toString()+"\n";
                                                                                                                                 
        //Gettin all the outputs attributes
        Attribute[] attrs_output = Attributes.getOutputAttributes();
        line += attrs_output[0].toString()+"\n";
                                                                                                                             
        //Getting @inputs and @outputs
        line += Attributes.getInputHeader()+"\n";
        line += Attributes.getOutputHeader()+"\n";
        
        String text = line;
        
        //Check which is the data type of the inputs
        HashMap<Integer,Boolean> nominalInput = new HashMap<Integer, Boolean>();
        for (int i=0; i<attrs_input.length; i++)
            nominalInput.put(i, (Attributes.getInputAttribute(i).getType()==Attribute.NOMINAL));

        //Check which is the data type of the outputs
        boolean nominal_output = (Attributes.getOutputAttribute(0).getType()==Attribute.NOMINAL);
        
        text += "@data\n";
        
        if(this.size()!=0){
	        //The data
	        int n_attributes = get(0).numberOfInputs();        
	        
	
	        for(Prototype p: this)
	        {
	            Prototype q = p.denormalize(); //TOKADO PARA NO NORMALIZAR
	            for(int i=0; i<n_attributes; ++i)
	            {
	                if(nominalInput.get(i))
	                {
	                	text += q.getInputAsNominal(i) + ", ";
	                }
	                else
	                {
	                    double q_i = q.getInput(i); //
	                    // p.print();
	                    //System.out.println("q_i" + q_i);
	                   if(Prototype.getTypeOfAttribute(i) == Prototype.INTEGER)
	                        text += Math.round(q_i) + ", "; // ERROR de DIEGO!?
	                    else if(Prototype.getTypeOfAttribute(i) == Prototype.DOUBLE)
	                        text += q_i + ", "; 
	                   
	                   /*
	                    if(Attributes.getInputAttribute(i).getType() == Attribute.INTEGER)
	                        text += Math.round(q_i) + ", "; // ERROR de DIEGO!?
	                    else if(Attributes.getInputAttribute(i).getType() == Attribute.REAL)
	                        text += q_i + ", "; 
	                    else{
	                        System.out.println("QUE POLLAS PAZA AKI");
	                    } */
	                }
	            }
	           if(nominal_output)
	                text += q.getOutputAsNominal(0) + "\n";
	           else
	                text += q.label() + "\n";
	        }
        }// end if
        return text;
    }
    
    /**
     * Converts data set into a Keelish-String.
     * @return String with the Keel data file representation of the prototype set
     */
    public String asKeelDataFileString()
    {
        return asKeelDataFileString(Attributes.getRelationName());
    }
    
    /**
     * Save the data in a file (Keel style)
     * @param filename Name of the output file.
     */
    public void save(String filename)
    {
        KeelFile.write(filename, this.asKeelDataFileString());
    }
    
    /**
     * Join two prototype sets
     * @param other Set to unite.
     * @return PrototypeSet which the elements of the two operands (two prototype sets).
     */
    public PrototypeSet union(PrototypeSet other)
    {
        PrototypeSet result = new PrototypeSet(other.size()+size());
        
        for(Prototype p: this)
            result.add(p);
        
        for(Prototype p: other)
            if(!result.contains(p))
                result.add(p);
        
        return result;
    }
    
    /**
     * Performs the union betwenn the set of protoypeset.
     * @return ProtoypeSet which is the union of all the parts.
     
    public static PrototypeSet union(ArrayList<PrototypeSet> parts)
    {
       PrototypeSet result = new PrototypeSet();
       for(PrototypeSet ps : parts)
           result = result.union(ps);
    }*/
    
    /**
     * Join a prototype to a set
     * @param other Other prototype to be joined.
     * @return A new set which is union of the set and the prototype.
     */
    public PrototypeSet join(Prototype other)
    {
        PrototypeSet result = this.copy();
        result.add(other);
        return result;
    }
    
    /**
     * Returns a copy of the set without an element.
     * @param other Element to be removed of the returned copy.
     * @return Copy of the set without other element.
     */
    public PrototypeSet without(PrototypeSet other)
    {
        PrototypeSet result = new PrototypeSet(this);
        for(Prototype o : other)
            if(result.contains(o))
                result.remove(o);
        return result;
    }
    
    
    
    /**
     *  Returns a copy of the set without an prototype.
     */
    public Prototype borrar(int index){
    	Prototype borrado = null;
    	
    	for(int i=0; i<this.size(); i++){
    		if(this.get(i).getIndex()==index){
    			borrado = new Prototype(this.remove(i));
    		}
    	}
		return borrado;
    
    	
    }
    
    
    /**
     *  Returns a copy of the set without an prototype.
     */
    public boolean remove(Prototype other){
    	
    	boolean fin = false;
    	
    	for(int i=0; i<this.size() && !fin; i++){
    		if( this.get(i).equals(other)){
    			fin = true;
    			this.remove(i);
    		}
    	}
    	
    	return fin;
    }
    
    /**
     *  Returns a copy of the set without an prototype.
     */
    public boolean remove(PrototypeSet other){
    	
    	boolean fin = false;
    	
    	for(int j=0; j<other.size(); j++){
	    	for(int i=0; i<this.size() && !fin; i++){
	    		if( this.get(i).equals(other.get(j))){
	    			fin = true;
	    			this.remove(i);
	    		}
	    	}
    	}
    	return fin;
    }
    
    
    /**
     *  Returns a copy of the set without an prototype without checking the class label
     */
    public boolean removeWithoutClass(Prototype other){
	
    	boolean fin = false;

    	
    	for(int i=0; i<this.size() && !fin; i++){
    		if(this.get(i).equalsInputs(other)){
    			fin = true;
    			this.remove(i);
    		}
    	}
    	
    	/*
    	if(!fin){
    		System.err.println("ERROR AL BORRAR");
    		other.print();

    	}*/
    	return fin;
    }
    
    /**
     *
     * @param other
     * @return
     */
    public boolean removeWithoutClass(PrototypeSet other){
    	
    	boolean fin = false;
    	
    	for(int j=0; j<other.size(); j++){
    		this.removeWithoutClass(other.get(j));
    	}
    	return fin;
    }
    
    
    
    /**
     *  Comprueba si existen en el conjunto....
     */
    public boolean pertenece(Prototype other){
    	
    	boolean fin = false;
    	
    	for(int i=0; i<this.size() && !fin; i++){
    		if( this.get(i).equals(other)){
    			fin = true;
    		}
    	}
    	
    	return fin;
    }
    
    public boolean perteneceSinClass(Prototype other){
    	
    	boolean fin = false;
    	
    	for(int i=0; i<this.size() && !fin; i++){
    		if( this.get(i).equalsInputs(other)){
    			fin = true;
    		}
    	}
    	
    	return fin;
    }
    
    
    
    /**
     * Returns a copy of the set without an prototype.
     * @param other Element to be removed of the returned copy.
     * @return Copy of the set without other element.
     */
    public PrototypeSet without(Prototype other)
    {
        PrototypeSet result = new PrototypeSet(this);
         if(result.contains(other))
                result.remove(other);
         else{
        	 System.err.println("Error al borrase");
         }
        return result;
    }
    
    
    /**
     * Returns a copy of the set without an element.
     * @param other Element to be removed of the returned copy.
     * @return Copy of the set without other element.
     */
    public PrototypeSet minus(PrototypeSet other)
    {
        return without(other);
    }
      
    /**
     * Performs avg operation of the prototype set.
     * @return Average prototype of the data set.
     */
    public Prototype avg()
    {
    	if (this.size() !=0){
	     int numInputs = get(0).numberOfInputs();
	     int numOutputs = get(0).numberOfOutputs();     
	     
	     double[] inputs = new double[numInputs];
	     for(int i=0; i<numInputs; ++i)
	         inputs[i]=0.0;
	     
	     double[] outputs = new double[numOutputs];
	     for(int i=0; i<numOutputs; ++i)
	         outputs[i]=get(0).getOutput(i);
	     
	     for(Prototype p: this)
	         for(int i=0; i<numInputs; ++i)
	            inputs[i] += p.getInput(i);
	
	     int _size = this.size();
	     for(int i=0; i<numInputs; ++i)
	            inputs[i] /= _size;
	     return new Prototype(inputs, outputs);
    	}
     return null;
    }
    
    
    /**
     * Performs standard desviation operation of the prototype set.
     * @return SD prototype of the data set.
     * @author Isaac.
     */
    public Prototype sd()
    {
    	Prototype sd = new Prototype(this.get(0).numberOfInputs(),1);
    	Prototype diference;
    	//First
    	Prototype avg = this.avg();
    	int N = this.size();
    	
    	Prototype Sumatory  = new Prototype(this.get(0).numberOfInputs(),1);
    	for(int i=0; i< Sumatory.numberOfInputs(); i++){
    		Sumatory.setInput(i,0);
    	}
    	
    	for(int i = 0; i< this.size(); i++){
    		Prototype Xi = this.get(i);
    		diference = Xi.sub(avg);
    		
    		diference = diference.mul(diference);  // ^2
    		
    		//diference.print();
        	for(int j=0; j< Sumatory.numberOfInputs(); j++){
        		Sumatory.setInput(j, Sumatory.getInput(j)+diference.getInput(j));
        	}
    		//Sumatory.print();
    	}
    	
    	
    	for(int j=0; j< Sumatory.numberOfInputs(); j++){
    		Sumatory.setInput(j, Sumatory.getInput(j)/N);
    	}
    	//Sumatory.mul(1./N);
    	sd = Sumatory.sqrt();
    	
    	return sd;

    }
    
    
    /**
     * Add the elements of a set.
     * @param other Set with the elements to include.
     */
    public void add(PrototypeSet other)
    {
        for(Prototype p: other)
            this.add(p);
    }
  
    
    /**
     * Add the elements of a set.
     * @param other Set with the elements to include.
     */
    public void addNoRepetition(PrototypeSet other)
    {
        for(Prototype p: other)
        	if(!this.perteneceSinClass(p))
        		this.add(p);
    }
  
    
    /**
     * Add one prototype to the set..
     * @param one prototype
     * @author isaac
     */
    public void addPrototype(Prototype other)
    {
         this.add(other);
    }
    
    public void addPrototypeNoRepetition(Prototype other)
    {
    	if(!this.perteneceSinClass(other))
           this.add(other);
    }
    
    
    /**
     * this + other.
     * @param other
     * @return  S U other
     * @author isaac
     */
    public PrototypeSet addPrototype2(Prototype other){
    	PrototypeSet otro = new PrototypeSet(this);
    	
    	otro.add(other);
    	return otro;
   
    }
    /**
     * Return all the existing classes in our universe.
     * @return All the values of the outputs (clasess) that exists in the dataset.
     * @see Prototype#possibleValuesOfOutput()
     */
    public ArrayList<Double> getPosibleValuesOfOutput()
    {
        return Prototype.possibleValuesOfOutput();
    }
    
    
    
    /**
     * Returns the greatest distance between center and other prototype and that prototype.
     * @param center Prototype to be euclidean-distance compared.
     * @return A Pair with the farthest prototype and the distance.
     */
    public Pair<Prototype,Double> radius(Prototype center)
    {
        double max = -1.0;
        Prototype pMax = null;
        for(Prototype p : this)
        {
            double current = Distance.d(p, center);
            if(current > max)
            {
                max = current;
                pMax = p;
            }
        }
        return new Pair<Prototype,Double>(pMax,max);
    }
    
    /**
     * Returns the smallest distance between center and other prototype and that prototype.
     * @param center Prototype to be euclidean-distance compared.
     * @return A Pair with the farthest prototype and the distance.
     */
    public Pair<Prototype,Double> antiRadius(Prototype center)
    {
        double min = 999999999;
        Prototype pMin = null;
        for(Prototype p : this)
        {
            double current = Distance.d(p, center);
            if(current < min)
            {
                min = current;
                pMin = p;
            }
        }
        return new Pair<Prototype,Double>(pMin,min);
    }
    
    /**
     * Returns the greatest distance between center.
     * @param center Prototype to be euclidean-distance compared.
     * @return Greatest distance between center and other prototype of the set.
    */
    public double maxDistanceTo(Prototype center)
    {
        return radius(center).second();
    }
   
    /**
     * Returns the smallest distance between center and other prototype and that prototype.
     * @param center Prototype to be euclidean-distance compared.
     * @return Greatest distance between center and other prototype of the set.
     */
    public double minDistanceTo(Prototype center)
    {
        return antiRadius(center).second();
    }
    
    /**
     * return the smallest distance between uno and all prototypes of the particle.
     * @author Isaac
     */
    
    public double minDist ( Prototype uno)
    {
    	
        double min = 999999999;
        //Prototype pMin = null;
        for(Prototype p : this)
        {
            double current = Distance.d(p, uno);
            if(current < min)
            {
                min = current;
                
            }
        }
        
    	return min;
    }
    
    /**
     * Returns variance of prototype set to the center.
     * @param ps Prototype set to be measured.
     * @param center Average prototype.
     * @return Value of the variance of the set.
     */
    public static double variance(PrototypeSet ps, Prototype center)
    {
        double acc = 0.0;
        for(Prototype p : ps)
            acc += Distance.d(center, p);
        return acc;
    }
    
    /**
     * Change values of the prototypes that are not in the values domain.
     */
    public void applyThresholds()
    {
        for(Prototype p : this)
            p.applyThresholds();
    }
    
    /*public void round()
    {
        for(Prototype p : this)
            p.round();
    }*/
    
    
    /**
     * Print Prototype Set.
     * This function can help to debug the algorithm.
     */
    
    public void printSet(){
    	System.out.println("The prototype Set has "+ this.size() + " instances");
    	
    	for(int i=0; i< this.size();i++){
    		
    		for(int j=0; j<this.get(i).numberOfInputs();j++){
    			System.out.print( this.get(i).getInput(j)+ " \t ");
    		}
    		System.out.print("\n");
    	}
    	
    }
    

    
    /**
     * 
     * @param datosTrain
     * @param C
     * @param centers
     * @return
   
    
    @SuppressWarnings("unused")
	public int[] Cmeans (double datosTrain[][], int C, double centers[][]) {



		int clusters[];

		int tmp, pos;

		int baraje[];

		int i, j;

		double minDist, dist;

		boolean cambio = true;

		int nc[];

		

		clusters = new int[datosTrain.length];

		baraje = new int[datosTrain.length];

		

		for (i=0; i<datosTrain.length; i++) {

			baraje[i] = i;

		}



		for (i=0; i<datosTrain.length; i++) {

			pos = Randomize.Randint(i, datosTrain.length);

			tmp = baraje[i];

			baraje[i] = baraje[pos];

			baraje[pos] = tmp;			

		}

		

		for (i=0; i<C; i++) {

			for (j=0; j<datosTrain[0].length; j++) {

				centers[i][j] = datosTrain[baraje[i]][j];

			}

		}

		

		for (i=0; i<datosTrain.length; i++) {

			pos = 0;

			minDist = KNN.distancia(datosTrain[i], centers[0]);

			for (j=1; j<C; j++) {

				dist = KNN.distancia(datosTrain[i], centers[j]);

				if (dist < minDist) {

					pos = j;

					minDist = dist;

				}

			}

			clusters[i] = pos;

		}

		

		nc = new int[C];

		while (cambio) {

			cambio = false;

			

			Arrays.fill(nc, 0);

			for (i=0; i<C; i++) {

				Arrays.fill(centers[i], 0.0);

			}

			

			for (i=0; i<datosTrain.length; i++) {

				nc[clusters[i]]++;

				for (j=0; j<datosTrain[0].length; j++) {

					centers[clusters[i]][j] += datosTrain[i][j];

				}

			}

			

			for (i=0; i<C; i++) {

				for (j=0; j<datosTrain[0].length; j++) {

					centers[i][j] /= (double)nc[i];

				}

			}

			

			for (i=0; i<datosTrain.length; i++) {

				pos = 0;

				minDist = KNN.distancia(datosTrain[i], centers[0]);

				for (j=1; j<C; j++) {

					dist = KNN.distancia(datosTrain[i], centers[j]);

					if (dist < minDist) {

						pos = j;

						minDist = dist;

					}

				}

				if (clusters[i] != pos) {

					cambio = true;

					clusters[i] = pos;
					

				}

			}

			

		}



		return clusters;

	}

  */
	
    
    /**
     * 
     * PrototypeSet to double.
     */
    
    public double[][] prototypeSetTodouble(){
    	double datos[][] = new double[this.size()][];
    	
    	 //System.out.println("Size prototype = " + this.size());
    	for(int i=0; i< this.size();i++){
    		//System.out.println("\n");
    		datos[i] = new double[this.get(0).numberOfInputs()];
    		
    		datos[i] = ((Prototype)this.get(i)).getInputs();  //.denormalize()

    		/*
    		for(int j=0; j< datos[i].length; j++){
    			System.out.print(datos[i][j]+", ");
    		}
    		System.out.println("");
    		*/
    	}
    	
    	return datos;
    	
    	   	
    }
    
    
    
   
   
    public int[] prototypeSetClasses(){
    	int datos[] = new int[this.size()];
    	
    	
    	 //System.out.println("Size prototype = " + this.size());
    	for(int i=0; i< this.size();i++){
    		
    		datos[i] = (int) this.get(i).getOutput(0);
    		//System.out.println(datos[i]);
    		
    	}
    	
    	return datos;
    	
    	   	
    }
    
    
    /**
     * 
     * PrototypeSet to double.
     */
    
    public void doubleToprototypeSet(double datos[][], int clase){
    	int j;
    	
    	 //System.out.println("Datos leng = " + datos.length);
    	  new PrototypeSet();
    	 
    	for(int i=0; i< datos.length;i++){
    		
    		if(!Double.isNaN(datos[i][0])){
	    		Prototype o = new Prototype(datos[i].length,1);
	    		
	    		for(j= 0; j< datos[i].length; j++){
	    			
	    			o.setInput(j, datos[i][j]);
	    			
	    		}
	    		o.setFirstOutput(clase);
	    		
	    		this.add(o);
    		}

    	}
    	
	
    	   	
    }
    

    /**********************************************
     * 
     * FUNCIONES PARA DIFFERENTIAL EVOLUTION.
     * ********************************************
     */
    
    
    /**
     * SUMAR dos conjuntos de prototipos , uno a uno. De la misma dimension.
     */
 
   public PrototypeSet sumar(PrototypeSet other){
   	PrototypeSet suma = new PrototypeSet();
 
   	if (this.size() == other.size()){
	    	for(int i= 0; i< this.size(); i++){
	    		suma.add(this.get(i).add(other.get(i)));	    		
	    	}
   	}else{
   		return null;
   	}   	
   	
   	return suma;
   }
   
   
     /**
      * Restar dos conjuntos de prototipos , uno a uno. De la misma dimension.
      */
  
    public PrototypeSet restar(PrototypeSet other){
    	PrototypeSet resta = new PrototypeSet();
  
    	if (this.size() == other.size()){
	    	for(int i= 0; i< this.size(); i++){
	    		resta.add(this.get(i).sub(other.get(i)));	    		
	    	}
    	}else{
    		return null;
    	}   	
    	
    	return resta;
    }

    /**
     * Multiplicar un conjunto por un Escalar.
     */
 
   public PrototypeSet mulEscalar(double escalar){
   	PrototypeSet result = new PrototypeSet();
 
   	for(int i= 0; i< this.size(); i++){
    		result.add(this.get(i).mul(escalar));	    		
   	}
   	
   	return result;
   }

   /**
    * Calculaa el opuesto de un conjunto .
    * @return
    */
   
   public PrototypeSet opposite(){
	   
	   PrototypeSet opuesto = new PrototypeSet();
	   
	   for(int i=0; i<this.size(); i++){
		   opuesto.add(this.get(i).opposite());
		   
	   }
	   return opuesto;
   }
   
   /**
    * Transform the prototypeSet (this) in a matrix of binary string 8-bit codification.
    * 
    */
   public String [][] toBinaryString (){
   	String datos[][] = new String[this.size()][];
	double number ;
	Integer num;
	String aux;
	double parametroConversion = 1./255; // Como vamos a pasar a 8 bits.. 2 ^8 = 256, y representamos num en [0,1]
	
	 //System.out.println("Size prototype = " + this.size());
	for(int i=0; i< this.size();i++){
		//System.out.println("\n");
		datos[i] = new String[this.get(0).numberOfInputs()];
		
		for(int j=0; j<((Prototype)this.get(i)).numberOfInputs(); j++ ){
			number = ((Prototype)this.get(i)).getInput(j);
			
			if(number < 0) number = 0;
			else if(number > 1) number = 1;
			num = (int) (number/parametroConversion); // Nos quedamos con la parte entera de la conversion
			
			aux = Integer.toBinaryString(num);
			//Tendremos que anadir ceros al a derecha...si es menor que 0.
			while(aux.length()<8){
				aux = "0"+aux;
			}
			datos[i][j] = new String();
			datos[i][j] = aux;
			//System.out.println("num = "+ num + "number = "+ number + " and the binary = "+ aux);
		}
		
	}
	
	return datos;
   }
    
   
   
   /**
    * Transform the prototypeSet (this) in a matrix of binary string Gray Code
    * 
    */
   public String [][] to8GrayString (){
   	String datos[][] = new String[this.size()][];
	double number ;
	Integer num;
	String aux;
	double parametroConversion = 1./255; // Como vamos a pasar a 8 bits.. 2 ^8 = 256, y representamos num en [0,1]
	
	 //System.out.println("Size prototype = " + this.size());
	for(int i=0; i< this.size();i++){
		//System.out.println("\n");
		datos[i] = new String[this.get(0).numberOfInputs()];
		
		for(int j=0; j<((Prototype)this.get(i)).numberOfInputs(); j++ ){
			number = ((Prototype)this.get(i)).getInput(j);
			
			if(number < 0) number = 0;
			else if(number > 1) number = 1;
			num = (int) (number/parametroConversion); // Nos quedamos con la parte entera de la conversion
			
			aux = Integer.toBinaryString(num);
			
			
			//Tendremos que anadir ceros al a derecha...si es menor que 0.
			while(aux.length()<8){
				aux = "0"+aux;
			}
			
			//System.out.println("Binario = " + aux);
			
			// Ahora tengo que pasar de binario a Gray Code
            /* We start at the highest available array element and
             * proceed to 1.
             * If the next (read from right to left) digit is 1, then
             * the actual digit is set to 1 minus its own value.
             * Otherwise nothing has to be done.
             * The last (in array #1) digit will remain untouched.
             */
			String gray [] = aux.split("");
			for(int m= (gray.length-1); m>1; m--){
				if(Integer.parseInt(gray[m-1])== 1){
					gray[m] = Integer.toString(1 - Integer.parseInt(gray[m]));
				}
			}
			
			aux = "";
			
			for(int m=0;m< gray.length; m++){
				aux+= gray[m];
			}
			

			datos[i][j] = new String();
			datos[i][j] = aux;
			//System.out.println("num = "+ num + "number = "+ number + " and the binary  Gray= "+ aux);
		}
		
	}
	
	return datos;
   }
   
    
   /**
    * Transform  a matrix of binary string 8-bit codification in a double PrototypeSEt
    * 
    */
   public void toPrototypeSet (String datos[][], double clases[]){
	   int num;
	   double valor;
	   double parametroConversion = 1./255; 
	   new PrototypeSet(datos.length);
	   
	   
	   
	   for(int i=0; i< datos.length;i++){
		   Prototype aux = new Prototype(datos[0].length,1);
		   
		   for(int j=0; j< datos[i].length; j++){
			   num =0;
			   String aux2 = ""; //datos[i][j];
			   
			   // Transformacion Gray to binary..
			   aux2 += datos[i][j].charAt(0); // El mas a la izquierda es igual
			   char uno, dos;
			   
			   for(int k=1; k< 8;k++){
				   uno = aux2.charAt(k-1); // El primero de Binary con el 2 de Gray
				   dos = datos[i][j].charAt(k);
				   
				   if(uno == '0' && dos == '0')
					   aux2 += "0";
				   else if( uno =='1' && dos == '1'){
					   aux2 += "0";
				   }else{
					   aux2 += "1";
				   }
				
			   }
			   
			  // System.out.println("Gray = " + datos[i][j] +  " toBinary-> "+ aux2);
			   for(int k=0; k< 8; k++){
				  int bit= Character.getNumericValue(aux2.charAt(k));
				  //System.out.println("Bit =" + bit);
				  num +=  Math.pow(2, 7-k)*bit; 
				  //System.out.println("num =" + num);
			   }
			  			 
			   valor = num * parametroConversion ;
			   //System.out.println("datos String " + datos[i][j] + "num = " + num + " valor = "+ valor);
			   aux.setInput(j, valor);
			   
		   }
		   this.add(aux);
		   
		   this.get(i).setFirstOutput(clases[i]);
	   }
   }
   
   
   
   public void formatear(PrototypeSet initial){
	   new PrototypeSet();
	   
	   for(int i=0; i< initial.size(); i++){
		  // initial.get(i).print();
		   //initial.get(i).formatear().print();
		this.add(initial.get(i).formatear());  
	   }
   }
   
   
   /**
    * It return the prototypeset with the features specified in lista.
    * @param lista
    * @return
    */
   public PrototypeSet getFeatures(ArrayList<Integer> lista){
	   PrototypeSet nuevo = new PrototypeSet();
	   
	   for(int i=0; i<this.size(); i++){
		   nuevo.add(this.get(i).getPrototypeWithSelectedInputs(lista));
	   }
	   
	   return nuevo;
   }
   
   
   /**
    * Sorted a ArrayList of Integers
    * @param numbers
    * @return
    */
   public ArrayList<Integer> quicksort(ArrayList<Integer> numbers) {
       if (numbers.size() <= 1)
           return numbers;
       int pivot = numbers.size() / 2;
       ArrayList<Integer> lesser = new ArrayList<Integer>();
       ArrayList<Integer> greater = new ArrayList<Integer>();
       int sameAsPivot = 0;
       for (int number : numbers) {
           if (number > numbers.get(pivot))
               greater.add(number);
           else if (number < numbers.get(pivot))
               lesser.add(number);
           else
               sameAsPivot++;
       }
       lesser = quicksort(lesser);
       for (int i = 0; i < sameAsPivot; i++)
           lesser.add(numbers.get(pivot));
       greater = quicksort(greater);
       ArrayList<Integer> sorted = new ArrayList<Integer>();
       for (int number : lesser)
           sorted.add(number);
       for (int number: greater)
           sorted.add(number);
       return sorted;
   }

   
   public ArrayList<Integer> getFeatures1(){
	   return this.FeaturesSet1;
   }
   
   public ArrayList<Integer> getFeatures2(){
	   return this.FeaturesSet2;
   }
   /**
    * This method divides the PrototypeSet into two different prototypesets where the original attributes sets are randomly partititoned, with similar sizes.
    * @return
    */
   public Pair<PrototypeSet,PrototypeSet> divideFeaturesRandomly(){
	   
       PrototypeSet first = new PrototypeSet();
       PrototypeSet second = new PrototypeSet();
	   
       ArrayList<Integer> indexes =  RandomGenerator.generateDifferentRandomIntegers(0, this.get(0).numberOfInputs()-1);

       
       
       // If the number of inputs is not par, We choose randomly in which prototype will be include this feature.
    	   
    	   int limit=this.get(0).numberOfInputs()/2; //+1;
    		    
    	   if(this.get(0).numberOfInputs()%2!=0 && Randomize.Randdouble(0, 1)<0.5){
    		   limit++;
    		  // System.out.println("Entro");
    	   }
    	
       	   this.FeaturesSet1 = new ArrayList<Integer>(limit);
    	   this.FeaturesSet2 = new ArrayList<Integer>(indexes.size()-limit);
 
    	  // System.out.println("El limite es: "+limit);
    	   
    	   ArrayList<Integer> primeros = new ArrayList<Integer>(limit);
    	   
	       for(int i=0; i<limit; i++){
	    	  primeros.add(i, indexes.get(i));
	       }
	       
	       primeros =new ArrayList<Integer>(this.quicksort(primeros)); // Los ordeno, para evitar problemas con instance set.
	       
	       for(int i=0; i<limit; i++){
		    	  this.FeaturesSet1.add(i,primeros.get(i));
		    }
		       
	       
	       first = new PrototypeSet(this.getFeatures(primeros).clone());
	       
	       ArrayList<Integer> segundos = new ArrayList<Integer>(indexes.size()-limit);
	       
	       for(int i=0; i<indexes.size()-limit; i++){
	    	   segundos.add(i, indexes.get(limit+i));
	       }
	   
	       segundos =new ArrayList<Integer>(this.quicksort(segundos));
	       
	       for(int i=0; i<indexes.size()-limit; i++){
	    	 	  this.FeaturesSet2.add(i,segundos.get(i));
	       }
	       
	       
	       second = new PrototypeSet(this.getFeatures(segundos).clone());
       
	   return new Pair<PrototypeSet, PrototypeSet>(first,second);
	   	   
   }
   
   
   
   /**
    * This method divides the datasets into X subspaces of dimension ''dimension''.
    * @param subspaces
    * @param dimension should be the half of the number of features!
    * @return
    */
 
   public PrototypeSet[] divideFeaturesRandomly(int subspaces, int dimension, int [][] indices){
	   
       PrototypeSet [] divididos = new PrototypeSet[subspaces];
       
	   
       for(int i=0; i< subspaces; i++){ // For each subspace:
    	   
    	   ArrayList<Integer> indexes =  RandomGenerator.generateDifferentRandomIntegers(0, this.get(0).numberOfInputs()-1); 
    	   
    	   ArrayList<Integer> lista = new ArrayList<Integer>(indexes.subList(0, dimension));
    	   
    	   lista =new ArrayList<Integer>(this.quicksort(lista));
    	   
    	   
    	   for(int j=0; j<lista.size();j++){
    		   indices[i][j] = (int) lista.get(j);
    	   }
    	   
    	   divididos[i] = new PrototypeSet();
    	   
    	   for(int j=0; j<this.size(); j++){
    		   divididos[i].add(this.get(j).getPrototypeWithSelectedInputs(lista));
    	   }
    	   
    	   
    	   indexes = null;
    	   lista = null;
    	   System.gc();
    	   
       }

       divididos[0].get(0).print();
       
	   return divididos;
	   	   
   }
   
   
   /**
    * This method divides the features based on the indexes parameters
    * @param indices
    * @return
    */
   public PrototypeSet[] divideFeaturesRandomly( int [][] indices){
	   int subspaces = indices.length;
	   int dimension = indices[0].length;
	   
       PrototypeSet [] divididos = new PrototypeSet[subspaces];
       
       
	   
       for(int i=0; i< subspaces; i++){ // For each subspace:
    	   
	   
    	   ArrayList<Integer> lista = new ArrayList<Integer>(dimension);
    	   
    	   for(int j=0; j<dimension; j++){
    		   lista.add(indices[i][j]);
    	   }
    	   
    	   lista =new ArrayList<Integer>(this.quicksort(lista));
    	   
    	   for(int j=0; j<lista.size();j++){ // reorder again
    		   indices[i][j] = (int) lista.get(j);
    	   }
    	   
    	   divididos[i] = new PrototypeSet();
    	   
    	   for(int j=0; j<this.size(); j++){
    		   divididos[i].add(this.get(j).getPrototypeWithSelectedInputs(lista));
    	   }
    	   
    	   
    	   
    	   
       }

       divididos[0].get(0).print();
       
	   return divididos;
	   	   
   }

   
   
   /**
    * Return a prototype set by Bootstrapping the current PrototypeSet
    * Creates a new dataset of the same size using random sampling
   * with replacement according to the given weight vector. The
   * weights of the instances in the new dataset are set to one.
    */
   
   public PrototypeSet resample(){
	   PrototypeSet boostrapped = new PrototypeSet();
	   
	   
	    double [] weights = new double[this.size()];
	    Arrays.fill(weights,1);
	    
	    double[] probabilities = new double[this.size()];
	    double sumProbs = 0, sumOfWeights=0;
	    
	    for(int i=0; i<weights.length;i++){
	    	sumOfWeights+=weights[i];
	    }
	    
	    for (int i = 0; i < this.size(); i++) {
	      sumProbs += Randomize.Rand();
	      probabilities[i] = sumProbs;
	    }
	    
	    for (int i = 0; i < probabilities.length; i++) {
	    	probabilities[i] /= (sumProbs / sumOfWeights);
	    }
	   
	 // Make sure that rounding errors don't mess things up
	    probabilities[this.size() - 1] = sumOfWeights;
	    int k = 0; int l = 0;
	    sumProbs = 0;
	    while ((k < this.size() && (l < this.size()))) {
	      if (weights[l] < 0) {
	    	  throw new IllegalArgumentException("Weights have to be positive.");
	      }
	      sumProbs += weights[l];
	      while ((k < this.size()) &&(probabilities[k] <= sumProbs)) { 
	    	  boostrapped.add(this.get(l));
	    	  k++;
	      }
	      l++;
	    } 
	    
	   
	   return boostrapped;
	   
   }
   
   
   public double sumOfWeights(){
	   double sum=0;
	   for (int i=0; i<this.size(); i++){
		   sum+= this.get(i).getWeight();
	   }
	   return sum;
   }
   
   
   
   
   /**
    * Help function needed for stratification of set.
    *
    * @param numFolds the number of folds for the stratification
    */
   protected void stratStep (int numFolds){
     
     PrototypeSet newVec = new PrototypeSet();
     int start = 0, j;

     // create stratified batch
     while (newVec.size() < this.size()) {
       j = start;
       while (j < this.size()) {
 	     newVec.add(this.get(j));
 	     j = j + numFolds;
       }
       start++;
     }
     
     new PrototypeSet(newVec.clone());
   }
   
   /**
    * Stratifies a set of instances according to its class values 
    * if the class attribute is nominal (so that afterwards a 
    * stratified cross-validation can be performed).
    *
    * @param numFolds the number of folds in the cross-validation
    */
   
   public void stratify(int numFolds) {
	    
       Attribute[] a = Attributes.getOutputAttributes();
       //System.out.println("a " + a[0]);        
       
	 if (a[0].getType() == Attribute.NOMINAL) {

	      // sort by class
	      int index = 1;
	  while (index < size()) {
		Prototype instance1 = this.get(index - 1);
		for (int j = index; j < this.size(); j++) {
		  Prototype instance2 = this.get(j);
		  if ((instance1.getOutput(0) == instance2.getOutput(0)) ) { //||     (instance1.classIsMissing() && 	       instance2.classIsMissing())
		    swap(index,j);
		    index++;
		  }
		}
		index++;
	      }
	      stratStep(numFolds);
	    }
  }
   
   
   /**
    * Creates the training set for one fold of a cross-validation 
    * on the dataset. 
    *
    * @param numFolds the number of folds in the cross-validation. Must
    * be greater than 1.
    * @param numFold 0 for the first fold, 1 for the second, ...
    * @return the training set 
    */
   //@ requires 2 <= numFolds && numFolds < numInstances();
   //@ requires 0 <= numFold && numFold < numFolds;
   public PrototypeSet trainCV(int numFolds, int numFold) {

     int numInstForFold, first, offset;
     PrototypeSet train;
  

     numInstForFold = size() / numFolds;
     if (numFold < size() % numFolds) {
       numInstForFold++;
       offset = numFold;
     }else
       offset = size() % numFolds;
     train = new PrototypeSet(); //this, numInstances() - numInstForFold);
     first = numFold * (size() / numFolds) + offset;
     //copyInstances(0, train, first);
     for(int i=0; i<first; i++){
    	 train.add(this.get(i));
     }
     
     //copyInstances(first + numInstForFold, train,  size() - first - numInstForFold);
     for(int i=first + numInstForFold; i< (size() - first - numInstForFold); i++){
    	 train.add(this.get(i));
     }
     

     train.randomize();
     return train;
   }
   
   
   
   
   /**
    * Creates the test set for one fold of a cross-validation on 
    * the dataset.
    *
    * @param numFolds the number of folds in the cross-validation. Must
    * be greater than 1.
    * @param numFold 0 for the first fold, 1 for the second, ...
    * @return the test set as a set of weighted instances

    */
   //@ requires 2 <= numFolds && numFolds < numInstances();
   //@ requires 0 <= numFold && numFold < numFolds;
   public  PrototypeSet testCV(int numFolds, int numFold) {

     int numInstForFold, first, offset;
     PrototypeSet test;
     

     numInstForFold = size() / numFolds;
     if (numFold < size() % numFolds){
       numInstForFold++;
       offset = numFold;
     }else
       offset = size() % numFolds;
     test = new PrototypeSet(); //Instances(this, numInstForFold);
     first = numFold * (size() / numFolds) + offset;
     
     //copyInstances(first, test, numInstForFold);
     for(int i=first; i<numInstForFold; i++){
    	 test.add(this.get(i));
     }

     return test;
   }
   
   
   /**
    * Sorts the instances based on an attribute. For numeric attributes, 
    * instances are sorted in ascending order. For nominal attributes, 
    * instances are sorted based on the attribute label ordering 
    * specified in the header. Instances with missing values for the 
    * attribute are placed at the end of the dataset.
    *
    * @param attIndex the attribute's index (index starts with 0)
    */
   public void sort(int attIndex) {

     int i,j;

     // move all instances with missing values to end
     j = size() - 1;
     i = 0;
   
     /*while (i <= j) {
       if (get(j).isMissing(attIndex)) {
 	j--;
       } else {
 	if (instance(i).isMissing(attIndex)) {
 	  swap(i,j);
 	  j--;
 	}
 	i++;
       }
     }*/
     
     quickSort(attIndex, 0, j);
   }
   
   
   /**
    * Partitions the instances around a pivot. Used by quicksort and
    * kthSmallestValue.
    *
    * @param attIndex the attribute's index (index starts with 0)
    * @param l the first index of the subset (index starts with 0)
    * @param r the last index of the subset (index starts with 0)
    *
    * @return the index of the middle element
    */
   //@ requires 0 <= attIndex && attIndex < numAttributes();
   //@ requires 0 <= left && left <= right && right < numInstances();
   protected int partition(int attIndex, int l, int r) {
     
     double pivot = get((l + r) / 2).getInput(attIndex);

     while (l < r) {
       while ((get(l).getInput(attIndex) < pivot) && (l < r)) {
         l++;
       }
       while ((get(r).getInput(attIndex) > pivot) && (l < r)) {
         r--;
       }
       if (l < r) {
         swap(l, r);
         l++;
         r--;
       }
     }
     if ((l == r) && (get(r).getInput(attIndex) > pivot)) {
       r--;
     } 

     return r;
   }
   
   
   /**
    * Implements quicksort according to Manber's "Introduction to
    * Algorithms".
    *
    * @param attIndex the attribute's index (index starts with 0)
    * @param left the first index of the subset to be sorted (index starts with 0)
    * @param right the last index of the subset to be sorted (index starts with 0)
    */
   //@ requires 0 <= attIndex && attIndex < numAttributes();
   //@ requires 0 <= first && first <= right && right < numInstances();
   protected void quickSort(int attIndex, int left, int right) {

     if (left < right) {
       int middle = partition(attIndex, left, right);
       quickSort(attIndex, left, middle);
       quickSort(attIndex, middle + 1, right);
     }
   }
   
   
}
