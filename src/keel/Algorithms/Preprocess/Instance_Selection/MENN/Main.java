//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 9-4-2008.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.MENN;

public class Main {

  public static void main (String args[]) {

    MENN menn;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      menn = new MENN (args[0]);
      menn.ejecutar();
    }
  }
}
