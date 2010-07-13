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
 * <p>Title: Reglas</p>
 * <p>Description: It defines a Rule-set</p>
 * <p>Company: KEEL</p>
 * @author Alberto Fernández (University of Granada) 27/02/2005
 * @since JDK1.5
 * @version 1.2
 */
public class ruleSet {

/*  Rules:

   R1: 'IF' cond1 ^ ...  ^ condn 'THEN' 'Class =' Ci, Strength
   R2: ......
   RN: ....

      */

  private LinkedList ruleSet;

  /**
   * Default Builder
   */
  public ruleSet() {
    super();
    ruleSet = new LinkedList();
  }

  /**
   * It add a new rule to the set
   * @param regl the rule
   */
  public void addRule(Rule regl) {
    //ruleSet.add(regl.copiaRule());
    ruleSet.add(regl);
  }

  /**
   * It removes a rule in the i-th position
   * @param i the position to remove
   */
  public void deleteRule(int i) {
    ruleSet.remove(i);
  }

  /**
   * It returns the i-th rule
   * @param i rule position
   * @return the i-th rule
   */
  public Rule getRule(int i) {
    return  ((Rule) ruleSet.get(i)).copyRule();
  }

  /**
   * It returns the size of the rule set
   * @return the size of the rule set
   */
  public int size() {
    return (ruleSet.size());
  }

  /**
   * It returns the whole rule set
   * @return the whole rule set
   */
  public LinkedList getRuleSet() {
    return ruleSet;
  }

  /**
   * It copies the rule set into a new one
   * @return A new rule set which is an exact copy of the current one
   */
  public ruleSet copyRuleSet() {
    int i;
    ruleSet c = new ruleSet();

    for (i = 0; i < ruleSet.size(); i++) {
      Rule reg = (Rule) ruleSet.get(i);
      c.addRule(reg.copyRule());
    }
    return c;
  }

  /**
   * It prints all rules by screen
   */
  public void print() {
    int i;

    for (i = 0; i < ruleSet.size(); i++) {
      Rule r = (Rule) ruleSet.get(i);
      System.out.print("Rule_" + (i+1) + ": ");
      r.print();
    }
  }

  /**
   * It prints all rules into a string
   * @return a string containing all rules
   */
  public String printString() {
    int i;
    String cad = "";

    for (i = 0; i < ruleSet.size(); i++) {
      Rule r = (Rule) ruleSet.get(i);
      cad += (i+1) + ": ";
      cad += r.printString();
    }

    return cad;
  }

  /**
   * It returns the last rule (it is supossed to be the one with less strength)
   * @return the last rule
   */
  public Rule getLastRule() {
    return (Rule) ruleSet.getLast();
  }

  /**
   * Insertion operator
   * @param r Rule to insert
   * If r is in the rule set, it is not inserted again
   * If the size of the rule set is lower than 50, we insert the new rule
   * If the size of the rule set is higher than 50, we replace the last rule (less strength)
   */
  public void insertion(Rule r){
    boolean seguir = true;
    for (int i = 0; i < this.size() && seguir; i++){
      if (this.getRule(i).isEqual(r)){
        seguir = false;
      }
    }
    if (seguir){
      if (this.size() < 50){
        this.addRule(r);
      }
      else{
        Collections.sort(this.getRuleSet());
        this.deleteRule(49);
        this.addRule(r);
      }
    }
  }

}

