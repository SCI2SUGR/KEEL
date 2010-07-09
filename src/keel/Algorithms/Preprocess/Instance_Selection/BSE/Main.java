//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 11-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.BSE;

public class Main {

  public static void main (String args[]) {

    BSE bse;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      bse = new BSE (args[0]);
      bse.ejecutar();
    }
  }
}