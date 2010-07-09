//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 22-2-2005.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.GG;

public class Main {

  public static void main (String args[]) {

    GG gg;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      gg = new GG (args[0]);
      gg.ejecutar();
    }
  }
}
