//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 10-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Generation.Depur;

public class Main {

  public static void main (String args[]) {

    Depur isaak;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      isaak = new Depur (args[0]);
      isaak.ejecutar();
    }
  }
}