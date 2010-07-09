//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 13-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.Shrink;

public class Main {

  public static void main (String args[]) {

    Shrink shrink;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      shrink = new Shrink (args[0]);
      shrink.ejecutar();
    }
  }
}