//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 30-3-2006.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.ImbalancedClassification.Ensembles.SMOTE;


public class Main {

  public static void main (String args[]) {

    SMOTE smote;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      smote = new SMOTE (args[0]);
      smote.ejecutar();
    }
  }
}

