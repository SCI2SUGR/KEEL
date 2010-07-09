package keel.Algorithms.Preprocess.Feature_Selection.evolutionary_algorithms.GA_Gen_IntCod.filter;

public class Main {


  public static void main (String args[]) {

    GGAIntegerIncon agge;

    if (args.length != 1)

      System.err.println("Error. A parameter is only needed.");

    else {

      agge = new GGAIntegerIncon (args[0]);
      agge.ejecutar();

    }

  }

}

