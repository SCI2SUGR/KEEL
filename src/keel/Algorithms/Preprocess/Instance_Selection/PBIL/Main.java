//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 15-4-2005.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.PBIL;

public class Main {

  public static void main (String args[]) {

    PBIL pbil;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      pbil = new PBIL (args[0]);
      pbil.ejecutar();
    }
  }
}
