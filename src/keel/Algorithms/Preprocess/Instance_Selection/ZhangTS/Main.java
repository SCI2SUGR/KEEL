//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 27-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.ZhangTS;

public class Main {

  public static void main (String args[]) {

    ZhangTS ts;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      ts = new ZhangTS (args[0]);
      ts.ejecutar();
    }
  }
}