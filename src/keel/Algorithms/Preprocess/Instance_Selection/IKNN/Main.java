//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 2-6-2009.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.IKNN;

public class Main {

  public static void main (String args[]) {

    IKNN iknn;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      iknn = new IKNN (args[0]);
      iknn.ejecutar();
    }
  }
}
