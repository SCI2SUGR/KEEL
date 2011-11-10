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

import java.util.*;
import java.io.*;

/**
 * Class for handling an instance. All values (numeric, nominal, or
 * string) are internally stored as floating-point numbers. If an
 * attribute is nominal (or a string), the stored value is the index
 * of the corresponding nominal (or string) value in the attribute's
 * definition. We have chosen this approach in favor of a more elegant
 * object-oriented approach because it is much faster.
 */
public class M5Instance implements Serializable {

    /** Constant representing a missing value. */
    protected final static double MISSING_VALUE = Double.NaN;

    /**
     * The dataset the instance has access to.  Null if the instance
     * doesn't have access to any dataset.  Only if an instance has
     * access to a dataset, it knows about the actual attribute types.
     */
    protected M5Instances m_Dataset;

    /** The instance's attribute values. */
    protected double[] m_AttValues;

    /** The instance's weight. */
    protected double m_Weight;

    /**
     * Constructor that copies the attribute values and the weight from
     * the given instance. Reference to the dataset is set to null.
     * (ie. the instance doesn't have access to information about the
     * attribute types)
     *
     * @param instance the instance from which the attribute
     * values and the weight are to be copied
     */
    public M5Instance(M5Instance instance) {

        m_AttValues = instance.m_AttValues;
        m_Weight = instance.m_Weight;
        m_Dataset = null;
    }

    /**
     * Constructor that inititalizes instance variable with given
     * values. Reference to the dataset is set to null. (ie. the instance
     * doesn't have access to information about the attribute types)
     *
     * @param weight the instance's weight
     * @param attValues a vector of attribute values
     */
    public M5Instance(double weight, double[] attValues) {

        m_AttValues = attValues;
        m_Weight = weight;
        m_Dataset = null;
    }

    /**
     * Constructor of an instance that sets weight to one, all values to
     * be missing, and the reference to the dataset to null. (ie. the instance
     * doesn't have access to information about the attribute types)
     *
     * @param numAttributes the size of the instance
     */
    public M5Instance(int numAttributes) {

        m_AttValues = new double[numAttributes];
        for (int i = 0; i < m_AttValues.length; i++) {
            m_AttValues[i] = MISSING_VALUE;
        }
        m_Weight = 1;
        m_Dataset = null;
    }

    /**
     * Returns the attribute with the given index.
     *
     * @param index the attribute's index
     * @return the attribute at the given position
     * @throws Exception
     * @exception UnassignedDatasetException if instance doesn't have access to a
     * dataset
     */
    public M5Attribute attribute(int index) throws Exception {

        if (m_Dataset == null) {
            throw new Exception("M5Instace doesn't have access to a dataset!");
        }
        return m_Dataset.attribute(index);
    }

    /**
     * Returns the attribute with the given index. Does the same
     * thing as attribute().
     *
     * @param indexOfIndex the index of the attribute's index
     * @return the attribute at the given position
     * @throws Exception
     * @exception UnassignedDatasetException if instance doesn't have access to a
     * dataset
     */
    public M5Attribute attributeSparse(int indexOfIndex) throws Exception {

        if (m_Dataset == null) {
            throw new Exception("M5Instace doesn't have access to a dataset!");
        }
        return m_Dataset.attribute(indexOfIndex);
    }

    /**
     * Returns class attribute.
     *
     * @return the class attribute
     * @throws Exception
     * @exception UnassignedDatasetException if the class is not set or the
     * instance doesn't have access to a dataset
     */
    public M5Attribute classAttribute() throws Exception {

        if (m_Dataset == null) {
            throw new Exception("M5Instace doesn't have access to a dataset!");
        }
        return m_Dataset.classAttribute();
    }

    /**
     * Returns the class attribute's index.
     *
     * @return the class index as an integer
     * @throws Exception
     * @exception UnassignedDatasetException if instance doesn't have access to a dataset
     */
    public int classIndex() throws Exception {

        if (m_Dataset == null) {
            throw new Exception("M5Instace doesn't have access to a dataset!");
        }
        return m_Dataset.classIndex();
    }

