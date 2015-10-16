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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.ARMMGA;

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

public class ARMMGAProcess
{
	private myDataset ds;
	private DataB dataBase;

	private int nTrials;
	private int trials;
	private int popsize;
	private double ps;
	private double pc;
	private double pm;
	private int kItemsets;
	private double alpha;

	private ArrayList<Chromosome> uPop;
	private ArrayList<Chromosome> best_pop;
	private ArrayList<Chromosome> parent_pop;
	private ArrayList<Chromosome> childs_pop;
	private ArrayList<AssociationRule> assocRules;
	private ArrayList<Gene> allItems;

	public ARMMGAProcess(myDataset ds, DataB dataBase, int nTrials, int popsize, int kItemsets, double ps, double pc, double pm, double alpha) {
		this.ds = ds;
		this.dataBase = dataBase;
		this.popsize = popsize;
		this.nTrials = nTrials;
		this.kItemsets = kItemsets;
		this.ps = ps;
		this.pc = pc;
		this.pm = pm;
		this.alpha = alpha;

		this.trials = 0;
		this.allItems = new ArrayList<Gene>();
		this.uPop = new ArrayList<Chromosome>();
		this.parent_pop = new ArrayList<Chromosome>();
		this.childs_pop = new ArrayList<Chromosome>();
		this.best_pop = new ArrayList<Chromosome>();

	}


	public void run() {
		int nGen = 0;
		this.trials = 0;

		System.out.println("Initialization");
		this.initialize();

		this.best_pop = copyListChromosome(this.uPop);

		do {
			System.out.println("Generation: " + nGen);
			this.select();
			this.crossover();
			this.mutate();
			this.selectNextpop();
			if((averageFitPop(this.best_pop) < averageFitPop(this.uPop))||(averageFitPop(this.best_pop) == averageFitPop(this.uPop)) && (varianceFitPop(this.best_pop) >= varianceFitPop(this.uPop))){
				this.best_pop = copyListChromosome(this.uPop);
			}
			nGen++;
		}while (this.trials < this.nTrials);

		this.uPop = copyListChromosome(this.best_pop); 
		this.remove_equalsChromo();
		this.genRules();
	}


	private void remove_equalsChromo(){ 

		for(int i= 0; i< this.uPop.size();i++){
			for(int j=i+1;j<this.uPop.size();j++){
				if(this.uPop.get(i).equals(this.uPop.get(j))){
					this.uPop.remove(j);
					j--;
				}
			}
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
			rulesList += ("" + roundDouble(rules.get(i).getAll_support(),2) + "\t" + roundDouble(rules.get(i).getSupport(),2) + "\t" + roundDouble(rules.get(i).getSupport_consq(),2) + "\t" + roundDouble(rules.get(i).getConfidence(),2) + "\t" + roundDouble(rules.get(i).getLift(),2)
					+ "\t" + roundDouble(rules.get(i).getConv(),2) + "\t" + roundDouble(rules.get(i).getCF(),2) + "\t" + roundDouble(rules.get(i).getNetConf(),2) + "\t" + roundDouble(rules.get(i).getYulesQ(),2) + "\t" + lenghtrule + "\n");
		}
		return rulesList;
	}

	private double averageFitPop(ArrayList<Chromosome> pop)
	{
		double suma=0,average;
		for(int i=0;i<pop.size();i++){
			suma = suma + pop.get(i).getFit();
		}
		average = suma/pop.size();
		return average;
	}

	private double varianceFitPop(ArrayList<Chromosome> pop){

		double average = this.averageFitPop(pop);
		double suma=0, resta, variance;
		for(int i=0;i<pop.size();i++){
			resta = pop.get(i).getFit()-average;
			suma = suma + Math.pow(resta,2);
		}
		variance = suma/pop.size();
		return variance;
	}

