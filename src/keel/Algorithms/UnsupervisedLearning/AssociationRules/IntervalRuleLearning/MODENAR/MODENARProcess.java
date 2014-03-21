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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.MODENAR;

/**
 * <p>
 * @author Written by Diana Martín (dmartin@ceis.cujae.edu.cu) 
 * @version 1.1
 * @since JDK1.6
 * </p>
 */

import java.util.*;
import java.io.PrintWriter;
import java.lang.Math;
import java.math.BigDecimal;

import org.core.*;



public class MODENARProcess {
	myDataset ds;
	private String paretos;
	private ArrayList<Chromosome> bestRules;
	private ArrayList<AssociationRule> assoc_rules_Pareto;
	//parameters 
	int nGeneration;
	private int nEvaluations;
	private int nRealEvaluations;
	int popSize;
	double CR;//crossover rate
	int threshold;
	double[]Wk;// weight for each fitness
	private double allow_ampl[];
	int max_rank;

	public MODENARProcess() {
		// TODO Auto-generated constructor stub
	}

	public MODENARProcess(myDataset ds, int nEvaluations, int popSize, double cr, int threshold, double[] wk, double AF) {
		this.ds = ds;
		this.popSize = popSize;
		this.CR = cr;
		this.threshold = threshold;
		this.Wk = wk;
		this.nEvaluations = nEvaluations;
		this.paretos = new String("");
		this.allow_ampl = new double[this.ds.getnVars()];

		for (int i=0; i < this.allow_ampl.length; i++)  this.allow_ampl[i] = (this.ds.getMax(i) - this.ds.getMin(i)) / (double) AF;
	}



	public void run()
	{
		//main fuction algorithm
		int nGen,count;
		Chromosome chromo_mutate, chromo_trial,chromo_select, parentChromo;
		ArrayList<Chromosome> pop_new = new ArrayList<Chromosome>();
		ArrayList<Chromosome> mutate_parentChromo;
		this.paretos = new String("");

		this.nRealEvaluations = 0;
		nGen = 0;

		System.out.println("Initialization");
		ArrayList<Chromosome> pop = this.initialize();

		while(!terminate()){
			System.out.println("Generation: " + nGen);
			count = 0;
			pop = this.remove_dominate_solut(pop);
			if(pop.size()>this.threshold){
				pop = this.filtrating(pop);
			}
			pop_new = new ArrayList<Chromosome>();
			while(pop_new.size()<popSize){
				mutate_parentChromo = this.mutate(pop);
				parentChromo = mutate_parentChromo.get(0).copy();
				chromo_mutate = mutate_parentChromo.get(1).copy();
				chromo_mutate = this.reparing(chromo_mutate).copy();
				chromo_trial = new Chromosome();
				chromo_trial = this.crossover(chromo_mutate.copy(), parentChromo.copy()).copy();
				chromo_select = this.select(chromo_trial, parentChromo).copy();
				if(pop_new.size()!=0){
					if(!equalChromotoPop(chromo_select, pop_new)) pop_new.add(chromo_select.copy());
					else count++;
					if(count> 15){
						chromo_select = chromo_trial.copy();
						if(!equalChromotoPop(chromo_select, pop_new)) pop_new.add(chromo_select.copy());
					}
				}
				else pop_new.add(chromo_select.copy());
			}
			pop = AdjustIntervalPop(pop_new);
			nGen++;
		}

		this.bestRules = pop;
		this.assign_rank(this.bestRules);
		this.genRules();
		this.printPareto(this.assoc_rules_Pareto);
	}

	public boolean terminate()
	{
		//stop condition for the algorithm
		if(this.nRealEvaluations >= this.nEvaluations) return true;
		return false;
	}

	private ArrayList<Chromosome> initialize()
	{
		ArrayList<Chromosome> popInit = new ArrayList<Chromosome>();
		Chromosome c;
		int count = 0;
		while ((popInit.size() < this.popSize) && (count <100)){
			c = this.generateRandomChromo().copy();
			if(popInit.size()!=0){
				if((!equalChromotoPop(c, popInit))&& (c.getFitness()[0]> 0.0)) {
					popInit.add(c);
					count = 0;
				}
				else count++;
			}
			else
				if(c.getFitness()[0]> 0.0){
					popInit.add(c);
					count = 0;
				}
				else count++;
		}
		while (popInit.size() < this.popSize) {
			c = this.generateChromoWithSupport().copy();
			popInit.add(c);
		}
		return popInit;
	}


