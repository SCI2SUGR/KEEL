package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.GFS_RB_MF;

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
public class Difuso {
  double x0, x1, x2, x3, y;
  String nombre;
  int etiqueta;

  public Difuso() {
  }

  public double Fuzzifica(double X) {
    if ( (X < x0) || (X > x3)) {
      return (0);
    }
    if (X < x1) {
      return ( (X - x0) * (y / (x1 - x0)));
    }
    if (X > x2) {
      return ( (x3 - X) * (y / (x3 - x2)));
    }

    return (y);

  }

  public Difuso clone() {
    Difuso d = new Difuso();
    d.x0 = this.x0;
    d.x1 = this.x1;
    d.x2 = this.x2;
    d.x3 = this.x3;
    d.y = this.y;
    d.nombre = this.nombre;
    d.etiqueta = this.etiqueta;
    return d;
  }

}
