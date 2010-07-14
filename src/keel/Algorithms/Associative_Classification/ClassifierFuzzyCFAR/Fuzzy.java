package keel.Algorithms.Associative_Classification.ClassifierFuzzyCFAR;

/**
 * <p>Title: Fuzzy</p>
 *
 * <p>Description: This class contains the representation of a fuzzy value</p>
 *
 * <p>Copyright: Copyright KEEL (c) 2007</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Jesus Alcalá (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */

public class Fuzzy {
  double x0, x1, x3, y;
  String name;

  public Fuzzy() {
  }

  /**
   * If fuzzyfies a crisp value
   * @param X double The crips value
   * @return double the degree of membership
   */
  public double Fuzzifica(double X) {
    if (X == x1) { 
      return (1.0); 
    }

	if ( (X <= x0) || (X >= x3)) {
      return (0.0); 
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
   * <p>
   * Clone Function
   * </p>
   */
  public Fuzzy clone(){
    Fuzzy d = new Fuzzy();
    d.x0 = this.x0;
    d.x1 = this.x1;
    d.x3 = this.x3;
    d.y = this.y;
    d.name = this.name;
    return d;
  }

  /**
   * <p>
   * It returns the name of the fuzzy set
   * </p>
   * @return String The name of the fuzzy set
   */
  public String getName(){
	  return (this.name);
  }
}
