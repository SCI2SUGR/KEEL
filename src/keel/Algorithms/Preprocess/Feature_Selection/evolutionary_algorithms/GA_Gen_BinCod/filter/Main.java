/**
 * <p>
 * @author Written by Manuel Chica Serrano (University of Jaen) 01/09/2005
 * @author Modified by Jose Joaquin Aguilera Garcia (University of Jaen) 19/12/2008
 * @author Modified by Cristobal Jose Carmona del Jesus (University of Jaen) 19/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
package keel.Algorithms.Preprocess.Feature_Selection.evolutionary_algorithms.GA_Gen_BinCod.filter;

public class Main {
/**
 * <p> 
 * This class realizes the call to the main method
 * </p>
 */

  public static void main (String args[]) {

    GGABinaryIncon aggb;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      aggb = new GGABinaryIncon (args[0]);
      aggb.ejecutar();
    }

  }

}

