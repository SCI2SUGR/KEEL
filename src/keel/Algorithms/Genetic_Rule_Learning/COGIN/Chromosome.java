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

package keel.Algorithms.Genetic_Rule_Learning.COGIN;

import org.core.Randomize;
import keel.Dataset.Instance;


/**
 * <p>
 * This class implements a binary chromosome as specified in the COGIN algorithm
 * </p>
 * 
 * <p>
 * @author Written by Julián Luengo Martín 08/02/2007
 * @version 0.2
 * @since JDK 1.5
 * </p>
 */
public class Chromosome implements Comparable{
	Gene rule[];
	int numGenes;
	int totalBits;
	int _class;
	int coveredInstances;
	double fitness;
	
	/**
	 * <p>
	 * Default constructor
	 * </p>
	 */
	public Chromosome(){
		rule = null;
		numGenes = 0;
		_class = -1;
		totalBits = 0;
	}
	
	/**
	 * <p>
	 * Builds up a new chromosome with specified number of genes
	 * </p>
	 * @param nGenes number of genes of this chromosome
	 */
	public Chromosome(int nGenes){
		numGenes = nGenes;
		rule = new Gene[numGenes];
		_class = -1;
		totalBits = 0;
	}
	
	/**
	 * <p>
	 * Copy constructor. Performs a deep-copy
	 * </p>
	 * @param ch the original chromosome
	 */
	public Chromosome(Chromosome ch){
		this.numGenes = ch.numGenes;
		this.totalBits = ch.totalBits;
		this._class = ch._class;
		this.coveredInstances = ch.coveredInstances;
		this.fitness = ch.fitness;
		this.rule = new Gene[ch.rule.length];
		for(int i=0;i<ch.rule.length;i++){
			this.rule[i] = new Gene(ch.rule[i]);
		}
	}
	
	/**
	 * <p>
	 * Gets the class output associated to this individual
	 * </p>
	 * @return the index of the class associated
	 */
	public int getClas(){
		return _class;
	}
	
	/**
	 * <p>
	 * Gives the fitness of the chromosome
	 * </p>
	 * @return the current fitness
	 */
	public double getFitness(){
		return fitness;
	}
	
	/**
	 * <p>
	 * Gets the gene at specified position
	 * </p>
	 * @param whichGene the index of the gene
	 * @return the value of the indicated gene
	 */
	public Gene getGene(int whichGene){
		return rule[whichGene];
	}
	
	/**
	 * <p>
	 * the number of genes of this individual
	 * </p>
	 * @return the number of genes
	 */
	public int getNumGenes(){
		return numGenes;
	}
	
	/**
	 * <p>
	 * Gets the number of instances covered by this chromosome
	 * </p>
	 * @return the number of instances covered
	 */
	public int getCoveredInstances(){
		return coveredInstances;
	}
	
	/**
	 * <p>
	 * Set the class of this chromosome
	 * </p>
	 * @param c the new class
	 */
	public void setClass(int c){
		_class = c;
	}
	
	/**
	 * <p>
	 * Set the value of the specified gene
	 * </p>
	 * @param pos the index of the gene to be changed
	 * @param gen The new gene value
	 */
	public void setGene(int pos,Gene gen){
		if(rule[pos]!=null)
			totalBits -= rule[pos].getNumBits();
		rule[pos] = gen;
		totalBits += rule[pos].getNumBits();
	}
	
	/**
	 * <p>
	 * Set the fitness of this individual
	 * </p>
	 * @param fit the new fitness
	 */
	public void setFitness(double fit){
		fitness = fit;
	}
	
	/**
	 * <p>
	 * Set the number of covered instances
	 * </p>
	 * @param cov the new count of covered instances
	 */
	public void setCoveredInstances(int cov){
		coveredInstances = cov;
	}
	
	/**
	 * <p>
	 * Test if the instances is covered by the rule
	 * </p>
	 * @param inst The instance to be tested
	 * @return True if it is covered by this rule, False if not
	 */
	public boolean covers(Instance inst){
		boolean covered;
		
		covered = true;
		//conjunctive rule, so all Genes must match
		for(int i=0;i<numGenes && covered;i++){
			covered = rule[i].test(inst.getAllInputValues()[i]);
		}
		return covered;
	}
	
	/**
	 * <p>
	 * Forces this individual to cover the provided instance
	 * </p>
	 * @param inst the instance to be covered
	 */
	public void makeCover(Instance inst){
		boolean covered;
		
		for(int i=0;i<numGenes;i++){
			covered = rule[i].test(inst.getAllInputValues()[i]);
			if(!covered){
				rule[i].applydiffs(inst.getAllInputValues()[i]);
			}
		}
		if(_class != (int)inst.getAllOutputValues()[0])
			_class = (int)inst.getAllOutputValues()[0];
	}
	
	/**
	 * <p>
	 * Crossover between chromosomes, using the swap from one point approach
	 * </p>
	 * @param ch the chromosome to be coupled with
	 */
	public void swapOnePoint(Chromosome ch){
		int swapPoint,actualPoint,i,tmp;
		Gene gen;
		//count the feature bits -totalBits- and the unique negation bits per gene -numGenes-
		swapPoint = Randomize.Randint(0, totalBits+numGenes);
		
		actualPoint = rule[0].getNumBits();
		//swap complete genes between the parents
		for(i=0;i<numGenes && actualPoint<swapPoint;i++){
			gen = this.rule[i];
			this.rule[i] = ch.rule[i];
			ch.rule[i] = gen;
			actualPoint = actualPoint + rule[i+1].getNumBits() + 1;
		}
		//swap the partial gene if needed
		actualPoint = actualPoint - rule[i].getNumBits() - 1;
		actualPoint = swapPoint - actualPoint;
		if(actualPoint >= 1){
			tmp = this.rule[i].getNegationBit();
			this.rule[i].setNegation(ch.rule[i].getNegationBit());
			ch.rule[i].setNegation(tmp);
			actualPoint--;
		}
		for(int j=0;j<actualPoint;j++){
			tmp = this.rule[i].getBit(j);
			this.rule[i].setBit(j, ch.rule[i].getBit(j));
			ch.rule[i].setBit(j,(char)tmp);
		}
	}
	
	/**
	 * <p>
	 * classifies the provided instance with this chromosome
	 * </p>
	 * @param inst the instance to be classified
	 * @return the predicted class (the class of the chromosome if covered, -1 if not)
	 */
	public int classify(Instance inst){
		boolean covered;
		
		covered = true;
		//conjunctive rule, so all Genes must match
		for(int i=0;i<numGenes && covered;i++){
			covered = rule[i].test(inst.getAllInputValues()[i]);
		}
		if(covered)
			return _class;
		else
			return -1;
	}
	
	/**
	 * Implements the Comparator method to sort chromosomes by
	 * its fitness.
	 */
	public int compareTo(Object o){
		Chromosome ch = (Chromosome) o;
		
		if(this.fitness > ch.fitness)
			return 1;
		if(this.fitness < ch.fitness)
			return -1;
		return 0;
	}

}

