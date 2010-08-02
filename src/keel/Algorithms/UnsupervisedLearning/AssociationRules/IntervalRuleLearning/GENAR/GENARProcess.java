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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.GENAR;


import java.util.*;

import org.core.Randomize;

public class GENARProcess
{
	private myDataset ds;
	private double[] weights;
	private double allow_ampl[];
	ArrayList<Chromosome> bestRules;
	ArrayList<AssociationRule> assoc_rules;

	private int nRules;
	private int nGen;
	private int popsize;
	private double pc;
	private double pm;
	private double pf;
	private int limit;
	
	
  public GENARProcess(myDataset ds, int nRules, int nGen, int popsize, double ps, double pc, double pm, double pf, double AF) {
	  int i;

	  this.nRules = nRules;
	  this.nGen = nGen;
	  this.popsize = popsize;
	  this.pc = pc;
	  this.pm = pm;
	  this.pf = pf;

	  this.limit = (int) Math.ceil(popsize * ps);
	  this.ds = ds;
	  this.weights = new double[this.ds.getnTrans()];
	  
	  this.allow_ampl = new double[this.ds.getnVars()];
	  for (i=0; i < this.allow_ampl.length; i++)  this.allow_ampl[i] = (this.ds.getMax(i) - this.ds.getMin(i)) / AF;
  }

  
  public void run() {
	  ArrayList<Chromosome> popNew;
	  Chromosome chromoBest;
	  this.bestRules = new ArrayList<Chromosome>();

  	  for (int i=0; i < this.weights.length; i++) this.weights[i] = 1.0;

	  do {
		  System.out.println("Number of Rules Selected: " + this.bestRules.size());

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

		  chromoBest = this.chooseTheBest(popCurrent);
		  this.penalizeRecordsCoveredBy(chromoBest);
		  this.bestRules.add(chromoBest.copy());
	  } while ( ( this.bestRules.size() < this.nRules ) && ( ! this.allRecordsCovered() ) );

	  
	  this.genRules();
  }

