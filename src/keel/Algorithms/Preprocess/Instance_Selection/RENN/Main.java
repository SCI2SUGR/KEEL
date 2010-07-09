//

//  Main.java

//

//  Salvador García López

//

//  Created by Salvador García López 11-7-2004.

//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.

//



package keel.Algorithms.Preprocess.Instance_Selection.RENN;



public class Main {



  public static void main (String args[]) {



    RENN renn;



    if (args.length != 1)

      System.err.println("Error. Only a parameter is needed.");

    else {

      renn = new RENN (args[0]);

      renn.ejecutar();

    }

  }

}

