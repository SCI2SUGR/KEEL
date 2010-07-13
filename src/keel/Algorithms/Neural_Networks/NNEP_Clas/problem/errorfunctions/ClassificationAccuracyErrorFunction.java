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

package keel.Algorithms.Neural_Networks.NNEP_Clas.problem.errorfunctions;

import keel.Algorithms.Neural_Networks.NNEP_Common.problem.errorfunctions.IErrorFunction;

/**
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penia (University of Cordoba)16/7/2007
 * @author Modified by Aaron Ruiz Mora (University of Cordoba)16/7/2007
 * @version 0.1
 * @since JDK1.5
 */

public class ClassificationAccuracyErrorFunction implements IErrorFunction<byte[][]> {
	
	/**
	 * <p>
	 * Classification accuracy Error Function.
	 * </p>
	 */
	
	/////////////////////////////////////////////////////////////////
	// -------------------------------------------------- Constructor
	/////////////////////////////////////////////////////////////////
    
    /**
     * <p>
     * Empty constructor
     * </p>
     */
    
    public ClassificationAccuracyErrorFunction() {
        super();
    }

	
	/////////////////////////////////////////////////////////////////
	// ------------------------ Implementing IErrorFunction Interface
	/////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
     * Returns the classification error of a matrix of obtained values for
     * classification, compared with a matrix of expected values
     *
     * @param obtained Byte matrix of obtained values
     * @param expected Byte matrix of expected values
     * 
     * @return double Classification accuracy error value
     * </p>
     */
	
	public double calculateError(byte[][] obtained, byte[][] expected) {
		
		// Resulting logistic error
		double CAE = obtained[0].length;
	    boolean good[] = new boolean[obtained[0].length];
	    
	    for(int i=0; i<good.length; i++)
	    	good[i]=true;
	    
	    for(int i=0; i<obtained.length; i++){
		    for(int j=0; j<obtained[i].length; j++){
		    	if(obtained[i][j] != expected[i][j]){
		    		if(good[j]) {
		    			CAE--;
		    			good[j]=false;
		    		}
		    	}
		    }
	    }
	    
	    return 100 * CAE / obtained[0].length;
	}

}

