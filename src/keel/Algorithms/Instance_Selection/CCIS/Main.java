//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 3-3-2010.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.CCIS;

public class Main {

  public static void main (String args[]) {

    CCIS ccis;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      ccis = new CCIS (args[0]);
      ccis.ejecutar();
    }
  }
}