	private Chromosome generateRandomChromo()
	{
		int nVars, attr;
		double lb, ub, max_attr, min_attr;
		nVars = this.ds.getnVars();
		ArrayList<Gene> genes = new ArrayList<Gene>();
		for(int g=0;g<nVars;g++){
			Gene gen = new Gene();
			attr = g;  

			gen.setAttr(attr);
			gen.setType(this.ds.getAttributeType(attr));
			gen.setCa(Randomize.RandintClosed(0,2));

			max_attr = this.ds.getMax(attr);
			min_attr = this.ds.getMin(attr);
			if ( gen.getType() != Gene.NOMINAL ) {
				if ( gen.getType() == Gene.REAL ) {
					lb = Randomize.RanddoubleClosed(min_attr, max_attr);
					ub = Randomize.RanddoubleClosed(min_attr, max_attr);
				}
				//type is INTENGER
				else {
					lb = Randomize.RandintClosed((int) min_attr, (int) max_attr);
					ub = Randomize.RandintClosed((int) min_attr, (int) max_attr);
				}
			}
			else 
				lb = ub = Randomize.RandintClosed((int) min_attr, (int) max_attr);
			if(lb <= ub){
				gen.setL(lb);
				gen.setU(ub);	
			}
			else{
				gen.setL(ub);
				gen.setU(lb);
			}
			genes.add(gen.copy());
		}
		//ensure that it has at least one antecedent and consequent
		int set_antec = Randomize.Randint(0, genes.size());
		int set_cons = Randomize.Randint(0, genes.size());
		while(set_antec == set_cons)  set_cons = Randomize.Randint(0,genes.size());

		genes.get(set_antec).setCa(0);
		genes.get(set_cons).setCa(1);

		Chromosome c = new Chromosome(genes);
		c.setFitness(this.evaluate_chromosome(c));
		return c;
	}

	private Chromosome generateChromoWithSupport()
	{
		int nVars, attr,tr;
		double lb, ub, max_attr, min_attr;
		nVars = this.ds.getnVars();
		ArrayList<Gene> genes = new ArrayList<Gene>();
		double[][] trans = this.ds.getRealTransactions();
		tr = Randomize.Randint(0, this.ds.getnTrans());

		for (int g=0; g < nVars; g++) {
			Gene gen = new Gene();

			attr = g;  

			gen.setAttr(attr);
			gen.setType( this.ds.getAttributeType(attr));
			gen.setCa(Randomize.RandintClosed(0,2));

			max_attr = this.ds.getMax(attr);
			min_attr = this.ds.getMin(attr);

			if ( gen.getType() != Gene.NOMINAL ) {
				if ( gen.getType() == Gene.REAL ) {
					lb = Math.max(trans[tr][attr] - (this.allow_ampl[attr] / 2.0), min_attr);
					ub = Math.min(trans[tr][attr] + (this.allow_ampl[attr] / 2.0), max_attr);
				}
				else {
					lb = Math.max(trans[tr][attr] - ((int) this.allow_ampl[attr] / 2), min_attr);
					ub = Math.min(trans[tr][attr] + ((int) this.allow_ampl[attr] / 2), max_attr);
				}
			}
			else lb = ub = trans[tr][attr];

			gen.setL(lb);
			gen.setU(ub);
			genes.add(gen.copy());
		}

		int set_antec = Randomize.Randint(0,genes.size()-1);
		genes.get(set_antec).setCa(0);
		int set_cons = Randomize.Randint(0,genes.size()-1);
		while(set_antec == set_cons)
			set_cons = Randomize.Randint(0,genes.size()-1);
		genes.get(set_cons).setCa(1);
		Chromosome c = new Chromosome(genes);
		c.setFitness(this.evaluate_chromosome(c));
		return c;
	}
	
	private Chromosome select(Chromosome trialChromo, Chromosome parentChromo)
	{
		//select between these chromosome: if one dominate the other one or not, 
		//if they do not dominate each other then select the solution 
		//calculating the sum of the weights
		Chromosome dominChromo = new Chromosome();
		int dominanc = Dominance(trialChromo,parentChromo);
		if(dominanc!= 0)
			if(dominanc == 1)
				dominChromo = trialChromo.copy() ;
			else
				dominChromo = parentChromo.copy();
		else
			dominChromo = SelectByWeightedFitness(trialChromo,parentChromo); 

		return dominChromo;
	}