    /**
     * Tests if an instance's class is missing.
     *
     * @return true if the instance's class is missing
     * @throws Exception
     * @exception UnassignedClassException if the class is not set or the instance doesn't
     * have access to a dataset
     */
    public boolean classIsMissing() throws Exception {

        if (classIndex() < 0) {
            throw new Exception("Class is not set!");
        }
        return isMissing(classIndex());
    }

    /**
     * Returns an instance's class value in internal format. (ie. as a
     * floating-point number)
     *
     * @return the corresponding value as a double (If the
     * corresponding attribute is nominal (or a string) then it returns the
     * value's index as a double).
     * @throws Exception
     * @exception UnassignedClassException if the class is not set or the instance doesn't
     * have access to a dataset
     */
    public double classValue() throws Exception {

        if (classIndex() < 0) {
            throw new Exception("Class is not set!");
        }
        return value(classIndex());
    }

    /**
     * Produces a shallow copy of this instance. The copy has
     * access to the same dataset. (if you want to make a copy
     * that doesn't have access to the dataset, use
     * <code>new M5Instace(instance)</code>
     *
     * @return the shallow copy
     */
    public Object copy() {

        M5Instance result = new M5Instance(this);
        result.m_Dataset = m_Dataset;
        return result;
    }

    /**
     * Returns the dataset this instance has access to. (ie. obtains
     * information about attribute types from) Null if the instance
     * doesn't have access to a dataset.
     *
     * @return the dataset the instance has accesss to
     */
    public M5Instances dataset() {

        return m_Dataset;
    }

    /**
     * Deletes an attribute at the given position (0 to
     * numAttributes() - 1). Only succeeds if the instance does not
     * have access to any dataset because otherwise inconsistencies
     * could be introduced.
     *
     * @param position the attribute's position
     * @exception RuntimeException if the instance has access to a
     * dataset
     */
    public void deleteAttributeAt(int position) {

        if (m_Dataset != null) {
            throw new RuntimeException("M5Instace has access to a dataset!");
        }
        forceDeleteAttributeAt(position);
    }

    /**
     * Returns an enumeration of all the attributes.
     *
     * @return enumeration of all the attributes
     * @throws Exception
     * @exception UnassignedDatasetException if the instance doesn't
     * have access to a dataset
     */
    public Enumeration enumerateAttributes() throws Exception {

        if (m_Dataset == null) {
            throw new Exception("M5Instace doesn't have access to a dataset!");
        }
        return m_Dataset.enumerateAttributes();
    }

    /**
     * Tests if the headers of two instances are equivalent.
     *
     * @param inst another instance
     * @return true if the header of the given instance is
     * equivalent to this instance's header
     * @throws Exception
     * @exception UnassignedDatasetException if instance doesn't have access to any
     * dataset
     */
    public boolean equalHeaders(M5Instance inst) throws Exception {

        if (m_Dataset == null) {
            throw new Exception("M5Instace doesn't have access to a dataset!");
        }
        return m_Dataset.equalHeaders(inst.m_Dataset);
    }

    /**
     * Returns the index of the attribute stored at the given position.
     * Just returns the given value.
     *
     * @param position the position
     * @return the index of the attribute stored at the given position
     */
    public int index(int position) {

        return position;
    }

    /**
     * Inserts an attribute at the given position (0 to
     * numAttributes()). Only succeeds if the instance does not
     * have access to any dataset because otherwise inconsistencies
     * could be introduced.
     *
     * @param position the attribute's position
     */
    public void insertAttributeAt(int position) {

        if (m_Dataset != null) {
            throw new RuntimeException("M5Instace has accesss to a dataset!");
        }
        if ((position < 0) ||
            (position > numAttributes())) {
            throw new IllegalArgumentException(
                    "Can't insert attribute: index out " +
                    "of range");
        }
        forceInsertAttributeAt(position);
    }

    /**
     * Tests if a specific value is "missing".
     *
     * @param attIndex the attribute's index
     */
    public boolean isMissing(int attIndex) {

        if (Double.isNaN(m_AttValues[attIndex])) {
            return true;
        }
        return false;
    }

