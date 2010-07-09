package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierFuzzySGERD;

/**
 * <p>Title: Fuzzy</p>
 *
 * <p>Description: This class contains the representation of a fuzzy value</p>
 *
 * <p>Copyright: Copyright KEEL (c) 2007</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Alberto Fernández (University of Granada) 16/10/2007
 * @author Modified by Jesus Alcalá (University of Granada) 19/05/2009
 * @version 1.0
 * @since JDK1.5
 */
public class Fuzzy {
  double x0, x1, x3, y;
  String name;
  int label;
  double covering;

  /**
   * Default constructor
   */
  public Fuzzy() {
  }

  /**
   * If fuzzyfies a crisp value
   * @param X double The crips value
   * @return double the degree of membership
   */
  public double Fuzzify(double X) {
    if ( (X <= x0) || (X >= x3)) /* If X is not in the range of D, the */
        {
      return (0.0); /* membership degree is 0 */
    }

    if (X < x1) {
      return ( (X - x0) * (y / (x1 - x0)));
    }

    if (X > x1) {
      return ( (x3 - X) * (y / (x3 - x1)));
    }

    return (y);

  }

  /**
   * It makes a copy for the object
   * @return Fuzzy a copy for the object
   */
  public Fuzzy clone(){
    Fuzzy d = new Fuzzy();
    d.x0 = this.x0;
    d.x1 = this.x1;
    d.x3 = this.x3;
    d.y = this.y;
    d.name = this.name;
    d.label = this.label;
    return d;
  }
}
