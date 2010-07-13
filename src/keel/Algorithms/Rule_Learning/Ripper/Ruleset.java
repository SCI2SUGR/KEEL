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

package keel.Algorithms.Rule_Learning.Ripper;

import java.util.Vector;


public class Ruleset {
/**
 * Representation of a disjuction of rules with a common consecuent.
 * It may be represented as: <b>if (rule1 || rule2) then output=consecuent<\b>
 */
	
  private Vector rules; //set of rules
  private String type; //class (consecuent)

  /**
   *
   * Constructs an empty ruleset.
   */
  public Ruleset() {
    rules=new Vector();
  }

  /**
   * Adds a new rule to the ruleset.
   * @param r Rule the new rule
   */
  public void addRule(Rule r){
    rules.add(r);
  }

  /**
   * It returns the number of true positives,true negatives,false positives and false negatives of the whole ruleset in a given dataset.
   * This methods takes into account the right part (consecuent) of the rules
   * @param data MyDataset the dataset
   * @return number of true positives, false positives, true negatives and false negatives of the whole ruleset in the following order: {tp,tn,fp,fn}
   */
  public Stats apply(MyDataset data){
    //int tp,tn,fp,fn;
    Stats stats=new Stats();
    //It splits the positive and negative instances according to the consecuent
    Mask positives=new Mask(data.size());
    data.filterByClass(positives,type);
    Mask negatives=positives.complement();
    int npositives=positives.getnActive();
    int nnegatives=negatives.getnActive();
    for (int i=0;i<rules.size();i++){
      //it extracts the instances covered by the i-th rule of the ruleset
      data.substract(positives,(Rule) rules.elementAt(i));
      data.substract(negatives,(Rule) rules.elementAt(i));
    }
    stats.fn=positives.getnActive(); //what remains are false positives
    stats.tp=npositives-stats.fn; //true positives
    stats.tn=negatives.getnActive(); //true negatives
    stats.fp=nnegatives-stats.tn; //false negatives
    return stats;
  }

  /**
   * It returns the number of true positives,true negatives,false positives and false negatives of the whole ruleset in a given dataset.
   * (This methods doesn't take into account the right part (consecuent) of the rules).
   * @param data MyDataset the dataset
   * @param positives active positive instances of data
   * @param negatives active negative instances of data
   * @return number of true positives, false positives, true negatives and false negatives of the whole ruleset in the following order: {tp,tn,fp,fn}
   */
  public Stats apply(MyDataset data,Mask positives,Mask negatives){
    Stats stats=new Stats();
    int npositives=positives.getnActive();
    int nnegatives=negatives.getnActive();
    Mask p=positives.copy();
    Mask n=negatives.copy();
    for (int i=0;i<rules.size();i++){
      //it extracts the instances covered by the i-th rule of the ruleset
      data.substract(p,(Rule) rules.elementAt(i));
      data.substract(n,(Rule) rules.elementAt(i));
    }
    stats.fn=p.getnActive(); //what remains are false positives
    stats.tp=npositives-stats.fn; //true positives
    stats.tn=n.getnActive(); //true negatives
    stats.fp=nnegatives-stats.tn; //false negatives
    return stats;
  }