    /**
     * Tests if a specific value is "missing". Does
     * the same thing as isMissing() if applied to an Instance.
     *
     * @param indexOfIndex the index of the attribute's index
     */
    public boolean isMissingSparse(int indexOfIndex) {

        if (Double.isNaN(m_AttValues[indexOfIndex])) {
            return true;
        }
        return false;
    }

    /**
     * Tests if a specific value is "missing".
     * The given attribute has to belong to a dataset.
     *
     * @param att the attribute
     */
    public boolean isMissing(M5Attribute att) {

        return isMissing(att.index());
    }

    /**
     * Tests if the given value codes "missing".
     *
     * @param val the value to be tested
     * @return true if val codes "missing"
     */
    public static boolean isMissingValue(double val) {

        return Double.isNaN(val);
    }

    /**
     * Merges this instance with the given instance and returns
     * the result. Dataset is set to null.
     *
     * @param inst the instance to be merged with this one
     * @return the merged instances
     */
    public M5Instance mergeInstance(M5Instance inst) {

        int m = 0;
        double[] newVals = new double[numAttributes() + inst.numAttributes()];
        for (int j = 0; j < numAttributes(); j++, m++) {
            newVals[m] = value(j);
        }
        for (int j = 0; j < inst.numAttributes(); j++, m++) {
            newVals[m] = inst.value(j);
        }
        return new M5Instance(1.0, newVals);
    }

    /**
     * Returns the double that codes "missing".
     *
     * @return the double that codes "missing"
     */
    public static double missingValue() {

        return MISSING_VALUE;
    }

    /**
     * Returns the number of attributes.
     *
     * @return the number of attributes as an integer
     */
    public int numAttributes() {

        return m_AttValues.length;
    }

    /**
     * Returns the number of class labels.
     *
     * @return the number of class labels as an integer if the
     * class attribute is nominal, 1 otherwise.
     * @throws Exception
     * @exception UnassignedDatasetException if instance doesn't have access to any
     * dataset
     */
    public int numClasses() throws Exception {

        if (m_Dataset == null) {
            throw new Exception("M5Instace doesn't have access to a dataset!");
        }
        return m_Dataset.numClasses();
    }

    /**
     * Returns the number of values present. Always the same as numAttributes().
     *
     * @return the number of values
     */
    public int numValues() {

        return m_AttValues.length;
    }

    /**
     * Replaces all missing values in the instance with the
     * values contained in the given array. A deep copy of
     * the vector of attribute values is performed before the
     * values are replaced.
     *
     * @param array containing the means and modes
     * @exception IllegalArgumentException if numbers of attributes are unequal
     */
    public void replaceMissingValues(double[] array) {

        if ((array == null) ||
            (array.length != m_AttValues.length)) {
            throw new IllegalArgumentException("Unequal number of attributes!");
        }
        freshAttributeVector();
        for (int i = 0; i < m_AttValues.length; i++) {
            if (isMissing(i)) {
                m_AttValues[i] = array[i];
            }
        }
    }

    /**
     * Sets the class value of an instance to be "missing". A deep copy of
     * the vector of attribute values is performed before the
     * value is set to be missing.
     * @throws Exception
     *
     * @exception UnassignedClassException if the class is not set
     * @exception UnassignedDatasetException if the instance doesn't
     * have access to a dataset
     */
    public void setClassMissing() throws Exception {

        if (classIndex() < 0) {
            throw new Exception("Class is not set!");
        }
        setMissing(classIndex());
    }

    /**
     * Sets the class value of an instance to the given value (internal
     * floating-point format).  A deep copy of the vector of attribute
     * values is performed before the value is set.
     *
     * @param value the new attribute value (If the corresponding
     * attribute is nominal (or a string) then this is the new value's
     * index as a double).
     * @throws Exception
     * @exception UnassignedClassException if the class is not set
     * @exception UnaddignedDatasetException if the instance doesn't
     * have access to a dataset
     */
    public void setClassValue(double value) throws Exception {

        if (classIndex() < 0) {
            throw new Exception("Class is not set!");
        }
        setValue(classIndex(), value);
    }