	private Chromosome crossover(Chromosome mutateChromo, Chromosome parentChromo)
	{
		//cross mutate and parent chromosome to get trial chromosome 
		int nVars, count;
		nVars = this.ds.getnVars();
		ArrayList<Gene> genes;
		Chromosome trialChromo = new Chromosome();
		boolean trialchromo_ok = false;
		count = 0;
		while (!trialchromo_ok){
			genes = new ArrayList<Gene>();
			for(int j=0;j < nVars; j++){
				Gene gen = new Gene();
				if((Randomize.Rand() < this.CR)||(j == Randomize.Randint(1,nVars)))
					gen = mutateChromo.getGenes().get(j).copy();
				else
					gen = parentChromo.getGenes().get(j).copy();
				genes.add(gen);
			}
			trialChromo = new Chromosome(genes);
			int set_antec = Randomize.Randint(0,genes.size()-1);
			genes.get(set_antec).setCa(0);
			int set_cons = Randomize.Randint(0,genes.size()-1);
			while(set_antec == set_cons)
				set_cons = Randomize.Randint(0,genes.size()-1);
			genes.get(set_cons).setCa(1);
			trialChromo.setFitness(this.evaluate_chromosome(trialChromo));
			if(trialChromo.getFitness()[0]> 0.0){
				trialchromo_ok = true;
			}
			else count++;
			if(count > 20){
				trialchromo_ok = true;
				trialChromo = generateChromoWithSupport();
			}
		}

		return trialChromo;
	}

	private ArrayList<Chromosome> mutate(ArrayList<Chromosome>pop)
	{
		//return a mutate solution, this method uses three randomly solution selected
		boolean allowmutate, allowmutateXb,allowmutateXc, mutatechromo_ok;
		Chromosome Xi, Xa, Xb, Xc;
		ArrayList<Chromosome> parent_mutate = new ArrayList<Chromosome>();;
		int pos_i,pos_a,pos_b,pos_c, count;
		mutatechromo_ok = false;
		count=0;
		while(!mutatechromo_ok){

			allowmutate = false;
			allowmutateXb = false;
			allowmutateXc = false;
			Xi = new Chromosome();
			Xa = new Chromosome();
			Xb = new Chromosome();
			Xc = new Chromosome();

			if(pop.size()<4){
				if(pop.size()==3){
					while(!allowmutate){
						pos_i= Randomize.Randint(0,pop.size());
						pos_a= Randomize.Randint(0,pop.size());
						if(pos_i != pos_a){
							Xi = pop.get(pos_i).copy();
							Xa = pop.get(pos_a).copy();
							allowmutate = true;
							while(!allowmutateXb){
								pos_b= Randomize.Randint(0,pop.size());
								if((pos_i != pos_b) && (pos_a!= pos_b)){
									allowmutateXb = true;
									Xb = pop.get(pos_b).copy();
									while(!allowmutateXc){
										Xc = generateChromoWithSupport().copy();
										if((!Xi.equals(Xc)) && (!Xa.equals(Xc))&&(!Xb.equals(Xc))){
											allowmutateXc = true;
										}
									}
								}
							}
						}
					}
				}
				if(pop.size()==2){
					while(!allowmutate){
						pos_i= Randomize.Randint(0,pop.size());
						pos_a= Randomize.Randint(0,pop.size());
						if(pos_i != pos_a){
							Xi = pop.get(pos_i).copy();
							Xa = pop.get(pos_a).copy();
							allowmutate = true;
							while(!allowmutateXb){
								Xb = generateChromoWithSupport().copy();
								if((!Xi.equals(Xb)) && (!Xa.equals(Xb))){
									allowmutateXb = true;
									while(!allowmutateXc){
										Xc = generateChromoWithSupport().copy();
										if((!Xi.equals(Xc)) && (!Xa.equals(Xc))&&(!Xb.equals(Xc))){
											allowmutateXc = true;
										}
									}
								}
							}
						}
					}
				}
				if(pop.size()==1){
					while(!allowmutate){
						Xi = pop.get(0).copy();
						Xa = generateChromoWithSupport().copy();
						if(!Xi.equals(Xa)){
							allowmutate = true;
							while(!allowmutateXb){
								Xb = generateChromoWithSupport().copy();
								if((!Xi.equals(Xb)) && (!Xa.equals(Xb))){
									allowmutateXb = true;
									while(!allowmutateXc){
										Xc = generateChromoWithSupport().copy();
										if((!Xi.equals(Xc)) && (!Xa.equals(Xc))&&(!Xb.equals(Xc))){
											allowmutateXc = true;
										}
									}
								}
							}
						}
					}
				}
			}

			else{
				while(!allowmutate){
					pos_i= Randomize.Randint(0,pop.size());
					pos_a= Randomize.Randint(0,pop.size());
					if(pos_i != pos_a){
						Xi = pop.get(pos_i).copy();
						Xa = pop.get(pos_a).copy();
						allowmutate = true;
						while(!allowmutateXb){
							pos_b= Randomize.Randint(0,pop.size());
							if((pos_i != pos_b) && (pos_a!= pos_b)){
								allowmutateXb = true;
								Xb = pop.get(pos_b).copy();
								while(!allowmutateXc){
									pos_c= Randomize.Randint(0,pop.size()); 
									if((pos_i != pos_c) && (pos_a!= pos_c) && (pos_b!= pos_c)){
										allowmutateXc = true;
										Xc = pop.get(pos_c).copy();
									}
								}
							}
						}
					}
				}

			}
			Chromosome mutateXi = generateMutanteRandDE(Xi,Xa,Xb,Xc);
			if(mutateXi.getFitness()[0]>0.0){
				parent_mutate.add(Xi.copy());
				parent_mutate.add(mutateXi.copy());
				mutatechromo_ok = true;
				count = 0;
			}
			else{
				count++;
			}
			if(count > 15){
				count = 0;
				Xi = new Chromosome();
				boolean allowmutateXi = false;
				while(!allowmutateXi){
					Xi = generateChromoWithSupport().copy();
					if((!Xc.equals(Xi)) && (!Xa.equals(Xi))&&(!Xb.equals(Xi))){
						allowmutateXi = true;
					}
				}
				mutateXi = generateMutanteRandDE(Xi,Xa,Xb,Xc);
				parent_mutate.add(Xi.copy());
				parent_mutate.add(mutateXi.copy());
				mutatechromo_ok = true;
			}
		}
		return parent_mutate;
	}

