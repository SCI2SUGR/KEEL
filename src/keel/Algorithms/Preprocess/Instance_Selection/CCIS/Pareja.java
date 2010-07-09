

package keel.Algorithms.Preprocess.Instance_Selection.CCIS;

public class Pareja implements Comparable {



  public int entero;

  public double real;



  public Pareja () {}



  public Pareja (int a, double b) {

    entero = a;

    real = b;

  }



  public int compareTo (Object o1) {

    if (this.real > ((Pareja)o1).real)

      return -1;

    else if (this.real < ((Pareja)o1).real)

      return 1;

    else return 0;

  }



  public String toString () {

    return new String ("{"+entero+", "+real+"}");

  }

}

