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

/**
* <p>
* @author Written by Sarah Vluymans (University of Ghent) 27/01/2014
* @version 0.1
* @since JDK 1.5
*</p>
*/

package keel.Algorithms.ImbalancedClassification.Auxiliar.AUC;

/**
 * Class to compute the positive probabilities
 * @author Written by Sarah Vluymans (University of Ghent) 27/01/2014
 * @version 1.1 (27-01-14)
 */
public class PosProb implements Comparable<PosProb>{
        
    // Indicates whether the instance belongs to the positive class
    private boolean isPositive ;
    
    // Score (probability of belonging to the positive class)
    private double prob;
    
    /** Constructor
     *
     * @param isPositive		Boolean value that indicates if the associated probability value is related to a positive instance
     * @param prob	Probability associated to a given instance
     */
    public PosProb (boolean isPositive, double prob){
        this.isPositive = isPositive;
        this.prob = prob;
    }
    
    /**
     * Checks if a given probability related to an instance is positive or not
     *
     * @return	A boolean value stating if the related instance is positive or not
     */
    public boolean isPositiveInstance(){
        return isPositive;
    }

    /**
     * Provides the probability associated to a given instance
     *
     * @return	Probability associated to a given instance
     */    
    public double getProb (){
        return prob;
    }

    /** Compares the ordering of two different PosProb objects with respect to their associated probability values
     *
     * @return	An integer stating which PosProb object is ordered before
     */
    public int compareTo(PosProb o) {
        if (prob < o.getProb()){  // our element should be considered later
            return 1;
        } else if (prob > o.getProb()){
            return -1;
        } else {
            return 0;
        }
    }
    
    /** Aggregates the class of the associated instance together with its associated probability
     *
     * @return	A string containing the class of the associated instance together with its associated probability
     */
    public String toString(){
        String text = "( ";
        if(isPositive){
            text += "positive";
        } else {
            text += "negative";
        }
        return text + " , " + prob + " )" ;
    }
}
