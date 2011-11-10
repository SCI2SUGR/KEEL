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

import java.util.ArrayList;

/**
 * <p>
 * This class represents a subpopulation of rules belonging to a same feature (class).
 * </p>
 */
public class Subpopulation {

	ArrayList<Chromosome> rules;
	int totalGenes;
	int Mu_next;
	
	/**
	 * <p>
	 * Default constructor. Initializes the structures.
	 * </p>
	 */
	public Subpopulation(){
		rules = new ArrayList<Chromosome>();
		totalGenes = 0;
		Mu_next = 0;
	}
	
	/**
	 * <p>
	 * Gets a rule from this subpopulation
	 * </p>
	 * @param i the index of the rule
	 * @return rule from this subpopulation
	 */
	public Chromosome getRule(int i){
		return rules.get(i);
	}
	
	/**
	 * <p>
	 * Adds a rule to this subpopulation
	 * </p>
	 * @param c the new rule to be added
	 */
	public void addRule(Chromosome c){
		rules.add(c);
		totalGenes+=c.getNumGenes();
	}
	
	/**
	 * <p>
	 * Removes one rule from the subpopulation list
	 * </p>
	 * @param c the rule to be removed
	 */
	public void removeRule(Chromosome c){
		rules.remove(c);
		totalGenes-=c.getNumGenes();
	}
	
	/**
	 * <p>
	 * Gets the TOTAL number of genes of this subpopulation
	 * </p>
	 * @return the number of genes
	 */
	public int getNumGenes(){
		return totalGenes;
	}
	
	/**
	 * <p>
	 * Gets the number of rules of this subpopulation
	 * </p>
	 * @return the number of rules
	 */
	public int getNumRules(){
		return rules.size();
	}
	
	/**
	 * <p>
	 * Mutates a specific rule and gene
	 * </p>
	 * @param rule rule which contains the gene to mutate
	 * @param gene the gene to be mutated
	 */
	public void mutate(int rule,int gene){
		rules.get(rule).mutateGene(gene);
	}

}

