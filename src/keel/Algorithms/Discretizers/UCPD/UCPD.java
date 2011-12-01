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

package keel.Algorithms.Discretizers.UCPD;

import keel.Algorithms.Genetic_Rule_Learning.Globals.*;
import keel.Algorithms.Discretizers.Basic.*;
import org.core.Randomize;
import java.util.Vector;
import keel.Dataset.*;


/**
 * <p>
 * This class implements the UCPD algorithm
 * </p>
 * 
 * @author Written by Jose A. Saez ( University of Granada), 21/12/2009
 * @version 1.0
 * @since JDK1.6
 */
public class UCPD extends Discretizer {
	
	// tags
	static final int LEFT = 0;
	static final int RIGHT = 1;
	
	// instance variables
	private int numInstances;				// number of instances
	private int numAttributes;				// number of attributes
	private Instance[] instances;			// instances of the dataset
	
	private double[][] ContinuousValues;	// set of continuous attributes
	private int numC;						// number of continuous attributes
	
	private int[][] DiscreteValues;			// set of discrete attributes
	private int numD;						// number of discrete attributes
	private int[] numValuesD;				// number of different values of each attribute
	
	private double[] MEAN;					// normalized mean (in [0,1]) of each C attribute
	
	private Vector[] cutpoints;				// cutpoints of each continuous attribute
	private int[] AttPositions;				// position of each attribute of instances in
											// ContinuousValues and DiscreteValues
	
	
//******************************************************************************************************	

	/**
	 * <p>
	 * It returns a vector with the discretized values
	 * </p>
	 * @param attribute index of the attribute to discretize
	 * @param values not used
	 * @param begin not used
	 * @param end not used
	 * @return vector with the discretized values
	 */
	protected Vector discretizeAttribute(int attribute, int []values, int begin, int end){
		
		return cutpoints[AttPositions[attribute]];
	}
	
//******************************************************************************************************

	/**
	 * <p>
	 * Constructor of the class
	 * </p>
	 * @param is set of instances
	 */
	public UCPD(InstanceSet is){
		
		// initialize parameters
		Randomize.setSeed(Parameters.seed);
		
		int i, j;									// loop indexes
		instances = is.getInstances();				// set of instances
		numInstances = is.getNumInstances();		// number of instances
		numAttributes = Parameters.numAttributes;	// number of attributes
		AttPositions = new int[numAttributes];
		DiscreteValues = null;

		
		// compute the number of continuous and discrete attributes
		numD = numC = 0;
		for(j = 0 ; j < numAttributes ; ++j){
			
			Attribute at = Attributes.getAttribute(j);
			if(at.getType() == Attribute.REAL || at.getType() == Attribute.INTEGER)
				AttPositions[j] = numC++;
			
			else{
				if(at.getType() == Attribute.NOMINAL)
					AttPositions[j] = numD++;
			}
		}

		// initialize matrix of continuous attrbutes
		ContinuousValues = new double[numInstances][numC];
		
		
		// if there are discrete attributes, also initialize matrix of discrete attributes
		if(numD > 0 && Parameters.useDiscrete){
			
			DiscreteValues = new int[numInstances][numD];
			
			// fill the set of discrete values
			int cont;
			for(i = 0 ; i < numInstances ; ++i){
				
				cont = 0;
				for(j = 0 ; j < numAttributes ; ++j){
					
					Attribute at = Attributes.getAttribute(j);
					if(at.getType() == Attribute.NOMINAL)
						DiscreteValues[i][cont++] = instances[i].getInputNominalValuesInt(j);
				}
			}
			
			// create the array with the number of different values of each attribute
			numValuesD = new int[numD];
			cont = 0;
			for(i = 0 ; i < numAttributes ; ++i){
				
				Attribute at = Attributes.getAttribute(i);
				if(at.getType() == Attribute.NOMINAL)
					numValuesD[cont++] = at.getNumNominalValues();
			}
		}

	}
	
//******************************************************************************************************	

