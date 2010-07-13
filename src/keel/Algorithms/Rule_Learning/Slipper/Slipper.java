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
 * @author Written by Antonio Alejandro Tortosa  (University of Granada)  05/04/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.3
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Rule_Learning.Slipper;

import keel.Dataset.*;
import java.io.IOException;
import org.core.*;


public class Slipper {
/**
 * <p>
 * Implementation of the classification algorithm Slipper, according to the paper [AAAI99].
 * </p>
 */
	
  //'Worth' metric
  public static int W=1; 
  //'Accuracy'metric
  public static int A=2; 
  //the datasets for training, validation and test
  MyDataset train, val, test; 
  //the names for the output files
  String outputTr, outputTst, outputRules; 
  //random numbers generator
  Randomize rand; 
  //ratio of growing/pruning instances
  double pct; 
  //number of growing rules per class
  int T; 
  //to check if everything is correct.
  private boolean somethingWrong = false; 

  /**
   * It reads the data from the input files (training, validation and test) and parse all the parameters
   * from the parameters array.
   * @param parameters parseParameters It contains the input files, output files and parameters
   */
  public Slipper(parseParameters parameters) {

    train = new MyDataset();
    val = new MyDataset();
    test = new MyDataset();
    try {
      System.out.println("\nReading the training set: " +
                         parameters.getTrainingInputFile());
      train.readClassificationSet(parameters.getTrainingInputFile(), true);
      System.out.println("\nReading the validation set: " +
                         parameters.getValidationInputFile());
      val.readClassificationSet(parameters.getValidationInputFile(), false);
      System.out.println("\nReading the test set: " +
                         parameters.getTestInputFile());
      test.readClassificationSet(parameters.getTestInputFile(), false);
    } catch (IOException e) {
      System.err.println(
          "There was a problem while reading the input data-sets: " +
          e);
      somethingWrong = true;
    }

    //We may check if there are some numerical attributes, because our algorithm may not handle them:
    //somethingWrong = somethingWrong || train.hasNumericalAttributes();
    //somethingWrong = somethingWrong || train.hasMissingAttributes();

    outputTr = parameters.getTrainingOutputFile();
    outputTst = parameters.getTestOutputFile();
    outputRules = parameters.getOutputFile(0);

    long seed = Long.parseLong(parameters.getParameter(0));
    pct = Double.parseDouble(parameters.getParameter(1));
    T = Integer.parseInt(parameters.getParameter(2));
    rand=new Randomize();
    rand.setSeed(seed);
    System.out.println("T: "+this.T+" pct: "+pct+" seed:"+seed);


  }

  /**
   * It launches the algorithm.
   */
  public void execute() {
    if (somethingWrong) { //We do not execute the program
      System.err.println("An error was found, the data-set have numerical values.");
      System.err.println("Aborting the program");
      //We should not use the statement: System.exit(-1);
    } else {
      //We do here the algorithm's operations
      Ruleset[] rulesets=this.slipperMulticlass(train);

      //Classificates the datasets' entries, according the generated rulesets
      String[] classification_train=train.classify(rulesets,rulesets.length);
      String[] classification_val=val.classify(rulesets,rulesets.length);
      String[] classification_test=test.classify(rulesets,rulesets.length);

      //Finally we should fill the training and test output files
      doOutput(this.val, this.outputTr, classification_val);
      doOutput(this.test, this.outputTst, classification_test);
      doRulesOutput2(this.outputRules,rulesets);
      System.out.println("Algorithm Finished");
    }
  }

  /**
   * It generates the output file from a given dataset and stores it in a file.
   * @param dataset myDataset input dataset
   * @param filename String the name of the file
   * @param classification String[] gererated classification of the dataset
   */
  private void doOutput(MyDataset dataset, String filename,String[] classification) {
    String output = new String("");
    output = dataset.copyHeader(); //we insert the header in the output file
    //We write the output for each example
    for (int i = 0; i < dataset.getnData(); i++) {
      output += dataset.getOutputAsString(i) + " " +classification[i] + "\n";
    }
    Fichero.escribeFichero(filename, output);
  }

