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



import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;

import org.core.Randomize;

/**
 * <p> It provides the implementation of the GENAR algorithm to be run in a process
   * 
 * @author Alberto Fernández
 * @author Modified by Diana Martín (dmartin@ceis.cujae.edu.cu)
 * @version 1.1
 * @since JDK1.6
 * </p>
 */
public class GENARProcess
{
	private myDataset ds;
	private double[] weights;
	private double allow_ampl[];
	ArrayList<Chromosome> bestRules;
	ArrayList<AssociationRule> assoc_rules;

	private int nRules, trials;
	private int nTrials;
	private int popsize;
	private double pm;
	private double pf;
	private int limit;
	
/**
   * <p>
   * It creates a new process for the algorithm by setting up its parameters
   * </p>
   * @param ds The instance of the dataset for dealing with its records
   * @param nRules The maximum number of rules to be generated.
   * @param nTrials The maximum number of evaluations to accomplish before terminating the genetic learning
   * @param popsize The maximum size of population to handle after each generation
   * @param ps The probability of the selection operator
   * @param pm The probability of the mutation operator
   * @param pf The probability of the crossover operator
   * @param AF The parameter which is used while executing the crossover operator
   */
    public GENARProcess(myDataset ds, int nRules, int nTrials, int popsize, double ps, double pm, double pf, double AF) {
	  int i;

	  this.nRules = nRules;
	  this.nTrials = (nTrials / nRules) + 1;
	  this.popsize = popsize;
	  this.pm = pm;
	  this.pf = pf;

	  this.limit = (int) Math.ceil(popsize * ps);
	  this.ds = ds;
	  this.weights = new double[this.ds.getnTrans()];
	  
	  this.allow_ampl = new double[this.ds.getnVars()];
	  for (i=0; i < this.allow_ampl.length; i++) {
		  if (!this.ds.isReal(i))  this.allow_ampl[i] = (int) ((this.ds.getMax(i) - this.ds.getMin(i)) / AF);
		  else  this.allow_ampl[i] = (this.ds.getMax(i) - this.ds.getMin(i)) / AF;
	  }
  }

    /**
     *  It runs the algorithm for mining association rules.
     */ 
  public void run() {
	  ArrayList<Chromosome> popNew;
	  Chromosome chromoBest;
	  this.bestRules = new ArrayList<Chromosome>();
	  this.trials = 0;

  	  for (int i=0; i < this.weights.length; i++) this.weights[i] = 1.0;

	  do {
		  System.out.println("Number of Rules Selected: " + this.bestRules.size());

		  int nGn = 0;	  
		  this.trials = 0;
		  ArrayList<Chromosome> popCurrent = this.initialize();
		  
		  do {
			  System.out.println("Generation: " + nGn);
			  popNew = this.select(popCurrent);
			  this.crossover(popNew);
			  this.mutate(popNew);

			  popCurrent.clear();
			  popCurrent = popNew;
			  
			  nGn++;
		  }	while (this.trials < this.nTrials);

		  chromoBest = this.chooseTheBest(popCurrent);
		  this.penalizeRecordsCoveredBy(chromoBest);
		  this.bestRules.add(chromoBest.copy());
	  } while ( ( this.bestRules.size() < this.nRules ) && ( ! this.allRecordsCovered() ) );

	  
	  this.genRules();
  }

