//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 20-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.CHC;

public class Main {

  public static void main (String args[]) {

    CHC chc;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      chc = new CHC (args[0]);
      chc.ejecutar();
    }
  }
}