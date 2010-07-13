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

import java.util.*;

/**
 * <p>Title: Regla</p>
 * <p>Description: It defines a Rule for the SIA algorithm</p>
 * <p>Company: KEEL</p>
 * @author Alberto Fernández (University of Granada) 23/02/2005
 * @since JDK1.5
 * @version 1.1
 */
public class Rule
    implements Comparable {

//Rule -> 'IF' cond1 ^ ...  ^ condn 'THEN' 'Class =' Ci, Strength

  private Vector conditionsList;
  private int clas;
  private double strength;
  private String[] attributeNames, classesNames;
  private String[][] valueNames;
  String className;
  private double[] minValues;

  /**
   * Default builder
   * @param nAtributos Number of antecedents of the rule
   */
  public Rule(int nAtributos) {
    super();
    clas = 0;
    strength = 0;
    conditionsList = new Vector(nAtributos);
  }

  /**
   * Rule Builder
   * @param nAtributos Number of antecedents of the rule
   * @param clas class in the consequent of the rule
   * @param fuerza Significance of the rule
   */
  public Rule(int nAtributos, int clas, double fuerza) {
    conditionsList = new Vector(nAtributos);
    this.clas = clas;
    this.strength = fuerza;
  }

  /**
   * It compares the rule with respect to their strength
   * @param o Another rule object
   * @return 0 if they have the same strength, 1 if the parameter rule is stronger, -1 in the contrary case
   */
  public int compareTo(Object o) {
    Rule r2 = (Rule) o;
    int sal = 0;

    if (strength == r2.getStrength()) {
      sal = 0;
    }
    else if (strength < r2.getStrength()) {
      sal = 1;
    }
    else if (strength > r2.getStrength()) {
      sal = -1;
    }
    return (sal);
  }

  /**
   * It prints the rule
   */
  public void print() {
    System.out.print("IF ");
    for (int i = 0; i < conditionsList.size() - 1; i++) {
      Condition c = (Condition) conditionsList.get(i);
      switch (c.getType()) {
        case 0:
          System.out.print("* AND ");
          break;
        case 1:
          //System.out.print(attributeNames[c.getAtributo()] + " = " + c.getValor() + " AND ");
          System.out.print(attributeNames[c.getAtributo()] + " = " +
                           valueNames[c.getAtributo()][ (int) (c.getValue() -
              minValues[c.getAtributo()])] + " AND ");
          break;
        case 2:
          System.out.print(c.getLowerBound() + " <= " +
                           attributeNames[c.getAtributo()] + " <= " +
                           c.getUpperBound() + " AND ");
          break;
      }
    }
    Condition c = (Condition) conditionsList.get(conditionsList.size() - 1);
    switch (c.getType()) {
      case 0:
        System.out.print("* ");
        break;
      case 1:
        //System.out.print(attributeNames[c.getAtributo()] + " = " + c.getValue());
        System.out.print(attributeNames[c.getAtributo()] + " = " +
                         valueNames[c.getAtributo()][ (int) (c.getValue() -
            minValues[c.getAtributo()])]);
        break;
      case 2:
        System.out.print(c.getLowerBound() + " <= " + attributeNames[c.getAtributo()] +
                         " <= " +
                         c.getUpperBound());
        break;
    }
    System.out.println(" THEN " + className + " = " +
                       classesNames[this.clas] + ", Strength: " +
                       this.strength);
  }

  /**
   * It prints the rule into a string
   * @return a string with the rule content
   */
  public String printString() {
    String cadena = "";
    boolean Rule = false;
    for (int i = 0; i < conditionsList.size()-1; i++) {
      Condition c = (Condition) conditionsList.get(i);
      if (c.getType() == 1) {
        if (Rule) {
          cadena += " AND ";
        }
        cadena += attributeNames[c.getAtributo()] + " = " +
            valueNames[c.
            getAtributo()][ (int) (c.getValue() - minValues[c.getAtributo()])];
        Rule = true;
      }
      else if (c.getType() == 2) {
        if (Rule) {
          cadena += " AND ";
        }
        cadena += attributeNames[c.getAtributo()] + " = [" + c.getLowerBound() +
            "," + c.getUpperBound() + "]";
        Rule = true;
      }
    }
    Condition c = (Condition) conditionsList.get(conditionsList.size() - 1);
    if (c.getType() == 1) {
      if (Rule) {
        cadena += " AND ";
      }
      cadena += attributeNames[c.getAtributo()] + " = " +
          valueNames[c.
          getAtributo()][ (int) (c.getValue() - minValues[c.getAtributo()])];
    }
    else if (c.getType() == 2) {
      if (Rule) {
        cadena += " AND ";
      }
      cadena += attributeNames[c.getAtributo()] + " = [" + c.getLowerBound() + "," +
          c.getUpperBound() + "]";
    }

    cadena += ": " + classesNames[this.clas] +
        ", Strength: " + this.strength + "\n";
    return cadena;
  }

  /**
   * It performs a copy of the rule
   * @return an exact copy of the antecedent of the rule
   */
  public Rule copyRule() {
    Rule r = new Rule(this.conditionsList.size(), this.clas, this.strength);
    for (int i = 0; i < this.conditionsList.size(); i++) {
      r.setCondition(this.getCondition(i));
    }
    return r;
  }

  /**
   * It assigns a new class for the rule
   * @param clas the new class
   */
  public void setClas(int clas) {
    this.clas = clas;
  }

  /**
   * It returns the consequent class of the rule
   * @return the consequent class of the rule
   */
  public int getClas() {
    return clas;
  }

  /**
   * It adds a new condition to the rule
   * @param c the condition
   */
  public void setCondition(Condition c) {
    conditionsList.add(c);
  }

  /**
   * It assigns a new strength to the rule
   * @param strength the new strength
   */
  public void setStrength(double strength) {
    this.strength = strength;
  }

  /**
   * It gets the strength of the rule
   * @return the strength of the rule
   */
  public double getStrength() {
    return this.strength;
  }

  /**
   * It returns a condition defined for the i-th attribute
   * @param i attribute id
   * @return the condition
   */
  public Condition getCondition(int i) {
    Condition aux = (Condition) conditionsList.get(i);
    Condition c = new Condition(aux.getAtributo());
    switch (aux.getType()) {
      case 0:
        c.setType(0);
        c.setValue(Double.NaN);
        break;
      case 1:
        c.setType(1);
        c.setValue(aux.getValue());
        break;
      case 2:
        c.setType(2);
        c.setLowerBound(aux.getLowerBound());
        c.setUpperBound(aux.getUpperBound());
    }
    return c;
  }

  /**
   * It checks if the rule is equal to another
   * @param r Rule for comparison
   * @return True if both rules are the same, false in other case
   */
  public boolean isEqual(Rule r) {
    if (this.clas != r.getClas()) {
      return false;
    }
    //if (this.getFuerza() != r.getFuerza())
    //return false;
    boolean igual = true;
    for (int i = 0; (i < this.conditionsList.size()) && (igual); i++) {
      igual = this.getCondition(i).isEqual(r.getCondition(i));
    }
    return igual;
  }

  /**
   * It stores the names of the input attributes
   * @param atributos String[] an Array that stores the name of each variable
   */
  public void addAttributeNames(String[] atributos) {
    attributeNames = new String[atributos.length - 1];
    for (int i = 0; i < atributos.length - 1; i++) {
      attributeNames[i] = atributos[i];
    }
  }

  /**
   * It stores the name for the class attribute
   * @param className String name of the class attribute
   */
  public void addClassName(String className) {
    this.className = className;
  }

  /**
   * It stores the names of the output attribute
   * @param classes String[] an Array that stores the name of the classes of the problem
   */
  public void addClassNames(String[] classes) {
    classesNames = new String[classes.length];
    for (int i = 0; i < classes.length; i++) {
      classesNames[i] = classes[i];
    }
  }

  /**
   * It stores the name for the different values in the data-set
   * @param valores String[][] the name for the different values in the data-set
   */
  public void addValuesNames(String[][] valores) {
    valueNames = valores;
  }

  /**
   * It stores the minimum values for each attribute
   * @param valores double[] the minimum values for each attribute
   */
  public void setMinValues(double[] valores) {
    this.minValues = valores;

  }

}

