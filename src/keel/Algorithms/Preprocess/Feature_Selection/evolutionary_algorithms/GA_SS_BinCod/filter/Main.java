package keel.Algorithms.Preprocess.Feature_Selection.evolutionary_algorithms.GA_SS_BinCod.filter;

public class Main {


  public static void main (String args[]) {

    SSGABinaryIncon ageb;

    if (args.length != 1)

      System.err.println("Error. A parameter is only needed.");

    else {

      ageb = new SSGABinaryIncon (args[0]);
      ageb.ejecutar();

    }

  }

}

