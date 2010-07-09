package keel.Algorithms.Preprocess.Feature_Selection.evolutionary_algorithms.CHC.wrapper;

public class Main {


  public static void main (String args[]) {

    CHCBinaryLVO chc;

    if (args.length != 1)

      System.err.println("Error. A parameter is only needed.");

    else {

      chc = new CHCBinaryLVO (args[0]);
      chc.ejecutar();

    }

  }

}

