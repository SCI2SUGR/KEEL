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
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 12/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Rule_Learning.C45Rules;

import java.util.Vector;
import org.core.*;


class C45Rules {
/**
 * <p>
 * Class to implement the C4.5Rules algorithm
 * </p>
 */
	
  //Inputs & Outputs
  //the datasets for training, validation and test
  MyDataset train, val, test; 
  //the names for the output files
  String outputTr, outputTst, outputRules; 

  //General
  private Tree root; //C4.5 tree
  // private MyDataset data;
  //generated rules
  private Ruleset[] classification_rules; 
  //name of the default class
  private String default_class_name; 

  //Class filters
  //filter of each class
  Mask[] class_filter; 
  //inverse filter of each class
  //Options
  Mask[] inverse_class_filter; 
  private static int GREEDY=0;
  private static int SA=1;
  //Algorithm for searching: greedy or simulated anneling
  private int SearchAlgorithm; 
  //Maximum size of a ruleset for wich the Exhaustive Search is used
  private int treshold; 
  //Confidence level for prunning
  private double CF; 
  //Simulated Anneling Options
  //Number of coldings
  private int Nmax=10; 
  //Number of neighbours per temperature level
  private int max_trials=5; 
  //used in the establishing of the initial temperature
  private double mu=0.5; 
  //used in the establishing of the initial temperature
  private double phi=0.5; 
  //colding speed
  private double alpha=0.5; 

  Randomize rand;

  /********************************PRIVATE METHODS************************************/

  /**
   * Returns a vector of random generated integers in a given (closed) interval
   * @param n int number of numbers to generate
   * @param low int lowest number of the interval (include)
   * @param high int highest number of the interval (include)
   * @return a vector of n random generated integers between low and high (both include)
   */
  private Vector getRandomNumbers(int n,int low,int high){
    Vector random=new Vector();
    int[] numbers=new int[high-low+1];
    for (int i=low;i<=high;i++)
      numbers[i-low]=i;
    int remained=high-low+1;
    for (int i=0;i<n;i++){
      int new_number=Randomize.Randint(1,remained);
      random.add(new Integer(numbers[new_number]));
      numbers[new_number]=numbers[remained-1];
      remained--;
    }
    return random;
  }

  /**
   * Extract recursively the rules out of a tree
   * @param node Tree the current node in cosideration
   * @param base_rule Rule the rule that generates the father of the node
   * @param link_to_father SimpleRule the simple rule that connects the node with its father
   * @param type String the class label for this node (only if the node is a leaf)
   * @return an array with all the rules extracted from the leafs of the subtree for wich the node is root
   */
  private Vector convert(Tree node,Rule base_rule,SimpleRule link_to_father,String type){

    //1.Producing of the rule linked to this node: node rule <- father rule + path rule
    Rule node_rule=base_rule.getCopy();
    if (link_to_father!=null)
      node_rule.grow(link_to_father);

    //2.Checking wether this node is a leaf
    Vector output=new Vector();
    if (node.isLeaf){
      //2.A If so, adding the node rule to the ruleset
      node_rule.setType(type);
      output.add(node_rule);
    }
    else{
      //2.B Else, compacting the ruleset linked to the subtrees
      int cut_attribute=node.nodeModel.attributeIndex();
      int class_index=train.getClassIndex();
      if (cut_attribute>class_index){
        cut_attribute++;
      }
      for (int i=0;i<node.getNChildren();i++){
        SimpleRule link_child=new SimpleRule();
        link_child.setAttribute(cut_attribute);
        if (train.getAttribute(cut_attribute).isDiscret()){
          link_child.setValue(i);
          link_child.setOperator(SimpleRule.EQUAL);
        }
        else{
          link_child.setValue(node.nodeModel.getCutPoint());
          if (i==0)
            link_child.setOperator(SimpleRule.LOWER);
          else
            link_child.setOperator(SimpleRule.GREATER);
        }
        String child_type="";
        if (node.getChild(i).isLeaf)
          child_type=node.nodeModel.label(i,train);
        Vector child_rules=convert(node.getChild(i),node_rule,link_child,child_type);
        output.addAll(child_rules);
      }
    }
    return output;
  }

