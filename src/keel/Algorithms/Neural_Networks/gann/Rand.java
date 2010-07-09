package keel.Algorithms.Neural_Networks.gann;

import org.core.Randomize;

/**
 * <p>
 * Class to obtain random values
 * </p>
 * @author Written by Nicolas Garcia Pedrajas (University of Cordoba) 27/02/2007
 * @version 0.1
 * @since JDK1.5
 */
public class Rand {
	
  /**
   * <p>
   * Empty constructor
   * </p>
   */
  public Rand() {
  }

  /**
   * <p>
   * Method that returns a random double value between min and max values
   * </p>
   * @param min Minimum double value
   * @param max Maximum double value
   * @return random double between min and max values
   */
  public static double frandom( double min, double max) {
	  return Randomize.Randdouble(min, max);
  }

  /**
   * <p>
   * Method that returns a random integer value between min and max values
   * </p>
   * @param min Minimum double value
   * @param max Maximum double value
   * @return random int between min and max values
   */
  public static int irandom( double min, double max) {
	  return (int) Randomize.Randdouble(min, max);
  }

  /**
   * <p>
   * Generate a normal distributed value for N(m, s)
   * </p>
   * @param mean Mean of the normal distribution
   * @param sigma Standard deviation of the normal distribution
   * @return normal distribution value
   */
  public static double Normal(double mean, double sigma) {

    double fac, r, v1, v2, gasdev;

    do {
      v1 = 2 * frandom( 0, 1) - 1;
      v2 = 2 * frandom( 0, 1) - 1;
      r = v1 * v1 + v2 * v2;
    }
    while (r >= 1);
    fac = Math.sqrt( -2 * Math.log(r) / r);
    gasdev = v2 * fac;

    return ( (gasdev * sigma) + mean);
  }

}
