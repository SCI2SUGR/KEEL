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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.GAR;

import java.util.*;
import org.core.Randomize;

public class GARProcess
{
	private myDataset ds;
	private boolean[] marked;
	private double allow_ampl[];
	ArrayList<Chromosome> freqIts;
	ArrayList<AssociationRule> assoc_rules;

	private int nItemset;
	private int nGen;
	private int popsize;
	private double ps;
	private double pc;
	private double pm;
	private double w;
	private double y;
	private double u;
	private int limit;
	private double avg_ampl;
	
	
  public GARProcess(myDataset ds, int nItemset, int nGen, int popsize, double ps, double pc, double pm, double w, double y, double u, double AF) {
	  int i;

	  this.ds = ds;
	  this.marked = new boolean[this.ds.getnTrans()];

	  this.nItemset = nItemset;
	  this.nGen = nGen;
	  this.popsize = popsize;
	  this.ps = ps;
	  this.pc = pc;
	  this.pm = pm;
	  this.w = w;
	  this.y = y;
	  this.u = u;
	  this.limit = (int) Math.ceil(popsize * ps);
	  
	  this.allow_ampl = new double[this.ds.getnVars()];
	  for (i=0; i < this.allow_ampl.length; i++)  this.allow_ampl[i] = (this.ds.getMax(i) - this.ds.getMin(i)) / AF;
  }

  
  public void run() {
	  ArrayList<Chromosome> popNew;
	  Chromosome chromoBest;
	  int nTrans = this.ds.getnTrans();

	  this.avg_ampl = 0.0;
	  this.freqIts = new ArrayList<Chromosome>();
	  for (int i=0; i < nTrans; i++)  this.marked[i] = false;

	  while (freqIts.size() < this.nItemset) {
		  System.out.println("Number of Itemsets Selected: " + this.freqIts.size());
		  int nGn = 0;
		  
		  ArrayList<Chromosome> popCurrent = this.initialize();
		  
		  while (nGn < this.nGen) {
			  System.out.println("Generation: " + nGn);
			  popNew = this.select(popCurrent);
			  this.crossover(popNew);
			  this.mutate(popNew);
		  
			  popCurrent = popNew;
			  nGn++;
		  }

	  	  Collections.sort(popCurrent);
		  chromoBest = popCurrent.get(0);
		  this.penalizeRecordsCoveredBy(chromoBest);
		  this.freqIts.add(chromoBest.copy());

		  this.avg_ampl += avgAmplitudeBestChromosome(chromoBest);
	  }
	  
	  this.genRules();
  }

  public void printReport (double minConfidence, double minSupport) {
	  int i, countRules, length;

	  System.out.println("Number of Frequent Itemsets generated: " + this.freqIts.size());
	  System.out.println("Average Amplitude of the intervals: " + (this.avg_ampl / this.freqIts.size()));
	  
	  countRules = length = 0;
	  for (i=0; i < this.assoc_rules.size(); i++)
		  if (((this.assoc_rules.get(i)).getConfidence() >= minConfidence) && ((this.assoc_rules.get(i)).getAll_support() >= minSupport)) {
			countRules++;
			length += (this.assoc_rules.get(i)).getLengthAnt();
		  }
	  
	  System.out.println("Number of Association Rules generated: " + countRules);
	  System.out.println("Average Length of the Rules generated: " + length / (double) countRules);
	  System.out.println("Number of Covered Records (%): " + (100.0 * this.numCoveredRecords (minConfidence, minSupport)) / this.ds.getnTrans());
  }  

  public ArrayList<AssociationRule> getSetRules (double minConfidence, double minSupport) {
	  int i;
	  ArrayList<AssociationRule> selectRules = new ArrayList<AssociationRule>();
	  AssociationRule rule;
	  
	  for (i=0; i < this.assoc_rules.size(); i++) {
		  rule = this.assoc_rules.get(i);
		  if ((rule.getConfidence() >= minConfidence) && (rule.getAll_support() >= minSupport))  selectRules.add(rule.copy());
	  }

	  return selectRules;
  }  
  
