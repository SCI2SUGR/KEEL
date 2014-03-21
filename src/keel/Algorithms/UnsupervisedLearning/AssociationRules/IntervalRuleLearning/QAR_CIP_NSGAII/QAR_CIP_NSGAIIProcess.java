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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.QAR_CIP_NSGAII;

/**
 * <p>
 * @author Written by Diana Martín (dmartin@ceis.cujae.edu.cu) 
 * @version 1.1
 * @since JDK1.6
 * </p>
 */

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;

import org.core.Randomize;

public class QAR_CIP_NSGAIIProcess {
	/**
	 * <p>
	 * It provides the implementation of the algorithm to be run in a process
	 * </p>
	 */

	private static double INF = 1.0e14;

	private String paretos;
	private myDataset dataset;
	private int nTrials;
	private int numObjectives;
	private double minSupport;
	private double pm;
	private double af;   
	private int uPopSize;
	private int nAttr;
	private int nTrans;
	private int trials;
	private int updatePop;
	private double percentUpdate;
	private ArrayList<Chromosome> uPop;
	private ArrayList<Chromosome> child_pop;
	private ArrayList<Chromosome> mixed_pop;
	private ArrayList<Chromosome> EP;

	/**
	 * <p>
	 * It creates a new process for the algorithm by setting up its parameters
	 * </p>
	 * @param dataset The instance of the dataset for dealing with its records
	 * @param nTrials The maximum number of generations to reach before completing the whole evolutionary learning
	 * @param popSize The number of chromosomes in the population
	 * @param pm The probability for the mutation operator
	 * @param af The factor of amplitude for each of the dataset attribute
	 * @param percentUpdate The difference threshold to restart the population
	 */

	//variant 1
	public QAR_CIP_NSGAIIProcess (myDataset dataset, int numObjectives, int nTrials, int popSize, double pm, double af,double percentUpdate) {

		this.dataset = dataset;
		this.nTrials = nTrials;
		this.uPopSize = popSize;
		this.pm = pm;
		this.af = af;
		this.minSupport = 0;
		this.percentUpdate = percentUpdate;

		this.numObjectives = numObjectives;

		this.nAttr = this.dataset.getnVars();
		this.nTrans = this.dataset.getnTrans();
		this.trials = 0;
		this.paretos = new String("");

		this.uPop = new ArrayList<Chromosome>();
		this.child_pop = new ArrayList<Chromosome>();
		this.mixed_pop = new ArrayList<Chromosome>();
		this.EP = new ArrayList<Chromosome>();
	}

	/**
	 * <p>
	 * It runs the evolutionary learning for mining association rules
	 * </p>
	 */
	public void run(){
		int nGn = 0;
		this.trials = 0;
		this.paretos = new String("");
		
		System.out.println("Initialization");
		this.initializePopulation();
		this.assign_rank_and_crowding_distance(this.uPop);  

		do {
			System.out.println("Computing Generation " + (nGn + 1));
			this.updatePop = 0;
			this.selection();
			this.merge();
			this.fill_nondominated_sort();
			this.upadateEP_pop();
			this.verifyRestartPop(this.percentUpdate);
			nGn++;
		}	while (this.trials < this.nTrials);

		this.removeRedundant(this.uPop);
		this.removeRedundant(this.EP);
		printPareto();
		System.out.println("done.\n");
	}

