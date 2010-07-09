package keel.Algorithms.Preprocess.Feature_Selection.nonevolutionary_algorithms.BACKWARD.wrapper;

public class Main {


  public static void main (String args[]) {

    BackwardLVO bck;

    if (args.length != 1)

      System.err.println("Error. A parameter is only needed.");

    else {

      bck = new BackwardLVO (args[0]);
      bck.ejecutar();

    }

  }

}

