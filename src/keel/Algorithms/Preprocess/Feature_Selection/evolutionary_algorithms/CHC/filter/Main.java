package keel.Algorithms.Preprocess.Feature_Selection.evolutionary_algorithms.CHC.filter;

public class Main {


  public static void main (String args[]) {

    CHCBinaryIncon chc;

    if (args.length != 1)

      System.err.println("Error. A parameter is only needed.");

    else {

      chc = new CHCBinaryIncon (args[0]);
      chc.ejecutar();

    }

  }

}

