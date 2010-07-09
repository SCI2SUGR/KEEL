//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 3-6-2009.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.TRKNN;

public class Main {

  public static void main (String args[]) {

    TRKNN trknn;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      trknn = new TRKNN (args[0]);
      trknn.ejecutar();
    }
  }
}
