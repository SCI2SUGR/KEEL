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

package keel.Algorithms.Discretizers.MODL;

import java.util.*;
import keel.Algorithms.Discretizers.Basic.*;
import keel.Dataset.Attributes;

/**
 * MODL Discretizer, based on the work of Marc Boullé
 * 
 *	M. Boulle. 
 *	MODL: A bayes optimal discretization method for continuous attributes. 
 *	Machine Learning 65:1 (2006) 131-165
 * <p>
 * @author Written by Julián Luengo Martín 07/05/2008
 * @version 0.2
 * @since JDK 1.5
 * </p>
 */
public class MODL extends Discretizer {
	int numClasses;
	int discretizationApplied;
	
	static int optimal = 1;
	static int greedy = 2;
	static int optimized = 3;
	
	public MODL(String processType){
		if(processType.compareTo("optimal")==0)
			discretizationApplied = optimal;
		if(processType.compareTo("greedy")==0)
			discretizationApplied = greedy;
		if(processType.compareTo("optimized")==0)
			discretizationApplied = optimized;
		
		numClasses = Attributes.getOutputAttribute(0).getNumNominalValues();
	}
	
	@Override
	protected Vector discretizeAttribute(int attribute,int []values,int begin,int end) {
		if(discretizationApplied == optimal)
			return optimalMODL(attribute,values,begin,end);
		if(discretizationApplied == greedy)
			return greedyMODL(attribute,values,begin,end);
		
		return postOptimizationMODL(attribute,values,begin,end);
	}
	
	/**
	 * Implements the post-optimization procedure for MODL, after obtaining the 
	 * best initial interval division.
	 * @param attribute the attribute which is being discretized
	 * @param values the values of the attribute
	 * @param begin the initial position of the values
	 * @param end the final position of the values
	 * @return the best discretization scheme found
	 */
	protected Vector postOptimizationMODL(int attribute,int []values,int begin,int end) {
		Vector cp,bckp,tmp;
		ArrayList<Double> substr,intA,intB=null,intC=null;
		int index,step;
		Neighbour neig;
		double mergeCost = 0;
		boolean improvement;
		PriorityQueue<Neighbour> bestNeighs = new PriorityQueue<Neighbour>();
		
		//compute the exhaustive merge
		cp = exhaustiveMerge(attribute,values,begin,end);
		
		//now proceed to the stage greedy post-optimization
		//explore all neighbours of the current discretization
		improvement = true;
		while(improvement){
			improvement = false;
			bestNeighs.clear();
			//lets search for the best neighbour in our hill-climbing
			//post-optimization algorithm
			index = 0;
			for(int i=0;i<cp.size();i++){
				intA = (ArrayList<Double>)cp.get(i);
				step = intA.size();
				
				//test the Split neighbour over S(i)
				neig = split(intA,index,realValues.length,cp.size(),values);
				neig.intervalPosition = i;
				neig.type = Neighbour.Split;
				if(neig.cost < 0 && Math.abs(neig.cost)>1E-5) //avoid precission leaks
					bestNeighs.add(neig);
				
				//test the MergeSplit neighbour over {S(i)}+{S(i+1)}
				if(i<cp.size()-1){
					intB = (ArrayList<Double>)cp.get(i+1);
					intC = new ArrayList<Double>();
					intC.addAll(intA);
					intC.addAll(intB);
					
					//partition cost variation
					mergeCost = partitionCost(cp.size()-1,realValues.length);
					mergeCost -= partitionCost(cp.size(),realValues.length);
					//interval cost variation
					mergeCost += intervalCost(intC,index,values, cp.size());
					mergeCost -= intervalCost(intA,index,values, cp.size());
					mergeCost -= intervalCost(intB,index+intA.size(),values, cp.size());
					//search for the best split on the merge
					neig = split(intC,index,realValues.length,cp.size()-1,values);
					neig.intervalPosition = i;
					neig.type = Neighbour.MergeSplit;
					neig.cost += mergeCost;
					if(neig.cost < 0 && Math.abs(neig.cost)>1E-5) //avoid precission leaks
						bestNeighs.add(neig);
				}
				
				//test the MergeMergeSplit neighbour over {S(i)}+{S(i+1)}+{S(i+2)}
				//be AWARE of we can enter here if we could enter to MergeSplit,
				//so some intervals and partially mergeCost are filled/computed
				if(i<cp.size()-2){
					intA = new ArrayList<Double>();
					intA.addAll(intC);
					intB = (ArrayList<Double>)cp.get(i+2);
					intC.addAll(intB);
					
					//partition cost variation
					mergeCost += partitionCost(cp.size()-2,realValues.length);
					mergeCost -= partitionCost(cp.size()-1,realValues.length);
					//interval cost variation
					mergeCost += intervalCost(intC,index,values, cp.size());
					mergeCost -= intervalCost(intA,index,values, cp.size());
					mergeCost -= intervalCost(intB,index+intA.size(),values, cp.size());

					//search for the best split on the merge
					neig = split(intC,index,realValues.length,cp.size()-2,values);
					neig.intervalPosition = i;
					neig.type = Neighbour.MergeMergeSplit;
					neig.cost += mergeCost;
					if(neig.cost < 0 && Math.abs(neig.cost)>1E-5) //avoid precission leaks
						bestNeighs.add(neig);
				}
				//step to the next candidate interval
				index += step;
			}
			if(bestNeighs.size()>0){
				improvement = true;
				//take the best neighbour...
				neig = bestNeighs.poll();
				//...and apply it
				applyNeighbour(cp,neig);
			}
		}
		
		//build the cutpoints
		return createCP(cp);
	}
	