	private Chromosome reparing(Chromosome chromo)
	{
       /*solves the problem of the interval values of the attributes that are created
		at random after initialize the population or the mutation*/
		double aux,L,U;
		for(int i=0;i<chromo.getGenes().size();i++){
			Gene gen = chromo.getGenes().get(i);
			if(gen.getL()> gen.getU()){
				aux = gen.getL();
				gen.setL(gen.getU());
				gen.setU(aux);
			}
			if(gen.getL() < ds.getMin(i)){
				L= (gen.getL() + ds.getMin(i))/2;
				gen.setL(L);
			}
			if(gen.getU() > ds.getMax(i)){
				U= (gen.getU() + ds.getMax(i))/2;
				gen.setU(U);
			}
			chromo.getGenes().set(i, gen);
		}
		return chromo;
	}

	private ArrayList<Chromosome> filtrating(ArrayList<Chromosome>pop)
	{
		//it used if the number of non-dominated solutions is greater than a certain threshold

		ArrayList<Double> distEucAveList = new ArrayList<Double>();
		ArrayList<Integer> posChromo = new ArrayList<Integer>();
		ArrayList<Chromosome> chromo_List = new ArrayList<Chromosome>();
		double minDist_i,minDist_j,distEuclid_x = 0,distEuclidAve_x = 0 ;
		for(int i=0; i< pop.size();i++){
			Chromosome chromo_x = pop.get(i);
			minDist_i = DistanceEuclidea(pop.get(i),pop.get(1));
			minDist_j = DistanceEuclidea(pop.get(i),pop.get(2));
			for(int j=i+1;j < pop.size();j++){
				Chromosome chromo_j = pop.get(j);
				distEuclid_x = DistanceEuclidea(chromo_x, chromo_j);
				if(distEuclid_x < minDist_i)
					minDist_i = distEuclid_x;
				else if(distEuclid_x < minDist_j)
					minDist_j = distEuclid_x;
			}
			distEuclidAve_x = (minDist_i + minDist_j)/2;
			boolean insert = false;
			int k = 0;
			if(distEucAveList.size()==0)
			{
				distEucAveList.add(0,distEuclidAve_x);
				insert=true;
			}
			while((!insert) && (k < distEucAveList.size())){
				if(distEuclidAve_x < distEucAveList.get(k)){
					distEucAveList.add(k,distEuclidAve_x);
					chromo_List.add(k,chromo_x);
					posChromo.add(k, i);
					insert = true;
				}
				else 
				{
					distEucAveList.add(distEuclidAve_x);
					posChromo.add(i);
					chromo_List.add(chromo_x);
					insert = true;
				}
				k++;
			}
		}
		//remove chromosome until popsize = threshold
		int l=0;
		while(pop.size()> this.threshold){
			//int posdelete = posChromo.get(l);
			pop.remove(chromo_List.get(l));
			//pop.remove(posdelete);
			l++;
		}
		return pop;
	}

	
	private double[] evaluate_chromosome(Chromosome chromo) {
		double[] fitness = new double[4];
		double comprehensibility = 0, amplitudeInterv = 0;

		AssociationRule rule = getRule(chromo);
		fitness[0]= rule.getAll_support();
		fitness[1]= rule.getConfidence();
		comprehensibility = Math.log10(1 + rule.getAntecedent().size())/ Math.log10(1 + rule.getAntecedent().size() + rule.getConsequent().size());
		fitness[2]= comprehensibility;
		int countAttbRule =  rule.getAntecedent().size() + rule.getConsequent().size();
		double sumInterv = 0;
		for(int i = 0;i<chromo.getGenes().size();i++){
			Gene gen = chromo.getGenes().get(i);
			if(gen.getCa()!= 2){
				if(ds.getMax(i)- ds.getMin(i) != 0)
					sumInterv = sumInterv + ((gen.getU()-gen.getL())/(ds.getMax(i)- ds.getMin(i)));
			}
		}
		double div_countAttbRule= (1.0/countAttbRule);
		amplitudeInterv = Math.abs(1-(div_countAttbRule*(sumInterv)));
		fitness[3]= amplitudeInterv;
		chromo.setRuleSupport(rule.getAll_support());
		chromo.setRuleConfidence(rule.getConfidence());
	
		this.nRealEvaluations++;
		return fitness;
	}
	private ArrayList<Chromosome> remove_dominate_solut(ArrayList<Chromosome>pop)
	{
		for(int j=0; j< pop.size(); j++){
			for(int k= j+1;k< pop.size();k++){
				if(Dominance(pop.get(j), pop.get(k))== 1)// j domina k
					pop.remove(k);
				else if(Dominance(pop.get(j), pop.get(k))== -1){
					pop.remove(j);
				}
			}
		}
		return pop;
	}