	private void initializePopulation() {
		int i, j, tmp, pos, nAnts;
		double lb, ub, value;
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

			nAnts = Randomize.Randint(1, this.nAttr);

			// Antecedent
			for (i=0; i < nAnts; i++) {
				rnd_genes[sample[i]].setAttr (sample[i]);	
				rnd_genes[sample[i]].setActAs (Gene.ANTECEDENT);	

				value = example[sample[i]];
				if  (this.dataset.getAttributeType(sample[i]) != myDataset.NOMINAL) {
					if  (this.dataset.getAttributeType(sample[i]) == myDataset.REAL) {
						lb = Math.max(value - (this.dataset.getAmplitude(sample[i]) / (this.af * 4)), this.dataset.getMin(sample[i]));
						ub = Math.min(value + (this.dataset.getAmplitude(sample[i]) / (this.af * 4)), this.dataset.getMax(sample[i]));
					}
					else {
						lb = Math.max((int) (value - (this.dataset.getAmplitude(sample[i]) / (this.af * 4))), this.dataset.getMin(sample[i]));
						ub = Math.min((int) (value + (this.dataset.getAmplitude(sample[i]) / (this.af * 4))), this.dataset.getMax(sample[i]));
					}
				}
				else lb = ub = (int) value;

				rnd_genes[sample[i]].setLowerBound(lb);
				rnd_genes[sample[i]].setUpperBound(ub);
				rnd_genes[sample[i]].setIsPositiveInterval(true);
			}

			// Consequent
			rnd_genes[sample[i]].setAttr (sample[i]);	
			rnd_genes[sample[i]].setActAs (Gene.CONSEQUENT);	

			value = example[sample[i]];
			if  (this.dataset.getAttributeType(sample[i]) != myDataset.NOMINAL) {
				if  (this.dataset.getAttributeType(sample[i]) == myDataset.REAL) {
					lb = Math.max(value - (this.dataset.getAmplitude(sample[i]) / (this.af * 4)), this.dataset.getMin(sample[i]));
					ub = Math.min(value + (this.dataset.getAmplitude(sample[i]) / (this.af * 4)), this.dataset.getMax(sample[i]));
				}
				else {
					lb = Math.max((int) (value - (this.dataset.getAmplitude(sample[i]) / (this.af * 4))), this.dataset.getMin(sample[i]));
					ub = Math.min((int) (value + (this.dataset.getAmplitude(sample[i]) / (this.af * 4))), this.dataset.getMax(sample[i]));
				}
			}
			else lb = ub = (int) value;

			rnd_genes[sample[i]].setLowerBound(lb);
			rnd_genes[sample[i]].setUpperBound(ub);
			rnd_genes[sample[i]].setIsPositiveInterval(true);

			// Rest of the rule
			for (i = nAnts + 1; i < this.nAttr; i++) {
				rnd_genes[sample[i]].setAttr (sample[i]);	
				rnd_genes[sample[i]].setActAs (Gene.NOT_INVOLVED);	

				if  (this.dataset.getAttributeType(sample[i]) != myDataset.NOMINAL) {
					if  (this.dataset.getAttributeType(sample[i]) == myDataset.REAL) {
						value = Randomize.RanddoubleClosed (this.dataset.getMin(sample[i]), this.dataset.getMax(sample[i]));
						lb = Math.max(value - (this.dataset.getAmplitude(sample[i]) / (this.af * 4)), this.dataset.getMin(sample[i]));
						ub = Math.min(value + (this.dataset.getAmplitude(sample[i]) / (this.af * 4)), this.dataset.getMax(sample[i]));
					}
					else {
						value = Randomize.RandintClosed ((int) this.dataset.getMin(sample[i]), (int) this.dataset.getMax(sample[i]));
						lb = (int) Math.max((int) (value - (this.dataset.getAmplitude(sample[i])) / (this.af * 4)), this.dataset.getMin(sample[i]));
						ub = (int) Math.min((int) (value + (this.dataset.getAmplitude(sample[i])) / (this.af * 4)), this.dataset.getMax(sample[i]));
					}
				}
				else {
					value = Randomize.RandintClosed ((int) this.dataset.getMin(sample[i]), (int) this.dataset.getMax(sample[i]));
					lb = ub = (int) value;
				}

				rnd_genes[sample[i]].setLowerBound(lb);
				rnd_genes[sample[i]].setUpperBound(ub);
				rnd_genes[sample[i]].setIsPositiveInterval(true);
			}

			chromo = new Chromosome(rnd_genes, this.numObjectives);
			chromo.computeObjetives (this.dataset);
			this.trials++;
			if(!equalChromotoPop(chromo, this.uPop)&& (chromo.getSupport()> this.minSupport)&&(!(chromo.getSupport()> 1 - this.minSupport))&& (chromo.getCF()>0)) {
				this.uPop.add(chromo);
				this.deleteTransCovered(chromo, tr_not_marked);
			}
		}
		this.EP = this.nonDominateSelection(this.uPop);
	}


	private void deleteTransCovered (Chromosome chromo, ArrayList<Integer> tr_not_marked){
		int i;
		double [] example;

		for (i = tr_not_marked.size()-1; i >= 0; i--) {
			example = this.dataset.getExample(tr_not_marked.get(i));
			if(chromo.isCovered(example))  tr_not_marked.remove(i); 
		}
	} 

	private void upadateEP_pop(){
		for(int i=0; i< this.uPop.size();i++){
			this.updateEP(this.uPop.get(i));
		}
	}

	private void verifyRestartPop(double percent){
		double percentUpdate = (this.uPopSize*percent)/100;

		if(this.updatePop < percentUpdate)
			this.restartPop();
	}

	private void restartPop(){
		int i,k,pos,random_pos, cont;
		ArrayList<Integer> tr_not_marked;
		Chromosome chromo;


		tr_not_marked = this.tr_notCovered_NoDominateSolutions();

		this.uPop.clear();
		//initialize population

		k = 0;
		cont = 0;

		while( k < this.uPopSize){
			if(tr_not_marked.size() == 0)
				for(i=0; i < this.nTrans; i++)  tr_not_marked.add(i);

			//create chromo
			random_pos = Randomize.Randint(0, tr_not_marked.size());
			pos =  tr_not_marked.get(random_pos);
			chromo = this.generateChromoCovered(pos);
			tr_not_marked.remove(random_pos);

			if(!equalChromotoPop(chromo, this.uPop)&& (chromo.getSupport()> this.minSupport)&&(!(chromo.getSupport()>(1 - this.minSupport)))&& (chromo.getCF()>0)) {
				this.uPop.add(chromo);
				this.deleteTransCovered(this.uPop.get(k), tr_not_marked);
				this.updateEP(chromo);
				k++;
				cont = 0;
			}
			else if (cont > 1000) {
				tr_not_marked.clear();
				cont = 0;
			}
			else  cont++;									
		}
	}

	private Chromosome generateChromoCovered(int pos_example){
		int i, j,tmp, nAnts;
		double lb, ub, value;
		double[] example;
		int[] sample;
		Gene[] rnd_genes;
		Chromosome chromo;

		example = this.dataset.getExample(pos_example);

		rnd_genes = new Gene[this.nAttr];
		sample = new int[this.nAttr];

		for(i=0; i < this.nAttr; i++)  rnd_genes[i] = new Gene();
		for(i=0; i < this.nAttr; i++)  sample[i] = i;

		for(i=0; i < this.nAttr; i++) {
			j = Randomize.Randint(0, this.nAttr);
			tmp = sample[i];
			sample[i] = sample[j];
			sample[j] = tmp;
		}

		nAnts = Randomize.Randint(1, this.nAttr);

		// Antecedent
		for (i=0; i < nAnts; i++) {
			rnd_genes[sample[i]].setAttr (sample[i]);	
			rnd_genes[sample[i]].setActAs (Gene.ANTECEDENT);	

			value = example[sample[i]];
			if  (this.dataset.getAttributeType(sample[i]) != myDataset.NOMINAL) {
				if  (this.dataset.getAttributeType(sample[i]) == myDataset.REAL) {
					lb = Math.max(value - (this.dataset.getAmplitude(sample[i]) / (this.af * 4)), this.dataset.getMin(sample[i]));
					ub = Math.min(value + (this.dataset.getAmplitude(sample[i]) / (this.af * 4)), this.dataset.getMax(sample[i]));
				}
				else {
					lb = Math.max((int) (value - (this.dataset.getAmplitude(sample[i]) / (this.af * 4))), this.dataset.getMin(sample[i]));
					ub = Math.min((int) (value + (this.dataset.getAmplitude(sample[i]) / (this.af * 4))), this.dataset.getMax(sample[i]));
				}
			}
			else lb = ub = (int) value;

			rnd_genes[sample[i]].setLowerBound(lb);
			rnd_genes[sample[i]].setUpperBound(ub);
			rnd_genes[sample[i]].setIsPositiveInterval(true);
		}

		// Consequent
		rnd_genes[sample[i]].setAttr (sample[i]);	
		rnd_genes[sample[i]].setActAs (Gene.CONSEQUENT);	

		value = example[sample[i]];
		if  (this.dataset.getAttributeType(sample[i]) != myDataset.NOMINAL) {
			if  (this.dataset.getAttributeType(sample[i]) == myDataset.REAL) {
				lb = Math.max(value - (this.dataset.getAmplitude(sample[i]) / (this.af * 4)), this.dataset.getMin(sample[i]));
				ub = Math.min(value + (this.dataset.getAmplitude(sample[i]) / (this.af * 4)), this.dataset.getMax(sample[i]));
			}
			else {
				lb = Math.max((int) (value - (this.dataset.getAmplitude(sample[i]) / (this.af * 4))), this.dataset.getMin(sample[i]));
				ub = Math.min((int) (value + (this.dataset.getAmplitude(sample[i]) / (this.af * 4))), this.dataset.getMax(sample[i]));
			}
		}
		else lb = ub = (int) value;

		rnd_genes[sample[i]].setLowerBound(lb);
		rnd_genes[sample[i]].setUpperBound(ub);
		rnd_genes[sample[i]].setIsPositiveInterval(true);

		// Rest of the rule
		for (i = nAnts + 1; i < this.nAttr; i++) {
			rnd_genes[sample[i]].setAttr (sample[i]);	
			rnd_genes[sample[i]].setActAs (Gene.NOT_INVOLVED);	

			if  (this.dataset.getAttributeType(sample[i]) != myDataset.NOMINAL) {
				if  (this.dataset.getAttributeType(sample[i]) == myDataset.REAL) {
					value = Randomize.RanddoubleClosed (this.dataset.getMin(sample[i]), this.dataset.getMax(sample[i]));
					lb = Math.max(value - (this.dataset.getAmplitude(sample[i]) / (this.af * 4)), this.dataset.getMin(sample[i]));
					ub = Math.min(value + (this.dataset.getAmplitude(sample[i]) / (this.af * 4)), this.dataset.getMax(sample[i]));
				}
				else {
					value = Randomize.RandintClosed ((int) this.dataset.getMin(sample[i]), (int) this.dataset.getMax(sample[i]));
					lb = (int) Math.max((int) (value - (this.dataset.getAmplitude(sample[i])) / (this.af * 4)), this.dataset.getMin(sample[i]));
					ub = (int) Math.min((int) (value + (this.dataset.getAmplitude(sample[i])) / (this.af * 4)), this.dataset.getMax(sample[i]));
				}
			}
			else {
				value = Randomize.RandintClosed ((int) this.dataset.getMin(sample[i]), (int) this.dataset.getMax(sample[i]));
				lb = ub = (int) value;
			}

			rnd_genes[sample[i]].setLowerBound(lb);
			rnd_genes[sample[i]].setUpperBound(ub);
			rnd_genes[sample[i]].setIsPositiveInterval(true);
		}

		chromo = new Chromosome(rnd_genes, this.numObjectives);

		chromo.computeObjetives(this.dataset);
		this.trials++;

		return chromo;
	}

	private ArrayList<Integer> tr_notCovered_NoDominateSolutions(){
		ArrayList<Integer> tr_not_marked;
		tr_not_marked = new ArrayList<Integer> ();
		int i;

		for(i=0; i < this.nTrans; i++)  tr_not_marked.add(i);

		for(i=0; i<this.EP.size();i++){
			this.deleteTransCovered(this.EP.get(i), tr_not_marked);
		}

		return tr_not_marked;
	}

	/**
	 * Non-domination Selection procedure to filter out the dominated
	 * individuals from the given list.
	 * 
	 * @param collection the collection of Chromosome object need to be filtered.
	 * @return the reference copy of the non-dominating individuals
	 */
	private void updateEP( Chromosome child) {
		int counter = 0, dominance;
		Chromosome chromosome1;

		boolean add = true;

		while (counter < this.EP.size()) {

			chromosome1 = this.EP.get(counter);
			dominance = check_dominance(child, chromosome1);

			if (dominance == 1) { //child dominates chromosome1 
				this.EP.remove(counter);
			} 
			else if (dominance == -1) { //chromosome1 dominates child
				add = false;
			}
			counter++;
		}

		if(add){
			if(!equalChromotoPop(child, this.EP))
				this.EP.add(child);
		}
	}


	/**
	 * Non-domination Selection procedure to filter out the dominated
	 * individuals from the given list.
	 * 
	 * @param collection the collection of Chromosome object need to be filtered.
	 * @return the reference copy of the non-dominating individuals
	 */
	private ArrayList<Chromosome> nonDominateSelection( ArrayList<Chromosome> collection) {
		int counter = 1, dominance;
		Chromosome chromosome1, chromosome2;
		ArrayList<Chromosome> result;


		result = new ArrayList<Chromosome>();
		result.add(collection.get(0));


		out: while (counter < collection.size()) {
			int jj = 0;
			chromosome2 = collection.get(counter);
			int resultsize = result.size();
			boolean remove[] = new boolean[resultsize];

			while (jj < resultsize) {
				chromosome1 = result.get(jj);
				dominance = check_dominance(chromosome2, chromosome1);

				if (dominance == 1) { //chromosome2 dominates chromosome1 
					remove[jj] = true;

				} else if (dominance == -1) { //chromosome1 dominates chromosome2
					counter++;
					continue out;
				}
				jj++;
			}
			for (int i = remove.length - 1; i >= 0; i--) {
				if (remove[i])
					result.remove(i);
			}
			result.add(chromosome2);
			counter++;
		}
		return result;
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
		int dad, mom;

		this.child_pop.clear();

		while ((this.child_pop.size() < this.uPopSize)&& (this.trials < this.nTrials)){
			dad = this.tournamentSelection();
			for (mom = this.tournamentSelection(); dad == mom; mom = this.tournamentSelection());
			crossover(this.uPop.get(dad), this.uPop.get(mom));
		}
	}


	/**
	 * Routine to fill a population with individuals in the decreasing order of crowding distance
	 * @param mixed_pop Mixed population 
	 * @param new_pop population to fill
	 * @param count start index
	 * @param front_size
	 * @param elite list with elite
	 */
	private void crowding_fill (int count, int front_size, Lists elite){
		int []dist;
		Lists temp;
		int i, j;

		this.assign_crowding_distance_list (this.mixed_pop, elite.child, front_size);
		dist = new int[front_size];
		temp = elite.child;
		for (j=0; j<front_size; j++)
		{
			dist[j] = temp.index;
			temp = temp.child;
		}
		Sort.quicksort_dist (this.mixed_pop, dist, front_size);
		for (i=count, j=front_size-1; i<this.uPopSize; i++, j--)
		{
			this.uPop.add(this.mixed_pop.get(dist[j]).copy()); 
		}

		return;
	}

	/**
	 * Routine to perform non-dominated sorting 
	 * @param mixed_pop Mixed population 
	 * @param new_pop new population
	 */
	private void fill_nondominated_sort (){
		int flag;
		int i, j;
		int end;
		int front_size;
		int archieve_size;
		int rank=1;
		Lists pool;
		Lists elite;
		Lists temp1, temp2;
		pool = new Lists();
		elite = new Lists();
		front_size = 0;
		archieve_size=0;
		temp1 = pool;

		this.uPop.clear();

		for (i=0; i < (this.mixed_pop.size()); i++)
		{
			temp1.insert (temp1,i);
			temp1 = temp1.child;
		}
		i=0;
		do
		{
			temp1 = pool.child;
			temp1.insert (elite, temp1.index);
			front_size = 1;
			temp2 = elite.child;
			temp1 = temp1.del (temp1);
			temp1 = temp1.child;
			do
			{
				temp2 = elite.child;
				if (temp1==null)
				{
					break;
				}
				do
				{
					end = 0;
					flag = check_dominance (this.mixed_pop.get(temp1.index), this.mixed_pop.get(temp2.index));
					if (flag == 1)
					{
						temp2.insert (pool, temp2.index);
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
				}
				while (end!=1 && temp2!=null);
				if (flag == 0 || flag == 1)
				{
					temp1.insert (elite, temp1.index);
					front_size++;
					temp1 = temp1.del (temp1);
				}

				temp1 = temp1.child;


			}
			while (temp1 != null);
			temp2 = elite.child;
			j=i;
			if  ((archieve_size+front_size) <= this.uPopSize)
			{
				do
				{
					if(temp2.index >= this.uPopSize)
						this.updatePop++;


					this.uPop.add(this.mixed_pop.get(temp2.index).copy());
					this.uPop.get(i).rank = rank;
					archieve_size++;
					temp2 = temp2.child;
					i++;
				}
				while (temp2 != null);
				this.assign_crowding_distance_indices (this.uPop, j, i-1);

				rank++;
			}
			else
			{
				this.crowding_fill (i, front_size, elite);

				archieve_size = this.uPopSize;
				for (j=i; j<this.uPopSize; j++)
				{
					this.uPop.get(j).rank = rank;
				}
			}
			temp2 = elite.child;
			do
			{
				temp2 = temp2.del (temp2);
				temp2 = temp2.child;
			}
			while (elite.child !=null);
		} while (archieve_size < this.uPopSize);
	}

	/**
	 * Routine to compute crowding distance based on objective function values when the population in in the form of an array 
	 * @param pop population to sort
	 * @param c1
	 * @param c2
	 */
	private void assign_crowding_distance_indices (ArrayList<Chromosome> pop, int c1, int c2) {
		int [][]obj_array;
		int []dist;
		int j;
		int front_size;
		front_size = c2-c1+1;
		if (front_size==1)
		{
			pop.get(c1).crowd_dist = INF;
			return;
		}
		if (front_size==2)
		{
			pop.get(c1).crowd_dist = INF;
			pop.get(c2).crowd_dist = INF;
			return;
		}
		obj_array =new int[this.numObjectives][front_size];
		dist = new int[front_size];
		for (j=0; j<front_size; j++)
		{
			dist[j] = c1++;
		}
		assign_crowding_distance (pop, dist, obj_array, front_size);
	}

	private void crossover(Chromosome dad, Chromosome mom) {
		int i;
		Gene[] genesSon1;
		Gene[] genesSon2;
		Chromosome son1, son2;

		genesSon1 = new Gene[this.nAttr];
		genesSon2 = new Gene[this.nAttr];

		for (i=0; i < this.nAttr; i++) {
			if ((dad.getGene(i).getActAs() == Gene.CONSEQUENT) || (mom.getGene(i).getActAs() == Gene.CONSEQUENT)) {
				genesSon1[i] = dad.getGene(i).copy();
				genesSon2[i] = mom.getGene(i).copy();
			}
		}

		for (i=0; i < this.nAttr; i++) { 
			if ((dad.getGene(i).getActAs() != Gene.CONSEQUENT) && (mom.getGene(i).getActAs() != Gene.CONSEQUENT)) {
				if (Randomize.Rand() < 0.5) {
					genesSon1[i] = dad.getGene(i).copy();
					genesSon2[i] = mom.getGene(i).copy();
				}
				else {
					genesSon1[i] = mom.getGene(i).copy();
					genesSon2[i] = dad.getGene(i).copy();
				}
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

		if(!equalChromotoPop(son1, this.child_pop)&& (son1.getSupport()> this.minSupport)&&(!(son1.getSupport()>1 - this.minSupport))&& (son1.getCF()>0))
			this.child_pop.add(son1);

		if(!equalChromotoPop(son2, this.child_pop)&& (son2.getSupport()> this.minSupport)&&(!(son2.getSupport()>1 - this.minSupport))&& (son2.getCF()>0))
			this.child_pop.add(son2);
	}


	private void mutate (Chromosome chr) {
		int i;
		double type_attr, min_attr, max_attr, top;
		Gene gene;

		i = Randomize.Randint(0, this.nAttr);
		gene = chr.getGene(i);

		type_attr = this.dataset.getAttributeType(i);
		min_attr = this.dataset.getMin(i);
		max_attr = this.dataset.getMax(i);

		if (type_attr != myDataset.NOMINAL) {
			if (type_attr == myDataset.REAL) {
				if (Randomize.Rand() < 0.5) {
					if (Randomize.Rand() < 0.5) {
						top = Math.max(gene.getUpperBound() - (this.dataset.getAmplitude(i) / this.af), min_attr);
						gene.setLowerBound(Randomize.RanddoubleClosed(top, gene.getLowerBound()));
					}
					else  gene.setLowerBound(Randomize.Randdouble(gene.getLowerBound(), gene.getUpperBound()));
				}
				else {
					if (Randomize.Rand() < 0.5) {
						top = Math.min(gene.getLowerBound() + (this.dataset.getAmplitude(i) / this.af), max_attr);
						gene.setUpperBound(Randomize.RanddoubleClosed(gene.getUpperBound(), top));
					}
					else  gene.setUpperBound(Randomize.RanddoubleClosed(gene.getLowerBound()+0.0001, gene.getUpperBound()));
				}				  
			}
			else {
				if (Randomize.Rand() < 0.5) {
					if (Randomize.Rand() < 0.5) {
						top = Math.max(gene.getUpperBound() - (this.dataset.getAmplitude(i) / this.af), min_attr);
						gene.setLowerBound(Randomize.RandintClosed((int)top, (int)gene.getLowerBound()));
					}
					else  gene.setLowerBound(Randomize.Randint((int)gene.getLowerBound(), (int)gene.getUpperBound()));
				}
				else {
					if (Randomize.Rand() < 0.5) {
						top = Math.min(gene.getLowerBound() + (this.dataset.getAmplitude(i) / this.af), max_attr);
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
		gene.setActAs (gene.randAct());
	}

	private int tournamentSelection() {
		int chromo1, chromo2;

		chromo1 = Randomize.Randint(0, this.uPop.size());
		for (chromo2 = Randomize.Randint(0, this.uPop.size()); chromo1 == chromo2; chromo2 = Randomize.Randint(0, this.uPop.size()));

		if (this.uPop.get(chromo1).isBetter(this.uPop.get(chromo2)) >= 0)  return (chromo1);
		else  return (chromo2);
	}

	private void merge(){
		this.mixed_pop.clear();

		for(int i=0; i<this.uPop.size(); i++)
			if(!equalChromotoPop(this.uPop.get(i),this.mixed_pop))  this.mixed_pop.add(this.uPop.get(i).copy());
		for(int i=0;i<this.child_pop.size();i++)
			if(!equalChromotoPop(this.child_pop.get(i),this.mixed_pop))  this.mixed_pop.add(this.child_pop.get(i).copy());
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
	 * Function to assign rank and crowding distance to a population of size pop_size
	 * @param new_pop population 
	 */
	private void assign_rank_and_crowding_distance (ArrayList<Chromosome> new_pop){
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

		System.err.println ("Tamaña de new_pop = " + new_pop.size() + "   Tamaño que deberia tener = " + this.uPopSize);

		temp1 = orig;
		for (i=0; i< this.uPopSize; i++) {
			temp1.insert (temp1,i);
			temp1 = temp1.child;
		}
		do {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
			if (orig.child.child == null) {
				new_pop.get(orig.child.index).rank = rank;
				new_pop.get(orig.child.index).crowd_dist = INF;
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

			this.assign_crowding_distance_list (new_pop, cur.child, front_size);
			temp2 = cur.child;

			do {
				temp2 = temp2.del (temp2);
				temp2 = temp2.child;
			} while (cur.child !=null);

			rank++;
		} while (orig.child != null);

		return;
	}	

	/**
	 * Routine to compute crowding distance based on ojbective function values when the population in in the form of a list 
	 * @param pop population 
	 * @param dist distance's vector
	 * @param obj_array objetive's array
	 * @param front_size 
	 */
	private void assign_crowding_distance (ArrayList<Chromosome> pop, int[] dist, int[][] obj_array, int front_size){
		int i, j;

		for (i=0; i<this.numObjectives; i++) {
			for (j=0; j<front_size; j++) {
				obj_array[i][j] = dist[j];
			}
			Sort.quicksort_front_obj (pop, i, obj_array[i], front_size);
		}
		for (j=0; j<front_size; j++) {
			pop.get(dist[j]).crowd_dist = 0.0;
		}
		for (i=0; i<this.numObjectives; i++) {
			pop.get(obj_array[i][0]).crowd_dist = INF;
			pop.get(obj_array[i][front_size-1]).crowd_dist = INF;
		}
		for (i=0; i<this.numObjectives; i++) {
			for (j=1; j<front_size-1; j++) {
				if (pop.get(obj_array[i][j]).crowd_dist != INF) {
					if (pop.get(obj_array[i][front_size-1]).getObjective(i) == pop.get(obj_array[i][0]).getObjective(i)) {
						pop.get(obj_array[i][j]).crowd_dist += 0.0;
					}
					else {
						pop.get(obj_array[i][j]).crowd_dist += ((pop.get(obj_array[i][j+1]).getObjective(i) - pop.get(obj_array[i][j-1]).getObjective(i)) / (pop.get(obj_array[i][front_size-1]).getObjective(i) - pop.get(obj_array[i][0]).getObjective(i)));
					}
				}
			}
		}
		for (j=0; j<front_size; j++) {
			if (pop.get(dist[j]).crowd_dist != INF) {
				pop.get(dist[j]).crowd_dist = (pop.get(dist[j]).crowd_dist) / this.numObjectives;
			}
		}
	}


	/**
	 * Routine to assing crowding distance when the population is in the form of a list 
	 * @param pop population 
	 * @param lst 
	 * @param front_size 
	 */
	private void assign_crowding_distance_list (ArrayList<Chromosome> pop, Lists lst, int front_size)
	{
		int [][]obj_array;
		int []dist;
		int j;
		Lists temp; 
		temp = lst;
		if (front_size == 1) {
			pop.get(lst.index).crowd_dist = INF;
			return;
		}
		if (front_size == 2) {
			pop.get(lst.index).crowd_dist = INF;
			pop.get(lst.child.index).crowd_dist = INF;
			return;
		}
		obj_array = new int[this.numObjectives][front_size];
		dist = new int[front_size];

		for (j=0; j<front_size; j++) {
			dist[j] = temp.index;
			temp = temp.child;
		}

		assign_crowding_distance (pop, dist, obj_array, front_size);
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
			avg_ant_length += (rule.getnAnts()+1);
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

		w.println("\nNumber of Frequent Itemsets found: " + "-");	
		System.out.println("\nNumber of Frequent Itemsets found: " + "-");
		w.println("\nNumber of Association Rules generated: " + rules.size());	
		System.out.println("Number of Association Rules generated: " + rules.size());

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
			w.println("Average yulesQ: " + roundDouble(( avg_yulesQ/ rules.size() ),2));
			System.out.println("Average yulesQ: " + roundDouble(( avg_yulesQ/ rules.size()),2));
			w.println("Average Number of Antecedents: " + roundDouble(( avg_ant_length / rules.size() ),2));
			System.out.println("Average Number of Antecedents: " + roundDouble(( avg_ant_length / rules.size() ),2));
			w.println("Number of Covered Records (%): " + roundDouble((100.0 * cnt_cov_rec) / this.nTrans, 2));
			System.out.println("Number of Covered Records (%): " + roundDouble((100.0 * cnt_cov_rec) / this.nTrans, 2));
		}
	}

	public void removeRedundant (ArrayList<Chromosome> upop) {
		int i, j;
		boolean stop;
		Chromosome chromo1, chromo2;

		Collections.sort(upop);

		for (i=0; i < upop.size(); i++) {
			stop = false;
			for (j = upop.size()-1; j >=0 && !stop; j--) {
				if (j != i) {
					chromo1 = upop.get(i);
					chromo2 = upop.get(j);
					if (chromo1.getnAnts() == chromo2.getnAnts()) {
						if (chromo1.isSubChromo(chromo2)) {
							if (chromo1.getCF() >= chromo2.getCF()) {
								upop.remove(j);
								if (j < i) i--;
							}
							else {
								upop.remove(i);
								i--;
								stop = true;
							}
						}
					}
					else if (chromo1.getnAnts() > chromo2.getnAnts()) stop = true;
				}
			}
		}
	}   

	public void printPareto() {
		int i;
		boolean stop;
		Chromosome chromo;

		stop = false;

		this.paretos += ("Support\tantecedent_support\tconsequent_support\tConfidence\tLift\tConv\tCF\tNetConf\tYulesQ\tnAttributes\n");

		for (i=0; i < this.EP.size() && !stop; i++) {
			chromo = this.EP.get(i);
			this.paretos += ("" + roundDouble(chromo.getSupport(),2) + "\t" + roundDouble(chromo.getAntsSupport(),2) + "\t" + roundDouble(chromo.getConsSupport(),2) + "\t" + roundDouble(chromo.getConfidence(),2) + "\t" + roundDouble(chromo.getObjective(0),2) + "\t" + roundDouble(chromo.getConv(),2) + "\t" + roundDouble(chromo.getCF(),2) + "\t" + roundDouble(chromo.getNetConf(),2) + "\t" + roundDouble(chromo.getYulesQ(),2) + "\t" + (chromo.getnAnts()+1) + "\n");
		}

	}
	public ArrayList<AssociationRule> generateRulesPareto() {
		int i;
		boolean stop;
		Chromosome chromo;
		ArrayList<AssociationRule> rulesPareto = new ArrayList<AssociationRule>();
		stop = false;

		for (i=0; i < this.EP.size() && !stop; i++) {
			chromo = this.EP.get(i);
			rulesPareto.add (new AssociationRule(chromo));
		}
		return rulesPareto;
	}

	public String getParetos() {
		return (this.paretos);
	}



}
