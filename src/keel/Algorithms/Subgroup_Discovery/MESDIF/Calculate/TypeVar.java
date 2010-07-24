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

package keel.Algorithms.Subgroup_Discovery.MESDIF.Calculate;

import java.util.Vector;

public class TypeVar {
    /**
     * <p>
     * Class defined to store the attributes characteristics
     * </p>
     */

    String nombre;           // Name of the variable stored in the dataset
    char tipoDato;           // 'i': integer, 'r':real, 'e':enumerated
    boolean continua;        // true: continuous, false: discrete
    Vector valores;          // type "i" or "r": range of real values
                             // type "e": list of valid values
    int n_etiq;              // Number of labels (continuous vars) or 
    			     //    values (discrete vars)
    float min, max;          // Values for the min and max valid values. 

    /**
     * <p>
     * Creates a new instance of TypeVar
     * </p>
     */
    public TypeVar() {
    }
    
}

