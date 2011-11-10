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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.Alatasetal;

/**
 * <p>
 * @author Written by Nicolò Flugy Papè (Politecnico di Milano) 24/03/2009
 * @version 1.0
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
	
	private Gene[] genes;
	private double fitness;
	private double ruleSupport;
	private double ruleConfidence;
	private ArrayList<Integer> coveredTIDs;
	
	/**
	 * <p>
	 * It creates a new chromosome by setting up its genes
	 * </p>
	 * @param genes The array of genes that the chromosome must handle
	 */
	public Chromosome(Gene[] genes) {
		this.genes = new Gene[genes.length];
		
		for (int i=0; i < genes.length; i++)
			this.genes[i] = genes[i].copy();
			
		this.coveredTIDs = new ArrayList<Integer>();
	}

	/**
	 * <p>
	 * It allows to clone correctly a chromosome
	 * </p>
	 * @return A copy of the chromosome
	 */
	public Chromosome copy() {
		Chromosome chromo = new Chromosome(this.genes);
		
		for (int i=0; i < this.coveredTIDs.size(); i++)
			chromo.addCoveredTID( this.coveredTIDs.get(i) );
		
		chromo.fitness = this.fitness;
		chromo.ruleSupport = this.ruleSupport;
		chromo.ruleConfidence = this.ruleConfidence;
		
		return chromo;
	}

	/**
	 * <p>
	 * It sets the fitness for a chromosome
	 * </p>
	 * @param fitness The fitness value of the chromosome
	 */
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	/**
	 * <p>
	 * It returns the fitness of a chromosome
	 * </p>
	 * @return The fitness value of the chromosome
	 */
	public double getFitness() {
		return this.fitness;
	}

	/**
	 * <p>
	 * It sets the support of the association rule represented by a chromosome
	 * </p>
	 * @param ruleSupport The value representing the rule support
	 */
	public void setRuleSupport(double ruleSupport) {
		this.ruleSupport = ruleSupport;
	}
	
	/**
	 * <p>
	 * It returns the support of the association rule represented by a chromosome
	 * </p>
	 * @return A value representing the rule support
	 */
	public double getRuleSupport() {
		return this.ruleSupport;
	}
	
	/**
	 * <p>
	 * It sets the confidence of the association rule represented by a chromosome
	 * </p>
	 * @param ruleConfidence The value representing the rule confidence
	 */
	public void setRuleConfidence(double ruleConfidence) {
		this.ruleConfidence = ruleConfidence;
	}
	
	/**
	 * <p>
	 * It returns the confidence of the association rule represented by a chromosome
	 * </p>
	 * @return A value representing the rule confidence
	 */
	public double getRuleConfidence() {
		return this.ruleConfidence;
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
	 * It indicates the genes which are involved to form an association rule later
	 * </p>
	 * @return An array of IDs for the involved genes
	 */
	public ArrayList<Integer> getIndexOfInvolvedGenes() {
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		
		for (int i=0; i < this.genes.length; i++)
			if ( this.genes[i].getActAs() != Gene.NOT_INVOLVED ) indexes.add(i);
		
		return indexes;
	}
	
	/**
	 * <p>
	 * It indicates the genes which are excluded to form an association rule later
	 * </p>
	 * @return An array of IDs for the excluded genes
	 */
	public ArrayList<Integer> getIndexOfNotInvolvedGenes() {
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		
		for (int i=0; i < this.genes.length; i++)
			if ( this.genes[i].getActAs() == Gene.NOT_INVOLVED ) indexes.add(i);
		
		return indexes;
	}

	/**
	 * <p>
	 * It indicates the genes which act as antecedents within a chromosome
	 * </p>
	 * @return An array of IDs for the genes acting as antecedents
	 */
	public ArrayList<Integer> getIndexOfAntecedentGenes() {
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		
		for (int i=0; i < this.genes.length; i++)
			if ( this.genes[i].getActAs() == Gene.ANTECEDENT ) indexes.add(i);
		
		return indexes;
	}
	
	/**
	 * <p>
	 * It indicates the genes which act as consequents within a chromosome
	 * </p>
	 * @return An array of IDs for the genes acting as consequents
	 */	
	public ArrayList<Integer> getIndexOfConsequentGenes() {
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		
		for (int i=0; i < this.genes.length; i++)
			if ( this.genes[i].getActAs() == Gene.CONSEQUENT ) indexes.add(i);
		
		return indexes;		
	}
	
	/**
	 * <p>
	 * It adds a dataset records to the list of records being covered by a chromosome
	 * </p>
	 * @param tid The ID of the covered record in the dataset
	 */
	public void addCoveredTID(int tid) {
		this.coveredTIDs.add(tid);
	}
	
	/**
	 * <p>
	 * It indicates the dataset records which have been covered by a chromosome
	 * </p>
	 * @return An array of IDs representing the covered records in the dataset
	 */
	public ArrayList<Integer> getCoveredTIDs() {
		return this.coveredTIDs;
	}
	
	/**
	 * <p>
	 * It checks whether a chromosome always contains at least one antecedent gene as well as at least one consequent gene.
	 * If not, it forces this constraint by randomly altering some of its genes
	 * </p>
	 */
	public void forceConsistency() {
		int n_not_involved, n_ant, n_cons, g_ant, g_cons;
		ArrayList<Integer> not_involved_attrs;
		 
		not_involved_attrs = this.getIndexOfNotInvolvedGenes();
		n_not_involved = not_involved_attrs.size();
		n_ant = this.getIndexOfAntecedentGenes().size();
		n_cons = this.getIndexOfConsequentGenes().size();
		
		if (n_ant == 0) {
			if (n_cons > 0) {
				if (n_not_involved == 0) {
					g_ant = Randomize.Randint(0, this.genes.length);
					this.genes[g_ant].setActAs(Gene.ANTECEDENT);
				}
				else {
					g_ant = not_involved_attrs.get( Randomize.Randint(0, n_not_involved) );
					this.genes[g_ant].setActAs(Gene.ANTECEDENT);
				}
			}
			else {
				g_ant = Randomize.Randint(0, this.genes.length);
				
				do {
					g_cons = Randomize.Randint(0, this.genes.length);
				}
				while (g_cons == g_ant);
				
				this.genes[g_ant].setActAs(Gene.ANTECEDENT);
				this.genes[g_cons].setActAs(Gene.CONSEQUENT);
			}
		}
		else {
			if (n_cons == 0) {
				if (n_not_involved == 0) {
					g_cons = Randomize.Randint(0, this.genes.length);
					this.genes[g_cons].setActAs(Gene.CONSEQUENT);
				}
				else {
					g_cons = not_involved_attrs.get( Randomize.Randint(0, n_not_involved) );
					this.genes[g_cons].setActAs(Gene.CONSEQUENT);
				}
			}
		}
	}
	
	/**
	 * <p>
	 * It compares a chromosome with another one in order to accomplish ordering later.
	 * The comparison is achieved by only considering fitness values.
	 * For this reason, note that this method provides a natural ordering that is inconsistent with equals
	 * </p>
	 * @param chr The object to be compared
	 * @return A negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object
	 */
	public int compareTo(Object chr) {
		if (((Chromosome) chr).fitness < this.fitness)	return -1;
			else if (((Chromosome) chr).fitness > this.fitness)	return 1;
		
		return 0;
	}
	
	/**
	 * <p>
	 * It indicates whether some other chromosome is "equal to" this one
	 * </p>
	 * @param obj The reference object with which to compare
	 * @return True if this chromosome is the same as the argument; False otherwise
	 */
	public boolean equals(Object obj) {
		Chromosome chr = (Chromosome)obj;
		boolean ok = true;
		
		for (int i=0; i < this.genes.length && ok; i++)
			if ( ! chr.genes[i].equals( this.genes[i] ) )	ok = false;
		
		return ok;
	}
	
	/**
	 * <p>
	 * It returns a string representation of a chromosome
	 * </p>
	 * @return A string representation of the chromosome
	 */
	public String toString() {
		String str = "Fitness: " + this.fitness + "; Rule Support: " + this.ruleSupport + "; Rule Confidence: " + this.ruleConfidence + "\n" ;
		
		for (int i=0; i < this.genes.length; i++)
			str += this.genes[i] + "\n";
		
		str += "Covered TIDs: " + this.coveredTIDs;
		
		return str;
	}
	
}
