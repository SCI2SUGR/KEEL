package keel.Algorithms.Preprocess.Feature_Selection.evolutionary_algorithms.GA_Gen_IntCod.wrapper;

public class Main {


  public static void main (String args[]) {

    GGAIntegerLVO agge;

    if (args.length != 1)

      System.err.println("Error. A parameter is only needed.");

    else {

      agge = new GGAIntegerLVO (args[0]);
      agge.ejecutar();

    }

  }

}