	/*   1 if a dominates b
	    -1 if b dominates a
	     0 if both a and b are non-dominated */
	private int Dominance(Chromosome chromo_a, Chromosome chromo_b) {
		int flag1  = 0, flag2 = 0;

		for (int i=0; i<chromo_a.getFitness().length; i++) {
			if (chromo_a.getFitness()[i] > chromo_b.getFitness()[i])  flag1 = 1;
			else if (chromo_a.getFitness()[i] < chromo_b.getFitness()[i])  flag2 = 1;
		}

		if ((flag1 == 1) && (flag2 == 0))  return (1);
		else if (flag1 == 0 && flag2 == 1)  return (-1);
		else  return (0);
	}

	private Chromosome SelectByWeightedFitness(Chromosome chromo1, Chromosome chromo2)
	{
		//it implements the function for weighted sum fitness in order to decide the chr that will be selected
		//if none dominates another
		if(calculate_WeightedFitness(chromo1)> calculate_WeightedFitness(chromo2))
			return chromo1;
		else
			return chromo2;
	}
	private double calculate_WeightedFitness(Chromosome chromo)
	{   
		double weightChromo = 0;
		for(int i=0; i< chromo.getFitness().length; i++){
			weightChromo = weightChromo + Wk[i] * chromo.getFitness()[i];
		}
		return weightChromo;
	}
	private ArrayList<Chromosome> AdjustIntervalPop(ArrayList<Chromosome> pop)
	{

		for(int i=0; i< pop.size();i++){
			pop.set(i, AdjustIntervalChromo(pop.get(i)));
		}
		return pop;
	}

