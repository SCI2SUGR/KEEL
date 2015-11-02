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



package keel.Algorithms.Genetic_Rule_Learning.Globals;

import java.util.*;
import java.io.*;

/**
 * Parameters.java
 *
 * This class contains all the parameters of the system
 */
public final class Parameters {
	public static String algorithmName;

	public static double confidenceThreshold;
	public static double inconsistencyThreshold;
	public static double alpha;
	public static double beta;
	public static double lambda;
	public static int numIntervals;
	public static boolean amevaR;
	public static String numIntrvls;
	public static int Neighborhood;    
	public static int WindowsSize;
	public static String DistanceFunction;
	public static int minIntervals;
	public static String mapType;
	public static int minSupport;
	public static double mergedThreshold;
	public static double scalingFactor;
	public static boolean useDiscrete;
	public static int frequencySize;
	public static boolean setConfig;

	public static int numClasses;
	public static int numAttributes;
	public static int numInstances;

	public static int popSize;
	public static int initialNumberOfRules;
	public static double probCrossover;
	public static double probMutationInd;
	public static int tournamentSize;
	public static int numIterations;
	public static double percentageOfLearning;

	public static boolean useMDL;
	public static int iterationMDL;
	public static double initialTheoryLengthRatio;
	public static double weightRelaxFactor;
	public static double theoryWeight;

	public static double probOne;

	public static String trainInputFile;
	public static String train2InputFile;
	public static String testInputFile;
	public static String trainOutputFile;
	public static String testOutputFile;
	public static String logOutputFile;
	public static int seed;

	public static boolean doRuleDeletion=false;
	public static int iterationRuleDeletion;
	public static int ruleDeletionMinRules;
	public static boolean doHierarchicalSelection=false;
	public static int iterationHierarchicalSelection;
	public static double hierarchicalSelectionThreshold;
	public static int sizePenaltyMinRules;

	public static int numStrata;

	public static String discretizer1;
	public static String discretizer2;
	public static String discretizer3;
	public static String discretizer4;
	public static String discretizer5;
	public static String discretizer6;
	public static String discretizer7;
	public static String discretizer8;
	public static String discretizer9;
	public static String discretizer10;
	public static int maxIntervals;
	public static double probSplit;
	public static double probMerge;
	public static double probReinitialize;
	public static double probReinitializeBegin;
	public static double probReinitializeEnd;
	public static boolean adiKR=false;
	public static int minimumValuesOfSameClassPerInterval;
	public static String processType;
        public static String defaultClass;
        public static String initMethod;
	public static double coefficient; 
}

