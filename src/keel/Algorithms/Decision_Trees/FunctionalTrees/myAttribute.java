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

import java.util.ArrayList;

/**
 * This class contains the most useful information about an attribute, and provides a set of functions to
 * manage this information easily.
 *
 * @author Written by Victoria Lopez Morales (University of Granada) 15/05/2009
 * @version 0.1
 * @since JDK1.6
 */
public class myAttribute {
    /**
     * Representative name of the attribute
     */
	private String name;
	
	/**
	 * Data type of the data of the attribute: 1 if integer, 2 if real, 3 if nominal
	 */
	private int attributeType;
	/**
	 * Determines if an attribute is an input attribute (true) or an output attribute (false)
	 */
	private boolean input;
	
	/**
	 * The minimum value for the attribute if it is not a nominal attribute
	 */
	private double min;
	/**
     * The maximum value for the attribute if it is not a nominal attribute
     */
    private double max;
    /**
     * The list of possible values for the attribute if it is a nominal attribute
     */
    private ArrayList <String> values;
	
	/** 
     * Creates an attribute with empty values that we can identify
     */     
    public myAttribute () {
		name = "notRead";
		attributeType = -1;
		min = 0;
		max = 0;
		values = new ArrayList <String> ();
		input = true;
	}
	
	/** 
     * Creates an attribute with the name of the attribute, the data type of the attribute and whether the
     * attribute is input or output; the rest of the values are initialized with empty values that we can 
     * identify. It is used when we create a nominal attribute.
     *
     * @param newname  The name of the attribute that is going to be created
     * @param type   The data type of the attribute that is going to be created
     * @param newinput  Whereas the attribute that is going to be created is input or output
     */     
    public myAttribute (String newname, int type, boolean newinput) {
		name = newname;
		attributeType = type;
		min = 0;
		max = 0;
		values = new ArrayList <String> ();
		input = newinput;
	}
	
	/** 
     * Creates an attribute with the name of the attribute, the data type of the attribute, the minimum value
     * for the attribute, the maximum value for the attribute, the minimum value for the attribute and whether
     * the attribute is input or output; the rest of the values arte initialized with empty values that we can 
     * identify. It is used when we create a numerical attribute.
     *
     * @param newname  The name of the attribute that is going to be created
     * @param type   The data type of the attribute that is going to be created
     * @param newmin    The minimum value for the attribute that is going to be created
     * @param newmax    The maximum value for the attribute that is going to be created
     * @param newinput  Whereas the attribute that is going to be created is input or output
     */     
    public myAttribute (String newname, int type, double newmin, double newmax, boolean newinput) {
		name = newname;
		attributeType = type;
		min = newmin;
		max = newmax;
		values = new ArrayList <String> ();
		input = newinput;
	}
	
	/** 
     * Creates an attribute from another existing attribute
     *
     * @param att  Original attribute from which we are going to create a copy
     */   
    public myAttribute (myAttribute att) {
		String aux;
		
		// Copy each data field to the new attribute
        this.name = new String (att.name);
		this.attributeType = att.attributeType;
		this.min = att.min;
		this.max = att.max;
		this.values = new ArrayList <String> ();
		for (int i=0; i<att.values.size(); i++) {
			aux = new String ((String)att.values.get(i));
			this.values.add((String)aux);
		}
		this.input = att.input;
	}
	
    /** 
     * Checks if an attribute is the same attribute as another object
     *
     * @param obj  Object that is checked to see if it is the same attribute
     * @return true if the attributes are the same, false otherwise
     * @see java.lang.Object#equals(java.lang.Object)
     */ 
    public boolean equals (Object obj) {
        // First we check if the reference is the same
    	if (this == obj)
    		return true;
    	
    	// Then we check if the object exists and is from the class myAttribute
        if((obj == null) || (obj.getClass() != this.getClass()))
    		return false;
    	
        // object must be myAttribute at this point
    	myAttribute test = (myAttribute)obj;
    	
    	// We check the class attributes of the myAttribute class
    	return ((min == test.min) && (max == test.max) && (attributeType == test.attributeType) && (input == test.input) && (name == test.name || (name != null && name.equals(test.name))) && (values == test.values || (values != null && values.equals(test.values))));
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
    	
    	bits = Double.doubleToLongBits(max);
    	hash = 31 * hash + (int)(bits ^ (bits >>> 32));
    	bits = Double.doubleToLongBits(min);
    	hash = 31 * hash + (int)(bits ^ (bits >>> 32));
    	hash = 31 * hash + attributeType;
    	hash = 31 * hash + (null == name ? 0 : name.hashCode());
    	hash = 31 * hash + (null == values ? 0 : values.hashCode());
    	hash = 31 * hash + (input ? 1 : 0);
    	return hash;
    }
    
