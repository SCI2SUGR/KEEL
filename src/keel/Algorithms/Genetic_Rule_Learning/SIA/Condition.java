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

package keel.Algorithms.Genetic_Rule_Learning.SIA;

/**
 * <p>Title: Condition</p>
 * <p>Description: It represents a rule condition</p>
 * @author Written by Alberto Fernández (University of Granada) 05/13/2005
 * @version 1.0
 * @since JDK1.4
 */
public class Condition {

  /*
     Possible values:
     - * (<null> -> NAN value)
     - Ai = value
     - B <= Ai <= B
   */

  private int tipo; //0,1,2 (see above)
  private int atributo; //Attribute id
  private double valor; //this can be "Nan"
  private double b1, b2; //interval bounds

  /**
   * Default builder
   */
  public Condition() {
    tipo = 0;
    atributo = 0;
    valor = 0;
    b1 = 0;
    b2 = 0;
  }

  /**
   * Builder for the first condition type (*)
   * @param atributo attribute id
   */
  public Condition(int atributo){
    tipo = 0;
    this.atributo = atributo;
  }

  /**
   * Builder for the second condition type (enumerate attributes)
   * @param atributo attribute id
   * @param valor the value for the attribute
   */
  public Condition(int atributo,double valor){
    tipo = 1;
    this.atributo = atributo;
    this.valor = valor;
  }

  /**
   * Builder for the third type of condition (real or integer attributes)
   * @param atributo attribute id
   * @param valor the value for the attribute
   * @param b1 lower bound
   * @param b2 upper bound
   */
  public Condition(int atributo,double valor,double b1,double b2){
    tipo = 2;
    this.atributo = atributo;
    this.valor = valor;
    this.b1 = b1;
    this.b2 = b2;
  }

  /**
   * It gets the type of the condition
   * @return the type of the condition
   */
  public int getType(){
    return tipo;
  }

  /**
   * It returns the attribute id
   * @return the attribute id
   */
  public int getAtributo(){
    return atributo;
  }

  /**
   * It returns the value for the attribute
   * @return the value for the attribute
   */
  public double getValue(){
    return valor;
  }

  /**
   * It returns the lower bound
   * @return the lower bound
   */
  public double getLowerBound(){
    return b1;
  }

  /**
   * It returns the upper bound
   * @return the upper bound
   */
  public double getUpperBound(){
    return b2;
  }

  /**
   * It sets the type
   * @param tip Type (0 == *; 1 == [Ai = value]; 2 == [B <= Ai <= B] )
   */
  public void setType(int tip){
    tipo = tip;
  }

  /**
   * It adds the attribute id
   * @param att the attribute id
   */
  public void setAttribute(int att){
    atributo = att;
  }

  /**
   * It add the value (or a nan if *)
   * @param val Value of the condition ("Nan" for type 0; it doesn't exist for type 2)
   */
  public void setValue(double val){
    valor = val;
  }

  /**
   * Sets the lower bound
   * @param lim lower bound
   */
  public void setLowerBound(double lim){
    b1 = lim;
  }

  /**
   * Sets the upper bound
   * @param lim upper bound
   */
  public void setUpperBound(double lim){
    b2 = lim;
  }

  /**
   * It checks if the condition is equal to other
   * @param c condition for comparison
   * @return True if they are the same for all its antecedents. False in other case
   */
  public boolean isEqual(Condition c){
    if (c.getAtributo() != this.getAtributo()){
      return false;
    }
    if (this.getType() != c.getType()){
      return false;
    }
    if (this.getValue() != c.getValue()){
      return false;
    }
    if (this.getLowerBound() != c.getLowerBound()){
      return false;
    }
    if (this.getUpperBound() != c.getUpperBound()){
      return false;
    }
    return true;
  }

}