  /**
   * It generates the output rules file from a given ruleset and stores it in a file
   * @param filename String the name of the file
   * @param rulesets Rulesets[] the rulesets (one for each class)
   */
  private void doRulesOutput(String filename,Ruleset[] rulesets) {
    String output = new String("");

    for (int i=0;i<rulesets.length-1;i++){
      output+="if(";
      for(int j=0;j<rulesets[i].size();j++){
        Rule current=rulesets[i].getRule(j);
        output+="(";
        for (int k=0;k<current.size();k++){
          output+=current.getSimpleRule(k);
          if (k!=current.size()-1) output+=" && ";
        }
        output+=")";
        if (j!=rulesets[i].size()-1) output+=" || ";
      }
      output+=")\n\t";
      output+="output="+rulesets[i].getType()+"\nelse ";
    }
    output+="\n\toutput="+rulesets[rulesets.length-1].getType();
    Fichero.escribeFichero(filename, output);
  }

  /**
   * It generates the output rules file from a given ruleset and stores it in a file
   * @param filename String the name of the file
   * @param rulesets Rulesets[] the rulesets (one for each class)
   */
  private void doRulesOutput2(String filename,Ruleset[] rulesets) {
    String output = new String("");

    for (int i=0;i<rulesets.length-1;i++){
      if (rulesets[i].size()>0){
        output += "Ruleset " + i + ":=" + rulesets[i].getType() + "\n";
        for (int j = 0; j < rulesets[i].size(); j++) {
          Rule current = rulesets[i].getRule(j);
          output += "\t(";
          for (int k = 0; k < current.size(); k++) {
            output += current.getSimpleRule(k);
            if (k != current.size() - 1)
              output += " && ";
          }
          output += ") " + "w: " + current.getCr() + "\n";
        }
        output += "Default Rule Weight=" + -rulesets[i].getDefaultCr() + "\n";
      }
    }
    output+="\nDefault Class="+rulesets[rulesets.length-1].getType();
    Fichero.escribeFichero(filename, output);
  }

