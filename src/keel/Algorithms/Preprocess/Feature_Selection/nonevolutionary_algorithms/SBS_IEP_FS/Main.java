/**
 * <p>
 * @author Ignacio Robles Paiz (University of Granada) 26/06/2010
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

package keel.Algorithms.Preprocess.Feature_Selection.nonevolutionary_algorithms.SBS_IEP_FS;


public class Main {
/**
 * <p>
 * This class calls the main method
 * </p>
 */


  public static void main (String args[]) {
    SBS alg;

    if (args.length != 1)
      System.err.println("Error. A parameter is needed.");
    else {
      alg = new SBS (args[0]);
      alg.ejecutar();
    }

  }

}