    /**
     * Sets the class value of an instance to the given value. A deep
     * copy of the vector of attribute values is performed before the
     * value is set.
     *
     * @param value the new class value (If the class
     * is a string attribute and the value can't be found,
     * the value is added to the attribute).
     * @throws Exception
     * @exception UnassignedClassException if the class is not set
     * @exception UnassignedDatasetException if the dataset is not set
     * @exception IllegalArgumentException if the attribute is not
     * nominal or a string, or the value couldn't be found for a nominal
     * attribute
     */
    public final void setClassValue(String value) throws Exception {

        if (classIndex() < 0) {
            throw new Exception("Class is not set!");
        }
        setValue(classIndex(), value);
    }

    /**
     * Sets the reference to the dataset. Does not check if the instance
     * is compatible with the dataset. Note: the dataset does not know
     * about this instance. If the structure of the dataset's header
     * gets changed, this instance will not be adjusted automatically.
     *
     * @param instances the reference to the dataset
     */
    public final void setDataset(M5Instances instances) {

        m_Dataset = instances;
    }

    /**
     * Sets a specific value to be "missing". Performs a deep copy
     * of the vector of attribute values before the value is set to
     * be missing.
     *
     * @param attIndex the attribute's index
     */
    public final void setMissing(int attIndex) {

        setValue(attIndex, MISSING_VALUE);
    }

    /**
     * Sets a specific value to be "missing". Performs a deep copy
     * of the vector of attribute values before the value is set to
     * be missing. The given attribute has to belong to a dataset.
     *
     * @param att the attribute
     */
    public final void setMissing(M5Attribute att) {

        setMissing(att.index());
    }

    /**
     * Sets a specific value in the instance to the given value
     * (internal floating-point format). Performs a deep copy
     * of the vector of attribute values before the value is set.
     *
     * @param attIndex the attribute's index
     * @param value the new attribute value (If the corresponding
     * attribute is nominal (or a string) then this is the new value's
     * index as a double).
     */
    public void setValue(int attIndex, double value) {

        freshAttributeVector();
        m_AttValues[attIndex] = value;
    }

    /**
     * Sets a specific value in the instance to the given value
     * (internal floating-point format). Performs a deep copy
     * of the vector of attribute values before the value is set.
     * Does exactly the same thing as setValue().
     *
     * @param indexOfIndex the index of the attribute's index
     * @param value the new attribute value (If the corresponding
     * attribute is nominal (or a string) then this is the new value's
     * index as a double).
     */
    public void setValueSparse(int indexOfIndex, double value) {

        freshAttributeVector();
        m_AttValues[indexOfIndex] = value;
    }

    /**
     * Sets a value of a nominal or string attribute to the given
     * value. Performs a deep copy of the vector of attribute values
     * before the value is set.
     *
     * @param attIndex the attribute's index
     * @param value the new attribute value (If the attribute
     * is a string attribute and the value can't be found,
     * the value is added to the attribute).
     * @throws Exception
     * @exception UnassignedDatasetException if the dataset is not set
     * @exception IllegalArgumentException if the selected
     * attribute is not nominal or a string, or the supplied value couldn't
     * be found for a nominal attribute
     */
    public final void setValue(int attIndex, String value) throws Exception {

        int valIndex;

        if (m_Dataset == null) {
            throw new Exception("M5Instace doesn't have access to a dataset!");
        }
        if (!attribute(attIndex).isNominal() &&
            !attribute(attIndex).isString()) {
            throw new IllegalArgumentException(
                    "Attribute neither nominal nor string!");
        }
        valIndex = attribute(attIndex).indexOfValue(value);
        if (valIndex == -1) {
            if (attribute(attIndex).isNominal()) {
                throw new IllegalArgumentException(
                        "Value not defined for given nominal attribute!");
            } else {
                attribute(attIndex).forceAddValue(value);
                valIndex = attribute(attIndex).indexOfValue(value);
            }
        }
        setValue(attIndex, (double) valIndex);
    }

