//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 3-3-2010.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.CCIS;

public class Main {

  public static void main (String args[]) {

    CCIS ccis;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      ccis = new CCIS (args[0]);
      ccis.ejecutar();
    }
  }
}
