//

//  Main.java

//

//  Salvador García López

//

//  Created by Salvador García López 16-7-2004.

//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.

//



package keel.Algorithms.Preprocess.Instance_Selection.DROP2;





public class Main {



  public static void main (String args[]) {



    DROP2 drop2;



    if (args.length != 1)

      System.err.println("Error. Only a parameter is needed.");

    else {

      drop2 = new DROP2 (args[0]);

      drop2.ejecutar();

    }

  }

}

