/**
 * <p>
 * @author Written by Jaume Bacardit (La Salle, Ramón Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 23/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

/*
 * Globals_GABIL.java
 *
 */
package keel.Algorithms.Genetic_Rule_Learning.GAssist;

import keel.Dataset.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.Parameters;

public class Globals_GABIL {
/**
 * <p>
 * Computes and maintains global information for the GABIL KR
 * </p>
 */
	
  public static int ruleSize;
  public static int[] size;
  public static int[] offset;

  public static void initialize() {
    ruleSize = 0;
    size = new int[Parameters.numAttributes];
    offset = new int[Parameters.numAttributes];

    for (int i = 0; i < Parameters.numAttributes; i++) {
      Attribute at = Attributes.getAttribute(i);
      offset[i] = ruleSize;
      size[i] = at.getNumNominalValues();
      ruleSize += size[i];
    }
    ruleSize++;

  }

  public static boolean hasDefaultClass() {
    return true;
  }
}
