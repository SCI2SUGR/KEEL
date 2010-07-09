//

//  Main.java

//

//  Salvador García López

//

//  Created by Salvador García López 16-7-2004.

//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.

//



package keel.Algorithms.Preprocess.Instance_Selection.DROP1;





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