	/**
	 * <p>
	 * It computes the cutpoints for each continuous variable
	 * </p>
	 */	
	public void discretizeAllAttributes(){
		
		int i, j, att;		// loop indexes
		
		
		// 1) normalize each continuous attribute in [0,1] and center on mean
		normalizeAndCenter();
		
		
		// 2) do PCA on all attributes in ContinuousValues
		PCA pca = new PCA(ContinuousValues);
		pca.ComputeParameters();
		double[][] eigenvector = pca.getEigenvectors(0.9);			// get most significative eigenvectors
		double[] eigenvalues = pca.getEigenvalues();					// get their eigenvalues
		int numDimensions = pca.getNumDimensions();					// get number of dimensions
		double[][] FinalData = pca.DerivingNewData(eigenvector);	// get the final data
		double[][] COVAR = pca.getCovarianceMatrix();				// get the convariance matrix

		
		// 3) compute the number of cutpoints in each eigendimension
		double total = 0;
		for(i = 0 ; i < numDimensions ; ++i)
			total += eigenvalues[i];
		
		int[] numCutpoints = new int[numDimensions];
		for(i = 0 ; i < numDimensions ; ++i)
			numCutpoints[i] = (int) ( (eigenvalues[i]/total)*(Parameters.maxIntervals-Parameters.minIntervals)+Parameters.minIntervals-1 );
		
		
		// 4) compute the cutpoints on each eigendimension
		double[][] cutpointsEdim = new double [numDimensions][];
		
		// dataset with categorical attributes
		if(numD > 0 && Parameters.useDiscrete){
			
			// compute association patterns on all categorical attributes
			Vector<Itemset> freqits = FrequentItemsets.getFrequentItemsets(DiscreteValues, numValuesD);
			
			// determine the cutpoints basing on uniform frequency
			for(i = 0 ; i < numDimensions ; ++i)
				cutpointsEdim[i] = uniformFrequencyCutpoints(numCutpoints[i], FinalData, i);
			
			
			int[][] selectedCP = new int[numDimensions][];			
			int[] inst1 = null; int[] inst2 = null;
			int numi1 = 0, numi2 = 0;
			boolean finish, conserve;
			Object[] resObj;
			
			
			// calculate frequent itemsets of each interval
			for(int dim = 0 ; dim < numDimensions ; ++dim){
								
				// if the number of cutpoints exceeds the minimum, it can remove some of them
				if(numCutpoints[dim] > (Parameters.minIntervals-1)){
										
					finish = conserve = false;
					selectedCP[dim] = new int[numCutpoints[dim]];
					Vector<Itemset> fiInter1 = new Vector<Itemset>();
					Vector<Itemset> fiInter2 = new Vector<Itemset>();

					
					for(int sp = 0 ; sp < numCutpoints[dim] ; ++sp)
						selectedCP[dim][sp] = 1;
					

					int numsp = numCutpoints[dim];
					finish = false;
					
					
					for(int sp = 0 ; sp < numCutpoints[dim] && !finish ; ++sp){
												
						if(!conserve){
							resObj = getInstancesInto(FinalData, dim, selectedCP[dim], cutpointsEdim[dim], numCutpoints[dim], sp, LEFT);
							inst1 = (int [])resObj[0];
							numi1 = (Integer)resObj[1];
							fiInter1 = frequentItemsetForInterval(freqits, inst1, numi1);
						}
						
						else{
							inst1 = new int[numi2];
							System.arraycopy(inst2, 0, inst1, 0, numi2);
							fiInter1 = fiInter2;
						}

						resObj = getInstancesInto(FinalData, dim, selectedCP[dim], cutpointsEdim[dim], numCutpoints[dim], sp, RIGHT);
						inst2 = (int [])resObj[0];
						numi2 = (Integer)resObj[1];
						fiInter2 = frequentItemsetForInterval(freqits, inst2, inst2.length);

						if(areSimilar(fiInter1,fiInter2)){
							conserve = false;
							
							if(numsp > (Parameters.minIntervals-1)){
								numsp--;
								selectedCP[dim][sp] = 0;
							}
							
							else{
								finish = true;
							}
						}
						
						else
							conserve = true;
					}
										
					double[] cutpAUX = new double[numsp];
					for(int sp = 0 ; sp < numsp ; ++sp){
						if(selectedCP[dim][sp] == 1){
							cutpAUX[sp] = cutpointsEdim[dim][sp];
						}
					}
					
					cutpointsEdim[dim] = new double[numsp];
					System.arraycopy(cutpAUX, 0, cutpointsEdim[dim], 0, numsp);
					numCutpoints[dim] = numsp;
				}

				
			}
			
		}
		
		// there are continuous attributes only
		else{
			for(i = 0 ; i < numDimensions ; ++i)
				cutpointsEdim[i] = KMeans(numCutpoints[i]+1, FinalData, i);
		}
		
		
		// 5) determine which eigenvector has more influence over each original dimension
		int pos;
		double maximum, aux;
		int[] whichEigenvector = new int[numC];
		
		for(att = 0 ; att < numC ; ++att){
			
			pos = 0;
			maximum = ( eigenvector[att][0]*Math.sqrt(eigenvalues[0]) ) / Math.sqrt(COVAR[att][att]);
			
			for(i = 1 ; i < numDimensions ; ++i){
				aux = ( eigenvector[att][i]*Math.sqrt(eigenvalues[i]) ) / Math.sqrt(COVAR[att][att]);

				if(aux > maximum){
					pos = i;
					maximum = aux;
				}
			}
			
			whichEigenvector[att] = pos;
		}
		

		// 6) get the final cutpoints
		cutpoints = new Vector[numC];
		
		if(Parameters.mapType.equals("knn")){
			
			att = 0;
			for(i = 0 ; i < numAttributes ; ++i){
				
				Attribute at = Attributes.getAttribute(i);
				if(at.getType() == Attribute.REAL || at.getType() == Attribute.INTEGER){
					
					cutpoints[att] = new Vector();		
					for(j = 0 ; j < numCutpoints[whichEigenvector[att]] ; ++j){
						double ptoCorte = KNN(i, cutpointsEdim[whichEigenvector[att]][j], FinalData, whichEigenvector[att]);
						cutpoints[att].add(ptoCorte);
					}
					
					att++;
				}
				
			}
			
			
			// sort cutpoints and remove double cutpoints
			for(i = 0 ; i < numC ; ++i){
								
				int numa = cutpoints[i].size();
				double[] puntosDecorte = new double[numa];
				
				for(j = 0 ; j < numa ; ++j)
					puntosDecorte[j] = (Double)cutpoints[i].get(j);
					
				int[] positions2 = Quicksort.sort(puntosDecorte, numa, Quicksort.LOWEST_FIRST);
				
				
				cutpoints[i] = new Vector();
				double valuecp = puntosDecorte[positions2[0]];
				cutpoints[i].add(valuecp);
				
				for(j = 1 ; j < numa ; ++j)
					if(valuecp != puntosDecorte[positions2[j]]){
						valuecp = puntosDecorte[positions2[j]];
						cutpoints[i].add(valuecp);
					}
				
			}
		}
		
		else if(Parameters.mapType.equals("projection")){
			
			for(att = 0 ; att < numC ; ++att){
				
				double producto = eigenvector[att][whichEigenvector[att]];
				cutpoints[att] = new Vector();
				
				Attribute at = Attributes.getAttribute(att);
				double min = at.getMinAttribute();
				double max = at.getMaxAttribute();
				
				for(j = 0 ; j < numCutpoints[whichEigenvector[att]] ; ++j){
					double ptoCorte = cutpointsEdim[whichEigenvector[att]][j]*producto;
					ptoCorte += MEAN[att];
					ptoCorte *= (max-min);
					ptoCorte += min;
					cutpoints[att].add(ptoCorte);
				}
			}
		}

	}
		
//******************************************************************************************************	