  /**
   * It expands a rule, greedily adding simple rules, maximizing the following heuristic:
   * Z=sqrt(W+)-sqrt(W_)
   * W+: sum of the weights of the positive instances that are covered by the current rule
   * W_: sum of the weights of the negative instances that are covered by the current rule
   * @param data MyDataset the dataset
   * @param grow_positives Mask active positive entries
   * @param grow_negatives Mask active negative entries
   * @param distribution double[] the distribution D of weights
   * @return the grown rule
   */
  public static Rule grow(MyDataset data,Mask grow_positives,Mask grow_negatives,double[] distribution){
    Rule rule=new Rule();

    Mask positives=grow_positives.copy();
    Mask negatives=grow_negatives.copy();

    int[] attributes=new int[data.getnInputs()];
    int nattributes=attributes.length;
    for (int i=0;i<attributes.length;i++){
      attributes[i]=i;
    }

    while (negatives.getnActive()>0 && nattributes>0  && positives.getnActive()>0){
      int A=-1,P=-1; //A->best attribute, P-> relative position inside Attributes
      double V=0,best_global=-Double.MAX_VALUE;
      int Op=-1;

      for (int i=0;i<nattributes;i++){
        int ai=attributes[i];
        Score score=new Score();
        double total_pos=0.0,total_neg=0.0;

        positives.resetIndex();
        while (positives.next()){
          if (!data.isMissing(positives,ai)){
            double[] exemple=data.getExample(positives);
            total_pos+=distribution[positives.getIndex()];
            int pos = score.findKey(exemple[ai]);
            if (pos!=-1)
              score.addPositive(pos,distribution[positives.getIndex()]);
            else
              score.addKey(exemple[ai],distribution[positives.getIndex()],Score.POSITIVE);
          }
        }

        negatives.resetIndex();
        while (negatives.next()){
          if (!data.isMissing(negatives,ai)){
            double[] exemple=data.getExample(negatives);
            total_neg+=distribution[negatives.getIndex()];
            int pos = score.findKey(exemple[ai]);
            if (pos!=-1)
              score.addNegative(pos,distribution[negatives.getIndex()]);
            else
              score.addKey(exemple[ai],distribution[negatives.getIndex()],Score.NEGATIVE);
          }
        }

        //First, to find the best value for the current attribute
        double best_v=0,best_h=-Double.MAX_VALUE;
        int best_operator=-1;
        if(Attributes.getInputAttribute(ai).getType()==Attribute.NOMINAL){
          for (int j = 0; j < score.size(); j++) {
            double W_plus=score.getPositive(j);
            double W_minus=score.getNegative(j);
            double h=Math.sqrt(W_plus)-Math.sqrt(W_minus);
            if (Utilities.gr(h,best_h)) {
              best_h = h;
              best_v = score.getKey(j);
              best_operator=Rule.EQUAL;
            }
          }
        }
        else{
          score.sort();
          //Evaluating the first element as cutting point with operator <=
          double count_pos=0.0;
          double count_neg=0.0;
          double W_plus,W_minus;
          if (score.size()==1 && score.getPositive(0)!=0){
            W_plus=score.getPositive(0);
            W_minus=score.getNegative(0);
            best_h=Math.sqrt(W_plus)-Math.sqrt(W_minus)+1.0;
            best_v = score.getKey(0);
            best_operator=Rule.EQUAL;
          }
          else if (score.size()==1){
            best_h = -Double.MAX_VALUE;
            best_v = score.getKey(0);
            best_operator=Rule.EQUAL;
          }
          else
            best_h = -Double.MAX_VALUE;
          for (int j = 0; j < score.size()-1; j++) {
            //Evaluating the j-th element as cutting point with <=
            count_pos+=score.getPositive(j);
            count_neg+=score.getNegative(j);
            W_plus=count_pos;
            W_minus=count_neg;
            double h_lower=Math.sqrt(W_plus)-Math.sqrt(W_minus)+1.0;
            //Evaluating the j-th element as cutting point with >
            W_plus=total_pos-count_pos;
            W_minus=total_neg-count_neg;
            double h_greater=Math.sqrt(W_plus)-Math.sqrt(W_minus)+1.0;
            //Comparing with the best so far
            if (Utilities.gr(h_lower,h_greater) && Utilities.gr(h_lower,best_h)) {
              best_h = h_lower;
              best_v = score.getKey(j);
              best_operator=Rule.LOWER;
            }
            else if (Utilities.gr(h_greater,best_h)){
              best_h = h_greater;
              best_v = score.getKey(j);
              best_operator=Rule.GREATER;
            }
          }
        }

        //Later, test if it is the best couple so far
        if (Utilities.gr(best_h,best_global)){
          P=i;
          A=ai;
          V=best_v;
          Op=best_operator;
          best_global=best_h;
        }

      }

      //2.Add to the rule the couple (A,V)
      //Julian - If no attribute could be found, do not add the couple
      //I really don't know if this assumption it is correct, but it allows the program
      //to finish, so...
      if(A!=-1){ 
    	  rule.grow(A,V,Op);
    	  data.filter(positives,A,V,Op);
    	  data.filter(negatives,A,V,Op);
    	  attributes[P]=attributes[nattributes-1];
      }
      nattributes--;

    }

    return rule;
  }

