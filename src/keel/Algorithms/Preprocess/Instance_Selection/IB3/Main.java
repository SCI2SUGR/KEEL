//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 14-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.IB3;

public class Main {

  public static void main (String args[]) {

    IB3 ib3;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      ib3 = new IB3 (args[0]);
      ib3.ejecutar();
    }
  }
}