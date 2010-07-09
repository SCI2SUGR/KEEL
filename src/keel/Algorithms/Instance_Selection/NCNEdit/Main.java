//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 26-4-2008.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.NCNEdit;

public class Main {

  public static void main (String args[]) {

    NCNEdit ncn;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      ncn = new NCNEdit (args[0]);
      ncn.ejecutar();
    }
  }
}
