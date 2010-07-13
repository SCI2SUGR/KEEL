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
 * @author Written by Antonio Alejandro Tortosa (University of Granada)  15/10/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */



package keel.Algorithms.Rule_Learning.PART;

import keel.Dataset.Attributes;


public class SimpleRule {
/**
 * <p>
 * Represent one single rule of the form: <b>exemple[a]==v</b>,
 * <b>exemple[a]>=v</b> or <b>exemple[a]<=v</b>.
 * a is one of the exemple's attributes and v is a legal value for a.
 * </p>
 */
	
  // operator >
  public static int GREATER=0; 
  //operator <=
  public static int LOWER=1; 
  //operator =
  public static int EQUAL=2; 
  //atribute's id
  private int attribute; 
  
  //Rule operator: >=, = or <=
  private int operator; 
  //attribute's nominal value or cut value (if continuos)
  private double value;  

  /**
   * Default constructor (the attribute's id and value are undefined)
   */
  public SimpleRule() {
  }

 /**
  * Constructs a SimpleRule with a given attribute and value
  * @param attribute int attribute's id (position of the attribute)
  * @param value double attribute's value
  * @param operator int rule operator: >, = ó <=
  */
  public SimpleRule(int attribute,double value,int operator) {
    this.attribute=attribute;
    this.value=value;
    this.operator=operator;
  }

 /**
  * Returns the attribute's id
  * @return attribute's id (position of the attribute)
  */
  public int getAttribute(){return attribute;}

  /**
   * Returns the value of the attribute
   * @return attribute's value
   */
  public double getValue(){return value;}

  /**
   * Returns the operator of the rule
   * @return rule operator
   */
  public int getOperator(){return operator;}

  /**
   * Returns true if the attribute is discret
   * @return true if the attribute is discret
   */
  public boolean isDiscret(){return operator==EQUAL;}

  /**
    * Returns true if the attribute is continous
    * @return true if the attribute is continous
    */
   public boolean isContinous(){return operator!=EQUAL;}

  /**
   * Sets the attribute's id and the attribute's value
   * @param attribute int attribute's id (position of the attribute)
   * @param value attribute's value
   * @param operator int rule operator: >, = ó <=
   */
  public void set(int attribute,double value,int operator){
    this.attribute=attribute;
    this.value=value;
    this.operator=operator;
  }

  /**
   * Sets the attribute's id and the attribute's value
   * @param attribute int attribute's id (position of the attribute)
   */
  public void setAttribute(int attribute){
    this.attribute=attribute;
  }

  /**
   * Sets the attribute's value
   * @param value attribute's value
   */
  public void setValue(double value){
    this.value=value;
  }

  /**
   * Sets the rule operator
   * @param operator int rule operator: >, = ó <=
   */
  public void setOperator(int operator){
    this.operator=operator;
  }

  /**
   * Return wether this simple rule is equal to another given simple rule
   * @param sr SimpleRule the given simple rule
   * @return true if this simple rule is equal to the given simple rule
   */
  public boolean isEqual(SimpleRule sr){
    return (this.attribute==sr.attribute && this.operator==sr.operator && this.value==sr.value);
  }

  /**
   * It returns a copy of this simple rule
   * @return a copy of this simple rule
   */
  public SimpleRule getCopy(){
    return new SimpleRule(attribute,value,operator);
  }

  /**
   * Returns a string representation of this SimpleRule
   * @return a string representation of this SimpleRule.
   */
  public String toString(){
    //return ""+Attributes.getAttribute(attribute).getName()+"="+Attributes.getAttribute(attribute).getNominalValue((int)value);
    String V="";
    V+=value;
    String operator_string="<undef>";
    if(operator==GREATER)
      operator_string=">";
    if(operator==LOWER)
      operator_string="<=";
    if(operator==EQUAL){
      operator_string = "=";
      V=Attributes.getInputAttribute(attribute).getNominalValue((int)value);
    }

    return ""+Attributes.getInputAttribute(attribute).getName()+operator_string+V;
  }

}