  /**
   * It prunes a rule, minimizing with the heuristic:
   * 1 - V+ + V_ + V+·exp(-Cr) + V_·exp(Cr)
   * V+: sum of the weights of the positive instances of the prune set that are covered by the current rule
   * V_: sum of the weights of the negative instances of the prune set that are covered by the current rule
   * Cr: rule confidence (computed in the grow set)
   * @param rule Rule the rule to prune
   * @param data MyDataset the dataset
   * @param prune_positives Mask active positive entries for pruning
   * @param prune_negatives Mask active negative entries for pruning
   * @param grow_positives Mask active positive entries for growing
   * @param grow_negatives Mask active negative entries for growing
   * @param distribution double[] the distribution D of weights
   * @return the pruned rule.
   */
  public static Rule prune(Rule rule,MyDataset data,Mask prune_positives,Mask prune_negatives,
                           Mask grow_positives,Mask grow_negatives,double[] distribution){
    double V_plus,V_minus,Cr;
    double h=Double.MAX_VALUE,next_h=0.0;
    SimpleRule last=null;

    V_plus=rule.getW(data,prune_positives,distribution);
    V_minus=rule.getW(data,prune_negatives,distribution);
    rule.setCr(data,grow_positives,grow_negatives,distribution);
    Cr=rule.getCr();
    next_h=1.0-V_plus-V_minus+(V_plus*Math.exp(-Cr))+(V_minus*Math.exp(Cr));

    while(Utilities.smOrEq(next_h,h) && rule.size()>1){
      h=next_h;
      last=rule.getSimpleRule(rule.size()-1);
      rule.prune(rule.size()-1);
      V_plus=rule.getW(data,prune_positives,distribution);
      V_minus=rule.getW(data,prune_negatives,distribution);
      rule.setCr(data,grow_positives,grow_negatives,distribution);
      Cr=rule.getCr();
      next_h=1.0-V_plus-V_minus+(V_plus*Math.exp(-Cr))+(V_minus*Math.exp(Cr));
    }

    if (Utilities.gr(next_h,h)){
      rule.grow(last);
    }

    return rule;
  }

  /**
   * It implements a multiclass variation of the algorithm Slipper:
   * 1. In each iteration, it takes the class with less instances in the dataset and
   *    it splits this into positive (those of the taken class) and negative (the rest) instances.
   * 2. Then it invokes Slipper to generates a Ruleset for the taken class.
   * 3. Finally, it removes the instances covered by the ruleset and it carries on with the next iteration.
   * @param data MyDataset the dataset
   * @return a vector with a Ruleset for each class.
   */
  public Ruleset[] slipperMulticlass(MyDataset data){
    Ruleset[] rules=new Ruleset[data.getnClasses()];
    Pair[] ordered_classes=new Pair[data.getnClasses()];
    for (int i=0;i<data.getnClasses();i++){
      ordered_classes[i]=new Pair();
      ordered_classes[i].key=i;
      ordered_classes[i].value=data.numberInstances(i);
    }
    Utilities.mergeSort(ordered_classes,data.getnClasses());

    Mask positives,negatives;
    Mask base=new Mask(data.size());
    for (int i=0;i<data.getnClasses()-1;i++){
      String target_class=Attributes.getOutputAttribute(0).getNominalValue(ordered_classes[i].key);
      positives=base.copy();
      data.filterByClass(positives,target_class);
      negatives=base.and(positives.complement());
      rules[i]=slipper(data,positives,negatives,T);
      rules[i].setType(target_class);
      base=negatives.copy();
    }
    rules[rules.length-1]=new Ruleset();
    rules[rules.length-1].addRule(new Rule());
    rules[rules.length-1].setType(Attributes.getOutputAttribute(0).getNominalValue(ordered_classes[data.getnClasses()-1].key));

    return rules;
  }