  public void printReport (double minConfidence, double minSupport) {
	  int i, countRules;
	  
	  countRules = 0;
	  for (i=0; i < this.assoc_rules.size(); i++)
		  if (((this.assoc_rules.get(i)).getConfidence() >= minConfidence) && ((this.assoc_rules.get(i)).getAll_support() >= minSupport))  countRules++;
	  
	  System.out.println("Number of Association Rules generated: " + countRules);
	  System.out.println("Number of Covered Records (%): " + (100.0 * this.numCoveredRecords(minConfidence, minSupport)) / this.ds.getnTrans());
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
	  int nVars, attr, count, tr;
	  double lb, ub, max_attr, min_attr;
	  double top;

	  nVars = this.ds.getnVars();

	  count = 0;
	  
	  while ((popInit.size() < this.popsize) && (count < 100)) {
		  Gene[] genes = new Gene[nVars];
		  
		  for (int g=0; g < nVars; g++) {
			  genes[g] = new Gene();
			  
			  attr = g;  
			  
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
		  c.setFit(this.fitness(c));
		  
		  if (c.getFit() > 0) {
			  popInit.add(c);
			  count = 0;
		  }
		  else  count++;
	  }

	  while (popInit.size() < this.popsize) {
		  Gene[] genes = new Gene[nVars];
		  double[][] trans = this.ds.getRealTransactions();
		  tr = Randomize.Randint(0, this.ds.getnTrans());
		  
		  for (int g=0; g < nVars; g++) {
			  genes[g] = new Gene();

			  attr = g;  
			  
			  genes[g].setAttr(attr);
			  genes[g].setType( this.ds.getAttributeType(attr) );

  			  max_attr = this.ds.getMax(attr);
			  min_attr = this.ds.getMin(attr);

			  if ( genes[g].getType() != Gene.NOMINAL ) {
				  if ( genes[g].getType() == Gene.REAL ) {
					  lb = Math.max(trans[tr][attr] - (this.allow_ampl[attr] / 2.0), min_attr);
					  ub = Math.min(trans[tr][attr] + (this.allow_ampl[attr] / 2.0), max_attr);
				  }
				  else {
					  lb = Math.max(trans[tr][attr] - ((int) this.allow_ampl[attr] / 2), min_attr);
					  ub = Math.min(trans[tr][attr] + ((int) this.allow_ampl[attr] / 2), max_attr);
				  }
			  }
			  else lb = ub = trans[tr][attr];
			  
			  genes[g].setL(lb);
			  genes[g].setU(ub);
		  }
		  
		  Chromosome c = new Chromosome(genes);
		  c.setFit(this.fitness(c));
		  popInit.add(c);
	  }
	  
	  return popInit;
  }
  
  
  private ArrayList<Chromosome> select(ArrayList<Chromosome> pop) {
	  ArrayList<Chromosome> popTmp = new ArrayList<Chromosome>();

	  Collections.sort(pop);
	  
	  for (int i = 0; i <= this.limit; i++)  popTmp.add((pop.get(i)).copy());
	  
	  return popTmp;
  }
  
  
  private void crossover(ArrayList<Chromosome> pop) {
	  int i, pos, nVars;
	  Chromosome dad, mom, off1, off2, off_best;
	  Gene gen1, gen2;

	  nVars = this.ds.getnVars();
	  Gene[] genesOff1 = new Gene[nVars];
	  Gene[] genesOff2 = new Gene[nVars];
	  
	  while (pop.size() < this.popsize) {
		  dad = pop.get(Randomize.Randint (0, pop.size()));
		  mom = pop.get(Randomize.Randint (0, pop.size()));
		  
		  if (Randomize.Rand() < this.pc) {  
			  pos = Randomize.Randint(1, nVars-1);

			  for (i=0; i<pos; i++) {
				  gen1 = dad.getGen(i);
				  gen2 = mom.getGen(i);
				  
				  genesOff1[i] = gen1.copy();
				  genesOff2[i] = gen2.copy();
			  }

			  for (i=pos; i<nVars; i++) {
				  gen1 = dad.getGen(i);
				  gen2 = mom.getGen(i);
				  
				  genesOff1[i] = gen2.copy();
				  genesOff2[i] = gen1.copy();
			  }

			  off1 = new Chromosome (genesOff1);
			  off2 = new Chromosome (genesOff2);
			  
			  off1.setFit(this.fitness(off1));			  
			  off2.setFit(this.fitness(off2));
			  
			  if (off1.getFit() > off2.getFit())  off_best = off1;
			  else  off_best = off2;
			  
			  if (off_best.getFit() > 0)  pop.add(off_best);
		  }
	  }
  }  

  
  private void mutate(ArrayList<Chromosome> pop) {
	  int i, index, attr, nVars;
	  double max_attr, min_attr, top;
	  Chromosome chromo;
	  Gene g;

	  nVars = this.ds.getnVars();

	  for (i=0; i < this.popsize; i++) {
		  if (Randomize.Rand() < this.pm) {
			  chromo = pop.get(i);
			  index = Randomize.Randint(0, nVars);
			  
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
		  
			  chromo.setFit(this.fitness(chromo));
		  }
	  }
  }

  
  private double fitness(Chromosome c) {
	  ArrayList<Integer> tid_lst = countSupport(c.getGenes());
	  double cov = 0.0;
	  
	  for (int t=0; t < tid_lst.size(); t++)
		  cov += this.weights[ tid_lst.get(t) ];
	  
	  return ( cov / (double)ds.getnTrans() );

  }
  
  
  private ArrayList<Integer> countSupport(Gene[] genes) {
	  ArrayList<Integer> tid_list = new ArrayList<Integer>();
	  double[][] trans = this.ds.getRealTransactions();
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
	  int i, tr;
	  ArrayList<Integer> tid_lst = countSupport( c.getGenes() );
	  
	  for (i=0; i < tid_lst.size(); i++) {
		  tr = tid_lst.get(i);
		  if ( this.weights[tr] == 1.0 ) this.weights[tr] = 1.0 - this.pf;
	  }	  
  }
  
  
  private void genRules() {
	  int i, j;
	  double all_sup, ant_sup, nTrans; 
	  ArrayList<Integer> tid_lst_all, tid_lst_ant;
	  AssociationRule rule;
	  Chromosome chromo;
	  Gene[] genes_ant;
	  
	  nTrans=(double)this.ds.getnTrans();
	  this.assoc_rules = new ArrayList<AssociationRule>();
	
	  for (i=0; i < bestRules.size(); i++) {
		  chromo = bestRules.get(i);
		  rule = new AssociationRule();
		  genes_ant = new Gene[chromo.length() - 1];
		  
		  for (j=0; j < chromo.length()-1; j++) {
			  rule.addAntecedent((chromo.getGen(j)).copy());
			  genes_ant[j] = chromo.getGen(j);
		  }
		
		  rule.addConsequent((chromo.getGen(j)).copy());
			
		  tid_lst_all = this.countSupport(chromo.getGenes());
		  all_sup = (double) tid_lst_all.size() / nTrans;
		  
		  tid_lst_ant = this.countSupport(genes_ant);
		  ant_sup = (double) tid_lst_ant.size() / nTrans;
		  
		  rule.setSupport(ant_sup);
		  rule.setAll_support(all_sup);
		  rule.setConfidence(all_sup / ant_sup);		  			  
		  
		  this.assoc_rules.add(rule);
	  }
  }
  
  
  private Chromosome chooseTheBest(ArrayList<Chromosome> pop) {
	  double all_sup, ant_sup, conf, prod, max_prod, nTrans;
	  int i, j;
	  Gene[] genes, ant_genes;
	  ArrayList<Integer> tid_lst_all, tid_lst_ant;
	  Chromosome chromo, chromo_the_best;
	  
	  nTrans = (double)this.ds.getnTrans();
	  max_prod = 0.0;
	  chromo_the_best = pop.get(0);
	  
	  for (i=1; i < pop.size(); i++) {
		  chromo = pop.get(i);
		  genes = chromo.getGenes();
		  
		  tid_lst_all = this.countSupport(genes);
		  all_sup = (double)tid_lst_all.size() / nTrans;
		  
		  ant_genes = new Gene[ genes.length-1 ];
		  for (j=0; j < genes.length-1; j++) ant_genes[j] = genes[j];
		  
		  tid_lst_ant = this.countSupport(ant_genes);
		  ant_sup = (double)tid_lst_ant.size() / nTrans;
		  conf = all_sup / ant_sup;
		  
		  prod = all_sup * conf * chromo.getFit();
		  
		  if ( prod > max_prod ) {
			  max_prod = prod;
			  chromo_the_best = chromo;
		  }
	  }
	  
	  return chromo_the_best;
  }
  
  
  private boolean allRecordsCovered() {
	  for (int i=0; i < this.weights.length; i++)
		  if ( this.weights[i] == 1.0 ) return false;
	  
	  return true;
  }
  
  
  private int numCoveredRecords (double minConfidence, double minSupport) {
	  int i, j, tr, covered, nTrans;
	  ArrayList<Gene> ant;
	  ArrayList<Integer> tidCovered;
	  AssociationRule rule;
	  Gene[] genes;
	  
	  nTrans = this.ds.getnTrans();
	  
	  boolean[] marked = new boolean[nTrans];
	  for (i=0; i < marked.length; i++)  marked[i] = false;

	  
	  for (i=0; i < this.assoc_rules.size(); i++) {
		  rule = this.assoc_rules.get(i);
		  if ((rule.getConfidence() >= minConfidence) && (rule.getAll_support() >= minSupport)) {
			  ant = rule.getAntecedent();
			  genes = new Gene[ant.size()];
			  for (j=0; j < ant.size(); j++)  genes[j] = ant.get(j);
			  
			  tidCovered = countSupport(genes);
			  
			  for (j=0; j < tidCovered.size(); j++) {
				  tr = tidCovered.get(j);
				  if ( !marked[tr] ) marked[tr] = true;
			  }
		  }
	  }

	  covered = 0;
	  for (i=0; i < marked.length; i++)
		  if (marked[i])  covered++;

	  return covered;
  }
  
}
