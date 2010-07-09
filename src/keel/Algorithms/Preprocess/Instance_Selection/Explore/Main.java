//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 1-8-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.Explore;

public class Main {

  public static void main (String args[]) {

    Explore explore;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      explore = new Explore (args[0]);
      explore.ejecutar();
    }
  }
}