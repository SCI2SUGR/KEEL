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

/**
 * The antecedent with nominal attribute
 * @author not attributable
 * @version 1.0
 */
public class NominalAntd extends Antd{

	/** for serialization */
	static final long serialVersionUID = -9102297038837585135L;

	/* The parameters of infoGain calculated for each attribute value
	 * in the growing data */
	private double[] accurate;
	private double[] coverage;

	/** 
	 * Constructor
     * @param a Weka attribute.
	 */
	public NominalAntd(AttributeWeka a){ 
		super(a);    
		int bag = att.numValues();
		accurate = new double[bag];
		coverage = new double[bag];
	}   

	/** 
	 * Implements Copyable
	 * 
	 * @return a copy of this object
	 */
	public Object copy(){
		Antd antec = new NominalAntd(getAttr());
		antec.m_confidence = m_confidence;
		antec.value = this.value;
		return antec;	    
	}

	/**
	 * Implements the splitData function.  
	 * This procedure is to split the data into bags according 
	 * to the nominal attribute value
	 * The infoGain for each bag is also calculated.  
	 * 
	 * @param data the data to be split
	 * @param defAcRt the default accuracy rate for data
	 * @param cl the class label to be predicted
	 * @return the array of data after split
	 */
	public Instances[] splitData(Instances data, double defAcRt, 
			double cl){
		int bag = att.numValues();
		Instances[] splitData = new Instances[bag];

		for(int x=0; x<bag; x++){
			splitData[x] = new Instances(data, data.numInstances());
			accurate[x] = 0;
			coverage[x] = 0;
		}

		for(int x=0; x<data.numInstances(); x++){
			Instance inst=data.instance(x);
			if(!inst.isMissing(att)){
				int v = (int)inst.value(att);
				splitData[v].add(inst);
				coverage[v] += inst.weight();
				if((int)inst.classValue() == (int)cl)
					accurate[v] += inst.weight();
			}
		}

		for(int x=0; x<bag; x++){
			double t = coverage[x]+1.0;
			double p = accurate[x] + 1.0;		
			double infoGain = 
				//Utils.eq(defAcRt, 1.0) ? 
				//accurate[x]/(double)numConds : 
				accurate[x]*(Utils.log2(p/t)-Utils.log2(defAcRt));

			if(infoGain > maxInfoGain){
				maxInfoGain = infoGain;
				cover = coverage[x];
				accu = accurate[x];
				accuRate = p/t;
				value = (double)x;
			}
		}

		return splitData;
	}

	/**
	 * Whether the instance is covered by this antecedent
	 * 
	 * @param inst the instance in question
	 * @return the boolean value indicating whether the instance is
	 *         covered by this antecedent
	 */
	public double covers(Instance inst){
		double isCover=0;
		if(!inst.isMissing(att)){
			if((int)inst.value(att) == (int)value)
				isCover=1;	    
		}
		return isCover;
	}

	/**
	 * Prints this antecedent
	 *
	 * @return a textual description of this antecedent
	 */
	public String toString() {
		return (att.name() + " = " +att.value((int)value));
	} 
}
