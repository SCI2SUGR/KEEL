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
 * @author Written by Jose A. Saez Munoz, research group SCI2S (Soft Computing and Intelligent Information Systems).
 * DECSAI (DEpartment of Computer Science and Artificial Intelligence), University of Granada - Spain.
 * Date: 06/01/10
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

package keel.Algorithms.Preprocess.NoiseFilters.IterativeMCSbFilter.EnsembleFilter;

import java.util.*;
import java.io.*;

import keel.Algorithms.Preprocess.NoiseFilters.IterativeMCSbFilter.PartitionScheme;


/**
 * <p>
 * Main class to parse the parameters of the algorithm
 * </p>
 */
public class Parameters {
	
	public static String trainInputFile;
	public static String testInputFile;
	
	public static String trainOutputFile;
	public static String testOutputFile;
	
	public static int numClasses;
	public static int numAttributes;
	public static int numInstances;
	
	public static long seed = 0;
	public static int numPartitions = 4;
	public static String filterType = "consensus";
	
	// K-NN
	public static int numNeighbors = 1;
	public static String distanceType = "hvdm";
	
	// C4.5
	public static boolean prune = true;
	public static double confidence = 0.25;
	public static int itemsetsPerLeaf = 2;

	// ----------------------------------------------

}