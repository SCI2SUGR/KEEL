package keel.Algorithms.Preprocess.Feature_Selection.nonevolutionary_algorithms.FORWARD.filter;

public class Main {


  public static void main (String args[]) {

    ForwardIncon fwd;

    if (args.length != 1)

      System.err.println("Error. A parameter is only needed.");

    else {

      fwd = new ForwardIncon (args[0]);
      fwd.ejecutar();

    }

  }

}

