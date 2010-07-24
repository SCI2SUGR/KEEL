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
 * @author Written by Pedro González (University of Jaen) 15/08/2004
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.SDIGA.Calculate;

public class TTable {
    /**
     * <p>
     * This class is defined to contain a bidimensional array with the instances
     * of the dataset, the class of the instance and if it is covered by any rule
     * </p>
     */

    int num;            // Number of the register of the dataset
    float[] ejemplo;    // Example values for all of the variables
                        // Enumerated values are translated into integers
    int clase;          // Class of the example for the target var
    boolean fcubierto;  // False if not covered by any rule; true otherwise - fuzzy version
    boolean ccubierto;  // False if not covered by any rule; true otherwise - crisp version
    
    /**
     * <p>
     * Creates a new instance of TTable
     * </p>
     */
    public TTable() {
    }
    
}
