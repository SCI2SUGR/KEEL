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
 * @author Written by Alejandro Tortosa (University of Granada)  15/10/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.4
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Rule_Learning.Ripper;

import keel.Dataset.*;
import java.io.IOException;
import org.core.*;



public class Ripper {
/**
 * Implementation of the classification algorithm Ripper, according to the paper [Cohen95]
 * and the Weka's implementation.
 */
	
  public static int W=1; //'Worth' metric
  public static int A=2; //'Accuracy'metric

  MyDataset train, val, test; //the datasets for training, validation and test
  String outputTr, outputTst, outputRules; //the names for the output files
  Randomize rand; //random numbers generator
  double pct; //ratio of growing/pruning instances
  int K; //number of optimization

  private boolean somethingWrong = false; //to check if everything is correct.

  /**
   * It reads the data from the input files (training, validation and test) and parse all the parameters
   * from the parameters array.
   * @param parameters parseParameters It contains the input files, output files and parameters
   */
  public Ripper(parseParameters parameters) {

    train = new MyDataset();
    val = new MyDataset();
    test = new MyDataset();
    try {
      System.out.println("\nReading the training set: " + parameters.getTrainingInputFile());
      train.readClassificationSet(parameters.getTrainingInputFile(), true);
      System.out.println("\nReading the validation set: " + parameters.getValidationInputFile());
      val.readClassificationSet(parameters.getValidationInputFile(), false);
      System.out.println("\nReading the test set: " + parameters.getTestInputFile());
      test.readClassificationSet(parameters.getTestInputFile(), false);
    } catch (IOException e) {
      System.err.println("There was a problem while reading the input data-sets: " + e);
      somethingWrong = true;
    }

    //We may check if there are some numerical attributes, because our algorithm may not handle them:
    //somethingWrong = somethingWrong || train.hasNumericalAttributes();
    //somethingWrong = somethingWrong || train.hasMissingAttributes();

    outputTr = parameters.getTrainingOutputFile();
    outputTst = parameters.getTestOutputFile();
    if ( parameters.getNOutputFiles()==0){
      System.err.println("No se ha especificado archivo para las reglas.");
      System.err.println("Usando nombre por defecto: rules-out.txt");
      outputRules="rules-out.txt";
    }
    else
      outputRules = parameters.getOutputFile(0);

    long seed = Long.parseLong(parameters.getParameter(0));
    pct = Double.parseDouble(parameters.getParameter(1));
    K = Integer.parseInt(parameters.getParameter(2));
    rand=new Randomize();
    rand.setSeed(seed);

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
      Ruleset[] rulesets=this.ripperMulticlass(train);

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
    int rules = 0;
    for (int i = 0; i < rulesets.length;i++){
      rules += rulesets[i].size();
    }
    output += "@Number of Rules: "+rules+"\n";
    for (int i=0;i<rulesets.length-1;i++){
      Mask class_filter=new Mask(train.size());
      train.filterByClass(class_filter,rulesets[i].getType());
      for(int j=0;j<rulesets[i].size();j++){
        output+="if(";
        Rule current=rulesets[i].getRule(j);
        for (int k=0;k<current.size();k++){
          output+=current.getSimpleRule(k);
          if (k!=current.size()-1) output+=" && ";
        }
        int covered=current.apply(train);
        int accuracy=current.apply(train,class_filter);
        output+=") ("+accuracy+"/"+covered+")\n\t";
        output+="output="+rulesets[i].getType()+"\nelse ";
      }
    }
    output+="\n\toutput="+rulesets[rulesets.length-1].getType();
    Fichero.escribeFichero(filename, output);
  }

  /**
   * It grows a rule maximizing the following heuristic:
   * h= p*(log(p/t)-log(P/T))
   * p/t: number of positive/total instances covered by the current rule
   * P/T: number of positive/total instances
   * @param data MyDataset the dataset
   * @param positives Mask active positive entries
   * @param negatives Mask active negative entries
   * @return the grown rule
   */
  public Rule grow(MyDataset data,Mask positives,Mask negatives){
    return grow(new Rule(),data,positives,negatives);
  }

