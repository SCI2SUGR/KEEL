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

package keel.Algorithms.Neural_Networks.NNEP_Common.problem.errorfunctions;

/**
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penya, Aaron Ruiz Mora (University of Cordoba) 17/07/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public interface IErrorFunction<T> {
	
	/**
	 * <p>
	 * Interface of an error function
	 * </p>
	 * @param <T> Type of evaluated entities
	 */

	/**
	 * <p>
	 * Calculate error of obtained entity, comparated with
	 * expected entity
	 * </p>
	 * @param obtained T entity obtained
	 * @param expected T entity expected
	 * @return double Error of obtained entity, comparated with
	 *                expected entity
	 */    
    public double calculateError(T obtained, T expected);
}

