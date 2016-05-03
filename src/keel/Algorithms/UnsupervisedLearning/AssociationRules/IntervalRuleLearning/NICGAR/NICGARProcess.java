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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.NICGAR;

/**
 * <p>
 * @author Written by Diana Martín (dmartin@ceis.cujae.edu.cu)
 * @version 1.1
 * @since JDK1.7
 * </p>
 */

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;

import org.core.Randomize;

public class NICGARProcess {
	/**
	 * <p>
	 * It provides the implementation of the algorithm to be run in a process
	 * </p>
	 */


	private String rulesEP;
	private static myDataset dataset;
	private int nTrials;
	private double minSupport;
	private double pm;
	private double af;   
	private int uPopSize;
	private int nAttr;
	private int nTrans;
	private int trials;
	private double nichMin;
	private double evMin;
	private double sumFitnessPop;
	private double sumFitnessEP;
	private double updatePop;
	private double percentUpdate;
	private double countReinc;
	private int trialsFirstRestart;
	private double[] wTrans;
	private double avgDistPop;
	private boolean coverallEP;

	private ArrayList<Chromosome> uPop;
	private ArrayList<Chromosome> child_pop;
	private ArrayList<Chromosome> EP;
	private ArrayList<Specie> speciesList;


	/**
	 * <p>
	 * It creates a new process for the algorithm by setting up its parameters
	 * </p>
	 * @param dataset The instance of the dataset for dealing with its records
	 * @param nTrials The maximum number of generations to reach before completing the whole evolutionary learning
	 * @param popSize The number of chromosomes in the population
	 * @param tournamentSize The size of tournament to select the fittest chromosome in the current population
	 * @param pc The probability for the crossover operator
	 * @param pm The probability for the mutation operator
	 * @param af The factor of amplitude for each of the dataset attribute
	 */

	
	public NICGARProcess (myDataset dataset, int nTrials, int popSize, double pm, double af, double nichMin, double evMin, double percentUpdate) {

		this.dataset = dataset;
		this.nTrials = nTrials;
		this.uPopSize = popSize;
		this.pm = pm;
		this.af = af;
		this.minSupport = 0;
		this.nichMin = nichMin;
		this.evMin = evMin;
		this.percentUpdate = percentUpdate;
		this.trialsFirstRestart = 0;

		this.nAttr = this.dataset.getnVars();
		this.nTrans = this.dataset.getnTrans();
		this.trials = 0;
		this.rulesEP = new String("");
		this.sumFitnessEP = 0;
		this.wTrans = new double [this.nTrans];

		for(int i=0; i < this.wTrans.length; i++) this.wTrans[i] = 1.0;

		this.uPop = new ArrayList<Chromosome>();
		this.child_pop = new ArrayList<Chromosome>();
		this.EP = new ArrayList<Chromosome>();
	}

	public static myDataset getMyDataset() {
		return NICGARProcess.dataset;
	}


	public void run(){
		int nGn = 0;
		this.trials = 0;
		this.rulesEP = new String("");
		this.countReinc = 0;
		this.coverallEP = false;
		this.updatePop =0;

		this.initializePopulation(); 

		do {
			System.out.println("Computing Generation " + (nGn + 1) + "... ");
			this.updatePop = 0;
			this.selection(); 
			this.createNextPop();
			this.checkRestart();
			nGn++;
		}	while (this.trials < this.nTrials);

		this.updateEPall();
		this.avgDistPop = this.avgDistanceChrPop(this.EP);
		this.printPop();

		System.out.println("done.\n");
		System.out.println("Number of trials = " + this.trials + "\n");

	}

	public void createNextPop(){
		ArrayList<Chromosome> allChr;
		int i;
		allChr = new ArrayList<>();

		for(i = 0; i < this.uPop.size(); i++){
			if(!equalChromotoPop(this.uPop.get(i), allChr))
				allChr.add(this.uPop.get(i).copy());
		}

		for(i = 0; i < this.child_pop.size(); i++){
			if(!equalChromotoPop(this.child_pop.get(i), allChr))
				allChr.add(this.child_pop.get(i).copy());
		}

		Collections.sort(allChr);

		this.uPop = new ArrayList<>();
		for(i = 0; i < allChr.size() && this.uPop.size() < this.uPopSize; i++){
			if((allChr.get(i).isSpecieChild())) this.updatePop++;
			allChr.get(i).setProceed(false);
			allChr.get(i).setSeed(false);
			allChr.get(i).setSpecieChild(false);
			allChr.get(i).setTournSelect(false);
			this.uPop.add(allChr.get(i).copy());
		}
	}

