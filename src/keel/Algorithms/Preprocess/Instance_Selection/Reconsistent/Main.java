//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 11-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.Reconsistent;

public class Main {

  public static void main (String args[]) {

    Reconsistent rec;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      rec = new Reconsistent (args[0]);
      rec.ejecutar();
    }
  }
}