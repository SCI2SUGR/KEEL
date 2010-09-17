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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.GeneticFuzzyAprioriMS;

/**
 * <p>
 * @author Written by Alvaro Lopez
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

	private Itemset antecedent;
	private Itemset consequent;

	private double ruleSupport;
	private double antecedentSupport;
	private double confidence;
        private double consequentSupport;
        private double interestingness;

	/**
	 * <p>
	 * It creates a new association rule by setting up its properties
	 * </p>
	 * @param antecedent The antecedent part of the rule
	 * @param consequent The consequent part of the rule
	 * @param ruleSupport The value representing the rule support
	 * @param confidence The value representing the rule confidence
         * @param antecedentSupport The value representing the antecedente support
         * @param consequentSupport The value representing the consequent support
         * @param interestingness The value representing the interest measure
	 */
	public AssociationRule(Itemset antecedent, Itemset consequent, double ruleSupport, double antecedentSupport, double confidence,double consequentSupport,double interestingness) {
		this.antecedent = antecedent;
		this.consequent = consequent;

		this.ruleSupport = ruleSupport;
		this.antecedentSupport = antecedentSupport;
		this.confidence = confidence;
                this.consequentSupport = consequentSupport;
                this.interestingness = interestingness;
	}

	/**
	 * <p>
	 * It retrieves the antecedent part of an association rule
	 * </p>
	 * @return An itemset containing items and representing antecedent attributes
	 */
	public Itemset getAntecedent() {
		return this.antecedent;
	}

	/**
	 * <p>
	 * It retrieves the consequent part of an association rule
	 * </p>
	 * @return An itemset containing items and representing consequent attributes
	 */
	public Itemset getConsequent() {
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
	 * @return A value representing the the antecedent support of the association rule
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
	 * It returns the consequent support of an association rule
	 * </p>
	 * @return A value representing the consequent support of the association rule
	 */
	public double getConsequentSupport() {
		return this.consequentSupport;
	}

        /**
	 * <p>
	 * It returns the interest measure of an association rule
	 * </p>
	 * @return A value representing the interest measure of the association rule
	 */
	public double getInterestingness() {
		return this.interestingness;
	}

	/**
	 * <p>
	 * It returns a raw string representation of an association rule
	 * </p>
	 * @return A raw string representation of the association rule
	 */
	public String toString() {
		return ( this.antecedent + " -> " + this.consequent + "; Rule Support: " + this.ruleSupport + "; Antecedent Support: " + this.antecedentSupport + "; Confidence: " + this.confidence + "; Consequent Support: " + this.consequentSupport + "; Interestingness: " + this.interestingness );
	}

}