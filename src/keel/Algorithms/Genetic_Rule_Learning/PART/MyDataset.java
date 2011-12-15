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

package keel.Algorithms.Genetic_Rule_Learning.PART;

import java.util.Enumeration;
import java.util.Vector;
import keel.Dataset.*;



/**
 * <p>
 * Class to implement the dataset
 * </p>
 * 
 * <p>
 * @author Written by Cristóbal Romero Morales (University of Oviedo)  01/07/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */
public class MyDataset{	
	
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

  /** Function to read the .dat file that contains the information of the dataset.
   *
   * @param name The reader object where the itemsets are readed.
   * @param train The flag if the file is for training
   */
  public MyDataset( String name, boolean train )
  {
    try {
      // create the set of instances
      IS = new InstanceSet();
      // Read the itemsets.
      IS.readSet(name,train);
    } catch (DatasetException e) {
      System.out.println("Error loading dataset instances");
      e.printStackTrace();
      System.exit(-1);
    } catch (HeaderFormatException e) {
      System.out.println("Error loading dataset instances");
      e.printStackTrace();
      System.exit(-1);
    }

    //Store Dataset file attributes
    readHeader();

    itemsets = new Vector( IS.getNumInstances() );

    // read all the itemsets
    getItemsetFull();


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
  private void readHeader( )
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

  /** Returns the index of the class attribute.
   * @return the index of the class attribute.
   */
  public final int getClassIndex()
  {
    return classIndex;
  }

  /** Returns the number of attributes.
   * @return the number of attributes.
   */
  public final int numAttributes()
  {
    return attributes.size();
  }

  /** Returns the number of possible values of the class attribute.
   * @return the number of possible values of the class attribute.
   */
  public final int numClasses()
  {
    if ( classIndex < 0 )
    {
      System.err.println("Class index wrong:"+classIndex);
      return -1;
    }
    return getClassAttribute().numValues();
  }

  /** Returns the number of itemsets.
   * @return the number of itemsets.
   */
  public final int numItemsets()
  {
    return itemsets.size();
  }

  /** Function to remove an itemset at the given position.
   *
   * @param index 	The index of the itemset to be deleted.
   */
  public final void delete( int index )
  {
    itemsets.removeElementAt( index );
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

  /** Function to add  the instances of one set to the end of another.
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
   * Classifies the entries' classes according to several sets of rules.
   * The last of wich is a default ruleset.
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
   * Classifies the entries' classes according to several rules.
   * The last of wich is a default rule.
   * @param actives Mask active entries of the dataset
   * @param rules Vector the rules vector
   * @return a vector of the length of this dataset with the class name for each entry.
   */
  public String[] classify(Mask actives,Vector rules){
    String[] classification=new String[this.itemsets.size()];
    for(int i=0;i<classification.length;i++)
      if ( actives.isActive(i) )
        classification[i]=((Rule)rules.lastElement()).getType();
    for (int i=0;i<rules.size()-1;i++){
      Mask filtered=actives.copy();
      this.filter(filtered,(Rule)rules.elementAt(i));
      filtered.resetIndex();
      while(filtered.next()){
        int ind=filtered.getIndex();
        classification[ind]=((Rule)rules.elementAt(i)).getType();
      }
      substract(actives,(Rule)rules.elementAt(i));
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
  public String[] classify(Ruleset[] rulesets,int length){
    return classify(new Mask(itemsets.size()),rulesets,length);
  }

  /**
   * Classifies the entries' classes according to several rules.
   * The last of wich is a default rule.
   * @param rules Vector the rules vector
   * @return a vector of the length of this dataset with the class name for each entry.
   */
  public String[] classify(Vector rules){
    return classify(new Mask(itemsets.size()),rules);
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
   * Returns the most frequent class.
   * @return the most frequent class.
   */
  public String getMostFrequentClass(){
    int[] frequency=getClassFequency();
    int best_class=-1,best_freq=-1;
    for (int i=0;i<numClasses();i++){
      if (frequency[i]>best_freq){
        best_class=i;
        best_freq=frequency[i];
      }
    }
    return getClassAttribute().value(best_class);
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
   * Returns the class entropy of this set.
   * @return the class entropy of this set.
   */
  public double getEntropy(){
    int[] frequency = getClassFequency();
    double entropy=0.0;
    for (int i=0;i<numClasses();i++){
      entropy+=Utilities.logFunc((double)frequency[i]);
    }
    return (Utilities.logFunc(numItemsets())-entropy)/numItemsets();
  }

  /**
   * Returns the class entropy of this set.
   * @param filter Mask only active entries.
   * @return the class entropy of this set.
   */
  public double getEntropy(Mask filter){
    int[] frequency = getClassFequency(filter);
    double entropy=0.0;
    for (int i=0;i<numClasses();i++){
      entropy+=Utilities.logFunc((double)frequency[i]);
    }
    return (Utilities.logFunc(filter.getnActive())-entropy)/filter.getnActive();
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
        salida+=i+".- ("+getAttribute(0).value((int)V);
      else
        salida+=i+".- ("+V;
      //Attributes
      for (int j=1;j<this.numAttributes();j++){
        if (j!=this.getClassIndex()){
          V = ( (Itemset) itemsets.elementAt(i)).getValue(j);
          if ( ( (MyAttribute) attributes.elementAt(j)).isDiscret())
            salida += "," + getAttribute(j).value( (int) V);
          else
            salida += "," + V;
        }
      }
      //CLASS
      V=((Itemset)itemsets.elementAt(i)).getValue(this.getClassIndex());
      if (((MyAttribute) attributes.elementAt(this.getClassIndex())).isDiscret())
        salida+=")-> "+getClassAttribute().value((int)V)+"\n";
      else
        salida+=")-> "+V;
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
        salida+=i+".- ("+getAttribute(0).value((int)V);
      else
        salida+=i+".- ("+V;
      //Attributes
      for (int j=1;j<this.numAttributes();j++){
        if (j!=this.getClassIndex()){
          V = ( (Itemset) itemsets.elementAt(i)).getValue(j);
          if ( ( (MyAttribute) attributes.elementAt(j)).isDiscret())
            salida += "," +
                getAttribute(j).value( (int) V);
          else
            salida += "," + V;
        }
      }
      //CLASS
      V=((Itemset)itemsets.elementAt(i)).getValue(this.getClassIndex());
      if (((MyAttribute) attributes.elementAt(this.getClassIndex())).isDiscret())
        salida+=")-> "+getClassAttribute().value((int)V)+"\n";
      else
        salida+=")-> "+V;
    }
    return salida;
  }

  /**
   * Returns a string representation of the active entries of this MyDataset.
   * @param mask IncrementalMask active entries
   * @return a string representation of the active entries of this MyDataset.
   */
  public String toString(IncrementalMask mask){
    String salida="";
    mask.resetIndex();
    while (mask.next()){
      int i=mask.getIndex();
      //First Attribute
      double V=((Itemset)itemsets.elementAt(i)).getValue(0);
      if (((MyAttribute) attributes.elementAt(0)).isDiscret())
        salida+=i+".- ("+getAttribute(0).value((int)V);
      else
        salida+=i+".- ("+V;
        //Attributes
      for (int j=1;j<this.numAttributes();j++){
        if (j!=this.getClassIndex()){
          V = ( (Itemset) itemsets.elementAt(i)).getValue(j);
          if ( ( (MyAttribute) attributes.elementAt(j)).isDiscret())
            salida += "," + getAttribute(j).value( (int) V);
          else
            salida += "," + V;
        }
      }
      //CLASS
      V=((Itemset)itemsets.elementAt(i)).getValue(this.getClassIndex());
      if (((MyAttribute) attributes.elementAt(this.getClassIndex())).isDiscret())
        salida+=")-> "+getClassAttribute().value((int)V)+"\n";
      else
        salida+=")-> "+V;
    }
    return salida;
  }

}
