//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 3-6-2009.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.TRKNN;

public class Main {

  public static void main (String args[]) {

    TRKNN trknn;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      trknn = new TRKNN (args[0]);
      trknn.ejecutar();
    }
  }
}
