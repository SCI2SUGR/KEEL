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

package keel.Algorithms.Genetic_Rule_Learning.BioHEL;


import java.util.*;
import java.io.*;
import keel.Dataset.Instance;


/**
 * <p>
 * Main class to parse the parameters of the algorithm
 * </p>
 */
public class Parameters {
	
	//-- tags
	final public static int MAJOR = 0;
	final public static int MINOR = 1;
	final public static int DISABLED = 2;
	final public static int MAXIMIZE = 0;
	final public static int MINIMIZE = 1;
	final public static int TRAIN = 0;
	final public static int TEST = 1;
	final public static int ILAS = 0;
	final public static int GWS = 1;
	final public static int NONE = 2;
	final public static int TOURNAMENT = 0;
	final public static int TOURNAMENT_WOR = 1;
	
	private static BufferedReader br;
	
	// configuration parameters ------------------
	public static String algorithmName;
	
	public static String trainInputFile;
	public static String train2InputFile;
	public static String testInputFile;
	
	public static String trainOutputFile;
	public static String testOutputFile;
	public static String logOutputFile;
	
	public static int numClasses;
	
	// parameters of the algorithm ------------------
	public static long seed;
	public static int popSize;
	public static int tournamentSize;
	public static int expectedRuleSize;
	public static int numIterations;
	public static int numRepetitionsLearning;
	public static int numStrataWindowing;
	public static int numStages;
	public static int numIterationsMDL;
	
	public static double crossoverProbability;
	public static double mutationProbability;
	public static double coverageBreakpoint;
	public static double coverageRatio;
	public static double generalizeProbability;
	public static double specializeProbability;
	public static double initialTheoryLengthRatio;
	public static double mdlWeightRelaxFactor;
	public static double probOne;
	

	public static boolean elitismEnabled;
	
	public static int NumAttributes;
	public static int NumInstances;
	
	public static Instance[] instances;
	//--
	public static String defClass;
	public static int defaultClassOption;
	public static int defaultClassInteger;
	
	public static String optMethod;
	public static String selectAlg;
	public static String initializationMethod;
	public static int initMethod;
	public static int selectionAlg;
	public static int optimizationMethod;
	public static int[] attributeSize;
	public static String winMethod;
	public static int windowingMethod;
	public static instanceSet is;
	public static populationWrapper pw;
	public static int[] InstancesOfClass;
	public static timersManagement timers;
	public static classifierFactory cFac;
	public static classifier_aggregated ruleSet;
	public static boolean classWiseInit;
	public static boolean smartInitMethod;
	public static boolean useMDL;
	// ----------------------------------------------
	

//******************************************************************************************************
	
	/**
	 * <p>
	 * It parses the parameters of the algorithm
	 * </p>
	 * @param fileName the configuration file name
	 */
	public static void doParse(String fileName){
		
		try {
			br = new BufferedReader(new FileReader(fileName));
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}

		parseParameters();
		System.out.println("--> Parameters correctly parsed\n\n");
	}
	
//******************************************************************************************************
	
	/**
	 * <p>
	 * It parses the header of the ARFF file
	 * </p>
	 */
	static void parseParameters(){
		
		String str = getLine();
		
		while(str != null){
			
			//System.out.println("Line: " + str);
			StringTokenizer st = new StringTokenizer(str,"=");
			String name = st.nextToken();
			name = name.trim();

			if(name.equalsIgnoreCase("algorithm")) 
				processAlgorithmName(st);
			
			else if(name.equalsIgnoreCase("inputData"))
				processInputs(st);
			
			else if(name.equalsIgnoreCase("outputData"))
				processOutputs(st);
			
			else processParameters(st,name);

			str = getLine();
		}
	}

//******************************************************************************************************
	
	/**
	 * <p>
	 * It processes the algorithm name
	 * </p>
	 * @param st string with the algorithm name
	 */
	static void processAlgorithmName(StringTokenizer st){
		
		if(!st.hasMoreTokens()){
			System.err.println("Parse error processing algorithm name");
			System.exit(1);
		}
		
		String name = st.nextToken();
		name = name.trim();
		//System.out.println("The name is: "+name);
		
		if(!validateAlgorithmName(name)){
			System.err.println("This config file is not for us: " + name);
			System.exit(1);
		}
		
		algorithmName = new String(name);
		//System.out.println ("Returning from processAlgorithmName");
	}
	
//******************************************************************************************************	

