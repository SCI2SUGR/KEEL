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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.MODENAR;

import java.util.*;


public class AssociationRule
{
	private ArrayList<Gene> antecedent;
	private ArrayList<Gene> consequent;
	private double all_support;
	private double support;
	private double support_consq;
	private double confidence;
	private double lift;
	private double comprehensibility;
	private double amplitudeInterv;
	private double conv;
	private double CF;
	private double yulesQ;

	public double getYulesQ() {
		return yulesQ;
	}

	public void setYulesQ(double yulesQ) {
		this.yulesQ = yulesQ;
	}

	public double getConv() {
		return conv;
	}

	private double netConf;

	public double getCF() {
		return CF;
	}

	public void setCF(double cf) {
		CF = cf;
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

	public double getComprehensibility() {
		return comprehensibility;
	}

	public void setComprehensibility(double comprehensibility) {
		this.comprehensibility = comprehensibility;
	}

	public double getAmplitudeInterv() {
		return amplitudeInterv;
	}

	public void setAmplitudeInterv(double amplitudeInterv) {
		this.amplitudeInterv = amplitudeInterv;
	}

	public void setConsequent(ArrayList<Gene> consequent) {
		this.consequent = consequent;
	}

	public AssociationRule()
	{
		this.antecedent = new ArrayList<Gene>();
		this.consequent = new ArrayList<Gene>();
	}

	public AssociationRule copy () {
		int i;
		AssociationRule rule = new AssociationRule();

		for (i=0; i < this.antecedent.size(); i++)  rule.addAntecedent((this.antecedent.get(i)).copy());
		for (i=0; i < this.consequent.size(); i++)  rule.addConsequent((this.consequent.get(i)).copy());

		rule.all_support = this.all_support;
		rule.support = this.support;
		rule.confidence = this.confidence;
		rule.lift = this.lift;
		rule.conv = this.conv;
		rule.CF = this.CF;
		rule.netConf = this.netConf;
		rule.yulesQ = this.yulesQ;
		rule.comprehensibility = this.comprehensibility;
		rule.amplitudeInterv = this.amplitudeInterv;

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


	public ArrayList<Gene> getAntecedent()
	{
		return this.antecedent;
	}


	public ArrayList<Gene> getConsequent()
	{
		return this.consequent;
	}


	public int getLengthAntecedent()
	{
		return this.antecedent.size();
	}

	public int getLengthConsequent()
	{
		return this.consequent.size();
	}

	public double getAll_support()
	{
		return all_support;
	}


	public void setAll_support(double all_support)
	{
		this.all_support = all_support;
	}

	public double getSupport() {
		return this.support;
	}


	public void setSupport(double support) {
		this.support = support;
	}


	public double getConfidence()
	{
		return confidence;
	}


	public void setConfidence(double confidence)
	{
		this.confidence = confidence;
	}


	public String toString()
	{
		return ( this.antecedent.toString() + "-> " + this.consequent.toString() + ": " + this.getSupport() + "; " + this.getAll_support() + "; " + this.getConfidence() + "; " + this.getLift());
	}

	public double getLift() {
		return lift;
	}

	public void setLift(double lift) {
		this.lift = lift;
	}

	public double getSupport_consq() {
		return support_consq;
	}

	public void setSupport_consq(double support_consq) {
		this.support_consq = support_consq;
	}

}
