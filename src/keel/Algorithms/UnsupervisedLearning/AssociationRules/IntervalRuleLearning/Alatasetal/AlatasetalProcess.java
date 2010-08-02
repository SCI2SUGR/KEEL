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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.Alatasetal;

/**
 * <p>
 * @author Written by Nicolò Flugy Papè (Politecnico di Milano) 24/03/2009
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

import java.util.*;
import org.core.Randomize;

public class AlatasetalProcess {
  /**
   * <p>
   * It provides the implementation of the algorithm to be run in a process
   * </p>
   */
  
  private final int ATTRIBUTE_NOT_COVERED = -1;
  private final int ATTRIBUTE_COVERED_BY_ANTECEDENT = 0;
  private final int ATTRIBUTE_COVERED_BY_CONSEQUENT = 1;
  private final int ATTRIBUTE_COVERED_BY_BOTH = 2;
  
  private myDataset dataset;
  private int nGen;
  private int randomChromosomes;
  private int r;
  private int tournamentSize;
  private double pc;
  private double pmMin;
  private double pmMax;
  private double a1;
  private double a2;
  private double a3;
  private double a4;
  private double a5;
  private double af;
  
  private int uPopSize;
  private int nAttr;
  private int nTrans;
  private double[] maxAmplitudes;
  private double minFitnessValue;
  private ArrayList<Chromosome> uPop;
  
  /**
   * <p>
   * It creates a new process for the algorithm by setting up its parameters
   * </p>
   * @param dataset The instance of the dataset for dealing with its records
   * @param nGen The maximum number of generations to reach before completing the whole evolutionary learning
   * @param randomChromosomes The number of initial random chromosomes
   * @param r The number of parts in which each random chromosome is divided to generate the others by doing inversions
   * @param tournamentSize The size of tournament to select the fittest chromosome in the current population
   * @param pc The probability for the crossover operator
   * @param pmMin The minimum probability for the adaptive mutation operator
   * @param pmMax The maximum probability for the adaptive mutation operator
   * @param a1 The factor determining the importance of the rules support
   * @param a2 The factor determining the importance of the rules confidence
   * @param a3 The factor determining the importance of the number of involved attributes
   * @param a4 The factor determining the importance of the amplitude of intervals
   * @param a5 The factor determining the importance of the number of rules already covered
   * @param af The factor of amplitude for each of the dataset attribute
   */
  public AlatasetalProcess(myDataset dataset, int nGen, int randomChromosomes, int r, int tournamentSize, double pc, double pmMin, double pmMax, double a1, double a2, double a3, double a4, double a5, double af) {
	  int i;
	  double sum_max_amp = 0.0;
	  
	  this.dataset = dataset;
	  this.nGen = nGen;
	  this.randomChromosomes = randomChromosomes;
	  this.r = r;
	  this.tournamentSize = tournamentSize;
	  this.pc = pc;
	  this.pmMin = pmMin;
	  this.pmMax = pmMax;
	  this.a1 = a1;
	  this.a2 = a2;
	  this.a3 = a3;
	  this.a4 = a4;
	  this.a5 = a5;
	  this.af = af;
	  
	  this.uPopSize = (int)Math.pow(2, this.r) * this.randomChromosomes;
	  this.nAttr = this.dataset.getnVars();
	  this.nTrans = this.dataset.getnTrans();
	  
	  this.maxAmplitudes = new double[this.nAttr];
	  for (i=0; i < this.maxAmplitudes.length; i++) {
		  this.maxAmplitudes[i] = this.dataset.getMax(i) - this.dataset.getMin(i);
		  sum_max_amp += this.maxAmplitudes[i];
	  }
	  
	  this.minFitnessValue = -(this.nAttr + sum_max_amp + 100.0);
  }
  
  /**
   * <p>
   * It runs the evolutionary learning for mining association rules
   * </p>
   */
  public void run() {
	  int i, j, nGn = 0;
	  Chromosome c1, c2;
	  
	  System.out.print("Initializing Uniform Population... ");
	  
	  this.uPop = this.initializeUniformPopulation();
	  this.evaluate(this.uPop, 0, this.uPopSize);
	  Collections.sort(this.uPop);
	  
	  System.out.print("done.\n");
	  
	  while (nGn < this.nGen) {
		  
		  System.out.print("Computing Generation " + (nGn + 1) + "... ");
		  
		  while (this.uPop.size() < (this.uPopSize * 2)) {
			  if (this.uPop.size() != this.uPopSize) {
				  if (Randomize.Rand() < 0.5) this.crossover(this.uPop);
				  else this.mutate(this.uPop);
			  }
			  else this.uniformOperator(this.uPop);
		  }
		  
		  this.evaluate(this.uPop, this.uPopSize, this.uPop.size());
		  this.computeAdjustedFitness(this.uPop);
		  
		  for (i=0; i < this.uPopSize; i++) {
			  c1 = this.uPop.get(i);
			  for (j=this.uPopSize; j < this.uPop.size(); j++) {
				  c2 = this.uPop.get(j);
				  if ( c1.equals(c2) ) this.uPop.remove(j);
			  }
		  }
		  
		  Collections.sort(this.uPop);
		  while (this.uPop.size() > this.uPopSize)
			  this.uPop.remove(this.uPopSize);
		  
		  nGn++;
		  
		  System.out.print("done.\n");
	  }
	  
	  this.adjustIntervals(this.uPop);
	  
	  /*for (i=0; i < this.uPop.size(); i++)
		  System.out.println("#" + (i + 1) + "\n" + this.uPop.get(i));*/
  }
  
  /**
   * <p>
   * It constructs a rules set once the whole evolutionary learning has been carried out.
   * From the last population it filters those chromosomes which satisfy both confidence and support thresholds
   * </p>
   * @param minConfidence The user-specified minimum confidence for the mined association rules
   * @param minSupport The user-specified minimum support for the mined association rules
   * @return An array of association rules having both minimum confidence and support
   */
  public ArrayList<AssociationRule> generateRulesSet(double minConfidence, double minSupport) {
	  int r;
	  Chromosome chr;
	  ArrayList<AssociationRule> rules = new ArrayList<AssociationRule>();
	  
	  for (r=0; r < this.uPop.size(); r++) {
		  chr = this.uPop.get(r);
		  if ( (chr.getRuleConfidence() >= minConfidence) && (chr.getRuleSupport() >= minSupport) ) rules.add( new AssociationRule(chr) );
	  }
	  
	  return rules;
  }
  
  /**
   * <p>
   * It prints out on screen relevant information regarding the mined association rules
   * </p>
   * @param rules The array of association rules from which gathering relevant information
   */
  public void printReport(ArrayList<AssociationRule> rules) {
	  int i, r, t, cnt_cov_rec = 0;
	  double avg_sup = 0.0, avg_conf = 0.0, avg_ant_length = 0.0;
	  boolean[] cov_rec;
	  ArrayList<Integer> cov_tids;
	  
	  cov_rec = new boolean[this.nTrans];
	  for (i=0; i < cov_rec.length; i++)
		  cov_rec[i] = false;
	  
	  AssociationRule ar;
	  
	  for (r=0; r < rules.size(); r++) {
		  ar = rules.get(r);
		  
		  avg_sup += ar.getSupport();
		  avg_conf += ar.getConfidence();
		  avg_ant_length += ar.getIdOfAntecedents().size();
		  
		  cov_tids = ar.getCoveredTIDs();
		  
		  for (i=0; i < cov_tids.size(); i++) {
			  t = cov_tids.get(i);
			  if (! cov_rec[t]) {
				  cov_rec[t] = true;
				  cnt_cov_rec++;
			  }
		  }
	  }
	  	  
	  System.out.println("\nNumber of Association Rules generated: " + rules.size());
	  if (! rules.isEmpty()) {
		  System.out.println("Average Support: " + ( avg_sup / rules.size() ));
		  System.out.println("Average Confidence: " + ( avg_conf / rules.size() ));
		  System.out.println("Average Antecedents Length: " + ( avg_ant_length / rules.size() ));
		  System.out.println("Number of Covered Records (%): " + ( (100.0 * cnt_cov_rec) / this.nTrans ));
	  }
  }
    
  private ArrayList<Chromosome> initializeUniformPopulation() {
	  int cnt_chr, g, type_attr, step, mod;
	  double lb, ub, top, min_attr, max_attr;
	  
	  step = this.nAttr / this.r;
	  mod = this.nAttr % this.r;
	  
	  ArrayList<Chromosome> popInit = new ArrayList<Chromosome>();
	  Gene[] rnd_genes = new Gene[this.nAttr];
	    
	  for (cnt_chr=0; cnt_chr < this.randomChromosomes; cnt_chr++) {		  
		  for (g=0; g < rnd_genes.length; g++) {
			  rnd_genes[g] = new Gene();
			  
			  type_attr = this.dataset.getAttributeType(g);
			  min_attr = this.dataset.getMin(g);
			  max_attr = this.dataset.getMax(g);
			  
			  if ( type_attr != myDataset.NOMINAL ) {
				  if ( type_attr == myDataset.REAL ) {
					  lb = Randomize.RanddoubleClosed(min_attr, max_attr);
					  top = Math.min(lb + this.maxAmplitudes[g], max_attr);
					  ub = Randomize.RanddoubleClosed(lb + 0.0001, top);
				  }
				  else {
					  lb = Randomize.RandintClosed((int)min_attr, (int)max_attr);
					  top = Math.min(lb + this.maxAmplitudes[g], max_attr);
					  ub = Randomize.RandintClosed((int)lb + 1, (int)top);
				  }
			  }
			  else lb = ub = Randomize.RandintClosed((int)min_attr, (int)max_attr);
			  
			  rnd_genes[g].setLowerBound(lb);
			  rnd_genes[g].setUpperBound(ub);
			  rnd_genes[g].setIsPositiveInterval( (Randomize.RandintClosed(0, 1) == 1) ? true : false );
			  rnd_genes[g].setActAs( Randomize.RandintClosed(Gene.NOT_INVOLVED, Gene.CONSEQUENT) );
		  }
		  
		  this.buildAllChromosomes(popInit, new Chromosome(rnd_genes), new boolean[this.r], 0, this.r, step, mod);
	  }
	  
	  return popInit;
  }
  
  private void buildAllChromosomes(ArrayList<Chromosome> upop, Chromosome orig_chr, boolean[] mask, int p, int r, int step, int mod) {
	  if (p == r - 1) {
		  mask[p] = false;
		  upop.add( this.buildChromosome(orig_chr, mask, step, mod) );
		  mask[p] = true;
		  upop.add( this.buildChromosome(orig_chr, mask, step, mod) );
	  }
	  else {
		  mask[p] = false;
		  this.buildAllChromosomes(upop, orig_chr, mask, p + 1, r, step, mod);
		  mask[p] = true;
		  this.buildAllChromosomes(upop, orig_chr, mask, p + 1, r, step, mod);
	  }
  }
  
  private Chromosome buildChromosome(Chromosome c, boolean[] mask, int step, int mod) {
	  int i, g, start, end;
	  Chromosome c_tmp;
	  
	  c_tmp = new Chromosome( c.getGenes() );
	  
	  end = 0;
	  for (i=1; i <= mask.length; i++) {
		  start = end;
		  end = ( (mask.length - i) >= mod ) ? (start + step) : (start + step + 1);
		  
		  if ( mask[i - 1] ) {
			  for (g=start; g < end; g++)
				  c_tmp.getGene(g).invert(this.dataset.getAttributeType(g), this.dataset.getMin(g), this.dataset.getMax(g));
		  }
	  }
	  
	  c_tmp.forceConsistency();
	  
	  return c_tmp;
  }
  
  private void evaluate(ArrayList<Chromosome> upop, int start_index, int end_index) {
	  for (int i=start_index; i < end_index; i++)
		  this.computeFitness( upop.get(i) );
  }
    
  private void crossover(ArrayList<Chromosome> upop) {
	  int g;
	  Chromosome parent1, parent2, offspring;
	  Gene[] genes_offspring;
	  
	  if (Randomize.Rand() < this.pc) {
		  parent1 = this.tournamentSelection(upop);
		  parent2 = this.tournamentSelection(upop);
		  
		  if (! parent1.equals(parent2)) {
		  
			  genes_offspring = new Gene[this.nAttr];
		  
			  for (g=0; g < this.nAttr; g++)
				  genes_offspring[g] = (Randomize.Rand() < 0.5) ? parent1.getGene(g).copy() : parent2.getGene(g).copy();
			  
			  offspring = new Chromosome(genes_offspring);
			  offspring.forceConsistency();
		  }
		  else offspring = new Chromosome( parent1.getGenes() );
		  
		  upop.add(offspring);
	  }
  }
  
  private void mutate(ArrayList<Chromosome> upop) {
	  int i, g, cnt_hit = 0;
	  double adaptive_ps, type_attr, min_attr, max_attr, top;
	  Gene gene;
	  Chromosome chr, best_chr;
	  
	  best_chr = upop.get(0);
	  
	  for (i=this.uPopSize; i < upop.size(); i++)
		  if ( upop.get(i).equals(best_chr) ) cnt_hit++;
	  
	  for (i=0; (i < this.uPopSize) && (upop.size() < (this.uPopSize * 2)); i++) {
		  adaptive_ps = this.pmMin + cnt_hit * ((this.pmMax - this.pmMin) / (upop.size() - this.uPopSize));

		  if (Randomize.Rand() < adaptive_ps) {
			  chr = new Chromosome( upop.get(i).getGenes() );
			  
			  g = Randomize.Randint(0, this.nAttr);
			  gene = chr.getGene(g);
			  
			  type_attr = this.dataset.getAttributeType(g);
			  min_attr = this.dataset.getMin(g);
			  max_attr = this.dataset.getMax(g);
			  
			  if (type_attr != myDataset.NOMINAL) {
				  if (type_attr == myDataset.REAL) {
					  if (Randomize.Rand() < 0.5) {
						  if (Randomize.Rand() < 0.5) {
							  top = Math.max(gene.getUpperBound() - this.maxAmplitudes[g], min_attr);
							  gene.setLowerBound(Randomize.RanddoubleClosed(top, gene.getLowerBound()));
						  }
						  else  gene.setLowerBound(Randomize.Randdouble(gene.getLowerBound(), gene.getUpperBound()));
					  }
					  else {
						  if (Randomize.Rand() < 0.5) {
							  top = Math.min(gene.getLowerBound() + this.maxAmplitudes[g], max_attr);
							  gene.setUpperBound(Randomize.RanddoubleClosed(gene.getUpperBound(), top));
						  }
						  else  gene.setUpperBound(Randomize.RanddoubleClosed(gene.getLowerBound()+0.0001, gene.getUpperBound()));
					  }				  
				  }
				  else {
					  if (Randomize.Rand() < 0.5) {
						  if (Randomize.Rand() < 0.5) {
							  top = Math.max(gene.getUpperBound() - this.maxAmplitudes[g], min_attr);
							  gene.setLowerBound(Randomize.RandintClosed((int)top, (int)gene.getLowerBound()));
						  }
						  else  gene.setLowerBound(Randomize.Randint((int)gene.getLowerBound(), (int)gene.getUpperBound()));
					  }
					  else {
						  if (Randomize.Rand() < 0.5) {
							  top = Math.min(gene.getLowerBound() + this.maxAmplitudes[g], max_attr);
							  gene.setUpperBound(Randomize.RandintClosed((int)gene.getUpperBound(), (int)top));
						  }
						  else  gene.setUpperBound(Randomize.RandintClosed((int)gene.getLowerBound() + 1, (int)gene.getUpperBound()));
					  }
				  }
			  }
			  else {
				  top = Randomize.RandintClosed((int)min_attr, (int)max_attr);
				  gene.setLowerBound(top);
				  gene.setUpperBound(top);
			  }
			  
			  gene.setIsPositiveInterval( (Randomize.RandintClosed(0, 1) == 1) ? true : false );
			  gene.setActAs( Randomize.RandintClosed(Gene.NOT_INVOLVED, Gene.CONSEQUENT) );
			  
			  chr.forceConsistency();
			  upop.add(chr);
			  
			  if ( chr.equals(best_chr) ) cnt_hit++;
		  }
	  }
  }
  
  private void uniformOperator(ArrayList<Chromosome> upop) {
	  int r, step, mod;
	  ArrayList<Integer> diff_pos;
	  ArrayList<Double> new_values;
	  Chromosome chr1, chr2;
	  
	  r = 2;
	  chr1 = upop.get(0);
	  chr2 = upop.get(1);
	  
	  diff_pos = this.getDifferentPositions(chr1, chr2);
	  
	  new_values = this.buildNewValues(chr1, chr2, diff_pos);
	  step = new_values.size() / r;
	  mod = new_values.size() % r;
	  
	  this.buildCombinationsOfNewValues(upop, chr1, new_values, diff_pos, new boolean[r], 0, r, step, mod);
  }
  
  private ArrayList<Integer> getDifferentPositions(Chromosome chr1, Chromosome chr2) {
	  int g;
	  ArrayList<Integer> diff_pos = new ArrayList<Integer>();
	  
	  for (g=0; g < this.nAttr; g++) {
		  if ( chr1.getGene(g).getActAs() != chr2.getGene(g).getActAs() ) diff_pos.add(g * 4);
		  if ( chr1.getGene(g).getIsPositiveInterval() != chr2.getGene(g).getIsPositiveInterval() ) diff_pos.add((g * 4) + 1);
		  if ( chr1.getGene(g).getLowerBound() != chr2.getGene(g).getLowerBound() ) diff_pos.add((g * 4) + 2);
		  if ( chr1.getGene(g).getUpperBound() != chr2.getGene(g).getUpperBound() ) diff_pos.add((g * 4) + 3);
	  }
	  
	  return diff_pos;
  }
  
  private ArrayList<Double> buildNewValues(Chromosome chr1, Chromosome chr2, ArrayList<Integer> diff_pos) {
	  int d, n, g, p;
	  ArrayList<Double> new_values = new ArrayList<Double>();
	  
	  for (d=0; d < diff_pos.size(); d++) {
		  n = diff_pos.get(d);
		  p = n % 4;
		  g = (n - p) / 4;
		  
		  switch (p) {
		  case 0:
			  new_values.add( (double)Randomize.RandintClosed(Gene.NOT_INVOLVED, Gene.CONSEQUENT) );
			  break;
		  case 1:
			  new_values.add( (double)Randomize.RandintClosed(0, 1) );
			  break;
		  case 2:
			  if (this.dataset.getAttributeType(g) == myDataset.REAL) new_values.add( (chr1.getGene(g).getLowerBound() + chr2.getGene(g).getLowerBound()) / 2.0 );
			  else new_values.add( (double)Math.round( (chr1.getGene(g).getLowerBound() + chr2.getGene(g).getLowerBound()) / 2.0 ) );
			  break;
		  case 3:
			  if (this.dataset.getAttributeType(g) == myDataset.REAL) new_values.add( (chr1.getGene(g).getUpperBound() + chr2.getGene(g).getUpperBound()) / 2.0 );
			  else new_values.add( (double)Math.round( (chr1.getGene(g).getUpperBound() + chr2.getGene(g).getUpperBound()) / 2.0 ) );
		  }
	  }
	  
	  return new_values;
  }
  
  private void buildCombinationsOfNewValues(ArrayList<Chromosome> upop, Chromosome orig_chr, ArrayList<Double> orig_values, ArrayList<Integer> diff_pos, boolean[] mask, int p, int r, int step, int mod) {
	  if (p == r - 1) {
		  mask[p] = false;
		  upop.add( this.buildChromosomeFromDifferentValues(orig_chr, orig_values, diff_pos, mask, step, mod) );
		  mask[p] = true;
		  upop.add( this.buildChromosomeFromDifferentValues(orig_chr, orig_values, diff_pos, mask, step, mod) );
	  }
	  else {
		  mask[p] = false;
		  this.buildCombinationsOfNewValues(upop, orig_chr, orig_values, diff_pos, mask, p + 1, r, step, mod);
		  mask[p] = true;
		  this.buildCombinationsOfNewValues(upop, orig_chr, orig_values, diff_pos, mask, p + 1, r, step, mod);
	  }
  }
  
  private Chromosome buildChromosomeFromDifferentValues(Chromosome orig_chr, ArrayList<Double> orig_values, ArrayList<Integer> diff_pos, boolean[] mask, int step, int mod) {
	  int i, d, n, p, g, start, end;
	  double v;
	  Chromosome c_tmp;
	  
	  c_tmp = new Chromosome( orig_chr.getGenes() );
	  
	  if (step != 0.0) {
		  end = 0;
		  for (i=1; i <= mask.length; i++) {
			  start = end;
			  end = ( (mask.length - i) >= mod ) ? (start + step) : (start + step + 1);
			  
			  for (d=start; d < end; d++) {
				  n = diff_pos.get(d);
				  v = orig_values.get(d);
				  p = n % 4;
				  g = (n - p) / 4;
				  
				  switch (p) {
				  case 0:
					  if ( mask[i - 1] ) {
						  switch ( (int)v ) {
						  case Gene.NOT_INVOLVED:
							  c_tmp.getGene(g).setActAs( Gene.ANTECEDENT );
							  break;
						  case Gene.ANTECEDENT:
							  c_tmp.getGene(g).setActAs( Gene.CONSEQUENT);
							  break;
						  case Gene.CONSEQUENT:
							  c_tmp.getGene(g).setActAs( Gene.NOT_INVOLVED );
						  }
					  }
					  else c_tmp.getGene(g).setActAs( (int)v );
					  break;
				  case 1:
					  if ( mask[i - 1] ) c_tmp.getGene(g).setIsPositiveInterval( (v == 1.0) ? false : true );
					  else c_tmp.getGene(g).setIsPositiveInterval( (v == 1.0) ? true : false );
					  break;
				  case 2:
					  if ( mask[i - 1] ) {
						  if (this.dataset.getAttributeType(g) != myDataset.NOMINAL) {
							  if (this.dataset.getAttributeType(g) == myDataset.REAL) c_tmp.getGene(g).setLowerBound( Randomize.RandClosed() * (v - this.dataset.getMin(g)) + this.dataset.getMin(g) );
							  else c_tmp.getGene(g).setLowerBound( Randomize.RandintClosed((int)this.dataset.getMin(g), (int)v) );
						  }
						  else {
							  if (v == this.dataset.getMax(g)) c_tmp.getGene(g).setLowerBound(this.dataset.getMin(g));
							  else c_tmp.getGene(g).setLowerBound(v + 1);
						  }
					  }
					  else c_tmp.getGene(g).setLowerBound(v);
					  break;
				  case 3:
					  if ( mask[i - 1] ) {
						  if (this.dataset.getAttributeType(g) != myDataset.NOMINAL) {
							  if (this.dataset.getAttributeType(g) == myDataset.REAL) c_tmp.getGene(g).setUpperBound( Randomize.RandClosed() * (this.dataset.getMax(g) - v) + v );
							  else c_tmp.getGene(g).setUpperBound( Randomize.RandintClosed((int)v, (int)this.dataset.getMax(g)) );
						  }
						  else {
							  if (v == this.dataset.getMax(g)) c_tmp.getGene(g).setUpperBound(this.dataset.getMin(g));
							  else c_tmp.getGene(g).setUpperBound(v + 1);
						  }
					  }
					  else c_tmp.getGene(g).setUpperBound(v);
				  }
			  }
		  }
		  
		  c_tmp.forceConsistency();
	  }
	  
	  return c_tmp;
  }
  
  private Chromosome tournamentSelection(ArrayList<Chromosome> upop) {
	  int rnd_index, cnt = 0;
	  ArrayList<Chromosome> rnd_chrs = new ArrayList<Chromosome>();
	  
	  while ( cnt < this.tournamentSize ) {
		  rnd_index = Randomize.Randint(0, this.uPopSize);
		  rnd_chrs.add( upop.get(rnd_index) );
		  cnt++;
	  }
	  
	  Collections.sort(rnd_chrs);
	  
	  return ( rnd_chrs.get(0) );
  }
  
  private void computeFitness(Chromosome c) {
	  double all_sup, ant_sup, conf;
	  ArrayList<Integer> involved_attrs, covered_tids;
	  
	  involved_attrs = c.getIndexOfInvolvedGenes();
	  covered_tids = this.countSupport(c.getGenes(), involved_attrs);
	  all_sup = (double)covered_tids.size() / (double)this.nTrans;
	  
	  if (all_sup > 0.0) {
		  ant_sup = (double)this.countSupport(c.getGenes(), c.getIndexOfAntecedentGenes()).size() / (double)this.nTrans;
		  conf = all_sup / ant_sup;
		  
		  c.setFitness( (this.a1 * all_sup) + (this.a2 * conf) - (this.a3 * involved_attrs.size()) - (this.a4 * this.sumInterval(c.getGenes(), involved_attrs)) );
		  c.setRuleSupport(all_sup);
		  c.setRuleConfidence(conf);
	  
		  for (int t=0; t < covered_tids.size(); t++)
			  c.addCoveredTID( covered_tids.get(t) );
	  }
	  else c.setFitness(this.minFitnessValue);
  }
    
  private double sumInterval(Gene[] genes, ArrayList<Integer> index_list) {
	  double lb, ub, amp, interval, sum_interval = 0.0;
	  int g;
	  
	  for (int i=0; i < index_list.size(); i++) {
		  g = index_list.get(i);
		  
		  lb = genes[g].getLowerBound();
		  ub = genes[g].getUpperBound();
		  amp = ub - lb;
		  
		  interval = ( genes[g].getIsPositiveInterval() ) ? amp / this.af : (this.maxAmplitudes[g] - amp) / this.af;			  
		  sum_interval += interval;
	  }
	  
	  return sum_interval;
  }
  
  private void computeAdjustedFitness(ArrayList<Chromosome> upop) {
	  int m1, m2, i, j, k, t, a, sum_marked;
	  boolean ok;
	  Chromosome c;
	  ArrayList<Integer> ant_attrs, cons_attrs, covered_tids;
	  
	  int[][] marked_attr = new int[this.nTrans][this.nAttr];
	  for (m1=0; m1 < marked_attr.length; m1++)
		  for (m2=0; m2 < marked_attr[m1].length; m2++)
			  marked_attr[m1][m2] = this.ATTRIBUTE_NOT_COVERED;
		  
	  for (i=0; i < this.uPopSize; i++) {
		  c = upop.get(i);
		  covered_tids = c.getCoveredTIDs();
		  ant_attrs = c.getIndexOfAntecedentGenes();
		  cons_attrs = c.getIndexOfConsequentGenes();
		  
		  for (j=0; j < covered_tids.size(); j++) {
			  t = covered_tids.get(j);
			  
			  for (k=0; k < ant_attrs.size(); k++) {
				  a = ant_attrs.get(k);
				  if ( marked_attr[t][a] == this.ATTRIBUTE_NOT_COVERED ) marked_attr[t][a] = this.ATTRIBUTE_COVERED_BY_ANTECEDENT;
				  else if ( marked_attr[t][a] == this.ATTRIBUTE_COVERED_BY_CONSEQUENT )	marked_attr[t][a] = this.ATTRIBUTE_COVERED_BY_BOTH;
			  }
			  
			  for (k=0; k < cons_attrs.size(); k++) {
				  a = cons_attrs.get(k);
				  if ( marked_attr[t][a] == this.ATTRIBUTE_NOT_COVERED ) marked_attr[t][a] = this.ATTRIBUTE_COVERED_BY_CONSEQUENT;
				  else if ( marked_attr[t][a] == this.ATTRIBUTE_COVERED_BY_ANTECEDENT )	marked_attr[t][a] = this.ATTRIBUTE_COVERED_BY_BOTH;
			  }
		  }
	  }
	  
	  for (i=this.uPopSize; i < upop.size(); i++) {
		  c = upop.get(i);
		  covered_tids = c.getCoveredTIDs();
		  ant_attrs = c.getIndexOfAntecedentGenes();
		  cons_attrs = c.getIndexOfConsequentGenes();
		  sum_marked = 0;
		  
		  for (j=0; j < covered_tids.size(); j++) {
			  t = covered_tids.get(j);
			  ok = true;
			  
			  for (k=0; k < ant_attrs.size() && ok; k++) {
				  a = ant_attrs.get(k);
				  if ( ( marked_attr[t][a] == this.ATTRIBUTE_NOT_COVERED ) || (marked_attr[t][a] == this.ATTRIBUTE_COVERED_BY_CONSEQUENT) )	ok = false;
			  }
			  
			  for (k=0; k < cons_attrs.size() && ok; k++) {
				  a = cons_attrs.get(k);
				  if ( ( marked_attr[t][a] == this.ATTRIBUTE_NOT_COVERED ) || (marked_attr[t][a] == this.ATTRIBUTE_COVERED_BY_ANTECEDENT) )	ok = false;
			  }
			  
			  if (ok) sum_marked++;
		  }
		  
		  if (sum_marked > 0) c.setFitness(c.getFitness() - (this.a5 * sum_marked));
	  }
  }
    
  private ArrayList<Integer> countSupport(Gene[] genes, ArrayList<Integer> index_list) {
	  ArrayList<Integer> tid_list = new ArrayList<Integer>();
	  double[][] trans = dataset.getTrueTransactions();
	  int t, i, g;
	  double lb, ub;
	  boolean ok;

	  for (t=0; t < this.nTrans; t++) {
		  ok = true;
		  
		  for (i=0; i < index_list.size() && ok; i++) {
			  g = index_list.get(i);
			  lb = genes[g].getLowerBound();
			  ub = genes[g].getUpperBound();
			  
			  if ( genes[g].getIsPositiveInterval() ) {
				  if ((trans[t][g] < lb) || (trans[t][g] > ub))		ok = false;
			  }
			  else {
				  if ((trans[t][g] >= lb) && (trans[t][g] <= ub))	ok = false;
			  }
		  }
		  
		  if (ok) tid_list.add(t);
	  }
	  
	  return tid_list;
  }
  
  private void adjustIntervals(ArrayList<Chromosome> upop) {
	  int i, g;
	  Chromosome chr;
	  Gene[] genes;
	  ArrayList<Integer> cov_tids;
	  
	  double[][] trans = this.dataset.getTrueTransactions();
	  
	  for (i=0; i < upop.size(); i++) {
		  chr = upop.get(i);
		  genes = chr.getGenes();
		  cov_tids = chr.getCoveredTIDs();
		  
		  for (g=0; g < genes.length; g++) {			  
			  if ( (this.dataset.getAttributeType(g) != myDataset.NOMINAL) && (genes[g].getActAs() != Gene.NOT_INVOLVED) ) {		  
				  if ( genes[g].getIsPositiveInterval() ) this.adjustPositiveInterval(genes[g], g, cov_tids, trans);
				  else {
					  if (this.dataset.getAttributeType(g) == myDataset.REAL) this.adjustNegativeInterval(genes[g], g, cov_tids, trans, 0.0001);
					  else this.adjustNegativeInterval(genes[g], g, cov_tids, trans, 1.0);
				  }
			  }
		  }
	  }
  }
  
  private void adjustPositiveInterval(Gene gene, int g, ArrayList<Integer> cov_tids, double[][] trans) {
	  int r, t;
	  double min, max;
	  
	  min = gene.getUpperBound();
	  max = gene.getLowerBound();
	  
	  for (r=0; r < cov_tids.size(); r++) {
		  t = cov_tids.get(r);
		  if (trans[t][g] < min) min = trans[t][g];
		  if (trans[t][g] > max) max = trans[t][g];
	  }
	  
	  gene.setLowerBound(min);
	  gene.setUpperBound(max);
  }
  
  private void adjustNegativeInterval(Gene gene, int g, ArrayList<Integer> cov_tids, double[][] trans, double delta) {
	  int r, t;
	  double min, max;
	  
	  min = this.dataset.getMax(g) + delta;
	  max = this.dataset.getMin(g) - delta;
	  
	  for (r=0; r < cov_tids.size(); r++) {
		  t = cov_tids.get(r);
		  if ( (trans[t][g] < min) && (trans[t][g] > gene.getUpperBound()) ) min = trans[t][g];
		  if ( (trans[t][g] > max) && (trans[t][g] < gene.getLowerBound()) ) max = trans[t][g];
	  }
	  
	  gene.setLowerBound(max + delta);
	  gene.setUpperBound(min - delta);
  }
  
}
