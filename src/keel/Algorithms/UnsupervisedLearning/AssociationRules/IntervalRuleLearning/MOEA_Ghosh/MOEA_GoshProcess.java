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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.MOEA_Ghosh;


import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;
import org.core.Randomize;

public class MOEA_GoshProcess {
	/**
	 * <p>
	 * It provides the implementation of the algorithm to be run in a process
	 * </p>
	 */
	private String paretos;
	private myDataset dataset;
	private int nTrials;
	private int pointCrossover;
	private int numObjectives;
	private double pc;
	private double pm;
	private double af;   
	private int uPopSize;
	private int nAttr;
	private int nTrans;
	private int trials;
	private int max_rank;

	private ArrayList<Chromosome> uPop;
	private ArrayList<Chromosome> child_pop;
	private ArrayList<Chromosome> separate_pop;

	/**
	 * <p>
	 * It creates a new process for the algorithm by setting up its parameters
	 * </p>
	 * @param dataset The instance of the dataset for dealing with its records
	 * @param nTrials The maximum number of generations to reach before completing the whole evolutionary learning
	 * @param popSize The number of chromosomes in the population
	 * @param pc The probability for the crossover operator
	 * @param pointCrossover The number of point crossover to uses in crossover operator
	 * @param pm The probability for the mutation operator
	 * @param af The factor of amplitude for each of the dataset attribute
	 */

	//variant 1
	public MOEA_GoshProcess (myDataset dataset, int numObjectives, int nTrials, int popSize, int pointCrossover, double pc, double pm, double af) {
		
		this.dataset = dataset;
		this.nTrials = nTrials;
		this.uPopSize = popSize;
		this.pointCrossover = pointCrossover;
		this.pc = pc;
		this.pm = pm;
		this.af = af; 

		this.numObjectives = numObjectives;

		this.nAttr = this.dataset.getnVars();
		this.nTrans = this.dataset.getnTrans();
		this.trials = 0;
		this.paretos = new String("");

		this.uPop = new ArrayList<Chromosome>();
		this.child_pop = new ArrayList<Chromosome>();
		this.separate_pop = new ArrayList<Chromosome>();
	}

	/**
	 * <p>
	 * It runs the evolutionary learning for mining association rules
	 * </p>
	 */
	public void run(){
		int nGn = 0;
		int rescue = 10000;
		this.trials = 0;
		this.paretos = new String("");

		this.initializePopulation();

		do {
			System.out.println("Computing Generation " + (nGn + 1) + "... ");

			this.assign_rank(this.uPop);  
			this.assign_fitness();
			this.update_separate_pop();
			this.selection();
			this.genetic_operators();

			nGn++;
			if (this.trials > rescue && rescue < this.nTrials) {
				rescue += 10000;
			}
		}	while (this.trials < this.nTrials);

		printPareto();

		System.out.println("done.\n");
		System.out.println("Number of trials = " + this.trials + "\n");

	}

	private void initializePopulation() {
		int i, j, tmp, pos, operator;
		double value;
		double[] example;
		int[] sample;
		ArrayList<Integer> tr_not_marked;	  
		Gene[] rnd_genes;
		Chromosome chromo;

		this.uPop.clear();
		tr_not_marked = new ArrayList<Integer> ();	  
		rnd_genes = new Gene[this.nAttr];
		sample = new int[this.nAttr];

		this.trials = 0;

		for(i=0; i < this.nAttr; i++)  rnd_genes[i] = new Gene();
		for(i=0; i < this.nAttr; i++)  sample[i] = i;
		for(i=0; i < this.nTrans; i++)  tr_not_marked.add(i);

		while(this.uPop.size() <= this.uPopSize){
			if(tr_not_marked.size() == 0)
				for(i=0; i<this.nTrans; i++)  tr_not_marked.add(i);

			pos =  tr_not_marked.get(Randomize.Randint(0, tr_not_marked.size()));
			example = this.dataset.getExample(pos);

			for(i=0; i < this.nAttr; i++) {
				j = Randomize.Randint(0, this.nAttr);
				tmp = sample[i];
				sample[i] = sample[j];
				sample[j] = tmp;
			}

			for (i=0; i < nAttr; i++) {
				rnd_genes[sample[i]].setAttr (sample[i]);	
				rnd_genes[sample[i]].setActAs (Randomize.RandintClosed(Gene.NOT_INVOLVED, Gene.CONSEQUENT));	

				value = example[sample[i]];
				if  (this.dataset.getAttributeType(sample[i]) != myDataset.NOMINAL) {
					if  (this.dataset.getAttributeType(sample[i]) == myDataset.REAL) {
						operator =  Randomize.RandintClosed(Gene.MINOR, Gene.MAJOR);
					}
					else {
						operator = Randomize.RandintClosed(Gene.MINOR, Gene.MAJOR);
					}
				}
				else operator = Randomize.RandintClosed(Gene.EQUAL, Gene.UNEQUAL);

				rnd_genes[sample[i]].setValue(value);
				rnd_genes[sample[i]].setOperator(operator);

			}

			chromo = new Chromosome(rnd_genes, this.numObjectives);
			chromo.forceConsistency();
			chromo.computeObjetives (this.dataset);
			this.trials++;
			if(!equalChromotoPop(chromo, this.uPop)) {
				this.uPop.add(chromo);
				this.deleteTransCovered(chromo, tr_not_marked);
			}
		}
	}