  private ArrayList<Chromosome> initialize() {
	  ArrayList<Chromosome> popInit = new ArrayList<Chromosome>();
	  int n_genes, nVars, attr;
	  double lb, ub, max_attr, min_attr;
	  double val1, val2, top;

	  nVars = this.ds.getnVars();
	  boolean[] sel_attr = new boolean[nVars];
	  
	  while (popInit.size() < this.popsize) {  
		  for (int i=0; i < nVars; i++)  sel_attr[i] = false;

		  n_genes = Randomize.RandintClosed(2, nVars);
		  Gene[] genes = new Gene[n_genes];
		  
		  for (int g=0; g < n_genes; g++) {
			  genes[g] = new Gene();
			  
			  attr = Randomize.Randint (0, nVars);  
			  
			  while (sel_attr[attr])  attr = (attr + 1) % nVars;
			  sel_attr[attr] = true;
			  
			  genes[g].setAttr(attr);
			  genes[g].setType( this.ds.getAttributeType(attr) );
			  
			  max_attr = this.ds.getMax(attr);
			  min_attr = this.ds.getMin(attr);
			  
			  if ( genes[g].getType() != Gene.NOMINAL ) {
				  if ( genes[g].getType() == Gene.REAL ) {
					  lb = Randomize.RanddoubleClosed(min_attr, max_attr);
					  top = Math.min(lb + this.allow_ampl[attr], max_attr);
					  ub = Randomize.RanddoubleClosed(lb + 0.0001, top);
				  }
				  else {
					  lb = Randomize.RandintClosed((int) min_attr, (int) max_attr);
					  top = Math.min(lb + this.allow_ampl[attr], max_attr);
					  ub = Randomize.RandintClosed((int) lb + 1, (int) top);
				  }
			  }
			  else lb = ub = Randomize.RandintClosed((int) min_attr, (int) max_attr);
			  
			  genes[g].setL(lb);
			  genes[g].setU(ub);
		  }
		  
		  Chromosome c = new Chromosome(genes);
		  this.fitness(c);
		  
		  if (c.getFit() > -5)  popInit.add(c);
	  }
	  
	  return popInit;
  }
  
  
  private ArrayList<Chromosome> select(ArrayList<Chromosome> pop) {
	  ArrayList<Chromosome> popTmp = new ArrayList<Chromosome>();

	  Collections.sort(pop);
	  
	  for (int i = 0; i <= limit; i++)  popTmp.add((pop.get(i)).copy());
	  
	  return popTmp;
  }
  
  
  private void crossover(ArrayList<Chromosome> pop) {
	  Chromosome p1, p2, off1, off2, off_best;
	  int posP1, posP2, posOff1, posOff2;
	  Gene gen1, gen2;

	  
	  while (pop.size() < this.popsize) {
		  p1 = pop.get(Randomize.Randint (0, pop.size()));
		  p2 = pop.get(Randomize.Randint (0, pop.size()));
		  
		  if (Randomize.Rand() < this.pc) {  
			  posP1 = posP2 = 0;
			  posOff1 = posOff2 = 0;

			  Gene[] genesOff1 = new Gene[this.ds.getnVars()];
			  Gene[] genesOff2 = new Gene[this.ds.getnVars()];
			  while ((posP1 < p1.length()) && (posP2 < p2.length())) {
				  gen1 = p1.getGen(posP1);
				  gen2 = p2.getGen(posP2);

				  if ((gen1.getAttr()) == (gen2.getAttr())) {
					  if (Randomize.Rand() < 0.5)  genesOff1[posOff1] = gen1.copy();
					  else  genesOff1[posOff1] = gen2.copy();

					  if (Randomize.Rand() < 0.5)  genesOff2[posOff2] = gen1.copy();
					  else  genesOff2[posOff2] = gen2.copy();

					  posOff1++;
					  posOff2++;
					  posP1++;
					  posP2++;
				  }
				  else if ((gen1.getAttr()) < (gen2.getAttr())) {
					  if (Randomize.Rand() < 0.5) {
						  genesOff1[posOff1] = gen1.copy();
						  posOff1++;
					  }
					  else {
						  genesOff2[posOff2] = gen1.copy();
						  posOff2++;
					  }

					  posP1++;
				  }
				  else {
					  if (Randomize.Rand() < 0.5) {
						  genesOff1[posOff1] = gen2.copy();
						  posOff1++;
					  }
					  else {
						  genesOff2[posOff2] = gen2.copy();
						  posOff2++;
					  }

					  posP2++;
				  }
			  }

			  if (posP1 < p1.length()) {
				  while (posP1 < p1.length()) {
					  gen1 = p1.getGen(posP1);
					  if (Randomize.Rand() < 0.5) {
						  genesOff1[posOff1] = gen1.copy();
						  posOff1++;
					  }
					  else {
						  genesOff2[posOff2] = gen1.copy();
						  posOff2++;
					  }

					  posP1++;
				  }
			  }
			  else if (posP2 < p2.length()) {
				  while (posP2 < p2.length()) {
					  gen2 = p2.getGen(posP2);
					  if (Randomize.Rand() < 0.5) {
						  genesOff1[posOff1] = gen2.copy();
						  posOff1++;
					  }
					  else {
						  genesOff2[posOff2] = gen2.copy();
						  posOff2++;
					  }
					  posP2++;
				  }
			  }

			  off1 = new Chromosome (genesOff1, posOff1);
			  off2 = new Chromosome (genesOff2, posOff2);

			  if (off1.length() < 2)  off1.setFit(-5.0);
			  else  this.fitness(off1);			  

			  if (off2.length() < 2)  off2.setFit(-5.0);
			  else  this.fitness(off2);			  
		  
			  if (off1.getFit() > off2.getFit())  off_best = off1;
			  else  off_best = off2;
			  
			  if (off_best.getFit() > -5)  pop.add(off_best);
		  }
	  }
  }  



