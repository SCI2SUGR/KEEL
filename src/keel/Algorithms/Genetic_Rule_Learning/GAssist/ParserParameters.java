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

/*
 * ParserParameters.java
 *
 */


package keel.Algorithms.Genetic_Rule_Learning.GAssist;

import java.util.*;
import java.io.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;

public class ParserParameters {
/**
 * <p>
 * Reads the configuration file using the KEEL format
 * </p>
 */
	
	
  static BufferedReader br;
  static String algorithmName = "GAssist";

  /**
   * Creates a new instance of ParserParameters
   * @param fileName name of the parameters file
  */
  public static void doParse(String fileName) {
    try {
      br = new BufferedReader(new FileReader(fileName));
    }
    catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

    parseParameters();
  }

  /**
   *  Parses the header of the ARFF file
   */
  static void parseParameters() {
    String str = getLine();
    while (str != null) {
      StringTokenizer st = new StringTokenizer(str, "=");
      String name = st.nextToken();
      name = name.trim();
      name.replaceAll(" ", "");

      if (name.equalsIgnoreCase("algorithm")) {
        processAlgorithmName(st);
      }
      else if (name.equalsIgnoreCase("inputData")) {
        processInputs(st);
      }
      else if (name.equalsIgnoreCase("outputData")) {
        processOutputs(st);
      }
      else {
        processParameters(st, name);
      }

      str = getLine();
    }
  }

  static void processAlgorithmName(StringTokenizer st) {
    if (!st.hasMoreTokens()) {
      System.err.println("Parse error processing algorithm name");
      System.exit(1);
    }
    String name = st.nextToken();
    name = name.trim();
    if (!algorithmName.equalsIgnoreCase(name)) {
      System.err.println("This config file is not for us");
      System.exit(1);
    }
  }

  static void processInputs(StringTokenizer st) {
    if (!st.hasMoreTokens()) {
      System.err.println("Parse error processing inputs");
      System.exit(1);
    }
    String files = st.nextToken();
    files = files.trim();
    if (! (files.startsWith("\"") && files.endsWith("\""))) {
      System.err.println("Parse error processing inputs " + files);
      System.exit(1);
    }
    files.replaceAll("^\"", "");
    files.replaceAll("\"$", "");
    StringTokenizer st2 = new StringTokenizer(files, "\"");
    try {
      String file1 = st2.nextToken();
      String sep = st2.nextToken();
      String file2 = st2.nextToken();
      sep = st2.nextToken();
      String file3 = st2.nextToken();
      insertStringParameter("trainInputFile", file1);
      insertStringParameter("train2InputFile", file2);
      insertStringParameter("testInputFile", file3);
    }
    catch (NoSuchElementException e) {
      System.err.println("Parse error processing inputs " + files);
      System.exit(1);
    }
  }

  static void processOutputs(StringTokenizer st) {
    if (!st.hasMoreTokens()) {
      System.err.println("Parse error processing outputs");
      System.exit(1);
    }
    String files = st.nextToken();
    files = files.trim();
    if (! (files.startsWith("\"") && files.endsWith("\""))) {
      System.err.println("Parse error processing outputs " + files);
      System.exit(1);
    }
    files.replaceAll("^\"", "");
    files.replaceAll("\"$", "");
    StringTokenizer st2 = new StringTokenizer(files, "\"");
    try {
      String file1 = st2.nextToken();
      String sep = st2.nextToken();
      String file2 = st2.nextToken();
      sep = st2.nextToken();
      String file3 = st2.nextToken();
      insertStringParameter("trainOutputFile", file1);
      insertStringParameter("testOutputFile", file2);
      insertStringParameter("logOutputFile", file3);
    }
    catch (NoSuchElementException e) {
      System.err.println("Parse error processing outputs " + files);
      System.exit(1);
    }
  }

