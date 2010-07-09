//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 23-2-2008.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.TCNN;

public class Main {

  public static void main (String args[]) {

    TCNN tcnn;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      tcnn = new TCNN (args[0]);
      tcnn.ejecutar();
    }
  }
}