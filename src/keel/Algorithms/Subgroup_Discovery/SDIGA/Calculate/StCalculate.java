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
 * @author Written by Pedro González (University of Jaen) 29/08/2004
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.SDIGA.Calculate;

import keel.Dataset.InstanceSet;


class StCalculate {

    /**
     * <p>
     * Global variables and structures used in the AGI algorithm
     * </p>
     */

    public static int n_eje;		  // Number of examples in dataset
    public static int num_vars;           // Number of variables of the dataset
    public static int n_clases;           // Number of classes of the target variable
    public static String name_class[];    // Name of the classes
    public static boolean claseSelec;     // Indicates if there is a selected class to run the algorithm or not
    public static int MaxEtiquetas;	  // Max number of labels for all of the cont variables
    public static int MaxValores;	  // Max number of values
    public static float GI[];             // Variable Information Gain
    public static int total_ej_cubiertos; // Total covered examples
    public static int NumReglasGeneradas; // Number of generated rules (number of iteration of the GA

    public static InstanceSet Data;
    public static TypeVar[] var;          // Variables characteristics (type, min-max values)
    public static TTable[] tabla;         // Data - DISCRETIZED OR TRANSLATED
    public static Param Param;       // Parameters for the AG
    public static Fuzzy[][] BaseDatos;   // Definitions for the fuzzy sets
    public static float[][] intervalos;	  // Aux array
    public static Population poblac;       // Main Population
    
    /**
     * <p>
     * Creates and initialices a new instance of StCalculate
     * </p>
     */
    private StCalculate() {
    }

}
