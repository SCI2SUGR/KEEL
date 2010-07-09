//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 18-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.AllKNN;

public class Main {

  public static void main (String args[]) {

    AllKNN allKNN;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      allKNN = new AllKNN (args[0]);
      allKNN.ejecutar();
    }
  }
}