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
 * @author Modified by Jose A. Saez Munoz (ETSIIT, Universidad de Granada - Granada) 10/09/10
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Genetic_Rule_Learning.MPLCS;
import java.util.*;
import keel.Algorithms.Genetic_Rule_Learning.MPLCS.Assistant.Globals.*;



public class Windowing {
/**
 * <p>
 * Manages the subset of training instances
 * that is used at each iteration to perform the fitness computations
 * </p>
 */
	
  InstanceWrapper[] is;
  InstanceWrapper[][] strata;
  int numStrata;
  int currentIteration;
  boolean lastIteration;
  int[] numInstancesStrata;

  public Windowing(InstanceWrapper[] _is) {
    is = _is;
    numStrata = Parameters.numStrata;
    strata = new InstanceWrapper[numStrata][];
    numInstancesStrata = new int[numStrata];
    currentIteration = 0;
    lastIteration = false;

    createStrata();
  }

  private void createStrata() {
    Vector[] tempStrata = new Vector[numStrata];
    Vector[] instancesOfClass = new Vector[Parameters.numClasses];

    for (int i = 0; i < numStrata; i++) {
      tempStrata[i] = new Vector();
    }
    for (int i = 0; i < Parameters.numClasses; i++) {
      instancesOfClass[i] = new Vector();
    }

    int numInstances = is.length;
    for (int i = 0; i < numInstances; i++) {
      int cl = is[i].classOfInstance();
      instancesOfClass[cl].addElement(is[i]);
    }

    for (int i = 0; i < Parameters.numClasses; i++) {
      int stratum = 0;
      int count = instancesOfClass[i].size();
      while (count >= numStrata) {
        int pos = Rand.getInteger(0, count - 1);
        tempStrata[stratum].addElement(instancesOfClass[i].elementAt(pos));
        instancesOfClass[i].removeElementAt(pos);
        stratum = (stratum + 1) % numStrata;
        count--;
      }
      while (count > 0) {
        stratum = Rand.getInteger(0, numStrata - 1);
        tempStrata[stratum].addElement(instancesOfClass[i].elementAt(0));
        instancesOfClass[i].removeElementAt(0);
        count--;
      }
    }

    for (int i = 0; i < numStrata; i++) {
      int num = tempStrata[i].size();
      strata[i] = new InstanceWrapper[num];
      numInstancesStrata[i]=num;
      for (int j = 0; j < num; j++) {
        strata[i][j] = (InstanceWrapper) tempStrata[i].elementAt(j);
      }
    }
  }
  
  public int getNumInstancesOfIteration(){
	  
	    if (lastIteration) {
	        return is.length;
	      }
	      return numInstancesStrata[currentIteration % numStrata];
	  
  }

  public boolean newIteration() {
    currentIteration++;
    if (currentIteration == Parameters.numIterations) {
      lastIteration = true;
    }

    if (numStrata > 1) {
      return true;
    }
    return false;
  }

  public InstanceWrapper[] getInstances() {
    if (lastIteration) {
      return is;
    }
    return strata[currentIteration % numStrata];
  }

  public int numVersions() {
    if (lastIteration) {
      return 1;
    }
    return numStrata;
  }

  public int getCurrentVersion() {
    if (lastIteration) {
      return 0;
    }
    return currentIteration % numStrata;
  }

}

