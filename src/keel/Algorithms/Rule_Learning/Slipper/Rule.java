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
import keel.Dataset.Attributes;


public class Rule {
/**
 * <p>
 * Representation of a string of simple rules chained by 'and's: <b>exemple[a1][=|>|<=]v1 && exemple[a2][=|>=|<=]v2</b>
 * The rule has also a positive value (confidence) associated.
 * </p>
 */
  // operator >
  public static int GREATER=SimpleRule.GREATER; 
  //operator <=
  public static int LOWER=SimpleRule.LOWER; 
  //operator =
  public static int EQUAL=SimpleRule.EQUAL; 
  //string of simple rules
  private Vector chain; 
  //right side of the rule
  private String type; 
  //Confidence
  private double Cr; 

  /***************Private methods**********************/
  /**
   * It returns wether a simple rule is part of the rule
   * @param sr SimpleRule the simple rule
   * @return true if the simple rule is part of the rule
   */
  private boolean contains(SimpleRule sr){
    boolean is_contained=false;
    for (int i=0;i<chain.size()&&!is_contained;i++)
      is_contained=sr.isEqual((SimpleRule) chain.elementAt(i));
    return is_contained;
  }
  /***************Private methods**********************/


  /**
   * Constructs an empty rule.
   */
  public Rule() {
    chain=new Vector();
    type="";
    Cr=Double.NaN;
  }

  /**
   * Returns the number of the instances covered by the rule in a given dataset.
   * This method allows to ignore a simple rule from that rule.
   * @param data MyDataset the dataset
   * @param select Mask the mask with the active entries of the dataset
   * @param ignore int id of the single rule that it will be ignore in the applying of the rule
   * @return number of instances (from the active entries) covered by the rule
   */
  public int apply(MyDataset data,Mask select,int ignore){
    int output=0; //That variable will take the count of the covered entries
    select.resetIndex();
    while (select.next()){
      double[] exemple=data.getExample(select);
      boolean salir=false;
      for (int j=0;j<chain.size() && !salir;j++){
        if (data.isMissing(select, j) && j != ignore) {
          salir = true; //if any value is missing the whole comprobation for that entry fails
        }
        else if (j != ignore) {
          SimpleRule sr = (SimpleRule) chain.elementAt(j);
          int attribute = sr.getAttribute();
          double value = sr.getValue();
          if (sr.isDiscret()) {
            salir = ! ( (exemple[sr.getAttribute()] == sr.getValue()));
          }
          else {
            if (sr.getOperator() == SimpleRule.GREATER)
              salir = ! ( (exemple[sr.getAttribute()] > sr.getValue()));
            else
              salir = ! ( (exemple[sr.getAttribute()] <= sr.getValue()));
          } //end if (sr.isDiscret())
        } //end if (j!ignore)
      }//end for
      if (!salir) output++;
    }//end while
    return output;
  }

  /**
   * Returns the number of the instances covered by the rule in a given dataset
   * @param data MyDataset the dataset
   * @param select Mask the mask with the active entries of the dataset
   * @return number of instances (from the active entries) covered by the rule
   */
  public int apply (MyDataset data,Mask select){
    return apply(data,select,-1);
  }

  /**
   * It returns the number of the instances covered by the rule in a given dataset
   * @param data MyDataset the dataset
   * @return number of instances (from the active entries) covered by the rule
   */
  public int apply (MyDataset data){
    return apply(data,new Mask(data.size()),-1);
  }


  /**
   * It returns the number of true positives,true negatives,false positives and false negatives of the rule in a given dataset
   * @param data MyDataset the dataset
   * @param positives active positive instances of data
   * @param negatives active negative instances of data
   * @return number of true positives, false positives, true negatives and false negatives of the rule in the following order: {tp,tn,fp,fn}
   */
  public Stats apply (MyDataset data,Mask positives,Mask negatives){
    Stats stats=new Stats();
    stats.tp=apply(data,positives); //true positives
    stats.fn=positives.getnActive()-stats.tp; //false negatives
    stats.fp=apply(data,negatives); //false positives
    stats.tn=negatives.getnActive()-stats.fp; //true negatives
    return stats;
  }

  /**
   * Computes W+ or W- for the default rule,
   * according to the function W=sum(Di) i e R
   * @param data MyDataset the dataset
   * @param actives Mask the active entries (positives or negatives)
   * @param distribution double[] the distribution D of weights
   * @return W=sum(Di) i e R (W+ if actives are positives, W- if they are negatives)
   */
  public static double getDefaultW(MyDataset data, Mask actives, double[] distribution){
    double w=0.0;

    actives.resetIndex();
    while (actives.next()){
      w+=distribution[actives.getIndex()];
    }

    return w;
  }