  /**
   * Returns the exception cost for the Minimum Data Length of a dataset given a theory (this ruleset). See [Quinlan95]
   * @param data MyDataset the datasets
   * @param positives Mask active positive entries of data
   * @param negatives Mask active negative entries of data
   * @return the MDL of data given this ruleset.
   */
  public double getExceptionCost(MyDataset data,Mask positives,Mask negatives){
    if (rules.size()==0)
      return 0.0;// Double.MAX_VALUE;

    Stats quartet=apply(data,positives,negatives);
    double tp=quartet.tp,tn=quartet.tn,fp=quartet.fp,fn=quartet.fn;
    double tp_prob,tn_prob,fp_prob,fn_prob;
    double U=tn+fn,C=tp+fp; //uncovered & covered cases
    double D=U+C,e=fn+fp;
    double mdl=0.0;//Double.MAX_VALUE;
    double uncoverBits,coverBits;
    if (C > U){
      /*mdl = Math.log(D + 1)
            + fp * ( -Math.log(e / (2 * C)))
            + (C - fp) * ( -Math.log(1 - (e / (2 * C))))
            + fn * ( -Math.log(fn / U))
            + (U - fn) * ( -Math.log(1 - fn / U));
       */
      coverBits=e/(2*C);
      uncoverBits=(U>0.0)?fn/U:0.0;
    }
    else{
      /* mdl = Math.log(D + 1)
            + fn * ( -Math.log(e / (2 * U)))
            + (U - fn) * ( -Math.log(1 - e / (2 * U)))
            + fp * ( -Math.log(fp / C))
            + (C - fp) * ( -Math.log(1 - (fp / C)));
       */
      coverBits=(C>0.0)?fp/C:0.0;
      uncoverBits=e/(2*U);
    }

    tp_prob=(coverBits==0)?0.0:tp*(-Utilities.log2(1-coverBits));
    fp_prob=(coverBits==0)?0.0:fp*(-Utilities.log2(coverBits));
    tn_prob=(uncoverBits==0)?0.0:tn*(-Utilities.log2(1-uncoverBits));
    fn_prob=(uncoverBits==0)?0.0:fn*(-Utilities.log2(uncoverBits));
    mdl = Utilities.log2(D + 1) + tp_prob + tn_prob + fp_prob + fn_prob;

    return mdl;
  }

  /**
   * Returns the Minimum Data Length of a dataset given a theory (this ruleset). See [Quinlan95]
   * @param data MyDataset the datasets
   * @param positives Mask active positive entries of data
   * @param negatives Mask active negative entries of data
   * @return the MDL of data given this ruleset.
   */
  public double getMDL(MyDataset data,Mask positives,Mask negatives){
    return getTheoryCost(data)+getExceptionCost(data,positives,negatives);
  }

  /**
   * Returns the exception cost for the Minimum Data Length of a dataset given a theory (this ruleset). See [Quinlan95]
   * @param data MyDataset the datasets
   * @return the MDL of data given this ruleset.
   */
   public double getExceptionCost(MyDataset data){
     Mask positives=new Mask(data.size());
     data.filterByClass(positives,this.type);
     Mask negatives=positives.complement();
     return getExceptionCost(data,positives,negatives);
   }

   /**
    * Returns the Minimum Data Length of a dataset given a theory (this ruleset). See [Quinlan95]
    * @param data MyDataset the datasets
    * @return the MDL of data given this ruleset.
    */
   public double getMDL(MyDataset data){
     return getTheoryCost(data)+getExceptionCost(data);
   }

   /**
    * Returns the exception cost for the Minimum Data Length of a dataset given a theory (this ruleset). See [Quinlan95]
    * @param data MyDataset the datasets
    * @param positives Mask active positive entries of data
    * @param negatives Mask active negative entries of data
    * @param rulesetMask the combine mask of all rules in the ruleset.
    * @return the MDL of data given this ruleset.
    */
   public double getExceptionCost(MyDataset data,Mask positives,Mask negatives,IncrementalMask rulesetMask){
     int tp=rulesetMask.and(positives).getnActive(); //true positives
     int fp=rulesetMask.and(negatives).getnActive(); //false positives
     int fn=positives.getnActive()-tp; //false negatives
     int tn=negatives.getnActive()-fp; //true negatives
     double mdl_ruleset=Rule.getExceptionCost(data,tp,tn,fp,fn);

     return mdl_ruleset;
   }

