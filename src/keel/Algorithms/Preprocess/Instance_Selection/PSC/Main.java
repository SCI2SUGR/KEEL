//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 4-2-2009.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.PSC;

public class Main {

  public static void main (String args[]) {

    PSC psc;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      psc = new PSC (args[0]);
      psc.ejecutar();
    }
  }
}
