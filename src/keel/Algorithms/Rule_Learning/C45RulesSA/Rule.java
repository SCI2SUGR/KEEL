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
 * @author Written by Antonio Alejandro Tortosa (University of Granada) 01/07/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Rule_Learning.C45RulesSA;

import java.util.Vector;



public class Rule {
/**
 * <p>
 * Representation of a string of simple rules chained by 'and's: <b>exemple[a1][=|>|<=]v1 && exemple[a2][=|>=|<=]v2</b>
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

  /** The redundancy factor in theory description length */
  private static double REDUNDANCY_FACTOR = 0.5;

  /** The theory weight in the MDL calculation */
  private double MDL_THEORY_WEIGHT = 1.0;

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
    if (this.chain.size()==0)
      return select.getnActive();

    if (this.chain.size()==1 && ignore==0)
      return select.getnActive();

    int output=0; //That variable will take the count of the covered entries
    select.resetIndex();
    while (select.next()){

      double[] exemple=data.getExample(select);
      boolean salir=false;
      for (int j=0;j<chain.size() && !salir;j++){

        SimpleRule sr = (SimpleRule) chain.elementAt(j);
        int attribute = sr.getAttribute();
        double value = sr.getValue();
        if (data.isMissing(select, attribute) && j != ignore) {
          salir = true; //if any value is missing the whole comprobation for that entry fails
        }
        else if (j != ignore) {
          if (sr.isDiscret()) {
            salir = ! ( exemple[attribute] == value);
          }
          else {
            if (sr.getOperator() == SimpleRule.GREATER)
              salir = ! ( exemple[attribute] > value);
            else
              salir = ! ( exemple[attribute] <= value);
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
   * Returns the Minimum Data Length of a dataset given a theory (this rule). See [Quinlan95]
   * @param data MyDataset the datasets
   * @param positives Mask active positive entries of data
   * @param negatives Mask active negative entries of data
   * @return the MDL of data given this rule
   */
  public double getExceptionCost(MyDataset data,Mask positives,Mask negatives){
    Stats quartet=apply(data,positives,negatives);
    double tp=quartet.tp,tn=quartet.tn,fp=quartet.fp,fn=quartet.fn;
    double tp_prob,tn_prob,fp_prob,fn_prob;
    double U=tn+fn,C=tp+fp; //uncovered & covered cases
    double D=U+C,e=fn+fp;
    if ( C > 0.5 * (D) )
     {
         return Utilities.log2(D+1)
                 + biased(C, fp, 0.5 * e)
                 + biased(U, fn, fn);
     }
     else
     {
         return Utilities.log2(D+1)
                 + biased(C, fp, fp)
                 + biased(U, fn, 0.5 * e);
     }

  }

  /**
  * Static version.
  * Returns the Minimum Data Length of a dataset given a theory (this rule). See [Quinlan95]
  * @param data MyDataset the datasets
  * @param tp int true positives
  * @param tn int true negatives
  * @param fp int false positives
  * @param fn int false negatives
  * @return the MDL of data given this rule
  */
 public static double getExceptionCost(MyDataset data,int tp,int tn,int fp,int fn){
   double tp_prob,tn_prob,fp_prob,fn_prob;
   double U=tn+fn,C=tp+fp; //uncovered & covered cases
   double D=U+C,e=fn+fp;

   if (C==0) return Double.MAX_VALUE;

   if ( C > 0.5 * (D) )
     {
         return Utilities.log2(D+1)
                 + biased(C, fp, 0.5 * e)
                 + biased(U, fn, fn);
     }
     else
     {
         return Utilities.log2(D+1)
                 + biased(C, fp, fp)
                 + biased(U, fn, 0.5 * e);
     }

  }

  public static double biased(double N, double E, double ExpE){

    double Rate;

    if ( ExpE <= 1E-6 )
    {
      return ( E == 0 ? 0.0 : 1E6 );
    }
    else
    if ( ExpE >= N-1E-6 )
    {
      return ( E == N ? 0.0 : 1E6 );
    }

    Rate = ExpE/N;
    return -E * Utilities.log2(Rate) - (N-E) * Utilities.log2(1-Rate);

  }

  /**
   * Subset description length: <br>
   * S(t,k,p) = -k*log2(p)-(n-k)log2(1-p)
   *
   * Details see Quilan: "MDL and categorical theories (Continued)",ML95
   *
   * @param t the number of elements in a known set
   * @param k the number of elements in a subset
   * @param p the expected proportion of subset known by recipient
   * @return the subset description length
   */
  public static double subsetDL(double t, double k, double p){
    double rt = (p>0.0) ? (- k*Utilities.log2(p)) : 0.0;
    rt -= (t-k)*Utilities.log2(1-p);
    return rt;
  }

  /**
   * The description length of the theory for a given rule.  Computed as:<br>
   *                 0.5* [||k||+ S(t, k, k/t)]<br>
   * where k is the number of antecedents of the rule; t is the total
   * possible antecedents that could appear in a rule; ||K|| is the
   * universal prior for k , log2*(k) and S(t,k,p) = -k*log2(p)-(n-k)log2(1-p)
   * is the subset encoding length.<p>
   *
   * Details see Quilan: "MDL and categorical theories (Continued)",ML95
   *
   * @param data MyDataset the dataset
   * @return the theory DL, weighted if weight != 1.0
   */
  public double theoryDL(MyDataset data){

    double k = size();

    if(k == 0)
      return 0.0;

    double tdl = Math.log(k);
    if(k > 1)                           // Approximation
      tdl += 2.0 * Math.log(tdl);   // of log2 star

    double totalCond=0.0;
    for (int i=0;i<k;i++)
      totalCond+=data.numAllConditions(getSimpleRule(i).getAttribute());

    tdl += subsetDL(totalCond, k, k/totalCond);

    return MDL_THEORY_WEIGHT * REDUNDANCY_FACTOR * tdl;
  }


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
    return output;
  }

}
