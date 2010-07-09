package keel.Algorithms.Preprocess.Feature_Selection.evolutionary_algorithms.GA_SS_BinCod.wrapper;

public class Main {


  public static void main (String args[]) {

    SSGABinaryLVO ageb;

    if (args.length != 1)

      System.err.println("Error. A parameter is only needed.");

    else {

      ageb = new SSGABinaryLVO (args[0]);
      ageb.ejecutar();

    }

  }

}