    /**
     * Sets a specific value in the instance to the given value
     * (internal floating-point format). Performs a deep copy of the
     * vector of attribute values before the value is set, so if you are
     * planning on calling setValue many times it may be faster to
     * create a new instance using toDoubleArray.  The given attribute
     * has to belong to a dataset.
     *
     * @param att the attribute
     * @param value the new attribute value (If the corresponding
     * attribute is nominal (or a string) then this is the new value's
     * index as a double).
     */
    public final void setValue(M5Attribute att, double value) {

        setValue(att.index(), value);
    }

    /**
     * Sets a value of an nominal or string attribute to the given
     * value. Performs a deep copy of the vector of attribute values
     * before the value is set, so if you are planning on calling setValue many
     * times it may be faster to create a new instance using toDoubleArray.
     * The given attribute has to belong to a dataset.
     *
     * @param att the attribute
     * @param value the new attribute value (If the attribute
     * is a string attribute and the value can't be found,
     * the value is added to the attribute).
     * @exception IllegalArgumentException if the the attribute is not
     * nominal or a string, or the value couldn't be found for a nominal
     * attribute
     */
    public final void setValue(M5Attribute att, String value) {

        if (!att.isNominal() &&
            !att.isString()) {
            throw new IllegalArgumentException(
                    "Attribute neither nominal nor string!");
        }
        int valIndex = att.indexOfValue(value);
        if (valIndex == -1) {
            if (att.isNominal()) {
                throw new IllegalArgumentException(
                        "Value not defined for given nominal attribute!");
            } else {
                att.forceAddValue(value);
                valIndex = att.indexOfValue(value);
            }
        }
        setValue(att.index(), (double) valIndex);
    }

    /**
     * Sets the weight of an instance.
     *
     * @param weight the weight
     */
    public final void setWeight(double weight) {

        m_Weight = weight;
    }

    /**
     * Returns the value of a nominal (or string) attribute
     * for the instance.
     *
     * @param attIndex the attribute's index
     * @return the value as a string
     * @throws Exception
     * @exception IllegalArgumentException if the attribute is not a nominal
     * (or string) attribute.
     * @exception UnassignedDatasetException if the instance doesn't belong
     * to a dataset.
     */
    public final String stringValue(int attIndex) throws Exception {

        if (m_Dataset == null) {
            throw new Exception("M5Instace doesn't have access to a dataset!");
        }
        if (!m_Dataset.attribute(attIndex).isNominal() &&
            !m_Dataset.attribute(attIndex).isString()) {
            throw new IllegalArgumentException(
                    "Attribute neither nominal nor string!");
        }
        return m_Dataset.attribute(attIndex).
                value((int) value(attIndex));
    }

    /**
     * Returns the value of a nominal (or string) attribute
     * for the instance.
     *
     * @param att the attribute
     * @return the value as a string
     * @throws Exception
     * @exception IllegalArgumentException if the attribute is not a nominal
     * (or string) attribute.
     * @exception UnassignedDatasetException if the instance doesn't belong
     * to a dataset.
     */
    public final String stringValue(M5Attribute att) throws Exception {

        return stringValue(att.index());
    }

    /**
     * Returns the values of each attribute as an array of doubles.
     *
     * @return an array containing all the instance attribute values
     */
    public double[] toDoubleArray() {

        double[] newValues = new double[m_AttValues.length];
        System.arraycopy(m_AttValues, 0, newValues, 0,
                         m_AttValues.length);
        return newValues;
    }

