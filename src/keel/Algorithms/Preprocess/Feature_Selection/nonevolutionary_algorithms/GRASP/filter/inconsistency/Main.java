package keel.Algorithms.Preprocess.Feature_Selection.nonevolutionary_algorithms.GRASP.filter.inconsistency;

public class Main {


  public static void main (String args[]) {

    GraspIncon grasp;

    if (args.length != 1)

      System.err.println("Error. A parameter is only needed.");

    else {

      grasp = new GraspIncon (args[0]);
      grasp.ejecutar();

    }

  }

}