      /**
   * <p>
   * It prints out on screen relevant information regarding the mined association rules
   * which have their confidence and support values higher than the minimum ones given.
   * </p>
     * @param minConfidence given minimum confidence value.
     * @param minSupport given minimum support value.
   */
  public void printReport (double minConfidence, double minSupport) {
	  int i, countRules, length;
	  AssociationRule rule;
	  double avg_yulesQ=0.0, avg_sup=0.0, avg_conf=0.0,avg_lift=0.0, avg_conv = 0.0, avg_CF = 0.0, avg_netConf = 0.0;

	  countRules = length = 0;
	  
	  for (i=0; i < this.assoc_rules.size(); i++) {
		  rule = this.assoc_rules.get(i); 
		  if ((rule.getConfidence() >= minConfidence) && (rule.getAll_support() >= minSupport)) {
			countRules++;
			length += rule.getLength();
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
  
      /**
     * Rounds the number applying the {@link BigDecimal} rounding mode given.
     * @param number number to be rounded.
     * @param decimalPlace given rounding mode.
     * @return the rounded number.
     */
  public static double roundDouble(double number, int decimalPlace){
	  double numberRound;
	  
	  if(!Double.isInfinite(number)&&(!Double.isNaN(number))){
		  BigDecimal bd = new BigDecimal(number);
		  bd = bd.setScale(decimalPlace, BigDecimal.ROUND_UP);
		  numberRound = bd.doubleValue();
		  return numberRound;
	  }else return number;
  }
  
   /**
   * <p>
   * Returns a String with relevant information regarding the mined association rules
   * </p>
   * @param rules The array of association rules from which gathering relevant information
     * @return String with relevant information regarding the mined association rules
   * 
   */
  public String printRules(ArrayList<AssociationRule> rules) {
	  int i, lenghtrule;
	  boolean stop;
	  String rulesList;

	  stop = false;
	  rulesList = "";
	  rulesList += ("Support\tantecedent_support\tconsequent_support\tConfidence\tLift\tConv\tCF\tNetConf\tYulesQ\tnAttributes\n");
	  for (i=0; i < rules.size() && !stop; i++) {
		  lenghtrule = rules.get(i).getAntecedent().size()+ rules.get(i).getConsequent().size();
		  rulesList += ("" + roundDouble(rules.get(i).getAll_support(),2) + "\t" + roundDouble(rules.get(i).getSupport(),2) + "\t" + roundDouble(rules.get(i).getSupport_cons(),2) + "\t" + roundDouble(rules.get(i).getConfidence(),2) + "\t" + roundDouble(rules.get(i).getLift(),2) + "\t" + roundDouble(rules.get(i).getConv(),2) + "\t" + roundDouble(rules.get(i).getCF(),2) + "\t" + roundDouble(rules.get(i).getNetConf(),2) + "\t" + roundDouble(rules.get(i).getYulesQ(),2) + "\t" + lenghtrule + "\n");
	  }
	  return rulesList;
  }
  
  /**
   * <p>
   * It prints out on the given {@link PrintWriter} object relevant information regarding the mined association rules.
   * These rules must have their confidence higher than the minimum given.
   * </p>
   * 
     * @param minSupport minimum support value given.
     * @param w given PrintWriter object to write on.
   */
  public void saveReport (double minSupport,PrintWriter w) {
	  int i, countRules, length;
	  AssociationRule rule;
	  double avg_yulesQ=0.0, avg_sup=0.0, avg_conf=0.0,avg_lift=0.0, avg_conv = 0.0, avg_CF = 0.0, avg_netConf = 0.0;

	  countRules = length = 0;

	  for (i=0; i < this.assoc_rules.size(); i++) {
		  rule = this.assoc_rules.get(i); 
		  if (rule.getAll_support() >= minSupport) {
			countRules++;
			length += rule.getLength();
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
		  w.println("Number of Covered Records (%): " + roundDouble(( (100.0 * this.numCoveredRecords (minSupport)) / this.ds.getnTrans()),2));
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

    /**
     * Returns the rules that have their support values higher than the minimum given.
     * @param minSupport minimum support value given.
     * @return the rules that have their confidence and support values higher than the minimum ones given.
     *
     */
  public ArrayList<AssociationRule> getSetRules (double minSupport) {
	  int i;
	  ArrayList<AssociationRule> selectRules = new ArrayList<AssociationRule>();
	  AssociationRule rule;
	  
	  for (i=0; i < this.assoc_rules.size(); i++) {
		  rule = this.assoc_rules.get(i);
		  if (rule.getAll_support() >= minSupport)  selectRules.add(rule.copy());
	  }

	  return selectRules;
  }  
  
  private ArrayList<Chromosome> initialize() {
	  ArrayList<Chromosome> popInit = new ArrayList<Chromosome>();
	  int nVars, attr, tr;
	  double lb, ub, max_attr, min_attr;
	
	  nVars = this.ds.getnVars();

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

			  if ( !this.ds.isNominal(attr) ) {
				  if ( this.ds.isReal(attr) ) {
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
  
  
  private ArrayList<Chromosome> select (ArrayList<Chromosome> pop) {
	  ArrayList<Chromosome> popTmp = new ArrayList<Chromosome>();

	  Collections.sort(pop);
	  
	  for (int i = 0; i <= this.limit && i < pop.size(); i++)  popTmp.add((pop.get(i)).copy());
	  
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
			  
			  if (!this.ds.isNominal(attr)) {
				  if (this.ds.isReal(attr)) {
					  if (Randomize.Rand() < 0.5) {
						  top = Math.max(g.getU() - this.allow_ampl[attr], min_attr);
						  g.setL(Randomize.RanddoubleClosed(top, g.getL()));
					  }
					  else  g.setL(Randomize.Randdouble(g.getL(), g.getU()));
					  if (Randomize.Rand() < 0.5) {
						  top = Math.min(g.getL() + this.allow_ampl[attr], max_attr);
						  g.setU(Randomize.RanddoubleClosed(g.getU(), top));
					  }
					  else  g.setU(Randomize.RanddoubleClosed(g.getL()+0.0001, g.getU()));
				  }
				  else {
					  if (Randomize.Rand() < 0.5) {
						  top = Math.max(g.getU() - this.allow_ampl[attr], min_attr);
						  g.setL(Randomize.RandintClosed((int) top, (int) g.getL()));
					  }
					  else  g.setL(Randomize.Randint((int) g.getL(), (int) g.getU()));
					  if (Randomize.Rand() < 0.5) {
						  top = Math.min(g.getL() + this.allow_ampl[attr], max_attr);
						  g.setU(Randomize.RandintClosed((int) g.getU(), (int) top));
					  }
					  else  g.setU(Randomize.RandintClosed((int) g.getL() + 1, (int) g.getU()));
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

	  this.trials++;
	  return (cov / (double) this.ds.getnTrans());
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
	  double yulesQ, numeratorYules, denominatorYules, all_sup, ant_sup, cons_sup, nTrans, conf, lift, conv, CF, netConf;  
	  ArrayList<Integer> tid_lst_all, tid_lst_ant, tid_lst_con;
	  AssociationRule rule;
	  Chromosome chromo;
	  Gene[] genes_ant;
	  Gene[] genes_con;
	  
	  nTrans = (double)this.ds.getnTrans();
	  this.assoc_rules = new ArrayList<AssociationRule>();
	
	  for (i=0; i < bestRules.size(); i++) {
		  chromo = bestRules.get(i);
		  rule = new AssociationRule();
		  genes_ant = new Gene[chromo.length() - 1];
		  genes_con = new Gene[1];
		  
		  for (j=0; j < chromo.length()-1; j++) {
			  rule.addAntecedent(chromo.getGen(j).copy());
			  genes_ant[j] = chromo.getGen(j);
		  }
		
		  rule.addConsequent((chromo.getGen(j)).copy());
		  genes_con[0] = chromo.getGen(j);
			
		  tid_lst_all = this.countSupport(chromo.getGenes());
		  all_sup = tid_lst_all.size() / (double) nTrans;
		  
		  tid_lst_ant = this.countSupport(genes_ant);
		  ant_sup = tid_lst_ant.size() / (double) nTrans;

  		  tid_lst_con = this.countSupport(genes_con);
		  cons_sup = tid_lst_con.size() / (double) nTrans;

		  conf = all_sup / ant_sup;
		  
		  //compute lift
		  if((cons_sup == 0) || (ant_sup == 0))
		     lift = 1;
		  else lift = all_sup / (ant_sup*cons_sup);
		
		  //compute conviction
		  if((cons_sup == 1)||(ant_sup == 0))
			 conv = 1;
		  else conv = (ant_sup*(1-cons_sup))/(ant_sup-all_sup);
				
		  //compute netconf
		  if((ant_sup == 0)||(ant_sup == 1)||(Math.abs((ant_sup * (1-ant_sup))) <= 0.001))
			 netConf = 0;
		  else netConf = (all_sup - (ant_sup*cons_sup))/(ant_sup * (1-ant_sup));
					  
		   //compute yulesQ
		  numeratorYules = ((all_sup * (1 - cons_sup - ant_sup + all_sup)) - ((ant_sup - all_sup)* (cons_sup - all_sup)));
		  denominatorYules = ((all_sup * (1 - cons_sup - ant_sup + all_sup)) + ((ant_sup - all_sup)* (cons_sup - all_sup)));
				
		  if((ant_sup == 0)||(ant_sup == 1)|| (cons_sup == 0)||(cons_sup == 1)||(Math.abs(denominatorYules) <= 0.001))
			 yulesQ = 0;
		  else yulesQ = numeratorYules/denominatorYules;
		  
		  //compute Certain Factor(CF)
		  CF = 0;
		  if(conf > cons_sup)
			CF = (conf - cons_sup)/(1-cons_sup);	
		  else 
		  if(conf < cons_sup)
			CF = (conf - cons_sup)/(cons_sup);	
		  
		  rule.setSupport(ant_sup);
		  rule.setSupport_cons(cons_sup);
		  rule.setAll_support(all_sup);
		  rule.setConfidence(conf);		  			  
		  rule.setLift(lift);	
		  rule.setConv(conv);
		  rule.setCF(CF);
		  rule.setNetConf(netConf);
		  rule.setYulesQ(yulesQ);
		  
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
	  ant_genes = new Gene[this.ds.getnVars()-1];
	  
	  for (i=1; i < pop.size(); i++) {
		  chromo = pop.get(i);
		  
		  genes = chromo.getGenes();
		  tid_lst_all = this.countSupport(genes);
		  
		  for (j=0; j < genes.length-1; j++) ant_genes[j] = genes[j];	  
		  tid_lst_ant = this.countSupport(ant_genes);

		  all_sup = tid_lst_all.size() / (double) nTrans;
		  ant_sup = tid_lst_ant.size() / (double) nTrans;
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
  
  
  private int numCoveredRecords (double minSupport) {
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
		  if (rule.getAll_support() >= minSupport) {
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