	/**
	 * Search for the best cutpoint in a given interval. The best cutpoint is located
	 * by means of the cost of each cutpoint (if negative, improves the actual interval).
	 * @param interv The interval which could be partitioned
	 * @param index Index of the first element of the interval in the complete real value list
	 * @param n Total number of real values
	 * @param I Current number of intervals
	 * @param values Mapping between instance number and the sorted rank by attribute values 
	 * @return The best cutpoint if found: [0,cutpoint) and [cutpoint,n_i], or -1 if no cutpoint improves the current interval cost
	 */
	protected Neighbour split(ArrayList<Double> interv,int index,int n,int I,int values[]){
		double cost,partitionCost,bestCost,intervCost;
		ArrayList<Double> lstr,rstr;
		int splitIndex = -1;
		Neighbour neig = new Neighbour();
		
		partitionCost = partitionCost(I+1,n) - partitionCost(I,n);
		intervCost = intervalCost(interv,index,values,I);
		
		lstr = new ArrayList<Double>();
		rstr = new ArrayList<Double>();
		bestCost = Double.MAX_VALUE;
		for(int i=1;i<interv.size()-1;i++){
			//the cutpoints MUST split different values!
			if(interv.get(i-1).doubleValue()!=interv.get(i).doubleValue()){
				lstr.addAll(interv.subList(0, i)); //sublist [0,i)
				rstr.addAll(interv.subList(i, interv.size())); //sublist [i,ni+1)

				cost = partitionCost + intervalCost(lstr,index,values,I);
				cost += intervalCost(rstr,index+i,values,I);
				cost -= intervCost;
				if(cost < bestCost){
					splitIndex = i; 
					bestCost = cost;
				}
				lstr.clear();
				rstr.clear();
			}
		}
		
		neig.cost = bestCost;
		neig.index = splitIndex;
		neig.interval = interv;
		return neig;
	}
	/**
	 * Apply the neighbour to the current interval set.
	 * @param cp The interval set
	 * @param neig The neighbour (Split, MergeSplit or MergeMergeSplit) we want to apply
	 */
	public void applyNeighbour(Vector cp, Neighbour neig){
		ArrayList<Double> intA,intB,intC;
		int cutpoint,position;
		
		position = neig.intervalPosition;
		cutpoint = neig.index;
		intA = (ArrayList<Double>)cp.get(position);
		
		if(neig.type == Neighbour.Split){
			intB = new ArrayList<Double>(intA.subList(cutpoint, intA.size()));
			for(int i=cutpoint;i<intA.size();)
				intA.remove(i);
			cp.insertElementAt(intB, position+1);
			
		}else if(neig.type == Neighbour.MergeSplit){
			intC = new ArrayList<Double>(intA);
			intB = (ArrayList<Double>)cp.get(position+1);
			intC.addAll(intB);
			
			intB = new ArrayList<Double>(intC.subList(cutpoint, intC.size()));
			for(int i=cutpoint;i<intC.size();)
				intC.remove(i);
			cp.set(position, intC);
			cp.set(position+1, intB);
			
		}else if(neig.type == Neighbour.MergeMergeSplit){
			intC = new ArrayList<Double>(intA);
			intB = (ArrayList<Double>)cp.get(position+1);
			intC.addAll(intB);
			intB = (ArrayList<Double>)cp.get(position+2);
			intC.addAll(intB);
			
			intB = new ArrayList<Double>(intC.subList(cutpoint, intC.size()));
			for(int i=cutpoint;i<intC.size();)
				intC.remove(i);
			cp.remove(position+2);
			cp.set(position, intC);
			cp.set(position+1, intB);
		}
	}
	
