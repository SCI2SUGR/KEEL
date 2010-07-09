//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 16-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.DROP3;

public class Main {

  public static void main (String args[]) {

    DROP3 drop3;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      drop3 = new DROP3 (args[0]);
      drop3.ejecutar();
    }
  }
}
