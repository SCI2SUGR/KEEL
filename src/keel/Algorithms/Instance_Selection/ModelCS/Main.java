//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 13-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.ModelCS;

public class Main {

  public static void main (String args[]) {

    ModelCS modelcs;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      modelcs = new ModelCS (args[0]);
      modelcs.ejecutar();
    }
  }
}