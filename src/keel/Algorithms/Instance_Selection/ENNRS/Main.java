//

//  Main.java

//

//  Salvador Garc�a L�pez

//

//  Created by Salvador Garc�a L�pez 17-7-2004.

//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.

//



package keel.Algorithms.Instance_Selection.ENNRS;



public class Main {



  public static void main (String args[]) {



    ENNRS ennrs;



    if (args.length != 1)

      System.err.println("Error. Only a parameter is needed.");

    else {

      ennrs = new ENNRS (args[0]);

      ennrs.ejecutar();

    }

  }

}

