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
 * File: Distribution2KeyTable.java.
 *
 * A class for managing 2-key tables for statistical distributions
 *
 * @author Written by Joaquin Derrac (University of Granada) 1/12/2010
 * @version 1.0
 * @since JDK1.5
 */

package keel.GraphInterKeel.statistical.tests;

import java.util.Arrays;

public class Distribution2KeyTable{
	
	private double body [][];

    /**
     * Builder
     *
     * @param lenght1 Lenght of the first key
     * @param lenght2 Lenght of the second key
     */
	public Distribution2KeyTable(int lenght1,int lenght2){
		
		body=new double[lenght1][lenght2];

		clear();

	}//end-method

    /**
     * Clear table
     */
	public void clear(){
		for(int i=0;i<body.length;i++){
			Arrays.fill(body[i],-1.0);
		}
	}//end-method

    /**
     * Erase a row of the table
     *
     * @param dim Row to erase
     */
	public void erase(int dim){
		
		Arrays.fill(body[dim],-1.0);
	}//end-method
	
    /**
     * Add a row to the table
     *
     * @param dim Index of the row
     * @param values Contents of the row
     */
	public void addRow(int dim, double [] values){
		
		System.arraycopy(values, 0, body[dim], 0, values.length);

	}//end-method

    /**
     * Modifies a value in the table
     *
     * @param dim1 First dimension
     * @param dim2 Second dimension
     * @param values Value
     */
	public void addValue(int dim1, int dim2, double values){
		
		body[dim1][dim2]=values;

	}//end-method

    /**
     * Get a value
     * @param dim1 First dimension
     * @param dim2 Second dimension
     *
     * @return value
     */
	public double get(int dim1, int dim2){
		
		double value;
		
		value=body[dim1][dim2];
		
		return value;
		
	}//end-method

	
}//end-class
