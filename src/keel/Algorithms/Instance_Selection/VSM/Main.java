//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 12-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.VSM;

public class Main {

  public static void main (String args[]) {

    VSM vsm;

    if (args.length != 1)
      System.err.println("Error. A parameter is onlyneeded.");
    else {
      vsm = new VSM (args[0]);
      vsm.ejecutar();
    }
  }
}
