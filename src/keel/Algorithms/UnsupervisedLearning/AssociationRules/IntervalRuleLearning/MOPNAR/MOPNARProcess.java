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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.MOPNAR;

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

public class MOPNARProcess {
	/**
	 * <p>
	 * It provides the implementation of the algorithm to be run in a process
	 * </p>
	 */
	
	private final int UPDATE_SOLUTION_NEIGHBOR = 1;
	private final int UPDATE_SOLUTION_WHOLEPOP = 2;


	private String paretos;
	private myDataset dataset;
	private int numObjectives;
	private int nTrials;
	private int H; 
	private int T;
	private double probDelta; 
	private int nr; 
	private double pm;	  
	private double af;
	private int max_rank;
	private int updatePop;
	private double minSupport;
	private double percentUpdate;
	
	
	private int nAttr;
	private int nTrans;
	private int trials;
	private ArrayList<Chromosome> uPop;
	private ArrayList<Chromosome> EP; //an external population (EP), which is used to store non dominated solutions found during the search
	private double [] Z; // Z= (z1,z2...zm) where Zi is the best (max) value found for objective fi  
	private double [] Z_min; // Z_min= (z1,z2...zm) where Zi is the min value found for objective fi  

	/**
	 * <p>
	 * It creates a new process for the algorithm by setting up its parameters
	 * </p>
	 * @param dataset The instance of the dataset for dealing with its records
	 * @param numObjectives The number of objectives to be optimized
	 * @param nTrials The maximum number of generations to reach before completing the whole evolutionary learning
	 * @param H  The parameter to control the population size and weight vectors
	 * @param T  The number of the weight vectors in the neighborhood
	 * @param probDelta  The probability that parent solutions are selected from the neighborhood
	 * @param nr  The maximal number of solutions replaced by each child solution
	 * @param pm The probability for the mutation operator
	 * @param af The factor of amplitude for each attribute of the dataset
	 * @param percentUpdate The difference threshold to restart the population
	 */

	public MOPNARProcess (myDataset dataset, int numObjectives, int nTrials, int H, int T, double probDelta, int nr, double pm, double af, double percentUpdate) {  	  
		this.dataset = dataset;
		this.nTrials = nTrials;
		this.H = H;
		this.T = T;
		this.probDelta = probDelta;
		this.nr = nr;
		this.pm = pm;
		this.af = af; 
		this.minSupport = 0.01;
		this.percentUpdate = percentUpdate;  
		this.numObjectives = numObjectives;

		this.nAttr = this.dataset.getnVars();
		this.nTrans = this.dataset.getnTrans();
		this.trials = 0;
		this.paretos = new String("");
		
		this.uPop = new ArrayList<Chromosome>();
		this.EP = new ArrayList<Chromosome>();
		this.Z = new double [this.numObjectives];  	  
		this.Z_min = new double [this.numObjectives];  	  
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

		do {
			System.out.println("Computing Generation " + (nGn + 1));

			this.updatePop = 0;
			this.diffEvolution(); 
			this.verifyRestartPop();

			nGn++;
		}	while (this.trials < this.nTrials);

		this.removeRedundant(this.EP);

		printPareto();
		System.out.println("done.\n");
	}

