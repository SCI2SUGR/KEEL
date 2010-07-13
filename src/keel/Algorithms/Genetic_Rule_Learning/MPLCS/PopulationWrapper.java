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
import keel.Algorithms.Genetic_Rule_Learning.MPLCS.Assistant.Globals.*;


public class PopulationWrapper {
/**
 * <p>
 * Contains methods that manipulate the population in various
 * ways: classifying the training set for the fitness computations, checking
 * if there are improved solutions in the population and performing the
 * test stage (generating the output files)
 * </p>
 */
	
  static public InstanceSet is;
  static public Windowing ilas;
  static public InstanceWrapper[] allInstances;

  static public int[][] instancesByClass;
  static public Sampling[] samplesOfClasses;
  static public boolean smartInit;
  static public boolean cwInit;

  public static int getCurrentVersion() {
    return ilas.getCurrentVersion();
  }

  public static int numVersions() {
    return ilas.numVersions();
  }

  public static void initInstancesEvaluation() {
    is = new InstanceSet();
    try {
      is.readSet(Parameters.trainInputFile, true);
    }
    catch (Exception e) {
      LogManager.printErr(e.toString());
      System.exit(1);
    }
    checkDataset();
    replaceMissing(is);
    if (Parameters.adiKR) {
      DiscretizationManager.init();
    }
    allInstances = createWrapperInstances(is);
    ilas = new Windowing(allInstances);

    if (Parameters.initMethod != null) {
      if (Parameters.initMethod.equalsIgnoreCase("smart")) {
        smartInit = true;
        cwInit = false;
      }
      else if (Parameters.initMethod.equalsIgnoreCase("cwInit")) {
        smartInit = true;
        cwInit = true;
      }
      else {
        smartInit = false;
        cwInit = false;
      }
    }
    else {
      smartInit = false;
      cwInit = false;
    }

    if (smartInit) {
      int nc = Parameters.numClasses;
      int classCounts[] = new int[nc];
      instancesByClass = new int[nc][];
      samplesOfClasses = new Sampling[nc];


      for (int i = 0; i < nc; i++) {
        int num = numInstancesOfClass(i);
        instancesByClass[i] = new int[num];
        samplesOfClasses[i] = new Sampling(num);
        classCounts[i] = 0;
      }
      for (int i = 0; i < allInstances.length; i++) {
        int cl = allInstances[i].classOfInstance();
        instancesByClass[cl][classCounts[cl]++] = i;
      }
    }
  }

  public static InstanceWrapper getInstanceInit(int forbiddenCL) {
    if (cwInit) {
      int cl;
      do {
        cl = Rand.getInteger(0, Parameters.numClasses - 1);
      }
      while (cl == forbiddenCL
             || instancesByClass[cl].length == 0);
      int pos = samplesOfClasses[cl].getSample();
      return allInstances[instancesByClass[cl][pos]];
    }

    int count[] = new int[Parameters.numClasses];
    int total = 0;
    for (int i = 0; i < count.length; i++) {
      if (i == forbiddenCL) {
        count[i] = 0;
      }
      else {
        count[i] = samplesOfClasses[i].numSamplesLeft();
        total += count[i];
      }
    }

    int pos = Rand.getInteger(0, total - 1);
    int acum = 0;
    for (int i = 0; i < count.length; i++) {
      acum += count[i];
      if (pos < acum) {
        int inst = samplesOfClasses[i].getSample();
        return allInstances[instancesByClass[i][inst]];
      }
    }

    LogManager.printErr("We should not be here !!!");
    System.exit(1);
    return null;
  }

  public static InstanceWrapper[] createWrapperInstances(InstanceSet is) {
    InstanceWrapper[] iw
        = new InstanceWrapper[is.getNumInstances()];
    for (int i = 0; i < iw.length; i++) {
      iw[i] = new InstanceWrapper(is.getInstance(i));
    }
    return iw;
  }

  public static boolean initIteration() {
    return ilas.newIteration();
  }

  public static void evaluateClassifier(Classifier ind) {
    int predicted, real;
    InstanceWrapper[] instances = ilas.getInstances();

    ind.resetPerformance();
    PerformanceAgent.resetPerformance(ind.getNumRules());
    for (int i = 0; i < instances.length; i++) {
      real = instances[i].classOfInstance();
      predicted = ind.doMatch(instances[i]);
      PerformanceAgent.addPrediction(predicted, real
                                     , ind.getPositionRuleMatch());
    }

    ind.computePerformance();

    if (Parameters.doRuleDeletion) {
      ind.deleteRules(PerformanceAgent.controlBloatRuleDeletion());
    }
  }

  public static void doEvaluation(Classifier[] _population) {
    Chronometer.startChronEvaluation();
    int popSize = _population.length;

    for (int i = 0; i < popSize; i++) {
      if (!_population[i].getIsEvaluated()) {
        evaluateClassifier(_population[i]);
      }
    }

    Chronometer.stopChronEvaluation();
  }

