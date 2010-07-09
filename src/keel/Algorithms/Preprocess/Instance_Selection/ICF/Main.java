//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 15-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.ICF;

public class Main {

  public static void main (String args[]) {

    ICF icf;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      icf = new ICF (args[0]);
      icf.ejecutar();
    }
  }
}
