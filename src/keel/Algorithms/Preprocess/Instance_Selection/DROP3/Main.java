//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 16-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.DROP3;

public class Main {

  public static void main (String args[]) {

    DROP3 drop3;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      drop3 = new DROP3 (args[0]);
      drop3.ejecutar();
    }
  }
}
