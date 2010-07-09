//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 12-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.RNN;

public class Main {

  public static void main (String args[]) {

    RNN rnn;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      rnn = new RNN (args[0]);
      rnn.ejecutar();
    }
  }
}
