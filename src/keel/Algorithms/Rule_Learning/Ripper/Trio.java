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
 * @author Written by Alberto Fernández (University of Granada)  15/10/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.2
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Rule_Learning.Ripper;

import keel.Dataset.Attributes;


public class Trio implements Comparable{
/** <i>Auxiliar class.<\i>
 * It counts the number of instances (positive and negative) that contains a given value.
 * of a given attribute in a dataset.
 *
 */
	
  private double clave; //Atribute's value.
  private int positivos; //positive instances of a given set that contains the value.
  private int negativos; //negative instances of a given set that contains the value.

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
  public int getPositive(){
    return positivos;
  }

  /**
   * Returns the number of negative instances of a given dataset that contains the value.
   *
   * @return the number of negative instances.
   */
  public int getNegative(){
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
   */
  public void addPositive(){
    positivos++;
  }

  /**
   * Increases the number of negative instances of a given dataset that contains the value.
   */
  public void addNegative(){
    negativos++;
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
    String outcome=""+Attributes.getInputAttribute(i).getNominalValue((int) clave)+" p:"+positivos+" n:"+negativos;

    return outcome;
  }
  
  public int compareTo(Object o){
	  Trio t = (Trio)o;
	  
	  if(this.clave < t.clave)
		  return -1;
	  if(this.clave > t.clave)
		  return 1;
	  return 0;
  }
}
