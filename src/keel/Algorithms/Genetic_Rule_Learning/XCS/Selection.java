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
 * @author Written by Albert Orriols (La Salle, Ramón Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Genetic_Rule_Learning.XCS;
import  keel.Algorithms.Genetic_Rule_Learning.XCS.KeelParser.Config;
import java.lang.*;
import java.io.*;
import java.util.*;


public interface Selection {
/**
 * <p>
 * It is the interface for Selection method. To implement a new selection model,
 * you only have to implement this interface.
 * </p>
 */
   ///////////////////////////////////////
  // associations



  ///////////////////////////////////////
  // operations

/**
 * <p>
 * Does make the random selection of a classifier.
 * </p>
 * <p>
 * @param pop is the population.
 * </p>
 * <p>
 * @return a Classifier with the selected classifier
 * </p>
 */
    public Classifier makeSelection(Population pop);

/**
 * <p>
 * Initializes the system for selection.
 * </p>
 * <p>
 * 
 * @param pop is the population
 * </p>
 */
    public void init(Population pop);

} // end Selection






