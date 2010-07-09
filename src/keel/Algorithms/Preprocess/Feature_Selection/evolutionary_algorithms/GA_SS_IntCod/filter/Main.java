package keel.Algorithms.Preprocess.Feature_Selection.evolutionary_algorithms.GA_SS_IntCod.filter;

public class Main {


  public static void main (String args[]) {

    SSGAIntegerIncon agee;

    if (args.length != 1)

      System.err.println("Error. A parameter is only needed.");

    else {

      agee = new SSGAIntegerIncon (args[0]);
      agee.ejecutar();

    }

  }

}

