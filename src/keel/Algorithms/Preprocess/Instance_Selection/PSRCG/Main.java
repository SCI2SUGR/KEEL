//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 1-6-2005.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.PSRCG;

public class Main {

  public static void main (String args[]) {

    PSRCG psrcg;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      psrcg = new PSRCG (args[0]);
      psrcg.ejecutar();
    }
  }
}