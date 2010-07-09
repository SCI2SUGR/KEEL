//

//  Main.java

//

//  Salvador Garc�a L�pez

//

//  Created by Salvador Garc�a L�pez 11-7-2004.

//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.

//



package keel.Algorithms.Instance_Selection.RENN;



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