	private void initialize() {
		int i,nVars, cont;
		Chromosome chromo;
		Gene gen;
		int []coveredTIDs, covered;

		ArrayList<Gene> allItemTemp = new ArrayList<Gene>();
		ArrayList<Gene> noExistItem = new ArrayList<Gene>();

		cont = 0;
		nVars = this.ds.getnVars();
		this.createListAllItem();

		for(i=0;i<this.allItems.size();i++){
			allItemTemp.add(this.allItems.get(i).copy());
		}

		while((this.uPop.size()<this.popsize)){
			if((this.kItemsets < this.countAttbDifItem(allItemTemp))){

				chromo = new Chromosome(Randomize.Randint(0, this.kItemsets-1), nVars);

				gen = allItemTemp.get(Randomize.Randint(0,allItemTemp.size()));
				chromo.getGenes().add(gen);
				chromo.onUsed(gen.getAttr());
				coveredTIDs = chromo.getCoveredTIDs(0, chromo.getGenes().size()-1, this.ds, this.dataBase);
				allItemTemp.remove(gen);

				while((this.kItemsets < this.countAttbDifItem(allItemTemp)) && (chromo.getGenes().size()< this.kItemsets)){ 
					gen = allItemTemp.get(Randomize.Randint(0,allItemTemp.size()));
					if(!chromo.useAttb(gen)){
						chromo.getGenes().add(gen);
						chromo.onUsed(gen.getAttr());
						covered = countSupportList(chromo, 0, chromo.getGenes().size()-1, coveredTIDs);
						chromo.orderByAttb();
						allItemTemp.remove(gen);
						if((covered.length == 0)||(equalChromotoPop(chromo, this.uPop))!=-1){
							cont++;
							if(cont < 1000)
								chromo.getGenes().remove(chromo.getGenes().size()-1);

						}
						else{
							coveredTIDs = covered; 

						}

					}
				}
			}

			else{ //restart list allItems

				chromo = new Chromosome(Randomize.Randint(0, this.kItemsets-1), nVars);
				chromo.setLengthAnt(Randomize.Randint(0, this.kItemsets-1));

				gen = allItemTemp.get(0);
				chromo.getGenes().add(gen);
				chromo.onUsed(gen.getAttr());
				coveredTIDs = chromo.getCoveredTIDs(0, chromo.getGenes().size()-1, this.ds, this.dataBase);
				allItemTemp.remove(gen);

				for(i=1;i<allItemTemp.size() && (chromo.getGenes().size()< this.kItemsets);i++){
					gen = allItemTemp.get(i);
					if(!chromo.useAttb(gen)){
						chromo.getGenes().add(gen);
						chromo.onUsed(gen.getAttr());
						covered = countSupportList(chromo, 0, chromo.getGenes().size()-1, coveredTIDs);
						chromo.orderByAttb();
						allItemTemp.remove(gen);
						if((covered.length == 0)||(equalChromotoPop(chromo, this.uPop))!=-1)
							chromo.getGenes().remove(chromo.getGenes().size()-1);
						else{
							coveredTIDs = covered; 
						}
					}
				}

				noExistItem = new ArrayList<Gene>();
				noExistItem = this.getItemNoUse(chromo);

				while((this.kItemsets < this.countAttbDifItem(allItemTemp)) && (chromo.getGenes().size() < this.kItemsets)){
					gen = noExistItem.get(Randomize.Randint(0,noExistItem.size()));
					if(!chromo.useAttb(gen)){
						chromo.getGenes().add(gen);
						chromo.onUsed(gen.getAttr());
						covered = countSupportList(chromo, 0, chromo.getGenes().size()-1, coveredTIDs);
						chromo.orderByAttb();
						noExistItem.remove(gen);
						if((covered.length == 0)||(equalChromotoPop(chromo, this.uPop))!=-1){
							cont++;
							if(cont < 1000)
								chromo.getGenes().remove(chromo.getGenes().size()-1);
						}
						else{
							coveredTIDs = covered; 
						}
					}

				}

				allItemTemp.clear();
				allItemTemp.addAll(noExistItem);
			}

			//add chr to initial population
			if(chromo.getGenes().size()>=this.kItemsets)
			{
				chromo.computeFitness(this.kItemsets, this.ds, this.dataBase);
				this.trials++;
				cont = 0;
				this.uPop.add(chromo);
			}
		}
	}



	private int equalChromotoPop(Chromosome chromo, ArrayList<Chromosome> pop)
	{
		int i, pos_Chr = -1;
		boolean value;
		Chromosome aux;

		value = false;

		for (i=0; (!value) && (i < pop.size()); i++) {
			aux = pop.get(i);
			if(chromo.equals(aux)){
				value = true;
				pos_Chr = i;
			}
		}
		return pos_Chr;
	}

