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
import java.util.Enumeration;
import java.util.Vector;

import keel.Dataset.*;


/**
 * Class to implement the dataset
 */

public class MyDataset
{
  /** The name of the dataset. */
  protected String name = "";

  /** The attributes. */
  protected Vector attributes;

  /** The itemsets. */
  protected Vector itemsets;

  /** The index of the class attribute. */
  protected int classIndex;

  /** Keel dataset InstanceSet **/
  protected InstanceSet IS;

  /** Buffer of values for sparse itemsets */
  protected double[] m_ValueBuffer;

  /** Buffer of indices for sparse itemsets */
  protected int[] m_IndicesBuffer;

  /** Function to read the .dat file that contains the information of the dataset.
   *
   * @param name The reader object where the itemsets are readed.
   * @param train The flag if the file is for training
   */
  public MyDataset( String name, boolean train )
  {
    try {
      // create the set of itemsets
      IS = new InstanceSet();
      // Read the itemsets.
      IS.readSet(name,train);
    } catch (DatasetException e) {
      System.err.println("Error loading dataset itemsets");
      e.printStackTrace();
      System.exit(-1);
    } catch (HeaderFormatException e) {
      System.err.println("Error loading dataset itemsets");
      e.printStackTrace();
      System.exit(-1);
    }

    //Store Dataset file attributes
    readHeader();

    itemsets = new Vector( IS.getNumInstances() );

    // read all the itemsets
    getItemsetFull();

  }

  /**
   * Creates an empty set of itemsets. Uses the given
   * attribute information. Sets the capacity of the set of
   * itemsets to 0 if its negative. Given attribute information
   * must not be changed after this constructor has been used.
   *
   * @param name the name of the relation
   * @param attInfo the attribute information
   * @param capacity the capacity of the set
   */
  public MyDataset(String name, Vector attInfo, int capacity) {

    name = name;
    classIndex = -1;
    attributes = attInfo;
    for (int i = 0; i < numAttributes(); i++) {
      getAttribute(i).setIndex(i);
    }
    itemsets = new Vector(capacity);
  }


  /** Constructor that copies another dataset.
   *
   * @param dataset The dataset to be copied.
   */
  public MyDataset( MyDataset dataset )
  {
    this( dataset, dataset.numItemsets() );
    dataset.copyItemsets( 0, this, dataset.numItemsets() );
  }

  /**
   * Creates a new set of itemsets by copying a
   * subset of another set.
   *
   * @param source the set of itemsets from which a subset
   * is to be created
   * @param first the index of the first itemset to be copied
   * @param toCopy the number of itemsets to be copied
   * @exception IllegalArgumentException if first and toCopy are out of range
   */
  public MyDataset(MyDataset source, int first, int toCopy) {

    this(source, toCopy);

    if ((first < 0) || ((first + toCopy) > source.numItemsets())) {
      throw new IllegalArgumentException("Parameters first and/or toCopy out of range");
    }
    source.copyItemsets(first, this, toCopy);
  }

  /** Constructor to copy all the attributes of another dataset but the itemsets.
   *
   * @param dataset The dataset to be copied.
   * @param capacity The number of itemsets.
   */
  public MyDataset( MyDataset dataset, int capacity )
  {
    if ( capacity < 0 )
      capacity = 0;

    classIndex = dataset.classIndex;
    name = dataset.getName();
    attributes = dataset.attributes;
    itemsets = new Vector( capacity );
  }

  /** Function to stores header of a data file.
   *
   */
  private void readHeader()
  {
    String attributeName;
    Vector attributeValues;
    int i;

    name = Attributes.getRelationName();

    // Create vectors to hold information temporarily.
    attributes = new Vector();

    Attribute at;

    // store attribute inputs and of the header
    for (int j =0; j<Attributes.getInputNumAttributes(); j++)
    {
      at=Attributes.getInputAttribute(j);
      attributeName = at.getName();

      // check if it is real
      if(at.getType()==2)
      {
        float min = (float) at.getMinAttribute();
        float max = (float) at.getMinAttribute();
        attributes.addElement( new MyAttribute( attributeName, j ) );
        MyAttribute att = (MyAttribute)attributes.elementAt( j );
        att.setRange( min, max );
        att.activate();
      }
      else
      {
        if(at.getType()==1) // check if it is integer
        {
          int min = (int) at.getMinAttribute();
          int max = (int) at.getMinAttribute();
          attributes.addElement( new MyAttribute( attributeName, j ) );
          MyAttribute att = (MyAttribute)attributes.elementAt( j );
          att.setRange( min, max );
          att.activate();
        }
        else // it is nominal
        {
          attributeValues = new Vector();
          for(int k=0; k<at.getNumNominalValues();k++)
          {
            attributeValues.addElement(at.getNominalValue(k));
          }
          attributes.addElement( new MyAttribute( attributeName, attributeValues, j ) );
          MyAttribute att = (MyAttribute)attributes.elementAt( j );
          att.activate();
        }

      }

    }//for



    // store outputs of the header
    at=Attributes.getOutputAttribute(0);
    attributeName = at.getName();

    int j = Attributes.getNumAttributes() - 1;

    // check if it is real
    if(at.getType()==2)
    {
      float min = (float) at.getMinAttribute();
      float max = (float) at.getMinAttribute();
      attributes.addElement( new MyAttribute( attributeName, j ) );
      MyAttribute att = (MyAttribute)attributes.elementAt( j );
      att.setRange( min, max );
      att.activate();
    }
    else
    {
      if(at.getType()==1) // check if it is integer
      {
        int min = (int) at.getMinAttribute();
        int max = (int) at.getMinAttribute();
        attributes.addElement( new MyAttribute( attributeName, j ) );
        MyAttribute att = (MyAttribute)attributes.elementAt( j );
        att.setRange( min, max );
        att.activate();
      }
      else // it is nominal
      {
        attributeValues = new Vector();
        for(int k=0; k<at.getNumNominalValues();k++)
        {
          attributeValues.addElement(at.getNominalValue(k));
        }
        attributes.addElement( new MyAttribute( attributeName, attributeValues, j ) );
        MyAttribute att = (MyAttribute)attributes.elementAt( j );
        att.activate();
      }
    }


    // set the index of the output class
    classIndex = Attributes.getNumAttributes() - 1;

  }


  /** Function to read an itemset and appends it to the dataset.
   *
   * @return True if the itemset was readed succesfully.
   */
  private boolean getItemsetFull( )
  {

    //fill itemset
    for( int j=0; j<IS.getNumInstances();j++)
    {

      double[] itemset = new double[Attributes.getNumAttributes()];
      int index;

      // Get values for all input attributes.
      for ( int i = 0; i < Attributes.getInputNumAttributes(); i++ )
      {

        // check type and if there is null

        if(IS.getInstance(j).getInputMissingValues(i))
          itemset[i] = Itemset.getMissingValue();
        else
        {
          if(Attributes.getInputAttribute(i).getType()==0) //nominal
          {
            for(int k=0; k<Attributes.getInputAttribute(i).getNumNominalValues();k++ )
              if(Attributes.getInputAttribute(i).getNominalValue(k).equals( IS.getInstance(j).getInputNominalValues(i)  ))
                itemset[i]=(double)k;
          }
          else // real and integer
          {
            itemset[i]=IS.getInstance(j).getInputRealValues(i);
          }
        } // else


      } //for

      // Get values for output attribute.
      int i=Attributes.getInputNumAttributes();

      //check type and if there is null
      if(IS.getInstance(j).getOutputMissingValues(0))
        itemset[i] = Itemset.getMissingValue();
      else
      {
        if(Attributes.getOutputAttribute(0).getType()==0) //nominal
        {
          for(int k=0; k<Attributes.getOutputAttribute(0).getNumNominalValues();k++ )
            if(Attributes.getOutputAttribute(0).getNominalValue(k).equals( IS.getInstance(j).getOutputNominalValues(0)  ))
              itemset[i]=(double)k;
        }
        else // real and integer
        {
          itemset[i]=IS.getInstance(j).getOutputRealValues(0);
        }
      } // else

      // Add itemset to dataset
      addItemset( new Itemset( 1, itemset ) );

    }// for

    return true;
  }

