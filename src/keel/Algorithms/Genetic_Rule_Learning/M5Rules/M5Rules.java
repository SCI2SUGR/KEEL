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

package keel.Algorithms.Genetic_Rule_Learning.M5Rules;

import java.util.Vector;
import org.core.Fichero;

/**
 * Class to implement the C4.5Rules algorithm
 * @author Antonio Alejandro Tortosa Urdiales (UGR)
 * @author Modified by Victoria Lopez (University of Granada) 03/05/2011
 * @version 1.0 (05-04-08)
 */
class M5Rules {
  public static int COVERAGE=0;
  public static int RMS=1;
  public static int MAE=2;
  public static int CC=3;

  //Inputs & Outputs
  MyDataset train, val, test; //the datasets for training, validation and test
  String outputTr, outputTst, outputRules; //the names for the output files

  //General
  private Vector classification_rules; //generated rules
  private String default_class_name; //name of the default class

  //Options
  private double pruningFactor; //factor for pruning
  private int verbosity; //verbosity level
  private boolean unsmoothed=true;
  private int heuristic = COVERAGE; //heuristic for rule's selection

 /********************************PRIVATE METHODS************************************/

 /**
  * Extract recursively the rules out of a tree
  * @param node Tree the current node in cosideration
  * @param base_rule Rule the rule that generates the father of the node
  * @param link_to_father SimpleRule the simple rule that connects the node with its father
  * @return an array with all the rules extracted from the leafs of the subtree for wich the node is root
  */
 private Vector convert(M5TreeNode node,Rule base_rule,SimpleRule link_to_father){

   //1.Producing of the rule linked to this node: node rule <- father rule + path rule
   Rule node_rule=base_rule.getCopy();
   if (link_to_father!=null)
     node_rule.grow(link_to_father);

   //2.Checking wether this node is a leaf
   Vector output=new Vector();
   if (node!=null){
     if (node.isLeaf()) {
       //2.A If so, adding the node rule to the ruleset
       if (unsmoothed)
         node_rule.setFunction(node.getUnsmoothedFunction());
       else
         node_rule.setFunction(node.getSmoothedFunction());
       output.add(node_rule);
     }
     else {
       //2.B Else, compacting the ruleset linked to the subtrees
       int cut_attribute = node.getSplitingAttribute();

       SimpleRule left_link = new SimpleRule();
       SimpleRule right_link = new SimpleRule();

       left_link.setAttribute(cut_attribute);
       left_link.setValue(node.getSplitingValue());
       right_link.setAttribute(cut_attribute);
       right_link.setValue(node.getSplitingValue());

       left_link.setOperator(SimpleRule.LOWER);
       right_link.setOperator(SimpleRule.GREATER);

       Vector right_rules = convert(node.getRightChild(), node_rule,right_link);
       Vector left_rules = convert(node.getLeftChild(), node_rule,left_link);

       output.addAll(right_rules);
       output.addAll(left_rules);

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
 private void doOutput(MyDataset dataset, String filename,double[] classification) {
   String output = new String("");
   output = dataset.copyHeader(); //we insert the header in the output file
   //We write the output for each example
   for (int i = 0; i < dataset.size(); i++) {
     double class_name=dataset.itemset(i).getClassValue();
     output += class_name + " " +classification[i] + "\n";
   }
   Fichero.escribeFichero(filename, output);
 }

 /**
  * It generates the output rules file from a given ruleset and stores it in a file
  * @param filename String the name of the file
  * @param rules Vector the rules
  */
 private void doRulesOutput(String filename,Vector rules) {
   //String output = new String("@Generated tree \n");
   String output = new String("");

   /*for (int i=0;i<rules.size()-1;i++){
     output+="if(";
     Rule current=(Rule) rules.elementAt(i);
     for (int k=0;k<current.size();k++){
       output+=current.getSimpleRule(k);
       if (k!=current.size()-1) output+=" && ";
     }
     output+=")\n\t";
     output+=((Rule) rules.elementAt(i)).getFunction()+"\nelse ";
   }
   output+="\n\t"+((Rule)rules.lastElement()).getFunction();*/
   
   //output = output + "\n\n@Number of rules: " + rules.size() +"\n\n";
   output = output + "@Number of rules: " + rules.size() +"\n\n";
   for(int i=0;i<rules.size();i++)
       output = output + "Rule " + (i+1) + ": "  +(Rule)rules.elementAt(i) + "\n";
   
   Fichero.escribeFichero(filename, output);
 }


 /************************************************************************************/


 /**
  * Constructor for Simulated Annealig Option.
  * @param paramFile parseParameters the algorithm's parameters.
  * @throws Exception if the class is not numeric.
  */
 public M5Rules(parseParameters paramFile) throws Exception{


   //Input File Names
   String trainFileName=paramFile.getTrainingInputFile();
   String valFileName=paramFile.getValidationInputFile();
   String testFileName=paramFile.getTestInputFile();

   //Output File Names
   outputTr=paramFile.getTrainingOutputFile();
   outputTst=paramFile.getTestOutputFile();
   outputRules=paramFile.getOutputFile(0);

   //Options
   pruningFactor=Double.parseDouble(paramFile.getParameter(0)); //pruning factor (a in (n+a)/(n-k))
   unsmoothed=true; //whether the tree must be smoothed or not
   verbosity = Integer.parseInt(paramFile.getParameter(1)); //verbosity level
   String heuristic_name = paramFile.getParameter(2); //verbosity level
   if (pruningFactor < 0 || pruningFactor > 10) {
     pruningFactor = 2;
     System.err.println("Error: Pruning Factor must be in the interval [0,10]");
     System.err.println("Using default value: 2");
   }
   if (verbosity < 0 || verbosity > 2) {
     verbosity = 0;
     System.err.println("Error: Verbosity must be 0, 1 or 2");
     System.err.println("Using default value: 0");
   }
   if (heuristic_name.equalsIgnoreCase("Coverage"))
     heuristic=COVERAGE;
   else if (heuristic_name.equalsIgnoreCase("RMS"))
     heuristic=RMS;
   else if (heuristic_name.equalsIgnoreCase("MAE"))
     heuristic=MAE;
   else if (heuristic_name.equalsIgnoreCase("CC"))
     heuristic=CC;
   else{
     heuristic=COVERAGE;
     System.err.println("Error: heuristic must be Coverage, RMS, MAE or CC");
     System.err.println("Using default value: Coverage");
   }

   /* Initializes the dataset. */
   train = new MyDataset( trainFileName, true  );
   val = new MyDataset( valFileName, false  );
   test = new MyDataset( testFileName, false  );

   if (train.getClassAttribute().isDiscret()) {
     throw new Exception("Class has to be numeric.");
   }

   classification_rules=null;
   default_class_name=null;

 }

 /**
  * It coverts a given C4.5 tree into an array of rules.
  * @param tree the C45 tree
  * @return an array of rules
  */
 public Vector treeToRules(M5TreeNode tree){
   Vector output=null;

   if (!tree.isLeaf()){
     output = convert(tree, new Rule(),null);
   }
   else{
     Rule r=new Rule();
     if (unsmoothed)
       r.setFunction(tree.getUnsmoothedFunction());
     else
       r.setFunction(tree.getSmoothedFunction());
       output=new Vector();
       output.add(r);
   }
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

   while (remained_data.numItemsets()>0){
     M5 subtree=null;
     //Get the subtree
     subtree = new M5(remained_data, pruningFactor, unsmoothed, verbosity);
     System.out.println("The partial tree\n"+subtree);

     //Get the rules
     Vector rules=treeToRules(subtree.getTree());
     System.out.println("The rules");
     for(int i=0;i<rules.size();i++)
       System.out.println((Rule)rules.elementAt(i));


     //Get the best rule (coverage heuristic)
     int best_rule=-1;
     double best_value=Double.MAX_VALUE;
     for(int i=0;i<rules.size();i++){
       double curr_value=Double.MAX_VALUE;
       Rule ri = (Rule) rules.elementAt(i);
       if (heuristic==COVERAGE)
         curr_value = -ri.apply(remained_data);
       else  if (heuristic==RMS)
         curr_value = remained_data.ruleDeviation(ri)/remained_data.classSTD();
       else  if (heuristic==MAE)
         curr_value = remained_data.ruleMeanAbsoluteError(ri);
       else  if (heuristic==CC)
         curr_value = remained_data.ruleCorrelation(ri);
       if (curr_value<best_value){
         best_rule=i;
         best_value=curr_value;
       }
     }

     //Add the best rule
     classification_rules.add((Rule) rules.elementAt(best_rule));

     //Remove the exemples covered by the rule
     MyDataset[] division=remained_data.split((Rule) rules.elementAt(best_rule));
     remained_data=division[1]; //Uncovered exemples

   }

   //Choosing a default rule
   //default_class_name=train.getMostFrequentClass();
   //Rule default_rule=new Rule();
   //default_rule.setType(default_class_name);
   //classification_rules.add(default_rule);

 }

 /**
  * It launches the algorithm.
  * @throws Exception if there are problems with the algorithm
  */
 public void execute() throws Exception{
   //We do here the algorithm's operations
   this.executeAlgorithm(train);

   //Classificates the datasets' entries, according the generated rulesets
   double[] classification_train=train.classify(classification_rules);
   double[] classification_val=val.classify(classification_rules);
   double[] classification_test=test.classify(classification_rules);

   //Finally we should fill the training and test output files
   doOutput(this.val, this.outputTr, classification_val);
   doOutput(this.test, this.outputTst, classification_test);
   doRulesOutput(this.outputRules,classification_rules);
   System.out.println("Algorithm Finished");

 }

}