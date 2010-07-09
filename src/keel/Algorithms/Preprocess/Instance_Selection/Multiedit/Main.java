//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 13-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.Multiedit;

public class Main {

  public static void main (String args[]) {

    Multiedit multiedit;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      multiedit = new Multiedit (args[0]);
      multiedit.ejecutar();
    }
  }
}