	/**
	 * <p>
	 * It processes input files
	 * </p>
	 * @param st string with the input files
	 */
	static void processInputs(StringTokenizer st){
		
		if(!st.hasMoreTokens()){
			System.err.println("Parse error processing inputs");
			System.exit(1);
		}
		
		String files = st.nextToken();
		files = files.trim();
		
		if(!(files.startsWith("\"") && files.endsWith("\""))){
			System.err.println("Parse error processing inputs " + files);
			System.exit(1);
		}
		
		files.replaceAll("^\"",""); files.replaceAll("\"$","");
		StringTokenizer st2 = new StringTokenizer(files,"\"");
		
		try {
			String file1 = st2.nextToken();
			st2.nextToken();
			String file2 = st2.nextToken();
			st2.nextToken();
			String file3 = st2.nextToken();
			insertStringParameter("trainInputFile",file1);
			insertStringParameter("train2InputFile",file2);
			insertStringParameter("testInputFile",file3);
		}catch(NoSuchElementException e){
			System.err.println("Parse error processing inputs " + files);
			System.exit(1);
		}
	}

//******************************************************************************************************	
	
	/**
	 * <p>
	 * It processes output files
	 * </p>
	 * @param st string with the output files
	 */
	static void processOutputs(StringTokenizer st){
		
		if(!st.hasMoreTokens()){
			System.err.println("Parse error processing outputs");
			System.exit(1);
		}
		
		String files = st.nextToken();
		files = files.trim();
		
		if(!(files.startsWith("\"") && files.endsWith("\""))){
			System.err.println("Parse error processing outputs " + files);
			System.exit(1);
		}
		
		files.replaceAll("^\"",""); files.replaceAll("\"$","");
		StringTokenizer st2 = new StringTokenizer(files,"\"");
		
		try {
			String file1 = st2.nextToken();
			st2.nextToken();
			String file2 = st2.nextToken();
			st2.nextToken();
			String file3 = st2.nextToken();
			insertStringParameter("trainOutputFile",file1);
			insertStringParameter("testOutputFile",file2);
			insertStringParameter("logOutputFile",file3);
		}catch(NoSuchElementException e){
			System.err.println("Parse error processing outputs " + files);
			System.exit(1);
		}
	}
	
//******************************************************************************************************

	/**
	 * <p>
	 * It processes the parameters
	 * </p>
	 * @param st string with the parameter value
	 * @param paramName parameter name
	 */
	static void processParameters(StringTokenizer st, String paramName){
		
		if(!st.hasMoreTokens()){
			System.err.println("Parse error processing parameter " + paramName);
			System.exit(1);
		}
		
		String paramValue = st.nextToken();
		paramValue = paramValue.trim();

		//System.out.println("paramName: " + paramName);
		
		if(isReal(paramName)) 
			insertRealParameter(paramName,paramValue);
		
		else if(isInteger(paramName))
			insertIntegerParameter(paramName,paramValue);
		
		else if(isBoolean(paramName))
			insertBooleanParameter(paramName,paramValue);
		
		else if(isString(paramName))
			insertStringParameter(paramName,paramValue);
		
		else{
			System.err.println("Unknown parameter " + paramName);
			System.exit(1);
		}
	}
	
//******************************************************************************************************

	/**
	 * <p>
	 * It parses a real parameter
	 * </p>
	 * @param paramName parameter name
	 */
	static boolean isReal(String paramName){

		if(paramName.equalsIgnoreCase("crossoverProbability")) return true;
		if(paramName.equalsIgnoreCase("mutationProbability")) return true;
		if(paramName.equalsIgnoreCase("coverageBreakpoint")) return true;
		if(paramName.equalsIgnoreCase("coverageRatio")) return true;
		if(paramName.equalsIgnoreCase("generalizeProbability")) return true;
		if(paramName.equalsIgnoreCase("specializeProbability")) return true;
		if(paramName.equalsIgnoreCase("initialTheoryLengthRatio")) return true;
		if(paramName.equalsIgnoreCase("mdlWeightRelaxFactor")) return true;
		if(paramName.equalsIgnoreCase("probOne")) return true;
				
		return false;
	}

//******************************************************************************************************
	
	/**
	 * <p>
	 * It parses an integer parameter
	 * </p>
	 * @param paramName parameter name
	 */
	static boolean isInteger(String paramName){
		
		if(paramName.equalsIgnoreCase("seed")) return true;
		if(paramName.equalsIgnoreCase("popSize")) return true;
		if(paramName.equalsIgnoreCase("tournamentSize")) return true;
		if(paramName.equalsIgnoreCase("expectedRuleSize")) return true;
		if(paramName.equalsIgnoreCase("numIterations")) return true;
		if(paramName.equalsIgnoreCase("numRepetitionsLearning")) return true;
		if(paramName.equalsIgnoreCase("numStrataWindowing")) return true;
		if(paramName.equalsIgnoreCase("numStages")) return true;
		if(paramName.equalsIgnoreCase("numIterationsMDL")) return true;
		
		return false;
	}

//******************************************************************************************************
	
