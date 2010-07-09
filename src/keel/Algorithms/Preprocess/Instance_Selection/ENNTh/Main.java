//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 19-5-2008.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.ENNTh;

public class Main {

  public static void main (String args[]) {

    ENNTh ennth;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      ennth = new ENNTh (args[0]);
      ennth.ejecutar();
    }
  }
}