	/**
	 * <p>
	 * It normalizes continuous attributes and center them on their mean
	 * </p>
	 */		
	public void normalizeAndCenter(){
		
		int i, j, cont;
		double min, max;
		
		// normalize real attributes to [0,1]
		for(i = 0 ; i < numInstances ; ++i){
			
			cont = 0;
			for(j = 0 ; j < numAttributes ; ++j){
				
				Attribute at = Attributes.getAttribute(j);
				
				if(at.getType() == Attribute.REAL || at.getType() == Attribute.INTEGER){
					min = at.getMinAttribute();
					max = at.getMaxAttribute();
					ContinuousValues[i][cont++] = (instances[i].getInputRealValues(j)-min)/(max-min);
				}
			}
		}		
		
		// compute de means of each attribute
		MEAN = new double[numC];
		for(i = 0 ; i < numC ; ++i)
			MEAN[i] = 0;
		
		for(i = 0 ; i < numInstances ; ++i)
			for(j = 0 ; j < numC ; ++j)
				MEAN[j] += ContinuousValues[i][j];
		
		for(j = 0 ; j < numC ; ++j)
			MEAN[j] /= numInstances;
		
		// mean centralization of the data
		for(i = 0 ; i < numInstances ; ++i)
			for(j = 0 ; j < numC ; ++j)
				ContinuousValues[i][j] -= MEAN[j];
	}
	
//******************************************************************************************************	

