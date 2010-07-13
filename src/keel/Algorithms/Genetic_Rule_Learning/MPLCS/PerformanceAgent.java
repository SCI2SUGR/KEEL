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

import keel.Algorithms.Genetic_Rule_Learning.MPLCS.Assistant.Globals.*;


public class PerformanceAgent {
/**
 * <p>
 * This class computes several performance measures taken from a fitness
 * computation of an individual
 * </p>
 */
	

  public static int[] utilRules;
  public static int[] okClass;
  public static int[] totalClass;
  public static int[][] confusionMatrix;

  static int numAliveRules;

  static double totalInstances;
  static double okInstances;

  /**
   * This function computes the rules of an individual that will be
   * deleted by the rule deletion operator, based on their activity
   * during the previous fitness computation cycle
   */
  public static int[] controlBloatRuleDeletion() {
    int nRules = utilRules.length;
    int minRules = Parameters.ruleDeletionMinRules;
    int[] rulesToDelete = new int[nRules];
    int countDeleted = 0;

    if (nRules > minRules) {
      for (int i = 0; i < nRules; i++) {
        if (utilRules[i] == 0) {
          rulesToDelete[countDeleted++] = i;
        }
      }

      if (nRules - countDeleted < minRules) {
        int rulesToKeep = minRules - (nRules - countDeleted);
        for (int i = 0; i < rulesToKeep; i++) {
          int pos = Rand.getInteger(0, countDeleted - 1);
          for (int j = pos + 1; j < countDeleted; j++) {
            rulesToDelete[j - 1] = rulesToDelete[j];
          }
          countDeleted--;
        }
      }
    }

    int[] arrayRules = new int[countDeleted];
    for (int i = 0; i < countDeleted; i++) {
      arrayRules[i] = rulesToDelete[i];
    }
    return arrayRules;
  }

  public static void resetPerformance(int _numRules) {
    utilRules = new int[_numRules];

    for (int i = 0; i < _numRules; i++) {
      utilRules[i] = 0;
    }

    numAliveRules = 0;
    totalInstances = 0;
    okInstances = 0;
  }

  /**
   * The test stage computes more statistics. The data structures that
   * hold these statistics are initialized here
   */
  public static void resetPerformanceTest(int _numRules) {
    utilRules = new int[_numRules];
    okClass = new int[Parameters.numClasses];
    totalClass = new int[Parameters.numClasses];
    confusionMatrix = new int[Parameters.numClasses][];

    for (int i = 0; i < _numRules; i++) {
      utilRules[i] = 0;
    }
    for (int i = 0; i < Parameters.numClasses; i++) {
      okClass[i] = totalClass[i] = 0;
      confusionMatrix[i] = new int[Parameters.numClasses];
      for (int j = 0; j < Parameters.numClasses; j++) {
        confusionMatrix[i][j] = 0;
      }
    }

    numAliveRules = 0;
    totalInstances = 0;
    okInstances = 0;
  }

  /**
   * Function used to inform PerformanceAgent of each example
   * classified during the training stage
   */
  public static void addPrediction(int predicted, int real, int activatedRule) {
    totalInstances++;
    if (predicted != -1) {
      if (utilRules[activatedRule] == 0
          && activatedRule < utilRules.length) {
        numAliveRules++;
      }
      utilRules[activatedRule]++;
    }
    if (predicted == real) {
      okInstances++;
    }
  }

  /**
   * Function used to inform PerformanceAgent of each example
   * classified during the test stage
   */
  public static void addPredictionTest(int predicted, int real,
                                       int activatedRule) {
    totalInstances++;
    totalClass[real]++;

    if (predicted != -1) {
      if (utilRules[activatedRule] == 0
          && activatedRule < utilRules.length) {
        numAliveRules++;
      }
      utilRules[activatedRule]++;
      confusionMatrix[real][predicted]++;
    }
    if (predicted == real) {
      okInstances++;
      okClass[real]++;
    }
  }

  public static double getAccuracy() {
    return okInstances / totalInstances;
  }

  public static int getNumAliveRules() {
    return numAliveRules;
  }

  public static int getActivationsOfRule(int rule) {
    return utilRules[rule];
  }

  /**
   * This function returns the fitness formula used
   */
  public static double getFitness(Classifier ind) {
    double fitness;
    double penalty = 1;

    if (numAliveRules < Parameters.sizePenaltyMinRules) {
      penalty = (1 - 0.025 * (Parameters.sizePenaltyMinRules - numAliveRules));
      if (penalty <= 0) {
        penalty = 0.01;
      }
      penalty *= penalty;
    }

    if (Parameters.useMDL) {
      fitness = Globals_MDL.mdlFitness(ind) / penalty;
    }
    else {
      fitness = getAccuracy();
      fitness *= fitness;
      fitness *= penalty;
    }

    return fitness;
  }

  /**
   * This function dumps the test statistics
   */
  public static void dumpStats(String typeOfTest) {
	  
	  
	  //guardo los resultados para imprimirlos
	  if(typeOfTest.equals("training"))
		  Main.accTrain=getAccuracy();
	  if(typeOfTest.equals("test"))
		  Main.accTest=getAccuracy();
	  
	  
    LogManager.println(typeOfTest + " accuracy " + okInstances + "/" +
                       totalInstances + "=" + getAccuracy());
    for (int i = 0; i < Parameters.numClasses; i++) {
      LogManager.println(typeOfTest + " acc. in class " + i + " : " + okClass[i] +
                         "/" + totalClass[i] + "=" +
                         ( (double) okClass[i] / (double) totalClass[i]));
    }
    LogManager.println(typeOfTest +
                       " confusion matrix. Rows=real, columns=predicted");
    for (int i = 0; i < Parameters.numClasses; i++) {
      for (int j = 0; j < Parameters.numClasses; j++) {
        LogManager.print(confusionMatrix[i][j] + "\t");
      }
      LogManager.println("");
    }
    LogManager.println(typeOfTest + " activations of classifiers");
    for (int i = 0; i < utilRules.length; i++) {
      LogManager.println("Classifier " + i + " : " + utilRules[i]);
    }

  }
}

