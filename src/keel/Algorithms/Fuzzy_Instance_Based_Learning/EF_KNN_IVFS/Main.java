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


package keel.Algorithms.Fuzzy_Instance_Based_Learning.EF_KNN_IVFS;

/**
 * 
 * File: Main.java
 * 
 * This is the main class of the algorithm.
 * It gets the configuration script, builds the classifier and executes it.
 * 
 * @author Written by Joaquín Derrac (University of Granada) 01/11/2014
 * @version 0.2
 * @since JDK1.5
 * 
 */
public class Main {
	
	//The classifier
	private static EFKNNIVFS classifier;

	/**
	 * <p>
	 * The main method of the class
	 * </p>
	 *
	 * @param args Arguments of the program (usually a configuration script)
	 *
	 */
	public static void main (String args[]) {

		if (args.length != 1){

			System.err.println("Wrong set up. Usage: >> java Main <config_file>");

		} else {

			classifier = new EFKNNIVFS(args[0]);
            classifier.init_search();
            classifier.search_solution();
            classifier.classifyTrain();
			classifier.classifyTest();
			classifier.printReport();
		}
		
	} //end-method 
  
} //end-class

