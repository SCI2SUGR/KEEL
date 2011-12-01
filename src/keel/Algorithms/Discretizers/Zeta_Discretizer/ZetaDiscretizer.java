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

package keel.Algorithms.Discretizers.Zeta_Discretizer;

import java.util.*;
import keel.Algorithms.Discretizers.Basic.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;


/**
 * <p>
 * This is the class with the operations of the Zeta based discretization. It adopts the
 * behavior of the general discretizers and specifies its differences in this class, 
 * that has to extend the abstract methods.
 * </p>
 * 
 * @author Written by Victoria Lopez Morales (University of Granada) 06/12/2009 
 * @version 1.0
 * @since JDK1.5
 */
public class ZetaDiscretizer extends Discretizer {

	
    /**
     * <p>
     * Selects, for a given attribute, the real values that best discretize the attribute
     * according to the Zeta based discretizer
     * </p>
     * @param attribute Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected
     * @param values    Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
     * @param begin First value that is considered to belong to the data considered, usually 0
     * @param end   Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
     * @return a vector with the real values that best discretize the attribute given according to 
     * the Zeta based discretizer
     */
	protected Vector discretizeAttribute(int attribute, int []values, int begin, int end) {
	    Vector classes = new Vector ();
	    Vector cutPoints;
	    
	    // First, add all the classes to the classes vector
	    for (int i=0; i<Parameters.numClasses; i++)
	        classes.addElement(i);
	    
	    // Obtain all the cut points by dichotomising using the zeta measure
	    cutPoints = dichotomiseZeta (attribute, values, begin, end, classes);
	    
	    // Return the obtained cutpoints
	    return cutPoints;
	}
	
	/**
	 * <p>
	 * Dichotomise the data using the zeta measure, obtaining the cut points in a recursively process
     * </p>
	 * @param attribute    Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected
	 * @param values   Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
	 * @param begin    First value that is considered to belong to the data considered, usually 0
	 * @param end  Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
	 * @param classes  A vector containing the current available classes for dichotomise
	 * @return a vector with all the values obtained that best divides classes
	 */
	private Vector dichotomiseZeta (int attribute, int []values, int begin, int end, Vector classes) {
	    int posMax;
	    double zetaMax;
	    boolean []maskMax = new boolean[classes.size()];
	    Vector cutPoints = new Vector();
	    
	    // If there is only one class, return no cut points
        if (classes.size() == 1) return cutPoints;
        
        // Obtain all candidate cut points
        Vector candidateCutPoints = getCandidateCutPoints(attribute,values,begin,end);
        if(candidateCutPoints.size()==0) return cutPoints;

        if (classes.size() == 2) {
            // If we only have two classes, we are in the basic case
            // Initially, the best cut point is the first one
            posMax = ((Integer)candidateCutPoints.elementAt(0)).intValue();
            zetaMax = computeBasicZeta (attribute, values, begin, posMax, end, classes); 
            
            // Check if there is a cut point better than the current best point selected
            for(int i=1,size=candidateCutPoints.size();i<size;i++) {
                int pos = ((Integer)candidateCutPoints.elementAt(i)).intValue();
                double zeta = computeBasicZeta (attribute, values, begin, pos, end, classes);
                if (zeta > zetaMax) {
                    zetaMax = zeta;
                    posMax = pos;
                }
            }
            
            // Add the best cut point found, and return that value
            double cutPoint = (realValues[attribute][values[posMax-1]]+realValues[attribute][values[posMax]])/2.0;
            cutPoints.addElement(cutPoint);
            return cutPoints;
        }
        else {
            // We have more than one class, we have to select one class to be separated from the
            // other classes that are considered to be merged
            boolean [] mask;
            mask = new boolean[classes.size()];
            
            // Initially, the best cut point is the first one
            posMax = ((Integer)candidateCutPoints.elementAt(0)).intValue();
            zetaMax = computeZeta (attribute, values, begin, posMax, end, classes, mask); 
            System.arraycopy(mask, 0, maskMax, 0, mask.length);
         
            // Check if there is a cut point better than the current best point selected
            for(int i=1,size=candidateCutPoints.size(); i<size; i++) {
                int pos = ((Integer)candidateCutPoints.elementAt(i)).intValue();
                double zeta = computeZeta (attribute, values, begin, pos, end, classes, mask);
                if (zeta > zetaMax) {
                    zetaMax = zeta;
                    posMax = pos;
                    System.arraycopy(mask, 0, maskMax, 0, mask.length);
                }
            }
            
            // The cut point found is added to the cutPoints vector
            double cutPoint = (realValues[attribute][values[posMax-1]]+realValues[attribute][values[posMax]])/2.0;
            cutPoints.addElement(cutPoint);
            
            // Compute recursively the other cut points
            boolean found = false;
            Vector new_classes = new Vector(classes);
            for (int i=0,size=classes.size(); i<size && !found; i++) {
                if (!maskMax[i]) {
                    new_classes.remove(i);
                    found = true;
                }
            }
            Vector otherCutPoints = dichotomiseZeta (attribute, values, begin, end, new_classes);
            cutPoints.addAll(otherCutPoints);
         
            // Sort all cut points values before giving the final result
            Collections.sort(cutPoints.subList(0, cutPoints.size()));
            return cutPoints;
        }
    }
	
