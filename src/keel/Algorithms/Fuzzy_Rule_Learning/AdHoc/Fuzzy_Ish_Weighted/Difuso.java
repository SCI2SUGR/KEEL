package keel.Algorithms.Fuzzy_Rule_Learning.AdHoc.Fuzzy_Ish_Weighted;

/**
 * <p>Title: Difuso</p>
 *
 * <p>Description: Contains the definition of a fuzzy value</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: KEEL</p>
 *
 * @author A. Fernández
 * @version 1.0
 */
public class Difuso {
  double x0, x1, x3, y;
  String name;
  int label;

  public Difuso() {
  }

  public double Fuzzifica(double X) {
    if ( (X <= x0) || (X >= x3)) /* Si X no esta en el rango de D, el */
        {
      return (0.0); /* grado de pertenencia es 0 */
    }

    if (X < x1) {
      return ( (X - x0) * (y / (x1 - x0)));
    }

    if (X > x1) {
      return ( (x3 - X) * (y / (x3 - x1)));
    }

    return (y);

  }

  public Difuso clone(){
    Difuso d = new Difuso();
    d.x0 = this.x0;
    d.x1 = this.x1;
    d.x3 = this.x3;
    d.y = this.y;
    d.name = this.name;
    d.label = this.label;
    return d;
  }

}
