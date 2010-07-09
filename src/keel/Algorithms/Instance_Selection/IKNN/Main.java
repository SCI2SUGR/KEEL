//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 2-6-2009.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.IKNN;

public class Main {

  public static void main (String args[]) {

    IKNN iknn;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      iknn = new IKNN (args[0]);
      iknn.ejecutar();
    }
  }
}
