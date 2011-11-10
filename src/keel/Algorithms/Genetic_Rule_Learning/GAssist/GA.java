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


package keel.Algorithms.Genetic_Rule_Learning.GAssist;

import java.util.*;
import java.lang.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;


public class GA {

  Classifier[] population;
  Classifier[] bestIndiv;
  int numVersions;

  /** Creates a new instance of GA */
  public GA() {
  }

  /**
   *  Prepares GA for a new run.
   */
  public void initGA() {
    //Init population
    population = new Classifier[Parameters.popSize];
    PopulationWrapper.initInstancesEvaluation();

    numVersions = PopulationWrapper.numVersions();
    bestIndiv = new Classifier[numVersions];

    Factory.initialize();
    initPopulation(population);
    Statistics.initStatistics();
  }

  /**
   *  Inits a new population.
   *  @param _population Population to init.
   */
  private void initPopulation(Classifier[] _population) {
    for (int i = 0; i < Parameters.popSize; i++) {
      _population[i] = Factory.newClassifier();
      _population[i].initRandomClassifier();
    }
  }

  public void checkBestIndividual() {
    Classifier best = PopulationWrapper.getBest(population);
    int currVer = PopulationWrapper.getCurrentVersion();

    if (bestIndiv[currVer] == null) {
      bestIndiv[currVer] = best.copy();
    }
    else {
      if (best.compareToIndividual(bestIndiv[currVer])) {
        bestIndiv[currVer] = best.copy();
      }
    }
  }

  /**
   *  Executes a number of iterations of GA.
   */
  public void run() {
    Classifier[] offsprings;

    PopulationWrapper.doEvaluation(population);

    int numIterations = Parameters.numIterations;

    for (int iteration = 0; iteration < numIterations; iteration++) {
      boolean lastIteration = (iteration == numIterations - 1);
      Parameters.percentageOfLearning = (double) iteration
          / (double) numIterations;
      boolean res1 = PopulationWrapper.initIteration();
      boolean res2 = Timers.runTimers(iteration, population);
      if (res1 || res2) {
        PopulationWrapper.setModified(population);
      }

      // GA cycle
      population = doTournamentSelection(population);
      offsprings = doCrossover(population);
      doMutation(offsprings);
      PopulationWrapper.doEvaluation(offsprings);
      population = replacementPolicy(offsprings, lastIteration);

      Statistics.computeStatistics(population);
      Timers.runOutputTimers(iteration, population);
    }

    Statistics.statisticsToFile();
    Classifier best = PopulationWrapper.getBest(population);

    LogManager.println("\nPhenotype: ");
    best.printClassifier();
    PopulationWrapper.testClassifier(best,"training",Parameters.train2InputFile,Parameters.trainOutputFile);
    PopulationWrapper.testClassifier(best,"test",Parameters.testInputFile,Parameters.testOutputFile);
  }

  Classifier[] doCrossover(Classifier[] _population) {
    Chronometer.startChronCrossover();

    int i, j, k, countCross = 0;
    int numNiches = _population[0].getNumNiches();
    ArrayList[] parents = new ArrayList[numNiches];
    Classifier parent1, parent2;
    Classifier[] offsprings = new Classifier[2];
    Classifier[] offspringPopulation = new Classifier[Parameters.popSize];

    for (i = 0; i < numNiches; i++) {
      parents[i] = new ArrayList();
      parents[i].ensureCapacity(Parameters.popSize);
    }

    for (i = 0; i < Parameters.popSize; i++) {
      int niche = _population[i].getNiche();
      parents[niche].add(new Integer(i));
    }

    for (i = 0; i < numNiches; i++) {
      int size = parents[i].size();
      Sampling samp = new Sampling(size);
      int p1 = -1;
      for (j = 0; j < size; j++) {
        if (Rand.getReal() < Parameters.probCrossover) {
          if (p1 == -1) {
            p1 = samp.getSample();
          }
          else {
            int p2 = samp.getSample();
            int pos1 = ( (Integer) parents[i].get(p1)).intValue();
            int pos2 = ( (Integer) parents[i].get(p2)).intValue();
            parent1 = _population[pos1];
            parent2 = _population[pos2];

            offsprings = parent1.crossoverClassifiers(parent2);
            offspringPopulation[countCross++] = offsprings[0];
            offspringPopulation[countCross++] = offsprings[1];
            p1 = -1;
          }
        }
        else {
          int pos = ( (Integer) parents[i].get(samp.getSample())).intValue();
          offspringPopulation[countCross++] = _population[pos].copy();
        }
      }
      if (p1 != -1) {
        int pos = ( (Integer) parents[i].get(p1)).intValue();
        offspringPopulation[countCross++] = _population[pos].copy();
      }
    }

    Chronometer.stopChronCrossover();
    return offspringPopulation;
  }

  private int selectNicheWOR(int[] quotas) {
    int num = quotas.length;
    if (num == 1) {
      return 0;
    }

    int total = 0, i;
    for (i = 0; i < num; i++) {
      total += quotas[i];
    }
    if (total == 0) {
      return Rand.getInteger(0, num - 1);
    }
    int pos = Rand.getInteger(0, total - 1);
    total = 0;
    for (i = 0; i < num; i++) {
      total += quotas[i];
      if (pos < total) {
        quotas[i]--;
        return i;
      }
    }

    LogManager.printErr("We should not be here");
    System.exit(1);
    return -1;
  }

