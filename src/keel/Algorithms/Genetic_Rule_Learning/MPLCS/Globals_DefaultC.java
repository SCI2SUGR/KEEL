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


package keel.Algorithms.Genetic_Rule_Learning.MPLCS;

import keel.Dataset.*;
import java.util.*;
import keel.Algorithms.Genetic_Rule_Learning.MPLCS.Assistant.Globals.*;

public class Globals_DefaultC {
  static int defaultClassPolicy;
  static int defaultClass;
  static boolean enabled;
  static int numClasses;

  static boolean nichingEnabled;
  static int numNiches;
  static ArrayList[] accDefaultRules;

  public final static int DISABLED = 1;
  public final static int MINOR = 2;
  public final static int MAJOR = 3;
  public final static int AUTO = 4;

  public static void init(boolean hasDefaultClass) {
    nichingEnabled = false;

    if (!hasDefaultClass) {
      defaultClassPolicy = DISABLED;
      enabled = false;
      numClasses = Parameters.numClasses;
      return;
    }
    if (Parameters.defaultClass == null) {
      defaultClassPolicy = DISABLED;
      enabled = false;
      numClasses = Parameters.numClasses;
      return;
    }
    if (Parameters.defaultClass.equalsIgnoreCase("disabled")) {
      defaultClassPolicy = DISABLED;
      enabled = false;
      numClasses = Parameters.numClasses;
    }
    else if (Parameters.defaultClass.equalsIgnoreCase("major")) {
      defaultClassPolicy = MAJOR;
      //Attributes.getOutputAttribute(0).getMostFrequentValue(0);
      defaultClass = majorityClass();
      numClasses = Parameters.numClasses - 1;
      enabled = true;
    }
    else if (Parameters.defaultClass.equalsIgnoreCase("minor")) {
      defaultClassPolicy = MINOR;
      defaultClass = minorityClass();
      numClasses = Parameters.numClasses - 1;
      enabled = true;
    }
    else if (Parameters.defaultClass.equalsIgnoreCase("auto")) {
      defaultClassPolicy = AUTO;
      numClasses = Parameters.numClasses - 1;
      enabled = true;

      nichingEnabled = true;
      numNiches = Parameters.numClasses;
      accDefaultRules = new ArrayList[numNiches];
      for (int i = 0; i < numNiches; i++) {
        accDefaultRules[i] = new ArrayList();
      }
    }
    else {
      System.err.println("Unknown default class option "
                         + Parameters.defaultClass);
      System.exit(1);
    }
  }

  static void checkNichingStatus(int iteration, Classifier[] population) {
    if (nichingEnabled) {
      int i;
      int[] counters = new int[numNiches];
      double[] nicheFit = new double[numNiches];
      for (i = 0; i < numNiches; i++) {
        counters[i] = 0;
        nicheFit[i] = 0;
      }

      for (i = 0; i < population.length; i++) {
        int niche = population[i].getNiche();
        counters[niche]++;
        double indAcc = population[i].getAccuracy();
        if (indAcc > nicheFit[niche]) {
          nicheFit[niche] = indAcc;
        }
      }

      if (accDefaultRules[0].size() == 15) {
        for (i = 0; i < numNiches; i++) {
          accDefaultRules[i].remove(0);
        }
      }

      for (i = 0; i < numNiches; i++) {
        accDefaultRules[i].add(new Double(nicheFit[i]));
      }

      if (accDefaultRules[0].size() == 15) {
        ArrayList aves = new ArrayList();
        for (i = 0; i < numNiches; i++) {
          double aveN = Utils.getAverage(
              accDefaultRules[i]);
          aves.add(new Double(aveN));
        }
        double dev = Utils.getDeviation(aves);
        if (dev < 0.005) {
          LogManager.println("Iteration " + iteration + ",niching disabled");
          nichingEnabled = false;
        }
      }
    }
  }

  static int majorityClass() {
    int [] counts = new int[Parameters.numClasses];
    Instance [] inst = PopulationWrapper.is.getInstances();
    for (int i = 0; i < inst.length; i++){
      counts[inst[i].getOutputNominalValuesInt(0)]++;
    }
    int maxClass = 0;
    for (int i = 1; i < Parameters.numClasses; i++){
      if (counts[i] > counts[maxClass]){
        maxClass = i;
      }
    }
    return maxClass;
  }

  static int minorityClass() {
    int [] counts = new int[Parameters.numClasses];
    Instance [] inst = PopulationWrapper.is.getInstances();
    for (int i = 0; i < inst.length; i++){
      counts[inst[i].getOutputNominalValuesInt(0)]++;
    }
    int minClass = 0;
    for (int i = 1; i < Parameters.numClasses; i++){
      if (counts[i] < counts[minClass]){
        minClass = i;
      }
    }
    return minClass;
  }

}

