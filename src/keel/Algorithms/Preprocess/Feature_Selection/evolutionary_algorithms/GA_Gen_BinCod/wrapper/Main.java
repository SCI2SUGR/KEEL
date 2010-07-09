package keel.Algorithms.Preprocess.Feature_Selection.evolutionary_algorithms.GA_Gen_BinCod.wrapper;

public class Main {


  public static void main (String args[]) {

    GGABinaryLVO aggb;

    if (args.length != 1)

      System.err.println("Error. A parameter is only needed.");

    else {

      aggb = new GGABinaryLVO (args[0]);
      aggb.ejecutar();

    }

  }

}

