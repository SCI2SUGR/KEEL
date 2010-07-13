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

package keel.Algorithms.Rule_Learning.Slipper;

import keel.Dataset.*;


public class Trio {
/** 
 * <p>
 * <i>Auxiliar class.<\i>
 * It counts the number of instances (positive and negative) that contains a given value.
 * of a given attribute in a dataset.
 * </p>
 */
	
  //Atribute's value.
  private double clave;
  //sum of the weights of the positive instances of a given set that contains the value.
  private double positivos;
  //sum of the weights of the negative instances of a given set that contains the value.
  private double negativos;
  //static constants
  static int POSITIVE=0;

  static int NEGATIVE=1;

  /**
   * Default constructor.
   */
  public Trio() {
  }

  /**
   * Constructs a Trio with the given value and 0 instaces for both positives and negatives.
   *
   * @param clave the attribute's value.
   */
  public Trio(double clave) {
    this.clave=clave;
    positivos=0;
    negativos=0;
  }

  /**
   * Constructs a Trio with the given value and 0 instaces for both positives and negatives.
   *
   * @param clave the attribute's value.
   * @param value initial value
   * @param sign whether value goes to positives or to negatives
   */
  public Trio(double clave,double value,int sign) {
    this.clave=clave;
    if (sign==POSITIVE)
      this.positivos=value;
    else if (sign==NEGATIVE)
      this.negativos=value;
  }

  /**
   * Returns the attribute's value.
   *
   * @return the attribute's value.
   */
  public double getKey(){
    return clave;
  }

  /**
   * Returns the number of positive instances of a given dataset that contains the value.
   *
   * @return the number of positive instances.
   */
  public double getPositive(){
    return positivos;
  }

  /**
   * Returns the number of negative instances of a given dataset that contains the value.
   *
   * @return the number of negative instances.
   */
  public double getNegative(){
    return negativos;
  }

  /**
   * Sets the attribute'value to a new value.
   *
   * @param clave the new attribute's value.
   */
  public void setKey(double clave){
    this.clave=clave;
  }

  /**
   * Increases the number of positive instances of a given dataset that contains the value.
   * @param value the adding value
   */
  public void addPositive(double value){
    positivos+=value;
  }

  /**
   * Increases the number of negative instances of a given dataset that contains the value.
   * @param value the adding value
   */
  public void addNegative(double value){
    negativos+=value;
  }

  /**
   * Returns a string that represent a Trio.
   *
   * @return a string that represent a Trio.
   */
  public String toString(){
    String outcome=""+clave+" p:"+positivos+" n:"+negativos;
    return outcome;
  }

  /**
   * Returns a string that represent a Trio, taking into account the given attribute's id.
   * @param i int the attribute's id.
   * @return a string that represent a Trio.
   */
  public String toString(int i){
    String outcome=""+Attributes.getAttribute(i).getNominalValue((int) clave)+" p:"+positivos+" n:"+negativos;

    return outcome;
  }

}