  /*identify species in EP and we keep only the seed for each specie*/
	public void updateEPCovered(){

		ArrayList<Chromosome> nextEP;
		int i;
	
		for(i = 0; i < this.EP.size(); i++){
			this.EP.get(i).setProceed(false);
			this.EP.get(i).setSeed(false);

		}
		this.identifySpeciesNewImp(this.EP);

		nextEP = new ArrayList<Chromosome>();
		for(i= 0; i < this.speciesList.size(); i++){
			nextEP.add(this.speciesList.get(i).getBestChr().copy());
		}

		this.sumFitnessEP = 0;
		this.EP = new ArrayList<>();
		for (i = 0; i < nextEP.size(); i++){
			this.sumFitnessEP+=nextEP.get(i).getFitness();
			nextEP.get(i).setSpecieChild(false);
			this.EP.add(nextEP.get(i).copy());
		}
		this.sumFitnessEP/=this.EP.size()*1.0;
	}

	private void checkRestart(){
		double numberPercUpdate = (this.uPopSize*this.percentUpdate)/100;

		if(this.updatePop < numberPercUpdate){
			if(this.countReinc == 0) {
				this.trialsFirstRestart = this.trials*2;
			}
			if((this.nTrials - this.trials) > this.trialsFirstRestart){
				this.updateEPall();     
				this.restartPopEP();
				this.countReinc++;
			}
		}
	}

	public void updateEPall(){
		Chromosome bestChr;

		this.identifySpeciesNewImp(this.uPop); 

		//update EP 
		for(int i=0; i < this.speciesList.size();i++){
			bestChr = this.speciesList.get(i).getBestChr().copy();
			if(!equalChromotoPop(bestChr,this.EP)){
				if(this.updatePop!= -1)	// esto es para poder controlar que soluciones estaban en EP y cuales no...
					bestChr.setSpecieChild(true);
				else bestChr.setSpecieChild(false);
				
				double fitnessAchive = this.sumFitnessEP - (this.sumFitnessEP*(1-this.evMin));
				if(bestChr.getFitness() >= fitnessAchive)
					this.EP.add(bestChr);
			}
		}
		if(this.updatePop!= -1)		
			this.updateEPCovered(); // identify species in EP and we keep only the best chr  for  each specie
	}

	private ArrayList<Integer> tr_notCoveredPop(ArrayList<Chromosome> pop){
		ArrayList<Integer> tr_not_marked;
		tr_not_marked = new ArrayList<Integer> ();
		int i;

		for(i=0; i < this.nTrans; i++)  tr_not_marked.add(i);

		for(i=0; i<pop.size();i++){
			this.deleteTransCovered(pop.get(i), tr_not_marked);
		}

		return tr_not_marked;
	}

	private void restartPopEP(){
		int i,k,pos,random_pos, cont;
		ArrayList<Integer> tr_not_marked, tr_not_markedEP;
		Chromosome chromo;

		this.sumFitnessPop = 0;
		this.coverallEP = false;
		tr_not_markedEP = this.tr_notCoveredPop(this.EP);

		tr_not_marked = new ArrayList<>();

		if(tr_not_markedEP.size() == 0)
			for(i=0; i < this.nTrans; i++)  {
				tr_not_markedEP.add(i);
				tr_not_marked.add(i);
			}
		else{
			for(i=0; i < tr_not_markedEP.size(); i++)  {

				tr_not_marked.add(tr_not_markedEP.get(i));
			}
		}
		this.uPop = new ArrayList<>();
		//initialize population

		k = 0;
		cont = 0;
		while( k < this.uPopSize){
			if(tr_not_marked.size() == 0){
				for(i=0; i < this.nTrans; i++)  tr_not_marked.add(i);
				this.coverallEP = true;
			} 

			//create chromo
			random_pos = Randomize.Randint(0, tr_not_marked.size());
			pos =  tr_not_marked.get(random_pos);
			chromo = this.generateChromoCoveredPosNegAmp(pos);
		
			if( !equalChromotoPop(chromo, this.uPop)&& (chromo.getSupport()> this.minSupport)&&(!(chromo.getSupport()>(1 - this.minSupport)))&& (this.roundDoubleDown(chromo.getCF(), 2)>0) && (this.roundDoubleDown(chromo.getLift(), 2)>1)) {
				this.sumFitnessPop+= chromo.getFitness(); 
			    this.adjustFitnessCloseEP(chromo);
				
				if(!this.coverallEP) chromo.setReinicio(true);
				this.uPop.add(chromo);
				this.deleteTransCovered(this.uPop.get(k), tr_not_marked);
				k++;
				cont = 0;
			}
			else if (cont > 1000) {
				System.out.println("cont = 100");
				tr_not_marked.clear();
				cont = 0;
			}
			else  cont++;		
		}
		this.sumFitnessPop/= this.uPop.size();
	}

