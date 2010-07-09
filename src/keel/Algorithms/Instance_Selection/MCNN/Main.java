//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 7-4-2007.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.MCNN;

public class Main {

  public static void main (String args[]) {

    MCNN mcnn;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      mcnn = new MCNN (args[0]);
      mcnn.ejecutar();
    }
  }
}