    /**
     * Returns the description of one instance. If the instance
     * doesn't have access to a dataset, it returns the internal
     * floating-point values. Quotes string
     * values that contain whitespace characters.
     *
     * @return the instance's description as a string
     */
    public String toString() {

        StringBuffer text = new StringBuffer();

        for (int i = 0; i < m_AttValues.length; i++) {
            if (i > 0) {
                text.append(",");
            }
            try {
                text.append(toString(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return text.toString();
    }

    /**
     * Returns the description of one value of the instance as a
     * string. If the instance doesn't have access to a dataset, it
     * returns the internal floating-point value. Quotes string
     * values that contain whitespace characters, or if they
     * are a question mark.
     *
     * @param attIndex the attribute's index
     * @return the value's description as a string
     * @throws Exception
     */
    public final String toString(int attIndex) throws Exception {

        StringBuffer text = new StringBuffer();

        if (isMissing(attIndex)) {
            text.append("?");
        } else {
            if (m_Dataset == null) {
                text.append(M5StaticUtils.doubleToString(m_AttValues[attIndex],
                        6));
            } else {
                if (m_Dataset.attribute(attIndex).isNominal() ||
                    m_Dataset.attribute(attIndex).isString()) {
                    text.append(M5StaticUtils.quote(stringValue(attIndex)));
                } else {
                    text.append(M5StaticUtils.doubleToString(value(attIndex), 6));
                }
            }
        }
        return text.toString();
    }

    /**
     * Returns the description of one value of the instance as a
     * string. If the instance doesn't have access to a dataset it
     * returns the internal floating-point value. Quotes string
     * values that contain whitespace characters, or if they
     * are a question mark.
     * The given attribute has to belong to a dataset.
     *
     * @param att the attribute
     * @return the value's description as a string
     * @throws Exception
     */
    public final String toString(M5Attribute att) throws Exception {

        return toString(att.index());
    }

    /**
     * Returns an instance's attribute value in internal format.
     *
     * @param attIndex the attribute's index
     * @return the specified value as a double (If the corresponding
     * attribute is nominal (or a string) then it returns the value's index as a
     * double).
     */
    public double value(int attIndex) {

        return m_AttValues[attIndex];
    }

    /**
     * Returns an instance's attribute value in internal format.
     * Does exactly the same thing as value() if applied to an M5Instace.
     *
     * @param indexOfIndex the index of the attribute's index
     * @return the specified value as a double (If the corresponding
     * attribute is nominal (or a string) then it returns the value's index as a
     * double).
     */
    public double valueSparse(int indexOfIndex) {

        return m_AttValues[indexOfIndex];
    }

    /**
     * Returns an instance's attribute value in internal format.
     * The given attribute has to belong to a dataset.
     *
     * @param att the attribute
     * @return the specified value as a double (If the corresponding
     * attribute is nominal (or a string) then it returns the value's index as a
     * double).
     */
    public double value(M5Attribute att) {

        return value(att.index());
    }

    /**
     * Returns the instance's weight.
     *
     * @return the instance's weight as a double
     */
    public final double weight() {

        return m_Weight;
    }

    /**
     * Deletes an attribute at the given position (0 to
     * numAttributes() - 1).
     *
     * @param pos the attribute's position
     */

    void forceDeleteAttributeAt(int position) {

        double[] newValues = new double[m_AttValues.length - 1];

        System.arraycopy(m_AttValues, 0, newValues, 0, position);
        if (position < m_AttValues.length - 1) {
            System.arraycopy(m_AttValues, position + 1,
                             newValues, position,
                             m_AttValues.length - (position + 1));
        }
        m_AttValues = newValues;
    }

    /**
     * Inserts an attribute at the given position
     * (0 to numAttributes()) and sets its value to be missing.
     *
     * @param pos the attribute's position
     */
    void forceInsertAttributeAt(int position) {

        double[] newValues = new double[m_AttValues.length + 1];

        System.arraycopy(m_AttValues, 0, newValues, 0, position);
        newValues[position] = MISSING_VALUE;
        System.arraycopy(m_AttValues, position, newValues,
                         position + 1, m_AttValues.length - position);
        m_AttValues = newValues;
    }

    /**
     * Private constructor for subclasses. Does nothing.
     */
    protected M5Instance() {
    }

    /**
     * Clones the attribute vector of the instance and
     * overwrites it with the clone.
     */
    private void freshAttributeVector() {

        m_AttValues = toDoubleArray();
    }
}

