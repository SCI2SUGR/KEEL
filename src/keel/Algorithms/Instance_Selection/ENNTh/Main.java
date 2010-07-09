//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 19-5-2008.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.ENNTh;

public class Main {

  public static void main (String args[]) {

    ENNTh ennth;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      ennth = new ENNTh (args[0]);
      ennth.ejecutar();
    }
  }
}
