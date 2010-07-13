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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.EARMGA;

import java.util.*;
import org.core.Randomize;
import keel.Dataset.*;

public class EARMGAProcess
{
	private myDataset ds;
	private DataB dataBase;
	ArrayList<Chromosome> pop;
	ArrayList<AssociationRule> assocRules;

	private int nGenetations;
	private int popsize;
	private double ps;
	private double pc;
	private double pm;
	private int kItemsets;
	private double alpha;
	private double avg_ampl;
	
	
  public EARMGAProcess(myDataset ds, DataB dataBase, int nGenetations, int popsize, int kItemsets, double ps, double pc, double pm, double alpha) {
	  this.ds = ds;
	  this.dataBase = dataBase;
	  this.nGenetations = nGenetations;
	  this.popsize = popsize;
	  this.kItemsets = kItemsets;
	  this.ps = ps;
	  this.pc = pc;
	  this.pm = pm;
	  this.alpha = alpha;
  }

  
  public void run() {
	  int i, nGen;
	  Chromosome chromo;
	  ArrayList<Chromosome> pop_temp;

	  System.out.println("Inicializacion");
	  this.initialize();
	  
	  nGen = 0;
	  do {
		  System.out.println("Generation: " + nGen);
		  this.select();
		  pop_temp = this.crossover();

		  for (i=pop_temp.size()-1; i>=0; i--) {
			  chromo = pop_temp.get(i);
			  if ((Randomize.Rand() * chromo.getFit()) < this.pm) {
				  this.mutate(chromo);
				  this.fitness(chromo);
			  }
			  if (chromo.getFit() <= 0.0)  pop_temp.remove(i);
		  }

		  this.elitist (pop_temp);
		  
		  nGen++;
	  }while (!terminate(nGen));

	  this.genRules();
  }

  public boolean terminate (int nGen) {
	  Chromosome best, worst;

	  Collections.sort(this.pop);
	  best = this.pop.get(0);
	  worst = this.pop.get(this.pop.size()-1);

//	  if ((best.getFit() - worst.getFit()) < this.alpha)  return (true);
	  if (nGen > this.nGenetations)  return (true);

	  return (false);
  }

