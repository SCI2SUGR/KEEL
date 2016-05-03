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

import java.util.ArrayList;
import org.core.Randomize;


public class Chromosome implements Comparable {
	/**
	 * <p>
	 * It is used for representing and handling a Chromosome throughout the evolutionary learning
	 * </p>
	 */

	public Gene[] genes;
	public ArrayList<Gene> antecedent;
	public ArrayList<Gene> consequent;
	public double fitness;
	public double antsSupport;
	public double consSupport;
	public double support;
	public double confidence;
	public double percentInterval;
	public double suppFit;
	public double lift;
	public double conv;
	public double CF;
	public double netConf;
	public double yulesQ;
	public int nAnts;
	boolean n_e;
	public boolean proceed; // it has been assigned to a niche
	public boolean seed; // it is the seed in the niche
	public boolean specieChild; // it is add to a niche from a child pop
	public boolean isReinicio;
	//related with a distance between other chr, they are obtained values after we call distanceChromosome
	ArrayList<Gene> listGeneCommon1 = new ArrayList<>();
	ArrayList<Gene> listGeneCommon2 = new ArrayList<>();
	double countGenDiff;
	double distCommonGenes;
	double distCoverGenes;
	boolean tournSelect;
	double distChrToPop;
	char [] transCover;


	/**
	 * <p>
	 * It creates a new chromosome by setting up its genes
	 * </p>
	 * @param genes The array of genes that the chromosome must handle
	 */

	public Chromosome(Gene[] genes) {
		this.genes = new Gene[genes.length];
		this.antecedent = new ArrayList<Gene>();
		this.consequent = new ArrayList<Gene>();

		this.nAnts = 0;
		for (int i=0; i < genes.length; i++) {
			this.genes[i] = genes[i].copy();
			if (this.genes[i].getActAs() == Gene.ANTECEDENT){
				this.nAnts++;
				this.antecedent.add(this.genes[i].copy());
			}
			if (this.genes[i].getActAs() == Gene.CONSEQUENT){
				this.consequent.add(this.genes[i].copy());
			}
		}


		this.fitness = 0.0;
		this.support = 0.0;
		this.antsSupport = 0.0;
		this.consSupport = 0.0;
		this.confidence = 0.0;
		this.conv = 0.0;
		this.CF = 0.0;
		this.netConf = 0.0;
		this.yulesQ = 0.0;
		this.n_e = true;
		this.proceed = false;
		this.seed = false;
		this.specieChild = false;
		this.tournSelect = false;
		this.suppFit = 0.0;
		this.isReinicio = false;

	}

	public double getDistChrToPop() {
		return distChrToPop;
	}
	public void setDistChrToPop(double distChrToPop) {
		this.distChrToPop = distChrToPop;
	}
	public boolean isReinicio() {
		return isReinicio;
	}

	public void setReinicio(boolean isReinicio) {
		this.isReinicio = isReinicio;
	}

	public boolean isSeed() {
		return seed;
	}

	public void setSeed(boolean seed) {
		this.seed = seed;
	}

	/**
	 * <p>
	 * It allows to clone correctly a chromosome
	 * </p>
	 * @return A copy of the chromosome
	 */
	public Chromosome copy() {
		Chromosome chromo = new Chromosome(this.genes);

		chromo.transCover = new char[this.transCover.length];
		for (int i = 0; i< this.transCover.length; i++){
			chromo.transCover[i]=this.transCover[i];
		}
		chromo.fitness = this.fitness;
		chromo.support = this.support;
		chromo.antsSupport = this.antsSupport;
		chromo.consSupport = this.consSupport;
		chromo.confidence = this.confidence;
		chromo.CF = this.CF;
		chromo.netConf = this.netConf;
		chromo.yulesQ = this.yulesQ;
		chromo.conv = this.conv;
		chromo.lift = this.lift;
		chromo.nAnts = this.nAnts;
		chromo.n_e = this.n_e;
		chromo.proceed = this.proceed;
		chromo.specieChild = this.specieChild;
		chromo.seed = this.seed;
		chromo.tournSelect = this.tournSelect;
		chromo.suppFit= this.suppFit;
		chromo.isReinicio = this.isReinicio;
		return chromo;
	}


	public boolean isTournSelect() {
		return tournSelect;
	}

	public void setTournSelect(boolean tournSelect) {
		this.tournSelect = tournSelect;
	}

