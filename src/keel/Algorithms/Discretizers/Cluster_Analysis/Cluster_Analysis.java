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

package keel.Algorithms.Discretizers.Cluster_Analysis;

import java.util.*;
import keel.Algorithms.Discretizers.Basic.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;
import keel.Dataset.InstanceSet;


/**
 * 
 * <p>
 * This class implements the Cluster Analysis discretizer.
 * </p>
 * 
 * <p>
 * @author Written by Salvador García (University of Jaén) 17/03/2011
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
public class Cluster_Analysis extends Discretizer {
	
	Instance []instances;

	/**
	* Builder
	*/
	public Cluster_Analysis() {
	}

	/**
	 * It computes the cutpoints of the given dataset
	 * 
	 * @param is the examples of the dataset
	 */
	public void buildCutPoints(InstanceSet is) {
		int i, j, l, m;
		boolean bHit;
		int numReal = 0;
		double examples[][];
		double examplesCopy[][];
		double distanceMatrix[][];
		double distanceEu;
		double Lc = 0, LcK;
		boolean stop;
		int posi, posj;
		double minDist;
		int clusters[];
		int cont;
		boolean clustersHit[];
		int numClusters;
		double clusterIntervals[][][];
		int atreal;
		double max;
		boolean LcClusters[];
		boolean classesHit[];
		int clusterID[];
		int classesCont;
		
		instances = is.getInstances();

		classOfInstances= new int[instances.length];
		for(i=0;i<instances.length;i++) 
			classOfInstances[i]=instances[i].getOutputNominalValuesInt(0);
		
		cutPoints=new double[Parameters.numAttributes][];
		realAttributes = new boolean[Parameters.numAttributes];
		realValues = new double[Parameters.numAttributes][];
		
		/*Consistency level computation Lc*/
		for (i=0; i<instances.length; i++) {
			for (j=i+1; j<instances.length; j++) {
				stop = false;
				for (l=0; l<Attributes.getInputNumAttributes() && !stop; l++) {
					if (Attributes.getInputAttribute(l).getType() == Attribute.NOMINAL) {
						if (instances[i].getInputNominalValues(l) != instances[j].getInputNominalValues(l)) {
							stop = true;
						}					
					} else {
						if (instances[i].getInputRealValues(l) != instances[j].getInputRealValues(l)) {
							stop = true;
						}						
					}
				}
				if (!stop) { // the examples have the same input values
					if (instances[i].getOutputNominalValuesInt(0) != instances[j].getOutputNominalValuesInt(0)) {
						Lc++;
					}
				}
			}
		}
		Lc = 1 - Lc/(double)instances.length;

		/*Identification of the set of numeric attributes*/
		for (i=0; i<Parameters.numAttributes; i++){
			Attribute at=Attributes.getAttribute(i);
			if (at.getDirectionAttribute() == Attribute.INPUT){
				if(at.getType()==Attribute.REAL || at.getType()==Attribute.INTEGER) {
					realAttributes[i]=true;
					numReal++;
				}
			}
		}
		
		/*Standarization (Normalization) of numeric attributes*/
		examples = new double[numReal][instances.length];
		examplesCopy = new double[numReal][instances.length];
		for (i=0, j=0; i<Parameters.numAttributes; i++) {
			if (realAttributes[i]) {				
				for (l=0; l<instances.length; l++) {
					examplesCopy[j][l] = instances[l].getInputRealValues(i);
					examples[j][l] = instances[l].getInputRealValues(i);
					examples[j][l] = (examples[j][l] - Attributes.getAttribute(i).getMinAttribute()) / (Attributes.getAttribute(i).getMaxAttribute() - Attributes.getAttribute(i).getMinAttribute());					
				}
				j++;
			}
		}
		
		/*Distance matrix computation*/
		distanceMatrix = new double[instances.length][instances.length];
		for (i=0; i<instances.length; i++) {
			for (j=0; j<instances.length; j++) {
				if (i == j) {
					distanceMatrix[i][j] = Double.POSITIVE_INFINITY;
				} else {
					distanceEu = 0;
					for (l=0; l<numReal; l++) {
						distanceEu += (examples[l][i] - examples[l][j]) * (examples[l][i] - examples[l][j]);
					}
					distanceMatrix[i][j] = distanceEu;
					distanceMatrix[j][i] = distanceEu;
				}
			}
		}
		
		/*Clusters fusion until level of consistency degrades*/
		clusters = new int[distanceMatrix.length];
		for (i=0; i<clusters.length; i++) {
			clusters[i] = i;
		}
		LcClusters = new boolean[distanceMatrix.length];
		Arrays.fill(LcClusters, true);
		stop = false;
		while (!stop) {
			minDist = Double.POSITIVE_INFINITY;
			posi = -1;
			posj = -1;
			for (i=0; i<distanceMatrix.length; i++) {
				if (LcClusters[i]) {
					for (j=i+1; j<distanceMatrix[i].length; j++) {
						if (distanceMatrix[i][j] < minDist) {
							posi = i;
							posj = j;
							minDist = distanceMatrix[i][j];
						}
					}
				}
			}
			
			/*Consistency level computation LcK*/
			LcK = 0;
			if (posi >= 0 && posj >= 0) {
				cont = 0;
				for (i=0; i<instances.length; i++) {
					if (clusters[i] == clusters[posi] || clusters[i] == clusters[posj]) {
						cont++;
						for (j=i+1; j<instances.length; j++) {
							if (clusters[j] == clusters[posi] || clusters[j] == clusters[posj]) {
								if (instances[i].getOutputNominalValuesInt(0) != instances[j].getOutputNominalValuesInt(0)) {
									LcK++;
								}
							}
						}
					}
				}
				LcK = 1 - LcK/(double)cont;			
			} else {
				stop = true;
			}
			/*check the level of consistency in new partition K*/
			if (LcK < Lc && !stop) {
				for (i=0; i<LcClusters.length; i++) {
					if (clusters[i] == posi)
						LcClusters[i] = false;
				}
				stop = true;
				for (i=0; i<LcClusters.length && stop; i++) {
					if (LcClusters[i])
						stop = false;
				}
			} else if (!stop) {
				for (i=0; i<clusters.length; i++) {
					if (clusters[i]==clusters[posj])
						clusters[i] = clusters[posi];
				}
				
				//distance re-computation
				for (i=0; i<distanceMatrix.length; i++) {
					if (i != posi && i != posj) {
						for (j=0; j<clusters.length; j++) {
							if (clusters[j] == clusters[posi]) {
								distanceMatrix[i][j] = 0.5 * distanceMatrix[i][posi] + 0.5 * distanceMatrix[i][posj] - 0.25 * distanceMatrix[posi][posj];								
								distanceMatrix[j][i] = 0.5 * distanceMatrix[i][posi] + 0.5 * distanceMatrix[i][posj] - 0.25 * distanceMatrix[posi][posj];								
							}
						}
					} else {
						for (j=0; j<clusters.length; j++) {
							if (clusters[j] == clusters[posi]) {
								distanceMatrix[i][j] = Double.POSITIVE_INFINITY;								
								distanceMatrix[j][i] = Double.POSITIVE_INFINITY;								
							}
						}
					}
				}
			}			
		}
		
		//Computation and Identification of clusters and number of them
		clustersHit = new boolean[distanceMatrix.length];
		Arrays.fill(clustersHit, false);
		for (i=0; i<clusters.length; i++) {
			clustersHit[clusters[i]] = true;
		}
		numClusters = 0;
		for (i=0; i<clustersHit.length; i++) {
			if (clustersHit[i])
				numClusters++;
		}
		
		//Obtaining the min and max boundaries of the interval of each cluster
		clusterIntervals = new double[numClusters][numReal][2];
		clusterID = new int[numClusters];
		for (i=0; i<numClusters; i++){
			for (j=0; j<numReal; j++) {
				clusterIntervals[i][j][0] = Double.POSITIVE_INFINITY;
				clusterIntervals[i][j][1] = Double.NEGATIVE_INFINITY;
			}
		}
		for (i=0, j=0; i<clustersHit.length; i++) {
			if (clustersHit[i]) {
				clusterID[j] = i;
				for (l=0; l<clusters.length; l++) {
					if (clusters[l] == i) {
						for (m=0; m<numReal; m++) {
							if (examplesCopy[m][l] < clusterIntervals[j][m][0]) {
								clusterIntervals[j][m][0] = examplesCopy[m][l];
							}							
							if (examplesCopy[m][l] > clusterIntervals[j][m][1]) {
								clusterIntervals[j][m][1] = examplesCopy[m][l];
							}							
						}
					}
				}
				j++;
			}
		}
		
		//Remove the clusters whose domain is a subdomain of other clusters for each attribute
		//and construct the set of cutpoints	
		bHit = false;
		i = 0;
		atreal = 0;
		for (int a = 0; i < Parameters.numAttributes; a++){
			Attribute at=Attributes.getAttribute(a);
			if (at.getDirectionAttribute() == Attribute.INPUT){
				if(at.getType()==Attribute.REAL || at.getType()==Attribute.INTEGER) {
					
					realValues[i] = new double[instances.length];
					int []points= new int[instances.length];
					int numPoints=0;
					
					classesHit = new boolean[Parameters.numClasses];
					for (j=0; j<numClusters; j++) {
						stop = false;
						Arrays.fill(classesHit, false);
						classesCont = 0;
						for (l=0; l<numClusters && !stop; l++) {
							if (j != l) {
								if (clusterIntervals[j][atreal][0] >= clusterIntervals[l][atreal][0] && clusterIntervals[j][atreal][1] <= clusterIntervals[l][atreal][1]) {
									for (m=0; m<classOfInstances.length; m++) {
										if (clusters[m] == clusterID[j] || clusters[m] == clusterID[l]) {
											classesHit[classOfInstances[m]] = true;
										}
									}
									for (m=0; m<classesHit.length; m++) {
										if (classesHit[m])
											classesCont++;
									}
									if (classesCont <= 1) 
										stop = true;
								}
							}
						}
						if (!stop) {
							points[numPoints++]=j;
							realValues[i][j]=clusterIntervals[j][atreal][0];							
						}
					}
					m=j;
					
					//search the greatest value of right boundary among the clusters and include into realvalues
					max = Double.NEGATIVE_INFINITY;
					for (j=0; j<numClusters; j++) {
						if (clusterIntervals[j][atreal][1] > max) {
							max = clusterIntervals[j][atreal][1];
						}
					}
					if (!stop) {
						points[numPoints++]=m;
						realValues[i][m] = max;							
					}
	
					sortValues(i,points,0,numPoints-1);
	
					Vector cp=discretizeAttribute(i,points,0,numPoints-1);
					if(cp.size()>0) {
						cutPoints[i]=new double[cp.size()];
						for(j=0;j<cutPoints[i].length;j++) {
							cutPoints[i][j]=((Double)cp.elementAt(j)).doubleValue();
							LogManager.println("Cut point "+j+" of attribute "+i+" : "+cutPoints[i][j]);
						}
					} else {
						cutPoints[i]=null;
					}
					LogManager.println("Number of cut points of attribute "+i+" : "+cp.size());
					atreal++;
				} else {
					realAttributes[i]=false;
				}
				i++;	
			} else {
				iClassIndex = a;
				bHit = true;
			}
		}
		
		if (bHit == false){
			iClassIndex = Parameters.numAttributes;
		}
	}
	
	/**
	 * <p>
	 * Returns a vector with the discretized values.
	 * </p>
	 * @param attribute
	 * @param values
	 * @param begin
	 * @param end
	 * @return vector with the discretized values
	 */
	protected Vector <Double> discretizeAttribute(int attribute,int []values,int begin,int end) {

		int cd[][];
		int i, j;
		
		/*Remove repetitions of boundaries*/
		for (i=begin; i<end; i++) {
			if (realValues[attribute][values[i]] == realValues[attribute][values[i+1]]) {
				for (j=i; j<end; j++) {
					values[j] = values[j+1];
				}
				end--;
			}
		}
		
		/*Computation of class distribution*/
		cd = new int[end][Parameters.numClasses];
		for (i=0; i<cd.length; i++) {
			Arrays.fill(cd[i], 0);
		}
		for (i=0; i<instances.length; i++) {
			for (j=1; j<=end; j++) {
				if (j<end) {
					if (instances[i].getInputRealValues(attribute) < realValues[attribute][values[j]] && instances[i].getInputRealValues(attribute) >= realValues[attribute][values[j-1]]) {
						cd[j-1][classOfInstances[i]]++;
					}
				} else {
					if (instances[i].getInputRealValues(attribute) <= realValues[attribute][values[j]] && instances[i].getInputRealValues(attribute) >= realValues[attribute][values[j-1]]) {
						cd[j-1][classOfInstances[i]]++;
					}					
				}
			}
		}
		
		/*Merge interval with entropy equal to zero*/
		Vector<Double> cutPoints=new Vector<Double>();
		for (i=1; i<end; i++) {
			if (computeEntropy(cd[i-1],cd[i]) > 0)
				cutPoints.addElement(new Double(realValues[attribute][values[i]]));
		}
		
		
		
		return cutPoints;
	}

	/**
	 * <p>
	 * Calculate the log base 2 of a number
	 * </p>
	 * @param value Number to apply log base2
	 * @return log base 2
	 */
	public double log2(double value) {
		return Math.log(value)/Math.log(2);
	}


	double computeEntropy(int cd1[],int cd2[]) {
		double ent=0;
		int numValues = 0;
		
		for (int i=0; i<cd1.length; i++) {
			numValues += cd1[i] + cd2[i];
		}

		for(int i=0,size=cd1.length;i<size;i++) {
			double prob=(double)(cd1[i]+cd2[i]);
			prob/=(double)numValues;
			if (prob > 0.0)
				ent+=prob*Math.log(prob)/Math.log(2);
		}
		return -ent;
	}
		
}
