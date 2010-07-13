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
 * @author Written by Jaume Bacardit (La Salle, Ramï¿½n Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Solï¿½ (La Salle, Ramï¿½n Llull University - Barcelona) 23/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Genetic_Rule_Learning.GAssist;

import keel.Dataset.*;
//import keel.Algorithms.Discretizers.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;



public class InstanceWrapper {
/**
 * <p>
 * Wrapper for the global KEEL Instance class tailored to the needs of GAssist
 * </p>
 */

  int[] nominalValues;
  double[] realValues;
  int instanceClass;
  int[][] nominalValuesFromDiscretizers;

  public InstanceWrapper(Instance ins) {
    nominalValues = new int[Parameters.numAttributes];
    realValues = new double[Parameters.numAttributes];

    for (int i = 0; i < Parameters.numAttributes; i++) {
      nominalValues[i]=ins.getInputNominalValuesInt(i);
      realValues[i]=ins.getInputRealValues(i);
    }
    instanceClass=ins.getOutputNominalValuesInt(0);

    if (Parameters.adiKR) {
      int num = DiscretizationManager.getNumDiscretizers();
      nominalValuesFromDiscretizers = new int[Parameters.numAttributes][];
      for (int i = 0; i < Parameters.numAttributes; i++) {
        if (Attributes.getAttribute(i).getType() == Attribute.REAL ||
            Attributes.getAttribute(i).getType() == Attribute.INTEGER) {
          nominalValuesFromDiscretizers[i] = new int[num];
          for (int j = 0; j < num; j++) {
            nominalValuesFromDiscretizers[i][j] =
                DiscretizationManager.getDiscretizer(j).discretize(i,
                realValues[i]);
          }
        }
      }
    }
  }

  public int[][] getDiscretizedValues() {
    return nominalValuesFromDiscretizers;
  }

  public int getDiscretizedValue(int attribute, int discretizer) {
    return nominalValuesFromDiscretizers[attribute][discretizer];
  }

  public int[] getNominalValues() {
    return nominalValues;
  }

  public int getNominalValue(int attribute) {
    return nominalValues[attribute];
  }

  public double[] getRealValues() {
    return realValues;
  }

  public double getRealValue(int attribute) {
    return realValues[attribute];
  }

  public int classOfInstance() {
    return instanceClass;
  }
}