  /**
   * Returns the masks generated by each rule from a ruleset
   * @param rules Ruleset the ruleset
   * @return the masks generated by each rule from a ruleset
   */
  private Mask[] getAllMasks(Ruleset rules){
    Mask[] output=new Mask[rules.size()];
    for (int i=0;i<rules.size();i++){
      output[i]=new Mask(train.size());
      train.filter(output[i],(Rule) rules.getRule(i));
    }
    return output;
  }

  /**
   * Makes recursively an exhaustive search of all posible subsets of a given ruleset
   * and returns the numbers of the rules that makes the one with the lowest MDL.
   * Each call to the method takes into account a combination (card) of rules form by the combination
   * of rules of the method that call it plus a new rule that it is not yet in the combination.
   * So, new card=base card + next rule
   * @param pool Ruleset All the available rules
   * @param all_masks Mask[] The mask of each one of the pool's rules
   * @param next_rule int the number of the next rule to considerate in this method
   * @param base_card int[] the number of the rules that has been considerated in the previous method
   * @param base_card_length int the length of the base card
   * @param base_card_theory_cost double The theory cost (for the DL) of the rules from the base card.
   * @param base_mask Mask Mask generated by all the rules in the base card
   * @param class_value int the target class
   * @return the numbers of the rules that makes the ruleset with the lowest MDL.
   */
  private Report allCombinations(Ruleset pool,Mask[] all_masks,int next_rule,
                                 int[] base_card,int base_card_length,double base_card_theory_cost,
                                 Mask base_mask, int class_value){
    Mask class_mask=class_filter[class_value], inverse_class_mask=inverse_class_filter[class_value];
    //it generates the "card" for this node with the base card information and the next rule
    int[] new_card=new int[base_card.length];
    int new_card_length=base_card_length+1;
    for (int i=0;i<base_card_length;i++)
      new_card[i]=base_card[i];
    new_card[base_card_length]=next_rule;
    //it filters the data with the new rule
    Mask new_mask=base_mask.or(all_masks[next_rule]);
    //now it generates the new stats
    int tp=new_mask.and(class_mask).getnActive(); //true positives
    int fp=new_mask.and(inverse_class_mask).getnActive(); //false positives
    int fn=class_mask.getnActive()-tp; //false negatives
    int tn=inverse_class_mask.getnActive()-fp; //true negatives
    double new_card_theory_cost=base_card_theory_cost+pool.getRule(next_rule).theoryDL(train);
    double new_card_value=new_card_theory_cost+Rule.getExceptionCost(train,tp,tn,fp,fn);

    //It initialize the best_report, that will store the best report so far
    Report best_report=new Report(new_card,new_card_length,new_card_value);
    for (int i=next_rule+1;i<pool.size();i++){
      Report current=allCombinations(pool,all_masks,i,new_card,new_card_length,new_card_theory_cost,new_mask,class_value);
      if(current.getValue()<best_report.getValue())
        best_report=current;
    }

    return best_report;
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
    for (int i = 0; i < dataset.size(); i++) {
      String class_name=dataset.getClassAttribute().value((int)dataset.itemset(i).getClassValue());
      output += class_name + " " +classification[i] + "\n";
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
      for(int j=0;j<rulesets[i].size();j++){
        output+="if(";
        Rule current=rulesets[i].getRule(j);
        for (int k=0;k<current.size();k++){
          output+=current.getSimpleRule(k);
          if (k!=current.size()-1) output+=" && ";
        }
        int class_id=train.getClassAttribute().valueIndex(rulesets[i].getType());
        int covered=current.apply(train);
        int accuracy=current.apply(train,class_filter[class_id]);
        output+=") ("+accuracy+"/"+covered+")\n\t";
        output+="output="+rulesets[i].getType()+"\nelse ";
      }
    }
    output+="\n\toutput="+rulesets[rulesets.length-1].getType();
    Fichero.escribeFichero(filename, output);
  }


  /************************************************************************************/