	private void createListAllItem()
	{
		Gene gen;

		for(int i=0;i<dataBase.numVariables();i++){
			for(int j=0;j<dataBase.numIntervals(i);j++){

				gen = new Gene();
				gen.setAttr(i);
				gen.setType(this.ds.getType(i));
				gen.addValue(j);
				this.allItems.add(gen);
			}
		}
	}

	private void select() {
		int i;
		this.parent_pop.clear();
		Collections.sort(this.uPop);

		for (i=0;i < this.uPop.size();i++) {
			if ((Randomize.Rand() * (this.uPop.size()- i)/this.uPop.size()) >= 1-this.ps){
				this.parent_pop.add(this.uPop.get(i));
			}

		}
	}

	private void selectNextpop() {
		ArrayList<Chromosome> parent_randompop, child_randompop;
		Chromosome bestchromo;
		int j=0, pos_Chr;
		boolean end_popsize=false;

		this.uPop.clear();

		Collections.sort(this.childs_pop);

		//add the 3 best children's childs_pop into next_pop
		if(this.childs_pop.size()==0)
			end_popsize = true;

		while((j<3)&&(!end_popsize)){
	        this.uPop.add(this.childs_pop.get(0));
			j++;  
			this.childs_pop.remove(0);
			if(this.childs_pop.size()<j)
				end_popsize = true;

		}

		while((this.uPop.size()<this.popsize)&&((this.parent_pop.size()!=0)||((this.childs_pop.size()!=0)))){
			parent_randompop = new ArrayList<Chromosome>();
			child_randompop = new ArrayList<Chromosome>();
			parent_randompop = selectRandomChromo(this.parent_pop,5);
			child_randompop = selectRandomChromo(this.childs_pop,5);
			bestchromo = getBestChromo(parent_randompop, child_randompop);
			if(bestchromo != null){
				pos_Chr = equalChromotoPop(bestchromo, this.parent_pop); 
				if(pos_Chr!=-1) this.parent_pop.remove(pos_Chr);
				else{
					pos_Chr = equalChromotoPop(bestchromo, this.childs_pop);
					if(pos_Chr!=-1)this.childs_pop.remove(pos_Chr);	     
				}
			}
			else{
				bestchromo = getBestChromo(this.parent_pop, this.childs_pop).copy();
				pos_Chr = equalChromotoPop(bestchromo, this.parent_pop); 
				if(pos_Chr!=-1) this.parent_pop.remove(pos_Chr);
				else{
					pos_Chr = equalChromotoPop(bestchromo, this.childs_pop);
					if(pos_Chr!=-1)this.childs_pop.remove(pos_Chr);	     
				}  
			}

			this.uPop.add(bestchromo.copy());
		}
	}

