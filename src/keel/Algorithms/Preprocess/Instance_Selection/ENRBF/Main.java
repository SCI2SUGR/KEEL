//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 11-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.ENRBF;

public class Main {

  public static void main (String args[]) {

    ENRBF enrbf;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      enrbf = new ENRBF (args[0]);
      enrbf.ejecutar();
    }
  }
}