  /** Function to add one itemset.
   *
   * @param itemset The itemset to add to the dataset.
   */
  public final void addItemset( Itemset itemset )
  {
    Itemset newItemset = (Itemset)itemset.copy();

    newItemset.setDataset( this );
    itemsets.addElement( newItemset );

  }

  /** Returns the name of the dataset.
   * @return the name of the dataset.
   */
  public String getName()
  {
    return name;
  }

  /** Returns the name of the dataset.
   * @param name the name of the dataset.
   */
  public void setName(String name)
  {
    this.name=name;
  }

  /** Returns the attribute that has the index.
   *
   * @param index	int The index of the attribute.
   * @return the attribute that has the index.
   */
  public final MyAttribute getAttribute( int index )
  {
    return (MyAttribute) attributes.elementAt( index );
  }

  /** Returns the attribute that has the name.
   *
   * @param name String The name of the attribute.
   * @return the attribute that has the name.
   */
  public final MyAttribute getAttribute( String name )
  {
    for ( int i = 0; i < attributes.size(); i++ )
      if ( ( (MyAttribute)attributes.elementAt( i ) ).name().equalsIgnoreCase( name ) )
        return (MyAttribute) attributes.elementAt( i );

    return null;
  }

  /**
   * Gets the value of all itemsets in this dataset for a particular
   * attribute. Useful in conjunction with Utils.sort to allow iterating
   * through the dataset in sorted order for some attribute.
   *
   * @param index the index of the attribute.
   * @return an array containing the value of the desired attribute for
   * each itemset in the dataset.
   */
  public double[] attributeToDoubleArray(int index) {

    double[] result = new double[numItemsets()];
    for (int i = 0; i < result.length; i++) {
      result[i] = itemset(i).getValue(index);
    }
    return result;
  }

  /** Returns class attribute.
   * @return class attribute
   */
  public final MyAttribute getClassAttribute()
  {
    if ( classIndex < 0 )
    {
      System.err.println("Class index wrong:"+classIndex);
      return null;
    }
    return getAttribute( classIndex );
  }

  /**
   * Sets the class attribute.
   *
   * @param att attribute to be the class
   */
  public final void setClass(MyAttribute att) {
    classIndex = att.index();
  }


  /** Returns the index of the class attribute.
   * @return the index of the class attribute.
   */
  public final int getClassIndex()
  {
    return classIndex;
  }

  /**
   * Sets the class index of the set.
   * If the class index is negative there is assumed to be no class.
   * (ie. it is undefined)
   *
   * @param classIndex the new class index
   * @exception IllegalArgumentException if the class index is too big or < 0
   */
  public final void setClassIndex(int classIndex) {

    if (classIndex >= numAttributes()) {
      throw new IllegalArgumentException("Invalid class index: " + classIndex);
    }
    this.classIndex = classIndex;
  }

  /**
   * Computes the variance for a numeric attribute.
   *
   * @param attIndex the numeric attribute
   * @return the variance if the attribute is numeric
   * @exception IllegalArgumentException if the attribute is not numeric
   */
  public final double variance(int attIndex) {

    double sum = 0, sumSquared = 0, sumOfWeights = 0;

    if (getAttribute(attIndex).isDiscret()) {
      throw new IllegalArgumentException(
          "Can't compute variance because attribute "+attIndex+" is " +
          "not numeric!");
    }
    for (int i = 0; i < numItemsets(); i++) {
      if (!itemset(i).isMissing(attIndex)) {
        sum += itemset(i).getWeight() * itemset(i).getValue(attIndex);
        sumSquared += itemset(i).getWeight() *
            itemset(i).getValue(attIndex) *
            itemset(i).getValue(attIndex);
        sumOfWeights += itemset(i).getWeight();
      }
    }

    if (M5StaticUtils.smOrEq(sumOfWeights, 1)) {
      return 0;
    }

    return (sumSquared - (sum * sum / sumOfWeights)) /
        (sumOfWeights - 1);
  }

  /**
   * Computes the variance for a numeric attribute.
   *
   * @param att the numeric attribute
   * @return the variance if the attribute is numeric
   * @exception IllegalArgumentException if the attribute is not numeric
   */
  public final double variance(MyAttribute att) {

    return variance(att.index());
  }

  /**
   * Computes the variance for the class attribute.
   *
   * @return the variance if the class is numeric
   * @exception IllegalArgumentException if the class is not numeric
   */
  public final double classVariance() {

    double sumSquared = 0.0;
    double mean=averageClassValue();

    if (getClassAttribute().isDiscret()) {
      throw new IllegalArgumentException(
          "Can't compute variance because class is " +
          "not numeric!");
    }
    for (int i = 0; i < numItemsets(); i++) {
      if (!itemset(i).isMissing(classIndex)) {
        double deviation=itemset(i).getClassValue()-mean;
        sumSquared += (deviation * deviation);
      }
    }

    return sumSquared/((double)numItemsets());
  }

  /**
   * Computes the standard deviation for the class attribute.
   *
   * @return the standard deviation if the class is numeric
   * @exception IllegalArgumentException if the class is not numeric
   */
  public final double classSTD() {

    return Math.sqrt(classVariance());
  }

  /**
   * Computes the variance for the instances covered by a rule.
   *
   * @param r the rule
   * @return the variance if the class is numeric
   * @exception IllegalArgumentException if the class is not numeric
   */
  public final double classVariance(Rule r) {

    double sumSquared = 0;
    double mean=averageClassValue(r);

    if (getClassAttribute().isDiscret()) {
      throw new IllegalArgumentException(
          "Can't compute rule variance because class is " +
          "not numeric!");
    }

    Mask covered=new Mask(size());
    filter(covered,r);
    while(covered.next()) {
      if (!itemset(covered.getIndex()).isMissing(classIndex)) {
        double deviation=itemset(covered.getIndex()).getClassValue()-mean;
        sumSquared += (deviation * deviation);
      }
    }

    return sumSquared/((double)covered.getnActive());
  }

  /**
   * Computes the standard deviation for the instances covered by a rule.
   *
   * @param r the rule
   * @return the standard deviation if the class is numeric
   * @exception IllegalArgumentException if the class is not numeric
   */
  public final double classSTD(Rule r) {

    return Math.sqrt(classVariance(r));
  }

  /**
   * Computes the variance (over the predicted values)
   * for the instances covered by a rule.
   *
   * @param r the rule
   * @return the variance if the class is numeric
   * @exception IllegalArgumentException if the class is not numeric
   */
  public final double classPredictedVariance(Rule r) {

    double sumSquared = 0.0;
    double mean=averagePredictedClassValue(r);
    Function function=r.getFunction();

    if (getClassAttribute().isDiscret()) {
      throw new IllegalArgumentException(
          "Can't compute predicted variance because class is " +
          "not numeric!");
    }

    Mask covered=new Mask(size());
    filter(covered,r);
    while(covered.next()) {
      if (!itemset(covered.getIndex()).isMissing(classIndex)) {
        double deviation=function.predict(itemset(covered.getIndex()))-mean;
        sumSquared += (deviation * deviation);
      }
    }

    return sumSquared/((double)covered.getnActive());
  }

