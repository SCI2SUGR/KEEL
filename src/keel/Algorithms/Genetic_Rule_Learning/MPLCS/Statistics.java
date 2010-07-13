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

public class Statistics {
/**
 * <p>
 * Computes and stores several statistics about the learning process
 * </p>
 */
	
	
  public static double[] averageFitness;
  public static double[] averageAccuracy;
  public static double[] bestAccuracy;
  public static double[] bestRules;
  public static double[] bestAliveRules;
  public static double[] averageNumRules;
  public static double[] averageNumRulesUtils;

  public static int iterationsSinceBest = 0;
  public static double bestFitness;
  public static double last10IterationsAccuracyAverage;

  public static int countStatistics = 0;

  public static void resetBestStats() {
    iterationsSinceBest = 0;
  }

  public static int getIterationsSinceBest() {
    return iterationsSinceBest;
  }

  public static void bestOfIteration(double itBestFit) {
    if (iterationsSinceBest == 0) {
      bestFitness = itBestFit;
      iterationsSinceBest++;
    }
    else {
      boolean newBest = false;
      if (Parameters.useMDL) {
        if (itBestFit < bestFitness) {
          newBest = true;
        }
      }
      else {
        if (itBestFit > bestFitness) {
          newBest = true;
        }
      }

      if (newBest) {
        bestFitness = itBestFit;
        iterationsSinceBest = 1;
      }
      else {
        iterationsSinceBest++;
      }
    }

    int i = countStatistics - 9;
    if (i < 0) {
      i = 0;
    }
    int max = countStatistics + 1;
    int num = max - i;
    last10IterationsAccuracyAverage = 0;
    for (; i < max; i++) {
      last10IterationsAccuracyAverage += bestAccuracy[i];
    }
    last10IterationsAccuracyAverage /= (double) num;
  }

  public static void initStatistics() {
    Chronometer.startChronStatistics();

    int numStatistics = Parameters.numIterations;

    averageFitness = new double[numStatistics];
    averageAccuracy = new double[numStatistics];
    bestAccuracy = new double[numStatistics];
    bestRules = new double[numStatistics];
    bestAliveRules = new double[numStatistics];
    averageNumRules = new double[numStatistics];
    averageNumRulesUtils = new double[numStatistics];

    Chronometer.stopChronStatistics();
  }

  public static void statisticsToFile() {
    FileManagement file = new FileManagement();
    int length = countStatistics;
    String line;
    String lineToWrite = "";
    try {
      //file.initWrite("NumRules.txt");

      //TODO

      //file.closeWrite();
    }
    catch (Exception e) {
      LogManager.println("Error in statistics file");
    }
  }

  public static void computeStatistics(Classifier[] _population) {
    Chronometer.startChronStatistics();
    int populationLength = Parameters.popSize;
    Classifier classAct;
    double sumFitness = 0;
    double sumAccuracy = 0;
    double sumNumRules = 0;
    double sumNumRulesUtils = 0;

    for (int i = 0; i < populationLength; i++) {
      classAct = _population[i];
      sumFitness += classAct.getFitness();
      sumAccuracy += classAct.getAccuracy();
      sumNumRules += classAct.getNumRules();
      sumNumRulesUtils += classAct.getNumAliveRules();
    }
    sumFitness = sumFitness / populationLength;
    sumAccuracy = sumAccuracy / populationLength;
    sumNumRules = sumNumRules / populationLength;
    sumNumRulesUtils = sumNumRulesUtils / populationLength;

    Statistics.averageFitness[countStatistics] = sumFitness;
    Statistics.averageAccuracy[countStatistics] = sumAccuracy;
    Statistics.averageNumRules[countStatistics] = sumNumRules;
    Statistics.averageNumRulesUtils[countStatistics] = sumNumRulesUtils;

    Classifier best = PopulationWrapper.getBest(_population);
    LogManager.println("Best of iteration " + countStatistics + " : " +
                       best.getAccuracy() + " " + best.getFitness() + " " +
                       best.getNumRules() + "(" + best.getNumAliveRules() + ")");
    Statistics.bestAccuracy[countStatistics] = best.getAccuracy();
    Statistics.bestRules[countStatistics] = best.getNumRules();
    Statistics.bestAliveRules[countStatistics] = best.getNumAliveRules();
    bestOfIteration(best.getFitness());

    countStatistics++;
    Chronometer.stopChronStatistics();
  }

}

