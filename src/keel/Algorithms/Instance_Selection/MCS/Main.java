//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 6-4-2007.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.MCS;

public class Main {

  public static void main (String args[]) {

    MCS mcs;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      mcs = new MCS (args[0]);
      mcs.ejecutar();
    }
  }
}
