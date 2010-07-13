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
 * @author Written by Antonio Alejandro Tortosa (University of Granada)  15/10/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.4
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Rule_Learning.PART;

import java.util.Vector;
import org.core.Fichero;


class PART {
/**
 * <p>
 * Class to implement the PART algorithm
 * </p>
 */
	
	
  //Inputs & Outputs
  //the datasets for training, validation and test
  MyDataset train, val, test; 
  //the names for the output files
  String outputTr, outputTst, outputRules; 

  //General
  //generated rules
  private Vector classification_rules;
  //name of the default class
  private String default_class_name; 

  //Options
  //Confidence level for prunning
  private double CF; 
  //Minimum number of itemsets per leaf
  private int minItemsets; 


  /********************************PRIVATE METHODS************************************/

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
    if (node!=null && !node.isUnexplored){
      if (node.isLeaf) {
        //2.A If so, adding the node rule to the ruleset
        node_rule.setType(type);
        output.add(node_rule);
      }
      else {
        //2.B Else, compacting the ruleset linked to the subtrees
        int cut_attribute = node.nodeModel.attributeIndex();
        for (int i = 0; i < node.getNChildren(); i++) {
          SimpleRule link_child = new SimpleRule();
          link_child.setAttribute(cut_attribute);
          if (train.getAttribute(cut_attribute).isDiscret()) {
            link_child.setValue(i);
            link_child.setOperator(SimpleRule.EQUAL);
          }
          else {
            link_child.setValue(node.nodeModel.getCutPoint());
            if (i == 0)
              link_child.setOperator(SimpleRule.LOWER);
            else
              link_child.setOperator(SimpleRule.GREATER);
          }
          String child_type = "";
          if (node.getChild(i)!=null && !node.isUnexplored){
            if ( node.getChild(i).isLeaf)
              child_type = node.nodeModel.label(i, train);
            Vector child_rules = convert(node.getChild(i), node_rule,
                                         link_child,
                                         child_type);
            output.addAll(child_rules);
          }
        }
      }
    }
    return output;
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
      String class_name=dataset.getAttribute(dataset.classIndex).value((int)dataset.itemset(i).getClassValue());
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
   * @param rules Vector the rules
   */
  private void doRulesOutput(String filename,Vector rules) {
    String output = new String("");
    output += "@Number of Rules: "+rules.size()+"\n"; //NUEVO
    for (int i=0;i<rules.size()-1;i++){
      output+="if(";
      Rule current=(Rule) rules.elementAt(i);
      for (int k=0;k<current.size();k++){
        output+=current.getSimpleRule(k);
        if (k!=current.size()-1) output+=" && ";
      }
      Mask class_filter=new Mask(train.size());
      train.filterByClass(class_filter,((Rule) rules.elementAt(i)).getType());
      int covered=current.apply(train);
      int accuracy=current.apply(train,class_filter);
      output+=") ("+accuracy+"/"+covered+")\n\t";
      output+="output="+((Rule) rules.elementAt(i)).getType()+"\nelse ";
    }
    output+="\n\toutput="+((Rule)rules.lastElement()).getType();
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
   * Constructor
   * @param parameters parseParameters the algorithm's parameters
   */
  public PART(parseParameters parameters){

    //Files
    String trainFileName=parameters.getTrainingInputFile();
    String valFileName=parameters.getValidationInputFile();
    String testFileName=parameters.getTestInputFile();

    System.out.println(trainFileName);
    System.out.println(valFileName);
    System.out.println(testFileName);

    train = new MyDataset(trainFileName,true);
    val = new MyDataset(valFileName,false);
    test = new MyDataset(testFileName,false);

    outputTr = parameters.getTrainingOutputFile();
    outputTst = parameters.getTestOutputFile();
    outputRules = parameters.getOutputFile(0);

    //Options
    CF=Double.parseDouble(parameters.getParameter(0)); //confidence level for the uniform distribution
    minItemsets = Integer.parseInt(parameters.getParameter(1)); //itemset per Leaf
    if (CF < 0 || CF > 1) {
      CF = 0.25F;
      System.err.println("Error: confidence must be in the interval [0,1]");
      System.err.println("Using default value: 0.25");
    }
    if (minItemsets <= 0) {
      minItemsets = 2;
      System.err.println("Error: itemsetPerLeaf must be greater than 0");
      System.err.println("Using default value: 2");
    }

    classification_rules=null;
    default_class_name=null;

  }

  /**
   * It coverts a given C4.5 tree into an array of rules.
   * @param tree the C45 tree
   * @return an array of rules
   */
  public Vector treeToRules(Tree tree){
    Vector output=null;

    if (!tree.isLeaf){
      output = convert(tree, new Rule(),null,"");
    }
    else
      output = new Vector();
    return output;
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
   * Runs the algorithm
   * @param remained_data the dataset
   * @throws Exception if there are problems with the algorithm
   */
  public void executeAlgorithm(MyDataset remained_data) throws Exception{

    classification_rules=new Vector();

    boolean end=false;
    while (remained_data.numItemsets()>2*minItemsets+1 && !end){
      C45 subtree=null;
      //Get the subtree
      subtree = new C45(remained_data, true, (float) CF, minItemsets);
      System.out.println("The partial tree\n"+subtree);

      //Get the rules
      Vector rules=treeToRules(subtree.getTree());
      System.out.println("The rules");
      for(int i=0;i<rules.size();i++)
        System.out.println((Rule)rules.elementAt(i));

      //Get the best rule (coverage heuristic)
      int best_rule=-1;
      int best_value=-1;
      for(int i=0;i<rules.size();i++){
        int curr_value=((Rule) rules.elementAt(i)).apply(remained_data);
        if (curr_value>best_value){
          best_rule=i;
          best_value=curr_value;
        }
      }

      //Add the best rule
      //******************************************************
      //Julian - instead of testing if best_rule is not -1,
      //we test if it is greater than 0, since a 0 value will implies
      //no example is covered by the best rule, and then the C4.5 tree
      //is either capable of build a tree which covers any example.
      //The original condition if (best_rule!=-1){ will produce infinite loop
      //if this scenary occurs, since no remaining example is covered, we allways
      //have the same set of remaining examples, for which C4.5 produces the same tree
      //which is not capable of cover any example :(
    //******************************************************
      if (best_rule>0){
        classification_rules.add( (Rule) rules.elementAt(best_rule));

        //Remove the exemples covered by the rule
        MyDataset[] division = remained_data.split( (Rule) rules.elementAt(
            best_rule));
        remained_data = division[1]; //Uncovered exemples
      }
      else
        end=true;

      System.out.println("The Final rules");
      for(int i=0;i<classification_rules.size();i++)
        System.out.println((Rule)classification_rules.elementAt(i));
    }

    //Choosing a default rule
    default_class_name=remained_data.getMostFrequentClass();
    Rule default_rule=new Rule();
    default_rule.setType(default_class_name);
    classification_rules.add(default_rule);

  }

  /**
   * It launches the algorithm.
   * @throws Exception if there are problems with the algorithm
   */
  public void execute() throws Exception{
    //We do here the algorithm's operations
    this.executeAlgorithm(train);

    //Classificates the datasets' entries, according the generated rulesets
    String[] classification_train=train.classify(classification_rules);
    String[] classification_val=val.classify(classification_rules);
    String[] classification_test=test.classify(classification_rules);

    //Finally we should fill the training and test output files
    doOutput(this.val, this.outputTr, classification_val);
    doOutput(this.test, this.outputTst, classification_test);
    doRulesOutput(this.outputRules,classification_rules);
    System.out.println("Algorithm Finished");

  }

}
