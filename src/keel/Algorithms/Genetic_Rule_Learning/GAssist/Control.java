/**
 * <p>
 * @author Written by Jaume Bacardit (La Salle, Ramón Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 23/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Genetic_Rule_Learning.GAssist;

import keel.Algorithms.Genetic_Rule_Learning.Globals.*;

public class Control {

  /** Creates a new instance of Control */
  public Control() {
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    long t1 = System.currentTimeMillis();

    ParserParameters.doParse(args[0]);
    //Parameters.trainFile = args[1];
    //Parameters.testFile = args[2];
    LogManager.initLogManager();
    Rand.initRand();

    GA ga = new GA();
    ga.initGA();
    ga.run();

    LogManager.println(Chronometer.getChrons());
    long t2 = System.currentTimeMillis();
    LogManager.println("Total time: " + ( (t2 - t1) / 1000.0));

    LogManager.closeLog();
  }

}