	private void deleteTransCovered (Chromosome chromo, ArrayList<Integer> tr_not_marked){
		int i;
		double [] example;

		for (i = tr_not_marked.size()-1; i >= 0; i--) {
			example = this.dataset.getExample(tr_not_marked.get(i));
			if(chromo.isCovered(example))  tr_not_marked.remove(i); 
		}
	} 

	private boolean equalChromotoPop(Chromosome chromo, ArrayList<Chromosome> pop){
		int i;
		boolean value;
		Chromosome aux;

		value = false;

		for (i=0; (!value) && (i < pop.size()); i++) {
			aux = pop.get(i);
			if(chromo.equals(aux))  value = true;
		}

		return value;
	}

	/**
	 * Selection
	 *
	 */	
	private void selection(){
		Chromosome chr;
		List<LimitRoulette> Listlimit;

		this.child_pop.clear();
		Listlimit = this.getLimitRoulette();

		while (this.child_pop.size() < this.uPopSize) {
			chr = this.roulette(Listlimit);
			this.child_pop.add(chr);
		}
		this.uPop.clear();
		this.uPop = (ArrayList<Chromosome>) this.child_pop.clone(); 
	}

	private void genetic_operators(){
		int i;
		Chromosome dad, mom;
		ArrayList <Chromosome> children; 

		//crossover
		for(i=0; i<this.uPop.size(); i+=2){
			dad = this.uPop.get(i);
			mom = this.uPop.get(i+1);
			if (Randomize.Rand() < this.pc){
				children = multipoint_crossover(dad, mom);
				this.uPop.set(i, children.get(0));
				this.uPop.set(i+1,children.get(1));
			}
		}
	}


	/**
	 * Routine to perform non-dominated sorting 
	 * @param mixed_pop Mixed population 
	 * @param new_pop new population
	 */
	private void update_separate_pop (){

		boolean finish_rank = false;
		int i;

		Collections.sort(this.uPop);

		for(i=0; i<this.uPop.size()&& !finish_rank;i++){
			if(this.uPop.get(i).getRank() == 1){
				if(!equalChromotoPop(this.uPop.get(i),this.separate_pop))
					this.separate_pop.add(this.uPop.get(i).copy());
			}
			else finish_rank = true;
		}

		this.assign_rank(this.separate_pop);

		finish_rank = false;
		Collections.sort(this.separate_pop);

		for(i= this.separate_pop.size()-1; i > 0 && !finish_rank;i--){
			if(this.separate_pop.get(i).getRank() != 1){
				this.separate_pop.remove(i);
			}
			else finish_rank = true;
		}
	}


