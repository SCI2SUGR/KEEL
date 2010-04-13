/**
 * <p>
 * @author Written by Jaume Bacardit (La Salle, Ramón Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 23/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Genetic_Rule_Learning.MPLCS;

import java.util.*;


public class Utils {
/**
 * <p>
 * Small routines doing trivial stuff here
 * </p>
 */
	
  public static double getAverage(ArrayList data) {
    double ave = 0;
    int i, size = data.size();

    for (i = 0; i < size; i++) {
      ave += ( (Double) data.get(i)).doubleValue();
    }

    ave /= (double) size;
    return ave;
  }

  public static double getDeviation(ArrayList data) {
    double ave = getAverage(data), dev = 0;
    int i, size = data.size();

    for (i = 0; i < size; i++) {
      double val = ( (Double) data.get(i)).doubleValue();
      dev += Math.pow(val - ave, 2.0);
    }

    dev /= (double) size;
    return Math.sqrt(dev);
  }
}
