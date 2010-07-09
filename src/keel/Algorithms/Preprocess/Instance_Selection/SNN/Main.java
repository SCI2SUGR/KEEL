//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 18-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.SNN;

public class Main {

  public static void main (String args[]) {

    SNN snn;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      snn = new SNN (args[0]);
      snn.ejecutar();
    }
  }
}
