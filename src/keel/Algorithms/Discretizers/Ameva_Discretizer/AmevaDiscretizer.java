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

package keel.Algorithms.Discretizers.Ameva_Discretizer;

import java.util.*;
import keel.Algorithms.Discretizers.Basic.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;

/**
 * <p>
 * This is the class with the operations of the Ameva discretization. It adopts the behavior
 * of the general discretizers and specifies its differences in this class, that has to 
 * extend the abstract methods.
 * 
 * @author Written by Victoria Lopez Morales (University of Granada) 15/12/2009 
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
public class AmevaDiscretizer extends Discretizer {

    /**
     * <p>
     * Selects, for a given attribute, the real values that best discretize the attribute
     * according to the Ameva discretizer
     * </p>
     * @param attribute Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected
     * @param values    Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
     * @param begin First value that is considered to belong to the data considered, usually 0
     * @param end   Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
     * @return a vector with the real values that best discretize the attribute given according to 
     * the Ameva discretizer
     */
	protected Vector discretizeAttribute(int attribute, int []values, int begin, int end) {
	    Vector discretization = new Vector();
        Vector positionCutPoints = new Vector();
        Vector CutPointSelected;
        int posNewCutPoint;
        double GlobalAmeva, ameva;
        
        // Initially, select a cut point
        GlobalAmeva = 0.0;
        CutPointSelected = selectNewCutPoint (attribute, values, begin, end, positionCutPoints);
        if (CutPointSelected.size() == 0) return discretization;
        
        posNewCutPoint = ((Integer)CutPointSelected.elementAt(0)).intValue();
        ameva = ((Double)CutPointSelected.elementAt(1)).doubleValue();
            
        // While the discretization improves the Ameva measure (in the basic mode)
        // or while there aren't enough cut points (in the AmevaR mode)
        while ((ameva > GlobalAmeva) || (Parameters.amevaR && (positionCutPoints.size()+1 < Parameters.numClasses))) {
            // Add the new point to the discretization
            positionCutPoints.addElement(new Integer (posNewCutPoint));
            Collections.sort(positionCutPoints.subList(0,positionCutPoints.size()));
            discretization.addElement(new Double ((realValues[attribute][values[posNewCutPoint-1]]+realValues[attribute][values[posNewCutPoint]])/2.0));
            GlobalAmeva = ameva;
            
            // Search for another cut point
            CutPointSelected = selectNewCutPoint (attribute, values, begin, end, positionCutPoints);
            if (CutPointSelected.size() == 0) {
                Collections.sort(discretization.subList(0,discretization.size()));
                return discretization;
            }
            
            posNewCutPoint = ((Integer)CutPointSelected.elementAt(0)).intValue();
            ameva = ((Double)CutPointSelected.elementAt(1)).doubleValue();
        }
        
        // Sort all discretization values before giving the final result
        Collections.sort(discretization.subList(0,discretization.size()));
        return discretization;
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
     * Chooses the new best discretization value given a current discretization using the Ameva
     * criteria
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
     * @return the new best cut point for the current discretization (its position) and its Ameva
     * value (in a vector form)
     */
	private Vector selectNewCutPoint (int attribute, int []values, int begin, int end, Vector posCutPoints) {
	    // First, obtain all candidate cut points
	    Vector candidateCutPoints = getCandidateCutPoints (attribute, values, begin, end);
	    Vector result = new Vector();
	    
	    if (candidateCutPoints.size()==0) return result;
        
	    // Initially, the best cut point is the first one
	    int posMax = ((Integer)candidateCutPoints.elementAt(0)).intValue();
	    double amevaMax = computeAmeva (attribute, values, begin, end, posCutPoints, posMax);
	    
	    // Check if there is a cut point better than the current best point selected
	    for(int i=1,size=candidateCutPoints.size(); i<size; i++) {
            int pos = ((Integer)candidateCutPoints.elementAt(i)).intValue();
            double ameva = computeAmeva (attribute, values, begin, end, posCutPoints, pos);
            
            if(ameva > amevaMax) {
                amevaMax = ameva;
                posMax = pos;
            }
        }
	    
	    // Return the best cut point found
	    result.addElement(posMax);
	    result.addElement(amevaMax);
	    return result;
	}
	
	/**
     * <p>
     * Computes the Ameva measure for a given discretization including the new cut point
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
     * Ameva measure is computed
     * @return Ameva for a given discretization including a new cut point
     */
	private double computeAmeva (int attribute, int []values, int begin, int end, Vector posCutPoints, int posNewCutPoint) {
	    double chiSquare, fraction, ameva;
	    int N, l, k;
	    
	    Vector cd, dd, jcd;
	    Vector posCutPointsExtended = new Vector (posCutPoints); 
        
	    // Obtain a list with all the cut points (including the new one)
	    posCutPointsExtended.addElement(new Integer (posNewCutPoint));
        Collections.sort(posCutPointsExtended.subList(0,posCutPointsExtended.size()));
        
        // Obtain the whole contingency table
        jcd = jointClassDistribution (attribute, values, begin, end, posCutPointsExtended);
        cd = classDistribution (jcd);
        dd = discretizationDistribution (jcd);
        N = sumValues(cd);
        l = getNumClasses (values, begin, end);
        k = posCutPointsExtended.size() + 1;
        
        // Compute the chi-square
        chiSquare = 0.0;
        for (int i=0; i<l; i++) {
            for (int j=0; j<k; j++) {
                fraction = Math.pow((double)((Integer)jcd.elementAt(j*Parameters.numClasses+i)).intValue(),2);
                fraction = fraction/((double)((Integer)cd.elementAt(i)).intValue()*(double)((Integer)dd.elementAt(j)).intValue());
                chiSquare += fraction;
            }
        }
        chiSquare = N * (-1 + chiSquare);
        
        // From the chi-square value compute the ameva value
        ameva = chiSquare/(double)(k*(l-1));
        
        return ameva;
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
     * @param jointClassDistribution    A joint distribution depending on a discretization and the 
     * class data which is the base to build the class distribution
     * @return the class distribution of the data
     */
	private Vector classDistribution (Vector jointClassDistribution) {
	    Vector cd = new Vector();
	    int count;
	    
	    for (int i=0; i<Parameters.numClasses; i++) {
	        count = 0;
	        for (int j=0, size=jointClassDistribution.size()/Parameters.numClasses; j<size; j++) {
	            count += ((Integer)jointClassDistribution.elementAt(Parameters.numClasses*j+i)).intValue();
	        }
	        cd.addElement(count);
	    }
	    return cd;
	}
	
	/**
     * <p>
     * Obtains the distribution of the data given conditioned by a discretization
     * </p>
     * @param jointClassDistribution    A joint distribution depending on a discretization and the
     * class data which is the base to build the discretization distribution
     * @return the distribution of the data conditioned by a discretization
     */
    private Vector discretizationDistribution (Vector jointClassDistribution) {
        Vector cd = new Vector();
        int count;
        
        for (int i=0, size=jointClassDistribution.size()/Parameters.numClasses; i<size; i++) {
            count = 0;
            for (int j=0; j<Parameters.numClasses; j++) {
                count += ((Integer)jointClassDistribution.elementAt(Parameters.numClasses*i+j)).intValue();
            }
            cd.addElement(count);
        }
        return cd;
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