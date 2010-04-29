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


public class Globals_ADI {
/**
 * <p>
 * Computes and maintains global information for the ADI KR
 * </p>
 */
	
  public static int ruleSize;
  public static int[] size;
  public static int[] offset;
  public static int[] types;
  public static ProbabilityManagement probReinit;

  public static void initialize() {
    ruleSize = 0;
    size = new int[Parameters.numAttributes];
    types = new int[Parameters.numAttributes];
    offset = new int[Parameters.numAttributes];

    for (int i = 0; i < Parameters.numAttributes; i++) {
      Attribute at = Attributes.getAttribute(i);
      offset[i] = ruleSize;
      if (at.getType() == Attribute.NOMINAL) {
        types[i] = Attribute.NOMINAL;
        size[i] = at.getNumNominalValues() + 2;
      }
      else {
        types[i] = Attribute.REAL;
        size[i] = Parameters.maxIntervals * 2 + 2;
      }
      ruleSize += size[i];
    }
    ruleSize++;
    ruleSize++;

    probReinit = new ProbabilityManagement(
        Parameters.probReinitializeBegin,
        Parameters.probReinitializeEnd,
        ProbabilityManagement.LINEAR);
  }

  public static void nextIteration() {
    if (!Parameters.adiKR) {
      return;
    }

    Parameters.probReinitialize = probReinit.incStep();
  }

  public static boolean hasDefaultClass() {
    return true;
  }
}
