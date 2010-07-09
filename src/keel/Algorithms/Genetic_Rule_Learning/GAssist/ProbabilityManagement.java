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

public class ProbabilityManagement {
  public final static int LINEAR = 0;
  public final static int SIGMOIDAL = 1;

  double probStart;
  double probEnd;
  double probLength;
  int evolMode;

  double currentProb;
  double sigmaYLength;
  double sigmaYBase;
  double sigmaXOffset;
  double beta;

  public ProbabilityManagement(double start, double end, int mode) {
    probStart = start;
    probEnd = end;
    evolMode = mode;

    if (mode == LINEAR) {
      probLength = end - start;
      currentProb = start;
    }
    else {
      sigmaYLength = end - start;
      sigmaYBase = start;
      sigmaXOffset = 0.5;
      beta = -10;
    }
  }

  public double incStep() {
    if (evolMode == LINEAR) {
      currentProb = Parameters.percentageOfLearning
          * probLength + probStart;
    }
    else {
      currentProb = sigmaYLength
          / (1 + Math.exp(beta
                          * (Parameters.percentageOfLearning - 0.5)))
          + sigmaYBase;
    }
    return currentProb;
  }
}
