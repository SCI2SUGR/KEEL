//
//  Main.java
//
//  Mikel Galar Idoate (UPNA)
//
//  Created by Mikel Galar Idoate (UPNA) 11-5-2010.
//

package keel.Algorithms.ImbalancedClassification.Ensembles.SPIDER;

public class Main {

  public static void main (String args[]) {

    SPIDER spider;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      spider = new SPIDER (args[0]);
      spider.ejecutar();
    }
  }
}
