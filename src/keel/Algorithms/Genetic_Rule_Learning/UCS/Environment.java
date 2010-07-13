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
 * @author Written by Albert Orriols (La Salle University Ramón Lull, Barcelona)  28/03/2004
 * @author Modified by Xavi Solé (La Salle University Ramón Lull, Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Genetic_Rule_Learning.UCS;

import java.lang.*;
import java.io.*;
import java.util.*;



public interface Environment {
/**
 * <p>
 * This interface is the environment interface. It has to be implemented for
 * all the environment problems. 
 * <p>
 */

/**
 * The function returns the current state.
 * 
 * @return a double[] with the current state.
 */
    public double[] getCurrentState();

/**
 * The function returns the current class
 *
 * @return an integer with the current class
 */
	public int getCurrentClass ();

/**
 * Creates or selects a new example of the problem. 
 *
 * @return a double[] with the new example.
 */
    public double[] newState();

    
/**
 * It initializes the first example. It is used in the file 
 * environment to get the examples sequentially.
 */
    public void beginSequentialExamples();
    
/**
 * It returns the new Example of a single step file environment.
 *	
 * @return a double[] with the next example
 */ 
    public double[] getSequentialState();
    
/**
 * It returns the number of examples of the database. It is only
 * used in the file environments. 
 * 
 * @return an integer with the number of examples in the DB
 */    
    public int getNumberOfExamples();
    
	
} // END OF CLASS Environment