	/**
	 * <p>
	 * It parses an boolean parameter
	 * </p>
	 * @param paramName parameter name
	 */
	static boolean isBoolean(String paramName){
		
		if(paramName.equalsIgnoreCase("elitismEnabled")) return true;
		if(paramName.equalsIgnoreCase("classWiseInit")) return true;
		if(paramName.equalsIgnoreCase("smartInitMethod")) return true;		
		if(paramName.equalsIgnoreCase("useMDL")) return true;		

		return false;
	}

//******************************************************************************************************
	
	/**
	 * <p>
	 * It parses a string parameter
	 * </p>
	 * @param paramName parameter name
	 */
	static boolean isString(String paramName){
		
		if(paramName.equalsIgnoreCase("defClass")) return true;
		if(paramName.equalsIgnoreCase("optMethod")) return true;
		if(paramName.equalsIgnoreCase("selectAlg")) return true;
		if(paramName.equalsIgnoreCase("initializationMethod")) return true;	
		if(paramName.equalsIgnoreCase("winMethod")) return true;

		return false;
	}
	
//******************************************************************************************************

	/**
	 * <p>
	 * It does the assignment of a real parameter
	 * </p>
	 * @param paramName parameter name
	 * @param paramValue parameter value
	 */
	static void insertRealParameter(String paramName, String paramValue){
		
		double num = Double.parseDouble(paramValue);
		
		try {
			Parameters param = new Parameters();
			java.lang.reflect.Field f = Parameters.class.getField(paramName);
			f.setDouble(param,num);
		}catch(Exception e){
			System.err.println("Cannot set param " + paramName);
			System.exit(1);
		}
	}

//******************************************************************************************************
	
	/**
	 * <p>
	 * It does the assignment of an integer parameter
	 * </p>
	 * @param paramName parameter name
	 * @param paramValue parameter value
	 */
	static void insertIntegerParameter(String paramName, String paramValue){
		
		int num = Integer.parseInt(paramValue);
		
		try{
			Parameters param = new Parameters();
			java.lang.reflect.Field f = Parameters.class.getField(paramName);
			f.setInt(param,num);
		}catch(Exception e){
			System.err.println("Cannot set param " + paramName);
			System.exit(1);
		}
	}
	
//******************************************************************************************************

	/**
	 * <p>
	 * It does the assignment of a boolean parameter
	 * </p>
	 * @param paramName parameter name
	 * @param paramValue parameter value
	 */
	static void insertBooleanParameter(String paramName, String paramValue){
		
		boolean val = false;
		
		if(paramValue.equals("true")) val = true;

		try{
			Parameters param = new Parameters();
			java.lang.reflect.Field f = Parameters.class.getField(paramName);
			f.setBoolean(param,val);
		}catch(Exception e){
			System.err.println("Cannot set param " + paramName);
			System.exit(1);
		}
	}

//******************************************************************************************************

	/**
	 * <p>
	 * It does the assignment of a string parameter
	 * </p>
	 * @param paramName parameter name
	 * @param paramValue parameter value
	 */
	static void insertStringParameter(String paramName, String paramValue){
		
		try{
			Parameters param = new Parameters();
			java.lang.reflect.Field f = Parameters.class.getField(paramName);
			f.set(param,new String(paramValue));
		}catch(Exception e){
			System.err.println("Cannot set param " + paramName);
			System.exit(1);
		}
	}
	
//******************************************************************************************************	

	/**
	 * <p>
	 * It checks the algorithm name
	 * </p>
	 * @param name algorithm name
	 */
	static boolean validateAlgorithmName(String name){
		
		if(name.equalsIgnoreCase("BioHEL Classifier")) return true;

		return false;
	}

//******************************************************************************************************

	/**
	 * <p>
	 * It returns the next line in the configuration file
	 * </p>
	 */
	static String getLine(){
		
		String st = null;
		
		do{
			
			try {
				st = br.readLine();
			}catch(Exception e){
				e.printStackTrace();
				System.exit(1);
			}
		
		}while(st != null && st.equals(""));
		
		return st;
	}
	
}
