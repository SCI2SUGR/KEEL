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
 * @author Written by Julián Luengo Martín 13/02/2007
 * @version 0.1
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Genetic_Rule_Learning.CORE;

import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;
import keel.Dataset.InstanceSet;

/**
 * <p>
 * This class represents the population of chromosomes in the CORE algorithm.
 * It is composed by a set of subpopulations (many as number of different classes in the data set)
 * </p>
 */
public class Population {
	
//	ArrayList<Chromosome> rules;
	Subpopulation rules[];
	
	/**
	 * <p>
	 * Constructor which initializes the memory allocations.
	 * </p>
	 */
	public Population(){
		Attribute a =Attributes.getOutputAttribute(0);
		int numClasses;
		if(a.getType() == Attribute.NOMINAL)
			numClasses = a.getNumNominalValues();
		else
			numClasses =(int)( a.getMaxAttribute() - a.getMinAttribute());

		rules = new Subpopulation[numClasses];
		for(int i=0;i<numClasses;i++)
			rules[i] = new Subpopulation();
	}
	
	/**
	 * <p>
	 * Gets the indicated rule
	 * </p>
	 * @param _class class of the subpopulation which the rule belongs to
	 * @param index index of the rule in the proper subpopulation
	 * @return the rule we want
	 */
	public Chromosome getRule(int _class,int index){
		return rules[_class].getRule(index);
	}
	
	/**
	 * <p>
	 * Gets one rule using its global index, that is, the index across all subpopulations
	 * </p>
	 * @param globalIndex global index of the rule
	 * @return the indicated rule 
	 */
	public Chromosome getRule(int globalIndex){
		int i;
		int tmp = globalIndex;
		
		i = 0;
		while(tmp >= 0){
			tmp -= rules[i].getNumRules();
			i++;
		}
		i--;
		tmp += rules[i].getNumRules();
		return rules[i].getRule(tmp);
		
	}
	
	/**
	 * <p>
	 * Adds a rule to the specified subpopulation
	 * </p>
	 * @param _class the class which identifies the subpopulation
	 * @param rule the rule to be added
	 */
	public void addRule(int _class,Chromosome rule){
		rules[_class].addRule(rule);
	}
	
	/**
	 * <p>
	 * Gets the number of genes of one subpopulation
	 * </p>
	 * @param _class the class which identifies the subpopulation
	 * @return the number of genes of the specified subpopulation
	 */
	public int getNumGenes(int _class){
		return rules[_class].getNumGenes();
	}
	
	/**
	 * <p>
	 * Gets the number of rules of the specified subpopulation
	 * </p>
	 * @param _class the class which identifies the subpopulation
	 * @return the number of rules of the specified subpopulation
	 */
	public int getNumRules(int _class){
		return rules[_class].rules.size();
	}
	
	/**
	 * <p>
	 * Gets the mu value of one subpopulation
	 * </p>
	 * @param _class the class which identifies the subpopulation
	 * @return the mu value of the subpopulation
	 */
	public int getMu(int _class){
		return rules[_class].Mu_next;
	}
	
	/**
	 * <p>
	 * Sets the new mu value for one subpopulation
	 * </p>
	 * @param _class the class which identifies the subpopulation
	 * @param newMu the new mu value for mutation
	 */
	public void setMu(int _class, int newMu){
		rules[_class].Mu_next = newMu;
	}
	
	/**
	 * <p>
	 * Mutates a chromosome in a specified subpopulation
	 * </p>
	 * @param _class the class which identifies the subpopulation
	 * @param chrom the index of the chromosome to be mutated
	 * @param gen the gene of the chromosome to be mutated
	 */
	public void mutate(int _class,int chrom,int gen){
		rules[_class].mutate(chrom,gen);
	}
	
	/**
	 * <p>
	 * Sets the evaluation status of one rule in a specific subpopulation
	 * </p>
	 * @param _class the class which identifies the subpopulation
	 * @param chrom the chromosome (rule) to be changed
	 * @param evaluated the new evaluation status of the rule
	 */
	public void setEvaluated(int _class,int chrom,boolean evaluated){
		rules[_class].getRule(chrom).setEvaluated(evaluated);
	}
	
	/**
	 * <p>
	 * Removes one rule of the specified subpopulation
	 * </p>
	 * @param _class the class which identifies the subpopulation
	 * @param r the rule to be deleted
	 */
	public void removeRule(int _class,Chromosome r){
		rules[_class].removeRule(r);
	}
	
	/**
	 * <p>
	 * Evaluates all this population with a given data set. Stores the obtained fitness
	 * in each rule, updating the previous one, and setting the evaluated status of the rules to True.
	 * </p>
	 * @param ISet the data set to evaluate with
	 */
	public void evaluate(InstanceSet ISet){
		double input[];
		Instance inst;
		int obtainedClass;
		int tp,fp,tn,fn;
		double fitness,penalty;
		for(int i=0;i<rules.length;i++){
			for(int j=0;j<rules[i].rules.size();j++){
				tp = fp = tn = fn = 0;
				if(!rules[i].getRule(j).isEvaluated()){
					for(int k=0;k<ISet.getNumInstances();k++){
						inst = ISet.getInstance(k);
						input = inst.getAllInputValues();
						obtainedClass = rules[i].getRule(j).evaluate(input);
						if(obtainedClass == inst.getAllOutputValues()[0])
							tp++;
						else if(obtainedClass != -1 &&obtainedClass != inst.getAllOutputValues()[0])
							fp++;
						else if (obtainedClass == -1 && inst.getAllOutputValues()[0] != rules[i].getRule(j).getClas())
							tn++;
						else if(obtainedClass == -1 && inst.getAllOutputValues()[0] == rules[i].getRule(j).getClas())
							fn++;
					}
					penalty = (double)ISet.getNumInstances()/(ISet.getNumInstances() + fp);
					if((tp+fn) == 0 || (tn+fp)==0)
						fitness = 0;
					else
						fitness = penalty * (tp/(tp+fn))*(1+(tn/(tn+fp)));

					rules[i].getRule(j).setFitness(fitness);
					rules[i].getRule(j).setEvaluated(true);
				}
			}
		}
	}

}

