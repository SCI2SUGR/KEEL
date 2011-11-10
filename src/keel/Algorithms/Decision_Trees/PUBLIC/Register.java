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

package keel.Algorithms.Decision_Trees.PUBLIC;

/**
 * 
 * File: Register.java
 * 
 * Data structure that is used in the construction of the decision tree.
 * It stores the information about the value of the attribute the register is hosting,
 * the class related to the register and one identificator of the registry. This data
 * structure is used to build the separate list of attributes.
 * 
 * @author Written by Victoria Lopez Morales (University of Granada) 15/03/2009 
 * @version 1.0 
 * @since JDK1.5
 */

public class Register implements Comparable <Register> {
    /**
     * Identifier of the register
     */
	int identifier;
	/**
	 * Value of the corresponding attribute
	 */
	double attributeValue;
	/**
	 * Class of the register
	 */
	int outputClass;
	
	/** 
     * Creates a register from a id, a value attribute and an output class of the item. It is used for
     * attribute lists to store the data contained in the dataset.
     *
     * @param id Identifier of the register
     * @param value Value of the corresponding attribute
     * @param oclass Class of the register
     */     
    public Register (int id, double value, int oclass) {
		identifier = id;
		attributeValue = value;
		outputClass = oclass;
	}

    /** 
     * Creates a register from another existing register
     *
     * @param reg  Original register from which we are going to create a copy
     */   
    public Register (Register reg) {
        this.identifier = reg.identifier;
        this.attributeValue = reg.attributeValue;
        this.outputClass = reg.outputClass;
    }
	
	/**
	 * Gets the identifier of the register itself
	 * 
	 * @return identifier of the register
	 */
	public int getIdentifier () {
		return identifier;
	}
	
	/**
	 * Gets the value stored in the register
	 * 
	 * @return value of the corresponding attribute
	 */
	public double getAttributeValue () {
		return attributeValue;
	}
	
	/**
	 * Gets the class of the register
	 * 
	 * @return class of the register
	 */
	public int getOutputClass () {
		return outputClass;
	}
	
	/**
	 * Replaces the value of the identifier of the register with another new identifier
	 * 
	 * @param id   New identifier of the register
	 */
	public void setIdentifier (int id) {
		identifier = id;
	}
	
	/**
	 * Replaces the value of the corresponding attribute with another new value
	 * 
	 * @param value    New value of the corresponding attribute
	 */
	public void setAttributeValue (double value) {
		attributeValue = value;
	}

	/**
	 * Replaces the class associated with the register with another class
	 * 
	 * @param oclass   New class of the register
	 */
	public void setOutputClass (int oclass) {
		outputClass = oclass;
	}
	
	/** 
     * Checks if a register is the same register as another object
     *
     * @param obj  Object that is checked to see if it is the same register
     * @return true if the registers are the same, false otherwise
     * @see java.lang.Object#equals(java.lang.Object)
     */ 
    public boolean equals (Object obj) {
        // First we check if the reference is the same
        if (this == obj)
            return true;
        
        // Then we check if the object exists and is from the class Register
        if((obj == null) || (obj.getClass() != this.getClass()))
            return false;
        
        // object must be Register at this point
        Register test = (Register)obj;
        
        // We check the class attributes of the Register class
        return ((identifier == test.identifier) && (attributeValue == test.attributeValue) && (outputClass == test.outputClass));
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
        
        bits = Double.doubleToLongBits(attributeValue);
        hash = 31 * hash + (int)(bits ^ (bits >>> 32));
        hash = 31 * hash + identifier;
        hash = 31 * hash + outputClass;
        return hash;
    }
    
    /** 
     * Overriden function that converts the class to a string
     *
     * @return the string representation of the class
     * @see java.lang.Object#toString()
     */ 
    public String toString() {
        return identifier + " " + attributeValue + " " + outputClass;
    } 
    
    /** 
     * Overriden function that symbolizes if the other register is equal, greater or smaller than the original
     * register with a integer as response
     *
     * @return 0 if both registers are equal, 1 if this register is greater than the other register, -1 if 
     * this register is smaller than the other register
     *
     */ 
    public int compareTo(Register other_register) {
    	if (this.attributeValue == other_register.attributeValue)
            return 0;
        else if ((this.attributeValue) > other_register.attributeValue)
            return 1;
        else
            return -1;
    } 
}

