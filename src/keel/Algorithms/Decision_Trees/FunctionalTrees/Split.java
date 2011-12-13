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

package keel.Algorithms.Decision_Trees.FunctionalTrees;

/**
 * Data structure that is used during the construction of the decision tree.
 * It stores the information about how a node can be divided into two descendant, this
 * means, which attribute is used for the split and the value of the attribute used
 * in the split.
 * 
 * @author Written by Victoria Lopez Morales (University of Granada) 14/04/2009 
 * @version 1.0 
 * @since JDK1.5
 */
public class Split {
    /**
     * Position of the attribute that is used for the split
     */
    private int attribute;
    /**
     * Value for the attribute that we are using for the split
     */
    private double value;
    
    /** 
     * Creates a split with empty values that we can identify
     */ 
    Split () {
        attribute = 0;
        value = 0.0;
    }
    
    /** 
     * Creates a split with the attribute used for it and the value used in it
     *
     * @param att  The attribute that is going to be used in the split
     * @param val   The value of the attribute that is going to be used in the split
     */     
    Split (int att, double val) {
        attribute = att;
        value = val;
    }
    
    /** 
     * Creates a split from another existing split
     *
     * @param sp  Original split from which we are going to create a copy
     */   
    Split (Split sp) {
        this.attribute = sp.attribute;
        this.value = sp.value;
    }
    
    /** 
     * Checks if a split is the same split as another object
     *
     * @param obj  Object that is checked to see if it is the same split
     * @return true if the splits are the same, false otherwise
     * @see java.lang.Object#equals(java.lang.Object)
     */ 
    public boolean equals (Object obj) {
        // First we check if the reference is the same
        if (this == obj)
            return true;
        
        // Then we check if the object exists and is from the class Split
        if((obj == null) || (obj.getClass() != this.getClass()))
            return false;
        
        // object must be Split at this point
        Split test = (Split)obj;
        
        // We check the class attributes of the Split class
        return ((attribute == test.attribute) && (value == test.value));
    }
    
    /** 
     * Hash-code function for the class that is used when object is inserted in a structure like a hashtable
     *
     * @return the hash code obtained
     * @see java.lang.Object#hashCode()
     */ 
    public int hashCode() {
        int hash = 7;
        long bits;
        
        bits = Double.doubleToLongBits(value);
        hash = 31 * hash + (int)(bits ^ (bits >>> 32));
        hash = 31 * hash + attribute;
        return hash;
    }
    
    /** 
     * Overriden function that converts the class to a string
     *
     * @return the string representation of the class
     * @see java.lang.Object#toString()
     */ 
    public String toString() { 
        return (attribute + " " + value);
    }

    /** 
     * Gets the position of the attribute for the split
     *
     * @return the position of the attribute for the split
     */
    public int getAttribute() {
        return attribute;
    }

    /**
     * Replaces the position of the attribute for the split with another new position
     * 
     * @param attribute  New position of the attribute for the split 
     */
    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    /** 
     * Gets the value of the attribute for the split
     *
     * @return the value of the attribute for the split
     */
    public double getValue() {
        return value;
    }

    /**
     * Replaces the value of the attribute for the split with another new value
     * 
     * @param value  New value of the attribute for the split 
     */
    public void setValue(double value) {
        this.value = value;
    }
}

