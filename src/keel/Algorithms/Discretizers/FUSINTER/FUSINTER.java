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

package keel.Algorithms.Discretizers.FUSINTER;

import java.util.*;
import keel.Algorithms.Discretizers.Basic.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;


/**
 * <p>
 * This class implements the FUSINTER discretizer.
 * </p>
 * 
 * @author Written by Salvador García (University of Jaén) 4/05/2011
 * @version 1.1
 * @since JDK1.5
 */
public class FUSINTER extends Discretizer {
	
	
	double lambda;
	double alpha;

	/**
	* Builder
	* 
	*/
	public FUSINTER(double _lambda, double _alpha) {
		lambda = _lambda;
		alpha = _alpha;
	}

	private class Interval {
	/**
	 * <p>
	 * Interval class.
	 * </p>
	 */	
		
		int attribute;
		int begin;
		int end;
		int []values;
		int []cd;
		
		/**
		 * <p>
		 * Compute the interval ratios.
		 * </p>
		 * @param _attribute
		 * @param []_values
		 * @param _begin
		 * @param _end
		 */
		public Interval(int _attribute,int []_values,int _begin,int _end) {
			attribute=_attribute;
			begin=_begin;
			end=_end;
			values=_values;

			computeIntervalRatios();
		}

		void computeIntervalRatios() {
			cd=classDistribution(attribute,values,begin,end);
		}
		
		/**
		 * <p>
		 * Enlarge the interval using a new "end"
		 * </p>
		 * @param newEnd indicates the new end
		 */
		public void enlargeInterval(int newEnd) {
			end=newEnd;
			computeIntervalRatios();
		}
	}


	protected Vector discretizeAttribute(int attribute,int []values,int begin,int end) {
		Vector <Interval> intervals=mergeEqualValues(attribute,values,begin,end);
		boolean exit=false;
		double criterion;

		while(intervals.size()>1 && !exit) {
			int posMin=-1;
			double maxCri=0;
			double eval = eval_2 (intervals, alpha, lambda);
			for(int i=0;i<intervals.size()-1;i++) {
				criterion = eval - eval_2(intervals, alpha, lambda, i);
				if(posMin==-1) {
					posMin = i;
					maxCri = criterion;
				} else {
					if(criterion > maxCri) {
						posMin=i;
						maxCri = criterion;
					}
				}
			}

			if(maxCri > 0) {
				Interval int1=(Interval)intervals.elementAt(posMin);
				Interval int2=(Interval)intervals.elementAt(posMin+1);
				int1.enlargeInterval(int2.end);
				intervals.removeElementAt(posMin+1);
			} else {
				exit=true;
			}
		}


		Vector cutPoints=new Vector();
		for(int i=0;i<intervals.size()-1;i++) {
			Interval int1=(Interval)intervals.elementAt(i);
			Interval int2=(Interval)intervals.elementAt(i+1);
			double cutPoint=(realValues[attribute][values[int1.end]]+realValues[attribute][values[int2.begin]])/2.0;
			cutPoints.addElement(new Double(cutPoint));
		}
		return cutPoints;
	}

	Vector <Interval> mergeEqualValues(int attribute,int []values,int begin,int end) {
		Vector <Interval> intervals = new Vector <Interval> ();
		int beginAnt=begin;
		double valueAnt=realValues[attribute][values[begin]];
		int classAnt = classOfInstances[values[begin]];

		for(int i=begin+1;i<=end;i++) {
			double val=realValues[attribute][values[i]];
			int clas = classOfInstances[values[i]];
			if(val!=valueAnt) {
				if (clas != classAnt) {
					intervals.addElement(new Interval(attribute,values,beginAnt,i-1));
					beginAnt=i;
					valueAnt=val;
					classAnt=clas;
				}
			}
		}
		intervals.addElement(new Interval(attribute,values,beginAnt,end));
		return intervals;
	}


	int []classDistribution(int attribute,int []values,int begin,int end) {
		int []classCount = new int[Parameters.numClasses];
		for(int i=0;i<Parameters.numClasses;i++) classCount[i]=0;

		for(int i=begin;i<=end;i++) classCount[classOfInstances[values[i]]]++;
		return classCount;	
	}
	
	double eval_2 (Vector <Interval> intervals, double alpha, double lambda) {
		
		int i, j;
		int Nj;
		double suma;
		double factor;
		double total = 0;
		
		for (i=0; i<intervals.size(); i++) {
			Nj = 0;
			for (j=0; j<Parameters.numClasses; j++) {
				Nj += intervals.elementAt(i).cd[j];
			}
			suma = 0;
			for (j=0; j<Parameters.numClasses; j++) {
				factor = (intervals.elementAt(i).cd[j] + lambda) / (Nj + Parameters.numClasses*lambda);
				suma += factor * (1 - factor);
			}
			total += (alpha * ((double)Nj / (double)Parameters.numInstances) * suma);
			total += ((1 - alpha) * (((double)Parameters.numClasses * lambda) / (double)Nj));
		}
		
		return total;
	}
	
	double eval_2 (Vector <Interval> intervals, double alpha, double lambda, int merged) {
		
		int i, j;
		int Nj;
		double suma;
		double factor;
		double total = 0;
		
		for (i=0; i<intervals.size(); i++) {
			if (i==merged) {
				Nj = 0;
				for (j=0; j<Parameters.numClasses; j++) {
					Nj += intervals.elementAt(i).cd[j];
					Nj += intervals.elementAt(i+1).cd[j];
				}
				suma = 0;
				for (j=0; j<Parameters.numClasses; j++) {
					factor = (intervals.elementAt(i).cd[j] + intervals.elementAt(i+1).cd[j] + lambda) / (Nj + Parameters.numClasses*lambda);
					suma += factor * (1 - factor);
				}
				total += (alpha * ((double)Nj / (double)Parameters.numInstances) * suma) + ((1 - alpha) * (((double)Parameters.numClasses * lambda) / (double)Nj));
			} else if (i==merged+1) {
				
			} else {
				Nj = 0;
				for (j=0; j<Parameters.numClasses; j++) {
					Nj += intervals.elementAt(i).cd[j];
				}
				suma = 0;
				for (j=0; j<Parameters.numClasses; j++) {
					factor = (intervals.elementAt(i).cd[j] + lambda) / (Nj + Parameters.numClasses*lambda);
					suma += factor * (1 - factor);
				}
				total += (alpha * ((double)Nj / (double)Parameters.numInstances) * suma) + ((1 - alpha) * (((double)Parameters.numClasses * lambda) / (double)Nj));
			}
		}
		
		return total;
	}
	
}