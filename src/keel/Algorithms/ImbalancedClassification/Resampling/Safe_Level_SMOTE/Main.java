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
 * File: Main.java
 *
 * A Main class to process the paramethers of the method and launch the algorithm
 *
 * @author Written by Salvador Garcia Lopez (University of Granada) 30/03/2006
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.ImbalancedClassification.Resampling.Safe_Level_SMOTE;


public class Main {

  /**
   * Main method
   *
   * @param args Arguments of the program
   */
  public static void main (String args[]) {

    Safe_Level_SMOTE sl_smote;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      sl_smote = new Safe_Level_SMOTE (args[0]);
      sl_smote.run();
    }
  }
}