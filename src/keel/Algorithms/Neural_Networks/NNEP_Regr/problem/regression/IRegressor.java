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

package keel.Algorithms.Neural_Networks.NNEP_Regr.problem.regression;

/**  
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penia (University of Cordoba) 16/7/2007
 * @author Written by Aaron Ruiz Mora (University of Cordoba) 16/7/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public interface IRegressor {
	
	/**
	 * <p>
	 * Generic regressor.
	 * </p>
	 */
	
	/**
	 * <p>
	 * Estimates output value of a observation, through
	 * its inputs values 
	 * </p>
	 * @param inputs Double array with all inputs of the observation
	 * 
	 * @return double Output of the regressor for these inputs
	 */
    
    public double operate(double []inputs);
    
	/**
	 * <p>
	 * Estimates output values of a set of observations, through
	 * their inputs values
	 * </p>
	 * @param inputs Double matrix with all inputs of all observations
	 * 
	 * @return double[] Output values of the regressor for all 
	 *                  observation inputs
	 */
    
    public double[] operate(double [][]inputs);
}