  /**
   * It reweights the instances, making use of the confidence of the last rule.
   * <li>- For each xi e Rt, set D(i)<-D(i)/exp(yi·Cr) yi e {-1,1} </li>
   * <li>- Let Zt= S(D(i) i e [1-m] </li>
   * <li>- For each xi, set D(i)<-D(i)/Zt</li>
   * @param data the Dataset
   * @param new_rule the new rule
   * @param positives the positives intances of the dataset (with yi=1)
   * @param negatives the negatives intances of the dataset (with yi=-1)
   * @param distribution the distribution D of weights
   * @param Cr the rule confidence (computed in the entire dataset)
   */
  public void update(MyDataset data,Rule new_rule,Mask positives,Mask negatives,double[] distribution,double Cr){

    Mask covered_pos=positives.copy(),covered_neg=negatives.copy();
    if (new_rule.size()!=0){
      data.filter(covered_pos, new_rule);
      data.filter(covered_neg, new_rule);
    }


    double expCr=Math.exp(Cr);
    covered_pos.resetIndex();
    while(covered_pos.next()){
      int i=covered_pos.getIndex();
      distribution[i]=distribution[i]/expCr;
    }

    expCr=Math.exp(-Cr);
    covered_neg.resetIndex();
    while(covered_neg.next()){
      int i=covered_neg.getIndex();
      distribution[i]=distribution[i]/expCr;
    }

    double Z=0.0;
    for (int i=0;i<data.size();i++)
      if (negatives.isActive(i) || positives.isActive(i))
        Z+=distribution[i];

    for (int i=0;i<data.size();i++)
      if (negatives.isActive(i) || positives.isActive(i))
        distribution[i]=distribution[i]/Z;

  }

  /**
   * It implements the algorithm Slipper (2 class version).
   * @param data MyDataset the dataset
   * @param positives Mask active positive entries
   * @param negatives Mask active negative entries
   * @param T int number of growing rules per class
   * @return the generated ruleset
   */
  public Ruleset slipper(MyDataset data,Mask positives,Mask negatives,int T){
    Ruleset rules=new Ruleset();

    /**********************Growing & Prunning***************************************/
    double m=positives.getnActive()+negatives.getnActive();
    double[] distribution=new double[data.size()];
    for (int i=0;i<m;i++)
      distribution[i]=1.0/m;

    for (int i=0;i<T;i++){
      //Splitting of the two dataset into two prune dataset and two grow dataset
      Mask[] gp_pos=positives.split(pct,rand);
      Mask[] gp_neg=negatives.split(pct,rand);

      Mask grow_pos=gp_pos[0], prune_pos=gp_pos[1];
      Mask grow_neg=gp_neg[0], prune_neg=gp_neg[1];

      //Grow & Prune
      Rule new_rule=Slipper.grow(data,grow_pos,grow_neg,distribution);
      Slipper.prune(new_rule,data,prune_pos,prune_neg,grow_pos,grow_neg,distribution);

      //The new rule is compared with the default
      //The new rule's stats
      double W_plus=new_rule.getW(data,positives,distribution);
      double W_minus=new_rule.getW(data,negatives,distribution);
      double Cr=0.5*Math.log((W_plus+1.0/(2.0*m))/(W_minus+1.0/(2.0*m)));
      double Zaux=Math.sqrt(W_plus)-Math.sqrt(W_minus)+1.0;
      //double Z=1.0-(Zaux*Zaux);
      //The default rule's stats
      double defaultW_plus=Rule.getDefaultW(data,positives,distribution);
      double defaultW_minus=Rule.getDefaultW(data,negatives,distribution);
      double defaultCr=0.5*Math.log((defaultW_plus+1.0/(2.0*m))/(defaultW_minus+1.0/(2.0*m)));
      double defaultZaux1=Math.sqrt(defaultW_plus)-Math.sqrt(defaultW_minus)+1.0;
      double defaultZaux2=Math.sqrt(defaultW_minus)-Math.sqrt(defaultW_plus)+1.0;
      double defaultZaux=(defaultZaux1>defaultZaux2)?defaultZaux1:defaultZaux2;

      //double defaultZ=1.0-(defaultZaux*defaultZaux);

      double bestCr=0.0;
      Rule best_rule=null;
      if (Utilities.gr(Zaux,defaultZaux)){
        bestCr=Cr;
        best_rule=new_rule;
        new_rule.setCr(Cr);
        rules.addRule(new_rule);
      }
      else{
        bestCr=defaultCr;
        best_rule=new Rule();
        rules.addToDefaultCr(defaultCr);
      }
      //Updating
      update(data,best_rule,positives,negatives,distribution,bestCr);

    }

    return rules;
  }

}
