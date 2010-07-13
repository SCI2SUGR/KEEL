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
 * @author Written by Luciano Sánchez (University of Oviedo) 20/01/2004
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
 * @version 1.0 
 * @since JDK1.4 
 * </p> 
 */

package keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy;

public class RuleBase {
	/** 
	* <p> 
	* Represents a FRBS (Fuzzy Rule Base System). A FRBS is composed of:  
	* 
	* -content: the consequents of the rules (each rule has an index to partition array)
	* -partitions: the antecedents for each rule.
	* -partition: the consequents shared by all the rules.
	* </p> 
	*/
	// TNorm
	int tnr;
	int agr;
	// The vector of fuzzy rules with their consequent index and weight.
	FuzzyRule[] content;
	// The vector of fuzzy partitions with the antecedents for each rule.
	FuzzyPartition[] partitions;
	// Fuzzy partition with consequents.
	FuzzyPartition partition;
	final static int minimum = 0;
	public final static int product = 1;
	final static int maximum = 0;
	public final static int sum = 1;
	// Defuzzification by mass center
	public final static int DEFUZCDM = 0;
	// Defuzzification by maximum
	public final static int DEFUZMAX = 1;
	long[] rules;

	
	/** 
     * <p> 
     * A constructor for a RuleBase.   
     * 
     * </p> 
     * @param pe vector of fuzzy partitions with the antecedents for each rule.
     * @param ps fuzzy partition with consequents for the rules.
     * @param tn T-Norm for RuleBase.
     * @param ag .
     */
	public RuleBase(FuzzyPartition[] pe, FuzzyPartition ps, int tn, int ag) {
		partitions = new FuzzyPartition[pe.length];
		for (int i = 0; i < pe.length; i++)
			partitions[i] = pe[i].clone();
		partition = ps.clone();
		tnr = tn;
		agr = ag;
		int n = 1;
		// for (int i=0;i<pe.length;i++) n*=pe[i].size();
		content = new FuzzyRule[n];
		for (int i = 0; i < n; i++)
			content[i] = new FuzzyRule();
	}
	/** 
     * <p> 
     * A copy constructor for a RuleBase, given other RuleBase. 
     * 
     * </p> 
     * @param b to be copied.    
     */
	public RuleBase(RuleBase b) {
		partitions = new FuzzyPartition[b.partitions.length];
		for (int i = 0; i < partitions.length; i++)
			partitions[i] = b.partitions[i].clone();
		partition = b.partition.clone();
		tnr = b.tnr;
		agr = b.agr;
		content = new FuzzyRule[b.content.length];
		for (int i = 0; i < b.content.length; i++)
			content[i] = b.content[i].clone();
	}
	/** 
     * <p> 
     * Copies the RuleBase parameter over the present instance. 
     * 
     * </p> 
     * @param b a RuleBase object to be copied
     */
	public void set(RuleBase b) {
		partitions = new FuzzyPartition[b.partitions.length];
		for (int i = 0; i < partitions.length; i++)
			partitions[i] = b.partitions[i].clone();
		partition = b.partition.clone();
		tnr = b.tnr;
		agr = b.agr;
		content = new FuzzyRule[b.content.length];
		for (int i = 0; i < b.content.length; i++)
			content[i] = b.content[i].clone();
	}
	 /** 
     * <p> 
     * Creates and returns a copy of this object.
     * 
     * </p>
     * @return a clone of this instance. 
     */    
	public RuleBase clone() {
		return new RuleBase(this);
	}
	/** 
     * <p> 
     * Prints the results.
     * 
     * </p> 
     */   
	public void debug() {
		print();
	}
	/** 
     * <p> 
     * Returns the number of rules.
     * 
     * </p> 
     */
	public int size() {
		return content.length;
	}
	/** 
     * <p> 
     * Returns rule n.
     * 
     * </p> 
     * @param n the number of rule.
     */
	public FuzzyRule getComponent(int n) {
		return content[n];
	}
	/** 
     * <p> 
     * Copies a new rule in the RuleBase.
     * 
     * </p> 
     * @param n the number of rule to rewrite.
     * @param b the new rule to copy in present RuleBase.
     */
	public void setComponent(int n, FuzzyRule b) {
		content[n] = b.clone();
	}
	/** 
     * <p> 
     * Returns a vector with indexes corresponding to antecedents labels of example r in vector partitions.
     * 
     * </p> 
     * @param r the codification of an example to be decoded.
     */
	int[] decodifyRule(long r) {
		// Now, a vector with indexes corresponding to antecedents labels is
		// built
		int[] result = new int[partitions.length];
		for (int i = result.length - 1; i >= 0; i--) {
			result[i] = (int) (r % partitions[i].size());
			r /= partitions[i].size();
		}
		return result;
	}
	/** 
     * <p> 
     * Returns a String with the antecedents (Vi) of rule r.
     * 
     * </p> 
     * @param r the number of rule whose antecedents are to be printed.
     */
	public String variableNames(long r) {
		int[] d = decodifyRule(r);
		String result = "[";
		for (int i = 0; i < d.length; i++) {
			result += "V" + i + "-" + d[i];
			if (i != d.length - 1)
				result += ",";
		}
		return result + "]";
	}
	/** 
     * <p> 
     * Returns the T-Norm of two membership values.
     * 
     * </p> 
     * @param x the membership grade of one individual.
     * @param y the membership grade of one individual.
     */
	public double tnorm(double x, double y) {
		if (tnr == minimum) {
			if (x < y)
				return x;
			else
				return y;
		} else
			return x * y;
	}
	/** 
     * <p> 
     * Returns the Add-Norm of two membership values.
     * 
     * </p> 
     * @param x the membership grade of one individual.
     * @param y the membership grade of one individual.
     */
	double add(double x, double y) {
		if (agr == maximum) {
			if (x > y)
				return x;
			else
				return y;
		} else
			return x + y;
	}
	/** 
     * <p> 
     * Returns Grade of Membership of x to rule r antecedent.
     * 
     * </p> 
     * @param r a rule index.
     * @param x individual.
     */
	public double evaluateMembership(long r, double[] x) {
		int[] d = decodifyRule(r);
		double pertenencia = 1;
		for (int j = 0; j < x.length; j++) {
			pertenencia = tnorm(pertenencia, partitions[j].getComponent(d[j])
					.evaluateMembership(x[j]));
		}
		return pertenencia;
	}
	/** 
     * <p> 
     * Returns output (Wang-Mendel) for input x.
     * 
     * </p> 
     * @param x individual.
     */
	public double[] output(double[] x) {
		// It calculates the output WM for input x
		double[] result = new double[partition.size()];
		for (int i = 0; i < content.length; i++) {
			if (content[i].weight == 0)
				continue;
			double p = tnorm(evaluateMembership(i, x), content[i].weight);
			result[content[i].consequent] = add(
					result[content[i].consequent], p);
		}
		return result;
	}
	/** 
     * <p> 
     * Adds new rules to BaseRule.
     * 
     * </p> 
     * @param rules vector with coded indexes of the rules.
     * @param rules2 vector with antecedents and weights of the rules.
     */
	public void addRules(long[] rules, FuzzyRule[] rules2) {
		this.rules = new long[rules.length];
		content = new FuzzyRule[rules.length];
		for (int i = 0; i < rules.length; i++) {
			this.rules[i] = rules[i];
			content[i] = new FuzzyRule(rules2[i]);
		}
	}
	/** 
     * <p> 
     * Returns output (Wang-Mendel) for input x.
     * 
     * </p> 
     * @param x individual.
     * @return a vector with association grade between the example x and the classes of the rules (content[i].consequent)
     */
	public double[] myOutput(double[] x) {
		// It calculates the output WM for input x
		double[] result = new double[partition.size()];
		for (int i = 0; i < rules.length; i++) {
			// it's calculated the association grade between rule i and example x
			double p = tnorm(evaluateMembership(rules[i], x), content[i].weight); 
			
			// It added the association grade rule-example to certainty grade of the class			
			result[content[i].consequent] = add(
					result[content[i].consequent], p);
			// It's calculated the association grade between the example x and the classes (content[i].consequent)
		}
		return result;
	}
	/** 
     * <p> 
     * Returns the number of consequents in RuleBase.
     * 
     * </p> 
     */
	public int numConsequents() {
		return partition.size();
	}
	/** 
     * <p> 
     * Prints all the rules in the base.
     * 
     * </p> 
     */
	void print() {
		// It prints the result
		System.out.println("Debug RuleBase " + size());
		for (int r = 0; r < size(); r++)
			if (content[r].weight >= 0)
				System.out.println("IF " + variableNames(r) + " THEN " + "S"
						+ content[r].consequent + " with weight "
						+ content[r].weight);
		System.out.println("------------");

	}
	/** 
     * <p> 
     * Defuzzifies output using method IDDEFUZZIFY.
     * 
     * </p> 
     * @param output vector with the output to be defuzzified.
     * @param IDDEFUZZIFY type of defuzzification to run (RuleBase.DEFUZCDM or RuleBase.DEFUZMAX).       
     */
	public double defuzzify(double[] output, int IDDEFUZZIFY) {

		if (IDDEFUZZIFY == DEFUZCDM) {
			// Defuzzification by massas center
			// It calculates a charateristic value from the fuzzy range of
			// outputs
			double centerSum = 0, weightSum = 0;
			for (int i = 0; i < partition.size(); i++) {
				centerSum += partition.getComponent(i).massCentre()
						* output[i];
				weightSum += output[i];
			}
			if (weightSum == 0)
				return 0; // Not covered output.
			return centerSum / weightSum;
		}

		if (IDDEFUZZIFY == DEFUZMAX) {
			// Defuzzification by maximum
			// It calculates a charateristic value from the fuzzy range of
			// outputs
			double val = 0, mumax = 0;
			for (int i = 0; i < partition.size(); i++) {
				if (output[i] > mumax) {
					mumax = output[i];
					val = partition.getComponent(i).massCentre();
				}
			}
			if (mumax == 0)
				return 0; // Not covered output.
			return val;

		}

		return 0;

	}
	/** 
     * <p> 
     * This method finds out the label with maximum membership value for each variable.
     * After that, it codifies the label indexes with corresponding variable cardinality (or partition cardinality) in a long value.
     * The codification consists in weighting each label index by the product of previous variables cardinality.
     * 
     * </p> 
     * @param example input example to codify. 
     * @return the codification of example in terms of the antecedent membership.      
     */
	public long codifyAntecents(double[] example) {
		int[] antecedent = new int[example.length];
		for (int i = 0; i < example.length; i++) { // for each input variables in the example
			double max = 0.0;
			int etq = 0;
			for (int j = 0; j < partitions[i].size(); j++) { // for each possible label in variable i
				double per = partitions[i].getComponent(j).evaluateMembership(
						example[i]);
				if (per > max) {
					max = per;
					etq = j;
				}
			}
			if (max == 0.0) {
				System.err.println("Error searching antecedent");
				System.err.println("\nExample:");
				for (int j = 0; j < example.length; j++)
					System.err.println("" + example[j] + "\t");
				System.err.println("Variable -> " + i);
				System.exit(1);
			}
			antecedent[i] = etq;
		}
		long rule = 0, j = 1;
		for (int i = antecedent.length - 1; i >= 0; j *= partitions[i].size(), i--) {
			rule += (long) (antecedent[i] * j);
		}
		return rule;
	}
}

