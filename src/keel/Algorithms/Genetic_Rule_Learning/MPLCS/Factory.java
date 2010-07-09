/**
 * <p>
 * @author Written by Jaume Bacardit (La Salle, Ramón Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 23/12/2008
 * @author Modified by Jose A. Saez Munoz (ETSIIT, Universidad de Granada - Granada) 10/09/10
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Genetic_Rule_Learning.MPLCS;

import keel.Dataset.*;
import keel.Algorithms.Genetic_Rule_Learning.MPLCS.Assistant.Globals.*;


public class Factory {
  static boolean realKR;

  public static void initialize() {
    boolean hasDefaultClass;
    if (Attributes.hasRealAttributes()|| Attributes.hasIntegerAttributes()) {
      if (Parameters.adiKR) {
    	System.out.println("\n\nRepresentaci—n de reglas = ADI\n\n");
        Globals_ADI.initialize();
      }

      hasDefaultClass = Globals_ADI.hasDefaultClass();
      realKR = true;
    }
    else {
      System.out.println("\n\nRepresentaci—n de reglas = GABIL\n\n");
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

    }
    return new ClassifierGABIL();
  }
}
