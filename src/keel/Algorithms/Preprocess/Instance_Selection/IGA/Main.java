//
//  Main.java
//
//  Salvador García López
//
//  Created by Salvador García López 19-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.IGA;

public class Main {

  public static void main (String args[]) {
	  
    IGA iga;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      iga = new IGA (args[0]);
      iga.ejecutar();
    }
  }
}
