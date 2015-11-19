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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.ARMMGA;

import java.util.*;

/**
 * <p>It is used for representing and handling an Association Rule
 * @author Written by Alvaro Lopez
 * @version 1.0
 * @since JDK1.6
 * </p>
 */
public class AssociationRule
{
	private ArrayList<Gene> antecedent;
	private ArrayList<Gene> consequent;
	private double all_support;
	private double support;
	private double support_consq;
	private double confidence;
	private double lift;
	private double conv;
	private double CF;
	private double netConf;
	private double yulesQ;

	public double getYulesQ() {
		return yulesQ;
	}

	public void setYulesQ(double yulesQ) {
		this.yulesQ = yulesQ;
	}

	public AssociationRule()
	{
		this.antecedent = new ArrayList<Gene>();
		this.consequent = new ArrayList<Gene>();
	}

	public AssociationRule copy () {
		int i;
		AssociationRule rule = new AssociationRule();

		for (i=0; i < this.antecedent.size(); i++) {
			rule.addAntecedent((this.antecedent.get(i)).copy());
		}

		for (i=0; i < this.consequent.size(); i++) {
			rule.addConsequent((this.consequent.get(i)).copy());
		}

		rule.all_support = this.all_support;
		rule.support = this.support;
		rule.confidence = this.confidence;
		rule.lift = this.lift;
		rule.CF = this.CF;
		rule.conv = this.conv;
		rule.netConf = this.netConf;
		rule.yulesQ = this.yulesQ;

		return (rule);
	} 

	public void addAntecedent(Gene g)
	{
		antecedent.add(g);
	}


	public void addConsequent(Gene g)
	{
		consequent.add(g);
	}


        /*
	 * <p>
	 * It retrieves the antecedent part of an association rule
	 * </p>
	 * @return An ArrayList containing Gene objects and representing antecedent attributes
	 */
	public ArrayList<Gene> getAntecedent()
	{
		return this.antecedent;
	}


        /**
	 * <p>
	 * It retrieves the consequent part of an association rule
	 * </p>
	 * @return An ArrayList containing Gene objects and representing consequent attributes
	 */
	public ArrayList<Gene> getConsequent()
	{
		return this.consequent;
	}

	public int getLengthRule()
	{
		return this.antecedent.size()+ this.consequent.size();
	}

	public int getLengthAntecedent()
	{
		return this.antecedent.size();
	}

	public double getAll_support()
	{
		return all_support;
	}


	public void setAll_support(double all_support)
	{
		this.all_support = all_support;
	}

        /**
	 * <p>
	 * It returns the support of an association rule
	 * </p>
	 * @return A value representing the support of the association rule
	 */
	public double getSupport() {
		return this.support;
	}


        /**
	 * <p>
	 * Sets the support of an association rule with the given value.
	 * </p>
         * @param support given value.
	 */
	public void setSupport(double support) {
		this.support = support;
	}


        /**
	 * <p>
	 * It returns the confidence of an association rule
	 * </p>
	 * @return A value representing the confidence of the association rule
	 */
	public double getConfidence()
	{
		return confidence;
	}


         /**
	 * 
	 * Sets the confidence of an association rule with the value given. 
         * @param confidence given value.
         */
	public void setConfidence(double confidence)
	{
		this.confidence = confidence;
	}


        /**
	 * <p>
	 * It returns a raw string representation of an association rule
	 * </p>
	 * @return A raw string representation of the association rule
	 */
	public String toString()
	{
		return ( this.antecedent.toString() + "-> " + this.consequent.toString() + ": " + this.getSupport() + "; " + this.getAll_support() + "; " + this.getConfidence());
	}

	public double getLift() {
		return lift;
	}

	public void setLift(double lift) {
		this.lift = lift;
	}

	public double getCF() {
		return CF;
	}

	public void setCF(double cf) {
		CF = cf;
	}

	public double getConv() {
		return conv;
	}

	public void setConv(double conv) {
		this.conv = conv;
	}

	public double getNetConf() {
		return netConf;
	}

	public void setNetConf(double netConf) {
		this.netConf = netConf;
	}

	public double getSupport_consq() {
		return support_consq;
	}

	public void setSupport_consq(double support_consq) {
		this.support_consq = support_consq;
	}

}
