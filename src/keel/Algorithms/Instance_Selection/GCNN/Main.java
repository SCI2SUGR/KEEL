//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 18-6-2007.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.GCNN;

public class Main {

  public static void main (String args[]) {

    GCNN gcnn;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      gcnn = new GCNN (args[0]);
      gcnn.ejecutar();
    }
  }
}