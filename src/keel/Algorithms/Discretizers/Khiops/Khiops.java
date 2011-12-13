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

package keel.Algorithms.Discretizers.Khiops;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Vector;
import keel.Algorithms.Discretizers.Basic.Discretizer;
import keel.Algorithms.Preprocess.Missing_Values.EventCovering.Stat.*;
import keel.Dataset.Attributes;


/**
 * Khiops Discretizer
 * Implemented by Julian Luengo, March 2010
 * julianlm@decsai.ugr.es
 * 
 * Based on the work of Marc Boullé
 * 
 *	M. Boulle. 
 *	Khiops: A Statistical Discretization Method of Continuous Attributes 
 *	Machine Learning 55:1 (2004) 53-69
 *
 * <p>
 * @author Written by Julián Luengo Martín 18/03/2010
 * @version 0.2
 * @since JDK 1.5
 * </p>
 */
public class Khiops extends Discretizer {
	
	int numClasses;
	long freqConstraint;
	ArrayList<Double> chi2Rows;
	int nj[];
	
	public Khiops(){
		numClasses = Attributes.getOutputAttribute(0).getNumNominalValues();
		chi2Rows = new ArrayList<Double>();
		freqConstraint = 5;
	}

	@Override
	protected Vector discretizeAttribute(int attribute, int[] values,
			int begin, int end) {
		ArrayList<Double> substr,intA,intB;
		Vector cp,tmp,bestcp;
		double actualChi2,bestConfidenceLevel,confidenceLevel,discCostVariation,rightChi2;
		DeltaValue variation,nextInt;
		int iter;
		PriorityQueue<DeltaValue> deltas;
		ArrayList<DeltaValue> pts;
		boolean improvement,control,allMetFreqConstraint;
		
		freqConstraint = Math.round(Math.max(5, Math.sqrt(end+1)));
		
		cp = new Vector();
		//create initial discretization with number of interval equal to number of different values
		substr = new ArrayList<Double>(1);
		substr.add(realValues[attribute][values[0]]);
		for(int i=1;i<=end;i++){
			if(realValues[attribute][values[i]]!=substr.get(substr.size()-1)){
				cp.add(substr);
				substr = new ArrayList<Double>(1);
			}
			substr.add(realValues[attribute][values[i]]);
		}
		cp.add(substr);
		
		//now compute the confidence level value for this initial discretization
		//and initialize the chi2 rows (one for each initial interval)
		bestConfidenceLevel = chi2RowValues(cp,values);
		bestConfidenceLevel = 1.0 - StatFunc.chiSquare(bestConfidenceLevel, (cp.size()-1)*(numClasses-1));
		
		//compute the delta-values related to all the possible merges
		deltas = new PriorityQueue<DeltaValue>(end,Collections.reverseOrder());
		pts = new ArrayList<DeltaValue>();
		for(int i=0;i<cp.size()-1;i++){
			variation = new DeltaValue();
			variation.leftInterval = (ArrayList<Double>)cp.get(i);
			variation.rightInterval = (ArrayList<Double>)cp.get(i+1);
			variation.leftChi2Row = chi2Rows.get(i);
			variation.rightChi2Row = chi2Rows.get(i+1);
			
			if(variation.leftInterval.size()>=freqConstraint && variation.rightInterval.size()>=freqConstraint)
				variation.freqConstrMet = true;
			else
				variation.freqConstrMet = false;
			
			variation.index = i;
			if(i!=0){ //make list of pointers
				variation.prev = pts.get(pts.size()-1);
				variation.prev.next = variation;
			}

			variation.delta = mergeCostVariation(variation.leftInterval,i,variation.leftChi2Row,variation.rightInterval,i+1,variation.rightChi2Row,values);
			deltas.add(variation);
			pts.add(variation);
		}
		//sort the possible merges in ascending order...
		//...not needed since we use a priority queue
		
		//Now we optimize the initial discretization
		actualChi2 = 0;
		iter = 0;
		improvement = true;
		bestcp = new Vector();
		for(int i=0;i<cp.size();i++){
			substr = (ArrayList<Double>) cp.get(i);
			bestcp.add(substr.clone());
		}
		allMetFreqConstraint = false;
		while(deltas.size()>0 && (!allMetFreqConstraint || improvement) ){

			//check for the best merge
			variation = deltas.poll(); //take the first item -i.e. one with the highest deltaChi2 value-

			//do the intervals implied in this iteration meet the minimum frequency constraints?
			allMetFreqConstraint = variation.freqConstrMet;
			
			intA = variation.leftInterval;
			intB = variation.rightInterval;

			//merge the intervals
//			index = cp.indexOf(intA); //for debugging purposes
			intA.addAll(intB);

			//remove interval B from list of intervals -now is in interval A-
			intB.clear(); //clear the unnecessary interval, so become unique in its content-we haven't empty intervals by definition-, and
			//remove() method which follows cannot confuse it with other interval -and therefore erase it incorrectly- 
			control = cp.remove(intB);
			variation.leftChi2Row = variation.delta + variation.leftChi2Row + variation.rightChi2Row;
			control = chi2Rows.remove(variation.rightChi2Row);

			//update the list references
			nextInt = variation.next;
			if(nextInt!=null){
				variation.next = nextInt.next; //point above the interval B to next C
				variation.rightInterval = nextInt.rightInterval;
				variation.rightChi2Row = nextInt.rightChi2Row;
				if(variation.next != null){ //it is not the last interval in the list
					//update the next interval previous pointer to the new merged interval
					variation.next.prev = variation;
				}
			}
			//remove the merge of interval B with subsequent interval from both 
			//priority queue and control list
			control = deltas.remove(nextInt);
			control = pts.remove(nextInt);

			//compute the cost variation of the two intervals adjacent to the merge:
			//with the next one
			if(variation.rightInterval.size()!=0){
				variation.delta = mergeCostVariation(variation.leftInterval,variation.index,variation.leftChi2Row,variation.rightInterval,variation.index+variation.leftInterval.size(),variation.rightChi2Row,values);
				if(variation.leftInterval.size()>=freqConstraint && variation.rightInterval.size()>=freqConstraint)
					variation.freqConstrMet = true;
				//extract and re-insert in the queue to order this item
				//control = deltas.remove(variation); <-- not needed, already erased from poll() at beginning
				deltas.add(variation);
			}
			//with the previous one
			if(variation.prev != null){
				variation.prev.delta = mergeCostVariation(variation.prev.leftInterval,variation.prev.index,variation.prev.leftChi2Row,variation.leftInterval,variation.index,variation.leftChi2Row,values);
				if(variation.prev.leftInterval.size()>=freqConstraint && variation.prev.rightInterval.size()>=freqConstraint)
					variation.prev.freqConstrMet = true;
				//extract and re-insert in the queue to order this item
				control = deltas.remove(variation.prev);
				deltas.add(variation.prev);
			}
			if(variation.rightInterval.size()==0){
				deltas.remove(variation);
			}


			actualChi2 = 0;
			for(int i=0;i<chi2Rows.size();i++){
				actualChi2 += chi2Rows.get(i);
			}
			confidenceLevel = 1.0 - StatFunc.chiSquare(actualChi2, (cp.size()-1)*(numClasses-1));
			//the new discretization scheme is accepted if it decreases the confidence level
			//or if it has merged one or two intervals with less than "freqConstraint" elements
			if(confidenceLevel < bestConfidenceLevel || !allMetFreqConstraint){
				bestConfidenceLevel = confidenceLevel;
				bestcp = new Vector();
				for(int i=0;i<cp.size();i++){
					substr = (ArrayList<Double>) cp.get(i);
					bestcp.add(substr.clone());
				}
				improvement = true;
			} else
				improvement = false;
			
			//check that all intervals meet the minimum frequency constraint
			//that is, no merge at the top of the queue does not meet this constraint
			//(merges between intervals with less elements than the constraint are always on top)
			if(deltas.size()>0)
				allMetFreqConstraint = deltas.peek().freqConstrMet;
			
		}
		
		//return the best set of intervals
		return createCP(bestcp);
	}
	
