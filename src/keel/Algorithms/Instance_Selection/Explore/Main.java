//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 1-8-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.Explore;

public class Main {

  public static void main (String args[]) {

    Explore explore;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      explore = new Explore (args[0]);
      explore.ejecutar();
    }
  }
}