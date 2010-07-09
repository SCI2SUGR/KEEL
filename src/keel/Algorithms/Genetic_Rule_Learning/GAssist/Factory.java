/**
 * <p>
 * @author Written by Jaume Bacardit (La Salle, Ramón Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 23/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Genetic_Rule_Learning.GAssist;

import keel.Dataset.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;


public class Factory {
  static boolean realKR;

  public static void initialize() {
    boolean hasDefaultClass;
    if (Attributes.hasRealAttributes()|| Attributes.hasIntegerAttributes()) {
      if (Parameters.adiKR) {
        Globals_ADI.initialize();
        hasDefaultClass = Globals_ADI.hasDefaultClass();
      }
      else {
        Globals_UBR.initialize();
        hasDefaultClass = Globals_UBR.hasDefaultClass();
      }
      realKR = true;
    }
    else {
      realKR = false;
      Parameters.adiKR = false;
      Globals_GABIL.initialize();
      hasDefaultClass = Globals_GABIL.hasDefaultClass();
    }
    Globals_DefaultC.init(hasDefaultClass);
  }

  public static Classifier newClassifier() {
    if (realKR) {
      if (Parameters.adiKR) {
        return new ClassifierADI();
      }
      return new ClassifierUBR();
    }
    return new ClassifierGABIL();
  }
}
