/**
 * <p>
 * @author Written by Julián Luengo Martín 13/02/2007
 * @version 0.1
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Genetic_Rule_Learning.CORE;

import java.util.*;

/**
 * <p>
 * This class represents the co-population of cochromosomes in the cooperative-competitive scheme of the CORE algorithm
 * </p>
 */
public class Copopulation {
	
	ArrayList<Cochromosome> chrom;
	
	/**
	 * <p>
	 * Default constructor. Allocates memory for the inner array.
	 * </p>
	 */
	public Copopulation(){
		chrom = new ArrayList<Cochromosome>();
	}
	
	/**
	 * <p>
	 * Adds a cochromsome to this population (cannot be latter deleted!)
	 * </p>
	 * @param ruleSet the new rule set to be aded
	 */
	public void addRule(Cochromosome ruleSet){
		chrom.add(ruleSet);
	}

}
