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
import java.util.*;
import java.lang.*;
import java.io.*;


public interface Reduction {
/**
 * <p>
 * This is the interface for all the reduction methods classes in the XCS.
 * By now, the Wilson Reduction and the Dixon Reduction (in two versions,
 * the strict one and the weak one) classes implement this interface. To
 * make other reduction methods, you only have to implement this class.
 * </p>
 */
  ///////////////////////////////////////
  // operations

/**
 * <p>
 * Makes a reduction of the population applying the chosen method by
 * the user (with the configuration file). You can implement new reduction
 * strategies implenting this interface.
 * </p>
 * <p>
 * 
 * @return a Population with the reducted population. So, the inital
 * population is not modified.
 * </p>
 * <p>
 * @param pop is the population that has to be reduced.
 * </p>
 * <p>
 * @param env is the environment that will be used to get the performance
 * of classifiers.
 * </p>
 */
    public Population makeReduction(Population pop, Environment env);

} // end Reduction