   /**
    * Returns the Minimum Data Length of a dataset given a theory (this ruleset). See [Quinlan95]
    * @param data MyDataset the datasets
    * @param positives Mask active positive entries of data
    * @param negatives Mask active negative entries of data
    * @param rulesetMask the combine mask of all rules in the ruleset.
    * @return the MDL of data given this ruleset.
    */
   public double getMDL(MyDataset data,Mask positives,Mask negatives,IncrementalMask rulesetMask){
     return getTheoryCost(data)+getExceptionCost(data,positives,negatives,rulesetMask);
   }

   /**
    * The description length of the theory for the ruleset.
    * Computed as the addition of the theory cost for each rule:<br>
    *                 0.5* [||k||+ S(t, k, k/t)]<br>
    * where k is the number of antecedents of the rule; t is the total
    * possible antecedents that could appear in a rule; ||K|| is the
    * universal prior for k , log2*(k) and S(t,k,p) = -k*log2(p)-(n-k)log2(1-p)
    * is the subset encoding length.<p>
    * @param data MyDataset the dataset
    * @return the description length of the theory for the ruleset
    */
   public double getTheoryCost(MyDataset data){
     double total=0.0;
     for (int i=0;i<size();i++)
       total+=getRule(i).theoryDL(data);
     return total;
   }


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
   * Returns the combine mask of all the rules in the set.
   * @param data the dataset
   * @return the combine mask of all the rules in the set.
   */
  public IncrementalMask getRulesetMask(MyDataset data){
    IncrementalMask rulesetMask=new IncrementalMask(data.size());
    for(int i=0;i<rules.size();i++){
      Mask ruleMask=new Mask(data.size());
      data.filter(ruleMask,getRule(i));
      rulesetMask.plus(ruleMask);
    }

    return rulesetMask;
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
   * Removes the duplicated rules
   */
  public void removeDuplicates(){

    for (int i=0;i<rules.size();i++){
      Rule current = (Rule) rules.elementAt(i);
      if (current.size()!=0){
        for (int j = i + 1; j < rules.size(); j++) {
          if (current.isEqual( (Rule) rules.elementAt(j))) {
            rules.remove(j);
            j--;
          }
        }
      }
      else{
        rules.remove(i);
        i--;
      }
    }

  }


  /**
   * Remove the rules that increase the DL value of the set.
   * @param data the dataset
   * @param positives the positives exemples
   * @param negatives the negatives exemples
   */
  public void pulish(MyDataset data,Mask positives,Mask negatives){
    IncrementalMask rulesetMask=new IncrementalMask(data.size());
    Mask[] ruleMask=new Mask[rules.size()];
    for(int i=0;i<rules.size();i++){
      ruleMask[i]=new Mask(data.size());
      data.filter(ruleMask[i],getRule(i));
      rulesetMask.plus(ruleMask[i]);
    }
    double thCost=getTheoryCost(data); //theory cost

    int tp=rulesetMask.and(positives).getnActive(); //true positives
    int fp=rulesetMask.and(negatives).getnActive(); //false positives
    int fn=positives.getnActive()-tp; //false negatives
    int tn=negatives.getnActive()-fp; //true negatives
    double mdl_ruleset=thCost+Rule.getExceptionCost(data,tp,tn,fp,fn);

    for(int i=0;i<rules.size();i++){
      rulesetMask.minus(ruleMask[i]);
      thCost-=getRule(i).theoryDL(data);
      tp=rulesetMask.and(positives).getnActive(); //true positives
      fp=rulesetMask.and(negatives).getnActive(); //false positives
      fn=positives.getnActive()-tp; //false negatives
      tn=negatives.getnActive()-fp; //true negatives
      double mdl_whithout_i=thCost+Rule.getExceptionCost(data,tp,tn,fp,fn);
      if (mdl_whithout_i<mdl_ruleset){
        rules.remove(i);
        i--;
        mdl_ruleset=mdl_whithout_i;
      }
      else{
        rulesetMask.plus(ruleMask[i]);
        thCost+=getRule(i).theoryDL(data);
      }
    }
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
    return output;
  }

}
