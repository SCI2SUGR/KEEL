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

package keel.Algorithms.Neural_Networks.IRPropPlus_Clas;

/**
 * <p>
 * @author by Pedro Antonio Gutierrez Penia (University of Cordoba) 27/10/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public interface IOptimizableFunc
{
	/**
	 * <p>
	 * Interface to specify a model or function from a set of coefficients
	 * and the gradient of an error function using this model
	 * </p> 
	 */
	
	
	/**
	 * <p>
	 * Returns the initial value of a[], that is, the coefficients of 
	 * the model
	 * @return double array of initial coefficients values
	 * </p>
	 */

	double[] getCoefficients();

	/**
	 * <p>
	 * Establish the final value of a[], that is, the coefficients of
	 * model 
	 * @param a double array of final coefficients values
	 * </p>
	 */

	void setCoefficients(double[] a);
	
	/** 
	 * <p>
	 * Returns the gradient vector of the derivative of an error function (E)
	 * with respect to each coefficient of the model, using an input observation
	 * matrix (x[]) and an expected output matrix (y[]). Also returns the
	 * error associated.
	 * 
	 * @param x Array with all inputs of all observations
	 * @param y Array with all expected outputs of all observations
	 *  
	 * @return double Resulting gradient vector of dE/da for all coefficients
	 * </p>
	 */

	public double[] gradient(double [][] x, double [][] y);
	
	/**
	 * <p>
	 * Last error of the model
	 * 
	 * @return double Error of the function of the model with respect to data y[]
	 * </p>
	 */
	
	public double getLastError();

}