	private Chromosome AdjustIntervalChromo(Chromosome chromo)
	{
		ArrayList<Integer> tid_list = new ArrayList<Integer>();
		double[][] examples = ds.getRealTransactions();
		int attr;
		double minlb, maxub; 

		tid_list = this.countSupport(chromo.getGenes());
		if(tid_list.size()!=0)
		{
			for (int g=0; g < chromo.getGenes().size(); g++)
			{
				attr = chromo.getGenes().get(g).getAttr();
				minlb = examples[tid_list.get(0)][attr];
				maxub = examples[tid_list.get(0)][attr];
				for (int t=1; t < tid_list.size(); t++) {
					if (examples[tid_list.get(t)][attr] < minlb)
						minlb = examples[t][attr]; 
					if(examples[t][attr] > maxub){
						maxub = examples[t][attr]; 
					}
				}
				chromo.getGenes().get(g).setL(minlb);
				chromo.getGenes().get(g).setU(maxub);
			}
		}

		return chromo;
	}
	private Chromosome generateMutanteRandDE(Chromosome Xi, Chromosome Xa,Chromosome Xb, Chromosome Xc)
	{
		// Xi = Xa + F(Xb-Xc)
		double genL=0,genU=0;
		int count;
		boolean allowed_gen;
		Chromosome mutateChromo = new Chromosome();
		mutateChromo = Xi.copy();
		for(int i = 0; i< ds.getnVars(); i++){
			Gene gen = mutateChromo.getGenes().get(i);
			allowed_gen = false;
			count = 0;
			while(!allowed_gen){
				if ( gen.getType() != Gene.NOMINAL ) {
					if(gen.getType()== Gene.REAL){
						genL = Math.abs(Xa.getGenes().get(i).getL() + Randomize.RandGaussian()*(Xb.getGenes().get(i).getL()- Xc.getGenes().get(i).getL()));
						genU =Math.abs(Xa.getGenes().get(i).getU() + Randomize.RandGaussian()*(Xb.getGenes().get(i).getU()- Xc.getGenes().get(i).getU()));

					}
					if(gen.getType()== Gene.INTEGER){
						genL = Math.abs(Xa.getGenes().get(i).getL() + Math.round(Randomize.RandGaussian()*(Xb.getGenes().get(i).getL()- Xc.getGenes().get(i).getL())));
						genU = Math.abs(Xa.getGenes().get(i).getU() + Math.round(Randomize.RandGaussian()*(Xb.getGenes().get(i).getU()- Xc.getGenes().get(i).getU())));
					}
				}
				else 
					genL = genU = Randomize.RandintClosed((int)this.ds.getMin(Xa.getGenes().get(i).getAttr()), (int)this.ds.getMax(Xa.getGenes().get(i).getAttr()));
				if(genL > genU){
					double aux = genL;
					genL = genU;
					genU = aux;
				}
				if(genL >= this.ds.getMin(i)&&(genU <= this.ds.getMax(i)))allowed_gen = true;
				else count ++;
				if(count > 15){
					allowed_gen = true;
					if(gen.getL() < ds.getMin(i)){
						genL= (gen.getL() + ds.getMin(i))/2;

					}
					if(gen.getU() > ds.getMax(i)){
						genU= (gen.getU() + ds.getMax(i))/2;

					}
				}

				mutateChromo.getGenes().get(i).setL(genL);
				mutateChromo.getGenes().get(i).setU(genU);
			}
		}
		/*for(int i=0;i< mutateChromo.getGenes().size();i++){
			 mutateChromo.getGenes().get(i).setCa(Randomize.RandintClosed(0,2));
		}*/
		mutateChromo.setFitness(evaluate_chromosome(mutateChromo));
		return mutateChromo;
	}

	/**
	 * @param chromo
	 * @return
	 */
	private AssociationRule getRule(Chromosome chromo)
	{
		int j;
		double all_sup, ant_sup, cons_sup, confidance, nData;  
		ArrayList<Integer> tid_lst_all, tid_lst_ant, tid_lst_cons;
		AssociationRule rule;
		ArrayList<Gene> genes_ant;
		ArrayList<Gene> genes_cons;
		ArrayList<Gene> genes_all;

		nData=(double)this.ds.getnTrans();
		rule = new AssociationRule();
		genes_ant = new ArrayList<Gene>();
		genes_cons = new ArrayList<Gene>();
		genes_all = new ArrayList<Gene>();

		for (j=0; j < this.ds.getnVars(); j++) {
			if(chromo.getGenes().get(j).getCa()== 0){
				rule.addAntecedent(chromo.getGenes().get(j).copy()); 
				genes_ant.add(chromo.getGenes().get(j));
				genes_all.add(chromo.getGenes().get(j));
			}
			if(chromo.getGenes().get(j).getCa()== 1){
				rule.addConsequent(chromo.getGenes().get(j).copy()); 
				genes_cons.add(chromo.getGenes().get(j));
				genes_all.add(chromo.getGenes().get(j));
			}
		}

		tid_lst_all = this.countSupport(genes_all);
		all_sup = (double) tid_lst_all.size() / nData;

		tid_lst_ant = this.countSupport(genes_ant);
		ant_sup = (double) tid_lst_ant.size() / nData;

		tid_lst_cons = this.countSupport(genes_cons);
		cons_sup = (double) tid_lst_cons.size() / nData;

		if ((ant_sup == 0.0) || (all_sup == 0.0))  confidance = 0.0;
		else  confidance = all_sup / ant_sup;

		rule.setSupport(ant_sup);
		rule.setAll_support(all_sup);
		rule.setSupport_consq(cons_sup);
		rule.setConfidence (confidance);
		return rule;
	}

