//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 23-2-2008.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.TCNN;

public class Main {

  public static void main (String args[]) {

    TCNN tcnn;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      tcnn = new TCNN (args[0]);
      tcnn.ejecutar();
    }
  }
}