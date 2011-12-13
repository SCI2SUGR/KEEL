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

package keel.Algorithms.Discretizers.Bayesian_Discretizer;

import java.util.*;
import keel.Algorithms.Discretizers.Basic.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;


/**
 * <p>
 * This is the class with the operations of the Bayesian discretization. It adopts the behavior
 * of the general discretizers and specifies its differences in this class, that has to extend
 * the abstract methods.
 * 
 * @author Written by Victoria Lopez Morales (University of Granada) 21/12/2009 
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
public class BayesianDiscretizer extends Discretizer {
    
    /**
     * <p>
     * Selects, for a given attribute, the real values that best discretize the attribute
     * according to the bayesian discretizer
     * </p>
     * @param attribute Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected
     * @param values    Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
     * @param begin First value that is considered to belong to the data considered, usually 0
     * @param end   Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
     * @return a vector with the real values that best discretize the attribute given according to 
     * the bayesian discretizer
     */
    protected Vector discretizeAttribute (int attribute, int []values, int begin, int end) {
	    double Pcj;
	    
	    // First we obtain the class distribution, needed to estimate Pcj
	    Vector cd = classDistribution(attribute,values,begin,end);
	    if (cd.size() == 1) return new Vector();
	    int numValues = sumValues(cd);
        
	    Vector cutPoints = new Vector();
	    Vector differentValues = getAttributeDifferentValues(attribute, values, begin, end);
	    Vector frequencyValues = new Vector();
	    Vector conditionalClassDistribution;
	    Vector f_x;
	    Vector F_x = new Vector();
	    Vector lead;
	    Vector all_leads = new Vector();
	    
	    // For all the different values in the data, compute its frequency
	    for (int j=0, size = differentValues.size()-1; j<size; j++) {
	        // Count the number of presences for this value
	        int frequency;
	        frequency = ((Integer)differentValues.elementAt(j+1)).intValue() - ((Integer)differentValues.elementAt(j)).intValue();
	        frequencyValues.addElement (new Integer(frequency));
	    }
	    frequencyValues.addElement (new Integer (((end-begin+1)-((Integer)differentValues.elementAt(differentValues.size()-1)).intValue())));
	    
	    // For each class
        for (int i=0; i<Parameters.numClasses; i++) {
            // Compute the probability of each class
            Pcj = (double)(((Integer)cd.elementAt(i)).intValue()+1)/(double)(numValues+2);
            
            conditionalClassDistribution = new Vector();
            f_x = new Vector();
            
            // First, we compute the distribution for the whole class conditioned by values
            int frequency;
            for (int j=0, size = differentValues.size()-1; j<size; j++) {
                frequency = 0;
                for (int k=((Integer)differentValues.elementAt(j)).intValue(); k<((Integer)differentValues.elementAt(j+1)).intValue(); k++) {
                    if (classOfInstances[values[k]] == i) {
                        frequency++;
                    }
                }
                conditionalClassDistribution.addElement(new Double((double)(frequency)/(double)(((Integer)frequencyValues.elementAt(j)).intValue())));
                f_x.addElement(new Double((double)(frequency)/(double)(((Integer)frequencyValues.elementAt(j)).intValue())*Pcj));
            }
            
            frequency = 0;
            for (int k=((Integer)differentValues.elementAt(differentValues.size()-1)).intValue(); k<(end-begin+1); k++) {
                if (classOfInstances[values[k]] == i) { 
                    frequency++;
                }
            }
            
            // Build the fj(x) curve
            conditionalClassDistribution.addElement(new Double((double)(frequency)/(double)(((Integer)frequencyValues.elementAt(differentValues.size()-1)).intValue())));
            f_x.addElement(new Double((double)(frequency)/(double)(((Integer)frequencyValues.elementAt(differentValues.size()-1)).intValue())*Pcj));
            
            F_x.add(f_x);
        }
        
        // Looking at all fj(x) curves, decide the cut points
        // First, we compute all the leads
        for (int j=0, size = differentValues.size(); j<size; j++) {
            lead = leadCurve (F_x, j);
            all_leads.add(lead);
        }
        
        // Then, we check the lead of each class
        for (int i=0; i<Parameters.numClasses; i++) {
            boolean is_leading;
            
            // Check the initial situation for the class in terms of leading
            if (((Vector)all_leads.get(0)).contains(new Integer(i))) {
                is_leading = true;
            }
            else {
                is_leading = false;
            }
            
            // Check the following situations for the class in terms of leading
            for (int j=1, size = differentValues.size()-1; j<size; j++) {
                if (((Vector)all_leads.get(j)).contains(new Integer(i))) {
                    // If a class is leading right now and wan't leading before
                    // Add this point as a cut point
                    if (!is_leading) {
                        int posMax = ((Integer)differentValues.elementAt(j)).intValue();
                        double cutPoint=(realValues[attribute][values[posMax-1]]+realValues[attribute][values[posMax]])/2.0;
                        if (!cutPoints.contains(new Double (cutPoint))) {
                            cutPoints.addElement(new Double(cutPoint));
                        }
                        is_leading = true;
                    }
                }
                else {
                    // If a class is not leading right now and was leading before
                    // Add this point as a cut point
                    if (is_leading) {
                        int posMax = ((Integer)differentValues.elementAt(j)).intValue();
                        double cutPoint=(realValues[attribute][values[posMax-1]]+realValues[attribute][values[posMax]])/2.0;
                        if (!cutPoints.contains(new Double (cutPoint))) {
                            cutPoints.addElement(new Double(cutPoint));
                        }
                        is_leading = false;
                    }
                }
            }
        }
        
        // Sort all discretization values before giving the final result
        Collections.sort(cutPoints.subList(0, cutPoints.size()));
        return cutPoints;
	}

    /**
     * <p>
     * Obtain the classes of the leading curves at a certain position of the values
     * </p>
     * @param function_points   A vector of vector that contains the fj(x) curves for each class
     * @param x_value   Position in the function from which we are obtaining the leading curves
     * @return all the classes that have the higher value of fj(x) at the x_value position
     */
	private Vector leadCurve (Vector function_points, int x_value) {
	    double high_value, value;
	    Vector lead_classes = new Vector();
	    
	    // The first class is supposed to be the leading class, its value is the hightest and
	    // this class is added to the lead_classes list
	    lead_classes.addElement(new Integer(0));
	    high_value = ((Double)((Vector)function_points.get(0)).elementAt(x_value)).doubleValue();
	    
	    // Check the values of the other classes
	    for (int i=1; i<Parameters.numClasses; i++) {
	        value = ((Double)((Vector)function_points.get(i)).elementAt(x_value)).doubleValue();
	        
	        // If a class has a higher value than the highest obtained value
	        // Clear the lead_classes list, add this class to that list and store this highest value
	        if (value > high_value) {
	            lead_classes.clear();
	            lead_classes.addElement(new Integer(i));
	            high_value = value;
	        }
	        // If a class has a value as high as the highest obtained value
	        // Add this class to the lead_classes list
	        else if (value == high_value) {
	            lead_classes.addElement(new Integer(i));
	        }
	    }
	    
	    // Return the lead_classes list
	    return lead_classes;
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
     * Obtains a vector of all the different values for the attribute
     * </p>
     * @param attribute Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected
     * @param values   Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
     * @param begin    First value that is considered to belong to the data considered, usually 0
     * @param end  Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
     * @return a vector with all the possible different values for the attribute
     */
    private Vector getAttributeDifferentValues (int attribute, int []values, int begin, int end) {
        // Add the first value of the attribute (the attribute is ordered)
        Vector cutPoints = new Vector();
        double valueAnt=realValues[attribute][values[begin]];
        cutPoints.addElement(new Integer(begin));

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
			res.addElement(new Integer(classCount[i]));
		}

		return res;
	}
		
}