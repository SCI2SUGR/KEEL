//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 25-2-2008.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.MNV;

public class Main {

  public static void main (String args[]) {

    MNV mnv;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      mnv = new MNV (args[0]);
      mnv.ejecutar();
    }
  }
}