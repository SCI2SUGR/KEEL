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

package keel.Algorithms.Fuzzy_Rule_Learning.Hybrid.FURIA.core;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */


import java.io.Serializable;
//import keel.Dataset.Attribute;

/**
 * The single antecedent in the rule, which is composed of an attribute and
 * the corresponding value.  There are two inherited classes, namely NumericAntd
 * and NominalAntd in which the attributes are numeric and nominal respectively.
 */
public abstract class Antd implements Copyable, WeightedInstancesHandler, Serializable {

	/** The attribute of the antecedent */
	public AttributeWeka att;

	/** The attribute value of the antecedent.  
   For numeric attribute, value is either 0(1st bag) or 1(2nd bag) */
	public double value; 

	/** The maximum infoGain achieved by this antecedent test 
	 * in the growing data */
	protected double maxInfoGain;

	/** The accurate rate of this antecedent test on the growing data */
	protected double accuRate;

	/** The coverage of this antecedent in the growing data */
	protected double cover;

	/** The accurate data for this antecedent in the growing data */
	protected double accu;


	/** Confidence / weight of this rule for the rule stretching procedure that
	 * is returned when this is the last antecedent of the rule.  */
	double weightOfTheRuleWhenItIsPrunedAfterThisAntecedent = 0;

	/** Confidence / weight of this antecedent.  */
	public double m_confidence = 0.0;

	/** 
	 * Constructor
	 */
	public Antd(AttributeWeka a){
		att=a;
		value=Double.NaN; 
		maxInfoGain = 0;
		accuRate = Double.NaN;
		cover = Double.NaN;
		accu = Double.NaN;
	}

	/* The abstract members for inheritance */
	public abstract Instances[] splitData(Instances data, double defAcRt, 
			double cla);
	public abstract double covers(Instance inst);
	public abstract String toString();

	/** 
	 * Implements Copyable
	 * 
	 * @return a copy of this object
	 */
	public abstract Object copy(); 

	/* Get functions of this antecedent */
	public AttributeWeka getAttr(){ return att; }
	public double getAttrValue(){ return value; }
	public double getMaxInfoGain(){ return maxInfoGain; }
	public double getAccuRate(){ return accuRate; } 
	public double getAccu(){ return accu; } 
	public double getCover(){ return cover; } 
}