  /**
   * Computes W+ or W- for this rule,
   * according to the function W=sum(Di) i e R
   * @param data MyDataset the dataset
   * @param actives Mask the active entries
   * @param distribution double[] the distribution D
   * @return W=sum(Di) i e R (W+ if actives are positives, W- if they are negatives)
   */
  public double getW(MyDataset data, Mask actives, double[] distribution){
    double w=0.0;

    Mask covered=actives.copy();
    data.filter(covered,this);
    covered.resetIndex();
    while (covered.next()){
      w+=distribution[covered.getIndex()];
    }

    return w;
  }

  /**
   * Computes the confidence of this rule, according to the equation 4
   * of [AAAI99]:
   * Cr=1/2ln((W+ + 1/(2n))/(W_ + 1/(2n)))
   * W+: sum of the weights of the positive instances that are covered by the current rule
   * W_: sum of the weights of the negative instances that are covered by the current rule
   * n: |p|+|n|
   * @param data MyDataset the dataset
   * @param positives Mask the positive entries
   * @param negatives Mask the negative entries
   * @param distribution double[] the distribution D of weights
   */
  public void setCr(MyDataset data, Mask positives, Mask negatives, double[] distribution){
    double w_plus=getW(data,positives,distribution);
    double w_minus=getW(data,negatives,distribution);
    double n=positives.getnActive()+negatives.getnActive();

    this.Cr=1.0/2.0*Math.log( (w_plus+(1.0/(2.0*n)))/(w_minus+(1.0/(2.0*n))) );
  }

  /**
   * Sets the new confidence of the rule.
   * @param newCr the new confidence.
   */
  public void setCr(double newCr){this.Cr=newCr;}

  /**
   * Returns the confidence of the rule.
   * @return the confidence of the rule.
   */
  public double getCr(){return Cr;}


  /**
   * Returns the i-ieth simple rule of this rule.
   * @param i position of the simple rule
   * @return the i-ieth simple rule of this rule.
   */
  public SimpleRule getSimpleRule(int i){
    return (SimpleRule) chain.elementAt(i);
  }

  /**
   * Adds a simple rule to this rule.
   * @param attribute int attribute id (position of the attribute)
   * @param value double attribute's value
   * @param operator int rule operator
   */
  public void grow(int attribute,double value,int operator){
    SimpleRule sr=new SimpleRule(attribute,value,operator);
    chain.add(sr);
  }

  /**
   * Adds a simple rule to this rule.
   * @param sr SimpleRule the simple rule
   */
  public void grow(SimpleRule sr){
    if (sr!=null)
      chain.add(sr);
  }

  /**
   * It sets the right side of the rule.
   * @param new_class double new class of the rule
   */
  public void setType(String new_class){
    this.type=new_class;
  }

  /**
   * It returns the right side (class) of the rule.
   * @return the right side (class) of the rule.
   */
  public String getType(){
    return type;
  }

  /**
   * It returns a copy of this rule
   * @return a copy of this rule
   */
  public Rule getCopy(){
    Rule r=new Rule();
    for (int i=0;i<chain.size();i++)
      r.grow(this.getSimpleRule(i).getCopy());
    return r;
  }

  /**
   * Deletes a simple rule from this chain
   * @param pos int position of the simple rule of the rule
   */
  public void prune(int pos){
    chain.remove(pos);
  }

  /**
   * Returns the size (number of simple rules) of the rule
   * @return the size (number of simple rules) of the rule
   */
  public int size(){
    return chain.size();
  }

  /**
   * Return wether this rule is equal to another given rule
   * @param r Rule the given rule
   * @return true if this rule is equal to the given rule
   */
  public boolean isEqual(Rule r){
    if (chain.size()!=r.size()) return false;
    boolean is_equal=true;
    for (int i=0;i<r.size() && is_equal;i++)
      is_equal=this.contains((SimpleRule) r.getSimpleRule(i));
    return is_equal;
  }

  /**
   * Returns a string representation of this Rule, containing the String representation of each SimpleRule.
   * @return a string representation of this Rule, containing the String representation of each SimpleRule.
   */
  public String toString(){
    String output="(";
    if (chain.size()!=0){
      output+=((SimpleRule)chain.elementAt(0)).toString();
    }
    for (int i=1;i<chain.size();i++)
      output+=" && "+((SimpleRule)chain.elementAt(i)).toString();
    output+=")";
    if (!type.equals("")){
      output+="-> ";
      output+=type;
    }
    output+=" ("+Cr+")";
    return output;
  }

}