	/**
	 * Exchange the genes of the father and mother according crossing points at intervals
	 * odd ex.> 1-2, 3-4....
	 * @param dad
	 * @param mom
	 */
	private ArrayList<Chromosome> multipoint_crossover (Chromosome dad, Chromosome mom) {
		Gene[] genesSon1;
		Gene[] genesSon2;
		Chromosome son1, son2;

		genesSon1 = new Gene[this.nAttr];
		genesSon2 = new Gene[this.nAttr];

		ArrayList<Integer> crosspoints = cross_points();
		ArrayList<Chromosome> children = new ArrayList<Chromosome>();

		int posini = 0;
		int posfin = crosspoints.get(0);
		boolean crossover = true;

		for (int i = 0;i < crosspoints.size();i++) {
			if (crossover) {
				for (int j = posini; j < posfin; j++) {
					genesSon1[j] = dad.getGene(j).copy();
					genesSon2[j] = mom.getGene(j).copy();
				}
			} else {
				for (int j = posini; j < posfin; j++) {
					genesSon2[j] = dad.getGene(j).copy();
					genesSon1[j] = mom.getGene(j).copy();
				}
			}
			if(i < crosspoints.size()-1){
				crossover = !crossover;
				posini = crosspoints.get(i);
				posfin = crosspoints.get(i+1);	
			}
		}

		son1 = new Chromosome(genesSon1, this.numObjectives);
		son2 = new Chromosome(genesSon2, this.numObjectives);

		if (Randomize.Rand() < this.pm)  this.mutate (son1);
		if (Randomize.Rand() < this.pm)  this.mutate (son2);

		son1.forceConsistency();
		son2.forceConsistency();

		son1.computeObjetives (this.dataset);
		son2.computeObjetives (this.dataset);
		this.trials += 2;

		children.add(son1);
		children.add(son2);

		return children;


	}

	private ArrayList<Integer> cross_points(){
		Integer num;
		ArrayList<Integer>points = new ArrayList<Integer>();

		for (int i = 0; i< this.pointCrossover;i++){
			for (num = Randomize.Randint(0, this.nAttr); points.contains(num); num = Randomize.Randint(0, this.nAttr));
			int j = 0;
			boolean found = false;
			while ((j<i)&&(!found)) {
				if(points.get(j) > num){
					found = true;
				}	
				else
					j++;
			}
			points.add(j,num);			 
		}

		points.add(this.nAttr);

		return points;
	}

	private void mutate (Chromosome chr) {
		int i;
		double type_attr, min_attr, max_attr;
		Gene gene;

		i = Randomize.Randint(0, this.nAttr);
		gene = chr.getGene(i);

		type_attr = this.dataset.getAttributeType(i);
		min_attr = this.dataset.getMin(i);
		max_attr = this.dataset.getMax(i);

		if (type_attr != myDataset.NOMINAL) {
			if (type_attr == myDataset.REAL) {
				gene.setValue(Randomize.RanddoubleClosed (min_attr, max_attr));	  
			}
			else {
				gene.setValue(Randomize.RandintClosed((int)min_attr, (int)max_attr));
			}
			gene.setOperator(gene.randOperatorNumeric());
		}
		else {
			gene.setValue(Randomize.RandintClosed((int)min_attr, (int)max_attr));
			gene.setOperator(gene.randOperatorNominal());
		}
		gene.setActAs (gene.randAct());
		chr.forceConsistency();
		chr.computeObjetives(this.dataset);
	}

	private  List<LimitRoulette> getLimitRoulette(){

		double totalWeight = 0,probF;

		for (int i = 0; i < this.uPop.size(); i++) {
			totalWeight = this.uPop.get(i).getFitness_rank() + totalWeight;
		}

		List<Double> listProb = new ArrayList<Double>();

		for (int i = 0; i < this.uPop.size(); i++) {
			probF = this.uPop.get(i).getFitness_rank() / totalWeight;
			listProb.add(probF);
		}

		List<LimitRoulette> listLimit = new ArrayList<LimitRoulette>();
		double limitHigh = 0;
		double limitLow = 0;

		for (int i = 0; i < listProb.size(); i++) {
			LimitRoulette limitRoulette = new LimitRoulette();
			limitHigh = listProb.get(i) + limitHigh;
			limitRoulette.setLimitHigh(limitHigh);
			limitRoulette.setLimitLow(limitLow);
			limitLow = limitHigh;
			limitRoulette.setChromosome(this.uPop.get(i));
			listLimit.add(limitRoulette);
		}

		return listLimit;
	}

	private Chromosome roulette(List<LimitRoulette> listLimit) {

		double numbAleatory = Randomize.Rand();
		boolean find = false;
		int i = 0;
		while ((find == false) && (i < listLimit.size())){
			if((listLimit.get(i).getLimitLow() <= numbAleatory) && (numbAleatory <= listLimit.get(i).getLimitHigh()))
				find = true;
			else i++;
		}
		return listLimit.get(i).getChromosome();
	}