	/**
	 * Computes the cost derived form merging two adjacent intervals na and nb
	 * @param na Interval to the left to merge
	 * @param indexna Index of the first element of na in the whole list of real values
	 * @param nb Right interval to merge
	 * @param indexnb Index of the first element of nb in the whole list of real values
	 * @param nbChi2 Current number of intervals (the total intervals prior to the merging)
	 * @param values Array in which position i there is the number of instance which explanatory (real) value has rank i after sorting
	 * @return The cost variation produced by the merge operation
	 */
	public double mergeCostVariation(ArrayList<Double> na,int indexna, double naChi2, ArrayList<Double> nb,int indexnb, double nbChi2,int values[]){
		double cost,newRowChi2;
		ArrayList<Double> merge = new ArrayList<Double>(na);
		
		merge.addAll(nb);

		newRowChi2 = mergedRowChi2Value(merge,indexna,values);
		
		cost = newRowChi2 - naChi2 - nbChi2;
		
		return cost;
		
	}
	
	/**
	 * Creates the initial chi square value of the initial discretization scheme.
	 * It also initialize the contribution to this value of each interval, so the combination
	 * of intervals can be quickly evaluated.
	 * @param disc the initial discretization scheme (one interval for each different value)
	 * @param values the global array of values (sorted)
	 * @return the chi square value for the initial discretization scheme
	 */
	public double chi2RowValues(Vector disc,int values[]){
		int n,I,J;
		int ni[];
		int nij[][];
		ArrayList<Double> interval;
		double chi2Value,eij;
		
		n = 0;
		I = disc.size();
		J = numClasses;
		ni = new int[I];
		nj = new int[J];
		nij = new int[I][J];
		chi2Rows = new ArrayList<Double>(I);

		for(int i=0,m=0;i<I;i++){
			interval = (ArrayList<Double>)disc.get(i); 
			ni[i] = interval.size();
			
			n += ni[i];

			for(int j=0;j<ni[i];j++,m++){
				nj[classOfInstances[values[m]]]++;
				
				nij[i][classOfInstances[values[m]]]++; 
			}
		}
		
		//TODO - optimize as there is only intervals with one element (second "for" can be avoided)
		chi2Value = 0;
		for(int i=0;i<I;i++){
			chi2Rows.add(0.0);
			for(int j=0;j<J;j++){
				eij = (double)(ni[i] * nj[j])/(n);
				
				chi2Rows.set(i,chi2Rows.get(i)+Math.pow(nij[i][j]-eij,2)/(double)eij);
			}
			chi2Value += chi2Rows.get(i);
		}
		
		
		return chi2Value;
	}
	
