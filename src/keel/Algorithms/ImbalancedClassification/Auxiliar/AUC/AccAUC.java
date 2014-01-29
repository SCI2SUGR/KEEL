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
 * This class represents, where one element is the accuracy of a classifier and
 * the other one the AUC.
 *
 * @author Written by Sarah Vluymans (University of Ghent) 28/01/2014
 * @version 1.1 (28-01-14)
 */

public class AccAUC {

		// Accuracy of a classifier
    private double acc;
    
    // AUC of a classifier
    private double auc;
    
    /** Constructor
     *
     * @param acc		Accuracy of a classifier
     * @param auc	AUC of a classifier
     */
    public AccAUC(double acc, double auc){
        this.acc = acc;
        this.auc = auc;
    }
    
    /**
     * Provides the accuracy of a given classifier
     *
     * @return	Accuracy of a given classifier
     */ 
    public double getAcc(){
        return acc;
    }
    
    /**
     * Provides the AUC of a given classifier
     *
     * @return	AUC of a given classifier
     */ 
    public double getAUC(){
        return auc;
    }
    
}