	private void verifyRestartPop(){
		double percentUpdate = (this.uPop.size() * this.percentUpdate) / 100.0;

		if(this.updatePop < percentUpdate){
			this.restartPop();
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

	/**
	 * Non-domination Selection procedure to filter out the dominated
	 * individuals from the given list.
	 * 
	 * @param collection the collection of Chromosome object need to be filtered.
	 * @return the reference copy of the non-dominating individuals
	 */
	private void updateEP (Chromosome child) {
		int i, dominance;
		Chromosome chromosome1;		
		boolean add = true;

		for (i=0; i < this.EP.size(); i++) {
			chromosome1 = this.EP.get(i);
			dominance = check_dominance(child, chromosome1);

			if (dominance == 1)  this.EP.remove(i);  //child dominates chromosome1 
			else if (dominance == -1)  add = false; //chromosome1 dominates child
		}

		if (add) {
			if(!this.equalChromotoPop (child, this.EP))  this.EP.add(child);
		}
	}

	private void diffEvolution(){
		int i, type; 

		for(i=0; i < this.uPop.size(); i++){
			if (Randomize.Rand() < this.probDelta)  type = this.UPDATE_SOLUTION_NEIGHBOR;
			else  type = this.UPDATE_SOLUTION_WHOLEPOP;

			this.crossover(this.uPop.get(i), this.uPop.get(this.matingselection(i,type)), type, i);
		}

		//update EP
		for(i= 0; i < this.uPop.size(); i++)  this.updateEP(this.uPop.get(i));
		this.removeRedundant(this.EP);   	
	}

	private void crossover(Chromosome dad, Chromosome mom, int type, int pos_chromo) {
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

		son1 = new Chromosome(genesSon1, this.numObjectives, this.T);
		son2 = new Chromosome(genesSon2, this.numObjectives, this.T);

		if (Randomize.Rand() < this.pm)  this.mutate (son1);
		if (Randomize.Rand() < this.pm)  this.mutate (son2);

		son1.forceConsistency();
		son2.forceConsistency();

		son1.computeObjetives (this.dataset);
		son2.computeObjetives (this.dataset);
		this.trials += 2;

		if((!this.equalChromotoPop(son1, this.uPop)) && (son1.getSupport() > this.minSupport) && (!(son1.getSupport() > (1.0 - this.minSupport))) && (son1.getCF() > 0)){
			this.update_Z(son1);
			this.updateSolutions(son1, type, pos_chromo);
		} 
		if((!this.equalChromotoPop(son2, this.uPop)) && (son2.getSupport() > this.minSupport) && (!(son2.getSupport() > 1.0 - this.minSupport)) && (son2.getCF() > 0)){
			this.update_Z(son2);
			this.updateSolutions(son2, type, pos_chromo);
		}
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

		gene.setIsPositiveInterval ((Randomize.RandintClosed(0, 1) == 1) ? true : false);
		gene.setActAs (gene.randAct());
	}

	private int matingselection(int pos_chr, int type){
		// pos_chr  : the id of current subproblem
		// type : 1 - neighborhood; otherwise - whole population

		if (type == this.UPDATE_SOLUTION_NEIGHBOR)  return (Randomize.Randint(0, this.T));
		else  return (Randomize.Randint(0, this.uPop.size()));
	}

	private int[] random_permutation(int size) {
		int i, j, tmp;
		int[] index = new int[size];

		for(i=0; i < size; i++)  index[i] = i;
		for(i=0; i < size; i++) {
			j = Randomize.Randint(0, size);
			tmp = index[i];
			index[i] = index[j];
			index[j] = tmp;
		}

		return  index;
	} 

	private void setWeightVectorsToEachChromo(){
		int i,j;
		double[] weightVector;
		Chromosome chr;
		Gene[] rnd_genes;
		rnd_genes = new Gene[this.nAttr];
		weightVector = new double [this.numObjectives];

		for(i=0; i < this.nAttr; i++)  rnd_genes[i] = new Gene();

		for(i=0; i <= this.H; i++){
			for(j=0; j <= (this.H - i); j++) {
				weightVector[0] = (1.0 * i) / (1.0 * this.H);
				weightVector[1] = (1.0 * j) / (1.0 * this.H);
				weightVector[2] = (1.0 * (this.H - i - j)) / (1.0 * this.H);

				chr = new Chromosome(rnd_genes, this.numObjectives, this.T);
				chr.setWeightVector(weightVector);

				this.uPop.add(chr);
			}
		}
	}


	/**
	 * Compute euclidean distance between any two weight vector to find 
       the number of closet weight vectors(neighbors vectors) to each weight vector 
	 */

	private double[][] computeEuclidean() {
		double[][] distance = new double[this.uPop.size()][this.uPop.size()];

		for(int i=0; i < this.uPop.size(); i++) distance[i][i] = 0.0;
		for(int i=0; i < this.uPop.size(); i++){
			for(int j=i+1; j < this.uPop.size(); j++){
				distance[i][j] = distance[j][i] = this.uPop.get(i).computeDistance(this.uPop.get(j).getWeightVector());
			}
		}

		return (distance);
	}

	private int [] closestVector (double[] dist, int vector) {
		int i, j, temp;
		int [] closest = new int[this.T];
		int [] index = new int[dist.length];

		for (i=0; i < index.length; i++)  index[i] = i;
		for (i=1; i < index.length; i++) {
			for (j=0; j < index.length-i; j++) {
				if (dist[index[j+1]] < dist[index[j]]) {
					temp = index[j+1];
					index[j+1] = index[j];
					index[j] = temp;
				}
			}
		}

		for (i=0, j=0; i < this.T; j++) {
			if (index[j] != vector) {
				closest[i] = index[j];
				i++;
			}
		}

		return (closest);
	}

	private void computeWeightVectorsNeighbors(){
		double [][] distance;

		distance = this.computeEuclidean();

		for (int i=0; i < this.uPop.size(); i++)
			this.uPop.get(i).setVectorsNeighbors(this.closestVector(distance[i], i));
	}  	

	private double bestObjectiveValue(int numberObj){
		int i=0;
		double bestValue;

		if (numberObj > 0)  bestValue = 1.0;
		else{
			bestValue = this.uPop.get(0).getObjective(numberObj);
			for(i=1; i < this.uPop.size(); i++) {
				if(bestValue < this.uPop.get(i).getObjective(numberObj))  bestValue = this.uPop.get(i).getObjective(numberObj);
			}
		}
		return bestValue;
	}

	private double minObjectiveValue(int numberObj){
		double minValue;

		if (numberObj == 1)  minValue = -1.0;
		else  minValue = 0.0;

		return minValue;
	}

	private void initialize_Z(){
		for(int i=0; i < this.numObjectives; i++){
			this.Z[i] = this.bestObjectiveValue(i);
			this.Z_min[i] = this.minObjectiveValue(i);
		}
	}

	private void initializePopulation() {
		int i, k, pos;
		ArrayList<Integer> tr_not_marked;	  
		Chromosome chromo;

		this.uPop.clear();

		this.setWeightVectorsToEachChromo();
		this.computeWeightVectorsNeighbors();

		//initialize population
		tr_not_marked = new ArrayList<Integer> ();	  

		this.trials = 0;

		for(i=0; i < this.nTrans; i++)  tr_not_marked.add(i);

		k = 0;
		while(k < this.uPop.size()) {
			if(tr_not_marked.size() == 0)
				for(i=0; i<this.nTrans; i++)  tr_not_marked.add(i);

			pos =  tr_not_marked.get(Randomize.Randint(0, tr_not_marked.size()));
			chromo = this.generateChromoCoveredPosNeg(pos);
			chromo.setWeightVector(this.uPop.get(k).getWeightVector());
			chromo.setVectorsNeighbors(this.uPop.get(k).getVectorsNeighbors());

			if((!this.equalChromotoPop(chromo, this.uPop)) && (chromo.getSupport() > this.minSupport) && (!(chromo.getSupport() > (1.0 - this.minSupport))) && (chromo.getCF() > 0)) {
				this.uPop.set(k, chromo.copy());
				this.deleteTransCovered(this.uPop.get(k), tr_not_marked);
				k++;
			}
		}

		this.initialize_Z();
		this.EP = this.nonDominateSelection(this.uPop);
	}

	private Chromosome generateChromoCoveredPosNeg(int pos_example){
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

		//create chromo
		rnd_genes = new Gene[this.nAttr];
		for(i=0; i < this.nAttr; i++)  rnd_genes[i] = new Gene();

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
			rnd_genes[sample[i]].setIsPositiveInterval((Randomize.RandintClosed(0, 1) == 1) ? true : false);

			value = example[sample[i]];
			if  (this.dataset.getAttributeType(sample[i]) != myDataset.NOMINAL) {
				if  (this.dataset.getAttributeType(sample[i]) == myDataset.REAL) {
					if(rnd_genes[sample[i]].getIsPositiveInterval()){
						lb = Math.max(value - (this.dataset.getAmplitude(sample[i]) / (this.af * 4)), this.dataset.getMin(sample[i]));
						ub = Math.min(value + (this.dataset.getAmplitude(sample[i]) / (this.af * 4)), this.dataset.getMax(sample[i]));
					}
					else{ //is negative
						if((value -  this.dataset.getMin(sample[i])) > (this.dataset.getMax(sample[i]) - value)) { // left of the value
							lb = value - ((value -  this.dataset.getMin(sample[i])) - this.dataset.getAmplitude(sample[i]) / (this.af * 4));
							ub = this.dataset.getMin(sample[i]) + ((value -  this.dataset.getMin(sample[i])) - this.dataset.getAmplitude(sample[i]) / (this.af * 4));   						 
						}
						else { // right of the value 
							lb = this.dataset.getMax(sample[i]) - ((this.dataset.getMax(sample[i]) -  value) - this.dataset.getAmplitude(sample[i]) / (this.af * 4));
							ub = value + ((this.dataset.getMax(sample[i]) -  value) - this.dataset.getAmplitude(sample[i]) / (this.af * 4));   						 
						}
					}
				}
				else {
					if(rnd_genes[sample[i]].getIsPositiveInterval()){
						lb = Math.max((int) (value - (this.dataset.getAmplitude(sample[i]) / (this.af * 4))), this.dataset.getMin(sample[i]));
						ub = Math.min((int) (value + (this.dataset.getAmplitude(sample[i]) / (this.af * 4))), this.dataset.getMax(sample[i]));
					}
					else{ //is negative
						if((value -  this.dataset.getMin(sample[i])) > (this.dataset.getMax(sample[i]) - value)) { // left of the value
							lb = (int) value - ((value -  this.dataset.getMin(sample[i])) - this.dataset.getAmplitude(sample[i]) / (this.af * 4));
							ub = (int) this.dataset.getMin(sample[i]) + ((value -  this.dataset.getMin(sample[i])) - this.dataset.getAmplitude(sample[i]) / (this.af * 4));   						 
						}
						else { // right of the value 
							lb = (int) this.dataset.getMax(sample[i]) - ((this.dataset.getMax(sample[i]) -  value) - this.dataset.getAmplitude(sample[i]) / (this.af * 4));
							ub = (int) value + ((this.dataset.getMax(sample[i]) -  value) - this.dataset.getAmplitude(sample[i]) / (this.af * 4));   						 
						}
					}
				}
			}
			else lb = ub = (int) value;

			rnd_genes[sample[i]].setLowerBound(lb);
			rnd_genes[sample[i]].setUpperBound(ub);
			rnd_genes[sample[i]].setType(this.dataset.getAttributeType(sample[i]));
			rnd_genes[sample[i]].setMin_attr(this.dataset.getMin(sample[i]));
			rnd_genes[sample[i]].setMax_attr(this.dataset.getMax(sample[i]));

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
		rnd_genes[sample[i]].setType(this.dataset.getAttributeType(sample[i]));
		rnd_genes[sample[i]].setMin_attr(this.dataset.getMin(sample[i]));
		rnd_genes[sample[i]].setMax_attr(this.dataset.getMax(sample[i]));
		rnd_genes[sample[i]].setIsPositiveInterval((Randomize.RandintClosed(0, 1) == 1) ? true : false);

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
			rnd_genes[sample[i]].setType(this.dataset.getAttributeType(sample[i]));
			rnd_genes[sample[i]].setMin_attr(this.dataset.getMin(sample[i]));
			rnd_genes[sample[i]].setMax_attr(this.dataset.getMax(sample[i]));
			rnd_genes[sample[i]].setIsPositiveInterval((Randomize.RandintClosed(0, 1) == 1) ? true : false);
		}

		chromo = new Chromosome(rnd_genes, this.numObjectives, this.T);				 
		chromo.computeObjetives(this.dataset);
		this.trials++;

		return chromo;
	}

	private void deleteTransCovered (Chromosome chromo, ArrayList<Integer> tr_not_marked){
		int i;
		double [] example;

		for (i = tr_not_marked.size()-1; i >= 0; i--) {
			example = this.dataset.getExample(tr_not_marked.get(i));
			if (chromo.isCovered(example))  tr_not_marked.remove(i); 
		}
	} 

	private ArrayList<Integer> tr_notCovered_NoDominateSolutions(){
		int i;
		ArrayList<Integer> tr_not_marked = new ArrayList<Integer> ();

		for(i=0; i < this.nTrans; i++)  tr_not_marked.add(i);
		for(i=0; i < this.EP.size(); i++)  this.deleteTransCovered(this.EP.get(i), tr_not_marked);

		return tr_not_marked;
	}

	private void restartPop(){
		int i, k, pos, cont;
		ArrayList<Integer> tr_not_marked;
		Chromosome chromo;

		tr_not_marked = this.tr_notCovered_NoDominateSolutions();

		this.uPop.clear();

		this.setWeightVectorsToEachChromo();
		this.computeWeightVectorsNeighbors();

		//initialize population
		k = 0;
		cont = 0;
		while(k < this.uPop.size()) {
			if(tr_not_marked.size() == 0)  
				for(i=0; i < this.nTrans; i++)  tr_not_marked.add(i);

			//create chromo
			pos = tr_not_marked.get(Randomize.Randint(0, tr_not_marked.size()));
			chromo = this.generateChromoCoveredPosNeg(pos);
			chromo.setWeightVector(this.uPop.get(k).getWeightVector());
			chromo.setVectorsNeighbors(this.uPop.get(k).getVectorsNeighbors());

			if((!this.equalChromotoPop(chromo, this.uPop)) && (chromo.getSupport() > this.minSupport) && (!(chromo.getSupport() > (1.0 - this.minSupport))) && (chromo.getCF() > 0)) {
				this.uPop.set(k, chromo.copy());
				this.deleteTransCovered(this.uPop.get(k), tr_not_marked);
				this.update_Z(chromo);
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


	private void update_Z(Chromosome chr){
		int i;

		for(i=0; i < this.numObjectives; i++) {
			if(this.Z[i] < chr.getObjective(i))  this.Z[i] = chr.getObjective(i);
			if(this.Z_min[i] > chr.getObjective(i)) this.Z_min[i] = chr.getObjective(i);
		}
	}

	private void updateSolutions(Chromosome child, int type, int r1){
		int i, c, l, numIndP; 
		int[] perm;

		if(type == this.UPDATE_SOLUTION_NEIGHBOR) numIndP = this.T;
		else numIndP = this.uPop.size();

		perm = random_permutation(numIndP);

		for (i=0, c=0; (c <= this.nr) && (i < numIndP); i++) {
			if(type == this.UPDATE_SOLUTION_NEIGHBOR)  l = this.uPop.get(r1).getVectorsNeighbors(perm[i]);
			else  l = perm[i];

			child.setWeightVector (this.uPop.get(l).getWeightVector());
			child.setVectorsNeighbors (this.uPop.get(l).getVectorsNeighbors());

			if(this.tchebycheffApproach(child) <= this.tchebycheffApproach(this.uPop.get(l))) {
				this.uPop.set(l, child.copy());
				c++;
			}
		}
		if (c > 0)  this.updatePop++;
	}


	private double tchebycheffApproach(Chromosome chr){
		double g_max, g;
		int i;

		g_max = chr.getWeightVector(0) * (Math.abs(this.Z[0] - chr.getObjective(0)) / (this.Z[0] - this.Z_min[0]));

		for(i=1; i < this.numObjectives; i++){
			g = chr.getWeightVector(i) * (Math.abs(this.Z[i] - chr.getObjective(i)) / (this.Z[i] - this.Z_min[i]));
			if(g > g_max)  g_max = g;
		}

		return g_max;
	}

	private boolean equalChromotoPop(Chromosome chromo, ArrayList<Chromosome> pop) {
		int i;
		Chromosome aux;
		boolean value = false;

		for (i=0; (!value) && (i < pop.size()); i++) {
			aux = pop.get(i);
			if(chromo.equals(aux))  value = true;
		}

		return value;
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
		double avg_sup = 0.0, avg_conf = 0.0, avg_ant_length = 0.0, avg_lift = 0.0, avg_conv = 0.0, avg_CF = 0.0, avg_netConf = 0.0, avg_yulesQ = 0.0;
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
			w.println("Average YulesQ: " + roundDouble(( avg_yulesQ/ rules.size() ),2));
			System.out.println("Average YulesQ: " + roundDouble(( avg_yulesQ/ rules.size()),2));
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

		this.paretos += "";
		this.paretos += ("Support\tantecedent_support\tconsequent_support\tConfidence\tLift\tConv\tCF\tNetConf\tYulesQ\tnAttributes\n");

		for (i=0; i < this.EP.size() && !stop; i++) {
			chromo = this.EP.get(i);
			this.paretos += ("" + roundDouble(chromo.getSupport(),2) + "\t" + roundDouble(chromo.getAntsSupport(),2) + "\t" + roundDouble(chromo.getConsSupport(),2) + "\t" + roundDouble(chromo.getConfidence(),2) + "\t" + roundDouble(chromo.getObjective(0),2) + "\t" + roundDouble(chromo.getConv(),2) + "\t" + roundDouble(chromo.getCF(),2) + "\t" + roundDouble(chromo.getNetConf(),2) + "\t" + roundDouble(chromo.getYulesQ(),2) + "\t" + (chromo.getnAnts()+1) + "\n");
		}
	}

	public ArrayList<AssociationRule> generateRulesPareto() {
		int i;
		Chromosome chromo;
		ArrayList<AssociationRule> rulesPareto = new ArrayList<AssociationRule>();

		for (i=0; i < this.EP.size(); i++) {
			chromo = this.EP.get(i);
			rulesPareto.add (new AssociationRule(chromo));
		}
		return rulesPareto;
	}

	public String getParetos() {
		return (this.paretos);
	}
	
	private int check_dominance (Chromosome a, Chromosome b){
		int i;
		int flag1;
		int flag2;
		flag1 = 0;
		flag2 = 0;

		for (i=0; i < this.numObjectives; i++){
			if (a.getObjective(i) > b.getObjective(i))  flag1 = 1;
			else if (a.getObjective(i) < b.getObjective(i))  flag2 = 1;
		}

		if ((flag1 == 1) && (flag2 == 0))  return (1);
		else if ((flag1 == 0) && (flag2 == 1))  return (-1);
		else  return (0);
	}

}
