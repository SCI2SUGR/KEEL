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

import keel.Dataset.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;


public class Globals_ADI {
/**
 * <p>
 * Computes and maintains global information for the ADI KR
 * </p>
 */
	
  public static int ruleSize;
  public static int[] size;
  public static int[] offset;
  public static int[] types;
  public static ProbabilityManagement probReinit;

  public static void initialize() {
    ruleSize = 0;
    size = new int[Parameters.numAttributes];
    types = new int[Parameters.numAttributes];
    offset = new int[Parameters.numAttributes];

    for (int i = 0; i < Parameters.numAttributes; i++) {
      Attribute at = Attributes.getAttribute(i);
      offset[i] = ruleSize;
      if (at.getType() == Attribute.NOMINAL) {
        types[i] = Attribute.NOMINAL;
        size[i] = at.getNumNominalValues() + 2;
      }
      else {
        types[i] = Attribute.REAL;
        size[i] = Parameters.maxIntervals * 2 + 2;
      }
      ruleSize += size[i];
    }
    ruleSize++;
    ruleSize++;

    probReinit = new ProbabilityManagement(
        Parameters.probReinitializeBegin,
        Parameters.probReinitializeEnd,
        ProbabilityManagement.LINEAR);
  }

  public static void nextIteration() {
    if (!Parameters.adiKR) {
      return;
    }

    Parameters.probReinitialize = probReinit.incStep();
  }

  public static boolean hasDefaultClass() {
    return true;
  }
}