	public boolean isSpecieChild() {
		return specieChild;
	}

	public void setSpecieChild(boolean specieChild) {
		this.specieChild = specieChild;
	}

	public boolean isProceed() {
		return proceed;
	}

	public void setProceed(boolean proceed) {
		this.proceed = proceed;
	}

	/**
	 * <p>
	 * It sets the support of the association rule represented by a chromosome
	 * </p>
	 * @param support The value representing the rule support
	 */
	public void setSupport(double support) {
		this.support = support;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	/**
	 * <p>
	 * It returns the support of the association rule represented by a chromosome
	 * </p>
	 * @return A value representing the rule support
	 */
	public double getSupport() {
		return this.support;
	}

	/**
	 * <p>
	 * It returns the support of the association rule represented by a chromosome
	 * </p>
	 * @return A value representing the rule support
	 */
	public double getAntsSupport() {
		return this.antsSupport;
	}


	public double getConsSupport(){
		return this.consSupport;
	}


	/**
	 * <p>
	 * It sets the confidence of the association rule represented by a chromosome
	 * </p>
	 * @param confidence The value representing the rule confidence
	 */
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	/**
	 * <p>
	 * It returns the confidence of the association rule represented by a chromosome
	 * </p>
	 * @return A value representing the rule confidence
	 */
	public double getConfidence() {
		return this.confidence;
	}

	/**
	 * <p>
	 * It returns the genes of a chromosome
	 * </p>
	 * @return An array of genes for the chromosome being considered
	 */
	public Gene[] getGenes() {
		return this.genes;
	}

	/**
	 * <p>
	 * It returns the "i-th" gene of a chromosome
	 * </p>
	 * @param i The index of the gene
	 * @return The "i-th" gene of the chromosome being considered
	 */
	public Gene getGene(int i) {
		return this.genes[i];
	}


	/**
	 * <p>
	 * It checks whether a chromosome always contains at least one antecedent gene as well as at least one consequent gene.
	 * If not, it forces this constraint by randomly altering some of its genes
	 * </p>
	 */
	public void forceConsistency() {
		int i, n_ant, n_cons, pos, count;

		n_ant = n_cons = 0;

		for (i=0; i < genes.length; i++) {
			if (genes[i].getActAs() == Gene.ANTECEDENT)  n_ant++;
			if (genes[i].getActAs() == Gene.CONSEQUENT)  n_cons++;
		}

		if (n_cons > 1) {
			pos = Randomize.RandintClosed(1, n_cons);
			for (i=0, count=1; i < genes.length; i++) {
				if (genes[i].getActAs() == Gene.CONSEQUENT) {
					if (count != pos) {
						genes[i].setActAs(Gene.ANTECEDENT);
						n_ant++;
					}
					count++;
				}
			}
		}
		else if (n_cons < 1) {
			if (n_ant > 1) {
				pos = Randomize.RandintClosed(1, n_ant);
				for (i=0, count=1; i < genes.length && count <= pos; i++) {
					if (genes[i].getActAs() == Gene.ANTECEDENT) {
						if (count == pos) {
							genes[i].setActAs(Gene.CONSEQUENT);
							n_ant--;
						}
						count++;
					}
				}
			}
			else {
				for (pos = Randomize.Randint(0, genes.length); genes[pos].getActAs() != Gene.NOT_INVOLVED; pos = Randomize.Randint(0, genes.length));
				genes[pos].setActAs(Gene.CONSEQUENT);
			}
			n_cons++;
		}

		if (n_ant < 1) {
			for (pos = Randomize.Randint(0, genes.length); genes[pos].getActAs() != Gene.NOT_INVOLVED; pos = Randomize.Randint(0, genes.length));
			genes[pos].setActAs(Gene.ANTECEDENT);
		}
	}
	/**
	 * <p>
	 * It indicates whether some other chromosome is "equal to" this one
	 * </p>
	 * @param obj The reference object with which to compare
	 * @return True if this chromosome is the same as the argument; False otherwise
	 */
	public boolean equals(Chromosome chr) {
		int i;

		for (i=0; i < this.genes.length; i++)	{
			if ((this.genes[i].getActAs() != Gene.NOT_INVOLVED) && (chr.genes[i].getActAs() == Gene.NOT_INVOLVED))  return (false);
			if ((this.genes[i].getActAs() == Gene.NOT_INVOLVED) && (chr.genes[i].getActAs() != Gene.NOT_INVOLVED))  return (false);
			if ((this.genes[i].getActAs() != Gene.NOT_INVOLVED) && (chr.genes[i].getActAs() != Gene.NOT_INVOLVED)) {
				if (!chr.genes[i].equals(this.genes[i]))  return (false);
			}
		}

		return (true);
	}

	/**
	 * <p>
	 * It returns a string representation of a chromosome
	 * </p>
	 * @return A string representation of the chromosome
	 */
	public String toString() {

		String str = "Lift:  " + this.lift+ "; Rule Support: " + this.support + "; Rule Confidence: " + this.confidence + "\n" ;
		str += "Fitness:" + this.fitness + "\n";
		for (int i=0; i < this.genes.length; i++)
			str += this.genes[i] + "\n";

		return str;
	}


	public int isBetter (Chromosome chromo) {
		if (this.fitness > chromo.fitness)  return (1);
		else if (this.fitness < chromo.fitness)  return (-1);
		else  return (0);

	}

	/**
	 * <p>
	 * It adds a dataset records to the list of records being covered by a chromosome
	 * </p>
	 * @param tid The ID of the covered record in the dataset
	 */
	public boolean isCovered(double[] example) {
		int i;
		double lb, ub;
		boolean covered;

		covered = true;

		for (i=0; i < this.genes.length && covered; i++) {
			if (this.genes[i].getActAs() != Gene.NOT_INVOLVED)  covered = this.genes[i].isCover(i, example[i]);
		}

		return (covered);
	}

	public void setNew (boolean value) {
		this.n_e = value;
	}

	public boolean isNew () {
		return (this.n_e);
	}

	public void computeObjetives (myDataset dataset, double[] wTrans) {
		int i, j, nTrans, nVars, nCons;
		boolean antsCover, consCover;
		double [] example;
		int[] covered;
		double amp, interval, numeratorYules, denominatorYules;
		double componentSupport;

		nTrans = dataset.getnTrans();
		nVars = dataset.getnVars();
		this.antsSupport = 0.0;
		this.consSupport = 0.0;
		this.support = 0.0;
		this.confidence = 0.0;
		this.conv = 0.0;
		this.netConf = 0.0;
		this.CF = 0.0;
		this.nAnts = 0;
		this.suppFit = 0.0;
		nCons = 0;

		covered = new int[nTrans];
		this.transCover = new char[nTrans];
		for (i=0; i < nTrans; i++)  {
			covered[i] = 0;
			this.transCover[i] = '0';
		}

		for (i = 0; i < nTrans; i++) {
			example = dataset.getExample(i);
			antsCover = consCover = true;
			for (j=0; j < nVars && (antsCover || consCover); j++) {
				if (this.genes[j].getActAs() == Gene.ANTECEDENT && antsCover) {
					if (!this.genes[j].isCover(j, example[j]))  antsCover = false;

				}
				else if (this.genes[j].getActAs() == Gene.CONSEQUENT && consCover) {
					if (!this.genes[j].isCover(j, example[j]))  consCover = false;
				}
			}

			if (antsCover)  this.antsSupport++;
			if (consCover)  this.consSupport++;
			if (antsCover && consCover) {
				this.support++;
				this.suppFit+=wTrans[i];
				covered[i] = 1;
				this.transCover[i] = '1';
			}
		}

		this.antsSupport /= nTrans;
		this.consSupport /= nTrans;
		this.support /= nTrans;
		this.suppFit  /= nTrans;

		if (this.antsSupport > 0.0)  this.confidence = this.support / this.antsSupport;

		for (i=0; i < nVars; i++) {
			if (this.genes[i].getActAs() == Gene.ANTECEDENT)  this.nAnts++;
			if (this.genes[i].getActAs() == Gene.CONSEQUENT)  nCons++;
			if (this.genes[i].getActAs() != Gene.NOT_INVOLVED) {
				this.genes[i].tuneInterval(dataset, covered);
				amp = this.genes[i].getUpperBound() - this.genes[i].getLowerBound();
				interval = ( this.genes[i].getIsPositiveInterval() ) ? amp/dataset.getAmplitude(i) : (dataset.getAmplitude(i) - amp) /dataset.getAmplitude(i);
			}
		}			
		this.percentInterval  /= (this.nAnts + nCons);
		if((this.antsSupport == 0)||(this.consSupport == 0))
			this.lift = 1;
		else this.lift = this.support/(this.antsSupport * this.consSupport);

		if((this.consSupport == 1)|| (this.antsSupport == 0))
			this.conv = 1;
		else 
			this.conv = (this.antsSupport*(1-this.consSupport))/(this.antsSupport-this.support);

		if ((this.antsSupport == 0)||(this.antsSupport == 1)||(Math.abs((this.antsSupport * (1-this.antsSupport))) <= 0.001))
			this.netConf = 0;
		else
			this.netConf = (this.support - (this.antsSupport*this.consSupport))/(this.antsSupport * (1-this.antsSupport));

		//compute yulesQ
		numeratorYules = ((this.support * (1 - this.consSupport - this.antsSupport + this.support)) - ((this.antsSupport - this.support)* (this.consSupport - this.support)));
		denominatorYules = ((this.support * (1 - this.consSupport - this.antsSupport + this.support)) + ((this.antsSupport - this.support)* (this.consSupport - this.support)));

		if ((this.antsSupport == 0)||(this.antsSupport == 1)|| (this.consSupport == 0)||(this.consSupport == 1)||(Math.abs(denominatorYules) <= 0.001))
			this.yulesQ = 0;
		else this.yulesQ = numeratorYules/denominatorYules;		

		this.CF = 0;
		if(this.confidence > this.consSupport)
			this.CF = (this.confidence - this.consSupport)/(1-this.consSupport);	
		else 
			if(this.confidence < this.consSupport)
				this.CF = (this.confidence - this.consSupport)/(this.consSupport);	

		componentSupport = (1 - (1.0 / (Math.pow(2, 10*this.support)))); 
		this.fitness = componentSupport*(1.0 - 1.0/this.lift) + this.netConf + 1.0/(this.nAnts*2);

		this.n_e = false;
	}

	public double getLift() {
		return lift;
	}

	public void setLift(double lift) {
		this.lift = lift;
	}

	public boolean isSubChromo (Chromosome chromo2) {
		int i;
		Gene gen;

		if (this.getSupport() < chromo2.getSupport())  return (false);

		for (i=0; i < genes.length; i++) {
			gen = chromo2.getGene(i);

			if ((genes[i].getActAs() != Gene.NOT_INVOLVED) && (gen.getActAs() == Gene.NOT_INVOLVED))  return (false);
			if ((genes[i].getActAs() == Gene.NOT_INVOLVED) && (gen.getActAs() != Gene.NOT_INVOLVED))  return (false);
			if ((genes[i].getActAs() != Gene.NOT_INVOLVED) && (gen.getActAs() != Gene.NOT_INVOLVED)) {
				if (!genes[i].isSubGen(gen))  return (false);
			}
		}

		return (true);
	}

	public double getPercentInterval() {
		return percentInterval;
	}

	public void setPercentInterval(double percentInterval) {
		this.percentInterval = percentInterval;
	}

	public void proceedCommonDiffGene(Chromosome chr){
		int i; 
		this.countGenDiff = 0;
		this.listGeneCommon1.clear();
		this.listGeneCommon2.clear();

		//calculate number of genes matchs in both chromosomes and number of genes different.     		
		for(i = 0; i < this.genes.length; i++){
			if ((this.genes[i].getActAs() != Gene.NOT_INVOLVED) && (chr.genes[i].getActAs() != Gene.NOT_INVOLVED)) {
				this.listGeneCommon1.add(this.genes[i]);
				this.listGeneCommon2.add(chr.genes[i]);
			}
			else {
				if ((this.genes[i].getActAs() != Gene.NOT_INVOLVED) && (chr.genes[i].getActAs() == Gene.NOT_INVOLVED)){
					this.countGenDiff++;
			
				}
				else if ((this.genes[i].getActAs() == Gene.NOT_INVOLVED) && (chr.genes[i].getActAs() != Gene.NOT_INVOLVED))  {
					this.countGenDiff++;
					
				}
			}
		}
	}

	public double distanceCommonGenes(ArrayList<Gene> listGeneCommon1, ArrayList<Gene> listGeneCommon2){
		int i;
		double distCommonGenes = 0.0;
		//calculate distance between each common attr
		if(!listGeneCommon1.isEmpty()){
			for(i=0; i < listGeneCommon1.size(); i++){
				distCommonGenes+= listGeneCommon1.get(i).distanceGenMaxOverlap(listGeneCommon2.get(i));
			}

			//distance between all genes is the average between all each distance.
			distCommonGenes =  distCommonGenes/listGeneCommon1.size();

			return distCommonGenes;
		}
		else return 1;

	}

	public double getDistCoverGenes() {
		return distCoverGenes;
	}

	public void setDistCoverGenes(double distCoverGenes) {
		this.distCoverGenes = distCoverGenes;
	}

	public double distanceChromosome(Chromosome chr){
		this.proceedCommonDiffGene(chr);
		this.distCommonGenes = this.distanceCommonGenes(this.listGeneCommon1, this.listGeneCommon2); 

		this.distCoverGenes = this.distCover(chr);

		return (this.distCommonGenes + this.distCoverGenes)/2;

	}

	public double distCover(Chromosome chr){

		/*** calculando cantidad de tuplas que cubre cada regla y la cantidad común que cubren las dos*/
		boolean coverR1, coverR2; // R1 sería la regla actual this.  y R2 la regla que se pasa por parámetro
		double cCoverBoth = 0.0, cCoverR1 = 0.0, cCoverR2 = 0.0;
		for (int i=0; i< this.transCover.length; i++){
			coverR1 = false;
			coverR2 = false;
			if(this.transCover[i] == '1'){ // la regla 1 cubre este ejemplo
				cCoverR1++;
				coverR1 = true;
			}

			if(chr.getTransCover()[i] == '1'){ // la regla 2 cubre este ejemplo
				cCoverR2++;
				coverR2 = true;
			}

			if((coverR1) && (coverR2)){
				cCoverBoth++;
			}
		}

		/*** variante buscando máximo entre proporción de tuplas q cubran ambas reglas 
		 * entre las que cubre cada una (2da variante)*/

		double distCover = 1.0- Math.max((cCoverBoth/cCoverR1), (cCoverBoth/cCoverR2));
		return distCover;
	}

	public char[] getTransCover() {
		return transCover;
	}

	public void setTransCover(char[] transCover) {
		this.transCover = transCover;
	}

	public ArrayList<Gene> getListGeneCommon1() {
		return listGeneCommon1;
	}

	public void setListGeneCommon1(ArrayList<Gene> listGeneCommon1) {
		this.listGeneCommon1 = listGeneCommon1;
	}

	public ArrayList<Gene> getListGeneCommon2() {
		return listGeneCommon2;
	}

	public void setListGeneCommon2(ArrayList<Gene> listGeneCommon2) {
		this.listGeneCommon2 = listGeneCommon2;
	}

	public double getDistCommonGenes() {
		return distCommonGenes;
	}

	public void setDistCommonGenes(double distCommonGenes) {
		this.distCommonGenes = distCommonGenes;
	}

	public int getnAnts () {
		return (this.nAnts);
	}

	public void setnAnts (int value) {
		this.nAnts = value;
	}


	/**
	 * <p>
	 * It compares a chromosome with another one in order to accomplish ordering later.
	 * The comparison is achieved by only considering objectives values.
	 * For this reason, note that this method provides a natural ordering that is inconsistent with equals
	 * </p>
	 * @param obj The object to be compared
	 * @return A negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object
	 */
	public ArrayList<Integer> getCoveredTIDs (myDataset dataset) {
		int i;
		double[] example;
		ArrayList<Integer> TIDs;

		TIDs = new ArrayList<Integer>();

		for (i=0; i < dataset.getnTrans(); i++) {
			example = dataset.getExample(i);
			if (this.isCovered(example))  TIDs.add(i);
		}

		return (TIDs);
	}


	public int compareTo(Object chr) {
		if (((Chromosome) chr).fitness < this.fitness)	return -1;
		else if (((Chromosome) chr).fitness >  this.fitness) return 1;
		else  return 0;
	}

	public double getCF() {
		return CF;
	}

	public void setCF(double cf) {
		CF = cf;
	}

	public double getConv() {
		return conv;
	}

	public void setConv(double conv) {
		this.conv = conv;
	}

	public double getNetConf() {
		return netConf;
	}

	public void setNetConf(double netConf) {
		this.netConf = netConf;
	}

	public double getYulesQ() {
		return yulesQ;
	}

	public ArrayList<Gene> getAntecedent() {
		return antecedent;
	}

	public ArrayList<Gene> getConsequent() {
		return consequent;
	}
}