    /** 
     * Overriden function that converts the class to a string
     *
     * @return the string representation of the class
     * @see java.lang.Object#toString()
     */ 
    public String toString() { 
    	return (name + " " + attributeType + " " + min + " " + max + " " + values + " " + input);
    }

    /** 
     * Answers if the attribute is nominal or not
     *
     * @return true if the attribute is nominal, false otherwise
     */
    public boolean isNominal() {
    	if (attributeType == 3) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }

    /** 
     * Gets the name of the attribute
     *
     * @return the name of the attribute
     */
	public String getName() {
		return name;
	}

    /**
     * Replaces the name of the attribute with another new name
     * 
     * @param name  New name for the attribute 
     */
	public void setName(String name) {
		this.name = name;
	}

	/** 
     * Gets the data type of the data of the attribute
     *
     * @return the data type of the data of the attribute
     */
    public int getAttributeType() {
		return attributeType;
	}

	/**
	 * Replaces the data type of the data of the attribute with a new data type
	 * 
	 * @param attributeType    New data type of the data of the attribute
	 */
	public void setAttributeType(int attributeType) {
		this.attributeType = attributeType;
	}

	/** 
     * Gets the minimum value for the attribute if it is not a nominal attribute
     *
     * @return the minimum value for the attribute if it is not a nominal attribute
     */
	public double getMin() {
		return min;
	}

	/**
     * Replaces the minimum value for the attribute with a new minimum value
     * 
     * @param min    New minimum value for the attribute
     */
    public void setMin(double min) {
		this.min = min;
	}

	/** 
     * Gets the maximum value for the attribute if it is not a nominal attribute
     *
     * @return the maximum value for the attribute if it is not a nominal attribute
     */
    public double getMax() {
		return max;
	}

    /**
     * Replaces the maximum value for the attribute with a new maximum value
     * 
     * @param max    New maximum value for the attribute
     */
    public void setMax(double max) {
		this.max = max;
	}

	/** 
     * Gets an array of possible values if it is a nominal attribute
     *
     * @return the array of possible values if it is a nominal attribute
     */
    public ArrayList<String> getValues() {
		return values;
	}

    /**
     * Replaces the array of possible values for the attribute data type with a new array of values
     * 
     * @param values    New array of possible values for the attribute
     */
    public void setValues(ArrayList<String> values) {
		this.values = values;
	}
	
	/** 
     * Gets the value of a nominal attribute from the position in the list of possible values
     *
     * @return the value of a nominal attribute from the position in the list of possible values
     */
    public String getValue (int j) {
		String value;
		
		value = new String ((String) values.get(j));
		
		return value;
	}
	
    /**
     * Replaces the value in a position of a nominal attribute with a new value
     * 
     * @param newvalue  New value of a nominal attribute for the specified position
     * @param j    Position of the value that is being replaced
     */
    public void setValue (String newvalue, int j) {
		String value;
		
		value = new String (newvalue);
		values.set(j, value);
	}

	/**
	 * Answer if the attribute is an input attribute or not
	 * 
	 * @return true, if it is an input attribute; false, if it is an output attribute
	 */
	public boolean isInput() {
		return input;
	}

	/**
     * Changes the logical attribute stating if an attribute is input or not
     * 
     * @param input    Logical value stating if an attribute is input or not
     */
    public void setInput(boolean input) {
		this.input = input;
	}
}

