//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 13-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.Multiedit;

public class Main {

  public static void main (String args[]) {

    Multiedit multiedit;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      multiedit = new Multiedit (args[0]);
      multiedit.ejecutar();
    }
  }
}