  private void initialize() {
	  int i, nVars, attr, top;
	  Chromosome chromo;
	  Gene gen;

	  this.pop = new ArrayList<Chromosome>();
	  nVars = this.ds.getnVars();

	  do {
		  chromo = new Chromosome(Randomize.Randint(0, this.kItemsets-1), nVars);

		  for (i=0; i < this.kItemsets; i++) {
			  gen = new Gene();

			  attr = Randomize.Randint (0, nVars);
			  while (chromo.isUsed(attr))  attr = (attr + 1) % nVars;

			  gen.setAttr(attr);
			  gen.setType(this.ds.getType(attr));
			  gen.addValue(Randomize.Randint(0, this.dataBase.numIntervals(attr)));
			  chromo.add(gen);
		  }

		  this.fitness(chromo);
	  } while (chromo.getFit() <= 0.0);

	  this.pop.add(chromo);
//	  System.out.println("Fitness Semilla: " + chromo.getFit());

	  while (this.pop.size() <= (this.popsize/2.0)) { 
		  top = this.pop.size();
		  for (i=0; i<top; i++) {
			  chromo = (this.pop.get(i)).copy();
			  this.mutate(chromo);
			  this.fitness(chromo);
			  // System.out.println("  Fitness mutacion semilla: " + chromo.getFit());
			  if (chromo.getFit() > 0.0)  this.pop.add(chromo);
		  }
	  }
  }
  
  
  private void select() {
	  int i;

	  for (i=this.pop.size()-1; i >= 0 ; i--) {
		  if ((Randomize.Rand() * (this.pop.get(i)).getFit()) > this.ps)  this.pop.remove(i);
	  }
  }
  
  
  private ArrayList<Chromosome> crossover() {
	  int i, j, k, l, posi, posj, aux, attr, pos;
	  ArrayList<Chromosome> pop_tmp = new ArrayList<Chromosome>();
	  Chromosome dad, mom, off1, off2;

	  for (i=0; i < this.pop.size(); i++) {
		  dad = this.pop.get(i);
		  for (j=i+1; j < this.pop.size(); j++) {
			  if (Randomize.Rand() < this.pc) {
				  mom = this.pop.get(j);
				  
				  posi = Randomize.Randint(0, this.kItemsets);
				  posj = Randomize.Randint(0, this.kItemsets);
				  if (posi > posj) {
					  aux = posi;
					  posi = posj;
					  posj = aux;
				  }

				  off1 = dad.copy();
				  off2 = mom.copy();

				  if (posi==0) {
					  off1.setLengthAnt(mom.getLengthAnt());
					  off2.setLengthAnt(dad.getLengthAnt());
				  }

				  for (k=posi; k <= posj; k++) {
					  off1.offUsed((off1.getGen(k)).getAttr());
					  off2.offUsed((off2.getGen(k)).getAttr());
				  }

				  for (k=posi; k <= posj; k++) {
					  pos = k;
					  if (off1.isUsed((mom.getGen(pos)).getAttr())) {
						  pos = (posj + 1) % this.kItemsets;
						  while (off1.isUsed((mom.getGen(pos)).getAttr())) {
							  pos = (pos + 1) % this.kItemsets;
						  }
					  }
					  (off1.getGen(k)).setAttr((mom.getGen(pos)).getAttr());
					  (off1.getGen(k)).setType((mom.getGen(pos)).getType());
					  (off1.getGen(k)).setValue((mom.getGen(pos)).getValue());
					  off1.onUsed((off1.getGen(k)).getAttr());

					  pos = k;
					  if (off2.isUsed((dad.getGen(pos)).getAttr())) {
						  pos = (posj + 1) % this.kItemsets;
						  while (off2.isUsed((dad.getGen(pos)).getAttr())) {
							  pos = (pos + 1) % this.kItemsets;
						  }
					  }
					  (off2.getGen(k)).setAttr((dad.getGen(pos)).getAttr());
					  (off2.getGen(k)).setType((dad.getGen(pos)).getType());
					  (off2.getGen(k)).setValue((dad.getGen(pos)).getValue());
					  off2.onUsed((off2.getGen(k)).getAttr());
				  }

				  this.fitness(off1);
				  this.fitness(off2);
//				  System.out.println("  Fitness cruce: " + off1.getFit());
//				  System.out.println("  Fitness cruce: " + off2.getFit());
				  pop_tmp.add(off1);
				  pop_tmp.add(off2);
			  }
		  }
	  }

	  return (pop_tmp);
  }  

  
  private void mutate(Chromosome chromo) {
	  int i, attr, attr_ant;
	  double prop;
	  Gene gen;

	  chromo.setLengthAnt(Randomize.Randint(0, this.kItemsets-1));
	  gen = chromo.getGen(Randomize.Randint(0, this.kItemsets));
	  attr_ant = gen.getAttr();
	  prop = gen.numIntervals() / (this.dataBase.numIntervals(attr_ant) * 1.0);

	  attr = Randomize.Randint(0, this.ds.getnVars());
	  for (i=0; chromo.isUsed(attr) && i < this.ds.getnVars(); i++)  attr = (attr + 1) % this.ds.getnVars();

	  if (chromo.isUsed(attr))  attr = attr_ant;
	  else {
		  chromo.offUsed(attr_ant);
		  chromo.onUsed(attr);
		  gen.setAttr(attr);
		  gen.setType(this.ds.getType(attr));
	  }

	  gen.clearValue();
	  for (i=0; i < this.dataBase.numIntervals(attr); i++) {
		  if (Randomize.Rand() <= prop)  gen.addValue(i);
	  }
	  
//	  if (gen.numIntervals() == this.dataBase.numIntervals(attr))  gen.removeValue(Randomize.Randint(0, gen.numIntervals()));		  
	  if (gen.numIntervals() == 0)  gen.addValue(Randomize.Randint(0, this.dataBase.numIntervals(attr)));
	  // System.out.println("  Mutracion, numero intervalos: " + gen.numIntervals());
  }

  
  private void fitness(Chromosome chromo) {
	  int i;
	  double nTrans = (double) this.ds.getnTrans();
	  double fit;
/*	  boolean trivial;


	  trivial = true;

	  for (i=chromo.getLengthAnt()+1;  i < this.kItemsets && trivial; i++) {
		  Gene gen = chromo.getGen(i);
		  if (gen.numIntervals() < this.dataBase.numIntervals(gen.getAttr())) {
			  trivial = false;
		  }
	  }

	  if (trivial) {
		  chromo.setFit(0.0);
		  chromo.setSupportAnt(0.0);
		  chromo.setSupportCon(0.0);
		  chromo.setSupportAll(0.0);
	  }
	  else { */
	  ArrayList<Integer> ant = this.countSupport(chromo, 0, chromo.getLengthAnt());
	  if (ant.size() == 0) {
		  chromo.setFit(0.0);
		  chromo.setSupportAnt(0.0);
		  chromo.setSupportCon(0.0);
		  chromo.setSupportAll(0.0);
	  }
	  else {
	  	  ArrayList<Integer> con = this.countSupport(chromo, chromo.getLengthAnt()+1, this.kItemsets-1);
		  if ((con.size() / nTrans) >= 1.0) {
			  chromo.setFit(1.0);
			  chromo.setSupportAnt(ant.size() / nTrans);
			  chromo.setSupportCon(1.0);
			  chromo.setSupportAll(chromo.getSupportAnt());
		  }
		  else {
			  ArrayList<Integer> all = this.countSupport(chromo, 0, this.kItemsets-1);
			  if (all.size()==0)  {
				  chromo.setFit(0.0);
				  chromo.setSupportAnt(ant.size() / nTrans);
				  chromo.setSupportCon(con.size() / nTrans);
				  chromo.setSupportAll(0.0);
			  }
			  else {
				  chromo.setSupportAnt(ant.size() / nTrans);
				  chromo.setSupportCon(con.size() / nTrans);
				  chromo.setSupportAll(all.size() / nTrans);
				  fit = (chromo.getSupportAll() - (chromo.getSupportAnt() * chromo.getSupportCon())) / (chromo.getSupportAnt() * (1.0 - chromo.getSupportCon()));
				  if (fit > 1.0)  fit = 1.0;
				  chromo.setFit(fit);
			  }
		  }
	  }
//	  }
  }

/*

  private void elitist (ArrayList<Chromosome> pop_temp) {
	  int i, j;
	  boolean stop;
	  Chromosome chromo1, chromo2;

	  this.pop.addAll(pop_temp);

	  Collections.sort(this.pop);

	  for (i=0; i<this.pop.size(); i++) {
		  chromo1 = this.pop.get(i);
		  stop = false;

		  for (j=i+1; j<this.pop.size() && !stop; j++) {
			chromo2 = this.pop.get(j);
			if ((chromo1.getFit() >= chromo2.getFit()-0.00001) && (chromo1.getFit() <= chromo2.getFit()+0.00001)) {
				if (chromo1.isEqual(chromo2)) {
					this.pop.remove(j);
					j--;
				}
			}
			else  stop = true;
		  }
	  }


	  while (this.pop.size() > this.popsize)  this.pop.remove(this.pop.size()-1);
	  System.gc();
  }

*/  

