//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 19-8-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.CPruner;

public class Main {

  public static void main (String args[]) {

    CPruner cPruner;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      cPruner = new CPruner (args[0]);
      cPruner.ejecutar();
    }
  }
}