  static void processParameters(StringTokenizer st, String paramName) {
    if (!st.hasMoreTokens()) {
      System.err.println("Parse error processing parameter " + paramName);
      System.exit(1);
    }
    String paramValue = st.nextToken();
    paramValue = paramValue.trim();

    if (isReal(paramName)) {
      insertRealParameter(paramName, paramValue);
    }
    else if (isInteger(paramName)) {
      insertIntegerParameter(paramName, paramValue);
    }
    else if (isBoolean(paramName)) {
      insertBooleanParameter(paramName, paramValue);
    }
    else if (isString(paramName)) {
      insertStringParameter(paramName, paramValue);
    }
    else {
      System.err.println("Unknown parameter " + paramName);
      System.exit(1);
    }
  }

  static boolean isReal(String paramName) {
    if (paramName.equalsIgnoreCase("hierarchicalSelectionThreshold")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("probCrossover")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("probMutationInd")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("probOne")) {
      return true;
    }

    if (paramName.equalsIgnoreCase("probSplit")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("probMerge")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("probReinitializeBegin")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("probReinitializeEnd")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("initialTheoryLengthRatio")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("weightRelaxFactor")) {
      return true;
    }
    return false;
  }

  static boolean isInteger(String paramName) {
    if (paramName.equalsIgnoreCase("iterationRuleDeletion")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("iterationHierarchicalSelection")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("ruleDeletionMinRules")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("sizePenaltyMinRules")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("numIterations")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("seed")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("initialNumberOfRules")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("popSize")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("tournamentSize")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("numStrata")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("maxIntervals")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("iterationMDL")) {
      return true;
    }
    return false;
  }

  static boolean isBoolean(String paramName) {
    if (paramName.equalsIgnoreCase("adiKR")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("useMDL")) {
      return true;
    }
    return false;
  }

  static boolean isString(String paramName) {
    if (paramName.equalsIgnoreCase("discretizer1")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("discretizer2")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("discretizer3")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("discretizer4")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("discretizer5")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("discretizer6")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("discretizer7")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("discretizer8")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("discretizer9")) {
      return true;
    }
    if (paramName.equalsIgnoreCase("discretizer10")) {
      return true;
    }
    if(paramName.equalsIgnoreCase("defaultClass")) return true;
    if(paramName.equalsIgnoreCase("initMethod")) return true;
    return false;
  }

  static void insertRealParameter(String paramName, String paramValue) {
    double num = Double.parseDouble(paramValue);
    try {
      Parameters param = new Parameters();
      java.lang.reflect.Field f = Parameters.class.getField(paramName);
      f.setDouble(param, num);
    }
    catch (Exception e) {
      System.err.println("Cannot set param " + paramName);
      System.exit(1);
    }
  }

  static void insertIntegerParameter(String paramName, String paramValue) {
    int num = Integer.parseInt(paramValue);
    try {
      Parameters param = new Parameters();
      java.lang.reflect.Field f = Parameters.class.getField(paramName);
      f.setInt(param, num);
    }
    catch (Exception e) {
      System.err.println("Cannot set param " + paramName);
      System.exit(1);
    }
  }

  static void insertBooleanParameter(String paramName, String paramValue) {
    boolean val = false;
    if (paramValue.equalsIgnoreCase("true")) {
      val = true;
    }

    try {
      Parameters param = new Parameters();
      java.lang.reflect.Field f = Parameters.class.getField(paramName);
      f.setBoolean(param, val);
    }
    catch (Exception e) {
      System.err.println("Cannot set param " + paramName);
      System.exit(1);
    }
  }

  static void insertStringParameter(String paramName, String paramValue) {
    try {
      Parameters param = new Parameters();
      java.lang.reflect.Field f = Parameters.class.getField(paramName);
      f.set(param, new String(paramValue));
    }
    catch (Exception e) {
      System.err.println("Cannot set param " + paramName);
      System.exit(1);
    }
  }

  static String getLine() {
    String st = null;
    do {
      try {
        st = br.readLine();
      }
      catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
    while (st != null && st.equalsIgnoreCase(""));
    return st;
  }
}

