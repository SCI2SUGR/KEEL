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

package keel.Algorithms.Genetic_Rule_Learning.M5Rules;

/**
 * Class to manipulate an itemset.
 */
public class Itemset
{
  /** The dataset which the itemset has access to. */
  protected MyDataset dataset;

  /** Values of the itemset. */
  protected double[] values;

  /** The weight of the itemset. */
  protected double weight;

  /** Constant that represents the missing value. */
  protected final static double MISSING_VALUE = Double.NaN;

  /** Constructor that copies the values and the weight.
   *
   * @param itemset The itemset to copy.
   */
  public Itemset( Itemset itemset )
  {
    values = itemset.values;
    weight = itemset.weight;
    dataset = null;
  }

  /** Constructor that sets the values and the weight.
   *
   * @param w The weight.
   * @param attributeValues The values.
   */
  public Itemset( double w, double[] attributeValues )
  {
    values = attributeValues;
    weight = w;
    dataset = null;
  }

  /**
   * Constructor of an instance that sets weight to one, all values to
   * be missing, and the reference to the dataset to null. (ie. the instance
   * doesn't have access to information about the attribute types)
   *
   * @param numAttributes the size of the instance
   */
  public Itemset(int numAttributes) {

    values = new double[numAttributes];
    for (int i = 0; i < values.length; i++) {
      values[i] = MISSING_VALUE;
    }
    weight = 1;
    dataset = null;
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


  /** Returns the index of the class attribute.
   * @return the index of the class attribute.
   */
  public int classIndex()
  {
    if ( dataset == null ) {
      throw new RuntimeException("Itemset doesn't have access to a dataset!");
      //System.err.println("dataset in itemset is null");
      //return(-1);
    }
    else
      return dataset.getClassIndex();
  }

  /** Function to test if the class attribute is missing.
   *
   * @return True if the value of the class attribute is missing.
   */
  public boolean classIsMissing()
  {
    if ( classIndex() < 0 )
      throw new RuntimeException( "Class is not set." );
    else
      return isMissing( classIndex() );
  }

  /** Returns the index of the value of the class.
   * @return the index of the value of the class.
   */
  public double getClassValue()
  {
    if ( classIndex() < 0 )
    {
      throw new RuntimeException("dataset in itemset is null");
      //return (-1);
    }
    else
      return getValue( classIndex() );
  }

  /**
   * Returns class attribute.
   *
   * @return the class attribute
   * @throws Exception
   * @exception UnassignedDatasetException if the class is not set or the
   * instance doesn't have access to a dataset
   */
  public MyAttribute getClassAttribute() throws Exception {

    if (dataset == null) {
      throw new Exception("Itemset doesn't have access to a dataset!");
    }
    return dataset.getClassAttribute();
  }


  /** Returns the number of class values.
   * @return the number of class values.
   */
  public int numClasses()
  {
    if ( dataset == null )
    {
      throw new RuntimeException("dataset in itemset is null");
      //return (-1);
    }
    else
      return dataset.numClasses();
  }

  /** Returns the attribute with the given index.
   * @param index the attribute's index
   * @return the attribute with the given index.
   */
  public MyAttribute getAttribute( int index )
  {
    if ( dataset == null )
    {
      throw new RuntimeException("dataset in itemset is null");
      //return null;
    }
    else
      return dataset.getAttribute( index );
  }

  /** Returns the number of attributes.
   * @return the number of attributes.
   */
  public int numAttributes()
  {
    return values.length;
  }

  /** Returns the number of values.  Always the same as numAttributes().
   *  @return the number of values.
   */
  public int numValues()
  {
    return values.length;
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
  public MyAttribute attributeSparse(int indexOfIndex) throws Exception {

      if (dataset == null) {
          throw new Exception("Itemset doesn't have access to a dataset!");
      }
      return dataset.getAttribute(indexOfIndex);
  }


  /** Function to set a value.
   *
   * @param value The value.
   */
  public void setClassValue( double value )
  {
    if ( classIndex() < 0 )
      throw new RuntimeException( "Class is not set." );
    else
      setValue( classIndex(),value );
  }

  /** Function to set a value.
   *
   * @param value The value.
   * @throws Exception If the index of the class is not set.
   */
  public void setClassValue( String value ) throws Exception
  {
    if ( classIndex() < 0 )
      throw new RuntimeException( "Class is not set." );
    else
      setValue( classIndex(),value );
  }



  /** Function to set a value.
   *
   * @param index The index of the attribute.
   * @param value The value.
   */
  public void setValue( int index, double value )
  {
    double[] help = new double[values.length];

    System.arraycopy( values, 0, help, 0, values.length );
    values = help;
    values[index] = value;
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
    double[] help = new double[values.length];

    System.arraycopy( values, 0, help, 0, values.length );
    values = help;
    values[indexOfIndex] = value;
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

      if (dataset == null) {
          throw new Exception("Itemset doesn't have access to a dataset!");
      }
      if (!getAttribute(attIndex).isDiscret()) {
          throw new IllegalArgumentException("Attribute neither nominal nor string!");
      }
      valIndex = getAttribute(attIndex).valueIndex(value);
      if (valIndex == -1) {
          if (getAttribute(attIndex).isDiscret()) {
              throw new IllegalArgumentException("Value not defined for given nominal attribute!");
          } else {
              //getAttribute(attIndex).forceAddValue(value);
              valIndex = getAttribute(attIndex).valueIndex(value);
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
  public final void setValue(MyAttribute att, double value) {

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
   * @throws Exception if the the attribute is not
   * nominal or a string, or the value couldn't be found for a nominal
   * attribute
   */
  public final void setValue(MyAttribute att, String value) throws Exception{

    setValue(att.index(), value);
  }

  /**
   * Returns an instance's attribute value in internal format.
   * Does exactly the same thing as value() if applied to an Itemset.
   *
   * @param indexOfIndex the index of the attribute's index
   * @return the specified value as a double (If the corresponding
   * attribute is nominal (or a string) then it returns the value's index as a
   * double).
   */
  public double getValueSparse(int indexOfIndex) {

      return values[indexOfIndex];
  }

  /** Returns the value of the given attribute.
   * @param index the attribute's index
   * @return the value of the given attribute.
   */
  public double getValue( int index )
  {
    return values[index];
  }

  /** Returns the value of the given attribute.
   * @param index the attribute's index
   * @return the value of the given attribute.
   * @throws Exception if the Itemset doesn't have access to a dataset or
   * the attribute neither nominal nor string.
   */
  public String getStringValue( int index ) throws Exception
  {
    if (dataset == null) {
      throw new Exception("Itemset doesn't have access to a dataset!");
    }
    if (!dataset.getAttribute(index).isDiscret()) {
      throw new IllegalArgumentException("Attribute neither nominal nor string!");
    }
    return dataset.getAttribute(index).value(index);
  }

  /** Returns the value of the given attribute.
   * @param att the attribute
   * @return the value of the given attribute.
   * @throws Exception if the Itemset doesn't have access to a dataset or
   * the attribute neither nominal nor string.
   */
  public String getStringValue( MyAttribute att ) throws Exception
  {
    return getStringValue(att.index());
  }

  /** Returns the value of the given attribute.
   * @param att the attribute
   * @return the value of the given attribute.
   */
  public double getValue(MyAttribute att) {

    return values[att.index()];
  }

  /** Function to set the weight.
   *
   * @param w The weight.
   */
  public final void setWeight( double w )
  {
    weight = w;
  }

  /** Returns the itemset weight.
   * @return the itemset weight.
   */
  public final double getWeight()
  {
    return weight;
  }

  /** Returns the dataset of this itemset.
   * @return the dataset of this itemset.
   */
  public MyDataset getDataset()
  {
    return dataset;
  }

  /** Function to set the dataset.
   *
   * @param data The dataset.
   */
  public final void setDataset( MyDataset data )
  {
    dataset = data;
  }

  /** Function to check if a value is missing.
   *
   * @param index The index of the attribute to check.
   *
   * @return True is the value of the attribute is missing. False otherwise.
   */
  public boolean isMissing( int index )
  {
    if ( Double.isNaN( values[index] ) )
      return true;
    else
      return false;
  }

  /**
   * Tests if a specific value is "missing".
   * The given attribute has to belong to a dataset.
   *
   * @param att the attribute
   *
   * @return True is the value of the attribute is missing. False otherwise.
   */
  public boolean isMissing(MyAttribute att) {

    return isMissing(att.index());
  }

  /**
   * Tests if a specific value is "missing". Does
   * the same thing as isMissing() if applied to an Instance.
   *
   * @param indexOfIndex the index of the attribute's index
   * @return True is the value of the attribute is missing. False otherwise.
   */
  public boolean isMissingSparse(int indexOfIndex) {

    if (Double.isNaN(values[indexOfIndex]))
      return true;
    else
      return false;
  }

  /** Function to check if the value given is the missing value.
   *
   * @param val	The value to check.
   *
   * @return True if the value given is the missing value. False otherwise.
   */
  public static boolean isMissingValue( double val )
  {
    return Double.isNaN( val );
  }

  /** Returns the missing value.
   * @return the missing value.
   */
  public static double getMissingValue()
  {
    return MISSING_VALUE;
  }

  /** Function to set as missing the class value.
   *
   */
  public void setClassMissing()
  {
    if ( classIndex() < 0 )
      throw new RuntimeException( "Class is not set." );
    else
      setMissing( classIndex() );
  }

  /** Function to set a value as missing.
   *
   * @param index The index of the attribute.
   */
  public final void setMissing( int index )
  {
    setValue( index, MISSING_VALUE );
  }

  /**
   * Sets a specific value to be "missing". Performs a deep copy
   * of the vector of attribute values before the value is set to
   * be missing. The given attribute has to belong to a dataset.
   *
   * @param att the attribute
   */
  public final void setMissing(MyAttribute att) {

    setMissing(att.index());
  }


  /** Function to copy an itemset.
   *
   * @return The itemset created.
   */
  public Object copy()
  {
    Itemset result = new Itemset( this );
    result.dataset = dataset;

    return result;
  }

  /**
   * Inserts an attribute at the given position (0 to
   * numAttributes()). Only succeeds if the instance does not
   * have access to any dataset because otherwise inconsistencies
   * could be introduced.
   *
   * @param position the attribute's position
   * @exception RuntimeException if the instance has accesss to a
   * dataset
   * @exception IllegalArgumentException if the position is out of range
   */
  public void insertAttributeAt(int position) {

    if (dataset != null) {
      throw new RuntimeException("Itemset has accesss to a dataset!");
    }
    if ((position < 0) ||(position > numAttributes())) {
      throw new IllegalArgumentException("Can't insert attribute: index out of range");
    }
    forceInsertAttributeAt(position);
  }

  /**
   * Merges this instance with the given instance and returns
   * the result. Dataset is set to null.
   *
   * @param inst the instance to be merged with this one
   * @return the merged instances
   */
  public Itemset mergeInstance(Itemset inst) {

    int m = 0;
    double[] newVals = new double[numAttributes() + inst.numAttributes()];
    for (int j = 0; j < numAttributes(); j++, m++) {
        newVals[m] = getValue(j);
    }
    for (int j = 0; j < inst.numAttributes(); j++, m++) {
        newVals[m] = inst.getValue(j);
    }
    return new Itemset(1.0, newVals);
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

      if (dataset != null) {
          throw new RuntimeException("Itemset has access to a dataset!");
      }
      forceDeleteAttributeAt(position);
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

    if ((array == null) || (array.length != values.length)) {
      throw new IllegalArgumentException("Unequal number of attributes!");
    }

    double[] help = new double[values.length];

    System.arraycopy( values, 0, help, 0, values.length );
    values = help;

    for (int i = 0; i < values.length; i++) {
      if (isMissing(i)) {
        values[i] = array[i];
      }
    }
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
  public boolean equalHeaders(Itemset inst) throws Exception {

    if (dataset == null) {
      throw new Exception("Itemset doesn't have access to a dataset!");
    }
    return dataset.equalHeaders(inst.dataset);
  }

  /**
   * Returns an enumeration of all the attributes.
   *
   * @return enumeration of all the attributes
   * @throws Exception
   * @exception UnassignedDatasetException if the instance doesn't
   * have access to a dataset
   */
  public java.util.Enumeration enumerateAttributes() throws Exception {

    if (dataset == null) {
      throw new Exception("M5Instace doesn't have access to a dataset!");
    }
    return dataset.enumerateAttributes();
  }

  /** Function to print the itemset.
   * @return the string representation of this itemset.
   */
  public String toString()
  {
    String result = "";
    for ( int i = 0; i < dataset.numAttributes(); i++ )
    {
      MyAttribute att = dataset.getAttribute( i );

      if ( att.isContinuous() )
        result += att.name() + "=" + values[i] + " ";
      else
        result += att.name() + "=" + att.value( (int)values[i] ) + " ";
    }

    return result;
  }

  /**
   * Function to print of one value of the itemset.
   *
   * @param att the attribute
   * @return the value's description as a string
   * @throws Exception
   */
  public final String toString(MyAttribute att) throws Exception {

    return Integer.toString(att.index());
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
    }
    else {
      if (dataset == null) {
        text.append(M5StaticUtils.doubleToString(values[attIndex],6));
      } else {
        if (dataset.getAttribute(attIndex).isDiscret()) {
          text.append(M5StaticUtils.quote(getStringValue(attIndex)));
        } else {
          text.append(M5StaticUtils.doubleToString(getValue(attIndex), 6));
        }
      }
    }
    return text.toString();
  }

  /**
   * Deletes an attribute at the given position (0 to
   * numAttributes() - 1).
   *
   * @param position the attribute's position
   */
  private void forceDeleteAttributeAt(int position) {

    double[] newValues = new double[values.length - 1];

    System.arraycopy(values, 0, newValues, 0, position);
    if (position < values.length - 1) {
      System.arraycopy(values, position + 1,newValues, position,
                       values.length - (position + 1));
    }
    values = newValues;
  }

  /**
   * Inserts an attribute at the given position
   * (0 to numAttributes()) and sets its value to be missing.
   *
   * @param position the attribute's position
   */
  private void forceInsertAttributeAt(int position) {

      double[] newValues = new double[values.length + 1];

      System.arraycopy(values, 0, newValues, 0, position);
      newValues[position] = MISSING_VALUE;
      System.arraycopy(values, position, newValues,position + 1, values.length - position);
      values = newValues;
  }

  protected Itemset(){}

}
