//

//  Main.java

//

//  Salvador García López

//

//  Created by Salvador García López 17-7-2004.

//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.

//



package keel.Algorithms.Preprocess.Instance_Selection.ENNRS;



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

