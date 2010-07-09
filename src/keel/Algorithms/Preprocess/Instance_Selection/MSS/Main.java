//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 11-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.MSS;

public class Main {

  public static void main (String args[]) {

    MSS mss;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      mss = new MSS (args[0]);
      mss.ejecutar();
    }
  }
}