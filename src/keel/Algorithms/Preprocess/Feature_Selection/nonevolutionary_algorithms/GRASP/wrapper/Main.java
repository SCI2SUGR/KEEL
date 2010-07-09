package keel.Algorithms.Preprocess.Feature_Selection.nonevolutionary_algorithms.GRASP.wrapper;

public class Main {


  public static void main (String args[]) {

    GraspLVO grasp;

    if (args.length != 1)

      System.err.println("Error. A parameter is only needed.");

    else {

      grasp = new GraspLVO (args[0]);
      grasp.ejecutar();

    }

  }

}