	/**
	 * Routine for usual non-domination checking
	 * @param a chromosome a
	 * @param b chromosome b
	 * @return   1 if a dominates b, -1 if b dominates a and 0 if both a and b are non-dominated
	 */
	 private int check_dominance (Chromosome a, Chromosome b){
		 int i;
		 int flag1;
		 int flag2;
		 flag1 = 0;
		 flag2 = 0;

		 for (i=0; i<this.numObjectives; i++){
			 if (a.getObjective(i) > b.getObjective(i))  flag1 = 1;
			 else if (a.getObjective(i) < b.getObjective(i))  flag2 = 1;
		 }

		 if ((flag1 == 1) && (flag2 == 0))  return (1);
		 else if ((flag1 == 0) && (flag2 == 1))  return (-1);
		 else  return (0);
	 }


	 /**
	  * Function to assign rank to a population of size pop_size
	  * @param new_pop population 
	  */
	 private void assign_rank (ArrayList<Chromosome> new_pop){
		 int flag;
		 int i;
		 int end;
		 int front_size;
		 int rank=1;
		 Lists orig;
		 Lists cur;
		 Lists temp1, temp2;
		 orig = new Lists();
		 cur = new Lists();
		 front_size = 0;

		 temp1 = orig;
		 for (i=0; i< new_pop.size(); i++) {
			 temp1.insert (temp1,i);
			 temp1 = temp1.child;
		 }
		 do {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
			 if (orig.child.child == null) {
				 new_pop.get(orig.child.index).rank = rank;
				 break;
			 }
			 temp1 = orig.child;
			 temp1.insert (cur, temp1.index);
			 front_size = 1;
			 temp2 = cur.child;
			 temp1 = temp1.del (temp1);
			 temp1 = temp1.child;
			 do
			 {
				 temp2 = cur.child;
				 do
				 {
					 end = 0;
					 flag = check_dominance (new_pop.get(temp1.index), new_pop.get(temp2.index));
					 if (flag == 1)
					 {
						 temp1.insert (orig, temp2.index);
						 temp2 = temp2.del (temp2);
						 front_size--;
						 temp2 = temp2.child;
					 }
					 if (flag == 0)
					 {
						 temp2 = temp2.child;
					 }
					 if (flag == -1)
					 {
						 end = 1;
					 }
				 } while (end!=1 && temp2!=null);
				 if (flag == 0 || flag == 1) {
					 temp1.insert (cur, temp1.index);
					 front_size++;
					 temp1 = temp1.del (temp1);
				 }
				 temp1 = temp1.child;
			 } while (temp1 != null);

			 temp2 = cur.child;

			 do {
				 new_pop.get(temp2.index).rank = rank;
				 temp2 = temp2.child;
			 } while (temp2 != null);

			 temp2 = cur.child;

			 do {
				 temp2 = temp2.del (temp2);
				 temp2 = temp2.child;
			 } while (cur.child !=null);

			 rank++;
		 } while (orig.child != null);
		 this.max_rank = rank;

		 return;
	 }	

	 private void assign_fitness(){
		 int i;
		 for(i=0; i<this.uPop.size(); i++){
			 this.uPop.get(i).computeFitness(this.max_rank);
		 }
	 }

	 public ArrayList<AssociationRule> generateRulesPareto() {
		 int i;
		 boolean stop;
		 Chromosome chromo;
		 ArrayList<AssociationRule> rulesPareto = new ArrayList<AssociationRule>();
		 stop = false;

		 for (i=0; i < this.separate_pop.size() && !stop; i++) {
			 chromo = this.separate_pop.get(i);
			 if (chromo.getRank() < 2) {
				 rulesPareto.add (new AssociationRule(chromo));
			 }
			 else  stop = true;
		 }
		 return rulesPareto;
	 }

	 static double roundDouble(double number, int decimalPlace){
		 double numberRound;

		 if(!Double.isInfinite(number)&&(!Double.isNaN(number))){
			 BigDecimal bd = new BigDecimal(number);
			 bd = bd.setScale(decimalPlace, BigDecimal.ROUND_UP);
			 numberRound = bd.doubleValue();
			 return numberRound;
		 }else return number;
	 }

