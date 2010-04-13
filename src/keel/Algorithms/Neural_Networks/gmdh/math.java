package keel.Algorithms.Neural_Networks.gmdh;

/**
 * <p>
 * Class with mathematical operations
 * </p>
 * @author Written by Nicolas Garcia Pedrajas (University of Cordoba) 27/02/2007
 * @version 0.1
 * @since JDK1.5
 */

public class math {
  /**
   * <p>
   * Empty constructor
   * </p>
   */
  public math() {
  }

  /**
   * <p>
   * Calculates base 10 logarithm of argument x
   * </p>
   * @param x argument
   * @return base 10 logarithm
   */
  public static double log10 (double x) {

    return Math.log(x)/Math.log(10.0);
  }
}
