//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 27-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.ZhangTS;

public class Main {

  public static void main (String args[]) {

    ZhangTS ts;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      ts = new ZhangTS (args[0]);
      ts.ejecutar();
    }
  }
}