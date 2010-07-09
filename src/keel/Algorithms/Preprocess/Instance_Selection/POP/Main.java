//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 28-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.POP;

public class Main {

  public static void main (String args[]) {

    POP pop;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      pop = new POP (args[0]);
      pop.ejecutar();
    }
  }
}