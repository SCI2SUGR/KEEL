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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.GeneticFuzzyApriori;

/**
 * <p>
 * @author Written by Alvaro Lopez
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

import java.util.*;
import org.core.Randomize;

public class GeneticFuzzyAprioriProcess {
  /**
   * <p>
   * It provides the implementation of the algorithm to be run in a process
   * </p>
   */
  
  private int nEvaluations;
  private int popSize;
  private double pm;
  private double pc;
  private double d;
  private int nFuzzyRegionsForNumericAttributes;
  private boolean useMaxForOneFrequentItemsets;
  private double minSupport;
  private double minConfidence;
  private myDataset dataset;
  
  private int nEval;
  private int nGenerations;
  private int evaluationStep;
  private String geneticLearningLog;
  private ArrayList<Integer> idOfAttributes;
  
  private ArrayList<FuzzyAttribute> fuzzyAttributes;
  private int countOneFrequentItemsets;
  private int countFrequentItemsets;
  private ArrayList<AssociationRule> associationRulesSet;
  private boolean[] coveredRecords;
  
  /**
   * <p>
   * It creates a new process for the algorithm by setting up its parameters
   * </p>
   * @param dataset The instance of the dataset for dealing with its records
   * @param nEvaluations The maximum number of evaluations to accomplish before terminating the genetic learning
   * @param popSize The maximum size of population to handle after each generation
   * @param pm The probability of the mutation operator
   * @param pc The probability of the crossover operator
   * @param d The parameter which is used while executing the crossover operator
   * @param nFuzzyRegionsForNumericAttributes The number of fuzzy regions with which numeric attributes are evaluated
   * @param useMaxForOneFrequentItemsets It indicates whether the max operator must be used while discovering 1-Frequent Itemsets
   * @param minSupport The user-specified minimum support for the mined association rules
   * @param minConfidence The user-specified minimum confidence for the mined association rules
   */
  public GeneticFuzzyAprioriProcess(myDataset dataset, int nEvaluations, int popSize, double pm, double pc, double d, int nFuzzyRegionsForNumericAttributes, boolean useMaxForOneFrequentItemsets, double minSupport, double minConfidence) {
	  this.nEvaluations = nEvaluations;
	  this.popSize = popSize;
	  this.pm = pm;
	  this.pc = pc;
	  this.d = d;
	  this.nFuzzyRegionsForNumericAttributes = nFuzzyRegionsForNumericAttributes;
	  this.useMaxForOneFrequentItemsets = useMaxForOneFrequentItemsets;
	  this.minSupport = minSupport;
	  this.minConfidence = minConfidence;
	  this.dataset = dataset;

	  this.nEval = 0;
	  this.nGenerations = 0;
	  this.evaluationStep = (int) Math.ceil(nEvaluations * 0.05);
	  this.idOfAttributes = dataset.getIDsOfNumericAttributes();
          if (this.idOfAttributes.size() == 0){
              this.idOfAttributes = dataset.getIDsOfNominalAttributes();
          }
	  
      this.countOneFrequentItemsets = 0;
      this.countFrequentItemsets = 0;
      this.associationRulesSet = new ArrayList<AssociationRule>();
	  
	  this.coveredRecords = new boolean[ dataset.getnTrans() ];
	  for (int i=0; i < this.coveredRecords.length; i++)
		  this.coveredRecords[i] = false;
  }
  
  /**
   * <p>
   * It runs the algorithm for mining association rules
   * </p>
   */
  public void run() {
	  this.fuzzyAttributes = this.runGeneticAlgorithm();
	  
	  if (this.fuzzyAttributes == null) this.fuzzyAttributes = new ArrayList<FuzzyAttribute>();
	  
	  this.addNominalFuzzyAttributes(this.fuzzyAttributes);
	  
	  /*for (int i=0; i < fuzzyAttributes.size(); i++)
	  System.out.println("ID Fuzzy Attribute #" + this.fuzzyAttributes.get(i).getIdAttr() + ":\n" + this.fuzzyAttributes.get(i) + "\n");*/
	  
	  this.runFuzzyApriori( new FuzzyDataset(this.dataset, this.fuzzyAttributes) );
  }
  
  /**
   * <p>
   * It returns a rules set once the algorithm has been carried out
   * </p>
   * @return An array of association rules having both minimum confidence and support
   */
  public ArrayList<AssociationRule> getRulesSet() {
	  return this.associationRulesSet;
  }
  
  /**
   * <p>
   * It prints out on screen relevant information regarding the mined association rules
   * </p>
   * @param rules The array of association rules from which gathering relevant information
   */
  public void printReport(ArrayList<AssociationRule> rules) {
	  int r;
	  double avg_sup = 0.0, avg_conf = 0.0, avg_ant_length = 0.0, avg_interest = 0.0;
	  AssociationRule ar;
	  
	  for (r=0; r < rules.size(); r++) {
		  ar = rules.get(r);
		  
		  avg_sup += ar.getRuleSupport();
		  avg_conf += ar.getConfidence();
		  avg_ant_length += ar.getAntecedent().size();
                  avg_interest += ar.getInterestingness();
	  }
	  
	  System.out.println("\nNumber of Frequent Itemsets found: " + this.countFrequentItemsets);
	  System.out.println("Number of Association Rules generated: " + rules.size());
	  
	  if (! rules.isEmpty()) {
		  System.out.println("Average Support: " + ( avg_sup / rules.size() ));
		  System.out.println("Average Confidence: " + ( avg_conf / rules.size() ));
		  System.out.println("Average Antecedents Length: " + ( avg_ant_length / rules.size() ));
		  System.out.println("Number of Covered Records (%): " + ( (100.0 * this.countCoveredRecords()) / this.dataset.getnTrans()));
                  System.out.println("Average Interestingness: " + ( avg_interest / rules.size() ));
	  }
  }
  
  /**
   * <p>
   * It returns the number of 1-Frequent Itemsets
   * </p>
   * @return A value representing the number of 1-Frequent Itemsets
   */
  public int getNumberOfOneFrequentItemsets() {
	  return this.countOneFrequentItemsets;
  }
  
  /**
   * <p>
   * It returns the XML string representing the genetic learning log
   * </p>
   * @return A string containing the genetic learning text
   */
  public String getGeneticLearningLog() {
	  return this.geneticLearningLog;
  }
  
  /**
   * <p>
   * It returns the mined fuzzy attributes once the genetic learning has been accomplished
   * </p>
   * @return An array representing the mined fuzzy attributes
   */
  public ArrayList<FuzzyAttribute> getFuzzyAttributes() {
	  return this.fuzzyAttributes;
  }
  
  private void addNominalFuzzyAttributes(ArrayList<FuzzyAttribute> fuzzy_attributes) {
	  int attr, id_attr, id_region;
	  FuzzyRegion[] fuzzy_regions;
	  ArrayList<Integer> id_of_nominal_attributes;
	  
	  id_of_nominal_attributes = this.dataset.getIDsOfNominalAttributes();
	  
	  for (attr=0; attr < id_of_nominal_attributes.size(); attr++) {
		  id_attr = id_of_nominal_attributes.get(attr);
		  fuzzy_regions = new FuzzyRegion[((int) this.dataset.getMax(id_attr)) + 1];
		  
		  for (id_region=0; id_region < fuzzy_regions.length; id_region++) {
			  fuzzy_regions[id_region] = new FuzzyRegion();
			  
			  fuzzy_regions[id_region].setX0(this.dataset.getMin(id_attr) + id_region - 1);
			  fuzzy_regions[id_region].setX1(this.dataset.getMin(id_attr) + id_region);
			  fuzzy_regions[id_region].setX3(this.dataset.getMin(id_attr) + id_region + 1);
			  
			  fuzzy_regions[id_region].setY(1.0);
			  fuzzy_regions[id_region].setLabel(this.dataset.getNominalValue(id_attr, id_region));
		  }
		  
		  fuzzy_attributes.add( new FuzzyAttribute(id_attr, fuzzy_regions) );
	  }
	  
  }
  
  private ArrayList<FuzzyAttribute> runGeneticAlgorithm() {
	  ArrayList<FuzzyAttribute> best_fuzzy_attrs = null;
	  ArrayList<Chromosome> pop;
	  
	  this.geneticLearningLog = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	  this.geneticLearningLog += "<genetic_learning>\n";
	  
	  if (! this.idOfAttributes.isEmpty()) {
		  
		  System.out.print("Initialing population... ");
		  
		  pop = this.initializePopulation();
		  
		  System.out.println("done [Evaluations: " + this.nEval + "].");
		    
		  while (this.nEval < this.nEvaluations) {
			  this.nGenerations++;
			  System.out.print("Computing Generation #" + this.nGenerations + "... ");
			  
			  this.crossover(pop);
			  this.mutate(pop);
			  best_fuzzy_attrs = this.select(pop);
			  System.out.println("done [Evaluations: " + this.nEval + "].");
		  }
		  
		  /*for (int i=0; i < pop.size(); i++)
		  	System.out.println("Chromosome #" + (i+1) + ":\n" + pop.get(i) + "\n");*/
	  }
	  
	  this.geneticLearningLog += "</genetic_learning>";
	  
	  return best_fuzzy_attrs;
  }
  
  private ArrayList<Chromosome> initializePopulation() {
	  int p, g, m, id_attr;
	  MembershipFunction[] membership_functions;
	  Gene[] genes;
	  Chromosome chr;
	  ArrayList<Chromosome> popInit;
	  
	  popInit = new ArrayList<Chromosome>();
	  
	  membership_functions = new MembershipFunction[ this.nFuzzyRegionsForNumericAttributes ];
	  genes = new Gene[ this.idOfAttributes.size() ];
	  
	  for (p=0; p < this.popSize; p++) {
		  for (g=0; g < genes.length; g++) {
			  id_attr = this.idOfAttributes.get(g);
			  
			  for (m=0; m < membership_functions.length; m++) {
				  membership_functions[m] = new MembershipFunction();
				  
				  membership_functions[m].setC( Randomize.RanddoubleClosed(this.dataset.getMin(id_attr), this.dataset.getMax(id_attr)) );
				  membership_functions[m].setW( Randomize.RanddoubleClosed(0.0, (this.dataset.getMax(id_attr) - this.dataset.getMin(id_attr)) / 2.0) );
			  }
			  
			  genes[g] = new Gene(membership_functions);
			  genes[g].sortMembershipFunctions();
		  }
			  
		  chr = new Chromosome(genes);
		  this.evaluateFitness(chr);
		  
		  popInit.add(chr);
	  }
	  
	  return popInit;
  }
  
  private ArrayList<FuzzyAttribute> select(ArrayList<Chromosome> pop) {
	  Collections.sort(pop);
	  
	  while (pop.size() > this.popSize)
		  pop.remove(this.popSize);
	  
	  return ( this.transformIntoFuzzyAttributes( pop.get(0) ) );
  }
  
  private void crossover(ArrayList<Chromosome> pop) {
	  int i, j, index_best_chr, aux;
	  double best_fitness, sum_expected_values, rank_min, rank_max, factor, sum, rnd;
	  double[] expected_values;
	  int[] index_mating_pool;
	  Chromosome mom, dad;
	  Chromosome[] offsprings;	  
	  
	  rank_min = 0.75;
	  rank_max = 2.0 - rank_min;
	  factor = (rank_max - rank_min) / (double)(pop.size() - 1);
	  
	  expected_values = new double[ pop.size() ];
	  for (i=0; i < expected_values.length; i++)
		  expected_values[i] = 0.0;
	  
	  sum_expected_values = 0.0;
	  
	  for (i=0; i < pop.size(); i++) {
			index_best_chr = -1;
			best_fitness = 0.0;
			
			for (j=0; j < pop.size(); j++) {
				if ( (expected_values[j] == 0.0) && ( (index_best_chr == -1) || (pop.get(j).getFitness() > best_fitness) ) ) {
					best_fitness = pop.get(j).getFitness();
					index_best_chr = j;
				}
			}
			
			expected_values[index_best_chr] = rank_min + (pop.size() - 1 - i) * factor;
			sum_expected_values += expected_values[index_best_chr];
	  }
	  
	  index_mating_pool = new int[ expected_values.length ];
	  
	  for (i=0; i < index_mating_pool.length; i++) {
		  sum = 0.0;
		  rnd = Randomize.RanddoubleClosed(0.0, sum_expected_values);
		  
		  for (j=0; j < expected_values.length; j++) {
			  sum += expected_values[j];
			  if (sum > rnd) break;
		  }
		  
		  index_mating_pool[i] = j;
	  }
	  
	  for (i=0; i < index_mating_pool.length; i++) {
		  j = Randomize.Randint(i, index_mating_pool.length);
		  aux = index_mating_pool[j];
		  index_mating_pool[j] = index_mating_pool[i];
		  index_mating_pool[i] = aux;
	  }
	  
	  offsprings = new Chromosome[4];
	  
	  for (i=0; i < (index_mating_pool.length / 2); i++) {
		  mom = pop.get( index_mating_pool[2 * i] );
		  dad = pop.get( index_mating_pool[2 * i + 1] );
		  
		  if (Randomize.Rand() < this.pc) {
			  for (j=0; j < offsprings.length; j++) {
				  offsprings[j] = this.mma(j, mom.getGenes(), dad.getGenes());
				  this.evaluateFitness(offsprings[j]);
			  }
			  
			  Arrays.sort(offsprings);
			  
			  pop.add(offsprings[0]);
			  pop.add(offsprings[1]);
		  }
	  }
  }

  /**
  * It implements the max-min-arithmetical crossover operator
  */
  private Chromosome mma(int index, Gene[] mom_genes, Gene[] dad_genes) {
	  int g, m;
	  Gene[] offspring_genes;
	  MembershipFunction[] offspring_mfs, mom_mfs, dad_mfs;
	  
	  offspring_mfs = new MembershipFunction[ this.nFuzzyRegionsForNumericAttributes ];
	  offspring_genes = new Gene[ this.idOfAttributes.size() ];
	  
	  switch(index) {
	  	case 0:
	  			for (g=0; g < offspring_genes.length; g++) {
	  				mom_mfs = mom_genes[g].getMembershipFunctions();
	  				dad_mfs = dad_genes[g].getMembershipFunctions();
	  				
	  				for (m=0; m < offspring_mfs.length; m++) {
	  					offspring_mfs[m] = new MembershipFunction();
	  					
	  					offspring_mfs[m].setC(this.d * mom_mfs[m].getC() + (1 - this.d) * dad_mfs[m].getC());
	  					offspring_mfs[m].setW(this.d * mom_mfs[m].getW() + (1 - this.d) * dad_mfs[m].getW());
	  				}
	  				
	  				offspring_genes[g] = new Gene(offspring_mfs);
	  				offspring_genes[g].sortMembershipFunctions();
	  			}
	  			
	  			break;
	  	case 1:
  				for (g=0; g < offspring_genes.length; g++) {
  					mom_mfs = mom_genes[g].getMembershipFunctions();
  					dad_mfs = dad_genes[g].getMembershipFunctions();
  					
  					for (m=0; m < offspring_mfs.length; m++) {
  						offspring_mfs[m] = new MembershipFunction();
  						
  						offspring_mfs[m].setC((1 - this.d) * mom_mfs[m].getC() + this.d * dad_mfs[m].getC());
  						offspring_mfs[m].setW((1 - this.d) * mom_mfs[m].getW() + this.d * dad_mfs[m].getW());
  					}
  					
  					offspring_genes[g] = new Gene(offspring_mfs);
  					offspring_genes[g].sortMembershipFunctions();
  				}
  				
	  			break;
	  	case 2:
	  			for (g=0; g < offspring_genes.length; g++) {
					mom_mfs = mom_genes[g].getMembershipFunctions();
					dad_mfs = dad_genes[g].getMembershipFunctions();
					
					for (m=0; m < offspring_mfs.length; m++) {
						offspring_mfs[m] = new MembershipFunction();
						
						offspring_mfs[m].setC( Math.min(mom_mfs[m].getC(), dad_mfs[m].getC()) );
						offspring_mfs[m].setW( Math.min(mom_mfs[m].getW(), dad_mfs[m].getW()) );
					}
					
					offspring_genes[g] = new Gene(offspring_mfs);
					offspring_genes[g].sortMembershipFunctions();
				}
	  			
	  			break;
	  	case 3:
	  			for (g=0; g < offspring_genes.length; g++) {
	  				mom_mfs = mom_genes[g].getMembershipFunctions();
	  				dad_mfs = dad_genes[g].getMembershipFunctions();
	  				
	  				for (m=0; m < offspring_mfs.length; m++) {
	  					offspring_mfs[m] = new MembershipFunction();
	  					
	  					offspring_mfs[m].setC( Math.max(mom_mfs[m].getC(), dad_mfs[m].getC()) );
	  					offspring_mfs[m].setW( Math.max(mom_mfs[m].getW(), dad_mfs[m].getW()) );
	  				}
	  				
	  				offspring_genes[g] = new Gene(offspring_mfs);
	  				offspring_genes[g].sortMembershipFunctions();
	  			}
	  }
	  
	  return ( new Chromosome(offspring_genes) );
  }

   /**
  * It implements the one-point mutation operator
  */
  private void mutate(ArrayList<Chromosome> pop) {
	  int p, id_attr, id_region;
	  double w, eps;
	  Chromosome chr;
	  Gene[] genes;
	  MembershipFunction[] membership_functions;
	  
	  for (p=0; p < pop.size(); p++) {
		  if (Randomize.Rand() < this.pm) {
			  chr = new Chromosome( pop.get(p).getGenes() );
			  
			  genes = chr.getGenes();
			  id_attr = Randomize.Randint(0, genes.length);
			  
			  membership_functions = genes[id_attr].getMembershipFunctions();
			  id_region = Randomize.Randint(0, membership_functions.length);
			  
			  w = membership_functions[id_region].getW();
			  eps = Randomize.RanddoubleClosed(-w, w);
			  
			  if (Randomize.Rand() < 0.5) {
				  membership_functions[id_region].setC(membership_functions[id_region].getC() + eps);
				  
				  genes[id_attr].sortMembershipFunctions();
			  }
			  else membership_functions[id_region].setW(w + eps);
			  
			  this.evaluateFitness(chr);
			  
			  pop.add(chr);
		  }
	  }
  }
  
  private void evaluateFitness(Chromosome c) {
	  int g, id_attr, num_one_frequent_itemsets;
	  double suitability, fitness;
	  Gene[] genes;
	  
	  genes = c.getGenes();
	  suitability = 0.0;
	  
	  for (g=0; g < genes.length; g++) {
		  id_attr = this.idOfAttributes.get(g);
		  suitability += ( genes[g].calculateOverlapFactor() + genes[g].calculateCoverageFactor(this.dataset.getMin(id_attr), this.dataset.getMax(id_attr)) );
	  }
	  
	  num_one_frequent_itemsets = ( this.generateOneFrequentItemsets( new FuzzyDataset(this.dataset, this.transformIntoFuzzyAttributes(c) ), false) ).size();
	  fitness = num_one_frequent_itemsets / suitability;
	  
	  c.setNumOneFrequentItemsets(num_one_frequent_itemsets);
	  c.setSuitability(suitability);
	  c.setFitness(fitness);
	  
	  this.nEval++;
	  
	  if ((this.nEval % this.evaluationStep) == 0) this.buildXMLRecord(fitness, num_one_frequent_itemsets, suitability);
  }
  
  private void buildXMLRecord(double fitness, int num_one_frequent_itemsets, double suitability) {
	  this.geneticLearningLog += "<log n_evaluations=\"" + this.nEval + "\" ";
	  this.geneticLearningLog += "n_generation=\"" + this.nGenerations + "\" ";
	  this.geneticLearningLog += "fitness=\"" + fitness + "\" ";
	  this.geneticLearningLog += "n_one_frequent_itemsets=\"" + num_one_frequent_itemsets + "\" ";
	  this.geneticLearningLog += "suitability=\"" + suitability + "\"/>\n";
  }
  
  private ArrayList<FuzzyAttribute> transformIntoFuzzyAttributes(Chromosome c) {
	  int g, m;
	  Gene[] genes;
	  MembershipFunction[] membership_functions;
	  FuzzyRegion[] fuzzy_regions;
	  ArrayList<FuzzyAttribute> fuzzy_attributes;
	  
	  fuzzy_attributes = new ArrayList<FuzzyAttribute>();
	  genes = c.getGenes();
	  
	  for (g=0; g < genes.length; g++) {
		  membership_functions = genes[g].getMembershipFunctions();
		  fuzzy_regions = new FuzzyRegion[ membership_functions.length ];
		  
		  for (m=0; m < membership_functions.length; m++) {
			  fuzzy_regions[m] = new FuzzyRegion();
			  
			  fuzzy_regions[m].setX0( membership_functions[m].getC() - membership_functions[m].getW() );
			  fuzzy_regions[m].setX1( membership_functions[m].getC() );
			  fuzzy_regions[m].setX3( membership_functions[m].getC() + membership_functions[m].getW() );
			  
			  fuzzy_regions[m].setY(1.0);
			  fuzzy_regions[m].setLabel("LABEL_" + m);
		  }
		  
		  fuzzy_attributes.add( new FuzzyAttribute(this.idOfAttributes.get(g), fuzzy_regions) );
	  }
	  
	  return fuzzy_attributes;
  }
  
  private void runFuzzyApriori(FuzzyDataset fuzzyDataset) {
	  int pass = 0;
	  ArrayList<Itemset> current_frequent_itemsets;
	  
	  current_frequent_itemsets = this.generateOneFrequentItemsets(fuzzyDataset, this.useMaxForOneFrequentItemsets);
	  this.countOneFrequentItemsets = current_frequent_itemsets.size();
	  this.countFrequentItemsets = this.countOneFrequentItemsets;
	  
	  System.out.println("\nPass: " + (pass + 1) + "; Total Frequent Itemsets: " + this.countFrequentItemsets);
	  
	  for (pass=1; (pass < this.dataset.getnVars()) && (current_frequent_itemsets.size() > 1); pass++) {
		  current_frequent_itemsets = this.generateCandidateItemsetsAndRules(fuzzyDataset, current_frequent_itemsets);
		  this.countFrequentItemsets += current_frequent_itemsets.size();
		  
		  System.out.println("Pass: " + (pass + 1) + "; Total Frequent Itemsets: " + this.countFrequentItemsets + "; Total Association Rules: " + this.associationRulesSet.size());
	  }
  }
  
  private ArrayList<Itemset> generateOneFrequentItemsets(FuzzyDataset fuzzyDataset, boolean use_max_for_one_frequent_itemsets) {
	  int id_attr, id_region;
	  double max_support;
	  int[] num_fuzzy_regions;
	  Itemset itemset, best_itemset;
	  ArrayList<Itemset> one_frequent_itemsets;
	  
	  num_fuzzy_regions = fuzzyDataset.getNumberOfFuzzyRegions();
	  one_frequent_itemsets = new ArrayList<Itemset>();
	  
	  if (use_max_for_one_frequent_itemsets) {
		  
		  for (id_attr=0; id_attr < fuzzyDataset.getNumberOfFuzzyAttributes(); id_attr++) {
			  best_itemset = new Itemset();
			  best_itemset.add( new Item(id_attr, 0) );
			  best_itemset.calculateSupport(fuzzyDataset);
			  max_support = best_itemset.getSupport();
			  
			  for (id_region=1; id_region < num_fuzzy_regions[id_attr]; id_region++) {
				  itemset = new Itemset();
				  itemset.add( new Item(id_attr, id_region) );
				  itemset.calculateSupport(fuzzyDataset);
				  
				  if (itemset.getSupport() > max_support) {
					  max_support = itemset.getSupport();
					  best_itemset = itemset;
				  }
			  }
			  
			  if (max_support >= this.minSupport) one_frequent_itemsets.add(best_itemset);
		  }
	  }
	  else {
		  for (id_attr=0; id_attr < fuzzyDataset.getNumberOfFuzzyAttributes(); id_attr++) {
			  
			  for (id_region=0; id_region < num_fuzzy_regions[id_attr]; id_region++) {
				  itemset = new Itemset();  
				  itemset.add( new Item(id_attr, id_region) );
				  itemset.calculateSupport(fuzzyDataset);
				  
				  if (itemset.getSupport() >= this.minSupport) one_frequent_itemsets.add(itemset);
			  }
		  }
	  }
	  
	  return one_frequent_itemsets;
  }
  
  private ArrayList<Itemset> generateCandidateItemsetsAndRules(FuzzyDataset fuzzyDataset, ArrayList<Itemset> curr_freq_itemsets) {
	  int i, j, size;
	  boolean generated_rules;
	  Itemset i_itemset, j_itemset, new_itemset;
	  ArrayList<Integer> covered_tids;
	  ArrayList<Itemset> next_freq_itemsets;
	  
	  size = curr_freq_itemsets.size();
	  next_freq_itemsets = new ArrayList<Itemset>();
	  
	  for (i=0; i < size-1; i++) {
		  i_itemset = curr_freq_itemsets.get(i);
		  
		  for (j=i+1; j < size; j++) {
			  j_itemset = curr_freq_itemsets.get(j);
			  
			  if ( this.isCombinable(i_itemset, j_itemset, curr_freq_itemsets) ) {
				  new_itemset = i_itemset.clone();
				  new_itemset.add( ( j_itemset.get(j_itemset.size() - 1) ).clone() );
				  covered_tids = new_itemset.calculateSupport(fuzzyDataset);
				  
				  if (new_itemset.getSupport() >= this.minSupport) {
					  generated_rules = this.generateRulesFromItemset(fuzzyDataset, new_itemset);
					  if (generated_rules) this.markCoveredRecords(covered_tids);
					  
					  next_freq_itemsets.add(new_itemset);
				  }
			  }
		  }
	  }
	  
	  return next_freq_itemsets;
  }
  
  private boolean generateRulesFromItemset(FuzzyDataset fuzzyDataset, Itemset curr_itemset) {
	  int i;
	  double rule_sup, ant_sup, rule_conf,cons_sup,interest;
	  boolean generated_rules = false;
	  Item i_item;
	  Itemset antecedent, consequent;
	  
	  for (i=0; i < curr_itemset.size(); i++) {
		  antecedent = curr_itemset.clone();
		  i_item = antecedent.remove(i);
		  antecedent.calculateSupport(fuzzyDataset);
		  
		  rule_sup = curr_itemset.getSupport();
		  ant_sup = antecedent.getSupport();
		  rule_conf = rule_sup / ant_sup;
		  
		  if (rule_conf >= this.minConfidence) {
			  consequent = new Itemset();
			  consequent.add(i_item);
                          consequent.calculateSupport(fuzzyDataset);
                          cons_sup = consequent.getSupport();
                          interest = rule_conf * (rule_sup/cons_sup) * (1 - (rule_sup/this.dataset.getnTrans()));
			  this.associationRulesSet.add( new AssociationRule(antecedent, consequent, rule_sup, ant_sup, rule_conf,cons_sup,interest) );
			  
			  if (! generated_rules) generated_rules = true;
		  }
	  }
	  
	  return generated_rules;
  }
  
  private boolean isCombinable(Itemset i_itemset, Itemset j_itemset, ArrayList<Itemset> curr_freq_itemsets) {
	  int i;
	  Item i_item, j_item;
	  Itemset itemset;

	  if (i_itemset.size() != j_itemset.size()) return false;

	  i_item = i_itemset.get(i_itemset.size() - 1);
	  j_item = j_itemset.get(i_itemset.size() - 1);
	  
	  if (i_item.getIDAttribute() >= j_item.getIDAttribute()) return false;

	  for (i=0; i < (i_itemset.size() - 1); i++) {
		  i_item = i_itemset.get(i);
		  j_item = j_itemset.get(i);
		  
		  if (! i_item.equals(j_item)) return false;
	  }
	  
	  itemset = i_itemset.clone();
	  itemset.add( ( j_itemset.get(i_itemset.size() - 1) ).clone() );
	  if ( this.pruning(itemset, curr_freq_itemsets) ) return false;

	  return true;
  }
  
  private boolean pruning(Itemset itemset, ArrayList<Itemset> curr_freq_itemsets) {
	  int i;
	  Itemset sub;

	  for (i=0; i < itemset.size() - 2; i++) {
		  sub = itemset.clone();
		  sub.remove(i);
		  if (! this.existingIntoFrequentItemsets(sub, curr_freq_itemsets)) return true;
	  }
	  
	  return false;
  }
  
  private boolean existingIntoFrequentItemsets(Itemset itemset, ArrayList<Itemset> curr_freq_itemsets) {
	  int i;
	  Itemset its;

	  for (i=0; i < curr_freq_itemsets.size(); i++) {
		  its = curr_freq_itemsets.get(i);
		  if ( its.equals(itemset) ) return true;
	  }
	  
	  return false;
  }
  
  private void markCoveredRecords(ArrayList<Integer> covered_tids) {
	  int i, t;
	  
	  for (i=0; i < covered_tids.size(); i++) {
		  t = covered_tids.get(i);
		  if (! this.coveredRecords[t]) this.coveredRecords[t] = true;
	  }
  }
  
  private int countCoveredRecords() {
	  int i, cnt_covered_records = 0;
	  
	  for (i=0; i < this.coveredRecords.length; i++) {
		  if (this.coveredRecords[i]) cnt_covered_records++;
	  }
	  
	  return cnt_covered_records;
  }
  
}