  private void mutate(ArrayList<Chromosome> pop) {
	  int i, index, attr;
	  double lb_new, ub_new, max_attr, min_attr, top;
	  Chromosome chromo;
	  Gene g;

	  for (i=0; i < this.popsize; i++) {
		  if (Randomize.Rand() < this.pm) {
			  chromo = pop.get(i);
			  index = Randomize.Randint(0, chromo.length());
			  
			  g = chromo.getGen(index);
			  attr = g.getAttr();
			  
			  max_attr = this.ds.getMax(attr);
			  min_attr = this.ds.getMin(attr);
			  
			  if (g.getType() != Gene.NOMINAL) {
				  if (g.getType() == Gene.REAL) {
					  if (Randomize.Rand() < 0.5) {
						  if (Randomize.Rand() < 0.5) {
							  top = Math.max(g.getU() - this.allow_ampl[attr], min_attr);
							  g.setL(Randomize.RanddoubleClosed(top, g.getL()));
						  }
						  else  g.setL(Randomize.Randdouble(g.getL(), g.getU()));
					  }
					  else {
						  if (Randomize.Rand() < 0.5) {
							  top = Math.min(g.getL() + this.allow_ampl[attr], max_attr);
							  g.setU(Randomize.RanddoubleClosed(g.getU(), top));
						  }
						  else  g.setU(Randomize.RanddoubleClosed(g.getL()+0.0001, g.getU()));
					  }				  
				  }
				  else {
					  if (Randomize.Rand() < 0.5) {
						  if (Randomize.Rand() < 0.5) {
							  top = Math.max(g.getU() - this.allow_ampl[attr], min_attr);
							  g.setL(Randomize.RandintClosed((int) top, (int) g.getL()));
						  }
						  else  g.setL(Randomize.Randint((int) g.getL(), (int) g.getU()));
					  }
					  else {
						  if (Randomize.Rand() < 0.5) {
							  top = Math.min(g.getL() + this.allow_ampl[attr], max_attr);
							  g.setU(Randomize.RandintClosed((int) g.getU(), (int) top));
						  }
						  else  g.setU(Randomize.RandintClosed((int) g.getL() + 1, (int) g.getU()));
					  }
				  }
			  }
			  else {
				  top = Randomize.RandintClosed((int) min_attr, (int) max_attr);
				  g.setL(top);
				  g.setU(top);
			  }
		  
			  this.fitness(chromo);
		  }
	  }
  }

  
  private void fitness(Chromosome c) {
	  double nTrans = (double) this.ds.getnTrans();

	  ArrayList<Integer> tid_lst = countSupport(c.getGenes());

	  if (tid_lst.size() < 1.0) {
		  c.setFit (-5.0);
		  c.setSupport (0.0);
	  }
	  else {
		  double cov = tid_lst.size() / nTrans;
		  int cnt = 0;
		  for (int t=0; t < tid_lst.size(); t++) {
			  if (this.marked[tid_lst.get(t)])  cnt++;
		  }

		  double already_covered = cnt / nTrans;
		  double ampl = this.amplitude(c.getGenes());
		  double nAttr = (double) c.getGenes().length / (double) this.ds.getnVars();
	  
		  c.setFit (cov - (already_covered * this.w) - (ampl * this.y) + (nAttr * this.u));
		  c.setSupport (cov);
	  }
  }
/*
  private double amplitude(Gene[] genes) {  
	  int g, attr;
	  double curr_amp, attr_amp, avg_curr_amp, avg_attr_amp;
	  double sum_curr_amp = 0.0, sum_attr_amp = 0.0, max_curr_amp = 0.0, max_attr_amp = 0.0;
	  
	  for (g=0; g < genes.length; g++) {
		  attr = genes[g].getAttr();
		  
		  curr_amp = genes[g].getU() - genes[g].getL();
		  attr_amp = this.allow_ampl[attr];
		  
		  sum_curr_amp += curr_amp;
		  sum_attr_amp += attr_amp;
		  
		  if (curr_amp > max_curr_amp)  max_curr_amp = curr_amp;  
		  if (attr_amp > max_attr_amp)  max_attr_amp = attr_amp;
	  }
	  
	  avg_curr_amp = sum_curr_amp / genes.length;
	  avg_attr_amp = sum_attr_amp / genes.length;
		  
	  return ((avg_curr_amp + max_curr_amp) / (avg_attr_amp + max_attr_amp));
  }
*/    
  
