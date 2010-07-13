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
 * @author Written by Alberto Fernández (University of Granada)  01/07/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Rule_Learning.Ripper;

import java.util.Vector;


public class Score {
/**
 * Representation of a Trio's vector. It counts the number of instances (positive and negative) that contains each value
 * for a given attribute in a dataset. Each element is a Trio in the form: {value,positives,negatives}
 *
 */
	
  private Vector trios; //vector of {value,positives,negatives}

  //static constants
  static int POSITIVE=0;

  static int NEGATIVE=1;

  /**
   * Default constructor.
   */
  public Score() {
    trios=new Vector();
  }

  /**
     * Adds a new value to the vector.
     * The number of positive and negative instances are initialize to 0.
     *
     * @param key double the new value
     */
    public void addKey(double key){
      Trio t=new Trio(key);
      trios.add(t);
    }

    /**
     * Adds a new value to the vector.
     * The number of positive and negative instances are initialize to 0 or 1
     * wether the sign is POSITIVE or NEGATIVE.
     *
     * @param key double the new value
     * @param sign int it indicates wich of the measures will be intialized to 1 (the other will be initialized to 0)
     */
    public void addKey(double key,int sign){
      Trio t=new Trio(key);
      if (sign==POSITIVE)
        t.addPositive();
      else if (sign==NEGATIVE)
        t.addNegative();
      trios.add(t);
    }

    /**
     * Increases the number of positives instances in a given vector's position.
     *
     * @param pos int vector's position
     */
    public void addPositive(int pos){
      ((Trio)trios.elementAt(pos)).addPositive();
    }

    /**
     * Increases the number of positives instances of a given values.
     *
     * @param key double the value
     */
    public void addPositive(double key){
      int pos=findKey(key);
      ((Trio)trios.elementAt(pos)).addPositive();
    }

    /**
     * Increases the number of negatives instances in a given vector's position.
     *
     * @param pos int vector's position
     */
    public void addNegative(int pos){
      ((Trio)trios.elementAt(pos)).addNegative();
    }

    /**
     * Increases the number of negatives instances of a given values.
     *
     * @param key double the value
     */
    public void addNegative(double key){
      int pos=findKey(key);
      ((Trio)trios.elementAt(pos)).addNegative();
    }

    /**
     * Returns the vector's position of a given value
     *
     * @param key double the value
     * @return the value's position in the vector
     */
    public int findKey(double key){
      int i;
      for (i=0; i<trios.size() && ((Trio)trios.elementAt(i)).getKey()!=key;i++);
      if (i<trios.size())
        return i;
      else
        return -1;
    }

    /**
     * Returns the value at a given position of the vector
     * (inverse method of findKey).
     *
     * @param pos int position of the vector.
     * @return the value at that position of the vector.
     */
    public double getKey(int pos){
      return ((Trio)trios.elementAt(pos)).getKey();
    }

    /**
     * Returns the number of positive instances of the dataset
     * that contains the value at a given position of the vector.
     *
     * @param pos int position of the vector
     * @return number of positive instances.
     */
    public int getPositive(int pos){
      return ((Trio)trios.elementAt(pos)).getPositive();
    }

    /**
     * Returns the number of positive instances that contains the given value.
     *
     * @param key double the value
     * @return number of positive instances.
     */
    public int getPositive(double key){
      int pos=findKey(key);
      return ((Trio)trios.elementAt(pos)).getPositive();
    }

    /**
     * Returns the number of negative instances of the dataset
     * that contains the value at a given position of the vector.
     *
     * @param pos int position of the vector
     * @return number of negative instances.
     */
    public int getNegative(int pos){
      return ((Trio)trios.elementAt(pos)).getNegative();
    }

    /**
     * Returns the number of negative instances that contains the given value.
     *
     * @param key double the value
     * @return number of negative instances.
     */
    public int getNegative(double key){
      int pos=findKey(key);
      return ((Trio)trios.elementAt(pos)).getNegative();
    }

    /**
     * Returns the total number of instances of the dataset
     * that contains the value at a given position of the vector.
     *
     * @param pos int position of the vector
     * @return the total number of instances
     */
    public int getTotal(int pos){
      return ((Trio)trios.elementAt(pos)).getNegative()+((Trio)trios.elementAt(pos)).getPositive();
    }

    /**
     * Returns the total number of instances that contains the given value.
     *
     * @param key double the value
     * @return the total number of instances
     */
    public int getTotal(double key){
      int pos=findKey(key);
      return ((Trio)trios.elementAt(pos)).getNegative()+((Trio)trios.elementAt(pos)).getPositive();
    }

    public void sort(){
      if (trios.size()>1){
        Trio[] t = new Trio[trios.size()];
        trios.toArray(t);
        Utilities.mergeSort(t, trios.size());
        trios = new Vector(java.util.Arrays.asList(t));
      }
    }

    /**
     * Returns the size of the vector (the number of values).
     *
     * @return the size of the vector
     */
    public int size(){
      return trios.size();
    }

    /**
     * Returns a string representation of this Score, containing the String representation of each Trio.
     *
     * @return a string representation of this Score, containing the String representation of each Trio.
     */
    public String toString(){
      String outcome="";
      for (int i=0;i<trios.size();i++){
        outcome+=(Trio) trios.elementAt(i)+"\n";
      }
      return outcome;
    }

    /**
     * Returns a string representation of this Score, containing the String representation of each Trio, taking into account the given attribute's id.
     *
     * @param a int attribute's id
     * @return a string representation of this Score, containing the String representation of each Trio, taking into account the given attribute's id.
     */
    public String toString(int a){
      String outcome="";
      for (int i=0;i<trios.size();i++){
        outcome+=((Trio) trios.elementAt(i)).toString(a)+"\n";
      }
      return outcome;
    }

  }

