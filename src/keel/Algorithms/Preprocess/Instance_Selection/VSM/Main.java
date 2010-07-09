//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 12-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.VSM;

public class Main {

  public static void main (String args[]) {

    VSM vsm;

    if (args.length != 1)
      System.err.println("Error. A parameter is onlyneeded.");
    else {
      vsm = new VSM (args[0]);
      vsm.ejecutar();
    }
  }
}
