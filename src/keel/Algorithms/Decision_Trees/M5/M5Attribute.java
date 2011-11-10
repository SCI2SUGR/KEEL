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
* @author Written by Cristobal Romero (Universidad de Córdoba) 10/10/2007
* @version 0.1
* @since JDK 1.5
*</p>
*/

package keel.Algorithms.Decision_Trees.M5;

import java.io.*;
import java.util.*;

/**
 * Class for handling an attribute. Once an attribute has been created,
 * it can't be changed. <p>
 *
 * Three attribute types are supported:
 * <ul>
 *    <li> numeric: <ul>
 *         This type of attribute represents a floating-point number.
 *    </ul>
 *    <li> nominal: <ul>
 *         This type of attribute represents a fixed set of nominal values.
 *    </ul>
 *    <li> string: <ul>
 *         This type of attribute represents a dynamically expanding set of
 *         nominal values. String attributes are not used by the learning
 *         schemes. They can be used, for example,  to store an
 *         identifier with each instance in a dataset.
 *    </ul>
 * </ul>
 */
public class M5Attribute implements Serializable {

    /** Constant set for numeric attributes. */
    public final static int NUMERIC = 0;

    /** Constant set for nominal attributes. */
    public final static int NOMINAL = 1;

    /** Constant set for attributes with string values. */
    public final static int STRING = 2;

    /** Strings longer than this will be stored compressed. */
    private final static int STRING_COMPRESS_THRESHOLD = 200;

    /** The attribute's name. */
    private String m_Name;

    /** The attribute's type. */
    private int m_Type;

    /** The attribute's values (if nominal or string). */
    private M5Vector m_Values;

    /** Mapping of values to indices (if nominal or string). */
    private Hashtable m_Hashtable;

    /** The attribute's index. */
    private int m_Index;

    /**
     * Constructor for a numeric attribute.
     *
     * @param attributeName the name for the attribute
     */
    public M5Attribute(String attributeName) {

        m_Name = attributeName;
        m_Index = -1;
        m_Values = null;
        m_Hashtable = null;
        m_Type = NUMERIC;
    }

    /**
     * Constructor for nominal attributes and string attributes.
     * If a null vector of attribute values is passed to the method,
     * the attribute is assumed to be a string.
     *
     * @param attributeName the name for the attribute
     * @param attributeValues a vector of strings denoting the
     * attribute values. Null if the attribute is a string attribute.
     */
    public M5Attribute(String attributeName,
                       M5Vector attributeValues) {

        m_Name = attributeName;
        m_Index = -1;
        if (attributeValues == null) {
            m_Values = new M5Vector();
            m_Hashtable = new Hashtable();
            m_Type = STRING;
        } else {
            m_Values = new M5Vector(attributeValues.size());
            m_Hashtable = new Hashtable(attributeValues.size());
            for (int i = 0; i < attributeValues.size(); i++) {
                Object store = attributeValues.elementAt(i);
                if (((String) store).length() > STRING_COMPRESS_THRESHOLD) {
                    try {
                        store = new SerializedObject(attributeValues.elementAt(
                                i), true);
                    } catch (Exception ex) {
                        System.err.println(
                                "Couldn't compress nominal attribute value -"
                                + " storing uncompressed.");
                    }
                }
                m_Values.addElement(store);
                m_Hashtable.put(store, new Integer(i));
            }
            m_Type = NOMINAL;
        }
    }

    /**
     * Produces a shallow copy of this attribute.
     *
     * @return a copy of this attribute with the same index
     */
    public Object copy() {

        M5Attribute copy = new M5Attribute(m_Name);

        copy.m_Index = m_Index;
        if (!isNominal() && !isString()) {
            return copy;
        }
        copy.m_Type = m_Type;
        copy.m_Values = m_Values;
        copy.m_Hashtable = m_Hashtable;

        return copy;
    }

    /**
     * Returns an enumeration of all the attribute's values if
     * the attribute is nominal or a string, null otherwise.
     *
     * @return enumeration of all the attribute's values
     */
    public final Enumeration enumerateValues() {

        if (isNominal() || isString()) {
            final Enumeration ee = m_Values.elements();
            return new Enumeration() {
                public boolean hasMoreElements() {
                    return ee.hasMoreElements();
                }

                public Object nextElement() {
                    Object oo = ee.nextElement();
                    if (oo instanceof SerializedObject) {
                        return ((SerializedObject) oo).getObject();
                    } else {
                        return oo;
                    }
                }
            };
        }
        return null;
    }

