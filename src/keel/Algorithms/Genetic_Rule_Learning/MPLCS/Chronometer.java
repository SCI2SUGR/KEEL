/**
 * <p>
 * @author Written by Jaume Bacardit (La Salle, Ramón Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 23/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Genetic_Rule_Learning.MPLCS;

public class Chronometer {
/**
 * <p>
 * Measure and count the amount of time that the system spends in each GA stage
 * </p> 
 */

  /************************************
   *  Parameters controlling evaluation time
   */
  public static long tmInitialGlobalEvaluation;
  public static long tmIterationEvaluation = 0;
  public static long tmGlobalEvaluation = 0;

  /************************************
   *  Parameters controlling selection time
   */
  public static long tmInitialGlobalSelection;
  public static long tmIterationSelection = 0;
  public static long tmGlobalSelection = 0;

  /************************************
   *  Parameters controlling genetic operators' time
   */
  public static long tmInitialGlobalGAOperators;
  public static long tmIterationGAOperators = 0;
  public static long tmGlobalGAOperators = 0;
  public static long tmInitialGlobalCrossover;
  public static long tmIterationCrossover = 0;
  public static long tmGlobalCrossover = 0;
  public static long tmInitialGlobalMutation;
  public static long tmIterationMutation = 0;
  public static long tmGlobalMutation = 0;

  /************************************
   *  Parameters controlling reemplacement time
   */
  public static long tmInitialGlobalReplacement;
  public static long tmIterationReplacement = 0;
  public static long tmGlobalReplacement = 0;

  /************************************
   *  Parameters controlling statistics time
   */
  public static long tmInitialGlobalStatistics;
  public static long tmIterationStatistics = 0;
  public static long tmGlobalStatistics = 0;

  /**
   *  Enables evaluation's chronometer
   */
  public static void startChronEvaluation() {
    tmInitialGlobalEvaluation = System.currentTimeMillis();
  }

  /**
   *  Stops evaluation's chronometer
   */
  public static void stopChronEvaluation() {
    long diff = System.currentTimeMillis() - tmInitialGlobalEvaluation;
    tmIterationEvaluation = diff;
    tmGlobalEvaluation += diff;
  }

  /**
   *  Enables selection's chronometer
   */
  public static void startChronSelection() {
    tmInitialGlobalSelection = System.currentTimeMillis();
  }

  /**
   *  Stops selection's chronometer
   */
  public static void stopChronSelection() {
    long diff = System.currentTimeMillis() - tmInitialGlobalSelection;
    tmIterationSelection = diff;
    tmGlobalSelection += diff;
  }

  /**
   *  Enables selection's chronometer
   */
  public static void startChronReplacement() {
    tmInitialGlobalReplacement = System.currentTimeMillis();
  }

  /**
   *  Stops selection's chronometer
   */
  public static void stopChronReplacement() {
    long diff = System.currentTimeMillis() - tmInitialGlobalReplacement;
    tmIterationReplacement = diff;
    tmGlobalReplacement += diff;
  }

  /**
   *  Enables GA operators' chronometer
   */
  public static void startChronGAOperators() {
    tmInitialGlobalGAOperators = System.currentTimeMillis();
  }

  /**
   *  Stops GA operators' chronometer
   */
  public static void stopChronGAOperators() {
    long diff = System.currentTimeMillis() - tmInitialGlobalGAOperators;
    tmIterationGAOperators = diff;
    tmGlobalGAOperators += diff;
  }

  /**
   *  Enables crossover's chronometer
   */
  public static void startChronCrossover() {
    tmInitialGlobalCrossover = System.currentTimeMillis();
  }

  /**
   *  Stops crossover's chronometer
   */
  public static void stopChronCrossover() {
    long diff = System.currentTimeMillis() - tmInitialGlobalCrossover;
    tmIterationCrossover = diff;
    tmGlobalCrossover += diff;
  }

  /**
   *  Enables mutation's chronometer
   */
  public static void startChronMutation() {
    tmInitialGlobalMutation = System.currentTimeMillis();
  }

  /**
   *  Stops crossover's chronometer
   */
  public static void stopChronMutation() {
    long diff = System.currentTimeMillis() - tmInitialGlobalMutation;
    tmIterationMutation = diff;
    tmGlobalMutation += diff;
  }

  /**
   *  Enables statistics' chronometer
   */
  public static void startChronStatistics() {
    tmInitialGlobalStatistics = System.currentTimeMillis();
  }

  /**
   *  Stops statistics' chronometer
   */
  public static void stopChronStatistics() {
    long diff = System.currentTimeMillis() - tmInitialGlobalStatistics;
    tmIterationStatistics = diff;
    tmGlobalStatistics += diff;
  }

  public static String getChronEvaluation() {
    return "" + (Chronometer.tmGlobalEvaluation / 1000.0);
  }

  public static String getChronSelection() {
    return "" + (Chronometer.tmGlobalSelection / 1000.0);
  }

  public static String getChronCrossover() {
    return "" + (Chronometer.tmGlobalCrossover / 1000.0);
  }

  public static String getChronMutation() {
    return "" + (Chronometer.tmGlobalMutation / 1000.0);
  }

  public static String getChronReplacement() {
    return "" + (Chronometer.tmGlobalReplacement / 1000.0);
  }

  public static String getChrons() {
    String ret = "";
    ret += "Evaluation time: " + getChronEvaluation() + "\n" +
        "Selection time: " + getChronSelection() + "\n" +
        "Crossover time: " + getChronCrossover() + "\n" +
        "Mutation time: " + getChronMutation() + "\n" +
        "Replacement time: " + getChronReplacement() + "\n";
    return ret;
  }
}