  /**
   * It expands a rule, greedily adding simple rules, maximizing the following heuristic:
   * h= p*(log(p/t)-log(P/T))
   * p/t: number of positive/total instances covered by the current rule
   * P/T: number of positive/total instances
   * @param rule Rule the base rule
   * @param data MyDataset the dataset
   * @param grow_pos Mask active positive entries
   * @param grow_neg Mask active negative entries
   * @return the grown rule
   */
  public Rule grow(Rule rule,MyDataset data,Mask grow_pos,Mask grow_neg){
	  double best_v=0,best_h=-Double.MAX_VALUE;
	  
    if (grow_pos.getnActive()<0)
      return new Rule();

    Mask positives = grow_pos.copy();
    Mask negatives = grow_neg.copy();
    int[] attributes=new int[data.getnInputs()];
    int nattributes=attributes.length;
    for (int i=0;i<attributes.length;i++){
      attributes[i]=i;
    }
    if (rule.size()>0){
      //Elimination of the attributes already used by the rule
      int[] aux = new int[data.getnInputs()];
      for (int i = 0; i < rule.size(); i++) {
        attributes[rule.getSimpleRule(i).getAttribute()] = -1;
      }
      int j = 0;
      for (int i = 0; i < nattributes; i++) {
        if (attributes[i] != -1) {
          aux[j] = attributes[i];
          j++;
        }
      }
      attributes = aux;
      nattributes = j;
      data.filter(positives,rule);
      data.filter(negatives,rule);
    }

    while (negatives.getnActive()>0 && nattributes>0 && positives.getnActive()>0){
      int A=-1,P=-1; //A->best attribute, P-> relative position inside Attributes
      double V=0,best_global=-Double.MAX_VALUE;
      int Op=-1;
      double C=Utilities.log2(positives.getnActive()/((double) (positives.getnActive()+negatives.getnActive())));

      for (int i=0;i<nattributes;i++){
        int ai=attributes[i];
        Score score=new Score();

        positives.resetIndex();
        while (positives.next()){
          if (!data.isMissing(positives,ai)){
            double[] exemple=data.getExample(positives);
            int pos = score.findKey(exemple[ai]);
            if (pos!=-1)
              score.addPositive(pos);
            else
              score.addKey(exemple[ai],Score.POSITIVE);
          }
        }

        negatives.resetIndex();
        while (negatives.next()){
          if (!data.isMissing(negatives,ai)){
            double[] exemple=data.getExample(negatives);
            int pos = score.findKey(exemple[ai]);
            if (pos!=-1)
              score.addNegative(pos);
            else
              score.addKey(exemple[ai],Score.NEGATIVE);
          }
        }

        //First, to find the best value for the current attribute
        best_v=0;
        best_h=-Double.MAX_VALUE;
        int best_operator=-1;
        if(Attributes.getInputAttribute(ai).getType()==Attribute.NOMINAL){
          for (int j = 0; j < score.size(); j++) {
            double h = score.getPositive(j) * (Utilities.log2(score.getPositive(j) / ( (double) score.getTotal(j))) -C);
           if (h > best_h) {
              best_h = h;
              best_v = score.getKey(j);
              best_operator=Rule.EQUAL;
            }
          }
        }
        else{
          score.sort();
          int total_pos=positives.getnActive(),total_neg=negatives.getnActive();
          //Evaluating the first element as cutting point with operator <=
          int count_pos=0;
          int count_neg=0;
          if (score.size()==1 && score.getPositive(0)!=0){
            best_h = count_pos * (Utilities.log2(score.getPositive(0)/( (double) score.getNegative(0) + score.getPositive(0))) - C);
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
            //Evaluating the j-th element as cutting point with >
            count_pos+=score.getPositive(j);
            count_neg+=score.getNegative(j);
            double h_lower;
            if (count_pos!=0)
              h_lower = count_pos * (Utilities.log2(count_pos / ( (double) count_neg+count_pos)) -C);
            else
              h_lower = -Double.MAX_VALUE;

            //Evaluating the (j-1)th element as cutting point with >
            int count_pos_g=total_pos-count_pos;
            int count_neg_g=total_neg-count_neg;
            double h_greater;
            if (count_pos_g!=0)
              h_greater = count_pos_g * (Utilities.log2(count_pos_g / ( (double) count_neg_g+count_pos_g)) -C);
            else
              h_greater=-Double.MAX_VALUE;

            //Comparing with the best so far
            if (h_lower>h_greater && h_lower > best_h) {
              best_h = h_lower;
              best_v = score.getKey(j);
              best_operator=Rule.LOWER;
            }
            else if (h_greater>best_h){
              best_h = h_greater;
              best_v = score.getKey(j);
              best_operator=Rule.GREATER;
            }

          }
        }

        //Later, test if it is the best couple so far
        if (best_h>best_global){
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
   * It prunes a rule, according with one of two heuristics:
   * W= (p+1)/(t+2)
   * A= (p+n')/T
   * p/t: number of positive/total instances covered by the current rule
   * n': number of negative instances not covered by the current rule (true negatives)
   * T: number of total instances
   * @param rule Rule the rule to prune
   * @param data MyDataset the dataset
   * @param positives Mask active positive entries
   * @param negatives Mask active negative entries
   * @param metric int heuristic's selector (A or W)
   * @return the pruned rule.
   */
  public Rule prune(Rule rule,MyDataset data,Mask positives,Mask negatives,int metric){
    double p,t,T,n,n_prime;
    double h,next_h=0.0;
    p=rule.apply(data,positives);
    T=positives.getnActive()+negatives.getnActive();
    n=rule.apply(data,negatives);
    n_prime=negatives.getnActive()-n;

    if (metric==A){
      next_h=(p+n_prime)/T;
    }
    if (metric==W){
      t=p+n;
      next_h=(p+1)/(t+2);
    }

    do{
      h=next_h;
      p=rule.apply(data,positives,rule.size()-1);
      T=positives.getnActive()+negatives.getnActive();
      n=rule.apply(data,negatives,rule.size()-1);
      n_prime=negatives.getnActive()-n;
      if (metric==A){
        next_h=(p+n_prime)/T;
      }
      if (metric==W){
        t=p+n;
        next_h=(p+1)/(t+2);
      }

      if (h<next_h && rule.size()>1){
        rule.prune(rule.size()-1);
      }
    }while(h<next_h && rule.size()>0 && rule.size()>1);

    return rule;
  }

  /**
   * It implements the algorithm Ripperk itself:
   * 1. In each iteration, it takes the class with less instances in the dataset and
   *    it splits this into positive (those of the taken class) and negative (the rest) instances.
   * 2. Then it invokes Ripper2 to generates a Ruleset for the taken class.
   * 3. Finally, it removes the instances covered by the ruleset and it carries on whit the next iteration.
   * @param data MyDataset the dataset
   * @return a vector with a Ruleset for each class.
   */
  public Ruleset[] ripperMulticlass(MyDataset data){
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
      rules[i]=ripperK(data,positives,negatives);
      rules[i].setType(target_class);
      base=negatives.copy();
    }
    rules[rules.length-1]=new Ruleset();
    rules[rules.length-1].addRule(new Rule());
    rules[rules.length-1].setType(Attributes.getOutputAttribute(0).getNominalValue(ordered_classes[data.getnClasses()-1].key));

    return rules;
  }

  /**
   * It implements the Ripper2's Build Phase:
   * Iteratively, it grows and prunes rules until the descrition length (DL) of the ruleset
   * and examples is 64 bits greater than the smallest DL met so far, or there are
   * no positive examples, or the error rate >= 50%.
   * The prune metric used here is W.
   * @param rules Ruleset the rules generated so far
   * @param data MyDataset the dataset
   * @param pos Mask active positive entries of data
   * @param neg Mask active negative entries of data
   * @return rules with the new grown & pruned rules
   */
  public Ruleset IREPstar(Ruleset rules, MyDataset data, Mask pos, Mask neg){
    double error_ratio,smallest_mdl,new_mdl;
    smallest_mdl=Double.MAX_VALUE-64.0;
    new_mdl=Double.MAX_VALUE;
    Mask positives=pos.copy(),negatives=neg.copy();

    do{
      //Splitting of the two dataset into two prune dataset and two grow dataset
      Mask[] gp_pos=positives.split(pct,rand);
      Mask grow_pos=gp_pos[0], prune_pos=gp_pos[1];
      Mask[] gp_neg=negatives.split(pct,rand);
      Mask grow_neg=gp_neg[0], prune_neg=gp_neg[1];
      //Grow & Prune
      Rule new_rule=grow(data,grow_pos,grow_neg);
      System.out.println("Regla criada\n"+new_rule);
      prune(new_rule,data,prune_pos,prune_neg,Ripper.W);
      System.out.println("Regla podada\n"+new_rule);
      //Estimation of the error ratio
      rules.addRule(new_rule);
      //double errors=new_rule.apply(data,prune_neg);
      //error_ratio=errors/prune_neg.getnActive();
      new_mdl=rules.getMDL(data,positives,negatives);
      if (new_mdl<=smallest_mdl+64){
        System.out.println("Regla añadida\n"+new_rule);
        data.substract(positives,new_rule);
        data.substract(negatives,new_rule);
        if (new_mdl<smallest_mdl)
          smallest_mdl = new_mdl;
      }
      else
        rules.removeRule(rules.size()-1);
    }while(positives.getnActive()>0 && new_mdl<=smallest_mdl+64);

    return rules;
  }

  /**
   * It implements the Ripper2's Optimization Phase:
   * After generating the initial ruleset {Ri},
   * generate and prune two variants of each rule Ri from randomized data
   * using the grow and prune method. But one variant is generated from an empty rule
   * while the other is generated by greedily adding antecedents to the original rule.
   * Moreover, the pruning metric used here is A. Then the smallest possible DL for
   * each variant and the original rule is computed. The variant with the minimal DL
   * is selected as the final representative of Ri in the ruleset. [WEKA]
   * @param rules Ruleset the rules from the build phase
   * @param data MyDataset the dataset
   * @param positives Mask active positive entries
   * @param negatives Mask active negative entries
   * @return the optimized rules
   */
  public Ruleset optimize(Ruleset rules,MyDataset data,Mask positives,Mask negatives){
    for (int i=0;i<rules.size();i++){

      //Splitting of the two dataset into two prune dataset and two grow dataset
      Mask[] gp_pos=positives.split(pct,rand);
      Mask grow_pos=gp_pos[0], prune_pos=gp_pos[1];
      Mask[] gp_neg=negatives.split(pct,rand);
      Mask grow_neg=gp_neg[0], prune_neg=gp_neg[1];

      //Removing from the pruning set of all instances that are covered by the other rules
      data.substract(prune_pos,rules,i);
      data.substract(prune_neg,rules,i);

      //Creation of the competing rules
      Rule revision=grow(data,grow_pos,grow_neg); //from scratch
      Rule replacement=rules.getRule(i).getCopy();
      grow(replacement,data,grow_pos,grow_neg); //from the current rule
      prune(revision,data,prune_pos,prune_neg,Ripper.A);
      prune(replacement,data,prune_pos,prune_neg,Ripper.A);

      //Select the representative
      Rule current=rules.getRule(i);
      double current_mdl=rules.getMDL(data,positives,negatives);
      rules.removeRule(i);
      rules.insertRule(revision,i);
      double revision_mdl=rules.getMDL(data,positives,negatives);
      rules.removeRule(i);
      rules.insertRule(replacement,i);
      double replacement_mdl=rules.getMDL(data,positives,negatives);
      rules.removeRule(i);
      if (current_mdl<=revision_mdl && current_mdl<=replacement_mdl)
        rules.insertRule(current,i);
      else if(revision_mdl<=replacement_mdl)
        rules.insertRule(revision,i);
      else
        rules.insertRule(replacement,i);

    }

    return rules;
  }

  /**
   * It implements the algorithm Ripper2:
   * 1. Build Phase
   * 2. Optimization Phase
   * 3. MOP UP: If there are not covered entries, repeat the Build Phase for these entries.
   * 4. CLEAN UP: Remove those rules that increase the description's length (DL)
   * @param data MyDataset the dataset
   * @param positives Mask active positive entries
   * @param negatives Mask active negative entries
   * @return the generated ruleset
   */
  public Ruleset ripperK(MyDataset data,Mask positives,Mask negatives){
    Ruleset rules=new Ruleset();

    /**********************Growing & Prunning***************************************/

    IREPstar(rules,data,positives,negatives);

    for (int i=0;i<K;i++){
      /**********************Optimization********************************************/
      optimize(rules, data, positives, negatives);
      /*************************************MOP UP******************************/
      Mask p = positives.copy();
      data.substract(p, rules);
      if (p.getnActive() > 0) {
        IREPstar(rules, data, p, negatives);
      }
    }
    /*************************************CLEAN UP******************************/

    rules.removeDuplicates();
    rules.pulish(data,positives,negatives);

    return rules;
  }

  public MyDataset getData(){return train;}

}
