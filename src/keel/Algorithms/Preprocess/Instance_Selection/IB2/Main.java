//

//  Main.java

//

//  Salvador García López

//

//  Created by Salvador García López 14-7-2004.

//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.

//



package keel.Algorithms.Preprocess.Instance_Selection.IB2;



public class Main {



  public static void main (String args[]) {



    IB2 ib2;



    if (args.length != 1)

      System.err.println("Error. Only a parameter is needed.");

    else {

      ib2 = new IB2 (args[0]);

      ib2.ejecutar();

    }

  }

}

