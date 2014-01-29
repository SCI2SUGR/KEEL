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
 * The antecedent with numeric attribute
 */
public class 
NumericAntd extends Antd {

	/** for serialization */
	static final long serialVersionUID = 5699457269983735442L;

	/** The split point for this numeric antecedent */
	public double splitPoint;

	/** The edge point for the fuzzy set of this numeric antecedent */
	public double supportBound; 

	public boolean fuzzyYet = false;


	/** 
	 * Constructor
	 */
	public NumericAntd(AttributeWeka a){ 
		super(a);
		splitPoint = Double.NaN;
		supportBound = Double.NaN;
	}    

	/** 
	 * Get split point of this numeric antecedent
	 * 
	 * @return the split point of this numeric antecedent
	 */
	public double getSplitPoint(){ 
		return splitPoint;
	}

	/** 
	 * Implements Copyable
	 * 
	 * @return a copy of this object
	 */
	public Object copy(){      
		NumericAntd na = new NumericAntd(getAttr());
		na.m_confidence = m_confidence;
		na.value = this.value;
		na.splitPoint = this.splitPoint;
		na.supportBound = this.supportBound;
		na.fuzzyYet = this.fuzzyYet;
		return na;
	}



	/**
	 * Implements the splitData function.  
	 * This procedure is to split the data into two bags according 
	 * to the information gain of the numeric attribute value
	 * The maximum infoGain is also calculated.  
	 * 
	 * @param insts the data to be split
	 * @param defAcRt the default accuracy rate for data
	 * @param cl the class label to be predicted
	 * @return the array of data after split
	 */
	public Instances[] splitData(Instances insts, double defAcRt, 
			double cl){
		Instances data = insts;
		int total=data.numInstances();// Total number of instances without 
		// missing value for att

		int split=1;                  // Current split position
		int prev=0;                   // Previous split position
		int finalSplit=split;         // Final split position
		maxInfoGain = 0;
		value = 0;	

		double fstCover=0, sndCover=0, fstAccu=0, sndAccu=0;

		data.sort(att);
		// Find the las instance without missing value 
		for(int x=0; x<data.numInstances(); x++){
			Instance inst = data.instance(x);
			if(inst.isMissing(att)){
				total = x;
				break;
			}

			sndCover += inst.weight();
			if(Utils.eq(inst.classValue(), cl))
				sndAccu += inst.weight();		
		}	    

		if(total == 0) return null; // Data all missing for the attribute
		splitPoint = data.instance(total-1).value(att);	

		for(; split <= total; split++){
			if((split == total) ||
					(data.instance(split).value(att) > // Can't split within
					data.instance(prev).value(att))){ // same value	    

				for(int y=prev; y<split; y++){
					Instance inst = data.instance(y);
					fstCover += inst.weight(); 
					if(Utils.eq(data.instance(y).classValue(), cl)){
						fstAccu += inst.weight();  // First bag positive# ++
					}	     		   
				}

				double fstAccuRate = (fstAccu+1.0)/(fstCover+1.0),
				sndAccuRate = (sndAccu+1.0)/(sndCover+1.0);

				/* Which bag has higher information gain? */
				boolean isFirst; 
				double fstInfoGain, sndInfoGain;
				double accRate, infoGain, coverage, accurate;

				fstInfoGain = 
					//Utils.eq(defAcRt, 1.0) ? 
					//fstAccu/(double)numConds : 
					fstAccu*(Utils.log2(fstAccuRate)-Utils.log2(defAcRt));

				sndInfoGain = 
					//Utils.eq(defAcRt, 1.0) ? 
					//sndAccu/(double)numConds : 
					sndAccu*(Utils.log2(sndAccuRate)-Utils.log2(defAcRt));

				if(fstInfoGain > sndInfoGain){
					isFirst = true;
					infoGain = fstInfoGain;
					accRate = fstAccuRate;
					accurate = fstAccu;
					coverage = fstCover;
				}
				else{
					isFirst = false;
					infoGain = sndInfoGain;
					accRate = sndAccuRate;
					accurate = sndAccu;
					coverage = sndCover;
				}

				/* Check whether so far the max infoGain */
				if(infoGain > maxInfoGain){
					splitPoint = data.instance(prev).value(att);
					value = (isFirst) ? 0 : 1;
					accuRate = accRate;
					accu = accurate;
					cover = coverage;
					maxInfoGain = infoGain;
					finalSplit = (isFirst) ? split : prev;
				}

				for(int y=prev; y<split; y++){
					Instance inst = data.instance(y);
					sndCover -= inst.weight(); 
					if(Utils.eq(data.instance(y).classValue(), cl)){
						sndAccu -= inst.weight();  // Second bag positive# --
					}	     		   
				}		    
				prev=split;
			}
		}

		/* Split the data */
		Instances[] splitData = new Instances[2];
		splitData[0] = new Instances(data, 0, finalSplit);
		splitData[1] = new Instances(data, finalSplit, total-finalSplit);



		return splitData;
	}

	/**
	 * The degree of coverage for the instance given that antecedent
	 * 
	 * @param inst the instance in question
	 * @return the numeric value indicating the membership of the instance 
	 *         for this antecedent
	 */
	public double covers(Instance inst){
		double isCover=0;
		if(!inst.isMissing(att)){

			if((int)value == 0){ // First bag
				if(inst.value(att) <= splitPoint)
					isCover=1;
				else if(fuzzyYet && (inst.value(att) > splitPoint) && (inst.value(att) < supportBound )) 
					isCover= 1-((inst.value(att) - splitPoint)/(supportBound-splitPoint));
			}
			else{ 
				if(inst.value(att) >= splitPoint) // Second bag
					isCover=1;
				else if(fuzzyYet && inst.value(att) < splitPoint && (inst.value(att) > supportBound )) 
					isCover= 1-((splitPoint - inst.value(att)) /(splitPoint-supportBound));
			}
		}
		return isCover;
	}

	/**
	 * Prints this antecedent
	 *
	 * @return a textual description of this antecedent
	 */
	public String toString() {
		String symbol = ((int)value == 0) ? " <= " : " >= ";
		if (fuzzyYet){
			return (att.name() + symbol + Utils.doubleToString(splitPoint, 6) + "(-> " + Utils.doubleToString(supportBound, 6) + ")");
		}
		return (att.name() + symbol + Utils.doubleToString(splitPoint, 6));
	}

}