	/**
	 * Computes the cost of the partition
	 * @param I the number of intervals
	 * @param n the number of different elements
	 * @return the cost of the number of partitions
	 */
	public double partitionCost(int I,int n){
		return binomialLog(n+I-1,I-1);
	}
	
	/**
	 * Computes the cost of the interval in the current discretization scheme
	 * @param interval the interval to be considered
	 * @param index the index of the intial element of the interval in the global array of values
	 * @param values the global array of values
	 * @param I the current number of intervals
	 * @return the cost of the interval
	 */
	public double intervalCost(ArrayList<Double> interval,int index,int values[],int I){
		double cost;
		int ni,nij[];
		int J = numClasses;
		
		ni = interval.size();
		
		cost = binomialLog(ni+numClasses-1,numClasses-1);
			
		nij = new int[J];
 
		for(int j=0;j<ni;j++){
			nij[classOfInstances[values[index+j]]]++; 
		}		
		
		cost += factorialLog(ni);
		for(int j=0;j<nij.length;j++)
			cost -= factorialLog(nij[j]);
		
		return cost;
	}
	/**
	 * Performs an exhaustive bottom-up merge of all unitary intervals to a unique interval.
	 *  The best configuration is returned.
	 * @param attribute The attribute of the data set we are discretizing
	 * @param values Mapping between instance number and the sorted rank by attribute values
	 * @param begin First position of values to be considered.
	 * @param end Last position of values to be considered.
	 * @return The best discretization configuration.
	 */
	protected Vector exhaustiveMerge(int attribute,int []values,int begin,int end) {
		ArrayList<Double> substr,intA,intB;
		Vector cp,tmp,bestcp;
		double actualMODL,bestMODL,partitioncostVariation,discCostVariation;
		DeltaValue variation,nextInt;
		int iter;
		PriorityQueue<DeltaValue> deltas;
		ArrayList<DeltaValue> pts;
		boolean improvement,control;
		
		cp = new Vector();
		//create initial discretization with number of interval equal to number of values
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
		//now compute the MODL value for this initial discretization
		bestMODL = modl(cp,values);
		
		//compute the delta-values related to all the possible merges
		deltas = new PriorityQueue<DeltaValue>(end);
		pts = new ArrayList<DeltaValue>();
		for(int i=0;i<cp.size()-1;i++){
			variation = new DeltaValue();
			variation.leftInterval = (ArrayList<Double>)cp.get(i);
			variation.rightInterval = (ArrayList<Double>)cp.get(i+1);
			variation.index = i;
			if(i!=0){ //make list pointers
				variation.prev = pts.get(pts.size()-1);
				variation.prev.next = variation;
			}

			variation.delta = mergeCostVariation(variation.leftInterval,i,variation.rightInterval,i+1,end,values);
			deltas.add(variation);
			pts.add(variation);
		}
		//sort the possible merges in ascending order...
		//...not needed since we use a priority queue
		
		//Now we optimize the discretization
		actualMODL = 0;
		iter = 0;
		variation = deltas.poll();
		improvement = true;
		bestcp = new Vector();
		for(int i=0;i<cp.size();i++){
			substr = (ArrayList<Double>) cp.get(i);
			bestcp.add(substr.clone());
		}
		while(cp.size() > 1 && iter<cp.size()){
			intA = variation.leftInterval;
			intB = variation.rightInterval;

			//join the intervals' values
//			index = cp.indexOf(intA); //for debugging purposes
			intA.addAll(intB);

			//remove interval B from list of intervals -now is in interval A-
			intB.clear(); //clear the unnecessary interval, so become unique in its content-we haven't empty intervals by definition-, and
			//remove() method which follows cannot confuse it with other interval -and therefore erase it incorrectly- 
			control = cp.remove(intB);

			//update the list references
			nextInt = variation.next;
			if(nextInt!=null){
				variation.next = nextInt.next; //point above the interval B to next C
				variation.rightInterval = nextInt.rightInterval;
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
			//with the next
			if(variation.rightInterval.size()!=0){
				variation.delta = mergeCostVariation(variation.leftInterval,variation.index,variation.rightInterval,variation.index+variation.leftInterval.size(),end,values);
				//extract and re-insert in the queue to order this item
				//control = deltas.remove(variation); <-- already erased from poll
				deltas.add(variation);
			}
			//with the previous
			if(variation.prev != null){
				variation.prev.delta = mergeCostVariation(variation.prev.leftInterval,variation.prev.index,variation.leftInterval,variation.index,end,values);
				//extract and re-insert in the queue to order this item
				control = deltas.remove(variation.prev);
				deltas.add(variation.prev);
			}
			if(variation.rightInterval.size()==0){
				deltas.remove(variation);
			}


			actualMODL = modl(cp,values);
			if(actualMODL < bestMODL){
				bestMODL = actualMODL;
				bestcp = new Vector();
				for(int i=0;i<cp.size();i++){
					substr = (ArrayList<Double>) cp.get(i);
					bestcp.add(substr.clone());
				}
			}
			iter++;
			variation = deltas.poll(); //take the first item -i.e. one such has higher/positive value-
		}
		
		//return the best set of intervals
		return bestcp;
	}
	
	/**
	 * This method implements the greedy version of the MODL discretizer.
	 * It is a bottom up proccess which merges the two more appropriate intervals, until
	 * no improvement can be done to the global MODL value.
	 * @param attribute the attribute which is being discretized
	 * @param values the global array of values (sorted)
	 * @param begin the initial position of the values to be discretized
	 * @param end the final position of the values  to be discretized
	 * @return the best discretization scheme found
	 */
	protected Vector greedyMODL(int attribute,int []values,int begin,int end) {
		ArrayList<Double> substr,intA,intB;
		Vector cp,tmp;
		double actualMODL,bestMODL,partitioncostVariation,discCostVariation;
		DeltaValue variation,nextInt;
		int iter;
		PriorityQueue<DeltaValue> deltas;
		ArrayList<DeltaValue> pts;
		boolean improvement,control;
		
		cp = new Vector();
		//create initial discretization with number of interval equal to number of values
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
		//now compute the MODL value for this initial discretization
		bestMODL = modl(cp,values);
		
		//compute the delta-values related to all the possible merges
		deltas = new PriorityQueue<DeltaValue>(end);
		pts = new ArrayList<DeltaValue>();
		for(int i=0;i<cp.size()-1;i++){
			variation = new DeltaValue();
			variation.leftInterval = (ArrayList<Double>)cp.get(i);
			variation.rightInterval = (ArrayList<Double>)cp.get(i+1);
			variation.index = i;
			if(i!=0){ //make list pointers
				variation.prev = pts.get(pts.size()-1);
				variation.prev.next = variation;
			}

			variation.delta = mergeCostVariation(variation.leftInterval,i,variation.rightInterval,i+1,end,values);
			deltas.add(variation);
			pts.add(variation);
		}
		//sort the possible merges in ascending order...
		//...not needed since we use a priority queue
		
		//Now we optimize the discretization
		actualMODL = 0;
		iter = 0;
		variation = deltas.poll();
		improvement = true;
		while(cp.size() > 1 && improvement && iter<cp.size()){
			partitioncostVariation = Math.log((double)(cp.size()-1)/(realValues.length+cp.size()-1));
			
			discCostVariation = partitioncostVariation + variation.delta;
			if(discCostVariation < 0){
				intA = variation.leftInterval;
				intB = variation.rightInterval;

				//join the intervals' values
//				index = cp.indexOf(intA); //for debugging purposes
				intA.addAll(intB);

				//remove interval B from list of intervals -now is in interval A-
				intB.clear(); //clear the unnecessary interval, so become unique in its content-we haven't empty intervals by definition-, and
				//remove() method which follows cannot confuse it with other interval -and therefore erase it incorrectly- 
				control = cp.remove(intB);

				//update the list references
				nextInt = variation.next;
				if(nextInt!=null){
					variation.next = nextInt.next; //point above the interval B to next C
					variation.rightInterval = nextInt.rightInterval;
					if(variation.next != null){ //it is not the last interval in the list
						//update the next interval previous pointer to the new merged interval
						variation.next.prev = variation;
					}
				}
				//remove the interval B from both priority queue and list
				control = deltas.remove(nextInt);
				control = pts.remove(nextInt);

				//compute the cost variation of the two intervals adjacent to the merge:
				//with the next
				if(variation.rightInterval.size()!=0){
					variation.delta = mergeCostVariation(variation.leftInterval,variation.index,variation.rightInterval,variation.index+variation.leftInterval.size(),end,values);
					//extract and re-insert in the queue to order this item
					//control = deltas.remove(variation); <-- already erased from poll
					deltas.add(variation);
				}
				//with the previous
				if(variation.prev != null){
					variation.prev.delta = mergeCostVariation(variation.prev.leftInterval,variation.prev.index,variation.leftInterval,variation.index,end,values);
					//extract and re-insert in the queue to order this item
					control = deltas.remove(variation.prev);
					deltas.add(variation.prev);
				}
				if(variation.rightInterval.size()==0){
					deltas.remove(variation);
				}

				actualMODL = modl(cp,values);
				if(actualMODL < bestMODL){
					bestMODL = actualMODL;					
				}
				iter++;
				variation = deltas.poll(); //take the first item -i.e. one such has higher/positive value-
			}else{
				improvement = false;
			}
		}
		
		//compute the cutpoints from the intervals
		return createCP(cp);
	}
	
	/**
	 * It seachs for the best possible optimization scheme.
	 * It is a VERY slow process, so it is not recommended.
	 * @param attribute the attribute to be discretized
	 * @param values the global array of values (sorted)
	 * @param begin the initial position of the array
	 * @param end the final position in the array
	 * @return the best global discretization scheme
	 */
	protected Vector optimalMODL(int attribute,int []values,int begin,int end) {
		Vector disc[][] = new Vector[end+1][end+1];
		Vector tmp,cp;
		ArrayList<Double> substr;
		double minMODL,actualMODL,bestMODL;
		int optimalIntervalNumber;
	
		minMODL = bestMODL = Double.MAX_VALUE;
		optimalIntervalNumber = -1;
		//for all possible intervals
		for(int k=0;k<=end;k++){
			//for all instances in the data set
			for(int j=0;j<=end;j++){
				minMODL = Double.MAX_VALUE;
				if(k == 0){
					//create a substring {S(i,j)} of all elements from the 1st to jth
					disc[j][0] = new Vector();
					substr = new ArrayList<Double>();
					for(int m=0;m<=j;m++)
						substr.add(realValues[attribute][values[m]]);
					disc[j][0].add(substr);
				}else{
					//find disc(S(1,j),k) which minimizes all discretizations
					//disc(S(1,i),k-1) U {S(1,j)} for 1 <= i <= j
					for(int i=0;i<=j;i++){
						//perform the Union of sets 'U'
						tmp = new Vector();
						tmp.addAll(disc[i][k-1]);
						substr = new ArrayList<Double>();
						for(int m=i+1;m<=j;m++)
							substr.add(realValues[attribute][values[m]]);
						tmp.add(substr);
						//compute the MODL criterion for this partition scheme
						actualMODL = modl(tmp,values);
						//if this discretization scheme minimizes the MODL value, is the optimum
						//for k intervals
						if(actualMODL < minMODL){
							disc[j][k] = tmp;
							minMODL = actualMODL;
						}
					}
				}
			}
			//Obtain the overall discretization scheme winner
			//which has the lower MODL value of all number of partitions
			if(minMODL < bestMODL){
				optimalIntervalNumber = k;
				bestMODL = minMODL;
			}
		}
		
		//once we have the optimal number of partitions -optimalIntervalNumber-
		//and the best discretization scheme -disc[number of instances -1][optimalIntervalNumber]-
		//compute the cutpoints
		tmp = disc[end][optimalIntervalNumber];
		return createCP(tmp);
	}
	

	/**
	 * Computes the MODL value for a current discretization scheme
	 * @param disc The discretization scheme to be evaluated. Comprises the intervals as ArrayList<Double> of values.
	 * @param values Array in which position i there is the number of instance which explanatory (real) value has rank i after sorting 
	 * @return The MODL value corresponding to the discretization scheme
	 */
	public double modl(Vector disc,int values[]){
		int n,I,J;
		int ni[];
		int nij[][];
		ArrayList<Double> interval;
		double modlValue;
		
		n = 0;
		I = disc.size();
		J = numClasses;
		ni = new int[I];
		nij = new int[I][J];

		for(int i=0,m=0;i<I;i++){
			interval = (ArrayList<Double>)disc.get(i); 
			ni[i] = interval.size(); 
			n += ni[i];

			for(int j=0;j<ni[i];j++,m++){
				nij[i][classOfInstances[values[m]]]++; 
			}
		}
		
		modlValue = Math.log(n);
		modlValue += binomialLog(n+I-1,I-1);
		for(int i=0;i<I;i++){
			modlValue += binomialLog(ni[i]+J-1,J-1);
		}
		for(int i=0;i<I;i++){
			modlValue += factDivision(i,ni,nij);
		}
		
		return modlValue;
	}
	
	/**
	 * Computes the MODL value for a current discretization scheme
	 * @param disc The discretization scheme to be evaluated. Comprises the intervals as ArrayList<Double> of values.
	 * @param values Array in which position i there is the number of instance which explanatory (real) value has rank i after sorting 
	 * @return The MODL value corresponding to the discretization scheme
	 */
	public double modl(ArrayList<ArrayList<Double>> disc,int values[]){
		int n,I,J;
		int ni[];
		int nij[][];
		ArrayList<Double> interval;
		double modlValue;
		
		n = 0;
		I = disc.size();
		J = numClasses;
		ni = new int[I];
		nij = new int[I][J];

		for(int i=0,m=0;i<I;i++){
			interval = disc.get(i); 
			ni[i] = interval.size(); 
			n += ni[i];
			for(int j=0;j<ni[i];j++,m++){
				nij[i][classOfInstances[values[m]]]++; 
			}
		}
		
		modlValue = Math.log(n);
		modlValue += binomialLog(n+I-1,I-1);
		for(int i=0;i<I;i++){
			modlValue += binomialLog(ni[i]+J-1,J-1);
		}
		for(int i=0;i<I;i++){
			modlValue += factDivision(i,ni,nij);
		}
		
		return modlValue;
	}
	
	/**
	 * Computes the cost derived form merging two adjacent intervals na and nb
	 * @param na Interval to the left to merge
	 * @param indexna Index of the first element of na in the whole list of real values
	 * @param nb Right interval to merge
	 * @param indexnb Index of the first element of nb in the whole list of real values
	 * @param I Current number of intervals (the total intervals prior to the merging)
	 * @param values Array in which position i there is the number of instance which explanatory (real) value has rank i after sorting
	 * @return The cost variation produced by the merge operation
	 */
	public double mergeCostVariation(ArrayList<Double> na,int indexna, ArrayList<Double> nb,int indexnb,int I,int values[]){
		double cost;
		int n = realValues.length;
		int J = numClasses;
		int countA[],countB[],countAB[];
		ArrayList<Double> merge = new ArrayList<Double>(na);
		merge.addAll(nb);

		countA = new int[J];
		countAB = new int[J];
		for(int k=0;k<na.size();k++){
			countA[classOfInstances[values[indexna+k]]]++;
			countAB[classOfInstances[values[indexna+k]]]++;
		}
		for(int k=0;k<nb.size();k++){
			countAB[classOfInstances[values[indexna+k]]]++;
		}
		//old version -faster-
//		cost = Math.log((double)(I-1)/(n+I-1)); //computed outside
//		cost += factorialLog(na.size()+nb.size()+J-1);
		
		cost = factorialLog(na.size()+nb.size()+J-1);
		cost += factorialLog(J-1);
		cost -= factorialLog(na.size()+J-1);
		cost -= factorialLog(nb.size()+J-1);
		
		
		for(int j=0;j<numClasses;j++){			
			cost -= binomialLog(countAB[j],countA[j]);
		}
		
		//new version -slower-
//		//partition cost variation -computed outside-
//		cost = partitionCost(I-1,n);
//		cost -= partitionCost(I,n);
//		//interval cost variation
//		cost = intervalCost(merge,indexna,values, I);
//		cost -= intervalCost(na,indexna,values, I);
//		cost -= intervalCost(nb,indexnb,values, I);
		
		return cost;
		
	}
	
	/**
	 * Computes the division of factorials of the form (ni[i]! / (nij[i][0]! * nij[i][1]! *...* nij[i][J-1]!))
	 * @param i The interval considered
	 * @param ni Number of instances which belong to interval i
	 * @param nij Number of instances of class j which belong to interval i
	 *
	 */
	public double factDivision(int i,int ni[],int nij[][]){
		double result;
		
		result = factorialLog(ni[i]);
		for(int j=0;j<nij[i].length;j++){
			result -= factorialLog(nij[i][j]);
		}
		
		return result;
	}
	
    /**
     * Returns the natural logarithm of n!.
     * @param n argument
     * @return <code>log(n!)</code>
     * @throws IllegalArgumentException if preconditions are not met.
     */
    public static double factorialLog(final int n) {
        double logSum = 0;
        for (int i = 2; i <= n; i++) {
            logSum += Math.log((double)i);
        }
        return logSum;
    }
    
    /**
     * Stirling formula for aproximating Log(n!) in O(1), if n is big enough.
     * @param n Number to factorize
     * @return Stirling's approximation to Log(n!)
     */
    public static double stirling(int n){
    	return (n * Math.log(n) -n +1);
    }
    
    /**
     * Returns the natural logarithm of m over n.
     * @param m Upper argument
     * @param n Lower argument
     * @return Log(m over n)
     */
    public static double binomialLog(int m, int n){
    	double result;
    	
    	result = factorialLog(m) - factorialLog(n) - factorialLog(m-n);
    	
    	return result;
    }
	
	  /**
	   * Function that calculates combinatory of two integers
	   * @param m first integer
	   * @param n second integer
	   * @return the combinatory of m and n
	   */
	  public static double combinatoria (int m, int n) {

	    double result = 1;
	    int i;

	    for (i=1; i<=m; i++)
	      result *= (double)(n-m+i)/(double)i;

	    return result;
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