	/**
	 * This method calculates the contribution to the global chi square value
	 * of a new interval (produced by merging two adjacent ones).
	 * @param mergedInterval the new interval
	 * @param index the index of the first element (left-most one) value in the global array of values
	 * @param values the global array of values
	 * @return the contribution of this new interval to the global chi square value
	 */
	public double mergedRowChi2Value(ArrayList<Double> mergedInterval,int index,int values[]){
		int n,J;
		int ni,nij[];
		double rowChi2Value,eij;
		
		n = values.length;
		J = numClasses;
		ni = mergedInterval.size();
		nij = new int[J];

		for(int j=0;j<ni;j++){
			nij[classOfInstances[values[index+j]]]++;
		}
		
		rowChi2Value = 0;
		for(int j=0;j<J;j++){
			eij = (double)(ni * nj[j])/(n);
			
			rowChi2Value +=Math.pow(nij[j]-eij,2)/(double)eij;
		}
		
		return rowChi2Value;
	}

	
	/**
	   * Construct an array of cutpoints from the set of intervals.
	   * @param intervals Vector which contains the intervals in ArrayList<Double> format
	   * @return A Vector with double formatted cutpoints, computed as the midterm between two adjacent intervals.
	   */
	  public Vector createCP(Vector intervals){
		  double cutPoint;
		  Vector cp;
		  ArrayList<Double> substr; 
		  
		  cp = new Vector();
			for(int i=0;i<intervals.size()-1;i++){
				substr = (ArrayList<Double>)intervals.get(i);
				cutPoint = substr.get(substr.size()-1);
				substr = (ArrayList<Double>)intervals.get(i+1);
				cutPoint += substr.get(0);
				cutPoint /= 2.0;
//				if(cutPoint != substr.get(0))
					cp.add(new Double(cutPoint));
			}
			
			return cp;
	  }
}