  /**
   * Constructor
   * @param root Tree the C45 tree from wich the algorithm will extract the rules
   * @param train MyDataset the training dataset from wich the tree has been extracted
   * @param seed long seed for the random numbers generator
   * @param CF double Confidence level for prunning
   * @param treshold int maximum size of a ruleset for wich the Exhaustive Search is used
   */
/*  public C45Rules(Tree root,MyDataset train,long seed,double CF,int treshold){
    this.root=root;
    this.data=train;
    this.treshold=treshold;
    Randomize.setSeed(seed);
    classification_rules=null;
    default_class_name=null;
    SearchAlgorithm=GREEDY;
    this.CF=CF;
  }*/

  /**
   * Constructor for Simulated Annealig Option
   * @param root Tree the C45 tree from wich the algorithm will extract the rules
   * @param train MyDataset the training dataset from wich the tree has been extracted
   * @param seed long seed for the random numbers generator
   * @param treshold int maximum size of a ruleset for wich the Exhaustive Search is used
   * @param CF double Confidence level for prunning
   * @param Nmax int number of coolings
   * @param max_trials int max number of neighbours
   * @param mu double a param between [0,1] for establishing the initial temperature
   * @param phi double a param between [0,1] for establishing the initial temperature
   * @param alpha double a param between [0,1] that determine the speed of cooling
   */
/*  public C45Rules(Tree root,MyDataset train,long seed,double CF,int treshold, int Nmax,int max_trials,double mu,double phi,double alpha){
    this.root=root;
    this.data=train;
    this.treshold=treshold;
    Randomize.setSeed(seed);
    classification_rules=null;
    default_class_name=null;
    SearchAlgorithm=SA;
    this.CF=CF;
  }*/

  /**
   * Constructor for Simulated Annealig Option
   * @param root Tree the C45 tree from wich the algorithm will extract the rules
   * @param parameters parseParameters the algorithm's parameters
   */
  public C45Rules(Tree root, parseParameters parameters){
    this.root=root;

    //Files
    String trainFileName=parameters.getTrainingInputFile();
    String valFileName=parameters.getValidationInputFile();
    String testFileName=parameters.getTestInputFile();

    train = new MyDataset(trainFileName,false);
    val = new MyDataset(valFileName,false);
    test = new MyDataset(testFileName,false);

    outputTr = parameters.getTrainingOutputFile();
    outputTst = parameters.getTestOutputFile();
    outputRules = parameters.getOutputFile(0);

    long seed = Long.parseLong(parameters.getParameter(0));
    //rand=new Randomize();
    Randomize.setSeed(seed);

    //Options
    treshold=Integer.parseInt(parameters.getParameter(3)); //Maximum size of a ruleset for wich the Exhaustive Search is used
    CF=Double.parseDouble(parameters.getParameter(1)); //confidence level for the uniform distribution
    SearchAlgorithm=GREEDY;

    if (CF < 0 || CF > 1) {
      CF = 0.25F;
      System.err.println("Error: confidence must be in the interval [0,1]");
      System.err.println("Using default value: 0.25");
    }
    if (treshold <= 0) {
      treshold = 10;
      System.err.println("Error: treshold must be greater than 0");
      System.err.println("Using default value: 10");
    }

    classification_rules=null;
    default_class_name=null;

  }

  /**
   * It coverts a given C4.5 tree into an array of rules.
   * @return an array of rules
   */
  public Vector treeToRules(){
    Vector output=null;

    if (!root.isLeaf){
      output = convert(root, new Rule(),null,"");
    }
    return output;
  }

  public void pruneRule(Rule rule){
    int class_value=train.getClassAttribute().valueIndex(rule.getType());
    Mask class_mask=class_filter[class_value];
    Mask inverse_class_mask=inverse_class_filter[class_value];
    int tp=rule.apply(train,class_mask);
    int fp=rule.apply(train,inverse_class_mask);
    double current_U,next_U=(fp+Extra.AddErrs(tp+fp,fp,CF))/(tp+fp);
    int to_prune=-1;
    boolean seguir_podando;
    do{
      current_U=next_U;
      seguir_podando=false;

      for (int i=0;i<rule.size();i++){

        int tp_i = rule.apply(train, class_mask, i);
        int fp_i = rule.apply(train, inverse_class_mask, i);
        double U_i = (fp_i + Extra.AddErrs(tp_i+fp_i, fp_i, CF)) / (tp_i + fp_i);
        if (U_i<=next_U){
          to_prune=i;
          next_U=U_i;
          seguir_podando=true;
        }
      }

      if (seguir_podando){
        rule.prune(to_prune);
      }

    }while(seguir_podando && rule.size()>0);

  }

