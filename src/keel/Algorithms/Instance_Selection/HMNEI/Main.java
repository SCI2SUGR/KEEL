//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 7-7-2008.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.HMNEI;

public class Main {

  public static void main (String args[]) {

    HMNEI hmnei;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      hmnei = new HMNEI (args[0]);
      hmnei.ejecutar();
    }
  }
}