  private double amplitude(Gene[] genes) {  
	  int g, attr;
	  double curr_amp, attr_amp, avg_amp, avg_curr;
	  double max_avg_curr = 0.0, sum_curr_amp = 0.0, sum_attr_amp = 0.0;
	  
	  for (g=0; g < genes.length; g++) {
		  attr = genes[g].getAttr();
		  
		  curr_amp = genes[g].getU() - genes[g].getL();
		  attr_amp = this.allow_ampl[attr];
		  
		  sum_curr_amp += curr_amp;
		  sum_attr_amp += attr_amp;

		  avg_curr = curr_amp / attr_amp;
		  
		  if (avg_curr > max_avg_curr) {
			  max_avg_curr = avg_curr;
		  }
	  }

//	  avg_curr_amp = sum_curr_amp / genes.length;
//	  avg_attr_amp = sum_attr_amp / genes.length;

	  avg_amp = sum_curr_amp / sum_attr_amp;

	  return ((avg_amp + max_avg_curr) / 2.0);
  }

/*
  private double amplitude(Gene[] genes) {  
	  int g, attr;
	  double curr_amp, attr_amp, avg_curr_amp, avg_attr_amp;
	  double sum_curr_amp = 0.0, sum_attr_amp = 0.0, max_curr_amp = 0.0, max_attr_amp = 0.0;
	  
	  for (g=0; g < genes.length; g++) {
		  attr = genes[g].getAttr();
		  
		  curr_amp = genes[g].getU() - genes[g].getL();
		  attr_amp = this.allow_ampl[attr];
		  
		  sum_curr_amp += curr_amp;
		  sum_attr_amp += attr_amp;
		  
		  if (curr_amp > max_curr_amp) {
			  max_curr_amp = curr_amp;
			  max_attr_amp = attr_amp;
		  }
	  }
	  
	  avg_curr_amp = sum_curr_amp / genes.length;
	  avg_attr_amp = sum_attr_amp / genes.length;
		  
	  return ((avg_curr_amp + max_curr_amp) / (avg_attr_amp + max_attr_amp));
  }
*/
  
