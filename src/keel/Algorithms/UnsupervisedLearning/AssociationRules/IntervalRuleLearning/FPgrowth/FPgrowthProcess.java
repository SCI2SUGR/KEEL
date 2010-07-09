package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.FPgrowth;

/**
 * <p>
 * @author Written by Nicolò Flugy Papè (Politecnico di Milano) 24/03/2009
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

import java.util.*;

import keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.FPgrowth.LUCS_KDD.FPtree;

public class FPgrowthProcess {
  /**
   * <p>
   * It provides the implementation of the algorithm to be run in a process
   * </p>
   */
  
  private double minSupport;
  private double minConfidence;
  
  private myDataset dataset;
  private FPtree newFPtree;
  
  /**
   * <p>
   * It creates a new process for the algorithm by setting up its parameters
   * </p>
   * @param dataset The instance of the dataset for dealing with its records
   * @param minSupport The user-specified minimum support for the mined association rules
   * @param minConfidence The user-specified minimum confidence for the mined association rules
   */
  public FPgrowthProcess(myDataset dataset, double minSupport, double minConfidence) {
	  this.minSupport = minSupport;
	  this.minConfidence = minConfidence;
	  
	  this.dataset = dataset;
  }
  
  /**
   * <p>
   * It runs the algorithm for mining association rules
   * </p>
   */
  public void run() {
	  this.newFPtree = new FPtree(this.dataset, this.minSupport, this.minConfidence);
	  
	  this.newFPtree.idInputDataOrdering();
	  this.newFPtree.recastInputDataAndPruneUnsupportedAtts();
	  this.newFPtree.setNumOneItemSets();
	  
	  this.newFPtree.createFPtree();
	  this.newFPtree.startMining();
  }
  
  /**
   * <p>
   * It constructs a rules set once the algorithm has been carried out
   * </p>
   * @return An array of association rules having both minimum confidence and support
   */
  public ArrayList<AssociationRule> generateRulesSet() {
	  return ( this.newFPtree.getRulesSet() );
  }
  
  /**
   * <p>
   * It prints out on screen relevant information regarding the mined association rules
   * </p>
   * @param rules The array of association rules from which gathering relevant information
   */
  public void printReport(ArrayList<AssociationRule> rules) {
	  int r;
	  double avg_sup = 0.0, avg_conf = 0.0, avg_ant_length = 0.0;	  
	  AssociationRule ar;
	  
	  for (r=0; r < rules.size(); r++) {
		  ar = rules.get(r);
		  
		  avg_sup += ar.getRuleSupport();
		  avg_conf += ar.getConfidence();
		  avg_ant_length += ar.getAntecedent().length;
	  }
	  
	  System.out.println("\nNumber of Frequent Itemsets found: " + this.newFPtree.getNumFreqSets());
	  System.out.println("Number of Association Rules generated: " + rules.size());
	  
	  if (! rules.isEmpty()) {
		  System.out.println("Average Support: " + ( avg_sup / rules.size() ));
		  System.out.println("Average Confidence: " + ( avg_conf / rules.size() ));
		  System.out.println("Average Antecedents Length: " + ( avg_ant_length / rules.size() ));
		  System.out.println("Number of Covered Records (%): " + ( (100.0 * this.newFPtree.getCoveredRecords().size()) / this.dataset.getnTrans() ));
	  }
  }
  
}