	/**
	 * <p>
	 * It calculates the cutpoints using the K-Means algorithm
	 * </p>
	 * @param k number of intervals
	 * @param FinalData the mapped data with eigendimension
	 * @param dim the eigendimension to discretize
	 * @return the cutpoints
	 */	
	public double[] KMeans(int k, double[][] FinalData, int dim){
		
		int i, j;
		double[] cutpoints = new double[k-1];		// cutpoints selected
		double[] centroids =  new double[k];		// actual centroids
		double[] groupDistance = new double[k];		// distance to each group
		
		int[] group = new int[numInstances];		// group of each instance
		for(i = 0 ; i < numInstances ; ++i)
			group[i] = -1;

		
		// take k different random centroids
		Sampling sampl = new Sampling(numInstances);
		for(i = 0 ; i < k ; ++i){			
			int rand = sampl.getSample();
			centroids[i] = FinalData[rand][dim];
		}
		
		
		boolean changes;
		
		do{
			
			changes = false;
			
			// for each object
			for(i = 0 ; i < numInstances ; ++i){
				
				// compute its distance to each centroid
				for(j = 0 ; j < k ; ++j)
					groupDistance[j] = Math.abs(centroids[j]-FinalData[i][dim]);
				
				// asign it the cluster closer
				double minor = groupDistance[0];
				int pos = 0;
				
				for(j = 1 ; j < k ; ++j){
					if(groupDistance[j] < minor){
						pos = j;
						minor = groupDistance[j];
					}
				}
				
				if(group[i] != pos){
					changes = true;
					group[i] = pos;
				}
			}
			
			// computes the new mean of each cluster
			double[] sum = new double[k];
			int[] ni = new int[k];
			for(i = 0 ; i < k ; ++i)
				sum[i] = ni[i] = 0;

			for(i = 0 ; i < numInstances ; ++i){
				sum[group[i]] += FinalData[i][dim];
				ni[group[i]]++;
			}
			
			for(i = 0 ; i < k ; ++i)
				centroids[i] = sum[i]/ni[i];			
			
		}while(changes);
		
		
		
		// compute the final cutpoints
		double[] cutp = new double[k-1];
		for(i = 0 ; i < k-1 ; ++i)
			cutp[i] = (centroids[i]+centroids[i+1])/2;
		
		// sorts the cutpoints
		int[] positions = Quicksort.sort(cutp, k-1, Quicksort.LOWEST_FIRST);
		
		for(i = 0 ; i < k-1 ; ++i)
			cutpoints[i] = cutp[positions[i]];
		
		return cutpoints;
	}
		
//******************************************************************************************************	

	/**
	 * <p>
	 * It computes the cutpoint using KNN algorithm
	 * </p>
	 * @param att original index of the attribute to compute the cutpoint
	 * @param value	value to find its nearest neighbors
	 * @param FinalData data matrix of PCA
	 * @param dim dimension to find the nearest neighbors
	 * @return the value of the cutpoint
	 */	
	public double KNN(int att, double value, double[][] FinalData, int dim){
		
		// compute the distance of each instance to value
		double[] distance = new double[numInstances];
		
		for(int i = 0 ; i < numInstances ; ++i)
			distance[i] = Math.abs(FinalData[i][dim]-value);
		
		int[] pos = Quicksort.sort(distance, numInstances, Quicksort.LOWEST_FIRST);
		
		double total = 0;

		for(int i = 0 ; i < Parameters.Neighborhood ; ++i)
			total += instances[pos[i]].getInputRealValues(att);

		total /= Parameters.Neighborhood;
		
		return total;
	}
	
//******************************************************************************************************	

	/**
	 * <p>
	 * It calculates the cutpoints with uniform frequency
	 * </p>
	 * @param k number of cutpoints to compute
	 * @param FinalData matrix of data of PCA
	 * @param att index of the dimension
	 * @return the cutpoints
	 */	
	public double[] uniformFrequencyCutpoints(int k, double[][] FinalData, int att){
		
		double[] cutpoints = new double[k];
		
		int instInter = (int)((double)numInstances/(double)(k+1));
		
		double[] DataArray = new double[numInstances];
		for(int i = 0 ; i < numInstances ; ++i)
			DataArray[i] = FinalData[i][att];
		
		int[] pos = Quicksort.sort(DataArray, numInstances, Quicksort.LOWEST_FIRST);
		
		int numcp = 0, cont = 0;
		for(int i = 0 ; i < numInstances  && numcp<k; ++i){
			cont++;
			if(cont == instInter){
				cont = 0;
				cutpoints[numcp++] = FinalData[pos[i]][att];
			}
		}
		
		return cutpoints;
	}
	
//******************************************************************************************************	

