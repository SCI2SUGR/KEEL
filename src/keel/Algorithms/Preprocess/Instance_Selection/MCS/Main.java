//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 6-4-2007.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.MCS;

public class Main {

  public static void main (String args[]) {

    MCS mcs;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      mcs = new MCS (args[0]);
      mcs.ejecutar();
    }
  }
}
