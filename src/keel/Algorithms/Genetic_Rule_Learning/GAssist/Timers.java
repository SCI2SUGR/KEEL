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


public class Timers {
/**
 * <p>
 * Manages timers: flags and parameters that are triggered at
 * certain iterations
 * </p>
 */
	

  public static boolean runTimers(int iteration, Classifier[] population) {
    Globals_ADI.nextIteration();
    boolean res1 = Globals_MDL.newIteration(iteration, population);
    boolean res2 = timerBloat(iteration);

    if (res1 || res2) {
      return true;
    }
    return false;
  }

  public static void runOutputTimers(int iteration
                                     , Classifier[] population) {
    Globals_DefaultC.checkNichingStatus(iteration, population);
  }

  static boolean timerBloat(int _iteration) {
    Parameters.doRuleDeletion = (_iteration >= Parameters.iterationRuleDeletion);
    Parameters.doHierarchicalSelection = (_iteration >=
                                          Parameters.iterationHierarchicalSelection);

    if (_iteration == Parameters.numIterations - 1) {
      Parameters.ruleDeletionMinRules = 1;
      return true;
    }

    if (_iteration == Parameters.iterationRuleDeletion) {
      return true;
    }

    return false;
  }
}
