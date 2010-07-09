//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 16-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.SVBPS;

public class Main {

  public static void main (String args[]) {

    SVBPS svbps;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      svbps = new SVBPS (args[0]);
      svbps.ejecutar();
    }
  }
}