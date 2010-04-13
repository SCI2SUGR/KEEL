//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 19-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.GGA;

public class Main {

  public static void main (String args[]) {

    GGA gga;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      gga = new GGA (args[0]);
      gga.ejecutar();
    }
  }
}
