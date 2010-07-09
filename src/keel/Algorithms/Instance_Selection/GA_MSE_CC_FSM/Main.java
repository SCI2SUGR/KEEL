//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 19-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.GA_MSE_CC_FSM;

public class Main {

  public static void main (String args[]) {

    GA_MSE_CC_FSM ga;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      ga = new GA_MSE_CC_FSM (args[0]);
      ga.ejecutar();
    }
  }
}