	/**
	 * <p>
	 * It computes the frequent itemsets of the given instances
	 * </p>
	 * @param its
	 * @param instances
	 * @param numi
	 * @return the the indexes of the instances into the interval inter
	 */	
	public Vector<Itemset> frequentItemsetForInterval(Vector<Itemset> its, int[] instances, int numi){
		
		Vector<Itemset> res = new Vector<Itemset>();
		int[] votes = new int[its.size()];
		for(int i = 0 ; i < its.size() ; ++i)
			votes[i] = 0;
		
		// compute the frequency of each itemset basing on given instances
		for(int i = 0 ; i < its.size() ; ++i)
			for(int j = 0 ; j < numi ; ++j)
				if(its.get(i).into(DiscreteValues[instances[j]]))
					votes[i]++;
			
		
		// see if they exceeds the minimal support
		for(int i = 0 ; i < its.size() ; ++i)
			if(votes[i] > Parameters.minSupport){
				Itemset auxi = its.get(i);
				auxi.setSupport((double)((double)votes[i]/(double)numi));
				res.add(auxi);
			}

		return res;
	}
	
//******************************************************************************************************	

	/**
	 * <p>
	 * It checks if two frequents itemsets are similar
	 * </p>
	 * @param A first set of frequents itemsets
	 * @param B second set of frequents itemsets
	 * @return if A and B are or not similar
	 */	
	public boolean areSimilar(Vector<Itemset> A, Vector<Itemset> B){
		
		double total = 0;
		int numEqualElements = 0;
		
		for(int i = 0 ; i < A.size() ; ++i){
			for(int j = 0 ; j < B.size() ; ++j){
				if(A.get(i).equalsTo(B.get(j))){
					total += Math.max(0,1-(Parameters.scalingFactor*Math.abs(A.get(i).getSupport()-B.get(j).getSupport())) );
					numEqualElements++;
				}
			}
		}
		
		// number of elements of AUB = A + B - A&B
		int numTotal = A.size()+B.size()-numEqualElements;
		double valorSim = total/(double)numTotal;

		
		if(valorSim > Parameters.mergedThreshold)
			return true;
		else
			return false;
	}
	
//******************************************************************************************************	

	/**
	 * <p>
	 * It computes the indexes of instances that fall into the interval selected
	 * </p>
	 * @param FinalData matrix of data of PCA
	 * @param dim index of the dimension
	 * @param selected indexes of the selected cutpoints
	 * @param cutp array of cutpoints
	 * @param ncp number of cutpoints
	 * @param sp index of the cutpoint to form the interval
	 * @param opt equals to LEFT to indicate the left interval of sp and equals to RIGHT to indicate the right
	 * interval
	 * @return the indexes of the selected instances and the number
	 */	
	public Object[] getInstancesInto(double[][] FinalData, int dim, int[] selected, double[] cutp, int ncp, int sp, int opt){
		
		int[] inst = new int[numInstances];
		int numInst = 0;
		
		double bottomBound = 0, highBound = 0;
		boolean finish = false;
		
		// compute the bottom and the high bounds
		if(opt == LEFT){
			
			// first cutpoint
			if(sp == 0){
				highBound = cutp[sp];
				bottomBound = (-1)*Double.MAX_VALUE;
			}
			
			// others cutpoints
			else{
				highBound = cutp[sp];
				
				for(int i = sp-1 ; i >= 0 && !finish ; --i)
					if(selected[i] == 1){
						bottomBound = i;
						finish = true;
					}
				
				if(finish == false)
					bottomBound = (-1)*Double.MAX_VALUE;
			}			
		}
		
		
		if(opt == RIGHT){
			
			// last cutpoint
			if(sp == ncp-1){
				bottomBound = cutp[sp];
				highBound = Double.MAX_VALUE;
			}
			
			// others cutpoints
			else{
				
				bottomBound = cutp[sp];
				for(int i = sp+1 ; i < ncp && !finish ; ++i)
					if(selected[i] == 1){
						highBound = i;
						finish = true;
					}
				
				if(finish == false)
					highBound = Double.MAX_VALUE;
			}
			
		}
		
		// compute the indexes
		for(int p = 0 ; p < numInstances ; ++p)	
			if(FinalData[p][dim] >= bottomBound && FinalData[p][dim] < highBound)
				inst[numInst++] = p;

		Object[] solution = new Object[2];
		solution[0] = inst;
		solution[1] = numInst;
		return solution;
	}
	
}