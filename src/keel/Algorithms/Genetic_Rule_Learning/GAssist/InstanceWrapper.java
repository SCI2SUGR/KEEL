/**
 * <p>
 * @author Written by Jaume Bacardit (La Salle, Ram�n Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Sol� (La Salle, Ram�n Llull University - Barcelona) 23/12/2008
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
