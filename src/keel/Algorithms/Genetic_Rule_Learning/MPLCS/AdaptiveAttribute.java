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

import keel.Algorithms.Genetic_Rule_Learning.MPLCS.Assistant.Discretizers.Basic.*;
import keel.Dataset.*;
import keel.Algorithms.Genetic_Rule_Learning.MPLCS.Assistant.Globals.*;

public class AdaptiveAttribute {
  public static void constructor(int[] crm, int base, int attribute,
                                 InstanceWrapper ins) {
    if (Globals_ADI.types[attribute] == Attribute.REAL) {
      int disc = Rand.getInteger(0
                                 ,
                                 DiscretizationManager.getNumDiscretizers() - 1);
      int numMicroInt = DiscretizationManager.getDiscretizer(disc).
          getNumIntervals(attribute);

      int intervalOfSample;
      boolean done = false;
      if (ins != null) {
        intervalOfSample = ins.getDiscretizedValue(attribute, disc);
      }
      else {
        intervalOfSample = -1;
        done = true;
      }

      int numIntervals;
      int maxAllowedIntervals = Math.min(numMicroInt
                                         , Parameters.maxIntervals);
      if (maxAllowedIntervals > 2) {
        numIntervals = Rand.getInteger(2, maxAllowedIntervals);
      }
      else {
        numIntervals = maxAllowedIntervals;
      }

      crm[base] = numIntervals;
      crm[base + 1] = disc;

      int i, pos, max;
      int sum = 0;
      for (i = 0, pos = base + 2, max = numIntervals - 1; i < max; i++,
           pos += 2) {
        int microInt = Rand.getInteger(1
                                       , numMicroInt - (numIntervals - i - 1));
        sum += microInt;
        crm[pos] = sum;
        numMicroInt -= microInt;

        if (!done && intervalOfSample < sum) {
          done = true;
          crm[pos + 1] = 1;
        }
        else {
          if (Rand.getReal() < Parameters.probOne) {
            crm[pos + 1] = 1;
          }
          else {
            crm[pos + 1] = 0;
          }
        }
      }

      sum += numMicroInt;
      crm[pos] = sum;

      if (!done && intervalOfSample < sum) {
        crm[pos + 1] = 1;
      }
      else {
        if (Rand.getReal() < Parameters.probOne) {
          crm[pos + 1] = 1;
        }
        else {
          crm[pos + 1] = 0;
        }
      }
    }
    else {
      int valueIns;
      if (ins != null) {
        valueIns = ins.getNominalValue(attribute);
      }
      else {
        valueIns = -1;
      }

      crm[base] = crm[base + 1] = Globals_ADI.size[attribute] - 2;
      for (int i = 0, pos = base + 2, max = crm[base]; i < max; i++, pos++) {
        if (i != valueIns) {
          if (Rand.getReal() < Parameters.probOne) {
            crm[pos] = 1;
          }
          else {
            crm[pos] = 0;
          }
        }
        else {
          crm[pos] = 1;
        }
      }
    }
  }

  public static boolean doMatchReal(int[] crm, int base, int value) {
    int base2 = base + 2;
    for (int i = 0, max = crm[base]; i < max; i++) {
      if (value < crm[base2]) {
        return (crm[base2 + 1] == 1);
      }
      base2 += 2;
    }
    LogManager.printErr("Integrity error at AdaptiveAttribute.doMatchReal. " +
                        crm[base] + " " + crm[base + 1] + " " + value);
    System.exit(1);
    return false;
  }

  public static boolean doMatchNominal(int[] crm, int base, int value) {
    return (crm[base + 2 + value] == 1);
  }

