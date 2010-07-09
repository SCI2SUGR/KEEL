package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.Apriori;

/**
 * <p>
 * @author Written by Nicolò Flugy Papè (Politecnico di Milano) 24/03/2009
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
	private double confidence;
	
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
	
}
