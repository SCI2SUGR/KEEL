package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Fuzzy_Ish_Selec;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Selectos implements Comparable{
  double probabilidad;
  int posicion;

  public Selectos(double prob, int pos) {
    this.probabilidad = prob;
    this.posicion = pos;
  }

  public int compareTo(Object a) {
    if ( ( (Selectos) a).probabilidad < this.probabilidad) {
      return -1;
    }
    if ( ( (Selectos) a).probabilidad > this.probabilidad) {
      return 1;
    }
    return 0;
  }

}
