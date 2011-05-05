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

package keel.Algorithms.Discretizers.Basic;

import java.util.*;
import java.io.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;

public class ParserParameters {
	static BufferedReader br;
	
	/** Creates a new instance of ParserParameters */
	public static void doParse(String fileName) {
		try {
			br=new BufferedReader(new FileReader(fileName));
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		parseParameters();	

		System.out.println ("Parameters parsed");
	}
	
	/**
	 *  Parses the header of the ARFF file
	 */
	static void parseParameters() {
		String str=getLine();
		while(str!= null) {
			System.out.println ("Line: "+str);
			StringTokenizer st = new StringTokenizer(str,"=");
			String name = st.nextToken();
			name=name.trim();

			if(name.equalsIgnoreCase("algorithm")) 
				processAlgorithmName(st);
			else if(name.equalsIgnoreCase("inputData"))
				processInputs(st);
			else if(name.equalsIgnoreCase("outputData"))
				processOutputs(st);
			else processParameters(st,name);

			str=getLine();
		}
	}

	static void processAlgorithmName(StringTokenizer st) {
		if(!st.hasMoreTokens()) {
			System.err.println("Parse error processing algorithm name");
			System.exit(1);
		}
		String name=st.nextToken();
		name=name.trim();
		System.out.println ("The name is: "+name);
		if(!validateAlgorithmName(name)) {
			System.err.println("This config file is not for us : "
				+name);
			System.exit(1);
		}
		Parameters.algorithmName = new String(name);
		System.out.println ("Returning from processAlgorithmName");
	}

	static void processInputs(StringTokenizer st) {
		if(!st.hasMoreTokens()) {
			System.err.println("Parse error processing inputs");
			System.exit(1);
		}
		String files=st.nextToken();
		files=files.trim();
		if(!(files.startsWith("\"") && files.endsWith("\""))) {
			System.err.println("Parse error processing inputs "+files);
			System.exit(1);
		}
		files.replaceAll("^\"",""); files.replaceAll("\"$","");
		StringTokenizer st2 = new StringTokenizer(files,"\"");
		try {
			String file1=st2.nextToken();
			st2.nextToken();
			String file2=st2.nextToken();
			insertStringParameter("trainInputFile",file1);
			insertStringParameter("testInputFile",file2);
		} catch(NoSuchElementException e) {
			System.err.println("Parse error processing inputs "+files);
			System.exit(1);
		}
	}

	static void processOutputs(StringTokenizer st) {
		if(!st.hasMoreTokens()) {
			System.err.println("Parse error processing outputs");
			System.exit(1);
		}
		String files=st.nextToken();
		files=files.trim();
		if(!(files.startsWith("\"") && files.endsWith("\""))) {
			System.err.println("Parse error processing outputs "+files);
			System.exit(1);
		}
		files.replaceAll("^\"",""); files.replaceAll("\"$","");
		StringTokenizer st2 = new StringTokenizer(files,"\"");
		try {
			String file1=st2.nextToken();
			st2.nextToken();
			String file2=st2.nextToken();
			st2.nextToken();
			String file3=st2.nextToken();
			insertStringParameter("trainOutputFile",file1);
			insertStringParameter("testOutputFile",file2);
			insertStringParameter("logOutputFile",file3);
		} catch(NoSuchElementException e) {
			System.err.println("Parse error processing outputs "+files);
			System.exit(1);
		}
	}

	static void processParameters(StringTokenizer st,String paramName) {
		if(!st.hasMoreTokens()) {
			System.err.println("Parse error processing parameter "+paramName);
			System.exit(1);
		}
		String paramValue=st.nextToken();
		paramValue=paramValue.trim();

		System.out.println ("paramName: "+paramName);
		if(isReal(paramName)) 
			insertRealParameter(paramName,paramValue);
		else if(isInteger(paramName))
			insertIntegerParameter(paramName,paramValue);
		else if(isBoolean(paramName))
			insertBooleanParameter(paramName,paramValue);
		else if(isString(paramName))
			insertStringParameter(paramName,paramValue);
		else {
			System.err.println("Unknown parameter "+paramName);
			System.exit(1);
		}
	}

	static boolean isReal(String paramName) {
		if(Parameters.algorithmName.equalsIgnoreCase("ChiMergeDiscretizer") && paramName.equalsIgnoreCase("confidenceThreshold")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("Chi-Merge Discretizer") && paramName.equalsIgnoreCase("confidenceThreshold")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("CADD") && paramName.equalsIgnoreCase("confidenceThreshold")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("Class-Atribute Dependent Discretizer") && paramName.equalsIgnoreCase("confidenceThreshold")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("Chi2Discretizer") && paramName.equalsIgnoreCase("inconsistencyThreshold")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("Chi2 Discretizer") && paramName.equalsIgnoreCase("inconsistencyThreshold")) return true;
        if(Parameters.algorithmName.equalsIgnoreCase("UnificationDiscretizer") && paramName.equalsIgnoreCase("alpha")) return true;
        if(Parameters.algorithmName.equalsIgnoreCase("Unification Discretizer") && paramName.equalsIgnoreCase("alpha")) return true;
        if(Parameters.algorithmName.equalsIgnoreCase("UnificationDiscretizer") && paramName.equalsIgnoreCase("beta")) return true;
        if(Parameters.algorithmName.equalsIgnoreCase("Unification Discretizer") && paramName.equalsIgnoreCase("beta")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("UCPD") && paramName.equalsIgnoreCase("mergedThreshold")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("UCPD") && paramName.equalsIgnoreCase("scalingFactor")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("HDD") && paramName.equalsIgnoreCase("coefficient")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("Multivariate Discretizer") && paramName.equalsIgnoreCase("alpha")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("FUSINTER") && paramName.equalsIgnoreCase("lambda")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("FUSINTER") && paramName.equalsIgnoreCase("alpha")) return true;
		return false;
	}

	static boolean isInteger(String paramName) {
		if(Parameters.algorithmName.equalsIgnoreCase("UniformWidthDiscretizer") && paramName.equalsIgnoreCase("numIntervals")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("Uniform Width Discretizer") && paramName.equalsIgnoreCase("numIntervals")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("UniformFrequencyDiscretizer") && paramName.equalsIgnoreCase("numIntervals")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("Uniform Frequency Discretizer") && paramName.equalsIgnoreCase("numIntervals")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("RandomDiscretizer") && paramName.equalsIgnoreCase("seed")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("Random Discretizer") && paramName.equalsIgnoreCase("seed")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("RandomDiscretizer") && paramName.equalsIgnoreCase("seed")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("Random Discretizer") && paramName.equalsIgnoreCase("seed")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("CADD") && paramName.equalsIgnoreCase("seed")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("Class-Atribute Dependent Discretizer") && paramName.equalsIgnoreCase("seed")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("UniformFrequencyDiscretizer") && paramName.equalsIgnoreCase("seed")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("Uniform Frequency Discretizer") && paramName.equalsIgnoreCase("seed")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("CADD") && paramName.equalsIgnoreCase("numIntervals")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("Class-Atribute Dependent Discretizer") && paramName.equalsIgnoreCase("numIntervals")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("FFD") && paramName.equalsIgnoreCase("frequencySize")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("Fixed Frequency Discretizer") && paramName.equalsIgnoreCase("frequencySize")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("PD") && paramName.equalsIgnoreCase("seed")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("Proportional Discretizer") && paramName.equalsIgnoreCase("seed")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("FFD") && paramName.equalsIgnoreCase("seed")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("Fixed Frequency Discretizer") && paramName.equalsIgnoreCase("seed")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("OneR Discretizer") && paramName.equalsIgnoreCase("minimumValuesOfSameClassPerInterval")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("IDD") && paramName.equalsIgnoreCase("Neighborhood")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("IDD") && paramName.equalsIgnoreCase("WindowsSize")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("UCPD") && paramName.equalsIgnoreCase("Neighborhood")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("UCPD") && paramName.equalsIgnoreCase("maxIntervals")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("UCPD") && paramName.equalsIgnoreCase("minIntervals")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("UCPD") && paramName.equalsIgnoreCase("minSupport")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("UCPD") && paramName.equalsIgnoreCase("seed")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("HeterDisc") && paramName.equalsIgnoreCase("numIntervals")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("HellingerBD") && paramName.equalsIgnoreCase("numIntervals")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("IDD") && paramName.equalsIgnoreCase("numIntervals")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("DIBD") && paramName.equalsIgnoreCase("numIntervals")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("Multivariate Discretizer") && paramName.equalsIgnoreCase("numIntervals")) return true;
		return false;
	}

	static boolean isBoolean(String paramName) {
	    if(Parameters.algorithmName.equalsIgnoreCase("AmevaDiscretizer") && paramName.equalsIgnoreCase("amevaR")) return true;
	    if(Parameters.algorithmName.equalsIgnoreCase("Ameva Discretizer") && paramName.equalsIgnoreCase("amevaR")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("UCPD") && paramName.equalsIgnoreCase("useDiscrete")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("HeterDisc") && paramName.equalsIgnoreCase("setConfig")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("HellingerBD") && paramName.equalsIgnoreCase("setConfig")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("IDD") && paramName.equalsIgnoreCase("setConfig")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("DIBD") && paramName.equalsIgnoreCase("setConfig")) return true;
		return false;
	}

	static boolean isString(String paramName) {
		if(Parameters.algorithmName.equalsIgnoreCase("MODL Discretizer") && paramName.equalsIgnoreCase("processType")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("DIBD") && paramName.equalsIgnoreCase("numIntrvls")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("HellingerBD") && paramName.equalsIgnoreCase("numIntrvls")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("HeterDisc") && paramName.equalsIgnoreCase("numIntrvls")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("IDD") && paramName.equalsIgnoreCase("numIntrvls")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("IDD") && paramName.equalsIgnoreCase("DistanceFunction")) return true;
		if(Parameters.algorithmName.equalsIgnoreCase("UCPD") && paramName.equalsIgnoreCase("mapType")) return true;
		return false;
	}


	static void insertRealParameter(String paramName,String paramValue) {
		double num=Double.parseDouble(paramValue);
		try {
			Parameters param=new Parameters();
			java.lang.reflect.Field f= Parameters.class.getField(paramName);
			f.setDouble(param,num);
		} catch(Exception e) {
			System.err.println("Cannot set param "+paramName);
			System.exit(1);
		}
	}

	static void insertIntegerParameter(String paramName,String paramValue) {
		int num=Integer.parseInt(paramValue);
		try {
			Parameters param=new Parameters();
			java.lang.reflect.Field f= Parameters.class.getField(paramName);
			f.setInt(param,num);
		} catch(Exception e) {
			System.err.println("Cannot set param "+paramName);
			System.exit(1);
		}
	}

	static void insertBooleanParameter(String paramName,String paramValue) {
		boolean val=false;
		if(paramValue.equals("true")) val=true;

		try {
			Parameters param=new Parameters();
			java.lang.reflect.Field f= Parameters.class.getField(paramName);
			f.setBoolean(param,val);
		} catch(Exception e) {
			System.err.println("Cannot set param "+paramName);
			System.exit(1);
		}
	}


	static void insertStringParameter(String paramName,String paramValue) {
		try {
			Parameters param=new Parameters();
			java.lang.reflect.Field f= Parameters.class.getField(paramName);
			f.set(param,new String(paramValue));
		} catch(Exception e) {
			System.err.println("Cannot set param "+paramName);
			System.exit(1);
		}
	}

	static boolean validateAlgorithmName(String name) {
		if(name.equalsIgnoreCase("ChiMergeDiscretizer") ||
			name.equalsIgnoreCase("Chi-Merge Discretizer")) return true;

		if(name.equalsIgnoreCase("FayyadDiscretizer") ||
			name.equalsIgnoreCase("Fayyad Discretizer")) return true;

		if(name.equalsIgnoreCase("Id3Discretizer") ||
			name.equalsIgnoreCase("ID3 Discretizer")) return true;

		if(name.equalsIgnoreCase("RandomDiscretizer") ||
			name.equalsIgnoreCase("Random Discretizer")) return true;

		if(name.equalsIgnoreCase("USDDiscretizer") ||
			name.equalsIgnoreCase("USD Discretizer")) return true;

		if(name.equalsIgnoreCase("UniformWidthDiscretizer") ||
			name.equalsIgnoreCase("Uniform Width Discretizer"))return true;

		if(name.equalsIgnoreCase("UniformFrequencyDiscretizer") ||
			name.equalsIgnoreCase("Uniform Frequency Discretizer"))return true;

		if(name.equalsIgnoreCase("CADD") ||
				name.equalsIgnoreCase("Class-Atribute Dependent Discretizer"))return true;
		
		if(name.equalsIgnoreCase("Chi2Discretizer") ||
				name.equalsIgnoreCase("Chi2 Discretizer")) return true;

		if(name.equalsIgnoreCase("ModifiedChi2Discretizer") ||
				name.equalsIgnoreCase("ModifiedChi2 Discretizer")) return true;

		if(name.equalsIgnoreCase("ExtendedChi2Discretizer") ||
				name.equalsIgnoreCase("ExtendedChi2 Discretizer")) return true;

		if(name.equalsIgnoreCase("CAIM") ||
				name.equalsIgnoreCase("Class-Attribute Interdependence Maximization Discretizer"))return true;

		if(name.equalsIgnoreCase("PD") ||
				name.equalsIgnoreCase("Proportional Discretizer")) return true;

		if(name.equalsIgnoreCase("FFD") ||
				name.equalsIgnoreCase("Fixed Frequency Discretizer")) return true;
				
		if(name.equalsIgnoreCase("MODLDiscretizer") ||
				name.equalsIgnoreCase("MODL Discretizer")) return true;
		
		if(name.equalsIgnoreCase("KhiopsDiscretizer") ||
				name.equalsIgnoreCase("Khiops Discretizer")) return true;
		
		if(name.equalsIgnoreCase("OneRDiscretizer") ||
				name.equalsIgnoreCase("OneR Discretizer")) return true;

				if(name.equalsIgnoreCase("MantarasDistDiscretizer") ||
                name.equalsIgnoreCase("Mantaras Distance-Based Discretizer")) return true;
        
		if(name.equalsIgnoreCase("ZetaDiscretizer") ||
                name.equalsIgnoreCase("Zeta Discretizer")) return true;
        
		if(name.equalsIgnoreCase("AmevaDiscretizer") ||
                name.equalsIgnoreCase("Ameva Discretizer")) return true;
        
		if(name.equalsIgnoreCase("BayesianDiscretizer") ||
                name.equalsIgnoreCase("Bayesian Discretizer")) return true;
        
		if(name.equalsIgnoreCase("UnificationDiscretizer") ||
                name.equalsIgnoreCase("Unification Discretizer")) return true;
		if(name.equalsIgnoreCase("CACC")) return true;
		if(name.equalsIgnoreCase("DIBD")) return true;
		if(name.equalsIgnoreCase("HellingerBD")) return true;
		if(name.equalsIgnoreCase("HeterDisc")) return true;
		if(name.equalsIgnoreCase("IDD")) return true;
		if(name.equalsIgnoreCase("UCPD")) return true;

		if(name.equalsIgnoreCase("CAD") ||
				name.equalsIgnoreCase("Cluster Analysis Discretizer")) return true;
		if(name.equalsIgnoreCase("HDD")) return true;
		
		if(name.equalsIgnoreCase("MVD") ||
				name.equalsIgnoreCase("Multivariate Discretizer")) return true;
		if(name.equalsIgnoreCase("FUSINTER")) return true;

		return false;
	}



	static String getLine() {
		String st=null;
		do {
			try {
				st=br.readLine();
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		} while(st!=null && st.equals(""));
		return st;
	}

	
}
