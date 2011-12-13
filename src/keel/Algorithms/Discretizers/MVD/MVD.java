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

package keel.Algorithms.Discretizers.MVD;

import keel.Algorithms.Genetic_Rule_Learning.Globals.*;
import keel.Algorithms.Discretizers.Basic.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import keel.Dataset.*;


/**
 * <p>
 * This class implements the UCPD algorithm
 * </p>
 * 
 * <p>
 * @author Written by Julian Luengo (SCI2S research group, DECSAI in ETSIIT, University of Granada), 18/04/2011
 * @version 1.0
 * @since JDK1.6
 * </p>
 */
public class MVD extends Discretizer {
	
	// tags
	static final int LEFT = 0;
	static final int RIGHT = 1;
	
	// instance variables
	private int numInstances;				// number of instances
	private int numAttributes;				// number of attributes
	private int numClasses;
	private Vector[] cutpoints;				// cutpoints of each continuous attribute
	
	ArrayList<Integer> indexContinuousAtt;
	
	protected int numBasicIntervals;
	
	private InstanceSet is;
	
	private double alpha;
	protected int min = -1;
	protected int max = -1;
	int majoritaryGroup;
	
	
//******************************************************************************************************	

	/**
	 * <p>
	 * Returns a vector with the discretized values
	 * </p>
	 * @param attribute index of the attribute to discretize
	 * @param values not used
	 * @param begin not used
	 * @param end not used
	 * @return vector with the discretized values
	 */
	protected Vector discretizeAttribute(int attribute, int []values, int begin, int end){
		
		return cutpoints[attribute];
	}
	
//******************************************************************************************************

	/**
	 * <p>
	 * Constructor of the class
	 * </p>
	 * @param _is set of instances
	 */
	public MVD(InstanceSet _is, int _numBasicIntervals,double _alpha){
		
		Attribute at;
		int numContinuous = 0;

		
		// initialize parameters
		//Randomize.setSeed(Parameters.seed);
		
		is = _is;
		numInstances = _is.getNumInstances();		// number of instances
		numAttributes = Attributes.getInputNumAttributes();	// number of attributes
		numClasses = Attributes.getOutputAttribute(0).getNumNominalValues();

		if (_numBasicIntervals > 0)
			numBasicIntervals=_numBasicIntervals;
		else 
			numBasicIntervals = (Parameters.numInstances / (100)) > Parameters.numClasses?Parameters.numInstances / (100):Parameters.numClasses;

		alpha = _alpha;
		
		//create the index array of numeric attributes
		indexContinuousAtt = new ArrayList<Integer>();
		for(int i=0;i<numAttributes;i++){
			at = Attributes.getInputAttribute(i);
			if(at.getType() != Attribute.NOMINAL){
				indexContinuousAtt.add(i);
			}
		}
		
	}
	
//******************************************************************************************************	

