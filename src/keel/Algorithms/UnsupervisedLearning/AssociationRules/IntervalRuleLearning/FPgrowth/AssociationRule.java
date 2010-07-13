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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.FPgrowth;

/**
 * <p>
 * @author Written by Nicolò Flugy Papè (Politecnico di Milano) 24/03/2009
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

public class AssociationRule {
	/**
	 * <p>
	 * It is used for representing and handling an Association Rule
	 * </p>
	 */
	
	private short[] antecedent;
	private short[] consequent;
	private double ruleSupport;
	private double antecedentSupport;
	private double confidence;
	
	/**
	 * <p>
	 * It creates a new association rule by setting up its properties
	 * </p>
	 * @param antecedent The antecedent part of the rule
	 * @param consequent The consequent part of the rule
	 * @param ruleSupport The value representing the rule support
	 * @param confidence The value representing the rule confidence
	 */
	public AssociationRule(short[] antecedent, short[] consequent, double ruleSupport, double antecedentSupport, double confidence) {
		this.setAntecedent(antecedent);
		this.setConsequent(consequent);
		this.ruleSupport = ruleSupport;
		this.antecedentSupport = antecedentSupport;
		this.confidence = confidence;
	}
		
	private void setAntecedent(short[] antecedent) {
		this.antecedent = new short[antecedent.length];
		
		for (int i=0; i < this.antecedent.length; i++)
			this.antecedent[i] = antecedent[i];
	}
	
	private void setConsequent(short[] consequent) {
		this.consequent = new short[consequent.length];
		
		for (int i=0; i < this.consequent.length; i++)
			this.consequent[i] = consequent[i];
	}
	
	/**
	 * <p>
	 * It retrieves the antecedent part of an association rule
	 * </p>
	 * @return An array of numbers representing antecedent attributes
	 */
	public short[] getAntecedent() {
		return this.antecedent;
	}
	
	/**
	 * <p>
	 * It retrieves the consequent part of an association rule
	 * </p>
	 * @return An array of numbers representing consequent attributes
	 */
	public short[] getConsequent() {
		return this.consequent;
	}
	
	/**
	 * <p>
	 * It returns the support of an association rule
	 * </p>
	 * @return A value representing the support of the association rule
	 */
	public double getRuleSupport() {
		return this.ruleSupport;
	}
	
	/**
	 * <p>
	 * It returns the antecedent support of an association rule
	 * </p>
	 * @return A value representing the antecedent support of the association rule
	 */
	public double getAntecedentSupport() {
		return this.antecedentSupport;
	}
	
	/**
	 * <p>
	 * It returns the confidence of an association rule
	 * </p>
	 * @return A value representing the confidence of the association rule
	 */
	public double getConfidence() {
		return this.confidence;
	}
		
	/**
	 * <p>
	 * It returns a raw string representation of an association rule
	 * </p>
	 * @return A raw string representation of the association rule
	 */
	public String toString() {
		String str = "{";
		int i;
		
		for (i=0; i < this.antecedent.length - 1; i++)
			str += this.antecedent[i] + ", ";
		
		str += this.antecedent[i] + "} -> {";
		
		for (i=0; i < this.consequent.length - 1; i++)
			str += this.consequent[i] + ", ";
		
		str += this.consequent[i] + "}; Rule Support: " + this.ruleSupport + "; Antecedent Support: " + this.antecedentSupport + "; Confidence: " + this.confidence;
		
		return str;
	}
}