  private void initPool(ArrayList pool, int whichNiche,
                        Classifier[] _population) {
    if (Globals_DefaultC.nichingEnabled) {
      for (int i = 0; i < Parameters.popSize; i++) {
        if (_population[i].getNiche() == whichNiche) {
          pool.add(new Integer(i));
        }
      }
    }
    else {
      for (int i = 0; i < Parameters.popSize; i++) {
        pool.add(new Integer(i));
      }
    }
  }

  private int selectCandidateWOR(ArrayList pool, int whichNiche,
                                 Classifier[] _population) {
    if (pool.size() == 0) {
      initPool(pool, whichNiche, population);
      if (pool.size() == 0) {
        return Rand.getInteger(0, Parameters.popSize - 1);
      }
    }

    int pos = Rand.getInteger(0, pool.size() - 1);
    int elem = ( (Integer) pool.get(pos)).intValue();
    pool.remove(pos);
    return elem;
  }

  /**
   *  Does Tournament Selection without replacement.
   */
  public Classifier[] doTournamentSelection(Classifier[] _population) {
    Chronometer.startChronSelection();

    Classifier[] selectedPopulation;
    selectedPopulation = new Classifier[Parameters.popSize];
    int i, j, winner, candidate;
    int numNiches;
    if (Globals_DefaultC.nichingEnabled) {
      numNiches = _population[0].getNumNiches();
    }
    else {
      numNiches = 1;
    }

    ArrayList[] pools = new ArrayList[numNiches];
    for (i = 0; i < numNiches; i++) {
      pools[i] = new ArrayList();
    }
    int[] nicheCounters = new int[numNiches];
    int nicheQuota = Parameters.popSize / numNiches;
    for (i = 0; i < numNiches; i++) {
      nicheCounters[i] = nicheQuota;
    }

    for (i = 0; i < Parameters.popSize; i++) {
      // There can be only one
      int niche = selectNicheWOR(nicheCounters);
      winner = selectCandidateWOR(pools[niche], niche
                                  , _population);
      for (j = 1; j < Parameters.tournamentSize; j++) {
        candidate = selectCandidateWOR(pools[niche]
                                       , niche, _population);
        if (_population[candidate].compareToIndividual(_population[winner])) {
          winner = candidate;
        }
      }
      selectedPopulation[i] = _population[winner].copy();
    }
    Chronometer.stopChronSelection();
    return selectedPopulation;
  }

  public void doMutation(Classifier[] _population) {
    Chronometer.startChronMutation();
    int popSize = Parameters.popSize;
    double probMut = Parameters.probMutationInd;

    for (int i = 0; i < Parameters.popSize; i++) {
      if (Rand.getReal() < probMut) {
        _population[i].doMutation();
      }
    }

    doSpecialStages(_population);

    Chronometer.stopChronMutation();
  }

  void sortedInsert(ArrayList set, Classifier cl) {
    for (int i = 0, max = set.size(); i < max; i++) {
      if (cl.compareToIndividual( (Classifier) set.get(i))) {
        set.add(i, cl);
        return;
      }
    }
    set.add(cl);
  }

  public Classifier[] replacementPolicy(Classifier[] offspring
                                        , boolean lastIteration) {
    int i;

    Chronometer.startChronReplacement();

    if (lastIteration) {
      for (i = 0; i < numVersions; i++) {
        if (bestIndiv[i] != null) {
          PopulationWrapper.evaluateClassifier(
              bestIndiv[i]);
        }
      }
      ArrayList set = new ArrayList();
      for (i = 0; i < Parameters.popSize; i++) {
        sortedInsert(set, offspring[i]);
      }
      for (i = 0; i < numVersions; i++) {
        if (bestIndiv[i] != null) {
          sortedInsert(set, bestIndiv[i]);
        }
      }

      for (i = 0; i < Parameters.popSize; i++) {
        offspring[i] = (Classifier) set.get(i);
      }
    }
    else {
      boolean previousVerUsed = false;
      int currVer = PopulationWrapper.getCurrentVersion();
      if (bestIndiv[currVer] == null && currVer > 0) {
        previousVerUsed = true;
        currVer--;
      }

      if (bestIndiv[currVer] != null) {
        PopulationWrapper.evaluateClassifier(bestIndiv[currVer]);
        int worst = PopulationWrapper.getWorst(offspring);
        offspring[worst] = bestIndiv[currVer].copy();
      }
      if (!previousVerUsed) {
        int prevVer;
        if (currVer == 0) {
          prevVer = numVersions - 1;
        }
        else {
          prevVer = currVer - 1;
        }
        if (bestIndiv[prevVer] != null) {
          PopulationWrapper.evaluateClassifier(bestIndiv[prevVer]);
          int worst = PopulationWrapper.getWorst(offspring);
          offspring[worst] = bestIndiv[prevVer].copy();
        }
      }
    }

    Chronometer.stopChronReplacement();
    return offspring;
  }

  public void doSpecialStages(Classifier[] population) {
    int numStages = population[0].numSpecialStages();

    for (int i = 0; i < numStages; i++) {
      for (int j = 0; j < population.length; j++) {
        population[j].doSpecialStage(i);
      }
    }
  }
}