    /**
     * Tests if given attribute is equal to this attribute.
     *
     * @param other the Object to be compared to this attribute
     * @return true if the given attribute is equal to this attribute
     */
    public final boolean equals(Object other) {

        if ((other == null) || !(other.getClass().equals(this.getClass()))) {
            return false;
        }
        M5Attribute att = (M5Attribute) other;
        if (!m_Name.equals(att.m_Name)) {
            return false;
        }
        if (isNumeric() && att.isNumeric()) {
            return true;
        }
        if (isNumeric() || att.isNumeric()) {
            return false;
        }
        if (m_Values.size() != att.m_Values.size()) {
            return false;
        }
        for (int i = 0; i < m_Values.size(); i++) {
            if (!m_Values.elementAt(i).equals(att.m_Values.elementAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the index of this attribute.
     *
     * @return the index of this attribute
     */
    public final int index() {

        return m_Index;
    }

    /**
     * Returns the index of a given attribute value. (The index of
     * the first occurence of this value.)
     *
     * @param value the value for which the index is to be returned
     * @return the index of the given attribute value if attribute
     * is nominal or a string, -1 if it is numeric or the value
     * can't be found
     */
    public final int indexOfValue(String value) {

        if (!isNominal() && !isString()) {
            return -1;
        }
        Object store = value;
        if (value.length() > STRING_COMPRESS_THRESHOLD) {
            try {
                store = new SerializedObject(value, true);
            } catch (Exception ex) {
                System.err.println("Couldn't compress string attribute value -"
                                   + " searching uncompressed.");
            }
        }
        Integer val = (Integer) m_Hashtable.get(store);
        if (val == null) {
            return -1;
        } else {
            return val.intValue();
        }
    }

    /**
     * Test if the attribute is nominal.
     *
     * @return true if the attribute is nominal
     */
    public final boolean isNominal() {

        return (m_Type == NOMINAL);
    }

    /**
     * Tests if the attribute is numeric.
     *
     * @return true if the attribute is numeric
     */
    public final boolean isNumeric() {

        return (m_Type == NUMERIC);
    }

    /**
     * Tests if the attribute is a string.
     *
     * @return true if the attribute is a string
     */
    public final boolean isString() {

        return (m_Type == STRING);
    }

    /**
     * Returns the attribute's name.
     *
     * @return the attribute's name as a string
     */
    public final String name() {

        return m_Name;
    }

    /**
     * Returns the number of attribute values. Returns 0 for numeric attributes.
     *
     * @return the number of attribute values
     */
    public final int numValues() {

        if (!isNominal() && !isString()) {
            return 0;
        } else {
            return m_Values.size();
        }
    }

    /**
     * Returns a description of this attribute in ARFF format. Quotes
     * strings if they contain whitespace characters, or if they
     * are a question mark.
     *
     * @return a description of this attribute as a string
     */
    public final String toString() {

        StringBuffer text = new StringBuffer();

        text.append("@attribute " + M5StaticUtils.quote(m_Name) + " ");
        if (isNominal()) {
            text.append('{');
            Enumeration enuma = enumerateValues();
            while (enuma.hasMoreElements()) {
                text.append(M5StaticUtils.quote((String) enuma.nextElement()));
                if (enuma.hasMoreElements()) {
                    text.append(',');
                }
            }
            text.append('}');
        } else {
            if (isNumeric()) {
                text.append("numeric");
            } else {
                text.append("string");
            }
        }
        return text.toString();
    }

    /**
     * Returns the attribute's type as an integer.
     *
     * @return the attribute's type.
     */
    public final int type() {

        return m_Type;
    }

    /**
     * Returns a value of a nominal or string attribute.
     * Returns an empty string if the attribute is neither
     * nominal nor a string attribute.
     *
     * @param valIndex the value's index
     * @return the attribute's value as a string
     */
    public final String value(int valIndex) {

        if (!isNominal() && !isString()) {
            return "";
        } else {
            Object val = m_Values.elementAt(valIndex);

            // If we're storing strings compressed, uncompress it.
            if (val instanceof SerializedObject) {
                val = ((SerializedObject) val).getObject();
            }
            return (String) val;
        }
    }

    /**
     * Constructor for a numeric attribute with a particular index.
     *
     * @param attributeName the name for the attribute
     * @param index the attribute's index
     */
    M5Attribute(String attributeName, int index) {

        this(attributeName);

        m_Index = index;
    }

    /**
     * Constructor for nominal attributes and string attributes with
     * a particular index.
     * If a null vector of attribute values is passed to the method,
     * the attribute is assumed to be a string.
     *
     * @param attributeName the name for the attribute
     * @param attributeValues a vector of strings denoting the attribute values.
     * Null if the attribute is a string attribute.
     * @param index the attribute's index
     */
    M5Attribute(String attributeName, M5Vector attributeValues,
                int index) {

        this(attributeName, attributeValues);

        m_Index = index;
    }

    /**
     * Adds a string value to the list of valid strings for attributes
     * of type STRING and returns the index of the string.
     *
     * @param value The string value to add
     * @return the index assigned to the string, or -1 if the attribute is not
     * of type M5Attribute.STRING
     */
    public int addStringValue(String value) {

        if (!isString()) {
            return -1;
        }
        Object store = value;

        if (value.length() > STRING_COMPRESS_THRESHOLD) {
            try {
                store = new SerializedObject(value, true);
            } catch (Exception ex) {
                System.err.println("Couldn't compress string attribute value -"
                                   + " storing uncompressed.");
            }
        }
        Integer index = (Integer) m_Hashtable.get(store);
        if (index != null) {
            return index.intValue();
        } else {
            int intIndex = m_Values.size();
            m_Values.addElement(store);
            m_Hashtable.put(store, new Integer(intIndex));
            return intIndex;
        }
    }

    /**
     * Adds a string value to the list of valid strings for attributes
     * of type STRING and returns the index of the string. This method is
     * more efficient than addStringValue(String) for long strings.
     *
     * @param src The Attribute containing the string value to add.
     * @param index the index of the string value in the source attribute.
     * @return the index assigned to the string, or -1 if the attribute is not
     * of type M5Attribute.STRING
     */
    public int addStringValue(M5Attribute src, int index) {

        if (!isString()) {
            return -1;
        }
        Object store = src.m_Values.elementAt(index);
        Integer oldIndex = (Integer) m_Hashtable.get(store);
        if (oldIndex != null) {
            return oldIndex.intValue();
        } else {
            int intIndex = m_Values.size();
            m_Values.addElement(store);
            m_Hashtable.put(store, new Integer(intIndex));
            return intIndex;
        }
    }

    /**
     * Adds an attribute value. Creates a fresh list of attribute
     * values before adding it.
     *
     * @param value the attribute value
     */
    final void addValue(String value) {

        m_Values = (M5Vector) m_Values.copy();
        m_Hashtable = (Hashtable) m_Hashtable.clone();
        forceAddValue(value);
    }

    /**
     * Produces a shallow copy of this attribute with a new name.
     *
     * @param newName the name of the new attribute
     * @return a copy of this attribute with the same index
     */
    final M5Attribute copy(String newName) {

        M5Attribute copy = new M5Attribute(newName);

        copy.m_Index = m_Index;
        if (!isNominal() && !isString()) {
            return copy;
        }
        copy.m_Type = m_Type;
        copy.m_Values = m_Values;
        copy.m_Hashtable = m_Hashtable;

        return copy;
    }

    /**
     * Removes a value of a nominal or string attribute. Creates a
     * fresh list of attribute values before removing it.
     *
     * @param index the value's index
     * @exception IllegalArgumentException if the attribute is not nominal
     */
    final void delete(int index) {

        if (!isNominal() && !isString()) {
            throw new IllegalArgumentException("Can only remove value of" +
                                               "nominal or string attribute!");
        } else {
            m_Values = (M5Vector) m_Values.copy();
            m_Values.removeElementAt(index);
            Hashtable hash = new Hashtable(m_Hashtable.size());
            Enumeration enuma = m_Hashtable.keys();
            while (enuma.hasMoreElements()) {
                Object string = enuma.nextElement();
                Integer valIndexObject = (Integer) m_Hashtable.get(string);
                int valIndex = valIndexObject.intValue();
                if (valIndex > index) {
                    hash.put(string, new Integer(valIndex - 1));
                } else if (valIndex < index) {
                    hash.put(string, valIndexObject);
                }
            }
            m_Hashtable = hash;
        }
    }

    /**
     * Adds an attribute value.
     *
     * @param value the attribute value
     */
    final void forceAddValue(String value) {

        Object store = value;
        if (value.length() > STRING_COMPRESS_THRESHOLD) {
            try {
                store = new SerializedObject(value, true);
            } catch (Exception ex) {
                System.err.println("Couldn't compress string attribute value -"
                                   + " storing uncompressed.");
            }
        }
        m_Values.addElement(store);
        m_Hashtable.put(store, new Integer(m_Values.size() - 1));
    }

    /**
     * Sets the index of this attribute.
     *
     * @param the index of this attribute
     */
    final void setIndex(int index) {

        m_Index = index;
    }

    /**
     * Sets a value of a nominal attribute or string attribute.
     * Creates a fresh list of attribute values before it is set.
     *
     * @param index the value's index
     * @param string the value
     * @exception IllegalArgumentException if the attribute is not nominal or
     * string.
     */
    final void setValue(int index, String string) {

        if (!isNominal() && !isString()) {
            throw new IllegalArgumentException("Can only set value of nominal" +
                                               "or string attribute!");
        } else {
            m_Values = (M5Vector) m_Values.copy();
            m_Hashtable = (Hashtable) m_Hashtable.clone();
            Object store = string;
            if (string.length() > STRING_COMPRESS_THRESHOLD) {
                try {
                    store = new SerializedObject(string, true);
                } catch (Exception ex) {
                    System.err.println(
                            "Couldn't compress string attribute value -"
                            + " storing uncompressed.");
                }
            }
            m_Hashtable.remove(m_Values.elementAt(index));
            m_Values.setElementAt(store, index);
            m_Hashtable.put(store, new Integer(index));
        }
    }

    /**
     * Simple main method for testing this class.
     */
    public static void main(String[] ops) {

        try {

            // Create numeric attributes "length" and "weight"
            M5Attribute length = new M5Attribute("length");
            M5Attribute weight = new M5Attribute("weight");

            // Create vector to hold nominal values "first", "second", "third"
            M5Vector my_nominal_values = new M5Vector(3);
            my_nominal_values.addElement("first");
            my_nominal_values.addElement("second");
            my_nominal_values.addElement("third");

            // Create nominal attribute "position"
            M5Attribute position = new M5Attribute("position",
                    my_nominal_values);

            // Print the name of "position"
            System.out.println("Name of \"position\": " + position.name());

            // Print the values of "position"
            Enumeration attValues = position.enumerateValues();
            while (attValues.hasMoreElements()) {
                String string = (String) attValues.nextElement();
                System.out.println("Value of \"position\": " + string);
            }

            // Shallow copy attribute "position"
            M5Attribute copy = (M5Attribute) position.copy();

            // Test if attributes are the same
            System.out.println("Copy is the same as original: " +
                               copy.equals(position));

            // Print index of attribute "weight" (should be unset: -1)
            System.out.println("Index of attribute \"weight\" (should be -1): " +
                               weight.index());

            // Print index of value "first" of attribute "position"
            System.out.println(
                    "Index of value \"first\" of \"position\" (should be 0): " +
                    position.indexOfValue("first"));

            // Tests type of attribute "position"
            System.out.println("\"position\" is numeric: " + position.isNumeric());
            System.out.println("\"position\" is nominal: " + position.isNominal());
            System.out.println("\"position\" is string: " + position.isString());

            // Prints name of attribute "position"
            System.out.println("Name of \"position\": " + position.name());

            // Prints number of values of attribute "position"
            System.out.println("Number of values for \"position\": " +
                               position.numValues());

            // Prints the values (againg)
            for (int i = 0; i < position.numValues(); i++) {
                System.out.println("Value " + i + ": " + position.value(i));
            }

            // Prints the attribute "position" in ARFF format
            System.out.println(position);

            // Checks type of attribute "position" using constants
            switch (position.type()) {
            case M5Attribute.NUMERIC:
                System.out.println("\"position\" is numeric");
                break;
            case M5Attribute.NOMINAL:
                System.out.println("\"position\" is nominal");
                break;
            case M5Attribute.STRING:
                System.out.println("\"position\" is string");
                break;
            default:
                System.out.println("\"position\" has unknown type");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

