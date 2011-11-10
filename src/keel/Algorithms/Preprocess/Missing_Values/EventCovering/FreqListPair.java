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
 * <p>
 * @author Written by Julián Luengo Martín 14/05/2006
 * @version 0.3
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Preprocess.Missing_Values.EventCovering;
import java.util.*;

/**
 * <p>
 * This class represents a list of pairs frequency (i.e. the frequency associated to a 
 * determined pair of strings).
 * </p>
 */
public class FreqListPair {
    protected Vector freqs = null;
    protected int index = 0;
    protected int totalElements = 0;
    
    /**
     * <p>
     * Creates a new object, with fresh allocated memory 
     * </p>
     */
    public FreqListPair(){
        freqs = new Vector();
        index = 0;
        totalElements = 0;
    }
    
    /**
     * <p>
     * Adds a new pair, increasing its frequency if already exists, or 
     * creating a new pair if not
     * </p>
     * @param newElem1 First element of the new pair
     * @param newElem2 Second element of the new pair
     */
    public void AddElement(String newElem1,String newElem2){
        boolean found = false;
        int isAt = -1;
        ValuesFreq elem = null;
        for(int i=0;i<freqs.size()&&!found;i++){ //search for an existing element
            elem = (ValuesFreq)(freqs.elementAt(i));
            if( newElem1.compareTo(elem.getValue1())==0){
                if( newElem2.compareTo(elem.getValue2())==0){
                    found = true;
                    isAt = i;
                }
            }
        }
        if(found){ //there was already 1 element
            elem.incFreq(); //add 1 to number of times seen
            freqs.setElementAt(elem, isAt);//store back the increased item, replacing the older one
        } else{//it is the first occurrence
            elem = new ValuesFreq(newElem1, newElem2, 1);
            freqs.addElement(elem);
        }
        totalElements++;
    }
    
    /**
     * <p>
     * Returns the most frequent pair
     * </p>
     * @return The pair with highest frequency
     */
    public ValuesFreq mostCommon(){
        int isAt = 0;
        ValuesFreq elem = null;
        ValuesFreq ref = null;
        
        if(freqs.size()>0){
            ref = (ValuesFreq)(freqs.elementAt(isAt));
            for(int i=1;i<freqs.size();i++){
                elem = (ValuesFreq)(freqs.elementAt(i));
                if(elem.moreFreq(ref)){
                    isAt = i;
                    ref = elem;
                }
            }
            return ref;
        } else
            return null;
    }
    
    /**
     * <p>
     * Number of different pairs of this list
     * </p>
     * @return Number of different pairs of this list
     */
    public int numElems(){
        return freqs.size();
    }
    
    /**
     * <p>
     * Returns the element at position indicated
     * </p>
     * @param i Position of the element we want
     * @return The element at position i
     */
    public ValuesFreq elementAt(int i){
        return (ValuesFreq)(freqs.elementAt(i));
    }
    
    /**
     * <p>
     * Reset the iterator to the beginning of the list.
     * </p>
     */
    public void reset(){
        index = 0;
    }
    
    /**
     * <p>
     * Iterates to the next element in the list
     * </p>
     * @return True if still there are more elements remaining, false if the end of the list has been reached.
     */
    public boolean iterate(){
        index += 1;
        if(index>=freqs.size())
            return false;
        return true;
    }
    
    /**
     * <p>
     * Obtains the element pointed currently by the iterator
     * </p>
     * @return The current element
     */
    public ValuesFreq getCurrent(){
        if(index<freqs.size())
            return (ValuesFreq)(freqs.elementAt(index));
        else{
            System.out.println("ERROR: Element Out Of Range");
            return null;
        }
    }
    
    /**
     * <p>
     * Test if the iterator is out of the bounds of the list
     * </p>
     * @return True if the iterator is beyond the limit, of false if there are still more elements
     */
    public boolean outOfBounds(){
        return (index>=freqs.size() || index < 0);
    }
    
    /**
     * <p>
     * The total number of elements stored, i.e. the sum of all the frequencies
     * </p>
     * @return the total number of elements added to this list
     */
    public int totalElems(){
        return totalElements;
    }
    
    /**
     * <p>
     * Sums the frequencies for the first elements which are equal to
     * the provided one
     * </p>
     * @param val the reference value
     * @return the sum of frequencies of all pairs which have the first element equal to val
     */
    public int elem1SumFreq(String val){
        ValuesFreq elem;
        int total;
        
        total = 0;
        for(int i=0;i<freqs.size();i++){
            elem = (ValuesFreq)(freqs.elementAt(i));
            if(val.compareTo(elem.getValue1())==0){
                total += elem.getFreq(); //sum every f_uv such u is the searched one
            }
        }
        return total; //return f_u
    }
    
    /**
     * <p>
     * Sums the frequencies for the second elements which are equal to
     * the provided one
     * </p>
     * @param val the reference value
     * @return the sum of frequencies of all pairs which have the second element equal to val
     */
    public int elem2SumFreq(String val){
        ValuesFreq elem;
        int total;
        
        total = 0;
        for(int i=0;i<freqs.size();i++){
            elem = (ValuesFreq)(freqs.elementAt(i));
            if(val.compareTo(elem.getValue2())==0){
                total += elem.getFreq(); //sum every f_uv such v is the searched one
            }
        }
        return total; //return f_v
    }
    
    /**
     * <p>
     * Searches for the first pair which has its elements equals to the provided ones, 
     * and return it frequency
     * </p>
     * @param e1 the first element of the pair
     * @param e2 the second element of the pair
     * @return the retrieved frequency of the pair
     */
    public int getPairFreq(String e1,String e2){
         ValuesFreq elem;
         
          for(int i=0;i<freqs.size();i++){
            elem = (ValuesFreq)(freqs.elementAt(i));
            if(e1.compareTo(elem.getValue1())==0 && e2.compareTo(elem.getValue2())==0){
                return elem.getFreq();
            }
          }
         return 0;
    }
    
    /**
     * <p>
     * Searches for the all the pairs which have its elements equals to the provided ones, 
     * and return their cummulative frequency
     * </p>
     * @param e1 the first element of the pair
     * @param e2 the second element of the pair
     * @return the sum of all frequencies
     */
    public int sumPairFreq(String e1,String e2){
         ValuesFreq elem;
         int accum = 0;
         
          for(int i=0;i<freqs.size();i++){
            elem = (ValuesFreq)(freqs.elementAt(i));
            if(e1.compareTo(elem.getValue1())==0 && e2.compareTo(elem.getValue2())==0){
                accum += elem.getFreq();
            }
          }
         return accum;
    }
}