  public static String dumpPhenotype(int[] crm, int base, int attribute) {
    String str = "";
    Attribute att = Attributes.getAttribute(attribute);
    String temp = "Att " + att.getName() + " is ";
    if (Globals_ADI.types[attribute] == Attribute.REAL) {
      double min = att.getMinAttribute();
      double max = att.getMaxAttribute();
      Discretizer d = DiscretizationManager.getDiscretizer(crm[base + 1]);

      if (crm[base] > 1) {
        int intervalCount = 0;
        int index = 0;
        int oldMicroInt = 0;
        while (index < crm[base]) {
          intervalCount++;
          int microInt = 0;
          int valueAct = crm[base + 3 + index * 2];
          while (index < crm[base] && crm[base + index * 2 + 3] == valueAct) {
            microInt = crm[base + 2 + index * 2];
            index++;
          }
          if (valueAct == 1) {
            if (oldMicroInt == 0) {
              if (index < crm[base]) {
                double maxInt = d.getCutPoint(attribute, microInt - 1);
                temp += "[<" + maxInt + "]";
              }
            }
            else {
              double minInt = d.getCutPoint(attribute, oldMicroInt - 1);
              if (index == crm[base]) {
                temp += "[>" + minInt + "]";
              }
              else {
                double maxInt = d.getCutPoint(attribute, microInt - 1);
                temp += "[" + minInt + "," + maxInt + "]";
              }
            }
          }
          oldMicroInt = microInt;
        }

        if (intervalCount > 1) {
          str += temp;
        }
      }
    }
    else {
      boolean irr = true;
      boolean first = true;
      for (int i = 0; i < crm[base]; i++) {
        if (crm[base + i + 2] == 1) {
          if (first) {
            temp += att.getNominalValue(i);
            first = false;
          }
          else {
            temp += "," + att.getNominalValue(i);
          }
        }
        else {
          irr = false;
        }
      }

      if (!irr) {
        temp.replaceAll(",$", "");
        str += temp;
      }
    }
    return str;
  }

  public static void mutation(int[] crm, int base, int attribute) {
    int value = Rand.getInteger(0, crm[base] - 1);
    int pos;
    if (Globals_ADI.types[attribute] == Attribute.REAL) {
      pos = base + value * 2 + 3;
    }
    else {
      pos = base + value + 2;
    }

    if (crm[pos] == 1) {
      crm[pos] = 0;
    }
    else {
      crm[pos] = 1;
    }
  }

  public static int doSplit(int[] crm, int base, int attribute, int interval) {
    if (Globals_ADI.types[attribute] != Attribute.REAL) {
      return 0;
    }

    int intervalPos = base + interval * 2 + 2;
    int size;
    if (interval == 0) {
      size = crm[intervalPos];
    }
    else {
      size = crm[intervalPos] - crm[intervalPos - 2];
    }
    if (size < 2 || crm[base] == Parameters.maxIntervals) {
      return 0;
    }

    int cutPoint = Rand.getInteger(0, size - 2);
    int size1 = cutPoint + 1;
    int size2 = size - size1;

    int pos = base + (crm[base] - 1) * 2 + 2;
    for (int i = crm[base] - 1; i >= interval; i--, pos -= 2) {
      crm[pos + 2] = crm[pos];
      crm[pos + 3] = crm[pos + 1];
    }
    crm[intervalPos] -= size2;

    crm[base]++;
    return +1;
  }

  public static int doMerge(int[] crm, int base, int attribute, int interval) {
    if (Globals_ADI.types[attribute] != Attribute.REAL) {
      return 0;
    }
    if (crm[base] == 1) {
      return 0;
    }

    int neighbour;
    if (interval == 0) {
      neighbour = 1;
    }
    else if (interval == crm[base] - 1) {
      neighbour = crm[base] - 2;
    }
    else {
      if (Rand.getReal() < 0.5) {
        neighbour = interval + 1;
      }
      else {
        neighbour = interval - 1;
      }
    }

    int pos1, pos2, nextInterval;
    if (neighbour < interval) {
      pos1 = base + neighbour * 2 + 2;
      nextInterval = interval + 1;
    }
    else {
      pos1 = base + interval * 2 + 2;
      nextInterval = neighbour + 1;
    }

    pos2 = pos1 + 2;
    int size1, size2;
    if (pos1 == base + 2) {
      size1 = crm[pos1];
    }
    else {
      size1 = crm[pos1] - crm[pos1 - 2];
    }
    size2 = crm[pos2] - crm[pos1];

    int value;
    if (size1 > size2) {
      value = crm[pos1 + 1];
    }
    else if (size2 > size1) {
      value = crm[pos2 + 1];
    }
    else {
      if (Rand.getReal() < 0.5) {
        value = crm[pos1 + 1];
      }
      else {
        value = crm[pos2 + 1];
      }
    }

    crm[pos1] = crm[pos2];
    crm[pos1 + 1] = value;

    for (int i = nextInterval, max = crm[base]; i < max; i++, pos2 += 2) {
      crm[pos2] = crm[pos2 + 2];
      crm[pos2 + 1] = crm[pos2 + 3];
    }

    crm[base]--;
    return -1;
  }

  public static void doReinitialize(int[] crm, int base, int attribute) {
    if (Globals_ADI.types[attribute] != Attribute.REAL) {
      return;
    }
    constructor(crm, base, attribute, null);
  }
}

