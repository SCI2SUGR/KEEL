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

package keel.Algorithms.Discretizers.ChiMerge_Discretizer;

import java.util.*;
import keel.Algorithms.Discretizers.Basic.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;


/**
 * <p>
 * This class implements the USD discretizer.
 * </p>
 * 
 * <p>
 * @author Written by Jaume Bacardit (La Salle, Ramón Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.5
 * </p>
 */
public class ChiMergeDiscretizer extends Discretizer {
	
	double confidenceThreshold;

	/**
	* Builder
	* @param _conf Confidence threshold
	*/
	public ChiMergeDiscretizer(double _conf) {
		confidenceThreshold=_conf;
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
		Vector intervals=mergeEqualValues(attribute,values,begin,end);
		boolean exit=false;
		int numTemp = (Parameters.numInstances / (100)) > Parameters.numClasses?Parameters.numInstances / (100):Parameters.numClasses;

		double threshold=critchi(confidenceThreshold
			,Parameters.numClasses-1);

		while(intervals.size()>1 && !exit) {
			int posMin=-1;
			double chiMin=0;
			for(int i=0;i<intervals.size()-1;i++) {
				Interval int1=(Interval)intervals.elementAt(i);
				Interval int2=(Interval)intervals.elementAt(i+1);
				double chi2=0;
				double []R=new double[2];
				double []C=new double[int1.cd.length];
				double [][]A = new double[2][];
				double N=0;
				for(int j=0;j<2;j++) {
					R[j]=0;
					A[j]=new double[int1.cd.length];
				}
				for(int j=0;j<int1.cd.length;j++) C[j]=0;

				for(int j=0;j<int1.cd.length;j++) {
					A[0][j]=int1.cd[j];
					A[1][j]=int2.cd[j];
					R[0]+=int1.cd[j];
					R[1]+=int2.cd[j];
					C[j]+=int1.cd[j];
					C[j]+=int2.cd[j];
				}
				for(int j=0;j<2;j++) N+=R[j];

				for(int j=0;j<2;j++) {
					for(int k=0;k<int1.cd.length;k++) {
						double exp=R[j]*C[k]/N;
						if(R[j]==0 || C[k]==0) exp=0.1;
				
						chi2+=(A[j][k]-exp)*(A[j][k]-exp)/exp;
					}
				}
				if(posMin==-1) {
					posMin=i;
					chiMin=chi2;
				} else {
					if(chi2<chiMin) {
						posMin=i;
						chiMin=chi2;
					}
				}
			}

			if(intervals.size()>numTemp || chiMin<threshold) {
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

	Vector mergeEqualValues(int attribute,int []values,int begin,int end) {
		Vector intervals = new Vector();
		int beginAnt=begin;
		double valueAnt=realValues[attribute][values[begin]];

		for(int i=begin+1;i<=end;i++) {
			double val=realValues[attribute][values[i]];
			if(val!=valueAnt) {
				intervals.addElement(new Interval(attribute,values,beginAnt,i-1));
				beginAnt=i;
				valueAnt=val;
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
		

	final static double	Z_EPSILON=0.000001      ;
	final static double	Z_MAX=6.0   ;

	double poz_orig (double z)
	{
		double	y, x, w;
	
		if (z == 0.0)
			x = 0.0;
		else {
			y = 0.5 * Math.abs (z);
			if (y >= (Z_MAX * 0.5))
				x = 1.0;
			else if (y < 1.0) {
				w = y*y;
				x = ((((((((0.000124818987 * w
					-0.001075204047) * w +0.005198775019) * w
					-0.019198292004) * w +0.059054035642) * w
					-0.151968751364) * w +0.319152932694) * w
					-0.531923007300) * w +0.797884560593) * y * 2.0;
			} else {
				y -= 2.0;
				x = (((((((((((((-0.000045255659 * y
					+0.000152529290) * y -0.000019538132) * y
					-0.000676904986) * y +0.001390604284) * y
					-0.000794620820) * y -0.002034254874) * y
					+0.006549791214) * y -0.010557625006) * y
					+0.011630447319) * y -0.009279453341) * y
					+0.005353579108) * y -0.002141268741) * y
					+0.000535310849) * y +0.999936657524;
			}
		}
	
		return (z > 0.0 ? ((x + 1.0) * 0.5) : ((1.0 - x) * 0.5));
	}

	final static double CHI_EPSILON=0.000001;
	final static double CHI_MAX=99999.0;
	final static double LOG_SQRT_PI=0.5723649429247000870717135;
	final static double I_SQRT_PI=0.5641895835477562869480795;
	final static double BIGX=20.0;

	double ex(double x) {
		return (((x) < -BIGX) ? 0.0 : Math.exp (x));
	}

	double pochisq(double x, int df)
	{
		double a, y=0, s;
		double e, c, z;
		boolean even;		/* true if df is an even number */
	
		if (x <= 0.0 || df < 1)
			return (1.0);
	
		a = 0.5 * x;
		even = (2 * (df / 2)) == df;
		if (df > 1)
			y = ex(-a);
		s = (even ? y : (2.0 * poz_orig(-Math.sqrt(x))));
		if (df > 2) {
			x = 0.5 * (df - 1.0);
			z = (even ? 1.0 : 0.5);
			if (a > BIGX) {
				e = (even ? 0.0 : LOG_SQRT_PI);
				c = Math.log(a);
				while (z <= x) {
					e = Math.log(z) + e;
					s += ex(c * z - a - e);
					z += 1.0;
				}
				return (s);
			} else {
				e = (even ? 1.0 : (I_SQRT_PI / Math.sqrt(a)));
				c = 0.0;
				while (z <= x) {
					e = e * (a / z);
					c = c + e;
					z += 1.0;
				}
				return (c * y + s);
			}
		} else
		return (s);
	}

	double critchi (double p, int df) {
		double	minchisq = 0.0;
		double	maxchisq = CHI_MAX;
		double	chisqval;
		
		if (p <= 0.0)
			return (maxchisq);
		else if (p >= 1.0)
			return (0.0);
						
		chisqval = df / Math.sqrt (p);    /* fair first value */
		while (maxchisq - minchisq > CHI_EPSILON) {
			if (pochisq (chisqval, df) < p)
				maxchisq = chisqval;
			else
				minchisq = chisqval;
			chisqval = (maxchisq + minchisq) * 0.5;
		}
		return (chisqval);
	}
}

