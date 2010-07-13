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

import keel.Dataset.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;

public class ClassifierGABIL
    extends Classifier implements Cloneable {
/**
 * <p>
 * Contains the classifier for the GABIL knowledge representation
 * </p>
 */
	
  // The cromosome
  int[] crm;
  int defaultClass;

  public ClassifierGABIL() {
    isEvaluated = false;
  }

  public void initRandomClassifier() {
    numRules = Parameters.initialNumberOfRules;
    int ruleSize = Globals_GABIL.ruleSize;
    double prob = Parameters.probOne;
    int nC = Parameters.numClasses;
    crm = new int[numRules * ruleSize];
    int base = 0;

    if (Globals_DefaultC.defaultClassPolicy == Globals_DefaultC.AUTO) {
      defaultClass = Rand.getInteger(0, Parameters.numClasses - 1);
    }
    else {
      defaultClass = Globals_DefaultC.defaultClass;
    }

    for (int i = 0; i < numRules; i++) {
      InstanceWrapper ins = null;
      if (PopulationWrapper.smartInit) {
        if (Globals_DefaultC.defaultClassPolicy != Globals_DefaultC.DISABLED) {
          ins = PopulationWrapper.getInstanceInit(defaultClass);
        }
        else {
          ins = PopulationWrapper.getInstanceInit(Parameters.numClasses);
        }
      }

      int base2 = base;
      for (int j = 0; j < Parameters.numAttributes; j++) {
        int value;
        if (ins != null) {
          value = ins.getNominalValue(j);
        }
        else {
          value = -1;
        }
        for (int k = 0; k < Globals_GABIL.size[j]; k++) {
          if (k != value) {
            if (Rand.getReal() < prob) {
              crm[base2 + k] = 1;
            }
            else {
              crm[base2 + k] = 0;
            }
          }
          else {
            crm[base2 + k] = 1;
          }
        }
        base2 += Globals_GABIL.size[j];
      }

      if (ins != null) {
        crm[base2] = ins.classOfInstance();
      }
      else {
        do {
          crm[base2] = Rand.getInteger(0, nC - 1);
        }
        while (Globals_DefaultC.enabled &&
               crm[base2] == defaultClass);
      }

      base += ruleSize;
    }

    resetPerformance();
  }

  public double computeTheoryLength() {
    int base = 0;
    int ruleSize = Globals_GABIL.ruleSize;
    theoryLength = 0;
    for (int i = 0; i < numRules; i++) {
      if (PerformanceAgent.getActivationsOfRule(i) > 0) {
        int base2 = base;
        for (int j = 0; j < Parameters.numAttributes; j++) {
          double countFalses = 0;
          int numValues = Globals_GABIL.size[j];
          for (int k = 0; k < numValues; k++) {
            if (crm[base2 + k] == 0) {
              countFalses++;
            }
          }
          theoryLength += numValues + countFalses;
          base2 += Globals_GABIL.size[j];
        }
      }
      base += ruleSize;
    }

    if (Globals_DefaultC.enabled) {
      theoryLength += 0.00000001;
    }
    return theoryLength;
  }

  /**
   * This function classifies input instances. It returns a class
   * prediction of -1 if the input example cannot be classified
   */
  public int doMatch(InstanceWrapper ins) {
    int nA = Parameters.numAttributes;
    boolean okMatch;
    int i, j;
    int base = 0;
    int ruleSize = Globals_GABIL.ruleSize;

    int[] val = ins.getNominalValues();

    for (i = 0; i < numRules; i++) {
      okMatch = true;

      for (j = 0; okMatch && j < nA; j++) {
        if (crm[base + Globals_GABIL.offset[j] + val[j]] == 0) {
          okMatch = false;
        }
      }

      if (okMatch) {
        positionRuleMatch = i;
        return crm[base + ruleSize - 1];
      }
      base += ruleSize;
    }
    if (Globals_DefaultC.enabled) {
      positionRuleMatch = numRules;
      return defaultClass;
    }
    return -1;
  }

  public void printClassifier() {
    int nA = Parameters.numAttributes;
    int ruleSize = Globals_GABIL.ruleSize;
    String str;
    int base = 0;

    for (int i = 0; i < numRules; i++) {
      str = i + ":";
      for (int j = 0; j < nA; j++) {
        Attribute att = Attributes.getAttribute(j);
        String temp = "Att " + att.getName() + " is ";
        boolean irr = true;
        boolean first = true;
        for (int k = 0; k < Globals_GABIL.size[j]; k++) {
          if (crm[base + Globals_GABIL.offset[j] + k] == 1) {
            if (first) {
              temp += att.getNominalValue(k);
              first = false;
            }
            else {
              temp += "," + att.getNominalValue(k);
            }
          }
          else {
            irr = false;
          }
        }
        if (!irr) {
          str += temp + "|";
        }
      }
      int cl = crm[base + ruleSize - 1];
      String name = Attributes.getAttribute(Parameters.numAttributes).
          getNominalValue(cl);
      str += name;
      LogManager.println(str);
      base += ruleSize;
    }
    if (Globals_DefaultC.enabled) {
      LogManager.println(numRules + ":Default rule -> "
                         +
                         Attributes.getAttribute(Parameters.numAttributes).
                         getNominalValue(defaultClass));
    }
  }

  public int getNumRules() {
    if (Globals_DefaultC.enabled) {
      return numRules + 1;
    }
    return numRules;
  }

  public Classifier[] crossoverClassifiers(Classifier _parent2) {
    ClassifierGABIL offspring1 = new ClassifierGABIL();
    ClassifierGABIL offspring2 = new ClassifierGABIL();
    ClassifierGABIL parent2 = (ClassifierGABIL) _parent2;

    int ruleSize = Globals_GABIL.ruleSize;
    int ruleP1 = (int) Rand.getInteger(0, numRules - 1);
    int ruleP2 = (int) Rand.getInteger(0, parent2.numRules - 1);
    offspring1.numRules = ruleP1 + parent2.numRules - ruleP2;
    offspring2.numRules = ruleP2 + numRules - ruleP1;
    int cutPoint = (int) Rand.getInteger(0, Globals_GABIL.ruleSize);
    offspring1.defaultClass = offspring2.defaultClass = defaultClass;

    offspring1.crm = new int[ruleSize * offspring1.numRules];
    offspring2.crm = new int[ruleSize * offspring2.numRules];

    System.arraycopy(crm, 0, offspring1.crm, 0, ruleP1 * ruleSize);
    System.arraycopy(parent2.crm, 0, offspring2.crm, 0, ruleP2 * ruleSize);

    int base1 = ruleP1 * ruleSize;
    int base2 = ruleP2 * ruleSize;

    System.arraycopy(crm, base1, offspring1.crm, base1, cutPoint);
    System.arraycopy(parent2.crm, base2, offspring2.crm, base2, cutPoint);
    System.arraycopy(crm, base1 + cutPoint, offspring2.crm, base2 + cutPoint,
                     ruleSize - cutPoint);
    System.arraycopy(parent2.crm, base2 + cutPoint, offspring1.crm,
                     base1 + cutPoint, ruleSize - cutPoint);

    base1 += ruleSize;
    base2 += ruleSize;
    System.arraycopy(crm, base1, offspring2.crm, base2,
                     (numRules - ruleP1 - 1) * ruleSize);
    System.arraycopy(parent2.crm, base2, offspring1.crm, base1,
                     (parent2.numRules - ruleP2 - 1) * ruleSize);

    Classifier[] ret = new Classifier[2];
    ret[0] = offspring1;
    ret[1] = offspring2;

    return ret;
  }

  public Classifier copy() {
    int ruleSize = Globals_GABIL.ruleSize;
    ClassifierGABIL ret = new ClassifierGABIL();

    ret.numRules = numRules;
    ret.theoryLength = theoryLength;
    ret.exceptionsLength = ret.exceptionsLength;
    ret.crm = new int[numRules * ruleSize];
    System.arraycopy(crm, 0, ret.crm, 0, numRules * ruleSize);
    ret.defaultClass = defaultClass;

    ret.setAccuracy(accuracy);
    ret.setFitness(fitness);
    ret.isEvaluated = isEvaluated;
    ret.setNumAliveRules(numAliveRules);
    return ret;
  }

  public void doMutation() {
    int whichRule = (int) Rand.getInteger(0, numRules - 1);
    int ruleSize = Globals_GABIL.ruleSize;
    int base = whichRule * ruleSize;
    int gene;

    if (Globals_DefaultC.numClasses > 1
        && Rand.getReal() < 0.1) {
      gene = ruleSize - 1;
    }
    else {
      gene = (int) Rand.getInteger(0, ruleSize - 2);
    }

    if (gene < ruleSize - 1) {
      if (crm[base + gene] == 1) {
        crm[base + gene] = 0;
      }
      else {
        crm[base + gene] = 1;
      }
    }
    else {
      int oldValue = crm[base + gene];
      int newValue;
      do {
        newValue = (int) Rand.getInteger(0, Parameters.numClasses - 1);
      }
      while (newValue == oldValue ||
             (Globals_DefaultC.enabled && newValue == defaultClass));
      crm[base + gene] = newValue;
    }

    isEvaluated = false;
  }

  public void deleteRules(int[] whichRules) {
    if (numRules == 1 || whichRules.length == 0) {
      return;
    }

    int ruleSize = Globals_GABIL.ruleSize;
    int rulesToDelete = whichRules.length;
    if (whichRules[rulesToDelete - 1] == numRules) {
      rulesToDelete--;
    }

    int[] newCrm = new int[ruleSize * (numRules - rulesToDelete)];
    int countPruned = 0;
    int baseOrig = 0;
    int baseNew = 0;

    for (int i = 0; i < numRules; i++) {
      if (countPruned < rulesToDelete) {
        if (i != whichRules[countPruned]) {
          System.arraycopy(crm, baseOrig, newCrm, baseNew, ruleSize);
          baseNew += ruleSize;
        }
        else {
          countPruned++;
        }
      }
      else {
        System.arraycopy(crm, baseOrig, newCrm, baseNew, ruleSize);
        baseNew += ruleSize;
      }
      baseOrig += ruleSize;
    }
    numRules -= rulesToDelete;
    crm = newCrm;
  }

  public double getLength() {
    return numAliveRules;
  }

  public int numSpecialStages() {
    return 0;
  }

  public void doSpecialStage(int stage) {}

  public int getNiche() {
    if (Globals_DefaultC.defaultClassPolicy != Globals_DefaultC.AUTO) {
      return 0;
    }
    return defaultClass;
  }

  public int getNumNiches() {
    if (Globals_DefaultC.defaultClassPolicy != Globals_DefaultC.AUTO) {
      return 1;
    }
    return Parameters.numClasses;
  }

}