  private void elitist (ArrayList<Chromosome> pop_temp) {
	  int i, j;
	  Chromosome chromo1, chromo2;

	  for (i=0; i<pop_temp.size(); i++) {
		  chromo1 = pop_temp.get(i);
		  
		  for (j=0; j<this.pop.size(); j++) {
			  chromo2 = this.pop.get(j);
			  if (chromo1.isSub(chromo2)) {
				  if (chromo1.getFit() > chromo2.getFit()) {
					  this.pop.remove(j);
					  j--;
				  }
				  else {
					  pop_temp.remove(i);
					  j = this.pop.size();
					  i--;
				  }
			  }
			  else if (chromo2.isSub(chromo1)) {
				  if (chromo2.getFit() >= chromo1.getFit()) {
					  pop_temp.remove(i);
					  j = this.pop.size();
					  i--;
				  }
				  else {
					  this.pop.remove(j);
					  j--;
				  }
			  }
		  }
	  }

	  this.pop.addAll(pop_temp);
	  Collections.sort(this.pop);
	  while (this.pop.size() > this.popsize)  this.pop.remove(this.pop.size()-1);
	  System.gc();
  }

  
  private ArrayList<Integer> countSupport(Chromosome chromo, int ini, int fin) {
	  ArrayList<Integer> tid_list = new ArrayList<Integer>();
	  ArrayList<Integer> value;
	  double[][] trans = this.ds.getRealTransactions();
	  int i, j, t, attr, nTrans;
	  boolean ok;
	  
	  nTrans = this.ds.getnTrans();

	  for (t=0; t < nTrans; t++) {
		  ok = true;
		  
		  for (i=ini; i <= fin && ok; i++) {
			  attr = (chromo.getGen(i)).getAttr();
			  value = (chromo.getGen(i)).getValue();

			  ok = false;

			  for (j=0; j < value.size() && !ok; j++) {
				  if (this.dataBase.isCovered(attr, value.get(j).intValue(), trans[t][attr]))   ok = true;
			  }		  
		  }
		  
		  if (ok) tid_list.add(t);
	  }
	  
	  return tid_list;
  }
  
