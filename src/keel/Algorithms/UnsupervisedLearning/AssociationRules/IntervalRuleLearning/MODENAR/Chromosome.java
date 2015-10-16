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

import java.util.*;


public class Chromosome {
	private ArrayList<Gene> genes;
	private double[] fitness;
	private double ruleSupport;
	private double ruleConfidence;
	int rank;
	boolean tournSelect;

	
	public double getRuleConfidence() {
		return ruleConfidence;
	}
	public void setRuleConfidence(double ruleConfidence) {
		this.ruleConfidence = ruleConfidence;
	}
	
	public double getRuleSupport() {
		return ruleSupport;
	}
	public void setRuleSupport(double ruleSupport) {
		this.ruleSupport = ruleSupport;
	}
	public Chromosome()
	{
		this.genes = new ArrayList<Gene>();
	}
	public Chromosome(ArrayList<Gene> pgenes) {
		// TODO Auto-generated constructor stub
		this.genes = new ArrayList<Gene>();
		for(int i=0;i<pgenes.size();i++)  this.genes.add((pgenes.get(i)).copy());
	}

	public Chromosome copy() {
		Chromosome chromo = new Chromosome(this.genes);
		double[] fitness_copy = new double[4];

		for(int i=0; i < 4; i++)  fitness_copy[i] = this.fitness[i];

		chromo.ruleSupport = this.ruleSupport;
		chromo.ruleConfidence = this.ruleConfidence;
		chromo.setFitness(fitness_copy);

		return  chromo;
	}

	public boolean equals(Object obj) {
		Chromosome chr = (Chromosome)obj;
		boolean ok = true;

		for (int i=0; i < this.genes.size() && ok; i++)
			if ( ! chr.getGenes().get(i).equals( this.genes.get(i)) )	ok = false;

		return ok;
	}

	public double[] getFitness() {
		return fitness;
	}


	public void setFitness(double[] fitness) {
		this.fitness = fitness;
	}


	public ArrayList<Gene> getGenes() {
		return genes;
	}


	public void setGenes(ArrayList<Gene> genes) {
		this.genes = genes;
	}
	public String toString() {
		String str = "Size: " + this.genes.size() + "\n";
		for (int i=0; i < this.fitness.length; i++)  str += "; Fit: " + i + " " + this.fitness[i] + "\n";
		for (int i=0; i < this.genes.size(); i++)  str += this.genes.get(i) + "\n";

		return str;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}

}
