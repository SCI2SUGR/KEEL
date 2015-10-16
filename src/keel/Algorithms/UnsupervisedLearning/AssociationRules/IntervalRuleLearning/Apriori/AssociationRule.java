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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.Apriori;

/**
 * <p>
 * @author Written by Nicolò Flugy Papè (Politecnico di Milano) 24/03/2009
 * @author Modified by Diana Martín (dmartin@ceis.cujae.edu.cu) 
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

import java.util.ArrayList;

public class AssociationRule {
	/**
	 * <p>
	 * It is used for representing and handling an Association Rule
	 * </p>
	 */

	private ArrayList<Integer> antecedent;
	private ArrayList<Integer> consequent;
	private double ruleSupport;
	private double antecedentSupport;
	private double consequentSupport;
	private double confidence;
	private double lift;
	private double conv;
	private double CF;
	private double netConf;
	private double yulesQ;

	/**
	 * <p>
	 * Default constructor
	 * </p>
	 */
	public AssociationRule() {
		this.antecedent = new ArrayList<Integer>();
		this.consequent = new ArrayList<Integer>();

	}

	/**
	 * <p>
	 * It adds a single antecedent term to an association rule
	 * </p>
	 * @param value The value representing the antecedent term
	 */
	public void addAntecedent(int value) {
		this.antecedent.add(value);
	}

	/**
	 * <p>
	 * It adds a single consequent term to an association rule
	 * </p>
	 * @param value The value representing the consequent term
	 */
	public void addConsequent(int value) {
		this.consequent.add(value);
	}

	/**
	 * <p>
	 * It sets the support of an association rule
	 * </p>
	 * @param ruleSupport The value representing the rule support
	 */
	public void setRuleSupport(double ruleSupport) {
		this.ruleSupport = ruleSupport;
	}

	/**
	 * <p>
	 * It sets the antecedent support of an association rule
	 * </p>
	 * @param antecedentSupport The value representing the antecedent support
	 */
	public void setAntecedentSupport(double antecedentSupport) {
		this.antecedentSupport = antecedentSupport;
	}

	/**
	 * <p>
	 * It sets the confidence of an association rule
	 * </p>
	 * @param confidence The value representing the rule confidence
	 */
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	/**
	 * <p>
	 * It retrieves the antecedent part of an association rule
	 * </p>
	 * @return An array of numbers representing antecedent attributes
	 */
	public ArrayList<Integer> getAntecedent() {
		return this.antecedent;
	}

	/**
	 * <p>
	 * It retrieves the consequent part of an association rule
	 * </p>
	 * @return An array of numbers representing consequent attributes
	 */
	public ArrayList<Integer> getConsequent() {
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
	 * @return A raw string representation of an association rule
	 */
	public String toString() {
		return ( this.antecedent.toString() + "-> " + this.consequent.toString() + "; Rule Support: " + this.ruleSupport + "; Antecedent Support: " + this.antecedentSupport + "; Confidence: " + this.confidence );
	}

	/**
	 * <p>
	 * It returns the CF of an association rule
	 * </p>
	 * @return A value representing the CF of the association rule
	 */
	public double getCF() {
		return CF;
	}

	/**
	 * <p>
	 * It sets the CF of an association rule
	 * </p>
	 * @param cf The value representing the rule cf
	 */
	public void setCF(double cf) {
		CF = cf;
	}

	/**
	 * <p>
	 * It returns the conviction of an association rule
	 * </p>
	 * @return A value representing the conviction of the association rule
	 */
	public double getConv() {
		return conv;
	}

	/**
	 * <p>
	 * It sets the conviction of an association rule
	 * </p>
	 * @param conv The value representing the rule conviction
	 */
	public void setConv(double conv) {
		this.conv = conv;
	}

	/**
	 * <p>
	 * It returns the netconf of an association rule
	 * </p>
	 * @return A value representing the netconf of the association rule
	 */
	public double getNetConf() {
		return netConf;
	}

	/**
	 * <p>
	 * It sets the netconf of an association rule
	 * </p>
	 * @param netconf The value representing the rule netconf
	 */
	public void setNetConf(double netConf) {
		this.netConf = netConf;
	}

	/**
	 * <p>
	 * It returns the consequent support of an association rule
	 * </p>
	 * @return A value representing the consequent support of the association rule
	 */
	public double getConsequentSupport() {
		return consequentSupport;
	}

	public void setConsequentSupport(double consequentSupport) {
		this.consequentSupport = consequentSupport;
	}

	/**
	 * <p>
	 * It returns the yulesQ of an association rule
	 * </p>
	 * @return A value representing the yulesQ of the association rule
	 */
	public double getYulesQ() {
		return yulesQ;
	}
	/**
	 * <p>
	 * It sets the yulesQ of an association rule
	 * </p>
	 * @param yulesQ The value representing the rule yulesQ
	 */
	public void setYulesQ(double yulesQ) {
		this.yulesQ = yulesQ;
	}

	/**
	 * <p>
	 * It returns the lift of an association rule
	 * </p>
	 * @return A value representing the lift of the association rule
	 */
	public double getLift() {
		return lift;
	}
	/**
	 * <p>
	 * It sets the lift of an association rule
	 * </p>
	 * @param lift The value representing the rule lift
	 */
	public void setLift(double lift) {
		this.lift = lift;
	}


}

