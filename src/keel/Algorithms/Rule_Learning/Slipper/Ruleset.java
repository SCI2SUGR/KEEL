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

import java.util.Vector;


public class Ruleset {
/**
 * <p>
 * Representation of a disjuction of rules with a common consecuent.
 * It may be represented as: <b>if (rule1 || rule2) then output=consecuent<\b>
 * Besides each rule has a positive value (confidence) associated. The entire ruleset
 * has also a default rule whose confidence value must be always negative.
 * </p>
 */
	
  //set of rules
  private Vector rules; 
  //class (consecuent)
  private String type; 
  //Confidence of the default rule
  private double defaultCr=0.0; 

  /**
   *
   * Constructs an empty ruleset.
   */
  public Ruleset() {
    rules=new Vector();
    defaultCr=0.0;
  }

  /**
   * It returns wether a rule belongs to the ruleset
   * @param r Rule the simple rule
   * @return true if the rule is part of the ruleset
   */
  public boolean contains(Rule r){
    boolean is_contained=false;
    for (int i=0;i<rules.size()&&!is_contained;i++)
      is_contained=r.isEqual((Rule) rules.elementAt(i));
    return is_contained;
  }

  /**
   * It returns the position of a rule (if it is belongs to the ruleset)
   * @param r Rule the simple rule
   * @return true if the rule is part of the ruleset
   */
  public int indexOf(Rule r){
    boolean is_contained=false;
    int i=0;
    for (i=0;i<rules.size()&&!is_contained;i++)
      is_contained=r.isEqual((Rule) rules.elementAt(i));
    if (is_contained)
      return i-1;
    else
      return -1;
  }


  /**
   * Adds a new rule to the ruleset.
   * If the rule is already in it, just adds its confidence.
   * @param r Rule the new rule
   */
  public void addRule(Rule r){
    int index=this.indexOf(r);
    if (index!=-1){
      Rule prev= (Rule) rules.get(index);
      double newCr=prev.getCr()+r.getCr();
      prev.setCr(newCr);
    }
    else
      rules.add(r);
  }

   /**
    * Adds a given value to the confidence of the default rule.
    * @param Cr the value
    */
   public void addToDefaultCr(double Cr){
     this.defaultCr+=Cr;
   }

   /**
    * Computes the confidence of the default rule, according to the equation 4
    * of [AAAI99]:
    * Cr=1/2ln((W+ + 1/(2n))/(W_ + 1/(2n)))
    * W+: sum of the weights of the positive instances that are covered by the current rule
    * W_: sum of the weights of the negative instances that are covered by the current rule
    * n: |p|+|n|
    * @param data MyDataset the dataset
    * @param positives Mask the positive entries
    * @param negatives Mask the negative entries
    * @param distribution double[] the distribution D
    * @return the confidence of the default rule
    */
   public double getDefaultCr(MyDataset data, Mask positives, Mask negatives, double[] distribution){
     double w_plus=Rule.getDefaultW(data,positives,distribution);
     double w_minus=Rule.getDefaultW(data,negatives,distribution);
     int n=positives.getnActive()+negatives.getnActive();

     return 1.0/2.0*Math.log((w_plus+1.0/(2.0*n))/(w_minus+1.0/(2.0*n)));
   }

   /**
    * Returns the confidence of the default rule.
    * @return the confidence of the default rule.
    */
   public double getDefaultCr(){return defaultCr;}

  /**
   * Returns the rule in the i-th position of the ruleset.
   * @param pos int position of the rule in the ruleset
   * @return the rule in the pos-th position of the ruleset.
   */
  public Rule getRule(int pos){
    return (Rule) rules.elementAt(pos);
  }

  /**
   * Returns the common output (consecuent) of the rules in the ruleset.
   * @return the common output (consecuent) of the rules in the ruleset.
   */
  public String getType(){
    return type;
  }

  /**
   * Inserts a new rule in a given position of the ruleset.
   * @param r Rule the new rule
   * @param pos int the position where r must be inserted
   */
  public void insertRule(Rule r,int pos){
    rules.insertElementAt(r,pos);
  }

  /**
   * Deletes a given rule of the ruleset.
   * @param pos int position of the rule in the ruleset.
   */
  public void removeRule(int pos){
    rules.remove(pos);
  }

  /**
   * Sets the common output (consecuent) of the rules in the ruleset.
   * @param type String the common output (consecuent) of the rules in the ruleset.
   */
  public void setType(String type){
    this.type=type;
  }

  /**
   * Returns the size (number of rules) of the ruleset.
   * @return the size (number of rules) of the ruleset.
   */
  public int size(){return rules.size();}

  /**
   * Returns a string representation of this Ruleset, containing the String representation of each Rule.
   * @return a string representation of this Ruleset, containing the String representation of each Rule.
   */
  public String toString(){
    String output="";
    for (int i=0;i<rules.size();i++)
      output+=((Rule) rules.elementAt(i)).toString()+" -> "+type+"\n";
    output+="Confianza regla por defecto: "+defaultCr;
    return output;
  }

}
