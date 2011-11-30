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

package keel.Algorithms.Associative_Classification.ClassifierCPAR;

/**
 * Class to store a Literal. It is almost the same as an Item, but it also stores the item's gain, which is calculated by the FOIL method.
 *
 * @author Jesus Alcalá (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */
public class Literal implements Comparable{
	
  int variable, value;
  double gain;

  /**
   * <p>
   * Default Constructor
   * </p>
   */
  public Literal() {
  }

  /**
   * <p>
   * Parameters Constructor
   * </p>
   * @param variable int Attribute of the literal
   * @param value int Associated value to an attribute
   */
  public Literal(int variable, int value) {
	  this.variable = variable;
	  this.value = value;
	  this.gain = 0.0;
  }

  /**
   * <p>
   * It sets the values for a literal
   * </p>
   * @param variable int Attribute of the literal
   * @param value int Associated value to an attribute
   */
  public void setValues (int variable, int value) {
	  this.variable = variable;
	  this.value = value;
  }

  /**
   * <p>
   * It sets the gain for a literal
   * </p>
   * @param value Gain to set
   */
  public void setGain (double value) {
	  this.gain = value;
  }

  /**
   * <p>
   * It returns the attribute stored in the literal
   * </p>
   * @return The attribute stored in the literal
   */
  public int getVariable () {
	  return (this.variable);
  }

  /**
   * <p>
   * It returns the value of the attribute stored in the literal
   * </p>
   * @return The value of the attribute attribute stored in the literal
   */
  public int getValue () {
	  return (this.value);
  }

  /**
   * <p>
   * It returns the gain of the literal
   * </p>
   * @return The gain of the literal
   */
  public double getGain () {
	  return (this.gain);
  }

  /**
   * <p>
   * Clone Function
   * </p>
   */
  public Literal clone(){
    Literal d = new Literal();
    d.variable = this.variable;
    d.value = this.value;
    d.gain = this.gain;

	return d;
  }

  /**
   * <p>
   * Function neccessary to sort literals
   * It sorts in a decreasing gain order
   * </p>
   */
  public int compareTo(Object a) {
    if ( ( (Literal) a).gain < this.gain) {
      return -1;
    }
    if ( ( (Literal) a).gain > this.gain) {
      return 1;
    }
    return 0;
  }
}

