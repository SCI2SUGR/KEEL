package keel.Algorithms.Neural_Networks.gann;

import java.util.Comparator;

/**
 * <p>
 * This is a public Class that implements the Comparator interface
 * </p>
 * @author Written by Nicolas Garcia Pedrajas (University of Cordoba) 27/02/2007
 * @version 0.1
 * @since JDK1.5
 */

public class Compare
    implements Comparator {
	
  /**
   * <p>
   * Method implemented for decremental ordering
   * </p>
   *
   * @param o1 Object
   * @param o2 Object
   * @return int
   */
  public int compare(Object o1, Object o2) {
    Ranking d1, d2;

    if ( (o1 instanceof Ranking) && (o2 instanceof Ranking)) {
      d1 = (Ranking) o1;
      d2 = (Ranking) o2;

      if (d1.fitness > d2.fitness) {
        return -1;
      }
      else if (d1.fitness < d2.fitness) {
        return 1;
      }
    }

    return 0;

  }
}
