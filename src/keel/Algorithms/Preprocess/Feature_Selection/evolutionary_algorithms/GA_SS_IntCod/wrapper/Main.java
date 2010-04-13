/**
 * <p>
 * @author Written by Manuel Chica Serrano (University of Jaen) 28/08/2005
 * @author Modified by Jose Joaquin Aguilera Garcia (University of Jaen) 19/12/2008
 * @author Modified by Cristobal Carmona del Jesus (University of Jaen) 19/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
package keel.Algorithms.Preprocess.Feature_Selection.evolutionary_algorithms.GA_SS_IntCod.wrapper;

public class Main {
/**
 * <p> 
 * This class realizes the call to the main method
 * </p>
 */

  public static void main (String args[]) {

    SSGAIntegerLVO agee;

    if (args.length != 1)

      System.err.println("Error. A parameter is only needed.");

    else {

      agee = new SSGAIntegerLVO (args[0]);
      agee.ejecutar();

    }

  }

}