	private Chromosome generateChromoCoveredPosNegAmp(int pos_example){
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
		if(nAnts > 5) nAnts = Randomize.Randint(1, 5);

		// Antecedent
		for (i=0; i < nAnts; i++) {
			rnd_genes[sample[i]].setAttr (sample[i]);	
			rnd_genes[sample[i]].setActAs (Gene.ANTECEDENT);	
			rnd_genes[sample[i]].setIsPositiveInterval((Randomize.RandintClosed(0, 1) == 1) ? true : false);

			value = example[sample[i]];
			if  (this.dataset.getAttributeType(sample[i]) != myDataset.NOMINAL) {
				if  (this.dataset.getAttributeType(sample[i]) == myDataset.REAL) {
					if(rnd_genes[sample[i]].getIsPositiveInterval()){
						lb = Math.max(value - (this.dataset.getAmplitude(sample[i]) / (this.af * 2)), this.dataset.getMin(sample[i]));
						ub = Math.min(value + (this.dataset.getAmplitude(sample[i]) / (this.af * 2)), this.dataset.getMax(sample[i]));
					}
					else{ //is negative
						if((value -  this.dataset.getMin(sample[i])) > (this.dataset.getMax(sample[i]) - value)) { // left of the value
							lb = value - ((value -  this.dataset.getMin(sample[i])) - this.dataset.getAmplitude(sample[i]) / (this.af * 2));
							ub = this.dataset.getMin(sample[i]) + ((value -  this.dataset.getMin(sample[i])) - this.dataset.getAmplitude(sample[i]) / (this.af * 2));   						 
						}
						else { // right of the value 
							lb = this.dataset.getMax(sample[i]) - ((this.dataset.getMax(sample[i]) -  value) - this.dataset.getAmplitude(sample[i]) / (this.af * 2));
							ub = value + ((this.dataset.getMax(sample[i]) -  value) - this.dataset.getAmplitude(sample[i]) / (this.af * 2));   						 
						}
					}
				}
				else {
					if(rnd_genes[sample[i]].getIsPositiveInterval()){
						lb = Math.max((int) (value - (this.dataset.getAmplitude(sample[i]) / (this.af * 2))), this.dataset.getMin(sample[i]));
						ub = Math.min((int) (value + (this.dataset.getAmplitude(sample[i]) / (this.af * 2))), this.dataset.getMax(sample[i]));
					}
					else{ //is negative
						if((value -  this.dataset.getMin(sample[i])) > (this.dataset.getMax(sample[i]) - value)) { // left of the value
							lb = (int) value - ((value -  this.dataset.getMin(sample[i])) - this.dataset.getAmplitude(sample[i]) / (this.af * 2));
							ub = (int) this.dataset.getMin(sample[i]) + ((value -  this.dataset.getMin(sample[i])) - this.dataset.getAmplitude(sample[i]) / (this.af * 2));   						 
						}
						else { // right of the value 
							lb = (int) this.dataset.getMax(sample[i]) - ((this.dataset.getMax(sample[i]) -  value) - this.dataset.getAmplitude(sample[i]) / (this.af * 2));
							ub = (int) value + ((this.dataset.getMax(sample[i]) -  value) - this.dataset.getAmplitude(sample[i]) / (this.af * 2));   						 
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
				lb = Math.max(value - (this.dataset.getAmplitude(sample[i]) / (this.af * 2)), this.dataset.getMin(sample[i]));
				ub = Math.min(value + (this.dataset.getAmplitude(sample[i]) / (this.af * 2)), this.dataset.getMax(sample[i]));
			}
			else {
				lb = Math.max((int) (value - (this.dataset.getAmplitude(sample[i]) / (this.af * 2))), this.dataset.getMin(sample[i]));
				ub = Math.min((int) (value + (this.dataset.getAmplitude(sample[i]) / (this.af * 2))), this.dataset.getMax(sample[i]));
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
					lb = Math.max(value - (this.dataset.getAmplitude(sample[i]) / (this.af * 2)), this.dataset.getMin(sample[i]));
					ub = Math.min(value + (this.dataset.getAmplitude(sample[i]) / (this.af * 2)), this.dataset.getMax(sample[i]));
				}
				else {
					value = Randomize.RandintClosed ((int) this.dataset.getMin(sample[i]), (int) this.dataset.getMax(sample[i]));
					lb = (int) Math.max((int) (value - (this.dataset.getAmplitude(sample[i])) / (this.af * 2)), this.dataset.getMin(sample[i]));
					ub = (int) Math.min((int) (value + (this.dataset.getAmplitude(sample[i])) / (this.af * 2)), this.dataset.getMax(sample[i]));
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

		chromo = new Chromosome(rnd_genes);
		chromo.computeObjetives (this.dataset,this.wTrans);
		this.trials++;

		return chromo;
	}

	public void adjustFitnessCloseEP(Chromosome chr){

		int i, posCloseChr = -1;
		double  distMinChr = Double.MAX_VALUE, distChr;
		double distCommon = 1;
		
		// find the closest chr from population to the chr(by parameter)
		for(i = 0; i < this.EP.size(); i++){
			this.EP.get(i).distanceChromosome(chr);
			distChr = this.EP.get(i).getDistCoverGenes();
			if(this.EP.get(i).getDistCommonGenes() < this.nichMin){
				if(this.EP.get(i).getDistCoverGenes() < this.nichMin){
					if(distChr < distMinChr){
						distMinChr= distChr;
						posCloseChr = i;
						distCommon = this.EP.get(i).getDistCommonGenes();
					}
					else
						if(distChr == distMinChr)
							if(this.EP.get(i).getFitness() > this.EP.get(posCloseChr).getFitness()){
								posCloseChr = i;
								distCommon = this.EP.get(i).getDistCommonGenes();
							}
				}
			}	
		}

		// adjust the fitness´s chr if is worst than the closest chr form EP
		if(posCloseChr !=-1)
			if(chr.getFitness() <= this.EP.get(posCloseChr).getFitness()){
				distMinChr = (distCommon + distMinChr)/2;
				chr.setFitness(chr.getFitness() - ((1-distMinChr) * (chr.getFitness()*0.2)));
	    	}
	}

	public void identifySpeciesNewImp(ArrayList<Chromosome> popSpecie){
		double distMinChrNiche, distChrNiche;
		int i, j, posCloseNiche;
		Chromosome seed;

		ArrayList<Chromosome> pop = new ArrayList<>();

		this.identifySeeds(popSpecie);
		// assign chr to the species created before with their seeds
		for(i = 0; i < pop.size(); i++){
			if(!pop.get(i).isSeed()){
				pop.get(i).setProceed(false);
			}
		}

		for(i = 0; i < pop.size(); i++){
			if(!pop.get(i).isSeed()){
				distMinChrNiche = Double.MAX_VALUE;
				// find the closest niche to the chr
				posCloseNiche = -1;
				for(j = 0; j < this.speciesList.size(); j++){
					seed = this.speciesList.get(j).getSeedSpecie();
					seed.distanceChromosome(pop.get(i));
					distChrNiche = seed.getDistCoverGenes();
					if(seed.getDistCommonGenes() < this.nichMin){
						if(seed.getDistCoverGenes() < this.nichMin){ //distance hace referencia a la dist en cuanto a ejemplos q cubren de la BD
							if(distChrNiche < distMinChrNiche){
								distMinChrNiche = distChrNiche;
								posCloseNiche = j;
							}
							else
								if(distChrNiche == distMinChrNiche){
									if(this.speciesList.get(j).getSeedSpecie().getFitness() > this.speciesList.get(posCloseNiche).getSeedSpecie().getFitness()){
										posCloseNiche = j;
									}
								}
						}

					}
				}

				if(posCloseNiche!=-1){
					pop.get(i).setProceed(true);
					this.speciesList.get(posCloseNiche).getChrList().add(pop.get(i).copy());
					this.speciesList.get(posCloseNiche).incrementCountParents();
				}
			}
		}

		for(i = 0; i< this.speciesList.size(); i++){
			this.speciesList.get(i).findBestChr();
		}
	}
	
	/*generate the seeds for each specie */
	public void identifySeeds(ArrayList<Chromosome> pop){
		boolean found;
		int i,j;
		Chromosome seed, chr;
		Specie specie;

		this.speciesList = new ArrayList<Specie>();

		Collections.sort(pop);

		for(i = 0; i < pop.size(); i++){
			if(!pop.get(i).proceed){
				pop.get(i).setProceed(true);
				found = false;

				for(j = 0; j < this.speciesList.size(); j++){ 
					seed = this.speciesList.get(j).getSeedSpecie();
				    pop.get(i).distanceChromosome(seed);
					if(pop.get(i).getDistCommonGenes() < this.nichMin){
						if(pop.get(i).getDistCoverGenes() < this.nichMin)
							found = true;
					}
				}

				if(!found){
					specie = new Specie();
					pop.get(i).setSeed(true);
					pop.get(i).setProceed(true);
					specie.getChrList().add(pop.get(i).copy());
					specie.incrementCountParents();

					this.speciesList.add(specie);
    			}
			}
		}
	}

	public double avgDistanceChrPop(ArrayList<Chromosome> pop){
		double avgdistChrPop, avgDistPop=0, dist,distCover;
		int i,j;

		for (i = 0; i < pop.size(); i++){
			avgdistChrPop = 0;
			for(j=0; j < pop.size();j++){
				if(i!=j){
					dist = pop.get(i).distanceChromosome(pop.get(j));
					//dist = (pop.get(i).getDistCommonGenes() + distCover)/2.0;
					avgdistChrPop+= dist;

				}
			}
			avgdistChrPop/=(pop.size()-1.0);
			pop.get(i).setDistChrToPop(avgdistChrPop);
			avgDistPop+= avgdistChrPop;
		}
		avgDistPop/= pop.size();
		return avgDistPop;

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
		int cont = 0;
		for(i=0; i < this.nAttr; i++)  rnd_genes[i] = new Gene();
		for(i=0; i < this.nAttr; i++)  sample[i] = i;
		for(i=0; i < this.nTrans; i++)  tr_not_marked.add(i);

		while(this.uPop.size() < this.uPopSize){
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
			if(nAnts > 5) nAnts = Randomize.Randint(1, 5);

			// Antecedent
			for (i=0; i < nAnts; i++) {
				rnd_genes[sample[i]].setAttr (sample[i]);	
				rnd_genes[sample[i]].setActAs (Gene.ANTECEDENT);	
				rnd_genes[sample[i]].setIsPositiveInterval((Randomize.RandintClosed(0, 1) == 1) ? true : false);

				value = example[sample[i]];
				if  (this.dataset.getAttributeType(sample[i]) != myDataset.NOMINAL) {
					if  (this.dataset.getAttributeType(sample[i]) == myDataset.REAL) {
						if(rnd_genes[sample[i]].getIsPositiveInterval()){
							lb = Math.max(value - (this.dataset.getAmplitude(sample[i]) / (this.af * 2)), this.dataset.getMin(sample[i]));
							ub = Math.min(value + (this.dataset.getAmplitude(sample[i]) / (this.af * 2)), this.dataset.getMax(sample[i]));
						}
						else{ //is negative
							if((value -  this.dataset.getMin(sample[i])) > (this.dataset.getMax(sample[i]) - value)) { // left of the value
								lb = value - ((value -  this.dataset.getMin(sample[i])) - this.dataset.getAmplitude(sample[i]) / (this.af * 2));
								ub = this.dataset.getMin(sample[i]) + ((value -  this.dataset.getMin(sample[i])) - this.dataset.getAmplitude(sample[i]) / (this.af * 2));   						 
							}
							else { // right of the value 
								lb = this.dataset.getMax(sample[i]) - ((this.dataset.getMax(sample[i]) -  value) - this.dataset.getAmplitude(sample[i]) / (this.af * 2));
								ub = value + ((this.dataset.getMax(sample[i]) -  value) - this.dataset.getAmplitude(sample[i]) / (this.af * 2));   						 
							}
						}
					}
					else {
						if(rnd_genes[sample[i]].getIsPositiveInterval()){
							lb = Math.max((int) (value - (this.dataset.getAmplitude(sample[i]) / (this.af * 2))), this.dataset.getMin(sample[i]));
							ub = Math.min((int) (value + (this.dataset.getAmplitude(sample[i]) / (this.af * 2))), this.dataset.getMax(sample[i]));
						}
						else{ //is negative
							if((value -  this.dataset.getMin(sample[i])) > (this.dataset.getMax(sample[i]) - value)) { // left of the value
								lb = (int) value - ((value -  this.dataset.getMin(sample[i])) - this.dataset.getAmplitude(sample[i]) / (this.af * 2));
								ub = (int) this.dataset.getMin(sample[i]) + ((value -  this.dataset.getMin(sample[i])) - this.dataset.getAmplitude(sample[i]) / (this.af * 2));   						 
							}
							else { // right of the value 
								lb = (int) this.dataset.getMax(sample[i]) - ((this.dataset.getMax(sample[i]) -  value) - this.dataset.getAmplitude(sample[i]) / (this.af * 2));
								ub = (int) value + ((this.dataset.getMax(sample[i]) -  value) - this.dataset.getAmplitude(sample[i]) / (this.af * 2));   						 
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
					lb = Math.max(value - (this.dataset.getAmplitude(sample[i]) / (this.af * 2)), this.dataset.getMin(sample[i]));
					ub = Math.min(value + (this.dataset.getAmplitude(sample[i]) / (this.af * 2)), this.dataset.getMax(sample[i]));
				}
				else {
					lb = Math.max((int) (value - (this.dataset.getAmplitude(sample[i]) / (this.af * 2))), this.dataset.getMin(sample[i]));
					ub = Math.min((int) (value + (this.dataset.getAmplitude(sample[i]) / (this.af * 2))), this.dataset.getMax(sample[i]));
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
						lb = Math.max(value - (this.dataset.getAmplitude(sample[i]) / (this.af * 2)), this.dataset.getMin(sample[i]));
						ub = Math.min(value + (this.dataset.getAmplitude(sample[i]) / (this.af * 2)), this.dataset.getMax(sample[i]));
					}
					else {
						value = Randomize.RandintClosed ((int) this.dataset.getMin(sample[i]), (int) this.dataset.getMax(sample[i]));
						lb = (int) Math.max((int) (value - (this.dataset.getAmplitude(sample[i])) / (this.af * 2)), this.dataset.getMin(sample[i]));
						ub = (int) Math.min((int) (value + (this.dataset.getAmplitude(sample[i])) / (this.af * 2)), this.dataset.getMax(sample[i]));
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


			chromo = new Chromosome(rnd_genes);
			chromo.computeObjetives (this.dataset, this.wTrans);
			this.trials++;
			if(!equalChromotoPop(chromo, this.uPop)&& (chromo.getSupport()> this.minSupport)&&(!(chromo.getSupport()> 1 - this.minSupport))&& (this.roundDoubleDown(chromo.getCF(), 2)>0) && (this.roundDoubleDown(chromo.getLift(), 2) > 1)) {
				this.uPop.add(chromo);
				this.deleteTransCovered(chromo, tr_not_marked);
				this.sumFitnessPop+= chromo.getFitness();
			}
		}

		this.sumFitnessPop = this.sumFitnessPop/this.uPop.size();
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

	private int getPosChrPop (Chromosome chromo, ArrayList<Chromosome> pop){
		int i, posChr;
		boolean value;
		Chromosome aux;

		value = false;
		posChr = -1;
		for (i=0; (!value) && (i < pop.size()); i++) {
			aux = pop.get(i);
			if(chromo.equals(aux))  posChr = i;
		}

		return posChr;
	}

	/**
	 * Selection
	 *
	 */	
	private void selection(){
		int dad, mom;

		this.child_pop = new ArrayList<Chromosome>();

		while(this.child_pop.size() < this.uPopSize){
			dad = this.tournamentSelection();
			for (mom = this.tournamentSelection(); dad == mom; mom = this.tournamentSelection());
			crossover(this.uPop.get(dad), this.uPop.get(mom));
		}
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
				if  (this.dataset.getAttributeType(i) != myDataset.NOMINAL) {
					this.crossInterval05(genesSon1[i], genesSon2[i]);
				}
			}
		}

		for (i=0; i < this.nAttr; i++) { 
			if ((dad.getGene(i).getActAs() != Gene.CONSEQUENT) && (mom.getGene(i).getActAs() != Gene.CONSEQUENT)) {
				if (Randomize.Rand() < 0.5) {
					genesSon1[i] = dad.getGene(i).copy();
					genesSon2[i] = mom.getGene(i).copy();
					if  (this.dataset.getAttributeType(i) != myDataset.NOMINAL) {
						this.crossInterval05(genesSon1[i], genesSon2[i]);
					}
				}
				else {
					genesSon1[i] = mom.getGene(i).copy();
					genesSon2[i] = dad.getGene(i).copy();
					if  (this.dataset.getAttributeType(i) != myDataset.NOMINAL) {
						this.crossInterval05(genesSon1[i], genesSon2[i]);
					}
				}
			}
		}

		son1 = new Chromosome(genesSon1);
		son2 = new Chromosome(genesSon2);

		if (Randomize.Rand() < this.pm)  this.mutate (son1);
		if (Randomize.Rand() < this.pm)  this.mutate (son2);

		son1.forceConsistency();
		son2.forceConsistency();

		son1.computeObjetives (this.dataset, this.wTrans);
		son2.computeObjetives (this.dataset, this.wTrans);


		this.trials += 2;

		if(!equalChromotoPop(son1, this.child_pop)&& (son1.getSupport()> this.minSupport)&&(!(son1.getSupport()>1 - this.minSupport))&& (this.roundDoubleDown(son1.getCF(), 2)>0) && (this.roundDoubleDown(son1.getLift(),2) > 1)){
			son1.setSpecieChild(true);
			this.adjustFitnessCloseEP(son1);
			this.child_pop.add(son1);
		} 
		if(!equalChromotoPop(son2, this.child_pop)&& (son2.getSupport()> this.minSupport)&&(!(son2.getSupport()>1 - this.minSupport))&& (this.roundDoubleDown(son2.getCF(), 2)>0) && (this.roundDoubleDown(son2.getLift(),2) > 1)){
			son2.setSpecieChild(true);
			this.adjustFitnessCloseEP(son2);
			this.child_pop.add(son2);
		}
	}

	private double roundDoubleDown(double number, int decimalPlace){
		double numberRound;

		if(!Double.isInfinite(number)&&(!Double.isNaN(number))){
			BigDecimal bd = new BigDecimal(number);
			bd = bd.setScale(decimalPlace, BigDecimal.ROUND_DOWN);
			numberRound = bd.doubleValue();
			return numberRound;
		}else return number;
	}
	private void crossInterval05(Gene fromDad, Gene fromMom){
		double ampLb, ampUb, minLb1, maxLb1, minLb2, maxLb2, minUb1, maxUb1, minUb2, maxUb2, lbGen1, ubGen1, lbGen2, ubGen2;

		ampLb = Math.abs(fromDad.getLowerBound() - fromMom.getLowerBound());
		ampUb = Math.abs(fromDad.getUpperBound() - fromMom.getUpperBound());

		if  (this.dataset.getAttributeType(fromDad.getAttr()) == myDataset.REAL) {
			minLb1 = Math.max(this.dataset.getMin(fromDad.getAttr()), fromDad.getLowerBound() - ampLb*0.5);
			maxLb1 = Math.min(this.dataset.getMax(fromDad.getAttr()), fromDad.getLowerBound() + ampLb*0.5);

			lbGen1 = Randomize.Randdouble(minLb1, maxLb1);

			minLb2 = Math.max(this.dataset.getMin(fromMom.getAttr()), fromMom.getLowerBound() - ampLb*0.5);
			maxLb2 = Math.min(this.dataset.getMax(fromMom.getAttr()), fromMom.getLowerBound() + ampLb*0.5);

			lbGen2 = Randomize.Randdouble(minLb2, maxLb2);

			minUb1 = Math.max(this.dataset.getMin(fromDad.getAttr()), fromDad.getUpperBound() - ampUb*0.5);
			maxUb1 = Math.min(this.dataset.getMax(fromDad.getAttr()), fromDad.getUpperBound() + ampUb*0.5);

			ubGen1 = Randomize.Randdouble(minUb1, maxUb1);

			minUb2 = Math.max(this.dataset.getMin(fromMom.getAttr()), fromMom.getUpperBound() - ampUb*0.5);
			maxUb2 = Math.min(this.dataset.getMax(fromMom.getAttr()), fromMom.getUpperBound() + ampUb*0.5);

			ubGen2 = Randomize.Randdouble(minUb2, maxUb2);
		}else{
			minLb1 = Math.max(this.dataset.getMin(fromDad.getAttr()), (fromDad.getLowerBound() - ampLb*0.5));
			maxLb1 = Math.min(this.dataset.getMax(fromDad.getAttr()), (fromDad.getLowerBound() + ampLb*0.5));

			lbGen1 = Randomize.Randint((int) minLb1, (int) maxLb1);

			minLb2 = Math.max(this.dataset.getMin(fromMom.getAttr()), (fromMom.getLowerBound() - ampLb*0.5));
			maxLb2 = Math.min(this.dataset.getMax(fromMom.getAttr()), (fromMom.getLowerBound() + ampLb*0.5));

			lbGen2 = Randomize.Randint((int) minLb2, (int) maxLb2);

			minUb1 = Math.max(this.dataset.getMin(fromDad.getAttr()), (fromDad.getUpperBound() - ampUb*0.5));
			maxUb1 = Math.min(this.dataset.getMax(fromDad.getAttr()), (fromDad.getUpperBound() + ampUb*0.5));

			ubGen1 = Randomize.Randint((int) minUb1, (int) maxUb1);

			minUb2 = Math.max(this.dataset.getMin(fromMom.getAttr()), (fromMom.getUpperBound() - ampUb*0.5));
			maxUb2 = Math.min(this.dataset.getMax(fromMom.getAttr()), (fromMom.getUpperBound() + ampUb*0.5));

			ubGen2 = Randomize.Randint((int) minUb2, (int) maxUb2);
		}


		if(lbGen1 < ubGen1){
			fromDad.setLowerBound(lbGen1);
			fromDad.setUpperBound(ubGen1);
		}else{
			fromDad.setLowerBound(ubGen1);
			fromDad.setUpperBound(lbGen1);
		}

		if(lbGen2 < ubGen2){
			fromMom.setLowerBound(lbGen2);
			fromMom.setUpperBound(ubGen2);
		}else{
			fromMom.setLowerBound(ubGen2);
			fromMom.setUpperBound(lbGen2);
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

	private int tournamentSelection() {
		int chromo1, chromo2;

		for (chromo1 = Randomize.Randint(0, this.uPop.size()); this.uPop.get(chromo1).getFitness() == 0; chromo1 = Randomize.Randint(0, this.uPop.size()));
		for (chromo2 = Randomize.Randint(0, this.uPop.size()); chromo1 == chromo2 || this.uPop.get(chromo2).getFitness() == 0; chromo2 = Randomize.Randint(0, this.uPop.size()));

		if (this.uPop.get(chromo1).isBetter(this.uPop.get(chromo2)) >= 0)  return (chromo1);
		else  return (chromo2);
	}

	private double roundDouble(double number, int decimalPlace){
		double numberRound;

		if(!Double.isInfinite(number)&&(!Double.isNaN(number))){
			BigDecimal bd = new BigDecimal(number);
			bd = bd.setScale(decimalPlace, BigDecimal.ROUND_UP);
			numberRound = bd.doubleValue();
			return numberRound;
		}else return number;
	}

	public double percentTransCovered(ArrayList<AssociationRule> rules){
		int i, j, r, cnt_cov_rec;
		int[] covered;   
		AssociationRule rule;
		double percentCov;

		covered = new int[this.nTrans];
		for (i=0; i < this.nTrans; i++)  covered[i] = 0;

		for (r=0; r < rules.size(); r++) {
			rule = rules.get(r);

			for (j=0; j < this.nTrans; j++) {
				if (covered[j] < 1) {
					if (rule.isCovered(this.dataset.getExample(j)))  covered[j] = 1;
				}
			}
		}

		cnt_cov_rec = 0;
		for (i=0; i < this.nTrans; i++)  cnt_cov_rec += covered[i];

		percentCov = (100.0 * cnt_cov_rec) / this.nTrans;
		return percentCov;
	}

	public void saveReport(ArrayList<AssociationRule> rules,PrintWriter w) {
		int i, j, r, cnt_cov_rec;
		double avg_yulesQ = 0.0, avg_sup = 0.0, avg_conf = 0.0, avg_percentInterv = 0.0,avg_ant_length = 0.0, avg_lift = 0.0, avg_conv = 0.0, avg_CF = 0.0, avg_netConf = 0.0;
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
			avg_percentInterv += rule.getPercentInterval();
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

		w.println("\nNumber of Frequent Itemsets found: " + 0);	
		System.out.println("\nNumber of Frequent Itemsets found: " + 0);
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
			w.println("Average Distance Pop: " + roundDouble(this.avgDistPop,2));
			System.out.println("Average Distance Pop: " + roundDouble(this.avgDistPop,2));
			w.println("Number of Covered Records (%): " + roundDouble((100.0 * cnt_cov_rec) / this.nTrans, 2));
			System.out.println("Number of Covered Records (%): " + roundDouble((100.0 * cnt_cov_rec) / this.nTrans, 2));
		}
	}


	public void printPop() {
		int i;
		boolean stop;
		Chromosome chromo;

		stop = false;

		this.rulesEP += ("Support\tantecedent_support\tconsequent_support\tConfidence\tLift\tConv\tCF\tNetConf\tYulesQ\tnAttributes\tnDistChrPop\n");

		for (i=0; i < this.EP.size() && !stop; i++) {
			chromo = this.EP.get(i);
			this.rulesEP += ("" + roundDouble(chromo.getSupport(),2) + "\t" + roundDouble(chromo.getAntsSupport(),2) + "\t" + roundDouble(chromo.getConsSupport(),2) + "\t" + roundDouble(chromo.getConfidence(),2) + "\t" + roundDouble(chromo.getLift(),2) + "\t" + roundDouble(chromo.getConv(),2) + "\t" + roundDouble(chromo.getCF(),2) + "\t" + roundDouble(chromo.getNetConf(),2) + "\t" + roundDouble(chromo.getYulesQ(),2) + "\t" + (chromo.getnAnts()+1) + "\t" + roundDouble(chromo.getDistChrToPop(),2) + "\n");
		}
	}

	public ArrayList<AssociationRule> generateRulesEP() {
		int i;
		boolean stop;
		Chromosome chromo;
		ArrayList<AssociationRule> rulesEP = new ArrayList<AssociationRule>();
		stop = false;

		for (i=0; i < this.EP.size() && !stop; i++) {
			chromo = this.EP.get(i);
			rulesEP.add (new AssociationRule(chromo));
		}
		return rulesEP;
	}

	public String getrulesEP() {
		return (this.rulesEP);
	}


}