	/**
	 * <p>
	 * Computes the cutpoints for each continuous variable
	 * </p>
	 */	
	public void discretizeAllAttributes(){
		
		Instance inst;
		Attribute at;
		Interval x,y,z;
		int currentInterval,previouscp, index;
		double cp,delta,sup_x,sup_y;
		boolean elegibleMergingCP,distributionDifference,significantDifference;
		ArrayList<Double> data;
		ArrayList<Integer> indices;
		Vector<ArrayList<Double>> cutpointsEdim = new Vector<ArrayList<Double>>(indexContinuousAtt.size());
		Vector<ArrayList<Double>> continuousValues = new Vector<ArrayList<Double>>(indexContinuousAtt.size());
		Vector<ArrayList<Interval>> intervalsEdim = new Vector<ArrayList<Interval>>(indexContinuousAtt.size());
		ArrayList<Interval> allIntervals = new ArrayList<Interval>();
		int instancesPerClass[] = new int[numClasses];
		ArrayList<Interval> intervals;
		int contingencyTable[][];
		Chi2 chi2 = new Chi2();
		int numIntervals = 0;
		
		
		for(int j=0;j<indexContinuousAtt.size();j++){
			continuousValues.add(new ArrayList<Double>());
			cutpointsEdim.add(new ArrayList<Double>());
			intervalsEdim.add(new ArrayList<Interval>());
		}
				
		//1) Create a fine partition of all continuous attributes
		for(int i=0;i<is.getNumInstances();i++){
			
			inst = is.getInstance(i);
			for(int j=0;j<indexContinuousAtt.size();j++){
				continuousValues.get(j).add(inst.getAllInputValues()[indexContinuousAtt.get(j)]);
			}
			
			instancesPerClass[(int)inst.getAllOutputValues()[0]]++;
		}
		//sort the values in order so we can get the initial cutpoints
		for(int j=0;j<continuousValues.size();j++){
			Collections.sort(continuousValues.get(j));
		}

		//use a equal frequency discretization as an initial guess partitioning
		for(int i = 0 ; i < continuousValues.size() ; i++){
			cutpointsEdim.set(i, uniformFrequencyCutpoints(numBasicIntervals,continuousValues.get(i), i));
		}
		
		//2) iteratively select two adjacent intervals X and Y that have the minimum combined support and do not have a known discretization
		//boundary between them as candidates for merging for each continuous attribute
		
		//first build up de intervals from the cutpoints
		for(int i=0;i<cutpointsEdim.size();i++){
			data = cutpointsEdim.get(i);
			
			//Dynamically convert the cutpoints to intervals
			intervals = new ArrayList<Interval>();
			intervals.add(new Interval(Double.MIN_VALUE,data.get(0),indexContinuousAtt.get(i)));
			for(int k=1;k<data.size();k++){
				intervals.add(new Interval(data.get(k-1),data.get(k),indexContinuousAtt.get(i)));
			}
			intervals.add(new Interval(data.get(data.size()-1),Double.MAX_VALUE,indexContinuousAtt.get(i)));
			
			intervalsEdim.set(i, intervals);	
			allIntervals.addAll(intervals);
			
			numIntervals += intervals.size();
		}
		
		//evaluate the instances covered by each interval
		for(int i=0;i<is.getNumInstances();i++){
			inst = is.getInstance(i);
			
			for(int j=0;j<intervalsEdim.size();j++){
				intervals = intervalsEdim.get(j);
				for(int k=0;k<intervals.size();k++){
					x = intervals.get(k);
					
					if(x.covers(inst))
						x.addToCoveredInstances(i);
				}
			}
		}
		
		//now initiate the merging process
		for(int i=0;i<intervalsEdim.size();i++){
			currentInterval = 0;
			previouscp = -1;
			elegibleMergingCP = true;
			
			intervals = intervalsEdim.get(i);
			
			while(elegibleMergingCP && currentInterval < intervals.size()-1 && intervals.size() >= 2){
				x = intervals.get(currentInterval);
				y = intervals.get(currentInterval+1);
				
				contingencyTable = new int[2][allIntervals.size()-2];
				for(int j=0,t=0;j<allIntervals.size();j++){
					z = allIntervals.get(j);
					if(!z.equals(x) && !z.equals(y)){
						
						indices = z.getCoveredInstances();
						for(int k=0;k<indices.size();k++){
							index = indices.get(k);
							if(x.covers(index) || y.covers(index)){
								contingencyTable[0][t]++;
							}else{
								contingencyTable[1][t]++;
							}
						}
						t++;
					}
				}
				
				sup_x = x.support()/(double)numInstances;
				sup_y = y.support()/(double)numInstances;
				//check the difference between distributions
				delta = 0.01*numInstances/Math.min(sup_x, sup_y);
				distributionDifference = maximumSupportDifference(contingencyTable) >= delta;
				
				//if the distributions are not different by the previous criteria, we check if such difference is statistically significant
				if(distributionDifference){
					/*indices = x.getCoveredInstances();
					
					for(int j=0;j<indices.size();j++){
						inst = is.getInstance(indices.get(j));
						contingencyTable[0][(int)inst.getAllOutputValues()[0]]++;
					}
					
					indices = y.getCoveredInstances();
					
					for(int j=0;j<indices.size();j++){
						inst = is.getInstance(indices.get(j));
						contingencyTable[0][(int)inst.getAllOutputValues()[0]]++;
					}
					for(int j=0;j<contingencyTable[1].length;j++){
						contingencyTable[1][j] = instancesPerClass[j] - contingencyTable[0][j];
					}*/
					/*contingencyTable[0][0] = x.support();
					contingencyTable[0][1] = y.support();
					contingencyTable[1][0] = numInstances - x.support();
					contingencyTable[1][1] = numInstances - y.support();*/

					double p = chiSquare(contingencyTable);
					
					significantDifference = chi2.critchi(p, contingencyTable[0].length-1)<((alpha/4.0)/(double)numIntervals);
				}else
					significantDifference = false;
				
				//if both conditions are not met, then we can merge the intervals
				if(!distributionDifference || !significantDifference){
					x.mergeIntervals(y);
					intervals.remove(y);
					
					numIntervals--;
					if(currentInterval > 0)
						currentInterval--;
				}
				else{
					previouscp = currentInterval;
					currentInterval++;
				}
			}
		}
		
		cutpoints = new Vector[numAttributes];
		for(int i=0,j=0;i<numAttributes;i++){
			at = Attributes.getInputAttribute(i);
			if(at.getType() != Attribute.NOMINAL){
				cutpoints[i] = new Vector();
				
				intervals = intervalsEdim.get(j);
				
				x = intervals.get(0);
				for(int k=1;k<intervals.size();k++){
					y = intervals.get(k);
					
					cutpoints[i].add((x.upperbound+y.lowerbound)/2.0);
					
					x = y;
					
				}
				j++;
			}
		}
		
		
	

	}
		
//******************************************************************************************************	

