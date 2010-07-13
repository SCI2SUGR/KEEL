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

/**
 * <p>
 * @author Written by Julián Luengo Martín 14/02/2007
 * @version 0.1
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Genetic_Rule_Learning.CORE;

import java.util.ArrayList;
import java.util.Collections;

import org.core.Randomize;

import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.InstanceSet;

/**
 * <p>
 * This class implements the cooperative-competitive rule-based scheme of
 * the CORE algorithm 
 * </p>
 */
public class Cochromosome{
	int numRules;
	ArrayList<Chromosome> rules;
	double fitness;
	boolean evaluated;
	int nGenes;
	int Mu;

	/**
	 * <p>
	 * default constructor. Allocates new memory for the arrays
	 * </p>
	 */
	public Cochromosome(){
		rules = new ArrayList<Chromosome>();
		numRules = 0;
		evaluated = false;
		nGenes = 0;
		Mu = 0;
	}
	
	/**
	 * <p>
	 * Deep-copy constructor (but the arraylist of chromosomes
	 * only copies the references to the chromosome objects!)
	 * </p>
	 * @param c
	 */
	public Cochromosome(Cochromosome c){
		numRules = c.numRules;
		rules = new ArrayList<Chromosome>(c.rules);
		fitness = c.fitness;
		evaluated = c.evaluated;
		nGenes = c.nGenes;
		Mu = c.Mu;
	}
	
	/**
	 * <p>
	 * Adds a chromosome to this list
	 * </p>
	 * @param c the new chromosome
	 */
	public void addChromosome(Chromosome c){
		rules.add(c);
		numRules++;
		nGenes += c.getNumGenes();
		Collections.sort(rules,Collections.reverseOrder());
	}
	
	/**
	 * <p>
	 * Remove the specified chromosome from this object
	 * </p>
	 * @param c the chromosome to be deleted 
	 */
	public void removeChromosome(Chromosome c){
		rules.remove(c);
		nGenes-=c.getNumGenes();
		numRules--;
	}
	
	/**
	 * <p>
	 * Removes the chromosome at the specified position
	 * </p>
	 * @param c the chromosome to be deleted
	 */
	public void removeChromosome(int c){
		nGenes-=rules.get(c).getNumGenes();
		rules.remove(c);
		numRules--;
	}
	
	/**
	 * <p>
	 * Test if this cochromosome contains the specified chromosome
	 * </p>
	 * @param c the chromosome we are looking for
	 * @return True if present, false otherwise
	 */
	public boolean contains(Chromosome c){
		return rules.contains(c);
	}
	
	/**
	 * <p>
	 * Gets the number of rules (chromosomes)
	 * </p>
	 * @return the number of chromosomes in this object
	 */
	public int getNumRules(){
		return numRules;
	}
	
	/**
	 * <p>
	 * Gets the TOTAL number of genes across all chromosomes in this object
	 * </p>
	 * @return the number of chromosomes
	 */
	public int getNumGenes(){
		return nGenes;
	}
	
	/**
	 * <p>
	 * Gets the mu parameter
	 * </p>
	 * @return the mu value
	 */
	public int getMu(){
		return Mu;
	}
	
	/**
	 * <p>
	 * Sets a new mu value
	 * </p>
	 * @param newMu the new mu
	 */
	public void setMu(int newMu){
		Mu = newMu;
	}
	
	/**
	 * <p>
	 * Evaluates this cochromosome with the provided data set
	 * </p>
	 * @param ISet data set for testing this set of rules
	 * @return the classification accuracy obtained (fitness)
	 */
	public double evaluate(InstanceSet ISet){
		Chromosome rule;
		double input[];
		int output;
		int predicted = -1;
		int wellClassified = 0;
		if(evaluated)
			return fitness;
		for(int k=0;k<ISet.getNumInstances();k++){
			input = ISet.getInstance(k).getAllInputValues();
			output = (int)ISet.getInstance(k).getAllOutputValues()[0];
			predicted = -1;
			//IMPORTANT!!! the rule vector MUST be sorted by each rule's fitness!
			for(int i=0;i<rules.size() && predicted == -1;i++){
				rule = rules.get(i);

				predicted = rule.evaluate(input);
			}
			//if no class predicted by this rule set, output the majority class of the data set
//			if(predicted==-1)
//				predicted = Core.majorityClass;
			
			if(predicted==output)
				wellClassified++;
		}
		
		fitness = (double)wellClassified / (double)ISet.getNumInstances();
		evaluated = true;
		return fitness;
	}
	
	/**
	 * <p>
	 * Gets the evaluation condition
	 * </p>
	 * @return true if it has been evaluated with this set of rules, false otherwise
	 */
	public boolean isEvaluated(){
		return evaluated;
	}
	
	/**
	 * <p>
	 * Sets the evaluation condition
	 * </p>
	 * @param newEval the new evaluation condition
	 */
	public void setEvaluated(boolean newEval){
		evaluated = newEval;
	}
	
	/**
	 * <p>
	 * Performs the classification of a data set for final results printing purposes.
	 * Writes the input (known) class and the output (predicted) class in two arrays
	 *  (previously created!!)
	 * </p>
	 * @param ISet the reference data set 
	 * @param instancesIN the array with size of examples. Will contain the known class labels
	 * @param instancesOUT the array with the predicted class labels
	 * @return the classification accuracy
	 */
	public double classify(InstanceSet ISet, String instancesIN[], String instancesOUT[]){
		Chromosome rule;
		double input[];
		int output;
		int predicted = -1;
		int wellClassified = 0;
		
		Attribute a = Attributes.getOutputAttribute(0);

		int tipo = a.getType();
		for(int k=0;k<ISet.getNumInstances();k++){
			input = ISet.getInstance(k).getAllInputValues();
			output = (int)ISet.getInstance(k).getAllOutputValues()[0];
			predicted = -1;
			//IMPORTANT!!! the rule vector MUST be sorted by each rule's fitness!
			for(int i=0;i<rules.size() && predicted == -1;i++){
				rule = rules.get(i);

				predicted = rule.evaluate(input);
			}
			//if no class predicted by this rule set, output the majority class of the data set
			if(predicted==-1)
				predicted = Core.majorityClass;
			
			if(predicted==output)
				wellClassified++;
			if(tipo!=Attribute.NOMINAL){
				instancesIN[k] = new String(String.valueOf(ISet.getInstance(k).getOutputNominalValues(0)));
				instancesOUT[k] = new String(String.valueOf(predicted));

			}
			else{
				instancesIN[k] = new String(ISet.getInstance(k).getOutputNominalValues(0));
				instancesOUT[k] = new String(a.getNominalValue(predicted));

			}
		}
		
		return (double)wellClassified / (double)ISet.getNumInstances();	
	}
}

