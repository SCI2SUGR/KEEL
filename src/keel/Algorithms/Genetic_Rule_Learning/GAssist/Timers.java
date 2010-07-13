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
 * @author Written by Jaume Bacardit (La Salle, Ramón Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 23/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Genetic_Rule_Learning.GAssist;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;


public class Timers {
/**
 * <p>
 * Manages timers: flags and parameters that are triggered at
 * certain iterations
 * </p>
 */
	

  public static boolean runTimers(int iteration, Classifier[] population) {
    Globals_ADI.nextIteration();
    boolean res1 = Globals_MDL.newIteration(iteration, population);
    boolean res2 = timerBloat(iteration);

    if (res1 || res2) {
      return true;
    }
    return false;
  }

  public static void runOutputTimers(int iteration
                                     , Classifier[] population) {
    Globals_DefaultC.checkNichingStatus(iteration, population);
  }

  static boolean timerBloat(int _iteration) {
    Parameters.doRuleDeletion = (_iteration >= Parameters.iterationRuleDeletion);
    Parameters.doHierarchicalSelection = (_iteration >=
                                          Parameters.iterationHierarchicalSelection);

    if (_iteration == Parameters.numIterations - 1) {
      Parameters.ruleDeletionMinRules = 1;
      return true;
    }

    if (_iteration == Parameters.iterationRuleDeletion) {
      return true;
    }

    return false;
  }
}

