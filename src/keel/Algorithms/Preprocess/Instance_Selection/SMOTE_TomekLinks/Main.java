//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 30-3-2006.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.SMOTE_TomekLinks;

public class Main {

  public static void main (String args[]) {

    SMOTE_TomekLinks smotetl;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      smotetl = new SMOTE_TomekLinks (args[0]);
      smotetl.run();
    }
  }
}