  /**
   * Computes the standard deviation (over the predicted values)
   * for the instances covered by a rule.
   *
   * @param r the rule
   * @return the standard deviation if the class is numeric
   * @exception IllegalArgumentException if the class is not numeric
   */
  public final double classPredictedSTD(Rule r) {

    return Math.sqrt(classPredictedVariance(r));
  }

  /**
   * Returns the mean (mode) for a numeric (nominal) attribute as
   * a floating-point value. Returns 0 if the attribute is neither nominal nor
   * numeric. If all values are missing it returns zero.
   *
   * @param attIndex the attribute's index
   * @return the mean or the mode
   */
  public final double meanOrMode(int attIndex) {

      double result, found;
      int[] counts;

      if (getAttribute(attIndex).isDiscret()) {
          result = found = 0;
          for (int j = 0; j < numItemsets(); j++) {
              if (!itemset(j).isMissing(attIndex)) {
                  found += itemset(j).getWeight();
                  result += itemset(j).getWeight() * itemset(j).getValue(attIndex);
              }
          }
          if (M5StaticUtils.eq(found, 0)) {
              return 0;
          } else {
              return result / found;
          }
      } else if (getAttribute(attIndex).isDiscret()) {
          counts = new int[getAttribute(attIndex).numValues()];
          for (int j = 0; j < numItemsets(); j++) {
              if (!itemset(j).isMissing(attIndex)) {
                  counts[(int) itemset(j).getValue(attIndex)] += itemset(j).getWeight();
              }
          }
          return (double) M5StaticUtils.maxIndex(counts);
      } else {
          return 0;
      }
  }

  /**
   * Returns the mean (mode) for a numeric (nominal) attribute as a
   * floating-point value.  Returns 0 if the attribute is neither
   * nominal nor numeric.  If all values are missing it returns zero.
   *
   * @param att the attribute
   * @return the mean or the mode
   */
  public final double meanOrMode(MyAttribute att) {

      return meanOrMode(att.index());
  }

  /** Returns the number of attributes.
   * @return the number of attributes.
   */
  public final int numAttributes()
  {
    return attributes.size();
  }

  /** Returns the number of possible values of the class attribute.
   * @return the number of class labels as an integer if the class
   * attribute is nominal, 1 otherwise.
   */
  public final int numClasses()
  {
    if ( classIndex < 0 )
    {
      System.err.println("Class index wrong:"+classIndex);
      return -1;
    }
    if (getClassAttribute().isContinuous())
      return 1;
    else
      return getClassAttribute().numValues();
  }

  /** Returns the number of itemsets.
   * @return the number of itemsets.
   */
  public final int numItemsets()
  {
    return itemsets.size();
  }

  /**
   * Compactifies the set of itemsets. Decreases the capacity of
   * the set so that it matches the number of itemsets in the set.
   */
  public final void compactify() {

    itemsets.trimToSize();
  }

  /**
   * Removes all itemsets from the set.
   */
  public final void delete() {

    itemsets = new Vector();
  }

  /** Function to remove an itemset at the given position.
   *
   * @param index 	The index of the itemset to be deleted.
   */
  public final void delete( int index )
  {
    itemsets.removeElementAt( index );
  }

