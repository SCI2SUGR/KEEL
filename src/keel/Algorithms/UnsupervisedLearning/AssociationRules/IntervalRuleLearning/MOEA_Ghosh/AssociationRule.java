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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.MOEA_Ghosh;

import java.util.*;

public class AssociationRule {
	/**
	 * <p>
	 * It is used for representing and handling an Association Rule.
	 * It mainly wraps the methods of a chromosome to offer high-level features later
	 * </p>
	 */

	public ArrayList<Gene> antecedent;
	public ArrayList<Gene> consequent;
	public double antSupport;
	public double consSupport;
	public double support;
	public double confidence;
	public double lift;
	public int nAnts;
	public int rank;
	public double conv;
	public double CF;
	public double netConf;
	public double yulesQ;

	public AssociationRule() {
	}

	/**
	 * <p>
	 * It creates a new association rule by setting up the chromosome which is based on
	 * </p>
	 * @param chr The chromosome which this association rule is based on
	 */
	public AssociationRule(Chromosome chr) {
		int i;
		Gene gen;

		this.antecedent = new ArrayList<Gene>();
		this.consequent = new ArrayList<Gene>();

		for (i=0; i < chr.genes.length; i++) {
			gen = chr.getGene(i);
			if (gen.getActAs() == Gene.ANTECEDENT)  this.antecedent.add(gen.copy());
			if (gen.getActAs() == Gene.CONSEQUENT)  this.consequent.add(gen.copy());
		}

		this.support = chr.getSupport();
		this.antSupport = chr.getAntsSupport();
		this.consSupport = chr.getConsSupport();
		this.confidence = chr.getConfidence();
		this.lift = chr.getLift();
		this.conv = chr.getConv();
		this.CF = chr.getCF();
		this.netConf = chr.getNetConf();
		this.yulesQ = chr.getYulesQ();
		this.nAnts = chr.getnAnts();
		this.rank = chr.getRank();

	}

	/**
	 * <p>
	 * It allows to clone correctly an association rule
	 * </p>
	 * @return A copy of the association rule
	 */
	public AssociationRule copy() {
		int i;
		AssociationRule rule;

		rule = new AssociationRule();

		rule.antecedent = new ArrayList<Gene>();
		rule.consequent = new ArrayList<Gene>();

		for (i=0; i < this.antecedent.size(); i++)  rule.antecedent.add(this.antecedent.get(i).copy());
		for (i=0; i < this.consequent.size(); i++)  rule.consequent.add(this.consequent.get(i).copy());

		rule.antSupport = this.antSupport;
		rule.consSupport = this.consSupport;
		rule.support = this.support;
		rule.confidence = this.confidence;
		rule.lift = this.lift;
		rule.nAnts = this.nAnts;
		rule.rank = this.rank;
		rule.lift = this.lift;
		rule.CF = this.CF;
		rule.conv = this.conv;
		rule.netConf = this.netConf;
		rule.yulesQ = this.yulesQ;

		return rule;
	}

	/**
	 * <p>
	 * It retrieves the antecedent part of an association rule
	 * </p>
	 * @return An array of genes only representing antecedent attributes
	 */

	public ArrayList<Gene> getAntecedents()
	{
		return this.antecedent;
	}


	public ArrayList<Gene> getConsequents()
	{
		return this.consequent;
	}


	/**
	 * <p>
	 * It returns the support of an association rule
	 * </p>
	 * @return A value representing the support of the association rule
	 */
	public double getSupport() {
		return (this.support);
	}

	public double getAntSupport() {
		return (this.antSupport);
	}
	public double getConsSupport() {
		return (this.consSupport);
	}

	/**
	 * <p>
	 * It returns the confidence of an association rule
	 * </p>
	 * @return A value representing the confidence of the association rule
	 */
	public double getConfidence() {
		return (this.confidence);
	}

	public double getLift() {
		return (this.lift);
	}

	public int getnAnts() {
		return (this.nAnts);
	}

	public int getRank() {
		return (this.rank);
	}

	public boolean isCovered (double[] example) {
		int i;
		boolean covered;
		Gene gen;

		covered = true;

		for (i=0; i < this.antecedent.size() && covered; i++) {
			gen = this.antecedent.get(i);
			if (!gen.isCover(gen.getAttr(), example[gen.getAttr()]))  covered = false;
		}

		for (i=0; i < this.consequent.size() && covered; i++) {
			gen = this.consequent.get(i);
			if (!gen.isCover(gen.getAttr(), example[gen.getAttr()]))  covered = false;
		}

		return (covered);
	}

	/**
	 * <p>
	 * It returns a raw string representation of an association rule
	 * </p>
	 * @return A raw string representation of the association rule
	 */  
	public String toString()
	{
		return ( this.antecedent.toString() + "-> " + this.consequent.toString() + ": " + this.antSupport + "; " + this.support + "; " + this.confidence + "; " + this.lift + "; " + this.nAnts + "; " + this.rank);
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

	public double getYulesQ() {
		return yulesQ;
	}



}
