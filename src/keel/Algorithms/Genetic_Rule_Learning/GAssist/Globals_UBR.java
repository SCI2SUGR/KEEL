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


public class Globals_UBR {
/**
 * <p>
 * Computes and maintains global information for the UBR KR
 * </p>
 */
	
  public static int ruleSize;
  public static int[] size;
  public static int[] offset;
  public static int[] types;
  public static double[] minD;
  public static double[] maxD;
  public static double[] sizeD;

  public static void initialize() {
    ruleSize = 0;
    size = new int[Parameters.numAttributes];
    types = new int[Parameters.numAttributes];
    offset = new int[Parameters.numAttributes];
    minD = new double[Parameters.numAttributes];
    maxD = new double[Parameters.numAttributes];
    sizeD = new double[Parameters.numAttributes];

    for (int i = 0; i < Parameters.numAttributes; i++) {
      Attribute at = Attributes.getAttribute(i);
      offset[i] = ruleSize;
      if (at.getType() == Attribute.NOMINAL) {
        types[i] = Attribute.NOMINAL;
        size[i] = at.getNumNominalValues();
      }
      else {
        types[i] = Attribute.REAL;
        size[i] = 4;
        minD[i] = at.getMinAttribute();
        maxD[i] = at.getMaxAttribute();
        sizeD[i] = maxD[i] - minD[i];
      }
      ruleSize += size[i];
    }
    ruleSize++;
  }

  public static boolean hasDefaultClass() {
    return true;
  }
}