	private ArrayList<Chromosome> selectRandomChromo(ArrayList<Chromosome> pop, int count_chromo)
	{
		int i, k, divide, resto;
		double prob_group_select;
		boolean end_popsize=false;
		ArrayList<Chromosome> group,select_randompop,selectgroup,pop_copy;

		ArrayList<ArrayList<Chromosome>> listgroups = new ArrayList<ArrayList<Chromosome>>();
		select_randompop = new ArrayList<Chromosome>();
		selectgroup = new ArrayList<Chromosome>();
		pop_copy = new ArrayList<Chromosome>();

		pop_copy.addAll(pop);
		Collections.sort(pop_copy);

		divide = pop.size()/4;
		resto = pop.size()% 4;
		if(resto == 0){
			for(k=0;k<4;k++){
				i=0;
				group = new ArrayList<Chromosome>();
				listgroups.add(group);

				if(pop_copy.size()<=i)
					end_popsize = true;

				while((i<divide)&&(!end_popsize)){
					listgroups.get(k).add(pop_copy.get(0).copy());
					pop_copy.remove(0);
					i++;
					if(pop.size()<=i)
						end_popsize = true;
				}
				end_popsize=false; 
			}
		}
		else{
			k=0;
			for(k=0;k<3;k++){
				i=0;
				group = new ArrayList<Chromosome>();
				listgroups.add(group);

				if(pop_copy.size()<=i)
					end_popsize = true;

				while((i<divide)&&(!end_popsize)){
					listgroups.get(k).add(pop_copy.get(0).copy());
					pop_copy.remove(0);
					i++;
					if(pop_copy.size()<=i)
						end_popsize = true;
				}
			}
			end_popsize=false;
			i=0;
			group = new ArrayList<Chromosome>();
			listgroups.add(group);
			if(pop_copy.size()<=i)
				end_popsize = true;
			while((i<(divide+resto))&&(!end_popsize)){
				listgroups.get(3).add(pop_copy.get(0).copy());
				pop_copy.remove(0);
				i++;
			}
		}

		for( i=0;i<count_chromo;i++){
			prob_group_select = Randomize.Rand();
			if((prob_group_select>=0)&&(prob_group_select < 0.1))
				selectgroup = listgroups.get(0);
			if((prob_group_select>=0.1)&&(prob_group_select < 0.3))
				selectgroup = listgroups.get(1);
			if((prob_group_select>=0.3)&&(prob_group_select < 0.6))
				selectgroup = listgroups.get(2);
			if((prob_group_select>=0.6)&&(prob_group_select < 1))
				selectgroup = listgroups.get(3);
			if(selectgroup.size()> 0){
				select_randompop.add(selectgroup.get(Randomize.Randint(0,selectgroup.size()-1)));
			}
			if(pop.size()< count_chromo){
				count_chromo = pop.size();
			}
		}
		return select_randompop;
	}

	private Chromosome getBestChromo(ArrayList<Chromosome> randomparent_pop, ArrayList<Chromosome> randomchilds_pop)
	{
		Chromosome bestChromo;
		Collections.sort(randomparent_pop);
		Collections.sort(randomchilds_pop);
		bestChromo = null;
		if((randomchilds_pop.size()!=0)&&((randomparent_pop.size()!=0))){
			if(randomparent_pop.get(0).getFit()> randomchilds_pop.get(0).getFit())
				bestChromo = randomparent_pop.get(0).copy();
			else
				bestChromo = randomchilds_pop.get(0).copy();
		}else
			if(randomparent_pop.size()!=0)
				bestChromo = randomparent_pop.get(0).copy();
			else
				if(randomchilds_pop.size()!=0)
					bestChromo = randomchilds_pop.get(0).copy();
		return bestChromo;
	}

	private ArrayList<Chromosome> copyListChromosome(ArrayList<Chromosome> listChromo)
	{
		ArrayList<Chromosome> copy_listChromo = new ArrayList<Chromosome>();
		for(int i=0;i<listChromo.size();i++){
			copy_listChromo.add(listChromo.get(i).copy());
		}
		return copy_listChromo;
	}

	private void crossover() {
		int i, j;

		this.childs_pop.clear();
		ArrayList<Chromosome> childs;
		Collections.sort(this.parent_pop);

		for(i=0; (i<this.parent_pop.size()) && (this.childs_pop.size()< this.popsize);i++){
			for (j=i+1; (j<this.parent_pop.size()) && (this.childs_pop.size()< this.popsize); j++){
				if ((Randomize.Rand()* (this.parent_pop.size()-i)/this.parent_pop.size() * (this.parent_pop.size()-j)/this.parent_pop.size())>= (1- pc)){
					childs = this.orderCrossover(this.parent_pop.get(i), this.parent_pop.get(j));
					childs.get(0).computeFitness(this.kItemsets, this.ds, this.dataBase);
					this.trials++;
					this.childs_pop.add(childs.get(0).copy());
					childs.get(1).computeFitness(this.kItemsets, this.ds, this.dataBase);
					this.trials++;
					this.childs_pop.add(childs.get(1).copy());
				}
			}
		}

		if(this.childs_pop.size()==0){
			this.childs_pop = copyListChromosome(this.parent_pop);
		}
	
    }