	private int numCoveredRecords () {
		int i, j, covered, nTrans;
		ArrayList<Integer> tidCovered;
		ArrayList<Gene> genes_all;
		Chromosome chromo;


		nTrans = this.ds.getnTrans();
		boolean [] marked = new boolean[nTrans];
		for (i=0; i < nTrans; i++)  marked[i] = false;
		
		for (i=0; i < this.bestRules.size(); i++) {
			chromo = this.bestRules.get(i);
			genes_all = new ArrayList<Gene>();

			for (int k=0; k < this.ds.getnVars(); k++) {
				if(chromo.getGenes().get(k).getCa()!= 2){
					genes_all.add(chromo.getGenes().get(k));
				}
			}
			if(chromo.rank < 2){
				tidCovered = this.countSupport(genes_all);
				for (j=0; j < tidCovered.size(); j++)  marked[tidCovered.get(j)] = true;
			}


		}

		covered = 0;
		for (i=0; i < nTrans; i++)
			if (marked[i])  covered++;

		return covered;
	}
	private void genRules() {
		int i, j;
		double yulesQ, numeratorYules, denominatorYules, all_sup, ant_sup, cons_sup, lift, confidance, nData, conv, CF, netConf; 
		ArrayList<Integer> tid_lst_all, tid_lst_ant, tid_lst_cons;
		AssociationRule rule;
		Chromosome chromo;
		ArrayList<Gene> genes_ant;
		ArrayList<Gene> genes_cons;
		ArrayList<Gene> genes_all;

		nData=(double)this.ds.getnTrans();
		this.assoc_rules_Pareto = new ArrayList<AssociationRule>();

		for (i=0; i < this.bestRules.size(); i++) {

			chromo = bestRules.get(i);
			if(chromo.rank < 2) {
				rule = new AssociationRule();
				genes_ant = new ArrayList<Gene>();
				genes_cons = new ArrayList<Gene>();
				genes_all = new ArrayList<Gene>();

				for (j=0; j < this.ds.getnVars(); j++) {
					if(chromo.getGenes().get(j).getCa()== 0){
						rule.addAntecedent(chromo.getGenes().get(j).copy()); 
						genes_ant.add(chromo.getGenes().get(j));
						genes_all.add(chromo.getGenes().get(j));
					}
					if(chromo.getGenes().get(j).getCa()== 1){
						rule.addConsequent(chromo.getGenes().get(j).copy()); 
						genes_cons.add(chromo.getGenes().get(j));
						genes_all.add(chromo.getGenes().get(j));
					}
				}

				tid_lst_all = this.countSupport(genes_all);
				all_sup = (double) tid_lst_all.size() / nData;

				tid_lst_cons = this.countSupport(genes_cons);
				cons_sup = (double) tid_lst_cons.size() / nData;

				tid_lst_ant = this.countSupport(genes_ant);
				ant_sup = (double) tid_lst_ant.size() / nData;

				rule.setSupport(ant_sup);
				rule.setSupport_consq(cons_sup);
				rule.setAll_support(all_sup);

				if(all_sup != 0)
					confidance = all_sup / ant_sup;
				else confidance = 0;

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
				if(confidance > cons_sup)
					CF = (confidance - cons_sup)/(1-cons_sup);	
				else 
					if(confidance < cons_sup)
						CF = (confidance - cons_sup)/(cons_sup);	

				rule.setConfidence (confidance);
				rule.setLift(lift);
				rule.setConv(conv);
				rule.setCF(CF);
				rule.setNetConf(netConf);
				rule.setYulesQ(yulesQ);
				rule.setComprehensibility(chromo.getFitness()[2]);
				rule.setAmplitudeInterv(chromo.getFitness()[3]);
				this.assoc_rules_Pareto.add(rule);
			}
		}
	}

	private ArrayList<Integer> countSupport(ArrayList<Gene> genes) {

		ArrayList<Integer> tid_list = new ArrayList<Integer>();
		double[][] trans = this.ds.getRealTransactions();
		int attr, nTrans;
		double lb, ub;
		boolean ok;

		nTrans = this.ds.getnTrans();

		for (int t=0; t < nTrans; t++) {
			ok = true;

			for (int g=0; g < genes.size() && ok; g++) {
				attr = genes.get(g).getAttr();
				lb = genes.get(g).getL();
				ub = genes.get(g).getU();

				if ((trans[t][attr] < lb) || (trans[t][attr] > ub))   ok = false;
			}

			if (ok) tid_list.add(t);
		}

		return tid_list;
	}

	private double DistanceEuclidea(Chromosome chromo_a,Chromosome chromo_b)
	{
		double DistEuclidea,lb_a,ub_a,lb_b,ub_b;
		double suma_lb_ub = 0,suma = 0, resta_lb,resta_ub;
		for(int i=0; i< chromo_a.getGenes().size();i++){
			lb_a = chromo_a.getGenes().get(i).getL();
			ub_a = chromo_a.getGenes().get(i).getU();
			lb_b = chromo_b.getGenes().get(i).getL();
			ub_b = chromo_b.getGenes().get(i).getU();
			resta_lb = lb_a - lb_b;
			resta_ub = ub_a - ub_b;
			suma_lb_ub = resta_lb + resta_ub;
			suma = suma + Math.pow(suma_lb_ub,2);//aplicando la sumatoria del cuadrado

		}
		DistEuclidea = Math.sqrt(suma);
		return DistEuclidea;
	}

