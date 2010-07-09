//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 4-2-2009.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.NRMCS;

public class Main {

  public static void main (String args[]) {

    NRMCS nrmcs;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      nrmcs = new NRMCS (args[0]);
      nrmcs.ejecutar();
    }
  }
}