	private ArrayList<Chromosome> orderCrossover(Chromosome dad, Chromosome mom)
	{
		int i, j, k, posi, posj, aux,lenght_ant,lenght_ant1 ;
		boolean insert;
		Chromosome off, off1;
		Gene gen;
		posi=posj=-1;
		while(posi==posj){
			posi = Randomize.Randint(0, this.kItemsets);
			posj = Randomize.Randint(0, this.kItemsets);
		}

		if (posi > posj) {
			aux = posi;
			posi = posj;
			posj = aux;
		}

		if (posi==0) {
			lenght_ant = dad.getLengthAnt();
			lenght_ant1 = mom.getLengthAnt();
		}
		else {
			lenght_ant = mom.getLengthAnt();
			lenght_ant1 = dad.getLengthAnt();
		}
		off = new Chromosome(lenght_ant,this.ds.getnVars());
		off1 = new Chromosome(lenght_ant1,this.ds.getnVars());

		for (k=posi; k < posj; k++) {
			off.onUsed((dad.getGen(k)).getAttr());
			off.getGenes().add(dad.getGen(k).copy());
			off1.onUsed((mom.getGen(k)).getAttr());
			off1.getGenes().add(mom.getGen(k).copy());
		}
		//building first offspring
		j= posj;
		for(i= posj;i<kItemsets;i++){
			insert = false;
			while((!insert)){
				gen = mom.getGen(j);
				if(!off.isUsed(gen.getAttr())){
					off.getGenes().add(mom.getGen(j).copy());
					off.onUsed((mom.getGen(j)).getAttr());
					insert = true;
				}
				j++;
				if(j== mom.getGenes().size()) j=0;
			}
		}
		for(i= 0;i<posi;i++){
			insert = false;
			while((!insert)){
				if(j== mom.getGenes().size()) j=0;
				gen = mom.getGen(j);
				if(!off.useAttb(gen)){
					off.getGenes().add(mom.getGen(j));
					off.onUsed((mom.getGen(j)).getAttr());
					insert = true;
				}
				j++;
			}
		}
		//	building second offspring
		k= posj;
		for(i= posj ;i<kItemsets;i++){
			insert = false;
			while((!insert)){
				gen = dad.getGen(k);
				if(!off1.isUsed(gen.getAttr())){
					off1.getGenes().add(dad.getGen(k).copy());
					off1.onUsed((dad.getGen(k)).getAttr());
					insert = true;
				}
				k++;
				if(k== dad.getGenes().size()) k=0;
			}
		}
		for(i= 0;i<posi;i++){
			insert = false;
			while((!insert)){
				if(k== dad.getGenes().size()) k=0;
				gen = dad.getGen(k);
				if(!off1.isUsed(gen.getAttr())){
					off1.getGenes().add(i,dad.getGen(k));
					off1.onUsed((dad.getGen(k)).getAttr());
					insert = true;
				}
				k++;
			}
		}

		off.orderByAttb();
		off1.orderByAttb();

		ArrayList<Chromosome> childs = new ArrayList<Chromosome>();
		childs.add(off);
		childs.add(off1);
		return childs; 		  
	}

	private void mutate() {
		int i;
		Chromosome chromo;
		Gene gen;
		Collections.sort(this.childs_pop);
		ArrayList<Gene> listgenes;

		for(i=0;i<this.childs_pop.size();i++){
			listgenes = new ArrayList<Gene>();
			chromo = this.childs_pop.get(i).copy();

			if((Randomize.Rand()* (this.childs_pop.size()-i)/this.childs_pop.size())>=1-this.pm){

				listgenes = getItemNoUse(chromo);
				chromo.setLengthAnt(Randomize.Randint(0, this.kItemsets-1));
				gen = listgenes.get(Randomize.Randint(1, listgenes.size()));

				if(!chromo.isUsed(gen.getAttr())){

					int gen_change = Randomize.Randint(0, this.kItemsets);
					chromo.offUsed(chromo.getGenes().get(gen_change).getAttr());
					chromo.getGenes().set(gen_change,gen);
					chromo.onUsed(gen.getAttr());
					chromo.orderByAttb();
					chromo.computeFitness(this.kItemsets, this.ds, this.dataBase);
					this.trials++;
					if(equalChromotoPop(chromo, this.childs_pop)==-1)
						this.childs_pop.set(i,chromo.copy());
				}
			}      
		}

	}

	private ArrayList<Gene> getItemNoUse(Chromosome chromo){

		ArrayList<Gene> allItemTemp = new ArrayList<Gene>();
		int pos_item;
		for(int i=0;i<this.allItems.size();i++){
			allItemTemp.add(this.allItems.get(i).copy());
		}
		for(int i=0;i<chromo.getGenes().size();i++){
			pos_item = findItem(chromo.getGen(i), allItemTemp);
			if(pos_item != -1) allItemTemp.remove(pos_item);
		}

		return allItemTemp;
	}

