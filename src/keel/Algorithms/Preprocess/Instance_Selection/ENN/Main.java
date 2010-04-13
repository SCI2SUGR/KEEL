//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 11-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.ENN;

public class Main {

  public static void main (String args[]) {

    ENN enn;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      enn = new ENN (args[0]);
      enn.ejecutar();
    }
  }
}
