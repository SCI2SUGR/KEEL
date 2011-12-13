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

package keel.Algorithms.Discretizers.Id3_Discretizer;

import java.util.*;
import keel.Algorithms.Discretizers.Basic.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;


/**
 * <p>
 * This class implements the Id3Discretizer discretizer.
 * </p>
 * 
 * <p>
 * @author Written by Jaume Bacardit (La Salle, Ramón Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.5
 * </p>
 */
public class Id3Discretizer extends Discretizer {
	
	protected Vector discretizeAttribute(int attribute,int []values,int begin,int end) {
		Vector cd=classDistribution(attribute,values,begin,end);
		if(cd.size()==1) return new Vector();
		int numValues=sumValues(cd);
		double entAll=computeEntropy(cd,numValues);

		Vector candidateCutPoints = getCandidateCutPoints(attribute,values,begin,end);
		if(candidateCutPoints.size()==0) return new Vector();

		int posMin=((Integer)candidateCutPoints.elementAt(0)).intValue();
		double entMin=computePartitionEntropy(attribute,values,begin,posMin,end);
		for(int i=1,size=candidateCutPoints.size();i<size;i++) {
			int pos=((Integer)candidateCutPoints.elementAt(i)).intValue();
			double ent=computePartitionEntropy(attribute,values,begin,pos,end);
			if(ent<entMin) {
				entMin=ent;
				posMin=pos;
			}
		}

		if(entMin<entAll) {
			Vector res1=discretizeAttribute(attribute,values,begin,posMin-1);
			double cutPoint=(realValues[attribute][values[posMin-1]]+realValues[attribute][values[posMin]])/2.0;
			res1.addElement(new Double(cutPoint));
			Vector res2=discretizeAttribute(attribute,values,posMin,end);
			res1.addAll(res2);
			return res1;
		}
		return new Vector();
	}

	double computePartitionEntropy(int attribute,int []values,int begin,int midPoint,int end) {
		Vector cd1=classDistribution(attribute,values,begin,midPoint-1);
		Vector cd2=classDistribution(attribute,values,midPoint,end);

		int numValues1=sumValues(cd1);
		int numValues2=sumValues(cd2);

		double ent1=computeEntropy(cd1,numValues1);
		double ent2=computeEntropy(cd2,numValues2);
		return ((double)numValues1*ent1+(double)numValues2*ent2)/(double)(numValues1+numValues2);
	}

	double computeEntropy(Vector v,int numValues) {
		double ent=0;

		for(int i=0,size=v.size();i<size;i++) {
			double prob=((Integer)v.elementAt(i)).intValue();
			prob/=(double)numValues;
			ent+=prob*Math.log(prob)/Math.log(2);
		}
		return -ent;
	}

	int sumValues(Vector v) {
		int sum=0;
		for(int i=0,size=v.size();i<size;i++) {
			sum+=((Integer)v.elementAt(i)).intValue();
		}
		return sum;
	}

	Vector getCandidateCutPoints(int attribute,int []values,int begin,int end) {
		Vector cutPoints = new Vector();
		double valueAnt=realValues[attribute][values[begin]];

		for(int i=begin;i<=end;i++) {
			double val=realValues[attribute][values[i]];
			if(val!=valueAnt) cutPoints.addElement(new Integer(i));
			valueAnt=val;
		}
		return cutPoints;
	}


	Vector classDistribution(int attribute,int []values,int begin,int end) {
		int []classCount = new int[Parameters.numClasses];
		for(int i=0;i<Parameters.numClasses;i++) classCount[i]=0;

		for(int i=begin;i<=end;i++) classCount[classOfInstances[values[i]]]++;
		
		Vector res= new Vector();
		for(int i=0;i<Parameters.numClasses;i++) {
			if(classCount[i]>0) res.addElement(new Integer(classCount[i]));
		}

		return res;
	}
		

}

