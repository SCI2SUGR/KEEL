package keel.Algorithms.Preprocess.Feature_Selection.nonevolutionary_algorithms.FORWARD.wrapper;

public class Main {


  public static void main (String args[]) {

    ForwardLVO fwd;

    if (args.length != 1)

      System.err.println("Error. A parameter is only needed.");

    else {

      fwd = new ForwardLVO (args[0]);
      fwd.ejecutar();

    }

  }

}