  /**
   * Checks if the given itemset is compatible
   * with this dataset. Only looks at the size of
   * the itemset and the ranges of the values for
   * nominal and string attributes.
   *
   * @param itemset the itemset
   * @return true if the itemset is compatible with the dataset
   */
  public final boolean checkInstance(Itemset itemset) {

    if (itemset.numAttributes() != numAttributes()) {
      return false;
    }
    for (int i = 0; i < numAttributes(); i++) {
      if (itemset.isMissing(i)) {
        continue;
      } else if (getAttribute(i).isDiscret()) {
        if (!(M5StaticUtils.eq(itemset.getValue(i),(double) (int) itemset.getValue(i)))) {
          return false;
        } else if (M5StaticUtils.sm(itemset.getValue(i), 0) ||
                   M5StaticUtils.gr(itemset.getValue(i),getAttribute(i).numValues())) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Inserts an attribute at the given position (0 to
   * numAttributes()) and sets all values to be missing.
   * Shallow copies the attribute before it is inserted, and performs
   * a deep copy of the existing attribute information.
   *
   * @param att the attribute to be inserted
   * @param position the attribute's position
   * @exception IllegalArgumentException if the given index is out of range
   */
  public void insertAttributeAt(MyAttribute att, int position) {

    if ((position < 0) || (position > attributes.size())) {
      throw new IllegalArgumentException("Index out of range");
    }
    att = (MyAttribute) att.copy();
    //freshAttributeInfo();
    att.setIndex(position);
    attributes.insertElementAt(att, position);
    for (int i = position + 1; i < attributes.size(); i++) {
      MyAttribute current = (MyAttribute) attributes.elementAt(i);
      current.setIndex(current.index() + 1);
    }
    for (int i = 0; i < numItemsets(); i++) {
      itemset(i).insertAttributeAt(position);
    }
    if (classIndex >= position) {
      classIndex++;
    }
  }

  /**
   * Deletes an attribute at the given position
   * (0 to numAttributes() - 1). A deep copy of the attribute
   * information is performed before the attribute is deleted.
   *
   * @param position the attribute's position
   * @exception IllegalArgumentException if the given index is out of range or the
   * class attribute is being deleted
   */
  public void deleteAttributeAt(int position) {

    if ((position < 0) || (position >= attributes.size())) {
      throw new IllegalArgumentException("Index out of range");
    }
    if (position == classIndex) {
      throw new IllegalArgumentException("Can't delete class attribute");
    }
    //freshAttributeInfo();
    if (classIndex > position) {
      classIndex--;
    }
    attributes.removeElementAt(position);
    for (int i = position; i < attributes.size(); i++) {
      MyAttribute current = (MyAttribute) attributes.elementAt(i);
      current.setIndex(current.index() - 1);
    }
    for (int i = 0; i < numItemsets(); i++) {
      itemset(i).deleteAttributeAt(position);
    }
  }

  /** Function to remove all the attributes with missing value in the given attribute.
   *
   * @param attIndex		The index of the attribute.
   */
  public final void deleteWithMissing( int attIndex )
  {
    Vector newItemsets = new Vector( numItemsets() );

    for ( int i = 0; i < numItemsets(); i++ )
      if ( !itemset(i).isMissing( attIndex ) )
        newItemsets.addElement( itemset( i ) );

    itemsets = newItemsets;
  }

  /**
   * Removes all itemsets with missing values for a particular
   * attribute from the dataset.
   *
   * @param att the attribute
   */
  public final void deleteWithMissing(MyAttribute att) {

    deleteWithMissing(att.index());
  }

  /**
   * Removes all itemsets with a missing class value
   * from the dataset.
   * @throws Exception
   *
   * @exception UnassignedClassException if class is not set
   */
  public final void deleteWithMissingClass() throws Exception {

    if (classIndex < 0) {
      throw new Exception("Class index is negative (not set)!");
    }
    deleteWithMissing(classIndex);
  }

  /** Enumerates all the attributes.
   *
   * @return An enumeration that contains all the attributes.
   */
  public Enumeration enumerateAttributes()
  {
    Vector help = new Vector( attributes.size() - 1 );

    for ( int i = 0; i < attributes.size(); i++ )
      if ( i != classIndex )
        help.addElement( attributes.elementAt( i ) );

    return help.elements();
  }

  /** Enumerates all the itemsets.
   *
   * @return An enumeration that contains all the itemsets.
   */
  public final Enumeration enumerateItemsets()
  {
    return itemsets.elements();
  }

  /**
   * Stratifies a set of itemsets according to its class values
   * if the class attribute is nominal (so that afterwards a
   * stratified cross-validation can be performed).
   *
   * @param numFolds the number of folds in the cross-validation
   * @throws Exception
   * @exception UnassignedClassException if the class is not set
   */
  public final void stratify(int numFolds) throws Exception {

    if (numFolds <= 0) {
      throw new IllegalArgumentException(
          "Number of folds must be greater than 1");
    }
    if (classIndex < 0) {
      throw new Exception("Class index is negative (not set)!");
    }
    if (getClassAttribute().isDiscret()) {

      // sort by class
      int index = 1;
      while (index < numItemsets()) {
        Itemset itemset1 = itemset(index - 1);
        for (int j = index; j < numItemsets(); j++) {
          Itemset itemset2 = itemset(j);
          if ((itemset1.getClassValue() == itemset2.getClassValue()) ||
              (itemset1.classIsMissing() && itemset2.classIsMissing())) {
            swap(index, j);
            index++;
          }
        }
        index++;
      }
      stratStep(numFolds);
    }
  }

  /**
   * Help function needed for stratification of set.
   *
   * @param numFolds the number of folds for the stratification
   */
  private void stratStep(int numFolds) {

    Vector newVec = new Vector(itemsets.capacity());
    int start = 0, j;

    // create stratified batch
    while (newVec.size() < numItemsets()) {
      j = start;
      while (j < numItemsets()) {
        newVec.addElement(itemset(j));
        j = j + numFolds;
      }
      start++;
    }
    itemsets = newVec;
  }

  /**
   * Returns the first itemset in the set.
   *
   * @return the first itemset in the set
   */
  public final Itemset firstInstance() {

    return (Itemset) itemsets.firstElement();
  }

  /** Returns the itemset at the given position.
   *
   * @param index The index of the itemset.
   * @return the itemset at the given position.
   */
  public final Itemset itemset( int index )
  {
    return (Itemset)itemsets.elementAt( index );
  }

  /** Returns the last itemset.
   *
   * @return the last itemset
   */
  public final Itemset lastItemset()
  {
    return (Itemset)itemsets.lastElement();
  }

  /** Function to add  the itemsets of one set to the end of another.
   *
   * @param from The index of the first that is going to be copied.
   * @param dest The dataset where the itemsets are going to be copied.
   * @param num	The number of itemsets to copy.
   */
  private void copyItemsets( int from, MyDataset dest, int num )
  {
    for ( int i = 0; i < num; i++ )
      dest.addItemset( itemset( from + i ) );
  }

  /** Function to compute the sum of all the weights of the itemsets.
   *
   * @return	The weight of all the itemsets.
   */
  public final double sumOfWeights()
  {
    double sum = 0;

    for ( int i = 0; i < numItemsets(); i++ )
      sum += itemset( i ).getWeight();

    return sum;
  }

  /**
   * Checks if two headers are equivalent.
   *
   * @param dataset another dataset
   * @return true if the header of the given dataset is equivalent
   * to this header
   */
  public final boolean equalHeaders(MyDataset dataset) {

    // Check class and all attributes
    if (classIndex != dataset.classIndex) {
      return false;
    }
    if (attributes.size() != dataset.attributes.size()) {
      return false;
    }
    for (int i = 0; i < attributes.size(); i++) {
      if (!(getAttribute(i).equals(dataset.getAttribute(i)))) {
        return false;
      }
    }
    return true;
  }

  /** Function to sort the dataset based on an attribute.
   *
   * @param attIndex The index of the attribute.
   */
  public final void sort( int attIndex )
  {
    int i, j;

    // move all dataset with missing values to end
    j = numItemsets() - 1;
    i = 0;

    while ( i <= j )
    {
      if ( itemset( j ).isMissing( attIndex ) )
        j--;
      else
      {
        if ( itemset( i ).isMissing( attIndex ) )
        {
          swap( i, j );
          j--;
        }

        i++;
      }
    }

    quickSort( attIndex, 0, j );
  }

  /** Function to sort the dataset based on an attribute.
   *
   * @param att The attribute.
   */
  public final void sort( MyAttribute att )
  {
    sort(att.index());
  }

  /** Function to implementate the quicksort method.
   *
   * @param attIndex The index of the attribute used to sort the itemsets.
   * @param lo0	Minimum value.
   * @param hi0	Maximum value.
   */
  private void quickSort( int attIndex, int lo0, int hi0 )
  {
    int lo = lo0, hi = hi0;
    double mid, midPlus, midMinus;

    if ( hi0 > lo0 )
    {
      // Arbitrarily establishing partition element as the
      // midpoint of the array.
      mid = itemset( ( lo0 + hi0 ) / 2 ).getValue( attIndex );
  			midPlus = mid + 1e-6;
      midMinus = mid - 1e-6;

      // loop through the array until indices cross
      while( lo <= hi )
      {
        // find the first element that is greater than or equal to
        // the partition element starting from the left Index.
        while ( ( itemset( lo ).getValue( attIndex ) < midMinus ) && ( lo < hi0 ) )
          ++lo;

        // find an element that is smaller than or equal to
        // the partition element starting from the right Index.
        while ( ( itemset( hi ).getValue( attIndex )  > midPlus ) && ( hi > lo0 ) )
          --hi;

        // if the indexes have not crossed, swap
        if( lo <= hi )
        {
          swap( lo,hi );
          ++lo;
          --hi;
        }
      }

      // If the right index has not reached the left side of array
      // must now sort the left partition.
      if( lo0 < hi )
        quickSort( attIndex, lo0, hi );

        // If the left index has not reached the right side of array
        // must now sort the right partition.
      if( lo < hi0 )
        quickSort( attIndex, lo, hi0 );
    }
  }

  /** Function to swap two itemsets.
   *
   * @param i The first itemset.
   * @param j The second itemset.
   */
  private void swap( int i, int j )
  {
    Object help = itemsets.elementAt( i );

    itemsets.insertElementAt( itemsets.elementAt( j ), i );
    itemsets.removeElementAt( i + 1 );
    itemsets.insertElementAt( help, j );
    itemsets.removeElementAt( j + 1 );
  }

  /**
   * Creates the training set for one fold of a cross-validation
   * on the dataset.
   *
   * @param numFolds the number of folds in the cross-validation. Must
   * be greater than 1.
   * @param numFold 0 for the first fold, 1 for the second, ...
   * @return the training set as a set of weighted
   * itemsets
   * @exception IllegalArgumentException if the number of folds is less than 2
   * or greater than the number of itemsets.
   */
  public MyDataset trainCV(int numFolds, int numFold) {

      int numInstForFold, first, offset;
      MyDataset train;

      if (numFolds < 2) {
          throw new IllegalArgumentException(
                  "Number of folds must be at least 2!");
      }
      if (numFolds > numItemsets()) {
          throw new IllegalArgumentException(
                  "Can't have more folds than itemsets!");
      }
      numInstForFold = numItemsets() / numFolds;
      if (numFold < numItemsets() % numFolds) {
          numInstForFold++;
          offset = numFold;
      } else {
          offset = numItemsets() % numFolds;
      }
      train = new MyDataset(this, numItemsets() - numInstForFold);
      first = numFold * (numItemsets() / numFolds) + offset;
      copyItemsets(0, train, first);
      copyItemsets(first + numInstForFold, train, numItemsets() - first - numInstForFold);

      return train;
  }

  /**
   * Creates the test set for one fold of a cross-validation on
   * the dataset.
   *
   * @param numFolds the number of folds in the cross-validation. Must
   * be greater than 1.
   * @param numFold 0 for the first fold, 1 for the second, ...
   * @return the test set as a set of weighted itemsets
   * @exception IllegalArgumentException if the number of folds is less than 2
   * or greater than the number of itemsets.
   */
  public MyDataset testCV(int numFolds, int numFold) {

    int numInstForFold, first, offset;
    MyDataset test;

    if (numFolds < 2) {
      throw new IllegalArgumentException(
          "Number of folds must be at least 2!");
    }
    if (numFolds > numItemsets()) {
      throw new IllegalArgumentException(
          "Can't have more folds than itemsets!");
    }
    numInstForFold = numItemsets() / numFolds;
    if (numFold < numItemsets() % numFolds) {
      numInstForFold++;
      offset = numFold;
    } else {
      offset = numItemsets() % numFolds;
    }
    test = new MyDataset(this, numInstForFold);
    first = numFold * (numItemsets() / numFolds) + offset;
    copyItemsets(first, test, numInstForFold);
    return test;
  }


  /*******************NEW METHODS*************************************/

  /**
   * It filters the instances covered by a simple rule from this dataset;
   * i.e., it deactivates the instances not covered by that rule.
   * @param mask Mask the mask with the active entries of the dataset
   * @param A int attribute's id
   * @param V double attribute's value
   * @param operator int rule operator. It could be: Rule.EQUAL(for discret attributes),
   * Rule.GREATER (>) or Rule.LOWER(<=)
   */
  public void filter(Mask mask,int A,double V,int operator){
    mask.resetIndex();
    while(mask.next()){
      //if (X[mask.getIndex()][A]!=V)
      if (((Itemset)itemsets.elementAt(mask.getIndex())).isMissing(A)){
        mask.reset();
      }
      else{
        if (operator == Rule.EQUAL &&
            ( (Itemset) itemsets.elementAt(mask.getIndex())).getValue(A) != V)
          mask.reset();
        if (operator == Rule.GREATER &&
            ( (Itemset) itemsets.elementAt(mask.getIndex())).getValue(A) <= V)
          mask.reset();
        if (operator == Rule.LOWER &&
            ( (Itemset) itemsets.elementAt(mask.getIndex())).getValue(A) > V)
          mask.reset();
      }
    }
  }

  /**
   * It filters the instances covered by a simple rule from this dataset;
   * i.e., it deactivates the instances not covered by that rule.
   * @param mask Mask the mask with the actives entries of the dataset
   * @param sr SimpleRule the rule
   */
  public void filter(Mask mask,SimpleRule sr){
    int A=sr.getAttribute();
    double V=sr.getValue();
    int operator=sr.getOperator();
    mask.resetIndex();
    while(mask.next()){
      //if (X[mask.getIndex()][sr.getAttribute()]!=sr.getValue())
      if (((Itemset)itemsets.elementAt(mask.getIndex())).isMissing(A)){
        mask.reset();
      }
      else{
        if (operator == Rule.EQUAL && ( (Itemset) itemsets.elementAt(mask.getIndex())).getValue(A) != V)
          mask.reset();
        if (operator == Rule.GREATER && ( (Itemset) itemsets.elementAt(mask.getIndex())).getValue(A) <= V)
          mask.reset();
        if (operator == Rule.LOWER && ( (Itemset) itemsets.elementAt(mask.getIndex())).getValue(A) > V)
          mask.reset();
      }
    }
  }

  /**
   * It filters the instances covered by a rule from this dataset;
   * i.e., it deactivates the instances not covered by that rule.
   * @param mask Mask the mask with the active entries of the dataset
   * @param rule Rule the rule
   */
  public void filter(Mask mask,Rule rule){
    for (int i=0;i<rule.size();i++)
      this.filter(mask, rule.getSimpleRule(i));
  }

  /**
   * It filters the instances covered by a set of rule from this dataset;
   * i.e., it deactivates the instances not covered by that ruleset.
   * @param mask Mask the mask with the active entries of the dataset
   * @param rules Ruleset the ruleset
   */
  public void filter(Mask mask,Ruleset rules){
    Mask previous=new Mask(this.itemsets.size(),false);
    for (int i=0;i<rules.size();i++){
      Mask current=mask.copy();
      filter(current,rules.getRule(i));
      previous=previous.or(current);
    }
    previous.copyTo(mask);
  }

  /**
   * It filters the instances covered by a set of rule from this dataset;
   * i.e., it deactivates the instances not covered by that ruleset.
   * @param mask Mask the mask with the active entries of the dataset
   * @param rules Ruleset the ruleset
   * @param ignore int the algorithm ignores the i-th rule of the ruleset
   */
  public void filter(Mask mask,Ruleset rules,int ignore){
    Mask previous=new Mask(this.itemsets.size(),false);
    for (int i=0;i<rules.size();i++){
      if (i!=ignore){
        Mask current = mask.copy();
        filter(current, rules.getRule(i));
        previous = previous.or(current);
      }
    }
    previous.copyTo(mask);
  }

  /**
   * It filters the instances of a given class from this dataset;
   * i.e., it deactivates the instances from the other class.
   * @param mask Mask the mask whit the active entries of the dataset
   * @param class_name String the name of the class
   */
  public void filterByClass(Mask mask,String class_name){
    double class_id=this.getAttribute(this.classIndex).valueIndex(class_name);
    mask.resetIndex();
    while (mask.next()) {
      //if (!output[mask.getIndex()].equals(value))
      if (((Itemset) itemsets.elementAt(mask.getIndex())).getClassValue()!=class_id)
        mask.reset();
    }
  }

  /**
   * It substracts the instances covered by a simple rule from this dataset;
   * i.e., it deactivates the instances covered by that rule.
   * @param mask Mask the mask with the active entries of the dataset
   * @param A int attribute's id
   * @param V double attribute's value
   * @param operator int rule operator. It could be: Rule.EQUAL(for discret attributes),
   * Rule.GREATER (>) or Rule.LOWER(<=)
   */
  public void substract(Mask mask,int A,double V,int operator){
    mask.resetIndex();
    while(mask.next()){
      //if (X[mask.getIndex()][A]==V)
      if (!((Itemset)itemsets.elementAt(mask.getIndex())).isMissing(A)){
        if (operator == Rule.EQUAL &&
            ( (Itemset) itemsets.elementAt(mask.getIndex())).getValue(A) == V)
          mask.reset();
        if (operator == Rule.GREATER &&
            ( (Itemset) itemsets.elementAt(mask.getIndex())).getValue(A) > V)
          mask.reset();
        if (operator == Rule.LOWER &&
            ( (Itemset) itemsets.elementAt(mask.getIndex())).getValue(A) <= V)
          mask.reset();
      }
    }
  }

  /**
   * It substracts the instances covered by a simple rule from this dataset;
   * i.e., it deactivates the instances covered by that rule.
   * @param mask Mask the mask with the active entries of the dataset
   * @param sr SimpleRule the rule
   */
  public void substract(Mask mask,SimpleRule sr){
    mask.resetIndex();
    int A=sr.getAttribute();
    double V=sr.getValue();
    int operator=sr.getOperator();
    while(mask.next()){
      //if (X[mask.getIndex()][sr.getAttribute()]==sr.getValue())
      if (!((Itemset)itemsets.elementAt(mask.getIndex())).isMissing(A)){
        if (operator == Rule.EQUAL && ( (Itemset) itemsets.elementAt(mask.getIndex())).getValue(A) == V)
          mask.reset();
        if (operator == Rule.GREATER && ( (Itemset) itemsets.elementAt(mask.getIndex())).getValue(A) > V)
          mask.reset();
        if (operator == Rule.LOWER && ( (Itemset) itemsets.elementAt(mask.getIndex())).getValue(A) <= V)
          mask.reset();
      }
    }
  }

  /**
   * It substracts the instances covered by a rule from this dataset;
   * i.e., it deactivates the instances covered by that rule.
   * @param mask Mask the mask with the active entries of the dataset
   * @param rule Rule the rule
   */
  public void substract(Mask mask,Rule rule){
    mask.resetIndex();
    while(mask.next()){
      boolean seguir=true;
      for (int i = 0; i < rule.size() && seguir; i++){
        int A=rule.getSimpleRule(i).getAttribute();
        double V=rule.getSimpleRule(i).getValue();
        int operator=rule.getSimpleRule(i).getOperator();
        if (((Itemset)itemsets.elementAt(mask.getIndex())).isMissing(A)){
          seguir=false;
        }
        else{
          if (operator == Rule.EQUAL)
            seguir = ( ( (Itemset) itemsets.elementAt(mask.getIndex())).getValue(A) == V);
          if (operator == Rule.GREATER)
            seguir = ( ( (Itemset) itemsets.elementAt(mask.getIndex())).getValue(A) > V);
          if (operator == Rule.LOWER)
            seguir = ( ( (Itemset) itemsets.elementAt(mask.getIndex())).getValue(A) <= V);
        }
      }
      if (seguir)
        mask.reset();
    }
  }

  /**
   * It substracts the instances covered by a set of rule from this dataset;
   * i.e., it deactivates the instances covered by that ruleset.
   * @param mask Mask the mask with the active entries of the dataset
   * @param rules Ruleset the set of rules
   */
  public void substract(Mask mask,Ruleset rules){
    for (int i=0;i<rules.size();i++){
      substract(mask,rules.getRule(i));
    }
  }

  /**
   * It substracts the instances covered by a set of rules from this dataset;
   * i.e., it deactivates the instances covered by that ruleset.
   * This method allows to ignore a rule of the set.
   * @param mask Mask the mask with the active entries of the dataset
   * @param rules Ruleset the set of rules
   * @param ignore int number of the rule to ignore
   */
  public void substract(Mask mask,Ruleset rules,int ignore){
    for (int i=0;i<rules.size();i++){
      if (i!=ignore)
        substract(mask,rules.getRule(i));
    }
  }

  /**
   * Computes the deviation of a rule from the predicted class values.
   * RMS=SQRT(Sum((Yi-yi)^2)/Nr)
   * Yi=actual class value of the itemset i
   * yi=predicted class value for the itemset i
   * Nr=covered instances
   *
   * @param r the rule
   * @return the deviation of a rule from the predicted class values.
   * @exception IllegalArgumentException if the class is not numeric
   */
  public final double ruleDeviation(Rule r) {

    double sumSquared = 0;
    Function function=r.getFunction();

    if (getClassAttribute().isDiscret()) {
      throw new IllegalArgumentException(
          "Can't compute rule deviation because class is " +
          "not numeric!");
    }
    Mask covered=new Mask(size());
    filter(covered,r);
    covered.resetIndex();
    while (covered.next()) {
      int i=covered.getIndex();
      if (!itemset(i).isMissing(classIndex)) {
        double deviation=itemset(i).getClassValue()-function.predict(itemset(i));
        sumSquared += (deviation * deviation);
      }
    }

    return Math.sqrt(sumSquared/((double)covered.getnActive()));
  }

  /**
   * Computes the mean absolute error of a rule for the predicted class values.
   * MAE/Cover=Sum(|Yi-yi|)/2Nr
   * Yi=actual class value of the itemset i
   * yi=predicted class value for the itemset i
   * Nr=covered instances
   *
   * @param r the rule
   * @return the mean absolute error of a rule for the predicted class values.
   * @exception IllegalArgumentException if the class is not numeric
   */
  public final double ruleMeanAbsoluteError(Rule r) {

    double sumAbs = 0;
    Function function=r.getFunction();

    if (getClassAttribute().isDiscret()) {
      throw new IllegalArgumentException(
          "Can't compute MAE because class is " +
          "not numeric!");
    }
    Mask covered=new Mask(size());
    filter(covered,r);
    covered.resetIndex();
    while (covered.next()) {
      int i=covered.getIndex();
      if (!itemset(i).isMissing(classIndex)) {
        double deviation=itemset(i).getClassValue()-function.predict(itemset(i));
        sumAbs += Math.abs(deviation);
      }
    }

    return sumAbs/(2* (double)covered.getnActive());
  }

  /**
   * Computes the third heuristic exposed in [Holmes99].
   *                 _      _
   * CC*Cover=Nr*Sum((Yi-Y)(yi-y))/(Nr*std(Y)*std(y))
   *
   * Yi=actual class value of the itemset i
   * yi=predicted class value for the itemset i
   * Nr=covered instances (obvied->Nr/Nr)
   * _ _
   * Y,y = means of its respective sets of values
   * std(Y),std(y) = standard deviation of its respective sets of values
   *
   * @param r the rule
   * @return the heuristic value.
   * @exception IllegalArgumentException if the class is not numeric
   */
  public final double ruleCorrelation(Rule r) {

    double sum = 0.0;
    Function function=r.getFunction();
    double mean=averageClassValue(r);
    double predicted_mean=averagePredictedClassValue(r);
    double std=classSTD(r);
    double predicted_std=classPredictedSTD(r);

    if (getClassAttribute().isDiscret()) {
      throw new IllegalArgumentException(
          "Can't compute correlation because attribute is " +
          "not numeric!");
    }
    Mask covered=new Mask(size());
    filter(covered,r);
    covered.resetIndex();
    while (covered.next()) {
      int i=covered.getIndex();
      if (!itemset(i).isMissing(classIndex)) {
        double Yi=itemset(i).getClassValue()-mean;
        double yi=function.predict(itemset(i))-predicted_mean;
        sum += (Yi*yi);
      }
    }
    if (std!=0.0 && predicted_std!=0.0)
      return sum/(std*predicted_std);
    else
      return 0.0;
  }


  /**
   * Classifies the entries' classes according to several sets of rules.
   * @param actives Mask active entries of the dataset
   * @param rulesets Ruleset[] the rulesets
   * @param length int the number of rulesets
   * @return a vector of the length of this dataset with the class name for each entry.
   */
  public String[] classify(Mask actives,Ruleset[] rulesets,int length){
    String[] classification=new String[this.itemsets.size()];
    for(int i=0;i<classification.length;i++)
      if ( actives.isActive(i) )
        classification[i]=rulesets[length-1].getType();
    for (int i=0;i<length-1;i++){
      Mask filtered=actives.copy();
      this.filter(filtered,rulesets[i]);
      filtered.resetIndex();
      while(filtered.next()){
        int ind=filtered.getIndex();
        classification[ind]=rulesets[i].getType();
      }
      substract(actives,rulesets[i]);
    }
    return classification;
  }


  /**
   * It returns wether the value for an attribute in a given exemple is missing
   * @param exemple int index of the exemple in the dataset
   * @param attribute int index of the attribute
   * @return true if the value for an attribute in a given exemple is missing
   */
  public boolean isMissing(int exemple,int attribute){
    return ((Itemset) itemsets.elementAt(exemple)).isMissing(attribute);
  }

  /**
   * It returns wether the value for an attribute in a given exemple is missing
   * @param mask Mask the index of the mask signs the given exemple
   * @param attribute int index of the attribute
   * @return true if the value for an attribute in a given exemple is missing
   */
  public boolean isMissing(Mask mask,int attribute){
    return ((Itemset) itemsets.elementAt(mask.getIndex())).isMissing(attribute);
  }

  /**
   * It returns the number of exemple of the dataset
   * @return the number of exemple of the dataset
   */
  public int size(){
    return itemsets.size();
  }

  /**
   * Classifies the entries' classes according to several sets of rules.
   * @param rulesets Ruleset[] the rulesets
   * @param length int the number of rulesets
   * @return a vector of the length of this dataset with the class name for each entry.
   */
  public String[] classify(Ruleset[] rulesets, int length){
    return classify(new Mask(itemsets.size()),rulesets,length);
  }

  /**
   * Classifies the entries' classes according to several rules.
   * The last of wich is a default rule.
   * @param actives Mask active entries of the dataset
   * @param rules Vector the rules vector
   * @return a vector of the length of this dataset with the class name for each entry.
   */
  public double[] classify(Mask actives,Vector rules){
    double[] classification=new double[this.itemsets.size()];
    Function default_function=((Rule)rules.lastElement()).getFunction();
    for(int i=0;i<classification.length;i++)
      if ( actives.isActive(i) )
        classification[i]=default_function.predict(itemset(i));
    for (int i=0;i<rules.size()-1;i++){
      Mask filtered=actives.copy();
      this.filter(filtered,(Rule)rules.elementAt(i));
      filtered.resetIndex();
      Function function=((Rule)rules.elementAt(i)).getFunction();
      while(filtered.next()){
        int ind=filtered.getIndex();
        classification[ind]=function.predict(itemset(ind));
      }
      substract(actives,(Rule)rules.elementAt(i));
    }

    return classification;
  }

  /**
   * Classifies the entries' classes according to several rules.
   * The last of wich is a default rule.
   * @param rules Vector the rules vector
   * @return a vector of the length of this dataset with the class name for each entry.
   */
  public double[] classify(Vector rules){
    return classify(new Mask(size()),rules);
  }

  /**
   * Output a specific example
   * @param pos int position (id) of the example in the data-set
   * @return double[] the attributes of the given example
   */
  public double[] getExample(int pos) {
    return ((Itemset) this.itemsets.elementAt(pos)).values;
  }

  /**
   * Output a specific example
   * @param mask Mask with the position (id) of the example in the data-set
   * @return double[] the attributes of the given example
   */
  public double[] getExample(Mask mask) {
    return ((Itemset) this.itemsets.elementAt(mask.getIndex())).values;
  }

  /**
   * Returns the frequency (number of instances) of each class.
   * @return the frequency (number of instances) of each class.
   */
  public int[] getClassFequency(){
    int[] frequency = new int[this.numClasses()];
    for (int i=0;i<frequency.length;i++) frequency[i]=0;
    for (int i=0;i<this.size();i++) {
      double class_value=((Itemset)itemsets.elementAt(i)).getValue(this.getClassIndex());
      frequency[(int)class_value]++;
    }
    return frequency;
  }

  /**
   * Returns the frequency (number of instances) of each class.
   * @param filter Mask filter
   * @return the frequency (number of instances) of each class.
   */
  public int[] getClassFequency(Mask filter){
    int[] frequency = new int[this.numClasses()];
    for (int i=0;i<frequency.length;i++) frequency[i]=0;
    filter.resetIndex();
    while (filter.next()) {
      double class_value=((Itemset)itemsets.elementAt(filter.getIndex())).getValue(this.getClassIndex());
      frequency[(int)class_value]++;
    }
    return frequency;
  }

  /**
   * It split phisically the itemsets into two subdatasets,
   * according to the coverage of a rule.
   * @param r the rule
   * @return two dataset, the first one (0) with the exemples covered by the rule,
   * the second one (1) with the rest of them (the uncoverd exemples).
   */
  public MyDataset[] split(Rule r){
    MyDataset[] split=new MyDataset[2];
    Mask rule_filter=new Mask(numItemsets());
    filter(rule_filter,r);
    split[0]=new MyDataset(this,rule_filter.getnActive()); //Covered exemples
    split[1]=new MyDataset(this,numItemsets()-split[0].numItemsets()); //Uncovered exemple
    for (int i=0;i<numItemsets();i++){
      if (rule_filter.isActive(i)){
        //Covered exemple
        split[0].addItemset(itemset(i));
      }
      else{
        //Uncovered exemple
        split[1].addItemset(itemset(i));
      }
    }
    return split;
  }

  /**
   * Returns the average class value of the set.
   * @return the average class value of the set.
   */
  public double averageClassValue(){
    double sum=0.0;

    for (int i=0;i<numItemsets();i++){
      sum+= itemset(i).getClassValue();
    }

    return sum/((double)numItemsets());
  }

  /**
   * Returns the average class value of the instances covered by a rule.
   * @param r the rule
   * @return the average class value of the instances covered by a rule.
   */
  public double averageClassValue(Rule r){
    double sum=0.0;

    Mask covered=new Mask(size());
    filter(covered,r);
    covered.resetIndex();
    while (covered.next()){
      sum+=itemset(covered.getIndex()).getClassValue();
    }

    return sum/((double)covered.getnActive());
  }

  /**
   * Returns the average predicted class value of the instances covered by a rule.
   * @param r the rule
   * @return the average predicted class value of the instances covered by a rule.
   */
  public double averagePredictedClassValue(Rule r){
    double sum=0.0;
    Function function=r.getFunction();

    Mask covered=new Mask(size());
    filter(covered,r);
    covered.resetIndex();
    while (covered.next()){
      sum+=function.predict(itemset(covered.getIndex()));
    }

    return sum/((double)covered.getnActive());
  }

  /**
   * Returns the average value for a given attribute of the set.
   * @param att the attribute's index
   * @return the average value for a given attribute of the set.
   */
  public double averageValue(int att){
    double sum=0.0;

    for (int i=0;i<numItemsets();i++){
      sum+=((Itemset) itemsets.elementAt(i)).getValue(att);
    }

    return sum/((double)numItemsets());
  }

  /**
   * Transforms the discret attribute into numValues()-1 synthetic binary attributes.
   * @return a copy of this dataset with the aforementioned changes.
   */
  public MyDataset discretToBinary(){
    int[][] indices=sortByAverageClassValues();

    MyDataset output=convertAttributes(indices);

    convertItemsets(output, indices);

    return output;
  }

  /**
   * Computes the average class values for each attribute and value,
   * and sort them by it.
   * @return the sorting of the values of each attribute,
   * according its average class value.
   */
  public int[][] sortByAverageClassValues(){
    int[][] indices = new int[numAttributes()][];
    double[][] avgClassValues = new double[numAttributes()][0];

    for (int j = 0; j < numAttributes(); j++) {
      MyAttribute att = getAttribute(j);
      if (att.isDiscret()) {
        avgClassValues[j] = new double[att.numValues()];
        double[] counts = new double[att.numValues()];
        for (int i = 0; i < numItemsets(); i++) {
          Itemset itemset = itemset(i);
          if (!itemset.classIsMissing() && (!itemset.isMissing(j))) {
            counts[(int) itemset.getValue(j)] += itemset.getWeight();
            avgClassValues[j][(int) itemset.getValue(j)] += itemset.getWeight() * itemset.getClassValue();
          }
        }
        double sum = M5StaticUtils.sum(avgClassValues[j]);
        double totalCounts = M5StaticUtils.sum(counts);
        if (M5StaticUtils.gr(totalCounts, 0)) {
          for (int k = 0; k < att.numValues(); k++) {
            if (M5StaticUtils.gr(counts[k], 0)) {
              avgClassValues[j][k] /= (double) counts[k];
            } else {
              avgClassValues[j][k] = sum / (double) totalCounts;
            }
          }
        }
        indices[j] = M5StaticUtils.sort(avgClassValues[j]);
      }
    }

    return indices;
  }

  /**
   * Convert the discret attribute into numValues()-1 synthetic binary attributes.
   * @param indices the output of getAverageClassValues.
   * @return the new Dataset with the binary attributes.
   */
  private MyDataset convertAttributes(int[][] indices){
    Vector newAtts;
    int newClassIndex;
    StringBuffer attributeName;
    Vector vals;

    // Compute new attributes
    newClassIndex = getClassIndex();
    newAtts = new Vector();
    for (int j = 0; j < numAttributes(); j++) {
      MyAttribute att = getAttribute(j);
      if ((!att.isDiscret()) || (j == getClassIndex())) {
        newAtts.addElement(att.copy());
      }
      else {
        if (j < getClassIndex()) {
          newClassIndex += att.numValues() - 2;
        }

        // Compute values for new attributes
        for (int k = 1; k < att.numValues(); k++) {
          attributeName = new StringBuffer(att.name() + "=");
          for (int l = k; l < att.numValues(); l++) {
            if (l > k) {
              attributeName.append(',');
            }
            attributeName.append(att.value(indices[j][l]));
          }
          MyAttribute newatt=new MyAttribute(attributeName.toString());
          newatt.enumerate();
          newAtts.addElement(newatt);

        }
      }
    }
    MyDataset outputFormat = new MyDataset(getName(),newAtts, 0);
    outputFormat.setClassIndex(newClassIndex);

    return outputFormat;
  }

  /**
   * Convert each itemset into an M5 compatible itemset
   * (the discret attributes have been previously converted into
   *  numValues()-1 synthetic binary attributes).
   * @param output the output of convertAttributes.
   * @param indices the output of getAverageClassValues().
   */
  private void convertItemsets(MyDataset output,int[][] indices){
    for(int i=0;i<numItemsets();i++){
      Itemset itemset=itemset(i);
      double[] vals = new double[output.numAttributes()];
      int attSoFar = 0;

      for (int j = 0; j < numAttributes(); j++) {
        MyAttribute att = getAttribute(j);
        if ( (!att.isDiscret()) || (j == getClassIndex())) {
          vals[attSoFar] = itemset.getValue(j);
          attSoFar++;
        }
        else {
          if (itemset.isMissing(j)) {
            for (int k = 0; k < att.numValues() - 1; k++) {
              vals[attSoFar + k] = itemset.getValue(j);
            }
          }
          else {
            int k = 0;
            while ( (int) itemset.getValue(j) != indices[j][k]) {
              vals[attSoFar + k] = 1;
              k++;
            }
            while (k < att.numValues() - 1) {
              vals[attSoFar + k] = 0;
              k++;
            }
          }
          attSoFar += att.numValues() - 1;
        }
      }
      Itemset inst = null;
      inst = new Itemset(itemset.getWeight(), vals);

      inst.setDataset( this );

      output.addItemset(inst);
    }

  }

  /**
   * It copies the header of the dataset
   * @return String A string containing all the data-set information
   */
  public String copyHeader() {
    String p = new String("");
    p = "@relation " + Attributes.getRelationName() + "\n";
    p += Attributes.getInputAttributesHeader();
    p += Attributes.getOutputAttributesHeader();
    p += Attributes.getInputHeader() + "\n";
    p += Attributes.getOutputHeader() + "\n";
    p += "@data\n";
    return p;
  }

  /**
   * Returns a string representation of the entries of this MyDataset.
   * @return a string representation of the entries of this MyDataset.
   */
  public String toString(){
    String salida="";
    for (int i=0;i<this.itemsets.size();i++){
      //First Attribute
      double V=((Itemset)itemsets.elementAt(i)).getValue(0);
      if (((MyAttribute) attributes.elementAt(0)).isDiscret())
        salida+=i+".- ("+Attributes.getAttribute(0).getNominalValue((int)V);
      else
        salida+=i+".- ("+V;
      //Attributes
      for (int j=1;j<this.numAttributes();j++){
        if (j!=this.getClassIndex()){
          V = ( (Itemset) itemsets.elementAt(i)).getValue(j);
          if ( ( (MyAttribute) attributes.elementAt(j)).isDiscret())
            salida += "," + Attributes.getAttribute(j).getNominalValue( (int) V);
          else
            salida += "," + V;
        }
      }
      //CLASS
      V=((Itemset)itemsets.elementAt(i)).getValue(this.getClassIndex());
      if (((MyAttribute) attributes.elementAt(this.getClassIndex())).isDiscret())
        salida+=")-> "+Attributes.getAttribute(this.getClassIndex()).getNominalValue((int)V)+"\n";
      else
        salida+=")-> "+V+"\n";
    }
    return salida;
  }

  /**
   * Returns a string representation of the active entries of this MyDataset.
   * @param mask Mask active entries
   * @return a string representation of the active entries of this MyDataset.
   */
  public String toString(Mask mask){
    String salida="";
    mask.resetIndex();
    while (mask.next()){
      int i=mask.getIndex();
      //First Attribute
      double V=((Itemset)itemsets.elementAt(i)).getValue(0);
      if (((MyAttribute) attributes.elementAt(0)).isDiscret())
        salida+=i+".- ("+Attributes.getAttribute(0).getNominalValue((int)V);
      else
        salida+=i+".- ("+V;
      //Attributes
      for (int j=1;j<this.numAttributes();j++){
        if (j!=this.getClassIndex()){
          V = ( (Itemset) itemsets.elementAt(i)).getValue(j);
          if ( ( (MyAttribute) attributes.elementAt(j)).isDiscret())
            salida += "," +
                Attributes.getAttribute(j).getNominalValue( (int) V);
          else
            salida += "," + V;
        }
      }
      //CLASS
      V=((Itemset)itemsets.elementAt(i)).getValue(this.getClassIndex());
      if (((MyAttribute) attributes.elementAt(this.getClassIndex())).isDiscret())
        salida+=")-> "+Attributes.getAttribute(this.getClassIndex()).getNominalValue((int)V)+"\n";
      else
        salida+=")-> "+V;
    }
    return salida;
  }

}
