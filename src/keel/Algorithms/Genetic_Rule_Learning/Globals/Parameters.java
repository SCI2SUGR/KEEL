/*
 * Parameters.java
 *
 * This class contains all the parameters of the system
 */

package keel.Algorithms.Genetic_Rule_Learning.Globals;

import java.util.*;
import java.io.*;

public final class Parameters {
	public static String algorithmName;

	public static double confidenceThreshold;
	public static double inconsistencyThreshold;
	public static int numIntervals;
	public static int frequencySize;

	public static int numClasses;
	public static int numAttributes;

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
}