  /**
   *  Obtains the best classifier of population.
   *  @param _population The population
   *  @return Best classifier of population.
   */
  public static Classifier getBest(Classifier[] _population) {
    int sizePop = _population.length;
    int posWinner = 0;

    for (int i = 1; i < sizePop; i++) {
      if (_population[i].compareToIndividual(_population[posWinner])) {
        posWinner = i;
      }
    }
    return _population[posWinner];
  }

  /**
   *  Obtains the worst classifier of population.
   *  @param _population The population
   *  @return Worst classifier of population.
   */
  public static int getWorst(Classifier[] _population) {
    int sizePop = _population.length;
    int posWorst = 0;

    for (int i = 1; i < sizePop; i++) {
      if (!_population[i].compareToIndividual(_population[posWorst])) {
        posWorst = i;
      }
    }
    return posWorst;
  }

  public static void setModified(Classifier[] _population) {
    int sizePop = _population.length;

    for (int i = 0; i < sizePop; i++) {
      _population[i].setIsEvaluated(false);
    }
  }

  public static void testClassifier(Classifier ind, String typeOfTest,
                                    String testInputFile, String testOutputFile) {
    InstanceSet testSet = new InstanceSet();
    try {
            testSet.readSet(testInputFile,false);
    } catch(Exception e) {
            LogManager.printErr(e.toString());
            System.exit(1);
    }
    replaceMissing(testSet);
    FileManagement fm = new FileManagement();
    InstanceWrapper []instances=createWrapperInstances(testSet);
    double numInstances=instances.length;
    int real, predicted;
    PerformanceAgent.resetPerformanceTest(ind.getNumRules());
    ind.resetPerformance();
    //Attribute att = Attributes.getAttribute(Parameters.numAttributes);
    Attribute att = Attributes.getOutputAttribute(0);

    try {
            fm.initWrite(testOutputFile);
            fm.writeLine(testSet.getHeader());

            for(int i=0;i<numInstances;i++) {
                    real=instances[i].classOfInstance();
                    predicted=ind.doMatch(instances[i]);
                    String pred;
                    if(predicted==-1) {
                            pred="unclassified";
                    } else {
                            pred=att.getNominalValue(predicted);
                    }
                    fm.writeLine(att.getNominalValue(real)+" "+pred+"\n");
                    PerformanceAgent.addPredictionTest(predicted,real
                            ,ind.getPositionRuleMatch());
            }
            fm.closeWrite();
    } catch(Exception e) {
            System.err.println("Error handling output test file "+testOutputFile);
            System.exit(1);
    }

    LogManager.println("\nStatistics on "+typeOfTest+" file");
    PerformanceAgent.dumpStats(typeOfTest);
    LogManager.println("");
  }

  static void checkDataset() {
    Attribute[] outputs = Attributes.getOutputAttributes();
    if (outputs.length != 1) {
      LogManager.printErr("Only datasets with one output are supported");
      System.exit(1);
    }
    if (outputs[0].getType() != Attribute.NOMINAL) {
      LogManager.printErr("Output attribute should be nominal");
      System.exit(1);
    }
    Parameters.numClasses = outputs[0].getNumNominalValues();
    Parameters.numAttributes = Attributes.getInputAttributes().length;
  }

  static void replaceMissing(InstanceSet is) {
    String[][] mostFreq = new String[Parameters.numAttributes][Parameters.
        numClasses];
    double[][] means = new double[Parameters.numAttributes][Parameters.
        numClasses];
    int[] types = new int[Parameters.numAttributes];
    Attribute[] inputs = Attributes.getInputAttributes();

    for (int i = 0; i < Parameters.numAttributes; i++) {
      types[i] = inputs[i].getType();
      if (types[i] == Attribute.NOMINAL) {
        for (int j = 0; j < Parameters.numClasses; j++) {
          mostFreq[i][j] = inputs[i].getMostFrequentValue(j);
        }
      }
      else {
        for (int j = 0; j < Parameters.numClasses; j++) {
          means[i][j] = inputs[i].getMeanValue(j);
        }
      }
    }

    Instance[] inst = is.getInstances();
    for (int i = 0; i < inst.length; i++) {
      if (inst[i].existsAnyMissingValue()) {
        int cl = inst[i].getOutputNominalValuesInt(0);
        boolean[] miss = inst[i].getInputMissingValues();
        for (int j = 0; j < Parameters.numAttributes; j++) {
          if (miss[j]) {
            if (types[j] == Attribute.NOMINAL) {
              inst[i].setInputNominalValue(j, mostFreq[j][cl]);
            }
            else {
              inst[i].setInputNumericValue(j, means[j][cl]);
            }
          }
        }
      }
    }
  }

  static int numInstancesOfClass(int clas){
    int count = 0;
    Instance [] inst = is.getInstances();
    for (int i = 0; i < inst.length; i++){
      if (inst[i].getOutputNominalValuesInt(0) == clas){
        count++;
      }
    }
    return count;
  }

}

