//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 26-9-2008.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.FCNN;

public class Main {

  public static void main (String args[]) {

    FCNN fcnn;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      fcnn = new FCNN (args[0]);
      fcnn.ejecutar();
    }
  }
}