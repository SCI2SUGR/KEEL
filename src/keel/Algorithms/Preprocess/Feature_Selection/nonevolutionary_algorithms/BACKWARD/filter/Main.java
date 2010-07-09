package keel.Algorithms.Preprocess.Feature_Selection.nonevolutionary_algorithms.BACKWARD.filter;

public class Main {


  public static void main (String args[]) {

    BackwardIncon bck;

    if (args.length != 1)

      System.err.println("Error. A parameter is only needed.");

    else {

      bck = new BackwardIncon (args[0]);
      bck.ejecutar();

    }

  }

}

