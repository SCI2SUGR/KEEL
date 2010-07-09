//

//  Main.java

//

//  Salvador Garc�a L�pez

//

//  Created by Salvador Garc�a L�pez 16-7-2004.

//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.

//



package keel.Algorithms.Instance_Selection.DROP1;





public class Main {



  public static void main (String args[]) {



    DROP1 drop1;



    if (args.length != 1)

      System.err.println("Error. Only a parameter is needed.");

    else {

      drop1 = new DROP1 (args[0]);

      drop1.ejecutar();

    }

  }

}

