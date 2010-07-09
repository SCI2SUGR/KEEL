//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 19-8-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.CPruner;

public class Main {

  public static void main (String args[]) {

    CPruner cPruner;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      cPruner = new CPruner (args[0]);
      cPruner.ejecutar();
    }
  }
}