	/**
	 * <p>
	 * It calcules the cutpoints with uniform frequency
	 * </p>
	 * @param k number of cutpoints to compute
	 * @param FinalData matrix of data of PCA
	 * @param att index of the dimension
	 * @return the cutpoints
	 */	
	private ArrayList<Double> uniformFrequencyCutpoints(int k, ArrayList<Double> data, int att){
		
		ArrayList<Double> cutpoints = new ArrayList<Double>(k);
			
		int instInter = (int)((double)data.size()/(double)(k+1));
		
		int numcp = 0, cont = 0;
		for(int i = 0 ; i < numInstances  && numcp<k; ++i){
			cont++;
			if(cont >= instInter){
				if(cutpoints.size()==0){
					cont = 0;
					cutpoints.add(numcp++, data.get(i));
				}else if(data.get(i).doubleValue()!=cutpoints.get(cutpoints.size()-1).doubleValue()){
					cont = 0;
					cutpoints.add(numcp++, data.get(i));
				}
			}
		}
		
		return cutpoints;
			
	}
	
	/**
	 * Computes the expected value from the contingency table of this node
	 * @param i the index of the row
	 * @param j the index of the column
	 * @return
	 */
	protected double expectedValue(int contingencyTable[][],int i, int j){
		double expected,aux;
		
		aux = 0;
		for(int ii=0;ii<contingencyTable[i].length;ii++){
			aux += contingencyTable[i][ii];
		}
		expected = aux;
		aux = 0;
		for(int ii=0;ii<contingencyTable.length;ii++){
			aux += contingencyTable[ii][j];
		}
		expected *= aux;
		expected /= numInstances;
		
		return expected;
		
	}
	
	/**
	 * Obtains the Chi square value of this node using the contigency table
	 * @return the chi square value
	 */
	public double chiSquare(int contingencyTable[][]){
		double chi = 0;
		double expected;
		
		for(int i=0;i<contingencyTable.length;i++){
			for(int j=0;j<contingencyTable[i].length;j++){
				expected = expectedValue(contingencyTable, i, j);
				chi += Math.pow(contingencyTable[i][j]-expected, 2)/expected;  
			}
		}
		
		return chi;
	}
	
	/**
	 * Gets the maximum support difference
	 * @return the difference between the maximum and minimum support of the contigency table
	 */
	public int maximumSupportDifference(int contingencyTable[][]){
		
		computeMaximumAndMinimumSupport(contingencyTable);
		
		return (max-min);
	}
	
	/**
	 * Finds the maximum and minimum supports of all groups
	 */
	protected void computeMaximumAndMinimumSupport(int contingencyTable[][]){
		max = Integer.MIN_VALUE;
		min = Integer.MAX_VALUE;
		for(int i=0;i<contingencyTable[0].length;i++){
			if(max < contingencyTable[0][i]){
				max = contingencyTable[0][i];
				majoritaryGroup = i; 
			}
			if(min > contingencyTable[0][i])
				min = contingencyTable[0][i];
		}
	}
	
}