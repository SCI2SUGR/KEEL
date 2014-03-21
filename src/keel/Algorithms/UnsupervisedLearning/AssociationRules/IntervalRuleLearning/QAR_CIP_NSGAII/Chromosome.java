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

import java.util.ArrayList;
import org.core.Randomize;


public class Chromosome implements Comparable {
	/**
	 * <p>
	 * It is used for representing and handling a Chromosome throughout the evolutionary learning
	 * </p>
	 */

	public Gene[] genes;
	public double[] objectives;
	public int numObjectives;
	public double antsSupport;
	public double consSupport;
	public double support;
	public double confidence;
	public double sumInterval;
	public double conv;
	public double CF;
	public double netConf;
	public double yulesQ;
	public int nAnts;
	boolean n_e;
	int rank;
	double crowd_dist;


	/**
	 * <p>
	 * It creates a new chromosome by setting up its genes
	 * </p>
	 * @param genes The array of genes that the chromosome must handle
	 */

	public Chromosome(Gene[] genes, int numObjectives) {
		this.genes = new Gene[genes.length];

		this.nAnts = 0;
		for (int i=0; i < genes.length; i++) {
			this.genes[i] = genes[i].copy();
			if (this.genes[i].getActAs() == Gene.ANTECEDENT)  this.nAnts++;
		}

		this.numObjectives = numObjectives;
		this.objectives = new double[3];
		this.support = 0.0;
		this.antsSupport = 0.0;
		this.consSupport = 0.0;
		this.confidence = 0.0;
		this.conv = 0.0;
		this.CF = 0.0;
		this.netConf = 0.0;
		this.yulesQ = 0.0;
		this.sumInterval = 0.0;
		this.rank = -1;
		this.crowd_dist = 0.0;
		this.n_e = true;
	}

	/**
	 * <p>
	 * It allows to clone correctly a chromosome
	 * </p>
	 * @return A copy of the chromosome
	 */
	public Chromosome copy() {
		Chromosome chromo = new Chromosome(this.genes, this.numObjectives);

		for(int i=0; i < this.numObjectives; i++)  chromo.objectives[i] = this.objectives[i];

		chromo.support = this.support;
		chromo.antsSupport = this.antsSupport;
		chromo.consSupport = this.consSupport;
		chromo.confidence = this.confidence;
		chromo.CF = this.CF;
		chromo.netConf = this.netConf;
		chromo.yulesQ = this.yulesQ;
		chromo.conv = this.conv;
		chromo.nAnts = this.nAnts;
		chromo.sumInterval = this.sumInterval;
		chromo.rank = this.rank;
		chromo.crowd_dist = this.crowd_dist;
		chromo.n_e = this.n_e;

		return chromo;
	}

	/**
	 * <p>
	 * It sets the objectives for a chromosome
	 * </p>
	 * @param objectives The objectives value of the chromosome
	 */
	public void setObjetives(double[] objectives) {
		this.objectives = objectives;
	}

	/**
	 * <p>
	 * It returns the objectives of a chromosome
	 * </p>
	 * @return The objectives value of the chromosome
	 */
	public double[] getObjectives() {
		return objectives;
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

		String str = "Rank:  " + this.rank + "Lift:  " + this.objectives[0]+ "; Rule Support: " + this.support + "; Rule Confidence: " + this.confidence + "\n" ;
		for( int j=0; j < this.numObjectives; j++)
			str += "Objetives:" + "["+ j + "]"+ this.objectives[j] + "\n";
		for (int i=0; i < this.genes.length; i++)
			str += this.genes[i] + "\n";

		return str;
	}


	public double getObjective(int num) {
		if ((num < 0) || (num > this.numObjectives))  return (-1);
		return (this.objectives[num]);
	}

	public int isBetter (Chromosome chromo) {
		if (this.rank < chromo.rank)  return (1);
		else if (this.rank > chromo.rank)  return (-1);
		else if (this.crowd_dist > chromo.crowd_dist)  return (1);
		else if (this.crowd_dist < chromo.crowd_dist)  return (-1);
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

	public void computeObjetives (myDataset dataset) {
		int i, j, nTrans, nVars, nCons;
		boolean antsCover, consCover;
		double [] example;
		int[] covered;
		double lift, numeratorYules, denominatorYules;

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
		this.sumInterval = 0.0;
		nCons = 0;

		covered = new int[nTrans];
		for (i=0; i < nTrans; i++)  covered[i] = 0;
		for (i=0; i < this.numObjectives; i++)  this.objectives[i] = 0.0;

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
				covered[i] = 1;
			}
		}

		this.antsSupport /= nTrans;
		this.consSupport /= nTrans;
		this.support /= nTrans;
		if (this.antsSupport > 0.0)  this.confidence = this.support / this.antsSupport;

		for (i=0; i < nVars; i++) {
			if (this.genes[i].getActAs() == Gene.ANTECEDENT)  this.nAnts++;
			if (this.genes[i].getActAs() == Gene.CONSEQUENT)  nCons++;
			if (this.genes[i].getActAs() != Gene.NOT_INVOLVED) {
				this.genes[i].tuneInterval(dataset, covered);
				this.sumInterval += ((this.genes[i].getUpperBound() - this.genes[i].getLowerBound()) / dataset.getAmplitude(i));
			}
		}			
		this.sumInterval /= (this.nAnts + nCons);

		//compute lift
		if((this.antsSupport == 0)||(this.consSupport == 0))
			lift = 1;
		else lift = this.support/(this.antsSupport * this.consSupport);

		//compute conviction
		if((this.consSupport == 1)|| (this.antsSupport == 0))
			this.conv = 1;
		else 
			this.conv = (this.antsSupport*(1-this.consSupport))/(this.antsSupport-this.support);

		//compute netconf
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

		//compute Certain Factor (CF)
		this.CF = 0;
		if(this.confidence > this.consSupport)
			this.CF = (this.confidence - this.consSupport)/(1-this.consSupport);	
		else 
			if(this.confidence < this.consSupport)
				this.CF = (this.confidence - this.consSupport)/(this.consSupport);	

		if (this.numObjectives > 0) this.objectives[0] = lift;	// lift 
		if (this.numObjectives > 1) this.objectives[1] = this.CF * this.support;  // performance
		if (this.numObjectives > 2) this.objectives[2] = 1.0 / this.nAnts;  // number of variables in the antecedent

		this.n_e = false;
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

	public void calculateSumInterval(myDataset dataset) {
		int i, nVars, nCons;

		nVars = dataset.getnVars(); 
		nCons = 0;

		this.sumInterval = 0.0;

		for (i=0; i < nVars; i++) {
			if (this.genes[i].getActAs() == Gene.CONSEQUENT)  nCons++;
			if (this.genes[i].getActAs() != Gene.NOT_INVOLVED)  this.sumInterval += ((this.genes[i].getUpperBound() - this.genes[i].getLowerBound()) / dataset.getAmplitude(i));
		}			

		this.sumInterval /= (this.nAnts + nCons);
	}

	public int getnAnts () {
		return (this.nAnts);
	}

	public void setnAnts (int value) {
		this.nAnts = value;
	}

	public int getRank () {
		return (this.rank);
	}

	public double getSumInterval () {
		return (this.sumInterval);
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
		if (((Chromosome) chr).nAnts > this.nAnts)	return -1;
		else if (((Chromosome) chr).nAnts <  this.nAnts)	return 1;
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
}
