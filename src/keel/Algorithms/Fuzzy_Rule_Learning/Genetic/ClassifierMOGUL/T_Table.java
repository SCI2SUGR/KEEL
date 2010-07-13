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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierMOGUL;

/**
 * <p>
 * @author Written by Jesus Alcala Fernandez (University of Granada) 01/01/2004
 * @author Modified by Francisco José Berlanga (University of Jaén) 09/12/2008 
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */

class T_Table {
/**	
 * <p>
 * Each instance has this form
 * </p>
 */

        public double [] ejemplo; /* data */
        public int n_variables;   /* number of variables */
        public double nivel_cubrimiento, maximo_cubrimiento; /* matching degree */
        public int cubierto;      /* it's 1 if the instance is covered */

	/**	
 	 * <p>
	 * Constructor
	 * </p>
	 * @param var int The number of variables (input + output) of the data set
	 */
        public T_Table (int var) {
                n_variables = var;
                ejemplo = new double[n_variables];

                nivel_cubrimiento = (double) 0.0;
                maximo_cubrimiento = (double) 0.0;
                cubierto = 0;
        }
}