	 public void saveReport(ArrayList<AssociationRule> rules,PrintWriter w) {
		 int i, j, r, cnt_cov_rec;
		 double avg_yulesQ = 0.0, avg_sup = 0.0, avg_conf = 0.0, avg_ant_length = 0.0, avg_lift = 0.0, avg_conv = 0.0, avg_CF = 0.0, avg_netConf = 0.0;
		 int[] covered;   
		 AssociationRule rule;

		 covered = new int[this.nTrans];
		 for (i=0; i < this.nTrans; i++)  covered[i] = 0;

		 for (r=0; r < rules.size(); r++) {
			 rule = rules.get(r);

			 avg_sup += rule.getSupport();
			 avg_conf += rule.getConfidence();
			 avg_lift += rule.getLift();
			 avg_ant_length += (rule.getnAnts()+rule.getConsequents().size());
			 avg_conv += rule.getConv();
			 avg_CF += rule.getCF();
			 avg_netConf += rule.getNetConf();
			 avg_yulesQ += rule.getYulesQ();

			 for (j=0; j < this.nTrans; j++) {
				 if (covered[j] < 1) {
					 if (rule.isCovered(this.dataset.getExample(j)))  covered[j] = 1;
				 }
			 }
		 }

		 cnt_cov_rec = 0;
		 for (i=0; i < this.nTrans; i++)  cnt_cov_rec += covered[i];

		 w.println("\nNumber of Frequent Itemsets found:\"" + "-");	
		 System.out.println("\nNumber of Frequent Itemsets found: " + "-");
		 w.println("\nNumber of Association Rules generated:\"" + rules.size());	  
		 System.out.println("\nNumber of Association Rules generated: " + rules.size());

		 if (! rules.isEmpty()) {
			 w.println("Average Support: " + roundDouble(( avg_sup / rules.size() ),2));
			 System.out.println("Average Support: " + roundDouble(( avg_sup / rules.size() ),2));
			 w.println("Average Confidence: " + roundDouble(( avg_conf / rules.size() ),2));
			 System.out.println("Average Confidence: " + roundDouble(( avg_conf / rules.size() ),2));
			 w.println("Average Lift: " + roundDouble(( avg_lift / rules.size() ),2));
			 System.out.println("Average Lift: " + roundDouble(( avg_lift / rules.size() ),2));
			 w.println("Average Conviction: " + roundDouble(( avg_conv / rules.size() ),2));
			 System.out.println("Average Conviction: " + roundDouble(( avg_conv/ rules.size() ),2));
			 w.println("Average Certain Factor: " + roundDouble(( avg_CF/ rules.size() ),2));
			 System.out.println("Average Certain Factor: " + roundDouble(( avg_CF/ rules.size()),2));
			 w.println("Average Netconf: " + roundDouble(( avg_netConf/ rules.size() ),2));
			 System.out.println("Average Netconf: " + roundDouble(( avg_netConf/ rules.size()),2));
			 w.println("Average YulesQ: " + roundDouble(( avg_yulesQ/ rules.size() ),2));
			 System.out.println("Average YulesQ: " + roundDouble(( avg_yulesQ/ rules.size()),2));
			 w.println("Average Number of Antecedents: " + roundDouble((avg_ant_length / rules.size() ),2));
			 System.out.println("Average Number of Antecedents: " + roundDouble(( avg_ant_length / rules.size() ),2));
			 w.println("Number of Covered Records (%): " + roundDouble((100.0 * cnt_cov_rec) / this.nTrans, 2));
			 System.out.println("Number of Covered Records (%): " + roundDouble((100.0 * cnt_cov_rec) / this.nTrans, 2));
		 } else System.out.println("No Statistics.");
	 }

	 public void printPareto() {
		 int i;
		 boolean stop;
		 Chromosome chromo;

		 stop = false;

		 this.paretos += "";
		 this.paretos += ("Support\tantecedent_support\tconsequent_support\tConfidence\tLift\tConv\tCF\tNetConf\tYulesQ\tnAttributes\n");
		 for (i=0; i < this.separate_pop.size() && !stop; i++) {
			 chromo = this.separate_pop.get(i);
			 if (chromo.getRank() < 2) {
				 this.paretos += ("" + roundDouble(chromo.getSupport(),2) + "\t" + roundDouble(chromo.getAntsSupport(),2) + "\t" + roundDouble(chromo.getConsSupport(),2) + "\t" + roundDouble(chromo.getConfidence(),2) + "\t" + roundDouble(chromo.getLift(),2) + "\t" + roundDouble(chromo.getConv(),2) + "\t" + roundDouble(chromo.getCF(),2) + "\t" + roundDouble(chromo.getNetConf(),2) + "\t" + roundDouble(chromo.getYulesQ(),2) + "\t" + (chromo.getnAnts()+1) + "\n");
			 }
			 else  stop = true;
		 }
	 }

	 public String getParetos() {
		 return (this.paretos);
	 }


}