  /**
   * Removes the duplicated rules from a vector of rules
   * @param rules Vector vector of rules
   */
  public void removeDuplicates(Vector rules){

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
   * Takes an array of rules and makes sets of rules according to the right side
   * @param rules an array of rules
   * @return an array of data.numClasses rulesets, the rules of each one have the same right side
   */
  public Ruleset[] classifyRules(Vector rules){
    Ruleset[] groups=new Ruleset[train.numClasses()];
    //it assigns a class to each ruleset
    for (int i=0;i<groups.length;i++){
      groups[i]=new Ruleset();
      groups[i].setType(train.getAttribute(train.getClassIndex()).value(i));
    }
    //it assign a ruleset for each rule, according to its right side
    while (rules.size()>0){
      String class_name=((Rule) rules.elementAt(0)).getType();
      int class_index=train.getAttribute(train.getClassIndex()).valueIndex(class_name);
      groups[class_index].addRule((Rule) rules.elementAt(0));
      rules.remove(0);
    }

    return groups;
  }

  /**
   * Makes an exhaustive search of all posible subsets of a given ruleset
   * and returns the one with the lowest MDL.
   * @param rules Ruleset the ruleset
   * @return returns the subset of rules with the lowest MDL.
   */
  public Ruleset exhaustiveSearch(Ruleset rules){
    Mask[] all_masks=getAllMasks(rules); //it contains the masks of each rule
    int class_value=train.getClassAttribute().valueIndex(rules.getType());
    Mask blank=new Mask(train.size(),false); //a mask with all the exemples off

    //Initial call to the recursive method
    Report best_report=allCombinations(rules,all_masks,0,new int[rules.size()],0,0.0,blank,class_value);
    for (int i=1;i<rules.size();i++){
      Report current=allCombinations(rules,all_masks,i,new int[rules.size()],0,0.0,blank,class_value);
      if(current.getValue()<best_report.getValue())
        best_report=current;
    }

    //Construction of the final ruleset with the selected rules
    Ruleset selected_rules=new Ruleset();
    selected_rules.setType(rules.getType());
    for (int i=0;i<best_report.length();i++){
      selected_rules.addRule(rules.getRule(best_report.get(i)));
    }

    return selected_rules;
  }

  /**
   * Makes an greedy search to find the best subsets of a given ruleset
   * @param rules Ruleset the ruleset
   * @return the best found subset
   */
  public Ruleset greedySearch(Ruleset rules){
    Mask[] all_masks=getAllMasks(rules); //it contains the masks of each rule
    int class_value=train.getClassAttribute().valueIndex(rules.getType());
    Mask class_mask=class_filter[class_value];
    Mask inverse_class_mask=inverse_class_filter[class_value];
    Report best_report=null;

    for (double pct=0.1;pct<=1.0;pct+=0.1){
      //************1.Construction of the base combination************//
      int base_length=(int)Math.ceil(pct*rules.size());
      int[] base_card=new int[base_length];
      IncrementalMask base_mask=new IncrementalMask(train.size());
      int[] exclude_rules=new int[rules.size()];
      for (int i=0;i<rules.size();i++)
        exclude_rules[i]=i;
      int remained=rules.size();
      //generation of the base card itself
      double theory_cost=0.0;
      for (int i=0;i<base_length;i++){
        int new_number=Randomize.Randint(0,remained);
        base_card[i]=exclude_rules[new_number];
        base_mask=base_mask.plus(all_masks[base_card[i]]);
        exclude_rules[new_number]=exclude_rules[remained-1];
        remained--;
        theory_cost+=rules.getRule(base_card[i]).theoryDL(train);
      }
      //now it generates the stats for the base combination
      int tp=base_mask.and(class_mask).getnActive(); //true positives
      int fp=base_mask.and(inverse_class_mask).getnActive(); //false positives
      int fn=class_mask.getnActive()-tp; //false negatives
      int tn=inverse_class_mask.getnActive()-fp; //true negatives
      double base_card_value=theory_cost+Rule.getExceptionCost(train,tp,tn,fp,fn);

      //************2.Evaluation of the neighbourhood: first, deleting rules************//
      Report base_report=new Report(base_card,base_length,base_card_value);
      if (best_report==null || base_report.getValue()<best_report.getValue())
        best_report=base_report;
      for (int i=0;i<base_report.length();i++){
        int rule_index=base_report.get(i);
        IncrementalMask without_rulei=base_mask.minus(all_masks[rule_index]);
        //now it generates the stats for the combination without the rule i
        tp=without_rulei.and(class_mask).getnActive(); //false positives
        fp=without_rulei.and(inverse_class_mask).getnActive(); //true positives
        fn=class_mask.getnActive()-tp; //false negatives
        tn=inverse_class_mask.getnActive()-fp; //true negatives
        double theory_cost_without_i=theory_cost-rules.getRule(rule_index).theoryDL(train);
        double without_rulei_value=theory_cost_without_i+Rule.getExceptionCost(train,tp,tn,fp,fn);
        if (without_rulei_value<best_report.getValue()){
          int[] new_card=new int[base_report.length()-1];
          for (int j=0;j<i;j++) new_card[j]=base_report.get(j);
          for (int j=i+1;j<base_report.length();j++) new_card[j-1]=base_report.get(j);
          best_report=new Report(new_card,base_report.length()-1,without_rulei_value);
        }
      }

      //************3.Evaluation of the neighbourhood: now, adding rules************//
      for (int i=0;i<remained;i++){
        int rule_index=exclude_rules[i];
        IncrementalMask with_rulei=base_mask.plus(all_masks[rule_index]);
        //now it generates the stats for the combination without the rule i
        tp=with_rulei.and(class_mask).getnActive(); //true positives
        fp=with_rulei.and(inverse_class_mask).getnActive(); //false positives
        fn=class_mask.getnActive()-tp; //false negatives
        tn=inverse_class_mask.getnActive()-fp; //true negatives
        double theory_cost_with_i=theory_cost+rules.getRule(rule_index).theoryDL(train);
        double with_rulei_value=theory_cost_with_i+Rule.getExceptionCost(train,tp,tn,fp,fn);
        if (with_rulei_value<best_report.getValue()){
          int[] new_card=new int[base_report.length()+1];
          for (int j=0;j<base_length;j++) new_card[j]=base_report.get(j);
          new_card[base_report.length()]=rule_index;
          best_report=new Report(new_card,base_report.length()+1,with_rulei_value);
        }
      }

    }
    //Construction of the final ruleset with the selected rules
    Ruleset selected_rules=new Ruleset();
    selected_rules.setType(rules.getType());
    for (int i=0;i<best_report.length();i++){
      selected_rules.addRule(rules.getRule(best_report.get(i)));
    }

    return selected_rules;
  }

  /**
   * Makes a Simulated Annealing search to find the best subsets of a given ruleset.
   * @param rules Ruleset the ruleset
   * @param Nmax int number of coolings
   * @param max_trials int max number of neighbours
   * @param mu double a param between [0,1] for establishing the initial temperature
   * @param phi double a param between [0,1] for establishing the initial temperature
   * @param alpha double a param between [0,1] that determine the speed of cooling
   * @return the best found subset
   */
  public Ruleset simulatedAnnealing(Ruleset rules, int Nmax,int max_trials,double mu,double phi,double alpha){
    Mask[] all_masks=getAllMasks(rules); //it contains the masks of each rule
    int class_value=train.getClassAttribute().valueIndex(rules.getType());
    Mask class_mask=class_filter[class_value];
    Mask inverse_class_mask=inverse_class_filter[class_value];

    //**************1.Generation of the initial combination****************************//
    int initial_length=Randomize.Randint(0,rules.size());
    int[] initial_card=new int[initial_length];
    IncrementalMask initial_mask=new IncrementalMask(train.size());
    // all_rules -> {exclude_rules|include rules}
    //              |<-remained-->|
    int[] all_rules=new int[rules.size()];
    for (int i=0;i<rules.size();i++)
      all_rules[i]=i;
    int remained=rules.size();
    //generation of the initial card itself
    double theory_cost=0.0;
    for (int i=0;i<initial_length;i++){
      int new_number=Randomize.Randint(0,remained);
      initial_card[i]=all_rules[new_number];
      initial_mask=initial_mask.plus(all_masks[initial_card[i]]);
      //The selected number goes to the "include rules section"
      int aux=all_rules[new_number];
      all_rules[new_number]=all_rules[remained-1];
      all_rules[remained-1]=aux;
      remained--;
      theory_cost+=rules.getRule(initial_card[i]).theoryDL(train);
    }
    //now it generates the stats for the initial combination
    int tp=initial_mask.and(class_mask).getnActive(); //true positives
    int fp=initial_mask.and(inverse_class_mask).getnActive(); //false positives
    int fn=class_mask.getnActive()-tp; //false negatives
    int tn=inverse_class_mask.getnActive()-fp; //true negatives
    double initial_card_value=theory_cost+Rule.getExceptionCost(train,tp,tn,fp,fn);


    //**************2.Main Loop****************************************************//
    Report best_report=new Report(initial_card,initial_length,initial_card_value);
    IncrementalMask current_mask=initial_mask;
    double current_value=initial_card_value;
    double t=(mu-Math.log(phi))*initial_card_value; //Initial temperature
    boolean success=true;
    int max_succeses=(int) 0.1*max_trials;
    for (int iter=0;iter<Nmax && success;iter++){
      int nsuccesses=0;
      for (int trial=0;trial<max_trials && nsuccesses<max_succeses;trial++){

        //*****Candidate generation********//
        int next=Randomize.Randint(0,rules.size());
        int rule_index=all_rules[next];
        IncrementalMask next_mask=null;
        double new_theory_cost=theory_cost;
        if(next<remained){
          //next belongs to the "exclude rules section" so we include it
          next_mask=current_mask.plus(all_masks[rule_index]);
          new_theory_cost+=rules.getRule(rule_index).theoryDL(train);
        }
        else{
          //next belongs to the "include rules section" so we exclude it
          next_mask=current_mask.minus(all_masks[rule_index]);
          new_theory_cost-=rules.getRule(rule_index).theoryDL(train);
        }
        //now it generates the stats for the candidate
        tp=next_mask.and(class_mask).getnActive(); //false positives
        fp=next_mask.and(inverse_class_mask).getnActive(); //true positives
        fn=class_mask.getnActive()-tp; //false negatives
        tn=inverse_class_mask.getnActive()-fp; //true negatives
        double next_value=new_theory_cost+Rule.getExceptionCost(train,tp,tn,fp,fn);


        //********Admission**************//
        double delta=next_value-current_value;
        double rand=Randomize.Rand();
        if(next_value<current_value || rand<Math.exp(-delta/t)){
          //current<-next
          if (next<remained){
            //adding rule
            int aux = all_rules[next];
            all_rules[next] = all_rules[remained - 1];
            all_rules[remained - 1] = aux;
            remained--;
            current_mask=current_mask.plus(all_masks[rule_index]);
          }
          else{
            //removing rule
            int aux = all_rules[next];
            all_rules[next] = all_rules[remained];
            all_rules[remained] = aux;
            remained++;
            current_mask=current_mask.minus(all_masks[rule_index]);
          }
          if (next_value<current_value){
            nsuccesses++;
            success=true;
          }
          current_value=next_value;
          theory_cost=new_theory_cost;

          //current<best?=>best<-current
          if (current_value<best_report.getValue()){
            int [] new_best_card=new int[rules.size()-remained];
            for(int i=0;i<rules.size()-remained;i++){
              new_best_card[i]=all_rules[remained+i];
            }
            best_report=new Report(new_best_card,rules.size()-remained,current_value);
          }
        }
      }
      t=alpha*t;
    }

    //Construction of the final ruleset with the selected rules
    Ruleset selected_rules=new Ruleset();
    selected_rules.setType(rules.getType());
    for (int i=0;i<best_report.length();i++){
      selected_rules.addRule(rules.getRule(best_report.get(i)));
    }

    return selected_rules;
  }

  /**
   * Sorts the rulesets according to the false positive value of each one,
   * and selects the default class.
   * @param rulesets Ruleset[] the rulesets to sort
   * @param all_ruleset_masks Masks[] the masks with the covered exemple of each ruleset
   * @return the default class
   */
  public String sortingRulesets(Ruleset[] rulesets,Mask[] all_ruleset_masks){

    //Sorting
    Mask filter=new Mask(train.size());
    for (int i=0;i<train.numClasses()-1;i++){
      int best_candidate=-1;int best_fp=train.size()+1;
      for (int j = i; j < train.numClasses(); j++) {
        int class_value = train.getClassAttribute().valueIndex(rulesets[j].getType());
        Mask candidate_mask=all_ruleset_masks[class_value].and(filter);
        int candidate_fp=candidate_mask.and(inverse_class_filter[class_value]).getnActive();
        if (candidate_fp<best_fp){
          best_candidate=j;
          best_fp=candidate_fp;
        }

      }
      //Swap the best with the i-th position
      Ruleset aux=rulesets[i];
      rulesets[i]=rulesets[best_candidate];
      rulesets[best_candidate]=aux;
      int class_value =train.getClassAttribute().valueIndex(rulesets[i].getType());
      filter=filter.and(all_ruleset_masks[class_value]).complement();
    }
    //Substracting the last ruleset
    int class_value = train.getClassAttribute().valueIndex(rulesets[train.numClasses()-1].getType());
    filter=filter.and(all_ruleset_masks[class_value]).complement();

    //Selecting the default class
    int[] remained_class_frequency=train.getClassFequency(filter);
    int[] class_frequency=train.getClassFequency();
    int higher_rel_freq=-1;int higher_freq=-1;int default_class=-1;
    for (int i=0;i<train.numClasses();i++){
      if(remained_class_frequency[i]>higher_rel_freq){
        higher_rel_freq=remained_class_frequency[i];
        higher_freq=class_frequency[i];
        default_class=i;
      }
      else if (remained_class_frequency[i]==higher_freq && class_frequency[i]>higher_freq){
        higher_rel_freq=remained_class_frequency[i];
        higher_freq=class_frequency[i];
        default_class=i;
      }
    }

    return train.getClassAttribute().value(default_class);
  }

  /**
   * Runs the algorithm
   */
  public void executeAlgorithm(){

    //Phase Zero: Constructing the class filters
    class_filter=new Mask[train.numClasses()];
    inverse_class_filter=new Mask[train.numClasses()];
    for (int i=0;i<train.numClasses();i++){
      class_filter[i]=new Mask(train.size());
      String class_name=train.getClassAttribute().value(i);
      train.filterByClass(class_filter[i],class_name);
      inverse_class_filter[i]=class_filter[i].complement();
    }

    System.out.println("1.Original Rules:");
    //Phase One: Tree to Rules
    Vector rules=treeToRules();

    for (int i=0;i<rules.size();i++)
      System.out.println((Rule) rules.elementAt(i));

    System.out.println("2.Pruned Rules:");
    //Phase Two: Prune rules
    for (int i=0;i<rules.size();i++){
      pruneRule( (Rule) rules.elementAt(i));
    }

    for (int i=0;i<rules.size();i++)
      System.out.println((Rule) rules.elementAt(i));

    //Phase Three: Removing duplicates
    removeDuplicates(rules);

    System.out.println("3.Rules without duplicates:");
    for (int i=0;i<rules.size();i++)
      System.out.println((Rule) rules.elementAt(i));

    //Phase Four: Each rule in its right ruleset
    Ruleset[] rulesets=classifyRules(rules);

    System.out.println("4.Classified Rules:");
    for (int i=0;i<train.numClasses();i++){
      System.out.println("Ruleset "+rulesets[i].getType()+":");
      for (int j=0;j<rulesets[i].size();j++)
      System.out.println(rulesets[i].getRule(j)+"->t:"+rulesets[i].getRule(j).theoryDL(train));
    }

    //Phase Five: Getting the final subsets of rules
    Ruleset[] final_rulesets=new Ruleset[train.numClasses()+1];
    for (int i=0;i<train.numClasses();i++){
      if (rulesets[i].size()>0){
        if (rulesets[i].size() < treshold) //Exhaustive Search
          final_rulesets[i] = exhaustiveSearch(rulesets[i]);
        else if (SearchAlgorithm == GREEDY) //Greedy Search
          final_rulesets[i] = greedySearch(rulesets[i]);
        else //Simulated Anneling Search
          final_rulesets[i] = simulatedAnnealing(rulesets[i], Nmax, max_trials,mu, phi, alpha);
      }
      else
        final_rulesets[i]=rulesets[i];
    }

    System.out.println("5.Remaining Rules:");
    for (int i=0;i<train.numClasses();i++){
      if (final_rulesets[i]!=null){
        System.out.println("Ruleset " + final_rulesets[i].getType() + ":");
        for (int j = 0; j < final_rulesets[i].size(); j++)
          System.out.println(final_rulesets[i].getRule(j));
      }
    }

    //Phase Six: Sorting the rulesets
    //Mask[][] all_masks=new Mask[rulesets.length][];
    Mask[] all_ruleset_masks=new Mask[train.numClasses()];
    for (int i=0;i<train.numClasses();i++){
      int class_value = train.getClassAttribute().valueIndex(final_rulesets[i].getType());
      Mask[] ruleset_mask=getAllMasks(final_rulesets[i]);
      all_ruleset_masks[class_value]=new Mask(train.size(),false);
      all_ruleset_masks[class_value]=all_ruleset_masks[class_value].or(ruleset_mask);
    }

    this.default_class_name=sortingRulesets(final_rulesets,all_ruleset_masks);

    System.out.println("6.Sorted Rules:");
    for (int i=0;i<train.numClasses();i++){
      System.out.println(i+"- Ruleset: "+final_rulesets[i].getType());
      for (int j=0;j<final_rulesets[i].size();j++)
        System.out.println(final_rulesets[i].getRule(j));
    }
    System.out.println("Clase por defecto: "+default_class_name);

    //Phase Seven: Polishing
    for (int i=0;i<train.numClasses();i++){
      int class_value=train.getClassAttribute().valueIndex(final_rulesets[i].getType());
      final_rulesets[i].pulish(train,class_filter[class_value],inverse_class_filter[class_value]);
    }

    System.out.println("7.Polish:");
    for (int i=0;i<train.numClasses();i++){
      System.out.println(i+"- Ruleset: "+final_rulesets[i].getType());
      for (int j=0;j<final_rulesets[i].size();j++)
        System.out.println(final_rulesets[i].getRule(j));
    }
    System.out.println("Clase por defecto: "+default_class_name);

    classification_rules=final_rulesets;
    Ruleset dflt=new Ruleset();
    dflt.setType(default_class_name);
    classification_rules[train.numClasses()]=dflt;
  }

  /**
   * It launches the algorithm.
   */
  public void execute() {
    //We do here the algorithm's operations
    this.executeAlgorithm();

    //Classificates the datasets' entries, according the generated rulesets
    String[] classification_train=train.classify(classification_rules,classification_rules.length);
    String[] classification_val=val.classify(classification_rules,classification_rules.length);
    String[] classification_test=test.classify(classification_rules,classification_rules.length);

    //Finally we should fill the training and test output files
    doOutput(this.val, this.outputTr, classification_val);
    doOutput(this.test, this.outputTst, classification_test);
    doRulesOutput2(this.outputRules,classification_rules);
    System.out.println("Algorithm Finished");

  }

}
