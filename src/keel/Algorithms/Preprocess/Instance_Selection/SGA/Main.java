//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 19-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.SGA;

public class Main {

  public static void main (String args[]) {

    SGA sga;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      sga = new SGA (args[0]);
      sga.ejecutar();
    }
  }
}