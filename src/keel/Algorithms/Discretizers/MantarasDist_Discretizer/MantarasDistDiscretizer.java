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

package keel.Algorithms.Discretizers.MantarasDist_Discretizer;

import java.util.*;
import keel.Algorithms.Discretizers.Basic.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;


/**
 * <p>
 * This is the class with the operations of the Mantaras Distance-Based discretization. It 
 * adopts the behavior of the general discretizers and specifies its differences in this 
 * class, that has to extend the abstract methods.
 * 
 * @author Written by Victoria Lopez Morales (University of Granada) 27/11/2009 
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
public class MantarasDistDiscretizer extends Discretizer {
	
    /**
     * <p>
     * Selects, for a given attribute, the real values that best discretize the attribute
     * according to the Distance-Based discretizer by Mantaras
     * </p>
     * @param attribute Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected
     * @param values    Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
     * @param begin First value that is considered to belong to the data considered, usually 0
     * @param end   Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
     * @return a vector with the real values that best discretize the attribute given according to 
     * the Distance-Based discretizer by Mantaras
     */
	protected Vector discretizeAttribute (int attribute, int []values, int begin, int end) {
	    Vector discretization = new Vector();
	    Vector positionCutPoints = new Vector();
	    int posNewCutPoint;
	    
	    // Initially, select a cut point
	    posNewCutPoint = selectNewCutPoint (attribute, values, begin, end, positionCutPoints);
	    if (posNewCutPoint == -1) return discretization;

	    // While the discretization improves
	    while (improvesDiscretization (attribute, values, begin, end, positionCutPoints, posNewCutPoint)) {
	        // Add the new point to the discretization
	        positionCutPoints.addElement(new Integer (posNewCutPoint));
	        Collections.sort(positionCutPoints.subList(0,positionCutPoints.size()));
	        discretization.addElement(new Double ((realValues[attribute][values[posNewCutPoint-1]]+realValues[attribute][values[posNewCutPoint]])/2.0));
	        
	        // Search for another cut point
	        posNewCutPoint = selectNewCutPoint (attribute, values, begin, end, positionCutPoints);
	        if (posNewCutPoint == -1) {
	            Collections.sort(discretization.subList(0,discretization.size()));
	            return discretization;
	        }
	    }
	    
	    // Sort all discretization values before giving the final result
	    Collections.sort(discretization.subList(0,discretization.size()));
        return discretization;
	}

	/**
	 * <p>
     * Checks if adding posNewCutPoint to the current discretization improves the performance of the 
     * discretization according to the MDLP  
     * </p>
	 * @param attribute    Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected
	 * @param values   Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
	 * @param begin    First value that is considered to belong to the data considered, usually 0
	 * @param end  Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
	 * @param posCutPoints Position of all the cut points selected until this moment
	 * @param posNewCutPoint   Position of the new cut point that has to be evaluated in the discretization 
	 * @return true, if adding posNewCutPoint to the discretization leads to a lower MDLP value, false otherwise
	 */
	private boolean improvesDiscretization (int attribute, int []values, int begin, int end, Vector posCutPoints, int posNewCutPoint) {
	    double lenDisc1, lenDisc2, lenClassesDisc1, lenClassesDisc2;
	    double partitionEntropy1, partitionEntropy2;
	    int p1 = posCutPoints.size();
	    int p2 = p1 + 1;
	    int N = classOfInstances.length;
	    int k = Parameters.numClasses;

	    Vector posCutPointsExtended = new Vector (posCutPoints);
	    
	    // Calculate Len(Disc) for both Discretizations
	    partitionEntropy1 = computeClassWeightedEntropy (attribute, values, begin, end, posCutPoints);
	    lenDisc1 = ((double)p1 * Math.log(N-1)/Math.log(2)) + ((double)(p1+1)*k) + partitionEntropy1;
        
	    posCutPointsExtended.addElement(new Integer (posNewCutPoint));
        Collections.sort(posCutPointsExtended.subList(0,posCutPointsExtended.size()));
        partitionEntropy2 = computeClassWeightedEntropy (attribute, values, begin, end, posCutPointsExtended);
	    lenDisc2 = ((double)p2 * Math.log(N-1)/Math.log(2)) + ((double)(p2+1)*k) + partitionEntropy2;
	    
	    // Calculate Len(Classes|Disc) for both Discretizations
	    lenClassesDisc1 = computeClassModifiedEntropy (attribute, values, begin, end, posCutPoints);
	    lenClassesDisc2 = computeClassModifiedEntropy (attribute, values, begin, end, posCutPointsExtended);

	    // Check if the new discretization improves the current discretization
	    if ((lenDisc1 + lenClassesDisc1) < (lenDisc2 + lenClassesDisc2)) {
	        return false;
	    }
	    else if ((lenDisc1 + lenClassesDisc1) > (lenDisc2 + lenClassesDisc2)) {
	        return true;
	    }
	    else {
	        System.out.println("The length of both solutions is the same");
	        return true;
	    }
	}
	
	/**
	 * <p>
     * Computes a pseudo-entropy measure dependent of a given discretization
     * </p>
	 * @param attribute    Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected    
	 * @param values   Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
	 * @param begin    First value that is considered to belong to the data considered, usually 0
	 * @param end  Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
	 * @param posCutPoints Discretization proposed for the attribute containing the position of the 
	 * selected cut points
	 * @return the value of the pseudo-entropy measure
	 */
    private double computeClassModifiedEntropy (int attribute, int []values, int begin, int end, Vector posCutPoints) {
        Vector cd;
        int numValues;
        double ent;
        double partitionEntropy = 0.0;

        if (posCutPoints.size() == 0) {
            // We don't have two partitions, we only have one partition
            cd = classDistribution (attribute, values, begin, end);
            numValues = sumValues(cd);
            partitionEntropy = (double)numValues * computeEntropy (cd, numValues);
        }
        else {
            // Check a first partition of the data
            cd = classDistribution (attribute, values, begin, ((Integer)posCutPoints.elementAt(0)).intValue() - 1);
            numValues = sumValues(cd);
            ent = computeEntropy (cd, numValues);
            partitionEntropy += (double)numValues * ent;
        
            // Check the central partitions of the data
            for (int i=1; i<posCutPoints.size(); i++) {
                cd = classDistribution (attribute, values, ((Integer)posCutPoints.elementAt(i-1)).intValue(), ((Integer)posCutPoints.elementAt(i)).intValue() - 1);
                numValues = sumValues(cd);
                ent = computeEntropy (cd, numValues);
                partitionEntropy += ((double)numValues * ent);
            }

            // Check the last partition of the data
            cd = classDistribution (attribute, values, ((Integer)posCutPoints.elementAt(posCutPoints.size()-1)).intValue(), end);
            numValues = sumValues(cd);
            ent = computeEntropy (cd, numValues);
            partitionEntropy += (double)numValues * ent;
        }
        
        return partitionEntropy;
    }

    /**
     * <p>
     * Computes a pseudo-entropy measure dependent of a given discretization, which is an entropy measure
     * weighted by the number of classes in the interval of the discretization
     * </p>
     * @param attribute Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected
     * @param values    Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
     * @param begin First value that is considered to belong to the data considered, usually 0
     * @param end   Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
     * @param posCutPoints Discretization proposed for the attribute containing the position of the 
     * selected cut points
     * @return  the value of the pseudo-entropy measure weighted by the class in the interval
     */
    private double computeClassWeightedEntropy (int attribute, int []values, int begin, int end, Vector posCutPoints) {
	    Vector cd;
        int numValues;
        double ent;
        double partitionEntropy = 0.0;

        if (posCutPoints.size() == 0) {
            // We don't have two partitions, we only have one partition
            cd = classDistribution (attribute, values, begin, end);
            numValues = sumValues(cd);
            partitionEntropy = (double)getNumClasses(values, begin, end) * computeEntropy (cd, numValues);
        }
        else {
            // Check a first partition of the data
            cd = classDistribution (attribute, values, begin, ((Integer)posCutPoints.elementAt(0)).intValue() - 1);
            numValues = sumValues(cd);
            ent = computeEntropy (cd, numValues);
            partitionEntropy += ((double)getNumClasses(values, begin, ((Integer)posCutPoints.elementAt(0)).intValue() - 1) * ent);
         
            // Check the central partitions of the data
            for (int i=1; i<posCutPoints.size(); i++) {
                cd = classDistribution (attribute, values, ((Integer)posCutPoints.elementAt(i-1)).intValue(), ((Integer)posCutPoints.elementAt(i)).intValue() - 1);
                numValues = sumValues(cd);
                ent = computeEntropy (cd, numValues);
                partitionEntropy += ((double)getNumClasses(values, ((Integer)posCutPoints.elementAt(i-1)).intValue(), ((Integer)posCutPoints.elementAt(i)).intValue() - 1) * ent);
            }
            
            // Check the last partition of the data
            cd = classDistribution (attribute, values, ((Integer)posCutPoints.elementAt(posCutPoints.size()-1)).intValue(), end);
            numValues = sumValues(cd);
            ent = computeEntropy (cd, numValues);
            partitionEntropy += ((double)getNumClasses(values, ((Integer)posCutPoints.elementAt(posCutPoints.size()-1)).intValue(), end) * ent);
        }
        
        return partitionEntropy;
	}
	
    /**
     * <p>
     * Gets the number of classes that are present in the data values
     * </p>
     * @param values    Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
     * @param begin First value that is considered to belong to the data considered, usually 0
     * @param end   Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
     * @return the number of classes that there are in the data
     */
	private int getNumClasses (int [] values, int begin, int end) {
        ArrayList <Integer> diff_values;
        
        diff_values = new ArrayList <Integer> ();
        
        // Create a list with all the different possible values for the output class
        for (int j=begin; j<=end; j++) {
            double aux = classOfInstances[values[j]];
            // If the class considered isn't in the diff_values list yet, add to that list
            if (!diff_values.contains(new Integer((int)aux))) {
                diff_values.add(new Integer((int)aux));                   
            }
        }
        
        return diff_values.size();
	}
	
	/**
	 * <p>
	 * Chooses the new best discretization value given a current discretization using the Mantaras
	 * Distance criteria.
     * </p>
     * @param attribute Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected
	 * @param values   Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
	 * @param begin    First value that is considered to belong to the data considered, usually 0
	 * @param end  Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
	 * @param posCutPoints Discretization proposed for the attribute containing the position of the 
     * selected cut points
	 * @return the new best cut point for the current discretization (its position)
	 */
	private int selectNewCutPoint (int attribute, int []values, int begin, int end, Vector posCutPoints) {
	    // First, obtain all candidate cut points
	    Vector candidateCutPoints = getCandidateCutPoints(attribute,values,begin,end);
	    if(candidateCutPoints.size()==0) return -1;
        
	    // Initially, the best cut point is the first one
	    int posMin = ((Integer)candidateCutPoints.elementAt(0)).intValue();
	    double distMin = computeDistanceNewPartition(attribute, values, begin, end, posCutPoints, posMin);
	    
	    // Check if there is a cut point better than the current best point selected
	    for(int i=1,size=candidateCutPoints.size();i<size;i++) {
            int pos=((Integer)candidateCutPoints.elementAt(i)).intValue();
            double dist=computeDistanceNewPartition(attribute, values, begin, end, posCutPoints, pos);
            
            if(dist < distMin) {
                distMin = dist;
                posMin = pos;
            }
        }
	    
	    // Return the best cut point found
	    return posMin;
	}
	
	/**
	 * <p>
	 * Computes the distance (Mantaras criteria) for a given discretization including the new cut point
     * </p>
     * @param attribute Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected
	 * @param values   Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
	 * @param begin    First value that is considered to belong to the data considered, usually 0
	 * @param end  Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
	 * @param posCutPoints Discretization proposed for the attribute containing the position of the 
     * selected cut points
	 * @param posNewCutPoint   Position of the cut point that belongs to the current discretization whose
	 * distance is computed
	 * @return distance for a given discretization including a new cut point
	 */
	private double computeDistanceNewPartition(int attribute, int []values, int begin, int end, Vector posCutPoints, int posNewCutPoint) {
	    double entC, entD, jointEnt;
	    Vector cd;
	    int numValues;
	    
	    Vector posCutPointsExtended = new Vector (posCutPoints); 
        
	    // Compute the entropy associated to the class distribution
	    cd = classDistribution (attribute, values, begin, end);
        numValues = sumValues(cd);
        entC = computeEntropy (cd, numValues);
	    
        // Compute the entropy associated to the discretization distribution
        posCutPointsExtended.addElement(new Integer (posNewCutPoint));
        Collections.sort(posCutPointsExtended.subList(0,posCutPointsExtended.size()));
        cd = discretizationDistribution (attribute, values, begin, end, posCutPointsExtended);
        numValues = sumValues(cd);
        entD = computeEntropy (cd, numValues);
        
        // Compute the entropy associated to the joint distribution
        jointEnt = computeJointEntropy (attribute, values, begin, end, posCutPointsExtended);

        // Return the distance
        return (2 - ((entC+entD)/jointEnt));
    }
	
	/**
	 * <p>
	 * Computes the joint entropy for a given discretization
     * </p>
     * @param attribute Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected
	 * @param values   Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
	 * @param begin    First value that is considered to belong to the data considered, usually 0
	 * @param end  Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
	 * @param Discretization Discretization proposed for the attribute containing the position of the 
     * selected cut points
	 * @return the entropy value corresponding to the joint class distribution of the discretization
	 */
	private double computeJointEntropy (int attribute, int [] values, int begin, int end, Vector Discretization) {
	    Vector cd;
	    int numValues;
	    
	    cd = jointClassDistribution (attribute, values, begin, end, Discretization);
	    numValues = sumValues (cd);
	    
	    return computeEntropy (cd, numValues);
	}
	
	/**
	 * <p>
	 * Computes the Shannon entropy for a set of values
     * </p>
     * @param v Set of values whose entropy is computed
	 * @param numValues    Total number of values whose entropy is computed
	 * @return the Shannon entropy of the set of values
	 */
	private double computeEntropy(Vector v, int numValues) {
		double ent=0;

		for(int i=0,size=v.size();i<size;i++) {
			double prob=((Integer)v.elementAt(i)).intValue();
			// This is done to avoid computing invalid log values
			if (prob != 0) {
			    prob/=(double)numValues;
			    ent+=prob*Math.log(prob)/Math.log(2);
			}
		}

		return -ent;
	}

	/**
	 * <p>
	 * Adds up the integer values stored in a vector
     * </p>
     * @param v Vector whose integer values are going to be added
	 * @return sum of the addition of all integer values in the vector
	 */
	private int sumValues(Vector v) {
		int sum=0;
		for(int i=0,size=v.size();i<size;i++) {
			sum+=((Integer)v.elementAt(i)).intValue();
		}
		return sum;
	}

	/**
	 * <p>
	 * Obtains a vector of all the possible cut points for the attribute
     * </p>
     * @param attribute Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected
	 * @param values   Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
	 * @param begin    First value that is considered to belong to the data considered, usually 0
	 * @param end  Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
	 * @return a vector with all the possible cut points for the attribute
	 */
	private Vector getCandidateCutPoints(int attribute,int []values,int begin,int end) {
		Vector cutPoints = new Vector();
		double valueAnt=realValues[attribute][values[begin]];

		// Add all the values different from its previous value
		for(int i=begin;i<=end;i++) {
			double val=realValues[attribute][values[i]];
			if(val!=valueAnt) cutPoints.addElement(new Integer(i));
			valueAnt=val;
		}
		return cutPoints;
	}

	/**
	 * <p>
	 * Obtains the class distribution of the data
     * </p>
     * @param attribute Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected
	 * @param values   Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
	 * @param begin    First value that is considered to belong to the data considered, usually 0
	 * @param end  Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
	 * @return the class distribution of the data
	 */
	private Vector classDistribution(int attribute,int []values,int begin,int end) {
		int []classCount = new int[Parameters.numClasses];
		for(int i=0;i<Parameters.numClasses;i++) classCount[i]=0;

		for(int i=begin;i<=end;i++) classCount[classOfInstances[values[i]]]++;
		
		Vector res= new Vector();
		for(int i=0;i<Parameters.numClasses;i++) {
			if(classCount[i]>0) res.addElement(new Integer(classCount[i]));
		}

		return res;
	}
	
	/**
	 * <p>
	 * Obtains the distribution of the data given a current discretization
     * </p>
     * @param attribute Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected
	 * @param values   Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
	 * @param begin    First value that is considered to belong to the data considered, usually 0
	 * @param end  Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
	 * @param posCutPoints Discretization proposed for the attribute containing the position of the 
     * selected cut points
	 * @return the distribution of the data given a current discretization
	 */
	private Vector discretizationDistribution(int attribute, int []values, int begin, int end, Vector posCutPoints) {
        int []distributionCount = new int[posCutPoints.size()+1];
        
        distributionCount[0] = ((Integer)posCutPoints.elementAt(0)).intValue() - begin;
        
        for (int i=1; i<posCutPoints.size(); i++) {
            distributionCount[i] = ((Integer)posCutPoints.elementAt(i)).intValue() - ((Integer)posCutPoints.elementAt(i-1)).intValue();
        }

        distributionCount[posCutPoints.size()] = end - ((Integer)posCutPoints.elementAt(posCutPoints.size()-1)).intValue() + 1;

        Vector res= new Vector();
        for(int i=0;i<posCutPoints.size()+1;i++) {
            if(distributionCount[i]>0) res.addElement(new Integer(distributionCount[i]));
        }

        return res;
    }
	
	/**
	 * <p>
	 * Obtains a joint distribution of the data given a current discretization and the class the data
	 * belongs to
     * </p>
     * @param attribute Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected
	 * @param values   Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
	 * @param begin    First value that is considered to belong to the data considered, usually 0
	 * @param end  Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
	 * @param posCutPoints Discretization proposed for the attribute containing the position of the 
     * selected cut points
	 * @return a joint distribution depending on the discretization and the class data
	 */
    private Vector jointClassDistribution(int attribute, int []values, int begin, int end, Vector posCutPoints) {
        int []jointClassCount = new int[Parameters.numClasses*(posCutPoints.size()+1)];
        for(int i=0;i<Parameters.numClasses*(posCutPoints.size()+1);i++) jointClassCount[i]=0;

        for(int i=begin; i<((Integer)posCutPoints.elementAt(0)).intValue(); i++) {
            jointClassCount[classOfInstances[values[i]]]++;
        }
        
        for (int i=1; i<posCutPoints.size(); i++) {
            for (int j=((Integer)posCutPoints.elementAt(i-1)).intValue(); j<((Integer)posCutPoints.elementAt(i)).intValue(); j++) {
                jointClassCount[Parameters.numClasses*i+classOfInstances[values[j]]]++;
            }
        }

        for(int i=((Integer)posCutPoints.elementAt(posCutPoints.size()-1)).intValue(); i<=end; i++) {
            jointClassCount[Parameters.numClasses*posCutPoints.size()+classOfInstances[values[i]]]++;
        }

        Vector res= new Vector();
        for(int i=0;i<Parameters.numClasses*(posCutPoints.size()+1);i++) {
            res.addElement(new Integer(jointClassCount[i]));
        }

        return res;
    }
    
}