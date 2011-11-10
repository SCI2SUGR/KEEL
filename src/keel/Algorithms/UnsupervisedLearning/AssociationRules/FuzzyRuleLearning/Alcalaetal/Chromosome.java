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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.Alcalaetal;

/**
 * <p>
 * @author Written by Alvaro Lopez
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

public class Chromosome implements Comparable {
	/**
	 * <p>
	 * It is used for representing and handling a chromosome throughout the evolutionary learning
	 * </p>
	 */
	
	private Gene[] genes;
	private double fitness;
	private double suitability;
	private double sumFuzzySupport;
	private int numOneFrequentItemsets;
	

	/**
	 * <p>
	 * It creates a new chromosome by setting up its genes
	 * </p>
	 * @param genes The array of genes that the chromosome must handle
	 */
	public Chromosome(Gene[] genes) {
		this.setGenes(genes);
	}
		
	private void setGenes(Gene[] genes) {
		this.genes = new Gene[ genes.length ];
		
		for (int i=0; i < this.genes.length; i++)
			this.genes[i] = genes[i].clone();
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
	 * It returns the fitness of a chromosome
	 * </p>
	 * @return The fitness value of the chromosome
	 */
	public double getFitness() {
		return this.fitness;
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
	 * It returns the suitability of a chromosome
	 * </p>
	 * @return The suitability value of the chromosome
	 */
	public double getSuitability() {
		return this.suitability;
	}

	/**
	 * <p>
	 * It sets the suitability for a chromosome
	 * </p>
	 * @param suitability The suitability value of the chromosome
	 */
	public void setSuitability(double suitability) {
		this.suitability = suitability;
	}
	
	/**
	 * <p>
	 * It returns the sum of fuzzy supports of the 1-Frequent Itemsets covered by a chromosome
	 * </p>
	 * @return The sum of fuzzy supports of the 1-Frequent Itemsets covered by the chromosome
	 */
	public double getSumFuzzySupport() {
		return this.sumFuzzySupport;
	}

	/**
	 * <p>
	 * It sets the sum of fuzzy supports of the 1-Frequent Itemsets covered by a chromosome
	 * </p>
	 * @param sumFuzzySupport The sum of fuzzy supports of the 1-Frequent Itemsets covered by the chromosome
	 */
	public void setSumFuzzySupport(double sumFuzzySupport) {
		this.sumFuzzySupport = sumFuzzySupport;
	}
	
	/**
	 * <p>
	 * It returns the number of 1-frequent itemsets of a chromosome
	 * </p>
	 * @return The number of 1-frequent itemsets of the chromosome
	 */
	public int getNumOneFrequentItemsets() {
		return numOneFrequentItemsets;
	}

	/**
	 * <p>
	 * It sets the number of 1-Frequent Itemsets for a chromosome
	 * </p>
	 * @param numOneFrequentItemsets The number of 1-Frequent Itemsets of the chromosome
	 */
	public void setNumOneFrequentItemsets(int numOneFrequentItemsets) {
		this.numOneFrequentItemsets = numOneFrequentItemsets;
	}
	
	/**
	 * <p>
	 * It compares a chromosome with another one in order to accomplish ordering (NOT ascending) later.
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
			if (! chr.genes[i].equals( this.genes[i] ))	ok = false;
		
		return ok;
	}
	
	/**
	 * <p>
	 * It returns a raw string representation of a chromosome
	 * </p>
	 * @return A raw string representation of the chromosome
	 */
	public String toString() {
		int i;
		String str = "";
		
		for (i=0; i < this.genes.length - 1; i++) {
			str += this.genes[i] + "\n";
		}
		
		str += this.genes[i] + "\nFitness: " + this.fitness + "; Sum of the fuzzy support: " + this.sumFuzzySupport + "; Suitability: " + this.suitability + "; Number of 1-Frequent Itemsets: " + this.numOneFrequentItemsets;
		
		return str;
	}
	
}