  private ArrayList<Integer> countSupport(Gene[] genes) {
	  ArrayList<Integer> tid_list = new ArrayList<Integer>();
	  double[][] trans = ds.getRealTransactions();
	  int attr, nTrans;
	  double lb, ub;
	  boolean ok;
	  
	  nTrans = this.ds.getnTrans();

	  for (int t=0; t < nTrans; t++) {
		  ok = true;
		  
		  for (int g=0; g < genes.length && ok; g++) {
			  attr = genes[g].getAttr();
			  lb = genes[g].getL();
			  ub = genes[g].getU();
			  
			  if ((trans[t][attr] < lb) || (trans[t][attr] > ub))   ok = false;
		  }
		  
		  if (ok) tid_list.add(t);
	  }
	  
	  return tid_list;
  }
  
  
  private void penalizeRecordsCoveredBy(Chromosome c) {
	  ArrayList<Integer> tid_lst = countSupport(c.getGenes());
	  
	  for (int t=0; t < tid_lst.size(); t++) {
		  int tr = tid_lst.get(t);
		  if (!this.marked[tr])  this.marked[tr] = true;
	  }	  
  }
  
  
  private void genRules() {
	  int i, j, g1, g2;
	  double all_sup, ant_sup; 
	  ArrayList<Integer> tid_lst_all, tid_lst_ant;
	  AssociationRule rule;
	  Chromosome chromo;
	  Gene[] genes_ant;

	  this.assoc_rules = new ArrayList<AssociationRule>();
	
	  for (i=0; i < freqIts.size(); i++) {
		  chromo = freqIts.get(i);
		  
		  tid_lst_all = countSupport(chromo.getGenes());
		  genes_ant = new Gene[chromo.length() - 1];

		  for (g1=0; g1 < chromo.length(); g1++) {
			  rule = new AssociationRule();

			  for (g2=0, j=0; g2 < chromo.length(); g2++) {
				  if (g1 != g2) {
					  rule.addAntecedent((chromo.getGen(g2)).copy());
					  genes_ant[j] = chromo.getGen(g2);
					  j++;
				  }
			  }
			  rule.addConsequent((chromo.getGen(g1)).copy());
				  			  
			  tid_lst_ant = this.countSupport(genes_ant);
			  all_sup = (double) tid_lst_all.size() / (double) this.ds.getnTrans();
			  ant_sup = (double) tid_lst_ant.size() / (double) this.ds.getnTrans();
					  
			  rule.setSupport (ant_sup);
			  rule.setAll_support (all_sup);
			  rule.setConfidence (all_sup / ant_sup);
					  			  
			  this.assoc_rules.add(rule);
		  }
	  }
  }
  
  
  private int numCoveredRecords (double minConfidence, double minSupport) {
	  int i, j, covered, nTrans;
	  ArrayList<Gene> ant;
	  ArrayList<Integer> tidCovered;
	  AssociationRule rule;
	  Gene[] genes;


	  for (i=0; i < this.marked.length; i++)  this.marked[i] = false;

	  nTrans = this.ds.getnTrans();

	  for (i=0; i < this.assoc_rules.size(); i++) {
		  rule = this.assoc_rules.get(i);
		  if ((rule.getConfidence() >= minConfidence) && (rule.getAll_support() >= minSupport)) {
			  ant = rule.getAntecedent();
			  genes = new Gene[ant.size()];
			  for (j=0; j < ant.size(); j++)  genes[j] = ant.get(j);
			  
			  tidCovered = countSupport(genes);
			  
			  for (j=0; j < tidCovered.size(); j++)  this.marked[tidCovered.get(j)] = true;
		  }
	  }

	  covered = 0;
	  for (i=0; i < this.marked.length; i++)
		  if (this.marked[i])  covered++;

	  return covered;
  }

  
  private double avgAmplitudeBestChromosome (Chromosome c) {
	  Gene[] g = c.getGenes();
	  double avg = 0.0;
	  
	  for (int i=0; i < g.length; i++)  avg += (g[i].getU() - g[i].getL());

	  return (avg / g.length);
  }  
}
