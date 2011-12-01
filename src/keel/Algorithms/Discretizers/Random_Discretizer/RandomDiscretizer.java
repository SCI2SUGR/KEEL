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

package keel.Algorithms.Discretizers.Random_Discretizer;

import java.util.*;
import keel.Algorithms.Discretizers.Basic.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;


/**
 * 
 * This class implements the Random Discretizer
 *
 */
public class RandomDiscretizer extends Discretizer {
	protected Vector discretizeAttribute(int attribute,int []values,int begin,int end) {
		Vector cd=classDistribution(attribute,values,begin,end);
		if(cd.size()==1) return new Vector();

		Vector candidateCutPoints = getCandidateCutPoints(attribute,values,begin,end);
		if(candidateCutPoints.size()==0) return new Vector();
		
		int numCP=Rand.getInteger(1,candidateCutPoints.size());
		Vector cutPoints=new Vector();
		
		for(int i=0;i<numCP;i++) {
			int pos=Rand.getInteger(0,candidateCutPoints.size()-1);
			int val=((Integer)candidateCutPoints.elementAt(pos)).intValue();
			candidateCutPoints.removeElementAt(pos);
			double cutPoint=(realValues[attribute][values[val-1]]+realValues[attribute][values[val]])/2.0;
			boolean endLoop=false;
			int insertPos=-1;
			for(int j=0;j<cutPoints.size() && !endLoop;j++) {
				if(cutPoint<((Double)cutPoints.elementAt(j)).doubleValue()) {
					endLoop=true;
					insertPos=j;
				}
			}
			if(endLoop) {
				cutPoints.insertElementAt(new Double(cutPoint),insertPos);
			} else {
				cutPoints.addElement(new Double(cutPoint));
			}
		}
		return cutPoints;
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