  public void printReport (double minConfidence, double minSupport) {
	  int i, countRules, length;
	  AssociationRule rule;

	  countRules = length = 0;
	  for (i=0; i < this.assocRules.size(); i++) {
		  rule = this.assocRules.get(i); 
		  if ((rule.getConfidence() >= minConfidence) && (rule.getAll_support() >= minSupport)) {
			countRules++;
			length += rule.getLengthAntecedent();
		  }
	  }

	  System.out.println("Number of Association Rules generated: " + countRules);
	  System.out.println("Length of the Itemsets: " + this.kItemsets);
	  System.out.println("Average Length of the Rules generated: " + length / (double) countRules);
	  System.out.println("Number of Covered Records(%): " + (100.0 * this.numCoveredRecords (minConfidence, minSupport)) / this.ds.getnTrans());
  }  

  public ArrayList<AssociationRule> getSetRules (double minConfidence, double minSupport) {
	  int i;
	  ArrayList<AssociationRule> selectRules = new ArrayList<AssociationRule>();
	  AssociationRule rule;
	  
	  for (i=0; i < this.assocRules.size(); i++) {
		  rule = this.assocRules.get(i);
		  if ((rule.getConfidence() >= minConfidence) && (rule.getAll_support() >= minSupport))  selectRules.add(rule.copy());
	  }

	  return selectRules;
  }  
   
  
  private void genRules() {
	  int i, j, g1, g2;
	  double all_sup, ant_sup; 
	  ArrayList<Integer> tid_lst_all, tid_lst_ant;
	  AssociationRule rule;
	  Chromosome chromo;
	  Gene[] genes_ant;

	  this.assocRules = new ArrayList<AssociationRule>();
	
	  for (i=0; i < this.pop.size(); i++) {
		  chromo = this.pop.get(i);
		  rule = new AssociationRule();
		  
		  for (j=0; j <= chromo.getLengthAnt(); j++)  rule.addAntecedent((chromo.getGen(j)).copy());
		  for (j=chromo.getLengthAnt()+1; j < this.kItemsets; j++)  rule.addConsequent((chromo.getGen(j)).copy());
					  
		  rule.setSupport (chromo.getSupportAnt());
		  rule.setAll_support (chromo.getSupportAll());
		  rule.setConfidence (chromo.getSupportAll() / chromo.getSupportAnt());
					  			  
		  this.assocRules.add(rule);
	  }
  }
  
  
  private int numCoveredRecords (double minConfidence, double minSupport) {
	  int i, j, covered, nTrans;
	  ArrayList<Integer> tidCovered;
	  Chromosome chromo;


	  nTrans = this.ds.getnTrans();
	  boolean [] marked = new boolean[nTrans];
	  for (i=0; i < nTrans; i++)  marked[i] = false;


	  // System.out.println("Tamano: " + this.pop.size());
	  for (i=0; i < this.pop.size(); i++) {
		  chromo = this.pop.get(i);

		  if (((chromo.getSupportAll() / chromo.getSupportAnt()) >= minConfidence) && (chromo.getSupportAll() >= minSupport)) {
			  tidCovered = this.countSupport(chromo, 0, this.kItemsets-1);
			  for (j=0; j < tidCovered.size(); j++)  marked[tidCovered.get(j)] = true;
		  }
	  }

	  covered = 0;
	  for (i=0; i < nTrans; i++)
		  if (marked[i])  covered++;

	  // System.out.println("Cubiertos: " + covered);

	  return covered;
  }
}

