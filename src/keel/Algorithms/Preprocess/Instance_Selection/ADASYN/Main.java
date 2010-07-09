/**
 * <p>
 * File: Main.java
 *
 * A Main class to process the paramethers of the method and launch the algorithm
 *
 * @author Written by Salvador Garcia Lopez (University of Granada) 30/03/2006
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Preprocess.Instance_Selection.ADASYN;


public class Main {
    
  /**
   * Main method
   *
   * @param args Arguments of the program
   */
  public static void main (String args[]) {

    ADASYN smote;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      smote = new ADASYN (args[0]);
      smote.run();
    }
  }
}

