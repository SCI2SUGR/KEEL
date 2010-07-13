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
 * @author Written by Julián Luengo Martín 08/03/2006
 * @version 0.3
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Preprocess.Missing_Values.fkmeans;
import java.util.Vector;


/**
 * <p>
 * This class stores a list of Value-attribute elements. This list contains a FreqList associated to each class,
 * i.e. stores the frequency of each classes associated to a given value.
 * </p>
 */
public class VAList {
    protected Vector elemsList = null;
    protected int totalElems = 0;

    /** 
     * <p>
     * Creates a new instance of VAList
     * </p>
     */
    public VAList() {
        elemsList = new Vector();
        totalElems = 0;
    }
    
    /**
     * <p>
     * Adds an attribute value with its associated class to this list. If no frequency list for this value
     * exists yet, then creates one new.
     * </p>
     * @param value The value of the attribute
     * @param _class The class of the instance from which the value has been extracted
     */
    public void addValueNClass(double value, double _class){
        valueAssociations a = null;
        boolean found = false;
        if(elemsList.size()!=0){
            for(int i=0;i<elemsList.size()&&!found;i++){
                a = (valueAssociations)elemsList.elementAt(i);
                if(a.getValue() == value){ //class of value found
                    found = true;
                    a.addElement(_class);
                }
            }
        }
        if(!found || elemsList.size()==0){ //list empty or not added yet, add new element
            a = new valueAssociations(value);
            a.addElement(_class);
            elemsList.addElement(a);
        }
        totalElems++;
    }
    
    /**
     * <p>
     * this method returns a object with the frequencies list associated to 
     * the value passed as parameter 
     * </p>
     * @param ref The class of the list we want to retrieve
     * @return The object with the frequencies list of this class
     */
    public valueAssociations getVA(double ref){
        valueAssociations a = null;
        boolean found = false;
        
        if(elemsList.size()!=0){
            for(int i=0;i<elemsList.size()&&!found;i++){
                a = (valueAssociations)elemsList.elementAt(i);
                if(a.getValue() == ref){ //class of value found
                    return a;                
                }
            }
        }
        return null; //not found or list empty
    }
    
    /**
     * <p>
     * This method returns the valueAssociations object which is in position 'i'.
     * It is intended for sequential access.
     * </p>
     * @param i The index of the VA object we want
     * @return The element VA at position 'i'
     */
    public valueAssociations getnVA(int i){
        return (valueAssociations)elemsList.elementAt(i);
    }
    
    /**
     * <p>
     * This method gives the number of VA elements stored (i.e. the number of different values)
     * </p>
     * @return The number of elements in the list
     */
    public int getNumVA(){
        return elemsList.size();
    }
    
    /**
     * <p>
     * This method returns the total of elements ever added to this list.
     * </p>
     * @return The total number of pairs value-class ever added
     */
    public int getNumElems(){
        return totalElems;
    }
}

