//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 17-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.RMHC;

public class Main {

  public static void main (String args[]) {

    RMHC rmhc;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      rmhc = new RMHC (args[0]);
      rmhc.ejecutar();
    }
  }
}
