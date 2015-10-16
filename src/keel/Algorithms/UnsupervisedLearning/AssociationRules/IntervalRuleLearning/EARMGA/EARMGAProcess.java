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

/**
 * <p>
 * @author Written by Alberto Fernández (University of Granada)
 * @author Modified by Diana Martín (dmartin@ceis.cujae.edu.cu) 
 * @version 1.1
 * @since JDK1.6
 * </p>
 */

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;

import org.core.Randomize;


public class EARMGAProcess
{
	private myDataset ds;
	private DataB dataBase;
	ArrayList<Chromosome> pop;
	ArrayList<AssociationRule> assocRules;

	private int nTrials, trials;
	private int popsize;
	private double ps;
	private double pc;
	private double pm;
	private int kItemsets;
	private double alpha;
	
  public EARMGAProcess(myDataset ds, DataB dataBase, int nTrials, int popsize, int kItemsets, double ps, double pc, double pm, double alpha) {
	  this.ds = ds;
	  this.dataBase = dataBase;
	  this.nTrials = nTrials;
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
	  this.trials = 0;
	  nGen = 0;

	  System.out.println("Initialization");
	  this.initialize();
	  
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
	  }while (!terminate());

	  this.genRules();
  }

  public boolean terminate () {
	  Chromosome best, worst;

	  Collections.sort(this.pop);
	  best = this.pop.get(0);
	  worst = this.pop.get(this.pop.size()-1);

//	  if ((best.getFit() - worst.getFit()) < this.alpha)  return (true);
	  if (this.trials > this.nTrials)  return (true);

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
	  int i, j, k, posi, posj, aux, pos;
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
	  double nTrans = (double) this.ds.getnTrans();
	  double fit;

	  this.trials++;
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
  
  public static double roundDouble(double number, int decimalPlace){
	  double numberRound;
	  
	  if(!Double.isInfinite(number)&&(!Double.isNaN(number))){
		  BigDecimal bd = new BigDecimal(number);
		  bd = bd.setScale(decimalPlace, BigDecimal.ROUND_UP);
		  numberRound = bd.doubleValue();
		  return numberRound;
	  }else return number;
	 
	  
  }
  
  public void printReport (double minConfidence, double minSupport) {
	  
	  double avg_yulesQ=0.0, avg_sup=0.0, avg_conf=0.0,avg_lift=0.0, avg_conv = 0.0, avg_CF = 0.0, avg_netConf = 0.0;
	  int i, countRules, length;
	  AssociationRule rule;
	  
	  countRules = length = 0;
	  
	  for (i=0; i < this.assocRules.size(); i++) {
		  rule = this.assocRules.get(i); 
		  if ((rule.getConfidence() >= minConfidence) && (rule.getAll_support() >= minSupport)) {
			countRules++;
			length += rule.getLengthRule();
			avg_sup += rule.getAll_support();
			avg_conf += rule.getConfidence();
			avg_lift += rule.getLift();
			avg_conv += rule.getConv();
			avg_CF += rule.getCF();
			avg_netConf += rule.getNetConf();
			avg_yulesQ += rule.getYulesQ();
			
		  }
	  }
	  
	  System.out.println("Number of Frequent Itemsets generated: " + "-");
	  System.out.println("Number of Association Rules generated: " + countRules);
	 
	  if(countRules!=0){
		  System.out.println("Average SupportRules: " + roundDouble(( avg_sup / countRules ), 2) );
		  System.out.println("Average Confidence: " + roundDouble(( avg_conf / countRules ), 2) );
		  System.out.println("Average Lift: " + roundDouble(( avg_lift / countRules ), 2) );
    	  System.out.println("Average Conviction: " + roundDouble(( avg_conv/ countRules ), 2));
		  System.out.println("Average Certain Factor: " + roundDouble(( avg_CF/ countRules ), 2));
		  System.out.println("Average Netconf: " + roundDouble(( avg_netConf/ countRules), 2));
		  System.out.println("Average YulesQ: " + roundDouble(( avg_yulesQ/ countRules), 2));
		  System.out.println("Average Length of the Rules generated: " + roundDouble((length / (double) countRules), 2));
		  System.out.println("Number of Covered Records(%): " + (100.0 * this.numCoveredRecords (minSupport)) / this.ds.getnTrans());
	  }
	  else{
		  System.out.println("Average Support: " + (0.0));
		  System.out.println("Average Confidence: " + (0.0 ));
		  System.out.println("Average Lift: " + ( 0.0 ));
		  System.out.println("Average Conviction: " + ( 0.0 ));
		  System.out.println("Average Certain Factor: " + ( 0.0 ));
		  System.out.println("Average Netconf: " + (0.0));
		  System.out.println("Average Antecedents Length: " + ( 0.0 ));
		  System.out.println("Number of Covered Records (%): " + (0.0) );
	  } 
	}   
  
  public String printRules(ArrayList<AssociationRule> rules) {
	  int i, lenghtrule;
	  boolean stop;
	  String rulesList;

	  stop = false;
	  rulesList = "";
	  rulesList += ("Support\tantecedent_support\tconsequent_support\tConfidence\tLift\tConv\tCF\tNetConf\tYulesQ\tnAttributes\n");
	  for (i=0; i < rules.size() && !stop; i++) {
		  lenghtrule = rules.get(i).getAntecedent().size()+ rules.get(i).getConsequent().size();
		  rulesList += ("" + roundDouble(rules.get(i).getAll_support(),2) + "\t" + roundDouble(rules.get(i).getSupport_Ant(),2) + "\t" + roundDouble(rules.get(i).getSupport_cons(),2) + "\t" + roundDouble(rules.get(i).getConfidence(),2) + "\t" + roundDouble(rules.get(i).getLift(),2) + "\t" + roundDouble(rules.get(i).getConv(),2) + "\t" + roundDouble(rules.get(i).getCF(),2) + "\t" + roundDouble(rules.get(i).getNetConf(),2) + "\t" + roundDouble(rules.get(i).getYulesQ(),2) + "\t" + lenghtrule + "\n");
	  }
	  return rulesList;
  }
  
  public void saveReport (double minSupport,PrintWriter w) {
	  int i, countRules, length;
	  AssociationRule rule;
	  double avg_yulesQ=0.0, avg_sup=0.0, avg_conf=0.0,avg_lift=0.0, avg_conv = 0.0, avg_CF = 0.0, avg_netConf = 0.0;

	  countRules = length = 0;
  
	  for (i=0; i < this.assocRules.size(); i++) {
		  rule = this.assocRules.get(i); 
		  if (rule.getAll_support() >= minSupport) {
			countRules++;
			length += rule.getLengthRule();
			avg_sup += rule.getAll_support();
			avg_conf += rule.getConfidence();
			avg_lift += rule.getLift();
			avg_conv += rule.getConv();
			avg_CF += rule.getCF();
			avg_netConf += rule.getNetConf();
			avg_yulesQ += rule.getYulesQ();
		  }
	  }

	  w.println("\nNumber of Frequent Itemsets generated: " + "-");	
	  System.out.println("Number of Frequent Itemsets generated: " + "-");
	  w.println("\nNumber of Association Rules generated: " + countRules);	
	  System.out.println("Number of Association Rules generated: " + countRules);
	 
	  if(countRules!=0){
		  w.println("Average Support: " + roundDouble(( avg_sup / countRules ), 2));
		  System.out.println("Average SupportRules: " + roundDouble(( avg_sup / countRules ), 2) );
		  w.println("Average Confidence: " + roundDouble(( avg_conf / countRules ), 2));
		  System.out.println("Average Confidence: " + roundDouble(( avg_conf / countRules ), 2) );
		  w.println("Average Lift: " + roundDouble(( avg_lift / countRules ), 2));
		  System.out.println("Average Lift: " + roundDouble(( avg_lift / countRules ), 2) );
		  w.println("Average Conviction: " + roundDouble(( avg_conv/ countRules ), 2));
		  System.out.println("Average Conviction: " + roundDouble(( avg_conv/ countRules ), 2));
		  w.println("Average Certain Factor: " + roundDouble(( avg_CF/ countRules ), 2));
		  System.out.println("Average Certain Factor: " + roundDouble(( avg_CF/ countRules ), 2));
		  w.println("Average Netconf: " + roundDouble(( avg_netConf/ countRules), 2));
		  System.out.println("Average Netconf: " + roundDouble(( avg_netConf/ countRules), 2));
		  w.println("Average YulesQ: " + roundDouble(( avg_yulesQ/ countRules), 2));
		  System.out.println("Average YulesQ: " + roundDouble(( avg_yulesQ/ countRules), 2));
		   w.println("Average Antecedents Length: " + roundDouble((length / (double) countRules), 2));
		  System.out.println("Average Length of the Rules generated: " + roundDouble((length / (double) countRules), 2));
		  w.println("Number of Covered Records (%): " + roundDouble((100.0 * this.numCoveredRecords (minSupport)) / this.ds.getnTrans(),2));
		  System.out.println("Number of Covered Records(%): " + (100.0 * this.numCoveredRecords (minSupport)) / this.ds.getnTrans());
	  }
	  else{
		  w.println("Average Support: " + ( 0.0 ));
		  System.out.println("Average Support: " + (0.0));
		  w.println("Average Confidence: " + ( 0.0 ));
		  System.out.println("Average Confidence: " + (0.0 ));
		  w.println("Average Lift: " + (0.0 ));
		  System.out.println("Average Lift: " + ( 0.0 ));
		  w.println("Average Conviction: " + ( 0.0  ));
		  System.out.println("Average Conviction: " + ( 0.0 ));
		  w.println("Average Certain Factor: " + ( 0.0  ));
		  System.out.println("Average Certain Factor: " + ( 0.0 ));
		  w.println("Average Netconf: " + ( 0.0 ));
		  System.out.println("Average Netconf: " + (0.0));
		  w.println("Average Antecedents Length: " + ( 0.0  ));
		  System.out.println("Average Antecedents Length: " + ( 0.0 ));
		  w.println("Number of Covered Records (%): " +  (0.0));
		  System.out.println("Number of Covered Records (%): " + (0.0) );
	  }
	  
  }  

  public ArrayList<AssociationRule> getSetRules (double minSupport) {
	  int i;
	  ArrayList<AssociationRule> selectRules = new ArrayList<AssociationRule>();
	  AssociationRule rule;
	  
	  for (i=0; i < this.assocRules.size(); i++) {
		  rule = this.assocRules.get(i);
		  if (rule.getAll_support() >= minSupport)  selectRules.add(rule.copy());
	  }
	  
	 return selectRules;
  }  
   
  
  private void genRules() {
	  int i, j;
	  double numeratorYules, denominatorYules, confidance,lift,conv, CF, netConf, yulesQ; 
	  AssociationRule rule;
	  Chromosome chromo;

	  this.assocRules = new ArrayList<AssociationRule>();
	
	  for (i=0; i < this.pop.size(); i++) {
		  chromo = this.pop.get(i);
		  rule = new AssociationRule();
		  
		  for (j=0; j <= chromo.getLengthAnt(); j++)  rule.addAntecedent((chromo.getGen(j)).copy());
		  for (j=chromo.getLengthAnt()+1; j < this.kItemsets; j++)  rule.addConsequent((chromo.getGen(j)).copy());
					  
		  confidance = chromo.getSupportAll() / chromo.getSupportAnt();
		 
		  if((chromo.getSupportAnt() == 0)||(chromo.getSupportCon() == 0))
			  lift = 1;
		  else lift = chromo.getSupportAll() /(chromo.getSupportAnt()* chromo.getSupportCon());
		  
		  if((chromo.getSupportCon()==1)||(chromo.getSupportAnt() == 0))
			  conv = 1;
		  else conv = (chromo.getSupportAnt()*(1-chromo.getSupportCon()))/(chromo.getSupportAnt()- chromo.getSupportAll());
		  
		  if ((chromo.getSupportAnt() == 0)||(chromo.getSupportAnt() == 1)||(Math.abs(chromo.getSupportAnt()*(1-chromo.getSupportAnt())) <= 0.001))
			  netConf = 0;
		  else netConf = (chromo.getSupportAll()-(chromo.getSupportAnt()* chromo.getSupportCon()))/(chromo.getSupportAnt()*(1-chromo.getSupportAnt()));
		  
		  //compute yulesQ
		  numeratorYules = ((chromo.getSupportAll() * (1 - chromo.getSupportCon() - chromo.getSupportAnt() + chromo.getSupportAll())) - ((chromo.getSupportAnt() - chromo.getSupportAll())* (chromo.getSupportCon() - chromo.getSupportAll())));
		  denominatorYules = ((chromo.getSupportAll() * (1 - chromo.getSupportCon() - chromo.getSupportAnt() + chromo.getSupportAll())) + ((chromo.getSupportAnt() - chromo.getSupportAll())* (chromo.getSupportCon() - chromo.getSupportAll())));
			
		  if((chromo.getSupportAnt() == 0)||(chromo.getSupportAnt() == 1)||(chromo.getSupportCon() == 0)||(chromo.getSupportCon() == 1)||(Math.abs(denominatorYules) <= 0.001))
			  yulesQ = 0;
		  else  yulesQ = numeratorYules/denominatorYules;
		  
		  CF = 0;
	      if(confidance > chromo.getSupportCon())
	    	  CF = (confidance - chromo.getSupportCon())/(1-chromo.getSupportCon());
	      else 
			 if(confidance < chromo.getSupportCon())
				CF = (confidance - chromo.getSupportCon())/(chromo.getSupportCon());
		  
		  rule.setSupport_Ant (chromo.getSupportAnt());
		  rule.setSupport_cons(chromo.getSupportCon());
		  rule.setAll_support (chromo.getSupportAll());
		  rule.setConfidence (confidance);
		  rule.setLift(lift);
		  rule.setConv(conv);
		  rule.setCF(CF);
		  rule.setNetConf(netConf);
		  rule.setYulesQ(yulesQ);
		  
					  			  
		  this.assocRules.add(rule);
	  }
	  
	 
  }
  
  
  private int numCoveredRecords (double minSupport) {
	  int i, j, covered, nTrans;
	  ArrayList<Integer> tidCovered;
	  Chromosome chromo;


	  nTrans = this.ds.getnTrans();
	  boolean [] marked = new boolean[nTrans];
	  for (i=0; i < nTrans; i++)  marked[i] = false;


	  // System.out.println("Tamano: " + this.pop.size());
	  for (i=0; i < this.pop.size(); i++) {
		  chromo = this.pop.get(i);

		  if ((chromo.getSupportAll() >= minSupport)) {
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