	/**
	 * <p>
	 * Computes the basic zeta measure, this means, when we have two classes
     * </p>
     * @param attribute Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected
	 * @param values   Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
	 * @param begin    First value that is considered to belong to the data considered, usually 0
	 * @param midPoint Middle value that is considered to belong to the data considered, that
	 * separates the data in two parts
	 * @param end  Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
	 * @param classes  A vector containing the current available classes for the zeta computation
	 * @return value of the zeta measure
	 */
	private double computeBasicZeta (int attribute, int []values, int begin, int midPoint, int end, Vector classes) {
	    if (classes.size() == 2) {
	        int N;
	        int nii, nij;
	        
	        // Obtain the two class distributions for the given values
	        Vector cd_below = classDistributionRestricted (attribute, values, begin, midPoint-1, classes);
	        Vector cd_above = classDistributionRestricted (attribute, values, midPoint, end, classes);
	        
	        N = sumValues(cd_below) + sumValues(cd_above);
	        nii = ((Integer)cd_below.elementAt(0)).intValue() + ((Integer)cd_above.elementAt(1)).intValue();
	        nij = ((Integer)cd_below.elementAt(1)).intValue() + ((Integer)cd_above.elementAt(0)).intValue();
	        
	        // Compute the Zeta measure
	        return ((double)(Math.max(nii, nij)))/((double)N);
	    }
	    else {
	        System.err.println("The basic zeta computation is only proposed for k = 2");
	        System.exit(-1);
	        return -1;
	    }
	}
	
	/**
	 * <p>
	 * Computes the zeta measure, in a general case with more than two classes
     * </p>
	 * @param attribute    Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected
	 * @param values   Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
	 * @param begin    First value that is considered to belong to the data considered, usually 0
	 * @param midPoint Middle value that is considered to belong to the data considered, that
     * separates the data in two parts
	 * @param end  Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
	 * @param classes  A vector containing the current available classes for the zeta computation
	 * @param mask Auxiliar value that contains the class that must be considered separate at the
	 * end of the computation
	 * @return value of the zeta measure
	 */
	private double computeZeta (int attribute, int []values, int begin, int midPoint, int end, Vector classes, boolean []mask) {
        int N;
        int nii, nij, n_below2, n_above2;
        boolean []maskMax = new boolean[classes.size()];
        double zetaMax;
        Vector aux;
        
        // Compute zeta considering the class alone is the first class
        maskMax[0] = false;
        Arrays.fill(maskMax, 1, classes.size(), true);
        
        Vector cd_below = classDistributionRestricted (attribute, values, begin, midPoint-1, classes);
        Vector cd_above = classDistributionRestricted (attribute, values, midPoint, end, classes);
        N = sumValues(cd_below) + sumValues(cd_above);
        
        aux = new Vector (cd_below);
        aux.remove(0);
        n_below2 = sumValues(aux);
        aux = new Vector (cd_above);
        aux.remove(0);
        n_above2 = sumValues(aux);
        
        nii = ((Integer)cd_below.elementAt(0)).intValue() + n_above2;
        nij = n_below2 + ((Integer)cd_above.elementAt(0)).intValue();
        
        zetaMax = ((double)(Math.max(nii, nij)))/((double)N);
        
        // Compute zeta considering the other classes alone
        for(int i=1,size=classes.size();i<size;i++) {
            aux = new Vector (cd_below);
            aux.remove(i);
            n_below2 = sumValues(aux);
            aux = new Vector (cd_above);
            aux.remove(i);
            n_above2 = sumValues(aux);
            
            nii = ((Integer)cd_below.elementAt(i)).intValue() + n_above2;
            nij = n_below2 + ((Integer)cd_above.elementAt(i)).intValue();
            
            double zeta = ((double)(Math.max(nii, nij)))/((double)N);
            if (zeta > zetaMax) {
                zetaMax = zeta;
                Arrays.fill(maskMax, true);
                maskMax[i] = false;
            }
        }

        System.arraycopy(maskMax, 0, mask, 0, maskMax.length);
        return zetaMax;
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
	 * Obtains the class distribution of the data, restricted to the classes that are in the classes param
     * </p>
	 * @param attribute    Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected
	 * @param values   Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
	 * @param begin    First value that is considered to belong to the data considered, usually 0
	 * @param end  Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
	 * @param classes  A vector containing the current available classes for the class distribution
	 * @return the class distribution of the data of the given classes
	 */
	private Vector classDistributionRestricted (int attribute, int []values, int begin, int end, Vector classes) {
        int item_class;
	    int []classCount = new int[classes.size()];
        for(int i=0;i<classes.size();i++) classCount[i]=0;

        // Count only the frequency of the values belonging to a class in the classes vector
        for(int i=begin; i<=end; i++) {
            item_class = classes.indexOf(classOfInstances[values[i]]);
            if (item_class > -1) {
                classCount[item_class]++;
            }
        }
        
        Vector res= new Vector();
        for(int i=0;i<classes.size();i++) {
            res.addElement(new Integer(classCount[i]));
        }

        return res;
    }

}

