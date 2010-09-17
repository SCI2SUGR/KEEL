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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.Alcalaetal;

/**
 * <p>
 * @author Written by Alvaro Lopez
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

import java.util.*;
import org.core.Randomize;

public class AlcalaetalProcess {
  /**
   * <p>
   * It provides the implementation of the algorithm to be run in a process
   * </p>
   */
  
  private int nEvaluations;
  private int popSize;
  private int nBitsGene;
  private double phi;
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
  private double initialL;
  
  private ArrayList<FuzzyAttribute> uniformFuzzyAttributes;
  private ArrayList<FuzzyAttribute> bestFuzzyAttributes;
  private int countOneFrequentItemsets;
  private int countFrequentItemsets;
  private ArrayList<AssociationRule> associationRulesSet;
  private boolean[] coveredRecords;
  
  /**
   * <p>
   * It creates a new process for the algorithm by setting up its parameters
   * </p>
   * @param dataset The instance of the dataset for dealing with its records
   * @param nEvaluations The maximum number of evaluations to reach before stopping the genetic learning
   * @param popSize The maximum size of population to handle after each generation
   * @param nBitsGene The number of bit digits for encoding a displacement within a gene
   * @param phi It represents the value used for decreasing the "L" threshold (CURRENTLY NOT USED)
   * @param d It indicates the value for controlling the Parent Centric BLX crossover
   * @param nFuzzyRegionsForNumericAttributes The number of fuzzy regions with which numeric attributes are evaluated
   * @param useMaxForOneFrequentItemsets It indicates whether the max operator must be used while discovering 1-Frequent Itemsets
   * @param minSupport The user-specified minimum support for the mined association rules
   * @param minConfidence The user-specified minimum confidence for the mined association rules
   */
  public AlcalaetalProcess(myDataset dataset, int nEvaluations, int popSize, int nBitsGene, double phi, double d, int nFuzzyRegionsForNumericAttributes, boolean useMaxForOneFrequentItemsets, double minSupport, double minConfidence) {
	  this.nEvaluations = nEvaluations;
	  this.popSize = popSize;
	  this.nBitsGene = nBitsGene;
	  this.phi = phi;
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
	  this.initialL = (this.idOfAttributes.size() * nFuzzyRegionsForNumericAttributes * nBitsGene) / 4.0;
	  
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
	  this.uniformFuzzyAttributes = this.buildInitialFuzzyAttributes();
	  this.bestFuzzyAttributes = this.runGeneticAlgorithm();
	  
	  if (this.bestFuzzyAttributes == null) this.bestFuzzyAttributes = new ArrayList<FuzzyAttribute>();
	  
	  this.addNominalFuzzyAttributes(this.uniformFuzzyAttributes);
	  this.addNominalFuzzyAttributes(this.bestFuzzyAttributes);
	  
	  /*for (int i=0; i < bestFuzzyAttributes.size(); i++)
		  System.out.println("ID Fuzzy Attribute #" + this.bestFuzzyAttributes.get(i).getIdAttr() + ":\n" + this.bestFuzzyAttributes.get(i) + "\n");*/
	  
	  this.runFuzzyApriori( new FuzzyDataset(this.dataset, this.bestFuzzyAttributes) );
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
   * It returns the uniform fuzzy attributes before running the genetic learning process
   * </p>
   * @return An array representing the uniform fuzzy attributes
   */
  public ArrayList<FuzzyAttribute> getUniformFuzzyAttributes() {
	  return this.uniformFuzzyAttributes;
  }
  
  /**
   * <p>
   * It returns the mined fuzzy attributes once the genetic learning has been accomplished
   * </p>
   * @return An array representing the mined fuzzy attributes
   */
  public ArrayList<FuzzyAttribute> getAdjustedFuzzyAttributes() {
	  return this.bestFuzzyAttributes;
  }
  
  private ArrayList<FuzzyAttribute> buildInitialFuzzyAttributes() {
	  int attr, id_region, id_attr;
	  double rank, mark, value;
	  FuzzyRegion[] fuzzy_regions;
	  ArrayList<FuzzyAttribute> fuzzy_attributes;
	  
	  fuzzy_attributes = new ArrayList<FuzzyAttribute>();
	  	  
	  for (attr=0; attr < this.idOfAttributes.size(); attr++) {
		  id_attr = this.idOfAttributes.get(attr);
		  
		  rank = Math.abs(this.dataset.getMax(id_attr) - this.dataset.getMin(id_attr));
		  mark = rank / (this.nFuzzyRegionsForNumericAttributes - 1.0);
		  
		  fuzzy_regions = new FuzzyRegion[ this.nFuzzyRegionsForNumericAttributes ];
		  
		  for (id_region=0; id_region < fuzzy_regions.length; id_region++) {
			  fuzzy_regions[id_region] = new FuzzyRegion();
			  
			  value = this.dataset.getMin(id_attr) + mark * (id_region - 1);
			  fuzzy_regions[id_region].setX0( this.setValue(value, this.dataset.getMax(id_attr)) );
			  
			  value = this.dataset.getMin(id_attr) + mark * id_region;
			  fuzzy_regions[id_region].setX1( this.setValue(value, this.dataset.getMax(id_attr)) );
			  
			  value = this.dataset.getMin(id_attr) + mark * (id_region + 1);
			  fuzzy_regions[id_region].setX3( this.setValue(value, this.dataset.getMax(id_attr)) );
			  
			  fuzzy_regions[id_region].setY(1.0);
			  fuzzy_regions[id_region].setLabel("LABEL_" + id_region);
		  }
		  
		  fuzzy_attributes.add( new FuzzyAttribute(id_attr, fuzzy_regions) );
	  }
	  
	  return fuzzy_attributes;
  }
  
  private double setValue(double val, double tope) {
	  if (val > -1E-4 && val < 1E-4) return 0.0;
	  
	  if (val > tope - 1E-4 && val < tope + 1E-4) return tope;
	  
	  return val;
  }
  
  private void addNominalFuzzyAttributes(ArrayList<FuzzyAttribute> fuzzy_attributes) {
	  int attr, id_attr, id_region;
	  FuzzyRegion[] fuzzy_regions;
	  
	  for (attr=0; attr < this.dataset.getIDsOfNominalAttributes().size(); attr++) {
		  id_attr = this.dataset.getIDsOfNominalAttributes().get(attr);
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
	  double l;
	  int decrement;
	  Chromosome best_chromosome, old_best_chromosome;
	  ArrayList<FuzzyAttribute> best_fuzzy_attribute = null;
	  ArrayList<Chromosome> old_pop, current_pop;
	  
		  
	  this.geneticLearningLog = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	  this.geneticLearningLog += "<genetic_learning>\n";
	  
	  if (! this.idOfAttributes.isEmpty()) {
		  
		  System.out.print("Initialing population... ");
		  
		  l = this.initialL;
		  decrement = 1;
		  current_pop = new ArrayList<Chromosome>();
		  best_chromosome = old_best_chromosome = this.buildInitialChromosome();
		  
		  this.initializePopulation(current_pop, best_chromosome);
		  this.evaluate(current_pop, 0, this.popSize);
		  
		  System.out.println("done [Evaluations: " + this.nEval + "].");
		  
		  while (this.nEval < this.nEvaluations) {
			  this.nGenerations++;
			  System.out.print("Computing Generation #" + this.nGenerations + "... ");
			  old_pop = new ArrayList<Chromosome>(current_pop);
			  
			  this.crossover(current_pop, l);
			  this.evaluate(current_pop, this.popSize, current_pop.size());
			  best_chromosome = this.select(current_pop);
			  
			  if ( best_chromosome.equals(old_best_chromosome) ) l--;
			  else {
				  decrement = 1;
				  old_best_chromosome = best_chromosome;
			  }
			  
			  if ( this.noNewChromosomes(current_pop, old_pop) ) {
				  l -= decrement;
				  if ( best_chromosome.equals(old_best_chromosome) ) decrement *= 2;
			  }
			  
			  if (l < 0.0) {
				  this.initializePopulation(current_pop, best_chromosome);
				  this.evaluate(current_pop, 0, this.popSize);
				  l = this.initialL;
				  decrement = 1;
			  }
			  
			  System.out.println("done [Evaluations: " + this.nEval + "].");
		  }
		  
		  best_fuzzy_attribute = this.transformIntoFuzzyAttributes(best_chromosome);
		  
		  /*for (int i=0; i < current_pop.size(); i++)
		  	System.out.println("Chromosome #" + (i+1) + ":\n" + current_pop.get(i) + "\n");*/
	  }
	  
	  this.geneticLearningLog += "</genetic_learning>";
	  
	  return best_fuzzy_attribute;
  }
  
  private Chromosome buildInitialChromosome() {
	  int g, d;
	  double[] displacements;
	  Gene[] genes;
	  
	  genes = new Gene[ this.idOfAttributes.size() ];
	  displacements = new double[ this.nFuzzyRegionsForNumericAttributes ];	  

	  for (g=0; g < genes.length; g++) {
		  for (d=0; d < displacements.length; d++) {
			  displacements[d] = 0.5;
		  }
		  
		  genes[g] = new Gene(displacements);
	  }
	  
	  
	  return ( new Chromosome(genes) );
  }
  
  private void initializePopulation(ArrayList<Chromosome> pop, Chromosome seed_chromosome) {
	  int p, g, d;
	  double[] displacements;
	  Gene[] genes;

	  pop.clear();
	  pop.add(seed_chromosome);

	  genes = new Gene[ this.idOfAttributes.size() ];
	  displacements = new double[ this.nFuzzyRegionsForNumericAttributes ];	  
	  
	  for (p=1; p < this.popSize; p++) {
		  for (g=0; g < genes.length; g++) {
			  for (d=0; d < displacements.length; d++) {
				  displacements[d] = Randomize.Rand();
			  }
			  
			  genes[g] = new Gene(displacements);
		  }
		  
		  pop.add( new Chromosome(genes) );
	  }
  }
  
  private void evaluate(ArrayList<Chromosome> pop, int start_index, int end_index) {
	  for (int i=start_index; i < end_index; i++) {
		  this.evaluateFitness( pop.get(i) );
	  }
  }
  
  private Chromosome select(ArrayList<Chromosome> pop) {
	  Collections.sort(pop);
	  
	  while (pop.size() > this.popSize)
		  pop.remove(this.popSize);
	  
	  return ( pop.get(0) );
  }
  
  private boolean noNewChromosomes(ArrayList<Chromosome> current_pop, ArrayList<Chromosome> old_pop) {
	  return ( current_pop.containsAll(old_pop) );
  }
  
  private void crossover(ArrayList<Chromosome> pop, double threshold) {
	  int i, j, aux, total_length;
	  double hamming_distance;
	  int[] index_mating_pool;
	  Chromosome mom, dad;
	  Chromosome[] offsprings;
	  index_mating_pool = new int[ pop.size() ];
	  
	  for (i=0; i < index_mating_pool.length; i++) {
		  index_mating_pool[i] = i;
	  }
	  
	  for (i=0; i < index_mating_pool.length; i++) {
		  j = Randomize.Randint(i, index_mating_pool.length);
		  aux = index_mating_pool[j];
		  index_mating_pool[j] = index_mating_pool[i];
		  index_mating_pool[i] = aux;
	  }
	  
	  for (i=0; i < (index_mating_pool.length / 2); i++) {
		  mom = pop.get( index_mating_pool[2 * i] );
		  dad = pop.get( index_mating_pool[2 * i + 1] );
		  
		  total_length = this.idOfAttributes.size() * this.nFuzzyRegionsForNumericAttributes * this.nBitsGene;
		  hamming_distance = this.calculateHammingDistance(this.transformIntoGrayCode(mom), this.transformIntoGrayCode(dad), total_length);
		  
		  if ((hamming_distance / 2.0) > threshold) {
			  offsprings = this.pcBLX(mom, dad);
			  
			  for (j=0; j < offsprings.length; j++) {
				  pop.add( offsprings[j] );
			  }
		  }
	  }
  }
  
  private int calculateHammingDistance(ArrayList<Boolean> bit_str1, ArrayList<Boolean> bit_str2, int length) {
	  int i, dist;
	  
	  dist = 0;
	  
	  for (i=0; i < length; i++) {
		  if (bit_str1.get(i) != bit_str2.get(i)) dist++;
	  }
	  
	  return dist;
  }
  
  private ArrayList<Boolean> transformIntoGrayCode(Chromosome chr) {
	  int g, d, b, num;
	  double step;
	  boolean[] bin_num, gray_num;
	  Gene[] genes;
	  double []displacements;
	  ArrayList<Boolean> bit_str;
	  
	  bit_str = new ArrayList<Boolean>();
	  
	  step = 1.0 / (Math.pow(2.0, (double)this.nBitsGene) - 1.0);
	  genes = chr.getGenes();
	  
	  for (g=0; g < genes.length; g++) {
		  displacements = genes[g].getDisplacements();
		  
		  for (d=0; d < displacements.length; d++) {
			  num = (int)((displacements[d] / step) + 0.5);
			  
			  bin_num = this.toBinary(num, this.nBitsGene);
			  gray_num = this.toGray(bin_num);
			  
			  for (b=0; b < gray_num.length; b++) {
				  bit_str.add( gray_num[b] );
			  }
		  }
	  }
	  
	  return bit_str;
  }
  
  private boolean[] toBinary(int num, int n_bits) {
	  int i;
	  boolean[] bin_num = new boolean[n_bits];
	  	  
	  for (i=bin_num.length-1; i >= 0; i--) {
		  bin_num[i] = ((num & 1) == 0) ? false : true;
		  num >>= 1;
	  }
	  
	  return bin_num;
  }
  
  private boolean[] toGray(boolean[] bin_num) {
	  int i;
	  boolean last = false;
	  boolean[] gray_num = new boolean[ bin_num.length ];
	  
	  for (i=0; i < gray_num.length; i++) {
	      gray_num[i] = (bin_num[i] != last);
	      last = bin_num[i];
	  }
	  
	  return gray_num;
  }

    /**
     * It implements the Parent Centric BLX crossover operator
     */
  private Chromosome[] pcBLX(Chromosome mom, Chromosome dad) {
	  int g, d;
	  double amp, l, u;
	  Gene[] mom_genes, dad_genes;
	  Gene[][] offsprings_genes;
	  double[] mom_disp, dad_disp;
	  double[][] offsprings_disp;
	  Chromosome[] offsprings;
	  
	  offsprings = new Chromosome[2];
	  
	  mom_genes = mom.getGenes();
	  dad_genes = dad.getGenes();
	  
	  offsprings_genes = new Gene[2][ mom_genes.length ];
	  
	  for (g=0; g < mom_genes.length; g++) {
		  mom_disp = mom_genes[g].getDisplacements();
		  dad_disp = dad_genes[g].getDisplacements();
		  
		  offsprings_disp = new double[2][ mom_disp.length ];
		  
		  for (d=0; d < mom_disp.length; d++) {
			  amp = Math.abs(mom_disp[d] - dad_disp[d]);
			  
			  l = Math.max(0.0, mom_disp[d] - (amp * this.d));
			  u = Math.min(1.0, mom_disp[d] + (amp * this.d));
			  offsprings_disp[0][d] = Randomize.Randdouble(l, u);
			  
			  l = Math.max(0.0, dad_disp[d] - (amp * this.d));
			  u = Math.min(1.0, dad_disp[d] + (amp * this.d));
			  offsprings_disp[1][d] = Randomize.Randdouble(l, u);
		  }
		  
		  offsprings_genes[0][g] = new Gene( offsprings_disp[0] );
		  offsprings_genes[1][g] = new Gene( offsprings_disp[1] );
	  }
	  
	  offsprings[0] = new Chromosome(offsprings_genes[0]);
	  offsprings[1] = new Chromosome(offsprings_genes[1]);
	  
	  return offsprings;
  }

  private void evaluateFitness(Chromosome c) {
	  int i, num_one_frequent_itemsets;
	  double suitability, fitness, sum_fuzzy_support;
	  Gene[] genes;
	  ArrayList<Itemset> one_frequent_itemsets;
	   
	  one_frequent_itemsets = this.generateOneFrequentItemsets( new FuzzyDataset(this.dataset, this.transformIntoFuzzyAttributes(c) ), false);
	  num_one_frequent_itemsets = one_frequent_itemsets.size();
	  sum_fuzzy_support = 0.0;
	  
	  for (i=0; i < num_one_frequent_itemsets; i++) {
		  sum_fuzzy_support += one_frequent_itemsets.get(i).getSupport();
	  }
	  
	  genes = c.getGenes();
	  suitability = 0.0;
	  
	  for (i=0; i < genes.length; i++) {
		  suitability += (genes[i].calculateOverlapFactor( this.uniformFuzzyAttributes.get(i) ) + 1.0);
	  }
	  
	  fitness = sum_fuzzy_support / suitability;
	  
	  c.setFitness(fitness);
	  c.setSumFuzzySupport(sum_fuzzy_support);
	  c.setSuitability(suitability);
	  c.setNumOneFrequentItemsets(num_one_frequent_itemsets);
	  
	  this.nEval++;
	  
	  if ((this.nEval % this.evaluationStep) == 0) this.buildXMLRecord(fitness, sum_fuzzy_support, suitability, num_one_frequent_itemsets);
  }
  
  private void buildXMLRecord(double fitness, double sum_fuzzy_support, double suitability, int num_one_frequent_itemsets) {
	  this.geneticLearningLog += "<log n_evaluations=\"" + this.nEval + "\" ";
	  this.geneticLearningLog += "n_generation=\"" + this.nGenerations + "\" ";
	  this.geneticLearningLog += "fitness=\"" + fitness + "\" ";
	  this.geneticLearningLog += "sum_fuzzy_support=\"" + sum_fuzzy_support + "\" ";
	  this.geneticLearningLog += "suitability=\"" + suitability + "\" ";
	  this.geneticLearningLog += "n_one_frequent_itemsets=\"" + num_one_frequent_itemsets + "\"/>\n";
  }
  
  private ArrayList<FuzzyAttribute> transformIntoFuzzyAttributes(Chromosome c) {
	  int g, r;
	  double displacement;
	  Gene[] genes;
	  double[] displacements;
	  FuzzyRegion[] fuzzy_regions, uniform_fuzzy_regions;
	  ArrayList<FuzzyAttribute> fuzzy_attributes;
	  
	  fuzzy_attributes = new ArrayList<FuzzyAttribute>();
	  genes = c.getGenes();
	  
	  for (g=0; g < genes.length; g++) {
		  uniform_fuzzy_regions = this.uniformFuzzyAttributes.get(g).getFuzzyRegions();
		  fuzzy_regions = new FuzzyRegion[ uniform_fuzzy_regions.length ];
		  displacements = genes[g].getDisplacements();
		  
		  for (r=0; r < fuzzy_regions.length; r++) {
			  
			  if (r == 0) displacement = (displacements[r] - 0.5) * (uniform_fuzzy_regions[r + 1].getX1() - uniform_fuzzy_regions[r].getX1());
			  else if (r == (fuzzy_regions.length-1)) displacement = (displacements[r] - 0.5) * (uniform_fuzzy_regions[r].getX1() - uniform_fuzzy_regions[r - 1].getX1());
			  else {
				  if ((displacements[r] - 0.5) < 0) displacement = (displacements[r] - 0.5) * (uniform_fuzzy_regions[r].getX1() - uniform_fuzzy_regions[r - 1].getX1());
				  else displacement = (displacements[r] - 0.5) * (uniform_fuzzy_regions[r + 1].getX1() - uniform_fuzzy_regions[r].getX1());
			  }
			  
			  fuzzy_regions[r] = new FuzzyRegion();
			  
			  fuzzy_regions[r].setX0( uniform_fuzzy_regions[r].getX0() + displacement );
			  fuzzy_regions[r].setX1( uniform_fuzzy_regions[r].getX1() + displacement );
			  fuzzy_regions[r].setX3( uniform_fuzzy_regions[r].getX3() + displacement );
			  
			  fuzzy_regions[r].setY( uniform_fuzzy_regions[r].getY() );
			  fuzzy_regions[r].setLabel( uniform_fuzzy_regions[r].getLabel() );
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