	private int findItem(Gene gen, ArrayList<Gene> genes){

		boolean find = false;
		int pos = -1;
		int i=0;
		while((i<genes.size())&&(!find)){
			if(genes.get(i).getAttr()== gen.getAttr()){
				if(genes.get(i).equals(gen)){
					find = true;
					pos = i;
				}
			}
			i++;
		}
		return pos;
	}

	private int countAttbDifItem(ArrayList<Gene> genes)
	{
		ArrayList<Gene> itemAttbDif = new ArrayList<Gene>();
		boolean find;
		int i,j;
		if(genes.size()!=0){
			itemAttbDif.add(genes.get(0));
			for(i=1;i<genes.size();i++){
				find = false;
				j=0;
				for(j=0; j<itemAttbDif.size() && !find; j++){
					if(itemAttbDif.get(j).getAttr()== genes.get(i).getAttr())
						find = true;
				}
				if(!find){
					itemAttbDif.add(genes.get(i));
				}
			}
		}
		return itemAttbDif.size();
	}

	private int[] countSupportList(Chromosome chromo, int ini, int fin, int[] covered_list_chr){

		int i, j, k, t, attr, cnt_cov_rec;
		boolean ok;
		int[] covered, coveredPos;
		covered = new int[covered_list_chr.length] ;
		ArrayList<Integer> value;
		double[][] trans = this.ds.getRealTransactions();

		k=0;

		for (i=0; i < covered_list_chr.length; i++)  covered[i] = 0;

		for (t=0; t < covered_list_chr.length; t++) {
			ok = true;

			for (i=ini; i <= fin && ok; i++) {
				attr = (chromo.getGen(i)).getAttr();
				value = (chromo.getGen(i)).getValue();

				ok = false;

				for (j=0; j < value.size() && !ok; j++) {
					if (this.dataBase.isCovered(attr, value.get(j).intValue(), trans[covered_list_chr[t]][attr]))   ok = true;
				}		  
			}

			if (ok)covered[t]= 1;


		}
		cnt_cov_rec = 0;
		for (i=0; i < covered_list_chr.length; i++)  cnt_cov_rec += covered[i];

		k=0;
		coveredPos = new int[cnt_cov_rec];
		for (i=0; i < covered_list_chr.length; i++){
			if(covered[i]==1){
				coveredPos[k]=covered_list_chr[i];
				k++;
			}
		}


		return coveredPos;
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
	
	public void saveReport (ArrayList<AssociationRule> rules,PrintWriter w ) {
		int i, countRules, length;
		AssociationRule rule;
		double avg_yulesQ = 0.0, avg_sup = 0.0, avg_conf = 0.0, avg_lift = 0.0, avg_conv = 0.0, avg_CF = 0.0, avg_netConf = 0.0;

		countRules = length = 0;
		avg_sup = avg_conf = avg_lift = 0.0;
		for (i=0; i < rules.size(); i++) {
			rule = rules.get(i); 
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
		System.out.println("Length of the Itemsets: " + this.kItemsets);
		w.println("\nNumber of Frequent Itemsets found: " + "-");	
		System.out.println("\nNumber of Frequent Itemsets found: " + "-");
		w.println("\nNumber of Association Rules generated: " + countRules);	
		System.out.println("Number of Association Rules generated: " + countRules);
		if ( ! rules.isEmpty() ){
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
			w.println("Average Number of Attributes: " + roundDouble(( length / (double) countRules ),2));
			System.out.println("Average Number of Attributes: " + roundDouble(( length / (double) countRules ),2));
			w.println("Number of Covered Records (%): " + roundDouble((100.0 * this.numCoveredRecords ()) / this.ds.getnTrans(), 2));
			System.out.println("Number of Covered Records (%): " + roundDouble(( 100.0 * this.numCoveredRecords ()) / this.ds.getnTrans(),2));
		}
		else System.out.println("No Statistics.");

	}  

	public ArrayList<AssociationRule> getRules () {
		return this.assocRules;
	}  

	private void genRules() {
		int i,j;
		AssociationRule rule;
		double yulesQ, numeratorYules, denominatorYules, confidance,lift,conv, CF, netConf; 
		Chromosome chromo;


		this.assocRules = new ArrayList<AssociationRule>();

		for (i=0; i < this.uPop.size(); i++) {
			chromo = this.uPop.get(i);
			rule = new AssociationRule();

			for (j=0; j <= chromo.getLengthAnt(); j++)  rule.addAntecedent((chromo.getGen(j)).copy());
			for (j=chromo.getLengthAnt()+1; j < this.kItemsets; j++)  rule.addConsequent((chromo.getGen(j)).copy());

			rule.setSupport (chromo.getSupportAnt());
			rule.setSupport_consq(chromo.getSupportCon());
			rule.setAll_support (chromo.getSupportAll());

			//compute confidance
			if(chromo.getSupportAll()!=0.0)
				confidance = chromo.getSupportAll() / chromo.getSupportAnt();
			else confidance = 0;

			// compute lift
			if((chromo.getSupportAnt() == 0)||(chromo.getSupportCon() == 0))
				lift = 1;
			else lift = chromo.getSupportAll() /(chromo.getSupportAnt()* chromo.getSupportCon());

			// compute conviction
			if((chromo.getSupportCon()==1)||(chromo.getSupportAnt() == 0))
				conv = 1;
			else conv = (chromo.getSupportAnt()*(1-chromo.getSupportCon()))/(chromo.getSupportAnt()- chromo.getSupportAll());

			// compute netconf
			if ((chromo.getSupportAnt() == 0)||(chromo.getSupportAnt() == 1)||(Math.abs(chromo.getSupportAnt()*(1-chromo.getSupportAnt())) <= 0.001))
				netConf = 0;
			else netConf = (chromo.getSupportAll()-(chromo.getSupportAnt()* chromo.getSupportCon()))/(chromo.getSupportAnt()*(1-chromo.getSupportAnt()));

			//compute yulesQ
			numeratorYules = ((chromo.getSupportAll() * (1 - chromo.getSupportCon() - chromo.getSupportAnt() + chromo.getSupportAll())) - ((chromo.getSupportAnt() - chromo.getSupportAll())* (chromo.getSupportCon() - chromo.getSupportAll())));
			denominatorYules = ((chromo.getSupportAll() * (1 - chromo.getSupportCon() - chromo.getSupportAnt() + chromo.getSupportAll())) + ((chromo.getSupportAnt() - chromo.getSupportAll())* (chromo.getSupportCon() - chromo.getSupportAll())));

			if((chromo.getSupportAnt() == 0)||(chromo.getSupportAnt() == 1)||(chromo.getSupportCon() == 0)||(chromo.getSupportCon() == 1)||(Math.abs(denominatorYules) <= 0.001))
				yulesQ = 0;
			else  yulesQ = numeratorYules/denominatorYules;

			// compute CF
			CF = 0;
			if(confidance > chromo.getSupportCon())
				CF = (confidance - chromo.getSupportCon())/(1-chromo.getSupportCon());
			else 
				if(confidance < chromo.getSupportCon())
					CF = (confidance - chromo.getSupportCon())/(chromo.getSupportCon());

			rule.setConfidence (confidance);
			rule.setLift(lift);
			rule.setConv(conv);
			rule.setCF(CF);
			rule.setNetConf(netConf);
			rule.setYulesQ(yulesQ);

			this.assocRules.add(rule);
		}
	}


	private int numCoveredRecords () {
		int i, j, covered, nTrans;
		int[] tidCovered;
		Chromosome chromo;


		nTrans = this.ds.getnTrans();
		boolean [] marked = new boolean[nTrans];
		for (i=0; i < nTrans; i++)  marked[i] = false;

		for (i=0; i < this.uPop.size(); i++) {
			chromo = this.uPop.get(i);
			tidCovered = chromo.getCoveredTIDs(0, this.kItemsets-1, this.ds, this.dataBase);
			for (j=0; j < tidCovered.length; j++) {
				marked[tidCovered[j]] = true;
			}
		}

		covered = 0;
		for (i=0; i < nTrans; i++)
			if (marked[i])  covered++;
		return covered;
	}
}