	private boolean equalChromotoPop(Chromosome chromo_a, ArrayList<Chromosome> pop){
		boolean equal_chr = false;
		int i=0;
		while((!equal_chr)&&(i<pop.size())){
			if(chromo_a.equals(pop.get(i)))
				equal_chr = true;
			i++;
		}
		return equal_chr;

	}

	public ArrayList<Chromosome> getBestrules() {
		return bestRules;
	}

	public void setBestrules(ArrayList<Chromosome> bestrules) {
		this.bestRules = bestrules;
	}

	public double getCR() {
		return CR;
	}

	public void setCR(double cr) {
		CR = cr;
	}

	public myDataset getDs() {
		return ds;
	}

	public void setDs(myDataset ds) {
		this.ds = ds;
	}

	public int getPopSize() {
		return popSize;
	}

	public void setPopSize(int popSize) {
		this.popSize = popSize;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public double[] getWk() {
		return Wk;
	}

	public void setWk(double[] wk) {
		this.Wk = wk;
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

	public void saveReport (ArrayList<AssociationRule> rules, PrintWriter w ) {
		int i, countRules, length;
		AssociationRule rule;
		double avg_yulesQ, avg_sup, avg_conf, avg_lift,avg_conv, avg_CF, avg_netConf;

		countRules = length = 0;
		avg_yulesQ = avg_sup = avg_conf = avg_lift =  avg_conv = avg_CF = avg_netConf = 0.0;

		for (i=0; i < rules.size(); i++) {
			rule = rules.get(i); 
			countRules++;
			length += (rule.getLengthAntecedent()+ rule.getLengthConsequent());
			avg_sup += rule.getAll_support();
			avg_conf += rule.getConfidence();
			avg_lift += rule.getLift();
			avg_conv += rule.getConv();
			avg_CF += rule.getCF();
			avg_netConf += rule.getNetConf();
			avg_yulesQ += rule.getYulesQ();

		}
		w.println("\nNumber of Frequent Itemsets found: " + "-");	
		System.out.println("\nNumber of Frequent Itemsets found: " + "-");
		w.println("\nNumber of Association Rules generated: " + countRules);	
		System.out.println("Number of Association Rules generated: " + countRules);

		if (countRules!=0 ){
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
			System.out.println("Average yulesQ: " + roundDouble(( avg_yulesQ/ countRules), 2));
			w.println("Average Number of Antecedents: " + roundDouble(( length / (double) countRules ),2));
			System.out.println("Average Number of Antecedents: " + roundDouble(( length / (double) countRules ),2));
			w.println("Number of Covered Records (%): " + roundDouble((100.0 * this.numCoveredRecords ()) / this.ds.getnTrans(), 2));
			System.out.println("Number of Covered Records (%): " + roundDouble(( 100.0 * this.numCoveredRecords ()) / this.ds.getnTrans(),2)); 
		}

		else System.out.println("No Statistics.");

	}  

	public void printPareto( ArrayList<AssociationRule> rulesPareto) {
		int i;
		AssociationRule rule;

		this.paretos += ("Support\tantecedent_support\tconsequent_support\tConfidence\tLift\tConv\tCF\tNetConf\tYulesQ\tnAttributes\n");
		for (i=0; i < this.assoc_rules_Pareto.size(); i++) {
			rule = this.assoc_rules_Pareto.get(i);
			this.paretos += ("" + roundDouble(rule.getAll_support(),2) + "\t" + roundDouble(rule.getSupport(),2) + "\t" + roundDouble(rule.getSupport_consq(),2) + "\t" + roundDouble(rule.getConfidence(),2) + "\t" + roundDouble(rule.getLift(),2) + "\t" + roundDouble(rule.getConv(),2) + "\t" + roundDouble(rule.getCF(),2) + "\t" + roundDouble(rule.getNetConf(),2) + "\t" + roundDouble(rule.getYulesQ(),2) + "\t" + (rule.getLengthAntecedent()+ rule.getLengthConsequent()) + "\n");

		}
	}

	public String getParetos() {
		return (this.paretos);
	}

	public ArrayList<AssociationRule> getAssoc_rules_Pareto() {
		return assoc_rules_Pareto;
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

		//System.err.println ("Tamaña de new_pop = " + new_pop.size() + "   Tamaño que deberia tener = " + this.uPopSize);

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
					flag = Dominance(new_pop.get(temp1.index), new_pop.get(temp2.index));
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


}
