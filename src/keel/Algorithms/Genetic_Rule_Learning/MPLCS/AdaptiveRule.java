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

public class AdaptiveRule {
  public static void constructor(int[] crm, int base, int defaultClass) {
    InstanceWrapper ins = null;
    if (PopulationWrapper.smartInit) {
      if (Globals_DefaultC.defaultClassPolicy != Globals_DefaultC.DISABLED) {
        ins = PopulationWrapper.getInstanceInit(defaultClass);
      }
      else {
        ins = PopulationWrapper.getInstanceInit(Parameters.numClasses);
      }
    }

    int base2 = base + 2;
    crm[base] = 1;
    for (int i = 0; i < Parameters.numAttributes; i++) {
      AdaptiveAttribute.constructor(crm, base2, i, ins);
      crm[base] += crm[base2];
      base2 += Globals_ADI.size[i];
    }

    if (ins != null) {
      crm[base + 1] = ins.classOfInstance();
    }
    else {
      do {
        crm[base + 1] = Rand.getInteger(0, Parameters.numClasses - 1);
      }
      while (Globals_DefaultC.enabled && crm[base + 1] == defaultClass);
    }
  }

  public static double computeTheoryLength(int[] crm, int base) {
    int base2 = base + 2;
    double length = 0;
    for (int i = 0; i < Parameters.numAttributes; i++) {
      if (Globals_ADI.types[i] == Attribute.REAL) {
        double intervalCount = 0;
        int previousValue = crm[base2 + 3];
        int numInt = crm[base2];
        if (previousValue == 1) {
          intervalCount++;
        }
        for (int j = base2 + 5, k = 1; k < numInt; k++, j += 2) {
          if (crm[j] != previousValue) {
            intervalCount++;
          }
          previousValue = crm[j];
        }
        if (previousValue == 0) {
          intervalCount--;
        }
        length += numInt + intervalCount;
      }
      else {
        double countFalses = 0;
        int numValues = Globals_ADI.size[i];
        for (int j = 2, pos = base2 + j; j < numValues; j++, pos++) {
          if (crm[pos] == 0) {
            countFalses++;
          }
        }
        length += numValues - 2.0 + countFalses;
      }
      base2 += Globals_ADI.size[i];
    }
    return length;
  }

  public static boolean doMatch(int[] crm, int base, InstanceWrapper ins) {
    int base2 = base + 2;
    int[][] discValues = ins.getDiscretizedValues();
    int[] nominalValues = ins.getNominalValues();
    for (int i = 0; i < Parameters.numAttributes; i++) {
      if (Globals_ADI.types[i] == Attribute.REAL) {
        int value = discValues[i][crm[base2 + 1]];
        if (!AdaptiveAttribute.doMatchReal(crm, base2, value)) {
          return false;
        }
      }
      else {
        int value = nominalValues[i];
        if (!AdaptiveAttribute.doMatchNominal(crm, base2, value)) {
          return false;
        }
      }
      base2 += Globals_ADI.size[i];
    }
    return true;
  }

  public static String dumpPhenotype(int[] crm, int base) {
    int base2 = base + 2;
    String str = "";
    for (int i = 0; i < Parameters.numAttributes; i++) {
      String temp
          = AdaptiveAttribute.dumpPhenotype(crm, base2, i);
      if (temp.length() > 0) {
    	Main.numAttsBest++; 
        str += temp + "|";
      }
      base2 += Globals_ADI.size[i];
    }
    int cl = crm[base + 1];
    String name = Attributes.getAttribute(Parameters.numAttributes).
        getNominalValue(cl);
    str += name;
    return str;
  }

  public static void crossover(int[] p1, int[] p2, int[] s1, int[] s2
                               , int base1, int base2, int cutPoint) {
    int baseP1 = base1 + 2;
    int baseP2 = base2 + 2;

    s1[base1] = 1;
    s2[base2] = 1;

    for (int i = 0; i < cutPoint && i < Parameters.numAttributes; i++) {
      int inc = Globals_ADI.size[i];
      System.arraycopy(p1, baseP1, s1, baseP1, inc);
      System.arraycopy(p2, baseP2, s2, baseP2, inc);
      s1[base1] += p1[baseP1];
      s2[base2] += p2[baseP2];
      baseP1 += inc;
      baseP2 += inc;
    }
    for (int i = cutPoint; i < Parameters.numAttributes; i++) {
      int inc = Globals_ADI.size[i];
      System.arraycopy(p1, baseP1, s2, baseP2, inc);
      System.arraycopy(p2, baseP2, s1, baseP1, inc);
      s1[base1] += p2[baseP2];
      s2[base2] += p1[baseP1];
      baseP1 += inc;
      baseP2 += inc;
    }

    if (cutPoint == Parameters.numAttributes) {
      s1[base1 + 1] = p1[base1 + 1];
      s2[base2 + 1] = p2[base2 + 1];
    }
    else {
      s1[base1 + 1] = p2[base2 + 1];
      s2[base2 + 1] = p1[base1 + 1];
    }

  }

  public static void mutation(int[] crm, int base, int defaultClass) {
    if (Globals_DefaultC.numClasses > 1 && Rand.getReal() < 0.10) {
      int newClass;
      int oldClass = crm[base + 1];
      do {
        newClass = Rand.getInteger(0, Parameters.numClasses - 1);
      }
      while (newClass == oldClass || (Globals_DefaultC.enabled
                                      && newClass == defaultClass));
      crm[base + 1] = newClass;
    }
    else {
      int attribute = Rand.getInteger(0, Parameters.numAttributes - 1);
      int base2 = base + 2 + Globals_ADI.offset[attribute];
      AdaptiveAttribute.mutation(crm, base2, attribute);
    }
  }

  public static boolean doSplit(int[] crm, int base) {
    int base2 = base + 2;
    boolean modif = false;

    for (int i = 0; i < Parameters.numAttributes; i++) {
      if (Rand.getReal() < Parameters.probSplit) {
        modif = true;
        int pos = Rand.getInteger(0, crm[base2] - 1);
        crm[base] += AdaptiveAttribute.doSplit(crm, base2, i, pos);
      }
      base2 += Globals_ADI.size[i];
    }
    return modif;
  }

  public static boolean doMerge(int[] crm, int base) {
    int base2 = base + 2;
    boolean modif = false;

    for (int i = 0; i < Parameters.numAttributes; i++) {
      if (Rand.getReal() < Parameters.probMerge) {
        modif = true;
        int pos = Rand.getInteger(0, crm[base2] - 1);
        crm[base] += AdaptiveAttribute.doMerge(crm, base2, i, pos);
      }
      base2 += Globals_ADI.size[i];
    }
    return modif;
  }

  public static boolean doReinitialize(int[] crm, int base) {
    int base2 = base + 2;
    boolean modif = false;

    if (Parameters.probReinitialize == 0) {
      return modif;
    }

    for (int i = 0; i < Parameters.numAttributes; i++) {
      if (Rand.getReal() < Parameters.probReinitialize) {
        modif = true;
        crm[base] -= crm[base2];
        AdaptiveAttribute.doReinitialize(crm, base2, i);
        crm[base] += crm[base2];
      }
      base2 += Globals_ADI.size[i];
    }
    return modif;
  }

}

